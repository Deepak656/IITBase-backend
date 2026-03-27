package com.iitbase.admin.staff;

import com.iitbase.admin.staff.dto.AcceptStaffInviteRequest;
import com.iitbase.admin.staff.dto.StaffInviteRequest;
import com.iitbase.admin.staff.dto.StaffInviteResponse;
import com.iitbase.auth.TokenService;
import com.iitbase.auth.dto.AuthResponse;
import com.iitbase.config.JwtUtil;
import com.iitbase.email.event.StaffInviteAcceptedEvent;
import com.iitbase.email.event.StaffInviteEvent;
import com.iitbase.user.User;
import com.iitbase.user.UserRepository;
import com.iitbase.user.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class StaffInviteService {

    private final StaffInviteRepository inviteRepository;
    private final UserRepository        userRepository;
    private final PasswordEncoder       passwordEncoder;
    private final TokenService          tokenService;
    private final JwtUtil               jwtUtil;
    private final ApplicationEventPublisher eventPublisher;

    // ── Send invite ───────────────────────────────────────────────────────────

    public StaffInviteResponse sendInvite(String senderEmail,
                                          Long senderUserId,
                                          StaffInviteRequest request) {
        String targetEmail = request.getEmail().toLowerCase().trim();

        // Reject if target is already staff
        userRepository.findByEmail(targetEmail).ifPresent(user -> {
            if (user.getRole() == UserRole.ADMIN) {
                throw new IllegalStateException(
                        targetEmail + " is already a staff member");
            }
        });

        // Prevent duplicate pending invites
        if (inviteRepository.existsByEmailAndStatus(targetEmail, StaffInviteStatus.PENDING)) {
            throw new IllegalStateException(
                    "A pending invite already exists for " + targetEmail);
        }

        StaffInvite invite = StaffInvite.builder()
                .token(UUID.randomUUID().toString())
                .email(targetEmail)
                .invitedByUserId(senderUserId)
                .invitedByEmail(senderEmail)
                .status(StaffInviteStatus.PENDING)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();

        StaffInvite saved = inviteRepository.save(invite);
        eventPublisher.publishEvent(new StaffInviteEvent(
                this,
                saved.getEmail(),
                senderEmail,
                saved.getToken()
        ));
        // Done: wire email service here
        log.info("Staff invite sent to={} by={}", targetEmail, senderEmail);
        return StaffInviteResponse.from(saved);
    }

    // ── Accept invite ─────────────────────────────────────────────────────────

    public AuthResponse acceptInvite(AcceptStaffInviteRequest request) {
        StaffInvite invite = inviteRepository.findByToken(request.getToken())
                .orElseThrow(() -> new IllegalArgumentException("Invalid invite link"));

        // Check expiry first
        if (invite.getExpiresAt().isBefore(LocalDateTime.now())) {
            invite.setStatus(StaffInviteStatus.EXPIRED);
            inviteRepository.save(invite);
            throw new IllegalStateException(
                    "This invite has expired. Ask your admin to send a new one.");
        }

        switch (invite.getStatus()) {
            case REVOKED  -> throw new IllegalStateException("This invite has been revoked.");
            case ACCEPTED -> throw new IllegalStateException("This invite has already been used.");
            case EXPIRED  -> throw new IllegalStateException("This invite has expired.");
            default       -> { /* PENDING — proceed */ }
        }

        // Find existing account or create new one
        User user = userRepository.findByEmail(invite.getEmail())
                .orElseGet(() -> {
                    if (request.getPassword() == null
                            || request.getPassword().length() < 8) {
                        throw new IllegalArgumentException(
                                "Password must be at least 8 characters");
                    }
                    return userRepository.save(
                            User.builder()
                                    .email(invite.getEmail())
                                    .password(passwordEncoder.encode(request.getPassword()))
                                    .role(UserRole.ADMIN)
                                    .build()
                    );
                });

        // Promote to ADMIN regardless of current role
        user.setRole(UserRole.ADMIN);
        userRepository.save(user);

        // Mark invite consumed
        invite.setStatus(StaffInviteStatus.ACCEPTED);
        invite.setAcceptedAt(LocalDateTime.now());
        invite.setAcceptedByUserId(user.getId());
        inviteRepository.save(invite);

        // Issue JWT so the frontend logs them in immediately
        String[] tokenData = jwtUtil.generateTokenWithJti(
                user.getEmail(), user.getRole().name());
        tokenService.storeToken(tokenData[1], user.getEmail(), user.getRole().name());
        // Notify the admin who sent the invite
        eventPublisher.publishEvent(new StaffInviteAcceptedEvent(
                this,
                invite.getInvitedByEmail(),   // who sent it
                user.getEmail()               // who accepted
        ));
        log.info("Staff invite accepted by={} userId={}", user.getEmail(), user.getId());
        return new AuthResponse(tokenData[0], user.getRole().name());
    }

    // ── Revoke invite ─────────────────────────────────────────────────────────

    public StaffInviteResponse revokeInvite(Long inviteId) {
        StaffInvite invite = inviteRepository.findById(inviteId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invite not found: " + inviteId));

        if (invite.getStatus() != StaffInviteStatus.PENDING) {
            throw new IllegalStateException("Only pending invites can be revoked");
        }

        invite.setStatus(StaffInviteStatus.REVOKED);
        log.info("Staff invite revoked: id={} email={}", inviteId, invite.getEmail());
        return StaffInviteResponse.from(inviteRepository.save(invite));
    }

    // ── List invites ──────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<StaffInviteResponse> listInvites(int page, int size) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "createdAt"));
        return inviteRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(StaffInviteResponse::from);
    }

    // ── Validate token ────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public StaffInviteResponse validateToken(String token) {
        StaffInvite invite = inviteRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid invite link"));

        if (invite.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("This invite has expired.");
        }
        if (invite.getStatus() != StaffInviteStatus.PENDING) {
            throw new IllegalStateException(
                    "This invite is no longer valid (" + invite.getStatus() + ")");
        }
        return StaffInviteResponse.from(invite);
    }
}
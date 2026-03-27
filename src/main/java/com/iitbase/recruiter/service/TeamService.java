package com.iitbase.recruiter.service;

import com.iitbase.email.event.*;
import com.iitbase.recruiter.dto.request.*;
import com.iitbase.recruiter.dto.response.*;
import com.iitbase.recruiter.entity.*;
import com.iitbase.recruiter.enums.*;
import com.iitbase.recruiter.exception.*;
import com.iitbase.recruiter.repository.*;
import com.iitbase.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TeamService {

    private final RecruiterRepository       recruiterRepository;
    private final CompanyRepository         companyRepository;
    private final TeamJoinRequestRepository joinRequestRepository;
    private final RecruiterInviteRepository inviteRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final UserRepository userRepository;
    // ── Company search (onboarding step) ─────────────────────────────────────

    /**
     * Called during onboarding. Returns companies matching the search query.
     * If the user's work email domain matches a company, that result is
     * flagged with domainMatch=true — auto-join is allowed for those.
     */
    @Transactional(readOnly = true)
    public List<CompanySearchResult> searchCompanies(String query,
                                                     String userEmailDomain,
                                                     int page,
                                                     int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Company> companies = companyRepository
                .findByNameContainingIgnoreCase(query, pageable);

        return companies.getContent().stream().map(company -> {
            boolean domainMatch = userEmailDomain != null
                    && userEmailDomain.equalsIgnoreCase(company.getEmailDomain());

            // Find admin name to show in "contact admin" message
            List<Recruiter> admins = recruiterRepository
                    .findAllByCompanyId(company.getId())
                    .stream()
                    .filter(r -> r.getRole() == TeamMemberRole.ADMIN)
                    .toList();

            String adminName = admins.isEmpty() ? null : admins.get(0).getName();
            String adminDesignation = admins.isEmpty() ? null : admins.get(0).getDesignation();

            return CompanySearchResult.from(company, domainMatch, adminName, adminDesignation);
        }).toList();
    }

    // ── Path A: Domain match — auto join ─────────────────────────────────────

    /**
     * Path A: User's email domain matches the company domain.
     * Auto-creates their recruiter profile without admin approval.
     * Called after the user has already created a company (or selected existing).
     */
    public RecruiterProfileResponse autoJoinByDomain(Long userId,
                                                     Long companyId,
                                                     String name,
                                                     String designation) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException(companyId));

        if (recruiterRepository.existsByUserId(userId)) {
            throw new RecruiterProfileAlreadyExistsException();
        }

        List<Recruiter> existing = recruiterRepository.findAllByCompanyId(companyId);
        TeamMemberRole role = existing.isEmpty()
                ? TeamMemberRole.ADMIN
                : TeamMemberRole.MEMBER;

        Recruiter recruiter = Recruiter.builder()
                .userId(userId)
                .company(company)
                .name(name)
                .designation(designation)
                .role(role)
                .build();

        Recruiter saved = recruiterRepository.save(recruiter);
        log.info("Auto-joined userId={} companyId={} via domain match role={}",
                userId, companyId, role);
        return RecruiterProfileResponse.from(saved);
    }

    // ── Path B: No domain match — join request ───────────────────────────────

    /**
     * Path B: User wants to join an existing company but email domain
     * doesn't match. Creates a join request for the company admin to review.
     */
    public JoinRequestResponse requestToJoin(Long userId, JoinCompanyRequest request) {
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new CompanyNotFoundException(request.getCompanyId()));

        if (recruiterRepository.existsByUserId(userId)) {
            throw new RecruiterProfileAlreadyExistsException();
        }

        // Prevent duplicate pending requests
        if (joinRequestRepository.existsByUserIdAndCompanyIdAndStatus(
                userId, request.getCompanyId(), JoinRequestStatus.PENDING)) {
            throw new IllegalStateException(
                    "You already have a pending request to join this company");
        }

        TeamJoinRequest joinRequest = TeamJoinRequest.builder()
                .userId(userId)
                .company(company)
                .message(request.getMessage())
                .workEmail(request.getWorkEmail())
                .status(JoinRequestStatus.PENDING)
                .build();

        TeamJoinRequest saved = joinRequestRepository.save(joinRequest);
        // Notify all admins of the company
        recruiterRepository.findAllByCompanyId(company.getId())
                .stream()
                .filter(r -> r.getRole() == TeamMemberRole.ADMIN)
                .forEach(adminRecruiter ->
                        userRepository.findById(adminRecruiter.getUserId()).ifPresent(adminUser ->
                                eventPublisher.publishEvent(new JoinRequestReceivedEvent(
                                        this,
                                        adminUser.getEmail(),
                                        adminRecruiter.getName(),
                                        request.getWorkEmail() != null
                                                ? request.getWorkEmail()
                                                : "User #" + userId,   // fallback if no work email
                                        company.getName(),
                                        request.getMessage()
                                ))
                        )
                );
        log.info("Join request created userId={} companyId={}", userId, company.getId());

        // TODO: send email notification to company admins here
        // emailService.notifyAdminsOfJoinRequest(company, saved);

        return JoinRequestResponse.from(saved);
    }

    /**
     * Company admin approves a join request.
     * Creates the recruiter profile for the requester.
     */
    public JoinRequestResponse approveJoinRequest(Long adminUserId, Long requestId,
                                                  String name, String designation) {
        Recruiter admin = getAdminRecruiter(adminUserId);
        TeamJoinRequest joinRequest = joinRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Join request not found: " + requestId));

        if (!joinRequest.getCompany().getId().equals(admin.getCompany().getId())) {
            throw new UnauthorizedActionException(
                    "You can only review requests for your own company");
        }

        if (joinRequest.getStatus() != JoinRequestStatus.PENDING) {
            throw new IllegalStateException("This request has already been reviewed");
        }

        // Create the recruiter profile
        Recruiter newMember = Recruiter.builder()
                .userId(joinRequest.getUserId())
                .company(joinRequest.getCompany())
                .name(name != null ? name : "Team Member")
                .designation(designation != null ? designation : "Recruiter")
                .role(TeamMemberRole.MEMBER)
                .build();

        recruiterRepository.save(newMember);

        joinRequest.setStatus(JoinRequestStatus.APPROVED);
        joinRequest.setReviewedByRecruiterId(admin.getId());
        joinRequest.setReviewedAt(LocalDateTime.now());
        // Notify the requester — need their email from UserRepository
        userRepository.findById(joinRequest.getUserId()).ifPresent(requesterUser ->
                eventPublisher.publishEvent(new JoinRequestApprovedEvent(
                        this,
                        requesterUser.getEmail(),
                        joinRequest.getCompany().getName(),
                        admin.getName()
                ))
        );

        log.info("Join request approved requestId={} by adminUserId={}",
                requestId, adminUserId);

        // Done: notify requester by email
        return JoinRequestResponse.from(joinRequestRepository.save(joinRequest));
    }

    /**
     * Company admin rejects a join request.
     */
    public JoinRequestResponse rejectJoinRequest(Long adminUserId, Long requestId,
                                                 String rejectionReason) {
        Recruiter admin = getAdminRecruiter(adminUserId);
        TeamJoinRequest joinRequest = joinRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Join request not found: " + requestId));

        if (!joinRequest.getCompany().getId().equals(admin.getCompany().getId())) {
            throw new UnauthorizedActionException(
                    "You can only review requests for your own company");
        }

        if (joinRequest.getStatus() != JoinRequestStatus.PENDING) {
            throw new IllegalStateException("This request has already been reviewed");
        }

        joinRequest.setStatus(JoinRequestStatus.REJECTED);
        joinRequest.setReviewedByRecruiterId(admin.getId());
        joinRequest.setReviewedAt(LocalDateTime.now());
        joinRequest.setRejectionReason(rejectionReason);
        // Notify the requester — need their email from UserRepository
        userRepository.findById(joinRequest.getUserId()).ifPresent(requesterUser ->
                eventPublisher.publishEvent(new JoinRequestRejectedEvent(
                        this,
                        requesterUser.getEmail(),
                        joinRequest.getCompany().getName(),
                        rejectionReason
                ))
        );
        log.info("Join request rejected requestId={} by adminUserId={}",
                requestId, adminUserId);

        return JoinRequestResponse.from(joinRequestRepository.save(joinRequest));
    }

    // ── Invite flow ───────────────────────────────────────────────────────────

    /**
     * Admin sends an invite link to a new team member by email.
     */
    public RecruiterInviteResponse inviteTeamMember(Long adminUserId,
                                                    InviteTeamMemberRequest request) {
        Recruiter admin = getAdminRecruiter(adminUserId);

        // No duplicate pending invites
        if (inviteRepository.existsByEmailAndCompanyIdAndStatus(
                request.getEmail(), admin.getCompany().getId(), InviteStatus.PENDING)) {
            throw new IllegalStateException(
                    "A pending invite already exists for " + request.getEmail());
        }

        // Don't invite someone already on the team
        // (check via email — we don't store email on Recruiter directly,
        //  so we check the invite history only for now)

        String token = UUID.randomUUID().toString();

        RecruiterInvite invite = RecruiterInvite.builder()
                .token(token)
                .email(request.getEmail())
                .company(admin.getCompany())
                .invitedByRecruiterId(admin.getId())
                .intendedRole(request.getRole() != null
                        ? request.getRole()
                        : TeamMemberRole.MEMBER)
                .status(InviteStatus.PENDING)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();

        RecruiterInvite saved = inviteRepository.save(invite);
        eventPublisher.publishEvent(new RecruiterTeamInviteEvent(
                this,
                saved.getEmail(),
                admin.getCompany().getName(),
                admin.getName(),
                saved.getToken()
        ));
        log.info("Invite sent to={} by adminUserId={} companyId={}",
                request.getEmail(), adminUserId, admin.getCompany().getId());

        //Done : send invite email with link /recruiter/invite?token={token}
        return RecruiterInviteResponse.from(saved);
    }

    /**
     * Recipient accepts an invite. Creates their recruiter profile.
     * Called after the recipient has signed up / logged in.
     */
    public RecruiterProfileResponse acceptInvite(Long userId, AcceptInviteRequest request) {
        RecruiterInvite invite = inviteRepository.findByToken(request.getToken())
                .orElseThrow(() -> new IllegalArgumentException("Invalid invite token"));

        if (invite.getStatus() != InviteStatus.PENDING) {
            throw new IllegalStateException("This invite has already been used or revoked");
        }

        if (invite.getExpiresAt().isBefore(LocalDateTime.now())) {
            invite.setStatus(InviteStatus.EXPIRED);
            inviteRepository.save(invite);
            throw new IllegalStateException(
                    "This invite has expired. Ask your admin to send a new one.");
        }

        if (recruiterRepository.existsByUserId(userId)) {
            throw new RecruiterProfileAlreadyExistsException();
        }

        Recruiter newMember = Recruiter.builder()
                .userId(userId)
                .company(invite.getCompany())
                .name(request.getName())
                .designation(request.getDesignation())
                .role(invite.getIntendedRole())
                .build();

        Recruiter saved = recruiterRepository.save(newMember);

        invite.setStatus(InviteStatus.ACCEPTED);
        invite.setAcceptedAt(LocalDateTime.now());
        invite.setAcceptedByUserId(userId);
        inviteRepository.save(invite);

        // Email Notify the admin who sent the invite
        recruiterRepository.findById(invite.getInvitedByRecruiterId()).ifPresent(adminRecruiter ->
                userRepository.findById(adminRecruiter.getUserId()).ifPresent(adminUser ->
                        eventPublisher.publishEvent(new RecruiterInviteAcceptedEvent(
                                this,
                                adminUser.getEmail(),
                                adminRecruiter.getName(),
                                saved.getName(),               // new member's name
                                invite.getCompany().getName()
                        ))
                )
        );
        log.info("Invite accepted userId={} companyId={} role={}",
                userId, invite.getCompany().getId(), invite.getIntendedRole());

        return RecruiterProfileResponse.from(saved);
    }

    /**
     * Admin revokes a pending invite.
     */
    public void revokeInvite(Long adminUserId, Long inviteId) {
        Recruiter admin = getAdminRecruiter(adminUserId);
        RecruiterInvite invite = inviteRepository.findById(inviteId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invite not found: " + inviteId));

        if (!invite.getCompany().getId().equals(admin.getCompany().getId())) {
            throw new UnauthorizedActionException("You can only revoke invites for your company");
        }

        if (invite.getStatus() != InviteStatus.PENDING) {
            throw new IllegalStateException("Only pending invites can be revoked");
        }

        invite.setStatus(InviteStatus.REVOKED);
        inviteRepository.save(invite);
        log.info("Invite revoked inviteId={} by adminUserId={}", inviteId, adminUserId);
    }

    // ── Team management ───────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<TeamMemberResponse> getTeamMembers(Long userId) {
        Recruiter recruiter = recruiterRepository.findByUserId(userId)
                .orElseThrow(() -> new RecruiterNotFoundException(userId));

        return recruiterRepository
                .findAllByCompanyId(recruiter.getCompany().getId())
                .stream()
                .map(TeamMemberResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<RecruiterInviteResponse> getPendingInvites(Long adminUserId,
                                                           int page, int size) {
        Recruiter admin = getAdminRecruiter(adminUserId);
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        return inviteRepository
                .findByCompanyIdAndStatus(
                        admin.getCompany().getId(), InviteStatus.PENDING, pageable)
                .map(RecruiterInviteResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<JoinRequestResponse> getPendingJoinRequests(Long adminUserId,
                                                            int page, int size) {
        Recruiter admin = getAdminRecruiter(adminUserId);
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        return joinRequestRepository
                .findByCompanyIdAndStatus(
                        admin.getCompany().getId(), JoinRequestStatus.PENDING, pageable)
                .map(JoinRequestResponse::from);
    }

    /**
     * Admin promotes a member to ADMIN or demotes an ADMIN to MEMBER.
     * At least one ADMIN must remain — you can't demote the last admin.
     */
    public TeamMemberResponse updateMemberRole(Long adminUserId,
                                               Long targetRecruiterId,
                                               TeamMemberRole newRole) {
        Recruiter admin = getAdminRecruiter(adminUserId);
        Recruiter target = recruiterRepository.findById(targetRecruiterId)
                .orElseThrow(() -> new RecruiterNotFoundException(targetRecruiterId));

        if (!target.getCompany().getId().equals(admin.getCompany().getId())) {
            throw new UnauthorizedActionException(
                    "You can only manage members of your own company");
        }

        if (admin.getId().equals(target.getId())) {
            throw new IllegalStateException(
                    "You cannot change your own role. Ask another admin.");
        }

        // Guard: don't allow last admin demotion
        if (newRole == TeamMemberRole.MEMBER
                && target.getRole() == TeamMemberRole.ADMIN) {
            long adminCount = recruiterRepository
                    .findAllByCompanyId(admin.getCompany().getId())
                    .stream()
                    .filter(r -> r.getRole() == TeamMemberRole.ADMIN)
                    .count();

            if (adminCount <= 1) {
                throw new IllegalStateException(
                        "Cannot demote the only admin. Promote another member first.");
            }
        }

        target.setRole(newRole);
        Recruiter saved = recruiterRepository.save(target);

        log.info("Member role updated recruiterId={} newRole={} by adminUserId={}",
                targetRecruiterId, newRole, adminUserId);

        return TeamMemberResponse.from(saved);
    }

    /**
     * Admin removes a team member. Cannot remove yourself.
     */
    public void removeTeamMember(Long adminUserId, Long targetRecruiterId) {
        Recruiter admin = getAdminRecruiter(adminUserId);
        Recruiter target = recruiterRepository.findById(targetRecruiterId)
                .orElseThrow(() -> new RecruiterNotFoundException(targetRecruiterId));

        if (!target.getCompany().getId().equals(admin.getCompany().getId())) {
            throw new UnauthorizedActionException(
                    "You can only remove members of your own company");
        }

        if (admin.getId().equals(target.getId())) {
            throw new IllegalStateException("You cannot remove yourself from the team");
        }

        recruiterRepository.delete(target);
        log.info("Team member removed recruiterId={} by adminUserId={}",
                targetRecruiterId, adminUserId);
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private Recruiter getAdminRecruiter(Long userId) {
        Recruiter recruiter = recruiterRepository.findByUserId(userId)
                .orElseThrow(() -> new RecruiterNotFoundException(userId));

        if (recruiter.getRole() != TeamMemberRole.ADMIN) {
            throw new UnauthorizedActionException(
                    "Only company admins can perform this action");
        }

        return recruiter;
    }
}
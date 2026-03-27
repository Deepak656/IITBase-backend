package com.iitbase.jobseeker.service;

import com.iitbase.exception.ResourceNotFoundException;
import com.iitbase.jobseeker.dto.JobPreferenceDTO;
import com.iitbase.jobseeker.model.JobPreference;
import com.iitbase.jobseeker.model.Jobseeker;
import com.iitbase.jobseeker.repository.JobPreferenceRepository;
import com.iitbase.jobseeker.repository.JobseekerRepository;
import com.iitbase.user.User;
import com.iitbase.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobPreferenceService {

    private final JobPreferenceRepository jobPreferenceRepository;
    private final JobseekerRepository jobseekerRepository;
    private final UserService userService;

    // ─────────────────────────────────────────────
    // Called by JobPreferenceController (dedicated endpoint)
    // ─────────────────────────────────────────────

    @Cacheable(value = "jobPreferences", key = "#email")
    @Transactional(readOnly = true)
    public JobPreferenceDTO getJobPreference(String email) {
        Jobseeker jobseeker = resolveJobseeker(email);
        return jobPreferenceRepository
                .findByJobseekerId(jobseeker.getId())
                .map(this::mapToDTO)
                .orElse(null);
    }

    @CacheEvict(value = "jobPreferences", key = "#email")
    @Transactional
    public JobPreferenceDTO saveOrUpdateJobPreference(String email, JobPreferenceDTO dto) {
        Jobseeker jobseeker = resolveJobseeker(email);

        JobPreference preference = jobPreferenceRepository
                .findByJobseekerId(jobseeker.getId())
                .orElse(JobPreference.builder()
                        .jobseekerId(jobseeker.getId())
                        .build());

        updateEntityFromDTO(preference, dto);

        JobPreference saved = jobPreferenceRepository.save(preference);
        log.info("Job preferences saved for jobseeker: {}", jobseeker.getId());
        return mapToDTO(saved);
    }

    @CacheEvict(value = "jobPreferences", key = "#email")
    @Transactional
    public void deleteJobPreference(String email) {
        Jobseeker jobseeker = resolveJobseeker(email);
        jobPreferenceRepository.deleteByJobseekerId(jobseeker.getId());
        log.info("Job preferences deleted for jobseeker: {}", jobseeker.getId());
    }

    // ─────────────────────────────────────────────
    // Called internally by JobseekerProfileService.getFullProfile()
    // Bypasses cache key mismatch — works directly on jobseekerId
    // ─────────────────────────────────────────────

    @Transactional(readOnly = true)
    public JobPreferenceDTO getByJobseekerId(Long jobseekerId) {
        return jobPreferenceRepository
                .findByJobseekerId(jobseekerId)
                .map(this::mapToDTO)
                .orElse(null);
    }

    // ─────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────

    private Jobseeker resolveJobseeker(String email) {
        User user = userService.findByEmail(email);
        return jobseekerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Jobseeker profile not found"));
    }

    public JobPreferenceDTO mapToDTO(JobPreference entity) {
        return JobPreferenceDTO.builder()
                .id(entity.getId())
                .currentLocation(entity.getCurrentLocation())
                .workLocationType(entity.getWorkLocationType())
                .preferredCities(entity.getPreferredCities() != null
                        ? Arrays.asList(entity.getPreferredCities()) : null)
                .previousSalary(entity.getPreviousSalary())
                .previousSalaryCurrency(entity.getPreviousSalaryCurrency())
                .expectedSalary(entity.getExpectedSalary())
                .expectedSalaryCurrency(entity.getExpectedSalaryCurrency())
                .noticePeriod(entity.getNoticePeriod())
                .primaryRole(entity.getPrimaryRole())
                .openToRoles(entity.getOpenToRoles() != null
                        ? Arrays.asList(entity.getOpenToRoles()) : null)
                .build();
    }

    private void updateEntityFromDTO(JobPreference entity, JobPreferenceDTO dto) {
        entity.setCurrentLocation(dto.getCurrentLocation());
        entity.setWorkLocationType(dto.getWorkLocationType());
        entity.setPreferredCities(dto.getPreferredCities() != null && !dto.getPreferredCities().isEmpty()
                ? dto.getPreferredCities().toArray(new String[0]) : null);
        entity.setPreviousSalary(dto.getPreviousSalary());
        entity.setPreviousSalaryCurrency(dto.getPreviousSalaryCurrency());
        entity.setExpectedSalary(dto.getExpectedSalary());
        entity.setExpectedSalaryCurrency(dto.getExpectedSalaryCurrency());
        entity.setNoticePeriod(dto.getNoticePeriod());
        entity.setPrimaryRole(dto.getPrimaryRole());
        entity.setOpenToRoles(dto.getOpenToRoles() != null && !dto.getOpenToRoles().isEmpty()
                ? dto.getOpenToRoles().toArray(new String[0]) : null);
    }
}
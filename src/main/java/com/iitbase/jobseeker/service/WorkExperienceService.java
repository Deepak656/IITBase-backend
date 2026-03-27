package com.iitbase.jobseeker.service;

import com.iitbase.exception.ResourceNotFoundException;
import com.iitbase.jobseeker.dto.WorkExperienceDTO;
import com.iitbase.jobseeker.model.Jobseeker;
import com.iitbase.jobseeker.model.WorkExperience;
import com.iitbase.jobseeker.repository.WorkExperienceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkExperienceService {

    private final WorkExperienceRepository workExperienceRepository;
    private final JobseekerProfileService profileService;

    @Transactional(readOnly = true)
    public List<WorkExperienceDTO> getAll(String email) {
        Jobseeker jobseeker = profileService.resolveJobseeker(email);
        return workExperienceRepository
                .findByJobseekerIdOrderByDisplayOrderAscStartYearDesc(jobseeker.getId())
                .stream().map(this::toDTO).toList();
    }

    @Transactional
    public WorkExperienceDTO add(String email, WorkExperienceDTO dto) {
        Jobseeker jobseeker = profileService.resolveJobseeker(email);
        validateDates(dto);

        WorkExperience entity = WorkExperience.builder()
                .jobseekerId(jobseeker.getId())
                .company(dto.getCompany())
                .title(dto.getTitle())
                .location(dto.getLocation())
                .employmentType(dto.getEmploymentType())
                .startMonth(dto.getStartMonth())
                .startYear(dto.getStartYear())
                .endMonth(dto.getEndMonth())
                .endYear(dto.getEndYear())
                .isCurrent(Boolean.TRUE.equals(dto.getIsCurrent()))
                .description(dto.getDescription())
                .skillsUsed(dto.getSkillsUsed())
                .displayOrder(dto.getDisplayOrder())
                .build();

        WorkExperience saved = workExperienceRepository.save(entity);
        profileService.recalculateCompletion(jobseeker);

        log.info("Work experience added for jobseeker: {}", jobseeker.getId());
        return toDTO(saved);
    }

    @Transactional
    public WorkExperienceDTO update(String email, Long id, WorkExperienceDTO dto) {
        Jobseeker jobseeker = profileService.resolveJobseeker(email);
        validateOwnership(id, jobseeker.getId());
        validateDates(dto);

        WorkExperience entity = workExperienceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Work experience not found"));

        entity.setCompany(dto.getCompany());
        entity.setTitle(dto.getTitle());
        entity.setLocation(dto.getLocation());
        entity.setEmploymentType(dto.getEmploymentType());
        entity.setStartMonth(dto.getStartMonth());
        entity.setStartYear(dto.getStartYear());
        entity.setEndMonth(dto.getEndMonth());
        entity.setEndYear(dto.getEndYear());
        entity.setIsCurrent(Boolean.TRUE.equals(dto.getIsCurrent()));
        entity.setDescription(dto.getDescription());
        entity.setSkillsUsed(dto.getSkillsUsed());
        entity.setDisplayOrder(dto.getDisplayOrder());

        return toDTO(workExperienceRepository.save(entity));
    }

    @Transactional
    public void delete(String email, Long id) {
        Jobseeker jobseeker = profileService.resolveJobseeker(email);
        validateOwnership(id, jobseeker.getId());
        workExperienceRepository.deleteById(id);
        profileService.recalculateCompletion(jobseeker);
        log.info("Work experience {} deleted for jobseeker: {}", id, jobseeker.getId());
    }

    private void validateDates(WorkExperienceDTO dto) {
        if (Boolean.TRUE.equals(dto.getIsCurrent()) && dto.getEndYear() != null) {
            throw new IllegalArgumentException("Current job cannot have an end date");
        }
        if (!Boolean.TRUE.equals(dto.getIsCurrent()) && dto.getEndYear() == null) {
            throw new IllegalArgumentException("End year is required for past jobs");
        }
    }

    private void validateOwnership(Long recordId, Long jobseekerId) {
        if (!workExperienceRepository.existsByIdAndJobseekerId(recordId, jobseekerId)) {
            throw new ResourceNotFoundException("Work experience not found");
        }
    }

    private WorkExperienceDTO toDTO(WorkExperience e) {
        return WorkExperienceDTO.builder()
                .id(e.getId()).company(e.getCompany()).title(e.getTitle())
                .location(e.getLocation()).employmentType(e.getEmploymentType())
                .startMonth(e.getStartMonth()).startYear(e.getStartYear())
                .endMonth(e.getEndMonth()).endYear(e.getEndYear())
                .isCurrent(e.getIsCurrent()).description(e.getDescription())
                .skillsUsed(e.getSkillsUsed()).displayOrder(e.getDisplayOrder())
                .build();
    }
}
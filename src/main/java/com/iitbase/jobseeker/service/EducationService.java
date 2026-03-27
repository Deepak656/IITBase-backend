package com.iitbase.jobseeker.service;

import com.iitbase.exception.ResourceNotFoundException;
import com.iitbase.jobseeker.dto.EducationDTO;
import com.iitbase.jobseeker.model.Education;
import com.iitbase.jobseeker.model.Jobseeker;
import com.iitbase.jobseeker.repository.EducationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EducationService {

    private final EducationRepository educationRepository;
    private final JobseekerProfileService profileService;

    @Transactional(readOnly = true)
    public List<EducationDTO> getAll(String email) {
        Jobseeker jobseeker = profileService.resolveJobseeker(email);
        return educationRepository
                .findByJobseekerIdOrderByDisplayOrderAscEndYearDesc(jobseeker.getId())
                .stream().map(this::toDTO).toList();
    }

    @Transactional
    public EducationDTO add(String email, EducationDTO dto) {
        Jobseeker jobseeker = profileService.resolveJobseeker(email);

        Education entity = Education.builder()
                .jobseekerId(jobseeker.getId())
                .institution(dto.getInstitution())
                .degree(dto.getDegree())
                .fieldOfStudy(dto.getFieldOfStudy())
                .startYear(dto.getStartYear())
                .endYear(dto.getEndYear())
                .grade(dto.getGrade())
                .gradeType(dto.getGradeType())
                .description(dto.getDescription())
                .displayOrder(dto.getDisplayOrder())
                .build();

        Education saved = educationRepository.save(entity);
        profileService.recalculateCompletion(jobseeker);
        return toDTO(saved);
    }

    @Transactional
    public EducationDTO update(String email, Long id, EducationDTO dto) {
        Jobseeker jobseeker = profileService.resolveJobseeker(email);
        if (!educationRepository.existsByIdAndJobseekerId(id, jobseeker.getId())) {
            throw new ResourceNotFoundException("Education record not found");
        }

        Education entity = educationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Education record not found"));

        entity.setInstitution(dto.getInstitution());
        entity.setDegree(dto.getDegree());
        entity.setFieldOfStudy(dto.getFieldOfStudy());
        entity.setStartYear(dto.getStartYear());
        entity.setEndYear(dto.getEndYear());
        entity.setGrade(dto.getGrade());
        entity.setGradeType(dto.getGradeType());
        entity.setDescription(dto.getDescription());
        entity.setDisplayOrder(dto.getDisplayOrder());

        return toDTO(educationRepository.save(entity));
    }

    @Transactional
    public void delete(String email, Long id) {
        Jobseeker jobseeker = profileService.resolveJobseeker(email);
        if (!educationRepository.existsByIdAndJobseekerId(id, jobseeker.getId())) {
            throw new ResourceNotFoundException("Education record not found");
        }
        educationRepository.deleteById(id);
        profileService.recalculateCompletion(jobseeker);
    }

    private EducationDTO toDTO(Education e) {
        return EducationDTO.builder()
                .id(e.getId()).institution(e.getInstitution()).degree(e.getDegree())
                .fieldOfStudy(e.getFieldOfStudy()).startYear(e.getStartYear())
                .endYear(e.getEndYear()).grade(e.getGrade()).gradeType(e.getGradeType())
                .description(e.getDescription()).displayOrder(e.getDisplayOrder())
                .build();
    }
}
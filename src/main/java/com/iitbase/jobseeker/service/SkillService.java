package com.iitbase.jobseeker.service;

import com.iitbase.exception.ResourceNotFoundException;
import com.iitbase.jobseeker.dto.SkillDTO;
import com.iitbase.jobseeker.model.Jobseeker;
import com.iitbase.jobseeker.model.Skill;
import com.iitbase.jobseeker.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;
    private final JobseekerProfileService profileService;

    @Transactional(readOnly = true)
    public List<SkillDTO> getAll(String email) {
        Jobseeker jobseeker = profileService.resolveJobseeker(email);
        return skillRepository.findByJobseekerIdOrderByDisplayOrderAsc(jobseeker.getId())
                .stream().map(this::toDTO).toList();
    }

    @Transactional
    public SkillDTO add(String email, SkillDTO dto) {
        Jobseeker jobseeker = profileService.resolveJobseeker(email);

        Skill entity = Skill.builder()
                .jobseekerId(jobseeker.getId())
                .name(dto.getName())
                .proficiencyLevel(dto.getProficiencyLevel())
                .yearsOfExperience(dto.getYearsOfExperience())
                .displayOrder(dto.getDisplayOrder())
                .build();

        Skill saved = skillRepository.save(entity);
        profileService.recalculateCompletion(jobseeker);
        return toDTO(saved);
    }

    @Transactional
    public SkillDTO update(String email, Long id, SkillDTO dto) {
        Jobseeker jobseeker = profileService.resolveJobseeker(email);
        if (!skillRepository.existsByIdAndJobseekerId(id, jobseeker.getId())) {
            throw new ResourceNotFoundException("Skill not found");
        }

        Skill entity = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found"));

        entity.setName(dto.getName());
        entity.setProficiencyLevel(dto.getProficiencyLevel());
        entity.setYearsOfExperience(dto.getYearsOfExperience());
        entity.setDisplayOrder(dto.getDisplayOrder());

        return toDTO(skillRepository.save(entity));
    }

    @Transactional
    public void delete(String email, Long id) {
        Jobseeker jobseeker = profileService.resolveJobseeker(email);
        if (!skillRepository.existsByIdAndJobseekerId(id, jobseeker.getId())) {
            throw new ResourceNotFoundException("Skill not found");
        }
        skillRepository.deleteById(id);
        profileService.recalculateCompletion(jobseeker);
    }

    private SkillDTO toDTO(Skill s) {
        return SkillDTO.builder()
                .id(s.getId()).name(s.getName())
                .proficiencyLevel(s.getProficiencyLevel())
                .yearsOfExperience(s.getYearsOfExperience())
                .displayOrder(s.getDisplayOrder())
                .build();
    }
}
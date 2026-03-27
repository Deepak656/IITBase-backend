package com.iitbase.jobseeker.service;

import com.iitbase.exception.ResourceNotFoundException;
import com.iitbase.jobseeker.dto.ProjectDTO;
import com.iitbase.jobseeker.model.Jobseeker;
import com.iitbase.jobseeker.model.Project;
import com.iitbase.jobseeker.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final JobseekerProfileService profileService;

    @Transactional(readOnly = true)
    public List<ProjectDTO> getAll(String email) {
        Jobseeker jobseeker = profileService.resolveJobseeker(email);
        return projectRepository
                .findByJobseekerIdOrderByDisplayOrderAscStartYearDesc(jobseeker.getId())
                .stream().map(this::toDTO).toList();
    }

    @Transactional
    public ProjectDTO add(String email, ProjectDTO dto) {
        Jobseeker jobseeker = profileService.resolveJobseeker(email);

        Project entity = Project.builder()
                .jobseekerId(jobseeker.getId())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .techStack(dto.getTechStack())
                .projectUrl(dto.getProjectUrl())
                .repoUrl(dto.getRepoUrl())
                .startMonth(dto.getStartMonth())
                .startYear(dto.getStartYear())
                .endMonth(dto.getEndMonth())
                .endYear(dto.getEndYear())
                .isOngoing(Boolean.TRUE.equals(dto.getIsOngoing()))
                .displayOrder(dto.getDisplayOrder())
                .build();

        return toDTO(projectRepository.save(entity));
    }

    @Transactional
    public ProjectDTO update(String email, Long id, ProjectDTO dto) {
        Jobseeker jobseeker = profileService.resolveJobseeker(email);
        if (!projectRepository.existsByIdAndJobseekerId(id, jobseeker.getId())) {
            throw new ResourceNotFoundException("Project not found");
        }

        Project entity = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setTechStack(dto.getTechStack());
        entity.setProjectUrl(dto.getProjectUrl());
        entity.setRepoUrl(dto.getRepoUrl());
        entity.setStartMonth(dto.getStartMonth());
        entity.setStartYear(dto.getStartYear());
        entity.setEndMonth(dto.getEndMonth());
        entity.setEndYear(dto.getEndYear());
        entity.setIsOngoing(Boolean.TRUE.equals(dto.getIsOngoing()));
        entity.setDisplayOrder(dto.getDisplayOrder());

        return toDTO(projectRepository.save(entity));
    }

    @Transactional
    public void delete(String email, Long id) {
        Jobseeker jobseeker = profileService.resolveJobseeker(email);
        if (!projectRepository.existsByIdAndJobseekerId(id, jobseeker.getId())) {
            throw new ResourceNotFoundException("Project not found");
        }
        projectRepository.deleteById(id);
    }

    private ProjectDTO toDTO(Project p) {
        return ProjectDTO.builder()
                .id(p.getId()).title(p.getTitle()).description(p.getDescription())
                .techStack(p.getTechStack()).projectUrl(p.getProjectUrl()).repoUrl(p.getRepoUrl())
                .startMonth(p.getStartMonth()).startYear(p.getStartYear())
                .endMonth(p.getEndMonth()).endYear(p.getEndYear())
                .isOngoing(p.getIsOngoing()).displayOrder(p.getDisplayOrder())
                .build();
    }
}
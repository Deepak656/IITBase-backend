package com.iitbase.jobseeker.service;

import com.iitbase.jobseeker.dto.JobPreferenceDTO;
import com.iitbase.exception.ResourceNotFoundException;
import com.iitbase.jobseeker.model.JobPreference;
import com.iitbase.jobseeker.model.Jobseeker;
import com.iitbase.jobseeker.repository.JobPreferenceRepository;
import com.iitbase.jobseeker.repository.JobseekerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobPreferenceService {
    
    private final JobPreferenceRepository jobPreferenceRepository;
    private final JobseekerRepository jobseekerRepository;
    
    @Cacheable(value = "jobPreferences", key = "#userId")
    @Transactional(readOnly = true)
    public JobPreferenceDTO getJobPreference(String userId) {
        log.info("Fetching job preferences for user: {}", userId);
        
        Jobseeker jobseeker = jobseekerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Jobseeker not found"));
        
        JobPreference preference = jobPreferenceRepository.findByJobseekerId(jobseeker.getId())
                .orElse(null);
        
        if (preference == null) {
            return null;
        }
        
        return mapToDTO(preference);
    }
    
    @CacheEvict(value = "jobPreferences", key = "#userId")
    @Transactional
    public JobPreferenceDTO saveOrUpdateJobPreference(String userId, JobPreferenceDTO dto) {
        log.info("Saving/updating job preferences for user: {}", userId);
        
        Jobseeker jobseeker = jobseekerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Jobseeker not found"));
        
        JobPreference preference = jobPreferenceRepository.findByJobseekerId(jobseeker.getId())
                .orElse(JobPreference.builder()
                        .jobseekerId(jobseeker.getId())
                        .build());
        
        updateEntityFromDTO(preference, dto);
        
        JobPreference savedPreference = jobPreferenceRepository.save(preference);
        return mapToDTO(savedPreference);
    }
    
    @CacheEvict(value = "jobPreferences", key = "#userId")
    @Transactional
    public void deleteJobPreference(String userId) {
        log.info("Deleting job preferences for user: {}", userId);
        
        Jobseeker jobseeker = jobseekerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Jobseeker not found"));
        
        jobPreferenceRepository.deleteByJobseekerId(jobseeker.getId());
    }
    
    private JobPreferenceDTO mapToDTO(JobPreference entity) {
        return JobPreferenceDTO.builder()
                .id(entity.getId())
                .currentLocation(entity.getCurrentLocation())
                .workLocationType(entity.getWorkLocationType())
                .preferredCities(entity.getPreferredCities() != null ? 
                        Arrays.asList(entity.getPreferredCities()) : null)
                .previousSalary(entity.getPreviousSalary())
                .previousSalaryCurrency(entity.getPreviousSalaryCurrency())
                .noticePeriod(entity.getNoticePeriod())
                .build();
    }
    
    private void updateEntityFromDTO(JobPreference entity, JobPreferenceDTO dto) {
        entity.setCurrentLocation(dto.getCurrentLocation());
        entity.setWorkLocationType(dto.getWorkLocationType());
        
        if (dto.getPreferredCities() != null && !dto.getPreferredCities().isEmpty()) {
            entity.setPreferredCities(dto.getPreferredCities().toArray(new String[0]));
        } else {
            entity.setPreferredCities(null);
        }
        
        entity.setPreviousSalary(dto.getPreviousSalary());
        entity.setPreviousSalaryCurrency(dto.getPreviousSalaryCurrency());
        entity.setNoticePeriod(dto.getNoticePeriod());
    }
}

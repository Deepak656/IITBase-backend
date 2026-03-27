package com.iitbase.jobseeker.service;

import com.iitbase.exception.ResourceNotFoundException;
import com.iitbase.jobseeker.dto.CertificationDTO;
import com.iitbase.jobseeker.model.Certification;
import com.iitbase.jobseeker.model.Jobseeker;
import com.iitbase.jobseeker.repository.CertificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CertificationService {

    private final CertificationRepository certificationRepository;
    private final JobseekerProfileService profileService;

    @Transactional(readOnly = true)
    public List<CertificationDTO> getAll(String email) {
        Jobseeker jobseeker = profileService.resolveJobseeker(email);
        return certificationRepository
                .findByJobseekerIdOrderByDisplayOrderAscIssueYearDesc(jobseeker.getId())
                .stream().map(this::toDTO).toList();
    }

    @Transactional
    public CertificationDTO add(String email, CertificationDTO dto) {
        Jobseeker jobseeker = profileService.resolveJobseeker(email);

        Certification entity = Certification.builder()
                .jobseekerId(jobseeker.getId())
                .name(dto.getName())
                .issuer(dto.getIssuer())
                .issueMonth(dto.getIssueMonth())
                .issueYear(dto.getIssueYear())
                .expiryMonth(dto.getExpiryMonth())
                .expiryYear(dto.getExpiryYear())
                .doesNotExpire(Boolean.TRUE.equals(dto.getDoesNotExpire()))
                .credentialId(dto.getCredentialId())
                .credentialUrl(dto.getCredentialUrl())
                .displayOrder(dto.getDisplayOrder())
                .build();

        return toDTO(certificationRepository.save(entity));
    }

    @Transactional
    public CertificationDTO update(String email, Long id, CertificationDTO dto) {
        Jobseeker jobseeker = profileService.resolveJobseeker(email);
        if (!certificationRepository.existsByIdAndJobseekerId(id, jobseeker.getId())) {
            throw new ResourceNotFoundException("Certification not found");
        }

        Certification entity = certificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Certification not found"));

        entity.setName(dto.getName());
        entity.setIssuer(dto.getIssuer());
        entity.setIssueMonth(dto.getIssueMonth());
        entity.setIssueYear(dto.getIssueYear());
        entity.setExpiryMonth(dto.getExpiryMonth());
        entity.setExpiryYear(dto.getExpiryYear());
        entity.setDoesNotExpire(Boolean.TRUE.equals(dto.getDoesNotExpire()));
        entity.setCredentialId(dto.getCredentialId());
        entity.setCredentialUrl(dto.getCredentialUrl());
        entity.setDisplayOrder(dto.getDisplayOrder());

        return toDTO(certificationRepository.save(entity));
    }

    @Transactional
    public void delete(String email, Long id) {
        Jobseeker jobseeker = profileService.resolveJobseeker(email);
        if (!certificationRepository.existsByIdAndJobseekerId(id, jobseeker.getId())) {
            throw new ResourceNotFoundException("Certification not found");
        }
        certificationRepository.deleteById(id);
    }

    private CertificationDTO toDTO(Certification c) {
        return CertificationDTO.builder()
                .id(c.getId()).name(c.getName()).issuer(c.getIssuer())
                .issueMonth(c.getIssueMonth()).issueYear(c.getIssueYear())
                .expiryMonth(c.getExpiryMonth()).expiryYear(c.getExpiryYear())
                .doesNotExpire(c.getDoesNotExpire()).credentialId(c.getCredentialId())
                .credentialUrl(c.getCredentialUrl()).displayOrder(c.getDisplayOrder())
                .build();
    }
}
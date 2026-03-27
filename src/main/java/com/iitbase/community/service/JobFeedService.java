package com.iitbase.community.service;

import com.iitbase.community.spec.JobSpecifications;
import com.iitbase.community.dto.JobFeedFilterRequest;
import com.iitbase.community.dto.JobFeedResponse;
import com.iitbase.community.enums.JobSource;
import com.iitbase.recruiter.service.RecruiterJobService;
import com.iitbase.recruiter.spec.RecruiterJobSpecifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JobFeedService {

    private final CommunityJobService jobService;
    private final RecruiterJobService recruiterJobService;

    public Map<String, Object> getFeed(JobFeedFilterRequest filter) {

        LocalDateTime postedAfter = filter.getPostedAfter() != null
                ? LocalDateTime.of(
                LocalDate.parse(filter.getPostedAfter()), LocalTime.MIN)
                : null;

        List<JobFeedResponse> merged = new ArrayList<>();

        // ── Fetch community jobs (unless filter asks for recruiter only) ──────
        boolean includeCommunity = filter.getSource() == null
                || filter.getSource() == JobSource.COMMUNITY;

        if (includeCommunity) {
            List<JobFeedResponse> communityJobs =
                    fetchCommunityJobs(filter, postedAfter);
            merged.addAll(communityJobs);
        }

        // ── Fetch recruiter jobs (unless filter asks for community only) ──────
        boolean includeRecruiter = filter.getSource() == null
                || filter.getSource() == JobSource.RECRUITER_EXTERNAL
                || filter.getSource() == JobSource.RECRUITER_DIRECT;

        if (includeRecruiter) {
            List<JobFeedResponse> recruiterJobs =
                    fetchRecruiterJobs(filter, postedAfter);
            merged.addAll(recruiterJobs);
        }

        // ── Sort merged list by createdAt DESC ────────────────────────────────
        merged.sort(Comparator
                .comparing(JobFeedResponse::getCreatedAt)
                .reversed());

        // ── Manual pagination on merged list ─────────────────────────────────
        int total = merged.size();
        int fromIndex = filter.getPage() * filter.getSize();
        int toIndex = Math.min(fromIndex + filter.getSize(), total);

        List<JobFeedResponse> pageContent = fromIndex >= total
                ? List.of()
                : merged.subList(fromIndex, toIndex);

        int totalPages = filter.getSize() == 0
                ? 0
                : (int) Math.ceil((double) total / filter.getSize());

        log.debug("JobFeed: total={} community={} recruiter={} page={}/{}",
                total,
                includeCommunity ? "included" : "excluded",
                includeRecruiter ? "included" : "excluded",
                filter.getPage(), totalPages);

        return Map.of(
                "jobs",        pageContent,
                "currentPage", filter.getPage(),
                "totalItems",  total,
                "totalPages",  totalPages
        );
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private List<JobFeedResponse> fetchCommunityJobs(
            JobFeedFilterRequest filter,
            LocalDateTime postedAfter) {

        // Fetch all approved community jobs matching filter (no pagination yet)
        // We need all results to merge + sort before paginating
        Pageable unpaged = Pageable.unpaged();

        var spec = Specification
                .where(JobSpecifications.approved())
                .and(JobSpecifications.domain(filter.getDomain()))
                .and(JobSpecifications.techRole(filter.getTechRole()))
                .and(JobSpecifications.experience(
                        filter.getMinExperience(),
                        filter.getMaxExperience()))
                .and(JobSpecifications.location(filter.getLocation()))
                .and(JobSpecifications.techStack(filter.getTechStack()))
                .and(JobSpecifications.postedAfter(postedAfter));

        return jobService.findAll(spec, unpaged)
                .getContent()
                .stream()
                .map(JobFeedResponse::fromCommunityJob)
                .toList();
    }
    // NOTE: In-memory merge works well up to ~10k total jobs.
    // At scale, replace with a DB UNION view across jobs + recruiter_jobs tables.
    // Interface contract (JobFeedService.getFeed) stays unchanged.

    private List<JobFeedResponse> fetchRecruiterJobs(
            JobFeedFilterRequest filter,
            LocalDateTime postedAfter) {

        Pageable unpaged = Pageable.unpaged();

        var spec = Specification
                .where(RecruiterJobSpecifications.active())
                .and(RecruiterJobSpecifications.notExpired())
                .and(RecruiterJobSpecifications.domain(filter.getDomain()))
                .and(RecruiterJobSpecifications.techRole(filter.getTechRole()))
                .and(RecruiterJobSpecifications.experience(
                        filter.getMinExperience(),
                        filter.getMaxExperience()))
                .and(RecruiterJobSpecifications.location(filter.getLocation()))
                .and(RecruiterJobSpecifications.techStack(filter.getTechStack()));

        // Apply source filter — RECRUITER_EXTERNAL vs RECRUITER_DIRECT
        if (filter.getSource() == JobSource.RECRUITER_EXTERNAL) {
            spec = spec.and(RecruiterJobSpecifications.applyType(
                    com.iitbase.recruiter.enums.JobApplyType.EXTERNAL));
        } else if (filter.getSource() == JobSource.RECRUITER_DIRECT) {
            spec = spec.and(RecruiterJobSpecifications.applyType(
                    com.iitbase.recruiter.enums.JobApplyType.INTERNAL));
        }

        return recruiterJobService.getActiveJobs(spec, unpaged)
                .getContent()
                .stream()
                .map(JobFeedResponse::fromRecruiterJob)
                .toList();
    }
}
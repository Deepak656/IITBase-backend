package com.iitbase.community.controller;

import com.iitbase.common.ApiResponse;
import com.iitbase.community.dto.*;
import com.iitbase.community.enums.JobDomain;
import com.iitbase.community.enums.JobStatus;
import com.iitbase.community.enums.TechRole;
import com.iitbase.community.enums.JobSource;
import com.iitbase.community.service.JobFeedService;
import com.iitbase.community.service.CommunityJobService;
import com.iitbase.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommunityJobController {

    private final CommunityJobService jobService;
    private final JobFeedService jobFeedService;

    @GetMapping("/public/jobs")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPublicJobs(
            @RequestParam(required = false) JobDomain domain,
            @RequestParam(required = false) TechRole techRole,
            @RequestParam(required = false) Integer minExperience,
            @RequestParam(required = false) Integer maxExperience,
            @RequestParam(required = false) String techStack,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String postedAfter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        JobFilterRequest filter = new JobFilterRequest();
        filter.setDomain(domain);
        filter.setTechRole(techRole);
        filter.setMinExperience(minExperience);
        filter.setMaxExperience(maxExperience);
        filter.setLocation(location);
        filter.setPostedAfter(postedAfter);
        filter.setPage(page);
        filter.setSize(size);

        Map<String, Object> result = jobService.getPublicJobs(filter);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
    @GetMapping("/public/feed")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getJobFeed(
            @RequestParam(required = false) JobDomain domain,
            @RequestParam(required = false) TechRole techRole,
            @RequestParam(required = false) JobSource source,
            @RequestParam(required = false) Integer minExperience,
            @RequestParam(required = false) Integer maxExperience,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String postedAfter,
            @RequestParam(required = false) List<String> techStack,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        JobFeedFilterRequest filter = new JobFeedFilterRequest();
        filter.setDomain(domain);
        filter.setTechRole(techRole);
        filter.setSource(source);
        filter.setMinExperience(minExperience);
        filter.setMaxExperience(maxExperience);
        filter.setLocation(location);
        filter.setPostedAfter(postedAfter);
        filter.setTechStack(techStack);
        filter.setPage(page);
        filter.setSize(size);

        return ResponseEntity.ok(ApiResponse.success(jobFeedService.getFeed(filter)));
    }
    /**
     * Get my job submissions with optional status filter
     *
     * Frontend usage examples:
     * - All tab: GET /api/jobs/my-submissions?page=0&size=20
     * - Pending tab: GET /api/jobs/my-submissions?statuses=PENDING&page=0&size=20
     * - Approved tab: GET /api/jobs/my-submissions?statuses=APPROVED&page=0&size=20
     * - Rejected tab: GET /api/jobs/my-submissions?statuses=REJECTED&page=0&size=20
     */
    @GetMapping("/jobs/my-submissions")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMyJobs(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) List<JobStatus> statuses,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        MyJobsFilterRequest filter = new MyJobsFilterRequest();
        filter.setStatuses(statuses);
        filter.setPage(page);
        filter.setSize(size);

        Map<String, Object> result = jobService.getMyJobs(user.getId(), filter);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
    /**
     * Get statistics for my job submissions
     * Returns counts by status without loading actual jobs
     *
     * GET /api/jobs/my-submissions/stats
     */
    @GetMapping("/jobs/my-submissions/stats")
    public ResponseEntity<ApiResponse<MyJobsStatsResponse>> getMyJobsStats(
            @AuthenticationPrincipal User user
    ) {
        MyJobsStatsResponse stats = jobService.getMyJobsStats(user.getId());
        return ResponseEntity.ok(ApiResponse.success(stats));

    }

    @PostMapping("/jobs/submit")
    public ResponseEntity<ApiResponse<Void>> submitJob(
            @Valid @RequestBody JobCreateRequest request,
            @AuthenticationPrincipal User user
    ) {
        jobService.submitJob(request, user);
        return ResponseEntity.ok(ApiResponse.success(null,"Job submitted for review"));
    }
    @GetMapping("/public/jobs/{id}")
    public ResponseEntity<ApiResponse<JobResponse>> getJobById(@PathVariable Long id) {
        JobResponse job = jobService.getJobById(id);
        return ResponseEntity.ok(ApiResponse.success(job));
    }

}
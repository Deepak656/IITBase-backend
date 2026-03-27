package com.iitbase.community.spec;

import com.iitbase.community.entity.CommunityJob;
import com.iitbase.community.enums.JobDomain;
import com.iitbase.community.enums.JobStatus;
import com.iitbase.community.enums.TechRole;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JobSpecifications {

    private JobSpecifications() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static Specification<CommunityJob> approved() {
        return (root, query, cb) ->
                cb.equal(root.get("status"), JobStatus.APPROVED);
    }

    public static Specification<CommunityJob> submittedBy(Long userId) {
        return (root, query, cb) ->
                userId == null ? null : cb.equal(root.get("submittedBy"), userId);
    }

    public static Specification<CommunityJob> statuses(List<JobStatus> statuses) {
        return (root, query, cb) ->
                statuses == null || statuses.isEmpty()
                        ? null
                        : root.get("status").in(statuses);
    }

    // Replaces role()
    public static Specification<CommunityJob> domain(JobDomain domain) {
        return (root, query, cb) ->
                domain == null ? null : cb.equal(root.get("jobDomain"), domain);
    }

    // Only meaningful when domain = TECHNOLOGY
    public static Specification<CommunityJob> techRole(TechRole techRole) {
        return (root, query, cb) ->
                techRole == null ? null : cb.equal(root.get("techRole"), techRole);
    }

    public static Specification<CommunityJob> experience(Integer min, Integer max) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (min != null)
                predicates.add(cb.greaterThanOrEqualTo(root.get("maxExperience"), min));
            if (max != null)
                predicates.add(cb.lessThanOrEqualTo(root.get("minExperience"), max));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<CommunityJob> location(String location) {
        return (root, query, cb) ->
                location == null ? null :
                        cb.like(cb.lower(root.get("location")),
                                "%" + location.toLowerCase() + "%");
    }

    public static Specification<CommunityJob> techStack(List<String> techStack) {
        return (root, query, cb) -> {
            if (techStack == null || techStack.isEmpty()) return null;
            Join<CommunityJob, String> techJoin = root.join("techStack");
            query.distinct(true);
            return techJoin.in(techStack);
        };
    }

    public static Specification<CommunityJob> postedAfter(LocalDateTime postedAfter) {
        return (root, query, cb) ->
                postedAfter == null ? null :
                        cb.greaterThanOrEqualTo(root.get("createdAt"), postedAfter);
    }
}
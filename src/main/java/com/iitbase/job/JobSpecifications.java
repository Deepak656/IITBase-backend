package com.iitbase.job;

import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

public class JobSpecifications {
    // 👇 Prevent instantiation
    private JobSpecifications() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static Specification<Job> approved() {
        return (root, query, cb) ->
                cb.equal(root.get("status"), JobStatus.APPROVED);
    }

    public static Specification<Job> submittedBy(Long userId) {
        return (root, query, cb) ->
                userId == null ? null : cb.equal(root.get("submittedBy"), userId);
    }

    public static Specification<Job> statuses(List<JobStatus> statuses) {
        return (root, query, cb) ->
                statuses == null || statuses.isEmpty() ? null : root.get("status").in(statuses);
    }

    public static Specification<Job> role(PrimaryRole role) {
        return (root, query, cb) ->
                role == null ? null : cb.equal(root.get("primaryRole"), role);
    }

    public static Specification<Job> experience(Integer min, Integer max) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (min != null)
                predicates.add(cb.greaterThanOrEqualTo(root.get("maxExperience"), min));
            if (max != null)
                predicates.add(cb.lessThanOrEqualTo(root.get("minExperience"), max));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Job> location(String location) {
        return (root, query, cb) ->
                location == null ? null :
                        cb.like(cb.lower(root.get("location")),
                                "%" + location.toLowerCase() + "%");
    }

    public static Specification<Job> techStack(List<String> techStack) {
        return (root, query, cb) -> {
            if (techStack == null || techStack.isEmpty()) return null;
            Join<Job, String> techJoin = root.join("techStack");
            query.distinct(true);
            return techJoin.in(techStack);
        };
    }
    public static Specification<Job> postedAfter(LocalDateTime postedAfter) {
        return (root, query, cb) ->
                postedAfter == null ? null :
                        cb.greaterThanOrEqualTo(root.get("createdAt"), postedAfter);
    }
}

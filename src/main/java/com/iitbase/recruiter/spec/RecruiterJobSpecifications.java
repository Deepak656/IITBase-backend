package com.iitbase.recruiter.spec;

import com.iitbase.community.enums.JobDomain;
import com.iitbase.community.enums.TechRole;
import com.iitbase.recruiter.entity.RecruiterJob;
import com.iitbase.recruiter.enums.JobApplyType;
import com.iitbase.recruiter.enums.RecruiterJobStatus;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class RecruiterJobSpecifications {

    private RecruiterJobSpecifications() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static Specification<RecruiterJob> active() {
        return (root, query, cb) ->
                cb.equal(root.get("status"), RecruiterJobStatus.ACTIVE);
    }

    public static Specification<RecruiterJob> byRecruiter(Long recruiterId) {
        return (root, query, cb) ->
                recruiterId == null ? null :
                        cb.equal(root.get("recruiter").get("id"), recruiterId);
    }

    public static Specification<RecruiterJob> byCompany(Long companyId) {
        return (root, query, cb) ->
                companyId == null ? null :
                        cb.equal(root.get("company").get("id"), companyId);
    }

    public static Specification<RecruiterJob> domain(JobDomain domain) {
        return (root, query, cb) ->
                domain == null ? null : cb.equal(root.get("jobDomain"), domain);
    }

    public static Specification<RecruiterJob> techRole(TechRole techRole) {
        return (root, query, cb) ->
                techRole == null ? null : cb.equal(root.get("techRole"), techRole);
    }

    public static Specification<RecruiterJob> applyType(JobApplyType applyType) {
        return (root, query, cb) ->
                applyType == null ? null : cb.equal(root.get("applyType"), applyType);
    }

    public static Specification<RecruiterJob> experience(Integer min, Integer max) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (min != null)
                predicates.add(cb.greaterThanOrEqualTo(root.get("maxExperience"), min));
            if (max != null)
                predicates.add(cb.lessThanOrEqualTo(root.get("minExperience"), max));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<RecruiterJob> location(String location) {
        return (root, query, cb) ->
                location == null ? null :
                        cb.like(cb.lower(root.get("location")),
                                "%" + location.toLowerCase() + "%");
    }

    public static Specification<RecruiterJob> techStack(List<String> techStack) {
        return (root, query, cb) -> {
            if (techStack == null || techStack.isEmpty()) return null;
            Join<RecruiterJob, String> join = root.join("techStack");
            query.distinct(true);
            return join.in(techStack);
        };
    }

    public static Specification<RecruiterJob> notExpired() {
        return (root, query, cb) -> cb.or(
                cb.isNull(root.get("expiresAt")),
                cb.greaterThan(root.get("expiresAt"),
                        java.time.LocalDateTime.now())
        );
    }
}
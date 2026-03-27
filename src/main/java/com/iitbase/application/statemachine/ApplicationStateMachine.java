package com.iitbase.application.statemachine;

import com.iitbase.application.enums.ApplicationStatus;
import com.iitbase.application.exception.InvalidStatusTransitionException;

import java.util.Map;
import java.util.Set;

public class ApplicationStateMachine {

    private ApplicationStateMachine() {
        throw new UnsupportedOperationException("Utility class");
    }

    // Valid transitions — recruiter driven
    private static final Map<ApplicationStatus, Set<ApplicationStatus>>
            RECRUITER_TRANSITIONS = Map.of(
            ApplicationStatus.APPLIED,    Set.of(
                    ApplicationStatus.SCREENING,
                    ApplicationStatus.REJECTED
            ),
            ApplicationStatus.SCREENING,  Set.of(
                    ApplicationStatus.INTERVIEW,
                    ApplicationStatus.REJECTED
            ),
            ApplicationStatus.INTERVIEW,  Set.of(
                    ApplicationStatus.OFFER,
                    ApplicationStatus.REJECTED
            ),
            ApplicationStatus.OFFER,      Set.of(
                    ApplicationStatus.HIRED,
                    ApplicationStatus.REJECTED
            )
    );

    // Valid transitions — jobseeker driven
    // Jobseeker can only withdraw, and only if not already hired/rejected
    private static final Set<ApplicationStatus> WITHDRAWABLE_FROM = Set.of(
            ApplicationStatus.APPLIED,
            ApplicationStatus.SCREENING,
            ApplicationStatus.INTERVIEW,
            ApplicationStatus.OFFER
    );

    /**
     * Validates recruiter-driven transition.
     * Throws InvalidStatusTransitionException if not allowed.
     */
    public static void validateRecruiterTransition(
            ApplicationStatus current,
            ApplicationStatus next) {

        Set<ApplicationStatus> allowed = RECRUITER_TRANSITIONS.get(current);

        if (allowed == null || !allowed.contains(next)) {
            throw new InvalidStatusTransitionException(current, next);
        }
    }

    /**
     * Validates jobseeker withdrawal.
     * Jobseeker can only withdraw — not move to any other status.
     */
    public static void validateWithdrawal(ApplicationStatus current) {
        if (!WITHDRAWABLE_FROM.contains(current)) {
            throw new InvalidStatusTransitionException(
                    current, ApplicationStatus.WITHDRAWN,
                    "Cannot withdraw application in status: " + current
            );
        }
    }
}
package com.iitbase.jobseeker.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobPreferenceDTO {

    private Long id;

    @Size(max = 255)
    private String currentLocation;

    @Size(max = 50)
    private String workLocationType;
    // Example: REMOTE, HYBRID, ONSITE

    private List<String> preferredCities;

    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal previousSalary;

    @Size(max = 10)
    private String previousSalaryCurrency;

    @Size(max = 50)
    private String noticePeriod;
}
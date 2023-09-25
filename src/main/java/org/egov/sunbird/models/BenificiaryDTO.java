package org.egov.sunbird.models;

import java.util.Date;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class BenificiaryDTO {
    private String beneficiaryId;
    private String beneficiaryType;
    private String projectId;
    private String tenantId;
    private String registrationDate;

    // Constructors, getters, and setters

}
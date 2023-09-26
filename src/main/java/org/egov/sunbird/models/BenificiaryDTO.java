package org.egov.sunbird.models;

import java.util.Date;

import lombok.*;
import org.springframework.beans.factory.annotation.Value;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class BenificiaryDTO {
    @Value("")
    private String beneficiaryId;
    @Value("")
    private String beneficiaryType;
    @Value("")
    private String projectId;
    @Value("")
    private String tenantId;
    @Value("")
    private String registrationDate;

    // Constructors, getters, and setters

}
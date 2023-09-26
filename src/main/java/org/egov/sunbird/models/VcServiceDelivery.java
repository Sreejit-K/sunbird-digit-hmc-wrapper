package org.egov.sunbird.models;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import digit.models.coremodels.AuditDetails;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VcServiceDelivery {

    @Value("")
    @JsonProperty("id")
    private String id ;

    @Value("")
    @JsonProperty("certificateId")
    private String certificateId ;

    @Value("")
    @JsonProperty("serviceTaskId")
    private String serviceTaskId;

    @Value("")
    @JsonProperty("beneficiaryId")
    private String beneficiaryId;

    @Value("")
    @JsonProperty("distributedBy")
    private String distributedBy;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;

}

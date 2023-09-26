package org.egov.sunbird.Repository.rowmapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import digit.models.coremodels.AuditDetails;
import org.egov.sunbird.models.VcServiceDelivery;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
@Component
public class VcServiceDeliveryRowMapper implements RowMapper<VcServiceDelivery> {

    @Override
    public VcServiceDelivery mapRow(ResultSet rs, int rowNum) throws SQLException {
        VcServiceDelivery vcServiceDelivery = new VcServiceDelivery();

        // Map the columns from the ResultSet to the corresponding fields in the class
        vcServiceDelivery.setId(rs.getString("id"));
        vcServiceDelivery.setCertificateId(rs.getString("certificateId"));
        vcServiceDelivery.setServiceTaskId(rs.getString("serviceTaskId"));
        vcServiceDelivery.setBeneficiaryId(rs.getString("beneficiaryId"));
        vcServiceDelivery.setDistributedBy(rs.getString("distributedBy"));

        // Assuming you have a custom method to map the audit details using Jackson
        // Here's a hypothetical method named mapAuditDetails
        AuditDetails auditDetails = mapAuditDetails(rs.getString("auditDetails"));
        vcServiceDelivery.setAuditDetails(auditDetails);

        return vcServiceDelivery;
    }

    // This method uses Jackson's ObjectMapper to deserialize the auditDetails JSON
    private AuditDetails mapAuditDetails(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json, AuditDetails.class);
        } catch (IOException e) {
            // Handle the exception appropriately
            e.printStackTrace();
            return null;
        }
    }
}

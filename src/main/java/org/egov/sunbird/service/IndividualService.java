package org.egov.sunbird.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.util.Strings;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.common.http.client.ServiceRequestClient;
import org.egov.common.models.individual.Individual;
import org.egov.common.models.individual.IndividualBulkResponse;
import org.egov.common.models.individual.IndividualSearch;
import org.egov.common.models.individual.IndividualSearchRequest;
import org.egov.sunbird.config.SunbirdProperties;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

import static org.egov.sunbird.Constants.INDIVIDUAL_FETCH_ERROR;
import static org.egov.sunbird.Constants.INDIVIDUAL_FETCH_ERROR_MESSAGE;

@Component
@Slf4j
public class IndividualService {

    private final SunbirdProperties sunbirdProperties;

    private final ServiceRequestClient serviceRequestClient;

    public IndividualService(SunbirdProperties sunbirdProperties, ServiceRequestClient serviceRequestClient) {
        this.sunbirdProperties = sunbirdProperties;
        this.serviceRequestClient = serviceRequestClient;
    }

    public List<Individual> searchIndividuals(List<String> clientRefIds, String tenantId) {
        IndividualSearchRequest request = IndividualSearchRequest.builder()
                .requestInfo(RequestInfo.builder().
                        userInfo(User.builder()
                                .uuid("transformer-uuid")
                                .build())
                        .build())
                .individual(IndividualSearch.builder().
                        clientReferenceId(clientRefIds).build())
                .build();
        IndividualBulkResponse response;
        try {
            StringBuilder uri = new StringBuilder();
            uri.append(sunbirdProperties.getIndividualHost())
                    .append(sunbirdProperties.getIndividualSearchUrl())
                    .append("?limit=").append(sunbirdProperties.getSearchApiLimit())
                    .append("&offset=0")
                    .append("&tenantId=").append(tenantId);
            response = serviceRequestClient.fetchResult(uri,
                    request,
                    IndividualBulkResponse.class);
        } catch (Exception e) {
            log.error("error while fetching individuals {}", ExceptionUtils.getStackTrace(e));
            throw new CustomException(INDIVIDUAL_FETCH_ERROR,
                    INDIVIDUAL_FETCH_ERROR_MESSAGE + Strings.join(clientRefIds, ','));
        }
        return response.getIndividual();
    }
}

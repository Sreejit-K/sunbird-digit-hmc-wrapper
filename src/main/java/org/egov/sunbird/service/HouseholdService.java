package org.egov.sunbird.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.util.Strings;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.common.http.client.ServiceRequestClient;
import org.egov.common.models.household.Household;
import org.egov.common.models.household.HouseholdBulkResponse;
import org.egov.common.models.household.HouseholdMember;
import org.egov.common.models.household.HouseholdMemberBulkResponse;
import org.egov.common.models.household.HouseholdMemberSearch;
import org.egov.common.models.household.HouseholdMemberSearchRequest;
import org.egov.common.models.household.HouseholdSearch;
import org.egov.common.models.household.HouseholdSearchRequest;
import org.egov.sunbird.config.SunbirdProperties;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

import static org.egov.sunbird.Constants.HOUSEHOLD_FETCH_ERROR;
import static org.egov.sunbird.Constants.HOUSEHOLD_FETCH_ERROR_MESSAGE;
import static org.egov.sunbird.Constants.HOUSEHOLD_MEMBER_FETCH_ERROR;
import static org.egov.sunbird.Constants.HOUSEHOLD_MEMBER_FETCH_ERROR_MESSAGE;

@Component
@Slf4j
public class HouseholdService {
    private final SunbirdProperties sunbirdProperties;

    private final ServiceRequestClient serviceRequestClient;

    public HouseholdService(SunbirdProperties sunbirdProperties, ServiceRequestClient serviceRequestClient) {
        this.sunbirdProperties = sunbirdProperties;
        this.serviceRequestClient = serviceRequestClient;
    }

    public List<Household> searchHouseholds(List<String> clientRefIds, String tenantId) {
        HouseholdSearchRequest request = HouseholdSearchRequest.builder()
                .requestInfo(RequestInfo.builder().
                        userInfo(User.builder()
                                .uuid("transformer-uuid")
                                .build())
                        .build())
                .household(HouseholdSearch.builder().
                        clientReferenceId(clientRefIds).build())
                .build();
        HouseholdBulkResponse response;
        try {
            StringBuilder uri = new StringBuilder();
            uri.append(sunbirdProperties.getHouseholdHost())
                    .append(sunbirdProperties.getHouseholdSearchUrl())
                    .append("?limit=").append(sunbirdProperties.getSearchApiLimit())
                    .append("&offset=0")
                    .append("&tenantId=").append(tenantId);
            response = serviceRequestClient.fetchResult(uri,
                    request,
                    HouseholdBulkResponse.class);
        } catch (Exception e) {
            log.error("error while fetching household {}", ExceptionUtils.getStackTrace(e));
            throw new CustomException(HOUSEHOLD_FETCH_ERROR,
                    HOUSEHOLD_FETCH_ERROR_MESSAGE + Strings.join(clientRefIds, ','));
        }
        return response.getHouseholds();
    }

    public List<HouseholdMember> searchHouseholdMembers(String householdClientReferenceId, String tenantId, Boolean isHouseholdHead) {
        HouseholdMemberSearchRequest request = HouseholdMemberSearchRequest.builder()
                .requestInfo(RequestInfo.builder().
                        userInfo(User.builder()
                                .uuid("transformer-uuid")
                                .build())
                        .build())
                .householdMemberSearch(HouseholdMemberSearch.builder().
                        householdClientReferenceId(householdClientReferenceId).isHeadOfHousehold(isHouseholdHead).build())
                .build();
        HouseholdMemberBulkResponse response;
        try {
            StringBuilder uri = new StringBuilder();
            uri.append(sunbirdProperties.getHouseholdHost())
                    .append(sunbirdProperties.getHouseholdMemberSearchUrl())
                    .append("?limit=").append(sunbirdProperties.getSearchApiLimit())
                    .append("&offset=0")
                    .append("&tenantId=").append(tenantId);
            response = serviceRequestClient.fetchResult(uri,
                    request,
                    HouseholdMemberBulkResponse.class);
        } catch (Exception e) {
            log.error("error while fetching household member {}", ExceptionUtils.getStackTrace(e));
            throw new CustomException(HOUSEHOLD_MEMBER_FETCH_ERROR,
                    HOUSEHOLD_MEMBER_FETCH_ERROR_MESSAGE + householdClientReferenceId);
        }
        return response.getHouseholdMembers();
    }
}

package org.egov.sunbird.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.util.Strings;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.common.http.client.ServiceRequestClient;
import org.egov.common.models.product.ProductVariant;
import org.egov.common.models.product.ProductVariantResponse;
import org.egov.common.models.product.ProductVariantSearch;
import org.egov.common.models.project.BeneficiaryBulkResponse;
import org.egov.common.models.project.BeneficiarySearchRequest;
import org.egov.common.models.project.Project;
import org.egov.common.models.project.ProjectBeneficiary;
import org.egov.common.models.project.ProjectBeneficiarySearch;
import org.egov.common.models.project.ProjectRequest;
import org.egov.common.models.project.ProjectResponse;
import org.egov.common.models.product.ProductVariantSearchRequest;

import org.egov.sunbird.config.SunbirdProperties;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

import static org.egov.sunbird.Constants.PROJECT_BENEFICIARY_FETCH_ERROR;
import static org.egov.sunbird.Constants.PROJECT_BENEFICIARY_FETCH_ERROR_MESSAGE;


@Component
@Slf4j
public class ProjectService {

    private final SunbirdProperties sunbirdProperties;

    private final ServiceRequestClient serviceRequestClient;



    public ProjectService(SunbirdProperties sunbirdProperties,
                          ServiceRequestClient serviceRequestClient,
                          ObjectMapper objectMapper) {
        this.sunbirdProperties = sunbirdProperties;
        this.serviceRequestClient = serviceRequestClient;
    }

    private List<Project> searchProject(String projectId, String tenantId) {

        ProjectRequest request = ProjectRequest.builder()
                .requestInfo(RequestInfo.builder().
                userInfo(User.builder()
                        .uuid("transformer-uuid")
                        .build())
                .build())
                .projects(Collections.singletonList(Project.builder().id(projectId).tenantId(tenantId).build()))
                .build();

        ProjectResponse response;
        try {
            StringBuilder uri = new StringBuilder();
            uri.append(sunbirdProperties.getProjectHost())
                    .append(sunbirdProperties.getProjectSearchUrl())
                    .append("?limit=").append(sunbirdProperties.getSearchApiLimit())
                    .append("&offset=0")
                    .append("&tenantId=").append(tenantId);
            response = serviceRequestClient.fetchResult(uri,
                    request,
                    ProjectResponse.class);
        } catch (Exception e) {
            log.error("error while fetching project list {}", ExceptionUtils.getStackTrace(e));
            throw new CustomException("PROJECT_FETCH_ERROR",
                    "error while fetching project details for id: " + projectId);
        }
        return response.getProject();
    }

    public List<ProjectBeneficiary> searchBeneficiaries(List<String> projectBeneficiaryClientRefIds, String tenantId) {
        BeneficiarySearchRequest request = BeneficiarySearchRequest.builder()
                .requestInfo(RequestInfo.builder().
                        userInfo(User.builder()
                                .uuid("transformer-uuid")
                                .build())
                        .build())
                .projectBeneficiary( ProjectBeneficiarySearch.builder().
                        clientReferenceId(projectBeneficiaryClientRefIds).build())
                .build();
        BeneficiaryBulkResponse response;
        try {
            StringBuilder uri = new StringBuilder();
            uri.append(sunbirdProperties.getProjectHost())
                    .append(sunbirdProperties.getProjectBeneficiarySearchUrl())
                    .append("?limit=").append(sunbirdProperties.getSearchApiLimit())
                    .append("&offset=0")
                    .append("&tenantId=").append(tenantId);
            response = serviceRequestClient.fetchResult(uri,
                    request,
                    BeneficiaryBulkResponse.class);
        } catch (Exception e) {
            log.error("error while fetching beneficiary {}", ExceptionUtils.getStackTrace(e));
            throw new CustomException(PROJECT_BENEFICIARY_FETCH_ERROR,
                    PROJECT_BENEFICIARY_FETCH_ERROR_MESSAGE + Strings.join(projectBeneficiaryClientRefIds, ','));
        }
        return response.getProjectBeneficiaries();
    }


}

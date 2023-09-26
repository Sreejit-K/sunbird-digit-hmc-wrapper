package org.egov.sunbird.service;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.http.client.ServiceRequestClient;
import org.egov.common.models.household.Household;
import org.egov.common.models.household.HouseholdMember;
import org.egov.common.models.individual.Individual;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import org.egov.common.models.project.BeneficiaryBulkResponse;
import org.egov.common.models.project.ProjectBeneficiary;
import org.egov.common.models.project.Task;
import org.egov.common.models.project.TaskResource;
import org.egov.common.producer.Producer;
import org.egov.sunbird.Repository.VCServiceDeliveryRepository;
import org.egov.sunbird.config.SunbirdProperties;
import org.egov.sunbird.models.BenificiaryDTO;
import org.egov.sunbird.models.RegistryRequest;
import org.egov.sunbird.models.ResourceDTO;
import org.egov.sunbird.models.VcServiceDelivery;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static org.egov.sunbird.Constants.HOUSEHOLD_FETCH_ERROR;
import static org.egov.sunbird.Constants.HOUSEHOLD_FETCH_ERROR_MESSAGE;
import static org.egov.sunbird.Constants.HOUSEHOLD_MEMBER_FETCH_ERROR;
import static org.egov.sunbird.Constants.HOUSEHOLD_MEMBER_FETCH_ERROR_MESSAGE;
import static org.egov.sunbird.Constants.INDIVIDUAL_FETCH_ERROR;
import static org.egov.sunbird.Constants.INDIVIDUAL_FETCH_ERROR_MESSAGE;
import static org.egov.sunbird.Constants.PROJECT_BENEFICIARY_FETCH_ERROR;
import static org.egov.sunbird.Constants.PROJECT_BENEFICIARY_FETCH_ERROR_MESSAGE;

@Component
@Slf4j
public class ProjectTaskService {

    private final Producer producer;

    private final VCServiceDeliveryRepository vcServiceDeliveryRepository;
    private final SunbirdProperties properties;

    private final ServiceRequestClient serviceRequestClient;

    private final HouseholdService householdService;
    private final IndividualService individualService;
    private final ProjectService projectService;



    @Autowired
    protected ProjectTaskService(Producer producer, VCServiceDeliveryRepository vcServiceDeliveryRepository, SunbirdProperties properties,
                                 ServiceRequestClient serviceRequestClient, HouseholdService householdService,
                                 IndividualService individualService,
                                 ProjectService projectService) {
        this.producer = producer;
        this.vcServiceDeliveryRepository = vcServiceDeliveryRepository;
        this.properties = properties;
        this.serviceRequestClient = serviceRequestClient;
        this.householdService = householdService;
        this.individualService = individualService;
        this.projectService = projectService;
    }

    public void transform(List<Task> taskList, Boolean isCreate) {

        List<String> projectBeneficiaryClientReferenceIds = new ArrayList<>();
        List<String> householdClientReferenceIds = new ArrayList<>();
        List<String> individualClientReferenceIds = new ArrayList<>();
        List<String> productVariantReferenceIds = new ArrayList<>();
        String tenantId = taskList.get(0).getTenantId();

        Map<String, ProjectBeneficiary> projectBeneficiaryMap = new HashMap<>();
        Map<String, Household> householdMap = new HashMap<>();
        Map<String, Individual> individualMap = new HashMap<>();
        Map<String, HouseholdMember> hosueholdHeadMap = new HashMap<>();

        for (Task task : taskList) {
            projectBeneficiaryClientReferenceIds.add(task.getProjectBeneficiaryClientReferenceId());
        }

        setProjectBeneficiaries(projectBeneficiaryClientReferenceIds, tenantId, projectBeneficiaryMap);
        setHouseholds(projectBeneficiaryClientReferenceIds, tenantId, householdClientReferenceIds, householdMap);
        setHouseholdHeads(householdClientReferenceIds, tenantId, individualClientReferenceIds, hosueholdHeadMap);
        setIndividuals(individualClientReferenceIds, tenantId, individualMap);

        processTasks(taskList, projectBeneficiaryMap, householdMap, hosueholdHeadMap, individualMap, isCreate);

    }

    private void processTasks(List<Task> taskList,
                              Map<String, ProjectBeneficiary> projectBeneficiaryMap,
                              Map<String, Household> householdMap,
                              Map<String, HouseholdMember> hosueholdHeadMap,
                              Map<String, Individual> individualMap,
                              boolean isCreate) {
        for (Task task : taskList) {
            ProjectBeneficiary projectBeneficiary = projectBeneficiaryMap
                    .get(task.getProjectBeneficiaryClientReferenceId());
            if (projectBeneficiary == null) {
                throw new CustomException(PROJECT_BENEFICIARY_FETCH_ERROR,
                        PROJECT_BENEFICIARY_FETCH_ERROR_MESSAGE + task.getProjectBeneficiaryClientReferenceId());
            }
            Household household = householdMap.get(projectBeneficiary.getBeneficiaryClientReferenceId());
            if (household == null) {
                throw new CustomException(HOUSEHOLD_FETCH_ERROR,
                        HOUSEHOLD_FETCH_ERROR_MESSAGE + projectBeneficiary.getBeneficiaryClientReferenceId());
            }
            HouseholdMember householdHeadMember = hosueholdHeadMap.get(household.getClientReferenceId());

            if (householdHeadMember == null) {
                throw new CustomException(HOUSEHOLD_MEMBER_FETCH_ERROR,
                        HOUSEHOLD_MEMBER_FETCH_ERROR_MESSAGE + household.getClientReferenceId());
            }

            Individual individual = individualMap.get(householdHeadMember.getIndividualClientReferenceId());

            if (individual == null) {
                throw new CustomException(INDIVIDUAL_FETCH_ERROR,
                        INDIVIDUAL_FETCH_ERROR_MESSAGE + householdHeadMember.getIndividualClientReferenceId());
            }

            RegistryRequest reqToCreateVC = registryRequestTransformer(task.getResources(), projectBeneficiary,task.getId());
            //TODO set all values in sunbird object
            log.debug( "this is the req that we send to registry",reqToCreateVC);

            if (isCreate) {
                try {
                    StringBuilder uri = new StringBuilder();
                    uri.append(properties.getRegistryHost()).append(properties.getRegistryURL());
//                    Object response = serviceRequestClient.fetchResult(uri,
//                            reqToCreateVC,
//                            BeneficiaryBulkResponse.class);
//                    log.debug((String) response);
                    // Create the audit data
                    VcServiceDelivery auditDetailsToAddInDB = VcServiceDelivery.builder()
                            .id(UUID.randomUUID().toString())
                            .serviceTaskId(task.getId())
                            .certificateId("Id from response")
                            .distributedBy(task.getCreatedBy())
                            .beneficiaryId(task.getProjectBeneficiaryId())
                            .auditDetails(task.getAuditDetails())
                            .build();
                    List<VcServiceDelivery> auditDetails = new ArrayList<VcServiceDelivery>();
                    auditDetails.add(auditDetailsToAddInDB );
                    vcServiceDeliveryRepository.save( auditDetails, properties.getSaveServiceDeliveryVCTaskTopic());
//                    producer.push(properties.getSaveServiceDeliveryVCTaskTopic(), auditDetailsToAddInDB);
                } catch (Exception exception) {
                    log.debug(exception.getMessage());
                    log.error("error occurred while creating a VC using the Registry service: {}", exception.getMessage());
                }
            } else {
                //                    TODO update logic here
                //                 get the vcId from the mapper table using the taskID
                //                 update the vc in registry
                //                 Emit an event to kafka topic to update using the taskID
            }
        }
    }


    public static String convertToISO8601(long unixTimestamp) {
        // Convert Unix timestamp to Instant
        Instant instant = Instant.ofEpochSecond(unixTimestamp);

        // Convert Instant to the ISO 8601 format with UTC timezone
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
                .withZone(ZoneId.of("UTC"));
        String iso8601DateTime = formatter.format(instant);

        return iso8601DateTime;
    }

    public static RegistryRequest registryRequestTransformer(List<TaskResource> resources, ProjectBeneficiary projectBeneficiary, String serviceDeliveryId) {

        List<ResourceDTO> benefitsDelivered = new ArrayList<ResourceDTO>() ;
        for (TaskResource resource : resources) {

            ResourceDTO resourceToSend = ResourceDTO.builder()
                    .productVariantId(resource.getProductVariantId())
                    .quantity( Integer.parseInt(resource.getQuantity().toString()))
                    .isDelivered(resource.getIsDelivered())
                    .deliveryComment(resource.getDeliveryComment())
                    .deliveryDate(convertToISO8601(resource.getAuditDetails().getCreatedTime()))
                    .deliveredBy(resource.getAuditDetails().getCreatedBy())
                    .name("Have to fetch it from the product API").build();
            benefitsDelivered.add(resourceToSend);
        }
        BenificiaryDTO benificiaryDTO = BenificiaryDTO.builder()
                .beneficiaryId(projectBeneficiary.getId())
                .beneficiaryType("HOUSEHOLD")
                .projectId(projectBeneficiary.getProjectId())
                .tenantId(projectBeneficiary.getTenantId())
                .registrationDate(convertToISO8601(projectBeneficiary.getDateOfRegistration()))
                .build();

        return new RegistryRequest(serviceDeliveryId, benificiaryDTO, benefitsDelivered);
    }

    private void setIndividuals(List<String> individualClientReferenceIds,
                                String tenantId, Map<String, Individual> individualMap) {
        List<Individual> individuals = individualService
                .searchIndividuals(individualClientReferenceIds, tenantId);

        for (Individual individual : individuals) {
            individualMap.put(individual.getClientReferenceId(), individual);
        }
    }

    private void setHouseholdHeads(List<String> householdClientReferenceIds,
                                   String tenantId, List<String> individualClientReferenceIds,
                                   Map<String, HouseholdMember> hosueholdHeadMap) {
        for (String householdClientReferenceId : householdClientReferenceIds) {
            List<HouseholdMember> members = householdService
                    .searchHouseholdMembers(householdClientReferenceId, tenantId, true);
            if (!CollectionUtils.isEmpty(members)) {
                HouseholdMember member = members.get(0);
                individualClientReferenceIds.add(member.getIndividualClientReferenceId());
                hosueholdHeadMap.put(member.getHouseholdClientReferenceId(), member);
            }
        }
    }

    private void setHouseholds(List<String> projectBeneficiaryClientReferenceIds,
                               String tenantId, List<String> householdClientReferenceIds,
                               Map<String, Household> householdMap) {
        List<Household> households = householdService
                .searchHouseholds(projectBeneficiaryClientReferenceIds, tenantId);
        for (Household household: households) {
            householdClientReferenceIds.add(household.getClientReferenceId());
            householdMap.put(household.getClientReferenceId(), household);
        }
    }

    private void setProjectBeneficiaries(List<String> projectBeneficiaryClientReferenceIds,
                                         String tenantId, Map<String, ProjectBeneficiary> projectBeneficiaryMap) {
        List<ProjectBeneficiary> beneficiaries = projectService
                .searchBeneficiaries(projectBeneficiaryClientReferenceIds, tenantId);
        for (ProjectBeneficiary projectBeneficiary : beneficiaries) {
            projectBeneficiaryClientReferenceIds.add(projectBeneficiary.getBeneficiaryClientReferenceId());
            projectBeneficiaryMap.put(projectBeneficiary.getClientReferenceId(), projectBeneficiary);
        }
    }
}

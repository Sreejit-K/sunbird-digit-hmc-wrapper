package org.egov.sunbird.models;

import org.springframework.beans.factory.annotation.Value;

import java.util.List;

public class RegistryRequest {
    @Value("")
    private String serviceDeliveryId;
    private BenificiaryDTO beneficiary;
    private List<ResourceDTO> benefitsDelivered;

    // Constructors, getters, and setters

    public RegistryRequest() {
    }

    public RegistryRequest(String serviceDeliveryId, BenificiaryDTO beneficiary, List<ResourceDTO> benefitsDelivered) {
        this.serviceDeliveryId = serviceDeliveryId;
        this.beneficiary = beneficiary;
        this.benefitsDelivered = benefitsDelivered;
    }

    public String getServiceDeliveryId() {
        return serviceDeliveryId;
    }

    public void setServiceDeliveryId(String serviceDeliveryId) {
        this.serviceDeliveryId = serviceDeliveryId;
    }

    public BenificiaryDTO getBeneficiary() {
        return beneficiary;
    }

    public void setBeneficiary(BenificiaryDTO beneficiary) {
        this.beneficiary = beneficiary;
    }

    public List<ResourceDTO> getBenefitsDelivered() {
        return benefitsDelivered;
    }

    public void setBenefitsDelivered(List<ResourceDTO> benefitsDelivered) {
        this.benefitsDelivered = benefitsDelivered;
    }
}

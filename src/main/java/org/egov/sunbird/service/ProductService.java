package org.egov.sunbird.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.util.Strings;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.common.http.client.ServiceRequestClient;
import org.egov.common.models.product.ProductVariant;
import org.egov.common.models.product.ProductVariantResponse;
import org.egov.common.models.product.ProductVariantSearch;
import org.egov.common.models.product.ProductVariantSearchRequest;
import org.egov.sunbird.config.SunbirdProperties;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.egov.sunbird.Constants.PRODUCT_VARIANT_FETCH_ERROR;
import static org.egov.sunbird.Constants.PRODUCT_VARIANT_FETCH_ERROR_MESSAGE;

@Component
@Slf4j
public class ProductService {

    private final SunbirdProperties sunbirdProperties;

    private final ServiceRequestClient serviceRequestClient;

    public ProductService(SunbirdProperties sunbirdProperties, ServiceRequestClient serviceRequestClient) {
        this.sunbirdProperties = sunbirdProperties;
        this.serviceRequestClient = serviceRequestClient;
    }

    public List<ProductVariant> searchProductVariant (List<String> productVariantRefIds, String tenantId) {
        ProductVariantSearchRequest request = ProductVariantSearchRequest.builder()
                .requestInfo(RequestInfo.builder().
                        userInfo(User.builder()
                                .uuid("transformer-uuid")
                                .build())
                        .build())
                .productVariant(ProductVariantSearch.builder().id(productVariantRefIds).build())
                .build();
        ProductVariantResponse response;
        try {
            StringBuilder uri = new StringBuilder();
            uri.append(sunbirdProperties.getProjectHost())
                    .append(sunbirdProperties.getProjectBeneficiarySearchUrl())
                    .append("?limit=").append(sunbirdProperties.getSearchApiLimit())
                    .append("&offset=0")
                    .append("&tenantId=").append(tenantId);
            response = serviceRequestClient.fetchResult(uri,
                    request,
                    ProductVariantResponse.class);
        } catch (Exception e) {
            log.error("error while fetching beneficiary {}", ExceptionUtils.getStackTrace(e));
            throw new CustomException(PRODUCT_VARIANT_FETCH_ERROR ,
                    PRODUCT_VARIANT_FETCH_ERROR_MESSAGE + Strings.join(productVariantRefIds, ','));
        }
        return response.getProductVariant();
    }


}

package org.egov.sunbird.models;

import lombok.*;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ResourceDTO {
    @Value("")
    private String productVariantId;
    @Value("")
    private int quantity;
    @Value("")
    private boolean isDelivered;
    @Value("")
    private String deliveryComment;
    @Value("")
    private String deliveryDate;
    @Value("")
    private String deliveredBy;
    @Value("")
    private String name;

}


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
    private String productVariantId;
    private int quantity;
    private boolean isDelivered;
    @Value("")
    private String deliveryComment;
    private String deliveryDate;
    private String deliveredBy;
    private String name;

}


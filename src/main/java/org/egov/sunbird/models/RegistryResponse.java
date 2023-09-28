package org.egov.sunbird.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class RegistryResponse {

    @JsonProperty("id")
    private String id;
    @JsonProperty("ver")
    private String ver;
    @JsonProperty("ets")
    private long ets;
    @JsonProperty("params")
    private Params params;
    @JsonProperty("responseCode")
    private String responseCode;
    @JsonProperty("result")
    private Result result;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Getter
    @Setter
    public static class Params {

        @JsonProperty("resmsgid")
        private String resmsgid;

        @JsonProperty("msgid")
        private String msgid;

        @JsonProperty("result")
        private String err;

        @JsonProperty("status")
        private String status;

        @JsonProperty("errmsg")
        private String errmsg;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Getter
    @Setter
    public static class Result {

        @JsonProperty("ServiceDelivery")
        private ServiceDelivery ServiceDelivery;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        @Getter
        @Setter
        public static class ServiceDelivery {

            @JsonProperty("osid")
            private String osid;
        }
    }
}

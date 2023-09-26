CREATE TABLE eg_vc_service_delivery
(
    id                                  character varying(1000),
    certificateId                       character varying(1000),
    serviceTaskId                       character varying(1000),
    beneficiaryId                       character varying(1000),
    distributedBy                       character varying(1000),
    createdBy                           character varying(64),
    lastModifiedBy                      character varying(64),
    createdTime                         bigint,
    lastModifiedTime                    bigint,
    CONSTRAINT uk_eg_vc_service_delivery_id PRIMARY KEY (id)
);
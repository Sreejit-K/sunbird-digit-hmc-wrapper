package org.egov.sunbird.web.controllers;


import io.swagger.annotations.ApiParam;
import org.egov.common.contract.response.ResponseInfo;
import org.egov.common.models.project.TaskBulkRequest;
import org.egov.common.producer.Producer;
import org.egov.common.utils.ResponseInfoFactory;
import org.egov.sunbird.config.SunbirdProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2022-12-14T20:57:07.075+05:30")
@Controller
@RequestMapping("")
@Validated
public class DummyController {

    private final Producer producer;

    private final SunbirdProperties sunbirdProperties;

    @Autowired
    public DummyController(Producer producer, SunbirdProperties sunbirdProperties) {
        this.producer = producer;
        this.sunbirdProperties = sunbirdProperties;
    }



    @RequestMapping(value = "/task/v1/bulk/_create", method = RequestMethod.POST)
    public ResponseEntity<ResponseInfo> projectTaskBulkV1CreatePost(@ApiParam(value = "Capture details of Task", required = true) @Valid @RequestBody TaskBulkRequest request) {
        producer.push(sunbirdProperties.getSaveProjectTaskTopic(), request.getTasks());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(ResponseInfoFactory
                .createResponseInfo(request.getRequestInfo(), true));
    }

    @RequestMapping(value = "/task/v1/bulk/_update", method = RequestMethod.POST)
    public ResponseEntity<ResponseInfo> projectTaskV1BulkUpdatePost(@ApiParam(value = "Capture details of Existing task", required = true) @Valid @RequestBody TaskBulkRequest request) {
        producer.push(sunbirdProperties.getUpdateProjectTaskTopic(), request.getTasks());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(ResponseInfoFactory
                .createResponseInfo(request.getRequestInfo(), true));
    }
}

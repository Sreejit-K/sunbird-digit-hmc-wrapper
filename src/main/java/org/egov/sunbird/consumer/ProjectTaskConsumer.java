package org.egov.sunbird.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.egov.common.models.project.Task;
import org.egov.sunbird.service.ProjectTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class ProjectTaskConsumer {

    private final ProjectTaskService projectTaskService;

    private final ObjectMapper objectMapper;

    @Autowired
    public ProjectTaskConsumer(ProjectTaskService projectTaskService,
                               @Qualifier("objectMapper") ObjectMapper objectMapper) {
        this.projectTaskService = projectTaskService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = { "${transformer.consumer.bulk.create.project.task.topic}"})
    public void consumeCreateTask(ConsumerRecord<String, Object> payload,
                           @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
            List<Task> payloadList = Arrays.asList(objectMapper
                    .readValue((String) payload.value(),
                            Task[].class));
            projectTaskService.transform(payloadList, true);
        } catch (Exception exception) {
            log.error("error in project task bulk consumer {}", ExceptionUtils.getStackTrace(exception));
        }
    }

    @KafkaListener(topics = { "${transformer.consumer.bulk.create.project.task.topic}"})
    public void consumeUpdateTask(ConsumerRecord<String, Object> payload,
                            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
            List<Task> payloadList = Arrays.asList(objectMapper
                    .readValue((String) payload.value(),
                            Task[].class));
            projectTaskService.transform(payloadList, false);
        } catch (Exception exception) {
            log.error("error in project task bulk consumer {}", ExceptionUtils.getStackTrace(exception));
        }
    }
}

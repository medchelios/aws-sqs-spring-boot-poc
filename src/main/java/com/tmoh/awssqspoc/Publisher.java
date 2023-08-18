package com.tmoh.awssqspoc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.jackson.JsonFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
@Slf4j
public class Publisher {

    @Autowired
    private QueueMessagingTemplate queueMessagingTemplate;

    @Value("${cloud.aws.end-point.uri}")
    private String endpoint;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Scheduled(fixedRate = 1000)
    public void scheduleFixedRateTask() throws JsonProcessingException {
        log.info("Producing CloudEvent for endpoint: " + endpoint);
        MyCloudEventData data = new MyCloudEventData();
        data.setMyData("Hello from Java");
        data.setMyCounter(1);

        CloudEventBuilder cloudEventBuilder = CloudEventBuilder.v1()
                .withId(UUID.randomUUID().toString())
                .withType("app-a.MyCloudEvent")
                .withSource(URI.create("application-a"))
                .withDataContentType("application/json; charset=UTF-8")
                .withData(objectMapper.writeValueAsString(data).getBytes(StandardCharsets.UTF_8));

        CloudEvent cloudEvent = cloudEventBuilder.build();

        logCloudEvent(cloudEvent);

        log.info("Producing CloudEvent with MyCloudEventData: " + data);
        //queueMessagingTemplate.send(endpoint, MessageBuilder.withPayload("Niraj").build());
        queueMessagingTemplate.convertAndSend(endpoint, new MyCloudEventData(data.getMyData(), data.getMyCounter()));
    }

    private void logCloudEvent(CloudEvent cloudEvent) {
        EventFormat format = EventFormatProvider
                .getInstance()
                .resolveFormat(JsonFormat.CONTENT_TYPE);

        log.info("Cloud Event: " + new String(format.serialize(cloudEvent)));

    }
}

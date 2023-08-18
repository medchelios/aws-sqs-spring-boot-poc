package com.tmoh.awssqspoc;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;

@Component
@Slf4j
public class Consumer {

    private ObjectMapper objectMapper = new ObjectMapper();
    private CloudEnventUtils cloudEnventUtils;

    @SqsListener(value = "spring-boot-poc",deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void processMessage(@RequestBody CloudEvent cloudEvent) throws IOException {
        cloudEnventUtils.logCloudEvent(cloudEvent);
        if (!cloudEvent.getType().equals("app-b.MyCloudEvent")) {
            throw new IllegalStateException("Wrong Cloud Event Type, expected: 'app-b.MyCloudEvent' and got: " + cloudEvent.getType());
        }

        MyCloudEventData data = objectMapper.readValue(cloudEvent.getData().toBytes(), MyCloudEventData.class);
        log.info("MyCloudEventData Data: " + data.getMyData());
        log.info("MyCloudEventData Counter: " + data.getMyCounter());
        log.info("Message from SQS {}", cloudEvent);
    }
}

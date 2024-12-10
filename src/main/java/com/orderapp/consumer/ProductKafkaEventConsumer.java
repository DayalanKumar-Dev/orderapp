package com.orderapp.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProductKafkaEventConsumer {

    @KafkaListener(topics = {"producer-events"}, groupId = "product-events-listener-group")
    public void retrieveProductEvent(ConsumerRecord<Integer, String> consumerRecord){
        log.info("inside kafka consumer=========");
        log.info("consumerrecord : {}", consumerRecord);
    }
}

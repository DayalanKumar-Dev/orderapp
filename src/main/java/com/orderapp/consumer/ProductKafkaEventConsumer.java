package com.orderapp.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.orderapp.service.ProductEventService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProductKafkaEventConsumer {

    @Autowired
    ProductEventService productEventService;

    @KafkaListener(topics = {"producer-events"}, groupId = "product-events-listener-group")
    public void retrieveProductEvent(ConsumerRecord<Integer, String> consumerRecord) throws JsonProcessingException {
        log.info("inside kafka consumer=========");
        log.info("consumerrecord : {}", consumerRecord);
        productEventService.processProductEvent(consumerRecord);
    }
}

package com.orderapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderapp.dao.ProductEventRepository;
import com.orderapp.event.ProductEvent;
import com.orderapp.event.ProductEventType;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class ProductEventService {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ProductEventRepository productEventRepository;

    public void processProductEvent(ConsumerRecord<Integer, String> consumerRecord) throws JsonProcessingException {
        ProductEvent productEvent = objectMapper.readValue(consumerRecord.value(), ProductEvent.class);
        log.info("event type==========={}",productEvent.getProductEventType());
        switch (productEvent.getProductEventType()){
            case NEW :  save(productEvent);
            case UPDATE: {
                validate(productEvent);
                save(productEvent);
            }

            default: log.info("Invalid product event type");

        }
    }

    private void validate(ProductEvent productEvent){
        if(productEvent.getProductEventId() == null){
            throw new IllegalArgumentException("Product Id is missing");
        }
       Optional<ProductEvent> productEventOptional = productEventRepository.findById(productEvent.getProductEventId());
        if(!productEventOptional.isPresent()){
            throw new IllegalArgumentException("Not a valid product event");
        }/*else{
            productEvent.setProductEventType(ProductEventType.UPDATE);
        }*/
       // productEvent.getProduct().setProductEvent(productEvent);
       // productEventRepository.save(productEvent);
        log.info("validation is successful for product event==={}", productEventOptional.get());
    }

    private void save(ProductEvent productEvent){
        log.info("save method called for {}",productEvent.getProductEventType());
        productEvent.getProduct().setProductEvent(productEvent);
        productEventRepository.save(productEvent);
        log.info("sucessfully saved the product event======={}", productEvent);

    }

}

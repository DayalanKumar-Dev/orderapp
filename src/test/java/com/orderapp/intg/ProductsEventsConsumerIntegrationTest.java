package com.orderapp.intg;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderapp.consumer.ProducerEventsConsumerManualOffset;
import com.orderapp.consumer.ProductKafkaEventConsumer;
import com.orderapp.dao.ProductEventRepository;
import com.orderapp.event.ProductEvent;
import com.orderapp.event.ProductEventType;
import com.orderapp.model.Product;
import com.orderapp.service.ProductEventService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@EmbeddedKafka(topics = {"producer-events"}, partitions = 3)
@TestPropertySource(properties = {"spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
"spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}",
"retryListener.startup=false"})
public class ProductsEventsConsumerIntegrationTest {

    @Autowired
    EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    KafkaTemplate kafkaTemplate;

    @Autowired
    KafkaListenerEndpointRegistry endpointRegistry;

    @SpyBean
    ProductKafkaEventConsumer productKafkaEventConsumerSpy;

    @SpyBean
    ProducerEventsConsumerManualOffset producerEventsConsumerManualOffsetSpy;

    @SpyBean
    ProductEventService productEventServiceSpy;

    @Autowired
    ProductEventRepository productEventRepository;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp(){
        var containers = endpointRegistry.getListenerContainers()
                .stream().filter(messageListenerContainer ->
                Objects.equals(messageListenerContainer.getGroupId(), "product-events-listener-group"))
                .collect(Collectors.toList()).get(0);
        ContainerTestUtils.waitForAssignment(containers, embeddedKafkaBroker.getPartitionsPerTopic());
    }

    @Test
    void publishNewProductEvent() throws ExecutionException, InterruptedException, JsonProcessingException {
        String json = "{\"productEventId\":null,\"productEventType\":\"NEW\",\"product\":{\"productId\":1,\"productName\":\"Soap\",\"productManufacturerName\":\"hamam\"}}";
        kafkaTemplate.sendDefault(json).get();
        //when
        CountDownLatch latch = new CountDownLatch(1);
        latch.await(3, TimeUnit.SECONDS);

        //then
    //    verify(productKafkaEventConsumerSpy, times(1))
       //         .retrieveProductEvent(isA(ConsumerRecord.class));

      /*  verify(producerEventsConsumerManualOffsetSpy, times(1))
                .onMessage(isA(ConsumerRecord.class));*/


    List<ProductEvent> productEventList  = (List<ProductEvent>) productEventRepository.findAll();

 //   assert productEventList.size()==1;
    productEventList.forEach(productEvent -> {
      //  assert productEvent.getProductEventId()!=null;
        assertEquals(1, productEvent.getProduct().getProductId());

    });
    }

    @Test
    void publishUpdateProductEvent() throws JsonProcessingException, ExecutionException, InterruptedException {
        String json = "{\"productEventId\":null,\"productEventType\":\"NEW\",\"product\":{\"productId\":1,\"productName\":\"Soap\",\"productManufacturerName\":\"hamam\"}}";

        ProductEvent event = objectMapper.readValue(json, ProductEvent.class);
        event.getProduct().setProductEvent(event);
        productEventRepository.save(event);

        Product product = Product.builder()
                .productId(1).productName("soap").productManufacturerName("hamam")
                .build();
        event.setProductEventType(ProductEventType.UPDATE);
        event.setProduct(product);
        String updatedJson = objectMapper.writeValueAsString(event);
        kafkaTemplate.sendDefault(event.getProductEventId(), updatedJson).get();

        // when
        CountDownLatch latch = new CountDownLatch(1);
        latch.await(3, TimeUnit.SECONDS);

       ProductEvent persistedProductEvent = productEventRepository.findById(event.getProductEventId()).get();
       assertEquals("soap", persistedProductEvent.getProduct().getProductName());
    }

}

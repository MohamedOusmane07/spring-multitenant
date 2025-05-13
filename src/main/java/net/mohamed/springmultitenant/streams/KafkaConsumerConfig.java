package net.mohamed.springmultitenant.streams;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import net.mohamed.springmultitenant.dto.InvoiceDto;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Configuration
@Slf4j
public class KafkaConsumerConfig {
  @Bean
  public <T> ConsumerFactory<String, T> genericConsumerFactory() {
    JsonDeserializer<T> jsonDeserializer = new JsonDeserializer<>();
    jsonDeserializer.addTrustedPackages("*");
    jsonDeserializer.setRemoveTypeHeaders(false);
    jsonDeserializer.setUseTypeMapperForKey(false);
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9093");
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
    props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class.getName());
    props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class.getName());
    props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "net.mohamed.springmultitenant.dto.InvoiceDto");


    return new DefaultKafkaConsumerFactory<>(props);
  }
  @Bean
  public <T> ConcurrentKafkaListenerContainerFactory<String, T> genericKafkaListenerContainerFactory(
          ConsumerFactory<String, T> consumerFactory) {

    ConcurrentKafkaListenerContainerFactory<String, T> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory);
    factory.setConcurrency(3);
    factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.BATCH);

    factory.setCommonErrorHandler(new DefaultErrorHandler(
            (record, exception) -> {
              // Logique pour gérer les erreurs, comme rediriger vers une DLQ
              log.error("Erreur de désérialisation pour le record : {}", record, exception);
            }
    ));


    return factory;
  }
}

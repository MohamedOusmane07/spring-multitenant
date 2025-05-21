package net.mohamed.springmultitenant.streams.clusters;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
@Slf4j
public class KafkaTenantFactory {

  public KafkaTenantFactory(TenantUtils tenantUtils) {
    this.tenantUtils = tenantUtils;
  }

  private final TenantUtils tenantUtils;

  private final Map<String, KafkaTemplate<String, Object>> templates = new ConcurrentHashMap<>();
  private final Map<String, ConsumerFactory<String, Object>> consumerFactories =
      new ConcurrentHashMap<>();

  public KafkaTemplate<String, Object> getTemplateForTenant(String tenantId) {
    return templates.computeIfAbsent(tenantId, this::createKafkaTemplate);
  }

  public ConsumerFactory<String, Object> getConsumerFactoryForTenant(String tenantId) {
    return consumerFactories.computeIfAbsent(tenantId, this::createConsumerFactory);
  }

  private KafkaTemplate<String, Object> createKafkaTemplate(String tenantId) {

    String bootstrapServer = getBootstrapServer(tenantId);

    Map<String, Object> props = new ConcurrentHashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
    log.info("Kafka template for tenant [{}]: {}", tenantId, props);

    return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(props));
  }

  private ConsumerFactory<String, Object> createConsumerFactory(String tenantId) {
    String bootstrapServer = getBootstrapServer(tenantId);

    Map<String, Object> props = new ConcurrentHashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
    props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
    props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "net.mohamed.springmultitenant.dto.InvoiceDto");
    log.info("Kafka consumer factory for tenant [{}]: {}", tenantId, props);

    return new DefaultKafkaConsumerFactory<>(props);
  }

  private String getBootstrapServer(String tenantId) {
    ConcurrentHashMap<String, String> clusters = tenantUtils.getCusters();
    if (clusters == null || !clusters.containsKey(tenantId)) {
      log.error("No cluster configuration found for tenant [{}]", tenantId);
      throw new IllegalStateException("No cluster configuration found for tenant ID: " + tenantId);
    }
    return clusters.get(tenantId);
  }

  public void removeTenantRessources(String tenantId) {
    KafkaTemplate<String, Object> template = templates.remove(tenantId);
    if (template != null) {
      template.destroy();
      log.info("Removed Kafka template for tenant [{}]", tenantId);
    }
  }
}

package net.mohamed.springmultitenant.streams.clusters;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.converter.MessagingMessageConverter;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
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
    props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, true);
    log.info("Kafka template for tenant [{}]: {}", tenantId, props);
    DefaultKafkaProducerFactory<String, Object> factory = new DefaultKafkaProducerFactory<>(props);
    KafkaTemplate<String, Object> kafkaTemplate = new KafkaTemplate<>(factory);
    kafkaTemplate.setMessageConverter(new MessagingMessageConverter());
    return kafkaTemplate;

    // return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(props));
  }

  private ConsumerFactory<String, Object> createConsumerFactory(String tenantId) {
    String bootstrapServer = getBootstrapServer(tenantId);

    Map<String, Object> props = new ConcurrentHashMap<>();

    // Utilisation du ErrorHandlingDeserializer
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
    props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);

    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
    props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);

    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
    props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
    props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, true);
    log.info("Kafka consumer factory for tenant [{}]: {}", tenantId, props);

    return new DefaultKafkaConsumerFactory<>(props);
  }

  public void createTopicIfNotExists(
      String topicName, int partitions, short replicationFactor, String bootstrapServers) {
    Map<String, Object> configs =
        Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

    try (AdminClient adminClient = AdminClient.create(configs)) {

      // Vérifie si le topic existe
      Set<String> existingTopics = adminClient.listTopics().names().get();
      if (existingTopics.contains(topicName)) {
        Map<String, TopicDescription> descriptions =
            adminClient.describeTopics(List.of(topicName)).all().get();

        log.info("Topic [{}] already exists, skipping creation.", topicName);

        topicDescription(topicName, descriptions);
        return;
      }

      // Crée le topic
      NewTopic topic = new NewTopic(topicName, partitions, replicationFactor);
      adminClient.createTopics(Collections.singletonList(topic)).all().get();

      // Vérifie la création du topic
      DescribeTopicsResult result = adminClient.describeTopics(List.of(topicName));
      Map<String, TopicDescription> descriptions = result.all().get();
      topicDescription(topicName, descriptions);

    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException("Thread interrupted while creating topic: " + topicName, e);
    } catch (ExecutionException e) {
      throw new RuntimeException("Error creating topic: " + topicName, e);
    }
  }

  public void describeTopic(String topicName, String bootstrapServers) {
    try (AdminClient adminClient =
        AdminClient.create(Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers))) {

      DescribeTopicsResult result = adminClient.describeTopics(List.of(topicName));
      Map<String, TopicDescription> descriptions = result.all().get();

      TopicDescription topicDescription = descriptions.get(topicName);
      System.out.println("Topic: " + topicDescription.name());
      System.out.println("Partitions: " + topicDescription.partitions().size());

      topicDescription
          .partitions()
          .forEach(
              partition -> {
                System.out.println("Partition: " + partition.partition());
                System.out.println("Leader: " + partition.leader());
                System.out.println("Replicas: " + partition.replicas());
                System.out.println("ISR: " + partition.isr());
              });

    } catch (Exception e) {
      throw new RuntimeException("Failed to describe topic: " + topicName, e);
    }
  }

  private void topicDescription(String topicName, Map<String, TopicDescription> descriptions) {

    TopicDescription topicDescription = descriptions.get(topicName);
    log.info("Topic: " + topicDescription.name());
    int partitionsCount = topicDescription.partitions().size();
    log.info("Partitions: " + partitionsCount);

    /*  topicDescription.partitions().forEach(partition -> {
       System.out.println("Partition: " + partition.partition());
       System.out.println("Leader: " + partition.leader());
       System.out.println("Replicas: " + partition.replicas());
       System.out.println("ISR: " + partition.isr());
     });

    */

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

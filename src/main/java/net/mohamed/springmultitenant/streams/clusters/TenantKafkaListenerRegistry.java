package net.mohamed.springmultitenant.streams.clusters;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.mohamed.springmultitenant.dto.InvoiceDto;
import net.mohamed.springmultitenant.dto.ProvisionDto;
import net.mohamed.springmultitenant.tenant.TenantContext;
import net.mohamed.springmultitenant.tenant.config.DataSourceConfig;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.listener.*;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TenantKafkaListenerRegistry {

  private final KafkaTenantFactory kafkaTenantFactory;
  private final DataSourceConfig dataSourceConfig;
  private final TenantUtils tenantUtils;
  private final Map<String, List<MessageListenerContainer>> tenantContainers =
      new ConcurrentHashMap<>();

  @EventListener(ApplicationReadyEvent.class)
  public void startListener() {
    List<String> tenantIds = getTenantIds();
    log.info("Starting Kafka listeners for all tenants: {}", tenantIds);
    for (String tenantId : tenantIds) {
      createTenantTopics(tenantId);
      invoiceListeners(tenantId);
      provisionListeners(tenantId);
      strListeners(tenantId);
    }
  }

  public void startListenerForTenant(String tenantId) {
    log.info("Starting Kafka listeners for tenant: {}", tenantId);
    invoiceListeners(tenantId);
    provisionListeners(tenantId);
  }

  public void invoiceListeners(String tenantId) {
    log.info("Starting Kafka startlisteners for {}", tenantId);

    ContainerProperties containerProps = new ContainerProperties("invoice-" + tenantId);
    containerProps.setGroupId("group-" + tenantId);

    Map<String, Object> invoicePropsMapOriginal =
        kafkaTenantFactory.getConsumerFactoryForTenant(tenantId).getConfigurationProperties();
    log.info("Kafka base properties for tenant [{}]: {}", tenantId, invoicePropsMapOriginal);

    Map<String, Object> invoicePropsMap = new HashMap<>(invoicePropsMapOriginal);

    Properties invoiceProps = new Properties();

    invoicePropsMap.forEach((key, value) -> invoiceProps.put(key, value));
    invoiceProps.forEach((k, v) -> log.info("Kafka property [{}] = [{}]", k, v));

    containerProps.setKafkaConsumerProperties(invoiceProps);
    log.info("Kafka properties for tenant [{}]: {}", tenantId, invoiceProps);

    containerProps.setMessageListener(
        (ConsumerAwareMessageListener<String, InvoiceDto>)
            (record, consumer) -> {
              TenantContext.setCurrentTenant(tenantId);

              Message<InvoiceDto> message =
                  MessageBuilder.withPayload(record.value())
                      .copyHeaders(
                          Arrays.stream(record.headers().toArray())
                              .collect(
                                  Collectors.toMap(
                                      Header::key,
                                      header ->
                                          new String(header.value(), StandardCharsets.UTF_8))))
                      .setHeader("tenantId", tenantId)
                      .build();
              String correlationdFromMessage =
                  message.getHeaders().get("correlationId", String.class);
              log.info("CorrelationId from message: {}", correlationdFromMessage);

              try {
                String receivedTenant = new String(record.headers().lastHeader("tenantId").value());
                String correlationId =
                    new String(
                        record.headers().lastHeader("correlationId").value(),
                        StandardCharsets.UTF_8);

                log.info(
                    "Header tenantId : {} & correlationId : {}", receivedTenant, correlationId);
                log.info("Received invoice: {}", record.value());
              } catch (Exception e) {
                log.error("Error processing invoice for [{}]: {}", tenantId, e.getMessage());
              } finally {
                TenantContext.clearCurrentTenant();
              }
            });

    ConcurrentMessageListenerContainer<String, InvoiceDto> container =
        new ConcurrentMessageListenerContainer<>(
            kafkaTenantFactory.getConsumerFactoryForTenant(tenantId), containerProps);
    container.setConcurrency(1);

    container.start();
  }

  public void provisionListeners(String tenantId) {
    log.info("Starting Kafka provisions Listeners for {}", tenantId);

    ContainerProperties containerProps = new ContainerProperties("prov-" + tenantId);
    containerProps.setGroupId("group-" + tenantId);

    containerProps.setMessageListener(
        (ConsumerAwareMessageListener<String, ProvisionDto>)
            (record, consumer) -> {
              TenantContext.setCurrentTenant(tenantId);
              try {
                Headers headers = record.headers();
                Header tenantHeader = headers.lastHeader("tenantId");
                String receivedTenantId =
                    tenantHeader != null
                        ? new String(tenantHeader.value(), StandardCharsets.UTF_8)
                        : "unknown";

                log.info("Header tenantId: {}", receivedTenantId);
                log.info("Received provision payload: {}", record.value()); //  InvoiceDto
              } catch (Exception e) {
                log.error("Error processing invoice for [{}]: {}", tenantId, e.getMessage());
              } finally {
                TenantContext.clearCurrentTenant();
              }
            });

    KafkaMessageListenerContainer<String, ProvisionDto> container =
        new KafkaMessageListenerContainer<>(
            kafkaTenantFactory.getConsumerFactoryForTenant(tenantId), containerProps);
    container.start();
  }

  ///

  public void strListeners(String tenantId) {
    log.info("Starting Kafka str Listeners for {}", tenantId);

    ContainerProperties containerProps = new ContainerProperties("str-" + tenantId);
    containerProps.setGroupId("group-" + tenantId);

    containerProps.setMessageListener(
        (ConsumerAwareMessageListener<String, String>)
            (record, consumer) -> {
              TenantContext.setCurrentTenant(tenantId);
              try {
                Headers headers = record.headers();
                Header tenantHeader = headers.lastHeader("tenantId");
                String receivedTenantId =
                    tenantHeader != null
                        ? new String(tenantHeader.value(), StandardCharsets.UTF_8)
                        : "unknown";

                log.info("Header tenantId: {}", receivedTenantId);
                log.info("Received str payload: {}", record.value()); // ✅ InvoiceDto
              } catch (Exception e) {
                log.error("Error processing str for [{}]: {}", tenantId, e.getMessage());
              } finally {
                TenantContext.clearCurrentTenant();
              }
            });

    KafkaMessageListenerContainer<String, String> container =
        new KafkaMessageListenerContainer<>(
            kafkaTenantFactory.getConsumerFactoryForTenant(tenantId), containerProps);
    container.start();
  }

  private void createTenantTopics(String tenantId) {
    String bootstrapServers = tenantUtils.getBootstrapServer(tenantId);

    kafkaTenantFactory.createTopicIfNotExists(
        "invoice-" + tenantId, 2, (short) 1, bootstrapServers);
    kafkaTenantFactory.createTopicIfNotExists("prov-" + tenantId, 3, (short) 1, bootstrapServers);
    kafkaTenantFactory.createTopicIfNotExists("str-" + tenantId, 3, (short) 1, bootstrapServers);
  }

  ///

  private List<String> getTenantIds() {
    List<String> tenantIds =
        dataSourceConfig.getAllTenants().stream().map(tenant -> tenant.getTenantId()).toList();
    return tenantIds;
  }
}

/*
// Si on veut ajouter des propriétés spécifiques au consommateur
    // invoicePropsMap.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 20000);
    // invoicePropsMap.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 5000);
    invoicePropsMap.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

 */

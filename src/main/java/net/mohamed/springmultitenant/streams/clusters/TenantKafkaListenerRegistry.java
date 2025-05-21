package net.mohamed.springmultitenant.streams.clusters;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.mohamed.springmultitenant.dto.InvoiceDto;
import net.mohamed.springmultitenant.tenant.TenantContext;
import net.mohamed.springmultitenant.tenant.config.DataSourceConfig;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.listener.*;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TenantKafkaListenerRegistry {

  private final KafkaTenantFactory kafkaTenantFactory;
  private final DataSourceConfig dataSourceConfig;
  private final Map<String, List<MessageListenerContainer>> tenantContainers =
      new ConcurrentHashMap<>();

  @EventListener(ApplicationReadyEvent.class)
  public void startListener() {
    List<String> tenantIds = getTenantIds();
    log.info("Starting Kafka listeners for all tenants: {}", tenantIds);
    for (String tenantId : tenantIds) {
      invoiceListeners(tenantId);
      provisionListeners(tenantId);
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

    // Si on veut ajouter des propriétés spécifiques au consommateur
    // invoicePropsMap.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 20000);
    // invoicePropsMap.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 5000);

    Properties invoiceProps = new Properties();

    invoicePropsMap.forEach((key, value) -> invoiceProps.put(key, value));
    invoiceProps.forEach((k, v) -> log.info("Kafka property [{}] = [{}]", k, v));

    containerProps.setKafkaConsumerProperties(invoiceProps);
    log.info("Kafka properties for tenant [{}]: {}", tenantId, invoiceProps);

    containerProps.setMessageListener(
        (MessageListener<String, InvoiceDto>)
            record -> {
              TenantContext.setCurrentTenant(tenantId);
              try {
                log.info("Processed invoice for tenant [{}]", tenantId);
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

    ContainerProperties containerProps = new ContainerProperties("provision-" + tenantId);
    containerProps.setGroupId("group-" + tenantId);

    containerProps.setMessageListener(
        (MessageListener<String, InvoiceDto>)
            record -> {
              TenantContext.setCurrentTenant(tenantId);

              try {
                log.info("Processed invoice for tenant [{}]", tenantId);
                log.info("Received provision: {}", record.value());
              } catch (Exception e) {
                log.error("Error processing invoice for [{}]: {}", tenantId, e.getMessage());
              } finally {
                TenantContext.clearCurrentTenant();
              }
            });

    KafkaMessageListenerContainer<String, InvoiceDto> container =
        new KafkaMessageListenerContainer<>(
            kafkaTenantFactory.getConsumerFactoryForTenant(tenantId), containerProps);
    container.start();
  }

  private List<String> getTenantIds() {
    List<String> tenantIds =
        dataSourceConfig.getAllTenants().stream().map(tenant -> tenant.getTenantId()).toList();
    return tenantIds;
  }
}

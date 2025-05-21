package net.mohamed.springmultitenant.streams.clusters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.mohamed.springmultitenant.dto.InvoiceDto;
import net.mohamed.springmultitenant.tenant.TenantContext;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaProducers {

  private final KafkaTenantFactory kafkaTenantFactory;

  public void sendInvoice(InvoiceDto dto) {
    String tenantId = TenantContext.getCurrentTenant();
    if (tenantId == null) {
      throw new IllegalStateException("Tenant ID is not set");
    }

    KafkaTemplate<String, Object> kafkaTemplate = kafkaTenantFactory.getTemplateForTenant(tenantId);
    String topic = "invoice-" + tenantId;
    kafkaTemplate.send(topic, dto);
    log.info("Sending invoice for tenant [{}] to topic [{}]: {}", tenantId, topic, dto);
  }

  public void sendProvision(InvoiceDto dto) {
    String tenantId = TenantContext.getCurrentTenant();
    if (tenantId == null) {
      throw new IllegalStateException("Tenant ID is not set");
    }

    KafkaTemplate<String, Object> kafkaTemplate = kafkaTenantFactory.getTemplateForTenant(tenantId);
    String topic = "provision-" + tenantId;
    kafkaTemplate.send(topic, dto);
    log.info("Sending provision for tenant [{}] to topic [{}]: {}", tenantId, topic, dto);
  }
}

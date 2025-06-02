package net.mohamed.springmultitenant.streams.clusters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.mohamed.springmultitenant.dto.InvoiceDto;
import net.mohamed.springmultitenant.dto.ProvisionDto;
import net.mohamed.springmultitenant.tenant.TenantContext;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaProducers {

  private final KafkaTenantFactory kafkaTenantFactory;

  public void sendInvoice(Message<InvoiceDto> dto, String tenantId) {

    log.info("Sending invoice message for tenant --- {}", TenantContext.getCurrentTenant());
    if (tenantId == null) {
      throw new IllegalStateException("Tenant ID is not set");
    }

    KafkaTemplate<String, Object> kafkaTemplate = kafkaTenantFactory.getTemplateForTenant(tenantId);

    String topic = "invoice-" + tenantId;
    kafkaTemplate.send(dto);
    log.info("Sending invoice for tenant [{}] to topic [{}]: {}", tenantId, topic, dto);
  }

  public void sendProvision(Message<ProvisionDto> dto) {
    String tenantId = TenantContext.getCurrentTenant();
    if (tenantId == null) {
      throw new IllegalStateException("Tenant ID is not set");
    }

    KafkaTemplate<String, Object> kafkaTemplate = kafkaTenantFactory.getTemplateForTenant(tenantId);
    String topic = "prov-" + tenantId;
    kafkaTemplate.send(dto);
    log.info("Sending provision for tenant [{}] to topic [{}]: {}", tenantId, topic, dto);
  }

  public void sendString(Message<String> message) {
    String tenantId = TenantContext.getCurrentTenant();
    if (tenantId == null) {
      throw new IllegalStateException("Tenant ID is not set");
    }

    KafkaTemplate<String, Object> kafkaTemplate = kafkaTenantFactory.getTemplateForTenant(tenantId);
    String topic = "str-" + tenantId;
    kafkaTemplate.send(message);
    log.info(
        "Sending message for tenant [{}] to topic [{}]: {}", tenantId, topic, message.getPayload());
  }
}

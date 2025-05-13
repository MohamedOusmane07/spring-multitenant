package net.mohamed.springmultitenant.streams;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.mohamed.springmultitenant.InvoiceService;
import net.mohamed.springmultitenant.dto.InvoiceDto;
import net.mohamed.springmultitenant.tenant.TenantContext;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class InvoiceKafkaListener {

  private final InvoiceService invoiceService;

  @KafkaListener(
      topicPattern =
          "invoice-.*", // 🔥 écoute tous les topics comme invoice-tenantA, invoice-tenantB...
      groupId = "invoice-group",
      containerFactory = "genericKafkaListenerContainerFactory")
  public void listenInvoices(
      @Payload InvoiceDto invoice,
      @Header(KafkaHeaders.RECEIVED_TOPIC) String topicName,
      @Headers Map<String, Object> headers) {

    String tenantId = extractTenantId(topicName, headers);
    if (tenantId == null) {
      log.error("Unable to determine tenant ID from topic [{}] or headers", topicName);
      return;
    }

    TenantContext.setCurrentTenant(tenantId);

    try {
      log.info("Received invoice for tenant [{}]: {}", tenantId, invoice);
      // invoiceService.createInvoice(invoice);
      log.info("Successfully invoice for tenant [{}]: {}", tenantId, invoice);
    } catch (Exception e) {
      log.error("Failed to process invoice for tenant [{}]: {}", tenantId, e.getMessage(), e);
      // TODO: Envoi vers une DLQ ou retry
    } finally {
      TenantContext.clearCurrentTenant();
    }
  }


    @KafkaListener(
        topicPattern = "invoice-.*",
        groupId = "invoice-group2",
        containerFactory = "genericKafkaListenerContainerFactory")
  public void SecondlistenInvoices(
      @Payload InvoiceDto invoice,
      @Header(KafkaHeaders.RECEIVED_TOPIC) String topicName,
      @Headers Map<String, Object> headers) {

    String tenantId = extractTenantId(topicName, headers);
    if (tenantId == null) {
      log.error("Unable to determine tenant ID from topic [{}] or headers", topicName);
      return;
    }

    TenantContext.setCurrentTenant(tenantId);

    try {
      log.info("Received second invoice for tenant [{}]: {}", tenantId, invoice);
      // invoiceService.createInvoice(invoice);
      log.info("Successfully second invoice for tenant [{}]: {}", tenantId, invoice);
    } catch (Exception e) {
      log.error("Failed to process invoice for tenant [{}]: {}", tenantId, e.getMessage(), e);
      // TODO: Envoi vers une DLQ ou retry
    } finally {
      TenantContext.clearCurrentTenant();
    }
  }

































  private String extractTenantId(String topicName, Map<String, Object> headers) {
    // 📌 Option 1 : Extraire depuis le topic (invoice-tenantX)
    if (topicName != null && topicName.startsWith("invoice-")) {
      return topicName.replace("invoice-", "");
    }

    // 📌 Option 2 : Fallback sur header (si envoyé par le producer)
    if (headers.containsKey("X-Tenant-ID")) {
      return (String) headers.get("X-Tenant-ID");
    }

    return null;
  }
}

package net.mohamed.springmultitenant.streams;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.mohamed.springmultitenant.dto.InvoiceDto;
import net.mohamed.springmultitenant.tenant.TenantContext;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaProducers {

  private final StreamBridge streamBridge;

  public void sendInvoice(InvoiceDto dto) {
    // Récupération du tenant courant
    String tenantId = TenantContext.getCurrentTenant();

    if (tenantId == null) {
      throw new IllegalStateException("Tenant ID is not set in context");
    }

    // Construction du nom de topic dynamique (ex: invoice-tenantA)
    String topicName = "invoice-" + tenantId;

    // Construction du message avec le header tenant
    Message<InvoiceDto> message =
        MessageBuilder.withPayload(dto)
            .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
            // .setHeader("X-Tenant-ID", tenantId) // Header custom
            .build();

    log.info("Sending invoice for tenant [{}] to topic [{}]: {}", tenantId, topicName, dto);

    boolean result = streamBridge.send(topicName, message);

    if (result) {
      log.info("Invoice sent successfully into topic [{}] for tenant [{}]", topicName, tenantId);
    } else {
      log.error("Failed to send invoice for tenant [{}]", tenantId);
    }
  }
}

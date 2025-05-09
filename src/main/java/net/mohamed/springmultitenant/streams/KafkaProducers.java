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
        String tenantId = TenantContext.getCurrentTenant();
        log.info("Current tenant ID: {}", tenantId);
        Message<InvoiceDto> message = MessageBuilder
                .withPayload(dto)
                .setHeader("tenantId", tenantId)
                .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                .build();

        log.info("Producer send invoice for tenant [{}]: {}", tenantId, dto);
        streamBridge.send("invoiceEventProducer", message);
    }


     public void sendProvision(InvoiceDto dto) {
        String tenantId = TenantContext.getCurrentTenant();
        log.info("Current tenant ID: {}", tenantId);
        Message<InvoiceDto> message = MessageBuilder
                .withPayload(dto)
                .setHeader("tenantId", tenantId)
                .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                .build();

        log.info("Sending invoice for tenant [{}]: {}", tenantId, dto);
        streamBridge.send("provisionEventProducer", message);
        log.info("Provision sent to Kafka");
    }



}

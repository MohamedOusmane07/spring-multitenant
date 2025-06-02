package net.mohamed.springmultitenant.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.mohamed.springmultitenant.dto.InvoiceDto;
import net.mohamed.springmultitenant.dto.ProvisionDto;
import net.mohamed.springmultitenant.streams.clusters.KafkaProducers;
import net.mohamed.springmultitenant.tenant.TenantContext;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/invoice")
@RequiredArgsConstructor
public class kafkaController {

  private final KafkaProducers producer;

  @PostMapping
  public ResponseEntity<Void> sendInvoice(@RequestBody InvoiceDto dto) {

    String tenantId = TenantContext.getCurrentTenant();
    log.info("Received invoice DTO: {}", dto);
    Message<InvoiceDto> message =
        MessageBuilder.withPayload(dto)
            .setHeader(KafkaHeaders.TOPIC, "invoice-" + tenantId) // ← nom du topic
            .setHeader(KafkaHeaders.KEY, dto.getStatus()) // ← clé explicite Kafka
            .setHeader("correlationId", "collectionId") // ← ID de corrélation
            .setHeader("tenantId", tenantId) // ← ID du locataire
            .build();
    log.info("Sending invoice message to tenant --- ~ {}", tenantId);
    producer.sendInvoice(message, tenantId);
    // producer.sendProvision(dto);
    return ResponseEntity.accepted().build();
  }

  @PostMapping("/prov")
  public ResponseEntity<Void> sendProvision(@RequestBody ProvisionDto dto) {
    log.info("Received provision DTO: {}", dto);
    Message<ProvisionDto> message =
        MessageBuilder.withPayload(dto)
            .setHeader(
                KafkaHeaders.TOPIC, "prov-" + TenantContext.getCurrentTenant()) // ← nom du topic
            // .setHeader(KafkaHeaders.KEY, "invoice-key") // ← clé explicite Kafka
            .setHeader("correlationId", "collectionId") // ← ID de corrélation
            .setHeader("tenantId", TenantContext.getCurrentTenant())
            .build();

    ///
    Message<String> strMessage =
        MessageBuilder.withPayload("Str message")
            .setHeader(
                KafkaHeaders.TOPIC, "str-" + TenantContext.getCurrentTenant()) // ← nom du topic
            // .setHeader(KafkaHeaders.KEY, "invoice-key") // ← clé explicite Kafka
            .setHeader("correlationId", "collectionId") // ← ID de corrélation
            .setHeader("tenantId", TenantContext.getCurrentTenant())
            .build();

    producer.sendProvision(message);
    producer.sendString(strMessage);

    return ResponseEntity.accepted().build();
  }
}

package net.mohamed.springmultitenant.streams;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.mohamed.springmultitenant.InvoiceService;
import net.mohamed.springmultitenant.dto.InvoiceDto;
import net.mohamed.springmultitenant.tenant.TenantContext;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumers {


    private final InvoiceService invoiceService;

    @Bean
    public Consumer<Message<InvoiceDto>> invoiceConsumer() {
        return message -> {
            String tenantId = message.getHeaders().get("tenantId", String.class);
            System.out.println("Tenant ID: " + tenantId);
            TenantContext.setCurrentTenant(tenantId);
           // invoiceService.createInvoice(message.getPayload());
            log.info("Received invoice: {}", message.getPayload());
            TenantContext.clearCurrentTenant();
        };
    }


/*
    @Bean
    public Consumer<Message<InvoiceDto>> provisionConsumer() {
        return message -> {
            String tenantId = message.getHeaders().get("tenantId", String.class);
            System.out.println("Tenant ID: " + tenantId);
            TenantContext.setCurrentTenant(tenantId);
           // invoiceService.createInvoice(message.getPayload());
            log.info("Received provision from consumer 2: {}", message.getPayload());
            TenantContext.clearCurrentTenant();
        };
    }*/


}


package net.mohamed.springmultitenant.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.mohamed.springmultitenant.dto.InvoiceDto;
import net.mohamed.springmultitenant.streams.KafkaProducers;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Void> send(@RequestBody InvoiceDto dto) {
        log.info("Received invoice DTO: {}", dto);
        producer.sendInvoice(dto);
        //producer.sendProvision(dto);
        return ResponseEntity.accepted().build();
    }
}



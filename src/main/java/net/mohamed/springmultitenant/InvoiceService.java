package net.mohamed.springmultitenant;

import lombok.RequiredArgsConstructor;
import net.mohamed.springmultitenant.dto.InvoiceDto;
import net.mohamed.springmultitenant.model.Invoice;
import net.mohamed.springmultitenant.repository.InvoiceRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;

    public void createInvoice(InvoiceDto invoiceDto) {
        Invoice invoice = new Invoice();
        invoice.setAmount(invoiceDto.getAmount());
        invoice.setCurrency(invoiceDto.getCurrency());
        invoice.setStatus(invoiceDto.getStatus());
        invoiceRepository.save(invoice);
    }


}

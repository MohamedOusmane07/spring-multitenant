package net.mohamed.springmultitenant.repository;

import net.mohamed.springmultitenant.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {

}

package net.mohamed.springmultitenant.repository;

import net.mohamed.springmultitenant.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {}

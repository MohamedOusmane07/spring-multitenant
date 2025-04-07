package net.mohamed.springmultitenant.controller;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import net.mohamed.springmultitenant.model.Customer;
import net.mohamed.springmultitenant.repository.CustomerRepository;
import net.mohamed.springmultitenant.tenant.TenantContext;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customers")
public class CustomerController {


    private final CustomerRepository customerRepository;

    @GetMapping("/home")
    public String home() {
        return TenantContext.getCurrentTenant();
    }

    @GetMapping
    public List<Customer> getCustomers() {
        return customerRepository.findAll();
    }


    @PostMapping
    public Customer createCustomer(@RequestBody Customer customer) {
        return customerRepository.save(customer);
    }


    //@PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/auth")
    public Authentication getAuthentication(Authentication authentication) {
        return authentication;
    }


}

package net.mohamed.springmultitenant.controller;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import net.mohamed.springmultitenant.model.Customer;
import net.mohamed.springmultitenant.repository.CustomerRepository;
import net.mohamed.springmultitenant.tenant.TenantContext;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customers")
public class CustomerController {


    private final CustomerRepository customerRepository;

    @GetMapping
    public String home() {
        return TenantContext.getCurrentTenant();
    }

    @GetMapping("/getAll")
    public List<Customer> getCustomers() {
        return customerRepository.findAll();
    }


    @PostMapping("/createCustomer")
    public Customer createCustomer(@RequestBody Customer customer) {
        return customerRepository.save(customer);
    }

}

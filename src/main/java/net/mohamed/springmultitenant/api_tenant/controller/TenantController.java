package net.mohamed.springmultitenant.api_tenant.controller;


import lombok.RequiredArgsConstructor;
import net.mohamed.springmultitenant.api_tenant.model.Tenant;
import net.mohamed.springmultitenant.api_tenant.service.TenantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/tenants")
public class TenantController {

    private final TenantService tenantService;


    @GetMapping
    public ResponseEntity<List<Tenant>> getAllTenants() {
        List<Tenant> tenants = tenantService.getAllTenants();
        return ResponseEntity.status(HttpStatus.OK).body(tenants);
    }


    @PostMapping
    public ResponseEntity<String> createTenant(@RequestBody Tenant tenant) {
            String tenantId = tenantService.createTenant(tenant);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Tenant created with ID: " + tenantId);
    }

}

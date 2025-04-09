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
@RequestMapping("api/tenants")
public class TenantController {

    private final TenantService tenantService;


    @GetMapping
    public List<Tenant> getAllTenants() {
        return tenantService.getAllTenants();
    }


    @PostMapping
    public ResponseEntity<String> createTenant(@RequestBody Tenant tenant) {
            String tenantId = tenantService.addTenant(tenant);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Tenant created with TenantID: " + tenantId);
    }

    @GetMapping("/{id}")
    public Tenant getTenantById(@PathVariable Integer id) {
        return tenantService.getTenantById(id);
    }

    @PutMapping("/{id}")
    public Tenant updateTenant(@PathVariable Integer id, @RequestBody Tenant tenant) {
        return tenantService.updateTenant(id, tenant);
    }

    @DeleteMapping("/{id}")
    public boolean deleteTenantById(@PathVariable Integer id) {
        return tenantService.deleteTenantById(id);
    }

}

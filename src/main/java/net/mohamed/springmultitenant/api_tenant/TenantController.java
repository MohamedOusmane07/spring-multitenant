package net.mohamed.springmultitenant.api_tenant;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/tenants")
public class TenantController {

    private final TenantService tenantService;

    @GetMapping
    public List<Tenant> getAllTenants() {
        return tenantService.getAllTenants();
    }

    @PostMapping
    public String createTenant(@RequestBody Tenant tenant) {
        return tenantService.createTenant(tenant);
    }


    /*

    @DeleteMapping({"id"})
    public void deleteTenant(@PathVariable Integer id) {
        tenantService.deleteTenant(id);
    }

     */
}

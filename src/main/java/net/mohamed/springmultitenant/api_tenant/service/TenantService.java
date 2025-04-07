package net.mohamed.springmultitenant.api_tenant.service;

import net.mohamed.springmultitenant.api_tenant.model.Tenant;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface TenantService {

    List<Tenant> getAllTenants();
    String createTenant(Tenant tenant);
    String findTenantByUsername(String username, Authentication authentication);
    //String validateTenant(String tenantId);



}

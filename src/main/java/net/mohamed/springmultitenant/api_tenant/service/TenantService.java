package net.mohamed.springmultitenant.api_tenant.service;

import net.mohamed.springmultitenant.api_tenant.model.Tenant;

import java.util.List;

public interface TenantService {

    List<Tenant> getAllTenants();
    String addTenant(Tenant tenant);
    String getTenantIdByUsername(String username);
    Tenant updateTenant(Integer id, Tenant tenant);
    Tenant getTenantById(Integer id);
    boolean deleteTenantById(Integer id);
    void tenantValidation(String tenantId);


}

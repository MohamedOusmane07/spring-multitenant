package net.mohamed.springmultitenant.api_tenant;

import java.util.List;

public interface TenantService {

    List<Tenant> getAllTenants();
    String createTenant(Tenant tenant);

}

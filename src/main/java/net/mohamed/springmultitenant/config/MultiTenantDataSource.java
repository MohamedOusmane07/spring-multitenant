package net.mohamed.springmultitenant.config;

import net.mohamed.springmultitenant.tenant.TenantContext;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class MultiTenantDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        System.out.println("Switching to tenant: " + TenantContext.getCurrentTenant()); // Ajout du log
        return TenantContext.getCurrentTenant();

    }
}

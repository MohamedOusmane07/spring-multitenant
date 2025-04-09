package net.mohamed.springmultitenant.tenant.config;

import lombok.extern.slf4j.Slf4j;
import net.mohamed.springmultitenant.tenant.TenantContext;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

@Slf4j
public class MultiTenantDataSource extends AbstractRoutingDataSource {

  @Override
  protected Object determineCurrentLookupKey() {
    log.info("Switching to tenant: {}", TenantContext.getCurrentTenant()); // Ajout du log
    return TenantContext.getCurrentTenant();
  }
}

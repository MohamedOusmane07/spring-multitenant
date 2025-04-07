package net.mohamed.springmultitenant.api_tenant.repository;

import net.mohamed.springmultitenant.api_tenant.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantRepository extends JpaRepository<Tenant, Integer> {
}

package net.mohamed.springmultitenant.api_tenant.service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import net.mohamed.springmultitenant.api_tenant.model.Tenant;
import net.mohamed.springmultitenant.api_tenant.repository.EbUserRepository;
import net.mohamed.springmultitenant.api_tenant.repository.TenantRepository;
import net.mohamed.springmultitenant.exception.TenantResolutionException;
import net.mohamed.springmultitenant.exception.tenant.TenantNotFoundException;
import net.mohamed.springmultitenant.tenant.TenantContext;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TenantServiceImpl implements TenantService {

  private final TenantRepository tenantRepository;
  private final EbUserService ebUserService;
  private final EbUserRepository userRepository;

  @Override
  public List<Tenant> getAllTenants() {

    return tenantRepository.findAll();
  }

  @Override
  public String addTenant(Tenant tenant) {
    return tenantRepository.save(tenant).getTenantId();
  }

  @Override
  public String getTenantIdByUsername(String username) {
    return ebUserService.getUserByUsername(username).getTenantId();
  }

  @Override
  public Tenant updateTenant(Integer id, Tenant tenant) {

    Tenant existingTenant =
        tenantRepository
            .findById(id)
            .orElseThrow(() -> new TenantNotFoundException("Tenant not found"));
    existingTenant.setTenantId(tenant.getTenantId());
    existingTenant.setDbUrl(tenant.getDbUrl());
    existingTenant.setDbUsername(tenant.getDbUsername());
    existingTenant.setDbPassword(tenant.getDbPassword());

    return tenantRepository.save(existingTenant);
  }

  @Override
  public Tenant getTenantById(Integer id) {
    Optional<Tenant> tenant = tenantRepository.findById(id);
    if (!tenant.isPresent()) {
      throw new TenantNotFoundException("Tenant not found");
    }
    return tenant.get();
  }

  @Override
  public boolean deleteTenantById(Integer id) {
    if (tenantRepository.existsById(id)) {
      tenantRepository.deleteById(id);
      return true;
    }

    return false;
  }

  @Override
  public void tenantValidation(String tenantId) {
    List<Tenant> tenants = tenantRepository.findAll();
    Collection<String> tenantIds =
        tenants.stream()
            .filter(tenant -> tenant.getTenantId() != null)
            .map(Tenant::getTenantId)
            .toList();
    if (!tenantIds.contains(tenantId)) {
      throw new TenantResolutionException("Tenant is not valid");
    }
    TenantContext.setCurrentTenant(tenantId);
  }
}

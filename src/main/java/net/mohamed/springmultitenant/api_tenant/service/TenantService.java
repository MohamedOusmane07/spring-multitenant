package net.mohamed.springmultitenant.api_tenant.service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.mohamed.springmultitenant.api_tenant.model.Tenant;
import net.mohamed.springmultitenant.api_tenant.repository.TenantRepository;
import net.mohamed.springmultitenant.exception.TenantResolutionException;
import net.mohamed.springmultitenant.exception.tenant.TenantNotFoundException;
import net.mohamed.springmultitenant.streams.clusters.KafkaTenantFactory;
import net.mohamed.springmultitenant.streams.clusters.TenantKafkaListenerRegistry;
import net.mohamed.springmultitenant.streams.clusters.TenantUtils;
import net.mohamed.springmultitenant.tenant.TenantContext;
import net.mohamed.springmultitenant.tenant.config.DataSourceConfig;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TenantService {

  private final TenantRepository tenantRepository;
  private final EbUserService ebUserService;
  private final DataSourceConfig dataSourceConfig;
  private final TenantUtils tenantUtils;
  private final KafkaTenantFactory kafkaTenantFactory;
  private final TenantKafkaListenerRegistry tenantKafkaListenerRegistry;

  public List<Tenant> getAllTenants() {

    return tenantRepository.findAll();
  }

  public String addTenant(Tenant tenant) {

    String tenantId = tenant.getTenantId();
    tenantRepository.save(tenant);
    dataSourceConfig.initializeNewDataSource(
        tenantId, tenant.getDbUrl(), tenant.getDbUsername(), tenant.getDbPassword());
    log.info("Tenant added: " + tenantId);
    tenantUtils.addCluster(tenantId, tenant.getBootstrapServers());

    tenantKafkaListenerRegistry.startListenerForTenant(tenantId);

    log.info("Tenant added to Kafka cluster: " + tenantId);

    return tenantId;
  }

  public String getTenantIdByUsername(String username) {
    return ebUserService.getUserByUsername(username).getTenantId();
  }

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

  public Tenant getTenantById(Integer id) {
    Optional<Tenant> tenant = tenantRepository.findById(id);
    if (!tenant.isPresent()) {
      throw new TenantNotFoundException("Tenant not found");
    }
    return tenant.get();
  }

  public boolean deleteTenantById(Integer id) {
    if (tenantRepository.existsById(id)) {
      Tenant tenant = tenantRepository.findById(id).get();
      String tenantId = tenant.getTenantId();

      kafkaTenantFactory.removeTenantRessources(tenantId);
      tenantRepository.deleteById(id);

      tenantUtils.removeCluster(tenantId);
      return true;
    }

    return false;
  }

  public void tenantValidation(String tenantId) {
    List<Tenant> tenants = tenantRepository.findAll();
    Collection<String> tenantIds =
        tenants.stream()
            .filter(tenant -> tenant.getTenantId() != null)
            .map(Tenant::getTenantId)
            .toList();
    if (!tenantIds.contains(tenantId) && !tenantId.equals("default")) {
      log.error("Tenant not found: " + tenantId);
      throw new TenantResolutionException("Tenant is not valid");
    }
    TenantContext.setCurrentTenant(tenantId);
  }
}

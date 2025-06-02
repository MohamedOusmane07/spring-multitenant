package net.mohamed.springmultitenant.streams.clusters;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.mohamed.springmultitenant.api_tenant.model.Tenant;
import net.mohamed.springmultitenant.tenant.config.DataSourceConfig;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TenantUtils {

  private final DataSourceConfig dataSourceConfig;

  private final ConcurrentHashMap<String, String> bootstrapServers = new ConcurrentHashMap<>();

  public ConcurrentHashMap<String, String> getCusters() {

    List<Tenant> tenants = dataSourceConfig.getAllTenants();

    tenants.forEach(
        tenant -> {
          String tenantId = tenant.getTenantId();
          String bootstrapServer = tenant.getBootstrapServers();
          bootstrapServers.put(tenantId, bootstrapServer);
        });
    log.info("Bootstrap servers: {}", bootstrapServers);
    return bootstrapServers;
  }

  public ConcurrentHashMap<String, String> addCluster(String tenantId, String bootstrapServer) {

    if (bootstrapServers.containsKey(tenantId)) {
      log.warn("Tenant [{}] already exists", tenantId);
      return bootstrapServers;
    }

    bootstrapServers.put(tenantId, bootstrapServer);
    log.info("Added new tenant [{}] with bootstrap server [{}]", tenantId, bootstrapServer);

    return bootstrapServers;
  }

  public ConcurrentHashMap<String, String> removeCluster(String tenantId) {

    if (!bootstrapServers.containsKey(tenantId)) {
      log.warn("Tenant [{}] does not exist", tenantId);
      return bootstrapServers;
    }
    bootstrapServers.remove(tenantId);
    log.info("Removed tenant [{}]", tenantId);
    return bootstrapServers;
  }

  public String getBootstrapServer(String tenantId) {
    ConcurrentHashMap<String, String> clusters = getCusters();
    if (clusters == null || !clusters.containsKey(tenantId)) {
      log.error("No cluster configuration found for tenant [{}]", tenantId);
      throw new IllegalStateException("No cluster configuration found for tenant ID: " + tenantId);
    }
    return clusters.get(tenantId);
  }
}

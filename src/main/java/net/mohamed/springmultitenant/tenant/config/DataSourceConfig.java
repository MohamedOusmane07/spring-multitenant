package net.mohamed.springmultitenant.tenant.config;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.mohamed.springmultitenant.api_tenant.model.Tenant;
import net.mohamed.springmultitenant.api_tenant.repository.TenantRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
@Slf4j
public class DataSourceConfig {

  private final FlywayConfig flywayConfig;
  private final DataSourceProvider dataSourceProvider;

  @Value("${spring.datasource.username}")
  private String defaultDbUsername;

  @Value("${spring.datasource.password}")
  private String defaultDbPassword;

  private MultiTenantDataSource multiTenantDataSource;
  private final Map<Object, Object> targetDataSources = new ConcurrentHashMap<>();
  private List<Tenant> tenants;
  private List<Tenant> tenantListWithoutDefault;

  @Bean
  public DataSource dataSource() {

    targetDataSources.put(
        "default",
        dataSourceProvider.createDataSource(
            "jdbc:postgresql://localhost:5432/db_centrale", defaultDbUsername, defaultDbPassword));

    multiTenantDataSource = new MultiTenantDataSource();
    multiTenantDataSource.setTargetDataSources(targetDataSources);
    multiTenantDataSource.setDefaultTargetDataSource(targetDataSources.get("default"));
    multiTenantDataSource.afterPropertiesSet();
    return multiTenantDataSource;
  }

  @Bean
  public ApplicationRunner initializeTenants(TenantRepository tenantRepository) {
    return args -> {
      tenants = tenantRepository.findAll();

      for (Tenant tenant : tenants) {
        if (!tenant.getTenantId().equals("default")) {

          log.info("Creating tenant " + tenant.toString());
          String tenantId = tenant.getTenantId();
          String dbUrl = tenant.getDbUrl();
          String dbUsername = tenant.getDbUsername();
          String dbPassword = tenant.getDbPassword();
          targetDataSources.put(
              tenantId, dataSourceProvider.createDataSource(dbUrl, dbUsername, dbPassword));
        }
      }

      multiTenantDataSource.setTargetDataSources(targetDataSources);
      multiTenantDataSource.afterPropertiesSet();
      flywayConfig.migrateAllTenants(targetDataSources);
    };
  }

  public void initializeNewDataSource(
      String tenantId, String dbUrl, String dbUsername, String dbPassword) {
    targetDataSources.put(
        tenantId, dataSourceProvider.createDataSource(dbUrl, dbUsername, dbPassword));
    multiTenantDataSource.setTargetDataSources(targetDataSources);
    multiTenantDataSource.afterPropertiesSet();
    flywayConfig.migrateNewDataSource(targetDataSources.get(tenantId));
    log.info("DataSource initialized for tenant: {}", tenantId);
  }

  public List<String> getAllTenantIds() {
    return targetDataSources.keySet().stream()
        .filter(key -> !key.equals("default"))
        .map(Object::toString)
        .toList();
  }

  public List<Tenant> getAllTenantsWithoutDefault() {
    return tenants.stream().filter(tenant -> !tenant.getTenantId().equals("default")).toList();
  }

  public List<Tenant> getAllTenants() {
    return tenants;
  }

  public Map<Object, Object> getTargetDataSources() {
    return targetDataSources;
  }
}

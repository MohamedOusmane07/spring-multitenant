package net.mohamed.springmultitenant.tenant.config;

import com.zaxxer.hikari.HikariDataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

  @Value("${spring.datasource.username}")
  private String dbUsername;

  @Value("${spring.datasource.password}")
  private String dbPassword;

  private MultiTenantDataSource multiTenantDataSource;
  Map<Object, Object> targetDataSources = new HashMap<>();

  // Créer une DataSource par défaut
  @Bean
  public DataSource dataSource() {
    targetDataSources.put(
        "default",
        createDataSource(
            "jdbc:postgresql://localhost:5432/db_centrale")); // Ajouter la source de données par
    // défaut
    // DataSource par défaut pour démarrer l'application


    multiTenantDataSource = new MultiTenantDataSource();
    multiTenantDataSource.setDefaultTargetDataSource(
        targetDataSources.get("default")); // Définir la source de données par défaut
    multiTenantDataSource.setTargetDataSources(new HashMap<>());
    multiTenantDataSource.afterPropertiesSet(); // Initialiser la configuration du multi-tenant
    return multiTenantDataSource; // Retourner le DataSource multi-tenant
  }

  // Méthode pour créer une DataSource
  private DataSource createDataSource(String url) {
    System.out.println("Creating DataSource for URL: " + url);
    HikariDataSource dataSource = new HikariDataSource();
    dataSource.setJdbcUrl(url);
    dataSource.setUsername(dbUsername);
    dataSource.setPassword(dbPassword);
    return dataSource;
  }

  // Utiliser un ApplicationRunner pour charger les tenants après l'initialisation de l'application
  @Bean
  public ApplicationRunner initializeTenants(TenantRepository tenantRepository) {
    return args -> {
      // Charger les tenants depuis la base de données
      List<Tenant> tenants = tenantRepository.findAll();

      for (Tenant tenant : tenants) {
        log.info("Creating tenant " + tenant.toString());
        String tenantId = tenant.getTenantId();
        String dbUrl = tenant.getDbUrl();
        targetDataSources.put(
            tenantId,
            createDataSource(dbUrl)); // Créer et ajouter les sources de données des tenants
      }

      // Mettre à jour le multi-tenant DataSource avec les nouvelles sources de données
      multiTenantDataSource.setTargetDataSources(targetDataSources);
      multiTenantDataSource.afterPropertiesSet(); // Appliquer les changements
    };
  }
}

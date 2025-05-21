package net.mohamed.springmultitenant.tenant.config;

import java.util.Map;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FlywayConfig {

  public void migrateAllTenants(Map<Object, Object> targetDataSources) {
    log.info("targetDataSources content: {}", targetDataSources);
    targetDataSources.forEach(
        (tenantId, dataSource) -> {
          log.info("Tenant ID: {}", tenantId);
          if (tenantId != null && !tenantId.equals("default")) {
            Flyway flyway =
                Flyway.configure()
                    .dataSource((DataSource) dataSource)
                    .baselineOnMigrate(true)
                    .schemas("flyway")
                    .locations("classpath:db/migration/tenants")
                    .load();

            flyway.migrate();
            System.out.println("Migrations exécutées pour le tenant : " + tenantId);
          } else {
            Flyway flyway =
                Flyway.configure()
                    .dataSource((DataSource) dataSource)
                    .baselineOnMigrate(true)
                    .schemas("flyway")
                    .locations("classpath:db/migration/masterdb")
                    .load();

            flyway.migrate();
            System.out.println("Migration exécutées pour le tenant par défaut : " + tenantId);
          }
        });
  }

  public void migrateNewDataSource(Object datasource) {
    Flyway flyway =
        Flyway.configure()
            .dataSource((DataSource) datasource)
            .baselineOnMigrate(false)
            .schemas("flyway")
            .validateMigrationNaming(true)
            .ignoreMigrationPatterns("*:missing", "*:Future")
            .locations("classpath:db/migration/tenants")
            .load();

    flyway.migrate();
  }
}

package net.mohamed.springmultitenant.tenant.config;

import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.springframework.stereotype.Component;

@Component
public class DataSourceProvider {

  public DataSource createDataSource(String url, String dbUsername, String dbPassword) {
    System.out.println("Creating DataSource for URL: " + url);
    HikariDataSource dataSource = new HikariDataSource();
    dataSource.setJdbcUrl(url);
    dataSource.setUsername(dbUsername);
    dataSource.setPassword(dbPassword);
    return dataSource;
  }
}

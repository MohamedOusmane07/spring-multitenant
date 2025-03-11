package net.mohamed.springmultitenant.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.username}")
    private String dbUsername;
    @Value("${spring.datasource.password}")
    private String dbPassword;



    @Bean
    public DataSource dataSource(){
        MultiTenantDataSource multiTenantDataSource=new MultiTenantDataSource();
        Map <Object, Object> targetDataSources= new HashMap<>();

        targetDataSources.put("tenant1",createDataSource("jdbc:postgresql://localhost:5432/tenant1_db"));
        targetDataSources.put("tenant2",createDataSource("jdbc:postgresql://localhost:5432/tenant2_db"));

        multiTenantDataSource.setTargetDataSources(targetDataSources);
        multiTenantDataSource.setDefaultTargetDataSource(targetDataSources.get("tenant2"));
        multiTenantDataSource.afterPropertiesSet();
        return multiTenantDataSource;
    }

    private DataSource createDataSource(String url) {
        System.out.println("Creating DataSource for URL: " + url);
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(dbUsername);
        dataSource.setPassword(dbPassword);
        return dataSource;
    }


}






















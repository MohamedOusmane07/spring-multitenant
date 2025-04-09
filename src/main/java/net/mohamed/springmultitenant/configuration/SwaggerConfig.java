package net.mohamed.springmultitenant.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .components(
            new io.swagger.v3.oas.models.Components()
                .addSecuritySchemes(
                    "bearer-key",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")))
        .addSecurityItem(new SecurityRequirement().addList("bearer-key"));
  }

  @Bean
  public GroupedOpenApi tenantApi() {
    return GroupedOpenApi.builder().group("tenants").pathsToMatch("/api/tenants/**").build();
  }

  @Bean
  public GroupedOpenApi ebUserApi() {
    return GroupedOpenApi.builder().group("ebusers").pathsToMatch("/api/users/**").build();
  }

  @Bean
  public GroupedOpenApi customerApi() {
    return GroupedOpenApi.builder().group("customers").pathsToMatch("/api/customers/**").build();
  }
}

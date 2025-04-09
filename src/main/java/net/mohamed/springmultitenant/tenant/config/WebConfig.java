package net.mohamed.springmultitenant.tenant.config;

import lombok.RequiredArgsConstructor;
import net.mohamed.springmultitenant.tenant.TenantInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// @Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

  private final TenantInterceptor tenantInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(tenantInterceptor);
  }
}

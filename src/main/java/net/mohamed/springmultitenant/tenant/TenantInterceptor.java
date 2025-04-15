package net.mohamed.springmultitenant.tenant;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// @Component
@RequiredArgsConstructor
@Slf4j
public class TenantInterceptor {

  /*
  private final HttpHeaderTenantResolvers headerTenantResolver;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {

    log.info("TenantInterceptor preHandle");
    String tenantId = headerTenantResolver.resolveTenantId(request);
    if (tenantId != null) {
      TenantContext.setCurrentTenant(tenantId);
    } else {
      TenantContext.setCurrentTenant("tenant1");
      // response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
      // return false;
    }
    return true;
  }

  @Override
  public void postHandle(
      HttpServletRequest request,
      HttpServletResponse response,
      Object handler,
      @Nullable ModelAndView modelAndView)
      throws Exception {
    clear();
  }

  @Override
  public void afterCompletion(
      HttpServletRequest request,
      HttpServletResponse response,
      Object handler,
      @Nullable Exception ex)
      throws Exception {
    clear();
  }

  private void clear() {
    TenantContext.clearCurrentTenant();
  }

   */
}

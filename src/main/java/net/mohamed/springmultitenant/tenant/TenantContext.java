package net.mohamed.springmultitenant.tenant;

public class TenantContext {

  private static final InheritableThreadLocal<String> CURRENT_TENANT =
      new InheritableThreadLocal<>();

  public static String getCurrentTenant() {
    return CURRENT_TENANT.get();
  }

  public static void setCurrentTenant(String tenant) {
    CURRENT_TENANT.set(tenant);
  }

  public static void clearCurrentTenant() {
    CURRENT_TENANT.remove();
  }
}

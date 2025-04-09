package net.mohamed.springmultitenant.exception.tenant;

public class TenantNotFoundException extends RuntimeException {

  public TenantNotFoundException(String message) {
    super(message);
  }
}

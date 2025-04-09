package net.mohamed.springmultitenant.tenant.resolvers;

import org.antlr.v4.runtime.misc.NotNull;

public interface TenantResolvers<T> {
  String resolveTenantId(@NotNull T tenant);
}

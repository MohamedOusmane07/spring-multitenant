package net.mohamed.springmultitenant.tenant.resolvers;

import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class HttpHeaderTenantResolvers implements TenantResolvers<HttpServletRequest> {

    private static final String TENANT_HEADER = "X-Tenant-ID";

    @Override
    @Nullable
    public String resolveTenantId(HttpServletRequest request) {
        return request.getHeader(TENANT_HEADER);
    }
}

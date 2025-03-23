package net.mohamed.springmultitenant.tenant;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.mohamed.springmultitenant.exception.TenantResolutionException;
import net.mohamed.springmultitenant.tenant.resolvers.HttpHeaderTenantResolvers;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
@RequiredArgsConstructor
@Slf4j
public class TenantContextFilter extends OncePerRequestFilter {
    
    private final HttpHeaderTenantResolvers headerTenantResolver;
    
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        log.info("TenantContextFilter doFilterInternal");
        var tenantIdentifier=headerTenantResolver.resolveTenantId(request);
        if (StringUtils.hasText(tenantIdentifier)) {
            TenantContext.setCurrentTenant(tenantIdentifier);
        } else  {
            throw new TenantResolutionException("A valid tenant identifier must be specified for requests to %s".formatted(request.getRequestURI()));
        }

        try {
            filterChain.doFilter(request, response);
        }finally {
            TenantContext.clearCurrentTenant();
        }
    }
}

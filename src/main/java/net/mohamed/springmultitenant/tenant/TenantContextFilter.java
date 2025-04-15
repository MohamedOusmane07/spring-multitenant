package net.mohamed.springmultitenant.tenant;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.mohamed.springmultitenant.api_tenant.service.TenantService;
import net.mohamed.springmultitenant.exception.TenantResolutionException;
import net.mohamed.springmultitenant.exception.user.UserNotAuthenticatedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Slf4j
public class TenantContextFilter extends OncePerRequestFilter {

  private final TenantService tenantService;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String requestURI = request.getRequestURI();
    if (requestURI.startsWith("/swagger-ui/") || requestURI.startsWith("/v3/api-docs/")) {
      // Si la requête est pour Swagger UI ou pour la documentation de l'API, on passe directement à
      // la requête suivante
      filterChain.doFilter(request, response);
      return;
    }

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    try {

      String authenticationName = getUsername(authentication);
      String tenantIdByUsername = tenantService.getTenantIdByUsername(authenticationName);
      log.info("User authentication & TenantID: {} & {}", authenticationName, tenantIdByUsername);

      if (StringUtils.hasText(tenantIdByUsername)) {
        tenantService.tenantValidation(tenantIdByUsername);
      } else {

        throw new TenantResolutionException("Tenant ID is empty or null.");
      }

      // Passe à la requête suivante dans la chaîne de filtres
      filterChain.doFilter(request, response);

    } catch (UserNotAuthenticatedException ex) {
      // Si l'utilisateur n'est pas authentifié, retourne une réponse HTTP 401
      log.error("User is not authenticated.", ex);
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
      response.getWriter().write("User is not authenticated.");
      // Interrompt la chaîne de filtres pour ne pas continuer avec la requête
    } catch (Exception ex) {
      // Gestion d'autres exceptions générales
      log.error("An error occurred in TenantContextFilter.", ex);
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500 Internal Server Error
      response.getWriter().write("Internal server error.");
    } finally {
      TenantContext.clearCurrentTenant();
    }
  }

  public String getUsername(Authentication authentication) {
    // Vérifier si l'utilisateur est authentifié
    if (authentication == null
        || !authentication.isAuthenticated()
        || authentication.getName().equals("anonymousUser")) {
      throw new UserNotAuthenticatedException("User is not authenticated.");
    }
    return authentication.getName();
  }
}

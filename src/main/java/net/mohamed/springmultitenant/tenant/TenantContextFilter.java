package net.mohamed.springmultitenant.tenant;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.mohamed.springmultitenant.api_tenant.repository.EbUserRepository;
import net.mohamed.springmultitenant.api_tenant.service.EbUserService;
import net.mohamed.springmultitenant.exception.user.UserNotAuthenticatedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
@RequiredArgsConstructor
@Slf4j
public class TenantContextFilter extends OncePerRequestFilter {
    
    private final EbUserService ebUserService;
    
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try {
            // Récupère le nom de l'utilisateur authentifié
            String authenticationName = getUsername(authentication);

            // Récupère le tenantId basé sur le nom d'utilisateur
            String tenantIdByUsername = ebUserService.getUserByUsername(authenticationName).getTenantId();
            log.info("User authentication & TenantID: {} & {}", authenticationName, tenantIdByUsername);

            // Si le tenantId est trouvé, on le définit dans le contexte, sinon, "default"
            if (StringUtils.hasText(tenantIdByUsername)) {
                TenantContext.setCurrentTenant(tenantIdByUsername);
            } else  {
                TenantContext.setCurrentTenant("default");
            }

            // Passe à la requête suivante dans la chaîne de filtres
            filterChain.doFilter(request, response);

        } catch (UserNotAuthenticatedException ex) {
            // Si l'utilisateur n'est pas authentifié, retourne une réponse HTTP 401
            log.error("User is not authenticated.", ex);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
            response.getWriter().write("User is not authenticated.");
            return; // Interrompt la chaîne de filtres pour ne pas continuer avec la requête
        } catch (Exception ex) {
            // Gestion d'autres exceptions générales
            log.error("An error occurred in TenantContextFilter.", ex);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500 Internal Server Error
            response.getWriter().write("Internal server error.");
            return;
        } finally {
            // Nettoyage du contexte du tenant à la fin de la requête
            TenantContext.clearCurrentTenant();
        }
    }


    public String getUsername(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UserNotAuthenticatedException("User is not authenticated.");
        }
        return authentication.getName(); // Le nom d'utilisateur de l'authentification
    }
}

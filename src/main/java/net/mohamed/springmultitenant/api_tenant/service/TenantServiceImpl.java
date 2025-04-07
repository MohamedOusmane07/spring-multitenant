package net.mohamed.springmultitenant.api_tenant.service;

import lombok.RequiredArgsConstructor;
import net.mohamed.springmultitenant.api_tenant.repository.EbUserRepository;
import net.mohamed.springmultitenant.api_tenant.model.Tenant;
import net.mohamed.springmultitenant.api_tenant.repository.TenantRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;
    private final EbUserRepository userRepository;

    @Override
    public List<Tenant> getAllTenants() {

        return tenantRepository.findAll();

    }

    @Override
    public String createTenant(Tenant tenant) {
        return tenantRepository.save(tenant).getTenantId();

    }


    @Override
    public String findTenantByUsername(String username, Authentication authentication) {




        return "";
    }


}

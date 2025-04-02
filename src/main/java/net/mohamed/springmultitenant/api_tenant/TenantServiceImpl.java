package net.mohamed.springmultitenant.api_tenant;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;

    @Override
    public List<Tenant> getAllTenants() {

        return tenantRepository.findAll();

    }

    @Override
    public String createTenant(Tenant tenant) {
        return tenantRepository.save(tenant).getTenant_id();

    }



}

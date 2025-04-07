package net.mohamed.springmultitenant.api_tenant.repository;

import net.mohamed.springmultitenant.api_tenant.model.EbUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EbUserRepository extends JpaRepository<EbUser, Integer> {
    Optional<EbUser> findByUsername(String username);

}

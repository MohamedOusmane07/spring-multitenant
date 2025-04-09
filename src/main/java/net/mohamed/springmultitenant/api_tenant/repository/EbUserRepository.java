package net.mohamed.springmultitenant.api_tenant.repository;

import java.util.Optional;
import net.mohamed.springmultitenant.api_tenant.model.EbUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EbUserRepository extends JpaRepository<EbUser, Integer> {
  Optional<EbUser> findByUsername(String username);
}

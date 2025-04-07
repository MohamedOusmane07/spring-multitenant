package net.mohamed.springmultitenant.api_tenant.service;

import lombok.RequiredArgsConstructor;
import net.mohamed.springmultitenant.api_tenant.model.EbUser;
import net.mohamed.springmultitenant.api_tenant.repository.EbUserRepository;
import net.mohamed.springmultitenant.exception.user.UserNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EbUserServiceImp implements EbUserService {

    private final EbUserRepository userRepository;

    @Override
    public EbUser getUserByUsername(String username) {
        EbUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User "+ username + " not found"));
        return user;

    }

}

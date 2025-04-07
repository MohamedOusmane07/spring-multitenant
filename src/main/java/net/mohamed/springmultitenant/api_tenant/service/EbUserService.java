package net.mohamed.springmultitenant.api_tenant.service;

import net.mohamed.springmultitenant.api_tenant.model.EbUser;
import org.springframework.security.core.Authentication;

public interface EbUserService {

    EbUser getUserByUsername(String username);



    //EbUser addUser(EbUser user);
}

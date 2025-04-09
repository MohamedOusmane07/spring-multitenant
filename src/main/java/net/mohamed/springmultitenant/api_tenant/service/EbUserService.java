package net.mohamed.springmultitenant.api_tenant.service;

import net.mohamed.springmultitenant.api_tenant.model.EbUser;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface EbUserService {

    List<EbUser> getAllUsers();
    EbUser getUserByUsername(String username);
    EbUser addUser(EbUser user);
    EbUser updateUser(Integer id,EbUser user);
    EbUser getUserById(Integer id);
    boolean deleteUserById(Integer id);



    //EbUser addUser(EbUser user);
}

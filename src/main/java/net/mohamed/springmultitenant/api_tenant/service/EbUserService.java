package net.mohamed.springmultitenant.api_tenant.service;

import java.util.List;
import net.mohamed.springmultitenant.api_tenant.model.EbUser;

public interface EbUserService {

  List<EbUser> getAllUsers();

  EbUser getUserByUsername(String username);

  EbUser addUser(EbUser user);

  EbUser updateUser(Integer id, EbUser user);

  EbUser getUserById(Integer id);

  boolean deleteUserById(Integer id);

  // EbUser addUser(EbUser user);
}

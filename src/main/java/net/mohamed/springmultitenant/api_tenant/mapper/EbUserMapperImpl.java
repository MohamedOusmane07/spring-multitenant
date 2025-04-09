package net.mohamed.springmultitenant.api_tenant.mapper;

import net.mohamed.springmultitenant.api_tenant.model.EbUser;
import org.springframework.stereotype.Service;

@Service
public class EbUserMapperImpl implements EbUserMapper {

  @Override
  public EbUser updateModel(EbUser userToUpdate, EbUser user) {
    if (user == null) {
      throw new IllegalArgumentException("user cannot be null");
    }
    userToUpdate.setTenantId(user.getTenantId());
    return userToUpdate;
  }
}

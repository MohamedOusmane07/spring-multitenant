package net.mohamed.springmultitenant.api_tenant.mapper;

import net.mohamed.springmultitenant.api_tenant.model.EbUser;

public interface EbUserMapper {

  EbUser updateModel(EbUser userToUpdate, EbUser user);
}

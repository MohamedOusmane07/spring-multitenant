package net.mohamed.springmultitenant.api_tenant.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import net.mohamed.springmultitenant.api_tenant.mapper.EbUserMapper;
import net.mohamed.springmultitenant.api_tenant.model.EbUser;
import net.mohamed.springmultitenant.api_tenant.repository.EbUserRepository;
import net.mohamed.springmultitenant.exception.user.UserNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EbUserServiceImp implements EbUserService {

  private final EbUserRepository userRepository;
  private final EbUserMapper userMapper;

  @Override
  public List<EbUser> getAllUsers() {
    return userRepository.findAll();
  }

  @Override
  public EbUser getUserByUsername(String username) {
    EbUser user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException("User " + username + " not found"));
    return user;
  }

  @Override
  public EbUser addUser(EbUser user) {
    return userRepository.save(user);
  }

  @Override
  public EbUser updateUser(Integer id, EbUser user) {
    EbUser existingUser =
        userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));

    existingUser = userMapper.updateModel(existingUser, user);

    return userRepository.save(existingUser);
  }

  @Override
  public EbUser getUserById(Integer id) {
    Optional<EbUser> user = userRepository.findById(id);
    if (!user.isPresent()) {
      throw new UserNotFoundException("User not found");
    }
    return user.get();
  }

  @Override
  public boolean deleteUserById(Integer id) {
    if (userRepository.existsById(id)) {
      userRepository.deleteById(id);
      return true;
    }
    ;
    throw new UserNotFoundException("User not found");
  }
}

package net.mohamed.springmultitenant.api_tenant.controller;

import lombok.RequiredArgsConstructor;
import net.mohamed.springmultitenant.api_tenant.model.EbUser;
import net.mohamed.springmultitenant.api_tenant.service.EbUserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/users")
public class EbUserController {

    private final EbUserService ebUserService;

    @GetMapping
    public List<EbUser> getAllUsers() {
        return ebUserService.getAllUsers();
    }

    @GetMapping("/{username}")
    public EbUser getUserByUsername(@PathVariable String username) {
        return ebUserService.getUserByUsername(username);
    }


    @PostMapping
    public EbUser addUser(@RequestBody EbUser user) {
        return ebUserService.addUser(user);
    }

    @PutMapping("/{id}")
    public EbUser updateUser(@PathVariable Integer id,@RequestBody EbUser user) {
        return ebUserService.updateUser(id, user);
    }

    @DeleteMapping("/{id}")
    public boolean deleteUserById(@PathVariable Integer id) {
        return ebUserService.deleteUserById(id);
    }
}

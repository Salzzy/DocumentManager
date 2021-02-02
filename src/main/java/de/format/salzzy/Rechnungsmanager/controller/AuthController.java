package de.format.salzzy.Rechnungsmanager.controller;

import com.google.common.collect.Sets;
import de.format.salzzy.Rechnungsmanager.model.Role;
import de.format.salzzy.Rechnungsmanager.model.User;
import de.format.salzzy.Rechnungsmanager.model.auth.Permission;
import de.format.salzzy.Rechnungsmanager.repository.RoleRepository;
import de.format.salzzy.Rechnungsmanager.repository.UserRepository;
import de.format.salzzy.Rechnungsmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Controller
public class AuthController {

    private final RoleRepository roleRepository;
    private final UserService userService;

    @Autowired
    public AuthController(RoleRepository roleRepository, UserService userService) {
        this.roleRepository = roleRepository;
        this.userService = userService;
    }

    @GetMapping("/login")
    public String getLogin()
    {


        /*
        Optional<Role> role = roleRepository.findById(1L);
        Role acutelRole = role.get();
        Permission permission = new Permission();
        permission.setId(1L);
        permission.setPermission("student:read");
        permission.setRoles(Sets.newHashSet(acutelRole));
        acutelRole.setPermissions(Sets.newHashSet(permission));
        roleRepository.save(acutelRole);
        */


        return "app/login";
    }

}

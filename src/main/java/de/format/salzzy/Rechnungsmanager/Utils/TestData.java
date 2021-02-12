package de.format.salzzy.Rechnungsmanager.Utils;

import de.format.salzzy.Rechnungsmanager.model.UserInfo;
import de.format.salzzy.Rechnungsmanager.model.auth.Role;
import de.format.salzzy.Rechnungsmanager.model.auth.User;
import de.format.salzzy.Rechnungsmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class TestData {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public TestData(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void runOnApplicationReady() {
        addTestUserIfNotExists();
        // deleteTestUser();
    }

    private void addTestUserIfNotExists() {
        if (userService.findByUsername("andre.s") == null) {
            userService.save(
                    new User(
                            "andre.s",
                            passwordEncoder.encode("password"),
                            true,
                            true,
                            true,
                            true
                    )
            );
            System.out.println("Default User created.");
        }
    }

    private void deleteTestUser() {
        if (userService.findByUsername("andre.s") != null) {
            userService.deleteUserById(userService.findByUsername("andre.s").getId());
            System.out.println("Default User deleted from Database");
        }
    }

}

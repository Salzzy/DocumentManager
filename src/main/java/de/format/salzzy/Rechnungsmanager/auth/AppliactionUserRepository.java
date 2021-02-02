package de.format.salzzy.Rechnungsmanager.auth;

import java.util.Optional;

public interface AppliactionUserRepository {

    public Optional<ApplicationUser> selectApplicationUserByUsername(String username);
}

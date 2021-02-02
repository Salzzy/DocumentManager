package de.format.salzzy.Rechnungsmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.format.salzzy.Rechnungsmanager.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
	
	Optional<User> findByUsername(String username);

}

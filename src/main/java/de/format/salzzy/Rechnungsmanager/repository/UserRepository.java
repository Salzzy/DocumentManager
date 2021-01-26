package de.format.salzzy.Rechnungsmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.format.salzzy.Rechnungsmanager.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
	
	User findByUsername(String username);

}

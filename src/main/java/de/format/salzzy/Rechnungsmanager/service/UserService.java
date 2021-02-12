package de.format.salzzy.Rechnungsmanager.service;

import java.util.List;

import de.format.salzzy.Rechnungsmanager.model.auth.User;
import de.format.salzzy.Rechnungsmanager.model.UserInfo;

public interface UserService {
	
	void save(User user);

	void save(UserInfo userinfo);

	User currentLoggedInUser();

    User findByUsername(String username);
    
    List<User> findAll();

    List<User> findAllActiveUsers();

    void deleteUserById(Long id);

    void saveDeleteUserById(Long id);

}

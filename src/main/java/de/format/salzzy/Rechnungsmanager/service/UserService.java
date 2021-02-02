package de.format.salzzy.Rechnungsmanager.service;

import java.util.List;

import de.format.salzzy.Rechnungsmanager.model.auth.User;
import de.format.salzzy.Rechnungsmanager.model.UserInfo;

public interface UserService {
	
	void save(User user);

	User currentLoggedInUser();
	
	void saveUserInfo(User user, UserInfo userinfo);
	
	void saveNormal(User user);

    User findByUsername(String username);
    
    List<User> findAll();

}

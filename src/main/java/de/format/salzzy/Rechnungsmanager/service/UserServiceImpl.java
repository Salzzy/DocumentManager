package de.format.salzzy.Rechnungsmanager.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import de.format.salzzy.Rechnungsmanager.model.auth.Role;
import de.format.salzzy.Rechnungsmanager.model.auth.User;
import de.format.salzzy.Rechnungsmanager.model.UserInfo;
import de.format.salzzy.Rechnungsmanager.repository.RoleRepository;
import de.format.salzzy.Rechnungsmanager.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder)
	{
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
	}


    @Override
    public void save(User user) {
        userRepository.save(user);
    }


	@Override
	public void save(UserInfo userinfo) {
    	User user = currentLoggedInUser();
    	user.setUserInfo(userinfo);
    	userRepository.save(user);
	}


	@Override
	public User currentLoggedInUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String currentPrincipalName = authentication.getName();
		return findByUsername(currentPrincipalName);
	}


	@Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).get();
    }


	@Override
	public List<User> findAll() {
		return userRepository.findAll();
	}

}

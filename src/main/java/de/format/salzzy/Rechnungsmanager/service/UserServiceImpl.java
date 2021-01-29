package de.format.salzzy.Rechnungsmanager.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import de.format.salzzy.Rechnungsmanager.model.Role;
import de.format.salzzy.Rechnungsmanager.model.User;
import de.format.salzzy.Rechnungsmanager.model.UserInfo;
import de.format.salzzy.Rechnungsmanager.repository.RoleRepository;
import de.format.salzzy.Rechnungsmanager.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder bCryptPasswordEncoder)
	{
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}


    @Override
    public void save(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        // USER Rolle in das HashSet und den User speichern
        HashSet<Role> roles = new HashSet<Role>();
        Optional<Role> role = roleRepository.findById(1l);
		role.ifPresent(roles::add);

        user.setUserinfo(new UserInfo());
        user.setRoles(roles);
        userRepository.save(user);
    }


	@Override
	public User currentLoggedInUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String currentPrincipalName = authentication.getName();
		User user = findByUsername(currentPrincipalName);
		return user;
	}


	@Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }


	@Override
	public void saveUserInfo(User user, UserInfo userinfo) {

		User userAktuell = userRepository.findByUsername(user.getUsername());
		UserInfo aktuell = userAktuell.getUserinfo();

		// Prüfe userDetails ab
		// Signatur muss getrennt von Daten gespeichert werden
		if(aktuell.getAbteilung() != userinfo.getAbteilung() && (userinfo.getAbteilung() != null || !userinfo.getAbteilung().isEmpty())) {
			aktuell.setAbteilung(userinfo.getAbteilung());
		}
		if(aktuell.getEmail() != userinfo.getEmail() && (userinfo.getEmail() != null || !userinfo.getEmail().isEmpty())) {
			aktuell.setEmail(userinfo.getEmail());
		}
		if(aktuell.getTelefonnr() != userinfo.getTelefonnr() && (userinfo.getTelefonnr() != null || !userinfo.getTelefonnr().isEmpty())) {
			aktuell.setTelefonnr(userinfo.getTelefonnr());
		} 
		
		userAktuell.setUserinfo(aktuell);
		userRepository.save(userAktuell);
	}


	@Override
	public void saveNormal(User user) {
		userRepository.save(user);
	}


	@Override
	public List<User> findAll() {
		return userRepository.findAll();
	}

}

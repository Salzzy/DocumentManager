package de.format.salzzy.Rechnungsmanager.service;

public interface SecurityService {

	String findLoggedInUsername();

    void autoLogin(String username, String password);
	
}

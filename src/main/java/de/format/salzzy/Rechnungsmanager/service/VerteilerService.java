package de.format.salzzy.Rechnungsmanager.service;

import java.io.File;
import java.util.List;

import de.format.salzzy.Rechnungsmanager.model.User;

public interface VerteilerService {

	public List<String> getFileNames(File folder);
	
	public void sendNotification(User user, Integer anzahl);
	
}

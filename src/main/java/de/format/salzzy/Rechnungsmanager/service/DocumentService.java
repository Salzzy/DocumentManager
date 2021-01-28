package de.format.salzzy.Rechnungsmanager.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import de.format.salzzy.Rechnungsmanager.model.User;
import org.springframework.web.multipart.MultipartFile;

public interface DocumentService {

	public List<String> getFileNames(File folder);
	
	public void sendNotification(User user, Integer anzahl);

	String saveFile(MultipartFile file) throws IOException;
	
}

package de.format.salzzy.Rechnungsmanager.service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import de.format.salzzy.Rechnungsmanager.model.User;

@Service
public class VerteilerServiceImpl implements VerteilerService {
	
	private JavaMailSender javaMailSender;
	
	@Autowired
	public VerteilerServiceImpl(JavaMailSender javaMailSender) {
		this.javaMailSender = javaMailSender;
	}
	
	@Override
	public List<String> getFileNames(File folder) {
		
		// Get all pdfs im Verzeichniss des Users
		File[] files = folder.listFiles();
		List<String> fileNames = new ArrayList<String>();
		
		// Speichere Pdf Namen in Liste
		if(files != null) {
			for(File f : files) {
				
				if(f.getName().endsWith(".pdf")) {
					String formatName = f.getName().replaceAll("\\s+", "_");
					fileNames.add(formatName);
				}
			}
		} 
		
		return fileNames;
	}

	@Override
	public void sendNotification(User user, Integer anzahl) {
		
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm dd.MM.yyyy");  
	    Date date = new Date();  
	    String dateString = formatter.format(date);  
	
		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setTo(user.getUserinfo().getEmail());
		msg.setSubject("Es wurden Rechnungen in ihre Ablage gelegt.");
		msg.setText(dateString + " Sie haben " + anzahl + " Rechnungen erhalten, die Sie signieren müssen!");
		
		javaMailSender.send(msg);
		
		
	}
	
	

}

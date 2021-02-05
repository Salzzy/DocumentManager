package de.format.salzzy.Rechnungsmanager.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.format.salzzy.Rechnungsmanager.Utils.FileUploadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import de.format.salzzy.Rechnungsmanager.model.auth.User;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DocumentServiceImpl implements DocumentService {
	
	private JavaMailSender javaMailSender;
	private SettingService settingService;

	private FileUploadUtils fileUploadUtils;
	
	@Autowired
	public DocumentServiceImpl(JavaMailSender javaMailSender, SettingService settingService, FileUploadUtils fileUploadUtils) {
		this.javaMailSender = javaMailSender;
		this.settingService = settingService;
		this.fileUploadUtils = fileUploadUtils;
	}

	@Override
	public String getDocumentPath() {
		return settingService.getSetting().getDocumentPath();
	}

	@Override
	public String getPublicInvoiceDocumentPath() {
		return settingService.getSetting().getDocumentPath() + "Rechnungen/";
	}

	@Override
	public String getUserDocumentUtilsPath(User user) {
		return settingService.getSetting().getDocumentUserUtilPath(user);
	}

	@Override
	public String getUserDocumentPath(User user) {
		Path docsFolder = Paths.get(settingService.getSetting().getDocumentPath());
		Path docsUserUtilFolder = Paths.get(settingService.getSetting().getDocumentUserUtilPath(user));

		try {
			if (!Files.exists(docsFolder)) Files.createDirectory(docsFolder);
			if (!Files.exists(docsUserUtilFolder)) Files.createDirectories(docsUserUtilFolder);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return settingService.getSetting().getDocumentUserPath(user);
	}


	@Override
	public List<String> getFileNames(File folder) {
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
		msg.setTo(user.getUserInfo().getEmail());
		msg.setSubject("Es wurden Rechnungen in ihre Ablage gelegt.");
		msg.setText(dateString + " Sie haben " + anzahl + " Rechnungen erhalten, die Sie freigeben müssen!");
		
		javaMailSender.send(msg);
	}

	@Override
	public String saveFile(MultipartFile file) throws IOException {
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		String uploadPath = settingService.getSetting().getDocumentInvoicePath();
		fileUploadUtils.saveFile(uploadPath, fileName, file);
		return uploadPath+file.getOriginalFilename();
	}

	@Override
	public String saveFile(MultipartFile file, String path) throws IOException {
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		String uploadPath = StringUtils.cleanPath(path);
		fileUploadUtils.saveFile(uploadPath, fileName, file);
		return uploadPath+file.getOriginalFilename();
	}
}

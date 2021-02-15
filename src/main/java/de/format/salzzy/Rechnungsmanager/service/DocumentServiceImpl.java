package de.format.salzzy.Rechnungsmanager.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.format.salzzy.Rechnungsmanager.Utils.FileUploadUtils;
import de.format.salzzy.Rechnungsmanager.model.Document;
import de.format.salzzy.Rechnungsmanager.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import de.format.salzzy.Rechnungsmanager.model.auth.User;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DocumentServiceImpl implements DocumentService {
	
	private final JavaMailSender javaMailSender;
	private final SettingService settingService;
	private final UserService userService;

	private final DocumentRepository documentRepository;

	private final FileUploadUtils fileUploadUtils;
	
	@Autowired
	public DocumentServiceImpl(JavaMailSender javaMailSender, SettingService settingService, FileUploadUtils fileUploadUtils, DocumentRepository documentRepository, UserService userService) {
		this.javaMailSender = javaMailSender;
		this.settingService = settingService;
		this.fileUploadUtils = fileUploadUtils;
		this.documentRepository = documentRepository;
		this.userService = userService;
	}

	@Override
	public String getDocumentPath() {
		return settingService.getSetting().getDocumentPath();
	}


	@Override
	public String getSystemDocumentPath() throws IOException {
		Path systemPath = Paths.get(settingService.getSetting().getDocumentSystemPath());
		if (!Files.exists(systemPath)) Files.createDirectories(systemPath);
		return systemPath.toString();
	}


	@Override
	public String getPublicInvoiceDocumentPath() throws IOException {
		Path invoicePath = Paths.get(settingService.getSetting().getDocumentPath() + "Rechnungen/");
		Path invoiceSignedPath = Paths.get(settingService.getSetting().getDocumentPath() + "Rechnungen/Freigegeben/");

		if(!Files.exists(invoicePath)) Files.createDirectories(invoiceSignedPath);

		return settingService.getSetting().getDocumentPath() + "Rechnungen/";
	}


	@Override
	public String getUserDocumentPath(User user) throws IOException {
		Path docsFolder = Paths.get(settingService.getSetting().getDocumentPath());
		Path docsUserUtilFolder = Paths.get(settingService.getSetting().getDocumentUserUtilPath(user));

		if (!Files.exists(docsFolder)) Files.createDirectories(docsUserUtilFolder);

		return settingService.getSetting().getDocumentUserPath(user);
	}


	@Override
	public String getUserDocumentUtilsPath(User user) {
		return settingService.getSetting().getDocumentUserUtilPath(user);
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
	public String saveFile(MultipartFile file) throws IOException
	{
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		String documentPath = settingService.getSetting().getDocumentInvoicePath();
		Document document = documentRepository.save(
			new Document(
				fileName,
				documentPath,
				userService.currentLoggedInUser()
			)
		);
		String uniqueFileName = String.format("%s %s", Long.toString(document.getId()), fileName);
		document.setFileName(uniqueFileName);
		documentRepository.save(document);
		fileUploadUtils.saveFile(documentPath, uniqueFileName, file);

		return documentPath+fileName;
	}

	@Override
	public String saveFile(MultipartFile file, String path) throws IOException {
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		String uploadPath = StringUtils.cleanPath(path);
		Document document = documentRepository.save(
				new Document(
						fileName,
						uploadPath,
						userService.currentLoggedInUser()
				)
		);
		String uniqueFileName = String.format("%s %s", Long.toString(document.getId()), fileName);
		document.setFileName(uniqueFileName);
		documentRepository.save(document);
		fileUploadUtils.saveFile(uploadPath, fileName, file);
		return uploadPath+file.getOriginalFilename();
	}

	@Override
	public List<Document> findAll(Sort sort) {
		return documentRepository.findAll(sort);
	}

	@Override
	public List<Document> getAllDocumentsByStatus(Integer status) {
		return documentRepository.findAllByStatus(status);
	}
}

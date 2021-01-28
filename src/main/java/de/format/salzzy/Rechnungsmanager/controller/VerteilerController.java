package de.format.salzzy.Rechnungsmanager.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.format.salzzy.Rechnungsmanager.service.DocumentService;
import de.format.salzzy.Rechnungsmanager.service.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.format.salzzy.Rechnungsmanager.model.User;
import de.format.salzzy.Rechnungsmanager.service.UserService;

@Controller
public class VerteilerController {
	
	private String PDF_DIR = "C:\\Users\\salzmann\\Desktop\\Test-Umgebung\\FIBU\\Rechnungen";
	private String INPUT_DIR = "C:\\Users\\salzmann\\Desktop\\Test-Umgebung\\FIBU\\Mitarbeiter\\{placeholder}";

	private UserService userService;
	private DocumentService documentService;
	private SettingService settingService;

	@Autowired
	public VerteilerController(UserService userService, DocumentService documentService, SettingService settingService)
	{
		this.userService = userService;
		this.documentService = documentService;
		this.settingService = settingService;
	}
	
	@GetMapping("/verteilen")
	public String verteilen(Model theModel) {
		
		File folder = new File(settingService.getSetting().getDocumentPath());
		
		// Get all pdfs im Verzeichniss des Users
		List<String> fileNames = documentService.getFileNames(folder);
		List<String> autoComplete = new ArrayList<String>();
		
		List<User> users = userService.findAll();
		users.stream().map(n -> n.getUsername()).forEach(autoComplete::add);
		
		theModel.addAttribute("users", autoComplete);
		theModel.addAttribute("pdfs", fileNames);
		theModel.addAttribute("anzahl", fileNames.size());
		
		return "app/verteilen/index";
	}

	
	@PostMapping("/verschieben")
	public String verteilen(@RequestParam("userName") String username, @RequestParam("pdfList[]") String[] pdfs) {
		
		System.out.println(username + " | " + pdfs[0]);
		
		String userFolderPath = INPUT_DIR.replace("{placeholder}", username);
		
		File rechnungsFolder = new File(PDF_DIR);
		File[] documents = rechnungsFolder.listFiles();
		Arrays.asList(documents).stream().forEach(file ->{
			String fileName = file.getName();
			for(String pdf : pdfs) {
				if(pdf.equals(fileName)) {
					File source = new File(PDF_DIR + "\\" + fileName);
					File dest = new File(userFolderPath + "\\" + pdf);
					try {
						Files.move(source.toPath(), dest.toPath());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
		});
		
		User user = userService.findByUsername(username);

		try {
			documentService.sendNotification(user, pdfs.length);
		} catch(NullPointerException e) {
			// return error no Email exists
		}
		return "redirect:/verteilen";
	}
	
}

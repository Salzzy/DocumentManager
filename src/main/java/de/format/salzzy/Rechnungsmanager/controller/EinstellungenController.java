package de.format.salzzy.Rechnungsmanager.controller;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.pdfbox.io.IOUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import de.format.salzzy.Rechnungsmanager.model.User;
import de.format.salzzy.Rechnungsmanager.model.UserInfo;
import de.format.salzzy.Rechnungsmanager.service.UserService;

@Controller
@RequestMapping("/app")
public class EinstellungenController {

//	private static String INPUT_DIR = "C:\\Users\\salzmann\\Desktop\\Test-Umgebung\\FIBU\\Mitarbeiter\\";
	
	@Autowired
	UserService userService;
	
	
	private User getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String currentPrincipalName = authentication.getName();
		User user = userService.findByUsername(currentPrincipalName);
		return user;
	}
	
	@GetMapping("/settings")
	public String settings(Model theModel) {
		
		User user = getCurrentUser();
		
		UserInfo userinfo = user.getUserinfo();
		
		theModel.addAttribute("userinfo", userinfo);
		
		
		
		theModel.addAttribute("signaturPic", Base64.encodeBase64String(userinfo.getSignatur()));
		
		return "app/settings";
	}
	
	
	@PostMapping("/save")
	public String save(@ModelAttribute("userinfo") UserInfo userinfo) {
		
		User user = getCurrentUser();
		
		// Muss getrennt von Signatur gespeichert werden
		userService.saveUserInfo(user, userinfo);
		
		return "redirect:/app/settings";
	}
	
	
	@PostMapping("/upload")
	public String upload(@RequestParam("file") MultipartFile imageValue) throws IOException {
		
		User user = getCurrentUser();
		
		// Stream von BLOB bilden
		FileInputStream in = (FileInputStream) imageValue.getInputStream();
		
		// in ByteArray speichern (BLOB) und in die Datenbank speichern
		byte[] signatur = IOUtils.toByteArray(in);
		user.getUserinfo().setSignatur(signatur);
		userService.saveNormal(user);
		
		
		
		return "redirect:/app/settings";
	}
	
	
}

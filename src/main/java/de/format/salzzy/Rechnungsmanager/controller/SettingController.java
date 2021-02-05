package de.format.salzzy.Rechnungsmanager.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

import de.format.salzzy.Rechnungsmanager.service.DocumentService;
import de.format.salzzy.Rechnungsmanager.service.SettingService;
import org.apache.pdfbox.io.IOUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import de.format.salzzy.Rechnungsmanager.model.auth.User;
import de.format.salzzy.Rechnungsmanager.model.UserInfo;
import de.format.salzzy.Rechnungsmanager.service.UserService;
import java.awt.*;

import javax.imageio.ImageIO;

@Controller
public class SettingController {

	private final UserService userService;
	private final DocumentService documentService;

	@Autowired
	public SettingController(UserService userService, DocumentService documentService) {
		this.userService = userService;
		this.documentService = documentService;
	}

	@GetMapping("/settings")
	public String settings(Model theModel)
	{
		User user = userService.currentLoggedInUser();
		UserInfo userinfo = user.getUserInfo();
		if (userinfo == null) userinfo = new UserInfo();

		theModel.addAttribute("userinfo", userinfo);
		theModel.addAttribute("user", user);
		theModel.addAttribute("signatureFileName", userinfo.getSignatureFileName());
		
		return "app/settings/index";
	}
	
	
	@PostMapping("/settings/userinfo")
	public String save(@ModelAttribute("userinfo") UserInfo userinfo)
	{
		userService.save(userinfo);
		return "redirect:/settings";
	}
	
	
	@PostMapping("/settings/userinfo/signature")
	public String upload(@RequestParam("image") MultipartFile signatureImage) throws IOException
	{
		User user = userService.currentLoggedInUser();
		String fullFilePath = documentService.saveFile(signatureImage, documentService.getUserDocumentUtilsPath(user));
		user.getUserInfo().setSignatureFileName(signatureImage.getOriginalFilename());
		userService.save(user);
		removeWhiteBackground(fullFilePath);
		return "redirect:/settings";
	}


	private void removeWhiteBackground(String filePath)
	{

	}


}

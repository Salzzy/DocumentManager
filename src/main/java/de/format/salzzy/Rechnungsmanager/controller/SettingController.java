package de.format.salzzy.Rechnungsmanager.controller;

import java.io.IOException;

import de.format.salzzy.Rechnungsmanager.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
	@PreAuthorize("hasAuthority('user:read')")
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
	@PreAuthorize("hasAuthority('user:write')")
	public String save(@ModelAttribute("userinfo") UserInfo userinfo)
	{
		userService.save(userinfo);
		return "redirect:/settings";
	}
	
	
	@PostMapping("/settings/userinfo/signature")
	@PreAuthorize("hasAuthority('user:write')")
	public String upload(@RequestParam("image") MultipartFile signatureImage) throws IOException
	{
		User user = userService.currentLoggedInUser();
		String fullFilePath = documentService.saveFile(signatureImage, documentService.getUserDocumentUtilsPath(user));
		user.getUserInfo().setSignatureFileName(signatureImage.getOriginalFilename());
		userService.save(user);
		return "redirect:/settings";
	}

	@PostMapping("/settings/stempel")
	@PreAuthorize("hasAuthority('fibu:write')")
	public String saveStempel()
	{

		return "redirect:/settings";
	}

}

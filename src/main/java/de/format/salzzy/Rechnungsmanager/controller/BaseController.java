package de.format.salzzy.Rechnungsmanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import de.format.salzzy.Rechnungsmanager.model.User;
import de.format.salzzy.Rechnungsmanager.model.UserInfo;
import de.format.salzzy.Rechnungsmanager.service.SecurityService;
import de.format.salzzy.Rechnungsmanager.service.UserService;

@Controller
public class BaseController {
	
	@Autowired
    private UserService userService;

    @Autowired
    private SecurityService securityService;


	@GetMapping("/login")
	public String login() {
		
		
		
		return "app/login";
	}
	
	
	@GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("userForm", new User());
        
        System.out.println("Testing");

        return "app/registration";
    }

    @PostMapping("/registration")
    public String registration(@ModelAttribute("userForm") User userForm) {
    	
    	System.out.println("Test3");
    	
        // Create new UserInfo Object and save it
    	
    	
    	

        System.out.println("Test1");
        userService.save(userForm);

        securityService.autoLogin(userForm.getUsername(), userForm.getPasswordConfirm());

        return "redirect:/app/home";
    }
	
}

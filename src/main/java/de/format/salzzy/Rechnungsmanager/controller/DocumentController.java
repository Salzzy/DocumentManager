package de.format.salzzy.Rechnungsmanager.controller;

import de.format.salzzy.Rechnungsmanager.model.Document;
import de.format.salzzy.Rechnungsmanager.service.ActivityService;
import de.format.salzzy.Rechnungsmanager.service.DocumentService;
import de.format.salzzy.Rechnungsmanager.service.UserService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentService documentService;
    private final UserService userService;
    private final ActivityService activityService;

    public DocumentController(DocumentService documentService, UserService userService, ActivityService activityService) {
        this.documentService = documentService;
        this.userService = userService;
        this.activityService = activityService;
    }


    @GetMapping
    public String index(Model model)
    {
        List<Document> documents = documentService.findAll(Sort.by(Sort.Direction.DESC, "id"));

        model.addAttribute("documents", documents);
        return "app/documents/index";
    }

}

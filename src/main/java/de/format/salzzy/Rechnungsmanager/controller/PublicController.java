package de.format.salzzy.Rechnungsmanager.controller;

import de.format.salzzy.Rechnungsmanager.model.auth.User;
import de.format.salzzy.Rechnungsmanager.service.DocumentService;
import de.format.salzzy.Rechnungsmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Controller
public class PublicController {

    private final UserService userService;
    private final DocumentService documentService;

    @Autowired
    public PublicController(UserService userService, DocumentService documentService) {
        this.userService = userService;
        this.documentService = documentService;
    }

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> getPDF(@RequestParam("name") String fileName,
                                         @RequestParam(name="verteiler", required=false) boolean verteiler) throws IOException
    {
        User user = userService.currentLoggedInUser();
        String fileNameWithoutSpace = fileName.replaceAll("_", " ");
        String filePath = documentService.getUserDocumentPath(user) + fileNameWithoutSpace;

        if(verteiler) filePath = documentService.getPublicInvoiceDocumentPath() + fileNameWithoutSpace;

        File quellFile = new File(filePath);
        byte[] encodedBase64 = Files.readAllBytes(quellFile.toPath());
        String filename = quellFile.getName();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/pdf"));
        headers.add("content-disposition", "inline;filename="+filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return new ResponseEntity<byte[]>(encodedBase64, headers, HttpStatus.OK);
    }

    @GetMapping("/image/{name}")
    public ResponseEntity<byte[]> getPNG(@PathVariable("name") String fileName) throws IOException
    {
        User user = userService.currentLoggedInUser();
        String filePath = documentService.getUserDocumentUtilsPath(user) + fileName;
        File quellFile = new File(filePath);
        byte[] encodedBase64 = Files.readAllBytes(quellFile.toPath());
        String filename = quellFile.getName();

        String mediaType = "application/png";
        if (fileName.endsWith(".jpg")) {
            mediaType = "application/jpg";
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(mediaType));
        headers.add("content-disposition", "inline;filename="+filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return new ResponseEntity<byte[]>(encodedBase64, headers, HttpStatus.OK);
    }


}

package de.format.salzzy.Rechnungsmanager.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import de.format.salzzy.Rechnungsmanager.model.Activity;
import de.format.salzzy.Rechnungsmanager.model.Document;
import de.format.salzzy.Rechnungsmanager.service.ActivityService;
import de.format.salzzy.Rechnungsmanager.service.DocumentService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.format.salzzy.Rechnungsmanager.model.auth.User;
import de.format.salzzy.Rechnungsmanager.service.UserService;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.print.Doc;

@Controller
public class VerteilerController {

	private final UserService userService;
	private final DocumentService documentService;

	@Autowired
	public VerteilerController(UserService userService,
							   DocumentService documentService)
	{
		this.userService = userService;
		this.documentService = documentService;
	}


	@GetMapping("/verteilen")
	@PreAuthorize("hasAnyRole('ROLE_FIBU', 'ROLE_ADMIN')")
	public String showDocuments(Model theModel, RedirectAttributes redirectAttributes) {
		String documentPath = null;
		try {
			documentPath = documentService.getPublicInvoiceDocumentPath();
		} catch (IOException e) {
			redirectAttributes.addFlashAttribute("error", "Der allgemeine Rechnungsordner konnte nicht gefunden/drauf zugegriffen werden.");
		}
		List<Document> documents = documentService.getAllDocumentsByStatus(1);
		List<User> users = userService.findAll();
		List<String> autoComplete = users.stream().map(User::getUsername).collect(Collectors.toList());
		
		theModel.addAttribute("users", autoComplete);
		theModel.addAttribute("documents", documents);
		
		return "app/verteilen/index";
	}


	@PostMapping("/verteilen")
	@PreAuthorize("hasAnyAuthority('fibu:write')")
	public String saveDocument(@RequestParam("file") MultipartFile mpf, RedirectAttributes redirectAttributes)
	{
		try {
			String docPath = documentService.saveFile(mpf);
		} catch (IOException e) {
			redirectAttributes.addFlashAttribute("error", "Es ist ein Fehler mit einer Datei oder dem Dateipfad unterlaufen.");
			return "redirect:/verteilen";
		} catch (NoSuchAlgorithmException ex) {
			redirectAttributes.addFlashAttribute("error", "Der Hash-Algorithmus konnte nicht ermittelt werden");
			return "redirect:/verteilen";
		}
		redirectAttributes.addFlashAttribute("success", "Datei wurde sicher abgespeichert.");
		return "redirect:/verteilen";
	}


	@DeleteMapping("/verteilen")
	@PreAuthorize("hasAnyAuthority('fibu:write')")
	public ResponseEntity<Long> deleteDocument(@RequestParam("pdfList[]") String[] pdfs)
	{
		Arrays.stream(pdfs)
			.map(pdf -> {
				Path path = null;
				try {
					path = Paths.get(documentService.getPublicInvoiceDocumentPath() + "\\" + pdf);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return path;
			})
			.forEach(pdf -> {
				try {
					Files.deleteIfExists(pdf);
				} catch (IOException e) {
					e.printStackTrace();
				}
			});

		return new ResponseEntity<>(1L, HttpStatus.OK);
	}

	
	@PostMapping("/verschieben")
	@PreAuthorize("hasAnyAuthority('fibu:write')")
	public String distributeDocument(@RequestParam("userName") String username,
									 @RequestParam("fileNameList[]") String[] fileNameList,
									 RedirectAttributes redirectAttributes)
	{
		User receiver = userService.findByUsername(username);

		// konvertieren von fileName zum Dokumentenobjekt
		List<Document> documentList = Arrays.stream(fileNameList)
				.map(fileName -> documentService.findDocumentById(Long.parseLong(fileName.split(" ")[0])))
				.collect(Collectors.toList());

		try {
			documentService.move(documentList, receiver);
		}
		catch (IOException e) {
			redirectAttributes.addFlashAttribute("error", "Eine Datei wurde nicht gefunden und konnte somit nicht verarbeitet werden.");
			return "redirect:/verteilen";
		}

		try {
			documentService.sendNotification(receiver, fileNameList.length);
		}
		catch (NullPointerException e) {
			redirectAttributes.addFlashAttribute("error", "Es konnte keine E-Mail an den Empfänger gesendet werden, <br> " +
					"da er noch keine E-Mail mit seinem Account verknüpft hat.");
			return "redirect:/verteilen";
		}

		return "redirect:/verteilen";
	}
}

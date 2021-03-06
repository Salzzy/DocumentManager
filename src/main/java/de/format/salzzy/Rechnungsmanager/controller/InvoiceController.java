package de.format.salzzy.Rechnungsmanager.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import de.format.salzzy.Rechnungsmanager.model.Document;
import de.format.salzzy.Rechnungsmanager.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.itextpdf.text.DocumentException;

import de.format.salzzy.Rechnungsmanager.Utils.PdfStempeln;
import de.format.salzzy.Rechnungsmanager.model.auth.User;
import de.format.salzzy.Rechnungsmanager.service.UserService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class InvoiceController {

	private final UserService userService;
	private final DocumentService documentService;

	private final PdfStempeln pdfStempeln;

	@Autowired
	public InvoiceController(UserService userService, DocumentService documentService, PdfStempeln pdfStempeln)
	{
		this.documentService = documentService;
		this.userService = userService;
		this.pdfStempeln = pdfStempeln;
	}

	@GetMapping("/rechnungen")
	@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_FIBU', 'ROLE_ADMIN')")
	public String index(Model theModel, RedirectAttributes redirectAttributes)
	{
		User user = userService.currentLoggedInUser();
		String userPath = null;
		try {
			userPath = documentService.getUserDocumentPath(user);
		} catch (IOException e) {
			redirectAttributes.addFlashAttribute("error", "Dein Benutzerorder konnte nicht ermittelt werden. <br>Wende dich an einen Administrator.");
		}
		List<Document> documents = documentService.getAllDocumentsByStatus(2);

		theModel.addAttribute("documents", documents);
		return "app/rechnungen/index";
	}
	
	
	@PostMapping("/rechnungUpload")
	@PreAuthorize("hasAuthority('user:write')")
	public String uploadRechnung(@RequestParam("file") MultipartFile file,
								 RedirectAttributes redirectAttributes)
	{
		User user = userService.currentLoggedInUser();
		String userFolder = null;
		try {
			userFolder = documentService.getUserDocumentPath(user);
		} catch (IOException e) {
			redirectAttributes.addFlashAttribute("error", "Dein Benutzerorder konnte nicht ermittelt werden. <br>Wende dich an einen Administrator.");
		}
		String destinationFilePath = userFolder + "\\" + file.getOriginalFilename();
		File fileDest = new File(destinationFilePath);

		try {
			file.transferTo(fileDest);
		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
		}
		
		return "redirect:/rechnungen";
	}
	
	@GetMapping("/delete")
	public String deleteInvoice(@RequestParam("id") Integer documentId) {
		
		User user = userService.currentLoggedInUser();
		// delete document by ID
		
		return "redirect:/rechnungen";
	}
	
	
	@PostMapping("/stempeln")
	@PreAuthorize("hasAuthority('user:write')")
	public String stempeln(@RequestParam(name="prs", required=false) boolean preislich,
						   @RequestParam(name="sach", required=false) boolean sachlich,
						   @RequestParam("ksst") String kostenstelle,
						   @RequestParam("name") String fileName,
						   @RequestParam("id") Document document) throws IOException
	{
		User user = userService.currentLoggedInUser();
		String fileNameWithoutWhiteSpace = fileName.replaceAll("_", " ");
		String userFolder = documentService.getUserDocumentPath(user);
		String userUtilsFolder = documentService.getUserDocumentUtilsPath(user);
		File signatureFile = new File(userUtilsFolder + user.getUserInfo().getSignatureFileName());
		byte[] signatureBytes = Files.readAllBytes(signatureFile.toPath());

		try {
			pdfStempeln.stempeln(preislich, sachlich, kostenstelle, fileNameWithoutWhiteSpace, signatureBytes);
			new File(userFolder + fileNameWithoutWhiteSpace).delete();
		} catch (IOException | DocumentException e) {
			e.printStackTrace();
		}

		return "redirect:/rechnungen";
	}
}

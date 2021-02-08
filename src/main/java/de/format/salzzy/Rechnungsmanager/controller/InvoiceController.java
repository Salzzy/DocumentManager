package de.format.salzzy.Rechnungsmanager.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

@Controller
public class InvoiceController {
	
	/*
	 * Test Ordner in dem die PDF liegt
	 * und alles andere passiert
	 */
	private static String FERTIG_DIR = "C:\\Users\\salzmann\\Desktop\\Test-Umgebung\\FIBU\\Rechnungen_Freigegeben\\";

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
	public String home(Model theModel, @RequestParam("name") Optional<String> name)
	{
		User user = userService.currentLoggedInUser();
		String userFolderPath = documentService.getUserDocumentPath(user);
		File userFolder = new File(userFolderPath);
		File[] files = userFolder.listFiles();
		List<String> fileNames = null;
		Integer amountOfFiles = null;

		if (files != null) {
			fileNames = Arrays.stream(files)
					.map(File::getName)
					.filter(fileName -> fileName.endsWith(".pdf"))
					.collect(Collectors.toList());
			amountOfFiles = fileNames.size();
		}

		theModel.addAttribute("pdfs", fileNames);
		theModel.addAttribute("anzahl", amountOfFiles);

		return "app/rechnungen/index";
	}
	
	
	@PostMapping("/rechnungUpload")
	public String uploadRechnung(@RequestParam("file") MultipartFile file,
								 @RequestParam("dateiName") String dateiName)
	{
		User user = userService.currentLoggedInUser();
		String userFolder = documentService.getUserDocumentPath(user);
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
	
	
	@GetMapping("/stempeln")
	public String stempeln(@RequestParam(name="prs", required=false) boolean preislich,
						   @RequestParam(name="sach", required=false) boolean sachlich,
						   @RequestParam("ksst") String kostenstelle,
						   @RequestParam("name") String fileName) throws IOException
	{
		User user = userService.currentLoggedInUser();
		String fileNameWithoutWhiteSpace = fileName.replaceAll("_", " ");
		String userFolder = documentService.getUserDocumentPath(user);
		String userUtilsFolder = documentService.getUserDocumentUtilsPath(user);
		File signatureFile = new File(userUtilsFolder + user.getUserInfo().getSignatureFileName());
		byte[] signatureBytes = Files.readAllBytes(signatureFile.toPath());

		try {
			pdfStempeln.stempeln(preislich, sachlich, kostenstelle, fileNameWithoutWhiteSpace, signatureBytes);
			File signedPdf = new File(FERTIG_DIR + fileNameWithoutWhiteSpace);
			new File(userFolder + fileNameWithoutWhiteSpace).delete();
		} catch (IOException | DocumentException e) {
			e.printStackTrace();
		}

		return "redirect:/rechnungen";
	}
}

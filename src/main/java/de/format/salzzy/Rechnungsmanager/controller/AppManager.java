package de.format.salzzy.Rechnungsmanager.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.format.salzzy.Rechnungsmanager.model.Document;
import de.format.salzzy.Rechnungsmanager.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.itextpdf.text.DocumentException;

import de.format.salzzy.Rechnungsmanager.Utils.PdfStempeln;
import de.format.salzzy.Rechnungsmanager.model.User;
import de.format.salzzy.Rechnungsmanager.service.UserService;

@Controller
public class AppManager {
	
	/*
	 * Test Ordner in dem die PDF liegt
	 * und alles andere passiert
	 */
	private static String FERTIG_DIR = "C:\\Users\\salzmann\\Desktop\\Test-Umgebung\\FIBU\\Rechnungen_Freigegeben\\";

	private UserService userService;
	private DocumentService documentService;

	@Autowired
	public AppManager(UserService userService, DocumentService documentService)
	{
		this.documentService = documentService;
		this.userService = userService;
	}

	@GetMapping("/dashboard")
	public String home(Model theModel, @RequestParam("name") Optional<String> name)  {
		
		User user = userService.currentLoggedInUser();
		String userFolder = documentService.getUserDocumentPath(user);
		
		// Check ob das Pdf Verzeichniss existiert
		File PDF_DIR = new File(userFolder);
		
		// Erstelle Pdf Verzeichniss
		if(!PDF_DIR.exists()) {
			PDF_DIR.mkdir();
		}
		
		// Get all pdfs im Verzeichniss des Users
		File[] files = PDF_DIR.listFiles();
		List<String> fileNames = new ArrayList<String>();
		
		// Speichere Pdf Namen in Liste
		if(files != null) {
			for(File f : files) {
				
				if(f.getName().endsWith(".pdf")) {
					fileNames.add(f.getName());
				}
			}
		} 

		theModel.addAttribute("pdfs", fileNames);
		theModel.addAttribute("anzahl", fileNames.size());

		return "app/dashboard/index";
	}
	
	
	@PostMapping("/rechnungUpload")
	public String uploadRechnung(@RequestParam("file") MultipartFile file, @RequestParam("dateiName") String dateiName) {
		
		User user = userService.currentLoggedInUser();
		String userFolder = documentService.getUserDocumentPath(user);
		String destinationFilePath = userFolder + "\\" + file.getOriginalFilename();
		
		File fileDest = new File(destinationFilePath);
		try {
			file.transferTo(fileDest);
		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
		}
		
		return "redirect:/dashboard";
	}
	
	@GetMapping("/delete")
	public String deleteInvoice(@RequestParam("id") Integer documentId) {
		
		User user = userService.currentLoggedInUser();
		// delete document by ID
		
		return "redirect:/dashboard";
	}
	
	
	@GetMapping("/stempeln")
	public String stempeln(@RequestParam(name="prs", required=false) boolean preislich,
						   @RequestParam(name="sach", required=false) boolean sachlich,
						   @RequestParam("ksst") String kostenstelle,
						   @RequestParam("name") String fileName)
	{
		User user = userService.currentLoggedInUser();
		fileName = fileName.replaceAll("_", " ");

		String userFolder = documentService.getUserDocumentPath(user);
		byte[] signaturBytes = user.getUserinfo().getSignatur();

		// Pdf Stempeln 
		try {
			PdfStempeln.Stempeln(userFolder, preislich, sachlich, kostenstelle, fileName, user.getUsername(), signaturBytes);
			File signedPdf = new File(FERTIG_DIR + fileName);

			// Datei in User Ordner löschen
			new File(userFolder + fileName).delete();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		
		return "redirect:/dashboard";
	}
	
	
	
	@GetMapping("/pdf")
	public ResponseEntity<byte[]> getPDF(@RequestParam("name") String fileName, @RequestParam(name="verteiler", required=false) boolean verteiler) throws IOException
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
	
	@GetMapping("/png")
	public ResponseEntity<byte[]> getPNG(@RequestParam("name") String fileName) throws IOException
	{
		User user = userService.currentLoggedInUser();
		String filePath = documentService.getUserDocumentPath(user) + "\\utils\\" + fileName;
		File quellFile = new File(filePath);
		byte[] encodedBase64 = Files.readAllBytes(quellFile.toPath());
		String filename = quellFile.getName();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType("application/png"));
		headers.add("content-disposition", "inline;filename="+filename);
		headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
		return new ResponseEntity<byte[]>(encodedBase64, headers, HttpStatus.OK);
	}
	
	
	@GetMapping("/jpg")
	public ResponseEntity<byte[]> getJPG(@RequestParam("name") String fileName) throws IOException
	{
		User user = userService.currentLoggedInUser();
		String filePath = documentService.getUserDocumentPath(user) + "\\utils\\" + fileName;
		File quellFile = new File(filePath);
		byte[] encodedBase64 = Files.readAllBytes(quellFile.toPath());
		String filename = quellFile.getName();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType("application/jpg"));
		headers.add("content-disposition", "inline;filename="+filename);
		headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
		return new ResponseEntity<byte[]>(encodedBase64, headers, HttpStatus.OK);
	}

	
}

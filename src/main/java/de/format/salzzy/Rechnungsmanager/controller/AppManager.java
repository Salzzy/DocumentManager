package de.format.salzzy.Rechnungsmanager.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
	private static String INPUT_DIR = "C:\\Users\\salzmann\\Desktop\\Test-Umgebung\\FIBU\\Mitarbeiter\\{placeholder}";
	private static String BACKUP_DIR = "C:\\Users\\salzmann\\Desktop\\Test-Umgebung\\FIBU\\Mitarbeiter\\{placeholder}\\Backup\\";
	private static String FERTIG_DIR = "C:\\Users\\salzmann\\Desktop\\Test-Umgebung\\FIBU\\Rechnungen_Freigegeben\\";
	
	@Autowired
	private UserService userService;

	@GetMapping("/dashboard")
	public String home(Model theModel, @RequestParam("name") Optional<String> name)  {
		
		User user = getCurrentUser();
		String userFolder = INPUT_DIR.replace("{placeholder}", user.getUsername());
		
		INPUT_DIR = INPUT_DIR.replace("{placeholder}", user.getUsername());
		BACKUP_DIR = BACKUP_DIR.replace("{placeholder}", user.getUsername());
		
		String userBackupFolder = BACKUP_DIR.replace("{placeholder}", user.getUsername());
		
		// Check ob das Pdf Verzeichniss existiert
		File PDF_DIR = new File(userFolder);
		File BACKUP_DIR = new File(userBackupFolder);
		
		// Erstelle Pdf Verzeichniss
		if(!PDF_DIR.exists()) {
			PDF_DIR.mkdir();
		}
		
		if(!BACKUP_DIR.exists()) {
			BACKUP_DIR.mkdir();
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
		
		
		// Übergebe Liste mit Namen an Frontend
		theModel.addAttribute("pdfs", fileNames);

		// Anzahl an Rechnungen im Ordner
		theModel.addAttribute("anzahl", fileNames.size());
		
		// return startseite
		return "app/dashboard/home";
	}
	
	
	@PostMapping("/rechnungUpload")
	public String uploadRechnung(@RequestParam("file") MultipartFile file, @RequestParam("dateiName") String dateiName) {
		
		User user = getCurrentUser();
		
		
		String userFolder = INPUT_DIR.replace("{placeholder}", user.getUsername());
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
	public String uploadRechnung(@RequestParam("id") String fileName) {
		
		User user = getCurrentUser();
		
		File file = new File(INPUT_DIR + "\\" + user.getUsername() + "\\" + fileName);
		file.delete();
		
		return "redirect:/dashboard";
	}
	
	
	@GetMapping("/stempeln")
	public String stempeln(@RequestParam(name="prs", required=false) boolean preislich, @RequestParam(name="sach", required=false) boolean sachlich, 
							@RequestParam("ksst") String kostenstelle, @RequestParam("name") String fileName) {
		
		User user = getCurrentUser();
		fileName = fileName.replaceAll("_", " ");
		
		// Ordner erstellen
		String userFolder = getUserFolder(user) + "\\";
		String userBackupFolder = BACKUP_DIR.replace("{placeholder}", user.getUsername());
		
		// SignaturBild als Blob aus der Datenbank erhalten
		byte[] signaturBytes = user.getUserinfo().getSignatur();

		// Pdf Stempeln 
		try {
			PdfStempeln.Stempeln(userFolder, preislich, sachlich, kostenstelle, fileName, user.getUsername(), signaturBytes);
			
			File signedPdf = new File(FERTIG_DIR + fileName);
			File backUpPdf = new File(userBackupFolder + fileName);

			System.out.println("Verschieben");
			
			// Datei kopieren
			try(FileInputStream fis = new FileInputStream(signedPdf)){
				FileOutputStream fos = new FileOutputStream(backUpPdf);
				byte[] buffer = new byte[1024];
				int length;
				while((length = fis.read(buffer)) > 0) {
					fos.write(buffer, 0, length);
				}
				fos.close();
			}
			
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
	public ResponseEntity<byte[]> getPDF(@RequestParam("name") String fileName, @RequestParam(name="verteiler", required=false) boolean verteiler) throws IOException {
		
		User user = getCurrentUser();
		
		fileName = fileName.replaceAll("_", " ");
		
		String filePath = getUserFolder(user) + "\\" + fileName;
		if(verteiler) {
			filePath = "C:\\Users\\salzmann\\Desktop\\Test-Umgebung\\FIBU\\Rechnungen\\" + fileName;
		}
		File quellFile = new File(filePath);
		
		byte[] encodedBase64 = Files.readAllBytes(quellFile.toPath());
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType("application/pdf"));
		String filename = quellFile.getName();
		
		headers.add("content-disposition", "inline;filename="+filename);
		headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
		ResponseEntity<byte[]> response = new ResponseEntity<byte[]>(encodedBase64, headers, HttpStatus.OK);

		return response;
	}
	
	@GetMapping("/png")
	public ResponseEntity<byte[]> getPNG(@RequestParam("name") String fileName) throws IOException {
		
		User user = getCurrentUser();
		
		String filePath = getUserFolder(user) + "\\utils\\" + fileName;
		File quellFile = new File(filePath);
		
		byte[] encodedBase64 = Files.readAllBytes(quellFile.toPath());
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType("application/png"));
		String filename = quellFile.getName();
		
		headers.add("content-disposition", "inline;filename="+filename);
		
		headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
		ResponseEntity<byte[]> response = new ResponseEntity<byte[]>(encodedBase64, headers, HttpStatus.OK);
		
		return response;
	}
	
	
	@GetMapping("/jpg")
	public ResponseEntity<byte[]> getJPG(@RequestParam("name") String fileName) throws IOException {
		
		User user = getCurrentUser();
		
		String filePath = getUserFolder(user) + "\\utils\\" + fileName;
		File quellFile = new File(filePath);
		
		byte[] encodedBase64 = Files.readAllBytes(quellFile.toPath());
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType("application/jpg"));
		String filename = quellFile.getName();
		
		headers.add("content-disposition", "inline;filename="+filename);
		
		headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
		ResponseEntity<byte[]> response = new ResponseEntity<byte[]>(encodedBase64, headers, HttpStatus.OK);
		
		return response;
	}
	
	
	private User getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String currentPrincipalName = authentication.getName();
		User user = userService.findByUsername(currentPrincipalName);
		return user;
	}
	
	private String getUserFolder(User user) {
		String userFolder = INPUT_DIR.replace("{placeholder}", user.getUsername());	
		return userFolder;
	}
	
}

package de.format.salzzy.Rechnungsmanager.Utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import de.format.salzzy.Rechnungsmanager.model.auth.User;
import de.format.salzzy.Rechnungsmanager.service.DocumentService;
import de.format.salzzy.Rechnungsmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PdfStempeln {

	private final DocumentService documentService;
	private final UserService userService;

	@Autowired
	public PdfStempeln(DocumentService documentService, UserService userService)
	{
		this.documentService = documentService;
		this.userService = userService;
	}

	/**
	 * Zeichnet den "Rechnungsprüfung" Stempel auf die Pdf. <br>
	 * Danach wird die Pdf in den Freigegeben Ordner verschoben
	 *
	 * @throws IOException Wenn Dateipfad/e konnten nicht gefunden werden
	 * @param sachlich Um Sachlich-Haken zu setzen
	 * @param preislich Um Preislich-Haken zu setzen
	 * @param kostenstelle Name der kostenstelle
	 * @param fileName Name + Endung der Rechnungsdatei
	 * @param signaturBytes Blob aus der DB (Signatur)
	 **/
	public void stempeln(boolean sachlich, boolean preislich,
						 String kostenstelle, String fileName,
						 byte[] signaturBytes) throws IOException, DocumentException
	{
		Date now = new Date();
		SimpleDateFormat sf = new SimpleDateFormat("dd.MM.yyyy");
		User user = userService.currentLoggedInUser();
		String dateString = sf.format(now);
		String stempPicPath = documentService.getSystemDocumentPath() + "stempel-digital.png";
		String checkPicPath = documentService.getSystemDocumentPath() + "check-mark.png";
		String pdfToStemp = documentService.getUserDocumentPath(user) + fileName;
		
		// Dateien laden
		BufferedImage signatur = ImageIO.read(new ByteArrayInputStream(signaturBytes));
		BufferedImage stamp = ImageIO.read(new File(stempPicPath));
		BufferedImage haken = ImageIO.read(new File(checkPicPath));
		
		// Pdf Seiten umwandlen in Bilderliste
		List<BufferedImage> pdfImages = ToImage.umwandel(pdfToStemp);
		
		// Erstes Bild von der Pdf erhalten
		BufferedImage picFromPDF = pdfImages.get(0);
		Graphics g2d = stamp.createGraphics();

		// Datum platzieren
		g2d.setFont(new Font("Arial", Font.BOLD, 32));
		g2d.setColor(Color.BLACK);
		g2d.drawString(dateString, 20, (stamp.getHeight() - (32 + 15)));

		// Signatur auf dem ersten Bild der Pdf platzieren
		g2d.drawImage(signatur, 200, (stamp.getHeight() - (signatur.getHeight() + 35)), null);

		// Kostenstelle platzieren
		g2d.setFont(new Font("Arial", Font.BOLD, 38));
		g2d.setColor(Color.BLACK);
		g2d.drawString(kostenstelle, 320, 120);
		
		// Sachlich Haken platzieren
		if(sachlich) g2d.drawImage(haken, 370, 160, null);
		// Preislich Haken platzieren
		if(preislich) g2d.drawImage(haken, 370, 205,  null);

		// Stempel Image "schließen"
		g2d.dispose();

		// Fertigen Stempel auf erste Pdfseite drucken
		g2d = picFromPDF.createGraphics();
		g2d.drawImage(stamp, 15, picFromPDF.getHeight() - stamp.getHeight() - 305, null);
		g2d.dispose();

		// PDF wieder Zusammensetzen und abspeichern in neuen Pfad
		Document document = new Document(PageSize.A4, 20, 20, 20, 20);   // Create document with a4 Size
		PdfWriter.getInstance(document, new FileOutputStream(documentService.getPublicInvoiceDocumentPath() + "/Freigegeben/" + fileName));  // Create pdf Writer to set pdf options
		document.open();
		
		for(BufferedImage img : pdfImages){
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(img, "png", baos);
            Image tempImage = Image.getInstance(baos.toByteArray());
            tempImage.scaleToFit(595, 842); // A4 Scaling
            tempImage.setAbsolutePosition(0, 0); // on Postition
            document.add(tempImage); // add to document pdf
            document.newPage();
        }
		document.close(); // Pdf schließen
	}
	
}

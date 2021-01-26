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

public class PdfStempeln {

	// Pfade der benutzten Bilder
	private static String STAMP_PIC_PATH = "C:\\Users\\salzmann\\Desktop\\Test-Umgebung\\test\\stempel-transparent.png";
	private static String CHECK_HOOK_PIC_PATH = "C:\\Users\\salzmann\\Desktop\\Test-Umgebung\\test\\check-mark.png";
	
	
	// Pfad in der die Fertige Rechnung abgelegt wird
	private static String RECHNUNG_PFAD = "C:\\Users\\salzmann\\Desktop\\Test-Umgebung\\FIBU\\Rechnungen_Freigegeben\\";
	
	
	/*
	 * Erhält: Pfad zu dem Verzeichniss
	 * 
	 * 
	 * Variablen Definition
	 * path: Pfad des Userverzeichnis in der die Rechnungen liegen
	 * sachlich: Um Sachlich-Haken zu setzen
	 * preislich: Um Preislich-Haken zu setzen
	 * kostenstelle: Name der kostenstelle
	 * fileName: Name + Endung der Rechnungsdatei
	 * username: Benutername des Users
	 * signaturBytes: Blob aus der DB (Signatur)
	 */
	public static void Stempeln(String path, boolean sachlich, boolean preislich,
								String kostenstelle, String fileName, String username, byte[] signaturBytes)  throws IOException, DocumentException {
		
		// Aktuelles Datum erstellen
		Date now = new Date();
		SimpleDateFormat sf = new SimpleDateFormat("dd.MM.yyyy");
		String dateString = sf.format(now);
		
		// Erstelle String für Datei => Zu stempelnde Pdf
		// Kompletter Dateipfad + Datei
		String TO_STAMP_PDF = path + fileName;
		
		// Lade Signatur von Bytes aus Datenbank
		BufferedImage signatur = ImageIO.read(new ByteArrayInputStream(signaturBytes));
		
		// Lade stempel aus Datei
		BufferedImage stamp = ImageIO.read(
				new File(STAMP_PIC_PATH));
		
		// Lade haken aus Datei
		BufferedImage haken = ImageIO.read(
				new File(CHECK_HOOK_PIC_PATH));
		
		// Lade Liste mit Images von einer PDF
		// Pdf wird in Bilder umgewandelt
		List<BufferedImage> pdfImages = ToImage.umwandel(TO_STAMP_PDF);
		
		// Erstes Bild von der Pdf erhalten
		BufferedImage picFromPDF = pdfImages.get(0);
	
		Graphics g2d = stamp.createGraphics();
		
		// Datum platzieren
		g2d.setFont(new Font("Arial", Font.BOLD, 32));
		g2d.setColor(Color.BLACK);
		g2d.drawString(dateString, 40, (stamp.getHeight() - signatur.getHeight() + 110));
		
		// Signatur auf dem ersten Bild der Pdf platzieren
		g2d.drawImage(signatur, 200, (stamp.getHeight() - signatur.getHeight() - 50), null);
		
		// Kostenstelle platzieren
		g2d.setFont(new Font("Arial", Font.BOLD, 38));
		g2d.setColor(Color.BLACK);
		g2d.drawString(kostenstelle, 320, 143);
		
		// Sachlich Haken platzieren
		if(sachlich) {
			g2d.drawImage(haken, 350, 170, null);
		}
		// Preislich Haken platzieren
		// (name, <>, I, null)
		if(preislich) {
			g2d.drawImage(haken, 350, 220, null);
		}
		
		// Stempel Image "schließen"
		g2d.dispose();
		
		g2d = picFromPDF.createGraphics();
		g2d.drawImage(stamp, 50, picFromPDF.getHeight() - stamp.getHeight(), null);
		g2d.dispose();

		
		// PDF wieder Zusammensetzen und abspeichern in neuen Pfad
		Document document = new Document(PageSize.A4, 20, 20, 20, 20);   // Create document with a4 Size
		PdfWriter.getInstance(document, new FileOutputStream(RECHNUNG_PFAD + fileName));  // Create pdf Writer to set pdf options
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
		
		// Pdf schließen
		document.close();
				
	}
	
}

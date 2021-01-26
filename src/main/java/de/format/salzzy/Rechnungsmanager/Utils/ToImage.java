package de.format.salzzy.Rechnungsmanager.Utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

public class ToImage {

	public static ArrayList<BufferedImage> umwandel(String filePath) {
		
		// Erstelle ArrayList von BufferedImages
		// Pdf-Seiten werden hier rein gespeichert
		ArrayList<BufferedImage> pdfPages = new ArrayList<BufferedImage>();
		
		// Erstelle Pdfdatei-Objekt dessen Seiten verarbeitet werden sollen
		File pdf = new File(filePath);
		
		// Lade Pdf Dokument und wandle es in BufferedImages um
		try(final PDDocument document = PDDocument.load(pdf)){
			
			PDFRenderer pdfRenderer = new PDFRenderer(document);
			for(int page = 0; page < document.getNumberOfPages(); ++page) {
				
				BufferedImage pdfPage = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);
				pdfPages.add(pdfPage);
			}
			document.close();
			
		} catch (IOException e) {
			System.err.println("Exception while trying to create pdf document - " + e);
		}
		
		// Rückgabe der Liste mit Pdfseiten als Bildern
		return pdfPages;
	}
	
}

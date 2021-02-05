package de.format.salzzy.Rechnungsmanager.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import de.format.salzzy.Rechnungsmanager.model.auth.User;
import org.springframework.web.multipart.MultipartFile;

public interface DocumentService {

	/**
	 * Das root-Verzeichniss in dem alle
	 * Dateien gespeichert werden
	 * @return String
	 */
	String getDocumentPath();

	/**
	 * Hier werden die Rechnungen abgelegt, die von der FIBU
	 * verteilt werden müssen
	 * @return String
	 */
	String getPublicInvoiceDocumentPath();

	/**
	 * Der User ordner, hier sind Rechnungen enthalten,
	 * die der User noch freigeben muss etc..
	 * @param user
	 * @return String
	 */
	String getUserDocumentPath(User user);

	/**
	 * Der User utils ordner, signaturen etc enthalten
	 * @param user
	 * @return String
	 */
	String getUserDocumentUtilsPath(User user);

	/**
	 * Gibt eine Liste mit den Namen aller Dateien aus dem Ordner zurück.
	 * @param folder Ordner in dem die Dateien gesucht werden
	 * @return List<String>
	 */
	List<String> getFileNames(File folder);
	
	void sendNotification(User user, Integer anzahl);

	/**
	 * Speichert die hochgeladene Datei in Filesystem
	 * und in der Datenbank ab.
	 * @param file uploaded Document
	 * @return java.lang.String
	 * @throws IOException
	 */
	String saveFile(MultipartFile file) throws IOException;

	/**
	 * Speichert die hochgeladene Datei in Filesystem
	 * und in der Datenbank ab.
	 * @param file uploaded Document
	 * @param path der Pfad in der die Datei gespeichert wird
	 * @return java.lang.String
	 * @throws IOException
	 */
	String saveFile(MultipartFile file, String path) throws IOException;

	// delete document

	// utils folder of user => username/utils


}

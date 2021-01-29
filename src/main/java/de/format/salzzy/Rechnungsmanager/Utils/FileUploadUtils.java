package de.format.salzzy.Rechnungsmanager.Utils;

import de.format.salzzy.Rechnungsmanager.service.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Component
public class FileUploadUtils {

    @Autowired
    private SettingService settingService;

    /**
     * Speichert eine Datei im angegebenen Verzeichnis.<br>
     * Wenn das Verzeichnis nicht existiert wird es erstellt.
     * @param uploadDir Pfad zum Verzeichnis
     * @param fileName Der Dateiname
     * @param multipartFile Die hochgeladene Datei
     * @throws IOException
     */
    public void saveFile(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException {
        Path baseDir = Paths.get(settingService.getSetting().getDocumentPath());
        if (!Files.exists(baseDir)){
            Files.createDirectory(baseDir);
        }

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)){
            Files.createDirectory(uploadPath);
        }

        InputStream inputStream = multipartFile.getInputStream();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
    }

}

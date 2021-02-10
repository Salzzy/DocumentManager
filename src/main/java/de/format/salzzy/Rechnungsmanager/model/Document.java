package de.format.salzzy.Rechnungsmanager.model;


import de.format.salzzy.Rechnungsmanager.model.auth.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;

import static com.google.common.io.Files.toByteArray;
import static java.nio.charset.StandardCharsets.UTF_8;

@Entity
@Table(name = "documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String hash;

    private String previousHash;

    private String fileName;

    private String documentPath;

    @Temporal(TemporalType.DATE)
    private Date timestamp;

    private int nonce;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = true)
    private User owner;

    public Document(String previousHash, String fileName, String documentPath, User owner) throws IOException, NoSuchAlgorithmException {
        this.previousHash = previousHash;
        this.fileName = fileName;
        this.documentPath = documentPath;
        this.timestamp = new Date();
        this.owner = owner;
        this.nonce = 1;
        this.hash = calculateDocumentHash();
    }

    private String calculateDocumentHash() throws IOException, NoSuchAlgorithmException {
        MessageDigest messageDigest = null;
        byte[] bytes = null;
        String documentBlockHash = "";

        while (!documentBlockHash.startsWith("00")){
            this.nonce++;
            String dataToHash = previousHash
                    + Long.toString(timestamp.getTime())
                    + Integer.toString(nonce)
                    + fileName
                    + documentPath
                    + fileHash();

            messageDigest = MessageDigest.getInstance("SHA-256");
            bytes = messageDigest.digest(dataToHash.getBytes(UTF_8));

            documentBlockHash = buildStringOfHash(bytes);
        }
        return documentBlockHash;
    }

    private String fileHash() throws IOException, NoSuchAlgorithmException {
        Path file = Paths.get(documentPath);
        byte[] fileToBytes = null;
        MessageDigest messageDigest = null;

        fileToBytes = toByteArray(file.toFile());
        messageDigest = MessageDigest.getInstance("SHA-256");
        fileToBytes = messageDigest.digest(fileToBytes);

        return buildStringOfHash(fileToBytes);
    }

    private String buildStringOfHash(byte[] bytes)
    {
        StringBuilder buffer = new StringBuilder();
        if (bytes != null) {
            for (byte b : bytes) {
                // Byte konvertiert zu Hexadecimalen wert
                buffer.append(String.format("%02x", b));
            }
        }
        return buffer.toString();
    }

}

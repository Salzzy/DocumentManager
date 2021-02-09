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

    public Document(String previousHash, String fileName, String documentPath, int nonce, User owner) {
        this.previousHash = previousHash;
        this.fileName = fileName;
        this.documentPath = documentPath;
        this.timestamp = new Date();
        this.nonce = nonce;
        this.owner = owner;
        this.hash = calculateDocumentHash();
    }

    private String calculateDocumentHash()
    {
        String dataToHash = previousHash
                + Long.toString(timestamp.getTime())
                + Integer.toString(nonce)
                + fileName
                + documentPath
                + fileHash();
        // System.out.printf("FileHash: %s%n", fileHash());
        MessageDigest messageDigest = null;
        byte[] bytes =null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            bytes = messageDigest.digest(dataToHash.getBytes(UTF_8));
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return buildStringOfHash(bytes);
    }

    private String fileHash()
    {
        Path file = Paths.get(documentPath);
        byte[] fileToBytes = null;
        try {
            fileToBytes = toByteArray(file.toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            fileToBytes = messageDigest.digest(fileToBytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return buildStringOfHash(fileToBytes);
    }

    private String buildStringOfHash(byte[] bytes)
    {
        StringBuilder buffer = new StringBuilder();
        if (bytes != null) {
            for (byte b : bytes) {
                // Byte konvertiert zu Hexadecimalen wert
                buffer.append(String.format("%02x", b));
                // System.out.printf("The Hexadecimal Byte: %02x%n The corresponding String: %s%n", b, buffer.toString().charAt(buffer.toString().length()-1));
            }
        }
        return buffer.toString();
    }

}

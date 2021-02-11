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
import java.util.List;
import java.util.Objects;
import java.util.Set;

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

    private String fileName;

    private String documentPath;

    @Temporal(TemporalType.DATE)
    private Date timestamp;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = true)
    private User owner;

    @OneToMany(mappedBy = "document")
    private Set<Activity> activitySet;

    public Document(String fileName, String documentPath, User owner) {
        this.fileName = fileName;
        this.documentPath = documentPath;
        this.timestamp = new Date();
        this.owner = owner;
    }

}

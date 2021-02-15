package de.format.salzzy.Rechnungsmanager.model;

import de.format.salzzy.Rechnungsmanager.model.auth.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Set;

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

    @Column(name = "status", columnDefinition = "TINYINT(1)")
    private Integer status;

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
        this.status = 1;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Path toPath() {
        return Paths.get(this.documentPath + this.fileName);
    }

}

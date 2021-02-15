package de.format.salzzy.Rechnungsmanager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.format.salzzy.Rechnungsmanager.model.auth.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "activities")
@Getter
@Setter
@NoArgsConstructor
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String action;

    @Temporal(TemporalType.DATE)
    private Date timestamp;

    @ManyToOne
    @JoinColumn(name = "documentId", nullable = false)
    private Document document;

    @ManyToOne
    @JoinColumn(name = "receiverId", nullable = true)
    private User receiver;

    public Activity(String action, Document document, User receiver) {
        this.action = action;
        this.document = document;
        this.receiver = receiver;
    }

    public Activity(String action, Document document) {
        this.action = action;
        this.document = document;
    }
}

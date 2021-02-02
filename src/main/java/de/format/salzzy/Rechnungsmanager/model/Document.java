package de.format.salzzy.Rechnungsmanager.model;


import de.format.salzzy.Rechnungsmanager.model.auth.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String documentPath;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = true)
    private User owner;

}

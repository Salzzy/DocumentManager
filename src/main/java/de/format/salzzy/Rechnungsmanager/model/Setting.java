package de.format.salzzy.Rechnungsmanager.model;

import javax.persistence.*;

@Entity
@Table(name = "settings")
public class Setting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String documentPath;

    

}

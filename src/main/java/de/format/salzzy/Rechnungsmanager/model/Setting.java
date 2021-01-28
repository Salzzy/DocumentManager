package de.format.salzzy.Rechnungsmanager.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "settings")
@Getter
@Setter
@NoArgsConstructor
public class Setting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String documentPath;

    public String getDocumentUserPath(User user){
        return this.documentPath + user.getUsername();
    }

    

}

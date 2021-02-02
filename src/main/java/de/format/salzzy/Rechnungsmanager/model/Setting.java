package de.format.salzzy.Rechnungsmanager.model;

import de.format.salzzy.Rechnungsmanager.model.auth.User;
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
        return this.documentPath + "Mitarbeiter/" + user.getUsername() + "/";
    }

    public String getDocumentUserUtilPath(User user){
        return this.documentPath + "Mitarbeiter/" + user.getUsername() + "/Utils/";
    }

    public String getDocumentInvoicePath(){
        return this.documentPath + "Rechnungen/";
    }

}

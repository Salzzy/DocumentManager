package de.format.salzzy.Rechnungsmanager.model;

import java.util.Collection;
import java.util.Set;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name="users")
@Getter
@Setter
public class User{
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	private String username;
	
	private String password;
	
	@Transient
    private String passwordConfirm;
	
	@OneToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="infoId", referencedColumnName="user_id", insertable=true, updatable=true)
	private UserInfo userinfo;

	@OneToMany(mappedBy = "owner")
	private Set<Document> document;
	
	@ManyToMany
	private Set<Role> roles;

	

}

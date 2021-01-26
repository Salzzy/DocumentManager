package de.format.salzzy.Rechnungsmanager.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="user_info")
public class UserInfo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="user_id")
	private Long id;
	
	@Column(name="email")
	private String email;
	
	@Column(name="abteilung")
	private String abteilung;
	
	@Column(name="telefonnr")
	private String telefonnr;
	
	@Column(name="signatur")
	@Lob
	private byte[] signatur;
	
	@OneToOne(mappedBy = "userinfo")
	private User user;
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAbteilung() {
		return abteilung;
	}

	public void setAbteilung(String abteilung) {
		this.abteilung = abteilung;
	}

	public String getTelefonnr() {
		return telefonnr;
	}

	public void setTelefonnr(String telefonnr) {
		this.telefonnr = telefonnr;
	}

	public byte[] getSignatur() {
		return signatur;
	}

	public void setSignatur(byte[] signatur) {
		this.signatur = signatur;
	}

	
	
	
	
}

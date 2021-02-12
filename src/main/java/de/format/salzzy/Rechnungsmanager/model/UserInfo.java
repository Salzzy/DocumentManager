package de.format.salzzy.Rechnungsmanager.model;

import de.format.salzzy.Rechnungsmanager.model.auth.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.GenerationType.*;

@Entity
@Table(name="user_info")
@Getter
@Setter
@NoArgsConstructor
public class UserInfo {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name="id")
	private Long id;

	@Column(name = "email", unique = true)
	private String email;

	@Column(name = "abteilung")
	private String abteilung;

	@Column(name = "telefon")
	private String telefon;

	@Column(name = "signature_file_name")
	private String signatureFileName;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	public UserInfo(String email, String abteilung, String telefon, String signatureFileName, User user) {
		this.email = email;
		this.abteilung = abteilung;
		this.telefon = telefon;
		this.signatureFileName = signatureFileName;
		this.user = user;
	}
}

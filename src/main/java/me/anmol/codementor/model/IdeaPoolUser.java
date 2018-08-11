package me.anmol.codementor.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.apache.commons.lang.StringUtils;

@Entity
public class IdeaPoolUser implements Serializable {
	
	private static final long serialVersionUID = 6405069048465109433L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@Column(unique = true)
	private String email;
	
	private String name;
	
	private String password;
		
	private String avatar_url;

	public IdeaPoolUser(String email, String name, String password) {
		super();
		this.email = validatedEmail(email);
		this.name = validatedString(name,"Name");
		this.password = validatedString(password,"Password");
		this.avatar_url = generateGravatarUrl(email);
	}
	
	IdeaPoolUser() {
		super();		
	}

	public static String generateGravatarUrl(String email) {
		String url = "https://www.gravatar.com/avatar/" + MD5Util.md5Hex(email);
		return url;
	}

	private String validatedString(String name, String fieldName) {
		if(StringUtils.isEmpty(name)){
			throw new InvalidDataException(fieldName + " must not be null or empty.");
		}
		return name;
	}

	private String validatedEmail(String email) {
		if(email == null){
			throw new InvalidDataException("Null email received");
		}
		return email;
	}

	public String getEmail() {
		return email;
	}

	public String getName() {
		return name;
	}

	public long id() {
		return id;
	}

	public String password() {		
		return password;
	}

	public String getAvatar_url() {
		return avatar_url;
	}

}

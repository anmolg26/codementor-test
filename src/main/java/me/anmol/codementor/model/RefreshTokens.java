package me.anmol.codementor.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;

@Entity
public class RefreshTokens implements Serializable {
	
	private static final long serialVersionUID = 499683708379654727L;

	@Id
	private long userId;
	
	@ElementCollection(fetch = FetchType.EAGER)
	private Set<String> tokens;
	
	public RefreshTokens(long userId) {
		super();
		this.userId = userId;
		this.tokens = new HashSet<>();			
	}

	RefreshTokens() {
		super();
	}
		
	public String generateNewRefreshToken(){		
		String token = UUID.randomUUID().toString();
		this.tokens.add(token);
		return token;
	}

	public long getUserId() {
		return userId;
	}

	public Set<String> getTokens() {
		return tokens;
	}
	
}

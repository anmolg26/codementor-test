package me.anmol.codementor.model;

public class JWTWrapper {
	
	private String jwt;
	
	private String refresh_token;

	public String getJwt() {
		return jwt;
	}

	public String getRefresh_token() {
		return refresh_token;
	}

	public void setJwt(String jwt) {
		this.jwt = jwt;
	}

	public void setRefresh_token(String refresh_token) {
		this.refresh_token = refresh_token;
	}
	
}

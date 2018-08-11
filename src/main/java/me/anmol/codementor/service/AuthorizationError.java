package me.anmol.codementor.service;

public class AuthorizationError extends Exception {

	private static final long serialVersionUID = 7760168726692643577L;

	public AuthorizationError(String message) {
		super(message);	
	}

}

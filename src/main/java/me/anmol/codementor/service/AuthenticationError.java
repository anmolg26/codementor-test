package me.anmol.codementor.service;

public class AuthenticationError extends Exception {

	private static final long serialVersionUID = 1L;

	public AuthenticationError(String message) {
		super(message);	
	}

}

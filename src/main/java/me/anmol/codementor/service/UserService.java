package me.anmol.codementor.service;

import java.util.Objects;
import me.anmol.codementor.model.JWTWrapper;
import me.anmol.codementor.model.IdeaPoolUser;

public class UserService {

	private UserAuthenticationService authenticationService;

	private GenericDAO<IdeaPoolUser, Long> userDao;

	public UserService(UserAuthenticationService authenticationService, GenericDAO<IdeaPoolUser, Long> userDao) {
		super();
		this.authenticationService = Objects.requireNonNull(authenticationService,
				"Authentication service must not be null.");
		this.userDao = Objects.requireNonNull(userDao, "User dao must not be null.");
	}

	public JWTWrapper createUser(String email, String name, String password) {
		validatePassword(password);
		String hashedPassword = authenticationService.generateHashedPassword(password);
		IdeaPoolUser user = new IdeaPoolUser(email, name, hashedPassword);
		user = userDao.persist(user);
		try {
			return authenticationService.authenticate(email, password);
		} catch (AuthenticationError e) {			
			throw new IllegalStateException("This should not have happened");
		}
	}

	public IdeaPoolUser getUser(long id) {
		return userDao.findById(id);
	}
	
	private void validatePassword(String password) {
		boolean isValid = true;
		System.out.println("Password received as: " + password);
		if (password.length() < 8) {
			System.out.println("Length determined less than 8: " + password);
			isValid = false;
		}
		if(isValid){
			boolean lowerCase = false;
			boolean upperCase = false;
			boolean number = false;
			for (int i = 0; i < password.length(); i++){
				char c = password.charAt(i);
				System.out.println("Checking character: " + c);
				if(!lowerCase){
					if(Character.isLowerCase(c)){
						System.out.println("It was lower case: " + c);
						lowerCase = true;
					}
				}
				if(!upperCase){
					if(Character.isUpperCase(c)){
						System.out.println("It was upper case: " + c);
						upperCase = true;
					}				
				}
				if(!number){
					if(Character.isDigit(c)){
						System.out.println("It was digit: " + c);
						number = true;
					}
				}		    		    		  
			}
			if(!upperCase || !lowerCase || !number){
				isValid = false;
			}			
		}						
		if (!isValid) {
			throw new IllegalArgumentException(
					"Password must contain one uppercase, one lowecase and one number. Also, it must be of minimum 8 characters.");
		}

	}

}

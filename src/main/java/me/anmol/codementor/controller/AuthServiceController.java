package me.anmol.codementor.controller;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import me.anmol.codementor.model.JWTWrapper;
import me.anmol.codementor.service.AuthenticationError;
import me.anmol.codementor.service.UserAuthenticationService;

@RestController
@RequestMapping(value = "/access-tokens")
public class AuthServiceController {
	
	@Inject
	private UserAuthenticationService authService;
	
	@RequestMapping(method = RequestMethod.POST)
	public JWTWrapper login(@RequestBody UserWrapper wrapper, HttpServletRequest request, HttpServletResponse response) throws IOException{
		JWTWrapper jwtWrapper;
		try {
			jwtWrapper = authService.authenticate(wrapper.getEmail(), wrapper.getPassword());
			response.setStatus(201);
			return jwtWrapper;
		} catch (AuthenticationError e) {
			response.sendError(401);
			return null;
		}
	}
	
	@RequestMapping(value = "/refresh", method = RequestMethod.POST)
	public JWTWrapper refreshToken(@RequestBody RefreshTokenWrapper refreshTokenWrapper, HttpServletResponse response) throws IOException{		
		String token;
		try {
			token = authService.getValidToken(refreshTokenWrapper.refresh_token);
			JWTWrapper wrapper = new JWTWrapper();
			wrapper.setJwt(token);
			return wrapper;
		} catch (AuthenticationError e) {
			response.sendError(401);
			return null;
		}
	}
	
	@RequestMapping(method = RequestMethod.DELETE)
	public void logout(@RequestBody RefreshTokenWrapper refreshTokenWrapper, HttpServletResponse response, HttpServletRequest request){
		String accessToken = request.getHeader("X-Access-Token");
		authService.logout(refreshTokenWrapper.refresh_token, accessToken);
		response.setStatus(204);
	}
	
	public static class RefreshTokenWrapper {
		
		private String refresh_token;

		public String getRefresh_token() {
			return refresh_token;
		}

		public void setRefresh_token(String refresh_token) {
			this.refresh_token = refresh_token;
		}			
	}
	
	

}

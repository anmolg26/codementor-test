package me.anmol.codementor.controller;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import me.anmol.codementor.model.JWTWrapper;
import me.anmol.codementor.model.IdeaPoolUser;
import me.anmol.codementor.service.UserService;

@RestController
@RequestMapping(value = "/")
public class UserServiceController {
	
	@Inject
	private UserService userService;		
	
	@RequestMapping(method = RequestMethod.POST, value = "/users")
	private JWTWrapper signUp(@RequestBody UserWrapper wrapper, HttpServletRequest request, HttpServletResponse response){
		JWTWrapper jwtWrapper = userService.createUser(wrapper.getEmail(), wrapper.getName(), wrapper.getPassword());
		response.setStatus(201);
		return jwtWrapper;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/me")
	public IdeaPoolUser getUser(HttpServletRequest request, HttpServletResponse response){
		long userId = FilterUtils.getUserId(request);
		IdeaPoolUser user = userService.getUser(userId);
		response.setStatus(200);
		return user;
	}

}

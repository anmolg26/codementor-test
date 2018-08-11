package me.anmol.codementor.controller;

import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import me.anmol.codementor.model.Idea;
import me.anmol.codementor.service.AuthorizationError;
import me.anmol.codementor.service.IdeaService;

@RestController
@RequestMapping(value = "/ideas")
public class IdeaServiceController {

	@Inject
	private IdeaService ideaService;

	@RequestMapping(method = RequestMethod.POST)
	public Idea createIdea(@RequestBody IdeaWrapper wrapper, HttpServletRequest request, HttpServletResponse response) {
		long id = FilterUtils.getUserId(request);
		Idea idea = ideaService.createIdea(wrapper.getContent(), wrapper.getImpact(), wrapper.getEase(),
				wrapper.getConfidence(), id);
		response.setStatus(201);
		return idea;
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void deleteIdea(@PathVariable String id, HttpServletRequest request, HttpServletResponse response){		
		long userId = FilterUtils.getUserId(request);
		try {
			ideaService.deleteIdea(userId,id);
			response.setStatus(204);
		} catch (AuthorizationError e) {			
			response.setStatus(403);
		}		
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public List<Idea> getIdeas(@RequestParam int page, HttpServletRequest request, HttpServletResponse response){
		long userId = FilterUtils.getUserId(request);
		response.setStatus(200);
		return ideaService.getIdeas(userId, page);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public Idea updateIdea(@RequestBody IdeaWrapper wrapper,@PathVariable String id, HttpServletRequest request, HttpServletResponse response){	
		long userId = FilterUtils.getUserId(request);
		response.setStatus(200);
		
		try {
			Idea updatedIdea = ideaService.updateIdea(userId, id, wrapper.getContent(), wrapper.getImpact(), wrapper.getEase(), wrapper.getConfidence());
			response.setStatus(200);
			return updatedIdea;
		} catch (AuthorizationError e) {			
			response.setStatus(403);
			return null;
		}
	}

}

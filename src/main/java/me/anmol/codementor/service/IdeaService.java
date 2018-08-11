package me.anmol.codementor.service;

import java.util.List;
import java.util.Objects;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import me.anmol.codementor.model.Idea;
import me.anmol.codementor.model.Idea_;
import me.anmol.codementor.model.IdeaPoolUser;
import me.anmol.codementor.model.IdeaPoolUser_;

public class IdeaService {

	private GenericDAO<IdeaPoolUser, Long> userDao;
	
	private GenericDAO<Idea, String> ideaDao;
	
	public IdeaService(GenericDAO<IdeaPoolUser, Long> userDao, GenericDAO<Idea, String> ideaDao) {
		super();
		this.userDao = Objects.requireNonNull(userDao,"User dao must not be null.");
		this.ideaDao = Objects.requireNonNull(ideaDao, "Idea dao must not be null.");
	}

	public Idea createIdea(String content, int impact, int ease, int confidence, long userId) {
		IdeaPoolUser user = userDao.findById(userId);
		Idea idea = new Idea(content, impact, ease, confidence, user);
		return ideaDao.persist(idea);		
	}
	
	public void deleteIdea(long userId, String id) throws AuthorizationError{
		Idea idea = ideaDao.findById(id);
		if(idea != null && idea.userId() == userId){
			ideaDao.delete(id);
		}
		else {
			throw new AuthorizationError("Unauthorized access");
		}
	}
	
	public List<Idea> getIdeas(long userId, int page){		
		ReadCriteria<Idea> readCriteria = (CriteriaBuilder criteriaBuilder) -> {
			CriteriaQuery<Idea> criteriaQuery = criteriaBuilder.createQuery(Idea.class);
			Root<Idea> root = criteriaQuery.from(Idea.class);
			Path<Long> pathUserId = root.join(Idea_.user).get(IdeaPoolUser_.id);
			criteriaQuery.where(criteriaBuilder.equal(pathUserId,userId));
			criteriaQuery.orderBy(criteriaBuilder.asc(root.get(Idea_.created_at)));
			return criteriaQuery;					
		};				
		return ideaDao.findAllByCriteria(readCriteria, page, 10);
	}
	
	public Idea updateIdea(long userId, String id, String content, int impact, int ease, int confidence) throws AuthorizationError{
		Idea idea = ideaDao.findById(id);
		if(idea == null || idea.userId() != userId){
			throw new AuthorizationError("Unauthorized access");
		}
		idea.updateSelf(content, impact, ease, confidence);
		return ideaDao.update(idea);
	}
	
}

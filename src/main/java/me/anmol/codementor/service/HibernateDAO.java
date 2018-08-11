package me.anmol.codementor.service;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@org.springframework.transaction.annotation.Transactional
public class HibernateDAO<T extends Serializable, K extends Serializable> implements GenericDAO<T, K> {

	private Class<T> entityClass;

	private EntityManagerFactory factory;

	static int transactionCount = 0;

	Logger logger = LoggerFactory.getLogger(this.getClass());

	protected int batchSize = 50;

	public HibernateDAO(EntityManagerFactory factory, Class<T> entityClass) {
		super();
		this.factory = Objects.requireNonNull(factory, "Entity manager factory must not be null.");
		this.entityClass = Objects.requireNonNull(entityClass, "Entity class must not be null.");
	}

	@Override
	public Class<T> getEntityClass() {
		return entityClass;
	}

	@Override
	public T persist(T entity) {
		EntityManager entityManager = factory.createEntityManager();
		logger.info("Starting transaction " + transactionCount++);
		try {
			entityManager.getTransaction().begin();
			entityManager.persist(entity);
			entityManager.getTransaction().commit();
			logger.debug("Entity was persisted and transaction was commited");
		} finally {
			entityManager.close();
		}
		return entity;
	}

	@Override
	public T update(T entity) {
		EntityManager entityManager = factory.createEntityManager();
		try {
			logger.info("Starting transaction " + transactionCount++);
			entityManager.getTransaction().begin();
			entityManager.merge(entity);
			entityManager.getTransaction().commit();
			logger.debug("Entity merged and transaction commited");
		} finally {
			entityManager.close();
		}
		return entity;
	}

	@Override
	public T findById(K id) {
		EntityManager entityManager = factory.createEntityManager();
		try {
			logger.info("Starting transaction " + transactionCount++);
			entityManager.getTransaction().begin();
			T t = entityManager.find(entityClass, id);
			entityManager.getTransaction().commit();
			logger.debug("Transaction commited and returning the entity");
			return t;
		} finally {
			entityManager.close();
			logger.info("Entity manager closed");
		}
	}

	@Override
	public boolean delete(K id) {
		EntityManager entityManager = factory.createEntityManager();
		try {
			logger.info("Starting transaction " + transactionCount++);
			entityManager.getTransaction().begin();
			boolean status;
			T t = entityManager.find(entityClass, id);
			if (t == null) {
				status = false; // Doesn't exist. Can not delete non existing
								// entity.
				logger.debug("Can not delete as entity does not exist");
			} else {
				entityManager.remove(t);
				status = true;
				logger.debug("Entity deleted");
			}
			entityManager.getTransaction().commit();
			logger.debug("Transaction commited and returning " + status);
			return status;
		} finally {
			entityManager.close();
			logger.info("Entity manager closed");
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<T> findAllByCriteria(ReadCriteria<T> readCriteria, int page, int limit) {
		EntityManager entityManager = factory.createEntityManager();
		try {
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CriteriaQuery<T> criteriaQuery = readCriteria.build(criteriaBuilder);
			TypedQuery<T> typedQuery = entityManager.createQuery(criteriaQuery);
			int offset = (page - 1) * 10;
			typedQuery.setFirstResult(offset);
			typedQuery.setMaxResults(limit);
			List<T> requiredEntities = typedQuery.getResultList();
			logger.debug("Total results returned are: " + requiredEntities.size());
			return requiredEntities;
		} finally {
			entityManager.close();
		}
	}

	@Override
	public List<T> findAllByCriteria(ReadCriteria<T> readCriteria) {
		EntityManager entityManager = factory.createEntityManager();
		try {
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CriteriaQuery<T> criteriaQuery = readCriteria.build(criteriaBuilder);
			TypedQuery<T> typedQuery = entityManager.createQuery(criteriaQuery);
			List<T> requiredEntities = typedQuery.getResultList();
			logger.debug("Total results returned are: " + requiredEntities.size());
			return requiredEntities;
		} finally {
			entityManager.close();
		}
	}

}

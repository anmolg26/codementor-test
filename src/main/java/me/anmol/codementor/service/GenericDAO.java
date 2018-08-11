package me.anmol.codementor.service;

import java.io.Serializable;
import java.util.List;

public interface GenericDAO<T extends Serializable, K extends Serializable> {

	public Class<T> getEntityClass();

	public T persist(T entity);

	public T update(T entity);

	public T findById(K id);

	public boolean delete(K id);
	
	List<T> findAllByCriteria(ReadCriteria<T> readCriteria, int page, int limit);
	
	List<T> findAllByCriteria(ReadCriteria<T> readCriteria);

}
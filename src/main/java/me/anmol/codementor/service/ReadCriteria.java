package me.anmol.codementor.service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

@FunctionalInterface
public interface ReadCriteria<T> {

	CriteriaQuery<T> build(CriteriaBuilder criteriaBuilder);
}

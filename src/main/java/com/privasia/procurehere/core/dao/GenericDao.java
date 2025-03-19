package com.privasia.procurehere.core.dao;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;

import org.hibernate.criterion.MatchMode;

public interface GenericDao<E, PK extends Serializable> {

	/**
	 * @return
	 */
	EntityManager getEntityManager();

	/**
	 * @param entityManager
	 */
	void setEntityManager(final EntityManager entityManager);

	/**
	 * @param e
	 * @return
	 */
	E save(E e);

	/**
	 * @param e
	 * @return TODO
	 */
	E update(E e);

	/**
	 * @param e
	 */
	void delete(E e);

	/**
	 * @param clazz
	 * @return
	 */
	List<E> findAll(final Class<E> clazz);

	/**
	 * @param serializedId
	 * @return
	 */
	E findById(final PK serializedId);

	/**
	 * @return
	 */
	CriteriaBuilder getCriteriaBuilder();

	/**
	 * @return
	 */
	int findCount(E t);

	/**
	 * @param e
	 * @return
	 */
	E saveOrUpdate(E e);


	/**
	 * @param e
	 * @return
	 */
	E saveOrUpdateWithFlush(E e);

	/**
	 * @param propertyName
	 * @param value
	 * @return
	 */
	E findByProperty(String propertyName, Object value);

	void batchInsert(List<E> dataList);

	/**
	 * FindAlllByProperty Method
	 * 
	 * @param propertyName
	 * @param value
	 * @return
	 */
	List<E> findAllByProperty(String propertyName, Object value);

	/**
	 * findByExample Method
	 * 
	 * @param object
	 * @param matchMode
	 * @param ignoreCase
	 * @return
	 */
	List<E> findByExample(E object, MatchMode matchMode, boolean ignoreCase);

	/**
	 * @param propertyNames
	 * @param values
	 * @return
	 */
	E findByProperties(String[] propertyNames, Object[] values);

	String getTopEventCategoryNativeQuery();
}

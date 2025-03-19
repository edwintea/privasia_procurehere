/**
 * 
 */
package com.privasia.procurehere.core.dao.impl;

import java.io.Serializable;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.GenericSupplierTeamMemberDao;
import com.privasia.procurehere.core.entity.EventTeamMember;

/**
 * @author Priyanka Singh
 * @param <T>
 * @param <PK>
 */
@Transactional(propagation = Propagation.REQUIRED)
public class GenericSupplierTeamMemberDaoImpl<T extends EventTeamMember, PK extends Serializable> extends GenericDaoImpl<T, PK> implements GenericSupplierTeamMemberDao<T, PK> {

	protected static final Logger LOG = LogManager.getLogger(GenericDaoImpl.class);

	@Autowired
	MessageSource messageSource;

	@Override
	public void deleteSupplierTeamMemberBySupplierIdForEvent(String supplierId, String userId) {
		LOG.info("Removing Team Member User : " + userId + " for Supplier : " + supplierId);
		StringBuilder hsql = new StringBuilder("delete from " + entityClass.getSimpleName() + " as t   where t.eventSupplier.id = :supplierId and t.user.id = :userId");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("supplierId", supplierId);
		query.setParameter("userId", userId);
		int result = query.executeUpdate();
		if (result <= 0) {
			LOG.warn("Query could not delete any records. Please check.");
		}
		/*
		 * hsql = new StringBuilder("select u from " + entityClass.getSimpleName() +
		 * " as t inner join t.eventSupplier es inner join fetch t.user as u where es.id = :supplierId "); query =
		 * getEntityManager().createQuery(hsql.toString()); query.setParameter("supplierId", supplierId); return
		 * query.getResultList();
		 */
	}

}

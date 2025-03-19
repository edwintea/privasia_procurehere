/**
 * 
 */
package com.privasia.procurehere.core.dao.impl;

/**
 * @author Ravi
 *
 */
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.GenericEventApprovalDao;
import com.privasia.procurehere.core.entity.EventApproval;
import com.privasia.procurehere.core.enums.ApprovalStatus;

@Transactional(propagation = Propagation.REQUIRED)
public class GenericEventApprovalDaoImpl<T extends EventApproval, PK extends Serializable> extends GenericDaoImpl<T, PK> implements GenericEventApprovalDao<T, PK> {

	protected static final Logger LOG = LogManager.getLogger(GenericDaoImpl.class);

	@Autowired
	MessageSource messageSource;

	@Override
	public List<T> findApprovelLevelsForNotification() {
		StringBuilder hsql = new StringBuilder("select distinct usr from " + entityClass.getName() + " usr left outer join usr.approvalUsers au where usr.active = :active and au.approvalStatus= :approvalStatus and au.reminderCount > 0 and au.nextReminderTime < :now " );
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("approvalStatus", ApprovalStatus.PENDING);
		query.setParameter("now", new Date());
		query.setParameter("active", true);
		return query.getResultList();
	}

}

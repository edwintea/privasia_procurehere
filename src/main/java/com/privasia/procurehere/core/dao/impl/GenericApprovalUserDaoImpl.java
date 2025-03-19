/**
 * 
 */
package com.privasia.procurehere.core.dao.impl;

/**
 * @author Nitin Otageri
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

import com.privasia.procurehere.core.dao.GenericApprovalUserDao;
import com.privasia.procurehere.core.entity.ApprovalUser;
import com.privasia.procurehere.core.enums.ApprovalStatus;
import com.privasia.procurehere.core.utils.Global;

@Transactional(propagation = Propagation.REQUIRED)
public class GenericApprovalUserDaoImpl<T extends ApprovalUser, PK extends Serializable> extends GenericDaoImpl<T, PK> implements GenericApprovalUserDao<T, PK> {

	protected static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@Autowired
	MessageSource messageSource;

	@SuppressWarnings("unchecked")
	@Override
	public List<T> findApprovelUserForNotification() {
		StringBuilder hsql = new StringBuilder("select distinct usr from " + entityClass.getName() + " usr where usr.approvalStatus= :approvalStatus and usr.reminderCount > 0 and usr.nextReminderTime < :now and usr.approval.active = :active");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("approvalStatus", ApprovalStatus.PENDING);
		query.setParameter("now", new Date());
		query.setParameter("active", true);
		return query.getResultList();
	}

}

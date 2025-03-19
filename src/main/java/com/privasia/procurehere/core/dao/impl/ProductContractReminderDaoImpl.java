package com.privasia.procurehere.core.dao.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.ProductContractReminderDao;
import com.privasia.procurehere.core.entity.ProductContractReminder;
import com.privasia.procurehere.core.enums.ContractStatus;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;

/**
 * @author pooja
 */
@Repository
public class ProductContractReminderDaoImpl extends GenericDaoImpl<ProductContractReminder, String> implements ProductContractReminderDao {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@SuppressWarnings("unchecked")
	@Override
	public List<ProductContractReminder> getAllContractRemindersByContractId(String contractId) {
		LOG.info("Getting contract reminder list");
		final Query query = getEntityManager().createQuery("from ProductContractReminder r where r.productContract.id = :contractId order by r.reminderDate");
		query.setParameter("contractId", contractId);
		List<ProductContractReminder> reminderList = query.getResultList();
		if (CollectionUtil.isNotEmpty(reminderList)) {
			return reminderList;
		} else {
			return null;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<ProductContractReminder> getContractRemindersForNotification() {
		StringBuilder hsql = new StringBuilder("from ProductContractReminder r inner join fetch r.productContract as e where r.reminderSent = false and r.reminderDate <= :now and e.status = :status order by r.reminderDate");
		Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("now", new Date());
		query.setParameter("status", ContractStatus.ACTIVE);
		List<ProductContractReminder> returnList = (List<ProductContractReminder>) query.getResultList();
		return returnList;
	}

	@Override
	public boolean isReminderPresentForSameInterval(int interval, String contractId) {
		LOG.info("Getting contract reminder list");
		final Query query = getEntityManager().createQuery("select count(r) from ProductContractReminder r where r.productContract.id = :contractId and r.interval = :interval");
		query.setParameter("contractId", contractId);
		query.setParameter("interval", interval);
		return ((Number) query.getSingleResult()).intValue() > 0;

	}

}
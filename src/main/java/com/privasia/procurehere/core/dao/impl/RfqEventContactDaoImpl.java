package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfpEventContactDao;
import com.privasia.procurehere.core.dao.RfqEventContactDao;
import com.privasia.procurehere.core.entity.RfqEventContact;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.StringUtils;

@Repository
public class RfqEventContactDaoImpl extends GenericDaoImpl<RfqEventContact, String> implements RfqEventContactDao {

	private static final Logger LOG = LogManager.getLogger(RfpEventContactDao.class);

	@SuppressWarnings("unchecked")
	@Override
	public List<RfqEventContact> findAllEventContactById(String id) {
		final Query query = getEntityManager().createQuery("from RfqEventContact rec where rec.rfxEvent.id = :id");
		query.setParameter("id", id);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean isExists(RfqEventContact rfqEventContact) {
		LOG.info("Contact Name : " + rfqEventContact.getContactName() + " Event Id : " + rfqEventContact.getEventId());
		StringBuilder hsql = new StringBuilder("from RfqEventContact rc where rc.contactName= :contactName and rc.rfxEvent.id = :rfxId");
		if (StringUtils.checkString(rfqEventContact.getId()).length() > 0) {
			hsql.append(" and rc.id <> :id");
		}
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("contactName", rfqEventContact.getContactName());
		query.setParameter("rfxId", rfqEventContact.getEventId());
		if (StringUtils.checkString(rfqEventContact.getId()).length() > 0) {
		query.setParameter("id", rfqEventContact.getId());
		}
		List<RfqEventContact> scList = query.getResultList();
		return (CollectionUtil.isNotEmpty(scList) ? true : false);
	}
}

package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfaEventContactDao;
import com.privasia.procurehere.core.entity.RfaEventContact;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.StringUtils;

@Repository
public class RfaEventContactDaoImpl extends GenericDaoImpl<RfaEventContact, String> implements RfaEventContactDao {

	Logger LOG = LogManager.getLogger(RfaEventContactDaoImpl.class);

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaEventContact> findAllEventContactById(String id) {
		LOG.info("Event Id in Dao  for contact " + id);
		final Query query = getEntityManager().createQuery("from RfaEventContact rec where rec.rfaEvent.id = :id");
		query.setParameter("id", id);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean isExists(RfaEventContact rfaEventContact) {
		LOG.info("Contact Name : " + rfaEventContact.getContactName() + " Event Id : " + rfaEventContact.getEventId());
		StringBuilder hsql = new StringBuilder("from RfaEventContact rc where rc.contactName= :contactName and rc.rfaEvent.id = :rfxId");
		if (StringUtils.checkString(rfaEventContact.getId()).length() > 0) {
			hsql.append(" and rc.id <> :id");
		}
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("contactName", rfaEventContact.getContactName());
		query.setParameter("rfxId", rfaEventContact.getEventId());
		if (StringUtils.checkString(rfaEventContact.getId()).length() > 0) {
			query.setParameter("id", rfaEventContact.getId());
		}
		List<RfaEventContact> scList = query.getResultList();
		return (CollectionUtil.isNotEmpty(scList) ? true : false);
	}

}

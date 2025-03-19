package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfpEventContactDao;
import com.privasia.procurehere.core.entity.RfpEventContact;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.StringUtils;

@Repository
public class RfpEventContactDaoImpl extends GenericDaoImpl<RfpEventContact, String> implements RfpEventContactDao {

	private static final Logger LOG = LogManager.getLogger(RfpEventContactDao.class);

	@SuppressWarnings("unchecked")
	@Override
	public List<RfpEventContact> findAllEventContactById(String id) {
		final Query query = getEntityManager().createQuery("from RfpEventContact rec where rec.rfxEvent.id = :id");
		query.setParameter("id", id);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean isExists(RfpEventContact rfpEventContact) {
		LOG.info("Contact Name : " + rfpEventContact.getContactName() + " Event Id : " + rfpEventContact.getEventId());
		StringBuilder hsql = new StringBuilder("from RfpEventContact rc where rc.contactName= :contactName and rc.rfxEvent.id = :rfxId");
		if (StringUtils.checkString(rfpEventContact.getId()).length() > 0) {
			hsql.append(" and rc.id <> :id");
		}
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("contactName", rfpEventContact.getContactName());
		query.setParameter("rfxId", rfpEventContact.getEventId());
		if (StringUtils.checkString(rfpEventContact.getId()).length() > 0) {
		query.setParameter("id", rfpEventContact.getId());
		}

		List<RfpEventContact> scList = query.getResultList();
		return (CollectionUtil.isNotEmpty(scList) ? true : false);
	}

}

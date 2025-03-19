package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfiEventContactDao;
import com.privasia.procurehere.core.dao.RfpEventContactDao;
import com.privasia.procurehere.core.entity.RfiEventContact;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.StringUtils;

@Repository
public class RfiEventContactDaoImpl extends GenericDaoImpl<RfiEventContact, String> implements RfiEventContactDao {
	private static final Logger LOG = LogManager.getLogger(RfpEventContactDao.class);

	@SuppressWarnings("unchecked")
	@Override
	public List<RfiEventContact> findAllEventContactById(String id) {
		final Query query = getEntityManager().createQuery("from RfiEventContact rec where rec.rfxEvent.id = :id");
		query.setParameter("id", id);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean isExists(RfiEventContact rfiEventContact) {
		LOG.info("Contact Name : " + rfiEventContact.getContactName() + " Event Id : " + rfiEventContact.getEventId());
		StringBuilder hsql = new StringBuilder("from RfiEventContact rc where rc.contactName= :contactName and rc.rfxEvent.id = :rfxId");
		if (StringUtils.checkString(rfiEventContact.getId()).length() > 0) {
			hsql.append(" and rc.id <> :id");
		}
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("contactName", rfiEventContact.getContactName());
		query.setParameter("rfxId", rfiEventContact.getEventId());
		if (StringUtils.checkString(rfiEventContact.getId()).length() > 0) {
			query.setParameter("id", rfiEventContact.getId());
		}
		List<RfiEventContact> scList = query.getResultList();
		return (CollectionUtil.isNotEmpty(scList) ? true : false);
	}
}

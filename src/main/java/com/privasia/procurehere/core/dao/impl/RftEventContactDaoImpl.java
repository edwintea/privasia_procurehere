package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfpEventContactDao;
import com.privasia.procurehere.core.dao.RftEventContactDao;
import com.privasia.procurehere.core.entity.RftEventContact;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.StringUtils;

@Repository
public class RftEventContactDaoImpl extends GenericDaoImpl<RftEventContact, String> implements RftEventContactDao {
	private static final Logger LOG = LogManager.getLogger(RfpEventContactDao.class);

	@SuppressWarnings("unchecked")
	@Override
	public List<RftEventContact> findAllEventContactById(String id) {
		final Query query = getEntityManager().createQuery("from RftEventContact rec where rec.rfxEvent.id = :id");
		query.setParameter("id", id);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean isExists(RftEventContact rftEventContact) {
		LOG.info("Contact Name : " + rftEventContact.getContactName() + " Event Id : " + rftEventContact.getEventId());
		StringBuilder hsql = new StringBuilder("from RftEventContact rc where rc.contactName= :contactName and rc.rfxEvent.id = :rfxId");
		if (StringUtils.checkString(rftEventContact.getId()).length() > 0) {
			hsql.append(" and rc.id <> :id");
		}
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("contactName", rftEventContact.getContactName());
		query.setParameter("rfxId", rftEventContact.getEventId());
		if (StringUtils.checkString(rftEventContact.getId()).length() > 0) {
			query.setParameter("id", rftEventContact.getId());
		}
		LOG.info("hsql :" + hsql);
		List<RftEventContact> scList = query.getResultList();
		return (CollectionUtil.isNotEmpty(scList) ? true : false);
	}
}

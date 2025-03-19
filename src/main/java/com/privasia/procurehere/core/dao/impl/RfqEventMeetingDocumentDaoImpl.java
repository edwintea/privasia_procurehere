package com.privasia.procurehere.core.dao.impl;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfqEventMeetingDocumentDao;
import com.privasia.procurehere.core.entity.EventMeetingDocument;
import com.privasia.procurehere.core.entity.RfqEventMeetingDocument;
import com.privasia.procurehere.core.utils.CollectionUtil;

@Repository
public class RfqEventMeetingDocumentDaoImpl extends GenericDaoImpl<RfqEventMeetingDocument, String> implements RfqEventMeetingDocumentDao {

	@Override
	public EventMeetingDocument getMeetingDocumentForDelete(String id) {
		final Query query = getEntityManager().createQuery("from RfqEventMeetingDocument e where e.id =:id");
		query.setParameter("id", id);
		return CollectionUtil.isNotEmpty(query.getResultList()) ? (EventMeetingDocument) query.getResultList().get(0) : null;
	}
}

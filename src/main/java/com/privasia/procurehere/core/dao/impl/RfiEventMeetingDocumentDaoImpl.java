package com.privasia.procurehere.core.dao.impl;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfiEventMeetingDocumentDao;
import com.privasia.procurehere.core.entity.EventMeetingDocument;
import com.privasia.procurehere.core.entity.RfiEventMeetingDocument;
import com.privasia.procurehere.core.utils.CollectionUtil;

@Repository
public class RfiEventMeetingDocumentDaoImpl extends GenericDaoImpl<RfiEventMeetingDocument, String> implements RfiEventMeetingDocumentDao {

	@Override
	public EventMeetingDocument getMeetingDocumentForDelete(String id) {
		final Query query = getEntityManager().createQuery("from RfiEventMeetingDocument e where e.id =:id");
		query.setParameter("id", id);
		return CollectionUtil.isNotEmpty(query.getResultList()) ? (EventMeetingDocument) query.getResultList().get(0) : null;
	}
}

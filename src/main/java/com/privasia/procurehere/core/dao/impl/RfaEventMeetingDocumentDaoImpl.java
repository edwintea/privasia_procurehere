package com.privasia.procurehere.core.dao.impl;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfaEventMeetingDocumentDao;
import com.privasia.procurehere.core.entity.EventMeetingDocument;
import com.privasia.procurehere.core.entity.RfaEventMeetingDocument;
import com.privasia.procurehere.core.utils.CollectionUtil;

/**
 * @author RT-Kapil
 */
@Repository
public class RfaEventMeetingDocumentDaoImpl extends GenericDaoImpl<RfaEventMeetingDocument, String> implements RfaEventMeetingDocumentDao {

	@Override
	public EventMeetingDocument getMeetingDocumentForDelete(String id) {
		final Query query = getEntityManager().createQuery("from RfaEventMeetingDocument e where e.id =:id");
		query.setParameter("id", id);
		return CollectionUtil.isNotEmpty(query.getResultList()) ? (EventMeetingDocument) query.getResultList().get(0) : null;
	}

}

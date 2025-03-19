package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.EventAdditionalDocumentDao;
import com.privasia.procurehere.core.entity.AdditionalDocument;
import com.privasia.procurehere.core.entity.RftEventDocument;

@Repository("eventAdditionalDocuemntDao")
public class EventAdditionalDocuemntDaoImpl extends GenericDaoImpl<AdditionalDocument, String> implements EventAdditionalDocumentDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<AdditionalDocument> findAllRftEventdocsbyEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.AdditionalDocument(a.id, a.fileName, a.description, a.uploadDate, a.credContentType, a.fileSizeInKb, sp) from AdditionalDocument a inner join a.rftEvent sp where sp.id =:id order by a.fileName");
		query.setParameter("id", eventId);
		return query.getResultList();
	}

	@Override
	public AdditionalDocument findRftDocsById(String id) {
		final Query query = getEntityManager().createQuery("from AdditionalDocument a  where a.id =:id");
		query.setParameter("id", id);
		return (AdditionalDocument) query.getSingleResult();
	}
	
}
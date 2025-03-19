package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfpDocumentDao;
import com.privasia.procurehere.core.entity.EventDocument;
import com.privasia.procurehere.core.entity.RfpEventDocument;

@Repository
public class RfpDocumentDaoImpl extends GenericDaoImpl<RfpEventDocument, String> implements RfpDocumentDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<RfpEventDocument> findAllEventdocsbyEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.RfpEventDocument(a.id, a.fileName, a.description, a.uploadDate, a.credContentType, sp, a.fileSizeInKb,a.internal, up, up.name) from RfpEventDocument a inner join a.rfxEvent sp inner join a.uploadBy up where sp.id =:id order by a.fileName");
		query.setParameter("id", eventId);
		return query.getResultList();
	}

	@Override
	public void deleteById(String id) {
		final Query query = getEntityManager().createQuery("delete from RfpEventDocument a where a.id =:id");
		query.setParameter("id", id);
		query.executeUpdate();
	}

	@Override
	public RfpEventDocument findDocsById(String id) {
		final Query query = getEntityManager().createQuery("from RfpEventDocument a  where a.id =:id");
		query.setParameter("id", id);
		return (RfpEventDocument) query.getSingleResult();
	}

	@Override
	public Integer getCountOfDocumentByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select count(rd) from RfpEvent e inner join e.documents rd  where e.id =:id");
		query.setParameter("id", eventId);
		Integer count = ((Number) query.getSingleResult()).intValue();
		return count;
	}

	@Override
	public void deleteByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("delete from RfpEventDocument a where a.rfxEvent.id =:eventId");
		query.setParameter("eventId", eventId);
		query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EventDocument> findAllEventDocsNameAndId(String eventId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.EventDocument(ed.id, ed.fileName, ed.fileSizeInKb, ed.credContentType) from RfpEventDocument ed where ed.rfxEvent.id =:eventId");
		query.setParameter("eventId", eventId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfpEventDocument> findAllRfadocsForZipbyEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select a from RfpEventDocument a inner join a.rfxEvent sp where sp.id =:id order by a.fileName");
		query.setParameter("id", eventId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfpEventDocument> findAllRfpEventdocsbyEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.RfpEventDocument(a.id, a.fileName, a.description, a.uploadDate, a.credContentType, sp, a.fileSizeInKb) from RfpEventDocument a inner join a.rfxEvent sp where sp.id =:id order by a.fileName");
		query.setParameter("id", eventId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EventDocument> findAllRfpEventDocsNameByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.EventDocument(ed.id, ed.fileName) from RfpEventDocument ed where ed.rfxEvent.id =:eventId");
		query.setParameter("eventId", eventId);
		return query.getResultList();
	}

	@Override
	public List<EventDocument> findAllRfpEventDocsByEventIdAndDocIds(String id, List<String> docIds) {
		if(docIds.size()>0){
			final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.EventDocument(ed.id, ed.fileName) from RfpEventDocument ed where ed.rfxEvent.id =:eventId and ed.id in (:docIds)");
			query.setParameter("eventId", id);
			query.setParameter("docIds", docIds);
			return query.getResultList();
		}else{
			return null;
		}
		
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<EventDocument> findAllRfpEventDocsNamesByEventIdAndDocIds(String id, List<String> docIds) {
		if(docIds.size()>0){
			final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.EventDocument(ed.fileName) from RfpEventDocument ed where ed.rfxEvent.id =:eventId and ed.id in (:docIds)");
			query.setParameter("eventId", id);
			query.setParameter("docIds", docIds);
			return query.getResultList();
		}else{
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<EventDocument> findAllRfpEventDocsNamesByEventIdAndDocIds(List<String> docIds) {
		if(docIds.size()>0){
			final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.EventDocument(ed.fileName) from RfpEventDocument ed where ed.id in (:docIds)");
			query.setParameter("docIds", docIds);
			return query.getResultList();
		}else{
			return null;
		}
	}
}

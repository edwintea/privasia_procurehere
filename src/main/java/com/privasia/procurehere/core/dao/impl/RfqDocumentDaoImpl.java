package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfqDocumentDao;
import com.privasia.procurehere.core.entity.EventDocument;
import com.privasia.procurehere.core.entity.RfqEventDocument;

@Repository
public class RfqDocumentDaoImpl extends GenericDaoImpl<RfqEventDocument, String> implements RfqDocumentDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<RfqEventDocument> findAllEventdocsbyEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.RfqEventDocument(a.id, a.fileName, a.description, a.uploadDate, a.credContentType, sp, a.fileSizeInKb,a.internal, up, up.name) from RfqEventDocument a inner join a.rfxEvent sp inner join a.uploadBy up where sp.id =:id order by a.fileName");
		query.setParameter("id", eventId);
		return query.getResultList();
	}

	@Override
	public void deleteById(String id) {
		final Query query = getEntityManager().createQuery("delete from RfqEventDocument a where a.id =:id");
		query.setParameter("id", id);
		query.executeUpdate();
	}

	@Override
	public RfqEventDocument findDocsById(String id) {
		final Query query = getEntityManager().createQuery("from RfqEventDocument a  where a.id =:id");
		query.setParameter("id", id);
		return (RfqEventDocument) query.getSingleResult();
	}

	@Override
	public Integer getCountOfDocumentByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select count(rd) from RfqEvent e inner join e.documents rd  where e.id =:id");
		query.setParameter("id", eventId);
		Integer count = ((Number) query.getSingleResult()).intValue();
		return count;
	}

	@Override
	public void deleteByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("delete from RfqEventDocument a where a.rfxEvent.id =:eventId");
		query.setParameter("eventId", eventId);
		query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EventDocument> findAllEventDocsNameAndId(String eventId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.EventDocument(ed.id, ed.fileName, ed.fileSizeInKb, ed.credContentType) from RfqEventDocument ed where ed.rfxEvent.id =:eventId");
		query.setParameter("eventId", eventId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfqEventDocument> findAllRfadocsForZipbyEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select a from RfqEventDocument a inner join a.rfxEvent sp where sp.id =:id order by a.fileName");
		query.setParameter("id", eventId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfqEventDocument> findAllRfqEventdocsbyEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.RfqEventDocument(a.id, a.fileName, a.description, a.uploadDate, a.credContentType, sp, a.fileSizeInKb) from RfqEventDocument a inner join a.rfxEvent sp where sp.id =:id order by a.fileName");
		query.setParameter("id", eventId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EventDocument> findAllRfqEventDocsNameByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.EventDocument(ed.id, ed.fileName) from RfqEventDocument ed where ed.rfxEvent.id =:eventId");
		query.setParameter("eventId", eventId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EventDocument> findAllRfqEventDocsByEventIdAndDocIds(String id, List<String> docIds) {
		if(docIds.size()>0){
			final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.EventDocument(ed.id, ed.fileName) from RfqEventDocument ed where ed.rfxEvent.id =:eventId and ed.id in (:docIds)");
			query.setParameter("eventId", id);
			query.setParameter("docIds", docIds);
			return query.getResultList();
		}else{
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<EventDocument> findAllRfqEventDocsNamesByEventIdAndDocIds(String id, List<String> docIds) {
		if(docIds.size()>0){
			final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.EventDocument(ed.fileName) from RfqEventDocument ed where ed.rfxEvent.id =:eventId and ed.id in (:docIds)");
			query.setParameter("eventId", id);
			query.setParameter("docIds", docIds);
			return query.getResultList();
		}else{
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<EventDocument> findAllRfqEventDocsNamesByEventIdAndDocIds(List<String> docIds) {
		if(docIds.size()>0){
			final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.EventDocument(ed.fileName) from RfqEventDocument ed where ed.id in (:docIds)");
			query.setParameter("docIds", docIds);
			return query.getResultList();
		}else{
			return null;
		}
	}
}

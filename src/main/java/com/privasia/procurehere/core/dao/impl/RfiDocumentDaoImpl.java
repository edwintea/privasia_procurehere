package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfiDocumentDao;
import com.privasia.procurehere.core.entity.EventDocument;
import com.privasia.procurehere.core.entity.RfiEventDocument;

@Repository
public class RfiDocumentDaoImpl extends GenericDaoImpl<RfiEventDocument, String> implements RfiDocumentDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<RfiEventDocument> findAllRfiEventdocsbyEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.RfiEventDocument(a.id, a.fileName, a.description, a.uploadDate, a.credContentType, sp, a.fileSizeInKb,a.internal, up, up.name) from RfiEventDocument a inner join a.rfxEvent sp inner join a.uploadBy up where sp.id =:id order by a.fileName");
		query.setParameter("id", eventId);
		return query.getResultList();
	}

	@Override
	public void deleteById(String id) {
		final Query query = getEntityManager().createQuery("delete from RfiEventDocument a where a.id =:id");
		query.setParameter("id", id);
		query.executeUpdate();
	}

	@Override
	public RfiEventDocument findRfiDocsById(String id) {
		final Query query = getEntityManager().createQuery("from RfiEventDocument a  where a.id =:id");
		query.setParameter("id", id);
		return (RfiEventDocument) query.getSingleResult();
	}

	public Integer getCountOfRfiDocumentByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select count(rd) from RfiEvent e inner join e.documents rd  where e.id =:id");
		query.setParameter("id", eventId);
		Integer count = ((Number) query.getSingleResult()).intValue();
		return count;
	}

	@Override
	public void deleteByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("delete from RfiEventDocument a where a.rfxEvent.id =:eventId");
		query.setParameter("eventId", eventId);
		query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EventDocument> findAllEventDocsNameAndId(String eventId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.EventDocument(ed.id, ed.fileName, ed.fileSizeInKb, ed.credContentType) from RfiEventDocument ed where ed.rfxEvent.id =:eventId");
		query.setParameter("eventId", eventId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfiEventDocument> findAllRfidocsForZipbyEventId(String id) {
		final Query query = getEntityManager().createQuery("select a from RfiEventDocument a inner join a.rfxEvent sp where sp.id =:id order by a.fileName");
		query.setParameter("id", id);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EventDocument> findAllRfiEventDocsNameByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.EventDocument(ed.id, ed.fileName) from RfiEventDocument ed where ed.rfxEvent.id =:eventId");
		query.setParameter("eventId", eventId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EventDocument> findAllRfiEventDocsByEventIdAndDocIds(String id, List<String> docIds) {
		if(docIds.size()>0){
			final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.EventDocument(ed.id, ed.fileName) from RfiEventDocument ed where ed.rfxEvent.id =:eventId and ed.id in (:docIds)");
			query.setParameter("eventId", id);
			query.setParameter("docIds", docIds);
			return query.getResultList();
		}else{
			return null;
		}
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<EventDocument> findAllRfiEventDocsNamesByEventIdAndDocIds(String id, List<String> docIds) {
		if(docIds.size()>0){
			final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.EventDocument(ed.fileName) from RfiEventDocument ed where ed.rfxEvent.id =:eventId and ed.id in (:docIds)");
			query.setParameter("eventId", id);
			query.setParameter("docIds", docIds);
			return query.getResultList();
		}else{
			return null;
		}
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<EventDocument> findAllRfiEventDocsNamesByEventIdAndDocIds(List<String> docIds) {
		if(docIds.size()>0){
			final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.EventDocument(ed.fileName) from RfiEventDocument ed where ed.id in (:docIds)");
			query.setParameter("docIds", docIds);
			return query.getResultList();
		}else{
			return null;
		}
		
	}
}

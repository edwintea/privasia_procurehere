package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RftDocumentDao;
import com.privasia.procurehere.core.entity.EventDocument;
import com.privasia.procurehere.core.entity.RftEventDocument;

@Repository("rftDocumentDao")
public class RftDocumentDaoImpl extends GenericDaoImpl<RftEventDocument, String> implements RftDocumentDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<RftEventDocument> findAllRftEventdocsbyEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.RftEventDocument(a.id, a.fileName, a.description, a.uploadDate, a.credContentType, sp, a.fileSizeInKb,a.internal, up, up.name) from RftEventDocument a inner join a.rfxEvent sp inner join a.uploadBy up where sp.id =:id order by a.fileName");
		query.setParameter("id", eventId);
		return query.getResultList();
	}

	@Override
	public void deleteById(String id) {
		final Query query = getEntityManager().createQuery("delete from RftEventDocument a where a.id =:id");
		query.setParameter("id", id);
		query.executeUpdate();
	}

	@Override
	public RftEventDocument findRftDocsById(String id) {
		final Query query = getEntityManager().createQuery("from RftEventDocument a  where a.id =:id");
		query.setParameter("id", id);
		return (RftEventDocument) query.getSingleResult();
	}
	
	public Integer getCountOfRftDocumentByEventId(String eventId){
		final Query query = getEntityManager().createQuery("select count(rd) from RftEvent e inner join e.documents rd  where e.id =:id");
		query.setParameter("id", eventId);
		Integer count = ((Number) query.getSingleResult()).intValue();
		return count;
	}
	
	@Override
	public void deleteByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("delete from RftEventDocument a where a.rfxEvent.id =:eventId");
		query.setParameter("eventId", eventId);
		query.executeUpdate();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<EventDocument> findAllEventDocsNameAndId(String eventId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.EventDocument(ed.id, ed.fileName, ed.fileSizeInKb, ed.credContentType) from RftEventDocument ed where ed.rfxEvent.id =:eventId");
		query.setParameter("eventId", eventId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RftEventDocument> findAllRfadocsForZipbyEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select a from RftEventDocument a inner join a.rfxEvent sp where sp.id =:id order by a.fileName");
		query.setParameter("id", eventId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EventDocument> findAllRftEventDocsNameByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.EventDocument(ed.id, ed.fileName) from RftEventDocument ed where ed.rfxEvent.id =:eventId");
		query.setParameter("eventId", eventId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EventDocument> findAllRftEventDocsByEventIdAndDocIds(String id, List<String> docIds) {
		if(docIds.size()>0){
			final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.EventDocument(ed.id, ed.fileName) from RftEventDocument ed where ed.rfxEvent.id =:eventId and ed.id in (:docIds)");
			query.setParameter("eventId", id);
			query.setParameter("docIds", docIds);
			return query.getResultList();
		}else{
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<EventDocument> findAllRftEventDocsNamesByEventIdAndDocIds(String id, List<String> docIds) {
		if(docIds.size()>0){
			final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.EventDocument(ed.fileName) from RftEventDocument ed where ed.rfxEvent.id =:eventId and ed.id in (:docIds)");
			query.setParameter("eventId", id);
			query.setParameter("docIds", docIds);
			return query.getResultList();
		}else{
			return null;
		}
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<EventDocument> findAllRftEventDocsNamesByEventIdAndDocIds(List<String> docIds) {
		if(docIds.size()>0){
			final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.EventDocument(ed.fileName) from RftEventDocument ed where ed.id in (:docIds)");
			query.setParameter("docIds", docIds);
			return query.getResultList();
		}else{
			return null;
		}
	}
}

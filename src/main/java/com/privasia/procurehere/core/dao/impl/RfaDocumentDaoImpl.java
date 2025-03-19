package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfaDocumentDao;
import com.privasia.procurehere.core.entity.EventDocument;
import com.privasia.procurehere.core.entity.RfaEventDocument;

@Repository
public class RfaDocumentDaoImpl extends GenericDaoImpl<RfaEventDocument, String> implements RfaDocumentDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaEventDocument> findAllRfaEventdocsbyEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.RfaEventDocument(a.id, a.fileName, a.description, a.uploadDate, a.credContentType, sp, a.fileSizeInKb,a.internal,up, up.name) from RfaEventDocument a inner join a.rfxEvent sp inner join a.uploadBy up where sp.id =:id order by a.fileName");
		query.setParameter("id", eventId);
		return query.getResultList();
	}

	@Override
	public void deleteById(String id) {
		final Query query = getEntityManager().createQuery("delete from RfaEventDocument a where a.id =:id");
		query.setParameter("id", id);
		query.executeUpdate();
	}

	@Override
	public RfaEventDocument findRfaDocsById(String id) {
		final Query query = getEntityManager().createQuery("from RfaEventDocument a  where a.id =:id");
		query.setParameter("id", id);
		return (RfaEventDocument) query.getSingleResult();
	}

	public Integer getCountOfRfaDocumentByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select count(rd) from RfaEvent e inner join e.documents rd  where e.id =:id");
		query.setParameter("id", eventId);
		Integer count = ((Number) query.getSingleResult()).intValue();
		return count;
	}

	@Override
	public void deleteByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("delete from RfaEventDocument a where a.rfxEvent.id =:eventId");
		query.setParameter("eventId", eventId);
		query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EventDocument> findAllEventDocsNameAndId(String eventId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.EventDocument(ed.id, ed.fileName, ed.fileSizeInKb, ed.credContentType) from RfaEventDocument ed where ed.rfxEvent.id =:eventId");
		query.setParameter("eventId", eventId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaEventDocument> findAllRfadocsForZipbyEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select a from RfaEventDocument a inner join a.rfxEvent sp where sp.id =:id");
		query.setParameter("id", eventId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EventDocument> findAllRfaEventDocsNameByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.EventDocument(ed.id, ed.fileName) from RfaEventDocument ed where ed.rfxEvent.id =:eventId");
		query.setParameter("eventId", eventId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EventDocument> findAllRfaEventDocsByEventIdAndDocIds(String id, List<String> docIds) {
		if (docIds.size() > 0) {
			final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.EventDocument(ed.id, ed.fileName) from RfaEventDocument ed where ed.rfxEvent.id =:eventId and ed.id in (:docIds)");
			query.setParameter("eventId", id);
			query.setParameter("docIds", docIds);
			return query.getResultList();
		} else {
			return null;
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EventDocument> findAllRfaEventDocsNamesByEventIdAndDocIds(String id, List<String> docIds) {
		if (docIds.size() > 0) {
			final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.EventDocument(ed.fileName) from RfaEventDocument ed where ed.rfxEvent.id =:eventId and ed.id in (:docIds)");
			query.setParameter("eventId", id);
			query.setParameter("docIds", docIds);
			return query.getResultList();
		} else {
			return null;
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EventDocument> findAllRfaEventDocsNamesByEventIdAndDocIds(List<String> docIds) {
		if (docIds.size() > 0) {
			final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.EventDocument(ed.fileName) from RfaEventDocument ed where ed.id in (:docIds)");
			query.setParameter("docIds", docIds);
			return query.getResultList();
		} else {
			return null;
		}

	}
}

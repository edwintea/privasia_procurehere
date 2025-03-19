package com.privasia.procurehere.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RfiDocumentDao;
import com.privasia.procurehere.core.dao.RfiEventDao;
import com.privasia.procurehere.core.entity.EventDocument;
import com.privasia.procurehere.core.entity.RfiEvent;
import com.privasia.procurehere.core.entity.RfiEventDocument;
import com.privasia.procurehere.service.RfiDocumentService;

@Service
@Transactional(readOnly = true)
public class RfiDocumentServiceImpl implements RfiDocumentService {

	@Autowired
	RfiEventDao rfiEventDao;

	@Autowired
	RfiDocumentDao rfiDocumentDao;

	@Override
	public List<RfiEventDocument> findAllRfiEventdocsbyEventId(String eventId) {
		return rfiDocumentDao.findAllRfiEventdocsbyEventId(eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public RfiEventDocument saveRfiDocuments(RfiEventDocument rftEventDocument) {
		return rfiDocumentDao.saveOrUpdate(rftEventDocument);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void removeRfiDocument(String removeDocId) {
		rfiDocumentDao.deleteById(removeDocId);

	}

	@Override
	public RfiEventDocument findRfiEventdocsById(String id) {
		return rfiDocumentDao.findRfiDocsById(id);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteAllRfiDocuments(String eventId, String eventRequirement) {

		rfiDocumentDao.deleteByEventId(eventId);
		RfiEvent event = rfiEventDao.findById(eventId);
		event.setDocumentCompleted(Boolean.FALSE);
		event.setDocumentReq(Boolean.FALSE);
		rfiEventDao.update(event);

	}

	@Override
	public RfiEvent getRfiEventById(String id) {
		return rfiEventDao.findById(id);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateEvent(RfiEventDocument documentvent) {
		rfiDocumentDao.update(documentvent);

	}

	@Override
	@Transactional(readOnly = false)
	public void updateEventDocumentDesc(String docId, String docDesc, String eventId, Boolean internal) {
		RfiEventDocument rfiDocument = findRfiEventdocsById(docId);
		rfiDocument.setDescription(docDesc);
		rfiDocument.setInternal(internal);
		RfiEvent rfiEvents = getRfiEventById(eventId);
		rfiDocument.setRfxEvent(rfiEvents);
		saveRfiDocuments(rfiDocument);
	}

	@Override
	public List<RfiEventDocument> findAllRfidocsForZipbyEventId(String id) {
		return rfiDocumentDao.findAllRfidocsForZipbyEventId(id);
	}

	@Override
	public List<EventDocument> findAllRfiEventDocsNameByEventId(String eventId) {
		List<EventDocument> rfiEventDocument = rfiDocumentDao.findAllRfiEventDocsNameByEventId(eventId);
		return rfiEventDocument;
	}

	@Override
	public List<EventDocument> findAllRfiEventDocsByEventIdAndDocIds(String id, List<String> docIds) {
		return rfiDocumentDao.findAllRfiEventDocsByEventIdAndDocIds(id,docIds);
	}
	
	@Override
	public List<EventDocument> findAllRfiEventDocsNamesByEventIdAndDocIds(String id, List<String> docIds) {
		return rfiDocumentDao.findAllRfiEventDocsNamesByEventIdAndDocIds(id,docIds);
	}
	
	@Override
	public List<EventDocument> findAllRfiEventDocsNamesByEventIdAndDocIds(List<String> docIds) {
		return rfiDocumentDao.findAllRfiEventDocsNamesByEventIdAndDocIds(docIds);
	}
}

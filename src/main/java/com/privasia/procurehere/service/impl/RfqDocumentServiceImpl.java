package com.privasia.procurehere.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RfqDocumentDao;
import com.privasia.procurehere.core.dao.RfqEventDao;
import com.privasia.procurehere.core.entity.EventDocument;
import com.privasia.procurehere.core.entity.RfqEvent;
import com.privasia.procurehere.core.entity.RfqEventDocument;
import com.privasia.procurehere.service.RfqDocumentService;

@Service
@Transactional(readOnly = true)
public class RfqDocumentServiceImpl implements RfqDocumentService {

	@Autowired
	RfqEventDao rfqEventDao;

	@Autowired
	RfqDocumentDao rfqDocumentDao;

	@Override
	public List<RfqEventDocument> findAllEventdocsbyEventId(String eventId) {
		return rfqDocumentDao.findAllEventdocsbyEventId(eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public RfqEventDocument saveDocuments(RfqEventDocument rftEventDocument) {
		return rfqDocumentDao.saveOrUpdate(rftEventDocument);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void removeDocument(String removeDocId) {
		rfqDocumentDao.deleteById(removeDocId);

	}

	@Override
	public RfqEventDocument findEventdocsById(String id) {
		return rfqDocumentDao.findDocsById(id);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteAllDocuments(String eventId, String eventRequirement) {
		rfqDocumentDao.deleteByEventId(eventId);
		RfqEvent event=  rfqEventDao.findById(eventId);
		event.setDocumentCompleted(Boolean.FALSE);
		event.setDocumentReq(Boolean.FALSE);
		rfqEventDao.update(event);
	}

	@Override
	public RfqEvent getEventById(String id) {
		return rfqEventDao.findById(id);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateEventDocumentDesc(String docId, String docDesc, String eventId, Boolean internal) {
		RfqEventDocument rfqDocument = findEventdocsById(docId);
		rfqDocument.setDescription(docDesc);
		rfqDocument.setInternal(internal);
		RfqEvent rfqEvents = getEventById(eventId);
		rfqDocument.setRfxEvent(rfqEvents);
		saveDocuments(rfqDocument);
	}

	@Override
	public List<RfqEventDocument> findAllRfadocsForZipbyEventId(String eventId) {
		return rfqDocumentDao.findAllRfadocsForZipbyEventId(eventId);
	}

	@Override
	public List<EventDocument> findAllRfqEventDocsNameByEventId(String eventId) {
		return rfqDocumentDao.findAllRfqEventDocsNameByEventId(eventId);
	}

	@Override
	public List<EventDocument> findAllRfqEventDocsByEventIdAndDocIds(String id, List<String> docIds) {
		return rfqDocumentDao.findAllRfqEventDocsByEventIdAndDocIds(id,docIds);
	}
	
	@Override
	public List<EventDocument> findAllRfqEventDocsNamesByEventIdAndDocIds(String id, List<String> docIds) {
		return rfqDocumentDao.findAllRfqEventDocsNamesByEventIdAndDocIds(id,docIds);
	}
	
	@Override
	public List<EventDocument> findAllRfqEventDocsNamesByEventIdAndDocIds(List<String> docIds) {
		return rfqDocumentDao.findAllRfqEventDocsNamesByEventIdAndDocIds(docIds);
	}
}

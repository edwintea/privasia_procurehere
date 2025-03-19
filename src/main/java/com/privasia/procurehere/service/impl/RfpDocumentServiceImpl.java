package com.privasia.procurehere.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RfpDocumentDao;
import com.privasia.procurehere.core.dao.RfpEventDao;
import com.privasia.procurehere.core.entity.EventDocument;
import com.privasia.procurehere.core.entity.RfpEvent;
import com.privasia.procurehere.core.entity.RfpEventDocument;
import com.privasia.procurehere.service.RfpDocumentService;

@Service
@Transactional(readOnly = true)
public class RfpDocumentServiceImpl implements RfpDocumentService {

	@Autowired
	RfpEventDao rfpEventDao;

	@Autowired
	RfpDocumentDao rfpDocumentDao;

	@Override
	public List<RfpEventDocument> findAllEventdocsbyEventId(String eventId) {
		return rfpDocumentDao.findAllEventdocsbyEventId(eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public RfpEventDocument saveDocuments(RfpEventDocument rftEventDocument) {
		return rfpDocumentDao.saveOrUpdate(rftEventDocument);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void removeDocument(String removeDocId) {
		rfpDocumentDao.deleteById(removeDocId);

	}

	@Override
	public RfpEventDocument findEventdocsById(String id) {
		return rfpDocumentDao.findDocsById(id);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteAllDocuments(String eventId, String eventRequirement) {
		rfpDocumentDao.deleteByEventId(eventId);
		RfpEvent event = rfpEventDao.findById(eventId);
		event.setDocumentCompleted(Boolean.FALSE);
		event.setDocumentReq(Boolean.FALSE);
		rfpEventDao.update(event);
	}

	@Override
	public RfpEvent getEventById(String id) {
		return rfpEventDao.findById(id);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateEventDocumentDesc(String docId, String docDesc, String eventId, Boolean internal) {
		RfpEventDocument rfpDocument = findEventdocsById(docId);
		rfpDocument.setDescription(docDesc);
		rfpDocument.setInternal(internal);
		RfpEvent rfpEvents = getEventById(eventId);
		rfpDocument.setRfxEvent(rfpEvents);
		saveDocuments(rfpDocument);
	}

	@Override
	public List<RfpEventDocument> findAllRfadocsForZipbyEventId(String eventId) {
		return rfpDocumentDao.findAllRfadocsForZipbyEventId(eventId);
	}

	@Override
	public List<EventDocument> findAllRfpEventDocsNameByEventId(String eventId) {
		return rfpDocumentDao.findAllRfpEventDocsNameByEventId(eventId);
	}

	@Override
	public List<EventDocument> findAllRfpEventDocsByEventIdAndDocIds(String id, List<String> docIds) {
		return rfpDocumentDao.findAllRfpEventDocsByEventIdAndDocIds(id,docIds);
	}
	
	@Override
	public List<EventDocument> findAllRfpEventDocsNamesByEventIdAndDocIds(String id, List<String> docIds) {
		return rfpDocumentDao.findAllRfpEventDocsNamesByEventIdAndDocIds(id,docIds);
	}
	
	@Override
	public List<EventDocument> findAllRfpEventDocsNamesByEventIdAndDocIds(List<String> docIds) {
		return rfpDocumentDao.findAllRfpEventDocsNamesByEventIdAndDocIds(docIds);
	}
}

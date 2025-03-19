package com.privasia.procurehere.service.impl;

import java.util.List;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RfaDocumentDao;
import com.privasia.procurehere.core.dao.RfaEventDao;
import com.privasia.procurehere.core.entity.EventDocument;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.entity.RfaEventDocument;
import com.privasia.procurehere.service.RfaDocumentService;

@Service
@Transactional(readOnly = true)
public class RfaDocumentServiceImpl implements RfaDocumentService {

	@Autowired
	RfaEventDao rfaEventDao;

	@Autowired
	RfaDocumentDao rfaDocumentDao;

	@Override
	public List<RfaEventDocument> findAllRfaEventdocsbyEventId(String eventId) {
		return rfaDocumentDao.findAllRfaEventdocsbyEventId(eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public RfaEventDocument saveRfaDocuments(RfaEventDocument rfaEventDocument) {
		return rfaDocumentDao.saveOrUpdate(rfaEventDocument);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void removeRfaDocument(String removeDocId) {
		rfaDocumentDao.deleteById(removeDocId);

	}

	@Override
	public RfaEventDocument findRfaEventdocsById(String id) {
		return rfaDocumentDao.findRfaDocsById(id);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteAllRfaDocuments(String eventId, String eventRequirement) {
		rfaDocumentDao.deleteByEventId(eventId);
		RfaEvent event = rfaEventDao.findById(eventId);
		event.setDocumentCompleted(Boolean.FALSE);
		event.setDocumentReq(Boolean.FALSE);
		rfaEventDao.update(event);

	}

	@Override
	public RfaEvent getRfaEventById(String id) {
		return rfaEventDao.findById(id);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateEventDocumentDesc(String docId, String docDesc, String eventId, Boolean internal) {
		RfaEventDocument rfaDocument = findRfaEventdocsById(docId);
		rfaDocument.setDescription(docDesc);
		rfaDocument.setInternal(internal);
		RfaEvent rfaEvents = getRfaEventById(eventId);
		rfaDocument.setRfxEvent(rfaEvents);
		saveRfaDocuments(rfaDocument);
	}

	@Override
	public List<RfaEventDocument> findAllRfadocsForZipbyEventId(String eventId) {
		return rfaDocumentDao.findAllRfadocsForZipbyEventId(eventId);
	}

	@Override
	public List<EventDocument> findAllRfaEventDocsNameByEventId(String eventId) {
		return rfaDocumentDao.findAllRfaEventDocsNameByEventId(eventId);
	}

	@Override
	public List<EventDocument> findAllRfaEventDocsByEventIdAndDocIds(String id, List<String> docIds) {
		return rfaDocumentDao.findAllRfaEventDocsByEventIdAndDocIds(id,docIds);
	}
	
	@Override
	public List<EventDocument> findAllRfaEventDocsNamesByEventIdAndDocIds(String id, List<String> docIds) {
		return rfaDocumentDao.findAllRfaEventDocsNamesByEventIdAndDocIds(id,docIds);
	}
	
	@Override
	public List<EventDocument> findAllRfaEventDocsNamesByEventIdAndDocIds(List<String> docIds) {
		return rfaDocumentDao.findAllRfaEventDocsNamesByEventIdAndDocIds(docIds);
	}
}

package com.privasia.procurehere.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RftDocumentDao;
import com.privasia.procurehere.core.dao.RftEventDao;
import com.privasia.procurehere.core.entity.EventDocument;
import com.privasia.procurehere.core.entity.RftEvent;
import com.privasia.procurehere.core.entity.RftEventDocument;
import com.privasia.procurehere.service.RftDocumentService;

@Service
@Transactional(readOnly = true)
public class RftDocumentServiceImpl implements RftDocumentService {

	@Autowired
	RftEventDao rftEventDao;

	@Autowired
	RftDocumentDao rftDocumentDao;

	@Override
	public List<RftEventDocument> findAllRftEventdocsbyEventId(String eventId) {
		return rftDocumentDao.findAllRftEventdocsbyEventId(eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public RftEventDocument saveRftDocuments(RftEventDocument rftEventDocument) {
		return rftDocumentDao.saveOrUpdate(rftEventDocument);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void removeRftDocument(String removeDocId) {
		rftDocumentDao.deleteById(removeDocId);

	}

	@Override
	public RftEventDocument findRftEventdocsById(String id) {
		return rftDocumentDao.findRftDocsById(id);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteAllRftDocuments(String eventId, String eventRequirement) {
		rftDocumentDao.deleteByEventId(eventId);
		RftEvent event = rftEventDao.findById(eventId);
		event.setDocumentCompleted(Boolean.FALSE);
		event.setDocumentReq(Boolean.FALSE);
		rftEventDao.update(event);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateEventDocumentDesc(String docId, String docDesc, String eventId, Boolean internal) {
		RftEventDocument rftDocument = findRftEventdocsById(docId);
		rftDocument.setDescription(docDesc);
		rftDocument.setInternal(internal);
		RftEvent rftEvents = getRftEventById(eventId);
		rftDocument.setRfxEvent(rftEvents);
		saveRftDocuments(rftDocument);
	}

	@Override
	public RftEvent getRftEventById(String id) {
		return rftEventDao.findById(id);
	}

	@Override
	public List<RftEventDocument> findAllRfadocsForZipbyEventId(String eventId) {
		return rftDocumentDao.findAllRfadocsForZipbyEventId(eventId);
	}

	@Override
	public List<EventDocument> findAllRftEventDocsNameByEventId(String eventId) {
		return rftDocumentDao.findAllRftEventDocsNameByEventId(eventId);
	}

	@Override
	public List<EventDocument> findAllRftEventDocsByEventIdAndDocIds(String id, List<String> docIds) {
		return rftDocumentDao.findAllRftEventDocsByEventIdAndDocIds(id,docIds);
	}
	
	@Override
	public List<EventDocument> findAllRftEventDocsNamesByEventIdAndDocIds(String id, List<String> docIds) {
		return rftDocumentDao.findAllRftEventDocsNamesByEventIdAndDocIds(id,docIds);
	}
	
	@Override
	public List<EventDocument> findAllRftEventDocsNamesByEventIdAndDocIds(List<String> docIds) {
		return rftDocumentDao.findAllRftEventDocsNamesByEventIdAndDocIds(docIds);
	}
}

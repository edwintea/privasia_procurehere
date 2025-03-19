package com.privasia.procurehere.service.impl;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;

import com.privasia.procurehere.core.dao.EventAdditionalDocumentDao;
import com.privasia.procurehere.core.dao.RftEventDao;
import com.privasia.procurehere.core.entity.AdditionalDocument;
import com.privasia.procurehere.core.entity.RftEvent;
import com.privasia.procurehere.service.EventAdditionalDocumentService;

@Service
@Transactional(readOnly = true)
public class EventAdditionlDocumentServiceImpl implements EventAdditionalDocumentService {

	@Autowired
	EventAdditionalDocumentDao eventAdditionalDocumentDao;

	@Autowired
	RftEventDao rftEventDao;

	@Override
	@Transactional(readOnly = false)
	public AdditionalDocument saveEventAdditionalDocuments(AdditionalDocument rftDocument) {
		return eventAdditionalDocumentDao.saveOrUpdate(rftDocument);

	}

	@Override
	public List<AdditionalDocument> findAllRftEventdocsbyEventId(String eventId) {
		return eventAdditionalDocumentDao.findAllRftEventdocsbyEventId(eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateEventAdditionalDocumentDesc(String docId, String docDesc, String eventId) {
		AdditionalDocument rftDocument = findRftEventdocsById(docId);
		rftDocument.setDescription(docDesc);
		RftEvent rftEvents = rftEventDao.findById(eventId);
		rftDocument.setRftEvent(rftEvents);
		saveEventAdditionalDocuments(rftDocument);
	}

	private AdditionalDocument findRftEventdocsById(String docId) {
		return eventAdditionalDocumentDao.findRftDocsById(docId);
	}

	@Override
	public void downloadAdditionalDocument(String docId, HttpServletResponse response) throws Exception {
		AdditionalDocument docs = eventAdditionalDocumentDao.findRftDocsById(docId);
		response.setContentType(docs.getCredContentType());
		response.setContentLength(docs.getFileData().length);
		response.setHeader("Content-Disposition", "attachment; filename=\"" + docs.getFileName() + "\"");
		FileCopyUtils.copy(docs.getFileData(), response.getOutputStream());
		response.flushBuffer();
		response.setStatus(HttpServletResponse.SC_OK);
	}

	@Override
	@Transactional(readOnly = false)
	public void removeAdditionalDocument(AdditionalDocument approvalDocument) {
		eventAdditionalDocumentDao.delete(approvalDocument);
	}

	@Override
	public AdditionalDocument findAdditionalDocById(String removeDocId) {
		return eventAdditionalDocumentDao.findById(removeDocId);
	}

}

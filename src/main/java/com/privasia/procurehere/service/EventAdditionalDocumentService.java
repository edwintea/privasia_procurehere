package com.privasia.procurehere.service;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.privasia.procurehere.core.entity.AdditionalDocument;

/**
 * @author sudesha
 */
public interface EventAdditionalDocumentService {

	AdditionalDocument saveEventAdditionalDocuments(AdditionalDocument rftDocument);

	List<AdditionalDocument> findAllRftEventdocsbyEventId(String eventId);

	void updateEventAdditionalDocumentDesc(String docId, String docDesc, String eventId);

	void downloadAdditionalDocument(String docId, HttpServletResponse response) throws Exception;

	void removeAdditionalDocument(AdditionalDocument approvalDocument);

	AdditionalDocument findAdditionalDocById(String removeDocId);

}

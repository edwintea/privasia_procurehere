package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.EventDocument;
import com.privasia.procurehere.core.entity.RfpEvent;
import com.privasia.procurehere.core.entity.RfpEventDocument;

/**
 * @author Kapil
 * @author Ravi
 */

public interface RfpDocumentService {

	List<RfpEventDocument> findAllEventdocsbyEventId(String eventId);

	/**
	 * @param rftEventDocument
	 * @return
	 */
	RfpEventDocument saveDocuments(RfpEventDocument rftEventDocument);

	/**
	 * @param removeDocId
	 */
	void removeDocument(String removeDocId);

	/**
	 * @param id
	 * @return
	 */
	RfpEventDocument findEventdocsById(String id);

	/**
	 * @param eventId
	 * @param eventRequirement
	 */
	void deleteAllDocuments(String eventId, String eventRequirement);

	/**
	 * @param id
	 * @return
	 */
	RfpEvent getEventById(String id);

	void updateEventDocumentDesc(String docId, String docDesc, String eventId, Boolean internal);

	List<RfpEventDocument> findAllRfadocsForZipbyEventId(String id);

	List<EventDocument> findAllRfpEventDocsNameByEventId(String eventId);

	List<EventDocument> findAllRfpEventDocsByEventIdAndDocIds(String id, List<String> docIds);

	List<EventDocument> findAllRfpEventDocsNamesByEventIdAndDocIds(String id, List<String> docIds);

	List<EventDocument> findAllRfpEventDocsNamesByEventIdAndDocIds(List<String> docIds);

}

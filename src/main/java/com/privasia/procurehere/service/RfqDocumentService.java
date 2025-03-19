package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.EventDocument;
import com.privasia.procurehere.core.entity.RfqEvent;
import com.privasia.procurehere.core.entity.RfqEventDocument;

/**
 * @author Kapil
 * @author Ravi
 */

public interface RfqDocumentService {

	List<RfqEventDocument> findAllEventdocsbyEventId(String eventId);

	/**
	 * @param eventDocument
	 * @return
	 */
	RfqEventDocument saveDocuments(RfqEventDocument eventDocument);

	/**
	 * @param removeDocId
	 */
	void removeDocument(String removeDocId);

	/**
	 * @param eventId
	 * @return
	 */
	RfqEventDocument findEventdocsById(String eventId);

	/**
	 * @param eventId
	 * @param eventRequirement
	 */
	void deleteAllDocuments(String eventId, String eventRequirement);

	/**
	 * @param id
	 * @return
	 */
	RfqEvent getEventById(String id);

	void updateEventDocumentDesc(String docId, String docDesc, String eventId, Boolean internal);

	List<RfqEventDocument> findAllRfadocsForZipbyEventId(String id);

	List<EventDocument> findAllRfqEventDocsNameByEventId(String eventId);

	List<EventDocument> findAllRfqEventDocsByEventIdAndDocIds(String id, List<String> docIds);

	List<EventDocument> findAllRfqEventDocsNamesByEventIdAndDocIds(String id, List<String> docIds);

	List<EventDocument> findAllRfqEventDocsNamesByEventIdAndDocIds(List<String> docIds);

}

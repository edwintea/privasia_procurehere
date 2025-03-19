package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.EventDocument;
import com.privasia.procurehere.core.entity.RfiEvent;
import com.privasia.procurehere.core.entity.RfiEventDocument;

/**
 * @author Kapil
 * @author Ravi
 */

public interface RfiDocumentService {

	List<RfiEventDocument> findAllRfiEventdocsbyEventId(String eventId);

	/**
	 * @param rfiEventDocument
	 * @return
	 */
	RfiEventDocument saveRfiDocuments(RfiEventDocument rfiEventDocument);

	/**
	 * @param removeDocId
	 */
	void removeRfiDocument(String removeDocId);

	/**
	 * @param eventId
	 * @return
	 */
	RfiEventDocument findRfiEventdocsById(String eventId);

	/**
	 * @param eventId
	 * @param eventRequirement
	 */
	void deleteAllRfiDocuments(String eventId, String eventRequirement);

	/**
	 * @param id
	 * @return
	 */
	RfiEvent getRfiEventById(String id);

	/**
	 * @param documentvent
	 */
	void updateEvent(RfiEventDocument documentvent);

	void updateEventDocumentDesc(String docId, String docDesc, String eventId, Boolean internal);

	List<RfiEventDocument> findAllRfidocsForZipbyEventId(String id);

	List<EventDocument> findAllRfiEventDocsNameByEventId(String eventId);

	List<EventDocument> findAllRfiEventDocsByEventIdAndDocIds(String id, List<String> docIds);

	List<EventDocument> findAllRfiEventDocsNamesByEventIdAndDocIds(String id, List<String> docIds);

	List<EventDocument> findAllRfiEventDocsNamesByEventIdAndDocIds(List<String> docIds);

}

package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.EventDocument;
import com.privasia.procurehere.core.entity.RftEvent;
import com.privasia.procurehere.core.entity.RftEventDocument;

/**
 * @author Kapil
 * @author Ravi
 */

public interface RftDocumentService {

	List<RftEventDocument> findAllRftEventdocsbyEventId(String eventId);

	/**
	 * @param rftEventDocument
	 * @return
	 */
	RftEventDocument saveRftDocuments(RftEventDocument rftEventDocument);

	/**
	 * @param removeDocId
	 */
	void removeRftDocument(String removeDocId);

	/**
	 * @param eventId
	 * @return
	 */
	RftEventDocument findRftEventdocsById(String eventId);

	/**
	 * @param eventId
	 * @param eventRequirement
	 */
	void deleteAllRftDocuments(String eventId, String eventRequirement);

	/**
	 * @param id
	 * @return
	 */
	RftEvent getRftEventById(String id);

	void updateEventDocumentDesc(String docId, String docDesc, String eventId, Boolean internal);

	List<RftEventDocument> findAllRfadocsForZipbyEventId(String id);

	List<EventDocument> findAllRftEventDocsNameByEventId(String eventId);

	List<EventDocument> findAllRftEventDocsByEventIdAndDocIds(String id, List<String> docIds);

	List<EventDocument> findAllRftEventDocsNamesByEventIdAndDocIds(String id, List<String> docIds);

	List<EventDocument> findAllRftEventDocsNamesByEventIdAndDocIds(List<String> docIds);

}

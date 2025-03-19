package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.EventDocument;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.entity.RfaEventDocument;

/**
 * @author RT-Kapil
 */

public interface RfaDocumentService {

	List<RfaEventDocument> findAllRfaEventdocsbyEventId(String eventId);

	/**
	 * @param rfaEventDocument
	 * @return
	 */
	RfaEventDocument saveRfaDocuments(RfaEventDocument rfaEventDocument);

	/**
	 * @param removeDocId
	 */
	void removeRfaDocument(String removeDocId);

	/**
	 * @param eventId
	 * @return
	 */
	RfaEventDocument findRfaEventdocsById(String eventId);

	/**
	 * @param eventId
	 * @param eventRequirement
	 */
	void deleteAllRfaDocuments(String eventId, String eventRequirement);

	/**
	 * @param id
	 * @return
	 */
	RfaEvent getRfaEventById(String id);

	void updateEventDocumentDesc(String docId, String docDesc, String eventId, Boolean internal);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfaEventDocument> findAllRfadocsForZipbyEventId(String eventId);

	List<EventDocument> findAllRfaEventDocsNameByEventId(String eventId);

	List<EventDocument> findAllRfaEventDocsByEventIdAndDocIds(String id, List<String> docIds);

	List<EventDocument> findAllRfaEventDocsNamesByEventIdAndDocIds(String id, List<String> docIds);

	List<EventDocument> findAllRfaEventDocsNamesByEventIdAndDocIds(List<String> docIds);

}

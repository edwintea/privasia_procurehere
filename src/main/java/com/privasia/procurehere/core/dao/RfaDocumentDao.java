package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.EventDocument;
import com.privasia.procurehere.core.entity.RfaEventDocument;

public interface RfaDocumentDao extends GenericDao<RfaEventDocument, String> {

	/**
	 * @param eventId
	 * @return
	 */
	List<RfaEventDocument> findAllRfaEventdocsbyEventId(String eventId);

	/**
	 * @param id
	 */
	void deleteById(String id);

	/**
	 * @param eventId
	 * @return
	 */
	RfaEventDocument findRfaDocsById(String id);

	/**
	 * @param eventId
	 * @return
	 */
	Integer getCountOfRfaDocumentByEventId(String eventId);

	void deleteByEventId(String eventId);

	/**
	 * @param id
	 * @return
	 */
	List<EventDocument> findAllEventDocsNameAndId(String id);

	List<RfaEventDocument> findAllRfadocsForZipbyEventId(String eventId);

	List<EventDocument> findAllRfaEventDocsNameByEventId(String eventId);

	List<EventDocument> findAllRfaEventDocsByEventIdAndDocIds(String id, List<String> docIds);

	List<EventDocument> findAllRfaEventDocsNamesByEventIdAndDocIds(String id, List<String> docIds);

	List<EventDocument> findAllRfaEventDocsNamesByEventIdAndDocIds(List<String> docIds);
}

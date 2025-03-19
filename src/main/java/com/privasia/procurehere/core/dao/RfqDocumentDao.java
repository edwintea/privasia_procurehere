package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.EventDocument;
import com.privasia.procurehere.core.entity.RfqEventDocument;

public interface RfqDocumentDao extends GenericDao<RfqEventDocument, String> {

	/**
	 * @param eventId
	 * @return
	 */
	List<RfqEventDocument> findAllEventdocsbyEventId(String eventId);

	/**
	 * @param id
	 */
	void deleteById(String id);

	/**
	 * @param eventId
	 * @return
	 */
	RfqEventDocument findDocsById(String id);

	/**
	 * @param eventId
	 * @return
	 */
	Integer getCountOfDocumentByEventId(String eventId);

	void deleteByEventId(String eventId);

	/**
	 * @param id
	 * @return
	 */
	List<EventDocument> findAllEventDocsNameAndId(String id);

	List<RfqEventDocument> findAllRfadocsForZipbyEventId(String eventId);

	List<RfqEventDocument> findAllRfqEventdocsbyEventId(String eventId);

	List<EventDocument> findAllRfqEventDocsNameByEventId(String eventId);

	List<EventDocument> findAllRfqEventDocsByEventIdAndDocIds(String id, List<String> docIds);

	List<EventDocument> findAllRfqEventDocsNamesByEventIdAndDocIds(String id, List<String> docIds);

	List<EventDocument> findAllRfqEventDocsNamesByEventIdAndDocIds(List<String> docIds);
}

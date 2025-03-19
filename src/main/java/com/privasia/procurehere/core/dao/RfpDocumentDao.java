package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.EventDocument;
import com.privasia.procurehere.core.entity.RfpEventDocument;

public interface RfpDocumentDao extends GenericDao<RfpEventDocument, String> {

	/**
	 * @param eventId
	 * @return
	 */
	List<RfpEventDocument> findAllEventdocsbyEventId(String eventId);

	/**
	 * @param id
	 */
	void deleteById(String id);

	/**
	 * @param eventId
	 * @return
	 */
	RfpEventDocument findDocsById(String id);

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

	List<RfpEventDocument> findAllRfadocsForZipbyEventId(String eventId);

	List<RfpEventDocument> findAllRfpEventdocsbyEventId(String eventId);

	List<EventDocument> findAllRfpEventDocsNameByEventId(String eventId);

	List<EventDocument> findAllRfpEventDocsByEventIdAndDocIds(String id, List<String> docIds);

	List<EventDocument> findAllRfpEventDocsNamesByEventIdAndDocIds(String id, List<String> docIds);

	List<EventDocument> findAllRfpEventDocsNamesByEventIdAndDocIds(List<String> docIds);
}

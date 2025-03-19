package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.EventDocument;
import com.privasia.procurehere.core.entity.RftEventDocument;

public interface RftDocumentDao extends GenericDao<RftEventDocument, String> {

	/**
	 * @param eventId
	 * @return
	 */
	List<RftEventDocument> findAllRftEventdocsbyEventId(String eventId);

	/**
	 * @param id
	 */
	void deleteById(String id);

	/**
	 * @param eventId
	 * @return
	 */
	RftEventDocument findRftDocsById(String id);

	/**
	 * @param eventId
	 * @return
	 */
	Integer getCountOfRftDocumentByEventId(String eventId);

	/**
	 * @param eventId
	 */
	void deleteByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<EventDocument> findAllEventDocsNameAndId(String eventId);

	List<RftEventDocument> findAllRfadocsForZipbyEventId(String eventId);

	List<EventDocument> findAllRftEventDocsNameByEventId(String eventId);

	List<EventDocument> findAllRftEventDocsByEventIdAndDocIds(String id, List<String> docIds);

	List<EventDocument> findAllRftEventDocsNamesByEventIdAndDocIds(String id, List<String> docIds);

	List<EventDocument> findAllRftEventDocsNamesByEventIdAndDocIds(List<String> docIds);
}

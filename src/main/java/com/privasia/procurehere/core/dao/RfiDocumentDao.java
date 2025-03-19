package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.EventDocument;
import com.privasia.procurehere.core.entity.RfiEventDocument;

public interface RfiDocumentDao extends GenericDao<RfiEventDocument, String> {

	/**
	 * @param eventId
	 * @return
	 */
	List<RfiEventDocument> findAllRfiEventdocsbyEventId(String eventId);

	/**
	 * @param id
	 */
	void deleteById(String id);

	/**
	 * @param eventId
	 * @return
	 */
	RfiEventDocument findRfiDocsById(String id);

	/**
	 * @param eventId
	 * @return
	 */
	Integer getCountOfRfiDocumentByEventId(String eventId);

	void deleteByEventId(String eventId);

	/**
	 * @param id
	 * @return
	 */
	List<EventDocument> findAllEventDocsNameAndId(String id);

	List<RfiEventDocument> findAllRfidocsForZipbyEventId(String id);
	
	 List<EventDocument> findAllRfiEventDocsNameByEventId(String eventId);

	List<EventDocument> findAllRfiEventDocsByEventIdAndDocIds(String id, List<String> docIds);

	List<EventDocument> findAllRfiEventDocsNamesByEventIdAndDocIds(String id, List<String> docIds);

	List<EventDocument> findAllRfiEventDocsNamesByEventIdAndDocIds(List<String> docIds);
}

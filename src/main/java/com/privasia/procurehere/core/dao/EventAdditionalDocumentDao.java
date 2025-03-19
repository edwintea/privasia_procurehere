package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.AdditionalDocument;

/**
 * @author pooja
 */
public interface EventAdditionalDocumentDao extends GenericDao<AdditionalDocument, String> {

	List<AdditionalDocument> findAllRftEventdocsbyEventId(String eventId);

	AdditionalDocument findRftDocsById(String docId);
}

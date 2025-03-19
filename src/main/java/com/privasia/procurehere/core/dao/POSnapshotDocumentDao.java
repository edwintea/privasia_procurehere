package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.POSnapshotDocument;

/**
 * @author ravi
 */
public interface POSnapshotDocumentDao extends GenericDao<POSnapshotDocument, String> {

	/**
	 * @param poId
	 * @return
	 */
	List<POSnapshotDocument> findAllPoDocsbyPoId(String poId);

	/**
	 * @param poId
	 * @return
	 */
	List<POSnapshotDocument> findAllPlainPoDocsbyPoId(String poId);

	/**
	 * @param poId
	 * @return
	 */
	List<POSnapshotDocument> findAllPoDocsNameAndId(String poId);

	/**
	 * @param poId TODO
	 */
	void deleteDocument(String poId);

	/**
	 * @param poId
	 * @return
	 */
	List<POSnapshotDocument> findAllPlainPoDocsbyPoIdForSupplier(String poId);

	/**
	 * 
	 * @param poId
	 * @param docId
	 * @return
	 */
	POSnapshotDocument findPoDocument(String poId, String docId);

}

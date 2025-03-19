package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.PoDocument;

/**
 * @author parveen
 */
public interface PoDocumentDao extends GenericDao<PoDocument, String> {
	/**
	 * @param prId
	 * @return
	 */
	List<PoDocument> findAllPlainPoDocsbyPrId(String prId);

}

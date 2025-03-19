package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.PrDocument;

/**
 * @author parveen
 */
public interface PrDocumentDao extends GenericDao<PrDocument, String> {

	/**
	 * @param prId
	 * @return
	 */
	List<PrDocument> findAllPrDocsbyPrId(String prId);

	/**
	 * @param prId
	 * @return
	 */
	List<PrDocument> findAllPlainPrDocsbyPrId(String prId);

	/**
	 * @param prId
	 * @return
	 */
	List<PrDocument> findAllPrDocsNameAndId(String prId);

}

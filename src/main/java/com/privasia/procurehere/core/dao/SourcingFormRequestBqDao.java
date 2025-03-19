package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.SourcingFormRequestBq;

/**
 * @author pooja
 */

public interface SourcingFormRequestBqDao extends GenericDao<SourcingFormRequestBq, String> {

	/**
	 * @param bq
	 * @param formId
	 * @return
	 */
	boolean isBqExists(String formId, String bqId, String name);

	/**
	 * @param formId
	 * @return
	 */
	List<SourcingFormRequestBq> findBqByFormId(String formId);

	/**
	 * @param id
	 */
	void deleteBq(String id);

	/**
	 * @param formId
	 * @return
	 */
	List<SourcingFormRequestBq> findBqsByFormId(String formId);

	/**
	 * @param bqId
	 * @return
	 */
	SourcingFormRequestBq findBqItemById(String bqId);

	/**
	 * @param formId
	 * @return
	 */
	List<SourcingFormRequestBq> findBqByFormIdByOrder(String formId);

	/**
	 * @param formId
	 * @return
	 */
	List<SourcingFormRequestBq> findRequestBqByFormIdByOrder(String formId);

}

package com.privasia.procurehere.service;

import java.io.File;
import java.util.List;

import com.privasia.procurehere.core.entity.SourcingFormRequest;
import com.privasia.procurehere.core.entity.SourcingFormRequestBq;
import com.privasia.procurehere.core.entity.SourcingFormRequestBqItem;
import com.privasia.procurehere.core.exceptions.ExcelParseException;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.SourcingBqItemPojo;
import com.privasia.procurehere.core.pojo.SourcingReqBqPojo;

/**
 * @author pooja
 */

public interface SourcingFormRequestBqService {

	/**
	 * @param formId
	 * @param bqId
	 * @param name
	 * @return
	 */
	boolean isBqExists(String formId, String bqId, String name);

	/**
	 * @param sourcingReqBqPojo
	 */
	SourcingFormRequestBq saveSourcingBq(SourcingReqBqPojo sourcingReqBqPojo);

	/**
	 * @param formId
	 * @return
	 */
	List<SourcingFormRequestBq> findBqByFormId(String formId);

	/**
	 * @param bqId
	 * @return
	 */
	SourcingFormRequestBq findBqById(String bqId);

	/**
	 * @param bqId
	 * @param itemLevel
	 * @param itemOrder
	 * @param searchVal
	 * @param start
	 * @param length
	 * @param pageNo
	 * @return
	 */
	List<SourcingFormRequestBqItem> getBqItemForSearchFilter(String bqId, Integer itemLevel, Integer itemOrder, String searchVal, Integer start, Integer length, Integer pageNo);

	/**
	 * @param bqId
	 * @param searchVal
	 * @return
	 */
	List<SourcingBqItemPojo> getAllLevelOrderBqItemByBqId(String bqId, String searchVal);

	/**
	 * @param bqId
	 * @param object
	 * @return
	 */
	long getTotalBqItemCountByBqId(String bqId, String searchVal);

	/**
	 * @param bq
	 * @return
	 */
	SourcingFormRequestBq updateSourcingBq(SourcingFormRequestBq bq);

	/**
	 * @param formId
	 * @return
	 */
	SourcingFormRequest getSourcingRequestBqByFormId(String formId);

	/**
	 * @param bq
	 * @return
	 */
	SourcingFormRequestBq getBqById(String bqId);

	/**
	 * @param bqParent
	 * @return
	 */
	SourcingFormRequestBqItem getBqItemsbyBqId(String bqParent);

	/**
	 * @param sourcingBqItem
	 * @param bq
	 * @param parent
	 * @return
	 */
	boolean isBqItemExists(SourcingFormRequestBqItem sourcingBqItem, String bq, String parent);

	/**
	 * @param sourcingBqItem
	 */
	SourcingFormRequestBqItem saveSourcingBqItem(SourcingFormRequestBqItem sourcingBqItem);

	/**
	 * @param sourcingReqBqItem
	 * @return
	 */
	SourcingFormRequestBqItem updateSourcingBqItem(SourcingFormRequestBqItem sourcingReqBqItem);

	/**
	 * @param id
	 */
	void deleteSourcingBq(String id);

	/**
	 * @param formId
	 * @return
	 */
	List<SourcingFormRequestBq> getAllBqListByFormId(String formId);

	/**
	 * @param bqItemId
	 * @return
	 */
	SourcingFormRequestBqItem getBqItembyBqItemId(String bqItemId);

	/**
	 * @param bqItemsArray
	 * @param bqId
	 * @throws NotAllowedException
	 */
	void deleteSourcingBqItems(String[] bqItemsIds, String bqId) throws NotAllowedException;

	/**
	 * @param sourcingBqItemPojo
	 * @throws NotAllowedException
	 */
	void reorderBqItems(SourcingBqItemPojo sourcingBqItemPojo) throws NotAllowedException;

	/**
	 * @param requestId
	 * @return boolean
	 * @throws NotAllowedException
	 */
	Boolean checkMandatoryToFinishEvent(String requestId) throws NotAllowedException;

	List<SourcingFormRequestBqItem> getAllbqItemByBqId(String id);

	/**
	 * @param id
	 * @return
	 */
	SourcingFormRequestBq getAllbqItemsByBqId(String id);

	/**
	 * @param bqId
	 * @param rfsId
	 * @param convFile
	 * @param tenantId
	 * @return
	 * @throws ExcelParseException
	 */
	int uploadBqFile(String bqId, String rfsId, File convFile, String tenantId) throws ExcelParseException;

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

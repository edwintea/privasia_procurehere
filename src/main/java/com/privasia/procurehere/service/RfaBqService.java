package com.privasia.procurehere.service;

import java.io.File;
import java.util.List;

import org.springframework.context.NoSuchMessageException;

import com.privasia.procurehere.core.entity.Bq;
import com.privasia.procurehere.core.entity.RfaBqItem;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.entity.RfaEventBq;
import com.privasia.procurehere.core.entity.RfaSupplierBq;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.exceptions.ExcelParseException;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.BqItemPojo;
import com.privasia.procurehere.core.pojo.BqPojo;

/**
 * @author RT-Kapil
 */

public interface RfaBqService {

	/**
	 * @param bqId
	 * @return
	 */
	List<RfaBqItem> findAllRfaEventBqbybqId(String bqId);

	/**
	 * @param id
	 * @return
	 */
	RfaBqItem getRfaBqItemById(String id);

	/**
	 * @param rfaBqItem
	 * @return
	 */
	RfaBqItem saveRfaBqItem(RfaBqItem rfaBqItem);

	/**
	 * @param id
	 * @return
	 */
	RfaBqItem getBqItemsbyBqId(String id);

	/**
	 * @param id
	 * @return
	 */
	RfaEventBq getBqItembyId(String id);

	/**
	 * @param id
	 * @return
	 */
	List<RfaEventBq> getAllBqListByEventId(String id);

	/**
	 * @param rfaBqItemPojo
	 * @param bqId TODO
	 * @param parentId TODO
	 * @return
	 */
	// boolean isBqItemExists(RfaBqItem rfaBqItem);
	boolean isBqItemExists(RfaBqItem rfaBqItemPojo, String bqId, String parentId);

	/**
	 * @param rfaEventBq
	 * @return
	 */
	RfaEventBq saveRfaBq(RfaEventBq rfaEventBq);

	/**
	 * @param bqItemIds
	 * @param bqId TODO
	 * @throws NotAllowedException
	 */
	void deleteBqItems(String[] bqItemIds, String bqId) throws NotAllowedException;

	/**
	 * @param rfaBqItem
	 */
	void updateRfaBqItems(RfaBqItem rfaBqItem);

	/**
	 * @param rfaBqPojo
	 * @return
	 */

	/**
	 * @param id
	 * @param bqIds
	 * @return
	 */
	List<RfaEventBq> getRfaEventBqForEventIdForEnvelop(String id, List<String> bqIds);

	/**
	 * @param id
	 * @return
	 */
	RfaEventBq getRfaBqById(String id);

	/**
	 * @param rfaBqItem
	 */
	void updateBqItem(RfaBqItem rfaBqItem);

	/**
	 * @param rfaBq
	 */
	void updateRfaBq(RfaEventBq rfaBq);

	/**
	 * @param id
	 */
	public void deleteRfaBq(String id);

	/**
	 * @param id
	 * @param eventType TODO
	 */
	void isAllowtoDeleteBQ(String id, RfxTypes eventType) throws Exception;

	/**
	 * @param bq
	 * @return
	 */
	RfaBqItem getBqItembyBqId(String bq);

	/**
	 * @param bqItemId
	 * @return
	 */
	RfaBqItem getBqItembyBqIdAndBqItemId(String bqItemId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfaEventBq> getNotAssignedRfaBqIdsByEventId(String eventId);

	/**
	 * @param bqId
	 * @return
	 */
	List<RfaBqItem> findRfaBqbyBqId(String bqId);

	/**
	 * @param rfaBqItemPojo
	 * @throws NotAllowedException
	 */
	void reorderBqItems(BqItemPojo rfaBqItemPojo) throws NotAllowedException;

	/**
	 * @param id
	 * @return
	 */
	RfaBqItem getBqItembyBqItemId(String parent);

	/**
	 * @param eventId
	 * @return
	 */
	Integer getCountOfRfaBqByEventId(String eventId);

	/**
	 * @param eventId
	 * @param eventRequirement
	 */
	void deleteAllRfaBq(String eventId, String eventRequirement);

	/**
	 * @param bqId
	 * @param label
	 */
	void deletefieldInBqItems(String bqId, String label);

	/**
	 * @param id
	 * @return
	 */
	RfaEvent getRfaEventById(String id);

	List<RfaBqItem> getAllBqItemListByEventId(String eventId);

	// List<RfaSupplierBqItem> getAllSupplierBqItemByBqIdAndSupplierId(String bqId, String supplierId);

	// List<RfaSupplierBqItem> saveSupplierEventBq(String bqId);

	/**
	 * @param bqId
	 * @return
	 */
	// List<RfaBqItem> getBqItemsbyId(String bqId);

	// RfaSupplierBqItem getSupplierBqItemByBqItemAndSupplierId(String itemId, String supplierId);

	boolean isBqExists(BqPojo bqPojo);

	List<Bq> findRfaBqbyEventId(String eventId);

	/**
	 * @param bqId
	 * @return
	 */
	List<RfaBqItem> getAllBqitemsbyBqId(String bqId);

	/**
	 * @param bqId
	 * @param level
	 * @return
	 */
	RfaBqItem getParentbyLevelId(String bqId, Integer level);

	RfaEvent getRfaEventBqById(String id);

	/**
	 * @param bqId
	 * @param eventId
	 * @param convFile
	 * @param tenantId TODO
	 * @return
	 * @throws ExcelParseException
	 */
	int uploadBqFile(String bqId, String eventId, File convFile, String tenantId) throws ExcelParseException;

	List<RfaSupplierBq> findRfaSupplierBqbyEventId(String eventId);

	/**
	 * @param eventBq
	 * @return
	 */
	RfaEventBq updateRfaBqFields(RfaEventBq eventBq);

	List<String> getNotSectionAddedRfaBqIdsByEventId(String id);

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
	List<RfaBqItem> getBqItemForSearchFilter(String bqId, Integer itemLevel, Integer itemOrder, String searchVal, Integer start, Integer length, Integer pageNo);

	/**
	 * @param bqId
	 * @param searchVal TODO
	 * @return
	 */
	List<BqItemPojo> getAllLevelOrderBqItemByBqId(String bqId, String searchVal);

	/**
	 * @param bqId
	 * @param searchVal
	 * @return
	 */
	long totalBqItemCountByBqId(String bqId, String searchVal);

	void deleteAllERPBqByEventId(String id);

	RfaBqItem saveUpdateRfaBqItem(RfaBqItem rfaBqItem);

	boolean checkIfBqItemExists(String eventId);

	List<String> getNotSectionItemAddedRfaBqIdsByEventId(String id);

	void deleteAllBqItems(String bqId) throws NoSuchMessageException, NotAllowedException;

	RfaEventBq getRfaEventBqByBqId(String bqId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfaEventBq> getAllBqListByEventIdByOrder(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	Boolean findPreSetPredBidAuctionRulesWithEventId(String eventId);

	List<RfaEventBq> findBqAndBqItemsByEventIdByOrder(String eventId);

}

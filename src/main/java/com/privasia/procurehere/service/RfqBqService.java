package com.privasia.procurehere.service;

import java.io.File;
import java.util.List;

import org.springframework.context.NoSuchMessageException;

import com.privasia.procurehere.core.entity.Bq;
import com.privasia.procurehere.core.entity.RfqBqItem;
import com.privasia.procurehere.core.entity.RfqEvent;
import com.privasia.procurehere.core.entity.RfqEventBq;
import com.privasia.procurehere.core.entity.RfqSupplierBqItem;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.exceptions.ExcelParseException;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.BqItemPojo;
import com.privasia.procurehere.core.pojo.BqPojo;

/**
 * @author Ravi
 * @author Kapil
 */

public interface RfqBqService {

	/**
	 * @param bqId
	 * @return
	 */
	List<RfqBqItem> findAllEventBqbybqId(String bqId);

	/**
	 * @param id
	 * @return
	 */
	RfqBqItem getBqItemById(String id);

	/**
	 * @param rftBqItem
	 * @return
	 */
	RfqBqItem saveBqItem(RfqBqItem rftBqItem);

	/**
	 * @param id
	 * @return
	 */
	RfqBqItem getBqItemsbyBqId(String id);

	/**
	 * @param id
	 * @return
	 */
	RfqEventBq getBqItembyId(String id);

	/**
	 * @param id
	 * @return
	 */
	List<RfqEventBq> getAllBqListByEventId(String id);

	/**
	 * @param item
	 * @param bqId TODO
	 * @param parentId TODO
	 * @return
	 */
	boolean isBqItemExists(RfqBqItem item, String bqId, String parentId);

	/**
	 * @param rftEventBq
	 * @return
	 */
	RfqEventBq saveBq(RfqEventBq rftEventBq);

	/**
	 * @param bqItemIds
	 * @param bqId TODO
	 * @throws NotAllowedException
	 */
	void deleteBqItems(String[] bqItemIds, String bqId) throws NotAllowedException;

	/**
	 * @param rftBqItem
	 */
	void updateBqItems(RfqBqItem rftBqItem);

	/**
	 * @param rftBqPojo
	 * @return
	 */
	boolean isBqExists(BqPojo rftBqPojo);

	/**
	 * @param id
	 * @param bqIds
	 * @return
	 */
	List<RfqEventBq> getEventBqForEventIdForEnvelop(String id, List<String> bqIds);

	/**
	 * @param id
	 * @return
	 */
	RfqEventBq getBqById(String id);

	/**
	 * @param rftBqItem
	 */
	void updateBqItem(RfqBqItem rftBqItem);

	/**
	 * @param rftBq
	 */
	void updateBq(RfqEventBq rftBq);

	/**
	 * @param id
	 */
	public void deleteBq(String id);

	/**
	 * @param id
	 * @param eventType TODO
	 */
	void isAllowtoDeleteBQ(String id, RfxTypes eventType) throws Exception;

	/**
	 * @param bq
	 * @return
	 */
	RfqBqItem getBqItembyBqId(String bq);

	/**
	 * @param bqItemId
	 * @return
	 */
	RfqBqItem getBqItembyBqIdAndBqItemId(String bqItemId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfqEventBq> getNotAssignedBqIdsByEventId(String eventId);

	/**
	 * @param bqId
	 * @return
	 */
	List<RfqBqItem> findBqbyBqId(String bqId);

	/**
	 * @param bqItemPojo
	 * @throws NotAllowedException
	 */
	void reorderBqItems(BqItemPojo bqItemPojo) throws NotAllowedException;

	/**
	 * @param id
	 * @return
	 */
	RfqBqItem getBqItembyBqItemId(String parent);

	/**
	 * @param eventId
	 * @return
	 */
	Integer getCountOfBqByEventId(String eventId);

	/**
	 * @param eventId
	 * @param eventRequirement
	 */
	void deleteAllBq(String eventId, String eventRequirement);

	/**
	 * @param bqId
	 * @param label
	 */
	void deletefieldInBqItems(String bqId, String label);

	/**
	 * @param id
	 * @return
	 */
	RfqEvent getEventById(String id);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfqBqItem> getAllBqItemListByEventId(String eventId);

	/**
	 * @param bqId
	 * @param supplierId
	 * @return
	 */
	List<RfqSupplierBqItem> getAllSupplierBqItemByBqIdAndSupplierId(String bqId, String supplierId);

	/**
	 * @param bqId
	 * @return
	 */
	// List<RfqSupplierBqItem> saveSupplierEventBq(String bqId);

	/**
	 * @param bqId
	 * @return
	 */
	List<RfqBqItem> getBqItemsbyId(String bqId);

	/**
	 * @param eventId
	 * @return
	 */
	List<Bq> findBqbyEventId(String eventId);

	/**
	 * @param bqId
	 * @param level
	 * @return
	 */
	RfqBqItem getParentbyLevelId(String bqId, Integer level);

	/**
	 * @param bqId
	 * @return
	 */
	List<RfqBqItem> getAllBqitemsbyBqId(String bqId);

	/**
	 * @param bqId
	 * @param eventId
	 * @param convFile
	 * @param tenantId TODO
	 * @throws ExcelParseException
	 */
	int uploadBqFile(String bqId, String eventId, File convFile, String tenantId) throws ExcelParseException;

	/**
	 * @param eventBq
	 * @return
	 */
	RfqEventBq updateRfqBqFields(RfqEventBq eventBq);

	List<String> getNotSectionAddedRfqBqIdsByEventId(String id);

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
	List<RfqBqItem> getBqItemForSearchFilter(String bqId, Integer itemLevel, Integer itemOrder, String searchVal, Integer start, Integer length, Integer pageNo);

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

	RfqBqItem saveUpdateRfqBqItem(RfqBqItem rfqBqItem);

	List<String> getNotSectionItemAddedRfqBqIdsByEventId(String id);

	void deleteAllBqItems(String bqId) throws NoSuchMessageException, NotAllowedException;

	RfqEventBq getRfqEventBqByBqId(String bqId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfqEventBq> getAllBqListByEventIdByOrder(String eventId);

}

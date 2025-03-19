package com.privasia.procurehere.service;

import java.io.File;
import java.util.List;

import org.springframework.context.NoSuchMessageException;

import com.privasia.procurehere.core.entity.Bq;
import com.privasia.procurehere.core.entity.RfpBqItem;
import com.privasia.procurehere.core.entity.RfpEvent;
import com.privasia.procurehere.core.entity.RfpEventBq;
import com.privasia.procurehere.core.entity.RfpSupplierBqItem;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.exceptions.ExcelParseException;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.BqItemPojo;
import com.privasia.procurehere.core.pojo.BqPojo;

/**
 * @author Ravi
 * @author Kapil
 */

public interface RfpBqService {

	/**
	 * @param bqId
	 * @return
	 */
	List<RfpBqItem> findAllEventBqbybqId(String bqId);

	/**
	 * @param id
	 * @return
	 */
	RfpBqItem getBqItemById(String id);

	/**
	 * @param rftBqItem
	 * @return
	 */
	RfpBqItem saveBqItem(RfpBqItem rftBqItem);

	/**
	 * @param id
	 * @return
	 */
	RfpBqItem getBqItemsbyBqId(String id);

	/**
	 * @param id
	 * @return
	 */
	RfpEventBq getBqItembyId(String id);

	/**
	 * @param id
	 * @return
	 */
	List<RfpEventBq> getAllBqListByEventId(String id);

	/**
	 * @param rftEventBq
	 * @return
	 */
	RfpEventBq saveBq(RfpEventBq rftEventBq);

	/**
	 * @param bqItemIds
	 * @param bqId TODO
	 * @throws NotAllowedException
	 */
	void deleteBqItems(String[] bqItemIds, String bqId) throws NotAllowedException;

	/**
	 * @param rftBqItem
	 */
	void updateBqItems(RfpBqItem rftBqItem);

	/**
	 * @param id
	 * @param bqIds
	 * @return
	 */
	List<RfpEventBq> getEventBqForEventIdForEnvelop(String id, List<String> bqIds);

	/**
	 * @param id
	 * @return
	 */
	RfpEventBq getBqById(String id);

	/**
	 * @param rftBqItem
	 */
	void updateBqItem(RfpBqItem rftBqItem);

	/**
	 * @param rftBq
	 */
	void updateBq(RfpEventBq rftBq);

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
	RfpBqItem getBqItembyBqId(String bq);

	/**
	 * @param bqItemId
	 * @return
	 */
	RfpBqItem getBqItembyBqIdAndBqItemId(String bqItemId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfpEventBq> getNotAssignedBqIdsByEventId(String eventId);

	/**
	 * @param bqId
	 * @return
	 */
	List<RfpBqItem> findRfpBqbyBqId(String bqId);

	/**
	 * @param rftBqItemPojo
	 * @throws NotAllowedException
	 */
	void reorderBqItems(BqItemPojo rftBqItemPojo) throws NotAllowedException;

	/**
	 * @param id
	 * @return
	 */
	RfpBqItem getBqItembyBqItemId(String parent);

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
	 * @param bqId TODO
	 * @param label
	 */
	void deletefieldInBqItems(String bqId, String label);

	/**
	 * @param id
	 * @return
	 */
	RfpEvent getRfpEventById(String id);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfpBqItem> getAllBqItemListByEventId(String eventId);

	/**
	 * @param bqId
	 * @param supplierId
	 * @return
	 */
	List<RfpSupplierBqItem> getAllSupplierBqItemByBqIdAndSupplierId(String bqId, String supplierId);

	/**
	 * @param bqId
	 * @return
	 */
	// List<RfpSupplierBqItem> saveSupplierEventBq(String bqId);

	/**
	 * @param bqId
	 * @return
	 */
	List<RfpBqItem> getBqItemsbyId(String bqId);

	/**
	 * @param eventId
	 * @return
	 */
	List<Bq> findBqbyEventId(String eventId);

	/**
	 * @param bqPojo
	 * @return
	 */
	boolean isBqExists(BqPojo bqPojo);

	/**
	 * @param item
	 * @param bqId TODO
	 * @param parentId TODO
	 * @return
	 */
	boolean isBqItemExists(RfpBqItem item, String bqId, String parentId);

	/**
	 * @param bqId
	 * @return
	 */
	List<RfpBqItem> getAllBqitemsbyBqId(String bqId);

	/**
	 * @param bqId
	 * @param level
	 * @return
	 */
	RfpBqItem getParentbyLevelId(String bqId, Integer level);

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
	RfpEventBq updateRfpBqFields(RfpEventBq eventBq);

	List<String> getNotSectionAddedRfpBqIdsByEventId(String id);

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
	List<RfpBqItem> getBqItemForSearchFilter(String bqId, Integer itemLevel, Integer itemOrder, String searchVal, Integer start, Integer length, Integer pageNo);

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

	RfpBqItem saveUpdateRfpBqItem(RfpBqItem rfpBqItem);

	List<String> getNotSectionItemAddedRfpBqIdsByEventId(String id);

	void deleteAllBqItems(String bqId) throws NoSuchMessageException, NotAllowedException;

	RfpEventBq getRfpEventBqByBqId(String bqId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfpEventBq> getAllBqListByEventIdByOrder(String eventId);

}

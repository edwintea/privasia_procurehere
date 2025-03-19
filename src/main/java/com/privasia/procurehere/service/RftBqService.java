package com.privasia.procurehere.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.NoSuchMessageException;

import com.privasia.procurehere.core.entity.Bq;
import com.privasia.procurehere.core.entity.RftBqItem;
import com.privasia.procurehere.core.entity.RftEvent;
import com.privasia.procurehere.core.entity.RftEventBq;
import com.privasia.procurehere.core.entity.RftSupplierBqItem;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.exceptions.ExcelParseException;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.BqItemPojo;
import com.privasia.procurehere.core.pojo.BqPojo;

/**
 * @author Ravi
 * @author Kapil
 */

public interface RftBqService {

	/**
	 * @param bqId
	 * @return
	 */
	List<RftBqItem> findAllRftEventBqbybqId(String bqId);

	/**
	 * @param id
	 * @return
	 */
	RftBqItem getRftBqItemById(String id);

	/**
	 * @param rftBqItem
	 * @return
	 */
	RftBqItem saveRftBqItem(RftBqItem rftBqItem);

	/**
	 * @param id
	 * @return
	 */
	RftBqItem getBqItemsbyBqId(String id);

	/**
	 * @param id
	 * @return
	 */
	RftEventBq getBqItembyId(String id);

	/**
	 * @param id
	 * @return
	 */
	List<RftEventBq> getAllBqListByEventId(String id);

	/**
	 * @param item
	 * @param bqId TODO
	 * @param parentId TODO
	 * @return
	 */
	boolean isBqItemExists(RftBqItem item, String bqId, String parentId);

	/**
	 * @param rftEventBq
	 * @return
	 */
	RftEventBq saveRftBq(RftEventBq rftEventBq);

	/**
	 * @param bqItemIds
	 * @param eventId TODO
	 * @throws NotAllowedException
	 */
	void deleteBqItems(String[] bqItemIds, String bqId) throws NotAllowedException;

	/**
	 * @param rftBqItem
	 */
	void updateRftBqItems(RftBqItem rftBqItem);

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
	List<RftEventBq> getRftEventBqForEventIdForEnvelop(String id, List<String> bqIds);

	/**
	 * @param id
	 * @return
	 */
	RftEventBq getRftBqById(String id);

	/**
	 * @param rftBqItem
	 */
	void updateBqItem(RftBqItem rftBqItem);

	/**
	 * @param rftBq
	 */
	void updateRftBq(RftEventBq rftBq);

	/**
	 * @param id
	 */
	public void deleteRftBq(String id);

	/**
	 * @param id
	 * @param eventType TODO
	 */
	void isAllowtoDeleteBQ(String id, RfxTypes eventType) throws Exception;

	/**
	 * @param bq
	 * @return
	 */
	RftBqItem getBqItembyBqId(String bq);

	/**
	 * @param bqItemId
	 * @return
	 */
	RftBqItem getBqItembyBqIdAndBqItemId(String bqItemId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RftEventBq> getNotAssignedRftBqIdsByEventId(String eventId);

	/**
	 * @param bqId
	 * @return
	 */
	List<RftBqItem> findRftBqbyBqId(String bqId);

	/**
	 * @param rftBqItemPojo
	 * @throws NotAllowedException
	 */
	void reorderBqItems(BqItemPojo rftBqItemPojo) throws NotAllowedException;

	/**
	 * @param id
	 * @return
	 */
	RftBqItem getBqItembyBqItemId(String parent);

	/**
	 * @param eventId
	 * @return
	 */
	Integer getCountOfRftBqByEventId(String eventId);

	/**
	 * @param eventId
	 * @param eventRequirement
	 */
	void deleteAllRftBq(String eventId, String eventRequirement);

	/**
	 * @param bqId TODO
	 * @param label
	 */
	void deletefieldInBqItems(String bqId, String label);

	/**
	 * @param id
	 * @return
	 */
	RftEvent getRftEventById(String id);

	List<RftBqItem> getAllBqItemListByEventId(String eventId);

	/**
	 * @param bqId
	 * @return
	 */
	// List<RftBqItem> getBqItemsbyId(String bqId);

	// RftSupplierBqItem getSupplierBqItemByBqItemAndSupplierId(String itemId, String supplierId);

	RftSupplierBqItem updateRftSupplierBqItem(RftSupplierBqItem persistObject);

	/**
	 * @param eventId
	 * @return
	 */
	List<Bq> findRftBqbyEventId(String eventId);

	/**
	 * @param bqId
	 * @return
	 */
	List<RftBqItem> getAllBqitemsbyBqId(String bqId);

	/**
	 * @param level
	 * @return
	 */
	RftBqItem getParentbyLevelId(String bqId, int level);

	/**
	 * @param bqId
	 * @param eventId
	 * @param convFile
	 * @param tenantId TODO
	 */
	int uploadBqFile(String bqId, String eventId, File convFile, String tenantId) throws ExcelParseException;

	/**
	 * @param eventId
	 * @return
	 */
	Integer countAllRftBqItemByEventId(String eventId);

	/**
	 * @param rftBq
	 * @return TODO
	 */
	RftEventBq updateRftBqFields(RftEventBq rftBq);

	List<String> getNotSectionAddedRftBqIdsByEventId(String eventId);

	/**
	 * @param bqId
	 * @param itemLevel
	 * @param itemOrder
	 * @param start
	 * @param length
	 * @param pageNo
	 * @return
	 */
	List<RftBqItem> getBqItemForSearchFilter(String bqId, Integer itemLevel, Integer itemOrder, String searchVal, Integer start, Integer length, Integer pageNo);

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

	/**
	 * @param id
	 */
	void deleteAllERPBqByEventId(String id);

	RftBqItem saveUpdateRftBqItem(RftBqItem rftBqItem);

	/**
	 * @param workbook
	 * @param bqid
	 * @param rfa
	 * @return
	 * @throws IOException
	 */
	XSSFWorkbook eventDownloader(String bqid, RfxTypes rfa) throws IOException;

	List<String> getNotSectionItemAddedRftBqIdsByEventId(String id);

	void deleteAllBqItems(String bqId) throws NoSuchMessageException, NotAllowedException;

	RftEventBq getRftEventBqByBqId(String bqId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RftEventBq> getAllBqListByEventIdByOrder(String eventId);

}

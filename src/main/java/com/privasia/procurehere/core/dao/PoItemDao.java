package com.privasia.procurehere.core.dao;

import java.util.Date;
import java.util.List;

import com.privasia.procurehere.core.entity.PoItem;
import com.privasia.procurehere.core.entity.PrItem;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.PoItemPojo;
import com.privasia.procurehere.core.pojo.PoItemSupplierPojo;
import com.privasia.procurehere.core.pojo.SearchFilterPoPojo;

/**
 * @author parveen
 */
public interface PoItemDao extends GenericDao<PoItem, String> {

	/**
	 * @param poId
	 * @return
	 */
	List<PoItem> getAllPoItemByPoId(String poId);

	/**
	 * @param poId
	 * @return
	 */
	List<PoItem> getAllPoItemByPoIdPojo(String poId);


	/**
	 * @param prId
	 * @return
	 */
	List<PrItem> getPrItemLevelOrder(String prId);

	/**
	 * @param prId
	 * @param prItemId
	 * @return
	 */
	PrItem getPrItembyPrIdAndPrItemId(String prId, String prItemId);

	/**
	 * @param prItemIds
	 * @param prId
	 * @return
	 */
	String deletePrItems(String[] prItemIds, String prId);

	/**
	 * @param prId
	 * @return
	 */
	List<PrItem> getPrItemsbyId(String prId);

	/**
	 * @param poId
	 * @return
	 */
	List<PoItem> getPoItemLevelOrder(String poId);

	/**
	 * @param itemId
	 * @return
	 */
	PrItem getPrItembyPrItemId(String itemId);

	/**
	 * @param poId
	 * @param poItem
	 * @param oldParent
	 * @param newParent
	 * @param oldOrder
	 * @param newOrder
	 * @param oldLevel
	 * @param newLevel
	 */
	void updateItemOrder(String poId, PoItem poItem, String oldParent, String newParent, int oldOrder, int newOrder, int oldLevel, int newLevel);

	/**
	 * @param label
	 * @param prId
	 */
	void deleteNewFieldPr(String label, String prId);

	/**
	 * @param prId
	 * @param level
	 * @return
	 */
	PrItem getParentbyLevelId(String prId, Integer level);

	/**
	 * @param prId
	 */
	void deletePrItemsbyPrid(String prId);

	/**
	 * @param prId
	 * @return
	 */
	List<PrItem> findAllChildPrItemByPrId(String prId);

	/**
	 * @param productId
	 * @return
	 */
	boolean checkProductInUse(String productId);

	/**
	 * @param prId
	 * @return
	 */
	String updateOnDeletePrItems(String prId);

	String deletePrItemsByPrId(String prId);

	/**
	 * @param prId
	 * @return
	 */
	Long findProductCategoryCountByPrId(String prId);

	/**
	 * @param itemId
	 */
	void deletePoItemsbyId(String itemId);

	/**
	 * @param prId
	 * @param prItemId
	 * @return
	 */
	PoItem getPoItembyPoIdAndPoItemId(String prId, String prItemId);

	/**
	 * @param poId
	 * @return
	 */
	List<PoItem> getPoItemListByPoId(String poId);

	/**
	 * @param prItemId
	 * @param poId
	 * @return
	 * @throws ApplicationException
	 */
	String deletePoItems(String prItemId, String poId) throws ApplicationException;

	/**
	 * @param prId
	 * @return
	 */
	String updateOnDeletePoItem(String prId);

	/**
	 * @param poItemIds
	 * @param poId
	 * @return
	 */
	String deletePoItems(String[] poItemIds, String poId);

	/**
	 * @param prItemsIds
	 * @param poId TODO
	 * @return
	 */
	Long findItemCountByPrItemIds(String[] prItemsIds, String poId);

	List<PoItemPojo> findPoItemForTenantIdForCsv(String tenantId, int pageSize, int pageNo, String[] poIds, SearchFilterPoPojo searchFilterPoPojo, boolean select_all, Date startDate, Date endDate, String userId,String poType,List<String> businessUnitIds,String status);

	List<PoItemSupplierPojo> findPoItemSummaryForSupplierCsvReport(String tenantId, int pAGE_SIZE, int pageNo,
			String[] poIds, SearchFilterPoPojo searchFilterPoPojo, boolean select_all, Date startDate, Date endDate);

	long findTotalPoItemSupplierSummaryCountForCsv(String tenantId, Date startDate, Date endDate);

	/**
	 * @param poItem
	 * @return
	 */
	boolean isExists(PoItem poItem,boolean isSection);

	/**
	 * @param poId
	 * @return
	 */
	List<PoItem> findAllChildPoItemByPoId(String poId);

	String deletePoItemsByPoId(String poId);

	/**
	 * @param poId
	 * @return
	 */
	List<PoItem> getPoItemsbyId(String poId);

	/**
	 * @param label
	 * @param poId
	 */
	void deleteNewFieldPo(String label, String poId);

	/**
	 * @param itemId
	 * @return
	 */
	PoItem getPoItembyPoItemId(String itemId);
}

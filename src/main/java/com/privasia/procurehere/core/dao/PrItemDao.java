package com.privasia.procurehere.core.dao;

import java.text.ParseException;
import java.util.List;

import com.privasia.procurehere.core.entity.PrItem;
import com.privasia.procurehere.core.pojo.SpentAnalysisPojo;

/**
 * @author parveen
 */
public interface PrItemDao extends GenericDao<PrItem, String> {

	/**
	 * @param prItem
	 * @return
	 */
	boolean isExists(PrItem prItem);

	/**
	 * @param prId
	 * @return
	 */
	List<PrItem> getAllPrItemByPrId(String prId);

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
	 * @param itemId
	 * @return
	 */
	PrItem getPrItembyPrItemId(String itemId);

	/**
	 * @param prId
	 * @param prItem
	 * @param oldParent
	 * @param newParent
	 * @param oldOrder
	 * @param newOrder
	 * @param oldLevel
	 * @param newLevel
	 */
	void updateItemOrder(String prId, PrItem prItem, String oldParent, String newParent, int oldOrder, int newOrder, int oldLevel, int newLevel);

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

	// For Spent analysis
	List<SpentAnalysisPojo> findPrItemForSpentAnalysis(int month, int year) throws ParseException;

	List<SpentAnalysisPojo> findPrItemValueForSpentAnalysis(int month, int year) throws ParseException;

	List<SpentAnalysisPojo> findPrItemForSpentAnalysisForSubsidiary(int month, int year) throws ParseException;

	List<SpentAnalysisPojo> findPrItemForSpentAnalysisForNonSubsidiary(int month, int year) throws ParseException;

	List<SpentAnalysisPojo> findPrItemValueForSpentAnalysisForSubsidiary(int month, int year) throws ParseException;

	List<SpentAnalysisPojo> findPrItemValueForSpentAnalysisForNonSubsidiary(int month, int year) throws ParseException;

}

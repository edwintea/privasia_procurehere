package com.privasia.procurehere.service;

import java.util.Date;
import java.util.List;

import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.FavouriteSupplier;
import com.privasia.procurehere.core.entity.Po;
import com.privasia.procurehere.core.entity.Pr;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.pojo.GlobalSearchPojo;

/**
 * @author Vipul
 */
public interface GlobalSearchService {

	/**
	 * @param searchVal
	 * @param tenantId
	 * @param isSupplier
	 * @param userId
	 * @param loggedInUser TODO
	 * @return
	 */
	List<GlobalSearchPojo> findAllRfxEventGlobally(String searchVal, String tenantId, boolean isSupplier, String userId, String loggedInUser);

	/**
	 * @param searchVal
	 * @return
	 */
	List<Buyer> findAllBuyerGlobally(String searchVal);

	/**
	 * @param searchVal
	 * @return
	 */
	List<Supplier> findAllSupplierGlobally(String searchVal);

	/**
	 * @param searchVal
	 * @param tenantId
	 * @param isSupplier
	 * @param userId
	 * @param status
	 * @param type
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	List<Pr> findAllPrPoGlobally(String searchVal, String tenantId, boolean isSupplier, String userId, String status, String type, Date startDate, Date endDate);

	/**
	 * @param searchVal
	 * @param tenantId
	 * @return
	 */
	List<FavouriteSupplier> findAllFavouriteSupplierGlobally(String searchVal, String tenantId);

	List<Supplier> findAllSupplierGloballyForFinance(String searchVal, String tenantId);

	List<Po> findAllPrPoGloballyForFinance(String searchVal, String tenantId);

	/**
	 * @param searchVal
	 * @param tenantId
	 * @param status
	 * @param type
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	List<Po> findAllPoGlobally(String searchVal, String tenantId, String status, String type, Date startDate, Date endDate);

}

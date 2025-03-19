package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.BuyerAddress;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author Vipul
 */

public interface BuyerAddressDao extends GenericDao<BuyerAddress, String> {

	/**
	 * @param BuyerAddress
	 */
	void updateBuyerAddress(BuyerAddress buyeraddress);

	/**
	 * @return
	 */
	List<BuyerAddress> findAllAddressesForTenant(String tenantId);

	/**
	 * @param tenantId
	 * @param tableParams
	 * @return
	 */
	List<BuyerAddress> findBuyerAddressForTenant(String tenantId, TableDataInput tableParams);

	/**
	 * @param tenantId
	 * @param tableParams
	 * @return
	 */
	long findTotalFilteredBuyerAddressForTenant(String tenantId, TableDataInput tableParams);

	/**
	 * @param tenantId
	 * @return
	 */
	long findTotalBuyerAddressForTenant(String tenantId);

	/**
	 * @param buyerAddress
	 * @param tenantId     TODO
	 * @return
	 */
	boolean isExists(BuyerAddress buyerAddress, String tenantId);

	/**
	 * @param loggedInUserTenantId
	 * @return
	 */
	List<BuyerAddress> getBuyerAddressForTenant(String loggedInUserTenantId);

	/**
	 * @param title
	 * @param tenantId
	 * @return
	 */
	BuyerAddress getBuyerAddressForTenantByTitle(String title, String tenantId);

	/**
	 * @param tenantId
	 * @return
	 */
	long findBuyerAddressCountForTenant(String tenantId);

	/**
	 * @param tenantId
	 * @param pageSize
	 * @param pageNo
	 * @return
	 */
	List<BuyerAddress> findBuyerAddressListForTenantIdForCsv(String tenantId, int pageSize, int pageNo);

	/**
	 * 
	 * @param addressId
	 * @return
	 */
	BuyerAddress getBuyerAddressAndStateAndCountryById(String addressId);

}

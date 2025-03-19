package com.privasia.procurehere.service;

import java.io.File;
import java.util.List;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import com.privasia.procurehere.core.entity.BuyerAddress;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.pojo.BuyerAddressPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;;

/**
 * @author Vipul
 */

public interface BuyerAddressService {

	/**
	 * @param buyeraddress
	 * @return
	 */
	String createBuyerAddress(BuyerAddress buyeraddress);

	/**
	 * @param buyeraddress
	 */
	void updateBuyerAddress(BuyerAddress buyeraddress);

	/**
	 * @param buyeraddress
	 * @param loggedInUser TODO
	 */
	void deleteBuyerAddress(BuyerAddress buyeraddress, User loggedInUser);

	/**
	 * @param id
	 * @return
	 */
	List<BuyerAddressPojo> getAllBuyerAddressPojo(String id);

	/**
	 * @param id
	 * @return
	 */
	BuyerAddress getBuyerAddress(String id);

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
	 * @param tenatId TODO
	 * @return
	 */
	boolean isExists(BuyerAddress buyerAddress, String tenatId);
	
	/**
	 * 
	 * @param response
	 * @param loggedInUserTenantId
	 */
	void buyerAddressDownloadTemplate(HttpServletResponse response, String loggedInUserTenantId);

	void buyerAddressExcelToZip(ZipOutputStream zos, String loggedInUserTenantId, String parentFolder);

	/**
	 * @param response
	 * @param file
	 * @param tenantId
	 */
	void downloadBuyerAddressCsvFile(HttpServletResponse response, File file, String tenantId);

}

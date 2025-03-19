package com.privasia.procurehere.service;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.Owner;
import com.privasia.procurehere.core.entity.Uom;
import com.privasia.procurehere.core.enums.TenantType;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.pojo.UomPojo;

public interface UomService {

	String createUom(Uom uom);

	Uom updateUom(Uom uom);

	void deleteUom(Uom uom);

	boolean isExists(Uom uom, TenantType tenantType);

	Uom getUomById(String id);

	List<UomPojo> getAllUomPojo(String buyerId);

	/**
	 * @param uom
	 * @param tenantId TODO
	 * @return
	 */
	Uom getUombyCode(String uom, String tenantId);

	/**
	 * @param tenantId
	 * @param tableParams
	 * @param tenantType TODO
	 * @return
	 */
	List<Uom> findUomForTenant(String tenantId, TableDataInput tableParams, TenantType tenantType);

	/**
	 * @param tenantId
	 * @param tableParams
	 * @param tenantType TODO
	 * @return
	 */
	long findTotalFilteredUomForTenant(String tenantId, TableDataInput tableParams, TenantType tenantType);

	/**
	 * @param tenantId
	 * @return
	 */
	long findTotalActiveUomForTenant(String tenantId, TenantType tenantType);

	/**
	 * @param tenantId
	 * @return
	 */
	List<Uom> getAllActiveUomForTenant(String tenantId);

	/**
	 * @param buyer
	 * @param owner
	 */
	void loadDefaultUomIntoBuyerAccount(Buyer buyer, Owner owner);

	/**
	 * @param uom
	 * @param tenantId
	 * @return
	 */
	Uom getUomByUomAndTenantId(String uom, String tenantId);

	void uomDownloadTemplate(HttpServletResponse response, String loggedInUserTenantId);

	void uomExcelToZip(ZipOutputStream zos, String loggedInUserTenantId, String zipFileNames);

	boolean isExists(String uom, String buyerId);

	String createUom(Uom uom, Boolean true1);

	Uom updateUom(Uom uom, Boolean true1);

	List<UomPojo> fetchAllActiveUomForTenant(String tenantId, String search);

	void downloadCsvFileForUom(HttpServletResponse response, File file, UomPojo uomPojo, String loggedInUserTenantId, TenantType tenantType);
}

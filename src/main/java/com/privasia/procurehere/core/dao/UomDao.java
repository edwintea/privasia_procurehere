package com.privasia.procurehere.core.dao;

import java.util.Date;
import java.util.List;

import com.privasia.procurehere.core.entity.Uom;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.TenantType;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.pojo.UomPojo;

public interface UomDao extends GenericDao<Uom, String> {

	/**
	 * 
	 * @param tenantId
	 * @param tenantType TODO
	 * @return
	 */
	List<Uom> getAllActiveUomForTenant(String tenantId, TenantType tenantType);

	/**
	 * 
	 * @param uom
	 * @param tenantType TODO
	 * @return
	 */
	boolean isExists(Uom uom, TenantType tenantType);

	/**
	 * 
	 * @param uom
	 * @param tenantId TODO
	 * @return
	 */
	Uom getUombyCode(String uom, String tenantId);

	/**
	 * 
	 * @param tenantId
	 * @param tableParams
	 * @param tenantType TODO
	 * @return
	 */
	List<Uom> findUomForTenant(String tenantId, TableDataInput tableParams, TenantType tenantType);

	/**
	 * 
	 * @param tenantId
	 * @param tableParams
	 * @param tenantType TODO
	 * @return
	 */
	long findTotalFilteredUomForTenant(String tenantId, TableDataInput tableParams, TenantType tenantType);

	/**
	 * 
	 * @param tenantId
	 * @return
	 */
	long findTotalActiveUomForTenant(String tenantId, TenantType tenantType);

	void loadUomMasterData(List<Uom> list, User createdBy);

	boolean isExists(Uom uom);

	/**
	 * 
	 * @param uom
	 * @param tenantId
	 * @return
	 */
	Uom getUomByUomAndTenantId(String uom, String tenantId);

	/**
	 * 
	 * @param loggedInUserTenantId
	 * @param buyer
	 * @return
	 */
	List<Uom> getAllUomForTenant(String loggedInUserTenantId, TenantType buyer);

	boolean isExists(String uom, String buyerId);

	/**
	 * @param tenantId
	 * @param search
	 * @return
	 */
	List<UomPojo> fetchAllActiveUomForTenant(String tenantId, String search);

	/**
	 * @param tenantId
	 * @return
	 */
	long countConstructQueryToFetchUom(String tenantId);

	long findTotalUomCountForCsv(String tenantId,TenantType tenantType);

	List<UomPojo> findAllActiveUomForTenantIdForCsv(String tenantId, TenantType tenantType, int pAGE_SIZE, int pageNo, String[] eventArr, UomPojo uomPojo, boolean select_all, Date startDate, Date endDate);

	List<Uom> getAllUomForCsv(String loggedInUserTenantId, TenantType buyer, int pAGE_SIZE, int pageNo);
	
}

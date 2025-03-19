package com.privasia.procurehere.service;

import java.io.File;
import java.util.List;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import com.privasia.procurehere.core.entity.AgreementType;
import com.privasia.procurehere.core.entity.ProcurementCategories;
import com.privasia.procurehere.core.pojo.TableDataInput;

public interface AgreementTypeService {

	String createAgreementType(AgreementType agreementType);

	AgreementType updateAgreementType(AgreementType agreementType);

	void deleteAgreementType(AgreementType agreementType);

	boolean isExists(AgreementType agreementType);

	AgreementType getAgreementTypeById(String id);

	List<AgreementType> getAllAgreementType(String tenantId);

	/**
	 * 
	 * @param agreementType
	 * @param tenantId
	 * @return
	 */
	AgreementType getAgreementTypebyCode(String agreementType, String tenantId);

	/**
	 * @param tenantId
	 * @param tableParams
	 * @return
	 */
	List<AgreementType> findAgreementTypeForTenant(String tenantId, TableDataInput tableParams);

	/**
	 * @param tenantId
	 * @param tableParams
	 * @return
	 */
	long findTotalFilteredAgreementTypeForTenant(String tenantId, TableDataInput tableParams);

	/**
	 * @param tenantId
	 * @return
	 */
	long findTotalActiveAgreementTypeForTenant(String tenantId);

	/**
	 * @param tenantId
	 * @return
	 */
	List<AgreementType> getAllActiveAgreementTypeForTenant(String tenantId);

	/**
	 * @param uom
	 * @param tenantId
	 * @return
	 */
	AgreementType getAgreementTypeByAgreementTypeAndTenantId(String agreementType, String tenantId);

	/**
	 * 
	 * @param tenantId
	 * @param searchVal
	 * @return
	 */
	List<AgreementType> fetchAllAgreementTypeForTenant(String tenantId, String searchVal);

	/**
	 * 
	 * @param tenantId
	 * @param search
	 * @return
	 */
	List<AgreementType> fetchAllActiveAgreementTypeForTenant(String tenantId, String search);

	void agreementTypeDownloadTemplate(HttpServletResponse response, String loggedInUserTenantId);

	void agreementTypeExcelToZip(ZipOutputStream zos, String loggedInUserTenantId, String zipFileNames);

	boolean isExists(String uom);

	void downloadCsvFileForAgreementType(HttpServletResponse response, File file, AgreementType agreementType, String loggedInUserTenantId);

	List<AgreementType> getAllAgreementTypeIdByBusinessUnitId(String buId);

	long getCountOfInactiveAgreementType(String buId);

	List<AgreementType> fetchAllAgreementTypeForTenantForUnit(String tenantId, String searchVal, String buId);

	List<AgreementType> findAgreementTypeListByTenantId(String tenantId, TableDataInput input, String id, String[] agreementTypeIds, String[] removeIds);

	long findTotalFilteredAgreementTypeForTenant(String tenantId, TableDataInput input, String id, String[] agreementTypeIds, String[] removeIds);

	List<AgreementType>  getAllActiveAgreementType(String loggedInUserTenantId);
}

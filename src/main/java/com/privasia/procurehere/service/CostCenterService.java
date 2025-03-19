package com.privasia.procurehere.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

import com.privasia.procurehere.core.entity.CostCenter;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.pojo.CostCenterPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author RT-Kapil
 */
public interface CostCenterService {

	/**
	 * @param costCenter
	 */
	public void saveCostCenter(CostCenter costCenter);

	/**
	 * @param costCenter
	 */
	public void updateCostCenter(CostCenter costCenter);

	/**
	 * @param costCenter
	 */
	public void deleteCostCenter(CostCenter costCenter);

	/**
	 * @param costCenter
	 * @param tenantId TODO
	 * @return
	 */
	boolean isExists(CostCenter costCenter, String tenantId);

	/**
	 * @param id
	 * @return
	 */
	CostCenter getCostCenterById(String id);

	/**
	 * @param tenantId
	 * @param tableParams
	 * @return
	 */
	public List<CostCenter> findCostCentersForTenant(String tenantId, TableDataInput tableParams);

	/**
	 * @param tenantId
	 * @param tableParams
	 * @return
	 */
	public long findTotalFilteredCostCentersForTenant(String tenantId, TableDataInput tableParams);

	/**
	 * @param tenantId
	 * @return
	 */
	public long findTotalCostCentersForTenant(String tenantId);

	/**
	 * @param tenantId
	 * @return
	 */
	List<CostCenter> getAllActiveCostCentersForTenant(String tenantId);

	/**
	 * @param tenantId
	 * @return
	 */

	public List<CostCenter> getAllCostCentersByTenantId(String tenantId);

	/**
	 * @param response
	 * @param loggedInUserTenantId
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void costCenterDownloadTemplate(HttpServletResponse response, String loggedInUserTenantId) throws FileNotFoundException, IOException;

	/**
	 * @param file
	 * @param tenantId
	 * @param loggedInUser
	 * @throws Exception
	 */
	void costCenterUploadFile(MultipartFile file, String tenantId, User loggedInUser) throws Exception;

	public void costCenterExcelToZip(ZipOutputStream zos, String loggedInUserTenantId, String parentFolder);

	/**
	 * @param tenantId
	 * @return
	 */
	List<CostCenter> getActiveCostCentersForTenant(String tenantId);

	/**
	 * @param costCenterName
	 * @param tenantId
	 * @return
	 */
	CostCenter getActiveCostCenterForTenantByCostCenterName(String costCenterName, String tenantId);

	/**
	 * @param tenantId
	 * @param searchValue
	 * @param businessUnitId TODO
	 * @return
	 */
	public List<CostCenterPojo> fetchAllCostCenterForTenant(String tenantId, String searchValue, String businessUnitId);

	/**
	 * @param id
	 * @return
	 */
	public List<String> getCostCenterByBusinessId(String id);

	/**
	 * @param tenantId
	 * @param searchValue
	 * @param assignedCostId
	 * @param buId TODO
	 * @return
	 */
	public List<CostCenterPojo> fetchAllCostCenterForTenantForUnit(String tenantId, String searchValue, List<String> assignedCostId, String buId);

	/**
	 * @param tenantId
	 * @param input
	 * @param id
	 * @return
	 */
	public List<CostCenterPojo> findCostCenterListByTenantId(String tenantId, TableDataInput input, String id, String[] costCenterIds, String[] removeIds);

	/**
	 * @param tenantId
	 * @param input
	 * @param id
	 * @return
	 */
	public long findTotalFilteredCostCenterForTenant(String tenantId, TableDataInput input, String id, String[] costCenterIds, String[] removeIds);

	/**
	 * @param id
	 * @return
	 */
	public CostCenterPojo getCostCenterByCostId(String id);

	/**
	 * @param id
	 */
	public void deleteAssignedCostCenter(String id);

	/**
	 * @param costCenterId
	 * @return
	 */
	public CostCenter getCostCenterBycostId(String costCenterId);

	/**
	 * @param assignedCostId
	 * @return
	 */
	public long getCountOfInactiveCostCenter(List<String> assignedCostId);

	/**
	 * @param buId
	 * @return
	 */
	public List<String> getListOfAssignedCostCenterIdsForBusinessUnit(String buId);

	/**
	 * @param response
	 * @param file
	 * @param tenantId
	 */
	public void downloadCostCenterCsvFile(HttpServletResponse response, File file, String tenantId);

	public List<CostCenter> getCostCentersByBusinessUnitIdForAwardScreen(String id);

}

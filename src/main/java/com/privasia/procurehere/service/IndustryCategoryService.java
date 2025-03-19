package com.privasia.procurehere.service;

import java.io.File;
import java.util.List;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import com.privasia.procurehere.core.entity.IndustryCategory;
import com.privasia.procurehere.core.entity.NaicsCodes;
import com.privasia.procurehere.core.entity.ProductCategory;
import com.privasia.procurehere.core.pojo.IndustryCategoryPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author RT-Kapil
 * @author arc
 */

public interface IndustryCategoryService {

	/**
	 * @param industryCategory
	 */
	public void save(IndustryCategory industryCategory);

	/**
	 * @param industryCategory
	 */
	public void update(IndustryCategory industryCategory);

	/**
	 * @param industryCategory
	 */
	public void delete(IndustryCategory industryCategory);

	/**
	 * @param industryCategory
	 * @param tenantId TODO
	 * @return
	 */
	boolean isExists(IndustryCategory industryCategory, String tenantId);

	/**
	 * @param id
	 * @return
	 */
	IndustryCategory getIndustryCategoryById(String id);

	/**
	 * @return
	 */
	List<NaicsCodes> findAllLeafIndustryCategory();

	/**
	 * @param code
	 * @param tenantId TODO
	 * @return
	 */
	IndustryCategory getIndustryCategoryByCode(String code, String tenantId);

	

	IndustryCategory getIndustryCategoryByCodeExceptStatus(String code, String tenantId);

	/**
	 * @param ids
	 * @return
	 */
	List<IndustryCategory> getAllIndustryCategoryByIds(List<String> ids);

	/**
	 * @return
	 */
	long countIndustryCategory();

	/**
	 * @param searchValue
	 * @param tenantId
	 * @return
	 */
	List<IndustryCategory> findIndustryCategoryByNameAndTenantId(String searchValue, String tenantId);

	/**
	 * @param tenantId
	 * @param userId
	 */
	void loadNaicsCodesForTenant(String tenantId, String userId);

	/**
	 * @param tenantId
	 * @param tableParams
	 * @return
	 */
	List<IndustryCategory> findIndustryCategoryForTenant(String tenantId, TableDataInput tableParams);

	/**
	 * @param tenantId
	 * @param tableParams
	 * @return
	 */
	long findTotalFilteredIndustryCategoryForTenant(String tenantId, TableDataInput tableParams);

	/**
	 * @param tenantId
	 * @return
	 */
	long findTotalIndustryCategoryForTenant(String tenantId);

	/**
	 * @param response
	 * @param loggedInUserTenantId
	 */
	public void industryCategoryExportTemplate(HttpServletResponse response, String loggedInUserTenantId);

	public void industryCategoryExcelToZip(ZipOutputStream zos, String loggedInUserTenantId, String parentFolder);

	public List<ProductCategory> getAllProductCategoryByIds(List<String> productCategory);

	/**
	 * @param id
	 * @return
	 */
	IndustryCategory getIndustryCategorCodeAndNameById(String id);

	/**
	 * @param ids
	 * @return
	 */
	List<IndustryCategory> getAllIndustryCategoryOnlyByIds(List<String> ids);

	/**
	 * @param response
	 * @param file
	 * @param industryCategoryPojo
	 * @param loggedInUserTenantId
	 */
	public void downloadCsvFileForIndustryCategory(HttpServletResponse response, File file, IndustryCategoryPojo industryCategoryPojo, String loggedInUserTenantId);

	/**
	 * @param tenantId
	 * @return
	 */
	List<IndustryCategory> findActiveIndustryCategoryByTenantId(String tenantId);

}

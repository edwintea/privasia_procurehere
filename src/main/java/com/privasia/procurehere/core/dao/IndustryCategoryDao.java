package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.IndustryCategory;
import com.privasia.procurehere.core.entity.NaicsCodes;
import com.privasia.procurehere.core.entity.ProductCategory;
import com.privasia.procurehere.core.enums.TenantType;
import com.privasia.procurehere.core.pojo.TableDataInput;

public interface IndustryCategoryDao extends GenericDao<IndustryCategory, String> {

	/**
	 * @param industryCategory
	 * @param tenantId TODO
	 * @return
	 */
	boolean isExists(IndustryCategory industryCategory, String tenantId);

	/**
	 * @return
	 */
	public List<IndustryCategory> getAllIndustryCategory();

	/**
	 * @param id
	 * @return
	 */
	public IndustryCategory getIndustryCategoryForRftById(String id);

	/**
	 * @return
	 */
	long countIndustryCategory();

	/**
	 * @param ids
	 * @return
	 */
	public List<IndustryCategory> getAllIndustryCategoryByIds(List<String> ids);

	/**
	 * @param searchValue
	 * @param tenantId
	 * @return
	 */
	List<IndustryCategory> findIndustryCategoryByNameAndTenantId(String searchValue, String tenantId);

	/**
	 * @param codeList
	 * @param tenantId
	 * @param createdById TODO
	 */
	void loadNaicsCodesForTenant(List<NaicsCodes> codeList, String tenantId, String createdById);

	/**
	 * @param tenantId
	 * @param tableParams
	 * @return
	 */
	long findTotalFilteredIndustryCategoryForTenant(String tenantId, TableDataInput tableParams);

	/**
	 * @param tenantId
	 * @param tableParams
	 * @return
	 */
	List<IndustryCategory> findIndustryCategoryForTenant(String tenantId, TableDataInput tableParams);

	/**
	 * @param tenantId
	 * @return
	 */
	long findTotalIndustryCategoryForTenant(String tenantId);

	/**
	 * @param code
	 * @param tenantId
	 * @return
	 */
	IndustryCategory findIndustryCategoryByCodeAndTenantId(String code, String tenantId);

	/**
	 * @param loggedInUserTenantId
	 * @return
	 */
	List<IndustryCategory> getAllIndustryCategoryForTenant(String loggedInUserTenantId);

	List<IndustryCategory> getIndustryCategoryForTenant(String tenantId);

	List<ProductCategory> getAllProductCategoryByIds(List<String> productCategory);

	/**
	 * 
	 * @param id
	 * @return
	 */
	IndustryCategory getIndustryCategorCodeAndNameById(String id);

	/**
	 * 
	 * @param ids
	 * @return
	 */
	List<IndustryCategory> getAllIndustryCategoryOnlyByIds(List<String> ids);

	List<IndustryCategory> getAllIndustryCategoryForCsv(String loggedInUserTenantId, int pAGE_SIZE, int pageNo);

	/**
	 * 
	 * @param tenantId
	 * @return
	 */
	List<IndustryCategory> findActiveIndustryCategoryByTenantId(String tenantId);

	

	IndustryCategory findIndustryCategoryByCodeAndTenantIdAndExceptStatus(String code, String tenantId);
}

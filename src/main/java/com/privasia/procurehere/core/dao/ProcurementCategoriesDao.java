package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.ProcurementCategories;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author sana
 */
public interface ProcurementCategoriesDao extends GenericDao<ProcurementCategories, String> {

	/**
	 * @param tenantId
	 * @param input
	 * @return
	 */
	List<ProcurementCategories> findProcurementCategoriesForTenant(String tenantId, TableDataInput input);

	/**
	 * @param tenantId
	 * @param input
	 * @return
	 */
	long findTotalFilteredProcurementCategoriesForTenant(String tenantId, TableDataInput input);

	/**
	 * @param tenantId
	 * @return
	 */
	long findCountOfProcurementCategoriesForTenant(String tenantId);

	/**
	 * @param procurementMethod
	 * @param tenantId
	 * @return
	 */
	boolean isExists(ProcurementCategories procurementCategories, String tenantId);

	/**
	 * @param tenantId
	 * @return
	 */
	List<ProcurementCategories> getAllProcurementCategoriesByTenantId(String tenantId);

	List<ProcurementCategories> getAllActiveProcurementCategory(String tenantId);


}

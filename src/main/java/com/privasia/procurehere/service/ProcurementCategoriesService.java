package com.privasia.procurehere.service;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.privasia.procurehere.core.entity.ProcurementCategories;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author sana
 */
public interface ProcurementCategoriesService {

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
	 * @param glCode
	 * @param tenantId
	 * @return
	 */
	boolean isExists(ProcurementCategories ProcurementCategories, String tenantId);

	/**
	 * @param id
	 * @return
	 */
	ProcurementCategories getProcurementCategoriesById(String id);

	/**
	 * @param procurementCategories
	 */
	void saveProcurementCategories(ProcurementCategories procurementCategories);

	/**
	 * @param persistObj
	 */
	void updateProcurementCategories(ProcurementCategories persistObj);

	/**
	 * @param procurementCategories
	 */
	void deleteProcurementCategories(ProcurementCategories procurementCategories);

	/**
	 * @param response
	 * @param tenantId
	 */
	void procurementCategoriesDownloadTemplate(HttpServletResponse response, String tenantId);

	List<ProcurementCategories> getAllActiveProcurementCategory(String tenantId);

}

/**
 * 
 */
package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.SupplierPerformanceTemplate;
import com.privasia.procurehere.core.enums.SourcingStatus;
import com.privasia.procurehere.core.enums.TenantType;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author Jayshree
 */
public interface SupplierPerformanceTemplateDao extends GenericDao<SupplierPerformanceTemplate, String> {

	/**
	 * @param templateId
	 */
	void deleteUsersForSPTemplate(String templateId);

	/**
	 * @param templateName
	 * @param tenantId
	 * @return
	 */
	boolean isTemplateExistsByTemplateName(String templateName, String tenantId);

	/**
	 * @param loggedInUserTenantId
	 * @param buyer
	 * @return
	 */
	long findTotalActiveSPTemplateForTenant(String loggedInUserTenantId, TenantType buyer);

	/**
	 * @param loggedInUserTenantId
	 * @param input
	 * @param buyer
	 * @return
	 */
	long findTotalFilteredSPTemplateForTenant(String loggedInUserTenantId, TableDataInput input, TenantType buyer);

	/**
	 * @param loggedInUserTenantId
	 * @param input
	 * @param buyer
	 * @return
	 */
	List<SupplierPerformanceTemplate> findSPTemplateForTenant(String loggedInUserTenantId, TableDataInput input, TenantType buyer);

	/**
	 * @param templateId
	 * @return
	 */
	List<String> getUserIdListByTemplateId(String templateId);

	/**
	 * @param userId
	 * @return
	 */
	List<SupplierPerformanceTemplate> getAllAssignedSpTemplateIdsForUser(String userId);

	/**
	 * @param templateId
	 * @param templateName
	 * @return
	 */
	boolean isExists(String templateId, String templateName);

	/**
	 * @param tenantId
	 * @return
	 */
	List<SupplierPerformanceTemplate> getAllSpTemplatesOfBuyer(String tenantId);

	/**
	 * @param templateId
	 * @param status
	 */
	void updateSPTemplateStatus(String templateId, SourcingStatus status);

	/**
	 * @param templateId
	 * @param performanceCriteriaCompleted
	 * @param status
	 * @return
	 */
	void updateSupplierPerformanceTemplateStatusAndComplete(String templateId, Boolean performanceCriteriaCompleted, SourcingStatus status);

}

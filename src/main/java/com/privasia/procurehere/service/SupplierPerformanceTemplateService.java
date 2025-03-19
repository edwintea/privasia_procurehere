/**
 * 
 */
package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.SupplierPerformanceTemplate;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.SourcingStatus;
import com.privasia.procurehere.core.enums.TenantType;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author Jayshree
 */
public interface SupplierPerformanceTemplateService {

	/**
	 * @param templateId
	 * @return
	 */
	SupplierPerformanceTemplate getSupplierPerformanceTemplatebyId(String templateId);

	/**
	 * @param templateId
	 */
	void deleteUsersForSPTemplate(String templateId);

	/**
	 * @param persistObject
	 * @return
	 */
	SupplierPerformanceTemplate saveOrUpdateSupplierPerformanceTemplate(SupplierPerformanceTemplate persistObject);

	/**
	 * @param templateName
	 * @param tenantId
	 * @return
	 */
	boolean isTemplateExistsByTemplateName(String templateName, String tenantId);

	/**
	 * @param spTemplate
	 * @return
	 */
	SupplierPerformanceTemplate saveSupplierPerformanceTemplate(SupplierPerformanceTemplate spTemplate);

	/**
	 * @param spTemplate
	 * @return
	 */
	SupplierPerformanceTemplate updateSupplierPerformanceTemplate(SupplierPerformanceTemplate spTemplate);

	/**
	 * @param templateId
	 * @return
	 */
	SupplierPerformanceTemplate getSupplierPerformanceTemplateById(String templateId);

	/**
	 * @param loggedInUserTenantId
	 * @param input
	 * @param buyer
	 * @return
	 */
	List<SupplierPerformanceTemplate> findSPTemplateForTenant(String loggedInUserTenantId, TableDataInput input, TenantType buyer);

	/**
	 * @param loggedInUserTenantId
	 * @param input
	 * @param buyer
	 * @return
	 */
	long findTotalFilteredSPTemplateForTenant(String loggedInUserTenantId, TableDataInput input, TenantType buyer);

	/**
	 * @param loggedInUserTenantId
	 * @param buyer
	 * @return
	 */
	long findTotalActiveSPTemplateForTenant(String loggedInUserTenantId, TenantType buyer);

	/**
	 * @param templateId
	 * @return
	 */
	List<String> getUserIdListByTemplateId(String templateId);

	/**
	 * @param tenantId
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
	 * @param templateId
	 * @param template
	 * @param user
	 * @return
	 */
	SupplierPerformanceTemplate copyTemplate(String templateId, SupplierPerformanceTemplate template, User user);

	/**
	 * @param spTemplate
	 * @param loggedInUser TODO
	 */
	void deleteSupplierPerformanceTemplate(SupplierPerformanceTemplate spTemplate, User loggedInUser);

	/**
	 * @param templateId
	 * @param performanceCriteriaCompleted
	 * @param status
	 * @return
	 */
	void updateSupplierPerformanceTemplateStatusAndComplete(String templateId, Boolean performanceCriteriaCompleted, SourcingStatus status);

}

package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.SourcingFormTemplate;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author pooja
 */

public interface SourcingTemplateDao extends GenericDao<SourcingFormTemplate, String> {
	/**
	 * @param tenantId
	 * @param input
	 * @param userId
	 * @return List<SourcingFormTemplate>
	 */
	List<SourcingFormTemplate> findAllActiveSourcingTemplateForTenant(String tenantId, TableDataInput input, String userId);

	/**
	 * @param tenantId
	 * @param tableParams
	 * @param userId
	 * @return long
	 */
	long findTotalFilteredTemplatesForTenant(String tenantId, TableDataInput tableParams, String userId);

	/**
	 * @param tenantId
	 * @return long
	 */
	long findTotalTemplatesForTenant(String tenantId);

	/**
	 * @param formId
	 * @param formName
	 * @return boolean
	 */
	boolean isExists(String formId, String formName);

	/**
	 * @param formName
	 * @param tenantId
	 * @return boolean
	 */
	boolean isTemplateExists(String formName, String tenantId);

	/**
	 * @param loggedInUserTenantId
	 * @return List<SourcingFormTemplate>
	 */
	List<SourcingFormTemplate> getSourcingFormbyteantId(String loggedInUserTenantId);

	/**
	 * @param loggedInUserTenantId
	 * @param input
	 * @param id
	 * @return List<SourcingFormTemplate>
	 */

	List<SourcingFormTemplate> getAllTemplate(String loggedInUserTenantId, TableDataInput input, String id);

	/**
	 * @param searchValue
	 * @param pageNo
	 * @param userId TODO
	 * @param loggedInUserTenantId
	 * @param id
	 * @return
	 */
	List<SourcingFormTemplate> findByTemplateNameForTenant(String searchValue, String tenantId, String pageNo, String userId);

	/**
	 * filter list on Sourcing Template Screen
	 * 
	 * @param tenantId
	 * @param input
	 * @param userId
	 * @return
	 */
	List<SourcingFormTemplate> findAllActiveSourcTemplateForTenant(String tenantId, TableDataInput input, String userId);

	/**
	 * @param tenantId
	 * @return
	 */
	List<SourcingFormTemplate> findAllActiveSourcingTemplateForTenantId(String tenantId);

	/**
	 * @param tenantId
	 * @param input
	 * @param userId
	 * @return
	 */
	List<SourcingFormTemplate> findAllAssignActiveSourcingTemplateForTenant(String tenantId, TableDataInput input, String userId);

	/**
	 * @param formId
	 * @return
	 */
	long getBqCount(String formId);

	List<String> getTemplateByUserIdAndTemplateId(String tempId, String loggedInUserTenantId);

	/**
	 * @param tenantId
	 * @param userId TODO
	 * @return
	 */
	long getTotalTemplateCountForTenant(String tenantId, String userId);

	/**
	 * @param searchValue
	 * @param pageNo
	 * @param pageLength
	 * @param start
	 * @param tenantId
	 * @param userId TODO
	 * @return
	 */
	List<SourcingFormTemplate> findTemplatesBySearchValForTenant(String searchValue, Integer pageNo, Integer pageLength, Integer start, String tenantId, String userId);

	/**
	 * @param searchValue
	 * @param tenantId
	 * @param userId TODO
	 * @return
	 */
	long findTemplatesCountBySearchValForTenant(String searchValue, String tenantId, String userId);

	List<SourcingFormTemplate> getAllSourcingTemplatesOfBuyer(String tenantId);

	List<String> getSourcingTemplateByUserIdAndTemplateId(String templateId, String tenantId);

	/**
	 * @param id
	 */
	void deleteusersForRfsTemplate(String id);

}

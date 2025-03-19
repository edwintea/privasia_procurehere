package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.PrTemplate;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.pojo.PrTemplatePojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author parveen
 */
public interface PrTemplateDao extends GenericDao<PrTemplate, String> {

	/**
	 * @param tenantId
	 * @param tableParams
	 * @param userId TODO
	 * @return
	 */
	List<PrTemplate> findTemplatesForTenant(String tenantId, TableDataInput tableParams, String userId);

	/**
	 * @param template
	 * @return
	 */
	boolean isExists(PrTemplate template);

	/**
	 * @param tenantId
	 * @return
	 */
	List<PrTemplate> findAllActiveTemplatesForTenant(String tenantId);

	/**
	 * @param tenantId
	 * @return
	 */
	long findTotalTemplatesForTenant(String tenantIds);

	/**
	 * @param tenantId
	 * @param tableParams
	 * @param userId TODO
	 * @return
	 */
	long findTotalFilteredTemplatesForTenant(String tenantId, TableDataInput tableParams, String userId);

	/**
	 * @param templateId
	 * @param tenantId
	 * @return
	 */
	PrTemplate getPrTemplateForEditById(String templateId, String tenantId);

	// List<RfxTemplate> findAllActiveTemplatesByRfxTypeForTenant(String tenantId, RfxTypes rfxTypes, String userId);
	/**
	 * @param templateName
	 * @param tenantId
	 * @param userId
	 * @param pageNo TODO
	 * @return
	 */
	List<PrTemplate> findByTemplateNameForTenant(String templateName, String tenantId, String userId, String pageNo);

	/**
	 * @param tenantId
	 * @param userId
	 * @param input TODO
	 * @return
	 */
	List<PrTemplate> findAllActiveTemplatesForTenantAndUser(String tenantId, String userId, TableDataInput input);

	Integer findAssignedTemplateCount(String templateId);

	List<PrTemplate> findAllPrTemplatesForTenantAndUser(String tenantId);

	List<PrTemplatePojo> findTemplatesForTenantId(String tenantId, TableDataInput tableParams, String userId);

	List<User> getAllUsers(String tempId);

	List<String> getTemplateByUserIdAndTemplateId(String id, String loggedInUserTenantId);

	void deleteusersForTemplate(String prTemplateId);

	boolean validateContractItemSetting(String id);

	/**
	 * @param tenantId
	 * @param userId TODO
	 * @param searchValue TODO
	 * @return
	 */
	long getTotalTemplateCountForTenant(String searchValue, String tenantId, String userId);

	/**
	 * @param searchValue
	 * @param pageNo
	 * @param pageLength
	 * @param start
	 * @param tenantId
	 * @param userId
	 * @return
	 */
	List<PrTemplate> findTemplatesByTemplateNameForTenantId(String searchValue, Integer pageNo, Integer pageLength, Integer start, String tenantId, String userId);

}

package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.PrTemplate;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.pojo.PrTemplatePojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author parveen
 */
public interface PrTemplateService {

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
	 * @param template
	 * @return
	 */
	PrTemplate save(PrTemplate template);

	/**
	 * @param template
	 */
	void update(PrTemplate template);

	/**
	 * @param id
	 * @return
	 */
	PrTemplate getPrTemplateById(String id);

	/**
	 * @param loggedInUserTenantId
	 * @return
	 */
	long findTotalTemplatesForTenant(String loggedInUserTenantId);

	/**
	 * @param template
	 */
	void delete(PrTemplate template);

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

	PrTemplate copyTemplate(PrTemplate prTemplate, PrTemplate newTemplate, User loggedInUser);

	Integer findAssignedTemplateCount(String templateId);

	List<PrTemplate> findAllPrTemplatesForTenantAndUser(String tenantId);

	List<PrTemplatePojo> findTemplatesForTenantId(String loggedInUserTenantId, TableDataInput input, String id);

	List<User> getAllUsers(String tempId);

	List<String> getTemplateByUserIdAndTemplateId(String userId, String loggedInUserTenantId);

	void deleteusersForTemplate(String id);

	boolean validateContractItemSetting(String id);

	/**
	 * @param tenantId
	 * @param userId TODO
	 * @param searchValue TODO
	 * @return
	 */
	long getTemplateCountBySearchValueForTenant(String searchValue, String tenantId, String userId);

	/**
	 * @param searchValue
	 * @param pageNo
	 * @param pageLength
	 * @param tenantId
	 * @param UserId
	 * @return
	 */
	List<PrTemplate> findTemplatesByTemplateNameForTenantId(String searchValue, Integer pageNo, Integer pageLength, String tenantId, String UserId);

}

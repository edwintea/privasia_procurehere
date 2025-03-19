package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfxTemplate;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.AuctionType;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.pojo.RfxTemplatePojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author Nitin Otageri
 */
public interface RfxTemplateDao extends GenericDao<RfxTemplate, String> {

	/**
	 * @param tenantId
	 * @param tableParams TODO
	 * @param userId TODO
	 * @return
	 */
	List<RfxTemplate> findTemplatesForTenant(String tenantId, TableDataInput tableParams, String userId);

	/**
	 * @param template
	 * @return
	 */
	boolean isExists(RfxTemplate template);

	/**
	 * @param tenantId
	 * @return
	 */
	List<RfxTemplate> findAllActiveTemplatesForTenant(String tenantId);

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
	RfxTemplate getRfxTemplateForEditById(String templateId, String tenantId);

	List<RfxTemplate> findAllActiveTemplatesByRfxTypeForTenant(String tenantId, RfxTypes rfxTypes, String userId);

	List<RfxTemplate> findByTemplateNameForTenant(String templateName, String tenantId, RfxTypes eventType, String userId);

	/**
	 * @param tenantId
	 * @param rfxTypes
	 * @return
	 */
	List<RfxTemplate> findAllActiveTemplatesByRfxTypeForTenantId(String tenantId, RfxTypes rfxTypes);

	List<RfxTemplate> findAllActiveTemplatesByAuctionTypeForTenantId(String tenantId, AuctionType auctionType, String userId);

	/**
	 * @param eventId
	 * @param rfxTypes
	 * @return
	 */
	Boolean findTemplateIndustryCategoryFlagByEventId(String eventId, RfxTypes rfxTypes);

	List<RfxTemplate> findAllActiveTemplatesByRfxTypeForTenant(String loggedInUserTenantId, RfxTypes valueOf, String id, String pageNo);

	List<RfxTemplate> findAllActiveTemplatesByRfxTypeForTenantInitial(String loggedInUserTenantId, RfxTypes rfxTypes, String pageNo);

	List<RfxTemplatePojo> findTemplatesForTenantId(String tenantId, TableDataInput tableParams, String userId);

	List<User> getAllUsers(String tempId);

	RfxTemplate getTemplateByTenantIdAndTemplateId(String id, String tenantId);

	List<String> getTemplateByUserIdAndTemplateId(String userId, String loggedInUserTenantId);

	void deleteAssociatedUserForTemplate(String rfxTemplateId);

	/**
	 * @return
	 */
	List<RfxTemplate> findTemplatesByRfxForUnMaskingUserIsNotNull();

	/**
	 * @param tempId
	 */
	void updateTemplateUnMaskUser(String tempId);

	/**
	 * @param declarationId
	 * @return
	 */
	boolean isAssignedDeclarationToTemplate(String declarationId);

	/**
	 * @param declarationId
	 * @return
	 */
	long findAssignedCountOfDeclaration(String declarationId);

	/**
	 * @param searchValue
	 * @param tenantId
	 * @param rfxType
	 * @param userId
	 * @return
	 */
	long findActiveTemplateCountByRfxTypeForTenantId(String searchValue, String tenantId, RfxTypes rfxType, String userId);

	/**
	 * @param searchValue
	 * @param pageNo
	 * @param pageLength
	 * @param start
	 * @param rfxType
	 * @param tenantId
	 * @param userId
	 * @return
	 */
	List<RfxTemplate> findTemplateByTemplateNameForTenant(String searchValue, Integer pageNo, Integer pageLength, Integer start, RfxTypes rfxType, String tenantId, String userId);

	// List<User> getAllUserForTemplate(String teanetId);

}

/**
 * 
 */
package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.RfsTemplateDocument;
import com.privasia.procurehere.core.entity.RfxTemplate;
import com.privasia.procurehere.core.entity.RfxTemplateDocument;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.AuctionType;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.pojo.RfxTemplatePojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Nitin Otageri
 */
public interface RfxTemplateService {

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
	 * @param template
	 * @return
	 */
	RfxTemplate save(RfxTemplate template);

	/**
	 * @param template
	 */
	void update(RfxTemplate template);

	/**
	 * @param id
	 * @return
	 */
	RfxTemplate getRfxTemplateById(String id);

	/**
	 * @param loggedInUserTenantId
	 * @return
	 */
	long findTotalTemplatesForTenant(String loggedInUserTenantId);

	/**
	 * @param template
	 */
	void delete(RfxTemplate template);

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

	RfxTemplate copyTemplate(RfxTemplate oldTemplate, RfxTemplate newTemplate, User loggedInUser);

	/**
	 * @param tenantId
	 * @param rfxTypes
	 * @return
	 */
	List<RfxTemplate> findAllActiveTemplatesByRfxTypeForTenantId(String tenantId, RfxTypes rfxTypes);

	/**
	 * @param tenantId
	 * @param auctionType
	 * @return
	 */
	List<RfxTemplate> findAllActiveTemplatesByAuctionTypeForTenantId(String tenantId, AuctionType auctionType, String userId);

	RfxTemplate getRfxTemplateByIdForBU(String templateId);

	/**
	 * @param eventId
	 * @param rfxTypes
	 * @return
	 */
	Boolean findTemplateIndustryCategoryFlagByEventId(String eventId, RfxTypes rfxTypes);

	List<RfxTemplate> findAllActiveTemplatesByRfxTypeForTenant(String loggedInUserTenantId, RfxTypes valueOf, String id, String pageNo);

	List<RfxTemplate> findAllActiveTemplatesByRfxTypeForTenantInitial(String loggedInUserTenantId, RfxTypes rfxTypes, String pageNo);

	List<RfxTemplatePojo> findTemplatesForTenantId(String loggedInUserTenantId, TableDataInput input, String id);

	List<User> getAllUsers(String tempId);

	RfxTemplate getTemplateByTenantIdAndTemplateId(String id, String loggedInUserTenantId);

	List<String> getTemplateByUserIdAndTemplateId(String id, String loggedInUserTenantId);

	void deleteAssociatedUserForTemplate(String rfxTemplateId);

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
	 * @param rfxType
	 * @param tenantId
	 * @param userId
	 * @return
	 */
	List<RfxTemplate> findTemplateByTemplateNameForTenant(String searchValue, Integer pageNo, Integer pageLength, RfxTypes rfxType, String tenantId, String userId);

	RfxTemplateDocument saveRfxTemplateDocument(RfxTemplateDocument document);

	List<RfxTemplateDocument> findAllTemplateDocsBytemplateId(String templateId);

	void downloadRfxTemplateDocument(String docId, HttpServletResponse response) throws Exception;

	void removeDocument(RfxTemplateDocument removeDoc);

	RfxTemplateDocument findDocsById(String id);

	void updateDocument(String docId, String docDesc, String formId, Boolean internal);

	public void updateTemplateUsed(String templateId);
}

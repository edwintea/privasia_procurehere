package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.RfsTemplateDocument;
import com.privasia.procurehere.core.entity.SourcingTemplateCq;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.TeamMemberType;
import com.privasia.procurehere.core.entity.SourcingFormTemplate;
import com.privasia.procurehere.core.pojo.TableDataInput;

import javax.servlet.http.HttpServletResponse;

/**
 * @author pooja
 */

public interface SourcingTemplateService {

	/**
	 * @param sourceForm
	 * @return
	 */
	SourcingFormTemplate saveSourcingTemplate(SourcingFormTemplate sourceForm);

	/**
	 * @param sourceForm
	 * @return
	 */
	SourcingFormTemplate updateSourcingTemplate(SourcingFormTemplate sourceForm);

	/**
	 * @param loggedInUserTenantId
	 * @param input
	 * @param string
	 * @return
	 */
	List<SourcingFormTemplate> findAllActiveSourcingTemplateForTenant(String loggedInUserTenantId, TableDataInput input, String string);

	/**
	 * @param sourcingTemplateId
	 * @return
	 */
	SourcingFormTemplate getSourcingFormbyId(String sourcingTemplateId);

	long findTotalFilteredTemplatesForTenant(String loggedInUserTenantId, TableDataInput input, String id);

	long findTotalTemplatesForTenant(String loggedInUserTenantId);

	List<SourcingTemplateCq> getAllQuestionnarie(String formId);

	void deleteSourcingForm(SourcingFormTemplate sourceForm);

	SourcingFormTemplate SaveOrupdateSourcingTemplate(SourcingFormTemplate persistObj);

	/**
	 * @param formId
	 * @param FormName
	 * @return
	 */
	boolean isExists(String formId, String FormName);

	/**
	 * @param templateId
	 * @param oldForm
	 * @param user TODO
	 * @return SourcingFormTemplate
	 */
	SourcingFormTemplate copyTemplate(String templateId, SourcingFormTemplate oldForm, User user);

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
	 * @param tenantId
	 * @param pageNo
	 * @param userId TODO
	 * @param userId
	 * @return
	 */
	List<SourcingFormTemplate> findByTemplateNameForTenant(String searchValue, String tenantId, String pageNo, String userId);

	/**
	 * @param tenantId
	 * @param input
	 * @param userId
	 * @return
	 */
	List<SourcingFormTemplate> findAllActiveSourcTemplateForTenant(String tenantId, TableDataInput input, String userId);

	/**
	 * @param loggedInUserTenantId
	 * @return
	 */
	List<SourcingFormTemplate> findAllActiveSourcingTemplateForTenantId(String loggedInUserTenantId);

	/**
	 * @param loggedInUserTenantId
	 * @param input
	 * @param id
	 * @return
	 */
	List<SourcingFormTemplate> findAssignActiveSourcingTemplateForTenant(String loggedInUserTenantId, TableDataInput input, String id);

	/**
	 * @param formId
	 * @return count of all cq Item
	 */
	long getBqCount(String formId);

	List<String> getTemplateByUserIdAndTemplateId(String tempId, String loggedInUserTenantId);

	void sendTeamMemberEmailNotificationEmail(User user, TeamMemberType teamMemberType, User loggedInUser, String formName, String referenceNumber, String id, String formId);

	/**
	 * @param formId
	 * @return
	 */
	List<SourcingTemplateCq> getAllQuestionnarieByOrder(String formId);

	/**
	 * @param formId
	 * @return
	 */
	List<SourcingTemplateCq> getAllQuestionnarieOrderByDate(String formId);

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
	 * @param tenantId
	 * @param userId TODO
	 * @return
	 */
	List<SourcingFormTemplate> findTemplatesBySearchValForTenant(String searchValue, Integer pageNo, Integer pageLength, String tenantId, String userId);

	/**
	 * @param searchValue
	 * @param tenantId
	 * @param userId TODO
	 * @return
	 */
	long findTemplatesCountBySearchValForTenant(String searchValue, String tenantId, String userId);

	List<String> getSourcingTemplateByUserIdAndTemplateId(String templateId, String tenantId);

	/**
	 * @param id
	 */
	void deleteusersForRfsTemplate(String id);

	RfsTemplateDocument saveRfsTemplateDocument(RfsTemplateDocument rfsTemplate);

	List<RfsTemplateDocument> findAllTemplateDocsBytemplateId(String templateId);

	void downloadRfsTemplateDocument(String docId, HttpServletResponse response) throws Exception;

	void removeDocument(RfsTemplateDocument removeDoc);

	RfsTemplateDocument findDocsById(String id);

	void updateDocument(String docId, String docDesc, String formId);
}

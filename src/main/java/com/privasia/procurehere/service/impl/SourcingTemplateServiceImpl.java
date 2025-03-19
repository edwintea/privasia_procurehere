package com.privasia.procurehere.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;


import com.privasia.procurehere.core.dao.*;
import com.privasia.procurehere.core.entity.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.NotificationType;
import com.privasia.procurehere.core.enums.SourcingStatus;
import com.privasia.procurehere.core.enums.TeamMemberType;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.BuyerService;
import com.privasia.procurehere.service.BuyerSettingsService;
import com.privasia.procurehere.service.DashboardNotificationService;
import com.privasia.procurehere.service.NotificationService;
import com.privasia.procurehere.service.SourcingFormRequestService;
import com.privasia.procurehere.service.SourcingTemplateService;
import com.privasia.procurehere.service.UserService;

import freemarker.template.Configuration;
import org.springframework.util.FileCopyUtils;

@Service
@Transactional(readOnly = true)
public class SourcingTemplateServiceImpl implements SourcingTemplateService {
	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@Autowired
	SourcingTemplateDao sourcingTemplateDao;

	@Autowired
	SourcingFormCqDao sourcingFormCqDao;

	@Autowired
	SourcingFormCqItemDao cqItemDao;

	@Autowired
	BuyerService buyerService;

	@Value("${app.url}")
	String APP_URL;

	@Resource
	MessageSource messageSource;

	@Autowired
	NotificationService notificationService;

	@Autowired
	BuyerSettingsService buyerSettingsService;

	@Autowired
	Configuration freemarkerConfiguration;

	@Autowired
	DashboardNotificationService dashboardNotificationService;

	@Autowired
	SourcingFormRequestService sourcingFormRequestService;

	@Autowired
	SourcingTemplateService sourcingTemplateService;

	@Autowired
	UserService userService;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Autowired
	RfsTemplateDocumentDao rfsTemplateDocumentDao;

	@Override
	@Transactional(readOnly = false)
	public SourcingFormTemplate saveSourcingTemplate(SourcingFormTemplate sourceForm) {
		try {
			BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "Sourcing Form Template '"+sourceForm.getFormName()+ "' is created", sourceForm.getCreatedBy().getTenantId(), sourceForm.getCreatedBy(), new Date(), ModuleType.SourcingTemplate);
			buyerAuditTrailDao.save(ownerAuditTrail);
		} catch (Exception e) {
			LOG.info("Error to create audit trails message");
		}
		if (CollectionUtil.isNotEmpty(sourceForm.getFields())) {
			List<SourcingTemplateField> templateFields = new ArrayList<SourcingTemplateField>();
			for (SourcingTemplateField tempField : sourceForm.getFields()) {
				SourcingTemplateField temp = new SourcingTemplateField();
				temp.setDefaultValue(tempField.getDefaultValue());
				temp.setFieldName(tempField.getFieldName());
				temp.setOptional(tempField.getOptional());
				temp.setReadOnly(tempField.getReadOnly());
				temp.setTemplate(sourceForm);
				temp.setVisible(tempField.getVisible());
				temp.setBuyer(buyerService.findBuyerById(tempField.getBuyer().getId()));
				templateFields.add(temp);
			}
			sourceForm.setFields(templateFields);
		}
		sourceForm = sourcingTemplateDao.save(sourceForm);
		return sourceForm;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public SourcingFormTemplate updateSourcingTemplate(SourcingFormTemplate sourceForm) {
		return sourcingTemplateDao.saveOrUpdate(sourceForm);
	}

	@Override
	public List<SourcingFormTemplate> findAllActiveSourcingTemplateForTenant(String tenantId, TableDataInput input, String userId) {
		ArrayList<SourcingFormTemplate> templateList = new ArrayList<>();
		List<SourcingFormTemplate> tempList = sourcingTemplateDao.findAllActiveSourcingTemplateForTenant(tenantId, input, userId);
		LOG.info("Size of the List " + templateList.size());
		for (SourcingFormTemplate sourcingFormTemplate : tempList) {
			templateList.add(sourcingFormTemplate.createShallowCopy());
		}
		return templateList;
	}

	@Override
	public List<SourcingFormTemplate> findAllActiveSourcTemplateForTenant(String tenantId, TableDataInput input, String userId) {
		ArrayList<SourcingFormTemplate> templateList = new ArrayList<>();
		List<SourcingFormTemplate> tempList = sourcingTemplateDao.findAllActiveSourcTemplateForTenant(tenantId, input, userId);
		LOG.info("Size of the List " + templateList.size());
		for (SourcingFormTemplate sourcingFormTemplate : tempList) {
			templateList.add(sourcingFormTemplate.createShallowCopy());
		}
		return templateList;
	}

	@Override
	@Transactional
	public SourcingFormTemplate getSourcingFormbyId(String sourcingTemplateId) {
		LOG.info("Sourcing Template Id................" + sourcingTemplateId);
		SourcingFormTemplate sourcingTemplate = sourcingTemplateDao.findById(sourcingTemplateId);
		if (sourcingTemplate != null)
			if (CollectionUtil.isNotEmpty(sourcingTemplate.getRfsTemplateDocuments())) {
				for (RfsTemplateDocument rfs : sourcingTemplate.getRfsTemplateDocuments()) {
					if (rfs.getUploadBy() != null) {
						rfs.getUploadBy().getLoginId();
					}
						rfs.getId();
				}
			}
			if (sourcingTemplate.getSourcingFormApproval() != null) {
				for (SourcingTemplateApproval approval : sourcingTemplate.getSourcingFormApproval()) {
					for (SourcingFormApprovalUser approvalUser : approval.getApprovalUsers()) {
						approvalUser.getRemarks();
					}
				}
			}

		if (sourcingTemplate != null) {
			sourcingTemplate.getFormName();
			if (CollectionUtil.isNotEmpty(sourcingTemplate.getFields())) {
				for (SourcingTemplateField field : sourcingTemplate.getFields()) {
					field.getDefaultValue();
				}
			}
		}

		if (CollectionUtil.isNotEmpty(sourcingTemplate.getTeamMembers())) {
			for (TemplateSourcingTeamMembers team : sourcingTemplate.getTeamMembers()) {
				team.getTeamMemberType();
				if (team.getUser() != null) {
					team.getUser().getLoginId();
				}
			}
		}
		// Initialize createdBy
		if (sourcingTemplate.getCreatedBy() != null) {
			sourcingTemplate.getCreatedBy().getLoginId();
		}
		// Initialize cqs
		if (CollectionUtil.isNotEmpty(sourcingTemplate.getCqs())) {
			for (SourcingTemplateCq cq : sourcingTemplate.getCqs()) {
				cq.getId(); // Replace with a real field access
			}
		}

		return sourcingTemplate;
	}

	@Override
	public long findTotalFilteredTemplatesForTenant(String tenantId, TableDataInput tableParams, String userId) {

		return sourcingTemplateDao.findTotalFilteredTemplatesForTenant(tenantId, tableParams, userId);
	}

	@Override
	public long findTotalTemplatesForTenant(String tenantId) {
		return sourcingTemplateDao.findTotalTemplatesForTenant(tenantId);
	}

	@Override
	public List<SourcingTemplateCq> getAllQuestionnarie(String formId) {
		return sourcingFormCqDao.getAllQuestionnarieByOrder(formId);

	}

	@Transactional(readOnly = false)
	@Override
	public void deleteSourcingForm(SourcingFormTemplate sourceForm) {
		try {
			BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.DELETE, "Sourcing Form Template '"+sourceForm.getFormName()+ "' is deleted", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.SourcingTemplate);
			buyerAuditTrailDao.save(ownerAuditTrail);
		} catch (Exception e) {
			LOG.info("Error to create audit trails message");
		}
		sourcingTemplateDao.delete(sourceForm);

	}

	@Transactional(readOnly = false)
	@Override
	public SourcingFormTemplate SaveOrupdateSourcingTemplate(SourcingFormTemplate persistObj) {
		return sourcingTemplateDao.saveOrUpdate(persistObj);
	}

	@Override
	public boolean isExists(String formId, String FormName) {
		return sourcingTemplateDao.isExists(formId, FormName);
	}

	@Transactional(readOnly = false)
	@Override
	public SourcingFormTemplate copyTemplate(String templateId, SourcingFormTemplate newForm, User user) {

		SourcingFormTemplate oldForm = sourcingTemplateDao.findById(templateId);
		LOG.info("SF : " + oldForm.getId());
		if (CollectionUtil.isNotEmpty(oldForm.getCqs())) {
			for (SourcingTemplateCq cq : oldForm.getCqs()) {
				cq.getName();
				cq.getCqItems();
				cq.getSourcingForm();
				for (SourcingTemplateCqItem item : cq.getCqItems()) {
					item.getCqOptions();
					item.getChildren();
					item.getCq();
					item.getSourcingForm();
				}

			}
		}

		if (CollectionUtil.isNotEmpty(oldForm.getFields())) {
			List<SourcingTemplateField> templateFields = new ArrayList<SourcingTemplateField>();
			for (SourcingTemplateField tempField : oldForm.getFields()) {
				SourcingTemplateField temp = new SourcingTemplateField();
				temp.setDefaultValue(tempField.getDefaultValue());
				temp.setFieldName(tempField.getFieldName());
				temp.setOptional(tempField.getOptional());
				temp.setReadOnly(tempField.getReadOnly());
				temp.setTemplate(newForm);
				temp.setVisible(tempField.getVisible());
				temp.setBuyer(tempField.getBuyer());
				templateFields.add(temp);
			}
			newForm.setFields(templateFields);
		}
		newForm.setCreatedBy(user);
		newForm.setCreatedDate(new Date());
		newForm.setStatus(SourcingStatus.DRAFT);
		newForm.setTenantId(oldForm.getTenantId());
		newForm.setCqCompleted(true);
		newForm.setEventDetailCompleted(true);
		newForm.setDecimal(oldForm.getDecimal());
		newForm.setReadOnlyTeamMember(oldForm.getReadOnlyTeamMember());
		if (oldForm.getApprovalsCount() != null) {
			newForm.setApprovalsCount(oldForm.getApprovalsCount());
		}
		if (oldForm.getAddAdditionalApprovals() != null) {
			newForm.setAddAdditionalApprovals(oldForm.getAddAdditionalApprovals());
		}
		newForm = sourcingTemplateDao.save(newForm);

		newForm.copyCqDetails(oldForm, newForm);

		List<SourcingTemplateCq> cqList = new ArrayList<>();
		if (CollectionUtil.isNotEmpty(newForm.getCqs())) {
			for (SourcingTemplateCq cq : newForm.getCqs()) {
				cq.setSourcingForm(newForm);
				SourcingTemplateCqItem parent = null;
				if (CollectionUtil.isNotEmpty(cq.getCqItems())) {
					for (SourcingTemplateCqItem item : cq.getCqItems()) {
						if (item.getOrder() != 0) {
							item.setParent(parent);
							/* LOG.info("parent======" + parent.getId()); */
						}
						item.setSourcingForm(newForm);
						if (item.getOrder() == 0) {
							parent = item;
						}
					}

				}
				SourcingTemplateCq dbCq = sourcingFormCqDao.saveOrUpdate(cq);
				cqList.add(dbCq);
			}
		}

		newForm.setCqs(cqList);
		List<SourcingTemplateApproval> approval = new ArrayList<>();
		List<SourcingFormApprovalUser> UserList = new ArrayList<>();
		for (SourcingTemplateApproval app : oldForm.getSourcingFormApproval()) {
			SourcingTemplateApproval app1 = new SourcingTemplateApproval();
			app1.setActive(app.isActive());
			app1.setApprovalType(app.getApprovalType());
			for (SourcingFormApprovalUser sourcingFormApprovalUser : app.getApprovalUsers()) {
				SourcingFormApprovalUser usr = new SourcingFormApprovalUser();
				usr.setActionDate(sourcingFormApprovalUser.getActionDate());
				usr.setApprovalStatus(sourcingFormApprovalUser.getApprovalStatus());
				usr.setRemarks(sourcingFormApprovalUser.getRemarks());
				usr.setUser(sourcingFormApprovalUser.getUser());
				usr.setUserName(sourcingFormApprovalUser.getUserName());
				usr.setApproval(app1);
				UserList.add(usr);
			}
			app1.setApprovalUsers(UserList);
			app1.setDone(app.isDone());
			app1.setLevel(app.getLevel());
			app1.setSourcingForm(newForm);
			approval.add(app1);
		}
		newForm.setSourcingFormApproval(approval);

		List<TemplateSourcingTeamMembers> teamMembers = new ArrayList<TemplateSourcingTeamMembers>();
		if (CollectionUtil.isNotEmpty(oldForm.getTeamMembers())) {
			for (TemplateSourcingTeamMembers team : oldForm.getTeamMembers()) {
				TemplateSourcingTeamMembers newTeamMembers = new TemplateSourcingTeamMembers();
				newTeamMembers.setTeamMemberType(team.getTeamMemberType());
				newTeamMembers.setUser(team.getUser());
				newTeamMembers.setSourcingForm(newForm);
				teamMembers.add(newTeamMembers);
			}
			newForm.setTeamMembers(teamMembers);
		}
			try {
				BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "Sourcing Form Template '"+newForm.getFormName()+ "' is created", newForm.getCreatedBy().getTenantId(), newForm.getCreatedBy(), new Date(), ModuleType.SourcingTemplate);
				buyerAuditTrailDao.save(ownerAuditTrail);
			} catch (Exception e) {
				LOG.info("Error to create audit trails message");
			}
		newForm = sourcingTemplateDao.update(newForm);

		List<String> assignedUserId = sourcingTemplateService.getSourcingTemplateByUserIdAndTemplateId(oldForm.getId(), user.getTenantId());
		if (CollectionUtil.isNotEmpty(assignedUserId)) {
			LOG.info("assignedSourcingTemplates 12");
			for (String assgnedUser : assignedUserId) {
				User userObj = userService.getUsersForRfxById(assgnedUser);
				List<SourcingFormTemplate> assignedTemplateList = userObj.getAssignedSourcingTemplates();
				assignedTemplateList.add(newForm);
				userObj.setAssignedSourcingTemplates(assignedTemplateList);
				userService.updateUser(userObj);
			}
		}

		return newForm;
	}

	@Override
	public boolean isTemplateExists(String formName, String tenantId) {
		return sourcingTemplateDao.isTemplateExists(formName, tenantId);
	}

	@Override
	public List<SourcingFormTemplate> getSourcingFormbyteantId(String loggedInUserTenantId) {
		return sourcingTemplateDao.getSourcingFormbyteantId(loggedInUserTenantId);
	}

	@Override
	public List<SourcingFormTemplate> getAllTemplate(String loggedInUserTenantId, TableDataInput input, String userId) {
		ArrayList<SourcingFormTemplate> templateList = new ArrayList<>();
		List<SourcingFormTemplate> tempList = sourcingTemplateDao.getAllTemplate(loggedInUserTenantId, input, userId);
		LOG.info("Size of the List " + templateList.size());
		for (SourcingFormTemplate sourcingFormTemplate : tempList) {
			templateList.add(sourcingFormTemplate.createShallowCopy());
		}
		return templateList;
	}

	@Override
	public List<SourcingFormTemplate> findByTemplateNameForTenant(String searchValue, String tenantId, String pageNo, String userId) {
		return sourcingTemplateDao.findByTemplateNameForTenant(searchValue, tenantId, pageNo, userId);
	}

	@Override
	public List<SourcingFormTemplate> findAllActiveSourcingTemplateForTenantId(String tenantId) {
		ArrayList<SourcingFormTemplate> templateList = new ArrayList<>();
		List<SourcingFormTemplate> tempList = sourcingTemplateDao.findAllActiveSourcingTemplateForTenantId(tenantId);
		LOG.info("Size of the List " + templateList.size());
		for (SourcingFormTemplate sourcingFormTemplate : tempList) {
			templateList.add(sourcingFormTemplate.createShallowCopy());
		}
		return templateList;
	}

	@Override
	public List<SourcingFormTemplate> findAssignActiveSourcingTemplateForTenant(String tenantId, TableDataInput input, String userId) {

		List<SourcingFormTemplate> sourcingTemplateList = sourcingTemplateDao.findAllAssignActiveSourcingTemplateForTenant(tenantId, input, userId);
		for (SourcingFormTemplate sourcingFormTemplate : sourcingTemplateList) {
			String desc = sourcingFormTemplate.getDescription();
			if (desc != null) {
				desc = desc.length() > 50 ? desc.substring(0, 30) + "..." : desc;
				sourcingFormTemplate.setDescription(desc);
			}
		}
		return sourcingTemplateList;
	}

	@Override
	public long getBqCount(String formId) {
		return sourcingTemplateDao.getBqCount(formId);

	}

	@Override
	public List<String> getTemplateByUserIdAndTemplateId(String tempId, String loggedInUserTenantId) {
		return sourcingTemplateDao.getTemplateByUserIdAndTemplateId(tempId, loggedInUserTenantId);
	}

	@Override
	public void sendTeamMemberEmailNotificationEmail(User user, TeamMemberType memberType, User createdBy, String formName, String referenceNumber, String id, String formId) {

		try {
			String subject = "You have been Invited as TEAM MEMBER In Sourcing Request";
			String url = APP_URL;
			HashMap<String, Object> map = new HashMap<String, Object>();

			map.put("userName", user.getName());
			map.put("memberType", memberType);

			if (memberType == TeamMemberType.Editor)
				map.put("memberMessage", " Allows you to edit the entire draft stage of the Sourcing Request but not finish the Sourcing Request");
			else if (memberType == TeamMemberType.Viewer)
				map.put("memberMessage", "Allows you to view entire draft stage of the Sourcing Request without the ability to edit");
			else
				map.put("memberMessage", "Allows you to perform the same actions as the Sourcing Request Owner.");

			map.put("eventName", formName);
			map.put("createdBy", createdBy.getName());
			map.put("eventId", formId);
			map.put("eventRefNum", referenceNumber);
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			String timeZone = "GMT+8:00";
			timeZone = getTimeZoneByBuyerSettings(user.getTenantId(), timeZone);
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("date", df.format(new Date()));
			map.put("loginUrl", APP_URL + "/login");
			url = APP_URL + "/buyer/createSourcingFormDetails/" + id;
			map.put("appUrl", url);
			String message = FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerConfiguration.getTemplate(Global.TEAM_MEMBER_TEMPLATE_SOURCING), map);
			if(user.getEmailNotifications()) {
				notificationService.sendEmail(user.getCommunicationEmail(), subject, message);
			}
			String notificationMessage = messageSource.getMessage("team.rfs.add", new Object[] { memberType, formName }, Global.LOCALE);
			sendDashboardNotification(user, url, subject, notificationMessage);

		} catch (Exception e) {
			LOG.error("error in sending team member email " + e.getMessage(), e);
		}

	}

	private String getTimeZoneByBuyerSettings(String tenantId, String timeZone) {
		try {
			if (StringUtils.checkString(tenantId).length() > 0) {
				String time = buyerSettingsService.getBuyerTimeZoneByTenantId(tenantId);
				if (time != null) {
					timeZone = time;
				}
			}
		} catch (Exception e) {
			LOG.error("Error while fetching buyer time zone :" + e.getMessage(), e);
		}
		return timeZone;
	}

	private void sendDashboardNotification(User messageTo, String url, String subject, String notificationMessage) {
		NotificationMessage message = new NotificationMessage();
		message.setCreatedBy(null);
		message.setCreatedDate(new Date());
		message.setMessage(notificationMessage);
		message.setNotificationType(NotificationType.EVENT_MESSAGE);
		message.setMessageTo(messageTo);
		message.setSubject(subject);
		message.setTenantId(messageTo.getTenantId());
		message.setUrl(url);
		dashboardNotificationService.save(message);
	}

	@Override
	public List<SourcingTemplateCq> getAllQuestionnarieByOrder(String formId) {
		return sourcingFormCqDao.getAllQuestionnarieByOrder(formId);
	}

	@Override
	public List<SourcingTemplateCq> getAllQuestionnarieOrderByDate(String formId) {
		return sourcingFormCqDao.getSourcingFormCqByFormId(formId);
	}

	@Override
	public long getTotalTemplateCountForTenant(String tenantId, String userId) {
		return sourcingTemplateDao.getTotalTemplateCountForTenant(tenantId, userId);
	}

	@Override
	public List<SourcingFormTemplate> findTemplatesBySearchValForTenant(String searchValue, Integer pageNo, Integer pageLength, String tenantId, String userId) {
		Integer start = 0;
		if (pageNo != null) {
			start = pageNo - 1;
		}
		if (pageLength != null) {
			start = start * pageLength;
		}
		LOG.info(" start  : " + start);

		return sourcingTemplateDao.findTemplatesBySearchValForTenant(searchValue, pageNo, pageLength, start, tenantId, userId);
	}

	@Override
	public long findTemplatesCountBySearchValForTenant(String searchValue, String tenantId, String userId) {
		return sourcingTemplateDao.findTemplatesCountBySearchValForTenant(searchValue, tenantId, userId);
	}

	@Override
	public List<String> getSourcingTemplateByUserIdAndTemplateId(String templateId, String tenantId) {
		return sourcingTemplateDao.getSourcingTemplateByUserIdAndTemplateId(templateId, tenantId);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteusersForRfsTemplate(String id) {
		sourcingTemplateDao.deleteusersForRfsTemplate(id);
	}

	@Override
	@Transactional(readOnly = false)
	public RfsTemplateDocument saveRfsTemplateDocument(RfsTemplateDocument rfsDocument) {
		return rfsTemplateDocumentDao.saveOrUpdate(rfsDocument);
	}

	public List<RfsTemplateDocument> findAllTemplateDocsBytemplateId(String templateId) {
		return rfsTemplateDocumentDao.findAllTemplateDocsBytemplateId(templateId);
	}

	@Override
	public void downloadRfsTemplateDocument(String docId, HttpServletResponse response) throws Exception {
		RfsTemplateDocument docs = rfsTemplateDocumentDao.findById(docId);
		response.setContentType(docs.getCredContentType());
		response.setContentLength(docs.getFileData().length);
		response.setHeader("Content-Disposition", "attachment; filename=\"" + docs.getFileName() + "\"");
		FileCopyUtils.copy(docs.getFileData(), response.getOutputStream());
		response.flushBuffer();
		response.setStatus(HttpServletResponse.SC_OK);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void removeDocument(RfsTemplateDocument removeDoc) {
		rfsTemplateDocumentDao.delete(removeDoc);
	}

	@Override
	public RfsTemplateDocument findDocsById(String id) {
		return rfsTemplateDocumentDao.findDocsById(id);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateDocument(String docId, String docDesc, String formId) {
		RfsTemplateDocument rfsTemplateDocument = rfsTemplateDocumentDao.findDocsById(docId);
		rfsTemplateDocument.setDescription(docDesc);
		SourcingFormTemplate sourcingFormTemplate = getSourcingFormbyId(formId);
		rfsTemplateDocument.setSourcingFormTemplate(sourcingFormTemplate);
		saveRfsTemplateDocument(rfsTemplateDocument);
	}
}
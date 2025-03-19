package com.privasia.procurehere.web.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.privasia.procurehere.core.entity.SourcingFormRequestSor;
import com.privasia.procurehere.core.entity.SourcingFormRequestSorItem;
import com.privasia.procurehere.core.pojo.SorPojo;
import com.privasia.procurehere.service.SourcingFormRequestSorService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.ErpSetupDao;
import com.privasia.procurehere.core.dao.TatReportDao;
import com.privasia.procurehere.core.entity.BusinessUnit;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.ErpSetup;
import com.privasia.procurehere.core.entity.EventTeamMember;
import com.privasia.procurehere.core.entity.RequestAudit;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.entity.RfsDocument;
import com.privasia.procurehere.core.entity.RfxTemplate;
import com.privasia.procurehere.core.entity.SourcingFormApprovalRequest;
import com.privasia.procurehere.core.entity.SourcingFormApprovalUserRequest;
import com.privasia.procurehere.core.entity.SourcingFormRequest;
import com.privasia.procurehere.core.entity.SourcingFormRequestBq;
import com.privasia.procurehere.core.entity.SourcingFormRequestCqItem;
import com.privasia.procurehere.core.entity.SourcingFormTeamMember;
import com.privasia.procurehere.core.entity.SourcingFormTemplate;
import com.privasia.procurehere.core.entity.SourcingTemplateCq;
import com.privasia.procurehere.core.entity.TatReport;
import com.privasia.procurehere.core.entity.TemplateField;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.AuctionType;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.RequestAuditType;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.SourcingFormStatus;
import com.privasia.procurehere.core.enums.TeamMemberType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.BqRequiredException;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.pojo.UserPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.DateUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.ApprovalService;
import com.privasia.procurehere.service.BusinessUnitService;
import com.privasia.procurehere.service.ErpIntegrationService;
import com.privasia.procurehere.service.EventIdSettingsService;
import com.privasia.procurehere.service.OwnerSettingsService;
import com.privasia.procurehere.service.RfxTemplateService;
import com.privasia.procurehere.service.SourcingFormRequestBqService;
import com.privasia.procurehere.service.SourcingFormRequestCqItemService;
import com.privasia.procurehere.service.SourcingFormRequestService;
import com.privasia.procurehere.service.SourcingTemplateService;
import com.privasia.procurehere.service.TatReportService;
import com.privasia.procurehere.service.UserService;
import com.privasia.procurehere.web.editors.SourcingFormApprovalUserRequestEditor;
import com.privasia.procurehere.web.editors.UserEditor;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

/**
 * @author sarang
 */
@Controller
@RequestMapping("/buyer")
public class SourcingSummeryController {

	private static final Logger LOG = LogManager.getLogger(SourcingSummeryController.class);

	@Autowired
	EventIdSettingsService eventIdSettingsService;

	@Autowired
	BusinessUnitService businessUnitService;

	@Autowired
	RfxTemplateService rfxTemplateService;

	@Autowired
	ApprovalService approvalService;

	@Autowired
	SourcingFormRequestService requestService;

	@Autowired
	SourcingFormRequestBqService bqService;

	@Resource
	MessageSource messageSource;

	@Autowired
	SourcingFormRequestService sourcingFormRequestService;

	@Autowired
	UserService userService;

	@Autowired
	UserEditor userEditor;

	@Autowired
	SourcingFormApprovalUserRequestEditor sourcingFormApprovalUserRequestEditor;

	@Autowired
	OwnerSettingsService ownerSettingsService;

	@Autowired
	ErpSetupDao erpSetupDao;

	@Autowired
	ErpIntegrationService erpIntegrationService;

	@Autowired
	SourcingTemplateService sourcingTemplateService;

	@Autowired
	SourcingFormRequestCqItemService sourcingFormRequestCqItemService;

	@Autowired
	TatReportDao tatReportDao;

	@Autowired
	TatReportService tatReportService;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Autowired
	SourcingFormRequestSorService sourcingFormRequestSorService;

	@InitBinder
	public void InitBinder(WebDataBinder binder, HttpSession session) {
		binder.registerCustomEditor(User.class, userEditor);
		binder.registerCustomEditor(SourcingFormApprovalUserRequest.class, sourcingFormApprovalUserRequestEditor);

	}

	@RequestMapping("/sourcingRequestSummary/{reqestId}")
	public String sourcingRequestSummery(@PathVariable("reqestId") String formId, Model model) {
		constructRequesrtSummerAttribute(formId, model);
		return "sourcingRequestSummary";
	}

	/**
	 * <p>
	 * Construct data for summary page
	 * </p>
	 * 
	 * @param requestId
	 * @param model
	 * @return void
	 */
	private void constructRequesrtSummerAttribute(String requestId, Model model) {
		try {
			List<SourcingFormRequestBq> bqList = requestService.getSourcingRequestBq(requestId);
			SourcingFormRequest request = requestService.getSourcingRequestByIdForSummary(requestId);

			List<User> userList = new ArrayList<User>();
			if (CollectionUtil.isNotEmpty(request.getSourcingFormApprovalRequests())) {
				for (SourcingFormApprovalRequest sourcingFormApprovalRequest : request.getSourcingFormApprovalRequests()) {
					for (SourcingFormApprovalUserRequest user : sourcingFormApprovalRequest.getApprovalUsersRequest()) {
						User u = new User(user.getUser().getId(), user.getUser().getLoginId(), user.getUser().getName(), user.getUser().getCommunicationEmail(), user.getUser().getEmailNotifications(), user.getUser().getTenantId(), user.getUser().isDeleted());
						if (!userList.contains(u)) {
							userList.add(u);
						}
					}
				}
			}

			List<UserPojo> userList1 = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", null);
			for (UserPojo user : userList1) {
				User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted());
				if (!userList.contains(u)) {
					userList.add(u);
				}
			}

			model.addAttribute("userList1", userList);

			Map<SourcingTemplateCq, List<SourcingFormRequestCqItem>> cqList = new LinkedHashMap<SourcingTemplateCq, List<SourcingFormRequestCqItem>>();
			RequestAudit audit = new RequestAudit();
			model.addAttribute("audit", audit);

			SourcingFormTemplate form = requestService.getSourcingFormByReqId(requestId);
			List<SourcingTemplateCq> cqList1 = sourcingFormRequestCqItemService.findCqsByTempId(form.getId());
			if (CollectionUtil.isNotEmpty(cqList1)) {
				for (SourcingTemplateCq sourcingTemplateCq : cqList1) {
					List<SourcingFormRequestCqItem> cqItemList = requestService.getCqItembyRequestIdCqId(requestId, sourcingTemplateCq.getId());
					if (CollectionUtil.isNotEmpty(cqItemList)) {
						for (SourcingFormRequestCqItem item : cqItemList) {
							List<SourcingFormRequestCqItem> itemList = cqList.get(sourcingTemplateCq);
							if (itemList == null) {
								itemList = new ArrayList<SourcingFormRequestCqItem>();
								itemList.add(item);
								cqList.put(sourcingTemplateCq, itemList);
							} else {
								itemList.add(item);
							}
						}
					} else {
						// if user has not clicked on View CQ yet, auto add the CQ
						SourcingFormRequest sourcingFormRequest = sourcingFormRequestService.loadFormById(requestId);
						List<SourcingFormRequestCqItem> sourcingReqCqItem = sourcingFormRequestCqItemService.getAllSourcingCqItemByCqId(sourcingTemplateCq.getId(), requestId);
						if (CollectionUtil.isEmpty(sourcingReqCqItem)) {
							sourcingFormRequestCqItemService.saveSourcingRequestCq(sourcingTemplateCq.getId(), sourcingFormRequest);
							sourcingReqCqItem = sourcingFormRequestCqItemService.getAllSourcingCqItemByCqId(sourcingTemplateCq.getId(), requestId);
						}
						cqList.put(sourcingTemplateCq, sourcingReqCqItem);
					}
				}
			}
			model.addAttribute("cqList", cqList);

			List<RequestAudit> requestAudit = requestService.getReqAudit(requestId);
			List<User> assignedTeamMembers = new ArrayList<>();
			if (request.getSourcingFormTeamMember() != null) {
				for (SourcingFormTeamMember rfaTeamMember : request.getSourcingFormTeamMember()) {
					assignedTeamMembers.add((User) rfaTeamMember.getUser().clone());
				}
			}
			List<User> userTeamMemberList = new ArrayList<User>();
			List<User> userTeamMember = userService.fetchAllActiveNormalUsersForTenant(SecurityLibrary.getLoggedInUserTenantId());
			for (User user : userTeamMember) {
				userTeamMemberList.add((User) user.clone());
			}
			userTeamMemberList.removeAll(assignedTeamMembers);
			model.addAttribute("userTeamMemberList", userTeamMemberList);
			EventPermissions eventPermissions = requestService.getUserPemissionsForRequest(SecurityLibrary.getLoggedInUser(), requestId);
			model.addAttribute("eventPermissions", eventPermissions);
			model.addAttribute("formId", requestId);
			model.addAttribute("aequestAudit", requestAudit);
			model.addAttribute("listDocs", sourcingFormRequestService.findAllPlainRfsDocsbyRfsIdWithInternal(requestId));

			// implemented for document editor
			if (eventPermissions.isEditor()) {
				for (RfsDocument document : request.getRfsDocuments()) {
					SourcingFormTeamMember sourcingFormTeamMember = sourcingFormRequestService.getTeamMemberByUserIdAndFormId(requestId, document.getUploadBy().getId());
					if (sourcingFormTeamMember != null) {
						String uploadedBy = (document.getUploadBy() != null ? document.getUploadBy().getId() : "");
						if (sourcingFormTeamMember.getTeamMemberType() == TeamMemberType.Editor && uploadedBy.equals(SecurityLibrary.getLoggedInUser().getId())) {
							LOG.info("editor member: ");
							document.setEditorMember(true);
						}
					}
				}
			}

			// implemented for document approval
			if (eventPermissions.isApprover()) {
				Boolean check = Boolean.FALSE;
				for (SourcingFormApprovalRequest requestapprove : request.getSourcingFormApprovalRequests()) {
					LOG.info("level check: " + requestapprove.getLevel());
					for (SourcingFormApprovalUserRequest appUser : requestapprove.getApprovalUsersRequest()) {
						if (appUser.getUser().getId().equals(SecurityLibrary.getLoggedInUser().getId())) {
							if (requestapprove.getLevel() == 1) {
								model.addAttribute("previousLevel", true);
							} else {
								if (check) {
									model.addAttribute("previousLevel", true);
								} else {
									model.addAttribute("previousLevel", false);
								}
							}
						}
					}
					check = requestapprove.isDone();
				}
			}

			model.addAttribute("sourcingFormRequest", request);
			model.addAttribute("ownerSettings", ownerSettingsService.getOwnersettings());

			Integer additionalLevelStart = 0;
			Integer additionalLevelNext = 0;

			if (request != null && request.getStatus() == SourcingFormStatus.APPROVED) {
				additionalLevelNext = request.getSourcingFormApprovalRequests().size() + 1;
				if (CollectionUtil.isNotEmpty(request.getSourcingFormApprovalRequests())) {
					for (SourcingFormApprovalRequest sourcingFormApprovalRequest : request.getSourcingFormApprovalRequests()) {
						if (sourcingFormApprovalRequest.isDone()) {
							additionalLevelStart = sourcingFormApprovalRequest.getLevel();
						} else {
							break;
						}
					}
				}
			}
			model.addAttribute("additionalLevelStart", additionalLevelStart + 1);
			model.addAttribute("additionalLevelNext", additionalLevelNext);

			if (form != null) {
				form.getReadOnlyTeamMember();
				model.addAttribute("readOnlyTeamMember", form.getReadOnlyTeamMember());
			}
			// model.addAttribute("sourcingAdditonalApprover", new
			// SourcingFormRequest(request.getSourcingFormApprovalRequests(), request));
			model.addAttribute("bqList", bqList);

			List<SourcingFormRequestSor> sorList = requestService.getSourcingRequestSor(requestId);
			model.addAttribute("sorList", sorList);
		} catch (

		Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	@RequestMapping("/finishRequest/{requestId}")
	public String finishRequest(@PathVariable String requestId, Model model) {
		SourcingFormRequest sourcingFormRequest = new SourcingFormRequest();
		sourcingFormRequest.setId(requestId);
		try {
			LOG.info("FinishRequest method execution...");
			bqService.checkMandatoryToFinishEvent(requestId);
			Boolean budgetCheck = requestService.isBudgetCheckingEnabledForBusinessUnit(SecurityLibrary.getLoggedInUserTenantId(), requestId);

			// Check if budget checking ERP interface is enabled
			ErpSetup erpSetup = erpSetupDao.getErpConfigBytenantId(SecurityLibrary.getLoggedInUserTenantId());
			if (erpSetup != null && Boolean.TRUE == erpSetup.getIsErpEnable() && Boolean.TRUE == erpSetup.getEnableRfsErpPush() && Boolean.TRUE == budgetCheck) {
				erpIntegrationService.transferRfsToErp(requestId, erpSetup, SecurityLibrary.getLoggedInUser());
				sourcingFormRequest = requestService.getSourcingRequestById(sourcingFormRequest.getId());
			} else {
				sourcingFormRequest = approvalService.doRequestApproval(sourcingFormRequest, SecurityLibrary.getLoggedInUser());
			}

			if (Boolean.TRUE == sourcingFormRequest.getEnableApprovalReminder()) {
				LOG.info("Enable ......" + sourcingFormRequest.getEnableApprovalReminder());
				LOG.info("count ........" + sourcingFormRequest.getReminderCount());
				LOG.info("hours ......" + sourcingFormRequest.getReminderAfterHour());
				if (sourcingFormRequest.getReminderAfterHour() == null) {
					model.addAttribute("error", messageSource.getMessage("approval.reminder.add.hour", new Object[] {}, Global.LOCALE));
					return "redirect:/buyer/sourcingRequestSummary/" + requestId;
				}
				if (sourcingFormRequest.getReminderCount() == null) {
					model.addAttribute("error", messageSource.getMessage("approval.reminder.count.reminder", new Object[] {}, Global.LOCALE));
					return "redirect:/buyer/sourcingRequestSummary/" + requestId;
				}
			}

			if (sourcingFormRequest != null) {
				RequestAudit audit = new RequestAudit();
				audit.setAction(RequestAuditType.FINISH);
				audit.setActionBy(SecurityLibrary.getLoggedInUser());
				audit.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
				audit.setActionDate(new Date());
				audit.setReq(sourcingFormRequest);
				requestService.saveAudit(audit);
			}

			updateTatReportSourcingData(sourcingFormRequest, SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser());

			try {
				LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.FINISH, "Sourcing Form '" + sourcingFormRequest.getFormId() + "' is Sent For Approval", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFS);
				buyerAuditTrailDao.save(buyerAuditTrail);
				LOG.info("--------------------------AFTER AUDIT---------------------------------------");
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
			}

			model.addAttribute("sourcingFormRequest", sourcingFormRequest);
			model.addAttribute("success", messageSource.getMessage("request.summery.success", new Object[] { sourcingFormRequest.getSourcingFormName() }, Global.LOCALE));
			return "redirect:/buyer/buyerDashboard";
		} catch (NotAllowedException e) {
			LOG.error("Error while checking mandatory cq bq [ " + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("request.summery.error", new Object[] {}, Global.LOCALE));
		} catch (BqRequiredException e) {
			LOG.error("Error while checking mandatory  bq [ " + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("rfs.summery.Bq.error", new Object[] {}, Global.LOCALE));
		} catch (ApplicationException e) {
			LOG.error("Error during Finish Request : " + e.getMessage(), e);
			model.addAttribute("error", "Error during Finish Request : " + e.getMessage());
		} catch (Exception e) {
			LOG.error("Error during Finish Request:" + e.getMessage(), e);
			// model.addAttribute("error", "Error occured during submission : " + e.getMessage());
			model.addAttribute("error", messageSource.getMessage("sourcingsummary.error.during.submission", new Object[] { e.getMessage() }, Global.LOCALE));
		}
		return "redirect:/buyer/sourcingRequestSummary/" + requestId;
	}

	private void updateTatReportSourcingData(SourcingFormRequest sourcingFormRequest, String tenantId, User loggedInUser) {
		try {
			sourcingFormRequest = requestService.getSourcingRequestById(sourcingFormRequest.getId());

			TatReport tatReport = tatReportService.getRfsForTatReportByRfsIDAndFormIdAndTenantId(sourcingFormRequest.getFormId(), SecurityLibrary.getLoggedInUserTenantId(), sourcingFormRequest.getId());
			if (tatReport != null) {
				tatReportService.updateTatReportReqFinishDateAndStatus(tatReport.getId(), new Date(), sourcingFormRequest.getStatus());
				if (CollectionUtil.isEmpty(sourcingFormRequest.getSourcingFormApprovalRequests())) {
					tatReportService.updateTatReportReqApprovedAndApprovalDaysCountAndFirstAndLastApprovedDate(tatReport.getId(), new Date(), BigDecimal.ZERO, new Date());
				}
			} else {

				tatReport = new TatReport();
				tatReport.setRequestGeneratedId(sourcingFormRequest.getId());
				tatReport.setFormId(sourcingFormRequest.getFormId());
				tatReport.setSourcingFormName(sourcingFormRequest.getSourcingFormName());
				tatReport.setBusinessUnit(sourcingFormRequest.getBusinessUnit().getUnitName());
				tatReport.setCostCenter(sourcingFormRequest.getCostCenter() != null ? sourcingFormRequest.getCostCenter().getCostCenter() : "");
				tatReport.setRequestOwner(sourcingFormRequest.getFormOwner() != null ? (sourcingFormRequest.getFormOwner().getName() + " " + sourcingFormRequest.getFormOwner().getLoginId()) : "");
				tatReport.setGroupCode(sourcingFormRequest.getGroupCode() != null ? sourcingFormRequest.getGroupCode().getGroupCode() : (sourcingFormRequest.getGroupCodeOld() != null ? sourcingFormRequest.getGroupCodeOld() : ""));
				tatReport.setAvailableBudget(sourcingFormRequest.getBudgetAmount());
				tatReport.setEstimatedBudget(sourcingFormRequest.getEstimatedBudget());
				tatReport.setCreatedDate(sourcingFormRequest.getCreatedDate());
				tatReport.setProcurementMethod(sourcingFormRequest.getProcurementMethod() != null ? sourcingFormRequest.getProcurementMethod().getProcurementMethod() : "");
				tatReport.setProcurementCategories(sourcingFormRequest.getProcurementCategories() != null ? sourcingFormRequest.getProcurementCategories().getProcurementCategories() : "");
				tatReport.setBaseCurrency(sourcingFormRequest.getCurrency() != null ? sourcingFormRequest.getCurrency().getCurrencyCode() : "");
				tatReport.setTenantId(loggedInUser.getTenantId());
				tatReport.setReqDecimal(sourcingFormRequest.getDecimal());

				tatReport.setFirstApprovedDate(sourcingFormRequest.getFirstApprovedDate() != null ? sourcingFormRequest.getFirstApprovedDate() : null);
				tatReport.setLastApprovedDate(sourcingFormRequest.getApprovedDate() != null ? sourcingFormRequest.getApprovedDate() : null);

				if (sourcingFormRequest.getApprovedDate() != null && sourcingFormRequest.getSubmittedDate() != null) {
					double diffInDays = DateUtil.differenceInDays(sourcingFormRequest.getApprovedDate(), sourcingFormRequest.getSubmittedDate());
					tatReport.setReqApprovalDaysCount(BigDecimal.valueOf(diffInDays).setScale(2, RoundingMode.HALF_UP));
				}
				tatReport.setFinishDate(sourcingFormRequest.getSubmittedDate());
				tatReport.setRequestStatus(sourcingFormRequest.getStatus());
				tatReportService.saveTatReport(tatReport);
			}

		} catch (Exception e) {
			LOG.error("Error during saving tat report :" + e.getMessage(), e);
		}
	}

	@RequestMapping("/viewSourcingSummary/{requestId}")
	public String editSourcingRequestSummary(@PathVariable("requestId") String requestId, Model model, RedirectAttributes redir, HttpServletRequest request) {
		model.addAttribute("ownerSettings", ownerSettingsService.getOwnersettings());
		constructRequesrtSummerAttribute(requestId, model);
		LOG.info(">>>>>>>>>>> requestId>>>>>> "+requestId);
		LOG.info(">>>>>>>>>>> LOGGED IN USER>>>>>> "+SecurityLibrary.getLoggedInUser());
		EventPermissions eventPermissions = requestService.getUserPemissionsForRequest(SecurityLibrary.getLoggedInUser(), requestId);
		LOG.info("EVENT PERMISSION PR ID ???????>>>>>>>>>>>>>>> "+eventPermissions.getPrId());
		// we add this here becoz we use the conclude event page in our summary page
		model.addAttribute("event", new RfaEvent());
		model.addAttribute("eventPermissions", eventPermissions);
		RfxTypes[] rfxTypesValues = { RfxTypes.RFI, RfxTypes.RFP, RfxTypes.RFQ, RfxTypes.RFT, RfxTypes.RFA };
		model.addAttribute("rfxType", rfxTypesValues);
		model.addAttribute("rfxTemplateList", rfxTemplateService.findAllActiveTemplatesByRfxTypeForTenantId(SecurityLibrary.getLoggedInUserTenantId(), RfxTypes.RFI));
		LOG.info("permissions>>>>>>> "+checkPermissionToAllow(eventPermissions));
		//commented this out as requirement ph-4105
/*		if (!checkPermissionToAllow(eventPermissions)) {
			return "redirect:/403_error";
		}*/

		return "viewSourcingRequestSummary";
	}

	private boolean checkPermissionToAllow(EventPermissions eventPermissions) {
		boolean allow = false;
		if (eventPermissions.isRequesterUser() || eventPermissions.isOwner() || eventPermissions.isEditor() || eventPermissions.isApprover() || eventPermissions.isEvaluator() || eventPermissions.isLeadEvaluator() || eventPermissions.isOpener() || eventPermissions.isViewer()) {
			allow = true;

		}
		if (SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY")) {
			allow = true;
		}
		return allow;
	}

	@RequestMapping(value = { "/SR/checkBusinessUnitEmpty/{requestId}/{rfxType}/{templateId}", "/SR/checkBusinessUnitEmpty/{requestId}", "/SR/checkBusinessUnitEmpty/{requestId}/{rfxType}" }, method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<BusinessUnit>> checkBusinessUnitEmpty(@PathVariable("requestId") String requestId, @PathVariable(value = "templateId", required = false) String templateId, @PathVariable(value = "rfxType", required = false) String rfxType) {
		HttpHeaders headers = new HttpHeaders();
		String type = "SR";
		if (StringUtils.checkString(rfxType).length() > 0) {
			type = rfxType;
		}
		List<BusinessUnit> businessUnits = null;
		try {
			if (eventIdSettingsService.isBusinessSettingEnable(SecurityLibrary.getLoggedInUserTenantId(), type)) {
				BusinessUnit businessUnit = null;
				if (StringUtils.checkString(templateId).length() > 0) {
					RfxTemplate rfxTemplate = rfxTemplateService.getRfxTemplateByIdForBU(templateId);
					for (TemplateField field : rfxTemplate.getFields()) {
						switch (field.getFieldName()) {
						case BUSINESS_UNIT:
							if (field.getDefaultValue() != null) {
								businessUnit = businessUnitService.getPlainBusinessUnitById(field.getDefaultValue());
								if (businessUnit != null) {
									LOG.error("Bussiness unit is found in template");
									return new ResponseEntity<List<BusinessUnit>>(null, headers, HttpStatus.EXPECTATION_FAILED);
								}
							}
							break;
						default:
							LOG.error("Bussiness unit not found in template");
						}
					}
				}
				SourcingFormRequest sourcingFormRequest = requestService.getSourcingRequestById(requestId);
				if (sourcingFormRequest.getBusinessUnit() != null) {
					LOG.error("Bussiness unit is found");
					return new ResponseEntity<List<BusinessUnit>>(null, headers, HttpStatus.EXPECTATION_FAILED);
				}
				businessUnits = businessUnitService.getPlainActiveBusinessUnitForTenant(SecurityLibrary.getLoggedInUserTenantId());

				return new ResponseEntity<List<BusinessUnit>>(businessUnits, headers, HttpStatus.OK);
			} else {
				LOG.error("Business Unit not enabled ..");
				return new ResponseEntity<List<BusinessUnit>>(null, headers, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			LOG.error("Error checking BU : " + e.getMessage(), e);
			return new ResponseEntity<List<BusinessUnit>>(null, headers, HttpStatus.EXPECTATION_FAILED);
		}
	}

	@RequestMapping(path = "/copyRequestTo", method = RequestMethod.POST)
	public String copyEventTo(@RequestParam(name = "id") String requestId, @RequestParam(name = "businessUnitId", required = false) String businessUnitId, @RequestParam(name = "idRfxTemplate", required = false) String idRfxTemplate, @RequestParam(name = "selectedRfxType", required = false) RfxTypes selectedRfxType, @RequestParam(name = "auctionType", required = false) AuctionType auctionType, @RequestParam(name = "bqId", required = false) String bqId, @RequestParam(name = "concludeRemarks", required = false) String concludeRemarks, @RequestParam(name = "selectedDocs", required = false) List<String> selectedDocs, Model model, RedirectAttributes redir) {
		String newEventId = "";
		try {
			List<String> Docs = new ArrayList<>();
			Docs= selectedDocs;
			newEventId = requestService.createNextEvent(requestId, selectedRfxType, auctionType, bqId, SecurityLibrary.getLoggedInUser(), idRfxTemplate, businessUnitId, selectedDocs);
			redir.addAttribute("success", "New event created successfully");
			return "redirect:/buyer/" + selectedRfxType.name() + "/createEventDetails/" + newEventId;
		} catch (Exception e) {

			LOG.error(e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.creating.event", new Object[] { e.getMessage() }, Global.LOCALE));
			SourcingFormRequest request = requestService.findById(requestId);
			if (request != null) {
				RequestAudit audit = new RequestAudit();
				audit.setActionDate(new Date());
				audit.setAction(RequestAuditType.ERROR);
				audit.setActionBy(SecurityLibrary.getLoggedInUser());
				audit.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
				audit.setReq(request);
				audit.setDescription("Error while creating new Event:" + e.getMessage());
				audit = requestService.saveAudit(audit);
			}
		}
		return "redirect:/buyer/viewSourcingSummary/" + requestId;
	}


	@RequestMapping(path = "/deleteSorNewField", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<SourcingFormRequestSorItem>> deleteBqNewField(@RequestParam("bqId") String bqId, @RequestParam("label") String label, @RequestParam(name = "filterVal", required = false) String filterVal, @RequestParam(name = "searchVal", required = false) String searchVal, @RequestParam(name = "pageLength", required = false) Integer pageLength, @RequestParam(name = "pageNo", required = false) Integer pageNo) {
		LOG.info("deleteBqNewField label ::" + label + " bqId :: " + bqId + " filterVal  :" + filterVal + " searchVal : " + searchVal + " pageNo : " + pageNo + " pageLength :" + pageLength);
		HttpHeaders headers = new HttpHeaders();
		List<SourcingFormRequestSorItem> bqItemList = null;
		try {
			if (StringUtils.checkString(label).length() > 0) {
				sourcingFormRequestSorService.deletefieldInSorItems(bqId, label);

				Integer itemLevel = null;
				Integer itemOrder = null;
				Integer start = null;
				Integer length = null;
				if (StringUtils.checkString(filterVal).length() == 1) {
					filterVal = "";
				}
				if (StringUtils.checkString(filterVal).length() > 0) {
					itemLevel = 0;
					itemOrder = 0;
					String[] values = filterVal.split("\\.");
					itemLevel = Integer.parseInt(values[0]);
					itemOrder = Integer.parseInt(values[1]);
				}
				start = 0;
				length = pageLength;
				LOG.info(" itemOrder : " + itemOrder + " itemLevel :" + itemLevel);
				bqItemList = sourcingFormRequestSorService.getSorItemForSearchFilter(bqId, itemLevel, itemOrder, searchVal, start, length, pageNo);
				for (SourcingFormRequestSorItem bqItem : bqItemList) {
					if (bqItem.getUom() != null) {
						bqItem.getUom().setCreatedBy(null);
						bqItem.getUom().setModifiedBy(null);
					}
				}
				headers.add("success", "Column deleted Successfully");
			}
		} catch (Exception e) {
			LOG.error("Error while deleting SOR New Field " + e.getMessage(), e);
			headers.add("error", "Error while deleting SOR New Field : " + e.getMessage());
			return new ResponseEntity<List<SourcingFormRequestSorItem>>(bqItemList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<SourcingFormRequestSorItem>>(bqItemList, headers, HttpStatus.OK);
	}


	@RequestMapping(path = "/addNewColumnsSor", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<SourcingFormRequestSorItem>> addNewColumns(@RequestBody SorPojo rfqBq) throws JsonProcessingException {
		LOG.info("Enter this Block :: " + rfqBq.toString());
		HttpHeaders headers = new HttpHeaders();
		List<SourcingFormRequestSorItem> bqItemList = null;
		if (rfqBq.getField1Label() == null && rfqBq.getField2Label() == null && rfqBq.getField3Label() == null && rfqBq.getField4Label() == null) {
			headers.add("error", "Field cannot be empty");
			return new ResponseEntity<List<SourcingFormRequestSorItem>>(null, headers, HttpStatus.BAD_REQUEST);
		} else {
			SourcingFormRequestSor sourcingFormRequestSor = sourcingFormRequestSorService.getSorById(rfqBq.getId());
			if (sourcingFormRequestSor != null) {
				try {

					List<String> fieldLabelList = new ArrayList<String>();
					if (rfqBq.getField1Label() != null && fieldLabelList.contains(rfqBq.getField1Label().toLowerCase())) {
						headers.add("error", messageSource.getMessage("rft.bq.newfields.exists.error", new Object[] { rfqBq.getField1Label() }, Global.LOCALE));
						return new ResponseEntity<List<SourcingFormRequestSorItem>>(bqItemList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
					} else if (rfqBq.getField1Label() != null) {
						fieldLabelList.add(rfqBq.getField1Label().toLowerCase());
					}

					buildAddNewColumns(rfqBq, sourcingFormRequestSor);
					sourcingFormRequestSorService.updateSourcingSor(sourcingFormRequestSor);

					Integer itemLevel = null;
					Integer itemOrder = null;
					Integer start = null;
					Integer length = null;
					if (StringUtils.checkString(rfqBq.getFilterVal()).length() == 1) {
						rfqBq.setFilterVal("");
					}
					if (StringUtils.checkString(rfqBq.getFilterVal()).length() > 0) {
						itemLevel = 0;
						itemOrder = 0;
						String[] values = rfqBq.getFilterVal().split("\\.");
						itemLevel = Integer.parseInt(values[0]);
						itemOrder = Integer.parseInt(values[1]);
					}
					start = 0;
					length = rfqBq.getPageLength();

					// bqList = rfqBqService.findBqbyBqId(rftEventBq.getId());
					bqItemList = sourcingFormRequestSorService.getSorItemForSearchFilter(sourcingFormRequestSor.getId(), itemLevel, itemOrder, rfqBq.getSearchVal(), start, length, rfqBq.getPageNo());
					for (SourcingFormRequestSorItem bqItem : bqItemList) {
						if (bqItem.getUom() != null) {
							bqItem.getUom().setCreatedBy(null);
							bqItem.getUom().setModifiedBy(null);
						}
					}
					headers.add("success", messageSource.getMessage("rft.sor.newfields.success", new Object[] {}, Global.LOCALE));
					return new ResponseEntity<List<SourcingFormRequestSorItem>>(bqItemList, headers, HttpStatus.OK);
				} catch (Exception e) {
					headers.add("error", messageSource.getMessage("buyer.rftbq.newfields.error", new Object[] {}, Global.LOCALE));
					return new ResponseEntity<List<SourcingFormRequestSorItem>>(null, headers, HttpStatus.BAD_REQUEST);
				}
			}
		}
		return new ResponseEntity<List<SourcingFormRequestSorItem>>(bqItemList, headers, HttpStatus.OK);
	}

	protected void buildAddNewColumns(SorPojo bqPojo, SourcingFormRequestSor eventBq) {
		eventBq.setField1Label(bqPojo.getField1Label() != null ? bqPojo.getField1Label() : null);
		eventBq.setField1FilledBy(bqPojo.getField1FilledBy() != null ? bqPojo.getField1FilledBy() : null);
		eventBq.setField1ToShowSupplier(bqPojo.isField1ToShowSupplier());
		eventBq.setField1Required(bqPojo.isField1Required());
	}

	@RequestMapping(path = "/requestApproved", method = RequestMethod.POST)
	public String requestApproved(@RequestParam String requestId, @RequestParam String remarks, RedirectAttributes redir) {
		SourcingFormRequest requests = requestService.findById(requestId);
		try {
			SourcingFormRequest request = new SourcingFormRequest();
			request.setId(requestId);
			SourcingFormRequest req = approvalService.doApprovalRequest(request, SecurityLibrary.getLoggedInUser(), remarks, true);
			if (req != null) {
				RequestAudit audit = new RequestAudit();
				audit.setActionDate(new Date());
				audit.setAction(RequestAuditType.APPROVED);
				audit.setActionBy(SecurityLibrary.getLoggedInUser());
				audit.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
				audit.setDescription(remarks);
				audit.setReq(req);
				requestService.saveAudit(audit);
			}
			redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.request.approved", new Object[] { (req.getSourcingFormName() != null ? req.getSourcingFormName() : "") }, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error while approving request :" + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.approving.request", new Object[] { e.getMessage() }, Global.LOCALE));
		}

		try {
			LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
			BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.APPROVED, " '" + requests.getFormId() + "’ is Approved", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFS);
			buyerAuditTrailDao.save(buyerAuditTrail);
			LOG.info("--------------------------AFTER AUDIT---------------------------------------");
		} catch (Exception e) {
			LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
		}
		return "redirect:/buyer/buyerDashboard";
	}

	@RequestMapping(path = "/requestRejected", method = RequestMethod.POST)
	public String requestRejected(@RequestParam String requestId, @RequestParam String remarks, RedirectAttributes redir) {
		SourcingFormRequest request = requestService.findById(requestId);
		try {
			if (StringUtils.checkString(remarks).length() == 0) {
				redir.addFlashAttribute("error", messageSource.getMessage("flasherror.remark.cannot.empty", new Object[] {}, Global.LOCALE));
				return "redirect:viewSourcingSummary/" + requestId;
			}
			SourcingFormRequest sr = new SourcingFormRequest();
			sr.setId(requestId);
			SourcingFormRequest req = approvalService.doApprovalRequest(sr, SecurityLibrary.getLoggedInUser(), remarks, false);
			if (req != null) {
				RequestAudit audit = new RequestAudit();
				audit.setAction(RequestAuditType.REJECTED);
				audit.setActionBy(SecurityLibrary.getLoggedInUser());
				audit.setActionDate(new Date());
				audit.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
				audit.setDescription(remarks);
				audit.setReq(req);
				requestService.saveAudit(audit);
			}
			redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.request.rejected", new Object[] { (req.getSourcingFormName() != null ? req.getSourcingFormName() : "") }, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error while rejecting request :" + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.rejecting.request", new Object[] { e.getMessage() }, Global.LOCALE));
		}

		try {
			LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
			BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.REJECTED, " '" + request.getFormId() + "' is Rejected", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFS);
			buyerAuditTrailDao.save(buyerAuditTrail);
			LOG.info("--------------------------AFTER AUDIT---------------------------------------");
		} catch (Exception e) {
			LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
		}
		return "redirect:/buyer/buyerDashboard";
	}

	@RequestMapping(path = "/cancelSourcingReq/{reqId}", method = RequestMethod.POST)
	public String cancelSourcingReq(RedirectAttributes redir, @PathVariable("reqId") String reqId, @RequestParam(name = "description") String description) {
		LOG.info("CANCEL REQ METHOD IS CALLED " + description);
		SourcingFormRequest req = requestService.getSourcingRequestById(reqId);
		requestService.cancelSourcingRequest(reqId, description);

		tatReportService.updateTatReportReqStatus(req.getFormId(), SecurityLibrary.getLoggedInUserTenantId(), req.getId(), SourcingFormStatus.CANCELED);

		// redir.addFlashAttribute("success", "Request is cancelled");
		redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.request.cancelled", new Object[] { (req.getSourcingFormName() != null ? req.getSourcingFormName() : "") }, Global.LOCALE));
		return "redirect:/buyer/buyerDashboard";

	}

	@RequestMapping(value = "/sourcingTeamMembersList/{formId}", method = RequestMethod.GET)
	public ResponseEntity<TableData<EventTeamMember>> eventTeamMembersList(@PathVariable(name = "formId") String formId, TableDataInput input) {
		TableData<EventTeamMember> data = null;
		try {
			data = new TableData<EventTeamMember>(requestService.getPlainTeamMembersForSourcing(formId));
			data.setDraw(input.getDraw());
		} catch (Exception e) {
			LOG.error("Error while loading draft list : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<EventTeamMember>>(data, HttpStatus.OK);
	}

	@RequestMapping(path = "/downlaodSourcingSummary/{formId}", method = RequestMethod.GET)
	public void downlaodPrSummary(@PathVariable("formId") String formId, HttpServletResponse response, HttpSession session) throws Exception {
		try {
			SourcingFormRequest sourcingFormRequest = sourcingFormRequestService.loadFormById(formId);
			String sourcingFilename = ("RFS Summary_" + sourcingFormRequest.getFormId()).replace("/", "-") + ".pdf";
			String filename = sourcingFilename;

			try {
				LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "RFS Summary is downloaded for '" + sourcingFormRequest.getFormId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFS);
				buyerAuditTrailDao.save(buyerAuditTrail);
				LOG.info("--------------------------AFTER AUDIT---------------------------------------");
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
			}

			JasperPrint jasperPrint = sourcingFormRequestService.getSourcingSummaryPdf(sourcingFormRequest, (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY));
			if (jasperPrint != null) {
				streamReport(jasperPrint, filename, response);
			}

		} catch (Exception e) {
			LOG.error("Could not generate RFS Summary Report. " + e.getMessage(), e);
		}
	}

	private void streamReport(JasperPrint jasperPrint, String filename, HttpServletResponse response) throws JRException, IOException {
		response.setContentType("application/pdf");
		response.addHeader("Content-Disposition", "attachment; filename=" + filename);

		JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
		response.getOutputStream().flush();
		response.getOutputStream().close();

	}

	@PostMapping("/concludeSourcingRequest")
	public String concludeSourcingRequest(@ModelAttribute(name = "sourcingFormRequest") SourcingFormRequest sourcingRequest, Model model, RedirectAttributes redir) {
		try {
			sourcingRequest = sourcingFormRequestService.concludeSourcingRequest(sourcingRequest, SecurityLibrary.getLoggedInUser());
			try {
				RequestAudit audit = new RequestAudit();
				audit.setAction(RequestAuditType.CONCLUDE);
				audit.setActionBy(SecurityLibrary.getLoggedInUser());
				audit.setActionDate(new Date());
				audit.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
				audit.setDescription("RFS " + sourcingRequest.getFormId() + " is concluded. Remark: " + sourcingRequest.getConcludeRemarks());
				audit.setReq(sourcingRequest);
				requestService.saveAudit(audit);
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
			try {
				LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CONCLUDE, "RFS '" + sourcingRequest.getFormId() + "' is concluded with remark: " + sourcingRequest.getConcludeRemarks() + " ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFS);
				buyerAuditTrailDao.save(buyerAuditTrail);
				LOG.info("--------------------------AFTER AUDIT---------------------------------------");
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
			}
			redir.addAttribute("success", "RFS Concluded Successfully");
		} catch (Exception e) {
			LOG.error("Error during save sourcing reuest Conclude: " + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("sourcingsummary.during.save.eventconclude", new Object[] { e.getMessage() }, Global.LOCALE));
			constructRequesrtSummerAttribute(sourcingRequest.getId(), model);
			return "sourcingRequestSummary";

		}
		return "redirect:viewSourcingSummary/" + sourcingRequest.getId();
	}
}
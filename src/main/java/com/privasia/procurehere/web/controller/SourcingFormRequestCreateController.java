package com.privasia.procurehere.web.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.*;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.UserDao;
import com.privasia.procurehere.core.entity.BusinessUnit;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.CostCenter;
import com.privasia.procurehere.core.entity.Currency;
import com.privasia.procurehere.core.entity.EventSettings;
import com.privasia.procurehere.core.entity.EventTeamMember;
import com.privasia.procurehere.core.entity.GroupCode;
import com.privasia.procurehere.core.entity.IdSettings;
import com.privasia.procurehere.core.entity.NotificationMessage;
import com.privasia.procurehere.core.entity.ProcurementCategories;
import com.privasia.procurehere.core.entity.ProcurementMethod;
import com.privasia.procurehere.core.entity.RequestAudit;
import com.privasia.procurehere.core.entity.SourcingFormApprovalRequest;
import com.privasia.procurehere.core.entity.SourcingFormApprovalUserRequest;
import com.privasia.procurehere.core.entity.SourcingFormRequest;
import com.privasia.procurehere.core.entity.SourcingFormTeamMember;
import com.privasia.procurehere.core.entity.SourcingFormTemplate;
import com.privasia.procurehere.core.entity.TatReport;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.NotificationType;
import com.privasia.procurehere.core.enums.RequestAuditType;
import com.privasia.procurehere.core.enums.SourcingFormStatus;
import com.privasia.procurehere.core.enums.SourcingStatus;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.enums.TeamMemberType;
import com.privasia.procurehere.core.enums.UserType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.TemplateInactivateException;
import com.privasia.procurehere.core.pojo.CostCenterPojo;
import com.privasia.procurehere.core.pojo.SourcingFormRequestPojo;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.pojo.UserPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.ApprovalService;
import com.privasia.procurehere.service.BusinessUnitService;
import com.privasia.procurehere.service.BuyerSettingsService;
import com.privasia.procurehere.service.CostCenterService;
import com.privasia.procurehere.service.CurrencyService;
import com.privasia.procurehere.service.DashboardNotificationService;
import com.privasia.procurehere.service.EventIdSettingsService;
import com.privasia.procurehere.service.EventSettingsService;
import com.privasia.procurehere.service.GroupCodeService;
import com.privasia.procurehere.service.NotificationService;
import com.privasia.procurehere.service.ProcurementCategoriesService;
import com.privasia.procurehere.service.ProcurementMethodService;
import com.privasia.procurehere.service.RftEventService;
import com.privasia.procurehere.service.SourcingFormRequestService;
import com.privasia.procurehere.service.SourcingTemplateService;
import com.privasia.procurehere.service.TatReportService;
import com.privasia.procurehere.service.UserService;
import com.privasia.procurehere.web.editors.BusinessUnitEditor;
import com.privasia.procurehere.web.editors.CostCenterEditor;
import com.privasia.procurehere.web.editors.CurrencyEditor;
import com.privasia.procurehere.web.editors.GroupCodeEditor;
import com.privasia.procurehere.web.editors.ProcurementCategoriesEditor;
import com.privasia.procurehere.web.editors.ProcurementMethodEditor;
import com.privasia.procurehere.web.editors.SourcingFormApprovalUserRequestEditor;
import com.privasia.procurehere.web.editors.UserEditor;

import freemarker.template.Configuration;

/**
 * @author pooja
 */
@Controller
@RequestMapping("/buyer")
public class SourcingFormRequestCreateController {

	private static final Logger LOG = LogManager.getLogger(Global.PR_LOG);

	@Autowired
	BusinessUnitService businessUnitService;

	@Autowired
	SourcingFormRequestService sourcingFormRequestService;

	@Autowired
	SourcingTemplateService sourcingTemplateService;

	@Autowired
	UserEditor userEditor;

	@Autowired
	SourcingFormApprovalUserRequestEditor sourcingFormApprovalUserRequestEditor;

	@Autowired
	BusinessUnitEditor businessUnitEditor;

	@Autowired
	RftEventService rftEventService;

	@Autowired
	CostCenterEditor costCenterEditor;

	@Autowired
	UserDao userDao;

	@Autowired
	private SourcingFormRequestService requestService;

	@Autowired
	CostCenterService costCenterService;

	@Autowired
	UserService userService;

	@Autowired
	ApprovalService approvalService;

	@Resource
	MessageSource messageSource;

	@Autowired
	NotificationService notificationService;

	@Value("${app.url}")
	String APP_URL;

	@Autowired
	BuyerSettingsService buyerSettingsService;

	@Autowired
	Configuration freemarkerConfiguration;

	@Autowired
	DashboardNotificationService dashboardNotificationService;

	@Autowired
	ServletContext context;

	@Autowired
	CurrencyService currencyService;

	@Autowired
	CurrencyEditor currencyEditor;

	@Autowired
	EventSettingsService eventSettingsService;

	@Autowired
	EventIdSettingsService eventIdSettingsService;

	@Autowired
	ProcurementMethodService procurementMethodService;

	@Autowired
	ProcurementCategoriesService procurementCategoriesService;

	@Autowired
	ProcurementMethodEditor procurementMethodEditor;

	@Autowired
	ProcurementCategoriesEditor procurementCategoriesEditor;

	@Autowired
	GroupCodeService groupCodeService;

	@Autowired
	GroupCodeEditor groupCodeEditor;

	@Autowired
	TatReportService tatReportService;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@InitBinder
	public void InitBinder(WebDataBinder binder, HttpSession session) {
		binder.registerCustomEditor(Currency.class, currencyEditor);
		binder.registerCustomEditor(BusinessUnit.class, businessUnitEditor);
		binder.registerCustomEditor(CostCenter.class, costCenterEditor);
		binder.registerCustomEditor(User.class, userEditor);
		binder.registerCustomEditor(ProcurementMethod.class, procurementMethodEditor);
		binder.registerCustomEditor(ProcurementCategories.class, procurementCategoriesEditor);
		binder.registerCustomEditor(GroupCode.class, groupCodeEditor);
		binder.registerCustomEditor(SourcingFormApprovalUserRequest.class, sourcingFormApprovalUserRequestEditor);
		binder.registerCustomEditor(List.class, "approvalUsersRequest", new CustomCollectionEditor(List.class) {
			protected Object convertElement(Object element) {
				if (element != null) {
					String id = (String) element;
					User user = userService.findUserById(id);
					return new SourcingFormApprovalUserRequest(user);
				}
				return null;
			}
		});

	}

	@ModelAttribute("step")
	public String getStep() {
		return "1";
	}

	@RequestMapping(value = "/createSourcingFormRequest", method = RequestMethod.GET)
	public String createSourcingFormRequest(Model model) {
		LOG.info(" create Sourcing Form controller called ");
		try {
			constructSourcingAndTemplateAttribute(model);
		} catch (Exception e) {
			LOG.error("Error While Fetching Sourcing Form Template :" + e.getMessage(), e);
		}
		return "createSourcingFormRequest";
	}

	@RequestMapping(path = "/copyFromSourcingTemplate", method = RequestMethod.POST)
	public String copyFromSourcingTemplate(Model model, @RequestParam(value = "businessUnitId", required = false) String businessUnitId, @RequestParam(value = "sourcingTemplateId") String sourcingTemplateId, RedirectAttributes redir) {
		SourcingFormRequest sourcingFormRequest = null;
		LOG.info("Copy From Sourcing Template Called with Template id .........." + sourcingTemplateId + " ..@@@@@@@@ businessUnitId. : " + businessUnitId);
		try {

			BusinessUnit businessUnit = null;
			if (StringUtils.isNotBlank(businessUnitId))
				businessUnit = businessUnitService.getBusinessUnitById(businessUnitId);
			SourcingFormTemplate sourcingTemplate = sourcingTemplateService.getSourcingFormbyId(sourcingTemplateId);
			if (sourcingTemplate.getStatus() == SourcingStatus.INACTIVE) {
				throw new TemplateInactivateException("Can not use inactive template ");
			}
			sourcingFormRequest = sourcingFormRequestService.copySourcingTemplate(sourcingTemplateId, SecurityLibrary.getLoggedInUser(), SecurityLibrary.getLoggedInUserTenantId(), businessUnit);
			if (sourcingFormRequest != null) {
				RequestAudit audit = new RequestAudit();
				audit.setAction(RequestAuditType.CREATE);
				audit.setActionBy(SecurityLibrary.getLoggedInUser());
				audit.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
				audit.setReq(sourcingFormRequest);
				audit.setActionDate(new Date());
				audit = requestService.saveAudit(audit);

				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "Sourcing '" + sourcingFormRequest.getFormId() + "' Form request is successfully created", sourcingFormRequest.getCreatedBy().getTenantId(), sourcingFormRequest.getCreatedBy(), new Date(), ModuleType.RFS);
				buyerAuditTrailDao.save(buyerAuditTrail);

				model.addAttribute("reqId", audit.getReq().getId());
				LOG.info("---------------- " + audit.getId());
			}

			SourcingFormTemplate template = sourcingFormRequest.getSourcingForm();
			if (template != null)
				template.setIsTemplateUsed(true);
			template = sourcingTemplateService.updateSourcingTemplate(template);
			LOG.info(template.getIsTemplateUsed());

			try {
				TatReport tatReport = new TatReport();

				tatReport.setRequestGeneratedId(sourcingFormRequest.getId());
				tatReport.setFormId(sourcingFormRequest.getFormId());
				tatReport.setSourcingFormName(sourcingFormRequest.getSourcingFormName());
				tatReport.setBusinessUnit(sourcingFormRequest.getBusinessUnit().getUnitName());
				tatReport.setCostCenter(sourcingFormRequest.getCostCenter() != null ? sourcingFormRequest.getCostCenter().getCostCenter() : "");
				tatReport.setRequestOwner(sourcingFormRequest.getFormOwner() != null ? (sourcingFormRequest.getFormOwner().getName() + " " + sourcingFormRequest.getFormOwner().getLoginId()) : "");
				tatReport.setGroupCode(sourcingFormRequest.getGroupCode() != null ? sourcingFormRequest.getGroupCode().getGroupCode() : (sourcingFormRequest.getGroupCodeOld() != null ? sourcingFormRequest.getGroupCodeOld() : ""));
				tatReport.setCreatedDate(sourcingFormRequest.getCreatedDate());
				tatReport.setProcurementMethod(sourcingFormRequest.getProcurementMethod() != null ? sourcingFormRequest.getProcurementMethod().getProcurementMethod() : "");
				tatReport.setProcurementCategories(sourcingFormRequest.getProcurementCategories() != null ? sourcingFormRequest.getProcurementCategories().getProcurementCategories() : "");
				tatReport.setBaseCurrency(sourcingFormRequest.getCurrency() != null ? sourcingFormRequest.getCurrency().getCurrencyCode() : "");
				tatReport.setRequestStatus(sourcingFormRequest.getStatus());
				tatReport.setTenantId(SecurityLibrary.getLoggedInUserTenantId());
				tatReport.setReqDecimal(sourcingFormRequest.getDecimal());

				tatReportService.saveTatReport(tatReport);
			} catch (Exception e) {
				LOG.info("ERROR  while saving tat report is >>>>>>> " + e.getMessage(), e);
			}

			return "redirect:/buyer/createSourcingFormDetails/" + sourcingFormRequest.getId();
		} catch (TemplateInactivateException e) {
			redir.addFlashAttribute("error", e.getMessage());
		} catch (ApplicationException e) {
			if (e.getMessage().equals("BUSINESS_UNIT_EMPTY")) {
				redir.addFlashAttribute("openModelForTemplateBu", true);
			} else {
				redir.addFlashAttribute("error", e.getMessage());
			}

			redir.addFlashAttribute("sourcingTemplateId", sourcingTemplateId);

			redir.addFlashAttribute("businessUnits", businessUnitService.getPlainActiveBusinessUnitForTenant(SecurityLibrary.getLoggedInUserTenantId()));
		} catch (Exception e) {
			LOG.error("Error While copy sourcing form from  Template :" + e.getMessage(), e);
			// model.addAttribute("error", "Error While copy Request from Template :" + e.getMessage());
			model.addAttribute("error", messageSource.getMessage("error.while.copying.templaterequest", new Object[] { e.getMessage() }, Global.LOCALE));
		}
		return "redirect:/buyer/createSourcingFormRequest";
		// return "createSourcingFormDetails";
		// return "redirect:createSourcingFormDetails/" + sourcingFormRequest.getId();
	}

	@RequestMapping(value = "/searchSourcingTemplate", method = RequestMethod.POST)
	public ResponseEntity<List<SourcingFormTemplate>> searchSourcingTemplate(@RequestParam(required = false, name = "templateName") String searchValue, @RequestParam(required = false, name = "pageNo") String pageNo, Model model) {
		LOG.info("inside the search sourcing template controller");
		HttpHeaders headers = new HttpHeaders();
		List<SourcingFormTemplate> searchResultTemplate = null;
		try {

			searchResultTemplate = sourcingTemplateService.findByTemplateNameForTenant(searchValue, SecurityLibrary.getLoggedInUserTenantId(), pageNo, null);
			LOG.info("*********** LIST SIze " + searchResultTemplate.size() + " >>>>>>>> page no " + pageNo + " ...... searchValue " + searchValue);
			if (CollectionUtil.isNotEmpty(searchResultTemplate)) {
				for (SourcingFormTemplate sourcingFormTemplate : searchResultTemplate) {
					sourcingFormTemplate.setCreatedByName(sourcingFormTemplate.getCreatedBy().getName());
					// commented to resolve issues reported in PH-2035
					// String desc = sourcingFormTemplate.getDescription();
					// if (desc != null) {
					// desc = desc.length() > 50 ? desc.substring(0, 30) + "..." : desc;
					// sourcingFormTemplate.setDescription(desc);
					// }
				}
			}

		} catch (Exception e) {
			LOG.error("Error while search Sourcing Template : " + e.getMessage(), e);
		}
		return new ResponseEntity<List<SourcingFormTemplate>>(searchResultTemplate, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/createSourcingFormDetails/{formId}", method = RequestMethod.GET)
	public String createSourcingFormDetails(Model model, @PathVariable String formId, HttpSession session) {
		LOG.info("create sourcing Form Details Called--------------------------" + formId);
		if (StringUtils.checkString(formId).length() == 0) {
			return "redirect:/400_error";
		}
		SourcingFormRequest sourcingFormRequest = new SourcingFormRequest();
		try {
			sourcingFormRequest = sourcingFormRequestService.loadFormById(formId);
			List<User> assignedApprovalUsers = new ArrayList<User>();
			if (CollectionUtil.isNotEmpty(sourcingFormRequest.getSourcingFormApprovalRequests())) {
				LOG.info("Not Empty approval User--------------------------");
				for (SourcingFormApprovalRequest approver : sourcingFormRequest.getSourcingFormApprovalRequests()) {
					if (CollectionUtil.isNotEmpty(approver.getApprovalUsersRequest())) {
						for (SourcingFormApprovalUserRequest user : approver.getApprovalUsersRequest()) {
							User u = new User(user.getUser().getId(), user.getUser().getLoginId(), user.getUser().getName(), user.getUser().getCommunicationEmail(), user.getUser().getEmailNotifications(), user.getUser().getTenantId(), user.getUser().isDeleted());
							if (!assignedApprovalUsers.contains(u)) {
								assignedApprovalUsers.add(u);
							}
						}
					}
				}
			}
			List<User> userTeamMemberList = new ArrayList<User>();
			List<UserPojo> userList = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", UserType.NORMAL_USER);
			// userService.fetchAllActiveUserForTenantId(SecurityLibrary.getLoggedInUserTenantId());
			List<UserPojo> userListSumm = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", null);

			List<SourcingFormApprovalUserRequest> approvalUserList = new ArrayList<SourcingFormApprovalUserRequest>();
			for (UserPojo user : userListSumm) {
				User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted());
				approvalUserList.add(new SourcingFormApprovalUserRequest(u));
			}

			for (UserPojo user : userList) {
				User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted());
				if (!assignedApprovalUsers.contains(u)) {
					assignedApprovalUsers.add(u);
				}
				if (!userTeamMemberList.contains(u)) {
					userTeamMemberList.add(u);
				}
			}
			model.addAttribute("userList1", assignedApprovalUsers);
			model.addAttribute("userList", approvalUserList);

			LOG.info("Form id of the sourcing request " + sourcingFormRequest.getFormId());
			if (sourcingFormRequest.getStatus() != SourcingFormStatus.DRAFT) {
				return "redirect:/buyer/viewSourcingSummary/" + formId;
			}

			if (sourcingFormRequest.getSourcingForm() != null && sourcingFormRequest.getSourcingForm().getId() != null) {
				SourcingFormTemplate rfsTemplate = sourcingTemplateService.getSourcingFormbyId(sourcingFormRequest.getSourcingForm().getId());
				model.addAttribute("templateFields", rfsTemplate.getFields());
			}

			List<User> assignedTeamMembers = new ArrayList<>();
			if (sourcingFormRequest.getSourcingFormTeamMember() != null) {
				for (SourcingFormTeamMember rfaTeamMember : sourcingFormRequest.getSourcingFormTeamMember()) {
					assignedTeamMembers.add((User) rfaTeamMember.getUser().clone());
				}
			}

			SourcingFormTemplate template = sourcingFormRequest.getSourcingForm();
			if (template != null) {
				template.getReadOnlyTeamMember();
				model.addAttribute("readOnlyTeamMember", template.getReadOnlyTeamMember());
			}

			userTeamMemberList.removeAll(assignedTeamMembers);
			model.addAttribute("baseCurrencyList", currencyService.getlActiveCurrencies());
			model.addAttribute("eventPermissions", requestService.getUserPemissionsForRequest(SecurityLibrary.getLoggedInUser(), formId));
			model.addAttribute("userTeamMemberList", userTeamMemberList);
			model.addAttribute("sourcingFormRequest", sourcingFormRequest);
			model.addAttribute("reqId", sourcingFormRequest.getId());
			model.addAttribute("businessUnitList", businessUnitService.getPlainActiveBusinessUnitForTenant(SecurityLibrary.getLoggedInUserTenantId()));
			model.addAttribute("procurementMethodList", procurementMethodService.getAllActiveProcurementMethod(SecurityLibrary.getLoggedInUserTenantId()));
			model.addAttribute("procurementCategoryList", procurementCategoriesService.getAllActiveProcurementCategory(SecurityLibrary.getLoggedInUserTenantId()));
			Boolean enableCorrelation = buyerSettingsService.isEnableUnitAndCostCorrelationByTenantId(SecurityLibrary.getLoggedInUserTenantId());
			model.addAttribute("isEnableUnitAndCostCorrelation", enableCorrelation);
			if (sourcingFormRequest.getBusinessUnit() != null && enableCorrelation == Boolean.TRUE) {
				if (sourcingFormRequest.getBusinessUnit().getStatus() == Status.ACTIVE) {
					List<CostCenterPojo> assignedCostList = businessUnitService.getCostCentersByBusinessUnitId(sourcingFormRequest.getBusinessUnit().getId(), Status.ACTIVE);
					// List<String> assignedCostId =
					// costCenterService.getCostCenterByBusinessId(sourcingFormRequest.getBusinessUnit().getId());
					// if (CollectionUtil.isNotEmpty(assignedCostId)) {
					// for (String assignedCost : assignedCostId) {
					// CostCenterPojo cost = costCenterService.getCostCenterByCostId(assignedCost);
					// if (cost.getStatus() == Status.ACTIVE) {
					// assignedCostList.add(cost);
					// }
					// }
					// }
					model.addAttribute("costCenterList", assignedCostList);
				} else {
					model.addAttribute("costCenterList", costCenterService.getActiveCostCentersForTenant(SecurityLibrary.getLoggedInUserTenantId()));
				}
			} else {
				model.addAttribute("costCenterList", costCenterService.getActiveCostCentersForTenant(SecurityLibrary.getLoggedInUserTenantId()));
			}

			Boolean enableBUGpCCorrelation = buyerSettingsService.isEnableUnitAndGroupCodeCorrelationByTenantId(SecurityLibrary.getLoggedInUserTenantId());
			model.addAttribute("isEnableUnitAndGpCodeCorr", enableBUGpCCorrelation);
			if (sourcingFormRequest.getBusinessUnit() != null && enableBUGpCCorrelation == Boolean.TRUE) {
				if (sourcingFormRequest.getBusinessUnit().getStatus() == Status.ACTIVE) {
					List<GroupCode> assignedGpCList = new ArrayList<>();
					List<String> assignedGpCIds = groupCodeService.getGroupCodeByBusinessId(sourcingFormRequest.getBusinessUnit().getId());
					if (CollectionUtil.isNotEmpty(assignedGpCIds)) {
						for (String assignedGpC : assignedGpCIds) {
							GroupCode gpC = groupCodeService.getGroupCodeByGCId(assignedGpC);
							if (gpC.getStatus() == Status.ACTIVE) {
								assignedGpCList.add(gpC);
							}
						}
						model.addAttribute("groupCodeList", assignedGpCList);
					}
				} else {
					model.addAttribute("groupCodeList", groupCodeService.fetchAllActiveGroupCodeForTenantID(SecurityLibrary.getLoggedInUserTenantId()));
				}
			} else {
				model.addAttribute("groupCodeList", groupCodeService.fetchAllActiveGroupCodeForTenantID(SecurityLibrary.getLoggedInUserTenantId()));
			}

			IdSettings idSettings = eventIdSettingsService.getIdSettingsByIdTypeForTenanatId(SecurityLibrary.getLoggedInUserTenantId(), "SR");
			model.addAttribute("idSettings", idSettings);

			RequestAudit audit = new RequestAudit();
			model.addAttribute("audit", audit);

		} catch (Exception e) {
			LOG.error("Error" + e.getMessage(), e);
		}
		return "createSourcingFormDetails";
	}

	@RequestMapping(path = "/teamMember/addTeamMemberToList", method = RequestMethod.POST)
	public ResponseEntity<List<EventTeamMember>> addTeamMemberToList(@RequestParam(value = "formId") String formId, @RequestParam(value = "userId") String userId, @RequestParam(value = "memberType") TeamMemberType memberType) {
		HttpHeaders headers = new HttpHeaders();
		LOG.info("addTeamMemberToList:  " + " formId: " + formId + " userId: " + userId);
		List<EventTeamMember> teamMembers = null;
		try {
			if (userId != null) {
				sourcingFormRequestService.addTeamMemberToList(formId, userId, memberType, SecurityLibrary.getLoggedInUser());
				teamMembers = sourcingFormRequestService.getPlainTeamMembersForSourcing(formId);
				sendAddTeamMemberEmailNotificationEmail(formId, userId, memberType);

			} else {
				headers.add("error", "Please Select TeamMember Users");
				LOG.error("Please Select TeamMember Users");
				return new ResponseEntity<List<EventTeamMember>>(null, headers, HttpStatus.BAD_REQUEST);

			}
		} catch (Exception e) {
			LOG.error("Error While adding TeamMember users : " + e.getMessage(), e);
			headers.add("error", "Please Select TeamMember Users");
			return new ResponseEntity<List<EventTeamMember>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		LOG.info(teamMembers.size() + "..................." + teamMembers);
		return new ResponseEntity<List<EventTeamMember>>(teamMembers, headers, HttpStatus.OK);
	}

	private void sendAddTeamMemberEmailNotificationEmail(String formId, String userId, TeamMemberType memberType) {
		try {
			String subject = "You have been Invited as TEAM MEMBER In Sourcing Request";
			String url = APP_URL;
			HashMap<String, Object> map = new HashMap<String, Object>();
			User user = userService.getUsersById(userId);
			SourcingFormRequest sourcingFormRequest = sourcingFormRequestService.loadFormById(formId);

			map.put("userName", user.getName());
			map.put("memberType", memberType.getValue());

			if (memberType == TeamMemberType.Editor)
				map.put("memberMessage", " Allows you to edit the entire draft stage of the Sourcing Request but not finish the Sourcing Request");
			else if (memberType == TeamMemberType.Viewer)
				map.put("memberMessage", "Allows you to view entire draft stage of the Sourcing Request without the ability to edit");
			else
				map.put("memberMessage", "Allows you to perform the same actions as the Sourcing Request Owner.");
			String eventName = StringUtils.checkString(sourcingFormRequest.getSourcingFormName()).length() > 0 ? sourcingFormRequest.getSourcingFormName() : " ";
			map.put("eventName", StringUtils.checkString(sourcingFormRequest.getSourcingFormName()).length() > 0 ? sourcingFormRequest.getSourcingFormName() : " ");
			map.put("createdBy", sourcingFormRequest.getCreatedBy().getName());
			map.put("eventId", sourcingFormRequest.getFormId());
			map.put("eventRefNum", StringUtils.checkString(sourcingFormRequest.getReferanceNumber()).length() > 0 ? sourcingFormRequest.getReferanceNumber() : " ");
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			String timeZone = "GMT+8:00";
			timeZone = getTimeZoneByBuyerSettings(user.getTenantId(), timeZone);
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("date", df.format(new Date()));
			map.put("loginUrl", APP_URL + "/login");
			url = APP_URL + "/buyer/createSourcingFormDetails/" + sourcingFormRequest.getId();
			map.put("appUrl", url);
			String message = FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerConfiguration.getTemplate(Global.TEAM_MEMBER_TEMPLATE_SOURCING), map);
			if(user.getEmailNotifications()) {
				notificationService.sendEmail(user.getCommunicationEmail(), subject, message);
			}
			String notificationMessage = messageSource.getMessage("team.rfs.add", new Object[] { memberType, eventName }, Global.LOCALE);
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

	@RequestMapping(path = "/removeSourcingTeamMemberfromList", method = RequestMethod.POST)
	public ResponseEntity<List<User>> removeSourcingTeamMemberfromList(@RequestParam(value = "formId") String formId, @RequestParam(value = "userId") String userId, Model model) {
		LOG.info("addTeamMemberToList:  " + " formId: " + formId + " userId: " + userId);
		HttpHeaders headers = new HttpHeaders();
		LOG.info("userId Call");
		List<User> teamMembers = null;
		List<User> userList = new ArrayList<User>();
		try {
			SourcingFormTeamMember sourcingFormTeamMember = sourcingFormRequestService.getTeamMemberByUserIdAndFormId(formId, userId);

			if (sourcingFormTeamMember == null) {
				LOG.error("Error While removing Team Member user : " + userId + " Form Id : " + formId);
				headers.add("error", "Error While removing Team Member users. Team member not found");
				return new ResponseEntity<List<User>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}

			sendRemoveTeamMemberEmailNotificationEmail(formId, userId, sourcingFormTeamMember.getTeamMemberType());
			teamMembers = sourcingFormRequestService.removeTeamMemberfromList(formId, userId, sourcingFormTeamMember);
			List<User> activeUserList = userService.fetchAllActiveNormalUsersForTenant(SecurityLibrary.getLoggedInUserTenantId());
			for (User user : activeUserList) {
				try {
					userList.add((User) user.clone());
				} catch (Exception e) {
					LOG.error("Error while cloning user List: " + e.getMessage(), e);
					headers.add("error", "Error While removing Team Member users : " + e.getMessage());
					return new ResponseEntity<List<User>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
			// Remove all users that are already added as editors.
			userList.removeAll(teamMembers);
		} catch (Exception e) {
			LOG.error("Error While removing Team Member users : " + e.getMessage(), e);
			headers.add("error", "Error While removing Team Member users : " + e.getMessage());
			return new ResponseEntity<List<User>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<User>>(userList, headers, HttpStatus.OK);
	}

	private void sendRemoveTeamMemberEmailNotificationEmail(String formId, String userId, TeamMemberType memberType) {
		try {
			String subject = "You have been Removed as TEAM MEMBER from Sourcing Request";
			String url = APP_URL;
			HashMap<String, Object> map = new HashMap<String, Object>();
			User user = userService.getUsersById(userId);
			map.put("userName", user.getName());
			map.put("memberType", memberType.getValue());

			SourcingFormRequest sourcingFormRequest = sourcingFormRequestService.loadFormById(formId);

			if (memberType == TeamMemberType.Editor)
				map.put("memberMessage", " Allows you to edit the entire draft stage of the Sourcing Request but not finish the Sourcing Request");
			else if (memberType == TeamMemberType.Viewer)
				map.put("memberMessage", "Allows you to view entire draft stage of the Sourcing Request without the ability to edit");
			else
				map.put("memberMessage", "Allows you to perform the same actions as the Sourcing Request Owner.");
			String eventName = StringUtils.checkString(sourcingFormRequest.getSourcingFormName()).length() > 0 ? sourcingFormRequest.getSourcingFormName() : " ";
			map.put("eventName", StringUtils.checkString(sourcingFormRequest.getSourcingFormName()).length() > 0 ? sourcingFormRequest.getSourcingFormName() : " ");
			map.put("createdBy", sourcingFormRequest.getCreatedBy().getName());
			map.put("createrEmail", sourcingFormRequest.getCreatedBy().getCommunicationEmail());
			map.put("eventId", sourcingFormRequest.getFormId());
			map.put("eventRefNum", StringUtils.checkString(sourcingFormRequest.getReferanceNumber()).length() > 0 ? sourcingFormRequest.getReferanceNumber() : " ");

			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			String timeZone = "GMT+8:00";
			timeZone = getTimeZoneByBuyerSettings(user.getTenantId(), timeZone);
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("date", df.format(new Date()));
			url = APP_URL + "/buyer/createSourcingFormDetails/" + sourcingFormRequest.getId();
			map.put("loginUrl", APP_URL + "/login");
			map.put("appUrl", url);
			String message = FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerConfiguration.getTemplate(Global.REMOVE_TEAM_MEMBER_TEMPLATE_SOURCING), map);
			if(user.getEmailNotifications()) {
				notificationService.sendEmail(user.getCommunicationEmail(), subject, message);
			}
			String notificationMessage = messageSource.getMessage("team.rfs.remove", new Object[] { memberType, eventName }, Global.LOCALE);
			sendDashboardNotification(user, url, subject, notificationMessage);

		} catch (Exception e) {
			LOG.error("error in sending team member email " + e.getMessage(), e);
		}
		/*
		 * String notificationMessage = messageSource.getMessage("po.create.notification.message", new Object[] {
		 * pr.getName() }, Global.LOCALE); sendDashboardNotification(user, url, subject, notificationMessage,
		 * NotificationType.CREATED_MESSAGE);
		 */

	}

	private void constructSourcingAndTemplateAttribute(Model model) {
		TableDataInput input = new TableDataInput();
		input.setStart(0);
		input.setLength(10);

		List<SourcingFormTemplate> allSourcingTemplate = sourcingTemplateService.findByTemplateNameForTenant(null, SecurityLibrary.getLoggedInUserTenantId(), "0", SecurityLibrary.getLoggedInUser().getId());
		LOG.info("Sourcing Template Size-----------" + allSourcingTemplate.size());

		model.addAttribute("allSourcingTemplate", allSourcingTemplate);
		List<SourcingFormRequest> allSourcingRequest = sourcingFormRequestService.searchSourcingRequestByNameAndRefNum(null, SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId(), "0");
		model.addAttribute("allSourcingRequest", allSourcingRequest);
		EventSettings eventSettings = eventSettingsService.getEventSettingsByTenantId(SecurityLibrary.getLoggedInUserTenantId());
		model.addAttribute("eventSettings", eventSettings);

		long templatesCount = sourcingTemplateService.getTotalTemplateCountForTenant(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId());
		model.addAttribute("templatesCount", templatesCount);
		long requestCount = sourcingFormRequestService.getTotalSourcingRequestCountForTenantId(null, SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId());
		model.addAttribute("requestCount", requestCount);
	}

	@RequestMapping(path = "/saveSourcingFormDetails", method = RequestMethod.POST)
	public ModelAndView saveSourcingFormDetails(@ModelAttribute("sourcingFormRequest") SourcingFormRequest sourcingFormRequest, Model model, BindingResult result, RedirectAttributes redir, boolean goNext, HttpSession session) {
		return saveSourcingRequestDetails(sourcingFormRequest, model, result, redir, true, session);
	}

	public ModelAndView saveSourcingRequestDetails(SourcingFormRequest sourcingFormRequest, Model model, BindingResult result, RedirectAttributes redir, boolean goNext, HttpSession session) {

		SourcingFormRequest persistObj = null;
		model.addAttribute("reqId", session.getAttribute("reqId"));

		try {
			persistObj = sourcingFormRequestService.loadFormById(sourcingFormRequest.getId());
			List<User> assignedApprovalUsers = new ArrayList<User>();
			if (CollectionUtil.isNotEmpty(persistObj.getSourcingFormApprovalRequests())) {
				LOG.info("Not Empty approval User--------------------------");
				for (SourcingFormApprovalRequest approver : persistObj.getSourcingFormApprovalRequests()) {
					if (CollectionUtil.isNotEmpty(approver.getApprovalUsersRequest())) {
						for (SourcingFormApprovalUserRequest user : approver.getApprovalUsersRequest()) {
							User u = new User(user.getUser().getId(), user.getUser().getLoginId(), user.getUser().getName(), user.getUser().getCommunicationEmail(), user.getUser().getEmailNotifications(), user.getUser().getTenantId(), user.getUser().isDeleted());
							if (!assignedApprovalUsers.contains(u)) {
								assignedApprovalUsers.add(u);
							}
						}
					}
				}
			}
			List<User> userTeamMemberList = new ArrayList<User>();
			List<UserPojo> userList = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", UserType.NORMAL_USER);
			List<UserPojo> userListSumm = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", null);

			List<SourcingFormApprovalUserRequest> approvalUserList = new ArrayList<SourcingFormApprovalUserRequest>();
			for (UserPojo user : userListSumm) {
				User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted());
				approvalUserList.add(new SourcingFormApprovalUserRequest(u));
			}

			for (UserPojo user : userList) {
				User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted());
				if (!assignedApprovalUsers.contains(u)) {
					assignedApprovalUsers.add(u);
				}
				if (!userTeamMemberList.contains(u)) {
					userTeamMemberList.add(u);
				}
			}
			model.addAttribute("userList1", assignedApprovalUsers);
			model.addAttribute("userList", approvalUserList);

			if (persistObj.getSourcingForm() != null && persistObj.getSourcingForm().getId() != null) {
				SourcingFormTemplate rfsTemplate = sourcingTemplateService.getSourcingFormbyId(persistObj.getSourcingForm().getId());
				model.addAttribute("templateFields", rfsTemplate.getFields());
			}

			if (CollectionUtil.isNotEmpty(persistObj.getSourcingFormTeamMember())) {
				List<User> assignedTeamMembers = new ArrayList<User>();
				List<SourcingFormTeamMember> userTeamMembers = new ArrayList<SourcingFormTeamMember>();
				for (SourcingFormTeamMember teamMember : persistObj.getSourcingFormTeamMember()) {
					try {
						assignedTeamMembers.add((User) teamMember.getUser().clone());
						userTeamMembers.add(teamMember);
					} catch (Exception e) {
						LOG.error("Error :  " + e.getMessage(), e);
					}
				}
				sourcingFormRequest.setSourcingFormTeamMember(userTeamMembers);
				LOG.info("User member in if condition :  " + userTeamMemberList.size());
				userTeamMemberList.removeAll(assignedTeamMembers);
				model.addAttribute("userTeamMemberList", userTeamMemberList);
			} else {
				model.addAttribute("userTeamMemberList", userTeamMemberList);

			}

			SourcingFormTemplate template = persistObj.getSourcingForm();
			if (template != null) {
				template.getReadOnlyTeamMember();
				model.addAttribute("readOnlyTeamMember", template.getReadOnlyTeamMember());
			}

			model.addAttribute("baseCurrencyList", currencyService.getlActiveCurrencies());
			model.addAttribute("eventPermissions", requestService.getUserPemissionsForRequest(SecurityLibrary.getLoggedInUser(), sourcingFormRequest.getId()));
			model.addAttribute("sourcingFormRequest", sourcingFormRequest);
			// model.addAttribute("costCenterList",
			// costCenterService.getActiveCostCentersForTenant(SecurityLibrary.getLoggedInUserTenantId()));
			model.addAttribute("businessUnitList", businessUnitService.getPlainActiveBusinessUnitForTenant(SecurityLibrary.getLoggedInUserTenantId()));
			model.addAttribute("procurementMethodList", procurementMethodService.getAllActiveProcurementMethod(SecurityLibrary.getLoggedInUserTenantId()));
			model.addAttribute("procurementCategoryList", procurementCategoriesService.getAllActiveProcurementCategory(SecurityLibrary.getLoggedInUserTenantId()));

			Boolean enableCorrelation = buyerSettingsService.isEnableUnitAndCostCorrelationByTenantId(SecurityLibrary.getLoggedInUserTenantId());
			model.addAttribute("isEnableUnitAndCostCorrelation", enableCorrelation);
			if (sourcingFormRequest.getBusinessUnit() != null && enableCorrelation == Boolean.TRUE) {
				if (sourcingFormRequest.getBusinessUnit().getStatus() == Status.ACTIVE) {
					List<CostCenterPojo> assignedCostList = new ArrayList<>();
					List<String> assignedCostId = costCenterService.getCostCenterByBusinessId(sourcingFormRequest.getBusinessUnit().getId());
					if (CollectionUtil.isNotEmpty(assignedCostId)) {
						for (String assignedCost : assignedCostId) {
							CostCenterPojo cost = costCenterService.getCostCenterByCostId(assignedCost);
							if (cost.getStatus() == Status.ACTIVE) {
								assignedCostList.add(cost);
							}
						}
						model.addAttribute("costCenterList", assignedCostList);
					}
				} else {
					model.addAttribute("costCenterList", costCenterService.getActiveCostCentersForTenant(SecurityLibrary.getLoggedInUserTenantId()));
				}
			} else {
				model.addAttribute("costCenterList", costCenterService.getActiveCostCentersForTenant(SecurityLibrary.getLoggedInUserTenantId()));
			}

			Boolean enableBUGpCCorrelation = buyerSettingsService.isEnableUnitAndGroupCodeCorrelationByTenantId(SecurityLibrary.getLoggedInUserTenantId());
			model.addAttribute("isEnableUnitAndGpCodeCorr", enableBUGpCCorrelation);
			if (sourcingFormRequest.getBusinessUnit() != null && enableBUGpCCorrelation == Boolean.TRUE) {
				if (sourcingFormRequest.getBusinessUnit().getStatus() == Status.ACTIVE) {
					List<GroupCode> assignedGpCList = new ArrayList<>();
					List<String> assignedGpCIds = groupCodeService.getGroupCodeByBusinessId(sourcingFormRequest.getBusinessUnit().getId());
					if (CollectionUtil.isNotEmpty(assignedGpCIds)) {
						for (String assignedGpC : assignedGpCIds) {
							GroupCode gpC = groupCodeService.getGroupCodeByGCId(assignedGpC);
							if (gpC.getStatus() == Status.ACTIVE) {
								assignedGpCList.add(gpC);
							}
						}
						model.addAttribute("groupCodeList", assignedGpCList);
					}
				} else {
					model.addAttribute("groupCodeList", groupCodeService.fetchAllActiveGroupCodeForTenantID(SecurityLibrary.getLoggedInUserTenantId()));
				}
			} else {
				model.addAttribute("groupCodeList", groupCodeService.fetchAllActiveGroupCodeForTenantID(SecurityLibrary.getLoggedInUserTenantId()));
			}
		} catch (Exception e) {
			LOG.error("Error" + e.getMessage(), e);
		}

		int sourcingFormApprovalRequests = (sourcingFormRequest.getSourcingFormApprovalRequests() != null ? sourcingFormRequest.getSourcingFormApprovalRequests().size() : 0);

		if (sourcingFormRequest.getApprovalsCount() != null) {
			if (sourcingFormApprovalRequests < sourcingFormRequest.getApprovalsCount()) {
				model.addAttribute("error", "Add Minimum " + sourcingFormRequest.getApprovalsCount() + " Approval Route ");
				return new ModelAndView("createSourcingFormDetails", "sourcingFormRequest", sourcingFormRequest);
			}
		}

		if (result.hasErrors()) {
			LOG.error("Page submitted with Errors.............................");
			List<String> errMessages = new ArrayList<String>();
			for (ObjectError err : result.getAllErrors()) {
				errMessages.add(err.getDefaultMessage());
				LOG.info("ERROR:" + err.getDefaultMessage());
			}
			model.addAttribute("formId", sourcingFormRequest.getFormId());
			return new ModelAndView("createSourcingFormDetails", "sourcingFormRequest", sourcingFormRequest);
		} else {
			LOG.info("page submitted with no errors..........................");
			try {
				if (StringUtils.checkString(sourcingFormRequest.getId()).length() > 0) {
					LOG.info("sourcing Form update:      " + sourcingFormRequest.getId());

					persistObj = sourcingFormRequestService.loadFormById(sourcingFormRequest.getId());
					persistObj.setSourcingFormApprovalRequests(sourcingFormRequest.getSourcingFormApprovalRequests());
					if (CollectionUtil.isNotEmpty(sourcingFormRequest.getSourcingFormTeamMember())) {
						persistObj.setSourcingFormTeamMember(sourcingFormRequest.getSourcingFormTeamMember());
					}
					setSourcingFormApprovalUserRequest(sourcingFormRequest);
					persistObj.setFormType(sourcingFormRequest.getFormType());
					persistObj.setUrgentForm(sourcingFormRequest.getUrgentForm());
					persistObj.setReferanceNumber(sourcingFormRequest.getReferanceNumber());
					persistObj.setDescription(sourcingFormRequest.getDescription());
					persistObj.setFormDetailCompleted(Boolean.TRUE);
					persistObj.setBudgetAmount(sourcingFormRequest.getBudgetAmount());
					persistObj.setHistoricaAmount(sourcingFormRequest.getHistoricaAmount());
					persistObj.setBusinessUnit(sourcingFormRequest.getBusinessUnit());
					persistObj.setCostCenter(sourcingFormRequest.getCostCenter());
					persistObj.setCurrency(sourcingFormRequest.getCurrency());
					persistObj.setMinimumSupplierRating(sourcingFormRequest.getMinimumSupplierRating() != null ? sourcingFormRequest.getMinimumSupplierRating() : null);
					persistObj.setMaximumSupplierRating(sourcingFormRequest.getMaximumSupplierRating() != null ? sourcingFormRequest.getMaximumSupplierRating() : null);
					persistObj.setGroupCode(sourcingFormRequest.getGroupCode() != null ? sourcingFormRequest.getGroupCode() : null);
					persistObj.setGroupCodeOld(null);
					persistObj.setEnableApprovalReminder(sourcingFormRequest.getEnableApprovalReminder());
					persistObj.setReminderAfterHour(sourcingFormRequest.getReminderAfterHour());
					persistObj.setReminderCount(sourcingFormRequest.getReminderCount());
					persistObj.setNotifyEventOwner(sourcingFormRequest.getNotifyEventOwner());
					persistObj.setEstimatedBudget(sourcingFormRequest.getEstimatedBudget());
					persistObj.setProcurementMethod(sourcingFormRequest.getProcurementMethod());
					persistObj.setProcurementCategories(sourcingFormRequest.getProcurementCategories());

					if (sourcingFormRequest.getAddAdditionalApprovals() != null) {
						persistObj.setAddAdditionalApprovals(sourcingFormRequest.getAddAdditionalApprovals());
					}
					sourcingFormRequestService.updateSourcingFormRequest(persistObj);
				}
			} catch (Exception e) {
				LOG.error("Error while storing Sourcing Form details : " + e.getMessage(), e);
				model.addAttribute("errors", "Error while storing Sourcing Form details for : " + sourcingFormRequest.getSourcingFormName() + ", message : " + e.getMessage());
				return new ModelAndView("createSourcingFormDetails", "sourcingFormRequest", sourcingFormRequest);
			}

			try {
				sourcingFormRequest = requestService.getSourcingRequestById(sourcingFormRequest.getId());

				TatReport report = tatReportService.getRfsForTatReportByRfsIDAndFormIdAndTenantId(sourcingFormRequest.getFormId(), SecurityLibrary.getLoggedInUserTenantId(), sourcingFormRequest.getId());

				if (sourcingFormRequest != null && report == null) {
					TatReport	tatReport = new TatReport();
					tatReport.setRequestGeneratedId(sourcingFormRequest.getId());
					tatReport.setFormId(sourcingFormRequest.getFormId());
					tatReport.setSourcingFormName(sourcingFormRequest.getSourcingFormName());
					tatReport.setFormDescription(sourcingFormRequest.getDescription());
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
					tatReport.setRequestStatus(sourcingFormRequest.getStatus());
					tatReport.setReqDecimal(sourcingFormRequest.getDecimal());
					tatReport.setTenantId(sourcingFormRequest.getTenantId());
					tatReportService.updateTatReport(tatReport);
				} else {
					tatReportService.updateTatReportReqDetails(report.getId(), sourcingFormRequest.getId(), sourcingFormRequest.getFormId(), sourcingFormRequest.getSourcingFormName(), sourcingFormRequest.getDescription(), //
							sourcingFormRequest.getBusinessUnit().getUnitName(), sourcingFormRequest.getCostCenter() != null ? sourcingFormRequest.getCostCenter().getCostCenter() : "", //
							sourcingFormRequest.getFormOwner() != null ? (sourcingFormRequest.getFormOwner().getName() + " " + sourcingFormRequest.getFormOwner().getLoginId()) : "", //
							sourcingFormRequest.getGroupCode() != null ? sourcingFormRequest.getGroupCode().getGroupCode() : (sourcingFormRequest.getGroupCodeOld() != null ? sourcingFormRequest.getGroupCodeOld() : ""), //
							sourcingFormRequest.getBudgetAmount(), sourcingFormRequest.getEstimatedBudget(), sourcingFormRequest.getCreatedDate(), //
							sourcingFormRequest.getProcurementMethod() != null ? sourcingFormRequest.getProcurementMethod().getProcurementMethod() : "", //
							sourcingFormRequest.getProcurementCategories() != null ? sourcingFormRequest.getProcurementCategories().getProcurementCategories() : "", //
							sourcingFormRequest.getCurrency() != null ? sourcingFormRequest.getCurrency().getCurrencyCode() : "", //
							sourcingFormRequest.getStatus(), sourcingFormRequest.getDecimal(), sourcingFormRequest.getTenantId());
				}

			} catch (Exception e) {
				LOG.error("Error during saving tat report :" + e.getMessage(), e);
			}

		}
		String next = "";
		if (goNext) {
			next = "redirect:rfsDocument/" + persistObj.getId();
		} else {
			next = "redirect:createSourcingFormDetails" + sourcingFormRequest.getId();
		}
		return new ModelAndView(next);
	}

	private void setSourcingFormApprovalUserRequest(SourcingFormRequest sourcingFormRequest) {
		if (CollectionUtil.isNotEmpty(sourcingFormRequest.getSourcingFormApprovalRequests())) {
			int level = 1;
			for (SourcingFormApprovalRequest app : sourcingFormRequest.getSourcingFormApprovalRequests()) {
				app.setSourcingFormRequest(sourcingFormRequest);
				app.setLevel(level++);
				LOG.info("app Type :" + app.getApprovalType());
				if (app != null && CollectionUtil.isNotEmpty(app.getApprovalUsersRequest())) {
					for (SourcingFormApprovalUserRequest rfsApprovalUser : app.getApprovalUsersRequest()) {
						rfsApprovalUser.setApprovalRequest(app);
						// Deleting user id which is assigned to bind the object with spring form
						rfsApprovalUser.setId(null);
					}
				}
			}
		}
	}

	@RequestMapping(value = "/searchSourcingRequest", method = RequestMethod.POST)
	public ResponseEntity<List<SourcingFormRequest>> searchSourcingRequest(@RequestParam(required = false, name = "searchValue") String searchValue, @RequestParam(required = false, name = "pageNo") String pageNo, Model model) {
		HttpHeaders headers = new HttpHeaders();

		LOG.info("searchValue:" + searchValue + "pageNo :" + pageNo);

		List<SourcingFormRequest> searchResultSourcingRequest = null;
		try {
			searchResultSourcingRequest = sourcingFormRequestService.searchSourcingRequestByNameAndRefNum(searchValue, SecurityLibrary.getLoggedInUserTenantId(), (SecurityLibrary.ifAnyGranted("ROLE_ADMIN") ? null : SecurityLibrary.getLoggedInUser().getId()), pageNo);
			if (CollectionUtil.isNotEmpty(searchResultSourcingRequest)) {
				for (SourcingFormRequest sourcingFormRequest : searchResultSourcingRequest) {
					sourcingFormRequest.setCreatedByName(sourcingFormRequest.getCreatedBy().getName());
					sourcingFormRequest.setCostCenter(null);
					sourcingFormRequest.setCurrency(null);
				}
			}
		} catch (Exception e) {
			LOG.error("Error while Search Sourcing Request from previous:" + e.getMessage(), e);
		}

		return new ResponseEntity<List<SourcingFormRequest>>(searchResultSourcingRequest, headers, HttpStatus.OK);

	}

	@ModelAttribute("sourcingFormStatusList")
	public List<SourcingFormStatus> getPrStatusList() {
		List<SourcingFormStatus> sourcingFormStatusList = Arrays.asList(SourcingFormStatus.DRAFT, SourcingFormStatus.FINISHED, SourcingFormStatus.CANCELED, SourcingFormStatus.PENDING, SourcingFormStatus.APPROVED, SourcingFormStatus.CONCLUDED);
		return sourcingFormStatusList;
	}

	@RequestMapping(path = "/sourcingForm", method = RequestMethod.GET)
	public String sourcingFormList(Model model) {
		return "sourcingFormList";
	}

	@RequestMapping(value = "/sourcingFormList", method = RequestMethod.GET)
	public ResponseEntity<TableData<SourcingFormRequestPojo>> sourcingFormList(HttpSession session, TableDataInput input, @RequestParam(required = false) String dateTimeRange) {
		TableData<SourcingFormRequestPojo> data = null;
		try {
			LOG.info("dateTimeRange :" + dateTimeRange);
			final User user = SecurityLibrary.getLoggedInUser();
            final String userId = SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId();
			Date startDate = null;
			Date endDate = null;

			if (StringUtils.checkString(dateTimeRange).length() > 0) {
				TimeZone timeZone = TimeZone.getDefault();
				String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
				if (strTimeZone != null) {
					timeZone = TimeZone.getTimeZone(strTimeZone);
				}

				String dateTimeArr[] = dateTimeRange.split("-");
				DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
				formatter.setTimeZone(timeZone);
				startDate = (Date) formatter.parse(dateTimeArr[0]);
				endDate = (Date) formatter.parse(dateTimeArr[1]);
				LOG.info("Start date : " + startDate + " End Date : " + endDate);
			}
			// List<SourcingFormRequestPojo> list =
			// sourcingFormRequestService.getAllSourcingRequestList(SecurityLibrary.getLoggedInUser(),
			// SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null :
			// SecurityLibrary.getLoggedInUser().getId(), input, startDate, endDate);
			// data = new TableData<SourcingFormRequestPojo>(list);
			// // long filterTotalCount =
			// // sourcingFormRequestService.getAllSourcingFormRequestFilterList(SecurityLibrary.getLoggedInUser(),
			// // SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null :
			// // SecurityLibrary.getLoggedInUser().getId(), input, startDate, endDate);
			//
			// long totalCount =
			// sourcingFormRequestService.getAllSourcingRequestListCount(SecurityLibrary.getLoggedInUser(),
			// SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null :
			// SecurityLibrary.getLoggedInUser().getId(), startDate, endDate);
			// data.setDraw(input.getDraw());
			// data.setRecordsTotal(totalCount);
			// data.setRecordsFiltered(totalCount);
			Date finalStartDate = startDate;
			Date finalEndDate = endDate;

			//List<String> assignedBizUnitId = sourcingTemplateService.getSourcingTemplateByUserIdAndTemplateId(userId);
			List<String> bizUnitIds = businessUnitService.getBusinessUnitIdByUserId(userId);
			LOG.info("RETRIEVE UNIT ID??? >>>>>>>>>>>>>>>   "+bizUnitIds);

			// Create a new thread pool with a fixed number of threads
			ExecutorService executor = Executors.newFixedThreadPool(3);

			// Define the task as a Callable
			Callable<TableData> task1 = new Callable<TableData>() {
				public TableData call() throws Exception {
					// Task to be performed in the separate thread
					//TODO
					//TableData<SourcingFormRequestPojo> tableData = new TableData<SourcingFormRequestPojo>(sourcingFormRequestService.getAllSourcingRequestForList(user, userId, input, finalStartDate, finalEndDate));
					TableData<SourcingFormRequestPojo> tableData = new TableData<SourcingFormRequestPojo>(sourcingFormRequestService.getAllSourcingRequestListForAssignedBizUnit(user, userId, input, finalStartDate, finalEndDate,bizUnitIds));
					return tableData;
				}
			};
			Callable<Long> task2 = new Callable<Long>() {
				public Long call() throws Exception {
					LOG.info("count 1?");
					// Task to be performed in the separate thread
					//long filterTotalCount = sourcingFormRequestService.getFilteredCountOfSourcingRequestForList(user, userId, input, finalStartDate, finalEndDate);
					long filterTotalCount = sourcingFormRequestService.getFilteredCountOfSourcingRequestListBizUnit(user, userId, input, finalStartDate, finalEndDate,bizUnitIds);
					LOG.info("filterTotalCount "+filterTotalCount);
					return filterTotalCount;
				}
			};
				Callable<Long> task3 = new Callable<Long>() {
					public Long call() throws Exception {
						LOG.info("count 2?");

						// Task to be performed in the separate thread
						//long totalCount = sourcingFormRequestService.getTotalSourcingRequestCountForList(user, userId, finalStartDate, finalEndDate);
						//long totalCount = sourcingFormRequestService.getTotalSourcingRequestCountListBizUnit(user, userId, finalStartDate, finalEndDate,bizUnitIds);
						long totalCount = sourcingFormRequestService.getFilteredCountOfSourcingRequestListBizUnit(user, userId, input, finalStartDate, finalEndDate,bizUnitIds);

						LOG.info("totalCount  "+totalCount);
						return totalCount;
					}
				};

			// Submit the task to the thread pool
			Future<TableData> futureResult1 = executor.submit(task1);
			Future<Long> futureResult2 = executor.submit(task2);
			Future<Long> futureResult3 = executor.submit(task3);

			// Wait for the task to finish and retrieve the result
			try {
				data = futureResult1.get();
				data.setRecordsFiltered(futureResult2.get());
				data.setRecordsTotal(futureResult3.get());
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}

			// Shutdown the executor
			executor.shutdown();
			// LOG.info(" totalCount :" + totalCount);
		} catch (Exception e) {
			LOG.error("Error while loading draft list : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<SourcingFormRequestPojo>>(data, HttpStatus.OK);
	}

	@RequestMapping(path = "/ExportSourcingReport", method = RequestMethod.POST)
	public void downloadSourcingReport(HttpSession session, HttpServletRequest request, HttpServletResponse response, @ModelAttribute("sourcingFormRequestPojo") SourcingFormRequestPojo sourcingFormRequestPojo, boolean select_all, @RequestParam String dateTimeRange) throws IOException {
		try {
			String EventArr[] = null;
			if (StringUtils.checkString(sourcingFormRequestPojo.getEventIds()).length() > 0) {
				EventArr = sourcingFormRequestPojo.getEventIds().split(",");
			}

			LOG.info("dateTimeRange :" + dateTimeRange);
			Date startDate = null;
			Date endDate = null;

			if (StringUtils.checkString(dateTimeRange).length() > 0) {
				TimeZone timeZone = TimeZone.getDefault();
				String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
				if (strTimeZone != null) {
					timeZone = TimeZone.getTimeZone(strTimeZone);
				}

				String dateTimeArr[] = dateTimeRange.split("-");
				DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
				formatter.setTimeZone(timeZone);
				startDate = (Date) formatter.parse(dateTimeArr[0]);
				endDate = (Date) formatter.parse(dateTimeArr[1]);
				LOG.info("Start date : " + startDate + " End Date : " + endDate);
			}

			XSSFWorkbook workbook = new XSSFWorkbook();
			String downloadFolder = context.getRealPath("/WEB-INF/");
			String fileName = "Sourcing Request Report.xlsx";
			Path file = Paths.get(downloadFolder, fileName);
			LOG.info("File Path ::" + file);

			sourcingReportDownloader(workbook, EventArr, session, sourcingFormRequestPojo, select_all, startDate, endDate);

			// Save Excel File
			FileOutputStream out = new FileOutputStream(downloadFolder + "/" + fileName);
			workbook.write(out);
			out.close();
			LOG.info("Successfully written in Excel");
			if (Files.exists(file)) {
				response.setContentType("application/vnd.ms-excel");
				response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
				try {
					Files.copy(file, response.getOutputStream());
					response.getOutputStream().flush();
				} catch (IOException e) {
					LOG.error("Error :- " + e.getMessage());
				}
			}

		} catch (Exception e) {
			LOG.error("Error while downloading Sourcing Report List :: " + e.getMessage(), e);
		}
	}

	protected void sourcingReportDownloader(XSSFWorkbook workbook, String[] eventIds, HttpSession session, SourcingFormRequestPojo sourcingFormRequestPojo, boolean select_all, Date startDate, Date endDate) throws IOException, Exception {

		XSSFSheet sheet = workbook.createSheet("Sourcing Report List");
		int r = 1;
		buildHeader(workbook, sheet);

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		SimpleDateFormat deliveryDate = new SimpleDateFormat("dd/MM/yyyy ");
		if (session != null && session.getAttribute(Global.SESSION_TIME_ZONE_KEY) != null) {
			sdf.setTimeZone(TimeZone.getTimeZone((String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY)));
			deliveryDate.setTimeZone(TimeZone.getTimeZone((String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY)));
		} else {
			sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
			deliveryDate.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));

		}

		List<SourcingFormRequestPojo> eventList = getSearchSourcingDetails(eventIds, sourcingFormRequestPojo, select_all, startDate, endDate, sdf);

		if (CollectionUtil.isNotEmpty(eventList)) {
			for (SourcingFormRequestPojo sourcingReport : eventList) {
				Row row = sheet.createRow(r++);
				int cellNum = 0;
				row.createCell(cellNum++).setCellValue(sourcingReport.getTemplateName() != null ? sourcingReport.getTemplateName() : "");
				row.createCell(cellNum++).setCellValue(sourcingReport.getFormId() != null ? sourcingReport.getFormId() : "");
				row.createCell(cellNum++).setCellValue(sourcingReport.getReferanceNumber() != null ? sourcingReport.getReferanceNumber() : "");
				row.createCell(cellNum++).setCellValue(sourcingReport.getSourcingFormName() != null ? sourcingReport.getSourcingFormName() : "");
				row.createCell(cellNum++).setCellValue(sourcingReport.getDescription() != null ? sourcingReport.getDescription() : "");
				row.createCell(cellNum++).setCellValue(sourcingReport.getFormOwner() != null ? sourcingReport.getFormOwner() : "");
				row.createCell(cellNum++).setCellValue(sourcingReport.getCreatedDate() != null ? sdf.format(sourcingReport.getCreatedDate()) : "");
				row.createCell(cellNum++).setCellValue(sourcingReport.getSubmittedDate() != null ? sdf.format(sourcingReport.getSubmittedDate()) : "");
				row.createCell(cellNum++).setCellValue(sourcingReport.getApprovedDate() != null ? sdf.format(sourcingReport.getApprovedDate()) : "");
				BigDecimal approvalDays = BigDecimal.ZERO;
				if (sourcingReport.getApprovalDaysHours() != null) {
					// approvalDays = sourcingReport.getApprovalDaysHours() / 24;
					approvalDays = new BigDecimal((double) sourcingReport.getApprovalDaysHours() / 24);
					approvalDays = approvalDays.setScale(2, RoundingMode.HALF_EVEN);
				}
				row.createCell(cellNum++).setCellValue(sourcingReport.getApprovalDaysHours() != null ? approvalDays.toString() : "");
				row.createCell(cellNum++).setCellValue(sourcingReport.getApprovalTotalLevels() != null ? sourcingReport.getApprovalTotalLevels().toString() : "");
				row.createCell(cellNum++).setCellValue(sourcingReport.getApprovalTotalUsers() != null ? sourcingReport.getApprovalTotalUsers().toString() : "");
				row.createCell(cellNum++).setCellValue(sourcingReport.getBusinessUnit() != null ? sourcingReport.getBusinessUnit() : "");
				row.createCell(cellNum++).setCellValue(sourcingReport.getCostCenter() != null ? sourcingReport.getCostCenter() : "");
				row.createCell(cellNum++).setCellValue(sourcingReport.getBaseCurrency() != null ? sourcingReport.getBaseCurrency() : "");
				row.createCell(cellNum++).setCellValue(sourcingReport.getAvailableBudget() != null ? sourcingReport.getAvailableBudget().toString() : "");
				row.createCell(cellNum++).setCellValue(sourcingReport.getEstimatedBudget() != null ? sourcingReport.getEstimatedBudget().toString() : "");
				row.createCell(cellNum++).setCellValue(sourcingReport.getGroupCode() != null ? sourcingReport.getGroupCode() : "");
				row.createCell(cellNum++).setCellValue(sourcingReport.getStat() != null ? sourcingReport.getStat() : "");

			}
		}

		for (int k = 0; k < 13; k++) {
			sheet.autoSizeColumn(k, true);
		}

	}

	private void buildHeader(XSSFWorkbook workbook, XSSFSheet sheet) {
		Row rowHeading = sheet.createRow(0);
		// Style of Heading Cells
		CellStyle styleHeading = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		styleHeading.setFont(font);
		styleHeading.setVerticalAlignment(CellStyle.ALIGN_CENTER);

		int i = 0;
		Cell cell = null;
		for (String column : Global.ALL_SOURCING_REPORT_EXCEL_COLUMNS_CUR) {
			cell = rowHeading.createCell(i++);
			cell.setCellValue(column);
			cell.setCellStyle(styleHeading);
		}
	}

	private List<SourcingFormRequestPojo> getSearchSourcingDetails(String[] eventIds, SourcingFormRequestPojo sourcingFormRequestPojo, boolean select_all, Date startDate, Date endDate, SimpleDateFormat sdf) {
		return sourcingFormRequestService.getAllExcelSearchSourcingReportForBuyer(SecurityLibrary.getLoggedInUserTenantId(), eventIds, sourcingFormRequestPojo, select_all, startDate, endDate, sdf);
	}

	@RequestMapping(path = "/copyFromSourcingFormRequest", method = RequestMethod.POST)
	public String copyFromSourcingFormRequest(@RequestParam(value = "formId") String reqeustId, Model model, RedirectAttributes redir) {
		LOG.info("Copy From Sourcing Request Called with form id ................." + reqeustId);
		SourcingFormRequest sourcingFormRequest = null;
		try {
			if (sourcingFormRequestService.checkSourcingRequestStatus(reqeustId)) {
				constructSourcingAndTemplateAttribute(model);
				// model.addAttribute("error", "Cannot copy Sourcing Form Request being used by this Sourcing Form
				// template is inactive");
				model.addAttribute("error", messageSource.getMessage("cannot.copy.sourcingform.inactive", new Object[] {}, Global.LOCALE));
				return "createSourcingFormRequest";
			}
			sourcingFormRequest = sourcingFormRequestService.copyFromSourcingRequest(reqeustId, SecurityLibrary.getLoggedInUser());

			try {
				SourcingFormRequest request = requestService.getSourcingRequestById(sourcingFormRequest.getId());

				TatReport tatReport = new TatReport();

				tatReport.setRequestGeneratedId(request.getId());
				tatReport.setFormId(request.getFormId());
				tatReport.setSourcingFormName(request.getSourcingFormName());
				tatReport.setBusinessUnit(request.getBusinessUnit().getUnitName());
				tatReport.setCostCenter(request.getCostCenter() != null ? request.getCostCenter().getCostCenter() : "");
				tatReport.setRequestOwner(request.getFormOwner() != null ? (request.getFormOwner().getName() + " " + request.getFormOwner().getLoginId()) : "");
				tatReport.setGroupCode(request.getGroupCode() != null ? request.getGroupCode().getGroupCode() : (request.getGroupCodeOld() != null ? request.getGroupCodeOld() : ""));
				tatReport.setCreatedDate(request.getCreatedDate());
				tatReport.setProcurementMethod(request.getProcurementMethod() != null ? request.getProcurementMethod().getProcurementMethod() : "");
				tatReport.setProcurementCategories(request.getProcurementCategories() != null ? request.getProcurementCategories().getProcurementCategories() : "");
				tatReport.setBaseCurrency(request.getCurrency() != null ? request.getCurrency().getCurrencyCode() : "");
				tatReport.setRequestStatus(request.getStatus());
				tatReport.setTenantId(SecurityLibrary.getLoggedInUserTenantId());
				tatReport.setReqDecimal(request.getDecimal());

				tatReportService.saveTatReport(tatReport);
			} catch (Exception e) {
				LOG.info("ERROR while saving tat report is  >>>>>>> " + e.getMessage(), e);
			}
		} catch (ApplicationException e) {
			if (e.getMessage().equals("BUSINESS_UNIT_EMPTY")) {
				redir.addFlashAttribute("openModelBu", true);
			} else {
				redir.addFlashAttribute("error", e.getMessage());
			}
			redir.addFlashAttribute("formId", reqeustId);
			redir.addFlashAttribute("businessUnits", businessUnitService.getPlainActiveBusinessUnitForTenant(SecurityLibrary.getLoggedInUserTenantId()));
			return "redirect:/buyer/createSourcingFormRequest";
		} catch (Exception e) {
			LOG.error("Error while copy from previous Sourcing Request :" + e.getMessage(), e);
			constructSourcingAndTemplateAttribute(model);
			// model.addAttribute("error", "Error while copy from previous Sourcing Form Request:" + e.getMessage());
			model.addAttribute("error", messageSource.getMessage("error.copying.previous.sourcingform", new Object[] { e.getMessage() }, Global.LOCALE));
			return "createSourcingFormRequest";
		}
		return "redirect:createSourcingFormDetails/" + sourcingFormRequest.getId();
	}

	@RequestMapping(path = "/saveAdditionalApproval/{rfsId}", method = RequestMethod.POST)
	public String saveAdditionalApproval(@ModelAttribute("sourcingAdditionalApprovals") SourcingFormRequest sourcingAdditionalApprovals, @PathVariable String rfsId, RedirectAttributes redir) {
		try {

			sourcingFormRequestService.addAdditionalApprover(sourcingAdditionalApprovals, rfsId, SecurityLibrary.getLoggedInUser());
		} catch (Exception e) {
			LOG.error("Error while storing Saving Sourcing Approval details : " + e.getMessage(), e);
			redir.addFlashAttribute("errors", "Error while Saving Sourcing Approval details for : " + rfsId + ", message : " + e.getMessage());
			return "redirect:/buyer/viewSourcingSummary/" + rfsId;
		}
		redir.addFlashAttribute("success", "Additional Approval save successfully");
		return "redirect:/buyer/viewSourcingSummary/" + rfsId;
	}

	@RequestMapping(path = "/finishAdditionalApproval/{rfsId}", method = RequestMethod.POST)
	public String finishAdditionalApproval(@ModelAttribute("sourcingAdditionalApprovals") SourcingFormRequest sourcingAdditionalApprovals, @PathVariable String rfsId, RedirectAttributes redir) {
		try {
			sourcingFormRequestService.finishAdditionalApprover(sourcingAdditionalApprovals, rfsId, SecurityLibrary.getLoggedInUser());
		} catch (ApplicationException e) {
			LOG.error("Error while storing Saving Sourcing Approval details : " + e.getMessage(), e);
			redir.addFlashAttribute("errors", e.getMessage());
			return "redirect:/buyer/viewSourcingSummary/" + rfsId;
		} catch (Exception e) {
			LOG.error("Error while storing Saving Sourcing Approval details : " + e.getMessage(), e);
			redir.addFlashAttribute("errors", "Error while Saving Sourcing Approval details :" + e.getMessage());
			return "redirect:/buyer/viewSourcingSummary/" + rfsId;
		}
		redir.addFlashAttribute("success", "Additional Approval added successfully");
		return "redirect:/buyer/viewSourcingSummary/" + rfsId;
	}

	@RequestMapping(path = "/sourcingFormRequest/updateSourcingFormRequestApproval", method = RequestMethod.POST)
	public String updateEventApproval(@ModelAttribute SourcingFormRequest sourcingFormRequest, RedirectAttributes redir, HttpSession session) {
		try {
			sourcingFormRequest = sourcingFormRequestService.updateSourcingFormRequestApproval(sourcingFormRequest, SecurityLibrary.getLoggedInUser());
			// approvalService.doApproval(sourcingFormRequest, session, SecurityLibrary.getLoggedInUser());
			redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.approval.updated", new Object[] {}, Global.LOCALE));
		} catch (Exception e) {
			LOG.info("Error while Updating Approval :" + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.updating.approval", new Object[] { e.getMessage() }, Global.LOCALE));
		}
		return "redirect:/buyer/viewSourcingSummary/" + sourcingFormRequest.getId();
	}

	@RequestMapping(path = "/exportSourcingCsvReport", method = RequestMethod.POST)
	public void downloadSourcingCsvReport(HttpSession session, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redir, @ModelAttribute("sourcingFormRequestPojo") SourcingFormRequestPojo sourcingFormRequestPojo, boolean select_all, @RequestParam String dateTimeRange) throws IOException {
		try {
			File file = File.createTempFile("Sourcing Request Report-", ".csv");
			LOG.info("Temp file : " + file.getAbsolutePath());

			String eventArr[] = null;
			if (StringUtils.checkString(sourcingFormRequestPojo.getEventIds()).length() > 0) {
				eventArr = sourcingFormRequestPojo.getEventIds().split(",");
			}

			LOG.info("dateTimeRange :" + dateTimeRange);
			Date startDate = null;
			Date endDate = null;

			if (StringUtils.checkString(dateTimeRange).length() > 0) {
				TimeZone timeZone = TimeZone.getDefault();
				String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
				if (strTimeZone != null) {
					timeZone = TimeZone.getTimeZone(strTimeZone);
				}

				String dateTimeArr[] = dateTimeRange.split("-");
				DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
				formatter.setTimeZone(timeZone);
				startDate = (Date) formatter.parse(dateTimeArr[0]);
				endDate = (Date) formatter.parse(dateTimeArr[1]);
				LOG.info("Start date : " + startDate + " End Date : " + endDate);
			}

			sourcingFormRequestService.downloadCsvFileForSourcing(response, file, eventArr, sourcingFormRequestPojo, startDate, endDate, select_all, SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), session);
			//ph 4105 not sure needed or not
			//sourcingFormRequestService.downloadCsvFileForSourcingBizUnit(response, file, eventArr, sourcingFormRequestPojo, startDate, endDate, select_all, SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), session);

			try {
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Sourcing Request Report is successfully downloaded", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFS);
				buyerAuditTrailDao.save(buyerAuditTrail);
			} catch (Exception e) {
				LOG.info("Error to create audit trails message");
			}

			// Send to UI
			response.setContentType("text/csv");
			response.addHeader("Content-Disposition", "attachment; filename=Sourcing Request Report.csv");
			response.setContentLengthLong(file.length());
			Files.copy(file.toPath(), response.getOutputStream());
			response.getOutputStream().flush();

		} catch (Exception e) {
			LOG.error("Error while downloading csv file : " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("product.error.download.csv", new Object[] { e.getMessage() }, Global.LOCALE));

		}
	}

	@RequestMapping(value = "/sourcingTemplatePagination", method = RequestMethod.POST)
	public ResponseEntity<TableData<SourcingFormTemplate>> sourcingTemplatePagination(@RequestParam(required = false, name = "searchValue") String searchValue, @RequestParam(required = false, name = "pageNo") Integer pageNo, @RequestParam(required = false, name = "pageLength") Integer pageLength) {
		LOG.info("Search sourcing template controller");
		HttpHeaders headers = new HttpHeaders();
		List<SourcingFormTemplate> searchResultTemplate = null;
		TableData<SourcingFormTemplate> data = null;
		try {
			searchResultTemplate = sourcingTemplateService.findTemplatesBySearchValForTenant(searchValue, pageNo, pageLength, SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId());
			LOG.info("*********** LIST SIze " + searchResultTemplate.size() + " >>>>>>>> page no " + pageNo + " ...... searchValue " + searchValue);
			if (CollectionUtil.isNotEmpty(searchResultTemplate)) {
				for (SourcingFormTemplate sourcingFormTemplate : searchResultTemplate) {
					sourcingFormTemplate.setCreatedByName(sourcingFormTemplate.getCreatedBy().getName());
				}
			}
			long templatesCount = sourcingTemplateService.findTemplatesCountBySearchValForTenant(searchValue, SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId());
			LOG.info("..................... templatesCount " + templatesCount);

			data = new TableData<SourcingFormTemplate>(searchResultTemplate);
			data.setRecordsTotal(templatesCount);

		} catch (Exception e) {
			LOG.error("Error while search Sourcing Template : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<SourcingFormTemplate>>(data, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/sourcingRequestPagination", method = RequestMethod.POST)
	public ResponseEntity<TableData<SourcingFormRequest>> searchSourcingRequestForPagination(@RequestParam(required = false, name = "searchValue") String searchValue, @RequestParam(required = false, name = "pageNo") Integer pageNo, @RequestParam(required = false, name = "pageLength") Integer pageLength) {
		HttpHeaders headers = new HttpHeaders();
		LOG.info("searchValue:" + searchValue + "pageNo :" + pageNo);

		List<SourcingFormRequest> searchResultSourcingRequest = null;
		TableData<SourcingFormRequest> data = null;
		try {
			searchResultSourcingRequest = sourcingFormRequestService.searchSourcingRequestByNameAndRefNumForTenantId(searchValue, pageNo, pageLength, SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId());

			long requestCount = sourcingFormRequestService.getTotalSourcingRequestCountForTenantId(searchValue, SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId());

			data = new TableData<SourcingFormRequest>(searchResultSourcingRequest);
			data.setRecordsTotal(requestCount);
		} catch (Exception e) {
			LOG.error("Error while Search Sourcing Request from previous:" + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<SourcingFormRequest>>(data, headers, HttpStatus.OK);
	}

}

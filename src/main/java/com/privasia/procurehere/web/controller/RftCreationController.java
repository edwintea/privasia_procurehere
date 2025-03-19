package com.privasia.procurehere.web.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.privasia.procurehere.service.RftSorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.privasia.procurehere.core.entity.BusinessUnit;
import com.privasia.procurehere.core.entity.BuyerSettings;
import com.privasia.procurehere.core.entity.ErpSetup;
import com.privasia.procurehere.core.entity.EventTeamMember;
import com.privasia.procurehere.core.entity.IndustryCategory;
import com.privasia.procurehere.core.entity.ProcurementCategories;
import com.privasia.procurehere.core.entity.ProcurementMethod;
import com.privasia.procurehere.core.entity.RftApprovalUser;
import com.privasia.procurehere.core.entity.RftEvaluationConclusionUser;
import com.privasia.procurehere.core.entity.RftEvent;
import com.privasia.procurehere.core.entity.RftEventApproval;
import com.privasia.procurehere.core.entity.RftEventContact;
import com.privasia.procurehere.core.entity.RftEventMeeting;
import com.privasia.procurehere.core.entity.RftEventSuspensionApproval;
import com.privasia.procurehere.core.entity.RftReminder;
import com.privasia.procurehere.core.entity.RftSuspensionApprovalUser;
import com.privasia.procurehere.core.entity.RftTeamMember;
import com.privasia.procurehere.core.entity.RftUnMaskedUser;
import com.privasia.procurehere.core.entity.RfxTemplate;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.AwardStatus;
import com.privasia.procurehere.core.enums.DeclarationType;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.EventVisibilityType;
import com.privasia.procurehere.core.enums.IntervalType;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.TeamMemberType;
import com.privasia.procurehere.core.enums.UserType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.SubscriptionException;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.TatReportPojo;
import com.privasia.procurehere.core.pojo.UserPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.DateUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.ErpSetupService;
import com.privasia.procurehere.service.ProcurementCategoriesService;
import com.privasia.procurehere.service.ProcurementMethodService;
import com.privasia.procurehere.service.RftBqService;
import com.privasia.procurehere.service.RftCqService;
import com.privasia.procurehere.service.RftDocumentService;
import com.privasia.procurehere.service.RftEventSupplierService;
import com.privasia.procurehere.service.RftMeetingService;
import com.privasia.procurehere.service.UserService;
import com.privasia.procurehere.web.editors.ProcurementCategoriesEditor;
import com.privasia.procurehere.web.editors.ProcurementMethodEditor;
import com.privasia.procurehere.web.editors.RftApprovalEditor;
import com.privasia.procurehere.web.editors.RftSuspensionApprovalEditor;

@Controller
@RequestMapping("/buyer/RFT")
public class RftCreationController extends EventCreationBase {

	@Autowired
	RftCqService rftCqService;

	@Autowired
	RftBqService rftBqService;

	@Autowired
	RftDocumentService rftDocumentService;

	@Autowired
	RftMeetingService rftMeetingService;

	@Autowired
	UserService userService;

	@Autowired
	RftApprovalEditor rftApprovalEditor;

	@Autowired
	RftEventSupplierService rftEventSupplierService;

	@Autowired
	ErpSetupService erpSetupService;

	@Autowired
	ProcurementMethodService procurementMethodService;

	@Autowired
	RftSuspensionApprovalEditor rftSuspensionApprovalEditor;

	@Autowired
	ProcurementCategoriesService procurementCategoriesService;

	@Autowired
	ProcurementMethodEditor procurementMethodEditor;

	@Autowired
	ProcurementCategoriesEditor procurementCategoriesEditor;

	@Autowired
	RftSorService rftSorService;

	@InitBinder
	public void initBinder(WebDataBinder binder, HttpSession session) {
		super.initBinder(binder, session);
		binder.registerCustomEditor(RftApprovalUser.class, rftApprovalEditor);
		binder.registerCustomEditor(ProcurementMethod.class, procurementMethodEditor);
		binder.registerCustomEditor(ProcurementCategories.class, procurementCategoriesEditor);
		binder.registerCustomEditor(RftSuspensionApprovalUser.class, rftSuspensionApprovalEditor);

	}

	public RftCreationController() {
		super(RfxTypes.RFT);
	}

	@RequestMapping(path = "/createRftEvent", method = RequestMethod.GET)
	public String createRftEvent(Model model, RedirectAttributes redir) {

		try {
			RftEvent rftEvent = new RftEvent();
			rftEvent.setCreatedBy(SecurityLibrary.getLoggedInUser());
			rftEvent.setCreatedDate(new Date());
			rftEvent.setBillOfQuantity(Boolean.TRUE);
			ErpSetup erpSetup = erpSetupService.getErpConfigBytenantId(SecurityLibrary.getLoggedInUserTenantId());
			if (erpSetup != null) {
				LOG.info("--------erp flag set for event-----------");
				rftEvent.setErpEnable(erpSetup.getIsErpEnable());
			} else {
				rftEvent.setErpEnable(false);
			}

			rftEvent.setEventId(eventIdSettingDao.generateEventIdByBusinessUnit(SecurityLibrary.getLoggedInUserTenantId(), "RFT", null));
			rftEvent.setStatus(EventStatus.DRAFT);
			rftEvent.setAwardStatus(null);
			rftEvent = rftEventService.saveRftEvent(rftEvent, SecurityLibrary.getLoggedInUser());
			rftEventService.setDefaultEventContactDetail(SecurityLibrary.getLoggedInUser().getId(), rftEvent.getId());
			return "redirect:createEventDetails/" + rftEvent.getId();
		} catch (SubscriptionException e) {
			LOG.error("You have reached your subscription limit. " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("reached.subscription.limit", new Object[] { e.getMessage() }, Global.LOCALE));
			return "redirect:/buyer/buyerDashboard";
		} catch (ApplicationException e) {
			LOG.error("Error : " + e.getMessage(), e);
			redir.addFlashAttribute("error", e.getMessage());
			return "redirect:/buyer/createEvent/" + RfxTypes.RFT.name();
			// return "redirect:/buyer/buyerDashboard";
		}
	}

	@RequestMapping(path = "/createRftEvent", method = RequestMethod.POST)
	public String createRftEventFromBlank(Model model, @RequestParam(value = "businessUnitId", required = false) String businessUnitId, RedirectAttributes redir) {
		try {

			RftEvent rftEvent = new RftEvent();
			rftEvent.setCreatedBy(SecurityLibrary.getLoggedInUser());
			rftEvent.setCreatedDate(new Date());
			rftEvent.setBillOfQuantity(Boolean.TRUE);
			LOG.info("-----------------------businessUnitId----------" + businessUnitId);
			BusinessUnit businessUnit = null;
			if (StringUtils.isNotBlank(businessUnitId)) {
				businessUnit = businessUnitService.getBusinessUnitById(businessUnitId);
				rftEvent.setBusinessUnit(businessUnit);
			}
			ErpSetup erpSetup = erpSetupService.getErpConfigBytenantId(SecurityLibrary.getLoggedInUserTenantId());
			if (erpSetup != null) {
				LOG.info("--------erp flag set for event-----------");
				rftEvent.setErpEnable(erpSetup.getIsErpEnable());

			} else {
				rftEvent.setErpEnable(false);
			}
			rftEvent.setEventId(eventIdSettingDao.generateEventIdByBusinessUnit(SecurityLibrary.getLoggedInUserTenantId(), "RFT", rftEvent.getBusinessUnit()));

			rftEvent.setStatus(EventStatus.DRAFT);
			rftEvent.setAwardStatus(null);
			rftEvent = rftEventService.saveRftEvent(rftEvent, SecurityLibrary.getLoggedInUser());
			rftEventService.setDefaultEventContactDetail(SecurityLibrary.getLoggedInUser().getId(), rftEvent.getId());
			return "redirect:createEventDetails/" + rftEvent.getId();
		} catch (SubscriptionException e) {
			LOG.error("You have reached your subscription limit. " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("reached.subscription.limit", new Object[] { e.getMessage() }, Global.LOCALE));
		} catch (ApplicationException e) {
			LOG.error("Error : " + e.getMessage(), e);
			redir.addFlashAttribute("error", e.getMessage());
			return "redirect:/buyer/createEvent/" + RfxTypes.RFT.name();
			// return "redirect:/buyer/buyerDashboard";
		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
			// return "redirect:/buyer/buyerDashboard";
		}
		return "redirect:/buyer/buyerDashboard";
	}

	@RequestMapping(path = "/editEventDetails", method = RequestMethod.POST)
	public String editEventDetails(Model model, @RequestParam String eventId, RedirectAttributes redir) {
		model.addAttribute("eventPermissions", rftEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
		try {
			return super.editEventDetails(eventId);
		} catch (SubscriptionException e) {
			LOG.error("Error :" + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("reached.subscription.limit", new Object[] { e.getMessage() }, Global.LOCALE));
			return "redirect:/buyer/buyerDashboard";
		}
	}

	@RequestMapping(path = "/eventCreationPrevious", method = RequestMethod.POST)
	public String eventCreationPrevious(@ModelAttribute("event") RftEvent rftEvent, Model model) {
		model.addAttribute("eventPermissions", rftEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), rftEvent.getId()));
		return super.eventCreationPrevious(rftEvent);
	}

	@RequestMapping(path = "/createEventDetails/{eventId}", method = RequestMethod.GET)
	public String createEventDetails(Model model, @PathVariable String eventId, HttpServletRequest request, HttpSession session) {
		LOG.info("Time Zone : " + request.getSession().getAttribute("timeZone"));
		EventPermissions eventPermissions = rftEventService.getUserEventPemissions(SecurityLibrary.getLoggedInUser().getId(), eventId);
		model.addAttribute("eventPermissions", eventPermissions);
		return super.createEventDetailsPage(model, eventId, session, eventPermissions);
	}

	@RequestMapping(path = "/storeEventDetails", method = RequestMethod.POST)
	public ModelAndView saveEvent(@ModelAttribute("event") RftEvent rftEvent, @RequestParam(value = "industryCateg[]") String[] industryCategories, Model model, BindingResult result, RedirectAttributes redir, HttpSession session) {
		if (rftEvent.getViewSupplerName()) {
			rftEvent.setViewSupplerName(false);
		} else {
			rftEvent.setViewSupplerName(true);
		}

		model.addAttribute("eventPermissions", rftEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), rftEvent.getId()));
		return saveDetails(rftEvent, model, result, redir, true, session, false, industryCategories);

	}

	/**
	 * @param rftEvent
	 * @param model
	 * @param result
	 * @param redir
	 * @param isDraft
	 * @param industryCat
	 * @return
	 */
	private ModelAndView saveDetails(RftEvent rftEvent, Model model, BindingResult result, RedirectAttributes redir, boolean goNext, HttpSession session, boolean isDraft, String[] industryCat) {
		LOG.info("rftEvent :" + rftEvent.getEventId() + " reference Number: " + rftEvent.getReferanceNumber() + " Urgent Event :" + rftEvent.getUrgentEvent());
		Date startDate = null;
		Date endDate = null;
		String userControlledFlag = null;
		boolean isUserControl = Boolean.TRUE.equals(SecurityLibrary.getLoggedInUser().getBuyer().getEnableEventUserControle());
		String next = "";
		if (goNext) {
			next = "redirect:eventDescription/" + rftEvent.getId();
		} else {
			redir.addFlashAttribute("success", messageSource.getMessage("rftEvent.success.savedraft", new Object[] { rftEvent.getEventName() != null ? rftEvent.getEventName() : rftEvent.getEventId() }, Global.LOCALE));
			next = "redirect:createEventDetails/" + rftEvent.getId();
		}

		if (industryCat != null) {
			List<IndustryCategory> icList = new ArrayList<>();
			for (String industryCatId : industryCat) {
				LOG.info("industry cat Id :" + industryCatId);
				IndustryCategory ic = new IndustryCategory();
				ic.setId(industryCatId);
				icList.add(ic);
			}
			rftEvent.setIndustryCategories(icList);

			if (rftEvent.getStatus() == EventStatus.DRAFT) {
				checkToAddSupplier(rftEvent.getId(), industryCat, rftEvent.getIndustryCategories(), rftEvent.getTemplate() != null ? rftEvent.getTemplate().getId() : null);
			}

		}

		List<RftReminder> reminderList = rftEventService.getAllRftEventReminderForEvent(rftEvent.getId());
		model.addAttribute("reminderList", reminderList);
		model.addAttribute("intervalType", IntervalType.values());
		List<RftEventContact> eventContactsList = rftEventService.getAllContactForEvent(rftEvent.getId());
		model.addAttribute("eventContactsList", eventContactsList);
		model.addAttribute("evaluationDeclaratonList", declarationService.getDeclarationsByTypeForTenant(DeclarationType.EVALUATION_PROCESS, SecurityLibrary.getLoggedInUserTenantId()));
		model.addAttribute("supplierDeclaratonList", declarationService.getDeclarationsByTypeForTenant(DeclarationType.SUPPLIER_ACCEPTANCE, SecurityLibrary.getLoggedInUserTenantId()));
		RftEventContact eventContact = new RftEventContact();
		eventContact.setRfxEvent(rftEvent);
		eventContact.setEventId(rftEvent.getId());
		model.addAttribute("eventContact", eventContact);

		try {
			constructDefaultModel(model, rftEvent, RfxTypes.RFT, null);
		} catch (JsonProcessingException e1) {
			LOG.error("Error : " + e1.getMessage(), e1);
		}

		// to load assigned member at time of error
		// model.addAttribute("eventPermissions",
		// rftEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), rftEvent.getId()));
		// List<User> userTeamMemberList =
		// userService.fetchAllActiveUsersForTenant(SecurityLibrary.getLoggedInUserTenantId());
		List<User> userTeamMemberList = new ArrayList<User>(); // userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId());

		List<UserPojo> userListSumm = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", UserType.NORMAL_USER);
		List<RftApprovalUser> rftApprovalUserList = new ArrayList<RftApprovalUser>();
		List<RftSuspensionApprovalUser> rftSuspApprUserList = new ArrayList<RftSuspensionApprovalUser>();
		List<User> approvalUsers = new ArrayList<User>();
		List<User> suspApprovalUsers = new ArrayList<User>();
		List<UserPojo> maskingUserList = new ArrayList<UserPojo>();
		List<UserPojo> evaluationConclusionUserList = new ArrayList<UserPojo>();
		if (rftEvent.getUnMaskedUser() != null) {
			maskingUserList.add(new UserPojo(rftEvent.getUnMaskedUser().getId(), rftEvent.getUnMaskedUser().getLoginId(), rftEvent.getUnMaskedUser().getName(), rftEvent.getUnMaskedUser().getTenantId(), rftEvent.getUnMaskedUser().isDeleted(), rftEvent.getUnMaskedUser().getCommunicationEmail(), rftEvent.getUnMaskedUser().getEmailNotifications()));
		}
		for (UserPojo user : userListSumm) {
			User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted());
			rftApprovalUserList.add(new RftApprovalUser(u));
			rftSuspApprUserList.add(new RftSuspensionApprovalUser(u));
			userTeamMemberList.add(u);
			maskingUserList.add(user);
			evaluationConclusionUserList.add(user);
		}

		// model.addAttribute("userList1", userTeamMemberList);
		LOG.info("User member in withourt :  " + userTeamMemberList.size());
		RftEvent eventObj = rftEventService.loadRftEventById(rftEvent.getId());

		String eventOwnerID = eventObj.getCreatedBy().getId();

		LOG.info("========" + userControlledFlag);
		LOG.info("========" + eventOwnerID);
		LOG.info("========" + isUserControl);

		List<UserPojo> appUserListSumm = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", null);
		for (UserPojo user : appUserListSumm) {
			User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted());
			if (!approvalUsers.contains(u)) {
				approvalUsers.add(u);
			}
		}
		if (rftEvent.getApprovals() != null) {
			for (RftEventApproval app : rftEvent.getApprovals()) {
				for (RftApprovalUser appU : app.getApprovalUsers()) {

					if (isUserControl && appU.getUser() != null && appU.getUser().getId().equals(eventOwnerID)) {
						userControlledFlag = "Event Approval User";
					}

					if (!approvalUsers.contains(appU.getUser())) {
						approvalUsers.add(appU.getUser());
					}
				}
			}
		}

		if (rftEvent.getSuspensionApprovals() != null) {
			for (RftEventSuspensionApproval suspApp : rftEvent.getSuspensionApprovals()) {
				for (RftSuspensionApprovalUser suspAppU : suspApp.getApprovalUsers()) {
					if (!suspApprovalUsers.contains(suspAppU.getUser())) {
						suspApprovalUsers.add(suspAppU.getUser());
					}
				}
			}
		}

		List<RftUnMaskedUser> unMaskedUsers = rftEvent.getUnMaskedUsers();
		if (CollectionUtil.isNotEmpty(unMaskedUsers)) {
			for (RftUnMaskedUser user : unMaskedUsers) {
				user.setEvent(rftEvent);
				UserPojo u = new UserPojo(user.getUser().getId(), user.getUser().getLoginId(), user.getUser().getName(), user.getUser().getTenantId(), user.getUser().isDeleted(), user.getUser().getCommunicationEmail(), user.getUser().getEmailNotifications());
				if (!maskingUserList.contains(u)) {
					maskingUserList.add(u);
				}
			}
		}
		List<RftEvaluationConclusionUser> evaluationConclusionUsers = rftEvent.getEvaluationConclusionUsers();
		if (CollectionUtil.isNotEmpty(evaluationConclusionUsers)) {
			for (RftEvaluationConclusionUser user : evaluationConclusionUsers) {
				user.setEvent(rftEvent);
				UserPojo u = new UserPojo(user.getUser().getId(), user.getUser().getLoginId(), user.getUser().getName(), user.getUser().getTenantId(), user.getUser().isDeleted(), user.getUser().getCommunicationEmail(), user.getUser().getEmailNotifications());
				if (!evaluationConclusionUserList.contains(u)) {
					evaluationConclusionUserList.add(u);
				}
			}
		}

		model.addAttribute("evaluationConclusionUsers", evaluationConclusionUserList);
		model.addAttribute("userList1", approvalUsers);
		model.addAttribute("suspApprvlUserList", suspApprovalUsers);
		model.addAttribute("maskingUserList", maskingUserList);
		// model.addAttribute("unMaskedUser", rftEvent.getUnMaskedUser());

		if (CollectionUtil.isNotEmpty(eventObj.getTeamMembers())) {
			List<User> assignedTeamMembers = new ArrayList<>();
			List<RftTeamMember> userTeamMembers = new ArrayList<>();
			for (RftTeamMember rftTeamMember : eventObj.getTeamMembers()) {
				try {
					if (isUserControl && rftTeamMember.getUser() != null && rftTeamMember.getUser().getId().equals(eventOwnerID)) {
						userControlledFlag = "Event Team Member User";
					}
					assignedTeamMembers.add((User) rftTeamMember.getUser().clone());
					userTeamMembers.add(rftTeamMember);
				} catch (Exception e) {
					LOG.error("Error :  " + e.getMessage(), e);
				}
			}
			rftEvent.setTeamMembers(userTeamMembers);
			LOG.info("User member in if condition :  " + userTeamMemberList.size());
			userTeamMemberList.removeAll(assignedTeamMembers);
			model.addAttribute("userTeamMemberList", userTeamMemberList);
		} else {
			model.addAttribute("userTeamMemberList", userTeamMemberList);

		}

		if (isUserControl && rftEvent.getUnMaskedUser() != null && rftEvent.getUnMaskedUser().getId().equals(eventOwnerID)) {
			LOG.info("========can not be owner ");
			userControlledFlag = "Supplier Masking Evaluation";
		}

		if (StringUtils.checkString(userControlledFlag).length() > 0) {
			model.addAttribute("error", messageSource.getMessage("rftEvent.error.owner.control", new Object[] { userControlledFlag }, Global.LOCALE));
			if (rftEvent.getViewSupplerName()) {
				rftEvent.setViewSupplerName(false);
			} else {
				rftEvent.setViewSupplerName(true);
			}

			return new ModelAndView("createEventDetails", "event", rftEvent);
		}

		if (rftEvent.getTemplate() != null && rftEvent.getTemplate().getId() != null) {
			RfxTemplate rfxTemplate = rfxTemplateService.getRfxTemplateForEditById(rftEvent.getTemplate().getId(), SecurityLibrary.getLoggedInUserTenantId());

			if (!rfxTemplate.getVisibleAddSupplier()) {
				rftEvent.setAddSupplier(rfxTemplate.getAddSupplier());
			}
			if (!rfxTemplate.getVisibleCloseEnvelope()) {
				rftEvent.setCloseEnvelope(rfxTemplate.getCloseEnvelope());
			}
			if (!rfxTemplate.getVisibleViewSupplierName()) {
				rftEvent.setViewSupplerName(rfxTemplate.getViewSupplerName());
			}

			if (!rfxTemplate.getVisibleAllowToSuspendEvent()) {
				rftEvent.setAllowToSuspendEvent(rfxTemplate.getAllowToSuspendEvent());
			}
			rftEvent.setRfxEnvelopeOpening(rftEvent.getRfxEnvelopeOpening());
			rftEvent.setRfxEnvOpeningAfter(rftEvent.getRfxEnvOpeningAfter());
			rftEvent.setAllowDisqualifiedSupplierDownload(rftEvent.getAllowDisqualifiedSupplierDownload());

			model.addAttribute("templateFields", rfxTemplate.getFields());
			model.addAttribute("rfxTemplate", rfxTemplate);
		}

		if (result.hasErrors()) {
			LOG.info("Approvers : " + rftEvent.getApprovals().stream().iterator().next().getApprovalUsers().iterator().next().getUserName());
			LOG.error("Page submitted With Errors ............................. ");
			List<String> errMessages = new ArrayList<String>();
			for (ObjectError err : result.getAllErrors()) {
				errMessages.add(err.getDefaultMessage());
				LOG.info("ERROR : " + err.getDefaultMessage());
			}
			return new ModelAndView("createEventDetails" + rftEvent.getId());
		} else {
			LOG.info("Page submitted with no errors ....................................... ");
			try {
				LOG.info("rftEvent.getApprovals(): " + rftEvent.getApprovals());
				if (CollectionUtil.isNotEmpty(rftEvent.getApprovals())) {
					int level = 1;
					for (RftEventApproval app : rftEvent.getApprovals()) {
						app.setEvent(rftEvent);
						app.setLevel(level++);
						if (app != null && CollectionUtil.isNotEmpty(app.getApprovalUsers())) {
							for (RftApprovalUser approvalUser : app.getApprovalUsers()) {
								approvalUser.setApproval(app);
								approvalUser.setId(null);
							}
						}
					}
				} else {
					LOG.warn("Approval levels is empty.");
				}

				boolean eventCatError = false;
				if (!isDraft && CollectionUtil.isEmpty(rftEvent.getIndustryCategories())) {
					model.addAttribute("error", messageSource.getMessage("event.one.industy.required", new Object[] {}, Global.LOCALE));
					if (rftEvent.getViewSupplerName()) {
						rftEvent.setViewSupplerName(false);
					} else {
						rftEvent.setViewSupplerName(true);
					}
					LOG.error("Error: " + messageSource.getMessage("event.one.industy.required", new Object[] {}, Global.LOCALE));
					eventCatError = true;
				}
				if (!doValidate(rftEvent, model, isDraft) && !eventCatError) {

					if (StringUtils.checkString(rftEvent.getId()).length() > 0) {
						LOG.info("rftEvent update:      " + rftEvent.getId());
						TimeZone timeZone = TimeZone.getDefault();
						String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
						if (strTimeZone != null) {
							timeZone = TimeZone.getTimeZone(strTimeZone);
						}
						DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
						dateFormatter.setTimeZone(timeZone);

						if (rftEvent.getEventVisibilityDates() != null) {
							String visibilityDates[] = rftEvent.getEventVisibilityDates().split("-");
							DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
							formatter.setTimeZone(timeZone);
							startDate = (Date) formatter.parse(visibilityDates[0]);
							endDate = (Date) formatter.parse(visibilityDates[1]);
							rftEvent.setEventEnd(endDate);
							rftEvent.setEventStart(startDate);

							if (CollectionUtil.isNotEmpty(reminderList)) {
								for (RftReminder reminder : reminderList) {
									reminder.getReminderDate();
									formatter.setTimeZone(timeZone);
									Calendar cal = Calendar.getInstance(timeZone);
									if (reminder.getStartReminder() != null && reminder.getStartReminder() == Boolean.TRUE) {
										cal.setTime(startDate);
										if(reminder.getReminderDate().compareTo(startDate) > 0) {
											model.addAttribute("error", messageSource.getMessage("event.reminder.pastdate", new Object[] {}, Global.LOCALE));
											rftEvent.setViewSupplerName(rftEvent.getViewSupplerName() ? false : true);
											return new ModelAndView("createEventDetails", "event", rftEvent);
										}
									} else {
										cal.setTime(endDate);
										if(reminder.getReminderDate().compareTo(endDate) > 0) {
											model.addAttribute("error", messageSource.getMessage("event.reminder.pastdate", new Object[] {}, Global.LOCALE));
											rftEvent.setViewSupplerName(rftEvent.getViewSupplerName() ? false : true);
											return new ModelAndView("createEventDetails", "event", rftEvent);
										}
									}
									if (reminder.getIntervalType() == IntervalType.DAYS) {
										cal.add(Calendar.DATE, -(reminder.getInterval()));
										LOG.info("Reminder : " + formatter.format(cal.getTime()));
									} else {
										cal.add(Calendar.HOUR, -(reminder.getInterval()));
										LOG.info("Reminder  Hous: " + formatter.format(cal.getTime()));
									}

									if(EventStatus.SUSPENDED == rftEvent.getStatus() && reminder.getStartReminder()) {
										continue;
									}
									else if (cal.getTime().compareTo(new Date()) < 0) {
										model.addAttribute("error", messageSource.getMessage("event.reminder.pastdate", new Object[] {}, Global.LOCALE));
										rftEvent.setViewSupplerName(rftEvent.getViewSupplerName() ? false : true);
										return new ModelAndView("createEventDetails", "event", rftEvent);
									}
								}
							}
							LOG.info("Event Start date : " + startDate + " End Date : " + endDate);
						}
						RftEvent persistObj = rftEventService.getRftEventById(rftEvent.getId());
						if (Boolean.TRUE == persistObj.getMeetingReq()) {// change query to count query
							List<RftEventMeeting> eventMeetings = rftMeetingService.getRftMeetingByEventId(rftEvent.getId());
							if (CollectionUtil.isNotEmpty(eventMeetings)) {
								Date meetingMinDate = rftMeetingService.getMinMeetingDateForEvent(rftEvent.getId());
								if (meetingMinDate != null && rftEvent.getEventPublishDate() != null && rftEvent.getEventPublishDate().after(meetingMinDate)) {
									redir.addFlashAttribute("warn", messageSource.getMessage("rftEvent.error.minmeetingdate", new Object[] { dateFormatter.format(meetingMinDate) }, Global.LOCALE));
								}
								Date meetingMaxDate = rftMeetingService.getMaxMeetingDateForEvent(rftEvent.getId());
								if (meetingMaxDate != null && rftEvent.getEventEnd() != null && rftEvent.getEventEnd().before(meetingMaxDate)) {
									redir.addFlashAttribute("error", messageSource.getMessage("rftEvent.error.maxmeetingdate", new Object[] { dateFormatter.format(meetingMaxDate) }, Global.LOCALE));
								}
							}
						}
						Date publishDateTime = null;
						if (rftEvent.getEventPublishDate() != null && rftEvent.getEventPublishTime() != null) {
							publishDateTime = DateUtil.combineDateTime(rftEvent.getEventPublishDate(), rftEvent.getEventPublishTime(), timeZone);
						}
						LOG.info("publish date : " + publishDateTime + "   stratas date : " + rftEvent.getEventStart() + "end Date :" + rftEvent.getEventEnd());
						rftEvent.setEventPublishDate(publishDateTime);

						LOG.info("Event Status():" + rftEvent.getStatus());
						if (rftEvent.getStatus() == EventStatus.DRAFT) {
							LOG.info("Event Status():" + rftEvent.getStatus());
							LOG.info("PublishDate:" + rftEvent.getEventPublishDate());
							if (!isDraft && rftEvent.getEventPublishDate() != null && rftEvent.getEventPublishDate().before(new Date())) {
								// Published date cannot be in the past
								Date publishDate = rftEvent.getEventPublishDate();
								LOG.info("EVENT PUBLISHDATE:" + publishDate);
								SimpleDateFormat eventPubDate = new SimpleDateFormat("dd/MM/YYYY hh:mm a");
								eventPubDate.setTimeZone(timeZone);
								model.addAttribute("error", messageSource.getMessage("rftEvent.error.publishDate", new Object[] { eventPubDate.format(publishDate) }, Global.LOCALE));
								if (rftEvent.getViewSupplerName()) {
									rftEvent.setViewSupplerName(false);
								} else {
									rftEvent.setViewSupplerName(true);
								}

								return new ModelAndView("createEventDetails", "event", rftEvent);
							}
							if (!isDraft && rftEvent.getEventStart() != null && rftEvent.getEventStart().before(new Date())) {
								// Event start date cannot be in the past
								startDate = rftEvent.getEventStart();
								SimpleDateFormat eventStartDate = new SimpleDateFormat("dd/MM/YYYY hh:mm a");
								eventStartDate.setTimeZone(timeZone);
								model.addAttribute("error", messageSource.getMessage("rftEvent.error.startdate", new Object[] { eventStartDate.format(startDate) }, Global.LOCALE));
								if (rftEvent.getViewSupplerName()) {
									rftEvent.setViewSupplerName(false);
								} else {
									rftEvent.setViewSupplerName(true);
								}

								return new ModelAndView("createEventDetails", "event", rftEvent);
							}
						}
						if (!isDraft && rftEvent.getEventStart() != null && rftEvent.getDeliveryDate() != null && rftEvent.getEventStart().after(rftEvent.getDeliveryDate())) {
							Date deliveryDate = rftEvent.getDeliveryDate();
							LOG.info("EVENT deliveryDate:" + deliveryDate);
							SimpleDateFormat eventDeliveryDate = new SimpleDateFormat("dd/MM/YYYY hh:mm a");
							eventDeliveryDate.setTimeZone(timeZone);
							model.addAttribute("error", messageSource.getMessage("rftEvent.error.deliverydate", new Object[] { eventDeliveryDate.format(deliveryDate) }, Global.LOCALE));
							if (rftEvent.getViewSupplerName()) {
								rftEvent.setViewSupplerName(false);
							} else {
								rftEvent.setViewSupplerName(true);
							}

							return new ModelAndView("createEventDetails", "event", rftEvent);
						}
						if (!isDraft && rftEvent.getEventStart() != null && rftEvent.getEventEnd() != null && rftEvent.getEventStart().after(rftEvent.getEventEnd())) {
							endDate = rftEvent.getEventEnd();
							LOG.info("EVENT endDate:" + endDate);
							SimpleDateFormat eventEndDate = new SimpleDateFormat("dd/MM/YYYY hh:mm a");
							eventEndDate.setTimeZone(timeZone);
							model.addAttribute("error", messageSource.getMessage("rftEvent.error.enddate", new Object[] { eventEndDate.format(endDate) }, Global.LOCALE));
							if (rftEvent.getViewSupplerName()) {
								rftEvent.setViewSupplerName(false);
							} else {
								rftEvent.setViewSupplerName(true);
							}

							return new ModelAndView("createEventDetails", "event", rftEvent);
						}
						// To check time for 1 day event
						if (!isDraft && rftEvent.getEventStart() != null && rftEvent.getEventEnd() != null) {
							Date endDate2 = rftEvent.getEventEnd();
							Date startDate2 = rftEvent.getEventStart();

							LOG.info("EVENT endDate:" + endDate2);
							SimpleDateFormat df = new SimpleDateFormat("dd/MM/YYYY hh:mm a");

							if (df.format(endDate2).equals(df.format(startDate2))) {
								LOG.info("EVENT endDate: and Event start date cannot be same" + endDate2);
								model.addAttribute("error", messageSource.getMessage("rftEvent.error.endtime", new Object[] { df.format(endDate) }, Global.LOCALE));
								if (rftEvent.getViewSupplerName()) {
									rftEvent.setViewSupplerName(false);
								} else {
									rftEvent.setViewSupplerName(true);
								}

								return new ModelAndView("createEventDetails", "event", rftEvent);
							}
						}
						if (!isDraft && rftEvent.getEventPublishDate() != null && rftEvent.getEventStart() != null && rftEvent.getEventPublishDate().after(rftEvent.getEventStart())) {
							Date pubDate = rftEvent.getEventPublishDate();
							SimpleDateFormat publishDate = new SimpleDateFormat("dd/MM/YYYY hh:mm a");
							publishDate.setTimeZone(timeZone);
							LOG.info("publishDate:" + publishDate);
							model.addAttribute("error", messageSource.getMessage("rftEvent.error.publishdate", new Object[] { publishDate.format(pubDate) }, Global.LOCALE));
							if (rftEvent.getViewSupplerName()) {
								rftEvent.setViewSupplerName(false);
							} else {
								rftEvent.setViewSupplerName(true);
							}

							return new ModelAndView("createEventDetails", "event", rftEvent);
						}

//						if (Boolean.TRUE == persistObj.getEnableSuspensionApproval() && rftEvent.getSuspensionApprovals() == null) {
//							model.addAttribute("error", messageSource.getMessage("susp.approval.empty", new Object[] {}, Global.LOCALE));
//							return new ModelAndView("createEventDetails", "event", rftEvent);
//						}

						persistObj.setUnMaskedUsers(rftEvent.getUnMaskedUsers());
						if (Boolean.TRUE == rftEvent.getEnableEvaluationConclusionUsers()) {
							persistObj.setEvaluationConclusionUsers(rftEvent.getEvaluationConclusionUsers());
						} else {
							persistObj.setEvaluationConclusionUsers(null);
						}
						persistObj.setEnableEvaluationConclusionUsers(rftEvent.getEnableEvaluationConclusionUsers());

						persistObj.setModifiedDate(new Date());
						persistObj.setIndustryCategory(rftEvent.getIndustryCategory());
						// persistObj.setStatus(EventStatus.DRAFT); -- DONT SET THE STATUS AS DRAFT AS THIS COULD BE A
						// SUSPENDED EVENT EDIT - @Nitin Otageri

						if (rftEvent.getStatus() != EventStatus.SUSPENDED) {
							persistObj.setApprovals(rftEvent.getApprovals());
						}

						if (Boolean.TRUE == persistObj.getEnableSuspensionApproval() || Boolean.TRUE == rftEvent.getEnableSuspensionApproval()) {
							if (CollectionUtil.isNotEmpty(rftEvent.getSuspensionApprovals())) {
								int suspLevel = 1;
								for (RftEventSuspensionApproval app : rftEvent.getSuspensionApprovals()) {
									app.setEvent(rftEvent);
									app.setLevel(suspLevel++);
									if (app != null && CollectionUtil.isNotEmpty(app.getApprovalUsers()))
										for (RftSuspensionApprovalUser approvalUser : app.getApprovalUsers()) {
											approvalUser.setApproval(app);
											approvalUser.setId(null);
										}
								}
							} else {
								LOG.warn("Suspension Approval levels is empty.");
							}
						} else {
							if (CollectionUtil.isNotEmpty(rftEvent.getSuspensionApprovals())) {
								rftEvent.getSuspensionApprovals().clear();
							}
						}

						persistObj.setSuspensionApprovals(rftEvent.getSuspensionApprovals());

						persistObj.setEventVisibility(rftEvent.getEventVisibility());
						persistObj.setUrgentEvent(rftEvent.getUrgentEvent());
						persistObj.setEventName(rftEvent.getEventName());
						persistObj.setReferanceNumber(rftEvent.getReferanceNumber());
						persistObj.setEventVisibilityDates(rftEvent.getEventVisibilityDates());
						persistObj.setDeliveryAddress(rftEvent.getDeliveryAddress());
						persistObj.setParticipationFeeCurrency(rftEvent.getParticipationFeeCurrency());
						persistObj.setParticipationFees(rftEvent.getParticipationFees());
						persistObj.setEventPublishDate(publishDateTime);
						persistObj.setSubmissionValidityDays(rftEvent.getSubmissionValidityDays());
						persistObj.setEventEnd(rftEvent.getEventEnd());
						persistObj.setEventStart(rftEvent.getEventStart());
						persistObj.setIndustryCategories(rftEvent.getIndustryCategories());
						persistObj.setDeliveryDate(rftEvent.getDeliveryDate());
						persistObj.setViewSupplerName(rftEvent.getViewSupplerName());
						persistObj.setUnMaskedUser(rftEvent.getUnMaskedUser());
						persistObj.setCloseEnvelope(rftEvent.getCloseEnvelope());
						persistObj.setAddSupplier(rftEvent.getAddSupplier());
						persistObj.setAllowToSuspendEvent(rftEvent.getAllowToSuspendEvent());
						persistObj.setDepositCurrency(rftEvent.getDepositCurrency());
						persistObj.setDeposit(rftEvent.getDeposit());
						persistObj.setRfxEnvelopeOpening(rftEvent.getRfxEnvelopeOpening());
						persistObj.setRfxEnvOpeningAfter(rftEvent.getRfxEnvOpeningAfter());
						persistObj.setAllowDisqualifiedSupplierDownload(rftEvent.getAllowDisqualifiedSupplierDownload());

						persistObj.setMinimumSupplierRating(rftEvent.getMinimumSupplierRating());
						persistObj.setMaximumSupplierRating(rftEvent.getMaximumSupplierRating());
						persistObj.setEnableEvaluationDeclaration(rftEvent.getEnableEvaluationDeclaration());
						persistObj.setEnableSupplierDeclaration(rftEvent.getEnableSupplierDeclaration());
						persistObj.setEvaluationProcessDeclaration(Boolean.TRUE == rftEvent.getEnableEvaluationDeclaration() && rftEvent.getEvaluationProcessDeclaration() != null ? rftEvent.getEvaluationProcessDeclaration() : null);
						persistObj.setSupplierAcceptanceDeclaration(Boolean.TRUE == rftEvent.getEnableSupplierDeclaration() && rftEvent.getSupplierAcceptanceDeclaration() != null ? rftEvent.getSupplierAcceptanceDeclaration() : null);
						persistObj.setEnableApprovalReminder(rftEvent.getEnableApprovalReminder());
//						persistObj.setProcurementMethod(rftEvent.getProcurementMethod());
//						persistObj.setProcurementCategories(rftEvent.getProcurementCategories());
						if (Boolean.TRUE == rftEvent.getEnableApprovalReminder()) {
							persistObj.setReminderAfterHour(rftEvent.getReminderAfterHour());
							persistObj.setReminderCount(rftEvent.getReminderCount());
						} else {
							persistObj.setReminderAfterHour(null);
							persistObj.setReminderCount(null);
						}
						persistObj.setNotifyEventOwner(rftEvent.getNotifyEventOwner());
						if (persistObj.getTemplate() == null) {
							persistObj.setEnableSuspensionApproval(rftEvent.getEnableSuspensionApproval());
						}
						rftEventService.updateRftEvent(persistObj);

						if (StringUtils.checkString(persistObj.getPreviousRequestId()).length() > 0) {
							TatReportPojo tatReport = tatReportService.getTatReportByEventIdAndFormIdAndTenantId(persistObj.getPreviousRequestId(), persistObj.getEventId(), SecurityLibrary.getLoggedInUserTenantId());
							if (tatReport != null) {
								tatReportService.updateTatReportEventDetails(tatReport.getId(), persistObj.getId(), persistObj.getEventStart(), persistObj.getEventEnd(), persistObj.getEventPublishDate(), persistObj.getEventName(), persistObj.getReferanceNumber());

							}
						}
					}
				} else {
					if (goNext == Boolean.FALSE) {
						return new ModelAndView(next);
					} else {
						return new ModelAndView("createEventDetails", "event", rftEvent);
					}
				}
			} catch (Exception e) {
				LOG.error("Error while storing RftEvent : " + e.getMessage(), e);
				model.addAttribute("errors", "Error while storing Rft Event details for : " + rftEvent.getEventName() + ", message : " + e.getMessage());
				return new ModelAndView("createEventDetails", "event", rftEvent);
			}

			try {
				if (!isDraft && StringUtils.checkString(rftEvent.getId()).length() > 0) {
					rftEventService.insertTimeLine(rftEvent.getId());
				}
			} catch (Exception e) {
				LOG.error("Error : " + e.getMessage(), e);
			}
			return new ModelAndView(next);
		}

	}

	@RequestMapping(path = "/eventDescription/{eventId}", method = RequestMethod.GET)
	public ModelAndView eventDescription(Model model, @PathVariable String eventId) {
		if (StringUtils.checkString(eventId).length() == 0) {
			return new ModelAndView("redirect:/400_error");
		}
		model.addAttribute("eventPermissions", rftEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
		RftEvent rftEvent = null;
		BuyerSettings settings = null;
		LOG.info("Event ID in Description : " + eventId);
		if (StringUtils.checkString(eventId).length() > 0) {
			model.addAttribute("eventId", eventId);
			rftEvent = rftEventService.loadRftEventById(eventId);
			settings = buyerSettingsService.getBuyerSettingsByTenantId(SecurityLibrary.getLoggedInUserTenantId());
			if (settings != null) {
				if (rftEvent.getBaseCurrency() == null) {
					rftEvent.setBaseCurrency(settings.getCurrency());
				}
				if (rftEvent.getDecimal() == null) {
					rftEvent.setDecimal(settings.getDecimal());
				}
			}
			if (rftEvent.getTemplate() != null && rftEvent.getTemplate().getId() != null) {
				RfxTemplate rfxTemplate = rfxTemplateService.getRfxTemplateForEditById(rftEvent.getTemplate().getId(), SecurityLibrary.getLoggedInUserTenantId());
				model.addAttribute("templateFields", rfxTemplate.getFields());
				model.addAttribute("templateId", rftEvent.getTemplate().getId());
				model.addAttribute("rfxTemplate", rfxTemplate);
			}
		}
		if (rftEvent != null) {
			try {
				constructDefaultModel(model, rftEvent, RfxTypes.RFT, settings);
			} catch (JsonProcessingException e) {
				LOG.error("Error :" + e.getMessage(), e);
			}
			return new ModelAndView("eventDescription");
		} else {
			return new ModelAndView("/500_error");
		}
	}

	@RequestMapping(path = "/eventDescription", method = RequestMethod.POST)
	public ModelAndView saveEventDescription(@ModelAttribute("event") RftEvent rftEvent, Model model, BindingResult result, RedirectAttributes redir) {
		LOG.info("rft    " + rftEvent.getEventId());
		return saveRftDescription(rftEvent, model, result, redir, true);
	}

	/**
	 * @param rftEvent
	 * @param model
	 * @param result
	 * @param eventId
	 * @param redir
	 * @return
	 */
	private ModelAndView saveRftDescription(RftEvent rftEvent, Model model, BindingResult result, RedirectAttributes redir, boolean goNext) {
		LOG.info("Save Rft Event Description Called ");
		RftEvent persistObj = null;
		model.addAttribute("costCenter", costCenterService.getAllActiveCostCentersForTenant(SecurityLibrary.getLoggedInUserTenantId()));
		model.addAttribute("currency", currencyService.getAllCurrency());
		model.addAttribute("eventId", rftEvent.getId());
		model.addAttribute("eventPermissions", rftEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), rftEvent.getId()));
		String eventId = rftEvent.getId();

		if (rftEvent.getTemplate() != null && rftEvent.getTemplate().getId() != null) {
			RfxTemplate rfxTemplate = rfxTemplateService.getRfxTemplateForEditById(rftEvent.getTemplate().getId(), SecurityLibrary.getLoggedInUserTenantId());
			model.addAttribute("templateFields", rfxTemplate.getFields());
			model.addAttribute("rfxTemplate", rfxTemplate);
		}
		List<String> errMessages = new ArrayList<String>();
		if (result.hasErrors()) {
			for (ObjectError err : result.getAllErrors()) {
				errMessages.add(err.getDefaultMessage());
				LOG.info("ERROR : " + err.getDefaultMessage());
			}
			model.addAttribute("btnValue", "Create");
			model.addAttribute("error", errMessages);
			model.addAttribute("businessUnitList", businessUnitService.getPlainActiveBusinessUnitForTenant(SecurityLibrary.getLoggedInUserTenantId()));
			model.addAttribute("procurementMethodList", procurementMethodService.getAllActiveProcurementMethod(SecurityLibrary.getLoggedInUserTenantId()));
			model.addAttribute("procurementCategoryList", procurementCategoriesService.getAllActiveProcurementCategory(SecurityLibrary.getLoggedInUserTenantId()));
			return new ModelAndView("eventDescription", "event", rftEventService.getRftEventWithBuByeventId(eventId));
		} else {
			LOG.info("Page submitted....................................... ");
			try {
				if (StringUtils.checkString(eventId).length() > 0) {
					LOG.info("rftEventDescription update:      " + eventId);
					if (rftEvent.getBillOfQuantity() == false && rftEvent.getQuestionnaires() == false && rftEvent.getScheduleOfRate() == false) {
						if (rftEvent.getTemplate() != null && rftEvent.getTemplate().getId() != null) {
							LOG.info("templt id 2342 " + rftEvent.getTemplate().getId());
							RfxTemplate rfxTemplate = rfxTemplateService.getRfxTemplateForEditById(rftEvent.getTemplate().getId(), SecurityLibrary.getLoggedInUserTenantId());
							model.addAttribute("templateFields", rfxTemplate.getFields());
							model.addAttribute("rfxTemplate", rfxTemplate);
						}
						model.addAttribute("error", messageSource.getMessage("rftEvent.error.bqorcq", new Object[] {}, Global.LOCALE));
						model.addAttribute("businessUnitList", businessUnitService.getPlainActiveBusinessUnitForTenant(SecurityLibrary.getLoggedInUserTenantId()));
						model.addAttribute("procurementMethodList", procurementMethodService.getAllActiveProcurementMethod(SecurityLibrary.getLoggedInUserTenantId()));
						model.addAttribute("procurementCategoryList", procurementCategoriesService.getAllActiveProcurementCategory(SecurityLibrary.getLoggedInUserTenantId()));
						return new ModelAndView("eventDescription", "event", rftEventService.getRftEventWithBuByeventId(eventId));
					}
					persistObj = rftEventService.getRftEventById(eventId);
					persistObj.setEventDescription(rftEvent.getEventDescription());
					persistObj.setBudgetAmount(rftEvent.getBudgetAmount());
					persistObj.setBaseCurrency(rftEvent.getBaseCurrency());
					persistObj.setHistoricaAmount(rftEvent.getHistoricaAmount());
					persistObj.setDecimal(rftEvent.getDecimal());
					persistObj.setPaymentTerm(rftEvent.getPaymentTerm());
					persistObj.setDocumentReq(rftEvent.getDocumentReq());
					persistObj.setInternalRemarks(rftEvent.getInternalRemarks());
					persistObj.setEstimatedBudget(rftEvent.getEstimatedBudget());
					persistObj.setProcurementMethod(rftEvent.getProcurementMethod());
					persistObj.setProcurementCategories(rftEvent.getProcurementCategories());
					persistObj.setGroupCode(rftEvent.getGroupCode());
					persistObj.setScheduleOfRate(rftEvent.getScheduleOfRate());

					if (rftEvent.getDocumentReq() == false) {
						persistObj.setDocumentCompleted(false);
					}
					persistObj.setMeetingReq(rftEvent.getMeetingReq());
					if (rftEvent.getMeetingReq() == false) {
						persistObj.setMeetingCompleted(false);
					}
					persistObj.setQuestionnaires(rftEvent.getQuestionnaires());
					if (rftEvent.getQuestionnaires() == false) {
						persistObj.setCqCompleted(false);
					}
					persistObj.setBillOfQuantity(rftEvent.getBillOfQuantity());
					if (rftEvent.getBillOfQuantity() == false) {
						persistObj.setBqCompleted(false);
					}
					persistObj.setCostCenter(rftEvent.getCostCenter());
					persistObj.setBusinessUnit(rftEvent.getBusinessUnit());
					persistObj.setEventDetailCompleted(Boolean.TRUE);
					rftEventService.updateRftEvent(persistObj);
					LOG.info("rftEvent update:      " + persistObj);

					redir.addFlashAttribute("eventId", persistObj.getId());

				}

			} catch (Exception e) {
				LOG.error("Error while storing RftEvent : " + e.getMessage(), e);
				// model.addAttribute("error", "Error while storing Rft Event details for : " +
				// persistObj.getEventName() + ", message : " + e.getMessage());
				model.addAttribute("error", messageSource.getMessage("error.while.storing.rftevent", new Object[] { persistObj.getEventName(), e.getMessage() }, Global.LOCALE));
				model.addAttribute("businessUnitList", businessUnitService.getPlainActiveBusinessUnitForTenant(SecurityLibrary.getLoggedInUserTenantId()));
				model.addAttribute("procurementMethodList", procurementMethodService.getAllActiveProcurementMethod(SecurityLibrary.getLoggedInUserTenantId()));
				model.addAttribute("procurementCategoryList", procurementCategoriesService.getAllActiveProcurementCategory(SecurityLibrary.getLoggedInUserTenantId()));
				return new ModelAndView("eventDescription", "event", rftEventService.getRftEventWithBuByeventId(eventId));
			}

		}
		String next = "";
		if (goNext) {
			next = doNavigation(persistObj);
		} else {
			redir.addFlashAttribute("success", messageSource.getMessage("rftEvent.success.savedraft", new Object[] { (persistObj.getEventName() != null ? persistObj.getEventName() : persistObj.getEventId()) }, Global.LOCALE));
			next = "redirect:eventDescription/" + persistObj.getId();
		}
		return new ModelAndView(next);
	}

	@RequestMapping(path = "addContactPerson", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<RftEventContact>> saveContact(@ModelAttribute RftEventContact rftEventContact, BindingResult result, Model model) throws JsonProcessingException {
		LOG.info("Save Rft event Contact Called " + rftEventContact.getEventId());
		HttpHeaders headers = new HttpHeaders();
		try {
			if (result.hasErrors()) {
				for (ObjectError err : result.getAllErrors()) {
					headers.add("error", err.getDefaultMessage());
				}
				return new ResponseEntity<List<RftEventContact>>(null, headers, HttpStatus.EXPECTATION_FAILED);
			} else {

				if (StringUtils.checkString(rftEventContact.getEventId()).length() > 0) {
					if (rftEventContact.getComunicationEmail() != null || rftEventContact.getContactNumber() != null || rftEventContact.getMobileNumber() != null || rftEventContact.getFaxNumber() != null) {
						if (doValidate(rftEventContact)) {
							RftEvent rftEvent = rftEventService.getRftEventById(rftEventContact.getEventId());
							if (StringUtils.checkString(rftEventContact.getId()).length() == 0) {
								LOG.info("evennt in contact :  " + rftEvent.getEventName());
								rftEventContact.setRfxEvent(rftEvent);
								rftEventService.saveRftEventContact(rftEventContact);
								LOG.info("Save Contact Called" + SecurityLibrary.getLoggedInUser());
								headers.add("success", "Successfully added contact details");
							} else {
								LOG.info("upadte event contact");
								RftEventContact persistObj = rftEventService.getEventContactById(rftEventContact.getId());
								persistObj.setRfxEvent(rftEvent);
								persistObj.setComunicationEmail(rftEventContact.getComunicationEmail());
								persistObj.setContactName(rftEventContact.getContactName());
								persistObj.setContactNumber(rftEventContact.getContactNumber());
								persistObj.setDesignation(rftEventContact.getDesignation());
								persistObj.setFaxNumber(rftEventContact.getFaxNumber());
								persistObj.setMobileNumber(rftEventContact.getMobileNumber());
								persistObj.setTitle(rftEventContact.getTitle());
								rftEventService.updateRftEventContact(persistObj);
								LOG.info("upadte event contact  " + persistObj.toLogString());
								headers.add("success", "Successfully updated contact details");
							}

						} else {
							LOG.info("Contact name has already Exist ");
							headers.add("error", "Contact name already exist");
							return new ResponseEntity<List<RftEventContact>>(null, headers, HttpStatus.EXPECTATION_FAILED);
						}
					} else {
						headers.add("error", "Please select one of Contact number, Mobile number, Communication email.");
						return new ResponseEntity<List<RftEventContact>>(null, headers, HttpStatus.EXPECTATION_FAILED);
					}

				}
			}
		} catch (Exception e) {
			LOG.error("Error While Save the Country" + e.getMessage(), e);
			return new ResponseEntity<List<RftEventContact>>(null, headers, HttpStatus.EXPECTATION_FAILED);
		}

		List<RftEventContact> rftContactList = rftEventService.getAllContactForEvent(rftEventContact.getEventId());

		return new ResponseEntity<List<RftEventContact>>(rftContactList, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "mangeEventRequirement", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Integer> mangeEventRequirement(@RequestParam("eventId") String eventId, @RequestParam("eventRequirement") String eventRequirement) {
		LOG.info("Requirment Event Id: " + eventId + " the Evnt button :  " + eventRequirement);
		if (eventRequirement.equals("documentReq")) {
			Integer countDocument = rftEventService.getCountOfRftDocumentByEventId(eventId);
			LOG.info("countDocument :  " + countDocument);
			if (countDocument > 0) {
				return new ResponseEntity<Integer>(countDocument, HttpStatus.EXPECTATION_FAILED);
			}
		}
		if (eventRequirement.equals("meetingReq")) {
			Integer countMeeting = rftMeetingService.getCountOfRftMeetingByEventId(eventId);
			if (countMeeting > 0) {
				return new ResponseEntity<Integer>(countMeeting, HttpStatus.EXPECTATION_FAILED);
			}
		}
		if (eventRequirement.equals("questionnaires")) {
			Integer countQuestionnaires = rftEventService.getCountOfRftCqByEventId(eventId);
			if (countQuestionnaires > 0) {
				return new ResponseEntity<Integer>(countQuestionnaires, HttpStatus.EXPECTATION_FAILED);
			}
		}
		if (eventRequirement.equals("billOfQuantity")) {
			Integer countBillOfQuantity = rftBqService.getCountOfRftBqByEventId(eventId);
			LOG.info("billOfQuantity :  " + countBillOfQuantity);
			if (countBillOfQuantity > 0) {
				return new ResponseEntity<Integer>(countBillOfQuantity, HttpStatus.EXPECTATION_FAILED);
			}
		}
		if(eventRequirement.equals("scheduleOfRate")) {
			Integer countBillOfQuantity = rftSorService.getCountOfSorByEventId(eventId);
			LOG.info("Schedule of Rate :  " + countBillOfQuantity);
			if (countBillOfQuantity > 0) {
				return new ResponseEntity<Integer>(countBillOfQuantity, HttpStatus.EXPECTATION_FAILED);
			}
		}

		return new ResponseEntity<Integer>(HttpStatus.OK);
	}

	@RequestMapping(value = "deleteEventRequirement", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> deleteEventRequirement(@RequestParam("eventId") String eventId, @RequestParam("eventRequirement") String eventRequirement) {
		LOG.info("Requirment Event Id: " + eventId + " the Evnt button :  " + eventRequirement);
		try {
			if (eventRequirement.equals("documentReq")) {
				rftDocumentService.deleteAllRftDocuments(eventId, eventRequirement);
			}
			if (eventRequirement.equals("meetingReq")) {
				rftMeetingService.deleteAllRftMeetings(eventId, eventRequirement);
			}
			if (eventRequirement.equals("questionnaires")) {
				rftCqService.deleteAllCqs(eventId, eventRequirement);
			}
			if (eventRequirement.equals("billOfQuantity")) {
				rftBqService.deleteAllRftBq(eventId, eventRequirement);
			}
			if (eventRequirement.equals("scheduleOfRate")) {
				rftSorService.deleteAllSor(eventId, eventRequirement);
			}
		} catch (Exception e) {
			LOG.error("Error while deleteEventRequirement " + e.getMessage(), e);
		}
		return new ResponseEntity<String>(HttpStatus.OK);
	}

	@RequestMapping(path = "/saveAsDraft", method = RequestMethod.POST)
	public ModelAndView saveAsDraft(@ModelAttribute("event") RftEvent rftEvent, @RequestParam(value = "industryCateg[]") String[] industryCategories, Model model, BindingResult result, RedirectAttributes redir, HttpSession session) {

		if (rftEvent.getViewSupplerName() == true) {
			rftEvent.setViewSupplerName(false);
		} else {
			rftEvent.setViewSupplerName(true);
		}

		model.addAttribute("eventPermissions", rftEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), rftEvent.getId()));
		return saveDetails(rftEvent, model, result, redir, false, session, true, industryCategories);
	}

	@RequestMapping(path = "/saveAsDraftDescription", method = RequestMethod.POST)
	public ModelAndView saveAsDraftDescription(@ModelAttribute("event") RftEvent rftEvent, Model model, BindingResult result, RedirectAttributes redir) {
		model.addAttribute("eventPermissions", rftEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), rftEvent.getId()));
		return saveRftDescription(rftEvent, model, result, redir, false);
	}

	@RequestMapping(path = "/editContact", method = RequestMethod.POST)
	public ResponseEntity<RftEventContact> editRftContact(@RequestParam(value = "contactId") String contactId, Model model) {
		LOG.info("Getting the editContact. : " + contactId);
		RftEventContact eventContact = rftEventService.getEventContactById(contactId);
		LOG.info("event contact   :  " + eventContact);
		model.addAttribute("btnValue", "Update");
		return new ResponseEntity<RftEventContact>(eventContact, HttpStatus.OK);
	}

	@RequestMapping(path = "/deleteContact", method = RequestMethod.POST)
	public ResponseEntity<List<RftEventContact>> deleteContact(@RequestParam(value = "contactId") String contactId, @RequestParam(value = "eventId") String eventId, Model model) {
		LOG.info("Getting the Delete Contact. : " + contactId);
		RftEventContact eventContact = rftEventService.getEventContactById(contactId);
		HttpHeaders headers = new HttpHeaders();
		List<RftEventContact> rftContactList = null;
		LOG.info("event contact   :  " + eventContact);
		model.addAttribute("btnValue", "Update");
		try {
			rftEventService.deleteRftContact(eventContact);
			LOG.info("Delete the Contact");
			headers.add("success", "Contact removed Successfully");
		} catch (Exception e) {
			LOG.error("Error while removing Contact from event . " + e.getMessage(), e);
			headers.add("error", "Contact removed unsuccessfull");
			return new ResponseEntity<List<RftEventContact>>(rftContactList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		rftContactList = rftEventService.getAllContactForEvent(eventId);
		return new ResponseEntity<List<RftEventContact>>(rftContactList, headers, HttpStatus.OK);

	}

	@RequestMapping(path = "/addReminderOfEvent", method = RequestMethod.POST)
	public ResponseEntity<List<RftReminder>> addReminderOfEvent(@RequestParam(value = "reminderDuration") String reminderDuration, @RequestParam(value = "dateRangeData") String dateRangeData, @RequestParam(value = "reminderDurationType") IntervalType reminderDurationType, @RequestParam(value = "eventId") String eventId, @RequestParam(value = "reminderId") String reminderId, @RequestParam(value = "reminderNotifyType") String reminderNotifyType, Model model, HttpSession session) {
		LOG.info("Getting the AddReminder : " + eventId + "-reminderNotifyType:" + reminderNotifyType);
		HttpHeaders headers = new HttpHeaders();
		try {
			TimeZone timeZone = TimeZone.getDefault();
		    String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}
			if (StringUtils.checkString(eventId).length() > 0) {
				RftEvent rftEvent = rftEventService.getRftEventById(eventId);
				LOG.info("evennt in reminder save :  ");
				if (StringUtils.checkString(reminderId).length() == 0) {
					RftReminder rftReminder = new RftReminder();
					if (StringUtils.checkString(dateRangeData).length() > 0) {
						LOG.info("Visibility Dates :  " + dateRangeData);
						String visibilityDates[] = dateRangeData.split("-");
						DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
						formatter.setTimeZone(timeZone);
						Date startDate = (Date) formatter.parse(visibilityDates[0]);
						Date endDate = (Date) formatter.parse(visibilityDates[1]);

						LOG.info("start Date :  " + startDate + "end date : " + endDate);
						Calendar cal = Calendar.getInstance(timeZone);
						if (StringUtils.checkString(reminderNotifyType).equalsIgnoreCase("Start")) {
							cal.setTime(startDate);
							rftReminder.setStartReminder(Boolean.TRUE);
						} else {
							cal.setTime(endDate);
						}
						if (reminderDurationType == IntervalType.DAYS) {
							cal.add(Calendar.DATE, -Integer.parseInt(reminderDuration));
							rftReminder.setIntervalType(IntervalType.DAYS);
							LOG.info("Reminder : " + formatter.format(cal.getTime()));
						} else {
							cal.add(Calendar.HOUR, -Integer.parseInt(reminderDuration));
							rftReminder.setIntervalType(IntervalType.HOURS);
							LOG.info("Reminder  Hous: " + formatter.format(cal.getTime()));
						}

						rftReminder.setReminderDate(cal.getTime());
						List<RftReminder> reminderList = rftEventService.getAllRftEventReminderForEvent(eventId);
						if (CollectionUtil.isNotEmpty(reminderList)) {
							for (RftReminder rftReminderCompare : reminderList) {
								if (rftReminder.getReminderDate().compareTo(rftReminderCompare.getReminderDate()) == 0 && (rftReminder.getStartReminder() == rftReminderCompare.getStartReminder())) {
									headers.add("error", "There is another reminder on this date is exists");
									return new ResponseEntity<List<RftReminder>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
								}
							}
						}
						if (cal.getTime().compareTo(new Date()) < 0) {
							headers.add("error", messageSource.getMessage("event.reminder.pastdate", new Object[] {}, Global.LOCALE));
							return new ResponseEntity<List<RftReminder>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
						}

						// if (rftReminder.getReminderDate().before(startDate)) {
						// headers.add("error", "Reminder date/time should not be less than event start date");
						// return new ResponseEntity<List<RftReminder>>(null, headers,
						// HttpStatus.INTERNAL_SERVER_ERROR);
						// }
						rftEvent.setEventStart(startDate);
						rftEvent.setEventEnd(endDate);
						rftEventService.updateRftEvent(rftEvent);

						rftReminder.setRftEvent(rftEvent);
						rftReminder.setInterval(Integer.parseInt(reminderDuration));
						LOG.info(rftReminder);
						rftEventService.saveEventReminder(rftReminder);
					}
				}
			}
		} catch (Exception e) {
			LOG.error("Error While Save the Reminder" + e.getMessage(), e);
			return new ResponseEntity<List<RftReminder>>(null, headers, HttpStatus.EXPECTATION_FAILED);
		}
		List<RftReminder> reminderList = rftEventService.getAllRftEventReminderForEvent(eventId);
		return new ResponseEntity<List<RftReminder>>(reminderList, HttpStatus.OK);
	}

	@RequestMapping(path = "/deleteReminder", method = RequestMethod.POST)
	public ResponseEntity<List<RftReminder>> deleteReminder(@RequestParam(value = "reminderId") String reminderId, @RequestParam(value = "eventId") String eventId, Model model) {
		LOG.info("Getting the Delete reminder : " + reminderId);
		RftReminder eventReminder = rftEventService.getEventReminderById(reminderId);
		LOG.info("event eventReminder   :  " + eventReminder);
		model.addAttribute("btnValue", "Update");
		rftEventService.deleteRftReminder(eventReminder);
		LOG.info("Delete the Reminder");

		List<RftReminder> reminderList = rftEventService.getAllRftEventReminderForEvent(eventId);
		return new ResponseEntity<List<RftReminder>>(reminderList, HttpStatus.OK);
	}

	@RequestMapping(path = "/manageReminderOnDateChange", method = RequestMethod.POST)
	public ResponseEntity<List<RftReminder>> manageReminderOnDateChange(@RequestParam(value = "dateRangeData") String dateRangeData, @RequestParam(value = "eventId") String eventId, @RequestParam(value = "reminderId", required = false) String reminderId, Model model, HttpSession session) {
		LOG.info("Getting the AddReminder according to event date : " + eventId);
		HttpHeaders headers = new HttpHeaders();
		try {
			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}
			if (StringUtils.checkString(eventId).length() > 0) {
				RftEvent event = rftEventService.getEventById(eventId);

				LOG.info("event in reminder save :  ");
				List<RftReminder> reminderList = rftEventService.getAllRftEventReminderForEvent(eventId);
				if (CollectionUtil.isNotEmpty(reminderList)) {
					for (RftReminder reminder : reminderList) {
						reminder.getReminderDate();
						String visibilityDates[] = dateRangeData.split("-");
						DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
						formatter.setTimeZone(timeZone);
						Date startDate = (Date) formatter.parse(visibilityDates[0]);
						Date endDate = (Date) formatter.parse(visibilityDates[1]);
						Calendar cal = Calendar.getInstance(timeZone);
						if (reminder.getStartReminder() != null && reminder.getStartReminder() == Boolean.TRUE) {
							cal.setTime(startDate);
						} else {
							cal.setTime(endDate);
						}
						if (reminder.getIntervalType() == IntervalType.DAYS) {
							cal.add(Calendar.DATE, -(reminder.getInterval()));
							LOG.info("Reminder : " + formatter.format(cal.getTime()));
						} else {
							cal.add(Calendar.HOUR, -(reminder.getInterval()));
							LOG.info("Reminder  Hous: " + formatter.format(cal.getTime()));
						}

						if(EventStatus.SUSPENDED == event.getStatus() && reminder.getStartReminder()) {
							continue;
						}
						else if (cal.getTime().compareTo(new Date()) < 0) {
							headers.add("error", messageSource.getMessage("event.reminder.pastdate", new Object[] {}, Global.LOCALE));
							return new ResponseEntity<List<RftReminder>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
						}

						event.setEventStart(startDate);
						event.setEventEnd(endDate);
						rftEventService.updateRftEvent(event);

						reminder.setReminderDate(cal.getTime());
						LOG.info("reminder : " + formatter.format(reminder.getReminderDate()));
						rftEventService.updateEventReminder(reminder);
					}
				}
			}
		} catch (Exception e) {
			LOG.error("Error While Save the Country" + e.getMessage(), e);
			return new ResponseEntity<List<RftReminder>>(null, headers, HttpStatus.EXPECTATION_FAILED);
		}
		List<RftReminder> reminderList = rftEventService.getAllRftEventReminderForEvent(eventId);
		return new ResponseEntity<List<RftReminder>>(reminderList, HttpStatus.OK);
	}

	@RequestMapping(path = "/addTeamMemberToList", method = RequestMethod.POST)
	public ResponseEntity<List<EventTeamMember>> addTeamMemberToList(@RequestParam(value = "eventId") String eventId, @RequestParam(value = "userId") String userId, @RequestParam(value = "memberType") TeamMemberType memberType) {
		return super.addTeamMemberToList(eventId, userId, memberType);
	}

	@RequestMapping(path = "/removeTeamMemberfromList", method = RequestMethod.POST)
	public ResponseEntity<List<User>> removeTeamMemberfromList(@RequestParam(value = "eventId") String eventId, @RequestParam(value = "userId") String userId, Model model) {
		return super.removeTeamMemberfromList(eventId, userId);
	}

	@RequestMapping(path = "/manageEventVisibility", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Integer> manageEventVisibility(@RequestParam("eventId") String eventId, @RequestParam("eventVisbility") EventVisibilityType eventVisbility) {
		LOG.info("Requirment Event Id: " + eventId + " the Evnt button :  " + eventVisbility);
		if (eventVisbility == (EventVisibilityType.PUBLIC)) {
			Integer countMeeting = rftMeetingService.getCountOfRftMeetingByEventId(eventId);
			Integer countEventSupplier = rftEventSupplierService.getCountOfSupplierByEventId(eventId);

			LOG.info("countMeeting :" + countMeeting + " countEventSupplier :" + countEventSupplier);
			if (countMeeting + countEventSupplier > 0) {
				return new ResponseEntity<Integer>(countMeeting + countEventSupplier, HttpStatus.EXPECTATION_FAILED);
			}
		}
		return new ResponseEntity<Integer>(HttpStatus.OK);
	}

	@RequestMapping(value = "deleteEventVisibilityMeetings", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> deleteEventVisibilityMeetings(@RequestParam("eventId") String eventId, @RequestParam("eventVisbility") EventVisibilityType eventVisbility) {
		LOG.info("Requirment Event Id: " + eventId + " the Evnt button :  " + eventVisbility);
		if (eventVisbility == (EventVisibilityType.PUBLIC)) {
			rftMeetingService.deleteAllRftMeetings(eventId, null);
			rftEventSupplierService.deleteAllSuppliersByEventId(eventId);
		}
		return new ResponseEntity<String>(HttpStatus.OK);
	}

	private boolean doValidate(RftEventContact rftEventContact) {
		boolean validate = true;
		if (rftEventService.isExists(rftEventContact)) {
			validate = false;
		}
		return validate;
	}

	@RequestMapping(path = "/autoSaveDraft", method = RequestMethod.POST)
	public @ResponseBody void autoSaveDraft(@ModelAttribute("event") RftEvent rftEvent, @RequestParam("industryCateg[]") String[] industryCategory, BindingResult result, HttpSession session, Model model) {
		LOG.info("autoSave Draft Industry Cat " + industryCategory);
		if (rftEvent.getViewSupplerName() == true) {
			rftEvent.setViewSupplerName(false);
		} else {
			rftEvent.setViewSupplerName(true);
		}
		if (rftEvent.getStatus() == EventStatus.DRAFT) {
			// rftEventService.autoSaveRftDetails(rftEvent, industryCategory, result, session, true);
			rftEventService.autoSaveRfaDetails(rftEvent, industryCategory, result, (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY));
		}

	}

	@RequestMapping(path = "/autoSaveDraftDesc", method = RequestMethod.POST)
	public @ResponseBody void autoSaveDraftDesc(@ModelAttribute("event") RftEvent rfiEvent, BindingResult result, HttpSession session, Model model) {
		saveRfiDescription(rfiEvent);
	}

	private void saveRfiDescription(RftEvent rfiEvent) {
		LOG.info("Save Rft Event Description Called ");
		RftEvent persistObj = null;
		/* rfiEvent.setBillOfQuantity(Boolean.FALSE); */
		String eventId = rfiEvent.getId();

		try {
			if (StringUtils.checkString(eventId).length() > 0) {
				LOG.info("rftEventDescription update:      " + eventId);

				persistObj = rftEventService.getRftEventById(eventId);
				persistObj.setEventDescription(rfiEvent.getEventDescription());
				persistObj.setBudgetAmount(rfiEvent.getBudgetAmount());
				persistObj.setBaseCurrency(rfiEvent.getBaseCurrency());
				persistObj.setHistoricaAmount(rfiEvent.getHistoricaAmount());
				persistObj.setDecimal(rfiEvent.getDecimal());
				persistObj.setPaymentTerm(rfiEvent.getPaymentTerm());
				persistObj.setDocumentReq(rfiEvent.getDocumentReq());
				persistObj.setInternalRemarks(rfiEvent.getInternalRemarks());
				persistObj.setEstimatedBudget(rfiEvent.getEstimatedBudget());
				persistObj.setProcurementMethod(rfiEvent.getProcurementMethod());
				persistObj.setProcurementCategories(rfiEvent.getProcurementCategories());
				if (rfiEvent.getDocumentReq() == false) {
					persistObj.setDocumentCompleted(false);
				}
				persistObj.setMeetingReq(rfiEvent.getMeetingReq());
				if (rfiEvent.getMeetingReq() == false) {
					persistObj.setMeetingCompleted(false);
				}
				persistObj.setQuestionnaires(rfiEvent.getQuestionnaires());
				if (rfiEvent.getQuestionnaires() == false) {
					persistObj.setCqCompleted(false);
				}
				persistObj.setBillOfQuantity(rfiEvent.getBillOfQuantity());
				if (rfiEvent.getBillOfQuantity() == false) {
					persistObj.setBqCompleted(false);
				}
				persistObj.setCostCenter(rfiEvent.getCostCenter());
				persistObj.setBusinessUnit(rfiEvent.getBusinessUnit());
				persistObj.setEventDetailCompleted(Boolean.TRUE);
				rftEventService.updateRftEvent(persistObj);
				LOG.info("rftEvent update:      " + persistObj);
			}

		} catch (Exception e) {
			LOG.error("Error while storing RfiEvent : " + e.getMessage(), e);
		}

	}

}
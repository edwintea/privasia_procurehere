package com.privasia.procurehere.web.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpSession;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import com.privasia.procurehere.core.entity.RfpApprovalUser;
import com.privasia.procurehere.core.entity.RfpEvaluationConclusionUser;
import com.privasia.procurehere.core.entity.RfpEvent;
import com.privasia.procurehere.core.entity.RfpEventApproval;
import com.privasia.procurehere.core.entity.RfpEventContact;
import com.privasia.procurehere.core.entity.RfpEventMeeting;
import com.privasia.procurehere.core.entity.RfpEventSuspensionApproval;
import com.privasia.procurehere.core.entity.RfpReminder;
import com.privasia.procurehere.core.entity.RfpSuspensionApprovalUser;
import com.privasia.procurehere.core.entity.RfpTeamMember;
import com.privasia.procurehere.core.entity.RfpUnMaskedUser;
import com.privasia.procurehere.core.entity.RfxTemplate;
import com.privasia.procurehere.core.entity.User;
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
import com.privasia.procurehere.service.RfpBqService;
import com.privasia.procurehere.service.RfpCqService;
import com.privasia.procurehere.service.RfpSorService;
import com.privasia.procurehere.service.RfpDocumentService;
import com.privasia.procurehere.service.RfpEventSupplierService;
import com.privasia.procurehere.service.RfpMeetingService;
import com.privasia.procurehere.web.editors.ProcurementCategoriesEditor;
import com.privasia.procurehere.web.editors.ProcurementMethodEditor;
import com.privasia.procurehere.web.editors.RfpApprovalEditor;
import com.privasia.procurehere.web.editors.RfpSuspensionApprovalEditor;

@Controller
@RequestMapping("/buyer/RFP")
public class RfpCreationController extends EventCreationBase {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Autowired
	RfpCqService rfpCqService;

	@Autowired
	RfpBqService rfpBqService;

	@Autowired
	RfpDocumentService rfpDocumentService;

	@Autowired
	RfpMeetingService rfpMeetingService;

	@Autowired
	RfpApprovalEditor rfpApprovalEditor;

	@Autowired
	RfpEventSupplierService rfpEventSupplierService;

	@Autowired
	ErpSetupService erpSetupService;

	@Autowired
	RfpSuspensionApprovalEditor rfpSuspensionApprovalEditor;

	@Autowired
	ProcurementMethodService procurementMethodService;

	@Autowired
	ProcurementCategoriesService procurementCategoriesService;

	@Autowired
	ProcurementMethodEditor procurementMethodEditor;

	@Autowired
	ProcurementCategoriesEditor procurementCategoriesEditor;

	@Autowired
	RfpSorService rfpSorService;

	public RfpCreationController() {
		super(RfxTypes.RFP);
	}

	@InitBinder
	@Override
	public void initBinder(WebDataBinder binder, HttpSession session) {
		super.initBinder(binder, session);
		binder.registerCustomEditor(RfpApprovalUser.class, rfpApprovalEditor);
		binder.registerCustomEditor(ProcurementMethod.class, procurementMethodEditor);
		binder.registerCustomEditor(ProcurementCategories.class, procurementCategoriesEditor);
		binder.registerCustomEditor(RfpSuspensionApprovalUser.class, rfpSuspensionApprovalEditor);

		/*
		 * binder.registerCustomEditor(List.class, "approvalUsers", new CustomCollectionEditor(List.class) { protected
		 * Object convertElement(Object element) { if (element != null) { String id = (String) element; User user =
		 * userService.findUserById(id); return new RfpApprovalUser(user); } return null; } });
		 */
	}

	@RequestMapping(path = "/createRfpEvent", method = RequestMethod.GET)
	public String createRfpEvent(Model model, RedirectAttributes redir) {

		LOG.info("**************8");
		try {
			RfpEvent rfpEvent = new RfpEvent();
			rfpEvent.setCreatedBy(SecurityLibrary.getLoggedInUser());
			// LOG.info("*********"+SecurityLibrary.getLoggedInUser().getId());
			// List<RfpEventContact> contact =new ArrayList<RfpEventContact>();
			// for (RfpEventContact eventContact : contact) {
			// LOG.info("***********");
			// User user = SecurityLibrary.getLoggedInUser();
			// LOG.info("*********"+user.getId());
			// eventContact.setContactName(user.getName());
			// LOG.info("*********"+user.getName());
			// eventContact.setDesignation(user.getDesignation());
			// eventContact.setContactNumber(user.getPhoneNumber());
			// eventContact.setComunicationEmail(user.getCommunicationEmail());
			// contact.add(eventContact);
			// }
			rfpEvent.setCreatedDate(new Date());
			ErpSetup erpSetup = erpSetupService.getErpConfigBytenantId(SecurityLibrary.getLoggedInUser().getTenantId());
			if (erpSetup != null) {
				LOG.info("--------erp flag set for event-----------");
				rfpEvent.setErpEnable(erpSetup.getIsErpEnable());
			} else {
				rfpEvent.setErpEnable(false);
			}

			rfpEvent.setEventId(eventIdSettingDao.generateEventIdByBusinessUnit(SecurityLibrary.getLoggedInUserTenantId(), "RFP", null));
			rfpEvent.setStatus(EventStatus.DRAFT);
			rfpEvent.setBillOfQuantity(Boolean.TRUE);
			rfpEvent = rfpEventService.saveEvent(rfpEvent, SecurityLibrary.getLoggedInUser());
			rfpEventService.setDefaultEventContactDetail(SecurityLibrary.getLoggedInUser().getId(), rfpEvent.getId());
			model.addAttribute("eventPermissions", rfpEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), rfpEvent.getId()));
			return "redirect:createEventDetails/" + rfpEvent.getId();
		} catch (SubscriptionException e) {
			LOG.error("You have reached your subscription limit. " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("reached.subscription.limit", new Object[] { e.getMessage() }, Global.LOCALE));
			return "redirect:/buyer/buyerDashboard";
		} catch (ApplicationException e) {
			LOG.error("Error : " + e.getMessage(), e);
			redir.addFlashAttribute("error", e.getMessage());

			return "redirect:/buyer/createEvent/" + RfxTypes.RFP.name();
			// return "redirect:/buyer/buyerDashboard";
		}
	}

	@RequestMapping(path = "/createRfpEvent", method = RequestMethod.POST)
	public String createRfpEventUsingBU(Model model, RedirectAttributes redir, @RequestParam(value = "businessUnitId", required = false) String businessUnitId) {

		try {
			RfpEvent rfpEvent = new RfpEvent();
			rfpEvent.setCreatedBy(SecurityLibrary.getLoggedInUser());
			rfpEvent.setCreatedDate(new Date());
			LOG.info("-----------------------businessUnitId----------" + businessUnitId);
			BusinessUnit businessUnit = null;
			if (StringUtils.isNotBlank(businessUnitId)) {
				businessUnit = businessUnitService.getBusinessUnitById(businessUnitId);
				rfpEvent.setBusinessUnit(businessUnit);
			}
			ErpSetup erpSetup = erpSetupService.getErpConfigBytenantId(SecurityLibrary.getLoggedInUser().getTenantId());
			if (erpSetup != null) {
				LOG.info("--------erp flag set for event-----------");
				rfpEvent.setErpEnable(erpSetup.getIsErpEnable());

			} else {
				rfpEvent.setErpEnable(false);
			}
			rfpEvent.setEventId(eventIdSettingDao.generateEventIdByBusinessUnit(SecurityLibrary.getLoggedInUserTenantId(), "RFP", rfpEvent.getBusinessUnit()));

			rfpEvent.setStatus(EventStatus.DRAFT);
			rfpEvent.setBillOfQuantity(Boolean.TRUE);
			rfpEvent = rfpEventService.saveEvent(rfpEvent, SecurityLibrary.getLoggedInUser());
			rfpEventService.setDefaultEventContactDetail(SecurityLibrary.getLoggedInUser().getId(), rfpEvent.getId());
			model.addAttribute("eventPermissions", rfpEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), rfpEvent.getId()));
			return "redirect:createEventDetails/" + rfpEvent.getId();
		} catch (SubscriptionException e) {
			LOG.error("You have reached your subscription limit. " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("reached.subscription.limit", new Object[] { e.getMessage() }, Global.LOCALE));
		} catch (ApplicationException e) {
			LOG.error("Error : " + e.getMessage(), e);
			redir.addFlashAttribute("error", e.getMessage());
			return "redirect:/buyer/createEvent/" + RfxTypes.RFP.name();
			// return "redirect:/buyer/buyerDashboard";
		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
			// return "redirect:/buyer/buyerDashboard";
		}
		return "redirect:/buyer/buyerDashboard";
	}

	@RequestMapping(path = "/editEventDetails", method = RequestMethod.POST)
	public String editEventDetails(@RequestParam String eventId, Model model, RedirectAttributes redir) {
		model.addAttribute("eventPermissions", rfpEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
		try {
			return super.editEventDetails(eventId);
		} catch (SubscriptionException e) {
			LOG.error("Error :" + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("reached.subscription.limit", new Object[] { e.getMessage() }, Global.LOCALE));
			return "redirect:/buyer/buyerDashboard";
		}
	}

	@RequestMapping(path = "/eventCreationPrevious", method = RequestMethod.POST)
	public String eventCreationPrevious(@ModelAttribute RfpEvent rfiEvent) {
		return super.eventCreationPrevious(rfiEvent);
	}

	@RequestMapping(path = "/createEventDetails/{eventId}", method = RequestMethod.GET)
	public String createEventDetails(Model model, @PathVariable String eventId, HttpSession session) {

		EventPermissions eventPermissions = rfpEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
		model.addAttribute("eventPermissions", eventPermissions);
		return super.createEventDetailsPage(model, eventId, session, eventPermissions);
	}

	@RequestMapping(path = "/storeEventDetails", method = RequestMethod.POST)
	public ModelAndView saveRfpEvent(@ModelAttribute RfpEvent event, @RequestParam(value = "industryCateg[]") String[] industryCategories, Model model, BindingResult result, RedirectAttributes redir, HttpSession session) {
		if (event.getViewSupplerName()) {
			event.setViewSupplerName(false);
		} else {
			event.setViewSupplerName(true);
		}
		model.addAttribute("eventPermissions", rfpEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), event.getId()));
		return saveDetails(event, model, result, redir, true, session, false, industryCategories);

	}

	/**
	 * @param event
	 * @param model
	 * @param result
	 * @param redir
	 * @param industryCat
	 * @return
	 */
	private ModelAndView saveDetails(RfpEvent event, Model model, BindingResult result, RedirectAttributes redir, boolean goNext, HttpSession session, boolean isDraft, String[] industryCat) {
		String next = "";
		if (goNext) {
			next = "redirect:eventDescription/" + event.getId();
		} else {
			redir.addFlashAttribute("success", messageSource.getMessage("rftEvent.success.savedraft", new Object[] { (event.getEventName() != null ? event.getEventName() : event.getEventId()) }, Global.LOCALE));
			next = "redirect:createEventDetails/" + event.getId();
		}

		List<RfpReminder> reminderList = rfpEventService.getAllRfpEventReminderForEvent(event.getId());
		model.addAttribute("reminderList", reminderList);
		List<RfpEventContact> eventContactsList = rfpEventService.getAllContactForEvent(event.getId());
		model.addAttribute("eventContactsList", eventContactsList);
		model.addAttribute("evaluationDeclaratonList", declarationService.getDeclarationsByTypeForTenant(DeclarationType.EVALUATION_PROCESS, SecurityLibrary.getLoggedInUserTenantId()));
		model.addAttribute("supplierDeclaratonList", declarationService.getDeclarationsByTypeForTenant(DeclarationType.SUPPLIER_ACCEPTANCE, SecurityLibrary.getLoggedInUserTenantId()));
		model.addAttribute("eventPermissions", rfpEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), event.getId()));

		RfpEventContact eventContact = new RfpEventContact();
		eventContact.setRfxEvent(event);
		eventContact.setEventId(event.getId());
		model.addAttribute("eventContact", eventContact);
		if (industryCat != null) {
			List<IndustryCategory> icList = new ArrayList<>();
			for (String industryCatId : industryCat) {
				LOG.info("industry cat Id :" + industryCatId);
				IndustryCategory ic = new IndustryCategory();
				ic.setId(industryCatId);
				icList.add(ic);
			}
			event.setIndustryCategories(icList);
			if (event.getStatus() == EventStatus.DRAFT) {
				checkToAddSupplier(event.getId(), industryCat, event.getIndustryCategories(), event.getTemplate() != null ? event.getTemplate().getId() : null);
			}
		}
		try {
			constructDefaultModel(model, event, RfxTypes.RFP, null);

		} catch (JsonProcessingException e1) {
			LOG.info("Error :" + e1.getMessage(), e1);
		}

		// to load assigned member at time of error
		// List<User> userTeamMemberList =
		// userService.fetchAllActiveUsersForTenant(SecurityLibrary.getLoggedInUserTenantId());
		// List<User> userTeamMemberList =
		// userService.fetchAllActiveNormalUsersForTenant(SecurityLibrary.getLoggedInUserTenantId());
		// List<RfpApprovalUser> rfpApprovalUserList = new ArrayList<RfpApprovalUser>();
		// for (User user : userTeamMemberList) {
		// rfpApprovalUserList.add(new RfpApprovalUser(user));
		// }

		List<UserPojo> userListSumm = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", UserType.NORMAL_USER);
		List<User> userTeamMemberList = new ArrayList<User>();
		List<User> approvalUsers = new ArrayList<User>();
		List<User> suspApprovalUsers = new ArrayList<User>();
		List<UserPojo> maskingUserList = new ArrayList<UserPojo>();
		List<UserPojo> evaluationConclusionUserList = new ArrayList<UserPojo>();
		if (event.getUnMaskedUser() != null) {
			maskingUserList.add(new UserPojo(event.getUnMaskedUser().getId(), event.getUnMaskedUser().getLoginId(), event.getUnMaskedUser().getName(), event.getUnMaskedUser().getTenantId(), event.getUnMaskedUser().isDeleted(), event.getUnMaskedUser().getCommunicationEmail(), event.getUnMaskedUser().getEmailNotifications()));
		}
		for (UserPojo user : userListSumm) {
			User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted());
			userTeamMemberList.add(u);
			maskingUserList.add(user);
			evaluationConclusionUserList.add(user);
		}

		List<UserPojo> appUserListSumm = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", null);
		for (UserPojo user : appUserListSumm) {
			User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted());
			if (!approvalUsers.contains(u)) {
				approvalUsers.add(u);
			}
		}
		if (event.getApprovals() != null) {
			for (RfpEventApproval app : event.getApprovals()) {
				for (RfpApprovalUser appU : app.getApprovalUsers()) {
					if (!approvalUsers.contains(appU.getUser())) {
						approvalUsers.add(appU.getUser());
					}
				}
			}
		}

		if (event.getSuspensionApprovals() != null) {
			for (RfpEventSuspensionApproval suspApp : event.getSuspensionApprovals()) {
				for (RfpSuspensionApprovalUser suspAppU : suspApp.getApprovalUsers()) {
					if (!suspApprovalUsers.contains(suspAppU.getUser())) {
						suspApprovalUsers.add(suspAppU.getUser());
					}
				}
			}
		}

		List<RfpUnMaskedUser> unMaskedUsers = event.getUnMaskedUsers();
		if (CollectionUtil.isNotEmpty(unMaskedUsers)) {
			for (RfpUnMaskedUser user : unMaskedUsers) {
				user.setEvent(event);
				UserPojo u = new UserPojo(user.getUser().getId(), user.getUser().getLoginId(), user.getUser().getName(), user.getUser().getTenantId(), user.getUser().isDeleted(), user.getUser().getCommunicationEmail(), user.getUser().getEmailNotifications());
				if (!maskingUserList.contains(u)) {
					maskingUserList.add(u);
				}
			}
		}

		List<RfpEvaluationConclusionUser> evaluationConclusionUsers = event.getEvaluationConclusionUsers();
		if (CollectionUtil.isNotEmpty(evaluationConclusionUsers)) {
			for (RfpEvaluationConclusionUser user : evaluationConclusionUsers) {
				user.setEvent(event);
				UserPojo u = new UserPojo(user.getUser().getId(), user.getUser().getLoginId(), user.getUser().getName(), user.getUser().getTenantId(), user.getUser().isDeleted(), user.getUser().getCommunicationEmail(), user.getUser().getEmailNotifications());
				if (!evaluationConclusionUserList.contains(u)) {
					evaluationConclusionUserList.add(u);
				}
			}
		}

		model.addAttribute("userList1", approvalUsers);
		model.addAttribute("suspApprvlUserList", suspApprovalUsers);
		model.addAttribute("maskingUserList", maskingUserList);
		model.addAttribute("evaluationConclusionUsers", evaluationConclusionUserList);
		// model.addAttribute("userList1", userTeamMemberList);
		LOG.info("User member in withourt :  " + userTeamMemberList.size());
		RfpEvent eventObj = rfpEventService.loadRfpEventById(event.getId());
		if (CollectionUtil.isNotEmpty(eventObj.getTeamMembers())) {
			List<User> assignedTeamMembers = new ArrayList<>();
			List<RfpTeamMember> userTeamMembers = new ArrayList<>();
			for (RfpTeamMember rfpTeamMember : eventObj.getTeamMembers()) {
				try {
					assignedTeamMembers.add((User) rfpTeamMember.getUser().clone());
					userTeamMembers.add(rfpTeamMember);
				} catch (Exception e) {
					LOG.error("Error :  " + e.getMessage(), e);
				}
			}
			event.setTeamMembers(userTeamMembers);
			LOG.info("User member in if condition :  " + userTeamMemberList.size());
			userTeamMemberList.removeAll(assignedTeamMembers);
			model.addAttribute("userTeamMemberList", userTeamMemberList);
		} else {
			model.addAttribute("userTeamMemberList", userTeamMemberList);
			LOG.info("User member in else condition :  " + userTeamMemberList.size());
		}

		if (event.getTemplate() != null && event.getTemplate().getId() != null) {
			RfxTemplate rfxTemplate = rfxTemplateService.getRfxTemplateForEditById(event.getTemplate().getId(), SecurityLibrary.getLoggedInUserTenantId());

			if (!rfxTemplate.getVisibleAddSupplier()) {
				event.setAddSupplier(rfxTemplate.getAddSupplier());
			}
			if (!rfxTemplate.getVisibleCloseEnvelope()) {
				event.setCloseEnvelope(rfxTemplate.getCloseEnvelope());
			}
			if (!rfxTemplate.getVisibleViewSupplierName()) {
				event.setViewSupplerName(rfxTemplate.getViewSupplerName());
			}

			if (!rfxTemplate.getVisibleAllowToSuspendEvent()) {
				event.setAllowToSuspendEvent(rfxTemplate.getAllowToSuspendEvent());
			}

			event.setRfxEnvelopeOpening(event.getRfxEnvelopeOpening());
			event.setRfxEnvOpeningAfter(event.getRfxEnvOpeningAfter());
			event.setAllowDisqualifiedSupplierDownload(event.getAllowDisqualifiedSupplierDownload());
			model.addAttribute("templateFields", rfxTemplate.getFields());
			model.addAttribute("rfxTemplate", rfxTemplate);
		}
		if (result.hasErrors()) {
			LOG.error("Page submitted With Errors ............................. ");
			List<String> errMessages = new ArrayList<String>();
			for (ObjectError err : result.getAllErrors()) {
				errMessages.add(err.getDefaultMessage());
			}
			return new ModelAndView("createEventDetails/" + event.getId());
		} else {
			LOG.info("Page submitted with no errors ....................................... ");
			try {

				// LOG.info("rfpEvent.getApprovals(): " + event.getApprovals());
				if (CollectionUtil.isNotEmpty(event.getApprovals())) {
					int level = 1;
					for (RfpEventApproval app : event.getApprovals()) {
						app.setEvent(event);
						app.setLevel(level++);
						if (app != null && CollectionUtil.isNotEmpty(app.getApprovalUsers()))
							for (RfpApprovalUser approvalUser : app.getApprovalUsers()) {
								approvalUser.setApproval(app);
								approvalUser.setId(null);
							}
					}
				} else {
					LOG.warn("Approval levels is empty.");
				}
				TimeZone timeZone = TimeZone.getDefault();
				String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
				if (strTimeZone != null) {
					timeZone = TimeZone.getTimeZone(strTimeZone);
				}
				DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
				dateFormatter.setTimeZone(timeZone);
				boolean eventCatError = false;
				if (!isDraft && CollectionUtil.isEmpty(event.getIndustryCategories())) {
					model.addAttribute("error", messageSource.getMessage("event.one.industy.required", new Object[] {}, Global.LOCALE));
					if (event.getViewSupplerName()) {
						event.setViewSupplerName(false);
					} else {
						event.setViewSupplerName(true);
					}
					LOG.error("Error: " + messageSource.getMessage("event.one.industy.required", new Object[] {}, Global.LOCALE));
					eventCatError = true;
				}
				if (!doValidate(event, model, isDraft) && !eventCatError) {
					if (StringUtils.checkString(event.getId()).length() > 0) {
						LOG.info("rftEvent update:      " + event.getId());

						if (event.getEventVisibilityDates() != null) {
							String visibilityDates[] = event.getEventVisibilityDates().split("-");
							DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
							formatter.setTimeZone(timeZone);
							Date startDate = (Date) formatter.parse(visibilityDates[0]);
							Date endDate = (Date) formatter.parse(visibilityDates[1]);
							event.setEventEnd(endDate);
							event.setEventStart(startDate);

							if (CollectionUtil.isNotEmpty(reminderList)) {
								for (RfpReminder reminder : reminderList) {
									reminder.getReminderDate();
									formatter.setTimeZone(timeZone);
									Calendar cal = Calendar.getInstance(timeZone);
									if (reminder.getStartReminder() != null && reminder.getStartReminder() == Boolean.TRUE) {
										cal.setTime(startDate);
										if(reminder.getReminderDate().compareTo(startDate) > 0) {
											model.addAttribute("error", messageSource.getMessage("event.reminder.pastdate", new Object[] {}, Global.LOCALE));
											event.setViewSupplerName(event.getViewSupplerName() ? false : true);
											return new ModelAndView("createEventDetails", "event", event);
										}
									} else {
										cal.setTime(endDate);
										if(reminder.getReminderDate().compareTo(endDate) > 0) {
											model.addAttribute("error", messageSource.getMessage("event.reminder.pastdate", new Object[] {}, Global.LOCALE));
											event.setViewSupplerName(event.getViewSupplerName() ? false : true);
											return new ModelAndView("createEventDetails", "event", event);
										}
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
										model.addAttribute("error", messageSource.getMessage("event.reminder.pastdate", new Object[] {}, Global.LOCALE));
										event.setViewSupplerName(event.getViewSupplerName() ? false : true);
										return new ModelAndView("createEventDetails", "event", event);
									}
								}
							}
							LOG.info("Event Start date : " + startDate + " End Date : " + endDate);
						}
						Date publishDateTime = null;
						if (event.getEventPublishDate() != null && event.getEventPublishTime() != null) {
							publishDateTime = DateUtil.combineDateTime(event.getEventPublishDate(), event.getEventPublishTime(), timeZone);
						}
						LOG.info("publist date : " + publishDateTime + "   stratas date : " + event.getEventStart());
						event.setEventPublishDate(publishDateTime);
						RfpEvent persistObj = rfpEventService.getEventById(event.getId());
						if (persistObj.getMeetingReq()) {
							List<RfpEventMeeting> eventMeetings = rfpMeetingService.getAllRfpMeetingByEventId(persistObj.getId());
							if (CollectionUtil.isNotEmpty(eventMeetings)) {
								Date meetingMinDate = rfpMeetingService.getMinMeetingDateForEvent(persistObj.getId());
								if (meetingMinDate != null && event.getEventPublishDate() != null && event.getEventPublishDate().after(meetingMinDate)) {
									redir.addFlashAttribute("warn", messageSource.getMessage("rftEvent.error.minmeetingdate", new Object[] { dateFormatter.format(meetingMinDate) }, Global.LOCALE));
									// return new ModelAndView("createEventDetails", "event", event);
								}

								Date meetingMaxDate = rfpMeetingService.getMaxMeetingDateForEvent(persistObj.getId());
								if (meetingMaxDate != null && event.getEventEnd() != null && persistObj.getEventEnd().before(meetingMaxDate)) {
									redir.addFlashAttribute("error", messageSource.getMessage("rftEvent.error.maxmeetingdate", new Object[] { dateFormatter.format(meetingMaxDate) }, Global.LOCALE));
									// return new ModelAndView("createEventDetails", "event", event);
								}
							}
						}

						LOG.info("Event Status():" + event.getStatus());
						if (event.getStatus() == EventStatus.DRAFT) {
							LOG.info("Event Status():" + event.getStatus());
							if (!isDraft && event.getEventPublishDate() != null && event.getEventPublishDate().before(new Date())) {
								// Published date cannot be in the past
								Date publishDate = event.getEventPublishDate();
								LOG.info("EVENT PUBLISHDATE:" + publishDate);
								SimpleDateFormat eventPubDate = new SimpleDateFormat("dd/MM/YYYY hh:mm a");
								eventPubDate.setTimeZone(timeZone);
								model.addAttribute("error", messageSource.getMessage("rftEvent.error.publishDate", new Object[] { eventPubDate.format(publishDate) }, Global.LOCALE));
								if (event.getViewSupplerName()) {
									event.setViewSupplerName(false);
								} else {
									event.setViewSupplerName(true);
								}

								return new ModelAndView("createEventDetails", "event", event);
							}
							if (!isDraft && event.getEventStart() != null && event.getEventStart().before(new Date())) {
								// Event start date cannot be in the past
								Date startDate = event.getEventStart();
								LOG.info("EVENT startDate:" + startDate);
								SimpleDateFormat eventStartDate = new SimpleDateFormat("dd/MM/YYYY hh:mm a");
								eventStartDate.setTimeZone(timeZone);
								LOG.info("EVENT startDate:" + startDate);
								LOG.info("New Date:" + new Date());
								model.addAttribute("error", messageSource.getMessage("rftEvent.error.startdate", new Object[] { eventStartDate.format(startDate) }, Global.LOCALE));
								if (event.getViewSupplerName()) {
									event.setViewSupplerName(false);
								} else {
									event.setViewSupplerName(true);
								}

								return new ModelAndView("createEventDetails", "event", event);
							}
						}

						if (!isDraft && event.getEventStart() != null && event.getDeliveryDate() != null && event.getEventStart().after(event.getDeliveryDate())) {
							Date deliveryDate = event.getDeliveryDate();
							LOG.info("EVENT deliveryDate:" + deliveryDate);
							SimpleDateFormat eventDeliveryDate = new SimpleDateFormat("dd/MM/YYYY hh:mm a");
							eventDeliveryDate.setTimeZone(timeZone);
							model.addAttribute("error", messageSource.getMessage("rftEvent.error.deliverydate", new Object[] { eventDeliveryDate.format(deliveryDate) }, Global.LOCALE));
							if (event.getViewSupplerName()) {
								event.setViewSupplerName(false);
							} else {
								event.setViewSupplerName(true);
							}

							return new ModelAndView("createEventDetails", "event", event);
						}
						if (!isDraft && event.getEventStart() != null && event.getEventEnd() != null && event.getEventStart().after(event.getEventEnd())) {
							Date endDate = event.getEventEnd();
							LOG.info("EVENT endDate:" + endDate);
							SimpleDateFormat eventEndDate = new SimpleDateFormat("dd/MM/YYYY hh:mm a");
							eventEndDate.setTimeZone(timeZone);
							model.addAttribute("error", messageSource.getMessage("rftEvent.error.enddate", new Object[] { eventEndDate.format(endDate) }, Global.LOCALE));
							if (event.getViewSupplerName()) {
								event.setViewSupplerName(false);
							} else {
								event.setViewSupplerName(true);
							}
							return new ModelAndView("createEventDetails", "event", event);
						}
						// To check time for 1 day event
						if (!isDraft && event.getEventStart() != null && event.getEventEnd() != null) {
							Date endDate = event.getEventEnd();
							Date startDate = event.getEventStart();

							LOG.info("EVENT endDate:" + endDate);
							SimpleDateFormat df = new SimpleDateFormat("dd/MM/YYYY hh:mm a");

							if (df.format(endDate).equals(df.format(startDate))) {
								LOG.info("EVENT endDate: and Event start date cannot be same" + endDate);
								model.addAttribute("error", messageSource.getMessage("rftEvent.error.endtime", new Object[] { df.format(endDate) }, Global.LOCALE));
								if (event.getViewSupplerName()) {
									event.setViewSupplerName(false);
								} else {
									event.setViewSupplerName(true);
								}

								return new ModelAndView("createEventDetails", "event", event);
							}
						}
						LOG.info("PublishDate:" + event.getEventPublishDate());
						LOG.info("EventStartDate:" + event.getEventStart());
						if (!isDraft && event.getEventPublishDate() != null && event.getEventStart() != null && event.getEventPublishDate().after(event.getEventStart())) {
							Date pubDate = event.getEventPublishDate();
							SimpleDateFormat publishDate = new SimpleDateFormat("dd/MM/YYYY hh:mm a");
							publishDate.setTimeZone(timeZone);
							model.addAttribute("error", messageSource.getMessage("rftEvent.error.publishdate", new Object[] { publishDate.format(pubDate) }, Global.LOCALE));
							return new ModelAndView("createEventDetails", "event", event);
						}


//						if (Boolean.TRUE == persistObj.getEnableSuspensionApproval() && event.getSuspensionApprovals() == null) {
//							model.addAttribute("error", messageSource.getMessage("susp.approval.empty", new Object[] {}, Global.LOCALE));
//							return new ModelAndView("createEventDetails", "event", event);
//						}

						/*
						 * if (!isDraft && CollectionUtil.isEmpty(contactList)) { model.addAttribute("error",
						 * messageSource.getMessage("rftEvent.error.contact", new Object[] { event.getEventName() },
						 * Global.LOCALE)); return new ModelAndView("createEventDetails", "event", event); }
						 */
						LOG.info("1");
						persistObj.setModifiedDate(new Date());
						persistObj.setUnMaskedUsers(event.getUnMaskedUsers());
						if (Boolean.TRUE == event.getEnableEvaluationConclusionUsers()) {
							persistObj.setEvaluationConclusionUsers(event.getEvaluationConclusionUsers());
						} else {
							persistObj.setEvaluationConclusionUsers(null);
						}
						persistObj.setEnableEvaluationConclusionUsers(event.getEnableEvaluationConclusionUsers());
						persistObj.setIndustryCategory(event.getIndustryCategory());
						// persistObj.setStatus(EventStatus.DRAFT); -- DONT SET THE STATUS AS DRAFT AS THIS COULD BE A
						// SUSPENDED EVENT EDIT - @Nitin Otageri

						if (event.getStatus() != EventStatus.SUSPENDED) {
							persistObj.setApprovals(event.getApprovals());
						}

						if (Boolean.TRUE == persistObj.getEnableSuspensionApproval() || Boolean.TRUE == event.getEnableSuspensionApproval()) {
							if (CollectionUtil.isNotEmpty(event.getSuspensionApprovals())) {
								int suspLevel = 1;
								for (RfpEventSuspensionApproval app : event.getSuspensionApprovals()) {
									app.setEvent(event);
									app.setLevel(suspLevel++);
									if (app != null && CollectionUtil.isNotEmpty(app.getApprovalUsers()))
										for (RfpSuspensionApprovalUser approvalUser : app.getApprovalUsers()) {
											approvalUser.setApproval(app);
											approvalUser.setId(null);
										}
								}
							} else {
								LOG.warn("Suspension Approval levels is empty.");
							}
						} else {
							LOG.info("Clrearing Susp Approvels >>>>>>>>>>>>>>>>>>>> .");
							if (CollectionUtil.isNotEmpty(event.getSuspensionApprovals())) {
								event.getSuspensionApprovals().clear();
							}
						}
						persistObj.setSuspensionApprovals(event.getSuspensionApprovals());

						persistObj.setEventPublishDate(publishDateTime);
						persistObj.setEventStart(event.getEventStart());
						persistObj.setEventEnd(event.getEventEnd());
						persistObj.setEventVisibility(event.getEventVisibility());
						persistObj.setEventName(event.getEventName());
						persistObj.setReferanceNumber(event.getReferanceNumber());
						persistObj.setUrgentEvent(event.getUrgentEvent());
						persistObj.setEventVisibilityDates(event.getEventVisibilityDates());
						persistObj.setDeliveryAddress(event.getDeliveryAddress());
						persistObj.setParticipationFeeCurrency(event.getParticipationFeeCurrency());
						persistObj.setParticipationFees(event.getParticipationFees());
						persistObj.setEventPublishDate(event.getEventPublishDate());
						persistObj.setSubmissionValidityDays(event.getSubmissionValidityDays());
						persistObj.setIndustryCategories(event.getIndustryCategories());
						persistObj.setDeliveryDate(event.getDeliveryDate());
						persistObj.setViewSupplerName(event.getViewSupplerName());
						persistObj.setUnMaskedUser(event.getUnMaskedUser());
						persistObj.setCloseEnvelope(event.getCloseEnvelope());
						persistObj.setAddSupplier(event.getAddSupplier());
						persistObj.setAllowToSuspendEvent(event.getAllowToSuspendEvent());
						persistObj.setDepositCurrency(event.getDepositCurrency());
						persistObj.setDeposit(event.getDeposit());
						persistObj.setRfxEnvelopeOpening(event.getRfxEnvelopeOpening());
						persistObj.setRfxEnvOpeningAfter(event.getRfxEnvOpeningAfter());
						persistObj.setMinimumSupplierRating(event.getMinimumSupplierRating());
						persistObj.setMaximumSupplierRating(event.getMaximumSupplierRating());
						persistObj.setEnableEvaluationDeclaration(event.getEnableEvaluationDeclaration());
						persistObj.setEnableSupplierDeclaration(event.getEnableSupplierDeclaration());
						persistObj.setEnableApprovalReminder(event.getEnableApprovalReminder());
						persistObj.setAllowDisqualifiedSupplierDownload(event.getAllowDisqualifiedSupplierDownload());
//						persistObj.setProcurementMethod(event.getProcurementMethod());
//						persistObj.setProcurementCategories(event.getProcurementCategories());

						if (Boolean.TRUE == event.getEnableApprovalReminder()) {
							persistObj.setReminderAfterHour(event.getReminderAfterHour());
							persistObj.setReminderCount(event.getReminderCount());
						} else {
							persistObj.setReminderAfterHour(null);
							persistObj.setReminderCount(null);
						}
						persistObj.setNotifyEventOwner(event.getNotifyEventOwner());
						persistObj.setEvaluationProcessDeclaration(Boolean.TRUE == event.getEnableEvaluationDeclaration() && event.getEvaluationProcessDeclaration() != null ? event.getEvaluationProcessDeclaration() : null);
						persistObj.setSupplierAcceptanceDeclaration(Boolean.TRUE == event.getEnableSupplierDeclaration() && event.getSupplierAcceptanceDeclaration() != null ? event.getSupplierAcceptanceDeclaration() : null);

						if (persistObj.getTemplate() == null) {
							persistObj.setEnableSuspensionApproval(event.getEnableSuspensionApproval());
						}

						rfpEventService.updateEvent(persistObj);

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
						return new ModelAndView("createEventDetails", "event", event);
					}

				}
			} catch (Exception e) {
				LOG.error("Error while storing RfpEvent : " + e.getMessage(), e);
				model.addAttribute("errors", "Error while storing Rft Event details for : " + event.getEventName() + ", message : " + e.getMessage());
				return new ModelAndView("createEventDetails", "event", event);
			}
			try {
				if (!isDraft && StringUtils.checkString(event.getId()).length() > 0) {
					rfpEventService.insertTimeLine(event.getId());
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

		RfpEvent event = null;
		BuyerSettings settings = null;
		LOG.info("Event ID in Description : " + eventId);
		if (StringUtils.checkString(eventId).length() > 0) {
			model.addAttribute("eventPermissions", rfpEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));

			model.addAttribute("eventId", eventId);
			event = rfpEventService.loadRfpEventById(eventId);
			settings = buyerSettingsService.getBuyerSettingsByTenantId(SecurityLibrary.getLoggedInUserTenantId());
			if (settings != null) {
				if (event.getBaseCurrency() == null) {
					event.setBaseCurrency(settings.getCurrency());
				}
				if (event.getDecimal() == null) {
					event.setDecimal(settings.getDecimal());
				}
			}
			if (event.getTemplate() != null && event.getTemplate().getId() != null) {
				RfxTemplate rfxTemplate = rfxTemplateService.getRfxTemplateForEditById(event.getTemplate().getId(), SecurityLibrary.getLoggedInUserTenantId());
				model.addAttribute("templateFields", rfxTemplate.getFields());
				model.addAttribute("templateId", event.getTemplate().getId());
				model.addAttribute("rfxTemplate", rfxTemplate);
			}
		}
		if (event != null) {
			try {
				constructDefaultModel(model, event, RfxTypes.RFP, settings);
			} catch (JsonProcessingException e1) {
				LOG.info("Error :" + e1.getMessage(), e1);
			}
			return new ModelAndView("eventDescription");
		} else {
			return new ModelAndView("/500_error");
		}
	}

	@RequestMapping(path = "/eventDescription", method = RequestMethod.POST)
	public ModelAndView saveEventDescription(@ModelAttribute RfpEvent rftEvent, Model model, BindingResult result, RedirectAttributes redir) {
		LOG.info("rft    " + rftEvent);
		model.addAttribute("eventPermissions", rfpEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), rftEvent.getId()));

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
	private ModelAndView saveRftDescription(RfpEvent rftEvent, Model model, BindingResult result, RedirectAttributes redir, boolean goNext) {
		LOG.info("Save Rft Event Description Called ");
		RfpEvent persistObj = null;
		model.addAttribute("eventPermissions", rfpEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), rftEvent.getId()));
		model.addAttribute("costCenter", costCenterService.getAllActiveCostCentersForTenant(SecurityLibrary.getLoggedInUserTenantId()));
		model.addAttribute("currency", currencyService.getAllCurrency());
		model.addAttribute("eventId", rftEvent.getId());
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
			model.addAttribute("event", rfpEventService.getRfpEventWithBuByeventId(eventId));
			model.addAttribute("businessUnitList", businessUnitService.getPlainActiveBusinessUnitForTenant(SecurityLibrary.getLoggedInUserTenantId()));
			model.addAttribute("procurementMethodList", procurementMethodService.getAllActiveProcurementMethod(SecurityLibrary.getLoggedInUserTenantId()));
			model.addAttribute("procurementCategoryList", procurementCategoriesService.getAllActiveProcurementCategory(SecurityLibrary.getLoggedInUserTenantId()));
			return new ModelAndView("eventDescription", "event", rfpEventService.getRfpEventWithBuByeventId(eventId));
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
						LOG.info("RFP ERROR");
						model.addAttribute("error", messageSource.getMessage("rftEvent.error.bqorcq", new Object[] {}, Global.LOCALE));
						model.addAttribute("businessUnitList", businessUnitService.getPlainActiveBusinessUnitForTenant(SecurityLibrary.getLoggedInUserTenantId()));
						model.addAttribute("procurementMethodList", procurementMethodService.getAllActiveProcurementMethod(SecurityLibrary.getLoggedInUserTenantId()));
						model.addAttribute("procurementCategoryList", procurementCategoriesService.getAllActiveProcurementCategory(SecurityLibrary.getLoggedInUserTenantId()));
						return new ModelAndView("eventDescription", "event", rfpEventService.getRfpEventWithBuByeventId(eventId));
					}
					persistObj = rfpEventService.getEventById(eventId);
					persistObj.setEventDescription(rftEvent.getEventDescription());
					persistObj.setInternalRemarks(rftEvent.getInternalRemarks());
					persistObj.setBudgetAmount(rftEvent.getBudgetAmount());
					persistObj.setBaseCurrency(rftEvent.getBaseCurrency());
					persistObj.setHistoricaAmount(rftEvent.getHistoricaAmount());
					persistObj.setDecimal(rftEvent.getDecimal());
					persistObj.setPaymentTerm(rftEvent.getPaymentTerm());
					persistObj.setDocumentReq(rftEvent.getDocumentReq());
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
					rfpEventService.updateEvent(persistObj);
					LOG.info("rftEvent update:      " + persistObj);

					redir.addFlashAttribute("eventId", persistObj.getId());

				}

			} catch (Exception e) {
				LOG.error("Error while storing RfpEvent : " + e.getMessage(), e);
				model.addAttribute("event", rfpEventService.getRfpEventWithBuByeventId(eventId));
				// model.addAttribute("error", "Error while storing Rft Event details for : " +
				// persistObj.getEventName() + ", message : " + e.getMessage());
				model.addAttribute("error", messageSource.getMessage("error.while.storing.rftevent", new Object[] { persistObj.getEventName(), e.getMessage() }, Global.LOCALE));
				model.addAttribute("businessUnitList", businessUnitService.getPlainActiveBusinessUnitForTenant(SecurityLibrary.getLoggedInUserTenantId()));
				model.addAttribute("procurementMethodList", procurementMethodService.getAllActiveProcurementMethod(SecurityLibrary.getLoggedInUserTenantId()));
				model.addAttribute("procurementCategoryList", procurementCategoriesService.getAllActiveProcurementCategory(SecurityLibrary.getLoggedInUserTenantId()));
				return new ModelAndView("eventDescription", "event", rfpEventService.getRfpEventWithBuByeventId(eventId));
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
	public @ResponseBody ResponseEntity<List<RfpEventContact>> saveContact(@ModelAttribute RfpEventContact rftEventContact, BindingResult result, Model model) throws JsonProcessingException {
		LOG.info("Save Rft event Contact Called " + rftEventContact.getEventId());
		HttpHeaders headers = new HttpHeaders();
		try {

			if (result.hasErrors()) {
				for (ObjectError err : result.getAllErrors()) {
					headers.add("error", err.getDefaultMessage());
				}
				return new ResponseEntity<List<RfpEventContact>>(null, headers, HttpStatus.EXPECTATION_FAILED);
			} else {
				LOG.info("Validation success");
				if (StringUtils.checkString(rftEventContact.getEventId()).length() > 0) {
					if (rftEventContact.getComunicationEmail() != null || rftEventContact.getContactNumber() != null || rftEventContact.getMobileNumber() != null || rftEventContact.getFaxNumber() != null) {
						if (doValidate(rftEventContact)) {
							RfpEvent rftEvent = rfpEventService.getEventById(rftEventContact.getEventId());
							if (StringUtils.checkString(rftEventContact.getId()).length() == 0) {
								LOG.info("evennt in contact :  " + rftEvent.getEventName());
								rftEventContact.setRfxEvent(rftEvent);
								rfpEventService.saveEventContact(rftEventContact);
								LOG.info("Save Contact Called" + SecurityLibrary.getLoggedInUser());
								headers.add("success", "Successfully added contact details");
							} else {
								LOG.info("upadte event contact");
								RfpEventContact persistObj = rfpEventService.getEventContactById(rftEventContact.getId());
								persistObj.setRfxEvent(rftEvent);
								persistObj.setComunicationEmail(rftEventContact.getComunicationEmail());
								persistObj.setContactName(rftEventContact.getContactName());
								persistObj.setContactNumber(rftEventContact.getContactNumber());
								persistObj.setDesignation(rftEventContact.getDesignation());
								persistObj.setFaxNumber(rftEventContact.getFaxNumber());
								persistObj.setMobileNumber(rftEventContact.getMobileNumber());
								persistObj.setTitle(rftEventContact.getTitle());
								rfpEventService.updateRfpEventContact(persistObj);
								LOG.info("upadte event contact  " + persistObj.toLogString());
								headers.add("success", "Successfully updated contact details");
							}

						} else {
							headers.add("error", "Contact name has already Exist");
							return new ResponseEntity<List<RfpEventContact>>(null, headers, HttpStatus.EXPECTATION_FAILED);
						}
					} else {
						headers.add("error", "Please select one of Contact number, Mobile number, Communication email.");
						return new ResponseEntity<List<RfpEventContact>>(null, headers, HttpStatus.EXPECTATION_FAILED);
					}

				}
			}
		} catch (Exception e) {
			LOG.error("Error While Save the Creation" + e.getMessage(), e);
			headers.add("error", e.getMessage());
			return new ResponseEntity<List<RfpEventContact>>(null, headers, HttpStatus.EXPECTATION_FAILED);
		}

		List<RfpEventContact> rftContactList = rfpEventService.getAllContactForEvent(rftEventContact.getEventId());

		return new ResponseEntity<List<RfpEventContact>>(rftContactList, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "mangeEventRequirement", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Integer> mangeEventRequirement(@RequestParam("eventId") String eventId, @RequestParam("eventRequirement") String eventRequirement) {
		LOG.info("Requirment Event Id: " + eventId + " the Evnt button :  " + eventRequirement);

		if (eventRequirement.equals("documentReq")) {
			Integer countDocument = rfpEventService.getCountOfRftDocumentByEventId(eventId);
			LOG.info("countDocument :  " + countDocument);
			if (countDocument > 0) {
				return new ResponseEntity<Integer>(countDocument, HttpStatus.EXPECTATION_FAILED);
			}
		}
		if (eventRequirement.equals("meetingReq")) {
			Integer countMeeting = rfpMeetingService.getCountOfRfpMeetingByEventId(eventId);
			if (countMeeting > 0) {
				return new ResponseEntity<Integer>(countMeeting, HttpStatus.EXPECTATION_FAILED);
			}
		}
		if (eventRequirement.equals("questionnaires")) {
			Integer countQuestionnaires = rfpEventService.getCountOfRfpCqByEventId(eventId);
			if (countQuestionnaires > 0) {
				return new ResponseEntity<Integer>(countQuestionnaires, HttpStatus.EXPECTATION_FAILED);
			}
		}
		if (eventRequirement.equals("billOfQuantity")) {
			Integer countBillOfQuantity = rfpBqService.getCountOfBqByEventId(eventId);
			LOG.info("billOfQuantity :  " + countBillOfQuantity);
			if (countBillOfQuantity > 0) {
				return new ResponseEntity<Integer>(countBillOfQuantity, HttpStatus.EXPECTATION_FAILED);
			}
		}
		if(eventRequirement.equals("scheduleOfRate")) {
			Integer countBillOfQuantity = rfpSorService.getCountOfSorByEventId(eventId);
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

		if (eventRequirement.equals("documentReq")) {
			rfpDocumentService.deleteAllDocuments(eventId, eventRequirement);
		}
		if (eventRequirement.equals("meetingReq")) {
			rfpMeetingService.deleteAllRfpMeetings(eventId, eventRequirement);
		}
		if (eventRequirement.equals("questionnaires")) {
			rfpCqService.deleteAllCqs(eventId, eventRequirement);
		}
		if (eventRequirement.equals("billOfQuantity")) {
			rfpBqService.deleteAllBq(eventId, eventRequirement);
		}
		if (eventRequirement.equals("scheduleOfRate")) {
			rfpSorService.deleteAllSor(eventId, eventRequirement);
		}

		return new ResponseEntity<String>(HttpStatus.OK);
	}

	@RequestMapping(path = "/saveAsDraft", method = RequestMethod.POST)
	public ModelAndView saveAsDraft(@ModelAttribute RfpEvent rfpEvent, @RequestParam(value = "industryCateg[]") String[] industryCategories, Model model, BindingResult result, RedirectAttributes redir, HttpSession session) {

		if (rfpEvent.getViewSupplerName() == true) {
			rfpEvent.setViewSupplerName(false);
		} else {
			rfpEvent.setViewSupplerName(true);
		}

		model.addAttribute("eventPermissions", rfpEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), rfpEvent.getId()));
		return saveDetails(rfpEvent, model, result, redir, false, session, true, industryCategories);
	}

	@RequestMapping(path = "/saveAsDraftDescription", method = RequestMethod.POST)
	public ModelAndView saveAsDraftDescription(@ModelAttribute RfpEvent rfpEvent, Model model, BindingResult result, RedirectAttributes redir) {
		model.addAttribute("eventPermissions", rfpEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), rfpEvent.getId()));

		return saveRftDescription(rfpEvent, model, result, redir, false);
	}

	@RequestMapping(path = "/editContact", method = RequestMethod.POST)
	public ResponseEntity<RfpEventContact> editRftContact(@RequestParam(value = "contactId") String contactId, Model model) {
		LOG.info("Getting the editContact. : " + contactId);
		RfpEventContact eventContact = rfpEventService.getEventContactById(contactId);
		LOG.info("event contact   :  " + eventContact);
		model.addAttribute("btnValue", "Update");
		return new ResponseEntity<RfpEventContact>(eventContact, HttpStatus.OK);
	}

	@RequestMapping(path = "/deleteContact", method = RequestMethod.POST)
	public ResponseEntity<List<RfpEventContact>> deleteContact(@RequestParam(value = "contactId") String contactId, @RequestParam(value = "eventId") String eventId, Model model) {
		LOG.info("Getting the Delete Contact. : " + contactId);
		RfpEventContact eventContact = rfpEventService.getEventContactById(contactId);
		HttpHeaders headers = new HttpHeaders();
		List<RfpEventContact> rfpContactList = null;
		LOG.info("event contact   :  " + eventContact);
		model.addAttribute("btnValue", "Update");
		try {
			rfpEventService.deleteContact(eventContact);
			LOG.info("Delete the contact");
			headers.add("success", "Contact removed Successfully");
		} catch (Exception e) {
			LOG.error("Error while removing Contact from event . " + e.getMessage(), e);
			headers.add("error", "Contact removed unsuccessfull");
			return new ResponseEntity<List<RfpEventContact>>(rfpContactList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		rfpContactList = rfpEventService.getAllContactForEvent(eventId);
		return new ResponseEntity<List<RfpEventContact>>(rfpContactList, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/addReminderOfEvent", method = RequestMethod.POST)
	public ResponseEntity<List<RfpReminder>> addReminderOfEvent(@RequestParam(value = "reminderDuration") String reminderDuration, @RequestParam(value = "dateRangeData") String dateRangeData, @RequestParam(value = "reminderDurationType") IntervalType reminderDurationType, @RequestParam(value = "eventId") String eventId, @RequestParam(value = "reminderId") String reminderId, @RequestParam(value = "reminderNotifyType") String reminderNotifyType, Model model, HttpSession session) {
		LOG.info("Getting the AddReminder : " + eventId);
		HttpHeaders headers = new HttpHeaders();
		try {
			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}

			if (StringUtils.checkString(eventId).length() > 0) {
				RfpEvent rfpEvent = rfpEventService.getEventById(eventId);
				LOG.info("event in reminder save :  ");
				if (StringUtils.checkString(reminderId).length() == 0) {
					RfpReminder rfpReminder = new RfpReminder();
					if (StringUtils.checkString(dateRangeData).length() > 0) {
						String visibilityDates[] = dateRangeData.split("-");
						DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
						formatter.setTimeZone(timeZone);
						Date startDate = (Date) formatter.parse(visibilityDates[0]);
						Date endDate = (Date) formatter.parse(visibilityDates[1]);

						Calendar cal = Calendar.getInstance(timeZone);
						if (StringUtils.checkString(reminderNotifyType).equalsIgnoreCase("Start")) {
							cal.setTime(startDate);
							rfpReminder.setStartReminder(Boolean.TRUE);
						} else {
							cal.setTime(endDate);
						}
						if (reminderDurationType == IntervalType.DAYS) {
							cal.add(Calendar.DATE, -Integer.parseInt(reminderDuration));
							rfpReminder.setIntervalType(IntervalType.DAYS);
							LOG.info("Reminder : " + formatter.format(cal.getTime()));
						} else {
							cal.add(Calendar.HOUR, -Integer.parseInt(reminderDuration));
							rfpReminder.setIntervalType(IntervalType.HOURS);
							LOG.info("Reminder  Hous: " + formatter.format(cal.getTime()));
						}

						rfpReminder.setReminderDate(cal.getTime());
						List<RfpReminder> reminderList = rfpEventService.getAllRfpEventReminderForEvent(eventId);
						if (CollectionUtil.isNotEmpty(reminderList)) {
							for (RfpReminder rfpReminderCompare : reminderList) {
								if (rfpReminder.getReminderDate().compareTo(rfpReminderCompare.getReminderDate()) == 0 && (rfpReminder.getStartReminder() == rfpReminderCompare.getStartReminder())) {
									headers.add("error", "There is another reminder on this date is exists");
									return new ResponseEntity<List<RfpReminder>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
								}
							}
						}
						// if (rfpReminder.getReminderDate().before(startDate)) {
						// headers.add("error", "Reminder date/time should not be less than event start date");
						// return new ResponseEntity<List<RfpReminder>>(null, headers,
						// HttpStatus.INTERNAL_SERVER_ERROR);
						// }
						if (cal.getTime().compareTo(new Date()) < 0) {
							headers.add("error", messageSource.getMessage("event.reminder.pastdate", new Object[] {}, Global.LOCALE));
							return new ResponseEntity<List<RfpReminder>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
						}
						rfpEvent.setEventStart(startDate);
						rfpEvent.setEventEnd(endDate);
						rfpEventService.updateRfpEvent(rfpEvent);

						rfpReminder.setRfxEvent(rfpEvent);
						rfpReminder.setInterval(Integer.parseInt(reminderDuration));
						LOG.info(rfpReminder);
						rfpEventService.saveRfpEventReminder(rfpReminder);
					}
				}
			}
		} catch (Exception e) {
			LOG.error("Error While Save the Reminder" + e.getMessage(), e);
			return new ResponseEntity<List<RfpReminder>>(null, headers, HttpStatus.EXPECTATION_FAILED);
		}
		List<RfpReminder> reminderList = rfpEventService.getAllRfpEventReminderForEvent(eventId);
		return new ResponseEntity<List<RfpReminder>>(reminderList, HttpStatus.OK);
	}

	@RequestMapping(path = "/deleteReminder", method = RequestMethod.POST)
	public ResponseEntity<List<RfpReminder>> deleteRfpReminder(@RequestParam(value = "reminderId") String reminderId, @RequestParam(value = "eventId") String eventId, Model model) {
		LOG.info("Getting the Delete reminder : " + reminderId);
		RfpReminder eventReminder = rfpEventService.getRfpEventReminderById(reminderId);
		LOG.info("event eventReminder   :  " + eventReminder);
		model.addAttribute("btnValue", "Update");
		rfpEventService.deleteRfpReminder(eventReminder);
		LOG.info("Delete the Reminder");

		List<RfpReminder> reminderList = rfpEventService.getAllRfpEventReminderForEvent(eventId);
		return new ResponseEntity<List<RfpReminder>>(reminderList, HttpStatus.OK);
	}

	@RequestMapping(path = "/addTeamMemberToList", method = RequestMethod.POST)
	public ResponseEntity<List<EventTeamMember>> addTeamMemberToList(@RequestParam(value = "eventId") String eventId, @RequestParam(value = "userId") String userId, @RequestParam(value = "memberType") TeamMemberType memberType) {
		return super.addTeamMemberToList(eventId, userId, memberType);
	}

	@RequestMapping(path = "/removeTeamMemberfromList", method = RequestMethod.POST)
	public ResponseEntity<List<User>> removeTeamMemberfromList(@RequestParam(value = "userId") String userId, @RequestParam(value = "eventId") String eventId) {
		return super.removeTeamMemberfromList(eventId, userId);
	}

	@RequestMapping(path = "/manageEventVisibility", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Integer> manageEventVisibility(@RequestParam("eventId") String eventId, @RequestParam("eventVisbility") EventVisibilityType eventVisbility) {
		LOG.info("Requirment Event Id: " + eventId + " the Evnt button :  " + eventVisbility);
		if (eventVisbility == (EventVisibilityType.PUBLIC)) {
			Integer countMeeting = rfpMeetingService.getCountOfRfpMeetingByEventId(eventId);
			Integer countEventSupplier = rfpEventSupplierService.getCountOfSupplierByEventId(eventId);

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
			rfpMeetingService.deleteAllRfpMeetings(eventId, null);
			rfpEventSupplierService.deleteAllSuppliersByEventId(eventId);
		}
		return new ResponseEntity<String>(HttpStatus.OK);
	}

	@RequestMapping(path = "/manageReminderOnDateChange", method = RequestMethod.POST)
	public ResponseEntity<List<RfpReminder>> manageReminderOnDateChange(@RequestParam(value = "dateRangeData") String dateRangeData, @RequestParam(value = "eventId") String eventId, @RequestParam(value = "reminderId", required = false) String reminderId, Model model, HttpSession session) {
		LOG.info("Getting the AddReminder acoordig to event date : " + eventId);
		HttpHeaders headers = new HttpHeaders();
		try {

			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}
			if (StringUtils.checkString(eventId).length() > 0) {

				RfpEvent event = rfpEventService.getEventById(eventId);

				LOG.info("evennt in reminder save :  ");
				List<RfpReminder> reminderList = rfpEventService.getAllRfpEventReminderForEvent(eventId);
				if (CollectionUtil.isNotEmpty(reminderList)) {
					for (RfpReminder reminder : reminderList) {
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
							return new ResponseEntity<List<RfpReminder>>(reminderList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
						}
						event.setEventStart(startDate);
						event.setEventEnd(endDate);
						rfpEventService.updateRfpEvent(event);

						reminder.setReminderDate(cal.getTime());
						rfpEventService.updateEventReminder(reminder);
					}
				}
			}
		} catch (Exception e) {
			LOG.error("Error While Save the Country" + e.getMessage(), e);
			return new ResponseEntity<List<RfpReminder>>(null, headers, HttpStatus.EXPECTATION_FAILED);
		}
		List<RfpReminder> reminderList = rfpEventService.getAllRfpEventReminderForEvent(eventId);
		return new ResponseEntity<List<RfpReminder>>(reminderList, HttpStatus.OK);
	}

	private boolean doValidate(RfpEventContact rfpEventContact) {
		boolean validate = true;
		if (rfpEventService.isExists(rfpEventContact)) {
			validate = false;
		}
		return validate;
	}

	@RequestMapping(path = "/autoSaveDraft", method = RequestMethod.POST)
	public @ResponseBody void autoSaveDraft(@ModelAttribute("event") RfpEvent rfpEvent, @RequestParam("industryCateg[]") String[] industryCategory, BindingResult result, HttpSession session, Model model) {
		LOG.info("autoSave Draft Industry Cat " + industryCategory);
		if (rfpEvent.getViewSupplerName() == true) {
			rfpEvent.setViewSupplerName(false);
		} else {
			rfpEvent.setViewSupplerName(true);
		}

		if (rfpEvent.getStatus() == EventStatus.DRAFT) {
			rfpEventService.autoSaveRfpDetails(rfpEvent, industryCategory, result, (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY));
		}
	}

	@RequestMapping(path = "/autoSaveDraftDesc", method = RequestMethod.POST)
	public @ResponseBody void autoSaveDraftDesc(@ModelAttribute("event") RfpEvent rfpEvent, BindingResult result, HttpSession session, Model model) {
		saveRfiDescription(rfpEvent);
	}

	private void saveRfiDescription(RfpEvent rfiEvent) {
		LOG.info("Save Rft Event Description Called ");
		RfpEvent persistObj = null;
		/* rfiEvent.setBillOfQuantity(Boolean.FALSE); */
		String eventId = rfiEvent.getId();

		try {
			if (StringUtils.checkString(eventId).length() > 0) {
				LOG.info("rftEventDescription update:      " + eventId);

				persistObj = rfpEventService.getEventById(eventId);
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
				rfpEventService.updateRfpEvent(persistObj);
				LOG.info("rftEvent update:      " + persistObj);
			}

		} catch (Exception e) {
			LOG.error("Error while storing RfiEvent : " + e.getMessage(), e);
		}

	}

}
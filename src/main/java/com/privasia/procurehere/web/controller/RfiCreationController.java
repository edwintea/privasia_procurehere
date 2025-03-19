package com.privasia.procurehere.web.controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpSession;

import com.privasia.procurehere.core.entity.*;

import com.privasia.procurehere.service.RfiSorService;
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
import com.privasia.procurehere.service.RfiCqService;
import com.privasia.procurehere.service.RfiDocumentService;
import com.privasia.procurehere.service.RfiEnvelopService;
import com.privasia.procurehere.service.RfiEventService;
import com.privasia.procurehere.service.RfiEventSupplierService;
import com.privasia.procurehere.service.RfiMeetingService;
import com.privasia.procurehere.web.editors.ProcurementCategoriesEditor;
import com.privasia.procurehere.web.editors.ProcurementMethodEditor;
import com.privasia.procurehere.web.editors.RfiApprovalEditor;
import com.privasia.procurehere.web.editors.RfiSuspensionApprovalEditor;
import com.privasia.procurehere.web.editors.RfiUnMaskedUserEditor;

@Controller
@RequestMapping("/buyer/RFI")
public class RfiCreationController extends EventCreationBase {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Autowired
	RfiCqService rfiCqService;
	@Autowired
	ErpSetupService erpSetupService;
	@Autowired
	RfiDocumentService rfiDocumentService;

	@Autowired
	RfiMeetingService rfiMeetingService;

	@Autowired
	RfiApprovalEditor rfiApprovalEditor;

	@Autowired
	RfiUnMaskedUserEditor rfiUnMaskedUserEditor;

	@Autowired
	RfiEventService eventService;

	@Autowired
	RfiEventSupplierService rfiEventSupplierService;

	@Autowired
	RfiEnvelopService rfiEnvelopService;
	
	@Autowired
	RfiSuspensionApprovalEditor rfiSuspensionApprovalEditor;

	@Autowired
	ProcurementMethodService procurementMethodService;

	@Autowired
	ProcurementCategoriesService procurementCategoriesService;

	@Autowired
	ProcurementMethodEditor procurementMethodEditor;

	@Autowired
	ProcurementCategoriesEditor procurementCategoriesEditor;

	@Autowired
	RfiSorService rfiSorService;

	public RfiCreationController() {
		super(RfxTypes.RFI);
	}

	@InitBinder
	public void initBinder(WebDataBinder binder, HttpSession session) {
		super.initBinder(binder, session);
		binder.registerCustomEditor(RfiApprovalUser.class, rfiApprovalEditor);
		binder.registerCustomEditor(ProcurementMethod.class, procurementMethodEditor);
		binder.registerCustomEditor(ProcurementCategories.class, procurementCategoriesEditor);
		binder.registerCustomEditor(RfiSuspensionApprovalUser.class, rfiSuspensionApprovalEditor);

		/*
		 * binder.registerCustomEditor(List.class, "approvalUsers", new CustomCollectionEditor(List.class) { protected
		 * Object convertElement(Object element) { if (element != null) { String id = (String) element; User user =
		 * userService.findUserById(id); return new RfiApprovalUser(user); } return null; } });
		 */
	}

	@RequestMapping(path = "/createRfiEvent", method = RequestMethod.GET)
	public String createRfiEvent(Model model, RedirectAttributes redir) {
		try {
			RfiEvent rfiEvent = new RfiEvent();
			rfiEvent.setBillOfQuantity(Boolean.FALSE);
			rfiEvent.setCreatedBy(SecurityLibrary.getLoggedInUser());
			rfiEvent.setCreatedDate(new Date());

			ErpSetup erpSetup = erpSetupService.getErpConfigBytenantId(SecurityLibrary.getLoggedInUser().getTenantId());
			if (erpSetup != null) {
				LOG.info("--------erp flag set for event-----------");
				rfiEvent.setErpEnable(erpSetup.getIsErpEnable());

			} else {
				rfiEvent.setErpEnable(false);
			}

			rfiEvent.setEventId(eventIdSettingDao.generateEventIdByBusinessUnit(SecurityLibrary.getLoggedInUserTenantId(), "RFI", null));
			rfiEvent.setStatus(EventStatus.DRAFT);
			rfiEvent = rfiEventService.saveRfiEvent(rfiEvent);
			model.addAttribute("eventPermissions", eventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), rfiEvent.getId()));
			rfiEventService.setDefaultEventContactDetail(SecurityLibrary.getLoggedInUser().getId(), rfiEvent.getId());
			return "redirect:createEventDetails/" + rfiEvent.getId();
		} catch (SubscriptionException e) {
			LOG.error("You have reached your subscription limit. " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("reached.subscription.limit", new Object[] { e.getMessage() }, Global.LOCALE));
			return "redirect:/buyer/buyerDashboard";
		} catch (ApplicationException e) {
			LOG.error("Error : " + e.getMessage(), e);
			redir.addFlashAttribute("error", e.getMessage());

			return "redirect:/buyer/createEvent/" + RfxTypes.RFI.name();
			// return "redirect:/buyer/buyerDashboard";
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);

			return "redirect:/buyer/createEvent/" + RfxTypes.RFI.name();
		}
	}

	@RequestMapping(path = "/createRfiEvent", method = RequestMethod.POST)
	public String createRfiEventUsingBU(Model model, RedirectAttributes redir, @RequestParam(value = "businessUnitId", required = false) String businessUnitId) {
		try {
			RfiEvent rfiEvent = new RfiEvent();
			rfiEvent.setBillOfQuantity(Boolean.FALSE);
			rfiEvent.setCreatedBy(SecurityLibrary.getLoggedInUser());
			rfiEvent.setCreatedDate(new Date());
			LOG.info("-----------------------businessUnitId----------" + businessUnitId);
			BusinessUnit businessUnit = null;
			if (StringUtils.isNotBlank(businessUnitId)) {
				businessUnit = businessUnitService.getBusinessUnitById(businessUnitId);
				rfiEvent.setBusinessUnit(businessUnit);
			}

			ErpSetup erpSetup = erpSetupService.getErpConfigBytenantId(SecurityLibrary.getLoggedInUserTenantId());
			if (erpSetup != null) {
				LOG.info("--------erp flag set for event-----------");
				rfiEvent.setErpEnable(erpSetup.getIsErpEnable());
			} else {
				rfiEvent.setErpEnable(false);
			}
			rfiEvent.setEventId(eventIdSettingDao.generateEventIdByBusinessUnit(SecurityLibrary.getLoggedInUserTenantId(), "RFI", rfiEvent.getBusinessUnit()));

			rfiEvent.setStatus(EventStatus.DRAFT);
			rfiEvent = rfiEventService.saveRfiEvent(rfiEvent);
			model.addAttribute("eventPermissions", eventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), rfiEvent.getId()));
			rfiEventService.setDefaultEventContactDetail(SecurityLibrary.getLoggedInUser().getId(), rfiEvent.getId());
			return "redirect:createEventDetails/" + rfiEvent.getId();
		} catch (SubscriptionException e) {
			LOG.error("You have reached your subscription limit. " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("reached.subscription.limit", new Object[] { e.getMessage() }, Global.LOCALE));
		} catch (ApplicationException e) {
			LOG.error("Error : " + e.getMessage(), e);
			redir.addFlashAttribute("error", e.getMessage());

			// redirecr to create from blank or copyfrom template page
			return "redirect:/buyer/createEvent/" + RfxTypes.RFI.name();
			// return "redirect:/buyer/buyerDashboard";
		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
			// return "redirect:/buyer/buyerDashboard";
		}
		return "redirect:/buyer/buyerDashboard";
	}

	@RequestMapping(path = "/editEventDetails", method = RequestMethod.POST)
	public String editEventDetails(@RequestParam String eventId, Model model, RedirectAttributes redir) {
		model.addAttribute("eventPermissions", eventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
		try {
			return super.editEventDetails(eventId);
		} catch (SubscriptionException e) {
			LOG.error("Error :" + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("reached.subscription.limit", new Object[] { e.getMessage() }, Global.LOCALE));
			return "redirect:/buyer/buyerDashboard";
		}
	}

	@RequestMapping(path = "/eventCreationPrevious", method = RequestMethod.POST)
	public String eventCreationPrevious(@ModelAttribute RfiEvent rfiEvent, Model model) {
		model.addAttribute("eventPermissions", eventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), rfiEvent.getId()));
		return super.eventCreationPrevious(rfiEvent);
	}

	@RequestMapping(path = "/createEventDetails/{eventId}", method = RequestMethod.GET)
	public String createEventDetails(Model model, @PathVariable String eventId, HttpSession session) {

		EventPermissions eventPermissions = eventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
		model.addAttribute("eventPermissions", eventPermissions);
		return super.createEventDetailsPage(model, eventId, session, eventPermissions);
	}

	@RequestMapping(path = "/storeEventDetails", method = RequestMethod.POST)
	public ModelAndView saveEvent(@ModelAttribute RfiEvent rfiEvent, @RequestParam(value = "industryCateg[]") String[] industryCategories, Model model, BindingResult result, RedirectAttributes redir, HttpSession session) {
		if (rfiEvent.getViewSupplerName()) {
			rfiEvent.setViewSupplerName(false);
		} else {
			rfiEvent.setViewSupplerName(true);
		}
		model.addAttribute("eventPermissions", eventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), rfiEvent.getId()));
		return saveDetails(rfiEvent, model, result, redir, true, session, false, industryCategories);
	}

	/**
	 * @param model
	 * @param result
	 * @param redir
	 * @param industryCat
	 * @param rftEvent
	 * @return
	 */
	private ModelAndView saveDetails(RfiEvent rfiEvent, Model model, BindingResult result, RedirectAttributes redir, boolean goNext, HttpSession session, boolean isDraft, String[] industryCat) {
		String next = "";
		Date feeStartDate = null;
		boolean isSuspended = EventStatus.SUSPENDED.equals(rfiEvent.getStatus()) ? true: false;
		if (goNext) {
			next = "redirect:eventDescription/" + rfiEvent.getId();
		} else {
			redir.addFlashAttribute("success", messageSource.getMessage("rftEvent.success.savedraft", new Object[] { (rfiEvent.getEventName() != null ? rfiEvent.getEventName() : rfiEvent.getEventId()) }, Global.LOCALE));
			next = "redirect:createEventDetails/" + rfiEvent.getId();
		}

		TimeZone timeZone = TimeZone.getDefault();
		String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);

		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}
		DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		dateFormatter.setTimeZone(timeZone);

		if (StringUtils.checkString(rfiEvent.getExpectedTenderDateTimeRange()).length() > 0) {
			String expectedTenderDates[] = rfiEvent.getExpectedTenderDateTimeRange().split("-");
			try {
				rfiEvent.setExpectedTenderStartDate(dateFormatter.parse(expectedTenderDates[0]));
				rfiEvent.setExpectedTenderEndDate(dateFormatter.parse(expectedTenderDates[1]));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		LOG.info(rfiEvent.getFeeDateTimeRange());
		if (StringUtils.checkString(rfiEvent.getFeeDateTimeRange()).length() > 0) {
			String feeDates[] = rfiEvent.getFeeDateTimeRange().split("-");
			try {
				rfiEvent.setFeeStartDate(dateFormatter.parse(feeDates[0]));
				feeStartDate = dateFormatter.parse(feeDates[0]);
				rfiEvent.setFeeEndDate(dateFormatter.parse(feeDates[1]));
			} catch (ParseException e) {
				LOG.error(e.getMessage(), e);
			}
		}

		List<RfiReminder> reminderList = rfiEventService.getAllRfiEventReminderForEvent(rfiEvent.getId());
		model.addAttribute("reminderList", reminderList);
		List<RfiEventContact> eventContactsList = rfiEventService.getAllContactForEvent(rfiEvent.getId());
		model.addAttribute("eventContactsList", eventContactsList);
		model.addAttribute("evaluationDeclaratonList", declarationService.getDeclarationsByTypeForTenant(DeclarationType.EVALUATION_PROCESS, SecurityLibrary.getLoggedInUserTenantId()));
		model.addAttribute("supplierDeclaratonList", declarationService.getDeclarationsByTypeForTenant(DeclarationType.SUPPLIER_ACCEPTANCE, SecurityLibrary.getLoggedInUserTenantId()));
		model.addAttribute("eventPermissions", eventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), rfiEvent.getId()));
		RfiEventContact eventContact = new RfiEventContact();
		eventContact.setRfxEvent(rfiEvent);
		eventContact.setEventId(rfiEvent.getId());
		model.addAttribute("eventContact", eventContact);
		if (industryCat != null) {
			List<IndustryCategory> icList = new ArrayList<>();
			for (String industryCatId : industryCat) {
				LOG.info("industry cat Id :" + industryCatId);
				IndustryCategory ic = new IndustryCategory();
				ic.setId(industryCatId);
				icList.add(ic);
			}
			rfiEvent.setIndustryCategories(icList);
			if (rfiEvent.getStatus() == EventStatus.DRAFT) {
				checkToAddSupplier(rfiEvent.getId(), industryCat, rfiEvent.getIndustryCategories(), rfiEvent.getTemplate() != null ? rfiEvent.getTemplate().getId() : null);
			}
		}
		try {
			constructDefaultModel(model, rfiEvent, RfxTypes.RFI, null);
		} catch (JsonProcessingException e1) {
			LOG.info("Error :" + e1.getMessage(), e1);
		}

		RfiEvent eventObj = rfiEventService.loadRfiEventById(rfiEvent.getId());

		// to load assigned member at time of error
		// List<User> userTeamMemberList =
		// userService.fetchAllActiveUsersForTenant(SecurityLibrary.getLoggedInUserTenantId());
		// List<User> userTeamMemberList =
		// userService.fetchAllActiveNormalUsersForTenant(SecurityLibrary.getLoggedInUserTenantId());
		List<UserPojo> userListSumm = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", UserType.NORMAL_USER);
		List<User> userTeamMemberList = new ArrayList<User>();
		List<User> approvalUsers = new ArrayList<User>();
		List<User> suspApprovalUsers = new ArrayList<User>();
		List<UserPojo> maskingUserList = new ArrayList<UserPojo>();
		List<UserPojo> evaluationConclusionUserList = new ArrayList<UserPojo>();
		if (rfiEvent.getUnMaskedUser() != null) {
			maskingUserList.add(new UserPojo(rfiEvent.getUnMaskedUser().getId(), rfiEvent.getUnMaskedUser().getLoginId(), rfiEvent.getUnMaskedUser().getName(), rfiEvent.getUnMaskedUser().getTenantId(), rfiEvent.getUnMaskedUser().isDeleted(), rfiEvent.getUnMaskedUser().getCommunicationEmail(), rfiEvent.getUnMaskedUser().getEmailNotifications()));
		}
		for (UserPojo user : userListSumm) {
			User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(),  user.getTenantId(), user.isDeleted());
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

		if (rfiEvent.getApprovals() != null) {
			for (RfiEventApproval app : rfiEvent.getApprovals()) {
				for (RfiApprovalUser appU : app.getApprovalUsers()) {
					if (!approvalUsers.contains(appU.getUser())) {
						approvalUsers.add(appU.getUser());
					}
				}
			}
		}
		
		if (rfiEvent.getSuspensionApprovals() != null) {
			for (RfiEventSuspensionApproval SuspApp : rfiEvent.getSuspensionApprovals()) {
				for (RfiSuspensionApprovalUser suspAppU : SuspApp.getApprovalUsers()) {
					if (!suspApprovalUsers.contains(suspAppU.getUser())) {
						suspApprovalUsers.add(suspAppU.getUser());
					}
				}
			}
		}

		List<RfiUnMaskedUser> unMaskedUsers = rfiEvent.getUnMaskedUsers();
		if (CollectionUtil.isNotEmpty(unMaskedUsers)) {
			for (RfiUnMaskedUser user : unMaskedUsers) {
				user.setEvent(rfiEvent);
				UserPojo u = new UserPojo(user.getUser().getId(), user.getUser().getLoginId(), user.getUser().getName(), user.getUser().getTenantId(), user.getUser().isDeleted(), user.getUser().getCommunicationEmail(), user.getUser().getEmailNotifications());
				if (!maskingUserList.contains(u)) {
					maskingUserList.add(u);
				}
			}
		}

		List<RfiEvaluationConclusionUser> evaluationConclusionUsers = rfiEvent.getEvaluationConclusionUsers();
		if (CollectionUtil.isNotEmpty(evaluationConclusionUsers)) {
			for (RfiEvaluationConclusionUser user : evaluationConclusionUsers) {
				user.setEvent(rfiEvent);
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
		LOG.info("User member in withourt :  " + userTeamMemberList.size());
		if (CollectionUtil.isNotEmpty(eventObj.getTeamMembers())) {
			List<User> assignedTeamMembers = new ArrayList<>();
			List<RfiTeamMember> userTeamMembers = new ArrayList<>();
			for (RfiTeamMember rfiTeamMember : eventObj.getTeamMembers()) {
				try {
					assignedTeamMembers.add((User) rfiTeamMember.getUser().clone());
					userTeamMembers.add(rfiTeamMember);
				} catch (Exception e) {
					LOG.error("Error :  " + e.getMessage(), e);
				}
			}
			rfiEvent.setTeamMembers(userTeamMembers);
			LOG.info("User member in if condition :  " + userTeamMemberList.size());
			userTeamMemberList.removeAll(assignedTeamMembers);
			model.addAttribute("userTeamMemberList", userTeamMemberList);
		} else {
			model.addAttribute("userTeamMemberList", userTeamMemberList);
			LOG.info("User member in else condition :  " + userTeamMemberList.size());

		}

		if (rfiEvent.getTemplate().getId() != null) {
			LOG.info("inside to check template");
			RfxTemplate rfxTemplate = rfxTemplateService.getRfxTemplateForEditById(rfiEvent.getTemplate().getId(), SecurityLibrary.getLoggedInUserTenantId());

			if (!rfxTemplate.getVisibleAddSupplier()) {
				rfiEvent.setAddSupplier(rfxTemplate.getAddSupplier());
			}
			if (!rfxTemplate.getVisibleCloseEnvelope()) {
				rfiEvent.setCloseEnvelope(rfxTemplate.getCloseEnvelope());
			}
			if (!rfxTemplate.getVisibleViewSupplierName()) {
				rfiEvent.setViewSupplerName(rfxTemplate.getViewSupplerName());
			}

			if (!rfxTemplate.getVisibleAllowToSuspendEvent()) {
				rfiEvent.setAllowToSuspendEvent(rfxTemplate.getAllowToSuspendEvent());
			}

			rfiEvent.setRfxEnvelopeOpening(rfiEvent.getRfxEnvelopeOpening());
			rfiEvent.setRfxEnvOpeningAfter(rfiEvent.getRfxEnvOpeningAfter());
			rfiEvent.setAllowDisqualifiedSupplierDownload(rfiEvent.getAllowDisqualifiedSupplierDownload());

			model.addAttribute("templateFields", rfxTemplate.getFields());
			model.addAttribute("rfxTemplate", rfxTemplate);
		}
		if (result.hasErrors()) {
			LOG.error("Page submitted With Errors ............................. ");
			List<String> errMessages = new ArrayList<String>();
			for (ObjectError err : result.getAllErrors()) {
				errMessages.add(err.getDefaultMessage());
				LOG.info("ERROR : " + err.getDefaultMessage());
			}
			return new ModelAndView("createEventDetails", "event", rfiEvent);
		} else {
			LOG.info("Page submitted with no errors ....................................... ");
			try {

				LOG.info("rfiEvent.getApprovals(): " + rfiEvent.getApprovals());
				if (CollectionUtil.isNotEmpty(rfiEvent.getApprovals())) {
					int level = 1;
					for (RfiEventApproval app : rfiEvent.getApprovals()) {
						app.setEvent(rfiEvent);
						app.setLevel(level++);
						if (app != null && CollectionUtil.isNotEmpty(app.getApprovalUsers()))
							for (RfiApprovalUser approvalUser : app.getApprovalUsers()) {
								approvalUser.setApproval(app);
								approvalUser.setId(null);
							}
					}
				} else {
					LOG.warn("Approval levels is empty.");
				}

				if (StringUtils.checkString(rfiEvent.getId()).length() > 0) {
					LOG.info("rfiEvent update:      " + rfiEvent.getId());

					boolean eventCatError = false;
					if (!isDraft && CollectionUtil.isEmpty(rfiEvent.getIndustryCategories())) {
						model.addAttribute("error", messageSource.getMessage("event.one.industy.required", new Object[] {}, Global.LOCALE));
						if (rfiEvent.getViewSupplerName()) {
							rfiEvent.setViewSupplerName(false);
						} else {
							rfiEvent.setViewSupplerName(true);
						}
						LOG.error("Error: " + messageSource.getMessage("event.one.industy.required", new Object[] {}, Global.LOCALE));
						eventCatError = true;
					}
					if (!doValidate(rfiEvent, model, isDraft) && !eventCatError) {
						RfiEvent persistObj = rfiEventService.getRfiEventById(rfiEvent.getId());
						if (rfiEvent.getEventVisibilityDates() != null) {
							String visibilityDates[] = rfiEvent.getEventVisibilityDates().split("-");
							DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
							formatter.setTimeZone(timeZone);
							Date startDate = (Date) formatter.parse(visibilityDates[0]);
							Date endDate = (Date) formatter.parse(visibilityDates[1]);
							rfiEvent.setEventEnd(endDate);
							rfiEvent.setEventStart(startDate);
							if (CollectionUtil.isNotEmpty(reminderList)) {
								for (RfiReminder reminder : reminderList) {
									reminder.getReminderDate();
									formatter.setTimeZone(timeZone);
									Calendar cal = Calendar.getInstance(timeZone);
									if (reminder.getStartReminder() != null && reminder.getStartReminder() == Boolean.TRUE) {
										cal.setTime(startDate);
										if(reminder.getReminderDate().compareTo(startDate) > 0) {
											model.addAttribute("error", messageSource.getMessage("event.reminder.pastdate", new Object[] {}, Global.LOCALE));
											rfiEvent.setViewSupplerName(rfiEvent.getViewSupplerName() ? false : true);
											return new ModelAndView("createEventDetails", "event", rfiEvent);
										}
									} else {
										cal.setTime(endDate);
										if(reminder.getReminderDate().compareTo(endDate) > 0) {
											model.addAttribute("error", messageSource.getMessage("event.reminder.pastdate", new Object[] {}, Global.LOCALE));
											rfiEvent.setViewSupplerName(rfiEvent.getViewSupplerName() ? false : true);
											return new ModelAndView("createEventDetails", "event", rfiEvent);
										}
									}
									if (reminder.getIntervalType() == IntervalType.DAYS) {
										cal.add(Calendar.DATE, -(reminder.getInterval()));
										LOG.info("Reminder : " + formatter.format(cal.getTime()));
									} else {
										cal.add(Calendar.HOUR, -(reminder.getInterval()));
										LOG.info("Reminder  Hous: " + formatter.format(cal.getTime()));
									}

									if(EventStatus.SUSPENDED == rfiEvent.getStatus() && reminder.getStartReminder()) {
										continue;
									}
									else if (cal.getTime().compareTo(new Date()) < 0) {
										model.addAttribute("error", messageSource.getMessage("event.reminder.pastdate", new Object[] {}, Global.LOCALE));
										rfiEvent.setViewSupplerName(rfiEvent.getViewSupplerName() ? false : true);
										return new ModelAndView("createEventDetails", "event", rfiEvent);
									}
								}
							}
							LOG.info("Event Start date : " + startDate + " End Date : " + endDate);
						}

						if (persistObj.getMeetingReq()) {
							List<RfiEventMeeting> eventMeetings = rfiMeetingService.getAllRfiMeetingByEventId(persistObj.getId());
							if (CollectionUtil.isNotEmpty(eventMeetings)) {
								Date meetingMinDate = rfiMeetingService.getMinMeetingDateForEvent(persistObj.getId());
								if (rfiEvent.getEventPublishDate() != null && meetingMinDate != null && rfiEvent.getEventPublishDate().after(meetingMinDate)) {
									redir.addFlashAttribute("warn", messageSource.getMessage("rftEvent.error.minmeetingdate", new Object[] { dateFormatter.format(meetingMinDate) }, Global.LOCALE));
									// return new ModelAndView("createEventDetails", "event", rfiEvent);
									LOG.info("warn:" + messageSource.getMessage("rftEvent.error.minmeetingdate", new Object[] { dateFormatter.format(meetingMinDate) }, Global.LOCALE));
								}

								Date meetingMaxDate = rfiMeetingService.getMaxMeetingDateForEvent(persistObj.getId());
								if (persistObj.getEventEnd() != null && meetingMaxDate != null && persistObj.getEventEnd().before(meetingMaxDate)) {
									LOG.info("Meeting max date :  " + persistObj.getEventEnd().before(meetingMaxDate));
									redir.addFlashAttribute("error", messageSource.getMessage("rftEvent.error.maxmeetingdate", new Object[] { dateFormatter.format(meetingMaxDate) }, Global.LOCALE));
									// return new ModelAndView("createEventDetails", "event", rfiEvent);
								}
							}
						}

						Date publishDateTime = null;
						if (rfiEvent.getEventPublishDate() != null && rfiEvent.getEventPublishTime() != null) {
							publishDateTime = DateUtil.combineDateTime(rfiEvent.getEventPublishDate(), rfiEvent.getEventPublishTime(), timeZone);
						}
						LOG.info("publist date : " + publishDateTime + "   stratas date : " + rfiEvent.getEventStart());
						rfiEvent.setEventPublishDate(publishDateTime);
						List<RfiEventContact> contactList = rfiEventService.getAllContactForEvent(persistObj.getId());
						LOG.info("Contacts + " + contactList.size());
						/*
						 * if (!isDraft && CollectionUtil.isEmpty(contactList)) { model.addAttribute("error",
						 * messageSource.getMessage("rftEvent.error.contact", new Object[] { rfiEvent.getEventName() },
						 * Global.LOCALE)); return new ModelAndView("createEventDetails", "event", rfiEvent); }
						 */

						// envelope null when flag change false

						List<RfiEnvelop> envlope = rfiEnvelopService.getAllEnvelopByEventId(rfiEvent.getId(), SecurityLibrary.getLoggedInUser());
						if (rfiEvent.getRfxEnvelopeOpening().equals(Boolean.FALSE)) {
							if (CollectionUtil.isNotEmpty(envlope)) {
								for (RfiEnvelop rfiEnvelop : envlope) {
									rfiEnvelop.setEnvelopSequence(null);
									rfiEnvelopService.updateRfiEnvelope(rfiEnvelop);
								}
							}

						}

						LOG.info("Event Status():" + rfiEvent.getStatus());
						if (rfiEvent.getStatus() == EventStatus.DRAFT) {
							LOG.info("Event Status():" + rfiEvent.getStatus());
							if (!isDraft && rfiEvent.getEventPublishDate() != null && rfiEvent.getEventPublishDate().before(new Date())) {
								// Published date cannot be in the past
								Date publishDate = rfiEvent.getEventPublishDate();
								LOG.info("EVENT PUBLISHDATE:" + publishDate);
								SimpleDateFormat eventPubDate = new SimpleDateFormat("dd/MM/YYYY hh:mm a");
								eventPubDate.setTimeZone(timeZone);
								model.addAttribute("error", messageSource.getMessage("rftEvent.error.publishDate", new Object[] { eventPubDate.format(publishDate) }, Global.LOCALE));

								if (rfiEvent.getViewSupplerName()) {
									rfiEvent.setViewSupplerName(false);
								} else {
									rfiEvent.setViewSupplerName(true);
								}

								return new ModelAndView("createEventDetails", "event", rfiEvent);
							}
							if (!isDraft && rfiEvent.getEventStart() != null && rfiEvent.getEventStart().before(new Date())) {
								// Event start date cannot be in the past
								Date startDate = rfiEvent.getEventStart();
								LOG.info("EVENT startDate:" + startDate);
								SimpleDateFormat eventStartDate = new SimpleDateFormat("dd/MM/YYYY hh:mm a");
								eventStartDate.setTimeZone(timeZone);
								model.addAttribute("error", messageSource.getMessage("rftEvent.error.startdate", new Object[] { eventStartDate.format(startDate) }, Global.LOCALE));

								if (rfiEvent.getViewSupplerName()) {
									rfiEvent.setViewSupplerName(false);
								} else {
									rfiEvent.setViewSupplerName(true);
								}

								return new ModelAndView("createEventDetails", "event", rfiEvent);
							}
						}
						if (!isDraft && rfiEvent.getEventStart() != null && rfiEvent.getEventEnd() != null && rfiEvent.getEventStart().after(rfiEvent.getEventEnd())) {
							Date endDate = rfiEvent.getEventEnd();
							LOG.info("EVENT endDate:" + endDate);
							SimpleDateFormat eventEndDate = new SimpleDateFormat("dd/MM/YYYY hh:mm a");
							eventEndDate.setTimeZone(timeZone);
							model.addAttribute("error", messageSource.getMessage("rftEvent.error.enddate", new Object[] { eventEndDate.format(endDate) }, Global.LOCALE));

							if (rfiEvent.getViewSupplerName()) {
								rfiEvent.setViewSupplerName(false);
							} else {
								rfiEvent.setViewSupplerName(true);
							}

							return new ModelAndView("createEventDetails", "event", rfiEvent);
						}
						if (!isSuspended && (!isDraft && feeStartDate != null && feeStartDate.before(new Date()))) {
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY hh:mm a");
							sdf.setTimeZone(timeZone);
							model.addAttribute("error", messageSource.getMessage("feeStart.error.feeDate", new Object[] { sdf.format(feeStartDate) }, Global.LOCALE));

							if (rfiEvent.getViewSupplerName()) {
								rfiEvent.setViewSupplerName(false);
							} else {
								rfiEvent.setViewSupplerName(true);
							}

							return new ModelAndView("createEventDetails", "event", rfiEvent);

						}

						if (!isDraft && rfiEvent.getEventStart() != null && feeStartDate != null && feeStartDate.before(rfiEvent.getEventStart())) {
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY hh:mm a");
							sdf.setTimeZone(timeZone);
							model.addAttribute("error", messageSource.getMessage("feeStart.error.beforeEventStart", new Object[] { sdf.format(feeStartDate), sdf.format(rfiEvent.getEventStart()) }, Global.LOCALE));

							if (rfiEvent.getViewSupplerName()) {
								rfiEvent.setViewSupplerName(false);
							} else {
								rfiEvent.setViewSupplerName(true);
							}

							return new ModelAndView("createEventDetails", "event", rfiEvent);

						}

						// To check time for 1 day event
						if (!isDraft && rfiEvent.getEventStart() != null && rfiEvent.getEventEnd() != null) {
							Date endDate = rfiEvent.getEventEnd();
							Date startDate = rfiEvent.getEventStart();

							LOG.info("EVENT endDate:" + endDate);
							SimpleDateFormat df = new SimpleDateFormat("dd/MM/YYYY hh:mm a");

							if (df.format(endDate).equals(df.format(startDate))) {
								LOG.info("EVENT endDate: and Event start date cannot be same" + endDate);
								model.addAttribute("error", messageSource.getMessage("rftEvent.error.endtime", new Object[] { df.format(endDate) }, Global.LOCALE));

								if (rfiEvent.getViewSupplerName()) {
									rfiEvent.setViewSupplerName(false);
								} else {
									rfiEvent.setViewSupplerName(true);
								}

								return new ModelAndView("createEventDetails", "event", rfiEvent);
							}
						}
						LOG.info("PublishDate:" + rfiEvent.getEventPublishDate());

						if (!isDraft && rfiEvent.getEventPublishDate() != null && rfiEvent.getEventStart() != null && rfiEvent.getEventPublishDate().after(rfiEvent.getEventStart())) {
							Date pubDate = rfiEvent.getEventPublishDate();
							SimpleDateFormat publishDate = new SimpleDateFormat("dd/MM/YYYY hh:mm a");
							publishDate.setTimeZone(timeZone);
							model.addAttribute("error", messageSource.getMessage("rftEvent.error.publishdate", new Object[] { publishDate.format(pubDate) }, Global.LOCALE));

							if (rfiEvent.getViewSupplerName()) {
								rfiEvent.setViewSupplerName(false);
							} else {
								rfiEvent.setViewSupplerName(true);
							}

							return new ModelAndView("createEventDetails", "event", rfiEvent);
						}

						if (!isDraft && rfiEvent.getExpectedTenderStartDate() != null && rfiEvent.getEventStart() != null && rfiEvent.getExpectedTenderStartDate().before(rfiEvent.getEventStart())) {
							Date pubDate = rfiEvent.getExpectedTenderStartDate();
							SimpleDateFormat publishDate = new SimpleDateFormat("dd/MM/YYYY hh:mm a");
							publishDate.setTimeZone(timeZone);
							model.addAttribute("error", messageSource.getMessage("rftEvent.error.expected.tender.startdate", new Object[] { publishDate.format(pubDate), publishDate.format(rfiEvent.getEventStart()) }, Global.LOCALE));
							if (rfiEvent.getViewSupplerName()) {
								rfiEvent.setViewSupplerName(false);
							} else {
								rfiEvent.setViewSupplerName(true);
							}
							return new ModelAndView("createEventDetails", "event", rfiEvent);
						}
						
//						if(Boolean.TRUE == persistObj.getEnableSuspensionApproval() && rfiEvent.getSuspensionApprovals() == null) {
//							model.addAttribute("error", messageSource.getMessage("susp.approval.empty", new Object[] { }, Global.LOCALE));
//							return new ModelAndView("createEventDetails", "event", rfiEvent);
//						}
						
						persistObj.setUnMaskedUsers(rfiEvent.getUnMaskedUsers());
						if (Boolean.TRUE == rfiEvent.getEnableEvaluationConclusionUsers()) {
							persistObj.setEvaluationConclusionUsers(rfiEvent.getEvaluationConclusionUsers());
						} else {
							persistObj.setEvaluationConclusionUsers(null);
						}
						persistObj.setEnableEvaluationConclusionUsers(rfiEvent.getEnableEvaluationConclusionUsers());
						persistObj.setModifiedDate(new Date());
						persistObj.setIndustryCategory(rfiEvent.getIndustryCategory());
						// persistObj.setStatus(EventStatus.DRAFT); -- DONT SET THE STATUS AS DRAFT AS THIS COULD BE A
						// SUSPENDED EVENT EDIT - @Nitin Otageri

						if (rfiEvent.getStatus() != EventStatus.SUSPENDED) {
							persistObj.setApprovals(rfiEvent.getApprovals());
						}
						
						
						LOG.info("rfiEvent.getSuspensionApprovals(): " + rfiEvent.getSuspensionApprovals());
						if (Boolean.TRUE == persistObj.getEnableSuspensionApproval() || Boolean.TRUE == rfiEvent.getEnableSuspensionApproval()) {
							if (CollectionUtil.isNotEmpty(rfiEvent.getSuspensionApprovals())) {
								int suspLevel = 1;
								for (RfiEventSuspensionApproval app : rfiEvent.getSuspensionApprovals()) {
									app.setEvent(rfiEvent);
									app.setLevel(suspLevel++);
									if (app != null && CollectionUtil.isNotEmpty(app.getApprovalUsers()))
										for (RfiSuspensionApprovalUser approvalUser : app.getApprovalUsers()) {
											approvalUser.setApproval(app);
											approvalUser.setId(null);
										}
								}
							} else {
								LOG.warn("Suspension Approval levels is empty.");
							}
						}else {
							if (CollectionUtil.isNotEmpty(rfiEvent.getSuspensionApprovals())) {
								rfiEvent.getSuspensionApprovals().clear();
							}
						}
						
						persistObj.setSuspensionApprovals(rfiEvent.getSuspensionApprovals());
						
						persistObj.setEventEnd(rfiEvent.getEventEnd());
						persistObj.setEventStart(rfiEvent.getEventStart());
						persistObj.setEventVisibility(rfiEvent.getEventVisibility());
						persistObj.setEventName(rfiEvent.getEventName());
						persistObj.setReferanceNumber(rfiEvent.getReferanceNumber());
						persistObj.setUrgentEvent(rfiEvent.getUrgentEvent());
						persistObj.setEventVisibilityDates(rfiEvent.getEventVisibilityDates());
						persistObj.setDeliveryAddress(rfiEvent.getDeliveryAddress());
						persistObj.setEventPublishDate(publishDateTime);
						persistObj.setSubmissionValidityDays(rfiEvent.getSubmissionValidityDays());
						persistObj.setIndustryCategories(rfiEvent.getIndustryCategories());
						persistObj.setDeliveryDate(rfiEvent.getDeliveryDate());
						persistObj.setViewSupplerName(rfiEvent.getViewSupplerName());
						persistObj.setExpectedTenderStartDate(rfiEvent.getExpectedTenderStartDate());
						persistObj.setExpectedTenderEndDate(rfiEvent.getExpectedTenderEndDate());
						persistObj.setFeeStartDate(rfiEvent.getFeeStartDate());
						persistObj.setFeeEndDate(rfiEvent.getFeeEndDate());
						persistObj.setUnMaskedUser(rfiEvent.getUnMaskedUser());
						persistObj.setAllowToSuspendEvent(rfiEvent.getAllowToSuspendEvent());
						persistObj.setMinimumSupplierRating(rfiEvent.getMinimumSupplierRating());
						persistObj.setMaximumSupplierRating(rfiEvent.getMaximumSupplierRating());
						persistObj.setParticipationFeeCurrency(rfiEvent.getParticipationFeeCurrency());
						persistObj.setParticipationFees(rfiEvent.getParticipationFees());
						persistObj.setDepositCurrency(rfiEvent.getDepositCurrency());
						persistObj.setDeposit(rfiEvent.getDeposit());
						persistObj.setCloseEnvelope(rfiEvent.getCloseEnvelope());
						persistObj.setAddSupplier(rfiEvent.getAddSupplier());
						persistObj.setRfxEnvelopeOpening(rfiEvent.getRfxEnvelopeOpening());
						persistObj.setRfxEnvOpeningAfter(rfiEvent.getRfxEnvOpeningAfter());
						persistObj.setEnableEvaluationDeclaration(rfiEvent.getEnableEvaluationDeclaration());
						persistObj.setEnableSupplierDeclaration(rfiEvent.getEnableSupplierDeclaration());
						persistObj.setEvaluationProcessDeclaration(Boolean.TRUE == rfiEvent.getEnableEvaluationDeclaration() && rfiEvent.getEvaluationProcessDeclaration() != null ? rfiEvent.getEvaluationProcessDeclaration() : null);
						persistObj.setSupplierAcceptanceDeclaration(Boolean.TRUE == rfiEvent.getEnableSupplierDeclaration() && rfiEvent.getSupplierAcceptanceDeclaration() != null ? rfiEvent.getSupplierAcceptanceDeclaration() : null);
//						persistObj.setProcurementMethod(rfiEvent.getProcurementMethod());
//						persistObj.setProcurementCategories(rfiEvent.getProcurementCategories());
						if (persistObj.getTemplate() == null) {
							persistObj.setEnableSuspensionApproval(rfiEvent.getEnableSuspensionApproval());
						}
						
						persistObj.setEnableApprovalReminder(rfiEvent.getEnableApprovalReminder());
						persistObj.setAllowDisqualifiedSupplierDownload(rfiEvent.getAllowDisqualifiedSupplierDownload());
						if (Boolean.TRUE == rfiEvent.getEnableApprovalReminder()) {
							persistObj.setReminderAfterHour(rfiEvent.getReminderAfterHour());
							persistObj.setReminderCount(rfiEvent.getReminderCount());
						} else {
							persistObj.setReminderAfterHour(null);
							persistObj.setReminderCount(null);
						}
						persistObj.setNotifyEventOwner(rfiEvent.getNotifyEventOwner());
						rfiEventService.updateRfiEvent(persistObj);
						
						if (StringUtils.checkString(persistObj.getPreviousRequestId()).length() > 0) {
							TatReportPojo tatReport = tatReportService.getTatReportByEventIdAndFormIdAndTenantId(persistObj.getPreviousRequestId(), persistObj.getEventId(), SecurityLibrary.getLoggedInUserTenantId());
							if(tatReport != null) {
								tatReportService.updateTatReportEventDetails(tatReport.getId(), persistObj.getId(), persistObj.getEventStart(), persistObj.getEventEnd(), persistObj.getEventPublishDate(), persistObj.getEventName(), persistObj.getReferanceNumber());
							}
						}
						
						redir.addFlashAttribute("eventId", persistObj.getId());
						if (rfiEvent.getTemplate().getId() != null) {
							redir.addFlashAttribute("templateId", persistObj.getTemplate().getId());
						}

					} else {
						if (goNext == Boolean.FALSE) {
							return new ModelAndView(next);
						} else {
							LOG.error("Validation failed ");
							return new ModelAndView("createEventDetails", "event", rfiEvent);
						}
					}
				}
			} catch (Exception e) {
				LOG.error("Error while storing RfiEvent : " + e.getMessage(), e);
				model.addAttribute("errors", "Error while storing Rft Event details for : " + rfiEvent.getEventName() + ", message : " + e.getMessage());
				return new ModelAndView("createEventDetails", "event", rfiEvent);
			}
			try {
				if (!isDraft && StringUtils.checkString(rfiEvent.getId()).length() > 0) {
					rfiEventService.insertTimeLine(rfiEvent.getId());
				}
			} catch (Exception e) {
				LOG.error("Error : " + e.getMessage(), e);
			}
			return new ModelAndView(next);
		}
	}

	@RequestMapping(path = "/eventDescription/{eventId}", method = RequestMethod.GET)
	public ModelAndView eventDescription(@PathVariable String eventId, Model model) {
		if (StringUtils.checkString(eventId).length() == 0) {
			return new ModelAndView("redirect:/400_error");
		}
		LOG.info("Event ID in Description : " + eventId);
		RfiEvent event = null;
		BuyerSettings settings = null;
		if (StringUtils.checkString(eventId).length() > 0) {
			model.addAttribute("eventId", eventId);
			model.addAttribute("eventPermissions", eventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
			event = rfiEventService.loadRfiEventById(eventId);
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
				constructDefaultModel(model, event, RfxTypes.RFI, settings);
			} catch (JsonProcessingException e1) {
				LOG.info("Error :" + e1.getMessage(), e1);
			}
			return new ModelAndView("eventDescription");
		} else {
			return new ModelAndView("/500_error");
		}
	}

	@RequestMapping(path = "/eventDescription", method = RequestMethod.POST)
	public ModelAndView saveEventDescription(@ModelAttribute RfiEvent rfiEvent, Model model, BindingResult result, RedirectAttributes redir) {
		model.addAttribute("eventPermissions", eventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), rfiEvent.getId()));
		return saveRfiDescription(rfiEvent, model, result, redir, true);
	}

	/**
	 * @param rfiEvent
	 * @param model
	 * @param result
	 * @param eventId
	 * @param redir
	 * @return
	 */
	private ModelAndView saveRfiDescription(RfiEvent rfiEvent, Model model, BindingResult result, RedirectAttributes redir, boolean goNext) {
		LOG.info("Save Rft Event Description Called ");
		RfiEvent persistObj = null;
		rfiEvent.setBillOfQuantity(Boolean.FALSE);
		model.addAttribute("costCenter", costCenterService.getAllActiveCostCentersForTenant(SecurityLibrary.getLoggedInUserTenantId()));
		model.addAttribute("currency", currencyService.getAllCurrency());
		model.addAttribute("eventId", rfiEvent.getId());
		model.addAttribute("eventPermissions", eventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), rfiEvent.getId()));
		String eventId = rfiEvent.getId();

		LOG.info("Template ID : " + rfiEvent.getTemplate());
		if (rfiEvent.getTemplate().getId() != null) {
			RfxTemplate rfxTemplate = rfxTemplateService.getRfxTemplateForEditById(rfiEvent.getTemplate().getId(), SecurityLibrary.getLoggedInUserTenantId());
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
			return new ModelAndView("eventDescription", "event", rfiEvent);
		} else {
			LOG.info("Page submitted....................................... ");
			try {
				if (StringUtils.checkString(eventId).length() > 0) {
					LOG.info("rftEventDescription update:      " + eventId);
					if (rfiEvent.getBillOfQuantity() == false && rfiEvent.getQuestionnaires() == false && rfiEvent.getScheduleOfRate() == false) {
						if (rfiEvent.getTemplate().getId() != null) {
							RfxTemplate rfxTemplate = rfxTemplateService.getRfxTemplateForEditById(rfiEvent.getTemplate().getId(), SecurityLibrary.getLoggedInUserTenantId());
							model.addAttribute("templateFields", rfxTemplate.getFields());
							model.addAttribute("rfxTemplate", rfxTemplate);
						}
						model.addAttribute("error", messageSource.getMessage("rftEvent.error.sororcq", new Object[] {}, Global.LOCALE));
						model.addAttribute("businessUnitList", businessUnitService.getPlainActiveBusinessUnitForTenant(SecurityLibrary.getLoggedInUserTenantId()));
						model.addAttribute("procurementMethodList", procurementMethodService.getAllActiveProcurementMethod(SecurityLibrary.getLoggedInUserTenantId()));
						model.addAttribute("procurementCategoryList", procurementCategoriesService.getAllActiveProcurementCategory(SecurityLibrary.getLoggedInUserTenantId()));
						return new ModelAndView("eventDescription", "event", rfiEventService.loadRfiEventById(eventId));
					}
					persistObj = rfiEventService.getRfiEventById(eventId);
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
					persistObj.setGroupCode(rfiEvent.getGroupCode());
					persistObj.setScheduleOfRate(rfiEvent.getScheduleOfRate());
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
					rfiEventService.updateRfiEvent(persistObj);
					LOG.info("rftEvent update:      " + persistObj);
				}

			} catch (Exception e) {
				LOG.error("Error while storing RfiEvent : " + e.getMessage(), e);
				// model.addAttribute("error", "Error while storing Rfi Event details for : " +
				// persistObj.getEventName() + ", message : " + e.getMessage());
				model.addAttribute("error", messageSource.getMessage("error.while.storing.rfievent", new Object[] { persistObj.getEventName(), e.getMessage() }, Global.LOCALE));
				model.addAttribute("businessUnitList", businessUnitService.getPlainActiveBusinessUnitForTenant(SecurityLibrary.getLoggedInUserTenantId()));
				model.addAttribute("procurementMethodList", procurementMethodService.getAllActiveProcurementMethod(SecurityLibrary.getLoggedInUserTenantId()));
				model.addAttribute("procurementCategoryList", procurementCategoriesService.getAllActiveProcurementCategory(SecurityLibrary.getLoggedInUserTenantId()));
				return new ModelAndView("eventDescription", "event", rfiEvent);
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
	public @ResponseBody ResponseEntity<List<RfiEventContact>> saveContact(@ModelAttribute RfiEventContact rfiEventContact, BindingResult result, Model model) throws JsonProcessingException {
		LOG.info("Save Rft event Contact Called " + rfiEventContact.getEventId());
		HttpHeaders headers = new HttpHeaders();
		String contactEventId = rfiEventContact.getEventId();
		try {
			if (result.hasErrors()) {
				for (ObjectError err : result.getAllErrors()) {
					headers.add("error", err.getDefaultMessage());
				}
				return new ResponseEntity<List<RfiEventContact>>(null, headers, HttpStatus.EXPECTATION_FAILED);
			} else {

				if (StringUtils.checkString(rfiEventContact.getEventId()).length() > 0) {
					if (rfiEventContact.getComunicationEmail() != null || rfiEventContact.getContactNumber() != null || rfiEventContact.getMobileNumber() != null || rfiEventContact.getFaxNumber() != null) {
						if (doValidate(rfiEventContact)) {
							RfiEvent rfiEvent = rfiEventService.getRfiEventById(rfiEventContact.getEventId());
							if (StringUtils.checkString(rfiEventContact.getId()).length() == 0) {
								LOG.info("evennt in contact :  " + rfiEvent.getEventName());
								rfiEventContact.setRfxEvent(rfiEvent);
								rfiEventContact = rfiEventService.saveRfiEventContact(rfiEventContact);
								LOG.info("Save Contact Called" + SecurityLibrary.getLoggedInUser());
								headers.add("success", "Successfully added contact details");
							} else {
								LOG.info("upadte event contact");
								RfiEventContact persistObj = rfiEventService.getEventContactById(rfiEventContact.getId());
								persistObj.setRfxEvent(rfiEvent);
								persistObj.setComunicationEmail(rfiEventContact.getComunicationEmail());
								persistObj.setContactName(rfiEventContact.getContactName());
								persistObj.setContactNumber(rfiEventContact.getContactNumber());
								persistObj.setDesignation(rfiEventContact.getDesignation());
								persistObj.setFaxNumber(rfiEventContact.getFaxNumber());
								persistObj.setMobileNumber(rfiEventContact.getMobileNumber());
								persistObj.setTitle(rfiEventContact.getTitle());
								rfiEventService.saveRfiEventContact(persistObj);
								LOG.info("upadte event contact  " + persistObj.toLogString());
								headers.add("success", "Successfully updated contact details");
							}
						} else {
							headers.add("error", "Contact name already exist");
							return new ResponseEntity<List<RfiEventContact>>(null, headers, HttpStatus.EXPECTATION_FAILED);
						}
					} else {
						headers.add("error", "Please select one of Contact number, Mobile number, Communication email.");
						return new ResponseEntity<List<RfiEventContact>>(null, headers, HttpStatus.EXPECTATION_FAILED);
					}

				}
			}
		} catch (Exception e) {
			LOG.error("Error While Save the Country" + e.getMessage(), e);
			return new ResponseEntity<List<RfiEventContact>>(null, headers, HttpStatus.EXPECTATION_FAILED);
		}
		List<RfiEventContact> rfiContactList = rfiEventService.getAllContactForEvent(contactEventId);

		return new ResponseEntity<List<RfiEventContact>>(rfiContactList, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "mangeEventRequirement", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Integer> mangeEventRequirement(@RequestParam("eventId") String eventId, @RequestParam("eventRequirement") String eventRequirement) {
		LOG.info("Requirment Event Id: " + eventId + " the Evnt button :  " + eventRequirement);

		if (eventRequirement.equals("documentReq")) {
			Integer countDocument = 0;// rfiEventService.getCountOfRfiDocumentByEventId(eventId);
			LOG.info("countDocument :  " + countDocument);
			if (countDocument > 0) {
				return new ResponseEntity<Integer>(countDocument, HttpStatus.EXPECTATION_FAILED);
			}
		}
		if (eventRequirement.equals("meetingReq")) {
			Integer countMeeting = rfiMeetingService.getCountOfRfiMeetingByEventId(eventId);
			if (countMeeting > 0) {
				return new ResponseEntity<Integer>(countMeeting, HttpStatus.EXPECTATION_FAILED);
			}
		}
		if (eventRequirement.equals("questionnaires")) {
			Integer countQuestionnaires = 0; // rfiEventService.getCountOfRfiCqByEventId(eventId);
			if (countQuestionnaires > 0) {
				return new ResponseEntity<Integer>(countQuestionnaires, HttpStatus.EXPECTATION_FAILED);
			}
		}
		if(eventRequirement.equals("scheduleOfRate")) {
			Integer countBillOfQuantity = rfiSorService.getCountOfSorByEventId(eventId);
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
			rfiDocumentService.deleteAllRfiDocuments(eventId, eventRequirement);
		}
		if (eventRequirement.equals("meetingReq")) {
			rfiMeetingService.deleteAllRfiMeetings(eventId, eventRequirement);
		}
		if (eventRequirement.equals("questionnaires")) {
			rfiCqService.deleteAllCqs(eventId, eventRequirement);
		}
		if (eventRequirement.equals("scheduleOfRate")) {
			rfiSorService.deleteAllSor(eventId, eventRequirement);
		}

		return new ResponseEntity<String>(HttpStatus.OK);
	}

	@RequestMapping(path = "/saveAsDraft", method = RequestMethod.POST)
	public ModelAndView saveAsDraft(@ModelAttribute RfiEvent rfiEvent, @RequestParam(value = "industryCateg[]") String[] industryCategories, Model model, BindingResult result, RedirectAttributes redir, HttpSession session) {
		if (rfiEvent.getViewSupplerName() == true) {
			rfiEvent.setViewSupplerName(false);
		} else {
			rfiEvent.setViewSupplerName(true);
		}

		return saveDetails(rfiEvent, model, result, redir, false, session, true, industryCategories);
	}

	@RequestMapping(path = "/saveAsDraftDescription", method = RequestMethod.POST)
	public ModelAndView saveAsDraftDescription(@ModelAttribute RfiEvent rfiEvent, Model model, BindingResult result, RedirectAttributes redir) {
		return saveRfiDescription(rfiEvent, model, result, redir, false);
	}

	/*
	 * @RequestMapping(path = "/editContact", method = RequestMethod.GET) public ModelAndView editContact(@RequestParam
	 * String id, Model model) { LOG.info( "Getting the editContact. : " + id); RfiEventContact eventContact =
	 * rfiEventService.getEventContactById(id); model.addAttribute("btnValue", "Update"); return new
	 * ModelAndView("rftEventContact", "eventContact", eventContact); }
	 */
	@RequestMapping(path = "/editContact", method = RequestMethod.POST)
	public ResponseEntity<RfiEventContact> editContact(@RequestParam(value = "contactId") String contactId, Model model) {
		LOG.info("Getting the editContact. : " + contactId);
		RfiEventContact eventContact = rfiEventService.getEventContactById(contactId);
		model.addAttribute("btnValue", "Update");
		return new ResponseEntity<RfiEventContact>(eventContact, HttpStatus.OK);
	}

	@RequestMapping(path = "/deleteContact", method = RequestMethod.POST)
	public ResponseEntity<List<RfiEventContact>> deleteContact(@RequestParam(value = "contactId") String contactId, @RequestParam(value = "eventId") String eventId, Model model) {
		LOG.info("Getting the Delete Contact. : " + contactId);
		RfiEventContact eventContact = rfiEventService.getEventContactById(contactId);
		HttpHeaders headers = new HttpHeaders();
		List<RfiEventContact> rfiContactList = null;
		LOG.info("event contact   :  " + eventContact);
		model.addAttribute("btnValue", "Update");
		try {
			rfiEventService.deleteContact(eventContact);
			LOG.info("Delete the Contact");
			headers.add("success", "Contact removed Successfully");
		} catch (Exception e) {
			LOG.error("Error while removing Contact from event . " + e.getMessage(), e);
			headers.add("error", "Contact removed unsuccessfull");
			return new ResponseEntity<List<RfiEventContact>>(rfiContactList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		rfiContactList = rfiEventService.getAllContactForEvent(eventId);
		return new ResponseEntity<List<RfiEventContact>>(rfiContactList, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/addReminderOfEvent", method = RequestMethod.POST)
	public ResponseEntity<List<RfiReminder>> addReminderOfEvent(@RequestParam(value = "reminderDuration") String reminderDuration, @RequestParam(value = "dateRangeData") String dateRangeData, @RequestParam(value = "reminderDurationType") IntervalType reminderDurationType, @RequestParam(value = "eventId") String eventId, @RequestParam(value = "reminderId") String reminderId, @RequestParam(value = "reminderNotifyType") String reminderNotifyType, Model model, HttpSession session) {
		LOG.info("Getting the AddReminder : " + eventId);
		HttpHeaders headers = new HttpHeaders();
		try {
			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}
			if (StringUtils.checkString(eventId).length() > 0) {
				RfiEvent rfiEvent = rfiEventService.getRfiEventById(eventId);
				LOG.info("evennt in reminder save :  ");
				if (StringUtils.checkString(reminderId).length() == 0) {
					RfiReminder rfiReminder = new RfiReminder();
					if (StringUtils.checkString(dateRangeData).length() > 0) {
						String visibilityDates[] = dateRangeData.split("-");
						DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
						formatter.setTimeZone(timeZone);
						Date startDate = (Date) formatter.parse(visibilityDates[0]);
						Date endDate = (Date) formatter.parse(visibilityDates[1]);

						Calendar cal = Calendar.getInstance(timeZone);
						if (StringUtils.checkString(reminderNotifyType).equalsIgnoreCase("Start")) {
							cal.setTime(startDate);
							rfiReminder.setStartReminder(Boolean.TRUE);
						} else {
							cal.setTime(endDate);
						}
						if (reminderDurationType == IntervalType.DAYS) {
							cal.add(Calendar.DATE, -Integer.parseInt(reminderDuration));
							rfiReminder.setIntervalType(IntervalType.DAYS);
							LOG.info("Reminder : " + formatter.format(cal.getTime()));
						} else {
							cal.add(Calendar.HOUR, -Integer.parseInt(reminderDuration));
							rfiReminder.setIntervalType(IntervalType.HOURS);
							LOG.info("Reminder  Hous: " + formatter.format(cal.getTime()));
						}
						rfiReminder.setReminderDate(cal.getTime());
						List<RfiReminder> reminderList = rfiEventService.getAllRfiEventReminderForEvent(eventId);
						if (CollectionUtil.isNotEmpty(reminderList)) {
							for (RfiReminder rfiReminderCompare : reminderList) {
								if (rfiReminder.getReminderDate().compareTo(rfiReminderCompare.getReminderDate()) == 0 && (rfiReminder.getStartReminder() == rfiReminderCompare.getStartReminder())) {
									headers.add("error", "There is another reminder on this date is exists");
									return new ResponseEntity<List<RfiReminder>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
								}
							}
						}
						// if (rfiReminder.getReminderDate().before(startDate)) {
						// headers.add("error", "Reminder date/time should not be less than event start date");
						// return new ResponseEntity<List<RfiReminder>>(null, headers,
						// HttpStatus.INTERNAL_SERVER_ERROR);
						// }

						if (cal.getTime().compareTo(new Date()) < 0) {
							headers.add("error", messageSource.getMessage("event.reminder.pastdate", new Object[] {}, Global.LOCALE));
							return new ResponseEntity<List<RfiReminder>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
						}

						rfiEvent.setEventStart(startDate);
						rfiEvent.setEventEnd(endDate);
						rfiEventService.updateRfiEvent(rfiEvent);

						rfiReminder.setRfiEvent(rfiEvent);
						rfiReminder.setInterval(Integer.parseInt(reminderDuration));
						LOG.info(rfiReminder);
						rfiEventService.saveRfiEventReminder(rfiReminder);
					}
				}
			}
		} catch (Exception e) {
			LOG.error("Error While Save the Reminder" + e.getMessage(), e);
			return new ResponseEntity<List<RfiReminder>>(null, headers, HttpStatus.EXPECTATION_FAILED);
		}
		List<RfiReminder> reminderList = rfiEventService.getAllRfiEventReminderForEvent(eventId);
		return new ResponseEntity<List<RfiReminder>>(reminderList, HttpStatus.OK);
	}

	@RequestMapping(path = "/deleteReminder", method = RequestMethod.POST)
	public ResponseEntity<List<RfiReminder>> deleteRfiReminder(@RequestParam(value = "reminderId") String reminderId, @RequestParam(value = "eventId") String eventId, Model model) {
		LOG.info("Getting the Delete reminder : " + reminderId);
		RfiReminder eventReminder = rfiEventService.getRfiEventReminderById(reminderId);
		LOG.info("event eventReminder   :  " + eventReminder);
		model.addAttribute("btnValue", "Update");
		rfiEventService.deleteRfiReminder(eventReminder);
		LOG.info("Delete the Reminder");

		List<RfiReminder> reminderList = rfiEventService.getAllRfiEventReminderForEvent(eventId);
		return new ResponseEntity<List<RfiReminder>>(reminderList, HttpStatus.OK);
	}

	@RequestMapping(path = "/addTeamMemberToList", method = RequestMethod.POST)
	public ResponseEntity<List<EventTeamMember>> addTeamMemberToList(@RequestParam(value = "eventId") String eventId, @RequestParam(value = "userId") String userId, @RequestParam(value = "memberType") TeamMemberType memberType) {
		return super.addTeamMemberToList(eventId, userId, memberType);
	}

	@RequestMapping(path = "/removeTeamMemberfromList", method = RequestMethod.POST)
	public ResponseEntity<List<User>> removeTeamMemberfromList(@RequestParam(value = "userId") String userId, @RequestParam(value = "eventId") String eventId, Model model) {
		return super.removeTeamMemberfromList(eventId, userId);
	}

	@RequestMapping(path = "/manageEventVisibility", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Integer> manageEventVisibility(@RequestParam("eventId") String eventId, @RequestParam("eventVisbility") EventVisibilityType eventVisbility) {
		LOG.info("Requirment Event Id: " + eventId + " the Evnt button :  " + eventVisbility);
		if (eventVisbility == (EventVisibilityType.PUBLIC)) {
			Integer countMeeting = rfiMeetingService.getCountOfRfiMeetingByEventId(eventId);
			Integer countEventSupplier = rfiEventSupplierService.getCountOfSupplierByEventId(eventId);

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
			rfiMeetingService.deleteAllRfiMeetings(eventId, null);
			rfiEventSupplierService.deleteAllSuppliersByEventId(eventId);
		}
		return new ResponseEntity<String>(HttpStatus.OK);
	}

	@RequestMapping(path = "/manageReminderOnDateChange", method = RequestMethod.POST)
	public ResponseEntity<List<RfiReminder>> manageReminderOnDateChange(@RequestParam(value = "dateRangeData") String dateRangeData, @RequestParam(value = "eventId") String eventId, @RequestParam(value = "reminderId", required = false) String reminderId, Model model, HttpSession session) {
		LOG.info("Getting the AddReminder acoordig to event date : " + eventId);
		HttpHeaders headers = new HttpHeaders();
		try {
			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}
			if (StringUtils.checkString(eventId).length() > 0) {

				RfiEvent event = rfiEventService.getRfiEventById(eventId);
				LOG.info("evennt in reminder save :  ");
				List<RfiReminder> reminderList = rfiEventService.getAllRfiEventReminderForEvent(eventId);
				if (CollectionUtil.isNotEmpty(reminderList)) {
					for (RfiReminder reminder : reminderList) {
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
							return new ResponseEntity<List<RfiReminder>>(reminderList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
						}
						event.setEventStart(startDate);
						event.setEventEnd(endDate);
						rfiEventService.updateRfiEvent(event);

						reminder.setReminderDate(cal.getTime());

						rfiEventService.updateEventReminder(reminder);
					}
				}
			}
		} catch (Exception e) {
			LOG.error("Error While Save the Country" + e.getMessage(), e);
			return new ResponseEntity<List<RfiReminder>>(null, headers, HttpStatus.EXPECTATION_FAILED);
		}
		List<RfiReminder> reminderList = rfiEventService.getAllRfiEventReminderForEvent(eventId);
		return new ResponseEntity<List<RfiReminder>>(reminderList, HttpStatus.OK);
	}

	private boolean doValidate(RfiEventContact rfiEventContact) {
		boolean validate = true;
		if (rfiEventService.isExists(rfiEventContact)) {
			validate = false;
		}
		return validate;
	}

	@RequestMapping(path = "/autoSaveDraft", method = RequestMethod.POST)
	public @ResponseBody void autoSaveDraft(@ModelAttribute("event") RfiEvent rfiEvent, @RequestParam("industryCateg[]") String[] industryCategory, BindingResult result, HttpSession session, Model model) {
		LOG.info("autoSave Draft Industry Cat " + industryCategory);
		rfiEventService.autoSaveRfaDetails(rfiEvent, industryCategory, result, (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY));
		if (rfiEvent.getViewSupplerName() == true) {
			rfiEvent.setViewSupplerName(false);
		} else {
			rfiEvent.setViewSupplerName(true);
		}

		rfiEventService.autoSaveRfaDetails(rfiEvent, industryCategory, result, (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY));
	}

	@RequestMapping(path = "/autoSaveDraftDesc", method = RequestMethod.POST)
	public @ResponseBody void autoSaveDraftDesc(@ModelAttribute("event") RfiEvent rfiEvent) {
		saveRfiDescription(rfiEvent);
	}

	private void saveRfiDescription(RfiEvent rfiEvent) {
		LOG.info("Save Rft Event Description Called ");
		RfiEvent persistObj = null;
		rfiEvent.setBillOfQuantity(Boolean.FALSE);
		String eventId = rfiEvent.getId();

		try {
			if (StringUtils.checkString(eventId).length() > 0) {
				LOG.info("rftEventDescription update:      " + eventId);

				persistObj = rfiEventService.getRfiEventById(eventId);
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
				rfiEventService.updateRfiEvent(persistObj);
				LOG.info("rftEvent update:      " + persistObj);
			}

		} catch (Exception e) {
			LOG.error("Error while storing RfiEvent : " + e.getMessage(), e);
		}

	}
}

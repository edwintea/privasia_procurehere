package com.privasia.procurehere.web.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;


import com.privasia.procurehere.service.RfaSorService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
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
import com.privasia.procurehere.core.entity.AuctionRules;
import com.privasia.procurehere.core.entity.BusinessUnit;
import com.privasia.procurehere.core.entity.BuyerAddress;
import com.privasia.procurehere.core.entity.BuyerSettings;
import com.privasia.procurehere.core.entity.ErpSetup;
import com.privasia.procurehere.core.entity.EventTeamMember;
import com.privasia.procurehere.core.entity.IndustryCategory;
import com.privasia.procurehere.core.entity.ProcurementCategories;
import com.privasia.procurehere.core.entity.ProcurementMethod;
import com.privasia.procurehere.core.entity.RfaApprovalUser;
import com.privasia.procurehere.core.entity.RfaAwardApprovalUser;
import com.privasia.procurehere.core.entity.RfaEvaluationConclusionUser;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.entity.RfaEventApproval;
import com.privasia.procurehere.core.entity.RfaEventAwardApproval;
import com.privasia.procurehere.core.entity.RfaEventContact;
import com.privasia.procurehere.core.entity.RfaEventMeeting;
import com.privasia.procurehere.core.entity.RfaEventSuspensionApproval;
import com.privasia.procurehere.core.entity.RfaReminder;
import com.privasia.procurehere.core.entity.RfaSuspensionApprovalUser;
import com.privasia.procurehere.core.entity.RfaTeamMember;
import com.privasia.procurehere.core.entity.RfaUnMaskedUser;
import com.privasia.procurehere.core.entity.RfxTemplate;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.AuctionType;
import com.privasia.procurehere.core.enums.AwardStatus;
import com.privasia.procurehere.core.enums.DeclarationType;
import com.privasia.procurehere.core.enums.DurationMinSecType;
import com.privasia.procurehere.core.enums.DurationType;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.EventVisibilityType;
import com.privasia.procurehere.core.enums.IntervalType;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.TeamMemberType;
import com.privasia.procurehere.core.enums.UserType;
import com.privasia.procurehere.core.enums.ValueType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.SubscriptionException;
import com.privasia.procurehere.core.pojo.BuyerAddressPojo;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.TatReportPojo;
import com.privasia.procurehere.core.pojo.UserPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.DateUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.job.DutchAuctionJob;
import com.privasia.procurehere.service.ErpSetupService;
import com.privasia.procurehere.service.RfaBqService;
import com.privasia.procurehere.service.RfaCqService;
import com.privasia.procurehere.service.RfaDocumentService;
import com.privasia.procurehere.service.RfaEventService;
import com.privasia.procurehere.service.RfaEventSupplierService;
import com.privasia.procurehere.service.RfaMeetingService;
import com.privasia.procurehere.service.RfaSupplierBqService;
import com.privasia.procurehere.service.TatReportService;
import com.privasia.procurehere.web.editors.PreviousAuctionEditor;
import com.privasia.procurehere.web.editors.ProcurementCategoriesEditor;
import com.privasia.procurehere.web.editors.ProcurementMethodEditor;
import com.privasia.procurehere.web.editors.RfaApprovalEditor;
import com.privasia.procurehere.web.editors.RfaEventEditor;
import com.privasia.procurehere.web.editors.RfaSuspensionApprovalEditor;
import com.privasia.procurehere.web.editors.TemplateEditor;

@Controller
@RequestMapping("/buyer/RFA")
public class RfaCreationController extends EventCreationBase {

	protected static final Logger LOG = LogManager.getLogger(Global.AUCTION_LOG);

	@Autowired
	RfaApprovalEditor rfaApprovalEditor;

	public RfaCreationController() {
		super(RfxTypes.RFA);
	}

	@Autowired
	ErpSetupService erpSetupService;

	@Autowired
	RfaEventService rfaEventService;

	@Autowired
	RfaEventEditor rfaEventEditor;

	@Autowired
	RfaBqService rfaBqService;

	@Autowired
	RfaCqService rfaCqService;

	@Autowired
	TemplateEditor templateEditor;

	@Autowired
	RfaDocumentService rfaDocumentService;

	@Autowired
	RfaMeetingService rfaMeetingService;

	@Autowired
	PreviousAuctionEditor previousAuctionEditor;

	@Autowired
	SchedulerFactoryBean schedulerFactoryBean;

	@Autowired
	RfaEventSupplierService rfaEventSupplierService;

	@Autowired
	RfaSupplierBqService rfaSupplierBqService;

	@Autowired
	ProcurementMethodEditor procurementMethodEditor;

	@Autowired
	ProcurementCategoriesEditor procurementCategoriesEditor;

	@Autowired
	RfaSuspensionApprovalEditor rfaSuspensionApprovalEditor;

	@Autowired
	TatReportService tatReportService;

	@Autowired
	RfaSorService rfaSorService;

	protected Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	@InitBinder
	public void initBinder(WebDataBinder binder, HttpSession session) {
		super.initBinder(binder, session);
		TimeZone timeZone = TimeZone.getDefault();
		String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}

		binder.registerCustomEditor(RfaEvent.class, "previousAuction", previousAuctionEditor);
		binder.registerCustomEditor(RfaEvent.class, "event", previousAuctionEditor);
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		format.setTimeZone(timeZone);
		SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
		timeFormat.setTimeZone(timeZone);

		binder.registerCustomEditor(Date.class, "supplierNotificationDate", new CustomDateEditor(format, true));
		binder.registerCustomEditor(Date.class, "eventStart", new CustomDateEditor(format, true));
		binder.registerCustomEditor(Date.class, "eventEnd", new CustomDateEditor(format, true));
		binder.registerCustomEditor(Date.class, "eventStartTime", new CustomDateEditor(timeFormat, true));
		binder.registerCustomEditor(Date.class, "eventEndTime", new CustomDateEditor(timeFormat, true));
		binder.registerCustomEditor(RfaApprovalUser.class, rfaApprovalEditor);
		binder.registerCustomEditor(ProcurementMethod.class, procurementMethodEditor);
		binder.registerCustomEditor(ProcurementCategories.class, procurementCategoriesEditor);
		binder.registerCustomEditor(RfaSuspensionApprovalUser.class, rfaSuspensionApprovalEditor);

		/*
		 * binder.registerCustomEditor(List.class, "approvalUsers", new CustomCollectionEditor(List.class) { protected
		 * Object convertElement(Object element) { if (element != null) { String id = (String) element; User user =
		 * userService.findUserById(id); return new RfaApprovalUser(user); } return null; } });
		 */
	}

	@RequestMapping(path = "/editEventDetails", method = RequestMethod.POST)
	public String editEventDetails(@RequestParam String eventId, Model model, RedirectAttributes redir) {
		model.addAttribute("eventPermissions", rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
		try {
			return super.editEventDetails(eventId);
		} catch (SubscriptionException e) {
			LOG.error("Error :" + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("reached.subscription.limit", new Object[] { e.getMessage() }, Global.LOCALE));
			return "redirect:/buyer/buyerDashboard";
		}
	}

	@RequestMapping(path = "/eventCreationPrevious", method = RequestMethod.POST)
	public String eventCreationPrevious(@ModelAttribute("event") RfaEvent rfaEvent, Model model) {
		model.addAttribute("eventPermissions", rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), rfaEvent.getId()));
		return super.eventCreationPrevious(rfaEvent);
	}

	@RequestMapping(path = "/createRfaEvent/{auctionType}", method = RequestMethod.GET)
	public String createRfaEvent(@PathVariable AuctionType auctionType, Model model, RedirectAttributes redir) {

		RfaEvent rfaEvent = new RfaEvent();
		try {
			rfaEvent.setCreatedBy(SecurityLibrary.getLoggedInUser());
			rfaEvent.setCreatedDate(new Date());

			// "RFA"));
			ErpSetup erpSetup = erpSetupService.getErpConfigBytenantId(SecurityLibrary.getLoggedInUserTenantId());
			if (erpSetup != null) {
				LOG.info("--------erp flag set for event-----------");
				rfaEvent.setErpEnable(erpSetup.getIsErpEnable());
			} else {
				rfaEvent.setErpEnable(false);
			}

			rfaEvent.setEventId(eventIdSettingDao.generateEventIdByBusinessUnit(SecurityLibrary.getLoggedInUserTenantId(), "RFA", null));// eventIdSettingDao.generateEventId(SecurityLibrary.getLoggedInUserTenantId(),
																																			// "RFA"));
			rfaEvent.setStatus(EventStatus.DRAFT);

			/**
			 * commented for self invited supplier cr to create public rfa event.
			 */
			// rfaEvent.setEventVisibility(EventVisibilityType.PRIVATE);

			rfaEvent.setAuctionType(auctionType);

			AuctionRules auctionRules = new AuctionRules();
			if (rfaEvent.getAuctionType() == AuctionType.FORWARD_ENGISH || rfaEvent.getAuctionType() == AuctionType.REVERSE_ENGISH || rfaEvent.getAuctionType() == AuctionType.FORWARD_SEALED_BID || rfaEvent.getAuctionType() == AuctionType.REVERSE_SEALED_BID) {
				rfaEvent.setBillOfQuantity(Boolean.TRUE);
				auctionRules.setLumsumBiddingWithTax(Boolean.FALSE);
			}
			rfaEvent = rfaEventService.saveRfaEvent(rfaEvent, SecurityLibrary.getLoggedInUser());

			rfaEventService.setDefaultEventContactDetail(SecurityLibrary.getLoggedInUser().getId(), rfaEvent.getId());
			// save Auction Rule
			auctionRules.setEvent(rfaEvent);
			auctionRules.setAuctionType(rfaEvent.getAuctionType());
			auctionRules.setBiddingPriceHigherLeadingBidType(ValueType.VALUE);
			auctionRules.setBiddingMinValueType(ValueType.VALUE);
			if (auctionRules.getAuctionType() == AuctionType.FORWARD_DUTCH || auctionRules.getAuctionType() == AuctionType.FORWARD_ENGISH || auctionRules.getAuctionType() == AuctionType.FORWARD_SEALED_BID) {
				auctionRules.setFowardAuction(Boolean.TRUE);
			} else {
				auctionRules.setFowardAuction(Boolean.FALSE);
			}

			if (auctionRules.getAuctionType() == AuctionType.FORWARD_DUTCH || auctionRules.getAuctionType() == AuctionType.REVERSE_DUTCH) {
				auctionRules.setItemizedBiddingWithTax(Boolean.TRUE);
			}

			model.addAttribute("eventPermissions", rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), rfaEvent.getId()));
			rfaEventService.saveAuctionRules(auctionRules);
		} catch (SubscriptionException e) {
			LOG.error("You have reached your subscription limit. " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("reached.subscription.limit", new Object[] { e.getMessage() }, Global.LOCALE));
			// return "redirect:/buyer/buyerDashboard";
		} catch (ApplicationException e) {
			redir.addFlashAttribute("error", e.getMessage());
			redir.addFlashAttribute("eventType", RfxTypes.RFA.name());
			redir.addFlashAttribute("auctionType", auctionType);
			return "redirect:/buyer/createEvent/" + RfxTypes.RFA.name();
		}
		return "redirect:/buyer/RFA/createEventDetails/" + rfaEvent.getId();
	}

	@RequestMapping(path = "/createRfaEvent", method = RequestMethod.POST)
	public String createRfaEvent(@RequestParam AuctionType auctionType, @RequestParam(value = "businessUnitId", required = false) String businessUnitId, Model model, RedirectAttributes redir) {

		RfaEvent rfaEvent = new RfaEvent();
		try {
			rfaEvent.setCreatedBy(SecurityLibrary.getLoggedInUser());
			rfaEvent.setCreatedDate(new Date());
			LOG.info("-----------------------businessUnitId----------" + businessUnitId);
			BusinessUnit businessUnit = null;
			if (StringUtils.isNotBlank(businessUnitId)) {
				businessUnit = businessUnitService.getBusinessUnitById(businessUnitId);
				rfaEvent.setBusinessUnit(businessUnit);
			}
			ErpSetup erpSetup = erpSetupService.getErpConfigBytenantId(SecurityLibrary.getLoggedInUser().getTenantId());
			if (erpSetup != null) {
				LOG.info("--------erp flag set for event-----------");
				rfaEvent.setErpEnable(erpSetup.getIsErpEnable());
			} else {
				rfaEvent.setErpEnable(false);
			}

			rfaEvent.setEventId(eventIdSettingDao.generateEventIdByBusinessUnit(SecurityLibrary.getLoggedInUserTenantId(), "RFA", rfaEvent.getBusinessUnit()));
			rfaEvent.setStatus(EventStatus.DRAFT);

			/**
			 * commented for self invite supplier cr to create public rfa event.
			 */
			// rfaEvent.setEventVisibility(EventVisibilityType.PRIVATE);

			rfaEvent.setAuctionType(auctionType);

			AuctionRules auctionRules = new AuctionRules();
			if (rfaEvent.getAuctionType() == AuctionType.FORWARD_ENGISH || rfaEvent.getAuctionType() == AuctionType.REVERSE_ENGISH || rfaEvent.getAuctionType() == AuctionType.FORWARD_SEALED_BID || rfaEvent.getAuctionType() == AuctionType.REVERSE_SEALED_BID) {
				rfaEvent.setBillOfQuantity(Boolean.TRUE);
				auctionRules.setLumsumBiddingWithTax(Boolean.FALSE);
			}
			rfaEvent = rfaEventService.saveRfaEvent(rfaEvent, SecurityLibrary.getLoggedInUser());
			rfaEventService.setDefaultEventContactDetail(SecurityLibrary.getLoggedInUser().getId(), rfaEvent.getId());
			// save Auction Rule
			auctionRules.setEvent(rfaEvent);
			auctionRules.setAuctionType(rfaEvent.getAuctionType());
			auctionRules.setBiddingPriceHigherLeadingBidType(ValueType.VALUE);
			auctionRules.setBiddingMinValueType(ValueType.VALUE);
			if (auctionRules.getAuctionType() == AuctionType.FORWARD_DUTCH || auctionRules.getAuctionType() == AuctionType.FORWARD_ENGISH || auctionRules.getAuctionType() == AuctionType.FORWARD_SEALED_BID) {
				auctionRules.setFowardAuction(Boolean.TRUE);
			} else {
				auctionRules.setFowardAuction(Boolean.FALSE);
			}

			if (auctionRules.getAuctionType() == AuctionType.FORWARD_DUTCH || auctionRules.getAuctionType() == AuctionType.REVERSE_DUTCH) {
				auctionRules.setItemizedBiddingWithTax(Boolean.TRUE);
			}

			model.addAttribute("eventPermissions", rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), rfaEvent.getId()));
			rfaEventService.saveAuctionRules(auctionRules);
		} catch (SubscriptionException e) {
			LOG.error("You have reached your subscription limit. " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("reached.subscription.limit", new Object[] { e.getMessage() }, Global.LOCALE));
			// return "redirect:/buyer/buyerDashboard";
		} catch (ApplicationException e) {
			LOG.error("Error : " + e.getMessage(), e);
			redir.addFlashAttribute("error", e.getMessage());
			return "redirect:/buyer/createEvent/" + RfxTypes.RFA.name();
			// return "redirect:/buyer/buyerDashboard";
		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
			// return "redirect:/buyer/buyerDashboard";
		}
		return "redirect:/buyer/RFA/createEventDetails/" + rfaEvent.getId();
	}

	@RequestMapping(path = "/createEventDetails/{eventId}", method = RequestMethod.GET)
	public String createRfaEventDetails(Model model, @PathVariable String eventId, HttpSession session) throws ApplicationException {

		EventPermissions eventPermissions = rfaEventService.getUserEventPemissions(SecurityLibrary.getLoggedInUser().getId(), eventId);
		model.addAttribute("eventPermissions", eventPermissions);
		LOG.info("event Id in create : " + eventId);
		return super.createEventDetailsPage(model, eventId, session, eventPermissions);
	}

	@RequestMapping(path = "/storeEventDetails", method = RequestMethod.POST)
	public ModelAndView saveRfaEvent(@ModelAttribute("event") RfaEvent rfaEvent, @RequestParam(value = "industryCateg[]") String[] industryCategories, Model model, BindingResult result, RedirectAttributes redir, HttpSession session) {
		if (rfaEvent.getViewSupplerName()) {
			rfaEvent.setViewSupplerName(false);
		} else {
			rfaEvent.setViewSupplerName(true);
		}

		model.addAttribute("event	Permissions", rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), rfaEvent.getId()));
		return saveRfaDetails(rfaEvent, model, result, redir, true, session, false, industryCategories);
	}

	/**
	 * @param rfaEvent
	 * @param industryCategory
	 * @param result
	 * @param strTimeZone
	 */
	private void autoSaveRfaDetails(RfaEvent rfaEvent, String[] industryCategory, BindingResult result, String strTimeZone) {
		try {
			TimeZone timeZone = TimeZone.getDefault();
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}
			SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("dd/MM/YYYY hh:mm a");
			dateTimeFormatter.setTimeZone(timeZone);
			if (industryCategory != null) {
				List<IndustryCategory> icList = new ArrayList<>();
				for (String industryCatId : industryCategory) {
					LOG.info("industry cat Id :" + industryCatId);
					IndustryCategory ic = new IndustryCategory();
					ic.setId(industryCatId);
					icList.add(ic);
				}
				rfaEvent.setIndustryCategories(icList);
			}

			if (result.hasErrors()) {
				LOG.error("Page submitted With Errors ............................. ");
				List<String> errMessages = new ArrayList<String>();
				for (ObjectError err : result.getAllErrors()) {
					errMessages.add(err.getDefaultMessage());
					LOG.info("ERROR : " + err.getDefaultMessage());
				}
			} else {
				if (CollectionUtil.isNotEmpty(rfaEvent.getApprovals())) {
					int level = 1;
					for (RfaEventApproval app : rfaEvent.getApprovals()) {
						app.setEvent(rfaEvent);
						app.setLevel(level++);
						if (app != null && CollectionUtil.isNotEmpty(app.getApprovalUsers()))
							for (RfaApprovalUser approvalUser : app.getApprovalUsers()) {
								approvalUser.setApproval(app);
								approvalUser.setId(null);
							}
					}
				} else {
					LOG.warn("Approval levels is empty.");
				}

			}
			if (StringUtils.checkString(rfaEvent.getId()).length() > 0) {

				RfaEvent persistObj = rfaEventService.getRfaEventByeventId(rfaEvent.getId());
				if (rfaEvent.getAuctionStartRelative() == Boolean.FALSE) {
					Date startDateTime = null;
					if (rfaEvent.getEventStart() != null && rfaEvent.getEventStartTime() != null) {
						startDateTime = DateUtil.combineDateTime(rfaEvent.getEventStart(), rfaEvent.getEventStartTime(), timeZone);
						/* persistObj.setEventStart(startDateTime); */
					}
					LOG.info("Event Start : = " + rfaEvent.getEventStart());
					rfaEvent.setEventStart(startDateTime);
					if (persistObj.getAuctionType() != AuctionType.FORWARD_DUTCH && persistObj.getAuctionType() != AuctionType.REVERSE_DUTCH) {
						Date endDateTime = null;
						if (rfaEvent.getEventEnd() != null && rfaEvent.getEventEndTime() != null) {
							endDateTime = DateUtil.combineDateTime(rfaEvent.getEventEnd(), rfaEvent.getEventEndTime(), timeZone);
							// persistObj.setEventEnd(endDateTime);
						}
						LOG.info("Event End : = " + rfaEvent.getEventEnd());
						rfaEvent.setEventEnd(endDateTime);
					}
				} else {
					if (rfaEvent.getPreviousAuction() != null && rfaEvent.getPreviousAuction().getEventEnd() != null) {
						DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
						formatter.setTimeZone(timeZone);
						Date previousEndDate = (Date) (rfaEvent.getPreviousAuction().getEventEnd());

						Calendar cal = Calendar.getInstance();
						cal.setTime(previousEndDate);

						if (rfaEvent.getAuctionStartDelayType() == DurationType.HOUR) {
							cal.add(Calendar.HOUR, (rfaEvent.getAuctionStartDelay()));
							LOG.info("event Start date : " + formatter.format(cal.getTime()));
						} else {
							cal.add(Calendar.MINUTE, (rfaEvent.getAuctionStartDelay()));
							LOG.info("event Start date 3: " + formatter.format(cal.getTime()));
						}
						// persistObj.setEventStart(cal.getTime());
						rfaEvent.setEventStart(cal.getTime());

						if (rfaEvent.getAuctionType() != AuctionType.FORWARD_DUTCH && rfaEvent.getAuctionType() != AuctionType.REVERSE_DUTCH) {
							cal.setTime(rfaEvent.getEventStart());

							if (rfaEvent.getAuctionDurationType() == DurationType.HOUR) {
								cal.add(Calendar.HOUR, (rfaEvent.getAuctionDuration()));
								LOG.info("event Start date : " + formatter.format(cal.getTime()));
							} else {
								cal.add(Calendar.MINUTE, (rfaEvent.getAuctionDuration()));
								LOG.info("event Start date 3: " + formatter.format(cal.getTime()));
							}
							rfaEvent.setEventEnd(cal.getTime());
						}
					}
				}

				Date notificationDateTime = null;
				if (rfaEvent.getEventPublishDate() != null && rfaEvent.getEventPublishTime() != null) {
					notificationDateTime = DateUtil.combineDateTime(rfaEvent.getEventPublishDate(), rfaEvent.getEventPublishTime(), timeZone);
				}
				rfaEvent.setEventPublishDate(notificationDateTime);

				setAndUpdateRfaEvent(rfaEvent, persistObj);

			}
		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
		}
	}

	private ModelAndView saveRfaDetails(RfaEvent rfaEvent, Model model, BindingResult result, RedirectAttributes redir, boolean goNext, HttpSession session, boolean isDraft, String[] industryCat) {
		String next = "";
		LOG.info("start Event : Bq : " + rfaEvent.getBillOfQuantity() + " : cq : " + rfaEvent.getQuestionnaires() + " : Doc : " + rfaEvent.getDocumentReq() + " : Meet : " + rfaEvent.getMeetingReq());
		TimeZone timeZone = TimeZone.getDefault();
		String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}
		SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("dd/MM/YYYY hh:mm a");
		dateTimeFormatter.setTimeZone(timeZone);
		try {
			model.addAttribute("reminderList", rfaEventService.getAllRfaEventReminderForEvent(rfaEvent.getId(), Boolean.FALSE));
			model.addAttribute("startReminderList", rfaEventService.getAllRfaEventReminderForEvent(rfaEvent.getId(), Boolean.TRUE));
		} catch (ApplicationException e1) {
			LOG.error("Error : " + e1.getMessage(), e1);
		}
		LOG.info("rfa starasdasd : " + rfaEvent.getAuctionStartRelative());
		model.addAttribute("intervalType", IntervalType.values());
		model.addAttribute("durationType", DurationType.values());
		model.addAttribute("eventPermissions", rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), rfaEvent.getId()));
		List<RfaEventContact> eventContactsList = rfaEventService.getAllContactForEvent(rfaEvent.getId());
		model.addAttribute("eventContactsList", eventContactsList);
		model.addAttribute("evaluationDeclaratonList", declarationService.getDeclarationsByTypeForTenant(DeclarationType.EVALUATION_PROCESS, SecurityLibrary.getLoggedInUserTenantId()));
		model.addAttribute("supplierDeclaratonList", declarationService.getDeclarationsByTypeForTenant(DeclarationType.SUPPLIER_ACCEPTANCE, SecurityLibrary.getLoggedInUserTenantId()));

		RfaEventContact eventContact = new RfaEventContact();
		eventContact.setRfaEvent(rfaEvent);
		eventContact.setEventId(rfaEvent.getId());
		model.addAttribute("eventContact", eventContact);
		if (rfaEvent.getDeliveryAddress() != null) {
			BuyerAddress buyerAddress = buyerAddressService.getBuyerAddress(rfaEvent.getDeliveryAddress().getId());
			model.addAttribute("buyerAddress", buyerAddress);
		}
		List<BuyerAddressPojo> addressList = buyerAddressService.getAllBuyerAddressPojo(SecurityLibrary.getLoggedInUserTenantId());
		model.addAttribute("addressList", addressList);
		LOG.info("rfa temlate to" + rfaEvent.getTemplate().toLogString());
		model.addAttribute("currency", currencyService.getAllCurrency());

		if (industryCat != null) {
			List<IndustryCategory> icList = new ArrayList<>();
			for (String industryCatId : industryCat) {
				LOG.info("industry cat Id :" + industryCatId);
				IndustryCategory ic = new IndustryCategory();
				ic.setId(industryCatId);
				icList.add(ic);
			}
			rfaEvent.setIndustryCategories(icList);

			if (rfaEvent.getStatus() == EventStatus.DRAFT) {
				checkToAddSupplier(rfaEvent.getId(), industryCat, rfaEvent.getIndustryCategories(), rfaEvent.getTemplate() != null ? rfaEvent.getTemplate().getId() : null);
			}
		}
		try {
			constructDefaultModel(model, rfaEvent, RfxTypes.RFA, null);
		} catch (JsonProcessingException e1) {
			LOG.info("Error :" + e1.getMessage(), e1);
		}

		// to load assigned member at time of error
		// List<User> userTeamMemberList =
		// userService.fetchAllActiveUsersForTenant(SecurityLibrary.getLoggedInUserTenantId());
		List<User> userTeamMemberList = new ArrayList<User>(); // userService.fetchAllActiveNormalUsersForTenant(SecurityLibrary.getLoggedInUserTenantId());
		List<UserPojo> userListSumm = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", UserType.NORMAL_USER);
		List<User> approvalUsers = new ArrayList<User>();
		List<User> suspApprovalUsers = new ArrayList<User>();
		List<User> awardApprovalUsers = new ArrayList<User>();
		List<UserPojo> maskingUserList = new ArrayList<UserPojo>();
		List<UserPojo> evaluationConclusionUserList = new ArrayList<UserPojo>();
		if (rfaEvent.getUnMaskedUser() != null) {
			maskingUserList.add(new UserPojo(rfaEvent.getUnMaskedUser().getId(), rfaEvent.getUnMaskedUser().getLoginId(), rfaEvent.getUnMaskedUser().getName(), rfaEvent.getUnMaskedUser().getTenantId(), rfaEvent.getUnMaskedUser().isDeleted(), rfaEvent.getUnMaskedUser().getCommunicationEmail(), rfaEvent.getUnMaskedUser().getEmailNotifications()));
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

		if (rfaEvent.getApprovals() != null) {
			for (RfaEventApproval app : rfaEvent.getApprovals()) {
				for (RfaApprovalUser appU : app.getApprovalUsers()) {
					if (!approvalUsers.contains(appU.getUser())) {
						approvalUsers.add(appU.getUser());
					}
				}
			}
		}

		if (rfaEvent.getSuspensionApprovals() != null) {
			for (RfaEventSuspensionApproval suspApp : rfaEvent.getSuspensionApprovals()) {
				for (RfaSuspensionApprovalUser suspAppU : suspApp.getApprovalUsers()) {
					if (!suspApprovalUsers.contains(suspAppU.getUser())) {
						suspApprovalUsers.add(suspAppU.getUser());
					}
				}
			}
		}

		List<RfaUnMaskedUser> unMaskedUsers = rfaEvent.getUnMaskedUsers();
		if (CollectionUtil.isNotEmpty(unMaskedUsers)) {
			for (RfaUnMaskedUser user : unMaskedUsers) {
				user.setEvent(rfaEvent);
				UserPojo u = new UserPojo(user.getUser().getId(), user.getUser().getLoginId(), user.getUser().getName(), user.getUser().getTenantId(), user.getUser().isDeleted(), user.getUser().getCommunicationEmail(), user.getUser().getEmailNotifications());
				if (!maskingUserList.contains(u)) {
					maskingUserList.add(u);
				}
			}
		}

		List<RfaEvaluationConclusionUser> evaluationConclusionUsers = rfaEvent.getEvaluationConclusionUsers();
		if (CollectionUtil.isNotEmpty(evaluationConclusionUsers)) {
			for (RfaEvaluationConclusionUser user : evaluationConclusionUsers) {
				user.setEvent(rfaEvent);
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
		// model.addAttribute("userList1", userTeamMemberList);
		LOG.info("User member in withourt :  " + userTeamMemberList.size());
		RfaEvent eventObj = rfaEventService.loadRfaEventById(rfaEvent.getId());
		if (CollectionUtil.isNotEmpty(eventObj.getTeamMembers())) {
			List<User> assignedTeamMembers = new ArrayList<>();
			List<RfaTeamMember> userTeamMembers = new ArrayList<>();
			for (RfaTeamMember rfaTeamMember : eventObj.getTeamMembers()) {
				try {
					assignedTeamMembers.add((User) rfaTeamMember.getUser().clone());
					userTeamMembers.add(rfaTeamMember);
				} catch (Exception e) {
					LOG.error("Error :  " + e.getMessage(), e);
				}
			}
			rfaEvent.setTeamMembers(userTeamMembers);
			LOG.info("User member in if condition :  " + userTeamMemberList.size());
			userTeamMemberList.removeAll(assignedTeamMembers);
			model.addAttribute("userTeamMemberList", userTeamMemberList);
		} else {
			model.addAttribute("userTeamMemberList", userTeamMemberList);
			LOG.info("User member in else condition :  " + userTeamMemberList.size());

		}

		LOG.info("Rfa event tostring : " + rfaEvent.toString());
		if (rfaEvent.getTemplate() != null && rfaEvent.getTemplate().getId() != null) {
			LOG.info("inside to check template");
			RfxTemplate rfxTemplate = rfxTemplateService.getRfxTemplateForEditById(rfaEvent.getTemplate().getId(), SecurityLibrary.getLoggedInUserTenantId());

			if (!rfxTemplate.getVisibleCloseEnvelope()) {
				rfaEvent.setCloseEnvelope(rfxTemplate.getCloseEnvelope());
			}
			if (!rfxTemplate.getVisibleViewSupplierName()) {
				rfaEvent.setViewSupplerName(rfxTemplate.getViewSupplerName());
			}

			if (!rfxTemplate.getVisibleAllowToSuspendEvent()) {
				rfaEvent.setAllowToSuspendEvent(rfxTemplate.getAllowToSuspendEvent());
			}
			if (!rfxTemplate.getVisibleViewAuctionHall()) {
				rfaEvent.setViewAuctionHall(rfxTemplate.getViewAuctionHall());
			}

			if (!rfxTemplate.getVisibleRevertLastBid()) {
				rfaEvent.setRevertLastBid(rfxTemplate.getRevertLastBid());
			}

			model.addAttribute("templateFields", rfxTemplate.getFields());
			model.addAttribute("rfxTemplate", rfxTemplate);
		}
		rfaEvent.setRfxEnvelopeOpening(rfaEvent.getRfxEnvelopeOpening());
		rfaEvent.setRfxEnvOpeningAfter(rfaEvent.getRfxEnvOpeningAfter());
		rfaEvent.setAllowDisqualifiedSupplierDownload(rfaEvent.getAllowDisqualifiedSupplierDownload());

		if (result.hasErrors()) {
			LOG.error("Page submitted With Errors ............................. ");
			List<String> errMessages = new ArrayList<String>();
			for (ObjectError err : result.getAllErrors()) {
				errMessages.add(err.getDefaultMessage());
				LOG.info("ERROR : " + err.getDefaultMessage());
			}
			return new ModelAndView("createEventDetails" + rfaEvent.getId());
		} else {
			LOG.info("Page submitted with no errors ....................................... ");
			try {

				LOG.info("rfaEvent.getApprovals(): " + rfaEvent.getApprovals());
				if (CollectionUtil.isNotEmpty(rfaEvent.getApprovals())) {
					int level = 1;
					for (RfaEventApproval app : rfaEvent.getApprovals()) {
						app.setEvent(rfaEvent);
						app.setLevel(level++);
						if (app != null && CollectionUtil.isNotEmpty(app.getApprovalUsers()))
							for (RfaApprovalUser approvalUser : app.getApprovalUsers()) {
								approvalUser.setApproval(app);
								approvalUser.setId(null);
							}
					}
				} else {
					LOG.warn("Approval levels is empty.");
				}
				boolean eventCatError = false;
				if (!isDraft && CollectionUtil.isEmpty(rfaEvent.getIndustryCategories())) {
					model.addAttribute("error", messageSource.getMessage("event.one.industy.required", new Object[] {}, Global.LOCALE));
					if (rfaEvent.getViewSupplerName()) {
						rfaEvent.setViewSupplerName(false);
					} else {
						rfaEvent.setViewSupplerName(true);
					}
					LOG.error("Error: " + messageSource.getMessage("event.one.industy.required", new Object[] {}, Global.LOCALE));
					eventCatError = true;
				}
				if (!doValidate(rfaEvent, model, isDraft) && !eventCatError) {
					if (StringUtils.checkString(rfaEvent.getId()).length() > 0) {
						LOG.info("rfaEvent update:      " + rfaEvent.getId());
						RfaEvent persistObj = rfaEventService.getRfaEventByeventId(rfaEvent.getId());
						if (rfaEvent.getAuctionStartRelative() == Boolean.FALSE) {
							Date startDateTime = null;
							if (rfaEvent.getEventStart() != null && rfaEvent.getEventStartTime() != null) {
								startDateTime = DateUtil.combineDateTime(rfaEvent.getEventStart(), rfaEvent.getEventStartTime(), timeZone);
								/* persistObj.setEventStart(startDateTime); */
							}
							LOG.info("Event Start : = " + rfaEvent.getEventStart());
							rfaEvent.setEventStart(startDateTime);
							if (persistObj.getAuctionType() != AuctionType.FORWARD_DUTCH && persistObj.getAuctionType() != AuctionType.REVERSE_DUTCH) {
								Date endDateTime = null;
								if (rfaEvent.getEventEnd() != null && rfaEvent.getEventEndTime() != null) {
									endDateTime = DateUtil.combineDateTime(rfaEvent.getEventEnd(), rfaEvent.getEventEndTime(), timeZone);
									// persistObj.setEventEnd(endDateTime);
								}
								LOG.info("Event End : = " + rfaEvent.getEventEnd());
								rfaEvent.setEventEnd(endDateTime);
							}
						} else {
							// RfaEvent previousAuction = persistObj.getPreviousAuction();
							// LOG.info("previous Event : " + rfaEvent.getPreviousAuction().getId());
							// LOG.info("previous Event startDate : " + rfaEvent.getPreviousAuction().getEventEnd());
							DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
							formatter.setTimeZone(timeZone);
							Date previousEndDate = (Date) (rfaEvent.getPreviousAuction().getEventEnd());

							Calendar cal = Calendar.getInstance();
							cal.setTime(previousEndDate);

							if (rfaEvent.getAuctionStartDelayType() == DurationType.HOUR) {
								cal.add(Calendar.HOUR, (rfaEvent.getAuctionStartDelay()));
								LOG.info("event Start date : " + formatter.format(cal.getTime()));
							} else {
								cal.add(Calendar.MINUTE, (rfaEvent.getAuctionStartDelay()));
								LOG.info("event Start date 3: " + formatter.format(cal.getTime()));
							}
							// persistObj.setEventStart(cal.getTime());
							rfaEvent.setEventStart(cal.getTime());

							if (rfaEvent.getAuctionType() != AuctionType.FORWARD_DUTCH && rfaEvent.getAuctionType() != AuctionType.REVERSE_DUTCH) {
								// AuctionRules auctionRules =
								// rfaEventService.getLeanAuctionRulesByEventId(rfaEvent.getId());
								// Date endDate =
								// rfaEventService.calculateEndDateForDutchAuction(auctionRules.getInterval(),
								// auctionRules.getIntervalType(), auctionRules.getDutchAuctionTotalStep(),
								// rfaEvent.getEventStart());

								// persistObj.setEventEnd(cal.getTime());

								cal.setTime(rfaEvent.getEventStart());

								if (rfaEvent.getAuctionDurationType() == DurationType.HOUR) {
									cal.add(Calendar.HOUR, (rfaEvent.getAuctionDuration()));
									LOG.info("event Start date : " + formatter.format(cal.getTime()));
								} else {
									cal.add(Calendar.MINUTE, (rfaEvent.getAuctionDuration()));
									LOG.info("event Start date 3: " + formatter.format(cal.getTime()));
								}
								rfaEvent.setEventEnd(cal.getTime());
							}
						}
						DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
						dateFormatter.setTimeZone(timeZone);
						if (persistObj.getMeetingReq()) {
							List<RfaEventMeeting> eventMeetings = rfaMeetingService.getAllRfaMeetingByEventId(persistObj.getId());
							if (CollectionUtil.isNotEmpty(eventMeetings)) {
								LOG.info("Enter in Log  :  " + eventMeetings.size());
								LOG.info("Event date  :  " + persistObj.getEventStart());
								Date meetingMinDate = rfaMeetingService.getMinMeetingDateForEvent(persistObj.getId());
								LOG.info("Meeting min date :  " + meetingMinDate);
								if (meetingMinDate != null && rfaEvent.getEventPublishDate() != null && rfaEvent.getEventPublishDate().after(meetingMinDate)) {
									redir.addFlashAttribute("warn", messageSource.getMessage("rftEvent.error.minmeetingdate", new Object[] { dateFormatter.format(meetingMinDate) }, Global.LOCALE));
									// return new ModelAndView("createEventDetails", "event", rfaEvent);
								}

								Date meetingMaxDate = rfaMeetingService.getMaxMeetingDateForEvent(persistObj.getId());
								if (meetingMaxDate != null && rfaEvent.getEventEnd() != null && persistObj.getEventEnd().before(meetingMaxDate)) {
									redir.addFlashAttribute("error", messageSource.getMessage("rftEvent.error.maxmeetingdate", new Object[] { dateFormatter.format(meetingMaxDate) }, Global.LOCALE));
								}
							}
						}


						if(rfaEvent.getEventStart() != null && rfaEvent.getEventEnd() != null) {
							List<RfaReminder> reminderList = rfaEventService.getAllRfaEventReminderForEvent(rfaEvent.getId());
							DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
							formatter.setTimeZone(timeZone);
							Date startDate = rfaEvent.getEventStart();
							Date endDate = rfaEvent.getEventEnd();

							if (CollectionUtil.isNotEmpty(reminderList)) {
								for (RfaReminder reminder : reminderList) {
									reminder.getReminderDate();
									formatter.setTimeZone(timeZone);

									Calendar cal = Calendar.getInstance(timeZone);
									if (reminder.getStartReminder() != null && reminder.getStartReminder() == Boolean.TRUE) {
										cal.setTime(startDate);
										if (reminder.getReminderDate().compareTo(startDate) > 0) {
											model.addAttribute("error", messageSource.getMessage("event.reminder.pastdate", new Object[]{}, Global.LOCALE));
											rfaEvent.setViewSupplerName(rfaEvent.getViewSupplerName() ? false : true);
											return new ModelAndView("createEventDetails", "event", rfaEvent);
										}
									} else {
										cal.setTime(endDate);
										if (reminder.getReminderDate().compareTo(endDate) > 0) {
											model.addAttribute("error", messageSource.getMessage("event.reminder.pastdate", new Object[]{}, Global.LOCALE));
											rfaEvent.setViewSupplerName(rfaEvent.getViewSupplerName() ? false : true);
											return new ModelAndView("createEventDetails", "event", rfaEvent);
										}
									}
									if (reminder.getIntervalType() == IntervalType.DAYS) {
										cal.add(Calendar.DATE, -(reminder.getInterval()));
										LOG.info("Reminder : " + formatter.format(cal.getTime()));
									} else {
										cal.add(Calendar.HOUR, -(reminder.getInterval()));
										LOG.info("Reminder  Hous: " + formatter.format(cal.getTime()));
									}

									if (EventStatus.SUSPENDED == rfaEvent.getStatus() && reminder.getStartReminder()) {
										continue;
									} else if (cal.getTime().compareTo(new Date()) < 0) {
										model.addAttribute("error", messageSource.getMessage("event.reminder.pastdate", new Object[]{}, Global.LOCALE));
										rfaEvent.setViewSupplerName(rfaEvent.getViewSupplerName() ? false : true);
										return new ModelAndView("createEventDetails", "event", rfaEvent);
									}
								}
							}
						}

						LOG.info("Suppplier Notification Time : " + rfaEvent.getEventPublishTime());

						Date notificationDateTime = null;
						if (rfaEvent.getEventPublishDate() != null && rfaEvent.getEventPublishTime() != null) {
							notificationDateTime = DateUtil.combineDateTime(rfaEvent.getEventPublishDate(), rfaEvent.getEventPublishTime(), timeZone);
						}
						LOG.info("publist date : " + notificationDateTime + "   stratas date : " + rfaEvent.getEventStart());
						rfaEvent.setEventPublishDate(notificationDateTime);

						LOG.info("Event Status():" + rfaEvent.getStatus());
						if (rfaEvent.getStatus() == EventStatus.DRAFT) {
							LOG.info("Event Status():" + rfaEvent.getStatus());
							if (!isDraft && rfaEvent.getEventPublishDate() != null && rfaEvent.getEventPublishDate().before(new Date())) {
								// Published date cannot be in the past
								LOG.info("EVENT startDate:" + rfaEvent.getEventEnd());
								model.addAttribute("error", messageSource.getMessage("rftEvent.error.publishDate", new Object[] { dateTimeFormatter.format(rfaEvent.getEventPublishDate()) }, Global.LOCALE));

								if (rfaEvent.getViewSupplerName()) {
									rfaEvent.setViewSupplerName(false);
								} else {
									rfaEvent.setViewSupplerName(true);
								}

								return new ModelAndView("createEventDetails", "event", rfaEvent);
							}
							if (!isDraft && rfaEvent.getEventStart() != null && rfaEvent.getEventStart().before(new Date())) {
								// Event start date cannot be in the past
								LOG.info("EVENT startDate:" + rfaEvent.getEventStart());
								model.addAttribute("error", messageSource.getMessage("rftEvent.error.startdate", new Object[] { dateTimeFormatter.format(rfaEvent.getEventStart()) }, Global.LOCALE));
								if (rfaEvent.getViewSupplerName()) {
									rfaEvent.setViewSupplerName(false);
								} else {
									rfaEvent.setViewSupplerName(true);
								}
								return new ModelAndView("createEventDetails", "event", rfaEvent);
							}
						}

						if (!isDraft && rfaEvent.getEventStart() != null && rfaEvent.getDeliveryDate() != null && rfaEvent.getEventStart().after(rfaEvent.getDeliveryDate())) {
							LOG.info("EVENT deliveryDate:" + rfaEvent.getDeliveryDate());
							model.addAttribute("error", messageSource.getMessage("rftEvent.error.deliverydate", new Object[] { dateTimeFormatter.format(rfaEvent.getDeliveryDate()) }, Global.LOCALE));
							if (rfaEvent.getViewSupplerName()) {
								rfaEvent.setViewSupplerName(false);
							} else {
								rfaEvent.setViewSupplerName(true);
							}
							return new ModelAndView("createEventDetails", "event", rfaEvent);
						}
						if (!isDraft && rfaEvent.getEventStart() != null && rfaEvent.getEventEnd() != null && rfaEvent.getEventStart().after(rfaEvent.getEventEnd())) {
							LOG.info("EVENT endDate:" + rfaEvent.getEventEnd());
							model.addAttribute("error", messageSource.getMessage("rftEvent.error.enddate", new Object[] { dateTimeFormatter.format(rfaEvent.getEventEnd()) }, Global.LOCALE));
							if (rfaEvent.getViewSupplerName()) {
								rfaEvent.setViewSupplerName(false);
							} else {
								rfaEvent.setViewSupplerName(true);
							}
							return new ModelAndView("createEventDetails", "event", rfaEvent);
						}
						// To check time for 1 day event
						if (!isDraft && rfaEvent.getEventStart() != null && rfaEvent.getEventEnd() != null) {
							Date endDate = rfaEvent.getEventEnd();
							Date startDate = rfaEvent.getEventStart();

							LOG.info("EVENT endDate:" + endDate);
							SimpleDateFormat df = new SimpleDateFormat("dd/MM/YYYY hh:mm a");

							if (df.format(endDate).equals(df.format(startDate))) {
								LOG.info("EVENT endDate: and Event start date cannot be same" + endDate);
								model.addAttribute("error", messageSource.getMessage("rftEvent.error.endtime", new Object[] { df.format(endDate) }, Global.LOCALE));
								if (rfaEvent.getViewSupplerName()) {
									rfaEvent.setViewSupplerName(false);
								} else {
									rfaEvent.setViewSupplerName(true);
								}
								return new ModelAndView("createEventDetails", "event", rfaEvent);
							}
						}
						LOG.info("publish date : " + notificationDateTime + "   stratas date : " + persistObj.getEventStart());
						// persistObj.setEventPublishDate(rfaEvent.getEventPublishDate());
						LOG.info("Suppplier publish Date : " + rfaEvent.getEventPublishDate());
						if (!isDraft && notificationDateTime != null && notificationDateTime.after(rfaEvent.getEventStart())) {
							LOG.info("publist date  : " + notificationDateTime + "   stratas date : " + rfaEvent.getEventStart());
							model.addAttribute("error", messageSource.getMessage("rfaEvent.error.notificationdate", new Object[] { notificationDateTime }, Global.LOCALE));
							if (rfaEvent.getViewSupplerName()) {
								rfaEvent.setViewSupplerName(false);
							} else {
								rfaEvent.setViewSupplerName(true);
							}
							return new ModelAndView("createEventDetails", "event", rfaEvent);
						}

//						if (Boolean.TRUE == persistObj.getEnableSuspensionApproval() && rfaEvent.getSuspensionApprovals() == null) {
//							model.addAttribute("error", messageSource.getMessage("susp.approval.empty", new Object[] {}, Global.LOCALE));
//							return new ModelAndView("createEventDetails", "event", rfaEvent);
//						}

						setAndUpdateRfaEvent(rfaEvent, persistObj);
						if (goNext) {
							next = "redirect:auctionRules/" + persistObj.getId();
						} else {
							redir.addFlashAttribute("success", messageSource.getMessage("rftEvent.success.savedraft", new Object[] { (rfaEvent.getEventName() != null ? rfaEvent.getEventName() : rfaEvent.getEventId()) }, Global.LOCALE));
							next = "redirect:createEventDetails/" + persistObj.getId();
						}
					}

				} else {
					return new ModelAndView("createEventDetails");
				}

			} catch (Exception e) {
				LOG.error("Error while storing RftEvent : " + e.getMessage(), e);
				model.addAttribute("errors", "Error while storing Rfa Event details for : " + rfaEvent.getEventName() + ", message : " + e.getMessage());
				return new ModelAndView("createEventDetails", "event", rfaEvent);
			}
			try {
				LOG.info("**********:" + rfaEvent.getId());
				if (!isDraft && StringUtils.checkString(rfaEvent.getId()).length() > 0) {
					rfaEventService.insertTimeLine(rfaEvent.getId());
				}
			} catch (Exception e) {
				LOG.error("Error : " + e.getMessage(), e);
			}
			return new ModelAndView(next);
		}
	}

	private void setAndUpdateRfaEvent(RfaEvent rfaEvent, RfaEvent persistObj) {
		LOG.info("Rfa Event : Bq : " + rfaEvent.getBillOfQuantity() + " : cq : " + rfaEvent.getQuestionnaires() + " : Doc : " + rfaEvent.getDocumentReq() + " : Meet : " + rfaEvent.getMeetingReq());
		persistObj.setModifiedDate(new Date());
		persistObj.setUnMaskedUsers(rfaEvent.getUnMaskedUsers());
		if (Boolean.TRUE == rfaEvent.getEnableEvaluationConclusionUsers()) {
			persistObj.setEvaluationConclusionUsers(rfaEvent.getEvaluationConclusionUsers());
		} else {
			persistObj.setEvaluationConclusionUsers(null);
		}
		persistObj.setEnableEvaluationConclusionUsers(rfaEvent.getEnableEvaluationConclusionUsers());
		persistObj.setIndustryCategory(rfaEvent.getIndustryCategory());
		// persistObj.setStatus(EventStatus.DRAFT); -- DONT SET THE STATUS AS DRAFT AS THIS COULD BE A SUSPENDED EVENT
		// EDIT - @Nitin Otageri

		if ((AuctionType.FORWARD_DUTCH == persistObj.getAuctionType() || AuctionType.REVERSE_DUTCH == persistObj.getAuctionType())) {
			if (rfaEvent.getEventStart() != null) {
				AuctionRules auctionRules = rfaEventService.getAuctionRulesByEventId(rfaEvent.getId());
				if (auctionRules != null && auctionRules.getInterval() != null && auctionRules.getIntervalType() != null && auctionRules.getDutchAuctionTotalStep() != null) {
					Date endDate = rfaEventService.calculateEndDateForDutchAuction(auctionRules.getInterval(), auctionRules.getIntervalType(), auctionRules.getDutchAuctionTotalStep(), rfaEvent.getEventStart());
					persistObj.setEventEnd(endDate);
				}
			}
		} else {
			persistObj.setEventEnd(rfaEvent.getEventEnd());
		}
		LOG.info("StartDate:" + rfaEvent.getEventStart());
		LOG.info("EndDate:" + rfaEvent.getEventEnd());
		persistObj.setEventVisibility(rfaEvent.getEventVisibility());
		persistObj.setEventName(rfaEvent.getEventName());
		persistObj.setReferanceNumber(rfaEvent.getReferanceNumber());
		persistObj.setUrgentEvent(rfaEvent.getUrgentEvent());
		persistObj.setEventVisibilityDates(rfaEvent.getEventVisibilityDates());
		persistObj.setDeliveryAddress(rfaEvent.getDeliveryAddress());
		persistObj.setParticipationFeeCurrency(rfaEvent.getParticipationFeeCurrency());
		persistObj.setParticipationFees(rfaEvent.getParticipationFees());
		persistObj.setDepositCurrency(rfaEvent.getDepositCurrency());
		persistObj.setDeposit(rfaEvent.getDeposit());
		persistObj.setRfxEnvelopeOpening(rfaEvent.getRfxEnvelopeOpening());
		persistObj.setRfxEnvOpeningAfter(rfaEvent.getRfxEnvOpeningAfter());
		persistObj.setEnableEvaluationDeclaration(rfaEvent.getEnableEvaluationDeclaration());
		persistObj.setEnableSupplierDeclaration(rfaEvent.getEnableSupplierDeclaration());
		persistObj.setEvaluationProcessDeclaration(Boolean.TRUE == rfaEvent.getEnableEvaluationDeclaration() && rfaEvent.getEvaluationProcessDeclaration() != null ? rfaEvent.getEvaluationProcessDeclaration() : null);
		persistObj.setSupplierAcceptanceDeclaration(Boolean.TRUE == rfaEvent.getEnableSupplierDeclaration() && rfaEvent.getSupplierAcceptanceDeclaration() != null ? rfaEvent.getSupplierAcceptanceDeclaration() : null);
		if (persistObj.getTemplate() == null) {
			persistObj.setEnableSuspensionApproval(rfaEvent.getEnableSuspensionApproval());
		}
		persistObj.setAllowDisqualifiedSupplierDownload(rfaEvent.getAllowDisqualifiedSupplierDownload());

		// Should not assign this
		if (persistObj.getTemplate() != null && persistObj.getTemplate().getId() != null) {
			RfxTemplate rfxTemplate = rfxTemplateService.getRfxTemplateForEditById(persistObj.getTemplate().getId(), SecurityLibrary.getLoggedInUserTenantId());

			if (!rfxTemplate.getVisibleCloseEnvelope()) {
				rfaEvent.setCloseEnvelope(rfxTemplate.getCloseEnvelope());
			}
			if (!rfxTemplate.getVisibleViewSupplierName()) {
				rfaEvent.setViewSupplerName(rfxTemplate.getViewSupplerName());
			}
			if (!rfxTemplate.getVisibleAllowToSuspendEvent()) {
				rfaEvent.setAllowToSuspendEvent(rfxTemplate.getAllowToSuspendEvent());
			}
			if (!rfxTemplate.getVisibleViewAuctionHall()) {
				rfaEvent.setViewAuctionHall(rfxTemplate.getViewAuctionHall());
			}
			if (!rfxTemplate.getVisibleRevertLastBid()) {
				rfaEvent.setRevertLastBid(rfxTemplate.getRevertLastBid());
			}
		}

		persistObj.setEventPublishDate(rfaEvent.getEventPublishDate());
		persistObj.setSubmissionValidityDays(rfaEvent.getSubmissionValidityDays());
		persistObj.setEventDescription(rfaEvent.getEventDescription());
		persistObj.setBudgetAmount(rfaEvent.getBudgetAmount());
		persistObj.setBaseCurrency(rfaEvent.getBaseCurrency());
		persistObj.setHistoricaAmount(rfaEvent.getHistoricaAmount());
		persistObj.setEstimatedBudget(rfaEvent.getEstimatedBudget());
		persistObj.setDecimal(rfaEvent.getDecimal());
		persistObj.setCostCenter(rfaEvent.getCostCenter());
		persistObj.setBusinessUnit(rfaEvent.getBusinessUnit());
		persistObj.setPaymentTerm(rfaEvent.getPaymentTerm());
		persistObj.setCloseEnvelope(rfaEvent.getCloseEnvelope());
		persistObj.setGroupCode(rfaEvent.getGroupCode());
		persistObj.setScheduleOfRate(rfaEvent.getScheduleOfRate());
		// Event Timeline
		// Date notificationDateTime = DateUtil.combineDateTime(rfaEvent.getSupplierNotificationDate(),
		// rfaEvent.getSupplierNotificationTime());
		// persistObj.setSupplierNotificationDate(notificationDateTime);
		persistObj.setAuctionDuration(rfaEvent.getAuctionDuration());
		persistObj.setAuctionDurationType(rfaEvent.getAuctionDurationType());
		persistObj.setAuctionStartDelayType(rfaEvent.getAuctionStartDelayType());
		persistObj.setAuctionStartRelative(rfaEvent.getAuctionStartRelative());
		persistObj.setCloseEnvelope(rfaEvent.getCloseEnvelope());
		persistObj.setInternalRemarks(rfaEvent.getInternalRemarks());
		if (rfaEvent.getAuctionStartRelative() == Boolean.TRUE) {
			// Remove Start reminders
			if (CollectionUtil.isNotEmpty(persistObj.getRfaStartReminder())) {
				persistObj.getRfaStartReminder().clear();
				persistObj.setRfaStartReminder(null);
			}
		} else if (rfaEvent.getAuctionStartRelative() == Boolean.FALSE) {
			rfaEvent.setPreviousAuction(null);
			rfaEvent.setAuctionStartDelay(null);
		}
		persistObj.setEventStart(rfaEvent.getEventStart());
		persistObj.setAuctionStartDelay(rfaEvent.getAuctionStartDelay());
		persistObj.setPreviousAuction(rfaEvent.getPreviousAuction());
		// Time Extension
		persistObj.setTimeExtensionType(rfaEvent.getTimeExtensionType());
		persistObj.setTimeExtensionDuration(rfaEvent.getTimeExtensionDuration());
		persistObj.setTimeExtensionDurationType(rfaEvent.getTimeExtensionDurationType());
		persistObj.setAutoDisqualify(rfaEvent.getAutoDisqualify());
		persistObj.setBidderDisqualify(rfaEvent.getBidderDisqualify());
		persistObj.setExtensionCount(rfaEvent.getExtensionCount());
		persistObj.setTimeExtensionLeadingBidType(rfaEvent.getTimeExtensionLeadingBidType());
		persistObj.setTimeExtensionLeadingBidValue(rfaEvent.getTimeExtensionLeadingBidValue());
		if (rfaEvent.getStatus() != EventStatus.SUSPENDED) {
			persistObj.setApprovals(rfaEvent.getApprovals());
		}

		if (Boolean.TRUE == persistObj.getEnableSuspensionApproval() || Boolean.TRUE == rfaEvent.getEnableSuspensionApproval()) {
			if (CollectionUtil.isNotEmpty(rfaEvent.getSuspensionApprovals())) {
				int suspLevel = 1;
				for (RfaEventSuspensionApproval app : rfaEvent.getSuspensionApprovals()) {
					app.setEvent(rfaEvent);
					app.setLevel(suspLevel++);
					if (app != null && CollectionUtil.isNotEmpty(app.getApprovalUsers()))
						for (RfaSuspensionApprovalUser approvalUser : app.getApprovalUsers()) {
							approvalUser.setApproval(app);
							approvalUser.setId(null);
						}
				}
			} else {
				LOG.warn("Suspension Approval levels is empty.");
			}
		} else {
			LOG.info("Clrearing Susp Approvels >>>>>>>>>>>>>>>>>>>> .");
			if (CollectionUtil.isNotEmpty(rfaEvent.getSuspensionApprovals())) {
				rfaEvent.getSuspensionApprovals().clear();
			}
		}

		persistObj.setSuspensionApprovals(rfaEvent.getSuspensionApprovals());

		rfaEvent.setRfxEnvelopeOpening(rfaEvent.getRfxEnvelopeOpening());
		rfaEvent.setRfxEnvOpeningAfter(rfaEvent.getRfxEnvOpeningAfter());
		rfaEvent.setAllowDisqualifiedSupplierDownload(rfaEvent.getAllowDisqualifiedSupplierDownload());

		// Event Req

		if (rfaEvent.getAuctionType() == AuctionType.FORWARD_DUTCH || rfaEvent.getAuctionType() == AuctionType.REVERSE_DUTCH) {
			persistObj.setBillOfQuantity(rfaEvent.getBillOfQuantity());
		}
		persistObj.setQuestionnaires(rfaEvent.getQuestionnaires());
		persistObj.setDocumentReq(rfaEvent.getDocumentReq());
		persistObj.setMeetingReq(rfaEvent.getMeetingReq());
		persistObj.setEventDetailCompleted(Boolean.TRUE);
		persistObj.setIndustryCategories(rfaEvent.getIndustryCategories());
		persistObj.setDeliveryDate(rfaEvent.getDeliveryDate());
		persistObj.setViewAuctionHall(rfaEvent.getViewAuctionHall());
		persistObj.setViewSupplerName(rfaEvent.getViewSupplerName());
		LOG.info("UnMasked User : " + rfaEvent.getViewSupplerName());
		if (Boolean.TRUE == rfaEvent.getViewSupplerName()) {
			persistObj.setUnMaskedUser(null);
		} else {
			persistObj.setUnMaskedUser(rfaEvent.getUnMaskedUser());
		}
		persistObj.setEnableEvaluationDeclaration(rfaEvent.getEnableEvaluationDeclaration());
		persistObj.setEnableSupplierDeclaration(rfaEvent.getEnableSupplierDeclaration());
		persistObj.setEvaluationProcessDeclaration(Boolean.TRUE == rfaEvent.getEnableEvaluationDeclaration() && rfaEvent.getEvaluationProcessDeclaration() != null ? rfaEvent.getEvaluationProcessDeclaration() : null);
		persistObj.setSupplierAcceptanceDeclaration(Boolean.TRUE == rfaEvent.getEnableSupplierDeclaration() && rfaEvent.getSupplierAcceptanceDeclaration() != null ? rfaEvent.getSupplierAcceptanceDeclaration() : null);
		persistObj.setAllowToSuspendEvent(rfaEvent.getAllowToSuspendEvent());
		persistObj.setRevertLastBid(rfaEvent.getRevertLastBid());
		persistObj.setRevertBidUser(rfaEvent.getRevertBidUser());
		persistObj.setMinimumSupplierRating(rfaEvent.getMinimumSupplierRating());
		persistObj.setMaximumSupplierRating(rfaEvent.getMaximumSupplierRating());
		persistObj.setEnableApprovalReminder(rfaEvent.getEnableApprovalReminder());
		persistObj.setProcurementMethod(rfaEvent.getProcurementMethod());
		persistObj.setProcurementCategories(rfaEvent.getProcurementCategories());
		if (Boolean.TRUE == rfaEvent.getEnableApprovalReminder()) {
			persistObj.setReminderAfterHour(rfaEvent.getReminderAfterHour());
			persistObj.setReminderCount(rfaEvent.getReminderCount());
		} else {
			persistObj.setReminderAfterHour(null);
			persistObj.setReminderCount(null);
		}
		persistObj.setNotifyEventOwner(rfaEvent.getNotifyEventOwner());

		RfaEvent event = rfaEventService.updateRfaEvent(persistObj);

		if (StringUtils.checkString(event.getPreviousRequestId()).length() > 0) {
			TatReportPojo tatReport = tatReportService.getTatReportListByEventIdAndFormIdAndTenantId(event.getPreviousRequestId(), event.getEventId(), SecurityLibrary.getLoggedInUserTenantId());
			if (tatReport != null) {
 				tatReportService.updateTatReportEventDetails(tatReport.getId(), event.getId(), persistObj.getEventStart(), persistObj.getEventEnd(), persistObj.getEventPublishDate(), persistObj.getEventName(), persistObj.getReferanceNumber());
			}
		}

		LOG.info("after save Event : Bq : " + event.getBillOfQuantity() + " : cq : " + event.getQuestionnaires() + " : Doc : " + event.getDocumentReq() + " : Meet : " + event.getMeetingReq());
	}

	@SuppressWarnings("unused")
	private boolean doValidate(RfaEvent rfaEvent, Model model) {
		RfaEvent oldEvent = rfaEventService.getRfaEventById(rfaEvent.getId());
		model.addAttribute("auctionType", oldEvent.getAuctionType());
		if (rfaEventService.isExists(rfaEvent)) {
			model.addAttribute("error", messageSource.getMessage("rftEvent.error.duplicate", new Object[] { rfaEvent.getReferanceNumber() }, Global.LOCALE));
			LOG.info("Error:   " + messageSource.getMessage("rftEvent.error.duplicate", new Object[] { rfaEvent.getReferanceNumber() }, Global.LOCALE));
			return false;
		}
		if (rfaEvent.getIndustryCategory() == null) {
			model.addAttribute("error", messageSource.getMessage("supplier.industy.required", new Object[] {}, Global.LOCALE));
			LOG.info("Error:   " + messageSource.getMessage("supplier.industy.required", new Object[] {}, Global.LOCALE));
			return false;
		}
		if (rfaEvent.getAuctionStartRelative() == false) {
			if (oldEvent.getAuctionType() != AuctionType.FORWARD_DUTCH && oldEvent.getAuctionType() != AuctionType.REVERSE_DUTCH) {
				if (rfaEvent.getEventEnd() == null || rfaEvent.getEventStart() == null) {
					model.addAttribute("error", messageSource.getMessage("rfaEvent.error.endstartdate", new Object[] { rfaEvent.getEventName() }, Global.LOCALE));
					return false;
				} else {
					if (rfaEvent.getEventStart() == null) {
						model.addAttribute("error", messageSource.getMessage("rfaEvent.error.startdate", new Object[] { rfaEvent.getEventName() }, Global.LOCALE));
						return false;
					}
				}
			}
		} else {
			if (rfaEvent.getAuctionDuration() == null || rfaEvent.getPreviousAuction() == null || rfaEvent.getAuctionStartDelay() == null) {
				model.addAttribute("error", messageSource.getMessage("rfaEvent.error.auctionsecondpart", new Object[] { rfaEvent.getEventName() }, Global.LOCALE));
				return false;
			}
		}
		if (rfaEvent.getEventPublishDate() == null) {
			model.addAttribute("error", messageSource.getMessage("rfaEvent.error.suppliernotificationdate", new Object[] { rfaEvent.getEventName() }, Global.LOCALE));
			return false;
		}
		return true;
	}

	@RequestMapping(path = "/eventDescription/{eventId}", method = RequestMethod.GET)
	public ModelAndView eventDescription(Model model, @PathVariable String eventId) {
		RfaEvent rfaEvent = null;
		LOG.info("Event ID in Description : " + eventId);
		if (StringUtils.checkString(eventId).length() > 0) {
			model.addAttribute("eventId", eventId);
			model.addAttribute("eventPermissions", rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
			rfaEvent = rfaEventService.loadRfaEventById(eventId);
			BuyerSettings settings = buyerSettingsService.getBuyerSettingsByTenantId(SecurityLibrary.getLoggedInUserTenantId());
			if (settings != null) {
				if (rfaEvent.getBaseCurrency() == null) {
					rfaEvent.setBaseCurrency(settings.getCurrency());
				}
				if (rfaEvent.getDecimal() == null) {
					rfaEvent.setDecimal(settings.getDecimal());
				}
			}
			if (rfaEvent.getTemplate() != null && rfaEvent.getTemplate().getId() != null) {
				RfxTemplate rfxTemplate = rfxTemplateService.getRfxTemplateForEditById(rfaEvent.getTemplate().getId(), SecurityLibrary.getLoggedInUserTenantId());
				model.addAttribute("templateFields", rfxTemplate.getFields());
				model.addAttribute("templateId", rfaEvent.getTemplate().getId());
				model.addAttribute("rfxTemplate", rfxTemplate);
			}
		} else {
			rfaEvent = rfaEventService.loadRfaEventById(eventId);
			if (rfaEvent != null) {
				model.addAttribute("eventPermissions", rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
				model.addAttribute("eventId", rfaEvent.getId());
			}
		}
		if (rfaEvent != null) {
			model.addAttribute("event", rfaEvent);
			model.addAttribute("eventPermissions", rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
			model.addAttribute("costCenter", costCenterService.findTotalCostCentersForTenant(SecurityLibrary.getLoggedInUserTenantId()));
			model.addAttribute("currency", currencyService.getAllCurrency());
			model.addAttribute("rfxType", RfxTypes.RFA);

			return new ModelAndView("eventDescription");
		} else {
			return new ModelAndView("/500_error");
		}
	}

	@RequestMapping(path = "/eventDescription", method = RequestMethod.POST)
	public ModelAndView saveEventDescription(@ModelAttribute RfaEvent rfaEvent, Model model, BindingResult result, RedirectAttributes redir) {
		LOG.info("rfa    " + rfaEvent);
		model.addAttribute("eventPermissions", rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), rfaEvent.getId()));
		return saveRfaDescription(rfaEvent, model, result, redir, true);
	}

	/**
	 * @param rfaEvent
	 * @param model
	 * @param result
	 * @param eventId
	 * @param redir
	 * @return
	 */
	private ModelAndView saveRfaDescription(RfaEvent rfaEvent, Model model, BindingResult result, RedirectAttributes redir, boolean goNext) {
		LOG.info("Save Rfa Event Description Called ");
		RfaEvent persistObj = null;
		model.addAttribute("costCenter", costCenterService.getAllActiveCostCentersForTenant(SecurityLibrary.getLoggedInUserTenantId()));
		model.addAttribute("currency", currencyService.getAllCurrency());
		model.addAttribute("eventId", rfaEvent.getId());
		model.addAttribute("eventPermissions", rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), rfaEvent.getId()));
		String eventId = rfaEvent.getId();

		if (rfaEvent.getTemplate() != null && rfaEvent.getTemplate().getId() != null) {
			RfxTemplate rfxTemplate = rfxTemplateService.getRfxTemplateForEditById(rfaEvent.getTemplate().getId(), SecurityLibrary.getLoggedInUserTenantId());
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
			return new ModelAndView("eventDescription", "event", rfaEvent);
		} else {
			LOG.info("Page submitted....................................... ");
			try {
				if (StringUtils.checkString(eventId).length() > 0) {
					LOG.info("rftEventDescription update:      " + eventId);
					if (rfaEvent.getBillOfQuantity() == false && rfaEvent.getQuestionnaires() == false && rfaEvent.getScheduleOfRate() == false) {
						if (rfaEvent.getTemplate() != null && rfaEvent.getTemplate().getId() != null) {
							LOG.info("templt id 2342 " + rfaEvent.getTemplate().getId());
							RfxTemplate rfxTemplate = rfxTemplateService.getRfxTemplateForEditById(rfaEvent.getTemplate().getId(), SecurityLibrary.getLoggedInUserTenantId());
							model.addAttribute("templateFields", rfxTemplate.getFields());
							model.addAttribute("rfxTemplate", rfxTemplate);
						}
						model.addAttribute("error", messageSource.getMessage("rftEvent.error.bqorcq", new Object[] {}, Global.LOCALE));
						return new ModelAndView("eventDescription", "event", rfaEvent);
					}
					persistObj = rfaEventService.getRfaEventById(eventId);
					persistObj.setEventDescription(rfaEvent.getEventDescription());
					persistObj.setBudgetAmount(rfaEvent.getBudgetAmount());
					persistObj.setBaseCurrency(rfaEvent.getBaseCurrency());
					persistObj.setHistoricaAmount(rfaEvent.getHistoricaAmount());
					persistObj.setDecimal(rfaEvent.getDecimal());
					persistObj.setCostCenter(rfaEvent.getCostCenter());
					persistObj.setPaymentTerm(rfaEvent.getPaymentTerm());
					persistObj.setDocumentReq(rfaEvent.getDocumentReq());
					persistObj.setInternalRemarks(rfaEvent.getInternalRemarks());
					persistObj.setEstimatedBudget(rfaEvent.getEstimatedBudget());
					persistObj.setProcurementMethod(rfaEvent.getProcurementMethod());
					persistObj.setProcurementCategories(rfaEvent.getProcurementCategories());
					persistObj.setGroupCode(rfaEvent.getGroupCode());
					persistObj.setScheduleOfRate(rfaEvent.getScheduleOfRate());
					if (rfaEvent.getDocumentReq() == false) {
						persistObj.setDocumentCompleted(false);
					}
					persistObj.setMeetingReq(rfaEvent.getMeetingReq());
					if (rfaEvent.getMeetingReq() == false) {
						persistObj.setMeetingCompleted(false);
					}
					persistObj.setQuestionnaires(rfaEvent.getQuestionnaires());
					if (rfaEvent.getQuestionnaires() == false) {
						persistObj.setCqCompleted(false);
					}
					persistObj.setBillOfQuantity(rfaEvent.getBillOfQuantity());
					if (rfaEvent.getBillOfQuantity() == false) {
						persistObj.setBqCompleted(false);
					}
					rfaEventService.updateRfaEvent(persistObj);
					LOG.info("rfaEvent update:      " + persistObj);

					redir.addFlashAttribute("eventId", persistObj.getId());

				}

			} catch (Exception e) {
				LOG.error("Error while storing RftEvent : " + e.getMessage(), e);
				// model.addAttribute("error", "Error while storing Rfa Event details for : " +
				// persistObj.getEventName() + ", message : " + e.getMessage());
				model.addAttribute("error", messageSource.getMessage("rfa.while.storing.eventdetails", new Object[] { persistObj.getEventName(), e.getMessage() }, Global.LOCALE));
				return new ModelAndView("eventDescription", "event", rfaEvent);
			}

		}
		String next = "";
		if (goNext) {
			next = doNavigation(persistObj);
		} else {
			redir.addFlashAttribute("success", messageSource.getMessage("rftEvent.success.savedraft", new Object[] { (persistObj.getEventName() != null ? persistObj.getEventName() : persistObj.getEventId()) }, Global.LOCALE));
			next = "redirect:auctionRules/" + persistObj.getId();
		}
		return new ModelAndView(next);
	}

	@RequestMapping(path = "addContactPerson", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<RfaEventContact>> saveContact(@ModelAttribute RfaEventContact rfaEventContact, BindingResult result, Model model) throws JsonProcessingException {
		HttpHeaders headers = new HttpHeaders();
		LOG.info("234234234342332432");
		try {
			if (result.hasErrors()) {
				for (ObjectError err : result.getAllErrors()) {
					headers.add("error", err.getDefaultMessage());
				}
				return new ResponseEntity<List<RfaEventContact>>(null, headers, HttpStatus.EXPECTATION_FAILED);
			} else {
				if (StringUtils.checkString(rfaEventContact.getEventId()).length() > 0) {
					if (rfaEventContact.getComunicationEmail() != null || rfaEventContact.getContactNumber() != null || rfaEventContact.getMobileNumber() != null || rfaEventContact.getFaxNumber() != null) {
						if (doValidate(rfaEventContact)) {
							RfaEvent rfaEvent = rfaEventService.getRfaEventById(rfaEventContact.getEventId());
							if (StringUtils.checkString(rfaEventContact.getId()).length() == 0) {
								LOG.info("evennt in contact :  " + rfaEvent.getEventName());
								rfaEventContact.setRfaEvent(rfaEvent);
								rfaEventService.saveRfaEventContact(rfaEventContact);
								LOG.info("Save Contact Called" + SecurityLibrary.getLoggedInUser());
								headers.add("success", "Successfully added contact details");
							} else {
								LOG.info("upadte event contact");
								RfaEventContact persistObj = rfaEventService.getEventContactById(rfaEventContact.getId());
								persistObj.setRfaEvent(rfaEvent);
								persistObj.setComunicationEmail(rfaEventContact.getComunicationEmail());
								persistObj.setContactName(rfaEventContact.getContactName());
								persistObj.setContactNumber(rfaEventContact.getContactNumber());
								persistObj.setDesignation(rfaEventContact.getDesignation());
								persistObj.setFaxNumber(rfaEventContact.getFaxNumber());
								persistObj.setMobileNumber(rfaEventContact.getMobileNumber());
								persistObj.setTitle(rfaEventContact.getTitle());
								rfaEventService.updateRfaEventContact(persistObj);
								LOG.info("upadte event contact  " + persistObj.toLogString());
								headers.add("success", "Successfully updated contact details");
							}

						} else {
							headers.add("error", "Contact name has already Exist");
							return new ResponseEntity<List<RfaEventContact>>(null, headers, HttpStatus.EXPECTATION_FAILED);
						}
					} else {
						headers.add("error", "Please select one of Contact number, Mobile number, Communication email.");
						return new ResponseEntity<List<RfaEventContact>>(null, headers, HttpStatus.EXPECTATION_FAILED);
					}

				}
			}
		} catch (Exception e) {
			LOG.error("Error While Save the Country" + e.getMessage(), e);
			return new ResponseEntity<List<RfaEventContact>>(null, headers, HttpStatus.EXPECTATION_FAILED);
		}

		List<RfaEventContact> rfaContactList = rfaEventService.getAllContactForEvent(rfaEventContact.getEventId());

		return new ResponseEntity<List<RfaEventContact>>(rfaContactList, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "mangeEventRequirement", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Integer> mangeEventRequirement(@RequestParam("eventId") String eventId, @RequestParam("eventRequirement") String eventRequirement) {
		LOG.info("Requirment Event Id: " + eventId + " the Evnt button :  " + eventRequirement);

		if (eventRequirement.equals("documentReq")) {
			Integer countDocument = rfaEventService.getCountOfRfaDocumentByEventId(eventId);
			LOG.info("countDocument :  " + countDocument);
			if (countDocument > 0) {
				return new ResponseEntity<Integer>(countDocument, HttpStatus.EXPECTATION_FAILED);
			}
		}
		if (eventRequirement.equals("meetingReq")) {
			Integer countMeeting = rfaMeetingService.getCountOfRfaMeetingByEventId(eventId);
			if (countMeeting > 0) {
				return new ResponseEntity<Integer>(countMeeting, HttpStatus.EXPECTATION_FAILED);
			}
		}
		if (eventRequirement.equals("questionnaires")) {
			Integer countQuestionnaires = rfaEventService.getCountOfRfaCqByEventId(eventId);
			if (countQuestionnaires > 0) {
				return new ResponseEntity<Integer>(countQuestionnaires, HttpStatus.EXPECTATION_FAILED);
			}
		}
		if (eventRequirement.equals("billOfQuantity")) {
			Integer countBillOfQuantity = rfaBqService.getCountOfRfaBqByEventId(eventId);
			LOG.info("billOfQuantity :  " + countBillOfQuantity);
			if (countBillOfQuantity > 0) {
				return new ResponseEntity<Integer>(countBillOfQuantity, HttpStatus.EXPECTATION_FAILED);
			}
		}
		if(eventRequirement.equals("scheduleOfRate")) {
			Integer countBillOfQuantity = rfaSorService.getCountOfSorByEventId(eventId);
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
			rfaDocumentService.deleteAllRfaDocuments(eventId, eventRequirement);
		}
		if (eventRequirement.equals("meetingReq")) {
			rfaMeetingService.deleteAllRfaMeetings(eventId, eventRequirement);
		}
		if (eventRequirement.equals("questionnaires")) {
			rfaCqService.deleteAllCqs(eventId, eventRequirement);
		}
		if (eventRequirement.equals("billOfQuantity")) {
			LOG.info("Here to delete Bq : " + eventRequirement);
			rfaBqService.deleteAllRfaBq(eventId, eventRequirement);
		}
		if (eventRequirement.equals("scheduleOfRate")) {
			rfaSorService.deleteAllSor(eventId, eventRequirement);
		}

		return new ResponseEntity<String>(HttpStatus.OK);
	}

	@RequestMapping(path = "/autoSaveDraft", method = RequestMethod.POST)
	public @ResponseBody String autoSaveDraft(@ModelAttribute("event") RfaEvent rfaEvent, @RequestParam("industryCateg[]") String[] industryCategory, BindingResult result, HttpSession session, Model model) {
		try {

			if (rfaEvent.getViewSupplerName() == true) {
				rfaEvent.setViewSupplerName(false);
			} else {
				rfaEvent.setViewSupplerName(true);
			}
			autoSaveRfaDetails(rfaEvent, industryCategory, result, (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY));
		} catch (Exception e) {
			LOG.error("error" + e.getMessage(), e);
		}
		return null;
	}

	@RequestMapping(path = "/saveAsDraft", method = RequestMethod.POST)
	public ModelAndView saveAsDraft(@ModelAttribute("event") RfaEvent rfaEvent, @RequestParam(value = "industryCateg[]") String[] industryCategories, Model model, BindingResult result, RedirectAttributes redir, HttpSession session) {
		if (rfaEvent.getViewSupplerName() == true) {
			rfaEvent.setViewSupplerName(false);
		} else {
			rfaEvent.setViewSupplerName(true);
		}

		model.addAttribute("eventPermissions", rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), rfaEvent.getId()));
		return saveRfaDetails(rfaEvent, model, result, redir, false, session, true, industryCategories);
	}

	@RequestMapping(path = "/saveAsDraftDescription", method = RequestMethod.POST)
	public ModelAndView saveAsDraftDescription(@ModelAttribute RfaEvent rfaEvent, Model model, BindingResult result, RedirectAttributes redir) {
		model.addAttribute("eventPermissions", rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), rfaEvent.getId()));
		return saveRfaDescription(rfaEvent, model, result, redir, false);
	}

	@RequestMapping(path = "/editContact", method = RequestMethod.POST)
	public ResponseEntity<RfaEventContact> editRfaContact(@RequestParam(value = "contactId") String contactId, Model model) {
		LOG.info("Getting the editContact. : " + contactId);
		RfaEventContact eventContact = rfaEventService.getEventContactById(contactId);
		LOG.info("event contact   :  " + eventContact);
		model.addAttribute("btnValue", "Update");
		return new ResponseEntity<RfaEventContact>(eventContact, HttpStatus.OK);
	}

	@RequestMapping(path = "/deleteContact", method = RequestMethod.POST)
	public ResponseEntity<List<RfaEventContact>> deleteRfaContact(@RequestParam(value = "contactId") String contactId, @RequestParam(value = "eventId") String eventId, Model model) {
		LOG.info("Getting the Delete Contact. : " + contactId);
		RfaEventContact eventContact = rfaEventService.getEventContactById(contactId);
		HttpHeaders headers = new HttpHeaders();
		List<RfaEventContact> rfaContactList = null;
		LOG.info("event contact   :  " + eventContact);
		model.addAttribute("btnValue", "Update");
		try {
			rfaEventService.deleteRfaContact(eventContact);
			LOG.info("Delete the contact");
			headers.add("success", "Contact removed Successfully");
		} catch (Exception e) {
			LOG.error("Error while removing Contact from event . " + e.getMessage(), e);
			headers.add("error", "Contact removed unsuccessfull");
			return new ResponseEntity<List<RfaEventContact>>(rfaContactList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		rfaContactList = rfaEventService.getAllContactForEvent(eventId);
		return new ResponseEntity<List<RfaEventContact>>(rfaContactList, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/addReminderOfEvent", method = RequestMethod.POST)
	public ResponseEntity<List<RfaReminder>> addReminderOfEvent(@RequestParam(value = "reminderDuration") String reminderDuration, @RequestParam(value = "dateRangeData") String dateRangeData, @RequestParam(value = "reminderDurationType") IntervalType reminderDurationType, @RequestParam(value = "eventId") String eventId, @RequestParam(value = "reminderId") String reminderId, Model model, HttpSession session) {
		LOG.info("Getting the AddReminder : " + eventId);
		HttpHeaders headers = new HttpHeaders();
		try {
			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}

			if (StringUtils.checkString(eventId).length() > 0) {
				RfaEvent rfaEvent = rfaEventService.getRfaEventById(eventId);
				LOG.info("evennt in reminder save :  ");
				if (StringUtils.checkString(reminderId).length() == 0) {
					RfaReminder rfaReminder = new RfaReminder();
					if (StringUtils.checkString(dateRangeData).length() > 0) {
						String visibilityDates[] = dateRangeData.split("-");
						DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
						formatter.setTimeZone(timeZone);
						Date endDate = (Date) formatter.parse(visibilityDates[1]);

						Calendar cal = Calendar.getInstance();
						cal.setTime(endDate);
						if (reminderDurationType == IntervalType.DAYS) {
							cal.add(Calendar.DATE, -Integer.parseInt(reminderDuration));
							rfaReminder.setIntervalType(IntervalType.DAYS);
							LOG.info("Reminder : " + formatter.format(cal.getTime()));
						} else {
							cal.add(Calendar.HOUR, -Integer.parseInt(reminderDuration));
							rfaReminder.setIntervalType(IntervalType.HOURS);
							LOG.info("Reminder  Hous: " + formatter.format(cal.getTime()));
						}
						rfaReminder.setReminderDate(cal.getTime());
						rfaReminder.setRfaEvent(rfaEvent);
						rfaReminder.setInterval(Integer.parseInt(reminderDuration));
						rfaEventService.saveEventReminder(rfaReminder);
					}
				}
			}
		} catch (Exception e) {
			LOG.error("Error While Save the Country" + e.getMessage(), e);
			return new ResponseEntity<List<RfaReminder>>(null, headers, HttpStatus.EXPECTATION_FAILED);
		}
		List<RfaReminder> reminderList = new ArrayList<RfaReminder>();
		try {
			reminderList = rfaEventService.getAllRfaEventReminderForEvent(eventId, null);
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
		return new ResponseEntity<List<RfaReminder>>(reminderList, HttpStatus.OK);
	}

	@RequestMapping(path = "/deleteReminder", method = RequestMethod.POST)
	public ResponseEntity<List<RfaReminder>> deleteRfaReminder(@RequestParam(value = "reminderId") String reminderId, @RequestParam(value = "eventId") String eventId, @RequestParam(value = "reminderType") String reminderType, Model model) {
		LOG.info("Getting the Delete reminder : " + reminderId);
		RfaReminder eventReminder = rfaEventService.getEventReminderById(reminderId);
		LOG.info("event eventReminder   :  " + eventReminder);
		model.addAttribute("btnValue", "Update");
		rfaEventService.deleteRfaReminder(eventReminder);
		LOG.info("Delete the Reminder");
		List<RfaReminder> reminderList = new ArrayList<RfaReminder>();
		if (StringUtils.checkString(reminderType).equals("start")) {
			LOG.info("comes inyo start");
			try {
				reminderList = rfaEventService.getAllRfaEventReminderForEvent(eventId, Boolean.TRUE);
			} catch (Exception e) {
				LOG.info("Error : " + e.getMessage(), e);
			}
		} else {
			LOG.info("comes inyo End");
			try {
				reminderList = rfaEventService.getAllRfaEventReminderForEvent(eventId, Boolean.FALSE);
			} catch (Exception e) {
				LOG.info("Error : " + e.getMessage(), e);
			}
		}
		return new ResponseEntity<List<RfaReminder>>(reminderList, HttpStatus.OK);
	}

	@RequestMapping(path = "/addReminderOfEventStartEnd", method = RequestMethod.POST)
	public ResponseEntity<List<RfaReminder>> addReminderOfEventStartEnd(@RequestParam(value = "reminderDuration") String reminderDuration, @RequestParam(value = "eventDate") String eventDate, @RequestParam(value = "eventTime") String eventTime, @RequestParam(value = "reminderDurationType") IntervalType reminderDurationType, @RequestParam(value = "eventId") String eventId, @RequestParam(value = "reminderId") String reminderId, @RequestParam(value = "reminderType") String reminderType, Model model, HttpSession session) throws ApplicationException {
		LOG.info("Getting the AddReminder : " + eventId);
		HttpHeaders headers = new HttpHeaders();
		try {
			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}
			if (StringUtils.checkString(eventId).length() > 0) {
				RfaEvent rfaEvent = rfaEventService.getRfaEventById(eventId);
				LOG.info("evennt in reminder save :  ");
				if (StringUtils.checkString(reminderId).length() == 0) {
					RfaReminder rfaReminder = new RfaReminder();
					if (StringUtils.checkString(eventDate).length() > 0) {
						DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
						DateFormat timeFormatter = new SimpleDateFormat("hh:mm a");
						formatter.setTimeZone(timeZone);
						timeFormatter.setTimeZone(timeZone);
						Date eventDateParse = (Date) formatter.parse(eventDate);
						Date eventTimeParse = (Date) timeFormatter.parse(eventTime);
						Date eventDateTime = DateUtil.combineDateTime(eventDateParse, eventTimeParse, timeZone);

						Calendar cal = Calendar.getInstance(timeZone);
						cal.setTime(eventDateTime);
						if (reminderDurationType == IntervalType.DAYS) {
							cal.add(Calendar.DATE, -Integer.parseInt(reminderDuration));
							rfaReminder.setIntervalType(IntervalType.DAYS);
							LOG.info("Reminder : " + formatter.format(cal.getTime()));
						} else {
							cal.add(Calendar.HOUR, -Integer.parseInt(reminderDuration));
							rfaReminder.setIntervalType(IntervalType.HOURS);
							LOG.info("Reminder  Hous: " + formatter.format(cal.getTime()));
						}
						if (cal.getTime().compareTo(new Date()) < 0) {
							headers.add("error", messageSource.getMessage("event.reminder.pastdate", new Object[] {}, Global.LOCALE));
							return new ResponseEntity<List<RfaReminder>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
						}
						rfaReminder.setReminderDate(cal.getTime());
						if (StringUtils.checkString(reminderType).equals("start")) {
							rfaReminder.setStartReminder(Boolean.TRUE);

						}
						if (rfaReminder.getStartReminder() == Boolean.TRUE) {
							rfaEvent.setEventStart(eventDateTime);
							List<RfaReminder> reminderList = rfaEventService.getAllRfaEventReminderForEvent(eventId, Boolean.TRUE);
							if (CollectionUtil.isNotEmpty(reminderList)) {
								for (RfaReminder rfaReminderCompare : reminderList) {
									if (rfaReminder.getReminderDate().compareTo(rfaReminderCompare.getReminderDate()) == 0) {
										headers.add("error", "There is another reminder for event start date is exists");
										return new ResponseEntity<List<RfaReminder>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
									}
								}
							}
						} else {
							rfaEvent.setEventEnd(eventDateTime);
							List<RfaReminder> reminderList = rfaEventService.getAllRfaEventReminderForEvent(eventId, Boolean.FALSE);
							if (CollectionUtil.isNotEmpty(reminderList)) {
								for (RfaReminder rfaReminderCompare : reminderList) {
									if (rfaReminder.getReminderDate().compareTo(rfaReminderCompare.getReminderDate()) == 0) {
										headers.add("error", "There is another reminder for event end date is exists");
										return new ResponseEntity<List<RfaReminder>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
									}
								}
							}
						}

						rfaEvent = rfaEventService.updateRfaEvent(rfaEvent);

						rfaReminder.setRfaEvent(rfaEvent);
						rfaReminder.setInterval(Integer.parseInt(reminderDuration));
						rfaEventService.saveEventReminder(rfaReminder);

					}
				}
			}
		} catch (Exception e) {
			LOG.error("Error While Save the Country" + e.getMessage(), e);
			return new ResponseEntity<List<RfaReminder>>(null, headers, HttpStatus.EXPECTATION_FAILED);
		}
		LOG.info("REminder type : " + reminderType);
		List<RfaReminder> reminderList = new ArrayList<RfaReminder>();
		if (StringUtils.checkString(reminderType).equals("start")) {
			LOG.info("comes inyo start");
			reminderList = rfaEventService.getAllRfaEventReminderForEvent(eventId, Boolean.TRUE);
		} else {
			LOG.info("comes inyo End");
			reminderList = rfaEventService.getAllRfaEventReminderForEvent(eventId, Boolean.FALSE);
		}
		return new ResponseEntity<List<RfaReminder>>(reminderList, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "searchAuction", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<RfaEvent>> searchAuction(@RequestParam("search") String search) {
		HttpHeaders headers = new HttpHeaders();
		try {
			LOG.info("Search Category: " + search);
			String tenantId = SecurityLibrary.getLoggedInUserTenantId();
			List<RfaEvent> rfaList = rfaEventService.findRfaEventByNameAndTenantId(search, tenantId);
			LOG.info("rfa Event : " + rfaList.size());
			// if (rfaList != null) {
			// for (RfaEvent event : rfaList) {
			// LOG.info("Matching Buyer Industry Category : " + event.getEventName());
			// event.setBaseCurrency(null);
			// event.setIndustryCategory(null);
			// event.setCostCenter(null);
			// event.setRfaEnvelop(null);
			// event.setDeliveryAddress(null);
			// event.setDocuments(null);
			// event.setAwardedSuppliers(null);
			// event.setPreviousAuction(null);
			// event.setBusinessUnit(null);
			// }
			// }
			return new ResponseEntity<List<RfaEvent>>(rfaList, HttpStatus.OK);
		} catch (Exception e) {
			LOG.info("Error while search the auction : " + e.getMessage(), e);
			return new ResponseEntity<List<RfaEvent>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		// return new ResponseEntity<List<RfaEvent>>(rfaList, HttpStatus.OK);
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
			Integer countMeeting = rfaMeetingService.getCountOfRfaMeetingByEventId(eventId);
			Integer countEventSupplier = rfaEventSupplierService.getCountOfSupplierByEventId(eventId);

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
			rfaMeetingService.deleteAllRfaMeetings(eventId, null);
			rfaEventSupplierService.deleteAllSuppliersByEventId(eventId);
		}
		return new ResponseEntity<String>(HttpStatus.OK);
	}

	@RequestMapping(path = "/auctionRules/{eventId}", method = RequestMethod.GET)
	public String auctionRules(@PathVariable String eventId, Model model) {
		LOG.info("auction rules before event id " + eventId);
		AuctionRules auctionRules = rfaEventService.getAuctionRulesByEventId(eventId);
		LOG.info("auction rules " + auctionRules.getId());
		RfaEvent event = rfaEventService.getRfaEventByeventId(eventId);
		buildModel(model, auctionRules, event);
		if (event.getTemplate() != null && event.getTemplate().getId() != null) {
			RfxTemplate rfxTemplate = rfxTemplateService.getRfxTemplateForEditById(event.getTemplate().getId(), SecurityLibrary.getLoggedInUserTenantId());
			model.addAttribute("templateFields", rfxTemplate.getFields());
			model.addAttribute("rfxTemplate", rfxTemplate);
		}
		return "auctionRules";
	}

	/**
	 * @param model
	 * @param auctionRules
	 * @param event
	 */
	private void buildModel(Model model, AuctionRules auctionRules, RfaEvent event) {
		model.addAttribute("isPreBidPricingExist", rfaSupplierBqService.isBqPreBidPricingExistForSuppliers(event.getId()));
		model.addAttribute("eventPermissions", rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), auctionRules.getEvent().getId()));
		model.addAttribute("auctionRules", auctionRules);
		model.addAttribute("event", event);
	}

	@RequestMapping(path = "/auctionRules", method = RequestMethod.POST)
	public ModelAndView saveAuctionRules(@ModelAttribute("auctionRules") AuctionRules auctionRules, Model model, RedirectAttributes redir, BindingResult result) {
		model.addAttribute("eventPermissions", rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), auctionRules.getEvent().getId()));
		return storeAuctionRules(auctionRules, model, redir, result, true, false);
	}

	/**
	 * @param model
	 * @param auctionRules
	 */
	public Boolean validateAuctionrules(AuctionRules auctionRules, Model model, Class<?>... validations) {
		List<String> errorList = new ArrayList<String>();
		LOG.info("Validaiting .....");

		Set<ConstraintViolation<AuctionRules>> constraintViolations = validator.validate(auctionRules, validations);
		for (ConstraintViolation<AuctionRules> cv : constraintViolations) {
			LOG.info("Message : " + cv.getMessage());
			errorList.add(cv.getMessage());
		}
		model.addAttribute("errors", errorList);
		if (errorList.isEmpty()) {
			return Boolean.FALSE;
		} else {
			return Boolean.TRUE;
		}
	}

	/**
	 * @param auctionRules
	 * @param redir
	 * @param result
	 * @return
	 */
	private ModelAndView storeAuctionRules(AuctionRules auctionRules, Model model, RedirectAttributes redir, BindingResult result, Boolean goNext, boolean isDraft) {
		LOG.info("here Auction rule for saving... ... ... ... ..." + auctionRules.getEvent());
		String next = "";
		RfaEvent event = new RfaEvent();
		try {
			if (validateAuctionrules(auctionRules, model, AuctionRules.AuctionCreate.class)) {
				event = rfaEventService.getRfaEventByeventId(auctionRules.getEvent().getId());
				buildModel(model, auctionRules, event);
				return new ModelAndView("auctionRules");
			}
			model.addAttribute("event", rfaEventService.getRfaEventById(auctionRules.getEvent().getId()));
			model.addAttribute("eventPermissions", rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), auctionRules.getEvent().getId()));
			event = rfaEventService.getRfaEventByeventId(auctionRules.getEvent().getId());
			if (StringUtils.checkString(auctionRules.getId()).length() > 0) {
				AuctionRules persistObj = rfaEventService.getAuctionRulesById(auctionRules.getId());
				if (event.getAuctionType() == AuctionType.FORWARD_DUTCH || event.getAuctionType() == AuctionType.REVERSE_DUTCH) {

					// Set lumpsum bidding with tax to true for Dutch Auction with BQ enabled. This will be used later
					// for Revised BQ Submission.
					if (Boolean.TRUE == event.getBillOfQuantity()) {
						persistObj.setLumsumBiddingWithTax(Boolean.TRUE);
					} else {
						persistObj.setLumsumBiddingWithTax(null);
					}
					LOG.info("here for Dutch forward auction ");
					BigDecimal differenceAmount = BigDecimal.ZERO;
					if (!isDraft) {
						if (Boolean.TRUE == persistObj.getFowardAuction()) {
							differenceAmount = auctionRules.getDutchStartPrice().subtract(auctionRules.getDutchMinimumPrice());
							LOG.info("dif amount  for: " + differenceAmount);
						} else {
							differenceAmount = auctionRules.getDutchMinimumPrice().subtract(auctionRules.getDutchStartPrice());
							LOG.info("data max price : " + auctionRules.getDutchMinimumPrice() + "  start p : " + auctionRules.getDutchStartPrice());
							LOG.info("dif amount  rev: " + differenceAmount);
						}
						BigDecimal amountDivide = (differenceAmount.divide(auctionRules.getAmountPerIncrementDecrement(), (event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2)));
						LOG.info("amountDivide : " + amountDivide);
						int amountDivision = amountDivide.setScale(0, RoundingMode.CEILING).intValue();
						LOG.info("amount : " + amountDivision);
						int totalDuration = ((amountDivision * auctionRules.getInterval()) + auctionRules.getInterval());
						event.setAuctionDuration(totalDuration);
						persistObj.setDutchAuctionTotalStep(totalDuration / auctionRules.getInterval());
						Date endDate = rfaEventService.calculateEndDateForDutchAuction(auctionRules.getInterval(), auctionRules.getIntervalType(), persistObj.getDutchAuctionTotalStep(), event.getEventStart());
						LOG.info("dadasd adasd 2: " + endDate);
						// event.setAuctionDurationType(auctionRules.getIntervalType());
						// Calendar cal = Calendar.getInstance();
						// cal.setTime(event.getEventStart());
						// if (event.getAuctionDurationType() == DurationType.MINUTE) {
						// cal.add(Calendar.MINUTE, (event.getAuctionDuration()));
						// } else {
						// cal.add(Calendar.HOUR, (event.getAuctionDuration()));
						// }
						event.setEventEnd(endDate);
						persistObj.setDutchAuctionCurrentStep(0);
						persistObj.setDutchAuctionCurrentStepAmount(auctionRules.getDutchStartPrice());
						event.setEventDetailCompleted(Boolean.TRUE);

						if (persistObj.getFowardAuction()) {
							if (auctionRules.getDutchStartPrice().longValue() > auctionRules.getDutchMinimumPrice().longValue()) {
								if (auctionRules.getDutchStartPrice().subtract(auctionRules.getDutchMinimumPrice()).longValue() < auctionRules.getAmountPerIncrementDecrement().longValue()) {
									// model.addAttribute("error", "In Forword Dutch Auction the difference between
									// start price and minimum price cannot be less then the amount per decrement");
									model.addAttribute("error", messageSource.getMessage("difference.cannot.less.decrement", new Object[] {}, Global.LOCALE));
									return new ModelAndView("auctionRules");
								}
							} else {
								// model.addAttribute("error", "In Forword Dutch Auction the start price cannot be less
								// then the minimum price");
								model.addAttribute("error", messageSource.getMessage("difference.cannot.less.minprice", new Object[] {}, Global.LOCALE));
								return new ModelAndView("auctionRules");
							}
						} else {
							if (auctionRules.getDutchStartPrice().longValue() < auctionRules.getDutchMinimumPrice().longValue()) {
								if (auctionRules.getDutchMinimumPrice().subtract(auctionRules.getDutchStartPrice()).longValue() < auctionRules.getAmountPerIncrementDecrement().longValue()) {
									// model.addAttribute("error", "In Reverse Dutch Auction the difference between
									// start price and maximum price cannot be less then the amount per increment");
									model.addAttribute("error", messageSource.getMessage("difference.cannot.less.increment", new Object[] {}, Global.LOCALE));
									return new ModelAndView("auctionRules");
								}
							} else {
								// model.addAttribute("error", "In Reverse Dutch Auction the start price cannot be
								// greater then the maxmimum price");
								model.addAttribute("error", messageSource.getMessage("difference.cannot.less.maxprice", new Object[] {}, Global.LOCALE));
								return new ModelAndView("auctionRules");
							}
						}
						event = rfaEventService.updateRfaEvent(event);
					}
					persistObj.setDutchMinimumPrice(auctionRules.getDutchMinimumPrice());
					persistObj.setDutchStartPrice(auctionRules.getDutchStartPrice());
					persistObj.setAmountPerIncrementDecrement(auctionRules.getAmountPerIncrementDecrement());
					persistObj.setInterval(auctionRules.getInterval());
					persistObj.setIntervalType(auctionRules.getIntervalType());
				}
				if (event.getAuctionType() == AuctionType.FORWARD_ENGISH || event.getAuctionType() == AuctionType.REVERSE_ENGISH) {
					if (auctionRules.getIsBiddingAllowSupplierSameBid() != null && auctionRules.getIsBiddingAllowSupplierSameBid()) {
						auctionRules.setIsBiddingPriceHigherLeadingBid(Boolean.FALSE);
					}
					if (auctionRules.getIsBiddingPriceHigherLeadingBid() != null && auctionRules.getIsBiddingPriceHigherLeadingBid()) {
						auctionRules.setIsBiddingAllowSupplierSameBid(Boolean.FALSE);
					}
					persistObj.setPreBidBy(auctionRules.getPreBidBy());
					persistObj.setIsPreBidHigherPrice(auctionRules.getIsPreBidHigherPrice());
					persistObj.setIsPreBidSameBidPrice(auctionRules.getIsPreBidSameBidPrice());
					persistObj.setIsBiddingMinValueFromPrevious(auctionRules.getIsBiddingMinValueFromPrevious());
					persistObj.setBiddingMinValueType(auctionRules.getBiddingMinValueType());
					persistObj.setBiddingMinValue(auctionRules.getBiddingMinValue());
					persistObj.setIsStartGate(auctionRules.getIsStartGate());
					persistObj.setIsBiddingPriceHigherLeadingBid(auctionRules.getIsBiddingPriceHigherLeadingBid());
					persistObj.setBiddingPriceHigherLeadingBidType(auctionRules.getBiddingPriceHigherLeadingBidType());
					persistObj.setBiddingPriceHigherLeadingBidValue(auctionRules.getBiddingPriceHigherLeadingBidValue());
					persistObj.setIsBiddingAllowSupplierSameBid(auctionRules.getIsBiddingAllowSupplierSameBid());
					persistObj.setAuctionConsolePriceType(auctionRules.getAuctionConsolePriceType());
					persistObj.setAuctionConsoleVenderType(auctionRules.getAuctionConsoleVenderType());
					persistObj.setAuctionConsoleRankType(auctionRules.getAuctionConsoleRankType());

					persistObj.setBuyerAuctionConsolePriceType(auctionRules.getBuyerAuctionConsolePriceType());
					persistObj.setBuyerAuctionConsoleVenderType(auctionRules.getBuyerAuctionConsoleVenderType());
					persistObj.setBuyerAuctionConsoleRankType(auctionRules.getBuyerAuctionConsoleRankType());

					persistObj.setItemizedBiddingWithTax(auctionRules.getItemizedBiddingWithTax());
					persistObj.setLumsumBiddingWithTax(auctionRules.getLumsumBiddingWithTax());
					persistObj.setPrebidAsFirstBid(auctionRules.getPrebidAsFirstBid());
				}
				if (event.getAuctionType() == AuctionType.FORWARD_SEALED_BID || event.getAuctionType() == AuctionType.REVERSE_SEALED_BID) {
					persistObj.setPreBidBy(auctionRules.getPreBidBy());
					persistObj.setIsPreBidHigherPrice(auctionRules.getIsPreBidHigherPrice());
					persistObj.setIsPreBidSameBidPrice(auctionRules.getIsPreBidSameBidPrice());
					persistObj.setItemizedBiddingWithTax(auctionRules.getItemizedBiddingWithTax());
					persistObj.setLumsumBiddingWithTax(auctionRules.getLumsumBiddingWithTax());
					persistObj.setIsBiddingMinValueFromPrevious(auctionRules.getIsBiddingMinValueFromPrevious());
					persistObj.setBiddingMinValueType(auctionRules.getBiddingMinValueType());
					persistObj.setBiddingMinValue(auctionRules.getBiddingMinValue());
				}
				persistObj.setPreSetSamePreBidForAllSuppliers(auctionRules.getPreSetSamePreBidForAllSuppliers());
				persistObj.setEvent(event);
				persistObj.setAuctionStarted(Boolean.FALSE);
				persistObj = rfaEventService.updateAuctionRules(persistObj);
				LOG.info("LBWIT" + persistObj.getLumsumBiddingWithTax());
				LOG.info("IBWIT" + persistObj.getItemizedBiddingWithTax());
			}
		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
			event = rfaEventService.getRfaEventByeventId(auctionRules.getEvent().getId());
			buildModel(model, auctionRules, event);
			// model.addAttribute("error", "Error while saving auction rules : " + e.getMessage());
			model.addAttribute("error", messageSource.getMessage("error.while.saving.auctionrules", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ModelAndView("auctionRules");
		}

		try {
			rfaEventService.insertTimeLine(auctionRules.getEvent().getId());
		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
		}

		// scheduleAuction(auctionRules);
		if (goNext) {
			next = doNavigation(event);
		} else {
			redir.addFlashAttribute("success", messageSource.getMessage("rftEvent.success.savedraft", new Object[] { (event.getEventName() != null ? event.getEventName() : event.getEventId()) }, Global.LOCALE));
			next = "redirect:auctionRules/" + event.getId();
		}
		return new ModelAndView(next);
	}

	@RequestMapping(path = "/manageReminderOnStartEndDateChange", method = RequestMethod.POST)
	public ResponseEntity<List<RfaReminder>> manageReminderOnDateChange(@RequestParam(value = "eventDate") String eventDate, @RequestParam(value = "eventTime") String eventTime, @RequestParam(value = "eventId") String eventId, @RequestParam(value = "reminderId", required = false) String reminderId, @RequestParam(value = "reminderType") String reminderType, Model model, HttpSession session) throws ApplicationException {
		LOG.info("Getting the AddReminder acoordig to event date : " + eventId);
		HttpHeaders headers = new HttpHeaders();
		try {
			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}
			if (StringUtils.checkString(eventId).length() > 0) {

				RfaEvent event = rfaEventService.getRfaEventById(eventId);

				DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				DateFormat timeFormatter = new SimpleDateFormat("hh:mm a");
				formatter.setTimeZone(timeZone);
				timeFormatter.setTimeZone(timeZone);

				if (0 >= StringUtils.checkString(eventDate).length() || 0 >= StringUtils.checkString(eventTime).length()) {
					return new ResponseEntity<List<RfaReminder>>(null, headers, HttpStatus.EXPECTATION_FAILED);
				}
				Date eventDateParse = (Date) formatter.parse(eventDate);
				Date eventTimeParse = (Date) timeFormatter.parse(eventTime);
				Date eventDateTime = DateUtil.combineDateTime(eventDateParse, eventTimeParse, timeZone);
				if (StringUtils.checkString(reminderType).equals("start")) {
					event.setEventStart(eventDateTime);
					List<RfaReminder> reminderList = rfaEventService.getAllRfaEventReminderForEvent(eventId, Boolean.TRUE);
					if (CollectionUtil.isNotEmpty(reminderList)) {
						for (RfaReminder reminder : reminderList) {
							reminder.getReminderDate();
							Calendar cal = Calendar.getInstance(timeZone);
							cal.setTime(eventDateTime);
							if (reminder.getIntervalType() == IntervalType.DAYS) {
								cal.add(Calendar.DATE, -(reminder.getInterval()));
								LOG.info("Reminder : " + formatter.format(cal.getTime()));
							} else {
								cal.add(Calendar.HOUR, -(reminder.getInterval()));
								LOG.info("Reminder  Hous: " + formatter.format(cal.getTime()));
							}
							reminder.setReminderDate(cal.getTime());
							if (reminder.getReminderDate().after(eventDateTime)) {
								headers.add("error", "Reminder date should not be after than event start date");
								return new ResponseEntity<List<RfaReminder>>(reminderList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
							}
							if (cal.getTime().compareTo(new Date()) < 0) {
								headers.add("error", messageSource.getMessage("event.reminder.pastdate", new Object[] {}, Global.LOCALE));
								return new ResponseEntity<List<RfaReminder>>(reminderList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
							}
							rfaEventService.updateEventReminder(reminder);
						}
					}
				} else {
					event.setEventEnd(eventDateTime);
					List<RfaReminder> reminderList = rfaEventService.getAllRfaEventReminderForEvent(eventId, Boolean.FALSE);
					if (CollectionUtil.isNotEmpty(reminderList)) {
						LOG.info("reminder daate end : " + reminderList.size());
						for (RfaReminder reminder : reminderList) {
							reminder.getReminderDate();
							Calendar cal = Calendar.getInstance(timeZone);
							cal.setTime(eventDateTime);
							if (reminder.getIntervalType() == IntervalType.DAYS) {
								cal.add(Calendar.DATE, -(reminder.getInterval()));
								LOG.info("Reminder : " + formatter.format(cal.getTime()));
							} else {
								cal.add(Calendar.HOUR, -(reminder.getInterval()));
								LOG.info("Reminder  Hous: " + formatter.format(cal.getTime()));
							}
							reminder.setReminderDate(cal.getTime());
							if (reminder.getReminderDate().after(eventDateTime)) {
								headers.add("error", "Reminder date should not be after than event end date");
								return new ResponseEntity<List<RfaReminder>>(reminderList, headers, HttpStatus.INTERNAL_SERVER_ERROR);
							}
							rfaEventService.updateRfaEvent(event);

							rfaEventService.updateEventReminder(reminder);
						}
					}
				}

			}
		} catch (Exception e) {
			LOG.error("Error While Save the event" + e.getMessage(), e);
			return new ResponseEntity<List<RfaReminder>>(null, headers, HttpStatus.EXPECTATION_FAILED);
		}
		List<RfaReminder> reminderList = new ArrayList<RfaReminder>();
		if (StringUtils.checkString(reminderType).equals("start")) {
			reminderList = rfaEventService.getAllRfaEventReminderForEvent(eventId, Boolean.TRUE);
			LOG.info("Reminer list size for start date : " + reminderList.size());
		} else {
			reminderList = rfaEventService.getAllRfaEventReminderForEvent(eventId, Boolean.FALSE);
			LOG.info("Reminer list size for end date : " + reminderList.size());
		}
		return new ResponseEntity<List<RfaReminder>>(reminderList, HttpStatus.OK);
	}

	private boolean doValidate(RfaEventContact rfaEventContact) {
		boolean validate = true;
		if (rfaEventService.isExists(rfaEventContact)) {
			validate = false;
		}
		return validate;
	}

	// MESSAGE: Schedule method move to After finish the event.
	@SuppressWarnings("unused")
	private void scheduleAuction(AuctionRules auctionRulesData) {
		try {
			AuctionRules auctionRules = rfaEventService.getAuctionRulesWithEventById(auctionRulesData.getId());

			schedulerFactoryBean.getScheduler().pauseAll();
			// if (auctionRules.getDutchAuctionTotalStep() == auctionRules.getDutchAuctionCurrentStep()) {
			// schedulerFactoryBean.getScheduler().shutdown();
			// auctionRules.setAuctionCompleted(Boolean.TRUE);
			// rfaEventService.updateAuctionRules(auctionRules);
			//
			// RfaEvent event = auctionRules.getEvent();
			// event.setStatus(EventStatus.COMPLETE);
			// rfaEventService.updateRfaEvent(event);
			// }
			JobKey jobKey = new JobKey("JOB" + auctionRules.getId(), "DUTCHAUCTION");
			JobDetail jobDetail = null;
			if (jobKey != null) {
				jobDetail = (JobDetail) schedulerFactoryBean.getScheduler().getJobDetail(jobKey);
			}
			TriggerKey triggerKey = new TriggerKey("TRIGGER" + auctionRules.getId(), "DUTCHAUCTION");
			SimpleTriggerImpl trigger = null;
			if (triggerKey != null) {
				trigger = (SimpleTriggerImpl) schedulerFactoryBean.getScheduler().getTrigger(triggerKey);
			}

			LOG.info("Scheduling auction : " + auctionRules.getEvent().getEventName());
			if (jobDetail == null) {
				JobDetailFactoryBean jobDetailBean = new JobDetailFactoryBean();
				jobDetailBean.setJobClass(DutchAuctionJob.class);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("auctionId", auctionRules.getId());
				jobDetailBean.setJobDataAsMap(map);
				jobDetailBean.setGroup("DUTCHAUCTION");
				jobDetailBean.setName("JOB" + auctionRules.getId());
				jobDetailBean.afterPropertiesSet();
				jobDetail = jobDetailBean.getObject();
			}

			if (trigger == null) {
				SimpleTriggerFactoryBean triggerBean = new SimpleTriggerFactoryBean();
				triggerBean.setName("TRIGGER" + auctionRules.getId());
				triggerBean.setGroup("DUTCHAUCTION");
				triggerBean.setJobDetail(jobDetail);
				if (auctionRules.getIntervalType() == DurationMinSecType.MINUTE) {
					triggerBean.setStartTime(auctionRules.getEvent().getEventStart());
					triggerBean.setRepeatInterval(auctionRules.getInterval() * 1000 * 60);
					triggerBean.setRepeatCount(auctionRules.getDutchAuctionTotalStep());
					LOG.info("Schedule a job If Minute : ");
				} else if (auctionRules.getIntervalType() == DurationMinSecType.SECONDS) {
					triggerBean.setStartTime(auctionRules.getEvent().getEventStart());
					triggerBean.setRepeatInterval(auctionRules.getInterval() * 1000);
					triggerBean.setRepeatCount(auctionRules.getDutchAuctionTotalStep());
					LOG.info("Schedule a job If Seconds : ");
				}

				triggerBean.afterPropertiesSet();
				trigger = (SimpleTriggerImpl) triggerBean.getObject();
				schedulerFactoryBean.getScheduler().scheduleJob(jobDetail, trigger);
			} else {
				if (auctionRules.getIntervalType() == DurationMinSecType.MINUTE) {
					trigger.setStartTime(auctionRules.getEvent().getEventStart());
					trigger.setRepeatInterval(auctionRules.getInterval() * 1000 * 60);
					trigger.setRepeatCount(auctionRules.getDutchAuctionTotalStep());
					LOG.info("ReSchedule a job If Minute : ");
				} else if (auctionRules.getIntervalType() == DurationMinSecType.SECONDS) {
					trigger.setStartTime(auctionRules.getEvent().getEventStart());
					trigger.setRepeatInterval(auctionRules.getInterval() * 1000);
					trigger.setRepeatCount(auctionRules.getDutchAuctionTotalStep());
					LOG.info("ReSchedule a job If Seconds : ");
				}
				schedulerFactoryBean.getScheduler().rescheduleJob(triggerKey, trigger);
			}

			schedulerFactoryBean.getScheduler().resumeAll();
		} catch (SchedulerException e) {
			LOG.error("Error : " + e.getMessage(), e);
		}

	}

	@RequestMapping(path = "/saveAsDraftAuctionRules", method = RequestMethod.POST)
	public ModelAndView auctionRulesSaveDraft(RfaEvent event, @ModelAttribute AuctionRules auctionRules, Model model, BindingResult result, RedirectAttributes redir, HttpSession session) {
		return storeAuctionRules(auctionRules, model, redir, result, false, true);
	}

	@RequestMapping(path = "/autoSaveAsDraftAuctionRules", method = RequestMethod.POST)
	public @ResponseBody void auctionRulesSaveDraft(RfaEvent event, @ModelAttribute AuctionRules auctionRules) {
		storeAuctionRules(auctionRules);
	}

	private void storeAuctionRules(AuctionRules auctionRules) {
		LOG.info("here Auction rule for saving... ... ... ... ..." + auctionRules.getEvent());
		RfaEvent event = new RfaEvent();
		try {
			event = rfaEventService.getRfaEventByeventId(auctionRules.getEvent().getId());
			if (StringUtils.checkString(auctionRules.getId()).length() > 0) {
				AuctionRules persistObj = rfaEventService.getAuctionRulesById(auctionRules.getId());
				if (event.getAuctionType() == AuctionType.FORWARD_DUTCH || event.getAuctionType() == AuctionType.REVERSE_DUTCH) {

					// Set lumpsum bidding with tax to true for Dutch Auction
					// with BQ enabled. This will be used later
					// for Revised BQ Submission.
					if (Boolean.TRUE == event.getBillOfQuantity()) {
						persistObj.setLumsumBiddingWithTax(Boolean.TRUE);
					} else {
						persistObj.setLumsumBiddingWithTax(null);
					}
					LOG.info("here for Dutch forward auction ");
					BigDecimal differenceAmount = BigDecimal.ZERO;

					persistObj.setDutchMinimumPrice(auctionRules.getDutchMinimumPrice());
					persistObj.setDutchStartPrice(auctionRules.getDutchStartPrice());
					persistObj.setAmountPerIncrementDecrement(auctionRules.getAmountPerIncrementDecrement());
					persistObj.setInterval(auctionRules.getInterval());
					persistObj.setIntervalType(auctionRules.getIntervalType());
				}
				if (event.getAuctionType() == AuctionType.FORWARD_ENGISH || event.getAuctionType() == AuctionType.REVERSE_ENGISH) {
					if (auctionRules.getIsBiddingAllowSupplierSameBid() != null && auctionRules.getIsBiddingAllowSupplierSameBid()) {
						auctionRules.setIsBiddingPriceHigherLeadingBid(Boolean.FALSE);
					}
					if (auctionRules.getIsBiddingPriceHigherLeadingBid() != null && auctionRules.getIsBiddingPriceHigherLeadingBid()) {
						auctionRules.setIsBiddingAllowSupplierSameBid(Boolean.FALSE);
					}
					persistObj.setPreBidBy(auctionRules.getPreBidBy());
					persistObj.setIsPreBidHigherPrice(auctionRules.getIsPreBidHigherPrice());
					persistObj.setIsPreBidSameBidPrice(auctionRules.getIsPreBidSameBidPrice());
					persistObj.setIsBiddingMinValueFromPrevious(auctionRules.getIsBiddingMinValueFromPrevious());
					persistObj.setBiddingMinValueType(auctionRules.getBiddingMinValueType());
					persistObj.setBiddingMinValue(auctionRules.getBiddingMinValue());
					persistObj.setIsStartGate(auctionRules.getIsStartGate());
					persistObj.setIsBiddingPriceHigherLeadingBid(auctionRules.getIsBiddingPriceHigherLeadingBid());
					persistObj.setBiddingPriceHigherLeadingBidType(auctionRules.getBiddingPriceHigherLeadingBidType());
					persistObj.setBiddingPriceHigherLeadingBidValue(auctionRules.getBiddingPriceHigherLeadingBidValue());
					persistObj.setIsBiddingAllowSupplierSameBid(auctionRules.getIsBiddingAllowSupplierSameBid());
					persistObj.setAuctionConsolePriceType(auctionRules.getAuctionConsolePriceType());
					persistObj.setAuctionConsoleVenderType(auctionRules.getAuctionConsoleVenderType());
					persistObj.setAuctionConsoleRankType(auctionRules.getAuctionConsoleRankType());

					persistObj.setBuyerAuctionConsolePriceType(auctionRules.getBuyerAuctionConsolePriceType());
					persistObj.setBuyerAuctionConsoleVenderType(auctionRules.getBuyerAuctionConsoleVenderType());
					persistObj.setBuyerAuctionConsoleRankType(auctionRules.getBuyerAuctionConsoleRankType());

					persistObj.setItemizedBiddingWithTax(auctionRules.getItemizedBiddingWithTax());
					persistObj.setLumsumBiddingWithTax(auctionRules.getLumsumBiddingWithTax());
					persistObj.setPrebidAsFirstBid(auctionRules.getPrebidAsFirstBid());
				}
				if (event.getAuctionType() == AuctionType.FORWARD_SEALED_BID || event.getAuctionType() == AuctionType.REVERSE_SEALED_BID) {
					persistObj.setPreBidBy(auctionRules.getPreBidBy());
					persistObj.setIsPreBidHigherPrice(auctionRules.getIsPreBidHigherPrice());
					persistObj.setIsPreBidSameBidPrice(auctionRules.getIsPreBidSameBidPrice());
					persistObj.setItemizedBiddingWithTax(auctionRules.getItemizedBiddingWithTax());
					persistObj.setLumsumBiddingWithTax(auctionRules.getLumsumBiddingWithTax());
					persistObj.setIsBiddingMinValueFromPrevious(auctionRules.getIsBiddingMinValueFromPrevious());
					persistObj.setBiddingMinValueType(auctionRules.getBiddingMinValueType());
					persistObj.setBiddingMinValue(auctionRules.getBiddingMinValue());
				}
				persistObj.setEvent(event);
				persistObj.setAuctionStarted(Boolean.FALSE);
				persistObj.setPreSetSamePreBidForAllSuppliers(auctionRules.getPreSetSamePreBidForAllSuppliers());
				persistObj = rfaEventService.updateAuctionRules(persistObj);
				LOG.info("LBWIT" + persistObj.getLumsumBiddingWithTax());
				LOG.info("IBWIT" + persistObj.getItemizedBiddingWithTax());
			}
		} catch (Exception e) {
			event = rfaEventService.getRfaEventByeventId(auctionRules.getEvent().getId());
		}

		try {
			rfaEventService.insertTimeLine(auctionRules.getEvent().getId());
		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
		}

	}

	@RequestMapping("/checkPrebidByBuyer/{eventId}")
	public ResponseEntity<Long> checkPrebidByBuyer(@PathVariable("eventId") String eventId) {
		Long supplierBqCount = 0l;
		try {
			supplierBqCount = rfaSupplierBqService.getRfaSupplierBqCountForEvent(eventId);
			LOG.info("Supplier Bq Count  " + supplierBqCount);
			return new ResponseEntity<Long>(supplierBqCount, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return new ResponseEntity<Long>(supplierBqCount, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}

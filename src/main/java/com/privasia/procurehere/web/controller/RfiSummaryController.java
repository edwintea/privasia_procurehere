package com.privasia.procurehere.web.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.privasia.procurehere.core.entity.RfiEventSor;
import com.privasia.procurehere.core.entity.Sor;
import com.privasia.procurehere.service.RfiSorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.UserDao;
import com.privasia.procurehere.core.entity.BusinessUnit;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.EventTeamMember;
import com.privasia.procurehere.core.entity.FavouriteSupplier;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.entity.RfiApprovalUser;
import com.privasia.procurehere.core.entity.RfiCq;
import com.privasia.procurehere.core.entity.RfiEnvelop;
import com.privasia.procurehere.core.entity.RfiEnvelopeOpenerUser;
import com.privasia.procurehere.core.entity.RfiEvaluatorUser;
import com.privasia.procurehere.core.entity.RfiEvent;
import com.privasia.procurehere.core.entity.RfiEventApproval;
import com.privasia.procurehere.core.entity.RfiEventAudit;
import com.privasia.procurehere.core.entity.RfiEventDocument;
import com.privasia.procurehere.core.entity.RfiEventMeeting;
import com.privasia.procurehere.core.entity.RfiEventMeetingDocument;
import com.privasia.procurehere.core.entity.RfiEventSupplier;
import com.privasia.procurehere.core.entity.RfiEventSuspensionApproval;
import com.privasia.procurehere.core.entity.RfiReminder;
import com.privasia.procurehere.core.entity.RfiSupplierMeetingAttendance;
import com.privasia.procurehere.core.entity.RfiSuspensionApprovalUser;
import com.privasia.procurehere.core.entity.RfiTeamMember;
import com.privasia.procurehere.core.entity.RfpEvent;
import com.privasia.procurehere.core.entity.RfqEvent;
import com.privasia.procurehere.core.entity.RftEvent;
import com.privasia.procurehere.core.entity.RfxTemplate;
import com.privasia.procurehere.core.entity.SourcingFormRequest;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.TemplateField;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.ApprovalStatus;
import com.privasia.procurehere.core.enums.AuctionType;
import com.privasia.procurehere.core.enums.AuditActionType;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.EnvelopType;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.EventVisibilityType;
import com.privasia.procurehere.core.enums.MeetingStatus;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.SubmissionStatusType;
import com.privasia.procurehere.core.enums.SuspensionType;
import com.privasia.procurehere.core.enums.TeamMemberType;
import com.privasia.procurehere.core.enums.UserType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.SubscriptionException;
import com.privasia.procurehere.core.pojo.EnvelopePojo;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.EventSupplierPojo;
import com.privasia.procurehere.core.pojo.FeePojo;
import com.privasia.procurehere.core.pojo.SupplierMeetingAttendancePojo;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.pojo.TatReportPojo;
import com.privasia.procurehere.core.pojo.UserPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.DateUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.integration.PublishEventService;
import com.privasia.procurehere.service.BusinessUnitService;
import com.privasia.procurehere.service.BuyerAddressService;
import com.privasia.procurehere.service.EventAuditService;
import com.privasia.procurehere.service.EventIdSettingsService;
import com.privasia.procurehere.service.FavoriteSupplierService;
import com.privasia.procurehere.service.RfiCqService;
import com.privasia.procurehere.service.RfiDocumentService;
import com.privasia.procurehere.service.RfiEnvelopService;
import com.privasia.procurehere.service.RfiEventMeetingService;
import com.privasia.procurehere.service.RfiEventService;
import com.privasia.procurehere.service.RfiEventSupplierService;
import com.privasia.procurehere.service.RfiMeetingService;
import com.privasia.procurehere.service.RfxTemplateService;
import com.privasia.procurehere.service.SourcingFormRequestService;
import com.privasia.procurehere.service.SupplierRfiAttendanceService;
import com.privasia.procurehere.service.UserService;
import com.privasia.procurehere.service.impl.SnapShotAuditService;
import com.privasia.procurehere.web.editors.RfiApprovalEditor;
import com.privasia.procurehere.web.editors.RfiEnvelopeOpenerUserEditor;
import com.privasia.procurehere.web.editors.RfiSuspensionApprovalEditor;
import com.privasia.procurehere.web.editors.SupplierEditor;
import com.privasia.procurehere.web.editors.UserEditor;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;
import net.sf.jasperreports.engine.util.JRSwapFile;

/**
 * @author Ravi
 */

@Controller
@RequestMapping("/buyer/RFI")
public class RfiSummaryController extends EventSummaryBase {

	@Autowired
	RfiEventService eventService;

	@Autowired
	BusinessUnitService businessUnitService;

	@Autowired
	BuyerAddressService buyerAddressService;

	@Autowired
	RfiEnvelopService envelopService;

	@Resource
	MessageSource messageSource;

	@Autowired
	RfiCqService cqService;

	@Autowired
	FavoriteSupplierService favoriteSupplierService;

	@Autowired
	UserService userService;

	@Autowired
	RfiDocumentService rfiDocumentService;

	@Autowired
	RfiMeetingService rfiMeetingService;

	@Autowired
	EventAuditService eventAuditService;

	@Autowired
	RfiApprovalEditor rfiApprovalEditor;

	@Autowired
	RfiEventService rfiEventService;

	@Autowired
	RfiEnvelopService rfiEnvelopService;

	@Autowired
	UserEditor userEditor;

	@Autowired
	SupplierEditor supplierEditor;

	@Autowired
	RfiEventSupplierService rfiEventSupplierService;

	@Autowired
	SupplierRfiAttendanceService supplierRfiMeetingAttendanceService;

	@Autowired
	SimpMessagingTemplate simpMessagingTemplate;

	@Autowired
	RfxTemplateService rfxTemplateService;
	@Autowired
	EventIdSettingsService eventIdSettingsService;

	@Autowired
	SourcingFormRequestService sourcingFormRequestService;

	@Autowired
	PublishEventService publishEventService;

	@Autowired
	SnapShotAuditService snapShotAuditService;

	@Autowired
	JmsTemplate jmsTemplate;

	@Autowired
	RfiEventMeetingService rfiEventMeetingService;

	@Autowired
	RfiEnvelopeOpenerUserEditor envelopeOpenerUserEditor;

	@Autowired
	UserDao userDao;

	@Autowired
	RfiSuspensionApprovalEditor rfiSuspensionApprovalEditor;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Autowired
	RfiSorService rfiSorService;

	public RfiSummaryController() {
		super(RfxTypes.RFI);
	}

	@InitBinder
	public void initBinder(ServletRequestDataBinder binder, HttpSession session) {
		binder.registerCustomEditor(RfiApprovalUser.class, rfiApprovalEditor);
		binder.registerCustomEditor(RfiSuspensionApprovalUser.class, rfiSuspensionApprovalEditor);
		binder.registerCustomEditor(Supplier.class, supplierEditor);
		StringTrimmerEditor stringtrimmer = new StringTrimmerEditor(true);
		binder.registerCustomEditor(String.class, stringtrimmer);
		binder.registerCustomEditor(User.class, userEditor);

		SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

		TimeZone timeZone = TimeZone.getDefault();
		String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}

		format.setTimeZone(timeZone);
		timeFormat.setTimeZone(timeZone);
		binder.registerCustomEditor(Date.class, "feePaidTime", new CustomDateEditor(timeFormat, true));
		binder.registerCustomEditor(Date.class, "feePaidDate", new CustomDateEditor(format, true));
		binder.registerCustomEditor(Date.class, "depositPaidTime", new CustomDateEditor(timeFormat, true));
		binder.registerCustomEditor(Date.class, "depositPaidDate", new CustomDateEditor(format, true));
		binder.registerCustomEditor(RfiEnvelopeOpenerUser.class, envelopeOpenerUserEditor);
	}

	@RequestMapping(path = "/eventSummary/{eventId}", method = RequestMethod.GET)
	public ModelAndView summaryRftEvent(@PathVariable String eventId, Model model, RedirectAttributes redir, HttpServletRequest request) {
		if (StringUtils.checkString(eventId).length() == 0) {
			return new ModelAndView("redirect:/400_error");
		}
		boolean subscriptionExpired = super.checkSubscriptionPackageUserBased();
		model.addAttribute("subscriptionExpired", subscriptionExpired);
		model.addAttribute("rfxTemplateList", rfxTemplateService.findAllActiveTemplatesByRfxTypeForTenantId(SecurityLibrary.getLoggedInUserTenantId(), RfxTypes.RFI));
		model.addAttribute("invitedSupplier", rfiEventSupplierService.getAllSubmittedSupplierByEventId(eventId));
		EventPermissions eventPermissions = eventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
		RfiEvent event = buildModel(eventId, model, eventPermissions);

		boolean isSuspensionApprovalActive = false;
		boolean showApprovalRemark = false;

		if (event.getStatus() == EventStatus.SUSPENDED && CollectionUtil.isNotEmpty(event.getSuspensionApprovals())) {
			for (RfiEventSuspensionApproval app : event.getSuspensionApprovals()) {
				for (RfiSuspensionApprovalUser user : app.getApprovalUsers()) {
					if ((SecurityLibrary.getLoggedInUser().getId()).equals(user.getUser().getId()) && ApprovalStatus.PENDING == user.getApprovalStatus()) {
						showApprovalRemark = true;
					}
				}
				if (app.isActive()) {
					LOG.info(">>>>>>>>>>>>>>>>>>>>>> : " + app.getLevel() + " - " + app.isActive());
					isSuspensionApprovalActive = true;
					break;
				}
			}
		}

		model.addAttribute("isSuspensionApprovalActive", isSuspensionApprovalActive);
		model.addAttribute("showApprovalRemark", showApprovalRemark);
		if (event.getStatus() == EventStatus.DRAFT || (event.getStatus() == EventStatus.SUSPENDED && !isSuspensionApprovalActive)) {
			if (eventPermissions.isEvaluator() && event.getStatus() == EventStatus.SUSPENDED && !event.getEventOwner().getId().equals(SecurityLibrary.getLoggedInUser().getId())) {
				model.addAttribute("showSummaryTab", true);
				return new ModelAndView("viewSummary");
			} else {
				return new ModelAndView("eventSummary");
			}
		} else if (event.getStatus() == EventStatus.CLOSED && eventPermissions.isConclusionUser()) {
			return new ModelAndView("redirect:/buyer/RFI/envelopList/" + eventId);
		} else {
			if (!eventPermissions.isUnMaskUser()) {
				model.addAttribute("showSummaryTab", true);
				return new ModelAndView("viewSummary");
			} else {
				return new ModelAndView("redirect:/buyer/RFI/envelopList/" + eventId);
			}
		}
	}

	private RfiEvent buildModel(String eventId, Model model, EventPermissions eventPermissions) {
		RfiEvent event = eventService.loadRfiEventForSummeryById(eventId);
		model.addAttribute("event", event);
		List<RfiReminder> startReminderList = null;
		List<RfiReminder> endReminderList = null;
		if (CollectionUtil.isNotEmpty(event.getRfiReminder())) {
			startReminderList = new ArrayList<RfiReminder>();
			endReminderList = new ArrayList<RfiReminder>();
			for (RfiReminder reminder : event.getRfiReminder()) {
				if (reminder.getStartReminder() != null && reminder.getStartReminder() == Boolean.TRUE) {
					startReminderList.add(reminder);
				} else {
					endReminderList.add(reminder);
				}
			}
		}
		model.addAttribute("startReminders", startReminderList);
		model.addAttribute("endReminders", endReminderList);
		model.addAttribute("listDocs", rfiDocumentService.findAllRfiEventdocsbyEventId(event.getId())); // event.getDocuments());
		List<Supplier> suppList = rfiMeetingService.getEventSuppliers(eventId);

		List<FeePojo> eventsuppList = rfiEventSupplierService.getAllInvitedSuppliersByEventId(eventId);

		model.addAttribute("listFavSuppliers", eventsuppList);
		model.addAttribute("supplierList", suppList);

		model.addAttribute("eventSuppliers", rfiEventSupplierService.getEventQualifiedSuppliersForEvaluation(event.getId()));

		// List<FavouriteSupplier> supplierList =
		// favoriteSupplierService.favoriteSuppliersOfBuyer(SecurityLibrary.getLoggedInUserTenantId(), invitedList);
		/*
		 * List<FavouriteSupplier> returnArray = new ArrayList<>(); List<FavouriteSupplier> supplierList =
		 * favoriteSupplierService.favoriteSuppliersOfBuyer(SecurityLibrary.getLoggedInUserTenantId(), null); boolean f
		 * = true; for (FavouriteSupplier fevSupplier : supplierList) { f = true; for (FeePojo favouriteSupplier :
		 * eventsuppList) { if (fevSupplier.getSupplier().getId().equals(favouriteSupplier.getId())) { f = false; break;
		 * } } if (f) { returnArray.add(fevSupplier); } } model.addAttribute("suppliers", returnArray);
		 */

		Boolean filterByIndustryCategory = rfxTemplateService.findTemplateIndustryCategoryFlagByEventId(event.getId(), getEventType());
		if (filterByIndustryCategory == null) {
			filterByIndustryCategory = Boolean.FALSE;
		}
		List<EventSupplierPojo> supplierList = favoriteSupplierService.favoriteEventSupplierPojosOfBuyerByIndusCategory(SecurityLibrary.getLoggedInUserTenantId(), filterByIndustryCategory, event.getId(), getEventType(), null);

		model.addAttribute("suppliers", supplierList);

		List<RfiEventMeeting> meetingList = rfiMeetingService.getAllRfiMeetingWithPlainDocByEventId(event.getId());
		model.addAttribute("meetingList", meetingList); // event.getMeetings());
		// model.addAttribute("cqList", event.getCqs());
		model.addAttribute("cqList", cqService.findRfiCqForEvent(eventId));
		model.addAttribute("rfxType", RfxTypes.values());

		User unMaskedUser = rfiEventDao.getUnMaskedUserNameAndMailByEventId(event.getId());
		if (unMaskedUser != null) {
			model.addAttribute("unMaskedUser", unMaskedUser);
		} else {
			model.addAttribute("unMaskedUser", null);
		}

		try {
			if (event.getTemplate() != null && event.getTemplate().getId() != null) {
				model.addAttribute("rfxTemplate", event.getTemplate());
			}
		} catch (Exception e1) {
			LOG.error("Error while sending rfx Template :" + e1.getMessage(), e1);
		}

		if (event.getNextEventId() != null) {
			if (event.getNextEventType() != null) {
				switch (event.getNextEventType()) {
				case RFA: {
					RfaEvent rfaEvent = rfaEventService.getPlainEventById(event.getNextEventId());
					model.addAttribute("nextEvent", rfaEvent);
					break;
				}
				case RFI: {
					RfiEvent rfiEvent = rfiEventService.getPlainEventById(event.getNextEventId());
					model.addAttribute("nextEvent", rfiEvent);
					break;
				}
				case RFP: {
					RfpEvent rfpEvent = rfpEventService.getPlainEventById(event.getNextEventId());
					model.addAttribute("nextEvent", rfpEvent);
					break;
				}
				case RFQ: {
					RfqEvent rfqEvent = rfqEventService.getPlainEventById(event.getNextEventId());
					model.addAttribute("nextEvent", rfqEvent);
					break;
				}
				case RFT: {
					RftEvent rftEvent = rftEventService.getPlainEventById(event.getNextEventId());
					model.addAttribute("nextEvent", rftEvent);
					break;
				}
				default:
					break;
				}
			}
		}
		if (event.getPreviousEventId() != null) {
			if (event.getPreviousEventType() != null) {
				switch (event.getPreviousEventType()) {
				case RFA: {
					RfaEvent rfaEvent = rfaEventService.getPlainEventById(event.getPreviousEventId());
					model.addAttribute("previousEvent", rfaEvent);
					break;
				}
				case RFI: {
					RfiEvent rfiEvent = rfiEventService.getPlainEventById(event.getPreviousEventId());
					model.addAttribute("previousEvent", rfiEvent);
					break;
				}
				case RFP: {
					RfpEvent rfpEvent = rfpEventService.getPlainEventById(event.getPreviousEventId());
					model.addAttribute("previousEvent", rfpEvent);
					break;
				}
				case RFQ: {
					RfqEvent rfqEvent = rfqEventService.getPlainEventById(event.getPreviousEventId());
					model.addAttribute("previousEvent", rfqEvent);
					break;
				}
				case RFT: {
					RftEvent rftEvent = rftEventService.getPlainEventById(event.getPreviousEventId());
					model.addAttribute("previousEvent", rftEvent);
					break;
				}
				default:
					break;
				}
			}
		}

		SourcingFormRequest request = null;
		if (StringUtils.checkString(event.getPreviousRequestId()).length() > 0) {
			request = sourcingFormRequestService.getSourcingRequestById(event.getPreviousRequestId());
		}
		model.addAttribute("previousRequest", request);

		model.addAttribute("envelopList", envelopService.getAllRfiEnvelopByEventId(eventId));
		model.addAttribute("envelopListCount", rfiEnvelopService.getAllEnvelopCountByEventId(event.getId()));
		model.addAttribute("comments", event.getComment());
		model.addAttribute("suspComments", event.getSuspensionComment());
		model.addAttribute("eventAudit", eventAuditService.getRfiEventAudit(event.getId()));
		model.addAttribute("eventPermissions", eventPermissions);
		model.addAttribute("eventTimeline", rfiEventService.getRfiEventTimeline(event.getId()));
		model.addAttribute("eventContactsList", rfiEventService.getAllContactForEvent(event.getId()));
		// List<User> userListSumm =
		// userService.fetchAllActiveUsersForEnvelopForTenant(SecurityLibrary.getLoggedInUserTenantId());
		// List<User> userListSumm =
		// userService.fetchAllActiveNormalUsersForEnvelopForTenant(SecurityLibrary.getLoggedInUserTenantId());
		// model.addAttribute("openers", userListSumm);
		// model.addAttribute("evaluationOwner", userListSumm);
		//
		List<UserPojo> userListSumm = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", UserType.NORMAL_USER);
		model.addAttribute("openers", userListSumm);
		model.addAttribute("evaluationOwner", userListSumm);

		model.addAttribute("envelop", new RfiEnvelop());
		List<User> assignedTeamMembers = new ArrayList<>();
		for (RfiTeamMember rfiTeamMember : ((RfiEvent) event).getTeamMembers()) {
			try {
				assignedTeamMembers.add((User) rfiTeamMember.getUser().clone());
			} catch (Exception e) {
				LOG.info("Error while fetching Team Members");
			}
		}
		List<User> userTeamMemberList = new ArrayList<User>();
		// List<User> activeUserList =
		// userService.fetchAllActiveUsersForTenant(SecurityLibrary.getLoggedInUserTenantId());
		// List<User> activeUserList =
		// userService.fetchAllActiveNormalUsersForTenant(SecurityLibrary.getLoggedInUserTenantId());
		//
		// for (User user : activeUserList) {
		// try {
		// userTeamMemberList.add((User) user.clone());
		// } catch (Exception e) {
		// LOG.info("Error while cloning the user List :" + e.getMessage());
		// }
		// }

		for (UserPojo user : userListSumm) {
			try {
				User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted());
				userTeamMemberList.add(u);
			} catch (Exception e) {
				LOG.info("Error while cloning the user List :" + e.getMessage());
			}
		}

		userTeamMemberList.removeAll(assignedTeamMembers);
		model.addAttribute("userTeamMemberList", userTeamMemberList);
		model.addAttribute("eventPermissions", eventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));

		List<RfiApprovalUser> rfiApprovalUserList = new ArrayList<RfiApprovalUser>();
		for (User user : userTeamMemberList) {
			rfiApprovalUserList.add(new RfiApprovalUser(user));
		}
		model.addAttribute("userList", rfiApprovalUserList);
		// List<User> userList = userService.fetchAllActiveUsersForTenant(SecurityLibrary.getLoggedInUserTenantId());
		List<User> userAppList = new ArrayList<User>();
		if (CollectionUtil.isNotEmpty(event.getApprovals())) {
			for (RfiEventApproval approvalLevel : event.getApprovals()) {
				approvalLevel.getLevel();
				if (CollectionUtil.isNotEmpty(approvalLevel.getApprovalUsers())) {
					for (RfiApprovalUser user : approvalLevel.getApprovalUsers()) {
						User u = new User(user.getUser().getId(), user.getUser().getLoginId(), user.getUser().getName(), user.getUser().getCommunicationEmail(), user.getUser().getEmailNotifications(), user.getUser().getTenantId(), user.getUser().isDeleted());
						if (!userAppList.contains(u)) {
							userAppList.add(u);
						}
					}
				}
			}
		}
		List<UserPojo> appUserList = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", null);
		for (UserPojo user : appUserList) {
			try {
				User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted());
				if (!userAppList.contains(u)) {
					userAppList.add(u);
				}
			} catch (Exception e) {
				LOG.info("Error while cloning the user List :" + e.getMessage());
			}
		}
		model.addAttribute("userList1", userAppList);


		List<RfiEventSor> sorList = event.getEventSors();


		for (Sor bq : sorList) {
			int i = 4;
			if (StringUtils.checkString(bq.getField1Label()).length() > 0) {
				i++;
			}

			bq.setHeaderCount(i);
		}

		model.addAttribute("sorList", sorList);

		return event;
	}

	@RequestMapping(path = "/viewSummary/{eventId}", method = RequestMethod.GET)
	public ModelAndView viewSummary(@PathVariable String eventId, Model model, HttpServletRequest request, RedirectAttributes redir) {
		if (StringUtils.checkString(eventId).length() == 0) {
			return new ModelAndView("redirect:/400_error");
		}
		boolean subscriptionExpired = super.checkSubscriptionPackageUserBased();
		model.addAttribute("subscriptionExpired", subscriptionExpired);

		EventPermissions eventPermissions = eventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
		RfiEvent event = buildModel(eventId, model, eventPermissions);

		boolean isSuspensionApprovalActive = false;
		boolean showApprovalRemark = false;

		if (event.getStatus() == EventStatus.SUSPENDED && CollectionUtil.isNotEmpty(event.getSuspensionApprovals())) {
			for (RfiEventSuspensionApproval app : event.getSuspensionApprovals()) {
				for (RfiSuspensionApprovalUser user : app.getApprovalUsers()) {
					if ((SecurityLibrary.getLoggedInUser().getId()).equals(user.getUser().getId()) && ApprovalStatus.PENDING == user.getApprovalStatus()) {
						showApprovalRemark = true;
					}
				}
				if (app.isActive()) {
					isSuspensionApprovalActive = true;
					break;
				}
			}
		}

		model.addAttribute("isSuspensionApprovalActive", isSuspensionApprovalActive);
		model.addAttribute("showApprovalRemark", showApprovalRemark);

		if (event.getStatus() == EventStatus.DRAFT && (eventPermissions.isOwner() || eventPermissions.isViewer() || eventPermissions.isEditor())) {
			return new ModelAndView("eventSummary");
		}
/*		if (!checkPermissionToAllow(eventPermissions)) {
			redir.addFlashAttribute("requestedUrl", request.getRequestURL());
			return new ModelAndView("redirect:/403_error");
		}*/
		model.addAttribute("invitedSupplier", rfiEventSupplierService.getAllSubmittedSupplierByEventId(eventId));
		model.addAttribute("showSummaryTab", true);
		return new ModelAndView("viewSummary");
	}

	/**
	 * @param eventPermissions
	 * @return
	 */
	private boolean checkPermissionToAllow(EventPermissions eventPermissions) {
		boolean allow = false;
		if (eventPermissions.isOwner() || eventPermissions.isEditor() || eventPermissions.isApprover() || eventPermissions.isEvaluator() || eventPermissions.isLeadEvaluator() || eventPermissions.isOpener() || eventPermissions.isViewer() || eventPermissions.isConclusionUser()) {
			allow = true;
		}
		if (SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY")) {
			allow = true;
		}
		return allow;
	}

	@RequestMapping(value = "/summeryPrevious", method = RequestMethod.POST)
	public String summeryPrevious(@ModelAttribute RfiEvent rftEvent, Model model) {
		LOG.info("Previous called in Documents : " + rftEvent.getId());
		rftEvent = eventService.getRfiEventById(rftEvent.getId());
		if (rftEvent != null) {
			model.addAttribute("eventPermissions", eventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), rftEvent.getId()));
			return "redirect:envelopList/" + rftEvent.getId();
		} else {
			LOG.error("Event not found redirecting to login ");
			return "redirect:/login";
		}
	}

	@RequestMapping(value = "finishEvent", method = RequestMethod.POST)
	public ModelAndView finishEvent(@ModelAttribute("event") RfiEvent event, BindingResult result, Model model, RedirectAttributes redir, HttpSession session) {
		LOG.info("Finish Event create : " + event.getId());
		JRSwapFileVirtualizer virtualizer = null;
		try {
			virtualizer = new JRSwapFileVirtualizer(200, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);
			try {
				// Check subscription limit
				super.checkSubscriptionPackage(SecurityLibrary.getLoggedInUserTenantId());
			} catch (SubscriptionException e) {
				LOG.error(e.getMessage());
				redir.addFlashAttribute("error", messageSource.getMessage("reached.subscription.limit", new Object[] { e.getMessage() }, Global.LOCALE));
				return new ModelAndView("redirect:/buyer/buyerDashboard");
			} catch (Exception e) {
				LOG.error("Error While checking subscription package : " + e.getMessage(), e);
			}
			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}

			SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/YYYY hh:mm a");
			dateTimeFormat.setTimeZone(timeZone);
			if (StringUtils.checkString(event.getId()).length() > 0) {
				LOG.info("Finish Event Called" + SecurityLibrary.getLoggedInUser());
				EventPermissions eventPermissions = eventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), event.getId());
				model.addAttribute("eventPermissions", eventPermissions);
				// implement validation to check if at least one Envelope exists
				Integer envelopCount = eventService.getCountOfEnvelopByEventId(event.getId());
				LOG.info("Envelope Count  :  " + envelopCount);
				if (envelopCount == 0) {
					LOG.info("Envelope Count My  :  " + envelopCount);
					buildModel(event.getId(), model, eventPermissions);
					model.addAttribute("error", messageSource.getMessage("summary.envelope.not.exists", new Object[] { event.getEventName() }, Global.LOCALE));
					return new ModelAndView("eventSummary");
				}

				List<RfiEventSor> notAssignSors = rfiSorService.getNotAssignedSorIdsByEventId(event.getId());
				if (CollectionUtil.isNotEmpty(notAssignSors)) {
					List<String> sorNames = new ArrayList<String>();
					for (RfiEventSor rftEventSor : notAssignSors) {
						sorNames.add(rftEventSor.getName());
					}
					buildModel(event.getId(), model, eventPermissions);
					model.addAttribute("error", messageSource.getMessage("summary.sor.not.assigned", new Object[] { sorNames }, Global.LOCALE));
					return new ModelAndView("eventSummary");
				}

				if(event.getScheduleOfRate() == Boolean.TRUE) {
					Integer totalSor = rfiSorService.getCountOfSorByEventId(event.getId());
					if(totalSor == null || totalSor == 0) {
						model.addAttribute("error", messageSource.getMessage("summary.sor.nosor", new Object[] {}, Global.LOCALE));
						buildModel(event.getId(), model, eventPermissions);
						return new ModelAndView("eventSummary");
					}
				}

				// Validate Sor items
				List<String> notSectionAddedSors = rfiSorService.getNotSectionAddedRfiSorIdsByEventId(event.getId());
				if (CollectionUtil.isNotEmpty(notSectionAddedSors)) {
					String names = org.apache.commons.lang.StringUtils.join(notSectionAddedSors, ",");
					model.addAttribute("error", messageSource.getMessage("summary.sor.not.sectionAdded", new Object[] { names }, Global.LOCALE));
					buildModel(event.getId(), model, eventPermissions);
					return new ModelAndView("eventSummary");
				}

				// Validate Sor items inside section
				List<String> notItemSectionAddedSors = rfiSorService.getNotSectionItemAddedRfiSorIdsByEventId(event.getId());
				if (CollectionUtil.isNotEmpty(notItemSectionAddedSors)) {
					String names = org.apache.commons.lang.StringUtils.join(notItemSectionAddedSors, ",");
					model.addAttribute("error", messageSource.getMessage("summary.sor.not.sectionAdded", new Object[] { names }, Global.LOCALE));
					buildModel(event.getId(), model, eventPermissions);
					return new ModelAndView("eventSummary");
				}

				List<RfiCq> notAssignCqs = cqService.getNotAssignedCqIdsByEventId(event.getId());
				if (CollectionUtil.isNotEmpty(notAssignCqs)) {
					List<String> cqNames = new ArrayList<String>();
					for (RfiCq rftCq : notAssignCqs) {
						cqNames.add(rftCq.getName());
					}
					buildModel(event.getId(), model, eventPermissions);
					model.addAttribute("error", messageSource.getMessage("summary.cq.not.assigned", new Object[] { cqNames }, Global.LOCALE));
					return new ModelAndView("eventSummary");
				}

				// check if empty envelop present
				RfiEnvelop envelop = rfiEnvelopService.getEmptyEnvelopByEventId(event.getId());
				if (envelop != null && envelop.getEnvelopEmpty() == 0) {
					buildModel(event.getId(), model, eventPermissions);
					model.addAttribute("error", messageSource.getMessage("empty.envelop.finish", new Object[] { envelop.getEnvelopTitle() }, Global.LOCALE));
					return new ModelAndView("eventSummary");
				}

				// validate approval reminder settings
				LOG.info("--------------->>>>>>>>>>>>> " + event.getEnableApprovalReminder());
				if (Boolean.TRUE == event.getEnableApprovalReminder()) {
					if (event.getReminderAfterHour() == null) {
						buildModel(event.getId(), model, eventPermissions);
						model.addAttribute("error", messageSource.getMessage("approval.reminder.add.hour", new Object[] {}, Global.LOCALE));
						return new ModelAndView("eventSummary");
					}
					if (event.getReminderCount() == null) {
						buildModel(event.getId(), model, eventPermissions);
						model.addAttribute("error", messageSource.getMessage("approval.reminder.count.reminder", new Object[] {}, Global.LOCALE));
						return new ModelAndView("eventSummary");
					}
				}

				// Validate Cq items
				List<String> notSectionAddedCqs = cqService.getNotSectionAddedRfiCqIdsByEventId(event.getId());
				if (CollectionUtil.isNotEmpty(notSectionAddedCqs)) {
					String names = org.apache.commons.lang.StringUtils.join(notSectionAddedCqs, ",");
					model.addAttribute("error", messageSource.getMessage("summary.cq.not.sectionAdded", new Object[] { names }, Global.LOCALE));
					buildModel(event.getId(), model, eventPermissions);
					return new ModelAndView("eventSummary");
				}
				// Validate Cq items inside section
				List<String> notItemSectionAddedCqs = cqService.getNotSectionItemAddedRfiCqIdsByEventId(event.getId());
				if (CollectionUtil.isNotEmpty(notItemSectionAddedCqs)) {
					String names = org.apache.commons.lang.StringUtils.join(notItemSectionAddedCqs, ",");
					model.addAttribute("error", messageSource.getMessage("summary.cq.item.not.sectionAdded", new Object[] { names }, Global.LOCALE));
					buildModel(event.getId(), model, eventPermissions);
					return new ModelAndView("eventSummary");
				}

				// Validate Supplier
				if (event.getEventVisibility() == EventVisibilityType.PRIVATE) {
					long supplierList = rfiEventService.getEventSuppliersCount(event.getId());
					if (supplierList == 0) {
						LOG.info("supplierList:" + supplierList);
						model.addAttribute("error", messageSource.getMessage("summary.not.supplierAdded", new Object[] { null }, Global.LOCALE));
						buildModel(event.getId(), model, eventPermissions);
						return new ModelAndView("eventSummary");
					}
				}

				RfiEvent persistObj = eventService.getPlainEventWithTemplateById(event.getId());

				// validate Decimal and Base Currency
				if (persistObj.getDecimal() == null) {
					model.addAttribute("error", messageSource.getMessage("summary.decimal.not.exists", new Object[] { event.getEventName() }, Global.LOCALE));
					buildModel(event.getId(), model, eventPermissions);
					return new ModelAndView("eventSummary");
				}

				if (persistObj.getBaseCurrency() == null) {
					model.addAttribute("error", messageSource.getMessage("summary.baseCurrency.not.exists", new Object[] { event.getEventName() }, Global.LOCALE));
					buildModel(event.getId(), model, eventPermissions);
					return new ModelAndView("eventSummary");
				}
				if (!persistObj.getViewSupplerName() && CollectionUtil.isEmpty(persistObj.getUnMaskedUsers())) {
					model.addAttribute("error", messageSource.getMessage("unmask.user", new Object[] {}, Global.LOCALE));
					buildModel(event.getId(), model, eventPermissions);
					return new ModelAndView("eventSummary");

				}
				if (persistObj.getEventEnd() != null && persistObj.getEventEnd().before(new Date())) {
					LOG.info("EVENT endDate:" + persistObj.getEventEnd());
					model.addAttribute("error", messageSource.getMessage("rftEvent.error.end", new Object[] { dateTimeFormat.format(persistObj.getEventEnd()) }, Global.LOCALE));
					buildModel(event.getId(), model, eventPermissions);
					return new ModelAndView("eventSummary");
				}
				if (persistObj.getEventStart() != null && persistObj.getEventEnd() != null && persistObj.getEventEnd().before(persistObj.getEventStart())) {
					LOG.info("EVENT endDate:" + persistObj.getEventEnd());
					model.addAttribute("error", messageSource.getMessage("rftEvent.error.enddate", new Object[] { dateTimeFormat.format(persistObj.getEventEnd()) }, Global.LOCALE));
					buildModel(event.getId(), model, eventPermissions);
					return new ModelAndView("eventSummary");
				}
				
				if (persistObj.getEventPublishDate() != null && persistObj.getEventStart() != null && persistObj.getEventPublishDate().after(persistObj.getEventStart())) {
					model.addAttribute("error", messageSource.getMessage("rftEvent.error.publishdate", new Object[] { dateTimeFormat.format(persistObj.getEventPublishDate()) }, Global.LOCALE));
					buildModel(event.getId(), model, eventPermissions);
					return new ModelAndView("eventSummary");
				}

				boolean validateTenderDate = false;

				if (persistObj.getTemplate() != null) {
					LOG.info("inside to check template");
					RfxTemplate rfxTemplate = rfxTemplateService.getRfxTemplateForEditById(persistObj.getTemplate().getId(), SecurityLibrary.getLoggedInUserTenantId());

					model.addAttribute("templateFields", rfxTemplate.getFields());
					for (TemplateField field : rfxTemplate.getFields()) {
						switch (field.getFieldName()) {
						case EXPECTED_TENDER_STARTEND_DATTIME:
							if (field.getOptional()) {
								validateTenderDate = true;
							}
							break;
						case FEE_STARTEND_DATETIME:

							break;
						default:
							break;
						}

					}
					model.addAttribute("rfxTemplate", rfxTemplate);
				}
				if (!validateTenderDate && persistObj.getExpectedTenderStartDate() != null && persistObj.getEventStart() != null && persistObj.getExpectedTenderStartDate().before(persistObj.getEventStart())) {
					Date pubDate = persistObj.getExpectedTenderStartDate();
					SimpleDateFormat publishDate = new SimpleDateFormat("dd/MM/YYYY hh:mm a");
					publishDate.setTimeZone(timeZone);
					model.addAttribute("error", messageSource.getMessage("rftEvent.error.expected.tender.startdate", new Object[] { publishDate.format(pubDate), publishDate.format(persistObj.getEventStart()) }, Global.LOCALE));
					buildModel(event.getId(), model, eventPermissions);
					return new ModelAndView("eventSummary");
				}

				if (!validateMeetings(model, dateTimeFormat, persistObj, redir)) {
					return new ModelAndView("redirect:eventSummary/" + event.getId());
				}

				if (persistObj.getStatus() == EventStatus.SUSPENDED) {
					// persistObj = suspensionApprovalService.doApproval(persistObj, session,
					// SecurityLibrary.getLoggedInUser(), virtualizer);

					RfiEvent rfiEventOBJ = eventService.loadRfiEventForSummeryById(event.getId());

					// if(!persistObj.getEnableSuspensionApproval()) {
					if ((Boolean.TRUE == persistObj.getEnableSuspensionApproval() && CollectionUtil.isEmpty(rfiEventOBJ.getSuspensionApprovals())) || Boolean.FALSE == persistObj.getEnableSuspensionApproval()) {
						eventService.resumeEvent(event);

						LOG.info("---------------------------------BEFORE AUDIT-----------------------------------");
						User user = SecurityLibrary.getLoggedInUser();
						snapShotAuditService.doRfiAudit(event, session, persistObj, user, AuditActionType.Resume, "event.audit.resume", virtualizer);

						LOG.info("---------------------------------AFTER AUDIT-----------------------------------");
						try {
							LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
							BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.RESUME, "Event '" + persistObj.getEventId() + "' Resumed", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFI);
							buyerAuditTrailDao.save(buyerAuditTrail);
							LOG.info("--------------------------AFTER AUDIT---------------------------------------");
						} catch (Exception e) {
							LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
						}
						try {
							LOG.info("publishing rfi resume event to epiportal");
							publishEventService.pushRfiEvent(event.getId(), SecurityLibrary.getLoggedInUser().getBuyer().getId(), event.getStatus() == EventStatus.APPROVED ? true : false);
						} catch (Exception e) {
							LOG.error("Error while publishing RFI event to EPortal:" + e.getMessage(), e);
						}
						try {
							// Send notification to suppliers

							try {
								// jmsTemplate.setDefaultDestinationName("QUEUE.EVENT.ADDI.SUPPLIER.NOTIFICATION");
								jmsTemplate.send("QUEUE.EVENT.ADDI.SUPPLIER.NOTIFICATION", new MessageCreator() {
									@Override
									public Message createMessage(Session session) throws JMSException {
										TextMessage objectMessage = session.createTextMessage();
										objectMessage.setText(event.getId());
										return objectMessage;
									}
								});
							} catch (Exception e) {
								LOG.error("Error sending message to queue : " + e.getMessage(), e);
							}

							// super.sendSupplierInvitationEmails(persistObj, getEventType());
						} catch (Exception e) {
							LOG.error("Error while sending notification to event creator " + e.getMessage(), e);
						}
						try {
							// Send Resume notification to suppliers
							if (persistObj.getSuspensionType() == SuspensionType.DELETE_NOTIFY || persistObj.getSuspensionType() == SuspensionType.KEEP_NOTIFY) {

								try {
									// jmsTemplate.setDefaultDestinationName("QUEUE.EVENT.RESUMED.NOTIFICATION");
									jmsTemplate.send("QUEUE.EVENT.RESUMED.NOTIFICATION", new MessageCreator() {

										@Override
										public Message createMessage(Session session) throws JMSException {
											TextMessage objectMessage = session.createTextMessage();
											objectMessage.setText(event.getId());
											return objectMessage;
										}
									});
								} catch (Exception e) {
									LOG.error("Error sending message to queue : " + e.getMessage(), e);
								}

								// super.sendEventResumeNotificationEmails(persistObj, getEventType());
							}
						} catch (Exception e) {
							LOG.error("Error while sending notification to event Resume " + e.getMessage(), e);
						}
						// model.addAttribute("envelop", new RfiEnvelop());
						try {
							simpMessagingTemplate.convertAndSend("/auctionTopic/" + event.getId(), "RESUME");
						} catch (Exception e) {
							LOG.error("Error while sending suspend event to MQ : " + e.getMessage(), e);
							buildModel(event.getId(), model, eventPermissions);
						}

						if (StringUtils.checkString(persistObj.getPreviousRequestId()).length() > 0) {
							tatReportService.updateTatReportEventStatus(persistObj.getEventId(), persistObj.getPreviousRequestId(), SecurityLibrary.getLoggedInUserTenantId(), EventStatus.ACTIVE);
						}
						redir.addFlashAttribute("success", messageSource.getMessage("event.resume.success", new Object[] { persistObj.getEventName() }, Global.LOCALE));

					} else if (Boolean.TRUE == persistObj.getEnableSuspensionApproval() && CollectionUtil.isNotEmpty(rfiEventOBJ.getSuspensionApprovals())) {
						persistObj = suspensionApprovalService.doApproval(persistObj, session, SecurityLibrary.getLoggedInUser(), virtualizer, model);
						redir.addFlashAttribute("success", "Your Resume Event request has been sent for approval.");
					}

				} else {

					LOG.info("PublishDate:" + persistObj.getEventPublishDate());
					LOG.info("EVENT STARTDATE:" + persistObj.getEventStart());

					if (persistObj.getEventPublishDate() != null && persistObj.getEventPublishDate().before(new Date())) {
						// Published date cannot be in the past
						LOG.info("EVENT PUBLISHDATE:" + persistObj.getEventPublishDate());
						model.addAttribute("error", messageSource.getMessage("rftEvent.error.publishDate", new Object[] { dateTimeFormat.format(persistObj.getEventPublishDate()) }, Global.LOCALE));
						buildModel(event.getId(), model, eventPermissions);
						return new ModelAndView("eventSummary");
					}
					if (persistObj.getEventStart() != null && persistObj.getEventStart().before(new Date())) {
						// Event start date cannot be in the past
						LOG.info("EVENT STARTDATE:" + persistObj.getEventStart());
						model.addAttribute("error", messageSource.getMessage("rftEvent.error.startdate", new Object[] { dateTimeFormat.format(persistObj.getEventStart()) }, Global.LOCALE));
						buildModel(event.getId(), model, eventPermissions);
						return new ModelAndView("eventSummary");
					}
					List<RfiReminder> reminderList = rfiEventService.getAllRfiEventReminderForEvent(event.getId());
					if (CollectionUtil.isNotEmpty(reminderList)) {
						for (RfiReminder reminder : reminderList) {
							LOG.info("Reminder Date:" + reminder.getReminderDate());
							if (reminder.getReminderDate().compareTo(new Date()) < 0) {
								model.addAttribute("error", messageSource.getMessage("event.reminder.pastdate", new Object[] {}, Global.LOCALE));
								buildModel(event.getId(), model, eventPermissions);
								return new ModelAndView("eventSummary");
							}
						}
					}

					Date eventApprovedAndFinishDate = new Date();

					LOG.info("=================BEFORE AUDIT=============================");
					User user = SecurityLibrary.getLoggedInUser();
					snapShotAuditService.doRfiAudit(event, session, persistObj, user, AuditActionType.Finish, "event.audit.finished", virtualizer);
					LOG.info("=================AFTER AUDIT=============================");
					persistObj.setSummaryCompleted(Boolean.TRUE);
					persistObj = eventService.updateRfiEvent(persistObj);
					persistObj = approvalService.doApproval(persistObj, session, SecurityLibrary.getLoggedInUser(), virtualizer, eventApprovedAndFinishDate);

					try {
						LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.FINISH, "Event '" + persistObj.getEventId() + "' sent for Approval.", user.getTenantId(), user, new Date(), ModuleType.RFI);
						buyerAuditTrailDao.save(buyerAuditTrail);
						LOG.info("--------------------------AFTER AUDIT---------------------------------------");
					} catch (Exception e) {
						LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
					}

					if (!

					validateMeetings(model, dateTimeFormat, persistObj, redir)) {
						return new ModelAndView("redirect:eventSummary/" + event.getId());
					}

					// byte[] summarySnapshot = null;
					// try {
					// JasperPrint eventSummary = rfiEventService.getEvaluationSummaryPdf(event,
					// SecurityLibrary.getLoggedInUser(), (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY));
					// summarySnapshot = JasperExportManager.exportReportToPdf(eventSummary);
					// } catch (JRException e) {
					// LOG.error("Error while Store summary PDF as byte : " + e.getMessage(), e);
					// }
					// RfiEventAudit audit = new RfiEventAudit(SecurityLibrary.getLoggedInUser().getBuyer(), persistObj,
					// SecurityLibrary.getLoggedInUser(), new java.util.Date(), AuditActionType.Finish,
					// messageSource.getMessage("event.audit.finished", new Object[] { persistObj.getEventName() },
					// Global.LOCALE), summarySnapshot);
					// eventAuditService.save(audit);

					try {
						// Send notification to event created
						super.sendRfxCreatedEmails(persistObj, persistObj.getCreatedBy(), getEventType());
						// super.sendSupplierInvitationEmails(persistObj, getEventType());
					} catch (Exception e) {
						LOG.error("Error while sending notification to event creator " + e.getMessage(), e);
					}

					if (StringUtils.checkString(persistObj.getPreviousRequestId()).length() > 0) {
						TatReportPojo tatReport = tatReportService.getTatReportByEventIdAndFormIdAndTenantId(persistObj.getPreviousRequestId(), persistObj.getEventId(), SecurityLibrary.getLoggedInUserTenantId());
						if (tatReport != null) {
							tatReportService.updateTatReportFinishDateAndDecimalAndStatus(tatReport.getId(), eventApprovedAndFinishDate, persistObj.getDecimal(), persistObj.getStatus());

						}
					}

					redir.addFlashAttribute("success", messageSource.getMessage("event.finish.success", new Object[] { persistObj.getEventName() }, Global.LOCALE));
				}

				try

				{
					eventService.insertTimeLine(event.getId());
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}

			}
		} catch (Exception e) {
			LOG.error("Error While Finish the event : :  " + e.getMessage(), e);
			// model.addAttribute("error", "Error saving event details : " + e.getMessage());
			model.addAttribute("error", messageSource.getMessage("rfisummary.while.saving.eventdetails", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ModelAndView("eventSummary");
		} finally {
			if (virtualizer != null) {
				virtualizer.cleanup();
			}
		}
		return new ModelAndView("redirect:/buyer/buyerDashboard");
	}

	private boolean validateMeetings(Model model, SimpleDateFormat dateTimeFormat, RfiEvent persistObj, RedirectAttributes redir) {
		boolean valid = true;
		if (persistObj.getMeetingReq() != null && persistObj.getMeetingReq()) {
			// Event start date cannot be in the past
			List<Date> meetingDates = rfiEventService.getAllMeetingDateByEventId(persistObj.getId());

			LOG.info("meeting Dates list size :" + meetingDates.size());
			if (CollectionUtil.isNotEmpty(meetingDates)) {
				for (Date meetingDate : meetingDates) {
					LOG.info("Meeting Date : " + meetingDate + " : publish Date : " + persistObj.getEventPublishDate() + " : Current Date : " + new Date());
					if ((persistObj.getEventPublishDate() != null && meetingDate != null && meetingDate.before(persistObj.getEventPublishDate())) || meetingDate.before(new Date())) {
						LOG.info("Meeting Date error : " + meetingDate + " : publish Date : " + persistObj.getEventPublishDate() + " : Current Date : " + new Date());
						redir.addAttribute("error", messageSource.getMessage("rft.meeting.error.eventpublishdate", new Object[] { dateTimeFormat.format(meetingDate), dateTimeFormat.format(persistObj.getEventPublishDate()) }, Global.LOCALE));
						// EventPermissions eventPermissions =
						// eventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(),
						// persistObj.getId());
						// buildModel(persistObj.getId(), model, eventPermissions);
						// return new ModelAndView("eventSummary");
						valid = false;
					}
					if (persistObj.getEventEnd() != null && meetingDate != null && persistObj.getEventEnd().before(meetingDate)) {
						LOG.info("Meeting Date error : " + meetingDate + " : eventEndDate : " + persistObj.getEventEnd() + " : Current Date : " + new Date());
						redir.addAttribute("error", messageSource.getMessage("rft.meeting.error.eventenddate", new Object[] { dateTimeFormat.format(meetingDate), dateTimeFormat.format(persistObj.getEventEnd()) }, Global.LOCALE));
						valid = false;
					}
				}
			}
			if (EventVisibilityType.PRIVATE == persistObj.getEventVisibility()) {
				List<RfiEventMeeting> meetingList = rfiEventMeetingService.findMeetingWithoutSuplliersByEventId(persistObj.getId());
				if (CollectionUtil.isNotEmpty(meetingList)) {
					LOG.info("Meeting does not have suppliers");
					List<String> meetings = new ArrayList<String>();
					for (RfiEventMeeting meeting : meetingList) {
						meetings.add(meeting.getTitle());
					}
					redir.addAttribute("error", messageSource.getMessage("rft.meeting.error.suppliers.required", new Object[] { String.join(",", meetings) }, Global.LOCALE));
					valid = false;
				}
			}

		}
		return valid;
	}

	@RequestMapping(value = "/downloadRftSummaryDocument/{id}", method = RequestMethod.GET)
	public void downloadRftSummaryDocument(@PathVariable String id, HttpServletResponse response) throws IOException {
		try {
			LOG.info("RFI Event Download  :: :: " + id + "::::::");
			RfiEventDocument docs = rfiDocumentService.findRfiEventdocsById(id);
			super.buildDocumentFile(response, docs);
		} catch (Exception e) {
			LOG.error("Error while downloaded RFT event Document : " + e.getMessage(), e);
		}
	}

	@RequestMapping(value = "/downloadEventMeetingDocument/{id}", method = RequestMethod.GET)
	public void downloadEventMeetingDocument(@PathVariable String id, HttpServletResponse response) throws IOException {
		try {
			LOG.info("RFT Event Download  :: :: " + id + "::::::");
			RfiEventMeetingDocument docs = rfiMeetingService.getRfiEventMeetingDocument(id);
			super.buildMeetingFile(response, docs);
		} catch (Exception e) {
			LOG.error("Error while downloaded RFT event Document : " + e.getMessage(), e);
		}
	}

	@RequestMapping(value = "cancelEvent", method = RequestMethod.POST)
	public ModelAndView cancelEvent(@ModelAttribute RfiEvent event, Model model, RedirectAttributes redirectAttributes, HttpSession session) {
		JRSwapFileVirtualizer virtualizer = null;
		try {
			// Virtualizar - To increase the performance
			virtualizer = new JRSwapFileVirtualizer(200, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);

			event = rfiEventService.cancelEvent(event, session, virtualizer, SecurityLibrary.getLoggedInUser());

			tatReportService.updateTatReportEventStatus(event.getEventId(), SecurityLibrary.getLoggedInUserTenantId(), EventStatus.CANCELED);

			redirectAttributes.addFlashAttribute("success", messageSource.getMessage("event.cancel.success", new Object[] { StringUtils.checkString(event.getEventName()).length() == 0 ? event.getEventId() : event.getEventName() }, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error while event cancel " + e.getMessage(), e);
			EventPermissions eventPermissions = eventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), event.getId());
			buildModel(event.getId(), model, eventPermissions);
			return new ModelAndView("eventSummary");
		} finally {
			if (virtualizer != null) {
				virtualizer.cleanup();
			}
		}
		return new ModelAndView("redirect:/buyer/buyerDashboard");
	}

	@RequestMapping(path = "/suspendEvent", method = RequestMethod.POST)
	public ModelAndView suspendEvent(@ModelAttribute RfiEvent event, RedirectAttributes redirectAttributes, Model model, HttpSession session) {
		JRSwapFileVirtualizer virtualizer = null;
		try {
			// Virtualizar - To increase the performance
			virtualizer = new JRSwapFileVirtualizer(200, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);
			eventService.suspendEvent(event);

			// byte[] summarySnapshot = null;
			// try {
			// JasperPrint eventSummary = rfiEventService.getEvaluationSummaryPdf(event,
			// SecurityLibrary.getLoggedInUser(), (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY));
			// summarySnapshot = JasperExportManager.exportReportToPdf(eventSummary);
			// } catch (Exception e) {
			// LOG.error("Error while Store summary PDF as byte : " + e.getMessage(), e);
			// }
			// RfiEventAudit audit = new RfiEventAudit(SecurityLibrary.getLoggedInUser().getBuyer(), event,
			// SecurityLibrary.getLoggedInUser(), new java.util.Date(), AuditActionType.Suspend,
			// messageSource.getMessage("event.audit.suspended", new Object[] { event.getEventName(),
			// event.getSuspensionType() }, Global.LOCALE), summarySnapshot);
			// eventAuditService.save(audit);

			LOG.info("-------------------------BEFORE AUDIT-------------------------------------");
			RfiEvent persistObject = rfiEventService.getRfiEventByeventId(event.getId());
			User user = SecurityLibrary.getLoggedInUser();

			List<RfiEventSuspensionApproval> approvalList = persistObject.getSuspensionApprovals();
			if (CollectionUtil.isNotEmpty(approvalList)) {
				for (RfiEventSuspensionApproval approval : approvalList) {
					approval.setDone(false);
					approval.setActive(false);
					for (RfiSuspensionApprovalUser user1 : approval.getApprovalUsers()) {
						user1.setActionDate(null);
						user1.setApprovalStatus(ApprovalStatus.PENDING);
						user1.setRemarks(null);
						user1.setActionDate(null);
					}
				}
			}
			rfiEventDao.update(persistObject);

			snapShotAuditService.doRfiAudit(event, session, persistObject, user, AuditActionType.Suspend, "event.audit.suspended", virtualizer);
			LOG.info("-------------------------AFTER AUDIT--------------------------------------------------");
			model.addAttribute("eventPermissions", eventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), event.getId()));
			try {
				LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.SUSPEND, "Event '" + persistObject.getEventId() + "' is suspended", persistObject.getCreatedBy().getTenantId(), persistObject.getCreatedBy(), new Date(), ModuleType.RFI);
				buyerAuditTrailDao.save(buyerAuditTrail);
				LOG.info("--------------------------AFTER AUDIT---------------------------------------");
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
			}
			try {
				// Send Suspend notification to suppliers
				if (event.getSuspensionType() == SuspensionType.DELETE_NOTIFY || event.getSuspensionType() == SuspensionType.KEEP_NOTIFY) {

					try {
						// jmsTemplate.setDefaultDestinationName("QUEUE.EVENT.SUSPEND.NOTIFICATION");
						jmsTemplate.send("QUEUE.EVENT.SUSPEND.NOTIFICATION", new MessageCreator() {
							@Override
							public Message createMessage(Session session) throws JMSException {
								TextMessage objectMessage = session.createTextMessage();
								objectMessage.setText(event.getId());
								return objectMessage;
							}
						});
					} catch (Exception e) {
						LOG.error("Error sending message to queue : " + e.getMessage(), e);
					}

					// super.sendEventSupendedNotificationEmails(event, getEventType());
				}
			} catch (Exception e) {
				LOG.error("Error while sending notification to event suspend " + e.getMessage(), e);
			}
			try {
				simpMessagingTemplate.convertAndSend("/auctionTopic/" + event.getId(), "SUSPENDED");
			} catch (Exception e) {
				LOG.error("Error while sending suspend event to MQ : " + e.getMessage(), e);
			}

			tatReportService.updateTatReportEventStatusById(event.getId(), SecurityLibrary.getLoggedInUserTenantId(), EventStatus.SUSPENDED);

			redirectAttributes.addFlashAttribute("success", messageSource.getMessage("event.suspended.success", new Object[] { event.getEventName() }, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error while event suspend " + e.getMessage(), e);
			EventPermissions eventPermissions = eventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), event.getId());
			buildModel(event.getId(), model, eventPermissions);
			// model.addAttribute("error", "Error while suspending Event");
			model.addAttribute("error", messageSource.getMessage("rfasummary.while.suspending.event", new Object[] {}, Global.LOCALE));
			return new ModelAndView("eventSummary");
		} finally {
			if (virtualizer != null) {
				virtualizer.cleanup();
			}
		}
		return new ModelAndView("redirect:/buyer/buyerDashboard");
	}

	@RequestMapping(path = "/resumeEvent", method = RequestMethod.POST)
	public ModelAndView resumeEvent(@ModelAttribute RfiEvent event, RedirectAttributes redirectAttributes, Model model, HttpSession session) {
		JRSwapFileVirtualizer virtualizer = null;
		try {
			// Check subscription limit
			super.checkSubscriptionPackageForUserBased();
		} catch (SubscriptionException e) {
			LOG.error(e.getMessage());
			// redirectAttributes.addFlashAttribute("error", "You have reached your subscription limit. " +
			// e.getMessage());
			redirectAttributes.addFlashAttribute("error", messageSource.getMessage("reached.subscription.limit", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ModelAndView("redirect:/buyer/buyerDashboard");
		} catch (Exception e) {
			LOG.error("Error While checking subscription package : " + e.getMessage(), e);
		}
		try {
			virtualizer = new JRSwapFileVirtualizer(200, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);
			eventService.resumeEvent(event);

			LOG.info("-----------------------------------BEFORE AUDIT-------------------------------------------");
			User user = SecurityLibrary.getLoggedInUser();
			snapShotAuditService.doRfiAudit(event, session, event, user, AuditActionType.Resume, "event.audit.resume", virtualizer);
			LOG.info("-----------------------------------AFTERAUDIT-------------------------------------------");

			try {
				LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.RESUME, "Event '" + event.getEventId() + "' Resumed", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFI);
				buyerAuditTrailDao.save(buyerAuditTrail);
				LOG.info("--------------------------AFTER AUDIT---------------------------------------");
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
			}
			try {
				LOG.info("publishing rfi resume event to epiportal");
				publishEventService.pushRfiEvent(event.getId(), SecurityLibrary.getLoggedInUser().getBuyer().getId(), false);
			} catch (Exception e) {
				LOG.error("Error while publishing RFI event to EPortal:" + e.getMessage(), e);
			}

			model.addAttribute("eventPermissions", eventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), event.getId()));
			try {
				// Send Resume notification to suppliers
				if (event.getSuspensionType() == SuspensionType.DELETE_NOTIFY || event.getSuspensionType() == SuspensionType.KEEP_NOTIFY) {
					try {
						// jmsTemplate.setDefaultDestinationName("QUEUE.EVENT.RESUMED.NOTIFICATION");
						jmsTemplate.send("QUEUE.EVENT.RESUMED.NOTIFICATION", new MessageCreator() {
							@Override
							public Message createMessage(Session session) throws JMSException {
								TextMessage objectMessage = session.createTextMessage();
								objectMessage.setText(event.getId());
								return objectMessage;
							}
						});
					} catch (Exception e) {
						LOG.error("Error sending message to queue : " + e.getMessage(), e);
					}
					// super.sendEventResumeNotificationEmails(event, getEventType());
				}
			} catch (Exception e) {
				LOG.error("Error while sending notification to event Resume " + e.getMessage(), e);
			}
			redirectAttributes.addFlashAttribute("success", messageSource.getMessage("event.resume.success", new Object[] { event.getEventName() }, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error while event resume " + e.getMessage(), e);
			EventPermissions eventPermissions = eventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), event.getId());
			buildModel(event.getId(), model, eventPermissions);
			return new ModelAndView("eventSummary");
		} finally {
			if (virtualizer != null) {
				virtualizer.cleanup();
			}
		}
		return new ModelAndView("redirect:/buyer/buyerDashboard");
	}

	@RequestMapping(value = "/eventTeamMembersList/{eventId}", method = RequestMethod.GET)
	public ResponseEntity<TableData<EventTeamMember>> eventPlainTeamMembersList(@PathVariable(name = "eventId") String eventId, TableDataInput input) {
		TableData<EventTeamMember> data = null;
		try {
			data = new TableData<EventTeamMember>(eventService.getTeamMembersForEvent(eventId));
			data.setDraw(input.getDraw());
		} catch (Exception e) {
			LOG.error("Error while loading draft list : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<EventTeamMember>>(data, HttpStatus.OK);
	}

	@RequestMapping(path = "/updateEventApproval", method = RequestMethod.POST)
	public String updateEventApproval(@ModelAttribute RfiEvent event, RedirectAttributes redir, HttpSession session) {
		JRSwapFileVirtualizer virtualizer = null;
		try {
			virtualizer = new JRSwapFileVirtualizer(200, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);

			Date eventApprovedAndFinishDate = new Date();

			event = eventService.updateEventApproval(event, SecurityLibrary.getLoggedInUser());
			approvalService.doApproval(event, session, SecurityLibrary.getLoggedInUser(), virtualizer, eventApprovedAndFinishDate);
			redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.approval.updated", new Object[] {}, Global.LOCALE));
		} catch (Exception e) {
			LOG.info("Error while Updating Approval :" + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.updating.approval", new Object[] { e.getMessage() }, Global.LOCALE));
		} finally {
			if (virtualizer != null) {
				virtualizer.cleanup();
			}
		}
		return "redirect:/buyer/" + RfxTypes.RFI.name() + "/eventSummary/" + event.getId();
	}

	@RequestMapping(path = "/summaryEnvelop", method = RequestMethod.POST)
	public ResponseEntity<EnvelopePojo> summaryEnvelop(@RequestParam("envelopeId") String envelopeId) {
		HttpHeaders headers = new HttpHeaders();
		RfiEnvelop rfiEnvelop = envelopService.getRfiEnvelopById(envelopeId);
		EnvelopePojo envelope = new EnvelopePojo(rfiEnvelop);
		@SuppressWarnings("unchecked")
		List<RfiEvaluatorUser> assignedEvaluators = (List<RfiEvaluatorUser>) envelope.getAssignedEvaluators();
		List<User> evaluators = new ArrayList<User>();

		@SuppressWarnings("unchecked")
		List<RfiEnvelopeOpenerUser> assignedOpeners = (List<RfiEnvelopeOpenerUser>) envelope.getAssignedOpeners();
		List<User> openersList = new ArrayList<User>();

		// List<User> userList = userService.fetchAllActiveUsersForTenant(SecurityLibrary.getLoggedInUserTenantId());
		// List<User> userList =
		// userService.fetchAllActiveNormalUsersForTenant(SecurityLibrary.getLoggedInUserTenantId());

		if (envelope.getLeadEvaluater() != null) {
			envelope.setLeadEvaluater(envelope.getLeadEvaluater().createStripCopy());
		}
		if (envelope.getOpener() != null) {
			envelope.setOpener(envelope.getOpener().createStripCopy());
		}

		if (CollectionUtil.isNotEmpty(envelope.getAssignedEvaluators())) {
			for (RfiEvaluatorUser evalUser : assignedEvaluators) {
				evalUser.setUser(evalUser.getUser().createStripCopy());
			}
		}

		if (CollectionUtil.isNotEmpty(envelope.getAssignedOpeners())) {
			for (RfiEnvelopeOpenerUser openerUser : assignedOpeners) {
				openerUser.setUser(openerUser.getUser().createStripCopy());
			}
		}

		// for (User user : userList) {
		// boolean found = false;
		// for (RfiEvaluatorUser evalUser : assignedEvaluators) {
		// if (evalUser.getUser().getId().equals(user.getId())) {
		// found = true;
		// break;
		// }
		// }
		// if (!found) {
		// evaluators.add(user.createStripCopy());
		// }
		// }

		for (RfiEvaluatorUser evalUser : assignedEvaluators) {
			User u = new User(evalUser.getUser().getId(), evalUser.getUser().getLoginId(), evalUser.getUser().getName(), evalUser.getUser().getCommunicationEmail(), evalUser.getUser().getEmailNotifications(),evalUser.getUser().getTenantId(), evalUser.getUser().isDeleted());
			if (!evaluators.contains(u)) {
				evaluators.add(u);
			}
		}

		for (RfiEnvelopeOpenerUser openerUser : assignedOpeners) {
			User u = new User(openerUser.getUser().getId(), openerUser.getUser().getLoginId(), openerUser.getUser().getName(), openerUser.getUser().getCommunicationEmail(), openerUser.getUser().getEmailNotifications(), openerUser.getUser().getTenantId(), openerUser.getUser().isDeleted());
			if (!openersList.contains(u)) {
				openersList.add(u);
			}
		}
		List<UserPojo> userListSumm = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", UserType.NORMAL_USER);
		for (UserPojo user : userListSumm) {
			User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(), user.isEmailNotifications(), user.getTenantId(), user.isDeleted());
			if (!evaluators.contains(u)) {
				evaluators.add(u);
			}
			if (!openersList.contains(u)) {
				openersList.add(u);
			}
		}

		envelope.setEvaluators(evaluators);
		envelope.setOpeners(openersList);

		return new ResponseEntity<EnvelopePojo>(envelope, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/updateEvaluators", method = RequestMethod.POST)
	public String summaryEnvelop(@ModelAttribute(name = "envelop") RfiEnvelop envelop, @RequestParam("eventId") String eventId, Model model, RedirectAttributes redir) {
		try {
			RfiEvent event = rfiEventService.loadRfiEventForSummeryById(eventId);
			EventPermissions eventPermissions = rfiEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
			if (envelop.getEnvelopType() == EnvelopType.CLOSED && CollectionUtil.isEmpty(envelop.getOpenerUsers())) {
				model.addAttribute("error", "Please add envelop opener for closed Envelope");
				buildModel(event.getId(), model, eventPermissions);
				return "eventSummary";
			}

			if (event.getEventStart().compareTo(new Date()) > 0 && EventStatus.ACTIVE == event.getStatus()) {
				model.addAttribute("error", "Change of envelope openers not allowed");
				buildModel(event.getId(), model, eventPermissions);
				return "eventSummary";
			}

			rfiEnvelopService.updateEnvelope(envelop, eventId);
			redir.addAttribute("success", "Envelope updated successfully");
		} catch (Exception e) {
			LOG.error("Error during envelope save : " + e.getMessage(), e);
			// model.addAttribute("error", "Error during envelope saving : " + e.getMessage());
			model.addAttribute("error", messageSource.getMessage("rfasummary.during.envelope.saving", new Object[] { e.getMessage() }, Global.LOCALE));
			EventPermissions eventPermissions = eventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
			RfiEvent event = buildModel(eventId, model, eventPermissions);
			if (event.getStatus() == EventStatus.DRAFT || event.getStatus() == EventStatus.SUSPENDED) {
				return "eventSummary";
			} else {
				model.addAttribute("showSummaryTab", true);
				return "viewSummary";
			}
		}
		return "redirect:eventSummary/" + eventId;
	}

	@RequestMapping(path = "/summaryMeeting", method = RequestMethod.POST)
	public ResponseEntity<RfiEventMeeting> summaryMeeting(@RequestParam("meetingId") String meetingId) {
		HttpHeaders headers = new HttpHeaders();
		LOG.info("meetingId :" + meetingId);
		RfiEventMeeting meeting = rfiMeetingService.getRfiMeetingById(meetingId);
		meeting.setCreatedBy(null);
		meeting.setModifiedBy(null);
		LOG.info("meeting.getId() :" + meeting.getId());
		return new ResponseEntity<RfiEventMeeting>(meeting, headers, HttpStatus.OK);
	}

	/**
	 * Generate Evaluation Summary PDF report
	 * 
	 * @param eventId
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(path = "/evaluationSummary/{eventId}", method = RequestMethod.POST)
	public void evaluationSummaryReport(@PathVariable("eventId") String eventId, HttpServletResponse response, HttpSession session) throws Exception {
		JRSwapFileVirtualizer virtualizer = null;
		try {
			// Virtualizar - To increase the performance
			virtualizer = new JRSwapFileVirtualizer(200, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);

			String filename = "EvaluationSummary.pdf";
			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}
			JasperPrint jasperPrint = rfiEventService.generatePdfforEvaluationSummary(eventId, timeZone, virtualizer);
			if (jasperPrint != null) {
				streamReport(jasperPrint, filename, response);
			}
			try {
				RfiEventAudit audit = new RfiEventAudit();
				audit.setActionBy(SecurityLibrary.getLoggedInUser());
				audit.setActionDate(new Date());
				audit.setDescription("Evaluation Summary report  downloaded ");
				RfiEvent event = new RfiEvent();
				event.setId(eventId);
				audit.setEvent(event);
				audit.setAction(AuditActionType.Download);
				eventAuditService.save(audit);

				RfiEvent rfiEvent = eventService.getPlainEventById(eventId);
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Evaluation Summary Report is downloaded for Event ’" + rfiEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFI);
				buyerAuditTrailDao.save(buyerAuditTrail);

			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}

		} catch (Exception e) {
			LOG.error("Could not generate Evaluation Summary Report. " + e.getMessage(), e);
		} finally {
			if (virtualizer != null) {
				virtualizer.cleanup();
			}
		}
	}

	@RequestMapping(path = "/downloadEvaluationSummary/{eventId}", method = RequestMethod.POST)
	public void downloadEvaluationSummary(@PathVariable("eventId") String eventId, HttpServletResponse response, HttpSession session) throws Exception {
		JRSwapFileVirtualizer virtualizer = null;
		try {
			// Virtualizar - To increase the performance
			virtualizer = new JRSwapFileVirtualizer(200, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);
			String filename = "unknowEventSummary.pdf";
			RfiEvent event = rfiEventService.getRfiEventById(eventId);
			if (event.getEventId() != null) {
				filename = (event.getEventId()).replace("/", "-") + " summary.pdf";
			}
			JasperPrint jasperPrint = rfiEventService.getEvaluationSummaryPdf(event, SecurityLibrary.getLoggedInUser(), (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), virtualizer);
			if (jasperPrint != null) {
				streamReport(jasperPrint, filename, response);
			}
			try {
				RfiEventAudit audit = new RfiEventAudit();
				audit.setAction(AuditActionType.Download);
				audit.setActionBy(SecurityLibrary.getLoggedInUser());
				audit.setActionDate(new Date());
				audit.setDescription("Event Summary downloaded");
				audit.setEvent(event);
				eventAuditService.save(audit);

				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Event '" + event.getEventId() + "' Summary downloaded", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFI);
				buyerAuditTrailDao.save(buyerAuditTrail);

			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}

		} catch (Exception e) {
			LOG.error("Could not Download Evaluation Summary Report. " + e.getMessage(), e);
		} finally {
			if (virtualizer != null) {
				virtualizer.cleanup();
			}
		}
	}

	@RequestMapping(path = "/downloadEventAudit/{eventId}", method = RequestMethod.POST)
	public void downloadEventAudit(@PathVariable("eventId") String eventId, HttpServletResponse response, HttpSession session) throws Exception {
		JRSwapFileVirtualizer virtualizer = null;
		try {
			// Virtualizar - To increase the performance
			virtualizer = new JRSwapFileVirtualizer(200, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);
			String filename = "unknowEventSummary.pdf";
			RfiEvent event = rfiEventService.getRfiEventByeventId(eventId);
			if (event.getEventId() != null) {
				filename = (event.getEventId()).replace("/", "-") + " AuditHistory.pdf";
			}
			JasperPrint jasperPrint = rfiEventService.getEventAuditPdf(event, SecurityLibrary.getLoggedInUser(), (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), virtualizer);
			if (jasperPrint != null) {
				streamReport(jasperPrint, filename, response);
			}
			try {
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Audit Trail is downloaded ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFI);
				buyerAuditTrailDao.save(buyerAuditTrail);
			} catch (Exception e) {
				LOG.info("Error to create audit trails message");
			}
		} catch (Exception e) {
			LOG.error("Could not Download Event Audit Report. " + e.getMessage(), e);
		} finally {
			if (virtualizer != null) {
				virtualizer.cleanup();
			}
		}
	}

	@RequestMapping(path = "/downloadMeetingAttendance/{eventId}/{title}/{id}", method = RequestMethod.POST)
	public void downloadMeetingAttendance(@PathVariable("eventId") String eventId, @PathVariable("title") String title, @PathVariable("id") String meetingId, HttpServletResponse response, HttpSession session) throws Exception {
		try {
			String filename = "RFI " + title + " summary.pdf";
			JasperPrint jasperPrint = rfiEventService.getMeetingAttendanceReport(eventId, meetingId, session);
			if (jasperPrint != null) {
				streamReport(jasperPrint, filename, response);
			}
		} catch (Exception e) {
			LOG.error("Could not Download Evaluation Summary Report. " + e.getMessage(), e);
		}
	}

	private void streamReport(JasperPrint jasperPrint, String filename, HttpServletResponse response) throws JRException, IOException {
		response.setContentType("application/pdf");
		response.addHeader("Content-Disposition", "attachment; filename=" + filename);

		JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
		response.getOutputStream().flush();
		response.getOutputStream().close();

	}

	@RequestMapping(path = "/copyEventTo", method = RequestMethod.POST)
	public String copyEventTo(@ModelAttribute(name = "event") RfiEvent event, @RequestParam(name = "businessUnitId", required = false) String businessUnitId, @RequestParam(name = "idRfxTemplate", required = false) String idRfxTemplate, @RequestParam(name = "selectedRfxType", required = false) RfxTypes selectedRfxType, @RequestParam(name = "auctionType", required = false) AuctionType auctionType, @RequestParam(name = "bqId", required = false) String bqId, @RequestParam(name = "concludeRemarks", required = false) String concludeRemarks, @RequestParam(name = "invitedSupp", required = false) String[] invitedSupp, Model model, RedirectAttributes redir) {
		String newEventId = "";

		try {
			newEventId = rfiEventService.createNextEvent(event, selectedRfxType, auctionType, bqId, SecurityLibrary.getLoggedInUser(), idRfxTemplate, businessUnitId, concludeRemarks, invitedSupp);
			RfiEvent oldEvent = rfiEventService.getRfiEventByeventId(event.getId());
			if (StringUtils.checkString(newEventId).length() > 0) {
				rfiEventService.updateEventPushedDate(event.getId());
				redir.addAttribute("success", "New event created successfully");
			} else {
				try {
					RfiEventAudit RfiAudit = new RfiEventAudit();
					RfiAudit.setAction(AuditActionType.Conclude);
					RfiAudit.setActionBy(SecurityLibrary.getLoggedInUser());
					RfiAudit.setActionDate(new Date());
					RfiAudit.setDescription("Event " + oldEvent.getEventName() + " 'is concluded");
					RfiAudit.setEvent(oldEvent);
					eventAuditService.save(RfiAudit);
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}
				redir.addAttribute("success", messageSource.getMessage("flashsuccess.event.concluded", new Object[] { (oldEvent.getEventName()) }, Global.LOCALE));
				return "redirect:/buyer/buyerDashboard";
			}
		} catch (ApplicationException e) {
			LOG.error("=============We are here======================================" + e.getMessage());
			redir.addFlashAttribute("templateId", idRfxTemplate);

			redir.addFlashAttribute("openModelForTemplateBu", true);
			redir.addFlashAttribute("businessUnits", businessUnitService.getPlainActiveBusinessUnitForTenant(SecurityLibrary.getLoggedInUserTenantId()));
			redir.addFlashAttribute("error", e.getMessage());
			return "redirect:eventSummary/" + event.getId();
		}

		catch (Exception e) {
			LOG.error("Error while creating new Event: " + e.getMessage(), e);
			/*
			 * model.addAttribute("error", "Error while creating new Event : " + e.getMessage()); EventPermissions
			 * eventPermissions = rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(),
			 * event.getId()); buildModel(event.getId(), model, eventPermissions);
			 */
		}
		if (selectedRfxType != null) {
			return "redirect:/buyer/" + selectedRfxType.name() + "/createEventDetails/" + newEventId;
		} else {
			return "redirect:eventSummary/" + event.getId();
		}
	}

	@RequestMapping(path = "/meetingAttendance", method = RequestMethod.POST)
	public ResponseEntity<List<SupplierMeetingAttendancePojo>> meetingAttendance(@RequestParam(value = "meetingId") String meetingId, @RequestParam(value = "eventId") String eventId, Model model) {
		List<SupplierMeetingAttendancePojo> result = null;
		LOG.info("Getting the Meeting Id  : " + meetingId);
		List<RfiSupplierMeetingAttendance> list = supplierRfiMeetingAttendanceService.getAllSupplierAttendance(meetingId, eventId);
		List<Supplier> suppliers = rfiMeetingService.getAllSuppliersByMeetId(meetingId);
		if (CollectionUtil.isNotEmpty(suppliers) && CollectionUtil.isNotEmpty(list)) {
			result = new ArrayList<SupplierMeetingAttendancePojo>();
			for (RfiSupplierMeetingAttendance atta : list) {
				SupplierMeetingAttendancePojo aa = new SupplierMeetingAttendancePojo(atta);
				aa.setEventId(eventId);
				result.add(aa);
				if (suppliers.contains(atta.getSupplier())) {
					suppliers.remove(atta.getSupplier());
				}
			}
		}
		if (CollectionUtil.isNotEmpty(suppliers)) {
			if (result == null)
				result = new ArrayList<SupplierMeetingAttendancePojo>();
			for (Supplier supplier : suppliers) {
				SupplierMeetingAttendancePojo aa = new SupplierMeetingAttendancePojo();
				aa.setCompanyName(supplier.getCompanyName());
				aa.setSupplierId(supplier.getId());
				aa.setMeetingId(meetingId);
				aa.setEventId(eventId);
				LOG.info("INVITED BUT NOT ACCEPTED : " + aa.getCompanyName());
				result.add(aa);
			}

		}
		return new ResponseEntity<List<SupplierMeetingAttendancePojo>>(result, HttpStatus.OK);
	}

	@RequestMapping(path = "/saveMeetingAttendance", method = RequestMethod.POST)
	public ResponseEntity<String> saveMeetingAttendance(@RequestBody List<SupplierMeetingAttendancePojo> meetingAttendances) throws JsonProcessingException {
		HttpHeaders header = new HttpHeaders();
		LOG.info("Size " + meetingAttendances.size());
		List<RfiSupplierMeetingAttendance> list = new ArrayList<RfiSupplierMeetingAttendance>();
		for (SupplierMeetingAttendancePojo pojo : meetingAttendances) {
			if (StringUtils.checkString(pojo.getName()).length() > 0) {
				LOG.info("Details : " + pojo.toLogString());
				RfiSupplierMeetingAttendance attendance = new RfiSupplierMeetingAttendance();
				attendance.setId(pojo.getId());
				attendance.setName(pojo.getName());
				attendance.setDesignation(pojo.getDesignation());
				attendance.setMobileNumber(pojo.getMobileNumber());
				attendance.setRemarks(pojo.getRemarks());
				attendance.setAttended(pojo.getAttended());
				attendance.setMeetingAttendanceStatus(pojo.getMeetingAttendanceStatus());

				RfiEvent event = new RfiEvent();
				event.setId(pojo.getEventId());
				attendance.setRfiEvent(event);
				RfiEventMeeting meeting = new RfiEventMeeting();
				meeting.setId(pojo.getMeetingId());
				attendance.setRfxEventMeeting(meeting);
				Supplier supplier = new Supplier();
				supplier.setId(pojo.getSupplierId());
				attendance.setSupplier(supplier);
				list.add(attendance);
			}
		}

		if (CollectionUtil.isNotEmpty(list)) {
			rfiMeetingService.saveOrUpdateAttendance(list);
			header.add("success", "Succesfully updated Attendance details");
		}
		return new ResponseEntity<String>("", header, HttpStatus.OK);
	}

	@RequestMapping(path = "/completMeeting", method = RequestMethod.POST)
	public ResponseEntity<String> completMeeting(@RequestParam String meetingId, @RequestParam String eventId) throws JsonProcessingException {
		HttpHeaders header = new HttpHeaders();
		try {
			RfiEventMeeting meeting = rfiMeetingService.getMeetingForIdAndEvent(meetingId, eventId);
			if (meeting != null) {
				meeting.setStatus(MeetingStatus.COMPLETED);
				meeting.setModifiedBy(SecurityLibrary.getLoggedInUser());
				meeting.setModifiedDate(new Date());
				rfiMeetingService.updateRfiMeeting(meeting);
			}
			header.add("success", "Meeting closed Succesfully");
		} catch (Exception e) {
			LOG.error("Error while closing meeting, " + e.getMessage(), e);
			header.add("error", "Error while closing meeting");
			return new ResponseEntity<String>("", header, HttpStatus.EXPECTATION_FAILED);
		}
		return new ResponseEntity<String>("", header, HttpStatus.OK);
	}

	@RequestMapping(value = { "/checkBusinessUnitEmpty/{eventId}/{rfxType}/{templateId}", "/checkBusinessUnitEmpty/{eventId}", "/checkBusinessUnitEmpty/{eventId}/{rfxType}" }, method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<BusinessUnit>> checkBusinessUnitEmpty(@PathVariable("eventId") String eventId, @PathVariable(value = "templateId", required = false) String templateId, @PathVariable(value = "rfxType", required = false) String rfxType) {
		HttpHeaders headers = new HttpHeaders();
		String type = "RFI";
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
				RfiEvent event = rfiEventService.getRfiEventById(eventId);
				if (event.getBusinessUnit() != null) {
					LOG.error("Bussiness unit is found");
					return new ResponseEntity<List<BusinessUnit>>(null, headers, HttpStatus.EXPECTATION_FAILED);
				}
				businessUnits = businessUnitService.getPlainActiveBusinessUnitForTenant(SecurityLibrary.getLoggedInUserTenantId());

				return new ResponseEntity<List<BusinessUnit>>(businessUnits, headers, HttpStatus.OK);
			} else {
				return new ResponseEntity<List<BusinessUnit>>(null, headers, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			LOG.error("Error checking BU : " + e.getMessage(), e);
			return new ResponseEntity<List<BusinessUnit>>(null, headers, HttpStatus.EXPECTATION_FAILED);
		}
	}

	@RequestMapping(path = "/evaluationSummaryBqCq/{eventId}/{evenvelopId}", method = RequestMethod.POST)
	public void evaluationSummaryBqCqReport(@PathVariable("eventId") String eventId, @PathVariable("evenvelopId") String evenvelopId, HttpServletResponse response) throws Exception {
		// try {
		// String filename = "EvaluationSummaryBqCq.pdf";
		// JasperPrint jasperPrint = rfiEventService.generatePdfforEvaluationSummary(eventId);
		// if (jasperPrint != null) {
		// streamReport(jasperPrint, filename, response);
		// }
		// } catch (Exception e) {
		// LOG.error("Could not generate Evaluation Summary BqCq Report. " + e.getMessage(), e);
		// }
	}

	@RequestMapping(path = "/downloadEvaluationReport/{eventId}/{evenvelopId}", method = RequestMethod.POST)
	public void BiddingReportEnglish(@PathVariable("eventId") String eventId, @PathVariable("evenvelopId") String evenvelopId, HttpServletResponse response, HttpSession session) throws Exception {
		JRSwapFileVirtualizer virtualizer = null;
		try {
			// Virtualizar - To increase the performance
			virtualizer = new JRSwapFileVirtualizer(200, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);
			String filename = "EvaluationReport.pdf";
			JasperPrint jasperPrint = rfiEventService.getEvaluationReport(eventId, evenvelopId, (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), virtualizer);
			if (jasperPrint != null) {
				streamReport(jasperPrint, filename, response);
			}
			try {
				RfiEnvelop envelope = rfiEnvelopDao.findById(evenvelopId);
				RfiEventAudit audit = new RfiEventAudit();
				audit.setAction(AuditActionType.Download);
				audit.setActionBy(SecurityLibrary.getLoggedInUser());
				audit.setActionDate(new Date());
				RfiEvent event = new RfiEvent();
				event.setId(eventId);
				audit.setEvent(event);
				audit.setDescription("Evaluation Report is downloaded for Envelope '" + envelope.getEnvelopTitle() + " '");
				eventAuditService.save(audit);

				RfiEvent rfiEvent = rfiEventService.getPlainEventById(eventId);
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Evaluation Report is downloaded for Envelop '" + envelope.getEnvelopTitle() + "' for Event '" + rfiEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFI);
				buyerAuditTrailDao.save(buyerAuditTrail);

			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}

		} catch (Exception e) {
			LOG.error("Could not BiddingReportEnglish Report. " + e.getMessage(), e);
		} finally {
			if (virtualizer != null) {
				virtualizer.cleanup();
			}
		}
	}

	@RequestMapping(path = "/envelopeEvaluationSummary/{eventId}/{envelopeId}", method = RequestMethod.POST)
	public void envelopeEvaluationSummary(@PathVariable("eventId") String eventId, @PathVariable("envelopeId") String envelopeId, HttpServletResponse response) throws Exception {
		JRSwapFileVirtualizer virtualizer = null;
		try {
			// Virtualizar - To increase the performance
			virtualizer = new JRSwapFileVirtualizer(200, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);
			String filename = "EvaluationSummary.pdf";
			JasperPrint jasperPrint = rfiEventService.generatePdfforEvaluationSummary(eventId, envelopeId, virtualizer);
			if (jasperPrint != null) {
				streamReport(jasperPrint, filename, response);
			}
		} catch (Exception e) {
			LOG.error("Could not generate Evaluation Summary Report. " + e.getMessage(), e);
		} finally {
			if (virtualizer != null) {
				virtualizer.cleanup();
			}
		}
	}

	@RequestMapping(path = "/addSupplierInEvent/{eventId}", method = RequestMethod.POST)
	public ModelAndView addSupplierInEvent(@PathVariable("eventId") String eventId, @RequestParam("supplierId") String supplierId, RfiEventSupplier eventSupplier, RedirectAttributes attributes) throws Exception {
		try {
			FavouriteSupplier favSupp = favoriteSupplierService.findFavSupplierBySuppId(StringUtils.checkString(supplierId), SecurityLibrary.getLoggedInUserTenantId());
			eventSupplier.setSupplier(favSupp.getSupplier());
			eventSupplier.setSupplierInvitedTime(new Date());
			eventSupplier.setRfxEvent(rfiEventService.getRfiEventById(eventId));
			if (!doValidate(eventSupplier)) {
				LOG.info("Not Exist.....................................................");
				eventSupplier.setSubmissionStatus(SubmissionStatusType.INVITED);
				rfiEventSupplierService.saveRfiEventSupplier(eventSupplier);
				// attributes.addFlashAttribute("success", "Event Supplier added successfully");
				attributes.addFlashAttribute("success", messageSource.getMessage("flashsuccess.eventsupplier.added", new Object[] {}, Global.LOCALE));
			} else {
				attributes.addFlashAttribute("error", messageSource.getMessage("flasherror.eventsupplier.not.added", new Object[] {}, Global.LOCALE));
			}
		} catch (Exception e) {
			LOG.error("error while add event supplier in Event" + e.getMessage(), e);
			attributes.addFlashAttribute("error", messageSource.getMessage("flasherror.while.add.supplier", new Object[] {}, Global.LOCALE));
		}
		return new ModelAndView("redirect:/buyer/RFI/viewSummary/" + eventId);
	}

	@RequestMapping(path = "/addFeeInSupplier/{eventId}", method = RequestMethod.POST)
	public ModelAndView addFeeInSupplier(@ModelAttribute("eventSupplier") RfiEventSupplier eventSupplier, @PathVariable("eventId") String eventId, HttpSession session, RedirectAttributes redir, Model model) throws Exception {
		try {
			List<RfiEventSupplier> eventsuppList = rfiEventSupplierService.getAllSuppliersByFeeEventId(eventId, eventSupplier.getId());
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			TimeZone timeZone = TimeZone.getDefault();
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}
			if (CollectionUtil.isNotEmpty(eventsuppList)) {
				for (RfiEventSupplier suppList : eventsuppList) {
					suppList.setFeePaid(Boolean.TRUE);
					suppList.setFeeReference(eventSupplier.getFeeReference());
					Date feeDateTime = null;
					if (eventSupplier.getFeePaidDate() != null && eventSupplier.getFeePaidTime() != null) {
						feeDateTime = DateUtil.combineDateTime(eventSupplier.getFeePaidDate(), eventSupplier.getFeePaidTime(), timeZone);
					}
					suppList.setFeePaidDate(feeDateTime);
					suppList = rfiEventSupplierService.saveRfiEventSupplier(suppList);

					try {
						RfiEventAudit audit = new RfiEventAudit();
						audit.setActionBy(SecurityLibrary.getLoggedInUser());
						audit.setActionDate(new Date());
						audit.setDescription("Event Participaton fee of " + suppList.getRfxEvent().getParticipationFeeCurrency().getCurrencyCode() + " " + (suppList.getRfxEvent().getParticipationFees().setScale(2, BigDecimal.ROUND_HALF_UP)) + " collected for " + suppList.getSupplierCompanyName() + ". Fee Reference: " + suppList.getFeeReference());
						audit.setAction(AuditActionType.Paid);
						audit.setSupplier(suppList.getSupplier());
						RfiEvent event = new RfiEvent();
						event.setId(eventId);
						audit.setEvent(event);
						eventAuditService.save(audit);

						// Email to All supplier admins
						List<User> users = userDao.getAllAdminUsersForSupplier(suppList.getSupplier().getId());
						if (CollectionUtil.isNotEmpty(users)) {
							for (User user : users) {
								if (StringUtils.isNotBlank(user.getCommunicationEmail())) {
									rfiEventService.sendEmailAfterParticipationFees(user.getCommunicationEmail(), "Event Participation Payment", "Event Participation Fee has been made successfully for the below", suppList.getRfxEvent(), user.getName(), suppList.getFeeReference(), (supplierSettingsService.getSupplierTimeZoneByTenantId(user.getTenantId())));
								} else {
									LOG.warn("Communication email not set");
								}
							}
						}

						// Email to event owner
						if (StringUtils.isNotBlank(suppList.getRfxEvent().getEventOwner().getCommunicationEmail())) {
							rfiEventService.sendEmailAfterParticipationFees(suppList.getRfxEvent().getEventOwner().getCommunicationEmail(), "Event Participation Payment", "Event Participation Fee has been made successfully for the below by " + suppList.getSupplierCompanyName(), suppList.getRfxEvent(), suppList.getRfxEvent().getEventOwner().getName(), suppList.getFeeReference(), (buyerSettingsService.getBuyerTimeZoneByTenantId(suppList.getRfxEvent().getEventOwner().getTenantId())));
						} else {
							LOG.warn("Communication email not set");
						}

						// Email to associate owner
						if (suppList.getRfxEvent().getTeamMembers() != null && suppList.getRfxEvent().getTeamMembers().size() > 0) {
							for (RfiTeamMember member : suppList.getRfxEvent().getTeamMembers()) {
								if (member.getTeamMemberType().equals(TeamMemberType.Associate_Owner)) {
									if (StringUtils.isNotBlank(member.getUser().getCommunicationEmail())) {
										rfiEventService.sendEmailAfterParticipationFees(member.getUser().getCommunicationEmail(), "Event Participation Payment", "Event Participation Fee has been made successfully for the below by " + suppList.getSupplierCompanyName(), suppList.getRfxEvent(), member.getUser().getName(), suppList.getFeeReference(), (buyerSettingsService.getBuyerTimeZoneByTenantId(member.getUser().getTenantId())));
									} else {
										LOG.warn("Communication email not set");
									}
								}
							}
						}

					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}

					redir.addFlashAttribute("success", "Event Fees added successfully");
					model.addAttribute("eventSupplier", suppList);
				}
			}
		} catch (Exception e) {
			LOG.error("error while add Fee Detail in supplier" + e.getMessage(), e);
			redir.addFlashAttribute("error", "Error while updating Fee Details");
		}
		return new ModelAndView("redirect:/buyer/RFI/viewSummary/" + eventId);
	}

	@RequestMapping(path = "/addDepositInSupplier/{eventId}", method = RequestMethod.POST)
	public ModelAndView addDepositInSupplier(@ModelAttribute("eventSupplier") RfiEventSupplier eventSupplier, @PathVariable("eventId") String eventId, HttpSession session, RedirectAttributes redir, Model model) throws Exception {
		try {

			List<RfiEventSupplier> eventsuppList = rfiEventSupplierService.getAllSuppliersByFeeEventId(eventId, eventSupplier.getId());
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			TimeZone timeZone = TimeZone.getDefault();
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}
			if (CollectionUtil.isNotEmpty(eventsuppList)) {
				for (RfiEventSupplier suppList : eventsuppList) {
					suppList.setDepositPaid(Boolean.TRUE);
					suppList.setDepositReference(eventSupplier.getDepositReference());
					Date depositDateTime = null;
					if (eventSupplier.getDepositPaidDate() != null && eventSupplier.getDepositPaidTime() != null) {
						depositDateTime = DateUtil.combineDateTime(eventSupplier.getDepositPaidDate(), eventSupplier.getDepositPaidTime(), timeZone);
					}
					suppList.setDepositPaidDate(depositDateTime);
					suppList = rfiEventSupplierService.saveRfiEventSupplier(suppList);
					redir.addFlashAttribute("success", "Event Deposits added successfully");
					model.addAttribute("eventSupplier", suppList);
					LOG.info("Deposit updated");
				}
			}

		} catch (Exception e) {
			LOG.error("error while add Deposit Detail in supplier" + e.getMessage(), e);
			redir.addFlashAttribute("error", "Error while updating Fee Details");
		}
		return new ModelAndView("redirect:/buyer/RFI/viewSummary/" + eventId);
	}

	@RequestMapping(path = "/downloadEvaluationConclustionReport/{eventId}", method = RequestMethod.POST)
	public void downloadEvaluationConclustionReport(@PathVariable("eventId") String eventId, HttpServletResponse response, HttpSession session) throws Exception {
		JRSwapFileVirtualizer virtualizer = null;
		try {
			// Virtualizar - To increase the performance
			virtualizer = new JRSwapFileVirtualizer(200, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);
			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}
			String filename = "EvaluationConclusionReport.pdf";
			JasperPrint jasperPrint = rfiEventService.generatePdfforEvaluationConclusion(eventId, timeZone, virtualizer);
			if (jasperPrint != null) {
				streamReport(jasperPrint, filename, response);
			}
			try {
				RfiEventAudit audit = new RfiEventAudit();
				audit.setActionBy(SecurityLibrary.getLoggedInUser());
				audit.setActionDate(new Date());
				audit.setDescription("Evaluation Conclusion Report is downloaded ");
				RfiEvent event = new RfiEvent();
				event.setId(eventId);
				audit.setEvent(event);
				audit.setAction(AuditActionType.Download);
				eventAuditService.save(audit);

				RfiEvent rfiEvent = rfiEventService.getPlainEventById(eventId);
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Evaluation Conclusion Report is downloaded for Event ‘" + rfiEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFI);
				buyerAuditTrailDao.save(buyerAuditTrail);

			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}

		} catch (Exception e) {
			LOG.error("Could not generate Evaluation Summary Report. " + e.getMessage(), e);
		} finally {
			if (virtualizer != null) {
				virtualizer.cleanup();
			}
		}
	}

	@RequestMapping(path = "/updateEventSuspApproval", method = RequestMethod.POST)
	public String updateEventSuspApproval(@ModelAttribute RfiEvent event, RedirectAttributes redir, HttpSession session) {
		JRSwapFileVirtualizer virtualizer = null;
		try {
			virtualizer = new JRSwapFileVirtualizer(200, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);

			event = eventService.updateEventSuspensionApproval(event, SecurityLibrary.getLoggedInUser());
			// suspensionApprovalService.doApproval(event, session, SecurityLibrary.getLoggedInUser(), virtualizer);
			redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.approval.updated", new Object[] {}, Global.LOCALE));
		} catch (Exception e) {
			LOG.info("Error while Updating Approval :" + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.updating.approval", new Object[] { e.getMessage() }, Global.LOCALE));
		} finally {
			if (virtualizer != null) {
				virtualizer.cleanup();
			}
		}
		return "redirect:/buyer/" + RfxTypes.RFI.name() + "/eventSummary/" + event.getId();
	}

	@RequestMapping(path = "/concludeEvent", method = RequestMethod.POST)
	public String concludeEvent(@ModelAttribute(name = "event") RfiEvent event, Model model, RedirectAttributes redir) {
		try {
			event = rfiEventService.concludeRfiEvent(event, SecurityLibrary.getLoggedInUser());
			try {
				RfiEventAudit audit = new RfiEventAudit();
				audit.setAction(AuditActionType.Conclude);
				audit.setActionBy(SecurityLibrary.getLoggedInUser());
				audit.setActionDate(new Date());
				audit.setDescription("Event " + event.getEventName() + " is concluded");
				audit.setEvent(event);
				eventAuditService.save(audit);
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}

			try {
				tatReportService.updateTatReportEventConcludeAndFinishedDateAndPaperApprovalDaysCount(event.getId(), EventStatus.FINISHED);
			} catch (Exception e) {
				LOG.error("Error updating Tat Report " + e.getMessage(), e);
			}
			
			redir.addAttribute("success", "Event Concluded Successfully");
		} catch (Exception e) {
			LOG.error("Error during save Event Conclude: " + e.getMessage(), e);
			// model.addAttribute("error", "Error during save Event Conclude : " + e.getMessage());
			model.addAttribute("error", messageSource.getMessage("rfasummary.during.save.eventconclude", new Object[] { e.getMessage() }, Global.LOCALE));
			EventPermissions eventPermissions = rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), event.getId());
			buildModel(event.getId(), model, eventPermissions);
			return "eventSummary";

		}
		return "redirect:eventSummary/" + event.getId();
	}
}

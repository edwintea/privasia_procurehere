package com.privasia.procurehere.web.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.privasia.procurehere.core.entity.RftEventSor;
import com.privasia.procurehere.core.entity.Sor;
import com.privasia.procurehere.service.RftSorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.RftCqDao;
import com.privasia.procurehere.core.dao.UserDao;
import com.privasia.procurehere.core.entity.AdditionalDocument;
import com.privasia.procurehere.core.entity.Bq;
import com.privasia.procurehere.core.entity.BusinessUnit;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.EventTeamMember;
import com.privasia.procurehere.core.entity.FavouriteSupplier;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.entity.RfiEvent;
import com.privasia.procurehere.core.entity.RfpEvent;
import com.privasia.procurehere.core.entity.RfqEvent;
import com.privasia.procurehere.core.entity.RftApprovalUser;
import com.privasia.procurehere.core.entity.RftCq;
import com.privasia.procurehere.core.entity.RftEnvelop;
import com.privasia.procurehere.core.entity.RftEnvelopeOpenerUser;
import com.privasia.procurehere.core.entity.RftEvaluatorUser;
import com.privasia.procurehere.core.entity.RftEvent;
import com.privasia.procurehere.core.entity.RftEventApproval;
import com.privasia.procurehere.core.entity.RftEventAudit;
import com.privasia.procurehere.core.entity.RftEventBq;
import com.privasia.procurehere.core.entity.RftEventDocument;
import com.privasia.procurehere.core.entity.RftEventMeeting;
import com.privasia.procurehere.core.entity.RftEventMeetingDocument;
import com.privasia.procurehere.core.entity.RftEventSupplier;
import com.privasia.procurehere.core.entity.RftEventSuspensionApproval;
import com.privasia.procurehere.core.entity.RftReminder;
import com.privasia.procurehere.core.entity.RftSupplierMeetingAttendance;
import com.privasia.procurehere.core.entity.RftSuspensionApprovalUser;
import com.privasia.procurehere.core.entity.RftTeamMember;
import com.privasia.procurehere.core.entity.RfxTemplate;
import com.privasia.procurehere.core.entity.SourcingFormRequest;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.TemplateField;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.ApprovalStatus;
import com.privasia.procurehere.core.enums.AuctionType;
import com.privasia.procurehere.core.enums.AuditActionType;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.DocumentReferenceType;
import com.privasia.procurehere.core.enums.EnvelopType;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.EventVisibilityType;
import com.privasia.procurehere.core.enums.IntervalType;
import com.privasia.procurehere.core.enums.MeetingAttendanceStatus;
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
import com.privasia.procurehere.service.CostCenterService;
import com.privasia.procurehere.service.EventAdditionalDocumentService;
import com.privasia.procurehere.service.EventAuditService;
import com.privasia.procurehere.service.EventIdSettingsService;
import com.privasia.procurehere.service.FavoriteSupplierService;
import com.privasia.procurehere.service.RftBqEvaluationCommentsService;
import com.privasia.procurehere.service.RftBqService;
import com.privasia.procurehere.service.RftCqEvaluationCommentsService;
import com.privasia.procurehere.service.RftCqService;
import com.privasia.procurehere.service.RftDocumentService;
import com.privasia.procurehere.service.RftEnvelopService;
import com.privasia.procurehere.service.RftEventMeetingService;
import com.privasia.procurehere.service.RftEventService;
import com.privasia.procurehere.service.RftMeetingService;
import com.privasia.procurehere.service.RftSupplierBqItemService;
import com.privasia.procurehere.service.RftSupplierBqService;
import com.privasia.procurehere.service.RfxTemplateService;
import com.privasia.procurehere.service.SourcingFormRequestService;
import com.privasia.procurehere.service.SupplierRftMeetingAttendanceService;
import com.privasia.procurehere.service.UserService;
import com.privasia.procurehere.service.impl.SnapShotAuditService;
import com.privasia.procurehere.web.editors.RftApprovalEditor;
import com.privasia.procurehere.web.editors.RftEnvelopeOpenerUserEditor;
import com.privasia.procurehere.web.editors.RftSuspensionApprovalEditor;
import com.privasia.procurehere.web.editors.SupplierEditor;
import com.privasia.procurehere.web.editors.UserEditor;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;
import net.sf.jasperreports.engine.util.JRSwapFile;

/**
 * @author Parveen
 * @author RT-Kapil
 */

@Controller
@RequestMapping("/buyer/RFT")
public class RftSummaryController extends EventSummaryBase {

	@Autowired
	RftEventService rftEventService;

	@Autowired
	BusinessUnitService businessUnitService;

	@Autowired
	BuyerAddressService buyerAddressService;

	@Autowired
	RftEnvelopService rftEnvelopService;

	@Autowired
	RftDocumentService rftDocumentService;

	@Autowired
	RftCqService rftCqService;

	@Autowired
	RftBqService rftBqService;

	@Autowired
	RftMeetingService rftMeetingService;

	@Autowired
	UserService userService;

	@Autowired
	RftCqDao rftCqDao;

	@Autowired
	FavoriteSupplierService favoriteSupplierService;

	@Autowired
	SupplierEditor supplierEditor;

	@Autowired
	EventAuditService eventAuditService;

	@Autowired
	RftApprovalEditor rftApprovalEditor;

	@Autowired
	CostCenterService costCenterService;

	@Autowired
	UserEditor userEditor;

	@Autowired
	RftSupplierBqItemService rftSupplierBqItemService;

	@Autowired
	RftSupplierBqService rftSupplierBqService;

	@Autowired
	ApplicationContext applicationContext;

	@Autowired
	RftCqEvaluationCommentsService cqEvaluationCommentsService;

	@Autowired
	RftBqEvaluationCommentsService rftBqEvaluationCommentsService;

	@Autowired
	SupplierRftMeetingAttendanceService supplierRftMeetingAttendanceService;

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
	EventAdditionalDocumentService eventAdditionalDocumentService;

	@Autowired
	RftEventMeetingService rftEventMeetingService;

	@Autowired
	RftEnvelopeOpenerUserEditor envelopeOpenerUserEditor;

	@Autowired
	UserDao userDao;

	@Autowired
	RftSuspensionApprovalEditor rftSuspensionApprovalEditor;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Autowired
	RftSorService rftSorService;

	public RftSummaryController() {
		super(RfxTypes.RFT);
	}

	@InitBinder
	public void initBinder(ServletRequestDataBinder binder, HttpSession session) {
		binder.registerCustomEditor(Supplier.class, supplierEditor);
		StringTrimmerEditor stringtrimmer = new StringTrimmerEditor(true);
		binder.registerCustomEditor(String.class, stringtrimmer);
		binder.registerCustomEditor(RftApprovalUser.class, rftApprovalEditor);
		binder.registerCustomEditor(RftSuspensionApprovalUser.class, rftSuspensionApprovalEditor);
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
		binder.registerCustomEditor(RftEnvelopeOpenerUser.class, envelopeOpenerUserEditor);
	}

	@RequestMapping(path = "/eventSummary/{eventId}", method = RequestMethod.GET)
	public ModelAndView summaryRftEvent(@PathVariable String eventId, Model model, RedirectAttributes redir, HttpServletRequest request) {
		LOG.info("eventId " + eventId + " Summry controller called...");
		if (StringUtils.checkString(eventId).length() == 0) {
			return new ModelAndView("redirect:/400_error");
		}

		RftEvent event = null;
		try {
			boolean subscriptionExpired = super.checkSubscriptionPackageUserBased();
			model.addAttribute("subscriptionExpired", subscriptionExpired);
			model.addAttribute("invitedSupplier", rftEventSupplierService.getAllSubmittedSupplierByEventId(eventId));
			model.addAttribute("rfxTemplateList", rfxTemplateService.findAllActiveTemplatesByRfxTypeForTenantId(SecurityLibrary.getLoggedInUserTenantId(), RfxTypes.RFI));
			event = rftEventService.loadRftEventForSummeryPageById(eventId);
			model.addAttribute("enableEventUserControle", Boolean.TRUE.equals(SecurityLibrary.getLoggedInUser().getBuyer().getEnableEventUserControle()));
			EventPermissions eventPermissions = rftEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);

			buildModel(event, model, eventPermissions);

			boolean isSuspensionApprovalActive = false;
			boolean showApprovalRemark = false;

			if (event.getStatus() == EventStatus.SUSPENDED && CollectionUtil.isNotEmpty(event.getSuspensionApprovals())) {
				for (RftEventSuspensionApproval app : event.getSuspensionApprovals()) {
					for (RftSuspensionApprovalUser user : app.getApprovalUsers()) {
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
				return new ModelAndView("redirect:/buyer/RFT/envelopList/" + eventId);
			} else {
				if (!eventPermissions.isUnMaskUser()) {
					model.addAttribute("showSummaryTab", true);
					return new ModelAndView("viewSummary");
				} else {
					return new ModelAndView("redirect:/buyer/RFT/envelopList/" + eventId);
				}
			}

		} catch (Exception e) {
			LOG.error("Error while loading summary page : " + e.getMessage(), e);
			return new ModelAndView("redirect:/500_error");
		}

	}

	private RftEvent buildModel(RftEvent event, Model model, EventPermissions eventPermissions) {
		model.addAttribute("event", event);
		List<RftReminder> startReminderList = null;
		List<RftReminder> endReminderList = null;
		if (CollectionUtil.isNotEmpty(event.getRftReminder())) {
			startReminderList = new ArrayList<RftReminder>();
			endReminderList = new ArrayList<RftReminder>();
			for (RftReminder reminder : event.getRftReminder()) {
				if (reminder.getStartReminder() != null && reminder.getStartReminder() == Boolean.TRUE) {
					startReminderList.add(reminder);
				} else {
					endReminderList.add(reminder);
				}
			}
		}
		model.addAttribute("startReminders", startReminderList);
		model.addAttribute("endReminders", endReminderList);
		model.addAttribute("listDocs", rftDocumentService.findAllRftEventdocsbyEventId(event.getId())); // event.getDocuments());
		List<Supplier> suppList = rftEventService.getEventSuppliers(event.getId());

		List<FeePojo> eventsuppList = rftEventSupplierService.getAllInvitedSuppliersByEventId(event.getId());
		model.addAttribute("listFavSuppliers", eventsuppList);
		model.addAttribute("supplierList", suppList);

		model.addAttribute("eventSuppliers", rftEventSupplierService.getEventQualifiedSuppliersForEvaluation(event.getId()));

		Boolean filterByIndustryCategory = rfxTemplateService.findTemplateIndustryCategoryFlagByEventId(event.getId(), getEventType());
		if (filterByIndustryCategory == null) {
			filterByIndustryCategory = Boolean.FALSE;
		}
		List<EventSupplierPojo> supplierList = favoriteSupplierService.favoriteEventSupplierPojosOfBuyerByIndusCategory(SecurityLibrary.getLoggedInUserTenantId(), filterByIndustryCategory, event.getId(), getEventType(), null);

		model.addAttribute("suppliers", supplierList);
		model.addAttribute("rfxType", RfxTypes.values());

		User unMaskedUser = rftEventDao.getUnMaskedUserNameAndMailByEventId(event.getId());
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
					RfaEvent rfaEvent = rfaEventService.getPlainEventById(event.getPreviousEventId());
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

		List<RftEventBq> bqList = event.getEventBqs(); // rftBqService.findRftBqbyEventId(event.getId());

		for (Bq bq : bqList) {
			int i = 4;
			if (StringUtils.checkString(bq.getField1Label()).length() > 0) {
				i++;
			}
			if (StringUtils.checkString(bq.getField2Label()).length() > 0) {
				i++;
			}
			if (StringUtils.checkString(bq.getField3Label()).length() > 0) {
				i++;
			}
			if (StringUtils.checkString(bq.getField4Label()).length() > 0) {
				i++;
			}
			if (StringUtils.checkString(bq.getField5Label()).length() > 0) {
				i++;
			}
			if (StringUtils.checkString(bq.getField6Label()).length() > 0) {
				i++;
			}
			if (StringUtils.checkString(bq.getField7Label()).length() > 0) {
				i++;
			}
			if (StringUtils.checkString(bq.getField8Label()).length() > 0) {
				i++;
			}
			if (StringUtils.checkString(bq.getField9Label()).length() > 0) {
				i++;
			}
			if (StringUtils.checkString(bq.getField10Label()).length() > 0) {
				i++;
			}

			bq.setHeaderCount(i);
		}

		model.addAttribute("bqList", bqList);

		List<RftEventSor> sorList = event.getEventSors();

		for (Sor bq : sorList) {
			int i = 4;
			if (StringUtils.checkString(bq.getField1Label()).length() > 0) {
				i++;
			}

			bq.setHeaderCount(i);
		}

		model.addAttribute("sorList", sorList);

		model.addAttribute("awardedSupplierList", rftEventService.getAwardedSuppliers(event.getId()));
		model.addAttribute("eventAudit", eventAuditService.getRftEventAudit(event.getId()));

		List<RftEventMeeting> meetingList = rftMeetingService.getAllRftMeetingByEventId(event.getId());
		model.addAttribute("meetingList", meetingList); // event.getMeetings());
		model.addAttribute("intervalType", IntervalType.values());
		model.addAttribute("cqList", rftCqService.findRftCqForEvent(event.getId()));
		model.addAttribute("envelopList", rftEnvelopService.getAllRftEnvelopByEventId(event.getId(), SecurityLibrary.getLoggedInUser()));
		model.addAttribute("envelopListCount", rftEnvelopService.getAllEnvelopCountByEventId(event.getId()));
		model.addAttribute("comments", event.getComment());
		model.addAttribute("suspComments", event.getSuspensionComment());
		model.addAttribute("eventTimeline", rftEventService.getRftEventTimeline(event.getId()));
		model.addAttribute("eventContactsList", rftEventService.getAllContactForEvent(event.getId()));
		model.addAttribute("eventPermissions", eventPermissions);
		model.addAttribute("awardComments", event.getAwardComment());
		// List<User> userListSumm =
		// userService.fetchAllActiveUsersForEnvelopForTenant(SecurityLibrary.getLoggedInUserTenantId());
		// List<User> userListSumm =
		// userService.fetchAllActiveNormalUsersForEnvelopForTenant(SecurityLibrary.getLoggedInUserTenantId());
		// model.addAttribute("openers", userListSumm);
		// model.addAttribute("evaluationOwner", userListSumm);

		List<UserPojo> userListSumm = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", UserType.NORMAL_USER);
		model.addAttribute("openers", userListSumm);
		model.addAttribute("evaluationOwner", userListSumm);

		model.addAttribute("envelop", new RftEnvelop());
		List<User> assignedTeamMembers = new ArrayList<>();

		List<EventTeamMember> listTeamMembers = rftEventService.getTeamMembersForEvent(event.getId());
		for (EventTeamMember rftTeamMember : listTeamMembers) {
			try {
				assignedTeamMembers.add((User) rftTeamMember.getUser().clone());
			} catch (Exception e) {
				LOG.info("Error while fetching Team Members");
			}
		}

		List<User> userTeamMemberList = new ArrayList<User>();

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

		// Already added to model above - scroll and check
		// model.addAttribute("eventPermissions",
		// rftEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), event.getId()));

		List<RftApprovalUser> rftApprovalUserList = new ArrayList<RftApprovalUser>();
		for (User user : userTeamMemberList) {
			rftApprovalUserList.add(new RftApprovalUser(user));
		}
		model.addAttribute("userList", rftApprovalUserList);
		// List<User> userList = userService.fetchAllActiveUsersForTenant(SecurityLibrary.getLoggedInUserTenantId());

		List<User> userAppList = new ArrayList<User>();
		if (CollectionUtil.isNotEmpty(event.getApprovals())) {
			for (RftEventApproval approvalLevel : event.getApprovals()) {
				approvalLevel.getLevel();
				if (CollectionUtil.isNotEmpty(approvalLevel.getApprovalUsers())) {
					for (RftApprovalUser user : approvalLevel.getApprovalUsers()) {
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

		return event;
	}

	@RequestMapping(path = "/viewSummary/{eventId}", method = RequestMethod.GET)
	public ModelAndView viewSummary(@PathVariable String eventId, Model model, HttpServletRequest request, RedirectAttributes redir) {
		if (StringUtils.checkString(eventId).length() == 0) {
			return new ModelAndView("redirect:/400_error");
		}
		model.addAttribute("ownerSettings", ownerSettingsService.getOwnersettings());
		boolean subscriptionExpired = super.checkSubscriptionPackageUserBased();
		model.addAttribute("subscriptionExpired", subscriptionExpired);

		RftEvent event = rftEventService.loadRftEventForSummeryPageById(eventId);
		EventPermissions eventPermissions = rftEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
		buildModel(event, model, eventPermissions);

		boolean isSuspensionApprovalActive = false;
		boolean showApprovalRemark = false;

		if (event.getStatus() == EventStatus.SUSPENDED && CollectionUtil.isNotEmpty(event.getSuspensionApprovals())) {
			for (RftEventSuspensionApproval app : event.getSuspensionApprovals()) {
				for (RftSuspensionApprovalUser user : app.getApprovalUsers()) {
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
		model.addAttribute("enableEventUserControle", Boolean.TRUE.equals(SecurityLibrary.getLoggedInUser().getBuyer().getEnableEventUserControle()));
		if (event.getStatus() == EventStatus.DRAFT && (eventPermissions.isOwner() || eventPermissions.isViewer() || eventPermissions.isEditor())) {
			return new ModelAndView("eventSummary");
		}
/*		if (!checkPermissionToAllow(eventPermissions)) {
			redir.addFlashAttribute("requestedUrl", request.getRequestURL());
			return new ModelAndView("redirect:/403_error");
		}*/
		model.addAttribute("invitedSupplier", rftEventSupplierService.getAllSubmittedSupplierByEventId(eventId));
		model.addAttribute("showSummaryTab", true);
		return new ModelAndView("viewSummary");
	}

	/**
	 * @param eventPermissions
	 * @return
	 */
	private boolean checkPermissionToAllow(EventPermissions eventPermissions) {
		boolean allow = false;
		if (eventPermissions.isOwner() || //
				eventPermissions.isEditor() || //
				eventPermissions.isApprover() || //
				eventPermissions.isEvaluator() || //
				eventPermissions.isLeadEvaluator() || //
				eventPermissions.isOpener() || eventPermissions.isViewer() || //
				eventPermissions.isConclusionUser()) {
			allow = true;
		}
		if (SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY")) {
			allow = true;
		}

		return allow;
	}

	@RequestMapping(value = "/summeryPrevious", method = RequestMethod.POST)
	public String summeryPrevious(@ModelAttribute RftEvent rftEvent, Model model) {
		rftEvent = rftEventService.getRftEventById(rftEvent.getId());
		if (rftEvent != null) {
			model.addAttribute("eventPermissions", rftEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), rftEvent.getId()));
			return "redirect:envelopList/" + rftEvent.getId();
		} else {
			LOG.error("Event not found redirecting to login ");
			return "redirect:/login";
		}
	}

	@RequestMapping(value = "finishEvent", method = RequestMethod.POST)
	public ModelAndView finishEvent(@ModelAttribute("event") RftEvent rftEvent, Model model, RedirectAttributes redir, HttpSession session) {
		LOG.info("Finish Event create : " + rftEvent.getId());
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

			if (StringUtils.checkString(rftEvent.getId()).length() > 0) {
				EventPermissions eventPermissions = rftEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), rftEvent.getId());

				LOG.info("--------------->>>>>>>>>>>>> " + rftEvent.getEnableApprovalReminder());
				if (Boolean.TRUE == rftEvent.getEnableApprovalReminder()) {
					if (rftEvent.getReminderAfterHour() == null) {
						model.addAttribute("error", messageSource.getMessage("approval.reminder.add.hour", new Object[] {}, Global.LOCALE));
						RftEvent persistObj = rftEventService.loadRftEventForSummeryPageById(rftEvent.getId());
						buildModel(persistObj, model, eventPermissions);
						return new ModelAndView("eventSummary");
					}
					if (rftEvent.getReminderCount() == null) {
						model.addAttribute("error", messageSource.getMessage("approval.reminder.count.reminder", new Object[] {}, Global.LOCALE));
						RftEvent persistObj = rftEventService.loadRftEventForSummeryPageById(rftEvent.getId());
						buildModel(persistObj, model, eventPermissions);
						return new ModelAndView("eventSummary");
					}
				}
				
				// implement validation to check if at least one Envelope exists
				Integer envelopCount = rftEventService.getCountOfEnvelopByEventId(rftEvent.getId());
				if (envelopCount == 0) {
					model.addAttribute("error", messageSource.getMessage("summary.envelope.not.exists", new Object[] { rftEvent.getEventName() }, Global.LOCALE));
					RftEvent persistObj = rftEventService.loadRftEventForSummeryPageById(rftEvent.getId());
					buildModel(persistObj, model, eventPermissions);
					return new ModelAndView("eventSummary");
				}
				// Validate if all BQ/CQ have been assigned envelope
				List<RftEventBq> notAssignBqs = rftBqService.getNotAssignedRftBqIdsByEventId(rftEvent.getId());
				if (CollectionUtil.isNotEmpty(notAssignBqs)) {
					List<String> bqNames = new ArrayList<String>();
					for (RftEventBq rftEventBq : notAssignBqs) {
						bqNames.add(rftEventBq.getName());
					}
					model.addAttribute("error", messageSource.getMessage("summary.bq.not.assigned", new Object[] { bqNames }, Global.LOCALE));
					RftEvent persistObj = rftEventService.loadRftEventForSummeryPageById(rftEvent.getId());
					buildModel(persistObj, model, eventPermissions);
					return new ModelAndView("eventSummary");
				}

				// Validate Envelope SORS
				List<RftEventSor> notAssignSors = rftSorService.getNotAssignedSorIdsByEventId(rftEvent.getId());
				if (CollectionUtil.isNotEmpty(notAssignSors)) {
					List<String> sorNames = new ArrayList<String>();
					for (RftEventSor rftEventSor : notAssignSors) {
						sorNames.add(rftEventSor.getName());
					}
					RftEvent persistObj = rftEventService.loadRftEventForSummeryPageById(rftEvent.getId());
					buildModel(persistObj, model, eventPermissions);
					model.addAttribute("error", messageSource.getMessage("summary.sor.not.assigned", new Object[] { sorNames }, Global.LOCALE));
					return new ModelAndView("eventSummary");
				}

				// Validate Envelop Cq
				List<RftCq> notAssignCqs = rftCqService.getNotAssignedRftCqIdsByEventId(rftEvent.getId());
				if (CollectionUtil.isNotEmpty(notAssignCqs)) {
					List<String> cqNames = new ArrayList<String>();
					for (RftCq rftEventCq : notAssignCqs) {
						cqNames.add(rftEventCq.getName());
					}
					model.addAttribute("error", messageSource.getMessage("summary.cq.not.assigned", new Object[] { org.apache.commons.lang.StringUtils.join(cqNames, ",") }, Global.LOCALE));
					RftEvent persistObj = rftEventService.loadRftEventForSummeryPageById(rftEvent.getId());
					buildModel(persistObj, model, eventPermissions);
					return new ModelAndView("eventSummary");
				}

				// check if empty envelop present
				RftEnvelop envelop = rftEnvelopService.getEmptyEnvelopByEventId(rftEvent.getId());
				if (envelop != null && envelop.getEnvelopEmpty() == 0) {
					model.addAttribute("error", messageSource.getMessage("empty.envelop.finish", new Object[] { envelop.getEnvelopTitle() }, Global.LOCALE));
					RftEvent persistObj = rftEventService.loadRftEventForSummeryPageById(rftEvent.getId());
					buildModel(persistObj, model, eventPermissions);
					return new ModelAndView("eventSummary");
				}

				// Validate Bq items
				List<String> notSectionAddedBqs = rftBqService.getNotSectionAddedRftBqIdsByEventId(rftEvent.getId());
				if (CollectionUtil.isNotEmpty(notSectionAddedBqs)) {
					String names = org.apache.commons.lang.StringUtils.join(notSectionAddedBqs, ",");
					model.addAttribute("error", messageSource.getMessage("summary.bq.not.sectionAdded", new Object[] { names }, Global.LOCALE));
					RftEvent persistObj = rftEventService.loadRftEventForSummeryPageById(rftEvent.getId());
					buildModel(persistObj, model, eventPermissions);
					return new ModelAndView("eventSummary");
				}

				// Validate Bq items inside section
				List<String> notItemSectionAddedBqs = rftBqService.getNotSectionItemAddedRftBqIdsByEventId(rftEvent.getId());
				if (CollectionUtil.isNotEmpty(notItemSectionAddedBqs)) {
					String names = org.apache.commons.lang.StringUtils.join(notItemSectionAddedBqs, ",");
					model.addAttribute("error", messageSource.getMessage("summary.bq.item.not.sectionAdded", new Object[] { names }, Global.LOCALE));
					RftEvent persistObj = rftEventService.loadRftEventForSummeryPageById(rftEvent.getId());
					buildModel(persistObj, model, eventPermissions);
					return new ModelAndView("eventSummary");
				}

				if(rftEvent.getScheduleOfRate() == Boolean.TRUE) {
					Integer totalSor = rftSorService.getCountOfSorByEventId(rftEvent.getId());
					if(totalSor == null || totalSor == 0) {
						model.addAttribute("error", messageSource.getMessage("summary.sor.nosor", new Object[] {}, Global.LOCALE));
						RftEvent persistObj = rftEventService.loadRftEventForSummeryPageById(rftEvent.getId());
						buildModel(persistObj, model, eventPermissions);
						return new ModelAndView("eventSummary");
					}
				}

				// Validate Sor items
				List<String> notSectionAddedSors = rftSorService.getNotSectionAddedRftSorIdsByEventId(rftEvent.getId());
				if (CollectionUtil.isNotEmpty(notSectionAddedSors)) {
					String names = org.apache.commons.lang.StringUtils.join(notSectionAddedSors, ",");
					model.addAttribute("error", messageSource.getMessage("summary.sor.not.sectionAdded", new Object[] { names }, Global.LOCALE));
					RftEvent persistObj = rftEventService.loadRftEventForSummeryPageById(rftEvent.getId());
					buildModel(persistObj, model, eventPermissions);
					return new ModelAndView("eventSummary");
				}

				// Validate Sor items inside section
				List<String> notItemSectionAddedSors = rftSorService.getNotSectionItemAddedRftSorIdsByEventId(rftEvent.getId());
				if (CollectionUtil.isNotEmpty(notItemSectionAddedSors)) {
					String names = org.apache.commons.lang.StringUtils.join(notItemSectionAddedSors, ",");
					model.addAttribute("error", messageSource.getMessage("summary.sor.not.sectionAdded", new Object[] { names }, Global.LOCALE));
					RftEvent persistObj = rftEventService.loadRftEventForSummeryPageById(rftEvent.getId());
					buildModel(persistObj, model, eventPermissions);
					return new ModelAndView("eventSummary");
				}

				// Validate Cq items
				List<String> notSectionAddedCqs = rftCqService.getNotSectionAddedRftCqIdsByEventId(rftEvent.getId());
				if (CollectionUtil.isNotEmpty(notSectionAddedCqs)) {
					String names = org.apache.commons.lang.StringUtils.join(notSectionAddedCqs, ",");
					model.addAttribute("error", messageSource.getMessage("summary.cq.not.sectionAdded", new Object[] { names }, Global.LOCALE));
					RftEvent persistObj = rftEventService.loadRftEventForSummeryPageById(rftEvent.getId());
					buildModel(persistObj, model, eventPermissions);
					return new ModelAndView("eventSummary");
				}
				// Validate Cq items inside section
				List<String> notItemSectionAddedCqs = rftCqService.getNotSectionItemAddedRftCqIdsByEventId(rftEvent.getId());
				if (CollectionUtil.isNotEmpty(notItemSectionAddedCqs)) {
					String names = org.apache.commons.lang.StringUtils.join(notItemSectionAddedCqs, ",");
					model.addAttribute("error", messageSource.getMessage("summary.cq.item.not.sectionAdded", new Object[] { names }, Global.LOCALE));
					RftEvent persistObj = rftEventService.loadRftEventForSummeryPageById(rftEvent.getId());
					buildModel(persistObj, model, eventPermissions);
					return new ModelAndView("eventSummary");
				}

				// Validate Supplier
				if (rftEvent.getEventVisibility() == EventVisibilityType.PRIVATE) {
					long supplierList = rftEventService.getEventSuppliersCount(rftEvent.getId());
					if (supplierList == 0) {
						LOG.info("supplierList:" + supplierList);
						model.addAttribute("error", messageSource.getMessage("summary.not.supplierAdded", new Object[] { null }, Global.LOCALE));
						RftEvent persistObj = rftEventService.loadRftEventForSummeryPageById(rftEvent.getId());
						buildModel(persistObj, model, eventPermissions);
						return new ModelAndView("eventSummary");
					}
				}

				RftEvent persistObj = rftEventService.getPlainEventById(rftEvent.getId());

				// validate Decimal and Base Currency
				if (persistObj.getDecimal() == null) {
					model.addAttribute("error", messageSource.getMessage("summary.decimal.not.exists", new Object[] { rftEvent.getEventName() }, Global.LOCALE));
					RftEvent event = rftEventService.loadRftEventForSummeryPageById(rftEvent.getId());
					buildModel(event, model, eventPermissions);
					return new ModelAndView("eventSummary");
				}

				if (persistObj.getBaseCurrency() == null) {
					model.addAttribute("error", messageSource.getMessage("summary.baseCurrency.not.exists", new Object[] { rftEvent.getEventName() }, Global.LOCALE));
					RftEvent event = rftEventService.loadRftEventForSummeryPageById(rftEvent.getId());
					buildModel(event, model, eventPermissions);
					return new ModelAndView("eventSummary");
				}

				if (!persistObj.getViewSupplerName() && CollectionUtil.isEmpty(persistObj.getUnMaskedUsers())) {
					model.addAttribute("error", messageSource.getMessage("unmask.user", new Object[] {}, Global.LOCALE));
					RftEvent event = rftEventService.loadRftEventForSummeryPageById(rftEvent.getId());
					buildModel(event, model, eventPermissions);
					return new ModelAndView("eventSummary");

				}

				if (persistObj.getEventEnd() != null && persistObj.getEventEnd().before(new Date())) {
					LOG.info("EVENT endDate:" + persistObj.getEventEnd());
					model.addAttribute("error", messageSource.getMessage("rftEvent.error.end", new Object[] { dateTimeFormat.format(persistObj.getEventEnd()) }, Global.LOCALE));
					RftEvent event = rftEventService.loadRftEventForSummeryPageById(rftEvent.getId());
					buildModel(event, model, eventPermissions);
					return new ModelAndView("eventSummary");
				}
				if (persistObj.getEventStart() != null && persistObj.getEventEnd() != null && persistObj.getEventEnd().before(persistObj.getEventStart())) {
					LOG.info("EVENT endDate:" + persistObj.getEventEnd());
					model.addAttribute("error", messageSource.getMessage("rftEvent.error.enddate", new Object[] { dateTimeFormat.format(persistObj.getEventEnd()) }, Global.LOCALE));
					RftEvent event = rftEventService.loadRftEventForSummeryPageById(rftEvent.getId());
					buildModel(event, model, eventPermissions);
					return new ModelAndView("eventSummary");
				}
				
				if (persistObj.getEventPublishDate() != null && persistObj.getEventStart() != null && persistObj.getEventPublishDate().after(persistObj.getEventStart())) {
					model.addAttribute("error", messageSource.getMessage("rftEvent.error.publishdate", new Object[] { dateTimeFormat.format(persistObj.getEventPublishDate()) }, Global.LOCALE));
					RftEvent event = rftEventService.loadRftEventForSummeryPageById(rftEvent.getId());
					buildModel(event, model, eventPermissions);
					return new ModelAndView("eventSummary");
				}
				

				if (!validateMeetings(model, dateTimeFormat, persistObj, redir)) {
					return new ModelAndView("redirect:eventSummary/" + rftEvent.getId());
				}

				doValidateOwnerUser(persistObj.getId());

				if (persistObj.getStatus() == EventStatus.SUSPENDED) {
					// persistObj = suspensionApprovalService.doApproval(persistObj, session,
					// SecurityLibrary.getLoggedInUser(), virtualizer);
					RftEvent rftEventOBJ = rftEventService.loadRftEventForSummeryPageById(rftEvent.getId());

					// if(!persistObj.getEnableSuspensionApproval()) {
					if ((Boolean.TRUE == persistObj.getEnableSuspensionApproval() && CollectionUtil.isEmpty(rftEventOBJ.getSuspensionApprovals())) || Boolean.FALSE == persistObj.getEnableSuspensionApproval()) {
						rftEventService.resumeEvent(rftEvent);
						User loggedInUser = SecurityLibrary.getLoggedInUser();
						LOG.info("--------------BEFORE AUDIT------------------");
						snapShotAuditService.doRftAudit(rftEvent, session, persistObj, loggedInUser, AuditActionType.Resume, "event.audit.resume", virtualizer);
						LOG.info("--------------AFTER AUDIT------------------");
						try {
							LOG.info("publishing rft suspended event to epiportal");
							publishEventService.pushRftEvent(persistObj.getId(), SecurityLibrary.getLoggedInUser().getBuyer().getId(), false);
						} catch (Exception e) {
							LOG.error("Error while publishing RFT event to EPortal:" + e.getMessage(), e);
						}
						try {
							LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
							BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.RESUME, "Event '" + persistObj.getEventId() + "' Resumed", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFT);
							buyerAuditTrailDao.save(buyerAuditTrail);
							LOG.info("--------------------------AFTER AUDIT---------------------------------------");
						} catch (Exception e) {
							LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
						}
						try {
							// Send notification to suppliers

							try {
								// jmsTemplate.setDefaultDestinationName("QUEUE.EVENT.ADDI.SUPPLIER.NOTIFICATION");
								jmsTemplate.send("QUEUE.EVENT.ADDI.SUPPLIER.NOTIFICATION", new MessageCreator() {
									@Override
									public Message createMessage(Session session) throws JMSException {
										TextMessage objectMessage = session.createTextMessage();
										objectMessage.setText(rftEvent.getId());
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
							LOG.info("Inside with the finish method");
							if (persistObj.getSuspensionType() == SuspensionType.DELETE_NOTIFY || persistObj.getSuspensionType() == SuspensionType.KEEP_NOTIFY) {

								try {
									// jmsTemplate.setDefaultDestinationName("QUEUE.EVENT.RESUMED.NOTIFICATION");
									jmsTemplate.send("QUEUE.EVENT.RESUMED.NOTIFICATION", new MessageCreator() {

										@Override
										public Message createMessage(Session session) throws JMSException {
											TextMessage objectMessage = session.createTextMessage();
											objectMessage.setText(rftEvent.getId());
											return objectMessage;
										}
									});
								} catch (Exception e) {
									LOG.error("Error sending message to queue : " + e.getMessage(), e);
								}

								// super.sendEventResumeNotificationEmails(persistObj, getEventType());
							}
							try {
								simpMessagingTemplate.convertAndSend("/auctionTopic/" + persistObj.getId(), "RESUME");
							} catch (Exception e) {
								LOG.error("Error while sending RESUME event to MQ : " + e.getMessage(), e);
								RftEvent event = rftEventService.loadRftEventForSummeryPageById(rftEvent.getId());
								buildModel(event, model, eventPermissions);
							}
						} catch (Exception e) {
							LOG.error("Error while sending notification to event Resume " + e.getMessage(), e);
						}
						model.addAttribute("eventPermissions", eventPermissions);

						if (StringUtils.checkString(persistObj.getPreviousRequestId()).length() > 0) {
							tatReportService.updateTatReportEventStatus(persistObj.getEventId(), persistObj.getPreviousRequestId(), SecurityLibrary.getLoggedInUserTenantId(), EventStatus.ACTIVE);
						}
						redir.addFlashAttribute("success", messageSource.getMessage("event.resume.success", new Object[] { persistObj.getEventName() }, Global.LOCALE));

					} else if (Boolean.TRUE == persistObj.getEnableSuspensionApproval() && CollectionUtil.isNotEmpty(rftEventOBJ.getSuspensionApprovals())) {
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
						RftEvent event = rftEventService.loadRftEventForSummeryPageById(rftEvent.getId());

						buildModel(event, model, eventPermissions);
						return new ModelAndView("eventSummary");
					}
					if (persistObj.getEventStart() != null && persistObj.getEventStart().before(new Date()))

					{
						// Event start date cannot be in the past
						LOG.info("EVENT STARTDATE:" + persistObj.getEventStart());
						model.addAttribute("error", messageSource.getMessage("rftEvent.error.startdate", new Object[] { dateTimeFormat.format(persistObj.getEventStart()) }, Global.LOCALE));
						RftEvent event = rftEventService.loadRftEventForSummeryPageById(rftEvent.getId());
						buildModel(event, model, eventPermissions);
						return new ModelAndView("eventSummary");
					}
					List<RftReminder> reminderList = rftEventService.getAllRftEventReminderForEvent(rftEvent.getId());
					if (CollectionUtil.isNotEmpty(reminderList)) {
						for (RftReminder reminder : reminderList) {
							LOG.info("Reminder Date:" + reminder.getReminderDate());
							if (reminder.getReminderDate().compareTo(new Date()) < 0) {
								model.addAttribute("error", messageSource.getMessage("event.reminder.pastdate", new Object[] {}, Global.LOCALE));
								RftEvent event = rftEventService.loadRftEventForSummeryPageById(rftEvent.getId());
								buildModel(event, model, eventPermissions);
								return new ModelAndView("eventSummary");
							}
						}
					}

					LOG.info("-----------------------BEFORE AUDIT----------------------------------");
					User user = SecurityLibrary.getLoggedInUser();
					snapShotAuditService.doRftAudit(rftEvent, session, persistObj, user, AuditActionType.Finish, "event.audit.finished", virtualizer);
					LOG.info("-----------------------AFTER AUDIT-----------------------------------------------");
					Date eventApprovedAndFinishDate = new Date();
					persistObj = approvalService.doApproval(persistObj, session, SecurityLibrary.getLoggedInUser(), virtualizer, eventApprovedAndFinishDate);

					persistObj.setSummaryCompleted(Boolean.TRUE);
					persistObj = rftEventService.updateRftEvent(persistObj);

					try {
						LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.FINISH, "Event '" + persistObj.getEventId() + "' sent for Approval.", user.getTenantId(), user, new Date(), ModuleType.RFT);
						buyerAuditTrailDao.save(buyerAuditTrail);
						LOG.info("--------------------------AFTER AUDIT---------------------------------------");
					} catch (Exception e) {
						LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
					}

					if (!validateMeetings(model, dateTimeFormat, persistObj, redir)) {
						return new ModelAndView("redirect:eventSummary/" + rftEvent.getId());
					}

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

					model.addAttribute("eventPermissions", rftEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), persistObj.getId()));
					redir.addFlashAttribute("success", messageSource.getMessage("event.finish.success", new Object[] { persistObj.getEventName() }, Global.LOCALE));
				}
				try {
					rftEventService.insertTimeLine(rftEvent.getId());
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}
			}
		} catch (Exception e) {
			LOG.error("Error While Finish the event : :  " + e.getMessage(), e);
			// model.addAttribute("error", "Error saving event : " + e.getMessage());
			model.addAttribute("error", messageSource.getMessage("rftsummary.error.saving.event", new Object[] { e.getMessage() }, Global.LOCALE));
			RftEvent persistObj = rftEventService.loadRftEventForSummeryPageById(rftEvent.getId());
			EventPermissions eventPermissions = rftEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), rftEvent.getId());
			buildModel(persistObj, model, eventPermissions);
			return new ModelAndView("eventSummary");
		} finally {
			if (virtualizer != null) {
				virtualizer.cleanup();
			}
		}
		return new ModelAndView("redirect:/buyer/buyerDashboard");
	}

	private void doValidateOwnerUser(String eventId) throws ApplicationException {
		boolean isUserControl = Boolean.TRUE.equals(SecurityLibrary.getLoggedInUser().getBuyer().getEnableEventUserControle());
		if (isUserControl) {
			if (rftEventService.doValidateOwnerUserUnmaskUser(eventId)) {
				throw new ApplicationException(messageSource.getMessage("rftEvent.error.owner.control", new Object[] { "Supplier Masking Evaluation" }, Global.LOCALE));
			}
			if (rftEventService.doValidateOwnerUserApprover(eventId)) {
				throw new ApplicationException(messageSource.getMessage("rftEvent.error.owner.control", new Object[] { "Event Approval User" }, Global.LOCALE));
			}
			if (rftEventService.doValidateOwnerUserTeamMember(eventId)) {
				throw new ApplicationException(messageSource.getMessage("rftEvent.error.owner.control", new Object[] { "Event Team Member User" }, Global.LOCALE));
			}
			if (rftEventService.doValidateOwnerUserEnvople(eventId)) {
				throw new ApplicationException(messageSource.getMessage("rftEvent.error.owner.control", new Object[] { "Envelope Evaluation" }, Global.LOCALE));
			}
		}
	}

	private boolean validateMeetings(Model model, SimpleDateFormat dateTimeFormat, RftEvent persistObj, RedirectAttributes redir) {
		boolean valid = true;
		if (persistObj.getMeetingReq() != null && persistObj.getMeetingReq()) {
			// Event start date cannot be in the past
			List<Date> meetingDates = rftEventService.getAllMeetingDateByEventId(persistObj.getId());

			LOG.info("meeting Dates list size :" + meetingDates.size());
			if (CollectionUtil.isNotEmpty(meetingDates)) {
				for (Date meetingDate : meetingDates) {
					LOG.info("Meeting Date : " + meetingDate + " : publish Date : " + persistObj.getEventPublishDate() + " : Current Date : " + new Date());
					if ((persistObj.getEventPublishDate() != null && meetingDate != null && meetingDate.before(persistObj.getEventPublishDate())) || meetingDate.before(new Date())) {
						LOG.info("Meeting Date error : " + meetingDate + " : pubDate : " + persistObj.getEventPublishDate() + " : Current Date : " + new Date());
						redir.addAttribute("error", messageSource.getMessage("rft.meeting.error.eventpublishdate", new Object[] { dateTimeFormat.format(meetingDate), dateTimeFormat.format(persistObj.getEventPublishDate()) }, Global.LOCALE));
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
				List<RftEventMeeting> meetingList = rftEventMeetingService.findMeetingWithoutSuplliersByEventId(persistObj.getId());
				if (CollectionUtil.isNotEmpty(meetingList)) {
					LOG.info("Meeting does not have suppliers");
					List<String> meetings = new ArrayList<String>();
					for (RftEventMeeting meeting : meetingList) {
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
			RftEventDocument docs = rftDocumentService.findRftEventdocsById(id);
			super.buildDocumentFile(response, docs);
		} catch (Exception e) {
			LOG.error("Error while downloaded RFT event Document : " + e.getMessage(), e);
		}
	}

	@RequestMapping(value = "/downloadEventMeetingDocument/{id}", method = RequestMethod.GET)
	public void downloadEventMeetingDocument(@PathVariable String id, HttpServletResponse response) throws IOException {
		try {
			RftEventMeetingDocument docs = rftMeetingService.getRftEventMeetingDocument(id);
			super.buildMeetingFile(response, docs);
		} catch (Exception e) {
			LOG.error("Error while downloaded RFT event Document : " + e.getMessage(), e);
		}
	}

	@RequestMapping(value = "cancelEvent", method = RequestMethod.POST)
	public ModelAndView cancelEvent(@ModelAttribute RftEvent event, Model model, RedirectAttributes redirectAttributes, HttpSession session) {
		JRSwapFileVirtualizer virtualizer = null;
		try {
			virtualizer = new JRSwapFileVirtualizer(200, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);
			event = rftEventService.cancelEvent(event, session, virtualizer, SecurityLibrary.getLoggedInUser());

			tatReportService.updateTatReportEventStatusById(event.getId(), SecurityLibrary.getLoggedInUserTenantId(), EventStatus.CANCELED);
			redirectAttributes.addFlashAttribute("success", messageSource.getMessage("event.cancel.success", new Object[] { StringUtils.checkString(event.getEventName()).length() == 0 ? event.getEventId() : event.getEventName() }, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error while cancel RFT event  : " + e.getMessage(), e);
			model.addAttribute("error", "Error during cancel RFT event  : " + e.getMessage());
			RftEvent persistObj = rftEventService.loadRftEventForSummeryPageById(event.getId());
			EventPermissions eventPermissions = rftEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), event.getId());
			buildModel(persistObj, model, eventPermissions);
			return new ModelAndView("eventSummary");
		} finally {
			if (virtualizer != null) {
				virtualizer.cleanup();
			}
		}
		return new ModelAndView("redirect:/buyer/buyerDashboard");
	}

	@RequestMapping(path = "/suspendEvent", method = RequestMethod.POST)
	public ModelAndView suspendEvent(@RequestParam(name = "docs", required = false) MultipartFile[] file, @RequestParam(name = "suspendRemarks", required = false) String[] desc, RedirectAttributes redir, @ModelAttribute RftEvent event, RedirectAttributes redirectAttributes, Model model, HttpSession session) {

		JRSwapFileVirtualizer virtualizer = null;
		try {
			virtualizer = new JRSwapFileVirtualizer(200, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);
			rftEventService.suspendEvent(event);
			RftEvent persistObject = rftEventService.getRftEventByeventId(event.getId());
			User loggedInUser = SecurityLibrary.getLoggedInUser();

			List<RftEventSuspensionApproval> approvalList = persistObject.getSuspensionApprovals();
			if (CollectionUtil.isNotEmpty(approvalList)) {
				for (RftEventSuspensionApproval approval : approvalList) {
					approval.setDone(false);
					approval.setActive(false);
					for (RftSuspensionApprovalUser user1 : approval.getApprovalUsers()) {
						user1.setActionDate(null);
						user1.setApprovalStatus(ApprovalStatus.PENDING);
						user1.setRemarks(null);
						user1.setActionDate(null);
					}
				}
			}
			rftEventDao.update(persistObject);
			LOG.info("--------------------------------------------------------------------before audit " + file);
			try {
				snapShotAuditService.doRftAudit(event, session, persistObject, loggedInUser, AuditActionType.Suspend, "event.audit.suspended", virtualizer);
			} catch (Exception e1) {
			}
			LOG.info("---------------------------------------------------------------------after audit");
			try {
				LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.SUSPEND, "Event '" + persistObject.getEventId() + "' is suspended", persistObject.getCreatedBy().getTenantId(), persistObject.getCreatedBy(), new Date(), ModuleType.RFT);
				buyerAuditTrailDao.save(buyerAuditTrail);
				LOG.info("--------------------------AFTER AUDIT---------------------------------------");
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
			}
			saveSuspendDocuments(file, desc, redir, event);
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

			tatReportService.updateTatReportEventStatusById(event.getId(), SecurityLibrary.getLoggedInUserTenantId(), EventStatus.SUSPENDED);

			model.addAttribute("eventPermissions", rftEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), event.getId()));
			redirectAttributes.addFlashAttribute("success", messageSource.getMessage("event.suspended.success", new Object[] { event.getEventName() }, Global.LOCALE));
			try {
				simpMessagingTemplate.convertAndSend("/auctionTopic/" + event.getId(), "SUSPENDED");
			} catch (Exception e) {
				LOG.error("Error while sending suspend event to MQ : " + e.getMessage(), e);
			}

		} catch (Exception e) {
			LOG.error("Error while suspend RFT event  : " + e.getMessage(), e);
			RftEvent persistObj = rftEventService.loadRftEventForSummeryPageById(event.getId());
			// model.addAttribute("error", "Error while suspending Event");
			model.addAttribute("error", messageSource.getMessage("rfasummary.while.suspending.event", new Object[] {}, Global.LOCALE));
			EventPermissions eventPermissions = rftEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), event.getId());
			buildModel(persistObj, model, eventPermissions);
			return new ModelAndView("eventSummary");
		} finally {
			if (virtualizer != null) {
				virtualizer.cleanup();
			}
		}
		return new ModelAndView("redirect:/buyer/buyerDashboard");
	}

	private void saveSuspendDocuments(MultipartFile[] file, String[] desc, RedirectAttributes redir, RftEvent event) {

		LOG.info("===================================");

		if (file != null && file.length > 0) {
			for (int i = 0; i < file.length; i++) {
				for (int j = 0; j < desc.length; j++) {
					if (i == j) {
						if (file[i] != null) {
							if (!file[i].isEmpty()) {
								try {
									String fileName = file[i].getOriginalFilename();
									LOG.info("File Name " + fileName);
									byte[] bytes = file[i].getBytes();

									AdditionalDocument notesDoc = new AdditionalDocument();
									notesDoc.setCredContentType(file[i].getContentType());
									notesDoc.setDescription(desc[i]);
									notesDoc.setFileName(fileName);
									notesDoc.setFileData(bytes);
									notesDoc.setUploadDate(new Date());
									notesDoc.setCreatedBy(SecurityLibrary.getLoggedInUser());
									notesDoc.setDocumentReferenceType(DocumentReferenceType.SUSPENDED);
									notesDoc.setRftEvent(event);
									notesDoc.setFileSizeInKb(bytes.length > 0 ? bytes.length / 1024 : 0);
									eventAdditionalDocumentService.saveEventAdditionalDocuments(notesDoc);

								} catch (Exception e) {
									redir.addFlashAttribute("error", "Error while uploading documents");
									LOG.error("Error in suppBlackUploadDocument" + e.getMessage(), e);
								}
							}
						}
					}
				}
			}
		}
	}

	@RequestMapping(path = "/resumeEvent", method = RequestMethod.POST)
	public ModelAndView resumeEvent(@ModelAttribute RftEvent event, RedirectAttributes redirectAttributes, Model model, HttpSession session) {
		JRSwapFileVirtualizer virtualizer = null;

		try {
			// Check subscription limit
			super.checkSubscriptionPackageForUserBased();
		} catch (SubscriptionException e) {
			LOG.error(e.getMessage());
			redirectAttributes.addFlashAttribute("error", messageSource.getMessage("reached.subscription.limit", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ModelAndView("redirect:/buyer/buyerDashboard");
		} catch (Exception e) {
			LOG.error("Error While checking subscription package : " + e.getMessage(), e);
		}
		try {
			rftEventService.resumeEvent(event);
			virtualizer = new JRSwapFileVirtualizer(200, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);

			LOG.info("-----------------BEFORE AUDIT-------------------------");
			User user = SecurityLibrary.getLoggedInUser();
			snapShotAuditService.doRftAudit(event, session, event, user, AuditActionType.Resume, "event.audit.resume", virtualizer);
			LOG.info("------------------AFTER AUDIT--------------------------");
			try {
				LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.RESUME, "Event '" + event.getEventId() + "' Resumed", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFT);
				buyerAuditTrailDao.save(buyerAuditTrail);
				LOG.info("--------------------------AFTER AUDIT---------------------------------------");
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
			}
			try {
				LOG.info("publishing rft resume event to epiportal");
				publishEventService.pushRftEvent(event.getId(), SecurityLibrary.getLoggedInUser().getBuyer().getId(), false);
			} catch (Exception e) {
				LOG.error("Error while publishing RFT event to EPortal:" + e.getMessage(), e);
			}
			try {
				// Send Resume notification to suppliers
				LOG.info("inside the ANother method");
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
				simpMessagingTemplate.convertAndSend("/auctionTopic/" + event.getId(), "RESUME");
			} catch (Exception e) {
				LOG.error("Error while sending notification to event Resume " + e.getMessage(), e);
			}

			model.addAttribute("eventPermissions", rftEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), event.getId()));
			redirectAttributes.addFlashAttribute("success", messageSource.getMessage("event.resume.success", new Object[] { event.getEventName() }, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error while resume RFT event : " + e.getMessage(), e);
			RftEvent persistObj = rftEventService.loadRftEventForSummeryPageById(event.getId());
			EventPermissions eventPermissions = rftEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), event.getId());
			buildModel(persistObj, model, eventPermissions);
			return new ModelAndView("eventSummary");

		} finally {
			if (virtualizer != null) {
				virtualizer.cleanup();
			}
		}
		return new ModelAndView("redirect:/buyer/buyerDashboard");
	}

	@RequestMapping(value = "/eventTeamMembersList/{eventId}", method = RequestMethod.GET)
	public ResponseEntity<TableData<EventTeamMember>> eventTeamMembersList(@PathVariable(name = "eventId") String eventId, TableDataInput input) {
		TableData<EventTeamMember> data = null;
		try {
			data = new TableData<EventTeamMember>(rftEventService.getPlainTeamMembersForEvent(eventId));
			data.setDraw(input.getDraw());
		} catch (Exception e) {
			LOG.error("Error while loading draft list : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<EventTeamMember>>(data, HttpStatus.OK);
	}

	@RequestMapping(path = "/updateEventApproval", method = RequestMethod.POST)
	public String updateEventApproval(@ModelAttribute RftEvent event, RedirectAttributes redir, HttpSession session) {
		JRSwapFileVirtualizer virtualizer = null;
		try {
			virtualizer = new JRSwapFileVirtualizer(200, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);
			event = rftEventService.updateEventApproval(event, SecurityLibrary.getLoggedInUser());
			Date eventApprovedAndFinishDate = new Date();
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
		return "redirect:/buyer/" + RfxTypes.RFT.name() + "/eventSummary/" + event.getId();
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
			virtualizer = new JRSwapFileVirtualizer(200, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);

			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}
			String filename = "EvaluationSummary.pdf";
			JasperPrint jasperPrint = rftEventService.generatePdfforEvaluationSummary(eventId, timeZone, virtualizer);
			if (jasperPrint != null) {
				streamReport(jasperPrint, filename, response);
			}
			try {
				RftEventAudit audit = new RftEventAudit();
				audit.setActionBy(SecurityLibrary.getLoggedInUser());
				audit.setActionDate(new Date());
				audit.setDescription("Evaluation Summary report downloaded ");
				RftEvent event = new RftEvent();
				event.setId(eventId);
				audit.setEvent(event);
				audit.setAction(AuditActionType.Download);
				eventAuditService.save(audit);

				RftEvent rftEvent = rftEventService.getPlainEventById(eventId);
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Evaluation Summary Report downloaded for Event '" + rftEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFT);
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
			RftEvent event = rftEventService.getRftEventByeventId(eventId);
			if (event.getEventId() != null) {
				filename = (event.getEventId()).replace("/", "-") + " summary.pdf";
			}
			JasperPrint jasperPrint = rftEventService.getEvaluationSummaryPdf(event, SecurityLibrary.getLoggedInUser(), (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), virtualizer);
			if (jasperPrint != null) {
				streamReport(jasperPrint, filename, response);
			}
			try {
				RftEventAudit audit = new RftEventAudit();
				audit.setAction(AuditActionType.Download);
				audit.setActionBy(SecurityLibrary.getLoggedInUser());
				audit.setActionDate(new Date());
				audit.setDescription("Event Summary downloaded");
				audit.setEvent(event);
				eventAuditService.save(audit);

				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Event '" + event.getEventId() + "' Summary downloaded", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFT);
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
			RftEvent event = rftEventService.getRftEventByeventId(eventId);
			if (event.getEventId() != null) {
				filename = (event.getEventId()).replace("/", "-") + " AuditHistory.pdf";
			}
			JasperPrint jasperPrint = rftEventService.getEventAuditPdf(event, SecurityLibrary.getLoggedInUser(), (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), virtualizer);
			if (jasperPrint != null) {
				streamReport(jasperPrint, filename, response);
			}
			try {
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Audit Trail is downloaded ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFT);
				buyerAuditTrailDao.save(buyerAuditTrail);
			} catch (Exception e) {
				LOG.info("Error to create audit trails message");
			}
		} catch (Exception e) {
			LOG.error("Could not Download Audit History Report. " + e.getMessage(), e);
		} finally {
			if (virtualizer != null) {
				virtualizer.cleanup();
			}
		}
	}

	@RequestMapping(path = "/downloadMeetingAttendance/{eventId}/{title}/{id}", method = RequestMethod.POST)
	public void downloadMeetingAttendance(@PathVariable("eventId") String eventId, @PathVariable("title") String title, @PathVariable("id") String meetingId, HttpServletResponse response, HttpSession session) throws Exception {
		LOG.info("Meeting ID  :: " + meetingId);
		try {
			String filename = "RFT " + title + " summary.pdf";
			JasperPrint jasperPrint = rftEventService.getMeetingAttendanceReport(eventId, meetingId, session);
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

	@RequestMapping(path = "/summaryEnvelop", method = RequestMethod.POST)
	public ResponseEntity<EnvelopePojo> summaryEnvelop(@RequestParam("envelopeId") String envelopeId) {
		HttpHeaders headers = new HttpHeaders();
		RftEnvelop rftEnvelop = rftEnvelopService.getRftEnvelopById(envelopeId);

		EnvelopePojo envelope = new EnvelopePojo(rftEnvelop);
		if (envelope.getLeadEvaluater() != null) {
			envelope.setLeadEvaluater(envelope.getLeadEvaluater().createStripCopy());
		}
		if (envelope.getOpener() != null) {
			envelope.setOpener(envelope.getOpener().createStripCopy());
		}

		@SuppressWarnings("unchecked")
		List<RftEvaluatorUser> assignedEvaluators = (List<RftEvaluatorUser>) envelope.getAssignedEvaluators();

		@SuppressWarnings("unchecked")
		List<RftEnvelopeOpenerUser> assignedOpeners = (List<RftEnvelopeOpenerUser>) envelope.getAssignedOpeners();
		List<User> openersList = new ArrayList<User>();

		if (CollectionUtil.isNotEmpty(envelope.getAssignedEvaluators())) {
			for (RftEvaluatorUser evalUser : assignedEvaluators) {
				evalUser.setUser(evalUser.getUser().createStripCopy());
			}
		}

		if (CollectionUtil.isNotEmpty(envelope.getAssignedOpeners())) {
			for (RftEnvelopeOpenerUser openerUser : assignedOpeners) {
				openerUser.setUser(openerUser.getUser().createStripCopy());
			}
		}

		List<User> evaluators = new ArrayList<User>();
		for (RftEvaluatorUser evalUser : assignedEvaluators) {
			User u = new User(evalUser.getUser().getId(), evalUser.getUser().getLoginId(), evalUser.getUser().getName(), evalUser.getUser().getCommunicationEmail(), evalUser.getUser().getEmailNotifications(),evalUser.getUser().getTenantId(), evalUser.getUser().isDeleted());
			if (!evaluators.contains(u)) {
				evaluators.add(u);
			}
		}

		for (RftEnvelopeOpenerUser openerUser : assignedOpeners) {
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
		LOG.info("evaluators.size() :" + evaluators.size());
		envelope.setEvaluators(evaluators);
		envelope.setOpeners(openersList);

		return new ResponseEntity<EnvelopePojo>(envelope, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/updateEvaluators", method = RequestMethod.POST)
	public String updateEvaluators(@ModelAttribute(name = "envelop") RftEnvelop envelop, @RequestParam("eventId") String eventId, Model model, RedirectAttributes redir) {
		try {

			RftEvent event = rftEventService.loadRftEventForSummeryPageById(eventId);
			EventPermissions eventPermissions = rftEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
			if (envelop.getEnvelopType() == EnvelopType.CLOSED && CollectionUtil.isEmpty(envelop.getOpenerUsers())) {
				model.addAttribute("error", "Please add envelop opener for closed Envelope");
				buildModel(event, model, eventPermissions);
				return "eventSummary";
			}

			if (event.getEventStart().compareTo(new Date()) > 0 && EventStatus.ACTIVE == event.getStatus()) {
				model.addAttribute("error", "Change of envelope openers not allowed");
				buildModel(event, model, eventPermissions);
				return "eventSummary";
			}

			boolean isUserControl = Boolean.TRUE.equals(SecurityLibrary.getLoggedInUser().getBuyer().getEnableEventUserControle());

			rftEnvelopService.updateRftEnvelopeTeam(envelop, eventId, isUserControl);
			redir.addAttribute("success", "Envelope updated successfully");
		} catch (Exception e) {
			LOG.error("Error during envelope save : " + e.getMessage(), e);
			// model.addAttribute("error", "Error during envelope saving : " + e.getMessage());
			model.addAttribute("error", messageSource.getMessage("rfasummary.during.envelope.saving", new Object[] { e.getMessage() }, Global.LOCALE));
			RftEvent event = rftEventService.loadRftEventForSummeryPageById(eventId);
			EventPermissions eventPermissions = rftEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
			buildModel(event, model, eventPermissions);
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
	public ResponseEntity<RftEventMeeting> summaryMeeting(@RequestParam("meetingId") String meetingId) {
		HttpHeaders headers = new HttpHeaders();
		LOG.info("meetingId :" + meetingId);
		RftEventMeeting meeting = rftMeetingService.getRftMeetingById(meetingId);
		meeting.setCreatedBy(null);
		meeting.setModifiedBy(null);
		LOG.info("meeting.getId() :" + meeting.getId());
		return new ResponseEntity<RftEventMeeting>(meeting, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/concludeEvent", method = RequestMethod.POST)
	public String concludeEvent(@ModelAttribute(name = "event") RftEvent event, Model model, RedirectAttributes redir) {
		try {
			LOG.info("event.getId()***************:" + event);
			event = rftEventService.concludeRftEvent(event, SecurityLibrary.getLoggedInUser());
			try {
				RftEventAudit audit = new RftEventAudit();
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
				LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CONCLUDE, "Event ' " + event.getEventId() + "' is concluded", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFT);
				buyerAuditTrailDao.save(buyerAuditTrail);
				LOG.info("--------------------------AFTER AUDIT---------------------------------------");
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
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
			model.addAttribute("error", messageSource.getMessage("rfpsummary.during.save.eventconclude", new Object[] { e.getMessage() }, Global.LOCALE));
			RftEvent persistObj = rftEventService.loadRftEventForSummeryPageById(event.getId());
			EventPermissions eventPermissions = rftEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), event.getId());
			buildModel(persistObj, model, eventPermissions);
			return "eventSummary";
		}
		return "redirect:eventSummary/" + event.getId();
	}

	@RequestMapping(path = "/copyEventTo", method = RequestMethod.POST)
	public String copyEventTo(@ModelAttribute(name = "event") RftEvent event, @RequestParam(name = "businessUnitId", required = false) String businessUnitId, @RequestParam(name = "idRfxTemplate", required = false) String idRfxTemplate, @RequestParam(name = "selectedRfxType", required = false) RfxTypes selectedRfxType, @RequestParam(name = "auctionType", required = false) AuctionType auctionType, @RequestParam(name = "invitedSupp", required = false) String[] invitedSupp, @RequestParam(name = "bqId", required = false) String bqId, @RequestParam("concludeRemarks") String concludeRemarks, Model model, RedirectAttributes redir) {
		String newEventId = "";
		try {
			newEventId = rftEventService.createNextEvent(event, selectedRfxType, auctionType, bqId, SecurityLibrary.getLoggedInUser(), idRfxTemplate, businessUnitId, concludeRemarks, invitedSupp);
			RftEvent oldEvent = rftEventService.getRftEventByeventId(event.getId());
			if (StringUtils.checkString(newEventId).length() > 0) {
				rftEventService.updateEventPushedDate(event.getId());
				redir.addAttribute("success", "New event created successfully");
			} else {
				try {
					RftEventAudit RftAudit = new RftEventAudit();
					RftAudit.setAction(AuditActionType.Conclude);
					RftAudit.setActionBy(SecurityLibrary.getLoggedInUser());
					RftAudit.setActionDate(new Date());
					RftAudit.setDescription("Event " + oldEvent.getEventName() + " is concluded");
					RftAudit.setEvent(oldEvent);
					eventAuditService.save(RftAudit);
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}
				try {
					LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CONCLUDE, "Event ' " + oldEvent.getEventId() + "' is concluded", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFT);
					buyerAuditTrailDao.save(buyerAuditTrail);
					LOG.info("--------------------------AFTER AUDIT---------------------------------------");
				} catch (Exception e) {
					LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
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
		List<RftSupplierMeetingAttendance> list = supplierRftMeetingAttendanceService.getAllSupplierAttendance(meetingId, eventId);
		List<Supplier> suppliers = rftMeetingService.getAllSuppliersByMeetId(meetingId);
		if (CollectionUtil.isNotEmpty(suppliers) && CollectionUtil.isNotEmpty(list)) {
			result = new ArrayList<SupplierMeetingAttendancePojo>();
			for (RftSupplierMeetingAttendance atta : list) {
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
		List<RftSupplierMeetingAttendance> list = new ArrayList<RftSupplierMeetingAttendance>();
		for (SupplierMeetingAttendancePojo pojo : meetingAttendances) {
			if (StringUtils.checkString(pojo.getName()).length() > 0) {
				LOG.info("Details : " + pojo.toLogString());
				RftSupplierMeetingAttendance attendance = new RftSupplierMeetingAttendance();
				attendance.setId(pojo.getId());
				attendance.setName(pojo.getName());
				attendance.setDesignation(pojo.getDesignation());
				attendance.setMobileNumber(pojo.getMobileNumber());
				attendance.setRemarks(pojo.getRemarks());
				attendance.setAttended(pojo.getAttended());
				attendance.setMeetingAttendanceStatus(pojo.getMeetingAttendanceStatus());

				if (attendance.getAttended() == Boolean.TRUE) {
					attendance.setMeetingAttendanceStatus(MeetingAttendanceStatus.Accepted);
				}

				RftEvent event = new RftEvent();
				event.setId(pojo.getEventId());
				attendance.setRftEvent(event);
				RftEventMeeting meeting = new RftEventMeeting();
				meeting.setId(pojo.getMeetingId());
				attendance.setRfxEventMeeting(meeting);

				Supplier supplier = new Supplier();
				supplier.setId(pojo.getSupplierId());
				attendance.setSupplier(supplier);
				list.add(attendance);
			}
		}

		if (CollectionUtil.isNotEmpty(list)) {
			rftMeetingService.saveOrUpdateAttendance(list);
			header.add("success", "Succesfully updated Attendance details");
		}
		return new ResponseEntity<String>("", header, HttpStatus.OK);
	}

	@RequestMapping(path = "/completMeeting", method = RequestMethod.POST)
	public ResponseEntity<String> completMeeting(@RequestParam String meetingId, @RequestParam String eventId) throws JsonProcessingException {
		HttpHeaders header = new HttpHeaders();
		try {
			RftEventMeeting meeting = rftMeetingService.getMeetingForIdAndEvent(meetingId, eventId);
			if (meeting != null) {
				meeting.setStatus(MeetingStatus.COMPLETED);
				meeting.setModifiedBy(SecurityLibrary.getLoggedInUser());
				meeting.setModifiedDate(new Date());
				rftMeetingService.updateRftMeeting(meeting);
			}
			header.add("success", "Meeting closed Succesfully");
		} catch (Exception e) {
			LOG.error("Error while closing meeting, " + e.getMessage(), e);
			header.add("error", "Error while closing meeting");
			return new ResponseEntity<String>("", header, HttpStatus.EXPECTATION_FAILED);
		}
		return new ResponseEntity<String>("", header, HttpStatus.OK);
	}

	@RequestMapping(path = "/getRfxTemplates/{eventType}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<RfxTemplate>> getRfxTemplates(@PathVariable("eventType") String eventType) {
		LOG.info("getRfxTemplates Called ..................type:" + RfxTypes.fromStringToRfxType(eventType));
		HttpHeaders headers = new HttpHeaders();
		List<RfxTemplate> templateList = null;
		try {
			templateList = rfxTemplateService.findAllActiveTemplatesByRfxTypeForTenant(SecurityLibrary.getLoggedInUserTenantId(), RfxTypes.fromStringToRfxType(eventType), SecurityLibrary.getLoggedInUser().getId());
			setLazyProperty(templateList);
		} catch (Exception e) {
			LOG.error("Error getting RFx templates" + e.getMessage(), e);
			return new ResponseEntity<List<RfxTemplate>>(null, headers, HttpStatus.EXPECTATION_FAILED);
		}
		return new ResponseEntity<List<RfxTemplate>>(templateList, headers, HttpStatus.OK);
	}

	private void setLazyProperty(List<RfxTemplate> templateList) {
		if (CollectionUtil.isNotEmpty(templateList)) {
			for (RfxTemplate rfxTemplate : templateList) {
				rfxTemplate.setCreatedBy(null);
				rfxTemplate.setModifiedBy(null);
			}
		}

	}

	@RequestMapping(value = { "/checkBusinessUnitEmpty/{eventId}/{rfxType}/{templateId}", "/checkBusinessUnitEmpty/{eventId}", "/checkBusinessUnitEmpty/{eventId}/{rfxType}" }, method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<BusinessUnit>> checkBusinessUnitEmpty(@PathVariable("eventId") String eventId, @PathVariable(value = "templateId", required = false) String templateId, @PathVariable(value = "rfxType", required = false) String rfxType) {
		HttpHeaders headers = new HttpHeaders();
		String type = "RFT";
		if (StringUtils.checkString(rfxType).length() > 0) {
			type = rfxType;
		}
		LOG.info("eventId : " + eventId + " rfxType :" + rfxType + " templateId :" + templateId);
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
				RftEvent event = rftEventService.getRftEventById(eventId);
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
		// JasperPrint jasperPrint = rftEventService.generatePdfforEvaluationSummary(eventId);
		// if (jasperPrint != null) {
		// streamReport(jasperPrint, filename, response);
		// }
		// } catch (Exception e) {
		// LOG.error("Could not generate RFQ Evaluation Summary BqCq Report. " + e.getMessage(), e);
		// }
	}

	@RequestMapping(path = "/downloadEvaluationReport/{eventId}/{evenvelopId}", method = RequestMethod.POST)
	public void BiddingReportEnglish(@PathVariable("eventId") String eventId, @PathVariable("evenvelopId") String evenvelopId, HttpServletResponse response, HttpSession session) throws Exception {
		JRSwapFileVirtualizer virtualizer = null;
		try {
			// Virtualizar - To increase the performance
			virtualizer = new JRSwapFileVirtualizer(200, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);
			String filename = "EvaluationReport.pdf";
			JasperPrint jasperPrint = rftEventService.getEvaluationReport(eventId, evenvelopId, (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), virtualizer);
			if (jasperPrint != null) {
				streamReport(jasperPrint, filename, response);
			}
			try {
				RftEnvelop envelope = rftEnvelopDao.findById(evenvelopId);
				RftEventAudit audit = new RftEventAudit();
				audit.setAction(AuditActionType.Download);
				audit.setActionBy(SecurityLibrary.getLoggedInUser());
				audit.setActionDate(new Date());
				RftEvent event = new RftEvent();
				event.setId(eventId);
				audit.setEvent(event);
				audit.setDescription("Evaluation Report is downloaded for Envelope '" + envelope.getEnvelopTitle() + " '");
				eventAuditService.save(audit);

				RftEvent rftEvent = rftEventService.getPlainEventById(eventId);
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Evaluation Report is downloaded for Envelope '" + envelope.getEnvelopTitle() + "' for Event '" + rftEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFT);
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
	public void envelopeEvaluationSummary(@PathVariable("eventId") String eventId, @PathVariable("envelopeId") String envelopeId, HttpServletResponse response, HttpSession session) throws Exception {
		// Virtualizar - To increase the performance
		JRSwapFileVirtualizer virtualizer = null;
		try {
			String filename = "EvaluationSummary.pdf";
			virtualizer = new JRSwapFileVirtualizer(200, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);

			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			TimeZone timeZone = TimeZone.getDefault();
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}

			JasperPrint jasperPrint = rftEventService.generatePdfforEvaluationSummary(eventId, envelopeId, timeZone, virtualizer);
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
	public ModelAndView addSupplierInEvent(@PathVariable("eventId") String eventId, @RequestParam("supplierId") String supplierId, RftEventSupplier eventSupplier, RedirectAttributes attributes) throws Exception {
		try {
			FavouriteSupplier favSupp = favoriteSupplierService.findFavSupplierBySuppId(StringUtils.checkString(supplierId), SecurityLibrary.getLoggedInUserTenantId());
			eventSupplier.setSupplier(favSupp.getSupplier());
			eventSupplier.setSupplierInvitedTime(new Date());
			eventSupplier.setRfxEvent(rftEventService.getRftEventById(eventId));
			if (!doValidate(eventSupplier)) {
				LOG.info("Not Exist.....................................................");
				eventSupplier.setSubmissionStatus(SubmissionStatusType.INVITED);
				rftEventSupplierService.saveRftEventSupplier(eventSupplier);
				attributes.addFlashAttribute("success", messageSource.getMessage("flashsuccess.eventsupplier.added", new Object[] {}, Global.LOCALE));
			} else {
				attributes.addFlashAttribute("error", messageSource.getMessage("flasherror.eventsupplier.not.added", new Object[] {}, Global.LOCALE));
			}
		} catch (Exception e) {
			LOG.error("error while add event supplier in Event" + e.getMessage(), e);
			attributes.addFlashAttribute("error", messageSource.getMessage("flasherror.while.add.supplier", new Object[] { e.getMessage() }, Global.LOCALE));
		}
		return new ModelAndView("redirect:/buyer/RFT/viewSummary/" + eventId);
	}

	@RequestMapping(path = "/addFeeInSupplier/{eventId}", method = RequestMethod.POST)
	public ModelAndView addFeeInSupplier(@ModelAttribute("eventSupplier") RftEventSupplier eventSupplier, @PathVariable("eventId") String eventId, HttpSession session, RedirectAttributes redir, Model model) throws Exception {
		try {

			List<RftEventSupplier> eventsuppList = rftEventSupplierService.getAllSuppliersByFeeEventId(eventId, eventSupplier.getId());

			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			TimeZone timeZone = TimeZone.getDefault();
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}

			if (CollectionUtil.isNotEmpty(eventsuppList)) {
				for (RftEventSupplier suppList : eventsuppList) {
					suppList.setFeePaid(Boolean.TRUE);
					suppList.setFeeReference(eventSupplier.getFeeReference());
					Date feeDateTime = null;
					if (eventSupplier.getFeePaidDate() != null && eventSupplier.getFeePaidTime() != null) {
						feeDateTime = DateUtil.combineDateTime(eventSupplier.getFeePaidDate(), eventSupplier.getFeePaidTime(), timeZone);
					}
					suppList.setFeePaidDate(feeDateTime);
					suppList = rftEventSupplierService.saveRftEventSupplier(suppList);

					try {
						RftEventAudit audit = new RftEventAudit();
						audit.setActionBy(SecurityLibrary.getLoggedInUser());
						audit.setActionDate(new Date());
						audit.setDescription("Event Participaton fee of " + suppList.getRfxEvent().getParticipationFeeCurrency().getCurrencyCode() + " " + (suppList.getRfxEvent().getParticipationFees().setScale(2, BigDecimal.ROUND_HALF_UP)) + " collected for " + suppList.getSupplierCompanyName() + ". Fee Reference: " + suppList.getFeeReference());
						audit.setAction(AuditActionType.Paid);
						audit.setSupplier(suppList.getSupplier());
						RftEvent event = new RftEvent();
						event.setId(eventId);
						audit.setEvent(event);
						eventAuditService.save(audit);

						// Email to All supplier admins
						List<User> users = userDao.getAllAdminUsersForSupplier(suppList.getSupplier().getId());
						if (CollectionUtil.isNotEmpty(users)) {
							for (User user : users) {
								if (StringUtils.isNotBlank(user.getCommunicationEmail()) && user.getEmailNotifications()) {
									rftEventService.sendEmailAfterParticipationFees(user.getCommunicationEmail(), "Event Participation Payment", "Event Participation Fee has been made successfully for the below", suppList.getRfxEvent(), user.getName(), suppList.getFeeReference(), (supplierSettingsService.getSupplierTimeZoneByTenantId(user.getTenantId())));
								} else {
									LOG.warn("Communication email not set");
								}
							}
						}

						// Email to event owner
						if (StringUtils.isNotBlank(suppList.getRfxEvent().getEventOwner().getCommunicationEmail()) && suppList.getRfxEvent().getEventOwner().getEmailNotifications()) {
							rftEventService.sendEmailAfterParticipationFees(suppList.getRfxEvent().getEventOwner().getCommunicationEmail(), "Event Participation Payment", "Event Participation Fee has been made successfully for the below by " + suppList.getSupplierCompanyName(), suppList.getRfxEvent(), suppList.getRfxEvent().getEventOwner().getName(), suppList.getFeeReference(), (buyerSettingsService.getBuyerTimeZoneByTenantId(suppList.getRfxEvent().getEventOwner().getTenantId())));
						} else {
							LOG.warn("Communication email not set");
						}

						// Email to associate buyers
						if (suppList.getRfxEvent().getTeamMembers() != null && suppList.getRfxEvent().getTeamMembers().size() > 0) {
							for (RftTeamMember member : suppList.getRfxEvent().getTeamMembers()) {
								if (member.getTeamMemberType().equals(TeamMemberType.Associate_Owner)) {
									if (StringUtils.isNotBlank(member.getUser().getCommunicationEmail()) && member.getUser().getEmailNotifications()) {
										rftEventService.sendEmailAfterParticipationFees(member.getUser().getCommunicationEmail(), "Event Participation Payment", "Event Participation Fee has been made successfully for the below by " + suppList.getSupplierCompanyName(), suppList.getRfxEvent(), member.getUser().getName(), suppList.getFeeReference(), (buyerSettingsService.getBuyerTimeZoneByTenantId(member.getUser().getTenantId())));
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
					LOG.info("fee updated");
				}
			}
		} catch (Exception e) {
			LOG.error("error while add Fee Detail in supplier" + e.getMessage(), e);
			redir.addFlashAttribute("error", "Error while updating Fee Details");
		}
		return new ModelAndView("redirect:/buyer/RFT/viewSummary/" + eventId);
	}

	@RequestMapping(path = "/addDepositInSupplier/{eventId}", method = RequestMethod.POST)
	public ModelAndView addDepositInSupplier(@ModelAttribute("eventSupplier") RftEventSupplier eventSupplier, @PathVariable("eventId") String eventId, HttpSession session, RedirectAttributes redir, Model model) throws Exception {
		try {

			List<RftEventSupplier> eventsuppList = rftEventSupplierService.getAllSuppliersByFeeEventId(eventId, eventSupplier.getId());
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			TimeZone timeZone = TimeZone.getDefault();
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}
			if (CollectionUtil.isNotEmpty(eventsuppList)) {
				for (RftEventSupplier suppList : eventsuppList) {
					suppList.setDepositPaid(Boolean.TRUE);
					suppList.setDepositReference(eventSupplier.getDepositReference());
					Date depositDateTime = null;
					if (eventSupplier.getDepositPaidDate() != null && eventSupplier.getDepositPaidTime() != null) {
						depositDateTime = DateUtil.combineDateTime(eventSupplier.getDepositPaidDate(), eventSupplier.getDepositPaidTime(), timeZone);
					}
					suppList.setDepositPaidDate(depositDateTime);
					suppList = rftEventSupplierService.saveRftEventSupplier(suppList);
					redir.addFlashAttribute("success", "Event Deposits added successfully");
					model.addAttribute("eventSupplier", suppList);
					LOG.info("Deposit updated");
				}
			}

		} catch (Exception e) {
			LOG.error("error while add Deposit Detail in supplier" + e.getMessage(), e);
			redir.addFlashAttribute("error", "Error while updating Fee Details");
		}
		return new ModelAndView("redirect:/buyer/RFT/viewSummary/" + eventId);
	}

	@RequestMapping(path = "/downloadEvaluationConclustionReport/{eventId}", method = RequestMethod.POST)
	public void downloadEvaluationConclustionReport(@PathVariable("eventId") String eventId, HttpServletResponse response, HttpSession session) throws Exception {
		JRSwapFileVirtualizer virtualizer = null;
		try {
			virtualizer = new JRSwapFileVirtualizer(200, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);
			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}
			String filename = "EvaluationConclusionReport.pdf";
			JasperPrint jasperPrint = rftEventService.generatePdfforEvaluationConclusion(eventId, timeZone, virtualizer);
			if (jasperPrint != null) {
				streamReport(jasperPrint, filename, response);
			}
			try {
				RftEventAudit audit = new RftEventAudit();
				audit.setActionBy(SecurityLibrary.getLoggedInUser());
				audit.setActionDate(new Date());
				audit.setDescription("Evaluation Conclusion Report is downloaded ");
				RftEvent event = new RftEvent();
				event.setId(eventId);
				audit.setEvent(event);
				audit.setAction(AuditActionType.Download);
				eventAuditService.save(audit);

				RfpEvent rfpEvent = rfpEventService.getPlainEventById(eventId);
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Evaluation Conclusion Report is downloaded for Event ‘" + rfpEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFT);
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
	public String updateEventSuspensionApproval(@ModelAttribute RftEvent event, RedirectAttributes redir, HttpSession session) {
		JRSwapFileVirtualizer virtualizer = null;
		try {
			virtualizer = new JRSwapFileVirtualizer(200, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);
			event = rftEventService.updateEventSuspensionApproval(event, SecurityLibrary.getLoggedInUser());
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
		return "redirect:/buyer/" + RfxTypes.RFT.name() + "/eventSummary/" + event.getId();
	}

}
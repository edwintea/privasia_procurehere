package com.privasia.procurehere.web.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.zip.ZipOutputStream;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import com.privasia.procurehere.core.entity.RfaEventSor;
import com.privasia.procurehere.core.entity.Sor;
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
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.RfaCqDao;
import com.privasia.procurehere.core.dao.RfaEnvelopDao;
import com.privasia.procurehere.core.dao.UserDao;
import com.privasia.procurehere.core.entity.AuctionBids;
import com.privasia.procurehere.core.entity.AuctionRules;
import com.privasia.procurehere.core.entity.Bq;
import com.privasia.procurehere.core.entity.BusinessUnit;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.EventTeamMember;
import com.privasia.procurehere.core.entity.RfaApprovalUser;
import com.privasia.procurehere.core.entity.RfaCq;
import com.privasia.procurehere.core.entity.RfaEnvelop;
import com.privasia.procurehere.core.entity.RfaEnvelopeOpenerUser;
import com.privasia.procurehere.core.entity.RfaEvaluatorUser;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.entity.RfaEventApproval;
import com.privasia.procurehere.core.entity.RfaEventAudit;
import com.privasia.procurehere.core.entity.RfaEventAwardAudit;
import com.privasia.procurehere.core.entity.RfaEventBq;
import com.privasia.procurehere.core.entity.RfaEventDocument;
import com.privasia.procurehere.core.entity.RfaEventMeeting;
import com.privasia.procurehere.core.entity.RfaEventMeetingDocument;
import com.privasia.procurehere.core.entity.RfaEventSupplier;
import com.privasia.procurehere.core.entity.RfaEventSuspensionApproval;
import com.privasia.procurehere.core.entity.RfaSupplierBq;
import com.privasia.procurehere.core.entity.RfaSupplierMeetingAttendance;
import com.privasia.procurehere.core.entity.RfaSuspensionApprovalUser;
import com.privasia.procurehere.core.entity.RfaTeamMember;
import com.privasia.procurehere.core.entity.RfiEnvelop;
import com.privasia.procurehere.core.entity.RfiEvaluatorUser;
import com.privasia.procurehere.core.entity.RfiEvent;
import com.privasia.procurehere.core.entity.RfiEventAudit;
import com.privasia.procurehere.core.entity.RfpEnvelop;
import com.privasia.procurehere.core.entity.RfpEvaluatorUser;
import com.privasia.procurehere.core.entity.RfpEvent;
import com.privasia.procurehere.core.entity.RfpEventAudit;
import com.privasia.procurehere.core.entity.RfpEventAwardAudit;
import com.privasia.procurehere.core.entity.RfqEnvelop;
import com.privasia.procurehere.core.entity.RfqEvaluatorUser;
import com.privasia.procurehere.core.entity.RfqEvent;
import com.privasia.procurehere.core.entity.RfqEventAudit;
import com.privasia.procurehere.core.entity.RfqEventAwardAudit;
import com.privasia.procurehere.core.entity.RftEnvelop;
import com.privasia.procurehere.core.entity.RftEvaluatorUser;
import com.privasia.procurehere.core.entity.RftEvent;
import com.privasia.procurehere.core.entity.RftEventAudit;
import com.privasia.procurehere.core.entity.RftEventAwardAudit;
import com.privasia.procurehere.core.entity.RfxTemplate;
import com.privasia.procurehere.core.entity.SourcingFormRequest;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.TemplateField;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.ApprovalStatus;
import com.privasia.procurehere.core.enums.AuctionType;
import com.privasia.procurehere.core.enums.AuditActionType;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.DurationMinSecType;
import com.privasia.procurehere.core.enums.EnvelopType;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.EventVisibilityType;
import com.privasia.procurehere.core.enums.MeetingStatus;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.PreBidByType;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.SuspensionType;
import com.privasia.procurehere.core.enums.TeamMemberType;
import com.privasia.procurehere.core.enums.UserType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.SubscriptionException;
import com.privasia.procurehere.core.pojo.EnvelopePojo;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.FeePojo;
import com.privasia.procurehere.core.pojo.SupplierMeetingAttendancePojo;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.pojo.TatReportPojo;
import com.privasia.procurehere.core.pojo.UserPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.DateUtil;
import com.privasia.procurehere.core.utils.FileUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.job.DutchAuctionJob;
import com.privasia.procurehere.service.BusinessUnitService;
import com.privasia.procurehere.service.BuyerAddressService;
import com.privasia.procurehere.service.EventAuditService;
import com.privasia.procurehere.service.EventIdSettingsService;
import com.privasia.procurehere.service.FavoriteSupplierService;
import com.privasia.procurehere.service.RfaBqService;
import com.privasia.procurehere.service.RfaCqService;
import com.privasia.procurehere.service.RfaDocumentService;
import com.privasia.procurehere.service.RfaEnvelopService;
import com.privasia.procurehere.service.RfaEventMeetingService;
import com.privasia.procurehere.service.RfaEventService;
import com.privasia.procurehere.service.RfaEventSupplierService;
import com.privasia.procurehere.service.RfaMeetingService;
import com.privasia.procurehere.service.RfaSupplierBqService;
import com.privasia.procurehere.service.RfaSupplierCqItemService;
import com.privasia.procurehere.service.RfiEnvelopService;
import com.privasia.procurehere.service.RfpEnvelopService;
import com.privasia.procurehere.service.RfqEnvelopService;
import com.privasia.procurehere.service.RftEnvelopService;
import com.privasia.procurehere.service.RfxTemplateService;
import com.privasia.procurehere.service.SourcingFormRequestService;
import com.privasia.procurehere.service.SupplierRfaAttendanceService;
import com.privasia.procurehere.service.UserService;
import com.privasia.procurehere.service.impl.SnapShotAuditService;
import com.privasia.procurehere.web.editors.RfaApprovalEditor;
import com.privasia.procurehere.web.editors.RfaEnvelopeOpenerUserEditor;
import com.privasia.procurehere.web.editors.RfaSuspensionApprovalEditor;
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
@RequestMapping("/buyer/RFA")
public class RfaSummaryController extends EventSummaryBase {

	protected static final Logger LOG = LogManager.getLogger(Global.AUCTION_LOG);

	@Autowired
	RfaEventService eventService;

	@Autowired
	BuyerAddressService buyerAddressService;

	@Autowired
	RfaEnvelopService envelopeService;

	@Autowired
	RfaDocumentService documentService;

	@Autowired
	RfaCqService cqService;

	@Autowired
	RfaBqService bqService;

	@Autowired
	RfaMeetingService meetingService;

	@Autowired
	RfaCqDao cqDao;

	@Autowired
	UserService userService;

	@Autowired
	FavoriteSupplierService favoriteSupplierService;

	@Autowired
	SchedulerFactoryBean schedulerFactoryBean;

	@Autowired
	EventAuditService eventAuditService;

	@Autowired
	RfaApprovalEditor rfaApprovalEditor;

	@Autowired
	RfaEventService rfaEventService;

	@Autowired
	RfaEnvelopService rfaEnvelopService;

	@Autowired
	UserEditor userEditor;

	@Autowired
	SupplierEditor supplierEditor;

	@Autowired
	RfaEventSupplierService rfaEventSupplierService;

	@Autowired
	SupplierRfaAttendanceService supplierRfaMeetingAttendanceService;

	@Autowired
	RfaSupplierBqService rfaSupplierBqService;

	@Autowired
	RfaMeetingService rfaMeetingService;

	@Autowired
	SimpMessagingTemplate simpMessagingTemplate;

	@Autowired
	BusinessUnitService businessUnitService;

	@Autowired
	RfxTemplateService rfxTemplateService;
	@Autowired
	EventIdSettingsService eventIdSettingsService;

	@Autowired
	SourcingFormRequestService sourcingFormRequestService;

	@Autowired
	SnapShotAuditService snapShotAuditService;

	@Autowired
	JmsTemplate jmsTemplate;

	@Autowired
	RfaSupplierCqItemService rfaSupplierCqItemService;

	@Autowired
	RftEnvelopService rftEnvelopService;

	@Autowired
	RfqEnvelopService rfqEnvelopService;

	@Autowired
	RfpEnvelopService rfpEnvelopService;

	@Autowired
	RfiEnvelopService rfiEnvelopService;

	@Autowired
	RfaEnvelopDao rfaEnvelopDao;

	@Autowired
	RfaEventMeetingService rfaEventMeetingService;

	@Autowired
	RfaEnvelopeOpenerUserEditor envelopeOpenerUserEditor;

	@Autowired
	UserDao userDao;

	@Autowired
	RfaSuspensionApprovalEditor rfaSuspensionApprovalEditor;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Autowired
	RfaSorService rfaSorService;

	@InitBinder
	public void initBinder(WebDataBinder binder, HttpSession session) {

		TimeZone timeZone = TimeZone.getDefault();
		String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}

		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		format.setTimeZone(timeZone);
		SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
		timeFormat.setTimeZone(timeZone);

		binder.registerCustomEditor(Date.class, "auctionResumeDateTime", new CustomDateEditor(format, true));
		binder.registerCustomEditor(Date.class, "auctionResumeTime", new CustomDateEditor(timeFormat, true));
		binder.registerCustomEditor(RfaApprovalUser.class, rfaApprovalEditor);
		binder.registerCustomEditor(RfaSuspensionApprovalUser.class, rfaSuspensionApprovalEditor);
		binder.registerCustomEditor(Supplier.class, supplierEditor);
		StringTrimmerEditor stringtrimmer = new StringTrimmerEditor(true);
		binder.registerCustomEditor(String.class, stringtrimmer);
		binder.registerCustomEditor(User.class, userEditor);

		format.setTimeZone(timeZone);
		timeFormat.setTimeZone(timeZone);
		binder.registerCustomEditor(Date.class, "feePaidTime", new CustomDateEditor(timeFormat, true));
		binder.registerCustomEditor(Date.class, "feePaidDate", new CustomDateEditor(format, true));
		binder.registerCustomEditor(Date.class, "depositPaidTime", new CustomDateEditor(timeFormat, true));
		binder.registerCustomEditor(Date.class, "depositPaidDate", new CustomDateEditor(format, true));
		binder.registerCustomEditor(RfaEnvelopeOpenerUser.class, envelopeOpenerUserEditor);
	}

	public RfaSummaryController() {
		super(RfxTypes.RFA);
	}

	@RequestMapping(path = "/eventSummary/{eventId}", method = RequestMethod.GET)
	public ModelAndView summaryRfaEvent(@PathVariable String eventId, Model model, RedirectAttributes redir, HttpServletRequest request) {
		if (StringUtils.checkString(eventId).length() == 0) {
			return new ModelAndView("redirect:/400_error");
		}

		boolean subscriptionExpired = super.checkSubscriptionPackageUserBased();
		model.addAttribute("subscriptionExpired", subscriptionExpired);
		model.addAttribute("invitedSupplier", rfaEventSupplierService.getAllSubmittedSupplierByEventId(eventId));
		model.addAttribute("rfxTemplateList", rfxTemplateService.findAllActiveTemplatesByRfxTypeForTenant(SecurityLibrary.getLoggedInUserTenantId(), RfxTypes.RFI, SecurityLibrary.getLoggedInUser().getId()));
		EventPermissions eventPermissions = rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
		RfaEvent event = buildModel(eventId, model, eventPermissions);

		boolean isSuspensionApprovalActive = false;
		boolean showApprovalRemark = false;

		if (event.getStatus() == EventStatus.SUSPENDED && CollectionUtil.isNotEmpty(event.getSuspensionApprovals())) {
			for (RfaEventSuspensionApproval app : event.getSuspensionApprovals()) {
				for (RfaSuspensionApprovalUser user : app.getApprovalUsers()) {
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
			if (eventPermissions.isOwner() || eventPermissions.isViewer() || eventPermissions.isEditor()) {
				LOG.info("--------------------------->");
				return new ModelAndView("eventSummary");
			} else if (eventPermissions.isRevertBidUser() || eventPermissions.isApprover() || eventPermissions.isEvaluator() || SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY")) {
				LOG.info("--------------------------->");
				model.addAttribute("showSummaryTab", true);
				return new ModelAndView("viewSummary");
			} else {
				LOG.info("--------------------------->");
				redir.addFlashAttribute("requestedUrl", request.getRequestURL());
				//return new ModelAndView("redirect:/403_error"); permission modified during display in dashboard
				return new ModelAndView("viewSummary");
			}
		} else if (event.getStatus() == EventStatus.CLOSED && eventPermissions.isConclusionUser()) {
			return new ModelAndView("redirect:/buyer/RFA/envelopList/" + eventId);
		} else {
			if (!eventPermissions.isUnMaskUser()) {
				model.addAttribute("showSummaryTab", true);
				return new ModelAndView("viewSummary");
			} else {
				return new ModelAndView("redirect:/buyer/RFA/envelopList/" + eventId);
			}
		}
	}

	private RfaEvent buildModel(String eventId, Model model, EventPermissions eventPermissions) {
		LOG.info("event Id :  model : " + eventId);
		RfaEvent event = eventService.loadRfaEventForSummeryById(eventId);
		LOG.info("event : " + event);
		model.addAttribute("event", event);
		model.addAttribute("endReminders", event.getRfaEndReminder());
		model.addAttribute("startReminders", event.getRfaStartReminder());
		model.addAttribute("listDocs", documentService.findAllRfaEventdocsbyEventId(event.getId())); // event.getDocuments());
		List<Supplier> suppList = eventService.getEventSuppliers(eventId);

		List<FeePojo> eventsuppList = rfaEventSupplierService.getAllInvitedSuppliersByEventId(eventId);
		model.addAttribute("listFavSuppliers", eventsuppList);
		model.addAttribute("supplierList", suppList);
		model.addAttribute("eventSuppliers", rfaEventSupplierService.getEventQualifiedSuppliersForEvaluation(event.getId()));
		List<RfaEventMeeting> meetingList = rfaMeetingService.getAllRfaMeetingWithPlainDocByEventId(event.getId());
		model.addAttribute("meetingList", meetingList); // event.getMeetings());
		model.addAttribute("rfxType", RfxTypes.values());

		User unMaskedUser = rfaEventDao.getUnMaskedUserNameAndMailByEventId(event.getId());
		if (unMaskedUser != null) {
			model.addAttribute("unMaskedUser", unMaskedUser);
		} else {
			model.addAttribute("unMaskedUser", null);
		}
		User revertLastBidUser = rfaEventDao.getRevertLastBidUserNameAndMailByEventId(event.getId());
		if (revertLastBidUser != null) {
			model.addAttribute("revertBidUser", revertLastBidUser);
		} else {
			model.addAttribute("revertBidUser", null);
		}
		// for auction bid history
		List<Supplier> eventSupplierList = rfaEventSupplierService.getRfaEventSupplierForAuctionConsole(eventId);
		model.addAttribute("eventSupplierList", eventSupplierList);
		model.addAttribute("eventPermissions", eventPermissions);

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

		List<RfaEventBq> bqList = event.getEventBqs(); // bqService.findRfaBqbyEventId(eventId);
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


		List<RfaEventSor> sorList = event.getEventSors();


		for (Sor bq : sorList) {
			int i = 4;
			if (StringUtils.checkString(bq.getField1Label()).length() > 0) {
				i++;
			}

			bq.setHeaderCount(i);
		}

		model.addAttribute("sorList", sorList);

		model.addAttribute("awardedSupplierList", rfaEventService.getAwardedSuppliers(event.getId()));
		model.addAttribute("eventAudit", eventAuditService.getRfaEventAudit(event.getId()));
		model.addAttribute("cqList", cqService.findRfaCqForEvent(eventId));
		model.addAttribute("envelopList", envelopeService.getAllRfaEnvelopByEventId(eventId));
		model.addAttribute("envelopListCount", envelopeService.getAllEnvelopCountByEventId(event.getId()));
		model.addAttribute("comments", event.getComment());
		model.addAttribute("suspComments", event.getSuspensionComment());
		model.addAttribute("eventTimeline", rfaEventService.getRfaEventTimeline(event.getId()));
		model.addAttribute("eventPermissions", eventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
		model.addAttribute("eventContactsList", rfaEventService.getAllContactForEvent(event.getId()));
		model.addAttribute("awardComments", event.getAwardComment());

		List<UserPojo> userListSumm = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(), "", UserType.NORMAL_USER);
		model.addAttribute("openers", userListSumm);
		model.addAttribute("evaluationOwner", userListSumm);

		AuctionRules auctionRules = rfaEventService.getAuctionRulesByEventId(eventId);
		model.addAttribute("auctionRules", auctionRules);

		model.addAttribute("envelop", new RfaEnvelop());
		List<User> assignedTeamMembers = new ArrayList<>();
		for (RfaTeamMember rfaTeamMember : ((RfaEvent) event).getTeamMembers()) {
			try {
				assignedTeamMembers.add((User) rfaTeamMember.getUser().clone());
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

		List<RfaApprovalUser> rfaApprovalUserList = new ArrayList<RfaApprovalUser>();
		for (User user : userTeamMemberList) {
			rfaApprovalUserList.add(new RfaApprovalUser(user));
		}
		model.addAttribute("userList", rfaApprovalUserList);
		// List<User> userList = userService.fetchAllActiveUsersForTenant(SecurityLibrary.getLoggedInUserTenantId());

		List<User> userAppList = new ArrayList<User>();
		if (CollectionUtil.isNotEmpty(event.getApprovals())) {
			for (RfaEventApproval approvalLevel : event.getApprovals()) {
				approvalLevel.getLevel();
				if (CollectionUtil.isNotEmpty(approvalLevel.getApprovalUsers())) {
					for (RfaApprovalUser user : approvalLevel.getApprovalUsers()) {
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

		boolean subscriptionExpired = super.checkSubscriptionPackageUserBased();
		model.addAttribute("subscriptionExpired", subscriptionExpired);

		EventPermissions eventPermissions = rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
		RfaEvent event = buildModel(eventId, model, eventPermissions);

		boolean isSuspensionApprovalActive = false;
		boolean showApprovalRemark = false;

		if (event.getStatus() == EventStatus.SUSPENDED && CollectionUtil.isNotEmpty(event.getSuspensionApprovals())) {
			for (RfaEventSuspensionApproval app : event.getSuspensionApprovals()) {
				for (RfaSuspensionApprovalUser user : app.getApprovalUsers()) {
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
		/*//no need this condi, the jsp already did checking when user click view button for the viewSummary page
		if (!checkPermissionToAllow(eventPermissions)) {
			redir.addFlashAttribute("requestedUrl", request.getRequestURL());
			return new ModelAndView("redirect:/403_error");
		}*/
		try {
			List<RfxTemplate> templateList = rfxTemplateService.findAllActiveTemplatesByRfxTypeForTenantId(SecurityLibrary.getLoggedInUserTenantId(), RfxTypes.RFI);
			model.addAttribute("rfxTemplateList", templateList);
			LOG.info("=========== " + templateList.size());
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
		model.addAttribute("invitedSupplier", rfaEventSupplierService.getAllSubmittedSupplierByEventId(eventId));
		model.addAttribute("showSummaryTab", true);
		return new ModelAndView("viewSummary");
	}

	/**
	 * @param eventPermissions
	 * @return
	 */
	private boolean checkPermissionToAllow(EventPermissions eventPermissions) {
		boolean allow = false;
		if (eventPermissions.isOwner() || eventPermissions.isEditor() //
				|| eventPermissions.isApprover() //
				|| eventPermissions.isEvaluator() //
				|| eventPermissions.isLeadEvaluator() //
				|| eventPermissions.isOpener() //
				|| eventPermissions.isViewer() //
				|| eventPermissions.isRevertBidUser() //
				|| eventPermissions.isConclusionUser() //
		) {
			allow = true;
		}
		if (SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY")) {
			allow = true;
		}
		return allow;
	}

	@RequestMapping(value = "/summeryPrevious", method = RequestMethod.POST)
	public String summeryPrevious(@ModelAttribute RfaEvent rfaEvent, Model model) {
		rfaEvent = eventService.getRfaEventByeventId(rfaEvent.getId());
		if (rfaEvent != null) {
			model.addAttribute("eventPermissions", eventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), rfaEvent.getId()));
			return "redirect:envelopList/" + rfaEvent.getId();
		} else {
			LOG.error("Event not found redirecting to login ");
			return "redirect:/login";
		}
	}

	@RequestMapping(value = "finishEvent", method = RequestMethod.POST)
	public ModelAndView finishEvent(@ModelAttribute("event") RfaEvent rfaEvent, Model model, RedirectAttributes redir, HttpSession session) {
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

			SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			dateTimeFormat.setTimeZone(timeZone);

			LOG.info("Event Id : " + rfaEvent.getId());
			EventPermissions eventPermissions = rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), rfaEvent.getId());
			buildModel(rfaEvent.getId(), model, eventPermissions);
			model.addAttribute("eventPermissions", eventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), rfaEvent.getId()));

			if (StringUtils.checkString(rfaEvent.getId()).length() > 0) {
				RfaEvent persistObj = eventService.getRfaEventByeventId(rfaEvent.getId());
				if (!persistObj.getViewSupplerName() && CollectionUtil.isEmpty(persistObj.getUnMaskedUsers())) {
					model.addAttribute("error", messageSource.getMessage("unmask.user", new Object[] {}, Global.LOCALE));
					// RfaEvent event = rfaEventService.loadRfaEventForSummeryById(rfaEvent.getId());
					buildModel(rfaEvent.getId(), model, eventPermissions);
					return new ModelAndView("eventSummary");

				}
				LOG.info("Check type : " + persistObj.getAuctionType());

				LOG.info("--------------->>>>>>>>>>>>> " + persistObj.getEnableApprovalReminder());
				if (Boolean.TRUE == persistObj.getEnableApprovalReminder()) {
					if (persistObj.getReminderAfterHour() == null) {
						model.addAttribute("error", messageSource.getMessage("approval.reminder.add.hour", new Object[] {}, Global.LOCALE));
						// RfaEvent event = rfaEventService.loadRfaEventForSummeryById(rfaEvent.getId());
						buildModel(rfaEvent.getId(), model, eventPermissions);
						return new ModelAndView("eventSummary");
					}
					if (persistObj.getReminderCount() == null) {
						model.addAttribute("error", messageSource.getMessage("approval.reminder.count.reminder", new Object[] {}, Global.LOCALE));
						// RfaEvent event = rfaEventService.loadRfaEventForSummeryById(rfaEvent.getId());
						buildModel(rfaEvent.getId(), model, eventPermissions);
						return new ModelAndView("eventSummary");
					}
				}

				if (persistObj.getAuctionStartRelative() != null && persistObj.getAuctionStartRelative()) {
					LOG.info("Event staus : " + persistObj.getAuctionStartRelative());
					EventStatus eventStatus = rfaEventService.checkRelativeEventStatus(persistObj.getId());
					LOG.info("Event staus : " + eventStatus);
					if (eventStatus != null && !(eventStatus == EventStatus.APPROVED || eventStatus == EventStatus.ACTIVE)) {
						buildModel(rfaEvent.getId(), model, eventPermissions);
						model.addAttribute("error", messageSource.getMessage("summary.error.relativeEvent.status", new Object[] { rfaEvent.getEventName() }, Global.LOCALE));
						return new ModelAndView("eventSummary");
					}
				}

				if (!(persistObj.getAuctionType().equals(AuctionType.FORWARD_DUTCH)) && !(persistObj.getAuctionType().equals(AuctionType.REVERSE_DUTCH))) {
					// Validate if all BQ/CQ have been assigned envelope

					Integer envelopCount = eventService.getCountOfEnvelopByEventId(rfaEvent.getId());
					if (envelopCount == 0) {
						buildModel(rfaEvent.getId(), model, eventPermissions);
						model.addAttribute("error", messageSource.getMessage("summary.envelope.not.exists", new Object[] { rfaEvent.getEventName() }, Global.LOCALE));
						return new ModelAndView("eventSummary");
					}
					LOG.info("Check type in side");

					List<RfaEventSor> notAssignSors = rfaSorService.getNotAssignedSorIdsByEventId(rfaEvent.getId());
					if (CollectionUtil.isNotEmpty(notAssignSors)) {
						List<String> sorNames = new ArrayList<String>();
						for (RfaEventSor rftEventSor : notAssignSors) {
							sorNames.add(rftEventSor.getName());
						}
						buildModel(rfaEvent.getId(), model, eventPermissions);
						model.addAttribute("error", messageSource.getMessage("summary.sor.not.assigned", new Object[] { sorNames }, Global.LOCALE));
						return new ModelAndView("eventSummary");
					}

					List<RfaEventBq> notAssignBqs = bqService.getNotAssignedRfaBqIdsByEventId(rfaEvent.getId());
					if (CollectionUtil.isNotEmpty(notAssignBqs)) {
						List<String> bqNames = new ArrayList<String>();
						for (RfaEventBq rfaEventBq : notAssignBqs) {
							bqNames.add(rfaEventBq.getName());
						}
						buildModel(rfaEvent.getId(), model, eventPermissions);
						model.addAttribute("error", messageSource.getMessage("summary.bq.not.assigned", new Object[] { bqNames }, Global.LOCALE));
						return new ModelAndView("eventSummary");
					}
					List<RfaCq> notAssignCqs = cqService.getNotAssignedRfaCqIdsByEventId(rfaEvent.getId());
					if (CollectionUtil.isNotEmpty(notAssignCqs)) {
						List<String> cqNames = new ArrayList<String>();
						for (RfaCq rfaCq : notAssignCqs) {
							cqNames.add(rfaCq.getName());
						}
						buildModel(rfaEvent.getId(), model, eventPermissions);
						model.addAttribute("error", messageSource.getMessage("summary.cq.not.assigned", new Object[] { cqNames }, Global.LOCALE));
						return new ModelAndView("eventSummary");
					}
				}

				// check if empty envelop present
				RfaEnvelop envelop = rfaEnvelopService.getEmptyEnvelopByEventId(rfaEvent.getId());
				if (envelop != null && envelop.getEnvelopEmpty() == 0) {
					buildModel(rfaEvent.getId(), model, eventPermissions);
					model.addAttribute("error", messageSource.getMessage("empty.envelop.finish", new Object[] { envelop.getEnvelopTitle() }, Global.LOCALE));
					return new ModelAndView("eventSummary");
				}

				// Validate Bq items
				List<String> notSectionAddedBqs = bqService.getNotSectionAddedRfaBqIdsByEventId(rfaEvent.getId());
				if (CollectionUtil.isNotEmpty(notSectionAddedBqs)) {
					String names = org.apache.commons.lang.StringUtils.join(notSectionAddedBqs, ",");
					model.addAttribute("error", messageSource.getMessage("summary.bq.not.sectionAdded", new Object[] { names }, Global.LOCALE));
					buildModel(rfaEvent.getId(), model, eventPermissions);
					return new ModelAndView("eventSummary");
				}

				// Validate Bq items inside section
				List<String> notItemSectionAddedBqs = bqService.getNotSectionItemAddedRfaBqIdsByEventId(rfaEvent.getId());
				if (CollectionUtil.isNotEmpty(notItemSectionAddedBqs)) {
					String names = org.apache.commons.lang.StringUtils.join(notItemSectionAddedBqs, ",");
					model.addAttribute("error", messageSource.getMessage("summary.bq.item.not.sectionAdded", new Object[] { names }, Global.LOCALE));
					buildModel(rfaEvent.getId(), model, eventPermissions);
					return new ModelAndView("eventSummary");
				}

				if(rfaEvent.getScheduleOfRate() == Boolean.TRUE) {
					Integer totalSor = rfaSorService.getCountOfSorByEventId(rfaEvent.getId());
					if(totalSor == null || totalSor == 0) {
						model.addAttribute("error", messageSource.getMessage("summary.sor.nosor", new Object[] {}, Global.LOCALE));
						buildModel(rfaEvent.getId(), model, eventPermissions);
						return new ModelAndView("eventSummary");
					}
				}
				// Validate Sor items
				List<String> notSectionAddedSors = rfaSorService.getNotSectionAddedRfaSorIdsByEventId(rfaEvent.getId());
				if (CollectionUtil.isNotEmpty(notSectionAddedSors)) {
					String names = org.apache.commons.lang.StringUtils.join(notSectionAddedSors, ",");
					model.addAttribute("error", messageSource.getMessage("summary.sor.not.sectionAdded", new Object[] { names }, Global.LOCALE));
					buildModel(rfaEvent.getId(), model, eventPermissions);
					return new ModelAndView("eventSummary");
				}

				// Validate Sor items inside section
				List<String> notItemSectionAddedSors = rfaSorService.getNotSectionItemAddedRfaSorIdsByEventId(rfaEvent.getId());
				if (CollectionUtil.isNotEmpty(notItemSectionAddedSors)) {
					String names = org.apache.commons.lang.StringUtils.join(notItemSectionAddedSors, ",");
					model.addAttribute("error", messageSource.getMessage("summary.sor.not.sectionAdded", new Object[] { names }, Global.LOCALE));
					buildModel(rfaEvent.getId(), model, eventPermissions);
					return new ModelAndView("eventSummary");
				}

				// Validate Cq items
				List<String> notSectionAddedCqs = cqService.getNotSectionAddedRfaCqIdsByEventId(rfaEvent.getId());
				if (CollectionUtil.isNotEmpty(notSectionAddedCqs)) {
					String names = org.apache.commons.lang.StringUtils.join(notSectionAddedCqs, ",");
					model.addAttribute("error", messageSource.getMessage("summary.cq.not.sectionAdded", new Object[] { names }, Global.LOCALE));
					buildModel(rfaEvent.getId(), model, eventPermissions);
					return new ModelAndView("eventSummary");
				}
				// Validate Cq items inside section
				List<String> notItemSectionAddedCqs = cqService.getNotSectionItemAddedRfaCqIdsByEventId(rfaEvent.getId());
				if (CollectionUtil.isNotEmpty(notItemSectionAddedCqs)) {
					String names = org.apache.commons.lang.StringUtils.join(notItemSectionAddedCqs, ",");
					model.addAttribute("error", messageSource.getMessage("summary.cq.item.not.sectionAdded", new Object[] { names }, Global.LOCALE));
					buildModel(rfaEvent.getId(), model, eventPermissions);
					return new ModelAndView("eventSummary");
				}
				// Validate Supplier
				if (rfaEvent.getEventVisibility() == EventVisibilityType.PRIVATE) {
					long supplierList = rfaEventService.getEventSuppliersCount(rfaEvent.getId());
					if (supplierList == 0) {
						LOG.info("supplierList:" + supplierList);
						model.addAttribute("error", messageSource.getMessage("summary.not.supplierAdded", new Object[] { null }, Global.LOCALE));
						buildModel(rfaEvent.getId(), model, eventPermissions);
						return new ModelAndView("eventSummary");
					}
				}
				// validate Decimal and Base Currency
				if (persistObj.getDecimal() == null) {
					buildModel(rfaEvent.getId(), model, eventPermissions);
					model.addAttribute("error", messageSource.getMessage("summary.decimal.not.exists", new Object[] { rfaEvent.getEventName() }, Global.LOCALE));
					return new ModelAndView("eventSummary");
				}

				if (persistObj.getBaseCurrency() == null) {
					buildModel(rfaEvent.getId(), model, eventPermissions);
					model.addAttribute("error", messageSource.getMessage("summary.baseCurrency.not.exists", new Object[] { rfaEvent.getEventName() }, Global.LOCALE));
					return new ModelAndView("eventSummary");
				}
				
				if (persistObj.getEventPublishDate() != null && persistObj.getEventStart() != null && persistObj.getEventPublishDate().after(persistObj.getEventStart())) {
					model.addAttribute("error", messageSource.getMessage("rftEvent.error.publishdate", new Object[] { dateTimeFormat.format(persistObj.getEventPublishDate()) }, Global.LOCALE));
					buildModel(rfaEvent.getId(), model, eventPermissions);
					return new ModelAndView("eventSummary");
				}

				// Resume Validation...
				Date publishDate = null;
				Date startDate = null;
				Date endDate = null;

				if (persistObj.getStatus() == EventStatus.SUSPENDED) {

					// RfaEvent rfaEventOBJ = eventService.loadRfaEventForSummeryById(rfaEvent.getId());

					try {
						// Check subscription limit
						super.checkSubscriptionPackageForUserBased();
					} catch (SubscriptionException e) {
						LOG.error(e.getMessage());
						redir.addFlashAttribute("error", messageSource.getMessage("reached.subscription.limit", new Object[] { e.getMessage() }, Global.LOCALE));
						return new ModelAndView("redirect:/buyer/buyerDashboard");
					} catch (Exception e) {
						LOG.error("Error While checking subscription package : " + e.getMessage(), e);
					}

					if (!validateMeetings(model, dateTimeFormat, persistObj, redir)) {
						return new ModelAndView("redirect:eventSummary/" + rfaEvent.getId());
					}

					if ((Boolean.TRUE == persistObj.getEnableSuspensionApproval() && CollectionUtil.isEmpty(persistObj.getSuspensionApprovals())) || Boolean.FALSE == persistObj.getEnableSuspensionApproval()) {

						LOG.info("resume date:" + rfaEvent.getAuctionResumeDateTime());
						// Possible that the event is being resumed even before auction start date. In such cases the
						// resume
						// date time will not be available.
						if ((rfaEvent.getAuctionResumeDateTime()) != null && (rfaEvent.getAuctionResumeTime()) != null) {

							//
							if (!validateResumeDateTime(rfaEvent, model, timeZone, dateTimeFormat, persistObj)) {
								return new ModelAndView("eventSummary", "rfaEvent", rfaEvent);
							}

							// Send Resume notification to suppliers
							try {
								if (persistObj.getSuspensionType() == SuspensionType.DELETE_NOTIFY || persistObj.getSuspensionType() == SuspensionType.KEEP_NOTIFY) {
									try {
										// jmsTemplate.setDefaultDestinationName("QUEUE.EVENT.RESUMED.NOTIFICATION");
										jmsTemplate.send("QUEUE.EVENT.RESUMED.NOTIFICATION", new MessageCreator() {
											@Override
											public Message createMessage(Session session) throws JMSException {
												TextMessage objectMessage = session.createTextMessage();
												objectMessage.setText(rfaEvent.getId());
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
							// Send notification to new suppliers
							try {

								try {
									// jmsTemplate.setDefaultDestinationName("QUEUE.EVENT.ADDI.SUPPLIER.NOTIFICATION");
									jmsTemplate.send("QUEUE.EVENT.ADDI.SUPPLIER.NOTIFICATION", new MessageCreator() {
										@Override
										public Message createMessage(Session session) throws JMSException {
											TextMessage objectMessage = session.createTextMessage();
											objectMessage.setText(rfaEvent.getId());
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
						} else {

							/**
							 * @author Nitin Otageri The resume is probably happening even before the auction start. No
							 *         need to have resume date and time in this case. Just change the status to active
							 *         and return after audit
							 */
							if (persistObj.getEventStart() != null && persistObj.getEventStart().after(new Date())) {

								// Validate Start vs End Date
								if (persistObj.getAuctionType() != AuctionType.FORWARD_DUTCH && persistObj.getAuctionType() != AuctionType.REVERSE_DUTCH) {
									if (persistObj.getEventEnd() != null && persistObj.getEventStart() != null && persistObj.getEventEnd().before(persistObj.getEventStart())) {
										// Event End date cannot be before the start Date
										startDate = persistObj.getEventStart();
										endDate = persistObj.getEventStart();
										LOG.info("EVENT STARTDATE:" + startDate);
										model.addAttribute("error", messageSource.getMessage("rftEvent.error.end.dates", new Object[] { dateTimeFormat.format(endDate), dateTimeFormat.format(startDate) }, Global.LOCALE));
										buildModel(rfaEvent.getId(), model, eventPermissions);
										return new ModelAndView("eventSummary");
									}
								}

								persistObj.setStatus(EventStatus.ACTIVE);
								persistObj.setSuspendRemarks(null);
								persistObj = eventService.updateRfaEvent(persistObj);

								try {
									User user = SecurityLibrary.getLoggedInUser();
									snapShotAuditService.doRfaAudit(rfaEvent, session, persistObj, user, AuditActionType.Resume, "rfaevent.audit.resume", virtualizer);
								} catch (Exception e) {
									LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
								}
								try {
									BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.RESUME, "Event '" + persistObj.getEventId() + "' Resumed", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFA);
									buyerAuditTrailDao.save(buyerAuditTrail);
								} catch (Exception e) {
									LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
								}

								// Resume the Dutch Auction
								if (persistObj.getAuctionType() == AuctionType.FORWARD_DUTCH || persistObj.getAuctionType() == AuctionType.REVERSE_DUTCH) {
									AuctionRules auctionRules = eventService.getAuctionRulesByEventId(persistObj.getId());
									LOG.info("auctionRules:" + eventService.getAuctionRulesByEventId(persistObj.getId()));
									rfaEventService.scheduleAuction(auctionRules);
								}

								// Send Resume notification to suppliers
								try {
									if (persistObj.getSuspensionType() == SuspensionType.DELETE_NOTIFY || persistObj.getSuspensionType() == SuspensionType.KEEP_NOTIFY) {

										try {
											// jmsTemplate.setDefaultDestinationName("QUEUE.EVENT.RESUMED.NOTIFICATION");
											jmsTemplate.send("QUEUE.EVENT.RESUMED.NOTIFICATION", new MessageCreator() {
												@Override
												public Message createMessage(Session session) throws JMSException {
													TextMessage objectMessage = session.createTextMessage();
													objectMessage.setText(rfaEvent.getId());
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
								// Send notification to new suppliers
								try {

									try {
										// jmsTemplate.setDefaultDestinationName("QUEUE.EVENT.ADDI.SUPPLIER.NOTIFICATION");
										jmsTemplate.send("QUEUE.EVENT.ADDI.SUPPLIER.NOTIFICATION", new MessageCreator() {
											@Override
											public Message createMessage(Session session) throws JMSException {
												TextMessage objectMessage = session.createTextMessage();
												objectMessage.setText(rfaEvent.getId());
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

								redir.addFlashAttribute("success", messageSource.getMessage("event.resume.success", new Object[] { persistObj.getEventName() }, Global.LOCALE));
								return new ModelAndView("redirect:/buyer/buyerDashboard");
							} else {
								model.addAttribute("error", messageSource.getMessage("event.resumeDate.error", new Object[] { rfaEvent.getEventName() }, Global.LOCALE));
								return new ModelAndView("eventSummary", "rfaEvent", rfaEvent);
							}
						}

						persistObj.setStatus(EventStatus.ACTIVE);
						persistObj.setSuspendRemarks(null);
						resumeAuction(persistObj);
						persistObj = eventService.updateRfaEvent(persistObj);

						/**
						 * @author Nitin Otageri
						 * 
						 *         <pre>
						 * Schedule only if its a DUTCH AUCTION.
						 * Paagal. Thik se soch ke likha kar na code. Is ke bajhe se woh "Repeat Interval cannot be zero" error aa raha tha.
						 * Ab chai peela.
						 *         </pre>
						 */
						if (persistObj.getAuctionType() == AuctionType.FORWARD_DUTCH || persistObj.getAuctionType() == AuctionType.REVERSE_DUTCH) {
							AuctionRules auctionRules = eventService.getAuctionRulesByEventId(persistObj.getId());
							rfaEventService.scheduleAuction(auctionRules);
						}

						User user = SecurityLibrary.getLoggedInUser();
						snapShotAuditService.doRfaAudit(rfaEvent, session, persistObj, user, AuditActionType.Resume, "rfaEvent.resume", virtualizer);

						try {
							BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.RESUME, "Event '" + persistObj.getEventId() + "' Resumed", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFA);
							buyerAuditTrailDao.save(buyerAuditTrail);
						} catch (Exception e) {
							LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
						}

						try {
							simpMessagingTemplate.convertAndSend("/auctionTopic/" + persistObj.getId(), "RESUME");
						} catch (Exception e) {
							LOG.error("Error while sending suspend event to MQ : " + e.getMessage(), e);
							buildModel(rfaEvent.getId(), model, eventPermissions);
						}
						// Insert event timeline
						try {
							eventService.insertTimeLine(rfaEvent.getId());
						} catch (Exception e) {
							LOG.error(e.getMessage(), e);
						}

						if (StringUtils.checkString(persistObj.getPreviousRequestId()).length() > 0) {
							tatReportService.updateTatReportEventStatus(persistObj.getEventId(), persistObj.getPreviousRequestId(), SecurityLibrary.getLoggedInUserTenantId(), EventStatus.ACTIVE);
						}

						redir.addFlashAttribute("success", messageSource.getMessage("event.resume.success", new Object[] { persistObj.getEventName() }, Global.LOCALE));

					} else if (Boolean.TRUE == persistObj.getEnableSuspensionApproval() && CollectionUtil.isNotEmpty(persistObj.getSuspensionApprovals())) {

						// APPROVAL IS REQUIRED TO RESUME THE EVENT

						if ((rfaEvent.getAuctionResumeDateTime()) != null && (rfaEvent.getAuctionResumeTime()) != null) {
							if (!validateResumeDateTime(rfaEvent, model, timeZone, dateTimeFormat, persistObj)) {
								return new ModelAndView("eventSummary", "rfaEvent", rfaEvent);
							}
						} else {
							/**
							 * @author Nitin Otageri The resume is probably happening even before the auction start. No
							 *         need to have resume date and time in this case. Just change the status to active
							 *         and return after audit
							 */
							if (persistObj.getEventStart() != null && persistObj.getEventStart().after(new Date())) {
								// Validate Start vs End Date
								if (persistObj.getAuctionType() != AuctionType.FORWARD_DUTCH && persistObj.getAuctionType() != AuctionType.REVERSE_DUTCH) {
									if (persistObj.getEventEnd() != null && persistObj.getEventStart() != null && persistObj.getEventEnd().before(persistObj.getEventStart())) {
										// Event End date cannot be before the start Date
										startDate = persistObj.getEventStart();
										endDate = persistObj.getEventStart();
										LOG.info("EVENT STARTDATE:" + startDate);
										model.addAttribute("error", messageSource.getMessage("rftEvent.error.end.dates", new Object[] { dateTimeFormat.format(endDate), dateTimeFormat.format(startDate) }, Global.LOCALE));
										buildModel(rfaEvent.getId(), model, eventPermissions);
										return new ModelAndView("eventSummary");
									}
								}
							} else {
								model.addAttribute("error", messageSource.getMessage("event.resumeDate.error", new Object[] { rfaEvent.getEventName() }, Global.LOCALE));
								return new ModelAndView("eventSummary", "rfaEvent", rfaEvent);
							}
						}

						persistObj = suspensionApprovalService.doApproval(persistObj, session, SecurityLibrary.getLoggedInUser(), virtualizer, model);
						redir.addFlashAttribute("success", "Your Resume Auction request has been sent for approval.");
					}

				} else {
					// Draft Vali Dates Validation...
					if (persistObj.getEventPublishDate() != null && persistObj.getEventPublishDate().before(new Date())) {
						// Published date cannot be in the past
						publishDate = persistObj.getEventPublishDate();
						LOG.info("EVENT PUBLISHDATE:" + publishDate);
						model.addAttribute("error", messageSource.getMessage("rftEvent.error.publishDate", new Object[] { dateTimeFormat.format(publishDate) }, Global.LOCALE));
						buildModel(rfaEvent.getId(), model, eventPermissions);
						return new ModelAndView("eventSummary");
					}
					if (persistObj.getEventStart() != null && persistObj.getEventStart().before(new Date())) {
						// Event start date cannot be in the past
						startDate = persistObj.getEventStart();
						LOG.info("EVENT STARTDATE:" + startDate);
						model.addAttribute("error", messageSource.getMessage("rftEvent.error.startdate", new Object[] { dateTimeFormat.format(startDate) }, Global.LOCALE));
						buildModel(rfaEvent.getId(), model, eventPermissions);
						return new ModelAndView("eventSummary");
					}

					LOG.info("here to check the supplier bq");
					AuctionRules auctionRules = rfaEventService.getLeanAuctionRulesByEventId(persistObj.getId());
					if (persistObj.getAuctionType() != AuctionType.FORWARD_DUTCH && persistObj.getAuctionType() != AuctionType.REVERSE_DUTCH) {
						LOG.info("Prea bid by :" + auctionRules.getPreBidBy());
						if (auctionRules.getPreBidBy() == PreBidByType.BUYER) {
							LOG.info("Prea bid by success :" + auctionRules.getPreBidBy());
							Integer eventSupplierCount = rfaEventSupplierService.getCountOfSupplierByEventId(persistObj.getId());
							LOG.info("Supplier count :" + eventSupplierCount);
							if (eventSupplierCount > 0) {
								LOG.info("Supplier count success :" + eventSupplierCount);
								List<RfaSupplierBq> eventSupplierBqList = rfaSupplierBqService.findRfaSummarySupplierBqbyEventId(persistObj.getId());
								LOG.info("event Supplier bqlist size :" + eventSupplierBqList.size());
								if (eventSupplierBqList.size() == eventSupplierCount) {
									LOG.info("event Supplier bqlist size  and supplier count comparison success:" + eventSupplierBqList.size());
									for (RfaSupplierBq rfaSupplierBq : eventSupplierBqList) {
										if (rfaSupplierBq.getGrandTotal() != null && rfaSupplierBq.getGrandTotal().floatValue() == 0.0f) {
											LOG.info("@@@@@@@@@@@@@@@@@@@@@@@@@@ Bq grand total" + rfaSupplierBq.getGrandTotal());

											model.addAttribute("error", messageSource.getMessage("event.summary.error.emptysupplierbq", new Object[] {}, Global.LOCALE));
											buildModel(rfaEvent.getId(), model, eventPermissions);
											return new ModelAndView("eventSummary");
										}
										// LOG.info("rfa supplier bq toyal after tax not null :" +
										// rfaSupplierBq.getTotalAfterTax());
										// for (RfaSupplierBqItem rfaSupplierBqItem :
										// rfaSupplierBq.getSupplierBqItems()) {
										// LOG.info("supplier bq item order : " + rfaSupplierBqItem.getOrder());
										// if (rfaSupplierBqItem.getOrder() > 0) {
										// LOG.info("supplier bq item order sucess greater then 0 : " +
										// rfaSupplierBqItem.getOrder());
										// if (rfaSupplierBqItem.getTotalAmountWithTax() == null ||
										// (rfaSupplierBqItem.getTotalAmountWithTax() != null &&
										// rfaSupplierBqItem.getTotalAmountWithTax().floatValue() == 0.0f)) {
										// LOG.info("supplier bq item order sucess greater then and value null : " +
										// rfaSupplierBqItem.getTotalAmountWithTax());
										//
										// model.addAttribute("error",
										// messageSource.getMessage("event.summary.error.emptysupplierbq", new Object[]
										// {}, Global.LOCALE));
										// buildModel(rfaEvent.getId(), model, eventPermissions);
										// return new ModelAndView("eventSummary");
										// }
										// }
										// }

									}
								} else {
									LOG.error("event supplier and bq not match for event Id : " + rfaEvent.getId());
									LOG.info("*******************************************************");
									model.addAttribute("error", messageSource.getMessage("event.summary.error.emptysupplierbq", new Object[] {}, Global.LOCALE));
									buildModel(rfaEvent.getId(), model, eventPermissions);
									return new ModelAndView("eventSummary");
								}
							}
						}
					}

					if (persistObj.getAuctionType() != AuctionType.FORWARD_DUTCH && persistObj.getAuctionType() != AuctionType.REVERSE_DUTCH) {
						if (persistObj.getEventEnd() != null && persistObj.getEventStart() != null && persistObj.getEventEnd().before(persistObj.getEventStart())) {
							// Event End date cannot be before the start Date
							startDate = persistObj.getEventStart();
							endDate = persistObj.getEventStart();
							LOG.info("EVENT STARTDATE:" + startDate);
							model.addAttribute("error", messageSource.getMessage("rftEvent.error.end.dates", new Object[] { dateTimeFormat.format(endDate), dateTimeFormat.format(startDate) }, Global.LOCALE));
							buildModel(rfaEvent.getId(), model, eventPermissions);
							return new ModelAndView("eventSummary");
						}

					}

					// If its dutch auction and its start is not relative to another event, create a schedule job for
					// it.
					if (!persistObj.getAuctionStartRelative()) {
						if (persistObj.getAuctionType() == AuctionType.FORWARD_DUTCH || persistObj.getAuctionType() == AuctionType.REVERSE_DUTCH) {
							// Calculate auction end date
							Date auctionEndDate = rfaEventService.calculateEndDateForDutchAuction(auctionRules.getInterval(), auctionRules.getIntervalType(), auctionRules.getDutchAuctionTotalStep(), persistObj.getEventStart());
							LOG.info("Auction End Date : - " + auctionEndDate);
							persistObj.setEventEnd(auctionEndDate);
							LOG.info("Revised Dutch auction end time : " + persistObj.getEventEnd());

							rfaEventService.scheduleAuction(auctionRules);
						}
					}

					if (!validateMeetings(model, dateTimeFormat, persistObj, redir)) {
						return new ModelAndView("redirect:eventSummary/" + rfaEvent.getId());
					}

					persistObj.setSummaryCompleted(Boolean.TRUE);
					persistObj = eventService.updateRfaEvent(persistObj);

					User user = SecurityLibrary.getLoggedInUser();
					// Do approval workflow if applicable
					snapShotAuditService.doRfaAudit(rfaEvent, session, persistObj, user, AuditActionType.Finish, "event.audit.finished", virtualizer);

					try {
						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.FINISH, "Event '" + persistObj.getEventId() + "' sent for Approval.", user.getTenantId(), user, new Date(), ModuleType.RFA);
						buyerAuditTrailDao.save(buyerAuditTrail);
					} catch (Exception e) {
						LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
					}

					Date eventApprovedAndFinishDate = new Date();

					persistObj = approvalService.doApproval(persistObj, session, SecurityLibrary.getLoggedInUser(), virtualizer, eventApprovedAndFinishDate);
					// Send notification to event created
					try {
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

				try {
					eventService.insertTimeLine(rfaEvent.getId());
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}
			}

		} catch (ApplicationException e) {
			LOG.error("Error While Finish the event : :  " + e.getMessage(), e);
			model.addAttribute("error", e.getMessage());
			return new ModelAndView("eventSummary");
		} catch (Exception e) {
			LOG.error("Error While Finish the event : :  " + e.getMessage(), e);
			return new ModelAndView("eventSummary");
		} finally {
			if (virtualizer != null) {
				virtualizer.cleanup();
			}
		}
		return new ModelAndView("redirect:/buyer/buyerDashboard");
	}

	private boolean validateResumeDateTime(RfaEvent rfaEvent, Model model, TimeZone timeZone, SimpleDateFormat dateTimeFormat, RfaEvent persistObj) {
		Date resumeAuctionDateTime = DateUtil.combineDateTime(rfaEvent.getAuctionResumeDateTime(), rfaEvent.getAuctionResumeTime(), timeZone);
		persistObj.setAuctionResumeDateTime(resumeAuctionDateTime);
		LOG.info("Revised auction resume time : " + persistObj.getAuctionResumeDateTime());
		LOG.info("Auction Resume date time :  " + resumeAuctionDateTime + " : timezone : " + timeZone.getDisplayName());

		// Calculate End Date based on Resume Date
		if (persistObj.getAuctionType() == AuctionType.FORWARD_DUTCH || persistObj.getAuctionType() == AuctionType.REVERSE_DUTCH) {
			AuctionRules auctionRules = rfaEventService.getLeanAuctionRulesByEventId(rfaEvent.getId());
			int remainingStep = ((auctionRules.getDutchAuctionTotalStep() - auctionRules.getDutchAuctionCurrentStep()) + 1);
			LOG.info("Remaining step : - " + remainingStep);
			Date auctionEndDate = rfaEventService.calculateEndDateForDutchAuction(auctionRules.getInterval(), auctionRules.getIntervalType(), remainingStep, resumeAuctionDateTime);
			LOG.info("Resume auctionEndDate : - " + auctionEndDate);
			persistObj.setEventEnd(auctionEndDate);
			LOG.info("Revised Dutch auction end time : " + persistObj.getEventEnd());
		}

		// Validate if End date is after resume date(validating nonDutch Auction while resuming the
		// event)
		if (persistObj.getAuctionType() != AuctionType.FORWARD_DUTCH && persistObj.getAuctionType() != AuctionType.REVERSE_DUTCH) {
			if (persistObj.getEventEnd().before(resumeAuctionDateTime)) {
				LOG.info("MEssage : " + messageSource.getMessage("event.endDate.error", new Object[] { dateTimeFormat.format(persistObj.getEventEnd()), dateTimeFormat.format(resumeAuctionDateTime) }, Global.LOCALE));
				model.addAttribute("error", messageSource.getMessage("event.endDate.error", new Object[] { dateTimeFormat.format(persistObj.getEventEnd()), dateTimeFormat.format(resumeAuctionDateTime) }, Global.LOCALE));
				return false;
			}
			if ((resumeAuctionDateTime).before(new Date())) {
				LOG.info("MEssage : " + messageSource.getMessage("event.startDate.error", new Object[] { dateTimeFormat.format(resumeAuctionDateTime) }, Global.LOCALE));
				model.addAttribute("error", messageSource.getMessage("event.startDate.error", new Object[] { dateTimeFormat.format(resumeAuctionDateTime) }, Global.LOCALE));
				return false;
			}
		}

		// validating Dutch Auction while resuming the event
		if (persistObj.getAuctionType() == AuctionType.FORWARD_DUTCH || persistObj.getAuctionType() == AuctionType.REVERSE_DUTCH) {
			if ((resumeAuctionDateTime).before(new Date())) {
				LOG.info("MEssage : " + messageSource.getMessage("event.startDate.error", new Object[] { dateTimeFormat.format(resumeAuctionDateTime) }, Global.LOCALE));
				model.addAttribute("error", messageSource.getMessage("event.startDate.error", new Object[] { dateTimeFormat.format(resumeAuctionDateTime) }, Global.LOCALE));
				return false;
			}
		}
		return true;
	}

	private boolean validateMeetings(Model model, SimpleDateFormat dateTimeFormat, RfaEvent persistObj, RedirectAttributes redir) {
		boolean valid = true;
		if (persistObj.getMeetingReq() != null && persistObj.getMeetingReq()) {
			// Event start date cannot be in the past
			List<Date> meetingDates = rfaEventService.getAllMeetingDateByEventId(persistObj.getId());

			LOG.info("meeting Dates list size :" + meetingDates.size());
			if (CollectionUtil.isNotEmpty(meetingDates)) {
				for (Date meetingDate : meetingDates) {
					LOG.info("Meeting Date : " + meetingDate + " : publish Date : " + persistObj.getEventPublishDate() + " : Current Date : " + new Date());
					if ((persistObj.getEventPublishDate() != null && meetingDate != null && meetingDate.before(persistObj.getEventPublishDate())) || meetingDate.before(new Date())) {
						LOG.info("Meeting Date error : " + meetingDate + " : publish Date : " + persistObj.getEventPublishDate() + " : Current Date : " + new Date());
						redir.addAttribute("error", messageSource.getMessage("rft.meeting.error.eventpublishdate", new Object[] { dateTimeFormat.format(meetingDate), dateTimeFormat.format(persistObj.getEventPublishDate()) }, Global.LOCALE));
						// EventPermissions eventPermissions =
						// rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(),
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
				List<RfaEventMeeting> meetingList = rfaEventMeetingService.findMeetingWithoutSuplliersByEventId(persistObj.getId());
				if (CollectionUtil.isNotEmpty(meetingList)) {
					LOG.info("Meeting does not have suppliers");
					List<String> meetings = new ArrayList<String>();
					for (RfaEventMeeting meeting : meetingList) {
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
			RfaEventDocument docs = documentService.findRfaEventdocsById(id);
			super.buildDocumentFile(response, docs);
		} catch (Exception e) {
			LOG.error("Error while downloaded RFA event Document : " + e.getMessage(), e);
		}
	}

	@RequestMapping(value = "/downloadEventMeetingDocument/{id}", method = RequestMethod.GET)
	public void downloadEventMeetingDocument(@PathVariable String id, HttpServletResponse response) throws IOException {
		try {
			RfaEventMeetingDocument docs = meetingService.getRfaEventMeetingDocument(id);
			buildMeetingFile(response, docs);
		} catch (Exception e) {
			LOG.error("Error while downloaded RFA event Document : " + e.getMessage(), e);
		}
	}

	@SuppressWarnings("unused")
	private void scheduleAuction1(AuctionRules auctionRulesData) throws ApplicationException {
		try {
			AuctionRules auctionRules = eventService.getAuctionRulesWithEventById(auctionRulesData.getId());
			LOG.info("auctionRules:" + auctionRules.toLogString());
			// RfaEvent event = auctionRules.getEvent();
			schedulerFactoryBean.getScheduler().pauseAll();
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
					LOG.info("Schedule a job If Minute  new time: ");
				} else if (auctionRules.getIntervalType() == DurationMinSecType.SECONDS) {
					triggerBean.setStartTime(auctionRules.getEvent().getEventStart());
					triggerBean.setRepeatInterval(auctionRules.getInterval() * 1000);
					triggerBean.setRepeatCount(auctionRules.getDutchAuctionTotalStep());
					LOG.info("Schedule a job If Seconds new Time: ");
				}
				triggerBean.afterPropertiesSet();
				trigger = (SimpleTriggerImpl) triggerBean.getObject();
				schedulerFactoryBean.getScheduler().scheduleJob(jobDetail, trigger);
			} else {
				if (auctionRules.getIntervalType() == DurationMinSecType.MINUTE) {
					if (auctionRules.getEvent().getAuctionResumeDateTime() != null) {
						trigger.setStartTime(auctionRules.getEvent().getAuctionResumeDateTime());
						LOG.info("here after suspened" + auctionRules.getEvent().getAuctionResumeDateTime());
					} else {
						trigger.setStartTime(auctionRules.getEvent().getEventStart());
						LOG.info("normal event");
					}
					trigger.setRepeatInterval(auctionRules.getInterval() * 1000 * 60);
					trigger.setRepeatCount(auctionRules.getDutchAuctionTotalStep());
					LOG.info("ReSchedule a job If Minute : ");
				} else if (auctionRules.getIntervalType() == DurationMinSecType.SECONDS) {
					if (auctionRules.getEvent().getAuctionResumeDateTime() != null) {
						trigger.setStartTime(auctionRules.getEvent().getAuctionResumeDateTime());
						LOG.info("here after suspened sec" + auctionRules.getEvent().getAuctionResumeDateTime());
					} else {
						trigger.setStartTime(auctionRules.getEvent().getEventStart());
					}
					trigger.setRepeatInterval(auctionRules.getInterval() * 1000);
					trigger.setRepeatCount(auctionRules.getDutchAuctionTotalStep());
					LOG.info("ReSchedule a job If Seconds : ");
				}
				schedulerFactoryBean.getScheduler().rescheduleJob(triggerKey, trigger);
			}
		} catch (Exception e) {
			LOG.error("Error in scheduling auction : " + e.getMessage(), e);
			throw new ApplicationException("Error while scheduling auction : " + e.getMessage(), e);
		} finally {
			try {
				schedulerFactoryBean.getScheduler().resumeAll();
			} catch (SchedulerException e) {
				LOG.fatal("Error in resuming Schedule jobs : " + e.getMessage(), e);
			}
		}

	}

	private void resumeAuction(RfaEvent event) {

		AuctionRules auctionRules = eventService.getAuctionRulesByEventId(event.getId());

		RfaEvent eventObj = rfaEventService.getRfaEventByeventId(event.getId());
		if (event.getAuctionType() == AuctionType.FORWARD_DUTCH || event.getAuctionType() == AuctionType.REVERSE_DUTCH) {
			BigDecimal differenceAmount = BigDecimal.ZERO;
			LOG.info("AUction Rules : " + auctionRules.getDutchMinimumPrice());
			if (Boolean.TRUE == auctionRules.getFowardAuction()) {
				differenceAmount = auctionRules.getDutchAuctionCurrentStepAmount().subtract(auctionRules.getDutchMinimumPrice());
			} else {
				differenceAmount = auctionRules.getDutchMinimumPrice().subtract(auctionRules.getDutchAuctionCurrentStepAmount());
			}
			LOG.info(" differenceAmount : " + differenceAmount);
			// int amountDivision = (differenceAmount.divide(auctionRules.getAmountPerIncrementDecrement(),
			// eventObj.getDecimal())).intValue();;
			// int totalDuration = amountDivision * auctionRules.getInterval();
			// event.setAuctionDuration(totalDuration + auctionRules.getInterval());

			// int amountDivision = (differenceAmount.divide(auctionRules.getAmountPerIncrementDecrement(),
			// Integer.parseInt(eventObj.getDecimal()))).intValue();
			// int totalDuration = amountDivision * auctionRules.getInterval();
			// event.setAuctionDuration(totalDuration + auctionRules.getInterval());

			BigDecimal amountDivide = (differenceAmount.divide(auctionRules.getAmountPerIncrementDecrement(), (event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2)));
			LOG.info("RESUME amountDivide : " + amountDivide);
			int amountDivision = amountDivide.setScale(0, RoundingMode.CEILING).intValue();
			LOG.info("RESUME amount : " + amountDivision);
			int totalDuration = ((amountDivision * auctionRules.getInterval()) + auctionRules.getInterval());
			LOG.info("RESUME total duration : " + event.getEventEnd());
			event.setAuctionDuration(totalDuration);
			auctionRules.setDutchAuctionTotalStep(totalDuration / auctionRules.getInterval());
			LOG.info("RESUME total step : " + auctionRules.getDutchAuctionTotalStep());

			// Date resumeDate = event.getAuctionResumeDateTime();
			// Calendar cal = Calendar.getInstance();
			// cal.setTime(resumeDate);
			// if (event.getAuctionDurationType() == DurationMinSecType.MINUTE) {
			// cal.add(Calendar.MINUTE, (event.getAuctionDuration()));
			// } else {
			// cal.add(Calendar.SECOND, (event.getAuctionDuration()));
			// }
			// event.setEventEnd(cal.getTime());
			LOG.info("Revised auction end time : " + event.getEventEnd());
			eventObj = eventService.updateRfaEvent(event);

			// auctionRules.setDutchAuctionTotalStep(event.getAuctionDuration() / auctionRules.getInterval());
			auctionRules.setDutchAuctionCurrentStep(0);
			auctionRules.setDutchStartPrice(auctionRules.getDutchAuctionCurrentStepAmount());
			auctionRules.setDutchAuctionCurrentStepAmount(auctionRules.getDutchAuctionCurrentStepAmount());
		}
		auctionRules.setEvent(eventObj);
		auctionRules.setAuctionStarted(Boolean.FALSE);
		eventService.updateAuctionRules(auctionRules);

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
	}

	@RequestMapping(value = "cancelEvent", method = RequestMethod.POST)
	public ModelAndView cancelAuction(@ModelAttribute("event") RfaEvent event, Model model, RedirectAttributes redirectAttributes, HttpSession session) {
		// RfaEvent persistObj = eventService.getRfaEventByeventId(rfaEvent.getId());
		model.addAttribute("eventPermissions", eventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), event.getId()));
		AuctionRules auctionRules = eventService.getAuctionRulesByEventId(event.getId());
		EventPermissions eventPermissions = rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), event.getId());
		buildModel(event.getId(), model, eventPermissions);
		JobKey jobKey = new JobKey("JOB" + auctionRules.getId(), "DUTCHAUCTION");
		JRSwapFileVirtualizer virtualizer = null;
		try {
			// Virtualizar - To increase the performance
			virtualizer = new JRSwapFileVirtualizer(200, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);
			LOG.info("at Starting stage date time  : " + new Date());
			if (schedulerFactoryBean.getScheduler().checkExists(jobKey)) {
				LOG.info("delete the job for event : " + event.getId() + " :job key  " + jobKey);
				schedulerFactoryBean.getScheduler().pauseJob(jobKey);
				schedulerFactoryBean.getScheduler().deleteJob(jobKey);
			}
			event = eventService.cancelEvent(event, session, virtualizer, SecurityLibrary.getLoggedInUser());
			tatReportService.updateTatReportEventStatus(event.getEventId(), SecurityLibrary.getLoggedInUserTenantId(), EventStatus.CANCELED);

			redirectAttributes.addFlashAttribute("success", messageSource.getMessage("event.cancel.success", new Object[] { StringUtils.checkString(event.getEventName()).length() == 0 ? event.getEventId() : event.getEventName() }, Global.LOCALE));
			LOG.info("Dutch auction (eventId): " + event.getId() + " Cancel by : " + SecurityLibrary.getLoggedInUser());
		} catch (Exception e) {
			LOG.error("Error while event cancel " + e.getMessage(), e);
			model.addAttribute("error", "Error during event cancel " + e.getMessage());

			try {
				if (schedulerFactoryBean.getScheduler().checkExists(jobKey)) {
					schedulerFactoryBean.getScheduler().resumeJob(jobKey);
				}
			} catch (SchedulerException e1) {
				LOG.error("Error during suspend auction by buyer : " + e1.getMessage(), e1);
				buildModel(event.getId(), model, eventPermissions);
				return new ModelAndView("eventSummary");
			}
			return new ModelAndView("eventSummary", "event", event);
		} finally {
			if (virtualizer != null) {
				virtualizer.cleanup();
			}
		}
		return new ModelAndView("redirect:/buyer/buyerDashboard");
	}

	@RequestMapping(path = "/suspendEvent", method = RequestMethod.POST)
	public ModelAndView suspendEvent(@ModelAttribute RfaEvent event, @RequestParam(required = false) Boolean auctionConsole, RedirectAttributes redirectAttributes, Model model, HttpSession session) {
		JRSwapFileVirtualizer virtualizer = null;
		try {
			virtualizer = new JRSwapFileVirtualizer(300, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);
			EventPermissions eventPermissions = rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), event.getId());
			RfaEvent dbEvent = buildModel(event.getId(), model, eventPermissions);

			if (dbEvent.getStatus() != EventStatus.ACTIVE && dbEvent.getStatus() != EventStatus.APPROVED) {
				// model.addAttribute("error", "Auction cannot be suspended as it is in " +
				// dbEvent.getStatus().toString() + " state.");
				model.addAttribute("error", messageSource.getMessage("rfasummary.auction.cant.suspended", new Object[] { dbEvent.getStatus() != null ? dbEvent.getStatus().toString() : "" }, Global.LOCALE));
				return new ModelAndView("eventSummary");
			}

			if (StringUtils.checkString(event.getId()).length() > 0) {
				rfaEventService.suspendAllRelativeEvents(event.getId());
				LOG.info("Auction Type : " + event.getAuctionType());
				if (event.getAuctionType() == AuctionType.FORWARD_DUTCH || event.getAuctionType() == AuctionType.REVERSE_DUTCH) {
					event = eventService.suspendDutchAuction(event);
					try {
						simpMessagingTemplate.convertAndSend("/auctionTopic/" + event.getId(), "SUSPENDED");
					} catch (Exception e) {
						LOG.error("Error while sending suspend event to MQ : " + e.getMessage(), e);
						buildModel(event.getId(), model, eventPermissions);
					}
				} else {
					event = eventService.suspendEnglishAuction(event);
					try {
						simpMessagingTemplate.convertAndSend("/auctionTopic/" + event.getId(), "SUSPENDED");
					} catch (Exception e) {
						LOG.error("Error while sending suspend event to MQ : " + e.getMessage(), e);
						buildModel(event.getId(), model, eventPermissions);
					}
				}
			}

			// Bcoz of Broker's error this code is not executing ..So separated code for broker and audit
			if (event != null) {
				LOG.info("---------------------------------BEFORE AUDIT------------------------------------------");
				RfaEvent persistObject = rfaEventService.getRfaEventByeventId(event.getId());

				List<RfaEventSuspensionApproval> approvalList = persistObject.getSuspensionApprovals();
				if (CollectionUtil.isNotEmpty(approvalList)) {
					for (RfaEventSuspensionApproval approval : approvalList) {
						approval.setDone(false);
						approval.setActive(false);
						for (RfaSuspensionApprovalUser user : approval.getApprovalUsers()) {
							user.setActionDate(null);
							user.setApprovalStatus(ApprovalStatus.PENDING);
							user.setRemarks(null);
							user.setActionDate(null);
						}
					}
				}

				rfaEventDao.update(persistObject);

				User user = SecurityLibrary.getLoggedInUser();
				snapShotAuditService.doRfaAudit(event, session, persistObject, user, AuditActionType.Suspend, "rfaevent.audit.suspended", virtualizer);
				LOG.info("---------------------------------AFTER AUDIT-------------------------------------------------------");

				try {
					LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.SUSPEND, "Event '" + persistObject.getEventId() + "' is suspended", persistObject.getCreatedBy().getTenantId(), persistObject.getCreatedBy(), new Date(), ModuleType.RFA);
					buyerAuditTrailDao.save(buyerAuditTrail);
					LOG.info("--------------------------AFTER AUDIT---------------------------------------");
				} catch (Exception e) {
					LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
				}
			}

			try {
				// Send Suspend notification to suppliers

				try {
					// jmsTemplate.setDefaultDestinationName("QUEUE.EVENT.SUSPEND.NOTIFICATION");
					jmsTemplate.send("QUEUE.EVENT.SUSPEND.NOTIFICATION", new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							TextMessage objectMessage = session.createTextMessage();
							objectMessage.setText(dbEvent.getId());
							return objectMessage;
						}
					});
				} catch (Exception e) {
					LOG.error("Error sending message to queue : " + e.getMessage(), e);
				}

				if (Boolean.TRUE == event.getRevertLastBid() && (event.getAuctionType() == AuctionType.FORWARD_ENGISH || event.getAuctionType() == AuctionType.REVERSE_ENGISH)) {
					String timeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
					if (timeZone == null) {
						timeZone = "GMT+8:00";
					}
					String url = APP_URL + "/buyer/RFA/viewSummary/" + event.getId();
					super.sendEventSuspendedRevertBidNotifications(event, getEventType(), event.getRevertBidUser(), timeZone, url);
				}

				// super.sendEventSupendedNotificationEmails(event, getEventType());
			} catch (Exception e) {
				LOG.error("Error while sending notification to event suspend " + e.getMessage(), e);
			}

			model.addAttribute("eventPermissions", eventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), event.getId()));
			//
			// byte[] summarySnapshot = null;
			// try {
			//
			// JasperPrint eventSummary = rfaEventService.getEvaluationSummaryPdf(event,
			// SecurityLibrary.getLoggedInUser(), (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY));
			// summarySnapshot = JasperExportManager.exportReportToPdf(eventSummary);
			// } catch (JRException e) {
			// LOG.error("Error while Store summary PDF as byte : " + e.getMessage(), e);
			// }
			//
			// RfaEventAudit audit = new RfaEventAudit(SecurityLibrary.getLoggedInUser().getBuyer(), event,
			// SecurityLibrary.getLoggedInUser(), new java.util.Date(), AuditActionType.Suspend,
			// messageSource.getMessage("rfaevent.audit.suspended", new Object[] { event.getEventName(),
			// event.getSuspendRemarks() }, Global.LOCALE), summarySnapshot);
			// eventAuditService.save(audit);

			// LOG.info("---------------------------------BEFORE AUDIT------------------------------------------");
			// RfaEvent persistObject = rfaEventService.getRfaEventByeventId(event.getId());
			//
			// User user = SecurityLibrary.getLoggedInUser();
			// snapShotAuditService.doRfaAudit(event, session, persistObject, user, AuditActionType.Suspend,
			// "rfaevent.audit.suspended", virtualizer);
			// LOG.info("---------------------------------AFTER
			// AUDIT-------------------------------------------------------");

			tatReportService.updateTatReportEventStatusById(event.getId(), SecurityLibrary.getLoggedInUserTenantId(), EventStatus.SUSPENDED);

			redirectAttributes.addFlashAttribute("success", messageSource.getMessage("event.suspended.success", new Object[] { event.getEventName() }, Global.LOCALE));
		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
			EventPermissions eventPermissions = rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), event.getId());
			buildModel(event.getId(), model, eventPermissions);
			// model.addAttribute("error", "Error while suspending Event");
			model.addAttribute("error", messageSource.getMessage("rfasummary.while.suspending.event", new Object[] {}, Global.LOCALE));
			return new ModelAndView("eventSummary");
		} finally {
			if (virtualizer != null) {
				virtualizer.cleanup();
			}
		}

		if (auctionConsole != null) {
			if (auctionConsole == Boolean.TRUE) {
				if (event.getAuctionType() == AuctionType.FORWARD_DUTCH || event.getAuctionType() == AuctionType.REVERSE_DUTCH) {
					return new ModelAndView("redirect:/auction/dutchAuctionConsole/" + event.getId());
				} else {
					return new ModelAndView("redirect:/buyer/englishAuctionConsole/" + event.getId());
				}
			}
		}
		return new ModelAndView("redirect:/buyer/buyerDashboard");
	}

	@RequestMapping(value = "/eventTeamMembersList/{eventId}", method = RequestMethod.GET)
	public ResponseEntity<TableData<EventTeamMember>> eventTeamMembersList(@PathVariable(name = "eventId") String eventId, TableDataInput input) {
		TableData<EventTeamMember> data = null;
		try {
			data = new TableData<EventTeamMember>(eventService.getPlainTeamMembersForEvent(eventId));
			data.setDraw(input.getDraw());
		} catch (Exception e) {
			LOG.error("Error while loading draft list : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<EventTeamMember>>(data, HttpStatus.OK);
	}

	@RequestMapping(path = "/updateEventApproval", method = RequestMethod.POST)
	public String updateEventApproval(@ModelAttribute RfaEvent event, RedirectAttributes redir, HttpSession session) {
		JRSwapFileVirtualizer virtualizer = null;
		try {
			virtualizer = new JRSwapFileVirtualizer(300, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);
			event = eventService.updateEventApproval(event, SecurityLibrary.getLoggedInUser());
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
		return "redirect:/buyer/" + RfxTypes.RFA.name() + "/eventSummary/" + event.getId();
	}

	@RequestMapping(path = "/summaryEnvelop", method = RequestMethod.POST)
	public ResponseEntity<EnvelopePojo> summaryEnvelop(@RequestParam("envelopeId") String envelopeId) {
		HttpHeaders headers = new HttpHeaders();

		RfaEnvelop rfaEnvelop = envelopeService.getRfaEnvelopById(envelopeId);

		EnvelopePojo envelope = new EnvelopePojo(rfaEnvelop);
		@SuppressWarnings("unchecked")
		List<RfaEvaluatorUser> assignedEvaluators = (List<RfaEvaluatorUser>) envelope.getAssignedEvaluators();
		List<User> evaluators = new ArrayList<User>();

		@SuppressWarnings("unchecked")
		List<RfaEnvelopeOpenerUser> assignedOpeners = (List<RfaEnvelopeOpenerUser>) envelope.getAssignedOpeners();
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
			for (RfaEvaluatorUser evalUser : assignedEvaluators) {
				evalUser.setUser(evalUser.getUser().createStripCopy());
			}
		}

		if (CollectionUtil.isNotEmpty(envelope.getAssignedOpeners())) {
			for (RfaEnvelopeOpenerUser openerUser : assignedOpeners) {
				openerUser.setUser(openerUser.getUser().createStripCopy());
			}
		}
		// for (User user : userList) {
		// boolean found = false;
		// for (RfaEvaluatorUser evalUser : assignedEvaluators) {
		// if (evalUser.getUser().getId().equals(user.getId())) {
		// found = true;
		// break;
		// }
		// }
		// if (!found) {
		// evaluators.add(user.createStripCopy());
		// }
		// }

		for (RfaEvaluatorUser evalUser : assignedEvaluators) {
			User u = new User(evalUser.getUser().getId(), evalUser.getUser().getLoginId(), evalUser.getUser().getName(), evalUser.getUser().getCommunicationEmail(), evalUser.getUser().getEmailNotifications(), evalUser.getUser().getTenantId(), evalUser.getUser().isDeleted());
			if (!evaluators.contains(u)) {
				evaluators.add(u);
			}
		}

		for (RfaEnvelopeOpenerUser openerUser : assignedOpeners) {
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

		// List<UserPojo> userListSumm = userService.fetchAllUsersForTenant(SecurityLibrary.getLoggedInUserTenantId(),
		// "", UserType.NORMAL_USER);
		// for (UserPojo user : userListSumm) {
		// User u = new User(user.getId(), user.getLoginId(), user.getName(), user.getCommunicationEmail(),
		// user.getTenantId(), user.isDeleted());
		// if (!evaluators.contains(u)) {
		// evaluators.add(u);
		// }
		// }

		envelope.setEvaluators(evaluators);
		envelope.setOpeners(openersList);

		return new ResponseEntity<EnvelopePojo>(envelope, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/updateEvaluators", method = RequestMethod.POST)
	public String summaryEnvelop(@ModelAttribute(name = "envelop") RfaEnvelop envelop, @RequestParam("eventId") String eventId, Model model, RedirectAttributes redir) {
		try {

			RfaEvent event = rfaEventService.loadRfaEventForSummeryById(eventId);
			EventPermissions eventPermissions = rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
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

			rfaEnvelopService.updateEnvelope(envelop, eventId);
			redir.addAttribute("success", "Envelope updated successfully");
		} catch (Exception e) {
			LOG.error("Error during envelope save : " + e.getMessage(), e);
			// model.addAttribute("error", "Error during envelope saving : " + e.getMessage());
			model.addAttribute("error", messageSource.getMessage("rfasummary.during.envelope.saving", new Object[] { e.getMessage() }, Global.LOCALE));
			EventPermissions eventPermissions = rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
			RfaEvent event = buildModel(eventId, model, eventPermissions);
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
	public ResponseEntity<RfaEventMeeting> summaryMeeting(@RequestParam("meetingId") String meetingId) {
		HttpHeaders headers = new HttpHeaders();
		LOG.info("meetingId :" + meetingId);
		RfaEventMeeting meeting = meetingService.getRfaMeetingById(meetingId);
		meeting.setCreatedBy(null);
		meeting.setModifiedBy(null);
		LOG.info("meeting.getId() :" + meeting.getId());
		return new ResponseEntity<RfaEventMeeting>(meeting, headers, HttpStatus.OK);
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

		TimeZone timeZone = TimeZone.getDefault();
		String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}
		JRSwapFileVirtualizer virtualizer = null;

		try {
			// Virtualizar - To increase the performance
			virtualizer = new JRSwapFileVirtualizer(300, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);
			String filename = "EvaluationSummary.pdf";
			JasperPrint jasperPrint = rfaEventService.generatePdfforEvaluationSummary(eventId, timeZone, virtualizer);
			if (jasperPrint != null) {
				streamReport(jasperPrint, filename, response);
			}

			try {
				RfaEventAudit audit = new RfaEventAudit();
				audit.setActionBy(SecurityLibrary.getLoggedInUser());
				audit.setActionDate(new Date());
				audit.setDescription("Evaluation Summary report downloaded ");
				RfaEvent event = new RfaEvent();
				event.setId(eventId);
				audit.setEvent(event);
				audit.setAction(AuditActionType.Download);
				eventAuditService.save(audit);
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
			try {
				RfaEvent rfaEvent = rfaEventService.getPlainEventById(eventId);
				LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, " Evaluation Summary report downloaded for Event '" + rfaEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFA);
				buyerAuditTrailDao.save(buyerAuditTrail);
				LOG.info("--------------------------AFTER AUDIT---------------------------------------");
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
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
			virtualizer = new JRSwapFileVirtualizer(300, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);
			String filename = "unknowEventSummary.pdf";
			RfaEvent event = rfaEventService.getRfaEventByeventId(eventId);
			if (event.getEventId() != null) {
				filename = (event.getEventId()).replace("/", "-") + " summary.pdf";
			}
			JasperPrint jasperPrint = rfaEventService.getEvaluationSummaryPdf(event, SecurityLibrary.getLoggedInUser(), (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), virtualizer);
			if (jasperPrint != null) {
				streamReport(jasperPrint, filename, response);
			}
			try {
				RfaEventAudit audit = new RfaEventAudit();
				audit.setAction(AuditActionType.Download);
				audit.setActionBy(SecurityLibrary.getLoggedInUser());
				audit.setActionDate(new Date());
				audit.setDescription("Event Summary downloaded");
				audit.setEvent(event);
				eventAuditService.save(audit);
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
			try {
				LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Event '" + event.getEventId() + "' Summary downloaded", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFA);
				buyerAuditTrailDao.save(buyerAuditTrail);
				LOG.info("--------------------------AFTER AUDIT---------------------------------------");
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
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
			virtualizer = new JRSwapFileVirtualizer(500, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);

			String filename = "unknowEventSummary.pdf";
			RfaEvent event = rfaEventService.getRfaEventByeventId(eventId);
			if (event.getEventId() != null) {
				filename = (event.getEventId()).replace("/", "-") + " AuctionHistory.pdf";
			}
			JasperPrint jasperPrint = rfaEventService.getEventAuditPdf(event, SecurityLibrary.getLoggedInUser(), (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), virtualizer);
			if (jasperPrint != null) {
				streamReport(jasperPrint, filename, response);
			}
			try {
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Audit Trail is downloaded ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFA);
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
			String filename = "RFA" + title + "MeetingSummary.pdf";
			JasperPrint jasperPrint = rfaEventService.getMeetingAttendanceReport(eventId, meetingId, session);
			if (jasperPrint != null) {
				streamReport(jasperPrint, filename, response);
			}
		} catch (Exception e) {
			LOG.error("Could not Download Evaluation Summary Report. " + e.getMessage(), e);
		}
	}

	@RequestMapping(path = "/downloadBuyerAuctionReport/{eventId}", method = RequestMethod.POST)
	public void downloadBuyerAuctionReport(@PathVariable("eventId") String eventId, HttpServletResponse response, HttpSession session) throws Exception {
		JRSwapFileVirtualizer virtualizer = null;
		try {
			virtualizer = new JRSwapFileVirtualizer(300, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);
			String filename = "BuyerAuctionReport.pdf";
			JasperPrint jasperPrint = rfaEventService.getBuyerAuctionReport(eventId, (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), virtualizer);
			if (jasperPrint != null) {
				streamReport(jasperPrint, filename, response);
			}
		} catch (Exception e) {
			LOG.error("Could not Download Evaluation Summary Report. " + e.getMessage(), e);
		} finally {
			if (virtualizer != null) {
				virtualizer.cleanup();
			}
		}
	}

	private void streamReport(JasperPrint jasperPrint, String filename, HttpServletResponse response) throws JRException, IOException {
		response.setContentType("application/pdf");
		response.addHeader("Content-Disposition", "attachment; filename=" + filename);

		JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
		response.getOutputStream().flush();
		response.getOutputStream().close();

	}

	@RequestMapping(path = "/concludeEvent", method = RequestMethod.POST)
	public String concludeEvent(@ModelAttribute(name = "event") RfaEvent event, Model model, RedirectAttributes redir) {
		try {
			event = rfaEventService.concludeRfaEvent(event, SecurityLibrary.getLoggedInUser());
			try {
				RfaEventAudit audit = new RfaEventAudit();
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
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CONCLUDE, "Event ' " + event.getEventId() + "' is concluded", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFA);
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
			model.addAttribute("error", messageSource.getMessage("rfasummary.during.save.eventconclude", new Object[] { e.getMessage() }, Global.LOCALE));
			EventPermissions eventPermissions = rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), event.getId());
			buildModel(event.getId(), model, eventPermissions);
			return "eventSummary";

		}
		return "redirect:eventSummary/" + event.getId();
	}

	@RequestMapping(path = "/copyEventTo", method = RequestMethod.POST)
	public String copyEventTo(@ModelAttribute(name = "event") RfaEvent event, @RequestParam(name = "businessUnitId", required = false) String businessUnitId, @RequestParam(name = "idRfxTemplate", required = false) String idRfxTemplate, @RequestParam(name = "selectedRfxType", required = false) RfxTypes selectedRfxType, @RequestParam(name = "auctionType", required = false) AuctionType auctionType, @RequestParam(name = "invitedSupp", required = false) String[] invitedSupp, @RequestParam(name = "bqId", required = false) String bqId, @RequestParam("concludeRemarks") String concludeRemarks, Model model, RedirectAttributes redir) {
		String newEventId = "";
		try {
			newEventId = rfaEventService.createNextEvent(event, selectedRfxType, auctionType, bqId, SecurityLibrary.getLoggedInUser(), idRfxTemplate, businessUnitId, invitedSupp, concludeRemarks);
			RfaEvent oldEvent = rfaEventService.getRfaEventByeventId(event.getId());
			if (StringUtils.checkString(newEventId).length() > 0) {
				rfaEventService.updateEventPushedDate(event.getId());
				redir.addAttribute("success", "New event created successfully");
			} else {
				try {
					RfaEventAudit RfaAudit = new RfaEventAudit();
					RfaAudit.setAction(AuditActionType.Conclude);
					RfaAudit.setActionBy(SecurityLibrary.getLoggedInUser());
					RfaAudit.setActionDate(new Date());
					RfaAudit.setDescription("Event " + oldEvent.getEventName() + " is concluded");
					RfaAudit.setEvent(oldEvent);
					eventAuditService.save(RfaAudit);
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}
				try {
					LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CONCLUDE, "Event ' " + oldEvent.getEventId() + "' is concluded", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFA);
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
		List<RfaSupplierMeetingAttendance> list = supplierRfaMeetingAttendanceService.getAllSupplierAttendance(meetingId, eventId);
		List<Supplier> suppliers = meetingService.getAllSuppliersByMeetId(meetingId);
		if (CollectionUtil.isNotEmpty(suppliers) && CollectionUtil.isNotEmpty(list)) {
			result = new ArrayList<SupplierMeetingAttendancePojo>();
			for (RfaSupplierMeetingAttendance atta : list) {
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
		List<RfaSupplierMeetingAttendance> list = new ArrayList<RfaSupplierMeetingAttendance>();
		for (SupplierMeetingAttendancePojo pojo : meetingAttendances) {
			if (StringUtils.checkString(pojo.getName()).length() > 0) {
				LOG.info("Details : " + pojo.toLogString());
				RfaSupplierMeetingAttendance attendance = new RfaSupplierMeetingAttendance();
				attendance.setId(pojo.getId());
				attendance.setName(pojo.getName());
				attendance.setDesignation(pojo.getDesignation());
				attendance.setMobileNumber(pojo.getMobileNumber());
				attendance.setRemarks(pojo.getRemarks());
				attendance.setAttended(pojo.getAttended());
				attendance.setMeetingAttendanceStatus(pojo.getMeetingAttendanceStatus());

				RfaEvent event = new RfaEvent();
				event.setId(pojo.getEventId());
				attendance.setRfaEvent(event);
				RfaEventMeeting meeting = new RfaEventMeeting();
				meeting.setId(pojo.getMeetingId());
				attendance.setRfaEventMeeting(meeting);
				Supplier supplier = new Supplier();
				supplier.setId(pojo.getSupplierId());
				attendance.setSupplier(supplier);
				list.add(attendance);
			}
		}

		if (CollectionUtil.isNotEmpty(list)) {
			meetingService.saveOrUpdateAttendance(list);
			header.add("success", "Succesfully updated Attendance details");
		}
		return new ResponseEntity<String>("", header, HttpStatus.OK);
	}

	@RequestMapping(path = "/completMeeting", method = RequestMethod.POST)
	public ResponseEntity<String> completMeeting(@RequestParam String meetingId, @RequestParam String eventId) throws JsonProcessingException {
		HttpHeaders header = new HttpHeaders();
		try {
			RfaEventMeeting meeting = meetingService.getMeetingForIdAndEvent(meetingId, eventId);
			if (meeting != null) {
				meeting.setStatus(MeetingStatus.COMPLETED);
				meeting.setModifiedBy(SecurityLibrary.getLoggedInUser());
				meeting.setModifiedDate(new Date());
				meetingService.updateRfaMeeting(meeting);
			}
			header.add("success", "Meeting closed Succesfully");
		} catch (Exception e) {
			LOG.error("Error while closing meeting, " + e.getMessage(), e);
			header.add("error", "Error while closing meeting");
			return new ResponseEntity<String>("", header, HttpStatus.EXPECTATION_FAILED);
		}
		return new ResponseEntity<String>("", header, HttpStatus.OK);
	}

	// this method use to get the suppliers auction bid
	@RequestMapping(path = "getAuctionBidsOfSuppliersInSummary/{eventId}/{supplierId}", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<AuctionBids>> getAuctionBidsOfSuppliers(@PathVariable("eventId") String eventId, @PathVariable("supplierId") String supplierId) {
		HttpHeaders headers = new HttpHeaders();
		List<AuctionBids> auctionBids = rfaEventService.getAuctionBidsForSupplier(supplierId, eventId);
		for (AuctionBids auctionBids2 : auctionBids) {
			LOG.info(auctionBids2.getBidSubmissionDate());
		}
		return new ResponseEntity<List<AuctionBids>>(auctionBids, headers, HttpStatus.OK);
	}

	// this method use after suspension of event the amount revert
	@RequestMapping(path = "revertOnAuctionBidInSummary/{eventId}/{supplierId}", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<AuctionBids>> revertOnAuctionBid(@PathVariable("eventId") String eventId, @PathVariable("supplierId") String supplierId, @RequestParam String auctionBidId, HttpServletRequest request) {
		HttpHeaders headers = new HttpHeaders();
		try {
			String ipAddress = request.getHeader("X-FORWARDED-FOR");
			if (ipAddress == null) {
				ipAddress = request.getRemoteAddr();
			}
			String revertedPrice = rfaSupplierBqService.revertOnAuctionBid(supplierId, eventId, auctionBidId, ipAddress);
			headers.add("success", "Bid price reverted to '" + revertedPrice + "' successfully.");
		} catch (JsonParseException e) {
			LOG.error("Error while reveting auction " + e.getMessage(), e);
			headers.add("error", "Error while reverting Auction Bid");
			return new ResponseEntity<List<AuctionBids>>(headers, HttpStatus.EXPECTATION_FAILED);
		} catch (JsonMappingException e) {
			LOG.error("Error while reveting auction " + e.getMessage(), e);
			headers.add("error", "Error while reverting Auction Bid");
			return new ResponseEntity<List<AuctionBids>>(headers, HttpStatus.EXPECTATION_FAILED);
		} catch (IOException e) {
			LOG.error("Error while reveting auction " + e.getMessage(), e);
			headers.add("error", "Error while reverting Auction Bid");
			return new ResponseEntity<List<AuctionBids>>(headers, HttpStatus.EXPECTATION_FAILED);
		} catch (Exception e) {
			LOG.error("Error while reveting auction " + e.getMessage(), e);
			headers.add("error", "Error while reverting Auction Bid");
			return new ResponseEntity<List<AuctionBids>>(headers, HttpStatus.EXPECTATION_FAILED);
		}
		headers.add("sucess", "Auction Bid reverted sucessfully");
		return new ResponseEntity<List<AuctionBids>>(headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/getRfaTemplates/{auctionType}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<RfxTemplate>> getRfxTemplatesByAuctionType(@PathVariable("auctionType") String auctionType) {
		LOG.info("getRfxTemplates by auctionType  Called ..................Auction Type:" + AuctionType.fromString(auctionType));
		HttpHeaders headers = new HttpHeaders();
		List<RfxTemplate> templateList = null;
		try {
			templateList = rfxTemplateService.findAllActiveTemplatesByAuctionTypeForTenantId(SecurityLibrary.getLoggedInUserTenantId(), AuctionType.fromString(auctionType), SecurityLibrary.getLoggedInUser().getId());
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
		String type = "RFA";
		if (StringUtils.checkString(rfxType).length() > 0) {
			type = rfxType;
		}
		LOG.info("check Business Unit Empty  Called RFA.....Template Id:" + templateId + " ...... Event Id: " + eventId + " rfxType " + rfxType);
		List<BusinessUnit> businessUnits = null;
		try {

			if (eventIdSettingsService.isBusinessSettingEnable(SecurityLibrary.getLoggedInUserTenantId(), type)) {
				LOG.info("seeting on for Business Unit Empty");
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

				LOG.info("checking Business Unit Empty");
				RfaEvent event = eventService.getRfaEventById(eventId);
				if (event.getBusinessUnit() != null) {
					LOG.error("Bussiness unit is found");
					return new ResponseEntity<List<BusinessUnit>>(null, headers, HttpStatus.EXPECTATION_FAILED);
				}

				LOG.info("Business Unit Empty");
				businessUnits = businessUnitService.getPlainActiveBusinessUnitForTenant(SecurityLibrary.getLoggedInUserTenantId());

				return new ResponseEntity<List<BusinessUnit>>(businessUnits, headers, HttpStatus.OK);
			} else {
				LOG.error("setting is not on for id genrate .......");
				return new ResponseEntity<List<BusinessUnit>>(null, headers, HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			LOG.error("Error checking bu" + e.getMessage(), e);
			return new ResponseEntity<List<BusinessUnit>>(null, headers, HttpStatus.EXPECTATION_FAILED);
		}
	}

	@RequestMapping(path = "/downloadEvaluationReport/{eventId}/{evenvelopId}", method = RequestMethod.POST)
	public void BiddingReportEnglish(@PathVariable("eventId") String eventId, @PathVariable("evenvelopId") String evenvelopId, HttpServletResponse response, HttpSession session) throws Exception {
		JRSwapFileVirtualizer virtualizer = null;
		try {
			// Virtualizar - To increase the performance
			virtualizer = new JRSwapFileVirtualizer(100, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);
			RfaEnvelop envelope = rfaEnvelopDao.findById(evenvelopId);

			String filename = "EvaluationReport.pdf";
			JasperPrint jasperPrint = rfaEventService.getEvaluationReport(eventId, evenvelopId, (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), virtualizer);
			if (jasperPrint != null) {
				streamReport(jasperPrint, filename, response);
			}
			try {
				RfaEventAudit audit = new RfaEventAudit();
				audit.setAction(AuditActionType.Download);
				audit.setActionBy(SecurityLibrary.getLoggedInUser());
				audit.setActionDate(new Date());
				RfaEvent event = new RfaEvent();
				event.setId(eventId);
				audit.setEvent(event);
				audit.setDescription("Evaluation Report is downloaded for Envelope '" + envelope.getEnvelopTitle() + " '");
				eventAuditService.save(audit);

				RfaEvent rfaEvent = rfaEventService.getPlainEventById(eventId);
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Evaluation Report is downloaded for Envelope '" + envelope.getEnvelopTitle() + "' for Event '" + rfaEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFA);
				buyerAuditTrailDao.save(buyerAuditTrail);

			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}

		} catch (Exception e) {
			LOG.error("Could not EvaluationReport Report. " + e.getMessage(), e);
		} finally {
			if (virtualizer != null) {
				virtualizer.cleanup();
			}
		}
	}

	@RequestMapping(path = "/evaluationSummaryBqCq/{eventId}/{evenvelopId}", method = RequestMethod.POST)
	public void evaluationSummaryBqCqReport(@PathVariable("eventId") String eventId, @PathVariable("evenvelopId") String evenvelopId, HttpServletResponse response) throws Exception {
		// try {
		// LOG.info("evaluationSummaryBqCq RFA");
		// String filename = "EvaluationSummaryBqCq.pdf";
		//
		// JasperPrint jasperPrint = rfaEventService.generatePdfforEvaluationSummary(eventId);
		// if (jasperPrint != null) {
		// streamReport(jasperPrint, filename, response);
		// }
		// } catch (Exception e) {
		// LOG.error("Could not generate Evaluation Summary BqCq Report. " + e.getMessage(), e);
		// }
	}

	@RequestMapping(path = "/downloadBuyerBiddingEnglishReport/{eventId}", method = RequestMethod.POST)
	public void downloadBuyerBiddingEnglishReport(@PathVariable("eventId") String eventId, HttpServletResponse response, HttpSession session) throws Exception {
		JRSwapFileVirtualizer virtualizer = null;
		try {
			// Virtualizar - To increase the performance
			virtualizer = new JRSwapFileVirtualizer(300, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);
			String filename = "BiddingReportEnglish.pdf";
			JasperPrint jasperPrint = rfaEventService.getBiddingReportEnglish(eventId, (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), virtualizer);
			if (jasperPrint != null) {
				streamReport(jasperPrint, filename, response);
			}
			try {
				RfaEventAudit audit = new RfaEventAudit();
				audit.setAction(AuditActionType.Download);
				audit.setActionBy(SecurityLibrary.getLoggedInUser());
				audit.setActionDate(new Date());
				RfaEvent event = new RfaEvent();
				event.setId(eventId);
				audit.setEvent(event);
				audit.setDescription("Auction Report downloaded");
				eventAuditService.save(audit);

				RfaEvent rfaEvent = rfaEventService.getPlainEventById(eventId);
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Auction Report is downloaded for Event ’" + rfaEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFA);
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
			virtualizer = new JRSwapFileVirtualizer(300, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);

			String filename = "EvaluationSummary.pdf";
			JasperPrint jasperPrint = rfaEventService.generatePdfforEvaluationSummary(eventId, envelopeId, virtualizer);
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

	// this method use after suspension of event the amount revert
	@RequestMapping(path = "revertOnAuctionBidInSummaryWithRemark/{eventId}/{supplierId}", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<AuctionBids>> revertOnAuctionBidInSummaryWithRemark(@PathVariable("eventId") String eventId, @PathVariable("supplierId") String supplierId, @RequestParam String auctionBidId, @RequestParam String revertReason, HttpServletRequest request) {
		HttpHeaders headers = new HttpHeaders();
		try {
			String ipAddress = request.getHeader("X-FORWARDED-FOR");
			if (ipAddress == null) {
				ipAddress = request.getRemoteAddr();
			}
			String revertedPrice = rfaSupplierBqService.revertOnAuctionBid(supplierId, eventId, auctionBidId, ipAddress, revertReason, SecurityLibrary.getLoggedInUser());
			headers.add("success", "Bid price reverted to '" + revertedPrice + "' successfully.");
		} catch (JsonParseException e) {
			LOG.error("Error while reveting auction " + e.getMessage(), e);
			headers.add("error", "Error while reverting Auction Bid");
			return new ResponseEntity<List<AuctionBids>>(headers, HttpStatus.EXPECTATION_FAILED);
		} catch (JsonMappingException e) {
			LOG.error("Error while reveting auction " + e.getMessage(), e);
			headers.add("error", "Error while reverting Auction Bid");
			return new ResponseEntity<List<AuctionBids>>(headers, HttpStatus.EXPECTATION_FAILED);
		} catch (IOException e) {
			LOG.error("Error while reveting auction " + e.getMessage(), e);
			headers.add("error", "Error while reverting Auction Bid");
			return new ResponseEntity<List<AuctionBids>>(headers, HttpStatus.EXPECTATION_FAILED);
		} catch (Exception e) {
			LOG.error("Error while reveting auction " + e.getMessage(), e);
			headers.add("error", "Error while reverting Auction Bid");
			return new ResponseEntity<List<AuctionBids>>(headers, HttpStatus.EXPECTATION_FAILED);
		}
		headers.add("sucess", "Auction Bid reverted sucessfully");
		return new ResponseEntity<List<AuctionBids>>(headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/addFeeInSupplier/{eventId}", method = RequestMethod.POST)
	public ModelAndView addFeeInSupplier(@ModelAttribute("eventSupplier") RfaEventSupplier eventSupplier, @PathVariable("eventId") String eventId, HttpSession session, RedirectAttributes redir, Model model) throws Exception {
		try {
			List<RfaEventSupplier> eventsuppList = rfaEventSupplierService.getAllSuppliersByFeeEventId(eventId, eventSupplier.getId());
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			TimeZone timeZone = TimeZone.getDefault();
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}

			if (CollectionUtil.isNotEmpty(eventsuppList)) {
				for (RfaEventSupplier suppList : eventsuppList) {
					suppList.setFeePaid(Boolean.TRUE);
					suppList.setFeeReference(eventSupplier.getFeeReference());
					Date feeDateTime = null;
					if (eventSupplier.getFeePaidDate() != null && eventSupplier.getFeePaidTime() != null) {
						feeDateTime = DateUtil.combineDateTime(eventSupplier.getFeePaidDate(), eventSupplier.getFeePaidTime(), timeZone);
					}
					suppList.setFeePaidDate(feeDateTime);
					suppList = rfaEventSupplierService.saveRfaEventSupplier(suppList);

					try {
						RfaEventAudit audit = new RfaEventAudit();
						audit.setActionBy(SecurityLibrary.getLoggedInUser());
						audit.setActionDate(new Date());
						audit.setDescription("Event Participaton fee of " + suppList.getRfxEvent().getParticipationFeeCurrency().getCurrencyCode() + " " + (suppList.getRfxEvent().getParticipationFees().setScale(2, BigDecimal.ROUND_HALF_UP)) + " collected for " + suppList.getSupplierCompanyName() + ". Fee Reference: " + suppList.getFeeReference());
						audit.setAction(AuditActionType.Paid);
						audit.setSupplier(suppList.getSupplier());
						RfaEvent event = new RfaEvent();
						event.setId(eventId);
						audit.setEvent(event);
						eventAuditService.save(audit);

						// Email to All supplier admins
						List<User> users = userDao.getAllAdminUsersForSupplier(suppList.getSupplier().getId());
						if (CollectionUtil.isNotEmpty(users)) {
							for (User user : users) {
								if (StringUtils.isNotBlank(user.getCommunicationEmail())) {
									rfaEventService.sendEmailAfterParticipationFees(user.getCommunicationEmail(), "Event Participation Payment", "Event Participation Fee has been made successfully for the below", suppList.getRfxEvent(), user.getName(), suppList.getFeeReference(), (supplierSettingsService.getSupplierTimeZoneByTenantId(user.getTenantId())));
								} else {
									LOG.warn("Communication email not set");
								}
							}
						}

						// Email to event owner
						if (StringUtils.isNotBlank(suppList.getRfxEvent().getEventOwner().getCommunicationEmail())) {
							rfaEventService.sendEmailAfterParticipationFees(suppList.getRfxEvent().getEventOwner().getCommunicationEmail(), "Event Participation Payment", "Event Participation Fee has been made successfully for the below by " + suppList.getSupplierCompanyName(), suppList.getRfxEvent(), suppList.getRfxEvent().getEventOwner().getName(), suppList.getFeeReference(), (buyerSettingsService.getBuyerTimeZoneByTenantId(suppList.getRfxEvent().getEventOwner().getTenantId())));
						} else {
							LOG.warn("Communication email not set");
						}

						// Email to associate buyers
						if (suppList.getRfxEvent().getTeamMembers() != null && suppList.getRfxEvent().getTeamMembers().size() > 0) {
							for (RfaTeamMember member : suppList.getRfxEvent().getTeamMembers()) {
								if (member.getTeamMemberType().equals(TeamMemberType.Associate_Owner)) {
									if (StringUtils.isNotBlank(member.getUser().getCommunicationEmail())) {
										rfaEventService.sendEmailAfterParticipationFees(member.getUser().getCommunicationEmail(), "Event Participation Payment", "Event Participation Fee has been made successfully for the below by " + suppList.getSupplierCompanyName(), suppList.getRfxEvent(), member.getUser().getName(), suppList.getFeeReference(), (buyerSettingsService.getBuyerTimeZoneByTenantId(member.getUser().getTenantId())));
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
		return new ModelAndView("redirect:/buyer/RFA/viewSummary/" + eventId);
	}

	@RequestMapping(path = "/addDepositInSupplier/{eventId}", method = RequestMethod.POST)
	public ModelAndView addDepositInSupplier(@ModelAttribute("eventSupplier") RfaEventSupplier eventSupplier, @PathVariable("eventId") String eventId, HttpSession session, RedirectAttributes redir, Model model) throws Exception {
		try {

			List<RfaEventSupplier> eventsuppList = rfaEventSupplierService.getAllSuppliersByFeeEventId(eventId, eventSupplier.getId());
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			TimeZone timeZone = TimeZone.getDefault();
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}
			if (CollectionUtil.isNotEmpty(eventsuppList)) {
				for (RfaEventSupplier suppList : eventsuppList) {
					suppList.setDepositPaid(Boolean.TRUE);
					suppList.setDepositReference(eventSupplier.getDepositReference());
					Date depositDateTime = null;
					if (eventSupplier.getDepositPaidDate() != null && eventSupplier.getDepositPaidTime() != null) {
						depositDateTime = DateUtil.combineDateTime(eventSupplier.getDepositPaidDate(), eventSupplier.getDepositPaidTime(), timeZone);
					}
					suppList.setDepositPaidDate(depositDateTime);
					suppList = rfaEventSupplierService.saveRfaEventSupplier(suppList);
					redir.addFlashAttribute("success", "Event Deposits added successfully");
					model.addAttribute("eventSupplier", suppList);
					LOG.info("Deposit updated");
				}
			}

		} catch (Exception e) {
			LOG.error("error while add Deposit Detail in supplier" + e.getMessage(), e);
			redir.addFlashAttribute("error", "Error while updating Fee Details");
		}
		return new ModelAndView("redirect:/buyer/RFA/viewSummary/" + eventId);
	}

	@RequestMapping(path = "/downloadAllReport/{eventId}/{eventType}", method = RequestMethod.POST)
	public void downloadAllReports(@PathVariable("eventId") String eventId, @PathVariable("eventType") String eventType, HttpServletResponse response, HttpSession session) throws Exception {
		JRSwapFileVirtualizer virtualizer = null;
		File zipFile = File.createTempFile(eventId + eventType, "" + new Date().getTime());
		FileOutputStream fos = new FileOutputStream(zipFile);

		try (ZipOutputStream zos = new ZipOutputStream(fos)) {
			virtualizer = new JRSwapFileVirtualizer(100, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);
			TimeZone timeZone = TimeZone.getDefault();
			String zipFileName = "";
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}
			switch (RfxTypes.valueOf(eventType)) {
			case RFA: {

				RfaEvent event = rfaEventService.getRfaEventByeventId(eventId);
				zipFileName = event.getReferanceNumber().replaceAll("/", "-") + Global.ZIP_FILE_EXTENTION;
				zipFileName = zipFileName.replaceAll("[^a-zA-Z0-9\\.]", "_");
				String parentFolder = event.getReferanceNumber().replaceAll("/", "-");
				parentFolder = parentFolder.replaceAll("[^a-zA-Z0-9\\.]", "_");
				JasperPrint jasperPrint = rfaEventService.getEvaluationSummaryPdf(event, SecurityLibrary.getLoggedInUser(), (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), virtualizer);
				if (jasperPrint != null) {
					FileUtil.writePdfToZip(zos, jasperPrint, "", "Event Summary");
					zos.flush();
				}

				if (event.getEvaluationConclusionEnvelopeEvaluatedCount() == null) {
					try {
						jasperPrint = rfaEventService.generatePdfforEvaluationSummary(eventId, timeZone, virtualizer);
						if (jasperPrint != null) {
							FileUtil.writePdfToZip(zos, jasperPrint, "", "Evaluation Summary");
							zos.flush();
						}
					} catch (Exception e) {
					}
				}
				if (event.getEvaluationConclusionEnvelopeNonEvaluatedCount() != null) {
					try {
						jasperPrint = rfaEventService.generatePdfforEvaluationConclusion(eventId, timeZone, virtualizer);
						if (jasperPrint != null) {
							FileUtil.writePdfToZip(zos, jasperPrint, "", "Evaluation Conclusion Report");
							zos.flush();
						}
					} catch (Exception e) {
					}
				}

				try {
					jasperPrint = rfaEventService.getBiddingReportEnglish(eventId, (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), virtualizer);
					if (jasperPrint != null) {
						FileUtil.writePdfToZip(zos, jasperPrint, "", "Auction Report");
						zos.flush();
					}
				} catch (Exception e1) {
				}

				List<RfaEnvelop> rfaEnvelopes = rfaEnvelopService.getAllEnvelopByEventId(eventId, SecurityLibrary.getLoggedInUser());
				String attachmentFolder = "Evaluator_Working_Sheet";
				if (CollectionUtil.isNotEmpty(rfaEnvelopes)) {
					for (RfaEnvelop rfaEnvelop : rfaEnvelopes) {
						// If this envelope has prematurely concluded evaluation then dont generate report.
						if (Boolean.TRUE == rfaEnvelop.getEvaluationCompletedPrematurely()) {
						} else {
							try {
								// Envelop attachments for evaluators
								if (CollectionUtil.isNotEmpty(rfaEnvelop.getEvaluators())) {
									for (RfaEvaluatorUser usr : rfaEnvelop.getEvaluators()) {
										if (usr.getFileName() != null && usr.getFileData() != null) {
											String fileName = usr.getUser().getName().replaceAll("[^a-zA-Z0-9\\.]", "_") + "-" + usr.getFileName();
											FileUtil.writeFileToZip(zos, usr.getFileData(), attachmentFolder + Global.PATH_SEPARATOR + event.getEventName().replaceAll(" ", "_").replaceAll("[^a-zA-Z0-9\\.]", "_") + "_" + rfaEnvelop.getEnvelopTitle().replaceAll(" ", "_").replaceAll("[^a-zA-Z0-9\\.]", "_") + Global.PATH_SEPARATOR, fileName);
											zos.flush();
										}
									}
								}

								// Envelop attachments for lead evaluators
								if (rfaEnvelop.getFileName() != null && rfaEnvelop.getFileData() != null) {
									String fileName = rfaEnvelop.getLeadEvaluater().getName().replaceAll("[^a-zA-Z0-9\\.]", "_") + "-" + rfaEnvelop.getFileName();
									FileUtil.writeFileToZip(zos, rfaEnvelop.getFileData(), attachmentFolder + Global.PATH_SEPARATOR + event.getEventName().replaceAll(" ", "_").replaceAll("[^a-zA-Z0-9\\.]", "_") + "_" + rfaEnvelop.getEnvelopTitle().replaceAll(" ", "_").replaceAll("[^a-zA-Z0-9\\.]", "_") + Global.PATH_SEPARATOR, fileName);
									zos.flush();
								}
								rfaEnvelopService.generateEnvelopeZip(event.getId(), rfaEnvelop.getId(), zos, true, session, virtualizer);
							} catch (Exception e) {
							}
						}
					}

				}

				List<RfaEventAwardAudit> award = rfaEventService.getRfaEventAwardByEventId(eventId);
				if (CollectionUtil.isNotEmpty(award)) {
					for (RfaEventAwardAudit rfaEventAwardAudit : award) {
						byte[] awardSnapshortPdf = rfaEventAwardAudit.getSnapshot();
						FileUtil.writeFileToZip(zos, awardSnapshortPdf, "", "Award Snapshort.pdf");
						zos.flush();
						byte[] awardSnapshortExcel = rfaEventAwardAudit.getExcelSnapshot();
						FileUtil.writeFileToZip(zos, awardSnapshortExcel, "", "Award Snapshort.xlsx");
						zos.flush();
					}
				}

				try {
					RfaEventAudit audit = new RfaEventAudit();
					audit.setAction(AuditActionType.Download);
					audit.setActionBy(SecurityLibrary.getLoggedInUser());
					audit.setActionDate(new Date());
					RfaEvent rfaEvent = new RfaEvent();
					rfaEvent.setId(eventId);
					audit.setEvent(rfaEvent);
					audit.setDescription("All reports downloaded ");
					eventAuditService.save(audit);

					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "All Reports downloaded for Event ’" + event.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFA);
					buyerAuditTrailDao.save(buyerAuditTrail);

				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}

				break;
			}
			case RFT: {
				RftEvent event = rftEventService.getRftEventByeventId(eventId);
				zipFileName = event.getReferanceNumber().replaceAll("/", "-") + Global.ZIP_FILE_EXTENTION;
				zipFileName = zipFileName.replaceAll("[^a-zA-Z0-9\\.]", "_");
				String parentFolder = event.getReferanceNumber().replaceAll("/", "-");
				parentFolder = parentFolder.replaceAll("[^a-zA-Z0-9\\.]", "_");
				JasperPrint jasperPrint = rftEventService.getEvaluationSummaryPdf(event, SecurityLibrary.getLoggedInUser(), (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), virtualizer);
				if (jasperPrint != null) {
					FileUtil.writePdfToZip(zos, jasperPrint, "", "Event Summary");
					zos.flush();
				}

				if (event.getEvaluationConclusionEnvelopeEvaluatedCount() == null) {
					try {
						jasperPrint = rftEventService.generatePdfforEvaluationSummary(eventId, timeZone, virtualizer);
						if (jasperPrint != null) {
							FileUtil.writePdfToZip(zos, jasperPrint, "", "Evaluation Summary");
							zos.flush();
						}
					} catch (Exception e) {
					}
				}
				if (event.getEvaluationConclusionEnvelopeNonEvaluatedCount() != null) {
					try {
						jasperPrint = rftEventService.generatePdfforEvaluationConclusion(eventId, timeZone, virtualizer);
						if (jasperPrint != null) {
							FileUtil.writePdfToZip(zos, jasperPrint, "", "Evaluation Conclusion Report");
							zos.flush();
						}
					} catch (Exception e) {
					}
				}

				List<RftEnvelop> rftEnvelopes = rftEnvelopService.getAllRftEnvelopByEventId(eventId, SecurityLibrary.getLoggedInUser());
				String attachmentFolder = "Evaluator_Working_Sheet";
				if (CollectionUtil.isNotEmpty(rftEnvelopes)) {
					for (RftEnvelop rftEnvelop : rftEnvelopes) {
						// If this envelope has prematurely concluded evaluation then dont generate report.
						if (Boolean.TRUE == rftEnvelop.getEvaluationCompletedPrematurely()) {
						} else {
							try {
								// Envelop attachments for evaluators
								if (CollectionUtil.isNotEmpty(rftEnvelop.getEvaluators())) {
									for (RftEvaluatorUser usr : rftEnvelop.getEvaluators()) {
										if (usr.getFileName() != null && usr.getFileData() != null) {
											String fileName = usr.getUser().getName().replaceAll("[^a-zA-Z0-9\\.]", "_") + "-" + usr.getFileName();
											FileUtil.writeFileToZip(zos, usr.getFileData(), attachmentFolder + Global.PATH_SEPARATOR + event.getEventName().replaceAll(" ", "_").replaceAll("[^a-zA-Z0-9\\.]", "_") + "_" + rftEnvelop.getEnvelopTitle().replaceAll(" ", "_").replaceAll("[^a-zA-Z0-9\\.]", "_") + Global.PATH_SEPARATOR, fileName);
											zos.flush();
										}
									}
								}

								// Envelop attachments for lead evaluators
								if (rftEnvelop.getFileName() != null && rftEnvelop.getFileData() != null) {
									String fileName = rftEnvelop.getLeadEvaluater().getName().replaceAll("[^a-zA-Z0-9\\.]", "_") + "-" + rftEnvelop.getFileName();
									FileUtil.writeFileToZip(zos, rftEnvelop.getFileData(), attachmentFolder + Global.PATH_SEPARATOR + event.getEventName().replaceAll(" ", "_").replaceAll("[^a-zA-Z0-9\\.]", "_") + "_" + rftEnvelop.getEnvelopTitle().replaceAll(" ", "_").replaceAll("[^a-zA-Z0-9\\.]", "_") + Global.PATH_SEPARATOR, fileName);
									zos.flush();
								}
								rftEnvelopService.generateEnvelopeZip(event.getId(), rftEnvelop.getId(), zos, true, session, virtualizer);
							} catch (Exception e) {
							}
						}
					}

				}

				List<RftEventAwardAudit> award = rftEventService.getRftEventAwardByEventId(eventId);
				if (CollectionUtil.isNotEmpty(award)) {
					for (RftEventAwardAudit rftEventAwardAudit : award) {
						byte[] awardSnapshortPdf = rftEventAwardAudit.getSnapshot();
						FileUtil.writeFileToZip(zos, awardSnapshortPdf, "", "Award Snapshort.pdf");
						zos.flush();
						byte[] awardSnapshortExcel = rftEventAwardAudit.getExcelSnapshot();
						FileUtil.writeFileToZip(zos, awardSnapshortExcel, "", "Award Snapshort.xlsx");
						zos.flush();
					}
				}

				try {
					RftEventAudit audit = new RftEventAudit();
					audit.setAction(AuditActionType.Download);
					audit.setActionBy(SecurityLibrary.getLoggedInUser());
					audit.setActionDate(new Date());
					RftEvent rftEvent = new RftEvent();
					rftEvent.setId(eventId);
					audit.setEvent(rftEvent);
					audit.setDescription("All reports downloaded ");
					eventAuditService.save(audit);

					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "All reports downloaded for Event '" + event.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFT);
					buyerAuditTrailDao.save(buyerAuditTrail);

				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}

				break;

			}
			case RFQ: {
				RfqEvent event = rfqEventService.getRfqEventByeventId(eventId);
				zipFileName = event.getReferanceNumber().replaceAll("/", "-") + Global.ZIP_FILE_EXTENTION;
				zipFileName = zipFileName.replaceAll("[^a-zA-Z0-9\\.]", "_");
				String parentFolder = event.getReferanceNumber().replaceAll("/", "-");
				parentFolder = parentFolder.replaceAll("[^a-zA-Z0-9\\.]", "_");

				JasperPrint jasperPrint;
				try {
					jasperPrint = rfqEventService.getEvaluationSummaryPdf(event, SecurityLibrary.getLoggedInUser(), (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), virtualizer);
					if (jasperPrint != null) {
						FileUtil.writePdfToZip(zos, jasperPrint, "", "Event Summary");
						zos.flush();
					}
				} catch (Exception e1) {
				}

				if (event.getEvaluationConclusionEnvelopeEvaluatedCount() == null) {
					try {
						jasperPrint = rfqEventService.generatePdfforEvaluationSummary(eventId, timeZone, virtualizer);
						if (jasperPrint != null) {
							FileUtil.writePdfToZip(zos, jasperPrint, "", "Evaluation Summary");
							zos.flush();
						}
					} catch (Exception e) {
					}

				}
				if (event.getEvaluationConclusionEnvelopeNonEvaluatedCount() != null) {
					try {
						jasperPrint = rfqEventService.generatePdfforEvaluationConclusion(eventId, timeZone, virtualizer);
						if (jasperPrint != null) {
							FileUtil.writePdfToZip(zos, jasperPrint, "", "Evaluation Conclusion Report");
							zos.flush();
						}
					} catch (Exception e) {
					}

				}

				List<RfqEnvelop> rfqEnvelopes = rfqEnvelopService.getAllEnvelopByEventId(eventId, SecurityLibrary.getLoggedInUser());
				String attachmentFolder = "Evaluator_working_sheet";
				if (CollectionUtil.isNotEmpty(rfqEnvelopes)) {
					for (RfqEnvelop rfqEnvelop : rfqEnvelopes) {
						// If this envelope has prematurely concluded evaluation then dont generate report.
						if (Boolean.TRUE == rfqEnvelop.getEvaluationCompletedPrematurely()) {
						} else {
							// Envelop attachments for evaluators
							if (CollectionUtil.isNotEmpty(rfqEnvelop.getEvaluators())) {
								for (RfqEvaluatorUser usr : rfqEnvelop.getEvaluators()) {
									if (usr.getFileName() != null && usr.getFileData() != null) {
										String fileName = usr.getUser().getName().replaceAll("[^a-zA-Z0-9\\.]", "_") + "-" + usr.getFileName();
										FileUtil.writeFileToZip(zos, usr.getFileData(), attachmentFolder + Global.PATH_SEPARATOR + event.getEventName().replaceAll(" ", "_").replaceAll("[^a-zA-Z0-9\\.]", "_") + "_" + rfqEnvelop.getEnvelopTitle().replaceAll(" ", "_").replaceAll("[^a-zA-Z0-9\\.]", "_") + Global.PATH_SEPARATOR, fileName);
										zos.flush();
									}
								}
							}

							// Envelop attachments for lead evaluators
							if (rfqEnvelop.getFileName() != null && rfqEnvelop.getFileData() != null) {
								String fileName = rfqEnvelop.getLeadEvaluater().getName().replaceAll("[^a-zA-Z0-9\\.]", "_") + "-" + rfqEnvelop.getFileName();
								FileUtil.writeFileToZip(zos, rfqEnvelop.getFileData(), attachmentFolder + Global.PATH_SEPARATOR + event.getEventName().replaceAll(" ", "_").replaceAll("[^a-zA-Z0-9\\.]", "_") + "_" + rfqEnvelop.getEnvelopTitle().replaceAll(" ", "_").replaceAll("[^a-zA-Z0-9\\.]", "_") + Global.PATH_SEPARATOR, fileName);
								zos.flush();
							}
							rfqEnvelopService.generateEnvelopeZip(event.getId(), rfqEnvelop.getId(), zos, true, session, virtualizer);
						}
					}

				}

				List<RfqEventAwardAudit> award = rfqEventService.getRfqEventAwardByEventId(eventId);
				if (CollectionUtil.isNotEmpty(award)) {
					for (RfqEventAwardAudit rfqEventAwardAudit : award) {
						byte[] awardSnapshortPdf = rfqEventAwardAudit.getSnapshot();
						FileUtil.writeFileToZip(zos, awardSnapshortPdf, "", "Award Snapshort.pdf");
						zos.flush();
						byte[] awardSnapshortExcel = rfqEventAwardAudit.getExcelSnapshot();
						FileUtil.writeFileToZip(zos, awardSnapshortExcel, "", "Award Snapshort.xlsx");
						zos.flush();
					}
				}
				try {
					RfqEventAudit audit = new RfqEventAudit();
					audit.setAction(AuditActionType.Download);
					audit.setActionBy(SecurityLibrary.getLoggedInUser());
					audit.setActionDate(new Date());
					RfqEvent rfqEvent = new RfqEvent();
					rfqEvent.setId(eventId);
					audit.setEvent(rfqEvent);
					audit.setDescription("All reports downloaded ");
					eventAuditService.save(audit);

					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "All reports downloaded for Event '" + event.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFQ);
					buyerAuditTrailDao.save(buyerAuditTrail);

				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}

				break;
			}
			case RFP: {
				RfpEvent event = rfpEventService.getRfpEventByeventId(eventId);
				zipFileName = event.getReferanceNumber().replaceAll("/", "-") + Global.ZIP_FILE_EXTENTION;
				zipFileName = zipFileName.replaceAll("[^a-zA-Z0-9\\.]", "_");
				String parentFolder = event.getReferanceNumber().replaceAll("/", "-");
				parentFolder = parentFolder.replaceAll("[^a-zA-Z0-9\\.]", "_");
				JasperPrint jasperPrint = rfpEventService.getEvaluationSummaryPdf(event, SecurityLibrary.getLoggedInUser(), (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), virtualizer);
				if (jasperPrint != null) {
					FileUtil.writePdfToZip(zos, jasperPrint, "", "Event Summary");
					zos.flush();
				}

				if (event.getEvaluationConclusionEnvelopeEvaluatedCount() == null) {
					jasperPrint = rfpEventService.generatePdfforEvaluationSummary(eventId, timeZone, virtualizer);
					if (jasperPrint != null) {
						FileUtil.writePdfToZip(zos, jasperPrint, "", "Evaluation Summary");
						zos.flush();
					}
				}
				if (event.getEvaluationConclusionEnvelopeNonEvaluatedCount() != null) {
					jasperPrint = rfpEventService.generatePdfforEvaluationConclusion(eventId, timeZone, virtualizer);
					if (jasperPrint != null) {
						FileUtil.writePdfToZip(zos, jasperPrint, "", "Evaluation Conclusion Report");
						zos.flush();
					}

				}
				List<RfpEnvelop> rfpEnvelopes = rfpEnvelopService.getAllEnvelopByEventId(eventId, SecurityLibrary.getLoggedInUser());
				String attachmentFolder = "Evaluator_Working_Sheet";
				if (CollectionUtil.isNotEmpty(rfpEnvelopes)) {
					for (RfpEnvelop rfpEnvelop : rfpEnvelopes) {
						// If this envelope has prematurely concluded evaluation then dont generate report.
						if (Boolean.TRUE == rfpEnvelop.getEvaluationCompletedPrematurely()) {
						} else {
							// Envelop attachments for evaluators
							if (CollectionUtil.isNotEmpty(rfpEnvelop.getEvaluators())) {
								for (RfpEvaluatorUser usr : rfpEnvelop.getEvaluators()) {
									if (usr.getFileName() != null && usr.getFileData() != null) {
										String fileName = usr.getUser().getName().replaceAll("[^a-zA-Z0-9\\.]", "_") + "-" + usr.getFileName();
										FileUtil.writeFileToZip(zos, usr.getFileData(), attachmentFolder + Global.PATH_SEPARATOR + event.getEventName().replaceAll(" ", "_").replaceAll("[^a-zA-Z0-9\\.]", "_") + "_" + rfpEnvelop.getEnvelopTitle().replaceAll(" ", "_").replaceAll("[^a-zA-Z0-9\\.]", "_") + Global.PATH_SEPARATOR, fileName);
										zos.flush();
									}
								}
							}

							// Envelop attachments for lead evaluators
							if (rfpEnvelop.getFileName() != null && rfpEnvelop.getFileData() != null) {
								String fileName = rfpEnvelop.getLeadEvaluater().getName().replaceAll("[^a-zA-Z0-9\\.]", "_") + "-" + rfpEnvelop.getFileName();
								FileUtil.writeFileToZip(zos, rfpEnvelop.getFileData(), attachmentFolder + Global.PATH_SEPARATOR + event.getEventName().replaceAll(" ", "_").replaceAll("[^a-zA-Z0-9\\.]", "_") + "_" + rfpEnvelop.getEnvelopTitle().replaceAll(" ", "_").replaceAll("[^a-zA-Z0-9\\.]", "_") + Global.PATH_SEPARATOR, fileName);
								zos.flush();
							}
							rfpEnvelopService.generateEnvelopeZip(event.getId(), rfpEnvelop.getId(), zos, true, session, virtualizer);
						}
					}

				}

				List<RfpEventAwardAudit> award = rfpEventService.getRfpEventAwardByEventId(eventId);
				if (CollectionUtil.isNotEmpty(award)) {
					for (RfpEventAwardAudit rfpEventAwardAudit : award) {
						byte[] awardSnapshortPdf = rfpEventAwardAudit.getSnapshot();
						FileUtil.writeFileToZip(zos, awardSnapshortPdf, "", "Award Snapshort.pdf");
						zos.flush();
						byte[] awardSnapshortExcel = rfpEventAwardAudit.getExcelSnapshot();
						FileUtil.writeFileToZip(zos, awardSnapshortExcel, "", "Award Snapshort.xlsx");
						zos.flush();
					}
				}
				try {
					RfpEventAudit audit = new RfpEventAudit();
					audit.setAction(AuditActionType.Download);
					audit.setActionBy(SecurityLibrary.getLoggedInUser());
					audit.setActionDate(new Date());
					RfpEvent rfpEvent = new RfpEvent();
					rfpEvent.setId(eventId);
					audit.setEvent(rfpEvent);
					audit.setDescription("All reports downloaded ");
					eventAuditService.save(audit);

					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "All reports downloaded for Event '" + event.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFP);
					buyerAuditTrailDao.save(buyerAuditTrail);

				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}

				try {
					LOG.info("--------------------------AFTER AUDIT---------------------------------------");
				} catch (Exception e) {
					LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
				}

				break;
			}
			case RFI: {
				RfiEvent event = rfiEventService.getRfiEventByeventId(eventId);
				zipFileName = event.getReferanceNumber().replaceAll("/", "-") + Global.ZIP_FILE_EXTENTION;
				zipFileName = zipFileName.replaceAll("[^a-zA-Z0-9\\.]", "_");
				String parentFolder = event.getReferanceNumber().replaceAll("/", "-");
				parentFolder = parentFolder.replaceAll("[^a-zA-Z0-9\\.]", "_");
				JasperPrint jasperPrint = rfiEventService.getEvaluationSummaryPdf(event, SecurityLibrary.getLoggedInUser(), (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), virtualizer);
				if (jasperPrint != null) {
					FileUtil.writePdfToZip(zos, jasperPrint, "", "Event Summary");
					zos.flush();
				}

				if (event.getEvaluationConclusionEnvelopeEvaluatedCount() == null) {
					try {
						jasperPrint = rfiEventService.generatePdfforEvaluationSummary(eventId, timeZone, virtualizer);
						if (jasperPrint != null) {
							FileUtil.writePdfToZip(zos, jasperPrint, "", "Evaluation Summary");
							zos.flush();
						}
					} catch (Exception e) {
					}
				}
				if (event.getEvaluationConclusionEnvelopeNonEvaluatedCount() != null) {
					try {
						jasperPrint = rfiEventService.generatePdfforEvaluationConclusion(eventId, timeZone, virtualizer);
						if (jasperPrint != null) {
							FileUtil.writePdfToZip(zos, jasperPrint, "", "Evaluation Conclusion Report");
							zos.flush();
						}
					} catch (Exception e) {
					}

				}
				List<RfiEnvelop> rfiEnvelopes = rfiEnvelopService.getAllEnvelopByEventId(eventId, SecurityLibrary.getLoggedInUser());
				String attachmentFolder = "Evaluator_Working_Sheet";
				if (CollectionUtil.isNotEmpty(rfiEnvelopes)) {
					for (RfiEnvelop rfpEnvelop : rfiEnvelopes) {
						// If this envelope has prematurely concluded evaluation then dont generate report.
						if (Boolean.TRUE == rfpEnvelop.getEvaluationCompletedPrematurely()) {
						} else {
							// Envelop attachments for evaluators
							if (CollectionUtil.isNotEmpty(rfpEnvelop.getEvaluators())) {
								for (RfiEvaluatorUser usr : rfpEnvelop.getEvaluators()) {
									if (usr.getFileName() != null && usr.getFileData() != null) {
										String fileName = usr.getUser().getName().replaceAll("[^a-zA-Z0-9\\.]", "_") + "-" + usr.getFileName();
										FileUtil.writeFileToZip(zos, usr.getFileData(), attachmentFolder + Global.PATH_SEPARATOR + event.getEventName().replaceAll(" ", "_").replaceAll("[^a-zA-Z0-9\\.]", "_") + "_" + rfpEnvelop.getEnvelopTitle().replaceAll(" ", "_").replaceAll("[^a-zA-Z0-9\\.]", "_") + Global.PATH_SEPARATOR, fileName);
										zos.flush();
									}
								}
							}

							// Envelop attachments for lead evaluators
							if (rfpEnvelop.getFileName() != null && rfpEnvelop.getFileData() != null) {
								String fileName = rfpEnvelop.getLeadEvaluater().getName().replaceAll("[^a-zA-Z0-9\\.]", "_") + "-" + rfpEnvelop.getFileName();
								FileUtil.writeFileToZip(zos, rfpEnvelop.getFileData(), attachmentFolder + Global.PATH_SEPARATOR + event.getEventName().replaceAll(" ", "_").replaceAll("[^a-zA-Z0-9\\.]", "_") + "_" + rfpEnvelop.getEnvelopTitle().replaceAll(" ", "_").replaceAll("[^a-zA-Z0-9\\.]", "_") + Global.PATH_SEPARATOR, fileName);
								zos.flush();
							}
							rfiEnvelopService.generateEnvelopeZip(event.getId(), rfpEnvelop.getId(), zos, true, session, virtualizer, strTimeZone);
						}
					}
				}
				try {
					RfiEventAudit audit = new RfiEventAudit();
					audit.setAction(AuditActionType.Download);
					audit.setActionBy(SecurityLibrary.getLoggedInUser());
					audit.setActionDate(new Date());
					RfiEvent rfiEvent = new RfiEvent();
					rfiEvent.setId(eventId);
					audit.setEvent(rfiEvent);
					audit.setDescription("All reports downloaded ");
					eventAuditService.save(audit);

					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "All reports downloaded for Event '" + event.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFI);
					buyerAuditTrailDao.save(buyerAuditTrail);

				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}

				break;
			}
			default:
				break;
			}

			zos.flush();
			zos.close();
			fos.flush();
			fos.close();

			FileInputStream fin = new FileInputStream(zipFile);
			long fileSize = fin.available();

			response.setContentType("application/zip,application/octet-stream");
			response.addHeader("Content-Disposition", "attachment; filename=" + zipFileName);
			// response.setHeader("Transfer-Encoding", "chunked");
			response.setHeader("Content-Length", "" + fileSize);

			byte[] data = new byte[1000];
			int read = 0;
			while ((read = fin.read(data)) != -1) {
				response.getOutputStream().write(data, 0, read);
			}

			response.getOutputStream().flush();

			fin.close();
			response.getOutputStream().close();

			// zos.close();
			// response.setContentType("application/zip,application/octet-stream");
			// response.addHeader("Content-Disposition", "attachment; filename=" + zipFileName);
			// response.setHeader("Content-Length", String.valueOf(baos.size()));
			// response.getOutputStream().write(baos.toByteArray());
			// response.getOutputStream().flush();
			// response.getOutputStream().close();

		} catch (Exception e) {
			LOG.error("Could not Download Evaluation Summary Report. " + e.getMessage(), e);
		} finally {
			if (virtualizer != null) {
				virtualizer.cleanup();
			}
		}

	}

	@RequestMapping(path = "/downloadEvaluationConclustionReport/{eventId}", method = RequestMethod.POST)
	public void downloadEvaluationConclustionReport(@PathVariable("eventId") String eventId, HttpServletResponse response, HttpSession session) throws Exception {
		JRSwapFileVirtualizer virtualizer = null;
		try {
			// Virtualizar - To increase the performance
			virtualizer = new JRSwapFileVirtualizer(300, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);
			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}
			String filename = "EvaluationConclusionReport.pdf";
			JasperPrint jasperPrint = rfaEventService.generatePdfforEvaluationConclusion(eventId, timeZone, virtualizer);
			if (jasperPrint != null) {
				streamReport(jasperPrint, filename, response);
			}
			try {
				RfaEventAudit audit = new RfaEventAudit();
				audit.setActionBy(SecurityLibrary.getLoggedInUser());
				audit.setActionDate(new Date());
				audit.setDescription("Evaluation Conclusion Report is downloaded ");
				RfaEvent event = new RfaEvent();
				event.setId(eventId);
				audit.setEvent(event);
				audit.setAction(AuditActionType.Download);
				eventAuditService.save(audit);

				RfaEvent rfaEvent = rfaEventService.getPlainEventById(eventId);
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Evaluation Conclusion Report is downloaded for Event ‘" + rfaEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFA);
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
	public String updateEventSuspApproval(@ModelAttribute RfaEvent event, RedirectAttributes redir, HttpSession session) {
		JRSwapFileVirtualizer virtualizer = null;
		try {
			virtualizer = new JRSwapFileVirtualizer(300, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);
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
		return "redirect:/buyer/" + RfxTypes.RFA.name() + "/eventSummary/" + event.getId();
	}
}

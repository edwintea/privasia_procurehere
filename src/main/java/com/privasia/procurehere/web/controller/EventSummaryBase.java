package com.privasia.procurehere.web.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.privasia.procurehere.core.entity.*;
import com.privasia.procurehere.core.pojo.EventPermissions;

import com.privasia.procurehere.service.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.RfaEnvelopDao;
import com.privasia.procurehere.core.dao.RfaEventDao;
import com.privasia.procurehere.core.dao.RfaSupplierTeamMemberDao;
import com.privasia.procurehere.core.dao.RfiEnvelopDao;
import com.privasia.procurehere.core.dao.RfiEventDao;
import com.privasia.procurehere.core.dao.RfiSupplierTeamMemberDao;
import com.privasia.procurehere.core.dao.RfpEnvelopDao;
import com.privasia.procurehere.core.dao.RfpEventDao;
import com.privasia.procurehere.core.dao.RfpSupplierTeamMemberDao;
import com.privasia.procurehere.core.dao.RfqEnvelopDao;
import com.privasia.procurehere.core.dao.RfqEventDao;
import com.privasia.procurehere.core.dao.RfqSupplierTeamMemberDao;
import com.privasia.procurehere.core.dao.RftEnvelopDao;
import com.privasia.procurehere.core.dao.RftEventDao;
import com.privasia.procurehere.core.dao.RftEventSupplierDao;
import com.privasia.procurehere.core.dao.RftSupplierBqDao;
import com.privasia.procurehere.core.dao.RftSupplierCqItemDao;
import com.privasia.procurehere.core.dao.RftSupplierTeamMemberDao;
import com.privasia.procurehere.core.enums.AuditActionType;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.NotificationType;
import com.privasia.procurehere.core.enums.PlanType;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.SubscriptionStatus;
import com.privasia.procurehere.core.exceptions.SubscriptionException;
import com.privasia.procurehere.core.pojo.FeePojo;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.Constant;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.impl.SnapShotAuditService;

import freemarker.template.Configuration;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;
import net.sf.jasperreports.engine.util.JRSwapFile;

public class EventSummaryBase {

	protected static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@javax.annotation.Resource
	MessageSource messageSource;

	@Autowired
	RftEventService rftEventService;

	@Autowired
	RfqEventService rfqEventService;

	@Autowired
	RfpEventService rfpEventService;

	@Autowired
	RfiEventService rfiEventService;

	@Autowired
	RfaEventService rfaEventService;

	@Autowired
	EventAuditService eventAuditService;

	@Autowired
	RftDocumentService rftDocumentService;

	@Autowired
	ApprovalService approvalService;

	@Autowired
	EventMessageService eventMessageService;

	@Autowired
	RftSupplierCqItemService rftSupplierCqItemService;

	@Autowired
	RftEventSupplierDao rftEventSupplierDao;

	@Autowired
	RftEventSupplierService rftEventSupplierService;

	@Autowired
	RfaEventSupplierService rfaEventSupplierService;

	@Autowired
	RfiEventSupplierService rfiEventSupplierService;

	@Autowired
	RfpEventSupplierService rfpEventSupplierService;

	@Autowired
	RfqEventSupplierService rfqEventSupplierService;

	@Autowired
	RftSupplierCqItemDao supplierCqItemDao;

	@Autowired
	RftSupplierBqDao rftSupplierBqDao;

	@Autowired
	UomService uomService;

	private RfxTypes eventType;

	@Autowired
	BuyerService buyerService;

	@Autowired
	NotificationService notificationService;

	@Autowired
	Configuration freemarkerConfiguration;

	@Autowired
	DashboardNotificationService dashboardNotificationService;

	@Autowired
	OwnerSettingsService ownerSettingsService;

	@Autowired
	UserService userService;

	@Autowired
	SupplierSettingsService supplierSettingsService;

	@Autowired
	RfaEventDao rfaEventDao;

	@Autowired
	RfiEventDao rfiEventDao;

	@Autowired
	RfpEventDao rfpEventDao;

	@Autowired
	RfqEventDao rfqEventDao;

	@Autowired
	RftEventDao rftEventDao;

	@Autowired
	RfaSupplierTeamMemberDao rfaSupplierTeamMemberDao;

	@Autowired
	RfiSupplierTeamMemberDao rfiSupplierTeamMemberDao;

	@Autowired
	RfpSupplierTeamMemberDao rfpSupplierTeamMemberDao;

	@Autowired
	RfqSupplierTeamMemberDao rfqSupplierTeamMemberDao;

	@Autowired
	RftSupplierTeamMemberDao rftSupplierTeamMemberDao;

	@Autowired
	BuyerSettingsService buyerSettingsService;

	@Autowired
	ApplicationContext applicationContext;

	@Autowired
	RfiEnvelopDao rfiEnvelopDao;

	@Autowired
	RfaEnvelopDao rfaEnvelopDao;

	@Autowired
	RfpEnvelopDao rfpEnvelopDao;

	@Autowired
	RftEnvelopDao rftEnvelopDao;

	@Autowired
	RfqEnvelopDao rfqEnvelopDao;

	@Autowired
	SuspensionApprovalService suspensionApprovalService;

	@Autowired
	SnapShotAuditService snapShotAuditService;

	@Autowired
	SimpMessagingTemplate simpMessagingTemplate;

	@Autowired
	JmsTemplate jmsTemplate;

	@Autowired
	TatReportService tatReportService;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Autowired
	PoEventService poEventService;

	@Value("${app.url}")
	String APP_URL;

	public EventSummaryBase(RfxTypes eventType) {
		this.eventType = eventType;
	}

	@ModelAttribute("eventType")
	public RfxTypes getEventType() {
		return eventType;
	}

	/**
	 * @param eventType the eventType to set
	 */
	public void setEventType(RfxTypes eventType) {
		this.eventType = eventType;
	}

	@ModelAttribute("step")
	public String getStep() {
		return "9";
	}

	@ModelAttribute("ownerSettings")
	public OwnerSettings getOwnersettings() {
		return ownerSettingsService.getOwnersettings();
	}

	@RequestMapping(path = "/approve", method = RequestMethod.POST)
	public String approveEvent(@RequestParam(name = "id") String id, @RequestParam String remarks, RedirectAttributes redir, HttpSession session) {
		LOG.info(SecurityLibrary.getLoggedInUserLoginId() + " is Approving " + getEventType() + " Event : " + id + " with remarks : " + remarks);
		JRSwapFileVirtualizer virtualizer = null;
		virtualizer = new JRSwapFileVirtualizer(100, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);
		Date actionDate = new Date();
		try {
			Event theEvent = null;
			byte[] summarySnapshot = null;
			switch (getEventType()) {
			case RFA: {
				RfaEvent event = new RfaEvent();
				event.setId(id);
				theEvent = approvalService.doApproval(event, SecurityLibrary.getLoggedInUser(), remarks, true, session, virtualizer, actionDate);
				try {
					JasperPrint eventSummary = rfaEventService.getEvaluationSummaryPdf(event, SecurityLibrary.getLoggedInUser(), (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), virtualizer);
					summarySnapshot = JasperExportManager.exportReportToPdf(eventSummary);
					RfaEventAudit audit = new RfaEventAudit(SecurityLibrary.getLoggedInUser().getBuyer(), event, SecurityLibrary.getLoggedInUser(), actionDate, AuditActionType.Approve, messageSource.getMessage("event.audit.approved", new Object[] { theEvent.getEventName() }, Global.LOCALE), summarySnapshot);
					eventAuditService.save(audit);
					try {
						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.APPROVED, "Event '" + theEvent.getEventId() + "' Approved", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), actionDate, ModuleType.RFA);
						buyerAuditTrailDao.save(buyerAuditTrail);
					} catch (Exception e) {
						LOG.info("Error to create audit trails message");
					}
					LOG.info("*********** RFA Approved and Event Audit Saved Successfully ************ ");
				} catch (Exception e) {
					LOG.info("Error saving RFA Event Audit " + e.getMessage());
				}
				break;
			}
			case RFI: {
				RfiEvent event = new RfiEvent();
				event.setId(id);
				theEvent = approvalService.doApproval(event, SecurityLibrary.getLoggedInUser(), remarks, true, session, virtualizer, actionDate);
				try {
					JasperPrint eventSummary = rfiEventService.getEvaluationSummaryPdf(event, SecurityLibrary.getLoggedInUser(), (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), virtualizer);
					summarySnapshot = JasperExportManager.exportReportToPdf(eventSummary);
					RfiEventAudit audit = new RfiEventAudit(SecurityLibrary.getLoggedInUser().getBuyer(), event, SecurityLibrary.getLoggedInUser(), actionDate, AuditActionType.Approve, messageSource.getMessage("event.audit.approved", new Object[] { theEvent.getEventName() }, Global.LOCALE), summarySnapshot);
					eventAuditService.save(audit);
					try {
						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.APPROVED, "Event '" + theEvent.getEventId() + "' Approved", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), actionDate, ModuleType.RFI);
						buyerAuditTrailDao.save(buyerAuditTrail);
					} catch (Exception e) {
						LOG.info("Error to create audit trails message");
					}
					LOG.info("*********** RFI Approved and Event Audit Saved Successfully ************ ");
				} catch (Exception e) {
					LOG.info("Error saving RFI Event Audit " + e.getMessage());
				}
				break;
			}
			case RFP: {
				RfpEvent event = new RfpEvent();
				event.setId(id);
				theEvent = approvalService.doApproval(event, SecurityLibrary.getLoggedInUser(), remarks, true, session, virtualizer, actionDate);
				try {
					JasperPrint eventSummary = rfpEventService.getEvaluationSummaryPdf(event, SecurityLibrary.getLoggedInUser(), (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), virtualizer);
					summarySnapshot = JasperExportManager.exportReportToPdf(eventSummary);
					RfpEventAudit audit = new RfpEventAudit(SecurityLibrary.getLoggedInUser().getBuyer(), event, SecurityLibrary.getLoggedInUser(), actionDate, AuditActionType.Approve, messageSource.getMessage("event.audit.approved", new Object[] { theEvent.getEventName() }, Global.LOCALE), summarySnapshot);
					eventAuditService.save(audit);
					try {
						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.APPROVED, "Event '" + theEvent.getEventId() + "' Approved", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), actionDate, ModuleType.RFP);
						buyerAuditTrailDao.save(buyerAuditTrail);
					} catch (Exception e) {
						LOG.info("Error to create audit trails message");
					}
					LOG.info("*********** RFP Approved and Event Audit Saved Successfully ************ ");
				} catch (Exception e) {
					LOG.info("Error saving RFP Event Audit " + e.getMessage());
				}
				break;
			}
			case RFQ: {
				RfqEvent event = new RfqEvent();
				event.setId(id);
				theEvent = approvalService.doApproval(event, SecurityLibrary.getLoggedInUser(), remarks, true, session, virtualizer, actionDate);

				try {
					JasperPrint eventSummary = rfqEventService.getEvaluationSummaryPdf(event, SecurityLibrary.getLoggedInUser(), (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), virtualizer);
					summarySnapshot = JasperExportManager.exportReportToPdf(eventSummary);
					RfqEventAudit audit = new RfqEventAudit(SecurityLibrary.getLoggedInUser().getBuyer(), event, SecurityLibrary.getLoggedInUser(), actionDate, AuditActionType.Approve, messageSource.getMessage("event.audit.approved", new Object[] { theEvent.getEventName() }, Global.LOCALE), summarySnapshot);
					eventAuditService.save(audit);
					try {
						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.APPROVED, "Event '" + theEvent.getEventId() + "' Approved", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), actionDate, ModuleType.RFQ);
						buyerAuditTrailDao.save(buyerAuditTrail);
					} catch (Exception e) {
						LOG.info("Error to create audit trails message");
					}
					LOG.info("*********** RFQ Approved and Event Audit Saved Successfully ************ ");
				} catch (Exception e) {
					LOG.info("Error saving RFQ Event Audit " + e.getMessage());
				}
				break;
			}
			case RFT: {
				RftEvent event = new RftEvent();
				event.setId(id);
				theEvent = approvalService.doApproval(event, SecurityLibrary.getLoggedInUser(), remarks, true, session, virtualizer, actionDate);
				try {
					JasperPrint eventSummary = rftEventService.getEvaluationSummaryPdf(event, SecurityLibrary.getLoggedInUser(), (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), virtualizer);
					summarySnapshot = JasperExportManager.exportReportToPdf(eventSummary);
					RftEventAudit audit = new RftEventAudit(SecurityLibrary.getLoggedInUser().getBuyer(), event, SecurityLibrary.getLoggedInUser(), actionDate, AuditActionType.Approve, messageSource.getMessage("event.audit.approved", new Object[] { theEvent.getEventName() }, Global.LOCALE), summarySnapshot);
					eventAuditService.save(audit);
					try {
						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.APPROVED, "Event '" + theEvent.getEventId() + "' Approved", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), actionDate, ModuleType.RFT);
						buyerAuditTrailDao.save(buyerAuditTrail);
					} catch (Exception e) {
						LOG.info("Error to create audit trails message");
					}
					LOG.info("*********** RFT Approved and Event Audit Saved Successfully ************ ");
				} catch (Exception e) {
					LOG.info("Error saving RFT Event Audit " + e.getMessage());
				}
				break;
			}
			default:
				break;
			}
			// redir.addFlashAttribute("success", getEventType().name() + " - '" + theEvent.getEventName() + "' has been
			// approved");
			redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.event.approved", new Object[] { (getEventType().name() != null ? getEventType().name() : ""), (theEvent.getEventName() != null ? theEvent.getEventName() : "") }, Global.LOCALE));
		} catch (Exception e) {
			LOG.info("Error while Approving Event :" + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.approving", new Object[] { e.getMessage() }, Global.LOCALE));
			return "redirect:/buyer/" + getEventType().name() + "/eventSummary/" + id;
		} finally {
			if (virtualizer != null) {
				virtualizer.cleanup();
			}
		}
		return "redirect:/buyer/buyerDashboard";
	}

	@RequestMapping(path = "/reject", method = RequestMethod.POST)
	public String rejectEvent(@RequestParam(name = "id") String id, @RequestParam String remarks, RedirectAttributes redir, HttpSession session) {
		LOG.info(SecurityLibrary.getLoggedInUserLoginId() + " is Rejecting " + getEventType() + " Event : " + id + " with remarks : " + remarks);
		JRSwapFileVirtualizer virtualizer = null;
		Date actionDate = new Date();
		try {
			virtualizer = new JRSwapFileVirtualizer(300, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);
			if (StringUtils.checkString(remarks).length() == 0) {
				redir.addFlashAttribute("error", messageSource.getMessage("flasherror.remark.required.rejection", new Object[] {}, Global.LOCALE));
				return "redirect:/buyer/" + getEventType().name() + "/eventSummary/" + id;
			}
			Event theEvent = null;
			switch (getEventType()) {
			case RFA: {
				RfaEvent event = new RfaEvent();
				event.setId(id);
				theEvent = approvalService.doApproval(event, SecurityLibrary.getLoggedInUser(), remarks, false, session, virtualizer, actionDate);
				break;
			}
			case RFI: {
				RfiEvent event = new RfiEvent();
				event.setId(id);
				theEvent = approvalService.doApproval(event, SecurityLibrary.getLoggedInUser(), remarks, false, session, virtualizer, actionDate);
				break;
			}
			case RFP: {
				RfpEvent event = new RfpEvent();
				event.setId(id);
				theEvent = approvalService.doApproval(event, SecurityLibrary.getLoggedInUser(), remarks, false, session, virtualizer, actionDate);
				break;
			}
			case RFQ: {
				RfqEvent event = new RfqEvent();
				event.setId(id);
				theEvent = approvalService.doApproval(event, SecurityLibrary.getLoggedInUser(), remarks, false, session, virtualizer, actionDate);
				break;
			}
			case RFT: {
				RftEvent event = new RftEvent();
				event.setId(id);
				theEvent = approvalService.doApproval(event, SecurityLibrary.getLoggedInUser(), remarks, false, session, virtualizer, actionDate);
				break;
			}
			default:
				break;
			}
			redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.event.rejected", new Object[] { (getEventType().name() != null ? getEventType().name() : ""), (theEvent.getEventName() != null ? theEvent.getEventName() : "") }, Global.LOCALE));
		} catch (Exception e) {
			LOG.info("Error while Rejecting Event :" + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.rejecting", new Object[] { e.getMessage() }, Global.LOCALE));
			return "redirect:/buyer/" + getEventType().name() + "/eventSummary/" + id;
		} finally {
			if (virtualizer != null) {
				virtualizer.cleanup();
			}
		}
		return "redirect:/buyer/buyerDashboard";
	}

	/**
	 * @param response
	 * @param docs
	 * @throws IOException
	 */
	protected void buildDocumentFile(HttpServletResponse response, EventDocument docs) throws IOException {
		response.setContentType(docs.getCredContentType());
		response.setContentLength(docs.getFileData().length);
		response.setHeader("Content-Disposition", "attachment; filename=\"" + docs.getFileName() + "\"");
		FileCopyUtils.copy(docs.getFileData(), response.getOutputStream());
		response.flushBuffer();
		response.setStatus(HttpServletResponse.SC_OK);
	}

	/**
	 * @param response
	 * @param docs
	 * @throws IOException
	 */
	protected void buildMeetingFile(HttpServletResponse response, EventMeetingDocument docs) throws IOException {
		response.setContentType(docs.getCredContentType());
		response.setContentLength(docs.getFileData().length);
		response.setHeader("Content-Disposition", "attachment; filename=\"" + docs.getFileName() + "\"");
		FileCopyUtils.copy(docs.getFileData(), response.getOutputStream());
		response.flushBuffer();
		response.setStatus(HttpServletResponse.SC_OK);
	}

	@RequestMapping(path = "/eventMessages/{eventId}", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<TableData<?>> getEventMessages(@PathVariable("eventId") String eventId, @RequestParam int page, @RequestParam int size, @RequestParam(required = false) String search, Model model) {
		HttpHeaders headers = new HttpHeaders();
		try {
			LOG.info("Fetching event messages for event : " + eventId + ", page : " + page + ", size : " + size + ", search : " + search);
			switch (getEventType()) {
			case RFA: {
				List<RfaEventMessage> messages = eventMessageService.getRfaEventMessagesByEventId(eventId, page, size, search);
				TableData<?> data = new TableData<RfaEventMessage>(messages, eventMessageService.getTotalRfaEventMessageCount(eventId), eventMessageService.getTotalFilteredRfaEventMessageCount(eventId, search));
				return new ResponseEntity<TableData<?>>(data, headers, HttpStatus.OK);
			}
			case RFI: {
				List<RfiEventMessage> messages = eventMessageService.getRfiEventMessagesByEventId(eventId, page, size, search);
				TableData<?> data = new TableData<RfiEventMessage>(messages, eventMessageService.getTotalRfiEventMessageCount(eventId), eventMessageService.getTotalFilteredRfiEventMessageCount(eventId, search));
				return new ResponseEntity<TableData<?>>(data, headers, HttpStatus.OK);
			}
			case RFP: {
				List<RfpEventMessage> messages = eventMessageService.getRfpEventMessagesByEventId(eventId, page, size, search);
				TableData<?> data = new TableData<RfpEventMessage>(messages, eventMessageService.getTotalRfpEventMessageCount(eventId), eventMessageService.getTotalFilteredRfpEventMessageCount(eventId, search));
				return new ResponseEntity<TableData<?>>(data, headers, HttpStatus.OK);
			}
			case RFQ: {
				List<RfqEventMessage> messages = eventMessageService.getRfqEventMessagesByEventId(eventId, page, size, search);
				TableData<?> data = new TableData<RfqEventMessage>(messages, eventMessageService.getTotalRfqEventMessageCount(eventId), eventMessageService.getTotalFilteredRfqEventMessageCount(eventId, search));
				return new ResponseEntity<TableData<?>>(data, headers, HttpStatus.OK);
			}
			case RFT: {
				List<RftEventMessage> messages = eventMessageService.getRftEventMessagesByEventId(eventId, page, size, search);
				TableData<?> data = new TableData<RftEventMessage>(messages, eventMessageService.getTotalRftEventMessageCount(eventId), eventMessageService.getTotalFilteredRftEventMessageCount(eventId, search));
				return new ResponseEntity<TableData<?>>(data, headers, HttpStatus.OK);
			}
			case PO: {
				List<PoEventMessage> messages = eventMessageService.getPoEventMessagesByEventId(eventId, page, size, search);
				TableData<?> data = new TableData<PoEventMessage>(messages, eventMessageService.getTotalPoEventMessageCount(eventId), eventMessageService.getTotalFilteredPoEventMessageCount(eventId, search));
				return new ResponseEntity<TableData<?>>(data, headers, HttpStatus.OK);
			}
			default:
			}
		} catch (Exception e) {
			LOG.error("Error fetching event messages : " + e.getMessage(), e);
			headers.add("error", "Error fetching event messages : " + e.getMessage());
		}
		return new ResponseEntity<TableData<?>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@RequestMapping(path = "/sendMessage/{eventId}", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<TableData<?>> sendMessage(@PathVariable String eventId, @ModelAttribute RftEventMessage message, @RequestParam(required = false) int page, @RequestParam(required = false) int size, @RequestParam(required = false) String search, @RequestParam(required = false) String parentId, @RequestParam(value = "file", required = false) MultipartFile file, BindingResult result, Model model) {
		LOG.info("eventId " + eventId + " getEventType() :" + getEventType());
		HttpHeaders headers = new HttpHeaders();
		try {
			if (result.hasErrors()) {
				for (ObjectError err : result.getAllErrors()) {
					headers.add("error", err.getDefaultMessage());
				}
				return new ResponseEntity<TableData<?>>(null, headers, HttpStatus.EXPECTATION_FAILED);
			} else {
				String fileName = null;
				switch (getEventType()) {
				case RFA: {
					RfaEvent rfaEvent = rfaEventService.getRfaEventByeventId(eventId);
					RfaEventMessage messageObj = new RfaEventMessage();
					messageObj.setMessage(message.getMessage());
					messageObj.setSubject(message.getSubject());
					messageObj.setSuppliers(message.getSuppliers());
					messageObj.setEvent(rfaEvent);
					messageObj.setSentByBuyer(Boolean.TRUE);
					messageObj.setCreatedBy(SecurityLibrary.getLoggedInUser());
					messageObj.setCreatedDate(new Date());
					if (file != null && !file.isEmpty()) {
						fileName = file.getOriginalFilename();
						byte[] bytes = file.getBytes();
						LOG.info("FILE CONTENT   " + file.getName());
						messageObj.setContentType(file.getContentType());
						messageObj.setFileName(fileName);
						messageObj.setFileAttatchment(bytes);
					}
					if (StringUtils.checkString(parentId).length() > 0) {
						RfaEventMessage parent = new RfaEventMessage();
						parent.setId(parentId);
						messageObj.setParent(parent);
					}
					LOG.info("Saving rfaMessage : " + messageObj.toLogString());
					messageObj = eventMessageService.saveEventMessage(messageObj);
					String id = messageObj.getId();
					try {
						// eventMessageService.sendDashboardNotificationForEventMessage(messageObj);
						jmsTemplate.send("QUEUE.EVENT.MESSAGE", new MessageCreator() {
							@Override
							public Message createMessage(Session session) throws JMSException {
								TextMessage objectMessage = session.createTextMessage();
								objectMessage.setText(RfxTypes.RFA.name() + id);
								return objectMessage;
							}
						});

					} catch (Exception e) {
						LOG.error("Error While sending notification For Event Message :" + e.getMessage(), e);
					}
					List<RfaEventMessage> messages = eventMessageService.getRfaEventMessagesByEventId(eventId, page, size, search);
					TableData<?> data = new TableData<RfaEventMessage>(messages, eventMessageService.getTotalRfaEventMessageCount(eventId), eventMessageService.getTotalFilteredRfaEventMessageCount(eventId, search));
					headers.add("success", "Event message with subject '" + message.getSubject() + "' sent successfully");
					return new ResponseEntity<TableData<?>>(data, headers, HttpStatus.OK);
				}
				case RFI: {
					RfiEvent rfiEvent = rfiEventService.getRfiEventByeventId(eventId);
					RfiEventMessage messageObj = new RfiEventMessage();
					messageObj.setMessage(message.getMessage());
					messageObj.setSubject(message.getSubject());
					messageObj.setSuppliers(message.getSuppliers());
					messageObj.setEvent(rfiEvent);
					messageObj.setSentByBuyer(Boolean.TRUE);
					messageObj.setCreatedBy(SecurityLibrary.getLoggedInUser());
					messageObj.setCreatedDate(new Date());
					if (file != null && !file.isEmpty()) {
						fileName = file.getOriginalFilename();
						byte[] bytes = file.getBytes();
						LOG.info("FILE CONTENT   " + file.getName());
						messageObj.setContentType(file.getContentType());
						messageObj.setFileName(fileName);
						messageObj.setFileAttatchment(bytes);
					}
					if (StringUtils.checkString(parentId).length() > 0) {
						RfiEventMessage parent = new RfiEventMessage();
						parent.setId(parentId);
						messageObj.setParent(parent);
					}
					LOG.info("Saving rfiMessage : " + messageObj.toLogString());
					messageObj = eventMessageService.saveEventMessage(messageObj);
					String id = messageObj.getId();
					try {
						jmsTemplate.send("QUEUE.EVENT.MESSAGE", new MessageCreator() {
							@Override
							public Message createMessage(Session session) throws JMSException {
								TextMessage objectMessage = session.createTextMessage();
								objectMessage.setText(RfxTypes.RFI.name() + id);
								return objectMessage;
							}
						});

						// eventMessageService.sendDashboardNotificationForEventMessage(messageObj);
					} catch (Exception e) {
						LOG.error("Error While sending notification For Event Message :" + e.getMessage(), e);
					}
					// List<RfiEventMessage> messages = eventMessageService.getRfiEventMessages(eventId, page, size,
					// search);
					List<RfiEventMessage> messages = eventMessageService.getRfiEventMessagesByEventId(eventId, page, size, search);
					TableData<?> data = new TableData<RfiEventMessage>(messages, eventMessageService.getTotalRfiEventMessageCount(eventId), eventMessageService.getTotalFilteredRfiEventMessageCount(eventId, search));
					headers.add("success", "Event message with subject '" + message.getSubject() + "' sent successfully");
					return new ResponseEntity<TableData<?>>(data, headers, HttpStatus.OK);
				}
				case RFP: {
					RfpEvent rfpEvent = rfpEventService.getRfpEventByeventId(eventId);
					RfpEventMessage messageObj = new RfpEventMessage();
					messageObj.setMessage(message.getMessage());
					messageObj.setSubject(message.getSubject());
					messageObj.setSuppliers(message.getSuppliers());
					messageObj.setEvent(rfpEvent);
					messageObj.setSentByBuyer(Boolean.TRUE);
					messageObj.setCreatedBy(SecurityLibrary.getLoggedInUser());
					messageObj.setCreatedDate(new Date());
					if (file != null && !file.isEmpty()) {
						fileName = file.getOriginalFilename();
						byte[] bytes = file.getBytes();
						LOG.info("FILE CONTENT   " + file.getName());
						messageObj.setContentType(file.getContentType());
						messageObj.setFileName(fileName);
						messageObj.setFileAttatchment(bytes);
					}
					if (StringUtils.checkString(parentId).length() > 0) {
						RfpEventMessage parent = new RfpEventMessage();
						parent.setId(parentId);
						messageObj.setParent(parent);
					}
					LOG.info("Saving rfpMessage : " + messageObj.toLogString());
					messageObj = eventMessageService.saveEventMessage(messageObj);
					String id = messageObj.getId();
					try {
						jmsTemplate.send("QUEUE.EVENT.MESSAGE", new MessageCreator() {
							@Override
							public Message createMessage(Session session) throws JMSException {
								TextMessage objectMessage = session.createTextMessage();
								objectMessage.setText(RfxTypes.RFP.name() + id);
								return objectMessage;
							}
						});
						// eventMessageService.sendDashboardNotificationForEventMessage(messageObj);
					} catch (Exception e) {
						LOG.error("Error While sending notification For Event Message :" + e.getMessage(), e);
					}
					List<RfpEventMessage> messages = eventMessageService.getRfpEventMessagesByEventId(eventId, page, size, search);
					TableData<?> data = new TableData<RfpEventMessage>(messages, eventMessageService.getTotalRfpEventMessageCount(eventId), eventMessageService.getTotalFilteredRfpEventMessageCount(eventId, search));
					headers.add("success", "Event message with subject '" + message.getSubject() + "' sent successfully");
					return new ResponseEntity<TableData<?>>(data, headers, HttpStatus.OK);
				}
				case RFQ: {
					RfqEvent rfqEvent = rfqEventService.getRfqEventByeventId(eventId);
					RfqEventMessage messageObj = new RfqEventMessage();
					messageObj.setMessage(message.getMessage());
					messageObj.setSubject(message.getSubject());
					messageObj.setSuppliers(message.getSuppliers());
					messageObj.setEvent(rfqEvent);
					// messageObj.setSentBySupplier(Boolean.FALSE);
					messageObj.setSentByBuyer(Boolean.TRUE);
					messageObj.setCreatedBy(SecurityLibrary.getLoggedInUser());
					messageObj.setCreatedDate(new Date());
					if (file != null && !file.isEmpty()) {
						fileName = file.getOriginalFilename();
						byte[] bytes = file.getBytes();
						LOG.info("FILE CONTENT   " + file.getName());
						messageObj.setContentType(file.getContentType());
						messageObj.setFileName(fileName);
						messageObj.setFileAttatchment(bytes);
					}
					if (StringUtils.checkString(parentId).length() > 0) {
						RfqEventMessage parent = new RfqEventMessage();
						parent.setId(parentId);
						messageObj.setParent(parent);
					}
					LOG.info("Saving rfqmessage : " + messageObj.toLogString());
					messageObj = eventMessageService.saveEventMessage(messageObj);
					String id = messageObj.getId();
					try {
						jmsTemplate.send("QUEUE.EVENT.MESSAGE", new MessageCreator() {
							@Override
							public Message createMessage(Session session) throws JMSException {
								TextMessage objectMessage = session.createTextMessage();
								objectMessage.setText(RfxTypes.RFQ.name() + id);
								return objectMessage;
							}
						});
						// eventMessageService.sendDashboardNotificationForEventMessage(messageObj);
					} catch (Exception e) {
						LOG.error("Error While sending notification For Event Message :" + e.getMessage(), e);
					}
					List<RfqEventMessage> messages = eventMessageService.getRfqEventMessagesByEventId(eventId, page, size, search);
					TableData<?> data = new TableData<RfqEventMessage>(messages, eventMessageService.getTotalRfqEventMessageCount(eventId), eventMessageService.getTotalFilteredRfqEventMessageCount(eventId, search));
					headers.add("success", "Event message with subject '" + message.getSubject() + "' sent successfully");
					return new ResponseEntity<TableData<?>>(data, headers, HttpStatus.OK);
				}
				case RFT: {
					RftEvent rftEvent = rftEventService.getRftEventByeventId(eventId);
					RftEventMessage messageObj = new RftEventMessage();
					messageObj.setMessage(message.getMessage());
					messageObj.setSubject(message.getSubject());
					messageObj.setSuppliers(message.getSuppliers());
					messageObj.setEvent(rftEvent);
					messageObj.setCreatedBy(SecurityLibrary.getLoggedInUser());
					messageObj.setCreatedDate(new Date());
					messageObj.setSentByBuyer(Boolean.TRUE);
					if (file != null && !file.isEmpty()) {
						fileName = file.getOriginalFilename();
						byte[] bytes = file.getBytes();
						LOG.info("FILE CONTENT   " + file.getName());
						messageObj.setContentType(file.getContentType());
						messageObj.setFileName(fileName);
						messageObj.setFileAttatchment(bytes);
					}
					if (StringUtils.checkString(parentId).length() > 0) {
						LOG.info(parentId);
						RftEventMessage parent = new RftEventMessage();
						parent.setId(parentId);
						messageObj.setParent(parent);
					}
					LOG.info("Saving rftmessage : " + messageObj.toLogString());
					messageObj = eventMessageService.saveEventMessage(messageObj);
					String id = messageObj.getId();
					try {
						jmsTemplate.send("QUEUE.EVENT.MESSAGE", new MessageCreator() {
							@Override
							public Message createMessage(Session session) throws JMSException {
								TextMessage objectMessage = session.createTextMessage();
								objectMessage.setText(RfxTypes.RFQ.name() + id);
								return objectMessage;
							}
						});
						// eventMessageService.sendDashboardNotificationForEventMessage(messageObj);
					} catch (Exception e) {
						LOG.error("Error While sending notification For Event Message :" + e.getMessage(), e);
					}
					List<RftEventMessage> messages = eventMessageService.getRftEventMessagesByEventId(eventId, page, size, search);

					// List<RftEventMessage> messages = eventMessageService.getRftEventMessages(eventId, page, size,
					// search);
					TableData<?> data = new TableData<RftEventMessage>(messages, eventMessageService.getTotalRftEventMessageCount(eventId), eventMessageService.getTotalFilteredRftEventMessageCount(eventId, search));
					headers.add("success", "Event message with subject '" + message.getSubject() + "' sent successfully");
					return new ResponseEntity<TableData<?>>(data, headers, HttpStatus.OK);
				}
				case PO: {
					LOG.info(" >>>>>>>>>>>>>>>>>  Event PO was triggered");
					PoEvent poEvent = poEventService.getPoEventByeventId(eventId);
					PoEventMessage messageObj = new PoEventMessage();
					messageObj.setMessage(message.getMessage());
					messageObj.setSubject(message.getSubject());
					messageObj.setSuppliers(message.getSuppliers());
					messageObj.setEvent(poEvent);
					messageObj.setSentByBuyer(Boolean.TRUE);
					messageObj.setCreatedBy(SecurityLibrary.getLoggedInUser());
					messageObj.setTenantId(SecurityLibrary.getLoggedInUserTenantId());
					messageObj.setCreatedDate(new Date());
					if (file != null && !file.isEmpty()) {
						fileName = file.getOriginalFilename();
						byte[] bytes = file.getBytes();
						LOG.info("FILE CONTENT   " + file.getName());
						messageObj.setContentType(file.getContentType());
						messageObj.setFileName(fileName);
						messageObj.setFileAttatchment(bytes);
					}
					if (StringUtils.checkString(parentId).length() > 0) {
						PoEventMessage parent = new PoEventMessage();
						parent.setId(parentId);
						messageObj.setParent(parent);
					}
					LOG.info("Saving po message : " + messageObj.toLogString());
					messageObj = eventMessageService.saveEventMessage(messageObj);
					String id = messageObj.getId();

					eventMessageService.sendNotificationForPoEventMessage(messageObj,true);

					try {
						jmsTemplate.send("QUEUE.EVENT.MESSAGE", new MessageCreator() {
							@Override
							public Message createMessage(Session session) throws JMSException {
								TextMessage objectMessage = session.createTextMessage();
								objectMessage.setText(RfxTypes.PO.name() + id);
								return objectMessage;
							}
						});

					} catch (Exception e) {
						LOG.error("Error While sending notification For Event Message :" + e.getMessage(), e);
					}
					List<PoEventMessage> messages = eventMessageService.getPoEventMessagesByEventId(eventId, page, size, search);
					TableData<?> data = new TableData<PoEventMessage>(messages, eventMessageService.getTotalPoEventMessageCount(eventId), eventMessageService.getTotalFilteredPoEventMessageCount(eventId, search));
					headers.add("success", "Event message with subject '" + message.getSubject() + "' sent successfully");
					return new ResponseEntity<TableData<?>>(data, headers, HttpStatus.OK);
				}
				default:
				}
			}
		} catch (Exception e) {
			LOG.error("Error Saving Message : " + e.getMessage(), e);
			headers.add("error", "Error saving event message : " + e.getMessage());
		}
		return new ResponseEntity<TableData<?>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Deprecated
	protected void sendSupplierInvitationEmailsOld(Event event, RfxTypes type) {
		List<String> suppNameList = new ArrayList<String>();
		String timeZone = "GMT+8:00";
		switch (type) {
		case RFA: {
			List<EventSupplier> suppliers = rfaEventSupplierService.getAllSuppliersByEventId(event.getId()); // ((RfaEvent)
			for (EventSupplier supplier : suppliers) {
				timeZone = getTimeZoneBySupplierSettings(supplier.getId(), timeZone); // event).getSuppliers();
				if (Boolean.TRUE == supplier.getNotificationSent()) {
					continue;
				}
				sendNotificationToSupplier(event, type, supplier.getSupplier(), timeZone);
				supplier.setNotificationSent(Boolean.TRUE);
				rfaEventSupplierService.updateRfaEventSuppliers((RfaEventSupplier) supplier);
				suppNameList.add(supplier.getSupplierCompanyName());
			}
			if (CollectionUtil.isNotEmpty(suppNameList)) {
				User eventOwner = rfaEventDao.getPlainEventOwnerByEventId(event.getId());
				timeZone = getTimeZoneByBuyerSettings(eventOwner.getTenantId(), timeZone);
				sendNotificationToBuyer(event, type, eventOwner, suppNameList, timeZone);

				// send event notifications for buyer team members
				List<User> rfaBuyerMembers = rfaEventDao.getUserBuyerTeamMemberByEventId(event.getId());
				if (CollectionUtil.isNotEmpty(rfaBuyerMembers)) {
					for (User buyerTeamUser : rfaBuyerMembers) {
						sendNotificationToBuyer(event, type, buyerTeamUser, suppNameList, timeZone);
					}
				}
			}
			break;
		}
		case RFI: {
			List<EventSupplier> suppliers = rfiEventSupplierService.getAllSuppliersByEventId(event.getId()); // ((RfiEvent)
																												// event).getSuppliers();
			for (EventSupplier supplier : suppliers) {
				timeZone = getTimeZoneBySupplierSettings(supplier.getId(), timeZone);
				if (Boolean.TRUE == supplier.getNotificationSent()) {
					continue;
				}
				sendNotificationToSupplier(event, type, supplier.getSupplier(), timeZone);
				supplier.setNotificationSent(Boolean.TRUE);
				rfiEventSupplierService.updateEventSuppliers((RfiEventSupplier) supplier);
				suppNameList.add(supplier.getSupplierCompanyName());
			}
			if (CollectionUtil.isNotEmpty(suppNameList)) {
				User eventOwner = rfiEventDao.getPlainEventOwnerByEventId(event.getId());
				timeZone = getTimeZoneByBuyerSettings(eventOwner.getTenantId(), timeZone);
				sendNotificationToBuyer(event, type, eventOwner, suppNameList, timeZone);

				// send event notifications for buyer team members
				List<User> rfiBuyerMembers = rfiEventDao.getUserBuyerTeamMemberByEventId(event.getId());
				if (CollectionUtil.isNotEmpty(rfiBuyerMembers)) {
					for (User buyerTeamUser : rfiBuyerMembers) {
						sendNotificationToBuyer(event, type, buyerTeamUser, suppNameList, timeZone);
					}
				}
			}
			break;
		}
		case RFP: {
			List<EventSupplier> suppliers = rfpEventSupplierService.getAllSuppliersByEventId(event.getId()); // ((RfpEvent)
																												// event).getSuppliers();
			for (EventSupplier supplier : suppliers) {
				timeZone = getTimeZoneBySupplierSettings(supplier.getId(), timeZone);
				if (Boolean.TRUE == supplier.getNotificationSent()) {
					continue;
				}
				sendNotificationToSupplier(event, type, supplier.getSupplier(), timeZone);
				supplier.setNotificationSent(Boolean.TRUE);
				rfpEventSupplierService.updateEventSuppliers((RfpEventSupplier) supplier);
				suppNameList.add(supplier.getSupplierCompanyName());
			}
			if (CollectionUtil.isNotEmpty(suppNameList)) {
				User eventOwner = rfpEventDao.getPlainEventOwnerByEventId(event.getId());
				timeZone = getTimeZoneByBuyerSettings(eventOwner.getTenantId(), timeZone);
				sendNotificationToBuyer(event, type, eventOwner, suppNameList, timeZone);

				// send event notifications for buyer team members
				List<User> rfpBuyerMembers = rfpEventDao.getUserBuyerTeamMemberByEventId(event.getId());
				if (CollectionUtil.isNotEmpty(rfpBuyerMembers)) {
					for (User buyerTeamUser : rfpBuyerMembers) {
						sendNotificationToBuyer(event, type, buyerTeamUser, suppNameList, timeZone);
					}
				}
			}
			break;
		}
		case RFQ: {
			List<EventSupplier> suppliers = rfqEventSupplierService.getAllSuppliersByEventId(event.getId()); // ((RfqEvent)
																												// event).getSuppliers();
			for (EventSupplier supplier : suppliers) {
				timeZone = getTimeZoneBySupplierSettings(supplier.getId(), timeZone);
				if (Boolean.TRUE == supplier.getNotificationSent()) {
					continue;
				}
				sendNotificationToSupplier(event, type, supplier.getSupplier(), timeZone);
				supplier.setNotificationSent(Boolean.TRUE);
				rfqEventSupplierService.updateEventSuppliers((RfqEventSupplier) supplier);
				suppNameList.add(supplier.getSupplierCompanyName());
			}
			if (CollectionUtil.isNotEmpty(suppNameList)) {
				User eventOwner = rfqEventDao.getPlainEventOwnerByEventId(event.getId());
				timeZone = getTimeZoneByBuyerSettings(eventOwner.getTenantId(), timeZone);
				sendNotificationToBuyer(event, type, eventOwner, suppNameList, timeZone);

				// send event notifications for buyer team members
				List<User> rfqBuyerMembers = rfqEventDao.getUserBuyerTeamMemberByEventId(event.getId());
				if (CollectionUtil.isNotEmpty(rfqBuyerMembers)) {
					for (User buyerTeamUser : rfqBuyerMembers) {
						sendNotificationToBuyer(event, type, buyerTeamUser, suppNameList, timeZone);
					}
				}
			}
			break;
		}
		case RFT: {
			List<EventSupplier> suppliers = rftEventSupplierService.getAllSuppliersByEventId(event.getId()); // ((RftEvent)
																												// event).getSuppliers();
			for (EventSupplier supplier : suppliers) {
				timeZone = getTimeZoneBySupplierSettings(supplier.getId(), timeZone);
				if (Boolean.TRUE == supplier.getNotificationSent()) {
					continue;
				}
				sendNotificationToSupplier(event, type, supplier.getSupplier(), timeZone);
				supplier.setNotificationSent(Boolean.TRUE);
				rftEventSupplierService.updateEventSuppliers((RftEventSupplier) supplier);
				suppNameList.add(supplier.getSupplierCompanyName());
			}
			if (CollectionUtil.isNotEmpty(suppNameList)) {
				User eventOwner = rftEventDao.getPlainEventOwnerByEventId(event.getId());
				timeZone = getTimeZoneByBuyerSettings(eventOwner.getTenantId(), timeZone);
				sendNotificationToBuyer(event, type, eventOwner, suppNameList, timeZone);

				// send event notifications for buyer team members
				List<User> rftBuyerMembers = rftEventDao.getUserBuyerTeamMemberByEventId(event.getId());
				if (CollectionUtil.isNotEmpty(rftBuyerMembers)) {
					for (User buyerTeamUser : rftBuyerMembers) {
						sendNotificationToBuyer(event, type, buyerTeamUser, suppNameList, timeZone);
					}
				}
			}
			break;
		}
		default:
			break;
		}

	}

	/**
	 * @param event
	 * @param type
	 * @param supplier
	 * @param timeZone
	 */
	private void sendNotificationToSupplier(Event event, RfxTypes type, Supplier supplier, String timeZone) {
		List<User> supUsers = userService.getAllAdminPlainUsersForSupplier(supplier.getId());
		for (User user : supUsers) {

			String mailTo = "";
			String subject = "Invitation to participate";
			String url = APP_URL + "/supplier/viewSupplierEvent/" + type.name() + "/" + event.getId();
			HashMap<String, Object> map = new HashMap<String, Object>();
			try {
				mailTo = user.getCommunicationEmail();
				map.put("userName", user.getName());
				map.put("event", event);
				map.put("eventStatus", event.getEventStart().after(new Date()) ? "upcoming" : "ongoing");
				map.put("eventType", type.name());
				map.put("businessUnit", StringUtils.checkString(findBusinessUnit(event.getId(), type)));
				SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
				df.setTimeZone(TimeZone.getTimeZone(timeZone));
				map.put("date", df.format(new Date()));
				map.put("appUrl", url);
				map.put("loginUrl", APP_URL + "/login");
				if(user.getEmailNotifications()) {
					sendEmail(mailTo, subject, map, Global.EVENT_INVITATION_TEMPLATE);
				}
			} catch (Exception e) {
				LOG.error("Error While sending mail For Event INVITATION :" + e.getMessage(), e);
			}
			try {
				String notificationMessage = messageSource.getMessage("event.invite.notification.message", new Object[] { event.getEventStart().after(new Date()) ? "upcoming" : "ongoing", event.getReferanceNumber() }, Global.LOCALE);
				sendDashboardNotification(user, url, subject, notificationMessage);
			} catch (Exception e) {
				LOG.error("Error While sending dashboard notification For Event INVITATION :" + e.getMessage(), e);
			}
		}
	}

	/**
	 * @param event
	 * @param type
	 * @param user
	 * @param suppList
	 * @param timeZone
	 */
	private void sendNotificationToBuyer(Event event, RfxTypes type, User user, List<String> suppList, String timeZone) {
		String mailTo = "";
		String subject = "Invitation to participate";
		String url = APP_URL + "/buyer/" + type.name() + "/eventSummary/" + event.getId();
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {
			mailTo = user.getCommunicationEmail();
			map.put("supplierList", suppList.toString());
			map.put("userName", user.getName());
			map.put("event", event);
			map.put("eventStatus", event.getStatus() != EventStatus.SUSPENDED ? "upcoming" : "ongoing");
			map.put("eventType", type.name());
			map.put("businessUnit", StringUtils.checkString(findBusinessUnit(event.getId(), type)));
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("date", df.format(new Date()));
			map.put("appUrl", url);
			map.put("loginUrl", APP_URL + "/login");
			if(user.getEmailNotifications()) {
				sendEmail(mailTo, subject, map, Global.EVENT_BUYER_INVITATION_TEMPLATE);
			}
		} catch (Exception e) {
			LOG.error("Error While sending mail For Event INVITATION :" + e.getMessage(), e);
		}
		try {
			String notificationMessage = messageSource.getMessage("event.buyer.invite.notification.message", new Object[] { event.getReferanceNumber() }, Global.LOCALE);
			sendDashboardNotification(user, url, subject, notificationMessage);
		} catch (Exception e) {
			LOG.error("Error While sending dashBoard notification For Event INVITATION :" + e.getMessage(), e);
		}
	}

	protected void sendRfxCreatedEmails(Event event, User user, RfxTypes type) {
		String mailTo = "";
		String subject = "Event Created";
		String url = APP_URL + "/buyer/" + type.name() + "/eventSummary/" + event.getId();
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {
			if (event != null && StringUtils.checkString(event.getEventName()).length() == 0) {
				event.setEventName("N/A");
			}
			mailTo = user.getCommunicationEmail();
			map.put("userName", user.getName());
			map.put("event", event);
			map.put("eventType", type.name());
			map.put("businessUnit", StringUtils.checkString(findBusinessUnit(event.getId(), type)));
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			String timeZone = "GMT+8:00";
			timeZone = getTimeZoneByBuyerSettings(user.getTenantId(), timeZone);
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("date", df.format(new Date()));
			map.put("appUrl", url);
			map.put("loginUrl", APP_URL + "/login");
			if(user.getEmailNotifications()) {
				sendEmail(mailTo, subject, map, Global.EVENT_CREATED_TEMPLATE);
			}
		} catch (Exception e) {
			LOG.error("Error While sending mail For Event CREATION :" + e.getMessage(), e);
		}
		try {
			String notificationMessage = messageSource.getMessage("event.created.notification.message", new Object[] { event.getReferanceNumber() }, Global.LOCALE);
			sendDashboardNotification(user, url, subject, notificationMessage);
		} catch (Exception e) {
			LOG.error("Error While sending notification For Event CREATION :" + e.getMessage(), e);
		}
	}

	private String findBusinessUnit(String eventId, RfxTypes rfxTypes) {
		String displayName = null;
		switch (rfxTypes) {
		case RFA:
			displayName = rfaEventDao.findBusinessUnitName(eventId);
			break;
		case RFI:
			displayName = rfiEventDao.findBusinessUnitName(eventId);
			break;
		case RFP:
			displayName = rfpEventDao.findBusinessUnitName(eventId);
			break;
		case RFQ:
			displayName = rfqEventDao.findBusinessUnitName(eventId);
			break;
		case RFT:
			displayName = rftEventDao.findBusinessUnitName(eventId);
			break;
		default:
			break;
		}
		return displayName;
	}

	public void sendEmail(String mailTo, String subject, Map<String, Object> map, String template) {

		notificationService.sendEmail(mailTo, subject, map, template);
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

	@Deprecated
	protected void sendEventSupendedNotificationEmailsNotUsed(Event event, RfxTypes type) {
		String timeZone = "GMT+8:00";
		String suppUrl = APP_URL + "/supplier/viewSupplierEvent/" + type.name() + "/" + event.getId();
		String buyerUrl = APP_URL + "/buyer/" + type.name() + "/eventSummary/" + event.getId();
		switch (type) {
		case RFA: {
			User eventOwner = rfaEventDao.getPlainEventOwnerByEventId(event.getId());
			timeZone = getTimeZoneByBuyerSettings(eventOwner.getTenantId(), timeZone);
			sendEventSuspendedNotifications(event, type, eventOwner, timeZone, buyerUrl);
			// send event notifications for buyer team members
			List<User> rfaBuyerMembers = rfaEventDao.getUserBuyerTeamMemberByEventId(event.getId());
			if (CollectionUtil.isNotEmpty(rfaBuyerMembers)) {
				for (User buyerTeamUser : rfaBuyerMembers) {
					sendEventSuspendedNotifications(event, type, buyerTeamUser, timeZone, buyerUrl);
				}
			}

			// sending supplier notifications
			List<String> suppIdList = rfaEventDao.getEventSuppliersId(event.getId());
			for (String suppId : suppIdList) {
				timeZone = getTimeZoneBySupplierSettings(suppId, timeZone);

				// Sending Mails and Notifications for admin suppliers
				List<User> supUsers = userService.getAllAdminPlainUsersForSupplierNotification(suppId);
				for (User user : supUsers) {
					sendEventSuspendedSupplierNotifications(event, type, user, timeZone, suppUrl);
				}
			}

			// Sending Mails and Notifications for supplier team members
			List<User> rfaSupplierMembers = rfaSupplierTeamMemberDao.getUserSupplierTeamMemberByEventId(event.getId());
			for (User supplierTeamUser : rfaSupplierMembers) {
				sendEventSuspendedNotifications(event, type, supplierTeamUser, timeZone, suppUrl);
			}
			break;
		}
		case RFI: {
			User eventOwner = rfiEventDao.getPlainEventOwnerByEventId(event.getId());
			timeZone = getTimeZoneByBuyerSettings(eventOwner.getTenantId(), timeZone);
			sendEventSuspendedNotifications(event, type, eventOwner, timeZone, buyerUrl);
			// send event notifications for buyer team members
			List<User> rfiBuyerMembers = rfiEventDao.getUserBuyerTeamMemberByEventId(event.getId());
			if (CollectionUtil.isNotEmpty(rfiBuyerMembers)) {
				for (User buyerTeamUser : rfiBuyerMembers) {
					sendEventSuspendedNotifications(event, type, buyerTeamUser, timeZone, buyerUrl);
				}
			}

			// sending supplier notifications
			List<String> suppIdList = rfiEventDao.getEventSuppliersId(event.getId());
			for (String suppId : suppIdList) {
				timeZone = getTimeZoneBySupplierSettings(suppId, timeZone);
				// Sending Mails and Notifications for supplier admins
				List<User> supUsers = userService.getAllAdminPlainUsersForSupplierNotification(suppId);
				for (User user : supUsers) {
					sendEventSuspendedSupplierNotifications(event, type, user, timeZone, suppUrl);
				}
			}

			// Sending Mails and Notifications for supplier team members
			List<User> supplierMembers = rfiSupplierTeamMemberDao.getUserSupplierTeamMemberByEventId(event.getId());
			for (User supplierTeamUser : supplierMembers) {
				sendEventSuspendedNotifications(event, type, supplierTeamUser, timeZone, suppUrl);
			}
			break;
		}
		case RFP: {
			User eventOwner = rfpEventDao.getPlainEventOwnerByEventId(event.getId());
			timeZone = getTimeZoneByBuyerSettings(eventOwner.getTenantId(), timeZone);
			sendEventSuspendedNotifications(event, type, eventOwner, timeZone, buyerUrl);
			// send event notifications for buyer team members
			List<User> rfpBuyerMembers = rfpEventDao.getUserBuyerTeamMemberByEventId(event.getId());
			if (CollectionUtil.isNotEmpty(rfpBuyerMembers)) {
				for (User buyerTeamUser : rfpBuyerMembers) {
					sendEventSuspendedNotifications(event, type, buyerTeamUser, timeZone, buyerUrl);
				}
			}

			// sending supplier notifications
			List<String> suppIdList = rfpEventDao.getEventSuppliersId(event.getId());
			for (String suppId : suppIdList) {
				timeZone = getTimeZoneBySupplierSettings(suppId, timeZone);
				// Sending Mails and Notifications for supplier admins
				List<User> supUsers = userService.getAllAdminPlainUsersForSupplierNotification(suppId);
				for (User user : supUsers) {
					sendEventSuspendedSupplierNotifications(event, type, user, timeZone, suppUrl);
				}
			}
			// Sending Mails and Notifications for supplier team members
			List<User> supplierMembers = rfpSupplierTeamMemberDao.getUserSupplierTeamMemberByEventId(event.getId());
			for (User supplierTeamUser : supplierMembers) {
				sendEventSuspendedNotifications(event, type, supplierTeamUser, timeZone, suppUrl);
			}
			break;
		}
		case RFQ: {
			User eventOwner = rfqEventDao.getPlainEventOwnerByEventId(event.getId());
			timeZone = getTimeZoneByBuyerSettings(eventOwner.getTenantId(), timeZone);
			sendEventSuspendedNotifications(event, type, eventOwner, timeZone, buyerUrl);
			// send event notifications for buyer team members
			List<User> rfqBuyerMembers = rfqEventDao.getUserBuyerTeamMemberByEventId(event.getId());
			if (CollectionUtil.isNotEmpty(rfqBuyerMembers)) {
				for (User buyerTeamUser : rfqBuyerMembers) {
					sendEventSuspendedNotifications(event, type, buyerTeamUser, timeZone, buyerUrl);
				}
			}

			// sending supplier notifications
			List<String> suppIdList = rfqEventDao.getEventSuppliersId(event.getId());
			for (String suppId : suppIdList) {
				timeZone = getTimeZoneBySupplierSettings(suppId, timeZone);

				// Sending Mails and Notifications for supplier admins
				List<User> supUsers = userService.getAllAdminPlainUsersForSupplierNotification(suppId);
				for (User user : supUsers) {
					sendEventSuspendedSupplierNotifications(event, type, user, timeZone, suppUrl);
				}
			}
			// Sending Mails and Notifications for supplier team members
			List<User> supplierMembers = rfqSupplierTeamMemberDao.getUserSupplierTeamMemberByEventId(event.getId());
			for (User supplierTeamUser : supplierMembers) {
				sendEventSuspendedNotifications(event, type, supplierTeamUser, timeZone, suppUrl);
			}
			break;
		}
		case RFT: {
			User eventOwner = rftEventDao.getPlainEventOwnerByEventId(event.getId());
			timeZone = getTimeZoneByBuyerSettings(eventOwner.getTenantId(), timeZone);
			sendEventSuspendedNotifications(event, type, eventOwner, timeZone, buyerUrl);
			// send event notifications for buyer team members
			List<User> rftBuyerMembers = rftEventDao.getUserBuyerTeamMemberByEventId(event.getId());
			if (CollectionUtil.isNotEmpty(rftBuyerMembers)) {
				for (User buyerTeamUser : rftBuyerMembers) {
					sendEventSuspendedNotifications(event, type, buyerTeamUser, timeZone, buyerUrl);
				}
			}

			// sending supplier notifications
			List<String> suppIdList = rftEventDao.getEventSuppliersId(event.getId());
			for (String suppId : suppIdList) {
				timeZone = getTimeZoneBySupplierSettings(suppId, timeZone);

				// Sending Mails and Notifications for supplier admins
				List<User> supUsers = userService.getAllAdminPlainUsersForSupplierNotification(suppId);
				for (User user : supUsers) {
					sendEventSuspendedSupplierNotifications(event, type, user, timeZone, suppUrl);
				}
			}

			// Sending Mails and Notifications for supplier team members
			List<User> supplierMembers = rftSupplierTeamMemberDao.getUserSupplierTeamMemberByEventId(event.getId());
			for (User supplierTeamUser : supplierMembers) {
				sendEventSuspendedNotifications(event, type, supplierTeamUser, timeZone, suppUrl);
			}
			break;
		}
		default:
			break;
		}

	}

	private void sendEventSuspendedNotifications(Event event, RfxTypes type, User user, String timeZone, String url) {

		String mailTo = "";
		String subject = "Event Suspended";
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {
			mailTo = user.getCommunicationEmail();
			map.put("userName", user.getName());
			map.put("event", event);
			map.put("eventType", type.name());
			map.put("businessUnit", StringUtils.checkString(findBusinessUnit(event.getId(), type)));
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("suspendRemarks", event.getSuspendRemarks());
			map.put("date", df.format(new Date()));
			map.put("appUrl", url);
			map.put("loginUrl", APP_URL + "/login");
			String notificationMessage = messageSource.getMessage("event.suspended.notification.message", new Object[] { event.getReferanceNumber() }, Global.LOCALE);
			sendDashboardNotification(user, url, subject, notificationMessage);
		} catch (Exception e) {
			LOG.error("Error While sending dashBoard notification on Event suspend : " + e.getMessage(), e);
		}
		try {
			if(user.getEmailNotifications()) {
				sendEmail(mailTo, subject, map, Global.EVENT_SUSPENDED_TEMPLATE);
			}
		} catch (Exception e) {
			LOG.error("Error While sending email on Event suspend : " + e.getMessage(), e);
		}
	}

	private void sendEventSuspendedSupplierNotifications(Event event, RfxTypes type, User user, String timeZone, String url) {

		String mailTo = "";
		String subject = "Event Suspended";
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {
			mailTo = user.getCommunicationEmail();
			map.put("userName", user.getName());
			map.put("event", event);
			map.put("eventType", type.name());
			map.put("businessUnit", StringUtils.checkString(findBusinessUnit(event.getId(), type)));
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("suspendRemarks", event.getSuspendRemarks());
			map.put("date", df.format(new Date()));
			map.put("appUrl", url);
			map.put("loginUrl", APP_URL + "/login");
			String notificationMessage = messageSource.getMessage("event.suspended.notification.message", new Object[] { event.getReferanceNumber() }, Global.LOCALE);
			sendDashboardNotification(user, url, subject, notificationMessage);
		} catch (Exception e) {
			LOG.error("Error While sending dashBoard notification on Event suspend : " + e.getMessage(), e);
		}
		try {
			if(user.getEmailNotifications()) {
				sendEmail(mailTo, subject, map, Global.EVENT_SUSPENDED_TEMPLATE);
			}
		} catch (Exception e) {
			LOG.error("Error While sending email on Event suspend : " + e.getMessage(), e);
		}
		try {
			String notificationMessage = messageSource.getMessage("event.suspended.notification.message", new Object[] { event.getReferanceNumber() }, Global.LOCALE);
			sendPushNotificationToSupplier(user, notificationMessage, event, type);
		} catch (Exception e) {
			LOG.error("Error While sending dashboard notification For Event INVITATION :" + e.getMessage(), e);
		}

	}

	@Deprecated
	protected void sendEventResumeNotificationEmailsOld(Event event, RfxTypes type) {
		String timeZone = "GMT+8:00";
		String suppUrl = APP_URL + "/supplier/viewSupplierEvent/" + type.name() + "/" + event.getId();
		String buyerUrl = APP_URL + "/buyer/" + type.name() + "/eventSummary/" + event.getId();
		switch (type) {
		case RFA: {
			User eventOwner = rfaEventDao.getPlainEventOwnerByEventId(event.getId());
			timeZone = getTimeZoneByBuyerSettings(eventOwner.getTenantId(), timeZone);
			sendEventResumeNotifications(event, type, eventOwner, timeZone, buyerUrl);
			// send event notifications for buyer team members
			List<User> rfaBuyerMembers = rfaEventDao.getUserBuyerTeamMemberByEventId(event.getId());
			if (CollectionUtil.isNotEmpty(rfaBuyerMembers)) {
				for (User buyerTeamUser : rfaBuyerMembers) {
					sendEventResumeNotifications(event, type, buyerTeamUser, timeZone, buyerUrl);
				}
			}

			// sending supplier notifications
			List<String> suppIdList = rfaEventDao.getEventSuppliersId(event.getId());
			for (String suppId : suppIdList) {
				timeZone = getTimeZoneBySupplierSettings(suppId, timeZone);

				// Sending Mails and Notifications for admin suppliers
				List<User> supUsers = userService.getAllAdminPlainUsersForSupplier(suppId);
				for (User user : supUsers) {
					sendEventResumeNotifications(event, type, user, timeZone, suppUrl);
				}
			}
			// Sending Mails and Notifications for supplier team members
			List<User> rfaSupplierMembers = rfaSupplierTeamMemberDao.getUserSupplierTeamMemberByEventId(event.getId());
			for (User supplierTeamUser : rfaSupplierMembers) {
				sendEventResumeNotifications(event, type, supplierTeamUser, timeZone, suppUrl);
			}
			break;
		}
		case RFI: {
			User eventOwner = rfiEventDao.getPlainEventOwnerByEventId(event.getId());
			timeZone = getTimeZoneByBuyerSettings(eventOwner.getTenantId(), timeZone);
			sendEventResumeNotifications(event, type, eventOwner, timeZone, buyerUrl);
			// send event notifications for buyer team members
			List<User> rfiBuyerMembers = rfiEventDao.getUserBuyerTeamMemberByEventId(event.getId());
			if (CollectionUtil.isNotEmpty(rfiBuyerMembers)) {
				for (User buyerTeamUser : rfiBuyerMembers) {
					sendEventResumeNotifications(event, type, buyerTeamUser, timeZone, buyerUrl);
				}
			}

			// sending supplier notifications
			List<String> suppIdList = rfiEventDao.getEventSuppliersId(event.getId());
			for (String suppId : suppIdList) {
				timeZone = getTimeZoneBySupplierSettings(suppId, timeZone);
				// Sending Mails and Notifications for supplier admins
				List<User> supUsers = userService.getAllAdminPlainUsersForSupplier(suppId);
				if (CollectionUtil.isNotEmpty(supUsers)) {
					for (User user : supUsers) {
						sendEventResumeNotifications(event, type, user, timeZone, suppUrl);
					}
				}
			}
			// Sending Mails and Notifications for supplier team members
			List<User> supplierMembers = rfiSupplierTeamMemberDao.getUserSupplierTeamMemberByEventId(event.getId());
			for (User supplierTeamUser : supplierMembers) {
				sendEventResumeNotifications(event, type, supplierTeamUser, timeZone, suppUrl);
			}
			break;
		}
		case RFP: {
			User eventOwner = rfpEventDao.getPlainEventOwnerByEventId(event.getId());
			timeZone = getTimeZoneByBuyerSettings(eventOwner.getTenantId(), timeZone);
			sendEventResumeNotifications(event, type, eventOwner, timeZone, buyerUrl);
			// send event notifications for buyer team members
			List<User> rfpBuyerMembers = rfpEventDao.getUserBuyerTeamMemberByEventId(event.getId());
			if (CollectionUtil.isNotEmpty(rfpBuyerMembers)) {
				for (User buyerTeamUser : rfpBuyerMembers) {
					sendEventResumeNotifications(event, type, buyerTeamUser, timeZone, buyerUrl);
				}
			}

			// sending supplier notifications
			List<String> suppIdList = rfpEventDao.getEventSuppliersId(event.getId());
			for (String suppId : suppIdList) {
				timeZone = getTimeZoneBySupplierSettings(suppId, timeZone);
				// Sending Mails and Notifications for supplier admins
				List<User> supUsers = userService.getAllAdminPlainUsersForSupplier(suppId);
				for (User user : supUsers) {
					sendEventResumeNotifications(event, type, user, timeZone, suppUrl);
				}
			}
			// Sending Mails and Notifications for supplier team members
			List<User> supplierMembers = rfpSupplierTeamMemberDao.getUserSupplierTeamMemberByEventId(event.getId());
			for (User supplierTeamUser : supplierMembers) {
				sendEventResumeNotifications(event, type, supplierTeamUser, timeZone, suppUrl);
			}
			break;
		}
		case RFQ: {
			User eventOwner = rfqEventDao.getPlainEventOwnerByEventId(event.getId());
			timeZone = getTimeZoneByBuyerSettings(eventOwner.getTenantId(), timeZone);
			sendEventResumeNotifications(event, type, eventOwner, timeZone, buyerUrl);
			// send event notifications for buyer team members
			List<User> rfqBuyerMembers = rfqEventDao.getUserBuyerTeamMemberByEventId(event.getId());
			if (CollectionUtil.isNotEmpty(rfqBuyerMembers)) {
				for (User buyerTeamUser : rfqBuyerMembers) {
					sendEventResumeNotifications(event, type, buyerTeamUser, timeZone, buyerUrl);
				}
			}

			// sending supplier notifications
			List<String> suppIdList = rfqEventDao.getEventSuppliersId(event.getId());
			for (String suppId : suppIdList) {
				timeZone = getTimeZoneBySupplierSettings(suppId, timeZone);

				// Sending Mails and Notifications for supplier admins
				List<User> supUsers = userService.getAllAdminPlainUsersForSupplier(suppId);
				for (User user : supUsers) {
					sendEventResumeNotifications(event, type, user, timeZone, suppUrl);
				}
			}
			// Sending Mails and Notifications for supplier team members
			List<User> supplierMembers = rfqSupplierTeamMemberDao.getUserSupplierTeamMemberByEventId(event.getId());
			for (User supplierTeamUser : supplierMembers) {
				sendEventResumeNotifications(event, type, supplierTeamUser, timeZone, suppUrl);
			}
			break;
		}
		case RFT: {
			User eventOwner = rftEventDao.getPlainEventOwnerByEventId(event.getId());
			timeZone = getTimeZoneByBuyerSettings(eventOwner.getTenantId(), timeZone);
			sendEventResumeNotifications(event, type, eventOwner, timeZone, buyerUrl);
			// send event notifications for buyer team members
			List<User> rftBuyerMembers = rftEventDao.getUserBuyerTeamMemberByEventId(event.getId());
			if (CollectionUtil.isNotEmpty(rftBuyerMembers)) {
				for (User buyerTeamUser : rftBuyerMembers) {
					sendEventResumeNotifications(event, type, buyerTeamUser, timeZone, buyerUrl);
				}
			}

			// sending supplier notifications
			List<String> suppIdList = rftEventDao.getEventSuppliersId(event.getId());
			for (String suppId : suppIdList) {
				timeZone = getTimeZoneBySupplierSettings(suppId, timeZone);

				// Sending Mails and Notifications for supplier admins
				List<User> supUsers = userService.getAllAdminPlainUsersForSupplier(suppId);
				for (User user : supUsers) {
					sendEventResumeNotifications(event, type, user, timeZone, suppUrl);
				}

			}
			// Sending Mails and Notifications for supplier team members
			List<User> supplierMembers = rftSupplierTeamMemberDao.getUserSupplierTeamMemberByEventId(event.getId());
			for (User supplierTeamUser : supplierMembers) {
				sendEventResumeNotifications(event, type, supplierTeamUser, timeZone, suppUrl);
			}
			break;
		}
		default:
			break;
		}
	}

	private void sendEventResumeNotifications(Event event, RfxTypes type, User user, String timeZone, String url) {
		String mailTo = "";
		String subject = "Event Resumed";
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {
			mailTo = user.getCommunicationEmail();
			map.put("userName", user.getName());
			map.put("event", event);
			map.put("eventType", type.name());
			map.put("businessUnit", StringUtils.checkString(findBusinessUnit(event.getId(), type)));
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("date", df.format(new Date()));
			map.put("appUrl", url);
			map.put("loginUrl", APP_URL + "/login");
			String notificationMessage = messageSource.getMessage("event.resumed.notification.message", new Object[] { event.getReferanceNumber() }, Global.LOCALE);
			sendDashboardNotification(user, url, subject, notificationMessage);
		} catch (Exception e) {
			LOG.error("Error While sending dashBoard notification on event resume :" + e.getMessage(), e);
		}
		try {
			if(user.getEmailNotifications()) {
				sendEmail(mailTo, subject, map, Global.EVENT_RESUMED_TEMPLATE);
			}
		} catch (Exception e) {
			LOG.error("Error While sending email on event resume :" + e.getMessage(), e);
		}
	}

	@RequestMapping(value = "/downloadMessageAttachment/{messageId}", method = RequestMethod.GET)
	public void downloadMessageAttachment(@PathVariable String messageId, HttpServletResponse response) {
		try {
			LOG.info("Message Download  :: :: " + messageId + "::::::");
			if (StringUtils.checkString(messageId).length() > 0) {
				switch (eventType) {
				case RFA:
					RfaEventMessage rfaMessage = eventMessageService.getRfaEventMessageById(messageId);
					response.setContentType(rfaMessage.getContentType());
					response.setContentLength(rfaMessage.getFileAttatchment().length);
					response.setHeader("Content-Disposition", "attachment; filename=\"" + rfaMessage.getFileName() + "\"");
					FileCopyUtils.copy(rfaMessage.getFileAttatchment(), response.getOutputStream());
					response.flushBuffer();
					response.setStatus(HttpServletResponse.SC_OK);
					break;
				case RFI:
					RfiEventMessage rfiMessage = eventMessageService.getRfiEventMessageById(messageId);
					response.setContentType(rfiMessage.getContentType());
					response.setContentLength(rfiMessage.getFileAttatchment().length);
					response.setHeader("Content-Disposition", "attachment; filename=\"" + rfiMessage.getFileName() + "\"");
					FileCopyUtils.copy(rfiMessage.getFileAttatchment(), response.getOutputStream());
					response.flushBuffer();
					response.setStatus(HttpServletResponse.SC_OK);
					break;
				case RFP:
					RfpEventMessage rfpMessage = eventMessageService.getRfpEventMessageById(messageId);
					response.setContentType(rfpMessage.getContentType());
					response.setContentLength(rfpMessage.getFileAttatchment().length);
					response.setHeader("Content-Disposition", "attachment; filename=\"" + rfpMessage.getFileName() + "\"");
					FileCopyUtils.copy(rfpMessage.getFileAttatchment(), response.getOutputStream());
					response.flushBuffer();
					response.setStatus(HttpServletResponse.SC_OK);
					break;
				case RFQ:
					RfqEventMessage rfqMessage = eventMessageService.getRfqEventMessageById(messageId);
					response.setContentType(rfqMessage.getContentType());
					response.setContentLength(rfqMessage.getFileAttatchment().length);
					response.setHeader("Content-Disposition", "attachment; filename=\"" + rfqMessage.getFileName() + "\"");
					FileCopyUtils.copy(rfqMessage.getFileAttatchment(), response.getOutputStream());
					response.flushBuffer();
					response.setStatus(HttpServletResponse.SC_OK);
					break;
				case RFT:
					RftEventMessage rftMessage = eventMessageService.getRftEventMessageById(messageId);
					response.setContentType(rftMessage.getContentType());
					response.setContentLength(rftMessage.getFileAttatchment().length);
					response.setHeader("Content-Disposition", "attachment; filename=\"" + rftMessage.getFileName() + "\"");
					FileCopyUtils.copy(rftMessage.getFileAttatchment(), response.getOutputStream());
					response.flushBuffer();
					response.setStatus(HttpServletResponse.SC_OK);
					break;
				case PO:
					PoEventMessage poMessage = eventMessageService.getPoEventMessageById(messageId);
					response.setContentType(poMessage.getContentType());
					response.setContentLength(poMessage.getFileAttatchment().length);
					response.setHeader("Content-Disposition", "attachment; filename=\"" + poMessage.getFileName() + "\"");
					FileCopyUtils.copy(poMessage.getFileAttatchment(), response.getOutputStream());
					response.flushBuffer();
					response.setStatus(HttpServletResponse.SC_OK);
					break;
				}
			}
		} catch (Exception e) {
			LOG.error("Error while downloading Message Document : " + e.getMessage(), e);
		}
	}

	@RequestMapping(value = "/viewMailBox/{eventId}", method = RequestMethod.GET)
	public String viewMailBox(@PathVariable String eventId, Model model) {
		LOG.info("VIEW MAIL BOX CALLED....");
		EventPermissions eventPermissions = null;
		try {
			switch (eventType) {
				case RFA:
					eventPermissions = rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
					model.addAttribute("eventPermissions", eventPermissions);
					model.addAttribute("event", rfaEventService.getRfaEventById(eventId));
					model.addAttribute("supplierList", rfaEventService.getEventSuppliers(eventId));
					break;
				case RFI:
					eventPermissions = rfiEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
					model.addAttribute("eventPermissions", eventPermissions);
					model.addAttribute("event", rfiEventService.getRfiEventById(eventId));
					model.addAttribute("supplierList", rfiEventService.getEventSuppliers(eventId));
					break;
				case RFP:
					eventPermissions = rfpEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
					model.addAttribute("eventPermissions", eventPermissions);
					model.addAttribute("event", rfpEventService.getEventById(eventId));
					model.addAttribute("supplierList", rfpEventService.getEventSuppliers(eventId));
					break;
				case RFQ:
					eventPermissions = rfqEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
					model.addAttribute("eventPermissions", eventPermissions);
					model.addAttribute("event", rfqEventService.getEventById(eventId));
					model.addAttribute("supplierList", rfqEventService.getEventSuppliers(eventId));
					break;
				case RFT:
					eventPermissions = rftEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId);
					model.addAttribute("eventPermissions", eventPermissions);
					model.addAttribute("event", rftEventService.getRftEventById(eventId));
					model.addAttribute("supplierList", rftEventService.getEventSuppliers(eventId));
					break;
			}
			model.addAttribute("messagePermission", checkMessagePermissionForTheEvent(eventPermissions));
		} catch (Exception e) {
			LOG.error("Error while fetching Messages : " + e.getMessage(), e);
		}
		model.addAttribute("showMessageTab", true);
		return "viewMailBox";
	}

	public Boolean checkMessagePermissionForTheEvent(EventPermissions eventPermissions) {
		if (eventPermissions != null && (eventPermissions.isOwner() || eventPermissions.isEditor())) {
			return Boolean.TRUE;
		} else {
			User user = userService.findUserWithRoleById(SecurityLibrary.getLoggedInUser().getId());
			if(user.getUserRole().getRoleName().equalsIgnoreCase(Constant.ROLE_BUYER_ADMIN)) {
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
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

	/**
	 * @param suppId
	 * @param timeZone
	 * @return
	 */
	public String getTimeZoneBySupplierSettings(String suppId, String timeZone) {
		try {
			if (StringUtils.checkString(suppId).length() > 0) {
				String time = supplierSettingsService.getSupplierTimeZoneByTenantId(suppId);
				if (time != null) {
					timeZone = time;
					LOG.info("time Zone :" + timeZone);
				}
			}
		} catch (Exception e) {
			LOG.error("Error while fetching supplier time zone :" + e.getMessage(), e);
		}
		return timeZone;

	}

	@RequestMapping(value = "/downloadAuditFile/{id}/{action}/{actionDate}", method = RequestMethod.GET)
	public void downloadFile(@PathVariable String id, @PathVariable String action, @PathVariable String actionDate, HttpServletResponse response) throws IOException {
		try {
			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + action + "_Audit_" + actionDate + ".pdf\"");

			switch (eventType) {
			case RFA:
				RfaEventAudit rfaAuditFile = eventAuditService.getRfaEventAuditById(id);
				if (rfaAuditFile.getSummarySnapshot() != null && rfaAuditFile.getSummarySnapshot().length > 0) {
					response.setContentLength(rfaAuditFile.getSummarySnapshot().length);
					FileCopyUtils.copy(rfaAuditFile.getSummarySnapshot(), response.getOutputStream());
				}
				break;
			case RFI:
				RfiEventAudit rfiAuditFile = eventAuditService.getRfiEventAuditById(id);
				if (rfiAuditFile.getSummarySnapshot() != null && rfiAuditFile.getSummarySnapshot().length > 0) {
					response.setContentLength(rfiAuditFile.getSummarySnapshot().length);
					FileCopyUtils.copy(rfiAuditFile.getSummarySnapshot(), response.getOutputStream());
				}
				break;
			case RFP:
				RfpEventAudit rfpAuditFile = eventAuditService.getRfpEventAuditById(id);
				if (rfpAuditFile.getSummarySnapshot() != null && rfpAuditFile.getSummarySnapshot().length > 0) {
					response.setContentLength(rfpAuditFile.getSummarySnapshot().length);
					FileCopyUtils.copy(rfpAuditFile.getSummarySnapshot(), response.getOutputStream());
				}
				break;
			case RFQ:
				RfqEventAudit rfqAuditFile = eventAuditService.getRfqEventAuditById(id);
				if (rfqAuditFile.getSummarySnapshot() != null && rfqAuditFile.getSummarySnapshot().length > 0) {
					response.setContentLength(rfqAuditFile.getSummarySnapshot().length);
					FileCopyUtils.copy(rfqAuditFile.getSummarySnapshot(), response.getOutputStream());
				}
				break;
			case RFT:
				RftEventAudit rftAuditFile = eventAuditService.getRftEventAuditById(id);
				if (rftAuditFile.getSummarySnapshot() != null && rftAuditFile.getSummarySnapshot().length > 0) {
					response.setContentLength(rftAuditFile.getSummarySnapshot().length);
					FileCopyUtils.copy(rftAuditFile.getSummarySnapshot(), response.getOutputStream());
				}
				break;
			default:
				break;
			}

			response.flushBuffer();
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (Exception e) {
			LOG.error("Error while File downloaded RFP Audit File : " + e.getMessage(), e);
		}
	}

	public void checkSubscriptionPackage(String loggedInUserTenantId) throws SubscriptionException, Exception {
		// Check subscription limit
		Buyer buyer = buyerService.findBuyerByIdWithBuyerPackage(SecurityLibrary.getLoggedInUser().getBuyer().getId());

		if (buyer != null && buyer.getBuyerPackage() != null && buyer.getBuyerPackage().getPlan() != null) {
			BuyerPackage bp = buyer.getBuyerPackage();
			if (bp.getPlan().getPlanType() == PlanType.PER_USER) {
				if (bp.getSubscriptionStatus() == SubscriptionStatus.EXPIRED || new Date().after(bp.getEndDate())) {
					throw new SubscriptionException(messageSource.getMessage("common.subscription.error.expire", new Object[] {}, Global.LOCALE));
				}
			} else {
				if (bp.getEventLimit() != null) {
					if (bp.getNoOfEvents() == null) {
						bp.setNoOfEvents(0);
					}
					if (bp.getNoOfEvents() >= bp.getEventLimit()) {
						throw new SubscriptionException("Event limit of " + bp.getEventLimit() + " reached.");
					} else if (bp.getSubscriptionStatus() == SubscriptionStatus.EXPIRED || new Date().after(bp.getEndDate())) {
						throw new SubscriptionException(messageSource.getMessage("common.subscription.error.expire", new Object[] {}, Global.LOCALE));
					}
				}
			}
			buyer.getBuyerPackage().setNoOfEvents(buyer.getBuyerPackage().getNoOfEvents() + 1);
			buyerService.updateBuyer(buyer);

			// sending notification once all Event Credits are used
			try {
				if (bp.getEventLimit() != null && bp.getEventLimit() == buyer.getBuyerPackage().getNoOfEvents()) {
					String timeZone = "GMT+8:00";
					// For buyer Admin
					User buyerUser = userService.getPlainUserByLoginId(buyer.getLoginEmail());
					timeZone = getTimeZoneByBuyerSettings(buyer.getId(), timeZone);
					String msg = "You have used all your event credits \"" + bp.getEventLimit() + "\".";
					sendBuyerEventCreditLimitUsedMail(buyerUser, msg, timeZone, bp);
				}
			} catch (Exception e) {
				LOG.error("Error While sending notification to all Event Credits are used :" + e.getMessage(), e);
			}

		}
	}

	public void checkSubscriptionPackageForUserBased() throws SubscriptionException {
		// Check subscription limit
		Buyer buyer = buyerService.findBuyerByIdWithBuyerPackage(SecurityLibrary.getLoggedInUser().getBuyer().getId());

		if (buyer != null && buyer.getBuyerPackage() != null) {
			BuyerPackage bp = buyer.getBuyerPackage();
			if (bp.getPlan() != null && bp.getPlan().getPlanType() == PlanType.PER_USER) {
				if (bp.getSubscriptionStatus() == SubscriptionStatus.EXPIRED || new Date().after(bp.getEndDate())) {
					throw new SubscriptionException("Your Subscription has Expired.");
				}
			}
		}
	}

	public boolean checkSubscriptionPackageUserBased() {
		// Check subscription limit
		Buyer buyer = buyerService.findBuyerByIdWithBuyerPackage(SecurityLibrary.getLoggedInUser().getBuyer().getId());
		boolean expired = false;
		if (buyer != null && buyer.getBuyerPackage() != null) {
			BuyerPackage bp = buyer.getBuyerPackage();
			if (bp.getPlan() != null && bp.getPlan().getPlanType() == PlanType.PER_USER) {
				if (bp.getSubscriptionStatus() == SubscriptionStatus.EXPIRED || new Date().after(bp.getEndDate())) {
					expired = true;
				}
			}
		}
		return expired;
	}

	private void sendBuyerEventCreditLimitUsedMail(User user, String msg, String timeZone, BuyerPackage buyerPackage) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		String mailTo = user.getCommunicationEmail();
		String subject = "Event Credits Reminder";
		String appUrl = APP_URL + "/buyer/billing/accountOverview";
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
		df.setTimeZone(TimeZone.getTimeZone(timeZone));
		map.put("date", df.format(new Date()));
		map.put("userName", user.getName());
		map.put("loginUrl", APP_URL + "/login");
		map.put("appUrl", appUrl);
		map.put("msg", msg);
		if(user.getEmailNotifications()) {
			notificationService.sendEmail(mailTo, subject, map, Global.EVENT_CREDIT_REMINDER);
		}
		try {
			sendDashboardNotification(user, appUrl, subject, msg);
		} catch (Exception e) {
			LOG.error("Error While Sending Dashboard on all event credit used notification :" + e.getMessage(), e);
		}
	}

	protected boolean doValidate(EventSupplier event) {
		boolean isvalid = false;
		switch (getEventType()) {
		case RFA:
			isvalid = rfaEventSupplierService.isExists((RfaEventSupplier) event);
			break;
		case RFI:
			isvalid = rfiEventSupplierService.isExists((RfiEventSupplier) event);
			break;
		case RFP:
			isvalid = rfpEventSupplierService.isExists((RfpEventSupplier) event);
			break;
		case RFQ:
			isvalid = rfqEventSupplierService.isExists((RfqEventSupplier) event);
			break;
		case RFT:
			isvalid = rftEventSupplierService.isExists((RftEventSupplier) event);
			break;
		default:
			break;
		}

		return isvalid;
	}

	private void sendPushNotificationToSupplier(User user, String notificationMessage, Event event, RfxTypes eventType) {

		if (StringUtils.checkString(user.getDeviceId()).length() > 0) {
			try {
				LOG.info("User '" + user.getCommunicationEmail() + "' and device Id :" + user.getDeviceId());
				Map<String, String> payload = new HashMap<String, String>();
				payload.put("id", event.getId());
				payload.put("messageType", NotificationType.EVENT_MESSAGE.toString());
				payload.put("eventType", eventType.name());
				notificationService.pushOneSignalNotification(notificationMessage, null, payload, Arrays.asList(user.getDeviceId()));
			} catch (Exception e) {
				LOG.error("Error While sending Event Evaluation Mobile push notification to '" + user.getCommunicationEmail() + "' : " + e.getMessage(), e);
			}
		} else {
			LOG.info("User '" + user.getCommunicationEmail() + "' Device Id is Null");
		}
	}

	@RequestMapping(path = "/downloadShortEvaluationSummary/{eventId}/{envelopeId}", method = RequestMethod.GET)
	public void downloadShortEvaluationSummary(@PathVariable("eventId") String eventId, @PathVariable("envelopeId") String envelopeId, HttpServletResponse response, HttpSession session) throws Exception {
		JasperPrint jasperPrint = null;
		String filename = "ShortSubmissionSummary.pdf";
		TimeZone timeZone = TimeZone.getDefault();
		String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}
		// Virtualizar - To increase the performance
		JRSwapFileVirtualizer virtualizer = null;

		try {
			virtualizer = new JRSwapFileVirtualizer(300, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);
			jasperPrint = rftEventService.generateShortEvaluationSummaryReport(eventType.name(), eventId, envelopeId, timeZone, virtualizer);
		} catch (Exception e1) {
		} finally {
			if (virtualizer != null) {
				virtualizer.cleanup();
			}
		}

		if (jasperPrint != null) {
			streamReport(jasperPrint, filename, response);

			String envelopeTitle = null;

			RfxTypes eventTypes = RfxTypes.fromStringToRfxType(eventType.name());
			switch (eventTypes) {
			case RFA: {
				try {
					RfaEventAudit audit = new RfaEventAudit();
					envelopeTitle = rftEnvelopDao.getEnvelipeTitleById(envelopeId, eventType.name());
					RfaEvent rfaEvent = new RfaEvent();
					rfaEvent.setId(eventId);
					audit.setEvent(rfaEvent);
					audit.setAction(AuditActionType.Download);
					audit.setActionDate(new Date());
					audit.setActionBy(SecurityLibrary.getLoggedInUser());
					audit.setDescription("Short Summary Report is downloaded for Envelope '" + envelopeTitle + "'");
					eventAuditService.save(audit);

					RfaEvent event = rfaEventService.getPlainEventById(eventId);
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Short Summary Report is downloaded for Envelope '" + envelopeTitle + "' for Event '" + event.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFA);
					buyerAuditTrailDao.save(buyerAuditTrail);

				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}

				break;
			}
			case RFI: {
				try {
					RfiEventAudit audit = new RfiEventAudit();
					envelopeTitle = rftEnvelopDao.getEnvelipeTitleById(envelopeId, eventType.name());
					RfiEvent rfaEvent = new RfiEvent();
					rfaEvent.setId(eventId);
					audit.setEvent(rfaEvent);
					audit.setAction(AuditActionType.Download);
					audit.setActionDate(new Date());
					audit.setActionBy(SecurityLibrary.getLoggedInUser());
					audit.setDescription("Short Summary Report is downloaded for Envelope '" + envelopeTitle + "'");
					eventAuditService.save(audit);

					RfiEvent rfiEvent = rfiEventService.getPlainEventById(eventId);
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Short Summary Report is downloaded for Envelope '" + envelopeTitle + "'for Event '" + rfiEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFI);
					buyerAuditTrailDao.save(buyerAuditTrail);

				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}

				break;
			}
			case RFT: {
				try {
					RftEventAudit audit = new RftEventAudit();
					envelopeTitle = rftEnvelopDao.getEnvelipeTitleById(envelopeId, eventType.name());
					RftEvent rfaEvent = new RftEvent();
					rfaEvent.setId(eventId);
					audit.setEvent(rfaEvent);
					audit.setAction(AuditActionType.Download);
					audit.setActionDate(new Date());
					audit.setActionBy(SecurityLibrary.getLoggedInUser());
					audit.setDescription("Short Summary Report is downloaded for Envelope '" + envelopeTitle + "'");
					eventAuditService.save(audit);

					RftEvent rftEvent = rftEventService.getPlainEventById(eventId);
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Short Summary Report is downloaded for Envelope '" + envelopeTitle + "' for Event '" + rftEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFT);
					buyerAuditTrailDao.save(buyerAuditTrail);

				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}

				break;
			}
			case RFQ: {
				try {
					RfqEventAudit audit = new RfqEventAudit();
					envelopeTitle = rftEnvelopDao.getEnvelipeTitleById(envelopeId, eventType.name());
					RfqEvent rfaEvent = new RfqEvent();
					rfaEvent.setId(eventId);
					audit.setEvent(rfaEvent);
					audit.setAction(AuditActionType.Download);
					audit.setActionDate(new Date());
					audit.setActionBy(SecurityLibrary.getLoggedInUser());
					audit.setDescription("Short Summary Report is downloaded for Envelope '" + envelopeTitle + "'");
					eventAuditService.save(audit);

					RfqEvent rfqEvent = rfqEventService.getPlainEventById(eventId);
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Short Summary Report is downloaded for Envelope '" + envelopeTitle + "' for Event '" + rfqEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFQ);
					buyerAuditTrailDao.save(buyerAuditTrail);

				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}

				break;
			}
			case RFP: {
				try {
					RfpEventAudit audit = new RfpEventAudit();
					envelopeTitle = rftEnvelopDao.getEnvelipeTitleById(envelopeId, eventType.name());
					RfpEvent rfaEvent = new RfpEvent();
					rfaEvent.setId(eventId);
					audit.setEvent(rfaEvent);
					audit.setAction(AuditActionType.Download);
					audit.setActionDate(new Date());
					audit.setActionBy(SecurityLibrary.getLoggedInUser());
					audit.setDescription("Short Summary Report is downloaded for Envelope '" + envelopeTitle + "'");
					eventAuditService.save(audit);

					RfpEvent rfpEvent = rfpEventService.getPlainEventById(eventId);
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Short Summary Report is downloaded for Envelope '" + envelopeTitle + "' for Event '" + rfpEvent.getEventId() + "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFP);
					buyerAuditTrailDao.save(buyerAuditTrail);

				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}

				break;
			}
			default:
				break;
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

	@RequestMapping(value = "/invitedSupplierList/{eventId}", method = RequestMethod.GET)
	public ResponseEntity<TableData<FeePojo>> eventReportList(@PathVariable String eventId, TableDataInput input, HttpSession session) {
		TableData<FeePojo> data = null;
		try {
			String timeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);

			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			df.setTimeZone(TimeZone.getTimeZone(timeZone));

			List<FeePojo> list = rfqEventSupplierService.getAllInvitedSuppliersByEventId(eventId, input, getEventType());
			for (FeePojo feePojo : list) {
				if (feePojo.getFeePaidDate() != null) {
					feePojo.setFeeTime(df.format(feePojo.getFeePaidDate()));
				}
				if (feePojo.getDepositPaidDate() != null) {
					feePojo.setDepositTime(df.format(feePojo.getDepositPaidDate()));
				}
			}

			data = new TableData<FeePojo>(list);
			data.setDraw(input.getDraw());

			long filterTotalCount = rfqEventSupplierService.getAllInvitedSuppliersFilterCountByEventId(eventId, input, getEventType());
			long totalCount = rfqEventSupplierService.getAllInvitedSuppliersCountByEventId(eventId, getEventType());

			LOG.info(totalCount + "=================" + filterTotalCount);

			data.setRecordsTotal(totalCount);
			data.setRecordsFiltered(filterTotalCount);

		} catch (Exception e) {
			LOG.error("Error while loading draft list : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<FeePojo>>(data, HttpStatus.OK);
	}

	protected void sendEventSuspendedRevertBidNotifications(Event event, RfxTypes type, User user, String timeZone, String url) {

		String mailTo = "";
		String subject = "Event Suspended";
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {
			mailTo = user.getCommunicationEmail();
			map.put("userName", user.getName());
			map.put("event", event);
			map.put("eventType", type.name());
			map.put("businessUnit", StringUtils.checkString(findBusinessUnit(event.getId(), type)));
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("suspendRemarks", event.getSuspendRemarks());
			map.put("date", df.format(new Date()));
			map.put("appUrl", url);
			map.put("loginUrl", APP_URL + "/login");
			String notificationMessage = messageSource.getMessage("event.suspended.revert.bid.notification.message", new Object[] { event.getReferanceNumber() }, Global.LOCALE);
			sendDashboardNotification(user, url, subject, notificationMessage);
		} catch (Exception e) {
			LOG.error("Error While sending dashBoard notification on Event suspend : " + e.getMessage(), e);
		}
		try {
			if(user.getEmailNotifications()) {
				sendEmail(mailTo, subject, map, Global.EVENT_SUSPENDED_REVERT_BID_TEMPLATE);
			}
		} catch (Exception e) {
			LOG.error("Error While sending email on Event suspend : " + e.getMessage(), e);
		}
	}

	@RequestMapping(path = "/approveSuspension", method = RequestMethod.POST)
	public ModelAndView approveSuspendedEvent(@RequestParam(name = "id") String id, @RequestParam String remarks, RedirectAttributes redir, HttpSession session, Model model) {
		LOG.info(SecurityLibrary.getLoggedInUserLoginId() + " is Approving Suspended " + getEventType() + " Event : " + id + " with remarks : " + remarks);
		JRSwapFileVirtualizer virtualizer = null;
		virtualizer = new JRSwapFileVirtualizer(100, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);
		try {
			Event theEvent = null;
			switch (getEventType()) {
			case RFA: {
				RfaEvent event = new RfaEvent();
				event.setId(id);
				theEvent = suspensionApprovalService.doApproval(event, SecurityLibrary.getLoggedInUser(), remarks, true, session, virtualizer, model);
				event.setEventName(theEvent.getEventName());
				break;
			}
			case RFI: {
				RfiEvent event = new RfiEvent();
				event.setId(id);
				theEvent = suspensionApprovalService.doApproval(event, SecurityLibrary.getLoggedInUser(), remarks, true, session, virtualizer, model);
				event.setEventName(theEvent.getEventName());
				break;
			}
			case RFP: {
				RfpEvent event = new RfpEvent();
				event.setId(id);
				theEvent = suspensionApprovalService.doApproval(event, SecurityLibrary.getLoggedInUser(), remarks, true, session, virtualizer, model);
				event.setEventName(theEvent.getEventName());
				break;
			}
			case RFQ: {
				RfqEvent event = new RfqEvent();
				event.setId(id);
				theEvent = suspensionApprovalService.doApproval(event, SecurityLibrary.getLoggedInUser(), remarks, true, session, virtualizer, model);
				event.setEventName(theEvent.getEventName());
				break;
			}
			case RFT: {
				RftEvent event = new RftEvent();
				event.setId(id);
				theEvent = suspensionApprovalService.doApproval(event, SecurityLibrary.getLoggedInUser(), remarks, true, session, virtualizer, model);
				event.setEventName(theEvent.getEventName());
				break;
			}
			default:
				break;
			}
			redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.event.approved", new Object[] { (getEventType().name() != null ? getEventType().name() : ""), (theEvent.getEventName() != null ? theEvent.getEventName() : "") }, Global.LOCALE));
		} catch (Exception e) {
			LOG.info("Error while Approving Event :" + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.approving", new Object[] { e.getMessage() }, Global.LOCALE));
			// return "redirect:/buyer/" + getEventType().name() + "/eventSummary/" + id;
			return new ModelAndView("redirect:/buyer/" + getEventType().name() + "/eventSummary/" + id);
		} finally {
			if (virtualizer != null) {
				virtualizer.cleanup();
			}
		}
		return new ModelAndView("redirect:/buyer/buyerDashboard");
	}

	@RequestMapping(path = "/rejectSuspension", method = RequestMethod.POST)
	public String rejectSuspension(@RequestParam(name = "id") String id, @RequestParam String remarks, RedirectAttributes redir, HttpSession session, Model model) {
		LOG.info(SecurityLibrary.getLoggedInUserLoginId() + " is Rejecting " + getEventType() + " Event : " + id + " with remarks : " + remarks);
		JRSwapFileVirtualizer virtualizer = null;
		try {
			virtualizer = new JRSwapFileVirtualizer(300, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);
			if (StringUtils.checkString(remarks).length() == 0) {
				redir.addFlashAttribute("error", messageSource.getMessage("flasherror.remark.required.rejection", new Object[] {}, Global.LOCALE));
				return "redirect:/buyer/" + getEventType().name() + "/eventSummary/" + id;
			}
			Event theEvent = null;
			switch (getEventType()) {
			case RFA: {
				RfaEvent event = new RfaEvent();
				event.setId(id);
				theEvent = suspensionApprovalService.doApproval(event, SecurityLibrary.getLoggedInUser(), remarks, false, session, virtualizer, model);
				break;
			}
			case RFI: {
				RfiEvent event = new RfiEvent();
				event.setId(id);
				theEvent = suspensionApprovalService.doApproval(event, SecurityLibrary.getLoggedInUser(), remarks, false, session, virtualizer, model);
				break;
			}
			case RFP: {
				RfpEvent event = new RfpEvent();
				event.setId(id);
				theEvent = suspensionApprovalService.doApproval(event, SecurityLibrary.getLoggedInUser(), remarks, false, session, virtualizer, model);
				break;
			}
			case RFQ: {
				RfqEvent event = new RfqEvent();
				event.setId(id);
				theEvent = suspensionApprovalService.doApproval(event, SecurityLibrary.getLoggedInUser(), remarks, false, session, virtualizer, model);
				break;
			}
			case RFT: {
				RftEvent event = new RftEvent();
				event.setId(id);
				theEvent = suspensionApprovalService.doApproval(event, SecurityLibrary.getLoggedInUser(), remarks, false, session, virtualizer, model);
				break;
			}
			default:
				break;
			}
			redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.event.rejected", new Object[] { (getEventType().name() != null ? getEventType().name() : ""), (theEvent.getEventName() != null ? theEvent.getEventName() : "") }, Global.LOCALE));
		} catch (Exception e) {
			LOG.info("Error while Rejecting Event :" + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.rejecting", new Object[] { e.getMessage() }, Global.LOCALE));
			return "redirect:/buyer/" + getEventType().name() + "/eventSummary/" + id;
		} finally {
			if (virtualizer != null) {
				virtualizer.cleanup();
			}
		}
		return "redirect:/buyer/buyerDashboard";
	}

}
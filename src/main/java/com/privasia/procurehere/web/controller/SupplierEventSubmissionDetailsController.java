
package com.privasia.procurehere.web.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.TimeZone;
import java.util.zip.ZipOutputStream;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.privasia.procurehere.core.dao.PoDao;
import com.privasia.procurehere.core.dao.PrDao;
import com.privasia.procurehere.core.entity.*;
import com.privasia.procurehere.core.pojo.SorItemPojo;
import com.privasia.procurehere.core.pojo.SupplierSorItemResponsePojo;
import com.privasia.procurehere.service.*;
import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonSyntaxException;
import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.PoReportDao;
import com.privasia.procurehere.core.enums.AuctionType;
import com.privasia.procurehere.core.enums.AuditActionType;
import com.privasia.procurehere.core.enums.CqType;
import com.privasia.procurehere.core.enums.DoStatus;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.EventVisibilityType;
import com.privasia.procurehere.core.enums.FinanceCompanyStatus;
import com.privasia.procurehere.core.enums.FinancePoStatus;
import com.privasia.procurehere.core.enums.FinancePoType;
import com.privasia.procurehere.core.enums.MeetingAttendanceStatus;
import com.privasia.procurehere.core.enums.MeetingStatus;
import com.privasia.procurehere.core.enums.NotificationType;
import com.privasia.procurehere.core.enums.PoAuditType;
import com.privasia.procurehere.core.enums.PoAuditVisibilityType;
import com.privasia.procurehere.core.enums.PoStatus;
import com.privasia.procurehere.core.enums.PreBidByType;
import com.privasia.procurehere.core.enums.PricingTypes;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.SubmissionStatusType;
import com.privasia.procurehere.core.enums.SupplierBqStatus;
import com.privasia.procurehere.core.enums.SupplierCqStatus;
import com.privasia.procurehere.core.enums.SuspensionType;
import com.privasia.procurehere.core.enums.TaxType;
import com.privasia.procurehere.core.enums.TeamMemberType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.exceptions.SubscriptionException;
import com.privasia.procurehere.core.pojo.BqItemPojo;
import com.privasia.procurehere.core.pojo.CqPojo;
import com.privasia.procurehere.core.pojo.DoSupplierPojo;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.EventPojo;
import com.privasia.procurehere.core.pojo.InvoiceSupplierPojo;
import com.privasia.procurehere.core.pojo.PaymentIntentPojo;
import com.privasia.procurehere.core.pojo.PoFinanceRequestDocumentsPojo;
import com.privasia.procurehere.core.pojo.PoFinanceRequestPojo;
import com.privasia.procurehere.core.pojo.PoReviseSnapshot;
import com.privasia.procurehere.core.pojo.PoRevisedSnapshotItem;
import com.privasia.procurehere.core.pojo.PoSupplierPojo;
import com.privasia.procurehere.core.pojo.SearchFilterPoPojo;
import com.privasia.procurehere.core.pojo.SupplierBqItemResponsePojo;
import com.privasia.procurehere.core.pojo.SupplierBqPojo;
import com.privasia.procurehere.core.pojo.SupplierCqItem;
import com.privasia.procurehere.core.pojo.SupplierCqItemPojo;
import com.privasia.procurehere.core.pojo.SupplierPerformanceEvaluationPojo;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.supplier.SupplierService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.net.ApiResource;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

@Controller
@RequestMapping("/supplier")
public class SupplierEventSubmissionDetailsController extends SupplierEventBase {

	@Value("${app.url}")
	String APP_URL;

	@Autowired
	EventMessageService eventMessageService;

	@Autowired
	EventAuditService eventAuditService;

	@Autowired
	RfaSupplierBqService rfaSupplierBqService;

	@Autowired
	RfpSupplierBqService rfpSupplierBqService;

	@Autowired
	RftSupplierBqService rftSupplierBqService;

	@Autowired
	RfqSupplierBqService rfqSupplierBqService;

	@Autowired
	RfaSupplierSorService rfaSupplierSorService;

	@Autowired
	RfpSupplierSorService rfpSupplierSorService;

	@Autowired
	RfqSupplierSorService rfqSupplierSorService;

	@Autowired
	RftSupplierSorService rftSupplierSorService;

	@Autowired
	RfiSupplierSorService rfiSupplierSorService;


	@Autowired
	SupplierEventDetailService supplierEventDetailService;

	@Autowired
	SupplierService supplierService;

	@Autowired
	RfaDocumentService rfaDocumentService;

	@Autowired
	RftDocumentService rftDocumentService;

	@Autowired
	RfiDocumentService rfiDocumentService;

	@Autowired
	RfpDocumentService rfpDocumentService;

	@Autowired
	RfqDocumentService rfqDocumentService;

	@Autowired
	FinanceCompanyService financeCompanyService;

	@Autowired
	PrService prService;

	@Autowired
	PoFinanceService poFinanceService;

	@Autowired
	FinanceSettingsService financeSettingsService;

	@Autowired
	NotificationService notificationService;

	@Autowired
	BuyerSettingsService buyerSettingsService;

	@Autowired
	DashboardNotificationService dashboardNotificationService;

	@Autowired
	SupplierRftMeetingAttendanceService supplierRftMeetingAttendanceService;

	@Autowired
	SupplierRfqAttendanceService supplierRfqAttendanceService;

	@Autowired
	SupplierRfpAttendanceService supplierRfpAttendanceService;

	@Autowired
	SupplierRfaAttendanceService supplierRfaAttendanceService;

	@Autowired
	SupplierRfiAttendanceService supplierRfiAttendanceService;

	@Autowired
	FavoriteSupplierService favoriteSupplierService;

	@Autowired
	GenericEventService genericEventService;

	@Autowired
	PoService poService;

	@Autowired
	PoAuditService poAuditService;

	@Autowired
	ApprovalService approvalService;

	@Autowired
	DeliveryOrderService deliveryOrderService;

	@Autowired
	InvoiceService invoiceService;

	@Autowired
	PoReportDao poReportDao;

	@Autowired
	InvoiceFinanceRequestService invoiceFinanceRequestService;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Autowired
	SupplierPerformanceEvaluationService supplierPerformanceEvaluationService;

	@Autowired
	SupplierPerformanceFormService supplierPerformanceFormService;

	@Autowired
	BusinessUnitService businessUnitService;

	@Autowired
	ScoreRatingService scoreRatingService;

	@Autowired
	JmsTemplate jmsTemplate;

	@Autowired
	UomService uomService;

	@Autowired
	PoEventService poEventService;

	@Autowired
	PrDao prDao;


	@ModelAttribute("poStatusList")
	public List<PoStatus> getPoStatusList() {
		return Arrays.asList(PoStatus.ORDERED, PoStatus.ACCEPTED, PoStatus.DECLINED, PoStatus.CANCELLED);
	}

	@ModelAttribute("poSupStatusList")
	public List<PoStatus> getPoSupStatusList() {
		return Arrays.asList(PoStatus.ORDERED, PoStatus.ACCEPTED, PoStatus.DECLINED, PoStatus.CANCELLED, PoStatus.REVISE);
	}

	@ModelAttribute("doStatusList")
	public List<DoStatus> getDoStatusList() {
		return Arrays.asList(DoStatus.values());
	}

	@RequestMapping(path = "/supplierEvent/{eventType}/{eventId}", method = RequestMethod.GET)
	public ModelAndView supplierEventDetails(@PathVariable(name = "eventType") RfxTypes eventType, @PathVariable(name = "eventId") String eventId) {
		LOG.info("eventType : " + eventType.name() + "    ::  " + eventType.toString());
		String eventName = "";
		switch (eventType) {
		case RFA:
			int updatedRowCountRfa = rfaEventSupplierService.updatePrivewTime(eventId, SecurityLibrary.getLoggedInUserTenantId());
			if (updatedRowCountRfa > 0) {
				try {
					eventName = rfaEventSupplierService.getEventNameByEventId(eventId);
					RfaEventAudit audit = new RfaEventAudit();
					audit.setActionBy(SecurityLibrary.getLoggedInUser());
					audit.setActionDate(new Date());
					audit.setDescription("Event '" + eventName + " ' previewed");
					audit.setAction(AuditActionType.Previewed);
					RfaEvent event = new RfaEvent();
					event.setId(eventId);
					audit.setEvent(event);
					eventAuditService.save(audit);
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}
			}
			break;
		case RFI:
			int updatedRowCountRfi = rfiEventSupplierService.updatePrivewTime(eventId, SecurityLibrary.getLoggedInUserTenantId());
			if (updatedRowCountRfi > 0) {
				try {
					eventName = rfiEventSupplierService.getEventNameByEventId(eventId);
					RfiEventAudit audit = new RfiEventAudit();
					audit.setActionBy(SecurityLibrary.getLoggedInUser());
					audit.setActionDate(new Date());
					audit.setDescription("Event '" + eventName + " ' previewed");
					audit.setAction(AuditActionType.Previewed);
					RfiEvent event = new RfiEvent();
					event.setId(eventId);
					audit.setEvent(event);
					eventAuditService.save(audit);
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}

			}
			break;
		case RFP:
			int updatedRowCountRfp = rfpEventSupplierService.updatePrivewTime(eventId, SecurityLibrary.getLoggedInUserTenantId());
			if (updatedRowCountRfp > 0) {
				try {
					eventName = rfpEventSupplierService.getEventNameByEventId(eventId);
					RfpEventAudit audit = new RfpEventAudit();
					audit.setActionBy(SecurityLibrary.getLoggedInUser());
					audit.setActionDate(new Date());
					audit.setDescription("Event '" + eventName + " ' previewed");
					audit.setAction(AuditActionType.Previewed);
					RfpEvent event = new RfpEvent();
					event.setId(eventId);
					audit.setEvent(event);
					eventAuditService.save(audit);
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}

			}
			break;
		case RFQ:
			int updatedRowCountRfq = rfqEventSupplierService.updatePrivewTime(eventId, SecurityLibrary.getLoggedInUserTenantId());
			if (updatedRowCountRfq > 0) {
				try {
					eventName = rfqEventSupplierService.getEventNameByEventId(eventId);
					RfqEventAudit audit = new RfqEventAudit();
					audit.setActionBy(SecurityLibrary.getLoggedInUser());
					audit.setActionDate(new Date());
					audit.setDescription("Event '" + eventName + " ' previewed");
					audit.setAction(AuditActionType.Previewed);
					RfqEvent event = new RfqEvent();
					event.setId(eventId);
					audit.setEvent(event);
					eventAuditService.save(audit);
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}

			}
			break;
		case RFT:
			int updatedRowCountRft = rftEventSupplierService.updatePrivewTime(eventId, SecurityLibrary.getLoggedInUserTenantId());
			if (updatedRowCountRft > 0) {
				try {
					eventName = rftEventSupplierService.getEventNameByEventId(eventId);
					RftEventAudit audit = new RftEventAudit();
					audit.setActionBy(SecurityLibrary.getLoggedInUser());
					audit.setActionDate(new Date());
					audit.setDescription("Event '" + eventName + " ' previewed");
					audit.setAction(AuditActionType.Previewed);
					RftEvent event = new RftEvent();
					event.setId(eventId);
					audit.setEvent(event);
					eventAuditService.save(audit);
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}

			}
			break;
		default:
			break;
		}
		return new ModelAndView("redirect:/supplier/viewSupplierEvent/" + eventType.name() + "/" + eventId);
	}

	@RequestMapping(path = "/viewSupplierEvent/{eventType}/{eventId}", method = RequestMethod.GET)
	public String viewSupplierEvent(@PathVariable(name = "eventType") RfxTypes eventType, @PathVariable String eventId, @RequestParam(name = "payment_intent", required = false) String paymentStatus, Model model) {
		LOG.info(" viewSupplierEvent supplierEvent called: " + paymentStatus);

		boolean hideEventDetails = false;
		try {
			if (StringUtils.checkString(eventId).length() == 0) {
				return "redirect:/400_error";
			}

			// Event event = null;
			EventPojo eventPojo = null;
			List<?> eventContacts = null;
			EventSupplier eventSupplier = null;
			EventPermissions eventPermissions = null;
			Declaration supplierDeclaration = null;
			BuyerSettings bs = null;
			boolean isExistSupplier = rftEventSupplierService.isSupplierExistsForPublicEvent(SecurityLibrary.getLoggedInUserTenantId(), eventId, eventType);
			if (!isExistSupplier) {
				LOG.info("self invited supplier validation checking..");
				String buyerId = rftEventService.findTenantIdBasedOnEventIdAndEventType(eventId, eventType);
				if (StringUtils.checkString(buyerId).length() > 0) {
					LOG.info("checking self invited suplier" + SecurityLibrary.getLoggedInUserLoginId() + " event meeting is in past");
					Date meetingDate = rftMeetingService.findMandatorySiteVisitMeetingsByEventId(eventId, eventType);
					if (meetingDate != null && meetingDate.before(new Date())) {
						hideEventDetails = true;
						throw new ApplicationException(messageSource.getMessage("event.public.siteVisit.expired", new Object[] {}, Global.LOCALE));
					}
					boolean isCategoryMandatory = rftEventService.isIndustryCategoryMandatoryInEvent(eventId, eventType);
					if (isCategoryMandatory) {
						LOG.info("checking self invited suplier" + SecurityLibrary.getLoggedInUserLoginId() + " industry categories are matched with event category");
						boolean industryCategoryFound = favoriteSupplierService.existsEventCategoriesInSupplier(eventId, SecurityLibrary.getLoggedInUserTenantId(), eventType);
						if (!industryCategoryFound) {
							hideEventDetails = true;
							throw new ApplicationException(messageSource.getMessage("event.public.categories.notMatched", new Object[] {}, Global.LOCALE));
						}
					}
					EventPojo eventRating = rftEventService.findMinMaxRatingsByEventId(eventId, eventType);
					if (eventRating.getMinimumSupplierRating() != null || eventRating.getMaximumSupplierRating() != null) {
						LOG.info("checking self invited suplier" + SecurityLibrary.getLoggedInUserLoginId() + " rating match to event supplier rating");
						boolean rating = favoriteSupplierService.isSupplierRatingMatchToEventRating(SecurityLibrary.getLoggedInUserTenantId(), eventRating.getMinimumSupplierRating() != null ? eventRating.getMinimumSupplierRating() : null, eventRating.getMaximumSupplierRating() != null ? eventRating.getMaximumSupplierRating() : null, buyerId);
						if (!rating) {
							hideEventDetails = true;
							throw new ApplicationException(messageSource.getMessage("event.public.rating.notSatisfactory", new Object[] {}, Global.LOCALE));
						}
					}
					LOG.info("checking self invited suplier" + SecurityLibrary.getLoggedInUserLoginId() + " is in buyer fav list");
					boolean supplierExistInFavList = favoriteSupplierService.isSelfInviteSupplierInFavouriteList(SecurityLibrary.getLoggedInUserTenantId(), buyerId);
					if (!supplierExistInFavList) {
						hideEventDetails = true;
						throw new ApplicationException(messageSource.getMessage("event.public.selfInvite.favSupplier", new Object[] {}, Global.LOCALE));
					}

				}
			}

			switch (eventType) {
			case RFA:
				int updatedRfa = rfaEventSupplierService.updatePrivewTime(eventId, SecurityLibrary.getLoggedInUserTenantId());
				if (updatedRfa > 0) {
					try {
						RfaEventAudit audit = new RfaEventAudit();
						audit.setActionBy(SecurityLibrary.getLoggedInUser());
						audit.setActionDate(new Date());
						audit.setDescription("Event previewed");
						audit.setAction(AuditActionType.Previewed);
						RfaEvent event = new RfaEvent();
						event.setId(eventId);
						audit.setEvent(event);
						eventAuditService.save(audit);
					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}

				}
				eventPojo = rfaEventService.loadEventPojoForSummeryDetailPageForSupplierById(eventId);
				eventContacts = rfaEventService.getAllContactForEvent(eventId);
				eventSupplier = rfaEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
				AuctionRules auctionRules = rfaEventService.getAuctionRulesByEventId(eventId);
				model.addAttribute("auctionRules", auctionRules);
				if (auctionRules.getLumsumBiddingWithTax() != null) {
					List<RfaSupplierBq> bqList = rfaSupplierBqService.findRfaSupplierBqbyEventIdAndSupplierId(eventId, SecurityLibrary.getLoggedInUserTenantId());
					if (CollectionUtil.isNotEmpty(bqList)) {
						model.addAttribute("bq", bqList.get(0));
					}
				}
				eventPermissions = rfaEventSupplierService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), eventId);
				bs = buyerSettingsService.getBuyerSettingsByTenantId(eventPojo.getTenantId());
				if (StringUtils.isNotBlank(bs.getStripePublishKey()) && StringUtils.isNotBlank(bs.getStripeSecretKey())) {
					model.addAttribute("publishKey", bs.getStripePublishKey());
				}
				model.addAttribute("eventAudit", eventAuditService.getRfaEventAuditForSupplier(eventId, SecurityLibrary.getLoggedInUserTenantId()));
				break;
			case RFI:
				int updateRfi = rfiEventSupplierService.updatePrivewTime(eventId, SecurityLibrary.getLoggedInUserTenantId());
				if (updateRfi > 0) {
					try {
						RfiEventAudit audit = new RfiEventAudit();
						audit.setActionBy(SecurityLibrary.getLoggedInUser());
						audit.setActionDate(new Date());
						audit.setDescription("Event previewed");
						audit.setAction(AuditActionType.Previewed);
						RfiEvent event = new RfiEvent();
						event.setId(eventId);
						audit.setEvent(event);
						eventAuditService.save(audit);
					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}

				}
				eventPojo = rfiEventService.loadEventPojoForSummeryDetailPageForSupplierById(eventId);
				eventContacts = rfiEventService.getAllContactForEvent(eventId);
				eventSupplier = rfiEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
				eventPermissions = rfiEventSupplierService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), eventId);
				bs = buyerSettingsService.getBuyerSettingsByTenantId(eventPojo.getTenantId());
				if (StringUtils.isNotBlank(bs.getStripePublishKey()) && StringUtils.isNotBlank(bs.getStripeSecretKey())) {
					model.addAttribute("publishKey", bs.getStripePublishKey());
				}
				model.addAttribute("eventAudit", eventAuditService.getRfiEventAuditForSupplier(eventId, SecurityLibrary.getLoggedInUserTenantId()));
				break;
			case RFP:
				int updateRfp = rfpEventSupplierService.updatePrivewTime(eventId, SecurityLibrary.getLoggedInUserTenantId());
				if (updateRfp > 0) {
					try {
						RfpEventAudit audit = new RfpEventAudit();
						audit.setActionBy(SecurityLibrary.getLoggedInUser());
						audit.setActionDate(new Date());
						audit.setDescription("Event previewed");
						audit.setAction(AuditActionType.Previewed);
						RfpEvent event = new RfpEvent();
						event.setId(eventId);
						audit.setEvent(event);
						eventAuditService.save(audit);
					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}

				}
				eventPojo = rfpEventService.loadEventPojoForSummeryDetailPageForSupplierById(eventId);
				eventContacts = rfpEventService.getAllContactForEvent(eventId);
				eventSupplier = rfpEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
				eventPermissions = rfpEventSupplierService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), eventId);
				bs = buyerSettingsService.getBuyerSettingsByTenantId(eventPojo.getTenantId());
				if (StringUtils.isNotBlank(bs.getStripePublishKey()) && StringUtils.isNotBlank(bs.getStripeSecretKey())) {
					model.addAttribute("publishKey", bs.getStripePublishKey());
				}
				model.addAttribute("eventAudit", eventAuditService.getRfpEventAuditForSupplier(eventId, SecurityLibrary.getLoggedInUserTenantId()));
				break;
			case RFQ:
				int updateRfq = rfqEventSupplierService.updatePrivewTime(eventId, SecurityLibrary.getLoggedInUserTenantId());
				if (updateRfq > 0) {
					try {
						RfqEventAudit audit = new RfqEventAudit();
						audit.setActionBy(SecurityLibrary.getLoggedInUser());
						audit.setActionDate(new Date());
						audit.setDescription("Event previewed");
						audit.setAction(AuditActionType.Previewed);
						RfqEvent event = new RfqEvent();
						event.setId(eventId);
						audit.setEvent(event);
						eventAuditService.save(audit);
					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}

				}
				eventPojo = rfqEventService.loadEventPojoForSummeryDetailPageForSupplierById(eventId);

				eventContacts = rfqEventService.getAllContactForEvent(eventId);
				eventSupplier = rfqEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
				eventPermissions = rfqEventSupplierService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), eventId);
				bs = buyerSettingsService.getBuyerSettingsByTenantId(eventPojo.getTenantId());
				model.addAttribute("eventAudit", eventAuditService.getRfqEventAuditForSupplier(eventId, SecurityLibrary.getLoggedInUserTenantId()));
				if (StringUtils.isNotBlank(bs.getStripePublishKey()) && StringUtils.isNotBlank(bs.getStripeSecretKey())) {
					model.addAttribute("publishKey", bs.getStripePublishKey());
				}
				break;
			case RFT:
				int updateRft = rftEventSupplierService.updatePrivewTime(eventId, SecurityLibrary.getLoggedInUserTenantId());
				if (updateRft > 0) {
					try {
						RftEventAudit audit = new RftEventAudit();
						audit.setActionBy(SecurityLibrary.getLoggedInUser());
						audit.setActionDate(new Date());
						audit.setDescription("Event previewed");
						audit.setAction(AuditActionType.Previewed);
						RftEvent event = new RftEvent();
						event.setId(eventId);
						audit.setEvent(event);
						eventAuditService.save(audit);
					} catch (Exception e) {
						LOG.error(e.getMessage(), e);
					}
				}
				eventPojo = rftEventService.loadEventPojoForSummeryPageForSupplierById(eventId);
				eventContacts = rftEventService.getAllContactForEvent(eventId);
				eventSupplier = rftEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
				eventPermissions = rftEventSupplierService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), eventId);
				bs = buyerSettingsService.getBuyerSettingsByTenantId(eventPojo.getTenantId());
				if (StringUtils.isNotBlank(bs.getStripePublishKey()) && StringUtils.isNotBlank(bs.getStripeSecretKey())) {
					model.addAttribute("publishKey", bs.getStripePublishKey());
				}
				model.addAttribute("eventAudit", eventAuditService.getRftEventAuditForSupplier(eventId, SecurityLibrary.getLoggedInUserTenantId()));

				break;
			default:
				break;
			}
			if (Boolean.TRUE == eventPojo.getEnableSupplierDeclaration()) {
				supplierDeclaration = rftEventService.getDeclarationForSupplierByEventId(eventId, eventType);
			}

			if (eventSupplier != null) {
				if (Boolean.FALSE == (eventSupplier.getFeePaid()) && StringUtils.isNotBlank(eventSupplier.getFeeReference()) && StringUtils.isNotBlank(eventSupplier.getFeeReferenceClientId())) {
					if (Boolean.TRUE.equals(supplierEventDetailService.checkForPendingPayments(eventSupplier.getFeeReference(), eventPojo.getTenantId()))) {
						model.addAttribute("info", "A transaction of " + (eventPojo.getParticipationFeeCurrency().toUpperCase() + " " + (eventPojo.getParticipationFees().divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP))) + " is in process. Please click pay to initiate a new transaction.");
					}
				}
				if (StringUtils.checkString(paymentStatus).length() > 0) {
					try {
						String msg = supplierEventDetailService.getPaymentStatus(eventSupplier.getFeeReference(), eventPojo.getTenantId());
						if (msg.indexOf("Processing") != -1) {
							model.addAttribute("info", msg);
						} else {

							// If payment is successful update event audit. Database values are updated by webhook.
							eventSupplier.setFeePaid(Boolean.TRUE);
							model.addAttribute("success", msg);
							switch (eventType) {
							case RFA:
								try {
									RfaEventAudit audit = new RfaEventAudit();
									audit.setActionBy(SecurityLibrary.getLoggedInUser());
									audit.setActionDate(new Date());
									audit.setDescription("Event Participaton Fee of " + eventPojo.getParticipationFeeCurrency() + " " + (eventPojo.getParticipationFees().setScale(2, BigDecimal.ROUND_HALF_UP)) + " paid by " + eventSupplier.getSupplierCompanyName() + ". Fee Reference: " + eventSupplier.getFeeReference());
									audit.setAction(AuditActionType.Paid);
									audit.setSupplier(eventSupplier.getSupplier());
									RfaEvent event = new RfaEvent();
									event.setId(eventId);
									audit.setEvent(event);
									eventAuditService.save(audit);
								} catch (Exception e) {
									LOG.error(e.getMessage(), e);
								}
								model.addAttribute("eventAudit", eventAuditService.getRfaEventAuditForSupplier(eventId, SecurityLibrary.getLoggedInUserTenantId()));
								break;
							case RFI:
								try {
									RfiEventAudit audit = new RfiEventAudit();
									audit.setActionBy(SecurityLibrary.getLoggedInUser());
									audit.setActionDate(new Date());
									audit.setDescription("Event Participaton Fee of " + eventPojo.getParticipationFeeCurrency() + " " + (eventPojo.getParticipationFees().setScale(2, BigDecimal.ROUND_HALF_UP)) + " paid by " + eventSupplier.getSupplierCompanyName() + ". Fee Reference: " + eventSupplier.getFeeReference());
									audit.setAction(AuditActionType.Paid);
									audit.setSupplier(eventSupplier.getSupplier());
									RfiEvent event = new RfiEvent();
									event.setId(eventId);
									audit.setEvent(event);
									eventAuditService.save(audit);
								} catch (Exception e) {
									LOG.error(e.getMessage(), e);
								}
								model.addAttribute("eventAudit", eventAuditService.getRfiEventAuditForSupplier(eventId, SecurityLibrary.getLoggedInUserTenantId()));
								break;
							case RFP:
								try {
									RfpEventAudit audit = new RfpEventAudit();
									audit.setActionBy(SecurityLibrary.getLoggedInUser());
									audit.setActionDate(new Date());
									audit.setDescription("Event Participaton Fee of " + eventPojo.getParticipationFeeCurrency() + " " + (eventPojo.getParticipationFees().setScale(2, BigDecimal.ROUND_HALF_UP)) + " paid by " + eventSupplier.getSupplierCompanyName() + ". Fee Reference: " + eventSupplier.getFeeReference());
									audit.setAction(AuditActionType.Paid);
									audit.setSupplier(eventSupplier.getSupplier());
									RfpEvent event = new RfpEvent();
									event.setId(eventId);
									audit.setEvent(event);
									eventAuditService.save(audit);
								} catch (Exception e) {
									LOG.error(e.getMessage(), e);
								}
								model.addAttribute("eventAudit", eventAuditService.getRfpEventAuditForSupplier(eventId, SecurityLibrary.getLoggedInUserTenantId()));
								break;
							case RFQ:
								try {
									RfqEventAudit audit = new RfqEventAudit();
									audit.setActionBy(SecurityLibrary.getLoggedInUser());
									audit.setActionDate(new Date());
									audit.setDescription("Event Participaton Fee of " + eventPojo.getParticipationFeeCurrency() + " " + (eventPojo.getParticipationFees().setScale(2, BigDecimal.ROUND_HALF_UP)) + " paid by " + eventSupplier.getSupplierCompanyName() + ". Fee Reference: " + eventSupplier.getFeeReference());
									audit.setAction(AuditActionType.Paid);
									audit.setSupplier(eventSupplier.getSupplier());
									RfqEvent event = new RfqEvent();
									event.setId(eventId);
									audit.setEvent(event);
									eventAuditService.save(audit);
								} catch (Exception e) {
									LOG.error(e.getMessage(), e);
								}
								model.addAttribute("eventAudit", eventAuditService.getRfqEventAuditForSupplier(eventId, SecurityLibrary.getLoggedInUserTenantId()));
								break;
							case RFT:
								try {
									RftEventAudit audit = new RftEventAudit();
									audit.setActionBy(SecurityLibrary.getLoggedInUser());
									audit.setActionDate(new Date());
									audit.setDescription("Event Participaton Fee of " + eventPojo.getParticipationFeeCurrency() + " " + (eventPojo.getParticipationFees().setScale(2, BigDecimal.ROUND_HALF_UP)) + " paid by " + eventSupplier.getSupplierCompanyName() + ". Fee Reference: " + eventSupplier.getFeeReference());
									audit.setAction(AuditActionType.Paid);
									audit.setSupplier(eventSupplier.getSupplier());
									RftEvent event = new RftEvent();
									event.setId(eventId);
									audit.setEvent(event);
									eventAuditService.save(audit);
								} catch (Exception e) {
									LOG.error(e.getMessage(), e);
								}
								model.addAttribute("eventAudit", eventAuditService.getRftEventAuditForSupplier(eventId, SecurityLibrary.getLoggedInUserTenantId()));
								break;
							default:
								break;
							}

						}
					} catch (ApplicationException e) {
						model.addAttribute("errors", e.getMessage());
					}

				}
			}

			boolean allowed = super.associateBuyerWithSupplier(true, SecurityLibrary.getLoggedInUserTenantId(), eventPojo.getTenantId());
			model.addAttribute("allowThisEvent", allowed);

			model.addAttribute("supplierDeclaration", supplierDeclaration);
			model.addAttribute("hideEventDetails", hideEventDetails);
			model.addAttribute("eventDetails", true);
			model.addAttribute("supplier", supplierService.findSupplierSubscriptionDetailsBySupplierId(SecurityLibrary.getLoggedInUserTenantId()));
			model.addAttribute("eventPermissions", eventPermissions);
			model.addAttribute("eventSupplier", eventSupplier);
			model.addAttribute("eventContacts", eventContacts);
			LOG.info("Tenant ID : " + eventPojo.getTenantId());
			model.addAttribute("event", eventPojo);
		} catch (ApplicationException e) {
			LOG.error("Error while validating self invite supplier : " + e.getMessage());
			model.addAttribute("errors", e.getMessage());
			model.addAttribute("hideEventDetails", hideEventDetails);
			return "supplierEvent";
		} catch (Exception e) {
			LOG.error("Error while view event details :" + e.getMessage(), e);
			model.addAttribute("errors", e.getMessage());
			model.addAttribute("hideEventDetails", hideEventDetails);
		}
		return "supplierEvent";
	}

	@RequestMapping(path = "/acceptOrRejectInvitation/{eventId}/{eventType}/{accepetd}", method = RequestMethod.POST)
	public ModelAndView acceptOrRejectInvitation(@PathVariable(name = "eventId") String eventId, @PathVariable(name = "eventType") RfxTypes eventType, @PathVariable("accepetd") boolean accepted, @RequestParam(value = "rejectionRemark", required = false) String rejectionRemark, Model model, RedirectAttributes redir) {
		LOG.info("EVENTID : " + eventId + "  Accepted : " + accepted + " eventType " + eventType);
		EventSupplier eventSupplier = null;
		Event event = null;
		try {
			super.checkSupplierExpireSubscription(SecurityLibrary.getLoggedInUserTenantId());
		} catch (SubscriptionException se) {
			LOG.error(se.getMessage());
			redir.addFlashAttribute("error", se.getMessage());
			return new ModelAndView("redirect:/supplier/supplierDashboard");
		} catch (Exception e) {
			LOG.error("Error While checking supplier subscription :" + e.getMessage(), e);
		}
		switch (eventType) {
		case RFA: {
			event = rfaEventService.loadRfaEventById(eventId);
			boolean allowed = super.associateBuyerWithSupplier(accepted, SecurityLibrary.getLoggedInUserTenantId(), event.getTenantId());
			if (!allowed) {
				redir.addFlashAttribute("error", "You are not allowed to participate due to your current subscription plan. Please subscribe to our Unlimited Buyer Plan to continue.");
				return new ModelAndView("redirect:/supplier/supplierDashboard");
			}
			if (EventVisibilityType.PRIVATE != event.getEventVisibility() && SecurityLibrary.getLoggedInUser().getSupplier() != null) {
				rfaEventSupplierService.addSupplierForPublicEvent(eventId, SecurityLibrary.getLoggedInUser().getSupplier().getId());
			}
			eventSupplier = rfaEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
			LOG.info("Supplier submission event Supplier : " + eventSupplier.getSupplierCompanyName());
			RfaEvent rfaEvent = rfaEventService.getRfaEventById(eventId);
			if (accepted) {
				eventSupplier.setRejectedBy(null);
				eventSupplier.setRejectedTime(null);
				eventSupplier.setRejectionRemarks(null);
				eventSupplier.setIsRejectedAfterStart(Boolean.FALSE);
				eventSupplier.setAcceptedBy(SecurityLibrary.getLoggedInUser());
				eventSupplier.setSupplierEventReadTime(new Date());
				eventSupplier.setSubmissionStatus(SubmissionStatusType.ACCEPTED);
				redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.supplier.accepted.event", new Object[] {}, Global.LOCALE));
				if (!event.getBillOfQuantity() && !event.getQuestionnaires()) {
					eventSupplier.setSupplierSubmittedTime(new Date());
					eventSupplier.setSubmissionStatus(SubmissionStatusType.COMPLETED);
					eventSupplier.setSubbmitedBy(SecurityLibrary.getLoggedInUser());
					eventSupplier.setSubmitted(Boolean.TRUE);
					try {
						RfaEventAudit rfaAudit = new RfaEventAudit(SecurityLibrary.getLoggedInUser().getSupplier(), rfaEvent, SecurityLibrary.getLoggedInUser(), new Date(), AuditActionType.Submitted, messageSource.getMessage("event.audit.submitted", new Object[] { event.getEventName() }, Global.LOCALE));
						eventAuditService.save(rfaAudit);
					} catch (Exception e) {
						LOG.error("Error during audit save : " + e.getMessage(), e);
					}
				}

			} else {
				eventSupplier.setAcceptedBy(null);
				eventSupplier.setSupplierEventReadTime(null);
				eventSupplier.setRejectedBy(SecurityLibrary.getLoggedInUser());
				eventSupplier.setRejectedTime(new Date());
				if (StringUtils.checkString(rejectionRemark).length() > 0) {
					eventSupplier.setRejectionRemarks(rejectionRemark);
				}
				if (event.getEventStart().before(new Date()) && eventSupplier.getSubmissionStatus() == SubmissionStatusType.ACCEPTED) {
					eventSupplier.setIsRejectedAfterStart(Boolean.TRUE);
				} else {
					eventSupplier.setIsRejectedAfterStart(Boolean.FALSE);
				}
				eventSupplier.setSubmissionStatus(SubmissionStatusType.REJECTED);
				redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.supplier.rejected.event", new Object[] {}, Global.LOCALE));
			}
			rfaEventSupplierService.saveRfaEventSuppliers((RfaEventSupplier) eventSupplier);

			EventSupplier eveSupp = rfaEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
			LOG.info("eve Supp : " + eveSupp.getSubmissionStatus());

			RfaEventAudit rfaEventAudit = null;
			if (accepted) {
				rfaEventAudit = new RfaEventAudit(SecurityLibrary.getLoggedInUser().getSupplier(), rfaEvent, SecurityLibrary.getLoggedInUser(), new Date(), (AuditActionType.Accepted), messageSource.getMessage(("event.audit.accepted"), new Object[] { rfaEvent.getEventName() }, Global.LOCALE));
			} else {
				rfaEventAudit = new RfaEventAudit(SecurityLibrary.getLoggedInUser().getSupplier(), rfaEvent, SecurityLibrary.getLoggedInUser(), new Date(), (AuditActionType.Reject), messageSource.getMessage(("event.supplier.audit.rejected"), new Object[] { rfaEvent.getEventName(), rejectionRemark }, Global.LOCALE));
			}
			eventAuditService.save(rfaEventAudit);
			break;
		}
		case RFI: {
			event = rfiEventService.loadRfiEventById(eventId);
			boolean allowed = super.associateBuyerWithSupplier(accepted, SecurityLibrary.getLoggedInUserTenantId(), event.getTenantId());
			if (!allowed) {
				redir.addFlashAttribute("error", "You are not allowed to participate due to your current subscription plan. Please subscribe to our Unlimited Buyer Plan to continue.");
				return new ModelAndView("redirect:/supplier/supplierDashboard");
			}
			if (EventVisibilityType.PRIVATE != event.getEventVisibility() && SecurityLibrary.getLoggedInUser().getSupplier() != null) {
				rfiEventSupplierService.addSupplierForPublicEvent(eventId, SecurityLibrary.getLoggedInUser().getSupplier().getId());
			}
			eventSupplier = rfiEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
			if (accepted) {

				eventSupplier.setRejectedBy(null);
				eventSupplier.setRejectedTime(null);
				eventSupplier.setRejectionRemarks(null);
				eventSupplier.setIsRejectedAfterStart(Boolean.FALSE);
				eventSupplier.setAcceptedBy(SecurityLibrary.getLoggedInUser());
				eventSupplier.setSupplierEventReadTime(new Date());
				eventSupplier.setSubmissionStatus(SubmissionStatusType.ACCEPTED);
				redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.supplier.accepted.event", new Object[] {}, Global.LOCALE));
			} else {
				eventSupplier.setAcceptedBy(null);
				eventSupplier.setSupplierEventReadTime(null);
				eventSupplier.setRejectedBy(SecurityLibrary.getLoggedInUser());
				eventSupplier.setRejectedTime(new Date());
				if (StringUtils.checkString(rejectionRemark).length() > 0) {
					eventSupplier.setRejectionRemarks(rejectionRemark);
				}
				if (event.getEventStart().before(new Date()) && eventSupplier.getSubmissionStatus() == SubmissionStatusType.ACCEPTED) {
					eventSupplier.setIsRejectedAfterStart(Boolean.TRUE);
				} else {
					eventSupplier.setIsRejectedAfterStart(Boolean.FALSE);
				}
				eventSupplier.setSubmissionStatus(SubmissionStatusType.REJECTED);
				redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.supplier.rejected.event", new Object[] {}, Global.LOCALE));
			}
			rfiEventSupplierService.saveRfiEventSuppliers((RfiEventSupplier) eventSupplier);
			RfiEvent rfiEvent = rfiEventService.getRfiEventById(eventId);

			RfiEventAudit rfiEventAudit = null;
			if (accepted) {
				rfiEventAudit = new RfiEventAudit(SecurityLibrary.getLoggedInUser().getSupplier(), rfiEvent, SecurityLibrary.getLoggedInUser(), new Date(), (AuditActionType.Accepted), messageSource.getMessage(("event.audit.accepted"), new Object[] { rfiEvent.getEventName() }, Global.LOCALE));
			} else {
				rfiEventAudit = new RfiEventAudit(SecurityLibrary.getLoggedInUser().getSupplier(), rfiEvent, SecurityLibrary.getLoggedInUser(), new Date(), (AuditActionType.Reject), messageSource.getMessage(("event.supplier.audit.rejected"), new Object[] { rfiEvent.getEventName(), rejectionRemark }, Global.LOCALE));
			}
			eventAuditService.save(rfiEventAudit);
			break;
		}
		case RFP: {
			event = rfpEventService.loadRfpEventById(eventId);
			boolean allowed = super.associateBuyerWithSupplier(accepted, SecurityLibrary.getLoggedInUserTenantId(), event.getTenantId());
			if (!allowed) {
				redir.addFlashAttribute("error", "You are not allowed to participate due to your current subscription plan. Please subscribe to our Unlimited Buyer Plan to continue.");
				return new ModelAndView("redirect:/supplier/supplierDashboard");
			}
			if (EventVisibilityType.PRIVATE != event.getEventVisibility() && SecurityLibrary.getLoggedInUser().getSupplier() != null) {
				rfpEventSupplierService.addSupplierForPublicEvent(eventId, SecurityLibrary.getLoggedInUser().getSupplier().getId());
			}
			eventSupplier = rfpEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
			if (accepted) {

				eventSupplier.setRejectedBy(null);
				eventSupplier.setRejectedTime(null);
				eventSupplier.setRejectionRemarks(null);
				eventSupplier.setIsRejectedAfterStart(Boolean.FALSE);
				eventSupplier.setAcceptedBy(SecurityLibrary.getLoggedInUser());
				eventSupplier.setSupplierEventReadTime(new Date());
				eventSupplier.setSubmissionStatus(SubmissionStatusType.ACCEPTED);
				redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.supplier.accepted.event", new Object[] {}, Global.LOCALE));
			} else {
				eventSupplier.setAcceptedBy(null);
				eventSupplier.setSupplierEventReadTime(null);
				eventSupplier.setRejectedBy(SecurityLibrary.getLoggedInUser());
				eventSupplier.setRejectedTime(new Date());
				if (StringUtils.checkString(rejectionRemark).length() > 0) {
					eventSupplier.setRejectionRemarks(rejectionRemark);
				}
				if (event.getEventStart().before(new Date()) && eventSupplier.getSubmissionStatus() == SubmissionStatusType.ACCEPTED) {
					eventSupplier.setIsRejectedAfterStart(Boolean.TRUE);
				} else {
					eventSupplier.setIsRejectedAfterStart(Boolean.FALSE);
				}
				eventSupplier.setSubmissionStatus(SubmissionStatusType.REJECTED);
				redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.supplier.rejected.event", new Object[] {}, Global.LOCALE));
			}
			rfpEventSupplierService.saveRfpEventSuppliers((RfpEventSupplier) eventSupplier);
			RfpEvent rfpEvent = rfpEventService.getEventById(eventId);
			RfpEventAudit rfpEventAudit = null;
			if (accepted) {
				rfpEventAudit = new RfpEventAudit(SecurityLibrary.getLoggedInUser().getSupplier(), rfpEvent, SecurityLibrary.getLoggedInUser(), new Date(), (AuditActionType.Accepted), messageSource.getMessage(("event.audit.accepted"), new Object[] { rfpEvent.getEventName() }, Global.LOCALE));
			} else {
				rfpEventAudit = new RfpEventAudit(SecurityLibrary.getLoggedInUser().getSupplier(), rfpEvent, SecurityLibrary.getLoggedInUser(), new Date(), (AuditActionType.Reject), messageSource.getMessage(("event.supplier.audit.rejected"), new Object[] { rfpEvent.getEventName(), rejectionRemark }, Global.LOCALE));
			}
			eventAuditService.save(rfpEventAudit);
			break;
		}
		case RFQ: {
			event = rfqEventService.loadRfqEventById(eventId);
			boolean allowed = super.associateBuyerWithSupplier(accepted, SecurityLibrary.getLoggedInUserTenantId(), event.getTenantId());
			if (!allowed) {
				redir.addFlashAttribute("error", "You are not allowed to participate due to your current subscription plan. Please subscribe to our Unlimited Buyer Plan to continue.");
				return new ModelAndView("redirect:/supplier/supplierDashboard");
			}
			if (EventVisibilityType.PRIVATE != event.getEventVisibility() && SecurityLibrary.getLoggedInUser().getSupplier() != null) {
				rfqEventSupplierService.addSupplierForPublicEvent(eventId, SecurityLibrary.getLoggedInUser().getSupplier().getId());
			}
			eventSupplier = rfqEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
			if (accepted) {
				eventSupplier.setRejectedBy(null);
				eventSupplier.setRejectedTime(null);
				eventSupplier.setRejectionRemarks(null);
				eventSupplier.setIsRejectedAfterStart(Boolean.FALSE);
				eventSupplier.setAcceptedBy(SecurityLibrary.getLoggedInUser());
				eventSupplier.setSupplierEventReadTime(new Date());
				eventSupplier.setSubmissionStatus(SubmissionStatusType.ACCEPTED);
				redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.supplier.accepted.event", new Object[] {}, Global.LOCALE));
			} else {
				eventSupplier.setAcceptedBy(null);
				eventSupplier.setSupplierEventReadTime(null);
				eventSupplier.setRejectedBy(SecurityLibrary.getLoggedInUser());
				eventSupplier.setRejectedTime(new Date());
				if (StringUtils.checkString(rejectionRemark).length() > 0) {
					eventSupplier.setRejectionRemarks(rejectionRemark);
				}
				if (event.getEventStart().before(new Date()) && eventSupplier.getSubmissionStatus() == SubmissionStatusType.ACCEPTED) {
					eventSupplier.setIsRejectedAfterStart(Boolean.TRUE);
				} else {
					eventSupplier.setIsRejectedAfterStart(Boolean.FALSE);
				}
				eventSupplier.setSubmissionStatus(SubmissionStatusType.REJECTED);
				redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.supplier.rejected.event", new Object[] {}, Global.LOCALE));
			}
			rfqEventSupplierService.saveEventSuppliers((RfqEventSupplier) eventSupplier);
			RfqEvent rfqEvent = rfqEventService.getEventById(eventId);
			RfqEventAudit rfqEventAudit = null;
			if (accepted) {
				rfqEventAudit = new RfqEventAudit(SecurityLibrary.getLoggedInUser().getSupplier(), rfqEvent, SecurityLibrary.getLoggedInUser(), new Date(), (AuditActionType.Accepted), messageSource.getMessage(("event.audit.accepted"), new Object[] { rfqEvent.getEventName() }, Global.LOCALE));
			} else {
				rfqEventAudit = new RfqEventAudit(SecurityLibrary.getLoggedInUser().getSupplier(), rfqEvent, SecurityLibrary.getLoggedInUser(), new Date(), (AuditActionType.Reject), messageSource.getMessage(("event.supplier.audit.rejected"), new Object[] { rfqEvent.getEventName(), rejectionRemark }, Global.LOCALE));
			}
			eventAuditService.save(rfqEventAudit);
			break;
		}
		case RFT: {
			event = rftEventService.loadRftEventById(eventId);
			boolean allowed = super.associateBuyerWithSupplier(accepted, SecurityLibrary.getLoggedInUserTenantId(), event.getTenantId());
			if (!allowed) {
				redir.addFlashAttribute("error", "You are not allowed to participate due to your current subscription plan. Please subscribe to our Unlimited Buyer Plan to continue.");
				return new ModelAndView("redirect:/supplier/supplierDashboard");
			}
			if (EventVisibilityType.PRIVATE != event.getEventVisibility() && SecurityLibrary.getLoggedInUser().getSupplier() != null) {
				rftEventSupplierService.addSupplierForPublicEvent(eventId, SecurityLibrary.getLoggedInUser().getSupplier().getId());
			}
			eventSupplier = rftEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
			if (accepted) {
				eventSupplier.setRejectedBy(null);
				eventSupplier.setRejectedTime(null);
				eventSupplier.setRejectionRemarks(null);
				eventSupplier.setIsRejectedAfterStart(Boolean.FALSE);
				eventSupplier.setAcceptedBy(SecurityLibrary.getLoggedInUser());
				eventSupplier.setSupplierEventReadTime(new Date());
				eventSupplier.setSubmissionStatus(SubmissionStatusType.ACCEPTED);
				redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.supplier.accepted.event", new Object[] {}, Global.LOCALE));
			} else {
				eventSupplier.setRejectedBy(SecurityLibrary.getLoggedInUser());
				eventSupplier.setAcceptedBy(null);
				eventSupplier.setSupplierEventReadTime(null);
				eventSupplier.setRejectedTime(new Date());
				if (StringUtils.checkString(rejectionRemark).length() > 0) {
					eventSupplier.setRejectionRemarks(rejectionRemark);
				}
				if (event.getEventStart().before(new Date()) && eventSupplier.getSubmissionStatus() == SubmissionStatusType.ACCEPTED) {
					eventSupplier.setIsRejectedAfterStart(Boolean.TRUE);
				} else {
					eventSupplier.setIsRejectedAfterStart(Boolean.FALSE);
				}
				eventSupplier.setSubmissionStatus(SubmissionStatusType.REJECTED);
				redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.supplier.rejected.event", new Object[] {}, Global.LOCALE));
			}
			rftEventSupplierService.saveRftEventSuppliers((RftEventSupplier) eventSupplier);
			RftEvent rftEvent = rftEventService.getRftEventById(eventId);
			RftEventAudit rftEventAudit = null;
			if (accepted) {
				rftEventAudit = new RftEventAudit(SecurityLibrary.getLoggedInUser().getSupplier(), rftEvent, SecurityLibrary.getLoggedInUser(), new Date(), (AuditActionType.Accepted), messageSource.getMessage(("event.audit.accepted"), new Object[] { rftEvent.getEventName() }, Global.LOCALE));
			} else {
				rftEventAudit = new RftEventAudit(SecurityLibrary.getLoggedInUser().getSupplier(), rftEvent, SecurityLibrary.getLoggedInUser(), new Date(), (AuditActionType.Reject), messageSource.getMessage(("event.supplier.audit.rejected"), new Object[] { rftEvent.getEventName(), rejectionRemark }, Global.LOCALE));
			}
			eventAuditService.save(rftEventAudit);

			// send Email and Dashboard notifications
			break;
		}
		default:
			break;
		}
		/*
		 * PH-833 fixed-----> try { supplierEventDetailService.sendAcceptOrRejectNotifications(event, eventType,
		 * SecurityLibrary.getLoggedInUser().getSupplier().getCompanyName(), accepted,
		 * SecurityLibrary.getLoggedInUser()); } catch (Exception e) {
		 * LOG.error("Error While Sending Accept or reject notifications :" + e.getMessage(), e); }
		 */// if (accepetd) {
		return new ModelAndView("redirect:/supplier/viewSupplierEvent/" + eventType.name() + "/" + eventId);
		// } else {
		// return new ModelAndView("redirect:/supplier/supplierDashboard");
		// }
	}

	@RequestMapping(path = "/finishEvent/{eventType}/{eventId}", method = RequestMethod.POST)
	public ResponseEntity<String> finishEvent(@PathVariable(name = "eventType") RfxTypes eventType, @PathVariable("eventId") String eventId, @RequestParam("siteVisitMandatoryCheck") Boolean siteVisitMandatoryCheck, Model model, HttpServletRequest request) {
		LOG.info("EVENTID : " + eventId);
		HttpHeaders headers = new HttpHeaders();
		EventSupplier eventSupplier = null;
		Boolean isProcced = Boolean.FALSE;
		boolean isRevisedBQ = false;
		Event event = null;
		String ipAddress = request.getHeader("X-FORWARDED-FOR");
		if (ipAddress == null) {
			ipAddress = request.getRemoteAddr();
		}

		try {
			super.checkSupplierExpireSubscription(SecurityLibrary.getLoggedInUserTenantId());
		} catch (SubscriptionException se) {
			LOG.error(se.getMessage());
			headers.add("error", se.getMessage());
			headers.add("url", "/supplier/supplierDashboard");
			return new ResponseEntity<String>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			LOG.error("Error While checking supplier subscription :" + e.getMessage(), e);
		}
		try {
			switch (eventType) {
			case RFA:
				try {
					isProcced = rfaSupplierBqService.checkMandatoryToFinishEvent(SecurityLibrary.getLoggedInUserTenantId(), eventId);
				} catch (NotAllowedException e) {
					LOG.error("Error :" + e.getMessage(), e);
					headers.add("error", e.getMessage());
					headers.add("url", "/supplier/viewSupplierEvent/" + eventType.name() + "/" + eventId);
					return new ResponseEntity<String>(null, headers, HttpStatus.EXPECTATION_FAILED);
				} catch (Exception e) {
					LOG.error("Error while checking mandatory cq bq :" + e.getMessage(), e);
					headers.add("error", "Error occured during submission : " + e.getMessage());
					headers.add("url", "/supplier/viewSupplierEvent/" + eventType.name() + "/" + eventId);
					return new ResponseEntity<String>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
				}
				RfaEvent rfaEvent = rfaEventService.getRfaEventByeventId(eventId);
				event = rfaEvent;
				if (isProcced) {
					if (siteVisitMandatoryCheck == Boolean.TRUE) {
						String rejectedMeetingNamesList = supplierRfaAttendanceService.findMandatoryAttendMeetingsByEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
						if (StringUtils.checkString(rejectedMeetingNamesList).length() > 0) {
							headers.add("rejectedMeetingNamesList", rejectedMeetingNamesList);
							return new ResponseEntity<String>(null, headers, HttpStatus.EXPECTATION_FAILED);
						}
					}
					eventSupplier = rfaEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);

					if (rfaEvent != null && rfaEvent.getStatus() == EventStatus.CLOSED) {
						if ((AuctionType.FORWARD_DUTCH == rfaEvent.getAuctionType() || AuctionType.REVERSE_DUTCH == rfaEvent.getAuctionType()) && rfaEvent.getWinningPrice() != null) {
							if (rfaEvent.getWinningPrice() != null) {
								BigDecimal winningPrice = rfaEvent.getWinningPrice();

								LOG.info("Wining Price     " + winningPrice);
								List<RfaSupplierBq> suppierBq = rfaSupplierBqService.findRfaSupplierBqbyEventIdAndSupplierId(eventId, SecurityLibrary.getLoggedInUserTenantId());
								for (RfaSupplierBq rfaSupplierBq : suppierBq) {
									List<RfaSupplierBqItem> listBq = rfaSupplierBq.getSupplierBqItems();
									if (CollectionUtil.isNotEmpty(listBq)) {
										BigDecimal price = new BigDecimal(0);
										BigDecimal bqItemUnitPrice = new BigDecimal(0);
										for (RfaSupplierBqItem bqitems : listBq) {
											if (bqitems.getOrder() != 0 && bqitems.getUnitPrice() != null) {
												price = bqitems.getTotalAmountWithTax();
												if (PricingTypes.TRADE_IN_PRICE == bqitems.getPriceType()) {
													bqItemUnitPrice = bqItemUnitPrice.subtract(price);
												} else {
													bqItemUnitPrice = bqItemUnitPrice.add(price);
												}
											}
										}

										if (rfaSupplierBq.getAdditionalTax() != null) {
											bqItemUnitPrice = bqItemUnitPrice.add(rfaSupplierBq.getAdditionalTax());
										}

										LOG.info("BqItemunitPrice    === " + bqItemUnitPrice);
										if (winningPrice.longValue() != bqItemUnitPrice.longValue()) {
											headers.add("error", "Your Bill Of Quantities total does not match with your final Auction bid price. Please revise and submit");
											headers.add("url", "/supplier/viewSupplierEvent/" + eventType.name() + "/" + eventId);
											return new ResponseEntity<String>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
										}
									}
								}
							}
						}

						if ((AuctionType.FORWARD_ENGISH == rfaEvent.getAuctionType() || AuctionType.REVERSE_ENGISH == rfaEvent.getAuctionType())) {

							List<RfaSupplierBq> suppierBq = rfaSupplierBqService.findRfaSupplierBqbyEventIdAndSupplierId(eventId, SecurityLibrary.getLoggedInUserTenantId());
							for (RfaSupplierBq rfaSupplierBq : suppierBq) {
								if (rfaSupplierBq.getRevisedGrandTotal() != null) {
									BigDecimal winningPrice = rfaSupplierBq.getRevisedGrandTotal();
									LOG.info("Wining Price     " + winningPrice);
									List<RfaSupplierBqItem> listBq = rfaSupplierBq.getSupplierBqItems();
									if (CollectionUtil.isNotEmpty(listBq)) {
										BigDecimal price = new BigDecimal(0);
										BigDecimal bqItemUnitPrice = new BigDecimal(0);
										for (RfaSupplierBqItem bqitems : listBq) {
											if (bqitems.getOrder() != 0 && bqitems.getUnitPrice() != null) {
												price = bqitems.getTotalAmountWithTax();
												if (PricingTypes.TRADE_IN_PRICE == bqitems.getPriceType()) {
													bqItemUnitPrice = bqItemUnitPrice.subtract(price);
												} else {
													bqItemUnitPrice = bqItemUnitPrice.add(price);
												}
											}
										}
										if (rfaSupplierBq.getAdditionalTax() != null) {
											bqItemUnitPrice = bqItemUnitPrice.add(rfaSupplierBq.getAdditionalTax());
										}
										LOG.info("BqItemunitPrice    === " + bqItemUnitPrice);
										if (winningPrice.longValue() != bqItemUnitPrice.longValue()) {
											headers.add("error", "Your Bill Of Quantities total does not match with your final Auction bid price. Please revise and submit");
											headers.add("url", "/supplier/viewSupplierEvent/" + eventType.name() + "/" + eventId);
											return new ResponseEntity<String>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
										}
									}
								}
							}
						}

					}

					LOG.info("Supplier Event : " + eventSupplier.toLogString());
					AuctionRules auctionRules = rfaEventService.getAuctionRulesByEventId(eventId);

					// Check if revised BQ Submission
					if (rfaEvent.getStatus() == EventStatus.CLOSED && auctionRules.getLumsumBiddingWithTax() != null && eventSupplier.getRevisedBidSubmitted() == Boolean.FALSE) {
						isRevisedBQ = true;
						LOG.info("User : " + SecurityLibrary.getLoggedInUserLoginId() + " performing revised submission for Auction : " + rfaEvent.getId() + " - " + rfaEvent.getEventName());
						((RfaEventSupplier) eventSupplier).setRevisedBidDateAndTime(new Date());
						eventSupplier.setSubmissionStatus(SubmissionStatusType.COMPLETED);
						((RfaEventSupplier) eventSupplier).setRevisedBidByUser(SecurityLibrary.getLoggedInUser());
						((RfaEventSupplier) eventSupplier).setRevisedBidSubmitted(Boolean.TRUE);
						rfaEventSupplierService.saveRfaEventSuppliers((RfaEventSupplier) eventSupplier);
						try {
							RfaEventAudit rfaAudit = new RfaEventAudit(SecurityLibrary.getLoggedInUser().getSupplier(), rfaEvent, SecurityLibrary.getLoggedInUser(), new Date(), AuditActionType.Submitted, messageSource.getMessage("event.audit.revised.submitted", new Object[] { rfaEvent.getEventName() }, Global.LOCALE));
							eventAuditService.save(rfaAudit);
						} catch (Exception e) {
							LOG.error("Error during audit save : " + e.getMessage(), e);
						}
					} else {
						LOG.info("User : " + SecurityLibrary.getLoggedInUserLoginId() + " performing submission for Auction : " + rfaEvent.getId() + " - " + rfaEvent.getEventName());

						eventSupplier.setSupplierSubmittedTime(new Date());
						eventSupplier.setSubmissionStatus(SubmissionStatusType.COMPLETED);
						eventSupplier.setSubbmitedBy(SecurityLibrary.getLoggedInUser());
						eventSupplier.setSubmitted(Boolean.TRUE);
						RfaEventSupplier supplier = (RfaEventSupplier) eventSupplier;
						List<RfaSupplierBq> supplierBqs = rfaSupplierBqService.findRfaSupplierBqbyEventIdAndSupplierId(eventId, SecurityLibrary.getLoggedInUserTenantId());
						if (CollectionUtil.isNotEmpty(supplierBqs)) {
							RfaSupplierBq supplierBq = supplierBqs.get(0);

							// Validation for pre-bid (first time submission) rule checking
							// AuctionRules auctionRules = rfaEventService.getLeanAuctionRulesByEventId(eventId);
							if (!validateAuctionRulesForOwnPrice(eventId, supplierBq.getTotalAfterTax(), auctionRules)) {
								headers.add("error", "Total amount is less than Initial bid price. Please enter more than or equal Initial bid price");
								headers.add("url", "/supplier/viewSupplierEvent/" + eventType.name() + "/" + eventId);
								return new ResponseEntity<String>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
							}

							if (!validateAuctionRulesForOtherSuppliers(eventId, supplierBq.getTotalAfterTax(), auctionRules)) {
								headers.add("error", "This bid value is not acceptable, One of the other supplier is having the same bid price");
								headers.add("url", "/supplier/viewSupplierEvent/" + eventType.name() + "/" + eventId);
								return new ResponseEntity<String>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
							}

							// If Buyer Price then first time Finish (confirm
							// price) should be considered as Bid
							if (Boolean.TRUE == supplierBq.getBuyerSubmited()) {
								supplier.setConfirmPriceDateAndTime(new Date());

							} else {
								// If Not buyer supplied price, consider this as
								// the initial price
								supplierBq.setInitialPrice(supplierBq.getTotalAfterTax());
								if (auctionRules != null && auctionRules.getLumsumBiddingWithTax() != null || (AuctionType.FORWARD_DUTCH == rfaEvent.getAuctionType() || AuctionType.REVERSE_DUTCH == rfaEvent.getAuctionType())) {
									supplierBq.setRevisedGrandTotal(supplierBq.getTotalAfterTax());
								}
								rfaSupplierBqService.updateSupplierBq(supplierBq);
							}
						}
						supplier.setIpAddress(ipAddress);

						LOG.info("Here ------------------------------------------------------------------------------------------------");
						rfaEventSupplierService.saveRfaEventSuppliers(supplier);

						if (CollectionUtil.isNotEmpty(supplierBqs)) {
							RfaSupplierBq supplierBq = supplierBqs.get(0);
							if (auctionRules.getPrebidAsFirstBid() && AuctionType.FORWARD_DUTCH != rfaEvent.getAuctionType() && AuctionType.REVERSE_DUTCH != rfaEvent.getAuctionType()) {
								LOG.info("Here to update ::::::::::");
								AuctionBids auctionBids = new AuctionBids();
								auctionBids.setAmount(supplierBq.getTotalAfterTax());
								auctionBids.setEvent(rfaEvent);
								auctionBids.setBidSubmissionDate(new Date());
								auctionBids.setBidBySupplier(rfaEventSupplierService.findSupplierForId(SecurityLibrary.getLoggedInUserTenantId()));
								try {
									ObjectMapper mapper = new ObjectMapper();
									String json = mapper.writeValueAsString(supplierBq);
									auctionBids.setDetails(json);
									supplierBq.setAuditSupplierBqItems(null);
									LOG.info("BID JSON : " + json);
									RfaSupplierBq bq = mapper.readValue(json, RfaSupplierBq.class);
									LOG.info("Deserialized Object : " + bq.toLogString());
								} catch (Exception e) {
									LOG.error("Error while Converting Supplier bid to json , " + e.getMessage(), e);
								}
								auctionBids.setIpAddress(ipAddress);
								auctionBids = rfaEventService.saveAuctionBids(auctionBids);

								// update ranking
								Integer rankOfSupplier = null;
								if (auctionRules.getFowardAuction()) {
									LOG.info("Here to update the rank : FRD :   ");
									rankOfSupplier = rfaEventSupplierService.updateSupplierAuctionRank(eventId, Boolean.TRUE, supplier.getSupplier().getId());
									LOG.info("Here to after update the rank : FRD :   " + rankOfSupplier);
								} else {
									rankOfSupplier = rfaEventSupplierService.updateSupplierAuctionRank(eventId, Boolean.FALSE, supplier.getSupplier().getId());
									LOG.info("Here to after update the rank : FRD :   " + rankOfSupplier);
								}
								auctionBids.setRankForBid(rankOfSupplier);
								rfaEventService.saveAuctionBids(auctionBids);
								rfaEventService.updateEventSupplierForAuction(eventId, SecurityLibrary.getLoggedInUserTenantId(), ipAddress);

							}
						}

						try {
							RfaEventAudit rfaAudit = new RfaEventAudit(SecurityLibrary.getLoggedInUser().getSupplier(), rfaEvent, SecurityLibrary.getLoggedInUser(), new Date(), AuditActionType.Submitted, messageSource.getMessage("event.audit.submitted", new Object[] { rfaEvent.getEventName() }, Global.LOCALE));
							eventAuditService.save(rfaAudit);
						} catch (Exception e) {
							LOG.error("Error during audit save : " + e.getMessage(), e);
						}
					}
				}
				break;
			case RFI:
				try {
					isProcced = rfiSupplierCqItemService.checkMandatoryToFinishEvent(SecurityLibrary.getLoggedInUserTenantId(), eventId);
				} catch (NotAllowedException e) {
					LOG.error("Error :" + e.getMessage(), e);
					headers.add("error", e.getMessage());
					headers.add("url", "/supplier/viewSupplierEvent/" + eventType.name() + "/" + eventId);
					return new ResponseEntity<String>(null, headers, HttpStatus.EXPECTATION_FAILED);
				} catch (Exception e) {
					LOG.error("Error while checking mandatory cq bq :" + e.getMessage(), e);
					headers.add("error", "Error occured during submission : " + e.getMessage());
					headers.add("url", "/supplier/viewSupplierEvent/" + eventType.name() + "/" + eventId);
					return new ResponseEntity<String>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
				}
				RfiEvent rfiEvent = rfiEventService.getRfiEventById(eventId);
				event = rfiEvent;
				if (isProcced) {
					if (siteVisitMandatoryCheck == Boolean.TRUE) {
						String rejectedMeetingNamesList = supplierRfiAttendanceService.findMandatoryAttendMeetingsByEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
						if (StringUtils.checkString(rejectedMeetingNamesList).length() > 0) {
							headers.add("rejectedMeetingNamesList", rejectedMeetingNamesList);
							return new ResponseEntity<String>(null, headers, HttpStatus.EXPECTATION_FAILED);
						}
					}
					eventSupplier = rfiEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
					LOG.info("Supplier Event : " + eventSupplier.toLogString());
					eventSupplier.setSupplierSubmittedTime(new Date());
					eventSupplier.setSubmissionStatus(SubmissionStatusType.COMPLETED);
					eventSupplier.setSubbmitedBy(SecurityLibrary.getLoggedInUser());
					eventSupplier.setIpAddress(ipAddress);
					eventSupplier.setSubmitted(Boolean.TRUE);
					rfiEventSupplierService.saveRfiEventSuppliers((RfiEventSupplier) eventSupplier);
					RfiEventAudit rfiAudit = new RfiEventAudit(SecurityLibrary.getLoggedInUser().getSupplier(), rfiEvent, SecurityLibrary.getLoggedInUser(), new Date(), AuditActionType.Submitted, messageSource.getMessage("event.audit.submitted", new Object[] { rfiEvent.getEventName() }, Global.LOCALE));
					eventAuditService.save(rfiAudit);
				}
				break;
			case RFP:
				try {
					isProcced = rfpSupplierCqItemService.checkMandatoryToFinishEvent(SecurityLibrary.getLoggedInUserTenantId(), eventId);
				} catch (NotAllowedException e) {
					LOG.error("Error :" + e.getMessage(), e);
					headers.add("error", e.getMessage());
					headers.add("url", "/supplier/viewSupplierEvent/" + eventType.name() + "/" + eventId);
					return new ResponseEntity<String>(null, headers, HttpStatus.EXPECTATION_FAILED);
				} catch (Exception e) {
					LOG.error("Error while checking mandatory cq bq :" + e.getMessage(), e);
					headers.add("error", "Error occured during submission : " + e.getMessage());
					headers.add("url", "/supplier/viewSupplierEvent/" + eventType.name() + "/" + eventId);
					return new ResponseEntity<String>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
				}
				RfpEvent rfpEvent = rfpEventService.getEventById(eventId);
				event = rfpEvent;
				if (isProcced) {
					if (siteVisitMandatoryCheck == Boolean.TRUE) {
						String rejectedMeetingNamesList = supplierRfpMeetingAttendanceService.findMandatoryAttendMeetingsByEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
						if (StringUtils.checkString(rejectedMeetingNamesList).length() > 0) {
							headers.add("rejectedMeetingNamesList", rejectedMeetingNamesList);
							return new ResponseEntity<String>(null, headers, HttpStatus.EXPECTATION_FAILED);
						}
					}
					eventSupplier = rfpEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
					LOG.info("Supplier Event : " + eventSupplier.toLogString());
					eventSupplier.setSupplierSubmittedTime(new Date());
					eventSupplier.setSubmissionStatus(SubmissionStatusType.COMPLETED);
					eventSupplier.setSubbmitedBy(SecurityLibrary.getLoggedInUser());
					eventSupplier.setSubmitted(Boolean.TRUE);
					eventSupplier.setIpAddress(ipAddress);
					rfpEventSupplierService.saveRfpEventSuppliers((RfpEventSupplier) eventSupplier);
					RfpEventAudit rfpAudit = new RfpEventAudit(SecurityLibrary.getLoggedInUser().getSupplier(), rfpEvent, SecurityLibrary.getLoggedInUser(), new Date(), AuditActionType.Submitted, messageSource.getMessage("event.audit.submitted", new Object[] { rfpEvent.getEventName() }, Global.LOCALE));
					eventAuditService.save(rfpAudit);
				}
				break;
			case RFQ:
				try {
					isProcced = rfqSupplierCqItemService.checkMandatoryToFinishEvent(SecurityLibrary.getLoggedInUserTenantId(), eventId);
				} catch (NotAllowedException e) {
					LOG.error("Error :" + e.getMessage(), e);
					headers.add("error", e.getMessage());
					headers.add("url", "/supplier/viewSupplierEvent/" + eventType.name() + "/" + eventId);
					return new ResponseEntity<String>(null, headers, HttpStatus.EXPECTATION_FAILED);
				} catch (Exception e) {
					LOG.error("Error while checking mandatory cq bq :" + e.getMessage(), e);
					headers.add("error", "Error occured during submission : " + e.getMessage());
					headers.add("url", "/supplier/viewSupplierEvent/" + eventType.name() + "/" + eventId);
					return new ResponseEntity<String>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
				}
				RfqEvent rfqEvent = rfqEventService.getEventById(eventId);
				event = rfqEvent;
				if (isProcced) {
					if (siteVisitMandatoryCheck == Boolean.TRUE) {
						String rejectedMeetingNamesList = supplierRfqAttendanceService.findMandatoryAttendMeetingsByEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
						if (StringUtils.checkString(rejectedMeetingNamesList).length() > 0) {
							headers.add("rejectedMeetingNamesList", rejectedMeetingNamesList);
							return new ResponseEntity<String>(null, headers, HttpStatus.EXPECTATION_FAILED);
						}
					}
					eventSupplier = rfqEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
					LOG.info("Supplier Event : " + eventSupplier.toLogString());
					eventSupplier.setSupplierSubmittedTime(new Date());
					eventSupplier.setSubmissionStatus(SubmissionStatusType.COMPLETED);
					eventSupplier.setSubbmitedBy(SecurityLibrary.getLoggedInUser());
					eventSupplier.setSubmitted(Boolean.TRUE);
					eventSupplier.setIpAddress(ipAddress);
					rfqEventSupplierService.saveEventSuppliers((RfqEventSupplier) eventSupplier);
					RfqEventAudit rfqAudit = new RfqEventAudit(SecurityLibrary.getLoggedInUser().getSupplier(), rfqEvent, SecurityLibrary.getLoggedInUser(), new Date(), AuditActionType.Submitted, messageSource.getMessage("event.audit.submitted", new Object[] { rfqEvent.getEventName() }, Global.LOCALE));
					eventAuditService.save(rfqAudit);
				}
				break;
			case RFT:
				try {
					isProcced = rftSupplierCqItemService.checkMandatoryToFinishEvent(SecurityLibrary.getLoggedInUserTenantId(), eventId);
				} catch (NotAllowedException e) {
					LOG.error("Error :" + e.getMessage(), e);
					headers.add("error", e.getMessage());
					headers.add("url", "/supplier/viewSupplierEvent/" + eventType.name() + "/" + eventId);
					return new ResponseEntity<String>(null, headers, HttpStatus.EXPECTATION_FAILED);
				} catch (Exception e) {
					LOG.error("Error while checking mandatory cq bq :" + e.getMessage(), e);
					headers.add("error", "Error occured during submission : " + e.getMessage());
					headers.add("url", "/supplier/viewSupplierEvent/" + eventType.name() + "/" + eventId);
					return new ResponseEntity<String>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
				}
				RftEvent rftEvent = rftEventService.getRftEventById(eventId);
				event = rftEvent;
				if (isProcced) {
					if (siteVisitMandatoryCheck == Boolean.TRUE) {
						LOG.info("checking site visit mandatory conditions...." + SecurityLibrary.getLoggedInUserTenantId());
						String rejectedMeetingNamesList = supplierRftMeetingAttendanceService.findMandatoryAttendMeetingsByEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
						if (StringUtils.checkString(rejectedMeetingNamesList).length() > 0) {
							headers.add("rejectedMeetingNamesList", rejectedMeetingNamesList);
							return new ResponseEntity<String>(null, headers, HttpStatus.EXPECTATION_FAILED);
						}
					}
					eventSupplier = rftEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
					LOG.info("Supplier Event : " + eventSupplier.toLogString());
					eventSupplier.setSupplierSubmittedTime(new Date());
					eventSupplier.setSubmissionStatus(SubmissionStatusType.COMPLETED);
					eventSupplier.setSubbmitedBy(SecurityLibrary.getLoggedInUser());
					eventSupplier.setSubmitted(Boolean.TRUE);
					eventSupplier.setIpAddress(ipAddress);
					rftEventSupplierService.saveRftEventSuppliers((RftEventSupplier) eventSupplier);

					RftEventAudit rftAudit = new RftEventAudit(SecurityLibrary.getLoggedInUser().getSupplier(), rftEvent, SecurityLibrary.getLoggedInUser(), new Date(), AuditActionType.Submitted, messageSource.getMessage("event.audit.submitted", new Object[] { rftEvent.getEventName() }, Global.LOCALE));
					eventAuditService.save(rftAudit);
				}
				break;
			default:
				break;
			}
		} catch (Exception e) {
			LOG.error("Error during submission :" + e.getMessage(), e);
			headers.add("url", "/supplier/viewSupplierEvent/" + eventType.name() + "/" + eventId);
			headers.add("error", "Error occured during submission : " + e.getMessage());
			return new ResponseEntity<String>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		headers.add("url", "/supplier/viewSupplierEvent/" + eventType.name() + "/" + eventId);
		headers.add("success", "Event successfully submitted");

		try {
			supplierEventDetailService.sendSubmissionNotifications(event, eventType, SecurityLibrary.getLoggedInUser().getSupplier().getCompanyName(), isRevisedBQ, SecurityLibrary.getLoggedInUser());
		} catch (Exception e) {
			LOG.error("Error While sending submission notification :" + e.getMessage(), e);
		}
		return new ResponseEntity<String>(null, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/viewSupplierDocument/{eventType}/{eventId}", method = RequestMethod.GET)
	public String viewSupplierDocument(Model model, @PathVariable(name = "eventType") RfxTypes eventType, @PathVariable String eventId) {
		EventPojo event = null;
		List<?> eventDocuments = null;
		EventSupplier eventSupplier = null;
		EventPermissions eventPermissions = null;
		switch (eventType) {
		case RFA:
			eventSupplier = rfaEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
			if (eventSupplier == null || (eventSupplier != null && (eventSupplier.getSupplierEventReadTime() == null || eventSupplier.getRejectedTime() == null) && (eventSupplier.getRejectedTime() != null && eventSupplier.getIsRejectedAfterStart() == Boolean.FALSE))) {
				return "redirect:/400_error";
			}
			event = rfaEventService.loadEventPojoForSummeryDetailPageForSupplierById(eventId);
			if (event.getEventEnd().after(new Date())) {
				eventDocuments = rfaDocumentService.findAllRfaEventdocsbyEventId(eventId);
			}
			eventPermissions = rfaEventSupplierService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), eventId);
			break;
		case RFI:
			eventSupplier = rfiEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
			if (eventSupplier == null || (eventSupplier != null && (eventSupplier.getSupplierEventReadTime() == null || eventSupplier.getRejectedTime() == null) && (eventSupplier.getRejectedTime() != null && eventSupplier.getIsRejectedAfterStart() == Boolean.FALSE))) {
				return "redirect:/400_error";
			}
			event = rfiEventService.loadEventPojoForSummeryDetailPageForSupplierById(eventId);
			if (event.getEventEnd().after(new Date())) {
				eventDocuments = rfiDocumentService.findAllRfiEventdocsbyEventId(eventId);
			}
			eventPermissions = rfiEventSupplierService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), eventId);
			break;
		case RFP:
			eventSupplier = rfpEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
			if (eventSupplier == null || (eventSupplier != null && (eventSupplier.getSupplierEventReadTime() == null || eventSupplier.getRejectedTime() == null) && (eventSupplier.getRejectedTime() != null && eventSupplier.getIsRejectedAfterStart() == Boolean.FALSE))) {
				return "redirect:/400_error";
			}
			event = rfpEventService.loadEventPojoForSummeryDetailPageForSupplierById(eventId);
			if (event.getEventEnd().after(new Date())) {
				eventDocuments = rfpDocumentService.findAllEventdocsbyEventId(eventId);
			}
			eventPermissions = rfpEventSupplierService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), eventId);
			break;
		case RFQ:
			eventSupplier = rfqEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
			if (eventSupplier == null || (eventSupplier != null && (eventSupplier.getSupplierEventReadTime() == null || eventSupplier.getRejectedTime() == null) && (eventSupplier.getRejectedTime() != null && eventSupplier.getIsRejectedAfterStart() == Boolean.FALSE))) {
				return "redirect:/400_error";
			}
			event = rfqEventService.loadEventPojoForSummeryDetailPageForSupplierById(eventId);
			if (event.getEventEnd().after(new Date())) {
				eventDocuments = rfqDocumentService.findAllEventdocsbyEventId(eventId);
			}
			eventPermissions = rfqEventSupplierService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), eventId);
			break;
		case RFT:
			eventSupplier = rftEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
			if (eventSupplier == null || (eventSupplier != null && (eventSupplier.getSupplierEventReadTime() == null || eventSupplier.getRejectedTime() == null) && (eventSupplier.getRejectedTime() != null && eventSupplier.getIsRejectedAfterStart() == Boolean.FALSE))) {
				return "redirect:/400_error";
			}
			event = rftEventService.loadEventPojoForSummeryPageForSupplierById(eventId);
			if (event.getEventEnd().after(new Date())) {
				eventDocuments = rftDocumentService.findAllRftEventdocsbyEventId(eventId);
			}
			eventPermissions = rftEventSupplierService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), eventId);
			break;
		default:
			break;
		}
		model.addAttribute("supplier", supplierService.findSupplierSubscriptionDetailsBySupplierId(SecurityLibrary.getLoggedInUserTenantId()));
		model.addAttribute("eventPermission", eventPermissions);
		model.addAttribute("eventSupplier", eventSupplier);
		model.addAttribute("event", event);
		model.addAttribute("documents", true);
		model.addAttribute("eventDocs", eventDocuments);
		// model.addAttribute("accepted", ((eventSupplier != null &&
		// eventSupplier.getSupplierEventReadTime() != null &&
		// ((eventType != RfxTypes.RFA ? event.getEventStart().before(new
		// Date()) :
		// event.getEventPublishDate().before(new Date())))) ? true : false));

		return "viewSupplierDocument";
	}

	@RequestMapping(path = "/viewSupplierEventMessages/{eventType}/{eventId}", method = RequestMethod.GET)
	public String viewEventMessages(Model model, @PathVariable(name = "eventType") RfxTypes eventType, @PathVariable String eventId) {
		EventPojo event = null;
		EventSupplier eventSupplier = null;
		EventPermissions eventPermissions = null;
		switch (eventType) {
		case RFA:
			eventSupplier = rfaEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
			if ((eventSupplier.getSupplierEventReadTime() == null || eventSupplier.getRejectedTime() == null) && (eventSupplier.getRejectedTime() != null && eventSupplier.getIsRejectedAfterStart() == Boolean.FALSE)) {
				return "redirect:/invalid_event";
			}
			eventPermissions = rfaEventSupplierService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), eventId);
			event = rfaEventService.loadEventPojoForSummeryDetailPageForSupplierById(eventId);
			break;
		case RFI:
			eventSupplier = rfiEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
			if ((eventSupplier.getSupplierEventReadTime() == null || eventSupplier.getRejectedTime() == null) && (eventSupplier.getRejectedTime() != null && eventSupplier.getIsRejectedAfterStart() == Boolean.FALSE)) {
				return "redirect:/invalid_event";
			}
			event = rfiEventService.loadEventPojoForSummeryDetailPageForSupplierById(eventId);
			eventPermissions = rfiEventSupplierService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), eventId);
			break;
		case RFP:
			eventSupplier = rfpEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
			if ((eventSupplier.getSupplierEventReadTime() == null || eventSupplier.getRejectedTime() == null) && (eventSupplier.getRejectedTime() != null && eventSupplier.getIsRejectedAfterStart() == Boolean.FALSE)) {
				return "redirect:/invalid_event";
			}
			event = rfpEventService.loadEventPojoForSummeryDetailPageForSupplierById(eventId);
			eventPermissions = rfpEventSupplierService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), eventId);
			break;
		case RFQ:
			eventSupplier = rfqEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
			if ((eventSupplier.getSupplierEventReadTime() == null || eventSupplier.getRejectedTime() == null) && (eventSupplier.getRejectedTime() != null && eventSupplier.getIsRejectedAfterStart() == Boolean.FALSE)) {
				return "redirect:/invalid_event";
			}
			event = rfqEventService.loadEventPojoForSummeryDetailPageForSupplierById(eventId);
			eventPermissions = rfqEventSupplierService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), eventId);
			break;
		case RFT:
			eventSupplier = rftEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
			if ((eventSupplier.getSupplierEventReadTime() == null || eventSupplier.getRejectedTime() == null) && (eventSupplier.getRejectedTime() != null && eventSupplier.getIsRejectedAfterStart() == Boolean.FALSE)) {
				return "redirect:/invalid_event";
			}
			event = rftEventService.loadEventPojoForSummeryPageForSupplierById(eventId);
			eventPermissions = rftEventSupplierService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), eventId);
			break;
		default:
			break;
		}
		model.addAttribute("supplier", supplierService.findSupplierSubscriptionDetailsBySupplierId(SecurityLibrary.getLoggedInUserTenantId()));
		model.addAttribute("eventSupplier", eventSupplier);
		model.addAttribute("eventPermissions", eventPermissions);
		model.addAttribute("event", event);
		model.addAttribute("eventMessage", true);
		model.addAttribute("messagePermission", checkMessagePermissionForTheEvent(eventSupplier));
		return "viewSupplierEventMessages";
	}

	public Boolean checkMessagePermissionForTheEvent(EventSupplier eventSupplier) {
		if(eventSupplier != null && eventSupplier.getAcceptedBy() != null) {
          return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	@RequestMapping(path = "/eventMessages/{eventType}/{eventId}", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<TableData<?>> getEventMessages(@PathVariable(name = "eventType") RfxTypes eventType, @PathVariable("eventId") String eventId, @RequestParam int page, @RequestParam int size, @RequestParam(required = false) String search, Model model) {
		HttpHeaders headers = new HttpHeaders();
		try {
			LOG.info("Fetching event messages for event : " + eventId + ", page : " + page + ", size : " + size + ", search : " + search);
			TableData<?> data = null;
			List<Supplier> suppliers = new ArrayList<>();
			suppliers.add(SecurityLibrary.getLoggedInUser().getSupplier());

			switch (eventType) {
			case RFA: {
				List<RfaEventMessage> messages = eventMessageService.getRfaEventMessagesForSupplierByEventId(eventId, SecurityLibrary.getLoggedInUser().getSupplier().getId(), page, size, search);
				for (RfaEventMessage rfaEventMessage : messages) {
					rfaEventMessage.setSuppliers(suppliers);
				}
				data = new TableData<RfaEventMessage>(messages, eventMessageService.getTotalRfaEventMessageCountForSupplier(eventId, SecurityLibrary.getLoggedInUser().getSupplier().getId()), eventMessageService.getTotalFilteredRfaEventMessageCountForSupplier(eventId, SecurityLibrary.getLoggedInUser().getSupplier().getId(), search));
				break;
			}
			case RFI: {
				List<RfiEventMessage> messages = eventMessageService.getRfiEventMessagesForSupplierByEventId(eventId, SecurityLibrary.getLoggedInUser().getSupplier().getId(), page, size, search);
				for (RfiEventMessage rfiEventMessage : messages) {
					rfiEventMessage.setSuppliers(suppliers);
				}
				data = new TableData<RfiEventMessage>(messages, eventMessageService.getTotalRfiEventMessageCountForSupplier(eventId, SecurityLibrary.getLoggedInUser().getSupplier().getId()), eventMessageService.getTotalFilteredRfiEventMessageCountForSupplier(eventId, SecurityLibrary.getLoggedInUser().getSupplier().getId(), search));
				break;
			}
			case RFP: {
				List<RfpEventMessage> messages = eventMessageService.getRfpEventMessagesForSupplierByEventId(eventId, SecurityLibrary.getLoggedInUser().getSupplier().getId(), page, size, search);
				for (RfpEventMessage rfpEventMessage : messages) {
					rfpEventMessage.setSuppliers(suppliers);
				}
				data = new TableData<RfpEventMessage>(messages, eventMessageService.getTotalRfpEventMessageCountForSupplier(eventId, SecurityLibrary.getLoggedInUser().getSupplier().getId()), eventMessageService.getTotalFilteredRfpEventMessageCountForSupplier(eventId, SecurityLibrary.getLoggedInUser().getSupplier().getId(), search));
				break;
			}
			case RFQ: {
				List<RfqEventMessage> messages = eventMessageService.getRfqEventMessagesForSupplierByEventId(eventId, SecurityLibrary.getLoggedInUser().getSupplier().getId(), page, size, search);
				for (RfqEventMessage rfqEventMessage : messages) {
					rfqEventMessage.setSuppliers(suppliers);
				}
				data = new TableData<RfqEventMessage>(messages, eventMessageService.getTotalRfqEventMessageCountForSupplier(eventId, SecurityLibrary.getLoggedInUser().getSupplier().getId()), eventMessageService.getTotalFilteredRfqEventMessageCountForSupplier(eventId, SecurityLibrary.getLoggedInUser().getSupplier().getId(), search));
				break;
			}
			case RFT: {
				List<RftEventMessage> messages = eventMessageService.getRftEventMessagesForSupplierByEventId(eventId, SecurityLibrary.getLoggedInUser().getSupplier().getId(), page, size, search);
				for (RftEventMessage rftEventMessage : messages) {
					rftEventMessage.setSuppliers(suppliers);
				}
				data = new TableData<RftEventMessage>(messages, eventMessageService.getTotalRftEventMessageCountForSupplier(eventId, SecurityLibrary.getLoggedInUser().getSupplier().getId()), eventMessageService.getTotalFilteredRftEventMessageCountForSupplier(eventId, SecurityLibrary.getLoggedInUser().getSupplier().getId(), search));
				break;
			}
			case PO: {
				List<PoEventMessage> messages = eventMessageService.getPoEventMessagesForSupplierByEventId(eventId, SecurityLibrary.getLoggedInUser().getSupplier().getId(), page, size, search);
				for (PoEventMessage poEventMessage : messages) {
					poEventMessage.setSuppliers(suppliers);
				}
				data = new TableData<PoEventMessage>(messages, eventMessageService.getTotalPoEventMessageCountForSupplier(eventId, SecurityLibrary.getLoggedInUser().getSupplier().getId()), eventMessageService.getTotalFilteredPoEventMessageCountForSupplier(eventId, SecurityLibrary.getLoggedInUser().getSupplier().getId(), search));
				break;
			}
			default:
			}
			return new ResponseEntity<TableData<?>>(data, headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching event messages : " + e.getMessage(), e);
			headers.add("error", "Error fetching event messages : " + e.getMessage());
			return new ResponseEntity<TableData<?>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/sendMessage/{eventType}/{eventId}", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<TableData<?>> addRftMessage(@PathVariable(name = "eventType") RfxTypes eventType, @PathVariable String eventId, @ModelAttribute RftEventMessage message, @RequestParam(required = false) int page, @RequestParam(required = false) int size, @RequestParam(required = false) String search, @RequestParam(required = false) String parentId, @RequestParam(value = "file", required = false) MultipartFile file, BindingResult result, Model model) {
		HttpHeaders headers = new HttpHeaders();
		try {
			if (result.hasErrors()) {
				for (ObjectError err : result.getAllErrors()) {
					headers.add("error", err.getDefaultMessage());
				}
				return new ResponseEntity<TableData<?>>(null, headers, HttpStatus.EXPECTATION_FAILED);
			} else {
				List<Supplier> suppliers = new ArrayList<>();
				suppliers.add(SecurityLibrary.getLoggedInUser().getSupplier());
				TableData<?> data = null;
				String fileName = null;
				switch (eventType) {
				case RFA: {
					RfaEvent rfiEvent = new RfaEvent();
					rfiEvent.setId(eventId);
					RfaEventMessage messageObj = new RfaEventMessage();
					messageObj.setMessage(message.getMessage());
					messageObj.setSubject(message.getSubject());
					List<Supplier> sups = new ArrayList<Supplier>();
					sups.add(SecurityLibrary.getLoggedInUser().getSupplier());
					messageObj.setSuppliers(sups);
					messageObj.setEvent(rfiEvent);
					messageObj.setSentBySupplier(true);
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
					LOG.info("Saving rfqmessage : " + messageObj.toLogString());
					messageObj = eventMessageService.saveEventMessage(messageObj);
					String id = messageObj.getId();
					jmsTemplate.send("QUEUE.EVENT.MESSAGE", new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							TextMessage objectMessage = session.createTextMessage();
							objectMessage.setText(RfxTypes.RFA.name() + id);
							return objectMessage;
						}
					});
					// eventMessageService.sendDashboardNotificationForEventMessage(messageObj);

					List<RfaEventMessage> messages = eventMessageService.getRfaEventMessagesForSupplierByEventId(eventId, SecurityLibrary.getLoggedInUser().getSupplier().getId(), page, size, search);
					for (RfaEventMessage rfaEventMessage : messages) {
						rfaEventMessage.setSuppliers(suppliers);
					}
					data = new TableData<RfaEventMessage>(messages, eventMessageService.getTotalRfaEventMessageCountForSupplier(eventId, SecurityLibrary.getLoggedInUser().getSupplier().getId()), eventMessageService.getTotalFilteredRfaEventMessageCountForSupplier(eventId, SecurityLibrary.getLoggedInUser().getSupplier().getId(), search));
					break;
				}
				case RFI: {
					RfiEvent rfiEvent = new RfiEvent();
					rfiEvent.setId(eventId);
					RfiEventMessage messageObj = new RfiEventMessage();
					messageObj.setMessage(message.getMessage());
					messageObj.setSubject(message.getSubject());
					List<Supplier> sups = new ArrayList<Supplier>();
					sups.add(SecurityLibrary.getLoggedInUser().getSupplier());
					messageObj.setSuppliers(sups);
					messageObj.setEvent(rfiEvent);
					messageObj.setSentBySupplier(true);
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
					LOG.info("Saving rfqmessage : " + messageObj.toLogString());
					messageObj = eventMessageService.saveEventMessage(messageObj);
					String id = messageObj.getId();
					jmsTemplate.send("QUEUE.EVENT.MESSAGE", new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							TextMessage objectMessage = session.createTextMessage();
							objectMessage.setText(RfxTypes.RFI.name() + id);
							return objectMessage;
						}
					});
					// eventMessageService.sendDashboardNotificationForEventMessage(messageObj);

					List<RfiEventMessage> messages = eventMessageService.getRfiEventMessagesForSupplierByEventId(eventId, SecurityLibrary.getLoggedInUser().getSupplier().getId(), page, size, search);
					for (RfiEventMessage rfiEventMessage : messages) {
						rfiEventMessage.setSuppliers(suppliers);
					}
					data = new TableData<RfiEventMessage>(messages, eventMessageService.getTotalRfiEventMessageCountForSupplier(eventId, SecurityLibrary.getLoggedInUser().getSupplier().getId()), eventMessageService.getTotalFilteredRfiEventMessageCountForSupplier(eventId, SecurityLibrary.getLoggedInUser().getSupplier().getId(), search));
					break;
				}
				case RFP: {
					RfpEvent rfpEvent = new RfpEvent();
					rfpEvent.setId(eventId);
					RfpEventMessage messageObj = new RfpEventMessage();
					messageObj.setMessage(message.getMessage());
					messageObj.setSubject(message.getSubject());
					List<Supplier> sups = new ArrayList<Supplier>();
					sups.add(SecurityLibrary.getLoggedInUser().getSupplier());
					messageObj.setSuppliers(sups);
					messageObj.setEvent(rfpEvent);
					messageObj.setSentBySupplier(true);
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
					LOG.info("Saving rfqmessage : " + messageObj.toLogString());
					messageObj = eventMessageService.saveEventMessage(messageObj);
					String id = messageObj.getId();
					jmsTemplate.send("QUEUE.EVENT.MESSAGE", new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							TextMessage objectMessage = session.createTextMessage();
							objectMessage.setText(RfxTypes.RFP.name() + id);
							return objectMessage;
						}
					});
					// eventMessageService.sendDashboardNotificationForEventMessage(messageObj);

					List<RfpEventMessage> messages = eventMessageService.getRfpEventMessagesForSupplierByEventId(eventId, SecurityLibrary.getLoggedInUser().getSupplier().getId(), page, size, search);
					for (RfpEventMessage rfpEventMessage : messages) {
						rfpEventMessage.setSuppliers(suppliers);
					}
					data = new TableData<RfpEventMessage>(messages, eventMessageService.getTotalRfpEventMessageCountForSupplier(eventId, SecurityLibrary.getLoggedInUser().getSupplier().getId()), eventMessageService.getTotalFilteredRfpEventMessageCountForSupplier(eventId, SecurityLibrary.getLoggedInUser().getSupplier().getId(), search));
					break;
				}
				case RFQ: {
					RfqEvent rfqEvent = new RfqEvent();
					rfqEvent.setId(eventId);
					RfqEventMessage messageObj = new RfqEventMessage();
					messageObj.setMessage(message.getMessage());
					messageObj.setSubject(message.getSubject());
					List<Supplier> sups = new ArrayList<Supplier>();
					sups.add(SecurityLibrary.getLoggedInUser().getSupplier());
					messageObj.setSuppliers(sups);
					messageObj.setEvent(rfqEvent);
					messageObj.setSentBySupplier(true);
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
					jmsTemplate.send("QUEUE.EVENT.MESSAGE", new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							TextMessage objectMessage = session.createTextMessage();
							objectMessage.setText(RfxTypes.RFQ.name() + id);
							return objectMessage;
						}
					});
					// eventMessageService.sendDashboardNotificationForEventMessage(messageObj);

					List<RfqEventMessage> messages = eventMessageService.getRfqEventMessagesForSupplierByEventId(eventId, SecurityLibrary.getLoggedInUser().getSupplier().getId(), page, size, search);
					for (RfqEventMessage rfqEventMessage : messages) {
						rfqEventMessage.setSuppliers(suppliers);
					}

					data = new TableData<RfqEventMessage>(messages, eventMessageService.getTotalRfqEventMessageCountForSupplier(eventId, SecurityLibrary.getLoggedInUser().getSupplier().getId()), eventMessageService.getTotalFilteredRfqEventMessageCountForSupplier(eventId, SecurityLibrary.getLoggedInUser().getSupplier().getId(), search));
					break;
				}
				case RFT: {
					RftEvent rftEvent = new RftEvent();
					rftEvent.setId(eventId);
					RftEventMessage messageObj = new RftEventMessage();
					messageObj.setMessage(message.getMessage());
					messageObj.setSubject(message.getSubject());
					List<Supplier> sups = new ArrayList<Supplier>();
					sups.add(SecurityLibrary.getLoggedInUser().getSupplier());
					LOG.info("supplier" + sups.toString());
					messageObj.setSuppliers(sups);
					messageObj.setEvent(rftEvent);
					messageObj.setSentBySupplier(true);
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
						RftEventMessage parent = new RftEventMessage();
						parent.setId(parentId);
						messageObj.setParent(parent);
					}
					LOG.info("Saving rftmessage : " + messageObj.toLogString());
					messageObj = eventMessageService.saveEventMessage(messageObj);
					String id = messageObj.getId();
					jmsTemplate.send("QUEUE.EVENT.MESSAGE", new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							TextMessage objectMessage = session.createTextMessage();
							objectMessage.setText(RfxTypes.RFT.name() + id);
							return objectMessage;
						}
					});

					// eventMessageService.sendDashboardNotificationForEventMessage(messageObj);

					List<RftEventMessage> messages = eventMessageService.getRftEventMessagesForSupplierByEventId(eventId, SecurityLibrary.getLoggedInUser().getSupplier().getId(), page, size, search);
					for (RftEventMessage rftEventMessage : messages) {
						rftEventMessage.setSuppliers(suppliers);
					}

					data = new TableData<RftEventMessage>(messages, eventMessageService.getTotalRftEventMessageCountForSupplier(eventId, SecurityLibrary.getLoggedInUser().getSupplier().getId()), eventMessageService.getTotalFilteredRftEventMessageCountForSupplier(eventId, SecurityLibrary.getLoggedInUser().getSupplier().getId(), search));
					break;
				}
				case PO: {
					PoEvent poEvent = new PoEvent();
					poEvent.setId(eventId);
					PoEventMessage messageObj = new PoEventMessage();
					messageObj.setMessage(message.getMessage());
					messageObj.setSubject(message.getSubject());
					List<Supplier> sups = new ArrayList<Supplier>();
					sups.add(SecurityLibrary.getLoggedInUser().getSupplier());
					messageObj.setSuppliers(sups);
					messageObj.setEvent(poEvent);
					messageObj.setSentBySupplier(true);
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
						PoEventMessage parent = new PoEventMessage();
						parent.setId(parentId);
						messageObj.setParent(parent);
					}
					LOG.info("Saving Po message : " + messageObj.toLogString());
					messageObj = eventMessageService.saveEventMessage(messageObj);
					String id = messageObj.getId();

					eventMessageService.sendNotificationForPoEventMessage(messageObj,false);

					try{
						jmsTemplate.send("QUEUE.EVENT.MESSAGE", new MessageCreator() {
							@Override
							public Message createMessage(Session session) throws JMSException {
								TextMessage objectMessage = session.createTextMessage();
								objectMessage.setText(RfxTypes.PO.name() + id);
								return objectMessage;
							}
						});

					}catch (Exception e){

					}

					// eventMessageService.sendDashboardNotificationForEventMessage(messageObj);

					List<PoEventMessage> messages = eventMessageService.getPoEventMessagesForSupplierByEventId(eventId, SecurityLibrary.getLoggedInUser().getSupplier().getId(), page, size, search);
					for (PoEventMessage poEventMessage : messages) {
						poEventMessage.setSuppliers(suppliers);
					}

					data = new TableData<PoEventMessage>(messages, eventMessageService.getTotalPoEventMessageCountForSupplier(eventId, SecurityLibrary.getLoggedInUser().getSupplier().getId()), eventMessageService.getTotalFilteredPoEventMessageCountForSupplier(eventId, SecurityLibrary.getLoggedInUser().getSupplier().getId(), search));
					break;
				}
				default:
				}
				headers.add("success", "Event message with subject '" + message.getSubject() + "' sent successfully");
				return new ResponseEntity<TableData<?>>(data, headers, HttpStatus.OK);
			}
		} catch (Exception e) {
			LOG.error("Error Saving Message : " + e.getMessage(), e);
			headers.add("error", "Error saving event message : " + e.getMessage());
		}
		return new ResponseEntity<TableData<?>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@RequestMapping(value = "/downloadSupplierDocument/{eventType}/{docsId}", method = RequestMethod.GET)
	public void downloadRftFile(@PathVariable("eventType") RfxTypes eventType, @PathVariable String docsId, HttpServletResponse response) throws IOException {

		try {
			EventDocument docs = null;

			switch (eventType) {
			case RFA:
				docs = rfaDocumentService.findRfaEventdocsById(docsId);
				break;
			case RFI:
				docs = rfiDocumentService.findRfiEventdocsById(docsId);
				break;
			case RFP:
				docs = rfpDocumentService.findEventdocsById(docsId);
				break;
			case RFQ:
				docs = rfqDocumentService.findEventdocsById(docsId);
				break;
			case RFT:
				docs = rftDocumentService.findRftEventdocsById(docsId);
				break;
			default:
				break;
			}

			response.setContentType(docs.getCredContentType());
			response.setContentLength(docs.getFileData().length);
			response.setHeader("Content-Disposition", "attachment; filename=\"" + docs.getFileName() + "\"");
			FileCopyUtils.copy(docs.getFileData(), response.getOutputStream());
			response.flushBuffer();
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (Exception e) {
			LOG.info("Supplier Document Download error :: :: " + e.getMessage() + "::::::");
			LOG.error("Error while downloaded RFT Supplier Document : " + e.getMessage(), e);
		}
	}

	@RequestMapping(path = "/viewSupplierMeeting/{eventType}/{eventId}", method = RequestMethod.GET)
	public String viewSupplierMeeting(Model model, @PathVariable("eventType") RfxTypes eventType, @PathVariable("eventId") String eventId) {
		LOG.info(" viewSupplierMeeting called");
		EventPojo event = null;
		List<?> listRftMeetObj = null;
		EventSupplier eventSupplier = null;
		EventPermissions eventPermissions = null;
		switch (eventType) {
		case RFA:
			eventSupplier = rfaEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
			if (eventSupplier == null || (eventSupplier != null && (eventSupplier.getSupplierEventReadTime() == null || eventSupplier.getRejectedTime() == null) && (eventSupplier.getRejectedTime() != null && eventSupplier.getIsRejectedAfterStart() == Boolean.FALSE))) {
				return "redirect:/400_error";
			}
			event = rfaEventService.loadEventPojoForSummeryDetailPageForSupplierById(eventId);
			listRftMeetObj = rfaMeetingService.loadSupplierMeetingByEventIdAndMeetingStatus(eventId, null, SecurityLibrary.getLoggedInUserTenantId());
			eventPermissions = rfaEventSupplierService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), eventId);
			break;
		case RFI:
			eventSupplier = rfiEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
			if (eventSupplier == null || (eventSupplier != null && (eventSupplier.getSupplierEventReadTime() == null || eventSupplier.getRejectedTime() == null) && (eventSupplier.getRejectedTime() != null && eventSupplier.getIsRejectedAfterStart() == Boolean.FALSE))) {
				return "redirect:/400_error";
			}
			event = rfiEventService.loadEventPojoForSummeryDetailPageForSupplierById(eventId);
			listRftMeetObj = rfiMeetingService.loadSupplierMeetingByEventIdAndMeetingStatus(eventId, null, SecurityLibrary.getLoggedInUserTenantId());
			eventPermissions = rfiEventSupplierService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), eventId);
			break;
		case RFP:
			eventSupplier = rfpEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
			if (eventSupplier == null || (eventSupplier != null && (eventSupplier.getSupplierEventReadTime() == null || eventSupplier.getRejectedTime() == null) && (eventSupplier.getRejectedTime() != null && eventSupplier.getIsRejectedAfterStart() == Boolean.FALSE))) {
				return "redirect:/400_error";
			}
			event = rfpEventService.loadEventPojoForSummeryDetailPageForSupplierById(eventId);
			listRftMeetObj = rfpMeetingService.loadSupplierMeetingByEventIdAndMeetingStatus(eventId, null, SecurityLibrary.getLoggedInUserTenantId());
			eventPermissions = rfpEventSupplierService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), eventId);
			break;
		case RFQ:
			eventSupplier = rfqEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
			if (eventSupplier == null || (eventSupplier != null && (eventSupplier.getSupplierEventReadTime() == null || eventSupplier.getRejectedTime() == null) && (eventSupplier.getRejectedTime() != null && eventSupplier.getIsRejectedAfterStart() == Boolean.FALSE))) {
				return "redirect:/400_error";
			}
			event = rfqEventService.loadEventPojoForSummeryDetailPageForSupplierById(eventId);
			listRftMeetObj = rfqMeetingService.loadSupplierMeetingByEventIdAndMeetingStatus(eventId, null, SecurityLibrary.getLoggedInUserTenantId());
			eventPermissions = rfqEventSupplierService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), eventId);
			break;
		case RFT:
			eventSupplier = rftEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
			if (eventSupplier == null || (eventSupplier != null && (eventSupplier.getSupplierEventReadTime() == null || eventSupplier.getRejectedTime() == null) && (eventSupplier.getRejectedTime() != null && eventSupplier.getIsRejectedAfterStart() == Boolean.FALSE))) {
				return "redirect:/400_error";
			}
			event = rftEventService.loadEventPojoForSummeryPageForSupplierById(eventId);
			listRftMeetObj = rftMeetingService.loadSupplierMeetingByEventIdAndMeetingStatus(eventId, null, SecurityLibrary.getLoggedInUserTenantId());
			eventPermissions = rftEventSupplierService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), eventId);
			break;
		default:
			break;
		}

		model.addAttribute("supplier", supplierService.findSupplierSubscriptionDetailsBySupplierId(SecurityLibrary.getLoggedInUserTenantId()));
		model.addAttribute("eventSupplier", eventSupplier);
		model.addAttribute("eventPermissions", eventPermissions);
		model.addAttribute("listEventMeeting", listRftMeetObj);
		// model.addAttribute("accepted", ((eventSupplier != null &&
		// eventSupplier.getSupplierEventReadTime() != null &&
		// ((eventType != RfxTypes.RFA ? event.getEventStart().before(new
		// Date()) :
		// event.getEventPublishDate().before(new Date())))) ? true : false));
		model.addAttribute("event", event);
		model.addAttribute("eventMeeting", true);
		model.addAttribute("statusList", MeetingStatus.values());
		return "viewSupplierMeeting";
	}

	@RequestMapping(path = { "/listMeetingStatus/{eventType}/{eventId}/{meetStatus}", "/listMeetingStatus/{eventType}/{eventId}" }, method = RequestMethod.POST)
	public ResponseEntity<List<?>> listMeetingStatus(@PathVariable("eventType") RfxTypes eventType, @PathVariable("eventId") String eventId, @PathVariable("meetStatus") Optional<MeetingStatus> meetStatus, Model model) {
		HttpHeaders headers = new HttpHeaders();
		List<?> listMeetObj = null;
		try {
			MeetingStatus status = null;
			if (meetStatus.isPresent()) {
				status = meetStatus.get();
			}
			LOG.info("TYPE : " + eventType + " Event Id : " + eventId + " Meeting Status : " + status);
			switch (eventType) {
			case RFA:
				listMeetObj = rfaMeetingService.loadSupplierMeetingByEventIdAndMeetingStatus(eventId, status, SecurityLibrary.getLoggedInUserTenantId());
				break;
			case RFI:
				listMeetObj = rfiMeetingService.loadSupplierMeetingByEventIdAndMeetingStatus(eventId, status, SecurityLibrary.getLoggedInUserTenantId());
				break;
			case RFP:
				listMeetObj = rfpMeetingService.loadSupplierMeetingByEventIdAndMeetingStatus(eventId, status, SecurityLibrary.getLoggedInUserTenantId());
				break;
			case RFQ:
				listMeetObj = rfqMeetingService.loadSupplierMeetingByEventIdAndMeetingStatus(eventId, status, SecurityLibrary.getLoggedInUserTenantId());
				break;
			case RFT:
				listMeetObj = rftMeetingService.loadSupplierMeetingByEventIdAndMeetingStatus(eventId, status, SecurityLibrary.getLoggedInUserTenantId());
				break;
			default:
				break;
			}

			model.addAttribute("listEventMeeting", listMeetObj);
			model.addAttribute("statusList", MeetingStatus.values());
			return new ResponseEntity<List<?>>(listMeetObj, headers, HttpStatus.OK);

		} catch (Exception e) {
			headers.add("error", messageSource.getMessage("supplierMeeting.error.list", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<List<?>>(null, headers, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(path = "/acceptMeeting/{eventType}", method = RequestMethod.POST)
	public ResponseEntity<SupplierMeetingAttendance> saveMeetingAttendance(@PathVariable("eventType") RfxTypes eventType, @RequestParam(name = "name") String name, @RequestParam(name = "designation") String designation, @RequestParam(name = "mobileNumber") String mobileNumber, @RequestParam(name = "remarks") String remarks, @RequestParam(name = "eventId") String eventId, @RequestParam(name = "meetingId") String meetingId) {
		LOG.info("save saveMeetingAttendance ");
		HttpHeaders headers = new HttpHeaders();
		Event event = null;
		SupplierMeetingAttendance attendance = null;
		try {
			if (StringUtils.checkString(name).length() == 0) {
				headers.add("error", messageSource.getMessage("suppliermeeting.error.name.empty", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<SupplierMeetingAttendance>(null, headers, HttpStatus.BAD_REQUEST);
			}
			switch (eventType) {
			case RFA:
				attendance = rfaEventService.loadSupplierMeetingAttendenceByEventId(meetingId, eventId, SecurityLibrary.getLoggedInUserTenantId());
				event = rfaEventService.getRfaEventByeventId(eventId);
				if (attendance == null) {
					attendance = new RfaSupplierMeetingAttendance();
				}
				buildAttandance(name, designation, mobileNumber, remarks, attendance, MeetingAttendanceStatus.Accepted, null);
				((RfaSupplierMeetingAttendance) attendance).setRfaEvent((RfaEvent) event);
				((RfaSupplierMeetingAttendance) attendance).setRfaEventMeeting(rfaMeetingService.getRfaMeetingById(meetingId));
				supplierRfaMeetingAttendanceService.saveMeetingAttendance(((RfaSupplierMeetingAttendance) attendance));
				break;
			case RFI:
				attendance = rfiEventService.loadSupplierMeetingAttendenceByEventId(meetingId, eventId, SecurityLibrary.getLoggedInUserTenantId());
				event = rfiEventService.getRfiEventById(eventId);
				if (attendance == null) {
					attendance = new RfiSupplierMeetingAttendance();
				}
				buildAttandance(name, designation, mobileNumber, remarks, attendance, MeetingAttendanceStatus.Accepted, null);
				((RfiSupplierMeetingAttendance) attendance).setRfiEvent((RfiEvent) event);
				((RfiSupplierMeetingAttendance) attendance).setRfxEventMeeting(rfiMeetingService.getRfiMeetingById(meetingId));
				supplierRfiMeetingAttendanceService.saveMeetingAttendance(((RfiSupplierMeetingAttendance) attendance));
				break;
			case RFP:
				attendance = rfpEventService.loadSupplierMeetingAttendenceByEventId(meetingId, eventId, SecurityLibrary.getLoggedInUserTenantId());
				event = rfpEventService.getEventById(eventId);
				if (attendance == null) {
					attendance = new RfpSupplierMeetingAttendance();
				}
				buildAttandance(name, designation, mobileNumber, remarks, attendance, MeetingAttendanceStatus.Accepted, null);
				((RfpSupplierMeetingAttendance) attendance).setRfxEvent((RfpEvent) event);
				((RfpSupplierMeetingAttendance) attendance).setRfxEventMeeting(rfpMeetingService.getRfpMeetingById(meetingId));
				supplierRfpMeetingAttendanceService.saveMeetingAttendance(((RfpSupplierMeetingAttendance) attendance));
				break;
			case RFQ:
				attendance = rfqEventService.loadSupplierMeetingAttendenceByEventId(meetingId, eventId, SecurityLibrary.getLoggedInUserTenantId());
				event = rfqEventService.getEventById(eventId);
				if (attendance == null) {
					attendance = new RfqSupplierMeetingAttendance();
				}
				buildAttandance(name, designation, mobileNumber, remarks, attendance, MeetingAttendanceStatus.Accepted, null);
				((RfqSupplierMeetingAttendance) attendance).setRfxEvent((RfqEvent) event);
				((RfqSupplierMeetingAttendance) attendance).setRfxEventMeeting(rfqMeetingService.getMeetingById(meetingId));
				supplierRfqMeetingAttendanceService.saveMeetingAttendance(((RfqSupplierMeetingAttendance) attendance));

				break;
			case RFT:
				attendance = rftEventService.loadSupplierMeetingAttendenceByEventId(meetingId, eventId, SecurityLibrary.getLoggedInUserTenantId());
				event = rftEventService.getRftEventById(eventId);
				if (attendance == null) {
					attendance = new RftSupplierMeetingAttendance();
				}
				buildAttandance(name, designation, mobileNumber, remarks, attendance, MeetingAttendanceStatus.Accepted, null);
				((RftSupplierMeetingAttendance) attendance).setRftEvent((RftEvent) event);
				((RftSupplierMeetingAttendance) attendance).setRfxEventMeeting(rftMeetingService.getRftMeetingById(meetingId));
				supplierRftEventMeetingAttendanceService.saveMeetingAttendance(((RftSupplierMeetingAttendance) attendance));
				break;
			default:
				break;
			}

			headers.add("success", messageSource.getMessage("supplierMeeting.save.success", new Object[] {}, Global.LOCALE));
			return new ResponseEntity<SupplierMeetingAttendance>(attendance, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error while saving MeetingAttendance : " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("suppliermeeting.error.save", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<SupplierMeetingAttendance>(null, headers, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * @param name
	 * @param designation
	 * @param mobileNumber
	 * @param remarks
	 * @param attendance
	 */
	private void buildAttandance(String name, String designation, String mobileNumber, String remarks, SupplierMeetingAttendance attendance, MeetingAttendanceStatus status, String rejectReason) {
		attendance.setName(name);
		attendance.setMobileNumber(mobileNumber);
		attendance.setDesignation(designation);
		attendance.setRemarks(remarks);
		attendance.setMeetingAttendanceStatus(status);
		if (status == MeetingAttendanceStatus.Accepted) {
			attendance.setAttended(Boolean.TRUE);
		} else if (status == MeetingAttendanceStatus.Rejected) {
			attendance.setAttended(Boolean.FALSE);
		} else if (status == MeetingAttendanceStatus.Undecided) {
			attendance.setAttended(Boolean.FALSE);
		}
		attendance.setActionDate(new Date());
		attendance.setSupplier(SecurityLibrary.getLoggedInUser().getSupplier());
		if (rejectReason != null) {
			attendance.setRejectReason(rejectReason);
		}
	}

	@RequestMapping(path = "/rejectMeeting/{eventType}", method = RequestMethod.POST)
	public ResponseEntity<SupplierMeetingAttendance> rejectMeetingAttendance(@PathVariable RfxTypes eventType, @RequestParam(name = "name") String name, @RequestParam(name = "rejectReason") String rejectReason, @RequestParam(name = "eventId") String eventId, @RequestParam(name = "meetingId") String meetingId) {
		LOG.info(" rejectMeetingAttendance " + eventId);
		HttpHeaders headers = new HttpHeaders();
		SupplierMeetingAttendance attendance = null;
		Event event = null;
		try {
			if (StringUtils.checkString(name).length() == 0) {
				headers.add("error", messageSource.getMessage("suppliermeeting.error.name.empty", new Object[] {}, Global.LOCALE));
				return new ResponseEntity<SupplierMeetingAttendance>(null, headers, HttpStatus.BAD_REQUEST);
			}
			switch (eventType) {
			case RFA:
				attendance = rfaEventService.loadSupplierMeetingAttendenceByEventId(meetingId, eventId, SecurityLibrary.getLoggedInUserTenantId());
				event = rfaEventService.getRfaEventByeventId(eventId);
				if (attendance == null) {
					attendance = new RfaSupplierMeetingAttendance();
					attendance.setSupplier(SecurityLibrary.getLoggedInUser().getSupplier());
				}
				buildAttandance(name, null, null, null, attendance, MeetingAttendanceStatus.Rejected, rejectReason);
				((RfaSupplierMeetingAttendance) attendance).setRfaEvent((RfaEvent) event);
				((RfaSupplierMeetingAttendance) attendance).setRfaEventMeeting(rfaMeetingService.getRfaMeetingById(meetingId));
				supplierRfaMeetingAttendanceService.saveMeetingAttendance(((RfaSupplierMeetingAttendance) attendance));
				break;
			case RFI:
				attendance = rfiEventService.loadSupplierMeetingAttendenceByEventId(meetingId, eventId, SecurityLibrary.getLoggedInUserTenantId());
				event = rfiEventService.getRfiEventById(eventId);
				if (attendance == null) {
					attendance = new RfiSupplierMeetingAttendance();
					attendance.setSupplier(SecurityLibrary.getLoggedInUser().getSupplier());
				}
				buildAttandance(name, null, null, null, attendance, MeetingAttendanceStatus.Rejected, rejectReason);
				((RfiSupplierMeetingAttendance) attendance).setRfiEvent((RfiEvent) event);
				((RfiSupplierMeetingAttendance) attendance).setRfxEventMeeting(rfiMeetingService.getRfiMeetingById(meetingId));
				supplierRfiMeetingAttendanceService.saveMeetingAttendance(((RfiSupplierMeetingAttendance) attendance));
				break;
			case RFP:
				attendance = rfpEventService.loadSupplierMeetingAttendenceByEventId(meetingId, eventId, SecurityLibrary.getLoggedInUserTenantId());
				event = rfpEventService.getEventById(eventId);
				if (attendance == null) {
					attendance = new RfpSupplierMeetingAttendance();
					attendance.setSupplier(SecurityLibrary.getLoggedInUser().getSupplier());
				}
				buildAttandance(name, null, null, null, attendance, MeetingAttendanceStatus.Rejected, rejectReason);
				((RfpSupplierMeetingAttendance) attendance).setRfxEvent((RfpEvent) event);
				((RfpSupplierMeetingAttendance) attendance).setRfxEventMeeting(rfpMeetingService.getRfpMeetingById(meetingId));
				supplierRfpMeetingAttendanceService.saveMeetingAttendance(((RfpSupplierMeetingAttendance) attendance));
				break;
			case RFQ:
				attendance = rfqEventService.loadSupplierMeetingAttendenceByEventId(meetingId, eventId, SecurityLibrary.getLoggedInUserTenantId());
				event = rfqEventService.getEventById(eventId);
				if (attendance == null) {
					attendance = new RfqSupplierMeetingAttendance();
					attendance.setSupplier(SecurityLibrary.getLoggedInUser().getSupplier());
				}
				buildAttandance(name, null, null, null, attendance, MeetingAttendanceStatus.Rejected, rejectReason);
				((RfqSupplierMeetingAttendance) attendance).setRfxEvent((RfqEvent) event);
				((RfqSupplierMeetingAttendance) attendance).setRfxEventMeeting(rfqMeetingService.getMeetingById(meetingId));
				supplierRfqMeetingAttendanceService.saveMeetingAttendance(((RfqSupplierMeetingAttendance) attendance));
				break;
			case RFT:
				attendance = rftEventService.loadSupplierMeetingAttendenceByEventId(meetingId, eventId, SecurityLibrary.getLoggedInUserTenantId());
				event = rftEventService.getRftEventById(eventId);
				if (attendance == null) {
					attendance = new RftSupplierMeetingAttendance();
					attendance.setSupplier(SecurityLibrary.getLoggedInUser().getSupplier());
				}
				buildAttandance(name, null, null, null, attendance, MeetingAttendanceStatus.Rejected, rejectReason);
				((RftSupplierMeetingAttendance) attendance).setRftEvent((RftEvent) event);
				((RftSupplierMeetingAttendance) attendance).setRfxEventMeeting(rftMeetingService.getRftMeetingById(meetingId));
				supplierRftEventMeetingAttendanceService.saveMeetingAttendance(((RftSupplierMeetingAttendance) attendance));
				break;
			default:
				break;
			}

			headers.add("success", messageSource.getMessage("supplierMeeting.reject.success", new Object[] {}, Global.LOCALE));
			return new ResponseEntity<SupplierMeetingAttendance>(attendance, HttpStatus.OK);

		} catch (Exception e) {
			LOG.error("Error while rejectMeetingAttendance : " + e.getMessage(), e);

			headers.add("error", "Error while rejectMeetingAttendance " + e.getMessage());
			headers.add("error", messageSource.getMessage("supplierMeeting.error.reject", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<SupplierMeetingAttendance>(null, headers, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(path = "viewBqList/{eventType}/{eventId}", method = RequestMethod.GET)
	public String viewSupplierBillOfQuantity(@PathVariable("eventType") RfxTypes eventType, @PathVariable("eventId") String eventId, Model model) {
		LOG.info(" viewSupplierBillOfQuantity called");
		EventPojo event = null;
		List<Bq> bqList = null;
		EventSupplier eventSupplier = null;
		EventPermissions eventPermissions = null;
		switch (eventType) {
		case RFP:
			List<RfpSupplierBq> supplierBqList = new ArrayList<RfpSupplierBq>();
			eventSupplier = rfpEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
			if (eventSupplier == null || (eventSupplier != null && (eventSupplier.getSupplierEventReadTime() == null || eventSupplier.getRejectedTime() == null) && (eventSupplier.getRejectedTime() != null && eventSupplier.getIsRejectedAfterStart() == Boolean.FALSE))) {
				return "redirect:/400_error";
			}
			event = rfpEventService.loadEventPojoForSummeryDetailPageForSupplierById(eventId);
			bqList = rfpBqService.findBqbyEventId(eventId);
			eventPermissions = rfpEventSupplierService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), eventId);
			eventSupplier = rfpEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);

			for (Bq bq : bqList) {
				RfpSupplierBq supplierBq = rfpSupplierBqService.findRfpSupplierBqStatusbyEventIdAndSupplierId(SecurityLibrary.getLoggedInUserTenantId(), eventId, bq.getId());
				LOG.info(" RFP called id " + bq.getId());
				supplierBqList.add(supplierBq);
			}

			LOG.info(" RFP called" + supplierBqList.size());
			model.addAttribute("supplierBqList", supplierBqList);
			break;
		case RFA:
			List<RfaSupplierBq> supplierBqList1 = new ArrayList<RfaSupplierBq>();
			eventSupplier = rfaEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
			if (eventSupplier == null || (eventSupplier != null && (eventSupplier.getSupplierEventReadTime() == null || eventSupplier.getRejectedTime() == null) && (eventSupplier.getRejectedTime() != null && eventSupplier.getIsRejectedAfterStart() == Boolean.FALSE))) {
				return "redirect:/400_error";
			}
			event = rfaEventService.loadEventPojoForSummeryDetailPageForSupplierById(eventId);
			AuctionRules auctionRules = rfaEventService.getAuctionRulesByEventId(eventId);
			model.addAttribute("auctionRules", auctionRules);
			bqList = rfaBqService.findRfaBqbyEventId(eventId);
			eventPermissions = rfaEventSupplierService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), eventId);
			eventSupplier = rfaEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
			model.addAttribute("accepted", ((eventSupplier != null && eventSupplier.getSupplierEventReadTime() != null && (event.getEventStart().before(new Date()))) ? true : false));

			for (Bq bq : bqList) {
				RfaSupplierBq supplierBq = rfaSupplierBqService.findRfaSupplierBqStatusbyEventIdAndSupplierId(SecurityLibrary.getLoggedInUserTenantId(), eventId, bq.getId());
				supplierBqList1.add(supplierBq);
			}
			model.addAttribute("supplierBqList", supplierBqList1);
			break;
		case RFI:
			eventSupplier = rfiEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
			if (eventSupplier == null || (eventSupplier != null && (eventSupplier.getSupplierEventReadTime() == null || eventSupplier.getRejectedTime() == null) && (eventSupplier.getRejectedTime() != null && eventSupplier.getIsRejectedAfterStart() == Boolean.FALSE))) {
				return "redirect:/400_error";
			}
			event = rfiEventService.loadEventPojoForSummeryDetailPageForSupplierById(eventId);
			eventPermissions = rfiEventSupplierService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), eventId);
			eventSupplier = rfiEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
			model.addAttribute("accepted", ((eventSupplier != null && eventSupplier.getSupplierEventReadTime() != null && (event.getEventStart().before(new Date()))) ? true : false));
			break;
		case RFQ:
			List<RfqSupplierBq> supplierBqList11 = new ArrayList<RfqSupplierBq>();
			eventSupplier = rfqEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
			if (eventSupplier == null || (eventSupplier != null && (eventSupplier.getSupplierEventReadTime() == null || eventSupplier.getRejectedTime() == null) && (eventSupplier.getRejectedTime() != null && eventSupplier.getIsRejectedAfterStart() == Boolean.FALSE))) {
				return "redirect:/400_error";
			}
			event = rfqEventService.loadEventPojoForSummeryDetailPageForSupplierById(eventId);
			bqList = rfqBqService.findBqbyEventId(eventId);
			eventPermissions = rfqEventSupplierService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), eventId);
			eventSupplier = rfqEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);

			for (Bq bq : bqList) {
				RfqSupplierBq supplierBq = rfqSupplierBqService.findRfqSupplierBqStatusbyEventIdAndSupplierId(SecurityLibrary.getLoggedInUserTenantId(), eventId, bq.getId());
				supplierBqList11.add(supplierBq);
			}

			model.addAttribute("supplierBqList", supplierBqList11);
			break;
		case RFT:
			List<RftSupplierBq> rftBqList = new ArrayList<RftSupplierBq>();
			eventSupplier = rftEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
			if (eventSupplier == null || (eventSupplier != null && (eventSupplier.getSupplierEventReadTime() == null || eventSupplier.getRejectedTime() == null) && (eventSupplier.getRejectedTime() != null && eventSupplier.getIsRejectedAfterStart() == Boolean.FALSE))) {
				return "redirect:/400_error";
			}
			event = rftEventService.loadEventPojoForSummeryPageForSupplierById(eventId);
			bqList = rftBqService.findRftBqbyEventId(eventId);
			eventPermissions = rftEventSupplierService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), eventId);
			eventSupplier = rftEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);

			for (Bq bq : bqList) {
				RftSupplierBq supplierBq = rftSupplierBqService.findRftSupplierBqStatusbyEventIdAndSupplierId(SecurityLibrary.getLoggedInUserTenantId(), eventId, bq.getId());
				rftBqList.add(supplierBq);
			}

			model.addAttribute("supplierBqList", rftBqList);
			break;
		default:
			break;
		}
		model.addAttribute("eventSupplier", eventSupplier);
		model.addAttribute("eventPermissions", eventPermissions);
		model.addAttribute("event", event);
		model.addAttribute("bqList", bqList);
		model.addAttribute("eventBq", true); // Fixed to highlight Tab - @Nitin
		model.addAttribute("supplier", supplierService.findSupplierSubscriptionDetailsBySupplierId(SecurityLibrary.getLoggedInUserTenantId()));
		return "viewBqList";
	}


	@RequestMapping(path = "viewSorList/{eventType}/{eventId}", method = RequestMethod.GET)
	public String viewSupplierScheduleOfRate(@PathVariable("eventType") RfxTypes eventType, @PathVariable("eventId") String eventId, Model model) {
		LOG.info(" viewSupplierScheduleOfRate called");
		EventPojo event = null;
		List<Sor> bqList = null;
		EventSupplier eventSupplier = null;
		EventPermissions eventPermissions = null;
		switch (eventType) {
			case RFP:
				List<RfpSupplierSor> supplierSorList11P = new ArrayList<RfpSupplierSor>();
				eventSupplier = rfpEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);

				if (eventSupplier == null || (eventSupplier != null && (eventSupplier.getSupplierEventReadTime() == null || eventSupplier.getRejectedTime() == null) && (eventSupplier.getRejectedTime() != null && eventSupplier.getIsRejectedAfterStart() == Boolean.FALSE))) {
					return "redirect:/400_error";
				}
				event = rfpEventService.loadEventPojoForSummeryDetailPageForSupplierById(eventId);
				bqList = rfpSorService.findSorbyEventId(eventId);
				eventPermissions = rfpEventSupplierService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), eventId);

				for (Sor bq : bqList) {
					RfpSupplierSor supplierBq = rfpSupplierSorService.findRfpSupplierSorStatusbyEventIdAndSupplierId(SecurityLibrary.getLoggedInUserTenantId(), eventId, bq.getId());
					supplierSorList11P.add(supplierBq);
				}

				model.addAttribute("supplierBqList", supplierSorList11P);
				break;
			case RFA:
				List<RfaSupplierSor> supplierSorList11A = new ArrayList<RfaSupplierSor>();
				eventSupplier = rfaEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);

				if (eventSupplier == null || (eventSupplier != null && (eventSupplier.getSupplierEventReadTime() == null || eventSupplier.getRejectedTime() == null) && (eventSupplier.getRejectedTime() != null && eventSupplier.getIsRejectedAfterStart() == Boolean.FALSE))) {
					return "redirect:/400_error";
				}
				event = rfaEventService.loadEventPojoForSummeryDetailPageForSupplierById(eventId);
				bqList = rfaSorService.findSorbyEventId(eventId);
				eventPermissions = rfaEventSupplierService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), eventId);

				for (Sor bq : bqList) {
					RfaSupplierSor supplierBq = rfaSupplierSorService.findRfaSupplierSorStatusbyEventIdAndSupplierId(SecurityLibrary.getLoggedInUserTenantId(), eventId, bq.getId());
					supplierSorList11A.add(supplierBq);
				}

				model.addAttribute("supplierBqList", supplierSorList11A);
				break;
			case RFI:
				List<RfiSupplierSor> supplierBqList111 = new ArrayList<RfiSupplierSor>();
				eventSupplier = rfiEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
				if (eventSupplier == null || (eventSupplier != null && (eventSupplier.getSupplierEventReadTime() == null || eventSupplier.getRejectedTime() == null) && (eventSupplier.getRejectedTime() != null && eventSupplier.getIsRejectedAfterStart() == Boolean.FALSE))) {
					return "redirect:/400_error";
				}
				event = rfiEventService.loadEventPojoForSummeryDetailPageForSupplierById(eventId);
				bqList = rfiSorService.findSorbyEventId(eventId);
				eventPermissions = rfiEventSupplierService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), eventId);
				eventSupplier = rfiEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
				for (Sor bq : bqList) {
					RfiSupplierSor supplierBq = rfiSupplierSorService.findRfiSupplierSorStatusbyEventIdAndSupplierId(SecurityLibrary.getLoggedInUserTenantId(), eventId, bq.getId());
					supplierBqList111.add(supplierBq);
				}
				model.addAttribute("supplierBqList", supplierBqList111);
				break;
			case RFQ:
				List<RfqSupplierSor> supplierBqList11 = new ArrayList<RfqSupplierSor>();
				eventSupplier = rfqEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);

				if (eventSupplier == null || (eventSupplier != null && (eventSupplier.getSupplierEventReadTime() == null || eventSupplier.getRejectedTime() == null) && (eventSupplier.getRejectedTime() != null && eventSupplier.getIsRejectedAfterStart() == Boolean.FALSE))) {
					return "redirect:/400_error";
				}
				event = rfqEventService.loadEventPojoForSummeryDetailPageForSupplierById(eventId);
				bqList = rfqSorService.findSorbyEventId(eventId);
				eventPermissions = rfqEventSupplierService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), eventId);

				for (Sor bq : bqList) {
					RfqSupplierSor supplierBq = rfqSupplierSorService.findRfqSupplierSorStatusbyEventIdAndSupplierId(SecurityLibrary.getLoggedInUserTenantId(), eventId, bq.getId());
					supplierBqList11.add(supplierBq);
				}

				model.addAttribute("supplierBqList", supplierBqList11);
				break;
			case RFT:
				List<RftSupplierSor> rftBqList = new ArrayList<RftSupplierSor>();
				eventSupplier = rftEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
				if (eventSupplier == null || (eventSupplier != null && (eventSupplier.getSupplierEventReadTime() == null || eventSupplier.getRejectedTime() == null) && (eventSupplier.getRejectedTime() != null && eventSupplier.getIsRejectedAfterStart() == Boolean.FALSE))) {
					return "redirect:/400_error";
				}
				event = rftEventService.loadEventPojoForSummeryPageForSupplierById(eventId);
				bqList = rftSorService.findSorbyEventId(eventId);
				eventPermissions = rftEventSupplierService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), eventId);
				eventSupplier = rftEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);

				for (Sor sor : bqList) {
					RftSupplierSor supplierBq = rftSupplierSorService.findRftSupplierSorStatusbyEventIdAndSupplierId(SecurityLibrary.getLoggedInUserTenantId(), eventId, sor.getId());
					rftBqList.add(supplierBq);
				}

				model.addAttribute("supplierBqList", rftBqList);
				break;
			default:
				break;
		}
		model.addAttribute("eventSupplier", eventSupplier);
		model.addAttribute("eventPermissions", eventPermissions);
		model.addAttribute("event", event);
		model.addAttribute("bqList", bqList);
		model.addAttribute("eventSor", true);
		model.addAttribute("supplier", supplierService.findSupplierSubscriptionDetailsBySupplierId(SecurityLibrary.getLoggedInUserTenantId()));
		return "viewSorList";
	}


	@RequestMapping(path = "/viewSorDetails/{eventType}/{bqId}", method = RequestMethod.POST)
	public ResponseEntity<SupplierSorItemResponsePojo> viewSorDetails(@PathVariable RfxTypes eventType, @PathVariable("bqId") String bqId) {
		HttpHeaders header = new HttpHeaders();
		List<?> supplierBqItem = null;
		long totalBqItemCount = 1;
		// sending level order list and bq Item list bind in new Pojo class
		SupplierSorItemResponsePojo sorItemResponsePojo = new SupplierSorItemResponsePojo();

		try {
			LOG.info("ViewSupplierBillOfQuantity called " + bqId);
			Integer pageLength = 50;
			pageLength = SecurityLibrary.getLoggedInUser().getBqPageLength();
			Integer pageNo = 1;
			List<SorItemPojo> leveLOrderList = null;
			switch (eventType) {
				case RFP:
					leveLOrderList = rfpSupplierSorItemService.getAllLevelOrderSorItemBySorId(bqId, SecurityLibrary.getLoggedInUserTenantId(), null);
					sorItemResponsePojo.setLevelOrderList(leveLOrderList);

					supplierBqItem = rfpSupplierSorItemService.getSorItemForSearchFilterForSupplier(bqId, SecurityLibrary.getLoggedInUserTenantId(), null, pageNo, pageLength, null, null);
					if (CollectionUtil.isNotEmpty(supplierBqItem)) {
						sorItemResponsePojo.setSupplierSorItemList(supplierBqItem);
					}

					if (CollectionUtil.isEmpty(supplierBqItem)) {
						rfpSupplierSorItemService.saveSupplierEventSor(bqId);
						supplierBqItem = rfpSupplierSorItemService.getSorItemForSearchFilterForSupplier(bqId, SecurityLibrary.getLoggedInUserTenantId(), null, pageNo, pageLength, null, null);
						sorItemResponsePojo.setSupplierSorItemList(supplierBqItem);
					}

					totalBqItemCount = rfpSupplierSorItemService.totalSorItemCountBySorId(bqId, SecurityLibrary.getLoggedInUserTenantId(), null);
					sorItemResponsePojo.setTotalBqItemCount(totalBqItemCount);
					LOG.info("RFQ totalBqItemCount :" + totalBqItemCount);
					break;
				case RFA:
					leveLOrderList = rfaSupplierSorItemService.getAllLevelOrderSorItemBySorId(bqId, SecurityLibrary.getLoggedInUserTenantId(), null);
					sorItemResponsePojo.setLevelOrderList(leveLOrderList);
					supplierBqItem = rfaSupplierSorItemService.getSorItemForSearchFilterForSupplier(bqId, SecurityLibrary.getLoggedInUserTenantId(), null, pageNo, pageLength, null, null);
					if (CollectionUtil.isNotEmpty(supplierBqItem)) {
						sorItemResponsePojo.setSupplierSorItemList(supplierBqItem);
					}
					if (CollectionUtil.isEmpty(supplierBqItem)) {
						supplierBqItem = rfaSupplierSorItemService.saveSupplierEventSor(bqId);
						supplierBqItem = rfaSupplierSorItemService.getSorItemForSearchFilterForSupplier(bqId, SecurityLibrary.getLoggedInUserTenantId(), null, pageNo, pageLength, null, null);
						sorItemResponsePojo.setSupplierSorItemList(supplierBqItem);
					}
					totalBqItemCount = rfaSupplierSorItemService.totalSorItemCountBySorId(bqId, SecurityLibrary.getLoggedInUserTenantId(), null);
					sorItemResponsePojo.setTotalBqItemCount(totalBqItemCount);
					LOG.info("RFA totalBqItemCount :" + totalBqItemCount);
					break;
				case RFI:
					leveLOrderList = rfiSupplierSorItemService.getAllLevelOrderSorItemBySorId(bqId, SecurityLibrary.getLoggedInUserTenantId(), null);
					sorItemResponsePojo.setLevelOrderList(leveLOrderList);
					supplierBqItem = rfiSupplierSorItemService.getSorItemForSearchFilterForSupplier(bqId, SecurityLibrary.getLoggedInUserTenantId(), null, pageNo, pageLength, null, null);
					if (CollectionUtil.isNotEmpty(supplierBqItem)) {
						sorItemResponsePojo.setSupplierSorItemList(supplierBqItem);
					}
					if (CollectionUtil.isEmpty(supplierBqItem)) {
						supplierBqItem = rfiSupplierSorItemService.saveSupplierEventSor(bqId);
						supplierBqItem = rfiSupplierSorItemService.getSorItemForSearchFilterForSupplier(bqId, SecurityLibrary.getLoggedInUserTenantId(), null, pageNo, pageLength, null, null);
						sorItemResponsePojo.setSupplierSorItemList(supplierBqItem);
					}
					totalBqItemCount = rfiSupplierSorItemService.totalSorItemCountBySorId(bqId, SecurityLibrary.getLoggedInUserTenantId(), null);
					sorItemResponsePojo.setTotalBqItemCount(totalBqItemCount);
					LOG.info("RFI totalBqItemCount :" + totalBqItemCount);
					break;
				case RFQ:
					leveLOrderList = rfqSupplierSorItemService.getAllLevelOrderSorItemBySorId(bqId, SecurityLibrary.getLoggedInUserTenantId(), null);
					sorItemResponsePojo.setLevelOrderList(leveLOrderList);

					supplierBqItem = rfqSupplierSorItemService.getSorItemForSearchFilterForSupplier(bqId, SecurityLibrary.getLoggedInUserTenantId(), null, pageNo, pageLength, null, null);
					if (CollectionUtil.isNotEmpty(supplierBqItem)) {
						sorItemResponsePojo.setSupplierSorItemList(supplierBqItem);
					}

					if (CollectionUtil.isEmpty(supplierBqItem)) {
						rfqSupplierSorItemService.saveSupplierEventSor(bqId);
						supplierBqItem = rfqSupplierSorItemService.getSorItemForSearchFilterForSupplier(bqId, SecurityLibrary.getLoggedInUserTenantId(), null, pageNo, pageLength, null, null);
						sorItemResponsePojo.setSupplierSorItemList(supplierBqItem);
					}

					totalBqItemCount = rfqSupplierSorItemService.totalSorItemCountBySorId(bqId, SecurityLibrary.getLoggedInUserTenantId(), null);
					sorItemResponsePojo.setTotalBqItemCount(totalBqItemCount);
					LOG.info("RFQ totalBqItemCount :" + totalBqItemCount);
					break;
				case RFT:
					leveLOrderList = rftSupplierSorItemService.getAllLevelOrderSorItemBySorId(bqId, SecurityLibrary.getLoggedInUserTenantId(), null);
					sorItemResponsePojo.setLevelOrderList(leveLOrderList);

					supplierBqItem = rftSupplierSorItemService.getSorItemForSearchFilterForSupplier(bqId, SecurityLibrary.getLoggedInUserTenantId(), null, pageNo, pageLength, null, null);
					if (CollectionUtil.isNotEmpty(supplierBqItem)) {
						sorItemResponsePojo.setSupplierSorItemList(supplierBqItem);
					}

					if (CollectionUtil.isEmpty(supplierBqItem)) {
						rftSupplierSorItemService.saveSupplierEventSor(bqId);
						supplierBqItem = rftSupplierSorItemService.getSorItemForSearchFilterForSupplier(bqId, SecurityLibrary.getLoggedInUserTenantId(), null, pageNo, pageLength, null, null);
						sorItemResponsePojo.setSupplierSorItemList(supplierBqItem);
					}

					totalBqItemCount = rftSupplierSorItemService.totalSorItemCountBySorId(bqId, SecurityLibrary.getLoggedInUserTenantId(), null);
					sorItemResponsePojo.setTotalBqItemCount(totalBqItemCount);
					LOG.info("RFT totalBqItemCount :" + totalBqItemCount);
					break;
				default:
					break;
			}

		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
			header.add("error", "Error loading BQ : " + e.getMessage());
			return new ResponseEntity<SupplierSorItemResponsePojo>(sorItemResponsePojo, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<SupplierSorItemResponsePojo>(sorItemResponsePojo, HttpStatus.OK);
	}

	@RequestMapping(path = "/viewBqDetails/{eventType}/{bqId}", method = RequestMethod.POST)
	public ResponseEntity<SupplierBqItemResponsePojo> viewBqDetails(@PathVariable RfxTypes eventType, @PathVariable("bqId") String bqId) {
		HttpHeaders header = new HttpHeaders();
		List<?> supplierBqItem = null;
		long totalBqItemCount = 1;
		// sending level order list and bq Item list bind in new Pojo class
		SupplierBqItemResponsePojo bqItemResponsePojo = new SupplierBqItemResponsePojo();

		try {
			LOG.info("ViewSupplierBillOfQuantity called " + bqId);
			Integer pageLength = 50;
			pageLength = SecurityLibrary.getLoggedInUser().getBqPageLength();
			Integer pageNo = 1;
			List<BqItemPojo> leveLOrderList = null;
			switch (eventType) {
			case RFP:
				leveLOrderList = rfpSupplierBqItemService.getAllLevelOrderBqItemByBqId(bqId, SecurityLibrary.getLoggedInUserTenantId(), null);
				bqItemResponsePojo.setLevelOrderList(leveLOrderList);
				supplierBqItem = rfpSupplierBqItemService.getBqItemForSearchFilterForSupplier(bqId, SecurityLibrary.getLoggedInUserTenantId(), null, pageNo, pageLength, null, null);
				if (CollectionUtil.isNotEmpty(supplierBqItem)) {
					bqItemResponsePojo.setSupplierBqItemList(supplierBqItem);
				}
				// rfpSupplierBqItemService.getAllSupplierBqItemByBqIdAndSupplierId(bqId,
				// SecurityLibrary.getLoggedInUserTenantId());
				if (CollectionUtil.isEmpty(supplierBqItem)) {
					LOG.info(" RFP vsupplierBqItem");
					supplierBqItem = rfpSupplierBqItemService.saveSupplierEventBq(bqId);
				}
				supplierBqItem = rfpSupplierBqItemService.getBqItemForSearchFilterForSupplier(bqId, SecurityLibrary.getLoggedInUserTenantId(), null, pageNo, pageLength, null, null);

				bqItemResponsePojo.setSupplierBqItemList(supplierBqItem);

				// supplierBqItem =
				// rfpSupplierBqItemService.getAllSupplierBqItemByBqIdAndSupplierId(bqId,
				// SecurityLibrary.getLoggedInUserTenantId());
				totalBqItemCount = rfpSupplierBqItemService.totalBqItemCountByBqId(bqId, SecurityLibrary.getLoggedInUserTenantId(), null);
				bqItemResponsePojo.setTotalBqItemCount(totalBqItemCount);
				LOG.info("RFP totalBqItemCount :" + totalBqItemCount);
				break;
			case RFA:
				leveLOrderList = rfaSupplierBqItemService.getAllLevelOrderBqItemByBqId(bqId, SecurityLibrary.getLoggedInUserTenantId(), null);
				bqItemResponsePojo.setLevelOrderList(leveLOrderList);
				supplierBqItem = rfaSupplierBqItemService.getBqItemForSearchFilterForSupplier(bqId, SecurityLibrary.getLoggedInUserTenantId(), null, pageNo, pageLength, null, null);
				if (CollectionUtil.isNotEmpty(supplierBqItem)) {
					bqItemResponsePojo.setSupplierBqItemList(supplierBqItem);
				}
				// supplierBqItem =
				// rfaSupplierBqItemService.getAllSupplierBqItemByBqIdAndSupplierId(bqId,
				// SecurityLibrary.getLoggedInUserTenantId());
				if (CollectionUtil.isEmpty(supplierBqItem)) {
					supplierBqItem = rfaSupplierBqItemService.saveSupplierEventBq(bqId);
					supplierBqItem = rfaSupplierBqItemService.getBqItemForSearchFilterForSupplier(bqId, SecurityLibrary.getLoggedInUserTenantId(), null, pageNo, pageLength, null, null);
					bqItemResponsePojo.setSupplierBqItemList(supplierBqItem);
					// supplierBqItem =
					// rfaSupplierBqItemService.getAllSupplierBqItemByBqIdAndSupplierId(bqId,
					// SecurityLibrary.getLoggedInUserTenantId());
				}
				totalBqItemCount = rfaSupplierBqItemService.totalBqItemCountByBqId(bqId, SecurityLibrary.getLoggedInUserTenantId(), null);
				bqItemResponsePojo.setTotalBqItemCount(totalBqItemCount);
				LOG.info("RFA totalBqItemCount :" + totalBqItemCount);
				break;
			case RFI:
				break;
			case RFQ:
				// supplierBqItem =
				// rfqSupplierBqItemService.getAllSupplierBqItemByBqIdAndSupplierId(bqId);
				leveLOrderList = rfqSupplierBqItemService.getAllLevelOrderBqItemByBqId(bqId, SecurityLibrary.getLoggedInUserTenantId(), null);
				bqItemResponsePojo.setLevelOrderList(leveLOrderList);
				supplierBqItem = rfqSupplierBqItemService.getBqItemForSearchFilterForSupplier(bqId, SecurityLibrary.getLoggedInUserTenantId(), null, pageNo, pageLength, null, null);
				if (CollectionUtil.isNotEmpty(supplierBqItem)) {
					bqItemResponsePojo.setSupplierBqItemList(supplierBqItem);
				}
				if (CollectionUtil.isEmpty(supplierBqItem)) {
					supplierBqItem = rfqSupplierBqItemService.saveSupplierEventBq(bqId);
					// supplierBqItem =
					// rfqSupplierBqItemService.getAllSupplierBqItemByBqIdAndSupplierId(bqId,
					// SecurityLibrary.getLoggedInUserTenantId());
					supplierBqItem = rfqSupplierBqItemService.getBqItemForSearchFilterForSupplier(bqId, SecurityLibrary.getLoggedInUserTenantId(), null, pageNo, pageLength, null, null);
					bqItemResponsePojo.setSupplierBqItemList(supplierBqItem);
				}
				totalBqItemCount = rfqSupplierBqItemService.totalBqItemCountByBqId(bqId, SecurityLibrary.getLoggedInUserTenantId(), null);
				bqItemResponsePojo.setTotalBqItemCount(totalBqItemCount);
				LOG.info("RFQ totalBqItemCount :" + totalBqItemCount);
				break;
			case RFT:
				leveLOrderList = rftSupplierBqItemService.getAllLevelOrderBqItemByBqId(bqId, SecurityLibrary.getLoggedInUserTenantId(), null);
				bqItemResponsePojo.setLevelOrderList(leveLOrderList);
				supplierBqItem = rftSupplierBqItemService.saveAndGetSupplierEventBq(bqId, SecurityLibrary.getLoggedInUserTenantId());
				// supplierBqItem =
				// rftSupplierBqItemService.getAllSupplierBqItemByBqIdAndSupplierId(bqId,
				// SecurityLibrary.getLoggedInUserTenantId());
				supplierBqItem = rftSupplierBqItemService.getBqItemForSearchFilterForSupplier(bqId, SecurityLibrary.getLoggedInUserTenantId(), null, pageNo, pageLength, null, null);
				bqItemResponsePojo.setSupplierBqItemList(supplierBqItem);
				totalBqItemCount = rftSupplierBqItemService.totalBqItemCountByBqId(bqId, SecurityLibrary.getLoggedInUserTenantId(), null);
				bqItemResponsePojo.setTotalBqItemCount(totalBqItemCount);
				LOG.info("RFT totalBqItemCount :" + totalBqItemCount);
				break;
			default:
				break;
			}

		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
			header.add("error", "Error loading BQ : " + e.getMessage());
			return new ResponseEntity<SupplierBqItemResponsePojo>(bqItemResponsePojo, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<SupplierBqItemResponsePojo>(bqItemResponsePojo, HttpStatus.OK);
	}

	@RequestMapping(path = "/updateSupplierAdditionalTax/{eventType}/{eventId}", method = RequestMethod.POST)
	public ResponseEntity<SupplierBqPojo> updateSupplierAdditionalTax(@PathVariable("eventType") RfxTypes eventType, @PathVariable("eventId") String eventId, @RequestBody BqItem bqItem) throws JsonProcessingException {
		HttpHeaders header = new HttpHeaders();

		SupplierBqPojo pojo = new SupplierBqPojo();
		switch (eventType) {
		case RFA:
			try {
				RfaSupplierBq persistObject = null;
				String itemId = bqItem.getId();
				LOG.info(itemId);
				persistObject = rfaSupplierBqService.getSupplierBqByBqAndSupplierId(bqItem.getId(), SecurityLibrary.getLoggedInUserTenantId());
				if (persistObject != null) {
					BigDecimal additionalTax = bqItem.getAdditionalTax();
					LOG.info("Additional Tax " + additionalTax);
					BigDecimal grandTotal = persistObject.getGrandTotal();
					LOG.info(grandTotal);
					if (additionalTax != null && grandTotal != null) {
						BigDecimal totalAfterTax = additionalTax.add(grandTotal);
						LOG.info(totalAfterTax);
						AuctionRules rules = rfaEventService.getAuctionRulesByEventId(eventId);
						Event event = genericEventService.getEventById(eventId, eventType);
						int round = event.getDecimal() != null ? Integer.parseInt(event.getDecimal()) : 2;
						RfaEventSupplier eventSupplier = rfaEventSupplierService.findEventSupplierByEventIdAndSupplierRevisedSubmission(eventId, SecurityLibrary.getLoggedInUserTenantId());
						boolean isDutchWithBQ = false;
						if ((eventSupplier.getRfxEvent().getAuctionType() == AuctionType.FORWARD_DUTCH || eventSupplier.getRfxEvent().getAuctionType() == AuctionType.REVERSE_DUTCH) && Boolean.TRUE == eventSupplier.getRfxEvent().getBillOfQuantity()) {
							isDutchWithBQ = true;
						}
						boolean revisedSubmissionMode = false;
						if (eventSupplier.getRfxEvent().getStatus() == EventStatus.CLOSED && (rules.getLumsumBiddingWithTax() != null || isDutchWithBQ) && eventSupplier.getRevisedBidSubmitted() == Boolean.FALSE) {
							revisedSubmissionMode = true;
						}
						if (!revisedSubmissionMode) {
							persistObject.setTotalAfterTax(totalAfterTax.setScale(round, RoundingMode.DOWN));
						} else {
							persistObject.setRevisedGrandTotal(totalAfterTax.setScale(round, RoundingMode.DOWN));
						}
						persistObject.setAdditionalTax(additionalTax);
						BigDecimal revisedGrandTotal = persistObject.getRevisedGrandTotal();
						persistObject = rfaSupplierBqService.updateSupplierBq(persistObject);
						pojo.setAdditionalTax(persistObject.getAdditionalTax());
						pojo.setGrandTotal(persistObject.getGrandTotal());
						pojo.setRevisedGrandTotal(persistObject.getRevisedGrandTotal());
						pojo.setTotalAfterTax(persistObject.getTotalAfterTax());
						if (revisedSubmissionMode && revisedGrandTotal != null && revisedGrandTotal.compareTo(persistObject.getTotalAfterTax()) != 0) {
							header.add("error", "Your Bill Of Quantities total does not match with your final Auction bid price. Please revise and submit");
							return new ResponseEntity<SupplierBqPojo>(pojo, header, HttpStatus.INTERNAL_SERVER_ERROR);
						}
					}
				}

			} catch (Exception e) {
				LOG.error(e);
				header.add("error", "Error  while Updating data");
				return new ResponseEntity<SupplierBqPojo>(pojo, header, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			break;
		case RFP:
			try {
				String itemId = bqItem.getId();
				LOG.info(itemId);
				RfpSupplierBq persistObject = null;
				persistObject = rfpSupplierBqService.getSupplierBqByBqAndSupplierId(bqItem.getId(), SecurityLibrary.getLoggedInUserTenantId());
				if (persistObject != null) {
					BigDecimal additionalTax = bqItem.getAdditionalTax();
					LOG.info("Additional Tax " + additionalTax);
					BigDecimal grandTotal = persistObject.getGrandTotal();
					LOG.info(grandTotal);
					if (additionalTax != null && grandTotal != null) {
						BigDecimal totalAfterTax = additionalTax.add(grandTotal);
						LOG.info(totalAfterTax);
						persistObject.setTotalAfterTax(totalAfterTax);
						persistObject.setAdditionalTax(additionalTax);
						persistObject = rfpSupplierBqService.updateSupplierBq(persistObject);
						pojo.setAdditionalTax(persistObject.getAdditionalTax());
						pojo.setGrandTotal(persistObject.getGrandTotal());
						pojo.setTotalAfterTax(persistObject.getTotalAfterTax());
					}
				}

			} catch (Exception e) {
				LOG.error(e);
				header.add("error", "Error  while Updating data");
				return new ResponseEntity<SupplierBqPojo>(pojo, header, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			break;

		case RFQ:
			try {
				String itemId = bqItem.getId();
				LOG.info(itemId);
				RfqSupplierBq persistObject = null;
				persistObject = rfqSupplierBqService.getSupplierBqByBqAndSupplierId(bqItem.getId(), SecurityLibrary.getLoggedInUserTenantId());
				if (persistObject != null) {
					BigDecimal additionalTax = bqItem.getAdditionalTax();
					LOG.info("Additional Tax " + additionalTax);
					BigDecimal grandTotal = persistObject.getGrandTotal();
					LOG.info(grandTotal);
					if (additionalTax != null && grandTotal != null) {
						BigDecimal totalAfterTax = additionalTax.add(grandTotal);
						LOG.info(totalAfterTax);
						persistObject.setTotalAfterTax(totalAfterTax);
						persistObject.setAdditionalTax(additionalTax);
						persistObject = rfqSupplierBqService.updateSupplierBq(persistObject);
						pojo.setAdditionalTax(persistObject.getAdditionalTax());
						pojo.setGrandTotal(persistObject.getGrandTotal());
						pojo.setTotalAfterTax(persistObject.getTotalAfterTax());
					}
				}

			} catch (Exception e) {
				LOG.error(e);
				header.add("error", "Error  while Updating data");
				return new ResponseEntity<SupplierBqPojo>(pojo, header, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			break;
		case RFT:
			try {
				String itemId = bqItem.getId();
				LOG.info(itemId);
				RftSupplierBq persistObject = null;
				persistObject = rftSupplierBqService.getSupplierBqByBqAndSupplierId(bqItem.getId(), SecurityLibrary.getLoggedInUserTenantId());
				if (persistObject != null) {
					BigDecimal additionalTax = bqItem.getAdditionalTax();
					LOG.info("Additional Tax " + additionalTax);
					BigDecimal grandTotal = persistObject.getGrandTotal();
					LOG.info(grandTotal);
					if (additionalTax != null && grandTotal != null) {
						BigDecimal totalAfterTax = additionalTax.add(grandTotal);
						LOG.info(totalAfterTax);
						persistObject.setTotalAfterTax(totalAfterTax);
						persistObject.setAdditionalTax(additionalTax);
						persistObject = rftSupplierBqService.updateSupplierBq(persistObject);
						pojo.setAdditionalTax(persistObject.getAdditionalTax());
						pojo.setGrandTotal(persistObject.getGrandTotal());
						pojo.setTotalAfterTax(persistObject.getTotalAfterTax());
					}
				}

			} catch (Exception e) {
				LOG.error(e);
				header.add("error", "Error  while Updating data");
				return new ResponseEntity<SupplierBqPojo>(pojo, header, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			break;
		default:
			break;
		}

		header.add("success", "Bq updated successfully");
		return new ResponseEntity<SupplierBqPojo>(pojo, header, HttpStatus.OK);
	}

	@RequestMapping(path = "/updateSupplierSORItemDetails/{eventType}/{eventId}", method = RequestMethod.POST)
	public ResponseEntity<?> updateSupplierSorItemDetails(@PathVariable("eventType") RfxTypes eventType, @RequestBody SorItem sorItem, @PathVariable("eventId") String eventId) throws JsonProcessingException, ApplicationException {
		HttpHeaders header = new HttpHeaders();
		switch (eventType) {
			case RFQ:
				RfqEvent rfqEvent = rfqEventService.getEventById(eventId);
				genericSorService.updateSupplierSorItem(sorItem, RfxTypes.RFQ , rfqEvent.getDecimal());
				break;
			case RFP:
				RfpEvent rfpEvent = rfpEventService.getEventById(eventId);
				genericSorService.updateSupplierSorItem(sorItem, RfxTypes.RFP , rfpEvent.getDecimal());
				break;
			case RFA:
				RfaEvent rfaEvent = rfaEventService.getRfaEventById(eventId);
				genericSorService.updateSupplierSorItem(sorItem, RfxTypes.RFA , rfaEvent.getDecimal());
				break;
			case RFI:
				RfiEvent rfiEvent = rfiEventService.getRfiEventById(eventId);
				genericSorService.updateSupplierSorItem(sorItem, RfxTypes.RFI , rfiEvent.getDecimal());
				break;
			case RFT:
				RftEvent rftEvent = rftEventService.getEventById(eventId);
				genericSorService.updateSupplierSorItem(sorItem, RfxTypes.RFT , rftEvent.getDecimal());
				break;
			default:
				break;
		}

		header.add("success", "Sor updated successfully");
		return new ResponseEntity<>(null, header, HttpStatus.OK);
	}



	@RequestMapping(path = "/updateSupplierSORItemExtraFieldDetails/{eventType}/{eventId}", method = RequestMethod.POST)
	public ResponseEntity<?> updateSupplierSORItemExtraFieldDetails(@PathVariable("eventType") RfxTypes eventType, @RequestBody SorItem sorItem, @PathVariable("eventId") String eventId) throws JsonProcessingException, ApplicationException {
		HttpHeaders header = new HttpHeaders();
		switch (eventType) {
			case RFQ:
				genericSorService.updateSupplierSorItemField1(sorItem, RfxTypes.RFQ);
				break;
			case RFP:
				genericSorService.updateSupplierSorItemField1(sorItem, RfxTypes.RFP);
				break;
			case RFA:
				genericSorService.updateSupplierSorItemField1(sorItem, RfxTypes.RFA);
				break;
			case RFI:
				genericSorService.updateSupplierSorItemField1(sorItem, RfxTypes.RFI);
				break;
			case RFT:
				genericSorService.updateSupplierSorItemField1(sorItem, RfxTypes.RFT);
				break;
			default:
				break;
		}

		header.add("success", "Sor updated successfully");
		return new ResponseEntity<>(null, header, HttpStatus.OK);
	}

	@RequestMapping(path = "/saveDraftSupplierSor/{eventType}/{eventId}/{bqId}", method = RequestMethod.POST)
	public ResponseEntity<List<?>> saveDraftSupplierSor(@PathVariable RfxTypes eventType, @PathVariable String eventId ,@PathVariable String bqId, @RequestBody Map<String, String> data) throws JsonProcessingException {
		HttpHeaders header = new HttpHeaders();

		try {
			switch (eventType) {
				case RFA:
					RfaSupplierSor rfaSupplierSor = rfaSupplierSorService.findRfaSupplierSorStatusbyEventIdAndSupplierId(SecurityLibrary.getLoggedInUserTenantId(),
							eventId, bqId);
					RfaSupplierSor rfqSupplierSor1 = rfaSupplierSorService.getById(rfaSupplierSor.getId());
					rfqSupplierSor1.setRemark(data.get("remarks"));
					rfqSupplierSor1.setSupplierSorStatus(SupplierBqStatus.DRAFT);
					rfaSupplierSorService.updateRfaSor(rfqSupplierSor1);
					break;
				case RFP:
					RfpSupplierSor rfpSupplierSor = rfpSupplierSorService.findRfpSupplierSorStatusbyEventIdAndSupplierId(SecurityLibrary.getLoggedInUserTenantId(),
							eventId, bqId);
					RfpSupplierSor rfqSupplierSor2 = rfpSupplierSorService.getById(rfpSupplierSor.getId());
					rfqSupplierSor2.setRemark(data.get("remarks"));
					rfqSupplierSor2.setSupplierSorStatus(SupplierBqStatus.DRAFT);
					rfpSupplierSorService.updateRfpSor(rfqSupplierSor2);
					break;
				case RFQ:
					RfqSupplierSor rfqSupplierSor = rfqSupplierSorService.findRfqSupplierSorStatusbyEventIdAndSupplierId(SecurityLibrary.getLoggedInUserTenantId(),
							eventId, bqId);
					RfqSupplierSor rfqSupplierSor3 = rfqSupplierSorService.getById(rfqSupplierSor.getId());
					rfqSupplierSor3.setRemark(data.get("remarks"));
					rfqSupplierSor3.setSupplierSorStatus(SupplierBqStatus.DRAFT);
					rfqSupplierSorService.updateRfqSor(rfqSupplierSor3);
					break;
				case RFT:
					RftSupplierSor rftSupplierSor = rftSupplierSorService.findRftSupplierSorStatusbyEventIdAndSupplierId(SecurityLibrary.getLoggedInUserTenantId(),
							eventId, bqId);
					RftSupplierSor rfqSupplierSor4 = rftSupplierSorService.getById(rftSupplierSor.getId());
					rfqSupplierSor4.setRemark(data.get("remarks"));
					rfqSupplierSor4.setSupplierSorStatus(SupplierBqStatus.DRAFT);
					rftSupplierSorService.updateRftSor(rfqSupplierSor4);
					break;
				case RFI:
					RfiSupplierSor rfiSupplierSor = rfiSupplierSorService.findRfiSupplierSorStatusbyEventIdAndSupplierId(SecurityLibrary.getLoggedInUserTenantId(),
							eventId, bqId);
					RfiSupplierSor rfiSupplierSor4 = rfiSupplierSorService.getById(rfiSupplierSor.getId());
					rfiSupplierSor4.setRemark(data.get("remarks"));
					rfiSupplierSor4.setSupplierSorStatus(SupplierBqStatus.DRAFT);
					rfiSupplierSorService.updateRfiSor(rfiSupplierSor4);
					break;
				default:
					break;

			}

		} catch (Exception e) {
			LOG.error("Error while drafting sor : " + e.getMessage(), e);
			header.add("error", "Error saving  Sor details : " + e.getMessage());
		}
		header.add("success", "Succesfully updated Sor details");
		return new ResponseEntity<List<?>>(header, HttpStatus.OK);
	}


	@RequestMapping(path = "/saveCompleteSupplierSor/{eventType}/{eventId}/{bqId}", method = RequestMethod.POST)
	public ResponseEntity<List<?>> saveSaveCompleteSupplierSor(@PathVariable RfxTypes eventType, @PathVariable String eventId ,@PathVariable String bqId, @RequestBody Map<String, String> data) throws JsonProcessingException {
		HttpHeaders header = new HttpHeaders();
		Boolean isNotAllCompleted = Boolean.FALSE;

		try {
			switch (eventType) {
				case RFA:
					RfaSupplierSor rfaSupplierSor = rfaSupplierSorService.findRfaSupplierSorStatusbyEventIdAndSupplierId(SecurityLibrary.getLoggedInUserTenantId(),
							eventId, bqId);
					RfaSupplierSor rfqSupplierSor1 = rfaSupplierSorService.getById(rfaSupplierSor.getId());
					rfqSupplierSor1.setRemark(data.get("remarks"));
					rfqSupplierSor1.setSupplierSorStatus(SupplierBqStatus.COMPLETED);

					isNotAllCompleted = genericSorService.isAllFilledUp(rfqSupplierSor1, RfxTypes.RFA, SecurityLibrary.getLoggedInUserTenantId());

					if(isNotAllCompleted == true) {
						throw new ApplicationException("Please fill up all Rate");
					}

					rfaSupplierSorService.updateRfaSor(rfqSupplierSor1);
					break;
				case RFP:
					RfpSupplierSor rfpSupplierSor = rfpSupplierSorService.findRfpSupplierSorStatusbyEventIdAndSupplierId(SecurityLibrary.getLoggedInUserTenantId(),
							eventId, bqId);
					RfpSupplierSor rfqSupplierSor2 = rfpSupplierSorService.getById(rfpSupplierSor.getId());
					rfqSupplierSor2.setRemark(data.get("remarks"));
					rfqSupplierSor2.setSupplierSorStatus(SupplierBqStatus.COMPLETED);

					isNotAllCompleted = genericSorService.isAllFilledUp(rfqSupplierSor2, RfxTypes.RFP, SecurityLibrary.getLoggedInUserTenantId());

					if(isNotAllCompleted == true) {
						throw new ApplicationException("Please fill up all Rate");
					}

					rfpSupplierSorService.updateRfpSor(rfqSupplierSor2);
					break;
				case RFQ:
					RfqSupplierSor rfqSupplierSor = rfqSupplierSorService.findRfqSupplierSorStatusbyEventIdAndSupplierId(SecurityLibrary.getLoggedInUserTenantId(),
							eventId, bqId);
					RfqSupplierSor rfqSupplierSor3 = rfqSupplierSorService.getById(rfqSupplierSor.getId());
					rfqSupplierSor3.setRemark(data.get("remarks"));
					rfqSupplierSor3.setSupplierSorStatus(SupplierBqStatus.COMPLETED);

					isNotAllCompleted = genericSorService.isAllFilledUp(rfqSupplierSor3, RfxTypes.RFQ, SecurityLibrary.getLoggedInUserTenantId());

					if(isNotAllCompleted == true) {
						throw new ApplicationException("Please fill up all Rate");
					}

					rfqSupplierSorService.updateRfqSor(rfqSupplierSor3);
					break;
				case RFT:
					RftSupplierSor rftSupplierSor = rftSupplierSorService.findRftSupplierSorStatusbyEventIdAndSupplierId(SecurityLibrary.getLoggedInUserTenantId(),
							eventId, bqId);
					RftSupplierSor rfqSupplierSor4 = rftSupplierSorService.getById(rftSupplierSor.getId());
					rfqSupplierSor4.setRemark(data.get("remarks"));
					rfqSupplierSor4.setSupplierSorStatus(SupplierBqStatus.COMPLETED);

					isNotAllCompleted = genericSorService.isAllFilledUp(rftSupplierSor, RfxTypes.RFT, SecurityLibrary.getLoggedInUserTenantId());

					if(isNotAllCompleted == true) {
						throw new ApplicationException("Please fill up all Rate");
					}
					rftSupplierSorService.updateRftSor(rfqSupplierSor4);
					break;
				case RFI:
					RfiSupplierSor rfiSupplierSor = rfiSupplierSorService.findRfiSupplierSorStatusbyEventIdAndSupplierId(SecurityLibrary.getLoggedInUserTenantId(),
							eventId, bqId);
					RfiSupplierSor rfiSupplierSor4 = rfiSupplierSorService.getById(rfiSupplierSor.getId());
					rfiSupplierSor4.setRemark(data.get("remarks"));
					rfiSupplierSor4.setSupplierSorStatus(SupplierBqStatus.COMPLETED);

					isNotAllCompleted = genericSorService.isAllFilledUp(rfiSupplierSor4, RfxTypes.RFI, SecurityLibrary.getLoggedInUserTenantId());

					if(isNotAllCompleted == true) {
						throw new ApplicationException("Please fill up all Rate");
					}

					rfiSupplierSorService.updateRfiSor(rfiSupplierSor4);
					break;
				default:
					break;

			}
			header.add("success", "Succesfully updated Sor details");
		} catch (ApplicationException e) {
			header.add("error", "Please fill up all Rate and mandatory fields");
			return new ResponseEntity<List<?>>(header, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			LOG.error("Error while drafting sor : " + e.getMessage(), e);
			header.add("error", "Error saving  Sor details : " + e.getMessage());
		}
		return new ResponseEntity<List<?>>(header, HttpStatus.OK);
	}

	@RequestMapping(path = "/updateSupplierBQItemDetails/{eventType}", method = RequestMethod.POST)
	public ResponseEntity<BqItem> updateSupplierBQItemDetails(@PathVariable("eventType") RfxTypes eventType, @RequestBody BqItem bqItem) throws JsonProcessingException {
		HttpHeaders header = new HttpHeaders();
		BqItem persistObject = null;
		Event event = null;
		LOG.info("  updateSupplierBQItemDetails  " + bqItem.getAdditionalTax());
		try {

			int decimal = 0;
			String itemId = bqItem.getId();
			String pos = bqItem.getPosition();
			event = genericEventService.getEventById(bqItem.getEventId(), eventType);

			decimal = Integer.valueOf(event.getDecimal() != null ? event.getDecimal() : "0");

			LOG.info("========================" + decimal);

			LOG.info("---------------tenantId---------------------" + SecurityLibrary.getLoggedInUserTenantId());
			LOG.info("---------------itemId-----------------------" + itemId);
			LOG.info("---------------eventType-----------------------" + eventType);
			LOG.info("Total Tax " + bqItem.getTax() + " TaxType " + bqItem.getTaxType());
			persistObject = genericBqService.getSupplierBqItem(itemId, SecurityLibrary.getLoggedInUserTenantId(), eventType);
			LOG.info(persistObject.getTaxType());
			if (bqItem.getTax() == null) {
				persistObject.setTotalAmountWithTax(persistObject.getTotalAmount() != null ? persistObject.getTotalAmount().setScale(decimal, RoundingMode.HALF_UP) : BigDecimal.ZERO);
				persistObject.setTaxType(bqItem.getTaxType());
				LOG.info("TAX TYPE " + persistObject.getTaxType());
			}
			if (bqItem.getQuantity() != null && bqItem.getUnitPrice() != null) {

				java.math.BigDecimal unitPrice = bqItem.getUnitPrice();
				java.math.BigDecimal qnty = bqItem.getQuantity();
				if (qnty.multiply(unitPrice).compareTo(new java.math.BigDecimal("999999999999.9999")) == 1) {
					LOG.info("VALUE IS " + qnty.multiply(unitPrice));
					header.add("error", messageSource.getMessage("buyer.rftbq.valueExceeds", new Object[] {}, null));
					return new ResponseEntity<BqItem>(persistObject, header, HttpStatus.BAD_REQUEST);
				}
			}
			switch (pos) {
			case "1":
				if (bqItem.getUnitPrice() == null) {
					bqItem.setUnitPrice(BigDecimal.ZERO);
				}

				persistObject.setUnitPriceType(bqItem.getUnitPriceType());
				persistObject.setUnitPrice(bqItem.getUnitPrice() != null ? bqItem.getUnitPrice().setScale(decimal, RoundingMode.DOWN) : BigDecimal.ZERO);
				LOG.info("========1111================" + persistObject.getUnitPrice());
				persistObject.setTax(bqItem.getTax() != null ? bqItem.getTax().setScale(decimal, RoundingMode.DOWN) : BigDecimal.ZERO);
				persistObject.setTaxType(bqItem.getTaxType());
				if (bqItem.getUnitPrice() != null && bqItem.getQuantity() != null) {
					LOG.info("Total Amount " + bqItem.getUnitPrice().multiply(bqItem.getQuantity()));
					persistObject.setTotalAmount((bqItem.getUnitPrice().multiply(bqItem.getQuantity())).setScale(decimal, RoundingMode.DOWN));
				}
				persistObject.setTotalAmountWithTax(persistObject.getTotalAmount() != null ? persistObject.getTotalAmount().setScale(decimal, RoundingMode.HALF_UP) : BigDecimal.ZERO);
				LOG.info("Total amount : " + persistObject.getTotalAmount());
				if (bqItem.getTax() == null) {
					bqItem.setTax(BigDecimal.ZERO);
				}

				if (bqItem.getTax() != null && bqItem.getTaxType() != null && persistObject.getTotalAmount() != null) {

					LOG.info("Total setTotalAmountWithTax : " + persistObject.getTotalAmountWithTax());

					switch (bqItem.getTaxType()) {
					case Percent:
						LOG.info("  Percent  ");
						persistObject.setTotalAmountWithTax(persistObject.getTotalAmount().add(persistObject.getTotalAmount().multiply(bqItem.getTax()).divide(new BigDecimal(100), decimal, RoundingMode.HALF_UP)));
						break;
					case Amount:
						LOG.info("  Amount  ");
						persistObject.setTotalAmountWithTax((persistObject.getTotalAmount().add(persistObject.getTax())).setScale(decimal, RoundingMode.HALF_UP));
						break;

					default:
						break;
					}
				} else {
					if (persistObject.getTotalAmount() != null) {
						persistObject.setTotalAmountWithTax(persistObject.getTotalAmount().setScale(decimal, RoundingMode.HALF_UP));
					} else {
						persistObject.setTotalAmountWithTax(BigDecimal.ZERO);
					}
				}
				break;
			case "2":
				if (bqItem.getTotalAmount() != null && bqItem.getQuantity() != null) {
					persistObject.setUnitPrice(bqItem.getTotalAmount().divide(bqItem.getQuantity(), decimal, RoundingMode.DOWN));
					persistObject.setTotalAmount(bqItem.getTotalAmount().setScale(decimal, RoundingMode.DOWN));
					persistObject.setTotalAmountWithTax(persistObject.getTotalAmount().setScale(decimal, RoundingMode.HALF_UP));
					if (bqItem.getTax() != null && bqItem.getTotalAmount() != null) {
						persistObject.setTax(bqItem.getTax().setScale(decimal, RoundingMode.DOWN));
						persistObject.setTaxType(bqItem.getTaxType());

						switch (bqItem.getTaxType()) {
						case Percent:
							persistObject.setTotalAmountWithTax(bqItem.getTotalAmount().add(bqItem.getTotalAmount().multiply(bqItem.getTax()).divide(new BigDecimal(100), decimal, RoundingMode.HALF_UP)));
							break;
						case Amount:
							persistObject.setTotalAmountWithTax(bqItem.getTotalAmount().add(bqItem.getTax()).setScale(decimal, RoundingMode.HALF_UP));
							break;
						default:
							break;
						}
					}
				}
				break;
			case "3":
			case "4":
				LOG.info("TAX : " + bqItem.getTax() + " Total Amount : " + bqItem.getTotalAmount());
				if (bqItem.getTax() != null && bqItem.getTotalAmount() != null) {
					persistObject.setTax(bqItem.getTax().setScale(decimal, RoundingMode.DOWN));
					persistObject.setTaxType(bqItem.getTaxType());

					switch (bqItem.getTaxType()) {
					case Percent:
						persistObject.setTotalAmountWithTax(bqItem.getTotalAmount().add(bqItem.getTotalAmount().multiply(bqItem.getTax()).divide(new BigDecimal(100), decimal, RoundingMode.HALF_UP)));
						break;
					case Amount:
						persistObject.setTotalAmountWithTax(bqItem.getTotalAmount().add(bqItem.getTax()).setScale(decimal, RoundingMode.HALF_UP));
						break;
					default:
						break;
					}
				} else {
					persistObject.setTax(BigDecimal.ZERO);
					persistObject.setTaxType(TaxType.Amount);
				}
				break;
			case "5":
				persistObject.setField1(bqItem.getField1());
				break;
			case "6":
				persistObject.setField2(bqItem.getField2());
				break;
			case "7":
				persistObject.setField3(bqItem.getField3());
				break;
			case "8":
				persistObject.setField4(bqItem.getField4());
				break;
			case "9":
				persistObject.setField5(bqItem.getField5());
				break;
			case "10":
				persistObject.setField6(bqItem.getField6());
				break;
			case "11":
				persistObject.setField7(bqItem.getField7());
				break;
			case "12":
				persistObject.setField8(bqItem.getField8());
				break;
			case "13":
				persistObject.setField9(bqItem.getField9());
				break;
			case "14":
				persistObject.setField10(bqItem.getField10());
				break;
			}
			if (bqItem.getAdditionalTax() == null) {
				bqItem.setAdditionalTax(BigDecimal.ZERO);
			}
			persistObject.setAdditionalTax(bqItem.getAdditionalTax() != null ? bqItem.getAdditionalTax().setScale(decimal, RoundingMode.DOWN) : BigDecimal.ZERO);
			persistObject.setTaxDescription(bqItem.getTaxDescription());
			// This will throw the class cast exception (Please not cast RFA
			// because its common for RFX)--
			// LOG.info("UI Object : " + ((RfaSupplierBqItem)
			// persistObject).getSupplierBq().toString());
			persistObject = genericBqService.updateSupplierBqItem(persistObject, eventType, "2");
			persistObject.getUom().setCreatedBy(null);
			persistObject.getUom().setModifiedBy(null);

			switch (eventType) {
			case RFP:
				/**
				 * Remove child objects that will throw Lazy during JSON conversion for View. This one is safe as there
				 * are no updates happening to this instance object.
				 */

				((RfpSupplierBqItem) persistObject).setChildren(null);
				((RfpSupplierBqItem) persistObject).setParent(null);
				break;
			case RFA:

				((RfaSupplierBqItem) persistObject).setChildren(null);
				((RfaSupplierBqItem) persistObject).setParent(null);

				RfaEvent rfaEvent = rfaEventService.getRfaEventByeventId(bqItem.getEventId());
				if (rfaEvent != null && rfaEvent.getStatus() == EventStatus.CLOSED) {

					LOG.info("close  event ");

					if ((AuctionType.FORWARD_DUTCH == rfaEvent.getAuctionType() || AuctionType.REVERSE_DUTCH == rfaEvent.getAuctionType()) && rfaEvent.getWinningPrice() != null) {
						if (rfaEvent.getWinningPrice() != null) {
							BigDecimal winningPrice = rfaEvent.getWinningPrice();

							LOG.info("Wining Price     " + winningPrice);
							List<RfaSupplierBq> suppierBq = rfaSupplierBqService.findRfaSupplierBqbyEventIdAndSupplierId(bqItem.getEventId(), SecurityLibrary.getLoggedInUserTenantId());
							for (RfaSupplierBq rfaSupplierBq : suppierBq) {
								List<RfaSupplierBqItem> listBq = rfaSupplierBq.getSupplierBqItems();
								if (CollectionUtil.isNotEmpty(listBq)) {
									BigDecimal price = new BigDecimal(0);
									BigDecimal bqItemUnitPrice = new BigDecimal(0);
									for (RfaSupplierBqItem bqitems : listBq) {
										if (bqitems.getOrder() != 0 && bqitems.getUnitPrice() != null) {
											price = bqitems.getTotalAmountWithTax();
											if (PricingTypes.TRADE_IN_PRICE == bqitems.getPriceType()) {
												bqItemUnitPrice = bqItemUnitPrice.subtract(price);
											} else {
												bqItemUnitPrice = bqItemUnitPrice.add(price);
											}
										}
									}

									if (rfaSupplierBq.getAdditionalTax() != null) {
										bqItemUnitPrice = bqItemUnitPrice.add(rfaSupplierBq.getAdditionalTax());
									}
									if (bqItem.getTax() == null) {
										persistObject.setTaxType(bqItem.getTaxType());
										LOG.info("TAX TYPE " + persistObject.getTaxType());
									}
									LOG.info("BqItemunitPrice    === " + bqItemUnitPrice);
									if (winningPrice.longValue() != bqItemUnitPrice.longValue()) {
										header.add("error", "Your Bill Of Quantities total does not match with your final Auction bid price. Please revise and submit");
										return new ResponseEntity<BqItem>(persistObject, header, HttpStatus.OK);
									}
								}
							}

						}

					}

					if ((AuctionType.FORWARD_ENGISH == rfaEvent.getAuctionType() || AuctionType.REVERSE_ENGISH == rfaEvent.getAuctionType())) {

						List<RfaSupplierBq> suppierBq = rfaSupplierBqService.findRfaSupplierBqbyEventIdAndSupplierId(bqItem.getEventId(), SecurityLibrary.getLoggedInUserTenantId());
						for (RfaSupplierBq rfaSupplierBq : suppierBq) {
							if (rfaSupplierBq.getRevisedGrandTotal() != null) {
								BigDecimal winningPrice = rfaSupplierBq.getRevisedGrandTotal();
								LOG.info("Wining Price     " + winningPrice);
								List<RfaSupplierBqItem> listBq = rfaSupplierBq.getSupplierBqItems();
								if (CollectionUtil.isNotEmpty(listBq)) {
									BigDecimal price = new BigDecimal(0);
									BigDecimal bqItemUnitPrice = new BigDecimal(0);
									for (RfaSupplierBqItem bqitems : listBq) {
										if (bqitems.getOrder() != 0 && bqitems.getUnitPrice() != null) {
											price = bqitems.getTotalAmountWithTax();
											if (PricingTypes.TRADE_IN_PRICE == bqitems.getPriceType()) {
												bqItemUnitPrice = bqItemUnitPrice.subtract(price);
											} else {
												bqItemUnitPrice = bqItemUnitPrice.add(price);
											}
										}
									}

									if (rfaSupplierBq.getAdditionalTax() != null) {
										bqItemUnitPrice = bqItemUnitPrice.add(rfaSupplierBq.getAdditionalTax());
									}
									LOG.info("BqItemunitPrice    === " + bqItemUnitPrice);
									if (bqItem.getTax() == null) {
										persistObject.setTaxType(bqItem.getTaxType());
										LOG.info("TAX TYPE " + persistObject.getTaxType());
									}
									if (winningPrice.longValue() != bqItemUnitPrice.longValue()) {
										header.add("error", "Your Bill Of Quantities total does not match with your final Auction bid price. Please revise and submit");
										return new ResponseEntity<BqItem>(persistObject, header, HttpStatus.OK);
									}
								}
							}
						}
					}

				} else {
					LOG.info("BqItemunitPrice    === emoty event ");
				}

				break;
			case RFI:
				break;
			case RFQ:
				((RfqSupplierBqItem) persistObject).setChildren(null);
				((RfqSupplierBqItem) persistObject).setParent(null);
				break;
			case RFT:
				((RftSupplierBqItem) persistObject).setChildren(null);
				((RftSupplierBqItem) persistObject).setParent(null);
				break;
			default:
				break;
			}
		} catch (ApplicationException ae) {
			header.add("error", ae.getMessage());
			persistObject = genericBqService.getSupplierBqItem(bqItem.getId(), SecurityLibrary.getLoggedInUserTenantId(), eventType);
			switch (eventType) {
			case RFP:
				/**
				 * Remove child objects that will throw Lazy during JSON conversion for View. This one is safe as there
				 * are no updates happening to this instance object.
				 */
				((RfpSupplierBqItem) persistObject).setChildren(null);
				((RfpSupplierBqItem) persistObject).setParent(null);
				break;
			case RFA:
				((RfaSupplierBqItem) persistObject).setChildren(null);
				((RfaSupplierBqItem) persistObject).setParent(null);
				break;
			case RFI:
				break;
			case RFQ:
				((RfqSupplierBqItem) persistObject).setChildren(null);
				((RfqSupplierBqItem) persistObject).setParent(null);
				break;
			case RFT:
				((RftSupplierBqItem) persistObject).setChildren(null);
				((RftSupplierBqItem) persistObject).setParent(null);
				break;
			default:
				break;
			}
			persistObject.getUom().setCreatedBy(null);
			persistObject.getUom().setModifiedBy(null);
			if (bqItem.getTax() == null) {
				persistObject.setTaxType(bqItem.getTaxType());
				LOG.info("TAX TYPE " + persistObject.getTaxType());
			}
			return new ResponseEntity<BqItem>(persistObject, header, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			LOG.error("Error ------- : " + e.getMessage(), e);
			persistObject = genericBqService.getSupplierBqItem(bqItem.getId(), SecurityLibrary.getLoggedInUserTenantId(), eventType);
			switch (eventType) {
			case RFP:
				/**
				 * Remove child objects that will throw Lazy during JSON conversion for View. This one is safe as there
				 * are no updates happening to this instance object.
				 */
				((RfpSupplierBqItem) persistObject).setChildren(null);
				((RfpSupplierBqItem) persistObject).setParent(null);
				break;
			case RFA:
				((RfaSupplierBqItem) persistObject).setChildren(null);
				((RfaSupplierBqItem) persistObject).setParent(null);
				break;
			case RFI:
				break;
			case RFQ:
				((RfqSupplierBqItem) persistObject).setChildren(null);
				((RfqSupplierBqItem) persistObject).setParent(null);
				break;
			case RFT:
				((RftSupplierBqItem) persistObject).setChildren(null);
				((RftSupplierBqItem) persistObject).setParent(null);
				break;
			default:
				break;
			}
			persistObject.getUom().setCreatedBy(null);
			persistObject.getUom().setModifiedBy(null);

			LOG.info("TOTAL AMOUNT WITH TAX " + persistObject.getTotalAmountWithTax() + " Tax Type " + persistObject.getTaxType());
			if (bqItem.getTax() == null) {
				persistObject.setTaxType(bqItem.getTaxType());

			}
			header.add("error", "Error  while saving data");
			return new ResponseEntity<BqItem>(persistObject, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		LOG.info("TOTAL AMOUNT WITH TAX " + persistObject.getTotalAmountWithTax() + " Tax Type " + persistObject.getTaxType());
		if (bqItem.getTax() == null) {
			persistObject.setTaxType(bqItem.getTaxType());

		}
		header.add("success", "Updated Bq details succesfully");
		return new ResponseEntity<BqItem>(persistObject, header, HttpStatus.OK);
	}



	@RequestMapping(path = "/saveSupplierBQDetails/{eventType}/{eventId}/{totalAmountForValidation}/", method = RequestMethod.POST)
	public ResponseEntity<List<?>> saveSupplierBQDetails(@PathVariable RfxTypes eventType, @PathVariable String eventId, @PathVariable BigDecimal totalAmountForValidation, @RequestBody List<SupplierBqItem> supplierBqItem) throws JsonProcessingException {
		LOG.info("  List supplier bq item :   " + supplierBqItem.size());
		LOG.info("  eventId : " + eventId + " total amount " + totalAmountForValidation);
		HttpHeaders header = new HttpHeaders();
		try {
			LOG.info(" Total Amount After validations " + totalAmountForValidation);
			// if (totalAmountForValidation != null &&
			// totalAmountForValidation.compareTo(new
			// java.math.BigDecimal("9999999999999999.9999")) == 1) {
			// header.add("error", messageSource.getMessage("buyer.rftbq.valueExceeds", new
			// Object[] {}, null));
			// return new ResponseEntity<List<?>>(header, HttpStatus.INTERNAL_SERVER_ERROR);
			// }
			switch (eventType) {
			case RFA:
				// if (!validateAuctionRules(eventId, totalAmountForValidation)) {
				// LOG.info("validate false");
				// header.add("error", "This bid value is not acceptable, One of the other supplier is having the same
				// bid price");
				// return new ResponseEntity<List<?>>(header, HttpStatus.INTERNAL_SERVER_ERROR);
				// }
				AuctionRules auctionRules = rfaEventService.getLeanAuctionRulesByEventId(eventId);
				if (!validateAuctionRulesForOwnPrice(eventId, totalAmountForValidation, auctionRules)) {
					header.add("error", "Total amount is less than Initial bid price. Please enter more than or equal Initial bid price");
					return new ResponseEntity<List<?>>(header, HttpStatus.INTERNAL_SERVER_ERROR);

				}

				if (!validateAuctionRulesForOtherSuppliers(eventId, totalAmountForValidation, auctionRules)) {
					header.add("error", "This bid value is not acceptable, One of the other supplier is having the same bid price");
					return new ResponseEntity<List<?>>(header, HttpStatus.INTERNAL_SERVER_ERROR);
				}

				RfaEvent rfaEvent = rfaEventService.getRfaEventByeventId(eventId);
				if (rfaEvent != null && rfaEvent.getStatus() == EventStatus.CLOSED) {
					if ((AuctionType.FORWARD_DUTCH == rfaEvent.getAuctionType() || AuctionType.REVERSE_DUTCH == rfaEvent.getAuctionType())) {
						if (rfaEvent.getWinningPrice() != null) {
							BigDecimal winningPrice = rfaEvent.getWinningPrice();

							LOG.info("Wining Price     " + winningPrice);
							List<RfaSupplierBq> suppierBq = rfaSupplierBqService.findRfaSupplierBqbyEventIdAndSupplierId(eventId, SecurityLibrary.getLoggedInUserTenantId());
							for (RfaSupplierBq rfaSupplierBq : suppierBq) {
								List<RfaSupplierBqItem> listBq = rfaSupplierBq.getSupplierBqItems();
								if (CollectionUtil.isNotEmpty(listBq)) {
									BigDecimal price = new BigDecimal(0);
									BigDecimal bqItemUnitPrice = new BigDecimal(0);
									for (RfaSupplierBqItem bqitems : listBq) {
										if (bqitems.getOrder() != 0 && bqitems.getUnitPrice() != null) {
											price = bqitems.getTotalAmountWithTax();
											if (PricingTypes.TRADE_IN_PRICE == bqitems.getPriceType()) {
												bqItemUnitPrice = bqItemUnitPrice.subtract(price);
											} else {
												bqItemUnitPrice = bqItemUnitPrice.add(price);
											}
										}
									}
									LOG.info("Additional TAx " + rfaSupplierBq.getAdditionalTax());
									LOG.info("BqItemunitPrice    === " + bqItemUnitPrice);
									if (rfaSupplierBq.getAdditionalTax() != null) {
										LOG.info("Additional TAx " + rfaSupplierBq.getAdditionalTax());
										bqItemUnitPrice = bqItemUnitPrice.add(rfaSupplierBq.getAdditionalTax());
									}
									LOG.info("BqItemunitPrice    === " + bqItemUnitPrice + " Additionqal Tax " + rfaSupplierBq.getAdditionalTax());
									if (winningPrice.longValue() != bqItemUnitPrice.longValue()) {
										header.add("error", "Your Bill Of Quantities total does not match with your final Auction bid price. Please revise and submit");
										return new ResponseEntity<List<?>>(header, HttpStatus.INTERNAL_SERVER_ERROR);
									}
								}
							}
						}
					}
					if ((AuctionType.FORWARD_ENGISH == rfaEvent.getAuctionType() || AuctionType.REVERSE_ENGISH == rfaEvent.getAuctionType())) {

						List<RfaSupplierBq> suppierBq = rfaSupplierBqService.findRfaSupplierBqbyEventIdAndSupplierId(eventId, SecurityLibrary.getLoggedInUserTenantId());
						for (RfaSupplierBq rfaSupplierBq : suppierBq) {
							if (rfaSupplierBq.getRevisedGrandTotal() != null) {
								BigDecimal winningPrice = rfaSupplierBq.getRevisedGrandTotal();
								LOG.info("Wining Price     " + winningPrice);
								List<RfaSupplierBqItem> listBq = rfaSupplierBq.getSupplierBqItems();
								if (CollectionUtil.isNotEmpty(listBq)) {
									BigDecimal price = new BigDecimal(0);
									BigDecimal bqItemUnitPrice = new BigDecimal(0);
									for (RfaSupplierBqItem bqitems : listBq) {
										if (bqitems.getOrder() != 0 && bqitems.getUnitPrice() != null) {
											price = bqitems.getTotalAmountWithTax();
											if (PricingTypes.TRADE_IN_PRICE == bqitems.getPriceType()) {
												bqItemUnitPrice = bqItemUnitPrice.subtract(price);
											} else {
												bqItemUnitPrice = bqItemUnitPrice.add(price);
											}
										}
									}
									if (rfaSupplierBq.getAdditionalTax() != null) {
										LOG.info("Service " + rfaSupplierBq.getAdditionalTax());
										bqItemUnitPrice = bqItemUnitPrice.add(rfaSupplierBq.getAdditionalTax());
									}
									LOG.info("Service " + rfaSupplierBq.getAdditionalTax());
									LOG.info("BqItemunitPrice    === " + bqItemUnitPrice);
									if (winningPrice.longValue() != bqItemUnitPrice.longValue()) {
										header.add("error", "Your Bill Of Quantities total does not match with your final Auction bid price. Please revise and submit");
										return new ResponseEntity<List<?>>(header, HttpStatus.INTERNAL_SERVER_ERROR);
									}
								}
							}
						}
					}
				}

				rfaSupplierBqItemService.updateBqItems(supplierBqItem, SecurityLibrary.getLoggedInUserTenantId(), SupplierBqStatus.COMPLETED);
				break;
			case RFI:
				break;
			case RFP:
				rfpSupplierBqItemService.updateBqItems(supplierBqItem, SecurityLibrary.getLoggedInUserTenantId(), SupplierBqStatus.COMPLETED);
				break;
			case RFQ:
				rfqSupplierBqItemService.updateBqItems(supplierBqItem, SecurityLibrary.getLoggedInUserTenantId(), SupplierBqStatus.COMPLETED);
				break;
			case RFT:
				rftSupplierBqItemService.updateBqItems(supplierBqItem, SecurityLibrary.getLoggedInUserTenantId(), SupplierBqStatus.COMPLETED);
				break;
			default:
				break;

			}

		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
			header.add("error", "Error saving Bq details : " + e.getMessage());
		}
		header.add("success", "Succesfully updated Bq details");
		return new ResponseEntity<List<?>>(header, HttpStatus.OK);
	}

	@RequestMapping(path = "/saveSupplierBQRemarks/{eventType}", method = RequestMethod.POST)
	public ResponseEntity<List<?>> saveSupplierBQRemarks(@PathVariable RfxTypes eventType, @RequestBody Comments supplierComment) throws JsonProcessingException {
		LOG.info(" saveSupplierBQRemarks  called   " + supplierComment);
		HttpHeaders header = new HttpHeaders();
		List<?> comment = null;
		try {

			switch (eventType) {
			case RFA:
				comment = rfaSupplierCommentService.saveSupplierBqComment(supplierComment, SecurityLibrary.getLoggedInUserTenantId());
				break;
			case RFI:
				break;
			case RFP:
				comment = rfpSupplierCommentService.saveSupplierBqComment(supplierComment, SecurityLibrary.getLoggedInUserTenantId());
				break;
			case RFQ:
				comment = rfqSupplierCommentService.saveSupplierBqComment(supplierComment, SecurityLibrary.getLoggedInUserTenantId());
				break;
			case RFT:
				comment = rftSupplierCommentService.saveSupplierBqComment(supplierComment, SecurityLibrary.getLoggedInUserTenantId());
				break;
			default:
				break;

			}

		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
			header.add("error", "Error saving Remarks : " + e.getMessage());
			return new ResponseEntity<List<?>>(null, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		header.add("success", "Successfully saved comments");
		return new ResponseEntity<List<?>>(comment, header, HttpStatus.OK);
	}

	@RequestMapping(path = "/getSupplierBQRemarks/{eventType}/{bqItemId}", method = RequestMethod.POST)
	public ResponseEntity<List<?>> getSupplierBQRemarks(@PathVariable("eventType") RfxTypes eventType, @PathVariable("bqItemId") String bqItemId) throws JsonProcessingException {
		HttpHeaders header = new HttpHeaders();
		List<?> commentList = null;
		try {

			switch (eventType) {
			case RFA:
				commentList = rfaSupplierCommentService.getSupplierBqCommentByBqId(bqItemId, SecurityLibrary.getLoggedInUserTenantId());
				break;
			case RFI:
				break;
			case RFP:
				commentList = rfpSupplierCommentService.getSupplierCommentsByBqId(bqItemId, SecurityLibrary.getLoggedInUserTenantId());
				break;
			case RFQ:
				commentList = rfqSupplierCommentService.getSupplierCommentsByBqId(bqItemId, SecurityLibrary.getLoggedInUserTenantId());
				break;
			case RFT:
				commentList = rftSupplierCommentService.getSupplierBqCommentByBqId(bqItemId, SecurityLibrary.getLoggedInUserTenantId());
				break;
			default:
				break;
			}

		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
			header.add("error", "Error showing Remarks : " + e.getMessage());
			return new ResponseEntity<List<?>>(commentList, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<?>>(commentList, header, HttpStatus.OK);
	}

	@RequestMapping(path = "/removeSupplierBQRemarks/{eventType}/{remarkId}", method = RequestMethod.POST)
	public ResponseEntity<List<?>> removeSupplierBQRemarks(@PathVariable("eventType") RfxTypes eventType, @PathVariable("remarkId") String remarkId) throws JsonProcessingException {
		HttpHeaders header = new HttpHeaders();
		List<?> comment = null;
		try {

			switch (eventType) {
			case RFA:
				comment = rfaSupplierCommentService.deleteSupplierBqComment(remarkId, SecurityLibrary.getLoggedInUserTenantId());
				break;
			case RFI:
				break;
			case RFP:
				comment = rfpSupplierCommentService.deleteSupplierBqComment(remarkId, SecurityLibrary.getLoggedInUserTenantId());
				break;
			case RFQ:
				comment = rfqSupplierCommentService.deleteSupplierBqComment(remarkId, SecurityLibrary.getLoggedInUserTenantId());
				break;
			case RFT:
				comment = rftSupplierCommentService.deleteSupplierBqComment(remarkId, SecurityLibrary.getLoggedInUserTenantId());
				break;
			default:
				break;
			}

		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
			header.add("error", "Error removing Remarks : " + e.getMessage());
			return new ResponseEntity<List<?>>(comment, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		header.add("success", "Successfully removed comments");
		return new ResponseEntity<List<?>>(comment, header, HttpStatus.OK);
	}

	@RequestMapping(path = "viewSupplierCq/{eventType}/{eventId}", method = RequestMethod.GET)
	public String viewSupplierCq(Model model, @PathVariable("eventType") RfxTypes eventType, @PathVariable("eventId") String eventId) {

		EventSupplier eventSupplier = null;
		EventPermissions eventPermissions = null;
		List<CqPojo> eventCqs = null;
		switch (eventType) {
		case RFA: {
			List<RfaSupplierCq> cqList = new ArrayList<RfaSupplierCq>();
			eventSupplier = rfaEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
			if (eventSupplier == null || (eventSupplier != null && (eventSupplier.getSupplierEventReadTime() == null || eventSupplier.getRejectedTime() == null) && (eventSupplier.getRejectedTime() != null && eventSupplier.getIsRejectedAfterStart() == Boolean.FALSE))) {
				return "redirect:/400_error";
			}
			eventCqs = rfaCqService.findEventForCqPojoByEventId(eventId);
			eventPermissions = rfaEventSupplierService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), eventId);
			model.addAttribute("event", rfaEventService.loadEventPojoForSummeryDetailPageForSupplierById(eventId));
			AuctionRules auctionRules = rfaEventService.getAuctionRulesByEventId(eventId);
			model.addAttribute("auctionRules", auctionRules);
			eventSupplier = rfaEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);

			for (CqPojo cq : eventCqs) {
				RfaSupplierCq rfaSupplierCq = rfaSupplierCqService.findSupplierCqByEventIdAndSupplierId(SecurityLibrary.getLoggedInUserTenantId(), eventId, cq.getId());
				cqList.add(rfaSupplierCq);
			}
			model.addAttribute("cqList", cqList);
			break;
		}
		case RFI: {
			List<RfiSupplierCq> cqList = new ArrayList<RfiSupplierCq>();
			eventSupplier = rfiEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
			if (eventSupplier == null || (eventSupplier != null && (eventSupplier.getSupplierEventReadTime() == null || eventSupplier.getRejectedTime() == null) && (eventSupplier.getRejectedTime() != null && eventSupplier.getIsRejectedAfterStart() == Boolean.FALSE))) {
				return "redirect:/400_error";
			}
			eventCqs = rfiCqService.findEventForCqPojoByEventId(eventId);
			eventPermissions = rfiEventSupplierService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), eventId);
			model.addAttribute("event", rfiEventService.loadEventPojoForSummeryDetailPageForSupplierById(eventId));
			for (CqPojo cq : eventCqs) {
				RfiSupplierCq rfiSupplierCq = rfiSupplierCqService.findSupplierCqByEventIdAndSupplierId(SecurityLibrary.getLoggedInUserTenantId(), eventId, cq.getId());
				cqList.add(rfiSupplierCq);
			}
			model.addAttribute("cqList", cqList);
			break;
		}
		case RFP: {
			List<RfpSupplierCq> cqList = new ArrayList<RfpSupplierCq>();
			eventSupplier = rfpEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
			if (eventSupplier == null || (eventSupplier != null && (eventSupplier.getSupplierEventReadTime() == null || eventSupplier.getRejectedTime() == null) && (eventSupplier.getRejectedTime() != null && eventSupplier.getIsRejectedAfterStart() == Boolean.FALSE))) {
				return "redirect:/400_error";
			}
			eventCqs = rfpCqService.findEventForCqPojoByEventId(eventId);
			eventPermissions = rfpEventSupplierService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), eventId);
			model.addAttribute("event", rfpEventService.loadEventPojoForSummeryDetailPageForSupplierById(eventId));
			for (CqPojo cq : eventCqs) {
				RfpSupplierCq rfpSupplierCq = rfpSupplierCqService.findSupplierCqByEventIdAndSupplierId(SecurityLibrary.getLoggedInUserTenantId(), eventId, cq.getId());
				cqList.add(rfpSupplierCq);
			}
			model.addAttribute("cqList", cqList);
			break;
		}
		case RFQ: {
			List<RfqSupplierCq> cqList = new ArrayList<RfqSupplierCq>();
			LOG.info("    Abe Aa Gya RFQ Andr   ");
			eventSupplier = rfqEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
			if (eventSupplier == null || (eventSupplier != null && (eventSupplier.getSupplierEventReadTime() == null || eventSupplier.getRejectedTime() == null) && (eventSupplier.getRejectedTime() != null && eventSupplier.getIsRejectedAfterStart() == Boolean.FALSE))) {
				return "redirect:/400_error";
			}
			eventCqs = rfqCqService.findEventForCqPojoByEventId(eventId);
			eventPermissions = rfqEventSupplierService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), eventId);
			model.addAttribute("event", rfqEventService.loadEventPojoForSummeryDetailPageForSupplierById(eventId));
			for (CqPojo cq : eventCqs) {
				RfqSupplierCq rfqSupplierCq = rfqSupplierCqService.findSupplierCqByEventIdAndSupplierId(SecurityLibrary.getLoggedInUserTenantId(), eventId, cq.getId());
				cqList.add(rfqSupplierCq);
			}
			model.addAttribute("cqList", cqList);
			break;
		}
		case RFT: {
			List<RftSupplierCq> cqList = new ArrayList<RftSupplierCq>();
			eventSupplier = rftEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
			if (eventSupplier == null || (eventSupplier != null && (eventSupplier.getSupplierEventReadTime() == null || eventSupplier.getRejectedTime() == null) && (eventSupplier.getRejectedTime() != null && eventSupplier.getIsRejectedAfterStart() == Boolean.FALSE))) {
				return "redirect:/400_error";
			}
			eventCqs = rftCqService.findEventForCqPojoByEventId(eventId);
			eventPermissions = rftEventSupplierService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), eventId);
			model.addAttribute("event", rftEventService.loadEventPojoForSummeryPageForSupplierById(eventId));
			for (CqPojo cq : eventCqs) {
				RftSupplierCq rftSupplierCq = rftSupplierCqService.findSupplierCqByEventIdAndSupplierId(SecurityLibrary.getLoggedInUserTenantId(), eventId, cq.getId());
				cqList.add(rftSupplierCq);
			}
			model.addAttribute("cqList", cqList);
			break;
		}
		default:
			break;

		}
		model.addAttribute("supplier", supplierService.findSupplierSubscriptionDetailsBySupplierId(SecurityLibrary.getLoggedInUserTenantId()));
		model.addAttribute("eventSupplier", eventSupplier);
		model.addAttribute("eventPermissions", eventPermissions);
		model.addAttribute("showCq", true);
		model.addAttribute("eventCq", true);
		model.addAttribute("eventCqs", eventCqs);

		return "viewSupplierQuestionnaire";
	}

	@RequestMapping(path = "/viewCqDetails/{eventType}/{cqId}/{eventId}", method = RequestMethod.GET)
	public String viewCqDetails(@PathVariable("eventType") RfxTypes eventType, @PathVariable("cqId") String cqId, @PathVariable("eventId") String eventId, Model model, RedirectAttributes redir) {
		LOG.info("======================started===============================>" + eventId + "===========>" + SecurityLibrary.getLoggedInUserTenantId() + "=======>" + SecurityLibrary.getLoggedInUserLoginId());
		EventPojo event = null;
		EventSupplier eventSupplier = null;
		SupplierCqItemPojo supplierCqItemPojo = new SupplierCqItemPojo();
		supplierCqItemPojo.setCqId(cqId);
		EventPermissions eventPermissions = null;
		try {
			switch (eventType) {
			case RFA: {
				event = rfaEventService.loadEventPojoForSummeryDetailPageForSupplierById(eventId);
				if (EventStatus.SUSPENDED == event.getStatus()) {
					redir.addAttribute("error", messageSource.getMessage("error.cqdetails.suspend.warning", new Object[] {}, Global.LOCALE));
					return "redirect:/supplier/viewSupplierCq/" + eventType.name() + "/" + eventId;
				}

				List<RfaSupplierCqItem> supplierCqItem = rfaSupplierCqItemService.getAllSupplierCqItemByCqId(cqId, SecurityLibrary.getLoggedInUserTenantId());
				if (CollectionUtil.isEmpty(supplierCqItem)) {
					rfaSupplierCqItemService.saveSupplierEventCq(cqId, eventId, SecurityLibrary.getLoggedInUserTenantId());
					supplierCqItem = rfaSupplierCqItemService.getAllSupplierCqItemByCqId(cqId, SecurityLibrary.getLoggedInUserTenantId());
				}

				Integer count = rfaSupplierCqService.findCountOfSupplierCqForSupplier(cqId, eventId, SecurityLibrary.getLoggedInUserTenantId());
				if (count == 0) {
					rfaSupplierCqItemService.saveSupplierCq(cqId, eventId, SecurityLibrary.getLoggedInUserTenantId());
				}

				List<SupplierCqItem> list = new ArrayList<SupplierCqItem>();
				for (RfaSupplierCqItem item : supplierCqItem) {
					List<String> docIds = new ArrayList<String>();
					SupplierCqItem itemObj = new SupplierCqItem(item);

					if (item.getCqItem().getCqType() == CqType.DOCUMENT_DOWNLOAD_LINK) {
						List<RfaCqOption> rfaCqOptions = item.getCqItem().getCqOptions();
						for (RfaCqOption rfaCqOption : rfaCqOptions) {
							docIds.add(StringUtils.checkString(rfaCqOption.getValue()));
						}
						List<EventDocument> eventDocuments = rfaDocumentService.findAllRfaEventDocsByEventIdAndDocIds(event.getId(), docIds);
						itemObj.setEventDocuments(eventDocuments);
					}
					list.add(itemObj);
				}
				supplierCqItemPojo.setItemList(list);
				eventPermissions = rfaEventSupplierService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), event.getId());
				eventSupplier = rfaEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), event.getId());
				RfaSupplierCq supplierCq = rfaSupplierCqService.findSupplierCqByEventIdAndSupplierId(SecurityLibrary.getLoggedInUserTenantId(), eventId, cqId);
				model.addAttribute("supplierCqStatus", supplierCq);
				break;
			}
			case RFI: {

				event = rfiEventService.loadEventPojoForSummeryDetailPageForSupplierById(eventId);
				if (EventStatus.SUSPENDED == event.getStatus() && (SuspensionType.DELETE_NOTIFY == event.getSuspensionType() || SuspensionType.DELETE_NO_NOTIFY == event.getSuspensionType())) {
					redir.addAttribute("error", messageSource.getMessage("error.cqdetails.suspend.warning", new Object[] {}, Global.LOCALE));
					return "redirect:/supplier/viewSupplierCq/" + eventType.name() + "/" + eventId;
				}
				List<RfiSupplierCqItem> supplierCqItem = rfiSupplierCqItemService.getAllSupplierCqItemByCqId(cqId, SecurityLibrary.getLoggedInUserTenantId());
				if (CollectionUtil.isEmpty(supplierCqItem)) {
					rfiSupplierCqItemService.saveSupplierEventCq(cqId, eventId, SecurityLibrary.getLoggedInUserTenantId());
					supplierCqItem = rfiSupplierCqItemService.getAllSupplierCqItemByCqId(cqId, SecurityLibrary.getLoggedInUserTenantId());
				}

				Integer count = rfiSupplierCqService.findCountOfSupplierCqForSupplier(cqId, eventId, SecurityLibrary.getLoggedInUserTenantId());
				if (count == 0) {
					rfiSupplierCqItemService.saveSupplierCq(cqId, eventId, SecurityLibrary.getLoggedInUserTenantId());
				}

				List<SupplierCqItem> list = new ArrayList<SupplierCqItem>();
				for (RfiSupplierCqItem item : supplierCqItem) {
					List<String> docIds = new ArrayList<String>();

					SupplierCqItem itemObj = new SupplierCqItem(item);
					if (item.getCqItem().getCqType() == CqType.DOCUMENT_DOWNLOAD_LINK) {
						List<RfiCqOption> rfiCqOptions = item.getCqItem().getCqOptions();
						for (RfiCqOption rfiCqOption : rfiCqOptions) {
							docIds.add(StringUtils.checkString(rfiCqOption.getValue()));
						}
						List<EventDocument> eventDocuments = rfiDocumentService.findAllRfiEventDocsByEventIdAndDocIds(event.getId(), docIds);
						itemObj.setEventDocuments(eventDocuments);
					}
					list.add(itemObj);
				}
				supplierCqItemPojo.setItemList(list);
				eventPermissions = rfiEventSupplierService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), event.getId());
				eventSupplier = rfiEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), event.getId());
				RfiSupplierCq supplierCq = rfiSupplierCqService.findSupplierCqByEventIdAndSupplierId(SecurityLibrary.getLoggedInUserTenantId(), eventId, cqId);
				model.addAttribute("supplierCqStatus", supplierCq);
				break;
			}
			case RFP: {
				event = rfpEventService.loadEventPojoForSummeryDetailPageForSupplierById(eventId);
				if (EventStatus.SUSPENDED == event.getStatus() && (SuspensionType.DELETE_NOTIFY == event.getSuspensionType() || SuspensionType.DELETE_NO_NOTIFY == event.getSuspensionType())) {
					redir.addAttribute("error", messageSource.getMessage("error.cqdetails.suspend.warning", new Object[] {}, Global.LOCALE));
					return "redirect:/supplier/viewSupplierCq/" + eventType.name() + "/" + eventId;
				}
				List<RfpSupplierCqItem> supplierCqItem = rfpSupplierCqItemService.getAllSupplierCqItemByCqId(cqId, SecurityLibrary.getLoggedInUserTenantId());
				if (CollectionUtil.isEmpty(supplierCqItem)) {
					rfpSupplierCqItemService.saveSupplierEventCq(cqId, eventId, SecurityLibrary.getLoggedInUserTenantId());
					supplierCqItem = rfpSupplierCqItemService.getAllSupplierCqItemByCqId(cqId, SecurityLibrary.getLoggedInUserTenantId());
				}

				Integer count = rfpSupplierCqService.findCountOfSupplierCqForSupplier(cqId, eventId, SecurityLibrary.getLoggedInUserTenantId());
				if (count == 0) {
					rfpSupplierCqItemService.saveSupplierCq(cqId, eventId, SecurityLibrary.getLoggedInUserTenantId());
				}

				List<SupplierCqItem> list = new ArrayList<SupplierCqItem>();
				for (RfpSupplierCqItem item : supplierCqItem) {
					List<String> docIds = new ArrayList<String>();
					SupplierCqItem itemObj = new SupplierCqItem(item);

					if (item.getCqItem().getCqType() == CqType.DOCUMENT_DOWNLOAD_LINK) {
						List<RfpCqOption> rfpCqOptions = item.getCqItem().getCqOptions();
						for (RfpCqOption rfpCqOption : rfpCqOptions) {
							docIds.add(StringUtils.checkString(rfpCqOption.getValue()));
						}
						List<EventDocument> eventDocuments = rfpDocumentService.findAllRfpEventDocsByEventIdAndDocIds(event.getId(), docIds);
						itemObj.setEventDocuments(eventDocuments);
					}

					list.add(itemObj);
				}
				supplierCqItemPojo.setItemList(list);
				eventPermissions = rfpEventSupplierService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), event.getId());
				eventSupplier = rfpEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), event.getId());
				RfpSupplierCq supplierCq = rfpSupplierCqService.findSupplierCqByEventIdAndSupplierId(SecurityLibrary.getLoggedInUserTenantId(), eventId, cqId);
				model.addAttribute("supplierCqStatus", supplierCq);
				break;
			}
			case RFQ: {
				event = rfqEventService.loadEventPojoForSummeryDetailPageForSupplierById(eventId);
				if (EventStatus.SUSPENDED == event.getStatus() && (SuspensionType.DELETE_NOTIFY == event.getSuspensionType() || SuspensionType.DELETE_NO_NOTIFY == event.getSuspensionType())) {
					redir.addAttribute("error", messageSource.getMessage("error.cqdetails.suspend.warning", new Object[] {}, Global.LOCALE));
					return "redirect:/supplier/viewSupplierCq/" + eventType.name() + "/" + eventId;
				}
				List<RfqSupplierCqItem> supplierCqItem = rfqSupplierCqItemService.getAllSupplierCqItemByCqId(cqId, SecurityLibrary.getLoggedInUserTenantId());
				if (CollectionUtil.isEmpty(supplierCqItem)) {
					rfqSupplierCqItemService.saveSupplierEventCq(cqId, eventId, SecurityLibrary.getLoggedInUserTenantId());
					supplierCqItem = rfqSupplierCqItemService.getAllSupplierCqItemByCqId(cqId, SecurityLibrary.getLoggedInUserTenantId());
				}

				Integer count = rfqSupplierCqService.findCountOfSupplierCqForSupplier(cqId, eventId, SecurityLibrary.getLoggedInUserTenantId());
				if (count == 0) {
					rfqSupplierCqItemService.saveSupplierCq(cqId, eventId, SecurityLibrary.getLoggedInUserTenantId());
				}

				List<SupplierCqItem> list = new ArrayList<SupplierCqItem>();
				for (RfqSupplierCqItem item : supplierCqItem) {
					List<String> docIds = new ArrayList<String>();
					SupplierCqItem itemObj = new SupplierCqItem(item);

					if (item.getCqItem().getCqType() == CqType.DOCUMENT_DOWNLOAD_LINK) {
						List<RfqCqOption> rfqCqOptions = item.getCqItem().getCqOptions();
						for (RfqCqOption rfqCqOption : rfqCqOptions) {
							docIds.add(StringUtils.checkString(rfqCqOption.getValue()));
						}
						List<EventDocument> eventDocuments = rfqDocumentService.findAllRfqEventDocsByEventIdAndDocIds(event.getId(), docIds);
						itemObj.setEventDocuments(eventDocuments);
					}

					list.add(itemObj);
				}
				supplierCqItemPojo.setItemList(list);
				List<RfqEventDocument> eventDocuments = rfqDocumentService.findAllEventdocsbyEventId(event.getId());
				model.addAttribute("eventDocs", eventDocuments);
				eventPermissions = rfqEventSupplierService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), event.getId());
				eventSupplier = rfqEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), event.getId());
				RfqSupplierCq supplierCq = rfqSupplierCqService.findSupplierCqByEventIdAndSupplierId(SecurityLibrary.getLoggedInUserTenantId(), eventId, cqId);
				model.addAttribute("supplierCqStatus", supplierCq);
				break;
			}
			case RFT: {
				event = rftEventService.loadEventPojoForSummeryPageForSupplierById(eventId);
				if (EventStatus.SUSPENDED == event.getStatus() && (SuspensionType.DELETE_NOTIFY == event.getSuspensionType() || SuspensionType.DELETE_NO_NOTIFY == event.getSuspensionType())) {
					redir.addAttribute("error", messageSource.getMessage("error.cqdetails.suspend.warning", new Object[] {}, Global.LOCALE));
					return "redirect:/supplier/viewSupplierCq/" + eventType.name() + "/" + eventId;
				}
				List<RftSupplierCqItem> supplierCqItem = rftSupplierCqItemService.getAllSupplierCqItemByCqId(cqId, SecurityLibrary.getLoggedInUserTenantId());
				if (CollectionUtil.isEmpty(supplierCqItem)) {
					rftSupplierCqItemService.saveSupplierEventCq(cqId, eventId, SecurityLibrary.getLoggedInUserTenantId());
					supplierCqItem = rftSupplierCqItemService.getAllSupplierCqItemByCqId(cqId, SecurityLibrary.getLoggedInUserTenantId());
				}

				Integer count = rftSupplierCqService.findCountOfSupplierCqForSupplier(cqId, eventId, SecurityLibrary.getLoggedInUserTenantId());
				if (count == 0) {
					rftSupplierCqItemService.saveSupplierCq(cqId, eventId, SecurityLibrary.getLoggedInUserTenantId());
				}

				List<SupplierCqItem> list = new ArrayList<SupplierCqItem>();
				for (RftSupplierCqItem item : supplierCqItem) {
					List<String> docIds = new ArrayList<String>();
					SupplierCqItem itemObj = new SupplierCqItem(item);

					if (item.getCqItem().getCqType() == CqType.DOCUMENT_DOWNLOAD_LINK) {
						List<RftCqOption> rftCqOptions = item.getCqItem().getCqOptions();
						for (RftCqOption rftCqOption : rftCqOptions) {
							docIds.add(StringUtils.checkString(rftCqOption.getValue()));
						}
						List<EventDocument> eventDocuments = rftDocumentService.findAllRftEventDocsByEventIdAndDocIds(event.getId(), docIds);
						itemObj.setEventDocuments(eventDocuments);
					}
					list.add(itemObj);
				}
				supplierCqItemPojo.setItemList(list);
				eventPermissions = rftEventSupplierService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), event.getId());
				eventSupplier = rftEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), event.getId());
				RftSupplierCq supplierCq = rftSupplierCqService.findSupplierCqByEventIdAndSupplierId(SecurityLibrary.getLoggedInUserTenantId(), eventId, cqId);
				model.addAttribute("supplierCqStatus", supplierCq);
				break;
			}
			default:
				break;

			}

		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
			// model.addAttribute("error", "Error fetching Questionnaire details : " +
			// e.getMessage());
			model.addAttribute("error", messageSource.getMessage("error.fetching.cqdetails", new Object[] { e.getMessage() }, Global.LOCALE));
		}
		model.addAttribute("supplier", supplierService.findSupplierSubscriptionDetailsBySupplierId(SecurityLibrary.getLoggedInUserTenantId()));
		model.addAttribute("eventSupplier", eventSupplier);
		model.addAttribute("eventPermissions", eventPermissions);
		model.addAttribute("event", event);
		model.addAttribute("supplierCqItemPojo", supplierCqItemPojo);
		model.addAttribute("cqItemList", supplierCqItemPojo);
		// model.addAttribute("accepted", ((eventSupplier != null &&
		// eventSupplier.getSupplierEventReadTime() != null &&
		// ((eventType != RfxTypes.RFA ? event.getEventStart().before(new
		// Date()) :
		// event.getEventPublishDate().before(new Date())))) ? true : false));
		model.addAttribute("showCq", false);
		model.addAttribute("eventCq", true); // Fixed to highlight the CQ tab -
												// @Nitin Otageri

		LOG.info("======================Ended===============================>" + eventId + "===========>" + SecurityLibrary.getLoggedInUserTenantId() + "=======>" + SecurityLibrary.getLoggedInUserLoginId());
		return "viewSupplierQuestionnaire";
	}

	@RequestMapping(path = "/saveCq/{eventType}/{eventId}", method = RequestMethod.POST)
	public String submitCqItems(@ModelAttribute("supplierCqItemPojo") SupplierCqItemPojo supplierCqItemPojo, Model model, @PathVariable("eventType") RfxTypes eventType, @PathVariable("eventId") String eventId, RedirectAttributes redir) throws IOException {
		LOG.info("Event Type : " + eventType.name() + " Event Id : " + eventId);
		try {
			switch (eventType) {
			case RFA:
				if (supplierCqItemPojo.getItemList() != null) {
					List<SupplierCqItem> itemList = supplierCqItemPojo.getItemList();
					LOG.info("   itemList   :" + itemList);
					if (CollectionUtil.isNotEmpty(itemList)) {
						List<RfaSupplierCqItem> list = new ArrayList<RfaSupplierCqItem>();
						for (SupplierCqItem item : itemList) {
							item.setEvent(rfaEventService.getRfaEventByeventId(item.getEvent().getId()));
							item.setCq(rfaCqService.getCqById(item.getCq().getId()));
							item.setCqItem(rfaCqService.getCqItembyCqItemId(item.getCqItem().getId()));
							RfaSupplierCqItem obj = new RfaSupplierCqItem(item);

							RfaSupplierCqItem suppItem = rfaSupplierCqItemService.findCqByEventIdAndCqName(eventId, item.getId());
							if ((item.getAttachment() == null || (item.getAttachment() != null && item.getAttachment().getBytes().length == 0)) && StringUtils.checkString(suppItem.getFileName()).length() > 0) {
								obj.setFileData(suppItem.getFileData());
								obj.setFileName(suppItem.getFileName());
								obj.setCredContentType(suppItem.getCredContentType());
							}
							list.add(obj);
						}
						LOG.info("list   :" + list);
						rfaSupplierCqItemService.updateCqItems(list, eventId, SupplierCqStatus.COMPLETED, SecurityLibrary.getLoggedInUser());

						redir.addFlashAttribute("success", "Questionnaire completed successfully");
					}
					break;
				}
			case RFI:
				if (supplierCqItemPojo.getItemList() != null) {
					List<SupplierCqItem> itemList = supplierCqItemPojo.getItemList();
					if (CollectionUtil.isNotEmpty(itemList)) {
						List<RfiSupplierCqItem> list = new ArrayList<RfiSupplierCqItem>();
						for (SupplierCqItem item : itemList) {
							item.setEvent(rfiEventService.getRfiEventByeventId(item.getEvent().getId()));
							item.setCq(rfiCqService.getCqById(item.getCq().getId()));
							item.setCqItem(rfiCqService.getCqItembyCqItemId(item.getCqItem().getId()));
							RfiSupplierCqItem suppItem = rfiSupplierCqItemService.findCqByEventIdAndCqItem(eventId, item.getId());
							LOG.info("suppItem : " + suppItem.getFileName() + " item.getId() : " + item.getId() + "  Attachment  : " + ((item.getAttachment() != null && item.getAttachment().getBytes() != null) ? item.getAttachment().getBytes().length : " No Attachment"));

							RfiSupplierCqItem obj = new RfiSupplierCqItem(item);
							if ((item.getAttachment() == null || (item.getAttachment() != null && item.getAttachment().getBytes().length == 0)) && StringUtils.checkString(suppItem.getFileName()).length() > 0) {
								obj.setFileData(suppItem.getFileData());
								obj.setFileName(suppItem.getFileName());
								obj.setCredContentType(suppItem.getCredContentType());
							}
							list.add(obj);
						}
						rfiSupplierCqItemService.updateCqItems(list, eventId, SupplierCqStatus.COMPLETED, SecurityLibrary.getLoggedInUser());
						redir.addFlashAttribute("success", "Questionnaire completed successfully");
					}
					break;
				}
			case RFP:
				if (supplierCqItemPojo.getItemList() != null) {
					List<SupplierCqItem> itemList = supplierCqItemPojo.getItemList();
					if (CollectionUtil.isNotEmpty(itemList)) {
						List<RfpSupplierCqItem> list = new ArrayList<RfpSupplierCqItem>();
						for (SupplierCqItem item : itemList) {
							item.setEvent(rfpEventService.getRfpEventByeventId(item.getEvent().getId()));
							item.setCq(rfpCqService.getCqById(item.getCq().getId()));
							item.setCqItem(rfpCqService.getCqItembyCqItemId(item.getCqItem().getId()));
							RfpSupplierCqItem obj = new RfpSupplierCqItem(item);
							RfpSupplierCqItem suppItem = rfpSupplierCqItemService.findCqByEventIdAndCqName(eventId, item.getId());
							if ((item.getAttachment() == null || (item.getAttachment() != null && item.getAttachment().getBytes().length == 0)) && StringUtils.checkString(suppItem.getFileName()).length() > 0) {
								obj.setFileData(suppItem.getFileData());
								obj.setFileName(suppItem.getFileName());
								obj.setCredContentType(suppItem.getCredContentType());
							}

							list.add(obj);
						}
						rfpSupplierCqItemService.updateCqItems(list, eventId, SupplierCqStatus.COMPLETED, SecurityLibrary.getLoggedInUser());
						redir.addFlashAttribute("success", "Questionnaire completed successfully");
					}
					break;
				}
			case RFQ:
				if (supplierCqItemPojo.getItemList() != null) {
					List<SupplierCqItem> itemList = supplierCqItemPojo.getItemList();
					if (CollectionUtil.isNotEmpty(itemList)) {
						List<RfqSupplierCqItem> list = new ArrayList<RfqSupplierCqItem>();
						for (SupplierCqItem item : itemList) {
							item.setEvent(rfqEventService.getRfqEventByeventId(item.getEvent().getId()));
							item.setCq(rfqCqService.getCqById(item.getCq().getId()));
							item.setCqItem(rfqCqService.getCqItembyCqItemId(item.getCqItem().getId()));
							RfqSupplierCqItem obj = new RfqSupplierCqItem(item);
							RfqSupplierCqItem suppItem = rfqSupplierCqItemService.findCqByEventIdAndCqName(eventId, item.getId());
							if ((item.getAttachment() == null || (item.getAttachment() != null && item.getAttachment().getBytes().length == 0)) && StringUtils.checkString(suppItem.getFileName()).length() > 0) {
								obj.setFileData(suppItem.getFileData());
								obj.setFileName(suppItem.getFileName());
								obj.setCredContentType(suppItem.getCredContentType());
							}
							list.add(obj);
						}
						rfqSupplierCqItemService.updateCqItems(list, eventId, SupplierCqStatus.COMPLETED, SecurityLibrary.getLoggedInUser());
						redir.addFlashAttribute("success", "Questionnaire completed successfully");
					}
					break;
				}
			case RFT:
				if (supplierCqItemPojo.getItemList() != null) {
					List<SupplierCqItem> itemList = supplierCqItemPojo.getItemList();
					if (CollectionUtil.isNotEmpty(itemList)) {
						List<RftSupplierCqItem> list = new ArrayList<RftSupplierCqItem>();
						for (SupplierCqItem item : itemList) {
							item.setEvent(rftEventService.getRftEventByeventId(item.getEvent().getId()));
							item.setCq(rftCqService.getRftCqById(item.getCq().getId()));
							item.setCqItem(rftCqService.getCqItembyCqItemId(item.getCqItem().getId()));
							RftSupplierCqItem obj = new RftSupplierCqItem(item);
							RftSupplierCqItem suppItem = rftSupplierCqItemService.findCqByEventIdAndCqName(eventId, item.getId());
							if ((item.getAttachment() == null || (item.getAttachment() != null && item.getAttachment().getBytes().length == 0)) && StringUtils.checkString(suppItem.getFileName()).length() > 0) {
								obj.setFileData(suppItem.getFileData());
								obj.setFileName(suppItem.getFileName());
								obj.setCredContentType(suppItem.getCredContentType());
							}

							list.add(obj);
						}
						rftSupplierCqItemService.updateCqItems(list, eventId, SupplierCqStatus.COMPLETED, SecurityLibrary.getLoggedInUser());
						redir.addFlashAttribute("success", "Questionnaire completed successfully");
					}
					break;
				}
			default:
				break;
			}
		} catch (NotAllowedException e) {
			LOG.error("Error saving Questionnaire : " + e.getMessage(), e);
			redir.addAttribute("error", e.getMessage());
			return "redirect:/supplier/viewCqDetails/" + eventType.name() + "/" + supplierCqItemPojo.getCqId() + "/" + eventId;
		} catch (Exception e) {
			LOG.error("Error saving Questionnaire : " + e.getMessage(), e);
			redir.addAttribute("error", "Error saving Questionnaire : " + e.getMessage());
		}

		return "redirect:/supplier/viewSupplierCq/" + eventType.name() + "/" + eventId;
	}

	@RequestMapping(path = "/resetAttachment/{eventType}/{eventId}/{itemId}", method = RequestMethod.POST)
	public ResponseEntity<Boolean> resetAttachment(@PathVariable("eventType") RfxTypes eventType, @PathVariable("eventId") String eventId, @PathVariable("itemId") String itemId) {
		HttpHeaders headers = new HttpHeaders();
		boolean removed = false;
		try {
			switch (eventType) {
			case RFA:
				removed = rfaSupplierCqItemService.resetAttachement(itemId, eventId, SecurityLibrary.getLoggedInUserTenantId());

				break;
			case RFI:
				removed = rfiSupplierCqItemService.resetAttachement(itemId, eventId, SecurityLibrary.getLoggedInUserTenantId());
				break;
			case RFP:
				removed = rfpSupplierCqItemService.resetAttachement(itemId, eventId, SecurityLibrary.getLoggedInUserTenantId());
				break;
			case RFQ:
				removed = rfqSupplierCqItemService.resetAttachement(itemId, eventId, SecurityLibrary.getLoggedInUserTenantId());
				break;
			case RFT:
				removed = rftSupplierCqItemService.resetAttachement(itemId, eventId, SecurityLibrary.getLoggedInUserTenantId());
				break;
			default:
				break;

			}
		} catch (Exception e) {
			LOG.error("Error during reset of attachment : " + e.getMessage(), e);
			headers.add("error", "Error while removing attachment for Questionnaire Item");
			return new ResponseEntity<Boolean>(removed, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		headers.add("sucess", "Removed attachment for Cq Item");
		return new ResponseEntity<Boolean>(removed, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/downloadAttachment/{eventType}/{eventId}/{itemId}", method = RequestMethod.GET)
	public void downloadAttachment(@PathVariable("eventType") RfxTypes eventType, @PathVariable("eventId") String eventId, @PathVariable("itemId") String itemId, HttpServletResponse response) {
		switch (eventType) {
		case RFA:
			try {
				RfaSupplierCqItem cqItem = rfaSupplierCqItemService.findCqByEventIdAndCqName(eventId, itemId);
				response.setContentType(cqItem.getCredContentType());
				response.setContentLength(cqItem.getFileData().length);
				response.setHeader("Content-Disposition", "attachment; filename=\"" + cqItem.getFileName() + "\"");
				FileCopyUtils.copy(cqItem.getFileData(), response.getOutputStream());
				response.flushBuffer();
				response.setStatus(HttpServletResponse.SC_OK);
			} catch (Exception e) {
				LOG.error("Error while File downloaded Company Profie : " + e.getMessage(), e);
			}
			break;
		case RFI:
			try {
				RfiSupplierCqItem cqItem = rfiSupplierCqItemService.findCqByEventIdAndCqItem(eventId, itemId);
				response.setContentType(cqItem.getCredContentType());
				response.setContentLength(cqItem.getFileData().length);
				response.setHeader("Content-Disposition", "attachment; filename=\"" + cqItem.getFileName() + "\"");
				FileCopyUtils.copy(cqItem.getFileData(), response.getOutputStream());
				response.flushBuffer();
				response.setStatus(HttpServletResponse.SC_OK);
			} catch (Exception e) {
				LOG.error("Error while File downloaded Company Profie : " + e.getMessage(), e);
			}
			break;
		case RFP:
			try {
				RfpSupplierCqItem cqItem = rfpSupplierCqItemService.findCqByEventIdAndCqName(eventId, itemId);
				response.setContentType(cqItem.getCredContentType());
				response.setContentLength(cqItem.getFileData().length);
				response.setHeader("Content-Disposition", "attachment; filename=\"" + cqItem.getFileName() + "\"");
				FileCopyUtils.copy(cqItem.getFileData(), response.getOutputStream());
				response.flushBuffer();
				response.setStatus(HttpServletResponse.SC_OK);
			} catch (Exception e) {
				LOG.error("Error while File downloaded Company Profie : " + e.getMessage(), e);
			}
			break;
		case RFQ:
			try {
				RfqSupplierCqItem cqItem = rfqSupplierCqItemService.findCqByEventIdAndCqName(eventId, itemId);
				response.setContentType(cqItem.getCredContentType());
				response.setContentLength(cqItem.getFileData().length);
				response.setHeader("Content-Disposition", "attachment; filename=\"" + cqItem.getFileName() + "\"");
				FileCopyUtils.copy(cqItem.getFileData(), response.getOutputStream());
				response.flushBuffer();
				response.setStatus(HttpServletResponse.SC_OK);
			} catch (Exception e) {
				LOG.error("Error while File downloaded Company Profie : " + e.getMessage(), e);
			}
			break;
		case RFT:
			try {
				RftSupplierCqItem cqItem = rftSupplierCqItemService.findCqByEventIdAndCqName(eventId, itemId);
				response.setContentType(cqItem.getCredContentType());
				response.setContentLength(cqItem.getFileData().length);
				response.setHeader("Content-Disposition", "attachment; filename=\"" + cqItem.getFileName() + "\"");
				FileCopyUtils.copy(cqItem.getFileData(), response.getOutputStream());
				response.flushBuffer();
				response.setStatus(HttpServletResponse.SC_OK);
			} catch (Exception e) {
				LOG.error("Error while File downloaded Company Profie : " + e.getMessage(), e);
			}
			break;
		default:
			break;
		}
	}

	@RequestMapping(path = "/addTeamMemberToList/{eventType}", method = RequestMethod.POST)
	public ResponseEntity<List<EventTeamMember>> addTeamMemberToList(@PathVariable("eventType") RfxTypes eventType, @RequestParam(value = "eventId") String eventId, @RequestParam(value = "userId") String userId, @RequestParam(value = "memberType") TeamMemberType memberType) {
		LOG.info("addTeamMemberToList :" + "eventType: " + eventType + " eventId: " + eventId + " userId: " + userId + " memberType: " + memberType);
		HttpHeaders headers = new HttpHeaders();
		List<EventTeamMember> teamMembers = null;
		try {
			if (userId != null) {
				switch (eventType) {
				case RFA:
					rfaEventSupplierService.addTeamMemberToList(eventId, userId, SecurityLibrary.getLoggedInUserTenantId(), memberType);
					teamMembers = rfaEventSupplierService.getSupplierTeamMembersForEvent(eventId, SecurityLibrary.getLoggedInUserTenantId());
					break;
				case RFI:
					rfiEventSupplierService.addTeamMemberToList(eventId, userId, SecurityLibrary.getLoggedInUserTenantId(), memberType);
					teamMembers = rfiEventSupplierService.getSupplierTeamMembersForEvent(eventId, SecurityLibrary.getLoggedInUserTenantId());
					break;
				case RFP:
					rfpEventSupplierService.addTeamMemberToList(eventId, userId, SecurityLibrary.getLoggedInUserTenantId(), memberType);
					teamMembers = rfpEventSupplierService.getSupplierTeamMembersForEvent(eventId, SecurityLibrary.getLoggedInUserTenantId());

					break;
				case RFQ:
					rfqEventSupplierService.addTeamMemberToList(eventId, userId, SecurityLibrary.getLoggedInUserTenantId(), memberType);
					teamMembers = rfqEventSupplierService.getSupplierTeamMembersForEvent(eventId, SecurityLibrary.getLoggedInUserTenantId());
					break;
				case RFT:
					rftEventSupplierService.addTeamMemberToList(eventId, userId, SecurityLibrary.getLoggedInUserTenantId(), memberType);
					teamMembers = rftEventSupplierService.getSupplierTeamMembersForEvent(eventId, SecurityLibrary.getLoggedInUserTenantId());
					break;
				default:
					break;
				}
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

	@SuppressWarnings("unchecked")
	@RequestMapping(path = "/removeTeamMemberfromList/{eventType}", method = RequestMethod.POST)
	public ResponseEntity<List<User>> removeTeamMemberfromList(@PathVariable("eventType") RfxTypes eventType, @RequestParam(value = "eventId") String eventId, @RequestParam(value = "userId") String userId) {
		LOG.info("removeTeamMemberfromList :" + "eventType: " + eventType + " eventId: " + eventId + " userId: " + userId);

		HttpHeaders headers = new HttpHeaders();
		LOG.info("userId Call");
		List<User> assignedTeamMembers = new ArrayList<>();
		List<?> teamMembers = new ArrayList<>();

		try {
			switch (eventType) {
			case RFA:
				rfaEventSupplierService.removeTeamMemberfromList(eventId, userId, SecurityLibrary.getLoggedInUserTenantId());
				teamMembers = rfaEventSupplierService.getRfaSupplierTeamMembersForEvent(eventId, SecurityLibrary.getLoggedInUserTenantId());

				for (RfaSupplierTeamMember rfaTeamMember : (List<RfaSupplierTeamMember>) teamMembers) {
					try {
						assignedTeamMembers.add((User) rfaTeamMember.getUser().clone());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				break;
			case RFI:
				rfiEventSupplierService.removeTeamMembersfromList(eventId, userId, SecurityLibrary.getLoggedInUserTenantId());
				teamMembers = rfiEventSupplierService.getRfiSupplierTeamMembersForEvent(eventId, SecurityLibrary.getLoggedInUserTenantId());

				for (RfiSupplierTeamMember rfiTeamMember : (List<RfiSupplierTeamMember>) teamMembers) {
					try {
						assignedTeamMembers.add((User) rfiTeamMember.getUser().clone());
					} catch (CloneNotSupportedException e) {
						e.printStackTrace();
					}
				}
				break;
			case RFP:
				rfpEventSupplierService.removeTeamMemberfromList(eventId, userId, SecurityLibrary.getLoggedInUserTenantId());
				teamMembers = rfpEventSupplierService.getRfpSupplierTeamMembersForEvent(eventId, SecurityLibrary.getLoggedInUserTenantId());

				for (RfpSupplierTeamMember rfpTeamMember : (List<RfpSupplierTeamMember>) teamMembers) {
					try {
						assignedTeamMembers.add((User) rfpTeamMember.getUser().clone());
					} catch (CloneNotSupportedException e) {
						e.printStackTrace();
					}
				}
				break;
			case RFQ:
				rfqEventSupplierService.removeTeamMemberfromList(eventId, userId, SecurityLibrary.getLoggedInUserTenantId());
				teamMembers = rfqEventSupplierService.getRfqSupplierTeamMembersForEvent(eventId, SecurityLibrary.getLoggedInUserTenantId());

				for (RfqSupplierTeamMember rfqTeamMember : (List<RfqSupplierTeamMember>) teamMembers) {
					try {
						assignedTeamMembers.add((User) rfqTeamMember.getUser().clone());
					} catch (CloneNotSupportedException e) {
						e.printStackTrace();
					}
				}
				break;
			case RFT:
				rftEventSupplierService.removeTeamMemberfromList(eventId, userId, SecurityLibrary.getLoggedInUserTenantId());
				teamMembers = rftEventSupplierService.getRftSupplierTeamMembersForEvent(eventId, SecurityLibrary.getLoggedInUserTenantId());

				for (RftSupplierTeamMember rftTeamMember : (List<RftSupplierTeamMember>) teamMembers) {
					try {
						assignedTeamMembers.add((User) rftTeamMember.getUser().clone());
					} catch (CloneNotSupportedException e) {
						e.printStackTrace();
					}
				}
				break;
			default:
				break;
			}

		} catch (Exception e) {
			LOG.error("Error While removing Team Member users : " + e.getMessage(), e);
			headers.add("error", "Error While removing Team Member users : " + e.getMessage());
			return new ResponseEntity<List<User>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		List<User> userTeamMember = new ArrayList<User>();

		List<User> userTeamMemberList = userService.fetchAllActiveUsersForTenant(SecurityLibrary.getLoggedInUserTenantId());
		for (User user : userTeamMemberList) {
			try {
				userTeamMember.add((User) user.clone());
			} catch (Exception e) {
				LOG.info("Error while cloning the user List :" + e.getMessage());
			}
		}
		userTeamMember.removeAll(assignedTeamMembers);
		return new ResponseEntity<List<User>>(userTeamMember, headers, HttpStatus.OK);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(path = "/viewSupplierTeam/{eventType}/{eventId}", method = RequestMethod.GET)
	public String viewSupplierTeamMembers(@PathVariable(name = "eventType") RfxTypes eventType, @PathVariable String eventId, Model model) {
		LOG.info(" viewSupplierEvent supplierEvent called");
		if (StringUtils.checkString(eventId).length() == 0) {
			return "redirect:/400_error";
		}
		EventPojo eventPojo = null;
		List<User> assignedTeamMembers = new ArrayList<>();
		List<?> teamMembers = new ArrayList<>();
		EventSupplier eventSupplier = null;
		EventPermissions eventPermissions = null;
		switch (eventType) {
		case RFA:
			eventPojo = rfaEventService.loadEventPojoForSummeryDetailPageForSupplierById(eventId);
			eventSupplier = rfaEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
			teamMembers = rfaEventSupplierService.getRfaSupplierTeamMembersForEvent(eventId, SecurityLibrary.getLoggedInUserTenantId());
			for (RfaSupplierTeamMember rfaTeamMember : (List<RfaSupplierTeamMember>) teamMembers) {
				try {
					assignedTeamMembers.add((User) rfaTeamMember.getUser().clone());
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
			}
			eventPermissions = rfaEventSupplierService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), eventId);
			break;
		case RFI:
			eventPojo = rfiEventService.loadEventPojoForSummeryDetailPageForSupplierById(eventId);
			eventSupplier = rfiEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
			teamMembers = rfiEventSupplierService.getRfiSupplierTeamMembersForEvent(eventId, SecurityLibrary.getLoggedInUserTenantId());

			for (RfiSupplierTeamMember rfiTeamMember : (List<RfiSupplierTeamMember>) teamMembers) {
				try {
					assignedTeamMembers.add((User) rfiTeamMember.getUser().clone());
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
			}
			eventPermissions = rfiEventSupplierService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), eventId);
			break;
		case RFP:
			eventPojo = rfpEventService.loadEventPojoForSummeryDetailPageForSupplierById(eventId);
			eventSupplier = rfpEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
			teamMembers = rfpEventSupplierService.getRfpSupplierTeamMembersForEvent(eventId, SecurityLibrary.getLoggedInUserTenantId());

			for (RfpSupplierTeamMember rfpTeamMember : (List<RfpSupplierTeamMember>) teamMembers) {
				try {
					assignedTeamMembers.add((User) rfpTeamMember.getUser().clone());
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
			}
			eventPermissions = rfpEventSupplierService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), eventId);
			break;
		case RFQ:
			eventPojo = rfqEventService.loadEventPojoForSummeryDetailPageForSupplierById(eventId);
			eventSupplier = rfqEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
			teamMembers = rfqEventSupplierService.getRfqSupplierTeamMembersForEvent(eventId, SecurityLibrary.getLoggedInUserTenantId());

			for (RfqSupplierTeamMember rfqTeamMember : (List<RfqSupplierTeamMember>) teamMembers) {
				try {
					assignedTeamMembers.add((User) rfqTeamMember.getUser().clone());
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
			}
			eventPermissions = rfqEventSupplierService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), eventId);
			break;
		case RFT:
			eventPojo = rftEventService.loadEventPojoForSummeryPageForSupplierById(eventId);
			eventSupplier = rftEventSupplierService.findSupplierByIdAndEventId(SecurityLibrary.getLoggedInUserTenantId(), eventId);
			teamMembers = rftEventSupplierService.getRftSupplierTeamMembersForEvent(eventId, SecurityLibrary.getLoggedInUserTenantId());

			for (RftSupplierTeamMember rftTeamMember : (List<RftSupplierTeamMember>) teamMembers) {
				try {
					assignedTeamMembers.add((User) rftTeamMember.getUser().clone());
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
			}
			eventPermissions = rftEventSupplierService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), eventId);
			break;
		default:
			break;
		}

		model.addAttribute("supplier", supplierService.findSupplierSubscriptionDetailsBySupplierId(SecurityLibrary.getLoggedInUserTenantId()));
		model.addAttribute("eventSupplier", eventSupplier);
		model.addAttribute("eventPermissions", eventPermissions);
		model.addAttribute("event", eventPojo);
		List<User> userTeamMemberList = userService.fetchAllActiveUsersForTenant(SecurityLibrary.getLoggedInUserTenantId());
		userTeamMemberList.removeAll(assignedTeamMembers);
		model.addAttribute("userTeamMemberList", userTeamMemberList);
		model.addAttribute("teamMembers", teamMembers);
		model.addAttribute("eventTeam", true);
		return "viewSupplierTeam";
	}

	@RequestMapping(value = "/downloadMessageAttachment/{eventType}/{messageId}", method = RequestMethod.GET)
	public void downloadMessageAttachment(@PathVariable(name = "eventType") RfxTypes eventType, @PathVariable String messageId, HttpServletResponse response) {
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

	@RequestMapping(value = "/downloadEventMeetingDocument/{eventType}/{meetingId}", method = RequestMethod.GET)
	public void downloadEventMeetingDocument(@PathVariable String meetingId, @PathVariable RfxTypes eventType, HttpServletResponse response) throws IOException {
		try {
			EventMeetingDocument docs = null;
			switch (eventType) {
			case RFA:
				docs = (RfaEventMeetingDocument) rfaMeetingService.getRfaEventMeetingDocument(meetingId);
				break;
			case RFI:
				docs = (RfiEventMeetingDocument) rfiMeetingService.getRfiEventMeetingDocument(meetingId);
				break;
			case RFP:
				docs = (RfpEventMeetingDocument) rfpMeetingService.getRfpEventMeetingDocument(meetingId);
				break;
			case RFQ:
				docs = (RfqEventMeetingDocument) rfqMeetingService.getEventMeetingDocument(meetingId);
				break;
			case RFT:
				docs = (RftEventMeetingDocument) rftMeetingService.getRftEventMeetingDocument(meetingId);
				break;
			default:
				break;
			}
			response.setContentType(docs.getCredContentType());
			response.setContentLength(docs.getFileData().length);
			response.setHeader("Content-Disposition", "attachment; filename=\"" + docs.getFileName() + "\"");
			FileCopyUtils.copy(docs.getFileData(), response.getOutputStream());
			response.flushBuffer();
			response.setStatus(HttpServletResponse.SC_OK);

		} catch (Exception e) {
			LOG.error("Error while downloaded supplier event meeting Document : " + e.getMessage(), e);
		}
	}

	private boolean validateAuctionRulesForOwnPrice(String eventId, BigDecimal totalAmountForValidation, AuctionRules auctionRules) {
		// RfaEvent rfaEvent = rfaEventService.getLeanEventbyEventId(eventId);

		if (auctionRules.getPreBidBy() == PreBidByType.BUYER) {
			if (auctionRules.getIsPreBidHigherPrice()) {
				BigDecimal lastAmount = rfaSupplierBqService.getLastTotalBqAmountBySupplierId(eventId, SecurityLibrary.getLoggedInUserTenantId());
				LOG.info("Current Amount : " + totalAmountForValidation + " : Last Amount : " + lastAmount);
				int res;
				if (auctionRules.getFowardAuction()) {
					res = totalAmountForValidation.compareTo(lastAmount);
				} else {
					res = lastAmount.compareTo(totalAmountForValidation);
				}
				LOG.info("Res value : " + res);
				if (res == -1) {
					return false;
				}
			}
		}
		return true;

	}

	private boolean validateAuctionRulesForOtherSuppliers(String eventId, BigDecimal totalAmountForValidation, AuctionRules auctionRules) {
		LOG.info("rfaEventSupplierService.checkAnySupplierSubmited(eventId) : " + rfaEventSupplierService.checkAnySupplierSubmited(eventId));
		if (!auctionRules.getIsPreBidSameBidPrice() && rfaEventSupplierService.checkAnySupplierSubmited(eventId)) {
			Integer countBidSup = rfaSupplierBqService.getCountsOfSamePreBidBySupliers(eventId, totalAmountForValidation, SecurityLibrary.getLoggedInUserTenantId());
			LOG.info("Count Bid Supplier: " + countBidSup);
			if (countBidSup != 0) {
				LOG.info("the pre bids are same for supplier (Update)..........................2");
				return false;
			}
		}
		return true;

	}

	@RequestMapping(value = "/getEventBqForResetValue", method = RequestMethod.POST)
	public ResponseEntity<SupplierBqItemResponsePojo> getEventBqForResetValue(@RequestParam("eventBqId") String eventBqId, @RequestParam("rfteventId") String rfteventId, @RequestParam("eventType") RfxTypes eventType, @RequestParam("searchVal") String searchVal, @RequestParam("pageNo") Integer pageNo, @RequestParam("pageLength") Integer pageLength) {
		HttpHeaders header = new HttpHeaders();
		List<?> supplierBqItem = null;
		long totalBqItemCount = 1;
		SupplierBqItemResponsePojo bqItemResponsePojo = new SupplierBqItemResponsePojo();
		List<BqItemPojo> leveLOrderList = null;
		try {
			// LOG.info(" getEventBqForResetValue called " + eventBqId + "
			// eventType :" + eventType);

			switch (eventType) {
			case RFP:
				leveLOrderList = rfpSupplierBqItemService.getAllLevelOrderBqItemByBqId(eventBqId, SecurityLibrary.getLoggedInUserTenantId(), searchVal);
				bqItemResponsePojo.setLevelOrderList(leveLOrderList);
				supplierBqItem = rfpSupplierBqItemService.getBqItemForSearchFilterForSupplier(eventBqId, SecurityLibrary.getLoggedInUserTenantId(), searchVal, pageNo, pageLength, null, null);
				bqItemResponsePojo.setSupplierBqItemList(supplierBqItem);
				LOG.info(" setSupplierBqItemList :" + bqItemResponsePojo.getSupplierBqItemList().size());
				totalBqItemCount = rfpSupplierBqItemService.totalBqItemCountByBqId(eventBqId, SecurityLibrary.getLoggedInUserTenantId(), searchVal);
				bqItemResponsePojo.setTotalBqItemCount(totalBqItemCount);
				LOG.info("RFP totalBqItemCount :" + totalBqItemCount);
				break;
			case RFA:
				leveLOrderList = rfaSupplierBqItemService.getAllLevelOrderBqItemByBqId(eventBqId, SecurityLibrary.getLoggedInUserTenantId(), searchVal);
				bqItemResponsePojo.setLevelOrderList(leveLOrderList);
				supplierBqItem = rfaSupplierBqItemService.getBqItemForSearchFilterForSupplier(eventBqId, SecurityLibrary.getLoggedInUserTenantId(), searchVal, pageNo, pageLength, null, null);
				bqItemResponsePojo.setSupplierBqItemList(supplierBqItem);
				LOG.info(" setSupplierBqItemList :" + bqItemResponsePojo.getSupplierBqItemList().size());
				totalBqItemCount = rfaSupplierBqItemService.totalBqItemCountByBqId(eventBqId, SecurityLibrary.getLoggedInUserTenantId(), searchVal);
				bqItemResponsePojo.setTotalBqItemCount(totalBqItemCount);
				LOG.info("RFA totalBqItemCount :" + totalBqItemCount);
				break;
			case RFI:
				break;
			case RFQ:
				leveLOrderList = rfqSupplierBqItemService.getAllLevelOrderBqItemByBqId(eventBqId, SecurityLibrary.getLoggedInUserTenantId(), searchVal);
				bqItemResponsePojo.setLevelOrderList(leveLOrderList);
				supplierBqItem = rfqSupplierBqItemService.getBqItemForSearchFilterForSupplier(eventBqId, SecurityLibrary.getLoggedInUserTenantId(), searchVal, pageNo, pageLength, null, null);
				bqItemResponsePojo.setSupplierBqItemList(supplierBqItem);
				LOG.info(" setSupplierBqItemList :" + bqItemResponsePojo.getSupplierBqItemList().size());
				totalBqItemCount = rfqSupplierBqItemService.totalBqItemCountByBqId(eventBqId, SecurityLibrary.getLoggedInUserTenantId(), searchVal);
				bqItemResponsePojo.setTotalBqItemCount(totalBqItemCount);
				LOG.info("RFQ totalBqItemCount :" + totalBqItemCount);
				break;
			case RFT:
				leveLOrderList = rftSupplierBqItemService.getAllLevelOrderBqItemByBqId(eventBqId, SecurityLibrary.getLoggedInUserTenantId(), searchVal);
				bqItemResponsePojo.setLevelOrderList(leveLOrderList);
				supplierBqItem = rftSupplierBqItemService.getBqItemForSearchFilterForSupplier(eventBqId, SecurityLibrary.getLoggedInUserTenantId(), searchVal, pageNo, pageLength, null, null);
				bqItemResponsePojo.setSupplierBqItemList(supplierBqItem);
				LOG.info(" setSupplierBqItemList :" + bqItemResponsePojo.getSupplierBqItemList().size());
				totalBqItemCount = rftSupplierBqItemService.totalBqItemCountByBqId(eventBqId, SecurityLibrary.getLoggedInUserTenantId(), searchVal);
				bqItemResponsePojo.setTotalBqItemCount(totalBqItemCount);
				LOG.info("RFT totalBqItemCount :" + totalBqItemCount);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
			header.add("error", "Error loading BQ : " + e.getMessage());
			return new ResponseEntity<SupplierBqItemResponsePojo>(bqItemResponsePojo, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<SupplierBqItemResponsePojo>(bqItemResponsePojo, HttpStatus.OK);
	}

	@RequestMapping(path = "/getBqItemForSearchFilterForSupplier", method = RequestMethod.POST)
	public ResponseEntity<SupplierBqItemResponsePojo> getBqItemForSearchFilter(@RequestParam("eventBqId") String eventBqId, @RequestParam("rfteventId") String rfteventId, @RequestParam("eventType") RfxTypes eventType, @RequestParam(name = "searchVal", required = false) String searchVal, @RequestParam("pageNo") Integer pageNo, @RequestParam(name = "pageLength", required = false) Integer pageLength, @RequestParam(name = "choosenSection", required = false) String choosenSection) {

		HttpHeaders header = new HttpHeaders();
		List<?> supplierBqItem = null;
		long totalBqItemCount = 1;
		SupplierBqItemResponsePojo bqItemResponsePojo = new SupplierBqItemResponsePojo();
		List<BqItemPojo> leveLOrderList = null;
		try {
			LOG.info(" getBqItemForSearchFilterForSupplier  : " + eventBqId + " eventId :" + rfteventId + " eventType :" + eventType + " pageLength :" + pageLength + " pageno :" + pageNo + " choosenSection :" + choosenSection);
			Integer itemLevel = null;
			Integer itemOrder = null;

			if (StringUtils.checkString(choosenSection).length() > 0 && StringUtils.checkString(choosenSection).length() == 1) {
				itemLevel = Integer.parseInt(choosenSection);
				itemOrder = 0;
			}
			if (StringUtils.checkString(choosenSection).length() > 0) {
				itemLevel = 0;
				itemOrder = 0;
				String[] values = choosenSection.split("\\.");
				itemLevel = Integer.parseInt(values[0]);
				itemOrder = Integer.parseInt(values[1]);
			}
			LOG.info(" itemOrder : " + itemOrder + " itemLevel :" + itemLevel);

			switch (eventType) {
			case RFP:
				leveLOrderList = rfpSupplierBqItemService.getAllLevelOrderBqItemByBqId(eventBqId, SecurityLibrary.getLoggedInUserTenantId(), searchVal);
				bqItemResponsePojo.setLevelOrderList(leveLOrderList);
				LOG.info("itemLevel :" + itemLevel + " itemOrder :" + itemOrder);
				supplierBqItem = rfpSupplierBqItemService.getBqItemForSearchFilterForSupplier(eventBqId, SecurityLibrary.getLoggedInUserTenantId(), searchVal, pageNo, pageLength, itemLevel, itemOrder);
				bqItemResponsePojo.setSupplierBqItemList(supplierBqItem);
				LOG.info(" setSupplierBqItemList :" + bqItemResponsePojo.getSupplierBqItemList().size());
				LOG.info(" setSupplierBqItemList :" + bqItemResponsePojo.getSupplierBqItemList().size());
				totalBqItemCount = rfpSupplierBqItemService.totalBqItemCountByBqId(eventBqId, SecurityLibrary.getLoggedInUserTenantId(), searchVal);
				bqItemResponsePojo.setTotalBqItemCount(totalBqItemCount);
				LOG.info("RFP totalBqItemCount :" + totalBqItemCount);
				break;
			case RFA:
				leveLOrderList = rfaSupplierBqItemService.getAllLevelOrderBqItemByBqId(eventBqId, SecurityLibrary.getLoggedInUserTenantId(), searchVal);
				bqItemResponsePojo.setLevelOrderList(leveLOrderList);
				supplierBqItem = rfaSupplierBqItemService.getBqItemForSearchFilterForSupplier(eventBqId, SecurityLibrary.getLoggedInUserTenantId(), searchVal, pageNo, pageLength, itemLevel, itemOrder);
				bqItemResponsePojo.setSupplierBqItemList(supplierBqItem);
				totalBqItemCount = rfaSupplierBqItemService.totalBqItemCountByBqId(eventBqId, SecurityLibrary.getLoggedInUserTenantId(), searchVal);
				bqItemResponsePojo.setTotalBqItemCount(totalBqItemCount);
				LOG.info("RFA totalBqItemCount :" + totalBqItemCount);
				break;
			case RFI:
				break;
			case RFQ:
				leveLOrderList = rfqSupplierBqItemService.getAllLevelOrderBqItemByBqId(eventBqId, SecurityLibrary.getLoggedInUserTenantId(), searchVal);
				bqItemResponsePojo.setLevelOrderList(leveLOrderList);
				supplierBqItem = rfqSupplierBqItemService.getBqItemForSearchFilterForSupplier(eventBqId, SecurityLibrary.getLoggedInUserTenantId(), searchVal, pageNo, pageLength, itemLevel, itemOrder);
				bqItemResponsePojo.setSupplierBqItemList(supplierBqItem);
				totalBqItemCount = rfqSupplierBqItemService.totalBqItemCountByBqId(eventBqId, SecurityLibrary.getLoggedInUserTenantId(), searchVal);
				bqItemResponsePojo.setTotalBqItemCount(totalBqItemCount);
				LOG.info("RFQ totalBqItemCount :" + totalBqItemCount);
				break;
			case RFT:
				leveLOrderList = rftSupplierBqItemService.getAllLevelOrderBqItemByBqId(eventBqId, SecurityLibrary.getLoggedInUserTenantId(), searchVal);
				bqItemResponsePojo.setLevelOrderList(leveLOrderList);
				// LOG.info("itemLevel :"+itemLevel + " itemOrder :"
				// +itemOrder);
				supplierBqItem = rftSupplierBqItemService.getBqItemForSearchFilterForSupplier(eventBqId, SecurityLibrary.getLoggedInUserTenantId(), searchVal, pageNo, pageLength, itemLevel, itemOrder);
				bqItemResponsePojo.setSupplierBqItemList(supplierBqItem);
				// LOG.info(" setSupplierBqItemList
				// :"+bqItemResponsePojo.getSupplierBqItemList().size());
				totalBqItemCount = rftSupplierBqItemService.totalBqItemCountByBqId(eventBqId, SecurityLibrary.getLoggedInUserTenantId(), searchVal);
				bqItemResponsePojo.setTotalBqItemCount(totalBqItemCount);
				LOG.info("RFT totalBqItemCount :" + totalBqItemCount);
				break;
			default:
				break;
			}
			if (pageLength != SecurityLibrary.getLoggedInUser().getBqPageLength()) {
				userService.updateUserBqPageLength(pageLength, SecurityLibrary.getLoggedInUser().getId());
				super.updateSecurityLibraryUser(pageLength);
			}
		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
			header.add("error", "Error loading BQ : " + e.getMessage());
			return new ResponseEntity<SupplierBqItemResponsePojo>(bqItemResponsePojo, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<SupplierBqItemResponsePojo>(bqItemResponsePojo, HttpStatus.OK);
	}

	@RequestMapping(path = "/supplierPoList", method = RequestMethod.GET)
	public String poList(Model model) {
		//ph-4113
		long orderedPoCount = supplierService.findCountOfPoForSupplierBasedOnStatus(SecurityLibrary.getLoggedInUserTenantId(), PoStatus.ORDERED);
		long acceptedPoCount = supplierService.findCountOfPoForSupplierBasedOnStatus(SecurityLibrary.getLoggedInUserTenantId(), PoStatus.ACCEPTED);
		long declinedPoCount = supplierService.findCountOfPoForSupplierBasedOnStatus(SecurityLibrary.getLoggedInUserTenantId(), PoStatus.DECLINED);
		long suspendedPoCount = supplierService.findCountOfPoForSupplierBasedOnStatus(SecurityLibrary.getLoggedInUserTenantId(), PoStatus.SUSPENDED);
		long closedPoCount = supplierService.findCountOfPoForSupplierBasedOnStatus(SecurityLibrary.getLoggedInUserTenantId(), PoStatus.CLOSED);
		long cancelledPoCount = supplierService.findCountOfPoForSupplierBasedOnStatus(SecurityLibrary.getLoggedInUserTenantId(), PoStatus.CANCELLED);

		model.addAttribute("orderedPoCount",orderedPoCount);
		model.addAttribute("acceptedPoCount",acceptedPoCount);

		model.addAttribute("declinedPoCount",declinedPoCount);
		model.addAttribute("suspendedPoCount",suspendedPoCount);
		model.addAttribute("closedPoCount",closedPoCount);
		model.addAttribute("cancelledPoCount",cancelledPoCount);

		return "supplierPoList";
	}

	@RequestMapping(path = "/poListData", method = RequestMethod.GET)
	public ResponseEntity<TableData<PoSupplierPojo>> poData(TableDataInput input, @RequestParam(required = false) String dateTimeRange,String status, HttpSession session, HttpServletResponse response) {
		try {
			LOG.info("Supplier Id :" + SecurityLibrary.getLoggedInUserTenantId() + " user id : " + SecurityLibrary.getLoggedInUser().getId()+" Status : "+status);



			String startDate = null;
			String endDate = null;
			Date sDate = null;
			Date eDate = null;

			if (StringUtils.checkString(dateTimeRange).length() > 0) {
				String dateTimeArr[] = dateTimeRange.split("-");
				if (dateTimeArr.length == 2) {
					startDate = dateTimeArr[0].trim();
					endDate = dateTimeArr[1].trim();

					try {
						// Use the correct date format
						SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
						sDate = dateFormat.parse(startDate);
						eDate = dateFormat.parse(endDate);
						LOG.info("Start date: " + sDate + " End date: " + eDate);

					} catch (ParseException e) {
						LOG.error("Invalid date format: " + e.getMessage());
						HttpHeaders headers = new HttpHeaders();
						headers.add("error", "Invalid date format. Expected format is MM/dd/yyyy.");
						return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
					}

				} else {
					LOG.warn("Invalid dateTimeRange format: {}", dateTimeRange);
					HttpHeaders headers = new HttpHeaders();
					headers.add("error", "Invalid dateTimeRange format. Expected format is 'startDate-endDate'.");
					return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
				}
			}

			List<PoSupplierPojo> poList = supplierService.findAllSearchFilterPoForSupplier(SecurityLibrary.getLoggedInUserTenantId(), input, sDate, eDate,status);
			TableData<PoSupplierPojo> data = new TableData<PoSupplierPojo>(poList);
			data.setDraw(input.getDraw());
			long recordFiltered = supplierService.findTotalSearchFilterPoForSupplier(SecurityLibrary.getLoggedInUserTenantId(), input, sDate, eDate,status);
			long totalCount = supplierService.findTotalPoForSupplier(SecurityLibrary.getLoggedInUserTenantId());
			LOG.info(" totalCount : " + totalCount);
			data.setRecordsFiltered(recordFiltered);
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<PoSupplierPojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching Po List For Supplier: " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching Po List For Supplier : " + e.getMessage());
			return new ResponseEntity<TableData<PoSupplierPojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/supplierPrView/{poId}", method = RequestMethod.GET)
	public String supplierPrView(@PathVariable String poId, Model model, HttpServletRequest request, RedirectAttributes redir) {
		LOG.info("Supplier View GET called By po id :" + poId);
		try {
			constructPoSummaryAttributesForSupplierView(poId, model);
			model.addAttribute("financeCompanys", financeCompanyService.searchFinanceCompany(FinanceCompanyStatus.ACTIVE.toString(), "Newest", null));
		} catch (Exception e) {
			LOG.info("Error in view Po For Supplier:" + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("error.view.pr.supplier", new Object[] { e.getMessage() }, Global.LOCALE));
		}
		return "supplierPrView";
	}

	public Po constructPoSummaryAttributesForSupplierView(String poId, Model model) {
		Po po = supplierService.getPoByIdForSupplierView(poId);
		if (PoStatus.REVISE == po.getStatus() || (Boolean.TRUE == po.getRevised() && StringUtils.checkString(po.getRevisePoDetails()).length() > 0)) {
			LOG.info("PO is in Revise status.");
			ObjectMapper mapper = new ObjectMapper();
			try {
				PoReviseSnapshot poReviseSnapshot = mapper.readValue(po.getRevisePoDetails(), PoReviseSnapshot.class);
				if (poReviseSnapshot != null) {
					po.setRequester(poReviseSnapshot.getRequester());
					po.setDeliveryDate(poReviseSnapshot.getDeliveryDate());
					po.setDeliveryReceiver(poReviseSnapshot.getDeliveryReceiver());
					po.setDeliveryAddressTitle(poReviseSnapshot.getDeliveryAddressTitle());
					po.setDeliveryAddressLine1(poReviseSnapshot.getDeliveryAddressLine1());
					po.setDeliveryAddressLine2(poReviseSnapshot.getDeliveryAddressLine2());
					po.setDeliveryAddressCity(poReviseSnapshot.getDeliveryAddressCity());
					po.setDeliveryAddressState(poReviseSnapshot.getDeliveryAddressState());
					po.setDeliveryAddressZip(poReviseSnapshot.getDeliveryAddressZip());
					po.setDeliveryAddressCountry(poReviseSnapshot.getDeliveryAddressCountry());
					po.setTotal(poReviseSnapshot.getTotal());
					po.setTaxDescription(poReviseSnapshot.getTaxDescription());
					po.setAdditionalTax(poReviseSnapshot.getAdditionalTax());
					po.setGrandTotal(poReviseSnapshot.getGrandTotal());

					po.setStatus(PoStatus.REVISE);

					if (CollectionUtil.isNotEmpty(poReviseSnapshot.getPoItems())) {
						List<PoItem> finalList = new ArrayList<PoItem>();
						for (PoRevisedSnapshotItem rItem : poReviseSnapshot.getPoItems()) {
							PoItem item = new PoItem();
							item.setItemName(rItem.getItemName());
							item.setItemCode(rItem.getItemCode());
							item.setLevel(rItem.getLevel());
							item.setOrder(rItem.getOrder());
							item.setQuantity(rItem.getQuantity());
							item.setLockedQuantity(rItem.getLockedQuantity());
							item.setBalanceQuantity(rItem.getBalanceQuantity());
							item.setUnitPrice(rItem.getUnitPrice());
							item.setPricePerUnit(rItem.getPricePerUnit());
							item.setItemDescription(rItem.getItemDescription());
							item.setItemTax(rItem.getItemTax());
							item.setTotalAmount(rItem.getTotalAmount());
							item.setTaxAmount(rItem.getTaxAmount());
							item.setTotalAmountWithTax(rItem.getTotalAmountWithTax());
							if (StringUtils.checkString(rItem.getUnit()).length() > 0) {
								Uom uom = uomService.getUomById(rItem.getUnit());
								if (uom != null) {
									uom.setBuyer(null);
									uom.setCreatedBy(null);
									uom.setModifiedBy(null);
									uom.setOwner(null);
								}
								item.setUnit(uom);
							}
							item.setField1(rItem.getField1());
							item.setField2(rItem.getField2());
							item.setField3(rItem.getField3());
							item.setField4(rItem.getField4());
							item.setField5(rItem.getField5());
							item.setField6(rItem.getField6());
							item.setField7(rItem.getField7());
							item.setField8(rItem.getField8());
							item.setField9(rItem.getField9());
							item.setField10(rItem.getField10());
							finalList.add(item);

						}
						if (CollectionUtil.isNotEmpty(finalList)) {
							po.setPoItems(finalList);
						}
					}

				} else {
					try {
						PoReviseSnapshot snapshot = new PoReviseSnapshot();
						snapshot.setId(po.getId());
						snapshot.setRequester(po.getRequester());
						snapshot.setGrandTotal(po.getGrandTotal());
						snapshot.setTaxDescription(po.getTaxDescription());
						snapshot.setAdditionalTax(po.getAdditionalTax());
						snapshot.setDeliveryAddressLine1(po.getDeliveryAddressLine1());
						snapshot.setDeliveryAddressLine2(po.getDeliveryAddressLine2());
						snapshot.setDeliveryAddressCity(po.getDeliveryAddressCity());
						snapshot.setDeliveryAddressState(po.getDeliveryAddressState());
						snapshot.setDeliveryAddressCountry(po.getDeliveryAddressCountry());
						snapshot.setDeliveryAddressTitle(po.getDeliveryAddressTitle());
						snapshot.setDeliveryAddressZip(po.getDeliveryAddressZip());
						snapshot.setDeliveryDate(po.getDeliveryDate());
						snapshot.setDeliveryReceiver(po.getDeliveryReceiver());
						if (po.getPoItems() != null) {
							List<PoRevisedSnapshotItem> items = new ArrayList<PoRevisedSnapshotItem>();
							for (PoItem poItem : po.getPoItems()) {
								PoRevisedSnapshotItem item = new PoRevisedSnapshotItem();
								item.setId(poItem.getId());
								item.setField1(poItem.getField1());
								item.setField2(poItem.getField2());
								item.setField3(poItem.getField3());
								item.setField4(poItem.getField4());
								item.setField5(poItem.getField5());
								item.setField6(poItem.getField6());
								item.setField7(poItem.getField7());
								item.setField8(poItem.getField8());
								item.setField9(poItem.getField9());
								item.setField10(poItem.getField10());
								item.setItemCode(poItem.getItemCode());
								item.setItemDescription(poItem.getItemDescription());
								item.setItemName(poItem.getItemName());
								item.setItemTax(poItem.getItemTax());
								item.setLevel(poItem.getLevel());
								item.setOrder(poItem.getOrder());
								item.setQuantity(poItem.getQuantity());
								item.setLockedQuantity(poItem.getLockedQuantity());
								item.setBalanceQuantity(poItem.getBalanceQuantity());
								item.setTaxAmount(poItem.getTaxAmount());
								item.setTotalAmount(poItem.getTotalAmount());
								item.setTotalAmountWithTax(poItem.getTotalAmountWithTax());
								item.setUnit(poItem.getUnit() != null ? poItem.getUnit().getUom() : "");
								item.setUnitPrice(poItem.getUnitPrice());
								item.setPricePerUnit(poItem.getPricePerUnit());

								items.add(item);
							}
							snapshot.setPoItems(items);
						}
						if (CollectionUtil.isNotEmpty(po.getPurchaseOrderDocuments())) {
							for (PurchaseOrderDocument doc : po.getPurchaseOrderDocuments()) {
								com.privasia.procurehere.core.entity.POSnapshotDocument docSnapShot = new com.privasia.procurehere.core.entity.POSnapshotDocument();
								docSnapShot.setId(doc.getId());
								docSnapShot.setCredContentType(doc.getCredContentType());
								docSnapShot.setDescription(doc.getDescription());
								docSnapShot.setFileData(doc.getFileData());
								docSnapShot.setFileName(doc.getFileName());
								docSnapShot.setFileSizeInKb(doc.getFileSizeInKb());
								docSnapShot.setInternal(doc.getInternal());
								docSnapShot.setPo(po);
								docSnapShot.setUploadDate(doc.getUploadDate());
								poService.saveOrUpdatePoSnapShotDocument(docSnapShot);
							}
						}

						String json = mapper.writeValueAsString(snapshot);
						poService.updatePoRevisedSnapshot(po.getId(), json);
					} catch (Exception e) {
						LOG.error("Error while converting to json " + e.getMessage(), e);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		LOG.info("-----------------------------------" + poId);
		FinancePo financePo = poFinanceService.getPoFinanceForSupplier(po.getId(), SecurityLibrary.getLoggedInUserTenantId());
		if (financePo != null) {
			LOG.info("if block for");
			model.addAttribute("requestDate", financePo.getRequestedDate());
			model.addAttribute("shareDate", financePo.getSharedDate());
			model.addAttribute("isShare", true);
			if (financePo.getFinancePoType() == FinancePoType.REQUESTED) {
				model.addAttribute("isRequest", true);
			} else
				model.addAttribute("isRequest", false);

			model.addAttribute("financeCompanyName", financePo.getFinanceCompany().getCompanyName());
			LOG.info(" finance company details -----------------------------------------------------" + financePo.toString());
		} else {
			model.addAttribute("isRequest", false);
			model.addAttribute("isShare", false);
		}

		// Check if Both Buyer and Supplier are onboarded in the Finanshere Platform
		boolean onboarded = false;
		long supplierCount = invoiceFinanceRequestService.findOnboardedSupplierForFinancingRequest(SecurityLibrary.getLoggedInUserTenantId());
		if (supplierCount > 0) {
			long buyerCount = invoiceFinanceRequestService.findOnboardedBuyerForInvoiceRequest(po.getBuyer().getId());
			if (buyerCount > 0) {
				onboarded = true;
			}
		}
		model.addAttribute("onboarded", onboarded);

		if (onboarded) {
			PoFinanceRequestPojo poFinanceRequest = invoiceFinanceRequestService.getPoFinanceRequestPojoByPoId(po.getId());
			model.addAttribute("poFinanceRequest", poFinanceRequest);

			if (poFinanceRequest != null) {
				List<PoFinanceRequestDocumentsPojo> docList = invoiceFinanceRequestService.getPoFinanceRequestDocumentsForRequestForSupplier(poFinanceRequest.getId());
				model.addAttribute("poFinanceRequestDocs", docList);
			}
		}

		model.addAttribute("po", po);
		List<PoItem> poItemlist = poService.findAllPoItemByPoIdForSummary(poId);
		LOG.info("poItemlist:" + poItemlist.size());
		model.addAttribute("poItemlist", poItemlist);
		List<PoAudit> poAuditList = poAuditService.getPoAuditByPoIdForSupplier(po.getId());
		model.addAttribute("poAuditList", poAuditList);

		List<DoSupplierPojo> dos = deliveryOrderService.getDosByPoId(poId);
		model.addAttribute("dos", dos);
		List<InvoiceSupplierPojo> invoices = invoiceService.getInvoicesByPoId(poId);
		model.addAttribute("invoices", invoices);
		if (PoStatus.REVISE == po.getStatus()) {
			model.addAttribute("listDocs", poService.findAllSnapshotPlainPoDocsbyPoIdForSupplier(poId));
		} else {
			model.addAttribute("listDocs", poService.findAllPlainPoDocsbyPoIdForSupplier(poId));
		}


		if (PoStatus.ORDERED == po.getStatus() || PoStatus.ACCEPTED == po.getStatus() || PoStatus.DECLINED == po.getStatus() || PoStatus.CANCELLED == po.getStatus()) {
			// PH 4113 SHOW PO Event for mailbox
			// PH 4113 SHOW PO Event for mailbox
			model.addAttribute("eventType", "PO");

			List<PoEvent> poEvents = poEventService.findEventByPoId(poId);
			PoEvent poEvent = new PoEvent();

			if (poEvents.isEmpty()) {
				poEvent.setEventName(po.getName());
				poEvent.setCreatedBy(po.getCreatedBy());
				poEvent.setTenantId(po.getCreatedBy().getTenantId());
				poEvent.setCreatedDate(new Date());
				poEvent.setEventOwner(po.getCreatedBy());
				poEvent.setEnableApprovalReminder(Boolean.FALSE);
				poEvent.setNotifyEventOwner(Boolean.TRUE);
				poEvent.setStatus(EventStatus.ACTIVE);
				poEvent.setPo(po);
				poEvent = poEventService.save(poEvent);
				poEvents = Collections.singletonList(poEvent);
			} else {
				poEvent = poEvents.get(0);
			}

			if(poEvents.size() > 0){
				model.addAttribute("poEvent", poEvents.get(0));
			}else{
				model.addAttribute("poEvent", null);
			}

			LOG.info("Po Event LIST >>>>>>>>>>>>>>>> " + poEvents.size());
			LOG.info("Po Event Id : "+poEvents.get(0).getId());
			LOG.info("Tenant Id : "+SecurityLibrary.getLoggedInUserTenantId());
			LOG.info("User LoggedIn Id : "+SecurityLibrary.getLoggedInUser().getId());
			model.addAttribute("tenantId", SecurityLibrary.getLoggedInUserTenantId());

			HashMap<String, Object> map = new HashMap<String, Object>();
			List<PoTeamMember> teamMember =  poService.getPlainTeamMembersForPoSummary(poId);
			StringBuilder memberId=new StringBuilder();
			StringBuilder memberName=new StringBuilder();
			teamMember.forEach(teamMemberData -> {
				if(!teamMemberData.getUser().getId().equals(po.getCreatedBy().getId())) {
					memberId.append("," + teamMemberData.getUser().getId());
					memberName.append(", " + teamMemberData.getUser().getName() + " [ " + teamMemberData.getUser().getBuyer().getCompanyName() + " ] ");
				}
			});
			model.addAttribute("memberId", memberId);
			model.addAttribute("memberName", memberName);


		}
		return po;
	}

	@RequestMapping(value = "/downloadPoDocumentForSupplier/{docId}", method = RequestMethod.GET)
	public void downloadPoDocumentForSupplier(@PathVariable String docId, HttpServletResponse response) throws IOException {
		try {
			LOG.info("PO Download  For Supplier : " + docId);
			prService.downloadPoDocument(docId, response);
		} catch (Exception e) {
			LOG.error("Error while downloading PO Document For Supplier: " + e.getMessage(), e);
		}
	}

	@RequestMapping(value = "/downloadFinansHereOfferDocument/{requestId}", method = RequestMethod.GET)
	public void downloadFinansHereOfferDocument(@PathVariable String requestId, HttpServletResponse response) throws IOException {
		try {
			LOG.info("Offer Download For Supplier : " + requestId);
			invoiceFinanceRequestService.downloadFinansHereOfferDocument(requestId, response);
		} catch (Exception e) {
			LOG.error("Error while downloading PO Document For Supplier: " + e.getMessage(), e);
		}
	}

	@RequestMapping(value = "/downloadFinansHerePoDocument/{requestId}", method = RequestMethod.GET)
	public void downloadFinansHerePoDocument(@PathVariable String requestId, HttpServletResponse response) throws IOException {
		try {
			LOG.info("PO Request doc Download For Supplier : " + requestId);
			invoiceFinanceRequestService.downloadFinansHerePoDocument(requestId, response);
		} catch (Exception e) {
			LOG.error("Error while downloading PO Document For Supplier: " + e.getMessage(), e);
		}
	}

	public void buildDocumentFile(HttpServletResponse response, PrDocument docs) throws IOException {
		response.setContentType(docs.getCredContentType());
		response.setContentLength(docs.getFileData().length);
		response.setHeader("Content-Disposition", "attachment; filename=\"" + docs.getFileName() + "\"");
		FileCopyUtils.copy(docs.getFileData(), response.getOutputStream());
		response.flushBuffer();
		response.setStatus(HttpServletResponse.SC_OK);
	}

	@RequestMapping(path = "/supplierPoReport/{poId}", method = RequestMethod.GET)
	public void generatePoReport(@PathVariable("poId") String poId, HttpServletResponse response, HttpSession session) throws Exception {
		try {
			LOG.info("Supplier PO Report : " + poId);
			Po po = supplierService.getPoByIdForSupplierView(poId);
			poService.generatePoReport(response, po);
			// JasperPrint jasperPrint = supplierService.getSupplierPOSummaryPdfForDownload(po, session);
			try {
				PoAudit supplierAudit = new PoAudit();
				supplierAudit.setAction(PoAuditType.DOWNLOADED);
				supplierAudit.setActionBy(SecurityLibrary.getLoggedInUser());
				supplierAudit.setActionDate(new Date());
				supplierAudit.setBuyer(po.getBuyer());
				supplierAudit.setSupplier(SecurityLibrary.getLoggedInUser().getSupplier());
				supplierAudit.setDescription(messageSource.getMessage("po.audit.downloadPo", new Object[] { po.getPoNumber() }, Global.LOCALE));
				supplierAudit.setVisibilityType(PoAuditVisibilityType.SUPPLIER);
				supplierAudit.setPo(po);
				poAuditService.save(supplierAudit);
			} catch (Exception e) {
				LOG.error("Error while saving po audit:" + e.getMessage(), e);
			}
		} catch (Exception e) {
			LOG.error("Could not generate PR Summary Report For Supplier View. " + e.getMessage(), e);
		}
	}

	@RequestMapping(path = "/supplierAllPoReport", method = RequestMethod.GET)
	public void generatesupplierAllPoReport(HttpServletResponse response, HttpSession session) throws Exception {

		String fileName = "";
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (ZipOutputStream zos = new ZipOutputStream(baos)) {

			fileName = supplierService.generateAllPoZip(zos, response, SecurityLibrary.getLoggedInUserTenantId(), session);
			zos.close();
			response.setContentType("application/zip,application/octet-stream");
			response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
			response.getOutputStream().write(baos.toByteArray());
			response.getOutputStream().flush();
			response.getOutputStream().close();
		} catch (Exception e) {
			LOG.error("Error zipping event evaluation report for download : " + e.getMessage(), e);
		}

	}

	@RequestMapping(path = "/sharePo", method = RequestMethod.POST)
	public String sharePo(Model model, @RequestParam("poId") String poId, @RequestParam("financeCompanyId") String financeCompanyId, HttpServletResponse response, HttpSession session, RedirectAttributes redir) throws Exception {
		try {
			LOG.info("Supplier PO Share :++++++++++++++++++++++++++++++++++ " + poId);
			Po po = supplierService.getPoByIdForSupplierView(poId);
			FinanceCompany company = financeCompanyService.getFinanceCompanyById(financeCompanyId);

			// LOG.info("Finance
			// Company:++++++++++++++++++++++++++++++++++"+company.getCompanyName());
			if (po != null && company != null) {
				FinancePo financePo = new FinancePo();

				financePo.setPo(po);
				financePo.setFinanceCompany(company);
				financePo.setSupplier(SecurityLibrary.getLoggedInUser().getSupplier());
				financePo.setReferenceNum(referenceNumber(company));
				financePo.setCreatedDate(new Date());
				financePo.setFinancePoStatus(FinancePoStatus.NEW);
				financePo.setFinancePoType(FinancePoType.SHARED);
				financePo.setSharedDate(new Date());
				poFinanceService.saveFinancePo(financePo);
				sendPoShareEmailsToFinance(userService.getAdminUserForFinance(company), po);
				try {
					PoAudit poAudit = new PoAudit();
					poAudit.setAction(PoAuditType.SHARED);
					poAudit.setActionBy(SecurityLibrary.getLoggedInUser());
					poAudit.setActionDate(new Date());
					poAudit.setSupplier(SecurityLibrary.getLoggedInUser().getSupplier());
					poAudit.setDescription(messageSource.getMessage("po.supplierAudit.sharedPo", new Object[] { po.getPoNumber(), company.getCompanyName() }, Global.LOCALE));
					poAudit.setVisibilityType(PoAuditVisibilityType.SUPPLIER);
					poAudit.setPo(po);
					poAuditService.save(poAudit);
				} catch (Exception e) {
					LOG.error("Error while saving po audit:" + e.getMessage(), e);
				}

				redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.po.share.tofinance", new Object[] { company.getCompanyName() != null ? company.getCompanyName() : "" }, Global.LOCALE));
			} else {
				redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.sharing.po", new Object[] {}, Global.LOCALE));
				LOG.error("========PR empty ========");
			}

		} catch (Exception e) {
			redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.sharing.po.param", new Object[] { e.getMessage() }, Global.LOCALE));
			LOG.error("error while shareing Po " + e.getMessage(), e);
		}
		return "redirect:supplierPrView/" + poId;
	}

	@RequestMapping(path = "/requestPo", method = RequestMethod.POST)
	public String requestPo(Model model, @RequestParam("poId") String poId, @RequestParam("financeCompanyId") String financeCompanyId, HttpServletResponse response, HttpSession session, RedirectAttributes redir) throws Exception {
		try {

			LOG.info("finance Company Id" + financeCompanyId);

			Po po = supplierService.getPoByIdForSupplierView(poId);

			FinancePo financePo = poFinanceService.getPoFinanceForSupplier(poId, SecurityLibrary.getLoggedInUserTenantId());
			// LOG.info("Finance
			// Company:++++++++++++++++++++++++++++++++++"+company.getCompanyName());

			FinanceCompany financeCompany = financeCompanyService.getFinanceCompanyById(financeCompanyId);

			financeCompany.setId(financeCompanyId);
			if (financePo != null) {

				financePo.setFinancePoStatus(FinancePoStatus.NEW);
				financePo.setFinancePoType(FinancePoType.REQUESTED);
				financePo.setRequestedDate(new Date());

				financePo.setFinanceCompany(financeCompany);

				poFinanceService.updateFinancePo(financePo, SecurityLibrary.getLoggedInUser());
				if (financePo.getFinanceCompany() != null) {
					sendPoRequestedEmailsToFinance(userService.getAdminUserForFinance(financePo.getFinanceCompany()), po);
					// redir.addFlashAttribute("success", "PO Requested successfully to Finance :" +
					// financePo.getFinanceCompany().getCompanyName());
					redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.supplier.info.updated", new Object[] { financePo.getFinanceCompany().getCompanyName() != null ? financePo.getFinanceCompany().getCompanyName() : "" }, Global.LOCALE));
				} else {
					redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.requesting.po", new Object[] {}, Global.LOCALE));
				}
			} else {
				FinancePo financePo1 = new FinancePo();

				financePo1.setPo(po);
				financePo1.setFinanceCompany(financeCompany);
				financePo1.setSupplier(SecurityLibrary.getLoggedInUser().getSupplier());
				financePo1.setReferenceNum(referenceNumber(financeCompany));
				financePo1.setCreatedDate(new Date());
				financePo1.setFinancePoStatus(FinancePoStatus.NEW);
				financePo1.setFinancePoType(FinancePoType.REQUESTED);
				financePo1.setRequestedDate(new Date());
				LOG.info("EXCEPTION MSG============================");
				poFinanceService.saveFinancePo(financePo1);

			}
			try {
				PoAudit poAudit = new PoAudit();
				poAudit.setAction(PoAuditType.REQUESTED);
				poAudit.setActionBy(SecurityLibrary.getLoggedInUser());
				poAudit.setActionDate(new Date());
				poAudit.setSupplier(SecurityLibrary.getLoggedInUser().getSupplier());
				poAudit.setDescription(messageSource.getMessage("po.supplierAudit.requestedPo", new Object[] { po.getPoNumber(), financeCompany.getCompanyName() }, Global.LOCALE));
				poAudit.setVisibilityType(PoAuditVisibilityType.SUPPLIER);
				poAudit.setPo(po);
				poAuditService.save(poAudit);
			} catch (Exception e) {
				LOG.error("Error while saving po audit:" + e.getMessage(), e);
			}
		} catch (Exception e) {
			redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.sharing.po.param", new Object[] { e.getMessage() }, Global.LOCALE));
			LOG.error("error while shareing Po " + e.getMessage(), e);
		}
		return "redirect:supplierPrView/" + poId;
	}

	@RequestMapping(path = "/finanshereRequest", method = RequestMethod.POST)
	public String finanshereRequest(Model model, @RequestParam("poId") String poId, HttpServletResponse response, HttpSession session, RedirectAttributes redir) throws Exception {
		try {

			LOG.info("Requesting financing from finanshere for PO Id : " + poId);

			Po po = invoiceFinanceRequestService.requestFinancingForPo(poId, SecurityLibrary.getLoggedInUser());
			try {
				PoAudit poAudit = new PoAudit();
				poAudit.setAction(PoAuditType.REQUESTED);
				poAudit.setActionBy(SecurityLibrary.getLoggedInUser());
				poAudit.setActionDate(new Date());
				poAudit.setSupplier(SecurityLibrary.getLoggedInUser().getSupplier());
				poAudit.setDescription(messageSource.getMessage("invoice.finance.po.request.success", new Object[] { po.getPoNumber() }, Global.LOCALE));
				poAudit.setVisibilityType(PoAuditVisibilityType.SUPPLIER);
				poAudit.setPo(po);
				poAuditService.save(poAudit);
			} catch (Exception e) {
				LOG.error("Error while saving po audit:" + e.getMessage(), e);
			}
		} catch (Exception e) {
			redir.addFlashAttribute("error", messageSource.getMessage("invoice.finance.po.request.error", new Object[] { e.getMessage() }, Global.LOCALE));
			LOG.error("error while requesting financing for Po " + e.getMessage(), e);
		}
		return "redirect:supplierPrView/" + poId;
	}

	@RequestMapping(path = "/acceptFinanshereOffer", method = RequestMethod.POST)
	public String accptFinanshereOffer(Model model, @RequestParam("requestId") String requestId, @RequestParam("poId") String poId, RedirectAttributes redir) throws Exception {
		try {
			LOG.info("Accepting Offer from finanshere for Request Id : " + requestId);

			PoFinanceRequest req = invoiceFinanceRequestService.acceptFinanshereOffer(requestId, SecurityLibrary.getLoggedInUser());
			try {
				PoAudit poAudit = new PoAudit();
				poAudit.setAction(PoAuditType.ACCEPTED);
				poAudit.setActionBy(SecurityLibrary.getLoggedInUser());
				poAudit.setActionDate(new Date());
				poAudit.setSupplier(SecurityLibrary.getLoggedInUser().getSupplier());
				poAudit.setDescription("Finanshere Offer Accepted for PO No " + req.getPoNumber());
				poAudit.setVisibilityType(PoAuditVisibilityType.SUPPLIER);
				poAudit.setPo(req.getPo());
				poAuditService.save(poAudit);
			} catch (Exception e) {
				LOG.error("Error while saving po audit:" + e.getMessage(), e);
			}
		} catch (Exception e) {
			redir.addFlashAttribute("error", "Error accepting finanshere offer : " + e.getMessage());
			LOG.error("Error accepting finanshere offer : " + e.getMessage(), e);
		}
		return "redirect:supplierPrView/" + poId;
	}

	@RequestMapping(path = "/declineFinanshereOffer", method = RequestMethod.POST)
	public String declineFinanshereOffer(Model model, @RequestParam("requestId") String requestId, @RequestParam("poId") String poId, RedirectAttributes redir) throws Exception {
		try {
			LOG.info("Declining Offer from finanshere for Request Id : " + requestId);

			PoFinanceRequest req = invoiceFinanceRequestService.declineFinanshereOffer(requestId, SecurityLibrary.getLoggedInUser());
			try {
				PoAudit poAudit = new PoAudit();
				poAudit.setAction(PoAuditType.DECLINED);
				poAudit.setActionBy(SecurityLibrary.getLoggedInUser());
				poAudit.setActionDate(new Date());
				poAudit.setSupplier(SecurityLibrary.getLoggedInUser().getSupplier());
				poAudit.setDescription("Finanshere Offer Declined for PO No " + req.getPoNumber());
				poAudit.setVisibilityType(PoAuditVisibilityType.SUPPLIER);
				poAudit.setPo(req.getPo());
				poAuditService.save(poAudit);
			} catch (Exception e) {
				LOG.error("Error while saving po audit:" + e.getMessage(), e);
			}
		} catch (Exception e) {
			redir.addFlashAttribute("error", "Error declining finanshere offer : " + e.getMessage());
			LOG.error("Error declining finanshere offer : " + e.getMessage(), e);
		}
		return "redirect:supplierPrView/" + poId;
	}

	private void sendPoRequestedEmailsToFinance(User mailTo, Po po) {
		LOG.info("Sending PO share email to--------------------------------> (" + mailTo.getName() + ") : " + mailTo.getCommunicationEmail());
		try {
			String subject = "PO Request";
			String url = APP_URL + "/finance/financePOView/" + po.getId();
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("userName", mailTo.getName());
			map.put("message", "");

			map.put("pr", po);
			map.put("businessUnit", StringUtils.checkString(po.getBusinessUnit().getDisplayName()));
			map.put("prReferanceNumber", (po.getPoNumber() == null ? "" : po.getPoNumber()));
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			String timeZone = "GMT+8:00";
			timeZone = getTimeZoneByBuyerSettings(mailTo.getTenantId(), timeZone);
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("date", df.format(new Date()));
			map.put("loginUrl", APP_URL + "/login");
			map.put("appUrl", url);
			map.put("supplierName", "requested by " + SecurityLibrary.getLoggedInUser().getSupplier().getCompanyName());
			if (StringUtils.checkString(mailTo.getCommunicationEmail()).length() > 0 && mailTo.getEmailNotifications()) {
				sendEmail(mailTo.getCommunicationEmail(), subject, map, Global.FINANCEPO_SHARE_TEMPLATE);
			} else {
				LOG.warn("No communication email configured for user : " + mailTo.getLoginId() + "... Not going to send email notification");
			}

			sendDashboardNotificationForFinance(mailTo, url, subject, "New PO requested  by Supplier" + SecurityLibrary.getLoggedInUser().getSupplier().getCompanyName(), NotificationType.CREATED_MESSAGE);
		} catch (Exception e) {
			LOG.error("Error while Sending PO Request Email:" + e.getMessage(), e);
		}

	}

	private void streamReport(JasperPrint jasperPrint, String filename, HttpServletResponse response) throws JRException, IOException {
		response.setContentType("application/pdf");
		response.addHeader("Content-Disposition", "attachment; filename=" + filename);
		JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
		response.getOutputStream().flush();
		response.getOutputStream().close();
	}

	private String referenceNumber(FinanceCompany financeCompany) {
		String referenceNumber = "";
		Integer length = 6;
		String seqNo = "1";
		if (financeCompany != null) {
			FinanceCompanySettings financeCompanySettings = financeSettingsService.getFinanceSettingsByTenantId(financeCompany.getId());
			if (financeCompanySettings != null) {
				if (StringUtils.checkString(financeCompanySettings.getPoSequencePrefix()).length() > 0) {
					referenceNumber += financeCompanySettings.getPoSequencePrefix();
				}
				if (StringUtils.checkString(financeCompanySettings.getPoSequenceNumber()).length() > 0) {
					seqNo = financeCompanySettings.getPoSequenceNumber();
				}
				if (financeCompanySettings.getPoSequenceLength() != null && financeCompanySettings.getPoSequenceLength() != 0) {
					length = financeCompanySettings.getPoSequenceLength();
				}

				referenceNumber += StringUtils.lpad(seqNo, length, '0');
				LOG.info("-----Updating settings-----------");
				int sequanceNum = Integer.parseInt((seqNo)) + 1;
				financeCompanySettings.setPoSequenceNumber("" + sequanceNum);
				financeSettingsService.updateFinanceSettingsSeqNumber(financeCompanySettings);
			} else {
				LOG.info("Finance Setting is null");
			}

		} else {
			LOG.info("Finance  is null");
		}
		LOG.info("-----referenceNumber----------->" + referenceNumber);
		return referenceNumber;
	}

	private void sendPoShareEmailsToFinance(User mailTo, Po po) {

		LOG.info("Sending PO share email to--------------------------------> (" + mailTo.getName() + ") : " + mailTo.getCommunicationEmail());
		try {
			String subject = "PO Share";
			String url = APP_URL + "/finance/financePOView/" + po.getId();
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("userName", mailTo.getName());
			map.put("message", "");
			map.put("pr", po);
			map.put("businessUnit", StringUtils.checkString(po.getBusinessUnit().getDisplayName()));
			map.put("prReferanceNumber", (po.getPoNumber() == null ? "" : po.getPoNumber()));
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			String timeZone = "GMT+8:00";
			timeZone = getTimeZoneByFinanceSettings(mailTo.getTenantId(), timeZone);
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("date", df.format(new Date()));
			map.put("loginUrl", APP_URL + "/login");
			map.put("appUrl", url);
			map.put("supplierName", "shared by " + SecurityLibrary.getLoggedInUser().getSupplier().getCompanyName());
			if (StringUtils.checkString(mailTo.getCommunicationEmail()).length() > 0 && mailTo.getEmailNotifications()) {
				sendEmail(mailTo.getCommunicationEmail(), subject, map, Global.FINANCEPO_SHARE_TEMPLATE);
			} else {
				LOG.warn("No communication email configured for user : " + mailTo.getLoginId() + "... Not going to send email notification");
			}

			sendDashboardNotificationForFinance(mailTo, url, subject, "New PO shared by " + SecurityLibrary.getLoggedInUser().getSupplier().getCompanyName(), NotificationType.CREATED_MESSAGE);
		} catch (Exception e) {
			LOG.error("Error while Sending PO Created Email:" + e.getMessage(), e);
		}

	}

	private String getTimeZoneByFinanceSettings(String tenantId, String timeZone) {

		try {
			if (StringUtils.checkString(tenantId).length() > 0) {
				String time = financeSettingsService.getFinanceTimeZoneByTenantId(tenantId);
				if (time != null) {
					timeZone = time;
				}
			}
		} catch (Exception e) {
			LOG.error("Error while fetching buyer time zone :" + e.getMessage(), e);
		}
		return timeZone;

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

	private void sendEmail(String mailTo, String subject, Map<String, Object> map, String template) {
		if (StringUtils.checkString(mailTo).length() > 0) {
			try {
				notificationService.sendEmail(mailTo, subject, map, template);
			} catch (Exception e) {
				LOG.info("ERROR while Sending mail :" + e.getMessage(), e);
			}
		} else {
			LOG.warn("No communication email configured for user... Not going to send email notification");
		}
	}

	private void sendDashboardNotificationForFinance(User messageTo, String url, String subject, String notificationMessage, NotificationType notificationType) {
		FinanceNotificationMessage message = new FinanceNotificationMessage();
		message.setCreatedBy(null);
		message.setCreatedDate(new Date());
		message.setMessage(notificationMessage);
		message.setNotificationType(notificationType);
		message.setMessageTo(messageTo);
		message.setSubject(subject);
		message.setTenantId(messageTo.getTenantId());
		message.setUrl(url);
		dashboardNotificationService.saveFinanceNotification(message);
	}

	@RequestMapping(path = "/acceptPo", method = RequestMethod.POST)
	public String acceptPo(@RequestParam("poId") String poId, @RequestParam("supplierRemark") String supplierRemark, HttpServletResponse response, HttpSession session, RedirectAttributes redir) throws Exception {
		try {
			LOG.info("Supplier PO ACCEPTED :++++++++++++++++++++++++++++++++++ " + poId);
			Po po = supplierService.getPoByIdForSupplierView(poId);

			if (StringUtils.checkString(poId).length() > 0) {
				po.setStatus(PoStatus.ACCEPTED);
				po.setActionDate(new Date());
				if (StringUtils.checkString(supplierRemark).length() > 0) {
				po.setSupplierRemark(supplierRemark);
				}

				PoAudit buyerAudit = new PoAudit();
				buyerAudit.setAction(PoAuditType.ACCEPTED);
				buyerAudit.setActionBy(SecurityLibrary.getLoggedInUser());
				buyerAudit.setActionDate(new Date());
				buyerAudit.setBuyer(po.getBuyer());
				buyerAudit.setSupplier(SecurityLibrary.getLoggedInUser().getSupplier());
				buyerAudit.setDescription(messageSource.getMessage("po.buyerAudit.accepted", new Object[] {
				po.getPoNumber(), po.getSupplier().getSupplier().getCompanyName(), supplierRemark }, Global.LOCALE));
				buyerAudit.setVisibilityType(PoAuditVisibilityType.BUYER);
				buyerAudit.setPo(po);

				PoAudit supplierAudit = new PoAudit();
				supplierAudit.setAction(PoAuditType.ACCEPTED);
				supplierAudit.setActionBy(SecurityLibrary.getLoggedInUser());
				supplierAudit.setActionDate(new Date());
				supplierAudit.setBuyer(po.getBuyer());
				supplierAudit.setSupplier(SecurityLibrary.getLoggedInUser().getSupplier());
				supplierAudit.setDescription(messageSource.getMessage("po.supplierAudit.accepted", new Object[] {
				po.getPoNumber(), supplierRemark }, Global.LOCALE));
				supplierAudit.setVisibilityType(PoAuditVisibilityType.SUPPLIER);
				supplierAudit.setPo(po);

				poService.updatePoStatus(poId, PoStatus.ACCEPTED, supplierRemark, SecurityLibrary.getLoggedInUser(), null);
				Supplier supplier = supplierService.findPlainSupplierById(SecurityLibrary.getLoggedInUser().getTenantId());

				try {
					approvalService.sendPoSupplierActionEmailNotificationToBuyer(supplier, true, po, supplierRemark);
				} catch (Exception e) {
					LOG.error("Error while sending email to po creater:" + e.getMessage(), e);
				}
				approvalService.sharePoToFinance(po);

				redir.addFlashAttribute("success", messageSource.getMessage("supplier.accepted.success.po", new Object[] { po.getPoNumber() }, Global.LOCALE));

			} else {
				redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.accepting.po", new Object[] {}, Global.LOCALE));
				LOG.error("========PO empty ========");
			}

		} catch (Exception e) {
			redir.addFlashAttribute("error", messageSource.getMessage("supplier.accepting.po.error", new Object[] { e.getMessage() }, Global.LOCALE));
			LOG.error("error while accepting Po " + e.getMessage(), e);
		}
		return "redirect:supplierPrView/" + poId;
	}

	@RequestMapping(path = "/declinePo", method = RequestMethod.POST)
	public String declinePo(@RequestParam("poId") String poId, @RequestParam("supplierRemark") String supplierRemark, HttpServletResponse response, HttpSession session, RedirectAttributes redir) throws Exception {
		try {
			LOG.info("Supplier PO DECLINED :++++++++++++++++++++++++++++++++++ " + poId);
			Po po = supplierService.getPoByIdForSupplierView(poId);

			if (StringUtils.checkString(poId).length() > 0) {
				po.setStatus(PoStatus.DECLINED);
				po.setActionDate(new Date());
				if (StringUtils.checkString(supplierRemark).length() > 0) {
					po.setSupplierRemark(supplierRemark);
				}

				PoAudit buyerAudit = new PoAudit();
				buyerAudit.setAction(PoAuditType.DECLINED);
				buyerAudit.setActionBy(SecurityLibrary.getLoggedInUser());
				buyerAudit.setActionDate(new Date());
				buyerAudit.setBuyer(po.getBuyer());
				buyerAudit.setSupplier(SecurityLibrary.getLoggedInUser().getSupplier());
				buyerAudit.setDescription(messageSource.getMessage("po.buyerAudit.declined", new Object[] {
				po.getPoNumber(), po.getSupplier().getSupplier().getCompanyName(), supplierRemark }, Global.LOCALE));
				buyerAudit.setVisibilityType(PoAuditVisibilityType.BUYER);
				buyerAudit.setPo(po);

				PoAudit supplierAudit = new PoAudit();
				supplierAudit.setAction(PoAuditType.DECLINED);
				supplierAudit.setActionBy(SecurityLibrary.getLoggedInUser());
				supplierAudit.setActionDate(new Date());
				supplierAudit.setBuyer(po.getBuyer());
				supplierAudit.setSupplier(SecurityLibrary.getLoggedInUser().getSupplier());
				supplierAudit.setDescription(messageSource.getMessage("po.supplierAudit.declined", new Object[] {
				po.getPoNumber(), supplierRemark }, Global.LOCALE));
				supplierAudit.setVisibilityType(PoAuditVisibilityType.SUPPLIER);
				supplierAudit.setPo(po);
				poService.updatePoStatus(poId, PoStatus.DECLINED, supplierRemark, SecurityLibrary.getLoggedInUser(), null);

				Supplier supplier = supplierService.findPlainSupplierById(SecurityLibrary.getLoggedInUser().getTenantId());

				try {
					approvalService.sendPoSupplierActionEmailNotificationToBuyer(supplier, false, po, supplierRemark);
				} catch (Exception e) {
					LOG.error("Error while sending email to po creater:" + e.getMessage(), e);
				}

				redir.addFlashAttribute("success", messageSource.getMessage("supplier.declined.success.po", new Object[] { po.getPoNumber() }, Global.LOCALE));

			} else {
				redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.declining.po", new Object[] {}, Global.LOCALE));
				LOG.error("========PO empty ========");
			}

		} catch (Exception e) {
			redir.addFlashAttribute("error", messageSource.getMessage("supplier.declining.po.error", new Object[] { e.getMessage() }, Global.LOCALE));
			LOG.error("error while declining Po " + e.getMessage(), e);
		}
		return "redirect:supplierPrView/" + poId;
	}

	@RequestMapping(path = "/exportPoReports", method = RequestMethod.POST)
	public void downloadpoReports(HttpSession session, HttpServletRequest request, HttpServletResponse response, @RequestParam(required = false) String poIds, boolean select_all, @RequestParam String dateTimeRange, @ModelAttribute("searchFilterPoPojo") SearchFilterPoPojo searchFilterPoPojo) {
		try {
			String poArr[] = null;
			if (StringUtils.checkString(poIds).length() > 0) {
				poArr = poIds.split(",");
			}
			TableDataInput input = new TableDataInput();
			input.setStart(0);
			input.setLength(5000);

			String tenantId = SecurityLibrary.getLoggedInUserTenantId();

			LOG.info("dateTimeRange :" + dateTimeRange);
			String startDate = null;
			String endDate = null;
			Date sDate = null;
			Date eDate = null;

			if (StringUtils.checkString(dateTimeRange).length() > 0) {
				String dateTimeArr[] = dateTimeRange.split("-");
				if (dateTimeArr.length == 2) {
					startDate = dateTimeArr[0].trim();
					endDate = dateTimeArr[1].trim();

					try {
						// Use the correct date format for parsing
						SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
						sDate = dateFormat.parse(startDate);
						eDate = dateFormat.parse(endDate);
						LOG.info("Parsed Start date: " + sDate + " Parsed End date: " + eDate);

					} catch (ParseException e) {
						LOG.error("Invalid date format: " + e.getMessage());
						HttpHeaders headers = new HttpHeaders();
						headers.add("error", "Invalid date format. Expected format is MM/dd/yyyy.");
					}

				} else {
					LOG.warn("Invalid dateTimeRange format: {}", dateTimeRange);
					HttpHeaders headers = new HttpHeaders();
					headers.add("error", "Invalid dateTimeRange format. Expected format is 'startDate-endDate'.");
				}
			}

			LOG.info("searchFilterPoPojo:" + searchFilterPoPojo.getPoNumber());

			poService.downloadPoReports(tenantId, poArr, response, session, select_all, sDate, eDate, searchFilterPoPojo);

		} catch (Exception e) {
			LOG.error("Error While Filter po list :" + e.getMessage(), e);
		}

	}

	// PH-1567
	// Client(FE) initiates payment request
	@RequestMapping(value = "/initializePayment/{paymentType}/{eventType}/{eventId}", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<PaymentIntentPojo> initializeStripePayment(@PathVariable("paymentType") String paymentType, @PathVariable("eventId") String eventId, @PathVariable("eventType") RfxTypes eventType, @RequestParam(name = "email", required = false) String email) {
		HttpHeaders headers = new HttpHeaders();
		try {
			LOG.info("Supplier requested Email : " + email);
			return new ResponseEntity<PaymentIntentPojo>(supplierEventDetailService.initiateStripePayment(paymentType, eventId, eventType, email), headers, HttpStatus.OK);
		} catch (Exception e) {
			headers.add("error", "Error in initiating payment, " + e.getMessage());
			return new ResponseEntity<PaymentIntentPojo>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// Stripe webhook to handle payment responses
	@RequestMapping(value = "/paymentEvents", method = RequestMethod.POST)
	public @ResponseBody void paymentWebHooks(@RequestBody String json, HttpServletRequest request) throws SignatureVerificationException, ApplicationException {
		com.stripe.model.Event event = null;
		try {
			event = ApiResource.GSON.fromJson(json, com.stripe.model.Event.class);
			supplierEventDetailService.handlePaymentWebhookEvent(event, true);
		} catch (JsonSyntaxException e) {
			LOG.error("Unable to get stripe event details");
		}
	}

	@RequestMapping(path = "/saveAsDraftCq/{eventType}/{eventId}", method = RequestMethod.POST)
	public String saveDraftCqItems(@ModelAttribute("supplierCqItemPojo") SupplierCqItemPojo supplierCqItemPojo, Model model, @PathVariable("eventType") RfxTypes eventType, @PathVariable("eventId") String eventId, RedirectAttributes redir) throws IOException {
		LOG.info("Event Type : " + eventType.name() + " Event Id : " + eventId);
		try {
			switch (eventType) {
			case RFA:
				if (supplierCqItemPojo.getItemList() != null) {
					List<SupplierCqItem> itemList = supplierCqItemPojo.getItemList();
					LOG.info("   itemList   :" + itemList);
					if (CollectionUtil.isNotEmpty(itemList)) {
						List<RfaSupplierCqItem> list = new ArrayList<RfaSupplierCqItem>();
						for (SupplierCqItem item : itemList) {
							item.setEvent(rfaEventService.getRfaEventByeventId(item.getEvent().getId()));
							item.setCq(rfaCqService.getCqById(item.getCq().getId()));
							item.setCqItem(rfaCqService.getCqItembyCqItemId(item.getCqItem().getId()));
							RfaSupplierCqItem obj = new RfaSupplierCqItem(item);
							RfaSupplierCqItem suppItem = rfaSupplierCqItemService.findCqByEventIdAndCqName(eventId, item.getId());
							if ((item.getAttachment() == null || (item.getAttachment() != null && item.getAttachment().getBytes().length == 0)) && StringUtils.checkString(suppItem.getFileName()).length() > 0) {
								obj.setFileData(suppItem.getFileData());
								obj.setFileName(suppItem.getFileName());
								obj.setCredContentType(suppItem.getCredContentType());
							}
							list.add(obj);
						}
						LOG.info("list   :" + list);
						rfaSupplierCqItemService.draftUpdateCqItems(list, eventId, SupplierCqStatus.DRAFT, SecurityLibrary.getLoggedInUser());
						redir.addFlashAttribute("success", "Questionnaire saved successfully");
					}
					break;
				}
			case RFI:
				if (supplierCqItemPojo.getItemList() != null) {
					List<SupplierCqItem> itemList = supplierCqItemPojo.getItemList();
					if (CollectionUtil.isNotEmpty(itemList)) {
						List<RfiSupplierCqItem> list = new ArrayList<RfiSupplierCqItem>();
						for (SupplierCqItem item : itemList) {
							item.setEvent(rfiEventService.getRfiEventByeventId(item.getEvent().getId()));
							item.setCq(rfiCqService.getCqById(item.getCq().getId()));
							item.setCqItem(rfiCqService.getCqItembyCqItemId(item.getCqItem().getId()));
							RfiSupplierCqItem obj = new RfiSupplierCqItem(item);
							RfiSupplierCqItem suppItem = rfiSupplierCqItemService.findCqByEventIdAndCqItem(eventId, item.getId());
							if ((item.getAttachment() == null || (item.getAttachment() != null && item.getAttachment().getBytes().length == 0)) && StringUtils.checkString(suppItem.getFileName()).length() > 0) {
								obj.setFileData(suppItem.getFileData());
								obj.setFileName(suppItem.getFileName());
								obj.setCredContentType(suppItem.getCredContentType());
							}
							list.add(obj);
						}
						rfiSupplierCqItemService.draftUpdateCqItems(list, eventId, SupplierCqStatus.DRAFT, SecurityLibrary.getLoggedInUser());
						redir.addFlashAttribute("success", "Questionnaire saved successfully");
					}
					break;
				}
			case RFP:
				LOG.info("Event Type ---------------: " + eventType.name());
				if (supplierCqItemPojo.getItemList() != null) {
					List<SupplierCqItem> itemList = supplierCqItemPojo.getItemList();
					if (CollectionUtil.isNotEmpty(itemList)) {
						List<RfpSupplierCqItem> list = new ArrayList<RfpSupplierCqItem>();
						for (SupplierCqItem item : itemList) {
							item.setEvent(rfpEventService.getRfpEventByeventId(item.getEvent().getId()));
							item.setCq(rfpCqService.getCqById(item.getCq().getId()));
							item.setCqItem(rfpCqService.getCqItembyCqItemId(item.getCqItem().getId()));
							RfpSupplierCqItem obj = new RfpSupplierCqItem(item);
							RfpSupplierCqItem suppItem = rfpSupplierCqItemService.findCqByEventIdAndCqName(eventId, item.getId());
							if ((item.getAttachment() == null || (item.getAttachment() != null && item.getAttachment().getBytes().length == 0)) && StringUtils.checkString(suppItem.getFileName()).length() > 0) {
								obj.setFileData(suppItem.getFileData());
								obj.setFileName(suppItem.getFileName());
								obj.setCredContentType(suppItem.getCredContentType());
							}
							list.add(obj);
						}
						rfpSupplierCqItemService.draftUpdateCqItems(list, eventId, SupplierCqStatus.DRAFT, SecurityLibrary.getLoggedInUser());
						redir.addFlashAttribute("success", "Questionnaire saved successfully");
					}
					break;
				}
			case RFQ:
				if (supplierCqItemPojo.getItemList() != null) {
					List<SupplierCqItem> itemList = supplierCqItemPojo.getItemList();
					if (CollectionUtil.isNotEmpty(itemList)) {
						List<RfqSupplierCqItem> list = new ArrayList<RfqSupplierCqItem>();
						for (SupplierCqItem item : itemList) {
							item.setEvent(rfqEventService.getRfqEventByeventId(item.getEvent().getId()));
							item.setCq(rfqCqService.getCqById(item.getCq().getId()));
							item.setCqItem(rfqCqService.getCqItembyCqItemId(item.getCqItem().getId()));
							RfqSupplierCqItem obj = new RfqSupplierCqItem(item);
							RfqSupplierCqItem suppItem = rfqSupplierCqItemService.findCqByEventIdAndCqName(eventId, item.getId());
							if ((item.getAttachment() == null || (item.getAttachment() != null && item.getAttachment().getBytes().length == 0)) && StringUtils.checkString(suppItem.getFileName()).length() > 0) {
								obj.setFileData(suppItem.getFileData());
								obj.setFileName(suppItem.getFileName());
								obj.setCredContentType(suppItem.getCredContentType());
							}
							list.add(obj);
						}
						rfqSupplierCqItemService.draftUpdateCqItems(list, eventId, SupplierCqStatus.DRAFT, SecurityLibrary.getLoggedInUser());
						redir.addFlashAttribute("success", "Questionnaire saved successfully");
					}
					break;
				}
			case RFT:
				if (supplierCqItemPojo.getItemList() != null) {
					List<SupplierCqItem> itemList = supplierCqItemPojo.getItemList();
					if (CollectionUtil.isNotEmpty(itemList)) {
						List<RftSupplierCqItem> list = new ArrayList<RftSupplierCqItem>();
						for (SupplierCqItem item : itemList) {
							item.setEvent(rftEventService.getRftEventByeventId(item.getEvent().getId()));
							item.setCq(rftCqService.getRftCqById(item.getCq().getId()));
							item.setCqItem(rftCqService.getCqItembyCqItemId(item.getCqItem().getId()));
							RftSupplierCqItem obj = new RftSupplierCqItem(item);
							RftSupplierCqItem suppItem = rftSupplierCqItemService.findCqByEventIdAndCqName(eventId, item.getId());
							if ((item.getAttachment() == null || (item.getAttachment() != null && item.getAttachment().getBytes().length == 0)) && StringUtils.checkString(suppItem.getFileName()).length() > 0) {
								obj.setFileData(suppItem.getFileData());
								obj.setFileName(suppItem.getFileName());
								obj.setCredContentType(suppItem.getCredContentType());
							}

							list.add(obj);
						}
						rftSupplierCqItemService.draftUpdateCqItems(list, eventId, SupplierCqStatus.DRAFT, SecurityLibrary.getLoggedInUser());
						redir.addFlashAttribute("success", "Questionnaire saved successfully");
					}
					break;
				}
			default:
				break;
			}
		} catch (Exception e) {
			LOG.error("Error saving Questionnaire : " + e.getMessage(), e);
			redir.addAttribute("error", "Error saving Questionnaire : " + e.getMessage());
		}
		return "redirect:/supplier/viewSupplierCq/" + eventType.name() + "/" + eventId;
	}

	@RequestMapping(path = "/saveDraftSupplierBq/{eventType}/{eventId}", method = RequestMethod.POST)
	public ResponseEntity<List<?>> saveDraftSupplierBq(@PathVariable RfxTypes eventType, @PathVariable String eventId, @RequestBody List<SupplierBqItem> supplierBqItem) throws JsonProcessingException {
		LOG.info("  List supplier bq item :   " + supplierBqItem.size());
		LOG.info("  eventId : " + eventId + " total amount ");
		HttpHeaders header = new HttpHeaders();
		try {
			switch (eventType) {
			case RFA:
				rfaSupplierBqItemService.updateBqItems(supplierBqItem, SecurityLibrary.getLoggedInUserTenantId(), SupplierBqStatus.DRAFT);
				break;
			case RFP:
				rfpSupplierBqItemService.updateBqItems(supplierBqItem, SecurityLibrary.getLoggedInUserTenantId(), SupplierBqStatus.DRAFT);
				break;
			case RFQ:
				rfqSupplierBqItemService.updateBqItems(supplierBqItem, SecurityLibrary.getLoggedInUserTenantId(), SupplierBqStatus.DRAFT);
				break;
			case RFT:
				rftSupplierBqItemService.updateBqItems(supplierBqItem, SecurityLibrary.getLoggedInUserTenantId(), SupplierBqStatus.DRAFT);
				break;
			case RFI:
				break;
			default:
				break;

			}

		} catch (Exception e) {
			LOG.error("Error while drafting bq : " + e.getMessage(), e);
			header.add("error", "Error saving  Bq details : " + e.getMessage());
		}
		header.add("success", "Succesfully updated Bq details");
		return new ResponseEntity<List<?>>(header, HttpStatus.OK);
	}

	@RequestMapping(path = "/exportSupplierPoSummaryCsvReport", method = RequestMethod.POST)
	public void downloadPoCsvReport(HttpSession session, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redir, @ModelAttribute("searchFilterPoPojo") SearchFilterPoPojo searchFilterPoPojo, boolean select_all, @RequestParam String dateTimeRange) throws IOException {
		try {
			File file = File.createTempFile("poReports-", ".csv");
			LOG.info("Temp file : " + file.getAbsolutePath());

			String poIds[] = null;
			if (StringUtils.checkString(searchFilterPoPojo.getPoIds()).length() > 0) {
				poIds = searchFilterPoPojo.getPoIds().split(",");
			}

			LOG.info("dateTimeRange :" + dateTimeRange);
			String startDate = null;
			String endDate = null;
			Date sDate = null;
			Date eDate = null;

			if (StringUtils.checkString(dateTimeRange).length() > 0) {
				String dateTimeArr[] = dateTimeRange.split("-");
				if (dateTimeArr.length == 2) {
					startDate = dateTimeArr[0].trim();
					endDate = dateTimeArr[1].trim();

					try {
						// Use the correct date format for parsing
						SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
						sDate = dateFormat.parse(startDate);
						eDate = dateFormat.parse(endDate);
						LOG.info("Parsed Start date: " + sDate + " Parsed End date: " + eDate);

					} catch (ParseException e) {
						LOG.error("Invalid date format: " + e.getMessage());
						HttpHeaders headers = new HttpHeaders();
						headers.add("error", "Invalid date format. Expected format is MM/dd/yyyy.");
					}

				} else {
					LOG.warn("Invalid dateTimeRange format: {}", dateTimeRange);
					HttpHeaders headers = new HttpHeaders();
					headers.add("error", "Invalid dateTimeRange format. Expected format is 'startDate-endDate'.");
				}
			}

			poService.downloadCsvFileForPoSupplierSummary(response, file, poIds, searchFilterPoPojo, sDate, eDate, select_all, SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), session);

			// Send to UI
			response.setContentType("text/csv");
			response.addHeader("Content-Disposition", "attachment; filename=poReport.csv");
			response.setContentLengthLong(file.length());
			Files.copy(file.toPath(), response.getOutputStream());
			response.getOutputStream().flush();



		} catch (Exception e) {
			LOG.error("Error while downloading csv file : " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("PoSummary.error.download.csv", new Object[] { e.getMessage() }, Global.LOCALE));

		}
	}

	@RequestMapping(value = "/downloadPoAuditFile/{id}/{action}/{actionDate}", method = RequestMethod.GET)
	public void downloadFile(@PathVariable String id, @PathVariable String action, @PathVariable String actionDate, HttpServletResponse response) throws IOException {
		try {
			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + action + "_Audit_" + actionDate + ".pdf\"");

			PoAudit auditFile = poAuditService.getPoAuditById(id);
			if (auditFile.getSnapshot() != null && auditFile.getSnapshot().length > 0) {
				response.setContentLength(auditFile.getSnapshot().length);
				FileCopyUtils.copy(auditFile.getSnapshot(), response.getOutputStream());
			}

			response.flushBuffer();
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (Exception e) {
			LOG.error("Error while File downloaded Po Audit File : " + e.getMessage(), e);
		}
	}

	@RequestMapping(value = "/po/downloadPoDocument/{docId}", method = RequestMethod.GET)
	public void downloadPoDocument(@PathVariable String docId, HttpServletResponse response) throws IOException {
		try {
			LOG.info("PO Download  For Supplier : " + docId);
			poService.downloadPrDocument(docId, response);
		} catch (Exception e) {
			LOG.error("Error while downloading PO Document For Supplier: " + e.getMessage(), e);
		}
	}

	@RequestMapping(value = "/supplierPerformance", method = RequestMethod.GET)
	public String supplierPerformance(Model model) throws JsonProcessingException {
		Supplier supplier = supplierService.findSupplierById(SecurityLibrary.getLoggedInUserTenantId());
		model.addAttribute("supplier", supplier);

		List<Buyer> buyerList = supplierService.getBuyerListForSupplierId(SecurityLibrary.getLoggedInUserTenantId());
		model.addAttribute("buyerList", buyerList);
		// model.addAttribute("businessUnitList",
		// businessUnitService.getPlainActiveBusinessUnitForTenant(SecurityLibrary.getLoggedInUserTenantId()));

		return "supplierPerformance";
	}

	@RequestMapping(path = "/getOverallScoreOfSupplierByBuyer", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<SupplierPerformanceEvaluationPojo> getOverallScoreByBuyer(@RequestParam("startDate") String startDate, @RequestParam(value = "endDate", required = false) String endDate, @RequestParam("buyerId") String buyerId, HttpSession session) {
		LOG.info("Overall score by Buyer for start date : " + startDate + " and end date : " + endDate + " tenantID : " + SecurityLibrary.getLoggedInUserTenantId());
		HttpHeaders header = new HttpHeaders();
		try {
			SupplierPerformanceEvaluationPojo data = new SupplierPerformanceEvaluationPojo();
			Date start = null;
			Date end = null;

			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}

			if (StringUtils.checkString(startDate).length() > 0 && StringUtils.checkString(endDate).length() > 0) {
				DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
				formatter.setTimeZone(timeZone);
				start = (Date) formatter.parse(startDate);
				end = (Date) formatter.parse(endDate);
			}

			data = supplierPerformanceEvaluationService.getOverallScoreOfSupplierByBuyerId(start, end, buyerId, SecurityLibrary.getLoggedInUserTenantId());

			if (data != null) {
				return new ResponseEntity<SupplierPerformanceEvaluationPojo>(data, HttpStatus.OK);
			} else {
				return new ResponseEntity<SupplierPerformanceEvaluationPojo>(null, header, HttpStatus.OK);
			}
		} catch (Exception e) {
			LOG.error("Error in fetching Overall Score By Buyer : " + e.getMessage(), e);
			header.add("error", e.getMessage());
			return new ResponseEntity<SupplierPerformanceEvaluationPojo>(null, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/getOverallScoreOfSupplierByBUnit", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<SupplierPerformanceEvaluationPojo>> getOverallScoreOfSupplierByBUnit(@RequestParam("startDate") String startDate, @RequestParam(value = "endDate", required = false) String endDate, @RequestParam("buyerId") String buyerId, HttpSession session) {
		LOG.info("Overall score by By B unit for Supplier for start date : " + startDate + " and end date : " + endDate + " tenantID : " + SecurityLibrary.getLoggedInUserTenantId());
		try {
			List<SupplierPerformanceEvaluationPojo> data = null;
			Date start = null;
			Date end = null;

			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}

			if (StringUtils.checkString(startDate).length() > 0 && StringUtils.checkString(endDate).length() > 0) {
				DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
				formatter.setTimeZone(timeZone);
				start = (Date) formatter.parse(startDate);
				end = (Date) formatter.parse(endDate);
			}

			data = supplierPerformanceEvaluationService.getSumOfOverallScoreOfSupplierByBuyerIdAndBUnit(buyerId, SecurityLibrary.getLoggedInUserTenantId(), start, end);

			return new ResponseEntity<List<SupplierPerformanceEvaluationPojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error in fetching Overall Score By B unit for Supplier : " + e.getMessage(), e);
			HttpHeaders header = new HttpHeaders();
			header.add("error", e.getMessage());
			return new ResponseEntity<List<SupplierPerformanceEvaluationPojo>>(null, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/getOverallScoreOfSupplierByBUnitAndEvent", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<SupplierPerformanceEvaluationPojo>> getOverallScoreOfSupplierByBUnitAndEvent(@RequestParam("startDate") String startDate, @RequestParam(value = "endDate", required = false) String endDate, @RequestParam("unitId") String unitId, @RequestParam("buyerId") String buyerId, HttpSession session) {
		LOG.info("Overall Score For Event And Business Unit for start date : " + startDate + " and end date : " + endDate);
		try {
			List<SupplierPerformanceEvaluationPojo> data = null;
			Date start = null;
			Date end = null;

			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}

			if (StringUtils.checkString(startDate).length() > 0 && StringUtils.checkString(endDate).length() > 0) {
				DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
				formatter.setTimeZone(timeZone);
				start = (Date) formatter.parse(startDate);
				end = (Date) formatter.parse(endDate);
			}

			data = supplierPerformanceEvaluationService.getOverallScoreOfSupplierByBUnitAndEvent(start, end, unitId, SecurityLibrary.getLoggedInUserTenantId(), buyerId);

			LOG.info("DAta " + data.size());

			return new ResponseEntity<List<SupplierPerformanceEvaluationPojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error in fetching Overall Score For Event And BUnit : " + e.getMessage());
			HttpHeaders header = new HttpHeaders();
			header.add("error", e.getMessage());
			return new ResponseEntity<List<SupplierPerformanceEvaluationPojo>>(null, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/getBUnitListByBuyerID", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<BusinessUnit>> getBUnitListByBuyerID(@RequestParam("buyerId") String buyerId) {
		try {
			LOG.info("Buyer Id : " + buyerId);
			return new ResponseEntity<List<BusinessUnit>>(businessUnitService.getPlainActiveBusinessUnitForTenant(buyerId), HttpStatus.OK);
		} catch (Exception e) {
			HttpHeaders headers = new HttpHeaders();
			LOG.error("Error fetching B Unit list : " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("assign.bunit.error", new Object[] {}, Global.LOCALE));
			return new ResponseEntity<List<BusinessUnit>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/getEventIdListByBuyerID", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<SupplierPerformanceForm>> getEventIdListByBuyerID(@RequestParam("buyerId") String buyerId, @RequestParam("startDate") String startDate, @RequestParam(value = "endDate", required = false) String endDate, HttpSession session) {
		try {
			LOG.info("Buyer Id : " + buyerId);
			Date start = null;
			Date end = null;

			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}

			if (StringUtils.checkString(startDate).length() > 0 && StringUtils.checkString(endDate).length() > 0) {
				DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
				formatter.setTimeZone(timeZone);
				start = (Date) formatter.parse(startDate);
				end = (Date) formatter.parse(endDate);
			}
			List<SupplierPerformanceForm> list = supplierPerformanceEvaluationService.getEventIdListForSupplierId(SecurityLibrary.getLoggedInUserTenantId(), buyerId, start, end);

			return new ResponseEntity<List<SupplierPerformanceForm>>(list, HttpStatus.OK);
		} catch (Exception e) {
			HttpHeaders headers = new HttpHeaders();
			LOG.error("Error fetching Event ID list : " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("eventId.error", new Object[] {}, Global.LOCALE));
			return new ResponseEntity<List<SupplierPerformanceForm>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/getFormIdByDate", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<SupplierPerformanceEvaluationPojo>> getFormIdByDate(@RequestParam("startDate") String startDate, @RequestParam(value = "endDate", required = false) String endDate, @RequestParam("buyerId") String buyerId, HttpSession session) {
		LOG.info("Form id start date : " + startDate + " and end date : " + endDate);
		try {
			List<SupplierPerformanceEvaluationPojo> data = null;
			Date start = null;
			Date end = null;

			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}

			if (StringUtils.checkString(startDate).length() > 0 && StringUtils.checkString(endDate).length() > 0) {
				DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
				formatter.setTimeZone(timeZone);
				start = (Date) formatter.parse(startDate);
				end = (Date) formatter.parse(endDate);
			}

			data = supplierPerformanceFormService.getSpFormIdListForSupplierIdAndTenantId(start, end, SecurityLibrary.getLoggedInUserTenantId(), buyerId);

			return new ResponseEntity<List<SupplierPerformanceEvaluationPojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error in fetching Overall Score SpForm And BUnit : " + e.getMessage());
			HttpHeaders header = new HttpHeaders();
			header.add("error", e.getMessage());
			return new ResponseEntity<List<SupplierPerformanceEvaluationPojo>>(null, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/getOverallScoreOfCriteriaByEventId", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<SupplierPerformanceEvaluationPojo>> getOverallScoreOfCriteriaByEventId(@RequestParam("startDate") String startDate, @RequestParam(value = "endDate", required = false) String endDate, @RequestParam("eventId") String eventId, @RequestParam("buyerId") String buyerId, HttpSession session) {
		LOG.info("Overall score by Event Id for start date : " + startDate + " and end date : " + endDate);
		try {
			List<SupplierPerformanceEvaluationPojo> data = null;
			Date start = null;
			Date end = null;

			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}

			if (StringUtils.checkString(startDate).length() > 0 && StringUtils.checkString(endDate).length() > 0) {
				DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
				formatter.setTimeZone(timeZone);
				start = (Date) formatter.parse(startDate);
				end = (Date) formatter.parse(endDate);
			}

			data = supplierPerformanceEvaluationService.getOverallScoreByCriteriaAndEventId(start, end, eventId, buyerId, SecurityLibrary.getLoggedInUserTenantId());

			return new ResponseEntity<List<SupplierPerformanceEvaluationPojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error in fetching Overall Score By FormId For Criteria : " + e.getMessage(), e);
			HttpHeaders header = new HttpHeaders();
			header.add("error", e.getMessage());
			return new ResponseEntity<List<SupplierPerformanceEvaluationPojo>>(null, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/getOverallScoreOfCriteriaByFormId", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<SupplierPerformanceEvaluationPojo> getOverallScoreOfCriteriaByFormId(@RequestParam("startDate") String startDate, @RequestParam(value = "endDate", required = false) String endDate, @RequestParam("formId") String formId, @RequestParam("buyerId") String buyerId, HttpSession session) {
		LOG.info("Overall score by Form Id for start date : " + startDate + " and end date : " + endDate);
		try {
			SupplierPerformanceEvaluationPojo data = new SupplierPerformanceEvaluationPojo();
			List<SupplierPerformanceEvaluationPojo> list = null;
			Date start = null;
			Date end = null;

			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}

			if (StringUtils.checkString(startDate).length() > 0 && StringUtils.checkString(endDate).length() > 0) {
				DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
				formatter.setTimeZone(timeZone);
				start = (Date) formatter.parse(startDate);
				end = (Date) formatter.parse(endDate);
			}

			list = supplierPerformanceEvaluationService.getOverallScoreByCriteriaAndFormId(start, end, formId, SecurityLibrary.getLoggedInUserTenantId(), buyerId);

			if (StringUtils.checkString(formId).length() > 0) {
				SupplierPerformanceForm form = supplierPerformanceEvaluationService.getSupplierPerformanceFormByFormId(formId);
				data.setOverallScoreBySpForm(list);
				data.setTotalOverallScore(form.getOverallScore());
				if (form.getScoreRating() != null) {
					data.setRating(form.getScoreRating().getRating().toString());
					data.setRatingDescription(form.getScoreRating().getDescription());
				}
			}

			return new ResponseEntity<SupplierPerformanceEvaluationPojo>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error in fetching Overall Score By FormId For Criteria : " + e.getMessage(), e);
			HttpHeaders header = new HttpHeaders();
			header.add("error", e.getMessage());
			return new ResponseEntity<SupplierPerformanceEvaluationPojo>(null, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/getBusinessUnitByDate", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<BusinessUnit>> getBusinessUnitList(@RequestParam("startDate") String startDate, @RequestParam(value = "endDate", required = false) String endDate, @RequestParam(value = "buyerId", required = false) String buyerId, HttpSession session) {
		LOG.info("BUnit list for start date : " + startDate + " and end date : " + endDate);
		try {
			Date start = null;
			Date end = null;

			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}

			if (StringUtils.checkString(startDate).length() > 0 && StringUtils.checkString(endDate).length() > 0) {
				DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
				formatter.setTimeZone(timeZone);
				start = (Date) formatter.parse(startDate);
				end = (Date) formatter.parse(endDate);
			}
			return new ResponseEntity<List<BusinessUnit>>(supplierPerformanceEvaluationService.getBusinessUnitListForTenant(buyerId, start, end, SecurityLibrary.getLoggedInUserTenantId()), HttpStatus.OK);
		} catch (ParseException e) {
			HttpHeaders headers = new HttpHeaders();
			LOG.error("Error fetching B Unit list : " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("assign.bunit.error", new Object[] {}, Global.LOCALE));
			return new ResponseEntity<List<BusinessUnit>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/getSorItemForSearchFilterForSupplier", method = RequestMethod.POST)
	public ResponseEntity<SupplierSorItemResponsePojo> getSorItemForSearchFilter(@RequestParam("eventBqId") String eventBqId, @RequestParam("rfteventId") String rfteventId, @RequestParam("eventType") RfxTypes eventType, @RequestParam(name = "searchVal", required = false) String searchVal, @RequestParam("pageNo") Integer pageNo, @RequestParam(name = "pageLength", required = false) Integer pageLength, @RequestParam(name = "choosenSection", required = false) String choosenSection) {

		HttpHeaders header = new HttpHeaders();
		List<?> supplierSorItem = null;
		long totalSorItemCount = 1;
		SupplierSorItemResponsePojo sorItemResponsePojo = new SupplierSorItemResponsePojo();
		List<SorItemPojo> leveLOrderList = null;
		try {
			LOG.info(" getBqItemForSearchFilterForSupplier  : " + eventBqId + " eventId :" + rfteventId + " eventType :" + eventType + " pageLength :" + pageLength + " pageno :" + pageNo + " choosenSection :" + choosenSection);
			Integer itemLevel = null;
			Integer itemOrder = null;

			if (StringUtils.checkString(choosenSection).length() > 0 && StringUtils.checkString(choosenSection).length() == 1) {
				itemLevel = Integer.parseInt(choosenSection);
				itemOrder = 0;
			}
			if (StringUtils.checkString(choosenSection).length() > 0) {
				itemLevel = 0;
				itemOrder = 0;
				String[] values = choosenSection.split("\\.");
				itemLevel = Integer.parseInt(values[0]);
				itemOrder = Integer.parseInt(values[1]);
			}
			LOG.info(" itemOrder : " + itemOrder + " itemLevel :" + itemLevel);

			switch (eventType) {
				case RFP:
					leveLOrderList = rfpSupplierSorItemService.getAllLevelOrderSorItemBySorId(eventBqId, SecurityLibrary.getLoggedInUserTenantId(), searchVal);
					sorItemResponsePojo.setLevelOrderList(leveLOrderList);
					supplierSorItem = rfpSupplierSorItemService.getSorItemForSearchFilterForSupplier(eventBqId, SecurityLibrary.getLoggedInUserTenantId(), searchVal, pageNo, pageLength, itemLevel, itemOrder);
					sorItemResponsePojo.setSupplierSorItemList(supplierSorItem);
					totalSorItemCount = rfpSupplierSorItemService.totalSorItemCountBySorId(eventBqId, SecurityLibrary.getLoggedInUserTenantId(), searchVal);
					sorItemResponsePojo.setTotalBqItemCount(totalSorItemCount);
					LOG.info("RFP totalSorItemCount :" + totalSorItemCount);
					break;
				case RFA:
					leveLOrderList = rfaSupplierSorItemService.getAllLevelOrderSorItemBySorId(eventBqId, SecurityLibrary.getLoggedInUserTenantId(), searchVal);
					sorItemResponsePojo.setLevelOrderList(leveLOrderList);
					supplierSorItem = rfaSupplierSorItemService.getSorItemForSearchFilterForSupplier(eventBqId, SecurityLibrary.getLoggedInUserTenantId(), searchVal, pageNo, pageLength, itemLevel, itemOrder);
					sorItemResponsePojo.setSupplierSorItemList(supplierSorItem);
					totalSorItemCount = rfaSupplierSorItemService.totalSorItemCountBySorId(eventBqId, SecurityLibrary.getLoggedInUserTenantId(), searchVal);
					sorItemResponsePojo.setTotalBqItemCount(totalSorItemCount);
					LOG.info("RFA totalSorItemCount :" + totalSorItemCount);
					break;
				case RFI:
					leveLOrderList = rfiSupplierSorItemService.getAllLevelOrderSorItemBySorId(eventBqId, SecurityLibrary.getLoggedInUserTenantId(), searchVal);
					sorItemResponsePojo.setLevelOrderList(leveLOrderList);
					supplierSorItem = rfiSupplierSorItemService.getSorItemForSearchFilterForSupplier(eventBqId, SecurityLibrary.getLoggedInUserTenantId(), searchVal, pageNo, pageLength, null, null);
					sorItemResponsePojo.setSupplierSorItemList(supplierSorItem);
					totalSorItemCount = rfiSupplierSorItemService.totalSorItemCountBySorId(eventBqId, SecurityLibrary.getLoggedInUserTenantId(), searchVal);
					sorItemResponsePojo.setTotalBqItemCount(totalSorItemCount);
					LOG.info("RFI totalSorItemCount :" + totalSorItemCount);
					break;
				case RFQ:
					leveLOrderList = rfqSupplierSorItemService.getAllLevelOrderSorItemBySorId(eventBqId, SecurityLibrary.getLoggedInUserTenantId(), searchVal);
					sorItemResponsePojo.setLevelOrderList(leveLOrderList);
					supplierSorItem = rfqSupplierSorItemService.getSorItemForSearchFilterForSupplier(eventBqId, SecurityLibrary.getLoggedInUserTenantId(), searchVal, pageNo, pageLength, itemLevel, itemOrder);
					sorItemResponsePojo.setSupplierSorItemList(supplierSorItem);
					totalSorItemCount = rfqSupplierSorItemService.totalSorItemCountBySorId(eventBqId, SecurityLibrary.getLoggedInUserTenantId(), searchVal);
					sorItemResponsePojo.setTotalBqItemCount(totalSorItemCount);
					LOG.info("RFQ totalSorItemCount :" + totalSorItemCount);
					break;
				case RFT:
					leveLOrderList = rftSupplierSorItemService.getAllLevelOrderSorItemBySorId(eventBqId, SecurityLibrary.getLoggedInUserTenantId(), searchVal);
					sorItemResponsePojo.setLevelOrderList(leveLOrderList);
					supplierSorItem = rftSupplierSorItemService.getSorItemForSearchFilterForSupplier(eventBqId, SecurityLibrary.getLoggedInUserTenantId(), searchVal, pageNo, pageLength, itemLevel, itemOrder);
					sorItemResponsePojo.setSupplierSorItemList(supplierSorItem);
					totalSorItemCount = rftSupplierSorItemService.totalSorItemCountBySorId(eventBqId, SecurityLibrary.getLoggedInUserTenantId(), searchVal);
					sorItemResponsePojo.setTotalBqItemCount(totalSorItemCount);
					LOG.info("RFT totalSorItemCount :" + totalSorItemCount);
					break;
				default:
					break;
			}
			if (pageLength != SecurityLibrary.getLoggedInUser().getBqPageLength()) {
				userService.updateUserBqPageLength(pageLength, SecurityLibrary.getLoggedInUser().getId());
				super.updateSecurityLibraryUser(pageLength);
			}
		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
			header.add("error", "Error loading SOR : " + e.getMessage());
			return new ResponseEntity<SupplierSorItemResponsePojo>(sorItemResponsePojo, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<SupplierSorItemResponsePojo>(sorItemResponsePojo, HttpStatus.OK);
	}

	@RequestMapping(value = "/getEventSorForResetValue", method = RequestMethod.POST)
	public ResponseEntity<SupplierSorItemResponsePojo> getEventSorForResetValue(@RequestParam("eventBqId") String eventBqId, @RequestParam("rfteventId") String rfteventId, @RequestParam("eventType") RfxTypes eventType, @RequestParam("searchVal") String searchVal, @RequestParam("pageNo") Integer pageNo, @RequestParam("pageLength") Integer pageLength) {
		HttpHeaders header = new HttpHeaders();
		List<?> supplierSorItem = null;
		long totalSorItemCount = 1;
		SupplierSorItemResponsePojo sorItemResponsePojo = new SupplierSorItemResponsePojo();
		List<SorItemPojo> leveLOrderList = null;
		try {
			switch (eventType) {
				case RFP:
					leveLOrderList = rfpSupplierSorItemService.getAllLevelOrderSorItemBySorId(eventBqId, SecurityLibrary.getLoggedInUserTenantId(), searchVal);
					sorItemResponsePojo.setLevelOrderList(leveLOrderList);
					supplierSorItem = rfpSupplierSorItemService.getSorItemForSearchFilterForSupplier(eventBqId, SecurityLibrary.getLoggedInUserTenantId(), searchVal, pageNo, pageLength, null, null);
					sorItemResponsePojo.setSupplierSorItemList(supplierSorItem);
					totalSorItemCount = rfpSupplierSorItemService.totalSorItemCountBySorId(eventBqId, SecurityLibrary.getLoggedInUserTenantId(), searchVal);
					sorItemResponsePojo.setTotalBqItemCount(totalSorItemCount);
					LOG.info("RFP totalSorItemCount :" + totalSorItemCount);
					break;
				case RFA:
					leveLOrderList = rfaSupplierSorItemService.getAllLevelOrderSorItemBySorId(eventBqId, SecurityLibrary.getLoggedInUserTenantId(), searchVal);
					sorItemResponsePojo.setLevelOrderList(leveLOrderList);
					supplierSorItem = rfaSupplierSorItemService.getSorItemForSearchFilterForSupplier(eventBqId, SecurityLibrary.getLoggedInUserTenantId(), searchVal, pageNo, pageLength, null, null);
					sorItemResponsePojo.setSupplierSorItemList(supplierSorItem);
					totalSorItemCount = rfaSupplierSorItemService.totalSorItemCountBySorId(eventBqId, SecurityLibrary.getLoggedInUserTenantId(), searchVal);
					sorItemResponsePojo.setTotalBqItemCount(totalSorItemCount);
					LOG.info("RFA totalSorItemCount :" + totalSorItemCount);
					break;
				case RFI:
					leveLOrderList = rfiSupplierSorItemService.getAllLevelOrderSorItemBySorId(eventBqId, SecurityLibrary.getLoggedInUserTenantId(), searchVal);
					sorItemResponsePojo.setLevelOrderList(leveLOrderList);
					supplierSorItem = rfiSupplierSorItemService.getSorItemForSearchFilterForSupplier(eventBqId, SecurityLibrary.getLoggedInUserTenantId(), searchVal, pageNo, pageLength, null, null);
					sorItemResponsePojo.setSupplierSorItemList(supplierSorItem);
					totalSorItemCount = rfiSupplierSorItemService.totalSorItemCountBySorId(eventBqId, SecurityLibrary.getLoggedInUserTenantId(), searchVal);
					sorItemResponsePojo.setTotalBqItemCount(totalSorItemCount);
					LOG.info("RFI totalSorItemCount :" + totalSorItemCount);
					break;
				case RFQ:
					leveLOrderList = rfqSupplierSorItemService.getAllLevelOrderSorItemBySorId(eventBqId, SecurityLibrary.getLoggedInUserTenantId(), searchVal);
					sorItemResponsePojo.setLevelOrderList(leveLOrderList);
					supplierSorItem = rfqSupplierSorItemService.getSorItemForSearchFilterForSupplier(eventBqId, SecurityLibrary.getLoggedInUserTenantId(), searchVal, pageNo, pageLength, null, null);
					sorItemResponsePojo.setSupplierSorItemList(supplierSorItem);
					totalSorItemCount = rfqSupplierSorItemService.totalSorItemCountBySorId(eventBqId, SecurityLibrary.getLoggedInUserTenantId(), searchVal);
					sorItemResponsePojo.setTotalBqItemCount(totalSorItemCount);
					LOG.info("RFQ totalSorItemCount :" + totalSorItemCount);
					break;
				case RFT:
					leveLOrderList = rftSupplierSorItemService.getAllLevelOrderSorItemBySorId(eventBqId, SecurityLibrary.getLoggedInUserTenantId(), searchVal);
					sorItemResponsePojo.setLevelOrderList(leveLOrderList);
					supplierSorItem = rftSupplierSorItemService.getSorItemForSearchFilterForSupplier(eventBqId, SecurityLibrary.getLoggedInUserTenantId(), searchVal, pageNo, pageLength, null, null);
					sorItemResponsePojo.setSupplierSorItemList(supplierSorItem);
					totalSorItemCount = rftSupplierSorItemService.totalSorItemCountBySorId(eventBqId, SecurityLibrary.getLoggedInUserTenantId(), searchVal);
					sorItemResponsePojo.setTotalBqItemCount(totalSorItemCount);
					LOG.info("RFT totalSorItemCount :" + totalSorItemCount);
					break;
				default:
					break;
			}
		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
			header.add("error", "Error loading SOR : " + e.getMessage());
			return new ResponseEntity<SupplierSorItemResponsePojo>(sorItemResponsePojo, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<SupplierSorItemResponsePojo>(sorItemResponsePojo, HttpStatus.OK);
	}

	@RequestMapping(path = "/exportSupplierPoItemSummaryCsvReport", method = RequestMethod.POST)
	public void downloadPoItemCsvReport(HttpSession session, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redir, @ModelAttribute("searchFilterPoPojo") SearchFilterPoPojo searchFilterPoPojo, boolean select_all, @RequestParam String dateTimeRange) throws IOException {
		try {
			File file = File.createTempFile("poReports-", ".csv");
			LOG.info("Temp file : " + file.getAbsolutePath());

			String poIds[] = null;
			if (StringUtils.checkString(searchFilterPoPojo.getPoIds()).length() > 0) {
				poIds = searchFilterPoPojo.getPoIds().split(",");
			}

			String startDate = null;
			String endDate = null;
			Date sDate = null;
			Date eDate = null;

			if (StringUtils.checkString(dateTimeRange).length() > 0) {
				String dateTimeArr[] = dateTimeRange.split("-");
				if (dateTimeArr.length == 2) {
					startDate = dateTimeArr[0].trim();
					endDate = dateTimeArr[1].trim();

					try {
						// Use the correct date format for parsing
						SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
						sDate = dateFormat.parse(startDate);
						eDate = dateFormat.parse(endDate);
						LOG.info("Parsed Start date: " + sDate + " Parsed End date: " + eDate);

					} catch (ParseException e) {
						LOG.error("Invalid date format: " + e.getMessage());
						HttpHeaders headers = new HttpHeaders();
						headers.add("error", "Invalid date format. Expected format is MM/dd/yyyy.");
					}

				} else {
					LOG.warn("Invalid dateTimeRange format: {}", dateTimeRange);
					HttpHeaders headers = new HttpHeaders();
					headers.add("error", "Invalid dateTimeRange format. Expected format is 'startDate-endDate'.");
				}
			}

			poService.downloadCsvFileForPoItemSupplierSummary(response, file, poIds, searchFilterPoPojo, sDate, eDate, select_all, SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), session);

			// Send to UI
			response.setContentType("text/csv");
			response.addHeader("Content-Disposition", "attachment; filename=poItemReport.csv");
			response.setContentLengthLong(file.length());
			Files.copy(file.toPath(), response.getOutputStream());
			response.getOutputStream().flush();

		} catch (Exception e) {
			LOG.error("Error while downloading csv file : " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("PoSummary.error.download.csv", new Object[] { e.getMessage() }, Global.LOCALE));

		}
	}
}

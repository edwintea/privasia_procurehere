package com.privasia.procurehere.web.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

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
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.EventIdSettingsDao;
import com.privasia.procurehere.core.entity.BusinessUnit;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.Event;
import com.privasia.procurehere.core.entity.EventSettings;
import com.privasia.procurehere.core.entity.EventSupplier;
import com.privasia.procurehere.core.entity.IndustryCategory;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.entity.RfiEvent;
import com.privasia.procurehere.core.entity.RfpEvent;
import com.privasia.procurehere.core.entity.RfqEvent;
import com.privasia.procurehere.core.entity.RftEvent;
import com.privasia.procurehere.core.entity.RfxTemplate;
import com.privasia.procurehere.core.enums.AuctionType;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.EventVisibilityType;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.SubscriptionException;
import com.privasia.procurehere.core.pojo.ColumnParameter;
import com.privasia.procurehere.core.pojo.DraftEventPojo;
import com.privasia.procurehere.core.pojo.SearchFilterEventPojo;
import com.privasia.procurehere.core.pojo.SupplierSearchPojo;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.BusinessUnitService;
import com.privasia.procurehere.service.BuyerSettingsService;
import com.privasia.procurehere.service.DashboardNotificationService;
import com.privasia.procurehere.service.EventSettingsService;
import com.privasia.procurehere.service.FavoriteSupplierService;
import com.privasia.procurehere.service.IndustryCategoryService;
import com.privasia.procurehere.service.NotificationService;
import com.privasia.procurehere.service.RfaEventService;
import com.privasia.procurehere.service.RfaEventSupplierService;
import com.privasia.procurehere.service.RfiEventService;
import com.privasia.procurehere.service.RfiEventSupplierService;
import com.privasia.procurehere.service.RfpEventService;
import com.privasia.procurehere.service.RfpEventSupplierService;
import com.privasia.procurehere.service.RfqEventService;
import com.privasia.procurehere.service.RfqEventSupplierService;
import com.privasia.procurehere.service.RftEventService;
import com.privasia.procurehere.service.RftEventSupplierService;
import com.privasia.procurehere.service.RfxTemplateService;

import freemarker.template.Configuration;

@Controller
@RequestMapping("/buyer")
public class EventCreateController {

	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@Autowired
	BuyerSettingsService buyerSettingsService;

	@Value("${app.url}")
	String APP_URL;

	@Autowired
	NotificationService notificationService;

	@Autowired
	DashboardNotificationService dashboardNotificationService;

	@Autowired
	Configuration freemarkerConfiguration;

	@Autowired
	RftEventService rftEventService;

	@Autowired
	RfaEventService rfaEventService;

	@Autowired
	RfiEventService rfiEventService;

	@Autowired
	RfpEventService rfpEventService;

	@Autowired
	RfqEventService rfqEventService;

	@Autowired
	RfxTemplateService rfxTemplateService;

	@Autowired
	EventIdSettingsDao eventIdSettingsDao;

	@Autowired
	BusinessUnitService businessUnitService;

	@Autowired
	IndustryCategoryService industryCategoryService;

	@Autowired
	FavoriteSupplierService favoriteSupplierService;

	@Autowired
	ServletContext context;

	@Autowired
	RfqEventSupplierService rfqEventSupplierService;

	@Autowired
	RfaEventSupplierService rfaEventSupplierService;

	@Autowired
	RfpEventSupplierService rfpEventSupplierService;

	@Autowired
	RftEventSupplierService rftEventSupplierService;

	@Autowired
	RfiEventSupplierService rfiEventSupplierService;

	@Autowired
	MessageSource messageSource;

	@Autowired
	EventSettingsService eventSettingsService;
	
	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@RequestMapping(value = "/createEvent/{eventType}", method = RequestMethod.GET)
	public String createEvent(Model model, @PathVariable String eventType, RedirectAttributes redir) {

		LOG.info("et ====   " + RfxTypes.valueOf(eventType));
		String returnString = constructEventAndTemplateAttributes(model, eventType, redir);
		return returnString;
	}

	@RequestMapping(value = "/paginationForEvents/{eventType}/{pageNo}", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<DraftEventPojo>> getEventForCopyFromPrevious(@PathVariable(name = "pageNo") String pageNo, @PathVariable("eventType") String eventType, HttpSession session, @RequestParam(name = "searchValue", required = false) String searchValue, @RequestParam(name = "industryCategory", required = false) String industryCategory) {
		List<DraftEventPojo> eventList = new ArrayList<DraftEventPojo>();
		LOG.info("search values " + searchValue);
		LOG.info("Industry cat " + industryCategory);
		TimeZone timeZone = TimeZone.getDefault();
		String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		formatter.setTimeZone(timeZone);
		LOG.info("page No " + pageNo + " Event Type " + eventType);
		switch (RfxTypes.valueOf(eventType)) {
		case RFA:
			List<DraftEventPojo> allRfaEvents = new ArrayList<DraftEventPojo>();
			try {
				if (SecurityLibrary.isLoggedInUserAdmin()) {
					allRfaEvents = rfaEventService.getAllRfaEventByTenantId(SecurityLibrary.getLoggedInUserTenantId(), null, pageNo, searchValue, industryCategory);
				} else {
					allRfaEvents = rfaEventService.getAllRfaEventByTenantId(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId(), pageNo, searchValue, industryCategory);
				}
				if (CollectionUtil.isNotEmpty(allRfaEvents)) {
					eventList.addAll(allRfaEvents);
				} else {
					eventList.addAll(new ArrayList<DraftEventPojo>());
				}
			} catch (SubscriptionException e) {
				LOG.error(e.getMessage(), e);
				return new ResponseEntity<List<DraftEventPojo>>(eventList, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			break;
		case RFI:
			List<DraftEventPojo> allRfiEvents = new ArrayList<DraftEventPojo>();
			try {
				if (SecurityLibrary.isLoggedInUserAdmin()) {
					allRfiEvents = rfiEventService.getAllRfiEventByTenantId(SecurityLibrary.getLoggedInUserTenantId(), null, pageNo, searchValue, industryCategory);
				} else {
					allRfiEvents = rfiEventService.getAllRfiEventByTenantId(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId(), pageNo, searchValue, industryCategory);
				}
				if (CollectionUtil.isNotEmpty(allRfiEvents)) {
					eventList.addAll(allRfiEvents);
				} else {
					eventList.addAll(new ArrayList<DraftEventPojo>());
				}
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				return new ResponseEntity<List<DraftEventPojo>>(eventList, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			break;
		case RFP:

			List<DraftEventPojo> allRfpEvents = new ArrayList<DraftEventPojo>();
			try {
				if (SecurityLibrary.isLoggedInUserAdmin()) {
					allRfpEvents = rfpEventService.getAllEventByTenantId(SecurityLibrary.getLoggedInUserTenantId(), null, pageNo, searchValue, industryCategory);
				} else {
					allRfpEvents = rfpEventService.getAllEventByTenantId(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId(), pageNo, searchValue, industryCategory);
				}
				if (CollectionUtil.isNotEmpty(allRfpEvents)) {
					eventList.addAll(allRfpEvents);
				} else {
					eventList.addAll(new ArrayList<DraftEventPojo>());
				}
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				return new ResponseEntity<List<DraftEventPojo>>(eventList, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			break;
		case RFQ:
			List<DraftEventPojo> allRfqEvents = new ArrayList<DraftEventPojo>();
			try {
				if (SecurityLibrary.isLoggedInUserAdmin()) {
					allRfqEvents = rfqEventService.getAllEventByTenantId(SecurityLibrary.getLoggedInUserTenantId(), null, pageNo, searchValue, industryCategory);
				} else {
					allRfqEvents = rfqEventService.getAllEventByTenantId(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId(), pageNo, searchValue, industryCategory);
				}
				if (CollectionUtil.isNotEmpty(allRfqEvents)) {
					eventList.addAll(allRfqEvents);
				} else {
					eventList.addAll(new ArrayList<DraftEventPojo>());
				}
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				return new ResponseEntity<List<DraftEventPojo>>(eventList, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			break;
		case RFT:
			List<DraftEventPojo> allRftEvents = new ArrayList<DraftEventPojo>();
			try {
				if (SecurityLibrary.isLoggedInUserAdmin()) {
					allRftEvents = rftEventService.getAllRftEventByTenantId(SecurityLibrary.getLoggedInUserTenantId(), null, pageNo, searchValue, industryCategory);
				} else {
					allRftEvents = rftEventService.getAllRftEventByTenantId(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId(), pageNo, searchValue, industryCategory);
				}
				if (CollectionUtil.isNotEmpty(allRftEvents)) {
					eventList.addAll(allRftEvents);
				} else {
					eventList.addAll(new ArrayList<DraftEventPojo>());
				}
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				return new ResponseEntity<List<DraftEventPojo>>(eventList, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			break;
		default:
			break;
		}
		for (DraftEventPojo event : eventList) {
			LOG.info("event Name " + event.getEventName() + "Id " + event.getId() + " IC " + event.getEventCategories() + " TemStatus " + event.isTemplateActive() + "event status " + event.getStatus() + " eventId ");
		}
		return new ResponseEntity<List<DraftEventPojo>>(eventList, HttpStatus.OK);
	}

	@RequestMapping("/paginationForTemplate/{eventType}/{pageNo}")
	public ResponseEntity<List<RfxTemplate>> paginationForTemplate(@PathVariable("eventType") String eventType, @PathVariable("pageNo") String pageNo, HttpSession session) {
		try {
			List<RfxTemplate> allRfxTemplate = rfxTemplateService.findAllActiveTemplatesByRfxTypeForTenant(SecurityLibrary.getLoggedInUserTenantId(), RfxTypes.valueOf(eventType), SecurityLibrary.getLoggedInUser().getId(), pageNo);
			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			formatter.setTimeZone(timeZone);
			if (CollectionUtil.isNotEmpty(allRfxTemplate)) {
				for (RfxTemplate rfxTemplate : allRfxTemplate) {
					rfxTemplate.setTemplateDescription(StringUtils.checkString(rfxTemplate.getTemplateDescription()).length() > 0 ? (rfxTemplate.getTemplateDescription()) : "");
					if (rfxTemplate.getCreatedDate() != null) {
						try {
							String eventStartDate = formatter.format(rfxTemplate.getCreatedDate());
							rfxTemplate.setCreatedDate(formatter.parse(eventStartDate));

						} catch (ParseException e) {
							LOG.error(e.getMessage(), e);
						}
					}
				}
			}
			return new ResponseEntity<List<RfxTemplate>>(allRfxTemplate, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<List<RfxTemplate>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private String constructEventAndTemplateAttributes(Model model, String eventType, RedirectAttributes redir) {
		List<RfxTemplate> allRfxTemplate = rfxTemplateService.findAllActiveTemplatesByRfxTypeForTenantInitial(SecurityLibrary.getLoggedInUserTenantId(), RfxTypes.valueOf(eventType), SecurityLibrary.getLoggedInUser().getId());
		model.addAttribute("allRfxTemplate", allRfxTemplate);

		long rfxTemplateCount = rfxTemplateService.findActiveTemplateCountByRfxTypeForTenantId(null, SecurityLibrary.getLoggedInUserTenantId(), RfxTypes.valueOf(eventType), SecurityLibrary.getLoggedInUser().getId());
		model.addAttribute("rfxTemplateCount", rfxTemplateCount);

		switch (RfxTypes.valueOf(eventType)) {
		case RFA:
			List<RfaEvent> allRfaEvents = new ArrayList<RfaEvent>();
			long rfaCount;
			try {
				if (SecurityLibrary.isLoggedInUserAdmin()) {
					allRfaEvents = rfaEventService.getAllRfaEventByTenantIdInitial(SecurityLibrary.getLoggedInUserTenantId(), null);
					rfaCount = rfaEventService.getRfaEventCountByTenantId(null, SecurityLibrary.getLoggedInUserTenantId(), null);
				} else {
					allRfaEvents = rfaEventService.getAllRfaEventByTenantIdInitial(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId());
					rfaCount = rfaEventService.getRfaEventCountByTenantId(null, SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId());
				}
				model.addAttribute("events", allRfaEvents);
				model.addAttribute("rfxCount", rfaCount);

				model.addAttribute("auctionTypeList", AuctionType.values());
			} catch (SubscriptionException e) {
				// LOG.error("You have reached your subscription limit. " + e.getMessage(), e);
				redir.addFlashAttribute("error", e.getMessage());
				return "redirect:/buyer/buyerDashboard";
			}
			break;
		case RFI:
			List<RfiEvent> allRfiEvents = new ArrayList<RfiEvent>();
			long rfiCount;
			try {
				if (SecurityLibrary.isLoggedInUserAdmin()) {
					allRfiEvents = rfiEventService.getAllRfiEventByTenantIdInitial(SecurityLibrary.getLoggedInUserTenantId(), null);
					rfiCount = rfiEventService.getRfiEventCountByTenantId(null, SecurityLibrary.getLoggedInUserTenantId(), null);
				} else {
					allRfiEvents = rfiEventService.getAllRfiEventByTenantIdInitial(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId());
					rfiCount = rfiEventService.getRfiEventCountByTenantId(null, SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId());
				}
				model.addAttribute("events", allRfiEvents);
				model.addAttribute("rfxCount", rfiCount);
				LOG.info("rfiCount ............ " + rfiCount);
			} catch (SubscriptionException e) {
				// LOG.error("You have reached your subscription limit. " + e.getMessage(), e);
				redir.addFlashAttribute("error", e.getMessage());
				return "redirect:/buyer/buyerDashboard";
			}
			break;
		case RFP:

			List<RfpEvent> allRfpEvents = new ArrayList<RfpEvent>();
			long rfpCount;
			try {
				if (SecurityLibrary.isLoggedInUserAdmin()) {
					allRfpEvents = rfpEventService.getAllEventByTenantIdInitial(SecurityLibrary.getLoggedInUserTenantId(), null);
					rfpCount = rfpEventService.getRfpEventCountByTenantId(null, SecurityLibrary.getLoggedInUserTenantId(), null);
				} else {
					allRfpEvents = rfpEventService.getAllEventByTenantIdInitial(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId());
					rfpCount = rfpEventService.getRfpEventCountByTenantId(null, SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId());
				}
				model.addAttribute("events", allRfpEvents);
				model.addAttribute("rfxCount", rfpCount);

			} catch (SubscriptionException e) {
				// LOG.error("You have reached your subscription limit. " + e.getMessage(), e);
				redir.addFlashAttribute("error", e.getMessage());
				return "redirect:/buyer/buyerDashboard";
			}
			break;
		case RFQ:
			List<RfqEvent> allRfqEvents = new ArrayList<RfqEvent>();
			long rfqCount;
			try {
				if (SecurityLibrary.isLoggedInUserAdmin()) {
					allRfqEvents = rfqEventService.getAllEventByTenantIdInitial(SecurityLibrary.getLoggedInUserTenantId(), null);
					rfqCount = rfqEventService.getRfqEventCountByTenantId(null, SecurityLibrary.getLoggedInUserTenantId(), null);
				} else {
					allRfqEvents = rfqEventService.getAllEventByTenantIdInitial(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId());
					rfqCount = rfqEventService.getRfqEventCountByTenantId(null, SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId());
				}
				model.addAttribute("events", allRfqEvents);
				model.addAttribute("rfxCount", rfqCount);

			} catch (SubscriptionException e) {
				// LOG.error("You have reached your subscription limit. " + e.getMessage(), e);
				redir.addFlashAttribute("error", e.getMessage());
				return "redirect:/buyer/buyerDashboard";
			}
			break;
		case RFT:
			List<RftEvent> allRftEvents = new ArrayList<RftEvent>();
			long rftCount;
			try {
				if (SecurityLibrary.isLoggedInUserAdmin()) {
					allRftEvents = rftEventService.getAllRftEventbyTenantidInitial(SecurityLibrary.getLoggedInUserTenantId(), null);
					rftCount = rftEventService.getRftEventCountByTenantId(null, SecurityLibrary.getLoggedInUserTenantId(), null);
				} else {
					allRftEvents = rftEventService.getAllRftEventbyTenantidInitial(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId());
					rftCount = rftEventService.getRftEventCountByTenantId(null, SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId());
				}
				model.addAttribute("events", allRftEvents);
				model.addAttribute("rfxCount", rftCount);

			} catch (SubscriptionException e) {
				// LOG.error("You have reached your subscription limit. " + e.getMessage(), e);
				redir.addFlashAttribute("error", e.getMessage());
				return "redirect:/buyer/buyerDashboard";
			}
			break;
		default:
			break;
		}
		model.addAttribute("eventType", eventType);
		TableDataInput input = new TableDataInput(1, 5);
		List<IndustryCategory> icList = industryCategoryService.findIndustryCategoryForTenant(SecurityLibrary.getLoggedInUserTenantId(), input);
		model.addAttribute("industryCategory", icList);

		EventSettings eventSettings = eventSettingsService.getEventSettingsByTenantId(SecurityLibrary.getLoggedInUserTenantId());
		model.addAttribute("eventSettings", eventSettings);

		return "createEvent";
	}

	@RequestMapping(value = "/navigateEvent/{eventType}", method = RequestMethod.GET)
	public String redirectNavigation(@PathVariable String eventType, @RequestParam(required = false) String auctionType, Model model) {

		switch (RfxTypes.valueOf(eventType)) {
		case RFA:
			LOG.info("auction Type " + auctionType);

			return "redirect:/buyer/" + eventType + "/createRfaEvent/" + auctionType;
		case RFI:
			return "redirect:/buyer/" + eventType + "/createRfiEvent";
		case RFP:
			return "redirect:/buyer/" + eventType + "/createRfpEvent";
		case RFQ:
			return "redirect:/buyer/" + eventType + "/createRfqEvent";
		case RFT:
			return "redirect:/buyer/" + eventType + "/createRftEvent";
		default:
			break;
		}

		return "";
	}

	@RequestMapping(value = "/createBlankEvent", method = RequestMethod.POST)
	public String redirectNavigationForBlankForRFA(@RequestParam String eventType, @RequestParam(required = false) String auctionType, Model model, RedirectAttributes redir) {
		try {
			LOG.info("=========eventType:" + eventType + "============" + auctionType);
			isBusinessSettingEnable(eventType);
			switch (RfxTypes.valueOf(eventType)) {
			case RFA:
				LOG.info("auction Type " + auctionType);

				return "redirect:/buyer/" + eventType + "/createRfaEvent/" + auctionType;
			case RFI:
				return "redirect:/buyer/" + eventType + "/createRfiEvent";
			case RFP:
				return "redirect:/buyer/" + eventType + "/createRfpEvent";
			case RFQ:
				return "redirect:/buyer/" + eventType + "/createRfqEvent";
			case RFT:
				return "redirect:/buyer/" + eventType + "/createRftEvent";
			default:
				break;
			}
		} catch (ApplicationException e) {
			if (e.getMessage().equals("BUSINESS_UNIT_EMPTY")) {
				redir.addFlashAttribute("modelBuForBlank", true);
			} else {
				redir.addFlashAttribute("error", e.getMessage());
			}
			redir.addFlashAttribute("eventType", eventType);
			redir.addFlashAttribute("auctionType", auctionType);
			LOG.error("=============handling exceptions====================================== " + e.getMessage());
			redir.addFlashAttribute("businessUnits", businessUnitService.getPlainActiveBusinessUnitForTenant(SecurityLibrary.getLoggedInUserTenantId()));
			return "redirect:/buyer/createEvent/" + eventType;
		}
		return "";
	}

	@RequestMapping(value = "/createBlankEvent/{eventType}", method = RequestMethod.GET)
	public String redirectNavigationForBlank(@PathVariable String eventType, Model model, RedirectAttributes redir) {
		try {
			LOG.info("=========eventType:" + eventType + "============");
			isBusinessSettingEnable(eventType);
			switch (RfxTypes.valueOf(eventType)) {

			case RFI:
				return "redirect:/buyer/" + eventType + "/createRfiEvent";
			case RFP:
				return "redirect:/buyer/" + eventType + "/createRfpEvent";
			case RFQ:
				return "redirect:/buyer/" + eventType + "/createRfqEvent";
			case RFT:
				return "redirect:/buyer/" + eventType + "/createRftEvent";
			default:
				break;
			}
		} catch (ApplicationException e) {
			if (e.getMessage().equals("BUSINESS_UNIT_EMPTY")) {
				redir.addFlashAttribute("modelBuForBlank", true);
			} else {
				redir.addFlashAttribute("error", e.getMessage());
			}
			redir.addFlashAttribute("eventType", eventType);

			LOG.error("=============handling exceptions======================================");
			redir.addFlashAttribute("businessUnits", businessUnitService.getPlainActiveBusinessUnitForTenant(SecurityLibrary.getLoggedInUserTenantId()));
			return "redirect:/buyer/createEvent/" + eventType;
		}
		return "";
	}

	@RequestMapping(value = "/searchEvent", method = RequestMethod.POST)
	public ResponseEntity<List<Event>> searchEvent(@RequestParam(required = false, value = "searchValue") String searchValue, @RequestParam(required = false, value = "industryCategory") String industryCategory, @RequestParam(required = false, value = "eventType") String eventType, Model model) {
		LOG.info("inside the search controller : ");
		HttpHeaders headers = new HttpHeaders();
		LOG.info("event Type : " + eventType);
		LOG.info("name or desc : " + searchValue + "  induscat id : " + industryCategory + " tenante Id :   " + SecurityLibrary.getLoggedInUserTenantId());
		List<Event> searchResultEvents = new ArrayList<Event>();
		if (SecurityLibrary.isLoggedInUserAdmin()) {
			searchResultEvents = rftEventService.findByEventNameaAndRefNumAndIndCatForTenant(searchValue, industryCategory, SecurityLibrary.getLoggedInUserTenantId(), eventType, null);
		} else {
			searchResultEvents = rftEventService.findByEventNameaAndRefNumAndIndCatForTenant(searchValue, industryCategory, SecurityLibrary.getLoggedInUserTenantId(), eventType, SecurityLibrary.getLoggedInUser().getId());
		}
		for (Event event : searchResultEvents) {
			LOG.info("getPreviousEventType" + event.getPreviousEventType());
		}

		return new ResponseEntity<List<Event>>(searchResultEvents, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/searchTemplate", method = RequestMethod.POST)
	public ResponseEntity<List<RfxTemplate>> searchTemplate(@RequestParam(required = false, value = "templateName") String templateName, @RequestParam(required = false, value = "eventType") String eventType, Model model) {
		LOG.info("inside the search controller : ");
		HttpHeaders headers = new HttpHeaders();

		List<RfxTemplate> searchResultTemplate = rfxTemplateService.findByTemplateNameForTenant(templateName, SecurityLibrary.getLoggedInUserTenantId(), RfxTypes.valueOf(eventType), SecurityLibrary.getLoggedInUser().getId());

		return new ResponseEntity<List<RfxTemplate>>(searchResultTemplate, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/copyFrom", method = RequestMethod.POST)
	public String copyFrom(Model model, @RequestParam(value = "eventId") String eventId, @RequestParam(value = "eventType") String eventType, @RequestParam(value = "businessUnitId", required = false) String businessUnitId, RedirectAttributes redir) {
		try {

			BusinessUnit businessUnit = null;
			if (StringUtils.isNotBlank(businessUnitId))
				businessUnit = businessUnitService.getBusinessUnitById(businessUnitId);
			LOG.info("========================" + businessUnitId);

			if (rftEventService.checkTemplateStatusForEvent(eventId, eventType)) {
				model.addAttribute("error", messageSource.getMessage("cannot.copy.event.incative", new Object[] {}, Global.LOCALE));
				LOG.info("checking status of the template ");
				constructEventAndTemplateAttributes(model, eventType, redir);
				throw new ApplicationException("TEMPLATE IS BEING USED IS INACTIVE");
			}
			switch (RfxTypes.valueOf(eventType)) {
			case RFA:
				RfaEvent newRfaEvent = rfaEventService.copyFrom(eventId, SecurityLibrary.getLoggedInUser(), businessUnit);
				newRfaEvent.setBillOfQuantity(Boolean.TRUE);
				if (newRfaEvent.getCqs() != null) {
					if (newRfaEvent.isUploadDocuments()) {
						redir.addAttribute("info", messageSource.getMessage("eventDocument.copyFromEvent.info", new Object[] {}, Global.LOCALE));
					}
				}
				return "redirect:/buyer/" + eventType + "/createEventDetails/" + newRfaEvent.getId();
			case RFI:
				RfiEvent newRfiEvent = rfiEventService.copyFrom(eventId, SecurityLibrary.getLoggedInUser(), businessUnit);
				newRfiEvent.setBillOfQuantity(Boolean.FALSE);
				if (newRfiEvent.getCqs() != null) {
					if (newRfiEvent.isUploadDocuments()) {
						redir.addAttribute("info", messageSource.getMessage("eventDocument.copyFromEvent.info", new Object[] {}, Global.LOCALE));
					}
				}
				return "redirect:/buyer/" + eventType + "/createEventDetails/" + newRfiEvent.getId();
			case RFP:
				RfpEvent newRfpEvent = rfpEventService.copyFrom(eventId, SecurityLibrary.getLoggedInUser(), businessUnit);
				newRfpEvent.setBillOfQuantity(Boolean.TRUE);
				if (newRfpEvent.getCqs() != null) {
					if (newRfpEvent.isUploadDocuments()) {
						redir.addAttribute("warn", messageSource.getMessage("eventDocument.copyFromEvent.info", new Object[] {}, Global.LOCALE));
					}
				}
				return "redirect:/buyer/" + eventType + "/createEventDetails/" + newRfpEvent.getId();
			case RFQ:
				RfqEvent newRfqEvent = rfqEventService.copyFrom(eventId, SecurityLibrary.getLoggedInUser(), businessUnit);
				newRfqEvent.setBillOfQuantity(Boolean.TRUE);
				if (newRfqEvent.getCqs() != null) {
					if (newRfqEvent.isUploadDocuments()) {
						redir.addAttribute("info", messageSource.getMessage("eventDocument.copyFromEvent.info", new Object[] {}, Global.LOCALE));
					}
				}
				return "redirect:/buyer/" + eventType + "/createEventDetails/" + newRfqEvent.getId();
			case RFT:
				RftEvent newEvent = rftEventService.copyFrom(eventId, SecurityLibrary.getLoggedInUser(), businessUnit);
				newEvent.setBillOfQuantity(Boolean.TRUE);
				return "redirect:/buyer/" + eventType + "/createEventDetails/" + newEvent.getId();
			default:
				break;
			}
		} catch (ApplicationException e) {
			LOG.error("=============We are here======================================" + e.getMessage(), e);
			if (e.getMessage().equals("BUSINESS_UNIT_EMPTY")) {
				redir.addFlashAttribute("openModelBu", true);
			} else if (e.getMessage().equals("TEMPLATE IS BEING USED IS INACTIVE")) {
				redir.addFlashAttribute("openModelBu", true);
			} else {
				redir.addFlashAttribute("error", e.getMessage());
			}
			redir.addFlashAttribute("eventId", eventId);
			redir.addFlashAttribute("eventType", eventType);

			redir.addFlashAttribute("businessUnits", businessUnitService.getPlainActiveBusinessUnitForTenant(SecurityLibrary.getLoggedInUserTenantId()));
			return "redirect:/buyer/createEvent/" + eventType;
		} catch (SubscriptionException e) {
			redir.addAttribute("error", "You have reached your subscription limit. " + e.getMessage());
			return "redirect:/buyer/buyerDashboard";
		} catch (Exception e) {
			LOG.error("Error during copy event : " + e.getMessage(), e);
			if (e.getMessage().equals("BUSINESS_UNIT_EMPTY")) {
				redir.addFlashAttribute("openModelBu", true);
			} else if (e.getMessage().equals("TEMPLATE IS BEING USED IS INACTIVE")) {
				redir.addFlashAttribute("openModelBu", true);
			} else {
				redir.addFlashAttribute("error", "Error during copy event : " + e.getMessage());
			}
			redir.addFlashAttribute("eventId", eventId);
			redir.addFlashAttribute("eventType", eventType);

			redir.addFlashAttribute("businessUnits", businessUnitService.getPlainActiveBusinessUnitForTenant(SecurityLibrary.getLoggedInUserTenantId()));
			return "redirect:/buyer/createEvent/" + eventType;

		}

		return "";
	}

	@RequestMapping(path = "/copyFromTemplate", method = RequestMethod.POST)
	public String copyFromTemplate(Model model, @RequestParam(value = "templateId") String templateId, @RequestParam(value = "businessUnitId", required = false) String businessUnitId, @RequestParam(value = "eventType") String eventType, RedirectAttributes redir) {
		try {
			BusinessUnit businessUnit = null;
			if (StringUtils.isNotBlank(businessUnitId))
				businessUnit = businessUnitService.getBusinessUnitById(businessUnitId);
			LOG.info("========================" + businessUnitId);

			switch (RfxTypes.valueOf(eventType)) {
			case RFA:
				RfaEvent newRfaEvent = rfaEventService.copyFromTemplate(templateId, SecurityLibrary.getLoggedInUser(), businessUnit);
				newRfaEvent.setBillOfQuantity(Boolean.TRUE);
				rfxTemplateService.updateTemplateUsed(templateId);
				return "redirect:/buyer/" + eventType + "/createEventDetails/" + newRfaEvent.getId();
			case RFI:
				RfiEvent newRfiEvent = rfiEventService.copyFromTemplate(templateId, SecurityLibrary.getLoggedInUser(), businessUnit);
				rfxTemplateService.updateTemplateUsed(templateId);
				return "redirect:/buyer/" + eventType + "/createEventDetails/" + newRfiEvent.getId();
			case RFP:
				RfpEvent newRfpEvent = rfpEventService.copyFromTemplate(templateId, SecurityLibrary.getLoggedInUser(), businessUnit);
				rfxTemplateService.updateTemplateUsed(templateId);
				newRfpEvent.setBillOfQuantity(Boolean.TRUE);
				return "redirect:/buyer/" + eventType + "/createEventDetails/" + newRfpEvent.getId();
			case RFQ:
				RfqEvent newRfqEvent = rfqEventService.copyFromTemplate(templateId, SecurityLibrary.getLoggedInUser(), businessUnit);
				rfxTemplateService.updateTemplateUsed(templateId);
				newRfqEvent.setBillOfQuantity(Boolean.TRUE);
				return "redirect:/buyer/" + eventType + "/createEventDetails/" + newRfqEvent.getId();
			case RFT:
				RftEvent newEvent = rftEventService.copyFromTemplate(templateId, SecurityLibrary.getLoggedInUser(), businessUnit);
				rfxTemplateService.updateTemplateUsed(templateId);
				newEvent.setBillOfQuantity(Boolean.TRUE);
				return "redirect:/buyer/" + eventType + "/createEventDetails/" + newEvent.getId();
			default:
				break;
			}
		} catch (ApplicationException e) {
			LOG.error("=============We are here======================================" + e.getMessage());
			if (e.getMessage().equals("BUSINESS_UNIT_EMPTY")) {
				redir.addFlashAttribute("openModelForTemplateBu", true);
			} else {
				redir.addFlashAttribute("error", e.getMessage());
			}
			redir.addFlashAttribute("templateId", templateId);
			redir.addFlashAttribute("eventType", eventType);
			redir.addFlashAttribute("businessUnits", businessUnitService.getPlainActiveBusinessUnitForTenant(SecurityLibrary.getLoggedInUserTenantId()));
			return "redirect:/buyer/createEvent/" + eventType;
		}

		catch (SubscriptionException e) {
			redir.addAttribute("error", "You have reached your subscription limit. " + e.getMessage());
			return "redirect:/buyer/buyerDashboard";
		}
		return "";
	}

	@RequestMapping(value = "searchCategory", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<IndustryCategory>> searchCategory(@RequestParam("search") String search) {
		LOG.info("Search Category: " + search);
		String tenantId = SecurityLibrary.getLoggedInUserTenantId();
		List<IndustryCategory> bicList = industryCategoryService.findIndustryCategoryByNameAndTenantId(search, tenantId);
		return new ResponseEntity<List<IndustryCategory>>(bicList, HttpStatus.OK);
	}

	private void isBusinessSettingEnable(String type) throws ApplicationException {

		if (eventIdSettingsDao.isBusinessSettingEnable(SecurityLibrary.getLoggedInUserTenantId(), type)) {
			throw new ApplicationException("BUSINESS_UNIT_EMPTY");
		}
	}

	/*
	 * private void sendTeamMemberEmailNotificationEmail(User user, TeamMemberType memberType, User cretedBy, String
	 * eventName, String eventId, String referanceNumber) { try { String subject = "Invited as TEAM MEMBER"; String url
	 * = APP_URL; HashMap<String, Object> map = new HashMap<String, Object>(); map.put("userName", user.getName());
	 * map.put("memberType", memberType); if (memberType == TeamMemberType.Editor) map.put("memberMessage",
	 * "accsess allows you to edit the whole draft Event but not finish the Event"); else if (memberType ==
	 * TeamMemberType.Viewer) map.put("memberMessage",
	 * "accsess allows you to view entire draft stage of the Event without the ability to edit"); else
	 * map.put("memberMessage", "accsess allows you to all actions that Event owner can do"); map.put("eventName",
	 * eventName); map.put("createdBy", cretedBy.getName()); map.put("eventId", eventId); map.put("eventRefNum",
	 * referanceNumber); SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)"); String timeZone =
	 * "GMT+8:00"; timeZone = getTimeZoneByBuyerSettings(user.getTenantId(), timeZone);
	 * df.setTimeZone(TimeZone.getTimeZone(timeZone)); map.put("date", df.format(new Date())); map.put("loginUrl",
	 * APP_URL + "/login"); map.put("appUrl", url); String message =
	 * FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerConfiguration.getTemplate(Global.TEAM_MEMBER_TEMPLATE
	 * ), map); notificationService.sendEmail(user.getCommunicationEmail(), subject, message); } catch (Exception e) {
	 * LOG.error("error in sending team member email " + e.getMessage(), e); } String notificationMessage =
	 * messageSource.getMessage("po.create.notification.message", new Object[] { pr.getName() }, Global.LOCALE);
	 * sendDashboardNotification(user, url, subject, notificationMessage, NotificationType.CREATED_MESSAGE); }
	 *//*
		 * private String getTimeZoneByBuyerSettings(String tenantId, String timeZone) { try { if
		 * (StringUtils.checkString(tenantId).length() > 0) { String time =
		 * buyerSettingsService.getBuyerTimeZoneByTenantId(tenantId); if (time != null) { timeZone = time; } } } catch
		 * (Exception e) { LOG.error("Error while fetching buyer time zone :" + e.getMessage(), e); } return timeZone; }
		 */

	@ModelAttribute("auctionsTypeList")
	public String[] getauctionsTypeList() {
		return new String[] { "FORWARD", "REVERSE" };
	}

	@RequestMapping(value = "/auctionReport", method = RequestMethod.GET)
	public String auctionReport() {
		return "auctionReportList";
	}

	@RequestMapping(value = "/auctionReportList", method = RequestMethod.GET)
	public ResponseEntity<TableData<DraftEventPojo>> auctionReportList(HttpSession session, TableDataInput input, @RequestParam(required = false) String dateTimeRange, @RequestParam("auctionTypeS") String auctionTypeS) {
		TableData<DraftEventPojo> data = null;

		try {
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

			List<AuctionType> auctionTypeList = new ArrayList<AuctionType>();
			if (auctionTypeS.equals("FORWARD")) {
				LOG.info("auctionType------------>" + auctionTypeS);
				auctionTypeList.add(AuctionType.FORWARD_DUTCH);
				auctionTypeList.add(AuctionType.FORWARD_ENGISH);
				auctionTypeList.add(AuctionType.FORWARD_SEALED_BID);

			} else {
				LOG.info("auctionType------------>" + auctionTypeS);
				auctionTypeList.add(AuctionType.REVERSE_DUTCH);
				auctionTypeList.add(AuctionType.REVERSE_ENGISH);
				auctionTypeList.add(AuctionType.REVERSE_SEALED_BID);
			}

			data = new TableData<DraftEventPojo>(rfaEventService.getAuctionEventsForAuctionSummaryReport(SecurityLibrary.getLoggedInUserTenantId(), input, startDate, endDate, auctionTypeList, auctionTypeS));
			data.setDraw(input.getDraw());
			long filterTotalCount = rfaEventService.getAuctionEventsCountForBuyerEventReport(SecurityLibrary.getLoggedInUserTenantId(), input, startDate, endDate, auctionTypeList, auctionTypeS);
			long totalCount = rfaEventService.findTotalEventForBuyer(SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null : SecurityLibrary.getLoggedInUser().getId(), auctionTypeList, auctionTypeS);
			data.setRecordsTotal(totalCount);
			data.setRecordsFiltered(filterTotalCount);

		} catch (Exception e) {
			LOG.error("Error while loading auction report list : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<DraftEventPojo>>(data, HttpStatus.OK);
	}

	@RequestMapping(path = "/ExportAuctionReport", method = RequestMethod.POST)
	public void downloadAuctionReport(HttpSession session, HttpServletRequest request, HttpServletResponse response, @ModelAttribute("searchFilterEventPojo") SearchFilterEventPojo searchFilterEventPojo, boolean select_all, @RequestParam(required = false) String eventtype, @RequestParam String dateTimeRange, @RequestParam("auctionTypeS") String auctionTypeS) throws IOException {
		try {
			String EventArr[] = null;
			if (StringUtils.checkString(searchFilterEventPojo.getEventIds()).length() > 0) {
				EventArr = searchFilterEventPojo.getEventIds().split(",");
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
			String fileName = "Auction_Summary_Report.xlsx";
			Path file = Paths.get(downloadFolder, fileName);
			LOG.info("File Path ::" + file);

			auctionReportDownloader(workbook, EventArr, session, searchFilterEventPojo, select_all, eventtype, startDate, endDate, auctionTypeS);

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
			LOG.error("Error while downloading Auction Report List :: " + e.getMessage(), e);
		}
	}

	private void auctionReportDownloader(XSSFWorkbook workbook, String[] eventIds, HttpSession session, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, String eventtype, Date startDate, Date endDate, String auctionTypeS) throws IOException, Exception {

		XSSFSheet sheet = workbook.createSheet("Auction Report List");
		int r = 1;
		buildAuctionHeader(workbook, sheet, auctionTypeS);

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		SimpleDateFormat deliveryDate = new SimpleDateFormat("dd/MM/yyyy ");
		if (session != null && session.getAttribute(Global.SESSION_TIME_ZONE_KEY) != null) {
			sdf.setTimeZone(TimeZone.getTimeZone((String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY)));
			deliveryDate.setTimeZone(TimeZone.getTimeZone((String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY)));
		} else {
			sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
			deliveryDate.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));

		}

		List<DraftEventPojo> eventList = getSearchAuctionEventDetails(eventIds, searchFilterEventPojo, select_all, startDate, endDate, sdf, auctionTypeS);

		if (CollectionUtil.isNotEmpty(eventList)) {
			for (DraftEventPojo eventReport : eventList) {

				Row row = sheet.createRow(r++);
				int cellNum = 0;
				row.createCell(cellNum++).setCellValue(eventReport.getUnitName() != null ? eventReport.getUnitName() : "");
				row.createCell(cellNum++).setCellValue(eventReport.getEventCategories() != null ? eventReport.getEventCategories() : "");
				row.createCell(cellNum++).setCellValue(eventReport.getReferanceNumber() != null ? eventReport.getReferanceNumber() : "");
				row.createCell(cellNum++).setCellValue(eventReport.getSysEventId() != null ? eventReport.getSysEventId() : "");
				row.createCell(cellNum++).setCellValue(eventReport.getEventStart() != null ? sdf.format(eventReport.getEventStart()) : "");
				row.createCell(cellNum++).setCellValue(eventReport.getEventEnd() != null ? sdf.format(eventReport.getEventEnd()) : "");
				row.createCell(cellNum++).setCellValue(eventReport.getEventName() != null ? eventReport.getEventName() : "");
				row.createCell(cellNum++).setCellValue(eventReport.getTemplateName());
				row.createCell(cellNum++).setCellValue(eventReport.getEventVisibility() != null ? eventReport.getEventVisibility().name() : "");
				row.createCell(cellNum++).setCellValue(eventReport.getEventUser() != null ? eventReport.getEventUser() : "");
				row.createCell(cellNum++).setCellValue(eventReport.getAuctionType());
				row.createCell(cellNum++).setCellValue(String.valueOf(eventReport.getInvitedSupplierCount()));
				row.createCell(cellNum++).setCellValue(String.valueOf(eventReport.getSelfInvitedSupplierCount()));
				row.createCell(cellNum++).setCellValue(String.valueOf(eventReport.getParticipatedSupplierCount()));
				row.createCell(cellNum++).setCellValue(String.valueOf(eventReport.getSubmittedSupplierCount()));
				row.createCell(cellNum++).setCellValue(String.valueOf(eventReport.getRatio()));
				row.createCell(cellNum++).setCellValue(String.valueOf(eventReport.getNoOfBids()));
				row.createCell(cellNum++).setCellValue(eventReport.getCurrencyName() != null ? eventReport.getCurrencyName() : " ");
				row.createCell(cellNum++).setCellValue(eventReport.getLeadingSuppierBid() != null ? (eventReport.getLeadingSuppierBid().toString()) : "");
				row.createCell(cellNum++).setCellValue(eventReport.getLeadingSuppier() != null ? eventReport.getLeadingSuppier() : "");
				row.createCell(cellNum++).setCellValue(eventReport.getSelfInvitedWinner());
				row.createCell(cellNum++).setCellValue(eventReport.getSupplierTags() != null ? eventReport.getSupplierTags() : "");
				row.createCell(cellNum++).setCellValue(eventReport.getSumAwardedPrice() != null ? eventReport.getSumAwardedPrice().toString() : ""); // Awarded
																																						// Price
				// row.createCell(cellNum++).setCellValue(eventReport.getAwardedSuppliers() != null ?
				// eventReport.getAwardedSuppliers() : "");
				row.createCell(cellNum++).setCellValue(eventReport.getBudgetAmount() != null ? (eventReport.getBudgetAmount().compareTo(BigDecimal.ZERO) == 0 ? "0.00" : eventReport.getBudgetAmount().toString()) : "");
				row.createCell(cellNum++).setCellValue(eventReport.getHistricAmount() != null ? (eventReport.getHistricAmount().compareTo(BigDecimal.ZERO) == 0 ? "0.00" : eventReport.getHistricAmount().toString()) : "");
				row.createCell(cellNum++).setCellValue(eventReport.getSavingsBudget() != null ? (eventReport.getBudgetAmount().compareTo(BigDecimal.ZERO) == 0 ? "N/A" : eventReport.getSavingsBudget()) : "");
				row.createCell(cellNum++).setCellValue(eventReport.getSavingsBudgetPercentage() != null ? (eventReport.getBudgetAmount().compareTo(BigDecimal.ZERO) == 0 ? "N/A" : eventReport.getSavingsBudgetPercentage()) : "");
				row.createCell(cellNum++).setCellValue(eventReport.getSavingsHistoric() != null ? (eventReport.getHistricAmount().compareTo(BigDecimal.ZERO) == 0 ? "N/A" : eventReport.getSavingsHistoric()) : "");
				row.createCell(cellNum++).setCellValue(eventReport.getSavingsHistoricPercentage() != null ? (eventReport.getHistricAmount().compareTo(BigDecimal.ZERO) == 0 ? "N/A" : eventReport.getSavingsHistoricPercentage()) : "");

			}
		}

		for (int k = 0; k < 10; k++) {
			sheet.autoSizeColumn(k, true);
		}

	}

	private List<DraftEventPojo> getSearchAuctionEventDetails(String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, Date startDate, Date endDate, SimpleDateFormat sdf, String auctionTypeS) {
		return rfaEventService.getAllSearchAuctionEventReportForBuyer(SecurityLibrary.getLoggedInUserTenantId(), eventIds, searchFilterEventPojo, select_all, startDate, endDate, sdf, auctionTypeS);
	}

	private void buildAuctionHeader(XSSFWorkbook workbook, XSSFSheet sheet, String auctionTypeS) {
		Row rowHeading = sheet.createRow(0);
		// Style of Heading Cells
		CellStyle styleHeading = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		styleHeading.setFont(font);
		styleHeading.setVerticalAlignment(CellStyle.ALIGN_CENTER);

		int i = 0;
		Cell cell = null;
		if (auctionTypeS.equals("FORWARD")) {
			for (String column : Global.AUCTION_EVENT_REPORT_FORWARD_EXCEL_COLUMNS) {
				cell = rowHeading.createCell(i++);
				cell.setCellValue(column);
				cell.setCellStyle(styleHeading);
			}
		} else {
			for (String column : Global.AUCTION_EVENT_REPORT_REVERSE_EXCEL_COLUMNS) {
				cell = rowHeading.createCell(i++);
				cell.setCellValue(column);
				cell.setCellStyle(styleHeading);
			}
		}
	}

	@RequestMapping(value = "/eventReport", method = RequestMethod.GET)
	public String buyerProfileFormView() {
		return "eventReportList";
	}

	@RequestMapping(value = "/eventReportList", method = RequestMethod.GET)
	public ResponseEntity<TableData<DraftEventPojo>> eventReportList(HttpSession session, TableDataInput input, @RequestParam(required = false) String dateTimeRange) {
		TableData<DraftEventPojo> data = null;
		LOG.info("Enter the POST Method of eventReportList");
		try {

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

			data = new TableData<DraftEventPojo>(rftEventService.getAllEventsForBuyerEventReport(SecurityLibrary.getLoggedInUserTenantId(), input, startDate, endDate));
			data.setDraw(input.getDraw());

			long filterTotalCount = rftEventService.getAllEventsCountForBuyerEventReport(SecurityLibrary.getLoggedInUserTenantId(), input, startDate, endDate);

			long totalCount = rftEventService.findTotalAdminEventByTenantId(SecurityLibrary.getLoggedInUserTenantId());

			// findTotalEventForBuyer(SecurityLibrary.getLoggedInUserTenantId(),
			// SecurityLibrary.ifAnyGranted("ROLE_ADMIN,ROLE_ADMIN_READONLY") ? null :
			// SecurityLibrary.getLoggedInUser().getId());
			data.setRecordsTotal(totalCount);
			data.setRecordsFiltered(filterTotalCount);

		} catch (Exception e) {
			LOG.error("Error while loading draft list : " + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<DraftEventPojo>>(data, HttpStatus.OK);
	}

	@RequestMapping(path = "/ExportEventReport", method = RequestMethod.POST)
	public void downloadEventReport(HttpSession session, HttpServletRequest request, HttpServletResponse response, @ModelAttribute("searchFilterEventPojo") SearchFilterEventPojo searchFilterEventPojo, boolean select_all, @RequestParam(required = false) String eventtype, @RequestParam String dateTimeRange) throws IOException {
		try {
			String EventArr[] = null;
			if (StringUtils.checkString(searchFilterEventPojo.getEventIds()).length() > 0) {
				EventArr = searchFilterEventPojo.getEventIds().split(",");
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
			String fileName = "EventReport.xlsx";
			Path file = Paths.get(downloadFolder, fileName);
			LOG.info("File Path ::" + file);

			eventReportDownloader(workbook, EventArr, session, searchFilterEventPojo, select_all, eventtype, startDate, endDate);

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
			LOG.error("Error while downloading Event Report List :: " + e.getMessage(), e);
		}
	}

	protected void eventReportDownloader(XSSFWorkbook workbook, String[] eventIds, HttpSession session, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, String eventtype, Date startDate, Date endDate) throws IOException, Exception {

		XSSFSheet sheet = workbook.createSheet("Event Report List");
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

		List<DraftEventPojo> eventList = getSearchEventDetails(eventIds, searchFilterEventPojo, select_all, startDate, endDate, sdf);

		if (CollectionUtil.isNotEmpty(eventList)) {
			for (DraftEventPojo eventReport : eventList) {

				DecimalFormat df = null;
				if (eventReport.getEventDecimal() != null) {
					if (eventReport.getEventDecimal().equals("1")) {
						df = new DecimalFormat("#,###,###,##0.0");
					} else if (eventReport.getEventDecimal().equals("2")) {
						df = new DecimalFormat("#,###,###,##0.00");
					} else if (eventReport.getEventDecimal().equals("3")) {
						df = new DecimalFormat("#,###,###,##0.000");
					} else if (eventReport.getEventDecimal().equals("4")) {
						df = new DecimalFormat("#,###,###,##0.0000");
					} else if (eventReport.getEventDecimal().equals("5")) {
						df = new DecimalFormat("#,###,###,##0.00000");
					} else if (eventReport.getEventDecimal().equals("6")) {
						df = new DecimalFormat("#,###,###,##0.000000");
					} else {
						df = new DecimalFormat("#,###,###,##0.00");
					}
				} else {
					df = new DecimalFormat("#,###,###,##0.00");
				}

				Row row = sheet.createRow(r++);
				int cellNum = 0;
				row.createCell(cellNum++).setCellValue(eventReport.getSysEventId() != null ? eventReport.getSysEventId() : "");
				row.createCell(cellNum++).setCellValue(eventReport.getReferenceNumber() != null ? eventReport.getReferenceNumber() : "");
				row.createCell(cellNum++).setCellValue(eventReport.getEventName() != null ? eventReport.getEventName() : "");
				row.createCell(cellNum++).setCellValue(eventReport.getEventDescription() != null ? eventReport.getEventDescription() : "");
				row.createCell(cellNum++).setCellValue(eventReport.getEventUser() != null ? eventReport.getEventUser() : "");
				row.createCell(cellNum++).setCellValue(eventReport.getPublishDate() != null ? sdf.format(eventReport.getPublishDate()) : "");
				row.createCell(cellNum++).setCellValue(eventReport.getEventStart() != null ? sdf.format(eventReport.getEventStart()) : "");
				row.createCell(cellNum++).setCellValue(eventReport.getEventEnd() != null ? sdf.format(eventReport.getEventEnd()) : "");
				row.createCell(cellNum++).setCellValue(eventReport.getDeliveryDate() != null ? deliveryDate.format(eventReport.getDeliveryDate()) : "");

				row.createCell(cellNum++).setCellValue(eventReport.getVisibility() != null ? eventReport.getVisibility().name() : "");
				row.createCell(cellNum++).setCellValue(eventReport.getValidityDays() != null ? eventReport.getValidityDays().toString() : "");
				row.createCell(cellNum++).setCellValue(eventReport.getType() != null ? eventReport.getType().name() : "");
				row.createCell(cellNum++).setCellValue(eventReport.getCurrencyName() != null ? eventReport.getCurrencyName() : " ");
				row.createCell(cellNum++).setCellValue(eventReport.getUnitName() != null ? eventReport.getUnitName() : "");
				row.createCell(cellNum++).setCellValue(eventReport.getCostCenter());
				row.createCell(cellNum++).setCellValue(eventReport.getStatus() != null ? (eventReport.getStatus().toString().equals("COMPLETE") ? "EVALUATED" : eventReport.getStatus().toString()) : "");
				row.createCell(cellNum++).setCellValue(eventReport.getLeadingSupplier() != null ? eventReport.getLeadingSupplier() : "");
				row.createCell(cellNum++).setCellValue(eventReport.getLeadingAmount() != null ? eventReport.getLeadingAmount().toString() : "");

				row.createCell(cellNum++).setCellValue(String.valueOf(eventReport.getInvitedSupplierCount()));
				row.createCell(cellNum++).setCellValue(String.valueOf(eventReport.getAcceptedSupplierCount()));
				row.createCell(cellNum++).setCellValue(String.valueOf(eventReport.getSubmittedSupplierCount()));
				row.createCell(cellNum++).setCellValue(eventReport.getEstimatedBudget() != null ? (eventReport.getEstimatedBudget().toString()) : "");
				row.createCell(cellNum++).setCellValue(eventReport.getHistricAmount() != null ? (eventReport.getHistricAmount().toString()) : "");
				// row.createCell(cellNum++).setCellValue(eventReport.getAvailableAmount() != null ?
				// eventReport.getAvailableAmount().toString() : "");
				row.createCell(cellNum++).setCellValue(eventReport.getTemplateName());
				row.createCell(cellNum++).setCellValue(eventReport.getAssoiciateOwner());

				row.createCell(cellNum++).setCellValue((eventReport.getUnmaskOwner() != null && eventReport.getViewUnmaskSupplerName() == Boolean.FALSE) ? eventReport.getUnmaskOwner() : "");
				String address = "";
				address += (StringUtils.checkString(eventReport.getAddressTitle()).length() > 0 ? (eventReport.getAddressTitle() + ",") : "");
				address += (StringUtils.checkString(eventReport.getLine1()).length() > 0 ? eventReport.getLine1() : "");
				row.createCell(cellNum++).setCellValue(address);
				row.createCell(cellNum++).setCellValue(eventReport.getAuctionType());
				row.createCell(cellNum++).setCellValue(eventReport.getParticipationFees().toString());
				row.createCell(cellNum++).setCellValue(eventReport.getDeposite() != null ? (eventReport.getDeposite().toString()) : "");
				row.createCell(cellNum++).setCellValue(String.valueOf(eventReport.getPreViewSupplierCount()));
				row.createCell(cellNum++).setCellValue(String.valueOf(eventReport.getRejectedSupplierCount()));
				row.createCell(cellNum++).setCellValue(String.valueOf(eventReport.getDisqualifedSuppliers()));
				// row.createCell(cellNum++).setCellValue(eventReport.getAwardedSupplier());
				row.createCell(cellNum++).setCellValue((eventReport.getEventConcludeDate() != null ? "Yes" : "No"));
				row.createCell(cellNum++).setCellValue(eventReport.getEventConcludeDate() != null ? sdf.format(eventReport.getEventConcludeDate()) : "");
				row.createCell(cellNum++).setCellValue(eventReport.getEventPushDate() != null ? "Yes" : "No");
				row.createCell(cellNum++).setCellValue(eventReport.getEventPushDate() != null ? sdf.format(eventReport.getEventPushDate()) : "");
				row.createCell(cellNum++).setCellValue(eventReport.getPrPushDate() != null ? "Yes" : "No");
				row.createCell(cellNum++).setCellValue(eventReport.getPrPushDate() != null ? sdf.format(eventReport.getPrPushDate()) : "");
				row.createCell(cellNum++).setCellValue(eventReport.getAwardDate() != null ? sdf.format(eventReport.getAwardDate()) : "");

				row.createCell(cellNum++).setCellValue(eventReport.getAvgGrandTotal() != null ? df.format(eventReport.getAvgGrandTotal()).toString() : "");
				row.createCell(cellNum++).setCellValue(eventReport.getEventCategories() != null ? eventReport.getEventCategories() : "");

			}
		}

		for (int k = 0; k < 10; k++) {
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
		for (String column : Global.ALL_EVENT_REPORT_EXCEL_COLUMNS) {
			cell = rowHeading.createCell(i++);
			cell.setCellValue(column);
			cell.setCellStyle(styleHeading);
		}
	}

	@ModelAttribute("statusList")
	public List<EventStatus> getStatusList() {
		return Arrays.asList(EventStatus.values());
	}

	@ModelAttribute("rfxList")
	public List<RfxTypes> getSrfxList() {
		return Arrays.asList(RfxTypes.values());
	}

	@ModelAttribute("eventTypeList")
	public List<EventVisibilityType> getEventTypeList() {
		return Arrays.asList(EventVisibilityType.values());
	}

	@ModelAttribute("newStatusList")
	public List<EventStatus> getNewStatusList() {
		return Arrays.asList(EventStatus.DRAFT, EventStatus.PENDING, EventStatus.APPROVED, EventStatus.CANCELED, EventStatus.SUSPENDED, EventStatus.ACTIVE, EventStatus.CLOSED, EventStatus.FINISHED, EventStatus.COMPLETE);
	}

	/*
	 * @RequestMapping(path = "/eventReportList", method = RequestMethod.POST) public
	 * ResponseEntity<List<DraftEventPojo>> getPoReportList(HttpSession session, HttpServletRequest request,
	 * HttpServletResponse response, @RequestParam(name = "dateTimeRange", required = false) String dateTimeRange) { try
	 * { LOG.info("dateTimeRange :" + dateTimeRange); Date startDate = null; Date endDate = null; if
	 * (StringUtils.checkString(dateTimeRange).length() > 0) { TimeZone timeZone = TimeZone.getDefault(); String
	 * strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY); if (strTimeZone != null) { timeZone =
	 * TimeZone.getTimeZone(strTimeZone); } if (StringUtils.checkString(dateTimeRange).length() > 0) { String
	 * dateTimeArr[] = dateTimeRange.split("-"); DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
	 * formatter.setTimeZone(timeZone); startDate = (Date) formatter.parse(dateTimeArr[0]); endDate = (Date)
	 * formatter.parse(dateTimeArr[1]); LOG.info("Start date : " + startDate + " End Date : " + endDate); } }
	 * TableDataInput input = new TableDataInput(); input.setStart(0); input.setLength(5000); List<DraftEventPojo> list
	 * = rftEventService.getAllEventForBuyer(SecurityLibrary.getLoggedInUserTenantId(), input, startDate, endDate);
	 * return new ResponseEntity<List<DraftEventPojo>>(list, HttpStatus.OK); } catch (Exception e) { HttpHeaders headers
	 * = new HttpHeaders(); LOG.error("Error While Filter po list :" + e.getMessage(), e); return new
	 * ResponseEntity<List<DraftEventPojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR); } }
	 */
	private List<DraftEventPojo> getEventDetails(String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, TableDataInput input, Date startDate, Date endDate) {
		String tenantId = SecurityLibrary.getLoggedInUserTenantId();
		List<DraftEventPojo> eventList = new ArrayList<DraftEventPojo>();
		RfxTypes rfxTypes = null;
		for (ColumnParameter cp : input.getColumns()) {
			if (Boolean.TRUE == cp.getSearchable() && cp.getSearch() != null && StringUtils.checkString(cp.getSearch().getValue()).length() > 0) {
				if (cp.getData().equals("type")) {
					String[] types = cp.getSearch().getValue().split(",");
					if (types != null && types.length > 0) {
						for (String ty : types) {
							rfxTypes = RfxTypes.valueOf(ty);
						}
					}
				}
			}
		}

		if (rfxTypes == null || rfxTypes == RfxTypes.RFT) {
			List<DraftEventPojo> tmpList = rftEventService.getAllExcelEventReportForBuyer(tenantId, eventIds, searchFilterEventPojo, select_all, input, startDate, endDate);
			if (CollectionUtil.isNotEmpty(tmpList)) {
				eventList.addAll(tmpList);
			}
		}
		if (rfxTypes == null || rfxTypes == RfxTypes.RFA) {
			List<DraftEventPojo> tmpList = rfaEventService.getAllExcelEventReportForBuyer(tenantId, eventIds, searchFilterEventPojo, select_all, input, startDate, endDate);
			if (CollectionUtil.isNotEmpty(tmpList)) {
				eventList.addAll(tmpList);
			}
		}
		if (rfxTypes == null || rfxTypes == RfxTypes.RFP) {
			List<DraftEventPojo> tmpList = rfpEventService.getAllExcelEventReportForBuyer(tenantId, eventIds, searchFilterEventPojo, select_all, input, startDate, endDate);
			if (CollectionUtil.isNotEmpty(tmpList)) {
				eventList.addAll(tmpList);
			}
		}

		if (rfxTypes == null || rfxTypes == RfxTypes.RFQ) {
			List<DraftEventPojo> tmpList = rfqEventService.getAllExcelEventReportForBuyer(tenantId, eventIds, searchFilterEventPojo, select_all, input, startDate, endDate);
			if (CollectionUtil.isNotEmpty(tmpList)) {
				eventList.addAll(tmpList);
			}
		}
		if (rfxTypes == null || rfxTypes == RfxTypes.RFI) {
			List<DraftEventPojo> tmpList = rfiEventService.getAllExcelEventReportForBuyer(tenantId, eventIds, searchFilterEventPojo, select_all, input, startDate, endDate);
			if (CollectionUtil.isNotEmpty(tmpList)) {
				eventList.addAll(tmpList);
			}
		}
		return eventList;
	}

	private List<DraftEventPojo> getSearchEventDetails(String[] eventIds, SearchFilterEventPojo searchFilterEventPojo, boolean select_all, Date startDate, Date endDate, SimpleDateFormat sdf) {
		return rftEventService.getAllExcelSearchEventReportForBuyer(SecurityLibrary.getLoggedInUserTenantId(), eventIds, searchFilterEventPojo, select_all, startDate, endDate, sdf);
	}

	@RequestMapping(value = "/searchCountryAndState", method = RequestMethod.POST)
	public ResponseEntity<TableData<SupplierSearchPojo>> searchCountryAndState(@RequestParam(required = false, value = "country") String country, @RequestParam(required = false, value = "state") String state,@RequestParam(required = false, value = "coverage") String coverage, @RequestParam(required = false, value = "eventType") String eventType, @RequestParam(required = false, value = "eventId") String eventId, @RequestParam(required = false, value = "supplierTagName") String supplierTagName, Boolean exclusive, Boolean inclusive) {
		List<SupplierSearchPojo> favSupplierList = new ArrayList<SupplierSearchPojo>();
		List<EventSupplier> eventSupplierList = new ArrayList<EventSupplier>();
		List<SupplierSearchPojo> supplierList = new ArrayList<>();
		SupplierSearchPojo supplierSearchPojo = new SupplierSearchPojo();
		supplierSearchPojo.setRegistrationOfCountry(country);
		String[] states = null;
		if (StringUtils.checkString(state).length() > 0) {

			states = state.split(",");
		}
		supplierSearchPojo.setState(states);
		String[] coverages = null;
		if(StringUtils.checkString(coverage).length() > 0){
			coverages = coverage.split(",");
		}
		supplierSearchPojo.setCoverage(coverages);
		String[] supplierTag = null;
		if (StringUtils.checkString(supplierTagName).length() > 0) {

			supplierTag = supplierTagName.split(",");
		}
		supplierSearchPojo.setSupplierTagName(supplierTag);

		if ((Boolean.FALSE == inclusive) && (Boolean.FALSE == exclusive)) {
			inclusive = Boolean.TRUE;
		}
		try {
			switch (RfxTypes.valueOf(eventType)) {
			case RFA:
				RfaEvent rfaeventId = rfaEventService.loadRfaEventById(eventId);
				RfaEvent rfaEvent = (RfaEvent) rfaeventId;

				if (rfaEvent.getTemplate() != null && Boolean.TRUE == rfaEvent.getTemplate().getSupplierBasedOnCategory()) {
					favSupplierList = rftEventSupplierService.favoriteSuppliersOfBuyerByState(SecurityLibrary.getLoggedInUserTenantId(), supplierSearchPojo, rfaEvent.getIndustryCategories(), exclusive, inclusive, eventType, eventId);
				} else {
					favSupplierList = rftEventSupplierService.favoriteSuppliersOfBuyer(SecurityLibrary.getLoggedInUserTenantId(), supplierSearchPojo, exclusive, inclusive, eventType, eventId);
				}

				eventSupplierList = rfaEventSupplierService.getAllSuppliersByEventId(eventId);
				break;
			case RFI:
				RfiEvent rfieventId = rfiEventService.getRfiEventByeventId(eventId);
				RfiEvent rfiEvent = (RfiEvent) rfieventId;
				if (rfiEvent.getTemplate() != null && Boolean.TRUE == rfiEvent.getTemplate().getSupplierBasedOnCategory()) {
					favSupplierList = rftEventSupplierService.favoriteSuppliersOfBuyerByState(SecurityLibrary.getLoggedInUserTenantId(), supplierSearchPojo, rfiEvent.getIndustryCategories(), exclusive, inclusive, eventType, eventId);
				} else {
					favSupplierList = rftEventSupplierService.favoriteSuppliersOfBuyer(SecurityLibrary.getLoggedInUserTenantId(), supplierSearchPojo, exclusive, inclusive, eventType, eventId);
				}

				eventSupplierList = rfiEventSupplierService.getAllSuppliersByEventId(eventId);
				break;
			case RFP:
				RfpEvent rfpeventId = rfpEventService.loadRfpEventById(eventId);
				RfpEvent rfpEvent = (RfpEvent) rfpeventId;
				if (rfpEvent.getTemplate() != null && Boolean.TRUE == rfpEvent.getTemplate().getSupplierBasedOnCategory()) {
					favSupplierList = rftEventSupplierService.favoriteSuppliersOfBuyerByState(SecurityLibrary.getLoggedInUserTenantId(), supplierSearchPojo, rfpEvent.getIndustryCategories(), exclusive, inclusive, eventType, eventId);
				} else {
					favSupplierList = rftEventSupplierService.favoriteSuppliersOfBuyer(SecurityLibrary.getLoggedInUserTenantId(), supplierSearchPojo, exclusive, inclusive, eventType, eventId);
				}

				eventSupplierList = rfpEventSupplierService.getAllSuppliersByEventId(eventId);
				break;
			case RFQ:
				RfqEvent rfqeventId = rfqEventService.loadRfqEventById(eventId);
				RfqEvent rfqEvent = (RfqEvent) rfqeventId;

				if (rfqEvent.getTemplate() != null && Boolean.TRUE == rfqEvent.getTemplate().getSupplierBasedOnCategory()) {
					favSupplierList = rftEventSupplierService.favoriteSuppliersOfBuyerByState(SecurityLibrary.getLoggedInUserTenantId(), supplierSearchPojo, rfqEvent.getIndustryCategories(), exclusive, inclusive, eventType, eventId);
				} else {
					favSupplierList = rftEventSupplierService.favoriteSuppliersOfBuyer(SecurityLibrary.getLoggedInUserTenantId(), supplierSearchPojo, exclusive, inclusive, eventType, eventId);
				}

				eventSupplierList = rfqEventSupplierService.getAllSuppliersByEventId(eventId);
				break;
			case RFT:
				RftEvent rfteventId = rftEventService.getRftEventByeventId(eventId);
				RftEvent rftEvent = (RftEvent) rfteventId;
				if (rftEvent.getTemplate() != null && Boolean.TRUE == rftEvent.getTemplate().getSupplierBasedOnCategory()) {
					favSupplierList = rftEventSupplierService.favoriteSuppliersOfBuyerByState(SecurityLibrary.getLoggedInUserTenantId(), supplierSearchPojo, rftEvent.getIndustryCategories(), exclusive, inclusive, eventType, eventId);
				} else {
					favSupplierList = rftEventSupplierService.favoriteSuppliersOfBuyer(SecurityLibrary.getLoggedInUserTenantId(), supplierSearchPojo, exclusive, inclusive, eventType, eventId);
				}
				eventSupplierList = rftEventSupplierService.getAllSuppliersByEventId(eventId);

			default:
				break;
			}
			List<SupplierSearchPojo> suppList = new ArrayList<>();
			boolean addSupplierList = true;

			if (CollectionUtil.isNotEmpty(favSupplierList)) {
				for (SupplierSearchPojo supplierSearchPojo1 : favSupplierList) {
					supplierSearchPojo1.setId(supplierSearchPojo1.getId());
					suppList.add(supplierSearchPojo1);
				}
				for (SupplierSearchPojo eventSupplier : suppList) {
					addSupplierList = true;
					for (EventSupplier eventSupplier1 : eventSupplierList) {
						if (eventSupplier.getId().equals(eventSupplier1.getSupplier().getId())) {
							addSupplierList = false;
							break;
						}
					}
					if (addSupplierList) {
						supplierList.add(eventSupplier);
					}
				}
			}
		} catch (Exception e) {
			LOG.error("error while add supplier" + e.getMessage(), e);
		}
		TableData<SupplierSearchPojo> data = new TableData<SupplierSearchPojo>(supplierList);
		return new ResponseEntity<TableData<SupplierSearchPojo>>(data, HttpStatus.OK);
	}

	@RequestMapping(path = "/exportCsvReport", method = RequestMethod.POST)
	public void downloadEventCsvReport(HttpSession session, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redir, @ModelAttribute("searchFilterEventPojo") SearchFilterEventPojo searchFilterEventPojo, boolean select_all, @RequestParam(required = false) String eventtype, @RequestParam String dateTimeRange) throws IOException {
		try {
			File file = File.createTempFile("EventReport-", ".csv");
			LOG.info("Temp file : " + file.getAbsolutePath());

			String eventArr[] = null;
			if (StringUtils.checkString(searchFilterEventPojo.getEventIds()).length() > 0) {
				eventArr = searchFilterEventPojo.getEventIds().split(",");
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

			rftEventService.downloadCsvFileForEvents(response, file, eventArr, searchFilterEventPojo, startDate, endDate, select_all, SecurityLibrary.getLoggedInUserTenantId(), session);

			try {
				BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Event Report is downloaded ",  SecurityLibrary.getLoggedInUserTenantId(),SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.EventReport);
				buyerAuditTrailDao.save(ownerAuditTrail);
			} catch (Exception e) {
				LOG.info("Error to create audit trails message");
			}
			
			
			// Send to UI
			response.setContentType("text/csv");
			response.addHeader("Content-Disposition", "attachment; filename=EventReport.csv");
			response.setContentLengthLong(file.length());
			Files.copy(file.toPath(), response.getOutputStream());
			response.getOutputStream().flush();

		} catch (Exception e) {
			LOG.error("Error while downloading csv file : " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("product.error.download.csv", new Object[] { e.getMessage() }, Global.LOCALE));
		}
	}

	@RequestMapping(path = "/exportAuctionCsvReport", method = RequestMethod.POST)
	public void downloadAuctionCsvReport(HttpSession session, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redir, @ModelAttribute("searchFilterEventPojo") SearchFilterEventPojo searchFilterEventPojo, boolean select_all, @RequestParam(required = false) String eventtype, @RequestParam String dateTimeRange, @RequestParam("auctionTypeS") String auctionTypeS) throws IOException {
		try {
			File file = File.createTempFile("Auction_Summary_Report-", ".csv");
			LOG.info("Temp file : " + file.getAbsolutePath());

			String eventArr[] = null;
			if (StringUtils.checkString(searchFilterEventPojo.getEventIds()).length() > 0) {
				eventArr = searchFilterEventPojo.getEventIds().split(",");
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

			rfaEventService.downloadCsvFileForEvents(response, file, eventArr, searchFilterEventPojo, startDate, endDate, select_all, auctionTypeS, SecurityLibrary.getLoggedInUserTenantId(), session);
		
			try {
				BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Auction Summary Report is downloaded",  SecurityLibrary.getLoggedInUserTenantId(),SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.AuctionSummaryReport);
				buyerAuditTrailDao.save(ownerAuditTrail);
			} catch (Exception e) {
				LOG.info("Error to create audit trails message");
			}
			
			// Send to UI
			response.setContentType("text/csv");
			response.addHeader("Content-Disposition", "attachment; filename=Auction_Summary_Report.csv");
			response.setContentLengthLong(file.length());
			Files.copy(file.toPath(), response.getOutputStream());
			response.getOutputStream().flush();

		} catch (Exception e) {
			LOG.error("Error while downloading csv file : " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("product.error.download.csv", new Object[] { e.getMessage() }, Global.LOCALE));
		}
	}

	@RequestMapping(value = "/rfxTemplatePagination", method = RequestMethod.POST)
	public ResponseEntity<TableData<RfxTemplate>> rfxTemplatePagination(@RequestParam(required = false, name = "searchValue") String searchValue, @RequestParam(required = false, name = "pageNo") Integer pageNo, @RequestParam(required = false, name = "pageLength") Integer pageLength, @RequestParam(required = false, value = "eventType") String eventType) {
		LOG.info("inside the search pr create controller : ");

		HttpHeaders headers = new HttpHeaders();
		List<RfxTemplate> searchResultTemplate = null;
		TableData<RfxTemplate> data = null;
		try {
			searchResultTemplate = rfxTemplateService.findTemplateByTemplateNameForTenant(searchValue, pageNo, pageLength, RfxTypes.valueOf(eventType), SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId());

			long prCount = rfxTemplateService.findActiveTemplateCountByRfxTypeForTenantId(searchValue, SecurityLibrary.getLoggedInUserTenantId(), RfxTypes.valueOf(eventType), SecurityLibrary.getLoggedInUser().getId());

			data = new TableData<RfxTemplate>(searchResultTemplate);
			data.setRecordsTotal(prCount);
		} catch (Exception e) {
			headers.add("error", e.getMessage());
			LOG.error("Error While searchPrTemplate :" + e.getMessage(), e);
		}
		return new ResponseEntity<TableData<RfxTemplate>>(data, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/rfxEventPagination", method = RequestMethod.POST)
	public ResponseEntity<TableData<Event>> rfxEventPagination(@RequestParam(required = false, value = "searchValue") String searchValue, @RequestParam(required = false, value = "industryCategory") String industryCategory, @RequestParam(required = false, value = "eventType") String eventType, @RequestParam(required = false, name = "pageNo") Integer pageNo, @RequestParam(required = false, name = "pageLength") Integer pageLength) {
		LOG.info("inside the search controller : ");
		HttpHeaders headers = new HttpHeaders();
		LOG.info("event Type : " + eventType + " name or desc : " + searchValue + "  induscat id : " + industryCategory + " tenante Id :   " + SecurityLibrary.getLoggedInUserTenantId());

		List<Event> searchResultEvents = new ArrayList<Event>();
		TableData<Event> data = null;
		long prCount;
		try {
			if (SecurityLibrary.isLoggedInUserAdmin()) {
				searchResultEvents = rftEventService.findEventsByEventNameAndRefNumAndIndCatForTenant(searchValue, pageNo, pageLength, industryCategory, RfxTypes.valueOf(eventType), SecurityLibrary.getLoggedInUserTenantId(), null);
				prCount = rftEventService.findActiveEventCountByRfxTypeForTenantId(searchValue, SecurityLibrary.getLoggedInUserTenantId(), RfxTypes.valueOf(eventType), null, industryCategory);
			} else {
				searchResultEvents = rftEventService.findEventsByEventNameAndRefNumAndIndCatForTenant(searchValue, pageNo, pageLength, industryCategory, RfxTypes.valueOf(eventType), SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser().getId());
				prCount = rftEventService.findActiveEventCountByRfxTypeForTenantId(searchValue, SecurityLibrary.getLoggedInUserTenantId(), RfxTypes.valueOf(eventType), SecurityLibrary.getLoggedInUser().getId(), industryCategory);
			}

			data = new TableData<Event>(searchResultEvents);
			data.setRecordsTotal(prCount);
		} catch (Exception e) {
			headers.add("error", e.getMessage());
			LOG.error("Error While searchPrTemplate :" + e.getMessage(), e);
		}

		return new ResponseEntity<TableData<Event>>(data, headers, HttpStatus.OK);
	}

}

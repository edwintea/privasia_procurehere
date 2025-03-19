package com.privasia.procurehere.web.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.codec.binary.Base64;

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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.privasia.procurehere.core.entity.Announcement;
import com.privasia.procurehere.core.entity.Bq;
import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.BuyerSettings;
import com.privasia.procurehere.core.entity.Country;
import com.privasia.procurehere.core.entity.IndustryCategory;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.SupplierSubscription;
import com.privasia.procurehere.core.entity.Supplier.SupplierSignup;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.SupplierStatus;
import com.privasia.procurehere.core.pojo.EventPojo;
import com.privasia.procurehere.core.pojo.PublicEventPojo;
import com.privasia.procurehere.core.pojo.SupplierPojo;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.AnnouncementService;
import com.privasia.procurehere.service.BuyerService;
import com.privasia.procurehere.service.BuyerSettingsService;
import com.privasia.procurehere.service.CountryService;
import com.privasia.procurehere.service.EventAuditService;
import com.privasia.procurehere.service.RfaBqService;
import com.privasia.procurehere.service.RfaEventService;
import com.privasia.procurehere.service.RfaMeetingService;
import com.privasia.procurehere.service.RfiEventService;
import com.privasia.procurehere.service.RfiMeetingService;
import com.privasia.procurehere.service.RfpBqService;
import com.privasia.procurehere.service.RfpEventService;
import com.privasia.procurehere.service.RfpMeetingService;
import com.privasia.procurehere.service.RfqBqService;
import com.privasia.procurehere.service.RfqEventService;
import com.privasia.procurehere.service.RfqMeetingService;
import com.privasia.procurehere.service.RftBqService;
import com.privasia.procurehere.service.RftEventService;
import com.privasia.procurehere.service.RftMeetingService;
import com.privasia.procurehere.web.editors.BuyerEditor;
import com.privasia.procurehere.web.editors.CountryEditor;

@Controller
@RequestMapping("/")
public class PublicEventsController {

	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@Autowired
	RftEventService rftEventService;

	@Autowired
	RfpEventService rfpEventService;

	@Autowired
	RfqEventService rfqEventService;

	@Autowired
	RfiEventService rfiEventService;

	@Autowired
	RfaEventService rfaEventService;

	@Autowired
	CountryService countryService;

	@Autowired
	EventAuditService eventAuditService;

	@Autowired
	CountryEditor countryEditor;

	@Autowired
	BuyerEditor buyerEditor;

	@Autowired
	BuyerSettingsService buyerSettingsService;

	@Autowired
	BuyerService buyerService;

	@Autowired
	AnnouncementService announcementService;

	@Autowired
	RftBqService rftBqService;

	@Autowired
	RfqBqService rfqBqService;

	@Autowired
	RfpBqService rfpBqService;

	@Autowired
	RfaBqService rfaBqService;

	@Autowired
	RftMeetingService rftMeetingService;

	@Autowired
	RfpMeetingService rfpMeetingService;

	@Autowired
	RfqMeetingService rfqMeetingService;

	@Autowired
	RfiMeetingService rfiMeetingService;

	@Autowired
	RfaMeetingService rfaMeetingService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Country.class, countryEditor);
		binder.registerCustomEditor(Buyer.class, buyerEditor);
	}

	@ModelAttribute("eventTypeList")
	public List<RfxTypes> getEventTypeList() {
		return Arrays.asList(RfxTypes.values());

	}

	@RequestMapping(path = "/publicEvents/{buyerId}", method = { RequestMethod.GET })
	public String publicEvents(@ModelAttribute PublicEventPojo publicEventPojo, @PathVariable("buyerId") String buyerId, Model model) {
		LOG.info(" *********** PublicEventsController() called *************** " + buyerId);
		String tenantId = buyerService.getTenantIdByPublicContextPath(buyerId);

		// WTH?? Why is this check required here??
		/*
		 * if (StringUtils.checkString(tenantId).length() == 0) { tenantId = buyerService.getTenantId(buyerId); }
		 */
		if (StringUtils.checkString(tenantId).length() > 0) {
			String strtimeZone = buyerSettingsService.getBuyerTimeZoneByTenantId(tenantId);
			TimeZone timezone = TimeZone.getDefault();
			if (StringUtils.checkString(strtimeZone).length() > 0) {
				timezone = TimeZone.getTimeZone(strtimeZone);
			}
			model.addAttribute("timezone", timezone);
			model.addAttribute("tenantId", tenantId);
			List<Announcement> announcementList = announcementService.getAnnouncementListByBuyerId(tenantId);
			model.addAttribute("announcementList", announcementList);
			BuyerSettings buyerSettings = buyerSettingsService.getBuyerSettingsByTenantId(tenantId);
			try {
				if (buyerSettings != null && buyerSettings.getFileAttatchment() != null) {
					byte[] encodeBase64 = Base64.encodeBase64(buyerSettings.getFileAttatchment());
					String base64Encoded = new String(encodeBase64, "UTF-8");
					model.addAttribute("logoImg", base64Encoded);
				}

			} catch (Exception e) {
				LOG.error("Error while  encoding image :" + e.getMessage());
			}
			return "publicEvents";
		} else {
			return "redirect:/404_error";
		}
	}

	@RequestMapping(path = "/publicEventsListData/{buyerId}", method = RequestMethod.GET)
	public ResponseEntity<TableData<PublicEventPojo>> publicEventList(@PathVariable("buyerId") String buyerId, TableDataInput input, Model model) throws JsonProcessingException {
		LOG.info("Getting public Events List.." + buyerId);
		try {
			TableData<PublicEventPojo> data = new TableData<PublicEventPojo>(rftEventService.findActivePublicEventsListByTenantId(buyerId, input));
			data.setDraw(input.getDraw());
			return new ResponseEntity<TableData<PublicEventPojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching Public Events List : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching  Public Events List : " + e.getMessage());
			return new ResponseEntity<TableData<PublicEventPojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/viewPublicEventSummary/{eventType}/{eventId}/{buyerId}", method = { RequestMethod.GET })
	public ModelAndView publicEvents(@PathVariable(name = "eventType") RfxTypes eventType, @PathVariable("eventId") String eventId, @PathVariable("buyerId") String buyerId, Model model) {
		LOG.info(eventType + "  viewPublicEventSummary() called  " + eventId);
		EventPojo event = null;
		List<?> eventContacts = null;
		boolean siteVisit = false;
		List<Bq> bqList = null;
		List<IndustryCategory> industryCategories = null;
		switch (eventType) {
		case RFT:
			try {
				event = rftEventService.getRftForPublicEventByeventId(eventId);
				industryCategories = rftEventService.getIndustryCategoriesForRftById(eventId);
				eventContacts = rftEventService.getAllContactForEvent(eventId);
				bqList = rftBqService.findRftBqbyEventId(event.getId());
				siteVisit = rftMeetingService.isSiteVisitExist(eventId);

			} catch (Exception e) {
				LOG.error("Error while getting public RFT event summary " + e.getMessage(), e);
			}
			break;
		case RFP:
			try {
				event = rfpEventService.getRfpForPublicEventByeventId(eventId);
				industryCategories = rfpEventService.getIndustryCategoriesForRfpById(eventId);
				eventContacts = rfpEventService.getAllContactForEvent(eventId);
				bqList = rfpBqService.findBqbyEventId(eventId);
				siteVisit = rfpMeetingService.isSiteVisitExist(eventId);
			} catch (Exception e) {
				LOG.error("Error while getting public RFP event summary" + e.getMessage(), e);
			}
			break;
		case RFQ:
			try {
				event = rfqEventService.getRfqForPublicEventByeventId(eventId);
				industryCategories = rfqEventService.getIndustryCategoriesForRfqById(eventId);
				eventContacts = rfqEventService.getAllContactForEvent(eventId);
				bqList = rfqBqService.findBqbyEventId(eventId);
				siteVisit = rfqMeetingService.isSiteVisitExist(eventId);
			} catch (Exception e) {
				LOG.error("Error while getting public RFQ event summary " + e.getMessage(), e);
			}
			break;
		case RFI:
			try {
				event = rfiEventService.getRfiForPublicEventByeventId(eventId);
				industryCategories = rfiEventService.getIndustryCategoriesForRfiById(eventId);
				eventContacts = rfiEventService.getAllContactForEvent(eventId);
				siteVisit = rfiMeetingService.isSiteVisitExist(eventId);
			} catch (Exception e) {
				LOG.error("Error while getting public RFI event summary " + e.getMessage(), e);
			}
			break;
		case RFA:
			try {
				event = rfaEventService.getRfaForPublicEventByeventId(eventId);
				industryCategories = rfaEventService.getIndustryCategoriesForRfaById(eventId);
				eventContacts = rfaEventService.getAllContactForEvent(eventId);
				bqList = rfaBqService.findRfaBqbyEventId(eventId);
				siteVisit = rfaMeetingService.isSiteVisitExist(eventId);
			} catch (Exception e) {
				LOG.error("Error while getting public RFA event summary" + e.getMessage(), e);
			}
			break;
		}
		if (event != null) {
			String strtimeZone = buyerSettingsService.getBuyerTimeZoneByTenantId(buyerId);
			TimeZone timezone = TimeZone.getDefault();
			if (StringUtils.checkString(strtimeZone).length() > 0) {
				timezone = TimeZone.getTimeZone(strtimeZone);
			}
			model.addAttribute("timeZone", timezone);
		}

		if (CollectionUtil.isNotEmpty(bqList)) {
			model.addAttribute("bqList", bqList);
		}
		model.addAttribute("industryCategories", industryCategories);

		String contextPath = buyerService.getContextPathByBuyerId(buyerId);

		contextPath = (StringUtils.checkString(contextPath).length() > 0) ? contextPath : buyerId;

		model.addAttribute("buyerContextPath", contextPath);
		model.addAttribute("tenantId", buyerId);
		model.addAttribute("siteVisit", siteVisit);
		model.addAttribute("eventContacts", eventContacts);
		model.addAttribute("event", event);
		model.addAttribute("eventType", eventType);
		return new ModelAndView("publicEventSummary");
	}

}
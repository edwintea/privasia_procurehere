package com.privasia.procurehere.web.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.RfaEventDao;
import com.privasia.procurehere.core.dao.RfaEventSupplierDao;
import com.privasia.procurehere.core.dao.RfaSupplierBqDao;
import com.privasia.procurehere.core.entity.AuctionBids;
import com.privasia.procurehere.core.entity.AuctionRules;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.Event;
import com.privasia.procurehere.core.entity.NotificationMessage;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.entity.RfaEventAudit;
import com.privasia.procurehere.core.entity.RfaEventBq;
import com.privasia.procurehere.core.entity.RfaSupplierBqItem;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.AuctionConsolePriceVenderType;
import com.privasia.procurehere.core.enums.AuctionType;
import com.privasia.procurehere.core.enums.AuditActionType;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.DurationType;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.NotificationType;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.pojo.BidHistoryChartPojo;
import com.privasia.procurehere.core.pojo.RfaSupplierBqPojo;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.WebSocketPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.BuyerSettingsService;
import com.privasia.procurehere.service.DashboardNotificationService;
import com.privasia.procurehere.service.EventAuditService;
import com.privasia.procurehere.service.NotificationService;
import com.privasia.procurehere.service.RfaBqService;
import com.privasia.procurehere.service.RfaEventService;
import com.privasia.procurehere.service.RfaEventSupplierService;
import com.privasia.procurehere.service.RfaSupplierBqItemService;
import com.privasia.procurehere.service.RfaSupplierBqService;
import com.privasia.procurehere.service.SupplierSettingsService;
import com.privasia.procurehere.service.UserService;
import com.privasia.procurehere.service.supplier.SupplierService;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;
import net.sf.jasperreports.engine.util.JRSwapFile;

@Controller
@RequestMapping("/buyer")
public class BuyerEnglishAuctionController {

	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@Autowired
	RfaEventService rfaEventService;

	@Autowired
	RfaSupplierBqItemService rfaSupplierBqItemService;

	@Autowired
	RfaSupplierBqService rfaSupplierBqService;

	@Autowired
	RfaBqService rfaBqService;

	@Autowired
	RfaEventSupplierService rfaEventSupplierService;

	@Resource
	MessageSource messageSource;

	@Autowired
	RfaSupplierBqDao rfaSupplierBqDao;

	@Autowired
	RfaEventSupplierDao rfaEventSupplierDao;

	@Autowired
	EventAuditService eventAuditService;

	@Autowired
	SimpMessagingTemplate simpMessagingTemplate;

	@Value("${app.url}")
	String APP_URL;

	@Autowired
	UserService userService;

	@Autowired
	RfaEventDao rfaEventDao;

	@Autowired
	DashboardNotificationService dashboardNotificationService;

	@Autowired
	NotificationService notificationService;

	@Autowired
	BuyerSettingsService buyerSettingsService;

	@Autowired
	SupplierSettingsService supplierSettingsService;
	
	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;
	
	@Autowired
	SupplierService supplierService;

	@ModelAttribute("eventType")
	public RfxTypes getEventType() {
		return RfxTypes.RFA;
	}

	@RequestMapping(path = "englishAuctionConsole/{eventId}", method = RequestMethod.GET)
	public String
    englishAuctionConsole(@PathVariable("eventId") String eventId, Model model) {
		try {

			// List<String> rfaEventSupplierIds =
			// rfaEventSupplierService.getAllRfaEventSuppliersIdByEventId(eventId);
			List<RfaSupplierBqPojo> supplirBqPojoList = rfaSupplierBqService.getSupplierListForAuctionConsole(eventId, null);
			// List<RfaSupplierBq> supplierBqs =
			// rfaSupplierBqService.findRfaSupplierBqBySupplierIds(rfaEventSupplierIds,
			// eventId);
			String bqId = null;
			// for (RfaSupplierBq rfaSupplierBq : supplierBqs) {
			// bqId = rfaSupplierBq.getBq().getId();
			// }
			RfaEvent event = rfaEventService.getLeanEventbyEventId(eventId);
			LOG.info("event : " + event.getId());
			// RfaSupplierBq supplierBq =
			// rfaSupplierBqService.findSupplierBqByBqId(bqId);

			// model.addAttribute("supplierBq", supplierBq);
			List<RfaEventBq> eventBqs = rfaBqService.getAllBqListByEventId(eventId);
			if (CollectionUtils.isNotEmpty(eventBqs)) {
				bqId = eventBqs.get(0).getId();
			}
			AuctionRules auctionRules = rfaEventService.getAuctionRulesForAuctionConsole(eventId);
			// List<Supplier> eventSupplierList = rfaEventSupplierService.getRfaEventSupplierForAuctionConsole(eventId);
			model.addAttribute("event", event);
			model.addAttribute("bqId", bqId);

			List<RfaSupplierBqPojo> getFilterList = getFilterList(eventId, auctionRules, supplirBqPojoList);
			List<Supplier> suppliers = new ArrayList<>();
			for (RfaSupplierBqPojo rfaSupplierBqPojo : getFilterList) {
				Supplier s = new Supplier();
				s.setId(rfaSupplierBqPojo.getSupplierId());
				s.setCompanyName(rfaSupplierBqPojo.getSupplierCompanyName());
				suppliers.add(s);
			}

			model.addAttribute("eventSupplierList", suppliers);
			model.addAttribute("eventPermissions", rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
			// model.addAttribute("supplierBqsList", supplierBqs);
			// AuctionRules auctionRules = rfaEventService.getAuctionRulesByEventId(eventId);
			model.addAttribute("auctionRules", auctionRules);
		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
		}
		return "buyerEnglishAuctionConsole";
	}

	@RequestMapping(path = "getBqofSelectSupplier", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<RfaSupplierBqItem>> getBqofSelectSupplier(@RequestParam("eventId") String eventId, @RequestParam("supplierId") String supplierId, @RequestParam("bqId") String bqId) {
		LOG.info("Requirment Event Id: " + eventId + " the Evnt button :  " + supplierId);
		HttpHeaders headers = new HttpHeaders();
		List<RfaSupplierBqItem> supplierBqItems = rfaSupplierBqItemService.getAllSupplierBqItemByBqIdAndSupplierId(bqId, supplierId);
		return new ResponseEntity<List<RfaSupplierBqItem>>(supplierBqItems, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "getBidHistoryOfSuppliers", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<BidHistoryChartPojo> getBidHistoryOfSuppliers(@RequestParam("eventId") String eventId, @RequestParam("arrangeBidBy") String arrangeBidBy, RedirectAttributes redir, HttpSession httpSession) {
		HttpHeaders headers = new HttpHeaders();
		try {

			// Get the user timezone from session as we will be needing this to format the bid time into his timezone
			// before sending the data.
			// The bid time is sent back as String hence we need to format it at the server side.
			String timeZone = "GMT+8:00";
			if (httpSession != null) {
				timeZone = (String) httpSession.getAttribute(Global.SESSION_TIME_ZONE_KEY);
				if (timeZone == null) {
					timeZone = "GMT+8:00";
				}
			}
			httpSession.setAttribute("graphArrangeBy", arrangeBidBy);
			BidHistoryChartPojo bidHistory = rfaEventService.getBidHistoryChartData(eventId, timeZone, arrangeBidBy);

			return new ResponseEntity<BidHistoryChartPojo>(bidHistory, headers, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("error while getting the bid history of supplier : " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.time.extension", new Object[] { e.getMessage() }, Global.LOCALE));
			return new ResponseEntity<BidHistoryChartPojo>(null, headers, HttpStatus.OK);
		}
	}

	@RequestMapping(path = "/refreshAuctionConsole/{eventId}", method = RequestMethod.GET)
	public ResponseEntity<TableData<RfaSupplierBqPojo>> refreshAuctionConsole(@PathVariable String eventId, @RequestParam(required = false) String limit) {
		LOG.info("Event : " + eventId + " Limit : " + limit);
		Integer listLimit = null;
		if (StringUtils.checkString(limit).length() > 0) {
			listLimit = Integer.parseInt(limit);
		}
		List<RfaSupplierBqPojo> supplirBqPojoList = rfaSupplierBqService.getSupplierListForAuctionConsole(eventId, listLimit);
		LOG.info("supplirBqPojoList : " + (supplirBqPojoList != null ? supplirBqPojoList.size() : 0));
		AuctionRules auctionRules = rfaEventService.getAuctionRulesForAuctionConsole(eventId);
		List<RfaSupplierBqPojo> finalList = getFilterList(eventId, auctionRules, supplirBqPojoList);
		TableData<RfaSupplierBqPojo> data = new TableData<RfaSupplierBqPojo>(finalList, finalList.size());
		if (CollectionUtil.isNotEmpty(finalList)) {
			data.setStatus(finalList.get(0).getCurrentAuctionStatus());
			data.setStartDate(finalList.get(0).getStartDate());
			data.setEndDate(finalList.get(0).getEndDate());
			data.setResumeDate(finalList.get(0).getResumeDate());
		}
		return new ResponseEntity<TableData<RfaSupplierBqPojo>>(data, HttpStatus.OK);

	}

	@SuppressWarnings("deprecation")
	@RequestMapping(path = "/timeExtension/{eventId}", method = RequestMethod.POST)
	public String timeExtension(@RequestParam("timeExtension") Integer timeExtension, @RequestParam("timeExtensionType") DurationType timeExtensionType, @PathVariable String eventId, RedirectAttributes redir, HttpSession session) {
		RfaEvent event = rfaEventService.getRfaEventByeventId(eventId);
		try {
			// event.setTimeExtensionDuration(timeExtension);
			// event.setTimeExtensionDurationType(timeExtensionType);
			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}
			LOG.info("Time extension Called : " + event.getEventName() + " : Event ENd  :  " + event.getEventEnd());
			Date eventEndDate = event.getEventEnd();
			Calendar cal = Calendar.getInstance(timeZone);
			cal.setTime(event.getEventEnd());
			if (timeExtensionType == DurationType.HOUR) {
				cal.add(Calendar.HOUR, (timeExtension));
			} else {
				cal.add(Calendar.MINUTE, (timeExtension));
			}
			event.setEventEnd(cal.getTime());
			LOG.info("New event end : " + event.getEventEnd());
			event.setTotalExtensions((event.getTotalExtensions() + 1));

			// Bidder Disqualify
			LOG.info("aucto disqualify check");
			if (event.getAutoDisqualify()) {
				LOG.info("aucto disqualify on : " + event.getBidderDisqualify());
				if (event.getBidderDisqualify() != null && event.getBidderDisqualify() > 0) {
					LOG.info("bider disqualify  greater then 0 ");
					List<RfaSupplierBqPojo> supplirBqPojoList = rfaSupplierBqDao.getSupplierListForAuctionConsole(event.getId(), null);
					LOG.info("supplier size for auction : " + supplirBqPojoList.size());
					int count = event.getBidderDisqualify();
					LOG.info("count for bidder disqualify : " + count);
					for (int i = supplirBqPojoList.size() - 1; i >= 0; i--) {
						if (count > 0) {
							LOG.info("count to supp pojo inside if : " + supplirBqPojoList.get(i).getSupplierId() + " :  " + i);
							RfaSupplierBqPojo suppPojo = supplirBqPojoList.get(i);
							if (suppPojo.getRankOfSupplier() == 999) {
								continue;
							} else {
								LOG.info("for update the supplier  I : " + i);
								rfaEventSupplierDao.updateEventSupplierDisqualify(event.getId(), suppPojo.getSupplierId(), event.getEventOwner(), "Auto disqualify due to auction rules");
								count--;
								LOG.info("count after update : " + count);
							}
						}
					}
				}

			}
			List<RfaEvent> relativeEventList = rfaEventService.getAllAssosiateAuctionForReschdule(event.getId());
			LOG.info("fro manual ext uperss +  : " + relativeEventList.size());
			if (CollectionUtil.isNotEmpty(relativeEventList)) {
				LOG.info("fro manual ext +  : " + relativeEventList.size());
				rfaEventService.manageRelativeEventOnTimeExt(event, timeZone, timeExtension, timeExtensionType);
			}
			rfaEventService.updateTimeExtension(eventId, (event.getTotalExtensions() + 1), event.getEventEnd());
			// sending auto time extension mail and notifications
			try {
				LOG.info("Extended time is :" + timeExtension + " " + timeExtensionType);

				String buyerTimeZone = "GMT+8:00";
				String buyerUrl = APP_URL + "/buyer/RFA/eventSummary/" + event.getId();
				String msg = messageSource.getMessage("time.extension.notification.message", new Object[] { timeExtension, timeExtensionType }, Global.LOCALE);
				// rfaEvent = rfaEvent != null ? rfaEvent :
				// rfaEventDao.getEventNameAndReferenceNumberById(event.getId());
				buyerTimeZone = getTimeZoneByBuyerSettings(event.getEventOwner(), buyerTimeZone);

				// Sending Mails and Notifications
				// for Event owner
				sendTimeExtensionNotification(event.getEventOwner(), event, buyerUrl, buyerTimeZone, RfxTypes.RFA.getValue(), msg);

				// sending supplier notifications
				List<String> suppIdList = rfaEventDao.getEventSuppliersId(event.getId());
				String suppUrl = APP_URL + "/supplier/viewSupplierEvent/RFA/" + event.getId();
				for (String suppId : suppIdList) {
					String suppTimeZone = getTimeZoneBySupplierSettings(suppId, buyerTimeZone);
					// Sending Mails and Notifications for admin suppliers
					List<User> supUsers = userService.getAllAdminPlainUsersForSupplier(suppId);
					for (User user : supUsers) {
						sendTimeExtensionNotification(user, event, suppUrl, suppTimeZone, RfxTypes.RFA.getValue(), msg);
					}
				}
			} catch (Exception e) {
				LOG.error("Error while sending auto time extension : " + e.getMessage(), e);
			}
			byte[] summarySnapshot = null;
			JRSwapFileVirtualizer virtualizer = null;
			try {
				virtualizer = new JRSwapFileVirtualizer(100, new JRSwapFile(System.getProperty("java.io.tmpdir"), 2048, 1024), false);
				JasperPrint eventSummary = rfaEventService.getEvaluationSummaryPdf(event, SecurityLibrary.getLoggedInUser(), (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY), virtualizer);
				summarySnapshot = JasperExportManager.exportReportToPdf(eventSummary);
			} catch (JRException e) {
				LOG.error("Error while Store summary PDF as byte : " + e.getMessage(), e);
			} finally {
				if (virtualizer != null) {
					virtualizer.cleanup();
				}
			}

			String buyerTimeZone = "GMT+8:00";
			buyerTimeZone = getTimeZoneByBuyerSettings(SecurityLibrary.getLoggedInUser(), buyerTimeZone);

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			sdf.setTimeZone(TimeZone.getTimeZone(buyerTimeZone));
			
			RfaEventAudit audit = new RfaEventAudit(SecurityLibrary.getLoggedInUser().getBuyer(), event, SecurityLibrary.getLoggedInUser(), new java.util.Date(), AuditActionType.Extension, messageSource.getMessage("event.audit.time.extended", new Object[] { sdf.format(eventEndDate), timeExtension, timeExtensionType }, Global.LOCALE), summarySnapshot);
			eventAuditService.save(audit);
			try {
				LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.EXTENSION, "Event end time extended from '" + sdf.format(eventEndDate) + " by '" + timeExtension  + " '" + timeExtensionType  + "' for event '"+event.getEventId()+ "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFA);
				buyerAuditTrailDao.save(buyerAuditTrail);
				LOG.info("--------------------------AFTER AUDIT---------------------------------------");
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
			}
			LOG.info("Save audit : ");
			redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.event.time.extension", new Object[] {}, Global.LOCALE));
			String data = timeExtension.toString() + " " + timeExtensionType.toString();
			WebSocketPojo webSocketPojo = new WebSocketPojo(data, "", "");
			LOG.info("web socket pojo " + webSocketPojo.getInfoMessage());
			LOG.info("the data " + data);
			simpMessagingTemplate.convertAndSend("/auctionTimeExtension/" + event.getId(), webSocketPojo);
		} catch (Exception e) {
			LOG.info("Error while adding time extension :" + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.time.extension", new Object[] { e.getMessage() }, Global.LOCALE));
		}
		return "redirect:/buyer/englishAuctionConsole/" + event.getId();
	}

	@RequestMapping(path = "getAuctionBidsOfSuppliers/{eventId}/{supplierId}", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<AuctionBids>> getAuctionBidsOfSuppliers(@PathVariable("eventId") String eventId, @PathVariable("supplierId") String supplierId) {
		HttpHeaders headers = new HttpHeaders();
		List<AuctionBids> auctionBids = rfaEventService.getAuctionBidsForSupplier(supplierId, eventId);
		AuctionRules auctionRules = rfaEventService.getAuctionRulesForAuctionConsole(eventId);
		LOG.info("=========================");
		for (AuctionBids auctionBid : auctionBids) {
			LOG.info("data:" + auctionBid.getRankForBid());
			if (auctionRules.getBuyerAuctionConsolePriceType() == AuctionConsolePriceVenderType.SHOW_NONE) {
				auctionBid.setAmount(null);

			} else if (auctionRules.getBuyerAuctionConsoleRankType() == AuctionConsolePriceVenderType.SHOW_NONE) {

				auctionBid.setRankForBid(null);
			} else if (auctionRules.getBuyerAuctionConsoleVenderType() == AuctionConsolePriceVenderType.SHOW_LEADING) {
				LOG.info("=========================");
				auctionBid.setBidBySupplier(null);
			}

		}

		return new ResponseEntity<List<AuctionBids>>(auctionBids, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "revertOnAuctionBid/{eventId}/{supplierId}", method = RequestMethod.POST)
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

	@RequestMapping(path = "/suspendEnglishAuction", method = RequestMethod.POST)
	public ModelAndView suspendEvent(@ModelAttribute RfaEvent event, RedirectAttributes redirectAttributes, Model model) {
		try {
			if (event != null) {
				RfaEvent persistObj = rfaEventService.getRfaEventByeventId(event.getId());
				persistObj.setStatus(EventStatus.SUSPENDED);
				persistObj.setSuspendRemarks(event.getSuspendRemarks());
				rfaEventService.updateRfaEvent(persistObj);
				simpMessagingTemplate.convertAndSend("/auctionTopic/" + event.getId(), "SUSPENDED");
			}
		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
		}
		return new ModelAndView("redirect:/buyer/englishAuctionConsole/" + event.getId());
	}

	private void sendTimeExtensionNotification(User user, Event event, String url, String timeZone, String eventType, String msg) {
		String mailTo = "";
		String subject = "Event Time Extended";
		HashMap<String, Object> map = new HashMap<String, Object>();
		try {
			mailTo = user.getCommunicationEmail();
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a (z)");
			df.setTimeZone(TimeZone.getTimeZone(timeZone));
			map.put("msg", msg);
			map.put("date", df.format(new Date()));
			map.put("userName", user.getName());
			map.put("eventType", eventType);
			map.put("referenceNumber", event.getReferanceNumber());
			map.put("eventName", event.getEventName());
			map.put("loginUrl", APP_URL + "/login");
			map.put("appUrl", url);
			map.put("businessUnit", findBusinessUnit(event.getId()));
			if(user.getEmailNotifications()) {
				notificationService.sendEmail(mailTo, subject, map, Global.TIME_EXTENSION_TEMPLATE);
			}
		} catch (Exception e) {
			LOG.error("Error While sending mail Notification on time extension :" + e.getMessage(), e);
		}
		try {
			String notificationMessage = msg;
			sendDashboardNotification(user, url, subject, notificationMessage);
		} catch (Exception e) {
			LOG.error("Error While sending dashboard Notification on time extension :" + e.getMessage(), e);
		}

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

	/**
	 * @param user
	 * @param timeZone
	 * @return timeZone
	 */
	private String getTimeZoneByBuyerSettings(User user, String timeZone) {
		try {
			if (StringUtils.checkString(user.getTenantId()).length() > 0) {
				String time = buyerSettingsService.getBuyerTimeZoneByTenantId(user.getTenantId());
				if (time != null) {
					timeZone = time;
				}
			}
		} catch (Exception e) {
			LOG.error("Error while fetching buyer setting time zone :" + e.getMessage(), e);
		}
		return timeZone;
	}

	/**
	 * @param suppId
	 * @param timeZone
	 * @return
	 */
	private String getTimeZoneBySupplierSettings(String suppId, String timeZone) {
		try {
			if (StringUtils.checkString(suppId).length() > 0) {
				String time = supplierSettingsService.getSupplierTimeZoneByTenantId(suppId);
				if (time != null) {
					timeZone = time;
					LOG.info("time Zone :" + timeZone);
				}
			}
		} catch (Exception e) {
			LOG.error("Error while fetching supplier setting time zone :" + e.getMessage(), e);
		}
		return timeZone;
	}

	private String findBusinessUnit(String eventId) {
		String displayName = rfaEventDao.findBusinessUnitName(eventId);
		return StringUtils.checkString(displayName);
	}

	@RequestMapping(path = "/disqualifySupplier", method = RequestMethod.POST)
	public String disqualifySupplier(@RequestParam("supplierId") String supplierId, @RequestParam("remark") String remark, @RequestParam("eventId") String eventId, RedirectAttributes redirectAttributes, Model model) {
		LOG.info("disqualifySupplier called supplier Id :" + supplierId + " == eventId :" + eventId);
		try {
			if (rfaEventService.disqualifySupplier(supplierId, remark, eventId, SecurityLibrary.getLoggedInUser())) {
				try {
					Supplier supplier = supplierService.findPlainSupplierUsingConstructorById(supplierId);
					RfaEvent event = rfaEventService.getPlainEventById(eventId);
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DISQUALIFIED, "Supplier '" + supplier.getCompanyName() + "' has been disqualified due to '" + remark + " ' for Event '" +event.getEventId()+ "' ", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.RFA);
					buyerAuditTrailDao.save(buyerAuditTrail);

					RfaEventAudit audit = new RfaEventAudit(event, SecurityLibrary.getLoggedInUser(), new Date(), AuditActionType.Disqualified, messageSource.getMessage("event.audit.disqualify.supplier", new Object[] { supplier.getCompanyName(), remark }, Global.LOCALE));
					eventAuditService.save(audit);

				} catch (Exception e) {
					LOG.error("Error while audit disqualify supplier: " + e.getMessage(), e);
				}
				
				LOG.info("Supplier successfully disqualified");
				redirectAttributes.addFlashAttribute("success", messageSource.getMessage("flashsuccess.supplier.disqualified", new Object[] {}, Global.LOCALE));
			} else {
				LOG.info("Failed to disqualify the supplier");
				redirectAttributes.addFlashAttribute("error", messageSource.getMessage("flasherror.failed.disqualify.supplier", new Object[] {}, Global.LOCALE));
			}
		} catch (Exception e) {
			LOG.error("Error : " + e.getMessage(), e);
			redirectAttributes.addFlashAttribute("error", messageSource.getMessage("flasherror.while.disqualify.supplier", new Object[] {}, Global.LOCALE));
		}
		return "redirect:/buyer/englishAuctionConsole/" + eventId;
	}

	private List<RfaSupplierBqPojo> getFilterList(String eventId, AuctionRules auctionRules, List<RfaSupplierBqPojo> supplirBqPojoList) {

		/*
		 * TableData<RfaSupplierBqPojo> data = new TableData<RfaSupplierBqPojo>(supplirBqPojoList,
		 * supplirBqPojoList.size()); if (CollectionUtil.isNotEmpty(supplirBqPojoList)) {
		 * data.setStatus(supplirBqPojoList.get(0).getCurrentAuctionStatus());
		 * data.setStartDate(supplirBqPojoList.get(0).getStartDate());
		 * data.setEndDate(supplirBqPojoList.get(0).getEndDate());
		 * data.setResumeDate(supplirBqPojoList.get(0).getResumeDate()); }
		 */
		// return new ResponseEntity<TableData<RfaSupplierBqPojo>>(data, HttpStatus.OK);

		List<RfaSupplierBqPojo> finalList = new ArrayList<RfaSupplierBqPojo>();
		// rfaEventSupplierService.updateAuctionOnlineDateTime(eventId, SecurityLibrary.getLoggedInUserTenantId());

		// List<RfaSupplierBqPojo> supplirBqPojoList = rfaSupplierBqService.getSupplierListForAuctionConsole(eventId,
		// null);

		LOG.info("AuctionRules : " + auctionRules.toLogString());
		
		for (RfaSupplierBqPojo rfaSupplierBqPojo : supplirBqPojoList) {
			if (auctionRules.getAuctionType() == AuctionType.FORWARD_ENGISH || auctionRules.getAuctionType() == AuctionType.REVERSE_ENGISH) {
				// Price : Show None
				// LOG.info("---------- Rank-----------" + auctionRules.getBuyerAuctionConsoleRankType());
				// LOG.info("---------- price -----------" + auctionRules.getBuyerAuctionConsolePriceType());
				// LOG.info("---------- supplier -----------" + auctionRules.getBuyerAuctionConsoleVenderType());

				if (auctionRules.getBuyerAuctionConsolePriceType() == AuctionConsolePriceVenderType.SHOW_NONE) {
					// LOG.info("----------SHOW_NONE 1-----------");
					rfaSupplierBqPojo.setCurrentPrice(null);
					rfaSupplierBqPojo.setInitialPrice(null);
					rfaSupplierBqPojo.setDifferencePercentage(null);
				} else if (auctionRules.getBuyerAuctionConsolePriceType() == AuctionConsolePriceVenderType.SHOW_LEADING && (rfaSupplierBqPojo.getRankOfSupplier() != null && 1 != rfaSupplierBqPojo.getRankOfSupplier())) {
					// Price : show leading
					// LOG.info("----------SHOW_LEADING-----------");
					rfaSupplierBqPojo.setCurrentPrice(null);
					rfaSupplierBqPojo.setInitialPrice(null);
					rfaSupplierBqPojo.setDifferencePercentage(null);
				}

				// Supplier name
				if (auctionRules.getBuyerAuctionConsoleVenderType() == AuctionConsolePriceVenderType.SHOW_NONE) {
					if (!rfaSupplierBqPojo.getSupplierId().equals(SecurityLibrary.getLoggedInUserTenantId())) {
						rfaSupplierBqPojo.setSupplierCompanyName("");
						// LOG.info("----------SHOW_NONE 2-----------");
					}
					finalList.add(rfaSupplierBqPojo);
				} else if (auctionRules.getBuyerAuctionConsoleVenderType() == AuctionConsolePriceVenderType.SHOW_LEADING) {
					if (rfaSupplierBqPojo.getRankOfSupplier() != null && 1 == rfaSupplierBqPojo.getRankOfSupplier()) {

						// LOG.info("----------SHOW_LEADING 2-----------");
						finalList.add(rfaSupplierBqPojo);
					} else {
						// Remove company name
						rfaSupplierBqPojo.setSupplierCompanyName("");
						finalList.add(rfaSupplierBqPojo);
					}
				} else {
					finalList.add(rfaSupplierBqPojo);
				}

				// rank
				if (auctionRules.getBuyerAuctionConsoleRankType() == AuctionConsolePriceVenderType.SHOW_NONE) {
					rfaSupplierBqPojo.setRankOfSupplier(null);
					// LOG.info("----------SHOW_NONE 3-----------");
				} else if (auctionRules.getBuyerAuctionConsoleRankType() == AuctionConsolePriceVenderType.SHOW_LEADING) {
					if (rfaSupplierBqPojo.getRankOfSupplier() != null && 1 != rfaSupplierBqPojo.getRankOfSupplier()) {
						rfaSupplierBqPojo.setRankOfSupplier(null);
					}
				}

			} else if (auctionRules.getAuctionType() == AuctionType.FORWARD_SEALED_BID || auctionRules.getAuctionType() == AuctionType.REVERSE_SEALED_BID) {
				if (rfaSupplierBqPojo.getSupplierId().equals(SecurityLibrary.getLoggedInUserTenantId())) {
					rfaSupplierBqPojo.setRankOfSupplier(null);
					finalList.add(rfaSupplierBqPojo);
				}
			}
		}
		// Remove no of bids and intial price and differce percentage for the
		// other suppliers.
		if (CollectionUtil.isNotEmpty(finalList)) {
			List<RfaSupplierBqPojo> superFinal = new ArrayList<RfaSupplierBqPojo>();
			for (RfaSupplierBqPojo sup : finalList) {
				if (StringUtils.checkString(sup.getSupplierCompanyName()).length() > 0 || sup.getRankOfSupplier() != null || sup.getInitialPrice() != null) {
					superFinal.add(sup);
					LOG.info("----------adding-----------");

				}
			}
			finalList = superFinal;
		}
		LOG.info("finalList : " + (finalList != null ?  finalList.size() : 0));
		return finalList;

	}

}

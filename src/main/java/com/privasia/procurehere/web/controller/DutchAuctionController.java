package com.privasia.procurehere.web.controller;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.privasia.procurehere.core.entity.AuctionRules;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.DurationMinSecType;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.TenantType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.AuctionConsolePojo;
import com.privasia.procurehere.core.pojo.DutchAuctionPojo;
import com.privasia.procurehere.core.pojo.RfaSupplierBqPojo;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.RfaEventService;
import com.privasia.procurehere.service.RfaEventSupplierService;
import com.privasia.procurehere.service.RfaSupplierBqService;
import com.privasia.procurehere.service.supplier.SupplierService;

@Controller
@RequestMapping("/auction")
public class DutchAuctionController {

	private static final Logger LOG = LogManager.getLogger(Global.AUCTION_LOG);

	@Autowired
	SchedulerFactoryBean schedulerFactoryBean;

	@Autowired
	RfaEventService rfaEventService;

	@Autowired
	SupplierService supplierService;

	@Autowired
	RfaEventSupplierService rfaEventSupplierService;

	@Autowired
	SimpMessagingTemplate simpMessagingTemplate;

	@Autowired
	RfaSupplierBqService rfaSupplierBqService;
	
	@Autowired
	MessageSource messageSource;

	@ModelAttribute("eventType")
	public RfxTypes getEventType() {
		return RfxTypes.RFA;
	}

	@RequestMapping(path = "dutchAuctionConsole/remainingTime/{eventId}", method = RequestMethod.GET)
	public ResponseEntity<String> getRemainingTime(@PathVariable String eventId, Model model, HttpSession session) {
		long remainingTime = 0;
		RfaEvent event = rfaEventService.getRfaEventByeventId(eventId);
		Date now = new Date();

		Date start = event.getEventStart();
		if (event.getAuctionResumeDateTime() != null) {
			start = event.getAuctionResumeDateTime();
		}

		// If not yet started, get the remaining time to start
		if (now.before(start)) {
			remainingTime = start.getTime() - now.getTime();
		} else if (now.before(event.getEventEnd())) {
			// If started, get the remaining time to End time
			remainingTime = event.getEventEnd().getTime() - now.getTime();
		}
		return new ResponseEntity<String>("{\"remainingTime\" : " + String.valueOf(remainingTime) + "}", HttpStatus.OK);
	}

	@RequestMapping(path = "dutchAuctionConsole/{eventId}", method = RequestMethod.GET)
	public String createDutchAuction(@PathVariable String eventId, Model model, HttpSession session) {
		TimeZone timeZone = TimeZone.getDefault();
		String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
		if (strTimeZone != null) {
			timeZone = TimeZone.getTimeZone(strTimeZone);
		}
		RfaEvent event = rfaEventService.getRfaEventByeventId(eventId);
		AuctionRules auctionRules = rfaEventService.getAuctionRulesByEventId(eventId);
		model.addAttribute("auctionRules", auctionRules);
		model.addAttribute("event", event);
		DateFormat timeFormatter = new SimpleDateFormat("hh:mm:ss a");
		timeFormatter.setTimeZone(timeZone);
		BigDecimal dutctStartPrice = auctionRules.getDutchStartPrice();
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2));
		df.setMinimumFractionDigits((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2));
		df.setGroupingUsed(false);
		Calendar cal = Calendar.getInstance();
		if (event.getAuctionResumeDateTime() != null) {
			cal.setTime(event.getAuctionResumeDateTime());
		} else {
			cal.setTime(event.getEventStart());
		}
		List<DutchAuctionPojo> dutchAuctionPojoList = new ArrayList<DutchAuctionPojo>();
		for (int i = 1; i <= auctionRules.getDutchAuctionTotalStep(); i++) {
			DutchAuctionPojo dutchAuctionPojo = new DutchAuctionPojo();
			String result = df.format(dutctStartPrice);
			dutchAuctionPojo.setCurrentStepAmount(result);
			dutchAuctionPojo.setCurrentSlotTime(timeFormatter.format(cal.getTime()));
			dutchAuctionPojo.setCurrentStep(i);
			dutchAuctionPojoList.add(dutchAuctionPojo);
			if (auctionRules.getIntervalType() == DurationMinSecType.MINUTE) {
				cal.add(Calendar.MINUTE, auctionRules.getInterval());
			} else {
				cal.add(Calendar.SECOND, auctionRules.getInterval());
			}

			if (auctionRules.getDutchAuctionTotalStep() - 1 != i) {
				// Forward Auction
				if (Boolean.TRUE == auctionRules.getFowardAuction()) {
					dutctStartPrice = dutctStartPrice.subtract(auctionRules.getAmountPerIncrementDecrement());
				} else {
					// Reverse Auction
					dutctStartPrice = dutctStartPrice.add(auctionRules.getAmountPerIncrementDecrement());
				}
			} else {
				dutctStartPrice = auctionRules.getDutchMinimumPrice();
			}
		}
		if (event.getWinningSupplier() != null) {
			Supplier winingSupplier = supplierService.findSuppById(event.getWinningSupplier().getId());
			model.addAttribute("winingSupplier", winingSupplier);
		}
		auctionRules.setDutchAuctionCurrentStep(auctionRules.getDutchAuctionCurrentStep());
		model.addAttribute("eventStart", event.getEventStart());
		model.addAttribute("eventEnd", event.getEventEnd());

		model.addAttribute("eventStartTimeRemain", event.getEventStart());
		model.addAttribute("dutchAuctionPojoList", dutchAuctionPojoList);
		model.addAttribute("currency", event.getBaseCurrency());
		model.addAttribute("event", event);
		model.addAttribute("loggedInUserTenantId", SecurityLibrary.getLoggedInUserTenantId());
		try {
			if (SecurityLibrary.getLoggedInUser().getTenantType() == TenantType.SUPPLIER) {
				model.addAttribute("eventPermissions", rfaEventSupplierService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), SecurityLibrary.getLoggedInUserTenantId(), eventId));
			} else {
				model.addAttribute("eventPermissions", rfaEventService.getUserPemissionsForEvent(SecurityLibrary.getLoggedInUser().getId(), eventId));
			}
		} catch (Exception e) {
			LOG.error("Error fetching event permissions for : " + SecurityLibrary.getLoggedInUserLoginId() + " - Error : " + e.getMessage(), e);
		}
		return "dutchAuctionConsole";
	}

	@RequestMapping(path = "/refreshDutchAuctionConsole/{auctionId}", method = RequestMethod.POST)
	public ResponseEntity<AuctionConsolePojo> refreshConsolePage(@PathVariable(name = "auctionId") String auctionId) {
		HttpHeaders headers = new HttpHeaders();
		AuctionConsolePojo auctionConsolePojo = new AuctionConsolePojo();
		try {
			AuctionRules auctionRules = rfaEventService.getAuctionRulesWithEventById(auctionId);
			Integer currentStep = auctionRules.getDutchAuctionCurrentStep();
			String status = auctionRules.getEvent().getStatus().toString();
			LOG.debug("Status : " + status);
			auctionConsolePojo.setCurrentStatus(status);
			auctionConsolePojo.setCurrentStepNo(currentStep);
			LOG.debug("Current Step : " + currentStep);
			if (SecurityLibrary.ifAnyGranted("ROLE_SUPPLIER")) {
				LOG.info("admin supplier");
				rfaEventSupplierService.updateAuctionOnlineDateTime(auctionRules.getEvent().getId(), SecurityLibrary.getLoggedInUserTenantId());
			}
		} catch (Exception e) {
			LOG.error("Error during refresh of dutch auction console : " + e.getMessage(), e);
			headers.add("error", "Error during refresh of dutch auction console : " + e.getMessage());
			return new ResponseEntity<AuctionConsolePojo>(auctionConsolePojo, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<AuctionConsolePojo>(auctionConsolePojo, HttpStatus.OK);
	}

	@RequestMapping(path = "/submitDutchAuction/{auctionId}", method = RequestMethod.POST)
	public String submitDutchAuction(@PathVariable(name = "auctionId") String auctionId, @RequestParam("currentStepNo") Integer currentStepNo, RedirectAttributes redir, HttpServletRequest request) {
		// HttpHeaders headers = new HttpHeaders();
		AuctionRules auctionRules = rfaEventService.getAuctionRulesWithEventById(auctionId);

		RfaEvent event = rfaEventService.getPlainEventById(auctionRules.getEvent().getId());
		if (event.getStatus() != EventStatus.ACTIVE) {
			redir.addFlashAttribute("error", messageSource.getMessage("flasherror.auction.not.active", new Object[] {}, Global.LOCALE));
			return "redirect:/auction/dutchAuctionConsole/" + event.getId();
		}
		JobKey jobKey = new JobKey("JOB" + auctionRules.getId(), "DUTCHAUCTION");
		try {
			String ipAddress = request.getHeader("X-FORWARDED-FOR");
			if (ipAddress == null) {
				ipAddress = request.getRemoteAddr();
			}
			LOG.info("at Starting stage date time  : " + new Date());
			if (schedulerFactoryBean.getScheduler().checkExists(jobKey)) {
				LOG.info("for pause the schdular : - ");
				schedulerFactoryBean.getScheduler().pauseJob(jobKey);
			}
			LOG.info("Curent Step no : - " + currentStepNo + " : " + auctionRules.getDutchAuctionCurrentStep());
			if (currentStepNo.intValue() == auctionRules.getDutchAuctionCurrentStep().intValue()) {
				LOG.info("true the current step");
				String loggedInTenantId = SecurityLibrary.getLoggedInUserTenantId();
				User loggedInUser = SecurityLibrary.getLoggedInUser();
				synchronized (this) {
					LOG.info("For submit bid : - ");
					rfaEventService.submitDutchAuction(currentStepNo, auctionId, event, loggedInUser, loggedInTenantId, ipAddress);
				}

				if (schedulerFactoryBean.getScheduler().checkExists(jobKey)) {
					schedulerFactoryBean.getScheduler().deleteJob(jobKey);
				}
				LOG.info("at final stage date time  : " + new Date());
				redir.addFlashAttribute("success", messageSource.getMessage("flashsuccess.bid.submitted", new Object[] {}, Global.LOCALE));
				simpMessagingTemplate.convertAndSend("/auctionTopic/" + event.getId(), "CLOSED");
			} else {
				redir.addFlashAttribute("error", messageSource.getMessage("flasherror.current.step.notmatched", new Object[] {}, Global.LOCALE));
				return "redirect:/auction/dutchAuctionConsole/" + event.getId();
			}
		} catch (ApplicationException ex) {
			LOG.error("Error during submit bid by supplier : " + ex.getMessage(), ex);
			redir.addFlashAttribute("error", messageSource.getMessage("flasherror.auction.already.included", new Object[] {}, Global.LOCALE));
			return "redirect:/auction/dutchAuctionConsole/" + event.getId();
		} catch (Exception e) {
			LOG.error("Error during submit bid by supplier : " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("flasherror.current.step.notmatched", new Object[] {}, Global.LOCALE));
			return "redirect:/auction/dutchAuctionConsole/" + event.getId();
		} finally {
			try {
				if (schedulerFactoryBean.getScheduler().checkExists(jobKey)) {
					schedulerFactoryBean.getScheduler().resumeJob(jobKey);
				}
			} catch (SchedulerException e1) {
			}
		}
		return "redirect:/auction/dutchAuctionConsole/" + event.getId();
	}

	@RequestMapping(path = "/suspendDutchAuction/{auctionId}", method = RequestMethod.POST)
	public ResponseEntity<String> suspendDutchAuction(@PathVariable(name = "auctionId") String auctionId, @RequestParam("currentStep") Integer currentStep) {
		HttpHeaders headers = new HttpHeaders();
		AuctionRules auctionRules = rfaEventService.getAuctionRulesWithEventById(auctionId);

		RfaEvent event = rfaEventService.getRfaEventByeventId(auctionRules.getEvent().getId());
		try {
			rfaEventService.suspendDutchAuction(event);
			headers.add("success", "Your auction successfully suspended");
			simpMessagingTemplate.convertAndSend("/auctionTopic/auctionMessage", "SUSPENDED");
		} catch (Exception e) {
			LOG.error("Error during suspend auction by buyer : " + e.getMessage(), e);
			return new ResponseEntity<String>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<String>(event.getId(), headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/refreshDutchAuctionConsoleForSupplierList/{eventId}", method = RequestMethod.GET)
	public ResponseEntity<TableData<RfaSupplierBqPojo>> refreshAuctionConsole(@PathVariable String eventId, @RequestParam(required = false) String limit) {
		Integer listLimit = null;
		LOG.info("Refresh dutch auction console for the buyer to get supplier list and details");
		LOG.info("event ID " + eventId);
		if (StringUtils.checkString(limit).length() > 0) {
			listLimit = Integer.parseInt(limit);
		}
		List<RfaSupplierBqPojo> supplirBqPojoList = rfaSupplierBqService.getSupplierListForDutchAuctionConsole(eventId, listLimit);

		TableData<RfaSupplierBqPojo> data = new TableData<RfaSupplierBqPojo>(supplirBqPojoList, supplirBqPojoList.size());
		if (CollectionUtil.isNotEmpty(supplirBqPojoList)) {
			for (RfaSupplierBqPojo rfaSupplierBqPojo : supplirBqPojoList) {
				rfaSupplierBqPojo.setCurrentPrice(null);
				rfaSupplierBqPojo.setInitialPrice(null);
				rfaSupplierBqPojo.setNumberOfBids(null);
				rfaSupplierBqPojo.setRankOfSupplier(null);
			}
			data.setStatus(supplirBqPojoList.get(0).getCurrentAuctionStatus());
			data.setStartDate(supplirBqPojoList.get(0).getStartDate());
			data.setEndDate(supplirBqPojoList.get(0).getEndDate());
			data.setResumeDate(supplirBqPojoList.get(0).getResumeDate());
		}

		return new ResponseEntity<TableData<RfaSupplierBqPojo>>(data, HttpStatus.OK);
	}

}

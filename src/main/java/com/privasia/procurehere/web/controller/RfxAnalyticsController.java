/**
 * 
 */
package com.privasia.procurehere.web.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.SourcingFormStatus;
import com.privasia.procurehere.core.pojo.RfxAnalyticsPojo;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.RfxAnalyticsService;

/**
 * @author jayshree
 *
 */
@Controller
@RequestMapping("/rfxAnalytics")
public class RfxAnalyticsController {
	
	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@Autowired
	RfxAnalyticsService rfxAnalyticsService;

	@ModelAttribute("eventStatusList")
	public List<EventStatus> getNewStatusList() {
		return Arrays.asList(EventStatus.DRAFT, EventStatus.PENDING, EventStatus.APPROVED, EventStatus.CANCELED, EventStatus.SUSPENDED, EventStatus.ACTIVE, EventStatus.CLOSED, EventStatus.FINISHED, EventStatus.COMPLETE);
	}

	@ModelAttribute("requestStatusList")
	public List<SourcingFormStatus> getSourcingStatusList() {
		List<SourcingFormStatus> sourcingFormStatusList = Arrays.asList(SourcingFormStatus.DRAFT, SourcingFormStatus.FINISHED, SourcingFormStatus.CANCELED, SourcingFormStatus.PENDING, SourcingFormStatus.APPROVED, SourcingFormStatus.CONCLUDED);
		return sourcingFormStatusList;
	}

	@RequestMapping(value = "/rfxAnalytics", method = RequestMethod.GET)
	public String tatReportView() {
		return "rfxAnalytics";
	}
	
	@RequestMapping(path = "/getTopRfsVolumeByCategoryForCurrentYear", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<TableData<RfxAnalyticsPojo>> getTopRfsVolumeByProcurementCategoryForCurrentYear(@RequestParam("startDate") String startDate, @RequestParam(value="endDate", required=false) String endDate, @RequestParam("rfsStatus") String rfsStatus, HttpSession session) {
		LOG.info("Top Rfs Volume By ProcurementCategory for start date : " + startDate + " and end date : " + endDate);
		try {
			TableData<RfxAnalyticsPojo> data = null;
			List<RfxAnalyticsPojo> list = null;
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
			
			list = rfxAnalyticsService.getTopRfsVolumeByProcurementCategoryForCurrentYear(start, end, rfsStatus, SecurityLibrary.getLoggedInUserTenantId());
			long count = rfxAnalyticsService.getCountOfTopRfsByProcurementCategoryForCurrentYear(start, end, rfsStatus, SecurityLibrary.getLoggedInUserTenantId());
			LOG.info("Count TopRfsVolumeByCategory @@@@@@@@@@@@@ "+count);
			data = new TableData<RfxAnalyticsPojo>(list);
			data.setRecordsTotal(count);
			
			return new ResponseEntity<TableData<RfxAnalyticsPojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error in fetching Rfs Volume By ProcurementCategory data : " + e.getMessage());
			HttpHeaders header = new HttpHeaders();
			header.add("error", e.getMessage());
			return new ResponseEntity<TableData<RfxAnalyticsPojo>>(null, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(path = "/getTopRfsVolumeByBusinessUnitForCurrentYear", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<TableData<RfxAnalyticsPojo>> getTopRfsVolumeByBusinessUnitForCurrentYear(@RequestParam("startDate") String startDate, @RequestParam(value="endDate", required=false) String endDate, @RequestParam("rfsStatus") String rfsStatus, HttpSession session) {
		LOG.info("Top Rfs Volume By BusinessUnit for start date : " + startDate + " and end date : " + endDate);
		try {
			TableData<RfxAnalyticsPojo> data = null;
			List<RfxAnalyticsPojo> list = null;
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
			
			list = rfxAnalyticsService.getTopRfsVolumeByBusinessUnitForCurrentYear(start, end, rfsStatus, SecurityLibrary.getLoggedInUserTenantId());
			long count = rfxAnalyticsService.getCountOfTopRfsByBusinessUnitForCurrentYear(start, end, rfsStatus, SecurityLibrary.getLoggedInUserTenantId());
			LOG.info("Count TopRfsVolumeByBusinessUnit ################ "+count);
			data = new TableData<RfxAnalyticsPojo>(list);
			data.setRecordsTotal(count);
			
			return new ResponseEntity<TableData<RfxAnalyticsPojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error in fetching Rfs Volume By BusinessUnit data : " + e.getMessage());
			HttpHeaders header = new HttpHeaders();
			header.add("error", e.getMessage());
			return new ResponseEntity<TableData<RfxAnalyticsPojo>>(null, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(path = "/getTopRfxVolumeByCategoryForCurrentYear", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<TableData<RfxAnalyticsPojo>> getTopRfxVolumeByCategoryForCurrentYear(@RequestParam("startDate") String startDate, @RequestParam(value="endDate", required=false) String endDate, @RequestParam("eventType") String eventType, @RequestParam("eventStatus") String eventStatus, HttpSession session) {
		LOG.info("Top RFX Volume By ProcurementCategory for start date : " + startDate + " and end date : " + endDate);
		try {
			TableData<RfxAnalyticsPojo> data = null;
			List<RfxAnalyticsPojo> list = null;
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
			
			list = rfxAnalyticsService.getTopRfxVolumeByCategoryForCurrentYear(start, end, eventType, eventStatus, SecurityLibrary.getLoggedInUserTenantId());
			long count = rfxAnalyticsService.getCountOfRfxVolumeByCategoryForCurrentYear(start, end, eventType, eventStatus, SecurityLibrary.getLoggedInUserTenantId());
			data = new TableData<RfxAnalyticsPojo>(list);
			LOG.info("Count TopRfxVolumeByCategory >>>>>>>>>>>> "+count);
			data.setRecordsTotal(count);
			
			return new ResponseEntity<TableData<RfxAnalyticsPojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error in fetching RFX Volume By ProcurementCategory data : " + e.getMessage());
			HttpHeaders header = new HttpHeaders();
			header.add("error", e.getMessage());
			return new ResponseEntity<TableData<RfxAnalyticsPojo>>(null, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(path = "/topRfxVolumeByBusinessUnitForCurrentYear", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<TableData<RfxAnalyticsPojo>> getTopRfxVolumeByBusinessUnitForCurrentYear(@RequestParam("startDate") String startDate, @RequestParam(value="endDate", required=false) String endDate, @RequestParam("eventType") String eventType, @RequestParam("eventStatus") String eventStatus, HttpSession session) {
		LOG.info("Top Rfx Volume By BusinessUnit for start date : " + startDate + " and end date : " + endDate);
		try {
			TableData<RfxAnalyticsPojo> data = null;
			List<RfxAnalyticsPojo> list = null;
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
			
			list = rfxAnalyticsService.getTopRfxVolumeByBusinessUnitForCurrentYear(start, end, eventType, eventStatus, SecurityLibrary.getLoggedInUserTenantId());
			long count = rfxAnalyticsService.getCountOfRfxVolumeByBusinessUnitForCurrentYear(start, end, eventType, eventStatus, SecurityLibrary.getLoggedInUserTenantId());
			data = new TableData<RfxAnalyticsPojo>(list);
			data.setRecordsTotal(count);
			
			return new ResponseEntity<TableData<RfxAnalyticsPojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error in fetching RFX Volume By BusinessUnit data : " + e.getMessage());
			HttpHeaders header = new HttpHeaders();
			header.add("error", e.getMessage());
			return new ResponseEntity<TableData<RfxAnalyticsPojo>>(null, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(path = "/topRfxAwardValueByCategoryForCurrentYear", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<RfxAnalyticsPojo>> getTopRfxAwardValueByCategoryForCurrentYear(@RequestParam("startDate") String startDate, @RequestParam(value="endDate", required=false) String endDate, @RequestParam("eventType") String eventType, HttpSession session) {
		LOG.info("Top RFX Award Value By Category for start date : " + startDate + " and end date : " + endDate);
		try {
			List<RfxAnalyticsPojo> data = null;
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
			
			data = rfxAnalyticsService.getTopRfxAwardValueByCategoryForForCurrentYear(start, end, eventType, SecurityLibrary.getLoggedInUserTenantId());
			
			return new ResponseEntity<List<RfxAnalyticsPojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error in fetching RFX Award Value By Category  data : " + e.getMessage());
			HttpHeaders header = new HttpHeaders();
			header.add("error", e.getMessage());
			return new ResponseEntity<List<RfxAnalyticsPojo>>(null, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(path = "/topRfxAwardValueByBusinessUnitForCurrentYear", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<RfxAnalyticsPojo>> getTopRfxAwardValueByBusinessUnitForCurrentYear(@RequestParam("startDate") String startDate, @RequestParam(value="endDate", required=false) String endDate, @RequestParam("eventType") String eventType, HttpSession session) {
		LOG.info("Top RFX Award Value By Business Unit for start date : " + startDate + " and end date : " + endDate);
		try {
			List<RfxAnalyticsPojo> data = null;
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
			
			data = rfxAnalyticsService.getTopRfxAwardValueByBusinessUnitForCurrentYear(start, end, eventType, SecurityLibrary.getLoggedInUserTenantId());
			
			return new ResponseEntity<List<RfxAnalyticsPojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error in fetching RFX Award Value By Business Unit  data : " + e.getMessage());
			HttpHeaders header = new HttpHeaders();
			header.add("error", e.getMessage());
			return new ResponseEntity<List<RfxAnalyticsPojo>>(null, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
}

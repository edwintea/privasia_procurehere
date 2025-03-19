/**
 * 
 */
package com.privasia.procurehere.web.controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.privasia.procurehere.core.entity.BusinessUnit;
import com.privasia.procurehere.core.entity.ProcurementCategories;
import com.privasia.procurehere.core.pojo.SpAnalyticsPojo;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.ProcurementCategoriesService;
import com.privasia.procurehere.service.SupplierPerformanceAnalyticsService;
import com.privasia.procurehere.service.SupplierPerformanceEvaluationService;

/**
 * @author Jayshree
 */
@Controller
@RequestMapping("/spAnalytics")
public class SupplierPerformanceAnalyticsController {

	private static final Logger LOG = LogManager.getLogger(SupplierPerformanceAnalyticsController.class);

	@Autowired
	SupplierPerformanceAnalyticsService supplierPerformanceAnalyticsService;

	@Autowired
	SupplierPerformanceEvaluationService supplierPerformanceEvaluationService;

	@Autowired
	MessageSource messageSource;

	@RequestMapping(path = "/getBusinessUnitByDate", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<BusinessUnit>> getBusinessUnitList(@RequestParam("startDate") String startDate, @RequestParam(value = "endDate", required = false) String endDate, HttpSession session) {
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
			return new ResponseEntity<List<BusinessUnit>>(supplierPerformanceEvaluationService.getBusinessUnitListForTenant(SecurityLibrary.getLoggedInUserTenantId(), start, end, null), HttpStatus.OK);
		} catch (ParseException e) {
			HttpHeaders headers = new HttpHeaders();
			LOG.error("Error fetching B Unit list : " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("assign.bunit.error", new Object[] {}, Global.LOCALE));
			return new ResponseEntity<List<BusinessUnit>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(path = "/getprocureherementCategoryListByDate", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<ProcurementCategories>> getprocureherementCategoryListList(@RequestParam("startDate") String startDate, @RequestParam(value = "endDate", required = false) String endDate, HttpSession session) {
		LOG.info("Proc Cat list for start date : " + startDate + " and end date : " + endDate);
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
			return new ResponseEntity<List<ProcurementCategories>>(supplierPerformanceEvaluationService.getProcurementCategoriesListForTenantForDate(SecurityLibrary.getLoggedInUserTenantId(), start, end), HttpStatus.OK);
		} catch (ParseException e) {
			HttpHeaders headers = new HttpHeaders();
			LOG.error("Error fetching Proc Cat list : " + e.getMessage(), e);
			headers.add("error", messageSource.getMessage("assign.bunit.error", new Object[] {}, Global.LOCALE));
			return new ResponseEntity<List<ProcurementCategories>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/supplierPerformanceAnalytics", method = RequestMethod.GET)
	public String spAnalyticsView() {
		return "supplierPerformanceAnalytics";
	}

	@RequestMapping(path = "/getTopHighPerformanceSuppier", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<TableData<SpAnalyticsPojo>> getTopHighPerformanceSuppier(@RequestParam("startDate") String startDate, @RequestParam(value = "endDate", required = false) String endDate, HttpSession session) {
		LOG.info("Top High Performance Suppier for start date : " + startDate + " and end date : " + endDate);
		try {
			TableData<SpAnalyticsPojo> data = null;
			List<SpAnalyticsPojo> list = null;
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

			list = supplierPerformanceAnalyticsService.getTopHighPerformanceSuppier(start, end, SecurityLibrary.getLoggedInUserTenantId());
			// long count = supplierPerformanceAnalyticsService.getCountOfTopHighPerformanceSuppier(start, end,
			// SecurityLibrary.getLoggedInUserTenantId());
			// LOG.info("Count Top High Performance Suppier @@@@@@@@@@@@@ "+count);
			data = new TableData<SpAnalyticsPojo>(list);
			// data.setRecordsTotal(count);

			return new ResponseEntity<TableData<SpAnalyticsPojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error in fetching Top High Performance Suppier data : " + e.getMessage());
			HttpHeaders header = new HttpHeaders();
			header.add("error", e.getMessage());
			return new ResponseEntity<TableData<SpAnalyticsPojo>>(null, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/getTopHighPerformanceSuppierByBUnit", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<TableData<SpAnalyticsPojo>> getTopHighPerformanceSuppierByBUnit(@RequestParam("startDate") String startDate, @RequestParam(value = "endDate", required = false) String endDate, @RequestParam(value = "buId", required = false) String buId, HttpSession session) {
		LOG.info("Top High Performance Suppier By BU for start date : " + startDate + " and end date : " + endDate);
		try {
			TableData<SpAnalyticsPojo> data = null;
			List<SpAnalyticsPojo> list = null;
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

			list = supplierPerformanceAnalyticsService.getTopHighPerformanceSuppierByBUnit(start, end, buId, SecurityLibrary.getLoggedInUserTenantId());
			data = new TableData<SpAnalyticsPojo>(list);

			return new ResponseEntity<TableData<SpAnalyticsPojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error in fetching Top High Performance Suppier By BU data : " + e.getMessage());
			HttpHeaders header = new HttpHeaders();
			header.add("error", e.getMessage());
			return new ResponseEntity<TableData<SpAnalyticsPojo>>(null, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/getTopHighPerformanceSuppByProcCat", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<TableData<SpAnalyticsPojo>> getTopHighPerformanceSuppByProcCat(@RequestParam("startDate") String startDate, @RequestParam(value = "endDate", required = false) String endDate, @RequestParam(value = "pcId", required = false) String pcId, HttpSession session) {
		LOG.info("Top High Performance Suppier By Proc Cat for start date : " + startDate + " and end date : " + endDate);
		try {
			TableData<SpAnalyticsPojo> data = null;
			List<SpAnalyticsPojo> list = null;
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

			list = supplierPerformanceAnalyticsService.getTopHighPerformanceSuppierByProcCat(start, end, pcId, SecurityLibrary.getLoggedInUserTenantId());
			data = new TableData<SpAnalyticsPojo>(list);

			return new ResponseEntity<TableData<SpAnalyticsPojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error in fetching Top High Performance Suppier By Proc Cat data : " + e.getMessage());
			HttpHeaders header = new HttpHeaders();
			header.add("error", e.getMessage());
			return new ResponseEntity<TableData<SpAnalyticsPojo>>(null, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/getTopLowPerformanceSuppier", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<TableData<SpAnalyticsPojo>> getTopLowPerformanceSuppier(@RequestParam("startDate") String startDate, @RequestParam(value = "endDate", required = false) String endDate, HttpSession session) {
		LOG.info("Top Low Performance Suppier for start date : " + startDate + " and end date : " + endDate);
		try {
			TableData<SpAnalyticsPojo> data = null;
			List<SpAnalyticsPojo> list = null;
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

			list = supplierPerformanceAnalyticsService.getTopLowPerformanceSuppier(start, end, SecurityLibrary.getLoggedInUserTenantId());
			data = new TableData<SpAnalyticsPojo>(list);

			return new ResponseEntity<TableData<SpAnalyticsPojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error in fetching Top Low Performance Suppier data : " + e.getMessage());
			HttpHeaders header = new HttpHeaders();
			header.add("error", e.getMessage());
			return new ResponseEntity<TableData<SpAnalyticsPojo>>(null, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/getTopLowPerformanceSuppByBU", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<TableData<SpAnalyticsPojo>> getTopLowPerformanceSuppByBU(@RequestParam("startDate") String startDate, @RequestParam(value = "endDate", required = false) String endDate, @RequestParam(value = "buId", required = false) String buId, HttpSession session) {
		LOG.info("Top LOw Performance Suppier By BU for start date : " + startDate + " and end date : " + endDate);
		try {
			TableData<SpAnalyticsPojo> data = null;
			List<SpAnalyticsPojo> list = null;
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

			list = supplierPerformanceAnalyticsService.getTopLowPerformanceSuppByBU(start, end, buId, SecurityLibrary.getLoggedInUserTenantId());
			data = new TableData<SpAnalyticsPojo>(list);

			return new ResponseEntity<TableData<SpAnalyticsPojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error in fetching Top Low Performance Suppier By BU data : " + e.getMessage());
			HttpHeaders header = new HttpHeaders();
			header.add("error", e.getMessage());
			return new ResponseEntity<TableData<SpAnalyticsPojo>>(null, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/getTopLowPerfSuppByProcCat", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<TableData<SpAnalyticsPojo>> getTopLowPerfSuppByProcCat(@RequestParam("startDate") String startDate, @RequestParam(value = "endDate", required = false) String endDate, @RequestParam(value = "pcId", required = false) String pcId, HttpSession session) {
		LOG.info("Top LOw Performance Suppier By Category for start date : " + startDate + " and end date : " + endDate);
		try {
			TableData<SpAnalyticsPojo> data = null;
			List<SpAnalyticsPojo> list = null;
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

			list = supplierPerformanceAnalyticsService.getTopLowPerformanceSuppByProcCat(start, end, pcId, SecurityLibrary.getLoggedInUserTenantId());
			data = new TableData<SpAnalyticsPojo>(list);

			return new ResponseEntity<TableData<SpAnalyticsPojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error in fetching Top Low Performance Suppier By Category data : " + e.getMessage());
			HttpHeaders header = new HttpHeaders();
			header.add("error", e.getMessage());
			return new ResponseEntity<TableData<SpAnalyticsPojo>>(null, header, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}

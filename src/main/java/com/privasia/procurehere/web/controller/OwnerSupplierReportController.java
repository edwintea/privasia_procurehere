package com.privasia.procurehere.web.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.privasia.procurehere.core.entity.CompanyStatus;
import com.privasia.procurehere.core.enums.SubscriptionStatus;
import com.privasia.procurehere.core.enums.SupplierStatus;
import com.privasia.procurehere.core.pojo.SupplierReportPojo;
import com.privasia.procurehere.core.pojo.SupplierSearchFilterPojo;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.CompanyStatusService;
import com.privasia.procurehere.service.supplier.SupplierService;

/**
 * @author Pooja
 */
@Controller
@RequestMapping("/owner")
public class OwnerSupplierReportController {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Autowired
	MessageSource messageSource;

	@Autowired
	SupplierService supplierService;

	@Autowired
	CompanyStatusService companyStatusService;

	@ModelAttribute("statusList")
	public List<SupplierStatus> getStatusList() {
		return Arrays.asList(SupplierStatus.values());
	}

	@ModelAttribute("companyStatusList")
	public List<CompanyStatus> populateCompanyStatus() {
		return companyStatusService.getAllComapnyStatus();
	}

	@ModelAttribute("subscriptionStatusList")
	public List<SubscriptionStatus> getSubscriptionStatusList() {
		return Arrays.asList(SubscriptionStatus.values());
	}

	@RequestMapping(path = "/supplierReportList", method = RequestMethod.GET)
	public String supplierReportList() {
		return "supplierReportList";
	}

	@RequestMapping(path = "/supplierReportListData", method = RequestMethod.GET)
	public ResponseEntity<TableData<SupplierReportPojo>> supplierReportListData(TableDataInput input, HttpSession session, HttpServletResponse response, @RequestParam(required = false) String dateTimeRange) {
		LOG.info("Getting supplier report list");
		try {
			LOG.info("dateTimeRange :" + dateTimeRange);
			Date startDate = null;
			Date endDate = null;
			if (StringUtils.checkString(dateTimeRange).length() > 0) {
				TimeZone timeZone = TimeZone.getDefault();
				String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
				if (strTimeZone != null) {
					timeZone = TimeZone.getTimeZone(strTimeZone);
				}
				if (StringUtils.checkString(dateTimeRange).length() > 0) {
					String dateTimeArr[] = dateTimeRange.split("-");
					DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
					formatter.setTimeZone(timeZone);
					startDate = (Date) formatter.parse(dateTimeArr[0]);
					endDate = (Date) formatter.parse(dateTimeArr[1]);
					LOG.info("Start date : " + startDate + " End Date : " + endDate);
				}
			}
			TableData<SupplierReportPojo> data = new TableData<SupplierReportPojo>(supplierService.findAllSearchFilterSupplierReportList(input, startDate, endDate));
			data.setDraw(input.getDraw());
			long recordFiltered = supplierService.findTotalSearchFilterSupplierReportCount(input, startDate, endDate);
			data.setRecordsFiltered(recordFiltered);
			long totalRecord = supplierService.findTotalSuppliersCount(startDate, endDate);
			data.setRecordsTotal(totalRecord);
			return new ResponseEntity<TableData<SupplierReportPojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching supplier report list : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching supplier report list : " + e.getMessage());
			return new ResponseEntity<TableData<SupplierReportPojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/exportSupplierReport", method = RequestMethod.GET)
	public String exportSupplierReport(HttpSession session, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redir, @ModelAttribute("supplierSearchFilterPojo") SupplierSearchFilterPojo supplierSearchFilterPojo, boolean select_all, @RequestParam(required = false, name = "dateTimeRange") String dateTimeRange) throws IOException {

		try {

			String supplieArr[] = null;
			if (StringUtils.checkString(supplierSearchFilterPojo.getSupplierIds()).length() > 0) {
				supplieArr = supplierSearchFilterPojo.getSupplierIds().split(",");
				LOG.info("Id " + supplierSearchFilterPojo.getSupplierIds());
			}

			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			formatter.setTimeZone(timeZone);

			Date startDate = null;
			Date endDate = null;
			LOG.info("dateTimeRange :" + dateTimeRange);
			if (StringUtils.checkString(dateTimeRange).length() > 0) {
	 			if (StringUtils.checkString(dateTimeRange).length() > 0) {
					String dateTimeArr[] = dateTimeRange.split("-");
					DateFormat dFormatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
					dFormatter.setTimeZone(timeZone);
					startDate = (Date) dFormatter.parse(dateTimeArr[0]);
					endDate = (Date) dFormatter.parse(dateTimeArr[1]);
					LOG.info("Start date : " + startDate + " End Date : " + endDate);
				}
			}
			
			File file = File.createTempFile("Supplier_Report", ".csv");
			LOG.info("Starting...................................");
			supplierService.downloadCsvFileForSupplierList(response, file, supplierSearchFilterPojo, select_all, supplieArr, formatter, startDate, endDate);
			LOG.info("End...................................");
			// Send to UI
			response.setContentType("text/csv");
			response.addHeader("Content-Disposition", "attachment; filename=Supplier_Report.csv");
			response.setContentLengthLong(file.length());
			Files.copy(file.toPath(), response.getOutputStream());
			response.getOutputStream().flush();

		} catch (Exception e) {
			LOG.error("Error while downloading csv file : " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("buyerreport.error.download.csv", new Object[] { e.getMessage() }, Global.LOCALE));
			return "redirect:supplierReportList";
		}
		return null;
	}

}
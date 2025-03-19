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
import com.privasia.procurehere.core.enums.BuyerStatus;
import com.privasia.procurehere.core.enums.PlanType;
import com.privasia.procurehere.core.pojo.BuyerReportPojo;
import com.privasia.procurehere.core.pojo.BuyerSearchFilterPojo;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.BuyerService;
import com.privasia.procurehere.service.CompanyStatusService;

/**
 * @author Pooja
 */
@Controller
@RequestMapping("/owner")
public class OwnerBuyerReportController {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Autowired
	MessageSource messageSource;

	@Autowired
	BuyerService buyerService;

	@Autowired
	CompanyStatusService companyStatusService;

	@ModelAttribute("statusList")
	public List<BuyerStatus> getStatusList() {
		return Arrays.asList(BuyerStatus.values());
	}

	@ModelAttribute("companyStatusList")
	public List<CompanyStatus> populateCompanyStatus() {
		return companyStatusService.getAllComapnyStatus();
	}

	@ModelAttribute("planTypeList")
	public List<PlanType> getPlanList() {
		return Arrays.asList(PlanType.values());
	}

	@RequestMapping(path = "/buyerReportList", method = RequestMethod.GET)
	public String buyerReportList() {
		return "buyerReportList";
	}

	@RequestMapping(path = "/buyerReportListData", method = RequestMethod.GET)
	public ResponseEntity<TableData<BuyerReportPojo>> buyerReportListData(TableDataInput input, @RequestParam(required = false) String dateTimeRange, HttpSession session, HttpServletResponse response) {
		LOG.info("Getting buyer report list");
		try {
			LOG.info("dateTimeRange :" + dateTimeRange);

			LOG.info("Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort());
			LOG.info(">>>>>>>>>>>>> Table Data Input : " + input.toString());
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
			
			TableData<BuyerReportPojo> data = new TableData<BuyerReportPojo>(buyerService.findAllSearchFilterBuyerReportList(input, startDate, endDate));
			data.setDraw(input.getDraw());
			long recordFiltered = buyerService.findTotalSearchFilterBuyerReportCount(input, startDate, endDate);
			data.setRecordsFiltered(recordFiltered);
			long totalRecord = buyerService.findTotalBuyerReportCount(startDate, endDate);
			LOG.info("recordFiltered : " + recordFiltered + " totalRecord : " + totalRecord);
			data.setRecordsTotal(totalRecord);
			return new ResponseEntity<TableData<BuyerReportPojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching buyer report list : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching buyer report list : " + e.getMessage());
			return new ResponseEntity<TableData<BuyerReportPojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/exportBuyerReport", method = RequestMethod.GET)
	public String exportCsvFile(HttpSession session, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redir, @ModelAttribute("searchFilterPaymentTransPojo") BuyerSearchFilterPojo buyerSearchFilterPojo, boolean select_all, @RequestParam(required = false, name = "dateTimeRange") String dateTimeRange) throws IOException {

		try {

			String buyerArr[] = null;
			if (StringUtils.checkString(buyerSearchFilterPojo.getBuyerIds()).length() > 0) {
				buyerArr = buyerSearchFilterPojo.getBuyerIds().split(",");
				LOG.info("----------Ids " + buyerArr.length);
			}

			LOG.info("dateTimeRange :" + dateTimeRange);
			TimeZone timeZone = TimeZone.getDefault();
			String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
			if (strTimeZone != null) {
				timeZone = TimeZone.getTimeZone(strTimeZone);
			}
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			formatter.setTimeZone(timeZone);

  			Date startDate = null;
			Date endDate = null;
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

			File file = File.createTempFile("Buyer_Report", ".csv");

			buyerService.downloadCsvFileForBuyerList(response, file, buyerSearchFilterPojo, select_all, buyerArr, formatter, startDate, endDate);

			// Send to UI
			response.setContentType("text/csv");
			response.addHeader("Content-Disposition", "attachment; filename=Buyer_Report.csv");
			response.setContentLengthLong(file.length());
			Files.copy(file.toPath(), response.getOutputStream());
			response.getOutputStream().flush();

		} catch (Exception e) {
			LOG.error("Error while downloading csv file : " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("buyerreport.error.download.csv", new Object[] { e.getMessage() }, Global.LOCALE));
			return "redirect:buyerReportList";
		}
		return null;
	}

}
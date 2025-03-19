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

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.privasia.procurehere.core.entity.PaymentTransaction;
import com.privasia.procurehere.core.enums.TransactionStatus;
import com.privasia.procurehere.core.enums.TransactionType;
import com.privasia.procurehere.core.pojo.PaymentTransactionPojo;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.PaymentTransactionService;

/**
 * @author Nitin Otageri
 */
@Controller
@RequestMapping("/owner/paymentTransaction")
public class PaymentTransactionController {

	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@Autowired
	PaymentTransactionService paymentTransactionService;

	@Resource
	MessageSource messageSource;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	}

	@ModelAttribute("transactionTypeList")
	public List<TransactionType> getTransactionTypeList() {
		return Arrays.asList(TransactionType.values());
	}

	@ModelAttribute("statusList")
	public List<TransactionStatus> getStatusList() {
		return Arrays.asList(TransactionStatus.values());
	}

	@RequestMapping(path = "/viewPaymentTransaction/{id}", method = RequestMethod.GET)
	public String viewPaymentTransaction(@PathVariable(name = "id") String id, Model model, RedirectAttributes redir) {
		LOG.info("Getting the PaymentTransaction : " + id);
		PaymentTransaction paymentTransaction = paymentTransactionService.getPaymentTransactionById(id);
		// Error condition. Send the user back to listing screen.
		if (paymentTransaction == null) {
			redir.addFlashAttribute("error", messageSource.getMessage("common.no.records.found", new Object[] { id }, Global.LOCALE));
			return "redirect:paymentTransactionList";
		}
		model.addAttribute("paymentTransaction", paymentTransaction);

		return "paymentTransaction";
	}

	@RequestMapping(path = "/paymentTransactionList", method = RequestMethod.GET)
	public String paymentTransactionList(Model model) {
		return "paymentTransactionList";
	}

	@RequestMapping(path = "/paymentTransactionData", method = RequestMethod.GET)
	public ResponseEntity<TableData<PaymentTransaction>> paymentTransactionData(TableDataInput input, @RequestParam(required = false) String dateTimeRange, HttpSession session, HttpServletResponse response) {
		try {
			LOG.info("Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort() + ", dateTimeRange :" + dateTimeRange);
			LOG.debug(">>>>>>>>>>>>> Table Data Input : " + input.toString());
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
					// LOG.info("Start date : " + startDate + " End Date : " + endDate);
				}
			}
			TableData<PaymentTransaction> data = new TableData<PaymentTransaction>(paymentTransactionService.findPaymentTransactions(input, startDate, endDate));
			long totalFilterCount = paymentTransactionService.findTotalFilteredPaymentTransactions(input, startDate, endDate);
			data.setRecordsFiltered(totalFilterCount);
			long totalCount = paymentTransactionService.findTotalPaymentTransactions();
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<PaymentTransaction>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching Payment Transaction list : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching Payment Transaction list : " + e.getMessage());
			return new ResponseEntity<TableData<PaymentTransaction>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/exportPaymentTransactionReport", method = RequestMethod.GET)
	public void exportCsvFile(HttpSession session, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redir, @ModelAttribute("searchFilterPaymentTransPojo") PaymentTransactionPojo paymentTransactionPojo, boolean select_all, @RequestParam String dateTimeRange) throws IOException {

		try {

			String paymentArr[] = null;
			if (StringUtils.checkString(paymentTransactionPojo.getPaymentIds()).length() > 0) {
				paymentArr = paymentTransactionPojo.getPaymentIds().split(",");
				LOG.info("_----------Id " + paymentTransactionPojo.getPaymentIds());
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
			if (StringUtils.checkString(dateTimeRange).length() > 0) {
				String dateTimeArr[] = dateTimeRange.split("-");

				startDate = (Date) formatter.parse(dateTimeArr[0]);
				endDate = (Date) formatter.parse(dateTimeArr[1]);
				LOG.info("Start date : " + startDate + " End Date : " + endDate);
			}
			File file = File.createTempFile("Payment_Transaction_Report", ".csv");

			paymentTransactionService.downloadCsvFileForPaymentTransactionList(response, file, paymentTransactionPojo, select_all, startDate, endDate, paymentArr, formatter);

			// Send to UI
			response.setContentType("text/csv");
			response.addHeader("Content-Disposition", "attachment; filename=Payment_Transaction_Report.csv");
			response.setContentLengthLong(file.length());
			Files.copy(file.toPath(), response.getOutputStream());
			response.getOutputStream().flush();

		} catch (Exception e) {
			LOG.error("Error while downloading csv file : " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("payementreport.error.download.csv", new Object[] { e.getMessage() }, Global.LOCALE));
		}
	}

}

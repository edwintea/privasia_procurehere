/**
 * 
 */
package com.privasia.procurehere.web.controller;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.annotation.Resource;
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

import com.privasia.procurehere.core.enums.FinanceRequestStatus;
import com.privasia.procurehere.core.pojo.InvoiceFinanceRequestPojo;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.InvoiceFinanceRequestService;

/**
 * @author pooja
 */
@Controller
@RequestMapping("/supplier/invoiceFinanceRequest")
public class InvoiceFinanceRequestSupplierController implements Serializable {

	private static final long serialVersionUID = 6813277285575700158L;

	private static final Logger LOG = LogManager.getLogger(InvoiceFinanceRequestSupplierController.class);

	@Resource
	MessageSource messageSource;

	@Autowired
	InvoiceFinanceRequestService invoiceFinanceRequestService;

	@ModelAttribute("statusList")
	public List<FinanceRequestStatus> getStatusList() {
		return Arrays.asList(FinanceRequestStatus.values());
	}

	@RequestMapping(path = "/requestList", method = RequestMethod.GET)
	public String invoiceList() {
		return "supplierInvoiceFinanceRequestList";
	}

	@RequestMapping(path = "/requestListData", method = RequestMethod.GET)
	public ResponseEntity<TableData<InvoiceFinanceRequestPojo>> invoiceListData(TableDataInput input, @RequestParam(required = false) String dateTimeRange, HttpSession session, HttpServletResponse response) {
		try {
			LOG.info("Supplier Id :" + SecurityLibrary.getLoggedInUserTenantId() + " user id : " + SecurityLibrary.getLoggedInUser().getId());
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
				}
			}
			List<InvoiceFinanceRequestPojo> invoiceList = invoiceFinanceRequestService.findAllInvoiceFinanceRequestsForSupplier(input, SecurityLibrary.getLoggedInUserTenantId());
			TableData<InvoiceFinanceRequestPojo> data = new TableData<InvoiceFinanceRequestPojo>(invoiceList);
			data.setDraw(input.getDraw());
			long recordFiltered = invoiceFinanceRequestService.findTotalFilteredInvoiceFinanceRequestsForSupplier(input, SecurityLibrary.getLoggedInUserTenantId());
			long totalCount = invoiceFinanceRequestService.findTotalInvoiceFinanceRequestsForSupplier(SecurityLibrary.getLoggedInUserTenantId());
			LOG.info(" totalCount : " + totalCount);
			data.setRecordsFiltered(recordFiltered);
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<InvoiceFinanceRequestPojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching invoice List For Supplier: " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching invoice List For Supplier : " + e.getMessage());
			return new ResponseEntity<TableData<InvoiceFinanceRequestPojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}

package com.privasia.procurehere.web.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.FinanceAuditTrail;
import com.privasia.procurehere.core.entity.OwnerAuditTrail;
import com.privasia.procurehere.core.entity.SupplierAuditTrail;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.BuyerAuditTrailService;
import com.privasia.procurehere.service.FinanceAuditTrailService;
import com.privasia.procurehere.service.OwnerAuditTrailService;
import com.privasia.procurehere.service.SupplierAuditTrailService;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

/**
 * @author Parveen
 */
@Controller
@RequestMapping("/")
public class OwnerAuditTrailController {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Autowired
	MessageSource messageSource;

	@Autowired
	OwnerAuditTrailService ownerAuditTrailService;

	@Autowired
	BuyerAuditTrailService buyerAuditTrailService;
	
	@Autowired
	FinanceAuditTrailService financeAuditTrailService;

	@Autowired
	SupplierAuditTrailService supplierAuditTrailService;
	
	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@ModelAttribute("activityList")
	public List<AuditTypes> getStatusList() {
		return Arrays.asList(AuditTypes.values());
	}

	@ModelAttribute("moduleTypeList")
	public List<ModuleType> getModuleTypeList() {
		List<ModuleType> modulelist = null;
		switch (SecurityLibrary.getLoggedInUser().getTenantType()) {
		case BUYER:
			modulelist = ModuleType.getModuleTypeForBuyer();
			return modulelist;
		case OWNER:
			modulelist = ModuleType.getModuleTypeForOwner();
			return modulelist;
		case SUPPLIER:
			modulelist = ModuleType.getModuleTypeForSupplier();
			return modulelist;
		default:
			break;
		}
		return null;

	}

	@RequestMapping(path = "/listAuditTrail", method = RequestMethod.GET)
	public String listAuditTrail(Model model) {
		return "listAuditTrail";
	}

	@RequestMapping(path = "/auditTrailData", method = RequestMethod.GET)
	public ResponseEntity<TableData<?>> auditTrailData(TableDataInput input, @RequestParam(required = false) String dateTimeRange, HttpSession session, @RequestParam(required = false) String moduleType) {
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
			LOG.info("*********module Type*********" + moduleType);
			TableData<?> data = null;
			long totalFilterCount = 0l;
			long totalCount = 0l;
			switch (SecurityLibrary.getLoggedInUser().getTenantType()) {
			case BUYER:
				data = new TableData<BuyerAuditTrail>(buyerAuditTrailService.findAuditTrailForTenant(SecurityLibrary.getLoggedInUserTenantId(), input, startDate, endDate, moduleType));
				data.setDraw(input.getDraw());
				totalFilterCount = buyerAuditTrailService.findTotalFilteredAuditTrailForTenant(SecurityLibrary.getLoggedInUserTenantId(), input, startDate, endDate, moduleType);
				totalCount = buyerAuditTrailService.findTotalAuditTrailForTenant(SecurityLibrary.getLoggedInUserTenantId());
				break;
			case OWNER:
				data = new TableData<OwnerAuditTrail>(ownerAuditTrailService.findOwnerAuditTrailForTenant(SecurityLibrary.getLoggedInUserTenantId(), input, startDate, endDate, moduleType));
				data.setDraw(input.getDraw());
				totalFilterCount = ownerAuditTrailService.findTotalFilteredOwnerAuditTrailForTenant(SecurityLibrary.getLoggedInUserTenantId(), input, startDate, endDate, moduleType);
				totalCount = ownerAuditTrailService.findTotalOwnerAuditTrailForTenant(SecurityLibrary.getLoggedInUserTenantId());
				break;
			case SUPPLIER:
				data = new TableData<SupplierAuditTrail>(supplierAuditTrailService.findAuditTrailForTenant(SecurityLibrary.getLoggedInUserTenantId(), input, startDate, endDate, moduleType));
				data.setDraw(input.getDraw());
				totalFilterCount = supplierAuditTrailService.findTotalFilteredAuditTrailForTenant(SecurityLibrary.getLoggedInUserTenantId(), input, startDate, endDate, moduleType);
				totalCount = supplierAuditTrailService.findTotalAuditTrailForTenant(SecurityLibrary.getLoggedInUserTenantId());

				break;
				
				case FINANCE_COMPANY:
				data = new TableData<FinanceAuditTrail>(financeAuditTrailService.findAuditTrailForTenant(SecurityLibrary.getLoggedInUserTenantId(), input, startDate, endDate));
				data.setDraw(input.getDraw());
				totalFilterCount = financeAuditTrailService.findTotalFilteredAuditTrailForTenant(SecurityLibrary.getLoggedInUserTenantId(), input, startDate, endDate);
				totalCount = financeAuditTrailService.findTotalAuditTrailForTenant(SecurityLibrary.getLoggedInUserTenantId());

				break;
				
				
			default:
				break;

			}
			data.setRecordsFiltered(totalFilterCount);
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<?>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching audit Trail Data  list : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching audit Trail Data list : " + e.getMessage());
			return new ResponseEntity<TableData<?>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/downloadAuditTrail", method = RequestMethod.GET)
	public void downloadAuditTrail(TableDataInput input, @RequestParam(required = false, name = "dateTimeRange") String dateTimeRange, HttpSession session, HttpServletResponse response, @RequestParam(required = false) String moduleType) throws Exception {
		LOG.info("Date Range :: " + dateTimeRange);
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
		try {
			String filename = "";
			JasperPrint jasperPrint = null;
			long totalCount = 0l;
			switch (SecurityLibrary.getLoggedInUser().getTenantType()) {
			case BUYER:
				totalCount = buyerAuditTrailService.findTotalAuditTrailForTenant(SecurityLibrary.getLoggedInUserTenantId());
				input.setLength((int) totalCount);
				jasperPrint = buyerAuditTrailService.getAuditTrailPdf(SecurityLibrary.getLoggedInUserTenantId(), input, startDate, endDate, session, moduleType);
				filename = "Buyer AuditTrail Report.pdf";
				try {
					LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Audit trail for buyer is successfully downloaded", SecurityLibrary.getLoggedInUserTenantId(),SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.AuditTrail);
					buyerAuditTrailDao.save(buyerAuditTrail);
					LOG.info("--------------------------AFTER AUDIT---------------------------------------");
				} catch (Exception e) {
					LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
				}
				break;
			case OWNER:
				totalCount = ownerAuditTrailService.findTotalOwnerAuditTrailForTenant(SecurityLibrary.getLoggedInUserTenantId());
				input.setLength((int) totalCount);
				jasperPrint = ownerAuditTrailService.getAuditTrailPdf(SecurityLibrary.getLoggedInUserTenantId(), input, startDate, endDate, session, moduleType);
				filename = "Owner AuditTrail Report.pdf";
				break;
			case SUPPLIER:
				totalCount = supplierAuditTrailService.findTotalAuditTrailForTenant(SecurityLibrary.getLoggedInUserTenantId());
				input.setLength((int) totalCount);
				jasperPrint = supplierAuditTrailService.getAuditTrailPdf(SecurityLibrary.getLoggedInUserTenantId(), input, startDate, endDate, session, moduleType);
				filename = "Supplier AuditTrail Report.pdf";
				break;
				
			case FINANCE_COMPANY:
				totalCount = financeAuditTrailService.findTotalAuditTrailForTenant(SecurityLibrary.getLoggedInUserTenantId());
				input.setLength((int) totalCount);
				jasperPrint = financeAuditTrailService.getAuditTrailPdf(SecurityLibrary.getLoggedInUserTenantId(), input, startDate, endDate, session);
				filename = "Finance AuditTrail Report.pdf";
				break;
				
			default:
				break;
			}
			if (jasperPrint != null) {
				streamReport(jasperPrint, filename, response);
			}
		} catch (Exception e) {
			LOG.error("Could not generate Evaluation Summary Report. " + e.getMessage(), e);
		}
	}

	private void streamReport(JasperPrint jasperPrint, String filename, HttpServletResponse response) throws JRException, IOException {
		response.setContentType("application/pdf");
		response.addHeader("Content-Disposition", "attachment; filename=" + filename);

		JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
		response.getOutputStream().flush();
		response.getOutputStream().close();

	}
}
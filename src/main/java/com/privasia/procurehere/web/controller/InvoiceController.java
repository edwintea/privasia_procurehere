package com.privasia.procurehere.web.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.Invoice;
import com.privasia.procurehere.core.entity.InvoiceAudit;
import com.privasia.procurehere.core.entity.InvoiceFinanceRequest;
import com.privasia.procurehere.core.entity.InvoiceItem;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.InvoiceAuditType;
import com.privasia.procurehere.core.enums.InvoiceAuditVisibilityType;
import com.privasia.procurehere.core.enums.InvoiceStatus;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.pojo.InvoiceSupplierPojo;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.InvoiceAuditService;
import com.privasia.procurehere.service.InvoiceFinanceRequestService;
import com.privasia.procurehere.service.InvoiceService;
import com.privasia.procurehere.service.PoService;

@Controller
@RequestMapping("/buyer")
public class InvoiceController {

	private static final Logger LOG = LogManager.getLogger(Global.DO_LOG);

	@Value("${app.url}")
	String APP_URL;

	@Autowired
	InvoiceService invoiceService;

	@Autowired
	PoService poService;

	@Autowired
	MessageSource messageSource;

	@Autowired
	InvoiceAuditService invoiceAuditService;

	@Autowired
	ServletContext context;

	@Autowired
	InvoiceFinanceRequestService invoiceFinanceRequestService;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@ModelAttribute("invoiceStatusList")
	public List<InvoiceStatus> getInvoiceStatusList() {
		return Arrays.asList(InvoiceStatus.ACCEPTED, InvoiceStatus.DECLINED, InvoiceStatus.INVOICED);

	}

	@RequestMapping(path = "/invoiceList", method = RequestMethod.GET)
	public String invoiceList() {
		return "buyerInvoiceList";
	}

	@RequestMapping(path = "/invoiceListData", method = RequestMethod.GET)
	public ResponseEntity<TableData<InvoiceSupplierPojo>> invoiceListData(TableDataInput input, @RequestParam(required = false) String dateTimeRange, HttpSession session, HttpServletResponse response) {
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
			List<InvoiceSupplierPojo> invoiceList = invoiceService.findAllSearchFilterInvoiceForBuyer(SecurityLibrary.getLoggedInUserTenantId(), input, startDate, endDate);
			TableData<InvoiceSupplierPojo> data = new TableData<InvoiceSupplierPojo>(invoiceList);
			data.setDraw(input.getDraw());
			long recordFiltered = invoiceService.findTotalSearchFilterInvoiceForBuyer(SecurityLibrary.getLoggedInUserTenantId(), input, startDate, endDate);
			long totalCount = invoiceService.findTotalInvoiceForBuyer(SecurityLibrary.getLoggedInUserTenantId());
			LOG.info(" totalCount : " + totalCount);
			data.setRecordsFiltered(recordFiltered);
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<InvoiceSupplierPojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching invoice List For Buyer: " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching invoice List For Buyer : " + e.getMessage());
			return new ResponseEntity<TableData<InvoiceSupplierPojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/invoiceView/{invoiceId}", method = RequestMethod.GET)
	public String invoiceView(@PathVariable String invoiceId, Model model, HttpServletRequest request, RedirectAttributes redir) {
		LOG.info("Buyer View GET called By Invoice id :" + invoiceId);
		try {
			constructInvoiceSummaryAttributesForBuyerView(invoiceId, model);
		} catch (Exception e) {
			LOG.info("Error in view Invoice For Supplier:" + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("error.view.do.supplier", new Object[] { e.getMessage() }, Global.LOCALE));
		}
		return "buyerInvoiceView";
	}

	public Invoice constructInvoiceSummaryAttributesForBuyerView(String invoiceId, Model model) {
		Invoice invoice = invoiceService.getInvoiceByIdForSupplierView(invoiceId);
		model.addAttribute("invoice", invoice);
		long count = invoiceFinanceRequestService.findOnboardedBuyerForInvoiceRequest(invoice.getBuyer().getId());
		model.addAttribute("buyerOnboarded", (count > 0));
		if (count > 0) {
			InvoiceFinanceRequest invoiceFinanceRequest = invoiceFinanceRequestService.findInvoiceFinanceRequestByInvoiceId(invoice.getId());
			LOG.info("IFR : " + (invoiceFinanceRequest == null ? "NULL" : "Exists " + invoiceFinanceRequest.getId()));
			;
			model.addAttribute("invoiceFinanceRequest", invoiceFinanceRequest);
		}
		List<InvoiceItem> invoiceItemlist = invoiceService.findAllInvoiceItemByInvoiceIdForSummary(invoiceId);
		LOG.info("invoiceItemlist" + invoiceItemlist.size());
		model.addAttribute("invoiceItemlist", invoiceItemlist);
		List<InvoiceAudit> auditList = invoiceAuditService.getInvoiceAuditForBuyerByInvoiceId(invoiceId);
		model.addAttribute("auditList", auditList);

		return invoice;
	}

	@RequestMapping(path = "/declineInvoice", method = RequestMethod.POST)
	public String declineInvoice(@RequestParam("invoiceId") String invoiceId, @RequestParam("buyerRemark") String buyerRemark, HttpServletResponse response, HttpSession session, RedirectAttributes redir) throws Exception {
		try {
			LOG.info("Buyer Invoice DECLINED :++++++++++++++++++++++++++++++++++ " + invoiceId);
			Invoice invoice = invoiceService.declineInvoice(invoiceId, SecurityLibrary.getLoggedInUser(), buyerRemark);
			if (invoice != null) {
				redir.addFlashAttribute("success", messageSource.getMessage("buyer.declined.success.invoice", new Object[] { invoice.getInvoiceId() }, Global.LOCALE));
			} else {
				redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.declining.invoice", new Object[] {}, Global.LOCALE));
			}
		} catch (Exception e) {
			redir.addFlashAttribute("error", messageSource.getMessage("buyer.declining.invoice.error", new Object[] { e.getMessage() }, Global.LOCALE));
			LOG.error("error while declining Do " + e.getMessage(), e);
		}
		return "redirect:invoiceView/" + invoiceId;
	}

	@RequestMapping(path = "/acceptInvoice", method = RequestMethod.POST)
	public String acceptInvoice(@RequestParam("invoiceId") String invoiceId, @RequestParam("buyerRemark") String buyerRemark, HttpServletResponse response, RedirectAttributes redir) throws Exception {
		try {
			LOG.info("Buyer Invoice ACCEPTED :++++++++++++++++++++++++++++++++++ " + invoiceId);
			Invoice invoice = invoiceService.acceptInvoice(invoiceId, SecurityLibrary.getLoggedInUser(), buyerRemark);
			if (invoice != null) {
				redir.addFlashAttribute("success", messageSource.getMessage("buyer.accepted.success.invoice", new Object[] { invoice.getInvoiceId() }, Global.LOCALE));
			} else {
				redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.accepting.invoice", new Object[] {}, Global.LOCALE));
			}
			
		} catch (Exception e) {
			redir.addFlashAttribute("error", messageSource.getMessage("buyer.accepting.invoice.error", new Object[] { e.getMessage() }, Global.LOCALE));
			LOG.error("error while accepting invoice " + e.getMessage(), e);
		}
		return "redirect:invoiceView/" + invoiceId;
	}

	@RequestMapping(path = "/acceptFinanceRequest", method = RequestMethod.POST)
	public String acceptFinanceRequest(@RequestParam("invoiceId") String invoiceId, Model model, RedirectAttributes redir) {

		try {
			InvoiceFinanceRequest request = invoiceFinanceRequestService.acceptFinancingRequest(invoiceId, SecurityLibrary.getLoggedInUser());
			redir.addFlashAttribute("success", messageSource.getMessage("invoice.finance.request.accepted", new Object[] { request.getInvoiceNumber() }, Global.LOCALE));
		} catch (Exception e) {
			LOG.info("Error while accepting request for financing for the invoice: " + e.getMessage(), e);
			redir.addFlashAttribute("error", "Error while accepting request for financing for the invoice: " + e.getMessage());
			return "redirect:/supplier/invoice/" + invoiceId;
		}
		return "redirect:invoiceView/" + invoiceId;
	}

	@RequestMapping(path = "/declineFinanceRequest", method = RequestMethod.POST)
	public String declineFinanceRequest(@RequestParam("invoiceId") String invoiceId, Model model, RedirectAttributes redir) {

		try {
			InvoiceFinanceRequest request = invoiceFinanceRequestService.declineFinancingRequest(invoiceId, SecurityLibrary.getLoggedInUser());
			redir.addFlashAttribute("success", messageSource.getMessage("invoice.finance.request.declined", new Object[] { request.getInvoiceNumber() }, Global.LOCALE));
		} catch (Exception e) {
			LOG.info("Error while declining request for financing for the invoice: " + e.getMessage(), e);
			redir.addFlashAttribute("error", "Error while declining request for financing for the invoice: " + e.getMessage());
			return "redirect:/supplier/invoice/" + invoiceId;
		}
		return "redirect:invoiceView/" + invoiceId;
	}

	@RequestMapping(path = "/downloadInvoiceReport/{invoiceId}", method = RequestMethod.GET)
	public void downloadInvoiceReport(@PathVariable("invoiceId") String invoiceId, HttpServletResponse response, HttpSession session) throws Exception {
		try {
			LOG.info(" Invoice REPORT : " + invoiceId);
			Invoice invoice = invoiceService.findByInvoiceId(invoiceId);
			try {
				LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Invoice '"+invoice.getInvoiceId()+"' is downloaded", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.Invoice);
				buyerAuditTrailDao.save(buyerAuditTrail);
				LOG.info("--------------------------AFTER AUDIT---------------------------------------");
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
			}
			invoiceService.generateInvoiceReport(response, invoice);
			try {
				InvoiceAudit invoiceAudit = new InvoiceAudit();
				invoiceAudit.setAction(InvoiceAuditType.DOWNLOADED);
				invoiceAudit.setActionBy(SecurityLibrary.getLoggedInUser());
				invoiceAudit.setActionDate(new Date());
				invoiceAudit.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
				if (invoice.getSupplier() != null) {
					invoiceAudit.setSupplier(invoice.getSupplier());
				}
				invoiceAudit.setDescription(messageSource.getMessage("invoice.audit.downloadInvoice", new Object[] { invoice.getInvoiceId() }, Global.LOCALE));
				invoiceAudit.setVisibilityType(InvoiceAuditVisibilityType.BUYER);
				invoiceAudit.setInvoice(invoice);
				invoiceAuditService.save(invoiceAudit);
			} catch (Exception e) {
				LOG.error("Error while saving Invoice audit:" + e.getMessage(), e);
			}
		} catch (Exception e) {
			LOG.error("Could not generate Invoice Summary Report. " + e.getMessage(), e);
		}
	}

	@RequestMapping(path = "/ExportBuyerInvoiceReport", method = RequestMethod.POST)
	public void downloadBuyerInvoiceReport(HttpSession session, HttpServletRequest request, HttpServletResponse response, @ModelAttribute("invoiceSupplierPojo") InvoiceSupplierPojo invoiceSupplierPojo, boolean select_all, @RequestParam String dateTimeRange) throws IOException {
		LOG.info("Pojo -------Cont ---- " + invoiceSupplierPojo);
		try {
			String EventArr[] = null;
			if (StringUtils.checkString(invoiceSupplierPojo.getInvoiceIds()).length() > 0) {
				EventArr = invoiceSupplierPojo.getInvoiceIds().split(",");
			}

			LOG.info("dateTimeRange :" + dateTimeRange);
			Date startDate = null;
			Date endDate = null;

			if (StringUtils.checkString(dateTimeRange).length() > 0) {
				TimeZone timeZone = TimeZone.getDefault();
				String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
				if (strTimeZone != null) {
					timeZone = TimeZone.getTimeZone(strTimeZone);
				}

				String dateTimeArr[] = dateTimeRange.split("-");
				DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
				formatter.setTimeZone(timeZone);
				startDate = (Date) formatter.parse(dateTimeArr[0]);
				endDate = (Date) formatter.parse(dateTimeArr[1]);
				LOG.info("Start date : " + startDate + " End Date : " + endDate);
			}

			XSSFWorkbook workbook = new XSSFWorkbook();
			String downloadFolder = context.getRealPath("/WEB-INF/");
			String fileName = "Buyer_Invoice_Report.xlsx";
			Path file = Paths.get(downloadFolder, fileName);
			LOG.info("File Path ::" + file);

			buyerInvoiceReportDownloader(workbook, EventArr, session, invoiceSupplierPojo, select_all, startDate, endDate);

			// Save Excel File
			FileOutputStream out = new FileOutputStream(downloadFolder + "/" + fileName);
			workbook.write(out);
			out.close();
			LOG.info("Successfully written in Excel");
			if (Files.exists(file)) {
				response.setContentType("application/vnd.ms-excel");
				response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
				try {
					Files.copy(file, response.getOutputStream());
					response.getOutputStream().flush();
				} catch (IOException e) {
					LOG.error("Error :- " + e.getMessage());
				}
			}

		} catch (Exception e) {
			LOG.error("Error while downloading Invoice Report List :: " + e.getMessage(), e);
		}
	}

	private void buyerInvoiceReportDownloader(XSSFWorkbook workbook, String[] invoiceIds, HttpSession session, InvoiceSupplierPojo invoiceSupplierPojo, boolean select_all, Date startDate, Date endDate) {
		XSSFSheet sheet = workbook.createSheet("Buyer Invoice Report List");
		int r = 1;
		buildInvoiceHeader(workbook, sheet);

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		SimpleDateFormat deliveryDate = new SimpleDateFormat("dd/MM/yyyy ");
		if (session != null && session.getAttribute(Global.SESSION_TIME_ZONE_KEY) != null) {
			sdf.setTimeZone(TimeZone.getTimeZone((String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY)));
			deliveryDate.setTimeZone(TimeZone.getTimeZone((String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY)));
		} else {
			sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
			deliveryDate.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));

		}

		List<InvoiceSupplierPojo> invoiceList = getSearchBuyerInvoiceDetails(invoiceIds, invoiceSupplierPojo, select_all, startDate, endDate, sdf);

		if (CollectionUtil.isNotEmpty(invoiceList)) {
			for (InvoiceSupplierPojo invoiceReport : invoiceList) {

				DecimalFormat df = null;
				if (invoiceReport.getDecimal().equals("1")) {
					df = new DecimalFormat("#,###,###,##0.0");
				} else if (invoiceReport.getDecimal().equals("2")) {
					df = new DecimalFormat("#,###,###,##0.00");
				} else if (invoiceReport.getDecimal().equals("3")) {
					df = new DecimalFormat("#,###,###,##0.000");
				} else if (invoiceReport.getDecimal().equals("4")) {
					df = new DecimalFormat("#,###,###,##0.0000");
				} else if (invoiceReport.getDecimal().equals("5")) {
					df = new DecimalFormat("#,###,###,##0.00000");
				} else if (invoiceReport.getDecimal().equals("6")) {
					df = new DecimalFormat("#,###,###,##0.000000");
				} else {
					df = new DecimalFormat("#,###,###,##0.00");
				}

				Row row = sheet.createRow(r++);
				int cellNum = 0;

				row.createCell(cellNum++).setCellValue(invoiceReport.getInvoiceId() != null ? invoiceReport.getInvoiceId() : "");
				// row.createCell(cellNum++).setCellValue(invoiceReport.getReferenceNumber() != null ?
				// invoiceReport.getReferenceNumber() : "");
				row.createCell(cellNum++).setCellValue(invoiceReport.getName() != null ? invoiceReport.getName() : "");
				row.createCell(cellNum++).setCellValue(invoiceReport.getPoNumber() != null ? invoiceReport.getPoNumber() : "");
				row.createCell(cellNum++).setCellValue(invoiceReport.getSupplierCompanyName() != null ? invoiceReport.getSupplierCompanyName() : "");
				row.createCell(cellNum++).setCellValue(invoiceReport.getBusinessUnit() != null ? invoiceReport.getBusinessUnit() : "");
				// row.createCell(cellNum++).setCellValue(invoiceReport.getCreatedBy() != null ?
				// invoiceReport.getCreatedBy() : "");
				row.createCell(cellNum++).setCellValue(invoiceReport.getSendDate() != null ? sdf.format(invoiceReport.getSendDate()) : "");
				row.createCell(cellNum++).setCellValue(invoiceReport.getActionDate() != null ? sdf.format(invoiceReport.getActionDate()) : "");
				row.createCell(cellNum++).setCellValue(invoiceReport.getCurrency() != null ? invoiceReport.getCurrency() : "");
				Cell grandTotalCell = row.createCell(cellNum++);
				grandTotalCell.setCellValue(invoiceReport.getGrandTotal() != null ? df.format(invoiceReport.getGrandTotal()) : "");
				CellStyle gt = workbook.createCellStyle();
				gt.setAlignment(CellStyle.ALIGN_RIGHT);
				grandTotalCell.setCellStyle(gt);

				row.createCell(cellNum++).setCellValue(invoiceReport.getStatus() != null ? invoiceReport.getStatus().toString() : "");

			}
		}

		for (int k = 0; k < 10; k++) {
			sheet.autoSizeColumn(k, true);
		}

	}

	private void buildInvoiceHeader(XSSFWorkbook workbook, XSSFSheet sheet) {
		Row rowHeading = sheet.createRow(0);
		// Style of Heading Cells
		CellStyle styleHeading = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		styleHeading.setFont(font);
		styleHeading.setAlignment(CellStyle.ALIGN_CENTER);

		int i = 0;
		Cell cell = null;
		for (String column : Global.ALL_BUYER_INVOICE_REPORT_EXCEL_COLUMNS) {
			cell = rowHeading.createCell(i++);
			cell.setCellValue(column);
			cell.setCellStyle(styleHeading);
		}
	}

	private List<InvoiceSupplierPojo> getSearchBuyerInvoiceDetails(String[] invoiceIds, InvoiceSupplierPojo invoiceSupplierPojo, boolean select_all, Date startDate, Date endDate, SimpleDateFormat sdf) {
		return invoiceService.getAllBuyerInvoiceDetailsForExcelReport(SecurityLibrary.getLoggedInUserTenantId(), invoiceIds, invoiceSupplierPojo, select_all, startDate, endDate, sdf);
	}

	@RequestMapping(path = "/exportBuyerInvoiceCsv", method = RequestMethod.POST)
	public void downloadBuyerInvoiceCsv(HttpSession session, HttpServletRequest request, HttpServletResponse response, @ModelAttribute("invoiceSupplierPojo") InvoiceSupplierPojo invoiceSupplierPojo, boolean select_all, @RequestParam String dateTimeRange) throws IOException {
		LOG.info("Pojo -------Cont ---- " + invoiceSupplierPojo);
		try {
			File file = File.createTempFile("Buyer_Invoice_Report", ".csv");
			LOG.info("Temp file : " + file.getAbsolutePath());

			String invIds[] = null;
			if (StringUtils.checkString(invoiceSupplierPojo.getInvoiceIds()).length() > 0) {
				invIds = invoiceSupplierPojo.getInvoiceIds().split(",");
			}
			Date startDate = null;
			Date endDate = null;

			if (StringUtils.checkString(dateTimeRange).length() > 0) {
				String[] array = dateTimeRange.split("-");

				if (array.length > 0) {
					startDate = new SimpleDateFormat("dd/MM/yyyy hh:mm a").parse(array[0]);
					endDate = new SimpleDateFormat("dd/MM/yyyy hh:mm a").parse(array[1]);
				}
			}
			invoiceService.downloadCsvFileForInvoice(response, file, invIds, startDate, endDate, invoiceSupplierPojo, select_all, SecurityLibrary.getLoggedInUserTenantId(), session);
			try {
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "Invoice Report is successfully downloaded", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.Invoice);
				buyerAuditTrailDao.save(buyerAuditTrail);
			} catch (Exception e) {
				LOG.info("Error to create audit trails message");
			}
			
			// Send to UI
			response.setContentType("text/csv");
			response.addHeader("Content-Disposition", "attachment; filename=Buyer_Invoice_Report.csv");
			response.setContentLengthLong(file.length());
			Files.copy(file.toPath(), response.getOutputStream());
			response.getOutputStream().flush();

		} catch (Exception e) {
			LOG.error("Error while downloading Invoice Report List :: " + e.getMessage(), e);
		}
	}

}
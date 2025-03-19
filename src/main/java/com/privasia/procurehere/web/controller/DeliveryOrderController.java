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
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.DoReportDao;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.DeliveryOrder;
import com.privasia.procurehere.core.entity.DeliveryOrderAudit;
import com.privasia.procurehere.core.entity.DeliveryOrderItem;
import com.privasia.procurehere.core.entity.DoReport;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.DeliveryOrderAuditType;
import com.privasia.procurehere.core.enums.DoAuditVisibilityType;
import com.privasia.procurehere.core.enums.DoStatus;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.pojo.DoSupplierPojo;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.DeliveryOrderService;
import com.privasia.procurehere.service.DoAuditService;
import com.privasia.procurehere.service.PoService;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

@Controller
@RequestMapping("/buyer")
public class DeliveryOrderController {

	private static final Logger LOG = LogManager.getLogger(Global.DO_LOG);

	@Value("${app.url}")
	String APP_URL;

	@Autowired
	DeliveryOrderService deliveryOrderService;

	@Autowired
	PoService poService;

	@Autowired
	MessageSource messageSource;

	@Autowired
	DoAuditService doAuditService;

	@Autowired
	ServletContext context;

	@Autowired
	DoReportDao doReportDao;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@ModelAttribute("doStatusList")
	public List<DoStatus> getDoStatusList() {
		return Arrays.asList(DoStatus.ACCEPTED, DoStatus.DECLINED, DoStatus.DELIVERED);
	}

	@RequestMapping(path = "/deliveryOrderList", method = RequestMethod.GET)
	public String deliveryOrderList() {
		return "buyerDeliveryOrderList";
	}

	@RequestMapping(path = "/doListData", method = RequestMethod.GET)
	public ResponseEntity<TableData<DoSupplierPojo>> doListData(TableDataInput input, @RequestParam(required = false) String dateTimeRange, HttpSession session, HttpServletResponse response) {
		try {
			LOG.info("buyer Id :" + SecurityLibrary.getLoggedInUserTenantId() + " user id : " + SecurityLibrary.getLoggedInUser().getId());
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
			List<DoSupplierPojo> poList = deliveryOrderService.findAllSearchFilterDoForBuyer(SecurityLibrary.getLoggedInUserTenantId(), input, startDate, endDate);
			TableData<DoSupplierPojo> data = new TableData<DoSupplierPojo>(poList);
			data.setDraw(input.getDraw());
			long recordFiltered = deliveryOrderService.findTotalSearchFilterDoForBuyer(SecurityLibrary.getLoggedInUserTenantId(), input, startDate, endDate);
			long totalCount = deliveryOrderService.findTotalDoForBuyer(SecurityLibrary.getLoggedInUserTenantId());
			LOG.info(" totalCount : " + totalCount);
			data.setRecordsFiltered(recordFiltered);
			data.setRecordsTotal(totalCount);
			return new ResponseEntity<TableData<DoSupplierPojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching Do List For Buyer: " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching Do List For Buyer : " + e.getMessage());
			return new ResponseEntity<TableData<DoSupplierPojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/deliveryOrderView/{doId}", method = RequestMethod.GET)
	public String deliveryOrderView(@PathVariable String doId, Model model, HttpServletRequest request, RedirectAttributes redir) {
		LOG.info("Buyer View GET called By Do id :" + doId);
		try {
			constructDoSummaryAttributesForBuyerView(doId, model);
		} catch (Exception e) {
			LOG.info("Error in view DO For Supplier:" + e.getMessage(), e);
			model.addAttribute("error", messageSource.getMessage("error.view.do.supplier", new Object[] { e.getMessage() }, Global.LOCALE));
		}
		return "buyerDeliveryOrderView";
	}

	public DeliveryOrder constructDoSummaryAttributesForBuyerView(String doId, Model model) {
		DeliveryOrder deliveryOrder = deliveryOrderService.getDoByIdForSupplierView(doId);
		model.addAttribute("deliveryOrder", deliveryOrder);
		List<DeliveryOrderItem> doItemlist = deliveryOrderService.findAllDoItemByDoIdForSummary(doId);
		LOG.info("doItemlist" + doItemlist.size());
		model.addAttribute("doItemlist", doItemlist);
		List<DeliveryOrderAudit> doAuditList = doAuditService.getDoAuditForBuyerByDoId(deliveryOrder.getId());
		model.addAttribute("auditList", doAuditList);

		return deliveryOrder;
	}

	@RequestMapping(path = "/declineDo", method = RequestMethod.POST)
	public String declineDo(@RequestParam("doId") String doId, @RequestParam("buyerRemark") String buyerRemark, HttpServletResponse response, HttpSession session, RedirectAttributes redir) throws Exception {
		try {
			LOG.info("Buyer DO DECLINED :++++++++++++++++++++++++++++++++++ " + doId);
			DeliveryOrder deliveryOrder = deliveryOrderService.declineOrder(doId, SecurityLibrary.getLoggedInUser(), buyerRemark);
			if (deliveryOrder != null) {
				redir.addFlashAttribute("success", messageSource.getMessage("buyer.declined.success.do", new Object[] { deliveryOrder.getDeliveryId() }, Global.LOCALE));
			} else {
				redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.declining.do", new Object[] {}, Global.LOCALE));
			}
		} catch (Exception e) {
			redir.addFlashAttribute("error", messageSource.getMessage("buyer.declining.do.error", new Object[] { e.getMessage() }, Global.LOCALE));
			LOG.error("error while declining Do " + e.getMessage(), e);
		}
		return "redirect:deliveryOrderView/" + doId;
	}

	@RequestMapping(path = "/acceptDo", method = RequestMethod.POST)
	public String acceptDo(@RequestParam("doId") String doId, @RequestParam("buyerRemark") String buyerRemark, HttpServletResponse response, RedirectAttributes redir) throws Exception {
		try {
			LOG.info("Buyer DO ACCEPTED :++++++++++++++++++++++++++++++++++ " + doId);
			DeliveryOrder deliveryOrder = deliveryOrderService.acceptOrder(doId, SecurityLibrary.getLoggedInUser(), buyerRemark);
			if (deliveryOrder != null) {
				redir.addFlashAttribute("success", messageSource.getMessage("buyer.accepted.success.do", new Object[] { deliveryOrder.getDeliveryId() }, Global.LOCALE));
			} else {
				redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.accepting.do", new Object[] {}, Global.LOCALE));
			}
		} catch (Exception e) {
			redir.addFlashAttribute("error", messageSource.getMessage("buyer.accepting.do.error", new Object[] { e.getMessage() }, Global.LOCALE));
			LOG.error("error while accepting Do " + e.getMessage(), e);
		}
		return "redirect:deliveryOrderView/" + doId;
	}

	@RequestMapping(path = "/downloadDoReport/{doId}", method = RequestMethod.GET)
	public void downloadDoReport(@PathVariable("doId") String doId, HttpServletResponse response, HttpSession session) throws Exception {
		try {
			LOG.info(" Invoice REPORT : " + doId);
			String poFilename = "UnknownPO.pdf";
			DeliveryOrder deliveryOrder = deliveryOrderService.findByDoId(doId);

			DoReport reportObj = doReportDao.findReportByDoId(deliveryOrder.getId(), SecurityLibrary.getLoggedInUserTenantId());

			if (reportObj != null) {
				response.setContentType("application/pdf");
				response.setContentLength(reportObj.getFileData().length);
				response.setHeader("Content-Disposition", "attachment; filename=\"" + reportObj.getFileName() + "\"");
				FileCopyUtils.copy(reportObj.getFileData(), response.getOutputStream());
				response.flushBuffer();
				response.setStatus(HttpServletResponse.SC_OK);
			} else {
				if (deliveryOrder.getDeliveryId() != null) {
					poFilename = (deliveryOrder.getDeliveryId()).replace("/", "-") + ".pdf";
				}
				String filename = poFilename;

				JasperPrint jasperPrint = deliveryOrderService.getGeneratedBuyerDoPdf(deliveryOrder, (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY));
				if (jasperPrint != null) {
					response.setContentType("application/pdf");
					response.addHeader("Content-Disposition", "attachment; filename=" + filename);

					JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());

					byte[] outputFile = JasperExportManager.exportReportToPdf(jasperPrint);
					DoReport attach = new DoReport();
					attach.setFileData(outputFile);
					attach.setFileName(filename);
					attach.setDoNumber(deliveryOrder.getDeliveryId());
					attach.setTenantId(SecurityLibrary.getLoggedInUserTenantId());
					attach.setDeliveryOrder(deliveryOrder);
					doReportDao.saveOrUpdate(attach);

					response.getOutputStream().flush();
					response.getOutputStream().close();
				}
				try {
					DeliveryOrderAudit doAudit = new DeliveryOrderAudit();
					doAudit.setAction(DeliveryOrderAuditType.DOWNLOADED);
					doAudit.setActionBy(SecurityLibrary.getLoggedInUser());
					doAudit.setActionDate(new Date());
					doAudit.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
					if (deliveryOrder.getSupplier() != null) {
						doAudit.setSupplier(deliveryOrder.getSupplier());
					}
					doAudit.setDescription(messageSource.getMessage("do.audit.downloadDo", new Object[] { deliveryOrder.getDeliveryId() }, Global.LOCALE));
					doAudit.setVisibilityType(DoAuditVisibilityType.BUYER);
					doAudit.setDeliveryOrder(deliveryOrder);
					doAuditService.save(doAudit);
				} catch (Exception e) {
					LOG.error("Error while saving Do audit:" + e.getMessage(), e);
				}
				try {
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "DO '" +deliveryOrder.getDeliveryId()+ "' is successfully downloaded", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.DO);
					buyerAuditTrailDao.save(buyerAuditTrail);
				} catch (Exception e) {
					LOG.info("Error to create audit trails message");
				}
			}
		} catch (Exception e) {
			LOG.error("Could not generate Delivery Summary Report. " + e.getMessage(), e);
		}
	}

	@RequestMapping(path = "/ExportBuyerDoReport", method = RequestMethod.POST)
	public void downloadBuyerDoReport(HttpSession session, HttpServletRequest request, HttpServletResponse response, @ModelAttribute("doSupplierPojo") DoSupplierPojo doSupplierPojo, boolean select_all, @RequestParam String dateTimeRange) throws IOException {
		LOG.info("POJO-------- cont ---- " + doSupplierPojo);
		try {
			String doArr[] = null;
			if (StringUtils.checkString(doSupplierPojo.getDoIds()).length() > 0) {
				doArr = doSupplierPojo.getDoIds().split(",");
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
			String fileName = "Buyer_DO_Report.xlsx";
			Path file = Paths.get(downloadFolder, fileName);
			LOG.info("File Path ::" + file);

			buyerDoReportDownloader(workbook, doArr, session, doSupplierPojo, select_all, startDate, endDate);

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
			LOG.error("Error while downloading DO Report List :: " + e.getMessage(), e);
		}
	}

	private void buyerDoReportDownloader(XSSFWorkbook workbook, String[] doIds, HttpSession session, DoSupplierPojo doSupplierPojo, boolean select_all, Date startDate, Date endDate) {
		XSSFSheet sheet = workbook.createSheet("Buyer DO Report List");
		int r = 1;
		buildDoHeader(workbook, sheet);

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		SimpleDateFormat deliveryDate = new SimpleDateFormat("dd/MM/yyyy ");
		if (session != null && session.getAttribute(Global.SESSION_TIME_ZONE_KEY) != null) {
			sdf.setTimeZone(TimeZone.getTimeZone((String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY)));
			deliveryDate.setTimeZone(TimeZone.getTimeZone((String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY)));
		} else {
			sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
			deliveryDate.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));

		}

		List<DoSupplierPojo> doList = getSearchBuyerDoDetails(doIds, doSupplierPojo, select_all, startDate, endDate, sdf);

		if (CollectionUtil.isNotEmpty(doList)) {
			for (DoSupplierPojo doReport : doList) {

				DecimalFormat df = null;
				if (doReport.getDecimal().equals("1")) {
					df = new DecimalFormat("#,###,###,##0.0");
				} else if (doReport.getDecimal().equals("2")) {
					df = new DecimalFormat("#,###,###,##0.00");
				} else if (doReport.getDecimal().equals("3")) {
					df = new DecimalFormat("#,###,###,##0.000");
				} else if (doReport.getDecimal().equals("4")) {
					df = new DecimalFormat("#,###,###,##0.0000");
				} else if (doReport.getDecimal().equals("5")) {
					df = new DecimalFormat("#,###,###,##0.00000");
				} else if (doReport.getDecimal().equals("6")) {
					df = new DecimalFormat("#,###,###,##0.000000");
				} else {
					df = new DecimalFormat("#,###,###,##0.00");
				}

				Row row = sheet.createRow(r++);
				int cellNum = 0;

				row.createCell(cellNum++).setCellValue(doReport.getDeliveryId() != null ? doReport.getDeliveryId() : "");
				// row.createCell(cellNum++).setCellValue(doReport.getReferenceNumber() != null ?
				// doReport.getReferenceNumber() : "");
				row.createCell(cellNum++).setCellValue(doReport.getName() != null ? doReport.getName() : "");
				row.createCell(cellNum++).setCellValue(doReport.getPoNumber() != null ? doReport.getPoNumber() : "");
				row.createCell(cellNum++).setCellValue(doReport.getSupplierCompanyName() != null ? doReport.getSupplierCompanyName() : "");
				row.createCell(cellNum++).setCellValue(doReport.getBusinessUnit() != null ? doReport.getBusinessUnit() : "");
				// row.createCell(cellNum++).setCellValue(invoiceReport.getCreatedBy() != null ?
				// invoiceReport.getCreatedBy() : "");
				row.createCell(cellNum++).setCellValue(doReport.getSendDate() != null ? sdf.format(doReport.getSendDate()) : "");
				row.createCell(cellNum++).setCellValue(doReport.getActionDate() != null ? sdf.format(doReport.getActionDate()) : "");
				row.createCell(cellNum++).setCellValue(doReport.getCurrency() != null ? doReport.getCurrency() : "");

				Cell grandTotalCell = row.createCell(cellNum++);
				grandTotalCell.setCellValue(doReport.getGrandTotal() != null ? df.format(doReport.getGrandTotal()) : "");
				CellStyle gt = workbook.createCellStyle();
				gt.setAlignment(CellStyle.ALIGN_RIGHT);
				grandTotalCell.setCellStyle(gt);

				row.createCell(cellNum++).setCellValue(doReport.getStatus() != null ? doReport.getStatus().toString() : "");

			}
		}

		for (int k = 0; k < 10; k++) {
			sheet.autoSizeColumn(k, true);
		}

	}

	private void buildDoHeader(XSSFWorkbook workbook, XSSFSheet sheet) {
		Row rowHeading = sheet.createRow(0);
		// Style of Heading Cells
		CellStyle styleHeading = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		styleHeading.setFont(font);
		styleHeading.setAlignment(CellStyle.ALIGN_CENTER);

		int i = 0;
		Cell cell = null;
		for (String column : Global.ALL_BUYER_DO_REPORT_EXCEL_COLUMNS) {
			cell = rowHeading.createCell(i++);
			cell.setCellValue(column);
			cell.setCellStyle(styleHeading);
		}
	}

	private List<DoSupplierPojo> getSearchBuyerDoDetails(String[] doIds, DoSupplierPojo doSupplierPojo, boolean select_all, Date startDate, Date endDate, SimpleDateFormat sdf) {
		return deliveryOrderService.getAllBuyerDoDetailsForExcelReport(SecurityLibrary.getLoggedInUserTenantId(), doIds, doSupplierPojo, select_all, startDate, endDate, sdf);
	}

	@RequestMapping(path = "/ExportBuyerDoCsv", method = RequestMethod.POST)
	public void downloadBuyerDoCsv(HttpSession session, HttpServletRequest request, HttpServletResponse response, @ModelAttribute("doSupplierPojo") DoSupplierPojo doSupplierPojo, boolean select_all, @RequestParam String dateTimeRange) throws IOException {
		LOG.info("-----------doSupplierPojo --------------" + doSupplierPojo.toString());
		try {
			File file = File.createTempFile("Buyer_DO_Report", ".csv");
			LOG.info("Temp file : " + file.getAbsolutePath());

			String doIdArr[] = null;
			if (StringUtils.checkString(doSupplierPojo.getDoIds()).length() > 0) {
				doIdArr = doSupplierPojo.getDoIds().split(",");
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
			deliveryOrderService.downloadCsvFileForDoList(response, file, doIdArr, startDate, endDate, doSupplierPojo, select_all, SecurityLibrary.getLoggedInUserTenantId(), session);

			try {
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "DO Report is successfully downloaded", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.DO);
				buyerAuditTrailDao.save(buyerAuditTrail);
			} catch (Exception e) {
				LOG.info("Error to create audit trails message");
			}

			// Send to UI
			response.setContentType("text/csv");
			response.addHeader("Content-Disposition", "attachment; filename=Buyer_DO_Report.csv");
			response.setContentLengthLong(file.length());
			Files.copy(file.toPath(), response.getOutputStream());
			response.getOutputStream().flush();

		} catch (Exception e) {
			LOG.error("Error while downloading csv file : " + e.getMessage(), e);
		}
	}

}
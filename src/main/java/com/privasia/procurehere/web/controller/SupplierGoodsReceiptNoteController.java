package com.privasia.procurehere.web.controller;

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
import javax.validation.Validation;
import javax.validation.Validator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.privasia.procurehere.core.dao.GrnReportDao;
import com.privasia.procurehere.core.entity.GoodsReceiptNote;
import com.privasia.procurehere.core.entity.GoodsReceiptNoteAudit;
import com.privasia.procurehere.core.entity.GoodsReceiptNoteItem;
import com.privasia.procurehere.core.entity.GrnReport;
import com.privasia.procurehere.core.enums.GrnAuditType;
import com.privasia.procurehere.core.enums.GrnAuditVisibilityType;
import com.privasia.procurehere.core.enums.GrnStatus;
import com.privasia.procurehere.core.pojo.GoodsReceiptNotePojo;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.CurrencyService;
import com.privasia.procurehere.service.FooterService;
import com.privasia.procurehere.service.GoodsReceiptNoteAuditService;
import com.privasia.procurehere.service.GoodsReceiptNoteItemService;
import com.privasia.procurehere.service.GoodsReceiptNoteService;
import com.privasia.procurehere.service.PoService;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

@Controller
@RequestMapping("/supplier")
public class SupplierGoodsReceiptNoteController {

	private static final Logger LOG = LogManager.getLogger(Global.SUPPLIER_LOG);

	protected Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	@Autowired
	MessageSource messageSource;

	@Autowired
	GoodsReceiptNoteService goodsReceiptNoteService;

	@Autowired
	FooterService footerService;

	@Autowired
	ServletContext context;

	@Autowired
	GoodsReceiptNoteAuditService goodsReceiptNoteAuditService;

	@Autowired
	CurrencyService currencyService;

	@Autowired
	PoService poService;

	@Autowired
	GoodsReceiptNoteItemService goodsReceiptNoteItemService;

	@Autowired
	GrnReportDao grnReportDao;

	@ModelAttribute("grnStatusList")
	public List<GrnStatus> getGrnStatusList() {
		List<GrnStatus> grnStatusList = Arrays.asList(GrnStatus.DELIVERED, GrnStatus.ACCEPTED, GrnStatus.DECLINED, GrnStatus.CANCELLED);
		return grnStatusList;
	}

	@RequestMapping(path = "/supplierGrnList", method = RequestMethod.GET)
	public String getSupplierGrnList() {
		return "supplierGrnList";
	}

	@RequestMapping(path = "/supplierGrnListData", method = RequestMethod.GET)
	public ResponseEntity<TableData<GoodsReceiptNotePojo>> supplierGrnListData(TableDataInput input, @RequestParam(required = false) String dateTimeRange, HttpSession session, HttpServletResponse response) {
		try {
			LOG.info(" SUPPLIER Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort() + ", dateTimeRange :" + dateTimeRange);
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
				}
			}
			LOG.info(" SUPPLIER tenantId : " + SecurityLibrary.getLoggedInUserTenantId() + " startDate : " + startDate + ", endDate :" + endDate);
			//List<GoodsReceiptNotePojo> grnList = goodsReceiptNoteService.findAllSearchFilterGrnsForSupplier(SecurityLibrary.getLoggedInUserTenantId(), input, startDate, endDate);
			List<GoodsReceiptNotePojo> grnList = goodsReceiptNoteService.findAllSearchFilterGrnsSupp(SecurityLibrary.getLoggedInUserTenantId(), input, startDate, endDate);
			TableData<GoodsReceiptNotePojo> data = new TableData<GoodsReceiptNotePojo>(grnList);
			data.setDraw(input.getDraw());
			//long recordFiltered = goodsReceiptNoteService.findTotalSearchFilterGrnForSupplier(SecurityLibrary.getLoggedInUserTenantId(), input, startDate, endDate);
			long recordFiltered = goodsReceiptNoteService.findTotalSearchFilterGrnsSupp(SecurityLibrary.getLoggedInUserTenantId(), input, startDate, endDate);
			data.setRecordsFiltered(recordFiltered);
			data.setRecordsTotal(recordFiltered);
			return new ResponseEntity<TableData<GoodsReceiptNotePojo>>(data, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error fetching GRN list : " + e.getMessage(), e);
			HttpHeaders headers = new HttpHeaders();
			headers.add("error", "Error fetching GRN list : " + e.getMessage());
			return new ResponseEntity<TableData<GoodsReceiptNotePojo>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(path = "/supplierGrnView/{id}", method = RequestMethod.GET)
	public String supplierGrnView(@PathVariable(name = "id") String grnId, Model model) {
		GoodsReceiptNote goodsReceiptNote = goodsReceiptNoteService.loadGrnById(grnId);
		try {
			List<GoodsReceiptNoteItem> grnItemlist = goodsReceiptNoteItemService.findAllGrnItemsByGrnId(goodsReceiptNote.getId());
			model.addAttribute("grnItemlist", grnItemlist);
			List<GoodsReceiptNoteAudit> auditList = goodsReceiptNoteAuditService.getGrnAuditForSupplierByGrnId(grnId);
			model.addAttribute("auditList", auditList);
			model.addAttribute("goodsReceiptNote", goodsReceiptNote);
			return "supplierGrnView";
		} catch (Exception e) {
			LOG.error("Error while getting supplier grn view:" + e.getMessage(), e);
			List<GoodsReceiptNoteAudit> auditList = goodsReceiptNoteAuditService.getGrnAuditForSupplierByGrnId(grnId);
			model.addAttribute("auditList", auditList);
			model.addAttribute("goodsReceiptNote", goodsReceiptNote);
			List<GoodsReceiptNoteItem> grnItemlist = goodsReceiptNoteItemService.findAllGrnItemsByGrnId(goodsReceiptNote.getId());
			model.addAttribute("grnItemlist", grnItemlist);
			return "supplierGrnView";

		}
	}

	@RequestMapping(path = "/declineGrn", method = RequestMethod.POST)
	public String declineGrn(@RequestParam("grnId") String grnId, @RequestParam("supplierRemark") String supplierRemark, HttpServletResponse response, HttpSession session, RedirectAttributes redir) throws Exception {
		try {
			LOG.info("Supplier Grn DECLINED :++++++++++++++++++++++++++++++++++ " + grnId);
			GoodsReceiptNote goodsReceiptNote = goodsReceiptNoteService.declineGrn(grnId, SecurityLibrary.getLoggedInUser(), supplierRemark);
			if (goodsReceiptNote != null) {
				redir.addFlashAttribute("success", messageSource.getMessage("buyer.declined.success.grn", new Object[] { goodsReceiptNote.getGrnId() }, Global.LOCALE));
			} else {
				redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.declining.grn", new Object[] {}, Global.LOCALE));
			}
		} catch (Exception e) {
			redir.addFlashAttribute("error", messageSource.getMessage("buyer.declining.grn.error", new Object[] { e.getMessage() }, Global.LOCALE));
			LOG.error("error while declining GRN " + e.getMessage(), e);
		}
		return "redirect:/supplier/supplierGrnView/" + grnId;
	}

	@RequestMapping(path = "/acceptGrn", method = RequestMethod.POST)
	public String acceptGrn(@RequestParam("grnId") String grnId, @RequestParam("supplierRemark") String supplierRemark, HttpServletResponse response, RedirectAttributes redir) throws Exception {
		try {
			LOG.info("Buyer GRN ACCEPTED :++++++++++++++++++++++++++++++++++ " + grnId);
			GoodsReceiptNote goodsReceiptNote = goodsReceiptNoteService.acceptGrn(grnId, SecurityLibrary.getLoggedInUser(), supplierRemark);
			if (goodsReceiptNote != null) {
				redir.addFlashAttribute("success", messageSource.getMessage("buyer.accepted.success.grn", new Object[] { goodsReceiptNote.getGrnId() }, Global.LOCALE));
			} else {
				redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.accepting.grn", new Object[] {}, Global.LOCALE));
			}
		} catch (Exception e) {
			redir.addFlashAttribute("error", messageSource.getMessage("buyer.accepting.grn.error", new Object[] { e.getMessage() }, Global.LOCALE));
			LOG.error("error while accepting GRN " + e.getMessage(), e);
		}
		return "redirect:/supplier/supplierGrnView/" + grnId;
	}

	@RequestMapping(path = "/exportSupplierGrnReport", method = RequestMethod.POST)
	public void exportSupplierGrnReport(HttpSession session, HttpServletRequest request, HttpServletResponse response, @ModelAttribute("goodsReceiptNotePojo") GoodsReceiptNotePojo goodsReceiptNotePojo, boolean select_all, @RequestParam String dateTimeRange) throws IOException {
		try {
			String grnArr[] = null;
			if (StringUtils.checkString(goodsReceiptNotePojo.getGrnIds()).length() > 0) {
				grnArr = goodsReceiptNotePojo.getGrnIds().split(",");
				LOG.info("_----------Id " + goodsReceiptNotePojo.getGrnIds());
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
			String fileName = "Supplier_GRN_Report.xlsx";
			Path file = Paths.get(downloadFolder, fileName);
			LOG.info("File Path ::" + file);

			supplierGrnReportDownloader(workbook, grnArr, session, goodsReceiptNotePojo, select_all, startDate, endDate);

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

	private void supplierGrnReportDownloader(XSSFWorkbook workbook, String[] grnIds, HttpSession session, GoodsReceiptNotePojo grnSupplierPojo, boolean select_all, Date startDate, Date endDate) {
		XSSFSheet sheet = workbook.createSheet("Buyer GRN Report List");
		int r = 1;
		buildGrnHeader(workbook, sheet);

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
		SimpleDateFormat deliveryDate = new SimpleDateFormat("dd/MM/yyyy ");
		if (session != null && session.getAttribute(Global.SESSION_TIME_ZONE_KEY) != null) {
			sdf.setTimeZone(TimeZone.getTimeZone((String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY)));
			deliveryDate.setTimeZone(TimeZone.getTimeZone((String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY)));
		} else {
			sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
			deliveryDate.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));

		}

		List<GoodsReceiptNotePojo> grnList = getSearchSupplierGrnDetails(grnIds, grnSupplierPojo, select_all, startDate, endDate, sdf);

		if (CollectionUtil.isNotEmpty(grnList)) {
			for (GoodsReceiptNotePojo grnReport : grnList) {

				DecimalFormat df = null;
				if (grnReport.getDecimal().equals("1")) {
					df = new DecimalFormat("#,###,###,##0.0");
				} else if (grnReport.getDecimal().equals("2")) {
					df = new DecimalFormat("#,###,###,##0.00");
				} else if (grnReport.getDecimal().equals("3")) {
					df = new DecimalFormat("#,###,###,##0.000");
				} else if (grnReport.getDecimal().equals("4")) {
					df = new DecimalFormat("#,###,###,##0.0000");
				} else if (grnReport.getDecimal().equals("5")) {
					df = new DecimalFormat("#,###,###,##0.00000");
				} else if (grnReport.getDecimal().equals("6")) {
					df = new DecimalFormat("#,###,###,##0.000000");
				} else {
					df = new DecimalFormat("#,###,###,##0.00");
				}

				Row row = sheet.createRow(r++);
				int cellNum = 0;

				row.createCell(cellNum++).setCellValue(grnReport.getGrnId() != null ? grnReport.getGrnId() : "");
				row.createCell(cellNum++).setCellValue(grnReport.getGrnTitle() != null ? grnReport.getGrnTitle() : "");
				row.createCell(cellNum++).setCellValue(grnReport.getBuyer() != null ? grnReport.getBuyer() : "");
				row.createCell(cellNum++).setCellValue(grnReport.getCreatedDate() != null ? sdf.format(grnReport.getCreatedDate()) : "");
				row.createCell(cellNum++).setCellValue(grnReport.getActionDate() != null ? sdf.format(grnReport.getActionDate()) : "");
				row.createCell(cellNum++).setCellValue(grnReport.getCurrency() != null ? grnReport.getCurrency() : "");
				Cell grandTotalCell = row.createCell(cellNum++);
				grandTotalCell.setCellValue(grnReport.getGrandTotal() != null ? df.format(grnReport.getGrandTotal()) : "");
				CellStyle gt = workbook.createCellStyle();
				gt.setAlignment(CellStyle.ALIGN_RIGHT);
				grandTotalCell.setCellStyle(gt);
				row.createCell(cellNum++).setCellValue(grnReport.getStatus() != null ? grnReport.getStatus().toString() : "");

			}
		}

		for (int k = 0; k < 10; k++) {
			sheet.autoSizeColumn(k, true);
		}

	}

	private void buildGrnHeader(XSSFWorkbook workbook, XSSFSheet sheet) {
		Row rowHeading = sheet.createRow(0);
		// Style of Heading Cells
		CellStyle styleHeading = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		styleHeading.setFont(font);
		styleHeading.setAlignment(CellStyle.ALIGN_CENTER);

		int i = 0;
		Cell cell = null;
		for (String column : Global.ALL_SUPPLIER_GRN_REPORT_EXCEL_COLUMNS) {
			cell = rowHeading.createCell(i++);
			cell.setCellValue(column);
			cell.setCellStyle(styleHeading);
		}
	}

	private List<GoodsReceiptNotePojo> getSearchSupplierGrnDetails(String[] grnIds, GoodsReceiptNotePojo goodsReceiptNotePojo, boolean select_all, Date startDate, Date endDate, SimpleDateFormat sdf) {
		return goodsReceiptNoteService.getAllSupplierGrnDetailsForExcelReport(SecurityLibrary.getLoggedInUserTenantId(), grnIds, goodsReceiptNotePojo, select_all, startDate, endDate, sdf);
	}

	@RequestMapping(path = "/downloadGrnReport/{grnId}", method = RequestMethod.GET)
	public void downloadGrn(@PathVariable("grnId") String grnId, HttpServletResponse response, HttpSession session) throws Exception {
		try {
			LOG.info("Supplier GRN REPORT : " + grnId);
			String grnFilename = "UnknownGRN.pdf";
			GoodsReceiptNote goodsReceiptNote = goodsReceiptNoteService.findByGrnId(grnId);
			GrnReport reportObj = grnReportDao.findReportByGrnId(goodsReceiptNote.getId(), SecurityLibrary.getLoggedInUserTenantId());

			if (reportObj != null) {
				response.setContentType("application/pdf");
				response.setContentLength(reportObj.getFileData().length);
				response.setHeader("Content-Disposition", "attachment; filename=\"" + reportObj.getFileName() + "\"");
				FileCopyUtils.copy(reportObj.getFileData(), response.getOutputStream());
				response.flushBuffer();
				response.setStatus(HttpServletResponse.SC_OK);
			} else {
				if (goodsReceiptNote.getGrnId() != null) {
					grnFilename = (goodsReceiptNote.getGrnId()).replace("/", "-") + ".pdf";
				}
				String filename = grnFilename;

				JasperPrint jasperPrint = goodsReceiptNoteService.getGeneratedSupplierGrnPdf(goodsReceiptNote, (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY));
				if (jasperPrint != null) {
					response.setContentType("application/pdf");
					response.addHeader("Content-Disposition", "attachment; filename=" + filename);

					JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());

					byte[] outputFile = JasperExportManager.exportReportToPdf(jasperPrint);
					GrnReport attach = new GrnReport();
					attach.setFileData(outputFile);
					attach.setFileName(filename);
					attach.setGrnId(goodsReceiptNote.getGrnId());
					attach.setTenantId(SecurityLibrary.getLoggedInUserTenantId());
					attach.setGoodsReceiptNote(goodsReceiptNote);
					//grnReportDao.saveOrUpdate(attach);

					response.getOutputStream().flush();
					response.getOutputStream().close();
				}
				try {
					GoodsReceiptNoteAudit grnAudit = new GoodsReceiptNoteAudit();
					grnAudit.setAction(GrnAuditType.DOWNLOADED);
					grnAudit.setActionBy(SecurityLibrary.getLoggedInUser());
					grnAudit.setActionDate(new Date());
					if (goodsReceiptNote.getSupplier() != null) {
						grnAudit.setSupplier(goodsReceiptNote.getSupplier());
					}
					grnAudit.setBuyer(SecurityLibrary.getLoggedInUser().getBuyer());
					grnAudit.setDescription(messageSource.getMessage("grn.audit.downloadGrn", new Object[] { goodsReceiptNote.getGrnId() }, Global.LOCALE));
					grnAudit.setVisibilityType(GrnAuditVisibilityType.SUPPLIER);
					grnAudit.setGoodsReceiptNote(goodsReceiptNote);
					goodsReceiptNoteAuditService.save(grnAudit);
				} catch (Exception e) {
					LOG.error("Error while saving Do audit:" + e.getMessage(), e);
				}
			}
		} catch (Exception e) {
			LOG.error("Could not generate Goods Receipt Note Summary Report. " + e.getMessage(), e);
		}
	}

}
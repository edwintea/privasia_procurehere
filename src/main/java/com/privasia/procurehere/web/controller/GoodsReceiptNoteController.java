package com.privasia.procurehere.web.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.GrnReportDao;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.GoodsReceiptNote;
import com.privasia.procurehere.core.entity.GoodsReceiptNoteAudit;
import com.privasia.procurehere.core.entity.GoodsReceiptNoteItem;
import com.privasia.procurehere.core.entity.GrnReport;
import com.privasia.procurehere.core.entity.Po;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.GrnAuditType;
import com.privasia.procurehere.core.enums.GrnAuditVisibilityType;
import com.privasia.procurehere.core.enums.GrnStatus;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.pojo.GoodsReceiptNotePojo;
import com.privasia.procurehere.core.pojo.GoodsReceiptNoteSearchPojo;
import com.privasia.procurehere.core.pojo.TableData;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.DateUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.BuyerAddressService;
import com.privasia.procurehere.service.CurrencyService;
import com.privasia.procurehere.service.EventIdSettingsService;
import com.privasia.procurehere.service.FavoriteSupplierService;
import com.privasia.procurehere.service.FooterService;
import com.privasia.procurehere.service.GoodsReceiptNoteAuditService;
import com.privasia.procurehere.service.GoodsReceiptNoteItemService;
import com.privasia.procurehere.service.GoodsReceiptNoteService;
import com.privasia.procurehere.service.PoService;
import com.privasia.procurehere.service.ProductListMaintenanceService;
import com.privasia.procurehere.service.UomService;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

@Controller
@RequestMapping("/buyer")
public class GoodsReceiptNoteController {

	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

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
	BuyerAddressService buyerAddressService;

	@Autowired
	EventIdSettingsService eventIdSettingsService;

	@Autowired
	GoodsReceiptNoteAuditService goodsReceiptNoteAuditService;

	@Autowired
	CurrencyService currencyService;

	@Autowired
	PoService poService;

	@Autowired
	GoodsReceiptNoteItemService goodsReceiptNoteItemService;

	@Autowired
	FavoriteSupplierService favoriteSupplierService;

	@Autowired
	ProductListMaintenanceService productListMaintenanceService;

	@Autowired
	UomService uomService;

	@Autowired
	GrnReportDao grnReportDao;

	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@InitBinder
	public void initBinder(WebDataBinder binder, HttpSession session) {
		StringTrimmerEditor stringtrimmer = new StringTrimmerEditor(true);
		binder.registerCustomEditor(String.class, stringtrimmer);
	}

	@ModelAttribute("grnStatusList")
	public List<GrnStatus> getGrnStatusList() {
		List<GrnStatus> grnStatusList = Arrays.asList(GrnStatus.DRAFT, GrnStatus.RECEIVED, GrnStatus.CANCELLED);
		return grnStatusList;
	}

	@PostMapping("/createGrn")
	public String createGrn(@RequestParam("poId") String poId, RedirectAttributes redir) {
		try {
			LOG.info("Creating GRN manually for po Id :" + poId);
			GoodsReceiptNote goodsReceiptNote = null;
			if (StringUtils.checkString(poId).length() > 0) {
				Po po = poService.getLoadedPoById(poId);
				goodsReceiptNote = goodsReceiptNoteService.createGrnFromPo(SecurityLibrary.getLoggedInUser(), po);
				redir.addFlashAttribute("success", messageSource.getMessage("grn.create.success.message", new Object[] { goodsReceiptNote.getGrnId() }, Global.LOCALE));
			}
			return "redirect:grnView/" + goodsReceiptNote.getId();
		} catch (Exception e) {
			LOG.error("Error while creating GRN :" + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("grn.create.error.message", new Object[] { e.getMessage() }, Global.LOCALE));
			return "redirect:poView/" + poId;
		}
	}

	@PostMapping("/sendGrn")
	public String sendGrn(@RequestParam("grnId") String grnId, RedirectAttributes redir) throws Exception {
		try {
			LOG.info("Buyer GRN Send :++++++++++++++++++++++++++++++++++ " + grnId);
			GoodsReceiptNote goodsReceiptNote = goodsReceiptNoteService.sendGrn(grnId, SecurityLibrary.getLoggedInUser());
			if (goodsReceiptNote != null) {
				redir.addFlashAttribute("success", messageSource.getMessage("grn.sent.success.message", new Object[] { goodsReceiptNote.getGrnId() }, Global.LOCALE));
			} else {
				redir.addFlashAttribute("error", messageSource.getMessage("flasherror.while.sending.grn", new Object[] {}, Global.LOCALE));
			}
		} catch (Exception e) {
			redir.addFlashAttribute("error", messageSource.getMessage("grn.draft.error.message", new Object[] { e.getMessage() }, Global.LOCALE));
			LOG.error("error while sending GRN " + e.getMessage(), e);
		}
		return "redirect:/buyer/grnView/" + grnId;
	}

	@RequestMapping(path = "/grnListData", method = RequestMethod.GET)
	public ResponseEntity<TableData<GoodsReceiptNotePojo>> grnListData(TableDataInput input, @RequestParam(required = false) String dateTimeRange, HttpSession session, HttpServletResponse response) {
		try {
			LOG.info(" BUYER Start : " + input.getStart() + " Length : " + input.getLength() + " Sort : " + input.getSort() + ", dateTimeRange :" + dateTimeRange);
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
			List<GoodsReceiptNotePojo> grnList = goodsReceiptNoteService.findAllSearchFilterGrns(SecurityLibrary.getLoggedInUserTenantId(), input, startDate, endDate);
			TableData<GoodsReceiptNotePojo> data = new TableData<GoodsReceiptNotePojo>(grnList);
			data.setDraw(input.getDraw());
			long recordFiltered = goodsReceiptNoteService.findTotalSearchFilterGrns(SecurityLibrary.getLoggedInUserTenantId(), input, startDate, endDate);
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

	@RequestMapping(path = "/grnList", method = RequestMethod.GET)
	public String grnList() {
		return "grnList";
	}

	@RequestMapping(path = "/cancelGrn", method = RequestMethod.POST)
	public String cancelGrn(@RequestParam("grnId") String grnId, @RequestParam("buyerRemark") String buyerRemark, HttpServletResponse response, RedirectAttributes redir) throws Exception {
		try {
			LOG.info("Buyer Cacelling GRN: " + grnId);
			GoodsReceiptNote grn = goodsReceiptNoteService.cancelGrn(grnId, SecurityLibrary.getLoggedInUser(), buyerRemark);
			if (grn != null) {
				redir.addFlashAttribute("success", messageSource.getMessage("grn.cancel.success", new Object[] { grn.getGrnId() }, Global.LOCALE));
			} else {
				redir.addFlashAttribute("error", messageSource.getMessage("grn.cancel.error", new Object[] {}, Global.LOCALE));
			}
			try {
				LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CANCELLED, "GRN '" + grn.getGrnId() + "' successfully cancelled", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.GRN);
				buyerAuditTrailDao.save(buyerAuditTrail);
				LOG.info("--------------------------AFTER AUDIT---------------------------------------");
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
			}
		} catch (Exception e) {
			redir.addFlashAttribute("error", messageSource.getMessage("grn.cancel.error", new Object[] { e.getMessage() }, Global.LOCALE));
			LOG.error("error while cancel GRN " + e.getMessage(), e);
		}
		return "redirect:/buyer/grnView/" + grnId;
	}

	@RequestMapping(path = "/grnView/{id}", method = RequestMethod.GET)
	public String grnView(@PathVariable(name = "id") String grnId, Model model, HttpSession session) {
		GoodsReceiptNote goodsReceiptNote = goodsReceiptNoteService.loadGrnById(grnId);
		try {

			if (goodsReceiptNote.getGoodsReceiptDate() != null) {
				goodsReceiptNote.setGoodsReceiptTime(goodsReceiptNote.getGoodsReceiptDate());
			}

			List<GoodsReceiptNoteItem> grnItemlist = goodsReceiptNoteItemService.findAllGrnItemsByGrnId(goodsReceiptNote.getId());
			model.addAttribute("grnItemlist", grnItemlist);
			List<GoodsReceiptNoteAudit> auditList = goodsReceiptNoteAuditService.getGrnAuditForBuyerByGrnId(grnId);
			model.addAttribute("auditList", auditList);
			model.addAttribute("goodsReceiptNote", goodsReceiptNote);
		} catch (Exception e) {
			LOG.error("Error while getting  view:" + e.getMessage());
			List<GoodsReceiptNoteAudit> auditList = goodsReceiptNoteAuditService.getGrnAuditForBuyerByGrnId(grnId);
			model.addAttribute("auditList", auditList);
			model.addAttribute("goodsReceiptNote", goodsReceiptNote);
			List<GoodsReceiptNoteItem> grnItemlist = goodsReceiptNoteItemService.findAllGrnItemsByGrnId(goodsReceiptNote.getId());
			model.addAttribute("grnItemlist", grnItemlist);

		}
		return "grnBuyerView";

	}

	@RequestMapping(path = "/exportBuyerGrnReport", method = RequestMethod.POST)
	public void exportBuyerGrnReport(HttpSession session, HttpServletRequest request, HttpServletResponse response, @ModelAttribute("goodsReceiptNotePojo") GoodsReceiptNotePojo goodsReceiptNotePojo, boolean select_all, @RequestParam String dateTimeRange) throws IOException {
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
			String fileName = "Buyer_GRN_Report.xlsx";
			Path file = Paths.get(downloadFolder, fileName);
			LOG.info("File Path ::" + file);

			buyerGrnReportDownloader(workbook, grnArr, session, goodsReceiptNotePojo, select_all, startDate, endDate);

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

	private void buyerGrnReportDownloader(XSSFWorkbook workbook, String[] grnIds, HttpSession session, GoodsReceiptNotePojo grnSupplierPojo, boolean select_all, Date startDate, Date endDate) {
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

		List<GoodsReceiptNotePojo> grnList = getSearchBuyerGrnDetails(grnIds, grnSupplierPojo, select_all, startDate, endDate, sdf);

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

				row.createCell(cellNum++).setCellValue(StringUtils.checkString(grnReport.getGrnId()).length() > 0 ? grnReport.getGrnId() : "");
				row.createCell(cellNum++).setCellValue(StringUtils.checkString(grnReport.getGrnTitle()).length() > 0 ? grnReport.getGrnTitle() : "");
				row.createCell(cellNum++).setCellValue(StringUtils.checkString(grnReport.getReferenceNumber()).length() > 0 ? grnReport.getReferenceNumber() : "");
				row.createCell(cellNum++).setCellValue(StringUtils.checkString(grnReport.getPoNumber()).length() > 0 ? grnReport.getPoNumber() : "");
				row.createCell(cellNum++).setCellValue(StringUtils.checkString(grnReport.getSupplierCompanyName()).length() > 0 ? grnReport.getSupplierCompanyName() : "");
				row.createCell(cellNum++).setCellValue(StringUtils.checkString(grnReport.getBusinessUnit()).length() > 0 ? grnReport.getBusinessUnit() : "");
				row.createCell(cellNum++).setCellValue(StringUtils.checkString(grnReport.getCreatedBy()).length() > 0 ? grnReport.getCreatedBy() : "");
				row.createCell(cellNum++).setCellValue(grnReport.getCreatedDate() != null ? sdf.format(grnReport.getCreatedDate()) : "");
				row.createCell(cellNum++).setCellValue(grnReport.getGoodsReceiptDate() != null ? sdf.format(grnReport.getGoodsReceiptDate()) : "");
				row.createCell(cellNum++).setCellValue(grnReport.getGrnReceivedDate() != null ? sdf.format(grnReport.getGrnReceivedDate()) : "");
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
		for (String column : Global.ALL_BUYER_GRN_REPORT_EXCEL_COLUMNS) {
			cell = rowHeading.createCell(i++);
			cell.setCellValue(column);
			cell.setCellStyle(styleHeading);
		}
	}

	private List<GoodsReceiptNotePojo> getSearchBuyerGrnDetails(String[] grnIds, GoodsReceiptNotePojo goodsReceiptNotePojo, boolean select_all, Date startDate, Date endDate, SimpleDateFormat sdf) {
		return goodsReceiptNoteService.getAllBuyerGrnDetailsForExcelReport(SecurityLibrary.getLoggedInUserTenantId(), grnIds, goodsReceiptNotePojo, select_all, startDate, endDate, sdf);
	}

	@RequestMapping(path = "/downloadGrn/{grnId}", method = RequestMethod.GET)
	public void downloadGrn(@PathVariable("grnId") String grnId, HttpServletResponse response, HttpSession session) throws Exception {
		try {
			LOG.info("Buyer GRN REPORT : " + grnId);
			String poFilename = "UnknownGRN.pdf";
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
					poFilename = (goodsReceiptNote.getGrnId()).replace("/", "-") + ".pdf";
				}
				String filename = poFilename;

				JasperPrint jasperPrint = goodsReceiptNoteService.getGeneratedBuyerGrnPdf(goodsReceiptNote, (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY));
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
					// grnReportDao.saveOrUpdate(attach);

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
					grnAudit.setVisibilityType(GrnAuditVisibilityType.BUYER);
					grnAudit.setGoodsReceiptNote(goodsReceiptNote);
					goodsReceiptNoteAuditService.save(grnAudit);
				} catch (Exception e) {
					LOG.error("Error while saving Do audit:" + e.getMessage(), e);
				}
				try {
					LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
					BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "GRN '" + goodsReceiptNote.getGrnId() + "' successfully downloaded", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.GRN);
					buyerAuditTrailDao.save(buyerAuditTrail);
					LOG.info("--------------------------AFTER AUDIT---------------------------------------");
				} catch (Exception e) {
					LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
				}
			}
		} catch (Exception e) {
			LOG.error("Could not generate Goods Receipt Note Summary Report. " + e.getMessage(), e);
		}
	}

	@RequestMapping(path = "/saveGrnDraft", method = RequestMethod.POST)
	public String saveGrnDraft(@RequestParam(name = "id") String grnId, //
			@RequestParam(name = "grnName") String grnName, //
			@RequestParam(name = "referenceNumber", required = false) String referenceNumber, //
			@RequestParam(name = "poId") String poId, //
			@RequestParam(name = "itemId", required = false) String[] itemId, //
			@RequestParam(name = "receivedQuantity", required = false) String[] receivedQuantity, //
			@RequestParam(name = "parentId", required = false) String[] parentId, //
			@RequestParam(name = "deliveryAddressTitle", required = false) String deliveryAddressTitle, //
			@RequestParam(name = "deliveryAddressLine1", required = false) String deliveryAddressLine1, //
			@RequestParam(name = "deliveryAddressLine2", required = false) String deliveryAddressLine2, //
			@RequestParam(name = "deliveryAddressCity", required = false) String deliveryAddressCity, //
			@RequestParam(name = "deliveryAddressState", required = false) String deliveryAddressState, //
			@RequestParam(name = "deliveryAddressZip", required = false) String deliveryAddressZip, //
			@RequestParam(name = "deliveryAddressCountry", required = false) String deliveryAddressCountry, //
			@RequestParam(name = "goodsReceiptDate", required = false) String goodsReceiptDate, //
			@RequestParam(name = "goodsReceiptTime", required = false) String goodsReceiptTime, //
			@RequestParam(name = "deliveryReceiver", required = false) String deliveryReceiver, //
			Model model, RedirectAttributes redir, HttpSession session) {

		if (itemId == null || (itemId != null && itemId.length == 0)) {
			redir.addFlashAttribute("error", messageSource.getMessage("grn.items.validation", new Object[] {}, Global.LOCALE));
			return "redirect:/buyer/grnView/" + grnId;
		}
		LOG.info("Saving grn draft:" + receivedQuantity);
		return updateGrnDetails(grnId, itemId, receivedQuantity, parentId, grnName, referenceNumber, model, redir, SecurityLibrary.getLoggedInUser(), session, deliveryAddressTitle, deliveryAddressLine1, deliveryAddressLine2, deliveryAddressCity, deliveryAddressState, deliveryAddressZip, deliveryAddressCountry, GrnStatus.DRAFT, goodsReceiptDate, goodsReceiptTime, deliveryReceiver);
	}

	@RequestMapping(path = "/receivedGrn", method = RequestMethod.POST)
	public String receivedGrn(@RequestParam(name = "id") String grnId, //
			@RequestParam(name = "grnName") String grnName, //
			@RequestParam(name = "referenceNumber", required = false) String referenceNumber, //
			@RequestParam(name = "poId") String poId, //
			@RequestParam(name = "itemId", required = false) String[] itemId, //
			@RequestParam(name = "receivedQuantity", required = false) String[] receivedQuantity, //
			@RequestParam(name = "parentId", required = false) String[] parentId, //
			@RequestParam(name = "deliveryAddressTitle", required = false) String deliveryAddressTitle, //
			@RequestParam(name = "deliveryAddressLine1", required = false) String deliveryAddressLine1, //
			@RequestParam(name = "deliveryAddressLine2", required = false) String deliveryAddressLine2, //
			@RequestParam(name = "deliveryAddressCity", required = false) String deliveryAddressCity, //
			@RequestParam(name = "deliveryAddressState", required = false) String deliveryAddressState, //
			@RequestParam(name = "deliveryAddressZip", required = false) String deliveryAddressZip, //
			@RequestParam(name = "deliveryAddressCountry", required = false) String deliveryAddressCountry, //
			@RequestParam(name = "goodsReceiptDate", required = false) String goodsReceiptDate, //
			@RequestParam(name = "goodsReceiptTime", required = false) String goodsReceiptTime, //
			@RequestParam(name = "deliveryReceiver", required = false) String deliveryReceiver, //

			Model model, RedirectAttributes redir, HttpSession session) {

		if (itemId == null || (itemId != null && itemId.length == 0)) {
			redir.addFlashAttribute("error", messageSource.getMessage("grn.items.validation", new Object[] {}, Global.LOCALE));
			return "redirect:/buyer/grnView/" + grnId;
		}
		LOG.info("Updating grn received:" + receivedQuantity);
		return updateGrnDetails(grnId, itemId, receivedQuantity, parentId, grnName, referenceNumber, model, redir, SecurityLibrary.getLoggedInUser(), session, deliveryAddressTitle, deliveryAddressLine1, deliveryAddressLine2, deliveryAddressCity, deliveryAddressState, deliveryAddressZip, deliveryAddressCountry, GrnStatus.RECEIVED, goodsReceiptDate, goodsReceiptTime, deliveryReceiver);
	}

	private String updateGrnDetails(String grnId, String[] itemId, String[] receivedQuantity, String parentId[], String grnName, String referenceNumber, Model model, RedirectAttributes redir, User loggedInUser, HttpSession session, String deliveryAddressTitle, String deliveryAddressLine1, String deliveryAddressLine2, String deliveryAddressCity, String deliveryAddressState, String deliveryAddressZip, String deliveryAddressCountry, GrnStatus status, String goodsReceiptDate, String goodsReceiptTime, String deliveryReceiver) {
		LOG.info("Updating grn");
		List<GoodsReceiptNoteItem> itemList = new ArrayList<GoodsReceiptNoteItem>();
		List<GoodsReceiptNoteItem> errorList = new ArrayList<GoodsReceiptNoteItem>();
		GoodsReceiptNote goodsReceiptNote = null;
		Date receiptDate = null;
		try {

			int index = 0;
			Boolean quantityError = Boolean.FALSE;
			for (String id : itemId) {
				LOG.info("ID : " + id);
				if (StringUtils.checkString(id).length() > 0) {
					GoodsReceiptNoteItem grnItem = goodsReceiptNoteItemService.findById(id);

					goodsReceiptNote = grnItem.getGoodsReceiptNote();
					if (grnItem.getParent() != null) {
						if (receivedQuantity.length > 0) {
							grnItem.setReceivedQuantity(new BigDecimal(receivedQuantity != null && StringUtils.checkString(receivedQuantity[index]).length() > 0 ? receivedQuantity[index].replaceAll(",", "") : "0").setScale(goodsReceiptNote.getDecimal(), BigDecimal.ROUND_DOWN));
						} else {
							grnItem.setReceivedQuantity(new BigDecimal("0").setScale(goodsReceiptNote.getDecimal(), BigDecimal.ROUND_DOWN));
						}
						BigDecimal previousReceivedQuantity = goodsReceiptNoteItemService.findReceivedQuantitiesByPoAndPoItemId(grnItem.getPo().getId(), grnItem.getPoItem().getId());
						grnItem.setPreviousReceivedQuantity(previousReceivedQuantity != null ? previousReceivedQuantity : BigDecimal.ZERO);
						BigDecimal receivedQuantityRequired = BigDecimal.ZERO;
						if (previousReceivedQuantity != null) {
							receivedQuantityRequired = grnItem.getQuantity().subtract(previousReceivedQuantity);

						}
						if ((previousReceivedQuantity != null && grnItem.getReceivedQuantity().compareTo(receivedQuantityRequired) == 1) || (previousReceivedQuantity == null && grnItem.getReceivedQuantity().compareTo(grnItem.getQuantity()) == 1)) {
							LOG.info("Item:" + grnItem.getItemName());
							quantityError = Boolean.TRUE;
						}

						itemList.add(grnItem);
					}
					if (parentId != null && parentId.length > 0) {
						GoodsReceiptNoteItem paItem = goodsReceiptNoteItemService.findById(parentId[index]);
						if (!errorList.contains(paItem)) {
							errorList.add(paItem); // This is for error
						}
					}
					errorList.add(grnItem); // This is for error
				}
				index++;
			}

			if (goodsReceiptDate != null && goodsReceiptTime != null) {
				TimeZone timeZone = TimeZone.getDefault();
				String strTimeZone = (String) session.getAttribute(Global.SESSION_TIME_ZONE_KEY);
				if (StringUtils.checkString(strTimeZone).length() > 0) {
					timeZone = TimeZone.getTimeZone(strTimeZone);
				}
				Date grnDate = null;
				Date grnTime = null;
				try {
					SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
					timeFormat.setTimeZone(timeZone);
					SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
					format.setTimeZone(timeZone);
					grnDate = format.parse(goodsReceiptDate);
					grnTime = timeFormat.parse(goodsReceiptTime);
				} catch (Exception e) {
					LOG.error("Error while parsing date:" + e.getMessage(), e);
				}
				receiptDate = DateUtil.combineDateTime(grnDate, grnTime, timeZone);
				if (receiptDate.after(new Date()) && status == GrnStatus.RECEIVED) {
					throw new ApplicationException(messageSource.getMessage("grn.receiptdate.error", new Object[] {}, Global.LOCALE));
				}
			}
			if (Boolean.TRUE == quantityError) {
				GoodsReceiptNote goodsReceiptObj = goodsReceiptNoteService.loadGrnById(grnId);
				goodsReceiptObj.setGrnTitle(grnName);
				goodsReceiptObj.setReferenceNumber(referenceNumber);
				goodsReceiptObj.setDeliveryReceiver(deliveryReceiver);
				goodsReceiptObj.setDeliveryAddressLine1(deliveryAddressLine1);
				goodsReceiptObj.setDeliveryAddressLine2(deliveryAddressLine2);
				goodsReceiptObj.setDeliveryAddressCity(deliveryAddressCity);
				goodsReceiptObj.setDeliveryAddressState(deliveryAddressState);
				goodsReceiptObj.setDeliveryAddressZip(deliveryAddressZip);
				goodsReceiptObj.setDeliveryAddressCountry(deliveryAddressCountry);

				if (receiptDate != null) {
					goodsReceiptObj.setGoodsReceiptDate(receiptDate);
					goodsReceiptObj.setGoodsReceiptTime(receiptDate);
				}
				model.addAttribute("grnItemlist", errorList);
				List<GoodsReceiptNoteAudit> auditList = goodsReceiptNoteAuditService.getGrnAuditForBuyerByGrnId(grnId);
				model.addAttribute("auditList", auditList);
				model.addAttribute("goodsReceiptNote", goodsReceiptObj);
				model.addAttribute("error", messageSource.getMessage("grn.received.quantity.validation", new Object[] {}, Global.LOCALE));
				return "grnBuyerView";
			}

			GoodsReceiptNote goodsReceiptNoteObj = goodsReceiptNoteService.updateGrnDetails(itemList, grnName, referenceNumber, loggedInUser, deliveryAddressTitle, deliveryAddressLine1, deliveryAddressLine2, deliveryAddressCity, deliveryAddressState, deliveryAddressZip, deliveryAddressCountry, status, session, deliveryReceiver, receiptDate);
			if (status == GrnStatus.RECEIVED) {
				redir.addFlashAttribute("success", messageSource.getMessage("grn.received.success.message", new Object[] { goodsReceiptNoteObj.getGrnId() }, Global.LOCALE));
			} else {
				redir.addFlashAttribute("success", messageSource.getMessage("grn.item.update", new Object[] {}, Global.LOCALE));
			}

		} catch (Exception e) {
			LOG.error("Error while updating grn details:" + e.getMessage(), e);
			GoodsReceiptNote goodsReceiptObj = goodsReceiptNoteService.loadGrnById(grnId);
			goodsReceiptObj.setGrnTitle(grnName);
			goodsReceiptObj.setReferenceNumber(referenceNumber);
			goodsReceiptObj.setDeliveryReceiver(deliveryReceiver);
			goodsReceiptObj.setDeliveryAddressLine1(deliveryAddressLine1);
			goodsReceiptObj.setDeliveryAddressLine2(deliveryAddressLine2);
			goodsReceiptObj.setDeliveryAddressCity(deliveryAddressCity);
			goodsReceiptObj.setDeliveryAddressState(deliveryAddressState);
			goodsReceiptObj.setDeliveryAddressZip(deliveryAddressZip);
			goodsReceiptObj.setDeliveryAddressCountry(deliveryAddressCountry);
			if (receiptDate != null) {
				goodsReceiptObj.setGoodsReceiptDate(receiptDate);
				goodsReceiptObj.setGoodsReceiptTime(receiptDate);
			}
			model.addAttribute("grnItemlist", errorList);
			List<GoodsReceiptNoteAudit> auditList = goodsReceiptNoteAuditService.getGrnAuditForBuyerByGrnId(grnId);
			model.addAttribute("auditList", auditList);
			model.addAttribute("goodsReceiptNote", goodsReceiptObj);
			model.addAttribute("error", e.getMessage());

			return "grnBuyerView";

		}
		return "redirect:/buyer/grnView/" + grnId;

	}

	@RequestMapping(path = "/exportGrnCsv", method = RequestMethod.GET)
	public String exportCsvFile(HttpSession session, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redir, @ModelAttribute("searchFilterGrnPojo") GoodsReceiptNoteSearchPojo goodsReceiptNotePojo, boolean select_all, @RequestParam String dateTimeRange) throws IOException {

		try {

			String grnArr[] = null;
			if (StringUtils.checkString(goodsReceiptNotePojo.getGrnIds()).length() > 0) {
				grnArr = goodsReceiptNotePojo.getGrnIds().split(",");
				LOG.info("_----------Id " + goodsReceiptNotePojo.getGrnIds());
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
			File file = File.createTempFile("Buyer_GRN_Report", ".csv");

			goodsReceiptNoteService.downloadCsvFileForGrnList(response, file, SecurityLibrary.getLoggedInUserTenantId(), goodsReceiptNotePojo, select_all, startDate, endDate, grnArr, formatter);

			try {
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DOWNLOAD, "GRN Report is successfully downloaded", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.GRN);
				buyerAuditTrailDao.save(buyerAuditTrail);
			} catch (Exception e) {
				LOG.info("Error to create audit trails message");
			}
			// Send to UI
			response.setContentType("text/csv");
			response.addHeader("Content-Disposition", "attachment; filename=Buyer_GRN_Report.csv");
			response.setContentLengthLong(file.length());
			Files.copy(file.toPath(), response.getOutputStream());
			response.getOutputStream().flush();

		} catch (Exception e) {
			LOG.error("Error while downloading csv file : " + e.getMessage(), e);
			redir.addFlashAttribute("error", messageSource.getMessage("grn.error.download.csv", new Object[] { e.getMessage() }, Global.LOCALE));
			return "redirect:grnList";
		}
		return "redirect:grnList";
	}

}
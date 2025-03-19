/**
 * 
 */
package com.privasia.procurehere.web.controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import com.privasia.procurehere.service.RfaSorEvaluationCommentsService;
import com.privasia.procurehere.service.RfaSorService;
import com.privasia.procurehere.service.RfaSupplierSorItemService;
import com.privasia.procurehere.service.RfiSorEvaluationCommentsService;
import com.privasia.procurehere.service.RfiSorService;
import com.privasia.procurehere.service.RfiSupplierSorItemService;
import com.privasia.procurehere.service.RfpSorEvaluationCommentsService;
import com.privasia.procurehere.service.RfpSorService;
import com.privasia.procurehere.service.RfpSupplierSorItemService;
import com.privasia.procurehere.service.RfqSorEvaluationCommentsService;
import com.privasia.procurehere.service.RfqSorService;
import com.privasia.procurehere.service.RfqSupplierSorItemService;
import com.privasia.procurehere.service.RftSorEvaluationCommentsService;
import com.privasia.procurehere.service.RftSorService;
import com.privasia.procurehere.service.RftSupplierSorItemService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.privasia.procurehere.core.entity.Envelop;
import com.privasia.procurehere.core.entity.Event;
import com.privasia.procurehere.core.entity.OwnerSettings;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.pojo.EventEvaluationPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.MaskUtils;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.OwnerSettingsService;
import com.privasia.procurehere.service.RfaBqEvaluationCommentsService;
import com.privasia.procurehere.service.RfaBqService;
import com.privasia.procurehere.service.RfaBqTotalEvaluationCommentsService;
import com.privasia.procurehere.service.RfaCqEvaluationCommentsService;
import com.privasia.procurehere.service.RfaCqService;
import com.privasia.procurehere.service.RfaEnvelopService;
import com.privasia.procurehere.service.RfaEventService;
import com.privasia.procurehere.service.RfaEventSupplierService;
import com.privasia.procurehere.service.RfaSupplierBqItemService;
import com.privasia.procurehere.service.RfaSupplierBqService;
import com.privasia.procurehere.service.RfaSupplierCqItemService;
import com.privasia.procurehere.service.RfiCqEvaluationCommentsService;
import com.privasia.procurehere.service.RfiCqService;
import com.privasia.procurehere.service.RfiEnvelopService;
import com.privasia.procurehere.service.RfiEventService;
import com.privasia.procurehere.service.RfiEventSupplierService;
import com.privasia.procurehere.service.RfiSupplierCqItemService;
import com.privasia.procurehere.service.RfpBqEvaluationCommentsService;
import com.privasia.procurehere.service.RfpBqService;
import com.privasia.procurehere.service.RfpBqTotalEvaluationCommentsService;
import com.privasia.procurehere.service.RfpCqEvaluationCommentsService;
import com.privasia.procurehere.service.RfpCqService;
import com.privasia.procurehere.service.RfpEnvelopService;
import com.privasia.procurehere.service.RfpEventService;
import com.privasia.procurehere.service.RfpEventSupplierService;
import com.privasia.procurehere.service.RfpSupplierBqItemService;
import com.privasia.procurehere.service.RfpSupplierBqService;
import com.privasia.procurehere.service.RfpSupplierCqItemService;
import com.privasia.procurehere.service.RfqBqEvaluationCommentsService;
import com.privasia.procurehere.service.RfqBqService;
import com.privasia.procurehere.service.RfqBqTotalEvaluationCommentsService;
import com.privasia.procurehere.service.RfqCqEvaluationCommentsService;
import com.privasia.procurehere.service.RfqCqService;
import com.privasia.procurehere.service.RfqEnvelopService;
import com.privasia.procurehere.service.RfqEventService;
import com.privasia.procurehere.service.RfqEventSupplierService;
import com.privasia.procurehere.service.RfqSupplierBqItemService;
import com.privasia.procurehere.service.RfqSupplierBqService;
import com.privasia.procurehere.service.RfqSupplierCqItemService;
import com.privasia.procurehere.service.RftBqEvaluationCommentsService;
import com.privasia.procurehere.service.RftBqService;
import com.privasia.procurehere.service.RftBqTotalEvaluationCommentsService;
import com.privasia.procurehere.service.RftCqEvaluationCommentsService;
import com.privasia.procurehere.service.RftCqService;
import com.privasia.procurehere.service.RftEnvelopService;
import com.privasia.procurehere.service.RftEventService;
import com.privasia.procurehere.service.RftEventSupplierService;
import com.privasia.procurehere.service.RftSupplierBqItemService;
import com.privasia.procurehere.service.RftSupplierBqService;
import com.privasia.procurehere.service.RftSupplierCqItemService;
import com.privasia.procurehere.service.supplier.SupplierService;

/**
 * @author Ravi
 */
public class EventEvaluationBase implements Serializable {

	private static final long serialVersionUID = -534046077138870559L;

	protected static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@Autowired
	RfaSupplierBqService rfaSupplierBqService;

	@Autowired
	RfpSupplierBqService rfpSupplierBqService;

	@Autowired
	RfqSupplierBqService rfqSupplierBqService;

	@Autowired
	RftSupplierBqService rftSupplierBqService;

	@Resource
	MessageSource messageSource;

	@Autowired
	SupplierService supplierService;

	@Autowired
	RftSupplierCqItemService rftSupplierCqItemService;

	@Autowired
	RfpSupplierCqItemService rfpSupplierCqItemService;

	@Autowired
	RfqSupplierCqItemService rfqSupplierCqItemService;

	@Autowired
	RfiSupplierCqItemService rfiSupplierCqItemService;

	@Autowired
	RfaSupplierCqItemService rfaSupplierCqItemService;

	@Autowired
	RfaSupplierBqItemService rfaSupplierBqItemService;

	@Autowired
	RftSupplierBqItemService rftSupplierBqItemService;

	@Autowired
	RfpSupplierBqItemService rfpSupplierBqItemService;

	@Autowired
	RfqSupplierBqItemService rfqSupplierBqItemService;

	@Autowired
	RfqSupplierSorItemService rfqSupplierSorItemService;

	@Autowired
	RfpSupplierSorItemService rfpSupplierSorItemService;

	@Autowired
	RfaSupplierSorItemService rfaSupplierSorItemService;

	@Autowired
	RftSupplierSorItemService rftSupplierSorItemService;

	@Autowired
    RfiSupplierSorItemService rfiSupplierSorItemService;

	@Autowired
	RftEventService rftEventService;

	@Autowired
	RfpEventService rfpEventService;

	@Autowired
	RfqEventService rfqEventService;

	@Autowired
	RfiEventService rfiEventService;

	@Autowired
	RfaEventService rfaEventService;

	@Autowired
	RftEnvelopService rftEnvelopService;

	@Autowired
	RfpEnvelopService rfpEnvelopService;

	@Autowired
	RfqEnvelopService rfqEnvelopService;

	@Autowired
	RfiEnvelopService rfiEnvelopService;

	@Autowired
	RfaEnvelopService rfaEnvelopService;

	@Autowired
	ServletContext context;

	@Autowired
	RftCqEvaluationCommentsService rftCqEvaluationCommentsService;

	@Autowired
	RfpCqEvaluationCommentsService rfpCqEvaluationCommentsService;

	@Autowired
	RfqCqEvaluationCommentsService rfqCqEvaluationCommentsService;

	@Autowired
	RfiCqEvaluationCommentsService rfiCqEvaluationCommentsService;

	@Autowired
	RfaCqEvaluationCommentsService rfaCqEvaluationCommentsService;

	@Autowired
	RftCqService rftCqService;

	@Autowired
	RfpCqService rfpCqService;

	@Autowired
	RfqCqService rfqCqService;

	@Autowired
	RfiCqService rfiCqService;

	@Autowired
	RfaCqService rfaCqService;

	@Autowired
	RfaBqEvaluationCommentsService rfaBqEvaluationCommentsService;

	@Autowired
	RfpBqEvaluationCommentsService rfpBqEvaluationCommentsService;

	@Autowired
	RfqBqEvaluationCommentsService rfqBqEvaluationCommentsService;

	@Autowired
	RftBqEvaluationCommentsService rftBqEvaluationCommentsService;

	@Autowired
	RfaSorEvaluationCommentsService rfaSorEvaluationCommentsService;

	@Autowired
	RfiSorEvaluationCommentsService rfiSorEvaluationCommentsService;

	@Autowired
	RfpSorEvaluationCommentsService rfpSorEvaluationCommentsService;

	@Autowired
	RfqSorEvaluationCommentsService rfqSorEvaluationCommentsService;

	@Autowired
	RftSorEvaluationCommentsService rftSorEvaluationCommentsService;

	@Autowired
	RfaBqService rfaBqService;

	@Autowired
	RfpBqService rfpBqService;

	@Autowired
	RfqBqService rfqBqService;

	@Autowired
	RftBqService rftBqService;

	@Autowired
	RftEventSupplierService rftEventSupplierService;

	@Autowired
	RfpEventSupplierService rfpEventSupplierService;

	@Autowired
	RfqEventSupplierService rfqEventSupplierService;

	@Autowired
	RfaEventSupplierService rfaEventSupplierService;

	@Autowired
	RfiEventSupplierService rfiEventSupplierService;

	@Autowired
	ApplicationContext applicationContext;

	@Autowired
	RftBqTotalEvaluationCommentsService rftBqTotalEvaluationCommentsService;

	@Autowired
	RfaBqTotalEvaluationCommentsService rfaBqTotalEvaluationCommentsService;

	@Autowired
	RfpBqTotalEvaluationCommentsService rfpBqTotalEvaluationCommentsService;

	@Autowired
	RfqBqTotalEvaluationCommentsService rfqBqTotalEvaluationCommentsService;

	@Autowired
	OwnerSettingsService ownerSettingsService;

	@Autowired
	RfaSorService rfaSorService;

	@Autowired
	RfpSorService rfpSorService;

	@Autowired
	RfqSorService rfqSorService;

	@Autowired
	RftSorService rftSorService;

	@Autowired
	RfiSorService rfiSorService;

	@ModelAttribute("ownerSettings")
	public OwnerSettings getOwnersettings() {
		return ownerSettingsService.getOwnersettings();
	}

	protected void buildCqComparisionFile(XSSFWorkbook workbook, List<EventEvaluationPojo> list, HttpServletResponse response) throws IOException {
		if (CollectionUtil.isNotEmpty(list)) {
			for (EventEvaluationPojo evaluationPojo : list) {
				XSSFSheet sheet = workbook.createSheet();
				Row rowHeading = sheet.createRow(0);
				CellStyle styleHeading = workbook.createCellStyle();
				Font font = workbook.createFont();
				font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
				styleHeading.setFont(font);
				// styleHeading.setVerticalAlignment(CellStyle.ALIGN_CENTER);
				styleHeading.setAlignment(CellStyle.ALIGN_CENTER);
				int r = 1;

				int i = 3;
				int cellMerge = 2;
				for (Supplier column : evaluationPojo.getColumns()) {
					cellMerge++;
					Cell cell = rowHeading.createCell(i++);
					cell.setCellValue(column.getCompanyName());
					cell.setCellStyle(styleHeading);
					cell = rowHeading.createCell(i++);
					cell.setCellStyle(styleHeading);
					sheet.addMergedRegion(new CellRangeAddress(0, 0, cellMerge, ++cellMerge));
				}

				i = 0;
				Row row = sheet.createRow(r++);
				Cell cell = row.createCell(i++);
				cell.setCellValue("No");
				// cell.setCellStyle(styleHeading);

				cell = row.createCell(i++);
				cell.setCellValue("Question");
				// cell.setCellStyle(styleHeading);

				cell = row.createCell(i++);
				cell.setCellValue("Scoring Scale");
				// cell.setCellStyle(styleHeading);

				List<Supplier> columns = evaluationPojo.getColumns();
				for (int j = 0; j < columns.size(); j++) {
					cell = row.createCell(i++);
					cell.setCellValue("Answer");
					cell.getCellStyle().setWrapText(true);
					// cell.setCellStyle(styleHeading);
					cell = row.createCell(i++);
					cell.setCellValue("Score");
					// cell.setCellStyle(styleHeading);
				}

				for (List<String> data : evaluationPojo.getData()) {
					row = sheet.createRow(r++);
					int cellNum = 0;
					for (String answers : data) {
						Cell anserwCell = row.createCell(cellNum++);
						anserwCell.setCellValue(answers);
						anserwCell.getCellStyle().setWrapText(true);
					}
				}

				r++;
				int cellNum = 1;
				Row totalRow = sheet.createRow(r++);
				cell = totalRow.createCell(cellNum++);
				cell.setCellValue("Total Scoring");
				cell.getCellStyle().setAlignment(CellStyle.ALIGN_CENTER);
				for (String score : evaluationPojo.getScoring()) {
					if (cellNum == 2) {
						cell = totalRow.createCell(cellNum++);
						cell.setCellValue(score);
						cell.getCellStyle().setAlignment(CellStyle.ALIGN_CENTER);
					} else {
						cell = totalRow.createCell(cellNum++);
						cell = totalRow.createCell(cellNum++);
						cell.setCellValue(score);
						cell.getCellStyle().setAlignment(CellStyle.ALIGN_RIGHT);
					}

				}

				for (int columnPosition = 0; columnPosition < 25; columnPosition++) {
					sheet.autoSizeColumn((short) (columnPosition));
				}
			}

			String downloadFolder = System.getProperty("user.home");
			String fileName = "CqComparisonTable.xlsx";
			FileOutputStream out = new FileOutputStream(downloadFolder + "/" + fileName);
			workbook.write(out);
			out.close();
			Path file = Paths.get(downloadFolder, fileName);

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
		}
	}

	protected void buildBqComparisionFile(XSSFWorkbook workbook, List<EventEvaluationPojo> list, HttpServletResponse response, String eventId, String evelopId, RfxTypes eventType) throws IOException {
		LOG.info("Event Id " + eventId);
		Event event = null;
		Envelop envolope = null;
		switch (eventType) {
		case RFA:
			event = rfaEventService.getRfaEventByeventId(eventId);
			envolope = rfaEnvelopService.getRfaEnvelopById(evelopId);
			break;
		case RFT:
			event = rftEventService.getRftEventById(eventId);
			envolope = rftEnvelopService.getRftEnvelopById(evelopId);
			break;
		case RFP:
			event = rfpEventService.getRfpEventByeventId(eventId);
			envolope = rfpEnvelopService.getRfpEnvelopById(evelopId);
			break;
		case RFQ:
			event = rfqEventService.getEventById(eventId);
			envolope = rfqEnvelopService.getRfqEnvelopById(evelopId);
			break;
		case RFI:
			event = rfiEventService.getRfiEventByeventId(eventId);
			envolope = rfiEnvelopService.getRfiEnvelopById(evelopId);
			break;
		default:
			break;
		}
		if (CollectionUtil.isNotEmpty(list)) {
			int k = 1;
			for (EventEvaluationPojo evaluationPojo : list) {
				LOG.info("decimal :" + evaluationPojo.getDecimal() + " With Tax : " + evaluationPojo.getWithTax());
				// For Financial Standard
				DecimalFormat df = null;
				if (evaluationPojo.getDecimal().equals("1")) {
					df = new DecimalFormat("#,###,###,##0.0");
				} else if (evaluationPojo.getDecimal().equals("2")) {
					df = new DecimalFormat("#,###,###,##0.00");
				} else if (evaluationPojo.getDecimal().equals("3")) {
					df = new DecimalFormat("#,###,###,##0.000");
				} else if (evaluationPojo.getDecimal().equals("4")) {
					df = new DecimalFormat("#,###,###,##0.0000");
				} else if (evaluationPojo.getDecimal().equals("5")) {
					df = new DecimalFormat("#,###,###,##0.00000");
				} else if (evaluationPojo.getDecimal().equals("6")) {
					df = new DecimalFormat("#,###,###,##0.000000");
				}

				XSSFSheet sheet = workbook.createSheet("BQ price comparison" + k);
				k++;
				Row rowHeading = sheet.createRow(0);
				CellStyle styleHeading = workbook.createCellStyle();
				Font font = workbook.createFont();
				font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
				styleHeading.setFont(font);
				// styleHeading.setVerticalAlignment(CellStyle.ALIGN_CENTER);
				styleHeading.setAlignment(CellStyle.ALIGN_CENTER);
				int r = 1;
				short[] arr = { IndexedColors.LIGHT_ORANGE.getIndex(), IndexedColors.LIGHT_GREEN.getIndex(), IndexedColors.PINK.getIndex(), IndexedColors.LIGHT_TURQUOISE.getIndex(), IndexedColors.LIGHT_YELLOW.getIndex(), IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex() };
				int colorIndex = 0;
				int i = 5;

				colorIndex = 0;
				int x = 1;
				for (Supplier column : evaluationPojo.getColumns()) {
					int cellFirstMerge = 0;
					int lastCellForMerge = 0;
					cellFirstMerge = i;
					i = i + 2;
					if (Boolean.TRUE == evaluationPojo.getWithTax()) {
						i = i + 2;
					}
					lastCellForMerge = i - 1;
					sheet.addMergedRegion(new CellRangeAddress(0, 0, cellFirstMerge, lastCellForMerge));
					Cell cell = rowHeading.createCell(cellFirstMerge);
					CellStyle styleHeadingb = workbook.createCellStyle();
					styleHeadingb.setFont(font);
					styleHeadingb.setAlignment(CellStyle.ALIGN_CENTER);
					styleHeadingb.setFillForegroundColor(arr[colorIndex % 6]);
					styleHeadingb.setFillPattern(CellStyle.SOLID_FOREGROUND);
					if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking()) {
						cell.setCellValue(MaskUtils.maskName(envolope.getPreFix(), column.getId(), envolope.getId()));
					} else {
						cell.setCellValue(column.getCompanyName());
					}

					cell.setCellStyle(styleHeadingb);
					colorIndex++;
					cellFirstMerge = i;
					x++;
				}

				i = 0;
				Row row = sheet.createRow(r++);
				Cell cell = row.createCell(i++);
				cell.setCellValue("No");
				cell.setCellStyle(styleHeading);

				cell = row.createCell(i++);
				cell.setCellValue("Item");
				cell.setCellStyle(styleHeading);

				cell = row.createCell(i++);
				cell.setCellValue("Description");
				cell.setCellStyle(styleHeading);

				cell = row.createCell(i++);
				cell.setCellValue("UOM");
				cell.setCellStyle(styleHeading);

				cell = row.createCell(i++);
				cell.setCellValue("Quantity");
				cell.setCellStyle(styleHeading);

				List<Supplier> columns = evaluationPojo.getColumns();
				colorIndex = 0;
				for (int j = 0; j < columns.size(); j++) {
					CellStyle styleHeadingb = workbook.createCellStyle();
					styleHeadingb.setFillForegroundColor(arr[colorIndex % 6]);
					styleHeadingb.setFont(font);
					styleHeadingb.setFillPattern(CellStyle.SOLID_FOREGROUND);
					cell = row.createCell(i++);
					cell.setCellValue("Unit Price");
					cell.setCellStyle(styleHeadingb);
					cell = row.createCell(i++);
					cell.setCellValue("Amount");
					cell.setCellStyle(styleHeadingb);

					if (Boolean.TRUE == evaluationPojo.getWithTax()) {
						cell = row.createCell(i++);
						cell.setCellValue("Tax");
						cell.setCellStyle(styleHeadingb);
						cell = row.createCell(i++);
						cell.setCellValue("Amount Inc Tax");
						cell.setCellStyle(styleHeadingb);
					}

					// cell.getCellStyle().setAlignment(CellStyle.ALIGN_CENTER);
					colorIndex++;
				}

				for (List<String> data : evaluationPojo.getData()) {
					row = sheet.createRow(r++);
					int cellNum = 0;
					for (String answers : data) {
						if (cellNum <= 3) {
							row.createCell(cellNum++).setCellValue(answers);
						} else {
							if (answers != null && StringUtils.checkString(answers).length() > 0) {
								Cell cell1 = row.createCell(cellNum++);
								cell1.setCellValue(StringUtils.checkString(answers).length() > 0 ? df.format(new BigDecimal(answers)) : "");
								// if (cellNum == 4) {
								// cell1 = row.createCell(cellNum++);
								// cell1.setCellValue(StringUtils.checkString(answers).length() > 0 ? df.format(new
								// BigDecimal(answers)) : "");
								// } else {
								// cell1 = row.createCell(cellNum++);
								// cell1.setCellValue(StringUtils.checkString(answers).length() > 0 ? df.format(new
								// BigDecimal(answers)) : "");
								// }
								cell1.getCellStyle().setAlignment(CellStyle.ALIGN_RIGHT);

							} else {
								row.createCell(cellNum++).setCellValue(answers);
							}
						}
					}
				}

				r++;
				int cellNum = 4;
				Row totalRow = sheet.createRow(r++);
				Cell totalCell = totalRow.createCell(cellNum++);
				totalCell.setCellValue("Sub Total");
				for (BigDecimal score : evaluationPojo.getTotalAmount()) {
					totalCell.getCellStyle().setAlignment(CellStyle.ALIGN_CENTER);
					totalCell = totalRow.createCell(cellNum++);
					totalCell.setCellValue(score != null ? df.format(score) : "");
					totalCell.setCellStyle(styleHeading);
					totalCell.getCellStyle().setAlignment(CellStyle.ALIGN_RIGHT);
					if (score == null)
						totalCell.setCellType(Cell.CELL_TYPE_BLANK);
					totalCell.getCellStyle().setAlignment(CellStyle.ALIGN_RIGHT);
				}

				if (Boolean.TRUE == evaluationPojo.getWithTax()) {
					cellNum = 4;
					totalRow = sheet.createRow(r++);
					totalCell = totalRow.createCell(cellNum++);
					totalCell.setCellValue("Additional Tax");

					for (String taxInfo : evaluationPojo.getAddtionalTaxInfo()) {
						LOG.info("taxInfo   :  " + taxInfo);
						totalCell = totalRow.createCell(cellNum++);
						if (taxInfo == null || StringUtils.checkString(taxInfo).length() == 0) {
							totalCell.setCellType(Cell.CELL_TYPE_BLANK);
						} else {
							totalCell.setCellValue(StringUtils.checkString(taxInfo));
							totalCell.getCellStyle().setAlignment(CellStyle.ALIGN_RIGHT);
						}
					}
				}

				cellNum = 4;
				totalRow = sheet.createRow(r++);
				totalCell = totalRow.createCell(cellNum++);
				totalCell.setCellValue("Grand Total (" + (event.getBaseCurrency() != null ? event.getBaseCurrency().getCurrencyCode() : "") + ")");
				totalCell.setCellStyle(styleHeading);
				totalCell.getCellStyle().setAlignment(CellStyle.ALIGN_CENTER);
				for (BigDecimal score : evaluationPojo.getGrandTotals()) {
					totalCell = totalRow.createCell(cellNum++);
					totalCell.setCellValue(score != null ? df.format(score) : "");
					totalCell.setCellStyle(styleHeading);
					totalCell.getCellStyle().setAlignment(CellStyle.ALIGN_RIGHT);
					if (score == null)
						totalCell.setCellType(Cell.CELL_TYPE_BLANK);
					totalCell.getCellStyle().setAlignment(CellStyle.ALIGN_RIGHT);
				}

				for (int columnPosition = 0; columnPosition < 50; columnPosition++) {
					sheet.autoSizeColumn((short) (columnPosition));
				}

				if (CollectionUtil.isNotEmpty(evaluationPojo.getBqNonPriceComprision().getBuyerHeading()) || CollectionUtil.isNotEmpty(evaluationPojo.getBqNonPriceComprision().getSupplierHeading())) {

					createSheet(workbook, font, styleHeading, evaluationPojo, colorIndex, arr);
				}

				// String downloadFolder = System.getProperty("user.home");
				// String fileName = "BqComparisonTable.xlsx";
				// FileOutputStream out = new FileOutputStream(downloadFolder + "/" + fileName);
				// workbook.write(out);
				// out.close();
				// Path file = Paths.get(downloadFolder, fileName);
				//
				// if (Files.exists(file)) {
				// response.setContentType("application/vnd.ms-excel");
				// response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
				// try {
				// Files.copy(file, response.getOutputStream());
				// response.getOutputStream().flush();
				// } catch (IOException e) {
				// LOG.error("Error :- " + e.getMessage());
				// }
				// }
			}
			String downloadFolder = System.getProperty("user.home");
			String fileName = "BqComparisonTable.xlsx";
			FileOutputStream out = new FileOutputStream(downloadFolder + "/" + fileName);
			workbook.write(out);
			out.close();
			Path file = Paths.get(downloadFolder, fileName);

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
		}

	}

	private void createSheet(XSSFWorkbook workbook, Font font, CellStyle styleHeading, EventEvaluationPojo evaluationPojo, int colorIndex, short[] arr) {

		XSSFSheet sheet2 = workbook.createSheet("BQ non-price comparison");

		CellStyle styleHeading1 = workbook.createCellStyle();
		styleHeading1.setFont(font);
		int r2 = 1;
		int i2 = 0;
		Row row2 = sheet2.createRow(r2++);
		Cell cell2 = row2.createCell(i2++);
		cell2.getCellStyle().setAlignment(CellStyle.ALIGN_RIGHT);
		cell2.setCellValue("No");
		cell2.setCellStyle(styleHeading);

		cell2 = row2.createCell(i2++);
		cell2.setCellValue("Item");
		cell2.setCellStyle(styleHeading1);

		cell2 = row2.createCell(i2++);
		cell2.setCellValue("Description");
		cell2.setCellStyle(styleHeading1);

		Cell cell21 = row2.createCell(i2++);
		cell21.setCellValue("UOM");
		cell21.setCellStyle(styleHeading1);

		cell2 = row2.createCell(i2++);
		cell2.setCellValue("Quantity");
		cell2.setCellStyle(styleHeading1);

		LOG.info("--------------------------------------------------------");

		if (evaluationPojo.getBqNonPriceComprision() != null) {
			if (CollectionUtil.isNotEmpty(evaluationPojo.getBqNonPriceComprision().getBuyerHeading())) {
				for (String buyerHeading : evaluationPojo.getBqNonPriceComprision().getBuyerHeading()) {
					CellStyle styleHeadingb = workbook.createCellStyle();
					Font font1 = workbook.createFont();
					styleHeadingb.setFont(font1);
					font1.setColor(IndexedColors.WHITE.getIndex());
					font1.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
					styleHeadingb.setFillForegroundColor(IndexedColors.OLIVE_GREEN.getIndex());
					styleHeadingb.setFillPattern(CellStyle.SOLID_FOREGROUND);
					cell2 = row2.createCell(i2++);
					cell2.setCellValue(buyerHeading);
					cell2.setCellStyle(styleHeadingb);

				}
			}
			colorIndex = 0;
			LOG.info("i2 after printing buyer header count" + i2);
			if (CollectionUtil.isNotEmpty(evaluationPojo.getBqNonPriceComprision().getSupplierName())) {
				for (int supp = 0; supp < evaluationPojo.getBqNonPriceComprision().getSupplierName().size(); supp++) {
					CellStyle styleHeadingb = workbook.createCellStyle();
					styleHeadingb.setFillForegroundColor(arr[colorIndex % 6]);
					styleHeadingb.setFont(font);
					styleHeadingb.setFillPattern(CellStyle.SOLID_FOREGROUND);
					if (CollectionUtil.isNotEmpty(evaluationPojo.getBqNonPriceComprision().getSupplierHeading())) {
						for (String supplierHeading : evaluationPojo.getBqNonPriceComprision().getSupplierHeading()) {
							cell2 = row2.createCell(i2++);
							cell2.setCellValue(supplierHeading);
							cell2.setCellStyle(styleHeadingb);
						}
					}
					colorIndex++;
				}
			}
		}

		LOG.info("i2 after printing supplier header count" + i2);
		int storeI2ForSupplier = i2;

		LOG.info("storeI2ForSupplier : " + storeI2ForSupplier);
		int storeCellNum1 = 0;
		int storeCellNum1ForSupplier = 0;
		int storeIndex = 0;

		for (List<String> data : evaluationPojo.getData()) {
			row2 = sheet2.createRow(r2++);
			int cellNum1 = 0;
			for (String answers : data) {
				if (cellNum1 <= 4) {
					row2.createCell(cellNum1++).setCellValue(answers);
					storeCellNum1 = cellNum1;

				}
			}

		}
		int rowIndex = 2;
		if (evaluationPojo.getBqNonPriceComprision() != null) {
			if (CollectionUtil.isNotEmpty(evaluationPojo.getBqNonPriceComprision().getBuyerHeading())) {
				for (List<String> buyerDataList : evaluationPojo.getBqNonPriceComprision().getBuyerFeildData()) {
					row2 = sheet2.getRow(rowIndex++);
					int buyerCell = storeCellNum1;
					for (String data : buyerDataList) {
						row2.createCell(buyerCell++).setCellValue(data);
						storeCellNum1ForSupplier = buyerCell;
					}
				}
			} else {
				storeCellNum1ForSupplier = storeCellNum1;
			}
		}

		LOG.info("==storeIndex==" + storeIndex);

		rowIndex = 2;
		if (evaluationPojo.getBqNonPriceComprision().getSupplierData() != null && evaluationPojo.getBqNonPriceComprision().getSupplierData().size() > 0) {
			for (Entry<String, List<String>> entry : evaluationPojo.getBqNonPriceComprision().getSupplierData().entrySet()) {
				row2 = sheet2.getRow(rowIndex++);
				int cellNumber = storeCellNum1ForSupplier;
				for (String supplierData : entry.getValue()) {
					row2.createCell(cellNumber++).setCellValue(supplierData);

				}
			}
		}
		row2 = sheet2.getRow(0);
		if (row2 == null) {
			row2 = sheet2.createRow(0);
		}

		if (CollectionUtil.isNotEmpty(evaluationPojo.getBqNonPriceComprision().getBuyerHeading())) {
			for (int colcount = 5; colcount < (5 + evaluationPojo.getBqNonPriceComprision().getBuyerHeading().size()); colcount++) {
				CellStyle styleHeadingb = workbook.createCellStyle();
				styleHeadingb.setFont(font);
				styleHeadingb.setAlignment(CellStyle.ALIGN_CENTER);
				styleHeadingb.setFillForegroundColor(IndexedColors.OLIVE_GREEN.getIndex());
				styleHeadingb.setFillPattern(CellStyle.SOLID_FOREGROUND);
				cell2 = row2.createCell(colcount);
				cell2.setCellValue("");
				cell2.setCellStyle(styleHeadingb);
			}

		}

		int fixedheadingCount = 4;
		int buyerheadingCount = 0;
		if (CollectionUtil.isNotEmpty(evaluationPojo.getBqNonPriceComprision().getBuyerHeading())) {
			buyerheadingCount = evaluationPojo.getBqNonPriceComprision().getBuyerHeading().size();
		}
		int supplierheadingCount = 0;
		if (CollectionUtil.isNotEmpty(evaluationPojo.getBqNonPriceComprision().getSupplierHeading())) {
			supplierheadingCount = evaluationPojo.getBqNonPriceComprision().getSupplierHeading().size();
		}

		colorIndex = 0;
		int cellNub = fixedheadingCount + buyerheadingCount + 1;

		if (CollectionUtil.isNotEmpty(evaluationPojo.getBqNonPriceComprision().getSupplierHeading())) {
			if (CollectionUtil.isNotEmpty(evaluationPojo.getBqNonPriceComprision().getSupplierName())) {
				for (String supName : evaluationPojo.getBqNonPriceComprision().getSupplierName()) {
					LOG.info("cellNub" + cellNub);
					CellStyle styleHeadingb = workbook.createCellStyle();
					styleHeadingb.setFont(font);
					styleHeadingb.setAlignment(CellStyle.ALIGN_CENTER);
					styleHeadingb.setFillForegroundColor(arr[colorIndex % 6]);
					styleHeadingb.setFillPattern(CellStyle.SOLID_FOREGROUND);
					sheet2.addMergedRegion(new CellRangeAddress(0, 0, cellNub, cellNub + supplierheadingCount - 1));
					cell2 = row2.createCell(cellNub);
					cell2.setCellValue(supName);
					cell2.setCellStyle(styleHeadingb);
					// row2.createCell(cellNub).setCellValue(supName);
					cellNub = cellNub + supplierheadingCount;
					colorIndex++;
				}
			}
		}

		for (int columnPosition = 0; columnPosition < 50; columnPosition++) {
			sheet2.autoSizeColumn((short) (columnPosition));
		}
	}

}

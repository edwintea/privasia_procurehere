package com.privasia.procurehere.web.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.apache.poi.xssf.usermodel.XSSFDataValidationConstraint;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.multipart.MultipartFile;

import com.privasia.procurehere.core.entity.Cq;
import com.privasia.procurehere.core.entity.CqItem;
import com.privasia.procurehere.core.entity.RfaCqItem;
import com.privasia.procurehere.core.entity.RfaCqOption;
import com.privasia.procurehere.core.entity.RfiCqItem;
import com.privasia.procurehere.core.entity.RfiCqOption;
import com.privasia.procurehere.core.entity.RfpCqItem;
import com.privasia.procurehere.core.entity.RfpCqOption;
import com.privasia.procurehere.core.entity.RfqCqItem;
import com.privasia.procurehere.core.entity.RfqCqOption;
import com.privasia.procurehere.core.entity.RftCqItem;
import com.privasia.procurehere.core.entity.RftCqOption;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.CqType;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.parsers.CqFileParser;
import com.privasia.procurehere.core.pojo.CqItemPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.RfaCqService;
import com.privasia.procurehere.service.RfiCqService;
import com.privasia.procurehere.service.RfpCqService;
import com.privasia.procurehere.service.RfqCqService;
import com.privasia.procurehere.service.RftCqService;
import com.privasia.procurehere.web.editors.UserEditor;

public class EventCqBase {

	private static final Logger LOG = LogManager.getLogger(EventCqBase.class);

	@Autowired
	UserEditor userEditor;

	@Autowired
	RftCqService rftCqService;

	@Autowired
	RfpCqService rfpCqService;

	@Autowired
	RfaCqService rfaCqService;

	@Autowired
	RfiCqService rfiCqService;

	@Autowired
	RfqCqService rfqCqService;

	@Autowired
	ServletContext context;

	private RfxTypes eventType;

	@Autowired
	protected MessageSource messageSource;

	public EventCqBase(RfxTypes eventType) {
		this.eventType = eventType;
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(User.class, userEditor);
		StringTrimmerEditor stringtrimmer = new StringTrimmerEditor(true);
		binder.registerCustomEditor(String.class, stringtrimmer);
	}

	@ModelAttribute("eventType")
	public RfxTypes getEventType() {
		return eventType;
	}

	/**
	 * @param eventType the eventType to set
	 */
	public void setEventType(RfxTypes eventType) {
		this.eventType = eventType;
	}

	@ModelAttribute("step")
	public String getStep() {
		return "5";
	}

	protected void eventDownloader(XSSFWorkbook workbook, String cqId, RfxTypes eventType) throws IOException {
		XSSFSheet sheet = workbook.createSheet("CQ Item List");
		// Creating Headings
		buildHeader(workbook, sheet);
		int r = 1;

		switch (eventType) {
		case RFA:
			List<RfaCqItem> rfaCqList = rfaCqService.getAllCqitemsbyCqId(cqId);
			// Write Data into Excel
			for (RfaCqItem item : rfaCqList) {
				r = buildRows(sheet, r, item.getCq(), item);
			}
		case RFI:
			List<RfiCqItem> rfiCqList = rfiCqService.getAllCqitemsbyCqId(cqId);
			// Write Data into Excel
			for (RfiCqItem item : rfiCqList) {
				r = buildRows(sheet, r, item.getCq(), item);
			}
			break;
		case RFP:
			List<RfpCqItem> rfpCqList = rfpCqService.getAllCqitemsbyCqId(cqId);
			// Write Data into Excel
			for (RfpCqItem item : rfpCqList) {
				r = buildRows(sheet, r, item.getCq(), item);
			}
		case RFQ:
			List<RfqCqItem> rfqCqList = rfqCqService.getAllCqitemsbyCqId(cqId);
			// Write Data into Excel
			for (RfqCqItem item : rfqCqList) {
				r = buildRows(sheet, r, item.getCq(), item);
			}
			break;
		case RFT:
			List<RftCqItem> rftCqList = rftCqService.getAllCqitemsbyCqId(cqId);
			// Write Data into Excel
			for (RftCqItem item : rftCqList) {
				r = buildRows(sheet, r, item.getCq(), item);
			}
			break;
		default:
			break;

		}

		XSSFSheet lookupSheet = workbook.createSheet("LOOKUP");
		XSSFRow firstRow = null;
		XSSFRow secondRow = null;

		int index = 0;
		ArrayList<CqType> cqTypeList = new ArrayList<CqType>();
		for (CqType cqType : CqType.values()) {
		/*	if (cqType == CqType.DATE || cqType == CqType.DATE) {
				continue;
			}*/
			cqTypeList.add(cqType);

			XSSFRow row = lookupSheet.createRow(index++);
			if (index == 1) {
				firstRow = row;
			} else if (index == 2) {
				secondRow = row;
			}
			XSSFCell cell1 = row.createCell(0);
			cell1.setCellValue(cqType.getValue());
		}

		CqType[] cqTypeArr = new CqType[cqTypeList.size()];
		cqTypeList.toArray(cqTypeArr);

		// CQ Type
		// DVConstraint cqConstraint = DVConstraint.createFormulaListConstraint("'LOOKUP'!$A$1:$A$" + (cqTypeArr.length
		// + 1));

		XSSFDataValidationHelper validationHelper = new XSSFDataValidationHelper(lookupSheet);
		XSSFDataValidationConstraint constraint = (XSSFDataValidationConstraint) validationHelper.createFormulaListConstraint("'LOOKUP'!$A$1:$A$" + (cqTypeArr.length + 1));
		CellRangeAddressList cqTypeListCr = new CellRangeAddressList(1, 1000, 3, 3);

		XSSFDataValidation cqTypeValidation = (XSSFDataValidation) validationHelper.createValidation(constraint, cqTypeListCr);
		// XSSFDataValidation cqTypeValidation = new XSSFDataValidation(cqTypeList, cqConstraint);
		cqTypeValidation.setSuppressDropDownArrow(true);
		cqTypeValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
		cqTypeValidation.createErrorBox("Invalid Question Type Selected", "Please select Question Type from the list");
		cqTypeValidation.createPromptBox("Question Type", "Select Question Type from the list provided.");
		cqTypeValidation.setShowPromptBox(true);
		cqTypeValidation.setShowErrorBox(true);
		sheet.addValidationData(cqTypeValidation);

		// Yes/No
		XSSFCell cell1 = firstRow.createCell(1);
		cell1.setCellValue("YES");
		XSSFCell cell2 = secondRow.createCell(1);
		cell2.setCellValue("NO");

		// DVConstraint yesNoConstraint = DVConstraint.createFormulaListConstraint("'LOOKUP'!$B$1:$B$2");
		XSSFDataValidationConstraint yesNoConstraint = (XSSFDataValidationConstraint) validationHelper.createFormulaListConstraint("'LOOKUP'!$B$1:$B$2");
		CellRangeAddressList yesNoList = new CellRangeAddressList(1, 1000, 4, 6);

		XSSFDataValidation yesNoValidation = (XSSFDataValidation) validationHelper.createValidation(yesNoConstraint, yesNoList);
		// XSSFDataValidation yesNoValidation = new XSSFDataValidation(yesNoList, yesNoConstraint);
		yesNoValidation.setSuppressDropDownArrow(true);
		yesNoValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
		yesNoValidation.createErrorBox("Invalid Option Selected", "Please select either YES or NO from the list");
		yesNoValidation.createPromptBox("Option", "Select YES or NO from the list provided.");
		yesNoValidation.setShowPromptBox(true);
		yesNoValidation.setShowErrorBox(true);
		sheet.addValidationData(yesNoValidation);
		workbook.setSheetHidden(1, true);

		// Auto Fit
		for (int i = 0; i < 50; i++) {
			sheet.autoSizeColumn(i, true);
		}
	}

	// Style of Heading Cells
	private void buildHeader(XSSFWorkbook workbook, XSSFSheet sheet) {
		Row rowHeading = sheet.createRow(0);
		CellStyle styleHeading = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		styleHeading.setFont(font);
		styleHeading.setVerticalAlignment(CellStyle.ALIGN_CENTER);

		int i = 0;
		List<String> option = Arrays.asList(Global.CQ_EXCEL_OPTION_COLUMNS);
		for (String column : Global.CQ_EXCEL_COLUMNS) {
			Cell cell = rowHeading.createCell(i++);
			cell.setCellValue(column);
			cell.setCellStyle(styleHeading);
			if (option.contains(column)) {
				// For Cell Comments
				Drawing drawing = cell.getSheet().createDrawingPatriarch();
				CreationHelper factory = cell.getSheet().getWorkbook().getCreationHelper();
				ClientAnchor anchor = factory.createClientAnchor();

				// size and location for comment box
				anchor.setCol1(cell.getColumnIndex() + 1);
				anchor.setCol2(cell.getColumnIndex() + 5);
				anchor.setRow1(cell.getRowIndex() + 1);
				anchor.setRow2(cell.getRowIndex() + 8);

				Comment comment = drawing.createCellComment(anchor);
				RichTextString str = factory.createRichTextString("If Question Type is \"Choice with Score\" then options should have score.\n You can write like [SCORE]/[OPTION] where SCORE is numeric value (eg: 1/YES).");
				comment.setVisible(Boolean.FALSE);
				comment.setString(str);
				cell.setCellComment(comment);
			}
		}
	}

	private int buildRows(XSSFSheet sheet, int r, Cq bq, CqItem item) throws IOException {
		// CellStyle unlockedCellStyle = workbook.createCellStyle();
		// unlockedCellStyle.setLocked(true);
		Row row = sheet.createRow(r++);
		int cellNum = 0;

		// row.createCell(cellNum++).setCellValue(item.getId());
		row.createCell(cellNum++).setCellValue(item.getLevel() + "." + item.getOrder());
		row.createCell(cellNum++).setCellValue(item.getItemName());
		row.createCell(cellNum++).setCellValue(item.getItemDescription() != null ? item.getItemDescription() : "");
		if (item.getOrder() == 0) {
			int colNum = 6;
			sheet.addMergedRegion(new CellRangeAddress(r - 1, r - 1, cellNum - 1, colNum));
		} else {
			row.createCell(cellNum++).setCellValue(item.getCqType() != null ? item.getCqType().getValue() : " ");
			row.createCell(cellNum++).setCellValue((item.getAttachment() != null && item.getAttachment() == true) ? "YES" : "NO");
			row.createCell(cellNum++).setCellValue((item.getOptional() != null && item.getOptional() == true) ? "YES" : "NO");
			row.createCell(cellNum++).setCellValue((item.getIsSupplierAttachRequired() != null && item.getIsSupplierAttachRequired() == true) ? "YES" : "NO");
			switch (eventType) {
			case RFA:
				for (RfaCqOption option : ((RfaCqItem) item).getCqOptions()) {
					row.createCell(cellNum++).setCellValue((option.getScoring() != null ? option.getScoring() + "/" : "") + option.getValue());
				}
				break;
			case RFI:
				for (RfiCqOption option : ((RfiCqItem) item).getCqOptions()) {
					row.createCell(cellNum++).setCellValue((option.getScoring() != null ? option.getScoring() + "/" : "") + option.getValue());
				}
				break;
			case RFP:
				for (RfpCqOption option : ((RfpCqItem) item).getCqOptions()) {
					row.createCell(cellNum++).setCellValue((option.getScoring() != null ? option.getScoring() + "/" : "") + option.getValue());
				}
				break;
			case RFQ:
				for (RfqCqOption option : ((RfqCqItem) item).getCqOptions()) {
					row.createCell(cellNum++).setCellValue((option.getScoring() != null ? option.getScoring() + "/" : "") + option.getValue());
				}
				break;
			case RFT:
				for (RftCqOption option : ((RftCqItem) item).getCqOptions()) {
					row.createCell(cellNum++).setCellValue((option.getScoring() != null ? option.getScoring() + "/" : "") + option.getValue());
				}
				break;
			default:
				break;
			}
		}
		return r;
	}

	/**
	 * @param file
	 * @param cqId
	 * @param eventId
	 * @param rft
	 * @return
	 * @throws Exception
	 */
	public String cqItemsUpload(MultipartFile file, String cqId, String eventId, RfxTypes eventType) throws Exception {

		String message = null;
		File convFile = File.createTempFile(file.getOriginalFilename(), "xlsx");

		convFile.createNewFile();
		file.transferTo(convFile);
		switch (eventType) {
		case RFA:
			CqFileParser<RfaCqItem> rfaCi = new CqFileParser<RfaCqItem>(RfaCqItem.class);
			Map<Integer, Map<Integer, RfaCqItem>> rfaData = rfaCi.parse(convFile);
			message = rfaCqService.doExcelDataSave(rfaData, eventId, cqId);
			break;
		case RFI:
			CqFileParser<RfiCqItem> rfiCi = new CqFileParser<RfiCqItem>(RfiCqItem.class);
			Map<Integer, Map<Integer, RfiCqItem>> rfiData = rfiCi.parse(convFile);
			message = rfiCqService.doExcelDataSave(rfiData, eventId, cqId);
			break;
		case RFP:
			CqFileParser<RfpCqItem> rfpCi = new CqFileParser<RfpCqItem>(RfpCqItem.class);
			Map<Integer, Map<Integer, RfpCqItem>> rfpData = rfpCi.parse(convFile);
			message = rfpCqService.doExcelDataSave(rfpData, eventId, cqId);
			break;
		case RFQ:
			CqFileParser<RfqCqItem> rfqCi = new CqFileParser<RfqCqItem>(RfqCqItem.class);
			Map<Integer, Map<Integer, RfqCqItem>> rfqData = rfqCi.parse(convFile);
			message = rfqCqService.doExcelDataSave(rfqData, eventId, cqId);
			break;
		case RFT:
			CqFileParser<RftCqItem> rftCi = new CqFileParser<RftCqItem>(RftCqItem.class);
			Map<Integer, Map<Integer, RftCqItem>> rftData = rftCi.parse(convFile);
			message = rftCqService.doExcelDataSave(rftData, eventId, cqId);
			break;
		default:
			break;

		}

		try {
			if (convFile != null) {
				convFile.delete();
			}
		} catch (Exception e) {

		}
		return message;
	}

	/**
	 * @param rftCqItem
	 * @param headers
	 * @param item
	 * @return
	 */
	protected boolean doValidate(CqItemPojo rftCqItem, HttpHeaders headers) {
		LOG.info("checking Duplicate options...");
		if (StringUtils.checkString(rftCqItem.getCqType()).length() > 0) {
			if (!rftCqItem.getCqType().equals(CqType.TEXT.toString()) && CollectionUtil.isNotEmpty(rftCqItem.getOptions())) {
				for (String option : rftCqItem.getOptions()) {
					if (StringUtils.checkString(option).length() == 0) {
						LOG.info("option).length() == 0");
						headers.add("error", messageSource.getMessage("----", new Object[] {}, "Option is required", Global.LOCALE));
						return false;
					}
					// else if (!rftCqItem.getCqType().equals(CqType.CHECKBOX.toString()) &&
					// rftCqItem.getOptions().size() < 2) {
					// LOG.info("size() <2");
					// headers.add("error", messageSource.getMessage("----", new Object[] {}, "Please add two options",
					// Global.LOCALE));
					// return false;
					// } else if (rftCqItem.getCqType().equals(CqType.CHECKBOX.toString()) &&
					// rftCqItem.getOptions().size() < 1) {
					// LOG.info("Checkboxes() size() <1");
					// headers.add("error", messageSource.getMessage("----", new Object[] {}, "Please add atleast one
					// option", Global.LOCALE));
					// return false;
					// }
					// break;
				}
				List<String> optionList = new ArrayList<String>();
				for (String option : rftCqItem.getOptions()) {
					if (optionList.contains(option.toUpperCase())) {
						LOG.info("Duplicate options...");
						headers.add("error", messageSource.getMessage("----", new Object[] {}, "Option with that name is already exists", Global.LOCALE));
						return false;
					}
					optionList.add(option.toUpperCase());
				}
				optionList.clear();
			}
		}
		return true;
	}

	/**
	 * @author parveen - DO NOT DELETE THIS. PAKAD KE MARUNGA
	 * @param rftCqItem
	 */
	// /*public ResponseEntity<List<RftCqItem>> createRftCqItem(CqItemPojo rftCqItem) {
	// HttpHeaders headers = new HttpHeaders();
	// try {
	// LOG.info("CQ ITEM : " + rftCqItem.toLogString() + "rftCqItem.getOptions() :" + rftCqItem.getOptions().size());
	// if (StringUtils.checkString(rftCqItem.getItemName()).length() > 0) {
	// CqItem item = new CqItem();
	// item.setItemName(rftCqItem.getItemName());
	// item.setItemDescription(rftCqItem.getItemDescription());
	//
	// boolean exists = false;
	// switch(eventType) {
	// case RFA:
	// break;
	// case RFI:
	// break;
	// case RFP:
	// break;
	// case RFQ:
	// break;
	// case RFT:
	// exists = rftCqService.isExists((RftCqItem)item, rftCqItem.getCq(), rftCqItem.getParent());
	// ((RftCqItem)item).setRfxEvent(rftCqService.getRftEventById(rftCqItem.getRftEvent()));
	// ((RftCqItem)item).setCq(rftCqService.getRftCqById(rftCqItem.getCq()));
	// if (CollectionUtil.isNotEmpty(rftCqItem.getOptions())) {
	// List<RftCqOption> optionItems = new ArrayList<RftCqOption>();
	// for (String option : rftCqItem.getOptions()) {
	// RftCqOption options = new RftCqOption();
	// options.setValue(option);
	// options.setRftCqItem((RftCqItem)item);
	// optionItems.add(options);
	// }
	// ((RftCqItem)item).setCqOptions(optionItems);
	// }
	// break;
	// default:
	// break;
	//
	// }
	//
	// if (exists) {
	// LOG.info("Duplicate....");
	// throw new ApplicationException("Duplicate CQ Item. CQ Item by that name already exists.");
	// }
	// if (!doValidate(rftCqItem, headers, item)) {
	// return new ResponseEntity<List<RftCqItem>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
	// }
	// item.setAttachment(rftCqItem.isAttachment());
	// item.setOptional(rftCqItem.isOptional());
	//
	// if (StringUtils.checkString(rftCqItem.getParent()).length() > 0) {
	// item.setParent(rftCqService.getCqItembyCqItemId(StringUtils.checkString(rftCqItem.getParent())));
	// }
	// rftCqService.saveRftCqItem(item);
	//
	// List<RftCqItem> bqList = rftCqService.findRftCqbyCqId(rftCqItem.getCq());
	// headers.add("success", messageSource.getMessage("buyer.rftcq.save", new Object[] {}, Global.LOCALE));
	// return new ResponseEntity<List<RftCqItem>>(bqList, headers, HttpStatus.OK);
	// } else {
	// List<RftCqItem> bqList = rftCqService.findRftCqbyCqId(rftCqItem.getCq());
	// headers.add("info", messageSource.getMessage("rft.cq.name.empty", new Object[] {}, Global.LOCALE));
	// return new ResponseEntity<List<RftCqItem>>(bqList, headers, HttpStatus.OK);
	// }
	// } catch (Exception e) {
	// LOG.error("Error while adding CQ Items to Event : " + e.getMessage(), e);
	// headers.add("error", messageSource.getMessage("buyer.rftcq.error", new Object[] { e.getMessage() },
	// Global.LOCALE));
	// return new ResponseEntity<List<RftCqItem>>(null, headers, HttpStatus.INTERNAL_SERVER_ERROR);
	// }
	//
	// }*/

}
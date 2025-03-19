package com.privasia.procurehere.web.controller;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.validation.Validation;
import javax.validation.Validator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.Font;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import com.privasia.procurehere.core.entity.Bq;
import com.privasia.procurehere.core.entity.BqItem;
import com.privasia.procurehere.core.entity.Event;
import com.privasia.procurehere.core.entity.RfaBqItem;
import com.privasia.procurehere.core.entity.RfaEventBq;
import com.privasia.procurehere.core.entity.RfpBqItem;
import com.privasia.procurehere.core.entity.RfpEventBq;
import com.privasia.procurehere.core.entity.RfqBqItem;
import com.privasia.procurehere.core.entity.RfqEventBq;
import com.privasia.procurehere.core.entity.RftBqItem;
import com.privasia.procurehere.core.entity.RftEventBq;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.BqUserTypes;
import com.privasia.procurehere.core.enums.EventVisibilityType;
import com.privasia.procurehere.core.enums.PricingTypes;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.exceptions.ExcelParseException;
import com.privasia.procurehere.core.pojo.AuthenticatedUser;
import com.privasia.procurehere.core.pojo.BqItemPojo;
import com.privasia.procurehere.core.pojo.BqPojo;
import com.privasia.procurehere.core.pojo.UomPojo;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.RfaBqService;
import com.privasia.procurehere.service.RfaEventService;
import com.privasia.procurehere.service.RfpBqService;
import com.privasia.procurehere.service.RfpEventService;
import com.privasia.procurehere.service.RfqBqService;
import com.privasia.procurehere.service.RftBqService;
import com.privasia.procurehere.service.RftEventService;
import com.privasia.procurehere.service.UomService;
import com.privasia.procurehere.service.UserService;
import com.privasia.procurehere.web.editors.RfaEventBqEditor;
import com.privasia.procurehere.web.editors.UserEditor;

public class EventBqBase {

	protected Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	protected static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@Autowired
	UserEditor userEditor;

	@Autowired
	UomService uomService;

	@Autowired
	RftBqService rftBqService;

	@Autowired
	RfaBqService rfaBqService;

	@Autowired
	RfpBqService rfpBqService;

	@Autowired
	RfqBqService rfqBqService;

	@Autowired
	MessageSource messageSource;

	@Autowired
	ServletContext context;

	@Autowired
	RftEventService rftEventService;

	@Autowired
	RfpEventService rfpEventService;

	@Autowired
	RfaEventService rfaEventService;

	@Autowired
	RfaEventBqEditor rfaEventBqEditor;

	@Autowired
	UserService userService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(User.class, userEditor);
		binder.registerCustomEditor(RfaEventBq.class, rfaEventBqEditor);
		StringTrimmerEditor stringtrimmer = new StringTrimmerEditor(true);
		binder.registerCustomEditor(String.class, stringtrimmer);
	}

	private RfxTypes eventType;

	public EventBqBase(RfxTypes eventType) {
		this.eventType = eventType;
	}

	@ModelAttribute("step")
	public String getStep() {
		return "6";
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

	protected String bqPrevious(Event rftEvent) {
		if (Boolean.TRUE == rftEvent.getQuestionnaires()) {
			return "redirect:eventCqList/" + rftEvent.getId();
		} else if (Boolean.TRUE == rftEvent.getMeetingReq()) {
			return "redirect:meetingList/" + rftEvent.getId();
		} else if (rftEvent.getEventVisibility() != EventVisibilityType.PUBLIC) {
			return "redirect:addSupplier/" + rftEvent.getId();
		} else if (Boolean.TRUE == rftEvent.getDocumentReq()) {
			return "redirect:createEventDocuments/" + rftEvent.getId();
		} else {
			return "redirect:eventDescription/" + rftEvent.getId();
		}
	}

	public String bqNext(Event rftEvent) {
		if(Boolean.TRUE == rftEvent.getScheduleOfRate()){
			return "redirect:createSorList/" + rftEvent.getId();
		} else {
			return "redirect:envelopList/" + rftEvent.getId();
		}
	}

	/**
	 * @param eventId
	 * @param bqId
	 * @param model
	 * @param rftEvent
	 * @param eventBq
	 * @param bqList
	 */
	protected void buildModel(String eventId, String bqId, Model model, Event rftEvent, Bq eventBq) {
		List<UomPojo> uomList = uomService.getAllUomPojo(SecurityLibrary.getLoggedInUserTenantId());
		PricingTypes[] pricingTypes = PricingTypes.values();
		BqUserTypes[] BqFilledBy = BqUserTypes.values();
		model.addAttribute("pricingTypes", pricingTypes);
		model.addAttribute("bqFilledBy", BqFilledBy);
		model.addAttribute("eventId", eventId);
		model.addAttribute("bqId", bqId);
		model.addAttribute("uomList", uomList);
		model.addAttribute("event", rftEvent);
		model.addAttribute("eventBq", eventBq);
		model.addAttribute("bqName", eventBq.getName());
	}

	/**
	 * @param rftBqItemPojo
	 * @param rftBqItem
	 */
	protected void constructBqItemValues(BqItemPojo rftBqItemPojo, BqItem rftBqItem) {
		rftBqItem.setItemDescription(rftBqItemPojo.getItemDescription());
		rftBqItem.setItemName(rftBqItemPojo.getItemName());
		rftBqItem.setQuantity(rftBqItemPojo.getQuantity());
		rftBqItem.setUnitPrice(rftBqItemPojo.getUnitPrice());
		rftBqItem.setUnitPriceType(rftBqItemPojo.getUnitPriceType());
		if (StringUtils.checkString(rftBqItemPojo.getUom()).length() > 0) {
			rftBqItem.setUom(uomService.getUombyCode(rftBqItemPojo.getUom(), SecurityLibrary.getLoggedInUserTenantId()));
		}

		// LOG.info("Price Type : " + rftBqItemPojo.getPriceType());
		rftBqItem.setPriceType(rftBqItemPojo.getPriceType());

		rftBqItem.setField1(rftBqItemPojo.getField1() != null ? rftBqItemPojo.getField1() : null);
		rftBqItem.setField2(rftBqItemPojo.getField2() != null ? rftBqItemPojo.getField2() : null);
		rftBqItem.setField3(rftBqItemPojo.getField3() != null ? rftBqItemPojo.getField3() : null);
		rftBqItem.setField4(rftBqItemPojo.getField4() != null ? rftBqItemPojo.getField4() : null);

		rftBqItem.setField5(rftBqItemPojo.getField5() != null ? rftBqItemPojo.getField5() : null);
		rftBqItem.setField6(rftBqItemPojo.getField6() != null ? rftBqItemPojo.getField6() : null);
		rftBqItem.setField7(rftBqItemPojo.getField7() != null ? rftBqItemPojo.getField7() : null);
		rftBqItem.setField8(rftBqItemPojo.getField8() != null ? rftBqItemPojo.getField8() : null);
		rftBqItem.setField9(rftBqItemPojo.getField9() != null ? rftBqItemPojo.getField9() : null);
		rftBqItem.setField10(rftBqItemPojo.getField10() != null ? rftBqItemPojo.getField10() : null);
	}

	public List<Bq> saveBq(BqPojo rftBqPojo, Integer count) {
		List<Bq> list = null;
		switch (getEventType()) {
		case RFT:
			RftEventBq rftEventBq = new RftEventBq();
			rftEventBq.setRfxEvent(rftBqService.getRftEventById(rftBqPojo.getEventId()));
			rftEventBq.setId(rftBqPojo.getId());
			rftEventBq.setName(rftBqPojo.getBqName());
			rftEventBq.setDescription(rftBqPojo.getBqDesc());
			rftEventBq.setCreatedDate(new Date());
			rftEventBq.setBqOrder(count);
			rftBqService.saveRftBq(rftEventBq);
			list = rftBqService.findRftBqbyEventId(rftBqPojo.getEventId());
			break;
		case RFI:
			break;
		case RFP:
			RfpEventBq rfpEventBq = new RfpEventBq();
			rfpEventBq.setRfxEvent(rfpBqService.getRfpEventById(rftBqPojo.getEventId()));
			rfpEventBq.setId(rftBqPojo.getId());
			rfpEventBq.setName(rftBqPojo.getBqName());
			rfpEventBq.setDescription(rftBqPojo.getBqDesc());
			rfpEventBq.setCreatedDate(new Date());
			rfpEventBq.setBqOrder(count);
			rfpBqService.saveBq(rfpEventBq);
			list = rfpBqService.findBqbyEventId(rftBqPojo.getEventId());
			break;
		case RFA:
			RfaEventBq rfaEventBq = new RfaEventBq();
			rfaEventBq.setRfxEvent(rfaBqService.getRfaEventById(rftBqPojo.getEventId()));
			rfaEventBq.setId(rftBqPojo.getId());
			rfaEventBq.setName(rftBqPojo.getBqName());
			rfaEventBq.setBqOrder(count);
			rfaEventBq.setDescription(rftBqPojo.getBqDesc());
			rfaEventBq.setCreatedDate(new Date());
			rfaBqService.saveRfaBq(rfaEventBq);
			list = rfaBqService.findRfaBqbyEventId(rftBqPojo.getEventId());
			break;
		case RFQ:
			RfqEventBq rfqEventBq = new RfqEventBq();
			rfqEventBq.setRfxEvent(rfqBqService.getEventById(rftBqPojo.getEventId()));
			rfqEventBq.setId(rftBqPojo.getId());
			rfqEventBq.setName(rftBqPojo.getBqName());
			rfqEventBq.setDescription(rftBqPojo.getBqDesc());
			rfqEventBq.setCreatedDate(new Date());
			rfqEventBq.setBqOrder(count);
			rfqBqService.saveBq(rfqEventBq);
			list = rfqBqService.findBqbyEventId(rftBqPojo.getEventId());
			break;

		default:
			break;
		}

		return list;
	}

	/**
	 * @param rftBqPojo
	 * @return
	 */
	protected boolean bqValidate(BqPojo rftBqPojo) {
		boolean validate = true;
		switch (getEventType()) {
		case RFT:
			if (rftBqService.isBqExists(rftBqPojo)) {
				validate = false;
			}
			break;
		case RFI:
			break;
		case RFP:
			if (rfpBqService.isBqExists(rftBqPojo)) {
				validate = false;
			}
			break;
		case RFA:
			if (rfaBqService.isBqExists(rftBqPojo)) {
				validate = false;
			}
			break;
		case RFQ:
			if (rfqBqService.isBqExists(rftBqPojo)) {
				validate = false;
			}
			break;

		default:
			break;
		}

		return validate;
	}

	/**
	 * @param bqPojo
	 * @param bqItem
	 */
	protected void buildUpdateBqItem(BqItemPojo bqPojo, BqItem bqItem) {
		bqItem.setItemName(bqPojo.getItemName());
		bqItem.setUom(bqItem.getUom());
		bqItem.setUnitPrice(bqPojo.getUnitPrice());
		bqItem.setUnitPriceType(bqPojo.getUnitPriceType());
		bqItem.setQuantity(bqPojo.getQuantity());
		bqItem.setItemDescription(bqPojo.getItemDescription());
		// LOG.info("Price Type : " + bqPojo.getPriceType());
		bqItem.setPriceType(bqPojo.getPriceType());

		bqItem.setField1(bqPojo.getField1());
		bqItem.setField2(bqPojo.getField2());
		bqItem.setField3(bqPojo.getField3());
		bqItem.setField4(bqPojo.getField4());
		bqItem.setField5(bqPojo.getField5());
		bqItem.setField6(bqPojo.getField6());
		bqItem.setField7(bqPojo.getField7());
		bqItem.setField8(bqPojo.getField8());
		bqItem.setField9(bqPojo.getField9());
		bqItem.setField10(bqPojo.getField10());
	}

	/**
	 * @param bqPojo
	 * @param eventBq
	 */
	protected void buildAddNewColumns(BqPojo bqPojo, Bq eventBq) {
		eventBq.setField1Label(bqPojo.getField1Label() != null ? bqPojo.getField1Label() : null);
		eventBq.setField1FilledBy(bqPojo.getField1FilledBy() != null ? bqPojo.getField1FilledBy() : null);
		eventBq.setField1ToShowSupplier(bqPojo.isField1ToShowSupplier());
		eventBq.setField1Required(bqPojo.isField1Required());

		eventBq.setField2Label(bqPojo.getField2Label() != null ? bqPojo.getField2Label() : null);
		eventBq.setField2FilledBy(bqPojo.getField2FilledBy() != null ? bqPojo.getField2FilledBy() : null);
		eventBq.setField2ToShowSupplier(bqPojo.isField2ToShowSupplier());
		eventBq.setField2Required(bqPojo.isField2Required());

		eventBq.setField3Label(bqPojo.getField3Label() != null ? bqPojo.getField3Label() : null);
		eventBq.setField3FilledBy(bqPojo.getField3FilledBy() != null ? bqPojo.getField3FilledBy() : null);
		eventBq.setField3ToShowSupplier(bqPojo.isField3ToShowSupplier());
		eventBq.setField3Required(bqPojo.isField3Required());

		eventBq.setField4Label(bqPojo.getField4Label() != null ? bqPojo.getField4Label() : null);
		eventBq.setField4FilledBy(bqPojo.getField4FilledBy() != null ? bqPojo.getField4FilledBy() : null);
		eventBq.setField4ToShowSupplier(bqPojo.isField4ToShowSupplier());
		eventBq.setField4Required(bqPojo.isField4Required());

		eventBq.setField5Label(bqPojo.getField5Label() != null ? bqPojo.getField5Label() : null);
		eventBq.setField5FilledBy(bqPojo.getField5FilledBy() != null ? bqPojo.getField5FilledBy() : null);
		eventBq.setField5ToShowSupplier(bqPojo.isField5ToShowSupplier());
		eventBq.setField5Required(bqPojo.isField5Required());

		eventBq.setField6Label(bqPojo.getField6Label() != null ? bqPojo.getField6Label() : null);
		eventBq.setField6FilledBy(bqPojo.getField6FilledBy() != null ? bqPojo.getField6FilledBy() : null);
		eventBq.setField6ToShowSupplier(bqPojo.isField6ToShowSupplier());
		eventBq.setField6Required(bqPojo.isField6Required());

		eventBq.setField7Label(bqPojo.getField7Label() != null ? bqPojo.getField7Label() : null);
		eventBq.setField7FilledBy(bqPojo.getField7FilledBy() != null ? bqPojo.getField7FilledBy() : null);
		eventBq.setField7ToShowSupplier(bqPojo.isField7ToShowSupplier());
		eventBq.setField7Required(bqPojo.isField7Required());

		eventBq.setField8Label(bqPojo.getField8Label() != null ? bqPojo.getField8Label() : null);
		eventBq.setField8FilledBy(bqPojo.getField8FilledBy() != null ? bqPojo.getField8FilledBy() : null);
		eventBq.setField8ToShowSupplier(bqPojo.isField8ToShowSupplier());
		eventBq.setField8Required(bqPojo.isField8Required());

		eventBq.setField9Label(bqPojo.getField9Label() != null ? bqPojo.getField9Label() : null);
		eventBq.setField9FilledBy(bqPojo.getField9FilledBy() != null ? bqPojo.getField9FilledBy() : null);
		eventBq.setField9ToShowSupplier(bqPojo.isField9ToShowSupplier());
		eventBq.setField9Required(bqPojo.isField9Required());

		eventBq.setField10Label(bqPojo.getField10Label() != null ? bqPojo.getField10Label() : null);
		eventBq.setField10FilledBy(bqPojo.getField10FilledBy() != null ? bqPojo.getField10FilledBy() : null);
		eventBq.setField10ToShowSupplier(bqPojo.isField10ToShowSupplier());
		eventBq.setField10Required(bqPojo.isField10Required());
	}

	/**
	 * @param workbook
	 * @param bqId
	 * @param eventType
	 * @param file
	 * @param downloadFolder
	 * @param rfxType
	 * @param response
	 * @throws IOException
	 */
	protected void eventDownloader(XSSFWorkbook workbook, String bqId, RfxTypes eventType) throws IOException {
		XSSFSheet sheet = workbook.createSheet("BQ Item List");
		int r = 1;
		Bq bq = null;
		switch (eventType) {
		case RFA:
			List<RfaBqItem> rfaBqList = rfaBqService.getAllBqitemsbyBqId(bqId);

			// Creating Headings
			bq = rfaBqService.getRfaBqById(bqId);
			buildHeader(bq, workbook, sheet);

			// Write Data into Excel
			for (RfaBqItem item : rfaBqList) {
				r = buildRows(sheet, r, item.getBq(), item);
			}
			break;
		case RFI:
			break;
		case RFP:
			List<RfpBqItem> rfpBqList = rfpBqService.getAllBqitemsbyBqId(bqId);

			// Creating Headings
			bq = rfpBqService.getBqById(bqId);
			buildHeader(bq, workbook, sheet);

			// Write Data into Excel
			for (RfpBqItem item : rfpBqList) {
				r = buildRows(sheet, r, item.getBq(), item);
			}
			break;
		case RFQ:
			List<RfqBqItem> rfqBqList = rfqBqService.getAllBqitemsbyBqId(bqId);

			// Creating Headings
			bq = rfqBqService.getBqById(bqId);
			buildHeader(bq, workbook, sheet);

			// Write Data into Excel
			for (RfqBqItem item : rfqBqList) {
				r = buildRows(sheet, r, item.getBq(), item);
			}
			break;
		case RFT:
			List<RftBqItem> rftBqList = rftBqService.getAllBqitemsbyBqId(bqId);

			// Creating Headings
			bq = rftBqService.getRftBqById(bqId);
			buildHeader(bq, workbook, sheet);

			// Write Data into Excel
			for (RftBqItem item : rftBqList) {
				r = buildRows(sheet, r, item.getBq(), item);
			}
			break;
		default:
			break;

		}

		XSSFSheet lookupSheet1 = workbook.createSheet("LOOKUP1");

		List<UomPojo> uom = uomService.getAllUomPojo(SecurityLibrary.getLoggedInUserTenantId());
		int index1 = 0;
		for (UomPojo u : uom) {
			String uomId = u.getId();
			String uomName = u.getUom();
			LOG.info("UOM NAME :: " + uomName);
			XSSFRow row = lookupSheet1.createRow(index1++);
			XSSFCell cell1 = row.createCell(0);
			cell1.setCellValue(uomId);
			XSSFCell cell2 = row.createCell(1);
			cell2.setCellValue(uomName);
		}
		XSSFSheet lookupSheet2 = workbook.createSheet("LOOKUP2");
		int index2 = 0;
		PricingTypes[] priceTypesArr = PricingTypes.values();
		for (PricingTypes type : priceTypesArr) {
			String name = type.getValue();
			LOG.info("NAME :: " + name);
			XSSFRow firstRow = lookupSheet2.createRow(index2++);
			XSSFCell cell2 = firstRow.createCell(0);
			cell2.setCellValue(name);
		}

		// UOM
		// DVConstraint constraint = DVConstraint.createFormulaListConstraint("'LOOKUP1'!$B$1:$B$" + (uom.size() + 1));
		XSSFDataValidationHelper validationHelper = new XSSFDataValidationHelper(lookupSheet1);
		XSSFDataValidationConstraint constraint = (XSSFDataValidationConstraint) validationHelper.createFormulaListConstraint("'LOOKUP1'!$B$1:$B$" + (uom.size() + 1));
		CellRangeAddressList addressList = new CellRangeAddressList(1, 1000, 3, 3);

		XSSFDataValidation validation = (XSSFDataValidation) validationHelper.createValidation(constraint, addressList);
		// XSSFDataValidation validation = new HSSFDataValidation(addressList, constraint);
		validation.setSuppressDropDownArrow(true);
		validation.setErrorStyle(DataValidation.ErrorStyle.STOP);
		validation.createErrorBox("Invalid UOM Selected", "Please select UOM from the list");
		validation.createPromptBox("UOM List", "Select UOM from the list provided. It has been exported from your master data.");
		validation.setShowPromptBox(true);
		validation.setShowErrorBox(true);
		sheet.addValidationData(validation);

		// PRICE TYPE
		// DVConstraint priceConstraint = DVConstraint.createFormulaListConstraint("'LOOKUP2'!$A$1:$A$" +
		// (priceTypesArr.length + 1));
		XSSFDataValidationHelper priceValidationHelper = new XSSFDataValidationHelper(lookupSheet2);
		XSSFDataValidationConstraint priceConstraint = (XSSFDataValidationConstraint) priceValidationHelper.createFormulaListConstraint("'LOOKUP2'!$A$1:$A$" + (priceTypesArr.length + 1));
		CellRangeAddressList priceaddressList = new CellRangeAddressList(1, 1000, 6, 6);

		XSSFDataValidation pricevalidation = (XSSFDataValidation) validationHelper.createValidation(priceConstraint, priceaddressList);
		pricevalidation.setSuppressDropDownArrow(true);
		pricevalidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
		pricevalidation.createErrorBox("Invalid PRICE TYPE Selected", "Please select PRICE TYPE from the list");
		pricevalidation.createPromptBox("PRICE TYPE List", "Select PRICE TYPE from the list provided. It has been exported from your master data.");
		pricevalidation.setShowPromptBox(true);
		pricevalidation.setShowErrorBox(true);
		sheet.addValidationData(pricevalidation);
		workbook.setSheetHidden(1, true);
		workbook.setSheetHidden(2, true);

		for (int i = 0; i < 15; i++) {
			sheet.autoSizeColumn(i, true);
		}
	}

	private int buildRows(XSSFSheet sheet, int r, Bq bq, BqItem item) throws IOException {
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

			if (StringUtils.checkString(bq.getField1Label()).length() > 0)
				colNum++;
			if (StringUtils.checkString(bq.getField2Label()).length() > 0)
				colNum++;
			if (StringUtils.checkString(bq.getField3Label()).length() > 0)
				colNum++;
			if (StringUtils.checkString(bq.getField4Label()).length() > 0)
				colNum++;
			if (StringUtils.checkString(bq.getField5Label()).length() > 0)
				colNum++;
			if (StringUtils.checkString(bq.getField6Label()).length() > 0)
				colNum++;
			if (StringUtils.checkString(bq.getField7Label()).length() > 0)
				colNum++;
			if (StringUtils.checkString(bq.getField8Label()).length() > 0)
				colNum++;
			if (StringUtils.checkString(bq.getField9Label()).length() > 0)
				colNum++;
			if (StringUtils.checkString(bq.getField10Label()).length() > 0)
				colNum++;

			sheet.addMergedRegion(new CellRangeAddress(r - 1, r - 1, cellNum - 1, colNum));
		} else {
			row.createCell(cellNum++).setCellValue(item.getUom() != null ? item.getUom().getUom() : "");
			row.createCell(cellNum++).setCellValue(item.getQuantity() != null ? String.valueOf(item.getQuantity()) : "");
			row.createCell(cellNum++).setCellValue(item.getUnitPrice() != null ? String.valueOf(item.getUnitPrice()) : "");
			row.createCell(cellNum++).setCellValue(item.getPriceType() != null ? item.getPriceType().getValue() : "");
			if (StringUtils.checkString(bq.getField1Label()).length() > 0)
				row.createCell(cellNum++).setCellValue(item.getField1());
			if (StringUtils.checkString(bq.getField2Label()).length() > 0)
				row.createCell(cellNum++).setCellValue(item.getField2());
			if (StringUtils.checkString(bq.getField3Label()).length() > 0)
				row.createCell(cellNum++).setCellValue(item.getField3());
			if (StringUtils.checkString(bq.getField4Label()).length() > 0)
				row.createCell(cellNum++).setCellValue(item.getField4());

			if (StringUtils.checkString(bq.getField5Label()).length() > 0)
				row.createCell(cellNum++).setCellValue(item.getField5());
			if (StringUtils.checkString(bq.getField6Label()).length() > 0)
				row.createCell(cellNum++).setCellValue(item.getField6());
			if (StringUtils.checkString(bq.getField7Label()).length() > 0)
				row.createCell(cellNum++).setCellValue(item.getField7());
			if (StringUtils.checkString(bq.getField8Label()).length() > 0)
				row.createCell(cellNum++).setCellValue(item.getField8());
			if (StringUtils.checkString(bq.getField9Label()).length() > 0)
				row.createCell(cellNum++).setCellValue(item.getField9());
			if (StringUtils.checkString(bq.getField10Label()).length() > 0)
				row.createCell(cellNum++).setCellValue(item.getField10());
		}
		// Auto Fit
		for (int i = 0; i < 18; i++) {
			sheet.autoSizeColumn(i, true);
		}
		return r;
	}

	private void buildHeader(Bq bq, XSSFWorkbook workbook, XSSFSheet sheet) {
		Row rowHeading = sheet.createRow(0);
		// Style of Heading Cells
		CellStyle styleHeading = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		styleHeading.setFont(font);
		styleHeading.setVerticalAlignment(CellStyle.ALIGN_CENTER);

		int i = 0;
		Cell cell = null;
		for (String column : Global.BQ_EXCEL_COLUMNS_TYPE_1) {
			cell = rowHeading.createCell(i++);
			cell.setCellValue(column);
			cell.setCellStyle(styleHeading);
		}
		if (bq.getField1Label() != null) {
			cell = rowHeading.createCell(7);
			cell.setCellValue(bq.getField1Label());
			cell.setCellStyle(styleHeading);
		}
		if (bq.getField2Label() != null) {
			cell = rowHeading.createCell(8);
			cell.setCellValue(bq.getField2Label());
			cell.setCellStyle(styleHeading);
		}
		if (bq.getField3Label() != null) {
			cell = rowHeading.createCell(9);
			cell.setCellValue(bq.getField3Label());
			cell.setCellStyle(styleHeading);
		}
		if (bq.getField4Label() != null) {
			cell = rowHeading.createCell(10);
			cell.setCellValue(bq.getField4Label());
			cell.setCellStyle(styleHeading);
		}
		if (bq.getField5Label() != null) {
			cell = rowHeading.createCell(11);
			cell.setCellValue(bq.getField5Label());
			cell.setCellStyle(styleHeading);
		}
		if (bq.getField6Label() != null) {
			cell = rowHeading.createCell(12);
			cell.setCellValue(bq.getField6Label());
			cell.setCellStyle(styleHeading);
		}
		if (bq.getField7Label() != null) {
			cell = rowHeading.createCell(13);
			cell.setCellValue(bq.getField7Label());
			cell.setCellStyle(styleHeading);
		}
		if (bq.getField8Label() != null) {
			cell = rowHeading.createCell(14);
			cell.setCellValue(bq.getField8Label());
			cell.setCellStyle(styleHeading);
		}
		if (bq.getField9Label() != null) {
			cell = rowHeading.createCell(15);
			cell.setCellValue(bq.getField9Label());
			cell.setCellStyle(styleHeading);
		}
		if (bq.getField10Label() != null) {
			cell = rowHeading.createCell(16);
			cell.setCellValue(bq.getField10Label());
			cell.setCellStyle(styleHeading);
		}
	}

	protected int bqItemsUpload(MultipartFile file, String bqId, String eventId, RfxTypes eventType) throws IOException, ExcelParseException {
		File convFile = File.createTempFile(file.getOriginalFilename(), "xlsx");
		convFile.createNewFile();
		file.transferTo(convFile);
		int count = 0;
		switch (eventType) {
		case RFA:
			count = rfaBqService.uploadBqFile(bqId, eventId, convFile, SecurityLibrary.getLoggedInUserTenantId());
			break;
		case RFI:
			break;
		case RFP:
			count = rfpBqService.uploadBqFile(bqId, eventId, convFile, SecurityLibrary.getLoggedInUserTenantId());
			break;
		case RFQ:
			count = rfqBqService.uploadBqFile(bqId, eventId, convFile, SecurityLibrary.getLoggedInUserTenantId());
			break;
		case RFT:
			count = rftBqService.uploadBqFile(bqId, eventId, convFile, SecurityLibrary.getLoggedInUserTenantId());
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
		return count;
	}

	public void validateUploadBQItems(MultipartFile file) {
		LOG.info("++++++++++++file.getContentType()++++++++++++++" + file.getContentType());
		if (!file.getContentType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") && !file.getContentType().equals("application/wps-office.xlsx") && !file.getContentType().equals("application/vnd.ms-excel"))
			throw new MultipartException("Only excel files accepted!");
	}

	/**
	 * @param pageLength
	 */
	public void updateSecurityLibraryUser(Integer pageLength) {
		/*
		 * UPDATE THE SECURITY CONTEXT AS THE BQ Page Length IS NOW CHANGED.
		 */
		// gonna need this to get user from Acegi
		SecurityContext ctx = SecurityContextHolder.getContext();
		Authentication auth = ctx.getAuthentication();
		// get user obj
		AuthenticatedUser authUser = (AuthenticatedUser) auth.getPrincipal();
		// update the bq Page length on the user obj
		authUser.setBqPageLength(pageLength);
		UsernamePasswordAuthenticationToken upat = new UsernamePasswordAuthenticationToken(authUser, auth.getCredentials(), authUser.getAuthorities());
		upat.setDetails(auth.getDetails());
		ctx.setAuthentication(upat);
	}
}
package com.privasia.procurehere.web.controller;

import com.privasia.procurehere.core.entity.Event;
import com.privasia.procurehere.core.entity.RfaEventSor;
import com.privasia.procurehere.core.entity.RfaSorItem;
import com.privasia.procurehere.core.entity.RfiEventSor;
import com.privasia.procurehere.core.entity.RfiSorItem;
import com.privasia.procurehere.core.entity.RfpEventSor;
import com.privasia.procurehere.core.entity.RfpSorItem;
import com.privasia.procurehere.core.entity.RfqEventSor;
import com.privasia.procurehere.core.entity.RfqSorItem;
import com.privasia.procurehere.core.entity.RftEventSor;
import com.privasia.procurehere.core.entity.RftSorItem;
import com.privasia.procurehere.core.entity.Sor;
import com.privasia.procurehere.core.entity.SorItem;
import com.privasia.procurehere.core.enums.BqUserTypes;
import com.privasia.procurehere.core.enums.EventVisibilityType;
import com.privasia.procurehere.core.enums.PricingTypes;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.exceptions.ExcelParseException;
import com.privasia.procurehere.core.pojo.AuthenticatedUser;
import com.privasia.procurehere.core.pojo.BqItemPojo;
import com.privasia.procurehere.core.pojo.BqPojo;
import com.privasia.procurehere.core.pojo.SorPojo;
import com.privasia.procurehere.core.pojo.UomPojo;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.RfaEventService;
import com.privasia.procurehere.service.RfaSorService;
import com.privasia.procurehere.service.RfiEventService;
import com.privasia.procurehere.service.RfiSorService;
import com.privasia.procurehere.service.RfpBqService;
import com.privasia.procurehere.service.RfpEventService;
import com.privasia.procurehere.service.RfpSorService;
import com.privasia.procurehere.service.RfqEventService;
import com.privasia.procurehere.service.RfqSorService;
import com.privasia.procurehere.service.RftEventService;
import com.privasia.procurehere.service.RftSorService;
import com.privasia.procurehere.service.UomService;
import com.privasia.procurehere.service.UserService;
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
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class EventSorBase {

    protected static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);
    protected Validator validator = Validation.buildDefaultValidatorFactory().getValidator();


    @Autowired
    ServletContext context;

    @Autowired
    UserService userService;

    @Autowired
    RfqSorService rfqSorService;

    @Autowired
    RfpEventService rfpEventService;

    @Autowired
    RfaEventService rfaEventService;

    @Autowired
    RfiEventService rfiEventService;

    @Autowired
    RfqEventService rfqEventService;

    @Autowired
    RfpSorService rfpSorService;

    @Autowired
    RfaSorService rfaSorService;

    @Autowired
    UomService uomService;


    @Autowired
    MessageSource messageSource;

    @Autowired
    RftEventService rftEventService;

    @Autowired
    RftSorService rftSorService;

    @Autowired
    RfiSorService rfiSorService;

    private RfxTypes eventType;

    public EventSorBase(RfxTypes eventType) {
        this.eventType = eventType;
    }

    @ModelAttribute("step")
    public String getStep() {
        return "7";
    }

    @ModelAttribute("eventType")
    public RfxTypes getEventType() {
        return eventType;
    }

    public void setEventType(RfxTypes eventType) {
        this.eventType = eventType;
    }

    public List<Sor> saveSor(SorPojo sorPojo, Integer count) {
        System.out.println(sorPojo);
        List<Sor> list = null;
        switch (getEventType()) {
            case RFI:
                RfiEventSor rfiEventSor = new RfiEventSor();
                rfiEventSor.setRfxEvent(rfiEventService.getRfiEventById(sorPojo.getEventId()));
                rfiEventSor.setId(sorPojo.getId());
                rfiEventSor.setName(sorPojo.getSorName());
                rfiEventSor.setDescription(sorPojo.getSorDesc());
                rfiEventSor.setCreatedDate(new Date());
                rfiEventSor.setSorOrder(count);
                rfiSorService.saveRfiSor(rfiEventSor);
                list = rfiSorService.findSorbyEventId(sorPojo.getEventId());
                break;
            case RFT:
                RftEventSor rftEventSor = new RftEventSor();
                rftEventSor.setRfxEvent(rftEventService.getEventById(sorPojo.getEventId()));
                rftEventSor.setId(sorPojo.getId());
                rftEventSor.setName(sorPojo.getSorName());
                rftEventSor.setDescription(sorPojo.getSorDesc());
                rftEventSor.setCreatedDate(new Date());
                rftEventSor.setSorOrder(count);
                rftSorService.saveRftSor(rftEventSor);
                list = rftSorService.findSorbyEventId(sorPojo.getEventId());
                break;
            case RFQ:
                RfqEventSor rfqEventSor = new RfqEventSor();
                rfqEventSor.setRfxEvent(rfqEventService.getEventById(sorPojo.getEventId()));
                rfqEventSor.setId(sorPojo.getId());
                rfqEventSor.setName(sorPojo.getSorName());
                rfqEventSor.setDescription(sorPojo.getSorDesc());
                rfqEventSor.setCreatedDate(new Date());
                rfqEventSor.setSorOrder(count);
                rfqSorService.saveRfqSor(rfqEventSor);
                list = rfqSorService.findSorbyEventId(sorPojo.getEventId());
                break;
            case RFP:
                RfpEventSor rfpEventBq = new RfpEventSor();
                rfpEventBq.setRfxEvent(rfpEventService.getEventById(sorPojo.getEventId()));
                rfpEventBq.setId(sorPojo.getId());
                rfpEventBq.setName(sorPojo.getSorName());
                rfpEventBq.setDescription(sorPojo.getSorDesc());
                rfpEventBq.setCreatedDate(new Date());
                rfpEventBq.setSorOrder(count);
                rfpSorService.saveRfpSor(rfpEventBq);
                list = rfpSorService.findSorbyEventId(sorPojo.getEventId());
                break;
            case RFA:
                RfaEventSor rfaEventBq = new RfaEventSor();
                rfaEventBq.setRfxEvent(rfaEventService.getRfaEventById(sorPojo.getEventId()));
                rfaEventBq.setId(sorPojo.getId());
                rfaEventBq.setName(sorPojo.getSorName());
                rfaEventBq.setDescription(sorPojo.getSorDesc());
                rfaEventBq.setCreatedDate(new Date());
                rfaEventBq.setSorOrder(count);
                rfaSorService.saveRfaSor(rfaEventBq);
                list = rfpSorService.findSorbyEventId(sorPojo.getEventId());
                break;
            default:
                break;
        }

        return list;
    }

    protected void buildModel(String eventId, String bqId, Model model, Event rftEvent, Sor eventSor) {
        List<UomPojo> uomList = uomService.getAllUomPojo(SecurityLibrary.getLoggedInUserTenantId());
        PricingTypes[] pricingTypes = PricingTypes.values();
        BqUserTypes[] BqFilledBy = BqUserTypes.values();
        model.addAttribute("pricingTypes", pricingTypes);
        model.addAttribute("bqFilledBy", BqFilledBy);
        model.addAttribute("eventId", eventId);
        model.addAttribute("bqId", bqId);
        model.addAttribute("uomList", uomList);
        model.addAttribute("event", rftEvent);
        model.addAttribute("eventBq", eventSor);
        model.addAttribute("bqName", eventSor.getName());
    }

    protected void constructSorItemValues(BqItemPojo rftBqItemPojo, SorItem rftBqItem) {
        rftBqItem.setItemDescription(rftBqItemPojo.getItemDescription());
        rftBqItem.setItemName(rftBqItemPojo.getItemName());
        if (StringUtils.checkString(rftBqItemPojo.getUom()).length() > 0) {
            rftBqItem.setUom(uomService.getUombyCode(rftBqItemPojo.getUom(), SecurityLibrary.getLoggedInUserTenantId()));
        }

        rftBqItem.setField1(rftBqItemPojo.getField1() != null ? rftBqItemPojo.getField1() : null);
    }

    protected void buildUpdateBqItem(BqItemPojo bqPojo, SorItem bqItem) {
        bqItem.setItemName(bqPojo.getItemName());
        bqItem.setUom(bqItem.getUom());
        bqItem.setItemDescription(bqPojo.getItemDescription());

        bqItem.setField1(bqPojo.getField1());
    }

    protected void constructBqItemValues(BqItemPojo rftBqItemPojo, SorItem rftBqItem) {
        rftBqItem.setItemDescription(rftBqItemPojo.getItemDescription());
        rftBqItem.setItemName(rftBqItemPojo.getItemName());
        if (StringUtils.checkString(rftBqItemPojo.getUom()).length() > 0) {
            rftBqItem.setUom(uomService.getUombyCode(rftBqItemPojo.getUom(), SecurityLibrary.getLoggedInUserTenantId()));
        }

        rftBqItem.setField1(rftBqItemPojo.getField1() != null ? rftBqItemPojo.getField1() : null);
    }

    public String sorNext(Event rftEvent) {
        return "redirect:envelopList/" + rftEvent.getId();
    }

    protected String sorPrevious(Event rftEvent) {
        if (Boolean.TRUE == rftEvent.getBillOfQuantity()) {
            return "redirect:createBQList/" + rftEvent.getId();
        } else if (Boolean.TRUE == rftEvent.getQuestionnaires()) {
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

    protected void eventDownloader(XSSFWorkbook workbook, String bqId, RfxTypes eventType) throws IOException {
        XSSFSheet sheet = workbook.createSheet("SOR Item List");
        int r = 1;
        Sor bq = null;
        switch (eventType) {
            case RFA:
                List<RfaSorItem> rfaBqList = rfaSorService.getAllSoritemsbySorId(bqId);

                // Creating Headings
                bq = rfaSorService.getSorById(bqId);
                buildHeader(bq, workbook, sheet);

                // Write Data into Excel
                for (RfaSorItem item : rfaBqList) {
                    r = buildRows(sheet, r, item.getSor(), item);
                }
                break;
            case RFT:
                List<RftSorItem> rftBqList = rftSorService.getAllSoritemsbySorId(bqId);

                // Creating Headings
                bq = rftSorService.getSorById(bqId);
                buildHeader(bq, workbook, sheet);

                // Write Data into Excel
                for (RftSorItem item : rftBqList) {
                    r = buildRows(sheet, r, item.getSor(), item);
                }
                break;
            case RFP:
                List<RfpSorItem> rfpBqList = rfpSorService.getAllSoritemsbySorId(bqId);

                // Creating Headings
                bq = rfpSorService.getSorById(bqId);
                buildHeader(bq, workbook, sheet);

                // Write Data into Excel
                for (RfpSorItem item : rfpBqList) {
                    r = buildRows(sheet, r, item.getSor(), item);
                }
                break;
            case RFQ:
                List<RfqSorItem> rfqBqList = rfqSorService.getAllSoritemsbyBqId(bqId);

                // Creating Headings
                bq = rfqSorService.getSorById(bqId);
                buildHeader(bq, workbook, sheet);

                // Write Data into Excel
                for (RfqSorItem item : rfqBqList) {
                    r = buildRows(sheet, r, item.getSor(), item);
                }
                break;
            case RFI:
                List<RfiSorItem> rfiSorList = rfiSorService.getAllSoritemsbySorId(bqId);

                // Creating Headings
                bq = rfiSorService.getSorById(bqId);
                buildHeader(bq, workbook, sheet);

                // Write Data into Excel
                for (RfiSorItem item : rfiSorList) {
                    r = buildRows(sheet, r, item.getSor(), item);
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

    private void buildHeader(Sor bq, XSSFWorkbook workbook, XSSFSheet sheet) {
        Row rowHeading = sheet.createRow(0);
        // Style of Heading Cells
        CellStyle styleHeading = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
        styleHeading.setFont(font);
        styleHeading.setVerticalAlignment(CellStyle.ALIGN_CENTER);

        int i = 0;
        Cell cell = null;
        for (String column : Global.SOR_EXCEL_COLUMNS_TYPE_RFX) {
            cell = rowHeading.createCell(i++);
            cell.setCellValue(column);
            cell.setCellStyle(styleHeading);
        }
        if (bq.getField1Label() != null) {
            cell = rowHeading.createCell(4);
            cell.setCellValue(bq.getField1Label());
            cell.setCellStyle(styleHeading);
        }
    }

    private int buildRows(XSSFSheet sheet, int r, Sor bq, SorItem item) throws IOException {
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

            sheet.addMergedRegion(new CellRangeAddress(r - 1, r - 1, cellNum - 1, colNum));
        } else {
            row.createCell(cellNum++).setCellValue(item.getUom() != null ? item.getUom().getUom() : "");
            if (StringUtils.checkString(bq.getField1Label()).length() > 0)
                row.createCell(cellNum++).setCellValue(item.getField1());
        }
        // Auto Fit
        for (int i = 0; i < 18; i++) {
            sheet.autoSizeColumn(i, true);
        }
        return r;
    }

    public void validateUploadSorItems(MultipartFile file) {
        LOG.info("++++++++++++file.getContentType()++++++++++++++" + file.getContentType());
        if (!file.getContentType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") && !file.getContentType().equals("application/wps-office.xlsx") && !file.getContentType().equals("application/vnd.ms-excel"))
            throw new MultipartException("Only excel files accepted!");
    }

    protected int sorItemsUpload(MultipartFile file, String bqId, String eventId, RfxTypes eventType) throws IOException, ExcelParseException {
        File convFile = File.createTempFile(file.getOriginalFilename(), "xlsx");
        convFile.createNewFile();
        file.transferTo(convFile);
        int count = 0;
        switch (eventType) {
            case RFP:
                count = rfpSorService.uploadSorFile(bqId, eventId, convFile, SecurityLibrary.getLoggedInUserTenantId());
                break;
            case RFQ:
                count = rfqSorService.uploadSorFile(bqId, eventId, convFile, SecurityLibrary.getLoggedInUserTenantId());
                break;
            case RFA:
                count = rfaSorService.uploadSorFile(bqId, eventId, convFile, SecurityLibrary.getLoggedInUserTenantId());
                break;
            case RFT:
                count = rftSorService.uploadSorFile(bqId, eventId, convFile, SecurityLibrary.getLoggedInUserTenantId());
                break;
            case RFI:
                count = rfiSorService.uploadSorFile(bqId, eventId, convFile, SecurityLibrary.getLoggedInUserTenantId());
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

    protected boolean sorValidate(SorPojo rftBqPojo) {
        boolean validate = true;
        switch (getEventType()) {
            case RFT:
                if (rftSorService.isSorExists(rftBqPojo)) {
                    validate = false;
                }
                break;
            case RFP:
                if (rfpSorService.isSorExists(rftBqPojo)) {
                    validate = false;
                }
                break;
            case RFA:
                if (rfaSorService.isSorExists(rftBqPojo)) {
                    validate = false;
                }
                break;
            case RFQ:
                if (rfqSorService.isSorExists(rftBqPojo)) {
                    validate = false;
                }
                break;
            case RFI:
                if (rfiSorService.isSorExists(rftBqPojo)) {
                    validate = false;
                }
                break;
            default:
                break;
        }

        return validate;
    }

    protected void buildAddNewColumns(SorPojo bqPojo, Sor eventBq) {
        eventBq.setField1Label(bqPojo.getField1Label() != null ? bqPojo.getField1Label() : null);
        eventBq.setField1FilledBy(bqPojo.getField1FilledBy() != null ? bqPojo.getField1FilledBy() : null);
        eventBq.setField1ToShowSupplier(bqPojo.isField1ToShowSupplier());
        eventBq.setField1Required(bqPojo.isField1Required());
    }
}

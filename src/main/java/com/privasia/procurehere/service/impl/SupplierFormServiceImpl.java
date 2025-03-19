package com.privasia.procurehere.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.SupplierFormDao;
import com.privasia.procurehere.core.dao.SupplierFormItemAttachmentDao;
import com.privasia.procurehere.core.dao.SupplierFormItemDao;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.SupplierForm;
import com.privasia.procurehere.core.entity.SupplierFormApproval;
import com.privasia.procurehere.core.entity.SupplierFormApprovalUser;
import com.privasia.procurehere.core.entity.SupplierFormItem;
import com.privasia.procurehere.core.entity.SupplierFormItemAttachment;
import com.privasia.procurehere.core.entity.SupplierFormItemOption;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.CqType;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.enums.SupplierFormsStatus;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.SupplierFormItemPojo;
import com.privasia.procurehere.core.pojo.SupplierFormPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.SupplierFormService;

@Service
@Transactional(readOnly = true)
public class SupplierFormServiceImpl implements SupplierFormService {

	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@Autowired
	SupplierFormDao supplierFormDao;

	@Autowired
	ServletContext context;

	@Autowired
	MessageSource messageSource;

	@Autowired
	SupplierFormItemDao supplierFormItemDao;

	@Autowired
	SupplierFormItemAttachmentDao supplierFormItemAttachmentDao;
	
	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;

	@Override
	public List<SupplierFormPojo> findSupplierFormsByTeantId(String loggedInUserTenantId, TableDataInput input) {
		return supplierFormDao.findSupplierFormsByTeantId(loggedInUserTenantId, input);
	}

	@Override
	public long findTotalFilteredSupplierFormsForTenant(String loggedInUserTenantId, TableDataInput input) {
		return supplierFormDao.findTotalFilteredSupplierFormsForTenant(loggedInUserTenantId, input);
	}

	@Override
	public long findTotalActiveSupplierFormsForTenant(String loggedInUserTenantId) {
		return supplierFormDao.findTotalFilteredSupplierFormsForTenant(loggedInUserTenantId);
	}

	@Override
	public SupplierForm getSupplierFormById(String formId) {
		SupplierForm supplierForm = supplierFormDao.findById(formId);
		if (supplierForm != null) {
			if (CollectionUtil.isNotEmpty(supplierForm.getApprovals())) {
				for (SupplierFormApproval approval : supplierForm.getApprovals()) {
					approval.getLevel();
					if (CollectionUtil.isNotEmpty(approval.getApprovalUsers())) {
						for (SupplierFormApprovalUser approvalUser : approval.getApprovalUsers()) {
							approvalUser.getApproval();
							approvalUser.getUser().getLoginId();
						}
					}
				}
			}
		}
		return supplierForm;
	}

	@Override
	public boolean isExists(SupplierFormItem supplierFormItem, String formId, String parentId) {
		return supplierFormItemDao.isExists(supplierFormItem, formId, parentId);
	}

	@Override
	@Transactional(readOnly = false)
	public SupplierFormItem saveSupplierFormItem(SupplierFormItem item) {
		if (item.getParent() == null) {
			int itemLevel = 0;
			LOG.info("supplier form id:" + item.getSupplierForm().getId());
			List<SupplierFormItem> list = supplierFormItemDao.getFormItemLevelOrder(item.getSupplierForm().getId());
			if (CollectionUtil.isNotEmpty(list)) {
				itemLevel = list.size();
			}
			item.setLevel(itemLevel + 1);
			item.setOrder(0);
		} else {
			LOG.info("PARENT : " + item.getParent().getId());
			if (item.getParent() != null) {
				item.setLevel(item.getParent().getLevel());
				item.setOrder(CollectionUtil.isEmpty(item.getParent().getChildren()) ? 1 : item.getParent().getChildren().size() + 1);
			}
		}
		List<SupplierFormItemAttachment> itemDocs = new ArrayList<SupplierFormItemAttachment>();

		if (CollectionUtil.isNotEmpty(item.getFileAttachList())) {
			for (MultipartFile file : item.getFileAttachList()) {
				LOG.info("FILE Name : " + file.getOriginalFilename());
				byte[] bytes;
				try {
					bytes = file.getBytes();
					if (bytes.length > 0) {
						SupplierFormItemAttachment document = new SupplierFormItemAttachment();
						document.setContentType(file.getContentType());
						document.setFileName(file.getOriginalFilename());
						document.setFileData(bytes);
						document.setFormItem(item);
						document.setTenantId(SecurityLibrary.getLoggedInUserTenantId());
						itemDocs.add(document);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if (CollectionUtil.isNotEmpty(itemDocs)) {
			item.setFormItemAttachments(itemDocs);
		}
		SupplierFormItem supplierFormitem = supplierFormItemDao.saveOrUpdate(item);
		// Validate Form items inside section
		List<String> notItemSectionAddedForms = supplierFormItemDao.getNotSectionItemAddedByFormId(item.getSupplierForm().getId());
		if (CollectionUtil.isNotEmpty(notItemSectionAddedForms)) {
			SupplierForm supplierForm = supplierFormDao.findById(item.getSupplierForm().getId());
			if (supplierForm != null) {
				supplierForm.setStatus(SupplierFormsStatus.DRAFT);
				supplierFormDao.saveOrUpdate(supplierForm);
			}

		}

		return supplierFormitem;
	}

	@Override
	public SupplierFormItem getFormItembyFormItemId(String itemId) {
		SupplierFormItem item = supplierFormItemDao.getFormItembyFormItemId(itemId);
		if (item != null && CollectionUtil.isNotEmpty(item.getFormOptions())) {
			for (SupplierFormItemOption option : item.getFormOptions()) {
				option.getValue();
			}
		}
		if (item != null && CollectionUtil.isNotEmpty(item.getFormItemAttachments())) {
			for (SupplierFormItemAttachment attachMent : item.getFormItemAttachments()) {
				if (attachMent.getFormItem() != null) {
					attachMent.getFormItem().getItemName();
				}
				attachMent.getFileName();
				attachMent.getContentType();
				attachMent.getFileData();
				attachMent.getTenantId();
			}
		}
		return item;
	}

	@Override
	public List<SupplierFormItem> findFormItembyFormId(String formId) {
		List<SupplierFormItem> returnList = new ArrayList<SupplierFormItem>();
		List<SupplierFormItem> list = supplierFormItemDao.getFormItemsbyFormId(formId);
		bulidFormItemList(returnList, list);
		return returnList;
	}

	private void bulidFormItemList(List<SupplierFormItem> returnList, List<SupplierFormItem> list) {
		if (CollectionUtil.isNotEmpty(list)) {
			for (SupplierFormItem item : list) {
				SupplierFormItem parent = item.createShallowCopy();
				returnList.add(parent);
				if (CollectionUtil.isNotEmpty(item.getChildren())) {
					for (SupplierFormItem child : item.getChildren()) {
						if (parent.getChildren() == null) {
							parent.setChildren(new ArrayList<SupplierFormItem>());
						}
						parent.getChildren().add(child.createShallowCopy());
					}
				}
			}
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void updateFormItem(SupplierFormItemPojo formItemPojo) {
		SupplierFormItem item = getFormItembyFormItemId(formItemPojo.getId());
		LOG.info("ITEM : " + item.getItemName());
		item.setItemName(formItemPojo.getItemName());
		item.setItemDescription(formItemPojo.getItemDescription());
		item.setAttachment(formItemPojo.isAttachment());
		item.setOptional(formItemPojo.isOptional());
		item.setIsSupplierAttachRequired(formItemPojo.getIsSupplierAttachRequired());
		item.setCqType(formItemPojo.getCqType() != null ? CqType.valueOf(formItemPojo.getCqType()) : null);
		if (CollectionUtil.isNotEmpty(formItemPojo.getOptions())) {
			List<SupplierFormItemOption> optionItems = new ArrayList<SupplierFormItemOption>();
			int optionOrder = 0;
			for (String option : formItemPojo.getOptions()) {
				if (StringUtils.checkString(option).length() == 0)
					continue;
				SupplierFormItemOption options = new SupplierFormItemOption();
				options.setSupplierFormItem(item);
				options.setValue(option);
				options.setOrder(++optionOrder);
				if (CollectionUtil.isNotEmpty(formItemPojo.getOptionScore())) {
					options.setScoring(Integer.parseInt(formItemPojo.getOptionScore().get(optionOrder - 1)));
				}
				options.setTenantId(item.getTenantId());
				optionItems.add(options);
			}
			item.setFormOptions(optionItems);
		}
		List<SupplierFormItemAttachment> itemDocs = new ArrayList<SupplierFormItemAttachment>();

		if (CollectionUtil.isNotEmpty(formItemPojo.getItemAttachFiles())) {
			for (MultipartFile file : formItemPojo.getItemAttachFiles()) {
				LOG.info("FILE Name : " + file.getOriginalFilename());
				byte[] bytes;
				try {
					bytes = file.getBytes();
					if (bytes.length > 0) {
						SupplierFormItemAttachment document = new SupplierFormItemAttachment();
						document.setContentType(file.getContentType());
						document.setFileName(file.getOriginalFilename());
						document.setFileData(bytes);
						document.setFormItem(item);
						document.setTenantId(SecurityLibrary.getLoggedInUserTenantId());
						itemDocs.add(document);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if (CollectionUtil.isNotEmpty(itemDocs)) {
			item.getFormItemAttachments().addAll(itemDocs);

		}
		supplierFormItemDao.saveOrUpdate(item);
	}

	@Override
	@Transactional(readOnly = false)
	public SupplierForm saveOrUpdate(SupplierForm supplierFormObj) {
		return supplierFormDao.saveOrUpdate(supplierFormObj);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteFormItems(String[] items, String formId, boolean isFormitemEmpy) throws NotAllowedException {
		supplierFormItemDao.deleteFormItems(items, formId);
		List<SupplierFormItem> formItemList = findFormItembyFormId(formId);
		if (CollectionUtil.isEmpty(formItemList)) {
			isFormitemEmpy = true;
		} else {
			// Validate Form items inside section
			List<String> notItemSectionAddedForms = supplierFormItemDao.getNotSectionItemAddedByFormId(formId);
			if (CollectionUtil.isNotEmpty(notItemSectionAddedForms)) {
				isFormitemEmpy = true;
			}
		}
		if (isFormitemEmpy) {
			SupplierForm supplierForm = supplierFormDao.findById(formId);
			if (supplierForm != null) {
				supplierForm.setStatus(SupplierFormsStatus.DRAFT);
				supplierFormDao.saveOrUpdate(supplierForm);
			}

		}

	}

	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
	public String doExcelDataSave(Map<Integer, Map<Integer, SupplierFormItem>> dataMap, String formId, String tenantId) throws NotAllowedException {
		SupplierForm form = getFormById(formId);

		for (SupplierFormItem item : form.getFormItems()) {
			if (item.getOrder() == 0) {
				item.setSupplierForm(null);
				supplierFormItemDao.delete(item);
			}
		}
		if (form.getFormItems() != null) {
			form.getFormItems().clear();
			supplierFormDao.update(form);
		}
		// checking item exists validation
		int levelTemp = 1;
		int orderTemp = 0;
		boolean isItemExists = false;
		String message = null;

		int rowNum = 2;
		for (Map.Entry<Integer, Map<Integer, SupplierFormItem>> entry : dataMap.entrySet()) {
			SupplierFormItem parent = null;
			for (Map.Entry<Integer, SupplierFormItem> cqItem : entry.getValue().entrySet()) {
				cqItem.getValue().setTenantId(form.getTenantId());
				cqItem.getValue().setParent(parent);
				if (cqItem.getValue().getOrder() == 0) {
					parent = cqItem.getValue();
				} else {
					cqItem.getValue().setFormOptions(new ArrayList<>());
					int index = 1;
					for (String option : cqItem.getValue().getOptions()) {
						SupplierFormItemOption rOption = new SupplierFormItemOption();
						if (option.indexOf('/') == 1 || option.indexOf('/') == 2) {
							String score = option.substring(0, option.indexOf('/'));
							option = option.substring(option.indexOf('/') + 1, option.length());
							try {
								rOption.setScoring(Integer.parseInt(score));
							} catch (Exception e) {
								LOG.info("Error while saving options :" + e.getMessage());
								throw new NotAllowedException("Only Numbers Allowed in score before option '" + option + "' should be start with a Number. eg:'1/" + option + "' at row Number :" + rowNum);
							}
						}
						rOption.setSupplierFormItem(cqItem.getValue());
						rOption.setValue(option);
						rOption.setOrder(index++);
						rOption.setTenantId(form.getTenantId());
						cqItem.getValue().getFormOptions().add(rOption);
					}
				}
				cqItem.getValue().setSupplierForm(form);
				LOG.info("Saving Item Sr No : " + entry.getKey() + " . " + cqItem.getKey());

				// checking item exists validation
				if (levelTemp != entry.getKey() && orderTemp == 0) {
					isItemExists = true;
					LOG.info("add sub items at row number : " + rowNum);
					levelTemp = entry.getKey();
					orderTemp = cqItem.getKey();
				} else {
					levelTemp = entry.getKey();
					orderTemp = cqItem.getKey();
				}

				supplierFormItemDao.save(cqItem.getValue());
				rowNum++;
			}
		}

		if (isItemExists) {
			message = messageSource.getMessage("common.upload.warning", new Object[] {}, Global.LOCALE);
		}
		return message;
	}

	@Override
	public SupplierForm getFormById(String id) {
		return supplierFormDao.getFormById(id);
	}

	@Override
	public List<SupplierFormItem> getAllFormitemsbyFormId(String formId) {
		List<SupplierFormItem> itemList = supplierFormItemDao.getAllFormitemsbyFormId(formId);
		for (SupplierFormItem formItem : itemList) {
			if (CollectionUtil.isNotEmpty(formItem.getFormOptions()))
				for (SupplierFormItemOption cqOption : formItem.getFormOptions()) {
					cqOption.getValue();
				}
		}
		return itemList;
	}

	@Override
	public List<SupplierForm> getSupplierFormListByTenantId(String loggedInUserTenantId) {
		return supplierFormDao.getSupplierFormListByTenantId(loggedInUserTenantId);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteSupplierForm(SupplierForm supplierFormObj) {
		try {
			LOG.info("--------------------------BEFORE AUDIT---------------------------------------");
			BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.DELETE, "Supplier Form '"+supplierFormObj.getName()+ "' is deleted", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.SupplierForm);
			buyerAuditTrailDao.save(buyerAuditTrail);
			LOG.info("--------------------------AFTER AUDIT---------------------------------------");
		} catch (Exception e) {
			LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
		}
		supplierFormDao.delete(supplierFormObj);
	}

	@Override
	public boolean isFormNameExists(SupplierForm supplierFormObj, String loggedInUserTenantId) {
		return supplierFormDao.isFormNameExists(supplierFormObj, loggedInUserTenantId);
	}

	@Override
	public void supplierFormDownloader(XSSFWorkbook workbook, String formId) {
		XSSFSheet sheet = workbook.createSheet("Form Item List");
		// Creating Headings
		buildHeader(workbook, sheet);

		int r = 1;
		List<SupplierFormItem> formItemList = getAllFormitemsbyFormId(formId);
		// Write Data into Excel
		for (SupplierFormItem item : formItemList) {
			r = buildRows(sheet, r, item.getSupplierForm(), item);
		}

		XSSFSheet lookupSheet = workbook.createSheet("LOOKUP");
		XSSFRow firstRow = null;
		XSSFRow secondRow = null;

		int index = 0;
		ArrayList<CqType> cqTypeList = new ArrayList<CqType>();
		List<CqType> cqTypesvalues = Arrays.asList(CqType.CHOICE, CqType.CHOICE_WITH_SCORE, CqType.CHECKBOX, CqType.LIST, CqType.DATE, CqType.NUMBER, CqType.PARAGRAPH);
		for (CqType cqType : cqTypesvalues) {
			/*
			 * if (cqType == CqType.DATE || cqType == CqType.DATE) { continue; }
			 */
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

	private void buildHeader(XSSFWorkbook workbook, XSSFSheet sheet) {
		Row rowHeading = sheet.createRow(0);
		CellStyle styleHeading = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		styleHeading.setFont(font);
		styleHeading.setAlignment(CellStyle.ALIGN_CENTER);

		int i = 0;
		List<String> option = Arrays.asList(Global.FORM_EXCEL_OPTION_COLUMNS);
		for (String column : Global.FORM_EXCEL_COLUMNS) {
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

	private int buildRows(XSSFSheet sheet, int r, SupplierForm supplierForm, SupplierFormItem item) {
		Row row = sheet.createRow(r++);
		int cellNum = 0;

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

			for (SupplierFormItemOption option : item.getFormOptions()) {
				row.createCell(cellNum++).setCellValue((option.getScoring() != null ? option.getScoring() + "/" : "") + option.getValue());
			}

		}
		return r;

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void reorderFormItem(SupplierFormItemPojo supplierFormItemPojo) throws NotAllowedException {
		SupplierFormItem formItem = getFormItembyFormItemId(supplierFormItemPojo.getId());

		if (CollectionUtil.isNotEmpty(formItem.getChildren()) && supplierFormItemPojo.getParent() != null) {
			throw new NotAllowedException("Form Item cannot be made a child if it has sub items");
		}

		int newOrder = supplierFormItemPojo.getOrder();
		int newLevel = supplierFormItemPojo.getOrder();
		int oldOrder = formItem.getOrder();
		int oldLevel = formItem.getLevel();

		SupplierFormItem newParent = null;
		if (supplierFormItemPojo.getParent() != null && StringUtils.checkString(supplierFormItemPojo.getParent()).length() > 0) {
			newParent = getFormItembyFormItemId(supplierFormItemPojo.getParent());
		}

		SupplierFormItem oldParent = formItem.getParent();

		if (oldParent == null) {
			oldOrder = 0;
		}
		if (newParent == null) {
			newOrder = 0;
		}

		formItem.setOrder(newOrder);
		formItem.setLevel(newParent == null ? supplierFormItemPojo.getOrder() : 0);
		formItem.setParent(newParent);
		supplierFormItemDao.updateItemOrder(formItem.getSupplierForm().getId(), formItem, oldParent == null ? null : oldParent.getId(), newParent == null ? null : newParent.getId(), oldOrder, newOrder, oldLevel, newLevel);

	}

	@Override
	public List<String> getNotSectionItemAddedByFormId(String formId) {
		return supplierFormItemDao.getNotSectionItemAddedByFormId(formId);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteFormItemDoc(SupplierFormItemAttachment formObj) {
		supplierFormItemAttachmentDao.delete(formObj);
	}

	@Override
	@Transactional(readOnly = false)
	public SupplierForm saveSupplierForm(SupplierForm supplierFormObj, User loggedInUser) {
		if (supplierFormObj.getId().length() == 0) {
			LOG.info("Saving Form:" + supplierFormObj.getName());
			supplierFormObj.setCreatedBy(loggedInUser);
			supplierFormObj.setCreatedDate(new Date());
			supplierFormObj.setTenantId(loggedInUser.getTenantId());
			supplierFormObj.setPendingCount((long) 0);
			supplierFormObj.setSubmittedCount((long) 0);
			supplierFormObj.setAcceptedCount((long) 0);
			supplierFormObj.setStatus(supplierFormObj.getStatus());
			supplierFormObj = supplierFormDao.save(supplierFormObj);
			
			return supplierFormObj;
		} else {
			LOG.info("Updating form:" + supplierFormObj.getName());
			SupplierForm persistObj = supplierFormDao.findById(supplierFormObj.getId());
			if (persistObj != null) {
				boolean doAuditStatusChange = supplierFormObj.getStatus() != persistObj.getStatus();
				persistObj.setName(supplierFormObj.getName());
				persistObj.setDescription(supplierFormObj.getDescription());
				persistObj.setStatus(supplierFormObj.getStatus());
				persistObj.setModifiedBy(SecurityLibrary.getLoggedInUser());
				persistObj.setModifiedDate(new Date());
				persistObj.setTenantId(SecurityLibrary.getLoggedInUserTenantId());
				persistObj.setStatus(supplierFormObj.getStatus());
				if (CollectionUtil.isNotEmpty(supplierFormObj.getApprovals())) {
					int level = 1;
					for (SupplierFormApproval app : supplierFormObj.getApprovals()) {
						LOG.info("Approval not empty");
						app.setSupplierForm(persistObj);
						app.setLevel(level++);
						if (CollectionUtil.isNotEmpty(app.getApprovalUsers())) {
							for (SupplierFormApprovalUser approvalUser : app.getApprovalUsers()) {
								LOG.info("approver User name:" + approvalUser.getUser().getLoginId());
								approvalUser.setApproval(app);
								approvalUser.setId(null);
							}
						}
					}
				}
				if (CollectionUtil.isNotEmpty(supplierFormObj.getApprovals())) {
					persistObj.setApprovals(supplierFormObj.getApprovals());
				} else {
					persistObj.setApprovals(new ArrayList<SupplierFormApproval>());
				}
				
				persistObj = supplierFormDao.update(persistObj);
				
				if(doAuditStatusChange) {
					if(supplierFormObj.getStatus() == SupplierFormsStatus.DRAFT) {
						try {
							BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "Supplier Form  '" + supplierFormObj.getName() + "' status is changed to DRAFT", loggedInUser.getTenantId() , loggedInUser, new Date(), ModuleType.SupplierForm);
							buyerAuditTrailDao.save(buyerAuditTrail);
						} catch (Exception e) {
							LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
						}
					} else {
						try {
							BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(supplierFormObj.getStatus() == SupplierFormsStatus.ACTIVE ? AuditTypes.ACTIVE : AuditTypes.INACTIVE, "Supplier Form'" + supplierFormObj.getName() + "' status changed to " + supplierFormObj.getStatus(), loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.SupplierForm);
							buyerAuditTrailDao.save(buyerAuditTrail);
						} catch (Exception e) {
							LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
						}
					}
				} else {
					try {
						BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.UPDATE, "Supplier Form '"+ supplierFormObj.getName() +"' is updated", loggedInUser.getTenantId() , loggedInUser, new Date(), ModuleType.SupplierForm);
						buyerAuditTrailDao.save(buyerAuditTrail);
					} catch (Exception e) {
						LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
					}
				}
				
			}
			return persistObj;
		}
	}

	@Override
	public SupplierForm getSupplierFormByTenantAndId(String formId, String loggedInUserTenantId) {
		return supplierFormDao.getSupplierFormByTenantAndId(formId, loggedInUserTenantId);
	}

	@Override
	@Transactional(readOnly = false)
	public SupplierForm copySupplierForm(SupplierForm oldSupplierForm, String formName, User loggedInUser, String formDesc) {
		oldSupplierForm = supplierFormDao.findById(oldSupplierForm.getId());
		SupplierForm newDbSupplierForm = null;
		if (oldSupplierForm != null) {
			// copy Form Detail
			SupplierForm newSupplierForm = oldSupplierForm.copyFormDetails(oldSupplierForm, loggedInUser.getTenantId());
			newSupplierForm.setName(formName);
			if (StringUtils.checkString(formDesc).length() > 0) {
				newSupplierForm.setDescription(formDesc);
			}
			newSupplierForm.setCreatedBy(loggedInUser);
			newSupplierForm.setTenantId(loggedInUser.getTenantId());
			newSupplierForm.setCreatedDate(new Date());
			newSupplierForm.setPendingCount((long) 0);
			newSupplierForm.setAcceptedCount((long) 0);
			newSupplierForm.setSubmittedCount((long) 0);
			newSupplierForm.setStatus(SupplierFormsStatus.DRAFT);

			// save Approvals
			if (CollectionUtil.isNotEmpty(newSupplierForm.getApprovals())) {
				for (SupplierFormApproval app : newSupplierForm.getApprovals()) {
					app.setSupplierForm(newSupplierForm);
					if (CollectionUtil.isNotEmpty(app.getApprovalUsers())) {
						for (SupplierFormApprovalUser appUser : app.getApprovalUsers()) {
							appUser.setApproval(app);

						}
					}
				}
			}

			newDbSupplierForm = supplierFormDao.save(newSupplierForm);
			
			try {
				BuyerAuditTrail buyerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "Supplier Form '"+newDbSupplierForm.getName()+"' is created", SecurityLibrary.getLoggedInUserTenantId(), SecurityLibrary.getLoggedInUser(), new Date(), ModuleType.SupplierForm);
				buyerAuditTrailDao.save(buyerAuditTrail);
			} catch (Exception e) {
				LOG.error("Error while recording auction event Resume " + e.getMessage(), e);
			}

			// save FORM ITEM
			if (CollectionUtil.isNotEmpty(newSupplierForm.getFormItems())) {
				SupplierFormItem parent = null;
				for (SupplierFormItem formItem : newSupplierForm.getFormItems()) {
					formItem.setSupplierForm(newDbSupplierForm);
					if (formItem.getOrder() != 0) {
						formItem.setParent(parent);
					}
					if (formItem.getOrder() == 0) {
						parent = formItem;
					}
					supplierFormItemDao.saveOrUpdate(formItem);
				}
			}

		}
		return newDbSupplierForm;

	}
}

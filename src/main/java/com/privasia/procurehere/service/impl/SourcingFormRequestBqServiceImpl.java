package com.privasia.procurehere.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.SourcingFormCqDao;
import com.privasia.procurehere.core.dao.SourcingFormRequestBqDao;
import com.privasia.procurehere.core.dao.SourcingFormRequestBqItemDao;
import com.privasia.procurehere.core.dao.SourcingFormRequestCqItemDao;
import com.privasia.procurehere.core.dao.SourcingFormRequestDao;
import com.privasia.procurehere.core.entity.Owner;
import com.privasia.procurehere.core.entity.SourcingFormRequest;
import com.privasia.procurehere.core.entity.SourcingFormRequestBq;
import com.privasia.procurehere.core.entity.SourcingFormRequestBqItem;
import com.privasia.procurehere.core.entity.SourcingFormRequestCqItem;
import com.privasia.procurehere.core.entity.SourcingFormRequestCqOption;
import com.privasia.procurehere.core.entity.SourcingFormTemplate;
import com.privasia.procurehere.core.entity.SourcingTemplateCq;
import com.privasia.procurehere.core.entity.SourcingTemplateCqItem;
import com.privasia.procurehere.core.enums.CqType;
import com.privasia.procurehere.core.enums.SourcingFormStatus;
import com.privasia.procurehere.core.exceptions.BqRequiredException;
import com.privasia.procurehere.core.exceptions.ExcelParseException;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.parsers.RFSBqFileParser;
import com.privasia.procurehere.core.pojo.SourcingBqItemPojo;
import com.privasia.procurehere.core.pojo.SourcingReqBqPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.SourcingFormCqService;
import com.privasia.procurehere.service.SourcingFormRequestBqService;
import com.privasia.procurehere.service.SourcingFormRequestCqItemService;
import com.privasia.procurehere.service.SourcingFormRequestService;
import com.privasia.procurehere.service.UomService;

@Service
@Transactional(readOnly = true)
public class SourcingFormRequestBqServiceImpl implements SourcingFormRequestBqService {

	private static final Logger LOG = LogManager.getLogger(Global.PR_LOG);

	@Autowired
	SourcingFormRequestCqItemService sourcingFormRequestCqItemService;

	@Autowired
	SourcingFormRequestBqDao sourcingFormRequestBqDao;

	@Autowired
	SourcingFormRequestBqItemDao sourcingFormRequestBqItemDao;

	@Autowired
	SourcingFormRequestService sourcingFormRequestService;

	@Autowired
	SourcingFormRequestDao sourcingFormRequestDao;

	@Autowired
	MessageSource messageSource;

	@Autowired
	SourcingFormCqService cqService;

	@Autowired
	SourcingFormCqDao cqDao;

	@Autowired
	SourcingFormRequestCqItemDao sourcingFormRequestCqItemDao;

	@Autowired
	UomService uomService;

	@Override
	public boolean isBqExists(String formId, String bqId, String name) {
		return sourcingFormRequestBqDao.isBqExists(formId, bqId, name);
	}

	@Override
	@Transactional(readOnly = false)
	public SourcingFormRequestBq saveSourcingBq(SourcingReqBqPojo sourcingReqBqPojo) {
		List<SourcingFormRequestBq> bqList = findBqByFormIdByOrder(sourcingReqBqPojo.getFormId());
		Integer count = 1;
		if (CollectionUtil.isNotEmpty(bqList)) {
			for (SourcingFormRequestBq bq : bqList) {
				if (bq.getBqOrder() == null) {
					bq.setBqOrder(count);
					sourcingFormRequestBqDao.update(bq);
					count++;
				}
			}
			count = bqList.size();
			count++;
		}
		SourcingFormRequestBq bq = new SourcingFormRequestBq();
		bq.setSourcingFormRequest(sourcingFormRequestService.getSourcingRequestById(sourcingReqBqPojo.getFormId()));
		bq.setId(sourcingReqBqPojo.getId());
		bq.setName(sourcingReqBqPojo.getBqName());
		bq.setDescription(sourcingReqBqPojo.getBqDesc());
		bq.setCreatedDate(new Date());
		bq.setBqOrder(count);
		return sourcingFormRequestBqDao.saveOrUpdate(bq);
	}

	@Override
	public List<SourcingFormRequestBq> findBqByFormId(String formId) {

		List<SourcingFormRequestBq> list = sourcingFormRequestBqDao.findBqByFormId(formId);
		for (SourcingFormRequestBq sourcingFormRequestBq : list) {

			if (CollectionUtil.isNotEmpty(sourcingFormRequestBq.getBqItems())) {
				for (SourcingFormRequestBqItem item : sourcingFormRequestBq.getBqItems()) {
					item.getParent();
					item.getChildren();
				}
			}
		}
		return list;
	}

	@Override
	public SourcingFormRequestBq findBqById(String bqId) {
		return sourcingFormRequestBqDao.findById(bqId);
	}

	@Override
	public List<SourcingFormRequestBqItem> getBqItemForSearchFilter(String bqId, Integer itemLevel, Integer itemOrder, String searchVal, Integer start, Integer length, Integer pageNo) {
		List<SourcingFormRequestBqItem> returnList = new ArrayList<SourcingFormRequestBqItem>();
		if (pageNo != null) {
			start = pageNo - 1;
		}
		if (length != null) {
			start = start * length;
		}
		List<SourcingFormRequestBqItem> bqList = sourcingFormRequestBqItemDao.getBqItemForSearchFilter(bqId, itemLevel, itemOrder, searchVal, start, length);
		if (CollectionUtil.isNotEmpty(bqList)) {
			for (SourcingFormRequestBqItem item : bqList) {
				SourcingFormRequestBqItem bqItem = item.createSearchShallowCopy();
				returnList.add(bqItem);
			}
		}
		return returnList;
	}

	@Override
	public List<SourcingBqItemPojo> getAllLevelOrderBqItemByBqId(String bqId, String searchVal) {
		return sourcingFormRequestBqItemDao.getAllLevelOrderBqItemByBqId(bqId, searchVal);
	}

	@Override
	public long getTotalBqItemCountByBqId(String bqId, String searchVal) {
		return sourcingFormRequestBqItemDao.getTotalBqItemCountByBqId(bqId, searchVal);
	}

	@Override
	@Transactional(readOnly = false)
	public SourcingFormRequestBq updateSourcingBq(SourcingFormRequestBq bq) {
		return sourcingFormRequestBqDao.update(bq);
	}

	@Override
	public SourcingFormRequest getSourcingRequestBqByFormId(String formId) {
		SourcingFormRequest sourcingFormRequest = sourcingFormRequestDao.getSourcingBqByFormId(formId);
		if (sourcingFormRequest.getFormOwner() != null) {
			sourcingFormRequest.getBusinessUnit();
			sourcingFormRequest.getFormOwner().getName();
			sourcingFormRequest.getFormOwner().getCommunicationEmail();
			sourcingFormRequest.getFormOwner().getPhoneNumber();
			if (sourcingFormRequest.getFormOwner().getOwner() != null) {
				Owner usr = sourcingFormRequest.getFormOwner().getOwner();
				usr.getCompanyContactNumber();
			}
		}
		return sourcingFormRequest;
	}

	@Override
	public SourcingFormRequestBq getBqById(String bqId) {
		return sourcingFormRequestBqDao.findById(bqId);
	}

	@Override
	public SourcingFormRequestBqItem getBqItemsbyBqId(String bqParent) {
		return sourcingFormRequestBqItemDao.findById(bqParent);
	}

	@Override
	public boolean isBqItemExists(SourcingFormRequestBqItem sourcingBqItem, String bq, String parent) {
		return sourcingFormRequestBqItemDao.isExist(sourcingBqItem, bq, parent);
	}

	@Override
	@Transactional(readOnly = false)
	public SourcingFormRequestBqItem saveSourcingBqItem(SourcingFormRequestBqItem sourcingBqItem) {
		if (sourcingBqItem.getLevel() == 0) {
			if (sourcingBqItem.getParent() == null) {
				int itemLevel = 0;
				List<SourcingFormRequestBqItem> list = sourcingFormRequestBqItemDao.getBqItemLevelOrder(sourcingBqItem.getBq().getId());
				if (CollectionUtil.isNotEmpty(list)) {
					itemLevel = list.size();
				}
				LOG.info("ITEM LEVEL :: " + itemLevel);
				sourcingBqItem.setLevel(itemLevel + 1);
				sourcingBqItem.setOrder(0);
			} else {
				SourcingFormRequestBqItem parent = sourcingFormRequestBqItemDao.findById(sourcingBqItem.getParent().getId());
				if (sourcingBqItem.getParent() != null) {
					sourcingBqItem.setLevel(parent.getLevel());
					sourcingBqItem.setOrder(CollectionUtil.isEmpty(parent.getChildren()) ? 1 : parent.getChildren().size() + 1);
				}
			}
		}
		return sourcingFormRequestBqItemDao.saveOrUpdate(sourcingBqItem);
	}

	@Override
	@Transactional(readOnly = false)
	public SourcingFormRequestBqItem updateSourcingBqItem(SourcingFormRequestBqItem sourcingReqBqItem) {
		return sourcingFormRequestBqItemDao.update(sourcingReqBqItem);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteSourcingBq(String id) {
		sourcingFormRequestBqDao.deleteBq(id);
	}

	@Override
	public List<SourcingFormRequestBq> getAllBqListByFormId(String formId) {
		return sourcingFormRequestBqDao.findBqsByFormId(formId);
	}

	@Override
	@Transactional(readOnly = false)
	public SourcingFormRequestBqItem getBqItembyBqItemId(String bqItemId) {
		return sourcingFormRequestBqItemDao.getBqItemByBqItemId(bqItemId);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteSourcingBqItems(String[] bqItemsIds, String bqId) throws NotAllowedException {
		SourcingFormRequestBq bq = sourcingFormRequestBqDao.findBqItemById(bqId);
		if (bq != null) {
			if (SourcingFormStatus.DRAFT != bq.getSourcingFormRequest().getStatus()) {
				throw new NotAllowedException(messageSource.getMessage("rft.bqitems.info.draft", new Object[] {}, Global.LOCALE));
			}
			bqId = sourcingFormRequestBqItemDao.deleteBqItems(bqItemsIds, bq.getId());
		}
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void reorderBqItems(SourcingBqItemPojo sourcingBqItemPojo) throws NotAllowedException {
		int newOrder = sourcingBqItemPojo.getOrder();
		SourcingFormRequestBqItem bqItem = getBqItembyBqItemId(sourcingBqItemPojo.getId());

		if (CollectionUtil.isNotEmpty(bqItem.getChildren()) && sourcingBqItemPojo.getParent() != null) {
			throw new NotAllowedException("Bill of Quantities Item cannot be made a child if it has sub items");
		}

		int oldOrder = bqItem.getOrder();
		LOG.info("Item Old Position :: " + oldOrder + " :: Item New Position :: " + newOrder);
		int oldLevel = bqItem.getLevel();
		int newLevel = sourcingBqItemPojo.getOrder(); // this will be ignored if
														// it is made a child
		SourcingFormRequestBqItem newParent = null;
		if (sourcingBqItemPojo.getParent() != null && StringUtils.checkString(sourcingBqItemPojo.getParent()).length() > 0) {
			newParent = getBqItembyBqItemId(sourcingBqItemPojo.getParent());
		}
		SourcingFormRequestBqItem oldParent = bqItem.getParent();

		// If these are not child, their order should be reset to 0
		if (oldParent == null) {
			oldOrder = 0;
		}
		if (newParent == null) {
			newOrder = 0;
		}

		// Update it to new position.
		bqItem.setOrder(newOrder);
		bqItem.setLevel(newParent == null ? sourcingBqItemPojo.getOrder() : 0);
		bqItem.setParent(newParent);
		sourcingFormRequestBqItemDao.updateItemOrder(bqItem, bqItem.getBq().getId(), (oldParent == null ? null : oldParent.getId()), (newParent == null ? null : newParent.getId()), oldOrder, newOrder, oldLevel, newLevel);

	}

	@Override
	public Boolean checkMandatoryToFinishEvent(String requestId) throws NotAllowedException {
		LOG.info("Checking validation checkMandatoryToFinishEvent ");

		boolean isBqFill = false;
		int requiredCq = 0;
		boolean cqNotReq = false;
		boolean cqReq = false;
		long bq = sourcingFormRequestService.getBqCount(requestId);
		if (bq == 0) {
			throw new BqRequiredException("Please fill up mandatory Bill Of Quantities : ");
		}

		// Empty Bq
		List<String> notSectionAddedBqs = sourcingFormRequestService.getNotSectionAddedRfsBq(requestId);
		if (CollectionUtil.isNotEmpty(notSectionAddedBqs)) {
			String names = org.apache.commons.lang.StringUtils.join(notSectionAddedBqs, ",");
			LOG.info("Empty BQ : " + names);
			throw new BqRequiredException("Please fill up mandatory Bill Of Quantities : ");
		}

		// Empty Section
		List<String> notItemSectionAddedBqs = sourcingFormRequestService.getNotSectionItemAddedRfsBq(requestId);
		if (CollectionUtil.isNotEmpty(notItemSectionAddedBqs)) {
			String names = org.apache.commons.lang.StringUtils.join(notItemSectionAddedBqs, ",");
			LOG.info("BQ Name having empty section: " + names);
			throw new BqRequiredException("Please fill up mandatory Bill Of Quantities : ");
		}

		SourcingFormTemplate form = sourcingFormRequestService.getSourcingFormByReqId(requestId);
		List<SourcingTemplateCq> cqs = form.getCqs();
		for (SourcingTemplateCq templateCq : cqs) {
			List<SourcingTemplateCqItem> cqItems = templateCq.getCqItems();
			for (SourcingTemplateCqItem templateCqItem : cqItems) {
				// Optional is inverted. True means it is required and False means not required
				if (Boolean.TRUE == templateCqItem.getOptional()) {
					List<SourcingFormRequestCqItem> reqCqItems = sourcingFormRequestCqItemDao.findRequestCqItemByFromCqItemId(templateCqItem.getId(), requestId);
					if (CollectionUtil.isNotEmpty(reqCqItems)) {
						for (SourcingFormRequestCqItem requestCqItem : reqCqItems) {
							if (templateCqItem.getCqType() == CqType.TEXT) {
								if (StringUtils.checkString(requestCqItem.getTextAnswers()).length() == 0) {
									throw new NotAllowedException("Please fill up mandatory Questionnaires : " + templateCq.getName());
								}
							} else {
								SourcingFormRequestCqOption cqOption = sourcingFormRequestCqItemDao.getCqOptionsByCqItemId(requestCqItem.getId());
								if (cqOption == null || (cqOption != null && StringUtils.checkString(cqOption.getValue()).length() == 0)) {
									throw new NotAllowedException("Please fill up mandatory Questionnaires : " + templateCq.getName());
								}
							}
						}
					} else {
						throw new NotAllowedException("Please fill up mandatory Questionnaires : " + templateCq.getName());
					}
				}
			}
		}

		return true;
	}

	@Override
	public List<SourcingFormRequestBqItem> getAllbqItemByBqId(String id) {

		return sourcingFormRequestBqItemDao.getAllbqItemByBqId(id);
	}

	@Override
	public SourcingFormRequestBq getAllbqItemsByBqId(String id) {
		return sourcingFormRequestBqItemDao.getAllbqItemsByBqId(id);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, rollbackFor = { ExcelParseException.class, Exception.class })
	public int uploadBqFile(String bqId, String rfsId, File convFile, String tenantId) throws ExcelParseException {
		RFSBqFileParser<SourcingFormRequestBqItem> rfaBi = new RFSBqFileParser<SourcingFormRequestBqItem>(SourcingFormRequestBqItem.class);
		List<SourcingFormRequestBqItem> rfaBqList = rfaBi.parse(convFile);

		// adding columns while uploading BQs
		Workbook workbook = null;
		Sheet sheet = null;
		try {
			workbook = WorkbookFactory.create(convFile);
			sheet = workbook.getSheetAt(0);
		} catch (InvalidFormatException | IOException e) {
			LOG.info("Error while uploading RFS BQ : " + e.getMessage(), e);
		}

		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		for (SourcingFormRequestBqItem bi : rfaBqList) {
			Integer order = map.get(bi.getLevel());
			if (order == null) {
				map.put(bi.getLevel(), 1);
			} else {
				map.put(bi.getLevel(), order + 1);
			}
		}

		for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
			Integer level = entry.getKey();
			Integer order = entry.getValue();
			LOG.info("Level : " + level + " Order : " + order);
			if (order < 2) {
				throw new ExcelParseException("Fill Up Mandatory BQ Section and Items.", convFile.getName());
			}
		}

		SourcingFormRequestBq rfsBq = null;
		if (sheet != null) {
			rfsBq = sourcingFormRequestBqDao.findById(bqId);
			int startingRow = 0;
			List<String> columnsTitle = new ArrayList<String>();
			Row rowData = sheet.getRow(startingRow);
			// columnCount = rowData.length;
			int columnCount = rowData.getLastCellNum();
			if (columnCount > 0 && rowData.getCell(0).getStringCellValue().trim().length() > 0) {
				for (int j = 8; j < columnCount; j++) {
					if (j >= 8) {
						String columnName = rowData.getCell(j).getStringCellValue().trim();
						if (StringUtils.checkString(columnName).length() > 0) {
							columnsTitle.add(columnName);
						}
					}
				}
			}
			buildAddNewColumnsToUpload(rfsBq, columnsTitle);
			rfsBq = sourcingFormRequestBqDao.update(rfsBq);
		}

		// Delete existing BQ Items by Bqs.
		sourcingFormRequestBqItemDao.deleteBqItemsbyBqid(bqId);
		int count = 1;
		List<String> columns = new ArrayList<String>();

		// If successfully Deleted then only inserting records into DB.
		for (SourcingFormRequestBqItem bi : rfaBqList) {
			SourcingFormRequestBqItem rfaBqItem = new SourcingFormRequestBqItem();

			// checking Extra column in Excel match with bq columns
			if (count == 1 && CollectionUtil.isNotEmpty(bi.getColumnTitles())) {

				if (StringUtils.checkString(rfsBq.getField1Label()).length() > 0) {
					columns.add(rfsBq.getField1Label());
				}
				if (StringUtils.checkString(rfsBq.getField2Label()).length() > 0) {
					columns.add(rfsBq.getField2Label());
				}
				if (StringUtils.checkString(rfsBq.getField3Label()).length() > 0) {
					columns.add(rfsBq.getField3Label());
				}
				if (StringUtils.checkString(rfsBq.getField4Label()).length() > 0) {
					columns.add(rfsBq.getField4Label());
				}

				if (StringUtils.checkString(rfsBq.getField5Label()).length() > 0) {
					columns.add(rfsBq.getField5Label());
				}
				if (StringUtils.checkString(rfsBq.getField6Label()).length() > 0) {
					columns.add(rfsBq.getField6Label());
				}
				if (StringUtils.checkString(rfsBq.getField7Label()).length() > 0) {
					columns.add(rfsBq.getField7Label());
				}
				if (StringUtils.checkString(rfsBq.getField8Label()).length() > 0) {
					columns.add(rfsBq.getField8Label());
				}
				if (StringUtils.checkString(rfsBq.getField9Label()).length() > 0) {
					columns.add(rfsBq.getField9Label());
				}
				if (StringUtils.checkString(rfsBq.getField10Label()).length() > 0) {
					columns.add(rfsBq.getField10Label());
				}
				if (columns.size() != bi.getColumnTitles().size()) {
					LOG.info("Invalid Excel format size");
					throw new ExcelParseException("Invalid Excel format. There should be '" + (columns.size() + 8) + "' Columns in excel.", convFile.getName());
				}

				for (int i = 0; i < columns.size(); i++) {
					if (!columns.get(i).equalsIgnoreCase(bi.getColumnTitles().get(i))) {
						LOG.info("Invalid Excel format column '" + columns.get(i) + "' in excel does not match with '" + bi.getColumnTitles().get(i) + "' in Bill of Quantity.");
						throw new ExcelParseException("Invalid Excel format column '" + columns.get(i) + "' in excel does not match with '" + bi.getColumnTitles().get(i) + "' in Bill of Quantity.", convFile.getName());
					}
				}

				count++;
			}
			rfaBqItem.setBq(rfsBq);
			rfaBqItem.setSourcingFormRequest(sourcingFormRequestDao.findById(rfsId));

			if (bi.getItemName().length() > 250) {
				throw new ExcelParseException("Item Name length must be between 1 to 250. ", convFile.getName());
			}
			rfaBqItem.setItemName(bi.getItemName());
			rfaBqItem.setItemDescription(bi.getItemDescription());
			rfaBqItem.setLevel(bi.getLevel());
			rfaBqItem.setOrder(bi.getOrder());

			// For Parent below fields set to null
			if (bi.getOrder() == 0) {
				rfaBqItem.setUom(null);
				rfaBqItem.setUnitPrice(null);
				rfaBqItem.setQuantity(null);
				rfaBqItem.setPriceType(null);
				rfaBqItem.setUnitBudgetPrice(null);
				rfaBqItem.setField1(null);
				rfaBqItem.setField2(null);
				rfaBqItem.setField3(null);
				rfaBqItem.setField4(null);
				rfaBqItem.setUnitPriceType(null);
				rfaBqItem.setField5(null);
				rfaBqItem.setField6(null);
				rfaBqItem.setField7(null);
				rfaBqItem.setField8(null);
				rfaBqItem.setField9(null);
				rfaBqItem.setField10(null);
			} else {
				SourcingFormRequestBqItem parentBq = sourcingFormRequestBqItemDao.getParentbyLevelId(bqId, bi.getLevel());
				rfaBqItem.setParent(parentBq);

				rfaBqItem.setUnitPrice(bi.getUnitPrice());
				rfaBqItem.setUnitBudgetPrice(bi.getUnitBudgetPrice());
				rfaBqItem.setQuantity(bi.getQuantity());
				rfaBqItem.setPriceType(bi.getPriceType());
				if (bi.getUom() != null) {
					rfaBqItem.setUom(uomService.getUombyCode(bi.getUom().getUom(), tenantId));
				}
				// if invalid UOM
				if (rfaBqItem.getUom() == null) {
					throw new ExcelParseException(messageSource.getMessage("file.parse.uom.invalid.found", new Object[] { bi.getUom().getUom() }, Global.LOCALE), convFile.getName());
				}
				if (StringUtils.checkString(rfsBq.getField1Label()).length() > 0) {
					rfaBqItem.setField1(bi.getField1());
				}
				if (StringUtils.checkString(rfsBq.getField2Label()).length() > 0) {
					rfaBqItem.setField2(bi.getField2());
				}
				if (StringUtils.checkString(rfsBq.getField3Label()).length() > 0) {
					rfaBqItem.setField3(bi.getField3());
				}
				if (StringUtils.checkString(rfsBq.getField4Label()).length() > 0) {
					rfaBqItem.setField4(bi.getField4());
				}
				if (StringUtils.checkString(rfsBq.getField5Label()).length() > 0) {
					rfaBqItem.setField5(bi.getField5());
				}
				if (StringUtils.checkString(rfsBq.getField6Label()).length() > 0) {
					rfaBqItem.setField6(bi.getField6());
				}
				if (StringUtils.checkString(rfsBq.getField7Label()).length() > 0) {
					rfaBqItem.setField7(bi.getField7());
				}
				if (StringUtils.checkString(rfsBq.getField8Label()).length() > 0) {
					rfaBqItem.setField8(bi.getField8());
				}
				if (StringUtils.checkString(rfsBq.getField9Label()).length() > 0) {
					rfaBqItem.setField9(bi.getField9());
				}
				if (StringUtils.checkString(rfsBq.getField10Label()).length() > 0) {
					rfaBqItem.setField10(bi.getField10());
				}

			}
			sourcingFormRequestBqItemDao.save(rfaBqItem);
		}
		return rfaBqList.size();
	}

	protected void buildAddNewColumnsToUpload(SourcingFormRequestBq rfsBq, List<String> columnsTitle) {
		if (columnsTitle.size() >= 1) {
			rfsBq.setField1Label(columnsTitle.get(0) != null ? columnsTitle.get(0) : null);
		}
		if (columnsTitle.size() >= 2) {
			rfsBq.setField2Label(columnsTitle.get(1) != null ? columnsTitle.get(1) : null);
		}
		if (columnsTitle.size() >= 3) {
			rfsBq.setField3Label(columnsTitle.get(2) != null ? columnsTitle.get(2) : null);
		}
		if (columnsTitle.size() >= 4) {
			rfsBq.setField4Label(columnsTitle.get(3) != null ? columnsTitle.get(3) : null);
		}
		if (columnsTitle.size() >= 5) {
			rfsBq.setField5Label(columnsTitle.get(4) != null ? columnsTitle.get(4) : null);
		}
		if (columnsTitle.size() >= 6) {
			rfsBq.setField6Label(columnsTitle.get(5) != null ? columnsTitle.get(5) : null);
		}
		if (columnsTitle.size() >= 7) {
			rfsBq.setField7Label(columnsTitle.get(6) != null ? columnsTitle.get(6) : null);
		}
		if (columnsTitle.size() >= 8) {
			rfsBq.setField8Label(columnsTitle.get(7) != null ? columnsTitle.get(7) : null);
		}
		if (columnsTitle.size() >= 9) {
			rfsBq.setField9Label(columnsTitle.get(8) != null ? columnsTitle.get(8) : null);
		}
		if (columnsTitle.size() >= 10) {
			rfsBq.setField10Label(columnsTitle.get(9) != null ? columnsTitle.get(9) : null);
		}
	}

	@Override
	public List<SourcingFormRequestBq> findBqByFormIdByOrder(String formId) {
		List<SourcingFormRequestBq> list = sourcingFormRequestBqDao.findBqByFormIdByOrder(formId);
		if (CollectionUtil.isNotEmpty(list)) {
			for (SourcingFormRequestBq sourcingFormRequestBq : list) {
				if (CollectionUtil.isNotEmpty(sourcingFormRequestBq.getBqItems())) {
					for (SourcingFormRequestBqItem item : sourcingFormRequestBq.getBqItems()) {
						item.getParent();
						item.getChildren();
					}
				}
			}
		}
		return list;
	}

	@Override
	public List<SourcingFormRequestBq> findRequestBqByFormIdByOrder(String formId) {
		return sourcingFormRequestBqDao.findRequestBqByFormIdByOrder(formId);
	}

}
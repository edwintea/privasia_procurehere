package com.privasia.procurehere.service.impl;

import java.io.File;
import java.io.IOException;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
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
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RftBqDao;
import com.privasia.procurehere.core.dao.RftBqItemDao;
import com.privasia.procurehere.core.dao.RftEnvelopDao;
import com.privasia.procurehere.core.dao.RftEventDao;
import com.privasia.procurehere.core.dao.RftSupplierBqDao;
import com.privasia.procurehere.core.dao.RftSupplierBqItemDao;
import com.privasia.procurehere.core.entity.Bq;
import com.privasia.procurehere.core.entity.BqItem;
import com.privasia.procurehere.core.entity.Owner;
import com.privasia.procurehere.core.entity.RfaBqItem;
import com.privasia.procurehere.core.entity.RfpBqItem;
import com.privasia.procurehere.core.entity.RfqBqItem;
import com.privasia.procurehere.core.entity.RftBqItem;
import com.privasia.procurehere.core.entity.RftEvent;
import com.privasia.procurehere.core.entity.RftEventBq;
import com.privasia.procurehere.core.entity.RftSupplierBqItem;
import com.privasia.procurehere.core.enums.BqUserTypes;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.PricingTypes;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.exceptions.ExcelParseException;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.parsers.BqFileParser;
import com.privasia.procurehere.core.pojo.BqItemPojo;
import com.privasia.procurehere.core.pojo.BqPojo;
import com.privasia.procurehere.core.pojo.UomPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.RfaBqService;
import com.privasia.procurehere.service.RfpBqService;
import com.privasia.procurehere.service.RfqBqService;
import com.privasia.procurehere.service.RftBqService;
import com.privasia.procurehere.service.UomService;

@Service
@Transactional(readOnly = true)
public class RftBqServiceImpl implements RftBqService {

	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@Autowired
	MessageSource messageSource;

	@Autowired
	RftEventDao rftEventDao;

	@Autowired
	RftBqDao rftEventBqDao;

	@Autowired
	RftBqItemDao rftBqItemDao;

	@Autowired
	RftEnvelopDao rftEnvelopDao;

	@Autowired
	RftSupplierBqItemDao rftSupplierBqItemDao;

	@Autowired
	RftSupplierBqDao rftSupplierBqDao;

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

	@Override
	public RftEvent getRftEventById(String id) {
		RftEvent rft = rftEventDao.findEventForBqByEventId(id);
		if (rft.getEventOwner() != null) {
			rft.getEventOwner().getName();
			rft.getEventOwner().getCommunicationEmail();
			rft.getEventOwner().getPhoneNumber();
			if (rft.getEventOwner().getOwner() != null) {
				Owner usr = rft.getEventOwner().getOwner();
				usr.getCompanyContactNumber();
			}
		}
		if (rft != null) {
			if (rft.getBaseCurrency() != null) {
				rft.getBaseCurrency().getCurrencyName();
			}
		}
		return rft;
	}

	@Override
	public List<RftBqItem> findAllRftEventBqbybqId(String bqId) {
		List<RftBqItem> returnList = new ArrayList<RftBqItem>();
		List<RftBqItem> list = rftBqItemDao.getListBqItemsbyId(bqId);
		if (CollectionUtil.isNotEmpty(list)) {
			for (RftBqItem item : list) {
				RftBqItem parent = item.createShallowCopy();
				returnList.add(parent);
				if (CollectionUtil.isNotEmpty(item.getChildren())) {
					for (RftBqItem child : item.getChildren()) {
						LOG.info("CHILD : " + child.getItemName());
						if (parent.getChildren() == null) {
							parent.setChildren(new ArrayList<RftBqItem>());
						}
						parent.getChildren().add(child.createShallowCopy());
					}
				}
			}
		}
		return returnList;
	}

	@Override
	public RftBqItem getRftBqItemById(String id) {
		return rftBqItemDao.findById(id);
	}

	@Override
	@Transactional(readOnly = false)
	public RftBqItem saveRftBqItem(RftBqItem rftBqItem) {
		// if (rftBqItem.getLevel() == null && rftBqItem.getOrder() == null) {
		// Below condition for, if data saving from EXCEL no need to set LEVEL and ORDER
		if (rftBqItem.getLevel() == 0) {
			if (rftBqItem.getParent() == null) {
				int itemLevel = 0;
				List<RftBqItem> list = rftBqItemDao.getBqItemLevelOrder(rftBqItem.getBq().getId());
				if (CollectionUtil.isNotEmpty(list)) {
					itemLevel = list.size();
				}
				LOG.info("ITEM LEVEL :: " + itemLevel);
				rftBqItem.setLevel(itemLevel + 1);
				rftBqItem.setOrder(0);
			} else {
				RftBqItem parent = rftBqItemDao.findById(rftBqItem.getParent().getId());
				if (rftBqItem.getParent() != null) {
					rftBqItem.setLevel(parent.getLevel());
					rftBqItem.setOrder(CollectionUtil.isEmpty(parent.getChildren()) ? 1 : parent.getChildren().size() + 1);
				}
			}
		}
		LOG.info("item name :" + rftBqItem.getItemName());
		return rftBqItemDao.saveOrUpdate(rftBqItem);
	}

	@Override
	@Transactional
	public RftBqItem getBqItemsbyBqId(String parent) {
		return rftBqItemDao.findById(parent);
	}

	@Override
	public boolean isBqItemExists(RftBqItem item, String bqId, String parentId) {
		return rftBqItemDao.isExists(item, bqId, parentId);
	}

	@Override
	public List<RftEventBq> getAllBqListByEventId(String id) {
		return rftEventBqDao.findBqsByEventId(id);
	}

	@Override
	public List<RftEventBq> getRftEventBqForEventIdForEnvelop(String id, List<String> bqIds) {
		return rftEventBqDao.findBqsByEventIdForEnvelop(id, bqIds);
	}

	@Override
	public RftEventBq getBqItembyId(String id) {
		return rftEventBqDao.findById(id);
	}

	@Override
	@Transactional(readOnly = false)
	public RftEventBq saveRftBq(RftEventBq rftEventBq) {
		return rftEventBqDao.saveOrUpdate(rftEventBq);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateRftBqItems(RftBqItem rftBqItem) {
		rftBqItemDao.update(rftBqItem);
	}

	@Override
	public List<RftBqItem> findRftBqbyBqId(String bqId) {
		LOG.info("Find BQ By ID :: " + bqId);
		List<RftBqItem> returnList = new ArrayList<RftBqItem>();
		List<RftBqItem> list = rftBqItemDao.getListBqItemsbyId(bqId);
		bulidBqItemList(returnList, list);
		return returnList;
	}

	/**
	 * @param returnList
	 * @param list
	 */
	private void bulidBqItemList(List<RftBqItem> returnList, List<RftBqItem> list) {
		LOG.info("BUILDING BQ ITEM LIST :: " + list.toString());
		if (CollectionUtil.isNotEmpty(list)) {
			for (RftBqItem item : list) {
				RftBqItem parent = item.createShallowCopy();
				// LOG.info("BQ PARENT ITEMS :: " + parent.toString());
				returnList.add(parent);
				if (CollectionUtil.isNotEmpty(item.getChildren())) {
					// LOG.info("BQ CHILDREN :: :: ");
					for (RftBqItem child : item.getChildren()) {
						// LOG.info("BQ CHILD LIST :: " + child.toString());
						if (parent.getChildren() == null) {
							parent.setChildren(new ArrayList<RftBqItem>());
						}
						parent.getChildren().add(child.createShallowCopy());
					}
				}
			}
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteBqItems(String[] bqItemIds, String bqId) throws NotAllowedException {
		LOG.info("BQ ITEM IDs :: " + Arrays.asList(bqItemIds));
		RftEventBq bq = rftEventBqDao.findBqItemById(bqId);
		if (bq != null) {
			if (EventStatus.DRAFT != bq.getRfxEvent().getStatus() && EventStatus.SUSPENDED != bq.getRfxEvent().getStatus()) {
				throw new NotAllowedException(messageSource.getMessage("rft.bqitems.info.draft", new Object[] {}, Global.LOCALE));
			}

			for (String id : bqItemIds) {
				RftBqItem parentItm = getBqItembyBqIdAndBqItemId(id);
				if (parentItm != null && parentItm.getParent() == null) {
					rftBqItemDao.deleteChildItems(id, bqId);
				}
			}

			rftBqItemDao.deleteRemaingBqItems(bqItemIds, bq.getId());

			// bqId = rftBqItemDao.deleteBqItems(bqItemIds, bq.getId());
		}
	}

	@Override
	public boolean isBqExists(BqPojo rftBqPojo) {
		RftEventBq bq = new RftEventBq();
		bq.setId(rftBqPojo.getId());
		bq.setName(rftBqPojo.getBqName());
		return rftEventBqDao.isExists(bq, rftBqPojo.getEventId());
	}

	@Override
	@Transactional
	public RftEventBq getRftBqById(String id) {
		return rftEventBqDao.findById(id);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateBqItem(RftBqItem rftBqItem) {
		rftBqItemDao.update(rftBqItem);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = { Exception.class })
	public void deletefieldInBqItems(String bqId, String label) {
		rftBqItemDao.deletefieldInBqItems(bqId, label);
		rftEventBqDao.deletefieldInBq(bqId, label);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateRftBq(RftEventBq rftBq) {
		rftEventBqDao.update(rftBq);

	}

	@Override
	@Transactional(readOnly = false)
	public void deleteRftBq(String id) {
		rftEventBqDao.deleteBQ(id);
	}

	@Override
	public void isAllowtoDeleteBQ(String id, RfxTypes eventType) throws Exception {
		rftEventBqDao.isAllowtoDeleteBQ(id, eventType);
	}

	@Override
	public RftBqItem getBqItembyBqId(String id) {
		return rftBqItemDao.findById(id);
	}

	@Override
	public RftBqItem getBqItembyBqIdAndBqItemId(String bqItemId) {
		return rftBqItemDao.getBqItemByBqIdAndBqItemId(bqItemId);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void reorderBqItems(BqItemPojo rftBqItemPojo) throws NotAllowedException {
		LOG.info("BQ ITEM Object :: " + rftBqItemPojo.toString());
		int newOrder = rftBqItemPojo.getOrder();
		RftBqItem bqItem = getBqItembyBqItemId(rftBqItemPojo.getId());

		if (CollectionUtil.isNotEmpty(bqItem.getChildren()) && rftBqItemPojo.getParent() != null) {
			throw new NotAllowedException("Bill of Quantities Item cannot be made a child if it has sub items");
		}

		LOG.info("DB BQ ITEM DETAILS ::" + bqItem.toString());
		int oldOrder = bqItem.getOrder();
		LOG.info("Item Old Position :: " + oldOrder + " :: Item New Position :: " + newOrder);
		int oldLevel = bqItem.getLevel();
		int newLevel = rftBqItemPojo.getOrder(); // this will be ignored if it is made a child
		RftBqItem newParent = null;
		if (rftBqItemPojo.getParent() != null && StringUtils.checkString(rftBqItemPojo.getParent()).length() > 0) {
			newParent = getBqItembyBqItemId(rftBqItemPojo.getParent());
		}
		RftBqItem oldParent = bqItem.getParent();

		// If these are not child, their order should be reset to 0
		if (oldParent == null) {
			oldOrder = 0;
		}
		if (newParent == null) {
			newOrder = 0;
		}

		// Update it to new position.
		bqItem.setOrder(newOrder);
		bqItem.setLevel(newParent == null ? rftBqItemPojo.getOrder() : 0);
		bqItem.setParent(newParent);
		rftBqItemDao.updateItemOrder(bqItem, bqItem.getBq().getId(), (oldParent == null ? null : oldParent.getId()), (newParent == null ? null : newParent.getId()), oldOrder, newOrder, oldLevel, newLevel);
	}

	@Override
	@Transactional(readOnly = false)
	public RftBqItem getBqItembyBqItemId(String itemId) {
		return rftBqItemDao.getBqItembyBqItemId(itemId);

	}

	@Override
	@Transactional(readOnly = false)
	public void deleteAllRftBq(String eventId, String eventRequirement) {

		rftEnvelopDao.removeBqsFromEnvelops(eventId);
		rftBqItemDao.deleteBqItemsForEventId(eventId);
		rftEventBqDao.deleteBQForEventId(eventId);

		RftEvent event = rftEventDao.findById(eventId);
		event.setBqCompleted(Boolean.FALSE);
		event.setBillOfQuantity(Boolean.FALSE);
		rftEventDao.update(event);
	}

	@Override
	public List<RftEventBq> getNotAssignedRftBqIdsByEventId(String eventId) {
		return rftEnvelopDao.getNotAssignedRftBqIdsByEventId(eventId);
	}

	@Override
	public Integer getCountOfRftBqByEventId(String eventId) {
		return rftEventBqDao.getCountOfRftBqByEventId(eventId);
	}

	@Override
	public List<RftBqItem> getAllBqItemListByEventId(String eventId) {
		return rftBqItemDao.findBqItemListByEventId(eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public RftSupplierBqItem updateRftSupplierBqItem(RftSupplierBqItem persistObject) {
		return rftSupplierBqItemDao.update(persistObject);
	}

	@Override
	public List<Bq> findRftBqbyEventId(String eventId) {
		return rftEventBqDao.findRftBqbyEventId(eventId);
	}

	@Override
	public List<RftBqItem> getAllBqitemsbyBqId(String bqId) {
		return rftBqItemDao.getAllBqItemsbybqId(bqId);
	}

	@Override
	public RftBqItem getParentbyLevelId(String bqId, int level) {
		return rftBqItemDao.getParentbyLevelId(bqId, level);
	}

	protected void buildAddNewColumnsToUpload(Bq eventBq, List<String> columnsTitle) {
		if (columnsTitle.size() >= 1) {
			eventBq.setField1Label(columnsTitle.get(0) != null ? columnsTitle.get(0) : null);
			if (eventBq.getField1FilledBy() == null) {
				eventBq.setField1FilledBy(BqUserTypes.BUYER);
			}
		}
		if (columnsTitle.size() >= 2) {
			eventBq.setField2Label(columnsTitle.get(1) != null ? columnsTitle.get(1) : null);
			if (eventBq.getField2FilledBy() == null) {
				eventBq.setField2FilledBy(BqUserTypes.BUYER);
			}
		}
		if (columnsTitle.size() >= 3) {
			eventBq.setField3Label(columnsTitle.get(2) != null ? columnsTitle.get(2) : null);
			if (eventBq.getField3FilledBy() == null) {
				eventBq.setField3FilledBy(BqUserTypes.BUYER);
			}
		}
		if (columnsTitle.size() >= 4) {
			eventBq.setField4Label(columnsTitle.get(3) != null ? columnsTitle.get(3) : null);
			if (eventBq.getField4FilledBy() == null) {
				eventBq.setField4FilledBy(BqUserTypes.BUYER);
			}
		}
		if (columnsTitle.size() >= 5) {
			eventBq.setField5Label(columnsTitle.get(4) != null ? columnsTitle.get(4) : null);
			if (eventBq.getField5FilledBy() == null) {
				eventBq.setField5FilledBy(BqUserTypes.BUYER);
			}
		}
		if (columnsTitle.size() >= 6) {
			eventBq.setField6Label(columnsTitle.get(5) != null ? columnsTitle.get(5) : null);
			if (eventBq.getField6FilledBy() == null) {
				eventBq.setField6FilledBy(BqUserTypes.BUYER);
			}
		}
		if (columnsTitle.size() >= 7) {
			eventBq.setField7Label(columnsTitle.get(6) != null ? columnsTitle.get(6) : null);
			if (eventBq.getField7FilledBy() == null) {
				eventBq.setField7FilledBy(BqUserTypes.BUYER);
			}
		}
		if (columnsTitle.size() >= 8) {
			eventBq.setField8Label(columnsTitle.get(7) != null ? columnsTitle.get(7) : null);
			if (eventBq.getField8FilledBy() == null) {
				eventBq.setField8FilledBy(BqUserTypes.BUYER);
			}
		}
		if (columnsTitle.size() >= 9) {
			eventBq.setField9Label(columnsTitle.get(8) != null ? columnsTitle.get(8) : null);
			if (eventBq.getField9FilledBy() == null) {
				eventBq.setField9FilledBy(BqUserTypes.BUYER);
			}
		}
		if (columnsTitle.size() >= 10) {
			eventBq.setField10Label(columnsTitle.get(9) != null ? columnsTitle.get(9) : null);
			if (eventBq.getField10FilledBy() == null) {
				eventBq.setField10FilledBy(BqUserTypes.BUYER);
			}
		}
	}

	/**
	 * @param bqId
	 * @param eventId
	 * @param convFile
	 * @throws ExcelParseException
	 */
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, rollbackFor = { ExcelParseException.class, Exception.class })
	public int uploadBqFile(String bqId, String eventId, File convFile, String tenantId) throws ExcelParseException {
		BqFileParser<RftBqItem> rftBq = new BqFileParser<RftBqItem>(RftBqItem.class);
		List<RftBqItem> rftBqList = rftBq.parse(convFile);

		// Delete existing BQ Items by Bqs.
		rftBqItemDao.deleteBqItemsbyBqid(bqId);

		// adding columns while uploading BQs
		Workbook workbook = null;
		Sheet sheet = null;
		try {
			workbook = WorkbookFactory.create(convFile);
			sheet = workbook.getSheetAt(0);
		} catch (InvalidFormatException | IOException e) {
			e.printStackTrace();
		}

		RftEventBq bq = null;
		if (sheet != null) {
			bq = rftBqService.getRftBqById(bqId);
			int startingRow = 0;
			List<String> columnsTitle = new ArrayList<String>();
			Row rowData = sheet.getRow(startingRow);
			// columnCount = rowData.length;
			int columnCount = rowData.getLastCellNum();
			if (columnCount > 0 && rowData.getCell(0).getStringCellValue().trim().length() > 0) {
				for (int j = 7; j < columnCount; j++) {
					if (j >= 7) {
						String columnName = rowData.getCell(j).getStringCellValue().trim();
						if (StringUtils.checkString(columnName).length() > 0) {
							columnsTitle.add(columnName);
						}
					}
				}
			}
			buildAddNewColumnsToUpload(bq, columnsTitle);
			bq = updateRftBqFields(bq);
		}

		int count = 1;
		List<String> columns = new ArrayList<String>();
		for (RftBqItem bi : rftBqList) {
			// RftEventBq bq = rftEventBqDao.findById(bqId);

			// checking Extra column in Excel match with bq columns
			if (count == 1 && CollectionUtil.isNotEmpty(bi.getColumnTitles())) {

				if (StringUtils.checkString(bq.getField1Label()).length() > 0) {
					columns.add(bq.getField1Label());
				}
				if (StringUtils.checkString(bq.getField2Label()).length() > 0) {
					columns.add(bq.getField2Label());
				}
				if (StringUtils.checkString(bq.getField3Label()).length() > 0) {
					columns.add(bq.getField3Label());
				}
				if (StringUtils.checkString(bq.getField4Label()).length() > 0) {
					columns.add(bq.getField4Label());
				}

				if (StringUtils.checkString(bq.getField5Label()).length() > 0) {
					columns.add(bq.getField5Label());
				}
				if (StringUtils.checkString(bq.getField6Label()).length() > 0) {
					columns.add(bq.getField6Label());
				}
				if (StringUtils.checkString(bq.getField7Label()).length() > 0) {
					columns.add(bq.getField7Label());
				}
				if (StringUtils.checkString(bq.getField8Label()).length() > 0) {
					columns.add(bq.getField8Label());
				}
				if (StringUtils.checkString(bq.getField9Label()).length() > 0) {
					columns.add(bq.getField9Label());
				}
				if (StringUtils.checkString(bq.getField10Label()).length() > 0) {
					columns.add(bq.getField10Label());
				}
				if (columns.size() != bi.getColumnTitles().size()) {
					LOG.info("Invalid Excel format size");
					throw new ExcelParseException("Invalid Excel format. There should be '" + (columns.size() + 7) + "' Columns in excel.", convFile.getName());
				}

				for (int i = 0; i < columns.size(); i++) {
					if (!columns.get(i).equalsIgnoreCase(bi.getColumnTitles().get(i))) {
						LOG.info("Invalid Excel format column '" + columns.get(i) + "' in excel does not match with '" + bi.getColumnTitles().get(i) + "' in Bill of Quantity.");
						throw new ExcelParseException("Invalid Excel format column '" + columns.get(i) + "' in excel does not match with '" + bi.getColumnTitles().get(i) + "' in Bill of Quantity.", convFile.getName());
					}
				}

				count++;
			}
			RftBqItem rftBqItem = new RftBqItem();
			rftBqItem.setBq(bq);
			RftEvent event = rftEventDao.findEventForBqByEventId(eventId);
			rftBqItem.setRfxEvent(event);
			rftBqItem.setItemName(bi.getItemName());
			rftBqItem.setItemDescription(bi.getItemDescription());
			rftBqItem.setLevel(bi.getLevel());
			rftBqItem.setOrder(bi.getOrder());

			// For Parent below fields set to null
			if (bi.getOrder() == 0) {
				rftBqItem.setUom(null);
				rftBqItem.setUnitPrice(null);
				rftBqItem.setUnitPriceType(null);
				rftBqItem.setQuantity(null);
				rftBqItem.setPriceType(null);
				rftBqItem.setField1(null);
				rftBqItem.setField2(null);
				rftBqItem.setField3(null);
				rftBqItem.setField4(null);
				rftBqItem.setField5(null);
				rftBqItem.setField6(null);
				rftBqItem.setField7(null);
				rftBqItem.setField8(null);
				rftBqItem.setField9(null);
				rftBqItem.setField10(null);
			} else {
				RftBqItem parentBq = rftBqItemDao.getParentbyLevelId(bqId, bi.getLevel());
				rftBqItem.setParent(parentBq);

				rftBqItem.setUnitPrice(bi.getUnitPrice());
				rftBqItem.setQuantity(bi.getQuantity().setScale((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), RoundingMode.HALF_EVEN));
				rftBqItem.setPriceType(bi.getPriceType());
				if (bi.getUom() != null)
					rftBqItem.setUom(uomService.getUombyCode(bi.getUom().getUom(), tenantId));
				// if invalid UOM
				if (rftBqItem.getUom() == null) {
					throw new ExcelParseException(messageSource.getMessage("file.parse.uom.invalid.found", new Object[] { bi.getUom().getUom() }, Global.LOCALE), convFile.getName());
				}
				if (StringUtils.checkString(bq.getField1Label()).length() > 0) {
					if (Boolean.TRUE == bq.getField1Required() && StringUtils.checkString(bi.getField1()).length() == 0) {
						throw new ExcelParseException(bq.getField1Label() + " is required for " + (bi.getLevel()) + "." + (bi.getOrder()) + " " + bi.getItemName(), convFile.getName());
					}
					rftBqItem.setField1(bi.getField1());
				}
				if (StringUtils.checkString(bq.getField2Label()).length() > 0) {
					if (Boolean.TRUE == bq.getField2Required() && StringUtils.checkString(bi.getField2()).length() == 0) {
						throw new ExcelParseException(bq.getField2Label() + " is required for " + (bi.getLevel()) + "." + (bi.getOrder()) + " " + bi.getItemName(), convFile.getName());
					}
					rftBqItem.setField2(bi.getField2());
				}
				if (StringUtils.checkString(bq.getField3Label()).length() > 0) {
					if (Boolean.TRUE == bq.getField3Required() && StringUtils.checkString(bi.getField3()).length() == 0) {
						throw new ExcelParseException(bq.getField3Label() + " is required for " + (bi.getLevel()) + "." + (bi.getOrder()) + " " + bi.getItemName(), convFile.getName());
					}
					rftBqItem.setField3(bi.getField3());
				}
				if (StringUtils.checkString(bq.getField4Label()).length() > 0) {
					if (Boolean.TRUE == bq.getField4Required() && StringUtils.checkString(bi.getField4()).length() == 0) {
						throw new ExcelParseException(bq.getField4Label() + " is required for " + (bi.getLevel()) + "." + (bi.getOrder()) + " " + bi.getItemName(), convFile.getName());
					}
					rftBqItem.setField4(bi.getField4());
				}
				if (StringUtils.checkString(bq.getField5Label()).length() > 0) {
					if (Boolean.TRUE == bq.getField5Required() && StringUtils.checkString(bi.getField5()).length() == 0) {
						throw new ExcelParseException(bq.getField5Label() + " is required for " + (bi.getLevel()) + "." + (bi.getOrder()) + " " + bi.getItemName(), convFile.getName());
					}
					rftBqItem.setField5(bi.getField5());
				}
				if (StringUtils.checkString(bq.getField6Label()).length() > 0) {
					if (Boolean.TRUE == bq.getField6Required() && StringUtils.checkString(bi.getField6()).length() == 0) {
						throw new ExcelParseException(bq.getField6Label() + " is required for " + (bi.getLevel()) + "." + (bi.getOrder()) + " " + bi.getItemName(), convFile.getName());
					}
					rftBqItem.setField6(bi.getField6());
				}
				if (StringUtils.checkString(bq.getField7Label()).length() > 0) {
					if (Boolean.TRUE == bq.getField7Required() && StringUtils.checkString(bi.getField7()).length() == 0) {
						throw new ExcelParseException(bq.getField7Label() + " is required for " + (bi.getLevel()) + "." + (bi.getOrder()) + " " + bi.getItemName(), convFile.getName());
					}
					rftBqItem.setField7(bi.getField7());
				}
				if (StringUtils.checkString(bq.getField8Label()).length() > 0) {
					if (Boolean.TRUE == bq.getField8Required() && StringUtils.checkString(bi.getField8()).length() == 0) {
						throw new ExcelParseException(bq.getField8Label() + " is required for " + (bi.getLevel()) + "." + (bi.getOrder()) + " " + bi.getItemName(), convFile.getName());
					}
					rftBqItem.setField8(bi.getField8());
				}
				if (StringUtils.checkString(bq.getField9Label()).length() > 0) {
					if (Boolean.TRUE == bq.getField9Required() && StringUtils.checkString(bi.getField9()).length() == 0) {
						throw new ExcelParseException(bq.getField9Label() + " is required for " + (bi.getLevel()) + "." + (bi.getOrder()) + " " + bi.getItemName(), convFile.getName());
					}
					rftBqItem.setField9(bi.getField9());
				}
				if (StringUtils.checkString(bq.getField10Label()).length() > 0) {
					if (Boolean.TRUE == bq.getField10Required() && StringUtils.checkString(bi.getField10()).length() == 0) {
						throw new ExcelParseException(bq.getField10Label() + " is required for " + (bi.getLevel()) + "." + (bi.getOrder()) + " " + bi.getItemName(), convFile.getName());
					}
					rftBqItem.setField10(bi.getField10());
				}
			}
			saveUpdateRftBqItem(rftBqItem);
		}
		return rftBqList.size();
	}

	@Override
	public Integer countAllRftBqItemByEventId(String eventId) {
		return rftBqItemDao.getCountOfAllRftBqItemByEventId(eventId);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public RftEventBq updateRftBqFields(RftEventBq eventBq) {

		RftEventBq bq = rftEventBqDao.update(eventBq);

		List<String> label = new ArrayList<>();
		if (eventBq.getField1FilledBy() == BqUserTypes.SUPPLIER) {
			label.add("field1");
		}
		if (eventBq.getField2FilledBy() == BqUserTypes.SUPPLIER) {
			label.add("field2");
		}
		if (eventBq.getField3FilledBy() == BqUserTypes.SUPPLIER) {
			label.add("field3");
		}
		if (eventBq.getField4FilledBy() == BqUserTypes.SUPPLIER) {
			label.add("field4");
		}
		if (eventBq.getField5FilledBy() == BqUserTypes.SUPPLIER) {
			label.add("field5");
		}
		if (eventBq.getField6FilledBy() == BqUserTypes.SUPPLIER) {
			label.add("field6");
		}
		if (eventBq.getField7FilledBy() == BqUserTypes.SUPPLIER) {
			label.add("field7");
		}
		if (eventBq.getField8FilledBy() == BqUserTypes.SUPPLIER) {
			label.add("field8");
		}
		if (eventBq.getField9FilledBy() == BqUserTypes.SUPPLIER) {
			label.add("field9");
		}
		if (eventBq.getField10FilledBy() == BqUserTypes.SUPPLIER) {
			label.add("field10");
		}
		rftBqItemDao.resetFieldsForFilledBySupplier(eventBq.getId(), label);

		return bq;
	}

	@Override
	public List<String> getNotSectionAddedRftBqIdsByEventId(String eventId) {
		return rftEventBqDao.getNotSectionAddedRftBqIdsByEventId(eventId);
	}

	// @Override
	// public List<RftBqItem> getEventBqForChoosenFilterValue(String bqId, Integer itemLevel, Integer itemOrder) {
	// List<RftBqItem> returnList = new ArrayList<RftBqItem>();
	// List<RftBqItem> bqList = rftBqItemDao.getEventBqForChoosenFilterValue(bqId, itemLevel, itemOrder);
	// if (CollectionUtil.isNotEmpty(bqList)) {
	// for (RftBqItem item : bqList) {
	// RftBqItem bqItem = item.createSearchShallowCopy();
	// returnList.add(bqItem);
	// }
	// }
	// return returnList;
	//
	// }

	@Override
	public List<RftBqItem> getBqItemForSearchFilter(String bqId, Integer itemLevel, Integer itemOrder, String searchVal, Integer start, Integer length, Integer pageNo) {
		List<RftBqItem> returnList = new ArrayList<RftBqItem>();
		if (pageNo != null) {
			start = pageNo - 1;
		}
		if (length != null) {
			start = start * length;
		}
		LOG.info(" start  : " + start);
		List<RftBqItem> bqList = rftBqItemDao.getBqItemForSearchFilter(bqId, itemLevel, itemOrder, searchVal, start, length);
		if (CollectionUtil.isNotEmpty(bqList)) {
			for (RftBqItem item : bqList) {
				RftBqItem bqItem = item.createSearchShallowCopy();
				returnList.add(bqItem);
			}
		}
		return returnList;

	}

	@Override
	public List<BqItemPojo> getAllLevelOrderBqItemByBqId(String bqId, String searchVal) {
		return rftBqItemDao.getAllLevelOrderBqItemByBqId(bqId, searchVal);
	}

	@Override
	public long totalBqItemCountByBqId(String bqId, String searchVal) {
		return rftBqItemDao.totalBqItemCountByBqId(bqId, searchVal);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = { Exception.class })
	public void deleteAllERPBqByEventId(String eventId) {
		rftEnvelopDao.removeBqsFromEnvelops(eventId);
		rftBqItemDao.deleteBqItemsForErpForEventId(eventId);
		rftEventBqDao.deleteBQForEventId(eventId);

	}

	@Override
	@Transactional(readOnly = false)
	public RftBqItem saveUpdateRftBqItem(RftBqItem rftBqItem) {
		return rftBqItemDao.saveOrUpdate(rftBqItem);
	}

	@Override
	public XSSFWorkbook eventDownloader(String bqId, RfxTypes eventType) throws IOException {
		XSSFWorkbook workbook = new XSSFWorkbook();
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
		return workbook;
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

	@Override
	public List<String> getNotSectionItemAddedRftBqIdsByEventId(String eventId) {
		return rftEventBqDao.getNotSectionItemAddedRftBqIdsByEventId(eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteAllBqItems(String bqId) throws NoSuchMessageException, NotAllowedException {
		RftEventBq bq = rftEventBqDao.findBqItemById(bqId);

		if (bq != null) {
			if (EventStatus.DRAFT != bq.getRfxEvent().getStatus() && EventStatus.SUSPENDED != bq.getRfxEvent().getStatus()) {
				throw new NotAllowedException(messageSource.getMessage("rft.bqitems.info.draft", new Object[] {}, Global.LOCALE));
			}
			rftBqItemDao.deleteBqItemsbyBqid(bq.getId());
		}
	}

	@Override
	public RftEventBq getRftEventBqByBqId(String bqId) {
		RftEventBq bq = rftEventBqDao.getRftEventBqByBqId(bqId);
		return bq;
	}

	@Override
	public List<RftEventBq> getAllBqListByEventIdByOrder(String eventId) {
		return rftEventBqDao.findBqsByEventIdByOrder(eventId);
	}

}

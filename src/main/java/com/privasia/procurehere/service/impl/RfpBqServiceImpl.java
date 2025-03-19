package com.privasia.procurehere.service.impl;

import java.io.File;
import java.io.IOException;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RfpBqDao;
import com.privasia.procurehere.core.dao.RfpBqItemDao;
import com.privasia.procurehere.core.dao.RfpEnvelopDao;
import com.privasia.procurehere.core.dao.RfpEventDao;
import com.privasia.procurehere.core.dao.RfpSupplierBqItemDao;
import com.privasia.procurehere.core.entity.Bq;
import com.privasia.procurehere.core.entity.RfpBqItem;
import com.privasia.procurehere.core.entity.RfpEvent;
import com.privasia.procurehere.core.entity.RfpEventBq;
import com.privasia.procurehere.core.entity.RfpSupplierBqItem;
import com.privasia.procurehere.core.enums.BqUserTypes;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.exceptions.ExcelParseException;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.parsers.BqFileParser;
import com.privasia.procurehere.core.pojo.BqItemPojo;
import com.privasia.procurehere.core.pojo.BqPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.RfpBqService;
import com.privasia.procurehere.service.UomService;

@Service
@Transactional(readOnly = true)
public class RfpBqServiceImpl implements RfpBqService {

	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@Autowired
	MessageSource messageSource;

	@Autowired
	RfpEventDao rfpEventDao;

	@Autowired
	RfpBqDao rfpEventBqDao;

	@Autowired
	RfpBqItemDao rfpBqItemDao;

	@Autowired
	RfpEnvelopDao rfpEnvelopDao;

	@Autowired
	RfpSupplierBqItemDao rfpSupplierBqItemDao;

	@Autowired
	UomService uomService;

	@Override
	public RfpEvent getRfpEventById(String id) {
		return rfpEventDao.findById(id);
	}

	@Override
	public List<RfpBqItem> findAllEventBqbybqId(String bqId) {
		List<RfpBqItem> returnList = new ArrayList<RfpBqItem>();
		List<RfpBqItem> list = rfpBqItemDao.getListBqItemsbyId(bqId);
		if (CollectionUtil.isNotEmpty(list)) {
			for (RfpBqItem item : list) {
				RfpBqItem parent = item.createShallowCopy();
				returnList.add(parent);
				if (CollectionUtil.isNotEmpty(item.getChildren())) {
					for (RfpBqItem child : item.getChildren()) {
						LOG.info("CHILD : " + child.getItemName());
						if (parent.getChildren() == null) {
							parent.setChildren(new ArrayList<RfpBqItem>());
						}
						parent.getChildren().add(child.createShallowCopy());
					}
				}
			}
		}
		return returnList;
	}

	@Override
	public RfpBqItem getBqItemById(String id) {
		return rfpBqItemDao.findById(id);
	}

	@Override
	@Transactional(readOnly = false)
	public RfpBqItem saveBqItem(RfpBqItem rftBqItem) {
		if (rftBqItem.getParent() == null) {
			int itemLevel = 0;
			List<RfpBqItem> list = rfpBqItemDao.getBqItemLevelOrder(rftBqItem.getBq().getId());
			if (CollectionUtil.isNotEmpty(list)) {
				itemLevel = list.size();
			}
			LOG.info("ITEM LEVEL :: " + itemLevel);
			rftBqItem.setLevel(itemLevel + 1);
			rftBqItem.setOrder(0);
		} else {
			RfpBqItem parent = getBqItemsbyBqId(rftBqItem.getParent().getId());
			if (rftBqItem.getParent() != null) {
				rftBqItem.setLevel(parent.getLevel());
				rftBqItem.setOrder(CollectionUtil.isEmpty(parent.getChildren()) ? 1 : parent.getChildren().size() + 1);
			}
		}
		return rfpBqItemDao.saveOrUpdate(rftBqItem);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
	public RfpBqItem getBqItemsbyBqId(String parent) {
		return rfpBqItemDao.findById(parent);
	}

	@Override
	public List<RfpEventBq> getAllBqListByEventId(String id) {
		return rfpEventBqDao.findBqsByEventId(id);
	}

	@Override
	public List<RfpEventBq> getEventBqForEventIdForEnvelop(String id, List<String> bqIds) {
		return rfpEventBqDao.findBqsByEventIdForEnvelop(id, bqIds);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
	public RfpEventBq getBqItembyId(String id) {
		return rfpEventBqDao.findBqItemById(id);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public RfpEventBq saveBq(RfpEventBq rftEventBq) {
		return rfpEventBqDao.saveOrUpdate(rftEventBq);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateBqItems(RfpBqItem rftBqItem) {
		rfpBqItemDao.update(rftBqItem);
	}

	@Override
	public List<RfpBqItem> getBqItemsbyId(String bqId) {
		return rfpBqItemDao.getListBqItemsbyId(bqId);
	}

	@Override
	public List<RfpBqItem> findRfpBqbyBqId(String bqId) {
		LOG.info("Find BQ By ID :: " + bqId);
		List<RfpBqItem> returnList = new ArrayList<RfpBqItem>();
		List<RfpBqItem> list = rfpBqItemDao.getListBqItemsbyId(bqId);
		bulidBqItemList(returnList, list);
		return returnList;
	}

	/**
	 * @param returnList
	 * @param list
	 */
	private void bulidBqItemList(List<RfpBqItem> returnList, List<RfpBqItem> list) {
		LOG.info("BUILDING BQ ITEM LIST :: " + list.toString());
		if (CollectionUtil.isNotEmpty(list)) {
			for (RfpBqItem item : list) {
				RfpBqItem parent = item.createShallowCopy();
				LOG.info("BQ PARENT ITEMS :: " + parent.toString());
				returnList.add(parent);
				if (CollectionUtil.isNotEmpty(item.getChildren())) {
					LOG.info("BQ CHILDREN :: :: ");
					for (RfpBqItem child : item.getChildren()) {
						LOG.info("BQ CHILD LIST :: " + child.toString());
						if (parent.getChildren() == null) {
							parent.setChildren(new ArrayList<RfpBqItem>());
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
		LOG.info("BQ ITEM IDs :: " + bqItemIds);
		RfpEventBq bq = rfpEventBqDao.findBqItemById(bqId);
		if (bq != null) {
			if (EventStatus.DRAFT != bq.getRfxEvent().getStatus() && EventStatus.SUSPENDED != bq.getRfxEvent().getStatus()) {
				throw new NotAllowedException(messageSource.getMessage("rft.bqitems.info.draft", new Object[] {}, Global.LOCALE));
			}
			for (String id : bqItemIds) {
				RfpBqItem parentItm = getBqItembyBqIdAndBqItemId(id);
				if (parentItm != null && parentItm.getParent() == null) {
					rfpBqItemDao.deleteChildItems(id, bqId);
				}
			}

			rfpBqItemDao.deleteRemaingBqItems(bqItemIds, bq.getId());

			// bqId = rfpBqItemDao.deleteBqItems(bqItemIds, bq.getId());
		}

	}

	@Override
	public boolean isBqExists(BqPojo bqPojo) {
		RfpEventBq bq = new RfpEventBq();
		bq.setId(bqPojo.getId());
		bq.setName(bqPojo.getBqName());
		return rfpEventBqDao.isExists(bq, bqPojo.getEventId());
	}

	@Override
	public boolean isBqItemExists(RfpBqItem item, String bqId, String parentId) {
		return rfpBqItemDao.isExists(item, bqId, parentId);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
	public RfpEventBq getBqById(String id) {
		return rfpEventBqDao.findById(id);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateBqItem(RfpBqItem rftBqItem) {
		rfpBqItemDao.update(rftBqItem);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = { Exception.class })
	public void deletefieldInBqItems(String bqId, String label) {
		rfpBqItemDao.deletefieldInBqItems(bqId, label);
		rfpEventBqDao.deletefieldInBq(bqId, label);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateBq(RfpEventBq rftBq) {
		rfpEventBqDao.update(rftBq);

	}

	@Override
	@Transactional(readOnly = false)
	public void deleteBq(String id) {
		rfpEventBqDao.deleteBQ(id);
	}

	@Override
	public void isAllowtoDeleteBQ(String id, RfxTypes eventType) throws Exception {
		rfpEventBqDao.isAllowtoDeleteBQ(id, eventType);
	}

	@Override
	public RfpBqItem getBqItembyBqId(String id) {
		return rfpBqItemDao.findById(id);
	}

	@Override
	public RfpBqItem getBqItembyBqIdAndBqItemId(String bqItemId) {
		return rfpBqItemDao.getBqItemByBqIdAndBqItemId(bqItemId);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void reorderBqItems(BqItemPojo bqItemPojo) throws NotAllowedException {
		LOG.info("BQ ITEM Object :: " + bqItemPojo.toString());
		int newOrder = bqItemPojo.getOrder();
		RfpBqItem bqItem = getBqItembyBqItemId(bqItemPojo.getId());

		if (CollectionUtil.isNotEmpty(bqItem.getChildren()) && bqItemPojo.getParent() != null) {
			throw new NotAllowedException("Bill of Quantities Item cannot be made a child if it has sub items");
		}

		LOG.info("DB BQ ITEM DETAILS ::" + bqItem.toString());
		int oldOrder = bqItem.getOrder();
		LOG.info("Item Old Position :: " + oldOrder + " :: Item New Position :: " + newOrder);
		int oldLevel = bqItem.getLevel();
		int newLevel = bqItemPojo.getOrder(); // this will be ignored if it is made a child
		RfpBqItem newParent = null;
		if (bqItemPojo.getParent() != null && StringUtils.checkString(bqItemPojo.getParent()).length() > 0) {
			newParent = getBqItembyBqItemId(bqItemPojo.getParent());
		}
		RfpBqItem oldParent = bqItem.getParent();

		// If these are not child, their order should be reset to 0
		if (oldParent == null) {
			oldOrder = 0;
		}
		if (newParent == null) {
			newOrder = 0;
		}

		// Update it to new position.
		bqItem.setOrder(newOrder);
		bqItem.setLevel(newParent == null ? bqItemPojo.getOrder() : 0);
		bqItem.setParent(newParent);
		rfpBqItemDao.updateItemOrder(bqItem, bqItem.getBq().getId(), (oldParent == null ? null : oldParent.getId()), (newParent == null ? null : newParent.getId()), oldOrder, newOrder, oldLevel, newLevel);
	}

	@Override
	@Transactional(readOnly = false)
	public RfpBqItem getBqItembyBqItemId(String itemId) {
		return rfpBqItemDao.getBqItembyBqItemId(itemId);

	}

	@Override
	@Transactional(readOnly = false)
	public void deleteAllBq(String eventId, String eventRequirement) {

		rfpEnvelopDao.removeBqsFromEnvelops(eventId);
		rfpBqItemDao.deleteBqItemsForEventId(eventId);
		rfpEventBqDao.deleteBQForEventId(eventId);

		RfpEvent event = rfpEventDao.findById(eventId);
		event.setBqCompleted(Boolean.FALSE);
		event.setBillOfQuantity(Boolean.FALSE);
		rfpEventDao.update(event);
	}

	@Override
	public List<RfpEventBq> getNotAssignedBqIdsByEventId(String eventId) {
		return rfpEnvelopDao.getNotAssignedBqIdsByEventId(eventId);
	}

	@Override
	public Integer getCountOfBqByEventId(String eventId) {
		return rfpEventBqDao.getCountOfBqByEventId(eventId);
	}

	@Override
	public List<RfpBqItem> getAllBqItemListByEventId(String eventId) {
		return rfpBqItemDao.findBqItemListByEventId(eventId);
	}

	@Override
	public List<RfpSupplierBqItem> getAllSupplierBqItemByBqIdAndSupplierId(String bqId, String supplierId) {
		List<RfpSupplierBqItem> returnList = new ArrayList<RfpSupplierBqItem>();
		List<RfpSupplierBqItem> list = rfpBqItemDao.findSupplierBqItemByListByBqIdAndSupplierId(bqId, supplierId);
		bulidSupplierBqItemList(returnList, list);
		return returnList;
	}

	private void bulidSupplierBqItemList(List<RfpSupplierBqItem> returnList, List<RfpSupplierBqItem> list) {
		LOG.info("BUILDING BQ ITEM LIST :: " + list.toString());
		if (CollectionUtil.isNotEmpty(list)) {
			for (RfpSupplierBqItem item : list) {
				RfpSupplierBqItem parent = item.createShallowCopy();
				LOG.info("BQ PARENT ITEMS :: " + parent.toString());
				returnList.add(parent);
				if (CollectionUtil.isNotEmpty(item.getChildren())) {
					LOG.info("BQ CHILDREN :: :: ");
					for (RfpSupplierBqItem child : item.getChildren()) {
						LOG.info("BQ CHILD LIST :: " + child.toString());
						if (parent.getChildren() == null) {
							parent.setChildren(new ArrayList<RfpSupplierBqItem>());
						}
						parent.getChildren().add(child.createShallowCopy());
					}
				}
			}
		}
	}

	@Override
	public List<Bq> findBqbyEventId(String eventId) {
		return rfpEventBqDao.findBqbyEventId(eventId);
	}

	@Override
	public List<RfpBqItem> getAllBqitemsbyBqId(String bqId) {
		return rfpBqItemDao.getAllBqItemsbybqId(bqId);
	}

	@Override
	public RfpBqItem getParentbyLevelId(String bqId, Integer level) {
		return rfpBqItemDao.getParentbyLevelId(bqId, level);
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
		BqFileParser<RfpBqItem> rfpBi = new BqFileParser<RfpBqItem>(RfpBqItem.class);
		List<RfpBqItem> rfpBqList = rfpBi.parse(convFile);

		// adding columns while uploading BQs
		Workbook workbook = null;
		Sheet sheet = null;
		try {
			workbook = WorkbookFactory.create(convFile);
			sheet = workbook.getSheetAt(0);
		} catch (InvalidFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		RfpEventBq rfpBq = null;
		if (sheet != null) {
			rfpBq = getBqById(bqId);
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
			buildAddNewColumnsToUpload(rfpBq, columnsTitle);
			rfpBq = updateRfpBqFields(rfpBq);
		}

		int count = 1;
		List<String> columns = new ArrayList<String>();
		// Delete existing BQ Items by Bqs.
		rfpBqItemDao.deleteBqItemsbyBqid(bqId);
		for (RfpBqItem bi : rfpBqList) {
			RfpBqItem rfpBqItem = new RfpBqItem();
			rfpBq = rfpEventBqDao.findBqItemById(bqId);

			// checking Extra column in Excel match with bq columns
			if (count == 1 && CollectionUtil.isNotEmpty(bi.getColumnTitles())) {

				if (StringUtils.checkString(rfpBq.getField1Label()).length() > 0) {
					columns.add(rfpBq.getField1Label());
				}
				if (StringUtils.checkString(rfpBq.getField2Label()).length() > 0) {
					columns.add(rfpBq.getField2Label());
				}
				if (StringUtils.checkString(rfpBq.getField3Label()).length() > 0) {
					columns.add(rfpBq.getField3Label());
				}
				if (StringUtils.checkString(rfpBq.getField4Label()).length() > 0) {
					columns.add(rfpBq.getField4Label());
				}

				if (StringUtils.checkString(rfpBq.getField5Label()).length() > 0) {
					columns.add(rfpBq.getField5Label());
				}
				if (StringUtils.checkString(rfpBq.getField6Label()).length() > 0) {
					columns.add(rfpBq.getField6Label());
				}
				if (StringUtils.checkString(rfpBq.getField7Label()).length() > 0) {
					columns.add(rfpBq.getField7Label());
				}
				if (StringUtils.checkString(rfpBq.getField8Label()).length() > 0) {
					columns.add(rfpBq.getField8Label());
				}
				if (StringUtils.checkString(rfpBq.getField9Label()).length() > 0) {
					columns.add(rfpBq.getField9Label());
				}
				if (StringUtils.checkString(rfpBq.getField10Label()).length() > 0) {
					columns.add(rfpBq.getField10Label());
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
			rfpBqItem.setBq(rfpBq);
			RfpEvent event = rfpEventDao.findById(eventId);
			rfpBqItem.setRfxEvent(event);

			rfpBqItem.setItemName(bi.getItemName());
			rfpBqItem.setItemDescription(bi.getItemDescription());
			rfpBqItem.setLevel(bi.getLevel());
			rfpBqItem.setOrder(bi.getOrder());

			// For Parent below fields set to null
			if (bi.getOrder() == 0) {
				rfpBqItem.setUom(null);
				rfpBqItem.setUnitPrice(null);
				rfpBqItem.setUnitPriceType(null);
				rfpBqItem.setQuantity(null);
				rfpBqItem.setPriceType(null);
				rfpBqItem.setField1(null);
				rfpBqItem.setField2(null);
				rfpBqItem.setField3(null);
				rfpBqItem.setField4(null);
				rfpBqItem.setField5(null);
				rfpBqItem.setField6(null);
				rfpBqItem.setField7(null);
				rfpBqItem.setField8(null);
				rfpBqItem.setField9(null);
				rfpBqItem.setField10(null);
			} else {
				RfpBqItem parentBq = rfpBqItemDao.getParentbyLevelId(bqId, bi.getLevel());
				rfpBqItem.setParent(parentBq);

				rfpBqItem.setUnitPrice(bi.getUnitPrice());
				rfpBqItem.setQuantity(bi.getQuantity().setScale((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), RoundingMode.HALF_EVEN));
				rfpBqItem.setPriceType(bi.getPriceType());
				if (bi.getUom() != null)
					rfpBqItem.setUom(uomService.getUombyCode(bi.getUom().getUom(), tenantId));
				// if invalid UOM
				if (rfpBqItem.getUom() == null) {
					throw new ExcelParseException(messageSource.getMessage("file.parse.uom.invalid.found", new Object[] { bi.getUom().getUom() }, Global.LOCALE), convFile.getName());
				}
				if (StringUtils.checkString(rfpBq.getField1Label()).length() > 0) {
					if (Boolean.TRUE == rfpBq.getField1Required() && StringUtils.checkString(bi.getField1()).length() == 0) {
						throw new ExcelParseException(rfpBq.getField1Label() + " is required for " + (bi.getLevel()) + "." + (bi.getOrder()) + " " + bi.getItemName(), convFile.getName());
					}
					rfpBqItem.setField1(bi.getField1());
				}
				if (StringUtils.checkString(rfpBq.getField2Label()).length() > 0) {
					if (Boolean.TRUE == rfpBq.getField2Required() && StringUtils.checkString(bi.getField2()).length() == 0) {
						throw new ExcelParseException(rfpBq.getField2Label() + " is required for " + (bi.getLevel()) + "." + (bi.getOrder()) + " " + bi.getItemName(), convFile.getName());
					}
					rfpBqItem.setField2(bi.getField2());
				}
				if (StringUtils.checkString(rfpBq.getField3Label()).length() > 0) {
					if (Boolean.TRUE == rfpBq.getField3Required() && StringUtils.checkString(bi.getField3()).length() == 0) {
						throw new ExcelParseException(rfpBq.getField3Label() + " is required for " + (bi.getLevel()) + "." + (bi.getOrder()) + " " + bi.getItemName(), convFile.getName());
					}
					rfpBqItem.setField3(bi.getField3());
				}
				if (StringUtils.checkString(rfpBq.getField4Label()).length() > 0) {
					if (Boolean.TRUE == rfpBq.getField4Required() && StringUtils.checkString(bi.getField4()).length() == 0) {
						throw new ExcelParseException(rfpBq.getField4Label() + " is required for " + (bi.getLevel()) + "." + (bi.getOrder()) + " " + bi.getItemName(), convFile.getName());
					}
					rfpBqItem.setField4(bi.getField4());
				}
				if (StringUtils.checkString(rfpBq.getField5Label()).length() > 0) {
					if (Boolean.TRUE == rfpBq.getField5Required() && StringUtils.checkString(bi.getField5()).length() == 0) {
						throw new ExcelParseException(rfpBq.getField5Label() + " is required for " + (bi.getLevel()) + "." + (bi.getOrder()) + " " + bi.getItemName(), convFile.getName());
					}
					rfpBqItem.setField5(bi.getField5());
				}
				if (StringUtils.checkString(rfpBq.getField6Label()).length() > 0) {
					if (Boolean.TRUE == rfpBq.getField6Required() && StringUtils.checkString(bi.getField6()).length() == 0) {
						throw new ExcelParseException(rfpBq.getField6Label() + " is required for " + (bi.getLevel()) + "." + (bi.getOrder()) + " " + bi.getItemName(), convFile.getName());
					}
					rfpBqItem.setField6(bi.getField6());
				}
				if (StringUtils.checkString(rfpBq.getField7Label()).length() > 0) {
					if (Boolean.TRUE == rfpBq.getField7Required() && StringUtils.checkString(bi.getField7()).length() == 0) {
						throw new ExcelParseException(rfpBq.getField7Label() + " is required for " + (bi.getLevel()) + "." + (bi.getOrder()) + " " + bi.getItemName(), convFile.getName());
					}
					rfpBqItem.setField7(bi.getField7());
				}
				if (StringUtils.checkString(rfpBq.getField8Label()).length() > 0) {
					if (Boolean.TRUE == rfpBq.getField8Required() && StringUtils.checkString(bi.getField8()).length() == 0) {
						throw new ExcelParseException(rfpBq.getField8Label() + " is required for " + (bi.getLevel()) + "." + (bi.getOrder()) + " " + bi.getItemName(), convFile.getName());
					}
					rfpBqItem.setField8(bi.getField8());
				}
				if (StringUtils.checkString(rfpBq.getField9Label()).length() > 0) {
					if (Boolean.TRUE == rfpBq.getField9Required() && StringUtils.checkString(bi.getField9()).length() == 0) {
						throw new ExcelParseException(rfpBq.getField9Label() + " is required for " + (bi.getLevel()) + "." + (bi.getOrder()) + " " + bi.getItemName(), convFile.getName());
					}
					rfpBqItem.setField9(bi.getField9());
				}
				if (StringUtils.checkString(rfpBq.getField10Label()).length() > 0) {
					if (Boolean.TRUE == rfpBq.getField10Required() && StringUtils.checkString(bi.getField10()).length() == 0) {
						throw new ExcelParseException(rfpBq.getField10Label() + " is required for " + (bi.getLevel()) + "." + (bi.getOrder()) + " " + bi.getItemName(), convFile.getName());
					}
					rfpBqItem.setField10(bi.getField10());
				}
			}
			saveUpdateRfpBqItem(rfpBqItem);

		}
		return rfpBqList.size();
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public RfpEventBq updateRfpBqFields(RfpEventBq eventBq) {

		rfpEventBqDao.update(eventBq);

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
		rfpBqItemDao.resetFieldsForFilledBySupplier(eventBq.getId(), label);
		return eventBq;
	}

	@Override
	public List<String> getNotSectionAddedRfpBqIdsByEventId(String eventId) {
		return rfpEventBqDao.getNotSectionAddedRfpBqIdsByEventId(eventId);
	}

	@Override
	public List<RfpBqItem> getBqItemForSearchFilter(String bqId, Integer itemLevel, Integer itemOrder, String searchVal, Integer start, Integer length, Integer pageNo) {
		List<RfpBqItem> returnList = new ArrayList<RfpBqItem>();
		if (pageNo != null) {
			start = pageNo - 1;
		}
		if (length != null) {
			start = start * length;
		}
		LOG.info(" start  : " + start);
		List<RfpBqItem> bqList = rfpBqItemDao.getBqItemForSearchFilter(bqId, itemLevel, itemOrder, searchVal, start, length);
		if (CollectionUtil.isNotEmpty(bqList)) {
			for (RfpBqItem item : bqList) {
				RfpBqItem bqItem = item.createSearchShallowCopy();
				returnList.add(bqItem);
			}
		}
		return returnList;

	}

	@Override
	public List<BqItemPojo> getAllLevelOrderBqItemByBqId(String bqId, String searchVal) {
		return rfpBqItemDao.getAllLevelOrderBqItemByBqId(bqId, searchVal);
	}

	@Override
	public long totalBqItemCountByBqId(String bqId, String searchVal) {
		return rfpBqItemDao.totalBqItemCountByBqId(bqId, searchVal);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void deleteAllERPBqByEventId(String eventId) {
		rfpEnvelopDao.removeBqsFromEnvelops(eventId);
		rfpBqItemDao.deleteBqItemsForErpForEventId(eventId);
		rfpEventBqDao.deleteBQForEventId(eventId);

	}

	@Override
	@Transactional(readOnly = false)
	public RfpBqItem saveUpdateRfpBqItem(RfpBqItem rfpBqItem) {
		return rfpBqItemDao.saveOrUpdate(rfpBqItem);
	}

	@Override
	public List<String> getNotSectionItemAddedRfpBqIdsByEventId(String eventId) {
		return rfpEventBqDao.getNotSectionItemAddedRfpBqIdsByEventId(eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteAllBqItems(String bqId) throws NoSuchMessageException, NotAllowedException {
		RfpEventBq bq = rfpEventBqDao.findBqItemById(bqId);

		if (bq != null) {
			if (EventStatus.DRAFT != bq.getRfxEvent().getStatus() && EventStatus.SUSPENDED != bq.getRfxEvent().getStatus()) {
				throw new NotAllowedException(messageSource.getMessage("rft.bqitems.info.draft", new Object[] {}, Global.LOCALE));
			}
			rfpBqItemDao.deleteBqItemsbyBqid(bq.getId());
		}
	}

	@Override
	public RfpEventBq getRfpEventBqByBqId(String bqId) {
		RfpEventBq bq = rfpEventBqDao.getRfpEventBqByBqId(bqId);
		return bq;
	}

	@Override
	public List<RfpEventBq> getAllBqListByEventIdByOrder(String eventId) {
		return rfpEventBqDao.findBqsByEventIdByOrder(eventId);
	}

}

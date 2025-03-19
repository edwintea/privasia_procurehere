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

import com.privasia.procurehere.core.dao.RfqBqDao;
import com.privasia.procurehere.core.dao.RfqBqItemDao;
import com.privasia.procurehere.core.dao.RfqEnvelopDao;
import com.privasia.procurehere.core.dao.RfqEventDao;
import com.privasia.procurehere.core.dao.RfqSupplierBqItemDao;
import com.privasia.procurehere.core.entity.Bq;
import com.privasia.procurehere.core.entity.Owner;
import com.privasia.procurehere.core.entity.RfqBqItem;
import com.privasia.procurehere.core.entity.RfqEvent;
import com.privasia.procurehere.core.entity.RfqEventBq;
import com.privasia.procurehere.core.entity.RfqSupplierBqItem;
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
import com.privasia.procurehere.service.RfqBqService;
import com.privasia.procurehere.service.UomService;

@Service
@Transactional(readOnly = true)
public class RfqBqServiceImpl implements RfqBqService {

	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@Autowired
	MessageSource messageSource;

	@Autowired
	RfqEventDao rfqEventDao;

	@Autowired
	RfqBqDao rfqBqDao;

	@Autowired
	RfqBqItemDao rfqBqItemDao;

	@Autowired
	RfqEnvelopDao rfqEnvelopDao;

	@Autowired
	RfqSupplierBqItemDao rfqSupplierBqItemDao;

	@Autowired
	UomService uomService;

	@Autowired
	RfqBqService rfqBqService;

	@Override
	public RfqEvent getEventById(String id) {
		RfqEvent rfq = rfqEventDao.findEventForBqByEventId(id);
		if (rfq.getEventOwner() != null) {
			rfq.getEventOwner().getName();
			rfq.getEventOwner().getCommunicationEmail();
			rfq.getEventOwner().getPhoneNumber();
			if (rfq.getEventOwner().getOwner() != null) {
				Owner usr = rfq.getEventOwner().getOwner();
				usr.getCompanyContactNumber();
			}
		}
		if (rfq != null) {
			if (rfq.getBaseCurrency() != null) {
				rfq.getBaseCurrency().getCurrencyName();
			}
		}
		return rfq;
	}

	@Override
	public List<RfqBqItem> findAllEventBqbybqId(String bqId) {
		List<RfqBqItem> returnList = new ArrayList<RfqBqItem>();
		List<RfqBqItem> list = rfqBqItemDao.getListBqItemsbyId(bqId);
		if (CollectionUtil.isNotEmpty(list)) {
			for (RfqBqItem item : list) {
				RfqBqItem parent = item.createShallowCopy();
				returnList.add(parent);
				if (CollectionUtil.isNotEmpty(item.getChildren())) {
					for (RfqBqItem child : item.getChildren()) {
						LOG.info("CHILD : " + child.getItemName());
						if (parent.getChildren() == null) {
							parent.setChildren(new ArrayList<RfqBqItem>());
						}
						parent.getChildren().add(child.createShallowCopy());
					}
				}
			}
		}
		return returnList;
	}

	@Override
	public RfqBqItem getBqItemById(String id) {
		return rfqBqItemDao.findById(id);
	}

	@Override
	@Transactional(readOnly = false)
	public RfqBqItem saveBqItem(RfqBqItem rftBqItem) {
		if (rftBqItem.getParent() == null) {
			int itemLevel = 0;
			List<RfqBqItem> list = rfqBqItemDao.getBqItemLevelOrder(rftBqItem.getBq().getId());
			if (CollectionUtil.isNotEmpty(list)) {
				itemLevel = list.size();
			}
			LOG.info("ITEM LEVEL :: " + itemLevel);
			rftBqItem.setLevel(itemLevel + 1);
			rftBqItem.setOrder(0);
		} else {
			RfqBqItem parent = getBqItemsbyBqId(rftBqItem.getParent().getId());
			if (rftBqItem.getParent() != null) {
				rftBqItem.setLevel(parent.getLevel());
				rftBqItem.setOrder(CollectionUtil.isEmpty(parent.getChildren()) ? 1 : parent.getChildren().size() + 1);
			}
		}
		return rfqBqItemDao.saveOrUpdate(rftBqItem);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
	public RfqBqItem getBqItemsbyBqId(String parent) {
		return rfqBqItemDao.findById(parent);
	}

	@Override
	public boolean isBqItemExists(RfqBqItem item, String bqId, String parentId) {
		return rfqBqItemDao.isExists(item, bqId, parentId);
	}

	@Override
	public List<RfqEventBq> getAllBqListByEventId(String id) {
		return rfqBqDao.findBqsByEventId(id);
	}

	@Override
	public List<RfqEventBq> getEventBqForEventIdForEnvelop(String id, List<String> bqIds) {
		return rfqBqDao.findBqsByEventIdForEnvelop(id, bqIds);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
	public RfqEventBq getBqItembyId(String id) {
		return rfqBqDao.findBqItemById(id);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public RfqEventBq saveBq(RfqEventBq rftEventBq) {
		return rfqBqDao.saveOrUpdate(rftEventBq);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateBqItems(RfqBqItem rftBqItem) {
		rfqBqItemDao.update(rftBqItem);
	}

	@Override
	public List<RfqBqItem> getBqItemsbyId(String bqId) {
		return rfqBqItemDao.getListBqItemsbyId(bqId);
	}

	@Override
	public List<RfqBqItem> findBqbyBqId(String bqId) {
		LOG.info("Find BQ By ID :: " + bqId);
		List<RfqBqItem> returnList = new ArrayList<RfqBqItem>();
		List<RfqBqItem> list = rfqBqItemDao.getListBqItemsbyId(bqId);
		bulidBqItemList(returnList, list);
		return returnList;
	}

	/**
	 * @param returnList
	 * @param list
	 */
	private void bulidBqItemList(List<RfqBqItem> returnList, List<RfqBqItem> list) {
		LOG.info("BUILDING BQ ITEM LIST :: " + list.toString());
		if (CollectionUtil.isNotEmpty(list)) {
			for (RfqBqItem item : list) {
				RfqBqItem parent = item.createShallowCopy();
				LOG.info("BQ PARENT ITEMS :: " + parent.toString());
				returnList.add(parent);
				if (CollectionUtil.isNotEmpty(item.getChildren())) {
					LOG.info("BQ CHILDREN :: :: ");
					for (RfqBqItem child : item.getChildren()) {
						LOG.info("BQ CHILD LIST :: " + child.toString());
						if (parent.getChildren() == null) {
							parent.setChildren(new ArrayList<RfqBqItem>());
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
		RfqEventBq bq = rfqBqDao.findBqItemById(bqId);
		if (bq != null) {
			if (EventStatus.DRAFT != bq.getRfxEvent().getStatus() && EventStatus.SUSPENDED != bq.getRfxEvent().getStatus()) {
				throw new NotAllowedException(messageSource.getMessage("rft.bqitems.info.draft", new Object[] {}, Global.LOCALE));
			}

			for (String id : bqItemIds) {
				RfqBqItem parentItm = getBqItembyBqIdAndBqItemId(id);
				if (parentItm != null && parentItm.getParent() == null) {
					rfqBqItemDao.deleteChildItems(id, bqId);
				}
			}

			rfqBqItemDao.deleteRemaingBqItems(bqItemIds, bq.getId());
		}
	}

	@Override
	public boolean isBqExists(BqPojo rftBqPojo) {
		RfqEventBq bq = new RfqEventBq();
		bq.setId(rftBqPojo.getId());
		bq.setName(rftBqPojo.getBqName());
		return rfqBqDao.isExists(bq, rftBqPojo.getEventId());
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
	public RfqEventBq getBqById(String id) {
		return rfqBqDao.findById(id);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateBqItem(RfqBqItem rftBqItem) {
		rfqBqItemDao.update(rftBqItem);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = { Exception.class })
	public void deletefieldInBqItems(String bqId, String label) {
		rfqBqItemDao.deletefieldInBqItems(bqId, label);
		rfqBqDao.deletefieldInBq(bqId, label);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateBq(RfqEventBq rftBq) {
		rfqBqDao.update(rftBq);

	}

	@Override
	@Transactional(readOnly = false)
	public void deleteBq(String id) {
		rfqBqDao.deleteBQ(id);
	}

	@Override
	public void isAllowtoDeleteBQ(String id, RfxTypes eventType) throws Exception {
		rfqBqDao.isAllowtoDeleteBQ(id, eventType);
	}

	@Override
	public RfqBqItem getBqItembyBqId(String id) {
		return rfqBqItemDao.findById(id);
	}

	@Override
	public RfqBqItem getBqItembyBqIdAndBqItemId(String bqItemId) {
		return rfqBqItemDao.getBqItemByBqIdAndBqItemId(bqItemId);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void reorderBqItems(BqItemPojo rftBqItemPojo) throws NotAllowedException {
		LOG.info("BQ ITEM Object :: " + rftBqItemPojo.toString());
		int newOrder = rftBqItemPojo.getOrder();
		RfqBqItem bqItem = getBqItembyBqItemId(rftBqItemPojo.getId());

		if (CollectionUtil.isNotEmpty(bqItem.getChildren()) && rftBqItemPojo.getParent() != null) {
			throw new NotAllowedException("Bill of Quantities Item cannot be made a child if it has sub items");
		}

		LOG.info("DB BQ ITEM DETAILS ::" + bqItem.toString());
		int oldOrder = bqItem.getOrder();
		LOG.info("Item Old Position :: " + oldOrder + " :: Item New Position :: " + newOrder);
		int oldLevel = bqItem.getLevel();
		int newLevel = rftBqItemPojo.getOrder(); // this will be ignored if it is made a child
		RfqBqItem newParent = null;
		if (rftBqItemPojo.getParent() != null) {
			newParent = getBqItembyBqItemId(rftBqItemPojo.getParent());
		}
		RfqBqItem oldParent = bqItem.getParent();

		// If these are not child, their order should be reset to 0
		if (oldParent == null) {
			oldOrder = 0;
		}
		if (newParent == null) {
			newOrder = 0;
		}

		// Update it to new position.
		bqItem.setOrder(newOrder);
		bqItem.setLevel(newParent == null ? rftBqItemPojo.getOrder() : newParent.getLevel());
		bqItem.setParent(newParent);
		rfqBqItemDao.updateItemOrder(bqItem, bqItem.getBq().getId(), (oldParent == null ? null : oldParent.getId()), (newParent == null ? null : newParent.getId()), oldOrder, newOrder, oldLevel, newLevel);
	}

	@Override
	@Transactional(readOnly = false)
	public RfqBqItem getBqItembyBqItemId(String itemId) {
		return rfqBqItemDao.getBqItembyBqItemId(itemId);

	}

	@Override
	@Transactional(readOnly = false)
	public void deleteAllBq(String eventId, String eventRequirement) {
		rfqEnvelopDao.removeBqsFromEnvelops(eventId);
		rfqBqItemDao.deleteBqItemsForEventId(eventId);
		rfqBqDao.deleteBQForEventId(eventId);

		RfqEvent event = rfqEventDao.findById(eventId);
		event.setBqCompleted(Boolean.FALSE);
		event.setBillOfQuantity(Boolean.FALSE);
		rfqEventDao.update(event);
	}

	@Override
	public List<RfqEventBq> getNotAssignedBqIdsByEventId(String eventId) {
		return rfqEnvelopDao.getNotAssignedBqIdsByEventId(eventId);
	}

	@Override
	public Integer getCountOfBqByEventId(String eventId) {
		return rfqBqDao.getCountOfBqByEventId(eventId);
	}

	@Override
	public List<RfqBqItem> getAllBqItemListByEventId(String eventId) {
		return rfqBqItemDao.findBqItemListByEventId(eventId);
	}

	@Override
	public List<RfqSupplierBqItem> getAllSupplierBqItemByBqIdAndSupplierId(String bqId, String supplierId) {
		List<RfqSupplierBqItem> returnList = new ArrayList<RfqSupplierBqItem>();
		List<RfqSupplierBqItem> list = rfqSupplierBqItemDao.findSupplierBqItemByListByBqIdAndSupplierId(bqId, supplierId);
		bulidSupplierBqItemList(returnList, list);
		return returnList;
	}

	private void bulidSupplierBqItemList(List<RfqSupplierBqItem> returnList, List<RfqSupplierBqItem> list) {
		LOG.info("BUILDING BQ ITEM LIST :: " + list.toString());
		if (CollectionUtil.isNotEmpty(list)) {
			for (RfqSupplierBqItem item : list) {
				RfqSupplierBqItem parent = item.createShallowCopy();
				LOG.info("BQ PARENT ITEMS :: " + parent.toString());
				returnList.add(parent);
				if (CollectionUtil.isNotEmpty(item.getChildren())) {
					LOG.info("BQ CHILDREN :: :: ");
					for (RfqSupplierBqItem child : item.getChildren()) {
						LOG.info("BQ CHILD LIST :: " + child.toString());
						if (parent.getChildren() == null) {
							parent.setChildren(new ArrayList<RfqSupplierBqItem>());
						}
						parent.getChildren().add(child.createShallowCopy());
					}
				}
			}
		}
	}

	@Override
	public List<Bq> findBqbyEventId(String eventId) {
		return rfqBqDao.findBqbyEventId(eventId);
	}

	@Override
	public RfqBqItem getParentbyLevelId(String bqId, Integer level) {
		return rfqBqItemDao.getParentbyLevelId(bqId, level);
	}

	@Override
	public List<RfqBqItem> getAllBqitemsbyBqId(String bqId) {
		return rfqBqItemDao.getAllBqItemsbybqId(bqId);
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
		BqFileParser<RfqBqItem> rfqBi = new BqFileParser<RfqBqItem>(RfqBqItem.class);
		List<RfqBqItem> rfqBqList = rfqBi.parse(convFile);

		// Delete existing BQ Items by Bqs.
		rfqBqItemDao.deleteBqItemsbyBqid(bqId);

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
		RfqEventBq rfqBq = null;
		if (sheet != null) {
			rfqBq = getBqById(bqId);
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
			buildAddNewColumnsToUpload(rfqBq, columnsTitle);
			rfqBq = updateRfqBqFields(rfqBq);
		}

		int count = 1;
		List<String> columns = new ArrayList<String>();
		for (RfqBqItem bi : rfqBqList) {
			RfqBqItem rfqBqItem = new RfqBqItem();
			// RfqEventBq rfqBq = rfqBqDao.findBqItemById(bqId);

			// checking Extra column in Excel match with bq columns
			if (count == 1 && CollectionUtil.isNotEmpty(bi.getColumnTitles())) {

				if (StringUtils.checkString(rfqBq.getField1Label()).length() > 0) {
					columns.add(rfqBq.getField1Label());
				}
				if (StringUtils.checkString(rfqBq.getField2Label()).length() > 0) {
					columns.add(rfqBq.getField2Label());
				}
				if (StringUtils.checkString(rfqBq.getField3Label()).length() > 0) {
					columns.add(rfqBq.getField3Label());
				}
				if (StringUtils.checkString(rfqBq.getField4Label()).length() > 0) {
					columns.add(rfqBq.getField4Label());
				}

				if (StringUtils.checkString(rfqBq.getField5Label()).length() > 0) {
					columns.add(rfqBq.getField5Label());
				}
				if (StringUtils.checkString(rfqBq.getField6Label()).length() > 0) {
					columns.add(rfqBq.getField6Label());
				}
				if (StringUtils.checkString(rfqBq.getField7Label()).length() > 0) {
					columns.add(rfqBq.getField7Label());
				}
				if (StringUtils.checkString(rfqBq.getField8Label()).length() > 0) {
					columns.add(rfqBq.getField8Label());
				}
				if (StringUtils.checkString(rfqBq.getField9Label()).length() > 0) {
					columns.add(rfqBq.getField9Label());
				}
				if (StringUtils.checkString(rfqBq.getField10Label()).length() > 0) {
					columns.add(rfqBq.getField10Label());
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
			rfqBqItem.setBq(rfqBq);
			RfqEvent event = rfqEventDao.findEventForBqByEventId(eventId);
			rfqBqItem.setRfxEvent(event);

			rfqBqItem.setItemName(bi.getItemName());

			rfqBqItem.setItemDescription(bi.getItemDescription());
			rfqBqItem.setLevel(bi.getLevel());
			rfqBqItem.setOrder(bi.getOrder());

			// For Parent below fields set to null
			if (bi.getOrder() == 0) {
				rfqBqItem.setUom(null);
				rfqBqItem.setUnitPrice(null);
				rfqBqItem.setUnitPriceType(null);
				rfqBqItem.setQuantity(null);
				rfqBqItem.setPriceType(null);
				rfqBqItem.setField1(null);
				rfqBqItem.setField2(null);
				rfqBqItem.setField3(null);
				rfqBqItem.setField4(null);
				rfqBqItem.setField5(null);
				rfqBqItem.setField6(null);
				rfqBqItem.setField7(null);
				rfqBqItem.setField8(null);
				rfqBqItem.setField9(null);
				rfqBqItem.setField10(null);
			} else {
				RfqBqItem parentBq = rfqBqItemDao.getParentbyLevelId(bqId, bi.getLevel());
				rfqBqItem.setParent(parentBq);

				rfqBqItem.setUnitPrice(bi.getUnitPrice());
				rfqBqItem.setQuantity(bi.getQuantity().setScale((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), RoundingMode.HALF_EVEN));
				rfqBqItem.setPriceType(bi.getPriceType());
				if (bi.getUom() != null)
					rfqBqItem.setUom(uomService.getUombyCode(bi.getUom().getUom(), tenantId));
				// if invalid UOM
				if (rfqBqItem.getUom() == null) {
					throw new ExcelParseException(messageSource.getMessage("file.parse.uom.invalid.found", new Object[] { bi.getUom().getUom() }, Global.LOCALE), convFile.getName());
				}
				if (StringUtils.checkString(rfqBq.getField1Label()).length() > 0) {
					if (Boolean.TRUE == rfqBq.getField1Required() && StringUtils.checkString(bi.getField1()).length() == 0) {
						throw new ExcelParseException(rfqBq.getField1Label() + " is required for " + (bi.getLevel()) + "." + (bi.getOrder()) + " " + bi.getItemName(), convFile.getName());
					}
					rfqBqItem.setField1(bi.getField1());
				}
				if (StringUtils.checkString(rfqBq.getField2Label()).length() > 0) {
					if (Boolean.TRUE == rfqBq.getField2Required() && StringUtils.checkString(bi.getField2()).length() == 0) {
						throw new ExcelParseException(rfqBq.getField2Label() + " is required for " + (bi.getLevel()) + "." + (bi.getOrder()) + " " + bi.getItemName(), convFile.getName());
					}
					rfqBqItem.setField2(bi.getField2());
				}
				if (StringUtils.checkString(rfqBq.getField3Label()).length() > 0) {
					if (Boolean.TRUE == rfqBq.getField3Required() && StringUtils.checkString(bi.getField3()).length() == 0) {
						throw new ExcelParseException(rfqBq.getField3Label() + " is required for " + (bi.getLevel()) + "." + (bi.getOrder()) + " " + bi.getItemName(), convFile.getName());
					}
					rfqBqItem.setField3(bi.getField3());
				}
				if (StringUtils.checkString(rfqBq.getField4Label()).length() > 0) {
					if (Boolean.TRUE == rfqBq.getField4Required() && StringUtils.checkString(bi.getField4()).length() == 0) {
						throw new ExcelParseException(rfqBq.getField4Label() + " is required for " + (bi.getLevel()) + "." + (bi.getOrder()) + " " + bi.getItemName(), convFile.getName());
					}
					rfqBqItem.setField4(bi.getField4());
				}
				if (StringUtils.checkString(rfqBq.getField5Label()).length() > 0) {
					if (Boolean.TRUE == rfqBq.getField5Required() && StringUtils.checkString(bi.getField5()).length() == 0) {
						throw new ExcelParseException(rfqBq.getField5Label() + " is required for " + (bi.getLevel()) + "." + (bi.getOrder()) + " " + bi.getItemName(), convFile.getName());
					}
					rfqBqItem.setField5(bi.getField5());
				}
				if (StringUtils.checkString(rfqBq.getField6Label()).length() > 0) {
					if (Boolean.TRUE == rfqBq.getField6Required() && StringUtils.checkString(bi.getField6()).length() == 0) {
						throw new ExcelParseException(rfqBq.getField6Label() + " is required for " + (bi.getLevel()) + "." + (bi.getOrder()) + " " + bi.getItemName(), convFile.getName());
					}
					rfqBqItem.setField6(bi.getField6());
				}
				if (StringUtils.checkString(rfqBq.getField7Label()).length() > 0) {
					if (Boolean.TRUE == rfqBq.getField7Required() && StringUtils.checkString(bi.getField7()).length() == 0) {
						throw new ExcelParseException(rfqBq.getField7Label() + " is required for " + (bi.getLevel()) + "." + (bi.getOrder()) + " " + bi.getItemName(), convFile.getName());
					}
					rfqBqItem.setField7(bi.getField7());
				}
				if (StringUtils.checkString(rfqBq.getField8Label()).length() > 0) {
					if (Boolean.TRUE == rfqBq.getField8Required() && StringUtils.checkString(bi.getField8()).length() == 0) {
						throw new ExcelParseException(rfqBq.getField8Label() + " is required for " + (bi.getLevel()) + "." + (bi.getOrder()) + " " + bi.getItemName(), convFile.getName());
					}
					rfqBqItem.setField8(bi.getField8());
				}
				if (StringUtils.checkString(rfqBq.getField9Label()).length() > 0) {
					if (Boolean.TRUE == rfqBq.getField9Required() && StringUtils.checkString(bi.getField9()).length() == 0) {
						throw new ExcelParseException(rfqBq.getField9Label() + " is required for " + (bi.getLevel()) + "." + (bi.getOrder()) + " " + bi.getItemName(), convFile.getName());
					}
					rfqBqItem.setField9(bi.getField9());
				}
				if (StringUtils.checkString(rfqBq.getField10Label()).length() > 0) {
					if (Boolean.TRUE == rfqBq.getField10Required() && StringUtils.checkString(bi.getField10()).length() == 0) {
						throw new ExcelParseException(rfqBq.getField10Label() + " is required for " + (bi.getLevel()) + "." + (bi.getOrder()) + " " + bi.getItemName(), convFile.getName());
					}
					rfqBqItem.setField10(bi.getField10());
				}
			}
			saveUpdateRfqBqItem(rfqBqItem);
		}
		return rfqBqList.size();
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public RfqEventBq updateRfqBqFields(RfqEventBq eventBq) {

		rfqBqDao.update(eventBq);

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
		rfqBqItemDao.resetFieldsForFilledBySupplier(eventBq.getId(), label);
		return eventBq;
	}

	@Override
	public List<String> getNotSectionAddedRfqBqIdsByEventId(String eventId) {
		return rfqBqDao.getNotSectionAddedRfqBqIdsByEventId(eventId);
	}

	@Override
	public List<RfqBqItem> getBqItemForSearchFilter(String bqId, Integer itemLevel, Integer itemOrder, String searchVal, Integer start, Integer length, Integer pageNo) {
		List<RfqBqItem> returnList = new ArrayList<RfqBqItem>();
		if (pageNo != null) {
			start = pageNo - 1;
		}
		if (length != null) {
			start = start * length;
		}
		LOG.info(" start  : " + start);
		List<RfqBqItem> bqList = rfqBqItemDao.getBqItemForSearchFilter(bqId, itemLevel, itemOrder, searchVal, start, length);
		if (CollectionUtil.isNotEmpty(bqList)) {
			for (RfqBqItem item : bqList) {
				RfqBqItem bqItem = item.createSearchShallowCopy();
				returnList.add(bqItem);
			}
		}
		return returnList;

	}

	@Override
	public List<BqItemPojo> getAllLevelOrderBqItemByBqId(String bqId, String searchVal) {
		return rfqBqItemDao.getAllLevelOrderBqItemByBqId(bqId, searchVal);
	}

	@Override
	public long totalBqItemCountByBqId(String bqId, String searchVal) {
		return rfqBqItemDao.totalBqItemCountByBqId(bqId, searchVal);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = { Exception.class })
	public void deleteAllERPBqByEventId(String eventId) {
		LOG.info("Event Id  deleteAllBqByEventId : " + eventId);
		rfqEnvelopDao.removeBqsFromEnvelops(eventId);
		// rfqBqItemDao.deleteBqItemsForEventId(eventId);
		rfqBqItemDao.deleteBqItemsForErpForEventId(eventId);
		rfqBqDao.deleteBQForEventId(eventId);

	}

	@Override
	@Transactional(readOnly = false)
	public RfqBqItem saveUpdateRfqBqItem(RfqBqItem rfqBqItem) {
		return rfqBqItemDao.saveOrUpdate(rfqBqItem);
	}

	@Override
	public List<String> getNotSectionItemAddedRfqBqIdsByEventId(String eventId) {

		return rfqBqDao.getNotSectionItemAddedRfqBqIdsByEventId(eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteAllBqItems(String bqId) throws NoSuchMessageException, NotAllowedException {
		RfqEventBq bq = rfqBqDao.findBqItemById(bqId);

		if (bq != null) {
			if (EventStatus.DRAFT != bq.getRfxEvent().getStatus() && EventStatus.SUSPENDED != bq.getRfxEvent().getStatus()) {
				throw new NotAllowedException(messageSource.getMessage("rft.bqitems.info.draft", new Object[] {}, Global.LOCALE));
			}
			rfqBqItemDao.deleteBqItemsbyBqid(bq.getId());
		}
	}

	@Override
	public RfqEventBq getRfqEventBqByBqId(String bqId) {
		RfqEventBq bq = rfqBqDao.getRfqEventBqByBqId(bqId);
		return bq;
	}

	@Override
	public List<RfqEventBq> getAllBqListByEventIdByOrder(String eventId) {
		return rfqBqDao.findBqsByEventIdByOrder(eventId);
	}
}

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

import com.privasia.procurehere.core.dao.AuctionRulesDao;
import com.privasia.procurehere.core.dao.RfaBqDao;
import com.privasia.procurehere.core.dao.RfaBqItemDao;
import com.privasia.procurehere.core.dao.RfaEnvelopDao;
import com.privasia.procurehere.core.dao.RfaEventDao;
import com.privasia.procurehere.core.dao.RfaSupplierBqDao;
import com.privasia.procurehere.core.dao.RfaSupplierBqItemDao;
import com.privasia.procurehere.core.entity.Bq;
import com.privasia.procurehere.core.entity.Owner;
import com.privasia.procurehere.core.entity.RfaBqItem;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.entity.RfaEventBq;
import com.privasia.procurehere.core.entity.RfaSupplierBq;
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
import com.privasia.procurehere.service.RfaBqService;
import com.privasia.procurehere.service.UomService;

@Service
@Transactional(readOnly = true)
public class RfaBqServiceImpl implements RfaBqService {

	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@Autowired
	MessageSource messageSource;

	@Autowired
	RfaEventDao rfaEventDao;

	@Autowired
	RfaBqDao rfaEventBqDao;

	@Autowired
	RfaBqItemDao rfaBqItemDao;

	@Autowired
	RfaEnvelopDao rfaEnvelopDao;

	@Autowired
	RfaSupplierBqItemDao rfaSupplierBqItemDao;

	@Autowired
	RfaSupplierBqDao rfaSupplierBqDao;

	@Autowired
	UomService uomService;

	@Autowired
	AuctionRulesDao auctionRulesDao;

	@Override
	public RfaEvent getRfaEventById(String id) {
		return rfaEventDao.findById(id);
	}

	@Override
	public List<RfaBqItem> findAllRfaEventBqbybqId(String bqId) {
		List<RfaBqItem> returnList = new ArrayList<RfaBqItem>();
		List<RfaBqItem> list = rfaBqItemDao.getListBqItemsbyId(bqId);
		if (CollectionUtil.isNotEmpty(list)) {
			for (RfaBqItem item : list) {
				RfaBqItem parent = item.createShallowCopy();
				returnList.add(parent);
				if (CollectionUtil.isNotEmpty(item.getChildren())) {
					for (RfaBqItem child : item.getChildren()) {
						LOG.info("CHILD : " + child.getItemName());
						if (parent.getChildren() == null) {
							parent.setChildren(new ArrayList<RfaBqItem>());
						}
						parent.getChildren().add(child.createShallowCopy());
					}
				}
			}
		}
		return returnList;
	}

	@Override
	public RfaBqItem getRfaBqItemById(String id) {
		return rfaBqItemDao.findById(id);
	}

	@Override
	@Transactional(readOnly = false)
	public RfaBqItem saveRfaBqItem(RfaBqItem rfaBqItem) {
		// Below condition for, if data saving from EXCEL no need to set LEVEL and ORDER
		if (rfaBqItem.getLevel() == 0) {
			if (rfaBqItem.getParent() == null) {
				int itemLevel = 0;
				List<RfaBqItem> list = rfaBqItemDao.getBqItemLevelOrder(rfaBqItem.getBq().getId());
				if (CollectionUtil.isNotEmpty(list)) {
					itemLevel = list.size();
				}
				LOG.info("ITEM LEVEL :: " + itemLevel);
				rfaBqItem.setLevel(itemLevel + 1);
				rfaBqItem.setOrder(0);
			} else {
				RfaBqItem parent = getBqItemsbyBqId(rfaBqItem.getParent().getId());
				if (rfaBqItem.getParent() != null) {
					rfaBqItem.setLevel(parent.getLevel());
					rfaBqItem.setOrder(CollectionUtil.isEmpty(parent.getChildren()) ? 1 : parent.getChildren().size() + 1);
				}
			}
		}
		return rfaBqItemDao.saveOrUpdate(rfaBqItem);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
	public RfaBqItem getBqItemsbyBqId(String parent) {
		return rfaBqItemDao.findById(parent);
	}

	@Override
	public List<RfaEventBq> getAllBqListByEventId(String id) {
		return rfaEventBqDao.findBqsByEventId(id);
	}

	@Override
	public List<RfaEventBq> getRfaEventBqForEventIdForEnvelop(String id, List<String> bqIds) {
		return rfaEventBqDao.findBqsByEventIdForEnvelop(id, bqIds);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
	public RfaEventBq getBqItembyId(String id) {
		return rfaEventBqDao.findBqItemById(id);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public RfaEventBq saveRfaBq(RfaEventBq rfaEventBq) {
		return rfaEventBqDao.saveOrUpdate(rfaEventBq);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateRfaBqItems(RfaBqItem rfaBqItem) {
		rfaBqItemDao.update(rfaBqItem);
	}

	@Override
	public List<RfaBqItem> findRfaBqbyBqId(String bqId) {
		LOG.info("Find BQ By ID :: " + bqId);
		List<RfaBqItem> returnList = new ArrayList<RfaBqItem>();
		List<RfaBqItem> list = rfaBqItemDao.getListBqItemsbyId(bqId);
		bulidBqItemList(returnList, list);
		return returnList;
	}

	/**
	 * @param returnList
	 * @param list
	 */
	private void bulidBqItemList(List<RfaBqItem> returnList, List<RfaBqItem> list) {
		LOG.info("BUILDING BQ ITEM LIST ");
		if (CollectionUtil.isNotEmpty(list)) {
			for (RfaBqItem item : list) {
				RfaBqItem parent = item.createShallowCopy();
				LOG.info("BQ PARENT ITEMS :: " + parent.getItemName());
				returnList.add(parent);
				if (CollectionUtil.isNotEmpty(item.getChildren())) {
					LOG.info("BQ CHILDREN :: :: ");
					for (RfaBqItem child : item.getChildren()) {
						LOG.info("BQ CHILD LIST :: " + child.getItemName());
						if (parent.getChildren() == null) {
							parent.setChildren(new ArrayList<RfaBqItem>());
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

		RfaEventBq bq = rfaEventBqDao.findBqItemById(bqId);
		if (bq != null) {
			if (EventStatus.DRAFT != bq.getRfxEvent().getStatus() && EventStatus.SUSPENDED != bq.getRfxEvent().getStatus()) {
				throw new NotAllowedException(messageSource.getMessage("rft.bqitems.info.draft", new Object[] {}, Global.LOCALE));
			}
			for (String id : bqItemIds) {
				RfaBqItem parentItm = getBqItembyBqIdAndBqItemId(id);
				if (parentItm != null && parentItm.getParent() == null) {
					rfaBqItemDao.deleteChildItems(id, bqId);
				}
			}
			rfaBqItemDao.deleteRemaingBqItems(bqItemIds, bq.getId());
		}

	}

	@Override
	public boolean isBqExists(BqPojo bqPojo) {
		RfaEventBq bq = new RfaEventBq();
		bq.setId(bqPojo.getId());
		bq.setName(bqPojo.getBqName());
		return rfaEventBqDao.isExists(bq, bqPojo.getEventId());
	}

	@Override
	public boolean isBqItemExists(RfaBqItem rfaBqItem, String bqId, String parentId) {
		return rfaBqItemDao.isExists(rfaBqItem, bqId, parentId);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
	public RfaEventBq getRfaBqById(String id) {
		return rfaEventBqDao.findById(id);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateBqItem(RfaBqItem rfaBqItem) {
		rfaBqItemDao.update(rfaBqItem);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = { Exception.class })
	public void deletefieldInBqItems(String bqId, String label) {
		rfaBqItemDao.deletefieldInBqItems(bqId, label);
		rfaEventBqDao.deletefieldInBq(bqId, label);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateRfaBq(RfaEventBq rfaBq) {
		rfaEventBqDao.update(rfaBq);

	}

	@Override
	@Transactional(readOnly = false)
	public void deleteRfaBq(String id) {
		rfaEventBqDao.deleteBQ(id);
	}

	@Override
	public void isAllowtoDeleteBQ(String id, RfxTypes eventType) throws Exception {
		rfaEventBqDao.isAllowtoDeleteBQ(id, eventType);
	}

	@Override
	public RfaBqItem getBqItembyBqId(String id) {
		return rfaBqItemDao.findById(id);
	}

	@Override
	public RfaBqItem getBqItembyBqIdAndBqItemId(String bqItemId) {
		return rfaBqItemDao.getBqItemByBqIdAndBqItemId(bqItemId);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void reorderBqItems(BqItemPojo rfaBqItemPojo) throws NotAllowedException {
		LOG.info("BQ ITEM Object :: " + rfaBqItemPojo.toString());
		int newOrder = rfaBqItemPojo.getOrder();
		RfaBqItem bqItem = getBqItembyBqItemId(rfaBqItemPojo.getId());

		if (CollectionUtil.isNotEmpty(bqItem.getChildren()) && rfaBqItemPojo.getParent() != null) {
			throw new NotAllowedException("Bill of Quantities Item cannot be made a child if it has sub items");
		}

		LOG.info("DB BQ ITEM DETAILS ::" + bqItem.toString());
		int oldOrder = bqItem.getOrder();
		LOG.info("Item Old Position :: " + oldOrder + " :: Item New Position :: " + newOrder);
		int oldLevel = bqItem.getLevel();
		int newLevel = rfaBqItemPojo.getOrder(); // this will be ignored if it is made a child
		RfaBqItem newParent = null;
		if (rfaBqItemPojo.getParent() != null) {
			newParent = getBqItembyBqItemId(rfaBqItemPojo.getParent());
		}
		RfaBqItem oldParent = bqItem.getParent();

		// If these are not child, their order should be reset to 0
		if (oldParent == null) {
			oldOrder = 0;
		}
		if (newParent == null) {
			newOrder = 0;
		}

		// Update it to new position.
		bqItem.setOrder(newOrder);
		bqItem.setLevel(newParent == null ? rfaBqItemPojo.getOrder() : newParent.getLevel());
		bqItem.setParent(newParent);
		rfaBqItemDao.updateItemOrder(bqItem, bqItem.getBq().getId(), (oldParent == null ? null : oldParent.getId()), (newParent == null ? null : newParent.getId()), oldOrder, newOrder, oldLevel, newLevel);

	}

	@Override
	@Transactional(readOnly = false)
	public RfaBqItem getBqItembyBqItemId(String itemId) {
		return rfaBqItemDao.getBqItembyBqItemId(itemId);

	}

	@Override
	@Transactional(readOnly = false)
	public void deleteAllRfaBq(String eventId, String eventRequirement) {
		// List<RfaEventBq> listBq = rfaEventBqDao.findBqsByEventId(eventId);
		// LOG.info("Size Bq IDs : " + listBq.size() + " :event Id : " + eventId);
		// List<String> rfaBqIds = new ArrayList<String>();
		// for (RfaEventBq rfaEventBq : listBq) {
		// LOG.info("Here deleteing to get ids : " + rfaEventBq.getId());
		// rfaBqIds.add(rfaEventBq.getId());
		// }
		// rfaEnvelopDao.removeBqsFromEnvelops(eventId);
		// for (String bqId : rfaBqIds) {
		// LOG.info("Here deleteing : " + bqId);
		// rfaBqItemDao.deleteBqItemsbyBqid(bqId);
		// rfaEventBqDao.deleteBQ(bqId);
		// LOG.info("deleted : " + bqId);
		// }
		//
		rfaEnvelopDao.removeBqsFromEnvelops(eventId);
		rfaBqItemDao.deleteBqItemsForEventId(eventId);
		rfaEventBqDao.deleteBQForEventId(eventId);

		RfaEvent event = rfaEventDao.findById(eventId);
		event.setBqCompleted(Boolean.FALSE);
		event.setBillOfQuantity(Boolean.FALSE);
		rfaEventDao.update(event);

	}

	@Override
	public List<RfaEventBq> getNotAssignedRfaBqIdsByEventId(String eventId) {
		return rfaEnvelopDao.getNotAssignedRfaBqIdsByEventId(eventId);
	}

	@Override
	public Integer getCountOfRfaBqByEventId(String eventId) {
		return rfaEventBqDao.getCountOfRfaBqByEventId(eventId);
	}

	@Override
	public List<RfaBqItem> getAllBqItemListByEventId(String eventId) {
		return rfaBqItemDao.findBqItemListByEventId(eventId);
	}

	@Override
	public List<Bq> findRfaBqbyEventId(String eventId) {
		return rfaEventBqDao.findRfaBqbyEventId(eventId);
	}

	@Override
	public List<RfaBqItem> getAllBqitemsbyBqId(String bqId) {
		return rfaBqItemDao.getAllBqItemsbybqId(bqId);
	}

	@Override
	public RfaBqItem getParentbyLevelId(String bqId, Integer level) {
		return rfaBqItemDao.getParentbyLevelId(bqId, level);
	}

	@Override
	public RfaEvent getRfaEventBqById(String id) {
		RfaEvent event = rfaEventDao.findEventForBqByEventId(id);
		LOG.info("event in service : " + event);
		if (event != null) {
			if (event.getBaseCurrency() != null) {
				event.getBaseCurrency().getCurrencyName();
			}
		}
		if (event.getEventOwner() != null) {
			event.getEventOwner().getName();
			event.getEventOwner().getCommunicationEmail();
			event.getEventOwner().getPhoneNumber();
			if (event.getEventOwner().getOwner() != null) {
				Owner usr = event.getEventOwner().getOwner();
				usr.getCompanyContactNumber();
			}
		}
		return event;
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
		BqFileParser<RfaBqItem> rfaBi = new BqFileParser<RfaBqItem>(RfaBqItem.class);
		List<RfaBqItem> rfaBqList = rfaBi.parse(convFile);

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
		RfaEventBq rfaBq = null;
		if (sheet != null) {
			rfaBq = getRfaBqById(bqId);
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
			buildAddNewColumnsToUpload(rfaBq, columnsTitle);
			rfaBq = updateRfaBqFields(rfaBq);
		}

		// Delete existing BQ Items by Bqs.
		rfaBqItemDao.deleteBqItemsbyBqid(bqId);
		int count = 1;
		List<String> columns = new ArrayList<String>();

		// If successfully Deleted then only inserting records into DB.
		for (RfaBqItem bi : rfaBqList) {
			RfaBqItem rfaBqItem = new RfaBqItem();
			// RfaEventBq rfaBq = rfaEventBqDao.findBqItemById(bqId);

			// checking Extra column in Excel match with bq columns
			if (count == 1 && CollectionUtil.isNotEmpty(bi.getColumnTitles())) {

				if (StringUtils.checkString(rfaBq.getField1Label()).length() > 0) {
					columns.add(rfaBq.getField1Label());
				}
				if (StringUtils.checkString(rfaBq.getField2Label()).length() > 0) {
					columns.add(rfaBq.getField2Label());
				}
				if (StringUtils.checkString(rfaBq.getField3Label()).length() > 0) {
					columns.add(rfaBq.getField3Label());
				}
				if (StringUtils.checkString(rfaBq.getField4Label()).length() > 0) {
					columns.add(rfaBq.getField4Label());
				}

				if (StringUtils.checkString(rfaBq.getField5Label()).length() > 0) {
					columns.add(rfaBq.getField5Label());
				}
				if (StringUtils.checkString(rfaBq.getField6Label()).length() > 0) {
					columns.add(rfaBq.getField6Label());
				}
				if (StringUtils.checkString(rfaBq.getField7Label()).length() > 0) {
					columns.add(rfaBq.getField7Label());
				}
				if (StringUtils.checkString(rfaBq.getField8Label()).length() > 0) {
					columns.add(rfaBq.getField8Label());
				}
				if (StringUtils.checkString(rfaBq.getField9Label()).length() > 0) {
					columns.add(rfaBq.getField9Label());
				}
				if (StringUtils.checkString(rfaBq.getField10Label()).length() > 0) {
					columns.add(rfaBq.getField10Label());
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
			rfaBqItem.setBq(rfaBq);
			RfaEvent event = rfaEventDao.findById(eventId);
			rfaBqItem.setRfxEvent(event);

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
				RfaBqItem parentBq = rfaBqItemDao.getParentbyLevelId(bqId, bi.getLevel());
				rfaBqItem.setParent(parentBq);

				rfaBqItem.setUnitPrice(bi.getUnitPrice());
				rfaBqItem.setQuantity(bi.getQuantity().setScale((event.getDecimal() != null && !event.getDecimal().isEmpty() ? Integer.parseInt(event.getDecimal()) : 2), RoundingMode.HALF_EVEN));
				rfaBqItem.setPriceType(bi.getPriceType());
				if (bi.getUom() != null) {
					rfaBqItem.setUom(uomService.getUombyCode(bi.getUom().getUom(), tenantId));
				}
				// if invalid UOM
				if (rfaBqItem.getUom() == null) {
					throw new ExcelParseException(messageSource.getMessage("file.parse.uom.invalid.found", new Object[] { bi.getUom().getUom() }, Global.LOCALE), convFile.getName());
				}
				if (StringUtils.checkString(rfaBq.getField1Label()).length() > 0) {
					if (Boolean.TRUE == rfaBq.getField1Required() && StringUtils.checkString(bi.getField1()).length() == 0) {
						throw new ExcelParseException(rfaBq.getField1Label() + " is required for " + (bi.getLevel()) + "." + (bi.getOrder()) + " " + bi.getItemName(), convFile.getName());
					}
					rfaBqItem.setField1(bi.getField1());
				}
				if (StringUtils.checkString(rfaBq.getField2Label()).length() > 0) {
					if (Boolean.TRUE == rfaBq.getField2Required() && StringUtils.checkString(bi.getField2()).length() == 0) {
						throw new ExcelParseException(rfaBq.getField2Label() + " is required for " + (bi.getLevel()) + "." + (bi.getOrder()) + " " + bi.getItemName(), convFile.getName());
					}
					rfaBqItem.setField2(bi.getField2());
				}
				if (StringUtils.checkString(rfaBq.getField3Label()).length() > 0) {
					if (Boolean.TRUE == rfaBq.getField3Required() && StringUtils.checkString(bi.getField3()).length() == 0) {
						throw new ExcelParseException(rfaBq.getField3Label() + " is required for " + (bi.getLevel()) + "." + (bi.getOrder()) + " " + bi.getItemName(), convFile.getName());
					}
					rfaBqItem.setField3(bi.getField3());
				}
				if (StringUtils.checkString(rfaBq.getField4Label()).length() > 0) {
					if (Boolean.TRUE == rfaBq.getField4Required() && StringUtils.checkString(bi.getField4()).length() == 0) {
						throw new ExcelParseException(rfaBq.getField4Label() + " is required for " + (bi.getLevel()) + "." + (bi.getOrder()) + " " + bi.getItemName(), convFile.getName());
					}
					rfaBqItem.setField4(bi.getField4());
				}
				if (StringUtils.checkString(rfaBq.getField5Label()).length() > 0) {
					if (Boolean.TRUE == rfaBq.getField5Required() && StringUtils.checkString(bi.getField5()).length() == 0) {
						throw new ExcelParseException(rfaBq.getField5Label() + " is required for " + (bi.getLevel()) + "." + (bi.getOrder()) + " " + bi.getItemName(), convFile.getName());
					}
					rfaBqItem.setField5(bi.getField5());
				}
				if (StringUtils.checkString(rfaBq.getField6Label()).length() > 0) {
					if (Boolean.TRUE == rfaBq.getField6Required() && StringUtils.checkString(bi.getField6()).length() == 0) {
						throw new ExcelParseException(rfaBq.getField6Label() + " is required for " + (bi.getLevel()) + "." + (bi.getOrder()) + " " + bi.getItemName(), convFile.getName());
					}
					rfaBqItem.setField6(bi.getField6());
				}
				if (StringUtils.checkString(rfaBq.getField7Label()).length() > 0) {
					if (Boolean.TRUE == rfaBq.getField7Required() && StringUtils.checkString(bi.getField7()).length() == 0) {
						throw new ExcelParseException(rfaBq.getField7Label() + " is required for " + (bi.getLevel()) + "." + (bi.getOrder()) + " " + bi.getItemName(), convFile.getName());
					}
					rfaBqItem.setField7(bi.getField7());
				}
				if (StringUtils.checkString(rfaBq.getField8Label()).length() > 0) {
					if (Boolean.TRUE == rfaBq.getField8Required() && StringUtils.checkString(bi.getField8()).length() == 0) {
						throw new ExcelParseException(rfaBq.getField8Label() + " is required for " + (bi.getLevel()) + "." + (bi.getOrder()) + " " + bi.getItemName(), convFile.getName());
					}
					rfaBqItem.setField8(bi.getField8());
				}
				if (StringUtils.checkString(rfaBq.getField9Label()).length() > 0) {
					if (Boolean.TRUE == rfaBq.getField9Required() && StringUtils.checkString(bi.getField9()).length() == 0) {
						throw new ExcelParseException(rfaBq.getField9Label() + " is required for " + (bi.getLevel()) + "." + (bi.getOrder()) + " " + bi.getItemName(), convFile.getName());
					}
					rfaBqItem.setField9(bi.getField9());
				}
				if (StringUtils.checkString(rfaBq.getField10Label()).length() > 0) {
					if (Boolean.TRUE == rfaBq.getField10Required() && StringUtils.checkString(bi.getField10()).length() == 0) {
						throw new ExcelParseException(rfaBq.getField10Label() + " is required for " + (bi.getLevel()) + "." + (bi.getOrder()) + " " + bi.getItemName(), convFile.getName());
					}
					rfaBqItem.setField10(bi.getField10());
				}

			}
			// saveRfaBqItem(rfaBqItem);
			saveUpdateRfaBqItem(rfaBqItem);
		}
		return rfaBqList.size();
	}

	@Override
	public List<RfaSupplierBq> findRfaSupplierBqbyEventId(String eventId) {
		return rfaSupplierBqDao.findSupplierBqbyEventId(eventId);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public RfaEventBq updateRfaBqFields(RfaEventBq eventBq) {

		rfaEventBqDao.update(eventBq);

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
		rfaBqItemDao.resetFieldsForFilledBySupplier(eventBq.getId(), label);
		return eventBq;
	}

	@Override
	public List<String> getNotSectionAddedRfaBqIdsByEventId(String eventId) {
		return rfaEventBqDao.getNotSectionAddedRfaBqIdsByEventId(eventId);
	}

	@Override
	public List<RfaBqItem> getBqItemForSearchFilter(String bqId, Integer itemLevel, Integer itemOrder, String searchVal, Integer start, Integer length, Integer pageNo) {
		List<RfaBqItem> returnList = new ArrayList<RfaBqItem>();
		if (pageNo != null) {
			start = pageNo - 1;
		}
		if (length != null) {
			start = start * length;
		}
		LOG.info(" start  : " + start);
		List<RfaBqItem> bqList = rfaBqItemDao.getBqItemForSearchFilter(bqId, itemLevel, itemOrder, searchVal, start, length);
		if (CollectionUtil.isNotEmpty(bqList)) {
			for (RfaBqItem item : bqList) {
				RfaBqItem bqItem = item.createSearchShallowCopy();
				returnList.add(bqItem);
			}
		}
		return returnList;

	}

	@Override
	public List<BqItemPojo> getAllLevelOrderBqItemByBqId(String bqId, String searchVal) {
		return rfaBqItemDao.getAllLevelOrderBqItemByBqId(bqId, searchVal);
	}

	@Override
	public long totalBqItemCountByBqId(String bqId, String searchVal) {
		return rfaBqItemDao.totalBqItemCountByBqId(bqId, searchVal);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = { Exception.class })
	public void deleteAllERPBqByEventId(String eventId) {
		// TODO Auto-generated method stub
		rfaEnvelopDao.removeBqsFromEnvelops(eventId);
		rfaBqItemDao.deleteBqItemsForErpForEventId(eventId);
		rfaEventBqDao.deleteBQForEventId(eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public RfaBqItem saveUpdateRfaBqItem(RfaBqItem rfaBqItem) {
		return rfaBqItemDao.saveOrUpdate(rfaBqItem);
	}

	@Override
	public boolean checkIfBqItemExists(String eventId) {
		return rfaBqItemDao.checkIfBqItemExists(eventId);
	}

	@Override
	public List<String> getNotSectionItemAddedRfaBqIdsByEventId(String eventId) {
		return rfaEventBqDao.getNotSectionItemAddedRfaBqIdsByEventId(eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteAllBqItems(String bqId) throws NoSuchMessageException, NotAllowedException {
		RfaEventBq bq = rfaEventBqDao.findBqItemById(bqId);

		if (bq != null) {
			if (EventStatus.DRAFT != bq.getRfxEvent().getStatus() && EventStatus.SUSPENDED != bq.getRfxEvent().getStatus()) {
				throw new NotAllowedException(messageSource.getMessage("rft.bqitems.info.draft", new Object[] {}, Global.LOCALE));
			}
			rfaBqItemDao.deleteBqItemsbyBqid(bq.getId());
		}
	}

	@Override
	public RfaEventBq getRfaEventBqByBqId(String bqId) {
		RfaEventBq bq = rfaEventBqDao.getRfaEventBqByBqId(bqId);
		return bq;
	}

	@Override
	public List<RfaEventBq> getAllBqListByEventIdByOrder(String eventId) {
		return rfaEventBqDao.findBqsByEventIdByOrder(eventId);
	}

	@Override
	public Boolean findPreSetPredBidAuctionRulesWithEventId(String eventId) {
		return auctionRulesDao.findPreSetPredBidAuctionRulesWithEventId(eventId);
	}

	@Override
	public List<RfaEventBq> findBqAndBqItemsByEventIdByOrder(String eventId) {
		return rfaEventBqDao.findBqAndBqItemsByEventIdByOrder(eventId);
	}
}

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

import com.privasia.procurehere.core.dao.RftCqDao;
import com.privasia.procurehere.core.dao.RftCqItemDao;
import com.privasia.procurehere.core.dao.RftEnvelopDao;
import com.privasia.procurehere.core.dao.RftEventDao;
import com.privasia.procurehere.core.entity.Cq;
import com.privasia.procurehere.core.entity.CqItem;
import com.privasia.procurehere.core.entity.Owner;
import com.privasia.procurehere.core.entity.RfaCqItem;
import com.privasia.procurehere.core.entity.RfaCqOption;
import com.privasia.procurehere.core.entity.RfiCqItem;
import com.privasia.procurehere.core.entity.RfiCqOption;
import com.privasia.procurehere.core.entity.RfpCqItem;
import com.privasia.procurehere.core.entity.RfpCqOption;
import com.privasia.procurehere.core.entity.RfqCqItem;
import com.privasia.procurehere.core.entity.RfqCqOption;
import com.privasia.procurehere.core.entity.RftCq;
import com.privasia.procurehere.core.entity.RftCqItem;
import com.privasia.procurehere.core.entity.RftCqOption;
import com.privasia.procurehere.core.entity.RftEvent;
import com.privasia.procurehere.core.enums.CqType;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.CqItemPojo;
import com.privasia.procurehere.core.pojo.CqPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.RfaCqService;
import com.privasia.procurehere.service.RfiCqService;
import com.privasia.procurehere.service.RfpCqService;
import com.privasia.procurehere.service.RfqCqService;
import com.privasia.procurehere.service.RftCqService;

/**
 * @author Ravi
 */
@Service
@Transactional(readOnly = true)
public class RftCqServiceImpl implements RftCqService {

	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

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

	@Autowired
	MessageSource messageSource;

	@Autowired
	RftEventDao rftEventDao;

	@Autowired
	RftCqDao rftCqDao;

	@Autowired
	RftCqItemDao rftCqItemDao;

	@Autowired
	RftEnvelopDao rftEnvelopDao;

	@Override
	public List<RftCq> findRftCqForEvent(String eventId) {
		List<RftCq> cqList = rftCqDao.findCqsForEvent(eventId);
		if (CollectionUtil.isNotEmpty(cqList)) {
			for (RftCq rftCq : cqList) {
				if (CollectionUtil.isNotEmpty(rftCq.getCqItems())) {
					for (RftCqItem rftCqItem : rftCq.getCqItems()) {
						if (CollectionUtil.isNotEmpty(rftCqItem.getCqOptions())) {
							for (RftCqOption option : rftCqItem.getCqOptions()) {
								option.getValue();
							}
						}
					}
				}
			}
		}
		return cqList;
	}

	@Override
	public RftEvent findEventForCqByEventId(String eventId) {
		RftEvent rft = rftEventDao.findEventForCqByEventId(eventId);
		if (rft.getEventOwner() != null) {
			rft.getEventOwner().getName();
			rft.getEventOwner().getCommunicationEmail();
			rft.getEventOwner().getPhoneNumber();
			if (rft.getEventOwner().getOwner() != null) {
				Owner usr = rft.getEventOwner().getOwner();
				usr.getCompanyContactNumber();
			}
		}
		return rft;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public RftCq stroreRftCq(RftCq cq) {
		List<RftCq> cqList = rftCqService.findRftCqForEventByOrder(cq.getRfxEvent().getId());
		Integer count = 1;
		if (CollectionUtil.isNotEmpty(cqList)) {
			for (RftCq eventCq : cqList) {
				if (eventCq.getCqOrder() == null) {
					eventCq.setCqOrder(count);
					rftCqDao.update(eventCq);
					count++;
				}
			}
			count = cqList.size();
			count++;
		}
		if (StringUtils.checkString(cq.getId()).length() == 0) {
			cq.setCreatedDate(new Date());
			cq.setCqOrder(count);
		} else {
			RftCq cqPersistObj = rftCqDao.findById(cq.getId());
			cqPersistObj.setName(cq.getName());
			cqPersistObj.setDescription(cq.getDescription());
			cqPersistObj.setModifiedDate(new Date());
			cq = cqPersistObj;
		}
		RftCq rftcq = rftCqDao.saveOrUpdate(cq);
		return rftcq;
	}

	@Override
	public List<RftCqItem> findRftCqItemsForCq(String eventId) {
		List<RftCqItem> cqItemList = rftCqItemDao.getCqItemsForEventId(eventId);
		for (RftCqItem rftCqItem : cqItemList) {
			if (CollectionUtil.isNotEmpty(rftCqItem.getCqOptions())) {
				for (RftCqOption rftCqOption : rftCqItem.getCqOptions()) {
					rftCqOption.getRftCqItem();
				}
			}
		}
		return cqItemList;
	}

	@Override
	@Transactional(readOnly = true)
	public RftCq getRftCqById(String id) {
		RftCq cq = rftCqDao.getCqForId(id);
		if (cq != null && cq.getRfxEvent() != null) {
			if (cq.getRfxEvent().getEventOwner() != null) {
				cq.getRfxEvent().getEventOwner().getTenantId();
			}
		}
		return cq;
	}

	@Override
	@Transactional(readOnly = false)
	public RftCqItem saveRftCqItem(RftCqItem rftCqItem) {

		if (rftCqItem.getParent() == null) {
			int itemLevel = 0;
			List<RftCqItem> list = rftCqItemDao.getCqItemLevelOrder(rftCqItem.getCq().getId());
			if (CollectionUtil.isNotEmpty(list)) {
				itemLevel = list.size();
			}
			rftCqItem.setLevel(itemLevel + 1);
			rftCqItem.setOrder(0);
		} else {
			LOG.info("PARENT : " + rftCqItem.getParent().getId());
			// RftCqItem parent = rftCqItemDao.getCqItembyCqId(rftCqItem.getParent().getId());
			if (rftCqItem.getParent() != null) {
				rftCqItem.setLevel(rftCqItem.getParent().getLevel());
				rftCqItem.setOrder(CollectionUtil.isEmpty(rftCqItem.getParent().getChildren()) ? 1 : rftCqItem.getParent().getChildren().size() + 1);
			}
		}
		return rftCqItemDao.saveOrUpdate(rftCqItem);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
	public String doExcelDataSave(Map<Integer, Map<Integer, RftCqItem>> dataMap, String eventId, String cqId) throws Exception {

		RftCq cq = getRftCqById(cqId);
		RftEvent event = getRftEventById(eventId);

		for (RftCqItem item : cq.getCqItems()) {
			if (item.getOrder() == 0) {
				LOG.info("Deleting CQ Item : " + item.toLogString());
				item.setCq(null);
				rftCqItemDao.delete(item);
			}
		}
		if (cq.getCqItems() != null) {
			cq.getCqItems().clear();
			rftCqDao.update(cq);
		}
		// checking item exists validation
		int levelTemp = 1;
		int orderTemp = 0;
		boolean isItemExists = false;
		String message = null;

		int rowNum = 2;
		for (Map.Entry<Integer, Map<Integer, RftCqItem>> entry : dataMap.entrySet()) {
			RftCqItem parent = null;
			for (Map.Entry<Integer, RftCqItem> cqItem : entry.getValue().entrySet()) {
				cqItem.getValue().setParent(parent);
				if (cqItem.getValue().getOrder() == 0) {
					parent = cqItem.getValue();
				} else {
					cqItem.getValue().setCqOptions(new ArrayList<>());
					int index = 1;
					for (String option : cqItem.getValue().getOptions()) {
						RftCqOption rOption = new RftCqOption();
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
						rOption.setRftCqItem(cqItem.getValue());
						rOption.setValue(option);
						rOption.setOrder(index++);
						cqItem.getValue().getCqOptions().add(rOption);
					}
				}
				cqItem.getValue().setCq(cq);
				cqItem.getValue().setRfxEvent(event);
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

				rftCqItemDao.save(cqItem.getValue());
				rowNum++;
			}
		}

		if (isItemExists) {
			message = messageSource.getMessage("common.upload.warning", new Object[] {}, Global.LOCALE);
		}
		return message;
	}

	@Override
	@Transactional(readOnly = true)
	public List<RftCqItem> findRftCqbyCqId(String cqId) {
		List<RftCqItem> returnList = new ArrayList<RftCqItem>();
		List<RftCqItem> list = rftCqItemDao.getCqItemsbyId(cqId);
		bulidCqItemList(returnList, list);
		return returnList;
	}

	/**
	 * @param returnList
	 * @param list
	 */
	private void bulidCqItemList(List<RftCqItem> returnList, List<RftCqItem> list) {
		if (CollectionUtil.isNotEmpty(list)) {
			for (RftCqItem item : list) {
				RftCqItem parent = item.createShallowCopy();
				returnList.add(parent);
				if (CollectionUtil.isNotEmpty(item.getChildren())) {
					for (RftCqItem child : item.getChildren()) {
						if (parent.getChildren() == null) {
							parent.setChildren(new ArrayList<RftCqItem>());
						}
						parent.getChildren().add(child.createShallowCopy());
					}
				}
			}
		}
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
	public RftCqItem getCqItembyCqItemId(String parent) {
		RftCqItem item = rftCqItemDao.getCqItembyCqItemId(parent);
		if (item != null && CollectionUtil.isNotEmpty(item.getCqOptions())) {
			for (RftCqOption option : item.getCqOptions()) {
				option.getValue();
			}
		}
		return item;
	}

	@Override
	@Transactional(readOnly = false)
	public void updateCqItem(CqItemPojo rftCqItem) throws ApplicationException {
		RftCqItem item = getCqItembyCqItemId(rftCqItem.getId());
		LOG.info("ITEM : " + item.getItemName());
		item.setItemName(rftCqItem.getItemName());
		item.setItemDescription(rftCqItem.getItemDescription());
		item.setAttachment(rftCqItem.isAttachment());
		item.setOptional(rftCqItem.isOptional());
		item.setIsSupplierAttachRequired(rftCqItem.getIsSupplierAttachRequired());
		item.setCqType(rftCqItem.getCqType() != null ? CqType.valueOf(rftCqItem.getCqType()) : null);
		if (CollectionUtil.isNotEmpty(rftCqItem.getOptions())) {
			List<RftCqOption> optionItems = new ArrayList<RftCqOption>();
			int optionOrder = 0;
			for (String option : rftCqItem.getOptions()) {
				if (StringUtils.checkString(option).length() == 0)
					continue;
				RftCqOption options = new RftCqOption();
				options.setRftCqItem(item);
				options.setValue(option);
				options.setOrder(++optionOrder);
				if (CollectionUtil.isNotEmpty(rftCqItem.getOptionScore()))
					options.setScoring(Integer.parseInt(rftCqItem.getOptionScore().get(optionOrder - 1)));
				optionItems.add(options);
			}
			item.setCqOptions(optionItems);
		}
		rftCqItemDao.saveOrUpdate(item);
	}

	@Override
	public List<RftCq> getNotAssignedRftCqIdsByEventId(String eventId) {
		return rftEnvelopDao.getNotAssignedRftCqIdsByEventId(eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteCq(RftCq cq) {
		rftCqDao.deleteCqById(cq.getId());

		List<RftCq> cqList = rftCqService.findRftCqForEventByOrder(cq.getRfxEvent().getId());
		if (CollectionUtil.isNotEmpty(cqList)) {
			Integer count = 1;
			for (RftCq rftCq : cqList) {
				rftCq.setCqOrder(count);
				rftCqDao.update(rftCq);
				count++;
			}
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteCqItems(String[] cqItemIds, String cqId) throws NotAllowedException {
		// RftCq item = rftCqDao.findById(cqId);
		// if (item != null) {
		// if (EventStatus.DRAFT != item.getRfxEvent().getStatus()) {
		// throw new NotAllowedException(messageSource.getMessage("rft.cq.info.draft", new Object[] {}, Global.LOCALE));
		// }
		// }
		rftCqItemDao.deleteCqItems(cqItemIds, cqId);
	}

	@Override
	public void isAllowToDeleteCq(String cqId) throws NotAllowedException {
		rftCqDao.isAllowToDeleteCq(cqId);
	}

	@Override
	public List<RftCq> getRftCqByEventId(String id, List<String> cqIds) {
		return rftCqDao.findCqsByEventIdAndCqIds(id, cqIds);
	}

	@Override
	public void reorderCqItems(CqItemPojo rftCqItemPojo) throws NotAllowedException {
		LOG.info("CQ ITEM Object :: " + rftCqItemPojo.toString());
		int newOrder = rftCqItemPojo.getOrder();
		RftCqItem cqItem = getCqItembyCqItemId(rftCqItemPojo.getId());
		if (CollectionUtil.isNotEmpty(cqItem.getChildren()) && rftCqItemPojo.getParent() != null) {
			throw new NotAllowedException("CQ Item cannot be made a child if it has sub items");
		}

		LOG.info("DB CQ ITEM DETAILS ::" + cqItem.toLogString());
		int oldOrder = cqItem.getOrder();
		LOG.info("Item Old Position :: " + oldOrder + " :: Item New Position :: " + newOrder);
		int oldLevel = cqItem.getLevel();
		int newLevel = rftCqItemPojo.getOrder(); // this will be ignored if it is made a child
		RftCqItem newParent = null;
		if (rftCqItemPojo.getParent() != null && StringUtils.checkString(rftCqItemPojo.getParent()).length() > 0) {
			newParent = getCqItembyCqItemId(rftCqItemPojo.getParent());
		}
		RftCqItem oldParent = cqItem.getParent();

		// If these are not child, their order should be reset to 0
		if (oldParent == null) {
			oldOrder = 0;
		}
		if (newParent == null) {
			newOrder = 0;
		}

		// Update it to new position.
		cqItem.setOrder(newOrder);
		cqItem.setLevel(newParent == null ? rftCqItemPojo.getOrder() : 0);
		cqItem.setParent(newParent);
		rftCqItemDao.updateItemOrder(cqItem.getCq().getId(), cqItem, oldParent == null ? null : oldParent.getId(), newParent == null ? null : newParent.getId(), oldOrder, newOrder, oldLevel, newLevel);
	}

	public Integer getCountOfRftCqByEventId(String eventId) {
		return rftCqDao.getCountOfRftCqByEventId(eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteAllCqs(String eventId, String eventRequirement) {

		rftEnvelopDao.removeCqsFromEnvelops(eventId);
		rftCqDao.deleteCqByEventId(eventId);
		RftEvent event = rftEventDao.findById(eventId);
		event.setCqCompleted(Boolean.FALSE);
		event.setQuestionnaires(Boolean.FALSE);
		rftEventDao.update(event);
	}

	@Override
	public RftEvent getRftEventById(String eventId) {
		return rftEventDao.findByEventId(eventId);
	}

	@Override
	public boolean isExists(RftCq rftCq) {
		LOG.info("Checking for duplicate CQ with name : " + rftCq.getName() + " event Id : " + rftCq.getRfxEvent().getId() + "  id : " + rftCq.getId());
		return rftCqDao.isExists(rftCq, rftCq.getRfxEvent().getId());
	}

	@Override
	public boolean isExists(RftCqItem rftCqItem, String cqId, String parentId) {
		LOG.info("Checking for duplicate CQ Item with name : " + rftCqItem.getItemName() + " CQ Id : " + cqId + "  Parent : " + parentId);
		return rftCqItemDao.isExists(rftCqItem, cqId, parentId);
	}

	@Override
	public List<RftCqItem> getAllCqitemsbyCqId(String cqId) {
		List<RftCqItem> itemList = rftCqItemDao.getAllCqItemsbycqId(cqId);
		for (RftCqItem rftCqItem : itemList) {
			if (CollectionUtil.isNotEmpty(rftCqItem.getCqOptions()))
				for (RftCqOption cqOption : rftCqItem.getCqOptions()) {
					cqOption.getValue();
				}
		}
		return itemList;
	}

	@Override
	public RftCqItem getParentbyLevelId(String cqId, Integer level) {
		return rftCqItemDao.getParentbyLevelId(cqId, level);
	}

	@Override
	public int CountAllMandatoryCqByEventId(String eventId) {
		return rftCqItemDao.CountCqItemsbyEventId(eventId);
	}

	@Override
	public List<String> getNotSectionAddedRftCqIdsByEventId(String eventId) {
		return rftCqDao.getNotSectionAddedRftCqIdsByEventId(eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public boolean updateLeadEvaluatorComment(String cqItemId, String leadComment) {
		return rftCqItemDao.updateLeadEvaluatorComment(cqItemId, leadComment);
	}

	@Override
	public String getLeadEvaluatorComment(String cqItemId) {
		return rftCqItemDao.getLeadEvaluatorComment(cqItemId);
	}

	@Override
	public XSSFWorkbook eventcqDownloader(String cqId, RfxTypes eventType) throws IOException {
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("CQ Item List");
		// Creating Headings
		buildHeader(workbook, sheet);
		int r = 1;

		switch (eventType) {
		case RFA:
			List<RfaCqItem> rfaCqList = rfaCqService.getAllCqitemsbyCqId(cqId);
			// Write Data into Excel
			for (RfaCqItem item : rfaCqList) {
				r = buildRows(sheet, r, item.getCq(), item, eventType);
			}
		case RFI:
			List<RfiCqItem> rfiCqList = rfiCqService.getAllCqitemsbyCqId(cqId);
			// Write Data into Excel
			for (RfiCqItem item : rfiCqList) {
				r = buildRows(sheet, r, item.getCq(), item, eventType);
			}
			break;
		case RFP:
			List<RfpCqItem> rfpCqList = rfpCqService.getAllCqitemsbyCqId(cqId);
			// Write Data into Excel
			for (RfpCqItem item : rfpCqList) {
				r = buildRows(sheet, r, item.getCq(), item, eventType);
			}
		case RFQ:
			List<RfqCqItem> rfqCqList = rfqCqService.getAllCqitemsbyCqId(cqId);
			// Write Data into Excel
			for (RfqCqItem item : rfqCqList) {
				r = buildRows(sheet, r, item.getCq(), item, eventType);
			}
			break;
		case RFT:
			List<RftCqItem> rftCqList = rftCqService.getAllCqitemsbyCqId(cqId);
			// Write Data into Excel
			for (RftCqItem item : rftCqList) {
				r = buildRows(sheet, r, item.getCq(), item, eventType);
			}
			break;
		default:
			break;

		}

		XSSFSheet lookupSheet = workbook.createSheet("LOOKUP");
		XSSFRow firstRow = null;
		XSSFRow secondRow = null;

		int index = 0;
		CqType[] cqTypeArr = CqType.values();
		for (CqType cqType : cqTypeArr) {
			XSSFRow row = lookupSheet.createRow(index++);
			if (index == 1) {
				firstRow = row;
			} else if (index == 2) {
				secondRow = row;
			}
			XSSFCell cell1 = row.createCell(0);
			cell1.setCellValue(cqType.getValue());
			return workbook;
		}
		// CQ Type
		// DVConstraint cqConstraint = DVConstraint.createFormulaListConstraint("'LOOKUP'!$A$1:$A$" + (cqTypeArr.length
		// + 1));

		XSSFDataValidationHelper validationHelper = new XSSFDataValidationHelper(lookupSheet);
		XSSFDataValidationConstraint constraint = (XSSFDataValidationConstraint) validationHelper.createFormulaListConstraint("'LOOKUP'!$A$1:$A$" + (cqTypeArr.length + 1));
		CellRangeAddressList cqTypeList = new CellRangeAddressList(1, 1000, 3, 3);

		XSSFDataValidation cqTypeValidation = (XSSFDataValidation) validationHelper.createValidation(constraint, cqTypeList);
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
		CellRangeAddressList yesNoList = new CellRangeAddressList(1, 1000, 4, 5);

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
		return workbook;
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

	private int buildRows(XSSFSheet sheet, int r, Cq bq, CqItem item, RfxTypes eventType) throws IOException {
		// CellStyle unlockedCellStyle = workbook.createCellStyle();
		// unlockedCellStyle.setLocked(true);
		Row row = sheet.createRow(r++);
		int cellNum = 0;

		// row.createCell(cellNum++).setCellValue(item.getId());
		row.createCell(cellNum++).setCellValue(item.getLevel() + "." + item.getOrder());
		row.createCell(cellNum++).setCellValue(item.getItemName());
		row.createCell(cellNum++).setCellValue(item.getItemDescription() != null ? item.getItemDescription() : "");
		if (item.getOrder() == 0) {
			int colNum = 5;
			sheet.addMergedRegion(new CellRangeAddress(r - 1, r - 1, cellNum - 1, colNum));
		} else {
			row.createCell(cellNum++).setCellValue(item.getCqType().getValue());
			row.createCell(cellNum++).setCellValue(item.getAttachment() == true ? "YES" : "NO");
			row.createCell(cellNum++).setCellValue(item.getOptional() == true ? "YES" : "NO");
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

	@Override
	public List<String> getNotSectionItemAddedRftCqIdsByEventId(String eventId) {
		return rftCqDao.getNotSectionItemAddedRftCqIdsByEventId(eventId);
	}

	@Override
	public List<RftCq> findRftCqForEventByEnvelopeId(String eventId, String envelopeId) {

		List<RftCq> cqList = rftCqDao.findCqsForEventByEnvelopeId(eventId, envelopeId);
		if (CollectionUtil.isNotEmpty(cqList)) {
			for (RftCq rftCq : cqList) {
				if (CollectionUtil.isNotEmpty(rftCq.getCqItems())) {
					for (RftCqItem rftCqItem : rftCq.getCqItems()) {
						if (CollectionUtil.isNotEmpty(rftCqItem.getCqOptions())) {
							for (RftCqOption option : rftCqItem.getCqOptions()) {
								option.getValue();
							}
						}
					}
				}
			}
		}
		return cqList;

	}

	@Override
	public List<RftCq> findRfaCqForEventByEnvelopeId(List<String> cqid, String id) {

		List<RftCq> cqs = rftCqDao.findCqsForEventEnvelopeId(cqid, id);
		if (CollectionUtil.isNotEmpty(cqs)) {
			for (RftCq cq : cqs) {
				if (CollectionUtil.isNotEmpty(cq.getCqItems())) {
					for (RftCqItem item : cq.getCqItems()) {
						if (CollectionUtil.isEmpty(item.getCqOptions())) {
							for (RftCqOption option : item.getCqOptions()) {
								option.getValue();
							}
						}
					}
				}
			}
		}
		return cqs;

	}

	@Override
	public List<CqPojo> findEventForCqPojoByEventId(String eventId) {
		return rftCqDao.findEventForCqPojoByEventId(eventId);
	}

	@Override
	public List<RftCq> findRftCqForEventByOrder(String eventId) {
		List<RftCq> cqList = rftCqDao.findCqsForEventByOrder(eventId);
		if (CollectionUtil.isNotEmpty(cqList)) {
			for (RftCq rftCq : cqList) {
				if (CollectionUtil.isNotEmpty(rftCq.getCqItems())) {
					for (RftCqItem rftCqItem : rftCq.getCqItems()) {
						if (CollectionUtil.isNotEmpty(rftCqItem.getCqOptions())) {
							for (RftCqOption option : rftCqItem.getCqOptions()) {
								option.getValue();
							}
						}
					}
				}
			}
		}
		return cqList;
	}

	@Override
	@Transactional(readOnly = false)
	public RftCq updateCq(RftCq cq) {
		return rftCqDao.update(cq);
	}
}

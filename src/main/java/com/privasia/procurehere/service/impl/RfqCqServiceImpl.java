package com.privasia.procurehere.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RfqCqDao;
import com.privasia.procurehere.core.dao.RfqCqItemDao;
import com.privasia.procurehere.core.dao.RfqEnvelopDao;
import com.privasia.procurehere.core.dao.RfqEventDao;
import com.privasia.procurehere.core.entity.Owner;
import com.privasia.procurehere.core.entity.RfqCq;
import com.privasia.procurehere.core.entity.RfqCqItem;
import com.privasia.procurehere.core.entity.RfqCqOption;
import com.privasia.procurehere.core.entity.RfqEvent;
import com.privasia.procurehere.core.enums.CqType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.CqItemPojo;
import com.privasia.procurehere.core.pojo.CqPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.RfqCqService;

/**
 * @author Ravi
 */
@Service
@Transactional(readOnly = true)
public class RfqCqServiceImpl implements RfqCqService {

	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@Autowired
	MessageSource messageSource;

	@Autowired
	RfqEventDao eventDao;

	@Autowired
	RfqCqDao cqDao;

	@Autowired
	RfqCqItemDao cqItemDao;

	@Autowired
	RfqEnvelopDao envelopDao;

	@Override
	public List<RfqCq> findCqForEvent(String eventId) {
		List<RfqCq> cqList = cqDao.findCqsForEvent(eventId);
		if (CollectionUtil.isNotEmpty(cqList)) {
			for (RfqCq rftCq : cqList) {
				if (CollectionUtil.isNotEmpty(rftCq.getCqItems())) {
					for (RfqCqItem rftCqItem : rftCq.getCqItems()) {
						if (CollectionUtil.isNotEmpty(rftCqItem.getCqOptions())) {
							for (RfqCqOption option : rftCqItem.getCqOptions()) {
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
	public RfqEvent findEventForCqByEventId(String eventId) {
		RfqEvent rfq = eventDao.findEventForCqByEventId(eventId);
		if (rfq.getEventOwner() != null) {
			rfq.getEventOwner().getName();
			rfq.getEventOwner().getCommunicationEmail();
			rfq.getEventOwner().getPhoneNumber();
			if (rfq.getEventOwner().getOwner() != null) {
				Owner usr = rfq.getEventOwner().getOwner();
				usr.getCompanyContactNumber();
			}
		}
		return rfq;
	}

	@Override
	@Transactional(readOnly = false)
	public RfqCq stroreCq(RfqCq cq) {
		List<RfqCq> cqList = findCqForEventByOrder(cq.getRfxEvent().getId());
		Integer count = 1;
		if (CollectionUtil.isNotEmpty(cqList)) {
			for (RfqCq eventCq : cqList) {
				if (eventCq.getCqOrder() == null) {
					eventCq.setCqOrder(count);
					cqDao.update(eventCq);
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
			RfqCq cqPersistObj = cqDao.findById(cq.getId());
			cqPersistObj.setName(cq.getName());
			cqPersistObj.setDescription(cq.getDescription());
			cqPersistObj.setModifiedDate(new Date());
			cq = cqPersistObj;
		}
		RfqCq rfqCq = cqDao.saveOrUpdate(cq);
		return rfqCq;
	}

	@Override
	public List<RfqCqItem> findCqItemsForCq(String eventId) {
		List<RfqCqItem> cqItemList = cqItemDao.getCqItemsForEventId(eventId);
		for (RfqCqItem rftCqItem : cqItemList) {
			if (CollectionUtil.isNotEmpty(rftCqItem.getCqOptions())) {
				for (RfqCqOption rftCqOption : rftCqItem.getCqOptions()) {
					rftCqOption.getCqItem();
				}
			}
		}
		return cqItemList;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
	public RfqCq getCqById(String id) {
		RfqCq cq = cqDao.getCqForId(id);
		if (cq != null && cq.getRfxEvent() != null) {
			if (cq.getRfxEvent().getEventOwner() != null) {
				cq.getRfxEvent().getEventOwner().getTenantId();
			}
		}
		return cq;
	}

	@Override
	@Transactional(readOnly = false)
	public RfqCqItem saveCqItem(RfqCqItem cqItem) {

		if (cqItem.getParent() == null) {
			int itemLevel = 0;
			List<RfqCqItem> list = cqItemDao.getCqItemLevelOrder(cqItem.getCq().getId());
			if (CollectionUtil.isNotEmpty(list)) {
				itemLevel = list.size();
			}
			cqItem.setLevel(itemLevel + 1);
			cqItem.setOrder(0);
		} else {
			// LOG.info("PARENT : " + cqItem.getParent().getId());
			// RfqCqItem parent = rftCqItemDao.getCqItembyCqId(rftCqItem.getParent().getId());
			if (cqItem.getParent() != null) {
				cqItem.setLevel(cqItem.getParent().getLevel());
				cqItem.setOrder(CollectionUtil.isEmpty(cqItem.getParent().getChildren()) ? 1 : cqItem.getParent().getChildren().size() + 1);
			}
		}
		// LOG.info("LOG : " + cqItem.getCqOptions().size());
		return cqItemDao.saveOrUpdate(cqItem);
	}

	@Override
	@Transactional(readOnly = true)
	public List<RfqCqItem> findCqbyCqId(String cqId) {
		List<RfqCqItem> returnList = new ArrayList<RfqCqItem>();
		List<RfqCqItem> list = cqItemDao.getCqItemsbyId(cqId);
		bulidCqItemList(returnList, list);
		return returnList;
	}

	/**
	 * @param returnList
	 * @param list
	 */
	private void bulidCqItemList(List<RfqCqItem> returnList, List<RfqCqItem> list) {
		if (CollectionUtil.isNotEmpty(list)) {
			for (RfqCqItem item : list) {
				RfqCqItem parent = item.createShallowCopy();
				returnList.add(parent);
				if (CollectionUtil.isNotEmpty(item.getChildren())) {
					for (RfqCqItem child : item.getChildren()) {
						if (parent.getChildren() == null) {
							parent.setChildren(new ArrayList<RfqCqItem>());
						}
						parent.getChildren().add(child.createShallowCopy());
					}
				}
			}
		}
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
	public RfqCqItem getCqItembyCqItemId(String parent) {
		RfqCqItem item = cqItemDao.getCqItembyCqItemId(parent);
		if (item != null && CollectionUtil.isNotEmpty(item.getCqOptions())) {
			for (RfqCqOption option : item.getCqOptions()) {
				option.getValue();
			}
		}
		return item;
	}

	@Override
	@Transactional(readOnly = false)
	public void updateCqItem(CqItemPojo item) throws ApplicationException {
		RfqCqItem rftCqItem = getCqItembyCqItemId(item.getId());
		LOG.info("item.getCq()...." + item.getCq() + "item.getParent()..." + item.getParent());
		rftCqItem.setItemName(item.getItemName());
		rftCqItem.setItemDescription(item.getItemDescription());
		rftCqItem.setIsSupplierAttachRequired(item.getIsSupplierAttachRequired());
		rftCqItem.setAttachment(item.isAttachment());
		rftCqItem.setOptional(item.isOptional());
		item.setIsSupplierAttachRequired(item.getIsSupplierAttachRequired());
		rftCqItem.setCqType(item.getCqType() != null ? CqType.valueOf(item.getCqType()) : null);
		if (CollectionUtil.isNotEmpty(item.getOptions())) {
			List<RfqCqOption> optionItems = new ArrayList<RfqCqOption>();
			int optionOrder = 0;
			for (String option : item.getOptions()) {
				if (StringUtils.checkString(option).length() == 0)
					continue;
				RfqCqOption options = new RfqCqOption();
				options.setCqItem(rftCqItem);
				options.setValue(option);
				options.setOrder(++optionOrder);
				if (CollectionUtil.isNotEmpty(item.getOptionScore()))
					options.setScoring(Integer.parseInt(item.getOptionScore().get(optionOrder - 1)));
				optionItems.add(options);
			}
			rftCqItem.setCqOptions(optionItems);
		}
		cqItemDao.saveOrUpdate(rftCqItem);
	}

	@Override
	public List<RfqCq> getNotAssignedCqIdsByEventId(String eventId) {
		return envelopDao.getNotAssignedCqIdsByEventId(eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteCq(RfqCq cq) {
		cqDao.deleteCqById(cq.getId());

		List<RfqCq> cqList = findCqForEventByOrder(cq.getRfxEvent().getId());
		if (CollectionUtil.isNotEmpty(cqList)) {
			Integer count = 1;
			for (RfqCq rfqCq : cqList) {
				rfqCq.setCqOrder(count);
				cqDao.update(rfqCq);
				count++;
			}
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteCqItems(String[] cqItemIds, String cqId) throws NotAllowedException {
		RfqCq item = cqDao.findById(cqId);
		// if (item != null) {
		// if (EventStatus.DRAFT != item.getRfxEvent().getStatus()) {
		// throw new NotAllowedException(messageSource.getMessage("rft.cq.info.draft", new Object[] {}, Global.LOCALE));
		// }
		// }
		cqItemDao.deleteCqItems(cqItemIds, cqId);
	}

	@Override
	public void isAllowToDeleteCq(String cqId) throws NotAllowedException {
		cqDao.isAllowToDeleteCq(cqId);
	}

	@Override
	public List<RfqCq> getCqByEventId(String id, List<String> cqIds) {
		return cqDao.findCqsByEventIdAndCqIds(id, cqIds);
	}

	@Override
	@Transactional(readOnly = false)
	public void reorderCqItems(CqItemPojo rftCqItemPojo) throws NotAllowedException {

		LOG.info("CQ ITEM Object :: " + rftCqItemPojo.toString());
		int newOrder = rftCqItemPojo.getOrder();
		RfqCqItem cqItem = getCqItembyCqItemId(rftCqItemPojo.getId());

		if (CollectionUtil.isNotEmpty(cqItem.getChildren()) && rftCqItemPojo.getParent() != null) {
			throw new NotAllowedException("CQ Item cannot be made a child if it has sub items");
		}

		LOG.info("DB CQ ITEM DETAILS ::" + cqItem.toString());
		int oldOrder = cqItem.getOrder();
		LOG.info("Item Old Position :: " + oldOrder + " :: Item New Position :: " + newOrder);
		int oldLevel = cqItem.getLevel();
		int newLevel = rftCqItemPojo.getOrder(); // this will be ignored if it is made a child
		RfqCqItem newParent = null;
		if (rftCqItemPojo.getParent() != null) {
			newParent = getCqItembyCqItemId(rftCqItemPojo.getParent());
		}
		RfqCqItem oldParent = cqItem.getParent();

		// If these are not child, their order should be reset to 0
		if (oldParent == null) {
			oldOrder = 0;
		}
		if (newParent == null) {
			newOrder = 0;
		}

		// Update it to new position.
		cqItem.setOrder(newOrder);
		cqItem.setLevel(newParent == null ? rftCqItemPojo.getOrder() : newParent.getLevel());
		cqItem.setParent(newParent);

		cqItemDao.updateItemOrder(cqItem.getCq().getId(), cqItem, oldParent == null ? null : oldParent.getId(), newParent == null ? null : newParent.getId(), oldOrder, newOrder, oldLevel, newLevel);

	}

	public Integer getCountOfRftCqByEventId(String eventId) {
		return cqDao.getCountOfCqByEventId(eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteAllCqs(String eventId, String eventRequirement) {
		envelopDao.removeCqsFromEnvelops(eventId);
		cqDao.deleteCqByEventId(eventId);

		RfqEvent event = eventDao.findById(eventId);
		event.setCqCompleted(Boolean.FALSE);
		event.setQuestionnaires(Boolean.FALSE);
		eventDao.update(event);
	}

	@Override
	public RfqCqItem findCqItemForCqByEventId(String eventId) {
		RfqCqItem item = cqItemDao.getCqItemByEventId(eventId);
		if (item != null && CollectionUtil.isNotEmpty(item.getCqOptions())) {
			for (RfqCqOption option : item.getCqOptions()) {
				option.getValue();
			}
		}
		return item;
	}

	@Override
	public RfqEvent getEventById(String eventId) {
		return eventDao.findByEventId(eventId);
	}

	@Override
	public boolean isExists(RfqCq rftCq) {
		LOG.info("Checking for duplicate CQ with name : " + rftCq.getName() + " event Id : " + rftCq.getRfxEvent().getId() + "  id : " + rftCq.getId());
		return cqDao.isExists(rftCq, rftCq.getRfxEvent().getId());
	}

	@Override
	public boolean isExists(RfqCqItem RfqCqItem, String cqId, String parentId) {
		LOG.info("Checking for duplicate CQ Item with name : " + RfqCqItem.getItemName() + " CQ Id : " + cqId + "  Parent : " + parentId);
		return cqItemDao.isExists(RfqCqItem, cqId, parentId);
	}

	@Override
	public List<RfqCqItem> getAllCqitemsbyCqId(String cqId) {
		List<RfqCqItem> itemList = cqItemDao.getAllCqItemsbycqId(cqId);
		for (RfqCqItem rfqCqItem : itemList) {
			for (RfqCqOption cqOption : rfqCqItem.getCqOptions()) {
				cqOption.getValue();
			}
		}
		return itemList;
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
	public String doExcelDataSave(Map<Integer, Map<Integer, RfqCqItem>> dataMap, String eventId, String cqId) throws Exception {
		LOG.info("dataMap  " + dataMap.size());
		RfqCq cq = getCqById(cqId);
		RfqEvent event = getEventById(eventId);

		for (RfqCqItem item : cq.getCqItems()) {
			if (item.getOrder() == 0) {
				LOG.info("Deleting CQ Item : " + item.toLogString());
				item.setCq(null);
				cqItemDao.delete(item);
			}
		}
		if (cq.getCqItems() != null) {
			cq.getCqItems().clear();
			cqDao.update(cq);
		}

		// checking item exists validation
		int levelTemp = 1;
		int orderTemp = 0;
		boolean isItemExists = false;
		String message = null;

		int rowNum = 2;
		for (Map.Entry<Integer, Map<Integer, RfqCqItem>> entry : dataMap.entrySet()) {
			RfqCqItem parent = null;
			for (Map.Entry<Integer, RfqCqItem> cqItem : entry.getValue().entrySet()) {
				// if(parent != null) {
				cqItem.getValue().setParent(parent);
				// }
				if (cqItem.getValue().getOrder() == 0) {
					parent = cqItem.getValue();
				} else {
					cqItem.getValue().setCqOptions(new ArrayList<>());
					int index = 1;
					for (String option : cqItem.getValue().getOptions()) {
						RfqCqOption rOption = new RfqCqOption();
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
						rOption.setCqItem(cqItem.getValue());
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
				cqItemDao.save(cqItem.getValue());
				rowNum++;
			}
		}

		if (isItemExists) {
			message = messageSource.getMessage("common.upload.warning", new Object[] {}, Global.LOCALE);
		}
		return message;
	}

	@Override
	public int CountAllMandatoryCqByEventId(String eventId) {
		return cqItemDao.CountCqItemsbyEventId(eventId);
	}

	@Override
	public List<String> getNotSectionAddedRfqCqIdsByEventId(String eventId) {
		return cqDao.getNotSectionAddedRfqCqIdsByEventId(eventId);
	}

	@Override
	public String getLeadEvaluatorComment(String itemId) {
		return cqItemDao.getLeadEvaluatorComment(itemId);
	}

	@Override
	@Transactional(readOnly = false)
	public boolean updateLeadEvaluatorComment(String itemId, String leadEvaluationComment) {
		return cqItemDao.updateLeadEvaluatorComment(itemId, leadEvaluationComment);
	}

	@Override
	public List<String> getNotSectionItemAddedRfqCqIdsByEventId(String eventId) {
		return cqDao.getNotSectionItemAddedRfqCqIdsByEventId(eventId);
	}

	@Override
	public List<RfqCq> findRfaCqForEventByEnvelopeId(List<String> cqid, String id) {

		List<RfqCq> cqs = cqDao.findCqsForEventEnvelopeId(cqid, id);
		if (CollectionUtil.isNotEmpty(cqs)) {
			for (RfqCq cq : cqs) {
				if (CollectionUtil.isNotEmpty(cq.getCqItems())) {
					for (RfqCqItem item : cq.getCqItems()) {
						if (CollectionUtil.isEmpty(item.getCqOptions())) {
							for (RfqCqOption option : item.getCqOptions()) {
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
		return cqDao.findEventForCqPojoByEventId(eventId);
	}

	@Override
	public List<RfqCq> findCqForEventByOrder(String eventId) {
		List<RfqCq> cqList = cqDao.findCqsForEventByOrder(eventId);
		if (CollectionUtil.isNotEmpty(cqList)) {
			for (RfqCq rftCq : cqList) {
				if (CollectionUtil.isNotEmpty(rftCq.getCqItems())) {
					for (RfqCqItem rftCqItem : rftCq.getCqItems()) {
						if (CollectionUtil.isNotEmpty(rftCqItem.getCqOptions())) {
							for (RfqCqOption option : rftCqItem.getCqOptions()) {
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
	public RfqCq updateCq(RfqCq cq) {
		return cqDao.update(cq);
	}

}

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

import com.privasia.procurehere.core.dao.RfpCqDao;
import com.privasia.procurehere.core.dao.RfpCqItemDao;
import com.privasia.procurehere.core.dao.RfpEnvelopDao;
import com.privasia.procurehere.core.dao.RfpEventDao;
import com.privasia.procurehere.core.entity.Owner;
import com.privasia.procurehere.core.entity.RfpCq;
import com.privasia.procurehere.core.entity.RfpCqItem;
import com.privasia.procurehere.core.entity.RfpCqOption;
import com.privasia.procurehere.core.entity.RfpEvent;
import com.privasia.procurehere.core.enums.CqType;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.CqItemPojo;
import com.privasia.procurehere.core.pojo.CqPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.RfpCqService;

/**
 * @author Ravi
 */
@Service
@Transactional(readOnly = true)
public class RfpCqServiceImpl implements RfpCqService {

	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@Autowired
	MessageSource messageSource;

	@Autowired
	RfpEventDao eventDao;

	@Autowired
	RfpCqDao cqDao;

	@Autowired
	RfpCqItemDao cqItemDao;

	@Autowired
	RfpEnvelopDao envelopDao;

	@Autowired
	RfpCqDao rfpCqDao;

	@Override
	public List<RfpCq> findCqForEvent(String eventId) {
		List<RfpCq> cqList = cqDao.findCqsForEvent(eventId);
		if (CollectionUtil.isNotEmpty(cqList)) {
			for (RfpCq rftCq : cqList) {
				if (CollectionUtil.isNotEmpty(rftCq.getCqItems())) {
					for (RfpCqItem rftCqItem : rftCq.getCqItems()) {
						if (CollectionUtil.isNotEmpty(rftCqItem.getCqOptions())) {
							for (RfpCqOption option : rftCqItem.getCqOptions()) {
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
	public RfpEvent findEventForCqByEventId(String eventId) {
		RfpEvent rfp = eventDao.findEventForCqByEventId(eventId);
		if (rfp.getEventOwner() != null) {
			rfp.getEventOwner().getName();
			rfp.getEventOwner().getCommunicationEmail();
			rfp.getEventOwner().getPhoneNumber();
			if (rfp.getEventOwner().getOwner() != null) {
				Owner usr = rfp.getEventOwner().getOwner();
				usr.getCompanyContactNumber();
			}
		}
		return rfp;
	}

	@Override
	@Transactional(readOnly = false)
	public RfpCq stroreCq(RfpCq cq) {
		List<RfpCq> cqList = findCqForEventByOrder(cq.getRfxEvent().getId());
		Integer count = 1;
		if (CollectionUtil.isNotEmpty(cqList)) {
			for (RfpCq eventCq : cqList) {
				if (eventCq.getCqOrder() == null) {
					eventCq.setCqOrder(count);
					rfpCqDao.update(eventCq);
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
			RfpCq cqPersistObj = cqDao.findById(cq.getId());
			cqPersistObj.setName(cq.getName());
			cqPersistObj.setDescription(cq.getDescription());
			cqPersistObj.setModifiedDate(new Date());
			cq = cqPersistObj;
		}
		RfpCq rfpCq = cqDao.saveOrUpdate(cq);
		return rfpCq;
	}

	@Override
	public List<RfpCqItem> findCqItemsForCq(String eventId) {
		List<RfpCqItem> cqItemList = cqItemDao.getCqItemsForEventId(eventId);
		for (RfpCqItem rftCqItem : cqItemList) {
			if (CollectionUtil.isNotEmpty(rftCqItem.getCqOptions())) {
				for (RfpCqOption rftCqOption : rftCqItem.getCqOptions()) {
					rftCqOption.getRfpCqItem();
				}
			}
		}
		return cqItemList;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
	public RfpCq getCqById(String id) {
		RfpCq cq = cqDao.getCqForId(id);
		if (cq != null && cq.getRfxEvent() != null) {
			if (cq.getRfxEvent().getEventOwner() != null) {
				cq.getRfxEvent().getEventOwner().getTenantId();
			}
		}
		return cq;
	}

	@Override
	@Transactional(readOnly = false)
	public RfpCqItem saveCqItem(RfpCqItem cqItem) {

		if (cqItem.getParent() == null) {
			int itemLevel = 0;
			List<RfpCqItem> list = cqItemDao.getCqItemLevelOrder(cqItem.getCq().getId());
			if (CollectionUtil.isNotEmpty(list)) {
				itemLevel = list.size();
			}
			cqItem.setLevel(itemLevel + 1);
			cqItem.setOrder(0);
		} else {
			// LOG.info("PARENT : " + cqItem.getParent().getId());
			// RfpCqItem parent = rftCqItemDao.getCqItembyCqId(rftCqItem.getParent().getId());
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
	public List<RfpCqItem> findCqbyCqId(String cqId) {
		List<RfpCqItem> returnList = new ArrayList<RfpCqItem>();
		List<RfpCqItem> list = cqItemDao.getCqItemsbyId(cqId);
		bulidCqItemList(returnList, list);
		return returnList;
	}

	/**
	 * @param returnList
	 * @param list
	 */
	private void bulidCqItemList(List<RfpCqItem> returnList, List<RfpCqItem> list) {
		if (CollectionUtil.isNotEmpty(list)) {
			for (RfpCqItem item : list) {
				RfpCqItem parent = item.createShallowCopy();
				returnList.add(parent);
				if (CollectionUtil.isNotEmpty(item.getChildren())) {
					for (RfpCqItem child : item.getChildren()) {
						if (parent.getChildren() == null) {
							parent.setChildren(new ArrayList<RfpCqItem>());
						}
						parent.getChildren().add(child.createShallowCopy());
					}
				}
			}
		}
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
	public RfpCqItem getCqItembyCqItemId(String parent) {
		RfpCqItem item = cqItemDao.getCqItembyCqItemId(parent);
		if (item != null && CollectionUtil.isNotEmpty(item.getCqOptions())) {
			for (RfpCqOption option : item.getCqOptions()) {
				option.getValue();
			}
		}
		return item;
	}

	@Override
	@Transactional(readOnly = false)
	public void updateCqItem(CqItemPojo rfpCqItem) throws ApplicationException {
		RfpCqItem item = getCqItembyCqItemId(rfpCqItem.getId());
		LOG.info("item.getCq()...." + rfpCqItem.getCq() + "item.getParent()..." + rfpCqItem.getParent());
		item.setItemName(rfpCqItem.getItemName());
		item.setIsSupplierAttachRequired(rfpCqItem.getIsSupplierAttachRequired());
		item.setItemDescription(rfpCqItem.getItemDescription());
		item.setAttachment(rfpCqItem.isAttachment());
		item.setOptional(rfpCqItem.isOptional());
		item.setCqType(rfpCqItem.getCqType() != null ? CqType.valueOf(rfpCqItem.getCqType()) : null);
		if (CollectionUtil.isNotEmpty(rfpCqItem.getOptions())) {
			List<RfpCqOption> optionItems = new ArrayList<RfpCqOption>();
			int optionOrder = 0;
			for (String option : rfpCqItem.getOptions()) {
				if (StringUtils.checkString(option).length() == 0)
					continue;
				RfpCqOption options = new RfpCqOption();
				options.setRfpCqItem(item);
				options.setValue(option);
				options.setOrder(++optionOrder);
				if (CollectionUtil.isNotEmpty(rfpCqItem.getOptionScore()))
					options.setScoring(Integer.parseInt(rfpCqItem.getOptionScore().get(optionOrder - 1)));
				optionItems.add(options);
			}
			item.setCqOptions(optionItems);
		}
		cqItemDao.saveOrUpdate(item);
	}

	@Override
	public List<RfpCq> getNotAssignedCqIdsByEventId(String eventId) {
		return envelopDao.getNotAssignedRfpCqIdsByEventId(eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteCq(RfpCq cq) {
		cqDao.deleteCqById(cq.getId());

		List<RfpCq> cqList = findCqForEventByOrder(cq.getRfxEvent().getId());
		if (CollectionUtil.isNotEmpty(cqList)) {
			Integer count = 1;
			for (RfpCq rfpCq : cqList) {
				rfpCq.setCqOrder(count);
				rfpCqDao.update(rfpCq);
				count++;
			}
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteCqItems(String[] cqItemIds, String cqId) throws NotAllowedException {
		RfpCq item = cqDao.findById(cqId);
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
	public List<RfpCq> getCqByEventId(String id, List<String> cqIds) {
		return cqDao.findCqsByEventIdAndCqIds(id, cqIds);
	}

	@Override
	@Transactional(readOnly = false)
	public void reorderCqItems(CqItemPojo rftCqItemPojo) throws NotAllowedException {

		LOG.info("CQ ITEM Object :: " + rftCqItemPojo.toString());
		int newOrder = rftCqItemPojo.getOrder();
		RfpCqItem cqItem = getCqItembyCqItemId(rftCqItemPojo.getId());

		if (CollectionUtil.isNotEmpty(cqItem.getChildren()) && rftCqItemPojo.getParent() != null) {
			throw new NotAllowedException("CQ Item cannot be made a child if it has sub items");
		}

		LOG.info("DB CQ ITEM DETAILS ::" + cqItem.toString());
		int oldOrder = cqItem.getOrder();
		LOG.info("Item Old Position :: " + oldOrder + " :: Item New Position :: " + newOrder);
		int oldLevel = cqItem.getLevel();
		int newLevel = rftCqItemPojo.getOrder(); // this will be ignored if it is made a child
		RfpCqItem newParent = null;
		if (rftCqItemPojo.getParent() != null) {
			newParent = getCqItembyCqItemId(rftCqItemPojo.getParent());
		}
		RfpCqItem oldParent = cqItem.getParent();

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

		RfpEvent event = eventDao.findById(eventId);
		event.setCqCompleted(Boolean.FALSE);
		event.setQuestionnaires(Boolean.FALSE);
		eventDao.update(event);
	}

	@Override
	public RfpCqItem findCqItemForCqByEventId(String eventId) {
		RfpCqItem item = cqItemDao.getCqItemByEventId(eventId);
		if (item != null && CollectionUtil.isNotEmpty(item.getCqOptions())) {
			for (RfpCqOption option : item.getCqOptions()) {
				option.getValue();
			}
		}
		return item;
	}

	@Override
	public RfpEvent getEventById(String eventId) {
		return eventDao.findByEventId(eventId);
	}

	@Override
	public boolean isExists(RfpCq rftCq) {
		LOG.info("Checking for duplicate CQ with name : " + rftCq.getName() + " event Id : " + rftCq.getRfxEvent().getId() + "  id : " + rftCq.getId());
		return cqDao.isExists(rftCq, rftCq.getRfxEvent().getId());
	}

	@Override
	public boolean isExists(RfpCqItem rfpCqItem, String cqId, String parentId) {
		LOG.info("Checking for duplicate CQ Item with name : " + rfpCqItem.getItemName() + " CQ Id : " + cqId + "  Parent : " + parentId);
		return cqItemDao.isExists(rfpCqItem, cqId, parentId);
	}

	@Override
	public List<RfpCqItem> getAllCqitemsbyCqId(String cqId) {
		List<RfpCqItem> itemList = cqItemDao.getAllCqItemsbycqId(cqId);
		for (RfpCqItem rfpCqItem : itemList) {
			if (CollectionUtil.isNotEmpty(rfpCqItem.getCqOptions()))
				for (RfpCqOption cqOption : rfpCqItem.getCqOptions()) {
					cqOption.getValue();
				}
		}
		return itemList;
	}

	@Override
	public RfpCqItem getParentbyLevelId(String cqId, Integer level) {
		return cqItemDao.getParentbyLevelId(cqId, level);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
	public String doExcelDataSave(Map<Integer, Map<Integer, RfpCqItem>> dataMap, String eventId, String cqId) throws Exception {

		LOG.info("dataMap  " + dataMap.size());
		RfpCq cq = getCqById(cqId);
		RfpEvent event = getEventById(eventId);

		for (RfpCqItem item : cq.getCqItems()) {
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
		for (Map.Entry<Integer, Map<Integer, RfpCqItem>> entry : dataMap.entrySet()) {
			RfpCqItem parent = null;
			for (Map.Entry<Integer, RfpCqItem> cqItem : entry.getValue().entrySet()) {
				// if(parent != null) {
				cqItem.getValue().setParent(parent);
				// }
				if (cqItem.getValue().getOrder() == 0) {
					parent = cqItem.getValue();
				} else {
					cqItem.getValue().setCqOptions(new ArrayList<>());
					int index = 1;
					for (String option : cqItem.getValue().getOptions()) {
						RfpCqOption rOption = new RfpCqOption();
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
						rOption.setRfpCqItem(cqItem.getValue());
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
	public List<String> getNotSectionAddedRfpCqIdsByEventId(String eventId) {
		return cqDao.getNotSectionAddedRfpCqIdsByEventId(eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public boolean updateLeadEvaluatorComment(String itemId, String leadEvaluationComment) {
		return cqItemDao.updateLeadEvaluatorComment(itemId, leadEvaluationComment);
	}

	@Override
	public String getLeadEvaluatorComment(String itemId) {
		return cqItemDao.getLeadEvaluatorComment(itemId);
	}

	@Override
	public List<String> getNotSectionItemAddedRfpCqIdsByEventId(String eventId) {
		return cqDao.getNotSectionItemAddedRfpCqIdsByEventId(eventId);
	}

	@Override
	public List<RfpCq> findCqForEventByEnvelopeId(String eventId, String envelopeId) {

		List<RfpCq> cqList = cqDao.findCqsForEventByEnvelopeId(eventId, envelopeId);
		if (CollectionUtil.isNotEmpty(cqList)) {
			for (RfpCq rftCq : cqList) {
				if (CollectionUtil.isNotEmpty(rftCq.getCqItems())) {
					for (RfpCqItem rftCqItem : rftCq.getCqItems()) {
						if (CollectionUtil.isNotEmpty(rftCqItem.getCqOptions())) {
							for (RfpCqOption option : rftCqItem.getCqOptions()) {
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
	public List<RfpCq> findRfaCqForEventByEnvelopeId(List<String> cqid, String id) {

		List<RfpCq> cqs = rfpCqDao.findCqsForEventEnvelopeId(cqid, id);
		if (CollectionUtil.isNotEmpty(cqs)) {
			for (RfpCq cq : cqs) {
				if (CollectionUtil.isNotEmpty(cq.getCqItems())) {
					for (RfpCqItem item : cq.getCqItems()) {
						if (CollectionUtil.isEmpty(item.getCqOptions())) {
							for (RfpCqOption option : item.getCqOptions()) {
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
		return rfpCqDao.findEventForCqPojoByEventId(eventId);
	}

	@Override
	public List<RfpCq> findCqForEventByOrder(String eventId) {
		List<RfpCq> cqList = cqDao.findCqsForEventByOrder(eventId);
		if (CollectionUtil.isNotEmpty(cqList)) {
			for (RfpCq rftCq : cqList) {
				if (CollectionUtil.isNotEmpty(rftCq.getCqItems())) {
					for (RfpCqItem rftCqItem : rftCq.getCqItems()) {
						if (CollectionUtil.isNotEmpty(rftCqItem.getCqOptions())) {
							for (RfpCqOption option : rftCqItem.getCqOptions()) {
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
	public RfpCq updateCq(RfpCq cq) {
		return rfpCqDao.update(cq);
	}
}

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

import com.privasia.procurehere.core.dao.RfiCqDao;
import com.privasia.procurehere.core.dao.RfiCqItemDao;
import com.privasia.procurehere.core.dao.RfiEnvelopDao;
import com.privasia.procurehere.core.dao.RfiEventDao;
import com.privasia.procurehere.core.entity.Owner;
import com.privasia.procurehere.core.entity.RfiCq;
import com.privasia.procurehere.core.entity.RfiCqItem;
import com.privasia.procurehere.core.entity.RfiCqOption;
import com.privasia.procurehere.core.entity.RfiEvent;
import com.privasia.procurehere.core.enums.CqType;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.CqItemPojo;
import com.privasia.procurehere.core.pojo.CqPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.RfiCqService;

/**
 * @author Ravi
 */
@Service
@Transactional(readOnly = true)
public class RfiCqServiceImpl implements RfiCqService {

	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@Autowired
	MessageSource messageSource;

	@Autowired
	RfiEventDao rfiEventDao;

	@Autowired
	RfiCqDao rfiCqDao;

	@Autowired
	RfiCqItemDao rfiCqItemDao;

	@Autowired
	RfiEnvelopDao rfiEnvelopDao;

	@Override
	public List<RfiCq> findRfiCqForEvent(String eventId) {
		// return rfiCqDao.findCqsForEvent(eventId);
		List<RfiCq> cqList = rfiCqDao.findCqsForEvent(eventId);
		if (CollectionUtil.isNotEmpty(cqList)) {
			for (RfiCq rfiCq : cqList) {
				if (CollectionUtil.isNotEmpty(rfiCq.getCqItems())) {
					for (RfiCqItem rftCqItem : rfiCq.getCqItems()) {
						if (CollectionUtil.isNotEmpty(rftCqItem.getCqOptions())) {
							for (RfiCqOption option : rftCqItem.getCqOptions()) {
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
	public RfiEvent findEventForCqByEventId(String eventId) {
		RfiEvent rfi = rfiEventDao.findEventForCqByEventId(eventId);
		if (rfi.getEventOwner() != null) {
			rfi.getEventOwner().getName();
			if (rfi.getEventOwner().getOwner() != null) {
				Owner usr = rfi.getEventOwner().getOwner();
				usr.getCompanyContactNumber();
			}
		}
		return rfi;
	}

	@Override
	@Transactional(readOnly = false)
	public RfiCq stroreCq(RfiCq cq) {
		List<RfiCq> cqList = findRfiCqForEventByOrder(cq.getRfxEvent().getId());
		Integer count = 1;
		if (CollectionUtil.isNotEmpty(cqList)) {
			for (RfiCq eventCq : cqList) {
				if (eventCq.getCqOrder() == null) {
					eventCq.setCqOrder(count);
					rfiCqDao.update(eventCq);
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
			RfiCq cqPersistObj = rfiCqDao.findById(cq.getId());
			cqPersistObj.setName(cq.getName());
			cqPersistObj.setDescription(cq.getDescription());
			cqPersistObj.setModifiedDate(new Date());
			cq = cqPersistObj;
		}
		RfiCq rfiCq = rfiCqDao.saveOrUpdate(cq);
		return rfiCq;
	}

	@Override
	public List<RfiCqItem> findCqItembyCqId(String cqId) {
		List<RfiCqItem> returnList = new ArrayList<RfiCqItem>();
		List<RfiCqItem> list = rfiCqItemDao.getCqItemsbyId(cqId);
		bulidCqItemList(returnList, list);
		return returnList;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
	public RfiCq getCqById(String id) {
		RfiCq cq = rfiCqDao.getCqForId(id);
		if (cq != null && cq.getRfxEvent() != null) {
			if (cq.getRfxEvent().getEventOwner() != null) {
				cq.getRfxEvent().getEventOwner().getTenantId();
			}
		}
		return cq;
	}

	@Override
	@Transactional(readOnly = false)
	public RfiCqItem saveCqItem(RfiCqItem rftCqItem) {

		if (rftCqItem.getParent() == null) {
			int itemLevel = 0;
			List<RfiCqItem> list = rfiCqItemDao.getCqItemLevelOrder(rftCqItem.getCq().getId());
			if (CollectionUtil.isNotEmpty(list)) {
				itemLevel = list.size();
			}
			rftCqItem.setLevel(itemLevel + 1);
			rftCqItem.setOrder(0);
		} else {
			// LOG.info("PARENT : " + rftCqItem.getParent().getId());
			// RftCqItem parent = rftCqItemDao.getCqItembyCqId(rftCqItem.getParent().getId());
			if (rftCqItem.getParent() != null) {
				rftCqItem.setLevel(rftCqItem.getParent().getLevel());
				rftCqItem.setOrder(CollectionUtil.isEmpty(rftCqItem.getParent().getChildren()) ? 1 : rftCqItem.getParent().getChildren().size() + 1);
			}
		}
		// LOG.info("LOG : " + rftCqItem.getCqOptions().size());
		return rfiCqItemDao.saveOrUpdate(rftCqItem);
	}

	@Override
	@Transactional(readOnly = true)
	public List<RfiCqItem> findCqItemsByCqId(String cqId) {
		List<RfiCqItem> returnList = new ArrayList<RfiCqItem>();
		List<RfiCqItem> list = rfiCqItemDao.getCqItemsbyId(cqId);
		bulidCqItemList(returnList, list);
		for (RfiCqItem rfiCqItem : returnList) {
			if (CollectionUtil.isNotEmpty(rfiCqItem.getCqOptions()))
				for (RfiCqOption cqOption : rfiCqItem.getCqOptions()) {
					cqOption.getValue();
				}
		}
		return returnList;
	}

	/**
	 * @param returnList
	 * @param list
	 */
	private void bulidCqItemList(List<RfiCqItem> returnList, List<RfiCqItem> list) {
		if (CollectionUtil.isNotEmpty(list)) {
			for (RfiCqItem item : list) {
				RfiCqItem parent = item.createShallowCopy();
				returnList.add(parent);
				if (CollectionUtil.isNotEmpty(item.getChildren())) {
					for (RfiCqItem child : item.getChildren()) {
						if (parent.getChildren() == null) {
							parent.setChildren(new ArrayList<RfiCqItem>());
						}
						parent.getChildren().add(child.createShallowCopy());
					}
				}
			}
		}
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
	public RfiCqItem getCqItembyCqItemId(String parent) {
		RfiCqItem item = rfiCqItemDao.getCqItembyCqItemId(parent);
		if (item != null && CollectionUtil.isNotEmpty(item.getCqOptions())) {
			for (RfiCqOption option : item.getCqOptions()) {
				option.getValue();
			}
		}
		return item;
	}

	@Override
	@Transactional(readOnly = false)
	public void updateCqItem(CqItemPojo rftCqItem) throws Exception {
		RfiCqItem item = getCqItembyCqItemId(rftCqItem.getId());
		LOG.info("ITEM : " + item.getItemName());
		item.setItemName(rftCqItem.getItemName());
		item.setItemDescription(rftCqItem.getItemDescription());
		item.setAttachment(rftCqItem.isAttachment());
		item.setIsSupplierAttachRequired(rftCqItem.getIsSupplierAttachRequired());
		item.setOptional(rftCqItem.isOptional());
		item.setCqType(rftCqItem.getCqType() != null ? CqType.valueOf(rftCqItem.getCqType()) : null);
		if (CollectionUtil.isNotEmpty(rftCqItem.getOptions())) {
			List<RfiCqOption> optionItems = new ArrayList<RfiCqOption>();
			int optionOrder = 0;
			for (String option : rftCqItem.getOptions()) {
				if (StringUtils.checkString(option).length() == 0)
					continue;
				RfiCqOption options = new RfiCqOption();
				options.setCqItem(item);
				options.setValue(option);
				options.setOrder(++optionOrder);
				if (CollectionUtil.isNotEmpty(rftCqItem.getOptionScore())) {
					options.setScoring(Integer.parseInt(rftCqItem.getOptionScore().get(optionOrder - 1)));
				}
				optionItems.add(options);
			}
			item.setCqOptions(optionItems);
		}
		rfiCqItemDao.saveOrUpdate(item);
	}

	@Override
	public List<RfiCq> getNotAssignedCqIdsByEventId(String eventId) {
		return rfiEnvelopDao.getNotAssignedCqIdsByEventId(eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteCq(RfiCq cq) {
		rfiCqDao.deleteCqById(cq.getId());
		List<RfiCq> cqList = findRfiCqForEventByOrder(cq.getRfxEvent().getId());
		if (CollectionUtil.isNotEmpty(cqList)) {
			Integer count = 1;
			for (RfiCq rfiCq : cqList) {
				rfiCq.setCqOrder(count);
				rfiCqDao.update(rfiCq);
				count++;
			}
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteCqItems(String[] cqItemIds, String cqId) throws NotAllowedException {
		RfiCq item = rfiCqDao.findById(cqId);
		// if (item != null) {
		// if (EventStatus.DRAFT != item.getRfxEvent().getStatus()) {
		// throw new NotAllowedException(messageSource.getMessage("rft.cq.info.draft", new Object[] {}, Global.LOCALE));
		// }
		// }
		rfiCqItemDao.deleteCqItems(cqItemIds, cqId);
	}

	@Override
	public void isAllowToDeleteCq(String cqId) throws NotAllowedException {
		rfiCqDao.isAllowToDeleteCq(cqId);
	}

	@Override
	public List<RfiCq> getCqByEventId(String id, List<String> cqIds) {
		return rfiCqDao.findCqsByEventIdAndCqIds(id, cqIds);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void reorderCqItems(CqItemPojo cqItemPojo) throws NotAllowedException {
		LOG.info("CQ ITEM Object :: " + cqItemPojo.toString());
		int newOrder = cqItemPojo.getOrder();
		RfiCqItem cqItem = getCqItembyCqItemId(cqItemPojo.getId());

		if (CollectionUtil.isNotEmpty(cqItem.getChildren()) && cqItemPojo.getParent() != null) {
			throw new NotAllowedException("CQ Item cannot be made a child if it has sub items");
		}

		LOG.info("DB CQ ITEM DETAILS ::" + cqItem.toString());
		int oldOrder = cqItem.getOrder();
		LOG.info("Item Old Position :: " + oldOrder + " :: Item New Position :: " + newOrder);
		int oldLevel = cqItem.getLevel();
		int newLevel = cqItemPojo.getOrder(); // this will be ignored if it is made a child
		RfiCqItem newParent = null;
		if (cqItemPojo.getParent() != null && StringUtils.checkString(cqItemPojo.getParent()).length() > 0) {
			newParent = getCqItembyCqItemId(cqItemPojo.getParent());
		}
		RfiCqItem oldParent = cqItem.getParent();

		// If these are not child, their order should be reset to 0
		if (oldParent == null) {
			oldOrder = 0;
		}
		if (newParent == null) {
			newOrder = 0;
		}

		// Update it to new position.
		cqItem.setOrder(newOrder);
		cqItem.setLevel(newParent == null ? cqItemPojo.getOrder() : 0);
		cqItem.setParent(newParent);
		rfiCqItemDao.updateItemOrder(cqItem.getCq().getId(), cqItem, oldParent == null ? null : oldParent.getId(), newParent == null ? null : newParent.getId(), oldOrder, newOrder, oldLevel, newLevel);

	}

	public Integer getCountOfCqByEventId(String eventId) {
		return rfiCqDao.getCountOfCqByEventId(eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteAllCqs(String eventId, String eventRequirement) {
		rfiEnvelopDao.removeCqsFromEnvelops(eventId);
		rfiCqDao.deleteCqByEventId(eventId);

		RfiEvent event = rfiEventDao.findById(eventId);
		event.setCqCompleted(Boolean.FALSE);
		event.setQuestionnaires(Boolean.FALSE);
		rfiEventDao.update(event);

	}

	@Override
	public RfiCqItem findCqItemForCqByEventId(String eventId) {
		RfiCqItem item = rfiCqItemDao.getCqItemByEventId(eventId);
		if (item != null && CollectionUtil.isNotEmpty(item.getCqOptions())) {
			for (RfiCqOption option : item.getCqOptions()) {
				option.getValue();
			}
		}
		return item;
	}

	@Override
	public RfiEvent getRfiEventById(String eventId) {
		return rfiEventDao.findById(eventId);
	}

	@Override
	public boolean isExists(RfiCqItem item, String cq, String parent) {
		LOG.info("Checking for duplicate CQ Item with name : " + item.getItemName() + " CQ Id : " + cq + "  Parent : " + parent);
		return rfiCqItemDao.isExists(item, cq, parent);
	}

	@Override
	public boolean isExists(RfiCq rftCq) {
		LOG.info("Checking for duplicate CQ with name : " + rftCq.getName() + " event Id : " + rftCq.getRfxEvent().getId() + "  id : " + rftCq.getId());
		return rfiCqDao.isExists(rftCq, rftCq.getRfxEvent().getId());
	}

	@Override
	public RfiCqItem getParentbyLevelId(String cqId, Integer level) {
		return rfiCqItemDao.getParentbyLevelId(cqId, level);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
	public String doExcelDataSave(Map<Integer, Map<Integer, RfiCqItem>> dataMap, String eventId, String cqId) throws Exception {

		LOG.info("dataMap  " + dataMap.size());
		RfiCq cq = getCqById(cqId);
		RfiEvent event = getRfiEventById(eventId);

		for (RfiCqItem item : cq.getCqItems()) {
			if (item.getOrder() == 0) {
				LOG.info("Deleting CQ Item : " + item.toLogString());
				item.setCq(null);
				rfiCqItemDao.delete(item);
			}
		}
		if (cq.getCqItems() != null) {
			cq.getCqItems().clear();
			rfiCqDao.update(cq);
		}
		int rowNum = 2;

		// checking item exists validation
		int levelTemp = 1;
		int orderTemp = 0;
		boolean isItemExists = false;
		String message = null;

		for (Map.Entry<Integer, Map<Integer, RfiCqItem>> entry : dataMap.entrySet()) {
			RfiCqItem parent = null;
			for (Map.Entry<Integer, RfiCqItem> cqItem : entry.getValue().entrySet()) {
				// if(parent != null) {
				cqItem.getValue().setParent(parent);
				// }
				if (cqItem.getValue().getOrder() == 0) {
					parent = cqItem.getValue();
				} else {
					cqItem.getValue().setCqOptions(new ArrayList<>());
					int index = 1;
					for (String option : cqItem.getValue().getOptions()) {
						RfiCqOption rOption = new RfiCqOption();
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

				rfiCqItemDao.save(cqItem.getValue());
				rowNum++;
			}
		}
		if (isItemExists) {
			message = messageSource.getMessage("common.upload.warning", new Object[] {}, Global.LOCALE);
		}
		return message;
	}

	@Override
	public List<RfiCqItem> getAllCqitemsbyCqId(String cqId) {
		List<RfiCqItem> itemList = rfiCqItemDao.getAllCqItemsbycqId(cqId);
		for (RfiCqItem rfiCqItem : itemList) {
			if (CollectionUtil.isNotEmpty(rfiCqItem.getCqOptions()))
				for (RfiCqOption cqOption : rfiCqItem.getCqOptions()) {
					cqOption.getValue();
				}
		}
		return itemList;
	}

	@Override
	public int CountAllMandatoryCqByEventId(String eventId) {
		return rfiCqItemDao.CountCqItemsbyEventId(eventId);
	}

	@Override
	public List<String> getNotSectionAddedRfiCqIdsByEventId(String eventId) {
		return rfiCqDao.getNotSectionAddedRfiCqIdsByEventId(eventId);
	}

	@Override
	public String getLeadEvaluatorComment(String itemId) {
		return rfiCqItemDao.getLeadEvaluatorComment(itemId);
	}

	@Override
	@Transactional(readOnly = false)
	public boolean updateLeadEvaluatorComment(String itemId, String leadEvaluationComment) {
		return rfiCqItemDao.updateLeadEvaluatorComment(itemId, leadEvaluationComment);
	}

	@Override
	public List<String> getNotSectionItemAddedRfiCqIdsByEventId(String eventId) {
		return rfiCqDao.getNotSectionItemAddedRfiCqIdsByEventId(eventId);
	}

	@Override
	public List<RfiCq> findRfiCqForEventByEnvelopeId(String eventId, String envelopeId) {
		return rfiCqDao.findRfiCqForEventByEnvelopeId(eventId, envelopeId);
	}

	@Override
	public List<RfiCq> findRfiCqForEventByEnvelopeId(List<String> cqid, String id) {

		List<RfiCq> cqs = rfiCqDao.findCqsForEventEnvelopeId(cqid, id);
		if (CollectionUtil.isNotEmpty(cqs)) {
			for (RfiCq cq : cqs) {
				if (CollectionUtil.isNotEmpty(cq.getCqItems())) {
					for (RfiCqItem item : cq.getCqItems()) {
						if (CollectionUtil.isEmpty(item.getCqOptions())) {
							for (RfiCqOption option : item.getCqOptions()) {
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
		return rfiCqDao.findEventForCqPojoByEventId(eventId);
	}

	@Override
	public List<RfiCq> findRfiCqForEventByOrder(String eventId) {
		return rfiCqDao.findCqsForEventByOrder(eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public RfiCq updateCq(RfiCq cq) {
		return rfiCqDao.update(cq);
	}
}

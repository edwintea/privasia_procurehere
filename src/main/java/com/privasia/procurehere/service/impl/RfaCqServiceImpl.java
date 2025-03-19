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

import com.privasia.procurehere.core.dao.RfaCqDao;
import com.privasia.procurehere.core.dao.RfaCqItemDao;
import com.privasia.procurehere.core.dao.RfaEnvelopDao;
import com.privasia.procurehere.core.dao.RfaEventDao;
import com.privasia.procurehere.core.entity.Owner;
import com.privasia.procurehere.core.entity.RfaCq;
import com.privasia.procurehere.core.entity.RfaCqItem;
import com.privasia.procurehere.core.entity.RfaCqOption;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.enums.CqType;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.CqItemPojo;
import com.privasia.procurehere.core.pojo.CqPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.RfaCqService;

/**
 * @author RT-Kapil
 */
@Service
@Transactional(readOnly = true)
public class RfaCqServiceImpl implements RfaCqService {

	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@Autowired
	MessageSource messageSource;

	@Autowired
	RfaEventDao rfaEventDao;

	@Autowired
	RfaCqDao rfaCqDao;

	@Autowired
	RfaCqItemDao rfaCqItemDao;

	@Autowired
	RfaEnvelopDao rfaEnvelopDao;

	@Override
	public List<RfaCq> findRfaCqForEvent(String eventId) {
		List<RfaCq> cqs = rfaCqDao.findCqsForEvent(eventId);
		if (CollectionUtil.isNotEmpty(cqs)) {
			for (RfaCq cq : cqs) {
				if (CollectionUtil.isNotEmpty(cq.getCqItems())) {
					for (RfaCqItem item : cq.getCqItems()) {
						if (CollectionUtil.isEmpty(item.getCqOptions())) {
							for (RfaCqOption option : item.getCqOptions()) {
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
	public RfaEvent findEventForCqByEventId(String eventId) {
		RfaEvent rfa = rfaEventDao.findEventForCqByEventId(eventId);
		if (rfa.getEventOwner() != null) {
			rfa.getEventOwner().getName();
			if (rfa.getEventOwner().getOwner() != null) {
				Owner usr = rfa.getEventOwner().getOwner();
				usr.getCompanyContactNumber();
			}
		}
		return rfa;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
	public RfaCq getCqById(String id) {
		return rfaCqDao.getCqForId(id);
	}

	@Override
	@Transactional(readOnly = false)
	public RfaCq stroreRfaCq(RfaCq cq) {
		List<RfaCq> cqList = findRfaCqForEventByOrder(cq.getRfxEvent().getId());
		Integer count = 1;
		if (CollectionUtil.isNotEmpty(cqList)) {
			for (RfaCq eventCq : cqList) {
				if (eventCq.getCqOrder() == null) {
					eventCq.setCqOrder(count);
					rfaCqDao.update(eventCq);
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
			RfaCq cqPersistObj = rfaCqDao.findById(cq.getId());
			cqPersistObj.setName(cq.getName());
			cqPersistObj.setDescription(cq.getDescription());
			cqPersistObj.setModifiedDate(new Date());
			cq = cqPersistObj;
		}
		RfaCq rfaCq = rfaCqDao.saveOrUpdate(cq);

		return rfaCq;
	}

	@Override
	public List<RfaCqItem> findRfaCqItemsForCq(String eventId) {
		List<RfaCqItem> cqItemList = rfaCqItemDao.getCqItemsForEventId(eventId);
		for (RfaCqItem rfaCqItem : cqItemList) {
			if (CollectionUtil.isNotEmpty(rfaCqItem.getCqOptions())) {
				for (RfaCqOption rfaCqOption : rfaCqItem.getCqOptions()) {
					rfaCqOption.getRfaCqItem();
				}
			}
		}
		return cqItemList;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
	public RfaCq getRfaCqById(String id) {
		RfaCq cq = rfaCqDao.getCqForId(id);
		if (cq != null && cq.getRfxEvent() != null && cq.getRfxEvent().getEventOwner() != null) {
			cq.getRfxEvent().getEventOwner().getTenantId();
		}
		return cq;
	}

	@Override
	@Transactional(readOnly = false)
	public RfaCqItem saveRfaCqItem(RfaCqItem rfaCqItem) {
		LOG.info("LOG : " + rfaCqItem.getCqOptions());

		if (rfaCqItem.getParent() == null) {
			int itemLevel = 0;
			List<RfaCqItem> list = rfaCqItemDao.getCqItemLevelOrder(rfaCqItem.getCq().getId());
			if (CollectionUtil.isNotEmpty(list)) {
				itemLevel = list.size();
			}
			rfaCqItem.setLevel(itemLevel + 1);
			rfaCqItem.setOrder(0);
		} else {
			// LOG.info("PARENT : " + rfaCqItem.getParent().getId());
			// RftCqItem parent =
			// rftCqItemDao.getCqItembyCqId(rftCqItem.getParent().getId());
			if (rfaCqItem.getParent() != null) {
				rfaCqItem.setLevel(rfaCqItem.getParent().getLevel());
				rfaCqItem.setOrder(CollectionUtil.isEmpty(rfaCqItem.getParent().getChildren()) ? 1 : rfaCqItem.getParent().getChildren().size() + 1);
			}
		}
		return rfaCqItemDao.saveOrUpdate(rfaCqItem);
	}

	@Override
	@Transactional(readOnly = true)
	public List<RfaCqItem> findRfaCqbyCqId(String cqId) {
		List<RfaCqItem> returnList = new ArrayList<RfaCqItem>();
		List<RfaCqItem> list = rfaCqItemDao.getCqItemsbyId(cqId);
		bulidCqItemList(returnList, list);
		return returnList;
	}

	/**
	 * @param returnList
	 * @param list
	 */
	private void bulidCqItemList(List<RfaCqItem> returnList, List<RfaCqItem> list) {
		if (CollectionUtil.isNotEmpty(list)) {
			for (RfaCqItem item : list) {
				RfaCqItem parent = item.createShallowCopy();
				returnList.add(parent);
				if (CollectionUtil.isNotEmpty(item.getChildren())) {
					for (RfaCqItem child : item.getChildren()) {
						if (parent.getChildren() == null) {
							parent.setChildren(new ArrayList<RfaCqItem>());
						}
						parent.getChildren().add(child.createShallowCopy());
					}
				}
			}
		}
	}

	@Override
	public boolean updateLeadEvaluatorComment(String cqItemId, String leadComment) {
		return rfaCqItemDao.updateLeadEvaluatorComment(cqItemId, leadComment);
	}

	@Override
	@Transactional(readOnly = true)
	public RfaCqItem getCqItembyCqItemId(String parent) {
		RfaCqItem item = rfaCqItemDao.getCqItembyCqItemId(parent);
		if (item != null && CollectionUtil.isNotEmpty(item.getCqOptions())) {
			for (RfaCqOption option : item.getCqOptions()) {
				option.getValue();
			}
		}
		return item;
	}

	@Override
	@Transactional(readOnly = false)
	public void updateCqItem(CqItemPojo item) {
		RfaCqItem rfaCqItem = getCqItembyCqItemId(item.getId());
		LOG.info("ITEM : " + rfaCqItem.getItemName());
		rfaCqItem.setItemName(item.getItemName());
		rfaCqItem.setItemDescription(item.getItemDescription());
		rfaCqItem.setAttachment(item.isAttachment());
		rfaCqItem.setOptional(item.isOptional());
		rfaCqItem.setIsSupplierAttachRequired(item.getIsSupplierAttachRequired());

		rfaCqItem.setCqType(item.getCqType() != null ? CqType.valueOf(item.getCqType()) : null);
		if (CollectionUtil.isNotEmpty(item.getOptions())) {
			List<RfaCqOption> optionItems = new ArrayList<RfaCqOption>();
			int optionOrder = 0;
			for (String option : item.getOptions()) {
				if (StringUtils.checkString(option).length() == 0)
					continue;
				RfaCqOption options = new RfaCqOption();
				options.setRfaCqItem(rfaCqItem);
				options.setValue(option);
				options.setOrder(++optionOrder);
				if (CollectionUtil.isNotEmpty(item.getOptionScore()))
					options.setScoring(Integer.parseInt(item.getOptionScore().get(optionOrder - 1)));
				optionItems.add(options);
			}
			rfaCqItem.setCqOptions(optionItems);
		}
		rfaCqItemDao.saveOrUpdate(rfaCqItem);
	}

	@Override
	public List<RfaCq> getNotAssignedRfaCqIdsByEventId(String eventId) {
		return rfaEnvelopDao.getNotAssignedRfaCqIdsByEventId(eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteCq(RfaCq cq) {
		rfaCqDao.deleteCqById(cq.getId());

		List<RfaCq> cqList = findRfaCqForEventByOrder(cq.getRfxEvent().getId());
		if (CollectionUtil.isNotEmpty(cqList)) {
			Integer count = 1;
			for (RfaCq rfaCq : cqList) {
				rfaCq.setCqOrder(count);
				rfaCqDao.update(rfaCq);
				count++;
			}
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteCqItems(String[] cqItemIds, String cqId) throws NotAllowedException {
		RfaCq item = rfaCqDao.findById(cqId);
		// if (item != null) {
		// if (EventStatus.DRAFT != item.getRfxEvent().getStatus()) {
		// throw new
		// NotAllowedException(messageSource.getMessage("rft.cq.info.draft", new
		// Object[] {}, Global.LOCALE));
		// }
		// }
		rfaCqItemDao.deleteCqItems(cqItemIds, cqId);
	}

	@Override
	public void isAllowToDeleteCq(String cqId) throws NotAllowedException {
		rfaCqDao.isAllowToDeleteCq(cqId);
	}

	@Override
	public List<RfaCq> getRfaCqByEventId(String id, List<String> cqIds) {
		return rfaCqDao.findCqsByEventIdAndCqIds(id, cqIds);
	}

	@Override
	@Transactional(readOnly = false)
	public void reorderCqItems(CqItemPojo rfaCqItemPojo) throws NotAllowedException {

		LOG.info("CQ ITEM Object :: " + rfaCqItemPojo.toString());
		int newOrder = rfaCqItemPojo.getOrder();
		RfaCqItem cqItem = getCqItembyCqItemId(rfaCqItemPojo.getId());

		if (CollectionUtil.isNotEmpty(cqItem.getChildren()) && rfaCqItemPojo.getParent() != null) {
			throw new NotAllowedException("CQ Item cannot be made a child if it has sub items");
		}

		LOG.info("DB CQ ITEM DETAILS ::" + cqItem.toString());
		int oldOrder = cqItem.getOrder();
		LOG.info("Item Old Position :: " + oldOrder + " :: Item New Position :: " + newOrder);
		int oldLevel = cqItem.getLevel();
		int newLevel = rfaCqItemPojo.getOrder(); // this will be ignored if it
													// is made a child
		RfaCqItem newParent = null;
		if (rfaCqItemPojo.getParent() != null) {
			newParent = getCqItembyCqItemId(rfaCqItemPojo.getParent());
		}
		RfaCqItem oldParent = cqItem.getParent();

		// If these are not child, their order should be reset to 0
		if (oldParent == null) {
			oldOrder = 0;
		}
		if (newParent == null) {
			newOrder = 0;
		}

		// Update it to new position.
		cqItem.setOrder(newOrder);
		cqItem.setLevel(newParent == null ? rfaCqItemPojo.getOrder() : newParent.getLevel());
		cqItem.setParent(newParent);

		rfaCqItemDao.updateItemOrder(cqItem.getCq().getId(), cqItem, oldParent == null ? null : oldParent.getId(), newParent == null ? null : newParent.getId(), oldOrder, newOrder, oldLevel, newLevel);

	}

	public Integer getCountOfRfaCqByEventId(String eventId) {
		return rfaCqDao.getCountOfRfaCqByEventId(eventId);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteAllCqs(String eventId, String eventRequirement) {
		rfaEnvelopDao.removeCqsFromEnvelops(eventId);
		rfaCqDao.deleteCqByEventId(eventId);

		RfaEvent event = rfaEventDao.findById(eventId);
		event.setCqCompleted(Boolean.FALSE);
		event.setQuestionnaires(Boolean.FALSE);
		rfaEventDao.update(event);

	}

	@Override
	public RfaCqItem findRfaCqItemForCqByEventId(String eventId) {
		RfaCqItem item = rfaCqItemDao.getCqItemByEventId(eventId);
		if (item != null && CollectionUtil.isNotEmpty(item.getCqOptions())) {
			for (RfaCqOption option : item.getCqOptions()) {
				option.getValue();
			}
		}
		return item;
	}

	@Override
	public RfaEvent getRfaEventById(String eventId) {
		return rfaEventDao.findByEventId(eventId);
	}

	@Override
	public boolean isExists(RfaCq rfaCq) {
		LOG.info("Checking for duplicate CQ with name : " + rfaCq.getName() + " event Id : " + rfaCq.getRfxEvent().getId() + "  id : " + rfaCq.getId());
		return rfaCqDao.isExists(rfaCq, rfaCq.getRfxEvent().getId());
	}

	@Override
	public boolean isExists(RfaCqItem rfaCqItem, String cq, String parent) {
		LOG.info("Checking for duplicate CQ Item with name : " + rfaCqItem.getItemName() + " CQ Id : " + cq + "  Parent : " + parent);
		return rfaCqItemDao.isExists(rfaCqItem, cq, parent);
	}

	@Override
	public List<RfaCqItem> getAllCqitemsbyCqId(String cqId) {
		List<RfaCqItem> itemList = rfaCqItemDao.getAllCqItemsbycqId(cqId);
		for (RfaCqItem rfaCqItem : itemList) {
			for (RfaCqOption cqOption : rfaCqItem.getCqOptions()) {
				cqOption.getValue();
			}
		}
		return itemList;
	}

	@Override
	public RfaCqItem getParentbyLevelId(String cqId, Integer level) {
		return rfaCqItemDao.getParentbyLevelId(cqId, level);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
	public String doExcelDataSave(Map<Integer, Map<Integer, RfaCqItem>> dataMap, String eventId, String cqId) throws Exception {

		LOG.info("dataMap  " + dataMap.size());
		RfaCq cq = getRfaCqById(cqId);
		RfaEvent event = getRfaEventById(eventId);

		for (RfaCqItem item : cq.getCqItems()) {
			if (item.getOrder() == 0) {
				LOG.info("Deleting CQ Item : " + item.toLogString());
				item.setCq(null);
				rfaCqItemDao.delete(item);
			}
		}
		if (cq.getCqItems() != null) {
			cq.getCqItems().clear();
			rfaCqDao.update(cq);
		}

		// checking item exists validation
		int levelTemp = 1;
		int orderTemp = 0;
		boolean isItemExists = false;
		String message = null;
		int rowNum = 2;
		for (Map.Entry<Integer, Map<Integer, RfaCqItem>> entry : dataMap.entrySet()) {
			RfaCqItem parent = null;
			for (Map.Entry<Integer, RfaCqItem> cqItem : entry.getValue().entrySet()) {
				// if(parent != null) {
				cqItem.getValue().setParent(parent);
				// }
				if (cqItem.getValue().getOrder() == 0) {
					parent = cqItem.getValue();
				} else {
					cqItem.getValue().setCqOptions(new ArrayList<>());
					int index = 1;
					for (String option : cqItem.getValue().getOptions()) {
						RfaCqOption rOption = new RfaCqOption();
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
						rOption.setRfaCqItem(cqItem.getValue());
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
				rfaCqItemDao.save(cqItem.getValue());
				rowNum++;
			}
		}
		if (isItemExists) {
			message = messageSource.getMessage("common.upload.warning", new Object[] {}, Global.LOCALE);
		}
		return message;
	}

	@Override
	public List<RfaCqItem> findCqItemsForCq(String cqId) {
		List<RfaCqItem> returnList = new ArrayList<RfaCqItem>();
		List<RfaCqItem> list = rfaCqItemDao.getCqItemsbyId(cqId);
		bulidCqItemList(returnList, list);
		return returnList;
	}

	@Override
	public int CountAllMandatoryCqByEventId(String eventId) {
		return rfaCqItemDao.CountCqItemsbyEventId(eventId);
	}

	@Override
	public List<String> getNotSectionAddedRfaCqIdsByEventId(String eventId) {
		return rfaCqDao.getNotSectionAddedRfaCqIdsByEventId(eventId);
	}

	@Override
	public String getLeadEvaluatorComment(String itemId) {
		return rfaCqItemDao.getLeadEvaluatorComment(itemId);
	}

	@Override
	public List<String> getNotSectionItemAddedRfaCqIdsByEventId(String eventId) {
		return rfaCqDao.getNotSectionItemAddedRfaCqIdsByEventId(eventId);
	}

	@Override
	public List<RfaCq> findRfaCqForEventByEnvelopeId(String eventId, String envelopeId) {
		List<RfaCq> cqs = rfaCqDao.findCqsForEventEnvelopeId(eventId, envelopeId);
		if (CollectionUtil.isNotEmpty(cqs)) {
			for (RfaCq cq : cqs) {
				if (CollectionUtil.isNotEmpty(cq.getCqItems())) {
					for (RfaCqItem item : cq.getCqItems()) {
						if (CollectionUtil.isEmpty(item.getCqOptions())) {
							for (RfaCqOption option : item.getCqOptions()) {
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
	public List<RfaCq> findRfaCqForEventByEnvelopeId(List<String> cqid, String id) {
		List<RfaCq> cqs = rfaCqDao.findCqsForEventEnvelopeId(cqid, id);
		if (CollectionUtil.isNotEmpty(cqs)) {
			for (RfaCq cq : cqs) {
				if (CollectionUtil.isNotEmpty(cq.getCqItems())) {
					for (RfaCqItem item : cq.getCqItems()) {
						if (CollectionUtil.isEmpty(item.getCqOptions())) {
							for (RfaCqOption option : item.getCqOptions()) {
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
		return rfaCqDao.findEventForCqPojoByEventId(eventId);
	}

	@Override
	public List<RfaCq> findRfaCqForEventByOrder(String eventId) {
		List<RfaCq> cqs = rfaCqDao.findCqsForEventByOrder(eventId);
		if (CollectionUtil.isNotEmpty(cqs)) {
			for (RfaCq cq : cqs) {
				if (CollectionUtil.isNotEmpty(cq.getCqItems())) {
					for (RfaCqItem item : cq.getCqItems()) {
						if (CollectionUtil.isEmpty(item.getCqOptions())) {
							for (RfaCqOption option : item.getCqOptions()) {
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
	@Transactional(readOnly = false)
	public RfaCq updatCq(RfaCq cq) {
		return rfaCqDao.update(cq);
	}

}

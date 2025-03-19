package com.privasia.procurehere.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.SourcingFormCqDao;
import com.privasia.procurehere.core.dao.SourcingFormCqItemDao;
import com.privasia.procurehere.core.entity.SourcingFormCqOption;
import com.privasia.procurehere.core.entity.SourcingFormRequestCqItem;
import com.privasia.procurehere.core.entity.SourcingFormTemplate;
import com.privasia.procurehere.core.entity.SourcingTemplateCq;
import com.privasia.procurehere.core.entity.SourcingTemplateCqItem;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.CqItemPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.service.SourcingFormCqService;

/**
 * @author sarang
 */
@Service
@Transactional(readOnly = true)
public class SourcingFormCqServiceImpl implements SourcingFormCqService {

	private static final Logger LOG = LogManager.getLogger(Global.BUYER_LOG);

	@Autowired
	SourcingFormCqDao sourcingFormCqDao;

	@Autowired
	SourcingFormCqItemDao sourcingFormCqItemDao;

	@Override
	public boolean isCqExists(String id, String name) {
		return sourcingFormCqDao.isCqExists(id, name);
	}

	@Transactional(readOnly = false)
	@Override
	public SourcingTemplateCq updateSourcingFormCq(SourcingTemplateCq sfcq) {
		return sourcingFormCqDao.saveOrUpdate(sfcq);
	}

	@Transactional(readOnly = false)
	@Override
	public SourcingTemplateCq saveSourcingFormCq(SourcingTemplateCq sourcingFormCq) {
		return sourcingFormCqDao.save(sourcingFormCq);
	}

	@Override
	public SourcingTemplateCq getSourcingFormCq(String cqId) {
		return sourcingFormCqDao.getSourcingFormCq(cqId);
	}

	@Override
	public List<SourcingTemplateCqItem> findCqItembyCqId(String cqId) {

		List<SourcingTemplateCqItem> list = sourcingFormCqDao.findCqItembyCqId(cqId);
		for (SourcingTemplateCqItem sourcingFormCqItem : list) {
			sourcingFormCqItem.setCq(null);
			sourcingFormCqItem.setParent(null);
			sourcingFormCqItem.setCqOptions(null);
			sourcingFormCqItem.setSourcingForm(null);
			if (CollectionUtil.isNotEmpty(sourcingFormCqItem.getChildren())) {
				for (SourcingTemplateCqItem cqItem : sourcingFormCqItem.getChildren()) {
					cqItem.setParent(null);
					cqItem.setCq(null);
					cqItem.setCqOptions(null);
					cqItem.setSourcingForm(null);
				}

			}
		}
		return list;
	}

	@Override
	public List<SourcingTemplateCqItem> findSourcingTemplateCqItembyCqId(String cqId) {

		List<SourcingTemplateCqItem> list = sourcingFormCqDao.findCqItembyCqId(cqId);
		for (SourcingTemplateCqItem sourcingFormCqItem : list) {
			sourcingFormCqItem.setCq(null);
			sourcingFormCqItem.setParent(null);
			sourcingFormCqItem.setCqOptions(null);
			sourcingFormCqItem.setSourcingForm(null);
			if (CollectionUtil.isNotEmpty(sourcingFormCqItem.getChildren())) {
				for (SourcingTemplateCqItem cqItem : sourcingFormCqItem.getChildren()) {
					cqItem.setParent(null);
					cqItem.setCq(null);
					cqItem.setCqOptions(null);
					cqItem.setSourcingForm(null);
					cqItem.setChildren(null);
				}

			}

		}
		return list;
	}

	@Override
	public SourcingFormTemplate getSourcingForm(String cqId) {

		return sourcingFormCqDao.getSourcingForm(cqId);
	}

	@Override
	public boolean isCqItemExists(String cqId, String name) {
		return sourcingFormCqDao.isCqItemExists(cqId, name);
	}

	@Override
	public SourcingTemplateCqItem getCqItembyCqItemId(String checkString) {

		return sourcingFormCqDao.getCqItembyCqItemId(checkString);
	}

	@Transactional(readOnly = false)
	@Override
	public SourcingTemplateCqItem saveCqItem(SourcingTemplateCqItem cqItem) {

		if (cqItem.getParent() == null) {
			int itemLevel = 0;
			List<SourcingTemplateCqItem> list = sourcingFormCqItemDao.getCqItemLevelOrder(cqItem.getCq().getId());
			if (CollectionUtil.isNotEmpty(list)) {
				itemLevel = list.size();
			}
			cqItem.setLevel(itemLevel + 1);
			cqItem.setOrder(0);
		} else {
			LOG.info("PARENT : " + cqItem.getParent().getId());
			// RftCqItem parent = rftCqItemDao.getCqItembyCqId(rftCqItem.getParent().getId());
			if (cqItem.getParent() != null) {
				cqItem.setLevel(cqItem.getParent().getLevel());
				cqItem.setOrder(CollectionUtil.isEmpty(cqItem.getParent().getChildren()) ? 1 : cqItem.getParent().getChildren().size() + 1);
			}
		}

		LOG.info("saveCqItem method executing ");
		return sourcingFormCqItemDao.save(cqItem);

	}

	@Override
	public SourcingTemplateCqItem getCqItembyCqId(String cqId) {

		return null;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
	public SourcingTemplateCqItem getSourcingCqItembyItemId(String itemId) {
		SourcingTemplateCqItem sourcingFormCqItem = sourcingFormCqItemDao.getCqItembyItemId(itemId);
		if (sourcingFormCqItem != null && CollectionUtil.isNotEmpty(sourcingFormCqItem.getCqOptions())) {
			for (SourcingFormCqOption option : sourcingFormCqItem.getCqOptions()) {
				option.getValue();
			}
		}
		return sourcingFormCqItem;
	}

	@Override
	@Transactional(readOnly = false)
	public SourcingTemplateCqItem updateCqItem(SourcingTemplateCqItem cqItem) {
		return sourcingFormCqItemDao.saveOrUpdate(cqItem);

	}

	@Override
	public List<SourcingTemplateCqItem> findAllCqItembyCqId(String cqId) {
		List<SourcingTemplateCqItem> list = sourcingFormCqDao.findLoadedCqItembyCqId(cqId);

		// List<SourcingTemplateCqItem> returnList = new ArrayList<SourcingTemplateCqItem>();

		// bulidCqItemList(returnList, list);

		for (SourcingTemplateCqItem sourcingFormCqItem : list) {

			sourcingFormCqItem.setCq(null);
			sourcingFormCqItem.setParent(null);
			sourcingFormCqItem.setCqOptions(null);
			sourcingFormCqItem.setSourcingForm(null);
			if (CollectionUtil.isNotEmpty(sourcingFormCqItem.getChildren())) {
				for (SourcingTemplateCqItem cqItem : sourcingFormCqItem.getChildren()) {
					cqItem.setParent(null);
					cqItem.setCq(null);
					cqItem.setCqOptions(null);
					cqItem.setSourcingForm(null);
					cqItem.setChildren(null);
				}

			}
		}

		return list;
	}

	/*
	 * private void bulidCqItemList(List<SourcingTemplateCqItem> returnList, List<SourcingTemplateCqItem> list) { if
	 * (CollectionUtil.isNotEmpty(list)) { for (SourcingTemplateCqItem item : list) { SourcingTemplateCqItem parent =
	 * item.createShallowCopy(); returnList.add(parent); if (CollectionUtil.isNotEmpty(item.getChildren())) { for
	 * (SourcingTemplateCqItem child : item.getChildren()) { if (parent.getChildren() == null) { parent.setChildren(new
	 * ArrayList<SourcingTemplateCqItem>()); } parent.getChildren().add(child.createShallowCopy()); } } } } }
	 */

	@Override
	public List<SourcingFormRequestCqItem> getSourcingFormRequestCqItemByFormId(String id) {
		return sourcingFormCqItemDao.getSourcingFormRequestCqItemByFormId(id);
	}

	@Override
	@Transactional(readOnly = false)

	public void deleteCq(SourcingTemplateCq cq) {
		sourcingFormCqDao.deleteCqById(cq.getId());
	}

	public void deleteCqItems(String[] items, String cqId) throws NotAllowedException {
		sourcingFormCqItemDao.deleteCqItemss(items, cqId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<SourcingTemplateCqItem> findCqbyCqId(String cqId) {
		List<SourcingTemplateCqItem> returnList = new ArrayList<SourcingTemplateCqItem>();
		List<SourcingTemplateCqItem> cqItemList = sourcingFormCqItemDao.getAllSourcingCqItemsbycqId(cqId);
		LOG.info(cqItemList.size());
		bulidCqItemList(returnList, cqItemList);
		return returnList;
	}

	private void bulidCqItemList(List<SourcingTemplateCqItem> returnList, List<SourcingTemplateCqItem> cqItemList) {
		if (cqItemList != null) {
			for (SourcingTemplateCqItem sourcingTemplateCqItem : cqItemList) {
				SourcingTemplateCqItem parent = sourcingTemplateCqItem.createShallowCopy();
				returnList.add(parent);
				if (CollectionUtil.isNotEmpty(sourcingTemplateCqItem.getChildren())) {
					for (SourcingTemplateCqItem child : sourcingTemplateCqItem.getChildren()) {
						if (parent.getChildren() == null) {
							parent.setChildren(new ArrayList<SourcingTemplateCqItem>());
						}
						parent.getChildren().add(child.createShallowCopy());
					}
				}
			}
		}
	}

	@Override
	public boolean isExistsItem(SourcingTemplateCqItem item, String cqid, String parentId) {
		return sourcingFormCqItemDao.isExistsItem(item, cqid, parentId);
	}

	@Override
	public void reorderCqItems(CqItemPojo formCqItemPojo) throws NotAllowedException {

		LOG.info("CQ ITEM Object :: " + formCqItemPojo.toString());
		int newOrder = formCqItemPojo.getOrder();
		SourcingTemplateCqItem cqItem = getCqItembyCqItemId(formCqItemPojo.getId());

		if (CollectionUtil.isNotEmpty(cqItem.getChildren()) && formCqItemPojo.getParent() != null) {
			throw new NotAllowedException("CQ Item cannot be made a child if it has sub items");
		}

		LOG.info("DB CQ ITEM DETAILS ::" + cqItem.toString());
		int oldOrder = cqItem.getOrder();
		LOG.info("Item Old Position :: " + oldOrder + " :: Item New Position :: " + newOrder);
		int oldLevel = cqItem.getLevel();
		int newLevel = formCqItemPojo.getOrder(); // this will be ignored if it
													// is made a child
		SourcingTemplateCqItem newParent = null;
		if (formCqItemPojo.getParent() != null) {
			newParent = getCqItembyCqItemId(formCqItemPojo.getParent());
		}
		SourcingTemplateCqItem oldParent = cqItem.getParent();

		// If these are not child, their order should be reset to 0
		if (oldParent == null) {
			oldOrder = 0;
		}
		if (newParent == null) {
			newOrder = 0;
		}

		// Update it to new position.
		cqItem.setOrder(newOrder);
		cqItem.setLevel(newParent == null ? formCqItemPojo.getOrder() : newParent.getLevel());
		cqItem.setParent(newParent);

		sourcingFormCqItemDao.updateRequestItemOrder(cqItem.getCq().getId(), cqItem, oldParent == null ? null : oldParent.getId(), newParent == null ? null : newParent.getId(), oldOrder, newOrder, oldLevel, newLevel);

	}

	@Override
	public List<String> getNotSectionItemAddedCqIdsByFormId(String formId) {
		return sourcingFormCqDao.getNotSectionItemAddedCqIdsByFormId(formId);
	}

}

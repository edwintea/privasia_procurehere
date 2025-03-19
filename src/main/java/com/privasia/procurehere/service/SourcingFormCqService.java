package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.SourcingFormRequestCqItem;
import com.privasia.procurehere.core.entity.SourcingFormTemplate;
import com.privasia.procurehere.core.entity.SourcingTemplateCq;
import com.privasia.procurehere.core.entity.SourcingTemplateCqItem;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.CqItemPojo;

/**
 * @author sarang
 */

public interface SourcingFormCqService {

	/**
	 * @param name
	 * @param string
	 * @return boolean
	 */
	public boolean isCqExists(String name, String string);

	/**
	 * @param sfcq
	 * @return
	 */
	public SourcingTemplateCq updateSourcingFormCq(SourcingTemplateCq sfcq);

	/**
	 * @param sourcingFormCq
	 * @return
	 */
	public SourcingTemplateCq saveSourcingFormCq(SourcingTemplateCq sourcingFormCq);

	/**
	 * @param cqId
	 * @return
	 */
	public SourcingTemplateCq getSourcingFormCq(String cqId);

	/**
	 * @param cqId
	 * @return
	 */
	public List<SourcingTemplateCqItem> findCqItembyCqId(String cqId);

	public SourcingFormTemplate getSourcingForm(String cqId);

	public boolean isCqItemExists(String name, String string);

	public SourcingTemplateCqItem getCqItembyCqItemId(String checkString);

	public SourcingTemplateCqItem saveCqItem(SourcingTemplateCqItem cqItem);

	public SourcingTemplateCqItem getCqItembyCqId(String checkString);

	/**
	 * @param id
	 * @return
	 */
	public SourcingTemplateCqItem getSourcingCqItembyItemId(String itemId);

	public SourcingTemplateCqItem updateCqItem(SourcingTemplateCqItem cqItem);

	public List<SourcingTemplateCqItem> findAllCqItembyCqId(String cqId);

	public List<SourcingFormRequestCqItem> getSourcingFormRequestCqItemByFormId(String id);

	public void deleteCq(SourcingTemplateCq cq);

	public void deleteCqItems(String[] items, String cqId) throws NotAllowedException;

	public List<SourcingTemplateCqItem> findCqbyCqId(String cqId);

	public boolean isExistsItem(SourcingTemplateCqItem item, String cq, String parent);

	public void reorderCqItems(CqItemPojo formCqItemPojo) throws NotAllowedException;

	List<SourcingTemplateCqItem> findSourcingTemplateCqItembyCqId(String cqId);

	public List<String> getNotSectionItemAddedCqIdsByFormId(String formId);

}

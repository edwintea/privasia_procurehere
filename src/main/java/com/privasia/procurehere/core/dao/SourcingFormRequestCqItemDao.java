package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.SourcingTemplateCqItem;
import com.privasia.procurehere.core.entity.SourcingFormRequestCqItem;
import com.privasia.procurehere.core.entity.SourcingFormRequestCqOption;

/**
 * @author pooja
 */
public interface SourcingFormRequestCqItemDao extends GenericDao<SourcingFormRequestCqItem, String> {

	/**
	 * @param cqId
	 * @param TemplateId
	 * @return
	 */
	List<SourcingTemplateCqItem> getCqItemsByCqIdAndTempId(String cqId, String TemplateId);

	/**
	 * @param formId
	 * @param cqId
	 * @return
	 */
	SourcingFormRequestCqItem findCqByFormIdAndCqItemId(String formId, String cqItemId);

	/**
	 * @param cqId
	 * @param formId
	 * @return
	 */
	List<SourcingFormRequestCqItem> findAllCqItemByCqId(String cqId, String formId);

	/**
	 * @param requestId
	 * @return
	 */
	List<SourcingFormRequestCqItem> getAllSourcingCqItemByRequestId(String requestId);

	/**
	 * @param id
	 * @return
	 */
	List<SourcingFormRequestCqOption> getListAnswers(String id);

	/**
	 * @param itemId
	 * @param formId
	 * @return
	 */
	boolean resetAttachement(String itemId, String formId);

	List<String> getListTextAnswers(String id);

	SourcingFormRequestCqOption getCqOptionsByCqItemId(String id);

	List<SourcingFormRequestCqItem> findAllCqItemByCqId(String cqId);

	long getListTextAnswersByItemId(String id);

	/**
	 * @param cqItemId
	 * @param requestFormId
	 * @return
	 */
	List<SourcingFormRequestCqItem> findRequestCqItemByFromCqItemId(String cqItemId, String requestFormId);

}

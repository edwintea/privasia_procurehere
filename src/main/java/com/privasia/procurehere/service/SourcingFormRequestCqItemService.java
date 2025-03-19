package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.SourcingFormCqOption;
import com.privasia.procurehere.core.entity.SourcingFormRequest;
import com.privasia.procurehere.core.entity.SourcingFormRequestCqItem;
import com.privasia.procurehere.core.entity.SourcingFormRequestCqOption;
import com.privasia.procurehere.core.entity.SourcingTemplateCq;
import com.privasia.procurehere.core.pojo.SourcingFormReqCqItem;

/**
 * @author pooja
 */

public interface SourcingFormRequestCqItemService {

	/**
	 * @param templateId
	 * @return
	 */
	List<SourcingTemplateCq> findCqsByTempId(String templateId);

	/**
	 * @param cqId
	 * @param sourcingFormRequest
	 */
	void saveSourcingRequestCq(String cqId, SourcingFormRequest sourcingFormRequest);

	/**
	 * @param cqId
	 * @param formId
	 * @return
	 */
	List<SourcingFormRequestCqItem> getAllSourcingCqItemByCqId(String cqId, String formId);

	/**
	 * @param formId
	 * @param id
	 * @return
	 */
	SourcingFormRequestCqItem findCqBySourcingReqIdAndCqItemId(String formId, String itemId);

	/**
	 * @param list
	 */
	void updateSourcingCqItem(List<SourcingFormRequestCqItem> itemsList);

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

	/**
	 * @param formId
	 * @return
	 */
	List<SourcingTemplateCq> getAllQuestionnarieByOrder(String formId);

	/**
	 * @param cqItemOption
	 * @param cqItemId
	 * @return
	 */
	List<SourcingFormCqOption> findCqItemOptionForCqItemId(String cqItemOption, String cqItemId);

	List<SourcingFormCqOption> findCqItemOptionForCqItemId(String cqItemId, SourcingFormReqCqItem itemObj);

}

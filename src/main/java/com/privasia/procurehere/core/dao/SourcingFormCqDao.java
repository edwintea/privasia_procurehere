package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.SourcingFormTemplate;
import com.privasia.procurehere.core.entity.SourcingTemplateCq;
import com.privasia.procurehere.core.entity.SourcingTemplateCqItem;

/**
 * @author sarang
 */
public interface SourcingFormCqDao extends GenericCqDao<SourcingTemplateCq, String> {

	/**
	 * @param name
	 * @param name2
	 * @return
	 */
	boolean isCqExists(String name, String name2);

	/**
	 * @param cqId
	 * @return
	 */
	SourcingTemplateCq getSourcingFormCq(String cqId);

	/**
	 * @param cqId
	 * @return
	 */
	List<SourcingTemplateCqItem> findCqItembyCqId(String cqId);

	/**
	 * @param cqId
	 * @return
	 */
	SourcingFormTemplate getSourcingForm(String cqId);

	/**
	 * @param name
	 * @param string
	 * @return
	 */
	boolean isCqItemExists(String name, String string);

	/**
	 * @param checkString
	 * @return
	 */
	SourcingTemplateCqItem getCqItembyCqItemId(String checkString);

	/**
	 * @param templateId
	 * @return
	 */
	List<SourcingTemplateCq> getSourcingFormCqByFormId(String templateId);

	List<SourcingTemplateCqItem> findAllCqItembyCqId(String cqId);

	List<SourcingTemplateCqItem> findLoadedCqItembyCqId(String cqId);

	List<String> getNotSectionItemAddedCqIdsByFormId(String formId);

	/**
	 * @param formId
	 * @return
	 */
	List<SourcingTemplateCq> getAllQuestionnarieByOrder(String formId);

}

package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.SourcingFormCqOption;

/**
 * @author sarang
 */
public interface SourcingFormCqOptionDao extends GenericDao<SourcingFormCqOption, String> {

	/**
	 * @param searchValue
	 * @param cqItemId
	 * @return
	 */
	List<SourcingFormCqOption> findCqItemOptionForCqItemId(String searchValue, String cqItemId);

	/**
	 * @param cqItemId
	 * @return
	 */
	long getCountOfAllOptionsForCqItem(String cqItemId);

	List<SourcingFormCqOption> findCqItemOptionForCqItemId(String cqItemId);

	/**
	 * @param templtCqItemId
	 * @param order
	 * @return
	 */
	SourcingFormCqOption findByTemplateCqItemIdAndOptionOrder(String templtCqItemId, Integer order);


}

package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfpCqOption;

/**
 * @author Ravi
 */
public interface RfpCqOptionDao extends GenericDao<RfpCqOption, String> {

	/**
	 * 
	 * @param cq
	 * @return
	 */
	List<RfpCqOption> findOptionsByCqItem(String cq);

}

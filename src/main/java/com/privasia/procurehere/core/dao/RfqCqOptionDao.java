package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfqCqOption;

/**
 * @author Ravi
 */
public interface RfqCqOptionDao extends GenericDao<RfqCqOption, String> {

	/**
	 * 
	 * @param cq
	 * @return
	 */
	List<RfqCqOption> findOptionsByCqItem(String cq);

}

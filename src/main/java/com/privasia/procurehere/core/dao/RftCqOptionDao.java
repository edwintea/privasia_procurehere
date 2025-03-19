package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RftCqOption;

/**
 * @author Ravi
 */
public interface RftCqOptionDao extends GenericDao<RftCqOption, String> {

	/**
	 * @param cq
	 * @return
	 */
	List<RftCqOption> findOptionsByCqItem(String cq);

}

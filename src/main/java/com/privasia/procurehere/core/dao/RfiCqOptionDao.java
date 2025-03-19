package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfiCqOption;

/**
 * @author Ravi
 */
public interface RfiCqOptionDao extends GenericDao<RfiCqOption, String> {

	/**
	 * 
	 * @param cq
	 * @return
	 */
	List<RfiCqOption> findOptionsByCqItem(String cq);

}

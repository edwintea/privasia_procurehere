package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfaCqOption;

/**
 * @author RT-Kapil
 */
public interface RfaCqOptionDao extends GenericDao<RfaCqOption, String> {

	/**
	 * @param cq
	 * @return
	 */
	List<RfaCqOption> findOptionsByCqItem(String cq);

}

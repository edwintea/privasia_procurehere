package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfiEventCorrespondenceAddress;

/**
 * @author Ravi
 */
public interface RfiEventCorrespondenceAddressDao extends GenericDao<RfiEventCorrespondenceAddress, String> {

	/**
	 * @param id
	 * @return
	 */
	List<RfiEventCorrespondenceAddress> findAllEventCAddressById(String id);

}

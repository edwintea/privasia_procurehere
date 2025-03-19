package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfqEventCorrespondenceAddress;

public interface RfqEventCorrespondenceAddressDao extends GenericDao<RfqEventCorrespondenceAddress, String> {

	/**
	 * 
	 * @param id
	 * @return
	 */
	List<RfqEventCorrespondenceAddress> findAllEventCAddressById(String id);

}

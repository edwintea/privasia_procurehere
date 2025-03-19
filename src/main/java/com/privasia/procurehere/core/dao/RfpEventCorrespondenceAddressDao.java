package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfpEventCorrespondenceAddress;

public interface RfpEventCorrespondenceAddressDao extends GenericDao<RfpEventCorrespondenceAddress, String> {

	/**
	 * 
	 * @param id
	 * @return
	 */
	List<RfpEventCorrespondenceAddress> findAllEventCAddressById(String id);

}

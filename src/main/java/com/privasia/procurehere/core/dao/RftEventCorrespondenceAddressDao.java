package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RftEventCorrespondenceAddress;

public interface RftEventCorrespondenceAddressDao extends GenericDao<RftEventCorrespondenceAddress, String> {

	List<RftEventCorrespondenceAddress> findAllEventCAddressById(String id);

}

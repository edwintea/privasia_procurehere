package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfaEventCorrespondenceAddress;

public interface RfaEventCorrespondenceAddressDao extends GenericDao<RfaEventCorrespondenceAddress, String> {

	List<RfaEventCorrespondenceAddress> findAllEventCAddressById(String id);

}

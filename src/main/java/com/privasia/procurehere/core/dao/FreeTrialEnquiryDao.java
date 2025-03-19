package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.FreeTrialEnquiry;

public interface FreeTrialEnquiryDao extends GenericDao<FreeTrialEnquiry, String> {

	/**
	 * @return
	 */
	List<FreeTrialEnquiry> findFreeTrialForNotification();

}

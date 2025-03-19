package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.FreeTrialEnquiry;

public interface FreeTrialEnquiryService {

	/**
	 * @param freeTrialInquiry
	 * @return
	 */
	FreeTrialEnquiry savefreeTrialEnquiry(FreeTrialEnquiry freeTrialInquiry);

	/**
	 * @param id
	 * @return
	 */
	FreeTrialEnquiry findById(String id);

	/**
	 * @param freeTrialInquiry
	 * @return
	 */
	FreeTrialEnquiry updateFreeTrialEnquiry(FreeTrialEnquiry freeTrialInquiry);

	/**
	 * 
	 * @return
	 */
	List<FreeTrialEnquiry> findFreeTrialForNotification();
}

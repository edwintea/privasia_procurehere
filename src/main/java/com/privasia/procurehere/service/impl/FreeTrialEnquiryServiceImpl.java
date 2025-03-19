package com.privasia.procurehere.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.FreeTrialEnquiryDao;
import com.privasia.procurehere.core.entity.FreeTrialEnquiry;
import com.privasia.procurehere.service.FreeTrialEnquiryService;

@Service
@Transactional(readOnly = true)
public class FreeTrialEnquiryServiceImpl implements FreeTrialEnquiryService {

	@Autowired
	FreeTrialEnquiryDao freeTrialEnquiryDao;

	@Override
	@Transactional(readOnly = false)
	public FreeTrialEnquiry savefreeTrialEnquiry(FreeTrialEnquiry freeTrialInquiry) {
		return freeTrialEnquiryDao.save(freeTrialInquiry);
	}

	@Override
	@Transactional(readOnly = false)
	public FreeTrialEnquiry updateFreeTrialEnquiry(FreeTrialEnquiry freeTrialInquiry) {
		return freeTrialEnquiryDao.update(freeTrialInquiry);
	}

	@Override
	public FreeTrialEnquiry findById(String id) {
		return freeTrialEnquiryDao.findById(id);
	}

	@Override
	public List<FreeTrialEnquiry> findFreeTrialForNotification() {
		return freeTrialEnquiryDao.findFreeTrialForNotification();
	}

}

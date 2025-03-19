package com.privasia.procurehere.core.dao.impl;

import java.util.Calendar;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.FreeTrialEnquiryDao;
import com.privasia.procurehere.core.entity.FreeTrialEnquiry;

/**
 * @author Yogesh
 */
@Repository
public class FreeTrialInquiryDaoImpl extends GenericDaoImpl<FreeTrialEnquiry, String> implements FreeTrialEnquiryDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<FreeTrialEnquiry> findFreeTrialForNotification() {
		Calendar now = Calendar.getInstance();
		now.add(Calendar.HOUR, -24);
		final Query query = getEntityManager().createQuery("select distinct fte from FreeTrialEnquiry fte where fte.createdDate < :createdDate and fte.signupDate is null and fte.followupEmailDate is null and fte.followupEmailSent = :followupEmailSent");
		query.setParameter("createdDate", now.getTime());
		query.setParameter("followupEmailSent", Boolean.FALSE);
		return query.getResultList();
	}
}

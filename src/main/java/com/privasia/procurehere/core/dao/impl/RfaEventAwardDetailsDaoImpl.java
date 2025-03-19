package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfaEventAwardDetailsDao;
import com.privasia.procurehere.core.entity.RfaEventAwardDetails;
import com.privasia.procurehere.core.utils.CollectionUtil;

@Repository
public class RfaEventAwardDetailsDaoImpl extends GenericDaoImpl<RfaEventAwardDetails, String> implements RfaEventAwardDetailsDao {

	@Override
	@SuppressWarnings("unchecked")
	public RfaEventAwardDetails rfaEventAwardByEventIdandBqId(String awardId, String bqId) {
		final Query query = getEntityManager().createQuery("select a from RfaEventAwardDetails a  left outer join a.supplier s where a.eventAward.id =:awardId and a.bqItem.id =:bqId");
		query.setParameter("awardId", awardId);
		query.setParameter("bqId", bqId);
		List<RfaEventAwardDetails> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

}

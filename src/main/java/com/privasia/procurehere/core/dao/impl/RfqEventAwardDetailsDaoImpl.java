package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfqEventAwardDetailsDao;
import com.privasia.procurehere.core.entity.RfqEventAwardDetails;
import com.privasia.procurehere.core.utils.CollectionUtil;

@Repository
public class RfqEventAwardDetailsDaoImpl extends GenericDaoImpl<RfqEventAwardDetails, String> implements RfqEventAwardDetailsDao {

	@Override
	@SuppressWarnings("unchecked")
	public RfqEventAwardDetails rfqEventAwardByEventIdandBqId(String awardId, String bqId) {
		final Query query = getEntityManager().createQuery("select a from RfqEventAwardDetails a  left outer join a.supplier s where a.eventAward.id =:awardId and a.bqItem.id =:bqId");
		query.setParameter("awardId", awardId);
		query.setParameter("bqId", bqId);
		List<RfqEventAwardDetails> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

}

package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfpEventAwardDetailsDao;
import com.privasia.procurehere.core.entity.RfpEventAwardDetails;
import com.privasia.procurehere.core.utils.CollectionUtil;

@Repository
public class RfpEventAwardDetailsDaoImpl extends GenericDaoImpl<RfpEventAwardDetails, String> implements RfpEventAwardDetailsDao {

	@Override
	@SuppressWarnings("unchecked")
	public RfpEventAwardDetails rfpEventAwardByEventIdandBqId(String awardId, String bqId) {
		final Query query = getEntityManager().createQuery("select a from RfpEventAwardDetails a  left outer join a.supplier s where a.eventAward.id =:awardId and a.bqItem.id =:bqId");
		query.setParameter("awardId", awardId);
		query.setParameter("bqId", bqId);
		List<RfpEventAwardDetails> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

}

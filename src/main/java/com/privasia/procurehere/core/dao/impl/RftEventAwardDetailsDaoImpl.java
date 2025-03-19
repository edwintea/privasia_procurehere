package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RftEventAwardDetailsDao;
import com.privasia.procurehere.core.entity.RftEventAwardDetails;
import com.privasia.procurehere.core.utils.CollectionUtil;

@Repository
public class RftEventAwardDetailsDaoImpl extends GenericDaoImpl<RftEventAwardDetails, String> implements RftEventAwardDetailsDao {

	@Override
	@SuppressWarnings("unchecked")
	public RftEventAwardDetails rftEventAwardByEventIdandBqId(String awardId, String bqId) {
		final Query query = getEntityManager().createQuery("select a from RftEventAwardDetails a  left outer join a.supplier s where a.eventAward.id =:awardId and a.bqItem.id =:bqId");
		query.setParameter("awardId", awardId);
		query.setParameter("bqId", bqId);
		List<RftEventAwardDetails> uList = query.getResultList();
		if (CollectionUtil.isNotEmpty(uList)) {
			return uList.get(0);
		} else {
			return null;
		}
	}

}

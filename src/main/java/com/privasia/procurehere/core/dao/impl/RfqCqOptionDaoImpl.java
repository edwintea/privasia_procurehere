package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfqCqOptionDao;
import com.privasia.procurehere.core.entity.RfqCqOption;

/**
 * @author Ravi
 */
@Repository
public class RfqCqOptionDaoImpl extends GenericDaoImpl<RfqCqOption, String> implements RfqCqOptionDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<RfqCqOption> findOptionsByCqItem(String cq){
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.RfqCqOption(a.id, a.value) from RfqCqOption a inner join a.cqItem cq where cq.id =:id");
		query.setParameter("id", cq);
		return query.getResultList();
	}
}

package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfiCqOptionDao;
import com.privasia.procurehere.core.entity.RfiCqOption;

/**
 * @author Ravi
 */
@Repository
public class RfiCqOptionDaoImpl extends GenericDaoImpl<RfiCqOption, String> implements RfiCqOptionDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<RfiCqOption> findOptionsByCqItem(String cq){
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.RfiCqOption(a.id, a.value) from RfiCqOption a inner join a.cqItem cq where cq.id =:id");
		query.setParameter("id", cq);
		return query.getResultList();
	}
}

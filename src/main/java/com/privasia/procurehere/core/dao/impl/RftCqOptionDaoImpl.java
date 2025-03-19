package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RftCqOptionDao;
import com.privasia.procurehere.core.entity.RftCqOption;

/**
 * @author Ravi
 */
@Repository
public class RftCqOptionDaoImpl extends GenericDaoImpl<RftCqOption, String> implements RftCqOptionDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<RftCqOption> findOptionsByCqItem(String cq){
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.RftCqOption(a.id, a.value) from RftCqOption a inner join a.rftCqItem cq where cq.id =:id");
		query.setParameter("id", cq);
		return query.getResultList();
	}

}

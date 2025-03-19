package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfpCqOptionDao;
import com.privasia.procurehere.core.entity.RfpCqOption;

/**
 * @author Ravi
 */
@Repository
public class RfpCqOptionDaoImpl extends GenericDaoImpl<RfpCqOption, String> implements RfpCqOptionDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<RfpCqOption> findOptionsByCqItem(String cq){
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.RfpCqOption(a.id, a.value) from RfpCqOption a inner join a.rfpCqItem cq where cq.id =:id");
		query.setParameter("id", cq);
		return query.getResultList();
	}

}

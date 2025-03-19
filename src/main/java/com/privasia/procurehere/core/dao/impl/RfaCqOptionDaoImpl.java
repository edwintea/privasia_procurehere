package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfaCqOptionDao;
import com.privasia.procurehere.core.entity.RfaCqOption;

/**
 * @author RT-Kapil
 */
@Repository
public class RfaCqOptionDaoImpl extends GenericDaoImpl<RfaCqOption, String> implements RfaCqOptionDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaCqOption> findOptionsByCqItem(String cq){
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.RfaCqOption(a.id, a.value) from RfaCqOption a inner join a.rfaCqItem cq where cq.id =:id");
		query.setParameter("id", cq);
		return query.getResultList();
	}
}

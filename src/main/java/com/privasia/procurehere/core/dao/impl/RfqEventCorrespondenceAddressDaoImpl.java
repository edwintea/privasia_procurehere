package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfqEventCorrespondenceAddressDao;
import com.privasia.procurehere.core.entity.RfqEventCorrespondenceAddress;

@Repository
public class RfqEventCorrespondenceAddressDaoImpl extends GenericDaoImpl<RfqEventCorrespondenceAddress, String> implements RfqEventCorrespondenceAddressDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<RfqEventCorrespondenceAddress> findAllEventCAddressById(String id) {
		final Query query = getEntityManager().createQuery("from RfqEventCorrespondenceAddress reca inner join fetch reca.state where reca.rfxEvent.id = :id");
		query.setParameter("id", id);
		return query.getResultList();
	}

}

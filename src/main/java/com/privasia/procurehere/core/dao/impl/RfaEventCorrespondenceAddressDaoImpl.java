package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfaEventCorrespondenceAddressDao;
import com.privasia.procurehere.core.entity.RfaEventCorrespondenceAddress;

@Repository
public class RfaEventCorrespondenceAddressDaoImpl extends GenericDaoImpl<RfaEventCorrespondenceAddress, String> implements RfaEventCorrespondenceAddressDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<RfaEventCorrespondenceAddress> findAllEventCAddressById(String id) {
		final Query query = getEntityManager().createQuery("from RfaEventCorrespondenceAddress reca inner join fetch reca.state where reca.rfaEvent.id = :id");
		query.setParameter("id", id);
		return query.getResultList();
	}

}

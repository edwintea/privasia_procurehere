package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfpEventCorrespondenceAddressDao;
import com.privasia.procurehere.core.entity.RfpEventCorrespondenceAddress;

@Repository
public class RfpEventCorrespondenceAddressDaoImpl extends GenericDaoImpl<RfpEventCorrespondenceAddress, String> implements RfpEventCorrespondenceAddressDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<RfpEventCorrespondenceAddress> findAllEventCAddressById(String id) {
		final Query query = getEntityManager().createQuery("from RfpEventCorrespondenceAddress reca inner join fetch reca.state where reca.rfxEvent.id = :id");
		query.setParameter("id", id);
		return query.getResultList();
	}

}

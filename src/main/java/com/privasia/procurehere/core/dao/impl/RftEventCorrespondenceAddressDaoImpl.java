package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RftEventCorrespondenceAddressDao;
import com.privasia.procurehere.core.entity.RftEventCorrespondenceAddress;

@Repository
public class RftEventCorrespondenceAddressDaoImpl extends GenericDaoImpl<RftEventCorrespondenceAddress, String> implements RftEventCorrespondenceAddressDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<RftEventCorrespondenceAddress> findAllEventCAddressById(String id) {
		final Query query = getEntityManager().createQuery("from RftEventCorrespondenceAddress reca inner join fetch reca.state where reca.rfxEvent.id = :id");
		query.setParameter("id", id);
		return query.getResultList();
	}

}

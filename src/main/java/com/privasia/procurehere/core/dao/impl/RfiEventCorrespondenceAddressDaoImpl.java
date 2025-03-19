package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.RfiEventCorrespondenceAddressDao;
import com.privasia.procurehere.core.entity.RfiEventCorrespondenceAddress;

@Repository
public class RfiEventCorrespondenceAddressDaoImpl extends GenericDaoImpl<RfiEventCorrespondenceAddress, String> implements RfiEventCorrespondenceAddressDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<RfiEventCorrespondenceAddress> findAllEventCAddressById(String id) {
		final Query query = getEntityManager().createQuery("from RfiEventCorrespondenceAddress reca inner join fetch reca.state where reca.rfxEvent.id = :id");
		query.setParameter("id", id);
		return query.getResultList();
	}

}

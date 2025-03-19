package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.ContractLoaAndAgreementDao;
import com.privasia.procurehere.core.entity.ContractLoaAndAgreement;
import com.privasia.procurehere.core.utils.CollectionUtil;

/**
 * @author anshul
 */
@Repository
public class ContractLoaAndAgreementDaoImpl extends GenericDaoImpl<ContractLoaAndAgreement, String> implements ContractLoaAndAgreementDao {
	private static final Logger LOG = LogManager.getLogger(ContractLoaAndAgreementDaoImpl.class);

	@SuppressWarnings("unchecked")
	@Override
	public ContractLoaAndAgreement findContractLoaAndAgreementByContractId(String id) {
		final Query query = getEntityManager().createQuery("from ContractLoaAndAgreement cla inner join fetch cla.productContract pc left outer join fetch cla.loaUploadedBy left outer join fetch cla.agreementUploadedBy where pc.id =:id");
		query.setParameter("id", id);
		
		List<ContractLoaAndAgreement> contractLoaAndAgreement = query.getResultList();
		if (CollectionUtil.isNotEmpty(contractLoaAndAgreement)) {
			return contractLoaAndAgreement.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ContractLoaAndAgreement> findPlainContractLoaAndAgreementByContractId(String productContractId) {
		LOG.info("Contract Id " + productContractId);
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.ContractLoaAndAgreement(a.id, a.loaDescription, a.loaFileName, a.loaContentType, a.loaFileSizeInKb, a.loaDate, a.agreementDescription, a.agreementFileName, a.agreementContentType, a.agreementFileSizeInKb, a.agreementDate, lub.name, aub.name,a.loaUploadDate,a.agreementUploadDate) from ContractLoaAndAgreement a inner join a.productContract pc left outer join a.loaUploadedBy lub left outer join a.agreementUploadedBy aub where pc.id = :id");
		query.setParameter("id", productContractId);
		return query.getResultList();

	}

	@Override
	public ContractLoaAndAgreement findLoaAndAgreeDocsByDocId(String id) {
		final Query query = getEntityManager().createQuery("from ContractLoaAndAgreement c  where c.id =:id");
		query.setParameter("id", id);
		return (ContractLoaAndAgreement) query.getSingleResult();
	}

	
}

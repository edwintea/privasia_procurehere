package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.ContractDocumentDao;
import com.privasia.procurehere.core.entity.ContractDocument;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class ContractDocumentDaoImpl extends GenericDaoImpl<ContractDocument, String> implements ContractDocumentDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<ContractDocument> findAllContractdocsbyContractId(String productContractId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.ContractDocument(a.id, a.fileName, a.contentType, sp, a.fileSizeInKb, a.uploadDate, a.description, ub) from ContractDocument a inner join a.productContract sp left outer join a.uploadedBy ub where sp.id =:id order by a.fileName");
		query.setParameter("id", productContractId);
		return query.getResultList();
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteById(String id) {
		final Query query = getEntityManager().createQuery("delete from ContractDocument a where a.id =:id");
		query.setParameter("id", id);
		query.executeUpdate();
	}
	

	@Override
	public ContractDocument findContractDocsById(String id) {
		final Query query = getEntityManager().createQuery("from ContractDocument c  where c.id =:id");
		query.setParameter("id", id);
		return (ContractDocument) query.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ContractDocument> findDocumentsById(String documentId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.ContractDocument(a.id, a.fileName, a.contentType, sp, a.fileSizeInKb, a.uploadDate, a.description) from ContractDocument a where a.id = :id");
		query.setParameter("id", documentId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ContractDocument> findProductContractByIdForDocument(String contractId) {
		final Query query = getEntityManager().createQuery("select new com.privasia.procurehere.core.entity.ContractDocument(a.id, a.fileName, a.contentType, sp, a.fileSizeInKb, a.uploadDate, a.description) from ContractDocument a where sp.id =:id order by a.fileName");
		query.setParameter("id", contractId);
		return query.getResultList();
	}
	
}

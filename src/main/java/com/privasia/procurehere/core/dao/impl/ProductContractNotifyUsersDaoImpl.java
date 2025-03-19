package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.ProductContractNotifyUsersDao;
import com.privasia.procurehere.core.entity.ProductContractNotifyUsers;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;

/**
 * @author pooja
 */
@Repository
public class ProductContractNotifyUsersDaoImpl extends GenericDaoImpl<ProductContractNotifyUsers, String> implements ProductContractNotifyUsersDao {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	@SuppressWarnings("unchecked")
	@Override
	public List<ProductContractNotifyUsers> getAllContractNotifyUsersByContractId(String contractId) {
		LOG.info("Getting contract notify user list");
		final Query query = getEntityManager().createQuery("from ProductContractNotifyUsers r where r.productContract.id = :contractId");
		query.setParameter("contractId", contractId);
		List<ProductContractNotifyUsers> notifyUserList = query.getResultList();
		if (CollectionUtil.isNotEmpty(notifyUserList)) {
			return notifyUserList;
		} else {
			return null;
		}
	}

}
package com.privasia.procurehere.core.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.FinanceCompanyDao;
import com.privasia.procurehere.core.entity.FinanceCompany;
import com.privasia.procurehere.core.enums.FinanceCompanyStatus;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;

@Repository
public class FinanceCompanyDaoImpl extends GenericDaoImpl<FinanceCompany, String> implements FinanceCompanyDao {

	private static final Logger LOG = LogManager.getLogger(Global.FINANCE_COMPANY_LOG);

	@Override
	public FinanceCompany getFinanceCompanyById(String id) {
		final Query query = getEntityManager().createQuery("from FinanceCompany b left outer join fetch b.registrationOfCountry rc left outer join fetch b.state st left outer join fetch b.companyStatus cs where b.id =:id");
		query.setParameter("id", id);
		return (FinanceCompany) query.getSingleResult();
	}

	@Override
	public boolean isExists(FinanceCompany financeCompany) {
		StringBuilder hsql = new StringBuilder("from FinanceCompany a where upper(a.companyRegistrationNumber) = :regNumber and a.registrationOfCountry = :registrationOfCountry");
		if (StringUtils.checkString(financeCompany.getId()).length() > 0) {
			hsql.append(" and a.id <> :id");
		}
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("regNumber", financeCompany.getCompanyRegistrationNumber().toUpperCase());
		query.setParameter("registrationOfCountry", financeCompany.getRegistrationOfCountry());
		if (StringUtils.checkString(financeCompany.getId()).length() > 0) {
			query.setParameter("id", financeCompany.getId());
		}
		return CollectionUtil.isNotEmpty(query.getResultList());
	}

	@Override
	public boolean isExistsLoginEmail(String loginEmail) {
		Query query = getEntityManager().createQuery("from User a where  upper(a.loginId) = :loginEmail ");
		query.setParameter("loginEmail", loginEmail.toUpperCase());
		return CollectionUtil.isNotEmpty(query.getResultList());

	}

	@Override
	public boolean isExistsRegistrationNumber(FinanceCompany financeCompany) {
		final Query query = getEntityManager().createQuery("from FinanceCompany a where a.companyRegistrationNumber = :companyRegistrationNumber and a.registrationOfCountry = :registrationOfCountry ");
		query.setParameter("companyRegistrationNumber", financeCompany.getCompanyRegistrationNumber().toUpperCase());
		query.setParameter("registrationOfCountry", financeCompany.getRegistrationOfCountry());
		return CollectionUtil.isNotEmpty(query.getResultList());
	}

	@Override
	public boolean isExistsCompanyName(FinanceCompany financeCompany) {
		final Query query = getEntityManager().createQuery("from FinanceCompany a where upper(a.companyName) = :companyName and a.registrationOfCountry = :registrationOfCountry ");
		query.setParameter("companyName", financeCompany.getCompanyName().toUpperCase());
		query.setParameter("registrationOfCountry", financeCompany.getRegistrationOfCountry());
		return CollectionUtil.isNotEmpty(query.getResultList());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FinanceCompany> searchFinanceCompany(String status, String order, String globalSearch) {
		LOG.info("searchFinanceCompany Search enter....." + status);
		StringBuilder hql = new StringBuilder(" from FinanceCompany a  left outer join fetch  a.registrationOfCountry rc  where 1 = 1 ");
		if (StringUtils.checkString(status).length() > 0 && FinanceCompanyStatus.ALL != FinanceCompanyStatus.valueOf(StringUtils.checkString(status).toUpperCase())) {
			hql.append("and a.status =:status ");
		}
		if (StringUtils.checkString(globalSearch).length() > 0) {
			hql.append("and ( upper(a.companyName) like :companyName or upper(a.fullName) like :fullName or upper(a.companyRegistrationNumber) like :companyRegistrationNumber)");
		}

		if (StringUtils.checkString(order).equals("Newest")) {
			hql.append("order by a.registrationCompleteDate desc");
		} else {
			hql.append("order by a.registrationCompleteDate ");
		}
		LOG.info(hql.toString());
		final Query query = getEntityManager().createQuery(hql.toString());
		if (StringUtils.checkString(status).length() > 0 && FinanceCompanyStatus.ALL != FinanceCompanyStatus.valueOf(StringUtils.checkString(status).toUpperCase())) {
			query.setParameter("status", FinanceCompanyStatus.valueOf(StringUtils.checkString(status).toUpperCase()));
		}
		if (StringUtils.checkString(globalSearch).length() > 0) {
			query.setParameter("companyName", "%" + globalSearch.toUpperCase() + "%");
			query.setParameter("fullName", "%" + globalSearch.toUpperCase() + "%");
			query.setParameter("companyRegistrationNumber", "%" + globalSearch.toUpperCase() + "%");

		}
		return query.getResultList();
	}

}

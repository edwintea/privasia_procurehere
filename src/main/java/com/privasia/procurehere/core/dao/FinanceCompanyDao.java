package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.FinanceCompany;

public interface FinanceCompanyDao extends GenericDao<FinanceCompany, String> {

	FinanceCompany getFinanceCompanyById(String id);

	boolean isExists(FinanceCompany financeCompany);

	boolean isExistsLoginEmail(String loginEmail);

	boolean isExistsRegistrationNumber(FinanceCompany financeCompany);

	boolean isExistsCompanyName(FinanceCompany financeCompany);

	List<FinanceCompany> searchFinanceCompany(String status, String order, String globalSearch);

}

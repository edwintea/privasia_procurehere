package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.FinanceCompany;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.exceptions.ApplicationException;

public interface FinanceCompanyService {


	FinanceCompany getFinanceCompanyById(String id);

	User saveFinanceCompany(FinanceCompany financeCompany, String loginUserId);

	FinanceCompany updateFinanceCompany(FinanceCompany financeCompany);

	boolean isExists(FinanceCompany bean);

	boolean isExistsLoginEmail(String loginEmail);

	boolean isExistsRegistrationNumber(FinanceCompany financeCompany);

	boolean isExistsCompanyName(FinanceCompany financeCompany);

	void sentFinanaceCompanyCreationMail(FinanceCompany financeCompany, User user) throws ApplicationException;

	List<FinanceCompany> searchFinanceCompany(String status, String order, String globalSerch);

}

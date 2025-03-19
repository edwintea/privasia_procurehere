package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.CompanyStatus;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.pojo.TableDataInput;

public interface CompanyStatusDao extends GenericDao<CompanyStatus, String> {

	/**
	 * @return
	 */
	List<CompanyStatus> findAllCompanyStatus();

	/**
	 * @param companyStatus
	 * @return
	 */
	boolean isExists(CompanyStatus companyStatus);

	/**
	 * @param list
	 * @param createdBy
	 */
	void loadCompanyStatusMasterData(List<CompanyStatus> list, User createdBy);

	/**
	 * @param tableParams
	 * @return
	 */
	List<CompanyStatus> findAllCompanyStatusList(TableDataInput tableParams);

	/**
	 * @param tableParams
	 * @return
	 */
	long findTotalFilteredCompanyStatusList(TableDataInput tableParams);

	/**
	 * @return
	 */
	long findTotalCompanyStatusList();

}

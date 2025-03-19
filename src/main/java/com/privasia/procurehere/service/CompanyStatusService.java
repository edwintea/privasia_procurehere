package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.CompanyStatus;
import com.privasia.procurehere.core.pojo.CompanyStatusPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;


public interface CompanyStatusService {
	
	
	/**
	 * Create CompanyStatus
	 * @return 
	 */
	public String createCompanyStatus(CompanyStatus companyStatus);
	
	/**
	 * Update CompanyStatus
	 */
	public void updateCompanyStatus(CompanyStatus companyStatus);
	
	/**
	 * Delete CompanyStatus
	 */
	public void deleteCompanyStatus(CompanyStatus companyStatus);
	
	/**
	 * @return
	 */
	List<CompanyStatus> getAllComapnyStatus();
	
	/**
	 * @param id
	 * 
	 */
	CompanyStatus getCompanyStatusById(String id);
    /**
     * 
     * @param companyStatus
     * @return
     */
	public boolean isExists(CompanyStatus companyStatus);
    /**
     * 
     * @return
     */
	List<CompanyStatusPojo> getAllComapnyStatusPojo();
    /**
     * 
     * @param tableParams
     * @return
     */
	public List<CompanyStatus> findAllCompanyStatusList(TableDataInput tableParams);
    /**
     * 
     * @param tableParams
     * @return
     */
	public long findTotalFilteredCompanyStatusList(TableDataInput tableParams);
    /**
     * 
     * @return
     */
	public long findTotalCompanyStatusList();

	
}

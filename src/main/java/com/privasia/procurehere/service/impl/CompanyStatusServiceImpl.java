package com.privasia.procurehere.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.CompanyStatusDao;
import com.privasia.procurehere.core.dao.OwnerAuditTrailDao;
import com.privasia.procurehere.core.entity.CompanyStatus;
import com.privasia.procurehere.core.entity.OwnerAuditTrail;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.pojo.CompanyStatusPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.service.CompanyStatusService;

@Service
@Transactional(readOnly = true)
public class CompanyStatusServiceImpl implements CompanyStatusService {

	Logger LOG = LogManager.getLogger(CompanyStatusServiceImpl.class);

	@Autowired
	CompanyStatusDao companyStatusDao;

	@Autowired
	OwnerAuditTrailDao ownerAuditTrailDao;
	
	@Override
	@Transactional(readOnly = false)
	public String createCompanyStatus(CompanyStatus companyStatus) {
		OwnerAuditTrail ownerAuditTrail = new OwnerAuditTrail(AuditTypes.CREATE, "'"+companyStatus.getCompanystatus()+"' Company Type settings created ", companyStatus.getCreatedBy().getTenantId(), companyStatus.getCreatedBy(), new Date(),ModuleType.CompanyStatus);
		ownerAuditTrailDao.save(ownerAuditTrail);	
		companyStatus = companyStatusDao.saveOrUpdate(companyStatus);
		return (companyStatus != null ? companyStatus.getId() : null);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateCompanyStatus(CompanyStatus companyStatus) {
		OwnerAuditTrail ownerAuditTrail = new OwnerAuditTrail(AuditTypes.UPDATE, "'"+companyStatus.getCompanystatus()+"' Company Type settings updated ", companyStatus.getModifiedBy().getTenantId(), companyStatus.getModifiedBy(), new Date(),ModuleType.CompanyStatus);
		ownerAuditTrailDao.save(ownerAuditTrail);			
		companyStatusDao.update(companyStatus);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteCompanyStatus(CompanyStatus companyStatus) {
		String comStatus=companyStatus.getCompanystatus();
		OwnerAuditTrail ownerAuditTrail = new OwnerAuditTrail(AuditTypes.DELETE,"'"+comStatus+"' Company Type settings deleted ",companyStatus.getModifiedBy().getTenantId(), companyStatus.getModifiedBy(), new Date(),ModuleType.CompanyStatus);
		ownerAuditTrailDao.save(ownerAuditTrail);			
		companyStatusDao.delete(companyStatus);
	}

	@Override
	@Transactional(readOnly = false)
	public List<CompanyStatus> getAllComapnyStatus() {
		return companyStatusDao.findAllCompanyStatus();
	}

	@Override
	public CompanyStatus getCompanyStatusById(String id) {

		return companyStatusDao.findById(id);
	}

	@Override
	public boolean isExists(CompanyStatus companyStatus) {

		return companyStatusDao.isExists(companyStatus);
	}

	@Override
	@Transactional(readOnly = false)
	public List<CompanyStatusPojo> getAllComapnyStatusPojo() {

		List<CompanyStatusPojo> returnList = new ArrayList<CompanyStatusPojo>();
		List<CompanyStatus> list = companyStatusDao.findAllCompanyStatus();
		if (CollectionUtil.isNotEmpty(list)) {

			for (CompanyStatus companyStatus : list) {
				if (companyStatus.getCreatedBy() != null)
					companyStatus.getCreatedBy().getLoginId();
				if (companyStatus.getModifiedBy() != null)
					companyStatus.getModifiedBy().getLoginId();

				CompanyStatusPojo cp = new CompanyStatusPojo(companyStatus);

				returnList.add(cp);
			}
		}

		return returnList;
	}

	@Override
	public List<CompanyStatus> findAllCompanyStatusList(TableDataInput tableParams) {
		return companyStatusDao.findAllCompanyStatusList(tableParams);
	}

	@Override
	public long findTotalFilteredCompanyStatusList(TableDataInput tableParams) {
		return companyStatusDao.findTotalFilteredCompanyStatusList(tableParams);
	}

	@Override
	public long findTotalCompanyStatusList() {
		return companyStatusDao.findTotalCompanyStatusList();
	}

}

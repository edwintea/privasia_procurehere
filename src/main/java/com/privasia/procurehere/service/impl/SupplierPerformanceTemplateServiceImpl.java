/**
 * 
 */
package com.privasia.procurehere.service.impl;

import java.util.Date;
import java.util.List;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.BuyerAuditTrailDao;
import com.privasia.procurehere.core.dao.SupplierPerformanceTemplateCriteriaDao;
import com.privasia.procurehere.core.dao.SupplierPerformanceTemplateDao;
import com.privasia.procurehere.core.entity.BuyerAuditTrail;
import com.privasia.procurehere.core.entity.SupplierPerformanceTemplate;
import com.privasia.procurehere.core.entity.SupplierPerformanceTemplateCriteria;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.enums.SourcingStatus;
import com.privasia.procurehere.core.enums.TenantType;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.service.BuyerService;
import com.privasia.procurehere.service.SupplierPerformanceTemplateService;
import com.privasia.procurehere.service.UserService;

/**
 * @author Jayshree
 *
 */
@Service
@Transactional(readOnly = true)
public class SupplierPerformanceTemplateServiceImpl implements SupplierPerformanceTemplateService {
	
	private static final Logger LOG = LogManager.getLogger(SupplierPerformanceTemplateServiceImpl.class);
	
	@Autowired
	SupplierPerformanceTemplateDao supplierPerformanceTemplateDao;
	
	@Autowired
	BuyerAuditTrailDao buyerAuditTrailDao;
	
	@Autowired
	BuyerService buyerService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	SupplierPerformanceTemplateCriteriaDao supplierPerformanceTemplateCriteriaDao;
	
	@Override
	public SupplierPerformanceTemplate getSupplierPerformanceTemplatebyId(String templateId) {
		LOG.info("Supplier Performance Template Id................" + templateId);
		return supplierPerformanceTemplateDao.findById(templateId);
	}
	
	@Override
	@Transactional(readOnly = false)
	public SupplierPerformanceTemplate saveSupplierPerformanceTemplate(SupplierPerformanceTemplate spTemplate) {
		LOG.info(">>>>> Sve SP TEMPL ........................ ");
		try {
			try {
				BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "Supplier Performance Template '"+spTemplate.getTemplateName()+ "' is created", spTemplate.getCreatedBy().getTenantId(), spTemplate.getCreatedBy(), new Date(), ModuleType.SupplierPerformanceTemplate);
				buyerAuditTrailDao.save(ownerAuditTrail);
			} catch (Exception e) {
				LOG.info("Error to create audit trails message"+e.getMessage(), e);
			}
			spTemplate = supplierPerformanceTemplateDao.save(spTemplate);
		}catch(Exception e) {
			LOG.info("Error while saving SP Template "+e.getMessage(), e);
		}
		return spTemplate;
	}
	
	@Override
	@Transactional(readOnly = false)
	public SupplierPerformanceTemplate saveOrUpdateSupplierPerformanceTemplate(SupplierPerformanceTemplate persistObject) {
		return supplierPerformanceTemplateDao.saveOrUpdate(persistObject);
	}
	
	@Override
	public boolean isTemplateExistsByTemplateName(String templateName, String tenantId) {
		return supplierPerformanceTemplateDao.isTemplateExistsByTemplateName(templateName, tenantId);
	}

	@Override
	@Transactional(readOnly = false)
	public SupplierPerformanceTemplate updateSupplierPerformanceTemplate(SupplierPerformanceTemplate spTemplate) {
		return supplierPerformanceTemplateDao.saveOrUpdate(spTemplate);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateSupplierPerformanceTemplateStatusAndComplete(String templateId, Boolean performanceCriteriaCompleted, SourcingStatus status) {
		supplierPerformanceTemplateDao.updateSupplierPerformanceTemplateStatusAndComplete(templateId, performanceCriteriaCompleted, status);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteUsersForSPTemplate(String templateId) {
		supplierPerformanceTemplateDao.deleteUsersForSPTemplate(templateId);
	}

	@Override
	public SupplierPerformanceTemplate getSupplierPerformanceTemplateById(String templateId) {
		SupplierPerformanceTemplate template = supplierPerformanceTemplateDao.findById(templateId);
		if(template != null) {
			if(template.getProcurementCategory() != null) {
				template.getProcurementCategory().getId();
				template.getProcurementCategory().getProcurementCategories();
				template.getProcurementCategory().getDescription();
			}
		}
		return template;
	}

	@Override
	public List<SupplierPerformanceTemplate> findSPTemplateForTenant(String loggedInUserTenantId, TableDataInput input, TenantType buyer) {
		List<SupplierPerformanceTemplate> list = supplierPerformanceTemplateDao.findSPTemplateForTenant(loggedInUserTenantId, input, buyer);
		if(CollectionUtil.isNotEmpty(list)) {
			for(SupplierPerformanceTemplate spTemplate : list) {
				if (CollectionUtil.isNotEmpty(spTemplate.getCriteria())) {
					for (SupplierPerformanceTemplateCriteria criteria : spTemplate.getCriteria()) {
						criteria.getName();
					}
				}
			}
		}
		return list;
	}

	@Override
	public long findTotalFilteredSPTemplateForTenant(String loggedInUserTenantId, TableDataInput input, TenantType buyer) {
		return supplierPerformanceTemplateDao.findTotalFilteredSPTemplateForTenant(loggedInUserTenantId, input, buyer);
	}

	@Override
	public long findTotalActiveSPTemplateForTenant(String loggedInUserTenantId, TenantType buyer) {
		return supplierPerformanceTemplateDao.findTotalActiveSPTemplateForTenant(loggedInUserTenantId, buyer);
	}

	@Override
	public List<String> getUserIdListByTemplateId(String templateId) {
		return supplierPerformanceTemplateDao.getUserIdListByTemplateId(templateId);
	}
	
	@Override
	public List<SupplierPerformanceTemplate> getAllAssignedSpTemplateIdsForUser(String userId) {
		return supplierPerformanceTemplateDao.getAllAssignedSpTemplateIdsForUser(userId);
	}

	@Override
	public boolean isExists(String templateId, String templateName) {
		return supplierPerformanceTemplateDao.isExists(templateId, templateName);
	}

	@Transactional(readOnly = false)	
	@Override
	public SupplierPerformanceTemplate copyTemplate(String templateId, SupplierPerformanceTemplate newTemplate, User user) {

		SupplierPerformanceTemplate oldTemplate = supplierPerformanceTemplateDao.findById(templateId);
		LOG.info("SF : " + oldTemplate.getId());
		if (CollectionUtil.isNotEmpty(oldTemplate.getCriteria())) {
			for (SupplierPerformanceTemplateCriteria criteria : oldTemplate.getCriteria()) {
				criteria.getName();
				criteria.getTemplate();
				criteria.getParent();
				for(SupplierPerformanceTemplateCriteria child : criteria.getChildren()) {
					child.getName();
					child.getMaximumScore();
					child.getDescription();
					criteria.getTemplate();
					criteria.getParent();
				}
			}
		}
		newTemplate.setMaximumScore(oldTemplate.getMaximumScore());
		newTemplate.setProcurementCategory(oldTemplate.getProcurementCategory());
		newTemplate.setProcurementCategoryDisabled(oldTemplate.getProcurementCategoryDisabled());
		newTemplate.setProcurementCategoryOptional(oldTemplate.getProcurementCategoryOptional());
		newTemplate.setProcurementCategoryVisible(oldTemplate.getProcurementCategoryVisible());
		newTemplate.setStatus(SourcingStatus.DRAFT);
		newTemplate.setBuyer(oldTemplate.getBuyer());
		newTemplate.setDetailCompleted(false);
		newTemplate.setPerformanceCriteriaCompleted(false);
		newTemplate.setCreatedBy(user);
		newTemplate.setCreatedDate(new Date());
		
		newTemplate = supplierPerformanceTemplateDao.save(newTemplate);

		newTemplate.copyCriteriaDetails(oldTemplate, newTemplate);

		if (CollectionUtil.isNotEmpty(newTemplate.getCriteria())) {
			SupplierPerformanceTemplateCriteria parent = null;
			for (SupplierPerformanceTemplateCriteria criteria : newTemplate.getCriteria()) {
				criteria.setTemplate(newTemplate);
				if (criteria.getOrder() == 0) {
					parent = criteria;
				}
				if (criteria.getOrder() != 0) {
					criteria.setParent(parent);
				}
			}
		}

		try {
			BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.CREATE, "Supplier Performance Template '"+newTemplate.getTemplateName()+ "' is created", newTemplate.getCreatedBy().getTenantId(), newTemplate.getCreatedBy(), new Date(), ModuleType.SupplierPerformanceTemplate);
			buyerAuditTrailDao.save(ownerAuditTrail);
		} catch (Exception e) {
			LOG.info("Error to create audit trails message");
		}
		newTemplate = supplierPerformanceTemplateDao.update(newTemplate);

		List<String> assignedUserIds = supplierPerformanceTemplateDao.getUserIdListByTemplateId(oldTemplate.getId());
		if (CollectionUtil.isNotEmpty(assignedUserIds)) {
			for (String assgnedUserId : assignedUserIds) {
				User userObj = userService.getUsersForSupplierPerformanceTemplateById(assgnedUserId);
				List<SupplierPerformanceTemplate> assignedTemplateList = userObj.getAssignedSupplierPerformanceTemplates();
				assignedTemplateList.add(newTemplate);
				userObj.setAssignedSupplierPerformanceTemplates(assignedTemplateList);
				userService.updateUser(userObj);
			}
		}

		return newTemplate;
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteSupplierPerformanceTemplate(SupplierPerformanceTemplate spTemplate, User loggedInUser) {
		try {
			BuyerAuditTrail ownerAuditTrail = new BuyerAuditTrail(AuditTypes.DELETE, "	Supplier Performance Template '"+spTemplate.getTemplateName()+ "' is deleted", loggedInUser.getTenantId(), loggedInUser, new Date(), ModuleType.SupplierPerformanceTemplate);
			buyerAuditTrailDao.save(ownerAuditTrail);
		} catch (Exception e) {
			LOG.info("Error to create audit trails message");
		}
		supplierPerformanceTemplateDao.delete(spTemplate);

	}

}

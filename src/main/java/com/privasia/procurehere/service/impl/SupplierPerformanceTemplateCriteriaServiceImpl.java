/**
 * 
 */
package com.privasia.procurehere.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.SupplierPerformanceTemplateCriteriaDao;
import com.privasia.procurehere.core.dao.SupplierPerformanceTemplateDao;
import com.privasia.procurehere.core.entity.SupplierPerformanceTemplateCriteria;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.SupplierPerformanceTemplateCriteriaPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.service.SupplierPerformanceTemplateCriteriaService;

/**
 * @author Jayshree
 */
@Service
@Transactional(readOnly = true)
public class SupplierPerformanceTemplateCriteriaServiceImpl implements SupplierPerformanceTemplateCriteriaService {

	private static final Logger LOG = LogManager.getLogger(SupplierPerformanceTemplateCriteriaServiceImpl.class);

	@Autowired
	SupplierPerformanceTemplateCriteriaDao supplierPerformanceTemplateCriteriaDao;

	@Autowired
	SupplierPerformanceTemplateDao supplierPerformanceTemplateDao;

	@Override
	public List<SupplierPerformanceTemplateCriteria> getAllCriteriaByOrder(String templateId) {
		return supplierPerformanceTemplateCriteriaDao.getAllCriteriaByOrder(templateId);
	}

	@Override
	public boolean isCriteriaExistsByName(SupplierPerformanceTemplateCriteria criteria) {
		LOG.info("Checking Criteria Exists By Name");
		return supplierPerformanceTemplateCriteriaDao.isCriteriaExistsByName(criteria);
	}

	@Override
	@Transactional(readOnly = false)
	public SupplierPerformanceTemplateCriteria saveSupplierPerformanceTemplateCriteria(SupplierPerformanceTemplateCriteria criteria) {

		if (criteria.getParent() == null) {
			int itemLevel = 0;
			List<SupplierPerformanceTemplateCriteria> list = supplierPerformanceTemplateCriteriaDao.getCriteriaLevelOrder(criteria.getTemplate().getId());
			if (CollectionUtil.isNotEmpty(list)) {
				itemLevel = list.size();
			}
			criteria.setLevel(itemLevel + 1);
			criteria.setOrder(0);
		} else {
			LOG.info("PARENT : " + criteria.getParent().getId());
			if (criteria.getParent() != null) {
				SupplierPerformanceTemplateCriteria parent = supplierPerformanceTemplateCriteriaDao.findById(criteria.getParent().getId());
				criteria.setLevel(parent.getLevel());
				criteria.setOrder(CollectionUtil.isEmpty(parent.getChildren()) ? 1 : parent.getChildren().size() + 1);
			}
		}
		LOG.info("save Supplier Performance Template Criteria method executing ");
		criteria = supplierPerformanceTemplateCriteriaDao.save(criteria);

		return criteria;

	}

	@Override
	public List<SupplierPerformanceTemplateCriteria> findAllCriteriasByTemplateId(String templateId) {
		List<SupplierPerformanceTemplateCriteria> list = supplierPerformanceTemplateCriteriaDao.findAllCriteriasByTemplateId(templateId);
		for (SupplierPerformanceTemplateCriteria criteria : list) {
			criteria.setTemplate(null);
			criteria.setParent(null);
			if (CollectionUtil.isNotEmpty(criteria.getChildren())) {
				for (SupplierPerformanceTemplateCriteria cqItem : criteria.getChildren()) {
					cqItem.setParent(null);
					cqItem.setTemplate(null);
					cqItem.setChildren(null);
				}
			}
		}
		return list;
	}

	@Override
	public List<SupplierPerformanceTemplateCriteriaPojo> findAllCriteriaPojoByTemplateId(String templateId) {
		return supplierPerformanceTemplateCriteriaDao.findAllCriteriaPojoByTemplateId(templateId);
	}

	@Override
	public SupplierPerformanceTemplateCriteria getSPTCriteriaByCriteriaId(String criteriaId) {
		SupplierPerformanceTemplateCriteria criteria = supplierPerformanceTemplateCriteriaDao.findById(criteriaId);
		if (criteria != null && criteria.getParent() != null) {
			criteria.getParent().getId();
		}
		return criteria;
	}

	@Override
	public SupplierPerformanceTemplateCriteria getSPTCriteriaByCriteriaIdAndTemplateId(String templateId, String criteriaId) {
		return supplierPerformanceTemplateCriteriaDao.getSPTCriteriaByCriteriaIdAndTemplateId(templateId, criteriaId);
	}

	@Override
	@Transactional(readOnly = false)
	public SupplierPerformanceTemplateCriteria updateSupplierPerformanceTemplateCriteria(SupplierPerformanceTemplateCriteria criteria) {
		return supplierPerformanceTemplateCriteriaDao.saveOrUpdate(criteria);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = ApplicationException.class)
	public void deleteCriteriaByCreteriaId(String criteriaId, String templateId) {
		supplierPerformanceTemplateCriteriaDao.deleteCriteriaByCreteriaId(criteriaId, templateId);
	}

	@Override
	public long getCriteriaCount(String templateId) {
		return supplierPerformanceTemplateCriteriaDao.getCriteriaCount(templateId);
	}

	@Override
	public List<String> getSubCriteriaNotAddedCriteriaIdsByTemplateId(String templateId) {
		return supplierPerformanceTemplateCriteriaDao.getSubCriteriaNotAddedCriteriaIdsByTemplateId(templateId);
	}

	@Override
	public BigDecimal getSumOfWeightageOfAllCriteriaByTemplateId(String templateId, String criteriaId) {
		return supplierPerformanceTemplateCriteriaDao.getSumOfWeightageOfAllCriteriaByTemplateId(templateId, criteriaId);
	}

	@Override
	public BigDecimal getSumOfWeightageOfAllSubCriteriaForCriteriaByCriteriaId(String subCriteriaId, String criteriaId, String templateId) {
		return supplierPerformanceTemplateCriteriaDao.getSumOfWeightageOfAllSubCriteriaForCriteriaByCriteriaId(subCriteriaId, criteriaId, templateId);
	}

	@Override
	public boolean isCriteriaExists(String name, String templateId, String parentId) {
		// TODO Implement the checking
		return supplierPerformanceTemplateCriteriaDao.isCriteriaExistsByName(name, templateId, parentId);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, rollbackFor = NotAllowedException.class)
	public void reorderCriteria(SupplierPerformanceTemplateCriteriaPojo criteriaPojo) throws NotAllowedException {
		LOG.info("Template Criteria Object :: " + criteriaPojo.toString());
		int newOrder = criteriaPojo.getOrder();
		SupplierPerformanceTemplateCriteria criteria = getSPTCriteriaByCriteriaId(criteriaPojo.getId());

		if (CollectionUtil.isNotEmpty(criteria.getChildren()) && criteriaPojo.getParent() != null) {
			throw new NotAllowedException("Criteria Item cannot be made a child if it has sub items");
		}

		LOG.info("Criteria ITEM DETAILS ::" + criteria.toString());
		int oldOrder = criteria.getOrder();
		LOG.info("Item Old Position :: " + oldOrder + " :: Item New Position :: " + newOrder);
		int oldLevel = criteria.getLevel();
		int newLevel = criteriaPojo.getOrder(); // this will be ignored if it is made a child
		SupplierPerformanceTemplateCriteria newParent = null;
		if (criteriaPojo.getParent() != null) {
			newParent = getSPTCriteriaByCriteriaId(criteriaPojo.getParent());
		}
		SupplierPerformanceTemplateCriteria oldParent = criteria.getParent();

		// If these are not child, their order should be reset to 0
		if (oldParent == null) {
			oldOrder = 0;
		}
		if (newParent == null) {
			newOrder = 0;
		}

		// Update it to new position.
		criteria.setOrder(newOrder);
		criteria.setLevel(newParent == null ? criteriaPojo.getOrder() : newParent.getLevel());
		criteria.setParent(newParent);

		// Check total of sub-criteria weightage
		if (criteria.getParent() != null) {
			BigDecimal sumOfWeightageOfSubCriteria = getSumOfWeightageOfAllSubCriteriaForCriteriaByCriteriaId(criteria.getId(), criteria.getParent().getId(), criteria.getTemplate().getId());
			sumOfWeightageOfSubCriteria = sumOfWeightageOfSubCriteria.add(criteria.getWeightage());
			if (sumOfWeightageOfSubCriteria.compareTo(new BigDecimal(100)) > 0) {
				throw new NotAllowedException("Summation of Weightage of all Sub Criteria for a Criteria exceeds 100%");
			}

		}

		supplierPerformanceTemplateCriteriaDao.updateCriteriaOrder(criteria, criteria.getTemplate().getId(), (oldParent == null ? null : oldParent.getId()), (newParent == null ? null : newParent.getId()), oldOrder, newOrder, oldLevel, newLevel);
	}

}

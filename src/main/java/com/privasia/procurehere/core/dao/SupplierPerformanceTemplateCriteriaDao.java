/**
 * 
 */
package com.privasia.procurehere.core.dao;

import java.math.BigDecimal;
import java.util.List;

import com.privasia.procurehere.core.entity.SupplierPerformanceTemplateCriteria;
import com.privasia.procurehere.core.pojo.SupplierPerformanceTemplateCriteriaPojo;

/**
 * @author Jayshree
 */
public interface SupplierPerformanceTemplateCriteriaDao extends GenericDao<SupplierPerformanceTemplateCriteria, String> {

	/**
	 * @param templateId
	 * @return
	 */
	List<SupplierPerformanceTemplateCriteria> getAllCriteriaByOrder(String templateId);

	/**
	 * @param criteria TODO
	 * @return
	 */
	boolean isCriteriaExistsByName(SupplierPerformanceTemplateCriteria criteria);

	/**
	 * @param templateId
	 * @return
	 */
	List<SupplierPerformanceTemplateCriteria> getCriteriaLevelOrder(String templateId);

	/**
	 * @param templateId
	 * @return
	 */
	List<SupplierPerformanceTemplateCriteria> findAllCriteriasByTemplateId(String templateId);

	/**
	 * @param templateId
	 * @param criteriaId
	 * @return
	 */
	SupplierPerformanceTemplateCriteria getSPTCriteriaByCriteriaIdAndTemplateId(String templateId, String criteriaId);

	/**
	 * @param criteriaId
	 * @param templateId
	 * @return
	 */
	String deleteCriteriaByCreteriaId(String criteriaId, String templateId);

	/**
	 * @param templateId
	 * @return
	 */
	long getCriteriaCount(String templateId);

	/**
	 * @param templateId
	 * @return
	 */
	List<String> getSubCriteriaNotAddedCriteriaIdsByTemplateId(String templateId);

	/**
	 * @param templateId
	 * @param criteriaId
	 * @return
	 */
	BigDecimal getSumOfWeightageOfAllCriteriaByTemplateId(String templateId, String criteriaId);

	/**
	 * @param subCriteriaId
	 * @param criteriaId
	 * @param templateId
	 * @return
	 */
	BigDecimal getSumOfWeightageOfAllSubCriteriaForCriteriaByCriteriaId(String subCriteriaId, String criteriaId, String templateId);

	/**
	 * @param templateId
	 * @return
	 */
	List<SupplierPerformanceTemplateCriteriaPojo> findAllCriteriaPojoByTemplateId(String templateId);

	/**
	 * @param name
	 * @param templateId
	 * @param parentId
	 * @return
	 */
	boolean isCriteriaExistsByName(String name, String templateId, String parentId);

	/**
	 * @param criteria
	 * @param templateId
	 * @param oldParent
	 * @param newParent
	 * @param oldOrder
	 * @param newOrder
	 * @param oldLevel
	 * @param newLevel
	 */
	void updateCriteriaOrder(SupplierPerformanceTemplateCriteria criteria, String templateId, String oldParent, String newParent, int oldOrder, int newOrder, int oldLevel, int newLevel);

}

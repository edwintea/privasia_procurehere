/**
 * 
 */
package com.privasia.procurehere.service;

import java.math.BigDecimal;
import java.util.List;

import com.privasia.procurehere.core.entity.SupplierPerformanceTemplateCriteria;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.SupplierPerformanceTemplateCriteriaPojo;

/**
 * @author Jayshree
 */
public interface SupplierPerformanceTemplateCriteriaService {

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
	 * @param criteria
	 * @return
	 */
	SupplierPerformanceTemplateCriteria saveSupplierPerformanceTemplateCriteria(SupplierPerformanceTemplateCriteria criteria);

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
	 * @param criteria
	 * @return
	 */
	SupplierPerformanceTemplateCriteria updateSupplierPerformanceTemplateCriteria(SupplierPerformanceTemplateCriteria criteria);

	/**
	 * @param criteriaId
	 * @param templateId
	 */
	void deleteCriteriaByCreteriaId(String criteriaId, String templateId);

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
	 * @param criteriaId TODO
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
	 * @param criteriaId
	 * @return
	 */
	SupplierPerformanceTemplateCriteria getSPTCriteriaByCriteriaId(String criteriaId);

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
	boolean isCriteriaExists(String name, String templateId, String parentId);

	/**
	 * @param criteriaPojo
	 * @return TODO
	 * @throws NotAllowedException 
	 */
	void reorderCriteria(SupplierPerformanceTemplateCriteriaPojo criteriaPojo) throws NotAllowedException;

}

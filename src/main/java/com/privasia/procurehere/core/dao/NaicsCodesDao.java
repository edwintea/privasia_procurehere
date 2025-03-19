/**
 * 
 */
package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.NaicsCodes;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.pojo.SearchVo;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author Nitin Otageri
 */
public interface NaicsCodesDao extends GenericDao<NaicsCodes, String> {

	/**
	 * @param id
	 * @return
	 */
	List<NaicsCodes> findAll(String id);

	/**
	 * @param level
	 * @return
	 */
	List<NaicsCodes> findForLevel(Integer level);

	/**
	 * @param serviceCategory
	 * @return
	 */
	boolean isExists(NaicsCodes industryCategory);

	/**
	 * @param assigned
	 * @return
	 */
	List<NaicsCodes> findAssignedNaicsCodes(String supplierId);

	/**
	 * @return
	 */
	List<NaicsCodes> getAllIndustryCategoryPojo();

	/**
	 * @param code
	 * @return
	 */
	NaicsCodes findByCategoryCode(Integer code);

	/**
	 * @param list
	 */
	void loadIndustryCatgoryData(List<NaicsCodes> list, User createdBy);

	/**
	 * @param searchVo
	 * @return
	 */
	List<NaicsCodes> findChildForId(SearchVo searchVo);

	/**
	 * @param search
	 * @return
	 */
	List<NaicsCodes> searchForCategories(String search);

	/**
	 * @param projectId
	 * @return
	 */
	List<NaicsCodes> findAssignedIndustryCategoryForProject(String projectId);

	/**
	 * @param industryCategoryIds
	 * @return
	 */
	List<NaicsCodes> findIndustryCategoryByIds(String[] industryCategoryIds);

	/**
	 * @return
	 */
	List<NaicsCodes> findParentIndustryCategories();

	/**
	 * @param searchValue
	 * @return
	 */
	List<NaicsCodes> findLeafIndustryCategoryByName(String searchValue);

	NaicsCodes getIndustryCategoryForRftById(String id);

	/**
	 * @param start
	 * @param length
	 * @param order
	 * @return
	 */
	List<NaicsCodes> findAllActiveIndustryCategory(int start, int length, String order);

	/**
	 * 
	 * @param tenantId
	 * @param tableParams
	 * @return
	 */
	List<NaicsCodes> findNaicsQuery(String tenantId, TableDataInput tableParams);

	/**
	 * 
	 * @param tenantId
	 * @param tableParams
	 * @return
	 */
	long findTotalFilteredNaics(String tenantId, TableDataInput tableParams);

	/**
	 * 
	 * @param tenantId
	 * @return
	 */
	long findTotalNaics(String tenantId);

	/**
	 * 
	 * @param id
	 * @return
	 */
	NaicsCodes getIndustryCategoryCodeAndNameById(String id);

}

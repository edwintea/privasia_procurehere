package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.NaicsCodes;
import com.privasia.procurehere.core.pojo.NaicsCodesPojo;
import com.privasia.procurehere.core.pojo.SearchVo;
import com.privasia.procurehere.core.pojo.TableDataInput;

public interface NaicsCodesService {

	/**
	 * create Service
	 * 
	 * @return
	 */
	public String createNaicsCodes(NaicsCodes industryCategory);

	/**
	 * Update Service
	 */
	public void updateNaicsCodes(NaicsCodes industryCategory);

	/**
	 * delete Service
	 */
	public void deleteNaicsCodes(NaicsCodes industryCategory);

	/**
	 * @return IndustryCategoryList
	 */
	List<NaicsCodes> getAllNaicsCodesExceptSelf(String id);

	/**
	 * @param id
	 * @return
	 */
	public NaicsCodes getNaicsCodesById(String id);

	/**
	 * @param level
	 * @return
	 */
	List<NaicsCodes> findForLevel(Integer level);

	/**
	 * @param industryCategory
	 * @return
	 */
	boolean isExist(NaicsCodes industryCategory);

	/**
	 * @return
	 */
	List<NaicsCodesPojo> getAllNaicsCodesPojo(int start, int length, String order);

	/**
	 * @param searchVo
	 * @return
	 */
	List<NaicsCodes> findChildForId(SearchVo searchVo);

	/**
	 * @param activeTab
	 * @param supplierId
	 * @param projectId
	 * @param selected TODO
	 * @param search
	 * @return
	 */
	List<NaicsCodes> searchForCategories(String activeTab, String supplierId, String projectId, String[] selected, String search);

	List<NaicsCodes> searchForCategories(String search);

	public List<NaicsCodes> findNaicsQuery(String tenantId, TableDataInput tableParams);

	public long findTotalFilteredNaics(String tenantId, TableDataInput tableParams);

	public long findTotalNaics(String tenantId);

	List<NaicsCodes> searchForCategoriesForSupplierProfile(String activeTab, String supplierId, String projectId, String[] selected, String search);

	/**
	 * 
	 * @param id
	 * @return
	 */
	NaicsCodes getIndustryCategoryCodeAndNameById(String id);
}

/**
 * 
 */
package com.privasia.procurehere.service;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import com.privasia.procurehere.core.entity.GroupCode;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author jayshree
 *
 */
public interface GroupCodeService {

	/**
	 * @param tenantId
	 * @param input
	 * @return
	 */
	List<GroupCode> findGroupCodesForTenant(String tenantId, TableDataInput input);

	/**
	 * @param tenantId
	 * @param input
	 * @return
	 */
	long findTotalFilteredGroupCodesForTenant(String tenantId, TableDataInput input);

	/**
	 * @param tenantId
	 * @return
	 */
	long findTotalGroupCodesForTenant(String tenantId);

	/**
	 * @param groupCode
	 * @param tenantId
	 * @return
	 */
	boolean isExists(GroupCode groupCode, String tenantId);

	/**
	 * @param groupCode
	 */
	void saveGroupCode(GroupCode groupCode);

	/**
	 * @param persistObj
	 */
	void updateGroupCode(GroupCode persistObj);

	/**
	 * @param id
	 * @return
	 */
	GroupCode getGroupCodeById(String id);

	/**
	 * @param response
	 * @param file
	 * @param tenantId
	 */
	void downloadGroupCodeCsvFile(HttpServletResponse response, File file, String tenantId);

	/**
	 * @param workbook
	 * @return
	 */
	XSSFWorkbook templateDownloader(XSSFWorkbook workbook);

	/**
	 * @param file
	 * @param tenantId
	 * @param user
	 * @throws Exception
	 */
	void groupCodeUploadFile(MultipartFile file, String tenantId, User user) throws Exception;

	/**
	 * @param groupCode
	 */
	void deleteGroupCode(GroupCode groupCode);

	/**
	 * @param tenantId
	 * @param searchVal
	 * @return
	 */
	List<GroupCode> fetchAllGroupCodesForTenant(String tenantId, String searchVal);

	/**
	 * @param buId
	 * @return
	 */
	List<GroupCode> getGroupCodeIdByBusinessId(String buId);

	/**
	 * @param tenantId
	 * @param searchVal
	 * @param buId
	 * @return
	 */
	List<GroupCode> fetchAllGroupCodeForTenantForUnit(String tenantId, String searchVal, String buId);
	
	/**
	 * @param buId
	 * @return
	 */
	long getCountOfInactiveGroupCode(String buId);

	/**
	 * @param tenantId
	 * @param input
	 * @param id
	 * @param groupCodeIds
	 * @param removeIds
	 * @return
	 */
	List<GroupCode> findGroupCodeListByTenantId(String tenantId, TableDataInput input, String id, String[] groupCodeIds, String[] removeIds);

	/**
	 * @param tenantId
	 * @param input
	 * @param id
	 * @param groupCodeIds
	 * @param removeIds
	 * @return
	 */
	long findTotalFilteredGroupCodeForTenant(String tenantId, TableDataInput input, String id, String[] groupCodeIds, String[] removeIds);

	/**
	 * @param tenantId
	 * @return
	 */
	List<GroupCode> fetchAllActiveGroupCodeForTenantID(String tenantId);

	/**
	 * @param buId
	 * @return
	 */
	List<String> getGroupCodeByBusinessId(String buId);
	
	/**
	 * @param gcId
	 * @return
	 */
	GroupCode getGroupCodeByGCId(String gcId);

	/**
	 * 
	 * @param gcIds
	 * @return
	 */
	List<GroupCode> getGroupCodedByIds(List<String> gcIds);

	/**
	 * 
	 * @param buId
	 * @return
	 */
	List<GroupCode> getAllGroupCodeIdByBusinessUnitId(String buId);

}

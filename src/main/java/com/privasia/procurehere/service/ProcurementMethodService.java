package com.privasia.procurehere.service;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.privasia.procurehere.core.entity.ProcurementMethod;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.pojo.TableDataInput;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author sana
 */
public interface ProcurementMethodService {

	/**
	 * @param tenantId
	 * @param input
	 * @return
	 */
	List<ProcurementMethod> findProcurementMethodsForTenant(String tenantId, TableDataInput input);

	/**
	 * @param tenantId
	 * @param input
	 * @return
	 */
	long findTotalFilteredProcurementMethodsForTenant(String tenantId, TableDataInput input);

	/**
	 * @param tenantId
	 * @return
	 */
	long findCountOfProcurementMethodsForTenant(String tenantId);

	/**
	 * @param glCode
	 * @param tenantId
	 * @return
	 */
	boolean isExists(ProcurementMethod procurementMethod, String tenantId);

	/**
	 * @param id
	 * @return
	 */
	ProcurementMethod getProcurementMethodById(String id);

	/**
	 * @param procurementMethod
	 */
	void saveProcurementMethod(ProcurementMethod procurementMethod);

	/**
	 * @param persistObj
	 */
	void updateProcurementMethod(ProcurementMethod persistObj);

	/**
	 * @param procurementMethod
	 */
	void deleteProcurementMethod(ProcurementMethod procurementMethod);

	/**
	 * @param response
	 * @param tenantId
	 */
	void procurementMethodDownloadTemplate(HttpServletResponse response, String tenantId);

	List<ProcurementMethod> getAllActiveProcurementMethod(String tenantId);

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
	void procurementMethodUploadFile(MultipartFile file, String tenantId, User user) throws Exception;
}

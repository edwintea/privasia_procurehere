package com.privasia.procurehere.core.dao;

import java.util.Date;
import java.util.List;

import com.privasia.procurehere.core.entity.AgreementType;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.pojo.TableDataInput;

public interface AgreementTypeDao extends GenericDao<AgreementType, String> {

	/**
	 * 
	 * @param tenantId
	 * @return
	 */
	List<AgreementType> getAllActiveAgreementTypeForTenant(String tenantId);


	/**
	 * 
	 * @param agreementType
	 * @return
	 */
	boolean isExists(AgreementType agreementType);

	/**
	 * 
	 * @param agreementType
	 * @param tenantId
	 * @return
	 */
	AgreementType getAgreementTypebyCode(String agreementType, String tenantId);

	/**
	 * 
	 * @param tenantId
	 * @param tableParams
	 * @return
	 */
	List<AgreementType> findAgreementTypeForTenant(String tenantId, TableDataInput tableParams);

	/**
	 * 
	 * @param tenantId
	 * @param tableParams
	 * @return
	 */
	long findTotalFilteredAgreementTypeForTenant(String tenantId, TableDataInput tableParams);

	/**
	 * 
	 * @param tenantId
	 * @return
	 */
	long findTotalActiveAgreementTypeForTenant(String tenantId);

	void loadAgreementTypeMasterData(List<AgreementType> list, User createdBy);

	/**
	 * 
	 * @param tenantId
	 * @return
	 */
	AgreementType getAgreementTypeByAgreementTypeAndTenantId(String agreementType, String tenantId);

	/**
	 * 
	 * @param loggedInUserTenantId
	 * @param buyer
	 * @return
	 */
	List<AgreementType> getAllAgreementTypeForTenant(String loggedInUserTenantId);

	boolean isExists(String agreementType);

	/**
	 * @param tenantId
	 * @param search
	 * @return
	 */
	List<AgreementType> fetchAllActiveAgreementTypeForTenant(String tenantId, String search);

	/**
	 * @param tenantId
	 * @return
	 */
	long countConstructQueryToFetchAgreementType(String tenantId);

	long findTotalAgreementTypeCountForCsv(String tenantId);

	List<AgreementType> findAllActiveAgreementTypeForTenantIdForCsv(String tenantId, int pAGE_SIZE, int pageNo, String[] eventArr, AgreementType agreementType, boolean select_all, Date startDate, Date endDate);

	List<AgreementType> getAllAgreementTypeForCsv(String loggedInUserTenantId, int pAGE_SIZE, int pageNo);
	
	List<AgreementType> fetchAllAgreementTypeForTenant(String tenantId, String searchVal);


	long findTotalAgreementTypeForTenant(String tenantId);


	List<AgreementType> getAllAgreementTypeIdByBusinessUnitId(String buId);


	long getCountOfInactiveAgreementType(String buId);


	List<AgreementType> fetchAllAgreementTypeForTenantForUnit(String tenantId, String searchVal, String buId);


	long fetchFilterCountAllAgreementTypeForTenantForUnit(String tenantId, String buId);


	List<AgreementType> findAgreementTypeListByTenantId(String tenantId, TableDataInput input, String id, String[] agreementTypeIds, String[] removeIds);


	long findTotalFilteredAgreementTypeForTenant(String tenantId, TableDataInput input, String id, String[] agreementTypeIds, String[] removeIds);


	List<AgreementType> getAllActiveAgreementType(String tenantId);
	
}

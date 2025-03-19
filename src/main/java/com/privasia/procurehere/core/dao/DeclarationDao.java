package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.Declaration;
import com.privasia.procurehere.core.enums.DeclarationType;
import com.privasia.procurehere.core.pojo.DeclarationTemplatePojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author pooja
 */
public interface DeclarationDao extends GenericDao<Declaration, String> {
	/**
	 * @param loggedInUserTenantId
	 * @param input
	 * @return
	 */
	List<DeclarationTemplatePojo> findDeclarationsByTeantId(String loggedInUserTenantId, TableDataInput input);

	/**
	 * @param loggedInUserTenantId
	 * @param input
	 * @return
	 */
	long findTotalFilteredDeclarationsForTenant(String loggedInUserTenantId, TableDataInput input);

	/**
	 * @param loggedInUserTenantId
	 * @return
	 */
	long findTotalActiveDeclarationsForTenant(String loggedInUserTenantId);

	/***
	 * @param declarationType
	 * @param loggedInUserTenantId
	 * @return
	 */
	List<Declaration> getDeclarationsByTypeForTenant(DeclarationType declarationType, String tenantId);
}
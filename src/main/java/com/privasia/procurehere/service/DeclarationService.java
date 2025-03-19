package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.Declaration;
import com.privasia.procurehere.core.enums.DeclarationType;
import com.privasia.procurehere.core.pojo.DeclarationTemplatePojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author pooja
 */
public interface DeclarationService {
	/**
	 * @param declaration
	 */
	void saveOrUpdateDeclaration(Declaration declaration);

	/**
	 * @param id
	 * @return
	 */
	Declaration getDeclarationById(String id);

	/**
	 * @param declarationObj
	 */
	void updateDeclaration(Declaration declarationObj);

	/**
	 * @param declarationObj
	 */
	void deleteDeclaration(Declaration declarationObj);

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

	/**
	 * @param evaluationProcess
	 * @param loggedInUserTenantId
	 * @return
	 */
	List<Declaration> getDeclarationsByTypeForTenant(DeclarationType declarationType, String loggedInUserTenantId);

	/**
	 * @param declarationId
	 * @return
	 */
	long findAssignedCountOfDeclaration(String declarationId);

}
package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfiEvaluatorDeclaration;

/**
 * @author pooja
 */

public interface RfiEvaluatorDeclarationDao extends GenericDao<RfiEvaluatorDeclaration, String> {
	/**
	 * @param envelopId
	 * @param loggedInUser
	 * @param eventId TODO
	 * @return
	 */
	boolean isAcceptedEvaluationDeclaration(String envelopId, String loggedInUser, String eventId);

	/**
	 * @param envelopId
	 * @param eventId
	 * @return
	 */
	List<RfiEvaluatorDeclaration> getAllEvaluatorDeclarationByEnvelopAndEventId(String envelopId, String eventId);

}

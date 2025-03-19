package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfaEvaluatorDeclaration;

/**
 * @author pooja
 */

public interface RfaEvaluatorDeclarationDao extends GenericDao<RfaEvaluatorDeclaration, String> {
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
	List<RfaEvaluatorDeclaration> getAllEvaluatorDeclarationByEnvelopAndEventId(String envelopId, String eventId);

}

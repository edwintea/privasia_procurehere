package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfpEvaluatorDeclaration;

/**
 * @author pooja
 */

public interface RfpEvaluatorDeclarationDao extends GenericDao<RfpEvaluatorDeclaration, String> {
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
	List<RfpEvaluatorDeclaration> getAllEvaluatorDeclarationByEnvelopAndEventId(String envelopId, String eventId);

}

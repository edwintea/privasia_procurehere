package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfqEvaluatorDeclaration;

/**
 * @author pooja
 */

public interface RfqEvaluatorDeclarationDao extends GenericDao<RfqEvaluatorDeclaration, String> {
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
	List<RfqEvaluatorDeclaration> getAllEvaluatorDeclarationByEnvelopAndEventId(String envelopId, String eventId);

}

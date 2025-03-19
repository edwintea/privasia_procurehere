package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RftEvaluatorDeclaration;

/**
 * @author pooja
 */

public interface RftEvaluatorDeclarationDao extends GenericDao<RftEvaluatorDeclaration, String> {
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
	List<RftEvaluatorDeclaration> getAllEvaluatorDeclarationByEnvelopAndEventId(String envelopId, String eventId);

}

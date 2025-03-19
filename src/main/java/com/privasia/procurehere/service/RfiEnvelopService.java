package com.privasia.procurehere.service;

import java.io.IOException;
import java.util.List;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpSession;

import com.privasia.procurehere.core.entity.RfiCq;
import com.privasia.procurehere.core.entity.RfiEnvelop;
import com.privasia.procurehere.core.entity.RfiEvaluatorDeclaration;
import com.privasia.procurehere.core.entity.RfiEvaluatorUser;
import com.privasia.procurehere.core.entity.RfiEventSor;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.exceptions.ApplicationException;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;

/**
 * @author Ravi
 */
public interface RfiEnvelopService extends EnvelopServiceBase {

	/**
	 * @param rfiEnvelop
	 * @param envelopItemIds
	 * @param cqIds
	 * @return
	 */
	RfiEnvelop saveRfiEnvelop(RfiEnvelop rfiEnvelop, String[] envelopItemIds, String[] cqIds, String[] sorIds);

	/**
	 * @param rfiEnvelop
	 * @param cqIds
	 */
	void updateRfiEnvelop(RfiEnvelop rfiEnvelop, String[] bqIds, String[] cqIds, String[] sorIds);

	/**
	 * @param rfiEnvelop
	 */
	void deleteRfiEnvelop(RfiEnvelop rfiEnvelop);

	/**
	 * @return
	 */
	List<RfiEnvelop> getAllRfiEnvelop();

	/**
	 * @param rfiEnvelop
	 * @param eventId
	 * @return
	 */
	boolean isExists(RfiEnvelop rfiEnvelop, String eventId);

	/**
	 * @param id
	 * @return
	 */
	RfiEnvelop getRfiEnvelopById(String id);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfiEnvelop> getAllRfiEnvelopByEventId(String eventId);

	/**
	 * @param envelopId
	 * @return
	 */
	List<RfiCq> getAllRfiCqByEnvelopId(String envelopId);

	List<RfiEvaluatorUser> findEvaluatorsByEnvelopId(String envelopId);

	List<RfiEvaluatorUser> addEvaluator(String eventId, String envelopeId, String userId) throws ApplicationException;

	List<User> removeEvaluator(String eventId, String envelopeId, String userId);

	/**
	 * @param envelop
	 */
	void openEnvelop(RfiEnvelop envelop);

	/**
	 * @param evaluatorUser
	 */
	void updateEvaluatorUser(RfiEvaluatorUser evaluatorUser);

	/**
	 * @param envelopeId
	 * @param userId
	 * @return
	 */
	RfiEvaluatorUser getRfiEvaluatorUserByUserIdAndEnvelopeId(String envelopeId, String userId);

	/**
	 * @param envelop
	 * @param eventId TODO
	 * @return
	 * @throws ApplicationException
	 */
	RfiEnvelop updateEnvelope(RfiEnvelop envelop, String eventId) throws ApplicationException;

	/**
	 * @param envelopId
	 * @param userId
	 * @return
	 */
	RfiEvaluatorUser findEvaluatorUser(String envelopId, String userId);

	/**
	 * @param eventId
	 * @param logedUser
	 * @return
	 */
	List<RfiEnvelop> getAllEnvelopByEventId(String eventId, User logedUser);

	/**
	 * @param id
	 * @param logedUser
	 * @return
	 */
	RfiEnvelop getEnvelopForEvaluationById(String id, User logedUser);

	/**
	 * @param evenvelopId
	 * @param eventId
	 * @param virtualizer TODO
	 * @return
	 */
	JasperPrint generateEvaluationSubmissionReport(String evenvelopId, String eventId, HttpSession session, JRSwapFileVirtualizer virtualizer);

	/**
	 * @param envelopId
	 * @param logedUser
	 * @throws ApplicationException
	 */
	void updateEnvelopeStatus(String envelopId, User logedUser) throws ApplicationException;

	/**
	 * @param eventId
	 * @param envelopeId
	 * @param zos
	 * @param virtualizer TODO
	 * @param timeZoneStr TODO
	 * @return
	 * @throws IOException
	 * @throws JRException
	 */
	String generateEnvelopeZip(String eventId, String envelopeId, ZipOutputStream zos, boolean isForAllReports, HttpSession session, JRSwapFileVirtualizer virtualizer, String timeZoneStr) throws IOException, JRException;

	/**
	 * @param envelopeId
	 * @param supplierId
	 * @return
	 */
	JasperPrint generateSupplierCqPdfForEnvelope(String envelopeId, String supplierId, int supplierNo);

	Integer getAllEnvelopCountByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	int getcountClosedEnvelop(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfiEnvelop> getAllClosedEnvelopAndOpener(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<User> getAllEnvelopEvaluatorUsers(String eventId);

	/**
	 * @param envelopeId
	 * @param key
	 * @param eventId TODO
	 * @param loggedInUser TODO
	 * @return
	 */
	Boolean openEnvelope(String envelopeId, String key, String eventId, User loggedInUser);

	void updateLeadEvaluatorSummary(RfiEnvelop envelop);

	List<RfiEvaluatorUser> getEvaluationSummaryRemarks(String eventId, String evelopId, User user);

	boolean getCountOfAssignedSupplier(String leadUserId, String envelopId, String eventId);

	void closeEnvelop(RfiEnvelop envelop);

	RfiEnvelop getRfiEnvelopBySeq(Integer integer, String eventId);

	void updateRfiEnvelope(RfiEnvelop rfiEnvelop);

	/**
	 * @param envelopeId
	 * @param loggedInUser
	 * @param eventId TODO
	 * @return
	 */
	boolean isAcceptedEvaluationDeclaration(String envelopeId, String loggedInUser, String eventId);

	/**
	 * @param evaluationDeclarationObj
	 * @return
	 */
	RfiEvaluatorDeclaration saveEvaluatorDeclaration(RfiEvaluatorDeclaration evaluationDeclarationObj);

	/**
	 * @param eventId
	 * @return
	 */
	RfiEnvelop getEmptyEnvelopByEventId(String eventId);

	/**
	 * @param envelopId
	 * @return
	 */
	List<RfiCq> getCqsByEnvelopIdByOrder(String envelopId);

	void updateLeadEvaluatorDocument(RfiEnvelop envelop);

	RfiEvaluatorUser getRfiEvaluationDocument(String eventId, String envelopId, User loggedInUser);

	/**
	 *
	 * @param envelopId
	 * @return
	 */
	List<RfiEventSor> getSorsByEnvelopIdByOrder(String envelopId);


	JasperPrint generateSupplierSorPdfForEnvelope(String envelopeId, String supplierId, int i, JRSwapFileVirtualizer virtualizer);


	String getEnvelipeTitleById(String evenvelopId, String string);
}

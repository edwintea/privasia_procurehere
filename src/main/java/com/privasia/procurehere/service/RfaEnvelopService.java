package com.privasia.procurehere.service;

import java.io.IOException;
import java.util.List;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpSession;

import com.privasia.procurehere.core.entity.RfaCq;
import com.privasia.procurehere.core.entity.RfaEnvelop;
import com.privasia.procurehere.core.entity.RfaEvaluatorDeclaration;
import com.privasia.procurehere.core.entity.RfaEvaluatorUser;
import com.privasia.procurehere.core.entity.RfaEventSor;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.exceptions.ApplicationException;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;

public interface RfaEnvelopService extends EnvelopServiceBase {

	/**
	 * @param rfaEnvelop
	 * @param envelopItemIds
	 * @param cqIds
	 * @return
	 */
	RfaEnvelop saveRfaEnvelop(RfaEnvelop rfaEnvelop, String[] envelopItemIds, String[] cqIds, String[] sorIds);

	/**
	 * @param rfaEnvelop
	 * @param cqIds
	 */
	void updateRfaEnvelop(RfaEnvelop rfaEnvelop, String[] bqIds, String[] cqIds, String[] sorIds);

	/**
	 * @param rfaEnvelop
	 */
	void deleteRfaEnvelop(RfaEnvelop rfaEnvelop);

	/**
	 * @return
	 */
	List<RfaEnvelop> getAllRfaEnvelop();

	/**
	 * @param rfaEnvelop
	 * @param eventId
	 * @return
	 */
	boolean isExists(RfaEnvelop rfaEnvelop, String eventId);

	RfaEnvelop getRfaEnvelopById(String id);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfaEnvelop> getAllRfaEnvelopByEventId(String eventId);

	/**
	 * @param envelopId
	 * @return
	 */
	List<String> getRfaBqByEnvelopId(List<String> envelopId);

	List<RfaEvaluatorUser> findEvaluatorsByEnvelopId(String envelopId);

	List<RfaEvaluatorUser> addEvaluator(String eventId, String envelopeId, String userId) throws ApplicationException;

	List<User> removeEvaluator(String eventId, String envelopeId, String userId);

	/**
	 * @param envelop
	 */
	void openEnvelop(RfaEnvelop envelop);

	/**
	 * @param envelop
	 * @param eventId TODO
	 * @return
	 * @throws ApplicationException
	 */
	RfaEnvelop updateEnvelope(RfaEnvelop envelop, String eventId) throws ApplicationException;

	/**
	 * @param envelopId
	 * @param userId
	 * @return
	 */
	RfaEvaluatorUser findEvaluatorUser(String envelopId, String userId);

	/**
	 * @param id
	 * @param logedUser
	 * @return
	 */
	RfaEnvelop getEnvelopForEvaluationById(String id, User logedUser);

	/**
	 * @param eventId
	 * @param logedUser
	 * @return
	 */
	List<RfaEnvelop> getAllEnvelopByEventId(String eventId, User logedUser);

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
	 * @param b
	 * @return
	 * @throws IOException
	 * @throws JRException
	 */
	String generateEnvelopeZip(String eventId, String envelopeId, ZipOutputStream zos, boolean isForAllReport, HttpSession session, JRSwapFileVirtualizer virtualizer) throws IOException, JRException;

	/**
	 * @param envelopeId
	 * @param supplierId
	 * @param virtualizer TODO
	 * @return
	 */
	JasperPrint generateSupplierBqPdfForEnvelope(String envelopeId, String supplierId, int supplierNo, JRSwapFileVirtualizer virtualizer);

	/**
	 * @param envelopeId
	 * @param supplierId
	 * @return
	 */
	JasperPrint generateSupplierCqPdfForEnvelope(String envelopeId, String supplierId, int supplierNo);

	/**
	 * @param evenvelopId
	 * @param eventId
	 * @param session
	 * @param virtualizer TODO
	 * @return
	 */
	JasperPrint generateEvaluationSubmissionReport(String evenvelopId, String eventId, HttpSession session, JRSwapFileVirtualizer virtualizer);

	void updateEvaluatorUser(RfaEvaluatorUser evaluatorUser);

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
	List<RfaEnvelop> getAllClosedEnvelopAndOpener(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<User> getAllEnvelopEvaluatorUsers(String eventId);

	/**
	 * @param envelopeId
	 * @param eventId TODO
	 * @param key
	 * @param loggedInUser TODO
	 * @return
	 */
	Boolean openEnvelope(String envelopeId, String eventId, String key, User loggedInUser);

	List<RfaEvaluatorUser> getEvaluationSummaryRemarks(String eventId, String evelopId, User loggedInUser);

	RfaEnvelop updateLeadEvaluatorSummary(RfaEnvelop envelop);

	boolean getCountOfAssignedSupplier(String leadUserId, String envelopId, String eventId);

	void closeEnvelop(RfaEnvelop envelop);

	RfaEnvelop getRfaEnvelopBySeq(int i, String eventId);

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
	RfaEvaluatorDeclaration saveEvaluatorDeclaration(RfaEvaluatorDeclaration evaluationDeclarationObj);

	/**
	 * @param eventId
	 * @return
	 */
	RfaEnvelop getEmptyEnvelopByEventId(String eventId);

	/**
	 * @param envelopId
	 * @return
	 */
	List<RfaCq> getCqsByEnvelopIdByOrder(String envelopId);

	RfaEvaluatorUser getRfaEvaluationDocument(String eventId, String evelopId, User loggedInUser);

	List<RfaEventSor> getSorsByEnvelopIdByOrder(String envelopId);


	JasperPrint generateSupplierSorPdfForEnvelope(String envelopeId, String supplierId, int i, JRSwapFileVirtualizer virtualizer);
}

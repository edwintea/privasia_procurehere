package com.privasia.procurehere.service;

import java.io.IOException;
import java.util.List;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpSession;

import com.privasia.procurehere.core.entity.RfpCq;
import com.privasia.procurehere.core.entity.RfpEnvelop;
import com.privasia.procurehere.core.entity.RfpEvaluatorDeclaration;
import com.privasia.procurehere.core.entity.RfpEvaluatorUser;
import com.privasia.procurehere.core.entity.RfpEventBq;
import com.privasia.procurehere.core.entity.RfpEventSor;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.exceptions.ApplicationException;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;

public interface RfpEnvelopService extends EnvelopServiceBase {

	/**
	 * @param envelop
	 * @param envelopItemIds
	 * @param cqIds
	 * @return
	 */
	RfpEnvelop saveEnvelop(RfpEnvelop envelop, String[] envelopItemIds, String[] cqIds, String[] sorIds);

	/**
	 * @param envelop
	 * @param cqIds
	 */
	void updateEnvelop(RfpEnvelop envelop, String[] bqIds, String[] cqIds, String[] sorIds);

	/**
	 * @param envelop
	 */
	void deleteEnvelop(RfpEnvelop envelop);

	/**
	 * @return
	 */
	List<RfpEnvelop> getAllEnvelop();

	/**
	 * @param envelop
	 * @param eventId
	 * @return
	 */
	boolean isExists(RfpEnvelop envelop, String eventId);

	/**
	 * @param id
	 * @return
	 */
	RfpEnvelop getEnvelopById(String id);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfpEnvelop> getAllEnvelopByEventId(String eventId);

	/**
	 * @param envelopId
	 * @return
	 */
	List<RfpEvaluatorUser> findEvaluatorsByEnvelopId(String envelopId);

	List<RfpEvaluatorUser> addEvaluator(String eventId, String envelopeId, String userId) throws ApplicationException;

	/**
	 * @param envelop
	 */
	void openEnvelop(RfpEnvelop envelop);

	List<User> removeEvaluator(String envelopeId, String userId);

	/**
	 * @param envelopeId
	 * @param userId
	 * @return
	 */
	RfpEvaluatorUser getRfpEvaluatorUserByUserIdAndEnvelopeId(String envelopeId, String userId);

	/**
	 * @param evaluatorUser
	 */
	void updateEvaluatorUser(RfpEvaluatorUser evaluatorUser);

	/**
	 * @param envelop
	 * @param eventId TODO
	 * @return
	 * @throws ApplicationException
	 */
	RfpEnvelop updateEnvelope(RfpEnvelop envelop, String eventId) throws ApplicationException;

	/**
	 * @param envelopId
	 * @param userId
	 * @return
	 */
	RfpEvaluatorUser findEvaluatorUser(String envelopId, String userId);

	/**
	 * @param evenvelopId
	 * @param eventId
	 * @param virtualizer TODO
	 * @return
	 */
	JasperPrint generateEvaluationSubmissionReport(String evenvelopId, String eventId, HttpSession session, JRSwapFileVirtualizer virtualizer);

	/**
	 * @param eventId
	 * @param logedUser
	 * @return
	 */
	List<RfpEnvelop> getAllEnvelopByEventId(String eventId, User logedUser);

	/**
	 * @param id
	 * @param logedUser
	 * @return
	 */
	RfpEnvelop getEnvelopForEvaluationById(String id, User logedUser);

	/**
	 * @param eventId
	 * @param evenvelopId
	 * @param zos
	 * @param virtualizer TODO
	 * @return
	 * @throws IOException
	 * @throws JRException
	 */
	String generateEnvelopeZip(String eventId, String evenvelopId, ZipOutputStream zos, boolean isForAllReports, HttpSession session, JRSwapFileVirtualizer virtualizer) throws IOException, JRException;

	/**
	 * @param envelopId
	 * @param logedUser
	 * @throws ApplicationException
	 */
	void updateEnvelopeStatus(String envelopId, User logedUser) throws ApplicationException;

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
	List<RfpEnvelop> getAllClosedEnvelopAndOpener(String eventId);

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

	void updateLeadEvaluatorSummary(RfpEnvelop envelop);

	List<RfpEvaluatorUser> getEvaluationSummaryRemarks(String eventId, String evelopId, User loginUser);

	RfpEnvelop getRfpEnvelopById(String evenvelopId);

	boolean getCountOfAssignedSupplier(String leadUserId, String envelopId, String eventId);

	void closeEnvelop(RfpEnvelop envelop);

	RfpEnvelop getRfpEnvelopBySeq(int i, String eventId);

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
	RfpEvaluatorDeclaration saveEvaluatorDeclaration(RfpEvaluatorDeclaration evaluationDeclarationObj);

	/**
	 * @param eventId
	 * @return
	 */
	RfpEnvelop getEmptyEnvelopByEventId(String eventId);

	/**
	 * @param envelopId
	 * @return
	 */
	List<RfpEventBq> getBqsByEnvelopIdByOrder(String envelopId);

	/**
	 * @param envelopId
	 * @return
	 */
	List<RfpCq> getCqsByEnvelopIdByOrder(String envelopId);

	void updateLeadEvaluatorDocument(RfpEnvelop envelop);


	List<RfpEventSor> getSorsByEnvelopIdByOrder(String envelopId);


	JasperPrint generateSupplierSorPdfForEnvelope(String envelopeId, String supplierId, int i, JRSwapFileVirtualizer virtualizer);
}

package com.privasia.procurehere.service;

import java.io.IOException;
import java.util.List;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpSession;

import com.privasia.procurehere.core.entity.RfqCq;
import com.privasia.procurehere.core.entity.RfqEnvelop;
import com.privasia.procurehere.core.entity.RfqEvaluatorDeclaration;
import com.privasia.procurehere.core.entity.RfqEvaluatorUser;
import com.privasia.procurehere.core.entity.RfqEventBq;
import com.privasia.procurehere.core.entity.RfqEventSor;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.exceptions.ApplicationException;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;

public interface RfqEnvelopService extends EnvelopServiceBase {

	/**
	 * @param envelop
	 * @param envelopItemIds
	 * @param cqIds
	 * @return
	 */
	RfqEnvelop saveEnvelop(RfqEnvelop envelop, String[] envelopItemIds, String[] cqIds, String[] sorIds);

	/**
	 * @param envelop
	 * @param cqIds
	 */
	void updateEnvelop(RfqEnvelop envelop, String[] bqIds, String[] cqIds, String[] sorIds);

	/**
	 * @param envelop
	 */
	void deleteEnvelop(RfqEnvelop envelop);

	/**
	 * @return
	 */
	List<RfqEnvelop> getAllEnvelop();

	/**
	 * @param envelop
	 * @param eventId
	 * @return
	 */
	boolean isExists(RfqEnvelop envelop, String eventId);

	/**
	 * @param id
	 * @return
	 */
	RfqEnvelop getEnvelopById(String id);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfqEnvelop> getAllEnvelopByEventId(String eventId);

	/**
	 * @param envelopId
	 * @return
	 */
	List<String> getBqByEnvelopId(List<String> envelopId);

	List<RfqEvaluatorUser> findEvaluatorsByEnvelopId(String envelopId);

	List<User> removeEvaluator(String eventId, String envelopeId, String userId);

	List<RfqEvaluatorUser> addEvaluator(String eventId, String envelopeId, String userId) throws ApplicationException;

	/**
	 * @param envelop
	 */
	void openEnvelop(RfqEnvelop envelop);

	/**
	 * @param evaluatorUser
	 */
	RfqEvaluatorUser updateEvaluatorUser(RfqEvaluatorUser evaluatorUser);

	/**
	 * @param envelopeId
	 * @param userId
	 * @return
	 */
	RfqEvaluatorUser getRfqEvaluatorUserByUserIdAndEnvelopeId(String envelopeId, String userId);

	/**
	 * @param envelop
	 * @param eventId TODO
	 * @return
	 * @throws ApplicationException
	 */
	RfqEnvelop updateEnvelope(RfqEnvelop envelop, String eventId) throws ApplicationException;

	/**
	 * @param envelopId
	 * @param userId
	 * @return
	 */
	RfqEvaluatorUser findEvaluatorUser(String envelopId, String userId);

	/**
	 * @param id
	 * @param logedUser
	 * @return
	 */
	RfqEnvelop getEnvelopForEvaluationById(String id, User logedUser);

	/**
	 * @param eventId
	 * @param logedUser
	 * @return
	 */
	List<RfqEnvelop> getAllEnvelopByEventId(String eventId, User logedUser);

	/**
	 * @param envelopId
	 * @param logedUser
	 * @throws ApplicationException
	 */
	void updateEnvelopeStatus(String envelopId, User logedUser) throws ApplicationException;

	/**
	 * @param evenvelopId
	 * @param eventId
	 * @param virtualizer TODO
	 * @return
	 */
	JasperPrint generateEvaluationSubmissionReport(String evenvelopId, String eventId, HttpSession session, JRSwapFileVirtualizer virtualizer);

	/**
	 * @param envelopeId
	 * @param supplierId
	 * @param virtualizer TODO
	 * @return
	 */
	JasperPrint generateSupplierBqPdfForEnvelope(String envelopeId, String supplierId, int supllierNo, JRSwapFileVirtualizer virtualizer);

	/**
	 * @param envelopeId
	 * @param supplierId
	 * @return
	 */
	JasperPrint generateSupplierCqPdfForEnvelope(String envelopeId, String supplierId, int supplierNo);

	/**
	 * @param eventId
	 * @param envelopeId
	 * @param zos
	 * @param virtualizer TODO
	 * @return
	 * @throws IOException
	 * @throws JRException
	 */
	String generateEnvelopeZip(String eventId, String envelopeId, ZipOutputStream zos, boolean isForAllReports, HttpSession session, JRSwapFileVirtualizer virtualizer) throws IOException, JRException;

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
	List<RfqEnvelop> getAllClosedEnvelopAndOpener(String eventId);

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

	void updateLeadEvaluatorSummary(RfqEnvelop envelop);

	List<RfqEvaluatorUser> getEvaluationSummaryRemarks(String eventId, String evelopId, User loginUser);

	RfqEnvelop getRfqEnvelopById(String evenvelopId);

	boolean getCountOfAssignedSupplier(String leadUserId, String envelopId, String eventId);

	void closeEnvelop(RfqEnvelop envelop);

	RfqEnvelop getRfiEnvelopBySeq(int i, String eventId);

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
	RfqEvaluatorDeclaration saveEvaluatorDeclaration(RfqEvaluatorDeclaration evaluationDeclarationObj);

	/**
	 * @param eventId
	 * @return
	 */
	RfqEnvelop getEmptyEnvelopByEventId(String eventId);

	/**
	 * @param envelopId
	 * @return
	 */
	List<RfqEventBq> getBqsByEnvelopIdByOrder(String envelopId);

	/**
	 * @param envelopId
	 * @return
	 */
	List<RfqCq> getCqsByEnvelopIdByOrder(String envelopId);

	void updateLeadEvaluatorDocument(RfqEnvelop envelop);

	List<RfqEventSor> getSorsByEnvelopIdByOrder(String envelopId);


	/**
	 * @param envelopeId
	 * @param supplierId
	 * @param i
	 * @param virtualizer
	 * @return
	 */
	JasperPrint generateSupplierSorPdfForEnvelope(String envelopeId, String supplierId, int i, JRSwapFileVirtualizer virtualizer);
}

package com.privasia.procurehere.service;

import java.io.IOException;
import java.util.List;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpSession;

import com.privasia.procurehere.core.entity.RftEventSor;
import org.springframework.context.NoSuchMessageException;

import com.privasia.procurehere.core.entity.RftCq;
import com.privasia.procurehere.core.entity.RftEnvelop;
import com.privasia.procurehere.core.entity.RftEvaluatorDeclaration;
import com.privasia.procurehere.core.entity.RftEvaluatorUser;
import com.privasia.procurehere.core.entity.RftEventBq;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.exceptions.ApplicationException;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;

public interface RftEnvelopService extends EnvelopServiceBase {

	/**
	 * @param rftEnvelop
	 * @param envelopItemIds
	 * @param cqIds
	 * @return
	 */
	RftEnvelop saveRftEnvelop(RftEnvelop rftEnvelop, String[] envelopItemIds, String[] cqIds, String[] sorIds);

	/**
	 * @param rftEnvelop
	 * @param cqIds
	 */
	void updateRftEnvelop(RftEnvelop rftEnvelop, String[] bqIds, String[] cqIds, String[] sorIds);

	/**
	 * @param rftEnvelop
	 */
	void deleteRftEnvelop(RftEnvelop rftEnvelop);

	/**
	 * @return
	 */
	List<RftEnvelop> getAllRftEnvelop();

	/**
	 * @param rftEnvelop
	 * @param eventId
	 * @return
	 */
	boolean isExists(RftEnvelop rftEnvelop, String eventId);

	RftEnvelop getRftEnvelopById(String id);

	/**
	 * @param eventId
	 * @param logedUser TODO
	 * @return
	 */
	List<RftEnvelop> getAllRftEnvelopByEventId(String eventId, User logedUser);

	/**
	 * @param envelopId
	 * @return
	 */
	List<String> getRftBqByEnvelopId(List<String> envelopId);

	List<RftEvaluatorUser> findEvaluatorsByEnvelopId(String envelopId);

	List<User> removeEvaluator(String eventId, String envelopeId, String userId);

	List<RftEvaluatorUser> addEvaluator(String eventId, String envelopeId, String userId) throws ApplicationException;

	/**
	 * @param envelop
	 */
	void openEnvelop(RftEnvelop envelop);

	/**
	 * @param envelopeId
	 * @param userId
	 * @return
	 */
	RftEvaluatorUser getRftEvaluatorUserByUserIdAndEnvelopeId(String envelopeId, String userId);

	/**
	 * @param evaluatorUser
	 * @return
	 */
	RftEvaluatorUser updateEvaluatorUser(RftEvaluatorUser evaluatorUser);

	/**
	 * @param rftEnvelop
	 * @param eventId TODO
	 * @param isUserControl TODO
	 * @return
	 * @throws ApplicationException
	 * @throws NoSuchMessageException
	 */
	RftEnvelop updateRftEnvelopeTeam(RftEnvelop rftEnvelop, String eventId, boolean isUserControl) throws NoSuchMessageException, ApplicationException;

	/**
	 * @param envelopId
	 * @param userId
	 * @return
	 */
	RftEvaluatorUser findEvaluatorUser(String envelopId, String userId);

	/**
	 * @param envelopId
	 * @return
	 */
	boolean findClosedStatusForLeadEvaluator(String envelopId);

	/**
	 * @param id
	 * @param logedUser
	 * @return
	 */
	RftEnvelop getRftEnvelopForEvaluationById(String id, User logedUser);

	/**
	 * @param eventId
	 * @param evenvelopId
	 * @param zos
	 * @param virtualizer TODO
	 * @return TODO
	 */
	String generateEnvelopeZip(String eventId, String evenvelopId, ZipOutputStream zos, boolean isForAllReports, HttpSession session, JRSwapFileVirtualizer virtualizer) throws IOException, JRException;

	/**
	 * @param envelopeId
	 * @param supplierId
	 * @param virtualizer TODO
	 * @return
	 */
	JasperPrint generateSupplierBqPdfForEnvelope(String envelopeId, String supplierId, int supplierNumber, JRSwapFileVirtualizer virtualizer);

	/**
	 * @param envelopeId
	 * @param supplierId
	 * @return
	 */
	JasperPrint generateSupplierCqPdfForEnvelope(String envelopeId, String supplierId, int supplierNo);

	/**
	 * @param envelopId
	 * @param logedUser TODO
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

	Integer getAllEnvelopCountByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RftEnvelop> getAllClosedEnvelopAndOpener(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	int getcountClosedEnvelop(String eventId);

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

	void updateLeadEvaluatorSummary(RftEnvelop envelop);

	List<RftEvaluatorUser> getEvaluationSummaryRemarks(String eventId, String evelopId, User loginUser);

	boolean getCountOfAssignedSupplier(String leadUserId, String envelopId, String eventId);

	void closeEnvelop(RftEnvelop envelop);

	String getEnvelipeTitleById(String evenvelopId, String string);

	RftEnvelop getRftEnvelopBySeqId(int i, String eventId);

	/**
	 * @param envelopId
	 * @param loggedInUser
	 * @param eventId TODO
	 * @return
	 */
	boolean isAcceptedEvaluationDeclaration(String envelopId, String loggedInUser, String eventId);

	/**
	 * @param evaluationDeclarationObj
	 * @return
	 */
	RftEvaluatorDeclaration saveEvaluatorDeclaration(RftEvaluatorDeclaration evaluationDeclarationObj);

	/**
	 * @param eventId
	 * @return
	 */
	RftEnvelop getEmptyEnvelopByEventId(String eventId);

	/**
	 * @param envelopId
	 * @return
	 */
	List<RftEventBq> getBqsByEnvelopIdByOrder(String envelopId);

	/**
	 * @param envelopId
	 * @return
	 */
	List<RftCq> getCqsByEnvelopIdByOrder(String envelopId);

	List<RftEventSor> getSorsByEnvelopIdByOrder(String envelopId);

	JasperPrint generateSupplierSorPdfForEnvelope(String envelopeId, String supplierId, int i, JRSwapFileVirtualizer virtualizer);
}

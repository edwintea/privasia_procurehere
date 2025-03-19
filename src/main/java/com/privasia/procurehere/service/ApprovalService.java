package com.privasia.procurehere.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.http.HttpSession;

import com.privasia.procurehere.core.entity.Budget;
import com.privasia.procurehere.core.entity.BudgetApprovalUser;
import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.Po;
import com.privasia.procurehere.core.entity.Pr;
import com.privasia.procurehere.core.entity.ProductContract;
import com.privasia.procurehere.core.entity.RequestedAssociatedBuyer;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.entity.RfiEvent;
import com.privasia.procurehere.core.entity.RfpEvent;
import com.privasia.procurehere.core.entity.RfqEvent;
import com.privasia.procurehere.core.entity.RftEvent;
import com.privasia.procurehere.core.entity.SourcingFormApprovalUserRequest;
import com.privasia.procurehere.core.entity.SourcingFormRequest;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.SupplierFormSubmition;
import com.privasia.procurehere.core.entity.SupplierFormSubmitionApprovalUser;
import com.privasia.procurehere.core.entity.SupplierPerformanceEvaluatorUser;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.FilterTypes;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.AuthenticatedUser;

import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;

/**
 * @author Parveen
 */
public interface ApprovalService {
	/**
	 * @param pr
	 * @param loggedInUser
	 * @param isFInish TODO
	 * @return
	 * @throws Exception
	 */
	Pr doApproval(Pr pr, User loggedInUser, Boolean isFInish) throws Exception;

	/**
	 * @param pr
	 * @param actionBy
	 * @param remarks
	 * @param approved
	 * @return
	 */
	Pr doApproval(Pr pr, User actionBy, String remarks, boolean approved) throws Exception;

	/**
	 * @param event
	 * @param loggedInUser
	 * @param virtualizer
	 * @return
	 * @throws Exception
	 */
	RftEvent doApproval(RftEvent event, HttpSession session, User loggedInUser, JRSwapFileVirtualizer virtualizer, Date eventApprovedAndFinishDate) throws Exception;

	/**
	 * @param event
	 * @param actionBy
	 * @param remarks
	 * @param approved
	 * @param virtualizer
	 * @param actionDate TODO
	 * @return
	 * @throws NotAllowedException
	 */
	RftEvent doApproval(RftEvent event, User actionBy, String remarks, boolean approved, HttpSession session, JRSwapFileVirtualizer virtualizer, Date actionDate) throws NotAllowedException;

	/**
	 * @param event
	 * @param loggedInUser
	 * @param virtualizer
	 * @return
	 * @throws Exception
	 */
	RfpEvent doApproval(RfpEvent event, HttpSession session, User loggedInUser, JRSwapFileVirtualizer virtualizer, Date eventApprovedAndFinishDate) throws Exception;

	/**
	 * @param event
	 * @param actionBy
	 * @param remarks
	 * @param approved
	 * @param virtualizer
	 * @param actionDate TODO
	 * @return
	 * @throws NotAllowedException
	 */
	RfpEvent doApproval(RfpEvent event, User actionBy, String remarks, boolean approved, HttpSession session, JRSwapFileVirtualizer virtualizer, Date actionDate) throws NotAllowedException;

	/**
	 * @param event
	 * @param loggedInUser
	 * @param virtualizer
	 * @return
	 * @throws Exception
	 */
	RfqEvent doApproval(RfqEvent event, HttpSession session, User loggedInUser, JRSwapFileVirtualizer virtualizer, Date eventApprovedAndFinishDate) throws Exception;

	/**
	 * @param event
	 * @param actionBy
	 * @param remarks
	 * @param approved
	 * @param virtualizer
	 * @param actionDate TODO
	 * @return
	 * @throws NotAllowedException
	 */
	RfqEvent doApproval(RfqEvent event, User actionBy, String remarks, boolean approved, HttpSession session, JRSwapFileVirtualizer virtualizer, Date actionDate) throws NotAllowedException;

	/**
	 * @param event
	 * @param loggedInUser
	 * @param virtualizer
	 * @return
	 * @throws Exception
	 */
	RfiEvent doApproval(RfiEvent event, HttpSession session, User loggedInUser, JRSwapFileVirtualizer virtualizer, Date eventApprovedAndFinishDate) throws Exception;

	/**
	 * @param event
	 * @param actionBy
	 * @param remarks
	 * @param approved
	 * @param virtualizer
	 * @param actionDate TODO
	 * @return
	 * @throws NotAllowedException
	 */
	RfiEvent doApproval(RfiEvent event, User actionBy, String remarks, boolean approved, HttpSession session, JRSwapFileVirtualizer virtualizer, Date actionDate) throws NotAllowedException;

	/**
	 * @param event
	 * @param loggedInUser
	 * @param virtualizer
	 * @return
	 * @throws Exception
	 */
	RfaEvent doApproval(RfaEvent event, HttpSession session, User loggedInUser, JRSwapFileVirtualizer virtualizer, Date eventApprovedAndFinishDate) throws Exception;

	/**
	 * @param event
	 * @param actionBy
	 * @param remarks
	 * @param approved
	 * @param virtualizer
	 * @param actionDate TODO
	 * @return
	 * @throws NotAllowedException
	 */
	RfaEvent doApproval(RfaEvent event, User actionBy, String remarks, boolean approved, HttpSession session, JRSwapFileVirtualizer virtualizer, Date actionDate) throws NotAllowedException;

	String findBusinessUnit(String eventId, RfxTypes rfxTypes);

	/**
	 * @param eventId
	 * @param type
	 * @param user
	 * @param remarks
	 * @param approve
	 * @param session
	 * @param virtualizer
	 * @return
	 * @throws Exception
	 */
	boolean doMobileApproval(String eventId, FilterTypes type, AuthenticatedUser user, String remarks, boolean approve, HttpSession session, JRSwapFileVirtualizer virtualizer) throws Exception;

	SourcingFormRequest doRequestApproval(SourcingFormRequest sourcingFormRequest, User loggedInUser) throws Exception;

	SourcingFormRequest doApprovalRequest(SourcingFormRequest sourcingFormRequest, User actionBy, String remarks, boolean approved) throws Exception;

	/**
	 * @param supplier
	 * @param supplierRemark
	 * @param buyer
	 * @param timeZone
	 */
	void sendEmailToAssociatedBuyer(Supplier supplier, String supplierRemark, Buyer buyer, TimeZone timeZone);

	void sendEmailToAssociatedSupplier(Supplier supplier, Buyer buyer, TimeZone timeZone, boolean approveRejectFlag, RequestedAssociatedBuyer associatedBuyer, String buyerRemark);

	void sendEmailToFavSupplier(Supplier supplier, Buyer buyer, TimeZone timeZone);

	Budget doBudgetApproval(Budget budget, User actionBy, String remarks, boolean approved, HttpSession session) throws NotAllowedException;

	void sentBudgetUtilizedNotifications(Budget budget, BigDecimal percentageUtilized);

	void sendBudgetOverrunNotification(Budget budget);

	/**
	 * @param budget
	 * @param nextLevelUser
	 */
	void sendBudgetApprovalReqEmailsOnCreate(Budget budget, BudgetApprovalUser nextLevelUser);

	void sendPoSupplierActionEmailNotificationToBuyer(Supplier supplier, boolean isAccept, Po po, String supplierRemark);

	void sendCancelPoEmailNotificationToSupplier(Po po, String poRemarks, User actionBy);

	void sendPoReceivedEmailNotificationToSupplier(Po po, User actionBy);

	void sendPoCreatedEmailToCreater(User mailTo, Pr pr, User actionBy);

	void sharePoToFinance(Po po);

	/**
	 * @param supplierFormSubmition
	 * @param loggedInUser
	 * @param remarks
	 * @param b
	 * @return
	 * @throws NotAllowedException
	 */
	SupplierFormSubmition doApprovalFormSubmition(SupplierFormSubmition supplierFormSubmition, User loggedInUser, String remarks, boolean b) throws NotAllowedException;

	/**
	 * @param supplierFormSubmition
	 * @param nextLevelUser
	 * @param buyerTimeZone
	 */
	void sendEmailToSupplierFormApprovers(SupplierFormSubmition supplierFormSubmition, SupplierFormSubmitionApprovalUser nextLevelUser, String buyerTimeZone);

	/**
	 * @param sourcingFormRequest
	 * @param nextLevelUser
	 * @param buyerTimeZone
	 */
	void sendEmailToRequestApprovers(SourcingFormRequest sourcingFormRequest, SourcingFormApprovalUserRequest nextLevelUser, String buyerTimeZone);

	/**
	 * @param po
	 * @param loggedInUser
	 * @return
	 * @throws Exception
	 */
	Po doApproval(Po po, User loggedInUser) throws Exception;

	/**
	 * @param po
	 * @param loggedInUser
	 * @return
	 * @throws Exception
	 */
	Po doPoFinishApprovalFromDraft(Po po, User loggedInUser) throws Exception;

	/**
	 * @param po
	 * @param loggedInUser
	 * @return
	 * @throws Exception
	 */
	Po doPoFinishApprovalFromSuspend(Po po, User loggedInUser) throws Exception;

	/**
	 * @param po
	 * @param actionBy
	 * @return
	 * @throws Exception
	 */

	Po doApproval(Po po, User actionBy, String remarks, boolean approved) throws Exception;

	/**
	 * @param po
	 * @param supplier
	 * @param buyerTimeZone
	 * @param loggedInUser
	 */
	void sendRevisedPoReceivedEmailNotification(Po po, Supplier supplier, String buyerTimeZone, User loggedInUser);

	/**
	 * @param evaluatorUser
	 * @param session
	 * @param loggedInUser
	 * @param virtualizer
	 * @return
	 * @throws Exception
	 */
	SupplierPerformanceEvaluatorUser doApproval(SupplierPerformanceEvaluatorUser evaluatorUser, HttpSession session, User loggedInUser, JRSwapFileVirtualizer virtualizer) throws Exception;

	/**
	 * @param evaluatorUser
	 * @param actionBy
	 * @param remarks
	 * @param approved
	 * @param session
	 * @param virtualizer
	 * @return
	 * @throws NotAllowedException
	 */
	SupplierPerformanceEvaluatorUser doApproval(SupplierPerformanceEvaluatorUser evaluatorUser, User actionBy, String remarks, boolean approved, HttpSession session, JRSwapFileVirtualizer virtualizer) throws NotAllowedException;

	/**
	 * @param contract
	 * @param loggedInUser
	 * @param isFinish
	 * @return
	 * @throws Exception
	 */
	ProductContract doApproval(ProductContract contract, User loggedInUser, Boolean isFinish, HttpSession session, JRSwapFileVirtualizer virtualizer) throws Exception;

	ProductContract doApproval(ProductContract contract, User actionBy, String remarks, boolean approved, HttpSession session) throws Exception;

	ProductContract doTerminationApproval(String contractId, User loggedInUser, Boolean isFInish, HttpSession session, String terminateReason) throws Exception;

	/**
	 * @param loggedInUser
	 * @param productContractId
	 * @param errormessage
	 */
	void updateContractDetailsOnErpError(User loggedInUser, String productContractId, String errormessage);

}

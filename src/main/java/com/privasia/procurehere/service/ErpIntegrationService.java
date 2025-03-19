package com.privasia.procurehere.service;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import com.privasia.procurehere.core.entity.ErpSetup;
import com.privasia.procurehere.core.entity.Po;
import com.privasia.procurehere.core.entity.RfaEvent;
import com.privasia.procurehere.core.entity.RfpEvent;
import com.privasia.procurehere.core.entity.RfqEvent;
import com.privasia.procurehere.core.entity.RftEvent;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.PoStatus;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.ErpIntegrationException;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.pojo.*;

import javax.servlet.http.HttpSession;

/**
 * @author parveen
 */

public interface ErpIntegrationService {

	/**
	 * @param templateId
	 * @param adminUser
	 * @param prToAuctionErpPojo
	 * @return
	 * @throws Exception
	 */
	RftEvent copyFromRftTemplateForErp(String templateId, User adminUser, PrToAuctionErpPojo prToAuctionErpPojo) throws Exception;

	/**
	 * @param templateId
	 * @param adminUser
	 * @param prToAuctionErpPojo
	 * @return
	 * @throws Exception
	 */
	RfpEvent copyFromRfpTemplateForErp(String templateId, User adminUser, PrToAuctionErpPojo prToAuctionErpPojo) throws Exception;

	/**
	 * @param templateId
	 * @param adminUser
	 * @param prToAuctionErpPojo
	 * @return
	 * @throws Exception
	 */
	RfqEvent copyFromRfqTemplateForErp(String templateId, User adminUser, PrToAuctionErpPojo prToAuctionErpPojo) throws Exception;

	/**
	 * @param templateId
	 * @param createdBy
	 * @param prToAuctionErpPojo
	 * @return
	 * @throws Exception
	 */
	RfaEvent copyFromRfaTemplateForErp(String templateId, User createdBy, PrToAuctionErpPojo prToAuctionErpPojo) throws Exception;

	/**
	 * @param awardErpPojoList
	 * @return
	 * @throws Exception
	 */
	AwardResponsePojo sendAwardPage(List<AwardErpPojo> awardErpPojoList, String eventId, RfxTypes eventType) throws Exception;

	/**
	 * @param prId
	 * @throws Exception
	 */
	void transferPrToErp(String prId) throws Exception;

	List<MobileEventPojo> getEventTypeFromPrNo(String prNo, String tanentId);

	RfaEvent overwriteFromRfaTemplateForErp(User loggedInUser, PrToAuctionErpPojo prToAuctionErpPojo, MobileEventPojo mobileEventPojo) throws ApplicationException;

	RfqEvent overwriteRfqTemplateForErp(User loggedInUser, PrToAuctionErpPojo prToAuctionErpPojo, MobileEventPojo draftEventPojo) throws ApplicationException;

	RfpEvent overwriteRfpTemplateForErp(User loggedInUser, PrToAuctionErpPojo prToAuctionErpPojo, MobileEventPojo draftEventPojo) throws ApplicationException;

	RftEvent overwriteRftTemplateForErp(User loggedInUser, PrToAuctionErpPojo prToAuctionErpPojo, MobileEventPojo draftEventPojo) throws ApplicationException;

	/**
	 * @param rfqResponseErpPojo
	 * @param erpSetup
	 */
	void updateRfqResponse(RFQResponseErpPojo rfqResponseErpPojo, ErpSetup erpSetup);

	/**
	 * @param rfsId
	 * @param erpConfig
	 * @param loggedInUser TODO
	 * @throws Exception
	 */
	void transferRfsToErp(String rfsId, ErpSetup erpConfig, User loggedInUser) throws Exception;


	/**
	 * @param rfxTypes
	 * @param eventId
	 * @param erpConfig
	 * @param loggedInUser TODO
	 * @throws Exception
	 */
	SapResponseToTransferAward transferRfxAwardToErp(RfxTypes rfxTypes, String eventId, ErpSetup erpConfig, User loggedInUser, String awardId,
													 HttpSession session) throws Exception;

	/**
	 * @param rfsId
	 * @param erpSetup
	 * @param loggedInUser TODO
	 * @throws Exception
	 */
	void transferRejectRfsToErp(String rfsId, ErpSetup erpSetup, User loggedInUser) throws Exception;

	/**
	 * @param event
	 * @param erpSetup
	 * @param loggedInUser TODO
	 * @throws Exception
	 */
	void transferRejectRfsToErp(RfqEvent event, ErpSetup erpSetup, User loggedInUser) throws Exception;

	/**
	 * @param event
	 * @param erpSetup
	 * @param loggedInUser TODO
	 * @throws Exception
	 */
	void transferRejectRfsToErp(RftEvent event, ErpSetup erpSetup, User loggedInUser) throws Exception;

	/**
	 * @param event
	 * @param erpSetup
	 * @param loggedInUser TODO
	 * @throws Exception
	 */
	void transferRejectRfsToErp(RfaEvent event, ErpSetup erpSetup, User loggedInUser) throws Exception;

	/**
	 * @param event
	 * @param erpSetup
	 * @param loggedInUser TODO
	 * @throws Exception
	 */
	void transferRejectRfsToErp(RfpEvent event, ErpSetup erpSetup, User loggedInUser) throws Exception;

	/**
	 * @param poAcceptDeclinePojo
	 * @param erpSetup TODO
	 * @throws Exception
	 */
	void sendPoAcceptDeclineToErp(PoAcceptDeclinePojo poAcceptDeclinePojo, ErpSetup erpSetup) throws Exception;

	/**
	 * @param formId
	 * @return TODO
	 * @throws ErpIntegrationException
	 */
	String transferSupplierPerformanceToErp(String formId) throws ErpIntegrationException;

	/**
	 * @param contractId
	 * @param erpSetup
	 * @param loggedInUser
	 * @throws Exception
	 * @throws ErpIntegrationException
	 */
	void createContractInErp(String contractId, ErpSetup erpSetup, User loggedInUser) throws Exception, ErpIntegrationException;

	/**
	 * @param contractId
	 * @param erpSetup
	 * @param loggedInUser
	 * @throws Exception
	 * @throws ErpIntegrationException
	 */
	void updateContractInErp(String contractId, ErpSetup erpSetup, User loggedInUser) throws Exception, ErpIntegrationException;


	Boolean sendPoAcceptanceToSap(Po po, PoStatus poStatus, Boolean isFromScheduler, String remarks) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException;
}

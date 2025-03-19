package com.privasia.procurehere.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.privasia.procurehere.core.entity.RfaEventAward;
import com.privasia.procurehere.core.entity.RfaSupplierBqItem;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.pojo.AwardResponsePojo;

import net.sf.jasperreports.engine.JasperPrint;

public interface RfaAwardService {

	RfaEventAward saveEventAward(RfaEventAward rftEventAward, HttpSession session, User loggedInUser, Boolean transfer, Boolean conclude) throws IOException;

	RfaEventAward rfaEventAwardByEventIdandBqId(String eventId, String bqId);

	RfaSupplierBqItem getBqItemByBqItemId(String bqItemId, String supplierId, String tenantId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfaEventAward> getRfaEventAwardsByEventId(String eventId);

	/**
	 * @param eventId
	 * @param tenantId
	 * @param user
	 * @param session
	 * @param rfaEventAwardId
	 * @param transfer
	 * @throws Exception
	 */
	void transferRfaAward(String eventId, String tenantId, String rfaEventAwardId, HttpSession session, User user, Boolean transfer, RfxTypes eventType) throws Exception;

	void downloadAwardAuditSnapshot(String id, HttpServletResponse response) throws IOException;

	void downloadAwardAuditExcelSnapShot(String id, HttpServletResponse response) throws IOException;

	/**
	 * @param id
	 * @param response
	 * @throws IOException
	 */
	void downloadAwardAttachFileSnapShot(String id, HttpServletResponse response) throws IOException;

	/**
	 * @param id
	 * @param response
	 * @throws IOException
	 */
	void downloadAwardAttachFile(String id, HttpServletResponse response) throws IOException;

	/**
	 * @param eventId
	 * @param bqId
	 * @return
	 */
	RfaEventAward rfaEventAwardDetailsByEventIdandBqId(String eventId, String bqId);

	Boolean updateEventAwardApproval(RfaEventAward eventAward, User loggedInUser, String eventId);

	/**
	 * @param dbRfaEventAward
	 * @param session
	 * @param loggedInUser
	 * @param awardResponsePojo
	 * @param transfer
	 * @param conclude
	 * @return
	 */
	JasperPrint getAwardSnapShotPdf(RfaEventAward dbRfaEventAward, HttpSession session, User loggedInUser, AwardResponsePojo awardResponsePojo, Boolean transfer, Boolean conclude);

	/**
	 * @param dbRfaEventAward
	 * @param session
	 * @param loggedInUser
	 * @param awardResponsePojo
	 * @param transfer
	 * @param conclude
	 * @return
	 * @throws IOException
	 */
	ByteArrayOutputStream getAwardSnapShotXcel(RfaEventAward dbRfaEventAward, HttpSession session, User loggedInUser, AwardResponsePojo awardResponsePojo, Boolean transfer, Boolean conclude) throws IOException;

}

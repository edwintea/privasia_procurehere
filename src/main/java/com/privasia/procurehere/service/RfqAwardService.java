package com.privasia.procurehere.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.privasia.procurehere.core.entity.RfqEventAward;
import com.privasia.procurehere.core.entity.RfqSupplierBqItem;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.pojo.AwardResponsePojo;

import net.sf.jasperreports.engine.JasperPrint;

public interface RfqAwardService {

	RfqEventAward saveEventAward(RfqEventAward rfqEventAward, HttpSession session, User loggedInUser, Boolean transfer, Boolean conclude);

	RfqEventAward rfqEventAwardByEventIdandBqId(String eventId, String bqId);

	RfqSupplierBqItem getBqItemByBqItemId(String bqItemId, String supplierId, String tenantId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfqEventAward> getRfqEventAwardsByEventId(String eventId);

	/**
	 * @param eventId
	 * @param tenantId
	 * @param rfqEventAwardId
	 * @param user
	 * @param session
	 * @param transfer
	 * @throws Exception
	 */
	void transferRfqAward(String eventId, String tenantId, HttpSession session, User user, String rfqEventAwardId, Boolean transfer, RfxTypes eventType) throws Exception;

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
	RfqEventAward rfqEventAwardDetailsByEventIdandBqId(String eventId, String bqId);

	Boolean updateEventAwardApproval(RfqEventAward eventAward, User loggedInUser, String eventId);

	/**
	 * @param dbRfqEventAward
	 * @param session
	 * @param loggedInUser
	 * @param awardResponsePojo
	 * @param transfer
	 * @param conclude
	 * @return
	 */
	JasperPrint getAwardSnapShotPdf(RfqEventAward dbRfqEventAward, HttpSession session, User loggedInUser, AwardResponsePojo awardResponsePojo, Boolean transfer, Boolean conclude);

	/**
	 * @param dbRfqEventAward
	 * @param session
	 * @param loggedInUser
	 * @param awardResponsePojo
	 * @param transfer
	 * @param conclude
	 * @return
	 * @throws IOException
	 */
	ByteArrayOutputStream getAwardSnapShotXcel(RfqEventAward dbRfqEventAward, HttpSession session, User loggedInUser, AwardResponsePojo awardResponsePojo, Boolean transfer, Boolean conclude) throws IOException;

}

package com.privasia.procurehere.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.privasia.procurehere.core.entity.RfpEventAward;
import com.privasia.procurehere.core.entity.RfpSupplierBqItem;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.pojo.AwardResponsePojo;

import net.sf.jasperreports.engine.JasperPrint;

public interface RfpAwardService {

	// RfpEventAward saveEventAward(RfpEventAward rftEventAward,HttpSession session, User loggedInUser);

	RfpEventAward rfpEventAwardByEventIdandBqId(String eventId, String bqId);

	RfpSupplierBqItem getBqItemByBqItemId(String bqItemId, String supplierId, String tenantId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfpEventAward> getRfpEventAwardsByEventId(String eventId);

	/**
	 * @param eventId
	 * @param tenantId
	 * @param rfpEventAwardId
	 * @param transfer
	 * @throws Exception
	 */
	void transferRfpAward(String eventId, String tenantId, HttpSession session, User loggedInUser, String rfpEventAwardId, Boolean transfer, RfxTypes eventType) throws Exception;

	void downloadAwardAuditSnapshot(String id, HttpServletResponse response) throws Exception;

	void downloadAwardAuditExcelSnapShot(String id, HttpServletResponse response) throws Exception;

	RfpEventAward saveEventAward(RfpEventAward rfpEventAward, HttpSession session, User loggedInUser, Boolean trasnfer, Boolean conclude);

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
	RfpEventAward rfpEventAwardDetailsByEventIdandBqId(String eventId, String bqId);

	Boolean updateEventAwardApproval(RfpEventAward eventAward, User loggedInUser, String eventId);

	/**
	 * @param dbRfpEventAward
	 * @param session
	 * @param loggedInUser
	 * @param awardResponsePojo
	 * @param transfer
	 * @param conclude
	 * @return
	 */
	JasperPrint getAwardSnapShotPdf(RfpEventAward dbRfpEventAward, HttpSession session, User loggedInUser, AwardResponsePojo awardResponsePojo, Boolean transfer, Boolean conclude);

	/**
	 * @param dbRfpEventAward
	 * @param session
	 * @param loggedInUser
	 * @param awardResponsePojo
	 * @param transfer
	 * @param conclude
	 * @return
	 * @throws IOException
	 */
	ByteArrayOutputStream getAwardSnapShotXcel(RfpEventAward dbRfpEventAward, HttpSession session, User loggedInUser, AwardResponsePojo awardResponsePojo, Boolean transfer, Boolean conclude) throws IOException;

}

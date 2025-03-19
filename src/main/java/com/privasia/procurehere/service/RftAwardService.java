package com.privasia.procurehere.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.privasia.procurehere.core.entity.RftEventAward;
import com.privasia.procurehere.core.entity.RftSupplierBqItem;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.pojo.AwardResponsePojo;

import net.sf.jasperreports.engine.JasperPrint;

public interface RftAwardService {

	RftEventAward saveEventAward(RftEventAward rftEventAward, HttpSession session, User loggedInUser, Boolean transfer, Boolean conclude);

	RftEventAward rftEventAwardByEventIdandBqId(String eventId, String bqId);

	RftSupplierBqItem getBqItemByBqItemId(String bqItemId, String supplierId, String tenantId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RftEventAward> getRftEventAwardsByEventId(String eventId);

	/**
	 * @param eventId
	 * @param tenantId
	 * @param user
	 * @param session
	 * @param rftEventAwardId
	 * @param transfer
	 * @throws Exception
	 */
	void transferRftAward(String eventId, String tenantId, HttpSession session, User user, String rftEventAwardId, Boolean transfer, RfxTypes eventType) throws Exception;

	/**
	 * @param id
	 * @param response TODO
	 * @throws Exception
	 */
	void downloadAwardAuditSnapshot(String id, HttpServletResponse response) throws Exception;

	void downloadAwardAuditExcelSnapShot(String id, HttpServletResponse response) throws Exception;

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
	RftEventAward rftEventAwardDetailsByEventIdandBqId(String eventId, String bqId);

	Boolean updateEventAwardApproval(RftEventAward eventAward, User loggedInUser, String eventId);

	/**
	 * @param dbRftEventAward
	 * @param session
	 * @param loggedInUser
	 * @param awardResponsePojo
	 * @param transfer
	 * @param conclude
	 * @return
	 */
	JasperPrint getAwardSnapShotPdf(RftEventAward dbRftEventAward, HttpSession session, User loggedInUser, AwardResponsePojo awardResponsePojo, Boolean transfer, Boolean conclude);

	/**
	 * @param dbRftEventAward
	 * @param session
	 * @param loggedInUser
	 * @param awardResponsePojo
	 * @param transfer
	 * @param conclude
	 * @return
	 * @throws IOException
	 */
	ByteArrayOutputStream getAwardSnapshotExcel(RftEventAward dbRftEventAward, HttpSession session, User loggedInUser, AwardResponsePojo awardResponsePojo, Boolean transfer, Boolean conclude) throws IOException;

}

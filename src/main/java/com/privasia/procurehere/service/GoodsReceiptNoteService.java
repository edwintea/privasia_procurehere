package com.privasia.procurehere.service;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.context.NoSuchMessageException;

import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.GoodsReceiptNote;
import com.privasia.procurehere.core.entity.GoodsReceiptNoteItem;
import com.privasia.procurehere.core.entity.Po;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.GrnStatus;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.EmailException;
import com.privasia.procurehere.core.pojo.ErpGrnPojo;
import com.privasia.procurehere.core.pojo.GoodsReceiptNotePojo;
import com.privasia.procurehere.core.pojo.GoodsReceiptNoteSearchPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

import net.sf.jasperreports.engine.JasperPrint;

/**
 * @author ravi
 */
public interface GoodsReceiptNoteService {

	/**
	 * @param tenantId
	 * @param input
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	List<GoodsReceiptNotePojo> findAllSearchFilterGrns(String tenantId, TableDataInput input, Date startDate, Date endDate);

	/**
	 * @param tenantId
	 * @param input
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	long findTotalSearchFilterGrns(String tenantId, TableDataInput input, Date startDate, Date endDate);

	/**
	 * @param id
	 * @return
	 */
	GoodsReceiptNote findByGrnId(String id);

	/**
	 * @param erpGrnPojo
	 * @param buyer TODO
	 * @return TODO
	 * @throws ApplicationException
	 * @throws NoSuchMessageException
	 */
	GoodsReceiptNote createGrn(ErpGrnPojo erpGrnPojo, Buyer buyer) throws NoSuchMessageException, ApplicationException;

	/**
	 * @param grnId
	 * @param loggedInUser
	 * @param buyerRemark
	 * @return
	 * @throws EmailException
	 */
	GoodsReceiptNote acceptGrn(String grnId, User loggedInUser, String buyerRemark) throws EmailException;

	/**
	 * @param grnId
	 * @param loggedInUser
	 * @param supplierRemark
	 * @return
	 * @throws EmailException
	 */
	GoodsReceiptNote declineGrn(String grnId, User loggedInUser, String supplierRemark) throws EmailException;

	/**
	 * @param grnId
	 * @param loggedInUser
	 * @param supplierRemark
	 * @return
	 */
	GoodsReceiptNote cancelGrn(String grnId, User loggedInUser, String supplierRemark);

	/**
	 * @param id
	 * @return
	 */
	GoodsReceiptNote loadGrnById(String id);

	/**
	 * @param loggedInUserTenantId
	 * @param input
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	List<GoodsReceiptNotePojo> findAllSearchFilterGrnsForSupplier(String loggedInUserTenantId, TableDataInput input, Date startDate, Date endDate);

	/**
	 * @param loggedInUserTenantId
	 * @param input
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	long findTotalSearchFilterGrnForSupplier(String loggedInUserTenantId, TableDataInput input, Date startDate, Date endDate);

	/**
	 * @param loggedInUserTenantId
	 * @param grnIds
	 * @param goodsReceiptNotePojo
	 * @param select_all
	 * @param startDate
	 * @param endDate
	 * @param sdf
	 * @return
	 */
	List<GoodsReceiptNotePojo> getAllBuyerGrnDetailsForExcelReport(String loggedInUserTenantId, String[] grnIds, GoodsReceiptNotePojo goodsReceiptNotePojo, boolean select_all, Date startDate, Date endDate, SimpleDateFormat sdf);

	/**
	 * @param loggedInUserTenantId
	 * @param grnIds
	 * @param goodsReceiptNotePojo
	 * @param select_all
	 * @param startDate
	 * @param endDate
	 * @param sdf
	 * @return
	 */
	List<GoodsReceiptNotePojo> getAllSupplierGrnDetailsForExcelReport(String loggedInUserTenantId, String[] grnIds, GoodsReceiptNotePojo goodsReceiptNotePojo, boolean select_all, Date startDate, Date endDate, SimpleDateFormat sdf);

	/**
	 * @param goodsReceiptNote
	 * @param attribute
	 * @return
	 */
	JasperPrint getGeneratedBuyerGrnPdf(GoodsReceiptNote goodsReceiptNote, String attribute);

	/**
	 * @param goodsReceiptNote
	 * @param strTimeZone
	 * @return
	 */
	JasperPrint getGeneratedSupplierGrnPdf(GoodsReceiptNote goodsReceiptNote, String strTimeZone);

	/**
	 * @param loggedInUser
	 * @param po
	 * @return
	 * @throws ApplicationException
	 */
	GoodsReceiptNote createGrnFromPo(User loggedInUser, Po po) throws ApplicationException;

	/**
	 * @param grnId
	 * @param loggedInUser
	 * @return
	 */
	GoodsReceiptNote sendGrn(String grnId, User loggedInUser);

	/**
	 * @param itemList
	 * @param grnName
	 * @param referenceNumber
	 * @param loggedInUser
	 * @param deliveryAddressTitle
	 * @param deliveryAddressLine1
	 * @param deliveryAddressLine2
	 * @param deliveryAddressCity
	 * @param deliveryAddressState
	 * @param deliveryAddressZip
	 * @param deliveryAddressCountry
	 * @param status TODO
	 * @param session TODO
	 * @param deliveryReceiver TODO
	 * @param receiptDate TODO
	 * @return
	 */
	GoodsReceiptNote updateGrnDetails(List<GoodsReceiptNoteItem> itemList, String grnName, String referenceNumber, User loggedInUser, String deliveryAddressTitle, String deliveryAddressLine1, String deliveryAddressLine2, String deliveryAddressCity, String deliveryAddressState, String deliveryAddressZip, String deliveryAddressCountry, GrnStatus status, HttpSession session, String deliveryReceiver, Date receiptDate);

	/**
	 * @param poId
	 * @return
	 */
	List<GoodsReceiptNotePojo> getGrnListByPoIdForBuyer(String poId);

	/**
	 * @param poId
	 * @return
	 */
	long findTotalGrnForPo(String poId);

	/**
	 * @param poId
	 * @return
	 */
	long findTotalDraftOrReceivedGrnForPo(String poId);

	/**
	 * @param response
	 * @param file
	 * @param teanantId
	 * @param goodsReceiptNotePojo TODO
	 * @param select_all TODO
	 * @param startDate TODO
	 * @param endDate TODO
	 * @param grnArr
	 * @param formatter 
	 */
	public void downloadCsvFileForGrnList(HttpServletResponse response, File file, String teanantId, GoodsReceiptNoteSearchPojo goodsReceiptNotePojo, boolean select_all, Date startDate, Date endDate, String[] grnArr, DateFormat formatter);

	/**
	 * @param poId
	 * @return
	 */
	long findTotalDraftOrReceivedGrnForPoByPoId(String poId);

	/**
	 * @param tenantId
	 * @param input
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	List<GoodsReceiptNotePojo> findAllSearchFilterGrnsSupp(String tenantId, TableDataInput input, Date startDate, Date endDate);
	/**
	 * @param tenantId
	 * @param input
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	long findTotalSearchFilterGrnsSupp(String tenantId, TableDataInput input, Date startDate, Date endDate);

}
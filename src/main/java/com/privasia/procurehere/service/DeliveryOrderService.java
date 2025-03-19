package com.privasia.procurehere.service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.privasia.procurehere.core.entity.Buyer;
import com.privasia.procurehere.core.entity.DeliveryOrder;
import com.privasia.procurehere.core.entity.DeliveryOrderItem;
import com.privasia.procurehere.core.entity.Po;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.EmailException;
import com.privasia.procurehere.core.pojo.DoSupplierPojo;
import com.privasia.procurehere.core.pojo.ErpDoPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

import net.sf.jasperreports.engine.JasperPrint;

/**
 * @author pooja
 */
public interface DeliveryOrderService {
	/**
	 * @param tenantId
	 * @param input
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	List<DoSupplierPojo> findAllSearchFilterDoForSupplier(String tenantId, TableDataInput input, Date startDate, Date endDate);

	/**
	 * @param tenantId
	 * @param input
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	long findTotalSearchFilterDoForSupplier(String tenantId, TableDataInput input, Date startDate, Date endDate);

	/**
	 * @param tenantId
	 * @return
	 */
	long findTotalDoForSupplier(String tenantId);

	/**
	 * @param loggedInUser
	 * @param po
	 * @return
	 * @throws ApplicationException
	 * @throws EmailException
	 */
	DeliveryOrder createDo(User loggedInUser, Po po) throws ApplicationException, EmailException;

	/**
	 * @param doId
	 * @return
	 */
	DeliveryOrder getDoByIdForSupplierView(String doId);

	/**
	 * @param doId
	 * @return
	 */
	List<DeliveryOrderItem> findAllDoItemByDoIdForSummary(String doId);

	/**
	 * @param tenantId
	 * @param input
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	List<DoSupplierPojo> findAllSearchFilterDoForBuyer(String tenantId, TableDataInput input, Date startDate, Date endDate);

	/**
	 * @param tenantId
	 * @param input
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	long findTotalSearchFilterDoForBuyer(String tenantId, TableDataInput input, Date startDate, Date endDate);

	/**
	 * @param tenantId
	 * @return
	 */
	long findTotalDoForBuyer(String tenantId);

	/**
	 * @param doId
	 * @param loggedInUser
	 * @return TODO
	 */
	DeliveryOrder finishDeliverOrder(String doId, User loggedInUser);

	/**
	 * @param doId
	 * @param loggedInUser
	 * @param buyerRemark
	 * @return
	 * @throws EmailException
	 */
	DeliveryOrder declineOrder(String doId, User loggedInUser, String buyerRemark) throws EmailException;

	/**
	 * @param doId
	 * @param loggedInUser
	 * @param buyerRemark
	 * @return
	 * @throws EmailException
	 */
	DeliveryOrder acceptOrder(String doId, User loggedInUser, String buyerRemark) throws EmailException;

	/**
	 * @param doId
	 * @param loggedInUser
	 * @param supplierRemark
	 * @return
	 * @throws EmailException
	 */
	DeliveryOrder cancelDo(String doId, User loggedInUser, String supplierRemark) throws EmailException;

	/**
	 * @param doId
	 * @return
	 */
	DeliveryOrder findByDoId(String doId);

	/**
	 * @param deliveryOrder
	 * @param attribute
	 * @return
	 */
	JasperPrint getGeneratedBuyerDoPdf(DeliveryOrder deliveryOrder, String attribute);

	/**
	 * @param doId
	 * @return
	 */
	List<DeliveryOrderItem> findAllDoItemByDoId(String doId);

	/**
	 * @param deliveryOrder
	 * @param strTimeZone
	 * @return
	 */
	JasperPrint getGeneratedSupplierDoPdf(DeliveryOrder deliveryOrder, String strTimeZone);

	List<DoSupplierPojo> getAllBuyerDoDetailsForExcelReport(String tenantId, String[] doIds, DoSupplierPojo doSupplierPojo, boolean select_all, Date startDate, Date endDate, SimpleDateFormat sdf);

	List<DoSupplierPojo> getAllSupplierDoDetailsForExcelReport(String tenantId, String[] doIds, DoSupplierPojo doSupplierPojo, boolean select_all, Date startDate, Date endDate, SimpleDateFormat sdf);

	/**
	 * @param poId
	 * @return
	 */
	List<DoSupplierPojo> getDosByPoId(String poId);

	/**
	 * @param poId
	 * @return
	 */
	List<DoSupplierPojo> getDosByPoIdForBuyer(String poId);

	/**
	 * @param poId
	 * @return
	 */
	long findTotalDoForBuyerPo(String poId);

	/**
	 * @param erpDoPojo
	 * @param buyer
	 * @return
	 * @throws ApplicationException
	 * @throws EmailException
	 */
	DeliveryOrder createDoFromErp(ErpDoPojo erpDoPojo, Buyer buyer) throws ApplicationException, EmailException;

	/**
	 * @param response
	 * @param file
	 * @param doIdArr
	 * @param startDate
	 * @param endDate
	 * @param doSupplierPojo
	 * @param select_all
	 * @param tenantId
	 */
	void downloadCsvFileForDoList(HttpServletResponse response, File file, String[] doIdArr, Date startDate, Date endDate, DoSupplierPojo doSupplierPojo, boolean select_all, String tenantId, HttpSession session);

	/**
	 * @param response
	 * @param file
	 * @param doIds
	 * @param startDate
	 * @param endDate
	 * @param doSupplierPojo
	 * @param select_all
	 * @param tenantId
	 */
	void downloadCsvFileForSupplierDoList(HttpServletResponse response, File file, String[] doIds, Date startDate, Date endDate, DoSupplierPojo doSupplierPojo, boolean select_all, String tenantId, HttpSession session);

	/**
	 * @param poId
	 * @return
	 */
	long findTotalDoForBuyerPoById(String poId);

}
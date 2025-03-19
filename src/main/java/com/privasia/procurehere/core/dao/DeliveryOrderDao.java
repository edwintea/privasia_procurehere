package com.privasia.procurehere.core.dao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.privasia.procurehere.core.entity.DeliveryOrder;
import com.privasia.procurehere.core.pojo.DoSupplierPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author pooja
 */
public interface DeliveryOrderDao extends GenericDao<DeliveryOrder, String> {
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
	 * @param doId
	 * @return
	 */
	DeliveryOrder findByDoId(String doId);

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
	 * @param poId
	 * @return
	 */
	long findTotalDoForPo(String poId);

	List<DoSupplierPojo> getAllBuyerDoDetailsForExcelReport(String tenantId, String[] doIds, DoSupplierPojo doSupplierPojo, boolean select_all, Date startDate, Date endDate);

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
	 * @param tenantId
	 * @param pageSize
	 * @param pageNo
	 * @param doIdArr
	 * @param doSupplierPojo
	 * @param select_all
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	List<DoSupplierPojo> findDoForTenantIdForCsv(String tenantId, int pageSize, int pageNo, String[] doIdArr, DoSupplierPojo doSupplierPojo, boolean select_all, Date startDate, Date endDate);

	/**
	 * @param tenantId
	 * @param pageSize
	 * @param pageNo
	 * @param doIds
	 * @param doSupplierPojo
	 * @param select_all
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	List<DoSupplierPojo> findDoForTenantIdForSupplierCsv(String tenantId, int pageSize, int pageNo, String[] doIds, DoSupplierPojo doSupplierPojo, boolean select_all, Date startDate, Date endDate);

	/**
	 * @param poId
	 * @return
	 */
	long findTotalDoForBuyerPoById(String poId);
}

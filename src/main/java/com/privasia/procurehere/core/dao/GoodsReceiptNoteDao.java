package com.privasia.procurehere.core.dao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.privasia.procurehere.core.entity.GoodsReceiptNote;
import com.privasia.procurehere.core.pojo.GoodsReceiptNotePojo;
import com.privasia.procurehere.core.pojo.GoodsReceiptNoteSearchPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author ravi
 */
public interface GoodsReceiptNoteDao extends GenericDao<GoodsReceiptNote, String> {

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
	List<GoodsReceiptNotePojo> getAllBuyerGrnDetailsForExcelReport(String loggedInUserTenantId, String[] grnIds, GoodsReceiptNoteSearchPojo goodsReceiptNotePojo, boolean select_all, Date startDate, Date endDate, SimpleDateFormat sdf);

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
	 * @param tenantId
	 * @return
	 */
	long findTotalGrnCountByTenantIdForBuyer(String tenantId);

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

	void transferOwnership(String fromUser, String toUser);
}

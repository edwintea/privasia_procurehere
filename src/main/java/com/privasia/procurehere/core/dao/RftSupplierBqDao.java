package com.privasia.procurehere.core.dao;

import java.math.BigDecimal;
import java.util.List;

import com.privasia.procurehere.core.entity.RftSupplierBq;
import com.privasia.procurehere.core.pojo.EvaluationSuppliersBqPojo;
import com.privasia.procurehere.core.pojo.RfaSupplierBqPojo;

/**
 * @author Vipul
 */
public interface RftSupplierBqDao extends GenericDao<RftSupplierBq, String> {

	/**
	 * D
	 * 
	 * @param id
	 * @param bqId
	 * @param supplierId
	 * @return
	 */
	RftSupplierBq findBqByEventIdAndSupplierId(String id, String bqId, String supplierId);

	/**
	 * @param id
	 * @param supplierId
	 * @return
	 */
	RftSupplierBq findBqByBqId(String id, String supplierId);

	/**
	 * @param supplierId
	 * @param eventId
	 * @return
	 */
	RftSupplierBq findSupplierBqByEventIdSupplierId(String eventId, String supplierId);

	/**
	 * @param eventId
	 */
	void deleteSupplierBqsForEvent(String eventId);

	List<RftSupplierBq> findRftSupplierBqbyEventId(String eventId);

	List<RftSupplierBq> findRftSupplierBqbyEventIdAndSupplierId(String eventId, String supplierId);

	/**
	 * @param eventId
	 * @param supplierId
	 * @return
	 */
	List<BigDecimal> grandTotalOfBqByEventIdAndSupplierId(String eventId, String supplierId);

	/**
	 * @param eventId
	 * @param supplierId
	 * @return
	 */
	List<RftSupplierBq> rftSupplierBqAnswerdByEventIdAndSupplierId(String eventId, String supplierId);

	List<RfaSupplierBqPojo> getAllRfaTopEventSuppliersIdByEventId(String id, int i, String id2);

	List<RfaSupplierBqPojo> findRftSupplierBqBySupplierIdsOdrByRank(String eventId, Integer limitSupplier);

	List<RfaSupplierBqPojo> findRftSupplierBqCompleteNessBySupplierIdsOdrByRank(String id, Integer object);

	List<EvaluationSuppliersBqPojo> getAllBqsByBqIdsAndEventId(String bqId, String id);

	List<RfaSupplierBqPojo> findRftSupplierBqCompleteNessBySupplierIds(String id);

	RftSupplierBq findBqByEventIdAndSupplierIdOfQualifiedSupplier(String id, String bqId, String supplierId);

	List<RfaSupplierBqPojo> getAllRftTopCompletedEventSuppliersIdByEventId(String eventId, Integer limit, String bqId);

	List<RfaSupplierBqPojo> findRftSupplierParticipation(String eventId);

	List<RftSupplierBq> findRftSummarySupplierBqbyEventId(String id);

	RftSupplierBq findRftSupplierBqStatusbyEventIdAndSupplierId(String supplierId, String eventId, String bqId);

	/**
	 * @param eventId
	 * @param supplierId
	 * @return
	 */
	long findPendingBqAnswerdByEventIdAndSupplierId(String eventId, String supplierId);

}

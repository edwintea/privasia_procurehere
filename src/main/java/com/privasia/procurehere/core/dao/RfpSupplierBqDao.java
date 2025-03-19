package com.privasia.procurehere.core.dao;

import java.math.BigDecimal;
import java.util.List;

import com.privasia.procurehere.core.entity.RfpSupplierBq;
import com.privasia.procurehere.core.pojo.EvaluationSuppliersBqPojo;
import com.privasia.procurehere.core.pojo.RfaSupplierBqPojo;

public interface RfpSupplierBqDao extends GenericDao<RfpSupplierBq, String> {

	/**
	 * @param id
	 * @param name
	 * @param string
	 * @return
	 */
	RfpSupplierBq findBqByEventIdAndBqName(String id, String bqId, String supplierId);

	/**
	 * @param id
	 * @param supplierId TODO
	 * @return
	 */
	RfpSupplierBq findBqByBqId(String id, String supplierId);

	void deleteSupplierBqsForEvent(String eventId);

	List<RfpSupplierBq> findRfpSupplierBqbyEventId(String eventId);

	List<RfpSupplierBq> findRfpSupplierBqbyEventIdAndSupplierId(String eventId, String supplierId);

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
	List<RfpSupplierBq> rfpSupplierBqAnswerdByEventIdAndSupplierId(String eventId, String supplierId);

	RfpSupplierBq findBqByEventIdAndSupplierId(String id, String id2, String id3);

	List<RfaSupplierBqPojo> getAllRfaTopEventSuppliersIdByEventId(String eventId, int limit, String bqId);

	List<RfaSupplierBqPojo> findRfpSupplierBqBySupplierIdsOdrByRank(String eventId, Integer limitSupplier);

	List<RfaSupplierBqPojo> findRfpSupplierBqCompleteNessBySupplierIdsOdrByRank(String eventId, Integer limitSupplier);

	List<EvaluationSuppliersBqPojo> getAllBqsByBqIdsAndEventId(String bqId, String id);

	List<RfaSupplierBqPojo> findRfaSupplierBqCompleteNessBySupplierIds(String id);

	RfpSupplierBq findBqByEventIdAndSupplierIdOfQualifiedSupplier(String id, String bqId, String supplierId);

	List<RfaSupplierBqPojo> getAllRfpTopCompletedEventSuppliersIdByEventId(String eventId, Integer limit, String bqId);

	List<RfaSupplierBqPojo> findRfpSupplierParticipation(String id);

	List<RfpSupplierBq> findRfpSummarySupplierBqbyEventId(String eventId);

	RfpSupplierBq findRfpSupplierBqStatusbyEventIdAndSupplierId(String supplierId, String eventId, String bqId);

	/**
	 * @param eventId
	 * @param supplierId
	 * @return
	 */
	long findPendingBqAnswerdByEventIdAndSupplierId(String eventId, String supplierId);

}

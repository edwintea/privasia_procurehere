package com.privasia.procurehere.core.dao;

import java.math.BigDecimal;
import java.util.List;

import com.privasia.procurehere.core.entity.RfqSupplierBq;
import com.privasia.procurehere.core.pojo.EvaluationSuppliersBqPojo;
import com.privasia.procurehere.core.pojo.RfaSupplierBqPojo;

public interface RfqSupplierBqDao extends GenericDao<RfqSupplierBq, String> {

	/**
	 * @param id
	 * @param name
	 * @return
	 */
	RfqSupplierBq findBqByEventIdAndBqName(String id, String bqId, String supplierId);

	/**
	 * @param id
	 * @param supplierId TODO
	 * @return
	 */
	RfqSupplierBq findBqByBqId(String id, String supplierId);

	/**
	 * @param eventId
	 */
	void deleteSupplierBqsForEvent(String eventId);

	List<RfqSupplierBq> findRfqSupplierBqbyEventId(String eventId);

	List<RfqSupplierBq> findRfqSupplierBqbyEventIdAndSupplierId(String eventId, String supplierId);

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
	List<RfqSupplierBq> rfqSupplierBqAnswerdByEventIdAndSupplierId(String eventId, String supplierId);

	RfqSupplierBq findBqByEventIdAndSupplierId(String eventId, String id2, String id3);

	List<RfaSupplierBqPojo> getAllRfqTopEventSuppliersIdByEventId(String id, int i, String id2);

	List<RfaSupplierBqPojo> findRfqTopSupplierBqBySupplierIdsOdrByRank(String eventId, int supplierLimit);

	List<RfqSupplierBq> findSupplierBqbyEventId(String id);

	List<RfaSupplierBqPojo> getAllRfqSuppliersIdByEventId(String id);

	RfqSupplierBq getRfqSupplierBqByEventIdAndSupplierId(String eventId, String supplierId);

	RfqSupplierBq getRfaSupplierBqByEventIdAndSupplierId(String eventId, String supplierId);

	List<RfaSupplierBqPojo> findRfaSupplierBqBySupplierIdsOdrByRank(String id, Integer limitSupplier);

	List<RfaSupplierBqPojo> findRfqSupplierBqCompleteNessBySupplierIdsOdrByRank(String id, Integer limitSupplier);

	List<RfaSupplierBqPojo> findRfqSupplierBqByDisqualifiedSupplier(String eventId, Integer limitSupplier);

	List<EvaluationSuppliersBqPojo> getAllBqsByBqIdsAndEventId(String bqId, String id);

	List<RfaSupplierBqPojo> findRfqSupplierBqCompleteNessBySupplierIds(String id);

	RfqSupplierBq findQualifiedSupplierBqByEventIdAndSupplierId(String id, String bqId, String supplierId);

	List<RfaSupplierBqPojo> getAllRfqTopCompletedEventSuppliersIdByEventId(String id, Integer limit, String id2);

	List<RfaSupplierBqPojo> findRfqSupplierParticipation(String eventId);

	List<RfqSupplierBq> findRfqSummarySupplierBqbyEventId(String eventId);

	RfqSupplierBq findRfqSupplierBqStatusbyEventIdAndSupplierId(String supplierId, String eventId, String bqId);

	/**
	 * @param eventId
	 * @param supplierId
	 * @return
	 */
	long findPendingBqAnswerdByEventIdAndSupplierId(String eventId, String supplierId);

}

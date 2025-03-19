package com.privasia.procurehere.service;

import java.util.List;

import javax.servlet.http.HttpSession;

import com.privasia.procurehere.core.entity.EventSupplier;
import com.privasia.procurehere.core.entity.EventTeamMember;
import com.privasia.procurehere.core.entity.IndustryCategory;
import com.privasia.procurehere.core.entity.RfqEvent;
import com.privasia.procurehere.core.entity.RfqEventSupplier;
import com.privasia.procurehere.core.entity.RfqSupplierTeamMember;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.TeamMemberType;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.EventSupplierPojo;
import com.privasia.procurehere.core.pojo.FeePojo;
import com.privasia.procurehere.core.pojo.SupplierSearchPojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;

public interface RfqEventSupplierService {

	/**
	 * @param eventSupplier
	 * @return
	 */
	String saveEventSuppliers(RfqEventSupplier eventSupplier);

	/**
	 * @param eventSupplier
	 * @return
	 */
	boolean isExists(RfqEventSupplier eventSupplier);

	/**
	 * @param eventID
	 * @return
	 */
	List<EventSupplier> getAllSuppliersByEventId(String eventID);

	/**
	 * @param eventSupplier
	 */
	void deleteRfqEventSuppliers(RfqEventSupplier eventSupplier);

	/**
	 * @param id
	 * @return
	 */
	RfqEventSupplier findSupplierById(String id);

	/**
	 * @param supplierId
	 * @param eventId
	 * @return
	 */
	RfqEventSupplier findSupplierByIdAndEventId(String supplierId, String eventId);

	/**
	 * @param eventSuppliersupplierId
	 */
	void updateEventSuppliers(RfqEventSupplier eventSupplier);

	void removeTeamMemberfromList(String eventId, String userId, String supplierId);

	List<RfqSupplierTeamMember> addTeamMemberToList(String eventId, String userId, String supplierId, TeamMemberType memberType);

	List<EventTeamMember> getSupplierTeamMembersForEvent(String eventId, String supplierId);

	RfqEvent getRfqEventByeventId(String eventId);

	List<RfqSupplierTeamMember> getRfqSupplierTeamMembersForEvent(String eventId, String supplierId);

	/**
	 * @param id
	 * @return
	 */
	RfqEventSupplier findSupplierBySupplierId(String id);

	void addSupplierForPublicEvent(String eventId, String id);

	/**
	 * @param eventId
	 * @return
	 */
	List<Supplier> getEventSuppliersForEvaluation(String eventId);

	/**
	 * @param userId
	 * @param supplierId
	 * @param eventId
	 * @return
	 */
	EventPermissions getUserPemissionsForEvent(String userId, String supplierId, String eventId);

	/**
	 * @param eventId
	 * @param supplierId
	 * @return TODO
	 */
	int updatePrivewTime(String eventId, String supplierId);

	List<EventSupplier> getAllSuppliersByEventIdOrderByCompName(String eventId);

	/**
	 * @param eventId
	 */
	void deleteAllSuppliersByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	Integer getCountOfSupplierByEventId(String eventId);

	/**
	 * @param event
	 * @param supplierId
	 * @param session
	 * @param virtualizer TODO
	 * @return
	 */
	JasperPrint generateSupplierSummary(RfqEvent event, String supplierId, HttpSession session, JRSwapFileVirtualizer virtualizer);

	/**
	 * @param eventId
	 * @return
	 */
	List<Supplier> getEventQualifiedSuppliersForEvaluation(String eventId);

	/**
	 * @param eventSupplier
	 * @return
	 */
	RfqEventSupplier saveRfqEventSupplier(RfqEventSupplier eventSupplier);

	List<RfqEventSupplier> findDisqualifySupplierByEventId(String eventId);

	List<String> getAllRfaEventSuppliersIdByEventId(String id);

	List<EventSupplier> getAllPartiallyCompleteBidsByEventId(String id);

	List<FeePojo> getAllInvitedSuppliersByEventId(String eventId);

	List<RfqEventSupplier> getAllSuppliersByFeeEventId(String eventId, String supplierId);

	List<RfqEventSupplier> getAllSuppliersByEventIdAndSupplierCode(String id, String supplierCode, String tenantId);

	List<EventSupplierPojo> getAllEventsSupplierPojoByEventId(String eventId, TableDataInput input);

	long getAllEventsSupplierPojoCountByEventId(String eventId, TableDataInput input);

	void updateEventSuppliersNotificationFlag(String id);

	List<EventSupplierPojo> getAllDetailsForSendInvitation(String id);

	EventSupplier saveInvitedSuppliers(RfqEventSupplier eventSupplier);

	List<RfqEventSupplier> getAllSubmittedSupplierByEventId(String eventId);

	/**
	 * @param eventSupplier
	 */
	void batchInsert(List<RfqEventSupplier> eventSupplier);

	List<FeePojo> getAllInvitedSuppliersByEventId(String eventId, TableDataInput input, RfxTypes eventType);

	long getAllInvitedSuppliersFilterCountByEventId(String eventId, TableDataInput input, RfxTypes eventType);

	long getAllInvitedSuppliersCountByEventId(String eventId, RfxTypes eventType);

	List<EventSupplier> getAllSubmitedSupplierByEevntId(String eventId);

	String getEventNameByEventId(String eventId);

	List<RfqEventSupplier> findDisqualifySupplierForEvaluationReportByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfqEventSupplier> findEventSuppliersForTatReportByEventId(String eventId);

}

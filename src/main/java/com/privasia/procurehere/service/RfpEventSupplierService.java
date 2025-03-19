package com.privasia.procurehere.service;

import java.util.List;

import javax.servlet.http.HttpSession;

import com.privasia.procurehere.core.entity.EventSupplier;
import com.privasia.procurehere.core.entity.EventTeamMember;
import com.privasia.procurehere.core.entity.RfpEvent;
import com.privasia.procurehere.core.entity.RfpEventSupplier;
import com.privasia.procurehere.core.entity.RfpSupplierTeamMember;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.enums.TeamMemberType;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.EventSupplierPojo;
import com.privasia.procurehere.core.pojo.FeePojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;

public interface RfpEventSupplierService {

	/**
	 * @param rfpEventSupplier
	 * @return
	 */
	String saveRfpEventSuppliers(RfpEventSupplier rfpEventSupplier);

	/**
	 * @param rfpEventSupplier
	 * @return
	 */
	boolean isExists(RfpEventSupplier rfpEventSupplier);

	/**
	 * @param eventID
	 * @return
	 */
	List<EventSupplier> getAllSuppliersByEventId(String eventID);

	/**
	 * @param rffEventSupplier
	 */
	void deleteRfpEventSuppliers(RfpEventSupplier rffEventSupplier);

	/**
	 * @param id
	 * @return
	 */
	RfpEventSupplier findSupplierById(String id);

	/**
	 * @param supplierId
	 * @param eventId
	 * @return
	 */
	RfpEventSupplier findSupplierByIdAndEventId(String supplierId, String eventId);

	/**
	 * @param eventSupplier
	 */
	void updateEventSuppliers(RfpEventSupplier eventSupplier);

	void removeTeamMemberfromList(String eventId, String userId, String supplierId);

	List<RfpSupplierTeamMember> addTeamMemberToList(String eventId, String userId, String loggedInUserTenantId, TeamMemberType memberType);

	List<EventTeamMember> getSupplierTeamMembersForEvent(String eventId, String loggedInUserTenantId);

	RfpEvent getRfpEventByeventId(String eventId);

	List<RfpSupplierTeamMember> getRfpSupplierTeamMembersForEvent(String eventId, String supplierId);

	/**
	 * @param id
	 * @return
	 */
	RfpEventSupplier findSupplierBySupplierId(String id);

	void addSupplierForPublicEvent(String eventId, String id);

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
	 * @return
	 */
	Integer getCountOfSupplierByEventId(String eventId);

	/**
	 * @param eventId
	 */
	void deleteAllSuppliersByEventId(String eventId);

	/**
	 * @param event
	 * @param session
	 * @param virtualizer TODO
	 * @param loggedInUserTenantId
	 * @return
	 */
	JasperPrint generateSupplierSummary(RfpEvent event, String supplierId, HttpSession session, JRSwapFileVirtualizer virtualizer);

	/**
	 * @param eventId
	 * @return
	 */
	List<Supplier> getEventQualifiedSuppliersForEvaluation(String eventId);

	RfpEventSupplier saveRfpEventSupplier(RfpEventSupplier rfpEventSupplier);

	List<RfpEventSupplier> findDisqualifySupplierByEventId(String eventId);

	List<FeePojo> getAllInvitedSuppliersByEventId(String eventId);

	List<RfpEventSupplier> getAllSuppliersByFeeEventId(String eventId, String id);

	List<RfpEventSupplier> getAllSuppliersByEventIdAndSupplierCode(String id, String supplierCode, String tenantId);

	List<EventSupplierPojo> getAllEventsSupplierPojoByEventId(String eventId, TableDataInput input);

	long getAllEventsSupplierPojoCountByEventId(String eventId, TableDataInput input);

	void updateEventSuppliersNotificationFlag(String id);

	List<EventSupplierPojo> getAllDetailsForSendInvitation(String id);

	EventSupplier saveInvitedSuppliers(RfpEventSupplier eventSupplier);

	List<RfpEventSupplier> getAllSubmittedSupplierByEventId(String eventId);

	/**
	 * @param eventSuppliers
	 */
	void batchInsert(List<RfpEventSupplier> eventSuppliers);

	String getEventNameByEventId(String eventId);

	List<RfpEventSupplier> findDisqualifySupplierForEvaluationReportByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfpEventSupplier> findEventSuppliersForTatReportByEventId(String eventId);

}

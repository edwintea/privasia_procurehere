package com.privasia.procurehere.service;

import java.util.List;

import javax.servlet.http.HttpSession;

import com.privasia.procurehere.core.entity.EventDocument;
import com.privasia.procurehere.core.entity.EventSupplier;
import com.privasia.procurehere.core.entity.EventTeamMember;
import com.privasia.procurehere.core.entity.RfiEvent;
import com.privasia.procurehere.core.entity.RfiEventSupplier;
import com.privasia.procurehere.core.entity.RfiSupplierTeamMember;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.enums.TeamMemberType;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.EventSupplierPojo;
import com.privasia.procurehere.core.pojo.FeePojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;

public interface RfiEventSupplierService {

	/**
	 * @param rfiEventSupplier
	 * @return
	 */
	String saveRfiEventSuppliers(RfiEventSupplier rfiEventSupplier);

	/**
	 * @param rfiEventSupplier
	 * @return
	 */
	boolean isExists(RfiEventSupplier rfiEventSupplier);

	/**
	 * @param eventID
	 * @return
	 */
	List<EventSupplier> getAllSuppliersByEventId(String eventID);

	/**
	 * @param rfiEventSupplier
	 */
	void deleteRfiEventSuppliers(RfiEventSupplier rfiEventSupplier);

	/**
	 * @param id
	 * @return
	 */
	RfiEventSupplier findSupplierById(String id);

	/**
	 * @param supplierId
	 * @param eventId
	 * @return
	 */
	RfiEventSupplier findSupplierByIdAndEventId(String supplierId, String eventId);

	/**
	 * @param eventSupplier
	 */
	void updateEventSuppliers(RfiEventSupplier eventSupplier);

	void removeTeamMembersfromList(String eventId, String userId, String supplierId);

	List<EventTeamMember> getSupplierTeamMembersForEvent(String eventId, String loggedInUserTenantId);

	List<RfiSupplierTeamMember> addTeamMemberToList(String eventId, String userId, String loggedInUserTenantId, TeamMemberType memberType);

	List<?> getRfiSupplierTeamMembersForEvent(String eventId, String loggedInUserTenantId);

	/**
	 * @param id
	 * @return
	 */
	RfiEventSupplier findSupplierBySupplierId(String id);

	/**
	 * @param eventId
	 * @param id
	 */
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
	JasperPrint generateSupplierSummary(RfiEvent event, String supplierId, HttpSession session, JRSwapFileVirtualizer virtualizer);

	/**
	 * 
	 * @param eventId
	 * @return
	 */
	List<Supplier> getEventQualifiedSuppliersForEvaluation(String eventId);

	RfiEventSupplier saveRfiEventSupplier(RfiEventSupplier rfqEventSupplier);

	List<RfiEventSupplier> findDisqualifySupplierByEventId(String eventId);

	List<FeePojo> getAllInvitedSuppliersByEventId(String eventId);

	List<RfiEventSupplier> getAllSuppliersByFeeEventId(String eventId, String supplierId);

	List<RfiEventSupplier> getAllSuppliersByEventIdAndSupplierCode(String id, String supplierCode, String tenantId);

	List<EventSupplierPojo> getAllEventsSupplierPojoByEventId(String eventId, TableDataInput input);

	long getAllEventsSupplierPojoCountByEventId(String eventId, TableDataInput input);

	void updateEventSuppliersNotificationFlag(String id);

	List<EventSupplierPojo> getAllDetailsForSendInvitation(String id);

	EventSupplier saveInvitedSuppliers(RfiEventSupplier eventSupplier);

	EventDocument findEventDocumentById(String docId);

	List<RfiEventSupplier> getAllSubmittedSupplierByEventId(String eventId);

	/**
	 * 
	 * @param eventSuppliers
	 */
	void batchInsert(List<RfiEventSupplier> eventSuppliers);

	String getEventNameByEventId(String eventId);

	List<RfiEventSupplier> findDisqualifySupplierForEvaluationReportByEventId(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<RfiEventSupplier> findEventSuppliersForTatReportByEventId(String eventId);

}

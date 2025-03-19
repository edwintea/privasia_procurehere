package com.privasia.procurehere.service;

import java.util.List;

import javax.servlet.http.HttpSession;

import com.privasia.procurehere.core.entity.EventSupplier;
import com.privasia.procurehere.core.entity.EventTeamMember;
import com.privasia.procurehere.core.entity.IndustryCategory;
import com.privasia.procurehere.core.entity.RftEvent;
import com.privasia.procurehere.core.entity.RftEventSupplier;
import com.privasia.procurehere.core.entity.RftSupplierTeamMember;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.TeamMemberType;
import com.privasia.procurehere.core.pojo.EventPermissions;
import com.privasia.procurehere.core.pojo.EventSupplierPojo;
import com.privasia.procurehere.core.pojo.FeePojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.pojo.SupplierSearchPojo;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;

public interface RftEventSupplierService {

	/**
	 * @param rftEventSupplier
	 * @return
	 */
	String saveRftEventSuppliers(RftEventSupplier rftEventSupplier);

	/**
	 * @param rftEventSupplier
	 * @return
	 */
	boolean isExists(RftEventSupplier rftEventSupplier);

	/**
	 * @param eventID
	 * @return
	 */
	List<EventSupplier> getAllSuppliersByEventId(String eventID);

	/**
	 * @param rftEventSupplier
	 */
	void deleteRftEventSuppliers(RftEventSupplier rftEventSupplier);

	/**
	 * @param id
	 * @return
	 */
	RftEventSupplier findSupplierById(String id);

	/**
	 * @param supplierId
	 * @param eventId
	 * @return
	 */
	RftEventSupplier findSupplierByIdAndEventId(String supplierId, String eventId);

	/**
	 * @param eventSupplier
	 */
	void updateEventSuppliers(RftEventSupplier eventSupplier);

	List<RftSupplierTeamMember> addTeamMemberToList(String eventId, String userId, String string, TeamMemberType memberType);

	List<EventTeamMember> getSupplierTeamMembersForEvent(String eventId, String supplierId);

	RftEvent getRftEventByeventId(String eventId);

	void removeTeamMemberfromList(String eventId, String userId, String string);

	RftSupplierTeamMember getRftTeamMemberByUserIdAndEventId(String eventId, String userId);

	List<RftSupplierTeamMember> getRftSupplierTeamMembersForEvent(String eventId, String supplierId);

	/**
	 * @param id
	 * @return
	 */
	RftEventSupplier findSupplierBySupplierId(String id);

	void addSupplierForPublicEvent(String eventId, String supplierId);

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

	/**
	 * @param eventId
	 * @return
	 */
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
	 * @param supplierId
	 * @param virtualizer TODO
	 * @return
	 */
	JasperPrint generateSupplierSummary(RftEvent event, String supplierId, HttpSession session, JRSwapFileVirtualizer virtualizer);

	/**
	 * @param eventId
	 * @return
	 */
	List<Supplier> getEventQualifiedSuppliersForEvaluation(String eventId);

	/**
	 * @param rftEventSupplier
	 * @return
	 */
	RftEventSupplier saveRftEventSupplier(RftEventSupplier rftEventSupplier);

	List<RftEventSupplier> findDisqualifySupplierByEventId(String eventId);

	List<FeePojo> getAllInvitedSuppliersByEventId(String id);

	List<RftEventSupplier> getAllSuppliersByFeeEventId(String eventId, String id);

	List<RftEventSupplier> getAllSuppliersByEventIdAndSupplierCode(String id, String supplierCode, String tenantId);

	List<EventSupplierPojo> getAllEventsSupplierPojoByEventId(String eventId, TableDataInput input);

	long getAllEventsSupplierPojoCountByEventId(String eventId, TableDataInput input);

	void updateEventSuppliersNotificationFlag(String id);

	List<EventSupplierPojo> getAllDetailsForSendInvitation(String id);

	EventSupplier saveInvitedSuppliers(RftEventSupplier eventSupplier);

	List<SupplierSearchPojo> favoriteSuppliersOfBuyerByState(String loggedInUserTenantId, SupplierSearchPojo supplierSearchPojo, List<IndustryCategory> industryCategories, Boolean exclusive, Boolean inclusive, String eventType, String eventId);

	List<SupplierSearchPojo> favoriteSuppliersOfBuyer(String loggedInUserTenantId, SupplierSearchPojo supplierSearchPojo, Boolean exclusive, Boolean inclusive, String eventType, String eventId);

	/**
	 * @param loggedInUserTenantId
	 * @param eventId
	 * @param eventType
	 * @return
	 */
	boolean isSupplierExistsForPublicEvent(String loggedInUserTenantId, String eventId, RfxTypes eventType);

	List<RftEventSupplier> getAllSubmittedSupplierByEventId(String eventId);

	/**
	 * 
	 * @param eventSuppliers
	 */
	void batchInsert(List<RftEventSupplier> eventSuppliers);

	String getEventNameByEventId(String eventId);

	List<RftEventSupplier> findDisqualifySupplierForEvaluationReportByEventId(String id);

	/**
	 * @param eventId
	 * @return
	 */
	List<RftEventSupplier> findEventSuppliersForTatReportByEventId(String eventId);

}

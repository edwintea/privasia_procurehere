package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.TimeZone;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.pojo.TableDataInput;

public interface TimeZoneDao extends GenericDao<TimeZone, String> {

	/**
	 * @param timeZone
	 * @return
	 */
	boolean isExists(TimeZone timeZone);

	/**
	 * @return
	 */
	List<TimeZone> getAllTimeZone();

	/**
	 * @param list
	 * @param createdBy
	 */
	void loadTimeZoneMasterData(List<TimeZone> list, User createdBy);

	/**
	 * @param tableParams
	 * @return
	 */
	List<TimeZone> findAllTimezones(TableDataInput tableParams);

	/**
	 * @param tableParams
	 * @return
	 */
	long findTotalFilteredTimeZones(TableDataInput tableParams);

	/**
	 * @return
	 */
	long findTotalTimeZones();

	/**
	 * @param tenantId
	 * @param search
	 * @return
	 */
	List<TimeZone> findTimeZonesForTenantId(String tenantId, String search);

	/**
	 * @param tenantId
	 * @return
	 */
	long countConstructQueryToFetchTimeZones(String tenantId);

	/**
	 * 
	 * @param country
	 * @return
	 */
	TimeZone fetchTimeZoneForCountry(String country);

}

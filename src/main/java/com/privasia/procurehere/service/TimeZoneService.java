package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.TimeZone;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.pojo.TimeZonePojo;

public interface TimeZoneService {

	/**
	 * @return
	 */
	List<TimeZone> findAllActiveTimeZone();

	/**
	 * @param timeZone
	 * @return
	 */
	public String createTimeZone(TimeZone timeZone);

	/**
	 * @param timeZone
	 */
	public void deleteTimeZone(TimeZone timeZone);

	/**
	 * @param timeZone
	 */
	public void updateTimeZone(TimeZone timeZone);

	/**
	 * @param timeZone
	 * @return
	 */
	boolean isExists(TimeZone timeZone);

	/**
	 * @param id
	 * @return
	 */
	TimeZone getTimeZoneById(String id);

	/**
	 * @return
	 */
	List<TimeZonePojo> getAllTimeZonePojo();

	/**
	 * @param input
	 * @return
	 */
	List<TimeZone> findAllTimezones(TableDataInput tableParams);

	/**
	 * @param input
	 * @return
	 */
	long findTotalFilteredTimeZones(TableDataInput tableParams);

	/**
	 * @return
	 */
	long findTotalTimeZones();

	/**
	 * @param loggedInUserTenantId
	 * @param search
	 * @return
	 */
	List<TimeZone> findTimeZonesForTenantId(String tenantId, String search);
}

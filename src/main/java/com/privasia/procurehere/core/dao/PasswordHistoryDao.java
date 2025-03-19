/**
 * 
 */
package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.PasswordHistory;

/**
 * @author Nitin Otageri
 */
public interface PasswordHistoryDao extends GenericDao<PasswordHistory, String> {

	/**
	 * @param userId
	 * @return
	 */
	List<PasswordHistory> findPasswordHistoryByUserId(String userId);

	/**
	 * @param numberOfPasswordRemember
	 * @param userId
	 * @return
	 */
	List<PasswordHistory> getPasswordHistory(Integer numberOfPasswordRemember, String userId);

}

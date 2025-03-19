/**
 * 
 */
package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.State;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.pojo.TableDataInput;

/**
 * @author jav3d
 */
public interface StateDao extends GenericDao<State, String> {

	/**
	 * @param state
	 */
	void updateState(State state);

	/**
	 * @return
	 */
	List<State> findAll();

	/**
	 * @param serviceCategory
	 * @return
	 */
	boolean isExists(State state);

	/**
	 * @param countryId
	 * @return
	 */
	List<State> statesForCountry(String countryId);

	/**
	 * @return
	 */
	List<State> getAllStatesPojo();

	/**
	 * @param list
	 * @param createdBy
	 */
	void loadStateMasterData(List<State> list, User createdBy);

	/**
	 * @param searchValue
	 * @return
	 */
	List<State> searchStatesByNameOrCode(String searchValue);

	/**
	 * @param tableParams
	 * @return
	 */
	List<State> findAllStateList(TableDataInput tableParams);

	/**
	 * @param tableParams
	 * @return
	 */
	long findTotalFilteredStateList(TableDataInput tableParams);

	/**
	 * @return
	 */
	long findTotalStateList();


	List<State> findAllActiveStatesForCountry(String countryId);

}

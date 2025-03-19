package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.State;
import com.privasia.procurehere.core.pojo.Coverage;
import com.privasia.procurehere.core.pojo.StatePojo;
import com.privasia.procurehere.core.pojo.TableDataInput;

public interface StateService {

	String createState(State state);

	void updateState(State state);

	void deletestate(State state);

	List<State> getAllStates();

	List<State> searchState(String id);

	List<State> searchStateByName(String stateName);

	State getState(String id);

		boolean isExists(State state);

	/**
	 * @param countryId
	 * @return
	 */
	List<State> statesForCountry(String countryId);

	List<StatePojo> getAllStatesPojo();

	/**
	 * @param searchValue
	 * @return
	 */
	List<State> searchStatesByNameOrCode(String searchValue);

	/**
	 * 
	 * @param tableParams
	 * @return
	 */
	List<State> findAllStateList(TableDataInput tableParams);

	/**
	 * 
	 * @param tableParams
	 * @return
	 */
	long findTotalFilteredStateList(TableDataInput tableParams);

	/**
	 * 
	 * @return
	 */
	long findTotalStateList();

	List<State> findAllActiveStatesForCountry(String countryId);

	public List<Coverage> retrieveAllStatesByCountry();
}

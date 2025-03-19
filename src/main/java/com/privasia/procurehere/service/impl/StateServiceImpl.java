package com.privasia.procurehere.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.privasia.procurehere.core.entity.Country;
import com.privasia.procurehere.core.pojo.Coverage;
import com.privasia.procurehere.service.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.OwnerAuditTrailDao;
import com.privasia.procurehere.core.dao.StateDao;
import com.privasia.procurehere.core.entity.OwnerAuditTrail;
import com.privasia.procurehere.core.entity.State;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.pojo.StatePojo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.service.StateService;

@Service
@Transactional(readOnly = true)
public class StateServiceImpl implements StateService {

	@Autowired
	StateDao stateDao;

	@Autowired
	OwnerAuditTrailDao ownerAuditTrailDao;

	@Autowired
	CountryService countryService;

	@Override
	@Transactional(readOnly = false)
	public String createState(State state) {
		OwnerAuditTrail ownerAuditTrail = new OwnerAuditTrail(AuditTypes.CREATE, "'"+state.getStateName()+"' State settings created ", state.getCreatedBy().getTenantId(), state.getCreatedBy(), new Date(),ModuleType.State);
		ownerAuditTrailDao.save(ownerAuditTrail);
		state = stateDao.saveOrUpdate(state);
		return (state != null ? state.getId() : null);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateState(State state) {
		OwnerAuditTrail ownerAuditTrail = new OwnerAuditTrail(AuditTypes.UPDATE, "'"+state.getStateName()+"' State settings updated ", state.getModifiedBy().getTenantId(), state.getModifiedBy(), new Date(),ModuleType.State);
		ownerAuditTrailDao.save(ownerAuditTrail);
		stateDao.update(state);
	}

	@Override
	@Transactional(readOnly = false)
	public void deletestate(State state) {
		String stateName=state.getStateName();
		OwnerAuditTrail ownerAuditTrail = new OwnerAuditTrail(AuditTypes.DELETE, "'"+stateName+"' State settings deleted ", state.getModifiedBy().getTenantId(), state.getModifiedBy(), new Date(),ModuleType.State);
		ownerAuditTrailDao.save(ownerAuditTrail);
		stateDao.delete(state);

	}

	@Override
	public List<State> getAllStates() {
		return stateDao.findAll();
	}

	@Override
	public List<State> searchState(String id) {
		return stateDao.findAllByProperty(id, State.class);
	}

	@Override
	public List<State> searchStateByName(String stateName) {
		return stateDao.findAllByProperty(stateName, State.class);
	}

	@Override
	public State getState(String id) {
		State state = stateDao.findById(id);
		if (state != null && state.getCountry() != null)
			state.getCountry().getCountryCode();

		return state;
	}

	@Override
	public boolean isExists(State state) {
		return stateDao.isExists(state);
	}

	@Override
	public List<State> statesForCountry(String countryId) {
		return stateDao.statesForCountry(countryId);
	}

	@Override
	public List<StatePojo> getAllStatesPojo() {
		List<StatePojo> returnList = new ArrayList<StatePojo>();

		List<State> list = stateDao.findAll();

		if (CollectionUtil.isNotEmpty(list)) {
			for (State state : list) {
				if (state.getCreatedBy() != null)
					state.getCreatedBy().getLoginId();
				if (state.getModifiedBy() != null)
					state.getModifiedBy().getLoginId();

				StatePojo cp = new StatePojo(state);

				returnList.add(cp);
			}
		}

		return returnList;
	}

	@Override
	public List<State> searchStatesByNameOrCode(String searchValue) {
		List<State> list = stateDao.searchStatesByNameOrCode(searchValue);
		return list;
	}

	@Override
	public List<State> findAllStateList(TableDataInput tableParams) {
		return stateDao.findAllStateList(tableParams);
	}

	@Override
	public long findTotalFilteredStateList(TableDataInput tableParams) {
		return stateDao.findTotalFilteredStateList(tableParams);
	}

	@Override
	public long findTotalStateList() {
		return stateDao.findTotalStateList();
	}
	
	@Override
	public List<State> findAllActiveStatesForCountry(String countryId) {
		return stateDao.findAllActiveStatesForCountry(countryId);
	}

	public List<Coverage> retrieveAllStatesByCountry() {
		List<Coverage> list = new ArrayList<>();

		// Fetch all active countries
		List<Country> countries = countryService.findAllActiveCountries();

		if (countries != null && !countries.isEmpty()) {
			for (Country country : countries) {
				// Fetch all states corresponding to the current country
				List<State> states = country.getStates();
				if (states != null && !states.isEmpty()) {
					for (State state : states) {
						// Create Coverage object for each state and set the country name
						Coverage coverageState = new Coverage(state, true);
						StringBuilder builder = new StringBuilder();
						builder.append(state.getStateName()).append(", ").append(country.getCountryName());
						coverageState.setCountryName(builder.toString());
						list.add(coverageState);

					}
				}
			}
		}

		return list;
	}

}

package com.privasia.procurehere.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.NaicsCodesDao;
import com.privasia.procurehere.core.dao.OwnerAuditTrailDao;
import com.privasia.procurehere.core.entity.NaicsCodes;
import com.privasia.procurehere.core.entity.OwnerAuditTrail;
import com.privasia.procurehere.core.enums.AuditTypes;
import com.privasia.procurehere.core.enums.ModuleType;
import com.privasia.procurehere.core.pojo.NaicsCodesPojo;
import com.privasia.procurehere.core.pojo.SearchVo;
import com.privasia.procurehere.core.pojo.TableDataInput;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.NaicsCodesService;

@Service
@Transactional(readOnly = true)
public class NaicsCodesServiceImpl implements NaicsCodesService {

	private static final Logger LOG = LogManager.getLogger(NaicsCodesServiceImpl.class);

	@Autowired
	NaicsCodesDao naicsCodesDao;

	@Autowired
	OwnerAuditTrailDao ownerAuditTrailDao;

	@Override
	public boolean isExist(NaicsCodes naicsCodes) {
		return naicsCodesDao.isExists(naicsCodes);
	}

	@Override
	@Transactional(readOnly = false)
	public String createNaicsCodes(NaicsCodes naics) {
		if (naics.getParent() == null) {
			naics.setLevel(1);
			naics.setOrder(1);
		} else {
			NaicsCodes parent = naicsCodesDao.findById(naics.getParent().getId());
			naics.setLevel(parent.getLevel() + 1);
			naics.setOrder(CollectionUtil.isEmpty(parent.getChildren()) ? 1 : parent.getChildren().size() + 1);
		}

		OwnerAuditTrail ownerAuditTrail = new OwnerAuditTrail(AuditTypes.CREATE, "'" + naics.getCategoryName() + "' Naics settings created ", naics.getCreatedBy().getTenantId(), naics.getCreatedBy(), new Date(), ModuleType.NaicsCodes);
		ownerAuditTrailDao.save(ownerAuditTrail);
		naics = naicsCodesDao.saveOrUpdate(naics);
		return (naics != null ? naics.getId() : null);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateNaicsCodes(NaicsCodes naics) {
		OwnerAuditTrail ownerAuditTrail = new OwnerAuditTrail(AuditTypes.UPDATE, "'" + naics.getCategoryName() + "' Naics settings updated ", naics.getModifiedBy().getTenantId(), naics.getModifiedBy(), new Date(), ModuleType.NaicsCodes);
		ownerAuditTrailDao.save(ownerAuditTrail);
		naicsCodesDao.update(naics);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteNaicsCodes(NaicsCodes naics) {
		String naicsCategory = naics.getCategoryName();
		OwnerAuditTrail ownerAuditTrail = new OwnerAuditTrail(AuditTypes.DELETE, "'" + naicsCategory + "' Naics settings deleted ", naics.getModifiedBy().getTenantId(), naics.getModifiedBy(), new Date(), ModuleType.NaicsCodes);
		ownerAuditTrailDao.save(ownerAuditTrail);
		naicsCodesDao.delete(naics);
	}

	@Override
	public List<NaicsCodes> getAllNaicsCodesExceptSelf(String id) {
		return naicsCodesDao.findAll(id);
	}

	@Override
	@Transactional(readOnly = true)
	public NaicsCodes getNaicsCodesById(String id) {
		return naicsCodesDao.findById(id);

	}

	@Override
	public List<NaicsCodes> findForLevel(Integer level) {
		return naicsCodesDao.findForLevel(level);
	}

	@Override
	@Transactional(readOnly = true)
	public List<NaicsCodesPojo> getAllNaicsCodesPojo(int start, int length, String order) {
		List<NaicsCodesPojo> returnList = new ArrayList<NaicsCodesPojo>();

		List<NaicsCodes> list = naicsCodesDao.findAllActiveIndustryCategory(start, length, order);

		if (CollectionUtil.isNotEmpty(list)) {
			for (NaicsCodes naicsCode : list) {
				if (naicsCode.getCreatedBy() != null)
					naicsCode.getCreatedBy().getLoginId();
				if (naicsCode.getModifiedBy() != null)
					naicsCode.getModifiedBy().getLoginId();

				NaicsCodesPojo ic = new NaicsCodesPojo(naicsCode);

				returnList.add(ic);
			}
		}

		return returnList;
	}

	@Override
	@Transactional(readOnly = true)
	public List<NaicsCodes> findChildForId(SearchVo searchVo) {
		List<NaicsCodes> list = naicsCodesDao.findChildForId(searchVo);
		if (CollectionUtil.isNotEmpty(list)) {
			for (NaicsCodes codes : list) {
				if (CollectionUtil.isNotEmpty(codes.getChildren())) {
					for (NaicsCodes code : codes.getChildren()) {
						code.getCategoryCode();
						if (CollectionUtil.isNotEmpty(code.getChildren())) {
							for (NaicsCodes cod : code.getChildren()) {
								cod.getCategoryCode();
								if (CollectionUtil.isNotEmpty(cod.getChildren())) {
									for (NaicsCodes cd : cod.getChildren()) {
										cd.getCategoryCode();
									}
								}
							}
						}
					}
				}
			}
		}
		return list;
	}

	@Override
	public List<NaicsCodes> searchForCategories(String activeTab, String supplierId, String projectId, String[] selected, String search) {
		LOG.info("TAB : " + activeTab + "  Supplier Id : " + supplierId + " Project Id : " + projectId);
		List<NaicsCodes> returnList = new ArrayList<NaicsCodes>();
		List<NaicsCodes> assignedCategories = new ArrayList<NaicsCodes>();

		/*
		 * If the user has selected some Objects, then merge them into the assigned list for re-display
		 */
		if (selected != null && selected.length > 0) {
			// LOG.info("Pre-Selected ids : " + Arrays.asList(selected));
			List<NaicsCodes> selectedCategories = naicsCodesDao.findIndustryCategoryByIds(selected);
			if (selectedCategories != null) {
				assignedCategories.addAll(selectedCategories);
			}
		}

		// Fetch the saved assigned NaicsCodes order by its level and code and append it to the assignedCategories
		// if its not already added.
		List<NaicsCodes> tempList = new ArrayList<NaicsCodes>();

		if (Global.SUPPLIER_COVERAGE.equals(StringUtils.checkString(activeTab))) {
			tempList = naicsCodesDao.findAssignedNaicsCodes(supplierId);
		} else if (StringUtils.checkString(projectId).length() > 0) {
			tempList = naicsCodesDao.findAssignedIndustryCategoryForProject(projectId);
		}

		if (tempList != null) {
			for (NaicsCodes ic : tempList) {
				if (!assignedCategories.contains(ic)) {
					assignedCategories.add(ic);
				}
			}
		}

		// Construct the tree based on Flat List of assigned Categories
		if (CollectionUtils.isNotEmpty(assignedCategories)) {
			for (NaicsCodes ic : assignedCategories) {
				// LOG.info("Adding to tree : " + ic.getCategoryCode() + " - " + ic.getCategoryName());
				if (ic.getLevel() >= 1) {
					int index = -1;
					NaicsCodes parent = null;
					List<NaicsCodes> children = null;
					switch (ic.getLevel()) {
					case 1:
						NaicsCodes copy = ic.createShallowCopy();
						copy.setChecked(true);
						// First level will always be added first to the list as the assigned list comes ordered.
						returnList.add(copy);
						continue;
					case 2:
						index = returnList.indexOf(ic.getParent()); // L2 -> Parent (L1)
						if (index != -1) {
							parent = returnList.get(index);
							children = ic.getParent().getChildren(); // L2 -> Parent (L1) -> Children (L2)
						}
						break;
					case 3:
						index = returnList.indexOf(ic.getParent().getParent()); // L3 -> Parent (L2) -> Parent (L1)
						if (index != -1) {
							parent = returnList.get(index); // Parent = L1
							if (parent.getChildren() != null) {
								index = parent.getChildren().indexOf(ic.getParent()); // L1 -> Children (L2)
								if (index != -1) {
									parent = parent.getChildren().get(index); // Parent = L2
									children = ic.getParent().getChildren(); // L3 -> Parent (L2) -> Children (L3)
								}
							}
						}
						break;
					case 4:
						// L4 -> Parent (L3) -> Parent (L2) -> Parent (L1)
						index = returnList.indexOf(ic.getParent().getParent().getParent());
						if (index != -1) {
							parent = returnList.get(index); // Parent = L1
							if (parent.getChildren() != null) {
								index = parent.getChildren().indexOf(ic.getParent().getParent());
								// L1 -> Children (L2) indexOf L4 -> Parent (L3) -> Parent (L2)
								if (index != -1) {
									parent = parent.getChildren().get(index); // Parent = L2
									if (parent.getChildren() != null) {
										index = parent.getChildren().indexOf(ic.getParent());
										// L2 -> Children (L3) indexof L4 -> Parent (L3)
										if (index != -1) {
											parent = parent.getChildren().get(index); // Parent = L3
											children = ic.getParent().getChildren();
											// L4 -> Parent (L3) -> Children (L4)
										}
									}
								}
							}
						}
						break;
					case 5:
						index = returnList.indexOf(ic.getParent().getParent().getParent().getParent());
						// L5 -> Parent (L4) -> Parent (L3) -> Parent (L2) -> Parent (L1)
						if (index != -1) {
							parent = returnList.get(index); // Parent = L1
							if (parent.getChildren() != null) {
								index = parent.getChildren().indexOf(ic.getParent().getParent().getParent());
								// L1 -> Children (L2) indexOf L5 -> Parent (L4) -> Parent (L3) -> Parent(L2)
								if (index != -1) {
									parent = parent.getChildren().get(index); // Parent = L2
									if (parent.getChildren() != null) {
										index = parent.getChildren().indexOf(ic.getParent().getParent());
										// L2 -> Children (L3) indexof L5 -> Parent (L4) -> Parent (L3)

										if (index != -1) {
											parent = parent.getChildren().get(index); // Parent = L3
											if (parent != null && parent.getChildren() != null) {
												// L3 -> Children (L4) indexof L5 -> Parent (L4)
												index = parent.getChildren().indexOf(ic.getParent());
												if (index != -1) {
													parent = parent.getChildren().get(index); // Parent = L4
													children = ic.getParent().getChildren();
													// L5 -> Parent (L4) -> Children (L5)
												}
											}
										}
									}
								}
							}
						}
						break;
					}
					if (parent != null && children != null) {
						addChildren(parent, children, ic);
					}
				}
			}
		}

		if (StringUtils.checkString(search).length() > 0) {
			List<NaicsCodes> searchCategories = naicsCodesDao.searchForCategories(search);
			if (CollectionUtil.isNotEmpty(searchCategories)) {
				for (NaicsCodes searchObj : searchCategories) {
					includeSearchIndustryCategory(returnList, returnList, searchObj);
				}
			}
		} else {
			// Add all Level 1 Industry Categories...
			List<NaicsCodes> parentList = naicsCodesDao.findParentIndustryCategories();
			for (NaicsCodes ic : parentList) {
				NaicsCodes copy = ic.createShallowCopy();
				if (!returnList.contains(copy)) {
					returnList.add(copy);
				}
			}
		}

		return returnList;
	}

	/**
	 * @param searchList
	 * @param industryCategory
	 */
	private NaicsCodes includeSearchIndustryCategory(List<NaicsCodes> finalList, List<NaicsCodes> searchList, NaicsCodes industryCategory) {
		NaicsCodes copy = industryCategory.createShallowCopy();
		if (industryCategory.getParent() != null) {
			NaicsCodes parent = includeSearchIndustryCategory(finalList, industryCategory.getParent().getChildren(), industryCategory.getParent());
			if (parent.getChildren() == null) {
				parent.setChildren(new ArrayList<NaicsCodes>());
			}
			if (!parent.getChildren().contains(copy)) {
				parent.getChildren().add(copy);
			} else {
				copy = parent.getChildren().get(parent.getChildren().indexOf(copy));
			}
		} else {
			if (!finalList.contains(copy)) {
				finalList.add(0, copy);
			} else {
				copy = finalList.get(finalList.indexOf(copy));
			}
		}
		return copy;
	}

	/**
	 * @param parent
	 * @param children
	 */
	private void addChildren(NaicsCodes parent, List<NaicsCodes> children, NaicsCodes checked) {
		if (parent.getChildren() == null) {
			parent.setChildren(new ArrayList<NaicsCodes>());
		}
		if (CollectionUtil.isNotEmpty(children)) {
			for (NaicsCodes icc : children) {
				NaicsCodes copy = icc.createShallowCopy();
				if (!parent.getChildren().contains(copy)) {
					if (copy.equals(checked)) {
						copy.setChecked(true);
					}
					parent.getChildren().add(copy);
				} else { // If the already present node is the one to be checked, then set its status to be checked.
					NaicsCodes listCopy = parent.getChildren().get(parent.getChildren().indexOf(copy));
					if (listCopy.equals(checked)) {
						listCopy.setChecked(true);
					}
				}
			}
		}
	}

	@Override
	public List<NaicsCodes> searchForCategories(String search) {
		return naicsCodesDao.searchForCategories(search);
	}

	@Override
	public List<NaicsCodes> findNaicsQuery(String tenantId, TableDataInput tableParams) {
		List<NaicsCodes> returnList = new ArrayList<NaicsCodes>();
		List<NaicsCodes> list = naicsCodesDao.findNaicsQuery(tenantId, tableParams);
		if (CollectionUtil.isNotEmpty(list)) {
			for (NaicsCodes item : list) {
				if (item.getCreatedBy() != null)
					item.getCreatedBy().getLoginId();
				if (item.getModifiedBy() != null)
					item.getModifiedBy().getLoginId();
				returnList.add(item);
			}
		}
		// return naicsCodesDao.findNaicsQuery(tenantId, tableParams);
		return returnList;
	}

	@Override
	public long findTotalFilteredNaics(String tenantId, TableDataInput tableParams) {
		return naicsCodesDao.findTotalFilteredNaics(tenantId, tableParams);
	}

	@Override
	public long findTotalNaics(String tenantId) {
		return naicsCodesDao.findTotalNaics(tenantId);
	}

	@Override
	public List<NaicsCodes> searchForCategoriesForSupplierProfile(String activeTab, String supplierId, String projectId, String[] selected, String search) {
		LOG.info("TAB : " + activeTab + "  Supplier Id : " + supplierId + " Project Id : " + projectId);
		List<NaicsCodes> returnList = new ArrayList<NaicsCodes>();
		List<NaicsCodes> assignedCategories = new ArrayList<NaicsCodes>();

		/*
		 * If the user has selected some Objects, then merge them into the assigned list for re-display
		 */
		List<NaicsCodes> selectedCategories = null;
		if (selected != null && selected.length > 0) {
			// LOG.info("Pre-Selected ids : " + Arrays.asList(selected));
			selectedCategories = naicsCodesDao.findIndustryCategoryByIds(selected);
			if (selectedCategories != null) {
				assignedCategories.addAll(selectedCategories);
			}
		}

		// Fetch the saved assigned NaicsCodes order by its level and code and append it to the assignedCategories
		// if its not already added.
		List<NaicsCodes> tempList = new ArrayList<NaicsCodes>();

		if (Global.SUPPLIER_COVERAGE.equals(StringUtils.checkString(activeTab))) {
			tempList = naicsCodesDao.findAssignedNaicsCodes(supplierId);
		} else if (StringUtils.checkString(projectId).length() > 0) {
			tempList = naicsCodesDao.findAssignedIndustryCategoryForProject(projectId);
		}

		if (tempList != null) {
			for (NaicsCodes ic : tempList) {
				if (!assignedCategories.contains(ic)) {
					assignedCategories.add(ic);
				}
			}
		}

		// Construct the tree based on Flat List of assigned Categories
		if (CollectionUtils.isNotEmpty(assignedCategories)) {
			for (NaicsCodes ic : assignedCategories) {
				// LOG.info("Adding to tree : " + ic.getCategoryCode() + " - " + ic.getCategoryName());
				if (ic.getLevel() >= 1) {
					int index = -1;
					NaicsCodes parent = null;
					List<NaicsCodes> children = null;
					switch (ic.getLevel()) {
					case 1:
						NaicsCodes copy = ic.createShallowCopy();
						copy.setChecked(true);
						// First level will always be added first to the list as the assigned list comes ordered.
						returnList.add(copy);
						continue;
					case 2:
						index = returnList.indexOf(ic.getParent()); // L2 -> Parent (L1)
						if (index != -1) {
							parent = returnList.get(index);
							children = ic.getParent().getChildren(); // L2 -> Parent (L1) -> Children (L2)
						}
						break;
					case 3:
						index = returnList.indexOf(ic.getParent().getParent()); // L3 -> Parent (L2) -> Parent (L1)
						if (index != -1) {
							parent = returnList.get(index); // Parent = L1
							if (parent.getChildren() != null) {
								index = parent.getChildren().indexOf(ic.getParent()); // L1 -> Children (L2)
								if (index != -1) {
									parent = parent.getChildren().get(index); // Parent = L2
									children = ic.getParent().getChildren(); // L3 -> Parent (L2) -> Children (L3)
								}
							}
						}
						break;
					case 4:
						// L4 -> Parent (L3) -> Parent (L2) -> Parent (L1)
						index = returnList.indexOf(ic.getParent().getParent().getParent());
						if (index != -1) {
							parent = returnList.get(index); // Parent = L1
							if (parent.getChildren() != null) {
								index = parent.getChildren().indexOf(ic.getParent().getParent());
								// L1 -> Children (L2) indexOf L4 -> Parent (L3) -> Parent (L2)
								if (index != -1) {
									parent = parent.getChildren().get(index); // Parent = L2
									if (parent.getChildren() != null) {
										index = parent.getChildren().indexOf(ic.getParent());
										// L2 -> Children (L3) indexof L4 -> Parent (L3)
										if (index != -1) {
											parent = parent.getChildren().get(index); // Parent = L3
											children = ic.getParent().getChildren();
											// L4 -> Parent (L3) -> Children (L4)
										}
									}
								}
							}
						}
						break;
					case 5:
						index = returnList.indexOf(ic.getParent().getParent().getParent().getParent());
						// L5 -> Parent (L4) -> Parent (L3) -> Parent (L2) ->Parent (L1)
						if (index != -1) {
							parent = returnList.get(index); // Parent = L1
							if (parent.getChildren() != null) {
								index = parent.getChildren().indexOf(ic.getParent().getParent().getParent());
								// L1 -> Children (L2) indexOf L5 -> Parent (L4) -> Parent (L3) -> Parent (L2)
								if (index != -1) {
									parent = parent.getChildren().get(index); // Parent = L2
									if (parent.getChildren() != null) {
										index = parent.getChildren().indexOf(ic.getParent().getParent());
										// L2 -> Children (L3) indexof L5 -> Parent (L4) -> Parent (L3)
										if (index != -1) {
											parent = parent.getChildren().get(index); // Parent = L3
											if (parent != null && parent.getChildren() != null) {
												// L3 -> Children (L4) indexof L5 -> Parent (L4)
												index = parent.getChildren().indexOf(ic.getParent());
												if (index != -1) {
													parent = parent.getChildren().get(index); // Parent = L4
													children = ic.getParent().getChildren();
													// L5 -> Parent (L4) -> Children (L5)
												}
											}
										}
									}
								}
							}
						}
						break;
					}
					if (parent != null && children != null) {
						addChildren(parent, children, ic);
					}
				}
			}
		}

		if (StringUtils.checkString(search).length() > 0) {
			List<NaicsCodes> searchCategories = naicsCodesDao.searchForCategories(search);
			if (CollectionUtil.isNotEmpty(searchCategories)) {
				for (NaicsCodes searchObj : searchCategories) {
					includeSearchIndustryCategory(returnList, returnList, searchObj);
				}
			}

		} else {
			// Add all Level 1 Industry Categories...
			List<NaicsCodes> parentList = naicsCodesDao.findParentIndustryCategories();
			for (NaicsCodes ic : parentList) {
				NaicsCodes copy = ic.createShallowCopy();
				if (!returnList.contains(copy)) {
					returnList.add(copy);
				}
			}
		}
		removeAlredyRemoved(returnList, selected);
		return returnList;
	}

	/**
	 * @param returnList
	 * @param selectedCategories this is fixed for PH 192 please refer
	 */
	private void removeAlredyRemoved(List<NaicsCodes> returnList, String[] selectedCategories) {
		if ((selectedCategories != null && selectedCategories.length > 0) && CollectionUtil.isNotEmpty(returnList)) {
			for (NaicsCodes naicsCodes : returnList) {
				setChecked(selectedCategories, naicsCodes);
				if (CollectionUtil.isNotEmpty(naicsCodes.getChildren())) {
					for (NaicsCodes naicsCodeslvl2 : naicsCodes.getChildren()) {
						setChecked(selectedCategories, naicsCodeslvl2);
						if (CollectionUtil.isNotEmpty(naicsCodeslvl2.getChildren())) {
							for (NaicsCodes naicsCodeslvl3 : naicsCodeslvl2.getChildren()) {
								setChecked(selectedCategories, naicsCodeslvl3);
								if (CollectionUtil.isNotEmpty(naicsCodeslvl3.getChildren())) {
									for (NaicsCodes naicsCodeslvl4 : naicsCodeslvl3.getChildren()) {
										setChecked(selectedCategories, naicsCodeslvl4);
										if (CollectionUtil.isNotEmpty(naicsCodeslvl4.getChildren())) {
											for (NaicsCodes naicsCodeslvl5 : naicsCodeslvl4.getChildren()) {
												setChecked(selectedCategories, naicsCodeslvl5);
											}
										}
									}
								}
							}
						}
					}
				}

			}
		}
	}

	private void setChecked(String[] selectedCategories, NaicsCodes naicsCodes) {
		if (Arrays.stream(selectedCategories).anyMatch(naicsCodes.getId()::equals)) {
			naicsCodes.setChecked(true);
		} else {
			naicsCodes.setChecked(false);
		}
	}

	@Override
	public NaicsCodes getIndustryCategoryCodeAndNameById(String id) {
		return naicsCodesDao.getIndustryCategoryCodeAndNameById(id);
	}

}

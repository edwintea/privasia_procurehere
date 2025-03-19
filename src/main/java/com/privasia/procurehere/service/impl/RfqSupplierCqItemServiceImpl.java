package com.privasia.procurehere.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.privasia.procurehere.core.dao.RfqSorDao;
import com.privasia.procurehere.core.dao.RfqSupplierSorDao;
import com.privasia.procurehere.core.entity.RfqSupplierSor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RfqBqDao;
import com.privasia.procurehere.core.dao.RfqBqItemDao;
import com.privasia.procurehere.core.dao.RfqCqDao;
import com.privasia.procurehere.core.dao.RfqCqItemDao;
import com.privasia.procurehere.core.dao.RfqCqOptionDao;
import com.privasia.procurehere.core.dao.RfqEnvelopDao;
import com.privasia.procurehere.core.dao.RfqEventDao;
import com.privasia.procurehere.core.dao.RfqEventSupplierDao;
import com.privasia.procurehere.core.dao.RfqSupplierBqDao;
import com.privasia.procurehere.core.dao.RfqSupplierBqItemDao;
import com.privasia.procurehere.core.dao.RfqSupplierCqDao;
import com.privasia.procurehere.core.dao.RfqSupplierCqItemDao;
import com.privasia.procurehere.core.dao.RfqSupplierCqOptionDao;
import com.privasia.procurehere.core.entity.CqOption;
import com.privasia.procurehere.core.entity.RfqCq;
import com.privasia.procurehere.core.entity.RfqCqEvaluationComments;
import com.privasia.procurehere.core.entity.RfqCqItem;
import com.privasia.procurehere.core.entity.RfqCqOption;
import com.privasia.procurehere.core.entity.RfqEnvelop;
import com.privasia.procurehere.core.entity.RfqEvent;
import com.privasia.procurehere.core.entity.RfqSupplierBq;
import com.privasia.procurehere.core.entity.RfqSupplierCq;
import com.privasia.procurehere.core.entity.RfqSupplierCqItem;
import com.privasia.procurehere.core.entity.RfqSupplierCqOption;
import com.privasia.procurehere.core.entity.Supplier;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.enums.CqType;
import com.privasia.procurehere.core.enums.SupplierCqStatus;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.CqPojo;
import com.privasia.procurehere.core.pojo.EventEvaluationPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import com.privasia.procurehere.core.utils.MaskUtils;
import com.privasia.procurehere.core.utils.SecurityLibrary;
import com.privasia.procurehere.core.utils.StringUtils;
import com.privasia.procurehere.service.EventAuditService;
import com.privasia.procurehere.service.RfqCqService;
import com.privasia.procurehere.service.RfqEventService;
import com.privasia.procurehere.service.RfqSupplierCqItemService;
import com.privasia.procurehere.service.RfqSupplierCqService;
import com.privasia.procurehere.service.supplier.SupplierService;

/**
 * @author Vipul
 */

@Service
@Transactional(readOnly = true)
public class RfqSupplierCqItemServiceImpl implements RfqSupplierCqItemService {

	private static final Logger LOG = LogManager.getLogger(Global.SUPPLIER_LOG);

	@Autowired
	RfqSupplierCqItemDao supplierCqItemDao;

	@Autowired
	RfqCqDao cqDao;

	@Autowired
	RfqEventDao eventDao;

	@Autowired
	RfqCqItemDao cqItemDao;

	@Autowired
	SupplierService supplierService;

	@Autowired
	RfqCqOptionDao cqOptionDao;

	@Autowired
	RfqEventSupplierDao eventSupplierDao;

	@Autowired
	RfqEnvelopDao envelopDao;

	@Autowired
	RfqBqItemDao rfqBqItemDao;

	@Autowired
	RfqSupplierBqItemDao rfqSupplierBqItemDao;

	@Autowired
	RfqSupplierBqDao rfqSupplierBqDao;

	@Autowired
	RfqSupplierSorDao rfqSupplierSorDao;

	@Autowired
	RfqBqDao rfqBqDao;

	@Autowired
	RfqSorDao rfqSorDao;

	@Autowired
	RfqCqService rfqCqService;

	@Autowired
	RfqEventService rfqEventService;

	@Autowired
	EventAuditService eventAuditService;

	@Autowired
	RfqSupplierCqOptionDao rfqSupplierCqOptionDao;

	@Autowired
	RfqSupplierCqDao rfqSupplierCqDao;

	@Autowired
	RfqSupplierCqService rfqSupplierCqService;

	@Override
	public List<EventEvaluationPojo> getCqEvaluationData(String eventId, String envelopId, List<Supplier> selectedSuppliers, User logedUser) {
		List<EventEvaluationPojo> returnList = new ArrayList<>();
		List<Supplier> suppliers = null;
		if (CollectionUtil.isEmpty(selectedSuppliers)) {
			suppliers = eventSupplierDao.getEventSuppliersForEvaluation(eventId);
		} else {
			suppliers = eventSupplierDao.getEventSuppliersForEvaluation(eventId, selectedSuppliers);
		}

		List<String> columns = new ArrayList<String>();
		if (CollectionUtil.isNotEmpty(suppliers)) {
			for (Supplier supplier : suppliers) {
				columns.add(supplier.getCompanyName());
			}
			List<String> envelopIds = new ArrayList<String>();
			envelopIds.add(envelopId);
			// List<String> cqIds = envelopDao.getCqsByEnvelopId(envelopIds);
			List<CqPojo> cqPojo = envelopDao.getCqsIdListByEnvelopIdByOrder(envelopIds);

			RfqEnvelop envelop = envelopDao.findById(envelopId);

			if (envelop.getRfxEvent() != null) {
				if (envelop.getRfxEvent().getViewSupplerName() != null && !envelop.getRfxEvent().getViewSupplerName() && !envelop.getRfxEvent().getDisableMasking()) {
					for (Supplier supplier : suppliers) {
						supplier.setCompanyName(MaskUtils.maskName(envelop.getPreFix(), supplier.getId(), envelop.getId()));
					}

				}

			}

			for (CqPojo cqObj : cqPojo) {
				EventEvaluationPojo response = new EventEvaluationPojo();
				List<List<String>> data = new ArrayList<List<String>>();
				List<RfqCqItem> cqItems = cqItemDao.findCqItemsForCq(cqObj.getId());
				for (RfqCqItem cqItem : cqItems) {
					response.setName(cqObj.getName());

					List<String> answers = new ArrayList<String>();
					List<RfqSupplierCqItem> responseList = supplierCqItemDao.findSupplierCqItemsByCqItemIdAndEventId(cqItem.getId(), eventId, suppliers);
					answers.add(cqItem.getLevel() + "." + cqItem.getOrder());
					boolean leadEvalComment = false;
					String comment = rfqCqService.getLeadEvaluatorComment(cqItem.getId());
					if (StringUtils.checkString(comment).length() > 0) {
						leadEvalComment = true;
					}
					answers.add(cqItem.getId() + "" + (leadEvalComment ? 1 : 0) + "-" + cqItem.getItemName());
					answers.add(cqItem.getId());
					response.setItemId(cqItem.getId());

					int supplierIndex = 0;
					for (RfqSupplierCqItem item : responseList) {

						if(suppliers.size() < supplierIndex){
							while (!suppliers.get(supplierIndex).getId().equals(item.getSupplier().getId())) {
								// this supplier has not supplied response
								response.setSupplierId(suppliers.get(supplierIndex).getId());
								// No response by supplier
								answers.add(false + "-" + suppliers.get(supplierIndex).getId() + "-dummy-" + "");
								supplierIndex++;
								if (supplierIndex == suppliers.size()) {
									break;
								}
							}
						}

						boolean commentsExist = false;
						String supplierId = "";
						String itemId = "";
						response.setSupplierId(item.getSupplier().getId());
						if (item.getCqItem().getCqType() == CqType.TEXT || item.getCqItem().getCqType() == CqType.NUMBER || item.getCqItem().getCqType() == CqType.DATE || item.getCqItem().getCqType() == CqType.PARAGRAPH) {
							if (item.getCqItem().getEvaluationComments() != null && item.getCqItem().getEvaluationComments().size() > 0) {
								for (RfqCqEvaluationComments com : item.getCqItem().getEvaluationComments()) {
									if ((com.getCreatedBy().getId().equals(logedUser.getId()) || (envelop.getLeadEvaluater().getId().equals(logedUser.getId()))) && com.getSupplier().getId().equals(item.getSupplier().getId())) {
										commentsExist = true;
										LOG.info("Comments : " + item.getCqItem().getEvaluationComments().size());
										continue;
									}
								}
							}
							answers.add(commentsExist + "-" + item.getSupplier().getId() + "-" + item.getCqItem().getId() + "-" + item.getTextAnswers());
						} else if (item.getCqItem().getCqType() == CqType.DATE) {
							if (item.getCqItem().getEvaluationComments() != null && item.getCqItem().getEvaluationComments().size() > 0) {
								for (RfqCqEvaluationComments com : item.getCqItem().getEvaluationComments()) {
									if ((com.getCreatedBy().getId().equals(logedUser.getId()) || (envelop.getLeadEvaluater().getId().equals(logedUser.getId()))) && com.getSupplier().getId().equals(item.getSupplier().getId())) {
										commentsExist = true;
										LOG.info("Comments : " + item.getCqItem().getEvaluationComments().size());
										continue;
									}
								}
							}
							answers.add(commentsExist + "-" + item.getSupplier().getId() + "-" + item.getCqItem().getId() + "-" + (item.getTextAnswers()));
						} else {
							String str = "";
							List<RfqSupplierCqOption> listAnswers = item.getListAnswers();
							for (RfqSupplierCqOption op : listAnswers) {
								if (op.getCqItem().getCqItem().getEvaluationComments() != null && op.getCqItem().getCqItem().getEvaluationComments().size() > 0) {
									for (RfqCqEvaluationComments com : op.getCqItem().getCqItem().getEvaluationComments()) {
										if ((com.getCreatedBy().getId().equals(logedUser.getId()) || (envelop.getLeadEvaluater().getId().equals(logedUser.getId()))) && com.getSupplier().getId().equals(item.getSupplier().getId())) {
											commentsExist = true;
											LOG.info("Comments : " + op.getCqItem().getCqItem().getEvaluationComments().size());
											continue;
										}
									}
								}
								if (StringUtils.checkString(supplierId).length() == 0)
									supplierId = item.getSupplier().getId();
								if (StringUtils.checkString(itemId).length() == 0)
									itemId = item.getCqItem().getId();

								str += op.getValue() + (op.getScoring() != null ? "/" + op.getScoring() : "") + ",";
							}
							if (str.length() > 0) {
								str = str.substring(0, str.length() - 1);
							}
							answers.add(commentsExist + "-" + supplierId + "-" + itemId + "-" + str);
						}
						supplierIndex++;
					}
					while (supplierIndex < suppliers.size()) {
						// this supplier has not supplied response
						response.setSupplierId(suppliers.get(supplierIndex).getId());
						// No response by supplier
						answers.add(false + "-" + suppliers.get(supplierIndex).getId() + "-dummy-" + "");
						supplierIndex++;
					}

					data.add(answers);
				}
				response.setData(data);
				/*
				 * if (envelop.getRfxEvent() != null) { if (envelop.getRfxEvent().getViewSupplerName() != null &&
				 * !envelop.getRfxEvent().getViewSupplerName() && !envelop.getRfxEvent().getDisableMasking()) {
				 * Collections.sort(suppliers, new Comparator<Supplier>() { public int compare(Supplier o1, Supplier o2)
				 * { if (o1.getCompanyName() == null || o2.getCompanyName() == null) { return 0; } return
				 * o1.getCompanyName().compareTo(o2.getCompanyName()); } }); } }
				 */

				response.setColumns(suppliers);
				List<String> sumOfScoring = supplierCqItemDao.findSumOfScoringForSupplier(cqObj.getId(), eventId, suppliers);
				LOG.info("Scoring : " + sumOfScoring.size());
				response.setScoring(sumOfScoring);
				response.setEnvelopId(envelopId);
				response.setEventId(eventId);
				returnList.add(response);
			}
		}

		return returnList;
	}

	@Override
	public List<RfqSupplierCqItem> getAllSupplierCqItemByCqId(String cqId, String supplierId) {
		List<RfqSupplierCqItem> returnList = new ArrayList<RfqSupplierCqItem>();
		List<RfqSupplierCqItem> list = supplierCqItemDao.findSupplierCqItemListByCqId(cqId, supplierId);
		bulidSupplierCqItemList(returnList, list);
		LOG.info("ITEM LIST SIZE : " + list.size());
		return returnList;
	}

	private void bulidSupplierCqItemList(List<RfqSupplierCqItem> returnList, List<RfqSupplierCqItem> list) {
		LOG.info("BUILDING CQ ITEM LIST :: " + list.size());
		if (CollectionUtil.isNotEmpty(list)) {
			for (RfqSupplierCqItem item : list) {
				if (item.getCqItem().getCqOptions() != null) {
					for (CqOption op : item.getCqItem().getCqOptions()) {
						LOG.info(" >>>>>>>>>>>>>>>>>>>>>>>> " + op.getValue());
					}
				}
				returnList.add(item.createShallowCopy());
			}
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void saveSupplierEventCq(String cqId, String eventId, String tenantId) {
		List<RfqCqItem> cqItems = supplierCqItemDao.getCqItemsbyId(cqId);
		for (RfqCqItem item : cqItems) {
			LOG.info("ITEM : " + item.toLogString() + "  Options " + item.getCqOptions().size());
			RfqCq cq = item.getCq();
			RfqSupplierCqItem supplierCq = supplierCqItemDao.findCqByEventIdAndCqName(eventId, item.getId());
			if (supplierCq == null) {
				supplierCq = new RfqSupplierCqItem(item);
				supplierCq.setSupplier(new Supplier(tenantId));
				supplierCq = supplierCqItemDao.saveOrUpdateWithFlush(supplierCq);
			}
		}

	}

	@Override
	@Transactional(readOnly = false)
	public void saveSupplierCq(String cqId, String eventId, String supplierId) {
		LOG.info("Saving Supplier Cq.... : ");
		RfqSupplierCq suppliercq = rfqSupplierCqDao.findCqByEventIdAndEventCqId(eventId, cqId, SecurityLibrary.getLoggedInUserTenantId());
		if (suppliercq == null) {
			suppliercq = new RfqSupplierCq();
			RfqEvent rfxEvent = eventDao.findByEventId(eventId);
			suppliercq.setEvent(rfxEvent);
			suppliercq.setSupplier(SecurityLibrary.getLoggedInUser().getSupplier());
			RfqCq rfaCq = cqDao.findById(cqId);
			suppliercq.setCq(rfaCq);
			suppliercq = rfqSupplierCqDao.saveOrUpdate(suppliercq);
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void updateCqItems(List<RfqSupplierCqItem> items) {
		for (RfqSupplierCqItem item : items) {
			List<RfqCqOption> options = item.getListOptAnswers();
			LOG.info("ITEM ID : " + item.getId());
			RfqSupplierCqItem cqItem = supplierCqItemDao.findById(item.getId());
			if (item.getTextAnswers() != null) {
				String textAnswer = item.getTextAnswers().replace('-', '_');
				cqItem.setTextAnswers(textAnswer);
			}
			LOG.info(cqItem.getTextAnswers());

			/*
			 * if (item.getDateAnswer() != null) { cqItem.setDateAnswer(item.getDateAnswer()); }
			 * LOG.info(cqItem.getDateAnswer());
			 */
			cqItem.setFileData(item.getFileData());
			cqItem.setFileName(item.getFileName());
			cqItem.setCredContentType(item.getCredContentType());
			List<RfqSupplierCqOption> supOptions = new ArrayList<RfqSupplierCqOption>();
			if (CollectionUtil.isNotEmpty(options)) {
				for (RfqCqOption op : options) {
					RfqCqOption option = cqOptionDao.findById(op.getId());
					RfqSupplierCqOption cqOption = new RfqSupplierCqOption();
					cqOption.setCqItem(cqItem);
					cqOption.setOrder(option.getOrder());
					cqOption.setScoring(option.getScoring());
					cqOption.setValue(option.getValue());
					supOptions.add(cqOption);
				}
			}
			cqItem.setListAnswers(supOptions);
			cqItem.setSupplier(SecurityLibrary.getLoggedInUser().getSupplier());
			supplierCqItemDao.update(cqItem);
		}
	}

	@Override
	public List<EventEvaluationPojo> getEvaluationDataForCqComparison(String eventId, String envelopId) {
		List<EventEvaluationPojo> returnList = new ArrayList<>();
		RfqEnvelop envolpe = null;
		List<Supplier> qualifiedSuppliersDb = eventSupplierDao.getEventQualifiedSuppliersForEvaluation(eventId);
		List<Supplier> qualifiedSuppliers = new ArrayList<Supplier>();
		RfqEvent event = rfqEventService.getRfqEventByeventId(eventId);

		// Masking will be auto flushed by Hibernate due to dirty checking.
		// This is happening because Dumbo changed the transaction type to readOnly=false for Audit.
		if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking()) {
			envolpe = envelopDao.findById(envelopId);
			if (envolpe != null) {
				for (Supplier supplier : qualifiedSuppliersDb) {
					Supplier supplierCopy = supplier.createShallowCopy();
					supplierCopy.setCompanyName(MaskUtils.maskName(envolpe.getPreFix(), supplier.getId(), envolpe.getId()));
					qualifiedSuppliers.add(supplierCopy);
				}
			}
		} else {
			qualifiedSuppliers = qualifiedSuppliersDb;
		}

		if (CollectionUtil.isNotEmpty(qualifiedSuppliers)) {
			List<String> envelopIds = new ArrayList<String>();
			envelopIds.add(envelopId);
			List<String> cqIds = envelopDao.getCqsByEnvelopId(envelopIds);
			List<CqPojo> cqList = envelopDao.getCqsIdListByEnvelopIdByOrder(envelopIds);

			for (CqPojo cq : cqList) {
				EventEvaluationPojo response = new EventEvaluationPojo();
				List<List<String>> data = new ArrayList<List<String>>();
				List<RfqCqItem> cqItems = cqItemDao.findCqItemsForCq(cq.getId());
				Integer scoreSum = 0;
				for (RfqCqItem cqItem : cqItems) {
					response.setName(cqItem.getCq().getName());
					response.setTotalScore(cqItem.getTotalScore());
					List<String> answers = new ArrayList<String>();
					// List<RfqSupplierCqItem> responseList =
					// supplierCqItemDao.findSupplierCqItemsByCqItemIdAndEventId(cqItem.getId(), eventId,
					// qualifiedSuppliers);
					List<RfqSupplierCqItem> responseList = supplierCqItemDao.findSupplierCqItemsByCqItemIdAndEventIdNew(cqItem.getId(), eventId, qualifiedSuppliers);

					answers.add(cqItem.getLevel() + "." + cqItem.getOrder());
					answers.add(cqItem.getItemName());
					scoreSum += cqItem.getTotalScore() != null ? cqItem.getTotalScore() : 0;
					answers.add(cqItem.getTotalScore() != null ? String.valueOf(cqItem.getTotalScore()) : " ");
					response.setItemId(cqItem.getId());
					for (RfqSupplierCqItem item : responseList) {
						response.setSupplierId(item.getSupplier().getId());
						if (item.getCqItem().getCqType() == CqType.TEXT || item.getCqItem().getCqType() == CqType.DATE || item.getCqItem().getCqType() == CqType.NUMBER || item.getCqItem().getCqType() == CqType.PARAGRAPH) {
							answers.add(item.getTextAnswers());
							answers.add("");
						} else if (item.getCqItem().getCqType() == CqType.LIST || item.getCqItem().getCqType() == CqType.CHECKBOX) {
							List<RfqSupplierCqOption> listAnswers = rfqSupplierCqOptionDao.findSupplierCqOptionsListWithCqByCqId(item.getId());
							// List<RfqSupplierCqOption> listAnswers = item.getListAnswers();
							String ans = "";
							for (RfqSupplierCqOption op : listAnswers) {
								ans += op.getValue() + "\n";
							}
							answers.add(ans);
							answers.add("");
						} else {
							List<RfqSupplierCqOption> listAnswers = rfqSupplierCqOptionDao.findSupplierCqOptionsListWithCqByCqId(item.getId());
							// List<RfqSupplierCqOption> listAnswers = item.getListAnswers();
							for (RfqSupplierCqOption op : listAnswers) {
								answers.add(op.getValue());
								answers.add(op.getScoring() != null ? String.valueOf(op.getScoring()) : " ");
							}
						}
					}
					data.add(answers);
				}
				response.setData(data);
				response.setColumns(qualifiedSuppliers);
				List<String> sumOfScoring = supplierCqItemDao.findSumOfScoringForSupplier(cq.getId(), eventId, qualifiedSuppliers);
				sumOfScoring.add(0, scoreSum != null ? String.valueOf(scoreSum) : " ");
				response.setScoring(sumOfScoring);
				response.setEnvelopId(envelopId);
				response.setEventId(eventId);
				returnList.add(response);
			}
		}

		return returnList;
	}

	@Override
	public Boolean checkMandatoryToFinishEvent(String supplierId, String eventId) throws NotAllowedException, Exception {

		List<String> bqNames = rfqBqDao.rfqBqNamesByEventId(eventId);
		if (CollectionUtil.isNotEmpty(bqNames) && bqNames.size() > 0) {
			List<RfqSupplierBq> supplierBqList = rfqSupplierBqDao.rfqSupplierBqAnswerdByEventIdAndSupplierId(eventId, supplierId);
			if (CollectionUtil.isEmpty(supplierBqList) || (CollectionUtil.isNotEmpty(supplierBqList)) && supplierBqList.size() != bqNames.size()) {
				if (CollectionUtil.isNotEmpty(supplierBqList)) {
					for (RfqSupplierBq bq : supplierBqList) {
						bqNames.remove(bq.getName());
					}
				}
			} else {
				// All OK
				bqNames.clear();
			}
		}

		List<String> sorNames = rfqSorDao.rfqSorNamesByEventId(eventId);
		if (CollectionUtil.isNotEmpty(sorNames) && sorNames.size() > 0) {
			List<RfqSupplierSor> supplierSorList = rfqSupplierSorDao.rfqSupplierSorAnswerdByEventIdAndSupplierId(eventId, supplierId);
			if (CollectionUtil.isEmpty(supplierSorList) || (CollectionUtil.isNotEmpty(supplierSorList)) && supplierSorList.size() != sorNames.size()) {
				if (CollectionUtil.isNotEmpty(supplierSorList)) {
					for (RfqSupplierSor bq : supplierSorList) {
						sorNames.remove(bq.getName());
					}
				}
			} else {
				// All OK
				sorNames.clear();
			}
		}

		List<String> cqNames = new ArrayList<>();
		List<RfqCq> cqItemList = cqItemDao.rfqMandatoryCqNamesbyEventId(eventId);
		if (CollectionUtil.isNotEmpty(cqItemList)) {
			List<RfqCq> supplierRequiredCqItemList = supplierCqItemDao.findRequiredSupplierCqCountByEventId(supplierId, eventId);

			for (RfqCq cq : cqItemList) {
				boolean found = false;
				for (RfqCq supCq : supplierRequiredCqItemList) {
					if (supCq.getName().equals(cq.getName())) {
						found = true;
						if (supCq.getMandatoryItemCount() != cq.getMandatoryItemCount()) {
							cqNames.add(cq.getName());
						}
					}
				}
				if (!found) {
					cqNames.add(cq.getName());
				}
			}
		}

		if (CollectionUtil.isNotEmpty(cqNames) && CollectionUtil.isNotEmpty(bqNames) && CollectionUtil.isNotEmpty(sorNames)) {
			LOG.error("Please fill up all mandatory Questionnaire and all Bill Of Quantity");
			throw new NotAllowedException("Please fill up all mandatory Questionnaire , all Bill Of Quantity and Schedule of Rate");
		} else if (CollectionUtil.isNotEmpty(cqNames) && CollectionUtil.isNotEmpty(bqNames)) {
			LOG.error("Please fill up all mandatory Questionnaire and all Bill Of Quantity");
			throw new NotAllowedException("Please fill up all mandatory Questionnaire and all Bill Of Quantity");
		} else if (CollectionUtil.isNotEmpty(bqNames) && CollectionUtil.isNotEmpty(sorNames)) {
			LOG.error("Please fill up all mandatory all Bill Of Quantity and Schedule of Rate");
			throw new NotAllowedException("Please fill up all mandatory Questionnaire and all Bill Of Quantity");
		} else if (CollectionUtil.isNotEmpty(cqNames) && CollectionUtil.isNotEmpty(sorNames)) {
			LOG.error("Please fill up all mandatory all Bill Of Quantity and Schedule of Rate");
			throw new NotAllowedException("Please fill up all mandatory Questionnaire and Schedule of Rate");
		} else if (CollectionUtil.isNotEmpty(cqNames)) {
			String s = "";
			int i = 0;
			for (String bq : cqNames) {
				if (i == 0) {
					s = bq;
				} else {
					s += "," + bq;
				}
				i++;
			}
			throw new NotAllowedException("Please fill up mandatory Questionnaire : " + s);
		} else if (CollectionUtil.isNotEmpty(bqNames)) {
			String s = "";
			int i = 0;
			for (String bq : bqNames) {
				if (i == 0) {
					s = bq;
				} else {
					s += "," + bq;
				}
				i++;
			}
			throw new NotAllowedException("Please fill up mandatory Bill of Quantities : " + s);
		} else if (CollectionUtil.isNotEmpty(sorNames)) {
			String s = "";
			int i = 0;
			for (String bq : sorNames) {
				if (i == 0) {
					s = bq;
				} else {
					s += "," + bq;
				}
				i++;
			}
			throw new NotAllowedException("Please fill up mandatory Schedule of Rate : " + s);
		}

		long pendingBq = rfqSupplierBqDao.findPendingBqAnswerdByEventIdAndSupplierId(eventId, supplierId);
		long pendingCq = rfqSupplierCqDao.findPendingCqsByEventIdAndEventCqId(eventId, supplierId);
		long pendingSor = rfqSupplierSorDao.findPendingSorsByEventIdAndEventSorId(eventId, supplierId);

		if (pendingBq > 0 && pendingCq > 0 && pendingSor > 0) {
			throw new NotAllowedException("Please complete all Questionnaire, all Bill of Quantity and all schedule of rate");
		}

		if (pendingBq > 0 ) {
			throw new NotAllowedException("Please complete all Bill of Quantity");
		}

		if (pendingCq > 0) {
			throw new NotAllowedException("Please complete all Questionnaire");
		}

		if(pendingSor > 0) {
			throw new NotAllowedException("Please complete all Schedule of rate");
		}


		List<String> supplierAttach = cqItemDao.findAllSupplierAttachRequiredId(eventId);
		if (CollectionUtil.isNotEmpty(supplierAttach)) {
			int rfqCqCount = supplierCqItemDao.findRfqRequiredCqCountBySupplierId(supplierId, eventId);
			if (rfqCqCount > 0) {
				throw new NotAllowedException("Please Attach All required Documents in Questionnaires");
			}
		}

		return true;
	}

	@Override
	@Transactional(readOnly = false)
	public boolean resetAttachement(String itemId, String eventId, String tenantId) {
		boolean removed = supplierCqItemDao.resetAttachment(eventId, itemId);
		if (removed) {
			RfqSupplierCqItem item = supplierCqItemDao.findCqByEventIdAndCqName(eventId, itemId);
			if (item != null) {
				RfqSupplierCq rfqSupplierCq = rfqSupplierCqDao.findSupplierCqByEventIdAndSupplierId(tenantId, eventId, item.getCq().getId());
				if (rfqSupplierCq != null) {
					rfqSupplierCq.setSupplierCqStatus(SupplierCqStatus.DRAFT);
					rfqSupplierCqService.updateSupplierCq(rfqSupplierCq);
				}
			}
		}
		return removed;

	}

	@Override
	public RfqSupplierCqItem findCqByEventIdAndCqName(String eventId, String cqId) {
		return supplierCqItemDao.findCqByEventIdAndCqName(eventId, cqId);
	}

	@Override
	public List<RfqSupplierCqItem> getSupplierCqItemsbySupplierIdAndEventId(String eventId, String supplierId) {
		List<RfqSupplierCqItem> returnList = new ArrayList<RfqSupplierCqItem>();
		List<RfqSupplierCqItem> list = supplierCqItemDao.getSupplierCqItemsbySupplierIdAndEventId(eventId, supplierId);
		bulidSupplierCqItemList(returnList, list);
		return returnList;
	}

	@Override
	@Transactional(readOnly = false)
	public void draftUpdateCqItems(List<RfqSupplierCqItem> items, String eventId, SupplierCqStatus status, User loggedInUser) {
		saveCqItems(items, eventId, status, loggedInUser);
	}

	@Override
	@Transactional(readOnly = false, noRollbackFor = NotAllowedException.class)
	public void updateCqItems(List<RfqSupplierCqItem> items, String eventId, SupplierCqStatus status, User loggedInUser) throws NotAllowedException {
		saveCqItems(items, eventId, status, loggedInUser);
		int count = supplierCqItemDao.findRfqRequiredCqCountBySupplierIdCqId(loggedInUser.getTenantId(), eventId, items.get(0).getCq().getId());
		if (count > 0) {
			RfqSupplierCq rfqSupplierCq = rfqSupplierCqDao.findSupplierCqByEventIdAndSupplierId(loggedInUser.getTenantId(), eventId, items.get(0).getCq().getId());
			if (rfqSupplierCq != null) {
				rfqSupplierCq.setSupplierCqStatus(SupplierCqStatus.DRAFT);
				rfqSupplierCqService.updateSupplierCq(rfqSupplierCq);
			}
			List<String> cqAttachments = supplierCqItemDao.findRequiredCqsBySupplierId(loggedInUser.getTenantId(), eventId, items.get(0).getCq().getId());
			if (CollectionUtil.isNotEmpty(cqAttachments)) {
				String cqItems = org.apache.commons.lang.StringUtils.join(cqAttachments, ",");
				if (StringUtils.checkString(cqItems).length() > 0) {
					throw new NotAllowedException("Please Attach All required Documents in Questionnaires <" + cqItems + ">");
				}
			}

//			throw new NotAllowedException("Please Attach All required Documents in Questionnaires");
		}
	}

	private void saveCqItems(List<RfqSupplierCqItem> items, String eventId, SupplierCqStatus status, User loggedInUser) {
		String cqId = "";
		for (RfqSupplierCqItem item : items) {
			List<RfqCqOption> options = item.getListOptAnswers();
			LOG.info("ITEM ID : " + item.getId());
			RfqSupplierCqItem cqItem = supplierCqItemDao.findById(item.getId());
			if (item.getTextAnswers() != null) {
				String textAnswer = item.getTextAnswers().replace('-', '_');
				cqItem.setTextAnswers(textAnswer);
			}
			LOG.info(cqItem.getTextAnswers());
			if (StringUtils.checkString(cqId).length() == 0) {
				cqId = item.getCq().getId();
			}

			/*
			 * if (item.getDateAnswer() != null) { cqItem.setDateAnswer(item.getDateAnswer()); }
			 * LOG.info(cqItem.getDateAnswer());
			 */
			cqItem.setFileData(item.getFileData());
			cqItem.setFileName(item.getFileName());
			cqItem.setCredContentType(item.getCredContentType());
			List<RfqSupplierCqOption> supOptions = new ArrayList<RfqSupplierCqOption>();
			if (CollectionUtil.isNotEmpty(options)) {
				for (RfqCqOption op : options) {
					RfqCqOption option = cqOptionDao.findById(op.getId());
					RfqSupplierCqOption cqOption = new RfqSupplierCqOption();
					cqOption.setCqItem(cqItem);
					cqOption.setOrder(option.getOrder());
					cqOption.setScoring(option.getScoring());
					cqOption.setValue(option.getValue());
					supOptions.add(cqOption);
				}
			}
			cqItem.setListAnswers(supOptions);
			cqItem.setSupplier(loggedInUser.getSupplier());
			supplierCqItemDao.update(cqItem);
		}

		RfqSupplierCq rfqSupplierCq = rfqSupplierCqDao.findSupplierCqByEventIdAndSupplierId(loggedInUser.getTenantId(), eventId, cqId);
		if (rfqSupplierCq != null) {
			rfqSupplierCq.setSupplierCqStatus(status);
			rfqSupplierCqService.updateSupplierCq(rfqSupplierCq);
		}
	}

}

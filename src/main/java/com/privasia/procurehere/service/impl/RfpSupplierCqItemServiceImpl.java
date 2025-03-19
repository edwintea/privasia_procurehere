package com.privasia.procurehere.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.privasia.procurehere.core.dao.RfpSorDao;
import com.privasia.procurehere.core.dao.RfpSupplierSorDao;
import com.privasia.procurehere.core.entity.RfpSupplierSor;
import com.privasia.procurehere.core.entity.RfqSupplierSor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RfpBqDao;
import com.privasia.procurehere.core.dao.RfpBqItemDao;
import com.privasia.procurehere.core.dao.RfpCqDao;
import com.privasia.procurehere.core.dao.RfpCqItemDao;
import com.privasia.procurehere.core.dao.RfpCqOptionDao;
import com.privasia.procurehere.core.dao.RfpEnvelopDao;
import com.privasia.procurehere.core.dao.RfpEventDao;
import com.privasia.procurehere.core.dao.RfpEventSupplierDao;
import com.privasia.procurehere.core.dao.RfpSupplierBqDao;
import com.privasia.procurehere.core.dao.RfpSupplierBqItemDao;
import com.privasia.procurehere.core.dao.RfpSupplierCqDao;
import com.privasia.procurehere.core.dao.RfpSupplierCqItemDao;
import com.privasia.procurehere.core.dao.RfpSupplierCqOptionDao;
import com.privasia.procurehere.core.entity.CqOption;
import com.privasia.procurehere.core.entity.RfpCq;
import com.privasia.procurehere.core.entity.RfpCqEvaluationComments;
import com.privasia.procurehere.core.entity.RfpCqItem;
import com.privasia.procurehere.core.entity.RfpCqOption;
import com.privasia.procurehere.core.entity.RfpEnvelop;
import com.privasia.procurehere.core.entity.RfpEvent;
import com.privasia.procurehere.core.entity.RfpSupplierBq;
import com.privasia.procurehere.core.entity.RfpSupplierCq;
import com.privasia.procurehere.core.entity.RfpSupplierCqItem;
import com.privasia.procurehere.core.entity.RfpSupplierCqOption;
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
import com.privasia.procurehere.service.RfpCqService;
import com.privasia.procurehere.service.RfpEventService;
import com.privasia.procurehere.service.RfpSupplierCqItemService;
import com.privasia.procurehere.service.RfpSupplierCqService;
import com.privasia.procurehere.service.supplier.SupplierService;

/**
 * @author Vipul
 */

@Service
@Transactional(readOnly = true)
public class RfpSupplierCqItemServiceImpl implements RfpSupplierCqItemService {

	private static final Logger LOG = LogManager.getLogger(Global.SUPPLIER_LOG);

	@Autowired
	RfpSupplierCqItemDao supplierCqItemDao;

	@Autowired
	RfpCqDao cqDao;

	@Autowired
	RfpEventDao eventDao;

	@Autowired
	RfpCqItemDao cqItemDao;

	@Autowired
	SupplierService supplierService;

	@Autowired
	RfpCqOptionDao cqOptionDao;

	@Autowired
	RfpEventSupplierDao eventSupplierDao;

	@Autowired
	RfpEnvelopDao envelopDao;

	@Autowired
	RfpBqItemDao rfpBqItemDao;

	@Autowired
	RfpSupplierBqItemDao rfpSupplierBqItemDao;

	@Autowired
	RfpSupplierBqDao rfpSupplierBqDao;

	@Autowired
	RfpBqDao rfpBqDao;

	@Autowired
	RfpSorDao rfpSorDao;

	@Autowired
	RfpSupplierSorDao rfpSupplierSorDao;

	@Autowired
	RfpCqService rfpCqService;

	@Autowired
	RfpEventService rfpEventService;

	@Autowired
	EventAuditService eventAuditService;

	@Autowired
	RfpSupplierCqOptionDao rfpSupplierCqOptionDao;

	@Autowired
	RfpSupplierCqDao rfpSupplierCqDao;

	@Autowired
	RfpSupplierCqService rfpSupplierCqService;

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
			RfpEnvelop envelop = envelopDao.findById(envelopId);
			if (envelop.getRfxEvent() != null) {
				if (envelop.getRfxEvent().getViewSupplerName() != null && !envelop.getRfxEvent().getViewSupplerName() && !envelop.getRfxEvent().getDisableMasking()) {
					for (Supplier supplier : suppliers) {
						supplier.setCompanyName(MaskUtils.maskName(envelop.getPreFix(), supplier.getId(), envelop.getId()));
					}

				}
			}

			// List<String> cqIds = envelopDao.getCqsByEnvelopId(envelopIds);
			List<CqPojo> cqPojo = envelopDao.getCqsIdListByEnvelopIdByOrder(envelopIds);
			for (CqPojo cqObj : cqPojo) {
				EventEvaluationPojo response = new EventEvaluationPojo();
				List<List<String>> data = new ArrayList<List<String>>();
				List<RfpCqItem> cqItems = cqItemDao.findCqItemsForCq(cqObj.getId());
				for (RfpCqItem cqItem : cqItems) {
					response.setName(cqObj.getName());

					List<String> answers = new ArrayList<String>();
					List<RfpSupplierCqItem> responseList = supplierCqItemDao.findSupplierCqItemsByCqItemIdAndEventId(cqItem.getId(), eventId, suppliers);
					answers.add(cqItem.getLevel() + "." + cqItem.getOrder());
					boolean leadEvalComment = false;
					String comment = rfpCqService.getLeadEvaluatorComment(cqItem.getId());
					if (StringUtils.checkString(comment).length() > 0) {
						leadEvalComment = true;
					}
					answers.add(cqItem.getId() + "" + (leadEvalComment ? 1 : 0) + "-" + cqItem.getItemName());
					answers.add(cqItem.getId());
					response.setItemId(cqItem.getId());

					int supplierIndex = 0;
					for (RfpSupplierCqItem item : responseList) {

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
						boolean commentsExist = false;
						String supplierId = "";
						String itemId = "";
						response.setSupplierId(item.getSupplier().getId());
						if (item.getCqItem().getCqType() == CqType.TEXT || item.getCqItem().getCqType() == CqType.NUMBER || item.getCqItem().getCqType() == CqType.DATE || item.getCqItem().getCqType() == CqType.PARAGRAPH) {
							if (item.getCqItem().getEvaluationComments() != null && item.getCqItem().getEvaluationComments().size() > 0) {
								for (RfpCqEvaluationComments com : item.getCqItem().getEvaluationComments()) {
									if ((com.getCreatedBy().getId().equals(logedUser.getId()) || (envelop.getLeadEvaluater().getId().equals(logedUser.getId()))) && com.getSupplier().getId().equals(item.getSupplier().getId())) {
										commentsExist = true;
										LOG.info("Comments : " + item.getCqItem().getEvaluationComments().size());
										continue;
									}
								}
							}
							answers.add(commentsExist + "-" + item.getSupplier().getId() + "-" + item.getCqItem().getId() + "-" + item.getTextAnswers());
						} else {
							String str = "";
							List<RfpSupplierCqOption> listAnswers = item.getListAnswers();
							for (RfpSupplierCqOption op : listAnswers) {
								commentsExist = false;
								if (op.getCqItem().getCqItem().getEvaluationComments() != null && op.getCqItem().getCqItem().getEvaluationComments().size() > 0) {
									for (RfpCqEvaluationComments com : op.getCqItem().getCqItem().getEvaluationComments()) {
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
				response.setScoring(sumOfScoring);
				response.setEnvelopId(envelopId);
				response.setEventId(eventId);
				returnList.add(response);
			}
		}

		return returnList;
	}

	@Override
	public List<RfpSupplierCqItem> getAllSupplierCqItemByCqId(String cqId, String supplierId) {
		List<RfpSupplierCqItem> returnList = new ArrayList<RfpSupplierCqItem>();
		List<RfpSupplierCqItem> list = supplierCqItemDao.findSupplierCqItemListByCqId(cqId, supplierId);
		bulidSupplierCqItemList(returnList, list);
		LOG.info("ITEM LIST SIZE : " + list.size());
		return returnList;
	}

	private void bulidSupplierCqItemList(List<RfpSupplierCqItem> returnList, List<RfpSupplierCqItem> list) {
		LOG.info("BUILDING CQ ITEM LIST :: " + list.size());
		if (CollectionUtil.isNotEmpty(list)) {
			for (RfpSupplierCqItem item : list) {
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
	public void saveSupplierEventCq(String cqId, String eventId, String supplierId) {
		List<RfpCqItem> cqItems = supplierCqItemDao.getCqItemsbyId(cqId);
		for (RfpCqItem item : cqItems) {
			LOG.info("ITEM : " + item.toLogString() + "  Options " + item.getCqOptions().size());
			RfpCq cq = item.getCq();
			RfpSupplierCqItem supplierCq = supplierCqItemDao.findCqByEventIdAndCqName(eventId, item.getId());
			if (supplierCq == null) {
				supplierCq = new RfpSupplierCqItem(item);
				supplierCq.setSupplier(new Supplier(supplierId));
				supplierCq = supplierCqItemDao.saveOrUpdateWithFlush(supplierCq);
			}

		}

	}

	@Override
	@Transactional(readOnly = false)
	public void saveSupplierCq(String cqId, String eventId, String supplierId) {
		LOG.info("Saving Supplier Cq.... : ");
		RfpSupplierCq suppliercq = rfpSupplierCqDao.findCqByEventIdAndEventCqId(eventId, cqId, SecurityLibrary.getLoggedInUserTenantId());
		if (suppliercq == null) {
			suppliercq = new RfpSupplierCq();
			RfpEvent rfxEvent = eventDao.findByEventId(eventId);
			suppliercq.setEvent(rfxEvent);
			suppliercq.setSupplier(SecurityLibrary.getLoggedInUser().getSupplier());
			RfpCq rfaCq = cqDao.findById(cqId);
			suppliercq.setCq(rfaCq);
			suppliercq = rfpSupplierCqDao.saveOrUpdate(suppliercq);
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void updateCqItems(List<RfpSupplierCqItem> items) {
		for (RfpSupplierCqItem item : items) {
			List<RfpCqOption> options = item.getListOptAnswers();
			LOG.info("ITEM ID : " + item.getId());
			RfpSupplierCqItem cqItem = supplierCqItemDao.findById(item.getId());
			if (item.getTextAnswers() != null) {
				String textAnswer = item.getTextAnswers().replace('-', '_');
				cqItem.setTextAnswers(textAnswer);
			}
			LOG.info(cqItem.getTextAnswers());

			/*
			 * if (item.getDateAnswer() != null) { cqItem.setDateAnswer(item.getDateAnswer()); }
			 * LOG.info(cqItem.getDateAnswer());
			 */ cqItem.setFileData(item.getFileData());
			cqItem.setFileName(item.getFileName());
			cqItem.setCredContentType(item.getCredContentType());
			List<RfpSupplierCqOption> supOptions = new ArrayList<RfpSupplierCqOption>();
			if (CollectionUtil.isNotEmpty(options)) {
				for (RfpCqOption op : options) {
					RfpCqOption option = cqOptionDao.findById(op.getId());
					RfpSupplierCqOption cqOption = new RfpSupplierCqOption();
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
		List<Supplier> qualifiedSupDb = eventSupplierDao.getEventQualifiedSuppliersForEvaluation(eventId);
		List<Supplier> qualifiedSup = new ArrayList<Supplier>();
		RfpEvent event = rfpEventService.getRfpEventByeventId(eventId);
		RfpEnvelop envolpe = null;

		// Masking will be auto flushed by Hibernate due to dirty checking.
		// This is happening because Dumbo changed the transaction type to readOnly=false for Audit.
		if (event.getViewSupplerName() != null && !event.getViewSupplerName() && !event.getDisableMasking()) {
			envolpe = envelopDao.findById(envelopId);
			for (Supplier supplier : qualifiedSupDb) {
				Supplier supplierCopy = supplier.createShallowCopy();
				supplierCopy.setCompanyName(MaskUtils.maskName(envolpe.getPreFix(), supplier.getId(), envolpe.getId()));
				qualifiedSup.add(supplierCopy);
			}
		} else {
			qualifiedSup = qualifiedSupDb;
		}

		if (CollectionUtil.isNotEmpty(qualifiedSup)) {

			List<String> envelopIds = new ArrayList<String>();
			envelopIds.add(envelopId);
			// List<String> cqIds = envelopDao.getCqsByEnvelopId(envelopIds);
			List<CqPojo> cqList = envelopDao.getCqsIdListByEnvelopIdByOrder(envelopIds);

			for (CqPojo cq : cqList) {
				EventEvaluationPojo response = new EventEvaluationPojo();
				List<List<String>> data = new ArrayList<List<String>>();
				List<RfpCqItem> cqItems = cqItemDao.findCqItemsForCq(cq.getId());
				Integer scoreSum = 0;
				for (RfpCqItem cqItem : cqItems) {
					response.setName(cqItem.getCq().getName());
					response.setTotalScore(cqItem.getTotalScore());
					List<String> answers = new ArrayList<String>();
					// List<RfpSupplierCqItem> responseList =
					// supplierCqItemDao.findSupplierCqItemsByCqItemIdAndEventId(cqItem.getId(), eventId, qualifiedSup);
					List<RfpSupplierCqItem> responseList = supplierCqItemDao.findSupplierCqItemsByCqItemIdAndEventIdNew(cqItem.getId(), eventId, qualifiedSup);
					answers.add(cqItem.getLevel() + "." + cqItem.getOrder());
					answers.add(cqItem.getItemName());
					scoreSum += cqItem.getTotalScore() != null ? cqItem.getTotalScore() : 0;
					answers.add(cqItem.getTotalScore() != null ? String.valueOf(cqItem.getTotalScore()) : " ");
					response.setItemId(cqItem.getId());
					for (RfpSupplierCqItem item : responseList) {
						response.setSupplierId(item.getSupplier().getId());
						if (item.getCqItem().getCqType() == CqType.TEXT || item.getCqItem().getCqType() == CqType.DATE || item.getCqItem().getCqType() == CqType.NUMBER || item.getCqItem().getCqType() == CqType.PARAGRAPH) {
							answers.add(item.getTextAnswers());
							answers.add("");
						} else if (item.getCqItem().getCqType() == CqType.LIST || item.getCqItem().getCqType() == CqType.CHECKBOX) {
							// List<RfpSupplierCqOption> listAnswers = item.getListAnswers();
							List<RfpSupplierCqOption> listAnswers = rfpSupplierCqOptionDao.findSupplierCqOptionsListWithCqByCqId(item.getId());
							String ans = "";
							for (RfpSupplierCqOption op : listAnswers) {
								ans += op.getValue() + "\n";
							}
							answers.add(ans);
							answers.add("");
						} else {
							List<RfpSupplierCqOption> listAnswers = rfpSupplierCqOptionDao.findSupplierCqOptionsListWithCqByCqId(item.getId());
							// List<RfpSupplierCqOption> listAnswers = item.getListAnswers();
							for (RfpSupplierCqOption op : listAnswers) {
								answers.add(op.getValue());
								answers.add(op.getScoring() != null ? String.valueOf(op.getScoring()) : " ");
							}
						}
					}
					data.add(answers);
				}
				response.setData(data);
				response.setColumns(qualifiedSup);
				List<String> sumOfScoring = supplierCqItemDao.findSumOfScoringForSupplier(cq.getId(), eventId, qualifiedSup);
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

		List<String> bqNames = rfpBqDao.rfpBqNamesByEventId(eventId);
		if (CollectionUtil.isNotEmpty(bqNames) && bqNames.size() > 0) {
			List<RfpSupplierBq> rfpSupplierBqList = rfpSupplierBqDao.rfpSupplierBqAnswerdByEventIdAndSupplierId(eventId, supplierId);
			if (CollectionUtil.isEmpty(rfpSupplierBqList) || (CollectionUtil.isNotEmpty(rfpSupplierBqList)) && rfpSupplierBqList.size() != bqNames.size()) {
				if (CollectionUtil.isNotEmpty(rfpSupplierBqList)) {
					for (RfpSupplierBq bq : rfpSupplierBqList) {
						bqNames.remove(bq.getName());
					}
				}
			} else {
				// All OK
				bqNames.clear();
			}
		}

		List<String> sorNames = rfpSorDao.rfpSorNamesByEventId(eventId);
		if (CollectionUtil.isNotEmpty(sorNames) && sorNames.size() > 0) {
			List<RfpSupplierSor> supplierSorList = rfpSupplierSorDao.rfpSupplierSorAnswerdByEventIdAndSupplierId(eventId, supplierId);
			if (CollectionUtil.isEmpty(supplierSorList) || (CollectionUtil.isNotEmpty(supplierSorList)) && supplierSorList.size() != sorNames.size()) {
				if (CollectionUtil.isNotEmpty(supplierSorList)) {
					for (RfpSupplierSor bq : supplierSorList) {
						sorNames.remove(bq.getName());
					}
				}
			} else {
				// All OK
				sorNames.clear();
			}
		}

		List<String> cqNames = new ArrayList<>();
		List<RfpCq> rfpCqItemList = cqItemDao.rfpMandatoryCqNamesbyEventId(eventId);
		if (CollectionUtil.isNotEmpty(rfpCqItemList)) {
			List<RfpCq> supplierRequiredCqItemList = supplierCqItemDao.findRequiredSupplierCqCountByEventId(supplierId, eventId);

			for (RfpCq rfpCq : rfpCqItemList) {
				boolean found = false;
				for (RfpCq supCq : supplierRequiredCqItemList) {
					if (supCq.getName().equals(rfpCq.getName())) {
						found = true;
						if (supCq.getMandatoryItemCount() != rfpCq.getMandatoryItemCount()) {
							cqNames.add(rfpCq.getName());
						}
					}
				}
				if (!found) {
					cqNames.add(rfpCq.getName());
				}
			}
		}

		if (CollectionUtil.isNotEmpty(cqNames) && CollectionUtil.isNotEmpty(bqNames) && CollectionUtil.isNotEmpty(sorNames)) {
			LOG.error("Please fill up all mandatory  Questionnaire , all Bill Of Quantity and Schedule of Rate");
			throw new NotAllowedException("Please fill up all mandatory Questionnaire , all Bill Of Quantity and Schedule of Rate");
		} else if (CollectionUtil.isNotEmpty(cqNames) && CollectionUtil.isNotEmpty(bqNames)) {
			LOG.error("Please fill up all mandatory Questionnaire and all Bill Of Quantity");
			throw new NotAllowedException("Please fill up all mandatory Questionnaire and all Bill Of Quantity");
		} else if (CollectionUtil.isNotEmpty(bqNames) && CollectionUtil.isNotEmpty(sorNames)) {
			LOG.error("Please fill up all mandatory all Bill Of Quantity and Schedule of Rate");
			throw new NotAllowedException("Please fill up all Bill Of Quantity and Schedule of Rate");
		} else if (CollectionUtil.isNotEmpty(cqNames) && CollectionUtil.isNotEmpty(sorNames)) {
			LOG.error("Please fill up all mandatory all Questionnaire and Schedule of Rate");
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

		long pendingBq = rfpSupplierBqDao.findPendingBqAnswerdByEventIdAndSupplierId(eventId, supplierId);
		long pendingCq = rfpSupplierCqDao.findPendingCqsByEventIdAndEventCqId(eventId, supplierId);
		long pendingSor = rfpSupplierSorDao.findPendingSorsByEventIdAndEventSorId(eventId, supplierId);

		if (pendingBq > 0 && pendingCq > 0 && pendingSor > 0) {
			throw new NotAllowedException("Please complete all Questionnaire, all Bill of Quantity and all schedule of rate");
		}

		if (pendingBq > 0) {
			throw new NotAllowedException("Please complete all Bill of Quantity");
		}

		if (pendingCq > 0) {
			throw new NotAllowedException("Please complete all Questionnaire ");
		}

		if(pendingSor > 0) {
			throw new NotAllowedException("Please complete all Schedule of rate");
		}

		List<String> supplierAttach = cqItemDao.findAllSupplierAttachRequiredId(eventId);
		if (CollectionUtil.isNotEmpty(supplierAttach)) {
			int rfpCqCount = supplierCqItemDao.findRfpRequiredCqCountBySupplierId(supplierId, eventId);
			if (rfpCqCount > 0) {
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
			RfpSupplierCqItem item = supplierCqItemDao.findCqByEventIdAndCqName(eventId, itemId);
			if (item != null) {
				RfpSupplierCq rfiSupplierCq = rfpSupplierCqDao.findSupplierCqByEventIdAndSupplierId(tenantId, eventId, item.getCq().getId());
				if (rfiSupplierCq != null) {
					rfiSupplierCq.setSupplierCqStatus(SupplierCqStatus.DRAFT);
					rfpSupplierCqService.updateSupplierCq(rfiSupplierCq);
				}
			}
		}
		return removed;
	}

	@Override
	public RfpSupplierCqItem findCqByEventIdAndCqName(String eventId, String cqId) {
		return supplierCqItemDao.findCqByEventIdAndCqName(eventId, cqId);
	}

	@Override
	public List<RfpSupplierCqItem> getSupplierCqItemsbySupplierIdAndEventId(String eventId, String supplierId) {
		List<RfpSupplierCqItem> returnList = new ArrayList<RfpSupplierCqItem>();
		List<RfpSupplierCqItem> list = supplierCqItemDao.getSupplierCqItemsbySupplierIdAndEventId(eventId, supplierId);
		bulidSupplierCqItemList(returnList, list);
		return returnList;
	}

	@Override
	@Transactional(readOnly = false)
	public void draftUpdateCqItems(List<RfpSupplierCqItem> items, String eventId, SupplierCqStatus status, User loggedInUser) {
		saveCqItems(items, eventId, status, loggedInUser);
	}

	@Override
	@Transactional(readOnly = false, noRollbackFor = NotAllowedException.class)
	public void updateCqItems(List<RfpSupplierCqItem> items, String eventId, SupplierCqStatus status, User loggedInUser) throws NotAllowedException {
		saveCqItems(items, eventId, status, loggedInUser);
		int count = supplierCqItemDao.findRfpRequiredCqCountBySupplierIdAndCqId(loggedInUser.getTenantId(), eventId, items.get(0).getCq().getId());
		LOG.info("Count : " + count);
		if (count > 0) {
			RfpSupplierCq rfpSupplierCq = rfpSupplierCqDao.findSupplierCqByEventIdAndSupplierId(loggedInUser.getTenantId(), eventId, items.get(0).getCq().getId());
			if (rfpSupplierCq != null) {
				rfpSupplierCq.setSupplierCqStatus(SupplierCqStatus.DRAFT);
				rfpSupplierCqService.updateSupplierCq(rfpSupplierCq);
			}

			List<String> cqAttachments = supplierCqItemDao.findRequiredCqsBySupplierId(loggedInUser.getTenantId(), eventId, items.get(0).getCq().getId());
			if (CollectionUtil.isNotEmpty(cqAttachments)) {
				String cqItems = org.apache.commons.lang.StringUtils.join(cqAttachments, ",");
				if (StringUtils.checkString(cqItems).length() > 0) {
					throw new NotAllowedException("Please attach required documents for item <" + cqItems + ">");
				}
			}
			// throw new NotAllowedException("Please Attach All required Documents in Questionnaires");
		}
	}

	private void saveCqItems(List<RfpSupplierCqItem> items, String eventId, SupplierCqStatus status, User loggedInUser) {
		String cqId = "";
		for (RfpSupplierCqItem item : items) {
			List<RfpCqOption> options = item.getListOptAnswers();
			LOG.info("ITEM ID : " + item.getId());
			RfpSupplierCqItem cqItem = supplierCqItemDao.findById(item.getId());
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
			 */ cqItem.setFileData(item.getFileData());
			cqItem.setFileName(item.getFileName());
			cqItem.setCredContentType(item.getCredContentType());
			List<RfpSupplierCqOption> supOptions = new ArrayList<RfpSupplierCqOption>();
			if (CollectionUtil.isNotEmpty(options)) {
				for (RfpCqOption op : options) {
					RfpCqOption option = cqOptionDao.findById(op.getId());
					RfpSupplierCqOption cqOption = new RfpSupplierCqOption();
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

		RfpSupplierCq rfpSupplierCq = rfpSupplierCqDao.findSupplierCqByEventIdAndSupplierId(loggedInUser.getTenantId(), eventId, cqId);
		if (rfpSupplierCq != null) {
			rfpSupplierCq.setSupplierCqStatus(status);
			rfpSupplierCqService.updateSupplierCq(rfpSupplierCq);
		}
	}

}

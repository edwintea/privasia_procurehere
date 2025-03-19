package com.privasia.procurehere.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.privasia.procurehere.core.dao.RfiSorDao;
import com.privasia.procurehere.core.dao.RfiSupplierSorDao;
import com.privasia.procurehere.core.entity.RfiSupplierSor;
import com.privasia.procurehere.core.entity.RftSupplierSor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.privasia.procurehere.core.dao.RfiCqDao;
import com.privasia.procurehere.core.dao.RfiCqEvaluationCommentsDao;
import com.privasia.procurehere.core.dao.RfiCqItemDao;
import com.privasia.procurehere.core.dao.RfiCqOptionDao;
import com.privasia.procurehere.core.dao.RfiEnvelopDao;
import com.privasia.procurehere.core.dao.RfiEventDao;
import com.privasia.procurehere.core.dao.RfiEventSupplierDao;
import com.privasia.procurehere.core.dao.RfiSupplierCqDao;
import com.privasia.procurehere.core.dao.RfiSupplierCqItemDao;
import com.privasia.procurehere.core.dao.RfiSupplierCqOptionDao;
import com.privasia.procurehere.core.entity.CqOption;
import com.privasia.procurehere.core.entity.RfiCq;
import com.privasia.procurehere.core.entity.RfiCqEvaluationComments;
import com.privasia.procurehere.core.entity.RfiCqItem;
import com.privasia.procurehere.core.entity.RfiCqOption;
import com.privasia.procurehere.core.entity.RfiEnvelop;
import com.privasia.procurehere.core.entity.RfiEvent;
import com.privasia.procurehere.core.entity.RfiSupplierCq;
import com.privasia.procurehere.core.entity.RfiSupplierCqItem;
import com.privasia.procurehere.core.entity.RfiSupplierCqOption;
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
import com.privasia.procurehere.service.RfiCqService;
import com.privasia.procurehere.service.RfiEventService;
import com.privasia.procurehere.service.RfiSupplierCqItemService;
import com.privasia.procurehere.service.RfiSupplierCqService;
import com.privasia.procurehere.service.supplier.SupplierService;

/**
 * @author Vipul
 */

@Service
@Transactional(readOnly = true)
public class RfiSupplierCqItemServiceImpl implements RfiSupplierCqItemService {

	private static final Logger LOG = LogManager.getLogger(Global.SUPPLIER_LOG);

	@Autowired
	RfiSupplierCqItemDao supplierCqItemDao;

	@Autowired
	RfiCqDao cqDao;

	@Autowired
	RfiEventDao eventDao;

	@Autowired
	RfiCqItemDao cqItemDao;

	@Autowired
	SupplierService supplierService;

	@Autowired
	RfiCqOptionDao cqOptionDao;

	@Autowired
	RfiEventSupplierDao eventSupplierDao;

	@Autowired
	RfiEnvelopDao envelopDao;

	@Autowired
	RfiCqService rfiCqService;

	@Autowired
	RfiEventService rfiEventService;

	@Autowired
	EventAuditService eventAuditService;

	@Autowired
	RfiCqEvaluationCommentsDao rfiCqEvaluationCommentsDao;

	@Autowired
	RfiSupplierCqOptionDao rfiSupplierCqOptionDao;

	@Autowired
	RfiSupplierCqDao rfiSupplierCqDao;

	@Autowired
	RfiSupplierCqService rfiSupplierCqService;

	@Autowired
	RfiSorDao rfiSorDao;

	@Autowired
	RfiSupplierSorDao rfiSupplierSorDao;

	@Override
	public List<EventEvaluationPojo> getCqEvaluationData(String eventId, String envelopId, List<Supplier> selectedSuppliers, User logedUser) {
		LOG.info("Geeting CQs for Event " + eventId + " Envelop : " + envelopId);
		List<EventEvaluationPojo> returnList = new ArrayList<>();
		List<Supplier> suppliers = null;

		if (CollectionUtil.isEmpty(selectedSuppliers)) {
			suppliers = eventSupplierDao.getEventSuppliersForEvaluation(eventId);
		} else {
			suppliers = eventSupplierDao.getEventSuppliersForEvaluation(eventId, selectedSuppliers);
		}

		if (CollectionUtil.isNotEmpty(suppliers)) {
			List<String> envelopIds = new ArrayList<String>();
			envelopIds.add(envelopId);
			// List<String> cqIds = envelopDao.getCqsByEnvelopId(envelopIds);
			List<CqPojo> cqPojo = envelopDao.getCqsIdListByEnvelopIdByOrder(envelopIds);

			RfiEnvelop envelop = envelopDao.findById(envelopId);

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
				List<RfiCqItem> cqItems = cqItemDao.findCqItemsForCq(cqObj.getId());
				for (RfiCqItem cqItem : cqItems) {
					response.setName(cqObj.getName());

					List<String> answers = new ArrayList<String>();
					// List<RfiSupplierCqItem> responseList =
					// supplierCqItemDao.findSupplierCqItemsByCqItemIdAndEventId(cqItem.getId(), eventId, suppliers);
					List<RfiSupplierCqItem> responseList = supplierCqItemDao.findSupplierCqItemsByCqItemIdAndEventIdAndSuppliers(cqItem.getId(), eventId, suppliers);

					answers.add(cqItem.getLevel() + "." + cqItem.getOrder());
					boolean leadEvalComment = false;
					String comment = rfiCqService.getLeadEvaluatorComment(cqItem.getId());
					if (StringUtils.checkString(comment).length() > 0) {
						leadEvalComment = true;
					}
					answers.add(cqItem.getId() + "" + (leadEvalComment ? 1 : 0) + "-" + cqItem.getItemName());
					answers.add(cqItem.getId());
					response.setItemId(cqItem.getId());
					int supplierIndex = 0;
					for (RfiSupplierCqItem item : responseList) {
						while (!suppliers.get(supplierIndex).getId().equals(item.getSupplier().getId())) {
							LOG.info("Supplier CQ response missing : " + suppliers.get(supplierIndex).getId() + " - " + supplierIndex);
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
							List<RfiCqEvaluationComments> comments = rfiCqEvaluationCommentsDao.getCommentsForSupplier(item.getSupplier(), eventId, item.getCqItem().getId(), logedUser);
							if (comments != null && comments.size() > 0) {
								for (RfiCqEvaluationComments com : comments) {
									if ((com.getCreatedBy().getId().equals(logedUser.getId()) || (envelop.getLeadEvaluater().getId().equals(logedUser.getId()))) && com.getSupplier().getId().equals(item.getSupplier().getId())) {
										commentsExist = true;
										continue;
									}
								}
							}
							answers.add(commentsExist + "-" + item.getSupplier().getId() + "-" + item.getCqItem().getId() + "-" + item.getTextAnswers());
						} else {
							String str = "";
							List<RfiSupplierCqOption> listAnswers = rfiSupplierCqOptionDao.findSupplierCqOptionsListByCqId(item.getId());
							// List<RfiSupplierCqOption> listAnswers = item.getListAnswers();
							for (RfiSupplierCqOption op : listAnswers) {
								commentsExist = false;
								List<RfiCqEvaluationComments> comments = rfiCqEvaluationCommentsDao.getCommentsForSupplier(item.getSupplier(), eventId, item.getCqItem().getId(), logedUser);
								if (comments != null && comments.size() > 0) {
									for (RfiCqEvaluationComments com : comments) {
										if ((com.getCreatedBy().getId().equals(logedUser.getId()) || (envelop.getLeadEvaluater().getId().equals(logedUser.getId()))) && com.getSupplier().getId().equals(item.getSupplier().getId())) {
											commentsExist = true;
											LOG.info("Comments : " + comments.size());
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
	public List<RfiSupplierCqItem> getAllSupplierCqItemByCqId(String cqId, String supplierId) {
		List<RfiSupplierCqItem> returnList = new ArrayList<RfiSupplierCqItem>();
		List<RfiSupplierCqItem> list = supplierCqItemDao.findSupplierCqItemListByCqId(cqId, supplierId);
		bulidSupplierCqItemList(returnList, list);
		LOG.info("ITEM LIST SIZE : " + list.size());
		return returnList;
	}

	private void bulidSupplierCqItemList(List<RfiSupplierCqItem> returnList, List<RfiSupplierCqItem> list) {
		LOG.info("BUILDING CQ ITEM LIST :: " + list.size());
		if (CollectionUtil.isNotEmpty(list)) {
			for (RfiSupplierCqItem item : list) {
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
		List<RfiCqItem> cqItems = supplierCqItemDao.getCqItemsbyId(cqId);
		for (RfiCqItem item : cqItems) {
			LOG.info("ITEM : " + item.toLogString() + "  Options " + item.getCqOptions().size());
			RfiCq cq = item.getCq();
			RfiSupplierCqItem supplierCq = supplierCqItemDao.findCqByEventIdAndCqName(eventId, item.getId());
			LOG.info("RftSupplierCq 1:" + supplierCq);
			if (supplierCq == null) {
				supplierCq = new RfiSupplierCqItem(item);
				supplierCq.setSupplier(new Supplier(supplierId));
				supplierCq = supplierCqItemDao.saveOrUpdateWithFlush(supplierCq);
			}
		}

	}

	@Override
	@Transactional(readOnly = false)
	public void saveSupplierCq(String cqId, String eventId, String supplierId) {
		LOG.info("Saving Supplier Cq.... : ");
		RfiSupplierCq suppliercq = rfiSupplierCqDao.findCqByEventIdAndEventCqId(eventId, cqId, SecurityLibrary.getLoggedInUserTenantId());
		if (suppliercq == null) {
			suppliercq = new RfiSupplierCq();
			RfiEvent rfxEvent = eventDao.findByEventId(eventId);
			suppliercq.setEvent(rfxEvent);
			suppliercq.setSupplier(SecurityLibrary.getLoggedInUser().getSupplier());
			RfiCq rfaCq = cqDao.findById(cqId);
			suppliercq.setCq(rfaCq);
			suppliercq = rfiSupplierCqDao.saveOrUpdate(suppliercq);
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void updateCqItems(List<RfiSupplierCqItem> items) {
		for (RfiSupplierCqItem item : items) {
			List<RfiCqOption> options = item.getListOptAnswers();
			LOG.info("ITEM ID : " + item.getId());
			RfiSupplierCqItem cqItem = supplierCqItemDao.findById(item.getId());
			if (item.getTextAnswers() != null) {
				String textAnswer = item.getTextAnswers().replace('-', '_');
				cqItem.setTextAnswers(textAnswer);
			}
			LOG.info(cqItem.getTextAnswers());

			// if (item.getDateAnswer() != null) {
			// cqItem.setDateAnswer(item.getDateAnswer());
			// }
			// LOG.info(cqItem.getDateAnswer());
			cqItem.setFileData(item.getFileData());
			cqItem.setFileName(item.getFileName());
			cqItem.setCredContentType(item.getCredContentType());
			List<RfiSupplierCqOption> supOptions = new ArrayList<RfiSupplierCqOption>();
			if (CollectionUtil.isNotEmpty(options)) {
				for (RfiCqOption op : options) {
					RfiCqOption option = cqOptionDao.findById(op.getId());
					RfiSupplierCqOption cqOption = new RfiSupplierCqOption();
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
		RfiEvent event = rfiEventService.getPlainEventById(eventId);
		RfiEnvelop envolpe = null;

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
				List<RfiCqItem> cqItems = cqItemDao.findCqItemsForCq(cq.getId());
				Integer scoreSum = 0;
				for (RfiCqItem cqItem : cqItems) {
					response.setName(cqItem.getCq().getName());
					response.setTotalScore(cqItem.getTotalScore());
					List<String> answers = new ArrayList<String>();
					// List<RfiSupplierCqItem> responseList =
					// supplierCqItemDao.findSupplierCqItemsByCqItemIdAndEventId(cqItem.getId(), eventId, qualifiedSup);
					List<RfiSupplierCqItem> responseList = supplierCqItemDao.findSupplierCqItemsByCqItemIdAndEventIdNew(cqItem.getId(), eventId, qualifiedSup);
					answers.add(cqItem.getLevel() + "." + cqItem.getOrder());
					answers.add(cqItem.getItemName());
					scoreSum += cqItem.getTotalScore() != null ? cqItem.getTotalScore() : 0;
					answers.add(cqItem.getTotalScore() != null ? String.valueOf(cqItem.getTotalScore()) : " ");
					response.setItemId(cqItem.getId());
					for (RfiSupplierCqItem item : responseList) {
						response.setSupplierId(item.getSupplier().getId());
						if (item.getCqItem().getCqType() == CqType.TEXT || item.getCqItem().getCqType() == CqType.DATE || item.getCqItem().getCqType() == CqType.NUMBER || item.getCqItem().getCqType() == CqType.PARAGRAPH) {
							answers.add(item.getTextAnswers());
							answers.add("");
						} else if (item.getCqItem().getCqType() == CqType.LIST || item.getCqItem().getCqType() == CqType.CHECKBOX) {
							// List<RfiSupplierCqOption> listAnswers = item.getListAnswers();
							List<RfiSupplierCqOption> listAnswers = rfiSupplierCqOptionDao.findSupplierCqOptionsListByCqId(item.getId());
							String ans = "";
							for (RfiSupplierCqOption op : listAnswers) {
								ans += op.getValue() + "\n";
							}
							answers.add(ans);
							answers.add("");
						} else {
							// List<RfiSupplierCqOption> listAnswers = item.getListAnswers();
							List<RfiSupplierCqOption> listAnswers = rfiSupplierCqOptionDao.findSupplierCqOptionsListWithCqByCqId(item.getId());
							for (RfiSupplierCqOption op : listAnswers) {
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
	public List<Supplier> getEventSuppliersForEvaluation(String eventId) {
		return eventSupplierDao.getEventSuppliersForEvaluation(eventId);
	}

	@Override
	public Boolean checkMandatoryToFinishEvent(String supplierId, String eventId) throws NotAllowedException, Exception {

		List<String> cqNames = new ArrayList<>();
		List<RfiCq> cqItemList = cqItemDao.rfiMandatoryCqNamesbyEventId(eventId);
		if (CollectionUtil.isNotEmpty(cqItemList)) {
			List<RfiCq> supplierRequiredCqItemList = supplierCqItemDao.findRequiredSupplierCqCountByEventId(supplierId, eventId);
			LOG.info("supplierRequiredCqItemList +  " + supplierRequiredCqItemList.size());
			for (RfiCq cq : cqItemList) {
				boolean found = false;
				for (RfiCq supCq : supplierRequiredCqItemList) {
					LOG.info("SUpp Cq " + supCq.getName() + " Cq Name " + cq.getName());
					LOG.info(supCq.getName().equals(cq.getName()));
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

		List<String> sorNames = rfiSorDao.rfiSorNamesByEventId(eventId);
		if (CollectionUtil.isNotEmpty(sorNames) && sorNames.size() > 0) {
			List<RfiSupplierSor> supplierSorList = rfiSupplierSorDao.rfiSupplierSorAnswerdByEventIdAndSupplierId(eventId, supplierId);
			if (CollectionUtil.isEmpty(supplierSorList) || (CollectionUtil.isNotEmpty(supplierSorList)) && supplierSorList.size() != sorNames.size()) {
				if (CollectionUtil.isNotEmpty(supplierSorList)) {
					for (RfiSupplierSor bq : supplierSorList) {
						sorNames.remove(bq.getName());
					}
				}
			} else {
				// All OK
				sorNames.clear();
			}
		}

		if (CollectionUtil.isNotEmpty(cqNames) && CollectionUtil.isNotEmpty(sorNames)) {
			LOG.error("Please fill up all mandatory Questionnaire and all Schedule of Rate");
			throw new NotAllowedException("Please fill up all mandatory Questionnaire and all Schedule Or Rate");
		} else if (CollectionUtil.isNotEmpty(cqNames)) {
			throw new NotAllowedException("Please fill up mandatory Questionnaire : " + cqNames);
		} else if (CollectionUtil.isNotEmpty(sorNames)) {
			throw new NotAllowedException("Please fill up mandatory Schedule Or Rate : " + sorNames);
		}
		long pendingCq = rfiSupplierCqDao.findPendingCqsByEventIdAndEventCqId(eventId, supplierId);
		long pendingSor = rfiSupplierSorDao.findPendingSorsByEventIdAndEventSorId(eventId, supplierId);

		if (pendingCq > 0 && pendingSor > 0) {
			throw new NotAllowedException("Please complete all Questionnaire and all schedule of rate");
		}

		if (pendingCq > 0) {
			throw new NotAllowedException("Please complete all Questionnaire");
		}

		if(pendingSor > 0) {
			throw new NotAllowedException("Please complete all Schedule of rate");
		}

		List<String> supplierAttach = cqItemDao.findAllSupplierAttachRequiredId(eventId);
		if (CollectionUtil.isNotEmpty(supplierAttach)) {
			int rfiCqCount = supplierCqItemDao.findRfiRequiredCqCountBySupplierId(supplierId, eventId);
			if (rfiCqCount > 0) {
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
			RfiSupplierCqItem item = supplierCqItemDao.findCqByEventIdAndCqName(eventId, itemId);
			if (item != null) {
				RfiSupplierCq rfiSupplierCq = rfiSupplierCqDao.findSupplierCqByEventIdAndSupplierId(tenantId, eventId, item.getCq().getId());
				if (rfiSupplierCq != null) {
					rfiSupplierCq.setSupplierCqStatus(SupplierCqStatus.DRAFT);
					rfiSupplierCqService.updateSupplierCq(rfiSupplierCq);
				}
			}
		}
		return removed;
	}

	@Override
	public RfiSupplierCqItem findCqByEventIdAndCqItem(String eventId, String cqId) {
		return supplierCqItemDao.findCqByEventIdAndCqName(eventId, cqId);
	}

	@Override
	public List<RfiSupplierCqItem> getSupplierCqItemsbySupplierIdAndEventId(String eventId, String supplierId) {
		List<RfiSupplierCqItem> returnList = new ArrayList<RfiSupplierCqItem>();
		List<RfiSupplierCqItem> list = supplierCqItemDao.getSupplierCqItemsbySupplierIdAndEventId(eventId, supplierId);
		bulidSupplierCqItemList(returnList, list);
		LOG.info("===========returnList.size()============" + returnList.size());
		return returnList;
	}

	@Override
	@Transactional(readOnly = false)
	public void draftUpdateCqItems(List<RfiSupplierCqItem> items, String eventId, SupplierCqStatus status, User loggedInUser) {
		saveCqItems(items, eventId, status, loggedInUser);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateCqItems(List<RfiSupplierCqItem> items, String eventId, SupplierCqStatus status, User loggedInUser) throws NotAllowedException {
		saveCqItems(items, eventId, status, loggedInUser);
		int count = supplierCqItemDao.findRfiRequiredCqCountBySupplierIdAndCqId(loggedInUser.getTenantId(), eventId, items.get(0).getCq().getId());
		if (count > 0) {
			RfiSupplierCq rfiSupplierCq = rfiSupplierCqDao.findSupplierCqByEventIdAndSupplierId(loggedInUser.getTenantId(), eventId, items.get(0).getCq().getId());
			if (rfiSupplierCq != null) {
				rfiSupplierCq.setSupplierCqStatus(SupplierCqStatus.DRAFT);
				rfiSupplierCqService.updateSupplierCq(rfiSupplierCq);
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

	private void saveCqItems(List<RfiSupplierCqItem> items, String eventId, SupplierCqStatus status, User loggedInUser) {
		String cqId = "";
		for (RfiSupplierCqItem item : items) {
			List<RfiCqOption> options = item.getListOptAnswers();
			LOG.info("ITEM ID : " + item.getId());
			RfiSupplierCqItem cqItem = supplierCqItemDao.findById(item.getId());
			if (item.getTextAnswers() != null) {
				String textAnswer = item.getTextAnswers().replace('-', '_');
				cqItem.setTextAnswers(textAnswer);
			}
			LOG.info(cqItem.getTextAnswers());
			if (StringUtils.checkString(cqId).length() == 0) {
				cqId = item.getCq().getId();
			}

			// if (item.getDateAnswer() != null) {
			// cqItem.setDateAnswer(item.getDateAnswer());
			// }
			// LOG.info(cqItem.getDateAnswer());
			cqItem.setFileData(item.getFileData());
			cqItem.setFileName(item.getFileName());
			cqItem.setCredContentType(item.getCredContentType());
			List<RfiSupplierCqOption> supOptions = new ArrayList<RfiSupplierCqOption>();
			if (CollectionUtil.isNotEmpty(options)) {
				for (RfiCqOption op : options) {
					RfiCqOption option = cqOptionDao.findById(op.getId());
					RfiSupplierCqOption cqOption = new RfiSupplierCqOption();
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

		RfiSupplierCq rfiSupplierCq = rfiSupplierCqDao.findSupplierCqByEventIdAndSupplierId(SecurityLibrary.getLoggedInUserTenantId(), eventId, cqId);
		if (rfiSupplierCq != null) {
			rfiSupplierCq.setSupplierCqStatus(status);
			rfiSupplierCqService.updateSupplierCq(rfiSupplierCq);
		}
	}

}

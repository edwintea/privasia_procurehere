<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<c:if test="${!empty cqList}">
	<div class="panel sum-accord">
		<div class="panel-heading">
			<h4 class="panel-title"> 
				<a data-toggle="collapse" class="accordion" href="#collapseQuestionnaire"> <spring:message code="questionnaire.label" /> </a>
			</h4>
		</div>
		<div id="collapseQuestionnaire" class="panel-collapse collapse">
			<div class="panel-body pad_all_15">
				<c:forEach items="${cqList}" var="suppCq">
					<div class="panel sum-accord">
						<div class="panel-heading">
							<h4 class="panel-title">
								<a data-toggle="collapse" data-parent="#accordion1" class="accordion" href="#collapse_cq_${suppCq.key.id}">${suppCq.key.name}</a>
								<button class="Quesbtn"></button>
							</h4>
						</div>
						<div id="collapse_cq_${suppCq.key.id}" class="panel-collapse collapse" aria-expanded="true">
							<div class="panel-body pad_all_15">
								<h3>${suppCq.key.description}</h3>
								<div class="panel-body-inner main_table_wrapper">
									<table class="table ph_table" width="100%" border="0" cellspacing="0" cellpadding="0">
										<tbody>
											<c:forEach var="item" items="${suppCq.value}" varStatus="status">

												<tr class="<c:if test="${item.cqItem.order > 0}">sub_item</c:if><c:if test="${item.cqItem.order == 0}"> border-top-width-1</c:if>">
													<td class="for-left minimizePadding">
														<div class="col-md-12 <c:if test="${item.cqItem.cqType == 'CHOICE' || item.cqItem.cqType == 'LIST' || item.cqItem.cqType == 'CHECKBOX'}">marg-bottom-10</c:if>">
															<div class="row">
																<span class="col-md-1 pull-left disablePaddings">${item.cqItem.optional ? '*' : ''} ${item.cqItem.level}.${item.cqItem.order}</span> <span class="col-md-11 pull-left disablePaddings"> <b>${item.cqItem.itemName}</b> <br> <span class="item_detail s1_text_small">${item.cqItem.itemDescription}</span>
																</span>
															</div>
														</div> 
														<c:if test="${item.cqItem.cqType == 'CHOICE' || item.cqItem.cqType == 'LIST' || item.cqItem.cqType == 'CHECKBOX' || item.cqItem.cqType == 'TEXT' || item.cqItem.cqType == 'DATE' || item.cqItem.cqType == 'CHOICE_WITH_SCORE' || item.cqItem.cqType == 'NUMBER' || item.cqItem.cqType == 'PARAGRAPH' || item.cqItem.cqType == 'DOCUMENT_DOWNLOAD_LINK'}">
															<div class="col-md-12">
																<div class="row">
																	<span class="col-md-1 pull-left disablePaddings">&nbsp;</span> <span class="col-md-11 pull-left disablePaddings">
																		<div class="row">
																			<c:if test="${item.cqItem.cqType == 'CHOICE' || item.cqItem.cqType == 'LIST' || item.cqItem.cqType == 'CHOICE_WITH_SCORE' || item.cqItem.cqType == 'CHECKBOX'}">
																				<ul style="list-style: none;">
																					<c:forEach var="cqOptions" items="${item.cqItem.cqOptions}">
																						<c:set var="exist" value="false" />
																						<c:if test="${suppCq.key.supplierCqStatus ne 'DRAFT'}">
																							<c:forEach items="${item.listAnswers}" var="ans">
																								<c:if test="${ans.value == cqOptions.value}">
																									<c:set var="exist" value="true" />
																								</c:if>
																							</c:forEach>
																						</c:if>
																						
																						<c:if test="${ exist == 'true'}">
																							<li><i class="fa fa-check-square-o" aria-hidden="true"></i>&nbsp;<b>${cqOptions.value}</b></li>
																						</c:if>
																						<c:if test="${exist == 'false'}">
																							<li><i class="fa fa-square-o" aria-hidden="true"></i>&nbsp;${cqOptions.value}</li>
																						</c:if>
																					</c:forEach>
																				</ul>
																			</c:if>
																			<c:if test="${item.cqItem.cqType == 'TEXT' and suppCq.key.supplierCqStatus ne 'DRAFT'}">
																				<div class="col-md-5 col-sm-5 col-xs-6" style="text-decoration: underline;">
																					<b>Ans:</b> ${item.textAnswers}
																				</div>
																			</c:if>
																			<c:if test="${item.cqItem.cqType == 'DATE' and suppCq.key.supplierCqStatus ne 'DRAFT'}">
																				<div class="col-md-5 col-sm-5 col-xs-6">
																					<p>
																						<b>Date:</b>&nbsp;${ item.textAnswers}
																					</p>
																				</div>
																			</c:if>
																			<c:if test="${item.cqItem.cqType == 'NUMBER' and suppCq.key.supplierCqStatus ne 'DRAFT'}">
																				<div class="col-md-5 col-sm-5 col-xs-6">
																					<p>
																						<b>Number:</b>&nbsp;${ item.textAnswers}
																					</p>
																				</div>
																			</c:if>
																			<c:if test="${item.cqItem.cqType == 'PARAGRAPH' and suppCq.key.supplierCqStatus ne 'DRAFT'}">
																				<div class="col-md-5 col-sm-5 col-xs-6">
																					<pre>&nbsp;${ item.textAnswers}</pre>
																				</div>
																			</c:if>
																			 <c:if test="${item.cqItem.cqType == 'DOCUMENT_DOWNLOAD_LINK' and suppCq.key.supplierCqStatus ne 'DRAFT'}">
																				<div class="tab-main-inner pad_all_15">
																					<ul>
																						<c:forEach var="docs" items="${eventDocs}">
																							<li> <p>${docs.fileName} </p></li>
																						</c:forEach>
																					</ul>
																				</div>
																			</c:if> 
																		</div>
																	</span>
																</div>
															</div>
														</c:if>
														<div>
															<c:if test="${!empty item.fileName and suppCq.key.supplierCqStatus ne 'DRAFT'}">
																<form:form method="GET">
																	<c:url var="download" value="/supplier/downloadAttachment/${eventType}/${event.id}/${item.id}" />
																	<a href="${download}"><font color="">${item.fileName}</font></a>
																</form:form>
															</c:if>
														</div>
													</td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</div>
							</div>
						</div>
					</div>
				</c:forEach>
			</div>
		</div>
	</div>
</c:if>
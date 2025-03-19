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
				<a data-toggle="collapse" class="accordion" href="#collapseQuestionnaire"> <spring:message code="application.questionnaire" /> </a>
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
																<span class="col-md-1 pull-left disablePaddings">${item.cqItem.optional ? '*' : ''} 
																	<c:if test="${item.cqItem.order == 0}"><h5>${item.cqItem.level}.${item.cqItem.order}</h5> <br></c:if>
																	<c:if test="${item.cqItem.order > 0}"><b>${item.cqItem.level}.${item.cqItem.order}</b> <br></c:if>
																</span>
																<span class="col-md-11 pull-left disablePaddings">
																	<c:if test="${item.cqItem.order == 0}"><h5>${item.cqItem.itemName}</h5> <br></c:if>
																	<c:if test="${item.cqItem.order > 0}"><b>${item.cqItem.itemName}</b> <br></c:if>
																	<span class="item_detail s1_text_small">${item.cqItem.itemDescription}</span>
																</span>
															</div>
														</div>
														<c:if test="${item.cqItem.cqType == 'CHOICE' || item.cqItem.cqType == 'LIST' || item.cqItem.cqType == 'CHECKBOX' || item.cqItem.cqType == 'TEXT' || item.cqItem.cqType == 'CHOICE_WITH_SCORE'}">
															<div class="col-md-12">
																<div class="row">
																	<span class="col-md-1 pull-left disablePaddings">&nbsp;</span>
																	<span class="col-md-11 pull-left disablePaddings">
																		<div class="row">
																			<c:if test="${item.cqItem.cqType == 'CHOICE' || item.cqItem.cqType == 'LIST' || item.cqItem.cqType == 'CHOICE_WITH_SCORE' || item.cqItem.cqType == 'CHECKBOX'}">
																				<ul style="list-style: none;">
																					<c:forEach var="cqOptions" items="${item.cqItem.cqOptions}">
																						<c:set var="exist" value="false" />
																						<c:forEach items="${item.listAnswers}" var="ans">
																							<c:if test="${ans.value == cqOptions.value}">
																								<c:set var="exist" value="true" />
																							</c:if>
																						</c:forEach>
																						<c:if test="${ exist == 'true'}">
																							<li><i class="fa fa-check-square-o" aria-hidden="true"></i>&nbsp;<b>${cqOptions.value}</b></li>
																						</c:if>
																						<c:if test="${exist == 'false'}">
																							<li><i class="fa fa-square-o" aria-hidden="true"></i>&nbsp;${cqOptions.value}</li>
																						</c:if>
																					</c:forEach>
																				</ul>
																			</c:if>
																			<c:if test="${item.cqItem.cqType == 'TEXT'}">
																				<div class="col-md-5 col-sm-5 col-xs-6">
																					<b><spring:message code="rfs.reqsummary.ans.label" />:</b> <u>${ item.textAnswers}</u>
																				</div>
																			</c:if>
																		</div>
																	</span>
																</div>
															</div>
														</c:if>
														<div>
															<c:if test="${!empty item.fileName}">
																<form:form method="GET">
																	<c:url var="download" value="/buyer/downloadCqAttachment//${sourcingFormRequest.id}/${item.id}" />
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
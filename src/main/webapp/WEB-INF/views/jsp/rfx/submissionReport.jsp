
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="/WEB-INF/template-functions.tld" prefix="tf"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<sec:authentication property="principal.bqPageLength" var="bqPageLength" />
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<script type="text/javascript" src="<c:url value="/resources/js/view/submissionReport.js?3"/>"></script>
<spring:message var="rfxSubmissionReportDesk" code="application.rfx.evaluation.report" />
<script type="text/javascript">
zE(function() {
	zE.setHelpCenterSuggestions({ labels: [${rfxSubmissionReportDesk}] });
});
</script>
<input type="hidden" id="bqPageLength" value="${bqPageLength}">
<div id="page-wrapper">
	<div id="page-content-wrapper">
		<div id="page-content">
			<div class="container">
				<ol class="breadcrumb">
					<li><a href="${pageContext.request.contextPath}/buyer/buyerDashboard"> <spring:message code="application.dashboard" />
					</a></li>
					<li class="active">${eventType.value}</li>
					<li class="active"><spring:message code="application.submission.evaluation" /></li>
					<li><a href="${pageContext.request.contextPath}/buyer/${eventType}/envelopList/${event.id}"><spring:message code="label.rftenvelop" /></a></li>
				</ol>
				<input type="hidden" id="eventTypeSearch" value="${eventType}" name="eventType"> <input type="hidden" id="eventDecimal" value="${event.decimal}" name="eventDecimal"> <input type="hidden" id="eventStatus" value="${event.status}" name="eventStatus">
				<section class="create_list_sectoin">
					<div class="page_title_wrapper">
						<h3 class="sub_title">
							<i aria-hidden="true" class="glyph-icon"><img src="${pageContext.request.contextPath}/resources/assets/image-resources/image-procurehere/envelop-icon.png" alt="envelop-icon" /></i>${event.eventName}
							<spring:message code="rfpenvelope.submission.report" />
						</h3>
						<h2 class="trans-cap pull-right">
							<spring:message code="application.status" />
							: ${event.status}
						</h2>
						<div class="right-header-button"></div>
					</div>
					<div class="ports-tital-heading"></div>
					<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
					<div class="clear"></div>
					<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
					<div class="white-bg border-all-side float-left width-100 pad_all_15">
						<div class="row">
							<div class="col-md-12 col-sm-12 col-xs-12">
								<div class="col-md-9 marg-bottom-20">
									<h3>${envelop.envelopTitle}:${envelop.description}</h3>
									<input type="hidden" value="${envelop.id}" id="evenvelopId"> <input type="hidden" value="${event.id}" id="eventId">
									<%-- <h3>${envelop.envelopTitle}:${envelop.description}</h3> --%>
									<p>
										<spring:message code="application.eventid" />
										:&nbsp;${event.eventId}
									</p>
									<p>
										<spring:message code="eventsummary.submission.envelope.owner" />
										: ${envelop.leadEvaluater.name}
									</p>
									<p>
										<spring:message code="eventsummary.submission.envelope.sequence" />
										: ${envelop.envelopSequence != null ? envelop.envelopSequence :'-'}
									</p>

								</div>
								<c:if test="${fn:length(eventSuppliers) ne 0 }">
									<div class="col-md-3 pull-right">
										<div class="row">
											<div class="col-md-12 pull-right">
												<form:form action="${pageContext.request.contextPath}/buyer/downloadEnvelopeSubmissionZip/${eventType}/${event.id}/${envelop.id}" id="downloadEnvelope">
													<button class="btn float-right btn-success hvr-pop marg-left-10 downloadEnvelopeSubmissionZip" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="tooltip.download.entire.envelope" />'>
														<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i>
														</span> <span class="button-content"><spring:message code="button.bq.download.entire.envelope" /></span>
													</button>
												</form:form>
											</div>
										</div>
									</div>
								</c:if>
							</div>
						</div>
					</div>
					<c:if test="${fn:length(eventSuppliers) ne 0 }">
						<div class="form-group">
							<div class="row">
								<div class="col-md-12">
									<form:form action="${pageContext.request.contextPath}/buyer/submissionReport/${eventType}/${event.id}/${envelop.id}" id="evaluationSubmission">
										<div class="pull-left" style="min-width: 250px; max-width: calc(100% - 58px);">
											<select name="selectedSuppliers" id="selectedSuppliers" class="" multiple>
												<c:forEach items="${eventSuppliers}" var="supp">
													<c:if test="${fn:contains(selectedSuppliers, supp.id)}">
														<option value="${supp.id}" selected="selected">${supp.companyName}</option>
													</c:if>
													<c:if test="${!fn:contains(selectedSuppliers, supp.id)}">
														<option value="${supp.id}">${supp.companyName}</option>
													</c:if>
												</c:forEach>
											</select>
										</div>
										<%-- <div class="pull-left" style="margin-left: 10px">
											<button class="btn float-right btn-info hvr-pop hvr-rectangle-out" type="submit">
												<spring:message code="application.go" />
											</button>
										</div> --%>
									</form:form>
								</div>
							</div>
						</div>
					</c:if>
					<c:if test="${fn:length(eventSuppliers) eq 0 }">
						<div class="form-group">
							<div class="row">
								<div class="col-md-12">
									<div id="idGlobalWarn" class="alert alert-warning">
										<h3>
											<spring:message code="alert.envelopes.none.suppliers" />
										</h3>
									</div>
								</div>
							</div>
						</div>
					</c:if>
					<c:if test="${fn:length(eventSuppliers) ne 0 }">
						<c:if test="${not empty evaluation}">
							<div class="row clearfix">
								<div class="col-sm-12">
									<section class="sa_sb_report_content_warp s1_white_panel">
										<div class="sa_inner_pad">
											<div class="sa_sbtable_mid_ctrl">
												<form action="${pageContext.request.contextPath}/buyer/generateCqComparisonTable/${eventType}/${event.id}/${envelop.id}">
													<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
													<button class="btn float-right btn-info hvr-pop marg-left-10 ${showComparision ? 'disabled' : ''}" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="tooltip.download.comparison.excel" />'>
														<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i>
														</span> <span class="button-content"><spring:message code="button.bq.download.comparision" /></span>
													</button>
												</form>
											</div>
											<div class="sa_sb_table_section1">
												<!-- CQ Loop -->
												<c:forEach items="${evaluation}" var="cq">
													<label class="control-label width_300 marg-top-10"><spring:message code="questionnaire.label" />: ${cq.name}</label>
													<div class="main_table_wrapper ph_table_border table-responsive sa_enlp_table_block ">
														<table class="table ph_table table-1 " width="100%" border="0" cellspacing="0" cellpadding="0">
															<thead>
																<tr class="sub_item">
																	<th class="width_45  width_45_fix align-left">No</th>
																	<th class="width_300_fix align-left"><spring:message code="questionnaire.label" /></th>
																	<c:forEach items="${cq.columns}" var="column">
																		<th data-type="cqitems" data-id="${column.id}" class="width_250_fix   align-left ${column.disqualified ? 'disqualified' : ''} disableItemFromCqBq">
																			<div class="input-group doWrap">
																				${column.companyName}
																				<c:if test="${eventPermissions.leadEvaluator}">
																					<c:if test="${!column.disqualified}">
																						<span class="input-groupaddon " style="border: 0px;" data-toggle="tooltip" data-original-title="${event.status == 'CLOSED' ? 'Disqualify Supplier' : ''}" data-placement="left"> <c:if test="${event.status == 'CLOSED'}">
																								<a href="#" class="rejectSupplier"> <i class="glyph-icon icon-times " style="color: #58d68d; font-size: 16px;"></i>
																								</a>
																							</c:if>
																						</span>
																					</c:if>
																					<c:if test="${column.disqualified}">
																						<span class="input-groupaddon " style="border: 0px;" data-toggle="tooltip" data-original-title="${event.status == 'CLOSED' ? 'Requalify Supplier' : ''}" data-placement="left"> <c:if test="${event.status == 'CLOSED'}">
																								<a href="#" class="passSupplier"> <i class="glyph-icon icon-times " style="color: #eb6759; font-size: 16px;"></i>
																								</a>
																							</c:if>
																						</span>
																					</c:if>
																				</c:if>
																			</div>
																		</th>
																	</c:forEach>
																</tr>
															</thead>
														</table>
														<div class="table_data_wrapper">
															<!-- CQ Items -->
															<table class="ph_table table table-2 cq_table" width="100%" border="0" cellspacing="0" cellpadding="0">
																<tbody>
																	<c:forEach items="${cq.data}" var="item" varStatus="count">
																		<tr>
																			<c:forEach items="${item}" var="answers" varStatus="status">
																				<c:set var="itemSection" value="${fn:endsWith(answers,'.0') }" />
																				<c:if test="${status.index == 0}">
																					<td class="width_45  width_45_fix align-left">
																						<p>
																							<c:if test="${itemSection}">
																								<span class="section_name">
																							</c:if>
																							<c:if test="${!itemSection}">
																								<span class="item_name">
																							</c:if>
																							${answers} </span>
																							<c:set var="section" value="${fn:endsWith(answers,'.0') }" />
																						</p>
																					</td>
																				</c:if>
																				<c:if test="${status.index == 1}">
																					<%-- <c:set var="answer" value="${fn:split(answers, '-')}" /> --%>
																					<c:set var="item" value="${fn:endsWith(answers,'.0') }" />
																					<c:set var="answer1" value="${fn:substring(answers, 0, 32)}" />
																					<c:set var="answer2" value="${fn:substringAfter(answers, answer1)}" />
																					<c:set var="answer3" value="${fn:substring(answer2, 0, 1)}" />
																					<c:set var="answer" value="${answer3}-" />
																					<c:set var="answer4" value="${fn:substringAfter(answer2, answer)}" />
																					<c:choose>
																						<c:when test="${eventPermissions.leadEvaluator}">
																							<td class="width_300_fix align-left">
																								<p class="${event.status == 'CLOSED' ? 'cqComment' : ''} triangleParentBlock" data-row="${count.index+1}" data-placement="right" data-toggle="tooltip" data-original-title="${event.status == 'CLOSED' ? 'Click to Add comment' : ''}">
																									<c:if test="${section}">
																										<span class="section_name">
																									</c:if>
																									<c:if test="${!section}">
																										<span class="item_name">
																									</c:if>
																									${answer4} </span>

																									<c:if test="${answer3 == '1'}">
																										<span class="move-top glyphicon glyphicon-triangle-right triangleBlock"></span>
																									</c:if>
																								</p> <input type="hidden" id="itemId${count.index +1}" value="${answer1}">
																							</td>
																						</c:when>
																						<c:otherwise>
																							<td class="width_300_fix align-left">
																								<p>
																									<c:if test="${section}">
																										<span class="section_name">
																									</c:if>
																									<c:if test="${!section}">
																										<span class="item_name">
																									</c:if>
																									${fn:substringAfter(answers, "-")} </span>
																									<%-- 																									${fn:substringAfter(answers, "-")} --%>
																								</p>
																							</td>
																						</c:otherwise>
																					</c:choose>
																				</c:if>

																				<c:if test="${status.index > 2}">
																					<td class="width_250_fix  align-left abcd "><c:if test="${not empty answers}">
																							<c:set var="answer" value="${fn:split(answers, '-')}" />
																							<p class="char_check align-left" id="check_char-${answer[2]}">
																								<!-- Item Id : ${answer[0]}  supplierId = ${cq.supplierId}-->
																								<c:if test="${!( answer[3] eq 'null')}">
																									<c:if test="${!section}">
																										<a data-supplier="${answer[1]}" data-subtype="getSupplierComments" data-itemid="${answer[2]}" class="${event.status == 'CLOSED' ? 'openComment' : ''} triangleParentBlock" data-toggle="tooltip" data-original-title="${event.status == 'CLOSED' ? 'Click to Add comment' : ''}" href="javascript:void(0);"> <c:if
																												test="${answer[0]}">
																												<span class="move-top glyphicon glyphicon-triangle-right triangleBlock"></span>
																											</c:if> ${answer[3]}
																										</a>
																									</c:if>
																								</c:if>
																								<c:if test="${( answer[3] eq 'null')}">
																									<c:if test="${!section}">
																										<span onclick='return false;'><spring:message code="eventsummary.submission.no.answer" /></span>
																									</c:if>
																								</c:if>
																								<c:if test="${!section && empty answer[3]}">
																									<span class="align-center" onclick='return false;'><spring:message code="eventsummary.submission.no.answer" /></span>
																								</c:if>

																							</p>
																						</c:if></td>
																				</c:if>
																			</c:forEach>
																		</tr>
																	</c:forEach>
																</tbody>
															</table>
														</div>
														<c:if test="${not empty cq.scoring}">
															<div class="table_data_footer">
																<table class="ph_table table" width="100%" border="0" cellspacing="0" cellpadding="0">
																	<tbody>
																		<tr class="sa_bg_gray">
																			<td class="width_45  width_45_fix">&nbsp;</td>
																			<td class="width_300_fix align-left"><strong><spring:message code="rfaevent.total.score" /></strong></td>
																			<c:forEach items="${cq.scoring}" var="score" varStatus="status">
																				<td class="width_250_fix  align-center "><c:if test="${not empty score}">
																						<strong>${score}</strong>
																					</c:if></td>
																			</c:forEach>
																		</tr>
																	</tbody>
																</table>
															</div>
														</c:if>
													</div>
												</c:forEach>
											</div>
										</div>
									</section>
								</div>
							</div>
						</c:if>
						<div class="row clearfix">&nbsp;</div>
						<div class="row clearfix">
							<div class="col-sm-12">
								<c:if test="${not empty bqEvaluation}">
									<section class="sa_sb_report_content_warp s1_white_panel">
										<div class="sa_inner_pad">
											<!-- Middle form control block -->
											<div class="sa_sbtable_mid_ctrl">
												<div>
													<div class="col-md-2 col-sm-2 col-xs-2" style="width: auto; display: flex; align-items: center;">
														<label style="margin-right: 10px; pointer-events: none !important;">Records per page</label> <select class="disablesearch chosen-select" name="" data-pasley-id="0644" id="selectPageLen" style="pointer-events: none;">
															<option value="10">10</option>
															<option value="30">30</option>
															<option value="50">50</option>
															<option value="100">100</option>
															<option value="500">500</option>
															<option value="9999"><spring:message code="application.all2" /></option>
														</select>
													</div>
													<div class="col-md-2 col-sm-2 col-xs-2" style="width: auto; display: flex; align-items: center;">
														<label style="margin-right: 10px;">Jump to Item</label> <select path="" class="chosen-select" id="chooseSection" style="pointer-events: none;">
															<option value="">&nbsp;</option>
															<c:forEach items="${bqEvaluation}" var="EvalBq" varStatus="index">
																<c:if test="${index.index == 0}">
																	<c:forEach items="${EvalBq.levelOrderList}" var="levelOrder">
																		<option value="">${levelOrder}</option>
																	</c:forEach>
																</c:if>
															</c:forEach>
														</select>
													</div>
													<div class="pull-left">
														<div aria-label="Page navigation" style="margin-top: -18px;">
															<ul class="pagination" id="pagination"></ul>
														</div>
													</div>
												</div>
												<%-- <div class="sa_boc_block">
													<div class="form-group clearfix">
																											    <c:if test="${withTax != null && withTax}">
															<form action="${pageContext.request.contextPath}/buyer/submissionReport/${eventType}/${event.id}/${envelop.id}" id="withOrWithoutTaxForm" method="post">
																<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> Show:&nbsp;&nbsp;&nbsp; <select name="withOrWithoutTax" class="custom-select withOrWithoutTax">
																	<option>Select Type</option>
																	<option value="0" <c:if test="${withOrWithoutTax =='0'}">selected</c:if>>With tax</option>
																	<option value="1" <c:if test="${withOrWithoutTax =='1'}">selected</c:if>>Without tax</option>
																</select>
															</form>
														</c:if> 
													</div>
												</div>--%>
												<div class="sa_dwl_btn">
													<form action="${pageContext.request.contextPath}/buyer/generateBqComparisonTable/${eventType}/${event.id}/${envelop.id}">
														<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
														<button class="btn float-right btn-info hvr-pop bqComparisonTable marg-left-10 ${showComparision ? 'disabled' : ''}" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="tooltip.download.bq.comparison" />'>
															<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i>
															</span> <span class="button-content"><spring:message code="button.bq.download.comparision" /></span>
														</button>
													</form>
												</div>

												<div style="float: right;">
													<div class="pull-right">
														<button class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" id="resetButton">
															<spring:message code="application.reset" />
														</button>
													</div>
													<div class="pull-right col-md-8">
														<input style="min-width: 260px; margin-right: 35px;" name="bqItemSearch" type="text" id="bqItemSearch" placeholder='<spring:message code="bq.search.itemname.placeholder" />' class="form-control" />
													</div>
												</div>
											</div>
											<!-- Middle form control block -->
											<!-- Table two block start  -->
											<c:forEach items="${bqEvaluation}" var="bq" varStatus="bqNumber">
												<div class="clear"></div>
												<label class="control-label width_300 marg-bottom-10 marg-top-10"><spring:message code="eventdescription.billofquantity.label" />: ${bq.name}</label>
												<div class="table-responsive sa_enlp_table_block s1_white_panel">
													<div class="sa_cutom_tbl_warpWidth">
														<table cellpadding="0" cellspacing="0" border="0" class="table bqitemsDataTable" style="margin-bottom: 0px;">
															<thead>
																<tr class="sub_item">
																	<th class="width_45  width_45_fix align-left" style="vertical-align: middle; padding-right: 0;">No</th>
																	<th class="width_400_fix align-left" style="vertical-align: middle; padding-right: 0;">Item Name</th>
																	<th class="width_100  width_100_fix align-center" style="vertical-align: middle; padding-right: 0;"><spring:message code="label.uom" /></th>
																	<th class="width_100  width_100_fix text-right" style="vertical-align: middle; padding-right: 0;"><spring:message code="label.rftbq.th.quantity" /></th>
																	<c:forEach items="${bq.columns}" var="column">
																		<th data-type="bqitems" data-id="${column.id}" style="padding-right: 0; vertical-align: middle;" class="width_250_fix text-right ${column.disqualified ? 'disqualified' : ''} disableItemFromCqBq">
																			<div class="input-group">
																				${column.companyName}
																				<c:if test="${eventPermissions.leadEvaluator}">
																					<c:if test="${!column.disqualified}">
																						<span class=" " style="border: 0px;" data-toggle="tooltip" data-original-title="${event.status == 'CLOSED' ? 'Disqualify Supplier' : ''}" data-placement="left"> <c:if test="${event.status == 'CLOSED'}">
																								<a href="#" class="rejectSupplier"> <i class="glyph-icon icon-times " style="color: #58d68d; font-size: 16px;"></i>
																								</a>
																							</c:if>
																						</span>
																					</c:if>
																					<c:if test="${column.disqualified}">
																						<span class=" " style="border: 0px;" data-toggle="tooltip" data-original-title="${event.status == 'CLOSED' ? 'Requalify Supplier' : ''}" data-placement="left"> <c:if test="${event.status == 'CLOSED'}">
																								<a href="#" class="passSupplier"> <i class="glyph-icon icon-times " style="color: #eb6759; font-size: 16px;"></i>
																								</a>
																							</c:if>
																						</span>
																					</c:if>
																				</c:if>
																			</div>
																		</th>
																	</c:forEach>
																</tr>
															</thead>
															<tbody id="bqItemList${bqNumber.index}">
																<c:forEach items="${bq.data}" var="item" varStatus="status">
																	<c:set var="levelOrder" value="${item[0]}" />
																	<c:set var="itemLevelOrder" value="${fn:replace(levelOrder, '.', '_')}" />
																	<c:set var="itemOrder" value="${fn:split(levelOrder,'.')[1]}" />
																	<tr class="item_${itemLevelOrder}">
																		<c:forEach items="${item}" var="answers" varStatus="status">
																			<c:if test="${status.index == 0}">
																				<td class="width_45  width_45_fix align-left">
																					<p>
																						<c:if test="${itemOrder == 0 }">
																							<span class="section_name">
																						</c:if>
																						<c:if test="${itemOrder > 0 }">
																							<span class="item_name">
																						</c:if>
																						${answers} </span>
																					</p>
																				</td>
																			</c:if>
																			<c:if test="${status.index == 1}">
																				<td class="width_400_fix align-left">
																					<p class="margin-right-15">
																						<c:if test="${itemOrder == 0 }">
																							<span class="section_name">
																						</c:if>
																						<c:if test="${itemOrder > 0 }">
																							<span class="item_name">
																						</c:if>
																						${answers} </span>
																					</p> <!-- </td> -->
																			</c:if>
																			<c:if test="${status.index == 2}">
																				<!-- <td class="width_100  width_100_fix text-center"> -->
																				<c:choose>
																					<c:when test="${answers eq 'Buyer fix Price'}">
																						<span class="bs-label label-success" style="color: #fff"><spring:message code="eventsummary.bq.buyer.fixedprice" /></span>
																					</c:when>

																					<c:when test="${answers eq 'Trade In Price'}">
																						<span class="bs-label label-info" style="color: #fff"><spring:message code="eventsummary.bq.trade.price" /></span>
																					</c:when>

																				</c:choose>
																				<!-- </td> -->
																			</c:if>
																			<c:if test="${status.index == 3}">
																				<td class="width_100  width_100_fix text-center">
																					<p>${answers}</p>
																				</td>
																			</c:if>
																			<c:if test="${status.index == 4}">

																				<td class="width_100  width_100_fix text-right"><c:if test="${not empty answers}">
																						<p>${fn:trim(answers)}</p>
																					</c:if></td>
																			</c:if>


																			<c:if test="${status.index > 4}">
																				<td class="width_250_fix text-right"><c:if test="${not empty answers && !section }">
																						<c:set var="answer" value="${fn:split(answers, '-')}" />
																						<p class="char_check" id="check_char-${answer[2]}">
																							<c:if test="${itemOrder ne '0'}">
																								<a data-supplier="${answer[1]}" data-subtype="getSupplierCommentsForBq" data-itemid="${answer[2]}" class="${event.status == 'CLOSED' ? 'openComment' : ''} triangleParentBlock" data-toggle="tooltip" data-original-title="${event.status == 'CLOSED' ? 'Click to Add comment' : ''}" href="javascript:void(0);"> <c:if
																										test="${answer[0]}">
																										<span class="move-top glyphicon glyphicon-triangle-right triangleBlock"></span>
																									</c:if> <strong> <fmt:formatNumber type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}" value="${answer[3]}" />
																								</strong>
																								</a>
																							</c:if>
																						</p>
																					</c:if></td>
																			</c:if>
																		</c:forEach>
																	</tr>
																</c:forEach>
															</tbody>
															<tbody>
																<tr class="sa_table_mid_heading sa_bg_gray">
																	<td class="width_250_fix text-right"></td>
																	<td class="width_250_fix text-right"></td>
																	<td class="width_250_fix text-right"></td>
																	<td class="text-right"><spring:message code="submission.report.subtotal" /></td>
																	<c:forEach items="${bq.totalAmountList}" var="total" varStatus="status">
																		<td class="width_250_fix text-right">
																			<p>
																				<%-- 																					<a data-supplier="${total.supplierId}" data-subtype="getSupplierTotalCommentsForBq" data-bqid="${total.bqId}"  class="totalBqComents triangleParentBlock" data-toggle="tooltip" data-original-title="Click to Add comment" href="javascript:void(0);">
																						<c:if test="${total.commentExisit}">
																							<span class="move-top glyphicon glyphicon-triangle-right triangleBlock"></span>
																						</c:if>
																					</a>
 --%>
																				<strong> <fmt:formatNumber type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}" value="${total.totalAmount}" />
																				</strong>
																			</p>
																		</td>
																	</c:forEach>
																</tr>
																<c:if test="${(eventType == 'RFA' && withTax != null && withTax) or eventType != 'RFA'}">
																	<c:if test="${ !erpEnable}">
																		<tr class="sa_table_mid_heading sa_bg_gray">
																			<td class="width_250_fix text-right"></td>
																			<td class="width_250_fix text-right"></td>
																			<td class="width_250_fix text-right"></td>
																			<td class="width_250_fix text-right"><spring:message code="submission.report.additional.tax" /></td>
																			<c:forEach items="${bq.addtionalTaxInfo}" var="tax" varStatus="status">
																				<td class="width_250_fix text-right"><strong>${tax} </strong></td>
																			</c:forEach>
																		</tr>
																	</c:if>
																	<tr class="sa_table_mid_heading sa_bg_gray">
																		<td class="width_250_fix text-right"></td>
																		<td class="width_250_fix text-right"></td>
																		<td class="width_250_fix text-right"></td>
																		<td class="width_250_fix text-right"><spring:message code="submission.report.grandtotal" /> (${event.baseCurrency.currencyCode})</td>
																		<c:forEach items="${bq.totalAmountList}" var="total" varStatus="status">
																			<td class="width_250_fix text-right">
																				<p>
																					<a data-supplier="${total.supplierId}" data-subtype="getSupplierTotalCommentsForBq" data-bqid="${total.bqId}" class="${event.status == 'CLOSED' ? 'totalBqComents' : ''} triangleParentBlock" data-toggle="tooltip" data-original-title="${event.status == 'CLOSED' ? 'Click to Add comment' : ''}" href="javascript:void(0);"> <c:if
																							test="${total.commentExisit}">
																							<span class="move-top glyphicon glyphicon-triangle-right triangleBlock"></span>
																						</c:if> <strong> <fmt:formatNumber type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}" value="${total.subtotal}" />
																					</strong>
																					</a>
																				</p>
																			</td>
																		</c:forEach>
																	</tr>
																</c:if>
																<c:if test="${fn:length(bq.supplierRemarks) ne 0}">
																	<tr class="sa_table_mid_heading sa_bg_gray">
																		<td class="width_250_fix text-right"></td>
																		<td class="width_250_fix text-right"></td>
																		<td class="width_250_fix text-right"></td>
																		<td class="width_250_fix text-right"></td>
																		<c:forEach items="${bq.supplierRemarks}" var="remark" varStatus="status">
																			<td class="width_250_fix text-right remark-box">
																				<p>
																					<c:if test="${!empty remark}">
																						<a data-placement="top" class="tooltiptext-top tooltip-remarks" data-toggle="tooltip" data-original-title='${remark}' href="javascript:void(0);"><spring:message code="pr.remark" /><span class="move-top glyphicon glyphicon-triangle-right triangleBlock"></span> </a>
																					</c:if>
																				</p>
																			</td>
																		</c:forEach>
																	</tr>
																</c:if>
															</tbody>
														</table>
													</div>
												</div>
											</c:forEach>
											<!-- Table two block ends  -->
											<div class="clear"></div>
										</div>
									</section>
								</c:if>
							</div>
						</div>
						<div class="row clearfix">
							<div class="col-sm-12">
								<c:if test="${not empty sorEvaluation}">
									<section class="sa_sb_report_content_warp s1_white_panel">
										<div class="sa_inner_pad">
											<!-- Middle form control block -->
											<div class="sa_sbtable_mid_ctrl">
												<div>
													<div class="col-md-2 col-sm-2 col-xs-2" style="width: auto; display: flex; align-items: center;">
														<label style="margin-right: 10px; pointer-events: none !important;">Records per page</label> <select class="disablesearch chosen-select" name="" data-pasley-id="0644" id="selectPageLenSor" style="pointer-events: none;">
														<option value="10">10</option>
														<option value="30">30</option>
														<option value="50">50</option>
														<option value="100">100</option>
														<option value="500">500</option>
														<option value="9999"><spring:message code="application.all2" /></option>
													</select>
													</div>
													<div class="col-md-2 col-sm-2 col-xs-2" style="width: auto; display: flex; align-items: center;">
														<label style="margin-right: 10px;">Jump to Item</label> <select path="" class="chosen-select" id="chooseSectionSor" style="pointer-events: none;">
														<option value="">&nbsp;</option>
														<c:forEach items="${sorEvaluation}" var="EvalBq" varStatus="index">
															<c:if test="${index.index == 0}">
																<c:forEach items="${EvalBq.levelOrderList}" var="levelOrder">
																	<option value="">${levelOrder}</option>
																</c:forEach>
															</c:if>
														</c:forEach>
													</select>
													</div>
													<div class="pull-left">
														<div aria-label="Page navigation" style="margin-top: -18px;">
															<ul class="pagination" id="paginationSor"></ul>
														</div>
													</div>
												</div>
												<div class="sa_dwl_btn">
													<form action="${pageContext.request.contextPath}/buyer/generateSorComparisonTable/${eventType}/${event.id}/${envelop.id}">
														<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
														<button class="btn float-right btn-info hvr-pop bqComparisonTable marg-left-10 ${showComparision ? 'disabled' : ''}" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="tooltip.download.sor.comparison" />'>
															<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i>
															</span> <span class="button-content"><spring:message code="button.bq.download.comparision" /></span>
														</button>
													</form>
												</div>

												<div style="float: right;">
													<div class="pull-right">
														<button class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" id="resetButtonSor">
															<spring:message code="application.reset" />
														</button>
													</div>
													<div class="pull-right col-md-8">
														<input style="min-width: 260px; margin-right: 35px;" name="sorItemSearch" type="text" id="sorItemSearch" placeholder='<spring:message code="sor.search.itemname.placeholder" />' class="form-control" />
													</div>
												</div>
											</div>
											<!-- Middle form control block -->
											<!-- Table two block start  -->
											<c:forEach items="${sorEvaluation}" var="bq" varStatus="bqNumber">
												<div class="clear"></div>
												<label class="control-label width_300 marg-bottom-10 marg-top-10"><spring:message code="eventdescription.schedule.rate.label" />: ${bq.name}</label>
												<div class="table-responsive sa_enlp_table_block s1_white_panel">
													<div class="sa_cutom_tbl_warpWidth">
														<table cellpadding="0" cellspacing="0" border="0" class="table soritemsDataTable" style="margin-bottom: 0px;">
															<thead>
															<tr class="sub_item">
																<th class="width_45  width_45_fix align-left" style="vertical-align: middle; padding-right: 0;">No</th>
																<th class="width_400_fix align-left" style="vertical-align: middle; padding-right: 0;">Item Name</th>
																<th class="width_100  width_100_fix align-center" style="vertical-align: middle; padding-right: 0;"><spring:message code="label.uom" /></th>
																<th class="width_100  width_100_fix text-right" style="vertical-align: middle; padding-right: 0;">
																<c:forEach items="${bq.columns}" var="column">
																	<th data-type="bqitems" data-id="${column.id}" style="padding-right: 0; vertical-align: middle;" class="width_250_fix text-right ${column.disqualified ? 'disqualified' : ''} disableItemFromCqBq">
																		<div class="input-group">
																				${column.companyName}
																			<c:if test="${eventPermissions.leadEvaluator}">
																				<c:if test="${!column.disqualified}">
																						<span class=" " style="border: 0px;" data-toggle="tooltip" data-original-title="${event.status == 'CLOSED' ? 'Disqualify Supplier' : ''}" data-placement="left"> <c:if test="${event.status == 'CLOSED'}">
																								<a href="#" class="rejectSupplier"> <i class="glyph-icon icon-times " style="color: #58d68d; font-size: 16px;"></i>
																								</a>
																						</c:if>
																						</span>
																				</c:if>
																				<c:if test="${column.disqualified}">
																						<span class=" " style="border: 0px;" data-toggle="tooltip" data-original-title="${event.status == 'CLOSED' ? 'Requalify Supplier' : ''}" data-placement="left"> <c:if test="${event.status == 'CLOSED'}">
																								<a href="#" class="passSupplier"> <i class="glyph-icon icon-times " style="color: #eb6759; font-size: 16px;"></i>
																								</a>
																						</c:if>
																						</span>
																				</c:if>
																			</c:if>
																		</div>
																	</th>
																</c:forEach>
															</tr>
															</thead>
															<tbody id="sorItemList${bqNumber.index}">
															<c:forEach items="${bq.data}" var="item" varStatus="status">
																<c:set var="levelOrder" value="${item[0]}" />
																<c:set var="itemLevelOrder" value="${fn:replace(levelOrder, '.', '_')}" />
																<c:set var="itemOrder" value="${fn:split(levelOrder,'.')[1]}" />
																<tr class="soritem_${itemLevelOrder}">
																	<c:forEach items="${item}" var="answers" varStatus="status">
																		<c:if test="${status.index == 0}">
																			<td class="width_45  width_45_fix align-left">
																				<p>
																					<c:if test="${itemOrder == 0 }">
																					<span class="section_name">
																						</c:if>
																						<c:if test="${itemOrder > 0 }">
																							<span class="item_name">
																						</c:if>
																						${answers} </span>
																				</p>
																			</td>
																		</c:if>
																		<c:if test="${status.index == 1}">
																			<td class="width_400_fix align-left">
																			<p class="margin-right-15">
																				<c:if test="${itemOrder == 0 }">
																				<span class="section_name">
																						</c:if>
																						<c:if test="${itemOrder > 0 }">
																							<span class="item_name">
																						</c:if>
																						${answers} </span>
																			</p> <!-- </td> -->
																		</c:if>
																		<c:if test="${status.index == 2}">
																			<td class="width_100  width_100_fix text-center">
																				<p>${answers}</p>
																			</td>
																		</c:if>
																		<c:if test="${status.index == 3}">

																			<td class="width_100  width_100_fix text-right"><c:if test="${not empty answers}">
<%--																				<p>${fn:trim(answers)}</p>--%>
																			</c:if></td>
																		</c:if>


																		<c:if test="${status.index >= 4}">
																			<td class="width_250_fix text-right"><c:if test="${not empty answers && !section }">
																				<c:set var="answer" value="${fn:split(answers, '-')}" />
																				<p class="char_check" id="check_char-${answer[2]}">
																					<c:if test="${itemOrder ne '0'}">
																						<a data-supplier="${answer[1]}" data-subtype="getSupplierCommentsForSor" data-itemid="${answer[2]}" class="${event.status == 'CLOSED' ? 'openComment' : ''} triangleParentBlock" data-toggle="tooltip" data-original-title="${event.status == 'CLOSED' ? 'Click to Add comment' : ''}" href="javascript:void(0);"> <c:if
																								test="${answer[0]}">
																							<span class="move-top glyphicon glyphicon-triangle-right triangleBlock"></span>
																						</c:if> <strong> <fmt:formatNumber type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}" value="${answer[3]}" />
																						</strong>
																						</a>
																					</c:if>
																				</p>
																			</c:if></td>
																		</c:if>
																	</c:forEach>
																</tr>
															</c:forEach>
															</tbody>
															<tbody>
																<c:if test="${fn:length(bq.supplierRemarks) ne 0}">
																	<tr class="sa_table_mid_heading sa_bg_gray">
																		<td class="width_250_fix text-right"></td>
																		<td class="width_250_fix text-right"></td>
																		<td class="width_250_fix text-right"></td>
																		<td class="width_250_fix text-right"></td>
																		<c:forEach items="${bq.supplierRemarks}" var="remark" varStatus="status">
																			<td class="width_250_fix text-right remark-box">
																				<p>
																					<c:if test="${!empty remark}">
																						<a data-placement="top" class="tooltiptext-top tooltip-remarks" data-toggle="tooltip" data-original-title='${remark}' href="javascript:void(0);"><spring:message code="pr.remark" /><span class="move-top glyphicon glyphicon-triangle-right triangleBlock"></span> </a>
																					</c:if>
																				</p>
																			</td>
																		</c:forEach>
																	</tr>
																</c:if>
															</tbody>
														</table>
													</div>
												</div>
											</c:forEach>
											<!-- Table two block ends  -->
											<div class="clear"></div>
										</div>
									</section>
								</c:if>
							</div>
						</div>
					</c:if>
					<div class="clear"></div>
					<div class="float-left width100  row">
						<div class="col-md-6">
							<form method="post" id="idFrmEvaluationComments" enctype="multipart/form-data" action="${pageContext.request.contextPath}/buyer/saveSumamryRemark/${eventType}/${event.id}/${envelop.id}?${_csrf.parameterName}=${_csrf.token}">
								<input type="hidden" id="eventType" name="eventType" value="${eventType}" /> <input type="hidden" id="eventId" name="eventId" value="${event.id}" /> <input type="hidden" id="envelopId" name="envelopId" value="${envelop.id}" /> <input type="hidden" id="envelopTitle" name="envelopId" value="${envelop.envelopTitle}" /> <input type="hidden"
									name="${_csrf.parameterName}" value="${_csrf.token}" />
								<c:if test="${not empty evaluationSummaryList[0].evaluatorSummary}">
									<label class="control-label width_300 marg-bottom-10 marg-top-10"> <spring:message code="rfaevent.evaluator.summary" /></label>
								</c:if>
								<c:if test="${leadEvaluator}">
									<div class="row">
										<div class="col-sm-12 form-group">
											<div class="ph_table_border answersBlock">
												<div class="reminderList marginDisable">
													<c:forEach items="${evaluationSummaryList}" var="summaryItem" varStatus="status">
														<c:if test="${not empty summaryItem.evaluatorSummary}">
															<div class="row remark">
																<p class="col-md-12">
																	<span class="width100 pull-left align-left item_name"><b>${summaryItem.user.name}</b></span> <span class="width100 pull-left align-left">${summaryItem.evaluatorSummary}</span> <span class="width100 pull-right align-right">${summaryItem.summaryDate}</span>
																	<c:if test="${not empty summaryItem.fileName}">
																		<a href="${pageContext.request.contextPath}/buyer/downloadEvaluationDocument/${eventType}/${summaryItem.id}" class="pull-left"><b>${summaryItem.fileName}</b> </a>
																		<span class="pull-right">File Size : ${summaryItem.fileSizeInKb} KB</span>
																	</c:if>
																</p>
															</div>
															</c:if>
													</c:forEach>
												</div>
											</div>
										</div>
									</div>
								</c:if>

								<c:if test="${leadEvaluator and not empty envelop.leadEvaluatorSummary}">
									<label class="control-label width_300 marg-bottom-10 marg-top-10"> <spring:message code="submission.report.lead.evaluator.summary" /></label>
									<div class="row">
										<div class="col-sm-12 form-group">
											<div class="ph_table_border answersBlock">
												<div class="reminderList marginDisable">
													<div class="row remark">
														<p class="col-md-12">
															<span class="width100 pull-left align-left"><b>${envelop.leadEvaluater.name} </b> <c:if test="${event.status == 'CLOSED' and envelop.showFinish}">
																	<a href="" class=" deleteSummary  pull-right"> <i class="fa fa-times-circle"></i>
																	</a>
																</c:if></span><span class="width100 pull-left align-left">${envelop.leadEvaluatorSummary}</span> 
																<span class="width100 pull-right align-right">${envelop.evaluatorSummaryDateStr}</span> 
														    <a href="${pageContext.request.contextPath}/buyer/downloadLeadEvaluationDocument/${eventType}/${envelop.id}" class="pull-left">
																<b>${envelop.fileName}</b> 
															</a>
														<c:if test="${not empty envelop.fileName}"> 
															<span class="pull-right">File Size : ${envelop.fileSizeInKb} KB</span>
														 </c:if>
														</p>
														<p class="col-md-12"></p>
													</div>
												</div>
											</div>
										</div>
									</div>
								</c:if>
								<c:if test="${!leadEvaluator and not empty evaluationSummaryList[0].evaluatorSummary}">
									<div class="row">
										<div class="col-sm-12 form-group">
											<div class="ph_table_border answersBlock">
												<div class="reminderList marginDisable">
													<div class="row remark">
														<p class="col-md-12">
															<span class="width100 pull-left align-left"><b>${evaluationSummaryList[0].user.name}</b> 
																<c:if test="${event.status == 'CLOSED' and envelop.showFinish}">
																	<a href="" class=" deleteSummary  pull-right"> <i class="fa fa-times-circle"></i>
																	</a>
																</c:if>
															</span>
															<span class="width100 pull-left align-left">${evaluationSummaryList[0].evaluatorSummary}</span> 
															<span class="width100 pull-right align-right">${evaluationSummaryList[0].summaryDate}</span> 
																<a href="${pageContext.request.contextPath}/buyer/downloadEvaluationDocument/${eventType}/${evaluationSummaryList[0].id}" class="pull-left">
																	<b>${evaluationSummaryList[0].fileName} </b>
																</a>
																<c:if test="${not empty evaluationSummaryList[0].fileName}">
																	  <span class="pull-right">File Size : ${evaluationSummaryList[0].fileSizeInKb} KB</span>
														   		</c:if>
														</p>
														<p class="col-md-12"></p>
													</div>
												</div>
											</div>
										</div>
									</div>
								</c:if>

								<c:if test="${event.status == 'CLOSED' and envelop.showFinish}">
										<c:if test="${!leadEvaluator and empty evaluationSummaryList[0].evaluatorSummary || leadEvaluator and empty envelop.leadEvaluatorSummary }">
										<c:set var="fileType" value="" />
										<c:forEach var="type" items="${ownerSettings.fileTypes}" varStatus="index">
											<c:set var="fileType" value="${fileType}${index.first ? '': ', '}${type}" />
										</c:forEach>
										<div class="row">
											<div class="col-md-12 ">
												<label class="control-label width_300 marg-bottom-10 marg-top-10"> <spring:message code="evaluation.comments" /></label>
												<div class="form-group">
													<textarea placeholder='<spring:message code="write.summary.placeholder" />' name="evaluatorSummary" rows="4" data-validation="required" data-validation-length="5000" id="evaluatorSummary" class="form-control"></textarea>
												</div>
											</div>
										</div>
										<div class="row">
											<div class="col-md-12 ">
												<label class="control-label width_300 marg-bottom-10 marg-top-10"> <spring:message code="evaluation.attachment" /></label>
												<div data-provides="fileinput" class="fileinput fileinput-new input-group">
													<spring:message code="event.doc.file.required" var="required" />
													<spring:message code="event.doc.file.length" var="filelength" />
													<spring:message code="event.doc.file.mimetypes" var="mimetypes" />
													<div data-trigger="fileinput" class="form-control">
														<span class="fileinput-filename show_name"></span>
													</div>
													<span class="input-group-addon btn btn-black btn-file"> <span class="fileinput-new"> <spring:message code="event.document.selectfile" />
													</span> <span class="fileinput-exists"> <spring:message code="event.document.change" />
													</span> <input type="file" name="file" id="evaluationUploadDocument" data-validation-allowing="${fileType}" data-validation="extension size" data-validation-error-msg-container="#Load_File-error-dialog" data-validation-max-size="${ownerSettings.fileSizeLimit} MB" data-validation-error-msg-required="${required}"
														data-validation-error-msg-size="You cannot upload file larger than 50 MB" data-validation-error-msg-mime="${mimetypes}">
												</div>
												<div id="Load_File-error-dialog" style="width: 100%; float: left; margin: 0 0 0 0;"></div>
												<span> <spring:message code="application.note" />:<br />
													<ul>
														<li><spring:message code="createrfi.documents.max.size" /> ${ownerSettings.fileSizeLimit} MB</li>
														<li><spring:message code="createrfi.documents.allowed.file.extension" />: ${fileType}.</li>
													</ul>
												</span>
											</div>
										</div>
										<div class="row">
											<div class="col-md-12">
												<div class="align-center">
													<button type="submit" class="btn btn-info ph_btn_small btn-tooltip hvr-pop hvr-rectangle-out disableSubmitComment" style="float: left;">
														<spring:message code="application.save" />
													</button>
												</div>
											</div>
										</div>
										</c:if>
								</c:if>
							</form>
						</div>
					</div>
					<div class="clear"></div>
					<div class="float-left width100  row"></div>
					<div class="clear"></div>
					<div class="row">
						<div class="col-md-12  marg-top-20">
							<div class="table_f_action_btn">
								<c:if test="${(fn:length(eventSuppliers) ne 0) or (fn:length(eventSuppliers) eq 0 and event.status == 'CLOSED')}">
									<c:if test="${envelop.showFinish}">
										<form class="bordered-row" id="submitNextForm" method="post" action="${pageContext.request.contextPath}/buyer/finishEvaluation/${eventType}/${event.id}/${envelop.id}">
											<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
											<button type="button" class="btn btn-info btn-lg float-right hvr-pop hvr-rectangle-out" id="nextStep">
												<spring:message code="application.finish" />
											</button>
										</form>
									</c:if>
								</c:if>
								<a href="${pageContext.request.contextPath}/buyer/${eventType}/envelopList/${event.id}" class="btn btn-lg btn-black float-right hvr-pop hvr-rectangle-out1 backButton" title=""><spring:message code="application.back" /></a>
							</div>
						</div>
					</div>
				</section>
			</div>
		</div>
	</div>
</div>
<div class="flagvisibility dialogBox" id="commentAnswer" title="${envelop.evaluationStatus =='COMPLETE'?'View Comments':'Add Comments'}">
	<div class="float-left width100 pad_all_15 white-bg">
		<form method="post" id="cancelCommentAnswer">
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /><input type="hidden" name="itemId" value=""> <input type="hidden" name="supplierId" value=""> <input type="hidden" name="subType" value=""> <input type="hidden" name="delsubType" value="">
			<div class="row">
				<div class="col-sm-12 form-group">
					<div class="ph_table_border answersBlock">
						<div class="reminderList marginDisable"></div>
					</div>
				</div>
				<div class="col-sm-12">
					<c:if test="${event.status == 'CLOSED' and envelop.showFinish}">
						<div class="form-group">
							<textarea placeholder='<spring:message code="write.comment.placeholder" />' rows="4" name="" id="suppComment" data-validation="required length" data-validation-length="max500" class="form-control"></textarea>
						</div>
					</c:if>
				</div>
			</div>
			<div class="row">
				<div class="col-md-12">
					<div class="align-center">
						<c:if test="${event.status == 'CLOSED' and envelop.showFinish}">
							<button type="button" id="saveCommentAnswer" class="btn btn-info ph_btn_small btn-tooltip hvr-pop hvr-rectangle-out OkCommentAnswer">
								<spring:message code="rfaevent.comments.button" />
							</button>
						</c:if>
						<button type="button" class="closeDialog btn btn-black marg-left-10 hvr-pop ph_btn_small hvr-rectangle-out1">
							<spring:message code="application.cancel" />
						</button>
					</div>
				</div>
			</div>
		</form>
	</div>
</div>
<div class="flagvisibility dialogBox" id="commentCqAnswer" title="${envelop.evaluationStatus =='COMPLETE'?'View Comments':'Add Comments'}">
	<div class="float-left width100 pad_all_15 white-bg">
		<form method="post" id="addCommentForm">
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> <input type="hidden" value="${eventType}" id="eventType" name="eventType" /> <input type="hidden" value="" id="item_id" name="itemId" />
			<div class="row">
				<div class="col-sm-12 form-group"></div>
				<div class="col-sm-12">
					<c:if test="${event.status == 'CLOSED' and envelop.showFinish}">
						<div class="form-group">
							<textarea placeholder='<spring:message code="write.comment.placeholder" />' rows="4" name="" id="commentAdded" data-validation="required length" data-validation-length="max500" class="form-control"></textarea>
						</div>
					</c:if>
				</div>
			</div>
			<div class="row">
				<div class="col-md-12">
					<div class="align-center">
						<c:if test="${event.status == 'CLOSED' and envelop.showFinish}">
							<button type="button" id="addComment" class="btn btn-info ph_btn_small btn-tooltip hvr-pop hvr-rectangle-out OkCommentAnswer">
								<spring:message code="rfaevent.comments.button" />
							</button>
						</c:if>
						<button type="button" class="closeDialog btn btn-black marg-left-10 hvr-pop ph_btn_small hvr-rectangle-out1" id="cancel">
							<spring:message code="application.cancel" />
						</button>
					</div>
				</div>
			</div>
		</form>
	</div>
</div>
<div class="flagvisibility dialogBox" id="commentBqAnswer" title="${envelop.evaluationStatus =='COMPLETE'?'View Comments':'Add Comments'}">
	<div class="float-left width100 pad_all_15 white-bg">
		<form method="post" id="addBqCommentForm">
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> <input type="hidden" value="${eventType}" id="eventType" name="eventType" /> <input type="hidden" value="" id="bqId" name="bqId" /> <input type="hidden" value="" id="supplierId" name="supplierId" /> <input type="hidden" id="eventId" name="eventId"
				value="${event.id}" />
			<div class="row">
				<div class="col-sm-12 form-group">
					<div class="ph_table_border answersBlock">
						<div class="reminderList marginDisable"></div>
					</div>
				</div>
				<div class="col-sm-12">
					<c:if test="${event.status == 'CLOSED' and envelop.showFinish}">
						<div class="form-group">
							<textarea placeholder='<spring:message code="write.comment.placeholder" />' rows="4" name="" id="bqCommentAdded" data-validation="required length" data-validation-length="max500" class="form-control"></textarea>
						</div>
					</c:if>
				</div>
			</div>
			<div class="row">
				<div class="col-md-12">
					<div class="align-center">
						<c:if test="${event.status == 'CLOSED' and envelop.showFinish}">
							<button type="button" id="bqAddComment" class="btn btn-info ph_btn_small btn-tooltip hvr-pop hvr-rectangle-out OkBqCommentAnswer">
								<spring:message code="rfaevent.comments.button" />
							</button>
						</c:if>
						<button type="button" class="closeDialog btn btn-black marg-left-10 hvr-pop ph_btn_small hvr-rectangle-out1" id="cancel">
							<spring:message code="application.cancel" />
						</button>
					</div>
				</div>
			</div>
		</form>
	</div>
</div>
<div class="flagvisibility dialogBox" id="disableCqBqItemPopup" title="Disqualify Supplier">
	<div class="float-left width100 pad_all_15 white-bg">
		<form id="disqualifyId" method="post">
			<%-- <form id="disableCqBqItemForm" method="post" action="${pageContext.request.contextPath}/buyer/disqualifySupplier/${eventType}"> --%>
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> <input type="hidden" name="supplier" value="" /> <input type="hidden" name="eventId" value="${event.id}" /> <input type="hidden" name="envelopId" value="${envelop.id}" /> <input type="hidden" name="qualify" value="false" />
			<div class="">
				<div class="col-md-12 form-group">
					<label><spring:message code="Product.remarks" /></label>
					<textarea class="form-control" name="remarks" data-validation="required"></textarea>
				</div>
			</div>
			<div class="row">
				<div class="col-md-12">
					<div class="form-group align-center">
						<c:if test="${event.status == 'CLOSED' and envelop.showFinish }">
							<button type="button" class="btn btn-info ph_btn_small btn-tooltip hvr-pop hvr-rectangle-out" id="submitDisqualify">
								<spring:message code="eventsummary.submission.disqualify" />
							</button>
						</c:if>
						<button type="button" class="closeDialog btn btn-black marg-left-10 hvr-pop ph_btn_small hvr-rectangle-out1">
							<spring:message code="application.cancel" />
						</button>
					</div>
				</div>
			</div>
		</form>
	</div>
</div>
<div class="flagvisibility dialogBox" id="enableCqBqItemPopup" title="Requalify Supplier">
	<div class="float-left width100 pad_all_15 white-bg">
		<form id="requalifyId" method="post">
			<%-- <form id="disableCqBqItemForm" method="post" action="${pageContext.request.contextPath}/buyer/disqualifySupplier/${eventType}"> --%>
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> <input type="hidden" name="supplier" value="" /> <input type="hidden" name="eventId" value="${event.id}" /> <input type="hidden" name="envelopId" value="${envelop.id}" /> <input type="hidden" name="qualify" value="true" />
			<div class="">
				<div class="col-md-12 form-group">
					<label><spring:message code="Product.remarks" /></label>
					<textarea class="form-control" name="remarks" data-validation="required"></textarea>
				</div>
			</div>
			<div class="row">
				<div class="col-md-12">
					<div class="form-group align-center">
						<c:if test="${event.status == 'CLOSED' and envelop.showFinish}">
							<button type="button" class="btn btn-info ph_btn_small btn-tooltip hvr-pop hvr-rectangle-out" id="submitRequalify">
								<spring:message code="rfaevent.requalify.button" />
							</button>
						</c:if>
						<button type="button" class="closeDialog btn btn-black marg-left-10 hvr-pop ph_btn_small hvr-rectangle-out1">
							<spring:message code="application.cancel" />
						</button>
					</div>
				</div>
			</div>
		</form>
	</div>
</div>
<!--pop up  -->
<div class="modal fade" id="confirmDeleteSummary" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="application.confirm.delete" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<div class="modal-body">
				<label> <spring:message code="summary.confirm.delete" />
				</label> <input type="hidden" id="deleteIdContact" />
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<form id="deletesummary" method="get" action="${pageContext.request.contextPath}/buyer/removeSumamryRemark/${eventType}/${event.id}/${envelop.id}">
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> <input type="hidden" name="eventType" value="${eventType}" /> <input type="hidden" name="eventId" value="${event.id}" /> <input type="hidden" name="envelopId" value="${envelop.id}" />
					<button type="submit" class=" btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" id="confDelContact">
						<spring:message code="application.delete" />
					</button>
				</form>
				<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
					<spring:message code="application.cancel" />
				</button>
			</div>
		</div>
	</div>
</div>
<div class="modal fade" id="confirmEvaluationPopup1" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="confirm.evaluation.label" />
					<button class="close for-absulate" type="button" data-dismiss="modal">X</button>
				</h3>
			</div>
			<div class="modal-body">
				<label><spring:message code="confirm.evaluation.prompt" /></label>
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<form class="bordered-row" id="submitNextForm" method="post" action="${pageContext.request.contextPath}/buyer/finishEvaluation/${eventType}/${event.id}/${envelop.id}">
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
					<button id="idCancel" class="btn btn-black ph_btn_small hvr-pop hvr-rectangle-out1" data-dismiss="modal">
						<spring:message code="application.no2" />
					</button>
					<input type="submit" class="btn btn-info btn-default ph_btn_small hvr-pop hvr-rectangle-out1 disableButtonOnClick" href="javascript:void(0);" value="Yes" />
				</form>
			</div>
		</div>
	</div>
</div>
<!-- DeleteEvaluationDocument -->
<div class="modal fade" id="confirmDeleteDocument" role="dialog">
	<div class="modal-dialog for-delete-all reminder documentBlock">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="application.confirm.delete" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">x</button>
			</div>
			<div class="modal-body">
				<label><spring:message code="evaluation.confirm.delete.document" /> </label> <input type="hidden" id="deleteIdDocument" />
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<form id="deletesummary" method="get" action="${pageContext.request.contextPath}/buyer/removeEvaluationDocument/${eventType}/${event.id}/${envelop.id}">
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> <input type="hidden" name="eventType" value="${eventType}" /> <input type="hidden" name="eventId" value="${event.id}" /> <input type="hidden" name="envelopId" value="${envelop.id}" />
					<button type="submit" class=" btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" id="confDelContact">
						<spring:message code="application.delete" />
					</button>
				</form>
				<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
					<spring:message code="application.cancel" />
				</button>
			</div>
		</div>
	</div>
</div>
</div>

<!-- <div class="flagvisibility dialogBox" id="showSupplierRemark" title="Supplier Remark">
	<div class="float-left width100 pad_all_15 white-bg">
		<div class="row">
			<div class="col-sm-12 form-group">
				<div class="ph_table_border remarkBlock">
					<div class="reminderList marginDisable" id="remarkData"></div>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col-md-12">
				<div class="align-center">
					<button type="button" class="closeDialog btn btn-black marg-left-10 hvr-pop ph_btn_small hvr-rectangle-out1">Close</button>
				</div>
			</div>
		</div>
	</div>
</div>
 -->
<style>
.scrolableTable_UserList>div.dataTables_wrapper {
	overflow: auto;
}

.move-top {
	top: -10px;
}

.triangleParentBlock {
	position: relative;
}

.triangleBlock {
	float: none;
	position: absolute;
	right: -13px;
}

.glyphicon-triangle-right {
	-ms-transform: rotate(-35deg); /* IE 9 */
	-webkit-transform: rotate(-35deg); /* Chrome, Safari, Opera */
	transform: rotaterotate(-35deg);
}

td.disabled {
	background: #ccc !important;
}

.width_45 {
	width: 45px !important;
}

.width_45_fix {
	width: 45px !important
}

.float-right {
	margin-bottom: 10px;
}

.tag-line {
	width: auto;
}

.ph_table_border.answersBlock {
	max-height: 300px;
	overflow: auto;
}

.ph_table td a {
	color: #4689cb;
	font-size: 13px;
}

.ph_table td a span {
	color: #4689cb;
}

.noborder {
	border: medium none !important;
}

/* .char_check {
	padding-left: 15px;
} */
.disableItemFromCqBq {
	cursor: pointer;
}

.d-flex {
	display: flex;
}

.margin-right-15 {
	margin-right: 15px;
}

.trans-cap {
	text-transform: capitalize;
	color: #e4e4e4;
	font-size: 18px;
	float: left;
	min-height: 36px;
	line-height: 36px;
}

/* .remarkBlock {
	overflow-y: scroll;
	overflow-x: hidden;
	max-height: 150px;
	color: #424242;
} */
.tooltip-remarks .tooltip.top .tooltip-inner {
	background-color: red;
}

.tooltip-remarks .tooltip.top .tooltip-arrow {
	border-top-color: red;
}

.tooltip-inner .arrow::before {
	border-bottom-color: #f00; /* Red */
}

.tooltip-remarks+.tooltip>.tooltip-inner {
	background-color: #f7f7f7;
	border-top-color: #f7f7f7;
	box-shadow: 0 10px 20px rgba(0, 0, 0, 0.19), 0 6px 6px
		rgba(0, 0, 0, 0.23);
	color: #000;
	max-width: 800px !important;
	max-height: 680px !important;
	height: auto !important;
	text-align: left !important;
}

.tooltip-remarks+.tooltip>.tooltip-arrow {
	border-bottom-color: #f7f7f7;
	border-top-color: #f7f7f7;
	box-shadow: 0 10px 20px rgba(0, 0, 0, 0.19), 0 6px 6px
		rgba(0, 0, 0, 0.23);
	color: #000;
}

#selectPageLen_chosen {
	width: 85px !important;
}

#selectPageLenSor_chosen {
	width: 85px !important;
}

#chooseSection_chosen {
	width: 85px !important;
}

#chooseSectionSor_chosen {
	width: 85px !important;
}

td .abcd {
	border-right: 1px solid !important;
}

th .doWrap {
	white-space: normal !important;
}

.disableCq {
	pointer-events: none !important;
}

</style>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.twbsPagination.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/numeral.min.js"/>"></script>
<script>
	$.validate({
		lang : 'en',
		validateOnBlur : false,
		modules : 'file'
	});
	$(document).ready(function(){
	    $(".tooltip-remarks").tooltip();
	});

	var selectedSuppliers = '';
	<c:forEach items="${eventSuppliers}" var="supp">
		<c:if test="${fn:contains(selectedSuppliers, supp.id)}">
		selectedSuppliers +='${supp.id},';
		</c:if>
	</c:forEach>
	
	if(selectedSuppliers.length > 0) {
		selectedSuppliers = selectedSuppliers.substring(0, selectedSuppliers.length - 1);
	}
	
//	$(function (e) {
//		e.preventDefault();
$('document').ready(function() {

	
	$('.disableSubmitComment').click(function() {
		if($('#idFrmEvaluationComments').isValid()) {
			$('#loading').show();
			$('.disableSubmitComment').addClass('disableCq');
		}
	});
	
	$('.disableButtonOnClick').click(function(){
		$('.disableButtonOnClick').addClass('disableCq');
	});
	  
	
	$('#nextStep').click(function(){
	    $('#confirmEvaluationPopup1').modal('show');
	});
	  
	$('#suppComment').on('keyup', function() {
		$('#suppComment').validate(function(valid, elem) {});
	});
	  
	$('#commentAdded').on('keyup', function() {
		$('#commentAdded').validate(function(valid, elem) {});
	});

	$('#bqCommentAdded').on('keyup', function() {
		$('#bqCommentAdded').validate(function(valid, elem) {});
	});
	
	$('#deleteEvaluationDocument').click(function(){
		 $('#confirmDeleteDocument').modal('show');
	});
	
	$('#confDelContact').click(function(){
		$('#loading').show();
	});
	
	var totalPage = 500;
	var visiblePage = 5;
	var bqPageLength = $('#bqPageLength').val();
	if( bqPageLength === undefined || bqPageLength === ''){
		bqPageLength = 50;
	}
	var totalBqItemCount =  Math.ceil($('.bqitemsDataTable tr').length/$('.bqitemsDataTable thead').length);
	//alert(totalBqItemCount);
	  totalPage = Math.ceil(totalBqItemCount/bqPageLength);
		
	  if(totalPage == 0 ||  totalPage === undefined || totalPage === ''){
			totalPage = 1;
		}
	
		if(totalPage < 5){
		visiblePage = totalPage;
		}
		 if (isNaN(totalPage))
		 {
		  
			 totalPage = 1;
		 }

	
    var obj = $('#pagination').twbsPagination({
        totalPages: totalPage,
        visiblePages: visiblePage	
    });

		 // For SOR
	var totalPageSor = 500;
	var visiblePageSor = 5;
	var sorPageLength = $('#bqPageLength').val();
	if( sorPageLength === undefined || sorPageLength === ''){
		sorPageLength = 50;
	}
	var totalSorItemCount = $('.soritemsDataTable tr').length;
	var sorHeaderCount = $('.soritemsDataTable thead').length;

	if (totalSorItemCount > 0 && sorHeaderCount > 0) {
		totalSorItemCount = Math.ceil(totalSorItemCount / sorHeaderCount);
		totalPageSor = Math.ceil(totalSorItemCount / sorPageLength);
	} else {
		totalPageSor = 1;
	}

	if(totalPageSor == 0 || isNaN(totalPageSor) || totalPageSor === undefined || totalPageSor === ''){
		totalPageSor = 1;
	}

	if(totalPageSor < 5){
		visiblePageSor = totalPageSor;
	}
	if (isNaN(totalPageSor))
	{
		totalPageSor = 1;
	}


	var obj2 = $('#paginationSor').twbsPagination({
		totalPages: isNaN(totalPageSor) ? 1 : totalPageSor,
		visiblePages: visiblePageSor
	});
    
   // $('.page-link').click(function(e) {
    $(document).delegate('.page-link', 'click', function(e) {
	   e.preventDefault();
	   
            var searchVal = $('#bqItemSearch').val();
		    var searchVal = $('#bqItemSearch').val();
    		var choosenVal = ''; //$('#chooseSection option:selected').text();
    		item_level_order = '';
    		var selectPageNo = $('#pagination').find('li.active').text();
    		var pageNo = parseInt(selectPageNo);
    		var pageLen = "50";
            if ($('#selectPageLen option:selected').text()) {
            	pageLen = $('#selectPageLen').val();
    		}
    		var pageLength = parseInt(pageLen);
    		var isPageEnable = false;
    		if(searchVal === "" || choosenVal === ""){
    		isPageEnable = true;
    		}

    		$("#chooseSection").val('').trigger("chosen:updated");
    		evaluationSearchFilterBqItem(choosenVal , searchVal ,pageNo ,pageLength, isPageEnable);
    });


	$(document).delegate('.page-link', 'click', function(e) {
		e.preventDefault();

		var searchVal = $('#sorItemSearch').val();
		var searchVal = $('#sorItemSearch').val();
		var choosenVal = ''; //$('#chooseSection option:selected').text();
		item_level_order = '';
		var selectPageNo = $('#paginationSor').find('li.active').text();
		var pageNo = parseInt(selectPageNo);
		var pageLen = "50";
		if ($('#selectPageLenSor option:selected').text()) {
			pageLen = $('#selectPageLenSor').val();
		}
		var pageLength = parseInt(pageLen);
		var isPageEnable = false;
		if(searchVal === "" || choosenVal === ""){
			isPageEnable = true;
		}

		$("#chooseSectionSor").val('').trigger("chosen:updated");
		evaluationSearchFilterSorItem(choosenVal , searchVal ,pageNo ,pageLength, isPageEnable);
	});

});

</script>
<script>
	
	<c:if test="${buyerReadOnlyAdmin or eventPermissions.approverUser}">
	$(window).bind('load', function() {
		var allowedFields = '#dashboard,.backButton,.downloadEnvelopeSubmissionZip,.bqComparisonTable';
		//var disableAnchers = ['#reloadMsg'];        
		disableFormFields(allowedFields);
	});
	</c:if>
	$(document).ready(function() {
		$('#selectedSuppliers').select2({
			placeholder : "View Suppliers"
		});
		
		$('#selectedSuppliers').change(function() {
			$('#loading').show();
			$('#evaluationSubmission').submit();
		});
		
		$(".char_check").each(function() {
			//console.log($(this).text().length);
			if ($(this).text().length > 300) {
				$(this).parent().removeClass("align-center").addClass("for-left");
				$(this).css("line-height", "2em");
			}
		});

		$('.withOrWithoutTax').change(function() {
			$('#withOrWithoutTaxForm').submit();
		});
		
		$('#submitDisqualify').on('click', function(e) {
			e.preventDefault();
			if($("#disqualifyId").isValid()) {
				$(this).addClass('disabled');
				$('#disqualifyId').attr('action', getContextPath() + '/buyer/disqualifySupplier/${eventType}');
				$("#disqualifyId").submit();
			} else {
				return;
			}

		});
		
		$('#submitRequalify').on('click', function(e) {
			e.preventDefault();
			if($("#requalifyId").isValid()) {
				$(this).addClass('disabled');
				$('#requalifyId').attr('action', getContextPath() + '/buyer/disqualifySupplier/${eventType}');
				$("#requalifyId").submit();
			} else {
				return;
			}

		});
		
		$.maxlength.setDefaults({showFeedback: true});
		
		$('#evaluatorSummary').maxlength({max: 5000});
	});
</script>
<script type="text/javascript">
    /* Textarea autoresize */

    $(function() { "use strict";
        $('#evaluatorSummary').autosize();
});
</script>

<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/select.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/jquery.maxlength.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/select.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.plugin.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.maxlength.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/textarea.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/file-input/file-input.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/progressbar/progressbar.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/progressbar/progressbar.css"/>">



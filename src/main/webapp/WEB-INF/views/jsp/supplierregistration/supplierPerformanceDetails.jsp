<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
 
			<div id="supplierPerformanceId" class="col-md-12 pad0" style="display: none;">
				<div class="panel">
					<div class="panel-heading">
						<h4 class="panel-title">
							<a data-toggle="collapse" data-parent="#accordion" href="#collapsePerform"> ${supplier.companyName}
								<p style="float: right; margin-right: 30px;">
									<spring:message code="buyercreation.status" />
									${supplier.status}
								</p>
							</a>
						</h4>
					</div>
					<div id="collapsePerform" class="panel-collapse">
							<div class="col-md-12 marg-top-10" style="margin-bottom: 1%;">
									<label><spring:message code="application.filter.By.Date" /></label>
									<div class="row">
										<div class="col-md-5 col-sm-8 col-lg-5" style="padding-right:0px;">
											<input onfocus="this.blur()" name="dateTimeRange" data-date-start-date="0d" id="datepickerAnalytics" class="form-control for-clander-view" type="text" data-validation-format="dd/mm/yyyy hh:ii A - dd/mm/yyyy hh:ii P" />
										</div>
										
										<div class="col-md-2 col-sm-4" style="padding-left:0px;">
											<spring:message code="application.reset" var="resetLabel" />
											<button type="button" id="resetDate" class="btn btn-sm btn-black " style="height:37px" data-toggle="tooltip" data-placement="top" data-original-title="${resetLabel}">
												<i aria-hidden="true" class="glyph-icon icon-close"></i> <i class="glyph-icon icon-cross"></i>
											</button>
										</div>
									</div>

									<div class="import-supplier-inner-first-new global-list form-middle marg-top-20 ">
										<div class="ph_tabel_wrapper">
											<div class="main_table_wrapper ph_table_border" style="margin: 0;">
												<table class="ph_table crtrt_tbl border-none display table table-bordered " width="100%" border="0" cellspacing="0" cellpadding="0" style="top: 0px;">
													<thead>
														<tr>
															<th colspan="4" class="hdn-clr align-left"><h4 class="hdn-clr">Average Score by Buyer</h4></th>
														</tr>
														<tr>
															<th class="width_100 width_100 align-left">Buyer</th>
															<th class="width_100 width_100 align-left">Overall Score (%)</th>
															<th class="width_100 width_100 align-left">Rating</th>
															<th class="width_200_fix width_200 align-left">Rating Description</th>
														</tr>
													</thead>
													<tbody id="scoringByBuyer">
														<tr>
															<td class="width_100 width_100 align-left">${overallScoreByBuyer.buyerName}</td>
															<td class="width_100 width_100 align-left">${overallScoreByBuyer.overallScore}</td>
															<td class="width_100 width_100 align-left">${overallScoreByBuyer.scoreRating}</td>
															<td class="width_200_fix width_200 align-left">${overallScoreByBuyer.ratingDescription}</td>
														</tr>
													</tbody>
												</table>
											</div>
										</div>
									</div>
									
									<div class="import-supplier-inner-first-new global-list form-middle marg-top-20 ">
										<div class="ph_tabel_wrapper">
											<div class="main_table_wrapper ph_table_border" style="margin: 0;">
												<table class="ph_table crtrt_tbl border-none display table table-bordered " width="100%" border="0" cellspacing="0" cellpadding="0" style="top: 0px;">
													<thead>
														<tr>
															<th colspan="4" class="hdn-clr align-left"><h4>Average Score by Business Unit</h4></th>
														</tr>
														<tr>
															<th class="width_100 width_100 align-left">Business Unit</th>
															<th class="width_100 width_100 align-left">Overall Score (%)</th>
															<th class="width_100 width_100 align-left">Rating</th>
															<th class="width_200_fix width_200 align-left">Rating Description</th>
														</tr>
													</thead>
													<tbody id="scoringByBUnit">
														<c:forEach items="${sumOfOverallScoreByBU}" var="score">
															<tr>
																<td class="width_100 width_100 align-left">${score.unitName}</td>
																<td class="width_100 width_100 align-left">${score.overallScore}</td>
																<td class="width_100 width_100 align-left">${score.scoreRating}</td>
																<td class="width_200_fix width_200 align-left">${score.ratingDescription}</td>
															</tr>
														</c:forEach>
														<c:if test="${empty sumOfOverallScoreByBU}">
															<tr>
																<td class="align-center" colspan="4">No data available in table</td>
															</tr>
														</c:if>
													</tbody>
												</table>
											</div>
										</div>
									</div>
									<div class="import-supplier-inner-first-new global-list form-middle marg-top-20 ">
										<div class="ph_tabel_wrapper ">
											<div style="margin: 0;">
												<table class="ph_table crtrt_tbl border-none display table table-bordered" id="idOverallScoreBySpForm" width="100%" border="0" cellspacing="0" cellpadding="0" style="top: 0px;margin-bottom: 0;">
													<thead>
														<tr>
															<th colspan="7" class="hdn-clr align-left"><h4>Overall Score by Supplier Performance Form</h4></th>
														</tr>
														<tr>
															<th class="hdn-clr align-left form-inline" colspan="7">
																<label class="display_inline">Filter By Business Unit<label>
																<select class="form-control chosen-select" id="idBusinessUnit">
																	<option value="">Please Select Business Unit</option>
																	<c:forEach items="${businessUnitList}" var="bu">
																		<option value="${bu.id}">${bu.displayName}</option>
																	</c:forEach>
																</select>
															</th>
														</tr>
														<tr>
															<th class="width_100 width_100 align-left">Form ID</th>
															<th class="width_100 width_100 align-left">Event ID</th>
															<th class="width_100 width_100 align-left">Procurement Category</th>
															<th class="width_100 width_100 align-left">Business Unit</th>
															<th class="width_200_fix width_200 align-left">Overall Score (%)</th>
															<th class="width_200_fix width_200 align-left">Rating</th>
															<th class="width_200_fix width_200 align-left">Rating Description</th>
														</tr>
													</thead>
													<tbody id="scoringBySpForm" >
													</tbody>
												</table>
											</div>
										</div>
									</div>
									
									<div class="import-supplier-inner-first-new global-list form-middle marg-top-20 ">
										<div class="ph_tabel_wrapper">
											<div style="margin: 0;">
												<table class="ph_table crtrt_tbl border-none display table table-bordered" width="100%" border="0" cellspacing="0" cellpadding="0" style="top: 0px;margin-bottom: 0;">
													<thead>
														<tr>
															<th colspan="3" class="hdn-clr align-left"><h4>Performance Criteria Score by Supplier Performance Form</h4></th>
														</tr>
														<tr>
															<th class="hdn-clr align-left form-inline" colspan="3">
																<label class="display_inline">Filter By Form ID<label>
																<select class="form-control chosen-select" id="idFormId">
																	<option value="">Please Select Form ID</option>
																	<c:forEach items="${formIdList}" var="sp">
																		<option value="${sp.id}">${sp.formId}</option>
																	</c:forEach>
																</select>
															</th>
														</tr>
														<tr>
															<th class=" align-left">Performance Criteria</th>
															<th class=" align-left">Criteria Weightage (%)</th>
															<th class=" align-left">Average Score with Weightage</th>
														</tr>
													</thead>
													<tbody id="scoringBySpCriteria" >
															<tr>
																<td class="align-center" colspan="3">No data available in table</td>
 															</tr>
 													</tbody>
													<tfoot id="scoringBySpCriteriaFoot">
 													</tfoot>
											</table>
											</div>
										</div>
									</div>
									<div class="import-supplier-inner-first-new global-list form-middle marg-top-20 ">
										<div class="ph_tabel_wrapper">
											<div class="main_table_wrapper ph_table_border" style="margin: 0;">
												<table class="ph_table crtrt_tbl border-none display table table-bordered " width="100%" border="0" cellspacing="0" cellpadding="0" style="top: 0px;">
													<thead>
														<tr>
															<th colspan="3" class="hdn-clr align-left" ><h4>Event Participation</h4></th>
														</tr>
														<tr>
															<th class="width_100 width_100 align-left">Description</th>
															<th class="width_200_fix width_200 align-left">All Buyer Events</th>
															<th class="width_100 width_100 align-left">My Events</th>
														</tr>
													</thead>
													<tbody>
														<tr>
<!-- 															<td class="width_100 width_100 align-left">1</td> -->
															<td class="width_200_fix width_200 align-left"><spring:message code="supplier.total.event.invited" /></td>
															<td class="width_100 width_100 align-left">${totalEventInvited}</td>
															<sec:authorize access="hasRole('BUYER')">
		 														<td class="width_100 width_100 align-left">${totalMyEventInvited}</td>
															</sec:authorize>
														</tr>
														<tr>
<!-- 															<td class="width_100 width_100 align-left">2</td> -->
															<td class="width_200_fix width_200 align-left"><spring:message code="supplier.total.event.participated" /></td>
															<td class="width_100 width_100 align-left">${totalEventParticipated}</td>
															<sec:authorize access="hasRole('BUYER')">
		 														<td class="width_100 width_100 align-left">${totalMyEventParticipated}</td>
															</sec:authorize>
														</tr>
														<tr>
															<!--Todo : now we are not calculate the awarded event so this will be calculated-->
<!-- 															<td class="width_100 width_100 align-left">3</td> -->
															<td class="width_200_fix width_200 align-left"><spring:message code="supplier.total.event.awarded" /></td>
															<td class="width_100 width_100 align-left">${totalEventAwarded}</td>
															<sec:authorize access="hasRole('BUYER')">
		 														<td class="width_100 width_100 align-left">${totalMyEventAwarded}</td>
															</sec:authorize>
														</tr>
													</tbody>
												</table>
											</div>
										</div>
									</div>
									
							</div>
					</div>
				</div>
			</div>
			

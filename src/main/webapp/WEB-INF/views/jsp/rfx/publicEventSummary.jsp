<%@ page import="com.privasia.procurehere.core.enums.RfxTypes"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div id="page-content-wrapper">
	<div id="page-content">
		<div class="container">
			<div class="Section-title title_border gray-bg">
				<h2 class="trans-cap progress-head">${eventType.value}</h2>
				<h2 class="trans-cap pull-right">Status : ${event.status}</h2>
			</div>

			<div class="col-md-12 pad0">
				<div class="panel bgnone">
					<div class="panel-body">
						<div class="example-box-wrapper">
							<div class="panel-group" id="accordion">
								<div class="panel sum-accord">
									<div class="panel-heading">
										<h4 class="panel-title">
											<a data-toggle="collapse" class="accordion" href="#collapseTwo">Event Detail </a>
										</h4>
									</div>
									<div id="collapseTwo" class="panel-collapse collapse in">
										<div class="panel-body pad_all_15  border-bottom">
											<div class="main-panal-box-main">
												<div class="main-panal-box">
													<label>Event Reference Number : </label>
													<p>${event.referanceNumber!=null?event.referanceNumber:'-'}</p>
												</div>
												<div class="main-panal-box">
													<label>Event Complete Name : </label>
													<p>${event.eventName!=null?event.eventName:'-'}</p>
												</div>
												<div class="main-panal-box">
													<label>Company Name : </label>
													<p>${event.companyName}</p>
												</div>
												<div class="main-panal-box">
													<label>Event Start Date & Time : </label>
													<p>
														<fmt:formatDate value="${event.eventStart}" pattern="dd/MM/yyyy hh:mm a" timeZone="${timeZone}" />
													</p>
												</div>
												<div class="main-panal-box">
													<label>Event End Date & Time : </label>
													<p>
														<fmt:formatDate value="${event.eventEnd}" pattern="dd/MM/yyyy hh:mm a" timeZone="${timeZone}" />
													</p>
												</div>
												<div class="main-panal-box">
													<label>Event Publish Date & Time : </label>
													<p>
														<fmt:formatDate value="${event.eventPublishDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="${timeZone}" />
													</p>
												</div>
												<div class="main-panal-box">
													<label>Event Visibility : </label>
													<p>${event.eventVisibility!=null?event.eventVisibility:'-'}</p>
												</div>
												<div class="main-panal-box">
													<label>Delivery Date : </label>
													<!-- <p></p>-->
													<c:if test="${!empty event.eventDeliveryDate}">
														<p>
															<fmt:formatDate value="${event.eventDeliveryDate}" pattern="dd/MM/yyyy" timeZone="${timeZone}" />
														</p>
													</c:if>
													<c:if test="${empty event.eventDeliveryDate}">
														<p>N/A</p>
													</c:if>
												</div>
												<div class="main-panal-box">
													<label>Site Visit : </label>
													<p>${siteVisit?'Yes':'No'}</p>
												</div>
												<div class="main-panal-box">
													<label>Participation Fee : </label>
													<c:choose>
														<c:when test="${event.participationFeeCurrency == null}">
															<p>MYR <fmt:formatNumber type="number" value="${event.participationFees == null ? 0 : event.participationFees}" pattern="#,##0.00"/></p>
														</c:when>
														<c:otherwise>
															<p>${event.participationFeeCurrency} <fmt:formatNumber type="number" value="${event.participationFees == null ? 0 : event.participationFees}" pattern="#,##0.00"/></p>
														</c:otherwise>
													</c:choose>
												</div>
												<div class="main-panal-box ">
													<div class="row">
														<div class="col-md-6">
															<label>Event Category : </label>
														</div>

														<div class="col-md-6">
															<ul style="margin-left: -40px;">
																<c:forEach items="${industryCategories}" var="industryCategories">
																	<li>${industryCategories.name}</li>
																</c:forEach>
															</ul>
														</div>
													</div>
												</div>
												<c:if test="${eventType =='RFA'}">
													<div class="main-panal-box">
														<label>Event Method: </label>
														<p>${event.auctionType.value}</p>
													</div>
												</c:if>

												<div class="col-md-12 border-bottom"></div>
												<div class="col-md-12 border-bottom"></div>
												<%-- <div class="main-panal-box-main comm-info">
													<br />
													<h2>Contact Details</h2>
													<div class="table-responsive width100">
														<table class="contactPersons display table table-bordered topBorderTable">
															<thead>
																<tr>
																	<th class="align-left width_150 width_150_fix"><spring:message code="application.name" /></th>
																	<th class="align-left width_150 width_150_fix"><spring:message code="application.contact" /></th>
																	<th class="align-left width_150 width_150_fix"><spring:message code="application.mobile" /></th>
																	<th class="align-left width_150 width_150_fix"><spring:message code="application.emailaddress" /></th>
																</tr>
															</thead>
															<tbody>
																<c:if test="${empty eventContacts}">
																	<td valign="top" colspan="6" class="dataTables_empty">No data available in table</td>
																</c:if>
																<c:if test="${not empty eventContacts}">
																	<c:forEach items="${eventContacts}" var="contact">
																		<tr>
																			<td class="align-left width_150 width_150_fix">${contact.contactName != null ?contact.contactName:'-'}</td>
																			<td class="align-left width_150 width_150_fix">${contact.contactNumber != null ?contact.contactNumber:'-'}</td>
																			<td>${contact.mobileNumber != null ? contact.mobileNumber : "-"}</td>
																			<td class="align-left width_150 width_150_fix">${contact.comunicationEmail != null ?contact.comunicationEmail:'-'}</td>
																		</tr>
																	</c:forEach>
																</c:if>
															</tbody>
														</table>
													</div>
													<div class="col-md-12 align-left">
														<div class="row"></div>
													</div>
												</div> --%>
											</div>
											<div class="main-panal-box-main comm-info">
												<h2>Commercial Information</h2>
												<div class="main-panal-box">
													<label>Base Currency : </label>
													<c:if test="${not empty event.currencyCode}">
														<p>${event.currencyCode}-${event.baseCurrency}</p>
													</c:if>
													<c:if test="empty event.currencyCode">
														<p>-</p>
													</c:if>
												</div>
												<div class="main-panal-box">
													<label>Payment Terms : </label>
													<p>${event.paymentTerm!=null?event.paymentTerm:'N/A'}</p>
												</div>
											</div>
											<c:if test="${eventType eq 'RFA' and (event.auctionType eq 'FORWARD_ENGISH' or event.auctionType eq 'REVERSE_ENGISH')}">
												<div class="col-md-12 border-bottom"></div>
												<div class="main-panal-box-main comm-info">
													<h2>Time Extension</h2>
													<div class="main-panal-box">
														<label>Time Extension Type : </label>
														<p>${event.timeExtensionType}</p>
													</div>
												</div>
											</c:if>
										</div>
									</div>
								</div>
								<%-- <c:if test="${eventType ne 'RFI'}">
									<jsp:include page="publicEventSummaryBq.jsp" />
								</c:if> --%>
							</div>
							<div>
								<div class="col-md-12 dd sky mar_b_15 d-flex-center">
									<input type="hidden" id="buyerId" name="buyerId" value="${tenantId}" /> <a href="${pageContext.request.contextPath}/supplier/viewSupplierEvent/${eventType}/${event.id}" onclick="$(this).closest('form').submit();" class="idKnowMore btn btn-info btn-bold-large hvr-pop hvr-rectangle-out marg-right-but">Self-Invite</a>
									<%-- <form:form action="${pageContext.request.contextPath}/downloadPublicEventSummary/${eventType}/${event.id}">
										<button class="idKnowMore btn btn-info btn-bold-large hvr-pop hvr-rectangle-out mr-10 marg-left-10" id="idSumDownload" data-toggle="tooltip" data-placement="top" data-original-title="Download Event Summary">
											<i class="fa fa-download"></i> <span class="button-content pad_all_15 pad-lr">Download</span>
										</button>
									</form:form> --%>
									<a href="${pageContext.request.contextPath}/publicEvents/${buyerContextPath}" class="marg-left-but btn btn-backToList btn-bold-large hvr-pop hvr-rectangle-out1"> Cancel </a>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<style>
.pad-lr {
	padding-left: 7px;
	padding-right: 7px;
}

 .marg-left-but{
   margin-left:5px;
}
 .marg-right-but{
   margin-right:5px;
 }
label {
	font-weight: bold;
	display: inline-block;
	color: #7f7f7f !important;
}

.btn-width {
	min-width: 8%;
}

.mr-10 {
	margin-right: 10px;
}

.btn-bold-large {
	font-weight: 700 !important;
	font-size: 17px;
}

.btn-backToList {
	background-color: #757575 !important;
	border-color: #757575 !important;
	font-weight: 300;
	color: #fff !important;
}

.dynamic_align {
	text-align: center;
}

h1, h2, h3, h4, h5, h6 {
	color: #424242;
	font-family: inherit !important;
}

b, strong {
	font-family: inherit !important;
}

#main {
	float: unset;
}

.panel .panel-heading .panel-title a {
	font-family: inherit !important;
}

label {
	font-family: inherit !important;
}

.table>thead>tr>th, .table>tbody>tr>th, .table>tfoot>tr>th {
	font-family: inherit !important;
}

.main_table_wrapper .item_detail, .main_table_wrapper .item_name {
	display: inline-block;
	width: 100%;
}

.s2_view_desc {
	color: #0095d5;
	font-size: 80%;
	
}

.panel-heading {
	border-bottom: 1px solid #dfe8f1 !important;
}

.d-flex-center {
	display: flex;
	justify-content: center;
}

@media screen and (max-width: 768px) {
	.main-panal-box {
		width: 100%;
	}
	.evnt-info-border {
		padding: 10px;
	}
	.evnt-common-border {
		padding: 10px;
	}
	.logo-img-algn {
		margin: 0;
	}
	.main-panal-box label {
		max-width: none;
	}
	.btn-bold-large {
		font-weight: 700 !important;
		font-size: 15px;
		width: 160px;
		margin-top: 10px;
	}
}
</style>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/content-box.css"/>" />
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery-core.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/bootstrap/js/bootstrap.js"/>"></script>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<sec:authorize access="hasRole('BUYER_CHANGE_EMAIL') or (hasRole('ADMIN') and hasRole('OWNER'))" var="canUpdateComEmail" />
<div id="page-content-wrapper">
	<div id="page-content">
		<div class="container">
			<ol class="breadcrumb">
				<li><a href="${pageContext.request.contextPath}/owner/ownerDashboard">Dashboard</a></li>
				<li><a href="${pageContext.request.contextPath}/owner/financeCompanyList">Finance Company List</a></li>
				<li class="active">Finance Company Detail</li>
			</ol>
			<!-- page title block -->
			<div class="Section-title title_border gray-bg">
				<h2 class="trans-cap supplier">Finance Company Detail</h2>
				<div class="trans-cap form-cursor-hand form-left" style="float: right; margin-right: 0px;">
					<sec:authorize access="hasRole('BUYER_ACCOUNT_MGMT') or (hasRole('ADMIN') and hasRole('OWNER'))" var="canManage">
						<form>
							<c:if test="${financeObj.status == 'ACTIVE'}">
								<div class="panel-heading" id="opensupDetail">Suspend Finance Company</div>
							</c:if>
							<c:if test="${financeObj.status == 'SUSPENDED'}">
								<div class="panel-heading" id="openActDetail">Activate Finance Company</div>
							</c:if>
						</form>
						<c:if test="${!financeObj.registrationComplete}">
							<form name="form" method="post" action="${pageContext.request.contextPath}/owner/resendFinanceActivationLink/${financeObj.id}">
								<input type="hidden" id="financeId" name="financeId" value="${financeObj.id}" />
								<div id="idResendFinanceActivationEmail" data-toggle="tooltip" data-placement="top" title="Resend Activation Email" data-original-title="Resend Activitation Email" class="panel-heading">Resend Activitation Email</div>
							</form>
						</c:if>
						<c:if test="${adminUser != null}">
							<form:form name="form" method="post" action="${pageContext.request.contextPath}/owner/toggleFinanceAdminAccountStatus" modelAttribute="adminUser">
								<div data-toggle="tooltip" data-placement="top" title="${adminUser.locked ? 'Click to unclock' : 'Click to lock'}" data-original-title="${adminUser.locked ? 'Click to unclock' : 'Click to lock'}" class="panel-heading"
									onclick="$(this).closest('form').submit();">
									<form:hidden name="id" path="id" />
									<input type="hidden" name="financeid" value="${financeObj.id}" /> Admin account status :
									<span style="color:${adminUser.locked ? 'red' : 'green'}"> ${adminUser.locked ? '<i class="fa fa-lock" aria-hidden="true"></i>' : '<i class="fa fa-unlock" aria-hidden="true"></i>'} </span>
								</div>
							</form:form>
						</c:if>

					</sec:authorize>
				</div>
			</div>
			<div class="clear"></div>
			<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
			<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
			<div class="col-md-12 pad0">
				<div class="panel bgnone">
					<div class="panel-body">
						<div class="example-box-wrapper">
							<div class="panel-group" id="accordion">
								<div class="panel">
									<div class="panel-heading">
										<h4 class="panel-title">
											<a data-toggle="collapse" data-parent="#accordion" href="#collapseOne"> Company Detail
												<p style="float: right; margin-right: 30px;">Account Status: ${financeObj.status}</p>
											</a>
										</h4>
									</div>
									<div id="collapseOne" class="panel-collapse collapse in">
										<div class="panel-body">
											<table class="tabaccor financedetail" cellpadding="0" cellspacing="0" border="0">
												<tr>
													<td>
														<strong>Company Name</strong>
													</td>
													<td>${financeObj.companyName}</td>
												</tr>
												<tr>
													<td>
														<strong><spring:message code="supplier.registration.company.number" /></strong>
													</td>
													<td>${financeObj.companyRegistrationNumber}</td>
												</tr>
												
												<tr>
													<td>
														<strong><spring:message code="supplier.year.of.establish" /></strong>
													</td>
													<td>${financeObj.yearOfEstablished}</td>
												</tr>
												<tr>
													<td>
														<strong><spring:message code="supplier.company.address" /></strong>
													</td>
													<td>${financeObj.line1},<br> ${financeObj.line2},<br> ${financeObj.city}<br> ${financeObj.registrationOfCountry.countryName}<br>
													</td>
												</tr>
												<tr>
													<td>
														<strong><spring:message code="supplier.company.tel.number" /></strong>
													</td>
													<td>${financeObj.companyContactNumber}</td>
												</tr>
												<tr>
													<td>
														<strong><spring:message code="supplier.company.fax.number" /></strong>
													</td>
													<td>${financeObj.faxNumber}</td>
												</tr>
												<tr>
													<td>
														<strong><spring:message code="supplier.company.website" /></strong>
													</td>
													<td>${financeObj.companyWebsite}</td>
												</tr>
												<tr>
													<td>
														<strong><spring:message code="supplier.primary.login.email" /></strong>
													</td>
													<td>${financeObj.loginEmail}</td>
												</tr>
												<tr>
													<td>
														<strong>Communication Email</strong>
													</td>
													<td>
														<span class="bEmail">${financeObj.communicationEmail}</span>
														<c:if test="${canUpdateComEmail}">
															<input type="hidden" value="" id="editEmailValue">
															<a href="#" data-mail="${financeObj.communicationEmail}" id="edit_mail"> <i class="fa fa-pencil-square-o" aria-hidden="true"></i>
															</a>
														</c:if>
													</td>
												</tr>
											</table>
										</div>
									</div>
								</div>
								<div class="panel">
									<div class="panel-heading">
										<h4 class="panel-title">
											<a data-toggle="collapse" data-parent="#accordion" href="#collapseTwo"><spring:message code="supplier.primary.contact" /> </a>
										</h4>
									</div>
									<div id="collapseTwo" class="panel-collapse collapse in">
										<div class="panel-body">
											<table class="tabaccor" cellpadding="0" cellspacing="0" border="0">
												<tr>
													<td>
														<strong><spring:message code="supplier.primary.contact" /></strong>
													</td>
													<td>${financeObj.fullName}</td>
												</tr>
												<tr>
													<td>
														<strong><spring:message code="supplier.primary.login.email" /></strong>
													</td>
													<td>${financeObj.loginEmail}</td>
												</tr>
												<tr>
													<td>
														<strong>Communication Email </strong>
													</td>
													<td>
														<span class="bEmail">${financeObj.communicationEmail}</span>
													</td>
												</tr>
												<tr>
													<td>
														<strong><spring:message code="supplier.primary.mob.number" /></strong>
													</td>
													<td>${financeObj.mobileNumber}</td>
												</tr>
											</table>
										</div>
									</div>
								</div>



							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<div class="modal fade" title="Enter password " id="suppDetailModal" role="dialog">
	<div class="modal-dialog">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<button class="close for-absulate" type="button" data-dismiss="modal">X</button>
			</div>
			<form action="${pageContext.request.contextPath}/owner/suspendFinance" method="post">
				<input type="hidden" name="financeid" value="${financeObj.id}"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<div class="modal-body">
					<div class="row">
						<label class="col-sm-2 control-label"> Remarks </label>
						<div class="col-sm-6 col-md-6">
							<textarea id="remarks" name="remarks" class="form-control mar-b10" maxlength="250"> </textarea>
						</div>
						<div class="col-sm-4 col-md-4">
							<button class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out marg-left-20" type="submit">Submit</button>
						</div>
					</div>
				</div>
			</form>
		</div>
	</div>
</div>
<div class="modal fade" title="Activate Finance Company " id="financeDetailModal" role="dialog">
	<div class="modal-dialog">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<button class="close for-absulate" type="button" data-dismiss="modal">X</button>
			</div>
			<form action="${pageContext.request.contextPath}/owner/activateFinance" method="post">
				<input type="hidden" name="financeid" id="financeid" value="${financeObj.id}"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<div class="modal-body">
					<div class="row">
						<label class="col-sm-2 control-label"> Remarks </label>
						<div class="col-sm-6 col-md-6">
							<textarea id="remarks" name="remarks" class="form-control mar-b10" maxlength="250"> </textarea>
						</div>
						<div class="col-sm-4 col-md-4">
							<button class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out marg-left-20" type="submit">Submit</button>
						</div>
					</div>
				</div>
			</form>
		</div>
	</div>
</div>
<div class="modal fade" title="Enter Comunication Email " id="editMailModal" role="dialog">
	<div class="modal-dialog">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>Enter new email</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">X</button>
			</div>
			<div class="modal-body">
				<div class="row">
					<label class="col-sm-2 control-label"> </label>
					<form id="updateEmailForm">
						<div class="col-sm-6 col-md-6">
							<input type="hidden" name="financeid" id="financeid" value="${financeObj.id}"> <input type="text" style="margin-top: 0;" class="form-control mar-b10" id="newEmail" data-validation="required email">
						</div>
						<div class="col-sm-4 col-md-4">
							<button id="submitEmail" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out marg-left-20" data-dismiss="modal">Submit</button>
						</div>
					</form>
				</div>
			</div>
		</div>
	</div>
</div>
<!-- change subscription pop up -->
<script type="text/javascript">
	$('document')
			.ready(
					function() {

						$("#opensupDetail").click(function() {
							$("#suppDetailModal").modal({
								backdrop : 'static',
								keyboard : false
							});
						});
						$("#openActDetail").click(function() {
							$("#financeDetailModal").modal({
								backdrop : 'static',
								keyboard : false
							});
						});
						$("#edit_mail").click(function() {
							if ($("#editEmailValue").val() != '') {
								$("#newEmail").val($("#editEmailValue").val());
							} else {
								$("#newEmail").val($(this).data("mail"));
							}
							$("#editMailModal").modal({
								backdrop : 'static',
								keyboard : false
							});
						});
						$("#changeSubscription").click(function() {
							$("#confirmChangeSubscription").modal({
								backdrop : 'static',
								keyboard : false
							});
						});

						$("#submitRemarks")
								.click(
										function() {

											var header = $(
													"meta[name='_csrf_header']")
													.attr("content");
											var token = $("meta[name='_csrf']")
													.attr("content");

											var remarks = $("#remarks").val()
											var financeid = $('#financeid')
													.val();
											$
													.ajax({
														url : getOwnerContextPath()
																+ "/suspendFinance/"
																+ financeid
																+ "/" + remarks,
														async : false,
														type : "POST",
														beforeSend : function(
																xhr) {
															$('#loading')
																	.show();
															xhr
																	.setRequestHeader(
																			header,
																			token);
														},
														success : function(
																data,
																textStatus,
																request) {
															var success = request
																	.getResponseHeader('success');
															showMessage(
																	'SUCCESS',
																	success);
															window.location.href = data;
															$('#loading')
																	.hide();
														},
														error : function(
																request,
																textStatus,
																errorThrown) {
															$(
																	'p[id=idGlobalErrorMessage]')
																	.html(
																			request
																					.getResponseHeader('error'));
															$(
																	'div[id=idGlobalError]')
																	.show();
															showMessage(
																	'ERROR',
																	request
																			.getResponseHeader('error'));
															$('#loading')
																	.hide();
														},
														complete : function() {
															$('#loading')
																	.hide();
														}
													});
										});

						$("#submitEmail")
								.click(
										function(e) {
											e.preventDefault();

											if (!$('#updateEmailForm')
													.isValid()) {
												return false;
											}
											var header = $(
													"meta[name='_csrf_header']")
													.attr("content");
											var token = $("meta[name='_csrf']")
													.attr("content");
											var email = $("#newEmail").val()
											var financeid = $('#financeid')
													.val();
											$
													.ajax({
														url : getOwnerContextPath()
																+ "/updateFinanceComunicationEmail/"
																+ financeid
																+ "/" + email,
														async : false,
														type : "POST",
														beforeSend : function(
																xhr) {
															$('#loading')
																	.show();
															xhr
																	.setRequestHeader(
																			header,
																			token);
														},
														success : function(
																data,
																textStatus,
																request) {
															$(".bEmail").html(
																	email);
															$("#editEmailValue")
																	.val(email);
															$("#newEmail").val(
																	email);
															var success = request
																	.getResponseHeader('success');
															showMessage(
																	'SUCCESS',
																	success);
															$('#loading')
																	.hide();
														},
														error : function(
																request,
																textStatus,
																errorThrown) {
															$(
																	'p[id=idGlobalErrorMessage]')
																	.html(
																			request
																					.getResponseHeader('error'));
															$(
																	'div[id=idGlobalError]')
																	.show();
															showMessage(
																	'ERROR',
																	request
																			.getResponseHeader('error'));
															$('#loading')
																	.hide();
														},
														complete : function() {
															$('#loading')
																	.hide();
														}
													});
										});

						
						
					});
</script>
<script type="text/javascript">
	$("#test-select").treeMultiselect({
		enableSelectAll : true,
		sortable : true
	});
</script>
<script type="text/javascript">
	$('document').ready(function() {
		var data = eval('${noteList}');

		$('#buyerNoteDetails').DataTable({
			'aaData' : data,
			"aoColumns" : [ {
				"mData" : "createdDate"
			}, {
				"mData" : "description"
			}, {
				"mData" : "incidentType"
			} ]
		});
	});
</script>
<script>
	$('document').ready(function() {
		$('.openAddNots > button').click(function() {
			$('.notesAddBlock').toggle();
		});
	});
</script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.js"/>"></script>
<script>
	$.validate({
		lang : 'en',
		validateOnBlur : false
	});
</script>
<script type="text/javascript" src="<c:url value="/resources/js/view/financecompany.js"/>"></script>
<style>
.form-left form {
	float: left;
	padding-left: 5px;
}

.form-cursor-hand form:hover {
	cursor: pointer;
}

#idTblPaymentHistory th input {
	min-width: 200px;
	text-align: center;
}

.ph_table.dataTable th {
	border-left: 0px solid #ddd !important;
	border-bottom: 0px solid #ddd !important;
}

table.dataTable {
	clear: both;
	max-width: none !important;
	margin-top: 0px !important;
	margin-bottom: 0px !important;
}
</style>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/moment.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker-demo.js?1"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/datepicker/datepicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/timepicker/timepicker.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/timepicker/timepicker.css"/>">
<script>
	$('#metric-start').bsdatepicker({
		format : 'dd/mm/yyyy',

	}).on('changeDate', function(ev) {
		$('#metric-end').prop("disabled", false);
		$('#metric-end').bsdatepicker({
			format : 'dd/mm/yyyy',
			minDate : ev.date,
			onRender : function(date) {
				return date.valueOf() < ev.date.valueOf() ? 'disabled' : '';
			}

		});

	});

	/* Datepicker bootstrap */

	$(function() {
		"use strict";

		$('#startDate').bsdatepicker({
			format : 'dd/mm/yyyy',
			onRender : function(date) {
				if (date.valueOf() <= $.now()) {
					return 'disabled';
				}
			}

		}).on('changeDate', function(e) {
			$(this).blur();
			$(this).bsdatepicker('hide');
		});

		$('#endDate').bsdatepicker({
			format : 'dd/mm/yyyy',
			onRender : function(date) {
				if (date.valueOf() <= $.now()) {
					return 'disabled';
				}
			}

		}).on('changeDate', function(e) {
			$(this).blur();
			$(this).bsdatepicker('hide');
		});
	});
</script>

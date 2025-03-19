<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page import="com.privasia.procurehere.core.enums.SubscriptionStatus"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<sec:authorize access="hasRole('SUPPLIER_APPROVAL') or hasRole('ADMIN')" var="canEdit" />
<script type="text/javascript">
	function canEdit() {
		return "${canEdit}";
	}
</script>
 

<div id="page-content-wrapper">
	<div id="page-content">
		<div class="container">
			<ol class="breadcrumb">
				<li class="active"><spring:message code="application.supplier.detail" /></li>
			</ol>
			<!-- page title block -->
			<div class="Section-title title_border gray-bg">
				<h2 class="trans-cap supplier">
					Supplier Performance
				</h2>
<%-- 				<input type="hidden" id="supplierId" name="supplierId" value="${supplier.id}" /> --%>
			</div>
			<div class="clear"></div>
			<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
			<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
			<div class="clear"></div>
			<div id="supplierPerformanceId" class="col-md-12 pad0" >
				<div class="panel">
					<div id="collapsePerform" class="panel-collapse collapse in">
						<div class="panel-body">
							<div class="marg-top-10" style="margin-bottom: 1%;">
									<div class="row ">
										<form id="idSeachForm">
											<div class="col-md-5 col-sm-8">
												<label><spring:message code="application.filter.By.Date" /></label>
												<input onfocus="this.blur()" name="dateTimeRange" data-date-start-date="0d" id="datepickerAnalytics" class="form-control for-clander-view" type="text" data-validation="required" data-validation-format="dd/mm/yyyy hh:ii A - dd/mm/yyyy hh:ii P" />
											</div>
											<div class="col-md-1 col-sm-2"  style="padding-left: 0px; margin-top: 22px;display:none;">
												<spring:message code="application.reset" var="resetLabel" />
												<button type="button" id="resetDate" class="btn btn-sm btn-black " style="height:37px" data-toggle="tooltip" data-placement="top" data-original-title="${resetLabel}">
													<i aria-hidden="true" class="glyph-icon icon-close"></i> <i class="glyph-icon icon-cross"></i>
												</button>
											</div>										
											<div class="col-md-2 col-sm-4">
												<label class="">Filter by Buyer</label>
												<select class="chosen-select" name="buyerList" id="buyerList" data-validation="required">
													<option value="" >Please select buyer</option>
													<c:forEach items="${buyerList}" var="buyer">
														<option value="${buyer.id}">${buyer.companyName}</option>
													</c:forEach>
												</select>
											</div>
											<div class="col-md-2 col-sm-4">
												<button class="btn btn-primary" id="spFilter" name="spFilter" style="margin-top: 23px;">
													<spring:message code="application.search" />
												</button>
												<label></label>
											</div>
										</form>
									</div>

									<div class="import-supplier-inner-first-new global-list form-middle row col-md-5 col-sm-5 marg-top-20 ">
										<div class="ph_tabel_wrapper byBuyertbl-style">
											<div class="main_table_wrapper ph_table_border" style="margin: 0;">
												<table class="ph_table  display table table-bordered overalScoreByBuyer" >
													<thead>
														<tr>
															<th colspan="4" class="hdn-clr align-left" ><h4>Average Score by Buyer</h4></th>
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
															<td class="width_100 width_100 align-left"></td>
															<td class="width_100 width_100 align-left"></td>
															<td class="width_100 width_100 align-left"></td>
															<td class="width_200_fix width_200 align-left"></td>
														</tr>
													</tbody>
												</table>
											</div>
										</div>
									</div>
									
									<div class="import-supplier-inner-first-new global-list form-middle row col-md-5 col-sm-5 marg-top-20 ">
										<div class="ph_tabel_wrapper byBuyertbl-style">
											<div class="main_table_wrapper ph_table_border" style="margin: 0;">
												<table class="ph_table  display table table-bordered overalScoreByBu" >
													<thead>
														<tr>
															<th colspan="4" class="hdn-clr align-left"><h4 class="hdn-clr">Average Score by Business Unit</h4></th>
														</tr>
														<tr>
															<th class="width_100 width_100 align-left">Business Unit</th>
															<th class="width_100 width_100 align-left">Overall Score (%)</th>
															<th class="width_100 width_100 align-left">Rating</th>
															<th class="width_200_fix width_200 align-left">Rating Description</th>
														</tr>
													</thead>
													<tbody id="scoringByBUnit">
														<tr>
															<td colspan="4">Please choose buyer to view data</td>
 														</tr>
													</tbody>
												</table>
											</div>
										</div>
									</div>
									<div class="import-supplier-inner-first-new global-list form-middle row col-md-5 col-sm-5 marg-top-20 ">
										<div class="ph_tabel_wrapper ">
											<div style="margin: 0;">
												<table class="ph_table  display table table-bordered ovralScoreByEvent" >
													<thead>
														<tr>
															<th colspan="7" class="hdn-clr align-left"><h4>Overall Score by Event</h4></th>
														</tr>
														<tr>
															<th class="hdn-clr" colspan="2">Filter By Business Unit</th>
															<th colspan="3" class="align-left hdn-clr">
																<select class="form-control chosen-select" id="idBusinessUnit">
																	<option value="">Please select business Unit</option>
 																</select>
															</th>
<!-- 															<th class="align-left hdn-clr"></th> -->
															<th class="align-left hdn-clr"></th>
															<th class="align-left hdn-clr"></th>
														</tr>
														<tr>
															<th class="width_100 width_100 align-left">Event ID</th>
															<th class="width_100 width_100 align-left">Form ID</th>
															<th class="width_200_fix width_200 align-left">Event Type</th>
															<th class="width_200_fix width_200 align-left">Business Unit</th>
															<th class="width_100 width_100 align-left">Overall Score (%)</th>
															<th class="width_100 width_100 align-left">Rating</th>
															<th class="width_200_fix width_200 align-left">Rating Description</th>
														</tr>
													</thead>
													<tbody id="scoringByEvent" >
														<tr>
															<td class="width_100 width_100 align-left"></td>
															<td class="width_100 width_100 align-left"></td>
															<td class="width_200_fix width_200 align-left"></td>
															<td class="width_200_fix width_200 align-left"></td>
															<td class="width_100 width_100 align-left"></td>
															<td class="width_100 width_100 align-left"></td>
															<td class="width_200_fix width_200 align-left"></td>
														</tr>
													</tbody>
												</table>
											</div>
										</div>
									</div>
									
									<div class="import-supplier-inner-first-new global-list form-middle row col-md-5 col-sm-5 marg-top-20 ">
										<div class="ph_tabel_wrapper">
											<div style="margin: 0;">
												<table class="ph_table  display table table-bordered overalScoreBasedOnPCByEvent" >
													<thead>
														<tr>
															<th colspan="3" class="hdn-clr align-left"><h4>Performance Criteria Score by Supplier Performance Form</h4></th>
														</tr>
														<tr>
															<th class="hdn-clr align-left">Form ID</th>
															<th colspan="2" class="align-left hdn-clr ">
																<select class="form-control chosen-select" id="idFormId">
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
															<td class="align-left"></td>
															<td class="align-left"></td>
															<td class="align-left"></td>
														</tr>
													</tbody>
													<tfoot id="scoringBySpCriteriaFoot">
													</tfoot>
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
<!-- Update communication email dialog -->
<script>
</script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/js-core/token-input.css"/>" />
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/content-box/contentbox.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.tokeninput.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/view/supplierPerformance.js?5"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/view/Notes.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/file-input/file-input.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/datepicker/datepicker.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/timepicker/timepicker.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/timepicker/timepicker.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/moment.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker-demo.js?1"/>"></script>
<script type="text/javascript">

	$(document).ready(function() {
		$.validate({
			lang : 'en',
		});
		
		$('#datepickerAnalytics').daterangepicker({
			format : 'DD/MM/YYYY h:mm A',
		});
		
	});

	$(function() {
		"use strict";

		$('#expiryDate').bsdatepicker({
			format : 'dd/mm/yyyy',
			onRender : function(date) {
				if (date.valueOf() < $.now()) {
					return 'disabled';
				}
			}

		}).on('changeDate', function(e) {
			$(this).blur();
			$(this).bsdatepicker('hide');
		});

		$.formUtils.addValidator({
		name : 'validate_date',
		validatorFunction : function(value, $el, config, language, $form) {
			var now = new Date($('#supplierEndDate').val().split('/')[2], Number($('#supplierEndDate').val().split('/')[1])-1, $('#supplierEndDate').val().split('/')[0]);
			// new Date("01/02/2020").getTime()
			var then = new Date(value.split('/')[2], Number(value.split('/')[1])-1, value.split('/')[0]);
			if (now.valueOf() > then.valueOf() ) {
				return false;
			} else {
				return true;
			}
		},
		errorMessage : 'The subscription end date cannot be less than the current subscription date.',
		errorMessageKey : 'validateDate'
	});

		
	});
</script>

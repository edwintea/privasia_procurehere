<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize access="hasRole('ADMIN')" var="isAdmin" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<sec:authentication property="principal.id" var="loggedInUserId" />

<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css?1"/>">

<script type="text/javascript" src="<c:url value="/resources/js/numeral.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>



<style type="text/css">

#pushPr {
	width: 600px;
	margin: auto;
}

#pushPr div.white-bg {
	border: 1px solid #e8e8e8;
}

#ConfirmSaveOrTrasfer {
	width: 500px;
	margin: auto;
}
#ConfirmSaveOrTrasfer div.white-bg {
	border: 1px solid #e8e8e8;
}


#confirmPerformanceEvaluation {
	width: 500px;
	margin: auto;
}

#confirmPerformanceEvaluation div.white-bg {
	border: 1px solid #e8e8e8;
}

#dlgConfirmContract {

}
#dlgConfirmContract div.white-bg {
	border: 1px solid #e8e8e8;
}
 
.remarks-display {
    font-size: 13px;
    display: block;
    float: none;
    background: #fff;
    width: 100%;
    min-height: 38px;
    padding: 6px 12px;
    color: #2b2f33;
    border: #dfe8f1 solid 1px;
    white-space: pre;
}

.exSixbtn {
    left: 170px;
    top: 17px;
}
.row {
	margin-right: 0px;
	margin-left: 0px;
}

.saas-content {
	margin-top: 3%;
	height: auto;
}

.dropdown-div {
	float: left;
}

.dropdwon-button {
	padding: 10px;
	font-size: 16px;
}

.float-right {
	float: right;
}

.dropdwon-caret {
	margin-left: 8px !important;
}

.span-price {
	font-size: 16px;
	font-weight: normal !important;
}

.head-four {
	font-family: sans-serif;
	font-weight: 600;
}

.form-control-inp {
	width: 100%;
	max-width: 80%;
}

.table-top-mrgn {
	margin-top: 0%;
	border: 1px solid #ddd;
}

.thead-style {
	background-color: #f1f1f1;
}

th {
	padding: 15px !important;
}

td {
	padding-left: 15px !important;
}

.tfoot-style {
	font-weight: 600;
}

.form-left-marg {
	margin-left: 11%;
}

.btn-div {
	margin-top: 3%;
	width: 70%;
	box-sizing: border-box;
	float: left;
}

.btn-div button {
	padding: 8px;
	width: 100%;
	font-size: 16px;
	max-width: 12%;
}

.mr-20 {
	margin-right: 20px;
}

.last-drpdwn-marg {
	margin-top: 3%;
}

.fit-left {
	margin-right: -30px;
	margin-left: 0px;
	float: right;
	margin-top: 4%;
}

.error-pos {
	position: absolute;
	top: 40px;
}

.margin-tp-5 {
	margin-top: 5%;
}

@media only screen and (max-width: 600px) {
	.itemised {
		float: left !important;
		width: 100%;
	}
	/* 	.fit-left{
margin-right: 0px; 
margin-left: 0px;
width: 100% !important;
	} */
}

.pop-up-pr {
	width: 30%;
	position: absolute;
	left: 50%;
	top: 50%;
	transform: translate(-50%, -50%);
	padding: 0;
	box-shadow: 0px 0px 10px 1px #ccc;
	border-top-left-radius: 9px;
	border-top-right-radius: 9px;
}

.ui-dialog-titlebar {
	padding: 10px;
	border-bottom: 1px solid transparent;
	background: #0095d5;
	line-height: 1;
	width: 100%;
	display: inline-block;
	margin-top: 0;
	border-top-left-radius: 9px;
	border-top-right-radius: 9px;
}

.ui-dialog-titlebar h3 {
	float: left;
	color: #fff;
}

.ui-dialog-titlebar button {
	float: right;
	padding: 3px;
	background: #fff;
}

.form-group {
	border: none !important;
}

.selectCategory {
	min-width: 170px;
}

.chosen-container .chosen-results li.active-result {
	text-align: left;
	word-wrap: normal;
}

.w-110 {
	width: 110px;
}

.pos-l-10 {
	position: relative;
	left: 10px
}

@media only screen and (max-width: 767px) {
	.pop-up-pr {
		width: 85%;
	}
}

@media only screen and (min-width: 768px) and (max-width: 1024px) {
	.pop-up-pr {
		width: 40%;
		margin-left: 10%;
	}
}

</style>

<div id="page-content-wrapper">
	<div id="page-content">
		<div class="container">
			<ol class="breadcrumb">
				<li>
					<a href="${pageContext.request.contextPath}/buyer/buyerDashboard"> <spring:message code="application.dashboard" /></a>
				</li>
				<li>
					<a href="${pageContext.request.contextPath}/buyer/${eventType}/eventSummary/${event.id}">${eventType.value} <spring:message code="application.summary" /></a>
				</li>
				<li class="active"><spring:message code="label.event.award" /></li>
			</ol>

			<input type="hidden" name="eventId" class="rfxEventId" value="${event.id}" /> 
			<input type="hidden" name="bqId" class="bqId" value="${suppBqId}" />
			<jsp:include page="ongoinEventHeader.jsp" />
			<div class="Invited-Supplier-List import-supplier white-bg">
				<div class="meeting2-heading">
					<h3>
						<spring:message code="label.event.award" />
					</h3>
<%-- 					<div class="marg-bottom-20 marg-left-20">
						<a class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out pull-right" style="margin-top: 4px; margin-right: 4px;" href="${pageContext.request.contextPath}/buyer/${eventType}/eventSummary/${event.id}">
								<i class="fa fa-chevron-left" aria-hidden="true"></i> Back to <spring:message code="eventdetails.event.details" />
						</a>
					</div>
 --%>			
 				</div>
				<div class="import-supplier-inner-first-new pad_all_15 global-list">
					<!-- <header class="form_header"> </header> -->
					<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
					<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
					<div class="clear"></div>
					<div class="tab-main">
						<input type="hidden" value="${event.decimal}" id="decimal">
						<ul class="nav-responsive nav nav-tabs marg-bottom-5 event-details-tabs supplier-tab">
							<c:forEach items="${supplierBqs}" var="suppBqs">
								<li class="tab-link ${suppBqs.bq.id == suppBqId ? 'active' : ''}"><a class="" href="${pageContext.request.contextPath}/buyer/${eventType}/eventAward/${event.id}/${suppBqs.bq.id}">${suppBqs.bq.name}</a></li>
							</c:forEach>
						</ul>
					</div>
					<c:if test="${not empty eventSuppliers}">
						<form:form method="POST" modelAttribute="eventAward" id="eventAwardCriteriaForm">
							<div class="row marg-bottom-20" style="margin-top: 70px;">
								<input type="hidden" name="eventId" class="rfxEventId" value="${event.id}" /> 
								<input type="hidden" name="bqId" class="bqId" value="${suppBqId}" />
								<div class="col-md-2">
									<label class="marg-top-10"><spring:message code="eventawards.criteria.label" /></label>
								</div>
								<div class="col-md-4 itemised">
									<c:if test="${(event.awardStatus eq 'PENDING' || event.awardStatus eq 'APPROVED')}">
										<form:hidden path="awardCriteria"/>
										<div class="form-control">${eventAward.awardCriteria.value }</div>
									</c:if>
									<c:if test="${!(event.awardStatus eq 'PENDING' || event.awardStatus eq 'APPROVED')}">
										<form:select path="awardCriteria" id="awardCriteria" var="awdCriteria" cssClass="chosen-select form-control disablesearch awardCriteria" data-validation="required">
											<form:options items="${awardCriteria}" itemLabel="value" />
										</form:select>
									</c:if>
								</div>
							</div>
						</form:form>
						<form:form method="POST" action="${pageContext.request.contextPath}/buyer/${eventType}/saveEventAward?${_csrf.parameterName}=${_csrf.token}" modelAttribute="eventAward" id="eventAwardForm" enctype="multipart/form-data">
							<input type="hidden" class="startDate" name="contractStartDateHid" />
							<input type="hidden" class="endDate" name="contractEndDateHid" />
							<input type="hidden" class="groupCode" name="groupCodeHid" />
							<input type="hidden" class="referenceNumber" name="referenceNumberHid" />
							<input type="hidden" class="contractCreator" name="contractCreatorHid" />
							
							<div class="Invited-Supplier-List dashboard-main tabulerDataList">
							<div class="Invited-Supplier-List-table add-supplier">
							<div class="ph_tabel_wrapper">
							<div class="main_table_wrapper ph_table_border pad_all_15">
								<table class="data display table table-bordered noarrow dataTable no-footer" id="tableXyz">
									<thead>
										<tr class="thead-style">
											<th>No.</th>
											<th><p align="left" class="w-110">
													<spring:message code="label.rftbq.th.itemname" />
												</p></th>
											<th><p align="left" class="w-110">
													<spring:message code="supplier.name" />
												</p></th>
											<th><p align="left">
													<spring:message code="eventaward.supplier.price" />
												</p></th>
											<th><p align="left">
													<spring:message code="summarydetails.event.conclusion.award.price" />
												</p></th>
											<th><p align="left">
													<spring:message code="account.overview.tax" />
												</p></th>
											<th><p align="left">
													<spring:message code="eventawards.tax.value" />
												</p></th>
											<th><p align="left">
													<spring:message code="eventawards.total.price" />
												</p></th>
											<th><p align="left">
													<spring:message code="eventawards.add.product.list" />
												</p></th>
											<th><p align="left">
													<spring:message code="application.catagory" />
												</p></th>
											<th><p align="left">
													<spring:message code="productz.code" />
												</p></th>
											<th><p align="left">
													<spring:message code="contract.item.brand" />
												</p></th>
											<th><p align="left">
													<spring:message code="product.item.type" />
												</p></th>
											<th><p align="left">
													<spring:message code="label.costcenter" />
												</p></th>
											<th><p align="left">
													<spring:message code="product.Contract.businessUnit" />
												</p></th>

										</tr>
									</thead>
									<tbody>
										<form:input type="hidden" path="awardCriteria" name="awardCriteria" value="${eventAward.awardCriteria}" />
										<form:input type="hidden" path="rfxEvent.id" name="rfxEventId" class="rfxEventId" value="${event.id}" />
										<form:input type="hidden" path="bq.id" name="bqId" class="bqId" value="${supplierBq.bq.id}" />
										<form:input type="hidden" path="id" name="eventAwardId" class="eventAwardId" value="${id}" />
										<c:forEach items="${supplierBq.supplierBqItems}" var="suppBqItem" varStatus="count">
											<tr>
												<input type="hidden" class="rowSeries" id="rowSeries-${count.index}" value="${count.index}" />
												<form:input type="hidden" path="rfxAwardDetails[${count.index}].id" name="awardDetailId" />
												<form:input type="hidden" path="rfxAwardDetails[${count.index}].bqItem.uom.id" id="bqUomId-${count.index}" />
												<form:input type="hidden" path="rfxAwardDetails[${count.index}].bqItem.level" value="${suppBqItem.bqItem.level}" id="bqLevel-${count.index}" />
												<form:input type="hidden" path="rfxAwardDetails[${count.index}].bqItem.order" value="${suppBqItem.bqItem.order}" id="bqOrder-${count.index}" />
												<input type="hidden" name="priceType" id="" value="${suppBqItem.bqItem.priceType}" class="priceType2" />
												<td>${suppBqItem.bqItem.level}.${suppBqItem.bqItem.order}</td>
												<td align="left"><c:out value="${suppBqItem.bqItem.itemName}" /><form:input type="hidden" path="rfxAwardDetails[${count.index}].bqItem.id" name="bqItemId" value="${suppBqItem.bqItem.id}" id="bqItemId-${count.index}" /> <br /> <c:if test="${suppBqItem.bqItem.priceType == 'BUYER_FIXED_PRICE'}">
														<span class="bs-label label-success" style="color: #fff"><spring:message code="eventsummary.bq.buyer.fixedprice" /></span>&nbsp;
												</c:if> <c:if test="${suppBqItem.bqItem.priceType == 'TRADE_IN_PRICE'}">
														<span class="bs-label label-info" style="color: #fff"><spring:message code="eventsummary.bq.trade.price" /></span>&nbsp;
												</c:if>
												</td>
												<td align="left">
													<div class="dropdown">
														<c:if test="${suppBqItem.bqItem.order != '0'}">
															<c:if test="${eventAward.awardCriteria == 'LOWEST_TOTAL_PRICE' || eventAward.awardCriteria == 'LOWEST_ITEMIZED_PRICE' || eventAward.awardCriteria == 'HIGHEST_TOTAL_PRICE' || eventAward.awardCriteria == 'HIGHEST_ITEMIZED_PRICE'}">
																<form:input type="hidden" path="rfxAwardDetails[${count.index}].supplier.id" value="${suppBqItem.supplier.id }" />${suppBqItem.supplier.companyName}
															</c:if>
															<c:if test="${!(eventAward.awardCriteria == 'LOWEST_TOTAL_PRICE' || eventAward.awardCriteria == 'LOWEST_ITEMIZED_PRICE' || eventAward.awardCriteria == 'HIGHEST_TOTAL_PRICE' || eventAward.awardCriteria == 'HIGHEST_ITEMIZED_PRICE')}">
																<form:select cssClass="chosen-select" id="awardSupplier" class="awardSupplier" onchange="manualSelection(this.value, $('#bqItemId-${count.index}').val(), $('#rowSeries-${count.index}').val())" path="rfxAwardDetails[${count.index}].supplier.id" data-validation="required">
																	<form:options items="${eventSuppliers}" itemValue="id" itemLabel="companyName" />
																</form:select>
															</c:if>
														</c:if>
													</div>
												</td>
												<td align="left"><c:if test="${suppBqItem.bqItem.order != '0'}">
														<c:set var="originalPrice" value="${(eventAward.rfxAwardDetails[count.index].originalPrice == null || changeCriteria) ? suppBqItem.totalAmount : eventAward.rfxAwardDetails[count.index].originalPrice}" />
														<form:input type="hidden" path="rfxAwardDetails[${count.index}].originalPrice" value="${originalPrice}" class="originalPrice" id="originalPricehid-${count.index}" />
														<div id="originalPrice-${count.index}">
															<fmt:formatNumber type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}" value="${originalPrice}" />
														</div>
													</c:if></td>
												<td align="left">
													<c:choose>
														<c:when test="${event.decimal==1}">
															<c:set var="decimalSet" value="0,0.0"></c:set>
														</c:when>
														<c:when test="${event.decimal==2}">
															<c:set var="decimalSet" value="0,0.00"></c:set>
														</c:when>
														<c:when test="${event.decimal==3}">
															<c:set var="decimalSet" value="0,0.000"></c:set>
														</c:when>
														<c:when test="${event.decimal==4}">
															<c:set var="decimalSet" value="0,0.0000"></c:set>
														</c:when>
														<c:when test="${event.decimal==5}">
															<c:set var="decimalSet" value="0,0.00000"></c:set>
														</c:when>
														<c:when test="${event.decimal==6}">
															<c:set var="decimalSet" value="0,0.000000"></c:set>
														</c:when>
													</c:choose> 
													<c:if test="${suppBqItem.bqItem.order != '0'}">
														<div id="awardPrice-${count.index}" style="display: none; ">
															<fmt:formatNumber var="fmtAwardPrice" type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}" value="${(eventAward.rfxAwardDetails[count.index].awardedPrice  == null || changeCriteria)? suppBqItem.totalAmount : eventAward.rfxAwardDetails[count.index].awardedPrice}" />
														</div>
														<div class="${eventType != 'RFI' && (event.awardStatus eq 'PENDING' || event.awardStatus eq 'APPROVED') ? 'disabled' : ''}">
															<form:input type="text" data-changeto="${count.index}" data-rate="${not empty suppBqItem.tax ? suppBqItem.tax : 0 }" data-taxtype="${suppBqItem.taxType}" path="rfxAwardDetails[${count.index}].awardedPrice" value="${fmtAwardPrice}" id="awardPricehid-${count.index}" class="form-control form-control-inp awardPrice" data-validation="" data-validation-regexp="^[\d,]{1,14}(\.\d{1,${event.decimal}})?$"  style="width: 126px;"/>
														</div>
													</c:if>
												</td>
												<td align="left"><c:if test="${suppBqItem.bqItem.order != '0'}">
														<form:input type="hidden" path="rfxAwardDetails[${count.index}].taxType" value="${eventAward.rfxAwardDetails[count.index].taxType == null || changeCriteria ? suppBqItem.taxType : eventAward.rfxAwardDetails[count.index].taxType}" class="taxType" id="taxTypehid-${count.index}" />
													</c:if>
													<div id="taxType-${count.index}">${eventAward.rfxAwardDetails[count.index].taxType == null || changeCriteria ? suppBqItem.taxType : eventAward.rfxAwardDetails[count.index].taxType}</div></td>
												<td><c:if test="${suppBqItem.bqItem.order != '0'}">
														<form:input type="hidden" path="rfxAwardDetails[${count.index}].tax" value="${eventAward.rfxAwardDetails[count.index].tax == null || changeCriteria ? suppBqItem.tax : eventAward.rfxAwardDetails[count.index].tax}" id="taxhid-${count.index}" />
														<div id="tax-${count.index}">
															<fmt:formatNumber type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}" value="${eventAward.rfxAwardDetails[count.index].tax == null || changeCriteria ? suppBqItem.tax : eventAward.rfxAwardDetails[count.index].tax}" />
														</div>
													</c:if></td>
												<td align="left"><c:if test="${suppBqItem.bqItem.order != '0'}">
														<form:input type="hidden" id="rfxawhid-${count.index}" path="rfxAwardDetails[${count.index}].totalPrice" value="${eventAward.rfxAwardDetails[count.index].totalPrice == null || changeCriteria ? suppBqItem.totalAmountWithTax : eventAward.rfxAwardDetails[count.index].totalPrice}" class="totalwithTax" />
													</c:if>
													<div id="rfxawdsp-${count.index}">
														<c:if test="${suppBqItem.bqItem.order != '0'}">
															<fmt:formatNumber type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}" value="${eventAward.rfxAwardDetails[count.index].totalPrice == null || changeCriteria ? suppBqItem.totalAmountWithTax : eventAward.rfxAwardDetails[count.index].totalPrice}" />

														</c:if>
													</div></td>
												<td align="left">
													<c:if test="${suppBqItem.bqItem.order != '0'}">
														<div class="${(event.awardStatus eq 'PENDING' || event.awardStatus eq 'APPROVED') ? 'disabled' : ''}">
															<input data-pid="${suppBqItem.bqItem.id}-${suppBqItem.supplier.id }" value="${suppBqItem.bqItem.id}-${suppBqItem.supplier.id }" type="checkbox" class="custom-itemCheck" name="rfxAwardDetails[${count.index}].selectItem" id="selectItem-${suppBqItem.bqItem.id}-${suppBqItem.supplier.id }">
														</div>
													</c:if>
												</td>
												<td align="left">
													<div class="selectCategory ${(event.awardStatus eq 'PENDING') ? 'disabled' : ''}">
														<c:if test="${suppBqItem.bqItem.order != '0'}">
															<select data-pid="${suppBqItem.bqItem.itemName}-${suppBqItem.supplier.id }" class="chosen-select productCategory" name="rfxAwardDetails[${count.index}].productCategory" id="category-${suppBqItem.bqItem.id}">
																<option value="">Select Category</option>
																<c:forEach items="${suppBqItem.productCategoryList}" var="item">
																	<option value="${item.id}">${item}</option>
																</c:forEach>
															</select>
														</c:if>
													</div>
												</td>
												<td align="left">
													<div class="product Code ${(event.awardStatus eq 'PENDING') ? 'disabled' : ''}">
														<c:if test="${suppBqItem.bqItem.order != '0'}">
															<spring:message code="systemsetting.product.code.placeholder" var="prodcode" />
															<form:input type="text" path="rfxAwardDetails[${count.index}].productCode" id="productCode-${suppBqItem.bqItem.id}" value="${productCode}" cssClass="form-control" class="form-control animated-search-filter1 search_textbox_1 " placeholder="${prodcode}" maxlength="50" style="width: 126px;"/>
														</c:if>
													</div>
												</td>
												<td align="left">
													<div class="brand ${(event.awardStatus eq 'PENDING') ? 'disabled' : ''}">
														<c:if test="${suppBqItem.bqItem.order != '0'}">
															<spring:message code="contract.brand.quantity" var="brands" />
															<form:input type="text" path="rfxAwardDetails[${count.index}].brand" id="brand" value="${brand}" cssClass="form-control" class="form-control animated-search-filter1 search_textbox_1 " placeholder="${brands}" maxlength="15" style="width: 126px;"/>
														</c:if>
													</div>
												</td>
												<td align="left">
													<div class="selectProductItemType ${(event.awardStatus eq 'PENDING') ? 'disabled' : ''}">
														<c:if test="${suppBqItem.bqItem.order != '0'}">
															<select class="chosen-select" name="rfxAwardDetails[${count.index}].productType" id="type">
																<option value="">Select Item Type</option>
																<c:forEach items="${itemTypeList}" var="item">
																	<option value="${item}">${item}</option>
																</c:forEach>
															</select>
														</c:if>
													</div>
												</td>
												<td align="left">
													<div class="selectCostCenter ${(event.awardStatus eq 'PENDING') ? 'disabled' : ''}" style="width: 163px;">
														<c:if test="${suppBqItem.bqItem.order != '0'}">
															<select class="chosen-select costCenter" name="rfxAwardDetails[${count.index}].costCenter" id="costCenterId${count.index}" >
																<option value="">Select Cost Center</option>
																<c:forEach items="${costCenterList}" var="cost" varStatus="status">
<%-- 																	<c:if test="${event.costCenter != null && event.costCenter.id == cost.id}">
																		<option value="${cost.id}" selected>${cost.costCenter} - ${cost.description}</option>
																	</c:if>
 --%>																	<c:if test="${status.index != 0}">
																		<option value="${cost.id}">${cost.costCenter} - ${cost.description}</option>
																	</c:if>
																</c:forEach>
															</select>
														</c:if>
													</div>
												</td>
												<td align="left">
													<div class="selectBusinessUnit ${(event.awardStatus eq 'PENDING') ? 'disabled' : ''}" style="width: 156px;">
														<c:if test="${suppBqItem.bqItem.order != '0'}">
															<select class="chosen-select businessUnit" name="rfxAwardDetails[${count.index}].businessUnit" data-index="${count.index}" id="businessUnitId${count.index}" >
																<option value="">Select Business Unit</option>
																<c:forEach items="${businessUnitData}" var="item" varStatus="status">
<%-- 																<c:if test="${status.index == 0}">
																	<option value="${item.id}" selected>${item.unitName}</option>
																</c:if> --%>
																<c:if test="${status.index != 0}">
																	<option value="${item.id}" >${item.unitName}</option>
																</c:if>
																</c:forEach>
															</select>
														</c:if>
													</div>
												</td>
											</tr>
										</c:forEach>
									</tbody>
									<tfoot class="tfoot-style">
										<tr>
											<td></td>
											<td></td>
											<td align="left"><spring:message code="eventawards.total" /></td>
											<td align="left"><form:input id="totalSupplierPrice" type="hidden" path="totalSupplierPrice" class="totalSupplierPrice" />
												<div id="originalPriceSum"></div></td>
											<td align="left"><form:input id="totalAwardPrice" type="hidden" path="totalAwardPrice" class="totalAwardPrice" />
												<div style="margin-left: 14px;" id="awardPriceSum"></div></td>
											<td></td>
											<td></td>
											<td align="left"><form:input id="grandTotal" type="hidden" path="grandTotalPrice" class="grandTotal" />
												<div id="totalAfterTax">
													<fmt:formatNumber type="number" minFractionDigits="${event.decimal}" maxFractionDigits="${event.decimal}" value="${eventAward.rfxAwardDetails[count.index].grandTotalPrice == null ? supplierBq.totalAfterTax : eventAward.rfxAwardDetails[count.index].grandTotalPrice}" />
												</div></td>
											<td></td>
											<td></td>
											<td></td>
										</tr>
									</tfoot>
								</table>
								</div>
								</div>
								</div>
							</div>
							<div class="row" style="margin-right: -15px; margin-left: -15px;">
								<div class="col-sm-6 col-md-6 col-lg-4 col-xs-12 marg-top-10">
									<label><spring:message code="pr.remark" />:</label>
									<spring:message code="conclude.remark.placeholder" var="remark" />
									<c:if test="${(event.awardStatus eq 'PENDING' || event.awardStatus eq 'APPROVED')}">
										<form:hidden path="awardRemarks"/>
										<div class="remarks-display">${eventAward.awardRemarks}</div>
									</c:if>
									<c:if test="${!(event.awardStatus eq 'PENDING' || event.awardStatus eq 'APPROVED')}">
										<form:textarea path="awardRemarks" name="awardRemarks" class="form-control textarea-counter" value="${awardRemarks}" data-validation="required length" placeholder="${remark}" data-validation-length="max500" rows="2"></form:textarea>
									</c:if>
								</div>
							</div>

							<c:if test="${event.awardStatus == null || (event.awardStatus != null && event.awardStatus eq 'DRAFT')}">
								<div class="row" style="margin-top: 15px; margin-left: -15px;">
									<div class="col-sm-6 col-md-6 col-lg-4 col-xs-12 ">
							            <c:if  test="${empty fileName}">
											<div>
												<label>Document Attachment:</label>
													<div class="fileinput fileinput-new input-group" data-provides="fileinput">
														<div class="form-control" data-trigger="fileinput" style="width: 600px;">
															<i class="glyphicon glyphicon-file fileinput-exists"></i> <span class="fileinput-filename"></span>
														</div>
														<span class="input-group-addon btn btn-black btn-file">
															<span class="fileinput-new">Select file </span>
															<span class="fileinput-exists">Change</span>
															<c:set var="fileType" value="" />
															<c:forEach var="type" items="${ownerSettings.fileTypes}" varStatus="index">
																<c:set var="fileType" value="${fileType}${index.first ? '': ', '}${type}" />
															</c:forEach>
															<form:input class="uploadAwardFileAttch" path="attachment" type="file" id="uploadAwardFileId" data-validation-allowing="${fileType}, application/vnd.ms-powerpoint, text/plain, application/msword, application/x-rar-compressed" data-validation="mime size" data-validation-max-size="${ownerSettings.fileSizeLimit}M" data-validation-error-msg-container="#Load_File-error-dialog" data-validation-error-msg-size="You can not upload file larger than ${ownerSettings.fileSizeLimit} MB" data-validation-error-msg-mime="You can only upload file ${ownerSettings.fileTypes}"></form:input>
														</span>
														<a href="#" class="input-group-addon btn btn-black fileinput-exists pos-l-10" data-dismiss="fileinput">Remove</a>
													</div>
													<div id="Load_File-error-dialog" style="width: 100%; float: left; margin: 0 0 0 0;"></div>
													<div style="margin-top: 15px;">
														Note:<br />
														<ul>
															<li>Max allowed file size is ${ownerSettings.fileSizeLimit} MB</li>
															<li>Allowed file extensions:${ownerSettings.fileTypes}.</li>
														</ul>
													</div>
											</div>
										</c:if>
									</div>
								</div>
							</c:if>

							<c:if test="${event.awardStatus == null || (event.awardStatus != null && (event.awardStatus eq 'DRAFT' || event.awardStatus eq 'PENDING' || event.awardStatus eq 'APPROVED'))}">
								<div class="row" style="margin-top: 15px; margin-left: -15px;">
									<div class="col-sm-6 col-md-6 col-lg-4 col-xs-12 ">
										<div id="deleteAwardData" style="width: 650px;">
											<c:if  test="${not empty fileName}">
												<c:url var="download" value="/buyer/${eventType}/downloadAwardFile/${fileId}" />
												<c:if test="${eventPermissions.editor}">
													<label>${fileName}</label>
												</c:if>
												<c:if test="${eventPermissions.owner || eventPermissions.approverUser ||
												eventPermissions.approver || eventPermissions.awardApprover}">
													<a class="downloadSnapshot marg-left-10" href="${download}" >
														<label style="cursor: pointer; text-decoration: underline;">${fileName}</label>
													</a>
												</c:if>
												<span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
												<c:if test="${eventPermissions.owner && event.status == 'COMPLETE' && event.awardStatus ne 'PENDING'}">
													<a onclick="deleteAwardData('${fileId}')">
																<span>
																	<img src="${pageContext.request.contextPath}/resources/images/delete.png" style="height: 23px; margin-left: 5px; cursor: pointer;" alt="download" title="${fileName}">
																</span>
													</a>
												</c:if>
											</c:if>
										</div>
									</div>
								</div>
							</c:if>

							<c:if test="${empty rfxTemplate or (!empty rfxTemplate && rfxTemplate.visibleAwardApproval)}">
							<c:if test="${(empty event.awardApprovals and event.status ne 'FINISHED')}">
								<div class="upload_download_wrapper clearfix marg-top-10 event_info w-42rm">
									<h4>
										<spring:message code="event.approvalroute.award" />
									</h4>
									<div id="apprTab" class="collapse in float-left width-100 position-relative in">
										<jsp:include page="eventAwardApproval.jsp"></jsp:include>
									</div>
								</div>
							</c:if>
	
							<c:if test="${not empty event.awardApprovals}">
								<input type="hidden" name="rfxEvent.enableAwardApproval" value="${event.enableAwardApproval}">
								<div class="panel sum-accord Approval-tabs marg-top-10">
									<div class="panel-heading">
										<h4 class="panel-title">
											<a data-toggle="collapse" class="accordion" href="#collapseeleven"><spring:message code="event.approvalroute.award" /></a>
											<c:if test="${eventPermissions.owner and event.status == 'COMPLETE' and (empty rfxTemplate or (!empty rfxTemplate and !rfxTemplate.readOnlyAwardApproval))}">
												<button class="editAwardApprovalPopupButton exSixbtn ${(!empty rfxTemplate and rfxTemplate.readOnlyAwardApproval) ? 'disabled' : '' }" data-toggle="dropdown" title="Edit">
													<img src="${pageContext.request.contextPath}/resources/images/edit-btn.png" alt="edit" />
												</button>
											</c:if>
										</h4>
									</div>
									<div id="collapseeleven" class="panel-collapse collapse accortab in">
										<div class="panel-body eleven-contant pad_all_15">
											<c:if test="${event.enableApprovalReminder}">
												<div>
													<label>Reminder Settings: </label>
													<div>
														<p>Reminder emails sent every ${event.reminderAfterHour} hours.</p>
														<p>Maximum ${event.reminderCount} reminder emails.</p>
														<c:if test="${event.notifyEventOwner}">
															<p>
																<spring:message code="notification.owner.message" />
															</p>
														</c:if>
													</div>
												</div>
											</c:if>
											<c:forEach items="${event.awardApprovals}" var="approval" varStatus="levelStatus">
												<div class="pad_all_15 float-left width-100 position-relative">
													<input type="hidden" name="awardApprovals[${levelStatus.index}].approvalType" value="${approval.approvalType}" />
													<label>Level ${approval.level}</label>
													<c:if test="${approval.active}">
														<span class="color-green marg-left-10"> 
															<i class="fa fa-arrow-left color-green" aria-hidden="true"></i>&nbsp;&nbsp;[<spring:message code="buyer.dashboard.active" />]
														</span>
													</c:if>
													<div class="Approval-lavel1-upper">
														<c:forEach items="${approval.approvalUsers}" var="user" varStatus="status">
															<input type="hidden" name="awardApprovals[${levelStatus.index}].approvalUsers" value="${user.user.id}" />
															<div class="Approval-lavel1 ${user.approvalStatus eq 'REJECTED' ? 'cross-red-mark' : ''} ${user.approvalStatus eq 'APPROVED' ? 'right-green-mark' : ''}">${user.user.name}
																<c:if test="${!user.user.active}">[<span class="inactiveCaption"><spring:message code="prsummary.inacive.status" /></span>]</c:if>
															</div>
															<c:if test="${fn:length(approval.approvalUsers) > (status.index + 1)}">
																<span class="pull-left">&nbsp;&nbsp${approval.approvalType}&nbsp;&nbsp</span>
															</c:if>
														</c:forEach>
													</div>
												</div>
											</c:forEach>
										</div>
										</div>
										</div>
										</c:if>
								<c:if test="${not empty event.awardComment}">
									<div class="panel sum-accord Approval-tabs marg-top-10">
										<div class="panel-heading">
											<h4 class="panel-title">
												<a data-toggle="collapse" class="accordion" href="#collapComments"><spring:message code="award.approval.comments" /></a>
											</h4>
										</div>
										<div id="collapComments" class="panel-collapse collapse accortab in">
											<div class="panel-body pad_all_15">
												<c:if test="${not empty event.awardComment}">
													<c:forEach items="${event.awardComment}" var="comment">
														<div class="Comments_inner ${comment.approved ? 'comment-right' : 'comment-wrong'}">
															<h3> ${comment.createdBy.name} <span> <fmt:formatDate value="${comment.createdDate}" pattern="E dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
																</span>
															</h3>
															<div class="${comment.approved ? 'approve-div' : 'reject-div'}">${comment.comment}</div>
														</div>
													</c:forEach>
												</c:if>
												<c:if test="${empty event.awardComment}">
													<div class="Comments_inner">
														<span>No Comments yet</span>
													</div>
												</c:if>
											</div>
										</div>
									</div>
								</c:if>
								</c:if>
								
							<c:if test="${!erpSetup.isErpEnable && !(eventPermissions.activeAwardApproval)}">
								<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
								<div class="row btn-group" style="margin-top: 10px;">

									<c:if test="${event.awardStatus ne 'PENDING'}">
										<button type="button" id="createPr" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out marg-right-10">
											<spring:message code="eventawards.create.pr.title" />
										</button>
									</c:if>
									
									<!-- create COntract -->
									<c:if test="${event.awardStatus ne 'PENDING'}">
										<button type="button" id="creatContract" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out marg-right-10">
											<spring:message code="eventawards.create.contract.title" />
										</button>
									</c:if>

									<!-- Evaluate Performance -->
									<c:if test="${event.status eq 'FINISHED' and eventPermissions.owner}">
										<button type="button" id="evaluatePerformance" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out marg-right-10">
											<spring:message code="eventawards.evaluate.performance" />
										</button>
									</c:if>

									<c:if test="${event.status ne 'FINISHED' and (event.awardStatus ne 'PENDING' and event.awardStatus ne 'APPROVED') and !(event.awarded)}">
										<button type="button" class=" btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out marg-right-10" id="saveAward">
											<spring:message code="application.save" />
										</button>
									</c:if>
									<c:if test="${event.status ne 'FINISHED' and !(event.awarded) and event.awardStatus != 'PENDING' and event.awardStatus != 'APPROVED'}">
										<button type="button" id="concludeEventAward" class=" btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out marg-right-10">
											<spring:message code="event.award.conclude" />
										</button>
									</c:if>

									<c:if test="${event.status ne 'FINISHED' and !(event.awarded) and event.awardStatus != 'PENDING' and event.awardStatus != 'APPROVED'}">
										<a class="btn  btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium button-previous marg-right-10" href="${pageContext.request.contextPath}/buyer/${eventType}/eventAward/${eventId}"> 
											<spring:message code="application.reset" />
										</a>
									</c:if>

								</div>
							</c:if>
							<c:if test="${erpSetup.isErpEnable && !(eventPermissions.activeAwardApproval)}">
								<div class="row btn-group" style="margin-top: 10px; margin-right: -15px; margin-left: -15px;">
									<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
									
									<c:if test="${event.awardStatus ne 'PENDING'}">
										<button type="button" id="createPr" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out marg-right-10">
											<spring:message code="eventawards.create.pr.title" />
										</button>
									</c:if>
									
									<!-- create COntract -->
									<c:if test="${event.awardStatus ne 'PENDING'}">
										<button type="button" id="creatContract" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out marg-right-10">
											<spring:message code="eventawards.create.contract.title" />
										</button>
									</c:if>
									
									<c:if test="${event.status eq 'FINISHED' and eventPermissions.owner}">
										<button type="button" id="evaluatePerformance" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out marg-right-10">
											<spring:message code="eventawards.evaluate.performance" />
										</button>
									</c:if>

									<c:if test="${event.status eq 'FINISHED' and not event.transfrAwrdRespFlag and erpSetup.erpIntegrationTypeForAward == 'TYPE_3'}">
										<button type="button" id="transferAward" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out marg-right-10">
											<spring:message code="eventawards.evaluate.award" />
										</button>
									</c:if>
									
									<c:if test="${event.status ne 'FINISHED' and (event.awardStatus ne 'PENDING' and event.awardStatus ne 'APPROVED') and !(event.awarded)}">
										<button type="button" class=" btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out marg-right-10" id="saveAward">
											<spring:message code="application.save" />
										</button>
									</c:if>

									<c:if test="${event.status ne 'FINISHED' and (event.awardStatus ne 'PENDING' and event.awardStatus ne 'APPROVED') and !(event.awarded)}">
										<button type="button" class=" btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out marg-right-10" id="concludeEventAward">
											<spring:message code="event.award.conclude" />
										</button>
									</c:if>
									<c:if test="${event.status eq 'FINISHED' and erpSetup.enableAwardErpPush and (erpSetup.erpIntegrationTypeForAward == 'TYPE_1' or (erpSetup.erpIntegrationTypeForAward == 'TYPE_2'and event.businessUnit.budgetCheck)) and !eventAward.transferred and (event.awarded) and event.awardStatus == 'APPROVED'}">
									<%-- <c:if test="${event.status eq 'FINISHED' and erpSetup.enableAwardErpPush and event.businessUnit.budgetCheck and !eventAward.transferred and (event.awarded) and event.awardStatus == 'APPROVED'}"> --%>
										<button type="button" id="saveOrTrasfer" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out marg-right-10">
											<spring:message code="application.transfer.btn" /> <spring:message code="event.archieve.award" />
										</button>
									</c:if>
									<c:if test="${event.status ne 'FINISHED' and !(event.awarded) and event.awardStatus != 'PENDING' and event.awardStatus != 'APPROVED'}">
										<a class="btn  btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium button-previous marg-right-10" href="${pageContext.request.contextPath}/buyer/${eventType}/eventAward/${eventId}"> 
											<spring:message code="application.reset" />
										</a>
									</c:if>
								</div>
							</c:if>
						</form:form>
					</c:if>
					<c:if test="${not empty event.awardApprovals && event.status eq 'COMPLETE'}">
						<jsp:include page="awardApproverScreen.jsp" />
					</c:if>
				</div>
				<c:if test="${empty eventSuppliers }">
					<div class="row marg-bottom-20" style="margin-top: 70px;">
						<div class="form-group">
							<div class="row">
								<div class="col-md-12">
									<div id="idGlobalWarn" class="alert alert-warning">
										<h3 align="center">
											<spring:message code="eventaward.no.qualified.found" />
										</h3>
									</div>
								</div>
							</div>
						</div>
					</div>
				</c:if>
			</div>
			<jsp:include page="eventAwardAudit.jsp" />
		</div>
	</div>
</div>
<div class="modal fade" id="myModal" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="eventaward.confirm.update" />
				</h3>
				<input type="hidden" id="selectedAwardId" name="selectedAwardId" value="">
				<button class="close for-absulate" onclick="javascript:doCancel();" type="button" data-dismiss="modal">×</button>
			</div>
			<div class="modal-body">
				<label><spring:message code="eventaward.item.already.exist" /></label>
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<button id="idConfirmUpdate" type="button" onclick="javascript:doUpdate();" class="btn btn-info pull-left ph_btn_small hvr-pop hvr-rectangle-out" data-dismiss="modal">
					<spring:message code="application.update" />
				</button>
				<button type="button" onclick="javascript:doCancel();" class="btn btn-black pull-right btn-default ph_btn_small hvr-pop hvr-rectangle-out1" data-dismiss="modal">
					<spring:message code="application.cancel" />
				</button>
			</div>
		</div>
	</div>
</div>
<div class=" modal flagvisibility dialogBox" id="pushPr" role="dialog">
	<div class="float-left width100 pad_all_15 white-bg pop-up-pr modal-content">
		<form class="bordered-row">
			<div class="row">
				<div class="ui-dialog-titlebar ui-widget-header ui-corner-all ui-helper-clearfix">
					<h3 id="ui-id-1" class="ui-dialog-title">Generate PR</h3>
					<button type="button" class="ui-dialog-titlebar-close closePd" data-dismiss="modal"></button>
				</div>
			</div>
			<div class="row form-group">
				<div class="col-sm-6 col-md-6">
					<label><spring:message code="eventawards.pr.creator" /></label>
				</div>
				<div class="col-sm-6 col-md-6">
					<div class="input_wrapper">
						<select class="chosen-select" name="selectPRCreator" required id="selectPRCreator">
							<option value="" selected disabled><spring:message code="award.pr.creator.placeholder" /></option>
							<c:forEach items="${prCreator}" var="item">
								<option value="${item.id}">${item.name}</option>
							</c:forEach>
						</select>
					</div>
				</div>
			</div>
			<div class="row form-group">
				<div class="col-sm-6 col-md-6">
					<label>PR Template</label>
				</div>
				<div class="col-sm-6 col-md-6">
					<div class="input_wrapper">
						<select class="chosen-select" name="selectPRTemplate" required id="selectPRTemplate" required>
							<option value="" selected disabled><spring:message code="award.pr.template.placeholder" /></option>
							<c:forEach items="${prTemplate}" var="item">
								<option value="${item.id}">${item.templateName}</option>
							</c:forEach>
						</select>
						<div style="font-style: italic;">*Decimal values will be rounded to the decimal setting of the PR template.</div>
					</div>
				</div>
			</div>
			<div class="row ">
				<div class="col-md-12">
					<div class="form-group align-center margin-tp-5">
						<button id="pushToPR" type="button" data-dismiss="modal" class="btn  marg-left-10 hvr-pop ph_btn  btn btn-success frm_bttn1 sub_frm">
							<spring:message code="eventawards.push.pr.button" />
						</button>
						<!-- <button type="submit" class=" hvr-pop sub_frm">Confirm & Pay</button> -->
					</div>
				</div>
			</div>
		</form>
	</div>
</div>



<div class=" modal flagvisibility dialogBox" id="ConfirmSaveOrTrasfer" role="dialog">
	<div class="float-left width100 pad_all_15 white-bg pop-up-pr modal-content">
		<form class="bordered-row">
			<div class="row">
				<div class="ui-dialog-titlebar ui-widget-header ui-corner-all ui-helper-clearfix">
					<h3 id="ui-id-1" class="ui-dialog-title">Please Confirm To Save Award</h3>
					<button type="button" class="ui-dialog-titlebar-close closePd" data-dismiss="modal"></button>
				</div>
			</div>

			<div class="row ">
				<div class="col-md-12">
					<div class="form-group align-center margin-tp-5">
						<button id="transferBtn" type="button" data-dismiss="modal" class="btn  marg-left-10 hvr-pop ph_btn  btn btn-success frm_bttn1 sub_frm">
							<spring:message code="application.transfer.btn" />
						</button>
						<!-- <button type="submit" class=" hvr-pop sub_frm">Confirm & Pay</button> -->
					</div>
				</div>
			</div>
		</form>
	</div>
</div>

<!-- create contract confirm -->
<div class=" modal flagvisibility dialogBox" id="dlgConfirmContract" role="dialog">
	<div class="float-left pad_all_15 white-bg pop-up-pr ">
		<form class="bordered-row" id="contractFormAward">
			<div class="row">
				<div class="ui-dialog-titlebar ui-widget-header ui-corner-all ui-helper-clearfix">
					<h3 id="ui-id-1" class="ui-dialog-title">Generate Contract</h3>
					<button type="button" class="ui-dialog-titlebar-close closePd" data-dismiss="modal"></button>
				</div>
			</div>

			<div class="row marg-top-10">
				<div class="col-md-5">
					<label cssClass="marg-top-10">
						<spring:message code="productlist.startDate.item" />
					<label>
				</div>

				<div class="col-md-5">
				<div class="input-prepend input-group">
						<spring:message code="dateformat.placeholder" var="dateformat" />
						<input id="contractStartDate" name="contractStartDate" data-validation="required" class="form-control for-clander-view contractStartDate" data-fv-date-min="15/10/2016" placeholder="${dateformat}" autocomplete="off" />
						<div id="contract_dateErrorBlock"></div>
					</div>
				</div>
			</div>

			<div class="row marg-top-10">
				<div class="col-md-5">
					<label cssClass="marg-top-10">
						<spring:message code="productlist.endDate.item" />
					<label>
				</div>
				<div class="col-md-5">
					<div class="input-prepend input-group">
						<spring:message code="dateformat.placeholder" var="dateformat" />
						<input id="contractEndDateId" name="contractEndDate" data-validation="required" class="bootstrap-datepicker form-control for-clander-view contractEndDate" data-fv-date-min="15/10/2016" placeholder="${dateformat}" autocomplete="off" />
						<div id="contract_dateErrorBlock"></div>
					</div>
				</div>
			</div>

			<div class="row marg-top-10">
				<div class="col-md-5">
					<label> <spring:message code="product.Contract.groupCode" />
					</label>
				</div>
				<div class="col-md-5">
					<spring:message code="contract.cost.center.empty" var="required" />
					<select name="groupCode" id="chosenGroupCode" class="chosen-select" data-validation="required">
						<option value="">
							<spring:message code="contract.select.group.Code" />
						</option>
						<c:forEach items="${groupCodeList}" var="groupCode">
							<option value="${groupCode.id}">${groupCode.groupCode}-${groupCode.description}</option>
						</c:forEach>
					</select>
				</div>
			</div>
			
			<div class="row marg-top-10">
				<div class="col-md-5">
					<label cssClass="marg-top-10">
						<spring:message code="product.Contract.code" />
					<label>
				</div>
				<div class="col-md-5">
					<spring:message code="application.referencenumber" var="contractReferenceNumber" />
					<spring:message code="contract.reference.number.required" var="required" />
					<spring:message code="contract.reference.number.length" var="length" />
					<spring:message code="systemsetting.product.code.placeholder" var="productcode" />
					<input type="text" data-validation-length="1-64" data-validation="required length" data-validation-error-msg-required="${required}" data-validation-error-msg-length="${length}" class="form-control " id="referenceNumber" placeholder="${contractReferenceNumber}" />
				</div>
			</div>
			
			<div class="row marg-top-10">
				<div class="col-md-5">
					<label cssClass="marg-top-10"><spring:message code="product.Contract.creator" /></label>
				</div>
				<div class="col-md-5">
					<div class="input_wrapper">
						<select class="chosen-select" id="contractCreator" class="chosen-select" data-validation="required">
							<option value="">
								<spring:message code="award.contract.creator.placeholder" />
							</option>
							<c:forEach items="${formOwner}" var="item">
								<option value="${item.id}">${item.name}</option>
							</c:forEach>
						</select>
					</div>
				</div>
			</div>

			<div class="row">
				<div class="col-md-12">
					<div class="form-group align-center margin-tp-5">
						<button id="btnCreateContract" type="button" class="btn  marg-left-10 hvr-pop ph_btn  btn btn-success frm_bttn1 sub_frm">
							<spring:message code="application.create" />
						</button>
						<!-- <button type="submit" class=" hvr-pop sub_frm">Confirm & Pay</button> -->
						<button type="button" data-dismiss="modal" class="btn  marg-left-10 hvr-pop ph_btn  btn btn-black frm_bttn1 sub_frm">
							<spring:message code="application.cancel" />
						</button>
					</div>
				</div>
			</div>
		</form>
	</div>
</div>

<!-- Confirm Performance Evaluation -->
<div class=" modal flagvisibility dialogBox" id="confirmPerformanceEvaluation" role="dialog">
	<div class="float-left width100 pad_all_15 white-bg pop-up-pr">
		<form class="bordered-row">
			<div class="row">
				<div class="ui-dialog-titlebar ui-widget-header ui-corner-all ui-helper-clearfix">
					<h3 id="ui-id-1" class="ui-dialog-title">Generate Supplier Performance Evaluation</h3>
					<button type="button" class="ui-dialog-titlebar-close closePd" data-dismiss="modal"></button>
				</div>
			</div>
			<div class="row form-group">
				<div class="col-sm-6 col-md-6">
					<label><spring:message code="eventawards.form.owner" /></label>
				</div>
				<div class="col-sm-6 col-md-6">
					<div class="input_wrapper">
						<select class="chosen-select" name="selectFormOwner" required id="selectFormOwner">
							<c:forEach items="${formOwner}" var="item">
								<c:if test="${item.id == event.eventOwner.id}">
									<option value="${item.id}" selected>${item.name}</option>
								</c:if>
								<c:if test="${item.id != event.eventOwner.id}">
									<option value="${item.id}">${item.name}</option>
								</c:if>
							</c:forEach>
						</select>
					</div>
				</div>
			</div>
			<div class="row form-group">
				<div class="col-sm-6 col-md-6">
					<label>SP Template</label>
				</div>
				<div class="col-sm-6 col-md-6">
					<div class="input_wrapper">
						<select class="chosen-select" name="selectFormTemplate" required id="selectFormTemplate" required>
							<option value="" selected disabled><spring:message code="award.sp.template.placeholder" /></option>
						</select>
					</div>
				</div>
			</div>
			<div class="row ">
				<div class="col-md-12">
					<div class="form-group align-center margin-tp-5">
						<button id="pushToEvaluation" type="button" data-dismiss="modal" class="btn  marg-left-10 hvr-pop ph_btn  btn btn-success frm_bttn1 sub_frm">
							<spring:message code="eventawards.push.evaluation.button" />
						</button>
					</div>
				</div>
			</div>
		</form>
	</div>
</div>

<div class="modal fade" id="ConfirmSaveModal" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="conclude.Award.confirmation" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<div class="col-md-12">
				<div class="row">
					<div class="modal-body col-md-12">
						<label> <spring:message code="conclude.dialog.text" />
						</label>
					</div>
				</div>
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<button type="button" id="saveConfirm" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left">
					<spring:message code="application.yes" />
				</button>
				<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
					<spring:message code="application.no" />
				</button>
			</div>
		</div>
	</div>
</div>



<div class="modal fade" id="ConfirmTransferModal" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="eventawards.evaluate.award" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<div class="col-md-12">
				<div class="row">
					<div class="modal-body col-md-12">
						<label> <spring:message code="eventawards.dialog.text" />
						</label>
					</div>
				</div>
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<button type="button" id="transferAwardConfirm" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left">
					<spring:message code="application.yes" />
				</button>
				<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
					<spring:message code="application.no" />
				</button>
			</div>
		</div>
	</div>
</div>

	<!--  EDIT APPROVAL POPUP -->
<c:if test="${not empty event.awardApprovals}">	
<div class="flagvisibility dialogBox" id="editAwardApprovalPopup" title="Edit Approval">
	<div class="float-left width100 pad_all_15 white-bg">
		<form:form id="awardApprovalForm" action="${pageContext.request.contextPath}/buyer/${eventType}/updateEventAwardApproval?${_csrf.parameterName}=${_csrf.token}" method="post" modelAttribute="eventAward">
			<form:hidden path="id" />
			<input type="hidden" name="eventId" class="eventId" value="${event.id}" />
			<jsp:include page="eventAwardApproval.jsp"></jsp:include>
			<div class="row">
				<div class="col-md-12">
					<div class="form-group align-center marg-top-20">
						<button type="reset" id="resetApprovalForm" style="display: none;"></button>
						<button type="button" id="updateApproval" class="btn btn-info ph_btn btn-tooltip hvr-pop hvr-rectangle-out">
							<spring:message code="application.update" />
						</button>
						<button type="button" id="cancelApprovalForm" class="closeDialog closeEditPopUp btn btn-black marg-left-10 hvr-pop ph_btn hvr-rectangle-out1">
							<spring:message code="application.cancel" />
						</button>
					</div>
				</div>
			</div>
		</form:form>
	</div>
</div>
</c:if>



<script type="text/javascript">

<c:if test="${eventPermissions.editor }">
$(window).bind('load', function() {
	var allowedFields = '#idOngoingSumTab,#idOngoingMessageTab';
	disableFormFields(allowedFields);
 });
</c:if> 

	var decimalLimit = $('#decimal').val();
	$(document).ready( function() {
		
		$('#updateApproval').on('click', function() {
			if($('#awardApprovalForm').isValid()) {
				// If Award Approval is enabled but no levels have been configured then show error
				if($('#enableAwardApproval').is(':checked') && $('.awardpApprvl').length == 0) {
					$('#idApprovalLevelError').show();
					return false;
				}
				
				$('#loading').show();
				$('#awardApprovalForm').submit()
			}
		});
		
		
		var totalwithTax = parseFloat(0);
		var grandTotalVal = 0;
		$('.totalwithTax').each(function() {
			var priceType = $(this).closest('tr').find('[name="priceType"]').val();

			if (priceType == 'TRADE_IN_PRICE') {
				grandTotalVal = (parseFloat(grandTotalVal) - parseFloat($(this).val().replace(/,/g, ''))).toFixed(decimalLimit);
			} else {
				grandTotalVal = (parseFloat(grandTotalVal) + parseFloat($(this).val().replace(/,/g, ''))).toFixed(decimalLimit);
			}
			totalwithTax = grandTotalVal;
			//totalwithTax += parseFloat($(this).val());
		});

		totalwithTax = ReplaceNumberWithCommas(parseFloat(totalwithTax).toFixed(decimalLimit));
		$('#totalAfterTax').html(totalwithTax);
		$('#grandTotal').val(totalwithTax);
		//---------------------------------------------------------------------------//

		var awardPriceSum = parseFloat(0);
		var grandTotalVal = 0;
		$('.awardPrice').each(function() {
			var priceType = $(this).closest('tr').find('[name="priceType"]').val();

			if (priceType == 'TRADE_IN_PRICE') {
				grandTotalVal = (parseFloat(grandTotalVal.toString().replace(/,/g, "").match(new RegExp("^-?\\d+\\.?\\d{0," + decimalLimit + "}"))) - parseFloat($(this).val().replace(/,/g, ''))).toFixed(decimalLimit);
			} else {
				grandTotalVal = (parseFloat(grandTotalVal.toString().replace(/,/g, "").match(new RegExp("^-?\\d+\\.?\\d{0," + decimalLimit + "}"))) + parseFloat($(this).val().replace(/,/g, ''))).toFixed(decimalLimit);
			}
			awardPriceSum = grandTotalVal;
			//awardPriceSum += parseFloat($(this).val().replace(/\,|\s|\#/g, ''));
			//console.log(parseFloat($(this).val().replace(/\,|\s|\#/g, '')));GeneralEnquiryServiceImpl
		});

		awardPriceSum = ReplaceNumberWithCommas(parseFloat(awardPriceSum).toFixed(decimalLimit));

		var grandTotalVal = 0;
		var originalPriceSum = parseFloat(0);
		$('.originalPrice').each(function() {
			var priceType = $(this).closest('tr').find('[name="priceType"]').val();

			if (priceType == 'TRADE_IN_PRICE') {
				grandTotalVal = (parseFloat(grandTotalVal.toString().replace(/,/g, "").match(new RegExp("^-?\\d+\\.?\\d{0," + decimalLimit + "}"))) - parseFloat($(this).val().replace(/,/g, ''))).toFixed(decimalLimit);
			} else {
				grandTotalVal = (parseFloat(grandTotalVal.toString().replace(/,/g, "").match(new RegExp("^-?\\d+\\.?\\d{0," + decimalLimit + "}"))) + parseFloat($(this).val().replace(/,/g, ''))).toFixed(decimalLimit);
			}
			originalPriceSum = grandTotalVal;
			//originalPriceSum += parseFloat($(this).val());
		});

		originalPriceSum = ReplaceNumberWithCommas(parseFloat(originalPriceSum).toFixed(decimalLimit));

		$("#totalSupplierPrice").val(originalPriceSum);
		$("#originalPriceSum").text(originalPriceSum);

		$("#totalAwardPrice").val(awardPriceSum);
		$("#awardPriceSum").text(awardPriceSum);

		$(document).delegate('.awardPrice', 'change', function(e) {
			$('#loading').show();
			e.preventDefault();
			decimalLimit = $('#decimal').val();

			this.value = this.value.replace(/\,|\s|\#/g, '').match(new RegExp("^\\d+\\.?\\d{0," + decimalLimit + "}"));

			var amt = parseFloat(0);
			amt = parseFloat(this.value);

			var type = $(this).data('taxtype');

			var rate = parseFloat($(this).attr('data-rate').replace(/\,|\s|\#/g, '').match(new RegExp("^\\d+\\.?\\d{0," + decimalLimit + "}")));

			var changeto = $(this).data('changeto');

			if (type == 'Percent') {
				var add = parseFloat((amt * rate / 100).toString().replace(/\,|\s|\#/g, '').match(new RegExp("^\\d+\\.?\\d{0," + decimalLimit + "}")));
				amt = (parseFloat(amt.toString().replace(/\,|\s|\#/g, '').match(new RegExp("^\\d+\\.?\\d{0," + decimalLimit + "}"))) + parseFloat(add.toString().replace(/\,|\s|\#/g, '').match(
						new RegExp("^\\d+\\.?\\d{0," + decimalLimit + "}")))).toFixed(decimalLimit);
			} else if (type == 'Amount') {
				var add = amt;
				amt = (parseFloat(rate.toString().replace(/\,|\s|\#/g, '').match(new RegExp("^\\d+\\.?\\d{0," + decimalLimit + "}"))) + parseFloat(amt.toString().replace(/\,|\s|\#/g, '').match(
						new RegExp("^\\d+\\.?\\d{0," + decimalLimit + "}")))).toFixed(decimalLimit);
			}
			if (isNaN(amt)) {
				amt = parseFloat(ReplaceNumberWithCommas((0).toFixed(decimalLimit)));
			}
			$('#rfxawhid-' + changeto).val(amt);
			$('#rfxawdsp-' + changeto).html(ReplaceNumberWithCommas(parseFloat(amt.toString().replace(/\,|\s|\#/g, '').match(new RegExp("^\\d+\\.?\\d{0," + decimalLimit + "}"))).toFixed(decimalLimit)));

			var totalwithTax = parseFloat(0);
			var grandTotalVal = 0;
			$('.totalwithTax').each(function() {
				var priceType = $(this).closest('tr').find('[name="priceType"]').val();
				if (priceType == 'TRADE_IN_PRICE') {
					grandTotalVal = (parseFloat(grandTotalVal.toString().replace(/\,|\s|\#/g, '').match(new RegExp("^-?\\d+\\.?\\d{0," + decimalLimit + "}"))) - parseFloat($(this).val().replace(/,/g, ''))).toFixed(decimalLimit);
				} else {
					grandTotalVal = (parseFloat(grandTotalVal.toString().replace(/\,|\s|\#/g, '').match(new RegExp("^-?\\d+\\.?\\d{0," + decimalLimit + "}"))) + parseFloat($(this).val().replace(/,/g, ''))).toFixed(decimalLimit);
				}
				totalwithTax = grandTotalVal;
				//totalwithTax += parseFloat($(this).val());
			});

			totalwithTax = ReplaceNumberWithCommas(parseFloat(totalwithTax.toString().replace(/\,|\s|\#/g, '').match(new RegExp("^\\d+\\.?\\d{0," + decimalLimit + "}"))).toFixed(decimalLimit));

			$('#totalAfterTax').html(totalwithTax);
			$('#grandTotal').val(totalwithTax);
			var decimalLimit = $('#decimal').val();
			var awardPriceSum = parseFloat(0);
			var grandTotalVal = 0;
			$('.awardPrice').each(function() {

				var priceType = $(this).closest('tr').find('[name="priceType"]').val();

				if (priceType == 'TRADE_IN_PRICE') {
					grandTotalVal = (parseFloat(grandTotalVal.toString().replace(/\,|\s|\#/g, '').match(new RegExp("^-?\\d+\\.?\\d{0," + decimalLimit + "}"))) - parseFloat($(this).val().replace(/,/g, ''))).toFixed(decimalLimit);
				} else {
					grandTotalVal = (parseFloat(grandTotalVal.toString().replace(/\,|\s|\#/g, '').match(new RegExp("^-?\\d+\\.?\\d{0," + decimalLimit + "}"))) + parseFloat($(this).val().replace(/,/g, ''))).toFixed(decimalLimit);
				}

				if ($(this).val().length != 0 && !isNaN($(this).val().replace(/\,|\s|\#/g, ''))) {
					awardPriceSum = grandTotalVal;
					//awardPriceSum += parseFloat($(this).val().replace(/\,|\s|\#/g, ''));
				}

			});
			this.value = ReplaceNumberWithCommas(parseFloat(this.value.replace(/\,|\s|\#/g, '').match(new RegExp("^\\d+\\.?\\d{0," + decimalLimit + "}"))).toFixed(decimalLimit));
			$("#awardPriceSum").html(ReplaceNumberWithCommas((parseFloat(awardPriceSum.toString().replace(/\,|\s|\#/g, '').match(new RegExp("^\\d+\\.?\\d{0," + decimalLimit + "}"))).toFixed(decimalLimit))));
			$("#totalAwardPrice").val(ReplaceNumberWithCommas((parseFloat(awardPriceSum.toString().replace(/\,|\s|\#/g, '').match(new RegExp("^\\d+\\.?\\d{0," + decimalLimit + "}"))).toFixed(decimalLimit))));
			$('#loading').hide();
		});

		$('.custom-itemCheck').on('change', function() {
			var currentBlock = $(this);
			var pid = currentBlock.attr('data-pid');

			if ($(this).prop("checked")) {
				$('#loading').show();
				var itemAndSupplierId = $(this).attr('data-pid');

				$.ajax({
					type : "POST",
					url : getContextPath() + '/buyer/${eventType}/checkProductItemExistOrNot',
					data : {
						'itemAndSupplierId' : itemAndSupplierId

					},
					success : function(data) {
						$("#myModal").find('#selectedAwardId').val(pid);
						$('#myModal').modal('show');
						$('#loading').hide();
					},
					error : function(data) {
						$('#loading').hide();
					}
				});
			}
		});

		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		$('#awardCriteria').change(function(e) {
			e.preventDefault();
			$('#loading').show();
			$('#eventAwardCriteriaForm').attr('action', getContextPath() + "/buyer/" + getEventType() + "/criteria/" + getEventType());
			$('#eventAwardCriteriaForm').submit();

		});

	});

	function manualSelection(val, itemId, row) {
		var supplierId = val;
		var bqItem = itemId;
		var eventId = $('.rfxEventId').val();
		var eventType = getEventType();
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		var changeto = row;
		var decimalLimit = $('#decimal').val();
		//alert(changeto);
		$.ajax({
			type : "POST",
			url : getContextPath() + "/buyer/" + getEventType() + "/getSupplierData/" + getEventType(),
			data : {
				'supplierId' : supplierId,
				'bqItemId' : bqItem,
				'eventId' : eventId
			},
			dataType : "json",
			beforeSend : function(xhr) {
				$('#loading').show();
				xhr.setRequestHeader(header, token);
			},
			complete : function() {
				$('#loading').hide();
			},
			success : function(data) {
				//id = "awardPricehid-${count.index}"
				$('#originalPricehid-' + changeto).val(data.totalAmount);
				$('#originalPrice-' + changeto).html(ReplaceNumberWithCommas((data.totalAmount).toFixed(decimalLimit)));
				$('#awardPricehid-' + changeto).val(ReplaceNumberWithCommas((data.totalAmount.toFixed(decimalLimit))));
				$('#awardPrice-' + changeto).html(ReplaceNumberWithCommas((data.totalAmount).toFixed(decimalLimit)));
				var dataPid = itemId + "-" + supplierId;
				//var  selected=$('#selectItem').html(dataPid);
				$('#selectItem').html(dataPid);
				$('#selectItem').val(dataPid);
				$('.custom-itemCheck').val(dataPid);
				$('.custom-itemCheck').attr('data-pid', dataPid);
				idValue = "selectItem-" + dataPid;

				var categoryId = "category-" + itemId;

				$('.custom-itemCheck').attr('id', idValue);

				var productCategoryList = '';
				$.each(data.productCategoryList, function(i, obj) {
					productCategoryList += '<option value="' + obj.id + '">' + obj.productCode +' - '+obj.productName + '</option>';
				});
				$('#' + categoryId).html(productCategoryList);
				$('#' + categoryId).trigger("chosen:updated");

				if (data.tax !== undefined && data.tax !== '') {
					$('#taxTypehid-' + changeto).val(data.taxType);
					$('#taxType-' + changeto).html(data.taxType);
					$('#taxhid-' + changeto).val(data.tax);
					$('#tax-' + changeto).html(ReplaceNumberWithCommas((data.tax).toFixed(decimalLimit)));
					$('#awardPricehid-' + changeto).attr('data-rate', data.tax);
					$('#awardPricehid-' + changeto).attr('data-taxtype', data.taxType);
				} else {
					$('#taxTypehid-' + changeto).val("");
					$('#taxType-' + changeto).html("");
					$('#taxhid-' + changeto).val("");
					$('#tax-' + changeto).html("");
					$('#awardPricehid-' + changeto).attr('data-rate', '0');
					$('#awardPricehid-' + changeto).attr('data-taxtype', '');
				}
				$('#rfxawhid-' + changeto).val(data.totalAmountWithTax);
				$('#rfxawdsp-' + changeto).html(ReplaceNumberWithCommas((data.totalAmountWithTax).toFixed(decimalLimit)));

				var grandTotalVal = 0;
				var awardPriceSum = parseFloat(0);
				$('.awardPrice').each(function() {
					var priceType = $(this).closest('tr').find('[name="priceType"]').val();

					if (priceType == 'TRADE_IN_PRICE') {
						grandTotalVal = (parseFloat(grandTotalVal) - parseFloat($(this).val().replace(/,/g, ''))).toFixed(decimalLimit);
					} else {
						grandTotalVal = (parseFloat(grandTotalVal) + parseFloat($(this).val().replace(/,/g, ''))).toFixed(decimalLimit);
					}
					awardPriceSum = grandTotalVal;
					//awardPriceSum += parseFloat($(this).val().replace(/\,|\s|\#/g, ''));
				});

				awardPriceSum = ReplaceNumberWithCommas(parseFloat(awardPriceSum).toFixed(decimalLimit));
				var grandTotalVal = 0;
				var originalPriceSum = parseFloat(0);
				$('.originalPrice').each(function() {
					var priceType = $(this).closest('tr').find('[name="priceType"]').val();

					if (priceType == 'TRADE_IN_PRICE') {
						grandTotalVal = (parseFloat(grandTotalVal) - parseFloat($(this).val().replace(/,/g, ''))).toFixed(decimalLimit);
					} else {
						grandTotalVal = (parseFloat(grandTotalVal) + parseFloat($(this).val().replace(/,/g, ''))).toFixed(decimalLimit);
					}
					originalPriceSum = grandTotalVal;

					//originalPriceSum += parseFloat($(this).val());
				});

				originalPriceSum = ReplaceNumberWithCommas(parseFloat(originalPriceSum).toFixed(decimalLimit));

				$("#totalSupplierPrice").val(originalPriceSum);
				$("#originalPriceSum").text(originalPriceSum);

				$("#totalAwardPrice").val(awardPriceSum);
				$("#awardPriceSum").text(awardPriceSum);
				var totalwithTax = parseFloat(0);
				var grandTotalVal = 0;
				$('.totalwithTax').each(function() {
					var priceType = $(this).closest('tr').find('[name="priceType"]').val();

					if (priceType == 'TRADE_IN_PRICE') {
						grandTotalVal = (parseFloat(grandTotalVal) - parseFloat($(this).val().replace(/,/g, ''))).toFixed(decimalLimit);
					} else {
						grandTotalVal = (parseFloat(grandTotalVal) + parseFloat($(this).val().replace(/,/g, ''))).toFixed(decimalLimit);
					}
					totalwithTax = grandTotalVal;
					/* totalwithTax += parseFloat($(this).val()); */

				});
				totalwithTax = ReplaceNumberWithCommas(parseFloat(totalwithTax).toFixed(decimalLimit));
				$('#totalAfterTax').html(totalwithTax);
				$('#grandTotal').val(totalwithTax);
			},
			error : function(request, textStatus, errorThrown) {
				console.log(" Error : " + request.getResponseHeader('error'));
			}
		});
	}

	function ReplaceNumberWithCommas(yourNumber) {
		// Seperates the components of the number
		var n = yourNumber.toString().split(".");
		// Comma-fies the first part
		n[0] = n[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
		// Combines the two sections
		return n.join(".");
	}
	function validateform() {
		//alert();
	}

	$('#concludeEventAward').click(function(e) {
		$('.costCenter').attr('data-validation-optional', "true");
		$('.businessUnit').attr('data-validation-optional', "true");
		$('.form-error').remove();
		if($('#eventAwardForm').isValid()) {
			
			// If Award Approval is enabled but no levels have been configured then show error
			if($('#enableAwardApproval').is(':checked') && $('.awardpApprvl').length == 0) {
				$('#idApprovalLevelError').show();
				return false;
			}
			
			$('#ConfirmSaveModal').modal('show');
		}
		$('.error-range.text-danger').remove();
		$('#loading').hide();
	});

	$('#saveConfirm').click(function(e) {
		console.log('Going.....');
		$('#eventAwardForm').attr('action', getContextPath() + "/buyer/" + getEventType() + "/concludeEventAward?${_csrf.parameterName}=${_csrf.token}");
		if($('#eventAwardForm').isValid()) {
			$('#loading').show();
			$('#eventAwardForm').submit();
		}
	});
	$('#saveOrTrasfer').click(function(e) {
		$('.costCenter').attr('data-validation-optional', "true");
		$('.businessUnit').attr('data-validation-optional', "true");
		$('.form-error').remove();
		$('#ConfirmSaveOrTrasfer').modal('show');
		$('.error-range.text-danger').remove();
		$('#loading').hide();
	});

	$('#transferBtn').click(function(e) {
		$('#eventAwardForm').attr('action', getContextPath() + "/buyer/" + getEventType() + "/transferAward/${event.id}/${suppBqId}/${eventAward.id}?${_csrf.parameterName}=${_csrf.token}");
		$('#eventAwardForm').submit();

	});

	$('#transferAwardConfirm').click(function(e) {
		postToTransferAward();
	});

	$('#pushToPR').click(function(e) {
		$('.costCenter').attr('data-validation-optional', "true");
		$('.businessUnit').attr('data-validation-optional', "true");

		//$('#eventAwardForm').validate();
		var prTemplate = $("select[name='selectPRTemplate']").val();
		var prCreator = $("select[name='selectPRCreator']").val();
		if (prCreator == null) {
			$('#selectPRCreator').after('<p class="error-pos error-range text-danger">Please Select PR Creator</p>');
			return false;
		}
		if (prTemplate == null) {
			$('#selectPRTemplate').after('<p class="error-pos error-range text-danger">Please Select PR Template</p>');
			return false;
		}
		$('#eventAwardForm').attr('action', getContextPath() + "/buyer/" + getEventType() + "/transferEventAwardToPr/" + prTemplate + "/" + prCreator + "/${event.id}?${_csrf.parameterName}=${_csrf.token}");
		$('#eventAwardForm').submit();

	});
	$('#selectPRCreator').on('change', function() {
		$('.error-range.text-danger').remove();
	});

	$('#selectPRTemplate').on('change', function() {
		$('.error-range.text-danger').remove();
	});

	function doUpdate() {
		var awardPid = $("#myModal").find('#selectedAwardId').val();
		$('.selectCategory').prop("disabled", true);
	}

	function doCancel() {
		var awardPid = $("#myModal").find('#selectedAwardId').val();
		$('#selectItem-' + awardPid).prop('checked', false);
	}
	$("#createPr").click(function(e) {
		$('.costCenter').attr('data-validation-optional', "true");
		$('.businessUnit').attr('data-validation-optional', "true");
		$('.form-error').remove();

		$('#pushPr').modal('show');
		$('.error-range.text-danger').remove();
		$('#loading').hide();
	});

	$("#saveAward").click(function(e) {
		$('.costCenter').attr('data-validation-optional', "true");
		$('.businessUnit').attr('data-validation-optional', "true");
		$('.form-error').remove();
		
		if($('#eventAwardForm').isValid()) {
			// If Award Approval is enabled but no levels have been configured then show error
			if($('#enableAwardApproval').is(':checked') && $('.awardpApprvl').length == 0) {
				$('#idApprovalLevelError').show();
				return false;
			}
			$('#loading').show();
			$('#eventAwardForm').submit()
		}
	});

	$("#creatContract").click(function(e) {
		$('.productCategory').attr('data-validation');
		$('.costCenter').removeAttr('data-validation-optional');
		$('.businessUnit').removeAttr('data-validation-optional');
		$('#awardRemarks').removeAttr('data-validation');
		
		
		if($("#eventAwardForm").isValid()){
			$('#dlgConfirmContract').modal('show');
			$('.error-range.text-danger').remove();
			$('#loading').hide();
		}
	});
	
	$('#btnCreateContract').click(function(e) {
		if($("#contractFormAward").isValid()){
		var groupCodeId = $("select[name='groupCode']").val();
		var contractStartDate = $("#contractStartDate").val();
		var contractEndDate = $("#contractEndDateId").val();
		var referenceNumber = $("#referenceNumber").val();
		var contractCreator = $("#contractCreator").val();
			
		$("#eventAwardForm").find('.startDate').val(contractStartDate);
		$("#eventAwardForm").find('.endDate').val(contractEndDate);
		$("#eventAwardForm").find('.groupCode').val(groupCodeId);
		$("#eventAwardForm").find('.referenceNumber').val(referenceNumber);
		$("#eventAwardForm").find('.contractCreator').val(contractCreator);
		
		$('#eventAwardForm').attr('action', getContextPath() + "/buyer/" + getEventType() + "/createContract/${event.id}?${_csrf.parameterName}=${_csrf.token}");
		$('#eventAwardForm').submit();
		}
	});
	
	$("#evaluatePerformance").click(function(e) {
		$('.costCenter').attr('data-validation-optional', "true");
		$('.businessUnit').attr('data-validation-optional', "true");
		$('#awardRemarks').removeAttr('data-validation');
		$('.form-error').remove();

		
		var ownerId = '${event.eventOwner.id}';
		getSupplierPerformanceTemplates(ownerId);
		
		$('#confirmPerformanceEvaluation').modal('show');
		$('.error-range.text-danger').remove();
		$('#loading').hide();
	});


	$("#transferAward").click(function (e) {
		$('#ConfirmTransferModal').modal('show');
		// postToTransferAward();
	});

	function getSupplierPerformanceTemplates(userId) {
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		var ajaxUrl = getContextPath() +  '/buyer/getAssignedSpTemplateForUser';
		$.ajax({
			url: ajaxUrl,
			data: {
				'userId' : userId
			},
			type: 'GET',
			dataType: 'json',
			beforeSend: function(xhr) {
				xhr.setRequestHeader(header, token);
				$('#loading').show();
			},
			success: function(data, textStatus, request) {
				$('#selectFormTemplate').find('option:not(:first)').remove();
				var html = '';
				if (data != '' && data != null && data.length > 0) {
					$.each(data, function(key, value) {
						if (value.id == null || value.id == '') {
							html += '<option value="" disabled>' + value.templateName +'</option>';
						}else{
							html += '<option value="' + value.id + '">' + value.templateName +'</option>';
						}
					});
				}
				$('#selectFormTemplate').append(html);
				$('#selectFormTemplate').trigger("chosen:updated");
			},
			error: function(request, textStatus, errorThrown) {
				console.log(request);
			},
			complete: function() {
				$('#loading').hide();
			}
		});
	}

	function deleteAwardData(eventAwardId) {
		var ajaxUrl = getContextPath() +  '/buyer/'+getEventType()+'/deleteAwardAttachFile/'+eventAwardId;
		$('#loading').show();

		$.ajax({
			type : "GET",
			url: ajaxUrl,
			success : function() {
				$('#loading').hide();
				$('#deleteAwardData').hide();
				window.location.href = getContextPath() + '/buyer/' + getEventType() + '/eventAward/${event.id}';
			},
			error : function(request, textStatus, errorThrown) {
			}
		})
	}

	function postToTransferAward() {
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		var ajaxUrl = getContextPath() +  '/buyer/'+getEventType()+'/transferRfxAwardToErp/${event.id}/${eventAward.id}';
		$('#loading').show();
		$.ajax({
			type : "POST",
			url: ajaxUrl,
			success : function(data, textStatus, request) {
				var info = request.getResponseHeader('success');
				if(info == null) {
					var info = request.getResponseHeader('error');
					$('p[id=idGlobalErrorMessage]').html(info);
					$('div[id=idGlobalError]').show();
					$.jGrowl(info, {
						sticky : false,
						position : 'top-right',
						theme : 'bg-red'
					});
				}
				else {
					$('p[id=idGlobalSuccessMessage]').html(info);
					$('div[id=idGlobalSuccess]').show();
					$("#transferAward").hide();
					$.jGrowl(info, {
						sticky : false,
						position : 'top-right',
						theme : 'bg-green'
					});
					// location.reload();
				}

				$('#loading').hide();
				$('#ConfirmTransferModal').modal('hide');
			},
			error : function(data) {
				$('div[id=idGlobalError]').show();
				$('#loading').hide();
			}
		});
	}
	
	$('#selectFormOwner').on('change', function() {
		console.log('Owner CHanged : ', $(this).val());
		getSupplierPerformanceTemplates($(this).val());
	});
	
	$('#pushToEvaluation').click(function(e) {
		$('.costCenter').attr('data-validation-optional', "true");
		$('.businessUnit').attr('data-validation-optional', "true");

		//$('#eventAwardForm').validate();
		var spTemplate = $("select[name='selectFormTemplate']").val();
		var formOwner = $("select[name='selectFormOwner']").val();
		if (formOwner == null) {
			$('#selectFormOwner').after('<p class="error-pos error-range text-danger">Please Select Form Owner</p>');
			return false;
		}
		if (spTemplate == null) {
			$('#selectFormTemplate').after('<p class="error-pos error-range text-danger">Please Select SP Template</p>');
			return false;
		}
		$('#eventAwardForm').attr('action', getContextPath() + "/buyer/" + getEventType() + "/pushToEvaluation/" + spTemplate + "/" + formOwner + "/${event.id}?${_csrf.parameterName}=${_csrf.token}");
		$('#eventAwardForm').submit();

	});
	
	$.formUtils.addValidator({
		name : 'validate_custom_length',
		validatorFunction : function(value, $el, config, language, $form) {
			var val = value.split(".");
			if (val[0].replace(/,/g, '').length > 14) {
				return false;
			} else {
				return true;
			}
		},
		errorMessage : 'The input value is longer than 14 characters',
		errorMessageKey : 'validateLengthCustom'
	});
	
	
    $("#eventAwardForm").submit(function () {
        if ($(this).isValid()) {  
            $("#loading").show();
        }else{
        	$("#loading").hide();
        }
    });
    
</script>
</div>

<script type="text/javascript" src="<c:url value="/resources/js/numeral.min.js"/>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/assets/widgets/file-input/file-input.js"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/datepicker/datepicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/timepicker/timepicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/moment.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker-demo.js"/>"></script>

<script>
	$.validate({
		lang : 'en',
		onfocusout : false,
		validateOnBlur : true,
		modules : 'date,sanitize,file'
	});
</script>

<script>

$(function() {
	"use strict";
	var nowTemp = new Date();
	var now = new Date(nowTemp.getFullYear(), nowTemp.getMonth(), nowTemp.getDate() + 1, 0, 0, 0, 0);
	// console.log(now);
	$('.bootstrap-datepicker').bsdatepicker({
		format: 'dd/mm/yyyy',
		onRender: function(date) {
			return date.valueOf() < now.valueOf() ? 'disabled' : '';
		}
	}).on('changeDate', function(e) {
		$(this).blur();
		$(this).bsdatepicker('hide');
	});

	$('.contractStartDate').bsdatepicker({
		format: 'dd/mm/yyyy',
	}).on('changeDate', function(e) {
		$(this).blur();
		$(this).bsdatepicker('hide');
	});
});

$( ".businessUnit" ).change(function(e) {
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");
	var index = $(this).attr('data-index');
	  
	  var businessUnitId = $(this).val();
	  $.ajax({
			url: getBuyerContextPath('getCostCenterByBusinessUnit'),
			data: {
				'businessUnitId' :businessUnitId
			},
			type: 'GET',
			dataType: 'json',
			beforeSend: function(xhr) {
				xhr.setRequestHeader(header, token);
				$('#loading').show();
			},
			success: function(data, textStatus, request) {
				var html = '';
				var costCenterId = '#costCenterId' + index;
				if (data != '' && data != null && data.length > 0) {
					$(costCenterId).find('option:not(:first)').remove();
					$.each(data, function(key, value) {
						if (value.id == null || value.id == '') {
							html += '<option value="" disabled>' + value.costCenter + '-' + value.description +'</option>';
						}else{
							html += '<option value="' + value.id + '">' + value.costCenter + '-' + value.description +'</option>';
						}
					});
				}
				$(costCenterId).append(html);
				$(costCenterId).trigger("chosen:updated");			
			},

			error: function(request, textStatus, errorThrown) {
				console.log(request);
			},
			complete: function() {
				$('#loading').hide();
			}
		});
});

$(document).delegate('.editAwardApprovalPopupButton', 'click', function(e) {
	console.log( " Going ......");
	e.preventDefault();
	$('div[id=idGlobalError]').hide();
	$('div[id=idGlobalSuccess]').hide();
	$("#editAwardApprovalPopup").dialog({
		modal : true,
		minWidth : 300,
		width : '800px',
		maxWidth : 600,
		minHeight : 200,
		dialogClass : "",
		show : "fadeIn",
		draggable : false,
		dialogClass : "dialogBlockLoaded"
	});
	$('.ui-widget-overlay').addClass('bg-white opacity-60');
});

$(document).delegate('.productCategory','change',function(e) {
	console.log(" *********** ");
	 $('.productCategory').parent().removeClass('has-error').find('.form-error').remove();
});
</script>





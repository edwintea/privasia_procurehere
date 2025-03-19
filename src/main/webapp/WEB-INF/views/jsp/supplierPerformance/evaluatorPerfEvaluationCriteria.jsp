<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<div class="panel sum-accord">
	<div class="panel-heading">
		<h4 class="panel-title">
			<a data-toggle="collapse" class="accordion" href="#collapsePerEval"> <spring:message code="sp.evaluation.title" /> </a>
		</h4>
	</div>
	<div id="collapsePerEval" class="panel-collapse collapse in">
		<div class="panel-body pad_all_15">
			<div class="form-tander1 requisition-summary-box">
				<div class="col-sm-4 col-md-3 col-xs-6" style="padding-left: 0px;">
					<label> <spring:message code="supplier.performance.form.supplier" />: ${spForm.awardedSupplier.companyName}</label>
				</div>
			</div>

			<div class="Invited-Supplier-List import-supplier white-bg marg-bottom-20">
				<div class="import-supplier-inner-first-new global-list form-middle">
					<div class="ph_tabel_wrapper">
						<div class="main_table_wrapper ph_table_border mega" style="margin: 0;">
							<table class="ph_table border-none display table table-bordered " width="100%" border="0" cellspacing="0" cellpadding="0" style="top: 0px;">
								<thead>
									<tr>
										<th class="for-left width_50 width_50_fix"><spring:message code="application.no2" /></th>
										<th class="for-left width_200 width_200_fix"><spring:message code="performance.criteria.lable" /></th>
										<th class="for-left width_100 width_100_fix align-right"><spring:message code="label.maxscore" /></th>
										<th class="for-left width_100 width_100_fix align-right"><spring:message code="label.weightage" /></th>
										<th class="for-left width_100 width_100_fix align-right"><spring:message code="label.score" /></th>
										<th class="for-left width_100 width_100_fix align-right"><spring:message code="label.totalScore" /></th>
										<th class="for-left width_100 width_100_fix"><spring:message code="label.comments" /></th>
									</tr>
								</thead>
								<tbody>
									<c:forEach items="${evaluatorUser.criteria}" var="item" varStatus="status">
										<tr>
 											<form:hidden path="criteria[${status.index}].id"/> 
											<c:if test="${item.order == 0}">
												<td class="for-left width_50 width_50_fix section_name"> ${item.level}.${item.order} </td>
												<td class="for-left width_200 width_200_fix section_name">${item.name}</td>
												<td class="for-left width_100 width_100_fix align-right section_name"><fmt:formatNumber type="number" value="${item.maximumScore}" /></td>
												<td class="for-left width_100 width_100_fix align-right section_name">${item.weightage}</td>
												<td class="for-left width_100 width_100_fix section_name align-right"></td>
												<td class="for-left width_100 width_100_fix align-right section_name">${item.evaluatorTotalScore}</td>
												<td class="for-left width_100 width_100_fix section_name"></td>
											</c:if>
										
											<c:if test="${item.order != 0}">
												<td class="for-left width_50 width_50_fix"> ${item.level}.${item.order} </td>
												<td class="for-left width_200 width_200_fix">${item.name}</td>
												<td class="for-left width_100 width_100_fix align-right"><fmt:formatNumber type="number" value="${item.maximumScore}" /></td>
												<td class="for-left width_100 width_100_fix align-right">${item.weightage}</td>
												
												<td class="for-left width_100 width_100_fix align-right">
													<c:if test="${evaluatorUser.evaluationStatus == 'DRAFT' and spForm.formStatus == 'ACTIVE' }">
														<form:input autocomplete="off" path="criteria[${status.index }].evaluatorScore" id="evalScore" cssClass="form-control" cssStyle="text-align:right" data-validation="required custom number length" data-validation-allowing="range[1;${item.maximumScore}],integer" data-validation-ignore="," class="validate itemValue" data-validation-length="1-3"/>
													</c:if>
													<c:if test="${ !(evaluatorUser.evaluationStatus == 'DRAFT' and spForm.formStatus == 'ACTIVE') }">
														<fmt:formatNumber type="number" value="${item.evaluatorScore}" />
													</c:if>
												</td>
												<td class="for-left width_100 width_100_fix align-right">${item.evaluatorTotalScore}</td>
												<td class="for-left width_100 width_100_fix">
													<c:if test="${evaluatorUser.evaluationStatus == 'DRAFT' and spForm.formStatus == 'ACTIVE' }">
														<form:input autocomplete="off" path="criteria[${status.index }].comments" id="comments" cssClass="form-control" data-validation="length" data-validation-length="0-128"/>
													</c:if>
													<c:if test="${ !(evaluatorUser.evaluationStatus == 'DRAFT' and spForm.formStatus == 'ACTIVE') }">
														${item.comments}
													</c:if>
												</td>
											</c:if>
										</tr>
									</c:forEach>
								</tbody>
							</table>
						</div>
					</div>
					
					<div class="import-supplier-inner-first-new global-list form-middle row col-md-4 col-sm-4 marg-top-20 ">
						<div class="ph_tabel_wrapper tbl-style">
							<div class="main_table_wrapper ph_table_border mega" style="margin: 0;">
								<table class="ph_table crtrt_tbl border-none display table table-bordered " width="100%" border="0" cellspacing="0" cellpadding="0" style="top: 0px;">
									<thead>
										<tr>
											<th class="for-left width_50 width_50_fix"><spring:message code="application.no2" /></th>
											<th class="for-left width_200 width_200_fix"><spring:message code="performance.criteria.lable" /></th>
											<th class="for-left width_100 width_100_fix align-center"><spring:message code="label.totalScore" /></th>
										</tr>
									</thead>
									<tbody>
										<c:forEach items="${evaluatorUser.criteria}" var="item">
											<c:if test="${item.order == 0 }">
												<tr>
													<td class="for-left width_50 width_50_fix"> ${item.level}.${item.order} </td>
													<td class="for-left width_200 width_200_fix section_name">${item.name}</td>
													<td class="for-left width_100 width_100_fix align-center"><fmt:formatNumber type="number" value="${item.evaluatorTotalScore}" /></td>
												</tr>
											</c:if>
										</c:forEach>
											<tr>
												<th class="for-left width_50 width_50_fix"></th>
												<th class="for-left width_200 width_200_fix section_name">Overall Score</th>
												<th class="for-left width_100 width_100_fix align-center" ><fmt:formatNumber type="number" value="${evaluatorUser.overallScore}" /></th>
											</tr>
											<tr>
												<th class="for-left width_50 width_50_fix"></th>
												<th class="for-left width_200 width_200_fix section_name">Rating</th>
												<th class="for-left width_100 width_100_fix align-center" >${evaluatorUser.scoreRating.rating}</th>
											</tr>
											<tr>
												<th class="for-left width_50 width_50_fix"></th>
												<th class="for-left width_200 width_200_fix section_name">Rating Description</th>
												<th class="for-left width_100 width_100_fix align-center" >${evaluatorUser.scoreRating.description}</th>
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
</div>

<style>
.s2_text_small {
	margin: 5px 0 0 0;
	font-size: 11px;
	display: none;
	max-height: 150px;
	text-align: left;
	margin-top: -1%;
}
.bqname {
   font-size: 16px;
}

.tbl-style {
	width: 500px !important ;
    border: ridge;
}

.crtrt_tbl.table td, .crtrt_tbl.table th {
    padding: 4px 9px !important;
    vertical-align: middle;
}
</style>
<script type="text/javascript">

var decimalLimit = 0;

$(document).delegate('.itemValue', 'change', function(e) {
	e.preventDefault();

	if($.trim(this.value) == ''){
		return;
	}
	var value = parseFloat($.trim(this.value).replace(/,/g, "").match(new RegExp("^\\d+\\.?\\d{0,"+decimalLimit+"}")));
	this.value=ReplaceNumberWithCommas(value.toFixed(decimalLimit));
});

$(document).delegate('.validate', 'keydown', function(e) {
	// Allow: backspace, delete, tab, escape, enter
	if ($.inArray(e.keyCode, [ 46, 8, 9, 27, 13, 110]) !== -1 ||
	// Allow: Ctrl+A, Command+A
	(e.keyCode === 65 && (e.ctrlKey === true || e.metaKey === true)) ||
	// Allow: home, end, left, right, down, up
	(e.keyCode >= 35 && e.keyCode <= 40)) {
		// let it happen, don't do anything
		return;
	}
	// Ensure that it is a number and stop the keypress
	if ((e.shiftKey || (e.keyCode < 48 || e.keyCode > 57)) && (e.keyCode < 96 || e.keyCode > 105)) {
		e.preventDefault();
	}
});
function ReplaceNumberWithCommas(yourNumber) {
	var n;
	// Seperates the components of the number
	if (yourNumber != null) {
		n = yourNumber.toString().split(".");
		// Comma-fies the first part
		n[0] = n[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
		// Combines the two sections
		return n.join(".");
	}
	return n;
}
</script>


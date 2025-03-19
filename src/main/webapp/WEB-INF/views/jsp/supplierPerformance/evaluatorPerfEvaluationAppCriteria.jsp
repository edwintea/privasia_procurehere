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
			<div class="col-sm-4 col-md-3 col-xs-6" style="padding-left: 0px;">
				<label> <spring:message code="supplier.performance.form.supplier" />: ${spForm.awardedSupplier.companyName}</label>
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
										<th class="for-left width_100 width_100_fix"><spring:message code="label.maxscore" /></th>
										<th class="for-left width_100 width_100_fix"><spring:message code="label.weightage" /></th>
										
										<th class="for-left width_100 width_100_fix"><spring:message code="label.score" /></th>
										<th class="for-left width_100 width_100_fix"><spring:message code="label.totalScore" /></th>
										<th class="for-left width_100 width_100_fix"><spring:message code="label.comments" /></th>
									</tr>
								</thead>
								<tbody>
									<c:forEach items="${evaluatorUser.criteria}" var="item" varStatus="status">
										<tr>
											<c:if test="${item.order == 0}">
												<td class="for-left width_50 width_50_fix section_name"> ${item.level}.${item.order} </td>
												<td class="for-left width_200 width_200_fix section_name" align="center">${item.name}</td>
												<td class="for-left width_100 width_100_fix section_name" align="right"><fmt:formatNumber type="number" value="${item.maximumScore}" /></td>
												<td class="for-left width_100 width_100_fix section_name" align="right">${item.weightage}</td>
												
												<td class="for-left width_100 width_100_fix section_name" align="right">${item.evaluatorScore}</td>
												<td class="for-left width_100 width_100_fix section_name" align="right">${item.evaluatorTotalScore}</td>
												<td class="for-left width_100 width_100_fix section_name" align="right"></td>
											</c:if>
											<c:if test="${item.order != 0}">
												<td class="for-left width_50 width_50_fix"> ${item.level}.${item.order} </td>
												<td class="for-left width_200 width_200_fix " align="center">${item.name}</td>
												<td class="for-left width_100 width_100_fix " align="right"><fmt:formatNumber type="number" value="${item.maximumScore}" /></td>
												<td class="for-left width_100 width_100_fix " align="right">${item.weightage}</td>
												
												<td class="for-left width_100 width_100_fix " align="right">${item.evaluatorScore}</td>
												<td class="for-left width_100 width_100_fix " align="right">${item.evaluatorTotalScore}</td>
												<td class="for-left width_100 width_100_fix " align="center">${item.comments}</td>
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
											<th class="for-left width_100 width_100_fix"><spring:message code="label.totalScore" /></th>
										</tr>
									</thead>
									<tbody>
										<c:forEach items="${evaluatorUser.criteria}" var="item">
											<c:if test="${item.order == 0 }">
												<tr>
													<td class="for-left width_50 width_50_fix"> ${item.level}.${item.order} </td>
													<td class="for-left width_200 width_200_fix section_name" align="center">${item.name}</td>
													<td class="for-left width_100 width_100_fix" align="center"><fmt:formatNumber type="number" value="${item.evaluatorTotalScore}"/></td>
												</tr>
											</c:if>
										</c:forEach>
											<tr>
												<td class="for-left width_50 width_50_fix"></td>
												<td class="for-left width_200 width_200_fix section_name" align="center">Overall Score</td>
												<td class="for-left width_100 width_100_fix" align="center"><fmt:formatNumber type="number" value="${evaluatorUser.overallScore}" /></td>
											</tr>
											<tr>
												<td class="for-left width_50 width_50_fix"></td>
												<td class="for-left width_200 width_200_fix section_name" align="center">Rating</td>
												<td class="for-left width_100 width_100_fix" align="center">${evaluatorUser.scoreRating.rating}</td>
											</tr>
											<tr>
												<td class="for-left width_50 width_50_fix"></td>
												<td class="for-left width_200 width_200_fix section_name" align="center">Rating Description</td>
												<td class="for-left width_100 width_100_fix" align="center">${evaluatorUser.scoreRating.description}</td>
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
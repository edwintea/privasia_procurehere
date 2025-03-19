<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<div class="marg-top-10 panel sum-accord" style="margin-bottom: 0px;">
	<div class="panel-heading">
		<h4 class="panel-title">
			<a data-toggle="collapse" class="accordion" href="#collapseEight"> <spring:message code="sp.evaluation.title" /> </a>
		</h4>
	</div>
	<div id="collapseEight" class="panel-collapse collapse in">
		<div class="panel-body pad_all_15">
			<div class="main_table_wrapper ph_table_border" style="margin: 0;">
				<table class="ph_table border-none display table" width="100%" border="0" cellspacing="0" cellpadding="0" style="top: 0px;">
					<thead>
						<tr>
							<th class="for-left width_50 width_50_fix"><spring:message code="application.no2" /></th>
							<th class="for-left width_200 width_200_fix"><spring:message code="performance.criteria.lable" /></th>
							<th class="for-left width_100 width_100_fix align-right"><spring:message code="label.maxscore" /></th>
							<th class="for-left width_100 width_100_fix align-right"><spring:message code="label.weightage" /></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${spfCriteriaList}" var="item">
							<tr>
								<c:if test="${item.order == 0}">
									<td class="for-left width_50 width_50_fix section_name"> ${item.level}.${item.order} </td>
									<td class="for-left width_200 width_200_fix section_name">${item.name}</td>
									<td class="for-left width_100 width_100_fix section_name align-right"><fmt:formatNumber type="number" value="${item.maximumScore}" /></td>
									<td class="for-left width_100 width_100_fix section_name align-right">${item.weightage}</td>
								</c:if>
							
								<c:if test="${item.order != 0}">
									<td class="for-left width_50 width_50_fix"> ${item.level}.${item.order} </td>
									<td class="for-left width_200 width_200_fix">${item.name}</td>
									<td class="for-left width_100 width_100_fix align-right"><fmt:formatNumber type="number" value="${item.maximumScore}" /></td>
									<td class="for-left width_100 width_100_fix align-right">${item.weightage}</td>
								</c:if>
							</tr>
						</c:forEach>
					</tbody>
				</table>
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
</style>
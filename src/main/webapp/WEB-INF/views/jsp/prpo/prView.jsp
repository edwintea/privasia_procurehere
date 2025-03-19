<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<spring:message var="prSummaryDesk" code="application.pr.create.summary" />
<spring:message var="poDetailsDesk" code="application.po.details" />
<spring:message var="prDefineApprovalDesk" code="application.pr.define.approvals" />
<script type="text/javascript">
<c:if test="${pr.status eq 'APPROVED'}">
zE(function() {
	zE.setHelpCenterSuggestions({ labels: [${poDetailsDesk}] });
});
</c:if>
<c:if test="${!(pr.status eq 'APPROVED')}">
zE(function() {
	zE.setHelpCenterSuggestions({ labels: [${prDefineApprovalDesk}, ${prSummaryDesk}] });
});			
</c:if>
</script>

<style>
.alert-lbl {
	background: red;
	color: #fff;
	margin-top: 3px;
	border-radius: 5px;
	padding: 7px 5px;
	font-size: 14px;
	text-align: right;
	font-weight: bold;
}

.alert-lbl-text {
	color: red;
	margin-top: 3px;
	border-radius: 5px;
	padding: 7px 5px;
	font-size: 14px;
	font-weight: bold;
}

.mb-14 {
	margin-bottom: 14px;
}
.w-200 {
	width: auto;
}
.d-flex {
	display: flex;
	justify-content: space-between;
	align-items: center;
}
</style>
<c:set var="viewMode" value="true" scope="request" />
<div id="page-content-wrapper">
	<div id="page-content">
		<div class="container">
			<!-- pageging  block -->

			<div class="d-flex mb-14">
				<div class="">
					<ol class="breadcrumb">
						<c:url var="buyerDashboard" value="/buyer/buyerDashboard" />
						<li>
							<a href="${buyerDashboard}"> <spring:message code="application.dashboard" />
							</a>
						</li>
						<li class="active">
							<spring:message code="pr.purchase.requisition" />
						</li>
					</ol>
				</div>
			</div>

			<div class="Section-title title_border gray-bg mar-b20">
				<h2 class="trans-cap supplier">PR: ${pr.name}</h2>
				<h2 class="trans-cap pull-right">
					<spring:message code="application.status" />
					: ${pr.status}
				</h2>
			</div>
			<div class="clear"></div>
			<jsp:include page="summary.jsp"></jsp:include>
		</div>
	</div>
</div>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<spring:message var="prSummaryDesk" code="application.pr.create.summary" />
<spring:message var="prTeamMemberDesk" code="application.pr.team.members" />
<spring:message var="prApprovalDesk" code="application.pr.approvals" />
<script type="text/javascript">
zE(function() {
	zE.setHelpCenterSuggestions({ labels: [${prSummaryDesk},${prTeamMemberDesk},${prApprovalDesk}] });
});
</script>
<div id="page-content-wrapper">
	<div id="page-content">
		<div class="container">
			<!-- pageging  block -->
			<ol class="breadcrumb">
				<c:url var="buyerDashboard" value="/buyer/buyerDashboard" />
				<li>
					<a href="${buyerDashboard}">
						<spring:message code="application.dashboard" />
					</a>
				</li>
				<li  class="text-black">
					<spring:message code="pr.report.title" />
				</li>
				<li class="active">
					<spring:message code="pr.purchase.requisition" />
				</li>
			</ol>
			<div class="Section-title title_border gray-bg mar-b20">
				<h2 class="trans-cap supplier">
					<spring:message code="pr.purchase.requisition" />
				</h2>
			</div>
			<jsp:include page="prHeader.jsp"></jsp:include>
			<div class="clear"></div>
			<jsp:include page="summary.jsp"></jsp:include>
		</div>
	</div>
</div>

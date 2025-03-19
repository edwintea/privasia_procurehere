<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
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
	<div id="page-content" view-name="product-contract">
		<div class="container">
			<!-- pageging  block -->
			<ol class="breadcrumb">
				<c:url var="buyerDashboard" value="/buyer/buyerDashboard" />
				<c:url var="contractDashboard" value="/buyer/productContractList" />
				
				<li>
					<a href="${buyerDashboard}" id="dashboardLink">
						<spring:message code="application.dashboard" />
					</a>
				</li>
				<li >
					<a href="${contractDashboard}" id="contractDashboardLink">
						<spring:message code="product.contracts.list.dashboard" />
					</a>	
				</li>
				<li class="active">
					<spring:message code="product.contract.summary" />
				</li>
			</ol>
			<div class="Section-title title_border gray-bg mar-b20">
				<h2 class="trans-cap supplier">
					<spring:message code="product.contract.summary" />
				</h2>
				<h2 class="trans-cap pull-right">
					<spring:message code="application.status" />
					: ${productContract.status}
				</h2>
			</div>
			<c:if test="${!inview}">
				<jsp:include page="contractHeader.jsp"></jsp:include>
			</c:if>
			<div class="clear"></div>
			<jsp:include page="contractSummary.jsp"></jsp:include>
		</div>
	</div>
</div>

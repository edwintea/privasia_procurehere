<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<div id="page-content-wrapper">
	<div id="page-content" style="background: #fff;">
		<div class="container">
			<!-- pageging  block -->
			<ol class="breadcrumb">
				<c:url var="buyerDashboard" value="/buyer/buyerDashboard" />
				<li><a id="dashboardLink" href="${buyerDashboard}"> <spring:message code="application.dashboard" />
				</a></li>
				<li class="active">Change Plan</li>
			</ol>
			<section class="create_list_sectoin">

				<div class="Section-title title_border gray-bg mar-b20">
					<h2 class="trans-cap supplier">Change Plan</h2>
				</div>
				<div class="clear"></div>
				<c:set var="changePlan" value="true" scope="request" />
				<input type="hidden" id="changePlanId" value="true">
				<jsp:include page="/WEB-INF/views/jsp/subscription/buyEventPlan.jsp" />
			</section>
		</div>
	</div>
</div>
<style>
.table1 td-border td, th {
	text-align: center;
	padding: 15px;
}

h4 {
	font-size: 18px;
	margin-top: 10px;
	margin-bottom: 10px;
}
</style>
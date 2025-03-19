<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">

<c:set var="viewMode" value="true" scope="request" />
<div id="page-content-wrapper">
	<div id="page-content">
		<div class="container">
			<ol class="breadcrumb">
				<c:url var="financeDashboard" value="/finance/financeDashboard" />
				<li><a href="${financeDashboard}"> <spring:message code="application.dashboard" />
				</a></li>
				<li class="active">Purchase Order</li>
			</ol>
			<div class="Section-title title_border gray-bg mar-b20">
				<h2 class="trans-cap supplier">
					PO : ${po.name}
				</h2>
				<h2 class="trans-cap pull-right">
				PO Date :  <fmt:formatDate value="${po.createdDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
				</h2>
				
			</div>
			<div class="clear"></div>
			<jsp:include page="financePoSummary.jsp"></jsp:include>
		</div>
	</div>
</div>
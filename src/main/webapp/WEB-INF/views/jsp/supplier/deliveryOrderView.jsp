<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css?1"/>">
<meta name="_csrf" content="${_csrf.token}" />
<meta name="_csrf_header" content="${_csrf.headerName}" />

<c:set var="viewMode" value="true" scope="request" />
<div id="page-content-wrapper">
	<div id="page-content">
		<div class="container">
			<ol class="breadcrumb">
				<c:url var="deliveryOrderList" value="/supplier/deliveryOrderList" />
				<li> <spring:message code="application.dashboard" />
				</li>
				<li ><a href="${deliveryOrderList}"><spring:message code="supplier.dashboard.dolist" /></a> </li>
				<li class="active"><spring:message code="supplier.do.delivery.order" /></li>
			</ol>
			<div class="Section-title title_border gray-bg mar-b20">
				<h2 class="trans-cap supplier">
					<spring:message code="supplier.do.summary.label" /> : ${deliveryOrder.name}
				</h2>
				<h2 class="trans-cap pull-right">
				<spring:message code="supplier.doListing.doStatus" /> :  ${deliveryOrder.status}
				</h2>
			</div>
 			<div class="clear"></div>
			<jsp:include page="deliveryOrderSummary.jsp"></jsp:include>
		</div>
	</div>
</div>
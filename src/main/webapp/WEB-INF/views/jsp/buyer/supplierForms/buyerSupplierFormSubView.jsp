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
				<li><a href="#"> <spring:message code="application.dashboard" /> </a>
				</li>
				<li><a href="${pageContext.request.contextPath}/buyer/importSupplier" id="idSupplierList"> <spring:message code="import.supplier" /> </a>
				</li>
				<li class="active"><spring:message code="supplier.form.label" /></li>
			</ol>
			<div class="Section-title title_border gray-bg mar-b20">
				<h2 class="trans-cap supplier">
					<spring:message code="supplierForm.name.label" /> : ${supplierForm.name}
				</h2>
				<h2 class="trans-cap pull-right">
				<spring:message code="supplier.form.status" /> :  ${supplierForm.status}
				</h2>
				
			</div>
			<div class="clear"></div>
			  <jsp:include page="buyerSupplierFormDetails.jsp"></jsp:include> 
		</div>
	</div>
</div>
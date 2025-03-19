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
				<c:url var="supplierDashboard" value="/supplier/supplierDashboard" />
				<li><a href="${supplierDashboard}"> <spring:message code="application.dashboard" />
				</a></li>
				<li class="active"><spring:message code="supplier.form.label" /></li>
			</ol>
			<div class="Section-title title_border gray-bg mar-b20">
				<h2 class="trans-cap supplier">
					<spring:message code="sourcingtemplate.form.name.placeholder" /> : ${supplierForm.name}
				</h2>
				<h2 class="trans-cap pull-right">
				<spring:message code="supplier.form.status" /> :  ${supplierForm.status}
				</h2>
				
			</div>
			<div class="clear"></div>
			<div class="white-bg border-all-side float-left width-100 pad_all_15">
	           <jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
	           <jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
	         <div class="row">
		       <div class="col-md-6 col-sm-12 col-xs-12">
			    <input type="hidden" id="poId" value="${supplierForm.id}">
			     <div class="tag-line">
				  <h2>
					<spring:message code="sourcingtemplate.form.name.placeholder" />
					: ${supplierForm.name}
				 </h2>
				 <br>
				 <h2>
					<spring:message code="supplier.form.request.date" />
					: <fmt:formatDate value="${supplierForm.requestedDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
				 </h2>
				  <br>
				  <h2>
					<spring:message code="formdetails.form.description.label" />
					: ${supplierForm.description}
				 </h2>
			    <br>
			   </div>
			  </div>
			 </div>
			</div>
			 <div class="clear"></div>
			  <jsp:include page="supplierFormDetails.jsp"></jsp:include> 
			  <div class="panel sum-accord marg-top-5">
			  <jsp:include page="supplierFormSubmissionAudit.jsp"></jsp:include>  
			  </div>
		</div>
	</div>
</div>
<script>


</script>
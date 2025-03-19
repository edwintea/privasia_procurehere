<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title><spring:message code="companystatus.title"/></title>
<meta charset="UTF-8">

<meta name="description" content="">
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">

</head>
<body>
<spring:message var="companyCreateDesk" code="application.owner.company.type.create" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${companyCreateDesk}] });
});
</script>
<div id="page-content">
		<div class="col-md-offset-1 col-md-10">
				 <!-- pageging  block -->	
                <ol class="breadcrumb">
					<li><a href="#"><spring:message code="application.dashboard"/></a></li>
					<c:url value="/admin/listCompanyStatus" var="listUrl"  />
					<li><a href="${listUrl}"><spring:message code="companystatus.list"/></a></li>
					<li class="active"><c:out  value='${btnValue}'/><spring:message code="label.companystatus"/></li>
				</ol>
				<div class="Section-title title_border gray-bg">
                            <h2 class="trans-cap manage_icon"><spring:message code="companystatus.administration"/></h2>
                </div>
                   
    <div class="Invited-Supplier-List import-supplier white-bg">
       
                <div class="meeting2-heading">
                                <h3><c:out  value='${btnValue}'/> <spring:message code="label.companystatus"/></h3></div>

        <div class="import-supplier-inner-first-new pad_all_15 global-list">

					
						<form:form id="companyStatusForm"  modelAttribute="companyObject" action="companyStatus" cssClass="form-horizontal bordered-row"  method="post">
						<header class="form_header">
						 <c:if test="${not empty error}">
							<div class="alert alert-danger" id="idGlobalError">
								<div class="bg-red alert-icon">
									<i class="glyph-icon icon-times"></i>
								</div>
								<div class="alert-content">
									<h4 class="alert-title"><spring:message code="application.error"/></h4>
									<p id="idGlobalErrorMessage">
										<c:forEach var="error" items="${error}">
													${error}<br />
										</c:forEach>
									</p>
								</div>
							</div>
						</c:if> </header>
					
						<form:hidden path="id" name="id" />
				              <div class="row marg-bottom-20">
									<div class="col-md-3"><label class="marg-top-10"><spring:message code="label.companystatus"/></label></div>
				                    <div class="col-md-5">
				                    <spring:message code="companystatus.placeholder" var="place"/>
							        <spring:message code="companystatus.type.required" var="required"/>
							        <spring:message code="companystatus.type.length" var="length"/>
										<form:input path="companystatus" id="idCompanyStatus" data-validation="required length alphanumeric" data-validation-length="1-64" data-validation-error-msg-required="${required}"  data-validation-error-msg-length="${length}" data-validation-allowing="-_/& " style="text-transform:uppercase" cssClass="form-control " placeholder="${place}" />
				                     <form:errors path="companystatus" cssClass="error"  />
				                    </div>
				                </div>
                
								<div class="row marg-bottom-20">
								
									<div class="col-md-3"><label class="marg-top-10"></label></div>
									<div class="col-md-9 dd sky mar_b_15" >
									 <form:button type="submit" id="saveCompanyStatus" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out ">${btnValue}</form:button>
									<%--  <input type="button" value="${btnValue}" id="saveCompanyStatus" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out"> --%> 
									
									<c:url value="/admin/listCompanyStatus" var="createUrl"  />
									<a href="${createUrl}" class="btn btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium button-previous"><spring:message code="application.cancel"/></a>

									</div>
								</div> 
                           </form:form>
        </div>
    </div>
</div>
</div>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.js"/>"></script>
<script>
  $.validate({
    lang: 'en'
  });
</script>
		
<!-- <script type="text/javascript" src="<c:url value="/resources/js/view/companyStatus.js"/>"></script> -->
</body>
</html>
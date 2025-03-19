<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<style>
.ph_btn_custom {
	height: 40px !important;
	min-width: 170px !important;
	font-size: 16px !important;
	line-height: 39px;
	font-weight: 500;
}
.red{
	color:red !important;
}
.greenColor{
	color:green !important;
}
.blueColor{
	color: #4689cb !important;
}

</style>
<c:set var="viewMode" value="true" scope="request" />
<div id="page-content-wrapper">
	<div id="page-content">
		<div class="container">
			<ol class="breadcrumb">
				<c:url var="supplierDashboard" value="/supplier/supplierDashboard" />
				<li><a href="${supplierDashboard}"> <spring:message code="application.dashboard" />
				</a></li>
				<li class="active"><spring:message code="supplier.view.request.buyer" /></li>
			</ol>
			<div class="Section-title title_border gray-bg mar-b20">
				<h2 class="trans-cap supplier">
					<spring:message code="supplier.view.request.buyerLabel" /> ${requestAssociteBuyerObj.buyerCompanyName}
				</h2>
			</div>
			<div class="clear"></div>
			<div class="white-bg border-all-side float-left width-100 pad_all_15">
				<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
				<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
				<div class="row marg-bottom-20">
					<div class="col-sm-4 col-md-3 col-xs-6">
						<label> <spring:message code="supplier.view.request.email" />
						</label>
					</div>
					<div class="col-sm-5 col-md-5 col-xs-6">
						<p>${requestAssociteBuyerObj.communicationEmail}</p>
					</div>
				</div>
				<div class="row marg-bottom-20">
					<div class="col-sm-4 col-md-3 col-xs-6">
						<label><spring:message code="supplier.view.request.contactNo" /></label>
					</div>
					<div class="col-sm-5 col-md-5 col-xs-6">
						<p>${requestAssociteBuyerObj.contactNumber}</p>
					</div>
				</div>
				<div class="row marg-bottom-20">
					<div class="col-sm-4 col-md-3 col-xs-6">
						<label> <spring:message code="supplier.view.request.contactPerson" />
						</label>
					</div>
					<div class="col-sm-5 col-md-5 col-xs-6">
						<p>${requestAssociteBuyerObj.contactPerson}</p>
					</div>
				</div>
				<div class="row marg-bottom-20">
					<div class="col-sm-4 col-md-3 col-xs-6">
						<label> <spring:message code="supplier.view.request.website" />
						</label>
					</div>
					<div class="col-sm-5 col-md-5 col-xs-6">
						<a href="http://${requestAssociteBuyerObj.website}" target="_blank"  id="idWebsite">${requestAssociteBuyerObj.website}</a>
					</div>
				</div>
				<div class="row marg-bottom-20">
					<div class="col-sm-4 col-md-3 col-xs-6">
						<label> <spring:message code="supplier.view.request.infoSupp" />
						</label>
					</div>
					<div class="col-sm-5 col-md-5 col-xs-6">
						<p>${requestAssociteBuyerObj.infoToSupplier}</p>
					</div>
				</div>
					
				<c:if test="${not empty requestAssociteBuyerObj.categories}">
					<div class="row marg-bottom-20">
						<div class="col-sm-4 col-md-3 col-xs-6">
							<label> <spring:message code="supplier.view.request.categories" /></label>
							<div class="row">
								<label style="margin-left:15px;">(Min:${requestAssociteBuyerObj.minimumCategories})</label>
							</div>
							<div class="row">
								<label style="margin-left:15px;">(Max:${requestAssociteBuyerObj.maximumCategories})</label>
							</div>
						</div>
						<div class="col-sm-5 col-md-5 col-xs-6 disabled">
							<select name="industryCategory" class="chosen-select" multiple="multiple"  id="chosenCategoryAll">
									<c:forEach items="${requestAssociteBuyerObj.categories}" var="cate">
										<option value="${cate.id}" label="${cate.code} - ${cate.name}" style="width: 100%" selected>${cate.code}-${cate.name} </option>
									</c:forEach>
							</select>
						</div>
					</div>
				</c:if>
				<div class="row marg-bottom-20">
					<div class="col-sm-4 col-md-3 col-xs-6">
						<label> <spring:message code="supplier.view.request.remark" />
						</label>
					</div>
					<div class="col-sm-5 col-md-5 col-xs-6">
						<p>${requestAssociteBuyerObj.supplierRemark}</p>
					</div>
				</div>
				<c:if test="${not empty requestAssociteBuyerObj.buyerRemark}">
					<div class="row marg-bottom-20">
						<div class="col-sm-4 col-md-3 col-xs-6">
							<label> <spring:message code="supplier.view.request.buyerRemark" />
							</label>
						</div>
						<div class="col-sm-5 col-md-5 col-xs-6">
							<p>${requestAssociteBuyerObj.buyerRemark}</p>
						</div>
					</div>
				</c:if>
				<div class="row marg-bottom-20">
					<div class="col-sm-4 col-md-3 col-xs-6">
						<label> <spring:message code="supplier.view.request.requestStatus" />
						</label>
					</div>
					<div class="col-sm-5 col-md-5 col-xs-6">
						<p class="${requestAssociteBuyerObj.status eq 'REJECTED' ?'red':(requestAssociteBuyerObj.status eq 'PENDING' ?'blueColor':'greenColor')}">${requestAssociteBuyerObj.status.value}</p>
					</div>
				</div>
				<div class="row marg-bottom-20">
					<div class="col-sm-4 col-md-3 col-xs-6">
						<label> <spring:message code="supplier.associated.requestedDate" />
						</label>
					</div>
					<div class="col-sm-5 col-md-5 col-xs-6">
						<p>
							<fmt:formatDate value="${requestAssociteBuyerObj.requestedDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
						</p>
					</div>
				</div>
				<c:if test="${requestAssociteBuyerObj.status eq'REJECTED'}">
				<div class="row marg-bottom-20">
					<div class="col-sm-4 col-md-3 col-xs-6">
						<label> <spring:message code="supplier.associated.rejectedDate" />
						</label>
					</div>
					<div class="col-sm-5 col-md-5 col-xs-6">
						<p>
							<fmt:formatDate value="${requestAssociteBuyerObj.rejectedDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
						</p>
					</div>
				</div>
				</c:if>
				<c:if test="${requestAssociteBuyerObj.status eq'APPROVED'}">
				<div class="row marg-bottom-20">
					<div class="col-sm-4 col-md-3 col-xs-6">
						<label> <spring:message code="supplier.associated.associationDate" />
						</label>
					</div>
					<div class="col-sm-5 col-md-5 col-xs-6">
						<p>
							<fmt:formatDate value="${requestAssociteBuyerObj.associatedDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
						</p>
					</div>
				</div>
				</c:if>
				<c:if test="${supplierForm.id ne null and supplierForm.isOnboardingForm}">
					<div class="panel">
					  <div class="panel-heading">
							<h4 class="panel-title">
								<a data-toggle="collapse" class="collapsed" data-parent="#accordion" href="#collapseForm"><spring:message code="defultMenu.suppliers.qualification.form" /> </a>
							</h4>
						</div>
						<div id="collapseForm" class="panel-collapse collapse">
						<div class="panel-body">
						   <jsp:include page="viewSupplierFormOnboardDetails.jsp"></jsp:include>  
						</div>
					  </div>
					</div>
						<div class="panel sum-accord" style="margin-top:-14px">
						 <jsp:include page="supplierFormSubmissionAudit.jsp"></jsp:include> 
						 </div>
		    	</c:if>				
				<div class="row marg-bottom-202">
					<div class="col-md-3">
							<label class="marg-top-10"></label>
					</div>
					<div class="col-md-9 dd sky mar_b_15">
						<a href="${pageContext.request.contextPath}/supplier/associateBuyerList" id="closeButtonId" class="btn btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium button-previous">Close</a>
					</div>
				</div>
		</div>
	</div>
</div>
</div>
<script type="text/javascript">
	$(document).on('click', '#idWebsite', function(e){ 
	    e.preventDefault(); 
	    var url = $(this).attr('href'); 
	 	// Search the pattern 
	    if (url && !url.match(/^http([s]?):\/\/.*/)) { 
	          
	        // If not exist then add http 
	        url = "http://" + url; 
	    } 
	    window.open(url, '_blank');
	});
</script>
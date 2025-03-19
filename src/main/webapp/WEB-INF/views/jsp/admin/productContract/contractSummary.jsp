<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize access="hasRole('ADMIN')" var="isAdmin" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<sec:authentication property="principal.id" var="loggedInUserId" />
<sec:authorize access="hasRole('ROLE_CONTRACT_EDIT')" var="canEdit" />

<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
<spring:message code="eventsummary.checkbox.editor" var="editorLabel" />
<spring:message code="eventsummary.checkbox.viewer" var="viewerLabel" />
<spring:message code="eventsummary.checkbox.associate.owner" var="associateOwnerLabel" />
<style>
.ph_btn_custom {
	height: 40px !important;
	min-width: 170px !important;
	font-size: 16px !important;
	line-height: 39px;
	font-weight: 500;
}
.bg-lbl{
	margin-bottom: 10px;
    background: red;    
    color: #fff !important;    
    display: inline-block;
    border-radius: 5px;
    padding-left: 10px !important;
    padding-right: 10px !important;
    margin-left: 10px !important;
}
.alert-lbl {
	color: red;
	padding: 7px 5px;
	font-size: 14px;
	font-weight: bold;
}
</style>
<%-- <div class="clear"></div>
<div class="white-bg border-all-side float-left width-100 pad_all_15">
	<div class="pull-right">
		<form:form action="${pageContext.request.contextPath}/buyer/downlaodContractSummary/${productContract.id}" method="GET">
			<button class="btn btn-sm float-right btn-info hvr-pop marg-left-10" id="idSumDownload" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="tooltip.contract.download.summary" />'>
				<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i></span>
				<span class="button-content"><spring:message code="product.contract.summary" /></span>
			</button>
		</form:form>
	</div>
</div> --%>
<div class="clear"></div>
<div class="tab-pane" style="display: block">
	<div class="upload_download_wrapper clearfix event_info">
		<h4>
			<spring:message code="product.contract.information" />
			<div class="pull-right">
				<form:form action="${pageContext.request.contextPath}/buyer/downlaodContractSummary/${productContract.id}" method="GET">
					<button class="btn btn-sm float-right btn-info hvr-pop marg-right-10" id="idSumDownload" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="tooltip.contract.download.summary" />'>
						<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i></span>
						<span class="button-content"><spring:message code="product.contract.summary" /></span>
					</button>
				</form:form>
			</div>
		</h4>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="product.contract.id" /></label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>${productContract.contractId}</p>
			</div>
		</div>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label><spring:message code="product.contract.name" /></label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>${productContract.contractName}</p>
			</div>
		</div>
		<c:if test="${not empty productContract.eventId}">
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label><spring:message code="application.eventid" /></label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>${productContract.eventId}</p>
			</div>
		</div>
		</c:if>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="product.contract.creator" />
				</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>
					${productContract.contractCreator.name} <br> ${productContract.contractCreator.communicationEmail} <br>
					<c:if test="${not empty productContract.buyer.companyContactNumber}">
						<spring:message code="prdraft.tel" />: ${productContract.buyer.companyContactNumber}</c:if>
					<c:if test="${not empty productContract.buyer.faxNumber}">
						<spring:message code="prtemplate.fax" />: ${productContract.buyer.faxNumber}</c:if>
					<c:if test="${not empty productContract.contractCreator.phoneNumber}">
						<spring:message code="prtemplate.hp" />: ${ productContract.contractCreator.phoneNumber}</c:if>
				</p>
			</div>
		</div>
		<c:if test="${!empty productContract.sapContractNumber}">
			<div class="form-tander1 requisition-summary-box">
				<div class="col-sm-4 col-md-3 col-xs-6">
					<label> <spring:message code="product.contract.sap.number" />
					</label>
				</div>
				<div class="col-sm-5 col-md-5 col-xs-6">
					<p>${productContract.sapContractNumber}</p>
				</div>
			</div>
		</c:if>
	</div>
	<div class="upload_download_wrapper clearfix marg-top-10 event_info">
		<h4>
			<spring:message code="product.contract.details" />
		</h4>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="product.contract.renewal" /> :
				</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<c:if test="${productContract.renewalContract == true}">
					<p>Yes</p>
				</c:if>
				<c:if test="${productContract.renewalContract != true}">
					<p>No</p>
				</c:if>
			</div>
		</div>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="application.referencenumber" /> :
				</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>${productContract.contractReferenceNumber}</p>
			</div>
		</div>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="product.contract.previous.contract" /> :
				</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>${productContract.previousContractNo}</p>
			</div>
		</div>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="label.businessUnit" /> :
				</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>
					<c:if test="${empty productContract.businessUnit}">
						<spring:message code="application.not.applicable" />
					</c:if>${productContract.businessUnit.unitName}</p>
			</div>
		</div>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="label.groupCode" /> :
				</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>${productContract.groupCodeStr}</p>
			</div>
		</div>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="application.supplier" /> :
				</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>${productContract.getSupplierName()}</p>
			</div>
		</div>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="label.procurement.category" /> :
				</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>${productContract.procurementCategory.procurementCategories}</p>
			</div>
		</div>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="label.agreement.type" /> :
				</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>${productContract.agreementType.agreementType} - ${productContract.agreementType.description}</p>
			</div>
		</div>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="product.contract.remark" /> :
				</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>${productContract.remark}</p>
			</div>
		</div>
	</div>
	<div class="upload_download_wrapper clearfix marg-top-10 event_info">
		<h4>
			<spring:message code="product.contract.period" />
		</h4>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="contract.start.date" /> :
				</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p><fmt:formatDate value="${productContract.contractStartDate}" pattern="dd/MM/yyyy" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" /></p>
			</div>
		</div>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="contract.end.date" /> :
				</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p><fmt:formatDate value="${productContract.contractEndDate}" pattern="dd/MM/yyyy" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" /></p>
			</div>
		</div>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="product.contract.reminder" /> :
				</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<ol style="padding-inline-start: 15px;margin-top: 10px;">
					<c:forEach var="dates" items="${productContractReminderDates}">
						<li>${dates}</li>
					</c:forEach>
				</ol>
			</div>
		</div>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="contract.reminder.notify.users" /> :
				</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<ol style="padding-inline-start: 15px;margin-top: 10px;">
					<c:forEach var="user" items="${notifyUsers}">
						<li>${user.user.name}</li>
					</c:forEach>
				</ol>
			</div>
		</div>
		<c:if test="${productContract.status != 'DRAFT' && productContract.status != 'PENDING' }">
			<div class="form-tander1 requisition-summary-box">
				<div class="col-sm-4 col-md-3 col-xs-6">
					<label> Document Date :</label>
				</div>
				<div class="col-sm-5 col-md-5 col-xs-6">
					<p><fmt:formatDate value="${productContract.documentDate}" pattern="dd/MM/yyyy" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" /></p>
				</div>
			</div>
		</c:if>
	</div>
	<div class="upload_download_wrapper clearfix marg-top-10 event_info">
		<h4>
			<spring:message code="product.Contract.value" />
		</h4>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="label.base.currency" /> :
				</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>${productContract.currency.currencyCode}</p>
			</div>
		</div>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="eventdescription.decimal.label" /> :
				</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>${productContract.decimal}</p>
			</div>
		</div>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> 
					<spring:message code="product.Contract.value" /> :
				</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>
				<fmt:formatNumber type="number" minFractionDigits="${productContract.decimal}" maxFractionDigits="${productContract.decimal}" value="${productContract.contractValue}" />
				</p>
			</div>
		</div>
	</div>
	
	<!-- Documents Table -->
	<div class="upload_download_wrapper clearfix marg-top-10 event_info">
		<h4 class="panel-title list-pad-0" style="margin-bottom: 0px;">
			<a data-toggle="collapse" data-parent="#accordion" class="accordion" href="#collapseThree"><spring:message code="product.contract.document" /> </a>
		</h4>
		<div id="collapseThree" class="col-md-12 col-sm-12 col-xs-12 in">
			<div class="form-tander1 requisition-summary-box">
				<div class="col-sm-4 col-md-3 col-xs-6" >
					<label>LOA Date:</label> 
				</div>
				<div class="col-sm-5 col-md-5 col-xs-6">
					<p><fmt:formatDate value="${contractLoaAndAgreement.loaDate}" pattern="dd/MM/yyyy" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" /></p>
				</div>
			</div>
			<div class="form-tander1 requisition-summary-box">
				<div class="col-sm-4 col-md-3 col-xs-6" >
					<label>LOA Documents:</label> 
				</div>
				<div class="col-sm-5 col-md-5 col-xs-6">
					<p>
					<c:if test="${not empty contractLoaAndAgreement.loaFileName}">
						<span class="color-green">COMPLETE</span>
					</c:if>
					<c:if test="${empty contractLoaAndAgreement.loaFileName}">
						<span class="color-red">INCOMPLETE</span>
					</c:if>
					</p>
				</div>
			</div>
			<div class="panel-body">
				<table width="100%" cellpadding="0" cellspacing="0" border="0" class="display table table-bordered noarrow dataTable no-footer">
					<thead>
						<tr>
							<th class="width_50 width_50_fix align-center wo-rp"><spring:message code="application.action" /></th>
							<th class="width_200 width_200_fix align-left wo-rp"><spring:message code="event.document.filename" /></th>
							<th class="width_200 width_200_fix align-left wo-rp"><spring:message code="application.description" /></th>
							<th class="width_100 width_100_fix align-left wo-rp"><spring:message code="application.upload.date" /></th>
							<th class="width_100 width_100_fix align-left wo-rp"><spring:message code="event.document.uploadby" /></th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td class="width_50 width_50_fix align-center wo-rp">
								<c:if test="${!empty contractLoaAndAgreement.loaFileName}">
									<a class="bluelink" href="${pageContext.request.contextPath}/buyer/downloadLoaDocument/${contractLoaAndAgreement.id}"><img src="${pageContext.request.contextPath}/resources/images/download.png" alt="download" /></i></a>
								</c:if>
							</td>
							<td class="width_200 width_200_fix align-left wo-rp">${empty contractLoaAndAgreement.loaFileName ? "Document Not Available" : contractLoaAndAgreement.loaFileName}</td>
							<td class="width_200 width_200_fix align-left wo-rp">${contractLoaAndAgreement.loaDescription}</td>
							<td class="width_100 width_100_fix align-left wo-rp"><fmt:formatDate value="${contractLoaAndAgreement.loaUploadDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" /></td>
							<td class="width_100 width_100_fix align-left wo-rp">${contractLoaAndAgreement.loaUploadedBy.name}</td>
						</tr>
					</tbody>
				</table>
			</div>

			<div class="form-tander1 requisition-summary-box">
				<div class="col-sm-4 col-md-3 col-xs-6" >
					<label>Agreement Date :</label> 
				</div>
				<div class="col-sm-5 col-md-5 col-xs-6">
					<p><fmt:formatDate value="${contractLoaAndAgreement.agreementDate}" pattern="dd/MM/yyyy" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" /></p>
				</div>
			</div>
			<div class="form-tander1 requisition-summary-box">
				<div class="col-sm-4 col-md-3 col-xs-6" >
					<label>Agreement Documents :</label> 
				</div>
				<div class="col-sm-5 col-md-5 col-xs-6">
					<p>
					<c:if test="${not empty contractLoaAndAgreement.agreementFileName}">
						<span class="color-green">COMPLETE</span>
					</c:if>
					<c:if test="${empty contractLoaAndAgreement.agreementFileName}">
						<span class="color-red">INCOMPLETE</span>
					</c:if>
					</p>
				</div>
			</div>

			<div class="panel-body">
				<table width="100%" cellpadding="0" cellspacing="0" border="0" class="display table table-bordered noarrow dataTable no-footer">
					<thead>
						<tr>
							<th class="width_50 width_50_fix align-center wo-rp"><spring:message code="application.action" /></th>
							<th class="width_200 width_200_fix align-left wo-rp"><spring:message code="event.document.filename" /></th>
							<th class="width_200 width_200_fix align-left wo-rp"><spring:message code="application.description" /></th>
							<th class="width_100 width_100_fix align-left wo-rp"><spring:message code="application.upload.date" /></th>
							<th class="width_100 width_100_fix align-left wo-rp"><spring:message code="event.document.uploadby" /></th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td class="width_50 width_50_fix align-center wo-rp">
								<c:if test="${!empty contractLoaAndAgreement.agreementFileName}">
									<a class="bluelink" href="${pageContext.request.contextPath}/buyer/downloadAgreementDocument/${contractLoaAndAgreement.id}"><img src="${pageContext.request.contextPath}/resources/images/download.png" alt="download" /></a>
								</c:if>
							</td>
							<td class="width_200 width_200_fix align-left wo-rp">${empty contractLoaAndAgreement.agreementFileName ? "Document Not Available" : contractLoaAndAgreement.agreementFileName}</td>
							<td class="width_200 width_200_fix align-left wo-rp">${contractLoaAndAgreement.agreementDescription}</td>
							<td class="width_100 width_100_fix align-left wo-rp"><fmt:formatDate value="${contractLoaAndAgreement.agreementUploadDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" /></td>
							<td class="width_100 width_100_fix align-left wo-rp">${contractLoaAndAgreement.agreementUploadedBy.name}</td>
						</tr>
					</tbody>
				</table>
			</div>

			<h4>Other Documents :</h4> 
			<div class="panel-body">
				<table width="100%" cellpadding="0" cellspacing="0" border="0" class="display table table-bordered noarrow dataTable no-footer">
					<thead>
						<tr class="tableHeaderWithongoing">
							<th class="width_50 width_50_fix align-center wo-rp"><spring:message code="application.action" /></th>
							<th class="width_200 width_200_fix align-left wo-rp"><spring:message code="event.document.filename" /></th>
							<th class="width_200 width_200_fix align-left wo-rp"><spring:message code="application.description" /></th>
							<th class="width_100 width_100_fix align-left wo-rp"><spring:message code="application.upload.date" /></th>
							<th class="width_100 width_100_fix align-left wo-rp"><spring:message code="event.document.uploadby" /></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="listDoc" items="${listDocs}">
							<tr>
								<td class="width_50 width_50_fix align-center wo-rp"><a class="bluelink" href="${pageContext.request.contextPath}/buyer/downloadContractDocument/${listDoc.id}"><img src="${pageContext.request.contextPath}/resources/images/download.png" alt="download"></a></td>
								<td class="width_200 width_200_fix align-left wo-rp">${listDoc.fileName}</td>
								<td class="width_200 width_200_fix align-left wo-rp">${listDoc.description}</td>
								<td class="width_100 width_100_fix align-left wo-rp"><fmt:formatDate value="${listDoc.uploadDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" /></td>
							 	<td class="width_100 width_100_fix align-left wo-rp">${listDoc.uploadedBy.name}</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</div>
	
	<!-- Contract Team Members -->
	<div class="panel sum-accord upload_download_wrapper clearfix marg-top-10 event_info">
		<div class="panel-heading">
			<h4 class="panel-title pad0">
				<a data-toggle="collapse" data-parent="#accordion" class="accordion" href="#collapseTeam"> <spring:message code="contract.team.members" />
				</a>
				<%-- <c:if test="${eventPermissions.owner  and !buyerReadOnlyAdmin}">
					<button class="editTeamMemberList sixbtn" title='<spring:message code="tooltip.edit" />'>
						<img src="${pageContext.request.contextPath}/resources/images/edit-btn.png" />
					</button>
				</c:if> --%>
			</h4>
		</div>
		<div id="collapseTeam" class="panel-collapse collapse accortab in">
			<div class="panel-body">
				<div class="clearfix"></div>
				<div class="col-md-12 col-sm-12 col-xs-12">
					<div class="main_table_wrapper">
						<table id="contractTeamMembersList" class="display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0">
							<thead>
								<tr class="tableHeaderWithongoing">
									<th class="for-left width_150 width_150_fix"><spring:message code="application.username" /></th>
									<th class="for-left width_150 width_150_fix"><spring:message code="buyer.profilesetup.loginmail" /></th>
									<th class="for-left width_150 width_150_fix"><spring:message code="application.access" /></th>
								</tr>
							</thead>
						</table>
					</div>
				</div>
			</div>
		</div>
	</div>

	<div class="upload_download_wrapper clearfix marg-top-10 event_info">
		<h4>
			<spring:message code="contract.item" />
		</h4>
		
		<div class="pad_all_15 float-left width-100">
			<div class="main_table_wrapper ph_table_border mega">
				<table class="ph_table border-none header parentBlocks" border="0" cellspacing="0" cellpadding="0" width="100%">
					<thead>
						<tr>
							<th class="width_100 width_100_fix text-nowrap"><spring:message code="product.Contract.itemNum" /></th>
							<th class="align-left width_200_fix"><spring:message code="label.rftbq.th.itemname" /></th>
							<th class="align-left width_100 width_100_fix"><spring:message code="product.itemCode" /></th>
							<th class="align-left width_200_fix"><spring:message code="contract.item.category" /></th>
							<th class="align-left width_100_fix"><spring:message code="label.rftbq.th.uom" /></th>
							<th class="align-right width_100 width_100_fix"><spring:message code="label.rftbq.th.quantity" /></th>
							<th class="align-right width_100 width_100_fix text-nowrap"><spring:message code="contract.balance.Quantity" /></th>
							<th class="align-right width_100 width_100_fix"><spring:message code="label.rftbq.th.unitprice" /></th>
							<th class="align-right width_100 width_100_fix"><spring:message code="contract.item.tax" /></th>
							<th class="align-right width_100 width_100_fix text-nowrap"><spring:message code="contract.price.per.unit" /></th>
							<th class="align-left width_100 width_100_fix"><spring:message code="product.item.type" /></th>
							<th class="align-left width_100 width_100_fix"><spring:message code="contract.item.brand" /></th>
							<th class="width_200 width_200_fix align-left"><spring:message code="storage.Location" /></th>
							<th class="width_200 width_200_fix align-left"><spring:message code="product.Contract.businessUnit" /></th>
							<th class="width_200 width_200_fix align-left"><spring:message code="label.costcenter" /></th>
						</tr>
					</thead>
				</table>
				<table class="data ph_table border-none childBlocks" border="0" cellspacing="0" cellpadding="0" width="100%">
					<tbody>
						<c:forEach items="${contractItemlist}" var="item">
							<tr class="${item.deleted ? 'deleted_row' : '' }">
								<td class="width_100 width_100_fix"> ${item.contractItemNumber}</td>
								<td class="align-left width_200_fix">${item.itemName}
									<c:if test="${not empty item.itemDescription}">
										<span class="item_detail s1_view_desc"><spring:message code="application.view.description" /></span>
									</c:if>
									<p class="s1_tent_tb_description s1_text_small">${item.itemDescription}</p>
								</td>
								<td class="align-left width_100 width_100_fix">${item.itemCode}</td>
								<td class="align-left width_200_fix">${item.productCategory.productName}</td>
								<td class="align-left width_100_fix">${item.uom.uom}</td>
								<td class="align-right width_100 width_100_fix"><fmt:formatNumber type="number" minFractionDigits="${productContract.decimal}" maxFractionDigits="${productContract.decimal}" value="${item.quantity}" /></td>
								<td class="align-right width_100 width_100_fix"><fmt:formatNumber type="number" minFractionDigits="${productContract.decimal}" maxFractionDigits="${productContract.decimal}" value="${item.balanceQuantity}" /></td>
								<td class="align-right width_100 width_100_fix"><fmt:formatNumber type="number" minFractionDigits="${productContract.decimal}" maxFractionDigits="${productContract.decimal}" value="${item.unitPrice}" /></td>
								<td class="align-right width_100 width_100_fix"><fmt:formatNumber type="number" minFractionDigits="${productContract.decimal}" maxFractionDigits="${productContract.decimal}" value="${item.tax}" /></td>
								<td class="align-right width_100 width_100_fix"><fmt:formatNumber type="number" minFractionDigits="${productContract.decimal}" maxFractionDigits="${productContract.decimal}" value="${item.pricePerUnit}" /></td>
								<td class="align-left width_100 width_100_fix">${item.productItemType}</td>
								<td class="align-left width_100 width_100_fix">${item.brand}</td>
								<td class="width_200 width_200_fix align-left">${item.storageLocation}</td>
								<td class="width_200 width_200_fix align-left">${item.businessUnit.unitName}</td>
								<td class="width_200 width_200_fix align-left">${item.costCenter.costCenter}</td>
							</tr>
						</c:forEach>
						<c:if test="${empty contractItemlist }">
							<tr>
								<td colspan="15">No contract items defined</td>
							</tr>
						</c:if>
					</tbody>
				</table>
			</div>
		</div>
	</div>
	<!-- Contract Approval -->
	<c:if test="${not empty productContract.approvals}">
		<div class="upload_download_wrapper clearfix marg-top-10 event_info Approval-tab">
			<h4>
				<spring:message code="contract.approval.route" />
<%-- 				<c:if test="${eventPermissions.owner and productContract.status == 'PENDING' and !buyerReadOnlyAdmin}">
					<a class="marg-left-20 editApprovalPopupButton" title=<spring:message code="tooltip.edit" />> <img src="${pageContext.request.contextPath}/resources/images/edit-btn.png" alt="edit" />
					</a>
				</c:if>
 --%>
 			</h4>
			<c:if test="${productContract.enableApprovalReminder}">
			<div class="pad_all_15 float-left appr-div position-relative">
				<div>
				  <div class="row">
					<label style="margin-left: 17px;">Reminder Settings: </label>
				  </div>
				  <div class="marg-left-10">
					 <p>Reminder emails sent every ${productContract.reminderAfterHour} hours.</p>
					 <p>Maximum ${productContract.reminderCount} reminder emails.</p>
					 <c:if test="${productContract.notifyEventOwner}">
					 <p><spring:message code="contract.notification.owner.message" /></p>
					 </c:if>
				  </div>
				</div>
			</div>
			</c:if>
			<c:forEach items="${productContract.approvals}" var="approval">
				<div class="pad_all_15 float-left appr-div position-relative">
					<label>Level ${approval.level}</label>
					<c:if test="${approval.active}">
						<div class="color-green marg-left-10">
							&nbsp;&nbsp;&nbsp; <i class="fa fa-arrow-left color-green" aria-hidden="true"></i>&nbsp;&nbsp;[Active]
						</div>
					</c:if>
					<div class="Approval-lavel1-upper">
						<c:forEach items="${approval.approvalUsers}" var="user" varStatus="status">
							<div class="Approval-lavel1 ${user.approvalStatus eq 'REJECTED' ? 'cross-red-mark' : ''} ${user.approvalStatus eq 'APPROVED' ? 'right-green-mark' : ''}">${user.user.name}
								<c:if test="${!user.user.active}">
										[<span class="inactiveCaption">INACTIVE</span>]
								</c:if>
							</div>
							<c:if test="${fn:length(approval.approvalUsers) > (status.index + 1)}">
								<span class="or-seg">${approval.approvalType}</span>
							</c:if>
						</c:forEach>
					</div>
				</div>
			</c:forEach>
			<div class="clear"></div>
			<div class="remark-tab pad0">
				<h4>
					<spring:message code="summarydetails.approval.comments" />
				</h4>
				<div class="pad_all_15 float-left width-100">
					<c:forEach items="${contractCommentList}" var="comment">
						<div class="Comments_inner ${comment.approved ? 'comment-right' : 'comment-wrong'}">
							<h3>${comment.createdBy.name}
								<span> <fmt:formatDate value="${comment.createdDate}" pattern="E dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
								</span>
							</h3>
							<div class="${comment.approved ? 'approve-div' : 'reject-div'}">${comment.comment}</div>
						</div>
					</c:forEach>
				</div>
				<c:if test="${productContract.status eq 'PENDING' and eventPermissions.activeApproval}">
					<div class="form_field">
						<form id="approvedRejectForm" method="post">
							<div class="row">
								<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> 
								<input type="hidden" name="contractId" id="contractId" value="${productContract.id}">

								<div class="col-sm-12 col-md-12 align-right col-xs-12 ">
									<label class="control-label"><spring:message code="label.meeting.remark" /> : </label>
								</div>
								<div class="col-sm-5 col-md-5 col-xs-12 ">
								
									<textarea name="remarks" id="approvalremarks" rows="4" data-validation="length" data-validation-length="0-450" class="form-control"></textarea>
								</div>

							</div>
							<div class="row">
								<div class="col-md-12 col-xs-12 col-ms-12 marg-top-20 marg-bottom-20">
									<button type="button" id="approvedButton" class="btn btn-info ph_btn hvr-pop marg-left-10 hvr-rectangle-out  ${ buyerReadOnlyAdmin ? 'disabled' : '' }">
										<spring:message code="buyer.dashboard.approve" />
									</button>
									<button type="button" id="rejectedButton" class="btn btn-black ph_btn hvr-pop marg-left-10 hvr-rectangle-out1  ${ buyerReadOnlyAdmin ? 'disabled' : '' }">
										<spring:message code="application.reject" />
									</button>
								</div>
							</div>
						</form>
					</div>
				</c:if>
			</div>
		</div>
	</c:if>

	<!-- Audit History -->
	<jsp:include page="contractAudit.jsp" />

	<c:if test="${(productContract.status == 'DRAFT' or ( (productContract.status == 'ACTIVE' or productContract.status == 'SUSPENDED') and eventPermissions.owner )) and !inview }">
		<div class="row">
			<div class="col-md-12 col-xs-12 col-ms-12">
				<c:if test="${!inView}">
					<c:url var="previous" value="/buyer/productContractItemList/${productContract.id}" />
					<a href="${previous}" class="btn btn-black ph_btn hvr-pop hvr-rectangle-out1" id="previousButton"><spring:message code="application.previous" /></a>
					<c:if test="${productContract.status == 'DRAFT' and !contractExpire and !buyerReadOnlyAdmin and eventPermissions.owner}">
						<a href="#confirmCancelContract" role="button" class="btn btn-danger marg-left-10 float-right ph_btn hvr-pop  ${ buyerReadOnlyAdmin ? 'disabled' : '' }" data-toggle="modal"><spring:message code="event.cancel.draft" /></a>
					</c:if>
					<c:if test="${eventPermissions.owner}">
						<c:url var="contractFinish" value="/buyer/contractFinish/${productContract.id}" />
						<a href="${contractFinish}" id="contractFinish" class="btn btn-info ph_btn hvr-pop marg-left-10 float-right hvr-rectangle-out  ${ buyerReadOnlyAdmin ? 'disabled' : '' }">
							<c:if test="${productContract.status == 'SUSPENDED'}">
								<spring:message code="application.resume" />
							</c:if>
							<c:if test="${productContract.status == 'DRAFT'}">
								<spring:message code="application.finish" />
							</c:if>
						</a>
					</c:if>
				</c:if>
				<c:if test="${eventPermissions.owner and (productContract.status == 'ACTIVE' or productContract.status == 'SUSPENDED')}">
					<a href="#confirmTerminateContract" role="button" class="btn btn-warning marg-left-10 ph_btn hvr-pop  ${ buyerReadOnlyAdmin ? 'disabled' : '' }" data-toggle="modal"><spring:message code="label.terminate.contract" /></a>
				</c:if>
			</div>
		</div>
	</c:if>

	<c:if test="${(productContract.status == 'ACTIVE' and eventPermissions.owner ) and inview }">
	<a href="#confirmTerminateContract" role="button" class="btn btn-warning marg-left-10 ph_btn hvr-pop  ${ buyerReadOnlyAdmin ? 'disabled' : '' }" data-toggle="modal"><spring:message code="label.terminate.contract" /></a>
	</c:if>
 <!-- Team Members list Pop-up -->
 <div class="flagvisibility dialogBox" id="teamMemberListPopup" title="Contract Team Members">
	<div class="float-left width100 pad_all_15 white-bg">
		<div class="form_field">
			<div class="form-group ">
				<div class="pull-left width100">
					<h3 class="modal-title text-center">
						<spring:message code="pr.event.team.member" />
					</h3>
					<hr />
					<div class="col-md-12 marginBottomA">
						<div class="ia-invite-controls-area">
							<div class="group">
								<div class="input-group mrg15T mrg15B">
									<select id="TeamMemberList" class="user-list-normal chosen-select" name="approverList1" selected-id="approver-id" cssClass="form-control user-list-normal chosen-select">
										<option value=""><spring:message code="prsummary.select.team.member" /></option>
										<c:forEach items="${userTeamMemberList}" var="TeamMember">
											<c:if test="${TeamMember.id == '-1' }">
												<option value="-1" disabled="true">${TeamMember.name}</option>
											</c:if>
											<c:if test="${TeamMember.id != '-1' }">
												<option value="${TeamMember.id}">${TeamMember.name}</option>
											</c:if>
										</c:forEach>
									</select>
									<div class="input-group-btn">
										<button type="button" class="btn btn-primary dropdown-toggle" data-toggle="dropdown">
											<span class="glyphicon glyphicon-eye-open"></span>
										</button>
										<ul class="dropdown-menu dropup">
											<li><a href="javascript:void(0);" class="small " tabIndex="-1"> <input id="access_editor" value="Editor" class="access_check" type="checkbox" /> &nbsp;<spring:message code="eventsummary.checkbox.editor" />
											</a></li>
											<li><a href="javascript:void(0);" class="small " tabIndex="-1"> <input id="access_viewer" value="Viewer" class="access_check" type="checkbox" /> &nbsp;<spring:message code="eventsummary.checkbox.viewer" />
											</a></li>
											<li><a href="javascript:void(0);" class="small " tabIndex="-1"> <input id="access_Associate_Owner" value="Associate_Owner" class="access_check" type="checkbox" /> &nbsp;<spring:message code="eventsummary.checkbox.associate.owner" />
											</a></li>
										</ul>
										<button class="btn btn-primary addTeamMemberToList" type="button">+</button>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class="col-md-12 col-sm-12 col-xs-12 margeinAllMDZero">
						<div class="mem-tab">
							<table cellpading="0" cellspacing="0" width="100%" id="appUsersList" class="table-hover table">
								<c:forEach items="${pr.prTeamMembers}" var="teamMembers">
									<tr data-username="${teamMembers.user.name}" approver-id="${teamMembers.user.id}">
										<td class="width_50_fix ">
											<!--	<img src='<c:url value="/resources/images/profile_setting_image.png"/>' alt="profile " /> -->
										</td>
										<td>${teamMembers.user.name}<br> <span>${teamMembers.user.loginId}</span>
										</td>
										<td class="edit-drop">
											<div class="advancee_menu">
												<div class="adm_box">
													<a class="adm_menu_link dropdown-toggle" data-toggle="dropdown" data-toggle="dropdown"> <i class="glyphicon ${teamMembers.teamMemberType=='Editor'?'glyphicon-pencil':teamMembers.teamMemberType=='Viewer'?'glyphicon-eye-open':teamMembers.teamMemberType=='Associate_Owner'?'fa fa-user-plus':''} " aria-hidden="true"
														title="${teamMembers.teamMemberType=='Editor'?editorLabel:teamMembers.teamMemberType=='Viewer'?viewerLabel:teamMembers.teamMemberType=='Associate_Owner'?associateOwnerLabel:''}"></i>
													</a>
													<ul class="dropdown-menu dropup">
														<li><a href="javascript:void(0);" class="small" tabIndex="-1"> <input data-uid="${teamMembers.user.id}" id="${teamMembers.user.id}-Editor" ${teamMembers.teamMemberType=='Editor' ? 'checked' : ''} value="Editor" class="access_check" type="checkbox" /> &nbsp;<spring:message code="eventsummary.checkbox.editor" />
														</a></li>
														<li><a href="javascript:void(0);" class="small " tabIndex="-1"> <input id="${teamMembers.user.id}-Viewer" ${teamMembers.teamMemberType=='Viewer'?'checked': ''} value="Viewer" data-uid="${teamMembers.user.id}" class="access_check" type="checkbox" /> &nbsp;<spring:message code="eventsummary.checkbox.viewer" />
														</a></li>
														<li><a href="javascript:void(0);" class="small " tabIndex="-1"> <input id="${teamMembers.user.id}-Associate_Owner" ${teamMembers.teamMemberType=='Associate_Owner'?'checked': ''} value="Associate_Owner" data-uid="${teamMembers.user.id}" class="access_check" type="checkbox" /> &nbsp;<spring:message code="eventsummary.checkbox.associate.owner" />
														</a></li>
													</ul>
												</div>
											</div>
										</td>
										<td>
											<div class="cqa_del">
												<a href="javascript:void(0);" list-type="Team Member" class="removeApproversList"><spring:message code="tooltip.delete" /></a>
											</div>
										</td>
									</tr>
								</c:forEach>
							</table>
						</div>
					</div>
					<div class="col-md-12 d-flex margeinAllMDZero">
						<a class="center-btn closeDialog btn btn-black pull-right marg-left-10 marg_top_20 hvr-pop ph_btn_small hvr-rectangle-out1" href="javascript:void(0);" onclick="window.location.reload();"> <spring:message code="eventReport.cls" />
						</a>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<!--Approver  popup-->
<div class="flagvisibility dialogBox" id="removeApproverListPopup" title='<spring:message code="tooltip.remove.team.member" />'>
	<div class="float-left width800 pad_all_15 white-bg">
		<input type="hidden" id="approverListId" name="approverListId" value=""> <input type="hidden" id="approverListType" name="approverListType" value="">
		<div class="row">
			<p class="col-md-12 marg-bottom-20 approverInfoBlock">
				<spring:message code="event.confirm.to.remove" />
				<span></span>
				<spring:message code="application.from" />
				<span></span>
				<spring:message code="application.list" />
				?
			</p>
		</div>
		<div class="event_form">
			<input id="id" type="hidden" value="${productContract.id}">
		</div>
		<div class="row">
			<div class="col-md-12">
				<div class="align-center">
					<a href="javascript:void(0);" title="" class="btn btn-info ph_btn_small btn-tooltip hvr-pop hvr-rectangle-out removeApproverListPerson" data-original-title="Delete"><spring:message code="tooltip.delete" /></a>
					<button type="button" class="closeDialog-btn btn btn-black marg-left-10 hvr-pop ph_btn_small hvr-rectangle-out1">
						<spring:message code="application.cancel" />
					</button>
				</div>
			</div>
		</div>
		<div></div>
	</div>
</div>
<!-- cancel contract popup  -->
<div class="modal fade" id="confirmCancelContract" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="eventsummary.confirm.cancel" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<form id="idFrmCancelContract" action="${pageContext.request.contextPath}/buyer/cancelContract" method="get">
				<input type="hidden" name="contractId" value="${productContract.id}"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<div class="col-md-12">
					<div class="row">
						<div class="modal-body col-md-12">
							<label> <spring:message code="contract.cancel.confirm" />
							</label>
						</div>
						<div class="form-group col-md-6">
							<spring:message code="event.reason.cancellation.placeholder" var="reasonCancellation" />
							<textarea class="width-100" placeholder="${reasonCancellation}" rows="3" name="cancelReason" id="cancelReason" data-validation="required length" data-validation-length="max1000"></textarea>
						</div>
					</div>
				</div>
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
					<input type="button" id="cancelContract" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left" value="Yes">
					<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
						<spring:message code="application.no2" />
					</button>
				</div>
			</form>
		</div>
	</div>
</div>

<!-- Terminate contract pop-up  -->
	<div class="modal fade" id="confirmTerminateContract" role="dialog">
		<div class="modal-dialog for-delete-all reminder">
			<!-- Modal content-->
			<div class="modal-content">
				<div class="modal-header">
					<h3>
						<spring:message code="confirm.terminate.cancel" />
					</h3>
					<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
				</div>
				<form id="terminateContractForm" action="${pageContext.request.contextPath}/buyer/terminateContract/${productContract.id}" method="get">
					<input type="hidden" name="contractId" value="${productContract.id}">
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
					<div class="col-md-12">
						<div class="row">
							<div class="modal-body col-md-12">
								<label>
									<spring:message code="contract.terminate.confirm" />
								</label>
							</div>
							<div class="form-group col-md-6">
								<spring:message code="rfaevent.enter.remarks" var="reasonTermination" />
								<textarea class="width-100" placeholder="${reasonTermination}" rows="3" name="terminationReason" id="terminationReason" data-validation="required length" data-validation-length="max250"></textarea>
							</div>
						</div>
					</div>
					<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
						<input type="submit" id="terminateContract" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left" value="YES">
						<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal" onclick="resetValidation()">
							<spring:message code="application.no" />
						</button>
					</div>
				</form>
			</div>
		</div>
	</div>
<style>
.d-flex {
	display: flex;
}

.center-btn {
	text-align: center;
	margin: 0 auto;
	margin-top: 25px !important;
}

.width-90 {
	width: 90%;
}

.editTeamMemberList {
	margin-left: 50px;
}

div.radio[id^='uniform-']>span {
	margin-top: 0 !important;
}

label.select-radio {
	width: auto;
}

#event {
	padding-left: 0;
}

.input-group.mrg15T.mrg15B {
	background-color: #f5f5f5;
	margin-bottom: 0;
	padding: 0;
}

.margeinAllMDZero {
	margin: 0;
	clear: both;
}

.marginBottomA {
	margin-bottom: 20px;
}

#appUsersList td {
	padding: 5px;
}

.mem-tab {
	border: 1px solid #ddd;
	border-radius: 2px;
	float: left;
	height: 300px;
	overflow: auto;
	position: relative;
	width: 100%;
}

.box_active {
	background: rgba(0, 0, 0, 0) none repeat scroll 0 0 !important;
}

.caret {
	color: #fff !important;
}

.cq_row_parent .input-group-btn {
	width: 200px;
}

.dropdown-menu input {
	display: inline !important;
	width: auto !important;
}

.advancee_menu ul {
	top: 100% !important;
	left: 0 !important;
}

.dropdown-menu .checkbox {
	display: inline-block !important;
	margin: 0 !important;
	padding: 0 !important;
	position: relative !important;
}

.dropdown-menu .checkbox input[type="checkbox"] {
	position: relative !important;
	margin-left: 0 !important;
}

.open>.dropdown-menu {
	padding-bottom: 17px;
	padding-top: 0;
}

#appUsersList tr:nth-child(odd) {
	background: #FFF
}

#appUsersList tr:nth-child(even) {
	background: #CCC
}

#eventTeamMembersList td, #eventTeamMembersList th {
	text-align: left !important;
	max-width: 100px !important;
}

#eventTeamMembersList {
	margin: 0 !important;
}

#eventTeamMembersList_length, #eventTeamMembersList_info,
	#eventTeamMembersList_paginate {
	display: none;
}

.dataTables_wrapper.form-inline.no-footer {
	overflow: hidden;
}

.editTeamMemberList {
	margin-left: 50px;
}

.grand-price-heading {
	width: 250px;
}

table.dataTable thead tr.tableHeaderWithongoing th.sorting_asc::after {
	content: "" !important;
}

table.dataTable thead tr.tableHeaderWithongoing th.sorting::after {
	content: "" !important
}

.inactiveCaption {
	margin: 0 0px 0 0px !important;
	font-weight: bold !important;
	color: #ff1d33 !important;
}


</style>
<script>
	$(document).delegate('.editTeamMemberList', 'click', function(e) {

		$("#teamMemberListPopup").dialog({
			modal : true,
			minWidth : 300,
			width : '50%',
			maxWidth : 600,
			minHeight : 200,
			open : function(event, ui) {
				$(".ui-dialog-titlebar-close").addClass("hide");
			},

			show : "fadeIn",
			draggable : true,
			resizable : false,
			dialogClass : "dialogBlockLoaded"
		});
		$(".dialogBlockLoaded").css({
			"position" : "absolute",
			"top" : $(this).offset().top - 300
		});
	});

	$('.closeDialog-btn').click(function() {
		$('#removeApproverListPopup').dialog('close');
	});
</script>
<script type="text/javascript">
	$('document').ready(function() {

		$('#contractFinish').click(function() {
			$(this).addClass('disabled');
		});

		$('#approvedButton').click(function() {
			$(this).addClass('disabled');
		});

		$('#rejectedButton').click(function() {
			$(this).addClass('disabled');
		});

		$('.childBlocks').css('margin-top', $('.parentBlocks').height());
		var eventTemMembersTable = $('#contractTeamMembersList').DataTable({
			"processing" : true,
			"serverSide" : true,
			"paging" : false,
			"info" : false,
			"ordering" : false,
			"searching" : false,
			"ajax" : {
				"url" : getContextPath() + '/buyer/contractTeamMembersList/${productContract.id}',
			},
			"columns" : [ {
				"data" : "user.name",
				"className" : "align-left",
				"defaultContent" : "Demo Test"
			}, {
				"data" : "user.loginId",
				"className" : "align-left",
				"defaultContent" : "Demo 2"
			}, {
				"data" : "teamMemberType",
				"className" : "align-left",
				"defaultContent" : ""
			} ]
		});

		$('#deliverPoBtn').click(function(e) {
			e.preventDefault();
			if (!$('#poStatusForm').isValid()) {
				return false;
			} else {
				$(this).addClass('disabled');
				$('#poStatusForm').attr('action', getContextPath() + "/buyer/deliverPo");
				$('#poStatusForm').submit();
			}
		})

		$('#transferPoBtn').click(function(e) {
			e.preventDefault();
			if (!$('#poStatusForm').isValid()) {
				return false;
			} else {
				$(this).addClass('disabled');
				$('#poStatusForm').attr('action', getContextPath() + "/buyer/transferPo");
				$('#poStatusForm').submit();
			}
		})

		$('#cancelPoBtn').click(function(e) {
			e.preventDefault();
			if (!$('#poStatusForm').isValid()) {
				return false;
			} else {
				$(this).addClass('disabled');
				$('#poStatusForm').attr('action', getContextPath() + "/buyer/cancelPo");
				$('#poStatusForm').submit();
			}
		})
		
		$('.editApprovalPopupButton').click(function(e){
			e.preventDefault();
			$("#idReminderSettings").hide();
		})

/* 		$('#createPoId').on('click', function(e) {
			e.preventDefault();
			if($("#createPoForm").isValid()) {
				$(this).addClass('disabled');
	 			$('#createPoForm').attr('action', getContextPath() + '/buyer/createPo');
				$("#createPoForm").submit();
			}else{
				return;
			}
		}) */;
		// Reset the form validation when the modal is hidden
		$('#confirmTerminateContract').on('hidden.bs.modal', function() {
			// Reset the form and validation
			$('#terminateContractForm')[0].reset();
			$.validate.resetForm();
		});

		// Handle the "Terminate" button click
		$('#terminateContract').click(function() {
			// Show the modal
			$('#confirmTerminateContract').modal('show');
		});

		// Handle the "No" button click
		$('.btn-default[data-dismiss="modal"]').click(function() {
			// Reset the form validation
			$.validate.resetForm();
		});
	});
	
	$('#approvedButton').click(function(e) {
		e.preventDefault();
		$(this).addClass('disabled');
		console.log("approved");
		$('#loading').show();
		$('#approvedRejectForm').attr('action', getContextPath() + "/buyer/contractApproved");
		$('#approvedRejectForm').submit();

	});
	$('#rejectedButton').click(function(e) {
		e.preventDefault();
		$(this).addClass('disabled');
		console.log("rejectedButton");
		$('#loading').show();
		$('#approvedRejectForm').attr('action', getContextPath() + "/buyer/contractRejected");
		$('#approvedRejectForm').submit();

	});
	
	$('#cancelContract').click(function(){
		if( $('#idFrmCancelContract').isValid()) {
			$('#idFrmCancelContract').submit();
			$('#loading').show();
		}
	});

</script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
	<script>
	<c:if test="${buyerReadOnlyAdmin or !canEdit or eventPermissions.viewer or (!eventPermissions.owner and !eventPermissions.editor and !eventPermissions.viewer)  or (eventPermissions.editor and (productContract.status == 'ACTIVE' or productContract.status == 'SUSPENDED'))}">
	$(window).bind('load', function() {
		var allowedFields = '#nextButton,#dashboardLink,#contractDashboardLink,#previousButton,#bubble, #downloadButton,#approvedButton,#rejectedButton,#idSumDownload,.downloadSnapshot,.s1_view_desc,.bluelink,#approvalremarks, .downloadPoBtn, #downloadTemplate';
		//var disableAnchers = ['#reloadMsg'];        
		disableFormFields(allowedFields);
	});
	</c:if>
	$.validate({
		lang : 'en',
		modules : 'file'
	});
</script>
<script type="text/javascript" src="<c:url value="/resources/js/view/prCreate.js"/>"></script>
<%-- <script type="text/javascript" src="<c:url value="/resources/js/view/prSummary.js"/>"></script> --%>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/cq_form.css"/>">

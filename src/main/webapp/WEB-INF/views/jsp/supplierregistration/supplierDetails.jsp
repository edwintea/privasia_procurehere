<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page import="com.privasia.procurehere.core.enums.SubscriptionStatus"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<sec:authorize access="hasRole('SUPPLIER_APPROVAL') or hasRole('ADMIN')" var="canEdit" />
<sec:authorize access="hasRole('SUPPLIER_CHANGE_EMAIL') or (hasRole('ADMIN') and hasRole('OWNER'))" var="canUpdateComEmail" />
<sec:authorize access="hasRole('ROLE_SUPPLIER_CHANGE_COMPANY_DETAILS') or (hasRole('ADMIN') and hasRole('OWNER'))" var="canEditComDetails" />

<style>
.display_inline > label {
	margin-left: 10px;
}

.display_inline > label > .chosen-container.chosen-container-single {
    width: 300px !important; /* or any value that fits your needs */
}
</style>

<script type="text/javascript">
	function canEdit() {
		return "${canEdit}";
	}
	
	function doAjaxPost(event, id, status) {
		event.preventDefault();
		$('div[id=idGlobalSuccess]').hide();
		$('div[id=idGlobalError]').hide();
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		var data = {}
		data["id"] = id;
		data["status"] = status;
		console.log(JSON.stringify(data));
		$.ajax({
			type : "POST",
			url : getContextPath() + "/supplierreg/confirmDetails",
			data : JSON.stringify(data),
			beforeSend : function(xhr) {
				$('#loading').show();
				xhr.setRequestHeader(header, token);
				xhr.setRequestHeader("Content-Type", "application/json");
			},
			complete : function() {
				$('#loading').hide();
			},
			success : function(data, textStatus, request) {
				window.location.href = getContextPath() + "/supplierreg/supplierSignupList";
			},
			error : function(request, textStatus, errorThrown) {

			}
		});
	}
</script>
<div id="page-content-wrapper">
	<div id="page-content">
		<div class="container">
			<ol class="breadcrumb">
				<sec:authorize access="hasRole('OWNER')">
					<li><a href="${pageContext.request.contextPath}/owner/ownerDashboard"><spring:message code="application.dashboard" /></a></li>
					<li><a href="${pageContext.request.contextPath}/supplierreg/supplierSignupList"><spring:message code="supplier.registeration.information" /></a></li>
				</sec:authorize>
				<sec:authorize access="hasRole('BUYER')">
					<li><a href="${pageContext.request.contextPath}/buyer/buyerDashboard"><spring:message code="application.dashboard" /></a></li>
					<li><a href="${pageContext.request.contextPath}/buyer/importSupplier">Supplier List</a></li>
				</sec:authorize>
				<li class="active"><spring:message code="application.supplier.detail" /></li>
			</ol>
			<!-- page title block -->
			<div class="Section-title title_border gray-bg">
				<h2 class="trans-cap supplier">
				${supplier.companyName}
				</h2>

				<form action="${pageContext.request.contextPath}/supplierreg/ExportSupplierProfile" method="post">
					<button class="btn btn-sm btn-success hvr-pop mrg-tp-10 pull-right" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="tooltip.export.profile" />'>
						<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i>
						</span> <span class="button-content"><spring:message code="supplier.export.profile.btn" /></span>
					</button>



					<div>
						<input type="hidden" name="supplierId" value="${supplier.id}" /> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
					</div>
				</form>

				<sec:authorize access="hasRole('SUPER_ADMIN') and hasRole('OWNER')">
					<c:if test="${adminUser != null and !supplier.registrationComplete}">
						<div style="float: right; margin-right: 5px;">
							<form id="resendSupplierActivationEmail" method="post" action="${pageContext.request.contextPath}/supplierreg/resendSupplierActivationLink">
								<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> <input type="hidden" id="supplierId" name="supplierId" value="${supplier.id}" />
								<div id="idResendSupplierActivationEmail" data-toggle="tooltip" data-placement="top" title="Resend Activation Email" style="cursor: pointer;" data-original-title='<spring:message code="tooltip.resend.activation.email" />' class="panel-heading">
									<spring:message code="supplier.resend.activation.email" />
								</div>
							</form>
						</div>
					</c:if>
				</sec:authorize>

				<input type="hidden" id="supplierId" name="supplierId" value="${supplier.id}" />
				<div class="trans-cap " style="float: right; margin-right: 0px;">
					<sec:authorize access="hasRole('SUPER_ADMIN') and hasRole('OWNER')">


						<c:if test="${adminUser != null}">
							<form:form name="form" method="post" action="${pageContext.request.contextPath}/supplierreg/toggleAdminAccountLockedStatus" modelAttribute="adminUser">
								<div data-toggle="tooltip" data-placement="top" title="${adminUser.locked ? 'Locked' : 'Unlocked'}" data-original-title="${adminUser.locked ? 'Locked' : 'Unlocked'}" class="panel-heading" onclick="$(this).closest('form').submit();" style="margin-right: 18px;">
									<form:hidden name="id" path="id" />
									<input type="hidden" name="supplierId" value="${supplier.id}" />
									<spring:message code="supplier.admin.account.status" />
									: <span style="color:${adminUser.locked ? 'red' : 'green'}"> ${adminUser.locked ? '<i class="fa fa-lock" aria-hidden="true"></i>' : '<i class="fa fa-unlock" aria-hidden="true"></i>'} </span>
								</div>
							</form:form>
						</c:if>
					</sec:authorize>
				</div>
			</div>
			<div class="clear"></div>
			<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
			<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
			<div class="clear"></div>
			<div class="tab-main">
				<ul class="tab">
					<li class="tab-link current">
						<a class="font-gray" id="switchToSupplierEngagementId"><spring:message code="supplierprofile.details"/></a>
					</li>
					<li class="tab-link">
						<a class="font-white" id="switchToSupplierProfileId"><spring:message code="supplier.engagement"/></a>
					</li>
					<sec:authorize access="hasRole('BUYER')">
						<li class="tab-link">
							<a class="font-white" id="switchToSupplierPerfomanceId"><spring:message code="supplier.performance"/></a>
						</li>
					</sec:authorize>
				</ul>
			</div> 
			<div class="clear"></div>
			<div id="supplierProfileViewId" class="col-md-12 pad0">
				<div class="panel bgnone">
					<div class="panel-body">
						<div class="example-box-wrapper">
							<div class="panel-group" id="accordion">
								<div class="panel">
									<div class="panel-heading">
										<h4 class="panel-title">
											<a data-toggle="collapse" data-parent="#accordion" href="#collapseOne"> ${supplier.companyName}
												<p style="float: right; margin-right: 30px;">
													<spring:message code="buyercreation.status" />
													${supplier.status}
												</p>
											</a>
										</h4>
									</div>
									<div id="collapseOne" class="panel-collapse collapse in">
										<div class="panel-body">
											<table class="tabaccor" cellpadding="0" cellspacing="0" border="0">
												<tr>
													<td><strong><spring:message code="buyercreation.company" /></strong></td>
													<td><span id="idCurrentCompName" class="bCompName">${supplier.companyName}</span>
														<c:if test="${canEditComDetails}">
															<input type="hidden" value="" id="editCompanyName">
															<a href="#" data-company="${supplier.companyName}" id="editCompName"> <i class="fa fa-pencil-square-o" aria-hidden="true"></i>
															</a>
														</c:if>
													</td>
													<td><c:if test="${supplier.formerCompanyName != null}"><div id="idFormerCompName" data-oldCompName="${supplier.companyName}">(formerly known as ${supplier.formerCompanyName})</div></c:if></td>
												</tr>
												<tr>
													<td><strong><spring:message code="supplier.registration.company.number" /></strong></td>
													<td><span id="idCurrentRegNo" class="bRegNo">${supplier.companyRegistrationNumber}</span>
														<c:if test="${canEditComDetails}"> 
															<input type="hidden" value="" id="editRegistrationNo">
															<a href="#" data-registnNumber="${supplier.companyRegistrationNumber}" id="editRegNo"> <i class="fa fa-pencil-square-o" aria-hidden="true"></i>
															</a>
														</c:if>
													</td>
													<td><c:if test="${supplier.formerRegistrationNumber != null}"><div id="idFormerRegNo" data-oldRegNo="${supplier.companyRegistrationNumber}">(formerly registered as ${supplier.formerRegistrationNumber})</div></c:if></td>
												</tr>
												<tr>
													<td><strong><spring:message code="label.companystatus" /></strong></td>
													<td>${supplier.companyStatus.companystatus}</td>
												</tr>
												<tr>
													<td><strong><spring:message code="supplier.year.of.establish" /></strong></td>
													<td>${supplier.yearOfEstablished}</td>
												</tr>
												<tr>
													<td><strong><spring:message code="supplier.tax.reg.number" /></strong></td>
													<td>${supplier.taxRegistrationNumber}</td>
												</tr>
												<tr>
													<td><strong><spring:message code="supplier.company.address" /></strong></td>
													<td>${supplier.line1},<br> ${supplier.line2},<br> ${supplier.city}<br> ${supplier.state.stateName}<br> ${supplier.registrationOfCountry.countryName}<br>
													</td>
												</tr>
												<tr>
													<td><strong><spring:message code="supplier.company.tel.number" /></strong></td>
													<td>${supplier.companyContactNumber}</td>
												</tr>
												<tr>
													<td><strong><spring:message code="supplier.company.fax.number" /></strong></td>
													<td>${supplier.faxNumber}</td>
												</tr>
												<tr>
													<td><strong><spring:message code="supplier.company.website" /></strong></td>
													<td>${supplier.companyWebsite}</td>
												</tr>
												<tr>
													<td><strong><spring:message code="supplier.primary.login.email" /></strong></td>
													<td>${supplier.loginEmail}</td>
												</tr>
												<tr>
													<td><strong><spring:message code="supplier.communication.email.address" /></strong></td>
													<td><span class="bEmail">${supplier.communicationEmail}</span> 
														<c:if test="${canUpdateComEmail}">
															<input type="hidden" value="" id="editEmailValue">
															<a href="#" data-mail="${supplier.communicationEmail}" id="edit_mail"> <i class="fa fa-pencil-square-o" aria-hidden="true"></i>
															</a>
														</c:if>
													</td>
												</tr>
												<tr>
													<td><strong><spring:message code="supplier.registeration.date" />:</strong></td>
													<td><fmt:formatDate value="${supplier.registrationDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" /></td>
												</tr>
												<tr>
													<td><strong><spring:message code="supplier.registeration.completion.date" /></strong></td>
													<td><fmt:formatDate value="${supplier.registrationCompleteDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" /></td>
												</tr>
												<tr>
													<td><strong><spring:message code="pr.remark" /></strong></td>
													<td>${supplier.remarks}</td>
												</tr>
											</table>
										</div>
									</div>
								</div>
								<div class="panel">
									<div class="panel-heading">
										<h4 class="panel-title">
											<a data-toggle="collapse" data-parent="#accordion" href="#collapseTwo"> <spring:message code="supplier.primary.contact" />
											</a>
										</h4>
									</div>
									<div id="collapseTwo" class="panel-collapse collapse">
										<div class="panel-body">
											<table class="tabaccor" cellpadding="0" cellspacing="0" border="0">
												<tr>
													<td><strong><spring:message code="supplier.primary.contact" /></strong></td>
													<td>${supplier.fullName}</td>
												</tr>
												<tr>
													<td><strong><spring:message code="supplier.designation" /></strong></td>
													<td>${supplier.designation}</td>
												</tr>
												<tr>
													<td><strong><spring:message code="supplier.primary.login.email" /></strong></td>
													<td>${supplier.loginEmail}</td>
												</tr>
												<tr>
													<td><strong><spring:message code="supplier.communication.email.address" /></strong></td>
													<td><span class="bEmail">${supplier.communicationEmail}</span></td>
												</tr>
												<tr>
													<td><strong><spring:message code="supplier.primary.mob.number" /></strong></td>
													<td>${supplier.mobileNumber}</td>
												</tr>
											</table>
										</div>
									</div>
								</div>

								<sec:authorize access="hasRole('OWNER')">
									<div class="panel">
										<div class="panel-heading">
											<h4 class="panel-title">
												<a data-toggle="collapse" data-parent="#accordion" href="#collapseTwelve"> <spring:message code="account.overview.current.subscription" />
												</a>
											</h4>
										</div>
										<div id="collapseTwelve" class="panel-collapse collapse">
											<div class="panel-body">
												<table class="tabaccor" cellpadding="0" cellspacing="0" border="0">
													<tr>
														<td><strong><spring:message code="account.overview.plan" /></strong></td>
														<td><b>${supplier.supplierPackage == null ? "N/A" : (supplier.supplierPackage.supplierPlan != null ? supplier.supplierPackage.supplierPlan.planName:"N/A") }</b> ${supplier.supplierPackage.supplierPlan != null ? " - ":""} ${supplier.supplierPackage == null ? "" :( supplier.supplierPackage.supplierPlan!= null ? supplier.supplierPackage.supplierPlan.shortDescription : "")}</td>
													</tr>
													<tr>
														<td><strong><spring:message code="supplier.subscription.type" /></strong></td>
														<td>${ supplier.getSupplierSubscription().getBuyerLimit()== 1 ?"Single Buyer Plan":"Unlimited Buyer Plan "}</td>
													</tr>
													<tr>
														<td><strong><spring:message code="supplier.promotional.codes.used" /></strong></td>
														<td>${(supplier.supplierSubscription != null and supplier.supplierSubscription.promoCode != null) ? supplier.supplierSubscription.promoCode :"N/A"}</td>
													</tr>
													<tr>
														<td><strong><spring:message code="account.overview.subscription.valid" /></strong></td>
														<td><c:choose>
																<c:when test="${supplier.supplierPackage != null and supplier.supplierPackage.startDate != null and supplier.supplierPackage.endDate != null }">
																	<fmt:formatDate value="${supplier.supplierPackage.startDate}" pattern="dd/MM/yyyy" /> to 
															<fmt:formatDate value="${supplier.supplierPackage.endDate}" pattern="dd/MM/yyyy" />
																<c:if test="${supplier.supplierPackage.subscriptionStatus != 'EXPIRED'}">
																	<span style="color: green;">(${supplier.supplierPackage.subscriptionStatus})</span>
																</c:if>
																<c:if test="${supplier.supplierPackage.subscriptionStatus == 'EXPIRED'}">
																	<span style="color: red;">(${supplier.supplierPackage.subscriptionStatus})</span>
																</c:if>
																	<br />
																</c:when>
																<c:otherwise>
       															 N/A 
        														<br />
																</c:otherwise>
															</c:choose></td>
													</tr>
													<c:if test="${!empty supplier.associatedBuyers}">
														<tr>
															<td><strong><spring:message code="supplier.associate.buyer" /> </strong></td>
															<td>
																<c:forEach items="${supplier.associatedBuyers}" var="buyer">
																	<li>${buyer.companyName}</li>
																</c:forEach>															
															</td>
														</tr>
													</c:if>
												</table>
												<h4 style="padding-top: 25px;"></h4>
												<div class="ph_table_border margin">
													<div class="mega range-header">

														<table class="header ph_table border-none" width="100%">
															<thead>
																<tr>
																	<th class="width_200 width_200_fix align-left"><strong><spring:message code="account.overview.subscription.valid" /></strong></th>
																	<th class="width_200 width_200_fix align-left"><strong><spring:message code="plan.planName" /></strong></th>
																	<th class="width_200 width_200_fix align-left"><strong><spring:message code="promotion.title" /></strong></th>
																	<th class="width_200 width_200_fix align-left"><strong><spring:message code="supplier.subscription.status" /></strong></th>
																	<th class="width_200 width_200_fix align-left"><strong><spring:message code="supplier.subscription.date" /></strong></th>
																</tr>
															</thead>
														</table>
														<table class="data ph_table border-none" width="100%">
															<tbody>
																<c:if test="${empty subscriptionsList}">
																	<tr>
																		<td class="width_200 width_200_fix align-left"><spring:message code="supplier.no.data.available" /></td>
																	</tr>
																</c:if>
																<c:forEach items="${subscriptionsList}" var="trans">
																	<tr>
																		<td class="width_200 width_200_fix align-left"><fmt:formatDate value="${trans.startDate}" pattern="dd/MM/yyyy" /> to <fmt:formatDate value="${trans.endDate}" pattern="dd/MM/yyyy"
																				 /></td>
																		<td class="width_200 width_200_fix align-left">${trans.supplierPlan.planName != null ? trans.supplierPlan.planName:"N/A" }</td>
																		<td class="width_200 width_200_fix align-left">${trans.promoCode != null ?trans.promoCode:"N/A" }</td>
																		<td class="width_200 width_200_fix align-left"><c:choose>
																				<c:when test="${trans.subscriptionStatus eq SubscriptionStatus.FUTURE}">
																					<span style="color: blue;">${trans.subscriptionStatus}</span>
																					<br />
																				</c:when>
																				<c:when test="${trans.subscriptionStatus eq SubscriptionStatus.ACTIVE}">
																					<span style="color: green;">${trans.subscriptionStatus}</span>
																					<br />
																				</c:when>
																				<c:otherwise>
																					<span style="color: red;">${trans.subscriptionStatus}</span>
																					<br />
																				</c:otherwise>
																			</c:choose></td>
																		<td class="width_200 width_200_fix align-left"><fmt:formatDate value="${trans.createdDate}" pattern="dd/MM/yyyy" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" /></td>

																	</tr>
																</c:forEach>
															</tbody>
														</table>
													</div>
												</div>
												<h4 style="padding-top: 25px;"></h4>


												<form:form id="extendValidityForm" method="post" action="${pageContext.request.contextPath}/supplierreg/extendSupplierValidity/${supplier.id}" modelAttribute="extensionValidity">
													<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
													<div class="row marg-bottom-20">
														<label class="col-sm-12 col-md-2 col-xs-2"> <spring:message code="account.validity.extension.reason"/>
														</label>
														<div class=" col-md-8">
															<form:textarea class="form-control" rows="3" path="extensionReason" data-validation="required length" data-validation-length="1-300" placeholder="Extension Reason"></form:textarea>
														</div>
													</div>
													<div class="row">
														<div>
															<label class="col-md-2"> <spring:message code="promotion.promotionExpiry" />
															</label>
															<div class="col-md-6">
																<div class="input-prepend input-group">
																	<fmt:formatDate var="endDate" value="${supplier.supplierPackage.endDate}" pattern="dd/MM/yyyy" />
																	<input type="hidden" value="${endDate}" id="supplierEndDate">
																	<form:input data-placement="top" data-toggle="tooltip" id="extensionDate" path="extensionDate" class="nvclick form-control for-clander-view" data-validation-format="dd/mm/yyyy" placeholder='dd/MM/yyyy' data-validation="required validate_date" readonly="true" style="cursor: pointer !important;"/>
																</div>
															</div>
															<div class="col-md-4">
																<button class="btn btn-blue" type="submit" name="extendValidity" style="width: 46%;">
																	<spring:message code="account.overview.extend.validity" />
																</button>
															</div>
														</div>
													</div>
												</form:form>


											</div>
										</div>
									</div>
								</sec:authorize>
								<div class="panel">
									<div class="panel-heading">
										<h4 class="panel-title">
											<a data-toggle="collapse" data-parent="#accordion" href="#collapseThree"> <spring:message code="supplier.industry.sector" />
											</a>
										</h4>
									</div>
									<div id="collapseThree" class="panel-collapse collapse">
										<div class="panel-body" style="max-height: 550px; overflow: scroll;">

											<div class="row">
												<c:forEach items="${supplier.naicsCodes}" var="sc" varStatus="status">
													<c:if test="${status.index % 3 == 0}">
														<div class="col-sm-6 col-md-4 col-lg-4 col-xs-12">
															<li><span class="number tree_heading">${sc.categoryCode} - ${sc.categoryName}</span></li>
														</div>
													</c:if>
													<c:if test="${status.index % 3 == 1}">
														<div class="col-sm-6 col-md-4 col-lg-4 col-xs-12">
															<li><span class="number tree_heading">${sc.categoryCode} - ${sc.categoryName}</span></li>
														</div>
													</c:if>
													<c:if test="${status.index % 3 == 2}">
														<div class="col-sm-6 col-md-4 col-lg-4 col-xs-12">
															<li><span class="number tree_heading">${sc.categoryCode} - ${sc.categoryName}</span></li>
														</div>
													</c:if>
												</c:forEach>
											</div>

										</div>
									</div>
								</div>
								<div class="panel">
									<div class="panel-heading">
										<h4 class="panel-title">
											<a data-toggle="collapse" data-parent="#accordion" href="#collapseFour"> <spring:message code="supplier.geo.coverage" />
											</a>
										</h4>
									</div>
									<div id="collapseFour" class="panel-collapse collapse">
										<div class="panel-body">
											<table class="tabaccor" cellpadding="0" cellspacing="0" border="0">
												<tr>
													<ul class="tree" id="tree">
														<c:forEach items="${supplier.coverages}" var="country">
															<li><span class="number tree_heading">${country.name}</span> <c:if test="${not empty country.children}">
																	<!-- AND SHOULD CHECK HERE -->
																	<c:forEach items="${country.children}" var="state">
																		<ul>
																			<li><span class="number">${state.name}</span></li>
																		</ul>
																	</c:forEach>
																</c:if></li>
														</c:forEach>
													</ul>
												</tr>
											</table>
										</div>
									</div>
								</div>
								<div class="panel">
									<div class="panel-heading">
										<h4 class="panel-title">
											<a data-toggle="collapse" data-parent="#accordion" href="#collapseFive"> <spring:message code="supplier.document.label" />
											</a>
										</h4>
									</div>
									<div id="collapseFive" class="panel-collapse collapse accortab">
										<div class="panel-body">
											<div class="borderradius">
												<table cellpadding="0" cellspacing="0" border="0">
													<c:forEach items="${supplier.supplierCompanyProfile}" var="companyPfo" varStatus="loop">
														<tr>
															<td style="font-weight: bold; font-size: 15px; width: 20%"><spring:message code="supplier.compny.profile" /> :</td>
															<td style="align: left"><form:form method="GET">
																	<c:url var="downloadComp" value="/supplierreg/downloadCopmanyProfile/${companyPfo.id}" />
																	<a href="${downloadComp}">${companyPfo.fileName}</a>
																</form:form></td>
														</tr>
													</c:forEach>
												</table>
											</div>
											<div class="borderradius">
												<br /> <span style="font-weight: bold; font-size: 15px; width: 20%">&nbsp;<spring:message code="supplier.other.profile" /> :</span> <br /> <br />
												<table class="tabaccor" cellpadding="0" cellspacing="0" border="0">
													<thead>
														<th class="pad-left-15"><strong><spring:message code="supplier.no.col" /></strong></th>
														<th><strong><spring:message code="event.document.filename" /></strong></th>
														<th><strong><spring:message code="podocument.file.description" /></strong></th>
														<th><strong><spring:message code="event.document.publishdate" /></strong></th>
													</thead>
													<tbody>
														<c:forEach items="${supplier.supplierOtherCredentials}" var="otherCred" varStatus="loop">
															<tr>
																<td>&nbsp;&nbsp; ${loop.index+1}</td>
																<td><form:form method="GET">
																		<c:url var="download" value="/supplierreg/downloadOtherCredential/${otherCred.id}" />
																		<a class="word-break" href="${download}">${otherCred.fileName}</a>
																	</form:form></td>
																<td>${otherCred.description}</td>
																<td><fmt:formatDate value="${otherCred.uploadDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" /></td>
															</tr>
														</c:forEach>
													</tbody>
												</table>
											</div>
										</div>
									</div>
								</div>
								<div class="panel">
									<div class="panel-heading">
										<h4 class="panel-title">
											<a data-toggle="collapse" data-parent="#accordion" href="#collapseSix"> <spring:message code="eventdescription.financial.info.label" /> </a>
										</h4>
									</div>
									<div id="collapseSix" class="panel-collapse collapse accortab">
										<div class="panel-body">
											<div class="borderradius">
												<br /> <span style="font-weight: bold; font-size: 15px; width: 20%">&nbsp;<spring:message code="supplier.registration.financial.info.label" /></span> <br /> <br />
												<table cellpadding="0" cellspacing="0" border="0" class="m-bottom-20">
													<tbody>
															<tr>
																<td style="font-weight: bold; font-size: 15px; width: 20%; padding: 15px 15px!important;"><spring:message code="supplier.paid.up.capital" /> : &nbsp;&nbsp;${supplier.paidUpCapital}&nbsp;${supplier.currency}</td>
															</tr>
													</tbody>
												</table>
												<br /> <span style="font-weight: bold; font-size: 15px; width: 20%">&nbsp;<spring:message code="supplier.registration.financial.info.label2" /></span> <br /> <br />
												<table cellpadding="0" cellspacing="0" border="0">
													<thead>
														<th class="pad-left-15"><strong><spring:message code="supplier.no.col" /></strong></th>
														<th><strong><spring:message code="event.document.filename" /></strong></th>
														<th><strong><spring:message code="podocument.file.description" /></strong></th>
														<th><strong><spring:message code="event.document.publishdate" /></strong></th>
													</thead>
													<tbody>
														<c:forEach items="${financialDocuments}" var="finanicalDocs" varStatus="loop">
															<tr>
																<td>&nbsp;&nbsp; ${loop.index+1}</td>
																<td style="align: left"><form:form method="GET">
																		<c:url var="downloadFinanicalDocs" value="/downloadFinancialDocuments/${finanicalDocs.id}" />
																		<a href="${downloadFinanicalDocs}">${finanicalDocs.fileName}</a>
																	</form:form></td>
																<td>${finanicalDocs.description}</td>
																<td><fmt:formatDate value="${finanicalDocs.uploadDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" /></td>
															</tr>
														</c:forEach>
														<c:if test="${financialDocuments != null && financialDocuments.size() == 0}">
															<tr>
																<td>&nbsp;&nbsp;</td>
																<td>&nbsp;</td>
																<td>&nbsp;</td>
																<td>&nbsp;</td>
															</tr>
														</c:if>
													</tbody>
												</table>
											</div>
										</div>
									</div>
								</div>
								<div class="panel">
									<div class="panel-heading">
										<h4 class="panel-title">
											<a data-toggle="collapse" data-parent="#accordion" href="#collapseSeven"> <spring:message code="eventdescription.org.details.label" /> </a>
										</h4>
									</div>
									<div id="collapseSeven" class="panel-collapse collapse accortab">
										<div class="panel-body">
											<div class="borderradius">
												<br /> <span style="font-weight: bold; font-size: 15px; width: 20%">&nbsp;<spring:message code="application.org.details" /> :</span> <br /> <br />
												<table class="tabaccor" cellpadding="0" cellspacing="0" border="0">
													<thead>
														<th class="pad-left-15"><strong><spring:message code="supplier.no.col"/></strong></th>
														<th><strong><spring:message code="supplier.board.of.director.name" /></strong></th>
														<th><strong><spring:message code="supplier.board.of.director.identification.type" /></strong></th>
														<th><strong><spring:message code="supplier.board.of.director.identification.number" /></strong></th>
														<th><strong><spring:message code="supplier.board.of.director.type" /></strong></th>
														<th><strong><spring:message code="supplier.board.of.director.email" /></strong></th>
														<th><strong><spring:message code="supplier.board.of.director.contact" /></strong></th>
													</thead>
													<tbody>
														<c:forEach items="${organisationalDetails}" var="boardOfDirectors" varStatus="loop">
															<tr>
																<td>&nbsp;&nbsp; ${loop.index+1}</td>
																<td>${boardOfDirectors.directorName}</td>
																<td>${boardOfDirectors.idType}</td>
																<td>${boardOfDirectors.idNumber}</td>
																<td>${boardOfDirectors.dirType}</td>
																<td>${boardOfDirectors.dirEmail}</td>
																<td>${boardOfDirectors.dirContact}</td>
															</tr>
														</c:forEach>
														<c:if test="${organisationalDetails != null && organisationalDetails.size() == 0}">
															<tr>
																<td>&nbsp;&nbsp;</td>
																<td>&nbsp;</td>
																<td>&nbsp;</td>
																<td>&nbsp;</td>
																<td>&nbsp;</td>
																<td>&nbsp;</td>
																<td>&nbsp;</td>
															</tr>
														</c:if>
													</tbody>
												</table>
											</div>
										</div>
									</div>
								</div>
								<div class="panel">
									<div class="panel-heading">
										<h4 class="panel-title">
											<a data-toggle="collapse" data-parent="#accordion" href="#collapseNotes"> <spring:message code="supplier.track.record" /> </a>
										</h4>
									</div>
									<div id="collapseNotes" class="panel-collapse collapse accortab">
										<div class="panel-body">
											<p style="margin-bottom: 10px; font-size: larger;">${supplier.supplierTrackDesc}</p>
											<div class="borderradius">
												<table class="tabaccor" cellpadding="0" cellspacing="0" border="0">
													<thead>
														<th class="pad-left-15"><strong><spring:message code="supplier.no.col" /></strong></th>
														<th><strong><spring:message code="supplier.registration.track.project" /></strong></th>
														<th><strong><spring:message code="supplier.registration.track.year" /></strong></th>
														<th><strong><spring:message code="supplier.project.category" /></strong></th>
														<th><strong><spring:message code="supplier.project.value" /></strong></th>
														<th><strong><spring:message code="supplier.client.name" /></strong></th>
														<th><strong><spring:message code="supplier.client.email.address" /></strong></th>
													</thead>
													<tbody>
														<c:forEach items="${supplier.supplierProjects}" var="trackRecord" varStatus="loop">
															<tr>
																<td>${loop.index+1}</td>
																<td>${trackRecord.projectName}</td>
																<td>${trackRecord.year}</td>
																<td><c:forEach items="${trackRecord.projectIndustries}" var="projCategory">
                                     										${projCategory.categoryName}<br />
																	</c:forEach></td>
																<td>${trackRecord.contactValue}</td>
																<td>${trackRecord.clientName}</td>
																<td>${trackRecord.clientEmail}</td>
															</tr>
														</c:forEach>
													</tbody>
												</table>
											</div>
										</div>
									</div>
								</div>
								


								<!-- OTHER DOCUMENTS -->
								<div class="panel">
									<div class="panel-heading">
										<h4 class="panel-title">
											<a data-toggle="collapse" data-parent="#accordion" href="#collapseOtherDocs"><spring:message code="supplier.other.documents" /></a>
										</h4>
									</div>
									<div id="collapseOtherDocs" class="panel-collapse collapse">
										<div class="panel-body">
											<sec:authorize access="hasRole('ADMIN') and hasRole('OWNER')">
												<div class="Invited-Supplier-List create_sub note marg-bottom-20">
													<div class="col-md-6">
														<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/hover.css"/>">
														<input type="hidden" id="idSupplier" value="${supplier.id}" />
														<h3 class="blue_form_sbtitle pad-tb-30 add_file text-black"></h3>
														<c:if test="${not empty errors }">
															<div class="alert alert-danger" id="idGlobalError">
																<div class="bg-red alert-icon">
																	<i class="glyph-icon icon-times"></i>
																</div>
																<div class="alert-content">
																	<h4 class="alert-title">Error</h4>
																	<p id="idGlobalErrorMessage">
																		<c:forEach var="error" items="${errors}">
																					${error}<br />
																		</c:forEach>
																	</p>
																</div>
															</div>
														</c:if>
														<form id="otherDocumentUploadForm">
															<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
															<div class="add_file_row">
																<c:set var="fileType" value="" />
																<c:forEach var="type" items="${ownerSettings.fileTypes}" varStatus="index">
																	<c:set var="fileType" value="${fileType}${index.first ? '': ', '}${type}" />
																</c:forEach>
																<span> <spring:message code="application.note" />:<br />
																	<ul>
																		<li><spring:message code="createrfi.documents.max.size" /> ${ownerSettings.fileSizeLimit} MB</li>
																		<li><spring:message code="createrfi.documents.allowed.file.extension" />: ${fileType}.</li>
																	</ul>
																</span>
																<div data-provides="fileinput" class="fileinput fileinput-new input-group">
																	<div data-trigger="fileinput" class="form-control">
																		<i class="glyphicon glyphicon-file fileinput-exists"></i> <span id="idOtherFileUploadSpan" class="fileinput-filename show_name"></span>
																	</div>
																	<span class="input-group-addon btn btn-black btn-file"> <span class="fileinput-new" id="selectNew"><spring:message code="application.selectfile" /></span> <span class="fileinput-exists" id="fileinput-exists"><spring:message code="event.document.change" /></span> <input
																		type="file" data-buttonName="btn-black" id="otherDocumentUpload" name="otherDocumentUpload" data-validation-allowing="${fileType}" data-validation-error-msg-container="#Load_File-error-dialogOtherDocs" data-validation-max-size="${ownerSettings.fileSizeLimit}MB"
																		data-validation="extension size" data-validation-error-msg-size="You can not upload file larger than ${ownerSettings.fileSizeLimit}MB" data-validation-error-msg-mime="${mimetypes}">
																	</span> <a data-dismiss="fileinput" class="input-group-addon btn btn-default fileinput-exists" id="remove" href="#"><spring:message code="application.remove" /></a>
																</div>
															</div>
															<div id="Load_File-error-dialogOtherDocs" style="width: 100%; float: left; margin: 0 0 10px 0;"></div>
															<div class="row marg-bottom-20">
																<label class="col-sm-4 col-md-4 col-xs-6"> <spring:message code="podocument.file.description" />
																</label>
																<div class=" col-md-8">
																	<textarea class="form-control" rows="3" id="otherDocumentDesc" name="otherDocumentDesc" data-validation="required" placeholder='<spring:message code="costcenter.placeholder.description" />'></textarea>
																</div>
															</div>
															<div class="form-group">
																<label class="col-sm-4 col-md-4 col-xs-4 "> <spring:message code="promotion.promotionExpiry" />
																</label>
																<div class="col-sm-4 col-md-4 col-xs-3">
																	<div class="input-prepend input-group">
																		<input data-placement="top" data-toggle="tooltip" id="expiryDate" class="nvclick form-control for-clander-view" data-validation-format="dd/mm/yyyy" placeholder='<spring:message code="dateformat.placeholder" />' />
																	</div>
																</div>
															</div>
															<div class="form-group">
																<div class="col-sm-4 col-md-4 col-xs-4">&nbsp;</div>
																<div class="col-sm-4 col-md-4 col-xs-4">
																	<button class="btn btn-blue btn-lg btn-block up_btn marg-bottom-20" type="button" name="OtherDocsUpload" id="OtherDocsUpload">
																		<spring:message code="supplier.upload.button" />
																	</button>
																</div>
																<div class="col-sm-4 col-md-4 col-xs-4">&nbsp;</div>
															</div>
														</form>
													</div>
												</div>
											</sec:authorize>
											<div class="col-xs-10">
												<div class="step_table mega">
													<table class="table header table-admin" cellspacing="0" cellpadding="0">
														<thead>
															<tr>
																<th class="align-center width_100_fix"><spring:message code="application.remove" /></th>
																<th class="align-left width_200"><spring:message code="event.document.filename" /></th>
																<th class="align-left width_200"><spring:message code="application.description" /></th>
																<th class="align-left width_200_fix"><spring:message code="event.document.publishdate" /></th>
																<th class="align-left width_200_fix"><spring:message code="supplier.expiry.date" /></th>
															</tr>
														</thead>
													</table>
													<table class="data for-pad-data table" id="uploadOtherFiless">
														<tbody>
															<c:forEach items="${otherDocsList}" var="sp">
																<tr>
																	<td class="align-center width_100_fix"><span class="removeOtherFile" removeOtherId='${sp.id}' otherDocFile='${sp.fileName}'> <a href=""> <i class="fa fa-trash-o" aria-hidden="true"></i>
																		</a>
																	</span></td>
																	<td class="align-left width_200"><form:form method="GET">
																			<c:url var="download" value="/supplierreg/downloadOtherDocument/${sp.id}" />
																			<a href="${download}">${sp.fileName}</a>
																		</form:form></td>
																	<td class="align-left width_200">${sp.description}</td>
																	<td class="align-left width_200_fix"><fmt:formatDate value="${sp.uploadDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" /></td>
																	<td class="align-left width_200_fix"><fmt:formatDate value="${sp.expiryDate}" pattern="dd/MM/yyyy" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" /></td>
																</tr>
															</c:forEach>
														</tbody>
													</table>
												</div>
												<div id="morris-bar-yearly" class="graph" style="visibility: hidden"></div>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div id="supplierEngagementViewId" class="col-md-12 pad0" style="display: none;">
				<div class="panel bgnone">
					<div class="panel-body">
						<div class="example-box-wrapper">
							<div class="panel-group" id="accordion">
								<sec:authorize access="hasRole('BUYER')">
									<c:if test="${supplierRequest != null}">
										<jsp:include page="/WEB-INF/views/jsp/supplier/supplierRequest.jsp"></jsp:include>
									</c:if>
								</sec:authorize>
								<sec:authorize access="hasRole('BUYER')">
									<!-- Add Supplier Form -->
										<jsp:include page="/WEB-INF/views/jsp/supplier/supplierForm.jsp"></jsp:include>
								</sec:authorize>
								<sec:authorize access="(hasRole('ADMIN') or hasRole('ROLE_SUPPLIER_NOTES'))">
									<!-- Add Note -->
									<div class="panel">
										<div class="panel-heading">
											<h4 class="panel-title">
												<a data-toggle="collapse" data-parent="#accordion" href="#collapseEight"><spring:message code="supplier.Notes" /> </a>
											</h4>
										</div>
										<div id="collapseEight" class="panel-collapse collapse">
											<div class="panel-body">
												<sec:authorize access="(hasRole('ADMIN') or hasRole('ROLE_SUPPLIER_NOTES_EDIT'))">
													<div class="Invited-Supplier-List create_sub note marg-bottom-20">
														<div class="col-md-8">
															<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/hover.css"/>">
															<input type="hidden" id="idSupplier" value="${supplier.id}" />
															<h3 class="blue_form_sbtitle pad-tb-30 add_file text-black"></h3>
															<c:if test="${not empty errors }">
																<div class="alert alert-danger" id="idGlobalError">
																	<div class="bg-red alert-icon">
																		<i class="glyph-icon icon-times"></i>
																	</div>
																	<div class="alert-content">
																		<h4 class="alert-title">Error</h4>
																		<p id="idGlobalErrorMessage">
																			<c:forEach var="error" items="${errors}">
																					${error}<br />
																			</c:forEach>
																		</p>
																	</div>
																</div>
															</c:if>
															<form id="noteForm">
																<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
																<div class="row marg-bottom-20">
																	<div class="col-md-3">
																		<label class="marg-top-10"><spring:message code="supplier.incident.type" /> :</label>
																	</div>
																	<div class="dd">
																		<select class="custom-select incidentType" name="" data-parsley-id="0644">
																			<option selected><spring:message code="supplier.complaint.opt" /></option>
																			<option><spring:message code="supplier.registered.opt" /></option>
																			<option><spring:message code="supplier.transacted.opt" /></option>
																		</select>
																	</div>
																</div>
																<div class="row marg-bottom-10">
																	<div class="col-md-3">
																		<label class="marg-top-10"><spring:message code="application.description" /> :</label>
																	</div>
																	<div class="form-group textarea">
																		<textarea placeholder='<spring:message code="costcenter.placeholder.description" />' name="" id="description" rows="3" class="form-control" data-validation="required length" data-validation-length="max500" data-parsley-id="7481"></textarea>
																		<ul class="parsley-errors-list" id="parsley-id-7481"></ul>
																	</div>
																</div>
																<div class="row marg-bottom-10">
																	<div class="col-md-3"></div>
																	<section class="step4_form selectfile">
																		<div class="col-md-3 marg-bottom-20 addnote marg-top-20" style="padding-left: 0px;">
																			<button class="btn btn-info ph_btn_midium btn-margin-top hvr-pop hvr-rectangle-out" type="button" id="saveNotes">
																				<spring:message code="supplier.add.note.btn" />
																			</button>
																		</div>
																	</section>
																</div>
															</form>
														</div>
													</div>
												</sec:authorize>
												<div class="row">
													<div class="col-xs-12">
														<div class="ph_tabel_wrapper scrolableTable_UserList">
															<table id="tableList" class="ph_table display table table-bordered noarrow" cellspacing="0" width="100%">
																<thead>
																	<tr>
																		<th><spring:message code="eventsummary.listdocuments.datetime" /></th>
																		<th><spring:message code="application.description" /></th>
																		<th><spring:message code="supplier.incident.type" /></th>
																	</tr>
																</thead>
																<tbody>
																	<td class="pad-left-15">${notes.createdDate}</td>
																	<td class="pad-left-15">${notes.description}</td>
																	<td class="pad-left-15">${notes.incidentType}</td>
																</tbody>
															</table>
														</div>
														<div id="morris-bar-yearly" class="graph" style="visibility: hidden"></div>
													</div>
												</div>
											</div>
										</div>
										<script type="text/javascript">
											$("#test-select").treeMultiselect({
												enableSelectAll : true,
												sortable : true
											});
										</script>
										<script type="text/javascript">
											$('document').ready(function() {
												var data = eval('${noteList}');
												$('#tableList').DataTable({
													'aaData' : data,
													"aoColumns" : [ {
														"mData" : "createdDate"
													}, {
														"mData" : "description"
													}, {
														"mData" : "incidentType"
													} ]
												});
											});
										</script>
									</div>
								</sec:authorize>
								<div class="panel">
									<div class="panel-heading">
										<h4 class="panel-title">
											<a data-toggle="collapse" data-parent="#accordion" href="#collapseNotesDoc"> <spring:message code="supplier.notes.docs" />
											</a>
										</h4>
									</div>
									<div id="collapseNotesDoc" class="panel-collapse collapse">
										<div class="panel-body">
											<input type="hidden" id="idSupplier" value="${supplier.id}" />
											<jsp:include page="/WEB-INF/views/jsp/supplier/SupplierNotesDocument.jsp"></jsp:include>
										</div>
									</div>
								</div>
<!-- 								PH-2724 => 1.1 -->
<!-- 								<div class="panel"> -->
<!-- 									<div class="panel-heading"> -->
<!-- 										<h4 class="panel-title"> -->
<%-- 											<a data-toggle="collapse" data-parent="#accordion" href="#collapseNine"> <spring:message code="supplier.event.participation" /> </a> --%>
<!-- 										</h4> -->
<!-- 									</div> -->
<!-- 									<div id="collapseNine" class="panel-collapse collapse accortab"> -->
<!-- 										<div class="panel-body"> -->
<!-- 											<table class="tabaccor" cellpadding="0" cellspacing="0" border="0"> -->
<!-- 												<thead> -->
<%-- 													<th class="pad-left-15"><strong><spring:message code="supplier.no.col" /></strong></th> --%>
<%-- 													<th><strong><spring:message code="application.description" /></strong></th> --%>
<%-- 													<th><strong><spring:message code="supplier.all.buyer.events" /></strong></th> --%>
<%-- 													<sec:authorize access="hasRole('BUYER')"> --%>
<%-- 														<th><strong><spring:message code="supplier.my.events" /></strong></th> --%>
<%-- 													</sec:authorize> --%>
<!-- 												</thead> -->
<!-- 												<tbody> -->
<!-- 													<tr> -->
<!-- 														<td class="pad-left-15">1</td> -->
<%-- 														<td><spring:message code="supplier.total.event.invited" /></td> --%>
<%-- 														<td>${totalEventInvited}</td> --%>
<%-- 														<sec:authorize access="hasRole('BUYER')"> --%>
<%-- 	 														<td>${totalMyEventInvited}</td> --%>
<%-- 														</sec:authorize> --%>

<!-- 													</tr> -->
<!-- 													<tr> -->
<!-- 														<td class="pad-left-15">2</td> -->
<%-- 														<td><spring:message code="supplier.total.event.participated" /></td> --%>
<%-- 														<td>${totalEventParticipated}</td> --%>
<%-- 														<sec:authorize access="hasRole('BUYER')"> --%>
<%-- 	 														<td>${totalMyEventParticipated}</td> --%>
<%-- 														</sec:authorize> --%>

<!-- 													</tr> -->
<!-- 													<tr> -->
<!-- 														Todo : now we are not calculate the awarded event so this will be calculated -->
<!-- 														<td class="pad-left-15">3</td> -->
<%-- 														<td><spring:message code="supplier.total.event.awarded" /></td> --%>
<%-- 														<td>${totalEventAwarded}</td> --%>
<%-- 														<sec:authorize access="hasRole('BUYER')"> --%>
<%-- 	 														<td>${totalMyEventAwarded}</td> --%>
<%-- 														</sec:authorize> --%>

<!-- 													</tr> -->
<!-- 												</tbody> -->
<!-- 											</table> -->
<!-- 										</div> -->
<!-- 									</div> -->
<!-- 								</div>								 -->
								<div>
									<jsp:include page="/WEB-INF/views/jsp/rfx/SupplierStatusSummaryEventAudit.jsp" />
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			 <!-- Performance Tab -->
			<jsp:include page="supplierPerformanceDetails.jsp"></jsp:include>
			
		<sec:authorize access="hasRole('BUYER')">
			<div class="row">
				<div class="col-md-5">
					<a href="${pageContext.request.contextPath}/buyer/importSupplier">
						<button class="btn btn-info ph_btn hvr-pop hvr-rectangle-out">
							<spring:message code="supplier.back.suppliers" />
						</button>
					</a>
				</div>
			</div>
		</sec:authorize>
		<c:if test="${supplier.status eq 'PENDING'}">
			<div class="row">
				<div class="col-md-5">
					<button class="btn btn-info ph_btn hvr-pop hvr-rectangle-out approve" onclick="doAjaxPost(event,'${supplier.id}', 'APPROVED')" ${canEdit ? '' : 'disabled=disabled'}>
						<spring:message code="buyer.dashboard.approve" />
					</button>
					<button class="btn btn-black ph_btn marg-left-10 hvr-pop hvr-rectangle-out1" data-toggle="modal" data-target="#rejectModel" onclick="javascript:document.getElementById('rejectId').value='${supplier.id}'" ; ${canEdit ? '' : 'disabled=disabled'}>
						<spring:message code="application.reject" />
					</button>
				</div>
			</div>
		</c:if>
	</div>
</div>
<div class="modal fade" id="rejectModel" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content" style="width: 135%">
			<div class="modal-header">
				<h3>Reject</h3>
				<button class="close for-absulate" data-dismiss="modal" type="button">x</button>
			</div>
			<div class="model_pop_mid">
				<c:url var="rejectDetails" value="supplierreg/rejectDetails" />
				<form class="bordered-row has-validation-callback" id="reject-form" name="reject-form" method="post" action="rejectDetails">
					<input id="rejectId" name="rejectId" type="hidden" value=""> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
					<div class="sup_pop_row">
						<div class="remark_text">
							<spring:message code="Product.remarks" />
						</div>
						<div class="remark_field">
							<textarea class="form-control rejectRemark" data-validation="length" data-validation-length="max500"></textarea>
							<span class="sky-blue"><spring:message code="dashboard.valid.max.characters" /></span>
						</div>
					</div>
					<div class="sup_pop_row">
						<div class="col-md-offset-2 col-md-10">
							<div class="col-md-5" style="padding-right: 2px">
								<button class="btn btn-info btn-block hvr-pop hvr-rectangle-out dis" id="idReject" type="submit">
									<spring:message code="application.reject" />
								</button>
							</div>
							<div class="col-md-5" style="padding-left: 2px">
								<button type="button" class="btn btn-black btn-block hvr-pop hvr-rectangle-out1" id="idRejectClose">
									<spring:message code="application.cancel" />
								</button>
							</div>
						</div>
					</div>
				</form>
			</div>
		</div>
	</div>
</div>
<!-- POPUP DELETE OTHER DOCUMENT -->
<div class="modal fade" id="myModalDelOtherDocConfirm" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="application.confirm.delete" />
				</h3>
			</div>
			<div class="modal-body">
				<label><spring:message code="suppliers.sure.delete.document" /></label> <input type="hidden" id="deleteOtherDocId" value="" /> <input type="hidden" id="otherDocFileName" value="" />
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<a type="button" href="javascript:void(0);" class="closeDialog btn btn-black marg-left-10 hvr-pop ph_btn_small hvr-rectangle-out1" data-dismiss="modal"><spring:message code="application.cancel" /></a>
				<button id="idConfirmDeleteOtherDocument" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out">
					<spring:message code="application.remove" />
				</button>
			</div>
		</div>
	</div>
</div>
<!-- Update communication email dialog -->
<div class="modal fade" title="Enter Comunication Email " id="editMailModal" role="dialog">
	<div class="modal-dialog box1">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="supplier.enternew.communcation.email" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">X</button>
			</div>
			<div class="modal-body">
				<div class="row">
					<label class="col-sm-2 control-label"> </label>
					<form id="updateEmailForm">
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
						<div class="col-sm-6 col-md-6">
							<input type="text" style="margin-top: 0;" class="form-control mar-b10" id="newEmail" data-validation="required email">
						</div>
						<div class="col-sm-4 col-md-4">
							<button id="submitEmail" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out marg-left-20" data-dismiss="modal">
								<spring:message code="application.submit" />
							</button>
						</div>
					</form>
				</div>
			</div>
		</div>
	</div>
</div>
<!-- Edit Company Name module -->
<div class="modal fade" title="Enter Company Name" id="modalCompanyName" role="dialog">
	<div class="modal-dialog box1">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="supplier.update.company.name" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">X</button>
			</div>
			<div class="modal-body">
				<div class="row">
					<label class="col-sm-2 control-label"> </label>
					<form id="updateCompNameForm">
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
						<div class="col-sm-6 col-md-6">
							<input type="text" style="margin-top: 0;" class="form-control mar-b10" id="newCompanyName" data-validation="required length alphanumeric" 
							data-validation-allowing=",-_ &.()'\/"
							data-validation-error-msg="The input value can only contain alphanumeric characters and ,-_ &.()'\/ and spaces"
							data-validation-length="4-124" >
						</div>
						<div class="col-sm-4 col-md-4">
							<button id="submitCompName" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out marg-left-20" data-dismiss="modal">
								<spring:message code="application.submit" />
							</button>
						</div>
					</form>
				</div>
			</div>
		</div>
	</div>
</div>
<!-- Edit Registration No module -->
<div class="modal fade" title="Enter Registration Number" id="modalRegNumber" role="dialog">
	<div class="modal-dialog box1">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="supplier.update.registration.no" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">X</button>
			</div>
			<div class="modal-body">
				<div class="row">
					<label class="col-sm-2 control-label"> </label>
					<form id="updateRegNoForm">
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
						<div class="col-sm-6 col-md-6">
							<input type="text" style="margin-top: 0;" class="form-control mar-b10" id="newRegNumber" data-validation="required length alphanumeric" 
							data-validation-allowing=" &.,/()_-" 
							data-validation-error-msg="The input value can only contain alphanumeric characters and ,-_&.()/ and spaces"
							data-validation-length="2-124" >
						</div>
						<div class="col-sm-4 col-md-4">
							<button id="submitRegNo" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out marg-left-20" data-dismiss="modal">
								<spring:message code="application.submit" />
							</button>
						</div>
					</form>
				</div>
			</div>
		</div>
	</div>
</div>
<script>
	$("#edit_mail").click(function() {
		if ($("#editEmailValue").val() != '') {
			$("#newEmail").val($("#editEmailValue").val());
		} else {
			$("#newEmail").val($(this).data("mail"));
		}
		$("#editMailModal").modal({
			backdrop : 'static',
			keyboard : false
		});
	});

	$("#submitEmail").click(function(e) {
		e.preventDefault();

		if (!$('#updateEmailForm').isValid()) {
			return false;
		}
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		var email = $("#newEmail").val()
		var supplierId = $('#supplierId').val();
		$.ajax({
			url : getContextPath() + "/updateSupplierComunicationEmail/" + supplierId + "/" + email,
			type : "POST",
			beforeSend : function(xhr) {
				$('#loading').show();
				xhr.setRequestHeader(header, token);
				console.log('sending request...');
			},
			success : function(data, textStatus, request) {
				$(".bEmail").html(email);
				$("#editEmailValue").val(email);
				$("#newEmail").val(email);
				var success = request.getResponseHeader('success');
				showMessage('SUCCESS', success);
				$('p[id=idGlobalSuccessMessage]').html(success);
				$('div[id=idGlobalSuccess]').show();
				$('div[id=idGlobalError]').hide();
				console.log('reponse received ...');
			},
			error : function(request, textStatus, errorThrown) {
				$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
				$('div[id=idGlobalError]').show();
				$('div[id=idGlobalSuccess]').hide();
				showMessage('ERROR', request.getResponseHeader('error'));
			},
			complete : function() {
				$('#loading').hide();
				console.log('all done...');
			}
		});
	});

	$('#idReject').click(function(event) {
		event.preventDefault();
		$('div[id=idGlobalSuccess]').hide();
		$('div[id=idGlobalError]').hide();

		var id = $('#rejectId').val();
		var data = {}
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		var data = {}
		data["id"] = id;
		console.log("ID  : " + id);
		$.ajax({
			type : "POST",
			url : getContextPath() + "/supplierreg/rejectSupplierDetails",
			data : JSON.stringify(data),
			beforeSend : function(xhr) {
				$('#loading').show();
				xhr.setRequestHeader(header, token);
				xhr.setRequestHeader("Accept", "application/json");
				xhr.setRequestHeader("Content-Type", "application/json");
			},
			complete : function() {
				$('#loading').hide();
			},
			success : function(data, textStatus, request) {
				$('#rejectModel').modal('hide');
				window.location.href = getContextPath() + "/supplierreg/supplierSignupList";
				$('p[id=idGlobalSuccessMessage]').html(request.getResponseHeader('success'));
				$('div[id=idGlobalSuccess]').show();
			},
			error : function(request, textStatus, errorThrown) {
				//alert('Error: ' + request.getResponseHeader('error'));
				$('#rejectModel').modal('hide');
			}
		});
	});

	$('#idResendSupplierActivationEmail').click(function(event) {
		$('#resendSupplierActivationEmail').submit();
	});

	$('#idRejectClose').click(function(event) {
		$('#rejectModel').modal('hide');
	});
	
	$("#editCompName").click(function() {
		if ($("#editCompanyName").val() != '') {
			$("#newCompanyName").val($("#editCompanyName").val());
		} else {
			$("#newCompanyName").val($(this).data("company"));
		}
		$("#modalCompanyName").modal({
			backdrop : 'static',
			keyboard : false
		});
	});
	
	$("#submitCompName").click(function(e) {
		e.preventDefault();

		if (!$('#updateCompNameForm').isValid()) {
			return false;
		}
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		var companyName = $("#newCompanyName").val()
		var supplierId = $('#supplierId').val();
		$.ajax({
			url : getContextPath() + "/updateSupplierCompanyName/" + supplierId,
			type : "POST",
			data : {
 				'companyName' : companyName
 			},
			beforeSend : function(xhr) {
				$('#loading').show();
				xhr.setRequestHeader(header, token);
				console.log('sending request for comp name...');
			},
			success : function(data, textStatus, request) {
				$(".bCompName").html(companyName);
				$("#editCompanyName").val(companyName);
				$("#newCompanyName").val(companyName);
				$("#currentCompName").text(companyName);
				$("#idFormerCompName").text("(formerly known as " +$("#idFormerCompName").attr('data-oldCompName') + ")");
				
				var success = request.getResponseHeader('success');
				showMessage('SUCCESS', success);
				$('p[id=idGlobalSuccessMessage]').html(success);
				$('div[id=idGlobalSuccess]').show();
				$('div[id=idGlobalError]').hide();
				console.log('reponse received for comp name...');
			},
			error : function(request, textStatus, errorThrown) {
				$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
				$('div[id=idGlobalError]').show();
				$('div[id=idGlobalSuccess]').hide();
				showMessage('ERROR', request.getResponseHeader('error'));
			},
			complete : function() {
				$('#loading').hide();
				console.log('all done...');
			}
		});
	});
	
	$("#editRegNo").click(function() {
		if ($("#editRegistrationNo").val() != '') {
			$("#newRegNumber").val($("#editRegistrationNo").val());
		} else {
			$("#newRegNumber").val($(this).attr("data-registnNumber"));
		}
		$("#modalRegNumber").modal({
			backdrop : 'static',
			keyboard : false
		});
	});
	
	$("#submitRegNo").click(function(e) {
		e.preventDefault();

		if (!$('#updateRegNoForm').isValid()) {
			return false;
		}
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		var regNumber = $("#newRegNumber").val()
		var supplierId = $('#supplierId').val();
		
		$.ajax({
			url : getContextPath() + "/updateSuppRegistrationNumber/" + supplierId,
			type : "POST",
			data : {
 				'regNumber' : regNumber
 			},
			beforeSend : function(xhr) {
				$('#loading').show();
				xhr.setRequestHeader(header, token);
				console.log('sending request for reg no...');
			},
			success : function(data, textStatus, request) {
				$(".bRegNo").html(regNumber);
				$("#editRegistrationNo").val(regNumber);
				$("#newRegNumber").val(regNumber);
				$("#currentRegNo").text(regNumber);
				$("#idFormerRegNo").text("(formerly registered as " +$("#idFormerRegNo").attr('data-oldRegNo') + ")");
	
				var success = request.getResponseHeader('success');
				showMessage('SUCCESS', success);
				$('p[id=idGlobalSuccessMessage]').html(success);
				$('div[id=idGlobalSuccess]').show();
				$('div[id=idGlobalError]').hide();
				console.log('reponse received for reg no...');
			},
			error : function(request, textStatus, errorThrown) {
				$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
				$('div[id=idGlobalError]').show();
				$('div[id=idGlobalSuccess]').hide();
				showMessage('ERROR', request.getResponseHeader('error'));
			},
			complete : function() {
				$('#loading').hide();
				console.log('all done...');
			}
		});
	});
</script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/js-core/token-input.css"/>" />
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/content-box/contentbox.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.tokeninput.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/view/supplierEngagement.js?6"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/view/Notes.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/file-input/file-input.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/datepicker/datepicker.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/timepicker/timepicker.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/timepicker/timepicker.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/moment.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker-demo.js?1"/>"></script>
<script type="text/javascript">

var isBuyer = false;
<sec:authorize access="hasRole('BUYER')">
isBuyer = true;
</sec:authorize>

	$(document).ready(function() {
		$.validate({
			lang : 'en',
			modules : 'file'
		});
		
		$('#datepickerAnalytics').daterangepicker({
			format : 'DD/MM/YYYY h:mm A',
		});
		
	});

	$(function() {
		"use strict";

		$('#expiryDate').bsdatepicker({
			format : 'dd/mm/yyyy',
			onRender : function(date) {
				if (date.valueOf() < $.now()) {
					return 'disabled';
				}
			}

		}).on('changeDate', function(e) {
			$(this).blur();
			$(this).bsdatepicker('hide');
		});

		$('#extensionDate').bsdatepicker({
			format : 'dd/mm/yyyy',
			onRender : function(date) {
				if($('#supplierEndDate').val()) {
					var now = new Date($('#supplierEndDate').val().split('/')[2], Number($('#supplierEndDate').val().split('/')[1])-1, $('#supplierEndDate').val().split('/')[0]);
					if (date.valueOf() < now.valueOf()) {
						return 'disabled';
					}
				}else {
					if (date.valueOf() < $.now()) {
						return 'disabled';
					}
				}
			}

		}).on('changeDate', function(e) {
			$(this).blur();
			$(this).bsdatepicker('hide');
		}).on('show', function(e) {
			$('#extensionDate').bsdatepicker("update", $('#supplierEndDate').val());
		});

		$.formUtils.addValidator({
		name : 'validate_date',
		validatorFunction : function(value, $el, config, language, $form) {
			var now = new Date($('#supplierEndDate').val().split('/')[2], Number($('#supplierEndDate').val().split('/')[1])-1, $('#supplierEndDate').val().split('/')[0]);
			// new Date("01/02/2020").getTime()
			var then = new Date(value.split('/')[2], Number(value.split('/')[1])-1, value.split('/')[0]);
			if (now.valueOf() > then.valueOf() ) {
				return false;
			} else {
				return true;
			}
		},
		errorMessage : 'The subscription end date cannot be less than the current subscription date.',
		errorMessageKey : 'validateDate'
	});

		
	});
</script>

<style>
.box1 {
	width: 600px;
	height: 100px;
	margin-left: 500px;
}

.m-bottom-20
{
	margin-bottom: 20px;
}

.word-break
{
	word-break: break-all;
}
.buttons-row
{
	display: flex;
	justify-content: center;
	margin-bottom: 15px;
}

.disable-input
{
	background: #f2f2f2 none repeat scroll 0 0;cursor: not-allowed;
}
.byBuyer {
  border: 2px solid black;
  border-collapse: collapse;
  width: 50%;
}

.byBuyertbl-style {
	width: 50% !important ;
    border: ridge;
}
.hdn-clr {
	background-color: #b9cde5 !important;
	height: 25px;
}
.byFormtbl-style {
    width: 80% !important;
    border: ridge;
}

.byCriteriatbl-style {
    width: 55% !important;
    border: ridge;
}

.width75 {
    width: 75% !important;
}

.eventPart-style {
	width: 55% !important ;
    border: ridge;
}
</style>
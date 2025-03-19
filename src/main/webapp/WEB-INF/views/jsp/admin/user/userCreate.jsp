<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<sec:authorize access="hasRole('USER_EDIT') or hasRole('ADMIN')" var="canEdit" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<sec:authorize access="hasRole('BUYER')">
	<spring:message var="userCreateDesk" code="application.user.create" />
	<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${userCreateDesk}] });
});
</script>
</sec:authorize>
<sec:authorize access="hasRole('SUPPLIER')">
	<spring:message var="supplierUserCreateDesk" code="application.supplier.user.create" />
	<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${supplierUserCreateDesk}] });
});
</script>
</sec:authorize>
<sec:authorize access="hasRole('OWNER')">
	<spring:message var="ownerUserCreateDesk" code="application.owner.user.create" />
	<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${ownerUserCreateDesk}] });
});
</script>
</sec:authorize>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<div id="page-content" view-name="user">
	<title>${btnValue}User</title>
	<div class="col-md-offset-1 col-md-10">
		<ol class="breadcrumb" style="margin-left: 10px">
			<sec:authorize access="hasRole('BUYER')">
				<li>
					<a id="dashboardLink" href="${pageContext.request.contextPath}/buyer/buyerDashboard""><spring:message code="application.dashboard" /></a>
				</li>
			</sec:authorize>
			<sec:authorize access="hasRole('SUPPLIER')">
				<li>
					<a id="dashboardLink" href="${pageContext.request.contextPath}/supplier/supplierDashboard""><spring:message code="application.dashboard" /></a>
				</li>
			</sec:authorize>
			<sec:authorize access="hasRole('OWNER')">
				<li>
					<a id="dashboardLink" href="${pageContext.request.contextPath}/owner/ownerDashboard""><spring:message code="application.dashboard" /></a>
				</li>
			</sec:authorize>
			<li>
				<a id="listLink" href="${pageContext.request.contextPath}/admin/listUser"><spring:message code="user.list" /></a>
			</li>
			<li class="active">
				<c:out value='${btnValue}' />
				<spring:message code="appication.user" />
			</li>
		</ol>
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap manage_icon">
				<spring:message code="user.administration" />
			</h2>
		</div>
		<!-- page title block -->
		<div class="Invited-Supplier-List import-supplier white-bg">
			<div class="meeting2-heading">
				<h3>
					<c:out value='${btnValue}' />
					<spring:message code="appication.user" />
				</h3>
			</div>
			<div class="import-supplier-inner-first-new pad_all_15 global-list">
				<c:url var="user" value="user" />
				<form:form id="userRegistration" method="post" cssClass="form-horizontal bordered-row" action="user" modelAttribute="userObj" autocomplete="false">
					<header class="form_header"> </header>
					<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="loginId" class="marg-top-10">
								<spring:message code="buyer.profilesetup.loginmail" />
							</form:label>
						</div>
						<div class="col-md-5">
							<form:hidden path="id" />
							<spring:message code="profilesetting.login.emailid.placeholder" var="loginIdLabel" />
							<form:input path="loginId" readonly="${editMode}" data-validation="required length email" data-validation-length="5-50" data-validation-error-msg-length="Login id values must be between 5 to 50." id="idLoginId" class="form-control" placeholder="${loginIdLabel}"></form:input>
							<form:errors path="loginId" cssClass="error" />
						</div>
					</div>
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="name" class="marg-top-10">
								<spring:message code="application.username" />
							</form:label>
						</div>
						<div class="col-md-5">
							<spring:message code="application.username.placeholder" var="usernameLabel" />
							<form:input path="name" cssClass="form-control" data-validation-regexp="^[^\s][a-zA-Z0-9\s]+$" data-validation="required,length,custom" data-validation-length="4-160" data-validation-error-msg-length="User name value must be between 4 to 160." id="idUserName" placeholder="${usernameLabel}"></form:input>
							<form:errors path="name" cssClass="error" />
						</div>
					</div>
					<c:if test="${!empty userObj.id}">
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<label class="marg-top-10"><spring:message code="application.password" /> </label>
						</div>
						<div class="col-md-5">
							<spring:message code="changepassword.custom" var="custom" />
							<c:set var="rgx" value="^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$@$!%*?&])[A-Za-z\\d$@$!%*?&]{8,15}$"></c:set>
							<input type="password" Class="form-control" data-validation="required custom length" data-validation-length="max15" ${!empty userObj.id ? 'data-validation-optional="true"' : ''} placeholder='<spring:message code="application.password.placeholder"/>' data-validation-regexp=${!empty regex ? regex.regx :rgx} data-validation-error-msg-custom="${!empty regex.message ? regex.message:custom}" id="idUserPassword" name="userPassword" />
						</div>
					</div>
					</c:if>
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="communicationEmail" class="marg-top-10">
								<spring:message code="buyer.profilesetup.commemail" />
							</form:label>
						</div>
						<div class="col-md-5">
							<spring:message code="buyer.profilesetup.commemail" var="commMailLabel" />
							<form:input path="communicationEmail" data-validation="required length email" data-validation-length="5-50" data-validation-error-msg-length="Communication email must be between 5 to 50." id="idCommunicationEmail" class="form-control" placeholder="${commMailLabel}"></form:input>
						</div>
					</div>
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="designation" class="marg-top-10">
								<spring:message code="supplier.designation" />
							</form:label>
						</div>
						<div class="col-md-5">
							<spring:message code="user.designation.placeholder" var="designationLabel" />
							<form:input path="designation" data-validation="required" cssClass="form-control" id="iddesignation" placeholder="${designationLabel}"></form:input>
							<form:errors path="designation" cssClass="error" />
						</div>
					</div>
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="phoneNumber" class="marg-top-10">
								<spring:message code="application.contactno" />
							</form:label>
						</div>
						<div class="col-md-5">
							<spring:message code="buyercreation.contact" var="contactNoLabel" />
							<form:input path="phoneNumber" cssClass="form-control" id="idPhoneNumber" placeholder="${contactNoLabel}"></form:input>
							<form:errors path="phoneNumber" cssClass="error" />
						</div>
					</div>
					<sec:authorize access="hasRole('BUYER')">
						<div class="row marg-bottom-20">
							<div class="col-md-3">
								<label class="marg-top-10"><spring:message code="application.usertype" /> </label>
							</div>
							<div class="col-md-5">
								<form:select path="userType" class="form-control chosen-select disablesearch" id="userType">
									<form:option value="NORMAL_USER" label="Normal User" />
									<form:option value="APPROVER_USER" label="Approver User" />
									<form:option value="REQUESTOR_USER" label="Requestor User" />
									<%-- <form:options items="${userType}" /> --%>
								</form:select>
							</div>
						</div>
					</sec:authorize>
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="userRole" class="marg-top-10">
								<spring:message code="userrole.label" />
							</form:label>
						</div>
						<div class="col-md-5 ${isAdmnstrLck ? 'disabled' : ''}">
							<form:select path="userRole" id="idUserRole" data-validation="required" cssClass="chosen-select">
								<form:option value="">
									<spring:message code="user.select" />
								</form:option>
								<form:options items="${userRole}" itemValue="id" itemLabel="roleName"></form:options>
							</form:select>
			    		</div>
					</div>
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<label class="marg-top-10"><spring:message code="label.industrycategory.status" /> </label>
						</div>
						<div class="col-md-5 ${isAdmnstrLck ? 'disabled' : ''} ">
							<form:select path="active" class="form-control chosen-select disablesearch">
								<form:option value="true" label="ACTIVE" />
								<form:option value="false" label="INACTIVE" />
							</form:select>
						</div>
					</div>
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<label class="marg-top-10"><spring:message code="createuser.account.locked" /> </label>
						</div>
						<div class="col-md-5">
							<div class="checkbox checkbox-info">
								<label> <form:checkbox id="idLocked" checked="${checked}" path="locked" class="custom-checkbox" />
								</label>
							</div>
						</div>
					</div>
					<sec:authorize access="hasRole('BUYER')">
						<div class="row marg-bottom-20">
							<div class="col-md-12">
							        <!--CR 4105-->
								<div class="panel">
                                    <div class="panel-heading">
                                        <h4 class="panel-title">
                                            <a data-toggle="collapse" data-parent="#accordion" href="#collapseBusinessUnit">
                                                <spring:message code="createuser.assign.business.unit" />
                                            </a>
                                        </h4>
                                    </div>
                                    <div id="collapseBusinessUnit" class="panel-collapse in collapse">
                                        <div class="container-fluid">
                                            <div class="col_12">
                                                <section class="index_table_block marg-top-20 marg-bottom-20">
                                                    <div class="row">
                                                        <div class="col-xs-12">
                                                            <div class="main_table_wrapper ph_table_border mega">
                                                            <!-- Table Header with Search Bar -->
                                                            <table id="tableList" width="100%" class="ph_table border-none header table-1" border="0" cellspacing="0" cellpadding="0">
                                                                <thead>
                                                                    <tr>
                                                                        <th class="align-center width_50_fix">
                                                                            <form:checkbox id="inlineCheckbox114" class="custom-checkbox buCheckAllCheckbox" name="tempname" path="" value=""  />
                                                                        </th>
                                                                        <th class="align-left width_300">
                                                                            <strong><spring:message code="label.businessUnitName"/></strong>
                                                                            <!-- Search Bar Inside Table Header -->
                                                                            <input type="text" id="searchInput" placeholder="Search Business Units" onkeyup="filterTable()" class="header-search-input" />
                                                                        </th>
                                                                    </tr>
                                                                </thead>
                                                            </table>
                                                                <div id="businessUnitContainer">
                                                                    <!-- Data Table -->
                                                                    <table width="100%" class="data ph_table border-none table-2" border="0" cellspacing="0" cellpadding="0">

                                                                      <tbody id="businessUnitTable">
                                                                        <c:forEach var="businessUnit" items="${bizList}" varStatus="status">
                                                                            <tr>
                                                                                <td class="align-center width_50_fix">
                                                                                     <form:checkbox id="inlineCheckbox113" path="assignedBusinessUnits" value="${businessUnit.id}" class="custom-checkbox buSubCheckBoxes" onchange="validateBusinessUnitSelection()" />
                                                                                </td>
                                                                                <td class="align-left width_300">${businessUnit.unitName}</td>
                                                                            </tr>
                                                                        </c:forEach>
                                                                      </tbody>
                                                                    </table>
                                                                </div>

                                                        </div>
                                                      </div>
                                                    </div>
                                                </section>
                                                <p id="businessUnitErrorMsg" style="color: red; display: none;">Please select at least one business unit.</p>
                                            </div>
                                        </div>
                                    </div>
                                </div>
								<div class="panel">
									<div class="panel-heading">
										<h4 class="panel-title">
											<a data-toggle="collapse" data-parent="#accordion" href="#collapseSourcingTemplate"><spring:message code="createuser.assign.sourcing.template" /></a>
										</h4>
									</div>
									<div id="#collapseSourcingTemplate" class="panel-collapse in collapse">
										<div class="container-fluid">
											<div class="col_12">
												<section class="index_table_block marg-top-20 marg-bottom-20">
													<div class="row">
														<div class="col-xs-12">
															<div class="main_table_wrapper ph_table_border mega">
																<table id="tableList" width="100%" class="ph_table border-none header table-1" border="0" cellspacing="0" cellpadding="0">
																	<thead>
																		<tr>
																			<th class="align-center width_50_fix"><form:checkbox id="inlineCheckbox114" class="custom-checkbox srCheckAllCheckbox" name="tempname" path="" value="" /></th>
																			<th class="align-left width_200"><strong><spring:message code="rfxTemplate.templateName" /></strong></th>
																			<th class="align-left width_300"><strong><spring:message code="rfxTemplate.templateDescription" /></strong></th>
																		</tr>
																	</thead>
																</table>
																<table width="100%" class="data ph_table border-none table-2" border="0" cellspacing="0" cellpadding="0">
																	<tbody>
																		<c:forEach var="sourcingTemplate" items="${sourcingTemplateList}" varStatus="status">
																			<tr>
																				<td class="align-center width_50_fix">
																					<form:checkbox id="inlineCheckbox113" path="assignedSourcingTemplates" value="${sourcingTemplate.id}" class="custom-checkbox srSubCheckBoxes" />
																				</td>
																				<td class="align-left width_200">${sourcingTemplate.formName}</td>
																				<td class="align-left width_300">${sourcingTemplate.description}</td>
																			</tr>
																		</c:forEach>
																	</tbody>
																</table>
															</div>
														</div>
													</div>
												</section>
											</div>
										</div>
									</div>
								</div>
							
								<div class="panel">
									<div class="panel-heading">
										<h4 class="panel-title">
											<a data-toggle="collapse" data-parent="#accordion" href="#collapseSeven"><spring:message code="createuser.assign.rfx.template" /></a>
										</h4>
									</div>
									<div id="collapseSeven" class="panel-collapse in collapse">
										<div class="container-fluid">
											<div class="col_12">
												<section class="index_table_block marg-top-20 marg-bottom-20">
													<div class="row">
														<div class="col-xs-12">
															<div class="main_table_wrapper ph_table_border mega">
																<table id="tableList" width="100%" class="ph_table border-none header table-1" border="0" cellspacing="0" cellpadding="0">
																	<thead>
																		<tr>
																			<th class="width_50 width_50_fix align-center"><form:checkbox id="inlineCheckbox114" class="custom-checkbox checkAllCheckbox" name="tempname" path="" value="" /></th>
																			<th class="width_200 width_200_fix align-left"><strong><spring:message code="rfxTemplate.templateName" /></strong></th>
																			<th class="width_200 width_200_fix align-left"><strong><spring:message code="rfxTemplate.templateDescription" /></strong></th>
																			<th class="width_150_fix align-center"><strong><spring:message code="createuser.template.type" /></strong></th>
																		</tr>
																	</thead>
																</table>
																<table width="100%" class="data ph_table border-none table-2" border="0" cellspacing="0" cellpadding="0">
																	<tbody>
																		<c:forEach var="temp" items="${templateList}" varStatus="status">
																			<tr>
																				<td class="width_50_fix align-center">
																					<form:checkbox id="inlineCheckbox113" path="assignedTemplates" value="${temp.id}" class="custom-checkbox subCheckBoxes" />
																				</td>
																				<td class="width_200 width_200_fix align-left">${temp.templateName}</td>
																				<td class="width_200 width_200_fix align-left">${temp.templateDescription}</td>
																				<td class="width_150_fix align-center">${temp.type}</td>
																			</tr>
																		</c:forEach>
																	</tbody>
																</table>
															</div>
														</div>
													</div>
												</section>
											</div>
										</div>
									</div>
								</div>
								<div class="panel">
									<div class="panel-heading">
										<h4 class="panel-title">
											<a data-toggle="collapse" data-parent="#accordion" href="#collapsePrTemplate"><spring:message code="createuser.assign.pr.template" /></a>
										</h4>
									</div>
									<div id="collapsePrTemplate" class="panel-collapse in collapse">
										<div class="container-fluid">
											<div class="col_12">
												<section class="index_table_block marg-top-20 marg-bottom-20">
													<div class="row">
														<div class="col-xs-12">
															<div class="main_table_wrapper ph_table_border mega">
																<table id="tableList" width="100%" class="ph_table border-none header table-1" border="0" cellspacing="0" cellpadding="0">
																	<thead>
																		<tr>
																			<th class="align-center width_50_fix"><form:checkbox id="inlineCheckbox114" class="custom-checkbox prCheckAllCheckbox" name="tempname" path="" value="" /></th>
																			<th class="align-left width_200"><strong><spring:message code="rfxTemplate.templateName" /></strong></th>
																			<th class="align-left width_300"><strong><spring:message code="rfxTemplate.templateDescription" /></strong></th>
																		</tr>
																	</thead>
																</table>
																<table width="100%" class="data ph_table border-none table-2" border="0" cellspacing="0" cellpadding="0">
																	<tbody>
																		<c:forEach var="prTemplate" items="${prTempList}" varStatus="status">
																			<tr>
																				<td class="align-center width_50_fix">
																					<form:checkbox id="inlineCheckbox113" path="assignedPrTemplates" value="${prTemplate.id}" class="custom-checkbox prSubCheckBoxes" />
																				</td>
																				<td class="align-left width_200">${prTemplate.templateName}</td>
																				<td class="align-left width_300">${prTemplate.templateDescription}</td>
																			</tr>
																		</c:forEach>
																	</tbody>
																</table>
															</div>
														</div>
													</div>
												</section>
											</div>
										</div>
									</div>
								</div>
								<div class="panel">
									<div class="panel-heading">
										<h4 class="panel-title">
											<a data-toggle="collapse" data-parent="#accordion" href="#collapseSPTemplate"><spring:message code="createuser.assign.sp.template" /></a>
										</h4>

									</div>
									<div id="#collapseSPTemplate" class="panel-collapse in collapse">
										<div class="container-fluid">
											<div class="col_12">
												<section class="index_table_block marg-top-20 marg-bottom-20">
													<div class="row">
														<div class="col-xs-12">
															<div class="main_table_wrapper ph_table_border mega">
																<table id="tableList" width="100%" class="ph_table border-none header table-1" border="0" cellspacing="0" cellpadding="0">
																	<thead>
																		<tr>
																			<th class="align-center width_50_fix"><form:checkbox id="inlineCheckbox114" class="custom-checkbox sptCheckAllCheckbox" name="tempname" path="" value="" /></th>
																			<th class="align-left width_200"><strong><spring:message code="rfxTemplate.templateName" /></strong></th>
																			<th class="align-left width_300"><strong><spring:message code="rfxTemplate.templateDescription" /></strong></th>
																		</tr>
																	</thead>
																</table>
																<table width="100%" class="data ph_table border-none table-2" border="0" cellspacing="0" cellpadding="0">
																	<tbody>
																		<c:forEach var="tempt" items="${spTempList}" varStatus="status">
																			<tr>
																				<td class="align-center width_50_fix">
																					<form:checkbox id="inlineCheckbox113" path="assignedSupplierPerformanceTemplates" value="${tempt.id}" class="custom-checkbox sptSubCheckBoxes" />
																				</td>
																				<td class="align-left width_200">${tempt.templateName}</td>
																				<td class="align-left width_300">${tempt.templateDescription}</td>
																			</tr>
																		</c:forEach>
																	</tbody>
																</table>
															</div>
														</div>
													</div>
												</section>
											</div>
										</div>
									</div>
								</div>
													<div style="display: none;">
                                                        <c:forEach var="businessUnit" items="${bizList}">
                                                            <p>ID: <c:out value="${businessUnit.id}" />, Name: <c:out value="${businessUnit.unitName}" /></p>
                                                        </c:forEach>
                                                    </div>
								
							</div>
						</div>
					</sec:authorize>

					<div class="col-md-12 dd sky mar_b_15 marg-top-20 align-center">
						<form:button type="submit" value="save" id="saveUser" class="btn btn-info marg-right-10 ph_btn_midium hvr-pop hvr-rectangle-out ${canEdit and !buyerReadOnlyAdmin ? '':'disabled'}">${btnValue}</form:button>
						<c:url value="/admin/listUser" var="cancelUrl" />
						<a href="${cancelUrl}" class="btn btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium button-previous open1"><spring:message code="application.cancel" /></a>
					</div>
				</form:form>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.js"/>"></script>
<script>
	$.validate({
		lang : 'en'
	});
</script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.mask.min.js"/>"></script>
<script>
	$(document).ready(function() {
		$(".pwd").click(function(){
			$('#passwordPlaceHolder').hide();
			});
		var regex;
		$('#idPhoneNumber').mask('+00 00000000000', {
			placeholder : "e.g. +60 352735465"
		});
	});
	
	<c:if test="${buyerReadOnlyAdmin}">
	$(window).bind('load', function() {
		var allowedFields = '.button-previous, #dashboardLink, #listLink';
		//var disableAnchers = ['#reloadMsg'];        
		disableFormFields(allowedFields);
		$('#page-content').find('select').not(allowedFields).parent('div').addClass('disabled');
	});
	</c:if>
	
</script>
<script type="text/javascript" src="<c:url value="/resources/js/view/user.js?2"/>"></script>
<script>
        function filterTable() {
            var input, filter, table, tbody, tr, td, i, txtValue;
            input = document.getElementById('searchInput');
            filter = input.value.toUpperCase();
            table = document.getElementById('businessUnitTable');
            tr = table.getElementsByTagName('tr');

            for (i = 0; i < tr.length; i++) {
                td = tr[i].getElementsByTagName('td')[1]; // Index 1 for the business unit name column
                if (td) {
                    txtValue = td.textContent || td.innerText;
                    if (txtValue.toUpperCase().indexOf(filter) > -1) {
                        tr[i].style.display = "";
                    } else {
                        tr[i].style.display = "none";
                    }
                }
            }
        }
</script>

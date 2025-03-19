<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!-- WIDGETS -->
<!-- Admin theme -->
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/privasia1.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/hover.css"/>">
<!-- JS Core -->
<%-- <script type="text/javascript" src="<c:url value="/resources/assets/js-core/animated-search-filter.js"/>"></script> --%>
<script src="<c:url value="/resources/assets/widgets/jquery-ui.min/jquery-ui.min.js"/>"></script>
<script src="<c:url value="/resources/assets/widgets/tree-multiselect/jquery.tree-multiselect.js"/>"></script>
<link rel="stylesheet" href="<c:url value="/resources/assets/widgets/tree-multiselect/jquery.tree-multiselect.min.css"/>">
<%--     <script type="text/javascript" src="<c:url value="/resources/assets/bootstrap/js/bootstrap-filestyle.min.js"/>"></script> --%>
	
<%-- <script type="text/javascript" src="<c:url value="/resources/assets/js-core/animated-search-filter.js"/>"></script> --%>
<script type="text/javascript">
	//$(":file").filestyle({buttonName: "btn-black"});
</script>
<spring:message var="suppRegGeneralInfoDesk" code="application.supplier.registration.general.info" />
<spring:message var="suppRegCategoryDesk" code="application.supplier.registration.category" />
<spring:message var="suppRegDeclarationDesk" code="application.supplier.registration.declaration" />
<spring:message var="suppRegCompanyProfDesk" code="application.supplier.registration.company.profile" />
<spring:message var="suppRegFinancialInfoDesk" code="application.supplier.registration.financial.info" />
<spring:message var="suppRegOrgDetailsDesk" code="application.supplier.registration.org.details" />
<spring:message var="suppRegTrackRecordsDesk" code="application.supplier.registration.track.records" />

<script>
$(document).ready(function() {
    $('.mega').on('scroll', function() {
        $(this).find('.header').css('top', $(this).scrollTop());

	});
});
</script>
<style type="text/css">
.leftSideOfCheckbox {
	width: 47%;
	float: left;
	border-right: 1px solid #d8d8d8;
	margin: 0 2% 0 0;
	height: 300px;
	overflow-y: auto;
}

.rightSideOfCheckbox {
	width: 50%;
	float: left;
	height: 300px;
	overflow-y: auto;
}

.chosen-select {
	display: block !important;
	position: absolute;
	opacity: 0;
}

.chosen-select.error+.chosen-container {
	border: 1px solid #b94a48;
}
</style>
<div id="page-content-wrapper">
	<section id="admin_regSteps_wrapper">
		<div class="container-fluid">
			<div class="row">
				<div class="col-xs-12">
					<section class="admin_wizard_step">
						<h2 class="adm_wzard_title text-center">Companies that fill up complete profile information stand to generate 3x more business</h2>
						<div class="example-box-wrapper">
							<div id="form-wizard-2" class="form-wizard">
								<ul>
									<li class="tb_1 active">
										<a href="#step-1" class="open11" data-move="0">
											<!--  href="#step-1" -->
											<label class="wizard-step"> <span class="inner_circle">
													<img src="${pageContext.request.contextPath}/resources/assets/image-resources/image-procurehere/right-mark.png" />
												</span>
											</label>
											<span class="wizard-description">General Company Info </span>
										</a>
									</li>
									<li class="tb_2 ">
										<a href="#step-2" class="open21" data-move="0">
											<!--  href="#step-2" -->
											<label class="wizard-step"><span class="inner_circle">
													<span class="step_num">2</span>
													<img src="${pageContext.request.contextPath}/resources/assets/image-resources/image-procurehere/right-mark.png" class="step_checkmark" />
												</span></label>
											<span class="wizard-description">NAICS Category </span>
										</a>
									</li>
									<li class="tb_3">
										<a href="#step-3" class="open31" data-move="0">
											<!--  href="#step-3" -->
											<label class="wizard-step"><span class="inner_circle">
													<span class="step_num">3</span>
													<img src="${pageContext.request.contextPath}/resources/assets/image-resources/image-procurehere/right-mark.png" class="step_checkmark" />
												</span></label>
											<span class="wizard-description">Declaration </span>
										</a>
									</li>
									<li class="tb_4">
										<a href="#step-4" class="open41" data-move="0">
											<!--  href="#step-4" -->
											<label class="wizard-step"><span class="inner_circle">
													<span class="step_num">4</span>
													<img src="${pageContext.request.contextPath}/resources/assets/image-resources/image-procurehere/right-mark.png" class="step_checkmark" />
												</span></label>
											<span class="wizard-description">Company Profile (Optional) </span>
										</a>
									</li>
									<li class="tb_5">
										<a href="#step-5" class="open51" data-move="0">
											<!--  href="#step-5" -->
											<label class="wizard-step"><span class="inner_circle">
													<span class="step_num">5</span>
													<img src="${pageContext.request.contextPath}/resources/assets/image-resources/image-procurehere/right-mark.png" class="step_checkmark" />
												</span></label>
											<span class="wizard-description">Financial Information (Optional) </span>
										</a>
									</li>
									<li class="tb_6">
										<a href="#step-6" class="open61" data-move="0">
											<label class="wizard-step"><span class="inner_circle">
													<span class="step_num">6</span>
													<img src="${pageContext.request.contextPath}/resources/assets/image-resources/image-procurehere/right-mark.png" class="step_checkmark" />
												</span></label>
											<span class="wizard-description">Organizational Details (Optional) </span>
										</a>
									</li>
									<li class="tb_7">
										<a href="#step-7" class="open71" data-move="0">
											<!--  href="#step-7" -->
											<label class="wizard-step"><span class="inner_circle">
													<span class="step_num">7</span>
													<img src="${pageContext.request.contextPath}/resources/assets/image-resources/image-procurehere/right-mark.png" class="step_checkmark" />
												</span></label>
											<span class="wizard-description">Services &amp; Track Records (Optional) </span>
										</a>
									</li>
								</ul>
								<div class="tab-content">
									<c:url var="registration" value="/supplierProfile" />
									<form:form class="bordered-row form-horizontal" id="demo-form1" data-parsley-validate="" method="post" modelAttribute="supplier" action="${registration}?${_csrf.parameterName}=${_csrf.token}" enctype="multipart/form-data">
										<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
										<form:hidden path="designation" />
                                  <%-- 	<form:hidden path="mobileNumber" />   PH-376--%>  
										<form:hidden path="password" />
										<form:hidden path="loginEmail" />
										<form:hidden path="fullName" />
										<form:hidden path="id" />
										<form:hidden path="status" />
										<form:hidden path="registrationDate" />
										<form:hidden path="remarks" />
										<div class="tab-pane active" id="step-1">
											<div class="content-box">
												<h3 class="content-box-header">
													General Company Info <small class="sub_text">As an Administrator, you may view and edit information freely.</small>
												</h3>
												<div class="content-box-wrapper">
													<div class="form-horizontal">
														<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
														<h3 class="blue_form_sbtitle">Basic Information :</h3>
														<div class="form-group">
															<label for="idCompanyName" class="col-sm-3 control-label">Company Name :</label>
															<div class="col-sm-6 col-md-5">
																<form:input path="companyName" cssClass="form-control" id="idCompanyName" name="idCompanyName" placeholder="ABC Company" readonly="true" />
															</div>
														</div>
														<div class="form-group">
															<label for="idCompanyRegNumber" class="col-sm-3 control-label"><spring:message code="supplier.registration.company.number" /> :</label>
															<div class="col-sm-6 col-md-5">
																<form:input path="companyRegistrationNumber" cssClass="form-control" name="idCompanyRegNumber" id="idCompanyRegNumber" placeholder="" readonly="true" />
															</div>
														</div>
														<div class="form-group">
															<label class="col-sm-3 control-label"> Year Established:</label>
															<div class="col-sm-6 col-md-5">
																<form:input path="yearOfEstablished" cssClass="form-control" id="idYearEst" data-validation="required length number year_established" data-validation-length="min4-max4" />
															</div>
														</div>
														<div class="form-group">
															<label for="idTelPhone" class="col-sm-3 control-label">Telephone Number :</label>
															<div class="col-sm-6 col-md-5">
																<form:input type="tel" path="companyContactNumber" cssClass="form-control" id="idTelPhone" placeholder="" data-validation="required length number" data-validation-ignore="+ " data-validation-length="6-14" />
															</div>
														</div>
														<div class="form-group">
															<label for="idFax" class="col-sm-3 control-label">Fax Number :</label>
															<div class="col-sm-6 col-md-5">
																<form:input path="faxNumber" cssClass="form-control" name="faxm" id="idFax" placeholder="" data-validation="required length number" data-validation-ignore="+ " data-validation-length="6-14" />
															</div>
														</div>
														<div class="form-group">
															<label for="idAdMoNo" class="col-sm-3 control-label"><spring:message code="suplier.primaryMobileNo" /> :</label>
															<div class="col-sm-6 col-md-5">
																<form:input path="mobileNumber" cssClass="form-control" id="idAdMoNo" placeholder="" data-validation="required length" data-validation-ignore="+ " data-validation-length="6-14" />
															</div>
														</div>
														<div class="form-group">
															<label for="idCompanyWebsite" class="col-sm-3 control-label"><spring:message code="supplier.company.website" /> :</label>
															<div class="col-sm-6 col-md-5">
																<form:input path="companyWebsite" cssClass="form-control" id="idCompanyWebsite" placeholder="e.g. yourcompany.com" data-validation="domain length" data-validation-length="5-128" data-validation-optional="true" />
															</div>
														</div>
														<div class="form-group">
															<label for="idCompanyEmail" class="col-sm-3 control-label">Company Email :</label>
															<div class="col-sm-6 col-md-5">
																<form:input path="communicationEmail" cssClass="form-control" id="idCompanyEmail" placeholder="" data-validation="required length email" data-validation-length="max128" />
															</div> 
														</div>
														<div class="form-group">
															<label for="idCompanyStatus" class="col-sm-3 control-label"><spring:message code="label.companystatus" /> :</label>
															<div class="col-sm-6 col-md-5">
																<form:select path="companyStatus" id="idCompanyStatus" cssClass="chosen-select" data-validation="required">
																	<form:option value="">Select Company Type</form:option>
																	<form:options items="${companyStatusList}" itemLabel="companystatus" itemValue="id"></form:options>
																</form:select>
															</div>
														</div>
														<div class="form-group">
															<label for="taxRegistrationNumber" class="col-sm-3 control-label">Tax Registration No.</label>
															<div class="col-sm-6 col-md-5">
																<form:input type="text" path="taxRegistrationNumber" cssClass="form-control" id="idtaxRegistrationNumber" placeholder="" data-validation="length custom" data-validation-length="0-17" data-validation-regexp="^[A-Za-z0-9-\/]{0,17}$"/>
															</div>
														</div>
														<h3 class="blue_form_sbtitle p_t20">Company Registered Address</h3>
														<div class="form-group">
															<label for="idAdressOne" class="col-sm-3 control-label">Address Line 1 :</label>
															<div class="col-sm-6 col-md-5">
																<form:input path="line1" cssClass="form-control" id="idAdressOne" placeholder="" data-validation="required length" data-validation-allowing="- " data-validation-length="2-250" />
															</div>
														</div>
														<div class="form-group">
															<label for="idAdressTwo" class="col-sm-3 control-label">Address Line 2 :</label>
															<div class="col-sm-6 col-md-5">
																<form:input path="line2" cssClass="form-control" id="idAdressTwo" placeholder="" data-validation="required length" data-validation-allowing="- " data-validation-length="2-250" />
															</div>
														</div>
														<div class="form-group">
															<label for="idCityTwon" class="col-sm-3 control-label">City/Town :</label>
															<div class="col-sm-6 col-md-5">
																<form:input path="city" cssClass="form-control" id="idCityTwon" placeholder="" data-validation="required custom" data-validation-regexp="^([a-zA-Z\\-\\s]+){2,200}$" />
															</div>
														</div>
														<div class="form-group">
															<label class="col-sm-3 control-label"> <spring:message code="suplier.postalCode" /> :</label>
															<div class="col-sm-6 col-md-5">
																<form:input path="postalCode" cssClass="form-control" id="idposCode" data-validation="required length alphanumeric" data-validation-length="2-15"  data-validation-allowing="\ " />
															</div>
														</div> 
														<div class="form-group">
															<label for="idRegCountry" class="col-sm-3 control-label">Country :</label>
															<div class="col-sm-6 col-md-5">
																<form:input path="registrationOfCountry" cssClass="form-control" id="idRegCountry" placeholder="" readonly="true" />
															</div>
														</div>
														<div class="form-group">
															<label for="idState" class="col-sm-3 control-label">State/Province :</label>
															<div class="col-sm-6 col-md-5">
																<form:select path="state" class="chosen-select" id="idState" data-validation="required">
																	<form:option value="">Select State/Province</form:option>
																	<form:options items="${states}" itemValue="id" itemLabel="stateName"></form:options>
																</form:select>
															</div>
														</div>
														<div class="form-group">
															<div class="col-sm-offset-3 col-sm-6 col-md-5">
																<form:button id="idBtnNext1" value="submit" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out open2 submit">Next</form:button>
															</div>
														</div>
													</div>
												</div>
											</div>
										</div>
									</form:form>
									<!-- Category Section -->
									<div class="tab-pane" id="step-2">
										<jsp:include page="supplierRegistrationCategory.jsp"></jsp:include>
									</div>
									<form:form class="bordered-row form-horizontal" id="demo-form3" data-parsley-validate="" method="post" modelAttribute="supplier" action="${registration}">
										<div class="tab-pane" id="step-3">
											<div class="content-box">
												<h3 class="content-box-header">
													Declaration <small class="sub_text">As an Administrator, you may view and edit information freely.</small>
												</h3>
												<div class="content-box-wrapper">
													<div class="step_3_content">
														<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
														<p>Hereby confirm that the information provided in this form and attached herewith are true and accurate.</p>
														<p>I / We hereby authorized Privasia Sdn Bhd and its representative to undertake further investigation or verify any information contained in this form or documents attached herewith with any related third party or us. In the event
															of changes details will be provided as soon as possible:</p>
														<p>I / We authorized Privasia Sdn Bhd and its representatives to visit our premises/company and examine relevant documents and interview or refer to any related party.</p>
														<div id="declaration-error-dialog"></div>
														<div class="checkbox checkbox-info">
															<label>
																<div class="checker" id="uniform-declaration1">
																	<span class="">
																		<form:checkbox path="declaration" data-validation="required" data-validation-error-msg="You must accept the declaration" data-validation-error-msg-container="#declaration-error-dialog" />
																		<i class="glyph-icon icon-check"></i>
																	</span>
																</div> I have read and understood the terms in this Declaration<br />&nbsp;
															</label>
														</div>
														<div class="row">
															<div class="step_button_pan">
																<button type="button" class="btn btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium button-previous open21" id="idBtnPrevious3">Back</button>
																<button type="button" class="btn btn-info hvr-pop hvr-rectangle-out ph_btn_midium btn-lg button-next open4" id="idBtnNext3">Next</button>
															</div>
														</div>
													</div>
												</div>
											</div>
										</div>
									</form:form>
									<div class="tab-pane" id="step-4">
										<div class="content-box">
											<h3 class="content-box-header">
												Company Profile (Optional) <small class="sub_text">As an Administrator, you may view and edit information freely.</small>
											</h3>
											<div class="content-box-wrapper">
												<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
												<div class="row">
													<c:set var="fileType" value="" />
													<c:forEach var="type" items="${ownerSettings.fileTypes}" varStatus="index">
														<c:set var="fileType" value="${fileType}${index.first ? '': ', '}${type}" />
													</c:forEach>
													<span>
														Note:<br />
														<ul>
															<li>Max allowed file size is ${ownerSettings.fileSizeLimit} MB</li>
															<li>Allowed file extensions: ${fileType}.</li>
														</ul>
													</span>
													<div class="col-sm-12">
														<div class="col-xs-12 col-sm-6">
															<section class="">
																<form id="companyProfileForm">
																<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
																	<h3 class="blue_form_sbtitle p_t20">Attach Company Profile</h3>
																	<div data-provides="fileinput" class="fileinput fileinput-new input-group">
																		<div data-trigger="fileinput" class="form-control">
																			<i class="glyphicon glyphicon-file fileinput-exists"></i>
																			<span id="idProfilefileUploadSpan" class="fileinput-filename show_name1"></span>
																		</div>
																		<span class="input-group-addon btn btn-black btn-file">
																			<span class="fileinput-new">Select file</span>
																			<span class="fileinput-exists">Change</span>
																			<input type="file" id="companyProfile" name="companyProfile" data-buttonName="btn-black" data-validation-allowing="${fileType}" data-validation-error-msg-container="#Load_File-error-dialog"
																				data-validation-max-size="${ownerSettings.fileSizeLimit}M" data-validation="extension size" data-validation-error-msg-size="You can not upload file larger than ${ownerSettings.fileSizeLimit}MB"
																				data-validation-error-msg-mime="${mimetypes}">
																		</span>
																		<a data-dismiss="fileinput" class="input-group-addon btn btn-default fileinput-exists" href="#">Remove</a>
																	</div>
																	<div id="Load_File-error-dialog" style="width: 100%; float: left; margin: 0 0 10px 0;"></div>
																	<div class="form-group other_attachemts" style="margin-left: 0; margin-right: 0;">
																		<button class="btn btn-gray btn-lg btn-block " type="button" name="companyProfileUpload" id="companyProfileUpload">Upload</button>
																	</div>
																</form>
															</section>
														</div>
														<div class="col-xs-12 col-sm-6">
															<section class="step4_table first-table-marg">
																<div class="step_table mega">
																	<table class="table header table-admin">
																		<thead>
																			<tr>
																				<th class="width-60">File name</th>
																				<th class="width-40">Upload Date</th>
																			</tr>
																		</thead>
																	</table>
																	<table class="data for-pad-data" id="uploadCompnayProfileDisplay">
																		<tbody>
																			<c:forEach items="${uploadCompnayDetails}" var="sp">
																				<tr>
																					<td class="width-60">
																						<form:form method="GET">
																							<c:url var="download" value="/downloadCompanyProfile/${sp.id}" />
																							<a class="word-break" href="${download}">${sp.fileName}</a>
																						</form:form>
																					</td>
																					<td class="width-40">
																						<span class="removeProfileFile" removeProfileId='${sp.id}' companyFileName='${sp.fileName}'>
																							<span class="col-sm-10 no-padding">
																								<fmt:formatDate value="${sp.uploadDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
																								</span>
																							<span class="col-sm-2 no-padding align-right">
																								<a href="#">
																									<i class="fa fa-trash-o" aria-hidden="true"> </i>
																								</a>
																							</span>
																						</span>
																					</td>
																				</tr>
																			</c:forEach>
																		</tbody>
																	</table>
																</div>
															</section>
														</div>
													</div>
													<div class="col-sm-12">
														<div class="col-xs-12 col-sm-6">
															<div class="other_attachemts">
																<h3 class="blue_form_sbtitle p_t20">Attach Other Credentials</h3>
																<ul class="add_more_feture_ul"></ul>
																<form id="otherCredentialUploadForm">
																<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
																	<div class="add_file_row">
																		<div data-provides="fileinput" class="fileinput fileinput-new input-group">
																			<div data-trigger="fileinput" class="form-control">
																				<i class="glyphicon glyphicon-file fileinput-exists"></i>
																				<span id="idOtherFileUploadSpan" class="fileinput-filename show_name"></span>
																			</div>
																			<span class="input-group-addon btn btn-black btn-file">
																				<span class="fileinput-new">Select file</span>
																				<span class="fileinput-exists">Change</span>
																				<input type="file" data-buttonName="btn-black" id="otherCredentialUpload" name="otherCredentialUpload" data-validation-allowing="${fileType}" data-validation-error-msg-container="#Load_File-error-dialogOtherCredential"
																					data-validation-max-size="${ownerSettings.fileSizeLimit}M" data-validation="extension size" data-validation-error-msg-size="You can not upload file larger than ${ownerSettings.fileSizeLimit}MB"
																					data-validation-error-msg-mime="${mimetypes}">
																			</span>
																			<a data-dismiss="fileinput" class="input-group-addon btn btn-default fileinput-exists" href="#">Remove</a>
																		</div>
																	</div>
																	<div id="Load_File-error-dialogOtherCredential" style="width: 100%; float: left; margin: 0 0 10px 0;"></div>
																	<div class="form-group" style="margin-left: 0; margin-right: 0;">
																		<textarea class="form-control" rows="3" id="otherCredentialDesc" data-validation="length" data-validation-length="0-250" name="otherCredentialDesc" placeholder="Enter Description"></textarea>
																	</div>
																	<div class="form-group" style="margin-left: 0; margin-right: 0;">
																		<button class="btn btn-gray btn-lg btn-block up_btn" type="button" name="OtherCredUpload" id="OtherCredUpload">Upload</button>
																	</div>
																</form>
																<form:form class="bordered-row form-horizontal" id="demo-form4" data-parsley-validate="" method="post" modelAttribute="supplier" action="${registration}">
																	<div class="three_btn_group">
																		<button type="button" class="btn hvr-pop marg-none hvr-rectangle-out1 btn-black open31" id="idBtnPrevious4">Back</button>
																		<button type="button" class="btn btn-black hvr-pop hvr-rectangle-out1 open5" id="idBtnSkip4">Skip</button>
																		<button type="button" class="btn btn-primary hvr-pop hvr-rectangle-out open5" id="idBtnNext4">Next</button>
																	</div>
																</form:form>
															</div>
														</div>
														<div class="col-xs-12 col-sm-6">
															<section class="step4_table last-table-marg list_other_Attachments">
																<div class="step_table mega">
																	<table class="table header table-admin">
																		<thead>
																			<tr>
																				<th class="width-33">File name</th>
																				<th class="width-33">Description</th>
																				<th class="width-33">Upload Date</th>
																			</tr>
																		</thead>
																	</table>
																	<table class="data for-pad-data table" id="uploadOtherFiless">
																		<tbody>
																			<c:forEach items="${otherCredList}" var="sp">
																				<tr>
																					<td class="width-33">
																						<form:form method="GET">
																							<c:url var="download" value="/supplierreg/downloadOtherCredential/${sp.id}" />
																							<a class="word-break" href="${download}">${sp.fileName}</a>
																						</form:form>
																					</td>
																					<td class="width-33">${sp.description}&nbsp;</td>
																					<td class="width-33">
																						<span class="removeOtherFile" removeOtherId='${sp.id}' otherCredFile='${sp.fileName}'>
																							<span class="col-sm-10 no-padding">
																								<fmt:formatDate value="${sp.uploadDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
																								</span>
																							<span class="col-sm-2 no-padding align-right">
																								<a href="">
																									<i class="fa fa-trash-o" aria-hidden="true"></i>
																								</a>
																							</span>
																						</span>
																					</td>
																				</tr>
																			</c:forEach>
																		</tbody>
																	</table>
																</div>
															</section>
														</div>
													</div>
												</div>
											</div>
										</div>
									</div>
									<form:form id="financeForm" class="bordered-row form-horizontal" data-parsley-validate="" method="post" modelAttribute="supplier" action="${registration}">
									<div class="tab-pane" id="step-5">
										<div class="content-box">
											<div class="content-box-wrapper">
												<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
												<h3 class="blue_form_sbtitle">Capital Structure :</h3>
												<div class="form-group">
													<label for="idCurrencyCode" class="col-sm-3 control-label"><spring:message code="currency.code" /> :</label>
														<div class="col-sm-6 col-md-5">
														<form:select path="currency" id="idCurrencyCode" cssClass="chosen-select">
															<form:option value="">Select Currency Code</form:option>
    														<form:options items="${activeCurrencyList}" itemLabel="currencyCode" itemValue="id"></form:options>
														</form:select>
														<div id="capitalCurrencyCodeError" class="error-message"></div>
														</div>
												</div>
												<div class="form-group">
															<label for="idPaidUpCapital" class="col-sm-3 control-label">Paid Up Capital :</label>
															<div class="col-sm-6 col-md-5">
																<form:input type="text" path="paidUpCapital" cssClass="form-control align-right" id="idPaidUpCapital" name="idPaidUpCapital" placeholder=""
																data-validation="length custom" data-validation-length="0-32" data-validation-regexp="^[0-9,.]{0,32}$" />
																<div id="paidUpCapitalError" class="error-message"></div>
															</div>
												</div>
											<h3 class="blue_form_sbtitle p_t20">Financial Documents :</h3>
											<span>
													Note:<br />
														<ul>
															<li>Max allowed file size is 100 MB</li>
															<li>Allowed file extensions: ${fileType}.</li>
														</ul>
													</span>
											<div class="col-sm-12">
														<div class="col-xs-12 col-sm-6">
															<section class="">
																<form id="financialDocumentsForm">
																<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
																<div class="add_file_row">
																	<div data-provides="fileinput" class="fileinput fileinput-new input-group">
																		<div data-trigger="fileinput" class="form-control">
																			<i class="glyphicon glyphicon-file fileinput-exists"></i>
																			<span id="idfinancialDocumentsSpan" class="fileinput-filename show_name2"></span>
																		</div>
																		<span class="input-group-addon btn btn-black btn-file">
																			<span class="fileinput-new">Select file</span>
																			<span class="fileinput-exists">Change</span>
																			<input type="file" data-buttonName="btn-black"
																			 id="financialDocuments" name="financialDocuments" 
																			 data-buttonName="btn-black"
																			 data-validation-allowing="${fileType}"
																			 data-validation-error-msg-container="#Load_File-error-dialogFinancialDoc"
																			 data-validation-max-size="100M"
																			 data-validation="extension size"
																			 data-validation-error-msg-size="You can not upload file larger than 100MB"
																			 data-validation-error-msg-mime="${mimetypes}">
																		</span>
																		<a data-dismiss="fileinput" class="input-group-addon btn btn-default fileinput-exists" href="#">Remove</a>
																	</div>
																	</div>
																	<div id="Load_File-error-dialogFinancialDoc" style="width: 100%; float: left; margin: 0 0 10px 0;"></div>
																	<div class="form-group" style="margin-left: 0; margin-right: 0;">
																		<textarea class="form-control" rows="3" id="financialDocDesc" data-validation="length" data-validation-length="0-250" name="financialDocDesc" placeholder="Enter Description"></textarea>
																	</div>
																	<div class="form-group other_attachemts" style="margin-left: 0; margin-right: 0;">
																		<button class="btn btn-gray btn-lg btn-block up_btn" type="button" name="financialDocumentsUpload" id="financialDocumentsUpload">Upload</button>
																	</div>
																</form>
															</section>
														</div>
														<div class="col-xs-12 col-sm-6">
															<section class="step4_table first-table-marg">
																<div class="mega h-140">
																	<table class="table header">
																		<thead>
																			<tr>
																				<th class="width-33">File name</th>
																				<th class="width-33">Description</th>
																				<th class="width-33">Upload Date</th>
																			</tr>
																		</thead>
																	</table>
																	<table class="data for-pad-data" id="financialDocumentsDisplay">
																		<tbody>
																			<c:forEach items="${uploadFinancialDocuments}" var="sp">
																				<tr>
																					<td class="width-33">
																						<form:form method="GET">
																							<c:url var="download" value="/downloadFinancialDocuments/${sp.id}" />
																							<a class="word-break" href="${download}">${sp.fileName}</a>
																						</form:form>
																					</td>
																					<td class="width-33">${sp.description}&nbsp;</td>
																					<td class="width-33">
																						<span class="removeFinancialDocsFile" removeFinancialDocsId='${sp.id}' financialDocsFileName='${sp.fileName}'>
																							<span class="col-sm-10 no-padding">
																								<fmt:formatDate value="${sp.uploadDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
																								</span>
																								<span class="col-sm-2 no-padding align-right">
																									<a href="">
																										<i class="fa fa-trash-o" aria-hidden="true"></i>
																									</a>
																								</span>
																						</span>
																					</td>
																				</tr>
																			</c:forEach>
																		</tbody>
																	</table>
																</div>
															</section>
														</div>
													</div>
														<div class="col-sm-12 clearfix" style="padding-left: 30px;">
															<div class="step_button_pan">
																<form:button type="button" class="btn btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium open41" id="idBtnPrevious6">Back</form:button>
																<form:button type="submit" class="btn btn-primary ph_btn_midium  hvr-pop hvr-rectangle-out open6" id="idButtonNext6">Next</form:button>
															</div>
														</div>
														<div class="clear"></div>
											</div>
										</div>
    								</div>
									</form:form>
									<div class="tab-pane" id="step-6">
										<div class="content-box">
											<div class="content-box-wrapper">
												<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
												<h3 class="blue_form_sbtitle">Board Of Directors</h3>
												<form:form id="boardOfDirectorForm" class="form-horizontal" data-parsley-validate="" method="post" modelAttribute="boardOfDirector">
												<div class="form-group" style="display: none;">
													<label for="id" class="col-sm-3 control-label">Id</label>
													<div class="col-sm-6 col-md-5">
														<form:input type="text" path="id" cssClass="form-control" name="id" placeholder="" id="directorId"/>
													</div>
												</div>
												<div class="form-group">
														<label for="dirName" class="col-sm-3 control-label">Director's Name</label>
															<div class="col-sm-6 col-md-5">
															<form:input type="text" path="directorName" cssClass="form-control" id="idDirectorName" name="idDirectorName" placeholder=""
															data-validation="length required custom" data-validation-length="0-64" data-validation-regexp="^[A-Za-z-\/'' '.]{0,64}$"/>
															</div>
												</div>
												<div class="form-group">
													<label for="idType" class="col-sm-3 control-label">Identification Type :</label>
														<div class="col-sm-6 col-md-5">
															<form:select path="idType" id="idType" cssClass="chosen-select"  data-validation="required">
																<form:option value="">Select Identification Type</form:option>
																<form:option value="National Identification Card (IC)">National Identification Card (IC)</form:option>
																<form:option value="Passport">Passport</form:option>
																<form:option value="Social Security Card">Social Security Card</form:option>
															</form:select>
														</div>
												</div>
												<div class="form-group">
														<label for="idNumber" class="col-sm-3 control-label">Identification Number :</label>
															<div class="col-sm-6 col-md-5">
															<form:input type="text" path="idNumber" cssClass="form-control" id="idNumber" name="idNumber" placeholder=""
															data-validation="length required custom" data-validation-length="0-32" data-validation-regexp="^[A-Za-z0-9-\/]{0,32}$" />
														</div>
												</div>
												<div class="form-group">
													<label for="dirType" class="col-sm-3 control-label">Type of Director :</label>
														<div class="col-sm-6 col-md-5">
															<form:select path="dirType" id="idDirType" cssClass="chosen-select" data-validation="required">
																<form:option value="">Select Type of Director</form:option>
																<form:option value="Executive">Executive</form:option>
																<form:option value="Non-Executive">Non-Executive</form:option>
																<form:option value="Managing">Managing</form:option>
																<form:option value="Independent">Independent</form:option>
																<form:option value="Others">Others</form:option>
															</form:select>
														</div>
												</div>
												<div class="form-group">
														<label for="idNumber" class="col-sm-3 control-label">Email Address :</label>
															<div class="col-sm-6 col-md-5">
															<form:input type="email" path="dirEmail" cssClass="form-control" id="idDirEmail" name="dirEmail" placeholder=""
															data-validation="length email" data-validation-length="0-64"/>
														</div>
												</div>
												<div class="form-group">
														<label for="dirContact" class="col-sm-3 control-label">Contact Number </label>
															<div class="col-sm-6 col-md-5">
															<form:input type="number" path="dirContact" cssClass="form-control" id="idDirContact" name="dirContact" placeholder=""
															data-validation="length custom" data-validation-length="0-24"  data-validation-regexp="^[0-9-+]{0,24}$"/>
														</div>
												</div>
												<div class="form-group">
													<label class="col-sm-3 control-label"></label>
														<div class="col-sm-6 col-md-5">
															<form:button type="button" class="btn btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium " id="addNewDir">Add New</form:button>
															<form:button type="button" class="btn ph_btn_midium hvr-pop hvr-rectangle-out disabled" disabled="true" id="saveDir">Save</form:button>
														</div>
												</div>
											</form:form>
												<div>
														<div>
															<section class="step4_table">
																<div class="mega">
																	<table class="table" id="directorsDisplay">
																		<thead>
																			<tr>
																				<th>Action</th>
																				<th>No.</th>
																				<th>Director's Name</th>
																				<th>Identification type</th>
																				<th>Identification Number</th>
																				<th>Type of Director</th>
																				<th>Email Address</th>
																				<th>Contact Number</th>
																			</tr>
																		</thead>
																		<tbody>
																			<c:forEach items="${boardOfDirectors}" var="sp" varStatus="loop">
																				<tr>
																					<td>
																						<div>
																							<span class="col-sm-6 p-l-0 no-padding" id="showConfirmDeletePopUp" delete-id="${sp.id}" delete-name="${sp.directorName}">
																								<a>
																									<i class="fa fa-trash-o" aria-hidden="true"></i>
																								</a>
																							</span>
																							<span class="col-sm-6 p-l-0 no-padding" id="editDirector" edit-id="${sp.id}">
																								<a>
																									<i class="fa fa-edit" aria-hidden="true"></i>
																								</a>
																							</span>
																						</div>
																						</td>
																					<td>${loop.count}</td>
																					<td>${sp.directorName}</td>
																					<td>${sp.idType}</td>
																					<td>${sp.idNumber}</td>
																					<td>${sp.dirType}</td>
																					<td>${sp.dirEmail}</td>
																					<td>${sp.dirContact}</td>
																				</tr>
																			</c:forEach>
																		</tbody>
																	</table>
																</div>
															</section>
														</div>
												</div>
											<form:form class="bordered-row form-horizontal" data-parsley-validate="" method="post" modelAttribute="supplier" action="${registration}">
														<div class="clearfix">
															<div class="step_button_pan" style="margin-top: 15px;">
																<form:button type="button" class="btn btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium open51" id="idBtnPrevious7">Back</form:button>
																<form:button type="submit" class="btn btn-primary ph_btn_midium hvr-pop hvr-rectangle-out open7" id="idButtonNext7">Next</form:button>
															</div>
														</div>
													<div class="clear"></div>
												</form:form>
											</div>
										</div>
										<div class="modal fade hideModal" id="confirmDeleteDirector" role="dialog">
										<div class="modal-dialog for-delete-all reminder documentBlock">
											<div class="modal-content">
												<div class="modal-header">
													<h3>
														<spring:message code="application.confirm.delete" />
													</h3>
													<button class="close for-absulate" id="confirmDeleteDirectorDismiss" type="button" data-dismiss="modal">X</button>
												</div>
												<div class="modal-body">
													<label>
														Are you sure you want to delete this record ? 
													</label>
													<input type="hidden"  />
												</div>
												<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
													<button type="button" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" id="confDelDir">
														<spring:message code="application.delete" />
													</button>
													<button type="button" id="confirmDeleteDirectorClose" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
														<spring:message code="application.cancel" />
													</button>
												</div>
											</div>
										</div>
									</div> 
									</div>
									<form:form class="bordered-row form-horizontal" id="demo-form5" data-parsley-validate="" method="post" modelAttribute="supplier" action="${registration}">
										<div class="tab-pane" id="step-7">
											<input type="hidden" id="supplierStep" value="7">
											<div class="content-box">
												<h3 class="content-box-header">
													Services: Add/Edit Track Record <small class="sub_text">High level free-text description of all services offered (this can be Searched by Buyer)</small>
												</h3>
												<div class="content-box-wrapper">
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
													<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
													<div class="row">
														<div class="col-xs-12 col-sm-12">
															<section class="last_step_left_block trackRecordDataTable">
																<p>High level free-text description of all services offered (this can be Searched by Buyer)</p>
																<div class="form-group">
																	<form:textarea path="supplierTrackDesc" class="form-control" name="textarea1" rows="4" id="idServicesOffered" placeholder="Enter Description"></form:textarea>
																</div>
															</section>
														</div>
														<div class="col-xs-12 col-sm-12">
															<section class="last_step_right_block">
																<div class="row">
																	<div class="col-sx-12 col-sm-6">
																		<div class="input-group search_box_gray">
																			<input type="text" class="form-control" id="searchTrackRecord" name="searchTrackRecord">
																			<span class="input-group-btn">
																				<button class="btn btn-gray" type="button"></button>
																			</span>
																		</div>
																	</div>
																	<div class="col-sx-12 col-sm-6">
																		<c:url var="TrackRecord" value="/supplierTrackRecord" />
																		<a href="javascript:void{};" id="idTrackrecord001" class="btn btn-primary ph_btn_midium  hvr-pop hvr-rectangle-out open8">Add New Record</a>
																	</div>
																</div>
																<div class="row">
																	<div class="col-xs-12">
																		<div class="last_step_table step_table mega trackRecordDataTable">
																			<table class="table header table-admin">
																				<thead>
																					<tr>
																						<th class="width_20">Year</th>
																						<th class="width_150">Project Name</th>
																						<th class="width_100">Contract Value</th>
																						<th class="width_20 transparent-color">Remove</th>
																					</tr>
																				</thead>
																			</table>
																			<table class="data for-pad-data" id="addProjectTrackRecord">
																				<tbody>
																					<c:forEach items="${supplier.supplierProjects}" var="supProj">
																						<tr>
																							<td class="width_20">${supProj.year}</td>
																							<c:url var="TrackRecord" value="/supplierTrackRecord" />
																							<td class="width_150 editRecord">
																								<a href="" editid="${supProj.id}">${supProj.projectName}</a>
																							</td>
																							<td class="numeric width_100">${supProj.contactValue}</td>
																							<td class="width_20" align="center">
																								<span class="removeProjectFile" removeProjectId='${supProj.id}' removeProject='${supProj.projectName}'>
																									<a href="#">
																										<i class="fa fa-trash-o" aria-hidden="true"></i>
																									</a>
																								</span>
																							</td>
																						</tr>
																					</c:forEach>
																				</tbody>
																			</table>
																		</div>
																	</div>
																</div>
															</section>
														</div>
														<div class="clear"></div>
														<div class="col-xs-12 col-sm-6 clearfix">
															<div class="step_button_pan">
																<form:button type="button" class="btn btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium open61" id="idBtnPrevious5">Back</form:button>
																<form:button type="submit" class="btn btn-primary ph_btn_midium  hvr-pop hvr-rectangle-out" id="idBtnFinish">Finish</form:button>
															</div>
														</div>
													</div>
												</div>
											</div>
										</div>
									</form:form>
									<div class="tab-pane" id="step-8">
										<div class="content-box">
											<h3 class="content-box-header">
												Services: Add/Edit Track Record <small class="sub_text">As an Administrator, you may view and edit information freely.</small>
											</h3>
											<form:form cssClass="form-horizontal bordered-row" id="track-form" data-parsley-validate="" modelAttribute="supplierProject">
												<form:hidden path="supplierId" />
												<form:hidden path="id" />
												<div class="content-box-wrapper">
													<div class="form-horizontal">
														<h3 class="blue_form_sbtitle">Project Information :</h3>
														<div class="form-group">
															<label class="col-sm-3 control-label">Project Name :</label>
															<div class="col-sm-6 col-md-5">
																<form:input type="text" class="form-control" path="projectName" placeholder="Enter Project Name" data-validation="required length alphanumeric" data-validation-allowing="-_ ." data-validation-length="10-250" />
															</div>
														</div>
														<div class="form-group">
															<label class="col-sm-3 control-label">Client Name :</label>
															<div class="col-sm-6 col-md-5">
																<form:input class="form-control" path="clientName" placeholder="Enter Client Name" data-validation="required length alphanumeric" data-validation-allowing="-_ ." data-validation-length="1-200" />
															</div>
														</div>
														<div class="form-group">
															<label class="col-sm-3 control-label">Project Completion Year :</label>
															<div class="col-sm-6 col-md-5">
																<form:input path="year" cssClass="form-control" id="idYearE" placeholder="Year Establised" data-validation="required length number year_comp" data-validation-length="min4-max4"
																	 />
															</div>
														</div>
														<div class="form-group">
															<label class="col-sm-3 control-label">Currency :</label>
															<div class="col-sm-6 col-md-5">
																<form:select path="currency" id="idCurrency" cssClass="chosen-select" data-validation="required">
																	<form:option value="">Select Currency </form:option>
																	<form:options items="${currency}" itemValue="id" itemLabel="currencyName" />
																</form:select>
															</div>
														</div>
														<div class="form-group">
															<label class="col-sm-3 control-label">Contract Value :</label>
															<div class="col-sm-6 col-md-5">
																<form:input class="form-control" path="contactValue" id="" placeholder="Enter Contract Value" data-validation="required length number" data-sanitize="numberFormat" data-sanitize-number-format="0,0.00" data-validation-length="max16" />
															</div>
														</div>
														<div class="form-group">
															<label class="col-sm-3 control-label">Client Email Address :</label>
															<div class="col-sm-6 col-md-5">
																<form:input type="text" class="form-control" path="clientEmail" id="" placeholder="Enter Client Email Address" data-validation="required length email" data-validation-length="max160" />
															</div>
														</div>
													</div>
													<section class="step_2_content">
														<h3 class="blue_form_sbtitle p_t20">Industry Sector(NAICS) :</h3>
														<div class="sub_txt_step2">Buyers of products and services will contact you and include you in their events based on the industry categories that you have selected below.</div>
														<div class="row">
															<div class="col-xs-12 col-sm-12 col-md-12">
																<div class="input-group search_box_gray">
																	<input type="text" class="form-control searchListCheck" data-from="" data-relclass="projectIndustryList" data-inpname="projectIndustries">
																	<span class="input-group-btn">
																		<form:button type="button" class="btn btn-gray"></form:button>
																	</span>
																</div>
																<div id="projectIndustries-error-dialog"></div>
																<div class="chk_scroll_box">
																	<div class="scroll_box_inner industry pad-top-bottom-15 industryCatCheckboxes tree-multiselect">
																		<div class="leftSideOfCheckbox">
																			<ul class="tree projectIndustryList" id="tree">
																				<c:forEach items="${projectCategories}" var="sc">
																					<li>
																						<span class="nvigator" data-id="${sc.id}" data-level="${sc.level}">
																							<i class="<c:if test="${empty sc.children}">fa fa-plus</c:if><c:if test="${not empty sc.children}">fa fa-minus</c:if>" aria-hidden="true"></i>
																						</span>
 																						<form:checkbox path="projectIndustries" value="${sc.id}" class="first" data-validation="checkbox_group" data-validation-qty="min1" data-validation-error-msg-container="#projectIndustries-error-dialog" />
																						<span class="number tree_heading">${sc.categoryCode} - ${sc.categoryName}</span>
																						<c:if test="${not empty sc.children}">
																							<!-- AND SHOULD CHECK HERE -->
																								<ul>
																							<c:forEach items="${sc.children}" var="child">
																									<li>
																										<span class="nvigator" data-id="${child.id}" data-level="${child.level}">
																											<i class="<c:if test="${not empty child.children}">fa fa-minus</c:if>
																													  <c:if test="${empty child.children}">fa fa-plus</c:if>" aria-hidden="true"></i>
																										</span>
 																										<form:checkbox path="projectIndustries" id="childCheckbox" value="${child.id}" data-validation="checkbox_group" data-validation-qty="min1" data-validation-error-msg-container="#projectIndustries-error-dialog" />
																										<span class="number">${child.categoryCode} - ${child.categoryName}</span>
																										<!-- SHOULD CHECK HERE -->
																										<c:if test="${not empty child.children}">
																											<!-- AND SHOULD CHECK HERE -->
																												<ul>
																											<c:forEach items="${child.children}" var="subChild">
																													<li>
																														<span class="nvigator" data-id="${subChild.id}" data-level="${subChild.level}">
																															<i class="<c:if test="${empty subChild.children}">fa fa-plus</c:if><c:if test="${not empty subChild.children}">fa fa-minus</c:if>" aria-hidden="true"></i>
																														</span>
																														<form:checkbox path="projectIndustries" value="${subChild.id}" data-validation="checkbox_group" data-validation-qty="min1" data-validation-error-msg-container="#projectIndustries-error-dialog" />
																														<span class="number">${subChild.categoryCode} - ${subChild.categoryName}</span>
																														<c:if test="${not empty subChild.children}">
																															<!-- AND SHOULD CHECK HERE -->
																																<ul>
																																<c:forEach items="${subChild.children}" var="subSubChild">
																																		<li>
																																			<span class="nvigator" data-id="${subSubChild.id}" data-level="${subSubChild.level}">
																																				<i class="<c:if test="${empty subSubChild.children}">fa fa-plus</c:if><c:if test="${not empty subSubChild.children}">fa fa-minus</c:if>" aria-hidden="true"></i>
																																			</span>
																																			<form:checkbox path="projectIndustries" value="${subSubChild.id}" data-validation="checkbox_group" data-validation-qty="min1" data-validation-error-msg-container="#projectIndustries-error-dialog" />
																																			<span class="number">${subSubChild.categoryCode} - ${subSubChild.categoryName}</span>
																																			<c:if test="${not empty subSubChild.children}">
																																				<!-- AND SHOULD CHECK HERE -->
																																					<ul>
																																					<c:forEach items="${subSubChild.children}" var="subSubSubChild">
																																							<li>
																																								<span class="nvigator" data-id="${subSubSubChild.id}" data-level="${subSubSubChild.level}">
																																									<i class="fa fa-minus" aria-hidden="true"></i>
																																								</span>
																																								<form:checkbox path="projectIndustries" value="${subSubSubChild.id}" data-validation="checkbox_group" data-validation-qty="min1" data-validation-error-msg-container="#projectIndustries-error-dialog" />
																																								<span class="number">${subSubSubChild.categoryCode} - ${subSubSubChild.categoryName}</span>
																																							</li>
																																					</c:forEach>
																																					</ul>
																																			</c:if>
																																		</li>
																																</c:forEach>
																																</ul>
																														</c:if>
																													</li>
																											</c:forEach>
																												</ul>
																										</c:if>
																									</li>
																							</c:forEach>
																								</ul>
																						</c:if>
																					</li>
																				</c:forEach>
																			</ul>
																		</div>
																		<div class="rightSideOfCheckbox"></div>
																	</div>
																</div>
															</div>
															<div class="col-xs-12 col-sm-12 col-md-12">
																<!-- All regian Block -->
																<h3 class="blue_form_sbtitle p_t20">Geographical Coverage :</h3>
																<div class="input-group search_box_gray">
																	<input type="text" class="form-control searchListCheckCountry" data-from="" data-relclass="projectCountyList" data-inpname="tracRecordCoverages">
																	<span class="input-group-btn">
																		<form:button class="btn btn-gray" type="button"></form:button>
																	</span>
																</div>
																<div id="tracRecordCoverages-error-dialog"></div>
																<div class="chk_scroll_box">
																	<div class="scroll_box_inner industry pad-top-bottom-15 industryCatCheckboxes tree-multiselect">
																		<div class="leftSideOfCheckbox">
																			<ul class="tree projectCountyList" id="tree">
																				<c:forEach items="${registeredTrackCountry}" var="country">
																					<li>
																						<span class="nvigator-place">
																							<i class="<c:if test="${empty country.children}">fa fa-plus</c:if><c:if test="${not empty country.children}">fa fa-minus</c:if>" aria-hidden="true"></i>
																						</span>
																						<form:checkbox path="tracRecordCoverages" value="${country.id}" class="first" data-validation="checkbox_group" data-validation-qty="min1" data-validation-error-msg-container="#tracRecordCoverages-error-dialog" />
																						<span class="number tree_heading">${country.name}</span>
																						<c:if test="${not empty country.children}">
																							<!-- AND SHOULD CHECK HERE -->
																							<c:forEach items="${country.children}" var="state">
																								<ul>
																									<li>
																										<span class="nvigator-place">
																											<i class="fa fa-minus" aria-hidden="true"></i>
																										</span>
																										<form:checkbox path="tracRecordCoverages" value="${state.id}" data-validation="checkbox_group" data-validation-qty="min1" data-validation-error-msg-container="#tracRecordCoverages-error-dialog" />
																										<span class="number">${state.name}</span>
																									</li>
																								</ul>
																							</c:forEach>
																						</c:if>
																					</li>
																				</c:forEach>
																			</ul>
																		</div>
																		<div class="rightSideOfCheckbox"></div>
																	</div>
																</div>
																<div class="step_button_pan">
																	<form:button type="button" class="btn btn-info hvr-pop hvr-rectangle-out ph_btn_midium btn-lg button-next" id="projectAdd">Save</form:button>
																	<form:button type="button" class="btn btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium button-previous open72">Cancel </form:button>
																</div>
															</div>
														</div>
													</section>
												</div>
											</form:form>
										</div>
									</div>
								</div>
							</div>
						</div>
					</section>
				</div>
			</div>
		</div>
	</section>
</div>
<script src="<c:url value="/resources/assets/js-core/validation.js"/>"></script>
<script src="<c:url value="/resources/js/view/registration.js"/>"></script>
<!-- WIDGETS -->
<!-- Uniform -->
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/uniform/uniform.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/uniform/uniform-demo.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/bootstrap/js/bootstrap.js"/>"></script>
<!-- Chosen -->
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/chosen/chosen.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/chosen/chosen-demo.js"/>"></script>
<!-- Bootstrap Tooltip -->
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/tooltip/tooltip.js"/>"></script>
<!-- Perfact scroll -->
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/perfect-scrollbar/js/perfect-scrollbar.jquery.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/perfect-scrollbar/js/perfect-scrollbar.min.js"/>"></script>
<!-- Content box -->
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/content-box/contentbox.js"/>"></script>
<style>
.tree li {
	position: relative;
}

table.data.for-pad-data.bordered tr {
	border-bottom: 1px solid #ccc;
}

.for-pad-data tr td {
	font-size: 13px;
}

.transparent-color
{
	color: transparent !important;;
}

.no-padding
{
	padding: 0;
}

.disabled
{
	color: #c7c7c7 !important;
	background: grey;
}
.highlight {
	background-color: #fff34d;
	-moz-border-radius: 5px;
	/* FF1+ */
	-webkit-border-radius: 5px;
	/* Saf3-4 */
	border-radius: 5px;
	/* Opera 10.5, IE 9, Saf5, Chrome */
	-moz-box-shadow: 0 1px 4px rgba(0, 0, 0, 0.7);
	/* FF3.5+ */
	-webkit-box-shadow: 0 1px 4px rgba(0, 0, 0, 0.7);
	/* Saf3.0+, Chrome */
	box-shadow: 0 1px 4px rgba(0, 0, 0, 0.7);
	/* Opera 10.5+, IE 9.0 */
}

.form-group {
	float: none;
}

.error-border
{
	border: 1px solid #b94a48 !important;
}

.error-message
{
	color: #ff5757 !important;
}

.highlight {
	padding: 1px 1px;
	margin: 0 -4px;
}

.animated-search-filter>* {
	position: inherit !important;
}

.search_ul_1 li, .search_ul li {
	position: relative;
	transform: translateY(0) !important;
}

.nvigator, .nvigator-place {
	position: absolute;
	left: -14px;
	cursor: pointer;
	top: 4px;
}
/* this css work for plus added text */
.add_more_feture_ul li {
	list-style: outside none none;
	padding: 5px 8px;
}

.add_more_feture_ul li a {
	float: right;
}

.width-33
{
	width: 33%;
}

.width-40
{
	width: 40%;
}

.width-60
{
	width: 60%;
}

.no-padding
{
	padding: 0;
}

.h-140
{
	height: 140px;
}

.add_more_feture_ul {
	padding-left: 8px;
}

.step4_table {
	margin-top: 58px;
}
/* this css work for plus added text */
#idState_chosen .chosen-drop {
	border-bottom: 0;
	border-top: 1px solid #aaa;
	top: auto;
	bottom: 40px;
}

.showModal
{
	display: block;
    background: #ffffffa6;
}

.hideModal
{
	display: none;
}

.p-l-0
{
	padding-left: 0;
}
.align-right {
        text-align: right;
	}
	
.word-break
{
	word-break: break-all;
}	
</style>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script>
	<c:set var="tab"  value="${step}"/>
	<c:if test="${not empty tab }" >
	$(document).ready(function() {
		<c:choose>
		<c:when test="${tab == 8}">
		$(".tb_7").addClass("active");
		$(".tb_7").prevAll().addClass("active");
		$(".tb_7").nextAll().removeClass("active");
		$(".tb_7").nextAll().removeClass("activeprev");
		$('.tab-pane').removeClass("active");
		$('#step-8').addClass('active');
		</c:when>
		<c:otherwise>
		$(".tb_${tab}").prevAll().addClass("active activeprev");
		$(".tb_${tab}").nextAll().removeClass("active");
		$(".tb_${tab}").nextAll().removeClass("activeprev");
		$(".tab-pane.active").removeClass("active");
		$("#step-${tab}, .tb_${tab}").addClass("active");
		</c:otherwise>
		</c:choose>
	});
	</c:if>
</script>
<script>
	$.formUtils.addValidator({
		name : 'year_established',
		validatorFunction : function(value, $el, config, language, $form) {
			var response = true;
			var currentYear = new Date().getFullYear();
			console.log(currentYear);
			if (parseInt(value) < 1900 || parseInt(value) > currentYear) {
				response = false;
			}
			return response;
		},
		errorMessage : 'Year established is not valid. It should be greater than or equal to 1900 and less than or equal to current year',
		errorMessageKey : 'badYearEstablished'
	});

	$.formUtils.addValidator({
		name : 'year_comp',
		validatorFunction : function(value, $el, config, language, $form) {
			var response = true;
			var currentYear = new Date().getFullYear();
			console.log(currentYear);
			if (parseInt(value) < 1900 || parseInt(value) > currentYear) {
				response = false;
			}
			return response;
		},
		errorMessage : 'Year completion is not valid. It should be greater than or equal to 1900 and less than or equal to current year',
		errorMessageKey : 'badYearCompletion'
	});

	$.validate({
		lang : 'en',
		modules : 'file,sanitize'
	});
</script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.mask.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/numeral.min.js"/>"></script>
<script>
	$(document).ready(function() {
		$('#idFax').mask('+00 00000000000', {
			placeholder : "e.g. +60 352735465"
		});
		$('#idTelPhone').mask('+00 00000000000', {
			placeholder : "e.g. +60 322761533"
		});
		$('#idYearEst').mask('0000', {
			placeholder : "e.g. 1989"
		});
		$('#idYearE').mask('0000', {
			placeholder : "e.g. 1989"
		});
		$('#idAdMoNo').mask('+00 00000000000', {
			placeholder : "e.g. +60 352735465"  
		});
		//$('#idCompanyWebsite').mask('http://www.company.com', {placeholder: "http://www.company.com"});
	});
</script>

<script>
	$(document).ready(function() {
		minPageHgt();
		$(window).resize(function() {
			minPageHgt();
		});
		
	    //zendesk labels
		<c:if test="${step == 8 or step == 7}">
		zendesk(${suppRegTrackRecordsDesk});
		</c:if>
		<c:if test="${step != 8 and step != 7}">
		zendesk(${suppRegGeneralInfoDesk});
		</c:if>
		//Step 1-2
	    $('#idBtnPrevious2').click(
	            function(e) {
	                e.preventDefault();
	                zendesk(${suppRegGeneralInfoDesk});
	            });
	    
	    $('#idBtnNext1').click(
	        function(e) {
	            e.preventDefault();
	            zendesk(${suppRegCategoryDesk});
	        });

	//Step 2-3 
	$('#idBtnPrevious3').click(
            function(e) {
                e.preventDefault();
                zendesk(${suppRegCategoryDesk});
            });
    
    $('#idBtnNext2').click(
        function(e) {
            e.preventDefault();
            zendesk(${suppRegDeclarationDesk});
        });

	//Step 3-4 
	$('#idBtnPrevious4').click(
            function(e) {
                e.preventDefault();
                zendesk(${suppRegDeclarationDesk});
            });
    
    $('#idBtnNext3').click(
        function(e) {
            e.preventDefault();
            zendesk(${suppRegCompanyProfDesk});
        });
    
  //Step 4-5 
	$('#idBtnPrevious5').click(
            function(e) {
                e.preventDefault();
                zendesk(${suppRegCompanyProfDesk });
            });
    
    $('#idBtnNext4').click(
        function(e) {
            e.preventDefault();
            zendesk(${suppRegFinancialInfoDesk});
		});
		
	$('#idButtonNext6').click(
        function(e) {
            e.preventDefault();
            zendesk(${suppRegOrgDetailsDesk});
		});	
		
	$('#idButtonNext7').click(
        function(e) {
            e.preventDefault();
            zendesk(${suppRegTrackRecordsDesk});
		});	

});
	

	function zendesk(label){
		zE(function() {
		console.log(label);
	        zE.setHelpCenterSuggestions({
	            labels: [label]
	        });
	    });
	}

	function minPageHgt() {
		var minHgt = $(window).height() - 51;
		$('#sb-site').css('min-height', minHgt);
	}
</script>

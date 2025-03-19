<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<sec:authorize access="hasRole('SUPPLIER_APPROVAL') or hasRole('ADMIN')" var="canEdit" />
<sec:authorize access="hasRole('SUPPLIER_CHANGE_EMAIL') or (hasRole('ADMIN') and hasRole('OWNER'))" var="canUpdateComEmail" />
<script type="text/javascript">
	function canEdit() {
		return "${canEdit}";
	}
</script>
<script type="text/javascript">
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
			url : getContextPath() + "/confirmDetails",
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
				window.location.href = getContextPath() + "/supplierSignupList";
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
					<li>
						<a href="${pageContext.request.contextPath}/owner/ownerDashboard">Dashboard</a>
					</li>
					<li>
						<a href="${pageContext.request.contextPath}/supplierSignupList">Supplier Registration Information</a>
					</li>
				</sec:authorize>
				<sec:authorize access="hasRole('BUYER')">
					<li>
						<a href="${pageContext.request.contextPath}/buyer/buyerDashboard">Dashboard</a>
					</li>
					<li>
						<a href="${pageContext.request.contextPath}/buyer/importSupplier">Supplier Registration Information</a>
					</li>
				</sec:authorize>
				<li class="active">Supplier Detail</li>
			</ol>
			<!-- page title block -->
			<div class="Section-title title_border gray-bg">
				<h2 class="trans-cap supplier">Supplier Details</h2>
				<input type="hidden" id="supplierId" name="supplierId" value="${supplier.id}" />
				<div class="trans-cap " style="float: right; margin-right: 0px;">
					<sec:authorize access="hasRole('SUPER_ADMIN') and hasRole('OWNER')">
						<c:if test="${adminUser != null}">
							<form:form name="form" method="post" action="${pageContext.request.contextPath}/toggleAdminAccountLockedStatus" modelAttribute="adminUser">
								<div data-placement="top" title="${adminUser.locked ? 'Locked' : 'Unlocked'}" data-original-title="${adminUser.locked ? 'Locked' : 'Unlocked'}" class="panel-heading" onclick="$(this).closest('form').submit();">
									<form:hidden name="id" path="id" />
									<input type="hidden" name="supplierId" value="${supplier.id}" />
									Admin account status :
									<span style="color:${adminUser.locked ? 'red' : 'green'}"> ${adminUser.locked ? '<i class="fa fa-lock" aria-hidden="true"></i>' : '<i class="fa fa-unlock" aria-hidden="true"></i>'} </span>
								</div>
							</form:form>
						</c:if>
					</sec:authorize>
				</div>
			</div>
			<div class="clear"></div>
			<div class="col-md-12 pad0">
				<div class="panel bgnone">
					<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
					<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
					<div class="panel-body">
						<div class="example-box-wrapper">
							<div class="panel-group" id="accordion">
								<div class="panel">
									<div class="panel-heading">
										<h4 class="panel-title">
											<a data-toggle="collapse" data-parent="#accordion" href="#collapseOne">
												${supplier.companyName}
												<%-- <p style="float: right; margin-right: 30px;">Account Status: ${supplier.status}</p> --%>
											</a>
										</h4>
									</div>
									<div id="collapseOne" class="panel-collapse collapse in">
										<div class="panel-body">
											<table class="tabaccor" cellpadding="0" cellspacing="0" border="0">
												<tr>
													<td>
														<strong>Company Name</strong>
													</td>
													<td>${supplier.companyName}</td>
												</tr>
												<tr>
													<td>
														<strong><spring:message code="supplier.registration.company.number" /></strong>
													</td>
													<td>${supplier.companyRegistrationNumber}</td>
												</tr>
												<tr>
													<td>
														<strong><spring:message code="label.companystatus" /></strong>
													</td>
													<td>${supplier.companyStatus.companystatus}</td>
												</tr>
												<tr>
													<td>
														<strong><spring:message code="supplier.year.of.establish" /></strong>
													</td>
													<td>${supplier.yearOfEstablished}</td>
												</tr>
												<tr>
													<td>
														<strong><spring:message code="supplier.company.address" /></strong>
													</td>
													<td>${supplier.line1},<br> ${supplier.line2},<br> ${supplier.city}<br> ${supplier.registrationOfCountry.countryName}<br>
													</td>
												</tr>
												<tr>
													<td>
														<strong><spring:message code="supplier.company.tel.number" /></strong>
													</td>
													<td>${supplier.companyContactNumber}</td>
												</tr>
												<tr>
													<td>
														<strong><spring:message code="supplier.company.fax.number" /></strong>
													</td>
													<td>${supplier.faxNumber}</td>
												</tr>
												<tr>
													<td>
														<strong><spring:message code="supplier.company.website" /></strong>
													</td>
													<td>${supplier.companyWebsite}</td>
												</tr>
											
												<tr>
													<td>
														<strong><spring:message code="supplier.communication.email.address" /></strong>
													</td>
													<td>
														<span class="bEmail">${supplier.communicationEmail}</span>
														<c:if test="${canUpdateComEmail}">
															<input type="hidden" value="" id="editEmailValue">
															<a href="#" data-mail="${supplier.communicationEmail}" id="edit_mail">
																<i class="fa fa-pencil-square-o" aria-hidden="true"></i>
															</a>
														</c:if>
													</td>
												</tr>
												<tr>
													<td>
														<strong><spring:message code="supplier.registeration.completion.date" /></strong>
													</td>
													<td>
														<fmt:formatDate value="${supplier.registrationCompleteDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
													</td>
												</tr>
												<tr>
													<td>
														<strong>Remarks</strong>
													</td>
													<td>${supplier.remarks}</td>
												</tr>
											</table>
										</div>
									</div>
								</div>
								
								
								<div class="panel">
									<div class="panel-heading">
										<h4 class="panel-title">
											<a data-toggle="collapse" data-parent="#accordion" href="#collapseTwo"> <spring:message code="supplier.primary.contact" /> </a>
										</h4>
									</div>
									<div id="collapseTwo" class="panel-collapse collapse">
										<div class="panel-body">
											<table class="tabaccor" cellpadding="0" cellspacing="0" border="0">
												<tr>
													<td>
														<strong><spring:message code="supplier.primary.contact" /></strong>
													</td>
													<td>${supplier.fullName}</td>
												</tr>
												<tr>
													<td>
														<strong><spring:message code="supplier.designation" /></strong>
													</td>
													<td>${supplier.designation}</td>
												</tr>
												
												<tr>
													<td>
														<strong><spring:message code="supplier.communication.email.address" /></strong>
													</td>
													<td>
														<span class="bEmail">${supplier.communicationEmail}</span>
													</td>
												</tr>
												<tr>
													<td>
														<strong><spring:message code="supplier.primary.mob.number" /></strong>
													</td>
													<td>${supplier.mobileNumber}</td>
												</tr>
											</table>
										</div>
									</div>
								</div>
								
								<div class="panel">
									<div class="panel-heading">
										<h4 class="panel-title">
											<a data-toggle="collapse" data-parent="#accordion" href="#collapseThree"> PO Share Setting </a>
										</h4>
									</div>
									<div id="collapseThree" class="panel-collapse collapse">
										<div class="panel-body">
											<table class="tabaccor" cellpadding="0" cellspacing="0" border="0">
												<tr>
													<td>
														<strong>Setting</strong>
													</td>
													<td>${poShareSetting}</td>
												</tr>
											</table>
										</div>
									</div>
								</div>
								
							
								<%-- <div class="panel">
									<div class="panel-heading">
										<h4 class="panel-title">
											<a data-toggle="collapse" data-parent="#accordion" href="#collapseSix"> Documents </a>
										</h4>
									</div>
									<div id="collapseSix" class="panel-collapse collapse accortab">
										<div class="panel-body">
											<div class="borderradius">
												<table cellpadding="0" cellspacing="0" border="0">
													<c:forEach items="${supplier.supplierCompanyProfile}" var="companyPfo" varStatus="loop">
														<tr>
															<td style="font-weight: bold; font-size: 15px; width: 20%">Company Profile :</td>
															<td style="align: left">
																<form:form method="GET">
																	<c:url var="downloadComp" value="/downloadCopmanyProfile/${companyPfo.id}" />
																	<a href="${downloadComp}">${companyPfo.fileName}</a>
																</form:form>
															</td>
														</tr>
													</c:forEach>
												</table>
											</div>
											<div class="borderradius">
												<br />
												<span style="font-weight: bold; font-size: 15px; width: 20%">&nbsp;Other Profile :</span>
												<br /> <br />
												<table class="tabaccor" cellpadding="0" cellspacing="0" border="0">
													<thead>
														<th class="pad-left-15"><strong>No.</strong></th>
														<th><strong>File Name</strong></th>
														<th><strong>File Description</strong></th>
														<th><strong>Upload Date</strong></th>
													</thead>
													<tbody>
														<c:forEach items="${supplier.supplierOtherCredentials}" var="otherCred" varStatus="loop">
															<tr>
																<td>&nbsp;&nbsp; ${loop.index+1}</td>
																<td>
																	<form:form method="GET">
																		<c:url var="download" value="/downloadOtherCredential/${otherCred.id}" />
																		<a class="word-break" href="${download}">${otherCred.fileName}</a>
																	</form:form>
																</td>
																<td>${otherCred.description}</td>
																<td>
																	<fmt:formatDate value="${otherCred.uploadDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
																</td>
															</tr>
														</c:forEach>
													</tbody>
												</table>
											</div>
										</div>
									</div>
								</div> --%>
								<!-- OTHER DOCUMENTS -->
								<%-- <div class="panel">
									<div class="panel-heading">
										<h4 class="panel-title">
											<a data-toggle="collapse" data-parent="#accordion" href="#collapseOtherDocs">Other Documents</a>
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
															<div class="add_file_row">
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
																<div data-provides="fileinput" class="fileinput fileinput-new input-group">
																	<div data-trigger="fileinput" class="form-control">
																		<i class="glyphicon glyphicon-file fileinput-exists"></i>
																		<span id="idOtherFileUploadSpan" class="fileinput-filename show_name"></span>
																	</div>
																	<span class="input-group-addon btn btn-black btn-file">
																		<span class="fileinput-new" id="selectNew">Select file</span>
																		<span class="fileinput-exists" id="fileinput-exists">Change</span>
																		<input type="file" data-buttonName="btn-black" id="otherDocumentUpload" name="otherDocumentUpload" data-validation-allowing="${fileType}" data-validation-error-msg-container="#Load_File-error-dialogOtherDocs"
																			data-validation-max-size="${ownerSettings.fileSizeLimit}M" data-validation="extension size" data-validation-error-msg-size="You can not upload file larger than ${ownerSettings.fileSizeLimit}MB"
																			data-validation-error-msg-mime="${mimetypes}">
																	</span>
																	<a data-dismiss="fileinput" class="input-group-addon btn btn-default fileinput-exists" id="remove" href="#">Remove</a>
																</div>
															</div>
															<div id="Load_File-error-dialogOtherDocs" style="width: 100%; float: left; margin: 0 0 10px 0;"></div>
															<div class="row marg-bottom-20">
																<label class="col-sm-4 col-md-4 col-xs-6"> File Description </label>
																<div class=" col-md-8">
																	<textarea class="form-control" rows="3" id="otherDocumentDesc" name="otherDocumentDesc" data-validation="required" placeholder="Description"></textarea>
																</div>
															</div>
															<div class="form-group">
																<label class="col-sm-4 col-md-4 col-xs-4 "> Expiry Date </label>
																<div class="col-sm-4 col-md-4 col-xs-3">
																	<div class="input-prepend input-group">
																		<input data-placement="top" data-toggle="tooltip" id="expiryDate" class="nvclick form-control for-clander-view" data-validation-format="dd/mm/yyyy" placeholder="dd/mm/yyyy" />
																	</div>
																</div>
															</div>
															<div class="form-group">
																<div class="col-sm-4 col-md-4 col-xs-4">&nbsp;</div>
																<div class="col-sm-4 col-md-4 col-xs-4">
																	<button class="btn btn-blue btn-lg btn-block up_btn marg-bottom-20" type="button" name="OtherDocsUpload" id="OtherDocsUpload">Upload</button>
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
																<th class="align-center width_100_fix">Remove</th>
																<th class="align-left width_200">File name</th>
																<th class="align-left width_200">Description</th>
																<th class="align-left width_200_fix">Upload Date</th>
																<th class="align-left width_200_fix">Expiry Date</th>
															</tr>
														</thead>
													</table>
													<table class="data for-pad-data table" id="uploadOtherFiless">
														<tbody>
															<c:forEach items="${otherDocsList}" var="sp">
																<tr>
																	<td class="align-center width_100_fix">
																		<span class="removeOtherFile" removeOtherId='${sp.id}' otherDocFile='${sp.fileName}'>
																			<a href="">
																				<i class="fa fa-trash-o" aria-hidden="true"></i>
																			</a>
																		</span>
																	</td>
																	<td class="align-left width_200">
																		<form:form method="GET">
																			<c:url var="download" value="/supplierreg/downloadOtherDocument/${sp.id}" />
																			<a href="${download}">${sp.fileName}</a>
																		</form:form>
																	</td>
																	<td class="align-left width_200">${sp.description}</td>
																	<td class="align-left width_200_fix">
																		<fmt:formatDate value="${sp.uploadDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
																	</td>
																	<td class="align-left width_200_fix">
																		<fmt:formatDate value="${sp.expiryDate}" pattern="dd/MM/yyyy" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
																	</td>
																</tr>
															</c:forEach>
														</tbody>
													</table>
												</div>
												<div id="morris-bar-yearly" class="graph" style="visibility: hidden"></div>
											</div>
										</div>
									</div>
								</div> --%>
								
								
							</div>
						</div>
					</div>
				</div>
				<!--End of List Of Notes -->
			</div>
		</div>
		
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
				<c:url var="rejectDetails" value="rejectDetails" />
				<form class="bordered-row has-validation-callback" id="reject-form" name="reject-form" method="post" action="rejectDetails">
					<input id="rejectId" name="rejectId" type="hidden" value="">
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
					<div class="sup_pop_row">
						<div class="remark_text">Remarks</div>
						<div class="remark_field">
							<textarea class="form-control rejectRemark" data-validation="length" data-validation-length="max500"></textarea>
							<span class="sky-blue">Max 500 characters only</span>
						</div>
					</div>
					<div class="sup_pop_row">
						<div class="col-md-offset-2 col-md-10">
							<div class="col-md-5" style="padding-right: 2px">
								<button class="btn btn-info btn-block hvr-pop hvr-rectangle-out dis" id="idReject" type="submit">Reject</button>
							</div>
							<div class="col-md-5" style="padding-left: 2px">
								<button type="button" class="btn btn-black btn-block hvr-pop hvr-rectangle-out1" id="idRejectClose">Cancel</button>
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
				<h3><spring:message code="application.confirm.delete" /></h3>
			</div>
			<div class="modal-body">
				<label>Are you sure you want to delete this Document?</label>
				<input type="hidden" id="deleteOtherDocId" value="" />
				<input type="hidden" id="otherDocFileName" value="" />
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<a type="button" href="javascript:void(0);" class="closeDialog btn btn-black marg-left-10 hvr-pop ph_btn_small hvr-rectangle-out1" data-dismiss="modal">Cancel</a>
				<button id="idConfirmDeleteOtherDocument" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out">Remove</button>
			</div>
		</div>
	</div>
</div>
<!-- Update communication email dialog -->
<div class="modal fade" title="Enter Comunication Email " id="editMailModal" role="dialog">
	<div class="modal-dialog">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>Enter new communication email</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">X</button>
			</div>
			<div class="modal-body">
				<div class="row">
					<label class="col-sm-2 control-label"> </label>
					<form id="updateEmailForm">
						<div class="col-sm-6 col-md-6">
							<input type="text" style="margin-top: 0;" class="form-control mar-b10" id="newEmail" data-validation="required email">
						</div>
						<div class="col-sm-4 col-md-4">
							<button id="submitEmail" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out marg-left-20" data-dismiss="modal">Submit</button>
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
			url : getContextPath() + "/rejectSupplierDetails",
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
				window.location.href = getContextPath() + "/supplierSignupList";
				$('p[id=idGlobalSuccessMessage]').html(request.getResponseHeader('success'));
				$('div[id=idGlobalSuccess]').show();
			},
			error : function(request, textStatus, errorThrown) {
				//alert('Error: ' + request.getResponseHeader('error'));
				$('#rejectModel').modal('hide');
			}
		});
	});

	$('#idRejectClose').click(function(event) {
		$('#rejectModel').modal('hide');
	});
</script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/view/Notes.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/file-input/file-input.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/datepicker/datepicker.js"/>"></script>
<script>
	$(document).ready(function() {
		$.validate({
			lang : 'en',
			modules : 'file'
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

	});
</script>
<style>
.word-break
{
	word-break: break-all;
}
</style>
<%@ page language="java" contentType="text/html; charset=UTF-8"
		 pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">	
<sec:authorize access="hasRole('ADMIN')" var="isAdmin" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')"
	var="buyerReadOnlyAdmin" />
<spring:message var="rfsDocumentsDesk"
	code="application.pr.create.documents" />
<script type="text/javascript">
zE(function() {
	zE.setHelpCenterSuggestions({ labels: [${rfsDocumentsDesk}] });
});
</script>
<div id="page-content-wrapper">
	<div id="page-content">
		<div class="container">
			<!-- pageging  block -->
			<ol class="breadcrumb">
				<c:url var="buyerDashboard" value="/buyer/buyerDashboard" />
				<li><a id="dashboardLink" href="${buyerDashboard}"> <spring:message
							code="application.dashboard" />
				</a></li>
				<li class="active"><spring:message code="sourcing.form.Request" /></li>
			</ol>
			<section class="create_list_sectoin">
				<div class="Section-title title_border gray-bg mar-b20">
					<h2 class="trans-cap supplier">
						<spring:message code="sourcing.document" />
					</h2>
				</div>
				<jsp:include page="sourcingFormHeader.jsp"></jsp:include>
				<div class="tab-pane" style="display: block">
					<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
					<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
					<div class="Invited-Supplier-List white-bg">
						<h4>Documents</h4>
						<c:set var="fileType" value="" />
						<c:forEach var="type" items="${ownerSettings.fileTypes}" varStatus="index">
							<c:set var="fileType" value="${fileType}${index.first ? '': ', '}${type}" />
						</c:forEach>
						<c:if test="${eventPermissions.owner or eventPermissions.editor}">
							<form:form class="form-horizontal" id="rfsDocumentForm">
								<input type="hidden" value="${formId}" id="formId" name="formId">
								<input type="hidden" name="${_csrf.parameterName}"
									   value="${_csrf.token}" />
								<div class="row">
									<div class="col-md-12 ${event.isTemplateUsed ? 'disabled':''}">
										<div class="col-md-4 pad_all_15">
											<div data-provides="fileinput" class="fileinput fileinput-new input-group">
												<spring:message code="event.doc.file.required" var="required" />
												<spring:message code="event.doc.file.length" var="filelength" />
												<spring:message code="event.doc.file.mimetypes" var="mimetypes" />
												<div data-trigger="fileinput" class="form-control">
													<span class="fileinput-filename show_name"></span>
												</div>
												<span class="input-group-addon btn btn-black btn-file">
												<span class="fileinput-new">
													<spring:message code="event.document.selectfile" />
												</span>
												<span class="fileinput-exists">
													<spring:message code="event.document.change" />
												</span>
												<input data-validation-allowing="${fileType}"
													   data-validation="extension size"
													   data-validation-error-msg-container="#Load_File-error-dialog"
													   data-validation-max-size="${ownerSettings.fileSizeLimit}M"
													   type="file" name="rfsUploadDocument"
													   id="rfsUploadDocument"
													   data-validation-error-msg-required="${required}"
													   data-validation-error-msg-size="You cannot upload file larger than ${ownerSettings.fileSizeLimit}MB"
													   data-validation-error-msg-mime="${mimetypes}">
											</span>
											</div>
											<div id="Load_File-error-dialog" style="width: 100%; float: left; margin: 0 0 0 0;"></div>
											<div class="progressbar flagvisibility" data-value="0">
												<div class="progressbar-value bg-purple">
													<div class="progress-overlay"></div>
													<div class="progress-label">0%</div>
												</div>
											</div>
											<span>
											<spring:message code="application.note" />:<br />
											<ul>
												<li><spring:message code="createrfi.documents.max.size" /> ${ownerSettings.fileSizeLimit} MB</li>
												<li><spring:message code="createrfi.documents.allowed.file.extension" />: ${fileType}.</li>
											</ul>
										</span>
										</div>
										<div class="col-md-5 pad_all_15">
											<spring:message code="event.doc.file.descrequired" var="descrequired" />
											<spring:message code="event.doc.file.maxlimit" var="maxlimit" />
											<spring:message code="event.document.filedesc" var="filedesc" />
											<input class="form-control"
												   name="docDescription" id="docDescription"
												   data-validation="length" data-validation-length="max250"
												   type="text" placeholder="${filedesc} ${maxlimit}">
										</div>
										<div class="col-md-1 pad_all_15">
											<button class="upload_btn btn btn-info   hvr-pop hvr-rectangle-out" type="button" name="uploadRfsDoc" id="uploadRfsDoc">
												<spring:message code="event.document.upload" />
											</button>
										</div>
										<div class="col-md-2 pad_all_15" style="">
											<label style="margin-top: 5%;"> <spring:message code="eventDocument.internal.document" /></label> &nbsp;&nbsp;
											<input id="internal" value="internal" class="internal" type="checkbox" checked/>
										</div>
									</div>
								</div>
							</form:form>
						</c:if>
						<div class="Invited-Supplier-List-table pad_all_15 documentapproval">
							<div class="ph_table_border">
								<div class="mega">
									<table class="header ph_table border-none" width="100%"
										border="0" cellspacing="0" cellpadding="0">
										<thead>
											<tr>
												<th class="width_200 width_200_fix align-center"><spring:message
														code="application.action" /></th>
												<th class="width_200 width_200_fix align-left"><spring:message
														code="event.document.filename" /></th>
												<th class="width_300 width_300_fix align-left"><spring:message
														code="event.document.description" /></th>
												<th class="width_100 width_100_fix align-left"><spring:message
														code="event.document.fileSize" /></th>
												<th class="width_200 width_200_fix align-left">Upload
													Date</th>
												<th class="width_200 width_200_fix align-left"><spring:message
														code="event.document.uploadby"></spring:message></th>
												<th class="width_100 width_100_fix align-left"><spring:message
														code="event.document.internal" /></th>
											</tr>
										</thead>
									</table>
									<table class="data ph_table border-none" width="100%"
										border="0" cellspacing="0" cellpadding="0" id="prDocList">
										<tbody>
											<c:forEach var="doc"
												items="${sourcingFormRequest.rfsDocuments}">
												<tr>
													<td class="width_200 width_200_fix align-center">
														<form method="GET">
															<input type="hidden" name="${_csrf.parameterName}"
																value="${_csrf.token}" />
															<c:url var="downloadRfsDocument"
																value="/buyer/downloadRfsDocument/${doc.id}" />
															<a id="downloadButton" href="${downloadRfsDocument}"
																data-placement="top" title="Download"> <img
																src="${pageContext.request.contextPath}/resources/images/download.png">
															</a> &nbsp;

															<c:if test="${doc.editorMember}">
																<span> <a href="" data-placement="top"
																	title="Delete" class="removeDocFile"
																	removeDocId=" ${doc.id}"> <img
																		src="${pageContext.request.contextPath}/resources/images/delete.png">
																</a>
																</span> &nbsp; <a href="" data-placement="top" title="Edit"><span
																	class="editDocFile ${buyerReadOnlyAdmin ? 'disabled' : ''}"
																	id="eventDocDesc" editDocId=" ${doc.id}"
																	editDocDec="<c:out value='${doc.description}'/>" eventDocInternal="${doc.internal}"> <img
																		src="${pageContext.request.contextPath}/resources/images/edit1.png">
																</span> </a>
															</c:if>

															<c:if
																test="${!doc.editorMember && !eventPermissions.editor}">
																<c:if test="${eventPermissions.owner}">
																	<span> <a href="" data-placement="top"
																		title="Delete" class="removeDocFile"
																		removeDocId=" ${doc.id}"> <img
																			src="${pageContext.request.contextPath}/resources/images/delete.png">
																	</a>
																	</span> &nbsp; <a href="" data-placement="top" title="Edit"><span
																		class="editDocFile ${buyerReadOnlyAdmin ? 'disabled' : ''}"
																		id="eventDocDesc" editDocId=" ${doc.id}"
																		editDocDec="<c:out value='${doc.description}'/>" eventDocInternal="${doc.internal}"> <img
																			src="${pageContext.request.contextPath}/resources/images/edit1.png">
																	</span> </a>
																</c:if>
															</c:if>


														</form>
													</td>
													<td class="width_200 width_200_fix align-left">${doc.fileName}</td>
													<td class="width_300 width_300_fix align-left">${doc.description}</td>
													<td class="width_100 width_100_fix align-left" id="size">
														<fmt:formatNumber type="number" maxFractionDigits="1" value="${doc.fileSizeInKb}" />KB
													</td>
													<fmt:formatDate var="uploadDate" value="${doc.uploadDate}"
														pattern="dd/MM/yyyy hh:mm a"
														timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
													<td class="width_200 width_200_fix align-left">${uploadDate}</td>
													<td class="width_200 width_200_fix align-left">${doc.uploadBy.name}</td>
													<td class="width_100 width_100_fix align-left">
														<c:if test="${doc.internal == true}">
															<spring:message code="eventDocument.document.internal" />
														</c:if>
														<c:if test="${doc.internal == false}">
															<spring:message code="event.document.external" />
														</c:if>
													</td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</div>
							</div>
						</div>
					</div>

					<div class="marg-top-10 btns-lower">
						<div class="row">
							<div class="col-md-12 col-xs-12 col-ms-12">
								<c:url var="createRfsDetails"
									value="/buyer/createSourcingFormDetails/${sourcingFormRequest.id}" />
								<a href="${createRfsDetails}" id="previousButton"
									class="top-marginAdminList btn btn-black ph_btn marg-bottom-10 hvr-pop hvr-rectangle-out1">
									<spring:message code="application.previous" />
								</a>
								<c:url var="rfsDocumentNext"
									value="/buyer/rfsDocumentNext/${sourcingFormRequest.id}" />
								<a href="${rfsDocumentNext}" id="nextButton"
									class=" top-marginAdminList btn btn-info ph_btn marg-left-10 marg-bottom-10 hvr-pop hvr-rectangle-out">
									<spring:message code="application.next" />
								</a>
								<c:if test="${(isAdmin or eventPermissions.owner)}">
									<a href="#confirmCancelRequest" role="button" class="top-marginAdminList btn btn-danger ph_btn hvr-pop pull-right" data-toggle="modal"><spring:message code="rfs.summary.cancel.request" /></a>
								</c:if>
							</div>
						</div>
					</div>
				</div>
			</section>
		</div>
	</div>
</div>
<div class="modal fade" id="confirmDeleteDocument" role="dialog">
	<div class="modal-dialog for-delete-all reminder documentBlock">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="application.confirm.delete" />
				</h3>
				<button class="close for-absulate" type="button"
					data-dismiss="modal">×</button>
			</div>
			<div class="modal-body">
				<label> <spring:message
						code="application.confirm.message.delete" />
				</label> <input type="hidden" id="deleteIdDocument" />
			</div>
			<div
				class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<button type="button"
					class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out"
					id="confDelDocument">
					<spring:message code="application.delete" />
				</button>
				<button type="button"
					class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right"
					data-dismiss="modal">
					<spring:message code="application.cancel" />
				</button>
			</div>
		</div>
	</div>
</div>

<!-- RFS DOCUMENT DESCRIPTION -->
<div class="flagvisibility dialogBox" id="documentDescriptionPopup"
	title="RFS Document Decription">
	<div class="float-left width100 pad_all_15 white-bg">
		<div class="row">
			<input type="hidden" id="editIdDocument" name="docId" />
			<div class="col-md-12">
				<div class="form-group col-md-6  marg-top-20">
					<spring:message code="event.doc.file.maxlimit" var="maxlimit" />
					<textarea class="width-100 form-control" id="docDec" value=""
						placeholder="${filedesc}" rows="3" data-validation="length"
						data-validation-length="max250" maxlength="250"></textarea>
					<spring:message code="event.document.filedesc" var="filedesc" />
					<span class="sky-blue">${maxlimit}</span>
				</div>
				<div class="form-group col-md-6">
					<label> <spring:message code="eventDocument.internal.document" /></label> &nbsp;&nbsp;
					<input id="internal_1" value="internal" class="internal_1" type="checkbox" data-internal="${doc.internal}"/>
				</div>
			</div>
			<div class="row">
				<div class="col-md-12">
					<div class="align-center">
						<a href="javascript:void(0);" title=""
							class="btn btn-info ph_btn_small btn-tooltip hvr-pop hvr-rectangle-out"
							id="confDocDec" data-original-title="Delete">Update</a>
						<button type="button"
							class="closeDialog btn btn-black marg-left-10 hvr-pop ph_btn_small hvr-rectangle-out1">Cancel</button>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<!-- cancel Event popup  -->
<div class="modal fade" id="confirmCancelRequest" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="eventsummary.confirm.cancel" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<form  action="${pageContext.request.contextPath}/buyer/cancelSourcingReq/${sourcingFormRequest.id}" method="post">
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<div class="col-md-12">
					<div class="row">
						<div class="modal-body col-md-12">
							<label> <spring:message code="request.confirm.to.cancel" /> </br>
							</label>
						</div>
						<div class="form-group col-md-6">
							<spring:message code="event.reason.cancellation.placeholder" var="reasonCancel" />
							<textarea name="description" class="width-100" placeholder="${reasonCancel}" id="reasonCancel" rows="3" data-validation="required length" data-validation-length="max500" ></textarea>
						</div>
					</div>
				</div>
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
					<button type="submit" id="RfsCancelRequest" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left">
						<spring:message code="application.yes" />
					</button>
					<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
						<spring:message code="application.no2" />
					</button>
				</div>
			</form>
			</div>
	</div>
</div>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script>
	$.validate({ lang : 'en',
		modules : 'file' });
</script>
<script type="text/javascript"
	src="<c:url value="/resources/assets/widgets/file-input/file-input.js"/>"></script>
<script>
	<c:if test="${eventPermissions.viewer or buyerReadOnlyAdmin or eventPermissions.approverUser or sourcingFormRequest.status ne 'DRAFT'}">
	$(window).bind('load', function() {
		var allowedFields = '.accordion,#nextButton,#dashboardLink,#previousButton,#bubble, #downloadButton';
		//var disableAnchers = ['#reloadMsg'];        
		disableFormFields(allowedFields);
	});
	</c:if>
</script>
<style>
.documentapproval {
	overflow-wrap: break-word;
}
</style>

<script type="text/javascript"
	src="<c:url value="/resources/js/view/rfsDocument.js?3"/>"></script>
<script type="text/javascript"
	src="<c:url value="/resources/assets/widgets/progressbar/progressbar.js"/>"></script>
<link rel="stylesheet" type="text/css"
	href="<c:url value="/resources/assets/widgets/progressbar/progressbar.css"/>">
<script type="text/javascript" src="<c:url value="/resources/js/view/modalValidation.js"/>"></script>
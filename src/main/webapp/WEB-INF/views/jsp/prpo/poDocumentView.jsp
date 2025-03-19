<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize access="hasRole('ADMIN')" var="isAdmin" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<spring:message var="poDocumentsDesk" code="application.po.create.documents" />
<script type="text/javascript">
zE(function() {
	zE.setHelpCenterSuggestions({ labels: [${poDocumentsDesk}] });
});
</script>
<div id="page-content-wrapper">
	<div id="page-content">
		<div class="container">
			<!-- pageging  block -->
			<ol class="breadcrumb">
				<c:url var="buyerDashboard" value="/buyer/buyerDashboard" />
				<li><a id="dashboardLink" href="${buyerDashboard}"> <spring:message code="application.dashboard" />
				</a></li>
				<li class="active"><spring:message code="po.purchase.order" /></li>
			</ol>
			<section class="create_list_sectoin">
				<div class="Section-title title_border gray-bg mar-b20">
					<h2 class="trans-cap supplier">
						<spring:message code="po.purchase.order" />
					</h2>
					<h2 class="trans-cap pull-right">
                        <spring:message code="application.status" />
                        : ${po.status}
                    </h2>
				</div>
				<jsp:include page="poHeader.jsp"></jsp:include>
				<div class="tab-pane" style="display: block">

				    <div class="clear"></div>
				    <jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
                    <jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
                    <jsp:include page="poTagHeader.jsp"></jsp:include>
                    <div class="clear"></div>

					<div class="Invited-Supplier-List white-bg">
						<h4><spring:message code="eventdescription.document.label" /></h4>
						<c:set var="fileType" value="" />
						<c:forEach var="type" items="${ownerSettings.fileTypes}" varStatus="index">
							<c:set var="fileType" value="${fileType}${index.first ? '': ', '}${type}" />
						</c:forEach>
						<form action="" id="poDocumentForm">
							<input type="hidden" id="poId" value="${po.id}" name="poId"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
							<div class="row">
								<div class="col-md-12">
									<div class="col-md-4 pad_all_15">
										<div class="fileinput fileinput-new input-group" data-provides="fileinput">
											<spring:message code="meeting.doc.file.length" var="filelength" />
											<spring:message code="meeting.doc.file.mimetypes" var="mimetypes" />
											<div data-trigger="fileinput" class="form-control">
												<span class="fileinput-filename show_name"></span>
											</div>
											<span class="input-group-addon btn btn-black btn-file">
												<span class="fileinput-new">
													<spring:message code="application.selectfile" />
												</span>
												<span class="fileinput-exists">
													<spring:message code="event.document.change" />
												</span>
												<input name="poUploadDocument" id="poUploadDocument" accept=".pdf, .doc, .docx, .xls, .xlsx, .jpeg, .zip, .rar, .png, .ppt, .pptx, .txt" data-validation-allowing="${fileType}" data-validation-error-msg-container="#Load_File-error-dialog" data-validation-max-size="${ownerSettings.fileSizeLimit}M" type="file" data-validation="extension size"
													data-validation-error-msg-size="You can not upload file larger than ${ownerSettings.fileSizeLimit}MB" data-validation-error-msg-mime="${mimetypes}">
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
									<div class="col-md-6 pad_all_15">
										<spring:message code="event.doc.file.descrequired" var="descrequired" />
										<spring:message code="event.doc.file.maxlimit" var="maxlimit" />
										<spring:message code="event.document.filedesc" var="filedesc" />
										<input class="form-control" name="docDescription" id="docDescription" data-validation="length" data-validation-length="max250" type="text" placeholder="${filedesc} ${maxlimit}">
									</div>
									<div class="col-md-2 pad_all_15">
										<button class="upload_btn btn btn-info  ph_btn_midium hvr-pop hvr-rectangle-out" type="button" name="uploadPoDoc" id="uploadPoDoc">
											<spring:message code="application.upload" />
										</button>
									</div>
								</div>
							</div>
						</form>
						<div class="Invited-Supplier-List-table pad_all_15 add-supplier">
							<div class="ph_table_border">
								<div class="mega">
									<table class="header ph_table border-none" width="100%" border="0" cellspacing="0" cellpadding="0">
										<thead>
											<tr>
												<th class="width_200 width_200_fix align-center">
													<spring:message code="application.action" />
												</th>
												<th class="width_200 width_200_fix align-left">
													<spring:message code="event.document.filename" />
												</th>
												<th class="width_300 width_300_fix align-left">
													<spring:message code="event.document.description" />
												</th>
												<th class="width_200 width_200_fix align-left"><spring:message code="event.document.publishdate" /></th>
											</tr>
										</thead>
									</table>
									<table class="data ph_table border-none" width="100%" border="0" cellspacing="0" cellpadding="0" id="poDocList">
										<tbody>
											<c:forEach var="doc" items="${poDocsList}">
												<tr>
													<td class="width_200 width_200_fix align-center">
														<form method="GET">
															<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
															<c:url var="downloadPoDocument" value="/buyer/po/downloadPoDocument/${doc.id}" />
															<a id="downloadButton" href="${downloadPoDocument}" data-placement="top" title='<spring:message code="tooltip.download" />' > <img src="${pageContext.request.contextPath}/resources/images/download.png">
															</a> &nbsp;
															<span>
																<a href="" data-placement="top" title='<spring:message code="tooltip.delete" />' class="removeDocFile" removeDocId=" ${doc.id}"> <img src="${pageContext.request.contextPath}/resources/images/delete.png">
																</a>
															</span>
															&nbsp; <a href="" data-placement="top" title='<spring:message code="tooltip.edit" />' ><span class="editDocFile ${buyerReadOnlyAdmin ? 'disabled' : ''}" id="eventDocDesc" editDocId=" ${doc.id}" editDocDec='<c:out value="${doc.description}" />'>
																	<img src="${pageContext.request.contextPath}/resources/images/edit1.png">
																</span> </a>
														</form>
													</td>
													<td class="width_200 width_200_fix align-left">${doc.fileName}</td>
													<td class="width_300 width_300_fix align-left">${doc.description}</td>
													<fmt:formatDate var="uploadDate" value="${doc.uploadDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
													<td class="width_200 width_200_fix align-left">${uploadDate}</td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</div>
							</div>
						</div>
					</div>
					<div class="marg-top-10 btns-lower">
					    <c:if test="${(po.status eq 'DRAFT' || po.status eq 'SUSPENDED') && eventPermissions.owner}">
                            <div class="row">
                                <div class="col-md-12 col-xs-12 col-ms-12">
                                    <c:url var="createPoDetails" value="/buyer/poCreate/${po.id}?prId=${pr.id}" />
                                    <a href="${createPoDetails}" id="previousButton" class="top-marginAdminList btn btn-black ph_btn marg-bottom-10 hvr-pop hvr-rectangle-out1"> <spring:message code="application.previous" />
                                    </a>
                                    <c:url var="documentNext" value="/buyer/po/poDocumentNext/${po.id}?prId=${pr.id}" />
                                    <a href="${documentNext}" id="nextButton" class=" top-marginAdminList btn btn-info ph_btn marg-left-10 marg-bottom-10 hvr-pop hvr-rectangle-out"> <spring:message code="application.next" />
                                    </a>
                                    <spring:message code="application.draft" var="draft" />
                                    <c:url var="savePoDraft" value="/buyer/po/savePoDraft/${po.id}?prId=${pr.id}" />
                                    <a href="${savePoDraft}"> <input type="button" id="submitStep1PrDetailDraft" class="top-marginAdminList step_btn_1 btn btn-black hvr-pop hvr-rectangle-out1 ph_btn hvr-pop submitStep1 pull-right" value="${draft}" />
                                    </a>
                                    <c:if test="${(po.status eq 'DRAFT' || po.status eq 'SUSPENDED' ) && eventPermissions.owner}">
                                        <a href="#confirmCancelPo" role="button" class="btn btn-danger marg-top-20 marg-right-10 ph_btn hvr-pop right-header-button" data-toggle="modal"><spring:message code="po.cancel.label" /></a>
                                    </c:if>
                                </div>
                            </div>
						</c:if>
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
				<button class="close for-absulate" type="button" data-dismiss="modal">×</button>
			</div>
			<div class="modal-body">
				<label>
					<spring:message code="application.confirm.message.delete" />
				</label>
				<input type="hidden" id="deleteIdDocument" />
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<button type="button" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" id="confDelDocument">
					<spring:message code="application.delete" />
				</button>
				<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
					<spring:message code="application.cancel" />
				</button>
			</div>
		</div>
	</div>
</div>

<!-- PO DOCUMENT DESCRIPTION -->
<div class="flagvisibility dialogBox" id="documentDescriptionPopup" title="PR Document Decription">
	<div class="float-left width100 pad_all_15 white-bg">
		<div class="row">
			<input type="hidden" id="editIdDocument" name="docId" />
			<div class="col-md-12">
				<div class="form-group col-md-6  marg-top-20">
					<spring:message code="event.doc.file.maxlimit" var="maxlimit" />
					<textarea class="width-100 form-control" id="docDec" value="" placeholder="${filedesc}" rows="3" data-validation="length" data-validation-length="max250" maxlength="250"></textarea>
					<spring:message code="event.document.filedesc" var="filedesc" />
					<span class="sky-blue">${maxlimit}</span>
				</div>
			</div>
			<div class="row">
				<div class="col-md-12">
					<div class="align-center">
						<a href="javascript:void(0);" title="" class="btn btn-info ph_btn_small btn-tooltip hvr-pop hvr-rectangle-out" id="confDocDec" data-original-title="Delete"><spring:message code="application.update"/></a>
						<button type="button" class="closeDialog btn btn-black marg-left-10 hvr-pop ph_btn_small hvr-rectangle-out1"><spring:message code="application.cancel" /></button>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<jsp:include page="poModal.jsp"></jsp:include>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/file-input/file-input.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/view/po.js"/>"></script>
<script>


	<c:if test="${eventPermissions.viewer or buyerReadOnlyAdmin or eventPermissions.approverUser}">
	$(window).bind('load', function() {
		var allowedFields = '#nextButton,#dashboardLink,#previousButton,#bubble, #downloadButton';
		//var disableAnchers = ['#reloadMsg'];        
		disableFormFields(allowedFields);
	});
	</c:if>
	$.validate({
		lang : 'en',
		modules : 'file'
	});
</script>
<script type="text/javascript" src="<c:url value="/resources/js/view/poDocument.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/progressbar/progressbar.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/progressbar/progressbar.css"/>">
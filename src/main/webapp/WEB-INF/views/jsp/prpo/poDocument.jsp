<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="Invited-Supplier-List white-bg">
	<h4><spring:message code="podocument" /></h4>
	<c:set var="fileType" value="" />
	<c:forEach var="type" items="${ownerSettings.fileTypes}" varStatus="index">
		<c:set var="fileType" value="${fileType}${index.first ? '': ', '}${type}" />
	</c:forEach>
	<form action="" id="prDocumentForm">
	<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
		<input type="hidden" id="prId" value="${prId}" name="prId">
		<div class="row">
			<div class="col-md-12">
				<div class="col-md-4 pad_all_15">
					<label><spring:message code="application.selectfile2" /></label>
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
							<input name="poUploadDocument" id="poUploadDocument" data-validation-allowing="${fileType}" data-validation-error-msg-container="#Load_File-error-dialog" data-validation-max-size="${ownerSettings.fileSizeLimit}M" type="file"
								data-validation="extension size" data-validation-error-msg-size="You can not upload file larger than ${ownerSettings.fileSizeLimit}MB" data-validation-error-msg-mime="${mimetypes}">
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
				<div class="col-md-4 pad_all_15">
					<spring:message code="event.doc.file.descrequired" var="descrequired" />
					<spring:message code="event.doc.file.maxlimit" var="maxlimit" />
					<spring:message code="event.document.filedesc" var="filedesc" />
					<label><spring:message code="podocument.file.description" /></label>
					<input class="form-control" name="docDescription" id="docDescription" data-validation="length" data-validation-length="max250" type="text" placeholder="${filedesc} ${maxlimit}">
				</div>
				<div class="col-md-2 pad_all_15">
					<label><spring:message code="podocument.select.type" /></label>
					<select id="docType" name="docType" class="form-control chosen-select disablesearch">
						<c:forEach var="docType" items="${poDocType}">
							<option value="${docType}">${docType}</option>
						</c:forEach>
					</select>
				</div>
				<div class="col-md-2 pad_all_15">
					<label>&nbsp;</label>
					<br />
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
							<th class="width_100 width_100_fix align-center">
								<spring:message code="application.action" />
							</th>
							<th class="width_200 width_200_fix align-left">
								<spring:message code="event.document.filename" />
							</th>
							<th class="width_300 width_300_fix align-left">
								<spring:message code="event.document.description" />
							</th>
							<th class="width_100 width_100_fix align-left"><spring:message code="podocument.document.type" /></th>
							<th class="width_150 width_150_fix align-left"><spring:message code="event.document.publishdate" /></th>
						</tr>
					</thead>
				</table>
				<table class="data ph_table border-none" width="100%" border="0" cellspacing="0" cellpadding="0" id="poDocList">
					<tbody>
						<c:forEach var="poDoc" items="${listPoDocs}">
							<tr>
								<td class="width_100 width_100_fix align-center">
									<form method="GET">
									<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
										<c:url var="downloadPoDocument" value="/buyer/downloadPoDocument/${poDoc.id}" />
										<a id="downloadButton" href="${downloadPoDocument}"  data-placement="top" title='<spring:message code="tooltip.download2" />'>
											<img src="${pageContext.request.contextPath}/resources/images/download.png">
										</a>
										&nbsp;
										<span>
											<a href="" class="removeDocFile" removeDocId=" ${poDoc.id}"  data-placement="top" title='<spring:message code="tooltip.delete" />'>
												<img src="${pageContext.request.contextPath}/resources/images/delete.png">
											</a>
										</span>
										&nbsp;
										<span class="editDocFile ${buyerReadOnlyAdmin ? 'disabled' : ''}" id="eventDocDesc" editDocId=" ${poDoc.id}" editDocDec="${poDoc.description}" editDocType="${poDoc.docType}">
											<a href="" data-placement="top" title='<spring:message code="tooltip.edit" />'>
												<img src="${pageContext.request.contextPath}/resources/images/edit1.png">
											</a>
										</span>
									</form>
								</td>
								<td class="width_200 width_200_fix align-left">${poDoc.fileName}</td>
								<td class="width_300 width_300_fix align-left">${poDoc.description}</td>
								<fmt:formatDate var="uploadDate" value="${poDoc.uploadDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
								<td class="width_100 width_100_fix align-left">${poDoc.docType}</td>
								<td class="width_150 width_150_fix align-left">${uploadDate}</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</div>























<%-- <div class="panel sum-accord upload_download_wrapper clearfix marg-top-10 event_info">
	<div class="panel-heading">
		<h4 class="panel-title list-pad-0" style="margin-bottom: 0px;">
			<a data-toggle="collapse" data-parent="#accordion" class="accordion" href="#collapsePODoc">PO Documents </a>
		</h4>
	</div>
	<div id="collapsePODoc" class="panel-collapse collapse in">
		<div class="panel-body mega">
			<table class="tabaccor padding-none-td header" width="100%" cellpadding="0" cellspacing="0" border="0">
				<thead>
					<tr>
						<th class="width_200 width_200_fix align-left wo-rp">Name</th>
						<th class="width_200 width_200_fix align-left wo-rp">Description</th>
						<th class="width_100 width_100_fix align-left wo-rp">Date & Time</th>
						<th class="width_100 width_100_fix align-left wo-rp">Size</th>
					</tr>
				</thead>
			</table>
			<table width="100%" cellpadding="0" cellspacing="0" border="0" class="deta pad-for-table">
				<tbody>
					<c:forEach var="listDoc" items="${listDocs}">
						<tr>
							<td class="width_200 width_200_fix align-left wo-rp">
								<a class="bluelink" href="${pageContext.request.contextPath}/buyer/downloadPrSummaryDocument/${listDoc.id}"> ${listDoc.fileName}</a>
							</td>
							<td class="width_200 width_200_fix align-left wo-rp">${listDoc.description}</td>
							<td class="width_100 width_100_fix align-left wo-rp">
								<fmt:formatDate value="${listDoc.uploadDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
							</td>
							<td class="width_100 width_100_fix align-left wo-rp">${listDoc.fileSizeInKb}KB</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
</div> --%>

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

<!-- Update PO DOCUMENT -->
<div class="flagvisibility dialogBox" id="documentDescriptionPopup" title="PO Document Decription">
	<div class="float-left width100 pad_all_15 white-bg">
		<div class="row">
			<input type="hidden" id="editIdDocument" name="docId" />
			<div class="col-md-12">
				<div class="form-group col-md-6">
				<label><spring:message code="podocument.file.description" /></label>
					<spring:message code="event.doc.file.maxlimit" var="maxlimit" />
					<textarea class="width-100 form-control" id="docDec" value="" placeholder="${filedesc}" rows="3" data-validation="length" data-validation-length="max250" maxlength="250"></textarea>
					<spring:message code="event.document.filedesc" var="filedesc" />
					<span class="sky-blue">${maxlimit}</span>
				</div>
				<div class="form-group col-md-6">
					<label><spring:message code="podocument.select.type" /></label>
					<select id="docTypeUpdate" name="docType" class="form-control chosen-select disablesearch">
						<c:forEach var="docType" items="${poDocType}">
							<option value="${docType}">${docType}</option>
						</c:forEach>
					</select>
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
<%-- <script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script> --%>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/file-input/file-input.js"/>"></script>
<!-- <script>
	<c:if test="${eventPermissions.viewer or buyerReadOnlyAdmin}">
        $(window).bind('load',function() {
            var allowedFields = '#nextButton,#dashboardLink,#previousButton,#bubble, #downloadButton';
            //var disableAnchers = ['#reloadMsg'];
            disableFormFields(allowedFields);
        });
	</c:if>
	$.validate({
		lang : 'en',
		modules : 'file'
	});
</script> -->
<script type="text/javascript" src="<c:url value="/resources/js/view/poDocument.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/progressbar/progressbar.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/progressbar/progressbar.css"/>">
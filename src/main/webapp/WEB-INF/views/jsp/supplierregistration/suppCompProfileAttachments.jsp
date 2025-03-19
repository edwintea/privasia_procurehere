<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<div id="page-content-wrapper">
	<div id="page-content">
		<div class="container">
			<div class="clear"></div>
			<jsp:include page="supplierProfileDetails.jsp" />
			<div class="tab-main-inner pad_all_15">
				<div class="content-box">
					<h3 class="content-box-header">
						<spring:message code="application.companyprofile" /> <small class="sub_text">
							<spring:message code="supplier.registration.company.administrator" /></small>
					</h3>
					<div class="content-box-wrapper">
						<%-- <jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" /> --%>
						<div class="row">
							<c:set var="fileType" value="" />
							<c:forEach var="type" items="${ownerSettings.fileTypes}" varStatus="index">
								<c:set var="fileType" value="${fileType}${index.first ? '': ', '}${type}" />
							</c:forEach>
							<span class="marg-left-10">
								<spring:message code="application.note" />:<br />
								<ul>
									<li>
										<spring:message code="createrfi.documents.max.size" />
										${ownerSettings.fileSizeLimit} MB</li>
									<li>
										<spring:message code="createrfi.documents.allowed.file.extension" />:
										${fileType}.</li>
								</ul>
							</span>
							<div class="col-sm-12">
								<div class="col-xs-12 col-sm-6">
									<section class="">
										<form id="companyProfileForm">
											<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
											<h3 class="blue_form_sbtitle p_t20">
												<spring:message code="supplier.registration.company.profile" />
											</h3>
											<div data-provides="fileinput" class="fileinput fileinput-new input-group">
												<div data-trigger="fileinput" class="form-control">
													<i class="glyphicon glyphicon-file fileinput-exists"></i> <span
														id="idProfilefileUploadSpan"
														class="fileinput-filename show_name1"></span>
												</div>
												<span class="input-group-addon btn btn-black btn-file"> <span
														class="fileinput-new">
														<spring:message code="event.document.selectfile" /></span> <span
														class="fileinput-exists">
														<spring:message code="application.change" /></span> <input
														type="file" id="companyProfile" name="companyProfile"
														data-buttonName="btn-black"
														data-validation-allowing="${fileType}"
														data-validation-error-msg-container="#Load_File-error-dialog"
														data-validation-max-size="${ownerSettings.fileSizeLimit}M"
														data-validation="extension size"
														data-validation-error-msg-size="You can not upload file larger than ${ownerSettings.fileSizeLimit}MB"
														data-validation-error-msg-mime="${mimetypes}">
												</span>
												<a data-dismiss="fileinput"
													class="input-group-addon btn btn-default fileinput-exists" href="#">
													<spring:message code="application.remove" /></a>
											</div>
											<div id="Load_File-error-dialog"
												style="width: 100%; float: left; margin: 0 0 10px 0;"></div>
											<div class="form-group other_attachemts"
												style="margin-left: 0; margin-right: 0;">
												<button class="btn btn-gray btn-lg btn-block " type="button"
													name="companyProfileUpload" id="companyProfileUpload">
													<spring:message code="supplier.upload.button" /></button>
											</div>
										</form>
									</section>
								</div>
								<div class="col-xs-12 col-sm-6">
									<div class="step_table mega">
										<table class="table header table-admin">
											<thead>
												<tr>
													<th class="width-60">
														<spring:message code="supplierprofile.filename" />
													</th>
													<th class="width-40">
														<spring:message code="application.upload.date" />
													</th>
												</tr>
											</thead>
										</table>
										<table class="data for-pad-data table" id="uploadCompnayProfileDisplay">
											<tbody>
												<c:forEach items="${uploadCompnayDetails}" var="sp">
													<tr>
														<td class="width-60">
															<form:form method="GET">
																<c:url var="download"
																	value="/downloadCompanyProfile/${sp.id}" />
																<a class="word-break" href="${download}">${sp.fileName}</a>
															</form:form>
														</td>
														<td class="width-40">
															<span class="removeProfileFile" removeProfileId='${sp.id}'
																companyFileName='${sp.fileName}'> 
																<span class="col-sm-10 no-padding">
																	<fmt:formatDate value="${sp.uploadDate}"
																		pattern="dd/MM/yyyy hh:mm a"
																		timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
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
								</div>
							</div>
							<div class="col-sm-12">
								<div class="col-xs-12 col-sm-6">
									<div class="other_attachemts">
										<h3 class="blue_form_sbtitle p_t20">
											<spring:message code="supplier.registration.attachcredential" />
										</h3>
										<ul class="add_more_feture_ul"></ul>
										<form id="otherCredentialUploadForm">
											<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
											<div class="add_file_row">
												<div data-provides="fileinput"
													class="fileinput fileinput-new input-group">
													<div data-trigger="fileinput" class="form-control">
														<i class="glyphicon glyphicon-file fileinput-exists"></i> <span
															id="idOtherFileUploadSpan"
															class="fileinput-filename show_name"></span>
													</div>
													<span class="input-group-addon btn btn-black btn-file"> <span
															class="fileinput-new">
															<spring:message code="event.document.selectfile" /></span>
														<span class="fileinput-exists">
															<spring:message code="application.change" /></span> <input
															type="file" data-buttonName="btn-black"
															id="otherCredentialUpload" name="otherCredentialUpload"
															data-validation-allowing="${fileType}"
															data-validation-error-msg-container="#Load_File-error-dialogOtherCredential"
															data-validation-max-size="${ownerSettings.fileSizeLimit}M"
															data-validation="extension size"
															data-validation-error-msg-size="You can not upload file larger than ${ownerSettings.fileSizeLimit}MB"
															data-validation-error-msg-mime="${mimetypes}">
													</span>
													<a data-dismiss="fileinput"
														class="input-group-addon btn btn-default fileinput-exists"
														href="#">
														<spring:message code="application.remove" /></a>
												</div>
											</div>
											<div id="Load_File-error-dialogOtherCredential"
												style="width: 100%; float: left; margin: 0 0 10px 0;"></div>
											<div class="form-group" style="margin-left: 0; margin-right: 0;">
												<textarea class="form-control" rows="3" id="otherCredentialDesc"
													name="otherCredentialDesc"
													placeholder='<spring:message code="supplierprofile.description.placeholder" />'></textarea>
											</div>
											<div class="form-group" style="margin-left: 0; margin-right: 0;">
												<button class="btn btn-gray btn-lg btn-block up_btn" type="button"
													name="OtherCredUpload" id="OtherCredUpload">
													<spring:message code="supplier.upload.button" /></button>
											</div>
										</form>
										<div class="three_btn_group">
											<!-- <button type="button" class="btn btn-primary hvr-pop hvr-rectangle-out open4" id="idBtnNext3">Save</button>  -->
										</div>
									</div>
								</div>
								<div class="col-xs-12 col-sm-6">
									<div class="step_table mega">
										<table class="table header table-admin">
											<thead>
												<tr>
													<th class="width-33">
														<spring:message code="supplierprofile.filename" />
													</th>
													<th class="width-33">
														<spring:message code="application.description" />
													</th>
													<th class="width-33">
														<spring:message code="application.upload.date" />
													</th>
												</tr>
											</thead>
										</table>
										<table class="data for-pad-data table" id="uploadOtherFiless">
											<tbody>
												<c:forEach items="${otherCredList}" var="sp">
													<tr>
														<td class="width-33">
															<form:form method="GET">
																<c:url var="download"
																	value="/supplierreg/downloadOtherCredential/${sp.id}" />
																<a class="word-break" href="${download}">${sp.fileName}</a>
															</form:form>
														</td>
														<td class="width-33">
															${sp.description}&nbsp;</td>
														<td class="width-33">
															<span class="removeOtherFile" removeOtherId='${sp.id}'
																otherCredFile='${sp.fileName}'>
																<span class="col-sm-10 no-padding">
																	<fmt:formatDate value="${sp.uploadDate}"
																		pattern="dd/MM/yyyy hh:mm a"
																		timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
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
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<%-- <script type="text/javascript" src="<c:url value="/resources/assets/widgets/file-input/file-input.js"/>"></script> --%>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/file.js"/>"></script>



<script>
	$.validate({ lang : 'en',
	modules : ''});
</script>
<style>
    .align-right {
        text-align: right;
    }

    .width-33 {
        width: 33%;
    }

    .no-padding {
        padding: 0;
	}
	
	.width-60
	{
		width: 60%;
	}

	.width-40
	{
		width: 40%;
	}

	.word-break
{
	word-break: break-all;
}

</style>
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
<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
<spring:message code="eventsummary.checkbox.editor" var="editorLabel" />
<spring:message code="eventsummary.checkbox.viewer" var="viewerLabel" />
<spring:message code="eventsummary.checkbox.associate.owner" var="associateOwnerLabel" />
<sec:authorize access="(hasRole('ROLE_GRN_EDIT') or hasRole('ADMIN')) and hasRole('BUYER')" var="canGrnEdit" />
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/cq_form.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/timepicker/timepicker.css"/>">


<script type="text/javascript">
	$(window).load(function() {
		setTimeout(function() {
			$('#loading').fadeOut(400, "linear");
		}, 300);
	});

	function canGrnEdit() {
		return "${canGrnEdit}";
	}
</script>

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

/* .dropdown-menu input {
	display: inline !important;
	width: auto !important;
} */

.advancee_menu ul {
	top: 100% !important;
	left: 0 !important;
}

/* .dropdown-menu .checkbox {
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
} */

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

.white-space-pre {
	white-space: pre;
}

.modal-body {
	padding: 15px 30px !important;
}

.extraEachBlock {
	position: relative;
	margin-bottom: 10px;
}

.extraEachBlock .help-block {
	position: absolute;
	top: 55px;
}

.modal-body input {
	margin: 0 0 0 0;
}

.custom-icon {
	font-size: 22px;
	padding-right: 10px;
	color: #92A0B3;
}


.ph_btn_custom {
	height: 40px !important;
	min-width: 170px !important;
	font-size: 16px !important;
	line-height: 39px;
	font-weight: 500;
}

.chosen-select {
	display: block !important;
	position: absolute;
	opacity: 0;
}

.chosen-select.error+.chosen-container {
	border: 1px solid #b94a48;
}

.match-h3 {
	color: #2b2b2b;
	font-size: 20px;
	font-style: normal;
	font-variant: normal;
	font-weight: normal;
	letter-spacing: 1px;
	font-family: 'open_sanssemibold';
}
.removeApproverDialogBlockLoaded .approverInfoBlock {
	color: #7f7f7f !important;
}

.tooltip-arrow {
	display: none !important;
}
</style>
<div class="clear"></div>
<%--<div class="white-bg border-all-side float-left width-100 pad_all_15">--%>
<%--	<div class="row">--%>
<%--		<div class="col-md-6 col-sm-12 col-xs-12">--%>
<%--			<input type="hidden" id="poId" value="${po.id}">--%>
<%--			<input type="hidden" id="prId" value="${pr.id}">--%>
<%--			<div class="tag-line">--%>
<%--				<h2>--%>
<%--					<spring:message code="supplier.po.summary.poNumber" />--%>
<%--					: ${po.poNumber}--%>
<%--				</h2>--%>
<%--				<br>--%>
<%--				<h2>--%>
<%--					<spring:message code="supplier.po.summary.poDate" />--%>
<%--					: <fmt:formatDate value="${po.createdDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />--%>

<%--				</h2>--%>
<%--				<br>--%>
<%--				<h2>--%>
<%--					<spring:message code="label.buyer.poList.reviseDate" />--%>
<%--					: <c:if test="${not empty po.poRevisedDate}"><fmt:formatDate value="${po.poRevisedDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" /></c:if> <c:if test="${empty po.poRevisedDate}">-</c:if>--%>
<%--				</h2>--%>

<%--				<br>--%>
<%--				<c:if test="${not empty po.supplier}">--%>
<%--					<h2>--%>
<%--						<spring:message code="application.supplier" />--%>
<%--						: ${po.supplier.supplier.companyName}--%>
<%--					</h2>--%>
<%--				</c:if>--%>
<%--				<c:if test="${empty po.supplier}">--%>
<%--					<h2>--%>
<%--						<spring:message code="application.supplier" />--%>
<%--						: ${po.supplierName}--%>
<%--					</h2>--%>
<%--				</c:if>--%>
<%--			</div>--%>
<%--		</div>--%>

<%--        <c:if test="${ po.status eq 'ORDERED' or po.status eq 'SUSPENDED' or po.status eq 'REVISE' or po.status eq 'PENDING' or (isLoggedInUserApprover)}">--%>
<%--            <div class="col-lg-6 col-md-12 col-sm-12 col-xs-12">--%>
<%--                <form:form action="${pageContext.request.contextPath}/buyer/downloadPoReport/${po.id}" method="GET">--%>
<%--                    <button class="btn btn-sm float-right btn-info hvr-pop marg-left-10" id="idSumDownload" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="tooltip.posummary.download.summary" />'>--%>
<%--                        <span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i>--%>
<%--                        </span> <span class="button-content">PO Summary</span>--%>
<%--                    </button>--%>
<%--                    <br>--%>
<%--                    <br>--%>
<%--                    <br>--%>
<%--                    <br>--%>
<%--                    <br>--%>
<%--                    <br>--%>

<%--                    <c:if test="${po.status eq 'PENDING'}">--%>
<%--                        <c:choose>--%>
<%--                            <c:when test="${po.revised}">--%>
<%--								<span class="float-right match-h3">Sub-Status: REVISE</span>--%>
<%--                            </c:when>--%>
<%--                            <c:when test="${po.cancelled}">--%>
<%--                                <span class="float-right match-h3">Sub-Status: CANCEL</span>--%>
<%--                            </c:when>--%>
<%--                        </c:choose>--%>
<%--                    </c:if>--%>
<%--                </form:form>--%>
<%--            </div>--%>
<%--        </c:if>--%>


<%--		<c:if test="${(po.status ne 'REVISE' and po.status ne 'SUSPENDED' and po.status ne 'ORDERED' and po.status ne 'PENDING' and (loggedInUserId eq po.createdBy.id))}">--%>
<%--            <div class="col-lg-6 col-md-12 col-sm-12 col-xs-12 btn_action">--%>
<%--                <div class="btn-group pull-right marg-right-10">--%>
<%--                    <button type="button" id="idActionBtn" class="btn ph_btn_small float-right btn-primary hvr-pop marg-left-10 dropdown-toggle" data-toggle="dropdown">--%>
<%--                        <span class="glyph-icon icon-separator"> <i class="glyph-icon icon-angle-down"></i></span>--%>
<%--                        <span class="button-content">Action</span>--%>
<%--                    </button>--%>
<%--                    <ul class="dropdown-menu" role="menu">--%>
<%--                        <li><a href="${pageContext.request.contextPath}/buyer/downloadPoReport/${po.id}" class="downloadPoBtn ${po.fromIntegration and (po.status eq 'READY' or po.status eq 'ACCEPTED' or po.status eq 'DECLINED') ? 'disabled' : ''}"><spring:message code="application.download" /></a></li>--%>
<%--                        <c:if test="${(po.status eq 'READY') and (isAdmin or loggedInUserId eq po.createdBy.id ) and !buyerReadOnlyAdmin}">--%>
<%--                             <li class="divider"></li>--%>
<%--                             <li><a href="#" id="idSendPo"><spring:message code="posummary.send.po.button" /></a></li>--%>
<%--                        </c:if>--%>
<%--                        <c:if test="${(po.status eq 'READY' or po.status eq 'ORDERED' or po.status eq 'ACCEPTED' or po.status eq 'DECLINED')}">--%>
<%--                             <li class="divider"></li>--%>
<%--                             <li><a href="#" id="idRevisePo" class="${po.fromIntegration and (po.status eq 'READY' or po.status eq 'ACCEPTED' or po.status eq 'DECLINED') ? 'disabled' : ''}">Suspend PO</a></li>--%>
<%--                        </c:if>--%>
<%--                        <!----%>
<%--                        <c:if test="${po.status eq 'ACCEPTED' and canGrnEdit}">--%>
<%--                              <li><a href="#" id="createGrnId"><spring:message code="create.grn" /></a></li>--%>
<%--                            <li class="divider"></li>--%>
<%--                        </c:if>--%>
<%--                        -->--%>
<%--                        <c:if test="${(po.status ne 'DECLINED') and (po.status ne 'CANCELLED') and (po.status ne 'READY') and (po.status ne 'ACCEPTED') and po.doCount == 0 and po.grnReceivedOrDraftCount == 0}">--%>
<%--                            <li><a href="#" id="idCancelPo"><spring:message code="poSummary.cancelPO.label" /></a></li>--%>
<%--                        </c:if>--%>
<%--                    </ul>--%>
<%--                </div>--%>
<%--			</div>--%>
<%--		</c:if>--%>
<%--	</div>--%>
<%--</div>--%>
<div class="clear"></div>

<div class="tab-pane" style="display: block">
<form:form id="poForm" action="${savePo}" method="post" modelAttribute="po">
	<div class="upload_download_wrapper clearfix event_info">
		<h4>
			<spring:message code="prsummary.general.information" />
		</h4>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="prSummary.pr.number" />
				</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<c:if test="${not empty po.pr}">
					<p><a href="${pageContext.request.contextPath}/buyer/prView/${po.pr.id}">${po.pr.prId}</a></p>
				</c:if>
				<c:if test="${empty po.pr}">
					<%-- <p>NA</p> --%>
				</c:if>
			</div>
		</div>
		<div class="form-tander1 requisition-summary-box">
            <div class="col-sm-4 col-md-3 col-xs-6">
                <label> <spring:message code="po.reference.number" />
                </label>
            </div>
            <div class="col-sm-5 col-md-5 col-xs-6">
                <p>${po.referenceNumber}</p>
            </div>
        </div>

		<form:hidden path="id" id="id" />

		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="poSummary.creator" />
				</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>
					${po.createdBy.name} <br> ${po.createdBy.communicationEmail} <br>
					<c:if test="${not empty po.buyer.companyContactNumber}">
						<spring:message code="prdraft.tel" />: ${po.buyer.companyContactNumber}</c:if>
					<c:if test="${not empty po.buyer.faxNumber}">
						<spring:message code="prtemplate.fax" />: ${po.buyer.faxNumber}</c:if>
					<c:if test="${not empty po.createdBy.phoneNumber}">
						<spring:message code="prtemplate.hp" />: ${ po.createdBy.phoneNumber}</c:if>
				</p>
			</div>
		</div>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="pr.requester" />
				</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>${po.requester}</p>
			</div>
		</div>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="pr.description" />
				</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>${po.description}</p>
			</div>
		</div>
	</div>
	<div class="upload_download_wrapper clearfix marg-top-10 event_info">
		<h4>
			<spring:message code="rfs.summary.finance.information" />
		</h4>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="pr.base.currency" /> :
				</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>${po.currency.currencyCode}</p>
			</div>
		</div>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="eventdescription.decimal.label" /> :
				</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>${po.decimal}</p>
			</div>
		</div>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="eventdescription.costcenter.label" /> :
				</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>${po.costCenter.costCenter}-${po.costCenter.description}</p>
			</div>
		</div>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="label.businessUnit" /> :
				</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>
					<c:if test="${empty po.businessUnit}">
						<spring:message code="application.not.applicable" />
					</c:if>${po.businessUnit.unitName}</p>
			</div>
		</div>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="eventdescription.paymentterm.label" /> :
				</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>${po.paymentTerm}</p>
			</div>
		</div>
		<c:if test="${not empty po.paymentTermDays}">
			<div class="form-tander1 requisition-summary-box">
				<div class="col-sm-4 col-md-3 col-xs-6">
					<label> <spring:message code="eventdescription.paymentday.label" /> :</label>
				</div>
				<div class="col-sm-5 col-md-5 col-xs-6">
					<p>${po.paymentTermDays}</p>
				</div>
			</div>
		</c:if>
	</div>
	<div class="upload_download_wrapper clearfix marg-top-10 event_info">
		<h4>
			<spring:message code="pr.correspondence.address" />
		</h4>
		<div class="form-tander1 requisition-summary-box marg-bottom-20">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label class="set-line-height"><spring:message code="prsummary.address.title" /></label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p class="set-line-height">
					<c:if test="${not empty po.correspondAddressTitle and empty po.line1}">
						${po.correspondAddressTitle} <br> ${po.correspondAddressLine1},
						<c:if test="${not empty po.correspondAddressLine2}">${po.correspondAddressLine2} ,</c:if>${ po.correspondAddressZip},
						<br>${ po.correspondAddressState},${ po.correspondAddressCountry}
					</c:if>
					<c:if test="${not empty po.line1 }">
						<span class='desc'>${po.line1}
							<c:if test="${not empty po.line2}">
								<br />${po.line2}
							</c:if>
							<c:if test="${not empty po.line3}">
								<br />${po.line3}
							</c:if>
							<c:if test="${not empty po.line4}">
								<br />${po.line4}
							</c:if>
							<c:if test="${not empty po.line5}">
								<br />${po.line5}
							</c:if>
							<c:if test="${not empty po.line6}">
								<br />${po.line6}
							</c:if>
							<c:if test="${not empty po.line7}">
								<br />${po.line7}
							</c:if>
						</span>
					</c:if>
				</p>
			</div>
		</div>
	</div>

	<div class="upload_download_wrapper clearfix marg-top-10 event_info">
			<h4>
				<spring:message code="posummary.po.doucment" />
			</h4>
 		<div id="collapseThree" class="form-tander1 requisition-summary-box marg-bottom-20">
			<c:if test="${po.status eq 'REVISE' && eventPermissions.owner}">
				<c:set var="fileType" value="" />
				<c:forEach var="type" items="${ownerSettings.fileTypes}" varStatus="index">
						<c:set var="fileType" value="${fileType}${index.first ? '': ', '}${type}" />
				</c:forEach>
				<input type="hidden" id="poId" value="${poId}" name="poId"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
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
									<input name="poUploadDocument" id="poUploadDocument" data-validation-allowing="${fileType}" data-validation-error-msg-container="#Load_File-error-dialog" data-validation-max-size="${ownerSettings.fileSizeLimit}M" type="file" data-validation="extension size"
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
						<div class="col-md-4 pad_all_15">
							<spring:message code="event.doc.file.descrequired" var="descrequired" />
							<spring:message code="event.doc.file.maxlimit" var="maxlimit" />
							<spring:message code="event.doc.file.descrequired" var="filedesc" />
							<input class="form-control" name="docDescription" id="docDescription" data-validation="length" data-validation-length="max250" type="text" placeholder="${filedesc} ${maxlimit}">
						</div>
						<div class="col-md-2 pad_all_15">
							<button class="upload_btn btn btn-info  ph_btn_midium hvr-pop hvr-rectangle-out" type="button" name="uploadPrDoc" id="uploadPrDoc">
								<spring:message code="application.upload" />
							</button>
						</div>
						<div class="col-md-2 pad_all_15">
							<label style="margin-top: 1%;"> <spring:message code="eventDocument.internal.document" /></label> &nbsp;&nbsp;
							 <input id="idInternal"  class="internal " type="checkbox" checked="checked"/>
						</div>
					</div>
				</div>
			</c:if>
			<div class="pad_all_15 ">
 				<table  cellpadding="0" cellspacing="0" border="0" class="data display table table-bordered noarrow mega" id="poDocList">
					<thead>
						<tr>
						    <c:if test="${(po.status eq 'REVISE' or po.status eq 'SUSPENDED') && eventPermissions.owner}">
							<th class="width_100 width_100_fix align-left">
								<spring:message code="application.action" />
							</th>
							</c:if>
							<th class="width_200 width_200_fix align-left"><spring:message code="application.name" /></th>
							<th class="width_200 width_200_fix align-left wo-rp"><spring:message code="application.description" /></th>
							<th class="width_100 width_100_fix align-left wo-rp"><spring:message code="eventsummary.listdocuments.datetime" /></th>
							<th class="width_100 width_100_fix align-left wo-rp"><spring:message code="eventsummary.listdocuments.size" /></th>
							<!--
							<th class="width_100 width_100_fix align-left wo-rp"><spring:message code="event.document.internal"/></th>
						    -->
						</tr>
					</thead>
					<tbody>
						<c:forEach var="listDoc" items="${listDocs}">
							<tr>
							    <c:url var="downloadPoDocument" value="/buyer/po/downloadPoDocument/${listDoc.id}" />
							    <c:if test="${(po.status eq 'REVISE' or po.status eq 'SUSPENDED') && eventPermissions.owner}">
								<td class="width_100 width_100_fix align-left wo-rp">
									<form method="GET">
										<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
										<a id="downloadButton" href="${downloadPoDocument}" data-placement="top" title='<spring:message code="tooltip.download" />' > <img src="${pageContext.request.contextPath}/resources/images/download.png"></a>
										&nbsp;

										<span>
											<a href="" data-placement="top" title='<spring:message code="tooltip.delete" />' class="removeDocFile" removeDocId=" ${listDoc.id}"> <img src="${pageContext.request.contextPath}/resources/images/delete.png"></a>
										</span>
										&nbsp;
										<span>
										    <a href="" data-placement="top" title='<spring:message code="tooltip.edit" />' ><span class="editDocFile ${buyerReadOnlyAdmin ? 'disabled' : ''}" id="eventDocDesc" editDocInternal="${listDoc.internal}" editDocId=" ${listDoc.id}" editDocDec="${listDoc.description}">
												<img src="${pageContext.request.contextPath}/resources/images/edit1.png">
											</a>
                                        </span>
									</form>
 								</td>
 								</c:if>
								<td class="width_200 width_200_fix align-left">
								    <a id="downloadButton" href="${downloadPoDocument}" data-placement="top" title='<spring:message code="tooltip.download" />' >
								        ${listDoc.fileName}
								    </a>
                                </td>
								<td class="width_200 width_200_fix align-left wo-rp">${listDoc.description}</td>
								<td class="width_100 width_100_fix align-left wo-rp"><fmt:formatDate value="${listDoc.uploadDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" /></td>
								<td class="width_100 width_100_fix align-left wo-rp">${listDoc.fileSizeInKb}KB</td>
								<!--
								<td class="width_100 width_100_fix align-left wo-rp">
									<c:if test="${listDoc.internal == true}">
									<spring:message code="eventDocument.document.internal" />
									</c:if>
									<c:if test="${listDoc.internal == false}">
									<spring:message code="event.document.external" />
									</c:if>
								</td>
								-->
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</div>
    <!-- do not change supplier to po ,it should carry from pr-->
	<div class="upload_download_wrapper clearfix marg-top-10 event_info">
		<h4>
			<spring:message code="application.supplier.detail" />
		</h4>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label><spring:message code="prsummary.supplier.info" /></label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<c:if test="${not empty pr.supplier}">
					<div class="">
					    <!--
						<h5>${pr.supplier.supplier.companyName}</h5>
						-->
						<span class='desc'>${pr.supplier.supplier.companyName}<br />${pr.supplier.fullName}<br />${pr.supplier.communicationEmail}<br />${pr.supplier.companyContactNumber}</span>
					</div>
				</c:if>
				<c:if test="${empty pr.supplier}">
					<div class="">
						<span class='desc'>${pr.supplierName}<br />${pr.supplierTelNumber}<br />${pr.supplierFaxNumber}<br />${pr.supplierTaxNumber}</span>
					</div>
				</c:if>
			</div>
		</div>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label><spring:message code="prevent.supplier.address" /></label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
			    <!--do not change to pr object ,coz supplier carry out from PR-->
				<c:if test="${not empty pr.supplier}">
					<div class="">
						<span class='desc'>${pr.supplier.supplier.line1}<br />${pr.supplier.supplier.line2}<br />${pr.supplier.supplier.city}</span>
					</div>
				</c:if>
				<c:if test="${empty pr.supplier}">
					<div class="">
						<span class='desc'>${pr.supplierAddress}</span>
					</div>
				</c:if>
			</div>
		</div>
	</div>
	<div class="upload_download_wrapper clearfix marg-top-10 event_info">
		<h4>
			<spring:message code="pr.delivery.detail" />
		</h4>
		<c:if test="${po.status ne 'REVISE'}">
			<div class="form-tander1 requisition-summary-box marg-bottom-20">
				<div class="col-sm-4 col-md-3 col-xs-6">
					<label class="set-line-height"><spring:message code="eventsummary.eventdetail.deliveryreceiver" /></label>
				</div>
				<div class="col-sm-5 col-md-5 col-xs-6">
					<p class="set-line-height">${po.deliveryReceiver}</p>
				</div>
			</div>
			<div class="form-tander1 requisition-summary-box marg-bottom-20">
				<div class="col-sm-4 col-md-3 col-xs-6">
					<label class="set-line-height"><spring:message code="eventsummary.eventdetail.deliveryadds" /></label>
				</div>
				<div class="col-sm-5 col-md-5 col-xs-6">
					<p class="set-line-height">
						<c:if test="${not empty po.deliveryAddress}">
							<h5>${po.deliveryAddress.title}</h5>
							<span class='desc'>${po.deliveryAddress.line1}, ${po.deliveryAddress.line2}, ${po.deliveryAddress.city}, ${po.deliveryAddress.zip}, ${po.deliveryAddress.state.stateName}, ${po.deliveryAddress.state.country.countryName}</span>
						</c:if>
					</p>
				</div>
			</div>
			<div class="form-tander1 requisition-summary-box ">
				<div class="col-sm-4 col-md-3 col-xs-6">
					<label><spring:message code="eventsummary.eventdetail.deliverydate" /> &amp; <spring:message code="application.time" /></label>
				</div>
				<div class="col-sm-5 col-md-5 col-xs-6">
					<p>
						<fmt:formatDate value="${po.deliveryDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
					</p>
				</div>
			</div>
		</c:if>
		<c:if test="${po.status eq 'REVISE'}">
			<div class="form-tander1 requisition-summary-box marg-bottom-20">
				<div class="col-sm-4 col-md-3 col-xs-6">
					<label class="set-line-height"><spring:message code="prsummary.receiver" /></label>
				</div>
				<div class="col-sm-5 col-md-5 col-xs-6">
					<form:input path="deliveryReceiver" type="text" data-validation="required" class="form-control" />
				</div>
			</div>
			<div class="form-tander1 requisition-summary-box marg-bottom-20">
				<div class="col-sm-4 col-md-3 col-xs-6">
					<label class="set-line-height"><spring:message code="eventsummary.eventdetail.deliveryadds" /></label>
				</div>
				<div class="col-sm-5 col-md-5 col-xs-5">
					<div class="input-prepend input-group">
						<label class="physicalCriterion pull-left"> </label>
						<span class="pull-left buyerAddressRadios <c:if test="${not empty po.deliveryAddress}">active enabledBlock</c:if>">
							<span class="phisicalArressBlock pull-left marg-top-10">
								<c:if test="${not empty po.deliveryAddress}">
									<div class="">
										<h5>${po.deliveryAddress.title}</h5>
										<span class='desc'>${po.deliveryAddress.line1},	${po.deliveryAddress.line2}, ${po.deliveryAddress.city}, ${po.deliveryAddress.zip}, ${po.deliveryAddress.state.stateName}, ${po.deliveryAddress.state.country.countryName}</span>
									</div>
								</c:if>
								</span>
								<div class=" align-right">
									<a id="deleteDelAddress" class="pull-right" style="font-size: 18px; line-height: 1; padding: 0px; color: rgb(127, 127, 127); margin-top: 8px;"> <i class="fa fa-times-circle"></i></a>
								</div>
						</span>
					</div>
					<div id="sub-credit" class="invite-supplier delivery-address margin-top-10" style="${not empty po.deliveryAddress ? 'display:none;' : ''}">
						<div class="role-upper ">
							<div class="col-sm-12 col-md-12 col-xs-12 float-left">
								<input type="text" placeholder='<spring:message code="event.search.address.placeholder" />' class="form-control delivery_add">
							</div>
						</div>
						<div class="chk_scroll_box" id="delivaddress">
							<div class="scroll_box_inner">
								<div class="role-main">
									<div class="role-bottom small-radio-btn ${buyerReadOnlyAdmin ? 'disabled' : ''}">
										<ul class="role-bottom-ul">
											<c:forEach var="address" items="${addressList}" varStatus="status">
												<li>
													<div class="radio-info">
														<label>
															<form:radiobutton path="deliveryAddress" name="test" id="test${status.index+1}" value="${address.id}" class="custom-radio" data-validation-error-msg-container="#address-buyer-dialog" data-validation="buyer_address" />
														</label>
													</div>
													<div class="del-add">
														<h5>${address.title}</h5>
														<span class='desc'>${address.line1}, ${address.line2}, ${address.city}, ${address.zip}, ${address.state.stateName}, ${address.country}</span>
													</div>
												</li>
											</c:forEach>
										</ul>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div id="address-buyer-dialog"></div>
				</div>
			</div>
			<div class="form-tander1 requisition-summary-box ">
				<div class="col-sm-4 col-md-3 col-xs-6">
					<label><spring:message code="eventsummary.eventdetail.deliverydate" /> &amp; <spring:message code="application.time" /></label>
				</div>
				<div id="deliveryTime">
					<div class="col-sm-3 col-md-3 col-xs-3">
						<div class="input-prepend input-group  ${buyerReadOnlyAdmin ? 'disabled' : ''}">
							<spring:message code="dateformat.placeholder" var="dateformat" />
							<spring:message code="tooltip.delivery.date" var="deliveryadds" />
							<form:input path="deliveryDate" readonly="readonly" data-placement="top" data-toggle="tooltip" data-original-title="${deliveryadds}" class="nvclick form-control for-clander-view" data-validation="required date" data-fv-date-min="15/10/2016" data-validation-format="dd/mm/yyyy" placeholder="${dateformat}" autocomplete="off" />
						</div>
					</div>
					<div class="col-md-2 col-sm-3 col-xs-3 col-lg-2">
						<div class="bootstrap-timepicker dropdown">
							<form:input path="deliveryTime" data-validation="required" class="bootstrap-timepicker timepicker-example for-timepicker-view form-control" autocomplete="off" />
						</div>
					</div>
				</div>
			</div>
		</c:if>
	</div>
	<div class="upload_download_wrapper clearfix marg-top-10 event_info">
		<h4>
			<spring:message code="prevent.purchase.item" />
		</h4>
		<c:if test="${po.status ne 'REVISE'}">
			<div class="col-md-2 pull-right" style="margin-right:2%;">
				<div class="marg-top-10 marg-bottom-10">
					<button class="btn btn-default hvr-pop hvr-rectangle-out3" id="downloadTemplate">
						<i class="excel_icon"></i>
						<spring:message code="prsummary.export.excel.button" />
					</button>
				</div>
			</div>
		</c:if>
		<c:if test="${po.status eq 'REVISE'}">
			<div class="col-md-2 pull-right">
				<div class="marg-top-10 marg-bottom-10">
					<button type="button" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out pull-left" id="deletePoItems">
						<spring:message code="application.delete" />
					</button>
				</div>
			</div>
		</c:if>
		<div class="pad_all_15 float-left width-100">
			<div class="main_table_wrapper ph_table_border mega">
				<table class="ph_table border-none header parentBlocks" border="0" cellspacing="0" cellpadding="0" width="100%">
					<thead>
						<tr>
							<c:if test="${po.status eq 'REVISE' or po.status eq 'SUSPENDED'}">
								<th class="width_50 width_75_fix"><spring:message code="application.action" /></th>
							</c:if>
							<th class="width_50 width_50_fix"><spring:message code="supplier.no.col" /></th>
							<th class="align-left width_200_fix"><spring:message code="label.rftbq.th.itemname" /></th>
							<th class="align-left width_100_fix"><spring:message code="label.rftbq.th.uom" /></th>

							<th class="align-right width_100 width_150_fix">PO <spring:message code="label.rftbq.th.quantity.only" /></th>
                            <c:if test="${po.status ne 'DRAFT'}">
                                <th class="align-right width_100 width_150_fix"><spring:message code="label.rftbq.th.locked.quantity" /></th>
                                <th class="align-right width_100 width_150_fix"><spring:message code="label.rftbq.th.balance.quantity" /></th>
                            </c:if>
							<th class="align-right width_150 width_150_fix"><spring:message code="label.rftbq.th.unitprice" /> (${po.currency})</th>
							<th class="align-right width_150 width_150_fix"><spring:message code="label.rftbq.th.priceperunit" /> (${po.currency})</th>

							<c:if test="${not empty po.field1Label}">
								<th class="align-left width_100 width_100_fix">${po.field1Label}</th>
							</c:if>
							<c:if test="${not empty po.field2Label}">
								<th class="align-left width_100 width_100_fix">${po.field2Label}</th>
							</c:if>
							<c:if test="${not empty po.field3Label}">
								<th class="align-left width_100 width_100_fix">${po.field3Label}</th>
							</c:if>
							<c:if test="${not empty po.field4Label}">
								<th class="align-left width_100 width_100_fix">${po.field4Label}</th>
							</c:if>
							<c:if test="${not empty po.field5Label}">
								<th class="align-left width_100 width_100_fix">${po.field5Label}</th>
							</c:if>
							<c:if test="${not empty po.field6Label}">
								<th class="align-left width_100 width_100_fix">${po.field6Label}</th>
							</c:if>
							<c:if test="${not empty po.field7Label}">
								<th class="align-left width_100 width_100_fix">${po.field7Label}</th>
							</c:if>
							<c:if test="${not empty po.field8Label}">
								<th class="align-left width_100 width_100_fix">${po.field8Label}</th>
							</c:if>
							<c:if test="${not empty po.field9Label}">
								<th class="align-left width_100 width_100_fix">${po.field9Label}</th>
							</c:if>
							<c:if test="${not empty po.field10Label}">
								<th class="align-left width_100 width_100_fix">${po.field10Label}</th>
							</c:if>

							<th class="width_150 width_150_fix align-right"><spring:message code="prtemplate.total.amount" />  (${po.currency})</th>
							<th class="width_150 align-right width_150_fix"><spring:message code="prtemplate.tax.amount" />  (${po.currency})</th>
							<th class="width_250 width_250_fix align-right"><spring:message code="prtemplate.total.amount.tax" />  (${po.currency})</th>
						</tr>
					</thead>
				</table>
				<table class="data ph_table border-none childBlocks" border="0" cellspacing="0" cellpadding="0" width="100%">
					<tbody>
						<c:forEach items="${poItemlist}" var="item">
							<tr>
								<c:if test="${po.status eq 'REVISE' or po.status eq 'SUSPENDED'}">
									<td class="width_50 width_75_fix ${item.itemIndicator == '003' ? 'grey-background' : ''}">
									 	<c:if test="${item.order ne 0}">
											<a href="#crateNewItem" data-item-id="${item.id}" data-po-id="${po.id}" data-toggle="modal" title="" class="Edit_btn_table">
												<img src="${pageContext.request.contextPath}/resources/images/edit1.png">
											</a>
											<input type="checkbox" class="custom-checkbox custom-checkbox1" value="${item.id}" id="itemCId" name="itemCId">
 									 	</c:if>
									</td>
								</c:if>
								<td class="width_50 width_50_fix ${item.itemIndicator == '003' ? 'grey-background' : ''}">${item.level}.${item.order}</td>
								<td class="align-left width_200_fix ${item.itemIndicator == '003' ? 'grey-background' : ''}">${item.itemName}<c:if test="${not empty item.itemDescription}">
										<span class="item_detail s1_view_desc"><spring:message code="application.view.description" /></span>
									</c:if>
									<p class="s1_tent_tb_description s1_text_small">${item.itemDescription}</p>
								</td>
								<td class="align-left width_100_fix ${item.itemIndicator == '003' ? 'grey-background' : ''}">${item.product.uom.uom != null ? item.product.uom.uom : item.unit.uom}</td>
                                <td class="align-right width_100 width_150_fix ${item.itemIndicator == '003' ? 'grey-background' : ''}"><fmt:formatNumber type="number" minFractionDigits="${po.decimal}" maxFractionDigits="${po.decimal}" value="${item.quantity}" /></td>

                                <c:if test="${po.status ne 'DRAFT'}">
                                    <td class="align-right width_100 width_150_fix ${item.itemIndicator == '003' ? 'grey-background' : ''}">
                                      <c:if test="${item.unit.uom != null}">
                                        <fmt:formatNumber type="number" minFractionDigits="${po.decimal}" maxFractionDigits="${po.decimal}" value="${item.lockedQuantity}" />
                                      </c:if>
                                    </td>
                                    <td class="align-right width_100 width_150_fix ${item.itemIndicator == '003' ? 'grey-background' : ''}">
                                      <c:if test="${item.unit.uom != null}">
                                        <fmt:formatNumber type="number" minFractionDigits="${po.decimal}" maxFractionDigits="${po.decimal}" value="${item.balanceQuantity}" />
                                      </c:if>
                                    </td>
                                </c:if>

                                <td class=" align-right width_150 width_150_fix ${item.itemIndicator == '003' ? 'grey-background' : ''}"><fmt:formatNumber type="number" minFractionDigits="${po.decimal}" maxFractionDigits="${po.decimal}" value="${item.unitPrice}" /></td>
                                <td class="align-right width_100 width_150_fix ${item.itemIndicator == '003' ? 'grey-background' : ''}">
                                  <c:if test="${item.unit.uom != null}">
                                    <fmt:formatNumber type="number" minFractionDigits="${po.decimal}" maxFractionDigits="${po.decimal}" value="${item.pricePerUnit}" />
                                  </c:if>
                                </td>

								<c:if test="${not empty po.field1Label}">
									<td class="align-left width_100 width_100_fix ${item.itemIndicator == '003' ? 'grey-background' : ''}">${item.field1}&nbsp;</td>
								</c:if>
								<c:if test="${not empty po.field2Label}">
									<td class="align-left width_100 width_100_fix ${item.itemIndicator == '003' ? 'grey-background' : ''}">${item.field2}&nbsp;</td>
								</c:if>
								<c:if test="${not empty po.field3Label}">
									<td class="align-left width_100 width_100_fix ${item.itemIndicator == '003' ? 'grey-background' : ''}">${item.field3}&nbsp;</td>
								</c:if>
								<c:if test="${not empty po.field4Label}">
									<td class="align-left width_100 width_100_fix ${item.itemIndicator == '003' ? 'grey-background' : ''}">${item.field4}&nbsp;</td>
								</c:if>
								<c:if test="${not empty po.field5Label}">
									<td class=" align-left width_100 width_100_fix ${item.itemIndicator == '003' ? 'grey-background' : ''}">${item.field5}&nbsp;</td>
								</c:if>
								<c:if test="${not empty po.field6Label}">
									<td class="align-left width_100 width_100_fix ${item.itemIndicator == '003' ? 'grey-background' : ''}">${item.field6}&nbsp;</td>
								</c:if>
								<c:if test="${not empty po.field7Label}">
									<td class="align-left width_100 width_100_fix ${item.itemIndicator == '003' ? 'grey-background' : ''}">${item.field7}&nbsp;</td>
								</c:if>
								<c:if test="${not empty po.field8Label}">
									<td class="align-left width_100 width_100_fix ${item.itemIndicator == '003' ? 'grey-background' : ''}">${item.field8}&nbsp;</td>
								</c:if>
								<c:if test="${not empty po.field9Label}">
									<td class=" align-left width_100 width_100_fix ${item.itemIndicator == '003' ? 'grey-background' : ''}">${item.field9}&nbsp;</td>
								</c:if>
								<c:if test="${not empty po.field10Label}">
									<td class="align-left width_100 width_100_fix ${item.itemIndicator == '003' ? 'grey-background' : ''}">${item.field10}&nbsp;</td>
								</c:if>
                                <c:set var="pricePerUnit" value="${empty item.pricePerUnit ?item.unitPrice:item.pricePerUnit}" />
								<td class="width_150 width_150_fix align-right ${item.itemIndicator == '003' ? 'grey-background' : ''}"><c:if test="${item.order != '0' }">
										<fmt:formatNumber type="number" minFractionDigits="${po.decimal}" maxFractionDigits="${po.decimal}" value="${(item.unitPrice/pricePerUnit)*item.quantity}" />
									</c:if></td>
								<td class="width_150 align-right width_150_fix ${item.itemIndicator == '003' ? 'grey-background' : ''}"><c:if test="${item.order != '0' }">
										<fmt:formatNumber type="number" minFractionDigits="${po.decimal}" maxFractionDigits="${po.decimal}" value="${item.taxAmount}" />
									</c:if></td>
								<td class="width_250 width_250_fix align-right ${item.itemIndicator == '003' ? 'grey-background' : ''}"><c:if test="${item.order != '0' }">
										<fmt:formatNumber type="number" minFractionDigits="${po.decimal}" maxFractionDigits="${po.decimal}" value="${item.totalAmountWithTax}" />
									</c:if></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>

		<div class="total_all total-with-tax-final table-resposive" style="font-size: 16px;">
			<table cellspacing="3" cellpadding="3" style="width: 98.8%; border-collapse: separate; border-spacing: 8px; margin-right: 20px;">
				<tr>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
					<td class="align-left" style="white-space: nowrap;"><strong><spring:message code="prsummary.total2" /> (${po.currency}) :</strong></td>
					<td style="white-space: nowrap;"><fmt:formatNumber type="number" minFractionDigits="${po.decimal}" maxFractionDigits="${po.decimal}" value="${po.total}" /></td>
				</tr>
				<tr>
					<td style="white-space: nowrap; vertical-align: top; padding-top: 0%;"><strong><spring:message code="prtemplate.additional.charges" />:</strong></td>
					<td style="width: 100%; padding-left: 10px; padding-right: 10px;">
						<c:if test="${po.status eq 'REVISE'}">
							<form:input path="taxDescription" id="taxDescription" type="text" class="form-control" style="width: 20%; padding-left: 10px; padding-right: 10px;"/>
						</c:if>
						<c:if test="${po.status ne 'REVISE'}">${po.taxDescription}</c:if>
					</td>
					<td style="white-space: nowrap; vertical-align: top;" class="align-left"><strong>(${po.currency}):</strong></td>
					<td style="white-space: nowrap; vertical-align: top;" class="align-right">
						<c:choose>
							<c:when test="${po.decimal==1}">
								<c:set var="decimalSet" value="0,0.0"></c:set>
							</c:when>
							<c:when test="${po.decimal==2}">
								<c:set var="decimalSet" value="0,0.00"></c:set>
							</c:when>
							<c:when test="${po.decimal==3}">
								<c:set var="decimalSet" value="0,0.000"></c:set>
							</c:when>
							<c:when test="${po.decimal==4}">
								<c:set var="decimalSet" value="0,0.0000"></c:set>
							</c:when>
						</c:choose>
						<c:if test="${po.status eq 'REVISE' or po.status eq 'SUSPENDED'}">
							<fmt:formatNumber var="addtax" type="number" minFractionDigits="${po.decimal}" maxFractionDigits="${po.decimal}" value="${po.additionalTax}" />
							<form:input type="text" path="additionalTax" data-validation="custom" data-validation-regexp="^\d{0,16}(\.\d{1,${po.decimal}})?$" data-validation-ignore="," data-validation-error-msg="Only numbers and ',' allowed, length should be less then 16 and after decimal ${po.decimal} digits allowed" id="additionalTax" name="additionalTax" placeholder='<spring:message code="prtemplate.charges.amount.placeholder"/>'
								value="${po.additionalTax  > 0 ? addtax : '' }" style="width: 100%; text-align: left;" data-sanitize="numberFormat" data-sanitize-number-format="${decimalSet}" />
						</c:if>
						<c:if test="${po.status ne 'REVISE' or po.status eq 'SUSPENDED'}">
						<fmt:formatNumber type="number" minFractionDigits="${po.decimal}" maxFractionDigits="${po.decimal}" value="${po.additionalTax}" />
						</c:if>
					</td>
				</tr>
				<tr>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
					<td style="white-space: nowrap;" class="align-left"><strong><spring:message code="submission.report.grandtotal" /> (${po.currency}):</strong></td>
					<td style="white-space: nowrap;" class="align-right" id="gTotal" > <fmt:formatNumber type="number" minFractionDigits="${po.decimal}" maxFractionDigits="${po.decimal}" value="${po.grandTotal}" /></td>
				</tr>
			</table>
		</div>

	</div>
	<div class="upload_download_wrapper clearfix marg-top-10 event_info remark-tab">
		<h4>
			<spring:message code="Product.remarks" />
		</h4>
		<div class="pad_all_15 float-left width-100">
			<label><spring:message code="prtemplate.general.remark" /></label>
			<p>${po.remarks}</p>
		</div>
		<div class="pad_all_15 float-left width-100">
			<label><spring:message code="prtemplate.terms.condition" /></label>
			<p>${po.termsAndConditions}</p>
		</div>
	</div>

    <div class="panel sum-accord marg-top-5">
        <div class="panel-heading">
            <h4 class="panel-title pad0">
                <a data-toggle="collapse" data-parent="#accordion" class="accordion" href="#collapseTeam"> PO Team Members
                </a>
                <c:if test="${eventPermissions.owner}">
                    <button class="editTeamMemberList sixbtn" title='<spring:message code="tooltip.edit" />'>
                        <img src="${pageContext.request.contextPath}/resources/images/edit-btn.png" />
                    </button>
                </c:if>
            </h4>
        </div>
        <div id="collapseTeam" class="panel-collapse collapse accortab in">
            <div class="panel-body">
                <div class="clearfix"></div>
                <div class="col-md-12 col-sm-12 col-xs-12">
                    <div class="main_table_wrapper">
                        <table id="poTeamMembersList" class="display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0">
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

    <c:if test="${!empty prTemplate and prTemplate.approvalPoVisible}">
        <div class="upload_download_wrapper clearfix marg-top-10 event_info Approval-tab">
            <h4>
                <spring:message code="po.approval.route" />
                <c:if test="${!prTemplate.approvalPoReadOnly}">
                    <c:choose>
                        <c:when test="${po.status eq 'DRAFT' or po.status eq 'PENDING'}">
                            <c:if test="${eventPermissions.owner}">
                                <a class="marg-left-20 editApprovalPopupButton" title="<spring:message code='tooltip.edit'/>">
                                    <img src="${pageContext.request.contextPath}/resources/images/edit-btn.png" alt="edit" />
                                </a>
                            </c:if>
                        </c:when>
                    </c:choose>
                </c:if>
            </h4>
            <c:if test="${po.enableApprovalReminder}">
            <div class="pad_all_15 float-left appr-div position-relative">
                <div>
                  <div class="row">
                    <label style="margin-left: 17px;">Reminder Settings: </label>
                  </div>
                  <div class="marg-left-10">
                     <p>Reminder emails sent every ${po.reminderAfterHour} hours.</p>
                     <p>Maximum ${po.reminderCount} reminder emails.</p>
                     <c:if test="${po.notifyEventOwner}">
                     <p><spring:message code="pr.notification.owner.message" /></p>
                     </c:if>
                  </div>
                </div>
            </div>
            </c:if>
            <c:if test="${!empty po.approvals}">
            <c:forEach items="${po.approvals}" var="approval">
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
            </c:if>
            <div class="clear"></div>
            <div class="remark-tab pad0">
                <h4>
                    <spring:message code="summarydetails.approval.comments" />
                </h4>
                <div class="pad_all_15 float-left width-100">
                    <c:forEach items="${comments}" var="comment">
                        <div class="Comments_inner ${comment.approved ? 'comment-right' : 'comment-wrong'}">
                            <h3>${comment.createdBy.name}
                                <span> <fmt:formatDate value="${comment.createdDate}" pattern="E dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
                                </span>
                            </h3>
                            <div class="${comment.approved ? 'approve-div' : 'reject-div'}">${comment.comment}</div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>
    </c:if>

	</form:form>
    <div class="clear"></div>

	<c:if test="${po.status eq 'PENDING' and eventPermissions.activeApproval}">
        <div class="upload_download_wrapper clearfix marg-top-10 event_info Approval-tab">
            <div class="form_field pad_all_15 float-left width-100">
                <form id="approvedRejectForm" method="post" action="">
                    <div class="row">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                        <input type="hidden" name="id" id="id" value="${po.id}">
                        <input type="hidden" name="prId" id="prId" value="${pr.id}">
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
        </div>
    </c:if>

	<jsp:include page="poAudit.jsp" />

	<c:if test="${eventPermissions.owner}">
        <div>
            <div class="btn-next">
                <div class="row">
                    <div class="col-md-12 col-xs-12 col-ms-12">
                        <c:if test="${po.status eq 'DRAFT' or po.status eq 'SUSPENDED'}">
                            <c:url var="poPurchaseItem" value="/buyer/poRemark/${po.id}?prId=${pr.id}" />
                            <a id="previousButton" href="${poPurchaseItem}" class="btn btn-black marg-top-20 ph_btn hvr-pop hvr-rectangle-out1"><spring:message code="application.previous" /></a>
                        </c:if>
                        <c:if test="${po.status eq 'DRAFT'}">
                            <c:url var="poFinish" value="/buyer/poFinish/${po.id}?prId=${pr.id}" />
                            <a href="${poFinish}" id="poFinish" class="btn btn-info ph_btn hvr-pop marg-top-20 right-header-button hvr-rectangle-out  ${ buyerReadOnlyAdmin ? 'disabled' : '' }"><spring:message code="application.finish" /></a>
                        </c:if>
                        <c:if test="${po.status eq 'SUSPENDED'}">
                            <c:url var="poRevise" value="/buyer/revisePo/${po.id}?prId=${pr.id}" />
                            <a href="${poRevise}" id="poRevise" class="btn btn-info ph_btn hvr-pop marg-top-20 right-header-button hvr-rectangle-out  ${ buyerReadOnlyAdmin ? 'disabled' : '' }"><spring:message code="application.finish" /></a>
                        </c:if>
                        <c:if test="${po.status eq 'DRAFT' || po.status eq 'SUSPENDED'}">
                            <!-- ph-4113 DO NOT ALLOWED CANCEL PO IF PO HAS 1 or More DO-->
                            <c:if test="${fn:length(dos) == 0}">
                                <c:if test="${po.status eq 'DRAFT' || po.status eq 'SUSPENDED'}">
                                    <a href="#confirmCancelPo" role="button" class="btn btn-danger marg-top-20 marg-right-10 ph_btn hvr-pop right-header-button" data-toggle="modal"><spring:message code="po.cancel.label" /></a>
                                </c:if>
                            </c:if>
                        </c:if>

                        <c:if test="${po.status eq 'REVISE'}">
                            <spring:message code="application.save" var="draft" />
                            <input type="button" id="savePoDetail" class="top-marginAdminList step_btn_1 btn btn-black hvr-pop hvr-rectangle-out1 ph_btn hvr-pop submitStep1 pull-right skipvalidation" value="${draft}" />
                            <button id="submitPo" class="btn btn-info ph_btn step_btn_1 marg-top-20 pull-right hvr-pop hvr-rectangle-out submitStep1 marg-right-10">
                                <spring:message code="application.submit.po" />
                            </button>
                        </c:if>
                    </div>
                </div>
            </div>
        </div>
    </c:if>
</div>

<!-- Create Grn popup  -->
<div class="modal fade" id="modal-createGrn" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="poSummary.confirm.createGrn" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<form id="createGrnForm" method="post">
				<input type="hidden" name="poId" value="${po.id}">
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<div class="col-md-12">
					<div class="row">
						<div class="modal-body col-md-12">
							<label> <spring:message code="posummary.sure.want.createGrn" />
							</label>
						</div>
					</div>
				</div>
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
					<input type="submit" id="grnCreate" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left" value="Yes">
					<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right no-button" data-dismiss="modal">
						<spring:message code="application.no2" />
					</button>
				</div>
			</form>
		</div>
	</div>
</div>
<!-- send po popup  -->
<div class="modal fade" id="confirmSendPo" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="poSummary.confirm.sendPO" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<%-- <form id="" action="${pageContext.request.contextPath}/buyer/sendPo" method="post"> --%>
			<form id="sendPoForm" method="post">
				<input type="hidden" name="poId" value="${po.id}">
				<input type="hidden" name="prId" value="${pr.id}">

				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<div class="col-md-12">
					<div class="row">
						<div class="modal-body col-md-12">
							<label> <spring:message code="posummary.sure.want.sendPo" />
							</label>
						</div>
					</div>
				</div>
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
					<input type="button" id="sendPo" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left" value="Yes">
					<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
						<spring:message code="application.no2" />
					</button>
				</div>
			</form>
		</div>
	</div>
</div>

<!-- Edit Approval Dialog -->
<div class="flagvisibility dialogBox" id="editApprovalPopup" title="Edit Approval">
	<div class="float-left width100 pad_all_15 white-bg">
		<c:url value="/buyer/updatePoApproval" var="updatePoApproval" />
		<form:form id="prSummaryForm" action="${updatePoApproval}/${pr.id}" method="post" modelAttribute="po">
			<form:hidden path="id" />
			<jsp:include page="poApproval.jsp"></jsp:include>
			<div class="row">
				<div class="col-md-12">
					<div class="form-group align-center">
						<button type="reset" id="resetEventContctForm" style="display: none;"></button>
						<button type="submit" class="btn btn-info ph_btn btn-tooltip hvr-pop hvr-rectangle-out">
							<spring:message code="application.update" />
						</button>
						<button type="button" class="closeDialog1 btn btn-black marg-left-10 hvr-pop ph_btn hvr-rectangle-out1">
							<spring:message code="application.cancel" />
						</button>
					</div>
				</div>
			</div>
		</form:form>
	</div>
</div>

<div class="flagvisibility dialogBox teamMemberListPopup" id="teamMemberListPopup" title="PO Team Members">
	<div class="float-left width100 pad_all_15 white-bg">
		<div class="form_field">
			<div class="form-group ">
				<div class="pull-left width100">
					<div class="col-md-12 marginBottomA">
						<div class="ia-invite-controls-area">
							<div class="group">
								<div class="input-group mrg15T mrg15B">
									<select id="TeamMemberList" class="user-list-all chosen-select" name="approverList1" selected-id="approver-id" cssClass="form-control user-list-all chosen-select">
										<option value=""><spring:message code="prsummary.select.team.member" /></option>
										<c:forEach items="${userList}" var="users2">
                                            <c:if test="${users2.id == '-1' }">
                                                template += '<option value="-1" disabled >${users2.user.name}</option>';
                                            </c:if>
                                            <c:if test="${users2.id != '-1' }">
                                                template += '<option value="${users2.id}" >${users2.user.name}</option>';
                                            </c:if>
                                        </c:forEach>
									</select>
									<div class="input-group-btn">
										<button class="btn btn-primary addTeamMemberToPoList" type="button">+</button>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class="col-md-12 col-sm-12 col-xs-12 margeinAllMDZero">
						<div class="mem-tab">
							<table cellpading="0" cellspacing="0" width="100%" id="appUsersList" class="table-hover table">
								<c:if test="${!empty poTeamMembers }">
                                    <c:forEach items="${poTeamMembers}" var="teamMembers">
                                        <tr data-username="${teamMembers.user.name}" approver-id="${teamMembers.user.id}">
                                            <td class="width_50_fix ">
                                                <!--	<img src='<c:url value="/resources/images/profile_setting_image.png"/>' alt="profile " /> -->
                                            </td>
                                            <td>${teamMembers.user.name}<br> <span>${teamMembers.user.loginId}</span>
                                            </td>

                                            <td>
                                                <div class="cqa_del">
                                                    <a href="javascript:void(0);" list-type="Team Member" class="removeApproversList"><spring:message code="tooltip.delete" /></a>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
								</c:if>
							</table>
						</div>
					</div>
					<div class="col-md-12 d-flex margeinAllMDZero">
						<a class="center-btn closeDialog btn btn-black pull-right marg-left-10 marg_top_20 hvr-pop ph_btn_small hvr-rectangle-out1" href="#" > <spring:message code="eventReport.cls" />
						</a>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<!--Approver  popup-->
<div class="flagvisibility dialogBox" id="removeApproverListPopup" title='<spring:message code="tooltip.remove.team.member" />'>
	<div class="float-left width800 pad_all_15 white-bg radius-bottom-all-10">
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
			<input id="id" type="hidden" value="${pr.id}">
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

<jsp:include page="poModal.jsp"></jsp:include>

<script type="text/javascript" src="<c:url value="/resources/assets/widgets/file-input/file-input.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/progressbar/progressbar.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/progressbar/progressbar.css"/>">

<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/moment.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker-demo.js?1"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/datepicker/datepicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/timepicker/timepicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/numeral.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/view/purchaseOrderDocument.js"/>"></script>


<c:set var="isApprovalReadOnly" value="${prTemplate.approvalPoReadOnly}" />
<c:set var="isApprovalVisible" value="${prTemplate.approvalPoVisible}" />
<c:set var="prTemplateId" value="${prTemplate.id}" />


<script type="text/javascript">
	$('document').ready(function() {
	    var poId = $('#poId').val();
        var prId = $('#prId').val();
        //ph-4113
        var isApprovalReadOnly='${isApprovalReadOnly}';
        var isApprovalVisible='${isApprovalVisible}';
        var prTemplateId='${prTemplateId}';

        if(isApprovalReadOnly=='true'){
            $('.Approval-tab').addClass('disabled')
        }

		$.validate({
			lang : 'en',
			modules : 'file'
		});

		let params = new URLSearchParams(location.search)
		params.delete('success')
		history.replaceState(null, '', '?' + params + location.hash)
		var poDecimal = ${po.decimal};

        var eventTeamMembersTable = new Object();

		const getPoTeamMembers=()=>{
            if (typeof prId !== 'undefined' && prId !== '' && prId !== null) {
                 eventTeamMembersTable = $('#poTeamMembersList').DataTable({
                    "processing": true,
                    "serverSide": true,
                    "paging": false,
                    "info": false,
                    "ordering": false,
                    "searching": false,
                    "ajax": {
                        "url": getContextPath() + '/buyer/poTeamMembersList/${po.id}',
                    },
                    "columns": [{
                        "data": "user.name",
                        "className": "align-left",
                        "defaultContent": "Demo Test"
                    }, {
                        "data": "user.loginId",
                        "className": "align-left",
                        "defaultContent": "Demo 2"
                    }, {
                        "data": "teamMemberType",
                        "className": "align-left",
                        "defaultContent": "",
						"render": function (data, type, row) {
							if (data === "Associate_Owner") {
								return "Associate Owner";
							}
							return data || "";
						}
                    }]
                });
            } else {
                // Initialize DataTable without ajax call
                var eventTemMembersTable = $('#poTeamMembersList').DataTable({
                    "paging": false,
                    "info": false,
                    "ordering": false,
                    "searching": false,
                    "columns": [{
                        "data": "user.name",
                        "className": "align-left",
                        "defaultContent": "Demo Test"
                    }, {
                        "data": "user.loginId",
                        "className": "align-left",
                        "defaultContent": "Demo 2"
                    }, {
                        "data": "teamMemberType",
                        "className": "align-left",
                        "defaultContent": ""
                    }]
                });
            }
        }

        getPoTeamMembers();

		function ReplaceNumberWithCommas(yourNumber) {
			var n;
			// Seperates the components of the number
			if (yourNumber != null) {
				n = yourNumber.toString().split(".");
				// Comma-fies the first part
				n[0] = n[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
				// Combines the two sections
				return n.join(".");
			}
			return n;
		}

		$('.childBlocks').css('margin-top', $('.parentBlocks').height());
		// First check if pr.id exists and is not empty


		$('#downloadTemplate').click(function(e) {
			e.preventDefault();
			var poId = $.trim($('#poId').val());
			window.location.href = getContextPath() + "/buyer/exportPoItemTemplate/" + poId;
		});
		$('#idSendPo').click(function(e) {
			$('#confirmSendPo').modal('show');
		});

		$('#idCancelPo').click(function(e) {
			$('#confirmCancelPo').modal('show');
		});

		$.formUtils.addValidator({ name : 'buyer_address', validatorFunction : function(value, $el, config, language, $form) {
			var response = true;
			var fieldName = $el.attr('name');
			console.log($('[name="' + fieldName + '"]:checked').length);
			if ($('[name="' + fieldName + '"]:checked').length == 0) {
				response = false;
			}
			return response;
		}, errorMessage : 'This is a required field', errorMessageKey : 'badBuyerAddress' });

        $('.closeDialog').off().on('click',function(){
            $("#teamMemberListPopup").dialog('close');
            eventTeamMembersTable.ajax.reload();
            return false;
        });

        $(document).delegate('.editTeamMemberList', 'click', function(e) {
            $("#teamMemberListPopup").dialog({
                modal : true,
                minWidth : 300,
                width : '50%',
                maxWidth : 600,
                minHeight : 200,
                open : function(event, ui) {
                    $(".ui-dialog-titlebar-close").addClass("hide");

					// Add styles to the overlay dynamically
					$(".ui-widget-overlay").css({
						"background-color": "#FFFFFF",
						"opacity": "0"
					});
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

			$(".removeApproverDialogBlockLoaded").css({
				"position" : "absolute",
				"top" : $(this).offset().top - 300
			});

            updateUserList('', $("#TeamMemberList"), 'ALL_USER');
            return false;
        });

        $('.closeDialog-btn').click(function() {
            $('#removeApproverListPopup').dialog('close');
            getPoTeamMembers();
        });

		$('#sendPo').on('click', function(e) {
			e.preventDefault();
			if($("#sendPoForm").isValid()) {
				$(this).addClass('disabled');
	 			$('#idSendPo').addClass('disabled');
				$('#idCancelPo').addClass('disabled');
	 			$('#sendPoForm').attr('action', getContextPath() + '/buyer/sendPo');
				$("#sendPoForm").submit();
			}else{
				return;
			}
		});


		$('#createGrnId').click(function(e) {
			e.preventDefault();
			$('#modal-createGrn').modal();
		});
		$('#grnCreate').on('click', function(e) {
			e.preventDefault();
			if($("#createGrnForm").isValid()) {
				$(this).addClass('disabled');
				$('#idCancelPo').addClass('disabled');
	 			$('#idSendPo').addClass('disabled');
	 			$('#createGrnForm').attr('action', getContextPath() + '/buyer/createGrn');
				$("#createGrnForm").submit();
			}else{
				return;
			}
		});

		$('#idRevisePo').click(function(e) {
			$('#confirmRevisePo').modal('show');
		});

		$('#suspendPo').on('click', function(e) {
			e.preventDefault();
			if($("#suspendPoForm").isValid()) {
				$(this).addClass('disabled');
	 			$('#idRevisePo').addClass('disabled');
				$('#idCancelPo').addClass('disabled');
	 			$('#suspendPoForm').attr('action', getContextPath() + '/buyer/suspendPo'); //PH-4113 change from revisePo
				$("#suspendPoForm").submit();
				$('#loading').show();
			}else{
				return;
			}
		});

		$(document).delegate('#deleteDelAddress', 'click', function() {
			 $(".buyerAddressRadios").removeClass("active");
			 $('#sub-credit input[type="radio"]').prop('checked', false);
			 $.uniform.update();
			 $("#sub-credit").slideDown();
		});

		var addressId="${po.deliveryAddress.id}";
		if(addressId){
			<c:forEach var="address" items="${addressList}" varStatus="status">
			  if(addressId === "${address.id}"){
			  $("#test"+"${status.index+1}").prop("checked", true);
			  }
		    </c:forEach>
		}

		$(document).delegate('.delivery_add', 'keyup', function() {
			var $rows = $('.role-bottom-ul li');
			var val = $.trim($(this).val()).replace(/ +/g, ' ').toLowerCase();
			$rows.show().filter(function() {
				var text = $(this).text().replace(/\s+/g, ' ').toLowerCase();
				return !~text.indexOf(val);
			}).hide();
		});

		$(document).delegate('.role-bottom-ul li [type="radio"]', 'click', function() {
			var dataAddress = $(this).closest('li').children('.del-add').html();
			$('.phisicalArressBlock').html(dataAddress);
			$('.physicalCriterion input[type="checkbox"]').prop('checked', true);
			$('.buyerAddressRadios').addClass('active enabledBlock');
			$.uniform.update();
			$("#sub-credit").slideUp();
		});

		$(document).delegate('.phisicalArressBlock', 'click', function(event) {
			$("#sub-credit").slideToggle();
		});

		// on Edit fill value
		$(document).on("click", ".Edit_btn_table", function() {
			var header = $("meta[name='_csrf_header']").attr("content");
			var token = $("meta[name='_csrf']").attr("content");
			var poItemId = $(this).attr('data-item-id');
			var poId = $(this).attr('data-po-id');
			$('#parentId').val($(this).parents('.sub_item').attr('data-parent'));
			var data = {};
			$('#itemTax').val('');
			$("#creat_subitem_form").find('[name="itemDescription"]').val('');
			data["poId"] = poId;
			data["poItemId"] = poItemId;

			$.ajax({
				url : getContextPath() + '/buyer/editPoItem',
				data : {
					poId : poId,
					poItemId : poItemId
				},
				type : "GET",
				beforeSend : function(xhr) {
					xhr.setRequestHeader(header, token);
					xhr.setRequestHeader("Accept", "application/json");
					xhr.setRequestHeader("Content-Type", "application/json");
					$('#loading').show();
				},
				success : function(data, textStatus, request) {
					var html = '';

					console.log(data);
					// $("select#itemUnit").val(data.uom.id).trigger("chosen:updated");
					$.each(data, function(i, item) {
						if (i == 'unitPrice' || i == 'quantity') {
							if(poDecimal != null && poDecimal !==undefined && item !== undefined){
								$("#creat_subitem_form").find('[name="' + i + '"]').val(ReplaceNumberWithCommas(item.toFixed(poDecimal)));
							}else{
								$("#creat_subitem_form").find('[name="' + i + '"]').val(ReplaceNumberWithCommas(item));
							}
							if (i == 'unitPrice' && data.product && data.product.contractItem == true) {
								$('#itemUnitPriceDiv').addClass('disabled');
							}
						} else {
							if(i=='itemTax'){
								var itemTax = parseFloat(item);
								if(poDecimal != null && poDecimal !==undefined && itemTax !== undefined){
									$("#creat_subitem_form").find('[name="' + i + '"]').val(ReplaceNumberWithCommas(itemTax.toFixed(poDecimal)));
								}else{
									$("#creat_subitem_form").find('[name="' + i + '"]').val(ReplaceNumberWithCommas(itemTax));
								}
							}else{
								$("#creat_subitem_form").find('[name="' + i + '"]').val(item);
							}
						}
						$("#itemId").val(data.id);

						$('#creat_subitem_form').find('h3.s1_box_title').text('Edit Item ');
						$('#itemSave').text('Update');
						$('#itemTitle').text('Edit Item');

					});

					try {
						$("#uomText").val(data.unit.id).trigger("chosen:updated");
					} catch (e) {
						$("#uomText").val(data.product.uom.id).trigger("chosen:updated");
					}
					$("#productCategory").val(data.productCategory.id).trigger("chosen:updated");

// 					$("#textInputPickerDiv").css("display", "block");
					$("#itemName").val(data.itemName);
					$("#itemNameText").val(data.itemName);

					$('#crateNewItem').modal('show');
				},
				complete : function() {
					$('#loading').hide();
				}
			});

		});

		// saving po item
		$(function() {
			var header = $("meta[name='_csrf_header']").attr("content");
			var token = $("meta[name='_csrf']").attr("content");
			$('#itemSave').click(function(e) {

				e.preventDefault();
				if (!$('#addNewItems').isValid()) {
					return false;
				}
				var poItemId = $("#itemId").val();
// 				var poId = $('#id').val();
				var freeTextItemEntered = false;

				var poId = $('#poId').val();
				var prId = $('#prId').val();
				console.log(">>>>>>>>>> poId "+poId);

				$("#allthesets div:last input:first").attr("name")
				var itemName = '';

				var itemId = $('#itemName').val();

				itemName = $("#itemNameText").val();

				// console.log("=======================" +
				var itemQuantity = $('#itemQuantity').val();
				var itemUnitPrice = $('#itemUnitPrice').val();
				var itemDescription = $('#itemDescription').val();
				var itemUnit = $('#itemUnit').val();
				var itemTax = $('#itemTax').val();
				var parentId = $('#parentId').val();

				var data = {};

				var productItem = {};

				var po = {};
				po["id"] = poId;
				data["po"] = po;
				var productCategory = {};
				productCategory["id"] = $('#productCategory').val();
				var unit = {};
				unit["id"] = $('#uomText').val();
				data["unit"] = unit;
				data["itemName"] = itemName;
				data["itemId"] = itemId;
				data["quantity"] = itemQuantity.replace(/,/g, '');
				data["unitPrice"] = itemUnitPrice.replace(/,/g, '');
				data["itemDescription"] = itemDescription;
				data["itemTax"] = itemTax;
				data["supplierId"] = $('#chosenSupplier').val();
				if (parentId != "") {
					var parent = {};
					parent["id"] = parentId;
					data["parent"] = parent;
				}
				data["id"] = poItemId;

				ajaxUrl = getContextPath() + '/buyer/updatePoItem';
				$.ajax({
					url : ajaxUrl,
					data : JSON.stringify(data),
					type : "POST",
					contentType : "application/json",
					dataType : 'json',
					beforeSend : function(xhr) {
						xhr.setRequestHeader(header, token);
						xhr.setRequestHeader("Accept", "application/json");
						xhr.setRequestHeader("Content-Type", "application/json");
						$('#loading').show();
					},
					success : function(data, textStatus, request) {

						var success = request.getResponseHeader('success');
						var info = request.getResponseHeader('info');

						window.location.href = getContextPath() + "/buyer/poView/" + poId+"?prId="+prId;
						$('#crateNewItem').modal('hide');
						$('#loading').hide();
					},
					error : function(request, textStatus, errorThrown) {
						$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
						$('.alert').hide();
						$('.alert-danger').hide();
						$('div[id=idGlobalError]').show();
						$('#loading').hide();

						var error = request.getResponseHeader('error');
						$.jGrowl(error, {
							sticky : false,
							position : 'top-right',
							theme : 'bg-red'
						});
					},
				});
			});
		});


		$.formUtils.addValidator({
			name : 'validate_max_13',
			validatorFunction : function(value, $el, config, language, $form) {
				var val = value.split(".");
				if (val[0].replace(/,/g, '').length > 13) {
					return false;
				} else {
					return true;
				}
			},
			errorMessage : 'The input value is longer than 13 characters',
			errorMessageKey : 'validateLengthCustom'
		});

		$('.deletePoItemButton').click(function(e) {
			console.log($('.deletePoItemButton').attr('data-item-id'));
			console.log(">>>>>>>>>>>>> " +$(this).attr('data-item-id'));
// 			$('#confirmDeletePoItem').modal('show');
			$('#confirmDeletePoItem').attr('data-item-id', $(this).attr('data-item-id'));
			$('#confirmDeletePoItem').attr('data-po-id', $(this).attr('data-po-id'));
		});


		$('#deletePoItem').on('click', function(e) {
			var header = $("meta[name='_csrf_header']").attr("content");
			var token = $("meta[name='_csrf']").attr("content");
			$('#confirmDeletePoItem').modal('hide');
			var poId = $('#confirmDeletePoItem').attr('data-po-id');
			var poItemId = $('#confirmDeletePoItem').attr('data-item-id');
			console.log(">>>>>>> poItemId "+poItemId +" poId.... "+poId);

			$.ajax({
				url : getContextPath() + '/buyer/po/'+poId+'/delete/' + poItemId,
				type : "POST",
				dataType : 'json',
				beforeSend : function(xhr) {
					xhr.setRequestHeader(header, token);
					xhr.setRequestHeader("Accept", "application/json");
					xhr.setRequestHeader("Content-Type", "application/json");
					$('#loading').show();
				},
				success : function(data, textStatus, request) {
					console.log(">>>>>>>>>>>>>>>>>>>>> ");
					var table = '';
					var success = request.getResponseHeader('success');
					var info = request.getResponseHeader('info');
					/* $.jGrowl(success, {
						sticky : false,
						position : 'top-right',
						theme : 'bg-green'
					}); */

					window.location.href = getContextPath() + "/buyer/poView/" + poId+"?success='Po item deleted successfully'";
					$('#confirmDeletePoItem').modal('hide');
					//$('#loading').hide();

				},
				error : function(request, textStatus, errorThrown) {
					console.log("...............................");
					var error = request.getResponseHeader('error');
					$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error') != null ? request.getResponseHeader('error').split(",").join("<br/>") : "");
					$('.alert').hide();
					$('.alert-danger').hide();
					$('div[id=idGlobalError]').show();
					$.jGrowl(error, {
						sticky : false,
						position : 'top-right',
						theme : 'bg-red'
					});
					$('#loading').hide();
				},
				complete : function() {
					//$('#loading').hide();
				}
			});
		});


		$('#submitPo').click(function(e) {
			e.preventDefault();
			var additionalTax = ($('#additionalTax').val());
			var taxDescription = ($('#taxDescription').val());
			$('#taxDescription').parent().removeClass('has-error').find('.form-error').remove();
			$('#additionalTax').parent().removeClass('has-error').find('.form-error').remove();
			var taxValue = parseFloat('0').toFixed(poDecimal);
			var tax = parseFloat('0');
			additionalTax = parseFloat(additionalTax).toFixed(poDecimal);

			console.log(" ............... taxValue : "+taxValue +" ....tax "+tax);
			if (additionalTax != '' && taxDescription == '' && additionalTax != taxValue) {
				console.log(">>>>>>>>>>>>>>>>>>>>additionalTax::  "+additionalTax+" .....taxValue ::  "+taxValue);
				$('#taxDescription').parent().addClass('has-error').append('<span class="help-block form-error">This is a required field</span>');
				return false;
			} else {
				console.log(" ****************** ");
				$('#taxDescription').parent().removeClass('has-error').find('.form-error').remove();
			}

			if (taxDescription != '' && additionalTax == '' ) {
				$('#additionalTax').parent().addClass('has-error').append('<span class="help-block form-error">This is a required field</span>');
				return false;
			} else {
				$('#additionalTax').parent().removeClass('has-error').find('.form-error').remove();
			}

			if($('#poForm').isValid()) {
				$('#poForm').attr('action', getBuyerContextPath('submitPo'));
				$('#poForm').submit();
			}
		});

		$('#savePoDetail').click(function(e) {
			e.preventDefault();
			$('#poForm').attr('action', getBuyerContextPath('savePoDetails'));
			$('#poForm').submit();
		});

        var contextPath = window.location.pathname.substring(0, window.location.pathname.indexOf("/", 2));


		$('#approvedButton').click(function(e) {
			e.preventDefault();
			//$(this).addClass('disabled');
			$('#approvedRejectForm').attr('action', getContextPath() + "/buyer/approvePo");
            console.log($('#approvedRejectForm').attr('action'));
			$('#approvedRejectForm').submit();
		});

		$('#rejectedButton').click(function(e) {
			e.preventDefault();
			if($('#approvedRejectForm').isValid()) {
				console.log("rejectedButton");
				$(this).addClass('disabled');
				$('#approvedRejectForm').attr('action', getContextPath() + "/buyer/rejectPo");
				$('#approvedRejectForm').submit();
			} else {
				console.log("Invalid form...");
			}
		});


		$('#additionalTax').on('change', function(e) {
			console.log(">>>>>>>>>>>>>>>>>>>>>>>>>+poDecimal :: "+poDecimal);
			e.preventDefault();
			if($('#additionalTax').val() == undefined || $('#additionalTax').val() == ''){
				$('#additionalTax').val(parseFloat('0').toFixed(poDecimal));
			}
			var additionalTax = parseFloat($.trim($('#additionalTax').val()).replace(/,/g, "").match(new RegExp("^\\d+\\.?\\d{0,"+poDecimal+"}")));
			$('#additionalTax').val(ReplaceNumberWithCommas(additionalTax.toFixed(poDecimal)));
			addTax();
		});

		function addTax() {
			console.log(".............................");
			$('.alert-danger').hide();
			$('.alert').hide();
			var additionalTax = $('#additionalTax').val();
// 			if ($('#additionalTax').val() != '') {
// 				console.log("---------------additionalTax "+additionalTax);
// 				return false;
// 			}
			console.log("======================================");
			if ($('#additionalTax').val() == '') {
				additionalTax = 0;
			}
			var poId = $('#id').val();
			var taxDescription = $('#taxDescription').val();
			var header = $("meta[name='_csrf_header']").attr("content");
			var token = $("meta[name='_csrf']").attr("content");
			$.ajax({
				type : "GET",
				url : getContextPath() + "/buyer/updatePoAdditionalTax",
				data : {
					additionalTax : additionalTax,
					poId : poId,
					taxDescription : taxDescription
				},
				beforeSend : function(xhr) {
					xhr.setRequestHeader(header, token);
				},
				success : function(data) {
					var grandTotal = data[0];
					var decimal = data[1];
					if(decimal != null && decimal !==undefined && grandTotal !== undefined){
						console.log("success" + parseFloat(ReplaceNumberWithCommas(grandTotal.toFixed(decimal))));
						$("#gTotal").text(ReplaceNumberWithCommas(grandTotal.toFixed(decimal)));
					}else{
						console.log("success" + ReplaceNumberWithCommas(grandTotal));
					}

					$('.alert-danger').hide();
					$('.alert').hide();
				},
				error : function(request, textStatus, errorThrown) {
					$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
					$('.alert-danger').hide();
					$('.alert').hide();
					$('div[id=idGlobalError]').show();
				},
			})
		}

		$('.timepicker-example').timepicker({
			disableFocus : true,
			explicitMode : false
		}).on('hide.timepicker', function(e) {
			e.preventDefault();
			$(this).blur();
		});

		$('#deletePoItems').click(function() {
			$('.error-range.text-danger').remove();
			var val = [];
			$('.custom-checkbox1:checked').each(function(i) {
				val[i] = $(this).val();
			});
			console.log(val + "val");
			if (typeof val === 'undefined' || val == '') {
				console.log("Error");
				$('p[id=idGlobalErrorMessage]').html("Please select atleast one PO Item for delete");
				$('.alert-danger').hide();
				$('.alert').hide();
				$('div[id=idGlobalError]').show();
				$(window).scrollTop(0);
				return false;
			} else {
				$('#modal-prItemDelete').modal();
			}
		});

		$('#deleteAllPrItems').on('click', function(e) {
			console.log('Po items clicked....');
			var url = getContextPath() +  "/buyer/poItemMultipleDelete";
			var val = [];

			$('.custom-checkbox1:checked').each(function(i) {
				val[i] = $(this).val();
			});
			var header = $("meta[name='_csrf_header']").attr("content");
			var token = $("meta[name='_csrf']").attr("content");
			console.log(val + "val");
			var poId = $('#poId').val();
			$.ajax({
				type : "POST",
				url : url,
				data : {
					'poItemIds' : val,
					'poId' : poId
				},
				beforeSend : function(xhr) {
					xhr.setRequestHeader(header, token);
					$('#loading').show();
				},
				success : function(data, textStatus, request) {
				var len = val.length;
					console.log("success len..."+val.length);
	 			location.reload();

				console.log("success")
//	 			var success = request.getResponseHeader('success');

				var msg = data;
				$('p[id=idGlobalSuccessMessage]').html(msg);
				$('div[id=idGlobalSuccess]').show();
				$('div[id=idGlobalError]').hide();
				$('#modal-prItemDelete').modal('hide');

				$('#loading').hide();
			},
			error : function(request, textStatus, errorThrown) {
				$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
				$('div[id=idGlobalError]').show();
				$('#loading').hide();
				$('#modal-prItemDelete').modal('hide');
				$(window).scrollTop(0);
			},
			complete : function() {
				$('#loading').hide();
				$('#modal-prItemDelete').modal('hide');
			}
				});
		});
	});

	<c:if test="${buyerReadOnlyAdmin }">
	$(window).bind('load', function() {
		var allowedFields = '#nextButton,#dashboardLink,#previousButton,#bubble, #downloadButton,#idSumDownload,.s1_view_desc,.bluelink,#approvalremarks, .downloadPoBtn, #downloadTemplate';
		//var disableAnchers = ['#reloadMsg'];
		disableFormFields(allowedFields);
	});
	</c:if>

</script>

<script type="text/javascript" src="<c:url value="/resources/js/view/poCreate.js"/>"></script><!-- for edit team member-->
<script type="text/javascript" src="<c:url value="/resources/js/view/poSummary.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/view/po.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/cq_form.css"/>">
<style>
.dropdown-menu input {
    display: inline !important;
    width: auto !important;
}


.grey-background {
    background-color: #d3d3d3 !important; /* Light grey */
}

</style>


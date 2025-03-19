<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<style>
.ph_btn_custom {
	height: 40px !important;
	min-width: 170px !important;
	font-size: 16px !important;
	line-height: 39px;
	font-weight: 500;
}

.reset-that {
	all: initial; * {
	all: unset;
}
;
word-break








































































































:




















































 




















































break-all








































































































;
}
</style>
<div class="clear"></div>
<div class="white-bg border-all-side float-left width-100 pad_all_15">
	<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
	<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
	<div class="row">
		<div class="col-md-6 col-sm-12 col-xs-12">
			<div class="tag-line">
				<h2>
					<spring:message code="supplier.doListing.deliveryId" />
					: ${deliveryOrder.deliveryId}
				</h2>
				<br>
				<c:if test="${deliveryOrder.status ne 'DRAFT'}">
					<h2>
						<spring:message code="supplier.do.summary.doDate" />
						:
						<fmt:formatDate value="${deliveryOrder.doSendDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
					</h2>
					<br>
				</c:if>
				<h2>
					<spring:message code="application.lable.buyer" />
					: ${deliveryOrder.buyer.companyName}
				</h2>
				<br>
				<h2>
					<spring:message code="supplier.po.summary.businessUnit" />
					:
					<c:if test="${empty deliveryOrder.businessUnit}">N/A</c:if>${deliveryOrder.businessUnit.unitName}
				</h2>
			</div>
		</div>
	</div>

	<c:if test="${deliveryOrder.status eq 'DRAFT'}">
		<div class="btn-group pull-right marg-right-10">
			<button type="button" id="idActionBtn" class="btn ph_btn_small float-right btn-primary hvr-pop marg-left-10 dropdown-toggle" data-toggle="dropdown">
				<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-angle-down"></i></span> <span class="button-content">Action</span>
			</button>
			<ul class="dropdown-menu" role="menu">
				<li><a id="doMPA" href="#"><spring:message code="supplier.ship.mpa" /></a></li>
				<li><a href="https://www.myparcelasia.com/track" target="_blank">Track Item</a></li>
			</ul>
		</div>
	</c:if>

	<c:if test="${deliveryOrder.status ne 'DRAFT'}">
		<div class="pull-right">
			<form:form action="${pageContext.request.contextPath}/supplier/downloadDo/${deliveryOrder.id}" method="GET">
				<button class="btn btn-sm float-right btn-info hvr-pop marg-left-10 downloadPoBtn" type="submit" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="dosummary.download.do.button" />'>
					<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i>
					</span> <span class="button-content"><spring:message code="dosummary.download.do.button" /></span>
				</button>
			</form:form>
		</div>
	</c:if>

	<c:if test="${deliveryOrder.status eq 'DELIVERED'}">
		<div class="pull-right">
			<div class="btn-group pull-right marg-right-10">
				<button type="button" id="idActionBtn" class="btn btn-sm float-right btn-info hvr-pop marg-left-10 dropdown-toggle" data-toggle="dropdown">
					<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-angle-down"></i></span> <span class="button-content">Action</span>
				</button>
				<ul class="dropdown-menu" role="menu">
					<li><a href="https://www.myparcelasia.com/track" target="_blank">Track Item</a></li>
				</ul>
			</div>
		</div>
	</c:if>

</div>
<form:form name="form" id="idDoForm" method="post" action="">
	<div class="clear"></div>
	<div class="tab-pane" style="display: block">
		<input type="hidden" id="doId" name="doId" value="${deliveryOrder.id}"> <input type="hidden" id="poId" name="poId" value="${deliveryOrder.po.id}">
		<div class="upload_download_wrapper clearfix event_info">
			<h4>
				<spring:message code="prsummary.general.information" />
			</h4>

			<div class="form-tander1 requisition-summary-box">
				<div class="col-sm-4 col-md-3 col-xs-6">
					<label> <spring:message code="supplier.doListing.deliveryName" />
					</label>
				</div>
				<spring:message var="placeReference" code="do.place.name" />
				<div class="col-sm-5 col-md-5 col-xs-6">
					<c:if test="${deliveryOrder.status eq 'DRAFT'}">
						<input name="name" value="${deliveryOrder.name}" type="text" placeholder="${placeReference}" data-validation="required length" data-validation-length="max128" class="form-control" />
					</c:if>
					<c:if test="${deliveryOrder.status ne 'DRAFT'}">
						<p>${deliveryOrder.name}</p>
					</c:if>
				</div>
			</div>
			<div class="form-tander1 requisition-summary-box">
				<div class="col-sm-4 col-md-3 col-xs-6">
					<label> <spring:message code="supplier.do.summary.referenceNo" />
					</label>
				</div>
				<spring:message var="placeReference" code="do.place.reference.number" />
				<div class="col-sm-5 col-md-5 col-xs-6">
					<c:if test="${deliveryOrder.status eq 'DRAFT'}">
						<input name="referenceNumber" value="${deliveryOrder.referenceNumber}" type="text" placeholder="${placeReference}" data-validation="alphanumeric length" data-validation-allowing="-_ /" data-validation-optional="true" data-validation-length="1-32" class="form-control" />
					</c:if>
					<c:if test="${deliveryOrder.status ne 'DRAFT'}">
						<p>${deliveryOrder.referenceNumber}</p>
					</c:if>
				</div>
			</div>

			<div class="form-tander1 requisition-summary-box">
				<div class="col-sm-4 col-md-3 col-xs-6">
					<label> <spring:message code="supplier.doListing.poNumber" />
					</label>
				</div>
				<div class="col-sm-5 col-md-5 col-xs-6">
					<p>
						<a href="${pageContext.request.contextPath}/supplier/supplierPrView/${deliveryOrder.po.id}">${deliveryOrder.po.poNumber}</a>
					</p>
				</div>
			</div>

			<div class="form-tander1 requisition-summary-box">
				<div class="col-sm-4 col-md-3 col-xs-6">
					<label> <spring:message code="supplier.do.summary.creator" />
					</label>
				</div>
				<div class="col-sm-5 col-md-5 col-xs-6">
					<p>
						${deliveryOrder.createdBy.name} <br> ${deliveryOrder.createdBy.communicationEmail} <br>
						<c:if test="${not empty deliveryOrder.supplier.companyContactNumber}">
							<spring:message code="prdraft.tel" />: ${deliveryOrder.supplier.companyContactNumber}</c:if>
						<c:if test="${not empty deliveryOrder.supplier.faxNumber}">
							<spring:message code="prtemplate.fax" />: ${deliveryOrder.supplier.faxNumber}</c:if>
						<c:if test="${not empty deliveryOrder.createdBy.phoneNumber}">
							<spring:message code="prtemplate.hp" />: ${ deliveryOrder.createdBy.phoneNumber}</c:if>
					</p>
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
					<p>${deliveryOrder.currency.currencyCode}</p>
				</div>
			</div>
			<div class="form-tander1 requisition-summary-box">
				<div class="col-sm-4 col-md-3 col-xs-6">
					<label> <spring:message code="eventdescription.decimal.label" /> :
					</label>
				</div>
				<div class="col-sm-5 col-md-5 col-xs-6">
					<p>${deliveryOrder.decimal}</p>
				</div>
			</div>

		</div>
		<div class="upload_download_wrapper clearfix marg-top-10 event_info">
			<h4>
				<spring:message code="supplierbilling.billing.detail" />
			</h4>

			<div class="form-tander1 requisition-summary-box marg-bottom-20">
				<div class="col-sm-4 col-md-3 col-xs-6">
					<label class="set-line-height"><spring:message code="supplier.billing.address" /></label>
				</div>
				<div class="col-sm-5 col-md-5 col-xs-6">
					<p class="set-line-height">
						<span class='desc'> ${deliveryOrder.line1} <br> <c:if test="${not empty deliveryOrder.line2}">${deliveryOrder.line2}  <br>
							</c:if> <c:if test="${not empty deliveryOrder.line3}">${deliveryOrder.line3}  <br>
							</c:if> <c:if test="${not empty deliveryOrder.line4}">${deliveryOrder.line4}  <br>
							</c:if> <c:if test="${not empty deliveryOrder.line5}">${deliveryOrder.line5}  <br>
							</c:if> <c:if test="${not empty deliveryOrder.line6}">${deliveryOrder.line6}  <br>
							</c:if> <c:if test="${not empty deliveryOrder.line7}">${deliveryOrder.line7}  <br>
							</c:if>
						</span>
					</p>
				</div>
			</div>
		</div>

		<div class="upload_download_wrapper clearfix marg-top-10 event_info">
			<h4>
				<spring:message code="pr.delivery.detail" />
			</h4>
			<div class="form-tander1 requisition-summary-box marg-bottom-20">
				<div class="col-sm-4 col-md-3 col-xs-6">
					<label class="set-line-height"><spring:message code="supplier.do.summary.receiver" /></label>
				</div>
				<div class="col-sm-5 col-md-5 col-xs-6">
					<c:if test="${deliveryOrder.status eq  'DRAFT' }">
						<input name="deliveryReceiver" type="text" value="${deliveryOrder.deliveryReceiver}" data-validation="required length" data-validation-length="1-150" class="form-control" />
					</c:if>
					<c:if test="${deliveryOrder.status ne  'DRAFT' }">
						<p class="set-line-height">${deliveryOrder.deliveryReceiver}</p>
					</c:if>
				</div>
			</div>
			<c:if test="${deliveryOrder.status eq 'DRAFT' }">
				<div class="form-tander1">
					<div class="form_field">
						<div class="form-group">
							<div class="form-group">
								<label for="idAdressOne" class="col-sm-3 control-label"><spring:message code="delivery.adds.line1" /> :</label>
								<div class="col-sm-6 col-md-5">
									<input name="deliveryAddressLine1" type="text" value="${deliveryOrder.deliveryAddressLine1}" data-validation="required length" data-validation-length="1-250" class="form-control" placeholder="Address Line 1" />
								</div>
							</div>
							<div class="form-group">
								<label for="idAdressTwo" class="col-sm-3 control-label"><spring:message code="delivery.adds.line2" /> :</label>
								<div class="col-sm-6 col-md-5">
									<input name="deliveryAddressLine2" type="text" value="${deliveryOrder.deliveryAddressLine2}" data-validation="required length" data-validation-length="1-250" class="form-control" placeholder="Address Line 2" />
								</div>
							</div>
							<div class="form-group">
								<label for="idAdressCity" class="col-sm-3 control-label"><spring:message code="delivery.city" /> :</label>
								<div class="col-sm-6 col-md-5">
									<input name="deliveryAddressCity" type="text" value="${deliveryOrder.deliveryAddressCity}" data-validation="required length" data-validation-length="1-250" class="form-control" placeholder="City" />
								</div>
							</div>
							<div class="form-group">
								<label for="idAdressState" class="col-sm-3 control-label"><spring:message code="delivery.state" /> :</label>
								<div class="col-sm-6 col-md-5">
									<input name="deliveryAddressState" type="text" value="${deliveryOrder.deliveryAddressState}" data-validation="required length" data-validation-length="1-150" class="form-control" placeholder="State" />
								</div>
							</div>
							<div class="form-group">
								<label for="idAdressPostCode" class="col-sm-3 control-label"><spring:message code="delivery.postcode" /> :</label>
								<div class="col-sm-6 col-md-5">
									<input name="deliveryAddressZip" type="text" value="${deliveryOrder.deliveryAddressZip}" data-validation="required length" data-validation-length="1-32" class="form-control" placeholder="Zip Code" />
								</div>
							</div>
							<div class="form-group">
								<label for="idAdressCountry" class="col-sm-3 control-label"><spring:message code="delivery.country" /> :</label>
								<div class="col-sm-6 col-md-5">
									<input name="deliveryAddressCountry" type="text" value="${deliveryOrder.deliveryAddressCountry}" data-validation="required length" data-validation-length="1-128" class="form-control" placeholder="Country" />
								</div>
							</div>
						</div>
					</div>
				</div>
			</c:if>
			<c:if test="${deliveryOrder.status ne  'DRAFT' }">
				<div class="form-tander1 requisition-summary-box marg-bottom-20">
					<div class="col-sm-4 col-md-3 col-xs-6">
						<label class="set-line-height"><spring:message code="supplier.do.summary.deliveryadds" /></label>
					</div>
					<div class="col-sm-5 col-md-5 col-xs-6">
						<p class="set-line-height">
							<span class='desc'> ${deliveryOrder.deliveryAddressLine1} <br> <c:if test="${not empty deliveryOrder.deliveryAddressLine2}"> ${deliveryOrder.deliveryAddressLine2} <br>
								</c:if> <c:if test="${not empty deliveryOrder.deliveryAddressCity}"> ${deliveryOrder.deliveryAddressCity} <br>
								</c:if> <c:if test="${not empty deliveryOrder.deliveryAddressZip}"> ${deliveryOrder.deliveryAddressZip} <br>
								</c:if> ${deliveryOrder.deliveryAddressState}, ${deliveryOrder.deliveryAddressCountry}
							</span>
						</p>
					</div>
				</div>
			</c:if>

			<div class="form-tander1 requisition-summary-box ">
				<div class="col-sm-4 col-md-3 col-xs-6">
					<label><spring:message code="eventsummary.eventdetail.deliverydate" /> &amp; <spring:message code="application.time" /></label>
				</div>
				<c:if test="${deliveryOrder.status eq  'DRAFT' }">
					<div id="deliveryTime">
						<div class="col-sm-3 col-md-3 col-xs-3">
							<div class="input-prepend input-group">
								<spring:message code="dateformat.placeholder" var="dateformat" />
								<spring:message code="tooltip.delivery.date" var="deliveryadds" />
								<fmt:formatDate var="changeDate" value="${deliveryOrder.deliveryDate}" pattern="dd/MM/yyyy" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
								<input name="deliveryDate" value="${changeDate}" id="deliveryDate" data-placement="top" data-toggle="tooltip" data-original-title="${deliveryadds}" class="nvclick form-control for-clander-view" data-validation="required date" data-fv-date-min="15/10/2016" data-validation-format="dd/mm/yyyy" placeholder="${dateformat}" autocomplete="off" />
							</div>
						</div>
						<div class="col-md-2 col-sm-3 col-xs-3 col-lg-2">
							<div class="bootstrap-timepicker dropdown">
								<fmt:formatDate var="changeTime" value="${deliveryOrder.deliveryTime}" pattern="hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
								<input name="deliveryTime" value="${changeTime}" data-validation="required" class="bootstrap-timepicker timepicker-example for-timepicker-view form-control" autocomplete="off" />
							</div>
						</div>
					</div>
				</c:if>
				<c:if test="${deliveryOrder.status ne  'DRAFT' }">
					<div class="col-sm-5 col-md-5 col-xs-6">
						<p>
							<fmt:formatDate value="${deliveryOrder.deliveryDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
						</p>
					</div>
				</c:if>
				<div class="form-tander1 requisition-summary-box marg-bottom-20" style="margin-top: 16px;">
					<div class="col-sm-4 col-md-3 col-xs-6">
						<label class="set-line-height"><spring:message code="delivery.tracking.number" /></label>
					</div>
					<div class="col-sm-5 col-md-5 col-xs-6">
						<c:if test="${deliveryOrder.status eq  'DRAFT' }">
							<input name="trackingNumber" id="trackingNumber" type="text" value="${deliveryOrder.trackingNumber}" class="form-control" placeholder="Tracking Number" maxLength="128" />
						</c:if>
						<c:if test="${deliveryOrder.status ne  'DRAFT' }">
							<p class="set-line-height">${deliveryOrder.trackingNumber != null ? deliveryOrder.trackingNumber : 'N/A'}</p>
						</c:if>
					</div>
				</div>
				<div class="form-tander1 requisition-summary-box marg-bottom-20">
					<div class="col-sm-4 col-md-3 col-xs-6">
						<label class="set-line-height"><spring:message code="delivery.courier" /></label>
					</div>
					<div class="col-sm-5 col-md-5 col-xs-6">
						<c:if test="${deliveryOrder.status eq  'DRAFT' }">
							<input name="courierName" type="text" value="${deliveryOrder.courierName}" class="form-control" placeholder="Courier" maxLength="64" />
						</c:if>
						<c:if test="${deliveryOrder.status ne  'DRAFT' }">
							<p class="set-line-height">${deliveryOrder.courierName != null ? deliveryOrder.courierName : 'N/A'}</p>
						</c:if>
					</div>
				</div>
			</div>
		</div>
	</div>
	<c:if test="${deliveryOrder.status eq 'DRAFT'}">
		<jsp:include page="doDraftItems.jsp" />
	</c:if>
	<c:if test="${deliveryOrder.status ne 'DRAFT'}">
		<jsp:include page="doItems.jsp" />
	</c:if>

	<div class="upload_download_wrapper clearfix marg-top-10 event_info">
		<h4>Footer Details</h4>
		<c:if test="${deliveryOrder.status eq 'DRAFT'}">
			<div class="form-tander1 requisition-summary-box">
				<label class="col-sm-4 col-md-3 col-xs-6 control-label">Select Footer</label>
				<div class="col-sm-4 col-md-4 col-xs-5 " id="selectFooterDiv">
					<select id="idSelectFooter" name="footer" class="form-control chosen-select" value="${deliveryOrder.footer} }" data-validation="required">
						<option value="">Select Footer</option>
						<c:forEach items="${footerList}" var="footer">
							<c:if test="${footer.id eq deliveryOrder.footer.id}">
								<option value="${footer.id}" selected>${footer.title}</option>
							</c:if>
							<c:if test="${footer.id ne deliveryOrder.footer.id}">
								<option value="${footer.id}">${footer.title}</option>
							</c:if>

						</c:forEach>
					</select>
				</div>
			</div>
		</c:if>
		<div class="pad_all_15" id="footerContent">
			<span class="md-0 reset-that"> ${deliveryOrder.footer.content} </span>
		</div>
	</div>

	<jsp:include page="doAudit.jsp" />
	</div>

	<c:if test="${deliveryOrder.status eq 'DRAFT' or deliveryOrder.status eq 'DELIVERED'}">
		<div class="row">
			<div class="col-md-12 col-xs-12 col-ms-12 marg-top-20">
				<c:if test="${deliveryOrder.status eq 'DRAFT'}">
					<button type="button" id="confirmFinishBtn" role="button" class="btn btn-info ph_btn hvr-pop marg-left-10 right-header-button hvr-rectangle-out  ${ buyerReadOnlyAdmin ? 'disabled' : '' }">
						<spring:message code="summarydetails.mailbox.send" />
					</button>
					<button type="button" id="saveDraftBtn" onclick="draftDo();" class="btn btn-black ph_btn hvr-pop marg-left-10 right-header-button skipvalidation">
						<spring:message code="application.draft" />
					</button>
				</c:if>
				<c:if test="${!buyerReadOnlyAdmin}">
					<a href="#confirmCancel" id="confirmCancelBtn" role="button" class="btn btn-danger ph_btn hvr-pop marg-left-10 right-header-button" data-toggle="modal"><spring:message code="do.application.cancel" /></a>
				</c:if>
			</div>
		</div>
	</c:if>

</form:form>

<div class="modal fade" id="confirmFinish" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="do.confirm.send" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<div class="col-md-12">
				<div class="row">
					<div class="modal-body col-md-12">
						<label> <spring:message code="do.send.confirm" />
						</label>
					</div>
				</div>
			</div>
			<%-- <form id="" action="${pageContext.request.contextPath}/supplier/doFinish" method="post"> --%>
			<input type="hidden" name="doId" value="${deliveryOrder.id}"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<input type="button" id="finishBtn" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left" value="Yes">
				<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
					<spring:message code="application.no2" />
				</button>
			</div>
			<%-- </form> --%>
		</div>
	</div>
</div>


<div class="modal fade" id="confirmCancel" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="do.confirm.canceldo" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<form id="" action="${pageContext.request.contextPath}/supplier/cancelDo" method="post">
				<input type="hidden" name="doId" value="${deliveryOrder.id}"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<div class="col-md-12">
					<div class="row">
						<div class="modal-body col-md-12">
							<label> <spring:message code="do.cancel.confirm" />
							</label>
						</div>
						<div class="form-group col-md-6">
							<textarea class="width-100" placeholder="Enter Comments.(Mandatory)" rows="3" name="supplierRemark" id="supplierRemark" data-validation="required length" data-validation-length="max500"></textarea>
						</div>
					</div>
				</div>
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
					<input type="submit" id="cancelPo" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left" value="Yes">
					<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
						<spring:message code="application.no2" />
					</button>
				</div>
			</form>
		</div>
	</div>
</div>

<!-- Create SHIP popup  -->
<div class="modal fade" id="modal-shipmpa" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="poSummary.confirm.ship" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<form id="" action="${pageContext.request.contextPath}/supplier/createDo" method="post">
				<input type="hidden" name="poId" value="${po.id}"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<div class="col-md-12">
					<div class="row">
						<div class="modal-body col-md-12">
							<label> <spring:message code="posummary.sure.want.shipitem" />
							</label>
						</div>
					</div>
				</div>
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
					<button type="button" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left" id="idShipMpa">
						<spring:message code="application.yes2" />
					</button>
					<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right no-button" data-dismiss="modal">
						<spring:message code="application.no2" />
					</button>
				</div>
			</form>
		</div>
	</div>
</div>
<style>
.width-90 {
	width: 90%;
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

//
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

.dropdown-menu .checkbox {
	display: inline-block !important;
	margin: 0 !important;
	padding: 0 !important;
	position: relative !important;
}

.dataTables_wrapper.form-inline.no-footer {
	overflow: hidden;
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

.buyerAddressRadiosCore.active:hover, .buyerAddressRadiosCore.active.enabledBlockCore
	{
	background: #eee;
}

.buyerAddressRadiosCore.active {
	border: 1px solid #dfe8f1;
	border-radius: 2px;
	display: block !important;
}

.buyerAddressRadiosCore {
	width: 100% !important;
	padding: 10px;
	cursor: pointer;
	display: none;
	-webkit-transition: all 1s;
	transition: all 1s;
}
</style>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/datepicker/datepicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/moment.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/timepicker/timepicker.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/timepicker/timepicker.css"/>">
<script type="text/javascript">
	$(function() {
		"use strict";
		var nowTemp = new Date();
		var now = new Date(nowTemp.getFullYear(), nowTemp.getMonth(), nowTemp
				.getDate(), 0, 0, 0, 0);

		$('#deliveryDate').bsdatepicker({
			format : 'dd/mm/yyyy',
			minDate : now,
			onRender : function(date) {
				return date.valueOf() < now.valueOf() ? 'disabled' : '';
			}

		}).on('changeDate', function(e) {
			$(this).blur();
			$(this).bsdatepicker('hide');
		});
	});

	$('.timepicker-example').timepicker({
		disableFocus : true,
		explicitMode : false
	}).on('hide.timepicker', function(e) {
		e.preventDefault();
		$(this).blur();
	});

	$.validate({
		lang : 'en',
		onfocusout : false,
		validateOnBlur : true,
		modules : 'date,sanitize'
	});

	$('document').ready(
			function() {
				$('#footerContentDiv').hide();

				$('#finishBtn').on(
						'click',
						function(e) {
							console.log('Finished clicked....');
							e.preventDefault();
							if ($("#idDoForm").isValid()) {
								$(this).addClass('disabled');
								$('#confirmFinishBtn').addClass('disabled');
								$('#confirmCancelBtn').addClass('disabled');
								$('#saveDraftBtn').addClass('disabled');
								$('#idDoForm').attr(
										'action',
										getContextPath()
												+ '/supplier/orderFinish');
								$("#idDoForm").submit();
							} else {
								return;
							}

						});

				$('#confirmFinishBtn').on('click', function(e) {
					if (!$("#idDoForm").isValid()) {
						return;
					}
					$('#finishDo').removeClass('disabled');
					$('#confirmCancelBtn').removeClass('disabled');
					$('#saveDraftBtn').removeClass('disabled');
					$('#confirmFinish').modal('show');
				});

			});

	//Skip JQuery validations for save draft
	$(".skipvalidation ").on('click', function(e) {
		if ($("#skipper").val() == undefined) {
			e.preventDefault();
			$(this).after("<input type='hidden' id='skipper' value='1'>");
			$('form.has-validation-callback :input').each(function() {
				$(this).on('beforeValidation', function(value, lang, config) {
					$(this).attr('data-validation-skipped', 1);
				});
			});
			$(this).trigger("click");
		}
	});

	function draftDo() {
		$('#idDoForm').attr('action',
				getContextPath() + '/supplier/updateDoItems');
		$("#idDoForm").submit();
	}

	$('#idSelectFooter')
			.change(
					function(event) {
						event.preventDefault();
						$('div[id=idGlobalSuccess]').hide();
						$('div[id=idGlobalError]').hide();
						var footerId = $('#idSelectFooter').val();
						if (footerId != '') {
							var header = $("meta[name='_csrf_header']").attr(
									"content");
							var token = $("meta[name='_csrf']").attr("content");
							$.ajax({
								type : "GET",
								url : getContextPath()
										+ "/supplier/getFooterContentById/"
										+ footerId,
								beforeSend : function(xhr) {
									$('#loading').show();
									xhr.setRequestHeader(header, token);
									xhr.setRequestHeader("Accept",
											"application/json");
									xhr.setRequestHeader("Content-Type",
											"application/json");
								},
								complete : function() {
									$('#loading').hide();
								},
								success : function(data) {
									$('#footerContent').html(data.content);
									$('#footerContent').show();
									$('#footerContentDiv').show();
								},
								error : function(request, textStatus,
										errorThrown) {
								}
							});
						} else {
							$('#footerContent').hide();
							$('#footerContentDiv').hide();
						}

					});

	$.formUtils.addValidator({
		name : 'buyer_address',
		validatorFunction : function(value, $el, config, language, $form) {
			var response = true;
			var fieldName = $el.attr('name');
			console.log($('[name="' + fieldName + '"]:checked').length);
			if ($('[name="' + fieldName + '"]:checked').length == 0) {
				response = false;
			}
			return response;
		},
		errorMessage : 'This is a required field',
		errorMessageKey : 'badBuyerAddress'
	});

	$(document).delegate('.delivery_add', 'keyup', function() {
		var $rows = $('.role-bottom-ul li');
		var val = $.trim($(this).val()).replace(/ +/g, ' ').toLowerCase();
		$rows.show().filter(function() {
			var text = $(this).text().replace(/\s+/g, ' ').toLowerCase();
			return !~text.indexOf(val);
		}).hide();
	});

	$(document).delegate(
			'.role-bottom-ul li [type="radio"]',
			'click',
			function() {
				var dataAddress = $(this).closest('li').children('.del-add')
						.html();
				$('.phisicalArressBlock').html(dataAddress);
				$('.physicalCriterion input[type="checkbox"]').prop('checked',
						true);
				$('.buyerAddressRadios').addClass('active enabledBlock');
				$.uniform.update();
				$("#sub-credit").slideUp();
			});
	$(document).delegate('#deletecorpAddress', 'click', function() {
		$(".buyerAddressRadios").removeClass("active");
		$('#sub-credit input[type="radio"]').prop('checked', false);
		$.uniform.update();
		$("#sub-credit").slideDown();
	});
	$(document).delegate('.phisicalArressBlock', 'click', function(event) {
		$("#sub-credit").slideToggle();
	});

	$.formUtils.addValidator({
		name : 'buyer_correspondenceaddress',
		validatorFunction : function(value, $el, config, language, $form) {
			var response = true;
			var fieldName = $el.attr('name');
			console.log($('[name="' + fieldName + '"]:checked').length);
			if ($('[name="' + fieldName + '"]:checked').length == 0) {
				response = false;
			}
			return response;
		},
		errorMessage : 'This is a required field',
		errorMessageKey : 'badBuyerAddresscor'
	});

	$(document).delegate('.delivery_corresadd', 'keyup', function() {
		var $rows = $('.role-bottom-ul-core li');
		var val = $.trim($(this).val()).replace(/ +/g, ' ').toLowerCase();
		$rows.show().filter(function() {
			var text = $(this).text().replace(/\s+/g, ' ').toLowerCase();
			return !~text.indexOf(val);
		}).hide();
	});
	$(document).delegate(
			'.role-bottom-ul-core li [type="radio"]',
			'click',
			function() {
				var dataAddress = $(this).closest('li')
						.children('.del-add-cor').html();
				$('.phisicalArressBlockCore').html(dataAddress);
				$('.physicalCriterionCore input[type="checkbox"]').prop(
						'checked', true);
				$('.buyerAddressRadiosCore')
						.addClass('active enabledBlockCore');
				$.uniform.update();
				$("#sub-credit-core").slideUp();
			});

	$(document).delegate('#deletecorpAddressCore', 'click', function() {
		$(".buyerAddressRadiosCore").removeClass("active");
		$('#sub-credit-core input[type="radio"]').prop('checked', false);
		$.uniform.update();
		$("#sub-credit-core").slideDown();
	});
	$(document).delegate('.phisicalArressBlockCore', 'click', function(event) {
		$("#sub-credit-core").slideToggle();
	});
	$('#doMPA').click(function(e) {
		e.preventDefault();
		$('#modal-shipmpa').modal();
	});
	$('#idShipMpa').click(function(e) {
		window.open("https://myparcelasia.com/procurehere");
		$('#modal-shipmpa').modal('hide');
	});
</script>


<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/cq_form.css"/>">

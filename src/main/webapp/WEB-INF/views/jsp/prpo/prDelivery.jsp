<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize access="hasRole('ADMIN')" var="isAdmin" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<spring:message var="prDeliveryDesk" code="application.pr.create.delivery.details" />
<script type="text/javascript">
zE(function() {
	zE.setHelpCenterSuggestions({ labels: [${prDeliveryDesk}] });
});
</script>
<style type="text/css">
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
	<div id="page-content">
		<div class="container">
			<!-- pageging  block -->
			<ol class="breadcrumb">
				<c:url var="buyerDashboard" value="/buyer/buyerDashboard" />
				<li><a id="dashboardLink" href="${buyerDashboard}"> <spring:message code="application.dashboard" />
				</a></li>
				<li class="active"><spring:message code="pr.purchase.requisition" /></li>
			</ol>
			<section class="create_list_sectoin">
				<div class="Section-title title_border gray-bg mar-b20">
					<h2 class="trans-cap supplier">
						<spring:message code="pr.purchase.requisition" />
					</h2>
				</div>
				<jsp:include page="prHeader.jsp"></jsp:include>
				<div class="clear"></div>
				<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
				<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
				<div class="clear"></div>
				<div class="white-bg border-all-side float-left width-100 pad_all_15">
					<div class="row">
						<div class="col-md-12 col-sm-12 col-xs-12">
							<div class="tag-line">
								<h2>PR :${pr.name}</h2>
								<br>
								<h2><spring:message code="prtemplate.case.id" /> :${pr.prId}</h2>
							</div>
							<div class="print-down">
								<label><spring:message code="application.status" /> : </label>${pr.status}
							</div>
						</div>
					</div>
				</div>
				<c:url var="prDelivery" value="/buyer/prDelivery" />
				<form:form id="prDeliveryForm" action="${prDelivery}" method="post" modelAttribute="pr" acceptCharset="UTF-8">
					<form:hidden path="id" value="${pr.id}" />
					<div class="tab-pane" style="display: block">
						<div class="heading-tab-pr"><spring:message code="pr.delivery.detail" /></div>
						<div class="upload_download_wrapper event_info">
							<h4><spring:message code="prtemplate.delivery.information" /></h4>
							<div class="event_form">
								<div class="form-tander1 pad_all_15">
									<div class="col-sm-4 col-md-3 col-xs-6">
										<label><spring:message code="eventsummary.eventdetail.deliverydate" /></label>
									</div>
									<%-- <div class="col-sm-5 col-md-5 col-xs-6">
										<div class="input-prepend input-group">
										<fmt:formatDate var="deliveryDate" value="${pr.deliveryDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>"  />
											<form:input onfocus="this.blur()" path="deliveryDate" data-date-start-date="0d" id="daterangepicker-time-single" class="form-control for-clander-view for-clander-view" type="text" data-validation="required"
												data-validation-format="dd/mm/yyyy hh:ii A - dd/mm/yyyy hh:ii P" value="${deliveryDate}"></form:input>
										</div>
									</div> --%>

									<div id="deliveryTime">

										<div class="col-sm-3 col-md-3 col-xs-3">
											<div class="input-prepend input-group  ${buyerReadOnlyAdmin ? 'disabled' : ''}">
												<spring:message code="dateformat.placeholder" var="dateformat"/>
												<spring:message code="tooltip.delivery.date" var="deliveryadds"/>
												<form:input path="deliveryDate" readonly="readonly" data-placement="top" data-toggle="tooltip" data-original-title="${deliveryadds}" class="nvclick form-control for-clander-view" data-validation="required date" data-fv-date-min="15/10/2016" data-validation-format="dd/mm/yyyy"
													placeholder="${dateformat}" autocomplete="off" />
											</div>
										</div>
										<div class="col-md-2 col-sm-3 col-xs-3 col-lg-2" >
											<div class="bootstrap-timepicker dropdown">
												<form:input path="deliveryTime" data-validation="required" class="bootstrap-timepicker timepicker-example for-timepicker-view form-control" autocomplete="off"/>
											</div>
										</div>
									</div>

								</div>
								<div class="form-tander1 pad_all_15">
									<div class="form_field">
										<div class="form-group">
											<label class="col-sm-4 col-md-3 col-xs-6 control-label"><spring:message code="eventsummary.eventdetail.deliveryadds" /> :</label>
											<div class="col-sm-5 col-md-5 col-xs-5">
												<div class="input-prepend input-group">
													<label class="physicalCriterion pull-left"> </label>
													<span class="pull-left buyerAddressRadios <c:if test="${not empty pr.deliveryAddress}">active enabledBlock</c:if>">
														<span class="phisicalArressBlock pull-left marg-top-10">
															<c:if test="${not empty pr.deliveryAddress}">
																<div class="">
																	<h5>${pr.deliveryAddress.title}</h5>
																	<span class='desc'>${pr.deliveryAddress.line1}, ${pr.deliveryAddress.line2}, ${pr.deliveryAddress.city}, ${pr.deliveryAddress.zip}, ${pr.deliveryAddress.state.stateName}, ${pr.deliveryAddress.state.country.countryName}</span>
																</div>
															</c:if>
														</span>
														<div class=" align-right">
															<a id="deletecorpAddress" class="pull-right" style="font-size: 18px; line-height: 1; padding: 0px; color: rgb(127, 127, 127); margin-top: 8px;"> <i class="fa fa-times-circle"></i>
															</a>
														</div>
												</div>
												<div id="sub-credit" class="invite-supplier delivery-address margin-top-10">
													<div class="role-upper ">
														<div class="col-sm-12 col-md-12 col-xs-12 float-left">
															<%-- <spring:message var="placeAddress" code="pr.correspondence.address" /> --%>
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
									</div>
								</div>
								<div class="clear"></div>
								<div class="form-tander1 pad_all_15">
									<div class="col-sm-4 col-md-3 col-xs-6">
										<label><spring:message code="prtemplate.delivery.reciever" /></label>
									</div>
									<div class="col-sm-5 col-md-5 col-xs-6">
										<form:input path="deliveryReceiver" type="text" data-validation="required length" data-validation-length="max150" class="form-control" />
									</div>
								</div>
							</div>
						</div>
						<div class="btn-next">
							<div class="row">
								<div class="col-md-12 col-xs-12 col-ms-12">
									<c:url var="prSupplier" value="/buyer/prDocument/${pr.id}" />
									<a href="${prSupplier}" id="previousButton" class="btn btn-black marg-top-20 ph_btn hvr-pop hvr-rectangle-out1"><spring:message code="application.previous" /></a>
									<button id="nextButton" class="btn btn-info ph_btn marg-left-10 marg-top-20 hvr-pop hvr-rectangle-out" href="#"><spring:message code="application.next" /></button>
									<spring:message code="application.draft" var="draft" />
									<input type="button" id="submitStep1PrDetailDraft" class="step_btn_1 btn btn-black marg-top-20 hvr-pop hvr-rectangle-out1 ph_btn hvr-pop submitStep1 pull-right skipvalidation" value="${draft}" />
									<c:if test="${pr.status eq 'DRAFT' && (isAdmin or eventPermissions.owner)}">
										<a href="#confirmCancelPr" role="button" class="btn btn-danger marg-top-20 marg-right-10 ph_btn hvr-pop right-header-button" data-toggle="modal"><spring:message code="prtemplate.cancel.pr" /></a>
									</c:if>
								</div>
							</div>
						</div>
					</div>
				</form:form>
			</section>
		</div>
	</div>
</div>
<!-- cancel pr popup  -->
<div class="modal fade" id="confirmCancelPr" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3><spring:message code="eventsummary.confirm.cancel" /></h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<form id="" action="${pageContext.request.contextPath}/buyer/cancelPr" method="get">
				<input type="hidden" name="prId" value="${pr.id}"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<div class="col-md-12">
					<div class="row">
						<div class="modal-body col-md-12">
							<label>
								<spring:message code="prtemplate.sure.want.cancel" />
							</label>
						</div>
						<div class="form-group col-md-6">
						<spring:message code="event.reason.cancellation.placeholder" var="reasonCancel" />
							<textarea class="width-100" placeholder="${reasonCancel}" rows="3" name="cancelReason" id="cancelReason" data-validation="required length" data-validation-length="max1000"></textarea>
						</div>
					</div>
				</div>
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
					<input type="submit" id="cancelPr" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left" value="Yes">
					<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal"><spring:message code="application.no2" /></button>
				</div>
			</form>
		</div>
	</div>
</div>
<script>

$('document').ready(function() {

	$('#cancelPr').click(function() {
		var cancelRequest = $('#cancelReason').val();
		if (cancelRequest != '') {
			$(this).addClass('disabled');
		}
	});
	var addressId="${pr.deliveryAddress.id}";
	if(addressId){
		<c:forEach var="address" items="${addressList}" varStatus="status">
		  if(addressId === "${address.id}"){
		  $("#test"+"${status.index+1}").prop("checked", true);
		  }
	    </c:forEach>
	}
});

	<c:if test="${eventPermissions.viewer or buyerReadOnlyAdmin or eventPermissions.approverUser}">
	$(window).bind('load', function() {
		var allowedFields = '#nextButton,#dashboardLink,#previousButton,#bubble,#test';
		//var disableAnchers = ['#reloadMsg'];  
		$('#delivaddress').addClass('readOnlyClass');
	    $('#delivaddress').addClass('disabled');
	    $('#deliveryTime').addClass('disabled');
	    
	    
	    
		disableFormFields(allowedFields);
	});
	</c:if>
</script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/moment.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker-demo.js?1"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/datepicker/datepicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/timepicker/timepicker.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/timepicker/timepicker.css"/>">
<script type="text/javascript" src="<c:url value="/resources/js/view/prDelivery.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/view/modalValidation.js"/>"></script>



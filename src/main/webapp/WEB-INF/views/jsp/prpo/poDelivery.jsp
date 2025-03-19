<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize access="hasRole('ADMIN')" var="isAdmin" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<spring:message var="poDeliveryDesk" code="application.po.create.delivery.details" />
<script type="text/javascript">
zE(function() {
	zE.setHelpCenterSuggestions({ labels: [${poDeliveryDesk}] });
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
				<div class="clear"></div>
				<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
				<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
				<div class="clear"></div>
				<jsp:include page="poTagHeader.jsp"></jsp:include>
				<c:url var="poDelivery" value="/buyer/poDelivery" />
				<form:form id="poDeliveryForm" action="${poDelivery}?prId=${pr.id}" method="post" modelAttribute="po" acceptCharset="UTF-8">
					<form:hidden path="id" value="${po.id}" />

					<div class="tab-pane" style="display: block">
						<div class="heading-tab-pr"><spring:message code="po.delivery.detail" /></div>
						<div class="upload_download_wrapper event_info">
							<h4><spring:message code="prtemplate.delivery.information" /></h4>
							<div class="event_form">
								<div class="form-tander1 pad_all_15">
									<div class="col-sm-4 col-md-3 col-xs-6">
										<label><spring:message code="eventsummary.eventdetail.deliverydate" /></label>
									</div>
									<%-- <div class="col-sm-5 col-md-5 col-xs-6">
										<div class="input-prepend input-group">
										<fmt:formatDate var="deliveryDate" value="${po.deliveryDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>"  />
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
													<span class="pull-left buyerAddressRadios <c:if test="${not empty po.deliveryAddress}">active enabledBlock</c:if>">
														<span class="phisicalArressBlock pull-left marg-top-10">
															<c:if test="${not empty po.deliveryAddress}">
																<div class="">
																	<h5>${po.deliveryAddress.title}</h5>
																	<span class='desc'>${po.deliveryAddress.line1}, ${po.deliveryAddress.line2}, ${po.deliveryAddress.city}, ${po.deliveryAddress.zip}, ${po.deliveryAddress.state.stateName}, ${po.deliveryAddress.state.country.countryName}</span>
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
													    <c:set var="deliveryAddressId" value="${po.deliveryAddress.id}"/>
														<div class="scroll_box_inner">
															<div class="role-main">
																<div class="role-bottom small-radio-btn ${buyerReadOnlyAdmin ? 'disabled' : ''}">
																	<ul class="role-bottom-ul">
																		<c:forEach var="address" items="${addressList}" varStatus="status">
																			<li>
																				<div class="radio-info">
																					<label>
																						<form:radiobutton
                                                                                            path="deliveryAddress"
                                                                                            id="test${status.index + 1}"
                                                                                            value="${address.id}"
                                                                                            class="custom-radio"
                                                                                            data-validation-error-msg-container="#address-buyer-dialog"
                                                                                            data-validation="buyer_address"
                                                                                            checked="${address.id eq deliveryAddressId}"
                                                                                        />
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
						<c:if test="${(po.status eq 'DRAFT' || po.status eq 'SUSPENDED') && eventPermissions.owner}">
                            <div class="btn-next">
                                <div class="row">
                                    <div class="col-md-12 col-xs-12 col-ms-12">
                                        <c:url var="poDocumentNext" value="/buyer/po/poDocumentView/${po.id}?prId=${pr.id}" />
                                        <a href="${poDocumentNext}" id="previousButton" class="btn btn-black marg-top-20 ph_btn hvr-pop hvr-rectangle-out1"><spring:message code="application.previous" /></a>
                                        <button id="nextButton" class="btn btn-info ph_btn marg-left-10 marg-top-20 hvr-pop hvr-rectangle-out" href="#"><spring:message code="application.next" /></button>
                                        <spring:message code="application.draft" var="draft" />
                                        <input type="button" id="submitStep1PoDetailDraft" class="step_btn_1 btn btn-black marg-top-20 hvr-pop hvr-rectangle-out1 ph_btn hvr-pop submitStep1 pull-right skipvalidation" value="${draft}" />
                                        <c:if test="${(po.status eq 'DRAFT' || po.status eq 'SUSPENDED') && eventPermissions.owner}">
                                            <a href="#confirmCancelPo" role="button" class="btn btn-danger marg-top-20 marg-right-10 ph_btn hvr-pop right-header-button" data-toggle="modal"><spring:message code="po.cancel.label" /></a>
                                        </c:if>
                                    </div>
                                </div>
                            </div>
                        </c:if>
					</div>
				</form:form>
			</section>
		</div>
	</div>
</div>
<jsp:include page="poModal.jsp"></jsp:include>
<script>

$('document').ready(function() {
	var addressId="${po.deliveryAddress.id}";

	if(addressId){
		<c:forEach var="address" items="${addressList}" varStatus="status">
		    var addr="${address.id}";
		    console.log(addressId+" >>>> "+addr);

              if(addressId === addr){
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
<script type="text/javascript" src="<c:url value="/resources/js/view/poDelivery.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/view/po.js"/>"></script>



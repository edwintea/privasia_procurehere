<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<sec:authorize access="hasRole('ADMIN')" var="isAdmin" />
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<spring:message var="prSupplierDesk" code="application.pr.create.supplier.details" />
<script type="text/javascript">
zE(function() {
	zE.setHelpCenterSuggestions({ labels: [${prSupplierDesk}] });
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
								<h2>ID :${pr.prId}</h2>
							</div>
							<div class="print-down">
								<label>Status : </label>${pr.status}
							</div>
						</div>
					</div>
				</div>
				<input type="hidden" id="contactListSize" value="${fn:length(prContactList)}"> <input type="hidden" id="selectedSuppId">
				<c:url var="prSupplier" value="/buyer/prSupplier" />
				<form:form id="prSupplierForm" action="${prSupplier}" method="post" modelAttribute="pr">
					<form:hidden path="id" value="${pr.id}" />
					<form:hidden path="prId" />
					<div class="tab-pane active">
						<div class="">
							<div class="tab-pane active white-bg" id="step-1">
								<div class="upload_download_wrapper clearfix mar-t20 event_info">
									<input type="hidden" id="prItemExists" value="${prItemExists}">
									<h4>Supplier Detail</h4>
									<div class="row">
										<div class="form-tander1 pad_all_15">
											<div class="col-sm-4 col-md-3 col-xs-6">
												<label>Supplier Selection</label>
											</div>
											<div class="col-sm-5 col-md-5 col-xs-6 col-xs-6">
												<div class="radio-info pull-left marg-right-10 marg_top_15">
													<label class="select-radio-lineHgt"> <input type="radio" name="supplierChoice" id="supplierChoice" <c:if test="${empty pr.supplierName}">checked="checked"</c:if> class="custom-radio showSupplierBlocks" value="LIST" /> My Supplier
													</label>
												</div>
												<div class="radio-info pull-left marg-left-10 marg_top_15">
													<label class="select-radio-lineHgt"> <input type="radio" name="supplierChoice" id="supplierChoice" <c:if test="${not empty pr.supplierName}">checked="checked"</c:if> class="custom-radio showSupplierBlocks" value="MANUAL" /> Open Supplier
													</label>
												</div>
											</div>
										</div>
										<div class="showPartMANUAL supplierBoxes">
											<div class="form-tander1 pad_all_15">
												<div class="col-sm-4 col-md-3 col-xs-6">
													<label>Supplier Name</label>
												</div>
												<div class="col-sm-5 col-md-5 col-xs-6 col-xs-6">
													<form:input path="supplierName" type="text" placeholder="Enter Name" data-validation="required length" data-validation-length="max128" class="form-control" />
												</div>
											</div>
											<div class="form-tander1 pad_all_15">
												<div class="col-sm-4 col-md-3 col-xs-6">
													<label>Address</label>
												</div>
												<div class="col-sm-5 col-md-5 col-xs-6 col-xs-6">
													<form:textarea path="supplierAddress" maxlength="250" placeholder="Enter Address" rows="5" data-validation="required length" data-validation-length="max250" class="form-control" />
												</div>
											</div>
											<div class="form-tander1 pad_all_15">
												<div class="col-sm-4 col-md-3 col-xs-6">
													<label>Office Telephone Number</label>
												</div>
												<div class="col-sm-5 col-md-5 col-xs-6 col-xs-6">
													<form:input path="supplierTelNumber" type="text" placeholder="e.g. 03-9282777 ext 234" data-validation="required length" data-validation-length="6-50" class="form-control" />
												</div>
											</div>
											<div class="form-tander1 pad_all_15">
												<div class="col-sm-4 col-md-3 col-xs-6">
													<label>Office Fax Number</label>
												</div>
												<div class="col-sm-5 col-md-5 col-xs-6">
													<form:input path="supplierFaxNumber" id="idFax" type="text" placeholder="e.g. 6017666666" data-validation="length number" data-validation-ignore="+ " data-validation-length="6-14" data-validation-optional="true" class="form-control" />
												</div>
											</div>
											<div class="form-tander1 pad_all_15">
												<div class="col-sm-4 col-md-3 col-xs-6">
													<label> Tax Number</label>
												</div>
												<div class="col-sm-5 col-md-5 col-xs-6">
													<form:input path="supplierTaxNumber" type="text" placeholder="Enter Tax Number" data-validation="length" data-validation-length="0-20" class="form-control" />
												</div>
											</div>
										</div>
									</div>
									<div class="form_field showPartLIST supplierBoxes">
										<div class="form-group">
											<label class="col-sm-4 col-md-3 col-xs-6 control-label"></label>
											<div class="col-sm-5 col-md-5 col-xs-5">
												<div class="input-prepend input-group">
													<label class="physicalCriterion pull-left"> </label> <span class="pull-left buyerAddressRadios <c:if test="${not empty pr.supplier}">active enabledBlock</c:if>"> <span class="phisicalArressBlock pull-left marg-top-10"> <c:if test="${not empty pr.supplier}">
																<div class="">
																	<h5>${pr.supplier.supplier.companyName}</h5>
																	<span class='desc'>${pr.supplier.fullName}/${pr.supplier.communicationEmail},${pr.supplier.companyContactNumber}</span>
																</div>
															</c:if>
													</span>
												</div>
												<div id="sub-credit" class="invite-supplier delivery-address margin-top-10">
													<div class="role-upper ">
														<div class="col-sm-12 col-md-12 col-xs-12 float-left">
															<input type="text" placeholder="Search Supplier" class="form-control delivery_add">
														</div>
													</div>
													<div class="chk_scroll_box">
														<div class="scroll_box_inner">
															<div class="role-main">
																<div class="role-bottom small-radio-btn ${buyerReadOnlyAdmin ? 'disabled' : ''}">
																	<ul class="role-bottom-ul">
																		<c:forEach var="supplier" items="${supplierList}">
																			<li>
																				<div class="radio-info">
																					<label> <form:radiobutton path="supplier" value="${supplier.id}" class="custom-radio supplierRadioButton" data-validation-error-msg-container="#pr_supplier-dialog" data-validation="pr_supplier" />
																					</label>
																				</div>
																				<div class="del-add">
																					<h5>${supplier.supplier.companyName}</h5>
																					<span class='desc'>${supplier.fullName}/${supplier.communicationEmail},${supplier.companyContactNumber}</span>
																				</div>
																			</li>
																		</c:forEach>
																	</ul>
																</div>
															</div>
														</div>
													</div>
												</div>
												<div id="pr_supplier-dialog"></div>
											</div>
										</div>
									</div>
								</div>
								<div class="upload_download_wrapper clearfix mar-t20 event_info event_form event_info ">
									<h4>Supplier Contact Details</h4>
									<div class="form_field">
										<div class="form-group">
											<div class="col-sm-5 col-md-5 col-xs-6">
												<div></div>
											</div>
										</div>
									</div>
									<div class="event_form">
										<div class="col-md-12">
											<table class="contactPersons ph_table display table table-bordered">
												<thead>
													<tr>
														<th><spring:message code="application.action" /></th>
														<th><spring:message code="eventdetails.event.title" /></th>
														<th><spring:message code="application.name" /></th>
														<th><spring:message code="application.designation" /></th>
														<th>Contact No.</th>
														<th>Mobile No.</th>
														<th>Fax No.</th>
														<th><spring:message code="application.emailaddress" /></th>
													</tr>
												</thead>
												<tbody>
													<c:forEach items="${prContactList}" var="contact">
														<tr>
															<td contact-id="${contact.id}"><a class="editContact" href=""> <img src="${pageContext.request.contextPath}/resources/images/edit1.png">
															</a> <a class="deleteContact" href=""> <img src="${pageContext.request.contextPath}/resources/images/delete1.png">
															</a></td>
															<td>${contact.title}</td>
															<td>${contact.contactName}</td>
															<td>${contact.designation}</td>
															<td>${contact.contactNumber}</td>
															<td>${contact.mobileNumber != null ? contact.mobileNumber : ""}</td>
															<td>${contact.faxNumber != null ? contact.faxNumber : ""}</td>
															<td>${contact.comunicationEmail}</td>
														</tr>
													</c:forEach>
												</tbody>
											</table>
											<div class="col-md-12 align-left">
												<div class="row">
													<a href="javascript:void(0);" data-toggle="tooltip" data-placement="top" title="Add More" class="btn btn-info btn-tooltip hvr-pop hvr-rectangle-out addContactPersonPop"> <spring:message code="eventdetails.event.add.contactperson" />
													</a>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class="btn-next">
						<c:url var="prDocument" value="/buyer/prDocument/${pr.id}" />
						<a href="${prDocument}" id="previousButton" class="btn btn-black ph_btn marg-top-20 hvr-pop hvr-rectangle-out1"> <spring:message code="application.previous" />
						</a>
						<button id="nextButton" class="btn btn-info ph_btn marg-left-10 marg-top-20 hvr-pop hvr-rectangle-out">
							<spring:message code="application.next" />
						</button>
						<spring:message code="application.draft" var="draft" />
						<input type="button" id="submitStep1PrDetailDraft" class="top-marginAdminList step_btn_1 btn btn-black hvr-pop hvr-rectangle-out1 ph_btn hvr-pop submitStep1 pull-right skipvalidation" value="${draft}" />
						<c:if test="${pr.status eq 'DRAFT' && (isAdmin or eventPermissions.owner)}">
							<a href="#confirmCancelPr" role="button" class="btn btn-danger marg-top-20 marg-right-10 ph_btn hvr-pop right-header-button" data-toggle="modal">Cancel PR</a>
						</c:if>
					</div>
				</form:form>
		</div>
		</section>
	</div>
</div>
<!-- contact detail pop up -->
<spring:message code="eventdetails.event.add.contactperson" var="addeditTitle" />
<div class="flagvisibility dialogBox" id="addEditContactPopup" title="${addeditTitle}">
	<div class="float-left width100 pad_all_15 white-bg">
		<form:form class="bordered-row" id="addContactForm" data-parsley-validate="" method="post" modelAttribute="prContact">
			<form:hidden path="id" />
			<input type="hidden" id="prId" name="prId" value="${pr.id}" />
			<input type="hidden" id="contactId" name="contactId" />
			<div class="row">
				<label class="col-sm-4 control-label"> <spring:message code="eventdetails.event.title" />
				</label>
				<div class="col-sm-6 col-md-6">
					<div class="input_wrapper form-group">
						<spring:message code="prSupplier.contact.place.title" var="title" />
						<form:input type="text" path="title" placeholder="${title}" class="form-control mar-b10" />
					</div>
				</div>
			</div>
			<div class="row">
				<label class="col-sm-4 control-label"> <spring:message code="eventdetails.event.contact.name" />
				</label>
				<div class="col-sm-6 col-md-6">
					<div class="input_wrapper form-group">
						<spring:message code="prSupplier.contact.place.name" var="name" />
						<form:input type="text" path="contactName" placeholder="${name}" data-validation="required length" data-validation-length="1-120" class="form-control mar-b10" />
					</div>
				</div>
			</div>
			<div class="row">
				<label class="col-sm-4 control-label"> <spring:message code="application.designation" />
				</label>
				<div class="col-sm-6 col-md-6">
					<div class="input_wrapper form-group">
						<spring:message code="prSupplier.contact.place.designation" var="designation" />
						<form:input type="text" path="designation" placeholder="${designation}" class="form-control mar-b10" />
					</div>
				</div>
			</div>
			<div class="row">
				<label class="col-sm-4 control-label"> <spring:message code="application.contact.number" />
				</label>
				<div class="col-sm-6 col-md-6">
					<div class="input_wrapper form-group">
						<spring:message code="prSupplier.contact.place.contactno" var="contactno" />
						<form:input type="text" path="contactNumber" id="idContactNumber" placeholder="${contactno}" data-validation="length number" data-validation-ignore="+ " data-validation-optional="true" data-validation-length="6-14" class="form-control mar-b10" />
					</div>
				</div>
			</div>
			<div class="row">
				<label class="col-sm-4 control-label"> <spring:message code="application.mobile.number" />
				</label>
				<div class="col-sm-6 col-md-6">
					<div class="input_wrapper form-group">
						<spring:message code="prSupplier.contact.place.mobile" var="mobile" />
						<form:input type="text" path="mobileNumber" id="idMobileNumber" placeholder="${mobile}" data-validation="length number" data-validation-optional="true" data-validation-ignore="+ " data-validation-length="max14" class="form-control mar-b10" />
					</div>
				</div>
			</div>
			<div class="row">
				<label class="col-sm-4 control-label"> <spring:message code="application.fax.number" />
				</label>
				<div class="col-sm-6 col-md-6">
					<div class="input_wrapper form-group">
						<spring:message code="prSupplier.contact.place.fax" var="fax" />
						<form:input type="text" path="faxNumber" id="idFaxNumber" placeholder="${fax}" data-validation="length number" data-validation-ignore="+ " data-validation-length="6-14" data-validation-optional="true" class="form-control mar-b10" />
					</div>
				</div>
			</div>
			<div class="row">
				<label class="col-sm-4 control-label"> <spring:message code="application.emailaddress" />
				</label>
				<div class="col-sm-6 col-md-6">
					<div class="input_wrapper form-group">
						<spring:message code="prSupplier.contact.place.email" var="email" />
						<form:input type="text" placeholder="${email}" path="comunicationEmail" data-validation="email" data-validation-optional="true" class="form-control mar-b10 feature_box" />
					</div>
				</div>
			</div>
			<div class="row">
				<div class="col-md-12">
					<div class="form-group align-center">
						<button type="reset" id="resetEventContctForm" style="display: none;"></button>
						<a href="javascript:void(0);" data-toggle="tooltip" data-placement="top" title="Add More" class="btn btn-info ph_btn btn-tooltip hvr-pop hvr-rectangle-out addContactPerson"> <spring:message code="eventdetails.event.add.contactperson" />
						</a>
						<button type="button" class="closeDialog btn btn-black marg-left-10 hvr-pop ph_btn hvr-rectangle-out1">
							<spring:message code="application.cancel" />
						</button>
					</div>
				</div>
			</div>
		</form:form>
	</div>
</div>
<!--delete contact pop up  -->
<div class="modal fade" id="confirmDeleteContact" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="application.confirm.delete" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<div class="modal-body">
				<label> <spring:message code="eventdetails.event.delete.contact" />
				</label> <input type="hidden" id="deleteIdContact" />
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<button type="button" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" id="confDelContact">
					<spring:message code="application.delete" />
				</button>
				<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
					<spring:message code="application.cancel" />
				</button>
			</div>
		</div>
	</div>
</div>
<!--confirm pop on changing supplier type -->
<div class="modal fade" id="confirmChangeSupplier" role="dialog" data-backdrop="static">
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
				<label id="changeSupplierLabel">Are you sure you want to delete purchase item related to this supplier? </label>
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<button type="button" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" id="confDelSupplier">
					<spring:message code="application.delete" />
				</button>
				<button id="cnclDelSupp" type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
					<spring:message code="application.cancel" />
				</button>
			</div>
		</div>
	</div>
</div>
<!-- confirm pop on changing supplier type test -->
<div class="modal fade" id="confirmChangeSupplierFavlist" role="dialog" data-backdrop="static">
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
				<label id="ChangeSupplierFavlistLabel">Are you sure you want to delete purchase item related to this supplier? </label>
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<button type="button" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" id="confDelSupplierFav">
					<spring:message code="application.delete" />
				</button>
				<button id="cnclDelSuppFav" type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
					<spring:message code="application.cancel" />
				</button>
			</div>
		</div>
	</div>
</div>
<!-- cancel pr popup       -->
<div class="modal fade" id="confirmCancelPr" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="eventsummary.confirm.cancel" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<form id="" action="${pageContext.request.contextPath}/buyer/cancelPr" method="get">
				<input type="hidden" name="prId" value="${pr.id}"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<div class="col-md-12">
					<div class="row">
						<div class="modal-body col-md-12">
							<label> Are you sure you want to cancel this PR. </br>Reason :
							</label>
						</div>
						<div class="form-group col-md-6">
							<spring:message code="event.reason.cancellation.placeholder" var="reasonCancellation"/>
							<textarea class="width-100" placeholder="${reasonCancellation}" rows="3" name="cancelReason" id="cancelReason" data-validation="required length" data-validation-length="max1000"></textarea>
						</div>
					</div>
				</div>
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
					<input type="submit" id="cancelPr" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left" value="Yes">
					<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">No</button>
				</div>
			</form>
		</div>
	</div>
</div>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/view/prSupplier.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.mask.min.js"/>"></script>
<script>
	$(document).ready(function(){
	  $('#idFax').mask('00 00000000000', {placeholder: "e.g. 60 352735465"});
	  $('#idFaxNumber').mask('00 00000000000', {placeholder: "e.g. 60 352735465"});
	  
	  	$('#cancelPr').click(function() {
	  		$(this).addClass('disabled');
	  	});

	});
</script>
<script>
	<c:if test="${eventPermissions.viewer or buyerReadOnlyAdmin or eventPermissions.approverUser}">
	$(window).bind('load', function() {
		var allowedFields = '#nextButton,#dashboardLink,#previousButton,#bubble';
		//var disableAnchers = ['#reloadMsg'];        
		disableFormFields(allowedFields);
	});
	</c:if>
</script>

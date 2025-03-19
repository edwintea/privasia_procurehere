<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<sec:authorize access="hasRole('BUYER_CREATE') or hasRole('ADMIN')" var="canEdit" />
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.css"/>">
<spring:message var="buyerCreateManualDesk" code="application.owner.manual.buyer.create" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${buyerCreateManualDesk}] });
});
</script>
<div id="page-content">
	<div class="col-md-offset-1 col-md-10">
		<ol class="breadcrumb" style="margin-left: 10px">
			<li>
				<a href="${pageContext.request.contextPath}/owner/ownerDashboard">Dashboard</a>
			</li>
			<li class="active">
				<a href="${pageContext.request.contextPath}/owner/buyerList"> Buyer List</a>
			</li>
			<li>Buyer Creation</li>
		</ol>
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap manage_icon">Buyer Creation</h2>
		</div>
		<div class="Invited-Supplier-List import-supplier white-bg">
			<div class="meeting2-heading">
				<h3>Create Buyer</h3>
			</div>
			<div class="import-supplier-inner-first-new pad_all_10 global-list">
				<div class="modal-content add_model_modifi">
					<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
					<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
					<c:url var="buyerCreation" value="/owner/buyerCreation" />
					<form:form action="${buyerCreation}" id="demo-form" method="POST" modelAttribute="buyerObj" autocomplete="off">
						<div class="form-group" id="add_buyerp1">
							<div class="row">
								<div class="col-md-4 col-sm-4 col-xs-12">
									<label class="control-label marg-top-10">Full Name</label>
								</div>
								<div class="col-sm-6 col-md-6 col-xs-12">
									<div class="form-group">
										<form:input type="text" path="fullName" placeholder="Admin Full Name" data-validation="required length" data-validation-length="4-128" class="form-control mar-b10" />
									</div>
								</div>
							</div>
							<div class="row">
								<div class="col-md-4 col-sm-4 col-xs-12">
									<label class="control-label marg-top-10">Login Email ID</label>
								</div>
								<div class="col-sm-6 col-md-6 col-xs-12">
									<div class="form-group">
										<form:input type="text" path="loginEmail" id="idLoginEmail" placeholder="This will also be your correspondence email" data-validation="required length email login_email"
											data-validation-length="6-128" class="form-control mar-b10" />
									</div>
								</div>
							</div>
							<div class="row">
								<div class="col-md-4 col-sm-4 col-xs-12">
									<label class="control-label marg-top-10">Contact Number</label>
								</div>
								<div class="col-sm-6 col-md-6 col-xs-12">
									<div class="form-group">
										<form:input type="tel" path="companyContactNumber" cssClass="form-control" id="idTelPhone" placeholder="e.g. +60 12345678" data-validation="required length number"
											data-validation-ignore="+ " data-validation-length="6-14" />
									</div>
								</div>
							</div>
							<div class="row">
								<div class="col-md-4 col-sm-4 col-xs-12">
									<label class="control-label marg-top-10">Company Name</label>
								</div>
								<div class="col-sm-6 col-md-6 col-xs-12">
									<div class="form-group">
										<form:input type="text" id="idCompanyName" path="companyName" data-validation="required length alphanumeric "
											data-validation-allowing=" &.,/()_-"
											data-validation-length="4-124"
											placeholder="Your Company Name" cssClass="form-control mar-b10" />
									</div>
								</div>
							</div>
							<div class="row">
								<div class="col-md-4 col-sm-4 col-xs-12">
									<label class="control-label marg-top-10"><spring:message code="supplier.registration.company.number" /></label>
								</div>
								<div class="col-sm-6 col-md-6 col-xs-12">
									<div class="form-group">
										<form:input type="text" id="idCompRegNum" path="companyRegistrationNumber" placeholder="Your company  registration number" 
											data-validation="required length alphanumeric "
											data-validation-allowing=" &.,/()_-" data-validation-length="2-124" cssClass="form-control mar-b10" />
									</div>
								</div>
							</div>
							<div class="row">
								<div class="col-md-4 col-sm-4 col-xs-12">
									<label for="idRegCountry">Country of Registration</label>
								</div>
								<div class="col-md-6 col-sm-6 col-xs-12">
									<div class="form-group">
										<form:select path="registrationOfCountry" id="idRegCountry" cssClass="chosen-select form-control" data-validation="required">
											<form:option value="">Select Country of Registration</form:option>
											<form:options items="${countryList}" itemValue="id" itemLabel="countryName" />
										</form:select>
									</div>
								</div>
							</div>
							<div class="row">
								<div class="col-md-4 col-sm-4 col-xs-12">
									<label for="idRegCountry">Plan</label>
								</div>
								<div class="col-md-6 col-sm-6 col-xs-12">
									<div class="input-prepend">
										<span class="phisicalArressBlock pull-left marg-top-10 width100   ${buyerObj.currentSubscription.plan != null ? 'buyerAddressRadios active' : ''}" style="display: none; padding: 10px 0;">
											<c:if test="${not empty buyerObj.currentSubscription.plan}">
												<div class="planSnip">
													<div class="col-md-10">
														<h5>${buyerObj.currentSubscription.plan.planName}</h5>
														<span class='desc width100'>${buyerObj.currentSubscription.plan.shortDescription}<br />Status: ${buyerObj.currentSubscription.plan.planStatus}
														</span>
													</div>
													<div class="col-md-2 align-right">
														<a class="pull-right ${buyerObj.currentSubscription.plan != null ? '' : 'flagvisibility'}" title="" data-placement="top" id="deletePlan" data-toggle="tooltip"
															data-original-title="Remove Plan" style="font-size: 18px; line-height: 1; padding: 0; color: #7f7f7f;">
															<i class="fa fa-times-circle"></i>
														</a>
													</div>
												</div>
											</c:if>
											</span>
									</div>
									<div id="sub-credit" class="invite-supplier delivery-address collapse margin-top-10">
										<div class="role-upper ">
											<div class="col-sm-12 col-md-12 col-xs-12 float-left">
												<input type="text" placeholder="Select Plan" class="form-control delivery_add">
											</div>
										</div>
										<div class="chk_scroll_box">
											<div class="scroll_box_inner">
												<div class="role-main">
													<div class="role-bottom small-radio-btn">
														<ul class="role-bottom-ul">
															<c:forEach var="plan" items="${planList}">
																<li>
																	<div class="radio-info">
																		<label>
																			<form:radiobutton path="currentSubscription.plan" data-validation-error-msg-container="#plan-sel-error" data-validation="required" value="${plan.id}" class="custom-radio" />
																		</label>
																	</div>
																	<div class="del-add">
																		<h5>${plan.planName}</h5>
																		<span class='desc width100'>${plan.shortDescription}<br />Status: ${plan.planStatus}
																		</span>
																	</div>
																</li>
															</c:forEach>
														</ul>
													</div>
												</div>
											</div>
										</div>
									</div>
									<span id="plan-sel-error"></span>
								</div>
							</div> 
							<%-- <div class="row">
								<div class="col-md-4 col-sm-4 col-xs-12">
									<label class="control-label marg-top-10">Quantity</label>
								</div>
								<div class="col-sm-6 col-md-6 col-xs-12">
									<div class="form-group">
										<form:input type="text" path="currentSubscription.planQuantity" data-validation="required length number" data-validation-length="1-6" placeholder="e.g. 10" class="form-control mar-b10" />
									</div>
								</div>
							</div> --%>
							
						<%-- 	<div class="row">
								<div class="col-md-4 col-sm-4 col-xs-12">
									<label class="control-label marg-top-10">Start Date</label>
								</div>
								<div class="col-sm-6 col-md-6 col-xs-12">
									<div class="form-group">
										<form:input path="currentSubscription.startDate" class="bootstrap-datepicker form-control for-clander-view" data-validation="required date" data-fv-date-min="15/10/2016"
											data-validation-format="dd/mm/yyyy" placeholder="dd/mm/yyyy" />
									</div>
								</div>
							</div> --%>
							
							<div class="row">
								<div class="col-md-4 col-sm-4 col-xs-12">
									<label class="control-label marg-top-10"><spring:message code="rfaevent.end.date"/></label>
								</div>
								<div class="col-sm-6 col-md-6 col-xs-12">
									<div class="form-group">
										<form:input path="currentSubscription.endDate" class="bootstrap-datepicker form-control for-clander-view" data-validation="required date" data-fv-date-min="15/10/2016"
											data-validation-format="dd/mm/yyyy" placeholder="dd/mm/yyyy" />
									</div>
								</div>
							</div>
							<%-- <div class="row">
								<div class="col-md-4 col-sm-4 col-xs-12">
									<label class="control-label marg-top-10">Trial Quantity (Days)</label>
								</div>
								<div class="col-sm-6 col-md-6 col-xs-12">
									<div class="form-group">
										<form:input type="text" path="currentSubscription.trialQuantity" data-validation="length" data-validation-length="0-6" placeholder="e.g. 10" class="form-control mar-b10" />
									</div>
								</div>
							</div> --%>
							<div class="row">
								<div class="col-md-4 col-sm-4 col-xs-12">
									<label class="control-label marg-top-10">Number of user</label>
								</div>
								<div class="col-sm-6 col-md-6 col-xs-12">
									<div class="form-group">
										<form:input data-validation="required length number" type="text" data-validation-length="1-9" id="idNoOfUsers" path="currentSubscription.userQuantity" class="form-control mar-b10" />
									</div>
								</div>
							</div>
							<div class="row">
								<div class="col-md-4 col-sm-4 col-xs-12">
									<label class="control-label marg-top-10">Number of event</label>
								</div>
								<div class="col-sm-6 col-md-6 col-xs-12">
									<div class="form-group">
										<form:input data-validation="required length, number" id="idNoOfEvents" path="currentSubscription.eventQuantity" data-validation-length="1-9" type="text" placeholder=""
											class="form-control mar-b10" />
									</div>
								</div>
							</div>
							<div class="row">
								<div class="col-md-8 col-xs-8 col-ms-offset-4 col-xs-offset-4 col-ms-8 col-md-offset-4 col-xs-offset-4">
									<sec:authorize access="hasRole('BUYER_CREATE') or hasRole('ADMIN')" var="canEdit">
										<form:button type="submit" class="btn btn-info ph_btn_midium getnextdata hvr-pop hvr-rectangle-out ${canEdit ? '':'disabled'}" id="addcompanyuser">Create</form:button>
									</sec:authorize>
									<c:url value="/owner/buyerList" var="listurl"></c:url>
									<a href="${listurl}" class="btn btn-black ph_btn_midium marg-left-10 getprevdata hvr-pop hvr-rectangle-out1">Cancel</a>
								</div>
							</div>
						</div>
					</form:form>
				</div>
			</div>
		</div>
	</div>
	</div>
	<script type="text/javascript" src="<c:url value="/resources/assets/widgets/chosen/chosen.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/resources/assets/widgets/chosen/chosen-demo.js"/>"></script>
	<!-- EQul height js-->
	<!-- daterange picker js and css start -->
	<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/moment.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker-demo.js?1"/>"></script>
	<!-- daterange picker js and css end -->
	<script type="text/javascript" src="<c:url value="/resources/assets/widgets/match-height/jquery.matchHeight.js"/>"></script>
	<!-- Theme layout -->
	<%-- <script type="text/javascript" src="<c:url value="/resources/assets/themes/admin/layout.js"/>"></script> --%>
	<script type="text/javascript" src="<c:url value="/resources/assets/widgets/datepicker/datepicker.js"/>"></script>
	<style>
.scroll_box_inner {
	overflow-y: scroll;
}

.del-add span {
	padding: 0 30px;
}
</style>
	<script type="text/javascript">
		// Datepicker bootstrap /

		$(function() {
			"use strict";
			var nowTemp = new Date();
			var now = new Date(nowTemp.getFullYear(), nowTemp.getMonth(), nowTemp.getDate(), 0, 0, 0, 0);
			// console.log(now);
			$('.bootstrap-datepicker').bsdatepicker({
				format : 'dd/mm/yyyy',
				onRender : function(date) {
					return date.valueOf() < now.valueOf() ? 'disabled' : '';
				}
			}).on('changeDate', function(e) {
				$(this).blur();
				$(this).bsdatepicker('hide');
			});
		});
	</script>
	<script>
		$(document).ready(function() {

			$(".toggle").click(function() {
				$(this).parent().toggleClass("highlight");
			});
		});
	</script>
	<script src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
	<script type="text/javascript">
		$.formUtils.addValidator({
			name : 'crn_number',
			validatorFunction : function(value, $el, config, language, $form) {
				var countryID = $('#idRegCountry').val();
				var crnNum = $('#idCompRegNum').val();
				var response = true;

				var header = $("meta[name='_csrf_header']").attr("content");
				var token = $("meta[name='_csrf']").attr("content");

				$.ajax({
					url : getOwnerContextPath() + "/checkBuyerRegistrationNumber",
					data : {
						'countryID' : countryID,
						'crnNum' : crnNum
					},
					async : false,
					beforeSend : function(xhr) {
						xhr.setRequestHeader(header, token);
					},
					success : function(data) {
						console.log(data);
						response = true;
					},
					error : function(request, textStatus, errorThrown) {
						console.log(textStatus);
						response = false;
					}
				});
				return response;
			},
			errorMessage : 'Company Registration Number already registered in the system',
			errorMessageKey : 'badCrnNumber'
		});

		$.formUtils.addValidator({
			name : 'company_name',
			validatorFunction : function(value, $el, config, language, $form) {
				var countryID = $('#idRegCountry').val();
				var companyName = $('#idCompanyName').val();
				var response = true;
				var header = $("meta[name='_csrf_header']").attr("content");
				var token = $("meta[name='_csrf']").attr("content");
				$.ajax({
					url : getOwnerContextPath() + "/checkBuyerCompanyName",
					data : {
						'countryID' : countryID,
						'companyName' : companyName
					},
					async : false,
					beforeSend : function(xhr) {
						xhr.setRequestHeader(header, token);
					},
					success : function(data) {
						console.log(data);
						response = true;
					},
					error : function(request, textStatus, errorThrown) {
						console.log(textStatus);
						response = false;
					}
				});
				return response;
			},
			errorMessage : 'Company Name already registered in the system',
			errorMessageKey : 'badCompanyName'
		});

		$.formUtils.addValidator({
			name : 'login_email',
			validatorFunction : function(value, $el, config, language, $form) {
				var loginEmailId = $('#idLoginEmail').val();
				var response = true;
				var header = $("meta[name='_csrf_header']").attr("content");
				var token = $("meta[name='_csrf']").attr("content");
				//alert(loginId);
				$.ajax({
					url : getContextPath() + "/owner/checkLoginEmail",
					data : {
						'loginEmail' : loginEmailId
					},
					async : false,
					beforeSend : function(xhr) {
						xhr.setRequestHeader(header, token);
					},
					success : function(data) {
						console.log(data);
						response = true;
					},
					error : function(request, textStatus, errorThrown) {
						console.log(textStatus);
						response = false;
					}
				});
				return response;
			},
			errorMessage : 'Login Email already registered in the system',
			errorMessageKey : 'badLoginEmail'
		});

		$.validate({
			lang : 'en',
			modules : 'date, security'
		});
	</script>
	<script type="text/javascript" src="<c:url value="/resources/js/jquery.mask.min.js"/>"></script>
	<script>
		$(document).ready(function() {
			$('#idNoOfEvents').mask('000000000', {
				placeholder : "e.g. 100"
			});
			$('#idNoOfUsers').mask('000', {
				placeholder : "e.g. 15"
			});
			$('#idTelPhone').mask('+00 00000000000', {
				placeholder : "e.g. +60 122735465"
			});
		});

		$(document).delegate('.delivery_add', 'keyup', function() {
			var $rows = $('.role-bottom-ul li');
			var val = $.trim($(this).val()).replace(/ +/g, ' ').toLowerCase();
			$rows.show().filter(function() {
				var text = $(this).text().replace(/\s+/g, ' ').toLowerCase();
				return !~text.indexOf(val);
			}).hide();
		});

		$(document).delegate('.role-bottom-ul li [type="radio"]', 'click', function() {
			var dataAddress = '<div class="planSnip"><div class="col-md-10">';
			dataAddress += $(this).closest('li').children('.del-add').html();
			dataAddress += '</div><div class="col-md-2 align-right">';
			dataAddress += '<a class="pull-right" title="" data-placement="top" id="deletePlan" data-original-title="Delete Delivery Address" style=" font-size: 18px; line-height: 1; padding: 0; color: #7f7f7f;"><i class="fa fa-times-circle"></i></a>';
			dataAddress += '</div></div>';
			$('.phisicalArressBlock').html(dataAddress);
			$('.phisicalArressBlock').show();
			$('#deletePlan').show();
			$.uniform.update();
			vertiCaleALignDeleteAddress();
			$("#sub-credit").slideUp();
		});
		vertiCaleALignDeleteAddress();
		$(document).delegate('#deletePlan', 'click', function() {
			$('.phisicalArressBlock').html('');
			$('.phisicalArressBlock').hide();
			$('.role-bottom-ul li [type="radio"]').prop('checked', false);
			$('#deletePlan').hide();
			$.uniform.update();
			$("#sub-credit").slideDown();
		});

		function vertiCaleALignDeleteAddress() {
			var marginTop = ($('.phisicalArressBlock').height() - $('#deletePlan').height()) / 2;
			$('#deletePlan').css('margin-top', marginTop);
		}

		//$("#sub-credit").slideToggle();

		$("#sub-credit").slideDown();

		$(document).on("keyup", ".delivery_add", function() {
			if ($(this).val() != "") {
				$('.delivery_add_btn').removeClass('btn-black disabled').addClass('btn-blue');
			} else {
				$('.delivery_add_btn').removeClass('btn-blue').addClass('btn-black disabled');
			}
		});

		$(document).on("click", ".delivery_add_btn", function() {
			if ($(this).hasClass('btn-blue')) {
				var txt = $(".delivery_add").val();

				var clo = '<li><div class="radio-primary"><label><input type="radio" id="inlineRadio110" name="example-radio1" ></label></div>  <div class="del-add"><span class="desc">' + txt + '</span><div class="li-links"><a href="#">Edit</a><a class="remove_del" href="javascript:void(0);">Remove</a></div></div></li>';
				$('.role-bottom-ul').prepend(clo);

				$('.role-bottom-ul').find("[type=radio]").uniform();
				$('.role-bottom-ul li:first').find(".radio span").append('<i class="glyph-icon icon-circle"></i>');

				$(".delivery_add").val('');
				$('.delivery_add_btn').removeClass('btn-blue');
				$('.delivery_add_btn').addClass('btn-black disabled');
			}
		});

		$(document).on("click", ".remove_del", function() {
			$(this).closest("li").slideUp();
		});
	</script>
	<style>
.del-add h5, .del-add .desc {
	margin-top: -5px;
}

.phisicalArressBlock {
	background-color: #f1f1f1;
}

#deletePlan :hover {
	cursor: pointer;
}
</style>
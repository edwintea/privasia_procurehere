<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<spring:message var="buyerSettingDesk" code="application.buyer.settings" />
<sec:authentication property="principal.isBuyerErpEnable" var="isBuyerErpEnable" />
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css?1"/>">

<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${buyerSettingDesk}] });
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
.ph_btn_small{
    height: 35px;
    min-width: 100px;
    line-height: 35px;
}
.remove-btn{
    background: #0095d5 none repeat scroll 0 0!important;
    color: #fff;
}
</style>

<div id="page-content-wrapper">
<div id="page-content">
 <div class="container">
	<div class="col-md-12">
		<!-- pageging  block -->
		<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
		<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp"></jsp:include>
		<ol class="breadcrumb" id="idBreadcrumb">
			<li><a id="dashboardLink" href='<c:url value="/buyer/buyerDashboard"/>'> <spring:message code="application.dashboard" />
			</a></li>
			<%-- <c:url value="/buyer/listBuyerSettings" var="listUrl"  />
					<li><a href="${listUrl }"><spring:message code="buyersettings.list"/></a></li> --%>
			<li class="active">
				<%-- <c:out  value='${btnValue}'/>  --%> <spring:message code="label.buyersettings" />
			</li>
			
			<li id="idGeneral">
				<spring:message code="buyerSettings.general.settings" />
			</li>
			
			<li id="idEvent">
				<spring:message code="buyerSettings.event.settings" />
			</li>
		</ol>
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap manage_icon">
				<spring:message code="buyersettings.administration" />
			</h2>
		</div>
		<div class="clear"></div>
		<div class="tab-main">
				<ul class="nav-responsive nav nav-tabs">
					<li class="active">
						<a class="" id="switchToGeneralSettingtId"><spring:message code="buyerSettings.general.settings"/></a>
					</li>
					<li class="">
						<a class="" id="switchToEventSettingsId"><spring:message code="buyerSettings.event.settings"/></a>
					</li>
				</ul>
		</div>
		
	<div id="idGeneralSettings" class="col-md-12 pad0">
		<div class="Invited-Supplier-List import-supplier white-bg">
			<div class="meeting2-heading">
				<h3>
					<spring:message code="label.buyersettings" />
				</h3>
			</div>
			<div class="import-supplier-inner-first-new pad_all_15 global-list">
				<form:form id="buyerSettingsForm" data-parsley-validate="" cssClass="form-horizontal bordered-row" commandName="buyerSettings" method="post" action="buyerSettings">
					<header class="form_header"> </header>
<%-- 					<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" /> --%>
					<form:hidden path="id" />
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="timeZone" for="idTimeZone" class="marg-top-10">
								<spring:message code="label.timezone" />
							</form:label>
						</div>
						<div class="col-md-5">
							<spring:message code="timezone.required" var="required" />
							<form:select path="timeZone" id="idTimeZone" cssClass="chosen-select" data-validation="required" data-validation-error-msg-required="${required}">
								<form:option value="">
									<spring:message code="buyersettings.select.timeZone" />
								</form:option>
								<c:forEach items="${timeZone}" var="tz">
									<c:if test="${empty tz.id}">
										<option value="" disabled>${tz.timeZoneDescription}</option>
									</c:if>
									<c:if test="${!empty tz.id}">
										<option value="${tz.id}">${tz.fullTimeZone}</option>
								    </c:if>
								    <c:if test="${!empty tz.id and tz.id == buyerSettings.timeZone.id}">
										<option value="${tz.id}" selected>${tz.fullTimeZone}</option>
								  </c:if>
							 </c:forEach>	
							</form:select>
							<form:errors path="timeZone" cssClass="error" />
						</div>
					</div>
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="currency" cssClass="marg-top-10">
								<spring:message code="label.currency" />
							</form:label>
						</div>
						<div class="col-md-5">
							<spring:message code="buyersetting.currency.required" var="required" />
							<form:select path="currency" data-validation="required" data-validation-error-msg-required="${required}" cssClass="form-control chosen-select" id="idCurrency">
								<form:option value="">
									<spring:message code="currency.select" />
								</form:option>
<%-- 								<form:options items="${currency}" itemValue="id" itemLabel="currencyName" /> --%>
			
								<c:forEach items="${currency}" var="cr">
									<c:if test="${empty cr.id}">
										<option value="" disabled>${cr.currencyName}</option>
									</c:if>
									<c:if test="${!empty cr.id}">
										<option value="${cr.id}">${cr.currencyName}</option>
								    </c:if>
								    <c:if test="${!empty cr.id and cr.id == buyerSettings.currency.id}">
										<option value="${cr.id}" selected>${cr.currencyName}</option>
								  </c:if>
							    </c:forEach>


							</form:select>
						</div>
					</div>
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="decimal" cssClass="marg-top-10">
								<spring:message code="buyersettings.decimal" />
							</form:label>
						</div>
						<div class="col-md-5">
							<spring:message code="buyersetting.decimal.required" var="required" />
							<form:select path="decimal" data-validation="required" data-validation-error-msg-required="${required}" cssClass="form-control chosen-select" id="iddecimal">
								<form:option value="">
									<spring:message code="buyersettings.selectdacimal" />
								</form:option>
								<form:option value="1"></form:option>
								<form:option value="2"></form:option>
								<form:option value="3"></form:option>
								<form:option value="4"></form:option>
							</form:select>
						</div>
					</div>
 
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="decimal" cssClass="marg-top-10">
								<spring:message code="buyer.setting.erp.notification" />
							</form:label>
						</div>
						<div class="col-md-5  ${isBuyerErpEnable ? '' : 'disabled'} ">

							<form:input type="text" path="erpNotificationEmails" maxlength="500" class="form-control " data-role="tagsinput" />
						</div>
					</div>

					
					<div class="row marg-bottom-20">
						<div class="col-md-3 ">
							<form:label path="decimal" cssClass="marg-top-10">
								<spring:message code="buyer.setting.unit.cost.label" />
							</form:label>
						</div>
						<div class="col-md-5" style="margin-top:12px;">
							<form:checkbox id="enableUnitAndCostCorId" path="enableUnitAndCostCorrelation" class="custom-checkbox marg-top-10"  />
							<input type="hidden" id="idExistingCorrelationValue" name="existingCorrelationValue" value="${buyerSettings.enableUnitAndCostCorrelation}"  />
						</div>
					</div>
					<div class="row marg-bottom-20">
						<div class="col-md-3 ">
							<form:label path="enableUnitAndGrpCodeCorrelation" cssClass="marg-top-10">
								<spring:message code="buyer.setting.unit.grp.code" />
							</form:label>
						</div>
						<div class="col-md-5" style="margin-top:12px;">
							<form:checkbox id="idEnableGrpCodeAndUnit" path="enableUnitAndGrpCodeCorrelation" class="custom-checkbox marg-top-10"  />
						</div>
					</div>
					
					<c:if test="${not empty enableEventPublishing and enableEventPublishing}">
						<div class="row marg-bottom-20">
							<div class="col-md-3">
								<form:label path="decimal" cssClass="marg-top-10">
								RFI Publish URL
							</form:label>
							</div>
							<div class="col-md-5">
								<form:input type="text" path="rfiPublishUrl" maxlength="1000" class="form-control " />
							</div>
						</div>
						<div class="row marg-bottom-20">
							<div class="col-md-3">
								<form:label path="decimal" cssClass="marg-top-10">
								RFQ Publish URL
							</form:label>
							</div>
							<div class="col-md-5">
								<form:input type="text" path="rfqPublishUrl" maxlength="1000" class="form-control " />
							</div>
						</div>
						<div class="row marg-bottom-20">
							<div class="col-md-3">
								<form:label path="decimal" cssClass="marg-top-10">
								RFT Publish URL
							</form:label>
							</div>
							<div class="col-md-5">
								<form:input type="text" path="rftPublishUrl" maxlength="1000" class="form-control " />
							</div>
						</div>
						<div class="row marg-bottom-20">
							<div class="col-md-3">
								<form:label path="decimal" cssClass="marg-top-10">
								RFI Update Publish URL
							</form:label>
							</div>
							<div class="col-md-5">
								<form:input type="text" path="rfiUpdatePublishUrl" maxlength="1000" class="form-control " />
							</div>
						</div>
						<div class="row marg-bottom-20">
							<div class="col-md-3">
								<form:label path="decimal" cssClass="marg-top-10">
								RFQ Update Publish URL
							</form:label>
							</div>
							<div class="col-md-5">
								<form:input type="text" path="rfqUpdatePublishUrl" maxlength="1000" class="form-control " />
							</div>
						</div>
						<div class="row marg-bottom-20">
							<div class="col-md-3">
								<form:label path="decimal" cssClass="marg-top-10">
								RFT Update Publish URL
							</form:label>
							</div>
							<div class="col-md-5">
								<form:input type="text" path="rftUpdatePublishUrl" maxlength="1000" class="form-control " />
							</div>
						</div>
					</c:if>
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="decimal" cssClass="marg-top-10">
								<spring:message code="buyer.profile.secret.key" />
							</form:label>
						</div>
						<div class="col-md-5">

							<form:input type="password" path="stripeSecretKey" maxlength="500" class="form-control" autocomplete="off"/>
						</div>
					</div>
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="decimal" cssClass="marg-top-10">
								<spring:message code="buyer.profile.publish.key" />
							</form:label>
						</div>
						<div class="col-md-5">
							<form:input type="text" path="stripePublishKey" maxlength="500" class="form-control" autocomplete="off"/>
						</div>
					</div>
					<div class="row marg-bottom-202">
						<div class="col-md-3">
							<label class="marg-top-10"></label>
						</div>
						<div class="col-md-9 dd sky mar_b_15">
							<spring:message code="application.update" var="updateLabel" />
							<spring:message code="application.save" var="saveLabel" />
							<form:button type="button" id="saveBuyerSettings" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out ">${not empty buyerSettings.id ? updateLabel : saveLabel }</form:button>
							<c:url value="/buyer/buyerDashboard" var="buyerDashboard" />
							<a href="${buyerDashboard}" class="btn btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium button-previous"> <spring:message code="application.cancel" />
							</a>
						</div>
					</div>
				</form:form>
			</div>
		</div>


		<sec:authorize access="(hasRole('BUYER') and hasRole('ADMIN'))">
			<c:if test="${ buyerSettings.id != null }">
				<div class="Invited-Supplier-List import-supplier white-bg" style="margin-top: 10px;">
					<div class="meeting2-heading">

						<h3>
							<spring:message code="buyer.backup.close.account" />
						</h3>


					</div>
					<div class="import-supplier-inner-first-new pad_all_15 global-list">

						<c:if test="${! buyerSettings.isClose and !buyerSettings.isBackup}">
							<div class="row marg-bottom-202">
								<div class="col-md-12">
									<div class="row">
										<div class="col-md-12">
											<button id="exportAccount" type="button" class="btn btn-info ph_btn_midium  " style="margin: 10px;">
												<spring:message code="suppliersetting.export.data" />
											</button>
										</div>
									</div>
									<div class="row">
										<div class="col-md-12">
											<label class="marg-top-10"> <b><spring:message code="application.note" />:</b> <spring:message code="buyer.setting.note1" />
											</label>
										</div>
									</div>
								</div>
								<div class="col-md-9 dd sky mar_b_15"></div>
							</div>
						</c:if>

						<c:if test="${buyerSettings.isBackup and buyerSettings.exportURL == null and !buyerSettings.isClose  }">
							<div class="row marg-bottom-202">
								<div class="col-md-12">
									<label class="marg-top-10"> <spring:message code="buyer.setting.note2" />
									</label>
								</div>
							</div>
						</c:if>


						<c:if test="${! buyerSettings.isClose}">
							<div class="row marg-bottom-202">
								<div class="col-md-12">
									<div class="row">
										<div class="col-md-12">
											<button id="closeAccount" type="button" class="btn btn-danger ph_btn_midium  " style="margin: 10px;">
												<spring:message code="suppliersetting.export.data.close" />
											</button>
										</div>
									</div>
									<div class="row">
										<div class="col-md-12">
											<label class="marg-top-10"> <b><spring:message code="application.note" />:</b> <spring:message code="buyer.setting.note1" />
											</label>
										</div>
									</div>
								</div>
								<div class="col-md-9 dd sky mar_b_15"></div>
							</div>
						</c:if>

						<c:if test="${ buyerSettings.isClose}">


							<div class="row marg-bottom-202">
								<div class="col-md-12">
									<label class="marg-top-10"> <fmt:formatDate var="closeRequestDate" value="${ buyerSettings.closeRequestDate}" pattern="yyyy-MM-dd HH:mm:ss" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" /> </b> <spring:message code="suppliersetting.user" /> <b>${ buyerSettings.requestedBy.name}</b> </b> <spring:message code="suppliersetting.has.requested.close" /> ${ closeRequestDate}

									</label>
								</div>
							</div>


							<div class="row marg-bottom-202">
								<div class="col-md-3">
									<label class="marg-top-10"><spring:message code="buyer.setting.to.cancel" /> </label>
								</div>
								<div class="col-md-9 dd sky mar_b_15">
									<button id="cancalRequest" type="button" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out ">
										<spring:message code="rfs.summary.cancel.request" />
									</button>
								</div>
							</div>

						</c:if>


						<c:if test="${ buyerSettings.exportURL != null}">
							<div class="row marg-bottom-202">
								<div class="col-md-3">
									<label class="marg-top-10"> <spring:message code="buyer.setting.link.download" />
									</label>
								</div>
								<div class="col-md-7 dd sky mar_b_15">
									<div class="input-group">
										<input type="text" id="link" class="form-control disabled" readonly="true" value="${ buyerSettings.exportURL}">
										<div class="input-group-btn">
											<button id="copyKey" class="btn btn-default" type="button" data-placement="bottom" title="Copy">
												<i class="glyphicon  glyphicon-copy"></i>
											</button>
										</div>
										<div class="col-sm-1 col-md-1 col-xs-3 col-xs-3">
											<a href="${ buyerSettings.exportURL}" class="btn btn-success" id="generateKey"><span class="glyphicon glyphicon-download-alt"></span></a>
										</div>
									</div>

								</div>
							</div>
						</c:if>



						</div>
					</div>
				</c:if>
			</sec:authorize>
		</div>	
				  
		  <div id="eventSettingsViewId" style="display: none;">
				<jsp:include page="eventAndPoSettings.jsp"></jsp:include>
		  </div>
				  
		</div>
 	 </div>	
   </div>
</div>

<div class="modal fade" id="colseAccountPopup" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<form:form method="post" action="${pageContext.request.contextPath}/buyer/closeAccount">
			<div class="modal-content" style="width: 100%; float: left;">
				<div class="modal-header">https://test.procurehere.com/buyer/declaration
					<label style="font-size: 16px;">
						<spring:message code="buyer.setting.confirm.close.account" />
					</label>
					<button class="close for-absulate" type="button" data-dismiss="modal">x</button>
				</div>
				<div class="modal-body">
					<label style="font-size: 14px;"><spring:message code="buyer.setting.sure.close" /></label>
				</div>
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1 ">
					<button type="submit" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left" style="margin-left: 1%;">
						<spring:message code="buyer.setting.close.account" />
					</button>
					<button type="button" class="btn btn-black ph_btn_small pull-right"  data-dismiss="modal">
						<spring:message code="application.cancel" />
					</button>

				</div>
			</div>
		</</form:form>
	</div>
</div>


<div class="modal fade" id="exportAccountPopup" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<form:form method="post" action="${pageContext.request.contextPath}/buyer/exportAccount">
			<div class="modal-content" style="width: 100%; float: left;">
				<div class="modal-header">
					<label style="font-size: 16px;"><spring:message code="buyer.setting.confirm.export.account" /></label>
					<button class="close for-absulate" type="button" data-dismiss="modal">x</button>
				</div>
				<div class="modal-body">
					<label style="font-size: 14px;"> <spring:message code="buyer.setting.sure.to.export" />
					</label>
				</div>
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1 ">
					<button type="submit" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" style="margin-right: 57%; margin-left: 1%;">
						<spring:message code="buyer.setting.export" />
					</button>
					<button type="button" class="btn btn-black ph_btn_small" data-dismiss="modal">
						<spring:message code="application.cancel" />
					</button>

				</div>
			</div>
		</</form:form>
	</div>
</div>



<div class="modal fade" id="cancelRequstPopup" role="dialog">
<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<form:form method="post" action="${pageContext.request.contextPath}/buyer/closeAccount">
			<div class="modal-content" style="width: 110%; float: left;">
				<div class="modal-header">
					<label style="font-size: 16px;"><spring:message code="buyer.setting.confirm.cancel.request" /></label>
					<button class="close for-absulate" type="button" data-dismiss="modal">x</button>
				</div>
				<div class="modal-body">
					<label style="font-size: 14px;"> <spring:message code="buyer.setting.sure.cancel.request" />
					</label>
				</div>

					<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1 ">
					<button type="submit" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out " style="margin-right: 49%; margin-left: 1%;">
						<spring:message code="rfs.summary.cancel.request" />
					</button>
					<button type="button" class="btn btn-black ph_btn_small " data-dismiss="modal">
						<spring:message code="application.button.closed" />
					</button>

				</div>
			</div>
		</</form:form>
	</div>
</div>

<div class="modal fade" id="removeAssignCostCenter" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="buyer.setting.remove.assign.cost" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">×</button>
			</div>
			<div class="modal-body">
				<label> <spring:message code="buyer.setting.remove.confirm.assign" />
				</label>
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
			  <button type="button" id="removeId" class="remove-btn btn btn-info ph_btn_small pull-left hvr-pop hvr-rectangle-out">
				<spring:message code="application.yes" />
			  </button>
			  <button type="button" class="btn btn-black pull-right btn-default ph_btn_small hvr-pop hvr-rectangle-out1" data-dismiss="modal">
				<spring:message code="application.no" />
			  </button>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.js"/>"></script>
<script>
<c:if test="${buyerReadOnlyAdmin}">
$(window).bind('load', function() {
	var allowedFields = '.button-previous, #dashboardLink, .tab-main, #eventSettingsViewId, #switchToEventSettingsId, #switchToGeneralSettingtId';
	//var disableAnchers = ['#reloadMsg'];        
	disableFormFields(allowedFields);
 	$('#page-content').find('select').not(allowedFields).parent('div').addClass('disabled');
});
</c:if>
	$.validate({
		lang : 'en'
	});
</script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.mask.min.js"/>"></script>
<script>
	var view='${param.view}';
	var page='${page}';
	
	$(document).ready(function() {
		$("#erpNotificationEmails").tagsinput('items');
		$('#idTimeZone').chosen({ search_contains: true });
		$('#idtimeZone').mask('GMT+00:00', {
			placeholder : "<spring:message code="timezone.placeholder"/>"
		});
		
		$('#erpNotificationEmails').keypress(function(e) {
			if (e.which == 13) {

				e.preventDefault();
				
				return false; //<---- Add this line
			}
		});
		$('#buyerSettingsForm .bootstrap-tagsinput').on('keyup keypress', function(e) {
			console.log(e.keyCode + " " + e.which);
			var keyCode = e.keyCode || e.which;
			if (keyCode === 13 ) {
				e.preventDefault();
				return false;
			}
		});

		if(view == 'event'){
			$('#switchToEventSettingsId').parent().addClass('active');
		    $("#switchToGeneralSettingtId").parent().removeClass('active');
		    $("#idGeneralSettings").hide();
		    $("#eventSettingsViewId").show();
		    
		    $("#idGeneral").addClass('hide');
		    $("#idEvent").removeClass('hide');

		}else {
// 			alert("view"+view);
			$('#switchToGeneralSettingtId').parent().addClass('active');
		    $("#switchToEventSettingsId").parent().removeClass('active');
		    $("#idGeneralSettings").show();
		    $("#eventSettingsViewId").hide();
		    
		    $("#idGeneral").removeClass('hide');
		    $("#idEvent").addClass('hide');
		}
	
		
	});
	
	
	$(document).delegate('#closeAccount', 'click', function(e) {
		$('#colseAccountPopup').modal();
	});
	
	
	$(document).delegate('#exportAccount', 'click', function(e) {
		$('#exportAccountPopup').modal();
	});
	
	
	$(document).delegate('#cancalRequest', 'click', function(e) {
		$('#cancelRequstPopup').modal();
	});
	

	$( '#copyKey' ).click( function() {
	     var clipboardText = "";
	     clipboardText = $( '#link' ).val();
	     copyToClipboard( clipboardText );
	 });
	
	function copyToClipboard(text) {
	   var textArea = document.createElement( "textarea" );
	   textArea.value = text;
	   document.body.appendChild( textArea );

	   textArea.select();

	   try {
	      var successful = document.execCommand( 'copy' );
	      var msg = successful ? 'successful' : 'unsuccessful';
	      console.log('Copying text command was ' + msg);
	   } catch (err) {
	      console.log('Oops, unable to copy');
	   }

	   document.body.removeChild( textArea );
	}
	
	$(document).on("keyup", "#idTimeZone_chosen .chosen-search input", keyDebounceDelay(function(e) {
		// ignore arrow keys
		switch (e.keyCode) {
		case 17: // CTRL
			return false;
			break;
		case 18: // ALT
			return false;
			break;
		case 37:
			return false;
			break;
		case 38:
			return false;
			break;
		case 39:
			return false;
			break;
		case 40:
			return false;
			break;
		}
		var timeZone = $.trim(this.value);
		if (timeZone.length > 2 || timeZone.length == 0 || e.keyCode == 8) {
			reloadTimeZoneList();
		}
	}, 650));
	
	
	function reloadTimeZoneList() {
		var timeZone = $.trim($('#idTimeZone_chosen .chosen-search input').val());
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		$.ajax({
			url : getContextPath() + '/buyer/searchTimeZoneFromList',
			data : {
				'timeZone' : timeZone,
			},
			type : 'POST',
			dataType : 'json',
			beforeSend : function(xhr) {
				xhr.setRequestHeader(header, token);
				$('#loading').show();
			},
			success : function(data) {
				var html = '';
				if (data != '' && data != null && data.length > 0) {
					$('#idTimeZone').find('option:not(:first)').remove();
					$.each(data, function(key, value) {
						if (value.id == null || value.id == '') {
							html += '<option value="" disabled>' + value.timeZoneDescription + '</option>';
						}else{
							html += '<option value="' + value.id + '">' + value.fullTimeZone + '</option>';
						}
					});
				}
				$('#idTimeZone').append(html);
				$("#idTimeZone").trigger("chosen:updated");
				$('#idTimeZone_chosen .chosen-search input').val(timeZone);
				$('#loading').hide();
			},
			error : function(error) {
				console.log(error);
			}
		});
	}
	
	function keyDebounceDelay(callback, ms) {
		var timer = 0;
		return function() {
			var context = this, args = arguments;
			clearTimeout(timer);
			timer = setTimeout(function() {
				callback.apply(context, args);
			}, ms || 0);
		};
	}

	$(document).on("keyup", "#idCurrency_chosen .chosen-search input", keyDebounceDelay(function(e) {
		// ignore arrow keys
		switch (e.keyCode) {
		case 17: // CTRL
			return false;
			break;
		case 18: // ALT
			return false;
			break;
		case 37:
			return false;
			break;
		case 38:
			return false;
			break;
		case 39:
			return false;
			break;
		case 40:
			return false;
			break;
		}
		var currencyNane = $.trim(this.value);
		if (currencyNane.length > 2 || currencyNane.length == 0 || e.keyCode == 8) {
			reloadCurrencyList();
		}
	}, 650));
	
	$(document).delegate('#saveBuyerSettings', 'click', function(e) {
		if($("#buyerSettingsForm").isValid()) {
			if($('#enableUnitAndCostCorId').prop('checked') == false) {
				<c:if test="${assignCostCenter}">
					if($('#idExistingCorrelationValue').val() == 'true' ) {
						// If existing is checked and the user is trying to uncheck it, only then show the confirm dialog.
						// else proceed normally
						$('#removeAssignCostCenter').modal();
					} else {
						saveBuyerSetting();
					}
				</c:if>
				<c:if test="${!assignCostCenter}">
				saveBuyerSetting();
				</c:if>
			}else{
				saveBuyerSetting();
			}
		}
	});
	
	$(document).delegate('#removeId', 'click', function(e) {
		$("#buyerSettingsForm").submit();
	});
	
	function reloadCurrencyList() {
		var currencyName = $.trim($('#idCurrency_chosen .chosen-search input').val());
		var header = $("meta[name='_csrf_header']").attr("content");
		var token = $("meta[name='_csrf']").attr("content");
		$.ajax({
			url : getContextPath() + '/buyer/searchCurrencyFromList',
			data : {
				'currencyName' : currencyName,
			},
			type : 'POST',
			dataType : 'json',
			beforeSend : function(xhr) {
				xhr.setRequestHeader(header, token);
				$('#loading').show();
			},
			success : function(data) {
				var html = '';
				if (data != '' && data != null && data.length > 0) {
					$('#idCurrency').find('option:not(:first)').remove();
					$.each(data, function(key, value) {
						if (value.id == null || value.id == '') {
							html += '<option value="" disabled>' + value.currencyName + '</option>';
						}else{
							html += '<option value="' + value.id + '">' + value.currencyName + '</option>';
						}
					});
				}
				$('#idCurrency').append(html);
				$("#idCurrency").trigger("chosen:updated");
				$('#idCurrency_chosen .chosen-search input').val(currencyName);
				$('#loading').hide();
			},
			error : function(error) {
				console.log(error);
			}
		});
	}
	
	function saveBuyerSetting(){
		if($("#buyerSettingsForm").isValid()) {
 			//$('#buyerSettingsForm').attr('action', getContextPath() + '/buyer/buyerSettings');
			$("#buyerSettingsForm").submit();
		}else{
			return;
		}
	}
	
	$("#enableUnitAndCostCorId").change(function() {
		if(this.checked) {
			var header = $("meta[name='_csrf_header']").attr("content");
			var token = $("meta[name='_csrf']").attr("content");
			$.ajax({
				url : getContextPath() + '/buyer/checkForAssignedCCForBU',
				data : {
					
				},
				type : 'POST',
				dataType : 'json',
				beforeSend : function(xhr) {
					xhr.setRequestHeader(header, token);
					$('#loading').show();
				},
				success : function(data) {
					var present = data;
					if(present == false){
						console.log("Success if "+present);
						$('p[id=idGlobalErrorMessage]').html('Some Business Units do not have Cost Centers.');
						$('div[id=idGlobalError]').show();
						$('div[id=idGlobalSuccess]').hide();
					}
				},
				error : function(error) {
					console.log("Error ..."+error);
				},
				complete : function(){
					$('#loading').hide();
				}
			});
		}
	});
	
</script>
<script type="text/javascript" src="<c:url value="/resources/js/buyer-settings-bootstrap-tagsinput.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/bootstrap-tagsinput.css"/>">
<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/view/buyerSettings.js"/>"></script>

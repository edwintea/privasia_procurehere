<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
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
<spring:message var="supplierSettingsDesk" code="application.supplier.settings" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${supplierSettingsDesk}] });
});
</script>
<div id="page-content" view-name="supplierSettings">
	<div class="col-md-offset-1 col-md-10">
		<ol class="breadcrumb">
			<li><a href="${pageContext.request.contextPath}/supplier/supplierDashboard"> <spring:message code="application.dashboard" />
			</a></li>
			<li class="active"><spring:message code="label.suppliersettings" /></li>
		</ol>
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap manage_icon">
				<spring:message code="suppliersettings.administration" />
			</h2>
		</div><jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
		<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
		<div class="Invited-Supplier-List import-supplier white-bg">
			<div class="meeting2-heading">
				<h3>
					<spring:message code="label.suppliersettings" />
				</h3>
			</div>
			<div class="import-supplier-inner-first-new pad_all_15 global-list">
				<form:form id="supplierSettingsForm" data-parsley-validate="" cssClass="form-horizontal bordered-row" modelAttribute="supplierSettings" method="post" action="supplierSettings?${_csrf.parameterName}=${_csrf.token}" enctype="multipart/form-data">
					<header class="form_header"> </header>
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
								<form:option value=" ">
									<spring:message code="buyersettings.selecttimezone" />
								</form:option>
								<form:options items="${timeZone}" itemValue="id"></form:options>
							</form:select>
							<form:errors path="timeZone" cssClass="error" />
						</div>
					</div>
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<label Class="marg-top-10"> <spring:message code="systemsetting.bu.logo" /></label>
						</div>
						<div class="col-md-5">
							<div class="profile">
								<c:if test="${empty logoImg}">
									<img id="logoImageHolder" src="${pageContext.request.contextPath}/resources/images/logo-image.png" alt="Logo" onclick="$('#logoImg').click()" />
									<div class="col-md-8">
										<a href="javascript:" onclick="$('#logoImg').click()"><spring:message code="systemsetting.bu.upload.logo" /></a>
									</div>
									<div class="col-md-4">
										<a href="javascript:" id="removeLogo"><spring:message code="systemsetting.bu.remove.logo" /></a>
									</div>
								</c:if>
								<c:if test="${not empty logoImg}">
									<img id="logoImageHolder" src="data:image/jpeg;base64,${logoImg}" alt="Logo" onclick="$('#logoImg').click()" />
									<div class="col-md-8">
										<a href="javascript:" onclick="$('#logoImg').click()"><spring:message code="systemsetting.bu.upload.logo" /></a>
									</div>
									<div class="col-md-4">
										<a href="javascript:" id="removeLogo"><spring:message code="systemsetting.bu.remove.logo" /></a>
									</div>
								</c:if>
								<form:input type="file" accept="image/*" style="visibility: hidden" name="logoImg" id="logoImg" path="" />
								<input type="hidden" id="removeFile" name="removeFile" value="false">
							</div>

						</div>
					</div>





					<div class="row marg-bottom-202">
						<div class="col-md-3">
							<label class="marg-top-10"></label>
						</div>
						<div class="col-md-9 dd sky mar_b_15">
							<form:button type="submit" id="saveSupplierSettings" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out ">
								<spring:message code="application.update" />
							</form:button>
							<c:url value="/supplier/supplierDashboard" var="supplierDashboard" />
							<a href="${supplierDashboard}" class="btn btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium button-previous"> <spring:message code="application.cancel" />
							</a>
						</div>
					</div>
				</form:form>
			</div>
		</div>





		<div class="Invited-Supplier-List import-supplier white-bg" style="margin-top: 10px">
			<div class="meeting2-heading">
				<h3>
					<spring:message code="supplier.setting.po.sharing" />
				</h3>
			</div>
			<div class="import-supplier-inner-first-new pad_all_15 global-list">

				<div class="row marg-bottom-202">
					<div class="col-md-4 dd sky mar_b_15">
						<input type="radio" name="settingRadio" ${supplierSettings.poShare eq 'NONE' ? 'checked' : '' } value="none"> <label class="marg-top-10"><spring:message code="supplier.setting.none" /> </label>
					</div>
					<div class="col-md-4 dd sky mar_b_15">
						<input type="radio" name="settingRadio" ${supplierSettings.poShare eq 'ALL' ? 'checked' : '' } value="all"> <label class="marg-top-10"><spring:message code="suppliersetting.share.all.po" /> </label>
					</div>

					<div class="col-md-4 dd sky mar_b_15">
						<input type="radio" name="settingRadio" ${supplierSettings.poShare eq 'BUYER' ? 'checked' : '' } value="selective"> <label class="marg-top-10"><spring:message code="suppliersetting.share.po.buyer" /> </label>
					</div>



				</div>


			</div>
		</div>


		<div class="Invited-Supplier-List import-supplier white-bg all box" style="margin-top: 10px">
			<div class="meeting2-heading">
				<h3>
					<spring:message code="suppliersetting.finance.company.share" />
				</h3>
			</div>

			<div class="import-supplier-inner-first-new pad_all_15 global-list">
				<form:form data-parsley-validate="" cssClass="form-horizontal bordered-row" modelAttribute="supplierSettings" method="post" action="supplierAllPoSettings">
					<header class="form_header"> </header>
					<form:hidden path="id" />





					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<label> </label>

						</div>
						<div class="col-md-5">

							<form:select path="financeCompany" id="financeCompany" cssClass="chosen-select" data-validation="required" data-validation-error-msg-required="No Finance Company is selected">
								<form:option value=" ">
									<spring:message code="suppliersetting.finance.company" />
								</form:option>
								<form:options items="${financeCompanies}" itemValue="id"></form:options>
							</form:select>

						</div>
					</div>


					<div class="row marg-bottom-202">
						<div class="col-md-3">
							<label class="marg-top-10"></label>
						</div>
						<div class="col-md-9 dd sky mar_b_15">
							<form:button type="submit" id="saveSupplierSettings" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out ">
								<spring:message code="application.save" />
							</form:button>
							<c:url value="/supplier/supplierDashboard" var="supplierDashboard" />
							<a href="${supplierDashboard}" class="btn btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium button-previous"> <spring:message code="application.cancel" />
							</a>
						</div>
					</div>
				</form:form>
			</div>
		</div>




		<div class="Invited-Supplier-List import-supplier white-bg selective box" style="margin-top: 10px">
			<div class="meeting2-heading">
				<h3>
					<spring:message code="suppliersetting.buyer.po.sharing" />
				</h3>
			</div>
			<div class="import-supplier-inner-first-new pad_all_15 global-list">


				<form:form modelAttribute="poSharingBuyer" method="post" action="${pageContext.request.contextPath}/supplier/saveBuyerForSharing" class="form-horizontal bordered-row">



					<div class="row marg-bottom-20">
						<div class="col-md-2">
							<label><spring:message code="suppliersetting.select.buyer" /></label>
						</div>
						<div class="col-md-3">
							<form:select path="buyer" class="chosen-select" id="buyer" data-validation="required" data-validation-error-msg-required="No Buyer Company is selected">


								<form:option value=" ">
									<spring:message code="suppliersetting.search.buyer" />
								</form:option>
								<form:options items="${buyers}" itemValue="id"></form:options>

							</form:select>
						</div>
						<div class="col-md-2">
							<label><spring:message code="defaultmenu.finance.company" /></label>


						</div>
						<div class="col-md-3">

							<form:select path="financeCompany" id="financeCompany" class="chosen-select" data-validation-error-msg-required="No Finance Company is selected">
								<form:option value=" ">
									<spring:message code="suppliersetting.finance.company" />
								</form:option>
								<form:options items="${financeCompanies}" itemValue="id"></form:options>
							</form:select>

						</div>


						<div class="col-md-2 col-sm-2">
							<spring:message code="tooltip.add.more" var="addmore" />
							<form:button type="submit" class="btn btn-info hvr-pop hvr-rectangle-out" data-toggle="tooltip" data-placement="top" title="${addmore}" id="addMore">
								<i class="fa fa-plus" aria-hidden="true"></i>
							</form:button>
						</div>
					</div>

				</form:form>

				<div class="row">
					<div class="col-xs-12">
						<div class="ph_tabel_wrapper scrolableTable_UserList">
							<table id="buyerDetails" class="ph_table display table table-bordered noarrow" cellspacing="0" width="100%">
								<thead>
									<tr>
										<th><spring:message code="application.lable.buyer" /></th>
										<th><spring:message code="defaultmenu.finance.company" /></th>
										<th><spring:message code="application.remove" /></th>
									</tr>
								</thead>
								<tbody>
								</tbody>
							</table>
						</div>
						<div id="morris-bar-yearly" class="graph" style="visibility: hidden"></div>
					</div>
				</div>





			</div>
		</div>


		<sec:authorize access="(hasRole('SUPPLIER') and hasRole('ADMIN'))">
			<c:if test="${ supplierSettings.id != null }">
				<div class="Invited-Supplier-List import-supplier white-bg" style="margin-top: 10px;">
					<div class="meeting2-heading">

						<h3>
							<spring:message code="buyer.backup.close.account" />
						</h3>


					</div>
					<div class="import-supplier-inner-first-new pad_all_15 global-list">
						<c:if test="${! supplierSettings.isClose and !supplierSettings.isBackup }">
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

						<c:if test="${supplierSettings.isBackup and supplierSettings.exportURL == null and !supplierSettings.isClose }">
							<div class="row marg-bottom-202">
								<div class="col-md-12">
									<label class="marg-top-10"> <spring:message code="buyer.setting.note2" />
									</label>
								</div>
							</div>
						</c:if>





						<c:if test="${! supplierSettings.isClose}">

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
											<label class="marg-top-10"> <b><spring:message code="application.note" />:</b> <spring:message code="suppliersetting.note1" /> <br /> <spring:message code="suppliersetting.note2" />

											</label>
										</div>
									</div>
								</div>
								<div class="col-md-9 dd sky mar_b_15"></div>
							</div>
						</c:if>

						<c:if test="${ supplierSettings.isClose}">


							<div class="row marg-bottom-202">
								<div class="col-md-12">
									<label class="marg-top-10"> <fmt:formatDate var="closeRequestDate" value="${ supplierSettings.closeRequestDate}" pattern="yyyy-MM-dd HH:mm:ss" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" /> <spring:message code="suppliersetting.user" /> <b>${ supplierSettings.requestedBy.name}</b> <spring:message code="suppliersetting.has.requested.close" /> ${ closeRequestDate}



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


						<c:if test="${ supplierSettings.exportURL != null}">
							<div class="row marg-bottom-202">
								<div class="col-md-3">
									<label class="marg-top-10"> <spring:message code="buyer.setting.link.download" />
									</label>
								</div>
								<div class="col-md-7 dd sky mar_b_15">
									<div class="input-group">
										<input type="text" id="link" class="form-control disabled" readonly="true" value="${ supplierSettings.exportURL}">
										<div class="input-group-btn">
											<button id="copyKey" class="btn btn-default" type="button" data-toggle="tooltip" data-placement="bottom" title='<spring:message code="tooltip.copy" />'>
												<i class="glyphicon  glyphicon-copy"></i>
											</button>
										</div>
										<div class="col-sm-1 col-md-1 col-xs-3 col-xs-3">
											<a href="${ supplierSettings.exportURL}" class="btn btn-success" id="generateKey"><span class="glyphicon glyphicon-download-alt"></span></a>
										</div>
									</div>

								</div>


							</div>
						</c:if>


					</div>
				</div>
			</c:if>
		</sec:authorize>




		<div class="modal fade" id="colseAccountPopup" role="dialog">
			<div class="modal-dialog for-delete-all reminder">
				<!-- Modal content-->
				<form:form method="post" action="${pageContext.request.contextPath}/supplier/closeAccount">
					<div class="modal-content" style="width: 100%; float: left;">
						<div class="modal-header">
							<label style="font-size: 16px;"> <spring:message code="buyer.setting.confirm.close.account" />
							</label>
							<button class="close for-absulate" type="button" data-dismiss="modal">×</button>
						</div>
						<div class="modal-body">
							<label style="font-size: 14px;"><spring:message code="buyer.setting.sure.close" /></label>
						</div>
						<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1 ">
							<button type="submit" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" style="margin-right: 1%">
								<spring:message code="buyer.setting.close.account" />
							</button>
							<button type="button" class="btn btn-black ph_btn_small" style="margin-left: 48%;" data-dismiss="modal">
								<spring:message code="application.cancel" />
							</button>

						</div>
					</div>
				</form:form>
			</div>
		</div>


		<div class="modal fade" id="exportAccountPopup" role="dialog">
			<div class="modal-dialog for-delete-all reminder">
				<!-- Modal content-->
				<form:form method="post" action="${pageContext.request.contextPath}/supplier/exportAccount">
					<div class="modal-content" style="width: 100%; float: left;">
						<div class="modal-header">
							<label style="font-size: 16px;"> <spring:message code="buyer.setting.confirm.export.account" />
							</label>
							<button class="close for-absulate" type="button" data-dismiss="modal">×</button>
						</div>
						<div class="modal-body">
							<label style="font-size: 14px;"> <spring:message code="buyer.setting.sure.to.export" />
							</label>
						</div>
						<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1 ">
							<button type="submit" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" style="margin-right: 57%; margin-left: 1%;">
								<spring:message code="buyer.setting.export" />
							</button>
							<button type="button" class="btn btn-black ph_btn_small"  data-dismiss="modal">
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
				<form:form method="post" action="${pageContext.request.contextPath}/supplier/closeAccount">
					<div class="modal-content" style="width: 110%; float: left;">
						<div class="modal-header">
							<label style="font-size: 16px;"> <spring:message code="buyer.setting.confirm.cancel.request" />
							</label>
							<button class="close for-absulate" type="button" data-dismiss="modal">×</button>
						</div>
						<div class="modal-body">
							<label style="font-size: 14px;"> <spring:message code="buyer.setting.sure.cancel.request" />
							</label>
						</div>
						<div class="row">
							<div class="col-xs-12">
								<%-- <div class="ph_tabel_wrapper scrolableTable_UserList">
									<table id="buyerDetails" class="ph_table display table table-bordered noarrow" cellspacing="0" width="100%">
										<thead>
											<tr>
												<th><spring:message code="application.lable.buyer" /></th>
												<th><spring:message code="defaultmenu.finance.company" /></th>
												<th><spring:message code="application.remove" /></th>
											</tr>
										</thead>
										<tbody>
										</tbody> 
									</table>
								</div> --%>
								<div id="morris-bar-yearly" class="graph" style="visibility: hidden"></div>
							</div>
								<div class="col-xs-12">
							<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1 ">
								<button type="submit" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out " style="margin-right: 49%; margin-left: 1%;">
									<spring:message code="rfs.summary.cancel.request" />
								</button>
								<button type="button" class="btn btn-black ph_btn_small " data-dismiss="modal">
									<spring:message code="application.button.closed" />
								</button>

							</div>
							</div>
						</div>
					</div>
				</form:form>
			</div>
		</div>




	</div>

	<%-- 				<div class="row" style="margin-top: 10px;">
					<div class="col-md-3">
						<label class="marg-top-10"></label>
					</div>
					<div class="col-md-9 dd sky mar_b_15">
						<c:url value="/supplier/buyerPoShare" var="buyerPoShare" />
						<a href="${buyerPoShare}" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out"> Save </a>

						<c:url value="/supplier/supplierDashboard" var="supplierDashboard" />
						<a href="${supplierDashboard}" class="btn btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium button-previous"> <spring:message code="application.cancel" />
						</a>
					</div>
				</div> --%>




</div>
</div>




</div>




</div>

<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.js"/>"></script>
<script>
	$.validate({
		lang : 'en'
	});
</script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.mask.min.js"/>"></script>




<script type="text/javascript">




$(document).ready(function() {
/* var data = eval('${poSharingBuyers}');
$('#tableList1').DataTable({ "serverSide" : false,
'aaData' : data,
"aoColumns" : [ { "mData" : "id",
"mRender" : function(data, type, row) {

return '';
}
},

}, { "mData" : "supplier.companyName" }, { "mData" : "supplier.communicationEmail" }, { "mData" : "supplier.companyContactNumber" } ]

*/



/* var data = eval('${poSharingBuyers}');
$('#favSupptableList').DataTable({ "serverSide" : false,
'aaData' : data,
"aoColumns" : [ { "mData" : "id",
"mRender" : function(data, type, row) {
console.log(data);
console.log(row);
//if (eventDraft === "true") {
//	return '<a href="" data-id="'+data+'" data-suppId="'+row.supplier.id+'" class="deleteSupplier" data-toggle="tooltip" data-placement="top" title="Delete" ><img src="${pageContext.request.contextPath}/resources/images/delete1.png"></a>';
//} else {
//	return '';
//}
},

}, { "mData" : "supplier.companyName" }, { "mData" : "supplier.communicationEmail" }, { "mData" : "supplier.companyContactNumber" } ] });

*/




});



</script>




<script type="text/javascript">
	$(document).ready(function() {
	
	
	$('#idtimeZone').mask('GMT+00:00', {
			placeholder : "<spring:message code="timezone.placeholder"/>"
		});
	
	var buyerDetails = $('#buyerDetails').DataTable({
		"processing" : true,
		"deferRender" : true,
		"preDrawCallback" : function(settings) {
			$('#loading').show();
			/* console.log("preDrawCallback"); */
			return true;
		},
		"drawCallback" : function() {
			// in case your overlay needs to be put away automatically you can put it here
			$('#loading').hide();
		},
		"serverSide" : true,
		"pageLength" : 10,
		"searching" : true,
		"ajax" : {
			"url" : "${pageContext.request.contextPath}/supplier/buyerDetailsData",
			
			"error" : function(request, textStatus, errorThrown) {
				var error = request.getResponseHeader('error');
				if (error != undefined) {
					$('p[id=idGlobalErrorMessage]').html(error != null ? error.split(",").join("<br/>") : "");
					$('div[id=idGlobalError]').show();
				}
				$('#loading').hide();
			}
		},
		"order" : [],
		"columns" : [ { 	"mData" : "buyerCompanyName"},
			{	"mData" : "financeCompanyName"},
			
			{	
				"mData" : "id",
				"mRender" : function(data, type, row) {
					console.log(data.id)
					/* return '<a href="editRegion?id='+ row.id+ '">Edit</a>/<a  href="deleteRegion?id='+ row.id + '">Delete</a>'; */
					return '<a  href="deleteBuyerForSharing?id='+ row.id + '" data-placement="top" title="Delete" ><img src="${pageContext.request.contextPath}/resources/images/delete1.png"></a>';
			},
			}]
	});

	
	
	
	var aa =  '${supplierSettings.poShare}';
	if(aa == 'ALL'){
	$(".selective").hide();
	}
	
	if(aa == 'NONE'){
		$(".box").hide();
		$(".all").hide();
		}
		
	
	if(aa == 'BUYER'){
		
		$(".all").hide();
		}
		
	
	
    $('input[type="radio"]').click(function(){
        var inputValue = $(this).attr("value");
        var targetBox = $("." + inputValue);
       
        $(".box").not(targetBox).hide();
        $(targetBox).show();
        if(inputValue == 'none'){
        	$.ajax({
    			type : "POST",
    			url : "${pageContext.request.contextPath}/supplier/disablePoShare",
    			beforeSend : function(xhr) {
    				$('#loading').show();
    				
    			},
    			complete : function() {
    				$('#loading').hide();
    			},
    			success : function(data, textStatus, request) {
    				var success = request.getResponseHeader('success');
					showMessage('SUCCESS', success);
					$('#loading').hide();
    				buyerDetails.ajax.reload();
    			},
    			error : function(request, textStatus, errorThrown) {
    				console.log('Error: ' + request.getResponseHeader('error'));
    				var error = request.getResponseHeader('error');
    				showMessage('error', error);
    			}
    		});
        }
        
		});
	});
	

	$(document).delegate('#exportAccount', 'click', function(e) {
			
			$('#exportAccountPopup').modal();
		});
		
	
	$(document).delegate('#closeAccount', 'click', function(e) {
		$('#colseAccountPopup').modal();
	});
	
	$(document).delegate('#cancalRequest', 'click', function(e) {
		$('#cancelRequstPopup').modal();
	});
	
	
	
	$(document).ready(function() {
		$("#image-holder").on('click', '.thumb-image', function() {
			$(this).toggleClass("selectedItem");
		});

		$("#selectedItem").on("click", function() {
			$(".selectedItem").remove();
		});

		$("#logoImg").on('change', function() {
			if (typeof (FileReader) == null) {
				var image_holder = document.getElementById("logoImageHolder").src;
				console.log("=====");
				image_holder.attr('src', getContextPath() + '/resources/images/logo-image.png');
			} else if (typeof (FileReader) != "undefined") {
				$("#removeFile").val(false);
				var image_holder = $("#logoImageHolder");
				image_holder.empty();
				var reader = new FileReader();
				reader.onload = function(e) {
					image_holder.attr('src', e.target.result);
				}
				//console.log("=====");
				image_holder.show();
				reader.readAsDataURL($(this)[0].files[0]);
			} else {
				//alert("This browser does not support FileReader.");
			}
		});

		$("#removeLogo").click(function() {
			$("#logoImg").val("");
			$("#removeFile").val(true);
			$('#logoImageHolder').attr('src', getContextPath() + '/resources/images/logo-image.png')
		});
	});
	
	$.formUtils.addValidator({
		validatorFunction : function(value, $el, config, language, $form) {
			var response = true;
			var header = $("meta[name='_csrf_header']").attr("content");
			var token = $("meta[name='_csrf']").attr("content");
			//alert(loginId);
			$.ajax({
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
	});
	
</script>

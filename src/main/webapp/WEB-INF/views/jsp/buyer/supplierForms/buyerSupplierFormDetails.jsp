<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize access="hasRole('ADMIN')" var="isAdmin" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<div class="white-bg border-all-side float-left width-100 pad_all_15">
	<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
	<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
	<div class="row">
	 	<div class="col-md-6 col-sm-12 col-xs-12">
		    <input type="hidden" id="poId" value="${supplierForm.id}">
		     <div class="tag-line">
				 <h2>
					<spring:message code="application.supplier" />
					: ${supplierForm.supplier.companyName}
				 </h2>
				 <br>
				 <h2>
					<spring:message code="supplier.form.request.date" />
					: <fmt:formatDate value="${supplierForm.requestedDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
				 </h2>
				  <br>
				 <c:if test="${supplierForm.status=='SUBMITTED'}">
				   <h2>
					<spring:message code="supplier.form.submitted.date" />
					: <fmt:formatDate value="${supplierForm.submitedDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
				 </h2>
				  <br>
				  </c:if>
				  <h2>
					<spring:message code="application.description" />
					: ${supplierForm.description}
				 </h2>
		   		</div>
		  </div>
	</div>
	<c:if test="${supplierForm.status=='SUBMITTED' and (eventPermissions.owner or isAdmin) and  (supplierForm.approvalStatus=='APPROVED' or supplierForm.approvalStatus=='REJECTED') }">
		<button id="revisedForm" class="btn ph_btn_small float-right btn-danger hvr-pop marg-left-10" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="tooltip.supplier.form.revised" />'>
			<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-times"></i></span>
			<span class="button-content"><spring:message code="supplier.form.summary.revised" /></span>
		</button>
	</c:if>
	<c:if test="${supplierForm.status=='SUBMITTED' and supplierForm.approvalStatus=='APPROVED' and (eventPermissions.owner or isAdmin)}">
		<button id="acceptForm" class="btn ph_btn_small float-right btn-success hvr-pop marg-left-10" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="tooltip.supplier.form.accept" />'>
			<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-check"></i></span>	
			 <span class="button-content"><spring:message code="supplier.form.summary.accepted" /></span>
		</button>
	</c:if>
</div>
<div class="clear"></div>


<jsp:include page="buyerSupplierFormCqSubmittion.jsp" />
<jsp:include page="supplierFormSubmitionApproval.jsp" />
<jsp:include page="supplierFormSubmitionAdditionalApproval.jsp" />
<jsp:include page="buyerSupplierFormAudit.jsp" />

<div class="row">
	<div class="col-md-12 col-xs-12 col-sm-12 margin-top-10 supplierCqSubmtBtons text-right">
		<a href="${pageContext.request.contextPath}/buyer/importSupplier" class="btn btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium button-previous" id="idClose">
			<spring:message code="application.button.closed" />
		</a>
	</div>
</div>

<!-- Accept Supplier Form -->
<div class="modal fade" id="modal-formAccept" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="supplier.poAccept.confirm.header" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<form id="idAcceptForm"  method="post">
				<input type="hidden" name="formSubId" value="${supplierForm.id}"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<div class="col-md-12">
					<div class="row">
						<div class="modal-body col-md-12">
							<label> <spring:message code="supplier.form.accept.confirm" />
							</label>
						</div>
						<div class="form-group col-md-6">
							<textarea class="width-100" id="idAcceptRemark" placeholder="Enter Comments.(Optional)" rows="3" name="buyerRemark" id="buyerRemark" data-validation="length" data-validation-length="max500"></textarea>
						</div>
					</div>
				</div>
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
					<input type="button" id="idAccept" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left" value="Yes">
					<button type="button" id="idAcceptCancelForm" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
						<spring:message code="application.no2" />
					</button>
				</div>
			</form>
		</div>
	</div>
</div>
<!-- Revised Supplier Form -->
<div class="modal fade" id="modal-formRevised" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="supplier.formRevise.confirm.header" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<form id="idReviseForm"  method="post">
				<input type="hidden" name="formSubId" value="${supplierForm.id}"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<div class="col-md-12">
					<div class="row">
						<div class="modal-body col-md-12">
							<label> <spring:message code="supplier.form.revise.confirm" />
							</label>
						</div>
						<div class="form-group col-md-6">
							<textarea class="width-100" id="idReviseRemark" placeholder="Enter Comments.(Mandatory)" rows="3" name="buyerRemark" id="buyerRemark" data-validation="required length" data-validation-length="max500"></textarea>
						</div>
					</div>
				</div>
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
					<input type="button" id="idRevise" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left" value="Yes">
					<button type="button" id="idReviseCancelForm" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
						<spring:message code="application.no2" />
					</button>
				</div>
			</form>
		</div>
	</div>
</div>

<script type="text/javascript" src="${pageContext.request.contextPath}/resources/assets/widgets/file-input/file-input.js"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/datepicker/datepicker.js"/>"></script>
<style>
.orangecol{
background: none 0px 0px repeat scroll rgb(230, 126, 34) !important;
}
.hideElement {
	width: 0 !important;
	height: 0 !important;
	padding: 0 !important;
	min-width: 0 !important;
	max-width: 0;
	border: 0;
}
.btnEditApproval {
	background: none;
	border: none;
	outline: none;
}
</style>
<script>
	$.validate({
		lang : 'en',
		modules : 'date'
	});
	$('#acceptForm').click(function() {
			$('#modal-formAccept').modal();
	});

	$('#revisedForm').click(function() {
			$('#modal-formRevised').modal();
	});
	
	<c:if test="${supplierForm.status == 'ACCEPTED'  or supplierForm.status == 'PENDING' }">
	$(window).bind('load', function() {
		var allowedFields = '#revisedForm,#acceptForm,#idClose,#idSupplierList,.collAudit,#idReviseForm,#idAcceptForm,#idReviseCancelForm,#idAcceptCancelForm,#idAcceptRemark,#idReviseRemark,.formAttachmentDownload,#idAccept,#idRevise,.buyerAttachDownload, .collAdditionalApprove';
		disableFormFields(allowedFields);
	});
	</c:if>
	
	
	$('document').ready(function() {
		$('#idAccept').on('click', function(e) {
			console.log('Accepted clicked....');
			e.preventDefault();
			if($("#idAcceptForm").isValid()) {
				$(this).addClass('disabled');
	 			$('#revisedForm').addClass('disabled');
				$('#acceptForm').addClass('disabled');
	 			$('#idAcceptForm').attr('action', getContextPath() + '/buyer/acceptSupplierForm');
				$("#idAcceptForm").submit();
			}else{
				return;
			}
		});

		$('#idRevise').on('click', function(e) {
			console.log('Revise clicked....');
			e.preventDefault();
			if($("#idReviseForm").isValid()) {
				$(this).addClass('disabled');
	 			$('#revisedForm').addClass('disabled');
				$('#acceptForm').addClass('disabled');
	 			$('#idReviseForm').attr('action', getContextPath() + '/buyer/reviseSupplierForm');
				$("#idReviseForm").submit();
			}else{
				return;
			}
		});

 		
	});

	
</script>
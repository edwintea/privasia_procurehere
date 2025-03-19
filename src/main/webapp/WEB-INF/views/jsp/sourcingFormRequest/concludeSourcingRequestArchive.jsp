<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!-- Modal content-->
<div id="tab-1" class="tab-content current doc-fir tab-main-inner" style="display: none;">
	<form:form action="${pageContext.request.contextPath}/buyer/concludeSourcingRequest" modelAttribute="sourcingFormRequest" id="idFrmConcludeWithRemark" method="post">
		<form:hidden path="id" />
		<div class="row marg-bottom-10" id="idSelectedEvent">
		</div>
			<div class="row marg-bottom-10">
				<div class="col-md-3 marg-left-10">
					<label class="marg-top-10 marg-left-5"> <spring:message code="Product.remarks" /> </label>
				</div>
				<div class="col-md-8">
					<spring:message code="Product.remarks" var="remarks"/>
					<form:textarea id="AwRemk" path="concludeRemarks" data-validation="required length" placeholder="${remarks}" data-validation-length="max500" class="form-control textarea-counter" rows="3" />
				</div>
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<form:button type="button" id="btnConcludeWithRemark" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out"><spring:message code="eventarchieve.conclude" /></form:button>
				<button type="button" id="btnConcludeWithRemarkCancel" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal" ><spring:message code="application.cancel" /></button>
			</div>
	</form:form>
</div>

 
 

<style>
.concluedeData.role-bottom.checkbox-set ul li span {
	padding: 0 !important;
}
</style>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/numeral.min.js"/>"></script>
<script>

	$(document).ready(function() {
		
		$('#btnConcludeWithRemark').click(function() {
			if($('#idFrmConcludeWithRemark').isValid()) {
 				$('#concludeEvent').modal('hide');
 				$('#concludeConfirmEvent').modal('show');
 
			}
		});

		$('#btnConcludeConfirm').click(function() {
				$(this).addClass('disabled');
				$('#loading').show();
				$('#concludeConfirmEvent').modal('hide');
				$('#idFrmConcludeWithRemark').submit();
		});

		$.validate({
			lang : 'en',
			onfocusout : false,
			validateOnBlur : true,
			modules : 'sanitize'
		});
	});
</script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/select.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/select.js"/>"></script>

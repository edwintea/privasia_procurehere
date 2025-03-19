<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="row marg-bottom-10">
	<div class="col-md-3">
		<label for="uom" class="marg-top-10">
			<spring:message code="questionnaire.label" />&nbsp;&nbsp;
			<spring:message code="label.name" />
		</label>
	</div>
	<div class="col-md-5">
		<input type="text" id="name" name="name" ,palceholder="Questionnaire name" data-validation="required length" class="form-control" data-validation-length="4-124" />
	</div>
</div>
<div class="row marg-bottom-10">
	<div class="col-md-3">
		<label for="uom" class="marg-top-10">
			Questionnaire&nbsp;&nbsp;
			<spring:message code="application.description" />
		</label>
	</div>
	<div class="col-md-5">
		<textarea rows="5" name="description" id="description" palceholder="Questionnaire Description" data-validation="length" class="form-control" data-validation-length="0-500"></textarea>
		<span class="sky-blue"><spring:message code="dashboard.valid.max.characters" /></span>
	</div>
</div>
<div class="row marg-bottom-10">
	<div class="col-md-3">&nbsp;</div>
	<div class="col-md-5">
		<button type="submit" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out" name="Save" id="q">
			<spring:message code="application.update" />
		</button>
		<button type="button" class="btn ph_btn_midium btn-black hvr-pop hvr-rectangle-out1" name="Cancel" id="cqCancel" onclick="javascript:$('#idCreateRftCq').get(0).reset();">
			<spring:message code="application.cancel" />
		</button>
	</div>
</div>



<!-- <script type="text/javascript">
	$(document).ready(function() {

		$("#cqSave").click(function() {
			var name = $('#name').val();
			var formId = "${sourceForm.id}";
			var description = $('#description').val();

			$.ajax({
				type : "post",
				url : "${pageContext.request.contextPath}" + "/buyer" + "/sourcingFormCqList" + "/" + formId,
				data : {
					name : name,
					description : description
				},
				success : function(result) {
					alert("sarang");
				}
			});
		});
	});
</script>

 -->
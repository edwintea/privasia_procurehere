<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<div class="row marg-bottom-10">
	<div class="col-md-3">
		<label for="uom" class="marg-top-10">
			<spring:message code="application.questionnaire" />&nbsp;&nbsp;
			<spring:message code="label.name" />
		</label>
	</div>
	<div class="col-md-5">
		<spring:message code="rfaevent.questionnaire.description.placeholder" var="cqdesc"/>
		<form:input type="text" path="name" id="name" placeholder="${cqdesc}" data-validation="required length" class="form-control" data-validation-length="4-124" />
	</div>
</div>
<div class="row marg-bottom-10">
	<div class="col-md-3">
		<label for="uom" class="marg-top-10">
			<spring:message code="application.questionnaire" />&nbsp;&nbsp;
			<spring:message code="application.description" />
		</label>
	</div>
	<div class="col-md-5">
		<form:textarea rows="5" path="description" id="description" placeholder="${cqdesc}" data-validation="length" class="form-control" data-validation-length="0-500" />
		<span class="sky-blue"><spring:message code="dashboard.valid.max.characters" /></span>
	</div>
</div>
<div class="row marg-bottom-10">
	<div class="col-md-3">&nbsp;</div>
	<div class="col-md-5">
		<form:button type="submit" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out" name="Save" id="cqSave">
			<spring:message code="application.create" />
		</form:button>
		<form:button type="button" class="btn ph_btn_midium btn-black hvr-pop hvr-rectangle-out1" name="Cancel" id="cqCancel" onclick="javascript:$('#idCreateRftCq').get(0).reset();">
			<spring:message code="application.cancel" />
		</form:button>
	</div>
</div>

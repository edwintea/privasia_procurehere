<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="">
<div class="row marg-bottom-20">
	<div class="col-md-3">
		<label for="uom" class="marg-top-10"> <spring:message code="application.name" /> </label>
	</div>
	 
	<div class="col-md-5">
		<spring:message code="event.bqname.required" var="required" />
		<input type="text" id="bqName" palceholder="Bill of Quantity" data-validation="required length" data-validation-length="max124" class="form-control" data-validation-error-msg-required="${required}" />
	</div>
</div>
<div class="row marg-bottom-20">
	<div class="col-md-3">
		<label for="uom" class="marg-top-10"> <spring:message code="application.description" /> </label>
	</div>
	<input id="id" name="id" value="" type="hidden">
	<div class="col-md-5">
		<textarea id="bqDesc" rows="5" class="form-control" data-validation="length" data-validation-length="0-500"></textarea>
		<span class="sky-blue"><spring:message code="dashboard.valid.max.characters" /></span>
	</div>
</div>
<div class="row marg-bottom-20">
	<div class="col-md-3">&nbsp;</div>
	<div class="col-md-5">
		<button type="submit" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out " id="bqSave"><spring:message code="application.save" /></button>
		<button type="button" class="btn ph_btn_midium btn-black hvr-pop hvr-rectangle-out1" name="Cancel" id="bqCancel"><spring:message code="application.cancel" /></button>
	</div>
</div>
<div>
	<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
</div>
</div>
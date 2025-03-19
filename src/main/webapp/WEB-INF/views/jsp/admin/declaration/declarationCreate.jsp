
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<sec:authorize access="(hasRole('DECLARATION_EDIT') or hasRole('ADMIN')) and hasRole('BUYER')" var="canEdit" />
<sec:authorize access="hasRole('DECLARATION_VIEW_ONLY')" var="buyerReadOnlyAdmin" />
<spring:message var="uomCreateDesk" code="application.uom.create" />
<script>
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${uomCreateDesk}] });
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
.ckContentDisabled {
  cursor:no-drop;
  pointer-events: none;
  touch-action: none;
}
select[readonly] option {
  background: #eee;
  cursor:no-drop;
  pointer-events: none;
  touch-action: none;
}

.modal-dialog.fordeclaration {
    position: absolute;
    top: 100px;
    right: 100px;
    bottom: 0;
    left: 0;
    display: inline-block;
  	text-align: left;
    overflow: auto;
}
#declarationModal.reset-that {
  all: initial;
  * {
    all: unset;
  };
  word-break: break-all;
}
.mb {
  color:#ffffff
  };
</style>
<div id="page-content" view-name="Declaration">
	<div class="col-md-offset-1 col-md-10">
		<!-- pageging  block -->
		<ol class="breadcrumb">
			<li><a id="dashboardLink" href="${pageContext.request.contextPath}/buyer/buyerDashboard">
					<spring:message code="application.dashboard" />
				</a></li>
			<c:url value="/buyer/declarationList" var="listUrl" />
			<li><a id="listLink" href="${listUrl}">
					<spring:message code="declaration.configuration" />
				</a></li>
			<li class="active"><c:out value='${btnValue}' /> <spring:message code="label.declaration" /></li>
		</ol>
		<div class="Section-title title_border gray-bg">
			<h2 class="trans-cap manage_icon">
			<c:out value='${btnValue}' /> <spring:message code="label.declaration" />
			</h2>
		</div>
		<div class="Invited-Supplier-List import-supplier white-bg">
			<div class="import-supplier-inner-first-new pad_all_15 global-list">
				<form:form id="declarationCreate" cssClass="form-horizontal bordered-row" method="post" action="saveDeclaration" modelAttribute="declarationObj">
					<header class="form_header"> </header>
					<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="title" cssClass="marg-top-10">
								<spring:message code="declaration.title" />
							</form:label>
						</div>
						<form:hidden path="id" />
						<div class="col-md-5 ${assignedCount > 0 ? 'disabled':''}">
							<spring:message code="declaration.title" var="title" />
							<spring:message code="declaration.title.required" var="required" />
							<spring:message code="declaration.title.length" var="length" />
							<form:input path="title" value=""  data-validation="required length" data-validation-length="1-128" data-validation-allowing="-_/ ." cssClass="form-control " id="idTitle" placeholder="${title}" />
						</div>
					</div>
					<div class="row marg-bottom-20">
					</div>	
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<label path="content" cssClass="marg-top-10">
								<spring:message code="declaration.content" />
							</label>
						</div>
						<div class="col-md-5" id="addContent">
							<spring:message code="declaration.content" var="desc" />
							<form:textarea path="content" id="declarationContentEditor" name="declarationContentEditor" data-validation="length" maxlength="20000" data-validation-length="max20000"  data-validation-error-msg-length="Input Value must be between 1 to 20000." cssClass="form-control"   placeholder="${desc}"  />
							<span class="sky-blue">Max 20,000 characters only</span>
						</div>
					</div>
					<div class="row marg-bottom-20 notifyType-group">
						<div class="col-md-3">
							<label class="marg-top-10"><spring:message code="declaration.type" /></label>
						</div>
						<div class="rfr_field col-md-9">
							<div class="radio_yes-no-main width100">
								<c:forEach  items="${declarationTypeList}" var="declarationType" >
									<div class="radio_yes-no">
										<div class="radio-info">
											<label class="marg-right-10"> 
											      <form:radiobutton path="declarationType" class="custom-radio"  name="declarationType" value="${declarationType}"  disabled="${(btnValue=='Update' || assignedCount > 0) ? true:false}" />${declarationType.value}
											</label>
										</div>
									</div>
								</c:forEach>
							</div>
						</div>
					</div>
					<div class="row marg-bottom-20">
						<div class="col-md-3">
							<form:label path="status" cssClass="marg-top-10">
								<spring:message code="application.status" />
							</form:label>
						</div>
						<div class="col-md-5 ${(assignedCount > 0 and declarationObj.status == 'ACTIVE') ? 'disabled':''}">
							<form:select path="status" cssClass="form-control chosen-select" id="idStatus" >
								<form:options items="${statusList}" />
							</form:select>
						</div>
					</div>
					<div class="row marg-bottom-202">
						<div class="col-md-3">
							<label class="marg-top-10"></label>
						</div>
						<div class="col-md-9 dd sky mar_b_15">
						<c:if test="${canEdit}">
							<form:button type="submit" value="save" id="saveDeclaration" class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out" >${btnValue}</form:button>
						</c:if>
							<c:url value="/buyer/declarationList" var="createUrl" />
							<a href="${createUrl}" class="btn btn-black hvr-pop hvr-rectangle-out1 ph_btn_midium button-previous">
								<spring:message code="application.cancel" />
							</a>
							<a href="#declarationModal" id="previewButton" class="btn btn-success hvr-pop hvr-rectangle-out2  ph_btn_midium button-previous">Preview </a>
						</div>
					</div>
				</form:form>
			</div>
		</div> 
	</div>
</div>
<!-- Preview POP UP -->
<div class="flagvisibility dialogBox" id="declarationModal"  style="overflow: auto;" title="<spring:message code="declaration.popup.title" />">
	<div class="float-left width800 pad_all_15 white-bg">
		<div class="row" style="margin-left:10px;">
				<span class="reset-that" id="declarationContent"></span>
		</div>
	</div>
</div>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/ckeditor/ckeditor.js"/>"></script>
<script>
	CKEDITOR.env.isCompatible = true;
	CKEDITOR.config.fillEmptyBlocks = false;	
</script>

<script>
	<c:if test="${buyerReadOnlyAdmin}">
	$(window).bind('load', function() {
		var allowedFields = '.button-previous, #dashboardLink, #listLink';
	});
	</c:if>

	$.validate({
		lang : 'en',
		modules : 'date'
	});
	
</script>
<script type="text/javascript">
	$('document').ready(function(){
		editorcmps = CKEDITOR.replace('declarationContentEditor',
				{
						toolbarLocation : 'bottom',
						width : '100%',
						height : '200px',
						removePlugins : 'dragdrop,basket,elementspath,resize,images',
						toolbarGroups : [
							{ name: 'document', groups: [ 'mode', 'document', 'doctools' ] },
							{ name: 'clipboard', groups: [ 'clipboard' ] },
							{ name: 'editing', groups: [ 'find', 'selection', 'spellchecker', 'editing' ] },
							{ name: 'forms', groups: [ 'forms' ] },
							{ name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ] },
							{ name: 'paragraph', groups: [ 'list', 'indent', 'blocks', 'align', 'bidi', 'paragraph' ] },
							{ name: 'links', groups: [ 'links' ] },
							{ name: 'insert', groups: [ 'insert' ] },
							{ name: 'colors', groups: [ 'colors' ] },
							{ name: 'tools', groups: [ 'tools' ] },
							{ name: 'others', groups: [ 'others' ] },
							'/'
						],
				});
		editorcmps.on('instanceReady',function(ev) {
			// Prevent drag-and-drop.
			ev.editor.document.on('drop',function(ev) {
								ev.data.preventDefault(true);
							});
		}); 
		
		
		
		
		$("#declarationCreate").submit(function(){
			 $('#declarationContentEditor').parent().removeClass('has-error');
			 $(".checkError").remove();
			 if(CKEDITOR.instances['declarationContentEditor'].getData()==''){
				$('#declarationContentEditor').parent().addClass('has-error').append('<span class="help-block form-error checkError">This is a required field.</span>');
				 return false;
	 		}
			var content = CKEDITOR.instances['declarationContentEditor'].getData();
			 if(CKEDITOR.instances['declarationContentEditor'].getData()!='' && content.length > 19917){
				 $('#declarationContentEditor').parent().addClass('has-error').append('<span class="help-block form-error checkError">The input value is longer than 20,000 characters.</span>');
				 return false;
			 }
			 
	 	});
	 	
		
		$("#previewButton").click(function(){
			var content = CKEDITOR.instances['declarationContentEditor'].getData().replace(/&Acirc;&#160;/g, '');
			$('#declarationContentEditor').parent().removeClass('has-error');
			 $(".checkError").remove();
			 if(CKEDITOR.instances['declarationContentEditor'].getData()==''){
				 $('#declarationContentEditor').parent().addClass('has-error').append('<span class="help-block form-error checkError">This is a required field.</span>');
			 }else{
				 $('#declarationContent').html(content);
					$("#declarationModal").dialog({
						modal : true,
						minWidth : 300,
						width : '50%',
						maxWidth : 600,
						minHeight : 200,
						maxHeight : 500,
						dialogClass : "",
						show : "fadeIn",
						draggable : true,
						resizable : false,
						dialogClass : "dialogBlockLoaded",
					});
			 }
		});
	});
</script>

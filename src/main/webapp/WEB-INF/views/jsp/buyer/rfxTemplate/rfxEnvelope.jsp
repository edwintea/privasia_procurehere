<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="/WEB-INF/template-functions.tld" prefix="tf"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<div class="meeting2-heading">
	<h3>
		<spring:message code="rfx.Envelope.Deatils" />
	</h3>
</div>
<div id="apprTab" class="pad_all_15 collapse in float-leftwidth-100position-relative">
	<spring:message code="approval.search.user" var="approversplace" />
	<div class="dynamic-Envelope form-group">
		<div class="row marg-bottom-10 pad_all_20">
			<div class="col-md-2 ">
				<div class="check-wrapper chk-lbl d-flex" style="width: 100%; display: flex; align-items: center;">
					<spring:message code="rfx.Envelope.preSet" var="visible" />
					<form:checkbox path="rfxEnvelopeReadOnly" id="preSet" class="custom-checkbox " title="${visible}" label="${visible}" />
				</div>
			</div>
			<div class="col-md-4 ">
				<div class="check-wrapper d-flex chk-lbl" style="width: 100%; display: flex; align-items: center;">
					<spring:message code="envelope.sequence.opening" var="visible" />
					<form:checkbox path="rfxEnvelopeOpening" id="openingSeq" class="custom-checkbox " title="${visible}" label="${visible}" />
				</div>
			</div>
			<div class="col-md-6 btn-radio" id="allowEvaluator">
				<div class="check-wrapper d-flex chk-lbl" style="width: 100%; display: flex; align-items: center;" id="media">
					<spring:message code="envelope.sequence.opening.allow" var="visible"/>
					<form:checkbox path="allowDisqualifiedSupplierDownload" id="openingSeq1" class="custom-checkbox" title="${visible}" label="${visible}" />
				</div>
			</div>
			<div class="col-md-2 "></div>
			<div class="col-md-10 btn-radio ">
				<div class="check-wrapper d-flex" style="width: 100%; margin-top: 15px; display: flex; align-items: center;">
					<label class="select-radio-lineHgt"> <form:radiobutton path="rfxEnvOpeningAfter" id="rfxEnvOpeningAfter" checked="checked" value="OPENING" class="custom-radio showSupplierBlocks" /> After each Opening
					</label>
				</div>
			</div>
			<div class="col-md-2"></div>
			<div class="col-md-10 btn-radio">
				<div class="check-wrapper d-flex" style="width: 100%; display: flex; align-items: center;">
					<label class="select-radio-lineHgt"> <form:radiobutton path="rfxEnvOpeningAfter" name="rfxEnvOpeningAfter" id="rfxEnvOpeningAfter" value="EVALUATION" class="custom-radio showSupplierBlocks" /> After each Evaluation
					</label>
				</div>
			</div>
		</div>
		
		<div id="envlop" class="row cloneready marg-bottom-10 pad_all_20">
			<div class="col-md-9">
				<spring:message code="tooltip.add.approval.level" var="addapproval" />
				<button type="button" class="btn btn-plus btn-info hvr-pop hvr-rectangle-out" id="addEnvelpe" data-placement="top" title="${addapproval}">
					<spring:message code="label.rftenvelop.create" />
				</button>
			</div>
		</div>
		<!-- For LOOP -->

		<c:forEach items="${envlopeList}" var="approval" varStatus="status">

			<c:if test="${ not empty approval}">
				<div id="new-envelope-${status.index + 1}" class="row new-envelope">
					<div class="col-md-2 col-sm-3 col-xs-12 col-lg-2">
						<label class="level">Envelope ${status.index + 1} </label>
					</div>
					<div class="col-md-7 col-sm-7 col-xs-8 col-lg-6">
						<div class="col-md-6">
							<spring:message code="label.rftenvelop.name" var="desc" />
							<spring:message code="label.rftenvelop.name" var="name" />
							<form:input path="rfxEnvelope${status.index+1}" data-validation-length="1-128" value="${approval.rfxEnvelope}" name="rfxEnvelope${status.index+1}" data-validation="required length" class="form-control envlope" placeholder="${name}" />
						</div>

						<div class="col-md-4 sequence">
							<spring:message code="label.rftenvelop.name" var="desc" />
							<spring:message code="label.rftenvelop.name" var="name" />
							<form:input id="rfxSequence${status.index+1}" path="rfxSequence${status.index+1}" value="${approval.rfxSequence}" name="rfxSequence${status.index+1}" class="form-control sequence" placeholder="Envelop Sequence" />
						</div>
						<button class="btn btn-plus btn-info hvr-pop hvr-rectangle-out removeEnvelope">
							<i class="glyphicon glyphicon-remove" aria-hidden="true"></i>
						</button>
					</div>
					<!-- <div class="col-sm-2 col-md-2 col-xs-2 ">
					<div class="btn-address-field pt-0">
						
						
					</div>
				</div> -->
				</div>
			</c:if>
		</c:forEach>

	</div>
	<div id="envelopeError" class="en-center"></div>

</div>
<style>
.chosen-container-multi {
	height: auto !important;
}

.en-center {
	text-align: center;
	width: 75%;
	margin-left: 152px;
}

.row.new-envelope {
	margin-bottom: 5px !important;
}

.pt-0 {
	padding-top: 1px !important;
	padding-left: 10px;
}

.dropdown-toggle {
	/* width: 78.52px; */
	width: 40px;
}

.select2-container-multi {
	margin-top: 10px !important;
}

.row.dynamic-approval {
	margin-top: 10px;
}

.new-envelope label {
	/* float: right; */
	padding: 15px;
}

.btn-address-field {
	padding-top: 10px;
	margin-left: -15px;
}

.removeApproval {
	margin-left: 10px;
}

#uniform-rfxEnvOpeningAfter {
	padding: 0px !important;
}

.cloneready {
	margin-bottom: 10px;
}

.collapseable  .meeting2-heading .checkbox-primary label {
	color: #636363 !important;
	float: left;
	font-size: 14px;
	margin-bottom: 15px;
	padding: 20px 0 18px 15px !important;
	width: 100%;
}

.disableCq {
	pointer-events: none !important;
}

.text-color {
	color: #1a1919 !important;
}

.checkbox.inline.no_indent input {
	margin-left: 10px;
	padding-top: 0;
}

/* #allowEvaluator {
	margin-left: 72%;
	margin-top: -52px;
}
 */
 
#openingSeq1 {
	margin-top: 4%;
}

/* @media only screen and (max-width: 1440px) {
	#media {
		margin-top: 23px;
		margin-left: -35px;
	}
}
 */
 </style>
<script type="text/JavaScript">

$(document).ready(function() {

	
if(!$("#openingSeq").prop('checked'))
	{
	$(".btn-radio").hide();
	$(".sequence").hide();

	}
	
	$('#openingSeq').change(function(e) {
if($(this).prop('checked'))
	{
	$(".btn-radio").show();
	$(".sequence").show();
	}else
		{
		$(".btn-radio").hide();
		$(".sequence").hide();

		}
		
		
	}); 
	

	$('#rfxSequence1').mask('00', {
	});
	$('#rfxSequence2').mask('00', {
	});
	$('#rfxSequence3').mask('00', {
	});
	$('#rfxSequence4').mask('00', {
	});
	$('#rfxSequence5').mask('00', {
	});
	$('#rfxSequence6').mask('00', {
	});
});


	var index;
	index = ${fn:length(envlopeList) + 1};
	$(document).ready(function() {
		
		$(document).delegate('.small', 'click', function(e) {
			$(this).find(".approval_condition").trigger("click");	
		});

		$("#addEnvelpe").click(function(e) {
		var rfxEnvelope="rfxEnvelope"+index;
		var rfxSequence = "rfxSequence"+index
			e.preventDefault();
			var template = '<div id="new-envelope-'+index+'" class="row new-envelope">';
			template += '<div class="col-md-2 col-sm-3 col-xs-12 col-lg-2"><label class="level">Envelope ' + index + '</label></div>';
			template += '<div class="col-md-7 col-sm-7 col-xs-8 col-lg-6 " id="sel">';
			template += '<div class="col-md-6"><spring:message code="label.rftenvelop.name" var="desc" />	<spring:message code="label.rftenvelop.name" var="name" />	<input path='+rfxEnvelope+' data-validation-length="1-128" name='+rfxEnvelope+' data-validation="required length" class="form-control envlope text-color"  id="idRfxTemplateName" placeholder="${name}" /></div>';
			
			template += '<div class="col-md-4 sequence"><spring:message code="label.rftenvelop.name" var="desc" />	<spring:message code="label.rftenvelop.name" var="name" />	<input path='+rfxSequence+'  name='+rfxSequence+'    name='+rfxEnvelope+'   class="form-control sequence text-color"      id='+rfxSequence+' placeholder="Envelop Sequence" /></div>';

			template += '<button class="btn btn-plus btn-info hvr-pop hvr-rectangle-out removeEnvelope"> <i class="glyphicon glyphicon-remove" aria-hidden="true"></i> </button></div></div>';
			template += '</div>';

		
			
			if(!(index>6))
				{
				$(".dynamic-Envelope").append(template);
				if(!$("#openingSeq").prop('checked'))
				{
				$(".sequence").hide();

				}

				index++;
				}
				
			
	
		});

		$(document).delegate('.removeEnvelope', 'click', function(e) {
	
			$(this).closest(".new-envelope").remove();

			$(".new-envelope").each(function(i, v) {
				i++;
				$(this).attr("id", "new-envelope-" + i);
				$(this).find(".level").text('Envelope ' + i);
				
				$(this).find("input[name='rfxEnvelope1']").each(function(){
					$(this).attr("name",'rfxEnvelope');
				}) 
				
				$(this).find(".envlope").each(function(){
					
					$(this).attr("name",'rfxEnvelope'+((i)));
					$(this).attr("id", "rfxEnvelope-" + ((i)) + "");	
				}) //select name reindex
				
	               $(this).find(".sequence").each(function(){
					
					$(this).attr("name",'rfxSequence'+((i)));
					$(this).attr("id", "rfxSequence-" + ((i)) + "");	
				}) //select name reindex
				
				
				
			});
			
		
			
			index--;
		});

	

	});
	
	
	window.onload = function() {
		//Register onclick event for button after loading document and dom
		document.getElementById("addEnvelpe").addEventListener("click", function() {

		});
	};
	
	
	


</script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/select.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/select.js"/>"></script>
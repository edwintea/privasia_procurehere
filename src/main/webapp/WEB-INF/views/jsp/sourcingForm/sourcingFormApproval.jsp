<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="/WEB-INF/template-functions.tld" prefix="tf"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<sec:authorize access="hasRole('REQUEST_TEMPLATE_EDIT') or hasRole('ADMIN')" var="canEdit" />
<div class="dynamic-approval col-md-10">
	<spring:message code="approval.search.user" var="approversplace" />
	<div>
		<div class="row">
			<div class="col-sm-11 col-md-11 col-xs-11 approvalcount">
				<form:checkbox path="addAdditionalApprovals" cssClass="custom-checkbox autoSave" />
				<label class="col-sm-11 col-md-11 col-xs-11 control-label"><spring:message code="sourcingtemplate.additional.approval" /></label>
			</div>
		</div>
	</div>
	<div class="form_field  ${isTemplateUsed ? 'disabled':''}">
		<div class="approvalcount row">
			<div class="col-md-3">
				<label class="control-label"> <spring:message code="sourcingForm.approval.approvalCount" />
				</label>
			</div>

			<div class="col-sm-3 col-md-3 col-xs-3">
				<div class="input-prepend input-group ${canEdit ? '' : 'disabled'}">
					<spring:message code="sourcingForm.approval.place.approvalCount" var="approversCount" />
					<form:input path="approvalsCount" autocomplete="off" id="idapproversCount" placeholder="${approversCount}" class="form-control autoSave" type="text" data-validation="positive length" data-validation-length="max2" />
				</div>
			</div>
		</div>
	</div>
	<div class="row cloneready ${canEdit ? '' : 'disabled'}">
		<c:if test="${!isTemplateUsed}">
			<div class="col-md-3 col-sm-4">

				<button type="button" class="btn btn-plus btn-info hvr-pop hvr-rectangle-out" id="addSelect" data-placement="top" style="margin-top: 10px;">
					<spring:message code="application.addapproval.button" />
				</button>
			</div>
		</c:if>
	</div>
	<!-- For LOOP -->
	<c:set var="doneUsers" value="" />
	<c:set var="doneLevel" value="" />
	<div class="approVAlRouteBox">
		<c:forEach items="${sourcingForm.sourcingFormApproval}" var="approval" varStatus="status">
			<div id="new-approval-${status.index + 1}" class="row new-approval">
				<c:if test="${approval.active}">
					<c:forEach items="${sourcingFormApproval.sourcingFormApproval[status.index].approvalUsers}" var="usr">
						<c:if test="${usr.approvalStatus != 'PENDING' }">
							<c:set var="doneUsers" value="${doneUsers.concat(\"'\").concat(usr.user.id).concat(\"',\") }" />
							<c:set var="doneLevel" value="${sourcingFormApproval.sourcingFormApproval[status.index].id}" />
						</c:if>
					</c:forEach>
				</c:if>
				<div class="col-md-3 col-xs-12 col-sm-3">
					<label class="level mb-0"> <spring:message code="rfi.createrfi.level" /> ${status.index + 1}
					</label>
				</div>
				<div class="col-md-7 col-sm-7 col-xs-8 col-lg-4 approval-chosen-multiselect ${buyerReadOnlyAdmin ? 'disabled' : ''} ${approval.done ? 'disabled':''}">
					<form:hidden class="approval_id_hidden" path="sourcingFormApproval[${status.index}].id" />
					<span class="dropUp">
						<form:select autocomplete="off" data-validation="required" id="multipleSelectExample-${status.index}" path="sourcingFormApproval[${status.index}].approvalUsers" cssClass="user-list-all chosen-select tagTypeMultiSelect ${approval.done ? 'disabled':''}" multiple="multiple">
							<c:forEach items="${userList}" var="usr">
								<c:if test="${usr.id == '-1' }">
									<form:option value="-1" label="${usr.user.name}" disabled="true" />
								</c:if>
								<c:if test="${usr.id != '-1' }">
									<form:option value="${usr.id}" label="${usr.user.name}" />
								</c:if>
							</c:forEach>
						</form:select>
					</span>
				</div>
				<div class="col-sm-2 col-md-3 col-xs-3">
					<div class="btn-address-field pt-0">
						<button type="button" class="btn dropdown-toggle  ${ (approval.active || approval.done ) ? 'disabled':''}" data-toggle="dropdown">
							<i class="fa ${approval.approvalType == 'OR' ? 'fa-user' : 'fa-users'}" aria-hidden="true"></i>
						</button>
						<ul class="dropdown-menu dropup">
							<li><a href="javascript:void(0);" class="small" tabIndex="-1">
									<form:checkbox path="sourcingFormApproval[${status.index}].approvalType" value="OR" cssClass="approval_condition" label="Any"></form:checkbox>
								</a></li>
							<li><a href="javascript:void(0);" class="small " tabIndex="-1">
									<form:checkbox path="sourcingFormApproval[${status.index}].approvalType" value="AND" cssClass="approval_condition" label="All"></form:checkbox>
								</a></li>
						</ul>
						<button class="btn btn-plus btn-info hvr-pop hvr-rectangle-out removeApproval ${canEdit ? '' : 'disabled'}">
							<i class="glyphicon glyphicon-remove" aria-hidden="true"></i>
						</button>
					</div>
				</div>
			</div>
		</c:forEach>
	</div>
</div>
<style>
.pt-4 {
	padding-top: 4px !important;
	padding-left: 15px;
}

.row.new-approval {
	margin-bottom: 5px;
}

.mb-0 {
	margin-bottom: 0 !important;
}
/* .chosen-container-multi .chosen-choices {
	padding: 3px;
}
.chosen-select {
	display: contents !important;
}
 */
.pt-0 {
	padding-top: 1px !important;
	padding-left: 10px;
}

/* .select2-container-multi {
	margin-top: 10px !important;
}
 */
#prSummaryForm .approVAlRouteBox {
	max-height: 300px;
	overflow-y: auto;
	overflow-x: hidden;
}

.row.dynamic-approval {
	margin-top: 10px;
}

.new-approval label {
	/* float: right; */
	padding: 15px 0;
}

.btn-address-field {
	padding-top: 10px;
	margin-left: -15px;
}

.dropdown-toggle {
	/* width: 78.52px; */
	width: 40px;
}

.removeApproval {
	margin-left: 10px;
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

.checkbox.inline.no_indent input {
	margin-left: 10px;
	padding-top: 0;
}

.dropdown-menu a.small label {
	display: inline-block;
	float: right;
	padding: 2px 10px;
	position: absolute;
}

.approvalcount {
	margin-top: 1%;
}
</style>
<script type="text/JavaScript">
	var index = 0;
	index = ${fn:length(sourcingForm.sourcingFormApproval) + 1};

	$(document).delegate('.small', 'click', function(e) {
		//console.log($(this).find(".approval_condition"));
		$(this).find(".approval_condition").trigger("click");	
	});
	
	$(document).ready(function() {
		 $('#addSelect').attr('disabled',${sourceForm.isTemplateUsed});
		 
		 
		$(document).delegate('.small', 'click', function(e) {
			//console.log($(this).find(".approval_condition"));
			$(this).find(".approval_condition").trigger("click");	
		});

		//.collapse('toggle')
		/* Prevent To Remove In Edit Mode */
		var lockedLevel = '${doneLevel}';
		var lockedElement = [${!empty doneUsers ? doneUsers : ''} ''];
		$.each(lockedElement, function(i, item){
			$(".tagTypeMultiSelect").find('option[selected="selected"][value="'+item+'"]').attr('data-locked',true);			
		});
		/* Prevent To Remove In Edit Mode */
		/* $(".tagTypeMultiSelect").select2(); */
		
		$("#addSelect").click(function(e) {
			e.preventDefault();
			var template = '<div id="new-approval-'+index+'" class="row new-approval">';
			template += '<input type="hidden" class="approval_id_hidden" name="sourcingFormApproval[' + (index - 1) + '].id" value="" />';
			template += '<div class="col-md-3 col-xs-12 col-sm-3"><label class="level mb-0">Level ' + index + '</label></div>';
			template += '<div class="col-md-7 col-sm-7 col-xs-8 col-lg-4 approval-chosen-multiselect" id="sel">';
			template += '<span class="dropUp"><select data-validation="required" autocomplete="off" id="multipleSelectExample-'+(index - 1)+'" name="sourcingFormApproval[' + (index - 1) + '].approvalUsers" class="user-list-all chosen-select tagTypeMultiSelect" data-placeholder=<spring:message code="createrfi.approvalroute.approvers.placeholder" /> multiple>';
			<c:forEach items="${userList}" var="users">
			<c:if test="${users.id == '-1' }">
					template += '<option value="" disabled>${users.user.name}</option>';
			</c:if>
			<c:if test="${users.id != '-1' }">
					template += '<option value="${users.id}" >${users.user.name}</option>';
			</c:if>				
			</c:forEach> 
			template += '</select></span></div><div class="col-sm-2 col-md-2 ">';
			template += '<div class="btn-address-field pt-0"><button type="button" class="btn dropdown-toggle" data-toggle="dropdown">';
			template += '<i class="fa fa-user" aria-hidden="true"></i></button>';
			template += '<ul class="dropdown-menu dropup">';
			template += '<li><a href="javascript:void(0);" class="small"  tabIndex="-1"><input name="sourcingFormApproval[' + (index - 1) + '].approvalType" checked="checked" value="OR" class="approval_condition" type="checkbox"/><label>Any</label></a></li>';
			template += '<li><a href="javascript:void(0);" class="small " tabIndex="-1"><input name="sourcingFormApproval[' + (index - 1) + '].approvalType" value="AND" class="approval_condition"  type="checkbox"/><label>All</label></a></li>	</ul>';
			template += '<button class="btn btn-plus btn-info hvr-pop hvr-rectangle-out removeApproval"> <i class="glyphicon glyphicon-remove" aria-hidden="true"></i> </button></div></div>';
			template += '</div>';

			$(".dynamic-approval").find('.approVAlRouteBox').append(template);
			/* $('select#multipleSelectExample-'+(index - 1)).select2(); */
			$('#multipleSelectExample-'+(index - 1)).chosen({search_contains:true}).change(function() { $(this).validate(); $(this).blur(); $(this).parent().find('.chosen-search-input').attr('placeholder', ''); });	
			$(".approVAlRouteBox").animate({ scrollTop: $(".approVAlRouteBox")[0].scrollHeight}, 1000);
			index++;
		});

		$(document).delegate('.removeApproval', 'click', function(e) {
			$(this).closest(".new-approval").remove();
			$(this).closest(".approval_id_hidden").remove();	

			$(".new-approval").each(function(i, v) {
				i++;
				$(this).attr("id", "new-approval-" + i);
				$(this).find(".level").text('Level ' + i);

				
				$(this).find(".approval_id_hidden").each(function(){					
					$(this).attr("name",'sourcingFormApproval[' +(i-1) + '].id');					
				});
				
				$(this).find(".approval_condition").each(function(){
					$(this).attr("name",'sourcingFormApproval[' +(i-1) + '].approvalType');
				}); // checkbox name reindex
				
				$(this).find("input[name='_sourcingFormApproval[" + i + "].approvalType']").each(function(){
					$(this).attr('name','_sourcingFormApproval[' +(i-1) + '].approvalType');
				}); //Checkbox hidden val reindex
				
				
				$(this).find(".tagTypeMultiSelect").each(function(){
					$(this).attr("name",'sourcingFormApproval[' +(i-1) + '].approvalUsers');
					$(this).attr("id", "multipleSelectExample-" + ((i-1)) + "");					
				}) //select name reindex
				
				$(this).find("input[name='_sourcingFormApproval[" + i + "].approvalUsers']").each(function(){
					$(this).attr("name",'_sourcingFormApproval[' +(i-1) + '].approvalUsers');
				}) //select name reindex
				
			});
			index--;
		});

		$(document).delegate('.approval_condition', 'click', function(e) {
		
			$(this).closest('.btn-address-field').find(".approval_condition").each(function(){
				$(this).prop('checked', false);
		      	//console.log($(this))
			});
		  
			$(this).prop('checked', true);
		 
			var current_val = $(this).val();
		
			if (current_val == "OR") {
				//console.log(current_val);
				$(this).closest('.btn-address-field').find(".dropdown-toggle").html('<i class="fa fa-user" aria-hidden="true"></i>');
			}
			if (current_val == "AND") {
				//console.log(current_val);
				$(this).closest('.btn-address-field').find(".dropdown-toggle").html('<i class="fa fa-users" aria-hidden="true"></i>');
			}
		});

	});
	
	
	
	//Create a dynamic Select
	function createSelect() {

		var sel = document.getElementById("sel");
		//alert("hello");
		//Create array of options to be added
		var array = [ "Steven White", "Nancy King", "Nancy Davolio", "Michael Leverling", "Michael Suyama" ];

		//Create and append select list
		var selectList = document.createElement("select");
		selectList.setAttribute("id", "mySelect");
		selectList.setAttribute("multiple", "");
		selectList.setAttribute("placeholder", "Approvers");

		sel.appendChild(selectList);
		$(".ApprovalOption").first().clone().appendTo(sel);
		//Create and append the options
		for (var i = 0; i < array.length; i++) {
			var option = document.createElement("option");
			option.setAttribute("value", array[i]);
			option.text = array[i];
			selectList.appendChild(option);
		/* 	$('select').select2(); */
		}

	}
</script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/select.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/select.js"/>"></script>

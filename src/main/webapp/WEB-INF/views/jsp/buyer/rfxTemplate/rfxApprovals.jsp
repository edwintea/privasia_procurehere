<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="/WEB-INF/template-functions.tld" prefix="tf"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<div class="meeting2-heading">
	<h3><spring:message code="rfi.createrfi.approvalroute.label.2" /></h3>
</div>
<div id="apprTab" class="pad_all_15 collapse in float-leftwidth-100position-relative">
	<spring:message code="approval.search.user" var="approversplace" />
	<div class="dynamic-approval form-group">
		<div class="row marg-bottom-10 pad_all_20">
			<div class="align-right col-md-3"></div>
			<div class="col-md-9 ">
				<div class="check-wrapper first">
					<spring:message code="prtemplate.label.visible" var="visible" />
					<form:checkbox path="approvalVisible" class="custom-checkbox approvalCheck" title="${visible}" label="${visible}" />
				</div>
				<div class="check-wrapper">
					<spring:message code="prtemplate.label.read.only" var="read" />
					<form:checkbox path="approvalReadOnly" class="custom-checkbox approvalCheck" title="${read}" label="${read}" />
				</div>
				<div class="check-wrapper">
					<spring:message code="prtemplate.label.optional" var="optional" />
					<form:checkbox path="approvalOptional" class="custom-checkbox approvalCheck" title="${optional}" label="${optional}" />
				</div>
			</div>
		</div>
		<div class="row cloneready marg-bottom-10 pad_all_20">
			<div class="col-md-9">
				<spring:message code="tooltip.add.approval.level" var="addapproval" />
				<button type="button" class="btn btn-plus btn-info hvr-pop hvr-rectangle-out" id="addSelect" data-placement="top" title="${addapproval}">
					<spring:message code="application.addapproval.button" />
				</button>
			</div>
		</div>
		<!-- For LOOP -->
		<c:forEach items="${rfxTemplate.approvals}" var="approval" varStatus="status">
			<div id="new-approval-${status.index + 1}" class="row new-approval">
				<div class="col-md-2 col-sm-3 col-xs-12 col-lg-2">
					<label class="level"><spring:message code="rfi.createrfi.level" /> ${status.index + 1}</label>
				</div>
				<div class="col-md-7 col-sm-7 col-xs-8 col-lg-4 approval-chosen-multiselect">
					<form:select data-validation="required" id="tempAppMultipleSelect-${status.index}" path="approvals[${status.index}].approvalUsers" cssClass="user-list-all chosen-select tagTypeMultiSelect" data-placeholder="${approversplace}" multiple="multiple">
						<c:forEach items="${userList}" var="usr">
							<c:if test="${usr.id == '-1' }">
								<form:option value="-1" label="${usr.name}" disabled="true" />
							</c:if>
							<c:if test="${usr.id != '-1' }">
								<form:option value="${usr.id}" label="${usr.name}" />
							</c:if>
						</c:forEach>
					</form:select>
				</div>
				<div class="col-sm-2 col-md-2 col-xs-2 ">
					<div class="btn-address-field pt-0">
						<button type="button" class="btn dropdown-toggle" data-toggle="dropdown">
							<i class="fa ${approval.approvalType == 'OR' ? 'fa-user' : 'fa-users'}" aria-hidden="true"></i>
						</button>
						<ul class="dropdown-menu dropup">
							<li><a href="javascript:void(0);" class="small" tabIndex="-1">
									<form:checkbox path="approvals[${status.index}].approvalType" value="OR" cssClass="approval_condition" label="Any"></form:checkbox>
								</a></li>
							<li><a href="javascript:void(0);" class="small " tabIndex="-1">
									<form:checkbox path="approvals[${status.index}].approvalType" value="AND" cssClass="approval_condition" label="All"></form:checkbox>
								</a></li>
						</ul>
						<button class="btn btn-plus btn-info hvr-pop hvr-rectangle-out removeApproval">
							<i class="glyphicon glyphicon-remove" aria-hidden="true"></i>
						</button>
					</div>
				</div>
			</div>
		</c:forEach>
	</div>
</div>
<style>
.chosen-container-multi {
	height: auto !important;
}
.row.new-approval {
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

.new-approval label {
	/* float: right; */
/* 	padding: 15px; */
	padding-right: 15px;
	padding-left: 5px;
}

.btn-address-field {
	padding-top: 10px;
	margin-left: -15px;
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
</style>
<script type="text/JavaScript">
	$(document).ready(function() {
	var index;
	index = ${fn:length(rfxTemplate.approvals) + 1};
		
		$(document).delegate('.small', 'click', function(e) {
			//console.log($(this).find(".approval_condition"));
			$(this).find(".approval_condition").trigger("click");	
		});

		//.collapse('toggle')
		//$(".tagTypeMultiSelect").select2();

	//	document.getElementById("addSelect").addEventListener("click", function(e) {
		$("#addSelect").click(function(e) {
			e.preventDefault();
			var template = '<div id="new-approval-'+index+'" class="row new-approval">';
			template += '<div class="col-md-2 col-sm-3 col-xs-12 col-lg-2"><label class="level">Level ' + index + '</label></div>';
			template += '<div class="col-md-7 col-sm-7 col-xs-8 col-lg-4 approval-chosen-multiselect" id="sel">';
			template += '<select data-validation="required" id="tempAppMultipleSelect-'+(index - 1)+'" name="approvals[' + (index - 1) + '].approvalUsers" class="user-list-all chosen-select tagTypeMultiSelect" data-placeholder=<spring:message code="createrfi.approvalroute.approvers.placeholder" /> multiple>';
			 <c:forEach items="${templateApprovalUserList}" var="users">
				<c:if test="${users.id == '-1' }">
					template += '<option value="-1" disabled >${users.user.name}</option>';
				</c:if>
				<c:if test="${users.id != '-1' }">
					template += '<option value="${users.id}" >${users.user.name}</option>';
				</c:if>	
			 </c:forEach> 
			template += '</select></div><div class="col-sm-2 col-md-2 col-xs-2">';
			template += '<div class="btn-address-field  pt-0"><button type="button" class="btn dropdown-toggle" data-toggle="dropdown">';
			template += '<i class="fa fa-user" aria-hidden="true"></i></button>';
			template += '<ul class="dropdown-menu dropup">';
			template += '<li><a href="javascript:void(0);" class="small"  tabIndex="-1"><input name="approvals[' + (index - 1) + '].approvalType" checked="checked" value="OR" class="approval_condition" type="checkbox"/>&nbsp;Any</a></li>';
			template += '<li><a href="javascript:void(0);" class="small " tabIndex="-1"><input name="approvals[' + (index - 1) + '].approvalType" value="AND" class="approval_condition"  type="checkbox"/>&nbsp;All</a></li>	</ul>';
			template += '<button class="btn btn-plus btn-info hvr-pop hvr-rectangle-out removeApproval"> <i class="glyphicon glyphicon-remove" aria-hidden="true"></i> </button></div></div>';
			template += '</div>';

			$(".dynamic-approval").append(template);
			
			$('#tempAppMultipleSelect-'+(index - 1)).chosen({search_contains:true}).change(function() { $(this).validate(); $(this).blur(); $(this).parent().find('.chosen-search-input').attr('placeholder', ''); });	
			updateUserList('',$('#tempAppMultipleSelect-'+(index - 1)),'ALL_USER'); //inital add users in drop down 
			
			//$('select').select2();
			console.log("index be4 adding "+index);
			index++;
			console.log("index after adding .............. "+index);
		});

		$(document).delegate('.removeApproval', 'click', function(e) {
			$(this).closest(".new-approval").remove();

			$(".new-approval").each(function(i, v) {
				i++;
				$(this).attr("id", "new-approval-" + i);
				$(this).find(".level").text('Level ' + i);
				
				$(this).find(".approval_condition").each(function(){
					$(this).attr("name",'approvals[' +(i-1) + '].approvalType');
				}) // checkbox name reindex
				
				$(this).find("input[name='_approvals[" + i + "].approvalType']").each(function(){
					$(this).attr('name','_approvals[' +(i-1) + '].approvalType');
				}); //Checkbox hidden val reindex
				
				
				$(this).find(".tagTypeMultiSelect").each(function(){
					$(this).attr("name",'approvals[' +(i-1) + '].approvalUsers');
					$(this).attr("id", "tempAppMultipleSelect-" + ((i-1)) + "");	
				}) //select name reindex
				
				$(this).find("input[name='_approvals[" + i + "].approvalUsers']").each(function(){
					$(this).attr("name",'_approvals[' +(i-1) + '].approvalUsers');
				}) //select name reindex hidden
			});
			console.log("index be4 removal "+index);
			index--;
			console.log("index after removal "+index);
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
			//$('select').select2(); 
		}

	}
</script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/select.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/select.js"/>"></script>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="/WEB-INF/template-functions.tld" prefix="tf"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<div class="dynamic-approval col-md-10">
	<spring:message code="approval.search.user" var="approversplace" />
	<div class="row cloneready">
		<div class="col-md-3 col-sm-4">
			<button type="button" class="btn btn-plus btn-info hvr-pop hvr-rectangle-out" id="addNewSelect" data-placement="top">Add Approval Level</button>
		</div>
	</div>
	<!-- For LOOP -->
	<c:set var="doneUsers" value="" />
	<div class="approVAlRouteBox " style="max-height: unset !important; overflow-y: unset !important; overflow-x: unset !important;">
		<c:forEach items="${sourcingFormRequest.sourcingFormApprovalRequests}" var="approval" varStatus="status">
			<div id="editnew-approval-${status.index + 1}" class="row new-approval editnew-approval ">
				<c:if test="${approval.active}">
					<c:forEach items="${sourcingFormRequest.sourcingFormApprovalRequests[status.index].approvalUsersRequest}" var="usr">
						<c:if test="${usr.approvalStatus != 'PENDING' }">
							<c:set var="doneUsers" value="${doneUsers.concat(\"'\").concat(usr.user.id).concat(\"',\") }" />
						</c:if>
					</c:forEach>
				</c:if>
				<form:hidden class="approval_id_hidden editapproval_id_hidden" path="sourcingFormApprovalRequests[${status.index}].id" />
				<div class="col-md-2 col-sm-3 col-xs-12">
					<label class="level"><spring:message code="rfi.createrfi.level" /> ${status.index + 1}</label>
				</div>
				<div class="col-md-7 col-sm-7 col-xs-8 approval-chosen-multiselect approvalsLevelSelect ${approval.done ? 'disabled':''}">
					<span class="dropUp"> <form:select  data-validation="required"  id="multipleSelectExample-${status.index}" path="sourcingFormApprovalRequests[${status.index}].approvalUsersRequest" cssClass="edittagTypeMultiSelect tagTypeMultiSelect user-list-all chosen-select  ${approval.done  ? 'disabled':''}" data-placeholder="${approversplace}" multiple="multiple">
							<c:forEach items="${userList1}" var="usr">
								<c:if test="${usr.id == '-1' }">
									<form:option value="-1" label="${usr.name}" disabled="true" />
								</c:if>
								<c:if test="${usr.id != '-1' }">
									<form:option value="${usr.id}" label="${usr.name}" />
								</c:if>
							</c:forEach>
						</form:select>
					</span>
				</div>
				<div class="col-sm-6 col-md-3 col-xs-4">
					<div class="btn-address-field pt-0">
						<button  type="button" class="approvalTypeButton btn marg-mins ${approval.approvalType == 'OR' ? 'btn-success' : 'btn-warning'} dropdown-toggle ${approval.active || approval.done ? 'disabled':''}" data-toggle="dropdown">
							<i class="fa ${approval.approvalType == 'OR' ? 'fa-user' : 'fa-users'}" aria-hidden="true"></i>
						</button>
						<ul class="dropdown-menu dropup">
							<li>
								<a href="javascript:void(0);" class="small editsmall" tabIndex="-1"> <form:checkbox path="sourcingFormApprovalRequests[${status.index}].approvalType" value="OR" cssClass="approval_condition editapproval_condition" label="Any"></form:checkbox>
								</a>
							</li>
							<li>
								<a href="javascript:void(0);" class="small editsmall" tabIndex="-1"> <form:checkbox path="sourcingFormApprovalRequests[${status.index}].approvalType" value="AND" cssClass="approval_condition editapproval_condition" label="All"></form:checkbox>
								</a>
							</li>
						</ul>
						<button  class="removeApprovalButton btn btn-plus btn-info hvr-pop hvr-rectangle-out removeApproval  editremoveApproval ${approval.active || approval.done ? 'disabled':''}">
							<i class="glyphicon glyphicon-remove" aria-hidden="true"></i>
						</button>
					</div>
				</div>
			</div>
		</c:forEach>
	</div>
</div>
<style>
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

#eventSummaryForm .approVAlRouteBox {
	max-height: 300px;
	overflow-y: auto;
	overflow-x: hidden;
}

#eventSummaryForm .dynamic-approval {
	width: 100% !important;
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

@media ( max-width : 995px) {
	.btn-address-field {
		width: 100%;
		display: flex;
		margin-left: 1.5rem;
	}
}
</style>
<script type="text/JavaScript">
	var indexx;
	indexx = ${fn:length(sourcingFormRequest.sourcingFormApprovalRequests) + 1};
	$(document).ready(function() {
		$(document).delegate('.editsmall', 'click', function(e) {
			$(this).find(".editapproval_condition").trigger("click");	
		});

		var lockedElement = [${!empty doneUsers ? doneUsers : ''} ''];
		$.each(lockedElement, function(i, item){
			$(".edittagTypeMultiSelect").find('option[selected="selected"][value="'+item+'"]').attr('data-locked',true);			
		});

		$("#addNewSelect").click(function(e) {
			e.preventDefault();
			var template = '<div id="editnew-approval-'+indexx+'" class="row new-approval editnew-approval">';
			template += '<input type="hidden" class="approval_id_hidden editapproval_id_hidden" name="sourcingFormApprovalRequests[' + (indexx - 1) + '].id" value="" />';
			template += '<div class="col-md-2 col-sm-3"><label class="level">Level ' + indexx + '</label></div>';
			template += '<div class="col-md-7 col-sm-7" id="sel">';
			template += '<span class="dropUp"><select data-validation="required" autocomplete="off" id="multipleSelectExample-'+(indexx - 1)+'" name="sourcingFormApprovalRequests[' + (indexx - 1) + '].approvalUsersRequest" class="edittagTypeMultiSelect user-list-all chosen-select tagTypeMultiSelect" data-placeholder="Approvers" multiple>';
			<c:forEach items="${userList}" var="users">
			<c:if test="${users.id != '-1' }">
				template += '<option value="${users.id}" >${users.name}</option>';
			</c:if>	
			<c:if test="${users.id == '-1' }">
				template += '<option value="-1" disabled>${users.name}</option>';
			</c:if>			
			</c:forEach> 
			template += '</select></span></div><div class="col-sm-2 col-md-2 pad0">';
			template += '<div class="btn-address-field pt-0"><button type="button" class="btn dropdown-toggle" data-toggle="dropdown">';
			template += '<i class="fa fa-user" aria-hidden="true"></i></button>';
			template += '<ul class="dropdown-menu dropup">';
			template += '<li><a href="javascript:void(0);" class="small editsmall"  tabIndex="-1"><input name="sourcingFormApprovalRequests[' + (indexx - 1) + '].approvalType" checked="checked" value="OR" class="approval_condition editapproval_condition" type="checkbox"/><label>Any</label></a></li>';
			template += '<li><a href="javascript:void(0);" class="small editsmall" tabIndex="-1"><input name="sourcingFormApprovalRequests[' + (indexx - 1) + '].approvalType" value="AND" class="approval_condition editapproval_condition"  type="checkbox"/><label>All</label></a></li>	</ul>';
			template += '<button class="btn btn-plus btn-info hvr-pop hvr-rectangle-out removeApproval editremoveApproval"> <i class="glyphicon glyphicon-remove" aria-hidden="true"></i> </button></div></div>';
			template += '</div>';

			$(".dynamic-approval").find('.approVAlRouteBox').append(template);
			$('#multipleSelectExample-'+(indexx - 1)).chosen({search_contains:true}).change(function() { $(this).validate(); $(this).blur(); $(this).parent().find('.chosen-search-input').attr('placeholder', ''); });

			updateUserList('',$('#multipleSelectExample-'+(indexx - 1)),'ALL_USER'); //inital add users in drop down 
			$(".approVAlRouteBox").animate({ scrollTop: $(".approVAlRouteBox")[0].scrollHeight}, 1000);
			indexx++;
		});

		$(document).delegate('.editremoveApproval', 'click', function(e) {
			$(this).closest(".editnew-approval").remove();
			$(this).closest(".editapproval_id_hidden").remove();
			$(this).closest(".editnew-approval").empty();
			$(this).closest(".editapproval_id_hidden").empty();
			$(".editnew-approval").each(function(i, v) {
				i++;
				$(this).attr("id", "editnew-approval-" + i);
				$(this).find(".level").text('Level ' + i);
				
				$(this).find(".editapproval_id_hidden").each(function(){					
					$(this).attr("name",'sourcingFormApprovalRequests[' +(i-1) + '].id');					
				});
				
				$(this).find(".editapproval_condition").each(function(){
					$(this).attr("name",'sourcingFormApprovalRequests[' +(i-1) + '].approvalType');
				}) // checkbox name reindex
				
				$(this).find("input[name='_sourcingFormApprovalRequests[" + (i) + "].approvalType']").each(function(){//
					$(this).attr('name','_sourcingFormApprovalRequests[' +(i-1) + '].approvalType');
				}); //Checkbox hidden val reindex
				
				
				$(this).find(".edittagTypeMultiSelect").each(function(){
					$(this).attr("name",'sourcingFormApprovalRequests[' +(i-1) + '].approvalUsersRequest');
					$(this).attr("id", "multipleSelectExample-" + ((i-1)) + "");
				}) //select name reindex
				
				$(this).find("input[name='_sourcingFormApprovalRequests[" + (i) + "].approvalUsersRequest']").each(function(){//
					$(this).attr("name",'_sourcingFormApprovalRequests[' +(i-1) + '].approvalUsersRequest');
				}) //select name reindex

			});
			indexx--;
			
		});

		$(document).delegate('.editapproval_condition', 'click', function(e) {
		
			$(this).closest('.btn-address-field').find(".editapproval_condition").each(function(){
				$(this).prop('checked', false);
		      	//console.log($(this))
			});
		  
			$(this).prop('checked', true);
		 
			var current_val = $(this).val();
		
			if (current_val == "OR") {
				//console.log(current_val);
				$(this).closest('.btn-address-field').find(".dropdown-toggle").addClass('btn-success');
				$(this).closest('.btn-address-field').find(".dropdown-toggle").removeClass('btn-warning');
				$(this).closest('.btn-address-field').find(".dropdown-toggle").html('<i class="fa fa-user" aria-hidden="true"></i>');
			}
			if (current_val == "AND") {
				//console.log(current_val);
				$(this).closest('.btn-address-field').find(".dropdown-toggle").addClass('btn-warning');
				$(this).closest('.btn-address-field').find(".dropdown-toggle").removeClass('btn-success');
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
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/ajaxFormPlugin.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/select.js"/>"></script>

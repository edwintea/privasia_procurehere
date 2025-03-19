<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="/WEB-INF/template-functions.tld" prefix="tf"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<div class="dynamic-suspApproval col-md-10">
	
	<spring:message code="approval.search.user" var="approversplace" />
	<c:if test="${empty rfxTemplate}">
		<div class="row col-md-8 pad_all_20">
			<div class="setleft">
				<spring:message code="rfxtemplate.enable.suspend.approval" var="suspendApproval" />
				<form:checkbox path="enableSuspensionApproval" id="enableSuspensionApproval" class="custom-checkbox" disabled="false" title="" label="${suspendApproval}" />
			</div>
		</div>
	</c:if>
	<c:if test="${(!empty rfxTemplate && rfxTemplate.readOnlySuspendApproval == 'false') || empty rfxTemplate}">
		<div class="row cloneready marg-bottom-10 pad_all_20">
			<div class="col-md-9">
				<button type="button" class="btn btn-plus btn-info hvr-pop " id="addSelectSuspend" ${(!empty rfxTemplate ? (rfxTemplate.readOnlySuspendApproval ? 'disabled="disabled"' : '') : '')} data-placement="top">
					<spring:message code="application.addapproval.button" />
				</button>
			</div>
			<div class="errSuspendApproval help-block form-error text-danger col-md-9" style="display:none;">Suspension Approvals cannot be empty.</div>
		</div>
	</c:if>
	
	<!-- For LOOP -->
	<c:set var="doneUsers" value="" />
	<div class="approvalSuspRouteBox " style="max-height: unset !important; overflow-y: unset !important; overflow-x: unset !important;">
		<c:forEach items="${event.suspensionApprovals}" var="approval" varStatus="status">
			<div id="new-suspApproval-${status.index + 1}" class="row new-suspApproval">
				<c:if test="${approval.active}">
					<c:forEach items="${event.suspensionApprovals[status.index].approvalUsers}" var="usr">
						<c:if test="${usr.approvalStatus != 'PENDING' }">
							<c:set var="doneUsers" value="${doneUsers.concat(\"'\").concat(usr.user.id).concat(\"',\") }" />
						</c:if>
					</c:forEach>
				</c:if>
				<form:hidden class="approval_id_hidden" path="suspensionApprovals[${status.index}].id" />
				<div class="col-md-2 col-sm-3 col-xs-12">
					<label class="level"><spring:message code="rfi.createrfi.level" /> ${status.index + 1}</label>
				</div>
				<div class="col-md-7 col-sm-7 col-xs-8 approval-chosen-multiselect ${ approval.done || (!empty rfxTemplate ? rfxTemplate.readOnlySuspendApproval : false) ? 'disabled':''} suspApprvl">
					<span class="dropUp appr-DP">
					<form:select data-validation="required" id="suspensionMultipleSelect-${status.index}" path="suspensionApprovals[${status.index}].approvalUsers" cssClass="tagTypeMultiSelect user-list-all chosen-select  ${approval.done || (!empty rfxTemplate ? rfxTemplate.readOnlySuspendApproval : false) ? 'disabled':''}" class="susp-apprvl" data-placeholder="${approversplace}" multiple="multiple">
						<c:forEach items="${suspApprvlUserList}" var="usr" >
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
					<div class="btn-address-field pt-0 apprvlType">
						<button type="button" class="btn marg-mins ${approval.approvalType == 'OR' ? 'btn-success' : 'btn-warning'} dropdown-toggle ${ (approval.active || approval.done || (!empty rfxTemplate ? rfxTemplate.readOnlySuspendApproval : false))  ? 'disabled':''}" id="appType" data-toggle="dropdown">
							<i class="fa ${approval.approvalType == 'OR' ? 'fa-user' : 'fa-users'}" aria-hidden="true"></i>
						</button>
						<ul class="dropdown-menu dropup">
							<li><a href="javascript:void(0);" class="small" tabIndex="-1"> <form:checkbox path="suspensionApprovals[${status.index}].approvalType" value="OR" cssClass="approval_condition" label="Any"></form:checkbox>
							</a></li>
							<li><a href="javascript:void(0);" class="small " tabIndex="-1"> <form:checkbox path="suspensionApprovals[${status.index}].approvalType" value="AND" cssClass="approval_condition" label="All"></form:checkbox>
							</a></li>
						</ul>
						<button class="btn btn-plus btn-info hvr-pop hvr-rectangle-out suspRemoveApproval  ${approval.active || approval.done  || (!empty rfxTemplate ? rfxTemplate.readOnlySuspendApproval : false ) ? 'disabled':''}">
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

#eventSummaryForm .approvalSuspRouteBox {
	max-height: 300px;
	overflow-y: auto;
	overflow-x: hidden;
}

#eventSummaryForm .dynamic-suspApproval {
	width: 100% !important;
}

.row.dynamic-suspApproval {
	margin-top: 10px;
}

.new-suspApproval label {
	/* float: right; */
	padding: 15px 0;
}

.btn-address-field {
	padding-top: 10px;
	margin-left: -15px;
}

.suspRemoveApproval {
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

.mr-tp {
   margin-top: 7px;
}

.wd-cl {
    width: 207px;
}

.pd-wd {
    width: 80px;
    padding: inherit;
}

.mr-tp-wd {
    width: 95px;
    margin-top: 5px;
}
@media ( max-width : 995px) {
	.btn-address-field {
		width: 100%;
		display: flex;
		margin-left: 1.5rem;
	}
}
.col-sm-remi {
    position: relative;
    min-height: 1px;
    padding-right: 15px;
    padding-left: 15px;
}
@media (min-width: 768px)
.notify-change{
    width: 25%;
}
</style>
<script type="text/JavaScript">


function autoSave(){
	<c:if test="${event.status =='DRAFT'}">
	//$("#demo-form1").ajaxSubmit({url: getBuyerContextPath('autoSaveDraft'), type: 'post'})
	</c:if>
}
	var cnt;
	cnt = ${fn:length(event.suspensionApprovals) + 1};
	
	$(document).ready(function() {
		
		$(document).delegate('.small', 'click', function(e) {
			//console.log($(this).find(".approval_condition"));
			$(this).find(".approval_condition").trigger("click");	
		});

		
		//.collapse('toggle')
		/* Prevent To Remove In Edit Mode */
		var lockedElement = [${!empty doneUsers ? doneUsers : ''} ''];
		$.each(lockedElement, function(i, item){
			$(".tagTypeMultiSelect").find('option[selected="selected"][value="'+item+'"]').attr('data-locked',true);			
		});
		

	//	document.getElementById("addSelect").addEventListener("click", function(e) {
		$("#addSelectSuspend").click(function(e) {
			e.preventDefault();
			$('.errSuspendApproval').hide();
			var template = '<div id="new-suspApproval-' + cnt + '" class="row new-suspApproval">';
			template += '<input type="hidden" class="approval_id_hidden" name="suspensionApprovals[' + (cnt - 1) + '].id" value="" />';
			template += '<div class="col-md-2 col-sm-3"><label class="level">Level ' + cnt + '</label></div>';
			template += '<div class="col-md-7 col-sm-7 col-xs-8 approval-chosen-multiselect" id="suspSel">';
			template += '<span class="dropUp"><select data-validation="required" id="suspensionMultipleSelect-'+(cnt - 1)+'" name="suspensionApprovals[' + (cnt - 1) + '].approvalUsers" class="tagTypeMultiSelect chosen-select user-list-all" data-placeholder="${approversplace}" multiple onclick="autoSave()">';
			<c:forEach items="${suspUserList}" var="users">
				<c:if test="${users.id == '-1' }">
					template += '<option value="-1" disabled >${users.user.name}</option>';
				</c:if>
				<c:if test="${users.id != '-1' }">
					template += '<option value="${users.id}" >${users.user.name}</option>';
				</c:if>
			</c:forEach>
			template += '</select></span></div><div class="col-sm-6 col-md-3 col-xs-4">';
			template += '<div class="btn-address-field pt-0"><button type="button" class="btn marg-mins btn-success dropdown-toggle" data-toggle="dropdown">';
			template += '<i class="fa fa-user" aria-hidden="true"></i></button>';
			template += '<ul class="dropdown-menu dropup">';
			template += '<li><a href="javascript:void(0);" class="small"  tabIndex="-1"><input name="suspensionApprovals[' + (cnt - 1) + '].approvalType" checked="checked" value="OR" class="approval_condition" type="checkbox"/><label>Any</label></a></li>';
			template += '<li><a href="javascript:void(0);" class="small " tabIndex="-1"><input name="suspensionApprovals[' + (cnt - 1) + '].approvalType" value="AND" class="approval_condition"  type="checkbox"/><label>All</label></a></li>	</ul>';
			template += '<button class="btn btn-plus btn-info hvr-pop hvr-rectangle-out suspRemoveApproval"> <i class="glyphicon glyphicon-remove" aria-hidden="true"></i> </button></div></div>';
			template += '</div>';

			$(".dynamic-suspApproval").find('.approvalSuspRouteBox').append(template);
			
			$('#suspensionMultipleSelect-'+(cnt - 1)).chosen({search_contains:true}).change(function() { $(this).validate(); $(this).blur(); $(this).parent().find('.chosen-search-input').attr('placeholder', ''); });	
			
			updateUserList('',$('#suspensionMultipleSelect-'+(cnt - 1)),'ALL_USER'); //inital add users in drop down 

			$(".approvalSuspRouteBox").animate({ scrollTop: $(".approvalSuspRouteBox")[0].scrollHeight}, 1000);
			cnt++;
		});

		$(document).delegate('.suspRemoveApproval', 'click', function(e) {
			$(this).closest(".new-suspApproval").remove();
			$(this).closest(".approval_id_hidden").remove();

			$(".new-suspApproval").each(function(i, v) {
				i++;
				$(this).attr("id", "new-suspApproval-" + i);
				$(this).find(".level").text('Level ' + i);
				
				$(this).find(".approval_id_hidden").each(function(){					
					$(this).attr("name",'suspensionApprovals[' +(i-1) + '].id');					
				});
				
				$(this).find(".approval_condition").each(function(){
					$(this).attr("name",'suspensionApprovals[' +(i-1) + '].approvalType');
				}) // checkbox name reindex
				
				$(this).find("input[name='_suspensionApprovals[" + i + "].approvalType']").each(function(){
					$(this).attr('name','_suspensionApprovals[' +(i-1) + '].approvalType');
				}); //Checkbox hidden val reindex
				
				
				$(this).find(".tagTypeMultiSelect").each(function(){
					$(this).attr("name",'suspensionApprovals[' +(i-1) + '].approvalUsers');
					$(this).attr("id", "suspensionMultipleSelect-" + ((i-1)) + "");
				}) //select name reindex
				
				$(this).find("input[name='_suspensionApprovals[" + i + "].approvalUsers']").each(function(){
					$(this).attr("name",'_suspensionApprovals[' +(i-1) + '].approvalUsers');
				}) //select name reindex hidden
				/*
				$(this).find("input[name='_approvals[" + i + "].approvalType']").each(function(){
					$(this).attr('name','_approvals[' +(i-1) + '].approvalType');
				});
				$(this).attr("name",'_approvals[' +(i-1) + '].approvalType');
				$(this).find(".tagTypeMultiSelect").attr("name",'approvals[' + (i-1) + '].approvalUsers');
				$(this).find(".approval_condition").each(function(){
					$(this).attr("name",'approvals[' +(i-1) + '].approvalType');
				})
				*/
			});
			cnt--;
			
			<c:if test="${event.status =='DRAFT'}">
			/* $("#demo-form1").ajaxSubmit({url: getBuyerContextPath('autoSaveDraft'), type: 'post'}) */
			</c:if>
			
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
		
		<c:if test="${empty rfxTemplate}">
			if($('#enableSuspensionApproval').is(':checked')){
				$('#addSelectSuspend').removeClass('disabled');
			}else{
				$('#addSelectSuspend').addClass('disabled');
			}
		</c:if>
		
		 $('#enableSuspensionApproval').change(function() {
		     if(this.checked) {
		    	 $('#addSelectSuspend').removeClass('disabled');
		     }else {
		    	 $('#addSelectSuspend').addClass('disabled');
		    	
		    	 console.log("cnt be4 removal "+cnt);
		    	 $('.suspRemoveApproval').closest(".new-suspApproval").remove();
			     $('.suspRemoveApproval').closest(".approval_id_hidden").remove();

				$(".new-suspApproval").each(function(i, v) {
					i++;
					$(this).attr("id", "new-suspApproval-" + i);
					$(this).find(".level").text('Level ' + i);
					
					$(this).find(".approval_id_hidden").each(function(){					
						$(this).attr("name",'suspensionApprovals[' +(i-1) + '].id');					
					});
					
					$(this).find(".approval_condition").each(function(){
						$(this).attr("name",'suspensionApprovals[' +(i-1) + '].approvalType');
					}) // checkbox name reindex
					
					$(this).find("input[name='_suspensionApprovals[" + i + "].approvalType']").each(function(){
						$(this).attr('name','_suspensionApprovals[' +(i-1) + '].approvalType');
					}); //Checkbox hidden val reindex
					
					
					$(this).find(".tagTypeMultiSelect").each(function(){
						$(this).attr("name",'suspensionApprovals[' +(i-1) + '].approvalUsers');
						$(this).attr("id", "suspensionMultipleSelect-" + ((i-1)) + "");
					}) //select name reindex
					
					$(this).find("input[name='_suspensionApprovals[" + i + "].approvalUsers']").each(function(){
						$(this).attr("name",'_suspensionApprovals[' +(i-1) + '].approvalUsers');
					}) //select name reindex hidden
						
				});
				cnt = 1;
		     
		     }
		 });

	});
	
	
	window.onload = function() {
		//Register onclick event for button after loading document and dom
		document.getElementById("addSelectSuspend").addEventListener("click", function() {

		});
	};

	//Create a dynamic Select
	function createSelect() {

		var suspSel = document.getElementById("suspSel");
		//alert("hello");
		//Create array of options to be added
		var array = [ "Steven White", "Nancy King", "Nancy Davolio", "Michael Leverling", "Michael Suyama" ];

		//Create and append select list
		var selectList = document.createElement("select");
		selectList.setAttribute("id", "mySelect");
		selectList.setAttribute("multiple", "");
		selectList.setAttribute("placeholder", "Approvers");

		suspSel.appendChild(selectList);
		$(".ApprovalOption").first().clone().appendTo(suspSel);
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

<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="/WEB-INF/template-functions.tld" prefix="tf"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
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

#eventSummaryForm .approvalAwardRouteBox {
	max-height: 300px;
	overflow-y: auto;
	overflow-x: hidden;
}

#eventSummaryForm .dynamic-awardApproval {
	width: 100% !important;
}

.row.dynamic-awardApproval {
	margin-top: 10px;
}

.new-awardApproval label {
	/* float: right; */
	padding: 15px 0;
}

.btn-address-field {
	padding-top: 10px;
	margin-left: -15px;
}

.awardRemoveApproval {
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
<div class="dynamic-awardApproval row">
	
	<spring:message code="approval.search.user" var="approversplace"/>
	<c:if test="${empty rfxTemplate or  (!empty rfxTemplate and rfxTemplate.enableAwardApproval)}">
		<div class="row col-md-8 marg-bottom-10 ${(!empty rfxTemplate and (rfxTemplate.readOnlyAwardApproval or !rfxTemplate.optionalAwardApproval) ) or event.awardStatus == 'PENDING' or event.awardStatus == 'APPROVED' ? 'disabled' : ''}">
			<spring:message code="rfxtemplate.enable.award.approval" var="awardApproval" />
			<form:checkbox path="rfxEvent.enableAwardApproval" id="enableAwardApproval" cssClass="custom-checkbox" title="${awardApproval}" label="${awardApproval}" />
		</div>
	</c:if>
	<c:if test="${(!empty rfxTemplate && rfxTemplate.readOnlyAwardApproval == 'false') or empty rfxTemplate}">
		<div class="row cloneready marg-bottom-10 ">
			<div class="col-md-9 ">
				<button type="button" class="btn btn-plus btn-info hvr-pop ${(!empty rfxTemplate and rfxTemplate.readOnlyAwardApproval) or !eventAward.rfxEvent.enableAwardApproval ? 'disabled' : '' }" id="addSelectAward1" data-placement="top">
					<spring:message code="application.addapproval.button" />
				</button>
			</div>
		</div>
		<div id="idApprovalLevelError" class="text-danger marg-left-20" style="display:none;">You must define at least one Approval Level if approval is enabled.</div>
	</c:if>
	
	<!-- For LOOP -->
	<c:set var="doneUsers" value="" />
	<div class="approvalAwardRouteBox " style="max-height: unset !important; overflow-y: unset !important; overflow-x: unset !important;">
		<c:forEach items="${event.awardApprovals}" var="approval" varStatus="status">
			<div id="new-awardApproval-${status.index + 1}" class="row new-awardApproval">
				<c:if test="${approval.active}">
					<c:forEach items="${event.awardApprovals[status.index].approvalUsers}" var="usr">
						<c:if test="${usr.approvalStatus != 'PENDING' }">
							<c:set var="doneUsers" value="${doneUsers.concat(\"'\").concat(usr.user.id).concat(\"',\") }" />
						</c:if>
					</c:forEach>
				</c:if>
				<form:hidden class="approval_id_hidden" path="awardApprovals[${status.index}].id" />
				<div class="col-md-2 col-sm-3 col-xs-12">
					<label class="level"><spring:message code="rfi.createrfi.level" /> ${status.index + 1}</label>
				</div>
				<div class="col-md-7 col-sm-7 col-xs-8 approval-chosen-multiselect ${ approval.done || (!empty rfxTemplate and rfxTemplate.readOnlyAwardApproval) ? 'disabled':''} awardpApprvl">
					<span class="dropUp appr-DP">
					<form:select id="awardMultipleSelect-${status.index}" path="awardApprovals[${status.index}].approvalUsers" data-validation="required" cssClass="tagTypeMultiSelect user-list-all chosen-select awardpApprvl ${approval.done || (!empty rfxTemplate ? rfxTemplate.readOnlyAwardApproval : false) ? 'disabled':''}" data-placeholder="${approversplace}" multiple="multiple">
						<c:forEach items="${userList1}" var="usr" >
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
					<div class="btn-address-field pt-0 apprvlType ${(!empty rfxTemplate and rfxTemplate.readOnlyAwardApproval) ? 'disabled' : ''}">
						<button type="button" class="btn marg-mins ${approval.approvalType == 'OR' ? 'btn-success' : 'btn-warning'} dropdown-toggle ${ (approval.active || approval.done || (!empty rfxTemplate and rfxTemplate.readOnlyAwardApproval))  ? 'disabled':''}" id="appType" data-toggle="dropdown">
							<i class="fa ${approval.approvalType == 'OR' ? 'fa-user' : 'fa-users'}" aria-hidden="true"></i>
						</button>
						<ul class="dropdown-menu dropup">
							<li>
								<a href="javascript:void(0);" class="small" tabIndex="-1"> 
									<form:checkbox path="awardApprovals[${status.index}].approvalType" value="OR" cssClass="approval_condition" label="Any" />
								</a>
							</li>
							<li>
								<a href="javascript:void(0);" class="small " tabIndex="-1"> 
									<form:checkbox path="awardApprovals[${status.index}].approvalType" value="AND" cssClass="approval_condition" label="All" />
								</a>
							</li>
						</ul>
						<button class="btn btn-plus btn-info hvr-pop hvr-rectangle-out awardRemoveApproval  ${approval.active || approval.done  || (!empty rfxTemplate ? rfxTemplate.readOnlyAwardApproval : false ) ? 'disabled':''}">
							<i class="glyphicon glyphicon-remove" aria-hidden="true"></i>
						</button>
					</div>
				</div>
			</div>
		</c:forEach>
	</div>
</div>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/select.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/ajaxFormPlugin.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/select.js"/>"></script>
<script type="text/JavaScript">
function autoSave(){
	<c:if test="${event.status =='COMPLETE'}">
	//$("#demo-form1").ajaxSubmit({url: getBuyerContextPath('autoSaveDraft'), type: 'post'})
	</c:if>
}
	var awcnt;
	awcnt = ${fn:length(event.awardApprovals) + 1};
	
	$(document).ready(function() {
		$.uniform.update();
		$('#idApprovalLevelError').hide();
		
		$(document).delegate('.small', 'click', function(e) {
			//console.log($(this).find(".approval_condition"));
			$(this).find(".approval_condition").trigger("click");	
		});

		
		var lockedElement = [${!empty doneUsers ? doneUsers : ''} ''];
		$.each(lockedElement, function(i, item){
			$(".tagTypeMultiSelect").find('option[selected="selected"][value="'+item+'"]').attr('data-locked',true);			
		});
		

		$("#addSelectAward1").click(function(e) {
			console.log("awcnt : " + awcnt);
			e.preventDefault();
	 		var template = '<div id="new-awardApproval-' + awcnt + '" class="row new-awardApproval">';
			template += '<input type="hidden" class="approval_id_hidden" name="awardApprovals[' + (awcnt - 1) + '].id" value="" />';
			template += '<div class="col-md-2 col-sm-3"><label class="level">Level ' + awcnt + '</label></div>';
			template += '<div class="col-md-7 col-sm-7 col-xs-8 approval-chosen-multiselect" id="awardSel">';
			template += '<span class="dropUp"><select id="awardMultipleSelect-'+(awcnt - 1)+'" name="awardApprovals[' + (awcnt - 1) + '].approvalUsers" data-validation="required" class="tagTypeMultiSelect chosen-select user-list-all awardpApprvl" data-placeholder="${approversplace}" multiple onclick="autoSave()">';
			<c:forEach items="${userList}" var="users">
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
			template += '<li><a href="javascript:void(0);" class="small"  tabIndex="-1"><input name="awardApprovals[' + (awcnt - 1) + '].approvalType" checked="checked" value="OR" class="approval_condition" type="checkbox"/><label>Any</label></a></li>';
			template += '<li><a href="javascript:void(0);" class="small " tabIndex="-1"><input name="awardApprovals[' + (awcnt - 1) + '].approvalType" value="AND" class="approval_condition"  type="checkbox"/><label>All</label></a></li>	</ul>';
			template += '<button class="btn btn-plus btn-info hvr-pop hvr-rectangle-out awardRemoveApproval"> <i class="glyphicon glyphicon-remove" aria-hidden="true"></i> </button></div></div>';
			template += '</div>';

			$(".dynamic-awardApproval").find('.approvalAwardRouteBox').append(template);
			
			$('#awardMultipleSelect-'+(awcnt - 1)).chosen({search_contains:true}).change(function() { $(this).validate(); $(this).blur(); $(this).parent().find('.chosen-search-input').attr('placeholder', ''); });	
			
			updateUserList('',$('#awardMultipleSelect-'+(awcnt - 1)),'ALL_USER'); //inital add users in drop down 

			$(".approvalAwardRouteBox").animate({ scrollTop: $(".approvalAwardRouteBox")[0].scrollHeight}, 1000); 
			awcnt++;
			
			$('#idApprovalLevelError').hide();
			console.log("Added level " + awcnt);
		});

		$(document).delegate('.awardRemoveApproval', 'click', function(e) {
			$(this).closest(".new-awardApproval").remove();
			$(this).closest(".approval_id_hidden").remove();

			$(".new-awardApproval").each(function(i, v) {
				i++;
				$(this).attr("id", "new-awardApproval-" + i);
				$(this).find(".level").text('Level ' + i);
				
				$(this).find(".approval_id_hidden").each(function(){					
					$(this).attr("name",'awardApprovals[' +(i-1) + '].id');					
				});
				
				$(this).find(".approval_condition").each(function(){
					$(this).attr("name",'awardApprovals[' +(i-1) + '].approvalType');
				}) // checkbox name reindex
				
				$(this).find("input[name='_awardApprovals[" + i + "].approvalType']").each(function(){
					$(this).attr('name','_awardApprovals[' +(i-1) + '].approvalType');
				}); //Checkbox hidden val reindex
				
				
				$(this).find(".tagTypeMultiSelect").each(function(){
					$(this).attr("name",'awardApprovals[' +(i-1) + '].approvalUsers');
					$(this).attr("id", "awardMultipleSelect-" + ((i-1)) + "");
				}) //select name reindex
				
				$(this).find("input[name='_awardApprovals[" + i + "].approvalUsers']").each(function(){
					$(this).attr("name",'_awardApprovals[' +(i-1) + '].approvalUsers');
				}) 
			});
			awcnt--;
			
			<c:if test="${event.status =='DRAFT'}">
			</c:if>
			
			if($('#enableAwardApproval').is(':checked') && $('.awardpApprvl').length == 0) {
				$('#idApprovalLevelError').show();
			}
		});

		$(document).delegate('.approval_condition', 'click', function(e) {
		
			$(this).closest('.btn-address-field').find(".approval_condition").each(function(){
				$(this).prop('checked', false);
			});
		  
			$(this).prop('checked', true);
		 
			var current_val = $(this).val();
		
			if (current_val == "OR") {
				$(this).closest('.btn-address-field').find(".dropdown-toggle").addClass('btn-success');
				$(this).closest('.btn-address-field').find(".dropdown-toggle").removeClass('btn-warning');
				$(this).closest('.btn-address-field').find(".dropdown-toggle").html('<i class="fa fa-user" aria-hidden="true"></i>');
			}
			if (current_val == "AND") {
				$(this).closest('.btn-address-field').find(".dropdown-toggle").addClass('btn-warning');
				$(this).closest('.btn-address-field').find(".dropdown-toggle").removeClass('btn-success');
				$(this).closest('.btn-address-field').find(".dropdown-toggle").html('<i class="fa fa-users" aria-hidden="true"></i>');
			}
		});
		
		<c:if test="${empty rfxTemplate}">
			if($('#enableAwardApproval').is(':checked')){
				$('#addSelectAward1').removeClass('disabled');
			}else{
				$('#addSelectAward1').addClass('disabled');
			}
		</c:if>
		
		 $('#enableAwardApproval').change(function() {
		     if(this.checked) {
		    	 console.log("Approval Checkbox checked...");
		    	 $('#addSelectAward1').removeClass('disabled');
		     }else {
		    	 $('#idApprovalLevelError').hide();
		    	 $('#addSelectAward1').addClass('disabled');
		    	
		    	 console.log("awcnt be4 removal "+awcnt);
		    	 $('.awardRemoveApproval').closest(".new-awardApproval").remove();
			     $('.awardRemoveApproval').closest(".approval_id_hidden").remove();

				$(".new-awardApproval").each(function(i, v) {
					i++;
					$(this).attr("id", "new-awardApproval-" + i);
					$(this).find(".level").text('Level ' + i);
					
					$(this).find(".approval_id_hidden").each(function(){					
						$(this).attr("name",'awardApprovals[' +(i-1) + '].id');					
					});
					
					$(this).find(".approval_condition").each(function(){
						$(this).attr("name",'awardApprovals[' +(i-1) + '].approvalType');
					}) // checkbox name reindex
					
					$(this).find("input[name='_awardApprovals[" + i + "].approvalType']").each(function(){
						$(this).attr('name','_awardApprovals[' +(i-1) + '].approvalType');
					}); //Checkbox hidden val reindex
					
					
					$(this).find(".tagTypeMultiSelect").each(function(){
						$(this).attr("name",'awardApprovals[' +(i-1) + '].approvalUsers');
						$(this).attr("id", "awardMultipleSelect-" + ((i-1)) + "");
					}) //select name reindex
					
					$(this).find("input[name='_awardApprovals[" + i + "].approvalUsers']").each(function(){
						$(this).attr("name",'_awardApprovals[' +(i-1) + '].approvalUsers');
					}) //select name reindex hidden
						
				});
				awcnt = 1;
		     
		     }
		 });

	});
	
	/* 
	window.onload = function() {
		document.getElementById("addSelectAward1").addEventListener("click", function() {
			console.log("windows onload....");
		});
	}; */

	//Create a dynamic Select
	function createSelect() {

		var awardSel = document.getElementById("awardSel");
		var array = [ "Steven White", "Nancy King", "Nancy Davolio", "Michael Leverling", "Michael Suyama" ];

		//Create and append select list
		var selectList = document.createElement("select");
		selectList.setAttribute("id", "mySelect");
		selectList.setAttribute("multiple", "");
		selectList.setAttribute("placeholder", "Approvers");

		awardSel.appendChild(selectList);
		$(".ApprovalOption").first().clone().appendTo(awardSel);
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

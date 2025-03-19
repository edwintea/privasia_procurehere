<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="/WEB-INF/template-functions.tld" prefix="tf"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize access="hasRole('ADMIN')" var="isAdmin" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<c:if test="${supplierForm.approvalStatus eq 'APPROVED' && supplierForm.status eq 'SUBMITTED' && (eventPermissions.owner or isAdmin)}">
		<form:form class="bordered-row" id="demo-form1" method="post" modelAttribute="supplierForm" action="${pageContext.request.contextPath}/buyer/saveSuppFormAddtionalApproval/${supplierForm.id}">
			<div class="panel sum-accord  " style="margin-top: 0%;">
				<div class="panel-heading">
					<h4 class="panel-title">
						<a data-toggle="collapse" class="accordion collAdditionalApprove" href="#collapseApproval"> <spring:message code="additional.approvals.route" />
						</a>
					</h4>
				</div>
				<div id="collapseApproval" class="panel-collapse collapse">
					<div class="dynamic-Add-approval col-md-6">
						<div class="row cloneready">
						<c:if test="${eventPermissions.owner or isAdmin}">
								<div class="col-md-3 col-sm-3">
									<button type="button" class="btn btn-plus btn-info hvr-pop hvr-rectangle-out" id="addAddiApproval" data-placement="top" style="margin-top: 10px;">
										<spring:message code="add.approval.level" />
									</button>
								</div>
						</c:if>
						</div>
						<!-- For LOOP -->
						<c:set var="doneUsers" value="" />
						<c:set var="doneLevel" value="" />
						<div class="approVAlRouteBox">
							<c:forEach items="${supplierForm.approvals}" var="approval" varStatus="status">
								<c:if test="${approval.done}">
									<form:hidden path="approvals[${status.index}].id" />
									<c:forEach items="${supplierForm.approvals[status.index].approvalUsers}" var="usr" varStatus="usrIndex">
										<input type="hidden" name="approvals[${status.index}].approvalUsers" value="${usr.user.id}" />
									</c:forEach>
								</c:if>
								<c:if test="${!approval.done}">
									<div id="new-addApproval-${status.index + 1}" class="row new-approval addApproval">
										<c:if test="${approval.active}">
											<c:forEach items="${supplierForm.approvals[status.index].approvalUsers}" var="usr">
												<c:if test="${usr.approvalStatus != 'PENDING' }">
													<c:set var="doneUsers" value="${doneUsers.concat(\"'\").concat(usr.user.id).concat(\"',\") }" />
													<c:set var="doneLevel" value="${supplierForm.approvals[status.index].id}" />
												</c:if>
											</c:forEach>

										</c:if>
										<div class="col-md-2 col-sm-2 mb-0">
											<label class="level">Level ${approval.level }</label>
										</div>
										<div class="col-md-7 col-sm-7" id="sel">
											<form:hidden class="addapproval_id_hidden" path="approvals[${status.index}].id" />
											<form:hidden class="addlevel_hidden" path="approvals[${status.index}].level" />
											<span class="dropUp"> <form:select autocomplete="off" path="approvals[${status.index}].approvalUsers" cssClass="user-list-all chosen-select tagTypeMultiSelect  addtagTypeMultiSelect" data-validation="required" id="multipleSelectExample-${status.index}" data-placeholder="Approvers" multiple="multiple">
													<c:forEach items="${userList}" var="user">
														<c:if test="${user.id != '-1' }">
															<form:option value="${user.id}">${user.name}</form:option>
														</c:if>
														<c:if test="${user.id == '-1' }">
															<form:option value="-1" disabled="true">${user.name}</form:option>
														</c:if>
													</c:forEach>
												</form:select>
											</span>
										</div>
										<div class="col-sm-2 col-md-3 col-xs-3 pad0">
											<div class="btn-address-field pt-0">
												<button type="button" class="btn dropdown-toggle" data-toggle="dropdown">
													<i class="fa ${approval.approvalType == 'OR' ? 'fa-user' : 'fa-users'}" aria-hidden="true"></i>
												</button>
												<ul class="dropdown-menu dropup">
													<li>
														<a href="javascript:void(0);" class="small smalladd" tabIndex="-1"> <form:checkbox path="approvals[${status.index}].approvalType" value="OR" cssClass="approval_condition addapproval_condition" label="Any"></form:checkbox>
														</a>
													</li>
													<li>
														<a href="javascript:void(0);" class="small smalladd" tabIndex="-1"> <form:checkbox path="approvals[${status.index}].approvalType" value="AND" cssClass="approval_condition addapproval_condition" label="All"></form:checkbox>
														</a>
													</li>
												</ul>
												<button class="btn btn-plus btn-info hvr-pop hvr-rectangle-out removeApproval removeAddApproval ${(approval.active || approval.done) ? 'disabled':''}">
													<i class="glyphicon glyphicon-remove" aria-hidden="true"></i>
												</button>
											</div>
										</div>
									</div>
								</c:if>

							</c:forEach>
						</div>
					</div>

					<c:if test="${eventPermissions.owner or isAdmin}">
						<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1 ">
							<button type="submit" id="saveAddApprove" class="btn btn-info pull-left ph_btn_small hvr-pop hvr-rectangle-out" title="Save">
								<spring:message code="application.save" />
							</button>
							<button id="finishAdditionalApproval" type="button" class="btn btn-info pull-left ph_btn_small hvr-pop hvr-rectangle-out" title="Finish">
								<spring:message code="application.finish" />
							</button>
						</div>
					</c:if>
				</div>
		</form:form>
	</c:if>
<script type="text/JavaScript">
	var addIndex = ${additionalLevelNext};
	console.log("additionalLevelNext : " + addIndex);
	//var indexlvl = ${additionalLevelNext};
	
	var defaultIndex=${additionalLevelStart};
	
	$(document).ready(function() {
		
		$(document).delegate('.smalladd', 'click', function(e) {
			$(this).find(".addapproval_condition").trigger("click");	
		});

		//.collapse('toggle')
		/* Prevent To Remove In Edit Mode */
		var lockedLevel = '${doneLevel}';
		var lockedElement = [${!empty doneUsers ? doneUsers : ''} ''];
		$.each(lockedElement, function(i, item){
			$(".addtagTypeMultiSelect").find('option[selected="selected"][value="'+item+'"]').attr('data-locked',true);			
		});
		
		$(document).delegate('#addAddiApproval', 'click', function(e) {
			console.log("additionalLevelNext : " + addIndex );
			e.preventDefault();
			var template = '<div id="new-addApproval-'+addIndex+'" class="row new-approval addApproval">';
			template += '<input type="hidden" class="addapproval_id_hidden" name="approvals[' + (addIndex - 1) + '].id" value="" />';
			template += '<input type="hidden" class="addlevel_hidden" name="approvals[' + (addIndex - 1) + '].level" value="' + addIndex + '" />';
			template += '<div class="col-md-2 col-sm-2"><label class="level mb-0">Level ' + addIndex + '</label></div>';
			template += '<div class="col-md-7 col-sm-7" id="sel">';
			template += '<span class="dropUp"><select data-validation="required" autocomplete="off" id="multipleSelectExample-'+(addIndex - 1)+'" name="approvals[' + (addIndex - 1) + '].approvalUsers" class="chosen-select user-list-all tagTypeMultiSelect addtagTypeMultiSelect" data-placeholder="Approvers" multiple>';
			<c:forEach items="${formApprovalUserList}" var="users">
				<c:if test="${users.id != '-1' }">
					template += '<option value="${users.id}" >${users.name}</option>';
				</c:if>
				<c:if test="${users.id == '-1' }">
					template += '<option value="-1"  disabled="true">${users.name}</option>';
				</c:if>
			</c:forEach> 
			template += '</select></span></div><div class="col-sm-2 col-md-2 pad0">';
			template += '<div class="btn-address-field pt-0"><button type="button" class="btn dropdown-toggle" data-toggle="dropdown">';
			template += '<i class="fa fa-user" aria-hidden="true"></i></button>';
			template += '<ul class="dropdown-menu dropup">';
			template += '<li><a href="javascript:void(0);" class="small smalladd"  tabIndex="1"><input name="approvals[' + (addIndex - 1) + '].approvalType" checked="checked" value="OR" class="approval_condition addapproval_condition" type="checkbox"/><label>Any</label></a></li>';
			template += '<li><a href="javascript:void(0);" class="small smalladd" tabIndex="1"><input name="approvals[' + (addIndex - 1) + '].approvalType" value="AND" class="approval_condition addapproval_condition"  type="checkbox"/><label>All</label></a></li>	</ul>';
			template += '<button class="btn btn-plus btn-info hvr-pop hvr-rectangle-out removeApproval removeAddApproval"> <i class="glyphicon glyphicon-remove" aria-hidden="true"></i> </button></div></div>';
			template += '</div>';

			$(".dynamic-Add-approval").find('.approVAlRouteBox').append(template);

			$('#multipleSelectExample-'+(addIndex - 1)).chosen({search_contains:true}).change(function() { $(this).validate(); $(this).blur(); $(this).parent().find('.chosen-search-input').attr('placeholder', ''); });	

			//updateUserList('',$('#multipleSelectExample-'+(index - 1)),'ALL_USER'); //inital add users in drop down 
			$(".approVAlRouteBox").animate({ scrollTop: $(".approVAlRouteBox")[0].scrollHeight}, 1000);
			addIndex++;
		});

		$(document).delegate('.removeAddApproval', 'click', function(e) {
			e.preventDefault();
			$(this).closest(".addApproval").remove();
			$(this).closest(".addapproval_id_hidden").remove();	
			$(this).closest(".addlevel_hidden").remove();	
			var ind=defaultIndex;
			console.log('Default Index ', defaultIndex);

			$(".addApproval").each(function(i, v) {
				$(this).attr("id", "new-addApproval-" + ind);
				$(this).find(".level").text('Level ' + ind);

				
				$(this).find(".addapproval_id_hidden").each(function(){					
					$(this).attr("name",'approvals[' +(ind-1) + '].id');
				});
				
				$(this).find(".addlevel_hidden").each(function(){					
					$(this).attr("name",'approvals[' +(ind-1) + '].level');
					$(this).val(ind);
				});
				
				$(this).find(".addapproval_condition").each(function(){
					$(this).attr("name",'approvals[' +(ind-1) + '].approvalType');
				}); // checkbox name reindex
				
				$(this).find("input[name='approvals[" + (ind) + "].approvalType']").each(function(){//
					$(this).attr('name','approvals[' +(ind-1) + '].approvalType');
				}); //Checkbox hidden val reindex
				
				
				$(this).find(".addtagTypeMultiSelect").each(function(){
					$(this).attr("name",'approvals[' +(ind-1) + '].approvalUsers');
					$(this).attr("id", "multipleSelectExample-" + ((i-1)) + "");
				}) //select name reindex
				
				$(this).find("input[name='approvals[" + (ind) + "].approvalUsers']").each(function(){//
					$(this).attr("name",'approvals[' +(ind-1) + '].approvalUsers');
				}) //select name reindex
				
				ind++;
			});
			addIndex--;
		});

		$(document).delegate('.addapproval_condition', 'click', function(e) {
		
			$(this).closest('.btn-address-field').find(".addapproval_condition").each(function(){
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
	
	$(document).delegate('#finishAdditionalApproval', 'click', function(e) {
		$("#demo-form1").attr('action', getContextPath()+'/buyer/finishSuppFormAdditionalApproval/${supplierForm.id}');
		$("#demo-form1").submit();
	});
	
</script>

<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/select.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/select.js"/>"></script>

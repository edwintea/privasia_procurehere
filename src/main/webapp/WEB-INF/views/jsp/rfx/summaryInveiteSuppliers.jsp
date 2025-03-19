<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authentication property="principal.languageCode" var="languageCode" />
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/privasia1.css"/>">
<sec:authorize var="isAdmin" access="hasRole('ADMIN')" />
<div class="panel sum-accord">

	<div class="panel-heading">
		<h4 class="panel-title">
			<a data-toggle="collapse" class="accordion" href="#collapseFour"><spring:message code="eventsummary.invited.supplier.title" /></a>
		</h4>
	</div>
	<div id="collapseFour" class="panel-collapse collapse">
		<div class="panel-body">
		<c:if test="${event.eventVisibility!='PRIVATE'}">
		<span class="selfinvitenote">&nbsp;&nbsp;&nbsp;Note:&nbsp;</span>&nbsp;&nbsp;<span class="selfinvite">&nbsp;&nbsp;&nbsp;Self Invited&nbsp;</span>
		</c:if>
			<c:if test="${ (!empty event.addSupplier and event.addSupplier) and (event.status eq 'ACTIVE' or event.status eq 'APPROVED') and (eventPermissions.owner or isAdmin) and eventType != 'RFA' }">
				<button class="btn btn-sm btn-info hvr-pop marg-right-10 add_Supplier pull-right" id="addSupplierError" style="margin-bottom: 15px;" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="tooltip.add.supplier" />' type="submit">
					<span class="button-content"><spring:message code="supplier.add" /></span>
				</button>
			</c:if>
			<div class="table-responsive width100 borderAllData">
				<div class="Invited-Supplier-List dashboard-main tabulerDataList myTaskData">
					<div class="Invited-Supplier-List-table add-supplier ">
						<div class="ph_tabel_wrapper">
							<div class="col-md-12">
								<div class="main_table_wrapper  ">
									<table id="invitedsupplierList" class="tabaccor padding-none-td table data display table table-bordered noarrow" width="100%" cellpadding="0" cellspacing="0" border="0">
										<thead>
											<tr>
												<th class="align-left width_200 wo-rp"><spring:message code="invitedSupplier.companyName" /></th>
												<th class="align-left width_200 wo-rp"><spring:message code="invitedSupplier.contactPerson" /></th>
												<th class="align-left width_200 wo-rp"><spring:message code="invitedSupplier.contactEmailAddress" /></th>
												<th class="align-left width_200 wo-rp"><spring:message code="invitedSupplier.contactNumber" /></th>
												<c:if test="${!empty event.deposit and event.deposit ne 0}">
													<th class="align-left width_150 wo-rp"><spring:message code="invitedSupplier.depositPaid" /></th>
													<th class="align-left width_200 wo-rp"><spring:message code="invitedSupplier.depositDate" /></th>
													<th class="align-left width_200 wo-rp"><spring:message code="invitedSupplier.depositReference" /></th>
												</c:if>
												<c:if test="${!empty event.participationFees and event.participationFees.unscaledValue() ne 0}">
													<th class="align-left width_150 wo-rp"><spring:message code="invitedSupplier.feePaid" /></th>
													<th class="align-left width_200 wo-rp"><spring:message code="invitedSupplier.feeDate" /></th>
													<th class="align-left width_200 wo-rp"><spring:message code="invitedSupplier.feeReference" /></th>
												</c:if>
											</tr>
										</thead>
										
									</table>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<input type="hidden" name="eventId" id="eventId" value="${event.id}" />


	<div class="modal fade" id="addSupplier" role="dialog">
		<div class="modal-dialog">
			<!-- Modal content-->
			<c:url var="url" value="/buyer/${eventType}/addSupplierInEvent/${event.id}"></c:url>
			<form:form action="${url}" method="post">
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<div class="modal-content" style="width: 30%; float: left; margin-left: 520px;">
					<div class="modal-header">
						<h3>
							<spring:message code="supplier.add" />
						</h3>
						<button class="close for-absulate" type="button" data-dismiss="modal"  id="cancleAddSupplier">×</button>
					</div>
					<div class="modal-body">
						<label><spring:message code="eventsummary.invitedsupplier.add.supplier" /></label>
					</div>

					<div class="input-group search_box_gray" style="margin-bottom: 10px; width: 90%; margin-left: 5%;">
						<select name="supplierId" id="idSupplierListChosen" class="chosen-select" data-validation="required" data-validation-error-msg-required="Please Select One Supplier">
							<option value=""><spring:message code="eventsummary.invitedsupplier.search.supplier" /></option>
							<c:forEach items="${suppliers}" var="supplier">
								<option value="${supplier.id}" ${empty supplier.id ? 'disabled' : ''}>${supplier.companyName}</option>
							</c:forEach>
						</select>
					</div>

					<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1 " style="background: #eff4f7;">
						<button type="submit" class="btn btn-info pull-left ph_btn_small hvr-pop hvr-rectangle-out" title=<spring:message code="application.add" />>
							<spring:message code="application.add" />
						</button>
					    <button type="button" id="cancleAddSupplier" class="btn btn-black pull-right btn-default ph_btn_small hvr-pop hvr-rectangle-out1" data-dismiss="modal">
							<spring:message code="application.cancel" />
						</button>
					</div>
				</div>
			</form:form>
		</div>
	</div>


	<div class="modal fade" id="addParticipationFee" role="dialog">
		<div class="modal-dialog">
			<!-- Modal content-->

			<c:url var="url" value="/buyer/${eventType}/addFeeInSupplier/${event.id}"></c:url>
			<form:form action="${url}" method="post" ModelAttribute="eventSupplier" id="addFeeInSupplierFromId">


				<input type="hidden" name="id" id="eventSuppId" value="">
				<span id="idHolder"></span>
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<div class="modal-dialog for-delete-all reminder">
					<div class="modal-content modal-content-fee" style="float: left;">
						<div class="modal-header">
							<h3>
								<spring:message code="summaryinvitesuppliers.add.fee" />
							</h3>
							<button class="close for-absulate" type="button" data-dismiss="modal">×</button>
						</div>
						<div class="modal-body">
							<label><spring:message code="summary.sure.add.fee" /></label>
						</div>
						<div class="form_field_fee ">
							<label class="col-sm-3 col-md-3 col-xs-3 col-lg-3 control-label"><spring:message code="summaryinvitesuppliers.fee.paid.date" /></label>
							<div class="col-sm-5 col-md-5 col-xs-5 col-lg-5">
								<div class="input-prepend input-group">
									<input name="feePaidDate" id="feePaidDate" data-placement="top" data-validation="required" data-toggle="tooltip" autocomplete="off" data-original-title="Fee Paid Date" class="bootstrap-datepicker form-control for-clander-view" id="meetingDate" type="text" data-validation-format="dd/mm/yyyy" placeholder=<spring:message code="dateformat.placeholder" /> data-date-format="dd/MM/yyyy" />
								</div>
							</div>
							<div class="col-md-3 col-sm-3 col-xs-3 col-lg-3">
								<div class="bootstrap-timepicker dropdown">
								<spring:message code="timeformat.placeholder" var="timefomatePlaceholder" />
									<input name="feePaidTime" id="feePaidTime" onfocus="this.blur()" data-validation="required" data-date-format="HH:mm" placeholder="${timefomatePlaceholder}" data-original-title="Fee Paid Time" autocomplete="off" class="bootstrap-timepicker timepicker-example for-timepicker-view form-control autoSave" />
								</div>
							</div>
						</div>
						<div class="form_field_fee ">
							<div>
								<label class="col-sm-3 col-md-3 col-xs-3 col-lg-3 control-label"><spring:message code="invitedSupplier.feeReference" /> </label>
								<div class="col-sm-8 col-md-8 col-xs-8 col-lg-8">
									<div class="input-prepend input-group">
									<spring:message code="summaryinvitesuppliers.fee.placeholder" var="feePlaceholder" />
										<input type="text" name="feeReference" id="feeReference" data-validation="required" maxlength="150" placeholder="${feePlaceholder}" autocomplete="off" data-original-title="Fee Reference" class="form-control autoSave" type="text" />
									</div>
								</div>
							</div>
						</div>
						<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1 " style="background: #eff4f7;">
						
							<button type="submit" class="btn btn-info pull-left ph_btn_small hvr-pop hvr-rectangle-out" style="width: 84px;"><spring:message code="application.yes" /></button>
							<button type="button" class="btn btn-black pull-right btn-default ph_btn_small hvr-pop hvr-rectangle-out1" data-dismiss="modal">
							<spring:message code="application.no2" />
						    </button>
						</div>
					</div>
				</div>
			</form:form>
		</div>
	</div>


	<div class="modal fade" id="addDeposit" role="dialog">
		<div class="modal-dialog">
			<!-- Modal content-->

			<c:url var="url" value="/buyer/${eventType}/addDepositInSupplier/${event.id}"></c:url>
			<form:form action="${url}" method="post" ModelAttribute="eventSupplier">
				<input type="hidden" name="id" id="evenSuppId" value="">
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<div class="modal-dialog for-delete-all reminder">
					<div class="modal-content modal-content-fee" style="float: left;">
						<div class="modal-header">
							
							<h3><spring:message code="summaryinvitesuppliers.add.deposite" /></h3>
							<button class="close for-absulate" type="button" data-dismiss="modal">×</button>
						</div>
						<div class="modal-body">
							<label><spring:message code="summary.sure.want.add.deposite" /></label>
						</div>
						<div class="form_field_fee ">
							<label class="col-sm-3 col-md-3 col-xs-3 col-lg-3 control-label"><spring:message code="invitedsupplier.deposite.paid.date" /></label>
							<div class="col-sm-5 col-md-5 col-xs-5 col-lg-5">
								<div class="input-prepend input-group">
									<input name="depositPaidDate" id="depositPaidDate" data-validation="required" data-placement="top" autocomplete="off" data-toggle="tooltip" data-original-title="Deposit Paid Date" class="bootstrap-datepicker form-control for-clander-view" type="text" data-validation-format="dd/mm/yyyy" placeholder="dd/mm/yyyy" data-date-format="dd/MM/yyyy" />
								</div>
							</div>
							<div class="col-md-3 col-sm-3 col-xs-3 col-lg-3">
								<div class="bootstrap-timepicker dropdown">
									<spring:message code="timeformat.placeholder" var="timefomatePlaceholder" />
									<input name="depositPaidTime" id="depositPaidTime" onfocus="this.blur()" autocomplete="off" data-date-format="HH:mm" placeholder="${timefomatePlaceholder}" data-original-title="Deposit Paid Time" autocomplete="off" data-validation="required" class="bootstrap-timepicker timepicker-example for-timepicker-view form-control autoSave" />
								</div>
							</div>
						</div>
						<div class="form_field_fee">
							<div>
								<label class="col-sm-3 col-md-3 col-xs-3 col-lg-3  control-label"><spring:message code="invitedSupplier.depositReference" /> </label>
								<div class="col-sm-8 col-md-8 col-xs-8 col-lg-8">
									<div class="input-prepend input-group">
									<spring:message code="summaryinvitesuppliers.deposite.placeholder" var="depositPlaceholder" />
										<input type="text" name="depositReference" id="depositReference" data-validation="required" maxlength="150" id="depositReference" autocomplete="off" data-original-title="Deposit Reference" placeholder="${depositPlaceholder}" class="form-control autoSave" type="text" />
									</div>
								</div>
							</div>
						</div>
						<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1 " style="background: #eff4f7;">
							
							<button type="submit" class="btn btn-info pull-left ph_btn_small hvr-pop hvr-rectangle-out" style="width: 84px;"><spring:message code="application.yes" /></button>
							
							<button type="button" class="btn btn-black pull-right btn-default ph_btn_small hvr-pop hvr-rectangle-out1" data-dismiss="modal">
							<spring:message code="application.no2" />
						    </button>
						</div>
					</div>
				</div>
			</form:form>
		</div>
	</div>
</div>

<script type="text/javascript">
	$(document).delegate('.add_Supplier', 'click', function(e) {
		$('#addSupplier').modal();
	});
	$('document').ready(function() {
try{
		draftsEventsData = $('#invitedsupplierList').DataTable({
			"oLanguage" : {
				"sUrl" : getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
			},
			"processing" : false,
			"deferRender" : true,
			"deferLoading" : 0,
			"preDrawCallback" : function(settings) {
				//	$('div[id=idGlobalError]').hide();
				$('#loading').show();
				return true;
			},
			"drawCallback" : function() {
				$('#loading').hide();
			},
			"serverSide" : true,
			"pageLength" : 10,
			 "ordering": false,
			"searching" : false,
			"ajax" : {
				"url" : getContextPath() + "/buyer/${eventType}/invitedSupplierList/${event.id}",
				"data" : function(d) {
				},
				"error" : function(request, textStatus, errorThrown) {
					var error = request.getResponseHeader('error');
					if (error != undefined) {
						$('p[id=idGlobalErrorMessage]').html(error != null ? error.split(",").join("<br/>") : "");
						$('div[id=idGlobalError]').show();
					}
					$('#loading').hide();
				}
			},
			"order" : [  ],
			"columns" : [ {
				'render' : function(data, type, row) {
					if (row.selfInvited == true) {
						var	html = '<span class="required-mark">&nbsp;*&nbsp;&nbsp;</span>';
 					 	html += row.companyName; 
					}
					if (row.selfInvited == false) {
						var	html = '<span class="">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>';
						html += row.companyName; 
					}
					return html;
				}
			}, {
				"data" : "fullName",
				"className" : "align-left",
				"defaultContent" : ""
			},

			{
				"data" : "communicationEmail",
				"className" : "align-left",
				"defaultContent" : ""
			}, {
				"data" : "companyContactNumber",
				"className" : "align-left",
				"defaultContent" : ""
			},
			
			<c:if test="${!empty event.deposit and event.deposit ne 0}">
			{
				'render' : function(data, type, row) {
					return '<span class="'+(row.depositPaid ? 'right-green-mark' : 'cross-waiting-mark')+'" > <i class="'+(row.depositPaid ? 'glyphicon glyphicon-ok' : 'glyphicon glyphicon-remove')+'"></i></span><button class="btn-link addDepositPopUp" data-id="'+(row.id)+'"><spring:message code="summaryinvitesuppliers.add.deposite" /></button>'
				}
			},
			 {
				"data" : "depositTime",
				"className" : "align-left",
				"defaultContent" : ""
			},
			
			 {
				"data" : "depositReference",
				"className" : "align-left",
				"defaultContent" : ""
			},
		</c:if>
			
		<c:if test="${!empty event.participationFees and event.participationFees.unscaledValue() ne 0}">
		{
			'render' : function(data, type, row) {
				return '<span class="'+(row.feePaid ? 'right-green-mark' : 'cross-waiting-mark')+'" > <i class="'+(row.feePaid ? 'glyphicon glyphicon-ok' : 'glyphicon glyphicon-remove')+'"></i></span><button class="btn-link addFeePopUp" data-id="'+(row.id)+'"><spring:message code="summaryinvitesuppliers.add.fee" /></button>'
			}
		},
		 {
			"data" : "feeTime",
			"className" : "align-left",
			"defaultContent" : ""
		},
		
		 {
			"data" : "feeReference",
			"className" : "align-left",
			"defaultContent" : ""
		},
		</c:if>
			

			],

		});
	}catch(err){
		console.log(e);
	}
		draftsEventsData.ajax.reload();
	});

	$(document).on("click", ".addFeePopUp", function() {
		var suppId = $(this).data('id');
		$('#eventSuppId').val(suppId);
		$('#addParticipationFee').modal();
	});

	$(document).on("click", ".addDepositPopUp", function() {
		var suppId = $(this).data('id');
		$('#evenSuppId').val(suppId);
		$('#addDeposit').modal();
	});

	$(function() {
		"use strict";
		var nowTemp = new Date();
		var now = new Date(nowTemp.getFullYear(), nowTemp.getMonth(), nowTemp.getDate(), 0, 0, 0, 0);

		$('.bootstrap-datepicker').bsdatepicker({
			format : 'dd/mm/yyyy',
			minDate : now,
			onRender : function(date) {
				return date.valueOf() > now.valueOf() ? 'disabled' : '';
			}
		})

	});
	
		$(document).delegate('#cancleAddSupplier', 'click', function(e) {
		$('#idSupplierListChosen').parent().removeClass('has-error').find('.form-error').remove();
});
		
		$(document).delegate('#addSupplierError', 'click', function(e) {
			$('#idSupplierListChosen').parent().removeClass('has-error').find('.form-error').remove();
	});

	$('#addFeeInSupplierFromId').submit(function() {
		console.log('.....Form Submitted.....');
		$('#loading').show();
	});
		
</script>

<style>
.bootstrap-timepicker-widget.dropdown-menu.open {
	max-width: 160px;
}

.bootstrap-timepicker-widget.dropdown-menu.open input {
	width: 35px;
	min-width: 35px;
	max-width: 35px;
}

@media ( max-width : 1300px) {
	.modal-content-fee {
		margin-left: 30%;
	}
}

.form_field_fee {
	float: left;
	margin: 10px 0 15px;
	width: 100%;
}
.chosen-select.error+.chosen-container {
	border: 1px solid #dfd5d4;
}

 .required-mark{
color:#ff5757;
} 


.selfinvite:before{
     content:"*" ;
     color:#ff5757 ;   
     }
     
.selfinvite {
	color: #ff5757;
	font-family: 'open_sanssemibold';
	font-size: 14px;
}

.selfinvitenote {
	color: #ff5757;
	font-family: 'open_sanssemibold';
	font-size: 14px;
}
</style>


<script type="text/javascript" src="<c:url value="/resources/assets/widgets/timepicker/timepicker.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/timepicker/timepicker.css"/>">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker-demo.js"/>"></script>

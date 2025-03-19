<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">
<spring:message var="rfxCreateBq" code="application.rfx.create.bill.of.quantities" />
<spring:message var="rfaCreateBq" code="application.rfa.create.bill.of.quantities" />
<script type="text/javascript">
zE(function() {
  zE.setHelpCenterSuggestions({ labels: [${rfxCreateBq},${rfaCreateBq}] });
});
</script>
<sec:authorize access="hasRole('ADMIN')" var="isAdmin" />
<jsp:useBean id="today" class="java.util.Date" />
<div id="sb-site">
	<div id="page-wrapper">
		<div id="page-content-wrapper">
			<div id="page-content">
				<div class="container">
					<ol class="breadcrumb">
						<li><a href="${pageContext.request.contextPath}/buyer/buyerDashboard"> <spring:message code="application.dashboard" />
						</a></li>
						<li class="active"><spring:message code="rfs.sourcing.form.request" /></li>
					</ol>
					<!-- page title block -->
					<div class="Section-title title_border gray-bg">
						<h2 class="trans-cap"><spring:message code="eventdescription.billofquantity.label" /></h2>
						<h2 class="trans-cap pull-right"><spring:message code="application.status" /> : ${sourcingFormRequest.status}</h2>
					</div>
					<div class="ports-tital-heading">
						<div class="example-box-wrapper wigad-new">
							<jsp:include page="sourcingFormHeader.jsp" />
							<!-- /.Container Div -->
							<div class="tab-main-inner ">
								<div class="clear"></div>
								<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
								<div class="clear"></div>
								<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
								<div class="clear"></div>
								 <input id="sourcingstatus" name="sourcingstatus" value="${sourcingFormRequest.status}" type="hidden">
								<%-- <c:if test="${eventType eq 'RFA' and event.auctionType != 'FORWARD_DUTCH' and event.auctionType != 'REVERSE_DUTCH' and auctionRules.preBidBy == 'BUYER' and (event.status == 'DRAFT' or event.status == 'SUSPENDED')}">
									<div class="meeting2-heading">
										<c:if test="${supplierBqCount}">
											<form action="${pageContext.request.contextPath}/buyer/${eventType}/discaredSuppliersInitialPrice/${event.id}" method="post">
												<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> <input class="pull-right btn ph_btn_midium btn-info hvr-pop hvr-rectangle-out margin-bottom-10" style="margin-right: 20px; margin-top: 7px; height: 30px; line-height: 1;" value="Discard Supplier Initial Prices" type="submit" id="discardBqSupp" />
											</form>
										</c:if>
										<form action="${pageContext.request.contextPath}/buyer/${eventType}/eventBqForSupplier/${event.id}">
											<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> <input class="pull-right btn ph_btn_midium btn-info hvr-pop hvr-rectangle-out margin-bottom-10" style="margin-right: 20px; margin-top: 7px; height: 30px; line-height: 1;" value="Enter Pre-Bid Pricing for Supplier" type="submit" id="addBqSupp" />
										</form>
									</div>
								</c:if> --%>
								<div class="margin-bottom-10">
									<div class="meeting2-heading">
										<h3><spring:message code="event.bq.create.title" /></h3>
										<button class="pull-right btn ph_btn_midium btn-info toggleCreateBq hvr-pop hvr-rectangle-out" type="button" data-toggle="tooltip" data-original-title='<spring:message code="bq.add.placeholder"/>' style="margin-right: 20px; margin-top: 7px; height: 30px; line-height: 1;"><spring:message code="event.bq.add.button"/></button>
										<c:if test="${sourcingFormRequest.status eq 'DRAFT'}">
										<button class="pull-right btn ph_btn_midium btn-success reArrangeOrder hvr-pop" type="button" data-toggle="tooltip" data-original-title='<spring:message code="rearrange.order"/>' style="margin-right: 20px; margin-top: 7px; height: 30px; line-height: 1;"><spring:message code="rearrange.order"/></button>
									    </c:if>
									</div>
									<div class="import-supplier-inner-first-new pad_all_15 global-list form-middle" id="bqDisplay" style="display: none;">
										<form id="bqFormAddEdit" class="form-horizontal bordered-row has-validation-callback" action="" method="post">
											<input type="hidden" name="formId" id="formId" value="${sourcingFormRequest.id}"> <input id="bqId" name="id" value="${bq.id}" type="hidden">
											<header class="form_header"></header>
											<jsp:include page="sourcingCreateAddBq.jsp" />
										</form>
									</div>
								</div>
								<div id="tab-5" class="tab-content current doc-fir pad_all_15" style="display: none;">
									<div class="doc-fir-inner1">
										<h3><spring:message code="eventdescription.billofquantity.label" /></h3>
										<div class="Invited-Supplier-List dashboard-main">
											<div class="Invited-Supplier-List-table add-supplier id-list">
												<div class="ph_tabel_wrapper">
													<div class="main_table_wrapper ph_table_border payment marg-bottom-20 document-table">
														<table class="ph_table tabaccor padding-none-td table fix-top-head " width="100%" border="0" cellspacing="0" cellpadding="0">
															<thead>
																<tr>
																	<th class="col-md-3 width_100_fix align-left"><spring:message code="application.action1"/></th>
																	<th class="col-md-3 width_100_fix align-left"><spring:message code="rfaevent.no.col"/></th>
																	<th class="col-md-3 align-left"><spring:message code="application.name"/></th>
																	<th class="col-md-5 align-left"><spring:message code="application.description"/></th>
																</tr>
															</thead>
														</table>
														<table class="ph_table tabaccor padding-none-td table tab_trans timezone induslist documents-page" width="100%" border="0" cellspacing="0" cellpadding="0">
															<tbody class="catecontent">
																<c:forEach items="${bqSourcingList}" var="bq" varStatus="loop">
																	<tr>
																		<td class="col-md-3 width_100_fix" data-id="${bq.id}">
																			<form action="${pageContext.request.contextPath}/buyer/showSourcingBqItems" method="post">
																				<span class="marg-right-5 pull-left">
																					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> <input type="hidden" name="bqId" value="${bq.id}" /> <input type="hidden" name="formId" value="${bq.sourcingFormRequest.id}" />
																					<button type="submit" name="submitbq" class=" font-size-26" id="idViewButton" data-toggle="tooltip" data-placement="top" title='<spring:message code="tooltip.edit" />'>
																						<img style="width: 20px;" src="<c:url value="/resources/images/edit1.png"/>" />
																					</button>
																				</span>
																			</form>
																			<span title="" class="pull-left ">
																				<c:if test="${sourcingFormRequest.status eq 'DRAFT'}">
																					<button class=" font-size-26 deleteBq" data-toggle="tooltip" data-placement="top" title='<spring:message code="tooltip.delete" />'>
																						<img src="<c:url value="/resources/images/delete1.png"/>" />
																					</button>
																				</c:if>
																			</span>
																		</td>
																		<c:if test="${bq.bqOrder != null}">
																	      <td class="col-md-3 width_100_fix align-left" style="padding-left: 18px;">${bq.bqOrder}</td>
																	    </c:if>
																	    <c:if test="${bq.bqOrder == null}">
																	      <td class="col-md-3 width_100_fix align-left" style="padding-left: 18px;">${loop.index + 1}</td>
																	    </c:if>
																		<td class="col-md-3 align-left">${bq.name}</td>
																		<td class="col-md-5 align-left">${bq.description}</td>
																	</tr>
																</c:forEach>
															</tbody>
														</table>
													</div>
												</div>
											</div>
										</div>
									</div>
									<div class="clear"></div>
									<div class="row">
										<div class="col-md-12">
											<div class="table_f_action_btn">
												<form class="bordered-row" id="submitPriviousForm" method="post" style="float: left;" action="${pageContext.request.contextPath}/buyer/sourcingBqPrevious">
													<input type="hidden" id="formId" value="${formId}" name="formId"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
													<button type="submit" class="btn btn-lg btn-black hvr-pop hvr-rectangle-out1" value="Previous" name="previous" id="priviousStep"><spring:message code="application.previous" /></button>
												</form>
												<form class="bordered-row" id="submitNextForm" method="post" style="float: left;" action="${pageContext.request.contextPath}/buyer/sourcingBqNext">
													<input type="hidden" id="formId" value="${formId}" name="formId"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
													<button type="submit" class="btn hvr-pop hvr-rectangle-out btn-lg btn-info hvr-pop hvr-rectangle-out" value="Next" id="priviousStep"><spring:message code="application.next" /></button>
												</form>
												<c:if test="${(isAdmin or eventPermissions.owner)}">
													<a href="#confirmCancelRequest" role="button" class="btn btn-danger ph_btn hvr-pop pull-right" data-toggle="modal"><spring:message code="rfs.summary.cancel.request" /></a>
												</c:if>
												<%-- <spring:message code="application.draft" var="draft" />
												<form action="${pageContext.request.contextPath}/buyer/${eventType}/bqSaveDraft" method="post" style="float: right;">
													<input type="hidden" id="eventId" value="${eventId}" name="eventId"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
													<c:if test="${sourcingFormRequest.status eq 'DRAFT'}">
														<input type="submit" id="idBqSaveDraft" class="step_btn_1 btn btn-black hvr-pop hvr-rectangle-out1 ph_btn hvr-pop pull-right" value="${draft}" />
													</c:if>
												</form> --%>
												<%-- <c:if test="${(sourcingFormRequest.status eq 'DRAFT' or sourcingFormRequest.status eq 'SUSPENDED')">
													<a href="#confirmCancelEvent" role="button" class="btn btn-danger marg-right-10 ph_btn hvr-pop right-header-button" data-toggle="modal" id="cancelEve">${sourcingFormRequest.status eq 'DRAFT' ? 'Cancel Draft' : 'Cancel Event'}</a>
												</c:if> --%>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="modal fade" id="myModalBqDelete" role="dialog">
		<div class="modal-dialog for-delete-all reminder">
			<!-- Modal content-->
			<div class="modal-content">
				<div class="modal-header">
					<h3><spring:message code="application.confirm.delete" /></h3>
					<button class="close for-absulate" type="button" data-dismiss="modal">X</button>
				</div>
				<div class="modal-body">
					<label><spring:message code="bq.sure.delete.bq"/></label>
					<input type="hidden" name="confirmRemoveBqId" id="confirmRemoveBqId">
				</div>
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
					<button type="button" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" id="confirmRemoveenvlop"><spring:message code="application.delete" /></button>
					<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal"><spring:message code="application.cancel" /></button>
				</div>
			</div>
		</div>
	</div>
	
	<!-- cancel Event popup  -->
	<div class="modal fade" id="confirmCancelRequest" role="dialog">
		<div class="modal-dialog for-delete-all reminder">
			<!-- Modal content-->
			<div class="modal-content">
				<div class="modal-header">
					<h3>
						<spring:message code="eventsummary.confirm.cancel" />
					</h3>
					<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
				</div>
			<form  action="${pageContext.request.contextPath}/buyer/cancelSourcingReq/${sourcingFormRequest.id}" method="post">
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<div class="col-md-12">
					<div class="row">
						<div class="modal-body col-md-12">
							<label> <spring:message code="request.confirm.to.cancel" /> </br>
							</label>
						</div>
						<div class="form-group col-md-6">
							<spring:message code="event.reason.cancellation.placeholder" var="reasonCancel" />
							<textarea name="description" class="width-100" placeholder="${reasonCancel}" id="reasonCancel" rows="3" data-validation="required length" data-validation-length="max500" ></textarea>
						</div>
					</div>
				</div>
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
					<button type="submit" id="RfsCancelRequest" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left">
						<spring:message code="application.yes" />
					</button>
					<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
						<spring:message code="application.no2" />
					</button>
				</div>
			</form>
			</div>
		</div>
	</div>
	
	<div class="modal fade" id="confirmReArrangeOrder" role="dialog">
		<div class="modal-dialog" style="width: 90%; max-width: 800px;">
			<!-- Modal content-->
			<div class="modal-content">
				<div class="modal-header">
					<h3>Rearrange Order</h3>
					<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
				</div>
				<input type="hidden" name="formId" id="formId" value="${sourcingFormRequest.id}">
				<div class="modal-body">
				    <table class="header_table header bq_font" width="100%" cellpadding="0" cellspacing="0" id="table_id">
					   <tr>
						<th class="width_50 width_60_fix move_col align-left"><a href="javascript:void(0);"> <i aria-hidden="true" class="glyph-icon fa-arrows-v move_icon"></i>
						</a></th>
						<th class="width_100 width_135_fix align-left font-ch-weight" ><spring:message code="rfaevent.no.col"/></th>
					     <th class="width_200 width_200_fix align-left font-ch-weight"><spring:message code="application.name"/></th>
					    <th class="width_200 width_200_fix align-left font-ch-weight"><spring:message code="application.description"/></th>
					</tr>
				    </table>
				    
			   <section id="demo">
				<ul class="sortable ui-sortable mjs-nestedSortable-branch mjs-nestedSortable-expanded" id="sortable">
				<c:forEach items="${bqSourcingList}" var="bq" varStatus="loop">
					<li style="display: list-item;" class="mjs-nestedSortable-leaf" id="menuItem_1" data-item="${bq.id}">
						<div class="menuDiv">
						  <table class="table data ph_table diagnosis_list sorted_table" id="table_id" style=" background: #fff;">
						   <tr class="sub_item item" data-id="${bq.id}">
						    <td class="width_50 width_60_fix move_col align-left"><a href="javascript:void(0);"> <i aria-hidden="true" class="glyph-icon fa-arrows-v move_icon"></i>
							</a></td>
						<c:if test="${bq.bqOrder != null}">
					     <td class="width_100 width_135_fix align-left orderChange"><span>${bq.bqOrder}</span></td>
					    </c:if>
					    <c:if test="${bq.bqOrder == null}">
					     <td class="width_100 width_135_fix align-left orderChange"><span>${loop.index+1}</span></td>
					    </c:if>
					     <td class="width_200 width_200_fix align-left"><span class="item_name">${bq.name}</span></td>
					     <td class="width_200 width_200_fix align-left"><span class="item_name">${bq.description}</span></td>
				        </tr>
					  </table>
					 </div>
				   </li>
		       </c:forEach>
			 </ul>
		    </section>
			     </div>
			     <div class="modal-footer  text-center">
					<button type="button" class="btn btn-info ph_btn_midium" id="updateBqOrder" data-dismiss="modal">
						<spring:message code="application.update" />
					</button>
					<button class="btn ph_btn_midium btn-black hvr-pop hvr-rectangle-out1 " data-dismiss="modal">
						<spring:message code="application.cancel" />
					</button>
				</div>
			</div>
		</div>
	</div> 
	
	<!-- cancel Event popup  -->
	<%-- <div class="modal fade" id="confirmCancelEvent" role="dialog">
		<div class="modal-dialog for-delete-all reminder">
			<!-- Modal content-->
			<div class="modal-content">
				<div class="modal-header">
					<h3>Confirm Cancellation</h3>
					<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
				</div>
				<form:form modelAttribute="event" action="${pageContext.request.contextPath}/buyer/${eventType}/cancelEvent" method="post">
					<form:hidden path="id" />
					<div class="col-md-12">
						<div class="row">
							<div class="modal-body col-md-12">
								<label>
									Are you sure you want to cancel this Sourcing Request. </br>Reason :
								</label>
							</div>
							<div class="form-group col-md-6">
								<form:textarea path="cancelReason" class="width-100" placeholder="Mention reason for cancellation" rows="3" data-validation="required length" data-validation-length="max500" />
							</div>
						</div>
					</div>
					<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
						<form:button type="submit" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left">Yes</form:button>
						<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">No</button>
					</div>
				</form:form>
			</div>
		</div>
	</div> --%>
	<script type="text/javascript" src="<c:url value="/resources/assets/js-core/nested-sortable-tests.js"/>"></script>
	<!-- Theme layout -->
	<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
	<script>
		$.validate();
	</script>
	<script type="text/javascript" src="<c:url value="/resources/js/view/sourcingRequestBQ.js?1"/>"></script>
	<script type="text/javascript" src="<c:url value="/resources/js/view/modalValidation.js"/>"></script>
	<script type="text/javascript">
		<c:if test="${(eventType != 'RFA' and event.status == 'SUSPENDED' and (event.suspensionType == 'KEEP_NOTIFY' or event.suspensionType == 'KEEP_NO_NOTIFY')) or (eventType == 'RFA' and event.status == 'SUSPENDED')}">
		$(window).bind('load', function() {
			var allowedFields = '#meetingListNext,#priviousStep,.s1_view_desc,#idViewButton,#bubble,#cancelEve,#addBqSupp';
			//var disableAnchers = ['#reloadMsg'];        
			disableFormFields(allowedFields);
		});
		</c:if>
		
		<c:if test="${eventPermissions.viewer or buyerReadOnlyAdmin or sourcingFormRequest.status ne 'DRAFT'}">
		$(window).bind('load', function() {
			var allowedFields = '.tab-link > a,#priviousStep,.s1_view_desc,#idViewButton,#bubble,#addBqSupp,#cancelEve,#addBqSupp';
			//var disableAnchers = ['#reloadMsg'];        
			disableFormFields(allowedFields);
		});
		</c:if>
		
		$(document).ready(function() {

			$(document).delegate('.deleteBq', 'click', function(e) {
				e.preventDefault();
				$('#confirmRemoveBqId').val($(this).parents('td').attr('data-id'));
				$('#myModalBqDelete').modal('show');
			});

			$(document).delegate('#confirmRemoveenvlop', 'click', function(e) {
				e.preventDefault();

				var id = $('#confirmRemoveBqId').val();
				deleteBQ(id);
				$('#myModalBqDelete').modal('hide');
				//$('#delete_'+id).submit();
			});
			
			$( function() {
				$("#sortable").sortable({
				    //update ordering number
					update: function (event, ui) {
				        $('.orderChange span').each(function (i) {
				        	 var number = i + 1;
				             $(this).text(number);
				        });
				    }
				});
				$("#sortable").disableSelection();
		  });
		});
	</script>

	<style>
.font-size-26 {
	font-size: 26px;
}
.header {
    position: relative !important;
}
.width_60_fix {
    width: 64px;
}
.width_135_fix {
    width: 135px;
}
.header+* {
	margin-top: 0px !important;
}
.ph_table {
    border: 1px solid #e8e8e8;
    border-top: 0;
    -moz-border-radius: 2px;
    border-radius: 2px;
}
ul {
    list-style-type: none;
}
.deleteCq {
	background: transparent none repeat scroll 0 0 !important;
	padding: 0 !important;
}

.col-md-3.pad-left.align-left {
	padding-left: 45px;
}
.font-ch-weight{
font-weight: 600 !important;
}
</style>
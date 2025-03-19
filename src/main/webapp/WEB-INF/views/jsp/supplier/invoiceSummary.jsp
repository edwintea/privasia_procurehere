<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<style>
.ph_btn_custom {
	height: 40px !important;
	min-width: 170px !important;
	font-size: 16px !important;
	line-height: 39px;
	font-weight: 500;
}
.reset-that {
  all: initial;
  * {
    all: unset;
  };
  word-break: break-all;
}

</style>
<div class="clear"></div>

<div class="white-bg border-all-side float-left width-100 pad_all_15">
	<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
	<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
	<div class="row">
		<div class="col-md-6 col-sm-12 col-xs-12">
			<div class="tag-line">
				<h2>
					<spring:message code="supplier.invoiceListing.invoiceId" />
					: ${invoice.invoiceId}
				</h2>
				<br>
				<c:if test="${invoice.status ne 'DRAFT' }">	
					<h2>
						<spring:message code="supplier.invoice.summary.invoiceDate" />
						: <fmt:formatDate value="${invoice.invoiceSendDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
					</h2>
					<br>
				</c:if>				
				<h2>
					<spring:message code="application.lable.buyer" />
					: ${invoice.buyer.companyName}
				</h2>
				<br>
				<h2>
					<spring:message code="supplier.invoiceListing.businessUnit" />
					: <c:if test="${empty invoice.businessUnit}">N/A</c:if>${invoice.businessUnit.unitName}
				</h2>
				<c:if test="${poFinanceRequest != null and poFinanceRequest.acceptedDate != null}">
					<br/>
					<div class="text-warning">
						<b>Note:</b> You have applied for financing for PO linked to this invoice.
					</div>
					<div class="text-warning">
						<b>FinansHere Offer Accepted Date:</b> <fmt:formatDate value="${poFinanceRequest.acceptedDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" /> 
					</div>
					<div class="text-warning">
						<b>PO Financing Application Status:</b> ${poFinanceRequest.status} 
					</div>
				</c:if>
				<c:if test="${not empty invoiceFinanceRequest}">
					<br>
					<div class="text-warning">
						<b>Invoice <spring:message code="funding.requested.date" />:</b> <fmt:formatDate value="${invoiceFinanceRequest.requestedDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
					</div>
					<div class="text-warning">
						<b>Invoice <spring:message code="funding.requested.status" />:</b> ${invoiceFinanceRequest.requestStatus}
					</div>
				</c:if>
				
			</div>
		</div>
	</div>
	
	<c:if test="${invoice.status ne 'DRAFT'}">
		<c:if test="${invoice.status eq 'ACCEPTED' and empty invoiceFinanceRequest and buyerOnboarded}">
			<div class="pull-right">
				<form:form action="${pageContext.request.contextPath}/supplier/requestFinance" method="POST">
					<input type="hidden" name="invoiceId" value="${invoice.id}">
					<button class="btn btn-sm float-right btn-warning hvr-pop marg-left-10" type="submit" data-toggle="tooltip" data-placement="top" data-original-title='Request for Financing'>
						<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-dollar"></i></span> 
						<span class="button-content">Request Financing</span>
					</button>
				</form:form>
			</div>
		</c:if>
		<div class="pull-right">
			<form:form action="${pageContext.request.contextPath}/supplier/downloadInvoice/${invoice.id}" method="GET">
				<button class="btn btn-sm float-right btn-info hvr-pop marg-left-10 downloadPoBtn" type="submit" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="invoicesummary.download.invoice.button" />'>
					<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i>
					</span> <span class="button-content"><spring:message code="invoicesummary.download.invoice.button" /></span>
				</button>
			</form:form>
		</div>
	</c:if>
	
	
</div>
<form:form name="form" id="invoicForm" method="post" action="" acceptCharset="UTF-8">
<div class="clear"></div>
<input type="hidden" id="invoiceId" name="invoiceId" value="${invoice.id}">
<input type="hidden" id="poId" name="poId" value="${invoice.po.id}">
<div class="tab-pane" style="display: block">
	<div class="upload_download_wrapper clearfix event_info">
		<h4><spring:message code="prsummary.general.information" /></h4>
		
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="supplier.invoiceListing.invoiceName" />
				</label>
			</div>
			<spring:message var="placeReference" code="invoice.place.name" />
			<div class="col-sm-5 col-md-5 col-xs-6">
			  <c:if test="${invoice.status eq 'DRAFT'}">
			      <input name="name" value="${invoice.name}" type="text" placeholder="${placeReference}" data-validation="required length" data-validation-length="max128" class="form-control" />
			  </c:if>
			  <c:if test="${invoice.status ne 'DRAFT'}">
				 <p>${invoice.name}</p> 
			  </c:if>
			</div>
		</div>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="supplier.do.summary.referenceNo" />
				</label>
			</div>
			<spring:message var="placeReference" code="invoice.place.reference.number" />
			<div class="col-sm-5 col-md-5 col-xs-6">
			<c:if test="${invoice.status eq 'DRAFT'}">
			      <input name="referenceNumber" value="${invoice.referenceNumber}" type="text" placeholder="${placeReference}" data-validation="alphanumeric length" data-validation-allowing="-_ /" data-validation-optional="true" data-validation-length="1-32" class="form-control" />
			  </c:if>
			  <c:if test="${invoice.status ne 'DRAFT'}">
				 <p>${invoice.referenceNumber}</p> 
			  </c:if>
			</div>
		</div>
		
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="supplier.invoiceListing.poNumber" />
				</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
			<p><a href="${pageContext.request.contextPath}/supplier/supplierPrView/${invoice.po.id}">${invoice.po.poNumber}</a></p>
			</div>
		</div>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="supplier.invoice.summary.creator" />
				</label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>
					${invoice.createdBy.name} <br> ${invoice.createdBy.communicationEmail} <br>
					<c:if test="${not empty invoice.supplier.companyContactNumber}"><spring:message code="prdraft.tel" />: ${invoice.supplier.companyContactNumber}</c:if>
					<c:if test="${not empty invoice.supplier.faxNumber}"><spring:message code="prtemplate.fax" />: ${invoice.supplier.faxNumber}</c:if>
					<c:if test="${not empty invoice.createdBy.phoneNumber}"><spring:message code="prtemplate.hp" />: ${ invoice.createdBy.phoneNumber}</c:if>
				</p>
			</div>
		</div>
	</div>
	<div class="upload_download_wrapper clearfix marg-top-10 event_info">
		<h4><spring:message code="rfs.summary.finance.information" /></h4>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="pr.base.currency" /> </label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>${invoice.currency.currencyCode}</p>
			</div>
		</div>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="eventdescription.decimal.label" /> </label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>${invoice.decimal}</p>
			</div>
		</div>
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="eventdescription.paymentterm.label" /> </label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>${invoice.paymentTerm}</p>
			</div>
		</div>
		<c:if test="${not empty invoice.paymentTermDays}">
			<div class="form-tander1 requisition-summary-box">
				<div class="col-sm-4 col-md-3 col-xs-6">
					<label> Payment Days :</label>
				</div>
				<div class="col-sm-5 col-md-5 col-xs-6">
					<p>${invoice.paymentTermDays}</p>
				</div>
			</div>
		</c:if>

		<c:if test="${buyerOnboarded && poFinanceRequest != null}">
		<div class="form-tander1 requisition-summary-box">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label> <spring:message code="invoice.summary.finance.request.label" /> </label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6 disabled">
			  	<c:if test="${invoice.status eq 'DRAFT'}">
					<p><input name="requestForFinance" id="requestForFinance" type="checkbox" readonly="readonly" class="custom-checkbox disabled" ${invoice.requestForFinance || poFinanceRequest != null ? 'checked value="true"' : ''} /></p>
				</c:if>
			  	<c:if test="${invoice.status ne 'DRAFT'}">
					<p>${invoice.requestForFinance ? 'Yes' : 'No'}</p>
			  	</c:if>
			</div>
		</div>
		</c:if>
		
		
	</div>
	<div class="upload_download_wrapper clearfix marg-top-10 event_info">
		<h4>
			<spring:message code="supplierbilling.billing.detail" />
		</h4>
		
		<div class="form-tander1 requisition-summary-box marg-bottom-20">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label class="set-line-height"><spring:message code="supplier.attention" /></label>
			</div>
			<spring:message var="placeholdReference" code="supplier.place.attention" />
			<div class="col-sm-5 col-md-5 col-xs-6">
				<c:if test="${invoice.status eq  'DRAFT' }">
			      <input name="attentionTo" type="text" value="${invoice.attentionTo}" placeholder="${placeholdReference}" data-validation="required length alphanumeric" data-validation-allowing="-_ /" data-validation-length="1-64" class="form-control" />
			   </c:if>
			    <c:if test="${invoice.status ne  'DRAFT' }">
					<p class="set-line-height">${invoice.attentionTo}</p>
				</c:if>
			</div>
		</div>
		
 		   <div class="form-tander1 requisition-summary-box marg-bottom-20">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label class="set-line-height"><spring:message code="supplier.billing.address" /></label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p class="set-line-height">
					<span class='desc'>	
					  ${invoice.line1} <br> 
						<c:if test="${not empty invoice.line2}">${invoice.line2}  <br></c:if>
						<c:if test="${not empty invoice.line3}">${invoice.line3}  <br></c:if>
						<c:if test="${not empty invoice.line4}">${invoice.line4}  <br></c:if>
						<c:if test="${not empty invoice.line5}">${invoice.line5}  <br></c:if>
						<c:if test="${not empty invoice.line6}">${invoice.line6}  <br></c:if>
						<c:if test="${not empty invoice.line7}">${invoice.line7}  <br></c:if>
					</span>
					
				</p>
			</div>
		</div>
     
		
		
		<div class="form-tander1 requisition-summary-box ">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label><spring:message code="supplier.invoice.duedate" /> </label>
			</div>
			<c:if test="${invoice.status eq  'DRAFT' }">
			<div id="deliveryTime">
			<div class="col-sm-3 col-md-3 col-xs-3">
			 <div class="input-prepend input-group">
		         <spring:message code="dateformat.placeholder" var="dateformat"/>
			     <spring:message code="tooltip.delivery.date" var="deliveryadds"/>
			     <fmt:formatDate var="changeDate" value="${invoice.dueDate}" pattern="dd/MM/yyyy" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
				  <input name="dueDate" id="dueDate" value="${changeDate}" data-placement="top" data-toggle="tooltip" data-original-title="${deliveryadds}" class="nvclick form-control for-clander-view" data-validation="required date" data-fv-date-min="15/10/2016" data-validation-format="dd/mm/yyyy"
			       placeholder="${dateformat}" autocomplete="off" />
		       </div>
			  </div>
			  </div>
			</c:if>
			<c:if test="${invoice.status ne  'DRAFT' }">
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p>
					<fmt:formatDate value="${invoice.dueDate}" pattern="dd/MM/yyyy" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
				</p>
			</div>
		  </c:if>
		</div>
		
	</div>


	<div class="upload_download_wrapper clearfix marg-top-10 event_info">
		<h4><spring:message code="pr.delivery.detail" /></h4>
		
		<div class="form-tander1 requisition-summary-box marg-bottom-20">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label class="set-line-height"><spring:message code="supplier.invoice.include.delivery" /></label>
			</div>
			<div class="col-md-4 d-flex">
				<c:if test="${invoice.status eq  'DRAFT' }">
					<input type="checkbox" class="custom-checkbox" name="includeDelievryAdress" id="includeDelievryAdress" ${invoice.includeDelievryAdress ? 'checked' : ''}  >
				</c:if>
				<c:if test="${invoice.status ne  'DRAFT' }">
					${invoice.includeDelievryAdress ? 'Yes' : 'No'}
				</c:if>
			</div>
		</div>
		
		<div class="form-tander1 requisition-summary-box marg-bottom-20">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label class="set-line-height"><spring:message code="supplier.do.summary.receiver" /></label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<c:if test="${invoice.status eq  'DRAFT' }">
			      <input name="deliveryReceiver" type="text" value="${invoice.deliveryReceiver}" data-validation="required length" data-validation-length="1-150" class="form-control" />
			   </c:if>
			    <c:if test="${invoice.status ne  'DRAFT' }">
				<p class="set-line-height">${invoice.deliveryReceiver}</p>
				</c:if>
			</div>
		</div>
		
		<c:if test="${invoice.status eq  'DRAFT' }">
		<div class="form-tander1">
			<div class="form_field">
				<div class="form-group">
						<div class="form-group">
							<label for="idAdressOne" class="col-sm-3 control-label"><spring:message code="delivery.adds.line1" /> :</label>
								<div class="col-sm-6 col-md-5">
								    <input name="deliveryAddressLine1" type="text" value="${invoice.deliveryAddressLine1}" data-validation="required length" data-validation-length="1-250" class="form-control" placeholder="Address Line 1" />
								</div> 
						</div>
						<div class="form-group">
							<label for="idAdressTwo" class="col-sm-3 control-label"><spring:message code="delivery.adds.line2" /> :</label>
								<div class="col-sm-6 col-md-5">
								    <input name="deliveryAddressLine2" type="text" value="${invoice.deliveryAddressLine2}" data-validation="required length" data-validation-length="1-250" class="form-control" placeholder="Address Line 2" />
								</div> 
						</div>
						<div class="form-group">
							<label for="idAdressCity" class="col-sm-3 control-label"><spring:message code="delivery.city" /> :</label>
								<div class="col-sm-6 col-md-5">
								   <input name="deliveryAddressCity" type="text" value="${invoice.deliveryAddressCity}" data-validation="required length" data-validation-length="1-250" class="form-control" placeholder="City" />
								</div> 
						</div>
						<div class="form-group">
							<label for="idAdressState" class="col-sm-3 control-label"><spring:message code="delivery.state" /> :</label>
								<div class="col-sm-6 col-md-5">
								   <input name="deliveryAddressState" type="text" value="${invoice.deliveryAddressState}" data-validation="required length" data-validation-length="1-150" class="form-control" placeholder="State" />
								</div> 
						</div>
						<div class="form-group">
							<label for="idAdressPostCode" class="col-sm-3 control-label"><spring:message code="delivery.postcode" /> :</label>
								<div class="col-sm-6 col-md-5">
								    <input name="deliveryAddressZip" type="text" value="${invoice.deliveryAddressZip}" data-validation="required length" data-validation-length="1-32" class="form-control" placeholder="Zip Code" />
								</div> 
						</div>
						<div class="form-group">
							<label for="idAdressCountry" class="col-sm-3 control-label"><spring:message code="delivery.country" /> :</label>
								<div class="col-sm-6 col-md-5">
								   <input name="deliveryAddressCountry" type="text" value="${invoice.deliveryAddressCountry}" data-validation="required length" data-validation-length="1-128" class="form-control" placeholder="Country" />
								</div> 
						</div>
					<div id="address-buyer-dialog"></div>
				</div>
			</div>
		</div>	
		</c:if>
		
		<c:if test="${invoice.status ne 'DRAFT'}">
		<div class="form-tander1 requisition-summary-box marg-bottom-20">
			<div class="col-sm-4 col-md-3 col-xs-6">
				<label class="set-line-height"><spring:message code="supplier.do.summary.deliveryadds" /></label>
			</div>
			<div class="col-sm-5 col-md-5 col-xs-6">
				<p class="set-line-height">
						<span class='desc'> ${invoice.deliveryAddressLine1}  <br>
							<c:if test="${not empty invoice.deliveryAddressLine2}"> ${invoice.deliveryAddressLine2} <br></c:if>
							<c:if test="${not empty invoice.deliveryAddressCity}"> ${invoice.deliveryAddressCity} <br></c:if>
							<c:if test="${not empty invoice.deliveryAddressZip}"> ${invoice.deliveryAddressZip} <br></c:if>
							${invoice.deliveryAddressState}, ${invoice.deliveryAddressCountry}
						</span>

				</p>
			</div>
		</div>
		</c:if>

	</div>

	<c:if test="${invoice.status eq 'DRAFT'}">
		<jsp:include page="invoiceDraftItems.jsp" />
	</c:if>
	<c:if test="${invoice.status ne 'DRAFT'}">
		<jsp:include page="invoiceItems.jsp" />
	</c:if>
	
	<div class="upload_download_wrapper clearfix marg-top-10 event_info">
		<h4>Footer Details</h4>
		<c:if test="${poFinanceRequest.status eq 'APPROVED' or poFinanceRequest.status eq 'ACTIVE'}">
		<div class="pad_all_15" id="footerContent">
	 				<b>This invoice has been financed and rights transferred to Finanshere Sdn Bhd.<br> Please pay directly to:<br><br>
					Finanshere Sdn Bhd<br>1397014-X<br>C-21-02, 3 Two Square<br>No. 2, Jalan 19/1<br>46300 Petaling Jaya<br>Selangor Darul Ehsan<br>
					Tel: 03-7967 9600<br><br>Account No:  640320010030075<br>Bank:  Alliance Islamic Bank Malaysia Berhad<br></b>
			</div>
		</c:if>
		
		<c:if test="${(poFinanceRequest !=null and (poFinanceRequest.status ne 'APPROVED' and poFinanceRequest.status ne 'ACTIVE')) or poFinanceRequest == null}">
		<c:if test="${invoice.status eq 'DRAFT'}">
			<div class="form-tander1 requisition-summary-box">
				<label class="col-sm-4 col-md-3 col-xs-6 control-label">Select Footer</label>
				<div class="col-sm-4 col-md-4 col-xs-5 " id="selectFooterDiv">
					<select id="idSelectFooter" name="footer" class="form-control chosen-select" value="${invoice.footer} }" data-validation="required">
						<option value="">Select Footer</option>
						<c:forEach items="${footerList}" var="footer">
							<c:if test="${footer.id eq invoice.footer.id}">
								<option value="${footer.id}" selected>${footer.title}</option>
							</c:if>
							<c:if test="${footer.id ne invoice.footer.id}">
								<option value="${footer.id}" >${footer.title}</option>
							</c:if>

						</c:forEach>
					</select>
				</div>
			</div>
			</c:if>
			<div class="pad_all_15" id="footerContent">
				<span class="md-0 reset-that" >
	 				${invoice.footer.content}
	 			</span>
			</div>
			</c:if>

 	</div>
	<jsp:include page="invoiceAudit.jsp" />
</div>

<c:if test="${invoice.status eq 'DRAFT' or invoice.status eq 'INVOICED'}">
	<div class="row">
			<div class="col-md-12 col-xs-12 col-ms-12 marg-top-20">
				<c:if test="${invoice.status eq 'DRAFT'}">
					<button type="button" id="confirmFinishBtn" role="button" class="btn btn-info ph_btn hvr-pop marg-left-10 right-header-button hvr-rectangle-out  ${ buyerReadOnlyAdmin ? 'disabled' : '' }" data-toggle="modal">
						<spring:message code="summarydetails.mailbox.send" />
					</button>
	 				<button type="button" id="saveDraftBtn" onclick="draftInvoice();"  class="btn btn-black ph_btn hvr-pop marg-left-10 right-header-button skipvalidation" >
						<spring:message code="application.draft" />
					</button>
		 		</c:if>
		 		<c:if test="${!buyerReadOnlyAdmin}">
					<a href="#confirmCancel" id="confirmCancelBtn" role="button" class="btn btn-danger ph_btn hvr-pop marg-left-10 right-header-button" data-toggle="modal"><spring:message code="invoice.application.cancel" /></a>
				</c:if>
 			</div>
	</div>
</c:if>
</form:form>

<div class="modal fade" id="confirmFinish" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="invoice.confirm.finish" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<div class="col-md-12">
					<div class="row">
						<div class="modal-body col-md-12">
							<label> <spring:message code="invoice.send.confirm" />
							</label>
						</div>
					</div>
			</div>
			<%-- <form id="" action="${pageContext.request.contextPath}/supplier/invoiceFinish" method="post"> --%>
				<input type="hidden" name="invoiceId" value="${invoice.id}"> 
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
					<input type="button" id="finishBtn" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left" value="Yes">
					<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
						<spring:message code="application.no2" />
					</button>
				</div>
<%-- 			</form> --%>
			
		</div>
	</div>
</div>

<div class="modal fade" id="confirmCancel" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="invoice.confirm.cancel" />
				</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<form id="" action="${pageContext.request.contextPath}/supplier/cancelInvoice" method="post" acceptCharset="UTF-8">
				<input type="hidden" name="invoiceId" value="${invoice.id}"> 
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<div class="col-md-12">
					<div class="row">
						<div class="modal-body col-md-12">
						<label> <spring:message code="invoice.cancel.confirm"/>
						</label>
						</div>
						<div class="form-group col-md-6">
							<textarea class="width-100" placeholder="Enter Comments.(Mandatory)" rows="3" name="supplierRemark" id="supplierRemark" data-validation="required length" data-validation-length="max500"></textarea>
						</div>
					</div>
				</div>
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
					<input type="submit" id="cancelPo" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out pull-left" value="Yes">
					<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal">
						<spring:message code="application.no2" />
					</button>
				</div>
			</form>

		</div>
	</div>
</div>


<style>
.width-90 {
	width: 90%;
}

div.radio[id^='uniform-']>span {
	margin-top: 0 !important;
}

label.select-radio {
	width: auto;
}

#event {
	padding-left: 0;
}

.input-group.mrg15T.mrg15B {
	background-color: #f5f5f5;
	margin-bottom: 0;
	padding: 0;
}

.margeinAllMDZero {
	margin: 0;
	clear: both;
}

.marginBottomA {
	margin-bottom: 20px;
}

.mem-tab {
	border: 1px solid #ddd;
	border-radius: 2px;
	float: left;
	height: 300px;
	overflow: auto;
	position: relative;
	width: 100%;
}

.box_active {
	background: rgba(0, 0, 0, 0) none repeat scroll 0 0 !important;
}

//
.caret {
	color: #fff !important;
}

.cq_row_parent .input-group-btn {
	width: 200px;
}
/* 
.dropdown-menu input {
	display: inline !important;
	width: auto !important;
} */

.advancee_menu ul {
	top: 100% !important;
	left: 0 !important;
}

.dropdown-menu .checkbox {
	display: inline-block !important;
	margin: 0 !important;
	padding: 0 !important;
	position: relative !important;
}

.dataTables_wrapper.form-inline.no-footer {
	overflow: hidden;
}

.grand-price-heading {
	width: 250px;
}

table.dataTable thead tr.tableHeaderWithongoing th.sorting_asc::after {
	content: "" !important;
}

table.dataTable thead tr.tableHeaderWithongoing th.sorting::after {
	content: "" !important
}

.inactiveCaption {
	margin: 0 0px 0 0px !important;
	font-weight: bold !important;
	color: #ff1d33 !important;
}

.white-space-pre {
	white-space: pre;
}
.buyerAddressRadiosCore.active:hover, .buyerAddressRadiosCore.active.enabledBlockCore
	{
	background: #eee;
}
.buyerAddressRadiosCore.active {
    border: 1px solid #dfe8f1;
    border-radius: 2px;
    display: block !important;
}
.buyerAddressRadiosCore {
    width: 100% !important;
    padding: 10px;
    cursor: pointer;
    display: none;
    -webkit-transition: all 1s;
    transition: all 1s;
}
</style>

<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/datepicker/datepicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/moment.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/timepicker/timepicker.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/timepicker/timepicker.css"/>">
<script type="text/javascript">
$(function() {
	"use strict";
	var nowTemp = new Date();
	var now = new Date(nowTemp.getFullYear(), nowTemp.getMonth(), nowTemp.getDate(), 0, 0, 0, 0);
	
	$('#dueDate').bsdatepicker({
		format : 'dd/mm/yyyy',
		minDate : now,
		onRender : function(date) {
			return date.valueOf() < now.valueOf() ? 'disabled' : '';
		}

	}).on('changeDate', function(e) {
		$(this).blur();
		$(this).bsdatepicker('hide');
	});
});

$('.timepicker-example').timepicker({
	disableFocus : true,
	explicitMode : false
}).on('hide.timepicker', function(e) {
	e.preventDefault();
	$(this).blur();
});
	
$.validate({
	lang : 'en',
	onfocusout : false,
	validateOnBlur : true,
	modules : 'date,sanitize'
});

	$('document').ready(function() {
		$.uniform.update();
		
		$('#footerContentDiv').hide();
		
		$('#finishBtn').on('click', function(e) {
			e.preventDefault();
			if($("#invoicForm").isValid()) {
				$(this).addClass('disabled');
				$('#confirmFinishBtn').addClass('disabled');
				$('#confirmCancelBtn').addClass('disabled');
				$('#saveDraftBtn').addClass('disabled');
				$('#invoicForm').attr('action', getContextPath() + '/supplier/invoiceFinish');
				$("#invoicForm").submit();
			} else {
				return;
			}

		});
		
		$('#confirmFinishBtn').on('click', function(e) {
			if (!$("#invoicForm").isValid()) {
				return;
			}
			$('#finishDo').removeClass('disabled');
			$('#confirmCancelBtn').removeClass('disabled');
			$('#saveDraftBtn').removeClass('disabled');
			$('#confirmFinish').modal('show');
		});

	});

	$('#idSelectFooter').change(function(event) {
		event.preventDefault();
		$('div[id=idGlobalSuccess]').hide();
		$('div[id=idGlobalError]').hide();
		var footerId = $('#idSelectFooter').val();
		if(footerId!=''){
			var header = $("meta[name='_csrf_header']").attr("content");
			var token = $("meta[name='_csrf']").attr("content");
			$.ajax({
				type : "GET",
				url : getContextPath() + "/supplier/getFooterContentById/" + footerId,
				beforeSend : function(xhr) {
					$('#loading').show();
					xhr.setRequestHeader(header, token);
					xhr.setRequestHeader("Accept", "application/json");
					xhr.setRequestHeader("Content-Type", "application/json");
				},
				complete : function() {
					$('#loading').hide();
				},
				success : function(data) {
					$('#footerContent').html(data.content);
					$('#footerContent').show();
					$('#footerContentDiv').show();
				},
				error : function(request, textStatus, errorThrown) {
				}
			});
		}else{
				$('#footerContent').hide();
				$('#footerContentDiv').hide();
			}
	
	});


	$.validate({
		lang : 'en',
		onfocusout : false,
		validateOnBlur : true,
		modules : 'date,sanitize'
	});
	
	//Skip JQuery validations for save draft
	$(".skipvalidation ").on('click', function(e) {
		if ($("#skipper").val() == undefined) {
			e.preventDefault();
			$(this).after("<input type='hidden' id='skipper' value='1'>");
			$('form.has-validation-callback :input').each(function() {
				$(this).on('beforeValidation', function(value, lang, config) {
					$(this).attr('data-validation-skipped', 1);
				});
			});
			$(this).trigger("click");
		}
	});

	function draftInvoice(){
		$('#invoicForm').attr('action', getContextPath() + '/supplier/updateInvoiceItems');
		$("#invoicForm").submit();
	}

	
	$.formUtils.addValidator({ name : 'buyer_address', validatorFunction : function(value, $el, config, language, $form) {
		var response = true;
		var fieldName = $el.attr('name');
		console.log($('[name="' + fieldName + '"]:checked').length);
		if ($('[name="' + fieldName + '"]:checked').length == 0) {
			response = false;
		}
		return response;
	}, errorMessage : 'This is a required field', errorMessageKey : 'badBuyerAddress' });

	$(document).delegate('.delivery_add', 'keyup', function() {
		var $rows = $('.role-bottom-ul li');
		var val = $.trim($(this).val()).replace(/ +/g, ' ').toLowerCase();
		$rows.show().filter(function() {
			var text = $(this).text().replace(/\s+/g, ' ').toLowerCase();
			return !~text.indexOf(val);
		}).hide();
	});

	$(document).delegate('.role-bottom-ul li [type="radio"]', 'click', function() {
		var dataAddress = $(this).closest('li').children('.del-add').html();
		$('.phisicalArressBlock').html(dataAddress);
		$('.physicalCriterion input[type="checkbox"]').prop('checked', true);
		$('.buyerAddressRadios').addClass('active enabledBlock');
		$.uniform.update();
		$("#sub-credit").slideUp();
	});
	$(document).delegate('#deletecorpAddress', 'click', function() { 
	 $(".buyerAddressRadios").removeClass("active");
	 $('#sub-credit input[type="radio"]').prop('checked', false);
	 $.uniform.update();
	 $("#sub-credit").slideDown();
	});
	$(document).delegate('.phisicalArressBlock', 'click', function(event) { 
		$("#sub-credit").slideToggle();
	});
	
	//Billing
	$.formUtils.addValidator({ name : 'buyer_correspondenceaddress', validatorFunction : function(value, $el, config, language, $form) {
		var response = true;
		var fieldName = $el.attr('name');
		console.log($('[name="' + fieldName + '"]:checked').length);
		if ($('[name="' + fieldName + '"]:checked').length == 0) {
			response = false;
		}
		return response;
	}, errorMessage : 'This is a required field', errorMessageKey : 'badBuyerAddresscor' });
	
	$(document).delegate('.delivery_corresadd', 'keyup', function() {
		var $rows = $('.role-bottom-ul-core li');
		var val = $.trim($(this).val()).replace(/ +/g, ' ').toLowerCase();
		$rows.show().filter(function() {
			var text = $(this).text().replace(/\s+/g, ' ').toLowerCase();
			return !~text.indexOf(val);
		}).hide();
	});
	$(document).delegate('.role-bottom-ul-core li [type="radio"]', 'click', function() {
		var dataAddress = $(this).closest('li').children('.del-add-cor').html();
		$('.phisicalArressBlockCore').html(dataAddress);
		$('.physicalCriterionCore input[type="checkbox"]').prop('checked', true);
		$('.buyerAddressRadiosCore').addClass('active enabledBlockCore');
		$.uniform.update();
		$("#sub-credit-core").slideUp();
	});
	
	$(document).delegate('#deletecorpAddressCore', 'click', function() { 
		 $(".buyerAddressRadiosCore").removeClass("active");
		 $('#sub-credit-core input[type="radio"]').prop('checked', false);
		 $.uniform.update();
		 $("#sub-credit-core").slideDown();
		});
		$(document).delegate('.phisicalArressBlockCore', 'click', function(event) { 
			$("#sub-credit-core").slideToggle();
		});

</script>


<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/cq_form.css"/>">

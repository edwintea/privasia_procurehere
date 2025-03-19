<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<div class="clear"></div>
<div class="white-bg border-all-side float-left width-100 pad_all_15">
 		<div class="col_12">

 		<div class=" marg-top-10 event_info">
			<h4>FinansHere Request Details</h4>
			<div class="form-tander1 requisition-summary-box marg-bottom-20">
				<div class="col-sm-4 col-md-3 col-xs-6">
					<label class="set-line-height">Request Date</label>
				</div>
				<div class="col-sm-5 col-md-5 col-xs-6">
					<p class="set-line-height"><fmt:formatDate value="${poFinanceRequest.requestedDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" /></p>
				</div>
			</div>
			<div class="form-tander1 requisition-summary-box marg-bottom-20">
				<div class="col-sm-4 col-md-3 col-xs-6">
					<label class="set-line-height">Request By</label>
				</div>
				<div class="col-sm-5 col-md-5 col-xs-6">
					<p class="set-line-height">${poFinanceRequest.requestedBy}</p>
				</div>
			</div>
			<div class="form-tander1 requisition-summary-box marg-bottom-20">
				<div class="col-sm-4 col-md-3 col-xs-6">
					<label class="set-line-height">Request Status</label>
				</div>
				<div class="col-sm-5 col-md-5 col-xs-6">
					<p class="set-line-height">${poFinanceRequest.status}</p>
				</div>
			</div>
			<div class="form-tander1 requisition-summary-box marg-bottom-20">
				<div class="col-sm-4 col-md-3 col-xs-6">
					<label class="set-line-height">Approved By</label>
				</div>
				<div class="col-sm-5 col-md-5 col-xs-6">
					<p class="set-line-height">${poFinanceRequest.approvedBy != null ? poFinanceRequest.approvedBy : 'N/A'}</p>
				</div>
			</div>
			<div class="form-tander1 requisition-summary-box marg-bottom-20">
				<div class="col-sm-4 col-md-3 col-xs-6">
					<label class="set-line-height">Approved Date</label>
				</div>
				<div class="col-sm-5 col-md-5 col-xs-6">
					<p class="set-line-height">
					<c:if test="${!empty poFinanceRequest.approvedDate}">
						<fmt:formatDate value="${poFinanceRequest.approvedDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
					</c:if>
					<c:if test="${empty poFinanceRequest.approvedDate}">
						N/A
					</c:if>
					</p>
				</div>
			</div>
		</div>
 		
 		<div class="upload_download_wrapper clearfix marg-top-10 event_info">
			<h4>Documents</h4>
			<div class="form-tander1 requisition-summary-box marg-bottom-20">
				<div class="col-md-3">
					<label class="set-line-height">Offer Document</label>
				</div>
				<div class="col-md-9">
					<div class="set-line-height">
						<c:if test="${poFinanceRequest != null && poFinanceRequest.status != 'REQUESTED'}">
							<a href="${pageContext.request.contextPath}/supplier/downloadFinansHereOfferDocument/${poFinanceRequest.id}" role="button" data-toggle="tooltip" data-placement="top" title="Download"><i class="fa fa-download fa-lg" aria-hidden="true"></i> Download FinansHere Offer</a>
						</c:if>
						<c:if test="${poFinanceRequest == null || ( poFinanceRequest != null && poFinanceRequest.status == 'REQUESTED')}">
							N/A
						</c:if>
					</div>
				</div>
				<div class="col-md-3">
					<div class="set-line-height">
						<c:if test="${poFinanceRequest.status == 'APPROVED' }">
							<div>
							<form:form method="post" action="${pageContext.request.contextPath}/supplier/acceptFinanshereOffer" style="display: inline-block">
								<input type="hidden" name="requestId" value="${poFinanceRequest.id}" />
								<input type="hidden" name="poId" value="${po.id}" />
								<button id="acceptPo" type="submit" class="btn ph_btn_small btn-success hvr-pop" data-toggle="tooltip" data-placement="top" data-original-title='Accept Offer'>
									<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-check"></i></span>
									 <span class="button-content">Accept Offer</span>
								</button>
							</form:form>
							<form:form method="post" action="${pageContext.request.contextPath}/supplier/declineFinanshereOffer" style="display: inline-block">
								<input type="hidden" name="requestId" value="${poFinanceRequest.id}" />
								<input type="hidden" name="poId" value="${po.id}" />
								<button id="declinedPo" type="submit" class="btn ph_btn_small btn-danger hvr-pop" data-toggle="tooltip" data-placement="top" data-original-title='Decline Offer'>
									<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-times"></i></span>
									<span class="button-content">Decline Offer</span>
								</button>
							</form:form>
							</div>
						</c:if>
					</div>
				</div>
			</div>
			<div class="form-tander1 requisition-summary-box marg-bottom-20">
				<div class="col-sm-4 col-md-3 col-xs-6">
					<label class="set-line-height">Offer Status</label>
				</div>
				<div class="col-sm-5 col-md-5 col-xs-6">
					<p class="set-line-height">
						${poFinanceRequest.acceptedDate != null ? 'ACCEPTED' : (poFinanceRequest.declinedDate != null ? 'DECLINED' : 'N/A')  }
					</p>
				</div>
			</div>
			<div class="form-tander1 requisition-summary-box marg-bottom-20">
				<div class="col-sm-4 col-md-3 col-xs-6">
					<label class="set-line-height">Offer Accepted/Decline Date</label>
				</div>
				<div class="col-sm-5 col-md-5 col-xs-6">
					<p class="set-line-height">
						<c:if test="${!empty poFinanceRequest.acceptedDate || !empty poFinanceRequest.declinedDate}">
							<fmt:formatDate value="${poFinanceRequest.acceptedDate != null ? poFinanceRequest.acceptedDate : poFinanceRequest.declinedDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
						</c:if>
						<c:if test="${empty poFinanceRequest.acceptedDate && empty poFinanceRequest.declinedDate}">
							N/A
						</c:if>
					</p>
				</div>
			</div>
			<div class="form-tander1 requisition-summary-box marg-bottom-20">
				<div class="col-sm-4 col-md-3 col-xs-6">
					<label class="set-line-height">Accepted/Decline By</label>
				</div>
				<div class="col-sm-5 col-md-5 col-xs-6">
					<p class="set-line-height">${poFinanceRequest.acceptedBy != null ? poFinanceRequest.acceptedBy : (poFinanceRequest.declinedBy != null ?  poFinanceRequest.declinedBy : 'N/A')}</p>
				</div>
			</div>
			
			<div class="form-tander1 requisition-summary-box">
				<div class="col-md-4">
					<label class="set-line-height">Other Documents</label>
				</div>
				<div class="col-md-12">
					<table  class="display table table-bordered noarrow marg-top-20" cellspacing="0" width="100%">
						<thead>
							<tr class="tableHeaderWithSearch">
								<th class="align-left width_200_fix">Document Name</th>
								<th class="align-left width_200_fix">Description</th>
								<th class="align-left width_200_fix">File Name</th>
								<th class="align-left width_150_fix">Uploaded By</th>
								<th class="align-left width_150_fix">Uploaded Date</th>
								<th class="align-left width_150_fix">Document Type</th>
							</tr>
						</thead>
						<tbody>
							<c:if test="${empty poFinanceRequestDocs}">
								<tr>
									<td colspan="6">
										No Documents.
									</td>
								</tr>
							</c:if>
							<c:forEach items="${poFinanceRequestDocs}" var="doc">
								<tr id="${doc.id}">
									<td>
										<a href="${pageContext.request.contextPath}/supplier/downloadFinansHerePoDocument/${doc.id}" role="button" data-toggle="modal">${doc.documentName}</a>
									</td>
									<td>
										${doc.documentDescription}
									</td>

									<td>
										${doc.fileName}
									</td>
										<td>
										${doc.uploadedBy}
									</td>
									<td>
										<fmt:formatDate value="${doc.uploadDate}" pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
									</td>
									<td>
										${doc.documentType}
									</td>

								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
		</div>
 		
		<section class="index_table_block">
			<div class="row">
				<div class="col-xs-12">
				
				
				
						
					</div>
			</div>
		</section>
	</div>
</div>
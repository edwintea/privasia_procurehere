<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<sec:authorize access="hasRole('BUYER_CHANGE_EMAIL') or (hasRole('ADMIN') and hasRole('OWNER'))" var="canUpdateComEmail" />
<style>
.form-left form {
	float: left;
	padding-left: 5px;
}

.form-cursor-hand form:hover {
	cursor: pointer;
}

#idTblPaymentHistory th input {
	min-width: 200px;
	text-align: center;
}

.ph_table.dataTable th {
	border-left: 0px solid #ddd !important;
	border-bottom: 0px solid #ddd !important;
}

table.dataTable {
	clear: both;
	max-width: none !important;
	margin-top: 0px !important;
	margin-bottom: 0px !important;
}
</style>
<div id="page-content-wrapper">
	<div id="page-content">
		<div class="container">
			<ol class="breadcrumb">
				<li><a href="${pageContext.request.contextPath}/owner/ownerDashboard"><spring:message code="application.dashboard"/></a></li>
				<li><a href="${pageContext.request.contextPath}/owner/buyerList"><spring:message code="buyercreation.list"/></a></li>
				<li class="active"><spring:message code="buyer.detail.label"/></li>
			</ol>
			<!-- page title block -->
			<div class="Section-title title_border gray-bg">
				<h2 class="trans-cap supplier"><spring:message code="buyer.detail.label"/></h2>
				<div class="trans-cap form-cursor-hand form-left" style="float: right; margin-right: 0px;">
					<sec:authorize access="hasRole('BUYER_ACCOUNT_MGMT') or (hasRole('ADMIN') and hasRole('OWNER'))" var="canManage">
						<form>
							<c:if test="${buyerObj.status == 'ACTIVE'}">
								<div class="panel-heading" id="opensupDetail"><spring:message code="buyerdetails.suspend.buyer"/></div>
							</c:if>
							<c:if test="${buyerObj.status == 'SUSPENDED'}">
								<div class="panel-heading" id="openActDetail"><spring:message code="buyerdetails.activate.buyer"/></div>
							</c:if>
						</form>
						<c:if test="${!buyerObj.registrationComplete}">
							<form name="form" method="post" action="${pageContext.request.contextPath}/owner/resendBuyerActivationLink/${buyerObj.id}">
								<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> <input type="hidden" id="buyerId" name="buyerId" value="${buyerObj.id}" />
								<div id="idResendBuyerActivationEmail" data-toggle="tooltip" data-placement="top" title="Resend Activation Email" data-original-title="Resend Activitation Email" class="panel-heading"><spring:message code="supplier.resend.activation.email"/></div>
							</form>
						</c:if>
						<c:if test="${adminUser != null}">
							<form:form name="form" method="post" action="${pageContext.request.contextPath}/owner/toggleErpStatus" modelAttribute="buyerObj">
								<div data-toggle="tooltip" data-placement="top" title="${buyerObj.erpEnable ? 'Click to enable' : 'Click to disable'}" data-original-title="${buyerObj.erpEnable ? 'Click to enable' : 'Click to disable'}" class="panel-heading" onclick="$(this).closest('form').submit();">
									<form:hidden name="id" path="id" />
									<input type="hidden" name="buyerId" value="${buyerObj.id}" /> <spring:message code="buyerdetails.erp.integration"/> : <span style="color:${buyerObj.erpEnable ? 'green' : 'red'}"> ${buyerObj.erpEnable ? '<i class="fa fa-unlock" aria-hidden="true"></i>' : '<i class="fa fa-lock" aria-hidden="true"></i>'} </span>
								</div>
							</form:form>

							<form:form name="form" method="post" action="${pageContext.request.contextPath}/owner/toggleAdminAccountLockedStatus" modelAttribute="adminUser">
								<div data-toggle="tooltip" data-placement="top" title="${adminUser.locked ? 'Click to unclock' : 'Click to lock'}" data-original-title="${adminUser.locked ? 'Click to unclock' : 'Click to lock'}" class="panel-heading" onclick="$(this).closest('form').submit();">
									<form:hidden name="id" path="id" />
									<input type="hidden" name="buyerId" value="${buyerObj.id}" /> <spring:message code="supplier.admin.account.status"/> : <span style="color:${adminUser.locked ? 'red' : 'green'}"> ${adminUser.locked ? '<i class="fa fa-lock" aria-hidden="true"></i>' : '<i class="fa fa-unlock" aria-hidden="true"></i>'} </span>
								</div>
							</form:form>

						</c:if>
						<form name="form" method="post" action="${pageContext.request.contextPath}/owner/allowSupplierUpload/${buyerObj.id}">
							<div data-toggle="tooltip" data-placement="top" title="${buyerObj.allowSupplierUpload ? 'Click to lock' : 'Click to unclock'}" data-original-title="${buyerObj.allowSupplierUpload ? 'Click to lock' : 'Click to unclock'}" class="panel-heading" onclick="$(this).closest('form').submit();">
								<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> <input type="hidden" name="buyerId" value="${buyerObj.id}" /> <input type="hidden" name="supplierUpload" value="${!buyerObj.allowSupplierUpload}" /> <spring:message code="buyerdetails.allow.supplier.upload"/> <span
									style="color:${buyerObj.allowSupplierUpload ? 'green' : 'red'}"> ${buyerObj.allowSupplierUpload ? '<i class="fa fa-unlock" aria-hidden="true"></i>' : '<i class="fa fa-lock" aria-hidden="true"></i>'} </span>
							</div>
						</form>
					</sec:authorize>
				</div>
			</div>
			<div class="clear"></div>
			<jsp:include page="/WEB-INF/views/jsp/templates/message.jsp" />
			<jsp:include page="/WEB-INF/views/jsp/templates/ajaxMessage.jsp" />
			<div class="col-md-12 pad0">
				<div class="panel bgnone">
					<div class="panel-body">
						<div class="example-box-wrapper">
							<div class="panel-group" id="accordion">
								<div class="panel">
									<div class="panel-heading">
										<h4 class="panel-title">
											<a data-toggle="collapse" data-parent="#accordion" href="#collapseOne"> <spring:message code="buyerdetails.company.detail"/>
												<p style="float: right; margin-right: 30px;"><spring:message code="buyercreation.status"/>: ${buyerObj.status}</p>
											</a>
										</h4>
									</div>
									<div id="collapseOne" class="panel-collapse collapse in">
										<div class="panel-body">
											<table class="tabaccor buyerdetail" cellpadding="0" cellspacing="0" border="0">
												<tr>
													<td><strong><spring:message code="buyer.profilesetup.companyname"/></strong></td>
													<td>${buyerObj.companyName}</td>
												</tr>
												<tr>
													<td><strong><spring:message code="supplier.registration.company.number" /></strong></td>
													<td>${buyerObj.companyRegistrationNumber}</td>
												</tr>
												<tr>
													<td><strong><spring:message code="label.companystatus" /></strong></td>
													<td>${buyerObj.companyStatus.companystatus}</td>
												</tr>
												<tr>
													<td><strong><spring:message code="supplier.year.of.establish" /></strong></td>
													<td>${buyerObj.yearOfEstablished}</td>
												</tr>
												<tr>
													<td><strong><spring:message code="supplier.company.address" /></strong></td>
													<td>${buyerObj.line1},<br> ${buyerObj.line2},<br> ${buyerObj.city}<br> ${buyerObj.registrationOfCountry.countryName}<br>
													</td>
												</tr>
												<tr>
													<td><strong><spring:message code="supplier.company.tel.number" /></strong></td>
													<td>${buyerObj.companyContactNumber}</td>
												</tr>
												<tr>
													<td><strong><spring:message code="supplier.company.fax.number" /></strong></td>
													<td>${buyerObj.faxNumber}</td>
												</tr>
												<tr>
													<td><strong><spring:message code="supplier.company.website" /></strong></td>
													<td>${buyerObj.companyWebsite}</td>
												</tr>
												<tr>
													<td><strong><spring:message code="supplier.primary.login.email" /></strong></td>
													<td>${buyerObj.loginEmail}</td>
												</tr>
												<tr>
													<td><strong><spring:message code="supplier.communication.email.address" /></strong></td>
													<td><span class="bEmail">${buyerObj.communicationEmail}</span> <c:if test="${canUpdateComEmail}">
															<input type="hidden" value="" id="editEmailValue">
															<a href="#" data-mail="${buyerObj.communicationEmail}" id="edit_mail"> <i class="fa fa-pencil-square-o" aria-hidden="true"></i>
															</a>
														</c:if></td>
												</tr>
												<tr>
													<td><strong>Public Context Path</strong></td>
													<td><span class="bPublicContextPath">${buyerObj.publicContextPath}</span>
														<input type="hidden" value="" id="editPublicContextPathValue">
															<a href="#" data-path="${buyerObj.publicContextPath}" id="edit_PublicContextPath"> <i class="fa fa-pencil-square-o" aria-hidden="true"></i>
														</a>
													</td>
												</tr>
											</table>
										</div>
									</div>
								</div>
								<div class="panel">
									<div class="panel-heading">
										<h4 class="panel-title">
											<a data-toggle="collapse" data-parent="#accordion" href="#collapseTwo"><spring:message code="supplier.primary.contact" /> </a>
										</h4>
									</div>
									<div id="collapseTwo" class="panel-collapse collapse">
										<div class="panel-body">
											<table class="tabaccor" cellpadding="0" cellspacing="0" border="0">
												<tr>
													<td><strong><spring:message code="supplier.primary.contact" /></strong></td>
													<td>${buyerObj.fullName}</td>
												</tr>
												<tr>
													<td><strong><spring:message code="supplier.primary.login.email" /></strong></td>
													<td>${buyerObj.loginEmail}</td>
												</tr>
												<tr>
													<td><strong><spring:message code="supplier.communication.email.address" /></strong></td>
													<td><span class="bEmail">${buyerObj.communicationEmail}</span></td>
												</tr>
												<tr>
													<td><strong><spring:message code="supplier.primary.mob.number" /></strong></td>
													<td>${buyerObj.mobileNumber}</td>
												</tr>
											</table>
										</div>
									</div>
								</div>
								<div class="panel">
									<div class="panel-heading">
										<h4 class="panel-title">
											<a data-toggle="collapse" data-parent="#accordion" href="#collapseThree"><spring:message code="buyerdetails.payment.history"/> </a>
										</h4>
									</div>
									<div id="collapseThree" class="panel-collapse collapse">
										<div class="panel-body">
											<div class="Invited-Supplier-List dashboard-main">
												<div class="Invited-Supplier-List-table add-supplier paymetnh bd_brd">
													<div class="ph_tabel_wrapper">
														<div class="ph_table_border marg-bottom-20 mega">
															<table id="idTblPaymentHistory" class="ph_table display table table-bordered noarrow" width="100%" border="0" cellspacing="0" cellpadding="0">
																<thead>
																	<tr class="tableHeaderWithSearch">
																		<%-- <th search-type="" class="align-center width_200 width_200_fix">
																			<spring:message code="application.action" />
																		</th> --%>
																		<th search-type="text" class="align-center width_200 width_200_fix"><spring:message code="buyerdetails.subscription.plan"/></th>
																		<th search-type="text" class="align-center width_200 width_200_fix"><spring:message code="buyerdetails.transacrion.ref"/></th>
																		<th search-type="" class="align-center width_300 width_300_fix"><spring:message code="eventsummary.listdocuments.datetime" /></th>
																		<th search-type="text" class="align-center width_200 width_200_fix"><spring:message code="account.overview.amount"/></th>
																		<th search-type="select" search-options="paymentStatusTypeList" class="align-center width_200 width_200_fix"><spring:message code="application.status" /></th>
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
								<div class="panel">
									<div class="panel-heading">
										<h4 class="panel-title">
											<a data-toggle="collapse" data-parent="#accordion" href="#collapseFour"><spring:message code="buyerdetails.subscription.details"/> </a>
										</h4>
									</div>
									<div id="collapseFour" class="panel-collapse collapse">
										<div class="panel-body">
											<table class="tabaccor" cellpadding="0" cellspacing="0" border="0">
												<tr>
													<td><strong><spring:message code="account.overview.plan"/></strong></td>
													<td><b>${buyerObj.buyerPackage == null ? "" : buyerObj.buyerPackage.plan.planName}</b> - ${buyerObj.buyerPackage == null ? "" : buyerObj.buyerPackage.plan.shortDescription}</td>
												</tr>
												<%-- <tr>
													<td>
														<strong>Trial Period</strong>
													</td>
													<td>
														<c:if test="${buyerObj.buyerPackage != null and buyerObj.buyerPackage.trialStartDate != null}">
															<fmt:formatDate value="${buyerObj.buyerPackage.trialStartDate}" pattern="dd/MM/yyyy" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" /> to 
															<fmt:formatDate value="${buyerObj.buyerPackage.trialEndDate}" pattern="dd/MM/yyyy" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
														</c:if>
														<c:if test="${buyerObj.buyerPackage != null and buyerObj.buyerPackage.trialStartDate == null}">
														Not Applicable
														</c:if>
													</td>
												</tr> --%>
												<tr>
													<td><strong><spring:message code="account.overview.subscription.valid" /></strong></td>
													<td><c:if test="${buyerObj.buyerPackage != null}">
															<fmt:formatDate value="${buyerObj.currentSubscription.startDate}" pattern="dd/MM/yyyy"/> to 
															<fmt:formatDate value="${buyerObj.currentSubscription.endDate}" pattern="dd/MM/yyyy"/>
														</c:if></td>
												</tr>
												<tr>
													<td><strong><spring:message code="buyerdetails.no.users"/></strong></td>
													<td><c:if test="${buyerObj.buyerPackage != null}">
																${buyerObj.currentSubscription.userQuantity}
															</c:if></td>
												</tr>
												<tr>
													<td><strong><spring:message code="buyerdetails.no.events"/></strong></td>
													<td><c:if test="${buyerObj.buyerPackage != null}">
																${buyerObj.currentSubscription.eventQuantity}
															</c:if></td>
												</tr>
												<%-- <tr>
														<td>
															<strong>Quantity</strong>
														</td>
														<td>
															<c:if test="${buyerObj.buyerPackage != null}">
																<c:if test="${buyerObj.buyerPackage.planQuantity == 0}">
															${buyerObj.buyerPackage.plan.trialPeriod} ${buyerObj.buyerPackage.plan.trialPeriodUnit}S
															</c:if>
																<c:if test="${buyerObj.currentSubscription.planQuantity > 0}">
															${buyerObj.buyerPackage.planQuantity} ${buyerObj.buyerPackage.plan.periodUnit}S
															</c:if>
															</c:if>
														</td>
													</tr> --%>
												<tr>
													<td></td>
													<td><c:if test="${buyerObj.buyerPackage != null}">
															<button class="btn btn-info ph_btn_small btn-margin-top hvr-pop hvr-rectangle-out" type="button" id="changeSubscription"><spring:message code="application.change"/></button>
														</c:if></td>
												</tr>

											</table>
											<h4 style="padding-top: 25px;"></h4>
											<div class="ph_table_border margin">
												<div class="mega range-header">

													<table class="header ph_table border-none" width="100%">
														<thead>
															<tr>
																<th class="width_200 width_200_fix align-left"><strong><spring:message code="account.overview.subscription.valid" /></strong></th>
																<th class="width_200 width_200_fix align-left"><strong><spring:message code="plan.planName" /></strong></th>
																<th class="width_200 width_200_fix align-left"><strong><spring:message code="promotion.title" /></strong></th>
																<th class="width_200 width_200_fix align-left"><strong><spring:message code="supplier.subscription.status" /></strong></th>
																<th class="width_200 width_200_fix align-left"><strong><spring:message code="supplier.subscription.date" /></strong></th>
															</tr>
														</thead>
													</table>
													<table class="data ph_table border-none" width="100%">
														<tbody>
															<c:if test="${empty subscriptionsList}">
																<tr>
																	<td class="width_200 width_200_fix align-left"><spring:message code="supplier.no.data.available" /></td>
																</tr>
															</c:if>
															<c:forEach items="${subscriptionsList}" var="trans">
																<tr>
																	<td class="width_200 width_200_fix align-left"><fmt:formatDate value="${trans.startDate}" pattern="dd/MM/yyyy" /> <spring:message code="application.to1" /> <fmt:formatDate value="${trans.endDate}" pattern="dd/MM/yyyy"
																			 /></td>
																	<td class="width_200 width_200_fix align-left">${trans.plan != null ? trans.plan:"N/A" }</td>
																	<td class="width_200 width_200_fix align-left">${trans.promoCode}</td>
																	<td class="width_200 width_200_fix align-left"><c:choose>
																			<c:when test="${trans.subscriptionStatus eq SubscriptionStatus.FUTURE}">
																				<span style="color: blue;">${trans.subscriptionStatus}</span>
																				<br />
																			</c:when>
																			<c:otherwise>
																				<span style="color: red;">${trans.subscriptionStatus}</span>
																				<br />
																			</c:otherwise>
																		</c:choose></td>
																	<td class="width_200 width_200_fix align-left"><fmt:formatDate value="${trans.createdDate}" pattern="dd/MM/yyyy" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" /></td>

																</tr>
															</c:forEach>
														</tbody>
													</table>
												</div>
											</div>
										</div>
									</div>
									
									
									
									
								</div>
								<div class="panel">
									<div class="panel-heading">
										<h4 class="panel-title">
											<a data-toggle="collapse" data-parent="#accordion" href="#collapseFive"><spring:message code="owner.metric" /></a>
										</h4>
									</div>
									<div id="collapseFive" class="panel-collapse collapse">
										<div class="panel-body">
											<div class="Section-title title_border marg-bottom-10 white-bg">
												<form>
													<div class="metric_date">
														<label><spring:message code="application.startdate" /></label> <input id="metric-start" autocomplete="off" name="metric-start" type="text" data-validation="required" class="bootstrap-datepicker form-control for-clander-view">
													</div>
													<div class="metric_date">
														<label><spring:message code="rfaevent.end.date"/></label> <input id="metric-end"  autocomplete="off" type="text" data-validation="required" class="bootstrap-datepicker form-control for-clander-view">
													</div>
													<div class="metric_search">
														<button class="btn btn-info ph_btn_midium hvr-pop hvr-rectangle-out" type="submit"><spring:message code="application.search"/></button>
													</div>
													<div class="metric_date marg-top-10">
														<label><spring:message code="buyerdetails.display.by" /></label> <select class="custom-select">
															<option><spring:message code="buyerdetails.daily" /></option>
															<option><spring:message code="buyerdetails.weekly" /></option>
															<option><spring:message code="buyerdetails.monthly" /></option>
															<option><spring:message code="buyerdetails.qualrtly" /></option>
															<option><spring:message code="buyerdetails.yearly" /></option>
														</select>
													</div>
												</form>
											</div>
											<div class="clear"></div>
											<table class="tabaccor" cellpadding="0" cellspacing="0" border="0">
												<tr>
													<td><strong><spring:message code="application.lable.buyer" /></strong></td>
													<td><b>${buyerObj.companyName}</b></td>
												</tr>
												<tr>
													<td><strong><spring:message code="buyerdetails.numbers.users" /></strong></td>
													<td><b>${buyerObj.buyerPackage.noOfUsers}/${buyerObj.buyerPackage.userLimit}</b></td>
												</tr>
												<tr>
													<td><strong><spring:message code="buyerdetails.numbers.users" /></strong></td>
													<td><b>${buyerObj.buyerPackage.noOfUsers}/${buyerObj.buyerPackage.userLimit}</b></td>
												</tr>
												<tr>
													<td><strong><spring:message code="buyerdetails.number.events" /></strong></td>
													<td><b>${buyerObj.buyerPackage.noOfEvents}/${buyerObj.buyerPackage.eventLimit}</b></td>
												</tr>
												<tr>
													<td><strong><spring:message code="buyerdetails.total.time.taken" /></strong></td>
													<td><b>${totalTimeTaken}</b></td>
												</tr>
												<tr>
													<td><strong><spring:message code="buyerdetails.events.per.category" /></strong></td>
													<td><b>${eventsPerCategory}</b></td>
												</tr>
												<tr>
													<td><strong><spring:message code="buyerdetails.events.cancelled" /></strong></td>
													<td><b>${eventsCancelled}</b></td>
												</tr>
												<tr>
													<td><strong><spring:message code="buyerdetails.no.suppliers" /></strong></td>
													<td><b>${numberOfSuppliers}</b></td>
												</tr>
												<tr>
													<td><strong><spring:message code="buyerdetails.pr.count" /></strong></td>
													<td><b>${prCount}</b></td>
												</tr>
												<tr>
													<td><strong><spring:message code="buyerdetailspo.count" /></strong></td>
													<td><b>${poCount}</b></td>
												</tr>
												<tr>
													<td><strong><spring:message code="buyerdetails.auction.total.saving" /></strong></td>
													<td><b>${auctionTotalSavings}</b></td>
												</tr>
												<tr>
													<td><strong><spring:message code="buyerdetails.auction.avg.saving" /></strong></td>
													<td><b>${auctionAverageSavings}</b></td>
												</tr>
											</table>
										</div>
									</div>
								</div>
								<sec:authorize access="(hasRole('ADMIN') or hasRole('ROLE_BUYER_NOTES'))">
									<div class="panel">
										<div class="panel-heading">
											<h4 class="panel-title">
												<a data-toggle="collapse" data-parent="#accordion" href="#collapseSix"><spring:message code="supplier.Notes" /> </a>
											</h4>
										</div>
										<div id="collapseSix" class="panel-collapse collapse">
											<div class="panel-body">
												<sec:authorize access="(hasRole('ADMIN') or hasRole('ROLE_BUYER_NOTES_EDIT'))">
													<div class="Invited-Supplier-List create_sub note marg-bottom-20">
														<div class="col-md-8 notesAddBlock">
															<input type="hidden" id="buyerId" value="${buyerObj.id}" />
															<h3 class="blue_form_sbtitle pad-tb-30 add_file text-black"></h3>
															<c:if test="${not empty errors }">
																<div class="alert alert-danger" id="idGlobalError">
																	<div class="bg-red alert-icon">
																		<i class="glyph-icon icon-times"></i>
																	</div>
																	<div class="alert-content">
																		<h4 class="alert-title">Error</h4>
																		<p id="idGlobalErrorMessage">
																			<c:forEach var="error" items="${errors}">
																					${error}<br />
																			</c:forEach>
																		</p>
																	</div>
																</div>
															</c:if>
															<div class="row marg-bottom-20">
																<div class="col-md-3">
																	<label class="marg-top-10"><spring:message code="supplier.incident.type" /> :</label>
																</div>
																<div class="dd">
																	<select class="custom-select incidentType" name="" data-parsley-id="0644">
																		<option selected><spring:message code="supplier.complaint.opt" /></option>
																		<option><spring:message code="supplier.registered.opt" /></option>
																		<option><spring:message code="supplier.transacted.opt" /></option>
																	</select>
																</div>
															</div>
															<div class="row marg-bottom-10">
																<div class="col-md-3">
																	<label class="marg-top-10"><spring:message code="application.description"/> :</label>
																</div>
																<div class="form-group textarea">
																	<textarea placeholder='<spring:message code="costcenter.placeholder.description"/>' required name="" id="description" rows="3" class="form-control" data-parsley-id="7481"></textarea>
																	<ul class="parsley-errors-list" id="parsley-id-7481"></ul>
																</div>
															</div>
															<div class="row marg-bottom-10">
																<div class="col-md-3"></div>
																<section class="step4_form selectfile">
																	<div class="col-md-3 marg-bottom-20 addnote marg-top-20" style="padding-left: 0px;">
																		<button class="btn btn-info ph_btn_midium btn-margin-top hvr-pop hvr-rectangle-out" type="button" id="addBuyerNotes"><spring:message code="supplier.add.note.btn" /></button>
																	</div>
																</section>
															</div>
														</div>
														<div class="col-md-4 openAddNots">
															<h3 class="blue_form_sbtitle pad-tb-30 add_file text-black"></h3>
															<button class="btn btn-info ph_btn_midium btn-margin-top hvr-pop hvr-rectangle-out"><spring:message code="supplier.add.note.btn" /></button>
														</div>
													</div>
												</sec:authorize>
												<div class="row">
													<div class="col-xs-12">
														<div class="ph_tabel_wrapper scrolableTable_UserList">
															<table id="buyerNoteDetails" class="ph_table display table table-bordered noarrow" cellspacing="0" width="100%">
																<thead>
																	<tr>
																		<th><spring:message code="eventsummary.listdocuments.datetime" /></th>
																		<th><spring:message code="application.description"/></th>
																		<th><spring:message code="supplier.incident.type" /></th>
																	</tr>
																</thead>
																<tbody>
																</tbody>
															</table>
														</div>
														<div id="morris-bar-yearly" class="graph" style="visibility: hidden"></div>
													</div>
												</div>
											</div>
										</div>
									</div>
								</sec:authorize>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<div class="modal fade" title="Enter password " id="suppDetailModal" role="dialog">
	<div class="modal-dialog">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<button class="close for-absulate" type="button" data-dismiss="modal">X</button>
			</div>
			<form action="${pageContext.request.contextPath}/owner/suspendBuyer" method="post">
				<input type="hidden" name="buyerId" value="${buyerObj.id}"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<div class="modal-body">
					<div class="row">
						<label class="col-sm-2 control-label"> <spring:message code="Product.remarks" /> </label>
						<div class="col-sm-6 col-md-6">
							<textarea id="remarks" name="remarks" class="form-control mar-b10" maxlength="250"> </textarea>
						</div>
						<div class="col-sm-4 col-md-4">
							<button class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out marg-left-20" type="submit"><spring:message code="application.submit" /></button>
						</div>
					</div>
				</div>
			</form>
		</div>
	</div>
</div>
<div class="modal fade" title="Activate Buyer " id="buyerDetailModal" role="dialog">
	<div class="modal-dialog">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<button class="close for-absulate" type="button" data-dismiss="modal">X</button>
			</div>
			<form action="${pageContext.request.contextPath}/owner/activateBuyer" method="post">
				<input type="hidden" name="buyerId" value="${buyerObj.id}"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<div class="modal-body">
					<div class="row">
						<label class="col-sm-2 control-label"> <spring:message code="Product.remarks" /> </label>
						<div class="col-sm-6 col-md-6">
							<textarea id="remarks" name="remarks" class="form-control mar-b10" maxlength="250"> </textarea>
						</div>
						<div class="col-sm-4 col-md-4">
							<button class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out marg-left-20" type="submit"><spring:message code="application.submit" /></button>
						</div>
					</div>
				</div>
			</form>
		</div>
	</div>
</div>
<div class="modal fade" title="Enter Comunication Email " id="editMailModal" role="dialog">
	<div class="modal-dialog">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>Enter new email</h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">X</button>
			</div>
			<div class="modal-body">
				<div class="row">
					<label class="col-sm-2 control-label"> </label>
					<form id="updateEmailForm">
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
						<div class="col-sm-6 col-md-6">
							<input type="text" style="margin-top: 0;" class="form-control mar-b10" id="newEmail" data-validation="required email">
						</div>
						<div class="col-sm-4 col-md-4">
							<button id="submitEmail" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out marg-left-20" data-dismiss="modal"><spring:message code="application.submit" /></button>
						</div>
					</form>
				</div>
			</div>
		</div>
	</div>
</div>
<div class="modal fade" title="Enter Public Context Path " id="editPublicContextPathModal" role="dialog">
	<div class="modal-dialog">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>Enter new public context path</h3>
				<button class="close for-absulate" type="button" id="closePublcContextPath" data-dismiss="modal">X</button>
			</div>
			<div class="modal-body">
				<div class="row">
					<label class="col-sm-2 control-label"> </label>
					<form id="updatePublicContextPathForm">
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
						<div class="col-sm-6 col-md-6">
							<input type="text" style="margin-top: 0;" class="form-control mar-b10" id="newPublicContextPath" data-validation="required length" data-validation-length="max100"  data-validation-error-msg-length="Input value must be between 1 and 100." >
						</div>
						<div class="col-sm-4 col-md-4">
							<button id="submitPublicContextPath" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out marg-left-20" data-dismiss="modal">Submit</button>
						</div>
					</form>
				</div>
			</div>
		</div>
	</div>
</div>
<!-- change subscription pop up -->
<div class="modal fade" id="confirmChangeSubscription" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3><spring:message code="application.update" /></h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">&times;</button>
			</div>
			<c:url var="updateSubscription" value="/owner/updateManualSubscription" />
			<form action="${updateSubscription}" method="post" id="updateSubscriptionForm">
				<input type="hidden" name="buyerId" value="${buyerObj.id}"> <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
				<div class="col-md-12">
					<div class="row">
						<div class="modal-body col-md-4">
							<label> <spring:message code="application.startdate"/> </label>
						</div>
						<div class="col-md-8 marg-top-10">
							<fmt:formatDate var="startDate" value="${buyerObj.currentSubscription.startDate}" pattern="dd/MM/yyyy" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" />
							<div class="input-prepend input-group">
								<input name="startDate" id="startDate" type="hidden" value="${startDate}"> ${startDate}
								<%-- <input name="startDate" id="startDate" type="text" data-validation="required date" data-validation-format="dd/mm/yyyy" class="bootstrap-datepicker form-control for-clander-view nvclick" value="${startDate}"> --%>
							</div>
						</div>
					</div>

					<div class="row">
						<div class="modal-body col-md-4">
							<label> <spring:message code="rfaevent.end.date"/> </label>
						</div>
						<div class="col-md-8 marg-top-10">
							<fmt:formatDate var="endDate" value="${buyerObj.currentSubscription.endDate}" pattern="dd/MM/yyyy" />
							<div class="input-prepend input-group">
								<input name="endDate" id="endDate" type="text" data-validation="required date" data-validation-format="dd/mm/yyyy" class="bootstrap-datepicker form-control for-clander-view" value="${endDate}">
							</div>
						</div>
					</div>
					<div class="row">
						<div class="modal-body col-md-4">
							<label> <spring:message code="buyerdetails.no.of.users" /> </label>
						</div>
						<div class="col-md-8 marg-top-10">
							<input type="text" name="userLimit" id="userLimit" data-validation="required number length" data-validation-length="1-5" class="form-control" value="${buyerObj.currentSubscription.userQuantity}">
						</div>
					</div>
					<div class="row">
						<div class="modal-body col-md-4">
							<label> <spring:message code="buyerdetails.no.of.events" /> </label>
						</div>
						<div class="col-md-8 marg-top-10">
							<input type="text" name="eventLimit" id="eventLimit" data-validation="required number length" data-validation-length="1-7" maxlength="7" class="form-control" value="${buyerObj.currentSubscription.eventQuantity}">
						</div>
					</div>

				</div>
				<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
					<input type="submit" class="btn btn-info ph_btn_medium hvr-pop hvr-rectangle-out pull-left" id="updateSubscriptionbtn" value="Update">
					<button type="reset" value="Reset" class="btn btn-black btn-default hvr-pop hvr-rectangle-out1 pull-right" data-dismiss="modal" onclick="clearField();"><spring:message code="application.cancel" /></button>
				</div>
			</form>
		</div>
	</div>
</div>
<script type="text/javascript">
	$('document').ready(
			function() {
				
				$('#confirmChangeSubscription').on('hidden.bs.modal', function () {
				    $(this).find('form').trigger('reset');
				});

				$("#opensupDetail").click(function() {
					$("#suppDetailModal").modal({ backdrop : 'static', keyboard : false });
				});
				$("#openActDetail").click(function() {
					$("#buyerDetailModal").modal({ backdrop : 'static', keyboard : false });
				});
				$("#edit_mail").click(function() {
					if ($("#editEmailValue").val() != '') {
						$("#newEmail").val($("#editEmailValue").val());
					} else {
						$("#newEmail").val($(this).data("mail"));
					}
					$("#editMailModal").modal({ backdrop : 'static', keyboard : false });
				});
				
				$("#edit_PublicContextPath").click(function() {
					if ($("#editPublicContextPathValue").val() != '') {
						$("#newPublicContextPath").val($("#editPublicContextPathValue").val());
					} else {
						$("#newPublicContextPath").val($("#edit_PublicContextPath").data("path"));
					}
					$("#editPublicContextPathModal").modal({ backdrop : 'static', keyboard : false });
				});
				
				$("#changeSubscription").click(function() {
					$("#confirmChangeSubscription").modal({ backdrop : 'static', keyboard : false });
				});

				$("#submitRemarks").click(function() {

					var header = $("meta[name='_csrf_header']").attr("content");
					var token = $("meta[name='_csrf']").attr("content");

					var remarks = $("#remarks").val()
					var buyerId = $('#buyerId').val();
					$.ajax({ url : getOwnerContextPath() + "/suspendBuyer/" + buyerId + "/" + remarks, async : false, type : "POST", beforeSend : function(xhr) {
						$('#loading').show();
						xhr.setRequestHeader(header, token);
					}, success : function(data, textStatus, request) {
						var success = request.getResponseHeader('success');
						showMessage('SUCCESS', success);
						window.location.href = data;
						$('#loading').hide();
					}, error : function(request, textStatus, errorThrown) {
						$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
						$('div[id=idGlobalError]').show();
						showMessage('ERROR', request.getResponseHeader('error'));
						$('#loading').hide();
					}, complete : function() {
						$('#loading').hide();
					} });
				});

				$("#submitEmail").click(function(e) {
					e.preventDefault();

					if (!$('#updateEmailForm').isValid()) {
						return false;
					}
					var header = $("meta[name='_csrf_header']").attr("content");
					var token = $("meta[name='_csrf']").attr("content");
					var email = $("#newEmail").val()
					var buyerId = $('#buyerId').val();
					$.ajax({ url : getOwnerContextPath() + "/updateComunicationEmail/" + buyerId + "/" + email, async : false, type : "POST", beforeSend : function(xhr) {
						$('#loading').show();
						xhr.setRequestHeader(header, token);
					}, success : function(data, textStatus, request) {
						$(".bEmail").html(email);
						$("#editEmailValue").val(email);
						$("#newEmail").val(email);
						var success = request.getResponseHeader('success');
						showMessage('SUCCESS', success);
						$('#loading').hide();
					}, error : function(request, textStatus, errorThrown) {
						$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
						$('div[id=idGlobalError]').show();
						showMessage('ERROR', request.getResponseHeader('error'));
						$('#loading').hide();
					}, complete : function() {
						$('#loading').hide();
					} });
				});
				
				$("#submitPublicContextPath").click(function(e){
					e.preventDefault();
					if(!$('#updatePublicContextPathForm').isValid()){
						return false;
					}
					var header = $("meta[name='_csrf_header']").attr("content");
					var token = $("meta[name='_csrf']").attr("content");
					var publicContextPath = $("#newPublicContextPath").val();
					var buyerId = $('#buyerId').val();
					$.ajax({ url : getOwnerContextPath() + "/updatePublicContextPath/" + buyerId + "/" + publicContextPath, async : false, type : "POST", beforeSend : function(xhr) {
						$('#loading').show();
						xhr.setRequestHeader(header, token);
					}, success : function(data, textStatus, request) {
						$('div[id=idGlobalError]').hide();
						$(".bPublicContextPath").html(publicContextPath);
						$("#editPublicContextPathValue").val(publicContextPath);
						$("#newPublicContextPath").val(publicContextPath);
						var success = request.getResponseHeader('success');
						showMessage('SUCCESS', success);
						$('#loading').hide();
					}, error : function(request, textStatus, errorThrown) {
						$('p[id=idGlobalErrorMessage]').html(request.getResponseHeader('error'));
						$('div[id=idGlobalError]').show();
						showMessage('ERROR', request.getResponseHeader('error'));
						$('#loading').hide();
					}, complete : function() {
						$('#loading').hide();
					} });
				});

				var table = $('#idTblPaymentHistory').DataTable(
						{
							"processing" : true,
							"deferRender" : true,
							"lengthChange" : false,
							"preDrawCallback" : function(settings) {
// 								$('div[id=idGlobalError]').hide();
								$('#loading').show();
								return true;
							},
							"drawCallback" : function() {
								// in case your overlay needs to be put away automatically you can put it here
								$('#loading').hide();
							},
							"serverSide" : true,
							"pageLength" : 10,
							"searching" : true,
							"ajax" : { "url" : getContextPath() + "/owner/buyerPaymentTransactionData/${buyerObj.id}", "error" : function(request, textStatus, errorThrown) {
								var error = request.getResponseHeader('error');
								if (error != undefined) {
									showMessage('ERROR', error.split(",").join("<br/>"));
									$('p[id=idGlobalErrorMessage]').html(error != null ? error.split(",").join("<br/>") : "");
									$('div[id=idGlobalError]').show();
								}
								$('#loading').hide();
							} },
							"order" : [],
							"columns" : [ /* {
								"data" : "id",
								"searchable" : false,
								"orderable" : false,
								"render" : function(data, type, row) {
								var ret = '<a href="viewBuyerPaymentTransaction/id=' + row.id + '"><img src="${pageContext.request.contextPath}/resources/images/edit1.png"></a>';
								return ret;
							}
							}, */{ 
							"data" : "buyerPlan.planName",
							"defaultContent" : "" 
							},{ 
								"data" : "referenceTransactionId", 
								"defaultContent" : "" 
							},{
								"data" : "createdDate",
								"searchable" : false, 
								"defaultContent" : "" 
							},{ 
							"data" : "totalPriceAmount",
							"defaultContent" : ""
							},{ 
									"data" : "status",
									"defaultContent" : ""
								}] 
						});
				var htmlSearch = '<tr class="tableHeaderWithSearch">';
				$('#idTblPaymentHistory thead tr:nth-child(1) th').each(function(i) {
					var title = $(this).text();
					if (!(title == "Actions") && $(this).attr('search-type') != '') {
						if ($(this).attr('search-type') == 'select') {
							var optionsType = $(this).attr('search-options');
							htmlSearch += '<th><select data-index="'+i+'"><option value="">Search ' + title + '</option>';
							if (optionsType == 'paymentStatusTypeList') {
								<c:forEach items="${paymentStatusTypeList}" var="item">
								htmlSearch += '<option value="${item}">${item}</option>';
								</c:forEach>
							}
							htmlSearch += '</select></th>';
						} else {
							htmlSearch += '<th><input type="text" placeholder="Search '+title+'" data-index="'+i+'" /></th>';
						}
					} else {
						htmlSearch += '<th>&nbsp;</th>';
					}
				});
				htmlSearch += '</tr>';
				$('#idTblPaymentHistory thead').append(htmlSearch);
				$(table.table().container()).on('keyup', 'thead input', function() {
					if ($.trim(this.value).length > 2 || this.value.length == 0) {
						table.column($(this).data('index')).search(this.value).draw();
					}
				});
				$(table.table().container()).on('change', 'thead select', function() {
					table.column($(this).data('index')).search(this.value).draw();
				});
			});
	$('#closePublcContextPath').click(function(e) {
		$('#newPublicContextPath').parent().removeClass('has-error').find('.form-error').remove();
	});
</script>
<script type="text/javascript">
	$("#test-select").treeMultiselect({ enableSelectAll : true, sortable : true });
</script>
<script type="text/javascript">
	$('document').ready(function() {
		var data = eval('${noteList}');

		$('#buyerNoteDetails').DataTable({ 'aaData' : data, "aoColumns" : [ { "mData" : "createdDate" }, { "mData" : "description" }, { "mData" : "incidentType" } ] });
	});
</script>
<script>
	$('document').ready(function() {
		$('.openAddNots > button').click(function() {
			$('.notesAddBlock').toggle();
		});
	});
</script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.js"/>"></script>
<script>
	$.validate({ lang : 'en', validateOnBlur : false });
</script>
<script type="text/javascript" src="<c:url value="/resources/js/view/buyer.js?1"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.css"/>">
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/moment.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/daterangepicker/daterangepicker-demo.js?1"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/datepicker/datepicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/js-core/jquery.form-validator.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/assets/widgets/timepicker/timepicker.js"/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/widgets/timepicker/timepicker.css"/>">
<script>
	$('#metric-start').bsdatepicker({ format : 'dd/mm/yyyy',

	}).on('changeDate', function(ev) {
		$('#metric-end').prop("disabled", false);
		$('#metric-end').bsdatepicker({ format : 'dd/mm/yyyy', minDate : ev.date, onRender : function(date) {
			return date.valueOf() < ev.date.valueOf() ? 'disabled' : '';
		}

		});

	});

	/* Datepicker bootstrap */

	$(function() {
		"use strict";

		$('#startDate').bsdatepicker({ format : 'dd/mm/yyyy', onRender : function(date) {
			if (date.valueOf() <= $.now()) {
				return 'disabled';
			}
		}

		}).on('changeDate', function(e) {
			$(this).blur();
			$(this).bsdatepicker('hide');
		});

		$('#endDate').bsdatepicker({ format : 'dd/mm/yyyy', onRender : function(date) {
			if (date.valueOf() <= $.now()) {
				return 'disabled';
			}
		}

		}).on('changeDate', function(e) {
			$(this).blur();
			$(this).bsdatepicker('hide');
		});
	});
</script>



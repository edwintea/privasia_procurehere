<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<spring:message code="supplier.onboarding.only" var="display" />
<sec:authorize access="hasRole('SUPPLIER')" var="sup" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<sec:authentication property="principal.profilePicture" var="profilePic" />
<sec:authentication property="principal.isBuyerErpEnable" var="isBuyerErpEnable" />
<sec:authorize access="hasRole('ROLE_BUDGET_PLANNER_EDIT')" var="canEditBudget" />
<sec:authorize access="hasRole('ROLE_BUDGET_PLANNER_VIEW_ONLY')" var="canViewBudget" />

<div id="sb-site">
	<!-- <div id="page-wrapper"> -->
	<!-- PAGE HEADER BLOCK -->
	<!-- PAGE HEADER BLOCK ENDS-->
	<div id="page-sidebar">
		<div class="scroll-sidebar">
			<div class="user_box">
				<div class="user-pic">
					<img id="profileImageHolder" class="profile_picture" src="${pageContext.request.contextPath}/admin/getUserProfile" alt="Looking amazing there :)" />
				</div>
				<div class="clearfix">&nbsp;</div>
				<div class="clearfix">${sessionScope["timeZone"]}</div>
				<div class="user_detail_box">
					<span class="user_Name"> <sec:authentication property="principal.username" />
					</span>
					<h4 class="user_mail">
						<sec:authentication property="principal.name" />
					</h4>
					<div data-intro="You can update your profile here" data-position="right">
						<a href="${pageContext.request.contextPath}/profileSetting" class="user_edit_icon" data-placement="top" title="<spring:message code="application.edit"/>"> <i aria-hidden="true" class="glyph-icon icon-edit"></i>
						</a>
					</div>
				</div>
				<div class="ip_detail_box">
					<span> <spring:message code="defaultmenu.last.login" /> : <sec:authentication var="lastLoginTime" property="principal.lastLoginTime" /> <c:if test="${!empty lastLoginTime}">
							<fmt:formatDate pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" value="${lastLoginTime}" />
						</c:if>
					</span> <span> <spring:message code="defaultmenu.last.failed.login" /> : <sec:authentication var="lastFailedLoginTime" property="principal.lastFailedLoginTime" /> <c:if test="${!empty lastFailedLoginTime}">
							<fmt:formatDate pattern="dd/MM/yyyy hh:mm a" timeZone="<%=request.getSession().getAttribute(\"timeZone\")%>" value="${lastFailedLoginTime}" />
						</c:if> <c:if test="${empty lastFailedLoginTime}">
							<spring:message code="defaultmenu.na" />
						</c:if>
					</span>
				</div>
			</div>
			<div ${display == 'true'  and sup ? 'style="display:none"' : ''}>
				<ul id="sidebar-menu">

					<!--  PH 182 is alredy in production just uncomment this to release CR  -->
					<sec:authorize access="hasRole('BUYER') or hasRole('SUPPLIER')">
						<li class="no-menu" title="<spring:message code="defaultmenu.tutorial"/>">
							<div class="row">
								<div class="col-md-8 p-0 col-sm-8 setcheckbox ">
									<span class="show_tutorial"><spring:message code="defaultmenu.show.tutorials" /></span>
								</div>
								<div class="col-md-4 col-sm-4" style="padding-left: 0px;">
									<div class="switchToggle">
										<input type="checkbox" ${!empty sessionScope["showWizardTutorial"] && sessionScope["showWizardTutorial"] ? "checked='checked'" : "" } id="idShowTutorial" data-toggle="modal" data-target="#showtutorialModel1"> <label for="idShowTutorial" style="margin-left: 20px; text-align: center;"><spring:message code="defaultmenu.tutorial.toggle" /></label>
									</div>
								</div>
							</div>
						</li>

					</sec:authorize>

					<div class="row">
						<div class="col-md-8 p-0 col-sm-8 setcheckbox ">
							<span class="show_tutorial"><spring:message code="dashboard.emailNotifications" /></span>
						</div>
						<div class="col-md-4 col-sm-4" style="padding-left: 0px;">
							<div class="switchToggle">
								<input type="checkbox" ${!empty sessionScope["unsubscribeEmailNotifications"] && sessionScope["unsubscribeEmailNotifications"] ? "checked='checked'" : "" } id="idUnsubcribeEmailNotifications" data-toggle="modal" data-target="#unsubcribeEmailNotificationsModel"> <label for="idUnsubcribeEmailNotifications" style="margin-left: 20px; text-align: center;"><spring:message code="defaultmenu.tutorial.toggle" /></label>
							</div>
						</div>
					</div>

					<li class="divider"></li>
					<sec:authorize access="hasRole('OWNER')">
						<li class="no-menu" title="<spring:message code="application.dashboard"/>"><a href="${pageContext.request.contextPath}/owner/ownerDashboard"> <i class="glyphicon glyphicon-blackboard"></i> <span><spring:message code="application.dashboard" /></span>
						</a></li>
						<li class="divider"></li>
					</sec:authorize>
					<sec:authorize access="hasRole('BUYER')">
						<li class="no-menu" title="<spring:message code="application.dashboard"/>"><a href="${pageContext.request.contextPath}/buyer/buyerDashboard"> <i class="glyphicon glyphicon-blackboard"></i> <span><spring:message code="application.dashboard" /></span>
						</a></li>
						<li class="divider"></li>
					</sec:authorize>
					<c:if test="${isBuyerErpEnable}">
						<sec:authorize access="hasRole('BUYER') and hasRole('ADMIN')">

							<li class="no-menu"><a href="#" title="<spring:message code="buyerdetails.erp.integration"/>"> 
								<i class="glyphicon glyphicon-transfer"></i> <span><spring:message code="buyerdetails.erp.integration" /></span>
							</a>
								<div class="sidebar-submenu">
									<ul>
										<li class="no-menu"><a href="${pageContext.request.contextPath}/buyer/erpSetup" title="<spring:message code="defaultmenu.erp.configuration"/>"> <span><spring:message code="defaultmenu.erp.configuration" /></span>
										</a></li>
										<li class="divider"></li>
										<li class="no-menu"><a href="${pageContext.request.contextPath}/buyer/erpManualList" title="<spring:message code="erp.event.list"/>"> <span><spring:message code="erp.event.list" /> </span>
										</a></li>
									</ul>
								</div></li>
							<li class="divider"></li>
						</sec:authorize>
					</c:if>
					<sec:authorize access="hasRole('BUYER') and hasRole('ROLE_SPEND_ANALYTICS')">
						<li class="no-menu" title="<spring:message code="application.spent.analysis"/>">
						<a href="${pageContext.request.contextPath}/buyer/spentAnalysis"> 
							<i class="glyphicon glyphicon-stats"></i> <span><spring:message code="application.spent.analysis" /></span>
						</a></li>
						<li class="divider"></li>
					</sec:authorize>

					<sec:authorize access="hasRole('ROLE_COMPANY_DETAILS') and hasRole('BUYER') and hasRole('ADMIN')">
						<li class="no-menu" title="<spring:message code="buyer.profile"/>"><a href="${pageContext.request.contextPath}/buyer/buyerProfileForm"> <i class="fa fa-black-tie"></i> <span><spring:message code="buyer.profile.name" /></span>
						</a></li>
						<li class="divider"></li>
					</sec:authorize>

					<sec:authorize access="(hasRole('ROLE_VIEW_REPORTS') and (hasRole('ROLE_VIEW_TAT_REPORT') or hasRole('ROLE_VIEW_EVENT_REPORTS') or hasRole('ROLE_VIEW_AUCTION_SUMMARY_REPORTS') or hasRole('ROLE_VIEW_RFX_ANALYTICS'))) ">
						<li class="no-menu">
							<a href="#" title="<spring:message code="defaultmenu.report.menu"/>">
								<i class="glyphicon glyphicon-dashboard"></i> <span><spring:message code="defaultmenu.report.rfxAnalytics.menu" /></span> </a>
							<div class="sidebar-submenu">
								<ul>
									<sec:authorize access="hasRole('ROLE_VIEW_RFX_ANALYTICS')">
										<li class="no-menu" view-name="rfxAnalytics" title="<spring:message code="defaultmenu.rfx.analytics.menu"/>"><a href="${pageContext.request.contextPath}/rfxAnalytics/rfxAnalytics"> <!--  <i></i>  --> <span><spring:message code="defaultmenu.rfx.analytics.menu" /></span>
										</a></li>
										<li class="divider"></li>
									</sec:authorize>
									<sec:authorize access="hasRole('ROLE_VIEW_TAT_REPORT')">
										<li class="no-menu" view-name="tatReport" title="<spring:message code="defaultmenu.tat.report.menu"/>"><a href="${pageContext.request.contextPath}/tatReport/tatReport"> <!--  <i></i>  --> <span><spring:message code="defaultmenu.tat.report.menu" /></span>
										</a></li>
										<li class="divider"></li>
									</sec:authorize>
									<sec:authorize access="hasRole('ROLE_VIEW_EVENT_REPORTS')">
										<li class="no-menu" view-name="eventReport" title="<spring:message code="defaultmenu.event.report.menu"/>"><a href="${pageContext.request.contextPath}/buyer/eventReport"> <!--  <i></i>  --> <span><spring:message code="defaultmenu.event.report.menu" /></span>
										</a></li>
										<li class="divider"></li>
									</sec:authorize>
									<sec:authorize access="hasRole('ROLE_VIEW_AUCTION_SUMMARY_REPORTS')">
										<li class="no-menu" view-name="auctionReport" title="<spring:message code="defaultmenu.auction.summary.report.menu"/>"><a href="${pageContext.request.contextPath}/buyer/auctionReport"> <!-- <i class="glyphicon glyphicon-book"></i> --> <span><spring:message code="defaultmenu.auction.summary.report.menu" /></span>
										</a></li>
										<li class="divider"></li>
									</sec:authorize>
								</ul>
							</div></li>
						<li class="divider"></li>
					</sec:authorize>


					<sec:authorize access="(hasRole('BUYER') and (hasRole('ROLE_REQUEST_RFS')))">
						<li class="no-menu" view-name="sourcingForm" title="<spring:message code="defaultmenu.sourcing.formlist"/>"><a href="${pageContext.request.contextPath}/buyer/sourcingForm"> <i class="glyphicon glyphicon-list-alt"></i> <span><span><spring:message code="defaultmenu.sourcing.reqlist" /></span></a></li>
						<li class="divider"></li>
					</sec:authorize>

					<sec:authorize access="hasRole('BUYER') and (hasRole('ROLE_VIEW_ANNOUNCEMENT') or hasRole('ADMIN') or ${buyerReadOnlyAdmin})">
						<li class="no-menu" title="Announcement" view-name="announcementList"><a href="${pageContext.request.contextPath}/buyer/announcementList"><i class="glyphicon glyphicon-bullhorn"></i> <span>Announcement</span> </a></li>
						<li class="divider"></li>
					</sec:authorize>

					<sec:authorize access="(hasRole('BUYER_FAV_SUPPLIER_LIST') or hasRole('ROLE_SUPPLIER_FORMS')) or (hasRole('BUYER') and (hasRole('ADMIN') or ${buyerReadOnlyAdmin} or hasRole('ROLE_SUPPLIER_PERFORMANCE_LIST') ))">
						<li class="no-menu"><a href="#" title="<spring:message code="application.supplier"/>"> <i class="fa fa-users"></i> <span><spring:message code="application.supplier" /></span>
						</a>
							<div class="sidebar-submenu">
								<ul>
									<sec:authorize access="hasRole('BUYER_FAV_SUPPLIER_LIST') or (hasRole('BUYER') and (hasRole('ADMIN') or ${buyerReadOnlyAdmin}))">
										<li><a href="${pageContext.request.contextPath}/buyer/importSupplier" title="<spring:message code="defultMenu.suppliers.list"/>"><spring:message code="defultMenu.suppliers.list" /></a></li>
									</sec:authorize>
									<sec:authorize access="hasRole('ROLE_SUPPLIER_FORMS') or (hasRole('BUYER') and (hasRole('ADMIN') or ${buyerReadOnlyAdmin}))">
										<li><a href="${pageContext.request.contextPath}/buyer/supplierFormList" title="<spring:message code="defultMenu.suppliers.forms"/>"><spring:message code="defultMenu.suppliers.forms" /></a></li>
									</sec:authorize>
									<sec:authorize access="hasRole('ROLE_SUPPLIER_PERFORMANCE_LIST') or (hasRole('BUYER') and (hasRole('ADMIN') or ${buyerReadOnlyAdmin}))">
										<li view-name="supplierPerformance" title="<spring:message code="defultMenu.suppliers.performance"/>"><a href="${pageContext.request.contextPath}/buyer/supplierPerformanceList" title="<spring:message code="defultMenu.suppliers.performance"/>"><spring:message code="defultMenu.suppliers.performance" /></a></li>
									</sec:authorize>
									<sec:authorize access="hasRole('ROLE_SP_ANALYTICS') or (hasRole('BUYER') and (hasRole('ADMIN') or ${buyerReadOnlyAdmin}))">
										<li view-name="supplierPerformanceAnalytics" title="<spring:message code="defultMenu.suppliers.performance.analytics"/>"><a href="${pageContext.request.contextPath}/spAnalytics/supplierPerformanceAnalytics" title="<spring:message code="defultMenu.suppliers.performance.analytics"/>"><spring:message code="defultMenu.suppliers.performance.analytics" /></a></li>
									</sec:authorize>
								</ul>
							</div></li>
						<li class="divider"></li>
					</sec:authorize>

					<sec:authorize access="hasRole('BUYER') and (hasRole('EVENT_TEMPLATE_LIST') or hasRole('PR_TEMPLATE_LIST') or hasRole('ADMIN') or ${buyerReadOnlyAdmin}) or hasRole('ROLE_SP_TEMPLATE_LIST')">
						<li class="no-menu"><a href="#" title="<spring:message code="defaultmenu.templates.menu"/>"> <i class="glyphicon glyphicon-paste"></i> <span><spring:message code="defaultmenu.templates.menu" /></span>
						</a>
							<div class="sidebar-submenu">
								<ul>
									<sec:authorize access="hasRole('EVENT_TEMPLATE_LIST') or hasRole('ADMIN') or ${buyerReadOnlyAdmin}">
										<li view-name="rfxTemplate" title="<spring:message code="rfxTemplate.title"/>"><a href="${pageContext.request.contextPath}/buyer/rfxTemplate/rfxTemplateList" z> <!-- <i class="glyphicon glyphicon-blackboard"></i> --> <span><spring:message code="rfxTemplate.title" /></span>
										</a></li>
										<li class="divider"></li>
									</sec:authorize>
									<!-- For event based subscription PR/PO is not allowed. Ref email from Soalen Dt: Sunday 09 July 2017 04:08 PM -->
									<%-- <sec:authorize access="hasRole('PR_TEMPLATE_LIST') or hasRole('ADMIN') or ${buyerReadOnlyAdmin}"> --%>
									<sec:authorize access="hasRole('PR_TEMPLATE_LIST') and hasRole('BUYER')">
										<li view-name="prTemplate" title="<spring:message code="defaultmenu.pr.template"/>"><a href="${pageContext.request.contextPath}/buyer/prTemplateList"> <!-- <i class="glyphicon glyphicon-blackboard"></i> --> <span><spring:message code="defaultmenu.pr.template" /></span>
										</a></li>
									</sec:authorize>
									<sec:authorize access="hasRole('REQUEST_TEMPLATE_LIST') and hasRole('BUYER')">
										<li view-name="requestTemplate" title="<spring:message code="defaultmenu.sourcing.template"/>"><a href="${pageContext.request.contextPath}/buyer/sourceTemplateList"> <span><spring:message code="defaultmenu.sourcing.template" /></span>
										</a></li>
									</sec:authorize>
									<sec:authorize access="hasRole('ROLE_SP_TEMPLATE_LIST') and hasRole('BUYER')">
										<li view-name="spTemplate" title="<spring:message code="defaultmenu.sp.template"/>"><a href="${pageContext.request.contextPath}/buyer/spTemplateList"> <span><spring:message code="defaultmenu.sp.template" /></span>
										</a></li>
									</sec:authorize>
								</ul>
							</div></li>
						<li class="divider"></li>
					</sec:authorize>
					<!-- PH-4105 1.1.2-->
					<sec:authorize access="hasRole('VIEW_PR_DRAFT') or hasRole('VIEW_PO_LIST') or  hasRole('ROLE_VIEW_DO_LIST') or hasRole('ROLE_GRN_LIST') or hasRole('ROLE_VIEW_INVOICE_LIST') or hasRole('ROLE_ACCOUNT_PAYABLE') or hasRole('ROLE_PAYMENT_RECORD')  or hasRole('BUYER') and (hasRole('PROC_TO_PAY')  or hasRole('ADMIN') or ${buyerReadOnlyAdmin}) or hasRole('PROC_TO_PAY')">
						<li class="no-menu"><a href="#" title="<spring:message code="defaultmenu.proc.to.pay"/>"> <i class="fa fa-book"></i> <span><spring:message code="defaultmenu.proc.to.pay" /></span>
						</a>
							<div class="sidebar-submenu">
								<ul>
									<sec:authorize access="hasRole('VIEW_PR_DRAFT') or (hasRole('BUYER') and (hasRole('ADMIN') or ${buyerReadOnlyAdmin}))">
										<li view-name="prList" title="<spring:message code="defaultmenu.p2p.pr"/>"><a href="${pageContext.request.contextPath}/buyer/prReportList"><span><spring:message code="defaultmenu.p2p.pr" /></span></a></li>
									</sec:authorize>
									<sec:authorize access="hasRole('VIEW_PO_LIST') or ${buyerReadOnlyAdmin} or  (hasRole('BUYER') and hasRole('ADMIN'))">
										<li view-name="poList" title="<spring:message code="defaultmenu.po"/>"><a href="${pageContext.request.contextPath}/buyer/poList"><span><spring:message code="defaultmenu.po" /></span></a></li>
									</sec:authorize>
									<sec:authorize access="hasRole('ROLE_VIEW_DO_LIST') or hasRole('ROLE_ADMIN_READONLY') or   hasRole('ADMIN')">
										<li view-name="doList" title="<spring:message code="defaultmenu.p2p.do"/>"><a href="${pageContext.request.contextPath}/buyer/deliveryOrderList"><span><spring:message code="defaultmenu.p2p.do" /></span></a></li>
									</sec:authorize>
									<sec:authorize access="hasRole('ROLE_GRN_LIST') and hasRole('BUYER') or   hasRole('ADMIN')">
										<li view-name="grnList" title="<spring:message code="defaultmenu.p2p.goods"/>"><a href="${pageContext.request.contextPath}/buyer/grnList"> <span><spring:message code="defaultmenu.p2p.goods" /></span>
										</a></li>
									</sec:authorize>
									<sec:authorize access="hasRole('ROLE_VIEW_INVOICE_LIST') and hasRole('BUYER') or  hasRole('ADMIN')">
                                        <!-- Parent List Item (Invoice) -->
                                        <li class="has-submenu" title="<spring:message code='defaultmenu.invoice'/>">
                                            <a href="${pageContext.request.contextPath}/buyer/invoiceList">
                                                <span><spring:message code="defaultmenu.invoice" /></span>
                                            </a>

                                            <!-- Sub-list for Credit Note & Debit Note -->
                                            <ul class="submenu">
                                                <li title="<spring:message code='defaultmenu.p2p.cn.dn'/>">
                                                     <a href="">
                                                        <span><spring:message code="defaultmenu.p2p.cn.dn" /></span>
                                                    </a>
                                                </li>
                                            </ul>
                                        </li>

									</sec:authorize>
									<sec:authorize access="hasRole('ROLE_VIEW_INVOICE_LIST') or hasRole('ROLE_ADMIN_READONLY') or   hasRole('ADMIN')">
										<li view-name="invoiceFrList" title="<spring:message code="defaultmenu.invoicefinancerequest"/>"><a href="${pageContext.request.contextPath}/buyer/invoiceFinanceRequest/requestList"> <span><spring:message code="defaultmenu.invoicefinancerequest" /></span>
										</a></li>
									</sec:authorize>
									<sec:authorize access="hasRole('ROLE_ACCOUNT_PAYABLE') and hasRole('BUYER') or   hasRole('ADMIN')">
										<li view-name="accountPayableList" title="<spring:message code="defaultmenu.p2p.ap"/>"><a href=""> <span><spring:message code="defaultmenu.p2p.ap" /></span>
										</a></li>
									</sec:authorize>
									<sec:authorize access="hasRole('ROLE_PAYMENT_RECORD') and hasRole('BUYER') or   hasRole('ADMIN')">
										<li view-name="paymentRecList" title="<spring:message code="defaultmenu.p2p.pymt.rec"/>"><a href=""> <span><spring:message code="defaultmenu.p2p.pymt.rec" /></span>
										</a></li>
									</sec:authorize>
								</ul>
							</div></li>
						<li class="divider"></li>
					</sec:authorize>

					<!-- END OF BUYER SIDEBAR -->
					<!-- For event based subscription PR/PO is not allowed. Ref email from Soalen Dt: Sunday 09 July 2017 04:08 PM -->
					<!-- TODO for buyer Read Only Admin-->
					<%-- <sec:authorize access="hasRole('PR_PO') or (hasRole('BUYER') and (hasRole('ADMIN') or ${buyerReadOnlyAdmin}))">
					<sec:authorize access="hasRole('PR_PO')   and hasRole('BUYER')">
						<li class="no-menu"><a href="#" title="<spring:message code="defaultmenu.pr.po"/>"> <i class="fa fa-book"></i> <span><spring:message code="defaultmenu.pr.po" /></span>
						</a>
							<div class="sidebar-submenu">
								<ul>
									<sec:authorize access="hasRole('VIEW_PR_DRAFT') or (hasRole('BUYER') and (hasRole('ADMIN') or ${buyerReadOnlyAdmin}))">
										<li view-name="prDraftList" title="<spring:message code="defaultmenu.pr.draft"/>"><a href="${pageContext.request.contextPath}/buyer/prDraftList"><spring:message code="defaultmenu.pr.draft" /></a></li>
									</sec:authorize>

									<sec:authorize access="hasRole('VIEW_PO_LIST') or ${buyerReadOnlyAdmin} or  (hasRole('BUYER') and hasRole('ADMIN'))">
										<li view-name="prReportList" title="<spring:message code="defaultmenu.pr.list"/>"><a href="${pageContext.request.contextPath}/buyer/prReportList"><span><spring:message code="defaultmenu.pr.report" /></span></a></li>
									</sec:authorize>

									<sec:authorize access="hasRole('VIEW_PO_LIST') or ${buyerReadOnlyAdmin} or  (hasRole('BUYER') and hasRole('ADMIN'))">
										<li view-name="poList" title="<spring:message code="defaultmenu.po"/>"><a href="${pageContext.request.contextPath}/buyer/poList"><span><spring:message code="defaultmenu.po" /></span></a></li>
									</sec:authorize>


								</ul>
							</div></li>
						<li class="divider"></li>
					</sec:authorize>
					<sec:authorize access="(hasRole('ROLE_DO_INVOICE') or hasRole('ROLE_GRN_LIST')) and hasRole('BUYER')">
						<li class="no-menu"><a href="#" title="<spring:message code="defaultmenu.do.grn.invoice"/>"> <i class="fa fa-book"></i> <span><spring:message code="defaultmenu.do.grn.invoice" /> </span>
						</a>
							<div class="sidebar-submenu">
								<ul>
									<sec:authorize access="hasRole('ROLE_GRN_LIST') or ${buyerReadOnlyAdmin} or  (hasRole('BUYER') and hasRole('ADMIN'))">
										<li view-name="grnList" title="<spring:message code="header.goods.receipt.note"/>"><a href="${pageContext.request.contextPath}/buyer/grnList"><span><spring:message code="header.goods.receipt.note" /></span></a></li>
									</sec:authorize>

									<sec:authorize access="hasRole('ROLE_VIEW_DO_LIST') or hasRole('ROLE_ADMIN_READONLY') or   hasRole('ADMIN')">
										<li view-name="doList" title="<spring:message code="defaultmenu.do"/>"><a href="${pageContext.request.contextPath}/buyer/deliveryOrderList"><span><spring:message code="defaultmenu.do" /></span></a></li>
									</sec:authorize>
									<sec:authorize access="hasRole('ROLE_VIEW_INVOICE_LIST') or hasRole('ROLE_ADMIN_READONLY') or   hasRole('ADMIN')">
										<li view-name="invoiceList" title="<spring:message code="defaultmenu.invoice"/>"><a href="${pageContext.request.contextPath}/buyer/invoiceList"><span><spring:message code="defaultmenu.invoice" /></span></a></li>
									</sec:authorize>
									<sec:authorize access="hasRole('ROLE_VIEW_INVOICE_LIST') or hasRole('ROLE_ADMIN_READONLY') or   hasRole('ADMIN')">
										<li view-name="buyerInvoiceFinanceRequestList" title="<spring:message code="defaultmenu.invoicefinancerequest"/>"><a href="${pageContext.request.contextPath}/buyer/invoiceFinanceRequest/requestList"><span><spring:message code="defaultmenu.invoicefinancerequest" /></span></a></li>
									</sec:authorize>

								</ul>
							</div></li>
						<li class="divider"></li>
					</sec:authorize>--%>

					<sec:authorize access="hasRole('SUPPLIER')">
						<li class="no-menu" title="<spring:message code="application.dashboard"/>"><a href="${pageContext.request.contextPath}/supplier/supplierDashboard"> <i class="glyphicon glyphicon-blackboard"></i> <span><spring:message code="application.dashboard" /></span>
						</a></li>
						<li class="divider"></li>
						<sec:authorize access="hasRole('SUPPLIER') and hasRole('ADMIN')">
							<li class="no-menu" title="<spring:message code="supplier.profile"/>Profile"><a href="${pageContext.request.contextPath}/supplier/supplierProfileDetails"> <i class="fa fa-address-card-o"></i> <span><spring:message code="defaultmenu.supplier.profile" /></span>
							</a>
								<div class="incomp-icon" style="display: none;">i</div></li>
							<li class="divider"></li>
						</sec:authorize>
						
						<sec:authorize access="hasRole('SUPPLIER') and hasRole('ADMIN')">
							<li view-name="supplierPerformance" title="<spring:message code="defaultmenu.sp"/>"><a href="${pageContext.request.contextPath}/supplier/supplierPerformance"> <i class="fa fa-address-card-o"></i> <span><spring:message code="defaultmenu.sp" /></span>
							</a></li>
							<li class="divider"></li>
						</sec:authorize>
						
						<sec:authorize access="hasRole('ROLE_SUPP_BUYER_LIST') or hasRole('SUPPLIER') and hasRole('ADMIN')">
							<li class="no-menu" title="<spring:message code="defaultmenu.buyers"/>"><a href="${pageContext.request.contextPath}/supplier/associateBuyerList"> <i class="fa fa-book"></i> <span><spring:message code="defaultmenu.buyers" /></span>
							</a></li>
							<li class="divider"></li>
						</sec:authorize>
						<!--4105 1.1.3-->
					<sec:authorize access="hasRole('USER_ADMINISTRATION') or hasRole('ADMIN') or ${buyerReadOnlyAdmin} or hasRole('PROC_TO_PAY')">
						<li class="no-menu"><a href="#" title="<spring:message code="defaultmenu.proc.to.pay"/>"> <i class="fa fa-book"></i> <span><spring:message code="defaultmenu.proc.to.pay" /></span>
						</a>
							<div class="sidebar-submenu">
								<ul>
                                    <sec:authorize access="hasRole('SUPPLIER') and hasRole('ADMIN')">
                                        <li view-name="supplierPoList" title="<spring:message code="defaultmenu.po"/>"><a href="${pageContext.request.contextPath}/supplier/supplierPoList"> <span><spring:message code="defaultmenu.po" /></span>
                                        </a></li>
									</sec:authorize>
                                    <sec:authorize access="hasRole('SUPPLIER') and hasRole('ADMIN')">
                                         <li view-name="deliveryOrderList" title="<spring:message code="defaultmenu.p2p.do"/>"><a href="${pageContext.request.contextPath}/supplier/deliveryOrderList">  <span><spring:message code="defaultmenu.p2p.do" /></span>
                                         </a></li>
									</sec:authorize>
                                    <sec:authorize access="hasRole('SUPPLIER') and hasRole('ADMIN')">
                                        <li view-name="invoiceList" title="<spring:message code="defaultmenu.p2p.goods"/>"><a href="${pageContext.request.contextPath}/supplier/supplierGrnList"> <span><spring:message code="defaultmenu.p2p.goods" /></span>
                                        </a></li>
                                    </sec:authorize>
                                    <sec:authorize access="hasRole('SUPPLIER') and hasRole('ADMIN')">
                                        <li view-name="supplierinvoiceFinanceRequestList" title="<spring:message code="defaultmenu.invoicefinancerequest"/>"><a href="${pageContext.request.contextPath}/supplier/invoiceFinanceRequest/requestList"><span><spring:message code="defaultmenu.invoicefinancerequest" /></span></a></li>
                                    </sec:authorize>
                                    <sec:authorize access="hasRole('SUPPLIER') and hasRole('ADMIN')">
                                        <!-- Parent List Item (Invoice) -->
                                        <li class="has-submenu" title="<spring:message code='defaultmenu.invoice'/>">
                                            <a href="${pageContext.request.contextPath}/supplier/invoiceList">
                                                <span><spring:message code="defaultmenu.invoice" /></span>
                                            </a>

                                            <!-- Sub-list for Credit Note & Debit Note -->
                                            <ul class="submenu">
                                                <li title="<spring:message code='defaultmenu.p2p.cn.dn'/>">
                                                     <a href="">
                                                        <span><spring:message code="defaultmenu.p2p.cn.dn" /></span>
                                                    </a>
                                                </li>
                                            </ul>
                                        </li>
                                    </sec:authorize>
                                    <sec:authorize access="hasRole('SUPPLIER') and hasRole('ADMIN')">
                                        <li view-name="paymentRec" title="<spring:message code="defaultmenu.p2p.pymt.rec"/>"><a href=""> <span><spring:message code="defaultmenu.p2p.pymt.rec" /></span>
                                        </a></li>
                                    </sec:authorize>
								</ul>
							</div></li>
						<li class="divider"></li>
					</sec:authorize>



					</sec:authorize>
					<sec:authorize access="hasRole('OWNER') and hasRole('SUPPLIER_LIST')">
						<li class="no-menu" title="<spring:message code="defaultmenu.suppliers"/>"><a href="${pageContext.request.contextPath}/supplierreg/supplierSignupList"> <i class="fa fa-users" aria-hidden="true"></i> <span><spring:message code="defaultmenu.suppliers" /></span>
						</a></li>
						<li class="divider"></li>
					</sec:authorize>
					<sec:authorize access="hasRole('OWNER')  and (hasRole('ADMIN') or hasRole('BUYER_LIST'))">
						<li class="no-menu" title="<spring:message code="defaultmenu.buyers"/>"><a href="${pageContext.request.contextPath}/owner/buyerList"> <i class="fa fa-black-tie" aria-hidden="true"></i> <span><spring:message code="defaultmenu.buyers" /></span>
						</a></li>
						<li class="divider"></li>
					</sec:authorize>
					<sec:authorize access=" hasRole('OWNER')  and (hasRole('ROLE_OWNER_REPORTS') or hasRole('ADMIN'))">
						<li class="divider"></li>

						<li class="no-menu" title="<spring:message code="owner.reports.menu.label"/>"><a href="#"> <i class="glyphicon glyphicon-book"></i> <span><spring:message code="owner.reports.menu.label" /></span>
						</a>
							<div class="sidebar-submenu">
								<ul>
									<sec:authorize access="hasRole('ROLE_OWNER_BUYER_REPORT') or hasRole('ADMIN') ">
										<li class="no-menu" title="<spring:message code="owner.reports.buyer.label"/>"><a href="${pageContext.request.contextPath}/owner/buyerReportList"> <span><spring:message code="owner.reports.buyer.label" /></span>
										</a></li>
									</sec:authorize>
									<sec:authorize access="hasRole('ROLE_OWNER_SUPPLIER_REPORT') or hasRole('ADMIN')">
										<li class="no-menu" title="<spring:message code="owner.reports.supplier.label"/>"><a href="${pageContext.request.contextPath}/owner/supplierReportList"> <span><spring:message code="owner.reports.supplier.label" /></span>
										</a></li>
									</sec:authorize>
								</ul>
							</div></li>
						<li class="divider"></li>
					</sec:authorize>

					<sec:authorize access="hasRole('OWNER')  and hasRole('ADMIN')">
						<li class="divider"></li>

						<li class="no-menu" title="<spring:message code="defaultmenu.invoice.financing"/>"><a href="#"> <i class="fa fa-usd"></i> <span><spring:message code="defaultmenu.invoice.financing" /></span>
						</a>
							<div class="sidebar-submenu">
								<ul>
									<li class="no-menu" title="<spring:message code="defaultmenu.finance.company"/>"><a href="${pageContext.request.contextPath}/owner/financeCompanyList"> <!--  <i class="fa fa-university" aria-hidden="true"></i>  --> <span><spring:message code="defaultmenu.finance.company" /></span>
									</a></li>
									<li class="no-menu" title="<spring:message code="defaultmenu.finance.report"/>"><a href="${pageContext.request.contextPath}/owner/financePoList"> <!-- <i class="fa fa-black-tie" aria-hidden="true"></i> --> <span><spring:message code="defaultmenu.finance.report" /></span>
									</a></li>
								</ul>
							</div></li>
						<li class="divider"></li>
					</sec:authorize>



					<sec:authorize access="hasRole('FINANCE')">
						<li class="no-menu" title="<spring:message code="application.dashboard"/>"><a href="${pageContext.request.contextPath}/finance/financeDashboard"> <i class="glyphicon glyphicon-blackboard"></i> <span><spring:message code="application.dashboard" /></span>
						</a></li>
						<li class="divider"></li>
					</sec:authorize>

					<sec:authorize access="hasRole('FINANCE')">
						<li class="no-menu" title="<spring:message code="finance.profile"/>"><a href="${pageContext.request.contextPath}/finance/financeCompanyProfileForm"> <i class="glyphicon glyphicon-book"></i> <span><spring:message code="defaultmenu.finance.company.profile" /></span>
						</a></li>
						<li class="divider"></li>
					</sec:authorize>



					<sec:authorize access="hasRole('FINANCE') and hasRole('ROLE_FINANCE_SUPPLIER')">
						<li class="no-menu" title="<spring:message code="defaultmenu.supplier"/>"><a href="${pageContext.request.contextPath}/finance/financeSupplierList"> <i class=" fa fa-users"></i> <span><spring:message code="defaultmenu.supplier" /></span>
						</a></li>
						<li class="divider"></li>
					</sec:authorize>


					<sec:authorize access="hasRole('FINANCE') and hasRole('ROLE_FINANCE_PO')">
						<li class="divider"></li>
						<li class="no-menu"><a href="#" title="<spring:message code="defaultmenu.pr.po"/>"> <i class="fa fa-book"></i> <span><spring:message code="defaultmenu.po" /></span>
						</a>
							<div class="sidebar-submenu">
								<ul>
									<li class="no-menu" title="<spring:message code="defaultmenu.shared.po.list"/>"><a href="${pageContext.request.contextPath}/finance/financePoList"> <span><spring:message code="defaultmenu.shared.po.list" /></span></a></li>

									<li view-name="poReportList" title="<spring:message code="defaultmenu.requested.po.list"/>"><a href="${pageContext.request.contextPath}/finance/requestedPoList"><spring:message code="defaultmenu.requested.po.list" /></a></li>
								</ul>
							</div></li>


						<li class="divider"></li>
					</sec:authorize>


					<sec:authorize access="hasRole('OWNER') and hasRole('SUBSCRIPTION_DETAILS')">
						<li class="no-menu"><a href="#" title="<spring:message code="defaultmenu.subscription.plans"/>"> <i class="fa fa-rss"></i> <span><spring:message code="defaultmenu.subscription.plans" /></span>
						</a>
							<div class="sidebar-submenu">
								<ul>
									<li view-name="plan" title="<spring:message code="defaultmenu.buyer.plans"/>"><a href="${pageContext.request.contextPath}/admin/plan/buyerPlanList"><spring:message code="defaultmenu.buyer.plans" /></a></li>
									<li view-name="supplierplan" title="<spring:message code="defaultmenu.suppliers.plans"/>"><a href="${pageContext.request.contextPath}/admin/supplierplan/supplierplanList"><spring:message code="defaultmenu.suppliers.plans" /></a></li>

									<li view-name="promotionalcode" title="<spring:message code="defaultmenu.promotional.code"/>"><a href="${pageContext.request.contextPath}/admin/promotionalCodeList"><spring:message code="defaultmenu.promotional.code" /></a></li>

								</ul>
							</div></li>
						<li class="divider"></li>
					</sec:authorize>
					<sec:authorize access="hasRole('ROLE_CONTRACT_LIST') or(hasRole('BUYER') and (hasRole('ADMIN') or ${buyerReadOnlyAdmin}))">
						<li class="no-menu"><a href="#" title="<spring:message code="Product.contract.list"/>"> <i class="glyphicon glyphicon-paste"></i> <span><spring:message code="Product.contract.list" /></span>
						</a>
							<div class="sidebar-submenu">
								<ul>
									<li id="idProductListMenu" view-name="product-contract"><a href="${pageContext.request.contextPath}/buyer/productContractList" title='<spring:message code="Product.contract.dashboard.menu"/>'><spring:message code="Product.contract.dashboard.menu" /></a></li>
								</ul>
							</div></li>
						<li class="divider"></li>
					</sec:authorize>

					<sec:authorize access="hasRole('USER_ADMINISTRATION') or hasRole('ADMIN') or ${buyerReadOnlyAdmin}">
						<li class="no-menu"><a href="#" title="<spring:message code="defaultmenu.users"/>"> <i class="glyphicon glyphicon-user"></i> <span><spring:message code="defaultmenu.users" /></span>
						</a>
							<div class="sidebar-submenu">
								<ul>
									<sec:authorize access="hasRole('USER_ROLE_LIST') or hasRole('ADMIN') or (hasRole('BUYER') and ${buyerReadOnlyAdmin})">
										<li view-name="userrole"><a href="${pageContext.request.contextPath}/admin/listRole" title="<spring:message code="defaultmenu.user.roles"/>"><spring:message code="defaultmenu.user.roles" /></a></li>
									</sec:authorize>
									<sec:authorize access="hasRole('USER_LIST') or hasRole('ADMIN') or (hasRole('BUYER') and ${buyerReadOnlyAdmin})">
										<li view-name="user"><a href="${pageContext.request.contextPath}/admin/listUser" title="<spring:message code="defaultmenu.users"/>"><spring:message code="defaultmenu.users" /></a></li>
									</sec:authorize>
								</ul>
							</div></li>
						<li class="divider"></li>
					</sec:authorize>

					<spring:url value="/admin/budgets/transactionLogs" var="transactionLogs" htmlEscape="true" />
					<!-- p2p -->
					<sec:authorize access="hasRole('ROLE_BUDGET_PLANNER_VIEW_ONLY') or hasRole('ROLE_BUDGET_PLANNER_EDIT')">
						<!--<img src="${pageContext.request.contextPath}/resources/images/icon/budget.png" width="18" height="18"></img>  -->
						<li class="no-menu"><a href="#" title="<spring:message code="defaultmenu.budget.planner"/>"> <i class="glyphicon glyphicon-usd"></i> <span><spring:message code="defaultmenu.budget.planner" /></span>
						</a>
							<div class="sidebar-submenu">
								<ul>
									<sec:authorize access="hasRole('ROLE_BUDGET_PLANNER_VIEW_ONLY') or hasRole('ROLE_BUDGET_PLANNER_EDIT')">
										<li><a href="${pageContext.request.contextPath}/admin/budgets/budgetDashboard" title="<spring:message code="defaultmenu.budget.summary"/>"><spring:message code="defaultmenu.budget.summary" /></a></li>
									</sec:authorize>
									<sec:authorize access="hasRole('ROLE_BUDGET_PLANNER_VIEW_ONLY') or hasRole('ROLE_BUDGET_PLANNER_EDIT')">
										<li><a href="${pageContext.request.contextPath}/admin/budgets/listBudget" title="<spring:message code="defaultmenu.budget.manage"/>"><spring:message code="defaultmenu.budget.manage" /></a></li>
									</sec:authorize>
									<%--moved to manage budget  	
								<sec:authorize access="hasRole('ROLE_BUDGET_PLANNER_EDIT') or hasRole('ADMIN') or (hasRole('BUYER') and ${buyerReadOnlyAdmin})">
										<li	>
											<a href="${pageContext.request.contextPath}/admin/budgets/transactionLogs" title="<spring:message code="transaction.logs"/>"><spring:message code="transaction.logs" /></a>
										</li>
									</sec:authorize> --%>
								</ul>
							</div></li>
						<li class="divider"></li>
					</sec:authorize>

					<sec:authorize access="(hasRole('BUYER') and hasRole('ADMIN')) or (hasRole('BUYER') and (hasRole('BUYER_BILLING') or ${buyerReadOnlyAdmin}))">
						<li class="no-menu"><a href="#" title="<spring:message code="defaultmenu.payment.billing.information"/>"> <i class=" fa fa-newspaper-o"></i> <span><spring:message code="defaultmenu.payment.billing" /></span>
						</a>
							<div class="sidebar-submenu">
								<ul>
									<li view-name="buyerAccountOverview"><a href="${pageContext.request.contextPath}/buyer/billing/accountOverview" title="<spring:message code="paymentbilling.account.overview"/>"> <span><spring:message code="paymentbilling.account.overview" /></span>
									</a></li>
									<li view-name="buyerBilling"><a href="${pageContext.request.contextPath}/buyer/billing/billing" title="<spring:message code="defaultmenu.billing"/>"> <span><spring:message code="defaultmenu.billing" /></span>
									</a></li>
								</ul>
							</div> <!-- .sidebar-submenu --></li>
						<li class="divider"></li>
					</sec:authorize>
					<sec:authorize access="(hasRole('SUPPLIER') and hasRole('ADMIN'))">
						<li class="no-menu"><a href="#" title="<spring:message code="defaultmenu.payment.billing"/>"> <i class=" fa fa-newspaper-o"></i> <span><spring:message code="defaultmenu.payment.billing" /></span>
						</a>
							<div class="sidebar-submenu">
								<ul>
									<li view-name="supplierAccountOverview"><a href="${pageContext.request.contextPath}/supplier/billing/accountOverview" title="<spring:message code="paymentbilling.account.overview"/>"> <span><spring:message code="paymentbilling.account.overview" /></span>
									</a></li>
									<li view-name="supplierBilling"><a href="${pageContext.request.contextPath}/supplier/billing/billing" title="<spring:message code="defaultmenu.billing"/>"> <span><spring:message code="defaultmenu.billing" /></span>
									</a></li>
								</ul>
							</div> <!-- .sidebar-submenu --></li>
						<li class="divider"></li>
					</sec:authorize>
					<sec:authorize access="hasRole('AUDITTRAIL_LIST') or hasRole('ADMIN')  or hasRole('FINANCE') or (hasRole('BUYER') and ${buyerReadOnlyAdmin})">
						<li class="no-menu" view-name="auditTrail" title="<spring:message code="defaultmenu.audit.trail"/>"><a href="${pageContext.request.contextPath}/listAuditTrail"> <i class="glyphicon glyphicon-modal-window"></i> <span><spring:message code="defaultmenu.audit.trail" /></span>
						</a></li>
						<li class="divider"></li>
					</sec:authorize>
					<sec:authorize access="hasRole('OWNER') and hasRole('ADMIN')">
						<li class="no-menu" view-name="paymentTransaction" title="<spring:message code="defaultmenu.payment.transaction"/>"><a href="${pageContext.request.contextPath}/owner/paymentTransaction/paymentTransactionList"> <i class="fa fa-usd" aria-hidden="true"></i> <span><spring:message code="defaultmenu.payment.transaction" /></span>
						</a></li>
						<li class="divider"></li>
					</sec:authorize>
					<sec:authorize access="hasRole('SETTINGS') or (hasRole('BUYER') and ${buyerReadOnlyAdmin}) or hasRole('ROLE_PASSWORD_SETTINGS')">
						<li class="no-menu"><a href="#" title="<spring:message code="defaultmenu.system.setting"/>" id="idSettingsMenu"> <i class="glyphicon glyphicon-cog"></i> <span><spring:message code="defaultmenu.system.setting" /></span>
						</a>
							<div class="sidebar-submenu" id="idEmailSettingsMenu">
								<ul>
									<%-- 									<sec:authorize access="hasRole('EMAIL_SETTINGS')">
										<li>
											<a href="${pageContext.request.contextPath}/admin/emailSettings">Email Settings</a>
										</li>
									</sec:authorize>
 --%>
									<sec:authorize access="(hasRole('OWNER') and hasRole('ADMIN')) or hasRole('OWNER_SETTINGS')">
										<li id="idOwnerSettingsMenu" view-name="ownerSettings" title="<spring:message code="defaultmenu.owner.settings"/>"><a href="${pageContext.request.contextPath}/owner/ownerSettings"><spring:message code="defaultmenu.owner.settings" /></a></li>
									</sec:authorize>
									<sec:authorize access="hasRole('TIMEZONE_LIST') or (hasRole('OWNER') and hasRole('ADMIN'))">
										<li view-name="timezone" title="<spring:message code="defaultmenu.timezone"/>"><a href="${pageContext.request.contextPath}/admin/listTimeZone"><spring:message code="defaultmenu.timezone" /></a></li>
									</sec:authorize>
									<sec:authorize access="hasRole('UOM_LIST') or (hasRole('BUYER') and (hasRole('ADMIN') or ${buyerReadOnlyAdmin})) or (hasRole('OWNER') and hasRole('ADMIN'))">
										<li view-name="uom" title="<spring:message code="label.uom"/>"><a href="${pageContext.request.contextPath}/admin/uom/uomList"><spring:message code="label.uom" /></a></li>
									</sec:authorize>

									<sec:authorize access="hasRole('BUYER') and (hasRole('ADMIN')) or hasRole('ROLE_SUPPLIER_TAG')">
										<li view-name="supplierTags" title="Supplier Tags"><a href="${pageContext.request.contextPath}/buyer/listSupplierTags">Supplier Tags</a></li>
									</sec:authorize>

									<sec:authorize access="hasRole('CURRENCY_LIST') or (hasRole('OWNER') and hasRole('ADMIN'))">
										<li view-name="currency" title="<spring:message code="label.currency"/>"><a href="${pageContext.request.contextPath}/admin/baseCurrencyList"><spring:message code="label.currency" /></a></li>
									</sec:authorize>
									<sec:authorize access="hasRole('COUNTRY_LIST') or (hasRole('OWNER') and hasRole('ADMIN'))">
										<li view-name="country" title="<spring:message code="application.country1"/>"><a href="${pageContext.request.contextPath}/admin/listCountry"><spring:message code="application.country1" /></a></li>
									</sec:authorize>
									<sec:authorize access="hasRole('STATE_LIST') or (hasRole('OWNER') and hasRole('ADMIN'))">
										<li view-name="state" title="<spring:message code="label.state"/>"><a href="${pageContext.request.contextPath}/admin/listState"><spring:message code="label.state" /></a></li>
									</sec:authorize>
									<sec:authorize access="hasRole('INDUSTRY_CATEGORY_LIST') or (hasRole('OWNER') and hasRole('ADMIN'))">
										<li view-name="naics" title="<spring:message code="naics.title"/>"><a href="${pageContext.request.contextPath}/admin/naicsList"><spring:message code="naics.title" /></a></li>
									</sec:authorize>
									<sec:authorize access="hasRole('COMPANY_TYPE_LIST') or (hasRole('OWNER') and hasRole('ADMIN'))">
										<li id="idCompanyTypeMenu" view-name="companyType" title="<spring:message code="label.companystatus"/>"><a href="${pageContext.request.contextPath}/admin/listCompanyStatus"><spring:message code="label.companystatus" /></a></li>
									</sec:authorize>
									<sec:authorize access="hasRole('BUYER_SETTINGS') or (hasRole('BUYER') and hasRole('ADMIN'))">
										<li id="idBuyerSettingsMenu" view-name="buyerSettings"><a href="${pageContext.request.contextPath}/buyer/buyerSettings" title='<spring:message code="label.buyersettings"/>'><spring:message code="label.buyersettings" /></a></li>
									</sec:authorize>
									<sec:authorize access="hasRole('ROLE_DECLARATION_CONFIGURATION') or hasRole('BUYER') and hasRole('ADMIN')">
										<li id="idDeclarationSettingMenu" view-name="declarationSettings"><a href="${pageContext.request.contextPath}/buyer/declarationList" title="<spring:message code="buyer.declarationSettings"/>"><spring:message code="buyer.declarationSettings" /></a></li>
									</sec:authorize>
									<sec:authorize access="hasRole('BUYER') and hasRole('ADMIN')">
										<li id="idApiSettingsMenu" view-name="apiSettings"><a href="${pageContext.request.contextPath}/buyer/apiSettings" title="<spring:message code="buyer.apiSettings"/>"><spring:message code="buyer.apiSettings" /></a></li>
									</sec:authorize>

									<sec:authorize access="hasRole('BUYER') and (hasRole('ADMIN')) or hasRole('ROLE_TRANSFER_OWNERSHIP')">
										<li id="idTransferOwnershipMenu" view-name="transferOwnership"><a href="${pageContext.request.contextPath}/buyer/transferOwnership" title='<spring:message code="buyer.setting.transfer.ownership"/>'><spring:message code="buyer.setting.transfer.ownership" /></a></li>
									</sec:authorize>

									<sec:authorize access="(hasRole('SUPPLIER') and hasRole('ADMIN') ) or hasRole('ROLE_SUPPLIER_SETTINGS')">
										<li id="idSupplierSettingsMenu" view-name="supplierSettings"><a href="${pageContext.request.contextPath}/supplier/supplierSettings" title='<spring:message code="label.suppliersettings"/>'><spring:message code="label.suppliersettings" /></a></li>
									</sec:authorize>
									<sec:authorize access="hasRole('BUYER_INDUSTRY_CATEGORY_LIST') or (hasRole('BUYER') and (hasRole('ADMIN') or ${buyerReadOnlyAdmin}))">
										<li id="idIndustryCategoryMenu" view-name="industryCategory"><a href="${pageContext.request.contextPath}/buyer/listIndustryCategory" title='<spring:message code="defaultmenu.industry.category"/>'><spring:message code="defaultmenu.industry.category" /></a></li>
									</sec:authorize>

									<sec:authorize access="hasRole('GROUP_CODE_LIST') or (hasRole('BUYER') and (hasRole('ADMIN') or ${buyerReadOnlyAdmin}))">
										<li view-name="groupCode"><a href="${pageContext.request.contextPath}/buyer/groupCodeList" title='<spring:message code="label.groupCode"/>'><spring:message code="label.groupCode" /></a></li>
									</sec:authorize>

									<sec:authorize access="hasRole('COST_CENTER_LIST') or (hasRole('BUYER') and (hasRole('ADMIN') or ${buyerReadOnlyAdmin}))">
										<li view-name="costCenter"><a href="${pageContext.request.contextPath}/buyer/listCostCenter" title='<spring:message code="label.costcenter"/>'><spring:message code="label.costcenter" /></a></li>
									</sec:authorize>
									<sec:authorize access="hasRole('BUSINESS_UNIT_LIST') or(hasRole('BUYER') and (hasRole('ADMIN') or ${buyerReadOnlyAdmin}))">
										<li view-name="businessUnit"><a href="${pageContext.request.contextPath}/buyer/listBusinessUnit" title='<spring:message code="label.businessUnit"/>'><spring:message code="label.businessUnit" /></a></li>
									</sec:authorize>
									<sec:authorize access="hasRole('ROLE_AGREEMENT_TYPE_LIST') or(hasRole('BUYER') and (hasRole('ADMIN') or ${buyerReadOnlyAdmin}))">
										<li view-name="agreementType"><a href="${pageContext.request.contextPath}/buyer/agreementType/agreementTypeList" title='<spring:message code="label.agreement.type"/>'><spring:message code="label.agreement.type" /></a></li>
									</sec:authorize>
									<sec:authorize access="((hasRole('BUYER') and (hasRole('ADMIN') or ${buyerReadOnlyAdmin}) ) or (hasRole('BUYER') and hasRole('ROLE_SCORE_RATING')) )">
										<li id="scoreRating" view-name="scoreRating" title="<spring:message code="defaultmenu.score.rating"/>"><a href="${pageContext.request.contextPath}/admin/scoreRating/scoreRatingList"><spring:message code="defaultmenu.score.rating" /></a></li>
									</sec:authorize>
									<sec:authorize access="hasRole('PROCUREMENT_METHOD_LIST') or(hasRole('BUYER') and (hasRole('ADMIN') or ${buyerReadOnlyAdmin}))">
										<li view-name="procurementMethod"><a href="${pageContext.request.contextPath}/buyer/procurementMethodList" title='<spring:message code="label.procurement.method"/>'><spring:message code="label.procurement.method" /></a></li>
									</sec:authorize>
									<sec:authorize access="hasRole('PROCUREMENT_CATEGORY_LIST') or(hasRole('BUYER') and (hasRole('ADMIN') or ${buyerReadOnlyAdmin}))">
										<li view-name="procurementCategories"><a href="${pageContext.request.contextPath}/buyer/procurementCategoriesList" title='<spring:message code="label.procurement.category"/>'><spring:message code="label.procurement.category" /></a></li>
									</sec:authorize>
									<sec:authorize access="hasRole('PRODUCT_CATEGORY_LIST') or (hasRole('BUYER') and (hasRole('ADMIN') or ${buyerReadOnlyAdmin}))">
										<li id="idProductCategoryMenu" view-name="productCategory"><a href="${pageContext.request.contextPath}/buyer/listProductCategory" title='<spring:message code="Product.category"/>'><spring:message code="Product.category" /></a></li>
									</sec:authorize>
									<%-- <sec:authorize access="hasRole('RFS_CATEGORY_LIST') or (hasRole('BUYER') and (hasRole('ADMIN') or ${buyerReadOnlyAdmin}))">
										<li id="idRfsCategoryMenu" view-name="rfsCategory"><a href="${pageContext.request.contextPath}/buyer/listRfsCategory" title="RFS Category">RFS Category</a></li>
									</sec:authorize> --%>
									<sec:authorize access="hasRole('PRODUCT_LIST') or (hasRole('BUYER') and (hasRole('ADMIN') or ${buyerReadOnlyAdmin}))">
										<li id="idProductListMenu" view-name="product"><a href="${pageContext.request.contextPath}/buyer/productList" title='<spring:message code="Productz.list"/>'><spring:message code="Productz.list" /></a></li>
									</sec:authorize>

									<sec:authorize access="hasRole('BUYER_ADDRESS_LIST') or (hasRole('BUYER') and (hasRole('ADMIN') or ${buyerReadOnlyAdmin}))">
										<li id="idAddressMenu" view-name="buyerAddress"><a href="${pageContext.request.contextPath}/buyer/listBuyerAddress" title='<spring:message code="defaultmenu.address"/>'><spring:message code="defaultmenu.address" /></a></li>
									</sec:authorize>
									<sec:authorize access="hasRole('PAYMENT_TERMES_LIST') or (hasRole('BUYER') and (hasRole('ADMIN') or ${buyerReadOnlyAdmin}))">
										<li view-name="paymentTermes"><a href="${pageContext.request.contextPath}/buyer/listPaymentTerms" title='<spring:message code="label.paymenttermes"/>'><spring:message code="label.paymenttermes" /></a></li>
									</sec:authorize>



									<sec:authorize access="hasRole('FINANCE')">
										<li id="idFinanceMenu" view-name="FinanceTimeZone" title='<spring:message code="defaultmenu.timezone"/>'><a href="${pageContext.request.contextPath}/finance/financeSettings"><spring:message code="defaultmenu.timezone" /></a></li>
									</sec:authorize>


									<!-- RFX AND PR ID SETTINGS FOR FUTURE ENHANCEMENT -->
									<sec:authorize access="(hasRole('BUYER') and hasRole('ADMIN')) or (hasRole('ROLE_ID_SETTINGS_LIST') and hasRole('BUYER'))">
										<li id="idSettingsMenu" view-name="idSettings"><a href="${pageContext.request.contextPath}/buyer/idSettingsList" title='<spring:message code="defaultmenu.id.settings"/>'><spring:message code="defaultmenu.id.settings" /></a></li>
									</sec:authorize>

									<sec:authorize access="hasRole('BUYER') and hasRole('ADMIN') or hasRole('ROLE_PASSWORD_SETTINGS')">
										<li id="passwordSetting" view-name="passwordSettings"><a href="${pageContext.request.contextPath}/passwordSetting" title="Password Settings">Password Settings</a></li>
									</sec:authorize>

									<sec:authorize access="(hasRole('SUPPLIER') and hasRole('ADMIN')) or (hasRole('ROLE_ID_SETTINGS_LIST') and hasRole('SUPPLIER'))">
										<li id="supplierSettingsMenuId" view-name="supplierIdSettings"><a href="${pageContext.request.contextPath}/supplier/supplierIdSettingsList" title='<spring:message code="defaultmenu.id.settings"/>'><spring:message code="defaultmenu.id.settings" /></a></li>
									</sec:authorize>
									<sec:authorize access="hasRole('SUPPLIER')">
										<li id="supplierFooterSettings" view-name="supplierFooterSettings"><a href="${pageContext.request.contextPath}/supplier/footerList" title='<spring:message code="defaultmenu.footer.settings"/>'><spring:message code="defaultmenu.footer.settings" /></a></li>
									</sec:authorize>

								</ul>
							</div> <!-- .sidebar-submenu --></li>
						<li class="divider"></li>
					</sec:authorize>
				</ul>
			</div>
			<!-- #sidebar-menu -->
		</div>
	</div>
</div>
<style>
.outerDivFull {
	margin-left: 150px;
}

.p-0 {
	padding: 0;
}

.switchToggle input[type=checkbox] {
	height: 0;
	width: 0;
	visibility: hidden;
	position: absolute;
}

.switchToggle label {
	cursor: pointer;
	text-indent: -9999px;
	width: 72px;
	max-width: 90px;
	height: 22px;
	background: #d1d1d1;
	display: block;
	border-radius: 100px;
	position: relative;
}

.switchToggle label:after {
	content: '';
	position: absolute;
	top: 2px;
	left: 2px;
	width: 18px;
	height: 17px;
	background: #fff;
	border-radius: 90px;
	transition: 0.3s;
}

.switchToggle input:checked+label, .switchToggle input:checked+input+label
	{
	background: #3e98d3;
}

.switchToggle input+label:before, .switchToggle input+input+label:before
	{
	content: '<spring:message code="application.no"/>';
	position: absolute;
	top: 2px;
	left: 25px;
	width: 35px;
	height: 26px;
	border-radius: 90px;
	transition: 0.3s;
	text-indent: 0;
	color: #fff;
}

.switchToggle input:checked+label:before, .switchToggle input:checked+input+label:before
	{
	content: '<spring:message code="application.yes"/>';
	position: absolute;
	top: 2px;
	left: 10px;
	width: 26px;
	height: 26px;
	border-radius: 90px;
	transition: 0.3s;
	text-indent: 0;
	color: #fff;
}

.switchToggle input:checked+label:after, .switchToggle input:checked+input+label:after
	{
	left: calc(100% - 2px);
	transform: translateX(-100%);
}

.switchToggle label:active:after {
	width: 60px;
}

.toggle-switchArea {
	margin: 10px 0 10px 0;
}

.btntoggle {
	padding-right: 130px;
	margin-top: 15px;
}

.setcheckbox {
	width: 50%;
	margin-left: 30px;
	margin-top: 1px;
}

.incomp-icon {
	position: absolute;
	top: 13px;
	right: 10px;
	width: 20px;
	height: 20px;
	color: white;
	background-color: #3e98d3;
	display: flex;
	justify-content: center;
	align-items: center;
	border-radius: 50px;
}
.submenu {
    display: none;
    margin-left: 15px; /* Indent the submenu */
}

.has-submenu:hover .submenu {
    display: block; /* Show submenu on hover */
}

</style>


<!-- 				<div class="modal fade" id="myModal1" role="dialog" > -->
<!-- 				    <div class="modal-dialog"> -->

<!-- 				      <div class="modal-content"> -->

<!-- 				        <div class="modal-body"> -->
<!-- 				          <p>Are you sure</p> -->
<!-- 				        </div> -->
<!-- 				        <div class="modal-footer "> -->
<!-- 				          <button type="button" class="btn btn-info" data-dismiss="modal">OK</button> -->
<!-- 				        </div> -->
<!-- 				      </div> -->

<!-- 				    </div> -->
<!--  				 </div> -->

<script>

	function loadDoc() {
		$.ajax({
			type : "GET",
			url : getContextPath() + "/admin/showWizardTutorial/"
					+ $("#idShowTutorial").is(':checked'),
			success : function() {
				window.location.href = getUrl();
			}
		});
	}

	function loadUnSubscribeEmailDoc() {
		$.ajax({
			type : "GET",
			url : getContextPath() + "/admin/unsubscribeEmailNotifications/"
					+ $("#idUnsubcribeEmailNotifications").is(':checked'),
			success : function() {
				window.location.href = getUrl();
			}
		});
	}

	function loadUnSubscribeEmailDoc() {
		$.ajax({
			type : "GET",
			url : getContextPath() + "/admin/unsubscribeEmailNotifications/"
					+ $("#idUnsubcribeEmailNotifications").is(':checked'),
			success : function() {
				window.location.href = getUrl();
			}
		});
	}

	$("#idShowTutorial")
			.on(
					'change',
					function() {
						if ($(this).is(':checked')) {
							$("#idConfirmRefresh")
									.html(
											'<spring:message code="defaultmenu.show.tutorials"/>');
						} else {

							$("#idConfirmRefresh").html('Hide Tutorial');
						}
					});

    $("#idUnsubcribeEmailNotifications").on('change', function () {
        var isChecked = $(this).is(':checked');
        $("#idRefresh").html(isChecked ? '<spring:message code="dashboard.emailNotifications.subscribe"/>' : '<spring:message code = "dashboard.emailNotifications.Unsubscribe"/>');
        $("#unsubscribeConfirm").html(isChecked ? '<spring:message code="dashboard.emailNotifications.subscribe.confirm"/>' : '<spring:message code="dashboard.emailNotifications.unsubscribe.confirm"/>');
        $("#unsubscribeEmailTitle").html(isChecked ? '<spring:message code="dashboard.emailNotifications.subscribe.confirm.title"/>' : '<spring:message code="dashboard.emailNotifications.unsubscribe.confirm.title"/>');
    });


    function getUrl() {
		var vars = [], hash;
		var hashes = window.location.href.slice(
				window.location.href.indexOf('?') + 1).split('&');
		for (var i = 0; i < hashes.length; i++) {
			hash = hashes[i].split('=');
			vars.push(hash[0]);
			vars[hash[0]] = hash[1];
		}
		return vars;
	}

	function canceltogglebtn() {
		if ($("#idShowTutorial").is(':checked')) {
			$('#idShowTutorial').prop('checked', false);
		} else {
			$('#idShowTutorial').prop('checked', true);
		}
	}

	function cancelUnsbscribeEmailtogglebtn() {
		if ($("#idUnsubcribeEmailNotifications").is(':checked')) {
			$('#idUnsubcribeEmailNotifications').prop('checked', false);
		} else {
			$('#idUnsubcribeEmailNotifications').prop('checked', true);
		}
	}
	/* $('#myModal1').modal({
	    backdrop: 'static',
	    keyboard: false,
	    show: true
	}) */
</script>
<div class="modal fade" id="showtutorialModel1" role="dialog" data-backdrop="static" data-keyboard="false">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="freetrial.confirm.delete" />
				</h3>
				<button class="close for-absulate" type="button" onclick="canceltogglebtn()" data-dismiss="modal">x</button>
			</div>
			<div class="modal-body">
				<label><spring:message code="freetrial.confirm" /></label>
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<a id="idConfirmRefresh" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" style="margin-right: 8px;" id="" onclick="loadDoc()"> <spring:message code="defaultmenu.tutorial" />
				</a>
				<button type="button" onclick="canceltogglebtn()" style="margin-left: 9px;" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1" data-dismiss="modal">
					<spring:message code="freetrial.cancle" />
				</button>
			</div>
		</div>
	</div>
</div>

<div class="modal fade" id="unsubcribeEmailNotificationsModel" role="dialog" data-backdrop="static" data-keyboard="false">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div  class="modal-header" >
				<h3>
                    <label id="unsubscribeEmailTitle"><spring:message code="dashboard.emailNotifications.unsubscribe.confirm.title" /></label>
				</h3>
				<button class="close for-absulate" type="button" onclick="cancelUnsbscribeEmailtogglebtn()" data-dismiss="modal">x</button>
			</div>
			<div class="modal-body">
				<label id = "unsubscribeConfirm"><spring:message code="dashboard.emailNotifications.unsubscribe.confirm" /></label>
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<a id="idRefresh" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" style="margin-right: 8px;" id="" onclick="loadUnSubscribeEmailDoc()"> <spring:message code="defaultmenu.tutorial" />
				</a>
				<button type="button" onclick="cancelUnsbscribeEmailtogglebtn()" style="margin-left: 9px;" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1" data-dismiss="modal">
					<spring:message code="dashboard.emailNotifications.cancel" />
				</button>
			</div>
		</div>
	</div>
</div>


<div class="modal fade" id="unsubcribeEmailNotificationsModel" role="dialog" data-backdrop="static" data-keyboard="false">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div  class="modal-header" >
				<h3>
                    <label id="unsubscribeEmailTitle"><spring:message code="dashboard.emailNotifications.unsubscribe.confirm.title" /></label>
				</h3>
				<button class="close for-absulate" type="button" onclick="cancelUnsbscribeEmailtogglebtn()" data-dismiss="modal">x</button>
			</div>
			<div class="modal-body">
				<label id = "unsubscribeConfirm"><spring:message code="dashboard.emailNotifications.unsubscribe.confirm" /></label>
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<a id="idRefresh" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" style="margin-right: 8px;" id="" onclick="loadUnSubscribeEmailDoc()"> <spring:message code="defaultmenu.tutorial" />
				</a>
				<button type="button" onclick="cancelUnsbscribeEmailtogglebtn()" style="margin-left: 9px;" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1" data-dismiss="modal">
					<spring:message code="dashboard.emailNotifications.cancel" />
				</button>
			</div>
		</div>
	</div>
</div>



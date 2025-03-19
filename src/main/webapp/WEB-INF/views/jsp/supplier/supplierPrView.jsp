<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/assets/elements/procurehere1.css"/>">

<c:set var="viewMode" value="true" scope="request" />
<div id="page-content-wrapper">

	<style>
	.badge {
	  margin-top: 20px;
	  position: relative;
	}
	.badge > .glyphicon {
	  position: absolute;
	  top: -10px;
	  background: red;
	  padding: 5px;
	  border-radius:50%;
	}	
	</style>

	<div id="page-content">
		<div class="container">
			<ol class="breadcrumb">
				<c:url var="supplierDashboard" value="/supplier/supplierDashboard" />
				<li><a href="${supplierDashboard}"> <spring:message code="application.dashboard" />
				</a></li>
				<li class="active"><spring:message code="supplier.po.purchase.order" /></li>
			</ol>

			<ul class="nav-responsive nav nav-tabs marg-bottom-5">
			    <li class="active"><a href="#tab1" data-toggle="tab">Purchase Order</a></li>
			    <c:if test="${onboarded and poFinanceRequest != null}">
			    	<li><a href="#tab4" data-toggle="tab">FinansHere <i class="fa ${poFinanceRequest.status == 'REQUESTED' ? 'fa-clock-o' : (poFinanceRequest.status == 'APPROVED' ? 'fa-check' : ((poFinanceRequest.status == 'ACCEPTED' || poFinanceRequest.status == 'ACTIVE') ? 'fa-check-circle' : 'fa-question' ) ) } fa-lg" aria-hidden="true"></i></a></li>
			    </c:if>

			    <c:choose>
                    <c:when test="${po.status ne 'ORDERED' and po.status ne 'ACCEPTED' and po.status ne 'DECLINED' and po.status ne 'CANCELLED'}">
                        <li><a href="#tab2"  data-toggle="tab">Delivery Orders <i class="bs-badge badge-blue-alt">${po.doCount != null ? po.doCount : 0} </i> </a></li>
                        <li><a href="#tab3" data-toggle="tab">Invoices <i class="bs-badge badge-success">${po.invoiceCount != null ? po.invoiceCount : 0}</i> </a></li>
                    </c:when>
                    <c:otherwise>
                        <li><a href="#tab5" data-toggle="tab">P2P Summary  </a></li>
                        <li><a href="#tab6" data-toggle="tab">Messages  </a></li>
                    </c:otherwise>
                </c:choose>


			</ul>
			<div class="tab-content">
			    <div class="tab-pane active" id="tab1">
					<div class="Section-title title_border gray-bg mar-b20">
						<h2 class="trans-cap supplier">
							<spring:message code="defaultmenu.po" /> : ${po.name}
						</h2>
						<h2 class="trans-cap pull-right">
						<spring:message code="supplier.po.summary.poStatus" /> : <c:if test="${!po.fromIntegration && (po.status=='PENDING' || po.status=='READY' && po.oldStatus!=undefined)}">SUSPENDED</c:if>
						<c:choose>
                                <c:when test="${po.fromIntegration && po.status == 'READY' && (po.oldStatus != null or po.oldStatus != undefined)}">
                                    ${po.oldStatus}
                                </c:when>
                                <c:when test="${po.fromIntegration && po.status == 'ORDERED'}">
                                    ${po.status}
                                </c:when>
                                <c:otherwise>
                                    <c:if test="${po.status != 'PENDING' && po.status != 'READY'}">
                                        ${po.status}
                                    </c:if>
                                </c:otherwise>
                        </c:choose>
						</h2>
					</div>
					<div class="clear"></div>
					<jsp:include page="supplierPrSummary.jsp"></jsp:include>
				</div>
			    <div class="tab-pane" id="tab2">
					<div class="Section-title title_border gray-bg mar-b20">
						<h2 class="trans-cap supplier">
							<spring:message code="defaultmenu.po" /> : ${po.name}
						</h2>
						<h2 class="trans-cap pull-right">
						<spring:message code="supplier.po.summary.poStatus" /> : <c:if test="${po.status=='PENDING' || (po.status=='READY' && po.oldStatus!=undefined)}">SUSPENDED</c:if><c:if test="${po.status!='PENDING' && po.status !='READY'}"> ${po.status}</c:if>
						</h2>
					</div>
					<div class="clear"></div>

			    	<jsp:include page="doQuickView.jsp"></jsp:include>
				</div>
			    <div class="tab-pane" id="tab3">
					<div class="Section-title title_border gray-bg mar-b20">
						<h2 class="trans-cap supplier">
							<spring:message code="defaultmenu.po" /> : ${po.name}
						</h2>
						<h2 class="trans-cap pull-right">
						<spring:message code="supplier.po.summary.poStatus" /> : <c:if test="${po.status=='PENDING' || (po.status=='READY' && po.oldStatus!=undefined)}">SUSPENDED</c:if><c:if test="${po.status!='PENDING' && po.status !='READY'}"> ${po.status}</c:if>
						</h2>
					</div>
					<div class="clear"></div>

			    	<jsp:include page="invoiceQuickView.jsp"></jsp:include>
				</div>
				<div class="tab-pane" id="tab4">
					<div class="Section-title title_border gray-bg mar-b20">
						<h2 class="trans-cap supplier">
							<spring:message code="defaultmenu.po" /> : ${po.name}
						</h2>
						<h2 class="trans-cap pull-right">
						<spring:message code="supplier.po.summary.poStatus" /> : <c:if test="${po.status=='PENDING' || (po.status=='READY' && po.oldStatus!=undefined)}">SUSPENDED</c:if><c:if test="${po.status!='PENDING' && po.status !='READY'}"> ${po.status}</c:if>
						</h2>
					</div>
					<div class="clear"></div>
			    	<jsp:include page="finanshereQuickView.jsp"></jsp:include>
				</div>

                <c:if test="${po.status eq 'ORDERED' or po.status eq 'ACCEPTED' or po.status eq 'DECLINED' or po.status eq 'CANCELLED'}">
                    <div class="tab-pane" id="tab5">
                        <div class="Section-title title_border gray-bg mar-b20">
                            <h2 class="trans-cap supplier">
                                <spring:message code="defaultmenu.po" /> : ${po.name}
                            </h2>
                            <h2 class="trans-cap pull-right">
                            <spring:message code="supplier.po.summary.poStatus" /> : <c:if test="${po.status=='PENDING' || (po.status=='READY' && po.oldStatus!=undefined)}">SUSPENDED</c:if><c:if test="${po.status!='PENDING' && po.status !='READY'}"> ${po.status}</c:if>
                            </h2>
                        </div>
                        <div class="clear"></div>
                        <jsp:include page="p2pSummaryView.jsp"></jsp:include>
                    </div>

                    <div class="tab-pane" id="tab6">
                        <div class="Section-title title_border gray-bg mar-b20">
                            <h2 class="trans-cap supplier">
                                <spring:message code="defaultmenu.po" /> : ${po.name}
                            </h2>
                            <h2 class="trans-cap pull-right">
                            <spring:message code="supplier.po.summary.poStatus" /> :  <c:if test="${po.status=='PENDING' || (po.status=='READY' && po.oldStatus!=undefined)}">SUSPENDED</c:if><c:if test="${po.status!='PENDING' && po.status !='READY'}"> ${po.status}</c:if>
                            </h2>
                        </div>
                        <div class="clear"></div>
                       <jsp:include page="mailBox.jsp"></jsp:include>
                    </div>
                </c:if>
			</div>

		</div>
	</div>
</div>
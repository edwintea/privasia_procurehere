<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<sec:authentication property="principal.languageCode" var="languageCode" />
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<div class="Invited-Supplier-List dashboard-main tabulerDataList table">
	<div class="lower-bar-search-contant-main white-bg pad-t35" style="overflow: auto;">
		<div class="row" id="inferno">
			<div class="ph_tabel_wrapper">
				<div class="container-fluid col-md-12 marg-left-10 marg-right-10">
						<div class="Invited-Supplier-List-table add-supplier ">
							<div class=" ph_tabel_wrapper">
								<div class="ph_tabel_wrapper">
									<div class="container-fluid col-md-12 marg-left-10 marg-right-10">
										<c:url value="/buyer/exportSupplierCsv" var="exportSupplierCsvReport" />
										<form:form action="${exportSupplierCsvReport}" method="post" id="exportSupplierForm" ModelAttribute="searchFilterSupplierPojo">
											<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
											<input type="hidden" id="activeOrInactive">
											<input type="hidden" name="cusStatus" class="idStatus">
											<input type="hidden" name="isRegistered" id="registeredValue" value="false" />
											<button id="exportSupplierReport" class="btn btn-sm btn-success hvr-pop marg-top-10 pull-right" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="tooltip.downloadSupplierList" />' style="margin-right: 20px;">
												<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i>
												</span> <span class="button-content"><spring:message code="supplier.download.supplier.list" /></span>
											</button>
											<table id="favSupptableList" class="display table table-bordered " cellspacing="0" width="100%">
												<thead>
													<tr class="tableHeaderWithSearch">
														<th search-type="" class="checkbox-stylling"><input type="checkbox" class="custom-checkAllbox" name="select_all" id="select-all"></th>
														<th search-type="" class="width_150 width_150_fix align-center"><spring:message code="application.action" /></th>
														<th search-type="text" class="width_150 width_150_fix align-left"><spring:message code="buyercreation.company" /></th>
														<th search-type="text" class="align-left"><spring:message code="label.country" /></th>
														<th search-type="" class="width_150 width_200_fix align-left"><spring:message code="supplier.year.of.established" /></th>
														<th search-type="" class="width_200 width_200_fix align-left"><spring:message code="supplier.registeration.date" /></th>
														<th search-type="select" search-options="statusList" class="width_100 width_100_fix align-left"><spring:message code="application.status" /></th>
														<th search-type="text" class="align-left"><spring:message code="supplier.person.in.charge" /></th>
													</tr>
												</thead>
											</table>
										 </form:form>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
	</div>
			<div class="Invited-Supplier-List dashboard-main tabulerDataList flagvisibility myActiveSupplierData">
				<c:if test="${activeSuppCount > 0}">
					<div class="Invited-Supplier-List-table add-supplier ">
						<div class=" ph_tabel_wrapper">
							<div class="main_table_wrapper ph_table_border ">
									<div class="ph_tabel_wrapper">
										<div class="col-md-12">
											<div class="nopad table-heading col-md-4"><spring:message code="supplier.fav.activeSupp.label"/></div>
										</div>
										<div class="container-fluid col-md-12 marg-left-10 marg-right-10">
<%-- 										<c:url value="/buyer/ExportSupplierByStatus" var="downloadSupplierActiveReport" /> --%>
<%-- 										<form:form action="${downloadSupplierActiveReport}" method="post" id="exportActiveSupplierForm" ModelAttribute="searchFilterSupplierPojo"> --%>
										<c:url value="/buyer/ExportSupplierCsvByStatus" var="downloadSupplierActiveCsv" />
										<form:form action="${downloadSupplierActiveCsv}" method="post" id="exportActiveSupplierCsvForm" ModelAttribute="searchFilterSupplierPojo">
											<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
											<input type="hidden" id="activeOrInactive">
											<input type="hidden" name="isRegistered" id="registeredValue" value="false" />
											<input type="hidden" name="status" id="supplierStatus" value="ACTIVE" />
<%-- 											<button id="exportActiveSupplierReport" class="btn btn-sm btn-success hvr-pop marg-top-10 pull-right" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="tooltip.downloadSupplierList" />' style="margin-right: 20px;"> --%>
<!-- 												<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i> -->
<%-- 												</span> <span class="button-content"><spring:message code="supplier.download.supplier.list" /></span> --%>
								
<!-- 											</button> -->
											<button id="exportActiveSupplierCsvReport" class="btn btn-sm btn-success hvr-pop marg-top-10 pull-right" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="tooltip.downloadSupplierList" />' style="margin-right: 20px;">
												<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i>
												</span> <span class="button-content"><spring:message code="supplier.download.supplier.list" /></span>
								
											</button>
											<table id="activeSupplierTableList" class="display table table-bordered " cellspacing="0" width="100%">
												<thead>
													<tr class="tableHeaderWithSearch">
														<th search-type="" class="checkbox-stylling"><input type="checkbox" class="active-checkAllbox" name="select_all_supp" id="select_all_supp"></th>
														<th search-type="" class="width_150 width_150_fix align-center"><spring:message code="application.action" /></th>
														<th search-type="text" class="width_150 width_150_fix align-left"><spring:message code="buyercreation.company" /></th>
														<th search-type="text" class="align-left"><spring:message code="label.country" /></th>
														<th search-type="" class="width_150 width_200_fix align-left"><spring:message code="supplier.year.of.established" /></th>
														<th search-type="" class="width_200 width_200_fix align-left"><spring:message code="supplier.registeration.date" /></th>
														<th search-type="select" search-options="statusList" class="width_100 width_100_fix align-left"><spring:message code="application.status" /></th>
														<th search-type="text" class="align-left"><spring:message code="supplier.person.in.charge" /></th>
													</tr>
												</thead>
											</table>
										</form:form>
										</div>
									</div>
								</div>
							</div>
						</div>
					</c:if>
				</div>
				<div class="Invited-Supplier-List dashboard-main tabulerDataList flagvisibility myActiveSupplierData1">
					<c:if test="${totalGlobalSuppCount > 0}">
						<div class="Invited-Supplier-List-table add-supplier ">
							<div class=" ph_tabel_wrapper">
								<div class="main_table_wrapper ph_table_border ">
										<div class="ph_tabel_wrapper">
											<div class="col-md-12">
												<div class="nopad table-heading col-md-4"><spring:message code="supplier.fav.totalSupp.label"/></div>
											</div>
											<div class="container-fluid col-md-12 marg-left-10 marg-right-10">
												<table id="totalSupplierTableList" class="display table table-bordered " cellspacing="0" width="100%">
													<thead>
														<tr class="tableHeaderWithSearch">
															<th search-type="" class="width_150 width_150_fix align-center"><spring:message code="application.action" /></th>
															<th search-type="text" class="width_150 width_150_fix align-left"><spring:message code="buyercreation.company" /></th>
															<th search-type="text" class="align-left"><spring:message code="label.country" /></th>
															<th search-type="" class="width_150 width_200_fix align-left"><spring:message code="supplier.year.of.established" /></th>
															<th search-type="" class="width_200 width_200_fix align-left"><spring:message code="supplier.registeration.date" /></th>
															<th search-type="" search-options="statusList" class="width_100 width_100_fix align-left"><spring:message code="application.status" /></th>
															<th search-type="text" class="align-left"><spring:message code="supplier.person.in.charge" /></th>
														</tr>
													</thead>
												</table>
											</div>
										</div>
									</div>
								</div>
							</div>
						</c:if>
				</div>		
				<div class="Invited-Supplier-List dashboard-main tabulerDataList flagvisibility myRequestedSupplierData">
					<c:if test="${requestedPendingSuppCount > 0}">
						<div class="Invited-Supplier-List-table add-supplier ">
							<div class=" ph_tabel_wrapper">
								<div class="main_table_wrapper ph_table_border ">
										<div class="ph_tabel_wrapper">
											<div class="col-md-12">
												<div class="nopad table-heading col-md-4"><spring:message code="supplier.fav.pendingSupp.label"/></div>
											</div>
											<div class="container-fluid col-md-12 marg-left-10 marg-right-10">
<%-- 											<c:url value="/buyer/ExportSupplierByStatus" var="downloadSupplierPendingReport" /> --%>
<%-- 											<form:form action="${downloadSupplierPendingReport}" method="post" id="exportPendingSupplierForm" ModelAttribute="searchFilterSupplierPojo"> --%>
												<c:url value="/buyer/ExportSupplierCsvByStatus" var="downloadPendingSupplierCsvReport" />
												<form:form action="${downloadPendingSupplierCsvReport}" method="post" id="exportPendingSupplierForm" ModelAttribute="searchFilterSupplierPojo">
													<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
													<input type="hidden" id="activeOrInactive">
													<input type="hidden" name="isRegistered" id="registeredValue" value="false" />
													<input type="hidden" name="status" id="supplierStatus" value="PENDING" />
<%-- 													<button id="exportPendingSupplierReport" class="btn btn-sm btn-success hvr-pop marg-top-10 pull-right" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="tooltip.downloadSupplierList" />' style="margin-right: 20px;"> --%>
<!-- 														<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i> -->
<%-- 														</span> <span class="button-content"><spring:message code="supplier.download.supplier.list" /></span> --%>
										
<!-- 													</button> -->
													<button id="exportPendingSupplierCsvReport" class="btn btn-sm btn-success hvr-pop marg-top-10 pull-right" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="tooltip.downloadSupplierList" />' style="margin-right: 20px;">
														<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i>
														</span> <span class="button-content"><spring:message code="supplier.download.supplier.list" /></span>
										
													</button>
												<table id="pendingSupplierTableList" class="display table table-bordered " cellspacing="0" width="100%">
													<thead>
														<tr class="tableHeaderWithSearch">
															<th search-type="" class="checkbox-stylling"><input type="checkbox" class="pending-checkAllbox" name="select_all_supp" id="select_all_supp"></th>
															<th search-type="" class="width_150 width_150_fix align-center"><spring:message code="application.action" /></th>
															<th search-type="text" class="width_150 width_150_fix align-left"><spring:message code="buyercreation.company" /></th>
															<th search-type="text" class="align-left"><spring:message code="label.country" /></th>
															<th search-type="" class="width_150 width_200_fix align-left"><spring:message code="supplier.year.of.established" /></th>
															<th search-type="" class="width_200 width_200_fix align-left"><spring:message code="supplier.registeration.date" /></th>
															<th search-type="select" search-options="statusList" class="width_100 width_100_fix align-left"><spring:message code="application.status" /></th>
															<th search-type="text" class="align-left"><spring:message code="supplier.person.in.charge" /></th>
														</tr>
													</thead>
												</table>
												</form:form>
											</div>
										</div>
								</div>
							</div>
						</div>
					</c:if>
				</div>
				<div class="Invited-Supplier-List dashboard-main tabulerDataList flagvisibility myRequestedSupplierData1">
					<c:if test="${requesteddRejectedSuppCount > 0}">
						<div class="Invited-Supplier-List-table add-supplier ">
							<div class=" ph_tabel_wrapper">
								<div class="main_table_wrapper ph_table_border ">
											<div class="ph_tabel_wrapper">
												<div class="col-md-12">
													<div class="nopad table-heading col-md-4"><spring:message code="supplier.fav.rejectedSupp.label"/></div>
												</div>
												<div class="container-fluid col-md-12 marg-left-10 marg-right-10">
<%-- 												<c:url value="/buyer/ExportSupplierByStatus" var="downloadSupplierRejectedReport" /> --%>
<%-- 												<form:form action="${downloadSupplierRejectedReport}" method="post" id="exportRejectedSupplierForm" ModelAttribute="searchFilterSupplierPojo"> --%>
												<c:url value="/buyer/ExportSupplierCsvByStatus" var="downloadSupplierRejectedCsvReport" />
												<form:form action="${downloadSupplierRejectedCsvReport}" method="post" id="exportRejectedSupplierForm" ModelAttribute="searchFilterSupplierPojo">
													<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
													<input type="hidden" id="activeOrInactive">
													<input type="hidden" name="isRegistered" id="registeredValue" value="false" />
													<input type="hidden" name="status" id="supplierStatus" value="REJECTED" />
<%-- 													<button id="exportRejectedSupplierReport" class="btn btn-sm btn-success hvr-pop marg-top-10 pull-right" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="tooltip.downloadSupplierList" />' style="margin-right: 20px;"> --%>
<!-- 														<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i> -->
<%-- 														</span> <span class="button-content"><spring:message code="supplier.download.supplier.list" /></span> --%>
										
<!-- 													</button> -->
													<button id="exportRejectedSupplierCsv" class="btn btn-sm btn-success hvr-pop marg-top-10 pull-right" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="tooltip.downloadSupplierList" />' style="margin-right: 20px;">
														<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i>
														</span> <span class="button-content"><spring:message code="supplier.download.supplier.list" /></span>
										
													</button>
													<table id="rejectedSupplierTableList" class="display table table-bordered " cellspacing="0" width="100%">
														<thead>
															<tr class="tableHeaderWithSearch">
																<th search-type="" class="checkbox-stylling"><input type="checkbox" class="rejected-checkAllbox" name="select_all_supp" id="select_all_supp"></th>
																<th search-type="" class="width_150 width_150_fix align-center"><spring:message code="application.action" /></th>
																<th search-type="text" class="width_150 width_150_fix align-left"><spring:message code="buyercreation.company" /></th>
																<th search-type="text" class="align-left"><spring:message code="label.country" /></th>
																<th search-type="" class="width_150 width_200_fix align-left"><spring:message code="supplier.year.of.established" /></th>
																<th search-type="" class="width_200 width_200_fix align-left"><spring:message code="supplier.registeration.date" /></th>
																<th search-type="select" search-options="statusList" class="width_100 width_100_fix align-left"><spring:message code="application.status" /></th>
																<th search-type="text" class="align-left"><spring:message code="supplier.person.in.charge" /></th>
															</tr>
														</thead>
													</table>
												</form:form>
												</div>
											</div>
									</div>
								</div>
							</div>
						</c:if>
					</div>						
					<div class="Invited-Supplier-List dashboard-main tabulerDataList flagvisibility myRestrictedSupplierData1">
						<c:if test="${suspendedSuppCount > 0}">
							<div class="Invited-Supplier-List-table add-supplier ">
								<div class=" ph_tabel_wrapper">
									<div class="main_table_wrapper ph_table_border ">
											<div class="ph_tabel_wrapper">
												<div class="col-md-12">
													<div class="nopad table-heading col-md-4"><spring:message code="supplier.fav.suspendedSupp.label"/></div>
												</div>
												<div class="container-fluid col-md-12 marg-left-10 marg-right-10">
<%-- 												<c:url value="/buyer/ExportSupplierByStatus" var="downloadSupplierSuspendedReport" /> --%>
<%-- 												<form:form action="${downloadSupplierSuspendedReport}" method="post" id="exportSuspendedSupplierForm" ModelAttribute="searchFilterSupplierPojo"> --%>
												<c:url value="/buyer/ExportSupplierCsvByStatus" var="downloadSupplierSuspendedCsv" />
												<form:form action="${downloadSupplierSuspendedCsv}" method="post" id="exportSuspendedSupplierForm" ModelAttribute="searchFilterSupplierPojo">
													<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
													<input type="hidden" id="activeOrInactive">
													<input type="hidden" name="isRegistered" id="registeredValue" value="false" />
													<input type="hidden" name="status" id="supplierStatus" value="SUSPENDED" />
<%-- 													<button id="exportSuspendedSupplierReport" class="btn btn-sm btn-success hvr-pop marg-top-10 pull-right" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="tooltip.downloadSupplierList" />' style="margin-right: 20px;"> --%>
<!-- 														<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i> -->
<%-- 														</span> <span class="button-content"><spring:message code="supplier.download.supplier.list" /></span> --%>
										
<!-- 													</button> -->
													<button id="exportSuspendedSupplierCsv" class="btn btn-sm btn-success hvr-pop marg-top-10 pull-right" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="tooltip.downloadSupplierList" />' style="margin-right: 20px;">
														<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i>
														</span> <span class="button-content"><spring:message code="supplier.download.supplier.list" /></span>
										
													</button>
													<table id="suspendedSupplierTableList" class="display table table-bordered " cellspacing="0" width="100%">
														<thead>
															<tr class="tableHeaderWithSearch">
																<th search-type="" class="checkbox-stylling"><input type="checkbox" class="suspended-checkAllbox" name="select_all_supp" id="select_all_supp"></th>
																<th search-type="" class="width_150 width_150_fix align-center"><spring:message code="application.action" /></th>
																<th search-type="text" class="width_150 width_150_fix align-left"><spring:message code="buyercreation.company" /></th>
																<th search-type="text" class="align-left"><spring:message code="label.country" /></th>
																<th search-type="" class="width_150 width_200_fix align-left"><spring:message code="supplier.year.of.established" /></th>
																<th search-type="" class="width_200 width_200_fix align-left"><spring:message code="supplier.registeration.date" /></th>
																<th search-type="select" search-options="statusList" class="width_100 width_100_fix align-left"><spring:message code="application.status" /></th>
																<th search-type="text" class="align-left"><spring:message code="supplier.person.in.charge" /></th>
															</tr>
														</thead>
													</table>
												</form:form>
												</div>
											</div>
									</div>
								</div>
							</div>
						</c:if>
					</div>
					<div class="Invited-Supplier-List dashboard-main tabulerDataList flagvisibility myRestrictedSupplierData">
						<c:if test="${blacklistedSuppCount > 0}">
							<div class="Invited-Supplier-List-table add-supplier ">
								<div class=" ph_tabel_wrapper">
									<div class="main_table_wrapper ph_table_border ">
											<div class="ph_tabel_wrapper">
												<div class="col-md-12">
													<div class="nopad table-heading col-md-4"><spring:message code="supplier.fav.blocklistedSupp.label"/></div>
												</div>
												<div class="container-fluid col-md-12 marg-left-10 marg-right-10">
<%-- 												<c:url value="/buyer/ExportSupplierByStatus" var="downloadSupplierBlackListedReport" /> --%>
<%-- 												<form:form action="${downloadSupplierBlackListedReport}" method="post" id="exportBlackListedSupplierForm" ModelAttribute="searchFilterSupplierPojo"> --%>
												<c:url value="/buyer/ExportSupplierCsvByStatus" var="downloadSupplierBlackListedCsv" />
												<form:form action="${downloadSupplierBlackListedCsv}" method="post" id="exportBlackListedSupplierForm" ModelAttribute="searchFilterSupplierPojo">
													<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
													<input type="hidden" id="activeOrInactive">
													<input type="hidden" name="isRegistered" id="registeredValue" value="false" />
													<input type="hidden" name="status" id="supplierStatus" value="BLACKLISTED" />
<%-- 													<button id="exportBlacklistedSupplierReport" class="btn btn-sm btn-success hvr-pop marg-top-10 pull-right" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="tooltip.downloadSupplierList" />' style="margin-right: 20px;"> --%>
<!-- 														<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i> -->
<%-- 														</span> <span class="button-content"><spring:message code="supplier.download.supplier.list" /></span> --%>
										
<!-- 													</button> -->
													<button id="exportBlacklistedSupplierCsv" class="btn btn-sm btn-success hvr-pop marg-top-10 pull-right" data-toggle="tooltip" data-placement="top" data-original-title='<spring:message code="tooltip.downloadSupplierList" />' style="margin-right: 20px;">
														<span class="glyph-icon icon-separator"> <i class="glyph-icon icon-download"></i>
														</span> <span class="button-content"><spring:message code="supplier.download.supplier.list" /></span>
										
													</button>
													<table id="blacklistedSupplierTableList" class="display table table-bordered " cellspacing="0" width="100%">
														<thead>
															<tr class="tableHeaderWithSearch">
																<th search-type="" class="checkbox-stylling"><input type="checkbox" class="blacklisted-checkAllbox" name="select_all_supp" id="select_all_supp"></th>
																<th search-type="" class="width_150 width_150_fix align-center"><spring:message code="application.action" /></th>
																<th search-type="text" class="width_150 width_150_fix align-left"><spring:message code="buyercreation.company" /></th>
																<th search-type="text" class="align-left"><spring:message code="label.country" /></th>
																<th search-type="" class="width_150 width_200_fix align-left"><spring:message code="supplier.year.of.established" /></th>
																<th search-type="" class="width_200 width_200_fix align-left"><spring:message code="supplier.registeration.date" /></th>
																<th search-type="select" search-options="statusList" class="width_100 width_100_fix align-left"><spring:message code="application.status" /></th>
																<th search-type="text" class="align-left"><spring:message code="supplier.person.in.charge" /></th>
															</tr>
														</thead>
													</table>
												</form:form>
												</div>
											</div>
										</div>
									</div>
								</div>
							</c:if>
					</div>
					<div class="Invited-Supplier-List dashboard-main tabulerDataList flagvisibility mySupplierFormSubData1">
						<c:if test="${pendingSuppFormCount > 0}">
							<div class="Invited-Supplier-List-table add-supplier ">
								<div class=" ph_tabel_wrapper">
									<div class="main_table_wrapper ph_table_border ">
											<div class="ph_tabel_wrapper">
												<div class="col-md-12">
													<div class="nopad table-heading col-md-4"><spring:message code="dashboard.pending.suppliers.forms"/></div>
												</div>
												<div class="container-fluid col-md-12 marg-left-10 marg-right-10">
												<c:url value="/buyer/ExportSupplierByStatus" var="downloadSupplierBlackListedReport" />
													<table id="pendingSuppFormTableList" class="display table table-bordered " cellspacing="0" width="100%">
														<thead>
															<tr class="tableHeaderWithSearch">
																<th search-type="" class=" width_100"><spring:message code="application.action" /></th>
																<th search-type="text" class="align-left"><spring:message code="supplierForm.name.label" /></th>
																<th search-type="" class="align-left"><spring:message code="supplierForm.description.label" /></th>
																<th search-type="" class="align-left"><spring:message code="supplier.form.requested.date" /></th>
																<th search-type="text" class="align-left"><spring:message code="supplier.form.request.owner" /></th>
																<th search-type="text" class="align-left"><spring:message code="application.supplier" /></th>
																<th search-type="" class="align-left"><spring:message code="supplier.form.submitted.date" /></th>
																<th search-type="text" class="align-left"><spring:message code="supplier.form.submit.owner" /></th>
																<th search-type="" class="align-left" search-options="supplierFormSubStatusList"><spring:message code="application.status" /></th>															</tr>
														</thead>
													</table>
												</div>
											</div>
										</div>
									</div>
								</div>
							</c:if>
					</div>
					<div class="Invited-Supplier-List dashboard-main tabulerDataList flagvisibility mySupplierFormSubData">
						<c:if test="${submittedSuppFormCount > 0}">
							<div class="Invited-Supplier-List-table add-supplier ">
								<div class=" ph_tabel_wrapper">
									<div class="main_table_wrapper ph_table_border ">
											<div class="ph_tabel_wrapper">
												<div class="col-md-12">
													<div class="nopad table-heading col-md-4"><spring:message code="dashboard.submitted.suppliers.forms"/></div>
												</div>
												<div class="container-fluid col-md-12 marg-left-10 marg-right-10">
												<c:url value="/buyer/ExportSupplierByStatus" var="downloadSupplierBlackListedReport" />
													<table id="submittedSuppFormTableList" class="display table table-bordered " cellspacing="0" width="100%">
														<thead>
															<tr class="tableHeaderWithSearch">
																<th search-type="" class=" width_100"><spring:message code="application.action" /></th>
																<th search-type="text" class="align-left"><spring:message code="supplierForm.name.label" /></th>
																<th search-type="" class="align-left"><spring:message code="supplierForm.description.label" /></th>
																<th search-type="" class="align-left"><spring:message code="supplier.form.requested.date" /></th>
																<th search-type="text" class="align-left"><spring:message code="supplier.form.request.owner" /></th>
																<th search-type="text" class="align-left"><spring:message code="application.supplier" /></th>
																<th search-type="" class="align-left"><spring:message code="supplier.form.submitted.date" /></th>
																<th search-type="text" class="align-left"><spring:message code="supplier.form.submit.owner" /></th>
																<th search-type="select" class="align-left" search-options="supplierFormSubStatusList"><spring:message code="application.status" /></th>
															</tr>
														</thead>
													</table>
												</div>
											</div>
										</div>
									</div>
								</div>
							</c:if>
					</div>
<input type="hidden" id="delId" value="" name="delId" />
<!-- NEw add -->
<script type="text/javascript" src="<c:url value="/resources/js/view/country.js"/>"></script>
<spring:message code="import.view.details" var="viewdetail"/>
<spring:message code="application.remove" var="remove"/>
<spring:message code="favsupplier.pending.registration" var="pendinglabel" />
<script type="text/javascript">
	var table;
	$('document')
			.ready(
					function() {
						var status = $.trim($('#favSupplierStatus').val());
						table = $('#favSupptableList')
								.DataTable(
										{
											"oLanguage":{
												"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
										},
											"processing" : true,
											"deferRender" : true,
											"preDrawCallback" : function(settings) {
												//$('div[id=idGlobalError]').hide();
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
											"ajax" : {
												"url" : getContextPath() + "/buyer/searchSupplier",
												//"dataType" : "json",
												//"contentType" : "application/json",
												"data" : function(d) {
													d.searchSupplierId = $.trim($('#searchSupplierId').val());
													d.searchCompanyName = $.trim($('#companyName').val());
													// 													var status =$.trim($('#favSupplierStatus').val());
													d.searchCompanyRegistrationNumber = $.trim($('#companyRegistrationNumber').val());
													d.searchOrder = $.trim($('.orderSupplierCustom').val());
													d.globalSearch = $('#globalSearch').is(':checked');
													d.registered = $('#registered').is(':checked');
													var status = $.trim($('select[name=supplierCustomStatus]').val());
													console.log("status : " + status);
													d.searchStatus = status;
													d.searchIndustryCategories = $.trim($('#chosenCategoryAll').val());
													d.searchNaicsCode = $.trim($('#chosenCategoryAllNaics').val());
													d.searchProjectName = $.trim($('#projectName').val());

												},
												"error" : function(request, textStatus, errorThrown) {
													var error = request.getResponseHeader('error');
													if (error != undefined) {
														$('p[id=idGlobalErrorMessage]').html(error != null ? error.split(",").join("<br/>") : "");
														$('div[id=idGlobalError]').show();
													}
													$('#loading').hide();
												},
												"complete" : function() {
													$('[data-toggle="tooltip"]').tooltip();
													$('#loading').hide();
													$('.error-range.text-danger').remove();
													if($('.custom-checkAllbox').is(":checked")){
														$("[type=checkbox]").each(function() {
															$(".custom-checkbox1").prop('checked', true);
															$.uniform.update($(this));
														});
													} 
													
												}
											},
											"order" : [],
											"columns" : [
													{
														'searchable' : false,
														'orderable' : false,
														'className' : 'checkbox-stylling',
														'render' : function(data, type, row) {
															if (!(row.favourite != true)) {
																return '<input type="checkbox" class="custom-checkbox1" value="'+row.id+'" id="poIds" name="poIds">';
															} else {
																return '';
															}
														}
													},
													{
														"mData" : "id",
														"searchable" : false,
														"orderable" : false,
														"className" : "align-center",
														"mRender" : function(data, type, row) {

															//alert(row.favourite);
															var displayBlockfav = 'none';
															var displayremovfav = 'block';
															if (row.favourite == true) {
																displayBlockfav = 'block';
																displayremovfav = 'none';
															}

															var attr = "";
															if (canEdit() === "false") {
																attr = "disabled";
															}
															if (row.favourite != true) {

																var isGryed = false;
																if (row.activeInactive == "BLACKLISTED" || row.activeInactive == "SUSPENDED") {
																	isGryed = true;
																}

																var html = '&nbsp;<a href="${pageContext.request.contextPath}/buyer/supplierDetailsOfGlobalSupplier/'+row.id+'" data-toggle="tooltip" data-placement="top" title="${viewdetail}" class="viewSuppBtn pull-left" aria-hidden="true">'
																		+ '<img style="width: 20px;" src="${pageContext.request.contextPath}/resources/images/view3.png">'
																		+ '</a>&nbsp;'
																		+ '<a href="#" data-toggle="tooltip" data-placement="top" title="Add to my list" class="fa '
																		+ (isGryed ? 'gray-heart' : '')
																		+ ' mar_5 addToListBtnWishlist pull-left marg-left-10" aria-hidden="true" wishListSuppId="'
																		+ row.id
																		+ '" wCommEmail="'
																		+ row.communicationEmail
																		+ '" wFullName="'
																		+ row.fullName
																		+ '" wDesignation="'
																		+ row.designation
																		+ '" wCompanyContactNum="'
																		+ row.companyContactNumber
																		+ '"wActiveInactive="'
																		+ row.status + '" wCompanyFaxNum="' + row.faxNumber + '" style="display:' + displayremovfav + '" ' + attr + '  > <img style="width: 20px;" src="${pageContext.request.contextPath}/resources/images/add.png"> </a>&nbsp';

																if (row.registrationComplete != true) {
																	html += '<a href="javascript:void(0);" data-toggle="tooltip" data-placement="top" title="${pendinglabel}" class="fa fa-clock-o fa-16x margin-r-10" aria-hidden="true" style="color: #0CB6FF";   ></a>&nbsp';
																}

																return html;

															} else {
																var isGryed = false;
																if (row.activeInactive == "BLACKLISTED" || row.activeInactive == "SUSPENDED") {
																	isGryed = true;
																} else {
																	isGryed = false;
																}

																var html = '&nbsp;<a href="${pageContext.request.contextPath}/buyer/supplierDetailsOfGlobalSupplier/'+row.id+'" data-toggle="tooltip" data-placement="right" title="${viewdetail}" class="viewSuppBtn pull-left" aria-hidden="true"><img style="width: 20px;" src="${pageContext.request.contextPath}/resources/images/view3.png"></a>';

																if(row.status == 'ACTIVE' || row.status == 'INACTIVE' || row.status == 'BLACKLISTED' || row.status == 'SUSPENDED' || row.status =='SCHEDULED'){
																	html += '<a href="#" class="${!buyerReadOnlyAdmin ? "" : "disabled"} editfavDetails pull-left marg-left-10" type="button" id="editSuppId" data-id="'+row.id+'" title="Edit" ><img src="${pageContext.request.contextPath}/resources/images/edit1.png"></a>';
																}	
																if (row.registrationComplete != true) {
																	html += '<a href="javascript:void(0);" data-toggle="tooltip" data-placement="top" title="${pendinglabel}" class="fa fa-clock-o fa-16x margin-r-10" aria-hidden="true" style="color: #0CB6FF";   ></a>&nbsp';
																}
																return html;
															}
														}
													}, {
														"mData" : "companyName",
														"className" : "align-left",
													}, {
														"mData" : "countryName",
														"defaultContent" : "",
														"className" : "align-left",
													}, {
														"mData" : "yearOfEstablished",
														"searchable" : false,
														"defaultContent" : "",
														"className" : "align-center",
													}, {
														"mData" : "registrationCompleteDate",
														"searchable" : false,
														"defaultContent" : "",
														"className" : "align-left",
													}, {
														"mData" : "status",
														"orderable" : false,
														"defaultContent" : "",
														"className" : "align-left",
														"mRender" : function(data, type, row) {
															if (row.favourite != true) {
 																return null;
 															}else{
 																return row.status;
 															}

														}

													}, {
														"mData" : "fullName",
														"defaultContent" : "",
														"className" : "align-left",
													}, ],

						"initComplete": function(settings, json) {
						var htmlSearch = '<tr class="tableHeaderWithSearch">';
						$('#favSupptableList thead tr:nth-child(1) th').each(
								function(i) {
									var title = $(this).text();
									if (!(title == "Actions") && $(this).attr('search-type') != '') {
										if ($(this).attr('search-type') == 'select') {
											var optionsType = $(this).attr('search-options');
											htmlSearch += '<th style="' + $(this).attr("style") + '"><select data-index="' + i + '" name="' + (title.replace(/ /g, "")).toLowerCase() + '" class="statusListDropDwn"><option value=""><spring:message code="application.search"/> ' + title
													+ '</option>';
											if (optionsType == 'statusList') {
												<c:forEach items="${statusList}" var="item">
												htmlSearch += '<option value="${item}">${item}</option>';
												</c:forEach>
											}
											htmlSearch += '</select></th>';
											/* htmlSearch += '<th></th>'; */
										} else {
											htmlSearch += '<th style="' + $(this).attr("style") + '" class="align-left"><input type="text" name="' + (title.replace(/ /g, "")).toLowerCase() + '" placeholder="<spring:message code="application.search"/> ' + title + '" data-index="' + i
													+ '" /></th>';
										}
									} else {
										htmlSearch += '<th style="' + $(this).attr("style") + '"><div style="visibility:hidden;' + $(this).attr("style") + '"></div></th>';
									}
								});
						htmlSearch += '</tr>';
						$('#favSupptableList thead').append(htmlSearch);
						$(table.table().container()).on('keyup', 'thead input[type=text]', function() {
							if ($.trim(this.value).length > 2 || this.value.length == 0) {
								table.column($(this).data('index')).search(this.value).draw();
							}
						});
						$(table.table().container()).on('change', 'thead select', function() {
							table.column($(this).data('index')).search(this.value).draw();
						});

						$('.custom-checkAllbox').on('change', function(e) {
							e.preventDefault();
							var check = this.checked;
							$("[type=checkbox]").each(function() {
								$(".custom-checkbox1").prop('checked', check);
								$.uniform.update($(this));
							});
						});
						$("#exportSupplierReport").click(function(e) {
							e.preventDefault();
							var status = $.trim($('select[name=supplierCustomStatus]').val());
							var globalSearch = $('#globalSearch').prop('checked');
							
							console.log('Status >> ' + status + " globalSearch : " + globalSearch);
							if(globalSearch != true){
								$('.idStatus').val(status);
							}
							$('.error-range.text-danger').remove();
							var val = [];
							$('.custom-checkbox1:checked').each(function(i) {
								val[i] = $(this).val();
							});

							if (typeof val === 'undefined' || val == '') {
								var isAllCheck = $('#select-all').is(':checked');
								if (!isAllCheck) {
									$('#exportSupplierReport').before('<p style="margin-top:10px;" class="error-range text-danger">Please Select atleast one Supplier</p>');
									return false;
								} else {
									$('#exportSupplierForm').submit();
								}
							} else {
								$('#exportSupplierForm').submit();
							}

						});
						
						$("#exportSupplierCsvReport").click(function(e) {
							e.preventDefault();
							var status = $.trim($('select[name=supplierCustomStatus]').val());
							var globalSearch = $('#globalSearch').prop('checked');
							
							console.log('Status >> ' + status + " globalSearch : " + globalSearch);
							if(globalSearch != true){
								$('.idStatus').val(status);
							}
							$('.error-range.text-danger').remove();
							var val = [];
							$('.custom-checkbox1:checked').each(function(i) {
								val[i] = $(this).val();
							});

							if (typeof val === 'undefined' || val == '') {
								var isAllCheck = $('#select-all').is(':checked');
								if (!isAllCheck) {
									$('#exportSupplierCsvReport').before('<p style="margin-top:10px;" class="error-range text-danger">Please Select atleast one Supplier</p>');
									return false;
								} else {
									$('#exportSupplierForm').submit();
								}
							} else {
								$('#exportSupplierForm').submit();
							}

						});
						
						
						}
					});
					});
</script>
<div class="modal fade" id="favSuppDelete" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3><spring:message code="application.confirm" /></h3>
				<button class="close for-absulate" type="button" data-dismiss="modal">×</button>
			</div>
			<div class="modal-body">
				<label> <spring:message code="import.remove.supplier" />
				</label>
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<input type="hidden" id="favSuppDeleteId" />
				<button type="button" class="btn btn-info ph_btn_small hvr-pop hvr-rectangle-out" id="removeFavSupplierById">
					<spring:message code="application.remove" />
				</button>
				<button type="button" class="btn btn-black btn-default ph_btn_small hvr-pop hvr-rectangle-out1 pull-right" title='<spring:message code="tooltip.delete" />' data-dismiss="modal">
					<spring:message code="application.cancel" />
				</button>
			</div>
		</div>
	</div>
</div>
<div class="modal fade" id="mydeleteFormModal" role="dialog">
	<div class="modal-dialog for-delete-all reminder">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h3>
					<spring:message code="application.confirm.delete" />
				</h3>
				<button class="close for-absulate" onclick="javascript:doCancel();" type="button" data-dismiss="modal">×</button>
			</div>
			<div class="modal-body">
				<label> <spring:message code="supplierForm.delete" />
				</label>
			</div>
			<div class="modal-footer pad_all_15 float-left width-100 border-top-width-1">
				<a id="idFormConfirmDelete" class="btn btn-info ph_btn_small pull-left hvr-pop hvr-rectangle-out" href="${pageContext.request.contextPath}/buyer/deleteSuppFormSub?id=" title="Delete"> <spring:message code="application.delete" />
				</a>
				<button type="button" onclick="javascript:doFormCancel();" class="btn btn-black pull-right btn-default ph_btn_small hvr-pop hvr-rectangle-out1" data-dismiss="modal">
					<spring:message code="application.cancel" />
				</button>
			</div>
		</div>
	</div>
</div>

<style>
.checkbox-stylling input {
	width: 15px !important;
	height: 15px !important;
	text-align: center !important;
}

td.checkbox-stylling {
	text-align: center !important;
	vertical-align: middle !important;
}

td {
	text-align: center !important;
}

.margin-r-10 {
	margin-left: 10px;
	float: left;
}

.gray-heart {
	color: #999 !important;
	pointer-events: none;
}
.tableHeaderWithSearch:first-child>th {
	font-size: 13px !important;
}
.tableHeaderWithSearch:nth-child(2)>th {
	font-size: 12px !important;
}
</style>
<script type="text/javascript">
<c:if test="${activeSuppCount > 0}">
var myActiveSupplierData;
$('document').ready(function() {
	myActiveSupplierData = $('#activeSupplierTableList').DataTable({
		"oLanguage":{
			"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
	},
	"processing" : true,
	"deferRender" : true,
	"preDrawCallback" : function(settings) {
		//$('div[id=idGlobalError]').hide();
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
	"ajax" : {
		"url" : getContextPath() + "/buyer/activeSupplierList",
		"data" : function(d) {
				},
				"error" : function(request, textStatus, errorThrown) {
					var error = request.getResponseHeader('error');
					if (error != undefined) {
						$('p[id=idGlobalErrorMessage]').html(error != null ? error.split(",").join("<br/>") : "");
						$('div[id=idGlobalError]').show();
					}
					$('#loading').hide();
				},
				"complete" : function() {
					$('[data-toggle="tooltip"]').tooltip();
					$('#loading').hide();
					$('.error-range.text-danger').remove();
					if($('.active-checkAllbox').is(":checked")){
						$("[type=checkbox]").each(function() {
							$(".active-checkbox1").prop('checked', true);
							$.uniform.update($(this));
						});
					} 
				}
	},
	"order" : [],
	"columns" : [{
		'searchable' : false,
		'orderable' : false,
		'className' : 'checkbox-stylling',
		'render' : function(data, type, row) {
				return '<input type="checkbox" class="active-checkbox1" value="'+row.id+'" id="poIds" name="poIds">';
		}
	},{
		"mData" : "id",
		"searchable" : false,
		"orderable" : false,
		"className" : "align-center",
		"mRender" : function(data, type, row) {

			//alert(row.favourite);
			var displayBlockfav = 'none';
			var displayremovfav = 'block';
			if (row.favourite == true) {
				displayBlockfav = 'block';
				displayremovfav = 'none';
			}

			var attr = "";
			if (canEdit() === "false") {
				attr = "disabled";
			}
			var isGryed = false;
			if (row.activeInactive == "BLACKLISTED" || row.activeInactive == "SUSPENDED") {
				isGryed = true;
			} else {
				isGryed = false;
			}

			var html = '&nbsp;<a href="${pageContext.request.contextPath}/buyer/supplierDetailsOfGlobalSupplier/'+row.id+'" data-toggle="tooltip" data-placement="right" title="${viewdetail}" class="viewSuppBtn pull-left" aria-hidden="true">'
					+ '<img style="width: 20px;" src="${pageContext.request.contextPath}/resources/images/view3.png">'
					+ '</a>'
					+ '<a href="#" class="${!buyerReadOnlyAdmin ? "" : "disabled"} editfavDetails pull-left marg-left-10" type="button" id="editSuppId" data-id="'
					+ row.id
					+ '"  title="Edit" ><img src="${pageContext.request.contextPath}/resources/images/edit1.png"></a>'
			if (row.registrationComplete != true) {
				html += '<a href="javascript:void(0);" data-toggle="tooltip" data-placement="top" title="${pendinglabel}" class="fa fa-clock-o fa-16x margin-r-10" aria-hidden="true" style="color: #0CB6FF";   ></a>&nbsp';
			}
			return html;
		}
	}, {
		"mData" : "companyName",
		"className" : "align-left",
	}, {
		"mData" : "countryName",
		"defaultContent" : "",
		"className" : "align-left",
	}, {
		"mData" : "yearOfEstablished",
		"searchable" : false,
		"defaultContent" : "",
		"className" : "align-center",
	}, {
		"mData" : "registrationCompleteDate",
		"searchable" : false,
		"defaultContent" : "",
		"className" : "align-left",
	}, {
		"mData" : "status",
		"searchable" : false,
		"orderable" : false,
		"defaultContent" : "",
		"className" : "align-left",

	}, {
		"mData" : "fullName",
		"defaultContent" : "",
		"className" : "align-left",
	}, ],

			"initComplete": function(settings, json) {
				var htmlSearch = '<tr class="tableHeaderWithSearch">';
				$('#activeSupplierTableList thead tr:nth-child(1) th').each(
						function(i) {
							var title = $(this).text();
							if (!(title == "Actions") && $(this).attr('search-type') != '') {
								if ($(this).attr('search-type') == 'select') {
									htmlSearch += '<th style="' + $(this).attr("style") + '"><div style="visibility:hidden;' + $(this).attr("style") + '"></div></th>';
								} else {
									htmlSearch += '<th style="' + $(this).attr("style") + '" class="align-left"><input type="text" name="' + (title.replace(/ /g, "")).toLowerCase() + '" placeholder="<spring:message code="application.search"/> ' + title + '" data-index="' + i
											+ '" /></th>';
								}
							} else {
								htmlSearch += '<th style="' + $(this).attr("style") + '"><div style="visibility:hidden;' + $(this).attr("style") + '"></div></th>';
							}
						});
				htmlSearch += '</tr>';
				$('#activeSupplierTableList thead').append(htmlSearch);
				$(myActiveSupplierData.table().container()).on('keyup', 'thead input[type=text]', function() {
					if ($.trim(this.value).length > 2 || this.value.length == 0) {
						myActiveSupplierData.column($(this).data('index')).search(this.value).draw();
					}
				});
				$(myActiveSupplierData.table().container()).on('change', 'thead select', function() {
					myActiveSupplierData.column($(this).data('index')).search(this.value).draw();
				});
				
				//myActiveSupplierData.columns.adjust();
				
				$('.active-checkAllbox').on('change', function(e) {
					e.preventDefault();
					var check = this.checked;
					$("[type=checkbox]").each(function() {
						$(".active-checkbox1").prop('checked', check);
						$.uniform.update($(this));
					});
				});
				$("#exportActiveSupplierReport").click(function(e) {
					e.preventDefault();

					$('.error-range.text-danger').remove();
					var val = [];
					$('.active-checkbox1:checked').each(function(i) {
						val[i] = $(this).val();
					});

					if (typeof val === 'undefined' || val == '') {
						var isAllCheck = $('.active-checkAllbox').is(':checked');
						if (!isAllCheck) {
							$('#exportActiveSupplierReport').before('<p style="margin-top:10px;" class="error-range text-danger">Please Select atleast one Supplier</p>');
							return false;
						} else {
							$('#exportActiveSupplierForm').submit();
						}
					} else {
						$('#exportActiveSupplierForm').submit();
					}

				});
				
				$("#exportActiveSupplierCsvReport").click(function(e) {
					e.preventDefault();

					$('.error-range.text-danger').remove();
					var val = [];
					$('.active-checkbox1:checked').each(function(i) {
						val[i] = $(this).val();
					});

					if (typeof val === 'undefined' || val == '') {
						var isAllCheck = $('.active-checkAllbox').is(':checked');
						if (!isAllCheck) {
							$('#exportActiveSupplierCsvReport').before('<p style="margin-top:10px;" class="error-range text-danger">Please Select atleast one Supplier</p>');
							return false;
						} else {
							$('#exportActiveSupplierCsvForm').submit();
						}
					} else {
						$('#exportActiveSupplierCsvForm').submit();
					}

				});

			}
		});
});
</c:if>
<c:if test="${totalGlobalSuppCount > 0}">
var myActiveSupplierData1;
$('document').ready(function() {
	myActiveSupplierData1 = $('#totalSupplierTableList').DataTable({
		"oLanguage":{
			"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
	},
	"processing" : true,
	"deferRender" : true,
	"preDrawCallback" : function(settings) {
		//$('div[id=idGlobalError]').hide();
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
	"ajax" : {
		"url" : getContextPath() + "/buyer/totalSupplierList",
		"data" : function(d) {
				}
	},
	"order" : [],
	"columns" : [{
		"mData" : "id",
		"searchable" : false,
		"orderable" : false,
		"className" : "align-center",
		"mRender" : function(data, type, row) {

			//alert(row.favourite);
			var displayBlockfav = 'none';
			var displayremovfav = 'block';
			if (row.favourite == true) {
				displayBlockfav = 'block';
				displayremovfav = 'none';
			}

			var attr = "";
			if (canEdit() === "false") {
				attr = "disabled";
			}
			var isGryed = false;
			if (row.activeInactive == "BLACKLISTED" || row.activeInactive == "SUSPENDED") {
				isGryed = true;
			}

			var html = '&nbsp;<a href="${pageContext.request.contextPath}/buyer/supplierDetailsOfGlobalSupplier/'+row.id+'" data-toggle="tooltip" data-placement="top" title="${viewdetail}" class="viewSuppBtn pull-left" aria-hidden="true">'
					+ '<img style="width: 20px;" src="${pageContext.request.contextPath}/resources/images/view3.png">'
					+ '</a>&nbsp;'
					+ '<a href="#" data-toggle="tooltip" data-placement="top" title="Add to my list" class="fa '
					+ (isGryed ? 'gray-heart' : '')
					+ ' mar_5 addToListBtnWishlist pull-left marg-left-10" aria-hidden="true" wishListSuppId="'
					+ row.id
					+ '" wCommEmail="'
					+ row.communicationEmail
					+ '" wFullName="'
					+ row.fullName
					+ '" wDesignation="'
					+ row.designation
					+ '" wCompanyContactNum="'
					+ row.companyContactNumber
					+ '"wActiveInactive="'
					+ row.status + '" wCompanyFaxNum="' + row.faxNumber + '" style="display:' + displayremovfav + '" ' + attr + '  > <img style="width: 20px;" src="${pageContext.request.contextPath}/resources/images/add.png"> </a>&nbsp';

			if (row.registrationComplete != true) {
				html += '<a href="javascript:void(0);" data-toggle="tooltip" data-placement="top" title="${pendinglabel}" class="fa fa-clock-o fa-16x margin-r-10" aria-hidden="true" style="color: #0CB6FF";   ></a>&nbsp';
			}

			return html;
		}
	}, {
		"mData" : "companyName",
		"className" : "align-left",
	}, {
		"mData" : "countryName",
		"defaultContent" : "",
		"className" : "align-left",
	}, {
		"mData" : "yearOfEstablished",
		"searchable" : false,
		"defaultContent" : "",
		"className" : "align-center",
	}, {
		"mData" : "registrationCompleteDate",
		"searchable" : false,
		"defaultContent" : "",
		"className" : "align-left",
	}, {
		"mData" : "status",
		"searchable" : false,
		"orderable" : false,
		"defaultContent" : "",
		"className" : "align-left",
		"mRender" : function(data, type, row) {
			return null;
		}

	}, {
		"mData" : "fullName",
		"defaultContent" : "",
		"className" : "align-left",
	}, ],

			"initComplete": function(settings, json) {
				var htmlSearch = '<tr class="tableHeaderWithSearch">';
				$('#totalSupplierTableList thead tr:nth-child(1) th').each(
						function(i) {
							var title = $(this).text();
							if (!(title == "Actions") && $(this).attr('search-type') != '') {
								if ($(this).attr('search-type') == 'select') {
									htmlSearch += '<th style="' + $(this).attr("style") + '"><div style="visibility:hidden;' + $(this).attr("style") + '"></div></th>';
								} else {
									htmlSearch += '<th style="' + $(this).attr("style") + '" class="align-left"><input type="text" name="' + (title.replace(/ /g, "")).toLowerCase() + '" placeholder="<spring:message code="application.search"/> ' + title + '" data-index="' + i
											+ '" /></th>';
								}
							} else {
								htmlSearch += '<th style="' + $(this).attr("style") + '"><div style="visibility:hidden;' + $(this).attr("style") + '"></div></th>';
							}
						});
				htmlSearch += '</tr>';
				$('#totalSupplierTableList thead').append(htmlSearch);
				$(myActiveSupplierData1.table().container()).on('keyup', 'thead input[type=text]', function() {
					if ($.trim(this.value).length > 2 || this.value.length == 0) {
						myActiveSupplierData1.column($(this).data('index')).search(this.value).draw();
					}
				});
				$(myActiveSupplierData1.table().container()).on('change', 'thead select', function() {
					myActiveSupplierData1.column($(this).data('index')).search(this.value).draw();
				});

			}
		});
});
</c:if>

<c:if test="${requestedPendingSuppCount > 0}">
var myRequestedSupplierData;
$('document').ready(function() {
	myRequestedSupplierData = $('#pendingSupplierTableList').DataTable({
		"oLanguage":{
			"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
	},
	"processing" : true,
	"deferRender" : true,
	"preDrawCallback" : function(settings) {
		//$('div[id=idGlobalError]').hide();
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
	"ajax" : {
		"url" : getContextPath() + "/buyer/pendingSupplierList",
		"data" : function(d) {
				},
		"error" : function(request, textStatus, errorThrown) {
			var error = request.getResponseHeader('error');
			if (error != undefined) {
				$('p[id=idGlobalErrorMessage]').html(error != null ? error.split(",").join("<br/>") : "");
				$('div[id=idGlobalError]').show();
			}
			$('#loading').hide();
		},
		"complete" : function() {
			$('[data-toggle="tooltip"]').tooltip();
			$('#loading').hide();
			$('.error-range.text-danger').remove();
			if($('.pending-checkAllbox').is(":checked")){
				$("[type=checkbox]").each(function() {
					$(".pending-checkbox1").prop('checked', true);
					$.uniform.update($(this));
				});
			} 
		}
	},
	"order" : [],
	"columns" : [{
			'searchable' : false,
			'orderable' : false,
			'className' : 'checkbox-stylling',
			'render' : function(data, type, row) {
					return '<input type="checkbox" class="pending-checkbox1" value="'+row.id+'" id="poIds" name="poIds">';
			}
		},{
		"mData" : "id",
		"searchable" : false,
		"orderable" : false,
		"className" : "align-center",
		"mRender" : function(data, type, row) {

			//alert(row.favourite);
			var displayBlockfav = 'none';
			var displayremovfav = 'block';
			if (row.favourite == true) {
				displayBlockfav = 'block';
				displayremovfav = 'none';
			}

			var attr = "";
			if (canEdit() === "false") {
				attr = "disabled";
			}
			var isGryed = false;
			if (row.activeInactive == "BLACKLISTED" || row.activeInactive == "SUSPENDED") {
				isGryed = true;
			} else {
				isGryed = false;
			}

			var html = '&nbsp;<a href="${pageContext.request.contextPath}/buyer/supplierDetailsOfGlobalSupplier/'+row.id+'" data-toggle="tooltip" data-placement="right" title="${viewdetail}" class="viewSuppBtn pull-left" aria-hidden="true">'
					+ '<img style="width: 20px;" src="${pageContext.request.contextPath}/resources/images/view3.png">'
					+ '</a>'
			if (row.registrationComplete != true) {
				html += '<a href="javascript:void(0);" data-toggle="tooltip" data-placement="top" title="${pendinglabel}" class="fa fa-clock-o fa-16x margin-r-10" aria-hidden="true" style="color: #0CB6FF";   ></a>&nbsp';
			}
			return html;
		}
	}, {
		"mData" : "companyName",
		"className" : "align-left",
	}, {
		"mData" : "countryName",
		"defaultContent" : "",
		"className" : "align-left",
	}, {
		"mData" : "yearOfEstablished",
		"searchable" : false,
		"defaultContent" : "",
		"className" : "align-center",
	}, {
		"mData" : "registrationCompleteDate",
		"searchable" : false,
		"defaultContent" : "",
		"className" : "align-left",
	}, {
		"mData" : "status",
		"searchable" : false,
		"orderable" : false,
		"defaultContent" : "",
		"className" : "align-left",

	}, {
		"mData" : "fullName",
		"defaultContent" : "",
		"className" : "align-left",
	}, ],

			"initComplete": function(settings, json) {
				var htmlSearch = '<tr class="tableHeaderWithSearch">';
				$('#pendingSupplierTableList thead tr:nth-child(1) th').each(
						function(i) {
							var title = $(this).text();
							if (!(title == "Actions") && $(this).attr('search-type') != '') {
								if ($(this).attr('search-type') == 'select') {
									htmlSearch += '<th style="' + $(this).attr("style") + '"><div style="visibility:hidden;' + $(this).attr("style") + '"></div></th>';
								} else {
									htmlSearch += '<th style="' + $(this).attr("style") + '" class="align-left"><input type="text" name="' + (title.replace(/ /g, "")).toLowerCase() + '" placeholder="<spring:message code="application.search"/> ' + title + '" data-index="' + i
											+ '" /></th>';
								}
							} else {
								htmlSearch += '<th style="' + $(this).attr("style") + '"><div style="visibility:hidden;' + $(this).attr("style") + '"></div></th>';
							}
						});
				htmlSearch += '</tr>';
				$('#pendingSupplierTableList thead').append(htmlSearch);
				$(myRequestedSupplierData.table().container()).on('keyup', 'thead input[type=text]', function() {
					if ($.trim(this.value).length > 2 || this.value.length == 0) {
						myRequestedSupplierData.column($(this).data('index')).search(this.value).draw();
					}
				});
				$(myRequestedSupplierData.table().container()).on('change', 'thead select', function() {
					myRequestedSupplierData.column($(this).data('index')).search(this.value).draw();
				});
				
				$('.pending-checkAllbox').on('change', function(e) {
					e.preventDefault();
					var check = this.checked;
					$("[type=checkbox]").each(function() {
						$(".pending-checkbox1").prop('checked', check);
						$.uniform.update($(this));
					});
				});
				$("#exportPendingSupplierReport").click(function(e) {
					e.preventDefault();

					$('.error-range.text-danger').remove();
					var val = [];
					$('.pending-checkbox1:checked').each(function(i) {
						val[i] = $(this).val();
					});

					if (typeof val === 'undefined' || val == '') {
						var isAllCheck = $('.pending-checkAllbox').is(':checked');
						if (!isAllCheck) {
							$('#exportPendingSupplierReport').before('<p style="margin-top:10px;" class="error-range text-danger">Please Select atleast one Supplier</p>');
							return false;
						} else {
							$('#exportPendingSupplierForm').submit();
						}
					} else {
						$('#exportPendingSupplierForm').submit();
					}

				});
				
				$("#exportPendingSupplierCsvReport").click(function(e) {
					e.preventDefault();

					$('.error-range.text-danger').remove();
					var val = [];
					$('.pending-checkbox1:checked').each(function(i) {
						val[i] = $(this).val();
					});

					if (typeof val === 'undefined' || val == '') {
						var isAllCheck = $('.pending-checkAllbox').is(':checked');
						if (!isAllCheck) {
							$('#exportPendingSupplierCsvReport').before('<p style="margin-top:10px;" class="error-range text-danger">Please Select atleast one Supplier</p>');
							return false;
						} else {
							$('#exportPendingSupplierForm').submit();
						}
					} else {
						$('#exportPendingSupplierForm').submit();
					}

				});


			}
		});
});
</c:if>
<c:if test="${requesteddRejectedSuppCount > 0}">
var myRequestedSupplierData1;
$('document').ready(function() {
	myRequestedSupplierData1 = $('#rejectedSupplierTableList').DataTable({
		"oLanguage":{
			"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
	},
	"processing" : true,
	"deferRender" : true,
	"preDrawCallback" : function(settings) {
		//$('div[id=idGlobalError]').hide();
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
	"ajax" : {
		"url" : getContextPath() + "/buyer/rejectedSupplierList",
		"data" : function(d) {
				},
			"error" : function(request, textStatus, errorThrown) {
				var error = request.getResponseHeader('error');
				if (error != undefined) {
					$('p[id=idGlobalErrorMessage]').html(error != null ? error.split(",").join("<br/>") : "");
					$('div[id=idGlobalError]').show();
				}
				$('#loading').hide();
			},
			"complete" : function() {
				$('[data-toggle="tooltip"]').tooltip();
				$('#loading').hide();
				$('.error-range.text-danger').remove();
				if($('.rejected-checkAllbox').is(":checked")){
					$("[type=checkbox]").each(function() {
						$(".rejected-checkbox1").prop('checked', true);
						$.uniform.update($(this));
					});
				} 
			}
	},
	"order" : [],
	"columns" : [{
		'searchable' : false,
		'orderable' : false,
		'className' : 'checkbox-stylling',
		'render' : function(data, type, row) {
				return '<input type="checkbox" class="rejected-checkbox1" value="'+row.id+'" id="poIds" name="poIds">';
		}
	},{
		"mData" : "id",
		"searchable" : false,
		"orderable" : false,
		"className" : "align-center",
		"mRender" : function(data, type, row) {

			//alert(row.favourite);
			var displayBlockfav = 'none';
			var displayremovfav = 'block';
			if (row.favourite == true) {
				displayBlockfav = 'block';
				displayremovfav = 'none';
			}

			var attr = "";
			if (canEdit() === "false") {
				attr = "disabled";
			}
			var isGryed = false;
			if (row.activeInactive == "BLACKLISTED" || row.activeInactive == "SUSPENDED") {
				isGryed = true;
			} else {
				isGryed = false;
			}

			var html = '&nbsp;<a href="${pageContext.request.contextPath}/buyer/supplierDetailsOfGlobalSupplier/'+row.id+'" data-toggle="tooltip" data-placement="right" title="${viewdetail}" class="viewSuppBtn pull-left" aria-hidden="true">'
					+ '<img style="width: 20px;" src="${pageContext.request.contextPath}/resources/images/view3.png">'
					+ '</a>'
			if (row.registrationComplete != true) {
				html += '<a href="javascript:void(0);" data-toggle="tooltip" data-placement="top" title="${pendinglabel}" class="fa fa-clock-o fa-16x margin-r-10" aria-hidden="true" style="color: #0CB6FF";   ></a>&nbsp';
			}
			return html;
		}
	}, {
		"mData" : "companyName",
		"className" : "align-left",
	}, {
		"mData" : "countryName",
		"defaultContent" : "",
		"className" : "align-left",
	}, {
		"mData" : "yearOfEstablished",
		"searchable" : false,
		"defaultContent" : "",
		"className" : "align-center",
	}, {
		"mData" : "registrationCompleteDate",
		"searchable" : false,
		"defaultContent" : "",
		"className" : "align-left",
	}, {
		"mData" : "status",
		"searchable" : false,
		"orderable" : false,
		"defaultContent" : "",
		"className" : "align-left",

	}, {
		"mData" : "fullName",
		"defaultContent" : "",
		"className" : "align-left",
	}, ],

			"initComplete": function(settings, json) {
				var htmlSearch = '<tr class="tableHeaderWithSearch">';
				$('#rejectedSupplierTableList thead tr:nth-child(1) th').each(
						function(i) {
							var title = $(this).text();
							if (!(title == "Actions") && $(this).attr('search-type') != '') {
								if ($(this).attr('search-type') == 'select') {
									htmlSearch += '<th style="' + $(this).attr("style") + '"><div style="visibility:hidden;' + $(this).attr("style") + '"></div></th>';
								} else {
									htmlSearch += '<th style="' + $(this).attr("style") + '" class="align-left"><input type="text" name="' + (title.replace(/ /g, "")).toLowerCase() + '" placeholder="<spring:message code="application.search"/> ' + title + '" data-index="' + i
											+ '" /></th>';
								}
							} else {
								htmlSearch += '<th style="' + $(this).attr("style") + '"><div style="visibility:hidden;' + $(this).attr("style") + '"></div></th>';
							}
						});
				htmlSearch += '</tr>';
				$('#rejectedSupplierTableList thead').append(htmlSearch);
				$(myRequestedSupplierData1.table().container()).on('keyup', 'thead input[type=text]', function() {
					if ($.trim(this.value).length > 2 || this.value.length == 0) {
						myRequestedSupplierData1.column($(this).data('index')).search(this.value).draw();
					}
				});
				$(myRequestedSupplierData1.table().container()).on('change', 'thead select', function() {
					myRequestedSupplierData1.column($(this).data('index')).search(this.value).draw();
				});

				$('.rejected-checkAllbox').on('change', function(e) {
					e.preventDefault();
					var check = this.checked;
					$("[type=checkbox]").each(function() {
						$(".rejected-checkbox1").prop('checked', check);
						$.uniform.update($(this));
					});
				});
				$("#exportRejectedSupplierReport").click(function(e) {
					e.preventDefault();

					$('.error-range.text-danger').remove();
					var val = [];
					$('.rejected-checkbox1:checked').each(function(i) {
						val[i] = $(this).val();
					});

					if (typeof val === 'undefined' || val == '') {
						var isAllCheck = $('.rejected-checkAllbox').is(':checked');
						if (!isAllCheck) {
							$('#exportRejectedSupplierReport').before('<p style="margin-top:10px;" class="error-range text-danger">Please Select atleast one Supplier</p>');
							return false;
						} else {
							$('#exportRejectedSupplierForm').submit();
						}
					} else {
						$('#exportRejectedSupplierForm').submit();
					}

				});
				
				$("#exportRejectedSupplierCsv").click(function(e) {
					e.preventDefault();

					$('.error-range.text-danger').remove();
					var val = [];
					$('.rejected-checkbox1:checked').each(function(i) {
						val[i] = $(this).val();
					});

					if (typeof val === 'undefined' || val == '') {
						var isAllCheck = $('.rejected-checkAllbox').is(':checked');
						if (!isAllCheck) {
							$('#exportRejectedSupplierCsv').before('<p style="margin-top:10px;" class="error-range text-danger">Please Select atleast one Supplier</p>');
							return false;
						} else {
							$('#exportRejectedSupplierForm').submit();
						}
					} else {
						$('#exportRejectedSupplierForm').submit();
					}

				});

			}
		});
});
</c:if>
<c:if test="${blacklistedSuppCount > 0}">
var myRestrictedSupplierData;
$('document').ready(function() {
	myRestrictedSupplierData = $('#blacklistedSupplierTableList').DataTable({
		"oLanguage":{
			"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
	},
	"processing" : true,
	"deferRender" : true,
	"preDrawCallback" : function(settings) {
		//$('div[id=idGlobalError]').hide();
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
	"ajax" : {
		"url" : getContextPath() + "/buyer/blacklistedSupplierList",
		"data" : function(d) {
				},
				"error" : function(request, textStatus, errorThrown) {
					var error = request.getResponseHeader('error');
					if (error != undefined) {
						$('p[id=idGlobalErrorMessage]').html(error != null ? error.split(",").join("<br/>") : "");
						$('div[id=idGlobalError]').show();
					}
					$('#loading').hide();
				},
				"complete" : function() {
					$('[data-toggle="tooltip"]').tooltip();
					$('#loading').hide();
					$('.error-range.text-danger').remove();
					if($('.blacklisted-checkAllbox').is(":checked")){
						$("[type=checkbox]").each(function() {
							$(".blacklisted-checkbox1").prop('checked', true);
							$.uniform.update($(this));
						});
					} 
				}
	},
	"order" : [],
	"columns" : [{
		'searchable' : false,
		'orderable' : false,
		'className' : 'checkbox-stylling',
		'render' : function(data, type, row) {
				return '<input type="checkbox" class="blacklisted-checkbox1" value="'+row.id+'" id="poIds" name="poIds">';
		}
	},{
		"mData" : "id",
		"searchable" : false,
		"orderable" : false,
		"className" : "align-center",
		"mRender" : function(data, type, row) {

			//alert(row.favourite);
			var displayBlockfav = 'none';
			var displayremovfav = 'block';
			if (row.favourite == true) {
				displayBlockfav = 'block';
				displayremovfav = 'none';
			}

			var attr = "";
			if (canEdit() === "false") {
				attr = "disabled";
			}
			var isGryed = false;
			if (row.activeInactive == "BLACKLISTED" || row.activeInactive == "SUSPENDED") {
				isGryed = true;
			} else {
				isGryed = false;
			}

			var html = '&nbsp;<a href="${pageContext.request.contextPath}/buyer/supplierDetailsOfGlobalSupplier/'+row.id+'" data-toggle="tooltip" data-placement="right" title="${viewdetail}" class="viewSuppBtn pull-left" aria-hidden="true">'
					+ '<img style="width: 20px;" src="${pageContext.request.contextPath}/resources/images/view3.png">'
					+ '</a>'
					+ '<a href="#" class="${!buyerReadOnlyAdmin ? "" : "disabled"} editfavDetails pull-left marg-left-10" type="button" id="editSuppId" data-id="'
					+ row.id
					+ '"  title="Edit" ><img src="${pageContext.request.contextPath}/resources/images/edit1.png"></a>'
			if (row.registrationComplete != true) {
				html += '<a href="javascript:void(0);" data-toggle="tooltip" data-placement="top" title="${pendinglabel}" class="fa fa-clock-o fa-16x margin-r-10" aria-hidden="true" style="color: #0CB6FF";   ></a>&nbsp';
			}
			return html;
		}
	}, {
		"mData" : "companyName",
		"className" : "align-left",
	}, {
		"mData" : "countryName",
		"defaultContent" : "",
		"className" : "align-left",
	}, {
		"mData" : "yearOfEstablished",
		"searchable" : false,
		"defaultContent" : "",
		"className" : "align-center",
	}, {
		"mData" : "registrationCompleteDate",
		"searchable" : false,
		"defaultContent" : "",
		"className" : "align-left",
	}, {
		"mData" : "status",
		"searchable" : false,
		"orderable" : false,
		"defaultContent" : "",
		"className" : "align-left",

	}, {
		"mData" : "fullName",
		"defaultContent" : "",
		"className" : "align-left",
	}, ],

			"initComplete": function(settings, json) {
				var htmlSearch = '<tr class="tableHeaderWithSearch">';
				$('#blacklistedSupplierTableList thead tr:nth-child(1) th').each(
						function(i) {
							var title = $(this).text();
							if (!(title == "Actions") && $(this).attr('search-type') != '') {
								if ($(this).attr('search-type') == 'select') {
									htmlSearch += '<th style="' + $(this).attr("style") + '"><div style="visibility:hidden;' + $(this).attr("style") + '"></div></th>';
								} else {
									htmlSearch += '<th style="' + $(this).attr("style") + '" class="align-left"><input type="text" name="' + (title.replace(/ /g, "")).toLowerCase() + '" placeholder="<spring:message code="application.search"/> ' + title + '" data-index="' + i
											+ '" /></th>';
								}
							} else {
								htmlSearch += '<th style="' + $(this).attr("style") + '"><div style="visibility:hidden;' + $(this).attr("style") + '"></div></th>';
							}
						});
				htmlSearch += '</tr>';
				$('#blacklistedSupplierTableList thead').append(htmlSearch);
				$(myRestrictedSupplierData.table().container()).on('keyup', 'thead input[type=text]', function() {
					if ($.trim(this.value).length > 2 || this.value.length == 0) {
						myRestrictedSupplierData.column($(this).data('index')).search(this.value).draw();
					}
				});
				$(myRestrictedSupplierData.table().container()).on('change', 'thead select', function() {
					myRestrictedSupplierData.column($(this).data('index')).search(this.value).draw();
				});

				$('.blacklisted-checkAllbox').on('change', function(e) {
					e.preventDefault();
					var check = this.checked;
					$("[type=checkbox]").each(function() {
						$(".blacklisted-checkbox1").prop('checked', check);
						$.uniform.update($(this));
					});
				});
				$("#exportBlacklistedSupplierReport").click(function(e) {
					e.preventDefault();

					$('.error-range.text-danger').remove();
					var val = [];
					$('.blacklisted-checkbox1:checked').each(function(i) {
						val[i] = $(this).val();
					});

					if (typeof val === 'undefined' || val == '') {
						var isAllCheck = $('.blacklisted-checkAllbox').is(':checked');
						if (!isAllCheck) {
							$('#exportBlacklistedSupplierReport').before('<p style="margin-top:10px;" class="error-range text-danger">Please Select atleast one Supplier</p>');
							return false;
						} else {
							$('#exportBlackListedSupplierForm').submit();
						}
					} else {
						$('#exportBlackListedSupplierForm').submit();
					}

				});
				
				$("#exportBlacklistedSupplierCsv").click(function(e) {
					e.preventDefault();

					$('.error-range.text-danger').remove();
					var val = [];
					$('.blacklisted-checkbox1:checked').each(function(i) {
						val[i] = $(this).val();
					});

					if (typeof val === 'undefined' || val == '') {
						var isAllCheck = $('.blacklisted-checkAllbox').is(':checked');
						if (!isAllCheck) {
							$('#exportBlacklistedSupplierCsv').before('<p style="margin-top:10px;" class="error-range text-danger">Please Select atleast one Supplier</p>');
							return false;
						} else {
							$('#exportBlackListedSupplierForm').submit();
						}
					} else {
						$('#exportBlackListedSupplierForm').submit();
					}

				});


			}
		});
});
</c:if>
<c:if test="${suspendedSuppCount> 0}">
var myRestrictedSupplierData1;
$('document').ready(function() {
	myRestrictedSupplierData1 = $('#suspendedSupplierTableList').DataTable({
		"oLanguage":{
			"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
	},
	"processing" : true,
	"deferRender" : true,
	"preDrawCallback" : function(settings) {
		//$('div[id=idGlobalError]').hide();
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
	"ajax" : {
		"url" : getContextPath() + "/buyer/suspendedSupplierList",
		"data" : function(d) {
				},
				"error" : function(request, textStatus, errorThrown) {
					var error = request.getResponseHeader('error');
					if (error != undefined) {
						$('p[id=idGlobalErrorMessage]').html(error != null ? error.split(",").join("<br/>") : "");
						$('div[id=idGlobalError]').show();
					}
					$('#loading').hide();
				},
				"complete" : function() {
					$('[data-toggle="tooltip"]').tooltip();
					$('#loading').hide();
					$('.error-range.text-danger').remove();
					if($('.suspended-checkAllbox').is(":checked")){
						$("[type=checkbox]").each(function() {
							$(".suspended-checkbox1").prop('checked', true);
							$.uniform.update($(this));
						});
					} 
				}
	},
	"order" : [],
	"columns" : [{
		'searchable' : false,
		'orderable' : false,
		'className' : 'checkbox-stylling',
		'render' : function(data, type, row) {
				return '<input type="checkbox" class="suspended-checkbox1" value="'+row.id+'" id="poIds" name="poIds">';
		}
	},{
		"mData" : "id",
		"searchable" : false,
		"orderable" : false,
		"className" : "align-center",
		"mRender" : function(data, type, row) {

			//alert(row.favourite);
			var displayBlockfav = 'none';
			var displayremovfav = 'block';
			if (row.favourite == true) {
				displayBlockfav = 'block';
				displayremovfav = 'none';
			}

			var attr = "";
			if (canEdit() === "false") {
				attr = "disabled";
			}
			var isGryed = false;
			if (row.activeInactive == "BLACKLISTED" || row.activeInactive == "SUSPENDED") {
				isGryed = true;
			} else {
				isGryed = false;
			}

			var html = '&nbsp;<a href="${pageContext.request.contextPath}/buyer/supplierDetailsOfGlobalSupplier/'+row.id+'" data-toggle="tooltip" data-placement="right" title="${viewdetail}" class="viewSuppBtn pull-left" aria-hidden="true">'
					+ '<img style="width: 20px;" src="${pageContext.request.contextPath}/resources/images/view3.png">'
					+ '</a>'
					+ '<a href="#" class="${!buyerReadOnlyAdmin ? "" : "disabled"} editfavDetails pull-left marg-left-10" type="button" id="editSuppId" data-id="'
					+ row.id
					+ '"  title="Edit" ><img src="${pageContext.request.contextPath}/resources/images/edit1.png"></a>'
			if (row.registrationComplete != true) {
				html += '<a href="javascript:void(0);" data-toggle="tooltip" data-placement="top" title="${pendinglabel}" class="fa fa-clock-o fa-16x margin-r-10" aria-hidden="true" style="color: #0CB6FF";   ></a>&nbsp';
			}
			return html;
		}
	}, {
		"mData" : "companyName",
		"className" : "align-left",
	}, {
		"mData" : "countryName",
		"defaultContent" : "",
		"className" : "align-left",
	}, {
		"mData" : "yearOfEstablished",
		"searchable" : false,
		"defaultContent" : "",
		"className" : "align-center",
	}, {
		"mData" : "registrationCompleteDate",
		"searchable" : false,
		"defaultContent" : "",
		"className" : "align-left",
	}, {
		"mData" : "status",
		"searchable" : false,
		"orderable" : false,
		"defaultContent" : "",
		"className" : "align-left",

	}, {
		"mData" : "fullName",
		"defaultContent" : "",
		"className" : "align-left",
	}, ],

			"initComplete": function(settings, json) {
				var htmlSearch = '<tr class="tableHeaderWithSearch">';
				$('#suspendedSupplierTableList thead tr:nth-child(1) th').each(
						function(i) {
							var title = $(this).text();
							if (!(title == "Actions") && $(this).attr('search-type') != '') {
								if ($(this).attr('search-type') == 'select') {
									htmlSearch += '<th style="' + $(this).attr("style") + '"><div style="visibility:hidden;' + $(this).attr("style") + '"></div></th>';
								} else {
									htmlSearch += '<th style="' + $(this).attr("style") + '" class="align-left"><input type="text" name="' + (title.replace(/ /g, "")).toLowerCase() + '" placeholder="<spring:message code="application.search"/> ' + title + '" data-index="' + i
											+ '" /></th>';
								}
							} else {
								htmlSearch += '<th style="' + $(this).attr("style") + '"><div style="visibility:hidden;' + $(this).attr("style") + '"></div></th>';
							}
						});
				htmlSearch += '</tr>';
				$('#suspendedSupplierTableList thead').append(htmlSearch);
				$(myRestrictedSupplierData1.table().container()).on('keyup', 'thead input[type=text]', function() {
					if ($.trim(this.value).length > 2 || this.value.length == 0) {
						myRestrictedSupplierData1.column($(this).data('index')).search(this.value).draw();
					}
				});
				$(myRestrictedSupplierData1.table().container()).on('change', 'thead select', function() {
					myRestrictedSupplierData1.column($(this).data('index')).search(this.value).draw();
				});
				
				$('.suspended-checkAllbox').on('change', function(e) {
					e.preventDefault();
					var check = this.checked;
					$("[type=checkbox]").each(function() {
						$(".suspended-checkbox1").prop('checked', check);
						$.uniform.update($(this));
					});
				});
				$("#exportSuspendedSupplierReport").click(function(e) {
					e.preventDefault();

					$('.error-range.text-danger').remove();
					var val = [];
					$('.suspended-checkbox1:checked').each(function(i) {
						val[i] = $(this).val();
					});

					if (typeof val === 'undefined' || val == '') {
						var isAllCheck = $('.suspended-checkAllbox').is(':checked');
						if (!isAllCheck) {
							$('#exportSuspendedSupplierReport').before('<p style="margin-top:10px;" class="error-range text-danger">Please Select atleast one Supplier</p>');
							return false;
						} else {
							$('#exportSuspendedSupplierForm').submit();
						}
					} else {
						$('#exportSuspendedSupplierForm').submit();
					}

				});
				
				$("#exportSuspendedSupplierCsv").click(function(e) {
					e.preventDefault();

					$('.error-range.text-danger').remove();
					var val = [];
					$('.suspended-checkbox1:checked').each(function(i) {
						val[i] = $(this).val();
					});

					if (typeof val === 'undefined' || val == '') {
						var isAllCheck = $('.suspended-checkAllbox').is(':checked');
						if (!isAllCheck) {
							$('#exportSuspendedSupplierCsv').before('<p style="margin-top:10px;" class="error-range text-danger">Please Select atleast one Supplier</p>');
							return false;
						} else {
							$('#exportSuspendedSupplierForm').submit();
						}
					} else {
						$('#exportSuspendedSupplierForm').submit();
					}

				});

			}
		});
});
</c:if>
<c:if test="${pendingSuppFormCount> 0}">
var mySupplierFormSubData1='';
$('document').ready(function() {

	// Setup - add a text input to each footer cell
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");

	mySupplierFormSubData1 = $('#pendingSuppFormTableList').DataTable({
		"oLanguage":{
			"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
	},
		"processing" : true,
		"deferRender" : true,
		"preDrawCallback" : function(settings) {
			$('#loading').show();
			return true;
		},
		"drawCallback" : function() {
			// in case your overlay needs to be put away automatically you can put it here
			$("#poReportId").val(false);
			$('#loading').hide();
		},
		"serverSide" : true,
		"pageLength" : 10,
		"searching" : true,
		"ajax" : {
			"url" : getContextPath() + "/buyer/pendingSupplierFormSubData",
			"data" : {},
			"error" : function(request, textStatus, errorThrown) {
				var error = request.getResponseHeader('error');
				if (error != undefined) {
					$('p[id=idGlobalErrorMessage]').html(error != null ? error.split(",").join("<br/>") : "");
					$('div[id=idGlobalError]').show();
				}
				$('#loading').hide();
			}
		},
		"order" : [],
		"columns" : [ {
			"mData" : "id",
			"searchable" : false,
			"orderable" : false,
			"mRender":function(data,type,row){
				var tImg="";
				var ret='';
				tImg='<img src="${pageContext.request.contextPath}/resources/images/delete1.png">';
				ret += '<a href="supplierFormSubView/' + row.id + '"  title="Edit"><img src="${pageContext.request.contextPath}/resources/images/edit1.png"></a>'; 
				ret += '<a href="#mydeleteFormModal" onClick="javascript:updateFormLink(\'' + row.id +'\');" title="Delete" role="button"  data-toggle="modal">' + tImg +'</a>';
				return ret;
			}
		},{
			"data" : "name",
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "description",
			"searchable" : false,
			"className" : "align-left",
			"defaultContent" : ""
		}, {
			"data" : "requestedDate",
			"searchable" : false,
			"className" : "align-left",
			"type" : 'custom-date',
			"defaultContent" : ""
		},{
			"data" : "requestedBy",
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "supplierCompanyName",
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "submitedDate",
			"searchable" : false,
			"className" : "align-left",
			"type" : 'custom-date',
			"defaultContent" : ""
		}, {
			"data" : "submittedBy",
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "status",
			"orderable" : false,
			"searchable" : false,
			"className" : "align-left",
			"defaultContent" : ""
		}],
		"initComplete": function(settings, json) {
	var htmlSearch = '<tr class="tableHeaderWithSearch">';
	$('#pendingSuppFormTableList thead tr:nth-child(1) th').each(function(i) {
		var title = $(this).text();
		var classStyle =  $(this).attr("class");
		if(classStyle.indexOf('sorting_desc') || classStyle.indexOf('sorting_asc') || classStyle.indexOf('sorting')){
			classStyle = classStyle.replace('sorting','');
		}
		if (!(title == "Actions") && $(this).attr('search-type') != '') {
				htmlSearch += '<th class="' + classStyle + '"><input type="text" placeholder="<spring:message code="application.search"/> '+title+'" data-index="'+i+'" /></th>';
		} else {
			htmlSearch += '<th class="' + classStyle + '"><div style="visibility:hidden;' + $(this).attr("style") + '"></div></th>';
		}
	});
	htmlSearch += '</tr>';
	$('#pendingSuppFormTableList thead').append(htmlSearch);
	$(mySupplierFormSubData1.table().container()).on('keyup', 'thead input', function() {
		if ($.trim(this.value).length > 2 || this.value.length == 0) {
			mySupplierFormSubData1.column($(this).data('index')).search(this.value).draw();
		}
	});
	$(mySupplierFormSubData1.table().container()).on('change', 'thead select', function() {
		mySupplierFormSubData1.column($(this).data('index')).search(this.value).draw();
	});
	}
	});

});	
</c:if>
<c:if test="${submittedSuppFormCount> 0}">
var mySupplierFormSubData='';
$('document').ready(function() {

	// Setup - add a text input to each footer cell
	var header = $("meta[name='_csrf_header']").attr("content");
	var token = $("meta[name='_csrf']").attr("content");

	mySupplierFormSubData = $('#submittedSuppFormTableList').DataTable({
		"oLanguage":{
			"sUrl":  getContextPath() + "/resources/assets/widgets/datatable/datatable.${languageCode}.json"
	},
		"processing" : true,
		"deferRender" : true,
		"preDrawCallback" : function(settings) {
			$('#loading').show();
			return true;
		},
		"drawCallback" : function() {
			// in case your overlay needs to be put away automatically you can put it here
			$("#poReportId").val(false);
			$('#loading').hide();
		},
		"serverSide" : true,
		"pageLength" : 10,
		"searching" : true,
		"ajax" : {
			"url" : getContextPath() + "/buyer/submittedSupplierFormSubData",
			"data" : {},
			"error" : function(request, textStatus, errorThrown) {
				var error = request.getResponseHeader('error');
				if (error != undefined) {
					$('p[id=idGlobalErrorMessage]').html(error != null ? error.split(",").join("<br/>") : "");
					$('div[id=idGlobalError]').show();
				}
				$('#loading').hide();
			}
		},
		"order" : [],
		"columns" : [ {
			"mData":"id",
			"searchable":false,
			"orderable":false,
			"mRender":function(data,type,row){
				var tImg="";
				var ret='';
				tImg='<img src="${pageContext.request.contextPath}/resources/images/delete1.png">';
				ret += '<a href="supplierFormSubView/' + row.id + '"  title="Edit"><img src="${pageContext.request.contextPath}/resources/images/edit1.png"></a>'; 
				return ret;
			}
		},{
			"data" : "name",
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "description",
			"searchable" : false,
			"className" : "align-left",
			"defaultContent" : ""
		}, {
			"data" : "requestedDate",
			"searchable" : false,
			"className" : "align-left",
			"type" : 'custom-date',
			"defaultContent" : ""
		},{
			"data" : "requestedBy",
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "supplierCompanyName",
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "submitedDate",
			"searchable" : false,
			"className" : "align-left",
			"type" : 'custom-date',
			"defaultContent" : ""
		}, {
			"data" : "submittedBy",
			"className" : "align-left",
			"defaultContent" : ""
		},{
			"data" : "status",
			"orderable" : false,
			"className" : "align-left",
			"defaultContent" : ""
		}],
		"initComplete": function(settings, json) {
	var htmlSearch = '<tr class="tableHeaderWithSearch">';
	$('#submittedSuppFormTableList thead tr:nth-child(1) th').each(function(i) {
		var title = $(this).text();
		var classStyle =  $(this).attr("class");
		if(classStyle.indexOf('sorting_desc') || classStyle.indexOf('sorting_asc') || classStyle.indexOf('sorting')){
			classStyle = classStyle.replace('sorting','');
		}
		if (!(title == "Actions") && $(this).attr('search-type') != '') {
			if ($(this).attr('search-type') == 'select') {
				var optionsType = $(this).attr('search-options');
				htmlSearch += '<th  class="' + classStyle + '"><select data-index="'+i+'"><option value="">Search ' + title + '</option>';
				if (optionsType == 'supplierFormSubStatusList') {
					<c:forEach items="${supplierFormSubStatusList}" var="item">
					htmlSearch += '<option value="${item}">${item}</option>';
					</c:forEach>
				}
				htmlSearch += '</select></th>';
			}else{
				htmlSearch += '<th class="' + classStyle + '"><input type="text" placeholder="<spring:message code="application.search"/> '+title+'" data-index="'+i+'" /></th>';
			}
		} else {
			htmlSearch += '<th class="' + classStyle + '"><div style="visibility:hidden;' + $(this).attr("style") + '"></div></th>';
		}
	});
	htmlSearch += '</tr>';
	$('#submittedSuppFormTableList thead').append(htmlSearch);
	$(mySupplierFormSubData.table().container()).on('keyup', 'thead input', function() {
		if ($.trim(this.value).length > 2 || this.value.length == 0) {
			mySupplierFormSubData.column($(this).data('index')).search(this.value).draw();
		}
	});
	$(mySupplierFormSubData.table().container()).on('change', 'thead select', function() {
		mySupplierFormSubData.column($(this).data('index')).search(this.value).draw();
	});
	}
	});

});	
</c:if>
	$(window).bind('load', function() {
		$('.box-bottom').click(function(e) {
			$("[type=checkbox]").each(function() {
				$(this).prop('checked', false);
				$.uniform.update($(this));
			});
			var targetElm = $(this).attr('data-target');
			$('.Invited-Supplier-List.dashboard-main.tabulerDataList').addClass('flagvisibility');
			var showTable = $('.Invited-Supplier-List.dashboard-main.tabulerDataList.' + targetElm);

			// Max 3 tables in one group. Try each
			if (typeof window[targetElm] !== 'undefined') {
				window[targetElm].ajax.reload();
			}

			
			showTable.removeClass('flagvisibility');
			//JS error in pending events error fixed
			if (showTable.length) {

				$('html,body').animate({
					scrollTop : showTable.offset().top
				}, 'slow');
			}
		});
		
		
		$("#searchSupplier").click(function(e) {
			e.preventDefault();
			// orderedSearch();
            if($('.custom-checkAllbox').is(":checked")){ 
                $("[type=checkbox]").each(function() {
                        $(".custom-checkbox1").prop('checked', false);
                });
                $(".custom-checkAllbox").prop('checked', false);
        	}
			
			$('.Invited-Supplier-List.dashboard-main.tabulerDataList').addClass('flagvisibility');
			var showTable = $('.Invited-Supplier-List.dashboard-main.tabulerDataList.table');
			window['table'].ajax.reload();
			
				showTable.removeClass('flagvisibility');
				//JS error in pending events error fixed
				if (showTable.length) {

					$('html,body').animate({
						scrollTop : showTable.offset().top
					}, 'slow');
				}
		});
		
	});


	function updateFormLink(id){
		var link=$("a#idFormConfirmDelete");
		link.data('href', link.attr("href"));
		link.attr("href", link.data('href') + '' + id);
	}
	function doFormCancel() {
		var link = $("a#idFormConfirmDelete");
		link.data('href', link.attr("href"));
		link.attr("href", '${pageContext.request.contextPath}/buyer/deleteSuppFormSub?id=');
	}

	
</script>
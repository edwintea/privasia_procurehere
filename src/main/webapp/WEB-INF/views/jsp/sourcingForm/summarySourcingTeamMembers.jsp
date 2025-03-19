<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<sec:authorize access="hasRole('ROLE_ADMIN_READONLY')" var="buyerReadOnlyAdmin" />
<div class="panel sum-accord">
	<div class="panel-heading">
		<h4 class="panel-title">
			<a data-toggle="collapse" class="accordion" href="#collapseTeam"> <spring:message code="sourcing.event.team.members" />
			</a>
			<c:if test="${eventPermissions.owner && sourcingFormRequest.status != 'DRAFT' && !readOnlyTeamMember}">
				<button class="editTeamMemberList sixbtn" data-toggle="dropdown" title="Edit">
					<img src="${pageContext.request.contextPath}/resources/images/edit-btn.png" />
				</button>
			</c:if>
		</h4>
	</div>
	<div id="collapseTeam" class="panel-collapse collapse accortab">
		<div class="Invited-Supplier-List-table add-supplier ">
			<div class="ph_tabel_wrapper">
				<div class="main_table_wrapper ph_table_border">
					<table id="sourcingTeamMembersList" class="data  display table table-bordered noarrow" cellspacing="0" width="100%">
						<thead>
							<tr class="tableHeaderWithSearch padding-set">
								<th class="for-left width_200 width_200_fix"><spring:message code="application.username" /></th>
								<th class="for-left width_200 width_200_fix"><spring:message code="buyer.profilesetup.loginmail" /></th>
								<th class="for-left width_100 width_100_fix"><spring:message code="application.access" /></th>
							</tr>
						</thead>
						<tbody>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>
</div>
<style>
div#TeamMemberList_chosen {
	background: #fff;
}

</style>

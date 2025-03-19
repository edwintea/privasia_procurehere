<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="/WEB-INF/template-functions.tld" prefix="tf"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<div class="white-bg border-all-side float-left width-100 pad_all_15">
	<div class="row">
		<div class="col-md-6 col-sm-12 col-xs-12">
			<div class="tag-line">
				<h2>PR : ${pr.name != null ? pr.name : 'N/A'}</h2>
				<br>
				<h2>ID : ${pr.prId}</h2>
			</div>
		</div>
		<div class="col-md-6 col-sm-12 col-xs-12">
			<div class="pull-right tag-line-right">
				<h2>PR Template Name : ${prTemplate.templateName != null ? prTemplate.templateName : 'N/A'}</h2>
				<br>
				<h2 style="float: right;">Status : ${pr.status}</h2>
			</div>
		</div>
		<%-- <div class="print-down">
								<label>Status : </label>${pr.status}
							</div> --%>
	</div>
</div>
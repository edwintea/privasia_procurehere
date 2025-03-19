<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<div class="alert alert-success ajax-msg" id="idGlobalSuccess"
	style="display: none">
	<div class="bg-green alert-icon">
		<i class="glyph-icon icon-check"></i>
	</div>
	<div class="alert-content">
		<h4 class="alert-title">Success</h4>
		<p id="idGlobalSuccessMessage">
			Information message box using the
			<code>.alert-success</code>
			color scheme. <a title="Link" href="#">Link</a>
		</p>
	</div>
</div>

<div class="alert alert-danger ajax-msg" id="idGlobalError" style="display: none">
	<div class="bg-red alert-icon">
		<i class="glyph-icon icon-times"></i>
	</div>
	<div class="alert-content">
		<h4 class="alert-title">Error</h4>
		<p id="idGlobalErrorMessage">
			Information message box using the
			<code>.alert-danger</code>
			color scheme. <a title="Link" href="#">Link</a>
		</p>
	</div>
</div>

<div class="alert alert-notice ajax-msg" id="idGlobalInfo" style="display: none">
	<div class="bg-blue alert-icon">
		<i class="glyph-icon icon-info"></i>
	</div>
	<div class="alert-content">
		<h4 class="alert-title">Info</h4>
		<p id="idGlobalInfoMessage">
			Information message box using the
			<code>.alert-notice</code>
			color scheme. <a title="Link" href="#">Link</a>
		</p>
	</div>
</div>

<div class="alert alert-warning ajax-msg" id="idGlobalWarn" style="display: none">
	<div class="bg-orange alert-icon">
		<i class="glyph-icon icon-exclamation"></i>
	</div>
	<div class="alert-content">
		<h4 class="alert-title">Warning</h4>
		<p id="idGlobalWarnMessage">
			Information message box using the
			<code>.alert-notice</code>
			color scheme. <a title="Link" href="#">Link</a>
		</p>
	</div>
</div>
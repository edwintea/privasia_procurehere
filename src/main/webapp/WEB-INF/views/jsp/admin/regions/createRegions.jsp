<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Create Region</title>
<meta charset="UTF-8">

<meta name="description" content="">
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">

<script type="text/javascript">
	$(window).load(function() {
		setTimeout(function() {
			$('#loading').fadeOut(400, "linear");
		}, 300);
	});
</script>

</head>
<body>




				<div id="page-content">
					<div class="container">
						<!-- pageging  block -->

						<ol class="breadcrumb">
							<li><a href="#">Dashboard</a></li>
							<li class="active">Create Region</li>
						</ol>

					
						<div class="panel">
							<div class="panel-body">
								<h3 class="title-hero">Add a Region</h3>
								<div class="example-box-wrapper">
								<c:url var="saveRegions" value="saveRegion" />
									<%-- <form class="form-horizontal bordered-row"> --%>
									<form:form id="registrationRegions" role="form" action="${saveRegions}" class="form-horizontal bordered-row"
				method="post" modelAttribute="regionsObject">
				
										<div class="form-group col-md-12" style="float:left">
										<form:label path="regionName" class="col-sm-3 control-label">Region Name</form:label>
										
										<form:hidden path="id"  name="id"/>
						
						
						
										
											<!-- <label >Input</label> -->
											<div class="col-sm-6 col-md-5">
											<form:input name="regionName" type="text" path="regionName"
							value="${regionsObject.regionName}" cssClass="form-control"
							id="idRegionName" placeholder="Enter the Region-Name" />
												
											</div>
										</div>
							<div class="form-group col-md-12" style="float:left">
							
			
										
										<form:label path="country" class="col-sm-3 control-label">Country Name</form:label>
						
											
											<div class="col-sm-6 col-md-5">
											<form:select path="country" items="${country}" cssClass="form-control" itemValue="id" itemLabel="countryName" id="id"></form:select>
											
												
											</div>
										</div> 
												<%-- 			<div class="form-group">
										<div>
											<form:label path="country" for="id">COUNTRY :</form:label>
										</div>
										<div>
											<form:select id="id" path="country" items="${country}"
												itemValue="id" itemLabel="countryName"
												cssClass="chosen-select" cssStyle="width:120px;">
											</form:select>
										</div>
									</div> --%>
										<div class="form-group  col-md-12" style="float:left">
											
											<div class="col-sm-6 col-md-12" >
											<input type="submit" value="Submit" id="saveRegion"
						class="btn bnt-block btn-primary">
											<a href="<c:url value="regionList"/>" class="btn bnt-block btn-info" style="float: right;">Regions-List</a>
											</div>
										</div>
									</form:form>
								</div>
							</div>
						</div>
					</div>
				</div>
			


</body>
</html>
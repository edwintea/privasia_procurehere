<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html lang="en">
<head>
<meta charset="UTF-8">
<!--[if IE]><meta http-equiv='X-UA-Compatible' content='IE=edge,chrome=1'><![endif]-->
<title>List-Regions</title>
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
							<li class="active">Regions-List</li>
						</ol>

						
						<div class="container-fluid">

							<div class="row">
								<div class="col_12 graph">
									<div class="white_box_brd pad_all_15">
										<section class="index_table_block">
										<h2 class="block_title">Registered Regions</h2>

										<div class="row">
											<div class="col-xs-12">
												<div class="ph_tabel_wrapper">
													<table id="tableList"
														class="ph_table display table table-bordered noarrow"
														cellspacing="0" width="100%">
														<thead>
															<tr>
																<th>Regions-Name</th>
																<th>Country-Name</th>
																<th>Edit/Delete</th>

															</tr>
														</thead>
													
													</table>
													
													<a href="<c:url value="/regionCreate"/>" class="btn btn-info">ADD NEW REGION</a>
												</div>
												<!-- <div id="morris-bar-yearly" class="graph"
													style="visibility: hidden"></div> -->
													
													
											</div>
										</div>
										</section>

									</div>
								</div>

							</div>
						</div>
					</div>
				</div>
				
 




	<script type="text/javascript">
		$("#test-select").treeMultiselect({
			enableSelectAll : true,
			sortable : true
		});
	</script>
	
<script type="text/javascript">
	$('document').ready(function() {
		var data = eval('${regionsList}');
		$('#tableList').DataTable({
			'aaData' : data,
			"aoColumns" : [
			{ 	"mData" : "regionName"},
			{	"mData" : "country.countryName"},
			{	
				"mData" : "id",
				"mRender" : function(data, type, row) {
					
					return '<a href="editRegion?id='+ row.id+ '">Edit</a>/<a  href="deleteRegion?id='+ row.id + '">Delete</a>';
					
			},
			} ]
	});
});
</script>





</body>
</html>
 
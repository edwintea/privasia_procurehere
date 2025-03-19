<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <!--[if IE]><meta http-equiv='X-UA-Compatible' content='IE=edge,chrome=1'><![endif]-->
    <title>Create Your Account </title>
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">

  
</head>
<body>
    <style type="text/css">
        html, body {
            height: 100%;
        }
        	.leftSideOfCheckbox{
		width:48%;
		float:left;
		border-right:1px solid #d8d8d8;
		margin:0 2% 0 0;
	}
	.rightSideOfCheckbox{
		width:50%;
		float:left;
	}   
    </style>

    <div id="sb-site">
        <div id="loading">
            <div class="spinner">
                <div class="bounce1"></div>
                <div class="bounce2"></div>
                <div class="bounce3"></div>
            </div>
        </div>
        <div id="page-wrapper">

            <div id="page-content-wrapper">
                <section id="admin_regSteps_wrapper">
                    <div class="container-fluid">
                        <div class="row">
                            <div class="col-xs-12">
                                <section class="admin_wizard_step">
                                    <h2 class="adm_wzard_title text-center">Companies that fill up Complete profile information stand to generate 3x more business</h2>
                                    <div class="example-box-wrapper">
                                        <div id="form-wizard-2" class="form-wizard">
                                            
											<c:url var="trackRecord" value="/supplierTrackRecord" />
                    						<form:form class="form-horizontal bordered-row" id="demo-form" data-parsley-validate=""  method="post"  modelAttribute="supplierProjects" action="${trackRecord}" >
                    						<form:hidden path="id"/>
                    						<form:hidden path="supplierId" value="${supplierId}"/>
                                            <div class="tab-content">
                                                
                                                   <div class="tab-pane active" id="step-5">
                                                        <div class="content-box">
                                                            <h3 class="content-box-header">Services: Add/Edit Track Record
                               							 <small class="sub_text">As an Administrator, you may view and edit information freely</small>
                                                            </h3>
                                                            <div class="content-box-wrapper">

                                                                <div class="row">
                                                                 <div class="form-horizontal">

                                                                    <h3 class="blue_form_sbtitle">Project Information :</h3>

                                                                    <div class="form-group">
                                                                        <label class="col-sm-3 control-label">Project Name :</label>
                                                                        <div class="col-sm-6 col-md-5">
                                                                            <form:input type="text" class="form-control" id="projectName" path="projectName" placeholder="" />
                                                                        </div>
                                                                    </div>
                                                                    <div class="form-group">
                                                                        <label class="col-sm-3 control-label">Client Name :</label>
                                                                        <div class="col-sm-6 col-md-5">
                                                                            <form:input type="text" class="form-control" id="clientName" path="clientName" placeholder="" />
                                                                        </div>
                                                                    </div>

                                                                    <div class="form-group">
                                                                        <label class="col-sm-3 control-label">Year :</label>
									                                      <div class="col-sm-6 col-md-3">
											                                 <form:select path="year"  id="idYear" cssClass="chosen-select" >
									                                        		<form:option value="">Select Year </form:option>
									                                        		<form:options items="${yearOfEstablishedList}"/>
										                                        </form:select>										
									                                     </div>
                                                                    </div>

                                                                    <div class="form-group">
                                                                        <label class="col-sm-3 control-label">Contract Value :</label>
                                                                        <div class="col-sm-6 col-md-5">
                                                                            <form:input type="tel" class="form-control" id="contactValue" path="contactValue" placeholder=""/>
                                                                        </div>
                                                                    </div>
                                                                    <div class="form-group">
                                                                        <label class="col-sm-3 control-label">Currency :</label>
									                                      <div class="col-sm-6 col-md-3">
											                                 <form:select path="currency"  id="idCurrency" cssClass="chosen-select" >
									                                        		<form:option value="">Select Currency </form:option>
									                                        		<form:options items="${currency}"/>
										                                        </form:select>										
									                                     </div>
                                                                    </div>
                                                                   

                                                                    <div class="form-group">
                                                                        <label class="col-sm-3 control-label">Client Name :</label>
                                                                        <div class="col-sm-6 col-md-5">
                                                                            <form:input type="text" class="form-control" id="clientEmail" path="clientEmail" placeholder="" />
                                                                        </div>
                                                                    </div>
                                                                    <section class="step_2_content">

                                                                    <h3 class="blue_form_sbtitle p_t20">Project's Industry :</h3>
                                                                    <div class="sub_txt_step2">Only industries selected in the Category page will appear here for selection.</div>

                                                                    <div class="row">
                                                                        <div class="col-xs-12 col-sm-12 col-md-12">
                                                                            <div class="input-group search_box_gray">
                                                                                <input type="text" class="form-control">
                                                                                <span class="input-group-btn">
                                                                                    <form:button type="button" class="btn btn-gray"></form:button>
                                                                                </span>
                                                                            </div>


                                                                           <div class="chk_scroll_box">
															<div class="scroll_box_inner industry pad-top-bottom-15 industryCatCheckboxes tree-multiselect">
																<div class="leftSideOfCheckbox">
																	<ul class="tree" id="tree">
																	 <c:forEach items="${supProjCategories}" var="sc">
																		<li>
																			<form:checkbox path="projectIndustries"  value="${sc.id}"   class="first"/><span class="number tree_heading">${sc.categoryName}</span>${sc.categoryName}
																			<c:if test="${not empty sc.children}">
																			<!-- AND SHOULD CHECK HERE -->
																				<c:forEach items="${sc.children}" var="child">
																				<ul>
																					<li>
																						<form:checkbox path="projectIndustries" value="${child.id}"   /><span class="number">${child.categoryName}</span>${child.categoryName} 
																						<!-- SHOULD CHECK HERE -->
																						<c:if test="${not empty child.children}">
																						<!-- AND SHOULD CHECK HERE -->
																						<c:forEach items="${child.children}" var="subChild">
																							<ul>
																								<li>
																									<form:checkbox path="projectIndustries" value="${subChild.id}"   /><span class="number">${subChild.categoryName}</span>
																									<c:if test="${not empty subChild.children}">
																									<!-- AND SHOULD CHECK HERE -->
																									<c:forEach items="${subChild.children}" var="subSubChild">
																										<ul>
																											<li>
																												<form:checkbox path="projectIndustries" value="${subSubChild.id}"   /><span class="number">${subSubChild.categoryName}</span>
																												<c:if test="${not empty subSubChild.children}">
																												<!-- AND SHOULD CHECK HERE -->
																												<c:forEach items="${subSubChild.children}" var="subSubSubChild">
																													<ul>
																														<li>
																															<form:checkbox path="projectIndustries" value="${subSubSubChild.id}"   /><span class="number">${subSubSubChild.categoryName}</span>
																														</li>
																													</ul>
																												</c:forEach>
																												</c:if>	
																											</li>
																										</ul>
																									</c:forEach>
																									</c:if>	
																								</li>
																							</ul>
																						</c:forEach>
																						</c:if>	
																					</li>
																				</ul>
																				</c:forEach>
																			</c:if>
																		</li>
																	</c:forEach>
																	</ul>
																</div>
																<div class="rightSideOfCheckbox">
																</div>
															</div>
														</div>
                                                                            
                                                                        </div>
                                                                        
                                                                        <div class="col-xs-12 col-sm-6 col-md-7">
                                                                            <!-- All regian Block -->
                                                                            <h3 class="blue_form_sbtitle p_t20">Project's Geographical Area :</h3>
                                                                            <div class="sub_txt_step2">Only geographical areas selected in the Category page will appear here for selection.</div>
                                                                            <div class="input-group search_box_gray">
                                                                                <input type="text" class="form-control" placeholder="Search" id="projSearch"  >
                                                                                <span class="input-group-btn">
                                                                                    <form:button class="btn btn-gray" type="button"></form:button>
                                                                                </span>
                                                                            </div>

                                                                            <div class="chk_scroll_box">
					                                                             <div class="scroll_box_inner">                     
					 																<div class="leftSideOfGeogaphicCheckbox tree-multiselect">
																						<ul class="tree" id="tree">
																						 <c:forEach items="${registeredCountry}" var="country">
																							<li>
																								<form:checkbox path=""  value="${country.id}"  class="first"/><span class="number tree_heading">${country.countryName}</span>
																									<c:if test="${not empty country.states}">
																									<!-- AND SHOULD CHECK HERE -->
																									<c:forEach items="${country.states}" var="state">
																										<ul>
																											<li>
																												<form:checkbox path="" value="${state.id}" /><span class="number">${state.stateName}</span>
																											</li>
																										</ul>
																									</c:forEach>
																									</c:if>				
																							</li>
																						</c:forEach>																		
					 																	</ul>
					 																</div>
																					<div class="rightSideOfGeogaphicCheckbox">
																					</div> 																	
					                                                             </div>
																			</div>     
														
                                                                            <div class="step_button_pan">
                                                                                <form:button type="submit" class="btn btn-info btn-lg" path="projectAdd" id="projectAdd">${not empty supplierProjects.id ? 'Edit and Back' : 'Add and Back'} </form:button>
                                                                                <form:button type="submit" class="btn btn-primary btn-lg" path="projectDelete" id="projectDelete">Delete and Back</form:button>
                                                                            </div>
                                                                        </div>                                                                     
                                                                    </div>
                                                                </section>
                                                                </div>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                            </div>
											</form:form>
                                        </div>
                                    </div>
                                </section>
                            </div>
                        </div>
                    </div>

                </section>
            </div>
        </div>


    </div>

    <!-- WIDGETS -->
    <!-- Uniform -->
    <script type="text/javascript" src="<c:url value="/resources/assets/widgets/uniform/uniform.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/resources/assets/widgets/uniform/uniform-demo.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/resources/assets/bootstrap/js/bootstrap.js"/>"></script>


    <!-- Chosen -->

    <script type="text/javascript" src="<c:url value="/resources/assets/widgets/chosen/chosen.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/resources/assets/widgets/chosen/chosen-demo.js"/>"></script>

    <!-- Bootstrap Tooltip -->
    <script type="text/javascript" src="<c:url value="/resources/assets/widgets/tooltip/tooltip.js"/>"></script>

    <!-- Perfact scroll -->
    <script type="text/javascript" src="<c:url value="/resources/assets/widgets/perfect-scrollbar/js/perfect-scrollbar.jquery.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/resources/assets/widgets/perfect-scrollbar/js/perfect-scrollbar.min.js"/>"></script>

    <!-- Content box -->
    <script type="text/javascript" src="<c:url value="/resources/assets/widgets/content-box/contentbox.js"/>"></script>

    <script type="text/javascript">
        
        
        $(document).ready(function(){
        	   $('input[type=checkbox]').click(function () {
        	   //$(this).parent().find('li input[type=checkbox]').css("font-weight","bold");
        	    $(this).parent().find('li input[type=checkbox]').prop('checked', $(this).is(':checked'));
        	    var sibs = false;
        	    $(this).closest('ul').children('li').each(function () {
        	        if($('input[type=checkbox]', this).is(':checked')) sibs=true;
        	    })
        	    $(this).parents('ul').prev().prop('checked', sibs);
        	});



        	$('.leftSideOfCheckbox').find('input[type="checkbox"]').change(function(){
        			$('.rightSideOfCheckbox').html('');
        			$('.leftSideOfCheckbox').find('input[type="checkbox"]:checked').each(function(){
        				var htmldata = '<div class="item" data-value="'+$(this).val()+'"><span class="remove-selected">×</span>'+$(this).val()+'</div>';
        				$('.rightSideOfCheckbox').append(htmldata);
        			});
        		});
        		$(document).delegate('.remove-selected','click',function(){
        			var deselVal = $(this).parent().attr('data-value');
        			$(this).parent().remove();
        			$('.leftSideOfCheckbox').find('input[type="checkbox"][value="'+deselVal+'"]').prop('checked',false);
        		});
        		
        	 });  
        	
 
        			
    </script>
</body>
</html>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
 


<div id="page-content-wrapper">
                <div id="page-content">
                    <div class="container">

                        <ol class="breadcrumb">
                            <li>
                                <a href="Buyer_Dashboard.html">Dashboard</a> 
                            </li>
                            <li class="active">Compose Mail</li>

                        </ol>
                        <!-- page title block -->


                        <div class="Section-title title_border gray-bg">
                            <h2 class="trans-cap compose_icon">Compose Mail</h2>
                        </div>


                        <div class="clear"></div>
                        <div class="col-md-12 pad0">
 
                            <div class="col-md-9 composer">

                                <div class="content-box">
                                    <div class="divider"></div>
									<form:form id="idComposeMsg"  data-parsley-validate=""  cssClass="form-horizontal bordered-row" method="post"  action="composeMessage1" modelAttribute="composeObj">                                    
                                        <div class="form-group row">
                                            <label class="col-sm-2 control-label" for="inputEmail1">To:</label>
                                            <div class="col-sm-9">
                                            <form:hidden path="id"/>
                                              <form:select path="userList"  id="idUser" cssClass="chosen-select" multiple="multiple"   data-validation="length" data-validation-length="min1">
                                        			<form:option value="">Add User  to Broadcast message</form:option>
													<form:options items="${user1}" ></form:options>                                        
                                        		</form:select>
                                            </div>
                                        </div>
                                        <div class="form-group row">
                                            <label class="col-sm-2 control-label" for="inputEmail4">Subject:</label>
                                            <div class="col-sm-9">
                                                <form:input path="subject"    placeholder="Subject" id="inputEmail4" class="form-control"/>
                                            </div>
                                        </div>

                                        <div class="button-pane">
                                            <form:button  value="Send Message" id="composeMessage" class="btn btn-info hvr-pop hvr-rectangle-out" >Send Message</form:button>
                                            <form:button class="btn btn-link font-gray-dark">Cancel</form:button>
                                        </div>
                                    </form:form>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
 <script type="text/javascript" src="<c:url value="/resources/js/jquery.form-validator.js"/>"></script>
<script>
  $.validate({
    lang: 'en'
  });
</script>


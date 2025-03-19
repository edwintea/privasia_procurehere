<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<style>
.ph_tabel_wrapper {
	float: left;
	width: 100%;
}
.Invited-Supplier-List {
	border-radius: 5px;
	float: left;
	width: 100%;
}
.import-supplier-inner-first-new {
	float: left;
	width: 100%;
}
.marg-top-10 {
	margin-top: 10px;
}
.main_table_wrapper {
    overflow-x: auto;
    overflow-y: hidden;
    position: relative;
}

.ph_table_border {
    border-radius: 2px;
}
.ph_table {
	display: table;
}

.border-none {
	border: none !important;
}
table {
  border-spacing: 0;
  border-collapse: collapse !important;
}
.table-bordered {
  border: 1px solid #ddd;
}
.table-bordered > thead > tr > th,
.table-bordered > tbody > tr > th,
.table-bordered > tfoot > tr > th,
.table-bordered > thead > tr > td,
.table-bordered > tbody > tr > td,
.table-bordered > tfoot > tr > td {
  border: 1px solid #ddd;
}
.table-bordered > thead > tr > th,
.table-bordered > thead > tr > td {
  border-bottom-width: 2px;
}

.align-left {
	text-align: left;
}

.width_50_fix {
	width: 50px;
}

.marg-bottom-20 {
    margin-bottom: 20px !important;
}

.import-supplier-inner-first label, .import-supplier-inner-first-new label {
	color: #636363;
	font-weight: normal;
	font-size: 13px;
}

.Invited-Supplier-List .import-supplier-inner-first h3 {
	color: #424242;
	border: none;
	padding: 0 0 15px;
}

.align-right {
	text-align: right;
}

.align-center {
	text-align: center;
}

.table > thead > tr > th {
    padding: 3px !important;
    background-color: #F4F6F6;
}

.summary_row {
	background-color: #EAF2F8;
	font-weight: bold;
}

.bold_text {
	font-weight: bold;
}

</style>
<body style="font-family: Verdana, Geneva, sans-serif;">
	<div  border="0" cellspacing="0" cellpadding="0" >
		<div   border="0" cellspacing="0" cellpadding="0">
           <h2><u>Score Card</u></h2>
           
           <table>
           	<tr>
           		<td><b>Supplier Performance Form ID</b></td>
           		<td style="width:20px;">&nbsp;:</td>
           		<td>${formId}</td>
           	</tr>
           	<tr>
           		<td><b>Reference Number</b></td>
           		<td>&nbsp;:</td>
           		<td>${referenceNumber}</td>
           	</tr>
           	<tr>
           		<td><b>Reference Name</b></td>
           		<td>&nbsp;:</td>
           		<td>${referenceName}</td>
           	</tr>
           	<tr>
           		<td><b>Procurement Category</b></td>
           		<td>&nbsp;:</td>
           		<td>${procurementCategory}</td>
           	</tr>
           	<tr>
           		<td><b>Business Unit</b></td>
           		<td>&nbsp;:</td>
           		<td>${unitName}</td>
           	</tr>
           	<tr>
           		<td><b>Evaluation Start Date</b></td>
           		<td>&nbsp;:</td>
           		<td>${evaluationStartDateStr}</td>
           	</tr>
           	<tr>
           		<td><b>Evaluation End Date</b></td>
           		<td>&nbsp;:</td>
           		<td>${evaluationEndDateStr}</td>
           	</tr>
           	<tr>
           		<td style="vertical-align: top;"><b>Evaluators</b></td>
           		<td style="vertical-align: top;">&nbsp;:</td>
           		<td>
           			<#list evaluator as eval>
						${eval?counter}. ${eval}
						<br/>
					</#list>
           		</td>
           	</tr>
           	<tr>
           		<td><b>Supplier</b></td>
           		<td>&nbsp;:</td>
           		<td>${supplierName}</td>
           	</tr>
           </table>
			
			<br/>
			<br/>
			<div class="Invited-Supplier-List import-supplier">
						<div>
							<h3 class="marg-top-10"><u>Total Score by Evaluator</u></h3>
						</div>
						<div class="import-supplier-inner-first-new ">
							<div class="ph_tabel_wrapper">
								<div class="main_table_wrapper ph_table_border eval_score" >
									<table class="ph_table border-none display table table-bordered " style="width:100%;" >
										<colgroup>
											<col class="width_50_fix" />
										</colgroup>													
										<thead>
											<tr>
												<th class="align-left" colspan="2" >Evaluator</th>
												<#list evaluators as evaluator>
													<th class="align-center">${evaluator}</th>
												</#list>
												<th>&nbsp;</th>
											</tr>
											<tr>
												<th class="align-left " colspan="2" >Performance Criteria</th>
												<#list evaluators as evaluator>																
													<th class="align-center " >Total Score (%)</th>
												</#list>
												<th class="align-right">Average Score</th>
											</tr>											
										</thead>
										<tbody>
											<#list scoreCardList as scoreCard>
												<#assign evalCount = 0>
												<#list scoreCard.score as score>
													<#assign evalCount++>
												</#list>
												
												<#if scoreCard.level != 1 && scoreCard.order == 0>
													<tr>
														<td class="align-right summary_row"></td>
														<td class="align-left summary_row bold_text" colspan="${evalCount + 1}">Average Total Criteria Score</td>
														<td class="align-right summary_row">${subTotal}</td>
													</tr>
												</#if>
												<tr>
													<td class="align-center ${(scoreCard.order == 0)?then("bold_text", "") }">${scoreCard.level}.${scoreCard.order}</td>
													<td class="align-left ${(scoreCard.order == 0)?then("bold_text", "") }">${scoreCard.criteria}</td>
													<#list scoreCard.score as score>
														<td class="align-center ${(scoreCard.order == 0)?then("bold_text", "") }">${(scoreCard.order != 0)?then(score, "")}</td>
													</#list>
													<td class="align-right">${(scoreCard.order != 0)?then(scoreCard.average, "")}</td>
												</tr>
												<#if scoreCard.order == 0>
													<#assign subTotal = scoreCard.average>
												</#if>
											</#list>
											<tr>
												<td class="align-right summary_row">&nbsp;</td>
												<td class="align-left summary_row bold_text" colspan="${evalCount + 1}">Average Total Criteria Score</td>
												<td class="align-right summary_row">${subTotal}</td>
											</tr>
										</tbody>
									</table>
								</div>
							</div>
						</div>
					</div>
				<div class="Invited-Supplier-List import-supplier marg-bottom-20">
						<div>
							<h3 class=" marg-top-10"><u>Consolidate Score</u></h3>
						</div>
						<div class="import-supplier-inner-first-new global-list">
							<div class="ph_tabel_wrapper">
								<div class="main_table_wrapper ph_table_border">
									<table class="ph_table border-none display table table-bordered" width="100%" border="0" cellspacing="0" cellpadding="0" style="top: 0px;">
										<colgroup>
											<col class="width_50_fix" />
										</colgroup>													
										<thead>
											<tr>
												<th class="align-left" colspan="2" >Performance Criteria</th>
												<th class="align-center">Average Total Criteria Score</th>
												<th class="align-center">Criteria Weightage (%)</th>
												<th class="align-center">Average Score with Weightage</th>
											</tr>
										</thead>
										<tbody>
											<#list conScoreList as score>
												<tr>
													<td class="align-center ">${score.level}.${score.order}</td>
													<td class="align-left bold_text">${score.criteria}</td>
													<td class="align-center">${score.averageScore}</td>
													<td class="align-center">${score.weightage}</td>
													<td class="align-center ">${score.totalScore}</td>
												</tr>
											</#list>
											<tr>
												<td class="align-left summary_row"></td>
												<td class="align-left summary_row">Overall Score</td>
												<td class="align-center summary_row"></td>
												<td class="align-center summary_row"></td>
												<td class="align-center summary_row">${overallScore}</td>
											</tr>
											<tr>
												<td class="align-left summary_row"></td>
												<td class="align-left summary_row">Rating</td>
												<td class="align-center summary_row"></td>
												<td class="align-center summary_row"></td>
												<td class="align-center summary_row">
													<#if scoreRating??>
													   ${scoreRating.rating}
													</#if>
												</td>
											</tr>
											<tr>
												<td class="align-left summary_row"></td>
												<td class="align-left summary_row">Rating Description</td>
												<td class="align-center summary_row"></td>
												<td class="align-center summary_row"></td>
												<td class="align-center summary_row">
													<#if scoreRating??>
													   ${scoreRating.description}
													</#if>
												</td>
											</tr>
										</tbody>
									</table>
								</div>
							</div>
						</div>
					</div>		
				<br/>	
				<br/>	
			<b>Remarks: </b> ${remarks}	
		</div>
	</div>
</body>
</html>

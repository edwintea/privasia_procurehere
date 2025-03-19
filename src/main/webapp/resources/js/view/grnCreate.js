$(document).ready(function() {
	
	$(document).delegate('#idSelectPo', 'change', function(e) {
		e.preventDefault();
		
		var poId=$(this).val();
		var grnId= $('#grnId').val();
		$.ajax({
			url : getBuyerContextPath('copyPoItems'),
			data : {
				grnId : grnId,
				poId : poId
			},
			type : "POST",
			beforeSend : function(xhr) {
				xhr.setRequestHeader(header, token);
				xhr.setRequestHeader("Accept", "application/json");
				xhr.setRequestHeader("Content-Type", "application/json");
				$('#loading').show();
			},
			success : function(data, textStatus, request) {
				var grnItem=renderGrid(data);
				$('#grnItemList').html(grnItem);
			},
			complete : function() {
				$('#loading').hide();
			}
		});
	});
	
	
	function renderGrid(goodsReceiptNote) {
		var grnItem='<table class=" ph_table border-none header parentBlocks" border="0" cellspacing="0" cellpadding="0" width="100%">';
			grnItem += '<thead><tr>';
			grnItem	+='<th class="width_100 width_100_fix">';
			grnItem +='<spring:message code="application.action" /></th>';
			grnItem	+='<th class="width_50 width_50_fix"><spring:message code="supplier.no.col" /></th>';
			grnItem	+='<th class="align-left width_200_fix"><spring:message code="label.rftbq.th.itemname" /></th>';
			grnItem +='<th class="align-left width_100_fix"><spring:message code="label.rftbq.th.uom" /></th>';
			grnItem	+='<th class="align-right width_100 width_100_fix"><spring:message code="label.rftbq.th.quantity" /></th>';
			grnItem +='<th class=" align-right width_150 width_150_fix"><spring:message code="label.rftbq.th.unitprice" /> ('+ goodsReceiptNote.currency +')</th>';
			grnItem	+='<th class=" align-right width_100 width_100_fix"><spring:message code="item.tax" /> </th>';
				if(goodsReceiptNote.field1Label!=''){
					grnItem +='<th class="align-left width_100 width_100_fix">' + goodsReceiptNote.field1Label + '</th>';
				}
				if(goodsReceiptNote.field2Label!=''){
					grnItem	+='<th class="align-left width_100 width_100_fix">' + goodsReceiptNote.field2Label + '</th>';
				}
				if(goodsReceiptNote.field3Label!=''){
					grnItem	+='<th class="align-left width_100 width_100_fix">' + goodsReceiptNote.field3Label + '</th>';
				}
				if(goodsReceiptNote.field4Label!=''){
					grnItem	+='<th class="align-left width_100 width_100_fix">' + goodsReceiptNote.field4Label + '</th>';
				}
				grnItem +='<th class="width_150 width_150_fix align-right"><spring:message code="prtemplate.total.amount" /> (' + goodsReceiptNote.currency + ')</th>';
				grnItem +='<th class="width_150 align-right width_150_fix"><spring:message code="prtemplate.tax.amount" /> ('+ goodsReceiptNote.currency +')</th>';
				grnItem +='<th class="width_250 width_250_fix align-right"><spring:message code="prtemplate.total.amount.tax" /> ('+ goodsReceiptNote.currency +')</th>';
				grnItem	+='</tr></thead></table>';
					
				grnItem	+='<table class="draftTabItems data ph_table border-none childBlocks" border="0" cellspacing="0" cellpadding="0" width="100%">';
				grnItem	+='<tbody>';
				
				var decimal= goodsReceiptNote.decimal;
				$.each(goodsReceiptNote.goodsReceiptNoteItems, function(i, item) {
					grnItem +='<tr id="'+ item.id +'">';
					grnItem +='<td class="width_100 width_100_fix">';
					if(item.parent.id!=null){
						grnItem +='<a href="#myModal" onClick="javascript:deleteLink(\'' + item.id +'\')" id="'+ item.id +'" role="button" data-toggle="modal"><span class="glyphicon glyphicon-remove red"></span></a></td>';
					}
					
					grnItem +='<td class="width_50 width_50_fix">'+ item.level +'.'+ item.order;
					if(item.parent.id!=null){
						grnItem +='<input type="hidden" name="itemId" id="itemId" value="'+ item.id +'"/>';
						grnItem +='<input type="hidden" name="parentId" id="parentId" value="' + item.parent.id +'"/>';
					}
					grnItem +='</td>';
					grnItem +='<td class="align-left width_200_fix">';
						if(item.order > 0){
							grnItem +='<a href="#" class="inline" data-type="text" data-title="Item Name">'+ item.itemName +'</a>';
							grnItem += '<input type="hidden" data-pos="0" name="itemName" class="validate form-control" value="'+ item.itemName +'"  data-validation="required" data-validation-length="max250"  />';
							grnItem += '<input type="hidden" data-pos="0" name="itemDescription" class="validate form-control" value="'+item.itemDescription+'" data-validation="required" data-validation-length="max250"  />';
							grnItem +='<div><a href="#" class="inlineDescription desc_text" data-type="textarea" data-title="Item Description">'+item.itemDescription+'</a></div>';
						}
							
					if(item.order == 0){
						grnItem += item.itemName;
					}
					grnItem +='</td>';
					grnItem +='<td class="align-left width_100_fix">'+ item.unit.uom +'</td>';
					grnItem +='<td class="align-right width_100 width_100_fix">';
					if(item.order > 0){
						var quantity = ReplaceNumberWithCommas(item.quantity.toFixed(decimal));
						grnItem	+='<input type="text" class="validate form-control itemValue text-right" data-pos="1"  name="quantity" data-validation="custom number required length" data-validation-regexp="^\d{1,16}(\.\d{1,'+ goodsReceiptNote.decimal+')?$" data-validation-ignore=",." value="'+ quantity +'"   data-validation-length="1-22"  />';
					}
					
					grnItem +='</td><td class=" align-right width_150 width_150_fix">';
						if(item.order > 0){
							var unitPrice = ReplaceNumberWithCommas(item.unitPrice.toFixed(decimal));
							grnItem	+='<input type="text" class="validate form-control itemValue text-right" data-pos="2"  name="unitPrice" data-validation="custom number required length" data-validation-regexp="^\d{1,16}(\.\d{1,'+ goodsReceiptNote.decimal+')?$" data-validation-ignore=",." value="'+ unitPrice +'"   data-validation-length="1-22"  />';
						}
						
					grnItem +='</td><td class=" align-right width_100 width_100_fix">';
					if(item.order > 0){
						var itemTax = ReplaceNumberWithCommas(item.itemTax.toFixed(decimal));
						grnItem	+='<input type="text" class="validate form-control itemValue text-right" data-pos="3"  name="itemTax" data-validation="custom number required length" data-validation-regexp="^\d{1,16}(\.\d{1,'+ goodsReceiptNote.decimal+')?$" data-validation-ignore=",." value="'+itemTax+'"   data-validation-length="1-22"  />';
					}
					grnItem +='</td>';
					if(goodsReceiptNote.field1Label!=''){
						grnItem +='<td class=" align-left width_100 width_100_fix">' + item.field1 + '&nbsp;</td>';
					}
					if(goodsReceiptNote.field2Label!=''){
						grnItem +='<td class="align-left width_200 approvalUsers width_200_fix">' + item.field2 + '&nbsp;</td>';
					
					}
					if(goodsReceiptNote.field3Label!=''){
						grnItem +='<td class="align-left width_200 width_200_fix">' + item.field3 + '&nbsp;</td>';
					}
					if(goodsReceiptNote.field4Label!=''){
						grnItem +='<td class="align-left width_200 width_200_fix">' + item.field4 + '&nbsp;</td>';
					}
					if(goodsReceiptNote.field5Label!=''){
						grnItem +='<td class="align-left width_200 width_200_fix">' + item.field5 + '&nbsp;</td>';
					}
					if(goodsReceiptNote.field6Label!=''){
						grnItem +='<td class="align-left width_200 width_200_fix">' + item.field6 + '&nbsp;</td>';
					}
					if(goodsReceiptNote.field7Label!=''){
						grnItem +='<td class="align-left width_200 width_200_fix">' + item.field7 + '&nbsp;</td>';
					}
					if(goodsReceiptNote.field8Label!=''){
						grnItem +='<td class="align-left width_200 width_200_fix">' + item.field8 + '&nbsp;</td>';
					}
					if(goodsReceiptNote.field9Label!=''){
						grnItem +='<td class="align-left width_200 width_200_fix">' + item.field9 + '&nbsp;</td>';
					}
					if(goodsReceiptNote.field10Label!=''){
						grnItem +='<td class="align-left width_200 width_200_fix">' + item.field1o + '&nbsp;</td>';
					}

					grnItem +='<td class="width_150 width_150_fix align-right">';
					
					if(item.order!=0){
						grnItem +='<span class="rowTotalAmount"> ' + ReplaceNumberWithCommas(item.totalAmount.toFixed(decimal)) + '</span>';
					}
					grnItem +='</td><td class="width_150 align-right width_150_fix">';
					if(item.order!=0){
						grnItem +='<span class="rowTaxAmount">' + ReplaceNumberWithCommas(item.taxAmount.toFixed(decimal)) + '</span>';
					}	
					grnItem +='</td><td class="width_250 width_250_fix align-right">';
					if(item.order!=0){
						grnItem +='<span class="rowTotalAfterTaxAmount">'+ ReplaceNumberWithCommas(item.totalAmountWithTax.toFixed(decimal)) + '</span>';
					}
					grnItem +='</td></tr>';
				});
				grnItem +='</tbody></table>';
				
				return grnItem;
	}
	
	function ReplaceNumberWithCommas(yourNumber) {
		// Seperates the components of the number
		var n = yourNumber.toString().split(".");
		// Comma-fies the first part
		n[0] = n[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
		// Combines the two sections
		return n.join(".");
	}
});


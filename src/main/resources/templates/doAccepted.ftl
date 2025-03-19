<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<#include "header.ftl">
<body style="font-family: Verdana, Geneva, sans-serif;">
	<table width="600" border="0" cellspacing="0" cellpadding="0" style="border: 1px solid #a1d0e4;">
		<#include "headerDate.ftl">
		<tr>
			<td>
				<table width="96%" border="0" cellspacing="0" cellpadding="0" style="margin: 0 2%">
					<tr>
						<td style="font-size: 14px; padding: 30px 0 30px 0; color: #525252; line-height: 21px; text-align: left;">
                            <h3>Hi ${supplierName}</h3>
							Welcome to procurehere
							<br />
							<br />
								 Buyer ${buyerCompany} has accepted the Delivery Order.
							<br />
							<b>DO Number : </b> <i>${doNumber}</i>
							<br/>
							<b>DO Date : </b> ${doDate}
							<br/>
							<b>PO Number : </b> ${poNumber}
							<br/>
							<b>Delivery Date : </b> ${deliveryDate}	
							<br/>
							<b>Comments : </b> ${comments}	
							<br/>
 							<b>Action By : </b> ${buyerName}(${buyerLoginEmail})
						</td>
					</tr>
				</table>
			</td>
		</tr>

		<tr>
			<td style="padding: 0 0 30px 0">
				<table width="96%" border="0" cellspacing="0" cellpadding="0" style="margin: 0 2%">
					<tr>
						<td style="font-size: 14px; color: #525252; line-height: 21px; text-align: left;">
							Thank You.
							<br />
							<br />
							Procurehere Team
						</td>
					</tr>
				</table>
			</td>
		</tr>
 		<#include "footer.ftl">
	</table>
</body>
</html>

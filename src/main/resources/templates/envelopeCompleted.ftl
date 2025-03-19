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
						<td style="font-size: 15px; padding: 30px 0 30px 0; color: #525252; line-height: 21px; text-align: left;">
							<h2>Hi ${userName}</h2>
							Welcome to procurehere
							<br />
							<br />
							${msg}<br />
							<b>Event Type : </b>${eventType}<br />
							<b>Reference Number :</b>${referenceNumber}<br />
							<b>Event Name :</b>${eventName}<br/>
							<b>Business Unit : </b> ${businessUnit}
							<br/>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td style="padding: 0 0 30px 0">
				<table width="96%" border="0" cellspacing="0" cellpadding="0" style="margin: 0 2%">
					<tr>
						<td style="font-size: 15px; color: #525252; line-height: 21px; text-align: left;">
							Thank You.<br /> <br /> Procurehere Team
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td style="background: #f0f0f0">
				<table width="96%" border="0" cellspacing="0" cellpadding="0" style="margin: 0 2%">
					<tr>
						<td class="help" style="text-align: left;">
							If you need assistance or have questions, please contact
							<a href="#">Procurehere Customer Service</a>
							or you can manage your
							<a href="${loginUrl}">${loginUrl}</a>
						</td>
					</tr>
				</table>
			</td>
		</tr>
			<#include "footer.ftl">
	</table>
</body>
</html>

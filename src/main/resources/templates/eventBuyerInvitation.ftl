<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<<#include "header.ftl">
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
							"${supplierList}" has been invited for following Event <br/>
				          	<b>Event Name :</b>${event.eventName}<br/>
							<b>Business Unit :</b>${businessUnit}<br/>
							<b>Event Type : </b>${eventType}<br />
							<b>Reference Number :</b>${event.referanceNumber}<br />
					        <b>Event Status :</b>${eventStatus}<br/>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td>
				<table width="96%" border="0" cellspacing="0" cellpadding="0" style="margin: 0 2%">
					<tr>
						<td style="width: 20%">&nbsp;</td>
						<td style="padding: 0 0 30px 0">
							<a class="email_bttn" href="${appUrl}">CLICK HERE TO TAKE ACTION</a>
						</td>
						<td style="width: 20%">&nbsp;</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td>
				<table width="96%" border="0" cellspacing="0" cellpadding="0" style="margin: 0 2%">
					<tr>
						<td style="padding: 0 0 30px 0; font-size: 12px; color: #525252; line-height: 21px; text-align: left;">
							If the button appears to be broken, please copy and paste this link into a web browser window:
							<a href="${appUrl}" style="word-break: break-all; font-size: 8px;">${appUrl} </a>
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
							Thank You.<br />
							<br /> Procurehere Team
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

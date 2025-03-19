<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<#include "header.ftl">
	<body style="font-family:Verdana, Geneva, sans-serif;">
		<table width="600" border="0" cellspacing="0" cellpadding="0" style="border:1px solid #a1d0e4;">
		<#include "headerDate.ftl">
		<tr>
			<td>
				<table width="96%" border="0" cellspacing="0" cellpadding="0" style="margin: 0 2%">
					<tr>
						<td style="font-size: 14px; padding: 30px 0 30px 0; color: #525252; line-height: 21px; text-align: left;">
							<h2>Hi ${contactPerson}</h2>
							Welcome to procurehere
							<br />
							<br />
							You recently requested to reset your password for your PROCUREHERE account. Click the button below to reset it.
							<br />
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
							<a class="email_bttn" href="${appLink}">Reset Your Password</a>
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
						<td style="padding: 0 0 30px 0; font-size: 14px; color: #525252; line-height: 21px; text-align: left;">
								If you did not request a password reset, please ignore this email or reply to let us know. 
								This password reset link is only valid for the next 30 minutes.
						</td>
					</tr>
				</table>
			</td>
		</tr>		
		<tr>
			<td style="background:#f0f0f0;word-break: break-all; font-size: 14px;">
				<table width="96%" border="0" cellspacing="0" cellpadding="0" style="margin:0 2%">
					<tr>
						<td class="help">
				        	If you're having trouble clicking the password reset button, copy and paste the URL below into your web browser.
							<br/>
							<a href="${appLink}" >${appLink}</a>
						</td>
					</tr>
				</table>
			</td>
		</tr>
			<#include "footer.ftl">
		</table>
	</body>
</html>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Email</title>

<style>
.email_bttn{width:100%; float:left; border:1px solid #0095d5; text-align:center; font-size:16px; color:#0095d5; text-decoration:none; padding:15px 0}
.email_bttn:hover {background:#0095d5; color:#fff;}
.footer{ text-align:center; color:#fff; font-size:12px; line-height:15px; padding:13px 0}
.help{ text-align:center; color:#595959; font-size:12px; line-height:15px; padding:8px 0; border-bottom:5px solid #fff;}
.help a {color:#0095d5; text-decoration:none}
.help a:hover {color:#ffa019; text-decoration:none}

</style>

</head>

<body style="font-family:Verdana, Geneva, sans-serif;">
<table width="600" border="0" cellspacing="0" cellpadding="0" style="border:1px solid #a1d0e4;">
  <tr>
    <td style="background:#0095d5;"><table width="96%" border="0" cellspacing="0" cellpadding="0" style="margin:0 2%">
      <tr>
        <td><table width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td style="font-size:13px; color:#fff; padding:10px 0"> <!-- <a href="#" style="color:#fff; text-decoration:none;">View in Browser</a> --> </td>
             <td style="font-size:13px; color:#fff; padding:10px 0; text-align:right">${date}</td>
          </tr>
        </table></td>
      </tr>
    
    </table></td>
  </tr>
  
  <tr>
    <td><table width="96%" border="0" cellspacing="0" cellpadding="0" style="margin:0 2%">
      <tr>
        <td style="font-size:15px; padding:30px 0 30px 0; color:#525252; line-height:21px; text-align:left;">
        <h2>Hi ${userName}</h2>
        Welcome to procurehere
</td>
      </tr>
      <tr>
      	<td>
      		Login email: ${loginId}<br/>
      		Password: ${password}
      	</td>
      </tr>
    </table></td>
  </tr>
  <tr>
    <td><table width="96%" border="0" cellspacing="0" cellpadding="0" style="margin:0 2%">
     
      <tr>
	  <td style="width:20%">&nbsp;</td>
        <td style="padding:0 0 30px 0">
        	<a class="email_bttn" href="${appUrl}">CLICK HERE TO GET STARTED</a>
        </td>
		<td style="width:20%">&nbsp;</td>
      </tr>
	  
    </table></td>
  </tr>
  <tr><td>
  <table width="96%" border="0" cellspacing="0" cellpadding="0" style="margin:0 2%">
     
      <tr>
	  <td style="padding:0 0 30px 0; font-size:12px; color:#525252; line-height:21px; text-align:left;">
	  If the button appears to be broken, please copy and paste this link into a web browser window:  <a href="${appUrl}" style="word-break:break-all; font-size:10px;"> ${appUrl}
	  </a>
	  </td>
      </tr>
	  
    </table>
	  </td>
	  </tr>
  <tr>
    <td style=" padding:0 0 30px 0"><table width="96%" border="0" cellspacing="0" cellpadding="0" style="margin:0 2%">
      <tr>
        <td style="font-size:15px;color:#525252; line-height:21px; text-align:left;">Being a registered supplier means that you stand a chance to be noticed and invited to participate in procurement events like RFPs and tenders. <br /><br />

                Please keep your registration information up-to-date from time to time.<br /><br />
                        Thank You.
							<br />
							<br />
                  Procurehere Team</td>
      </tr>
    </table></td>
  </tr>
  <tr>
    <td style="background:#f0f0f0"><table width="96%" border="0" cellspacing="0" cellpadding="0" style="margin:0 2%">
      <tr>
        <td class="help" style="text-align:left;">If you need assistance  or have questions, please contact <a href="#">Procurehere Customer Service</a>
          or you can  manage your <a href="#">account here</a></td>
      </tr>
    </table></td>
  </tr>
 	<#include "footer.ftl">
</table>
</body>
</html>

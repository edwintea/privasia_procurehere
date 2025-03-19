
<!DOCTYPE html>
<html lang="en">

<head>
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1, user-scalable=no">
  <title>SignUp Email</title>
<style type="text/css">
  .logout-text{
    text-align: center;
  }
  .email-temp{
    padding: 0;
}
.email-temp h1{
    color: #0092d7;
    font-weight: 600;
    font-size: 27px;
    font-family: Open Sans;
    line-height: 36px;
    margin-top: 2%;
    
}
.email-temp img{
   max-width: 160px;
   margin-top: 4%;
 
}
.email-temp p{
    margin: 3% auto;
    font-size: 14px;
    font-family: Open Sans;
    clear: both;
    color: #696969;
    text-align: justify;
}
.email-content{
    background: #f1f1f1;
    padding-top: 1%;
}
.email-content h4{
    color: #0092d7;
    font-family: Open Sans;
    font-weight: 600;
    margin: auto;
    margin: 0px auto;
    width: 100%;
    padding-bottom: 3%;
    font-size: 14px;
}
.email-content div{
    margin: 0px auto;
    width: 555px;
}
.headline-p{
    margin: 0px auto;
    width: 80%;
    text-align: center !important;
}
  @media only screen and (max-width: 380px) and (min-width: 320px){
.email-temp h1{
    font-size: 23px;
    padding: 10px;
}
.email-temp p{
    width: 85%;
    margin: 8% auto;
}
.email-content h4{
    width: 85%;
    padding: 5% 0;
}
.email-content div{
    width: 100%;
}
.headline-p{
    text-align: center !important;
    margin: 3% auto !important;
    width: 95%  !important;
    }
    .email-temp p br{
        display: none;
    }
    .captcha-align{
        width: auto;
    }
    .email-temp img{
        max-width: 140px;
    }
    .email-temp {
        padding: 0;
        border: 1px solid #0092d7;
        margin: 15px;
        padding-top: 10%;
    }
  }
  @media only screen and (max-width: 560px) and (min-width: 381px){
.email-temp h1{
    font-size: 23px;
    padding: 10px;
}
.email-temp p{
    width: 85%;
    margin: 8% auto;
}
.email-content h4{
    width: 85%;
    padding: 5% 0;
}
.email-content div{
    width: 100%;
}
.headline-p{
    width: 95% !important;
    text-align: center !important;
    margin: 3% auto !important;
}
.email-temp p br{
    display: none;
}
.captcha-align{
    width: auto;
}
.email-temp img{
    max-width: 140px;
}
.email-temp {
    padding: 0;
    border: 1px solid #0092d7;
    margin: 15px;
    padding-top: 8%;
}
  }
   @media only screen and (max-width: 767px) and (min-width: 561px){
    .email-temp h1{
    font-size: 23px;
    padding: 10px;
}
.email-temp p{
    width: 85%;
    margin: 8% auto;
}
.email-content h4{
    width: 85%;
    padding: 5% 0;
}
.headline-p{
    width: 95% !important;
    text-align: center !important;
    margin: 3% auto !important;
}
.email-content div{
    width: 550px;
}
.email-temp p br{
    display: none;
}
.captcha-align{
    width: auto;
}
.email-temp img{
    max-width: 140px;
}
.email-temp {
    padding: 0;
    border: 1px solid #0092d7;
    margin: 15px;
    padding-top: 4%;
}
   }
   @media only screen and (max-width: 1023px) and (min-width: 768px){
 .email-temp p{
        width: 85%;
    }
    
    .email-temp h1{
        font-size: 23px;
        padding: 10px;
    }
    .email-content h4{
        width: 85%;
        padding: 5% 0;
    }
    .headline-p{
        width: 95% !important;
        text-align: center !important;
        margin: 3% auto !important;
    }
    .email-content div{
        width: 650px;
    }
    .email-temp p br{
        display: none;
    }
    .captcha-align{
        width: auto;
    }
    .email-temp {
        padding: 0;
        border: 1px solid #0092d7;
        margin: 15px;
        padding-top: 4%;
    }
   }
   @media only screen and (max-width: 1200px) and (min-width: 1024px){
        .email-temp {
        padding: 0;
        border: 1px solid #0092d7;
        margin: 15px;
        padding-top: 4%;
    }
   }
@font-face {
  font-family: 'Open Sans';
  font-style: normal;
  font-weight: 400;
  src: local('Open Sans'), local('OpenSans'), url(https://fonts.gstatic.com/s/opensans/v13/cJZKeOuBrn4kERxqtaUH3VtXRa8TVwTICgirnJhmVJw.woff2) format('woff2');
  unicode-range: U+0000-00FF, U+0131, U+0152-0153, U+02C6, U+02DA, U+02DC, U+2000-206F, U+2074, U+20AC, U+2212, U+2215, U+E0FF, U+EFFD, U+F000;
}


</style>
</head>
<body>
  <!-- -----------------------------Header Section Starts-------------------------------- -->


<!-- ------------------------------Heade Section Ends------------------------------- -->

<div class="container-fluid email-temp">
  <div class="logout-text">
    <img src="${logoUrl}" alt="PROCUREHERE" width="140" class="img-reponsive text-center">
    <h1>One Final Step To Unlock Simplified<br>e-Procurement Opportunity</h1>
    </div>
      <p class="headline-p">Fill in your details below to  activate your account and get instant access to start now!</p>   
    <div class="email-content">
      <div>
        <p>Hi ${userName}</p>
        <p>Thanks for signing up to your free trial of Procurehere. You're just a few steps away from unlocking access to the leading simplified e-procurement solution that's saved customers over US$1.3 billion to date.</p>
        <p>To access your free trial we just need a few basic details about you and your company.</p>
        <p><a href="${appUrl}">Follow this link to activate your free trial account.</a></p>
        <p>If you have any  further questions or need support with your setup, we offer 24/7 helpdesk support through our helpline on 1-800-88-77-48 and live chat on the Procurehere homepage.</p>
        <p>Welcome to e-procurement with Procurehere,</p>
        <h4>PROCUREHERE</h4>
    </div>
  </div>
</div>





<!-- ------------------------------------Footer Section Starts----------------------------------------- -->



<!-- -------------------------------Footer Section Ends------------------------------------------------ -->



</body>
</html>

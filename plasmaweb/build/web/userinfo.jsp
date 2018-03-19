<%-- 
    Document   : frontpage
    Created on : Mar 13, 2018, 10:18:00 AM
    Author     : erik.saarenvirta
--%>

<% 
     
%>
<%@page contentType="text/html" session="true" pageEncoding="UTF-8"%>

<html>
<title>Under Construction</title>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" type="text/css" href="css/main.css">
<link rel="stylesheet" type="text/css" href="css/console.css">
<link rel="stylesheet" type="text/css" href="css/userinfo.css">
<link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Raleway">
<script type="text/javascript"> 
function toggle(theform) { 
    document.getElementById("userinfo-party-form").style.display = "none"; 
    document.getElementById("userinfo-contact-form").style.display = "none"; 
    document.getElementById("userinfo-address-form").style.display = "none";
    document.getElementById(theform).style.display = "block"; 
} 
</script> 
<body>
<div class="background-img">
  <div class="nav-bar-holder">
      <iframe class="nav-frame" src="navbar.jsp" style="border:none;"></iframe>
  </div>
  <div class="middle">
      <div class="toggle-userinfo-forms">
          <button class="send" type="button" onclick="toggle('userinfo-party-form');">person</button>
          <button class="send" type="button" onclick="toggle('userinfo-contact-form');">contact</button>
          <button class="send" type="button" onclick="toggle('userinfo-address-form');">address</button>
      </div>
      <form id="userinfo-party-form">
        <input type="text" class="userinfo-salutation" placeholder="title">
        <input type="text" class="userinfo-first" placeholder="first" required>
        <input type="text" class="userinfo-middle" placeholder="middle">
        <input type="text" class="userinfo-last" placeholder="last" required>
        <button class="submit-userinfo" type="submit">update</button>
      </form>
      <form id="userinfo-contact-form">
        <input type="text" class="userinfo-phone" placeholder="phone x ext">
        <input type="text" class="userinfo-email" placeholder="email" required>
        <input type="text" class="userinfo-other" placeholder="other">
        <button class="submit-userinfo" type="submit">update</button>
      </form>
      <form id="userinfo-address-form">
        <input type="text" class="userinfo-line1" placeholder="address line 1" required>
        <input type="text" class="userinfo-line2" placeholder="address line 2">
        <input type="text" class="userinfo-city" placeholder="city">
        <input type="text" class="userinfo-state" placeholder="state">
        <input type="text" class="userinfo-postal" placeholder="postal" required>
        <input type="text" class="userinfo-country" placeholder="country" required>
        <button class="submit-userinfo" type="submit">update</button>
      </form>
  </div>
  </div>
  <div class="bottomleft">
    <p stlye="color:white">Powered by Island Stream media</p>
  </div>    
</div>      
</body>
</html>
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
<link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Raleway">
<body>
<div class="background-img">
  <div class="topright">
      <iframe class="nav-frame" src="navbar.jsp" style="border:none;"></iframe>
  </div>
  <hr style="border-color:white;color:white;">
  <div class="middle">
      <form id="landing-msg-form">
        <h1>${welcome_msg} : ${username}</h1>
        <hr>
    </form>
  </div>
  <div class="bottomleft">
    <p stlye="color:white">Powered by Island Stream media</p>
  </div>    
</div>
</body>
</html>

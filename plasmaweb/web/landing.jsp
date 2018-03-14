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
<link rel="stylesheet" type="text/css" href="css/frontpage.css">
<link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Raleway">
<body>
<div class="background-img">
  <div class="topleft">
      <div class="register">
        <form method="post" action="Logout">
            <button class="send" type="submit">log out</button>
        </form>
    </div>
  </div>
  <div class="middle">
    <h1>Welcome: ${username}</h1>
    <hr>
  </div>
  <div class="bottomleft">
    <p stlye="color:white">Powered by Island Stream media</p>
  </div>
</div>
</body>
</html>
<%-- 
    Document   : frontpage
    Created on : Mar 13, 2018, 10:18:00 AM
    Author     : erik.saarenvirta
--%>

<% 
    String hello_world = "hello world";
%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<html>
<title>Under Construction</title>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" type="text/css" href="css/frontpage.css">
<link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Raleway">
<body>
<div class="background-img">
  <div class="topleft">
    <div class="send-email">
        <form method="post" action="createEmailRecord">
            <input type="text" name="email-to-register" placeholder="email"/>
            <button class="send" type="submit">register</button>
        </form>
    </div>
  </div>
  <div class="middle">
    <h1>Coming Soon</h1>
    <hr>
    <p id="countdown"></p>
  </div>
  <div class="bottomleft">
    <p stlye="color:white">Powered by Island Stream media : <%=hello_world%></p>
  </div>
</div>
<script type="text/javascript" src="timer.js"></script>
<script type="text/javascript" src="send-button.js"></script>
</body>
</html>

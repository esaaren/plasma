<%-- 
    Document   : frontpage
    Created on : Mar 13, 2018, 10:18:00 AM
    Author     : erik.saarenvirta
--%>

<% 
     
%>
<%@page contentType="text/html" session="false" pageEncoding="UTF-8"%>

<html>
<title>Under Construction</title>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" type="text/css" href="css/main.css">
<link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Raleway">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
<script>
    $(document).ready(function(){
        $('#mainbody').fadeIn();
    });
</script>
<body>
<div class="background-img">
  <div class="topleft">
    <div class="register">
        <form method="post" action="login.jsp">
            <button class="send" type="submit">log in / register</button>
        </form>
    </div>
  </div>
  <div class="middle" id="mainbody">
    <h1>Coming Soon</h1>
    <hr>
    <p id="countdown"></p>
  </div>
</div>
<script type="text/javascript" src="timer.js"></script>
</body>
</html>

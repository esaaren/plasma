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
<link rel="stylesheet" type="text/css" href="css/console.css">
<link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Raleway">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
<script>
    $(document).ready(function(){ 
        $('#left-drop-button').click(function() {
            $('#mainbody').toggleClass('active');
        });
        $('#search-bar').hover(function() {
            $('#mainbody').toggleClass('active');
        });
    });
</script>
<script>
    function showMenu() {
        var menu = document.getElementById("menu-dropdown");
        
        if (menu.style.display == "block"){
            menu.style.display="none";
        }
        else {
            menu.style.display="block";
            menu.style.backgroundColor="white";
        }
    }
</script>
<body>
<div class="background-img">
    <div class="header">
        <h1 class="logout"><a href="Logout">Logout</a></h1>
        <div class="navbar">
            <div class="bar-component" id = "left-bar">
               <button class="drop-button" id="left-drop-button" onclick="showMenu();">P</button>
               <div class="dropdown-content" id = "menu-dropdown">
                   <form method="post" action="userinfo.jsp" target='_parent'>
                       <button class="send" type="submit" onclick="">user info</button>
                   </form>
                   <form method="post" action="personalize.jsp" target='_parent'>
                       <button class="send" type="submit" onclick="">personalize</button>
                   </form>
                   <form method="post" action="settings.jsp" target='_parent'>
                       <button class="send" type="submit" onclick="">settings</button>
                   </form>
               </div>
           </div>  
            <div class="bar-component" id="search-bar">
                  <input type="search" class="searchbar" placeholder="Thoughts?">        
            </div>
        </div>
    </div>
    <div class="middle" id="mainbody">
        <div class="dashboard-container">
            <iframe class="dashboard-frame" frameborder="0" src="dashboard.jsp" style="border:none;"></iframe>
        </div>
    </div>
</div>
</body>
</html>

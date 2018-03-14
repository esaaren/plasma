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
<link rel="stylesheet" type="text/css" href="css/landing.css">
<link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Raleway">
<body>
<div class="background-img">
  <div class="topright">
      <div class="navbar">
        <div class="dropdown">
            <button class="drop-button">Data
                <i class="fall-down"></i>
            </button>
            <div class="dropdown-content">
                <form method="post" action="">
                    <button class="send" type="button" onclick="toggle('manage-form');">manage</button>
                </form>
                <form method="post" action="">
                    <button class="send" type="button" onclick="toggle('create-form');">create</button>
                </form>
                <form method="post" action="">
                    <button class="send" type="button" onclick="toggle('upload-form');">upload</button>
                </form>
                <form method="post" action="">
                    <button class="send" type="button" onclick="toggle('run-form');">run</button>
                </form>
            </div>
        </div> 
        <div class="dropdown">
            <button class="drop-button">Home
                <i class="fall-down"></i>
            </button>
            <div class="dropdown-content">
                <form>
                    <button class="send" type="button" onclick="toggle('user-form');">user info</button>
                </form>
                <form>
                    <button class="send" type="button" onclick="toggle('settings-form');">settings</button>
                </form>
                <form method="post" action="Logout">
                    <button class="send" type="submit">logout</button>
                </form>
            </div>
        </div> 
    </div>
  </div>
  <div class="middle">
    <form id="landing-msg-form">
        <h1>${welcome_msg} : ${username}</h1>
        <hr>
    </form>
    <form id='manage-form' action="ManageHandler" method='post'>
        <input type="text" name="manager" placeholder="manager?" required>
    </form>
    <form id='create-form' action="CreateHandler" method='post'>
        <input type="text" name="create" placeholder="create?" required>
    </form>
    <form id='upload-form' action="UploadHandler" method='post'>
        <input type="text" name="upload" placeholder="upload?" required>
    </form>
    <form id='run-form' action="RunHandler" method='post'>
        <input type="text" name="run" placeholder="run?" required>
    </form>
    <form id='user-form' action="UserInfoHandler" method='post'>
        <input type="text" name="user-first" placeholder="First" required>
    </form>
    <form id='settings-form' action="SettingHandler" method='post'>
        <input type="text" name="setting-subscription" placeholder="Subscription?" required>
    </form>
  </div>
  <div class="bottomleft">
    <p stlye="color:white">Powered by Island Stream media</p>
  </div>
</div>
        
<script type="text/javascript"> 
function toggle(theform) { 
    document.getElementById("landing-msg-form").style.display = "none"; 
    document.getElementById("manage-form").style.display = "none";
    document.getElementById("create-form").style.display = "none"; 
    document.getElementById("upload-form").style.display = "none";
    document.getElementById("run-form").style.display = "none"; 
    document.getElementById("user-form").style.display = "none";
    document.getElementById("settings-form").style.display = "none"; 
    document.getElementById(theform).style.display = "block"; 
} 
</script> 
</body>
</html>
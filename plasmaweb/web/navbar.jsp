<html>
<title>Under Construction</title>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" type="text/css" href="css/main.css">
<link rel="stylesheet" type="text/css" href="css/navbar.css">
<link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Raleway">
<body>
    <div class="topbar">
        <form method="post" action="Logout" target='_parent'>
                    <button class="send" type="submit">logout</button>
        </form>
        <hr>
    </div>
      <div class="navbar">
        <div class="dropdown" id = "data-drop">
            <button class="drop-button">Data
                <i class="fall-down"></i>
            </button>
            <div class="dropdown-content">
                <form method="post" action="">
                    <button class="send" type="button" onclick="">manage</button>
                </form>
                <form method="post" action="">
                    <button class="send" type="button" onclick="">create</button>
                </form>
                <form method="post" action="">
                    <button class="send" type="button" onclick="">upload</button>
                </form>
                <form method="post" action="">
                    <button class="send" type="button" onclick="">run</button>
                </form>
            </div>
        </div> 
        <div class="dropdown" id ="home-drop">
            <button class="drop-button">Home
                <i class="fall-down"></i>
            </button>
            <div class="dropdown-content">
                <form method="post" action="userinfo.jsp" target='_parent'>
                    <button class="send" type="button" onclick="">user info</button>
                </form>
                <form method="post" action="settings.jsp" target='_parent'>
                    <button class="send" type="button" onclick="">settings</button>
                </form>
            </div>
        </div>
        <hr>
    </div>
    
</body>
</html>

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
              <button class="logout" type="submit">logout</button>
        </form>
    </div>
      <div class="navbar">
         <div class="bar-component" id = "left-bar">
            <button class="drop-button">Plasma Applications
                <i class="fall-down"></i>
            </button>
            <div class="dropdown-content">
                <form method="post" action="">
                    <button class="send" type="button" onclick="" >Career</button>
                </form>
                <form method="post" action="">
                    <button class="send" type="button" onclick="">Travel</button>
                </form>
                <form method="post" action="">
                    <button class="send" type="button" onclick="">Music</button>
                </form>
                <form method="post" action="">
                    <button class="send" type="button" onclick="">Security</button>
                </form>
            </div>
        </div>  
        <div class="bar-component" id = "data-drop">
            <button class="drop-button">Data
                <i class="fall-down"></i>
            </button>
            <div class="dropdown-content">
                <form method="post" action="">
                    <button class="send" type="button" onclick="">create</button>
                </form>
                <form method="post" action="">
                    <button class="send" type="button" onclick="">input</button>
                </form>
                <form method="post" action="">
                    <button class="send" type="button" onclick="">feedback</button>
                </form>
                <form method="post" action="">
                    <button class="send" type="button" onclick="">upload</button>
                </form>
                <form method="post" action="">
                    <button class="send" type="button" onclick="">download</button>
                </form>
            </div>
        </div>
        <div class="bar-component" id="search-bar">
              <input type="search" class="textbox" placeholder="Search">        
        </div>
        <div class="bar-component" id ="notification-drop">
            <button class="drop-button">Alerts
                <i class="fall-down"></i>
            </button>
            <div class="dropdown-content">
                <form method="post" action="" target='_parent'>
                    <button class="send" type="button" onclick="">main</button>
                </form>
                <form method="post" action="" target='_parent'>
                    <button class="send" type="button" onclick="">notifications</button>
                </form>
            </div>
        </div>
        <div class="bar-component" id ="profile-drop">
            <button class="drop-button">Profile
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
    </div>
</body>
</html>

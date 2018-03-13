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
<link rel="stylesheet" type="text/css" href="css/login.css">
<link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Raleway">
<body>
<div class="background-img">
  <div class="topleft">
      <div class="send-email">
        <form method="post" action="index.jsp">
            <button class="send" type="submit">main</button>
        </form>
      </div>
  </div>
  <div class="middle">
   <h1>Login</h1>
   <input type='checkbox' id='form-switch'>
    <form id='login-form' action="" method='post'>
        <input type="text" name="username-login" placeholder="Username" required>
        <input type="password" name="password-login" placeholder="Password" required>
        <button class="login-button" type="submit">Login</button>
        <label for='form-switch'><span>Register</span></label>
    </form>
    <form id='register-form' action="CreateUser" method='post'>
        <input type="text" name = "username-register" placeholder="Username" required>
        <input type="email" name = "email-register" placeholder="Email" required>
        <input type="password" id = "password1" name = "password-register" placeholder="Password" required>
        <input type="password" id = "password2" name = "password-register2" oninput="checkpass(this)" placeholder="Re Password" required>
        <button class="login-button" type='submit'>Register</button>
        <label for='form-switch'>Already Registered?</label>
        </form>
  </div>
  <div class="bottomleft">
    <p stlye="color:white">Powered by Island Stream media</p>
  </div>
</div>
<script language='javascript' type='text/javascript'>
                function checkpass(input) {
                    if (input.value != document.getElementById('password1').value) {
                        input.setCustomValidity('Password Must be Matching.');
                    } else {
                    // input is valid -- reset the error message
                    input.setCustomValidity('');
                    }
                }
</script>
</body>
</html>

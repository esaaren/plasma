/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package plasma.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpSession;

/**
 *
 * @author erik.saarenvirta
 */
public class Login extends HttpServlet {

   
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
       
      HttpSession session = request.getSession();
        
      // Set response type
      response.setContentType("text/html");
      
      // Set connection vars
      String url = "jdbc:postgresql://35.203.11.125/postgres";
      String user = "postgres";
      String password = "admin";
      
      // Check user sql
      String check_user = "select username,password,salt from users where username = ?";
      
      
      // Session sql
      
      
      // Get the register data
      String username = request.getParameter("username-login").trim();
      String user_password = request.getParameter("password-login").trim();
      
      
      // Validate if username already exists
      
      Connection conn = null;
      String user_exists = null;
      String salt_from_db = null;
      String password_from_db = null;
      
      
      try {
          //Get connection
          try {
              Class.forName("org.postgresql.Driver");
          } catch (ClassNotFoundException ex) {
              Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
          }
          conn = DriverManager.getConnection(url, user, password);
          conn.setAutoCommit(true);
          //Prepare the statement
          PreparedStatement pst = conn.prepareStatement(check_user);
          pst.setString(1,username);
          ResultSet rs = pst.executeQuery(); 
          
          if (!rs.next() ) {
             user_exists = "false";
          }
          else {
             user_exists = "true";
             password_from_db = rs.getString(2);
             salt_from_db = rs.getString(3);

          }
          
          pst.close();
          
      }catch(SQLException e) {
          System.out.print(e);
      }finally {
        // Always close the database connection.
         try {
            if (conn != null) conn.close();
         } catch (SQLException e){
                System.out.println(e);
         }
      }
     
      if (user_exists.equals("true")) { 
           
           Password hashUserEnteredPass = new Password(user_password,salt_from_db);
           String password_entered = hashUserEnteredPass.getHashedPassword();
           
           if (password_entered.equals(password_from_db)) {
               
                session.setAttribute("subscription","Subscribed");
                session.setAttribute("login_page_msg", "");
                session.setAttribute("welcome_msg", "Welcome back");
                session.setAttribute("username",username);
                response.sendRedirect("/plasmaweb/console.jsp");
               
           }
           else {
               session.setAttribute("login_page_msg", "Incorrect password");
               response.sendRedirect("/plasmaweb/login.jsp");
           }
           return;
             
      }
      else {
          session.setAttribute("login_page_msg", "Username does not exist");
          response.sendRedirect("/plasmaweb/login.jsp");
          return;
      }
      

      
    }


}

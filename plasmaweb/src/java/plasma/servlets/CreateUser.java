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
public class CreateUser extends HttpServlet {

    
    public void init() throws ServletException {
     
   }
   
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
      HttpSession session = request.getSession();
        
      // Set response type
      response.setContentType("text/html");
      
      
      // Create user sql
      String insert_user = "insert into users(username, email, password, state,subscribed, salt) values (?,?,?,?,?,?)";
      
      // Check user sql
      String check_user = "select username from users where username = ?";
      
      
      // Session sql
      
      
      // Get the register data
      String username = request.getParameter("username-register").trim();
      String user_email = request.getParameter("email-register").trim();
      String user_password = request.getParameter("password-register").trim();
      
      
      // Validate if username already exists
      
      Connection conn = ConnectionManager.getConnection();
      String user_exists = null;
      
      
      try {
          
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
          }
          pst.close();
          
      }catch(SQLException ex) {
          Logger.getLogger(CreateUser.class.getName()).log(Level.SEVERE, null, ex); 
      }finally {
        // Always close the database connection.
         try {
            if (conn != null) conn.close();
         } catch (SQLException ex){
                Logger.getLogger(CreateUser.class.getName()).log(Level.SEVERE, null, ex); 
         }
      }
      
      if (user_exists.equals("true")) { 
           session.setAttribute("login_page_msg", "*Username already taken");
           response.sendRedirect("/plasmaweb/login.jsp");
           return;
             
      }
      
      
      
      // Create a hash for the password with salt length 10 
      
      Password pass = new Password(user_password,10);
       
      // Get the salt and the password
      String salt = pass.getSalt();
      String secure_password = pass.getHashedPassword();
      
      // Initialize connection 
     conn = ConnectionManager.getConnection();
      
      
      // Execute Insert 
      try {
          conn.setAutoCommit(true);
          //Prepare the statement
          PreparedStatement pst = conn.prepareStatement(insert_user);
          pst.setString(1,username);
          pst.setString(2,user_email);
          pst.setString(3,secure_password);
          pst.setInt(4,1);
          pst.setBoolean(5,true);
          pst.setString(6,salt);
          
          //Check if inserted or not
          int numRowsChanged = pst.executeUpdate();
          if(numRowsChanged!=0){
           }
          else{
            Logger.getLogger(CreateUser.class.getName()).log(Level.SEVERE, null, "Failed to insert a new user"); 
          }
          pst.close();
        
      }catch(SQLException ex) {
          Logger.getLogger(CreateUser.class.getName()).log(Level.SEVERE, null, ex); 
      }finally {
        // Always close the database connection.
         try {
            if (conn != null) conn.close();
         } catch (SQLException ex){
                Logger.getLogger(CreateUser.class.getName()).log(Level.SEVERE, null, ex); 
         }
      }
      // Set session vars
      session.setAttribute("subscription","Subscribed");
      session.setAttribute("login_page_msg", "");
      session.setAttribute("welcome_msg", "Welcome");
      session.setAttribute("username",username);
      response.sendRedirect("/plasmaweb/console.jsp");

      
    }


}

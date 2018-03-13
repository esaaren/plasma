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


/**
 *
 * @author erik.saarenvirta
 */
public class createEmailRecord extends HttpServlet {

    public void init() throws ServletException {
     
   }
   
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
      // Set response type
      response.setContentType("text/html");
      
      // Set connection vars
      String url = "jdbc:postgresql://35.203.11.125/postgres";
      String user = "postgres";
      String password = "admin";
      
      // Sql query
      String sql = "insert into email_registry(emailval) values (?)";
      
      // Get the email   
      String user_email = request.getParameter("email-to-register");
      
      // Initialize connection var
      Connection conn = null;
      
      
      // Execute Insert 
      try {
          //Get connection
          try {
              Class.forName("org.postgresql.Driver");
          } catch (ClassNotFoundException ex) {
              Logger.getLogger(createEmailRecord.class.getName()).log(Level.SEVERE, null, ex);
          }
          conn = DriverManager.getConnection(url, user, password);
          conn.setAutoCommit(true);
          //Prepare the statement
          PreparedStatement pst = conn.prepareStatement(sql);
          pst.setString(1,user_email);
          
          //Check if inserted or not
          int numRowsChanged = pst.executeUpdate();
          if(numRowsChanged!=0){
            System.out.println("<br>Record inserted");
           }
          else{
            System.out.println("Failed to insert the data");
          }
          pst.close();
        
      }catch(SQLException e) {
          System.out.print("<h1>" + e + "</h1>");
      }finally {
        // Always close the database connection.
         try {
            if (conn != null) conn.close();
         } catch (SQLException e){
                System.out.println("<h1>" + e + "</h1>");
         }
      }
      
      PrintWriter out = response.getWriter();
      out.println("<h1>" + user_email + "</h1>");
      
    }


}

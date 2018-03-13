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


/**
 *
 * @author erik.saarenvirta
 */
public class createEmailRecord extends HttpServlet {

    public void init() throws ServletException {
     
       
      
   }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
      // Set response type
      response.setContentType("text/html");
      
      // Set connection vars
      String url = "jdbc:postgresql://35.203.11.125/email_registry";
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
          conn = DriverManager.getConnection(url, user, password);
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
          System.out.print(e);
      }finally {
        // Always close the database connection.
         try {
            if (conn != null) conn.close();
         } catch (SQLException e){
                System.out.println(e);
         }
      }
          
      //PrintWriter out = response.getWriter();
      //out.println("<h1>" + user_email + "</h1>");
    }


}

package plasma.servlets;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionManager {
    private final static String url = "jdbc:postgresql://35.203.11.125/postgres";    
    private final static String driverName = "org.postgresql.Driver";   
    private final static String username = "postgres";   
    private final static String password = "admin";
    private static Connection con;

    public static Connection getConnection() {
        try {
            Class.forName(driverName);
            try {
                con = DriverManager.getConnection(url, username, password);
            } catch (SQLException ex) {
                Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex); 
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);  
        }
        return con;
    }
}
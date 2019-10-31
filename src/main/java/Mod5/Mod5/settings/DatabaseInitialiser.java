package Mod5.Mod5.settings;


import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseInitialiser implements ServletContextListener {
    private static Connection con;


    public static Connection getCon() {
        return con;
    }

    public void contextInitialized(ServletContextEvent arg0) {
        System.out.println("Connecting to databse...");
        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("Driver found.");
        } catch (ClassNotFoundException cnfe) {
            System.err.println("Error loading driver: " + cnfe);
        }

        String host = "localhost";
        String dbName = "postgres";
        String myschema = "mod5";
        String url = "jdbc:postgresql://" + host + ":5432/" + dbName + "?currentSchema=" + myschema;
        String user = "ngocapu";
        String password = "";


//        String host = "farm02.ewi.utwente.nl";
//        String dbName = "docker";
//        String url = "jdbc:postgresql://" + host + ":7005/" + dbName;
//        String user = "docker";
//        String password = "1Pu7WY99OW";

        try {
            con = DriverManager.getConnection(url, user, password);
            System.out.println("Connection established.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void contextDestroyed(ServletContextEvent arg0) {
        try {
            con.close();
            System.out.println("Server disconnected");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
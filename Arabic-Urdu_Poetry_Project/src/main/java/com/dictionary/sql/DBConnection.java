package com.dictionary.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * This class is used to create a connection with SQLite database.
 */
public class DBConnection {
    
    // We use a new filename 'dictionary_v3.db' to ensure a fresh start
    private static final String URL = "jdbc:sqlite:dictionary_v3.db";    
    
    // SQLite does not require User/Password, so we don't need these constants anymore.
    // private static final String USER="root";
    // private static final String PASSWORD ="";

    /**
     * Static block runs only once when the class is first loaded.
     * It makes sure that the SQLite JDBC driver is available.
     */
    static {
        try {
            // Changed driver from com.mysql.cj.jdbc.Driver to org.sqlite.JDBC
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found - check pom.xml dependencies.");
            e.printStackTrace();
        }
    }

    /**
     * @return connection object which is linked to SQLite object.
     * @throws SQLException as well if an error occurs while they're connecting.
     */
    public static Connection getConnection() throws SQLException {
        // SQLite connections usually just need the URL
        return DriverManager.getConnection(URL);
    }
}

//package com.dictionary.sql;
//
//import java.io.InputStream;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.util.Properties;
//
//public class DBConnection {
//    
//    private static Properties props = new Properties();
//
//    static {
//        try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
//            if (input == null) {
//                System.out.println("Sorry, unable to find db.properties");
//            }
//            props.load(input);
//            // Load driver dynamically
//            Class.forName(props.getProperty("db.driver"));
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
//
//    public static Connection getConnection() {
//        try {
//            return DriverManager.getConnection(
//                props.getProperty("db.url"),
//                props.getProperty("db.user"),
//                props.getProperty("db.password")
//            );
//        } catch (Exception e) {
//            System.err.println("Connection Failed! Check output console");
//            e.printStackTrace();
//            return null;
//        }
//    }
//}
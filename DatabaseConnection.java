package com.chudasama.sufeeds;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection
{
  private static final String URL = "jdbc:mysql://localhost:3306/database_name";
  private static final String USER = "your_username_here";  
  private static final String PASSWORD = "your_password_here";  

  public static Connection getConnection()
    {
        try
        {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}

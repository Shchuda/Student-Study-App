package com.chudasama.sufeeds;

import java.sql.Connection;

public class DatabaseTest
{
    public static void main(String[] args)
    {
        Connection connection = DatabaseConnection.getConnection();
        if (connection != null)
        {
            System.out.println("Connection successful!");
        }
        else
        {
            System.out.println("Failed to connect to the database.");
        }
    }
}

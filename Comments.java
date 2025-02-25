package com.chudasama.sufeeds;

// This class represents and manages user comments
public class Comments
{
    private String username; // Username of the commenter
    private String content; // The comment text
    private String date; // Timestamp of when the comment was made

    // Constructor to initialize a new comment
    public Comments(String username, String content, String date)
    {
        this.username = username;
        this.content = content;
        this.date = date; // Sets the date to the current date and time
    }

    // Getter for the username
    public String getUsername()
    {
        return username;
    }

    // Getter for the comment content
    public String getContent()
    {
        return content;
    }

    // Getter for the formatted date
    public String getDate()
    {
        return date; // Formats and returns the date string
    }

    @Override
    public String toString()
    {
        // Formats the comment for display with date, username, and content
        return "[" + getDate() + "] " + username + ": " + content; // Format for display
    }
}

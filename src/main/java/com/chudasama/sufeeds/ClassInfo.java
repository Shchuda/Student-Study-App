package com.chudasama.sufeeds;

// This class represents and manages topics added for each class by a user
public class ClassInfo
{
    private String week; // Week number of the class
    private String className;  // Name of the class
    private String topicsLearnt; // Topics covered in the class

    // Default constructor
    public ClassInfo()
    {
        this.week = "0"; // Initializes week to 0
        this.className = ""; // Initializes class name to an empty string
        this.topicsLearnt = "";  // Initializes topics learnt to an empty string
    }

    // Constructor with parameters to initialize class details
    public ClassInfo(String week, String className, String topicsLearnt)
    {
        this.week = week;
        this.className = className;
        this.topicsLearnt = topicsLearnt;
    }

    // Setter for the week number
    public void setWeek(String week)
    {
        this.week = week;
    }

    // Setter for the class name
    public void setClassName(String className)
    {
        this.className = className;
    }

    // Setter for the topics learnt
    public void setTopicsLearnt(String topicsLearnt)
    {
        this.topicsLearnt = topicsLearnt;
    }

    // Getter for the week number
    public String getWeek()
    {
        return week;
    }

    // Getter for the class name
    public String getClassName()
    {
        return className;
    }

    // Getter for the topics learnt
    public String getTopicsLearnt()
    {
        return topicsLearnt;
    }
}

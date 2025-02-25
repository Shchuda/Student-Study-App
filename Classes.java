package com.chudasama.sufeeds;

// This class represents and manages classes added by a user
public class Classes
{
    // string that holds the list of classes
    private String classesList;

    // Constructor to initialize classesList with a given value
    public Classes(String classesList)
    {
        this.classesList = classesList;
    }

    // Default constructor that initializes classesList as an empty string
    public Classes()
    {
        this.classesList = "";
    }

    // Setter method for classesList
    public void setClassesList(String classesList)
    {
        this.classesList = classesList;
    }

    // Getter method for classesList
    public String getClassesList()
    {
        return classesList;
    }
}

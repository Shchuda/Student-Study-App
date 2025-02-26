package com.chudasama.sufeeds;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

// Classes needed for database connectivity
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Register
{
    Stage window;
    TextField studentidInput;
    TextField firstnameInput;
    TextField lastnameInput;
    PasswordField passInput;

    // Constructor to set up the register window
    public Register(Stage primaryStage)
    {
        window = primaryStage;
        window.setTitle("Register");

        window.setWidth(500);
        window.setHeight(490);

        // StackPane to center the register frame
        StackPane root = new StackPane();
        root.setPadding(new Insets(20));

        // VBox for the register form elements
        VBox registerFrame = new VBox(10); // 10 pixels space between elements
        registerFrame.getStyleClass().add("register-frame");

        // title label
        Label titleLabel = new Label("Register");
        titleLabel.getStyleClass().add("title-label");

        // copyright label
        Label copyrightLabel = new Label("© Sheel Chudasama");
        copyrightLabel.getStyleClass().add("copyright-label");

        // first name input
        firstnameInput = new TextField();
        firstnameInput.setPromptText("First Name");
        firstnameInput.getStyleClass().add("firstname-text-field");

        // last name input
        lastnameInput = new TextField();
        lastnameInput.setPromptText("Last Name");
        lastnameInput.getStyleClass().add("lastname-text-field");

        // student id input
        studentidInput = new TextField();
        studentidInput.setPromptText("Student ID");
        studentidInput.getStyleClass().add("studentid-text-field");

        // password input
        passInput = new PasswordField();
        passInput.setPromptText("Password");
        passInput.getStyleClass().add("password-field");

        // register button
        Button registerButton = new Button("Register");
        registerButton.getStyleClass().add("register-button");
        HBox.setMargin(registerButton, new Insets(10, 0, 0, 0)); // adds space above the button
        registerButton.setOnAction(e -> registerButtonClicked());

        // hbox for the register button
        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(registerButton);
        buttonBox.setAlignment(Pos.CENTER);

        // adds all elements to the register frame
        registerFrame.getChildren().addAll(titleLabel, copyrightLabel, firstnameInput, lastnameInput, studentidInput, passInput, buttonBox);

        // adds the register frame to the main layout (stackpane)
        root.getChildren().add(registerFrame);

        Scene scene = new Scene(root, 400, 300);
        scene.getStylesheets().add("Register.css");

        window.setScene(scene);
        window.show();
    }

    public void registerButtonClicked()
    {
        String username = studentidInput.getText().trim();
        String firstname = firstnameInput.getText().trim();
        String lastname = lastnameInput.getText().trim();
        String password = passInput.getText().trim();

        StringBuilder errorMessage = new StringBuilder();

        // Checks if any fields are empty
        if (username.isEmpty() || firstname.isEmpty() || lastname.isEmpty() || password.isEmpty())
        {
            if (username.isEmpty())
            {
                errorMessage.append("Please fill in your Student ID\n");
            }
            if (firstname.isEmpty())
            {
                errorMessage.append("Please fill in your first name\n");
            }
            if (lastname.isEmpty())
            {
                errorMessage.append("Please fill in your last name\n");
            }
            if (password.isEmpty())
            {
                errorMessage.append("Please fill in your Password\n");
            }

            showErrorPopup(errorMessage.toString());
            return;
        }

        // Validate student ID length and format
        if (username.length() != 6)
        {
            errorMessage.append("Invalid input: Student ID must be exactly 6 digits\n");
        }
        try
        {
            Integer.parseInt(username);
        }
        catch (NumberFormatException e) {
            errorMessage.append("Invalid input: Student ID must contain only numeric characters\n");
        }

        // Checks first name and last name for only letters
        if (!firstname.matches("[a-zA-Z]+"))
        {
            errorMessage.append("Invalid input: First name must contain only letters\n");
        }
        if (!lastname.matches("[a-zA-Z]+"))
        {
            errorMessage.append("Invalid input: Last name must contain only letters\n");
        }

        if (errorMessage.length() > 0)
        {
            showErrorPopup(errorMessage.toString());
            return;
        }

        // Checks if the student ID already exists
        if (checkIfUserExists(username))
        {
            showErrorPopup("Student ID already exists. Please enter a different student ID");
            return;
        }

        // Database insertion
        try (Connection conn = DatabaseConnection.getConnection())
        {
            String sql = "INSERT INTO tbl_login (student_id, first_name, last_name, password) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, firstname);
            pstmt.setString(3, lastname);
            pstmt.setString(4, password);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0)
            {
                successfulPopup("Registration successful!");

                Stage dashboardStage = new Stage();
                new Dashboard(dashboardStage, firstname, lastname, username);
                window.close();

                firstnameInput.clear();
                lastnameInput.clear();
                studentidInput.clear();
                passInput.clear();
            }
            else
            {
                showErrorPopup("Registration failed. Please try again.");
            }
        }
        catch (SQLException e)
        {
            System.out.println(e);
            showErrorPopup("An error occurred while connecting to the database: " + e.getMessage());
        }
    }

    // Checks if the user already exists
    private boolean checkIfUserExists(String studentId)
    {
        try (Connection conn = DatabaseConnection.getConnection())
        {
            String query = "SELECT * FROM tbl_login WHERE student_id = ?";  // SQL query to find a user with the given student ID
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, studentId);
            ResultSet resultSet = pstmt.executeQuery(); // Executes the query and gets the results

            return resultSet.next(); // Returns true if a matching record is found
        }
        catch (SQLException e)
        {
            showErrorPopup("An error occurred while checking for existing users: " + e.getMessage());
            return false; // Returns false if there was a problem with the database
        }
    }

    // Method to show an error message in a popup window
    public void showErrorPopup(String message)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Method to show a registration successful message in a popup window
    public void successfulPopup(String message)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registration successful");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}


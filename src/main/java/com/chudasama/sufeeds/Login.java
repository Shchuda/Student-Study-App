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

public class Login
{
    Stage window;
    TextField studentidInput;
    PasswordField passInput;
    private String studentId;

    // Constructor to set up the login window
    public Login(Stage primaryStage)
    {
        window = primaryStage;
        window.setTitle("Login");

        // size for the window
        window.setWidth(500);
        window.setHeight(380);

        // StackPane to center the login frame
        StackPane root = new StackPane();
        root.setPadding(new Insets(20));

        // VBox for the login form elements
        VBox loginFrame = new VBox(10); // 10 pixels space between elements
        loginFrame.getStyleClass().add("login-frame");

        // title label
        Label titleLabel = new Label("Welcome to SU Feeds");
        titleLabel.getStyleClass().add("title-label");

        // copyright label
        Label copyrightLabel = new Label("© Sheel Chudasama");
        copyrightLabel.getStyleClass().add("copyright-label");

        // student id input
        studentidInput = new TextField();
        studentidInput.setPromptText("Student ID");
        studentidInput.getStyleClass().add("studentid-text-field");

        // password input
        passInput = new PasswordField();
        passInput.setPromptText("Password");
        passInput.getStyleClass().add("password-field");

        // login button
        Button loginButton = new Button("Login");
        loginButton.getStyleClass().add("login-button");
        HBox.setMargin(loginButton, new Insets(10, 0, 0, 0)); // adds space above the button
        loginButton.setOnAction(e -> loginButtonClicked());

        // register button
        Button registerButton = new Button("Not a member? Register");
        registerButton.getStyleClass().add("register-button");
        HBox.setMargin(registerButton, new Insets(10, 0, 0, 0)); // adds space above the button
        registerButton.setOnAction(e -> openRegisterWindow());

        // hbox for login and register buttons
        VBox buttonBox = new VBox(10);
        buttonBox.getChildren().addAll(loginButton, registerButton);
        buttonBox.setAlignment(Pos.CENTER);

        // adds all elements to the login frame
        loginFrame.getChildren().addAll(titleLabel, copyrightLabel, studentidInput, passInput, buttonBox);

        // adds the login frame to the main layout (stackpane)
        root.getChildren().add(loginFrame);

        Scene scene = new Scene(root, 400, 300);
        scene.getStylesheets().add("Login.css");

        window.setScene(scene);
        window.show();
    }

    public void loginButtonClicked()
    {
        String username = studentidInput.getText(); // Gets the text from the username input field
        String password = passInput.getText(); // Gets the password from the password input field

        // Checks if either the username or password fields are empty
        if (username.isEmpty() && password.isEmpty())
        {
            showErrorPopup("Please fill in both fields: Student ID and Password");
            return;
        }
        else if (username.isEmpty())
        {
            showErrorPopup("Please fill in your Student ID");
            return;
        }
        else if (password.isEmpty())
        {
            showErrorPopup("Please fill in your Password");
            return;
        }

        // Checks if the username is not exactly 6 digits long
        if (username.length() != 6)
        {
            showErrorPopup("Invalid input: Student ID must be exactly 6 digits");
            return;
        }

        try
        {
            // trys to convert the username into an integer to check if it's numeric
            Integer.parseInt(username);
        }
        catch (NumberFormatException e)
        {
            // If this exception is caught, the username is not a valid integer
            showErrorPopup("Invalid input: Student ID must contain only numeric characters");
            return;
        }

        // checks the username and password against stored credentials
        String[] userDetails = checkCredentials(username, password);
        if (userDetails != null)
        {
            // gets the user's first name, last name, and student ID if the credentials are valid
            String firstName = userDetails[0];
            String lastName = userDetails[1];
            studentId = userDetails[2];

            // new stage for the dashboard window
            Stage dashboardStage = new Stage();
            new Dashboard(dashboardStage, firstName, lastName, studentId); // opens the dashboard
            window.close(); // closes the login window
        }
        else
        {
            showErrorPopup("Invalid Student ID or Password");
        }
    }

    // this method checks the credentials against the database and returns the users details
    private String[] checkCredentials(String username, String password)
    {
        try (Connection connection = DatabaseConnection.getConnection())
        {
            // SQL query to get the user's first name, last name, and student ID
            String query = "SELECT first_name, last_name, student_id FROM tbl_login WHERE student_id = ? AND password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username); // Sets the first placeholder to the username
            preparedStatement.setString(2, password); // Sets the second placeholder to the password

            // Executes the query and gets the result
            ResultSet resultSet = preparedStatement.executeQuery();

            // Checks if we got a result back
            if (resultSet.next())
            {
                // Return user details as an array if user is found
                return new String[] {
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"),
                        resultSet.getString("student_id")
                };
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return null; // Return null if credentials are invalid or an error occurs
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

    // Method to open the Register window
    private void openRegisterWindow()
    {
        // Creates a new stage (window) for the registration form
        Stage registerStage = new Stage();

        // Creates a new Register object, which sets up the registration window
        new Register(registerStage);
    }
}

package com.chudasama.sufeeds;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.control.cell.PropertyValueFactory;

// Classes needed for database connectivity
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Objects;

public class Dashboard
{
    Stage window; // main window for the dashboard
    BorderPane layout; // main layout with sidebar and content area
    VBox sidebar; // sidebar for navigation
    Label contentLabel;

    // table for adding topics
    TableView<ClassInfo> table; // table name
    TextField weekInput; // Input field for week number
    TextField classNameInput; // Input field for class name
    TextField topicsLearntInput; // Input field for topics learnt

    // table for adding classes
    TableView<Classes> tableClasses; // table name
    TextField classInput; // Input field for class name

    // Comments section
    private TextField commentInput; // Input field for comments
    private ListView<Comments> commentsList; // ListView to display comments
    private ObservableList<Comments> commentsListView; // List that updates automatically when comments change

    // fields for user information
    private String firstName;
    private String lastName;
    private String studentId;

    private ObservableList<ClassInfo> classInfos; // List to store class topics
    private ObservableList<Classes> classCollection; // List to store classes

    public Dashboard(Stage primaryStage, String firstName, String lastName, String studentId)
    {
        this.window = primaryStage;
        this.firstName = firstName; // Sets the first name
        this.lastName = lastName;   // Sets the last name
        this.studentId = studentId; // Sets the student ID

        classInfos = getClassInfo(); // Loads topics associated with the user
        classCollection = fetchClasses(); // Fetches the classes the user is enrolled in

        window.setTitle("Dashboard");

        window.setWidth(850);
        window.setHeight(650);

        // Main layout setup
        layout = new BorderPane();
        layout.setPadding(new Insets(20));
        layout.getStyleClass().add("layout-frame");

        // Creates and adds the sidebar with buttons
        sidebar = createSidebar();
        layout.setLeft(sidebar);

        Label titleLabel = new Label("SU Feeds");
        titleLabel.getStyleClass().add("title-label");
        layout.setTop(titleLabel);
        BorderPane.setAlignment(titleLabel, Pos.CENTER);

        // Creates a copyright label with small text
        Label copyrightLabel = new Label("© Sheel Chudasama");
        copyrightLabel.getStyleClass().add("copyright-label");

        // VBox to stack the titleLabel and copyrightLabel vertically
        VBox titleBox = new VBox(5); // Spacing of 5 between title and copyright
        titleBox.setAlignment(Pos.CENTER);
        titleBox.getChildren().addAll(titleLabel, copyrightLabel);

        // Sets titleBox as the top element of the layout
        layout.setTop(titleBox);

        // Content area (center)
        contentLabel = new Label();
        contentLabel.getStyleClass().add("content-label");
        layout.setCenter(contentLabel);
        layout.getStyleClass().add("dashboard-frame");

        // Sets a greeting message for the user
        setGreeting(firstName, lastName);

        // Creates and sets the scene for the window
        Scene scene = new Scene(layout, 600, 400);
        scene.getStylesheets().add("Dashboard.css");
        window.setScene(scene);
        window.show();
    }

    // greeting message for the user
    public void setGreeting (String firstName, String lastName)
    {
        String greeting = "Hello, " + firstName + " " + lastName; // Creates the greeting
        contentLabel.setText(greeting); // Updates the label text
        contentLabel.getStyleClass().add("content-label");
        layout.setCenter(contentLabel); // Adds the label to the layout
    }

    // sidebar
    private VBox createSidebar()
    {
        // Creates a vertical box for the sidebar layout
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(10));
        sidebar.setPrefWidth(150);
        sidebar.getStyleClass().add("sidebar");

        Image svgProfileImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/user-solid.png")));
        ImageView iconViewProfile = new ImageView(svgProfileImage);
        iconViewProfile.setFitHeight(20);  // Set icon size
        iconViewProfile.setFitWidth(20);  // Set icon size

        Image svgTopicsImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/pen-solid.png")));
        ImageView iconViewTopics = new ImageView(svgTopicsImage);
        iconViewTopics.setFitHeight(17);  // Set icon size
        iconViewTopics.setFitWidth(17);  // Set icon size

        Image svgClassesImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/chalkboard-user.png")));
        ImageView iconViewClasses = new ImageView(svgClassesImage);
        iconViewClasses.setFitHeight(20);  // Set icon size
        iconViewClasses.setFitWidth(20);  // Set icon size

        Image svgCommentsImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/comments-solid.png")));
        ImageView iconViewComments = new ImageView(svgCommentsImage);
        iconViewComments.setFitHeight(20);  // Set icon size
        iconViewComments.setFitWidth(20);  // Set icon size


        // Create buttons
        Button profileButton = new Button("Profile", iconViewProfile);
        Button addTopicsButton = new Button("Add Topics", iconViewTopics);
        Button classesButton = new Button("My classes", iconViewClasses);
        Button commentsButton = new Button("Comments", iconViewComments);
        Button logoutButton = new Button("Logout");

        // Set button styles
        profileButton.getStyleClass().add("profile-button");
        addTopicsButton.getStyleClass().add("addTopics-button");
        classesButton.getStyleClass().add("classes-button");
        commentsButton.getStyleClass().add("comments-button");
        logoutButton.getStyleClass().add("logout-button");

        classesButton.setGraphicTextGap(6);

        // Set button maximum width
        profileButton.setMaxWidth(Double.MAX_VALUE);
        addTopicsButton.setMaxWidth(Double.MAX_VALUE);
        classesButton.setMaxWidth(Double.MAX_VALUE);
        commentsButton.setMaxWidth(Double.MAX_VALUE);
        logoutButton.setMaxWidth(Double.MAX_VALUE);

        // Button actions with the setButtonSelected method
        profileButton.setOnAction(e -> {
            setButtonSelected(profileButton, addTopicsButton, classesButton, commentsButton);
            showProfile();
        });

        addTopicsButton.setOnAction(e -> {
            setButtonSelected(addTopicsButton, profileButton, classesButton, commentsButton);
            addTopics();
        });

        classesButton.setOnAction(e -> {
            setButtonSelected(classesButton, profileButton, addTopicsButton, commentsButton);
            addClasses();
        });

        commentsButton.setOnAction(e -> {
            setButtonSelected(commentsButton, profileButton, addTopicsButton, classesButton);
            showComments();
        });

        logoutButton.setOnAction(e -> logoutInformation());

        // Adds the buttons to the sidebar
        sidebar.getChildren().addAll(profileButton, addTopicsButton, classesButton, commentsButton);

        // Creates a spacer to push the logout button to the bottom
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Adds the spacer and logout button
        sidebar.getChildren().addAll(spacer, logoutButton);

        return sidebar; // Returns the completed sidebar
    }

    // Method to handle the selection state
    private void setButtonSelected(Button selectedButton, Button... otherButtons)
    {
        // Add the selected class to the clicked button
        selectedButton.getStyleClass().add("selected");

        // Remove the selected class from all other buttons
        for (Button button : otherButtons)
        {
            button.getStyleClass().remove("selected"); // removes selected class from other buttons that are not selected by looping through them
        }
    }

    // profile section
    private void showProfile()
    {
        // name label
        Label nameLabel = new Label(String.format("Name: %s %s", firstName, lastName));
        nameLabel.getStyleClass().add("profile-name");

        // student id label
        Label studentIdLabel = new Label(String.format("Student ID: %s", studentId));
        studentIdLabel.getStyleClass().add("profile-details");

        // VBox to hold the labels
        VBox profileContent = new VBox(5); // 10 pixels spacing between labels
        profileContent.getChildren().addAll(nameLabel, studentIdLabel);
        profileContent.setAlignment(Pos.CENTER);

        layout.setCenter(profileContent); // Sets profileContent as the center content
    }

    // add topics section
    private void addTopics()
    {
        // week column
        TableColumn<ClassInfo, Integer> weekColumn = new TableColumn<>("Week");
        weekColumn.setMinWidth(100);
        weekColumn.getStyleClass().add("week-column");
        weekColumn.setCellValueFactory(new PropertyValueFactory<>("week")); // Binds to the week property in the class classinfo

        // class name column
        TableColumn<ClassInfo, String> classNameColumn = new TableColumn<>("Class Name");
        classNameColumn.setMinWidth(200);
        classNameColumn.setCellValueFactory(new PropertyValueFactory<>("className")); // Binds to the class name property in the class classinfo

        // topics learnt column
        TableColumn<ClassInfo, String> topicsLearntColumn = new TableColumn<>("Topics Learnt");
        topicsLearntColumn.setMinWidth(301);
        topicsLearntColumn.setCellValueFactory(new PropertyValueFactory<>("topicsLearnt")); // Binds to the topics learnt property in the class classinfo

        // week input field
        weekInput = new TextField();
        weekInput.setPromptText("Week");
        weekInput.getStyleClass().add("week-input");

        // class name input field
        classNameInput = new TextField();
        classNameInput.setPromptText("Class Name");
        classNameInput.getStyleClass().add("class-input");
        classNameInput.setPrefWidth(250);    // Adjust width as needed

        // topics learnt input field
        topicsLearntInput = new TextField();
        topicsLearntInput.setPromptText("Topics Learnt");
        topicsLearntInput.getStyleClass().add("topics-learnt");
        topicsLearntInput.setPrefWidth(250); // Adjust width as needed

        // add button
        Button addButton = new Button("Add");
        addButton.setMinWidth(80);
        addButton.getStyleClass().add("add-button");
        addButton.setOnAction(e -> addButtonClicked());

        // delete button
        Button deleteButton = new Button("Delete");
        deleteButton.setMinWidth(80);
        deleteButton.getStyleClass().add("delete-button");
        deleteButton.setOnAction(e -> deleteButtonClicked());

        // HBox to hold the add and delete buttons
        HBox hbox = new HBox(10); // Spacing between buttons
        hbox.setPadding(new Insets(10, 10, 10, 0)); // Padding around the HBox (Top, Right, Bottom, Left)
        hbox.setAlignment(Pos.TOP_LEFT);
        hbox.getChildren().addAll(addButton, deleteButton);

        // VBox to organize input fields and buttons vertically
        VBox vBox = new VBox(10); // Spacing between elements
        vBox.setPadding(new Insets(10));  // Padding around the VBox
        vBox.setAlignment(Pos.TOP_LEFT);
        vBox.getChildren().addAll(weekInput, classNameInput, topicsLearntInput, hbox);

        // Table setup
        table = new TableView<>();
        table.getStyleClass().add("table-view");
        table.setItems(classInfos); // Binds the TableView to the data source (classInfos)
        table.getColumns().addAll(weekColumn, classNameColumn, topicsLearntColumn);

        // Vertical box to hold the table and vBox
        VBox classLayout = new VBox(10);
        classLayout.setPadding(new Insets(10));
        classLayout.getChildren().addAll(table, vBox);

        // Sets the margin for the classLayout within the borderpane
        BorderPane.setMargin(classLayout, new Insets(10, 10, 10, 10)); // (top, right, bottom, left)

        // Sets the VBox as the content in the main layout's center area
        layout.setCenter(classLayout);
    }

    private void addClasses()
    {
        // class column
        TableColumn<Classes, String> classColumn = new TableColumn<>("Classes");
        classColumn.getStyleClass().add("class-column");
        classColumn.setMaxWidth(Double.MAX_VALUE);
        classColumn.setMinWidth(630);
        classColumn.setCellValueFactory(new PropertyValueFactory<>("classesList"));  // Binds to the classes list property in the class classes

        // class input field
        classInput = new TextField();
        classInput.setPromptText("Class Name");
        classInput.getStyleClass().add("classes-input");

        // add class button
        Button addClassesButton = new Button("Add");
        addClassesButton.setMinWidth(80);
        addClassesButton.getStyleClass().add("addClasses-button");
        addClassesButton.setOnAction(e -> addClassesButtonClicked());

        // delete class button
        Button deleteClassesButton = new Button("Delete");
        deleteClassesButton.setMinWidth(80);
        deleteClassesButton.getStyleClass().add("deleteClasses-button");
        deleteClassesButton.setOnAction(e -> deleteClassesButtonClicked());

        // Hbox for add and delete buttons
        HBox hboxClasses = new HBox(10);
        hboxClasses.setPadding(new Insets(10, 10, 10, 0));
        hboxClasses.setAlignment(Pos.TOP_LEFT);
        hboxClasses.getChildren().addAll(addClassesButton, deleteClassesButton);

        // Hbox for inputs and buttons
        VBox vboxClasses = new VBox(10);
        vboxClasses.setPadding(new Insets(10));
        vboxClasses.setAlignment(Pos.TOP_LEFT);
        vboxClasses.getChildren().addAll(classInput, hboxClasses);

        // Table setup
        tableClasses = new TableView<>();
        tableClasses.getStyleClass().add("tableClasses-view");
        tableClasses.setItems(classCollection); // Binds the TableView to the data source (classCollection)
        tableClasses.getColumns().addAll(classColumn);

        // Vertical box to hold the table and vBox
        VBox classesLayout = new VBox(10);
        classesLayout.setPadding(new Insets(10));
        classesLayout.getChildren().addAll(tableClasses, vboxClasses);

        // Sets the margin for the classesLayout within the borderpane
        BorderPane.setMargin(classesLayout, new Insets(10, 10, 10, 10)); // (top, right, bottom, left)

        // Sets the VBox as the content in the main layout's center area
        layout.setCenter(classesLayout);
    }

    // add button for add topics table
    private void addButtonClicked()
    {
        // Gets the input values from the UI fields
        String weekInputText = weekInput.getText();
        String className = classNameInput.getText();
        String topicsLearnt = topicsLearntInput.getText();

        // Validates inputs
        if (weekInputText.isEmpty() || className.isEmpty() || topicsLearnt.isEmpty())
        {
            showErrorPopup("Please fill in all fields");
            return;
        }

        // Validates week input to ensure it is an integer
        try
        {
            int week = Integer.parseInt(weekInputText); // Attempt to parse week as an integer
            if (week <= 0) // Checks if week is a positive integer
            {
                showErrorPopup("Week number must be a positive integer");
                return;
            }
        }
        catch (NumberFormatException e)
        {
            showErrorPopup("Invalid week format. Please enter a positive integer");
            return;
        }

        // Connects to the database and inserts the new topic
        try (Connection connection = DatabaseConnection.getConnection())
        {
            String query = "INSERT INTO tbl_topics (week, class_name, topics_learnt, student_id) VALUES (?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, Integer.parseInt(weekInputText));
            preparedStatement.setString(2, className);
            preparedStatement.setString(3, topicsLearnt);
            preparedStatement.setString(4, studentId);

            // Executes the query and checks if the insertion was successful
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0)
            {
                classInfos.add(new ClassInfo(weekInputText, className, topicsLearnt)); // Add to list
                successfulPopupTopicAdded("Topic added successfully");
                loadTopicsIntoTable();
                weekInput.clear();
                classNameInput.clear();
                topicsLearntInput.clear();
            }
            else
            {
                showErrorPopup("Failed to add the topic");
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            showErrorPopup("Database error: " + e.getMessage());
        }
    }

    // loads topics data into the table
    private void loadTopicsIntoTable()
    {
        classInfos.clear(); // Clears the existing list
        classInfos.addAll(getClassInfo()); // Loads new data from the database
        table.setItems(classInfos); // Refreshes the table
    }

    // delete button for add topics table
    private void deleteButtonClicked()
    {
        // Gets the selected topic from the TableView
        ClassInfo selectedTopic = table.getSelectionModel().getSelectedItem();

        // Validate if a topic has been selected
        if (selectedTopic == null)
        {
            showErrorPopup("Please select a topic to delete");
            return;
        }

        // Connects to the database and deletes the selected topic
        try (Connection connection = DatabaseConnection.getConnection())
        {
            String query = "DELETE FROM tbl_topics WHERE week = ? AND class_name = ? AND topics_learnt = ? AND student_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, selectedTopic.getWeek());
            preparedStatement.setString(2, selectedTopic.getClassName());
            preparedStatement.setString(3, selectedTopic.getTopicsLearnt());
            preparedStatement.setString(4, studentId); // Ensures the topic belongs to the logged-in user

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0)
            {
                successfulPopupTopicDeleted("Topic deleted successfully");
                table.getItems().remove(selectedTopic); // Remove from the observable list
                loadTopicsIntoTable();
            }
            else
            {
                showErrorPopup("Failed to delete the selected topic");
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            showErrorPopup("Database error: " + e.getMessage());
        }
    }

    // Add button for classes table
    private void addClassesButtonClicked()
    {
        String className = classInput.getText();
        StringBuilder errorMessage = new StringBuilder();

        // Check if the class name is empty
        if (className.isEmpty())
        {
            errorMessage.append("Class name cannot be empty\n");
        }

        // Check if the class name is numeric
        try
        {
            Integer.parseInt(className);
            errorMessage.append("Class name cannot be a number\n");
        }
        catch (NumberFormatException e)
        {
            // Class name is not numeric, so we can proceed
        }

        // Show error message if there are any issues
        if (errorMessage.length() > 0)
        {
            showErrorPopup(errorMessage.toString());
            return;
        }

        // Connect to the database and insert the class
        try (Connection connection = DatabaseConnection.getConnection())
        {
            String query = "INSERT INTO tbl_classes (class_name, student_id) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, className);
            preparedStatement.setString(2, studentId); // Use the logged-in student's ID

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0)
            {
                classCollection.add(new Classes(className));
                successfulPopupTopicAdded("Class added successfully");
                loadTopicsIntoTableClasses();
                classInput.setText(""); // Clear the input field
            }
            else
            {
                showErrorPopup("Failed to add the class");
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            showErrorPopup("Database error: " + e.getMessage());
        }
    }

    private void loadTopicsIntoTableClasses()
    {
        classCollection.clear(); // Clear the existing list
        classCollection.addAll(fetchClasses()); // Load new data from the database
        tableClasses.setItems(classCollection); // Refresh the table
    }

    // delete button for classes table
    public void deleteClassesButtonClicked()
    {
        // Get the selected topic from the TableView
        Classes selectedClass = tableClasses.getSelectionModel().getSelectedItem();

        // Validate selection
        if (selectedClass == null)
        {
            showErrorPopup("Please select a class to delete");
            return;
        }

        // Connect to the database and delete the selected topic
        try (Connection connection = DatabaseConnection.getConnection())
        {
            String query = "DELETE FROM tbl_classes WHERE class_name = ? AND student_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, selectedClass.getClassesList());
            preparedStatement.setString(2, studentId);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0)
            {
                successfulPopupTopicDeleted("Class deleted successfully");
                tableClasses.getItems().remove(selectedClass); // Remove from the observable list
                loadTopicsIntoTableClasses();
            }
            else
            {
                showErrorPopup("Failed to delete the selected class");
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            showErrorPopup("Database error: " + e.getMessage());
        }
    }

    // Method to fetch and return the list of topics from the database
    public ObservableList<ClassInfo> getClassInfo()
    {
        ObservableList<ClassInfo> classInfos = FXCollections.observableArrayList();

        try (Connection connection = DatabaseConnection.getConnection())
        {
            String query = "SELECT week, class_name, topics_learnt FROM tbl_topics WHERE student_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, studentId); // Fetch topics for this user

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) // Loops through each result row
            {
                String week = resultSet.getString("week"); // Retrieves the week for the topic
                String className = resultSet.getString("class_name"); // Retrieves the class name
                String topicsLearnt = resultSet.getString("topics_learnt"); // Retrieves the topics learnt

                // Adds each topic to classInfos
                classInfos.add(new ClassInfo(week, className, topicsLearnt));
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            showErrorPopup("Database error: " + e.getMessage());
        }
        return classInfos;
    }

    // Method to fetch and return the list of classes from the database
    public ObservableList<Classes> fetchClasses()
    {
        // Creates an observable list to store the class names
        ObservableList<Classes> classes = FXCollections.observableArrayList();

        // Connects to the database and executes the query to fetch classes for the logged-in student
        try (Connection connection = DatabaseConnection.getConnection())
        {
            String query = "SELECT class_name FROM tbl_classes WHERE student_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, studentId); // Fetch topics for this user

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next())  // Loops through each result row
            {
                String className = resultSet.getString("class_name"); // Retrieves the class name

                // Adds each topic to Classes
                classes.add(new Classes(className));
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            showErrorPopup("Database error: " + e.getMessage());
        }
        return classes;
    }

    // error popup
    public void showErrorPopup(String message)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // popup for logout
    public void successfulPopup(String message)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Logout Successful");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // popup for topic added
    public void successfulPopupTopicAdded(String message)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Add operation successful");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // popup for topic deleted
    public void successfulPopupTopicDeleted(String message)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Delete operation successful");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // sets up the user interface (UI) for the comments section
    private void showComments()
    {
        // Creates a VBox to hold the comments section with a vertical spacing of 10px
        VBox commentsLayout = new VBox(10);
        commentsLayout.setPadding(new Insets(10));

        // Creates a horizontal layout (HBox) for the comment input field and submit button
        HBox commentsHorizontalLayout = new HBox(10);
        commentsHorizontalLayout.setPadding(new Insets(10));
        commentsHorizontalLayout.setSpacing(8);

        // TextField for user to input their comments
        commentInput = new TextField();
        commentInput.getStyleClass().add("comment-input");
        commentInput.setPromptText("Comments");
        commentInput.setPrefWidth(500);
        commentInput.setMaxHeight(30);

        // ListView to display comments
        commentsList = new ListView<>();
        commentsList.getStyleClass().add("comments-list");

        // Load the SVG image
        Image svgImage = new Image(getClass().getResourceAsStream("/icons/paper-plane-solid.png"));

        // Create an ImageView with the SVG
        ImageView iconView = new ImageView(svgImage);
        iconView.setFitHeight(20);  // Set icon size
        iconView.setFitWidth(20);  // Set icon size

        // Button to submit comment
        Button submitButton = new Button();
        submitButton.setGraphic(iconView);
        submitButton.getStyleClass().add("submit-button");
        submitButton.setOnAction(e -> submitComment());

        // Adds the comment input field and submit button to the horizontal layout
        commentsHorizontalLayout.getChildren().addAll(commentInput, submitButton);

        // Adds the horizontal layout (input field and button) and the comments list to the main layout (VBox)
        commentsLayout.getChildren().addAll(commentsList, commentsHorizontalLayout);
        layout.setCenter(commentsLayout); // Sets the commentsLayout as the central content of the main layout

        loadComments(); // Loads existing comments from the database when initializing the section
    }

    // sends the comment to the database
    private void submitComment()
    {
        String comment = commentInput.getText().trim();
        StringBuilder commentErrorMessage = new StringBuilder();

        // Validate input
        if (comment.isEmpty())
        {
            commentErrorMessage.append("Comment input cannot be empty\n");
        }

        // Show error message if validation fails
        if (commentErrorMessage.length() > 0)
        {
            showErrorPopup(commentErrorMessage.toString());
            return;
        }

        // Connects to the database and inserts the comment
        try (Connection connection = DatabaseConnection.getConnection())
        {
            String query = "INSERT INTO tbl_comments (comment_text, student_id) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, comment);
            preparedStatement.setString(2, studentId); // Use the logged-in student's ID

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0)
            {
                // Successfully added comment, refresh comments list
                loadComments();
                commentInput.clear(); // Clears the input field
            }
            else
            {
                showErrorPopup("Failed to add comment");
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            showErrorPopup("Database error: " + e.getMessage());
        }
    }

    // Refreshes the comments displayed in the commentsList
    private void loadComments()
    {
        commentsList.getItems().clear(); // Clears the existing comments from the list
        ObservableList<Comments> comments = getCommentsFromDatabase(); // Gets new comments from the database

        // Sets a custom way to display each comment in the list
        commentsList.setCellFactory(param -> new ListCell<Comments>()
        {
            @Override
            protected void updateItem(Comments comment, boolean empty)
            {
                super.updateItem(comment, empty);

                // If the item is empty (no comment) or null, do not display anything
                if (empty || comment == null)
                {
                    setText(null); // Removes text
                    setGraphic(null); // Removes any graphic (image or layout)
                }
                else
                {
                    // Creates a label to display the comment text
                    Label commentLabel = new Label(comment.getContent());
                    commentLabel.setWrapText(true); // Allows the text to wrap within the comment bubble

                    // Creates a label to display the timestamp of the comment
                    Label timestampLabel = new Label(comment.getDate()); // Uses the date from the comment
                    timestampLabel.getStyleClass().add("timestamp"); // CSS class for styling the timestamp

                    // Creates a label for the username
                    Label usernameLabel = new Label(comment.getUsername()); // Display the user's name
                    usernameLabel.getStyleClass().add("username"); // CSS class for username styling

                    // VBox to hold the comment label and timestamp label, with space between them
                    VBox commentContainer = new VBox(5, commentLabel, timestampLabel, usernameLabel);
                    commentContainer.setSpacing(5); // Sets spacing between the comment and the timestamp

                    // Checks if the comment is from the logged-in user
                    boolean isSent = comment.getUsername().equals(getLoggedInUsername());

                    // If the comment is from the logged-in user, it displays it on the right side
                    if (isSent)
                    {
                        commentLabel.getStyleClass().add("sent-message");
                        commentContainer.setAlignment(Pos.CENTER_RIGHT); // Aligns sent message to the right
                    }
                    else
                    {
                        commentLabel.getStyleClass().add("received-message");
                        commentContainer.setAlignment(Pos.CENTER_LEFT); // Aligns received message to the left
                    }

                    // Adds padding and styling to the comment bubble
                    commentLabel.setPadding(new Insets(5, 10, 5, 10)); // Adds padding around the comment text
                    commentLabel.getStyleClass().add("comment-bubble");

                    // Sets the final layout of the comment (comment and timestamp) as the graphic of the ListCell
                    setGraphic(commentContainer);
                }
            }
        });

        // Adds all the comments to the ListView (this will use the custom display method above)
        commentsList.getItems().addAll(comments);
    }

    //  Retrieves all comments from the database
    private ObservableList<Comments> getCommentsFromDatabase()
    {
        ObservableList<Comments> commentList = FXCollections.observableArrayList();

        // SQL query to fetch the first name, last name, and comment text from the database
        String sql = "SELECT l.first_name, l.last_name, c.comment_text , c.created_at FROM tbl_comments c " +
                "JOIN tbl_login l ON c.student_id = l.student_id";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) // Executes the query and gets the result set
        {
            while (rs.next())
            {
                String username = rs.getString("first_name") + " " + rs.getString("last_name");
                String content = rs.getString("comment_text");

                // Get the created_at timestamp from the database
                Timestamp timestamp = rs.getTimestamp("created_at");

                // Format the timestamp to display dd-MM-yyyy HH:mm
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                String formattedDate = dateFormat.format(timestamp);

                // Add the comment with the formatted date to the list
                commentList.add(new Comments(username, content, formattedDate));
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return commentList; // Return the list of comments
    }

    private String getLoggedInUsername()
    {
        String username = "";
        String sql = "SELECT first_name, last_name FROM tbl_login WHERE student_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, studentId); // Assuming studentId is available
            try (ResultSet rs = stmt.executeQuery())
            {
                if (rs.next())
                {
                    username = rs.getString("first_name") + " " + rs.getString("last_name");
                }
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return username;
    }

    private void logoutInformation()
    {
        successfulPopup("You have successfully logged out");

        // Create a new Login window
        Stage loginStage = new Stage();
        new Login(loginStage); // Open the login window

        // Close the dashboard window
        window.close();
    }
}

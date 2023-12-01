import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.ProgressBar;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ProgressBar;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MainView extends Application {
    
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Login Screen");

        // Default login view
        Scene scene = new Scene(createLoginView(primaryStage), 600, 600);
        
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createLoginView(Stage primaryStage) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(10);
        grid.setHgap(10);

        Label emailLabel = new Label("Email:");
        GridPane.setConstraints(emailLabel, 0, 0);
        TextField emailInput = new TextField();
        GridPane.setConstraints(emailInput, 1, 0);

        Label passLabel = new Label("Password:");
        GridPane.setConstraints(passLabel, 0, 1);
        PasswordField passInput = new PasswordField();
        GridPane.setConstraints(passInput, 1, 1);

        Button loginButton = new Button("Login");
        GridPane.setConstraints(loginButton, 1, 2);
        loginButton.setOnAction(e -> {
            // Validate and process login
            if (isValidEmail(emailInput.getText()) && isValidPassword(passInput.getText())) {
                // Here, you would check credentials against the database
                // using parameterized queries.
            } else {
                showInputError();
            }
        });
        
        Button signupButton = new Button("Sign Up");
        GridPane.setConstraints(signupButton, 1, 3);
        signupButton.setOnAction(e -> {
            Scene signupScene = new Scene(createSignupView(primaryStage), 600, 600);
            primaryStage.setScene(signupScene);
        });

        grid.getChildren().addAll(emailLabel, emailInput, passLabel, passInput, loginButton, signupButton);

        VBox layout = new VBox();
        layout.getChildren().add(grid);
        
        return layout;
    }

   private VBox createSignupView(Stage primaryStage) {
    GridPane grid = new GridPane();
    grid.setPadding(new Insets(10, 10, 10, 10));
    grid.setVgap(10);
    grid.setHgap(10);

    Label emailLabel = new Label("Email:");
    GridPane.setConstraints(emailLabel, 0, 0);
    TextField emailInput = new TextField();
    GridPane.setConstraints(emailInput, 1, 0);

    Label passLabel = new Label("Password:");
    GridPane.setConstraints(passLabel, 0, 1);
    PasswordField passInput = new PasswordField();
    GridPane.setConstraints(passInput, 1, 1);

    ProgressBar passwordStrengthBar = new ProgressBar(0);
    GridPane.setConstraints(passwordStrengthBar, 2, 1);

    TextField strengthPercentageField = new TextField("0%");
    strengthPercentageField.setEditable(false);
    GridPane.setConstraints(strengthPercentageField, 3, 1);

    Button signupButton = new Button("Create Account");
    GridPane.setConstraints(signupButton, 1, 2);

    Button backButton = new Button("Back");
    GridPane.setConstraints(backButton, 1, 3);
    backButton.setOnAction(e -> {
        Scene loginScene = new Scene(createLoginView(primaryStage), 400, 250);
        primaryStage.setScene(loginScene);
    });

    // Listener for password strength
    passInput.textProperty().addListener(new ChangeListener<String>() {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            int strength = PasswordValidate.checkPasswordStrength(newValue);
            passwordStrengthBar.setProgress(strength / 100.0);
            strengthPercentageField.setText(strength + "%");
        }
    });

    signupButton.setOnAction(e -> {
        if (!PasswordValidate.isValidEmail(emailInput.getText())) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Account Creation");
            alert.setHeaderText(null);
            alert.setContentText("Invalid email address\n Enter a valid email address");
            alert.showAndWait();
            return; 
        } else {
            int strength = PasswordValidate.checkPasswordStrength(passInput.getText());
            if (strength >= 70) { // Adjust the threshold as per your requirements
                try {
                    // Insert user into the database (using parameterized queries)
                    insertUser(emailInput.getText(), passInput.getText());
                    // Notify user of successful account creation
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Account Creation");
                    alert.setHeaderText(null);
                    alert.setContentText("Account successfully created!");
                    alert.showAndWait();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    // Handle SQL Exception
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Database Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Error creating account. Please try again.");
                    alert.showAndWait();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Account Creation");
                alert.setHeaderText(null);
                alert.setContentText("Password not strong enough\nTry again");
                alert.showAndWait();
            }
        }
        
        PasswordSecurity.hashPasswordAndStore(passInput.getText());
    });

    grid.getChildren().addAll(emailLabel, emailInput, passLabel, passInput, passwordStrengthBar, strengthPercentageField, signupButton, backButton);

    VBox layout = new VBox();
    layout.getChildren().add(grid);
    
    return layout;
}
   private void insertUser(String email, String password) throws SQLException {
        String query = "INSERT INTO users (email, password) VALUES (?, ?)";
        try (Connection connection = getConnection(); // Assume getConnection() returns a valid connection
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password); // Hash the password in real scenarios

            preparedStatement.executeUpdate();
        }
    }

    // Method to validate email
    private boolean isValidEmail(String email) {
        String regex = "^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}$";
        return email.matches(regex);
    }

    // Method to validate password (modify criteria as needed)
    private boolean isValidPassword(String password) {
        return password.length() > 6; // Simple length check, enhance as needed
    }

    // Show an error alert
    private void showInputError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Input Error");
        alert.setHeaderText(null);
        alert.setContentText("Invalid input. Please enter valid email and password.");
        alert.showAndWait();
    }

    // Placeholder for getting database connection
    private Connection getConnection() {
        // Here you would return a valid database connection
        return null;
    }

    // ... include PasswordValidate class as before ...
}

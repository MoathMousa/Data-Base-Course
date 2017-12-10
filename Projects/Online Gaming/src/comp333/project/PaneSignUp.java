package comp333.project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class PaneSignUp extends VBox {
    
    public static final int DATE_WIDTH = 65;
    
    private Label labelMessage;
    private TextField textUsername;
    private PasswordField textPassword;
    private TextField textEmail;
    private TextField textDate1;
    private TextField textDate2;
    private TextField textDate3;
    
    public PaneSignUp() {
        
        // Buttons
        ButtonM btnSignUp = new ButtonM("Sign Up");
        ButtonM btnCancel = new ButtonM("Cancel");
        
        btnSignUp.setOnAction(e -> { buttonActionSignUp(); });
        btnCancel.setOnAction(e -> { buttonActionCancel(); });
        
        // Text fields
        textUsername = new TextField();
        textPassword = new PasswordField();
        textEmail = new TextField();
        textDate1 = new TextField();
        textDate2 = new TextField();
        textDate3 = new TextField();
        
        textDate1.setPrefWidth(DATE_WIDTH);
        textDate2.setPrefWidth(DATE_WIDTH);
        textDate3.setPrefWidth(DATE_WIDTH);
        
        // Labels
        labelMessage = new Label();
        Label labelUsername = new Label("Username:");
        Label labelPassword = new Label("Password:");
        Label labelEmail = new Label("Email:");
        Label labelDate = new Label("Date:");
        
        // Panes
        HBox paneButtons = new HBox(3, btnSignUp, btnCancel);
        HBox paneDates = new HBox(3, textDate1, new Label("/"), textDate2, new Label("/"), textDate3);
        HBox paneLabel = new HBox(labelMessage);
        
        GridPane paneGrid = new GridPane();
        
        paneGrid.add(labelUsername, 0, 0);
        paneGrid.add(labelPassword, 0, 1);
        paneGrid.add(labelDate, 0, 2);
        paneGrid.add(labelEmail, 0, 3);
        paneGrid.add(textUsername, 1, 0);
        paneGrid.add(textPassword, 1, 1);
        paneGrid.add(paneDates, 1, 2);
        paneGrid.add(textEmail, 1, 3);
        
        // Alignments and stuff
        paneDates.setAlignment(Pos.CENTER);
        paneButtons.setAlignment(Pos.CENTER);
        
        paneGrid.setVgap(35);
        paneGrid.setHgap(40);
        paneGrid.setAlignment(Pos.CENTER);
        
        paneLabel.setAlignment(Pos.CENTER);
        
        this.setPadding(new Insets(15));
        this.getChildren().addAll(paneGrid, paneButtons, paneLabel);
        this.setSpacing(10);
        
    }
    
    private void buttonActionSignUp() {
        
        String username = textUsername.getText().trim();
        String password = textPassword.getText().trim();
        String email = textEmail.getText().trim();
        String date1 = textDate1.getText().trim();
        String date2 = textDate2.getText().trim();
        String date3 = textDate3.getText().trim();
        
        if (!username.isEmpty() && !password.isEmpty() && !email.isEmpty() && 
                !date1.isEmpty() && !date2.isEmpty() && !date3.isEmpty()) {
        
            // If validSignup = true, then add this account to the database
            // Assume initially it is true
            boolean validSignup = true;
        
            if (!validUsername(username)) {
                validSignup = false;
                labelMessage.setText("Username: letters, digits or underscores only and < 15");
            }
            
            else if (password.length() < 5 || password.length() > 15) {
                validSignup = false;
                labelMessage.setText("Password length must be between 5 and 15 characters");
            }
            
            // At least one '@' must be in email
            else if (!email.contains("@")) {
                validSignup = false;
                labelMessage.setText("Wrong email format");
            }
            
            else
                try {
                    
                    if (!validDate(Byte.parseByte(textDate1.getText()),
                            Byte.parseByte(textDate2.getText()),
                            Integer.parseInt(textDate3.getText()))) {
                        validSignup = false;
                        labelMessage.setText("Wrong date format");
                    }
                
                } catch (NumberFormatException ex) {
                    // If the text fields have a non-digit character in it, then NumberFormatException will be thrown
                    validSignup = false;
                    labelMessage.setText("Wrong date format");
                }
            
            // If validSignup is still true, this means all input formats were correct.
            // Now check if the account already exists in the database.
            
            if (validSignup) {
                
                try {
                    
                    Connection c = DriverManager.getConnection(Main.DATABASE_SERVER, "root", "admin");
                    
                    if (c != null) {
                        
                        Statement stmt1 = c.createStatement();
                        Statement stmt2 = c.createStatement();
                        
                        // Username and email must be unique
                        ResultSet resultSetUsernameAlready = stmt1.executeQuery("SELECT G.username FROM GAME_ACCOUNT G WHERE G.username = '" + username + "'");
                        ResultSet resultSetEmailAlready = stmt2.executeQuery("SELECT G.username FROM GAME_ACCOUNT G WHERE G.email = '" + email + "'");
                        
                        if (resultSetUsernameAlready.next())
                            // Found a similar username, so can't sign up
                            labelMessage.setText("Username already exists");
                        else if (resultSetEmailAlready.next())
                            // Found a similar email, so can't sign up
                            labelMessage.setText("Email already exists");
                        else {
                            
                            // We want to know what is the id and player_number of the added player.
                            // What we will do is start from 0 and go through the list as long
                            // there are rows and the current row's id or player_number is the same.
                            // This way, we can fill "gap" ids such as:
                            // Suppose we had the ids: 1 3 4 5 6
                            // Adding 0 would be the best choice.
                            // Another ex: 0 1 2 3 4 5 7
                            // Adding 6 would be the best choice
                            // Another ex: 0 1 2 3
                            // Adding 4 would be the best choice
                            
                            // First get all of the list of ids and player numbers
                            Statement stmt3 = c.createStatement();
                            Statement stmt4 = c.createStatement();
                            // Get the ids from GAME_ACCOUNT table and get the player numbers from PLAYER table
                            ResultSet resultSetGameAccountIds = stmt3.executeQuery("SELECT G.id FROM GAME_ACCOUNT G ORDER BY G.id");
                            ResultSet resultSetPlayerNumbers = stmt4.executeQuery("SELECT P.player_number FROM PLAYER P ORDER BY P.player_number");
                            
                            int idGameAccountToAdd = 0;
                            int numberPlayerToAdd = 0;
                            
                            while (resultSetGameAccountIds.next() && idGameAccountToAdd == resultSetGameAccountIds.getInt("id"))
                                idGameAccountToAdd++;
                            
                            while (resultSetPlayerNumbers.next() && numberPlayerToAdd == resultSetPlayerNumbers.getInt("player_number"))
                                numberPlayerToAdd++;
                            
                            Statement stmt5 = c.createStatement();
                            Statement stmt6 = c.createStatement();
                            // Create two rows, one for GAME_ACCOUNT, and one for PLAYER (Inheritance)
                            stmt5.executeUpdate("INSERT INTO GAME_ACCOUNT (`id`, `username`, `password`, `email`, `level`, `day`, `month`, `year`, `sel_car_no`, `sel_color_no`, `server_id`)"
                                    + "VALUES (" + idGameAccountToAdd + ", '" + username + "', '" + password + "', '" + email + "', 1, " + date1 + ", " + date2 + ", " + date3 + ", 0, 0, NULL);");
                            stmt6.executeUpdate("INSERT INTO PLAYER (`id`, `player_number`, `banned_by`) VALUES (" + idGameAccountToAdd + ", " + numberPlayerToAdd + ", NULL)");
                            
                            Main.loadSignIn();
                            
                        } 
                        
                        
                        c.close();
                    }
                    
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
                
            }
            
        } else labelMessage.setText("Not all fields are filled");
        
    }
    
    // A valid username is less than 15 characters and contains only characters, letters or '_'
    private boolean validUsername(String username) {
        
        if (username.length() > 15)
            return false;
        
        for (int i = 0; i < username.length(); i++)
            if (!Character.isLetterOrDigit(username.charAt(i))
                && username.charAt(i) != '_')
                return false;
        
        return true;
        
    }
    
    private boolean validDate(byte day, byte month, int year) {
        
        // You can't register in the same year as current year
        if (year >= Calendar.getInstance().get(Calendar.YEAR))
            return false;
        
        if (month > 12 || month < 1)
            return false;
        
        if (day < 1)
            return false;
        
        byte dayLimit = 31;
        
        if (month == 2)
            dayLimit = 28;
        else if (month == 4 || month == 6 || month == 9 || month == 11)
            dayLimit = 30;
        
        return day <= dayLimit;
        
    }
    
    private void buttonActionCancel() {
        Main.loadSignIn();
    }
    
}
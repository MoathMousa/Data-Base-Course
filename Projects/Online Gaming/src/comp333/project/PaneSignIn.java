package comp333.project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class PaneSignIn extends VBox {
        
    private Label labelMessage;
    
    private TextField textUsername;
    private PasswordField textPassword;
    
    public PaneSignIn() {
        
        // Buttons
        ButtonM btnSignIn = new ButtonM("Sign In");
        ButtonM btnSignUp = new ButtonM("Sign Up");
        
        btnSignIn.setOnAction(e -> { buttonActionSignIn(); });
        btnSignUp.setOnAction(e -> { buttonActionSignUp(); });
        
        // Text Fields       
        textUsername = new TextField();
        textPassword = new PasswordField();
        
        // Labels       
        Label labelUsername = new Label("Username:");
        Label labelPassword = new Label("Password:");
        labelMessage = new Label();
        
        // Panes       
        HBox paneRow1 = new HBox(2, labelUsername, textUsername);
        HBox paneRow2 = new HBox(6, labelPassword, textPassword);
        HBox paneRow3 = new HBox(2, btnSignIn, btnSignUp);
        
        // Styles and alignments
        paneRow1.setAlignment(Pos.CENTER);
        paneRow2.setAlignment(Pos.CENTER);
        paneRow3.setAlignment(Pos.CENTER);
        
        this.setSpacing(12);
        this.getChildren().addAll(paneRow1, paneRow2, paneRow3, labelMessage);
        this.setPadding(new Insets(20));
    
    }
    
    private void buttonActionSignUp() {
        
        Main.loadSignUp();
        
    }
    
    private void buttonActionSignIn() {
        
        String username = textUsername.getText().trim();
        String password = textPassword.getText().trim();
        
        // Check if the text fields' inputs are not empty
        if (!username.isEmpty() && !password.isEmpty()) {
            
            // If noSuchLogin is true, this means incorrect login
            // Assume initially that it isn't a correct login
            boolean noSuchLogin = true;
            
            // Query for textUsername.getText(), textPassword.getText()
            // Found something matching, then it is a valid login

            try {
                
                Connection c = DriverManager.getConnection(Main.DATABASE_SERVER, "root", "admin");
                
                if (c != null) {
                    
                    Statement stmt1 = c.createStatement();
                    Statement stmt2 = c.createStatement();
                    
                    ResultSet resultSetPlayers = stmt1.executeQuery("SELECT G.id, G.username, G.level FROM GAME_ACCOUNT G, PLAYER P WHERE G.username = '" + username + "' AND G.password = '" + password + "' AND G.id = P.id");
                    ResultSet resultSetModerators = stmt2.executeQuery("SELECT G.id, G.username, G.level FROM GAME_ACCOUNT G, MODERATOR M WHERE G.username = '" + username + "' AND G.password = '" + password + "' AND G.id = M.id");
                    
                    if (resultSetPlayers.next()) {
                        
                        Main.currentUserAccountId = resultSetPlayers.getInt("id");
                        Main.currentUserUsername = username;
                        Main.currentUserLevel = resultSetPlayers.getInt("level");
                        Main.currentUserIsModerator = false;
                        
                        Main.loadPlayer();
                        
                        // Valid login, so noSuchLogin becomes false
                        noSuchLogin = false;
                        
                    } else if (resultSetModerators.next()) {
                        
                        Main.currentUserAccountId = resultSetModerators.getInt("id");
                        Main.currentUserUsername = username;
                        Main.currentUserLevel = resultSetModerators.getInt("level");
                        Main.currentUserIsModerator = true;
                        
                        Main.loadPlayer();
                        
                        // Valid login, so noSuchLogin becomes false
                        noSuchLogin = false;
                        
                    } else {
                                  
                        Statement stmt = c.createStatement();
                        ResultSet resultSetServerManagers = stmt.executeQuery("SELECT S.id, S.username FROM SERVER_MANAGER S WHERE S.username = '" + username + "' AND S.password = '" + password + "'");
                        
                        if (resultSetServerManagers.next()) {
                            
                            Main.currentUserAccountId = resultSetServerManagers.getInt("id");
                            Main.currentUserUsername = username;
                            
                            Main.loadServerManager();
                            
                            // Valid login, so noSuchLogin becomes false
                            noSuchLogin = false;
                            
                        }
                        
                    }
                    
                    c.close();
                    
                }
                
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
            
            if (noSuchLogin)
                labelMessage.setText("Wrong username or password");
            
        }
        
    }
    
}

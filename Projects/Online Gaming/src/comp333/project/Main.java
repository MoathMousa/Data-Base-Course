package comp333.project;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;

public class Main extends Application {
    
    // Everything here is static to make access from other classes directly from the Main class
    
    // Essential account data used in all other classes
    public static int currentUserAccountId;
    public static String currentUserUsername;
    public static int currentUserLevel;
    public static boolean currentUserIsModerator;
    // isMember and isBanned are not put here, as we check for these at specific actions,
    // and they aren't fixed as long as the player logged in, like those 4 variables above.
    
    public static Image[][][] carImages; // carImages is used in PaneCarSelection
    
    // ConstantHebros
    public static final int WINDOW_WIDTH = 915;
    public static final int WINDOW_HEIGHT = 600;
    public static final int CAR_IMAGE_WIDTH = 500;
    public static final int CAR_IMAGE_HEIGHT = 270;
    public static final String DATABASE_SERVER = "jdbc:mysql://localhost:3306/project?useSSL=false";
    
    // Two AudioClips for button sounds
    public static AudioClip buttonHoverSound;
    public static AudioClip buttonClickSound;
    
    // CSS Style sheet file
    private static final File FILE_STYLE_SHEET = new File("Stylesheet.css");
    
    // Labels
    private static Label labelTop = new Label("Sign In");
    private static Label labelLevel = new Label();
    private static Label labelUsername = new Label();
    
    // driverImageView for the image for player's profile (chaffuer)
    private static ImageView driverImageView = new ImageView(new Image(Main.class.getResource("drivericon.jpg").toExternalForm()));
    
    private static FlowPane paneHolder; // paneHolder holds the other pane classes (PaneSignIn, PaneSignUp, etc...)
    
    private static VBox root; // Main pane that the scene will hold
    private static Scene scene;
    
    private static HBox labelTopPane1; // The top pane that contains only text ("Sign In" or "Sign Up")
    private static BorderPane labelTopPane2; // The top pane that contains the driver image, player's username and level
    
    @Override
    public void start(Stage primaryStage) {
        
        loadCarPictures();
        
        // Load Sounds
        buttonHoverSound =
            new AudioClip(getClass().getResource("Hover.wav").toString());
        buttonClickSound  =
            new AudioClip(getClass().getResource("Click.wav").toString());
        
        labelTopPane1 = new HBox(labelTop);
        
        VBox temp1 = new VBox(3, labelUsername, labelLevel); // Temporary pane to build labelTopPane2
        HBox temp2 = new HBox(2, driverImageView, temp1); // Temporary pane to build labelTopPane2
        labelTopPane2 = new BorderPane(null, null, temp2, null, null); // Make temp2 on east to make it on right
        labelTopPane2.setPadding(new Insets(10));
        
        labelTopPane1.setMaxWidth(WINDOW_WIDTH);
        labelTopPane2.setMaxWidth(WINDOW_WIDTH);
        
        // The first pane must be PaneSignIn
        paneHolder = new FlowPane(new PaneSignIn());
        paneHolder.setAlignment(Pos.CENTER);
    
        paneHolder.setMaxWidth(Main.WINDOW_WIDTH);
        paneHolder.setMaxHeight(Main.WINDOW_HEIGHT);
        
        root = new VBox(3, labelTopPane1, paneHolder);
        root.setAlignment(Pos.CENTER);
        
        // CSS stuff. An object with id "name" will have the style of #name found in the style sheet file
        root.setId("backpane");
        labelTop.setId("labeltitle1");
        labelUsername.setId("labeltitle2");
        labelLevel.setId("labellevel");
        paneHolder.setId("holderpane");
        labelTopPane1.setId("holderpane");
        labelTopPane2.setId("holderpane");
        
        scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        
        scene.getStylesheets().clear();
        scene.getStylesheets().add("file:///" + FILE_STYLE_SHEET.getAbsolutePath().replace("\\", "/"));
        
        primaryStage.setTitle("COMP333");
        primaryStage.setScene(scene);
    
        primaryStage.setFullScreen(true);
        primaryStage.show();
    
    }
    
    public static void loadSignIn() {
        
        driverImageView.setImage(null);
        labelTop.setText("Sign In");
        root.getChildren().set(0, labelTopPane1);
        paneHolder.getChildren().set(0, new PaneSignIn());
    
        scene.setRoot(root);
        
    }
    
    public static void loadSignUp() {
        
        driverImageView.setImage(null);
        labelTop.setText("Sign Up");
        root.getChildren().set(0, labelTopPane1);
        paneHolder.getChildren().set(0, new PaneSignUp());
        
        scene.setRoot(root);
        
    }
    
    // Load the screen with server manager controls
    public static void loadServerManager() {
        
        root.getChildren().set(0, labelTopPane2);
        labelUsername.setText(currentUserUsername);
        labelLevel.setText("Server Manager");
        
        paneHolder.getChildren().set(0, new PaneServerManager());
        
        scene.setRoot(root);
        
    }
    
    // Load the screen with player controls
    public static void loadPlayer() {
        
        root.getChildren().set(0, labelTopPane2);
        labelUsername.setText(currentUserUsername);
        labelLevel.setText("Level: " + currentUserLevel);
        paneHolder.getChildren().set(0, new PanePlayer());
        
        scene.setRoot(root);
        
    }
    
    // Load the black screen with "Playing..." printed
    public static void loadPlaying() {
        
        scene.setRoot(new PanePlaying());
        
    }
    
    // Checks if the player with id = currentUserAccountId is a member or not.
    // Note that this returns String, not boolean, because we will return the time
    // of the membership, if it was there. Returning an empty string implies no membership.
    public static String checkPlayerMembership() {
        
        // Moderators are members for free
        if (currentUserIsModerator)
            return "M"; // Returning a non-empty string means a member
        
        String returnValue = ""; // Assume initially that the player isn't a member (empty string means not a member)
        try {
            
            Connection c = DriverManager.getConnection(Main.DATABASE_SERVER, "root", "admin");
            
            if (c != null) {
                
                // Query if the player has a relation with the membership class and get the time column
                Statement stmt = c.createStatement();
                ResultSet resultSetMembership = stmt.executeQuery("SELECT M.time FROM MEMBERSHIP M WHERE M.id = " + currentUserAccountId);
                
                // If the ResultSet is non-empty, then the player is a member and set returnValue to that time
                if (resultSetMembership.next())
                    returnValue = resultSetMembership.getString("time");
                
                c.close();
                
            }
            
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        
        return returnValue;
        
    }
    
    // Checks if the player with id = currentUserAccountId is banned or not
    public static boolean checkPlayerBan() {
        
        // Moderators cannot be banned
        if (currentUserIsModerator)
            return false;
        
        boolean returnValue = true;
        try {
            
            Connection c = DriverManager.getConnection(Main.DATABASE_SERVER, "root", "admin");
            
            if (c != null) {
                
                // Query if the player has a relation a moderator
                Statement stmt = c.createStatement();
                ResultSet resultSetMembership = stmt.executeQuery("SELECT * FROM PLAYER P WHERE P.id = " + currentUserAccountId + " AND P.banned_by IS NOT NULL");
                
                // If the ResultSet is empty, then the player is not banned
                if (!resultSetMembership.next())
                    returnValue = false;
                
                c.close();
                
            }
            
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        
        return returnValue;
        
    }
    
    private void loadCarPictures() {
        
        carImages = new Image[8][8][5];
        // carImages[i][j][k] means the image of ith car, jth angle and kth color
        
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                for (int k = 0; k < 5; k++)
                    carImages[i][j][k] =
                            new Image(getClass().getResource("carimages/" + i + "" + j + "" + k + ".jpg").toExternalForm(), CAR_IMAGE_WIDTH, CAR_IMAGE_HEIGHT, false, false);     
        
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
}

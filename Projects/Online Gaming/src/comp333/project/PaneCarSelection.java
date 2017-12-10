package comp333.project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class PaneCarSelection extends VBox {
    
    public static final int BUTTON_WIDTH = 60;
    public static final int BUTTON_HEIGHT = 105;
    public static final int COLOR_DIMENSION = 40;
    public static final boolean LEFT = false;
    public static final boolean RIGHT = true;
    
    private HBox rowColors;
    private ImageView carImageView = new ImageView();
    
    private int carIndex = 0; // Which car?
    private int angleIndex = 0; // Which angle?
    private int colorIndex = 0; // Which color?
    
    public PaneCarSelection() {
        
        try {
            
            Connection c = DriverManager.getConnection(Main.DATABASE_SERVER, "root", "admin");
            
            if (c != null) {
                
                // Get the current selection from the database for this player
                
                Statement stmt = c.createStatement();
                ResultSet resultSetSelection = stmt.executeQuery("SELECT G.sel_car_no, G.sel_color_no FROM GAME_ACCOUNT G WHERE G.id = " + Main.currentUserAccountId);
                resultSetSelection.next();
                
                carIndex = resultSetSelection.getInt("sel_car_no");
                colorIndex = resultSetSelection.getInt("sel_color_no");
                
                c.close();
                
            }
            
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        
        selectCar();
        
        // Car Buttons
        ButtonM bt1 = new ButtonM("1", BUTTON_WIDTH, BUTTON_HEIGHT);
        ButtonM bt2 = new ButtonM("2", BUTTON_WIDTH, BUTTON_HEIGHT);
        ButtonM bt3 = new ButtonM("3", BUTTON_WIDTH, BUTTON_HEIGHT);
        ButtonM bt4 = new ButtonM("4", BUTTON_WIDTH, BUTTON_HEIGHT);
        ButtonM bt5 = new ButtonM("5", BUTTON_WIDTH, BUTTON_HEIGHT);
        ButtonM bt6 = new ButtonM("6", BUTTON_WIDTH, BUTTON_HEIGHT);
        ButtonM bt7 = new ButtonM("7", BUTTON_WIDTH, BUTTON_HEIGHT);
        ButtonM bt8 = new ButtonM("8", BUTTON_WIDTH, BUTTON_HEIGHT);
        
        bt1.setOnAction(e -> { buttonActionSelection(0); });
        bt2.setOnAction(e -> { buttonActionSelection(1); });
        bt3.setOnAction(e -> { buttonActionSelection(2); });
        bt4.setOnAction(e -> { buttonActionSelection(3); });
        bt5.setOnAction(e -> { buttonActionSelection(4); });
        bt6.setOnAction(e -> { buttonActionSelection(5); });
        bt7.setOnAction(e -> { buttonActionSelection(6); });
        bt8.setOnAction(e -> { buttonActionSelection(7); });
        
        // Color Buttons
        ButtonM bt1C = new ButtonM("", COLOR_DIMENSION, COLOR_DIMENSION);
        ButtonM bt2C = new ButtonM("", COLOR_DIMENSION, COLOR_DIMENSION);
        ButtonM bt3C = new ButtonM("", COLOR_DIMENSION, COLOR_DIMENSION);
        ButtonM bt4C = new ButtonM("", COLOR_DIMENSION, COLOR_DIMENSION);
        ButtonM bt5C = new ButtonM("", COLOR_DIMENSION, COLOR_DIMENSION);
        
        bt1C.setOnAction(e -> { buttonActionColor(0); });
        bt2C.setOnAction(e -> { buttonActionColor(1); });
        bt3C.setOnAction(e -> { buttonActionColor(2); });
        bt4C.setOnAction(e -> { buttonActionColor(3); });
        bt5C.setOnAction(e -> { buttonActionColor(4); });
        
        // Rotate Buttons
        ButtonM rotLeft = new ButtonM("<", COLOR_DIMENSION, COLOR_DIMENSION);
        ButtonM rotRight = new ButtonM(">", COLOR_DIMENSION, COLOR_DIMENSION);
        
        rotLeft.setOnAction(e -> { buttonActionRotation(LEFT); });
        rotRight.setOnAction(e -> { buttonActionRotation(RIGHT); });
        
        bt1.setStyle("-fx-background-radius: 12;");
        bt2.setStyle("-fx-background-radius: 12;");
        bt3.setStyle("-fx-background-radius: 12;");
        bt4.setStyle("-fx-background-radius: 12;");
        bt5.setStyle("-fx-background-radius: 12;");
        bt6.setStyle("-fx-background-radius: 12;");
        bt7.setStyle("-fx-background-radius: 12;");
        bt8.setStyle("-fx-background-radius: 12;");
        
        bt1C.setStyle("-fx-background-color: yellow;");
        bt2C.setStyle("-fx-background-color: rgb(30, 30, 30);");
        bt3C.setStyle("-fx-background-color: blue;");
        bt4C.setStyle("-fx-background-color: white;");
        bt5C.setStyle("-fx-background-color: green;");
        
        carImageView.setStyle("-fx-border-color: cyan; -fx-border-width: 3; -fx-opacity: 1.0;");
        
        HBox row1 = new HBox(8, bt1, bt2, bt3, bt4, bt5, bt6, bt7, bt8);
        HBox row2 = new HBox(16, rotLeft, carImageView, rotRight);
        rowColors = new HBox(8, bt1C, bt2C, bt3C, bt4C, bt5C);
        
        row1.setAlignment(Pos.CENTER);
        row2.setAlignment(Pos.CENTER);
        rowColors.setAlignment(Pos.CENTER);
        
        // Delete car buttons that the player's level isn't up enough to them
        for (int i = 7; i >= Main.currentUserLevel; i--)
            row1.getChildren().get(i).setVisible(false);
        
        // The last 2 car buttons is only available to members.
        if (Main.checkPlayerMembership().isEmpty()) { // If an empty string is returned, then not a member.
            row1.getChildren().get(7).setVisible(false);
            row1.getChildren().get(6).setVisible(false);
        }
        
        selectCar();
        
        this.setSpacing(20);
        this.getChildren().addAll(row1, row2, rowColors);
        this.setPadding(new Insets(1, 25, 25, 25));
        
    }

    private void buttonActionSelection(int index) {
        carIndex = index;
        selectCar();
    }
    
    private void buttonActionRotation(boolean right) {
        
        // !right = left
        angleIndex = right ? angleIndex = (angleIndex + 1) % 8 : (angleIndex == 0 ? 7 : angleIndex - 1);
        selectCar();
    }
    
    private void buttonActionColor(int index) {
        colorIndex = index;
        selectCar();
    }
    
    private void selectCar() {
        
        carImageView.setImage(Main.carImages[carIndex][angleIndex][colorIndex]);
        
        try {
            
            Connection c = DriverManager.getConnection(Main.DATABASE_SERVER, "root", "admin");
            
            if (c != null) {
                
                // Update the current selection for this player
                Statement stmt = c.createStatement();
                stmt.executeUpdate("UPDATE GAME_ACCOUNT SET sel_car_no = " + carIndex + ", sel_color_no = " + colorIndex + " WHERE id = " + Main.currentUserAccountId);
                c.close();
                
            }
            
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        
    }
    
}

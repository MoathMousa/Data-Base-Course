package comp333.project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class PanePlaying extends BorderPane {

    public PanePlaying() {
        
        // Buttons
        ButtonM btnExit = new ButtonM("Exit", 225);
        btnExit.setOnAction(e -> { buttonActionExit(); });
        
        // Labels
        Label labelPlaying = new Label("Playing...");
        labelPlaying.setStyle("-fx-text-fill: white; -fx-font-family: \"Arial Black\"; -fx-font-size: 32;");
        
        // Panes
        HBox pane1 = new HBox(labelPlaying);
        VBox pane2 = new VBox(20, btnExit);
        
        // Alignment and stuff
        
        pane1.setAlignment(Pos.CENTER);
        pane2.setAlignment(Pos.CENTER);
        
        this.setCenter(pane1);
        this.setBottom(pane2);
        this.setStyle("-fx-background-color: black;");
        this.setPadding(new Insets(50));
        //BorderPane fullPane = new BorderPane(pane1, null, null, pane2, null);
        
    }
        
    private void buttonActionExit() {
        
        try {
            
            Connection c = DriverManager.getConnection(Main.DATABASE_SERVER, "root", "admin");
            
            if (c != null) {
                
                // Remove the relation between this player and the server to indicate he isn't playing.
                Statement stmt = c.createStatement();
                stmt.executeUpdate("UPDATE GAME_ACCOUNT SET server_id = NULL WHERE id = " + Main.currentUserAccountId);
                    
                c.close();
                
            }
            
            Main.loadPlayer();
            
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        
    }
    
}

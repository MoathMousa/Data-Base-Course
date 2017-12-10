package comp333.project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class PaneMembership extends VBox {
    
    private ComboBox<String> comboxDu;
    private Label labelRemTime; // Label with remaining time
    private ButtonM btnBuy;
    
    public PaneMembership() {
    
        // Moderators are given free membership so don't show controls
        if (Main.currentUserIsModerator) {
        
            HBox row = new HBox(new Label("You are a game moderator, you are\ngranted free membership"));
            row.setPrefWidth(PanePlayer.OPTION_WIDTH);
            row.setAlignment(Pos.CENTER);
            row.getChildren().get(0).setStyle("-fx-text-alignment: center; -fx-font-size: 20");
            
            this.getChildren().add(row);
            
        } else {
            btnBuy = new ButtonM("Buy", 225);
            
            String membershipTime = Main.checkPlayerMembership();
            // Check for membership
            if (membershipTime.isEmpty())
                // Empty string returned, then not a member.
                labelRemTime = new Label("Remaining Time: -");
            else {
                // Non-Empty string, so member and we have the membership's time.
                switch (membershipTime) {
                    case "week":
                        labelRemTime = new Label("Remaining Time: 7 days");
                        break;
                    case "month":
                        labelRemTime = new Label("Remaining Time: 30 days");
                        break;
                    case "year":
                        labelRemTime = new Label("Remaining Time: 365 days");
                        break;
                    default:
                }
                // No need for btnBuy
                btnBuy.setVisible(false);
            }
            
            btnBuy.setOnAction(e -> { buttonActionBuy(); });
            
            Label labelBuy = new Label("Select Duration:");
            
            comboxDu = new ComboBox<>();
            comboxDu.getItems().add("Week - $2.99");
            comboxDu.getItems().add("Month - $9.99");
            comboxDu.getItems().add("Year - $99.99");
            
            HBox row1 = new HBox(labelRemTime);
            HBox row2 = new HBox(15, labelBuy, comboxDu);
            HBox row3 = new HBox(btnBuy);
            
            row1.setAlignment(Pos.CENTER_LEFT);
            row2.setAlignment(Pos.CENTER_LEFT);
            row3.setAlignment(Pos.CENTER_LEFT);
            
            this.setAlignment(Pos.CENTER);
            this.getChildren().addAll(new Label(), new Label(), row1, row2, row3);
            this.setSpacing(50);
            
        }
        
    }
    
    private void buttonActionBuy() {
        
        int selectedIndex = comboxDu.getSelectionModel().getSelectedIndex();
        
        if (!comboxDu.getItems().isEmpty() && selectedIndex != -1) {
            
            try {
                
                Connection c = DriverManager.getConnection(Main.DATABASE_SERVER, "root", "admin");
                
                if (c != null) {
                    
                    Statement stmt = c.createStatement();
                    
                    switch(selectedIndex) {
                        
                        case 0: // Selected Week
                            stmt.executeUpdate("INSERT INTO MEMBERSHIP (`id`, `since`, `time`) VALUES (" + Main.currentUserAccountId + ", NULL, 'week')");
                            labelRemTime.setText("Remaining Time: 7 days");
                            break;
                        case 1: // Selected Month
                            stmt.executeUpdate("INSERT INTO MEMBERSHIP (`id`, `since`, `time`) VALUES (" + Main.currentUserAccountId + ", NULL, 'month')");
                            labelRemTime.setText("Remaining Time: 30 days");
                            break;
                        case 2: // Selected Year
                            stmt.executeUpdate("INSERT INTO MEMBERSHIP (`id`, `since`, `time`) VALUES (" + Main.currentUserAccountId + ", NULL, 'year')");
                            labelRemTime.setText("Remaining Time: 365 days");
                            break;
                        default:
                    }
                    
                    btnBuy.setVisible(false);
                    
                    c.close();
                    
                }
                
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
            
        }
        
    }
    
}

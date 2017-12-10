package comp333.project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class PaneBan extends VBox {
    
    private TextField textSearch;
    private ListView<String> listPlayers;
    private Label labelMessage;
    
    // List of player numbers
    private LinkedList<Integer> listPlayerNumbers;
    
    public PaneBan() {
        
        if (!Main.currentUserIsModerator) {
            
            // Normal players can't ban so don't show controls
            HBox row = new HBox(new Label("This option is only available\nto game moderators"));
            row.setPrefWidth(PanePlayer.OPTION_WIDTH);
            row.setAlignment(Pos.CENTER);
            row.getChildren().get(0).setStyle("-fx-text-alignment: center; -fx-font-size: 20");
            
            this.getChildren().add(row);
            
        } else {
            
            // Buttons
            ButtonM buttonBan = new ButtonM("Ban", 225);
            ButtonM buttonUnban = new ButtonM("Unban", 225);
            ButtonM buttonSearch = new ButtonM("Search");
            
            buttonBan.setOnAction(e -> { buttonActionBan(); });
            buttonUnban.setOnAction(e -> { buttonActionUnban(); });
            buttonSearch.setOnAction(e -> { buttonActionSearch(); });
            
            labelMessage = new Label();
            
            textSearch = new TextField();
            
            listPlayers = new ListView<>();
            listPlayerNumbers = new LinkedList<>();
            
            HBox row1 = new HBox(5, textSearch, buttonSearch);
            HBox row2 = new HBox(listPlayers);
            HBox row3 = new HBox(10, buttonBan, buttonUnban);
            HBox row4 = new HBox(labelMessage);
            
            row1.setAlignment(Pos.CENTER);
            row2.setAlignment(Pos.CENTER);
            row3.setAlignment(Pos.CENTER);
            row4.setAlignment(Pos.CENTER);
            
            listPlayers.setPrefWidth(PanePlayer.OPTION_WIDTH);
            listPlayers.setPrefHeight(200);
            
            this.setAlignment(Pos.CENTER);
            this.getChildren().addAll(row1, row2, row3, row4);
            this.setSpacing(40);
            
        }
        
    }
    
    private void buttonActionSearch() {
        
        listPlayers.getItems().clear();
        listPlayerNumbers.clear();
        
        try {
            
            Connection c = DriverManager.getConnection(Main.DATABASE_SERVER, "root", "admin");
            
            if (c != null) {
                
                Statement stmt = c.createStatement();
                ResultSet resultSetPlayers;
                
                String searchForWhat = textSearch.getText().trim();
                
                // If searchForWhat is empty, this means don't search and bring all entries,
                // else search
                if (!searchForWhat.isEmpty())
                    resultSetPlayers = stmt.executeQuery("SELECT G.username, P.player_number FROM GAME_ACCOUNT G, PLAYER P WHERE G.username like '%" + searchForWhat + "%' AND G.id = P.id");
                else
                    resultSetPlayers = stmt.executeQuery("SELECT G.username, P.player_number FROM GAME_ACCOUNT G, PLAYER P WHERE G.id = P.id");
                
                int i = 1;
                
                while (resultSetPlayers.next()) {
                    listPlayers.getItems().add(i++ + "- " + resultSetPlayers.getString("username"));
                    listPlayerNumbers.addLast(resultSetPlayers.getInt("player_number"));
                }
                
                c.close();
            }
            
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        
    }
    
    private void buttonActionBan() {
        
        int selectedIndex = listPlayers.getSelectionModel().getSelectedIndex();
        
        if (!listPlayers.getItems().isEmpty() && selectedIndex != -1) {
            
            try {
                
                Connection c = DriverManager.getConnection(Main.DATABASE_SERVER, "root", "admin");
                
                if (c != null) {
                    
                    int selectedPlayerNumber = listPlayerNumbers.get(selectedIndex);
                    
                    Statement stmt1 = c.createStatement();
                    // Check if the selected player is already banned (Get row from table)
                    ResultSet resultSetBannedPlayer = stmt1.executeQuery("SELECT player_number FROM PLAYER P WHERE P.player_number = " + selectedPlayerNumber + " AND P.banned_by IS NOT NULL");
                    
                    if (resultSetBannedPlayer.next())
                        labelMessage.setText("This player is already banned");
                    else {
                        
                        // Player is not banned
                        Statement stmt2 = c.createStatement();
                        // Set the banned_by column in PLAYER table to this account's id to make him/her banned
                        stmt2.executeUpdate("UPDATE PLAYER SET banned_by = " + Main.currentUserAccountId + " WHERE player_number = " + selectedPlayerNumber);
                        labelMessage.setText("Player banned succesfully");
                        
                    }
                    
                    c.close();
                }
                
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
            
        }
        
    }
    
    private void buttonActionUnban() {
        
        // The moderator account can only ban players banned by him/her.
        int selectedIndex = listPlayers.getSelectionModel().getSelectedIndex();
        
        if (!listPlayers.getItems().isEmpty() && selectedIndex != -1) {
            
            try {
                
                Connection c = DriverManager.getConnection(Main.DATABASE_SERVER, "root", "admin");
                
                if (c != null) {
                    
                    int selectedPlayerNumber = listPlayerNumbers.get(selectedIndex);
                    
                    Statement stmt1 = c.createStatement();
                    // Get the row with the selected player number and banned by this account
                    ResultSet resultSetBannedPlayer = stmt1.executeQuery("SELECT player_number FROM PLAYER P WHERE P.player_number = " + selectedPlayerNumber + " AND P.banned_by = " + Main.currentUserAccountId);
                    
                    if (resultSetBannedPlayer.next()) {
                        // If the ResultSet is not empty, this means the selected player is banned by this account, so can unban.
                        Statement stmt2 = c.createStatement();
                        
                        // Set banned_by to NULL for this player (Unban).
                        stmt2.executeUpdate("UPDATE PLAYER SET banned_by = NULL WHERE player_number = " + selectedPlayerNumber);
                        labelMessage.setText("Player unbanned successfully");
                        
                    } else labelMessage.setText("This player is not banned or not banned by you"); // Empty set means not banned, or not banned by this account.
                    
                    c.close();
                }
                
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        
        }
        
    }
    
}

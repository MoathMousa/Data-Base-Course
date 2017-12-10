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

public class PaneServers extends VBox {
    
    private TextField textSearch;
    private ListView<String> listServers;
    private Label labelMessage;
    
    // Lists that stores server ids and capacities
    private LinkedList<Integer> listServerIds;
    private LinkedList<Integer> listServerCapacities;
    
    public PaneServers() {
        
        // Buttons
        ButtonM buttonPlay = new ButtonM("Play", 225);
        ButtonM buttonSearch = new ButtonM("Search");
        
        buttonSearch.setOnAction(e -> { buttonActionSearch(); });
        buttonPlay.setOnAction(e -> { buttonActionPlay(); });
        
        listServers = new ListView<>();
        listServerIds = new LinkedList<>();
        listServerCapacities = new LinkedList<>();
        
        labelMessage = new Label("");
            
        textSearch = new TextField();
        
        listServers.setPrefWidth(PanePlayer.OPTION_WIDTH);
        listServers.setPrefHeight(200);
        
        HBox row1 = new HBox(5, textSearch, buttonSearch);
        HBox row2 = new HBox(listServers);
        HBox row3 = new HBox(buttonPlay);
        HBox row4 = new HBox(labelMessage);
        
        row1.setAlignment(Pos.CENTER);
        row2.setAlignment(Pos.CENTER);
        row3.setAlignment(Pos.CENTER);
        row4.setAlignment(Pos.CENTER);
        
        // Banned players cannot play so remove play button
        if (Main.checkPlayerBan())
            buttonPlay.setVisible(false);
        
        this.setAlignment(Pos.CENTER);
        this.getChildren().addAll(row1, row2, row3, row4);
        this.setSpacing(40);
        
    }
    
    private void buttonActionSearch() {
        
        listServers.getItems().clear();
        listServerIds.clear();
        listServerCapacities.clear();
        
        try {
            
            Connection c = DriverManager.getConnection(Main.DATABASE_SERVER, "root", "admin");
            
            if (c != null) {
                
                Statement stmt = c.createStatement();
                ResultSet resultSetServers;
                
                String searchForWhat = textSearch.getText().trim();
                
                // If searchForWhat is empty, this means don't search and bring all entries,
                // else search
                if (!textSearch.getText().isEmpty())
                    resultSetServers = stmt.executeQuery("SELECT S.name, S.capacity, S.id FROM SERVER S WHERE S.name like '%" + searchForWhat + "%'");
                else
                    resultSetServers = stmt.executeQuery("SELECT S.name, S.capacity, S.id FROM SERVER S");
                
                int i = 1;
                
                while (resultSetServers.next()) {
                    listServers.getItems().add(i++ + "- " + resultSetServers.getString("name") + " (" + resultSetServers.getInt("capacity") + ")");
                    listServerIds.addLast(resultSetServers.getInt("id"));
                    listServerCapacities.addLast(resultSetServers.getInt("capacity"));
                }
                c.close();
            }
            
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        
    }
    
    private void buttonActionPlay() {
        
        int selectedIndex = listServers.getSelectionModel().getSelectedIndex();
        
        if (!listServers.getItems().isEmpty() && selectedIndex != -1) {
            
            try {
                
                Connection c = DriverManager.getConnection(Main.DATABASE_SERVER, "root", "admin");
                
                if (c != null) {
                    
                    int selectedServerId = listServerIds.get(selectedIndex);
                    int selectedServerCapacity = listServerCapacities.get(selectedIndex);
                    
                    // In order to play, we must first check the server's capacity and if the currently playing number is less.

                    Statement stmt1 = c.createStatement();
                    // The number of players in a relation with the server is the number of currently playing in this server
                    ResultSet resultSetNumberOfPlaying = stmt1.executeQuery("SELECT COUNT(*) FROM GAME_ACCOUNT G WHERE G.server_id = " + selectedServerId);
                    resultSetNumberOfPlaying.next();
                    int numberOfPlaying = resultSetNumberOfPlaying.getInt(1); // Get first column's value
                    
                    if (numberOfPlaying < selectedServerCapacity) {
                        // Able to play
                        Statement stmt2 = c.createStatement();
                        // Make relation between this player and the server
                        stmt2.executeUpdate("UPDATE GAME_ACCOUNT SET server_id = " + selectedServerId + " WHERE id = " + Main.currentUserAccountId);
                        Main.loadPlaying();
                        
                    } else labelMessage.setText("Server is full");
                    
                    c.close();
                }
                
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
            
        }
        
    }
    
}

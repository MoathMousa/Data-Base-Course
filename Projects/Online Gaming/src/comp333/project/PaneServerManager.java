package comp333.project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class PaneServerManager extends BorderPane {
    
    // Constants
    public static final int OPTION_WIDTH = 700;
    public static final int OPTION_HEIGHT = 400;
    
    // holderPane is the pane that holds other panes only avaliable to PaneServerManager
    private VBox holderPane;
    
    private TextField textSearch;
    private TextField textServerToAddName;
    private TextField textServerToAddCapacity;
    
    private ListView<String> listServers;
    private LinkedList<Integer> listServerIds;
    
    private HBox rowAddServer; // Contains the objects to add a server

    // The strucutre of this pane is very similar to PanePlayer
    public PaneServerManager() {
    
        // Buttons
        ButtonM btnAddServer = new ButtonM("Add Server", 175, 15); // This buttons shows/unshows rowAddServer
        ButtonM btnDeleteServer = new ButtonM("Delete Server", 175, 15);
        ButtonM btnExit = new ButtonM("Exit", 175, 15);
        ButtonM btnSearch = new ButtonM("Search");
        ButtonM btnMiniAddServer = new ButtonM("Add"); // This button is in rowAddServer
        
        btnAddServer.setOnAction(e -> { buttonActionAddServer(); });
        btnDeleteServer.setOnAction(e -> { buttonActionDeleteServer(); });
        btnExit.setOnAction(e -> { Main.loadSignIn(); });
        btnSearch.setOnAction(e -> { buttonActionSearch(); });
        btnMiniAddServer.setOnAction(e -> { buttonActionMiniAddServer(); });
        
        // Text fields
        textSearch = new TextField();
        textServerToAddName = new TextField(); // Found in rowAddServer
        textServerToAddCapacity = new TextField(); // Found in rowAddServer
        
        textServerToAddName.setPromptText("Name");
        textServerToAddCapacity.setPromptText("Capacity");
        textServerToAddCapacity.setPrefWidth(100);
        
        listServers = new ListView<>();
        listServerIds = new LinkedList<>();
        
        ImageView logo = new ImageView(new Image(getClass().getResource("cap.png").toExternalForm()));
        
        VBox paneButtons = new VBox(20, logo, btnAddServer, btnDeleteServer, btnExit);
        paneButtons.setId("playeroptionpane");
        
        HBox row1 = new HBox(5, textSearch, btnSearch);
        HBox row2 = new HBox(listServers);
        rowAddServer = new HBox(5, textServerToAddName, textServerToAddCapacity, btnMiniAddServer);
        
        row1.setAlignment(Pos.CENTER);
        row2.setAlignment(Pos.CENTER);
        rowAddServer.setAlignment(Pos.CENTER);
        rowAddServer.setVisible(false);
        
        // holderPane is the pane that holds other panes only available to PanePlayer
        holderPane = new VBox(30, row1, row2, rowAddServer);
        // scrollPane is the pane that holds holderPane with scrolling
        ScrollPane scrollPane = new ScrollPane(holderPane);
        // optionPane is the pane that holds scrollPane
        FlowPane optionPane = new FlowPane(scrollPane);
        
        listServers.setPrefWidth(OPTION_WIDTH - 30);
        listServers.setPrefHeight(200);
        
        holderPane.setAlignment(Pos.CENTER);
        
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setPrefWidth(OPTION_WIDTH - 20);
        scrollPane.setPrefHeight(OPTION_HEIGHT - 20);
        
        optionPane.setId("playeroptionpane");
        optionPane.setAlignment(Pos.CENTER);
        optionPane.setPrefWidth(OPTION_WIDTH);
        optionPane.setPrefHeight(OPTION_HEIGHT);    
        
        this.setMargin(optionPane, new Insets(5));
        this.setMargin(paneButtons, new Insets(5));
        this.setCenter(optionPane);
        this.setLeft(paneButtons);
        
    }
    
    private void buttonActionAddServer() {
        
        // Show or hide rowAddServer
        if (rowAddServer.isVisible())
            rowAddServer.setVisible(false);
        else
            rowAddServer.setVisible(true);
        
    }
    
    private void buttonActionDeleteServer() {
        
        int selectedIndex = listServers.getSelectionModel().getSelectedIndex();
        
        if (!listServers.getItems().isEmpty() && selectedIndex != -1) {
            
            listServers.getItems().remove(selectedIndex);
        
            try {
                
                Connection c = DriverManager.getConnection(Main.DATABASE_SERVER, "root", "admin");
                
                if (c != null) {
                    
                    int serverId = listServerIds.get(selectedIndex);
                    
                    Statement stmt = c.createStatement();
                    
                    stmt.executeUpdate("DELETE FROM SERVER WHERE id = " + serverId);
                    listServerIds.remove(serverId);
                    c.close();
                
                }
                
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        
        }
        
    }
    
    private void buttonActionSearch() {
        
        listServers.getItems().clear();
        listServerIds.clear();
        
        try {
            
            Connection c = DriverManager.getConnection(Main.DATABASE_SERVER, "root", "admin");
            
            if (c != null) {
                
                Statement stmt = c.createStatement();
                ResultSet resultSetServers;
                
                String searchForWhat = textSearch.getText().trim().replaceAll("'", "''");
                
                // If searchForWhat is empty, this means don't search and bring all servers created by this manager,
                // else search but only get servers created by this manager
                if (!searchForWhat.isEmpty())
                    resultSetServers = stmt.executeQuery("SELECT S.id, S.name, S.capacity FROM SERVER S WHERE S.name like '%" + searchForWhat + "%' AND S.server_man_id = " + Main.currentUserAccountId);
                else
                    resultSetServers = stmt.executeQuery("SELECT S.id, S.name, S.capacity FROM SERVER S WHERE S.server_man_id = " + Main.currentUserAccountId);
                
                //int i = 1;
                
                while (resultSetServers.next()) {
                    listServers.getItems().add(resultSetServers.getString("name") + " (" + resultSetServers.getInt("capacity") + ")");
                    listServerIds.addLast(resultSetServers.getInt("id"));
                }    
                c.close();                
            }
            
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        
    }
    
    private void buttonActionMiniAddServer() {
        
        String serverToAddName = textServerToAddName.getText().trim().replaceAll("'", "''");
        String serverToAddCapacity = textServerToAddCapacity.getText().trim().replaceAll("'", "''");
        
        if (!serverToAddName.isEmpty() && !serverToAddCapacity.isEmpty()) {
            
            try {
                
                Connection c = DriverManager.getConnection(Main.DATABASE_SERVER, "root", "admin");
                
                if (c != null) {
                    
                    int capacity = Integer.parseInt(textServerToAddCapacity.getText());
                    
                    Statement stmt1 = c.createStatement();
                    // Search for servers with name serverToAddName, if found sth, we can't add this server.
                    // Servers have unique names.
                    ResultSet resultSetServers = stmt1.executeQuery("SELECT S.name FROM SERVER S WHERE S.name = '" + serverToAddName + "'");
                    
                    if (!resultSetServers.next()) {
                        // If didn't find sth we can add
                        
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
                        
                        // First get all of the list of ids
                        Statement stmt2 = c.createStatement();
                        ResultSet resultSetServerIds = stmt2.executeQuery("SELECT S.id FROM SERVER S ORDER BY S.id");
                        
                        int serverToAddId = 0;
                        
                        while (resultSetServerIds.next() && serverToAddId == resultSetServerIds.getInt("id"))
                            serverToAddId++;
                        
                        Statement stmt3 = c.createStatement();
                        stmt3.executeUpdate("INSERT INTO SERVER (`id`, `name`, `capacity`, `server_man_id`) VALUES (" + serverToAddId + ", '" + serverToAddName + "', " + capacity + ", " + Main.currentUserAccountId + ")");
                        
                        listServers.getItems().add((serverToAddName + " (" + capacity + ")"));
                        listServerIds.addLast(serverToAddId);
                        
                    }
                    c.close();
                }
                
            } catch (SQLException ex1) {
                System.out.println(ex1.getMessage());
            } catch (NumberFormatException ex2) {  
            }
            
        }
        
    }
    
}
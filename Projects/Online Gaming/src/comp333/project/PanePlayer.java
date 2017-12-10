package comp333.project;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

public class PanePlayer extends BorderPane {
    
    // Constants
    public static final int OPTION_WIDTH = 700;
    public static final int OPTION_HEIGHT = 520;
    
    // holderPane is the pane that holds other panes only available to PanePlayer
    private FlowPane holderPane;
    
    public PanePlayer() {
        
        // Buttons
        ButtonM btnCar = new ButtonM("Car", 175, 15);
        ButtonM btnForums = new ButtonM("Forums", 175, 15);
        ButtonM btnServers = new ButtonM("Servers", 175, 15);
        ButtonM btnMembership = new ButtonM("Membership", 175, 15);
        ButtonM btnBan = new ButtonM("Ban", 175, 15);
        ButtonM btnExit = new ButtonM("Exit", 175, 15);
        
        btnCar.setOnAction(e -> { loadCarSelection(); });
        btnForums.setOnAction(e -> { loadForums(); });
        btnServers.setOnAction(e -> { loadServers(); });
        btnMembership.setOnAction(e -> { loadMembership(); });
        btnBan.setOnAction(e -> { loadBan(); });
        btnExit.setOnAction(e -> { Main.loadSignIn(); });
        
        ImageView logo = new ImageView(new Image(getClass().getResource("cap.png").toExternalForm()));
        
        // Panes
        
        VBox paneButtons = new VBox(20, logo, btnCar, btnForums, btnServers, btnMembership, btnBan, btnExit);
        paneButtons.setId("playeroptionpane");
        
        // holderPane is the pane that holds other panes only available to PanePlayer
        holderPane = new FlowPane(new PaneCarSelection());
        
        holderPane.setAlignment(Pos.CENTER);
        
        // scrollPane is the pane that holds holderPane with scrolling
        ScrollPane scrollPane = new ScrollPane(holderPane);
        
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setPrefWidth(OPTION_WIDTH - 20);
        scrollPane.setPrefHeight(OPTION_HEIGHT - 20);
        
        // optionPane is the pane that holds scrollPane
        FlowPane optionPane = new FlowPane(scrollPane);
        
        optionPane.setId("playeroptionpane");
        optionPane.setAlignment(Pos.CENTER);
        optionPane.setPrefWidth(OPTION_WIDTH);
        optionPane.setPrefHeight(OPTION_HEIGHT);
        
        
        this.setMargin(optionPane, new Insets(5));
        this.setMargin(paneButtons, new Insets(5));
        this.setCenter(optionPane);
        this.setLeft(paneButtons);
        
    }
    
    public void loadCarSelection() {
        holderPane.getChildren().set(0, new PaneCarSelection());
    }
    
    public void loadForums() {
        holderPane.getChildren().set(0, new PaneForums());
    }
    
    public void loadServers() {
        holderPane.getChildren().set(0, new PaneServers());
    }
    
    public void loadMembership() {
        holderPane.getChildren().set(0, new PaneMembership());
    }
    
    public void loadBan() {
        holderPane.getChildren().set(0, new PaneBan());
    }
    
}

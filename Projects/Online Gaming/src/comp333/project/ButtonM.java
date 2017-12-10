package comp333.project;

import javafx.scene.control.Button;

public class ButtonM extends Button {

    public ButtonM(String text) {
        super(text);
    
        setOnMouseEntered(e -> {
            Main.buttonHoverSound.play();
        });
        
        setOnMousePressed(e -> {
            Main.buttonClickSound.play();
        });
        
    }
    
    public ButtonM(String text, int width) {
        this(text);
        setPrefWidth(width);
    }
    
    public ButtonM(String text, int width, int height) {
        this(text);
        setPrefWidth(width);
        setPrefHeight(height);
    }
    
}

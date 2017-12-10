package comp333.project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class PanePost extends VBox {

    private TextArea textContents;
    private ButtonM buttonEdit;
    private ButtonM buttonDone;
    
    // A head post is the primary post for a thread. If it is deleted, the whole thread deletes too.
    private boolean headPost; // If true, then this post is a head post.
    private int postOwnerId; // The id of the account who posted this post.
    
    public PanePost(int postOwnerId, String content, boolean headPost) {    
        
        this.headPost = headPost;
        this.postOwnerId = postOwnerId;
        
        // Buttons
        ButtonM buttonDelete = new ButtonM("Delete", 185);
        buttonEdit = new ButtonM("Edit", 185);
        buttonDone = new ButtonM("Done", 185);
        
        buttonDelete.setOnAction(e -> { buttonActionDelete(); });
        buttonEdit.setOnAction(e -> { buttonActionEdit(); });
        buttonDone.setOnAction(e -> { buttonActionDone(); });
        
        buttonDone.setVisible(false);
        
        // Only moderators can delete posts. Normal players can't delete posts.
        if (!Main.currentUserIsModerator)
            buttonDelete.setVisible(false);
        
        // Check if the current user is the owner of this post
        // because only owners of their posts can edit them.
        // However, it's an exception for the moderators, as they can edit
        // any post they want.
        // First check if the player isn't a moderator
        if (!Main.currentUserIsModerator)
            // Next check if the player isn't the owner of this post
            if (Main.currentUserAccountId != postOwnerId)
                // The player can't edit this
                buttonEdit.setVisible(false);
                
        Label labelPlayerName = new Label();
        // We want to put the post owner's username so use query.
        try {
            
            Connection c = DriverManager.getConnection(Main.DATABASE_SERVER, "root", "admin");
            
            if (c != null) {
                
                // Get the username of the post owner (from the id)
                
                Statement stmt = c.createStatement();
                ResultSet resultSetAccount = stmt.executeQuery("SELECT G.username FROM GAME_ACCOUNT G WHERE G.id = " + postOwnerId);
                
                resultSetAccount.next();
                
                labelPlayerName.setText(resultSetAccount.getString("username"));
                
                c.close();
                
            }
            
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        
        textContents = new TextArea(content);
        
        textContents.setEditable(false);
        textContents.setPrefWidth(PanePlayer.OPTION_WIDTH - 60);
        textContents.setPrefHeight(200);
        
        HBox row1 = new HBox(labelPlayerName);
        HBox row2 = new HBox(textContents);
        HBox row3 = new HBox(10, buttonDelete, buttonEdit, buttonDone);
        
        row1.setAlignment(Pos.CENTER_LEFT);
        row2.setAlignment(Pos.CENTER);
        row3.setAlignment(Pos.CENTER_RIGHT);
        
        this.setId("postpane");
        this.getChildren().addAll(row1, row2, row3);
        this.setSpacing(7);
        this.setPadding(new Insets(10));
        
    }
    
    private void buttonActionDelete() {
        
        // Check if we deleted the head post (the first post), if so
        // we have to delete the thread all itself.
        
        // Get the parent (PaneForums) to remove posts
        VBox parent = (VBox) PanePost.this.getParent();
        
        if (headPost) {
            
            // We just deleted the head post, so now we have to remove all the thread.
            
            try {
                
                Connection c = DriverManager.getConnection(Main.DATABASE_SERVER, "root", "admin");
                
                if (c != null) {
                    
                    // First, delete all posts that share the same value of thread_id of this post
                    Statement stmt1 = c.createStatement();
                    ResultSet resultSetSameThreadPosts = stmt1.executeQuery("SELECT P.thread_id FROM POST P WHERE P.game_acc_id = " + postOwnerId);
                    
                    resultSetSameThreadPosts.next();
                    
                    int threadId = resultSetSameThreadPosts.getInt("thread_id");
                    
                    Statement stmt2 = c.createStatement();
                    stmt2.executeUpdate("DELETE FROM POST WHERE thread_id = " + threadId);
                    
                    // We deleted all posts for this thread, now delete the thread itself.
                    
                    Statement stmt3 = c.createStatement();
                    stmt3.executeUpdate("DELETE FROM THREAD WHERE id = " + threadId);
                    
                    c.close();
                    
                }
                
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
            
            parent.getChildren().clear();
            
        } else
            parent.getChildren().remove(PanePost.this);
        
        
    }
    
    private void buttonActionEdit() {
        // if the player is the owner of this post {
        
            buttonEdit.setVisible(false);
            buttonDone.setVisible(true);
            
            textContents.setEditable(true);
            
        // }
    }
    
    private void buttonActionDone() {
        
        buttonEdit.setVisible(true);
        buttonDone.setVisible(false);
        
        textContents.setEditable(false);
        // update database
        
    }
    
}

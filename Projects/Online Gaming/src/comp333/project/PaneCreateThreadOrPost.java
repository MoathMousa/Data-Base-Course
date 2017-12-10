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
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class PaneCreateThreadOrPost extends VBox {
    
    // If creatingAThread = true, then we are creating a thread with a post, else we are creating only a post (reply post to a thread).
    private boolean creatingAThread;
    // parentId is the id of the forum if we are creating a thread, or the id of the thread if we are creating a post.
    private int parentId;
    
    private TextArea textContents;
    private TextField textThreadTitle; // Fill in the thread title if we creating a thread
    
    public PaneCreateThreadOrPost(boolean creatingAThread, int parentId) {
        
        this.creatingAThread = creatingAThread;
        this.parentId = parentId;
        
        // Buttons
        ButtonM buttonCreate = new ButtonM("Create", 185);
        ButtonM buttonCancel = new ButtonM("Cancel", 185);
        
        buttonCreate.setOnAction(e -> { buttonActionCreate(); });
        buttonCancel.setOnAction(e -> { buttonActionCancel(); });
        
        // Labels
        Label labelTop = new Label("\tCreate New " + (creatingAThread ? "Thread" : "Post"));
        labelTop.setStyle("-fx-text-size: 15; -fx-border-radius: 3; -fx-border-width: 3; -fx-border-color: darkcyan;");
        
        textContents = new TextArea("");
        textThreadTitle = new TextField("Thread Title");
        
        HBox row1 = new HBox(labelTop);
        HBox row2 = new HBox(textThreadTitle);
        HBox row3 = new HBox(textContents);
        HBox row4 = new HBox(10, buttonCreate, buttonCancel);
        
        row1.setAlignment(Pos.CENTER_LEFT);
        row2.setAlignment(Pos.CENTER);
        row3.setAlignment(Pos.CENTER);
        row4.setAlignment(Pos.CENTER_RIGHT);
        
        labelTop.setPrefWidth(PanePlayer.OPTION_WIDTH - 60);
        textThreadTitle.setPrefWidth(PanePlayer.OPTION_WIDTH - 60);
        textContents.setPrefWidth(PanePlayer.OPTION_WIDTH - 60);
        textContents.setPrefHeight(200);
        
        if (!creatingAThread) // If we aren't creating a thread no need for textThreadTitle
            textThreadTitle.setVisible(false);
        
        this.setId("postpane");
        this.getChildren().addAll(row1, row2, row3, row4);
        this.setSpacing(7);
        this.setPadding(new Insets(10));
        
    }
    
    private void buttonActionCreate() {
        
        String threadTitle = textThreadTitle.getText().trim();
        String contents = textContents.getText().trim();
        
        if (!contents.isEmpty() && !threadTitle.isEmpty()) {
            try {
                
                Connection c = DriverManager.getConnection(Main.DATABASE_SERVER, "root", "admin");
                
                if (c != null) {
                    
                    // We want to know what is the id of the thread and the id of the post.
                    // What we will do is start from 0 and go through the list as long
                    // there are rows and the current row's thread or post id is the same.
                    // This way, we can fill "gap" ids such as:
                    // Suppose we had the ids: 1 3 4 5 6
                    // Adding 0 would be the best choice.
                    // Another ex: 0 1 2 3 4 5 7
                    // Adding 6 would be the best choice
                    // Another ex: 0 1 2 3
                    // Adding 4 would be the best choice
                    
                    int idPostToAdd = 0;
                    Statement stmt1 = c.createStatement();
                    // First get all of the list of ids
                    ResultSet resultSetPostIds = stmt1.executeQuery("SELECT P.id FROM POST P ORDER BY P.id");
                    
                    while (resultSetPostIds.next() && idPostToAdd == resultSetPostIds.getInt("id"))
                        idPostToAdd++;
                    
                    int idThreadToAdd = 0;
                    
                    if (creatingAThread) {
                        
                        // We need to find a suitable id of a thread only if creatingAThread = true,
                        // but if it's false, why do we need to do the following?
                        
                        Statement stmt2 = c.createStatement();
                        // First get all of the list of ids
                        ResultSet resultSetThreadIds = stmt2.executeQuery("SELECT T.id FROM THREAD T ORDER BY T.id");
                        
                        while (resultSetThreadIds.next() && idThreadToAdd == resultSetThreadIds.getInt("id"))
                            idThreadToAdd++;
                        
                        // If we are creating a thread
                        // Add this new thread with its head post to the database
                        // Here, parentId will be the id of a forum
                        Statement stmt3 = c.createStatement();
                        stmt3.executeUpdate("INSERT INTO THREAD (`id`, `name`, `forum_id`) VALUES (" + idThreadToAdd + ", '" + threadTitle + "', " + parentId + ")");
                        Statement stmt4 = c.createStatement();
                        stmt4.executeUpdate("INSERT INTO POST (`id`, `contents`, `game_acc_id`, `thread_id`, `head_post`) VALUES (" + idPostToAdd + ", '" + contents + "', " + Main.currentUserAccountId + ", " + idThreadToAdd + ", 'Y')");
                        
                    } else {
                        // We are not creating a thread
                        // Add this post to the database
                        // Here, parentId will be the id of a thread
                        Statement stmt5 = c.createStatement();
                        stmt5.executeUpdate("INSERT INTO POST (`id`, `contents`, `game_acc_id`, `thread_id`) VALUES (" + idPostToAdd + ", '" + contents + "', " + Main.currentUserAccountId + ", " + parentId + ", 'N')");
                    }
                    
                    c.close();
                    
                }
                
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
            
            // Get the parent of this class, which will be PaneForums (because this was instantiated there in buttonActionCreateThread() or buttonActionCreatePost()
            PaneForums parent = (PaneForums) PaneCreateThreadOrPost.this.getParent();
            // Why did we get the parent? We need to call reDrawForum() or reDrawThread()
            if (creatingAThread)
                parent.reDrawForum(); // If we are creating a thread, re-draw the forum
            else
                parent.reDrawThread(); // If we are creating a post, re-draw the post
        
        }
        
    }
    
    private void buttonActionCancel() {
        // Get the parent of this class, which will be PaneForums (because this was instantiated there in buttonActionCreateThread() or buttonActionCreatePost()
        PaneForums parent = (PaneForums) PaneCreateThreadOrPost.this.getParent();
        // Why did we get the parent? We need to call reDrawForum() or reDrawThread()
        if (creatingAThread)
            parent.reDrawForum(); // If we are creating a thread, re-draw the forum
        else
            parent.reDrawThread(); // If we are creating a post, re-draw the post
    }
    
    
}



package comp333.project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class PaneForums extends VBox {
    
    private ButtonM buttonCreateNewThread;
    private ButtonM buttonCreateNewPost;
    
    // These two int variables are used temporary when you switch between viewing forums and creating new threads/posts panes.
    // They are used in PaneCreateThreadOrPost, go there to see their method.
    private String tempName;
    private int tempId;
    
    public PaneForums() {
        
        buttonCreateNewThread = new ButtonM("Create New Thread", PanePlayer.OPTION_WIDTH - 45); // Only appears when you chose a forum
        buttonCreateNewPost = new ButtonM("Create New Post", PanePlayer.OPTION_WIDTH - 45); // Only appears when you chose a thread
        
        buttonCreateNewThread.setOnAction(e -> { buttonActionCreateThread(); });
        buttonCreateNewPost.setOnAction(e -> { buttonActionCreatePost(); });
        
        buttonCreateNewThread.setAlignment(Pos.CENTER_RIGHT);
        buttonCreateNewPost.setAlignment(Pos.CENTER_RIGHT);
        
        try {
            
            Connection c = DriverManager.getConnection(Main.DATABASE_SERVER, "root", "admin");
            
            if (c != null) {
                
                // Get all forums and add them as buttons
                
                Statement stmt = c.createStatement();
                ResultSet resultSetForums = stmt.executeQuery("SELECT F.name, F.id FROM FORUM F");
                
                while (resultSetForums.next()) {
                
                    String currentName = resultSetForums.getString("name");
                    int currentId = resultSetForums.getInt("id");
                    
                    ButtonM buttonToAdd = new ButtonM(currentName, PanePlayer.OPTION_WIDTH - 45);
                    buttonToAdd.setAlignment(Pos.CENTER_LEFT);
                    buttonToAdd.setOnAction(e -> { buttonActionForum(currentName, currentId); });
                    
                    this.getChildren().add(buttonToAdd);
                
                }
             
                c.close();
                
            }
            
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        
        this.setSpacing(2);
        
    }
    
    private void buttonActionForum(String name, int id) {
        
        this.getChildren().clear();
        this.getChildren().add(buttonCreateNewThread);
        
        Label labelForumTitle = new Label("\t" + name);
        labelForumTitle.setStyle("-fx-text-size: 15; -fx-border-radius: 3; -fx-border-width: 3; -fx-border-color: darkcyan;");
        labelForumTitle.setPrefWidth(PanePlayer.OPTION_WIDTH - 45);
        
        this.getChildren().add(labelForumTitle);
        
        try {
            
            Connection c = DriverManager.getConnection(Main.DATABASE_SERVER, "root", "admin");
            
            if (c != null) {
                
                // Get all threads from the forum with the specified id
                
                Statement stmt = c.createStatement();
                ResultSet resultSetThreads = stmt.executeQuery("SELECT T.name, T.id FROM THREAD T WHERE T.forum_id = " + id);
                
                while (resultSetThreads.next()) {
                    
                    String currentName = resultSetThreads.getString("name");
                    int currentId = resultSetThreads.getInt("id");
                    
                    ButtonM buttonToAdd = new ButtonM(currentName, PanePlayer.OPTION_WIDTH - 45);
                    buttonToAdd.setAlignment(Pos.CENTER_LEFT);
                    buttonToAdd.setOnAction(e -> { buttonActionThread(currentName, currentId); });
                    
                    this.getChildren().add(buttonToAdd);
                
                }
                
                c.close();
                
            }
            
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        
        // Save the name and id of this forum for later if you switch between viewing or creating new threads.
        tempName = name;
        tempId = id;
        
    }
    
    private void buttonActionThread(String name, int id) {
        
        this.getChildren().clear();
        this.getChildren().add(buttonCreateNewPost);
        
        Label labelThreadTitle = new Label("\t" + name);
        labelThreadTitle.setStyle("-fx-text-size: 15; -fx-border-radius: 3; -fx-border-width: 3; -fx-border-color: darkcyan;");
        labelThreadTitle.setPrefWidth(PanePlayer.OPTION_WIDTH - 45);
        
        this.getChildren().add(labelThreadTitle);
        
        try {
            
            Connection c = DriverManager.getConnection(Main.DATABASE_SERVER, "root", "admin");
            
            if (c != null) {
                
                // Get all posts from the thread with the specified id.
                // First get the head post (head post is explained in PanePost)
                
                Statement stmt0 = c.createStatement();
                ResultSet resultSetHeadPost = stmt0.executeQuery("SELECT P.game_acc_id, P.contents FROM POST P WHERE P.thread_id = " + id + " AND P.head_post = 'Y'");
                
                if (resultSetHeadPost.next())
                    this.getChildren().add(new PanePost(resultSetHeadPost.getInt("game_acc_id"), resultSetHeadPost.getString("contents"), true));
                
                // Now the other posts (not head posts)
                Statement stmt = c.createStatement();
                ResultSet resultSetPosts = stmt.executeQuery("SELECT P.game_acc_id, P.contents FROM POST P WHERE P.thread_id = " + id + " AND P.head_post = 'N'");
                
                // Other posts are not the head post
                while (resultSetPosts.next())
                    this.getChildren().add(new PanePost(resultSetPosts.getInt("game_acc_id"), resultSetPosts.getString("contents"), false));
                
                c.close();
                
            }
            
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        
        // Save the name and id for this thread for later if you switch between viewing or creating new posts.
        tempName = name;
        tempId = id;
        
    }    
        
    private void buttonActionCreateThread() {
        
        // Create a thread with a head post (head post is explained in PanePost)
        this.getChildren().clear();
        this.getChildren().add(new PaneCreateThreadOrPost(true, tempId));
        
    }
    
    private void buttonActionCreatePost() {
        
        // Create a reply post
        this.getChildren().clear();
        this.getChildren().add(new PaneCreateThreadOrPost(false, tempId));
        
    }
    
    // These two methods are public because they are accessed from PaneCreateThreadOrPost
    // Their goal is simply draw the pane of the threads or posts with
    // name = tempName and id = tempId. They are called when the player goes back to the
    // view of the forums or the threads
    public void reDrawForum() {
        buttonActionForum(tempName, tempId);
    }
    
    public void reDrawThread() {
        buttonActionThread(tempName, tempId);
    }
    
}
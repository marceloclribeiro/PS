package gui;

/**
 *
 * @author edlcorrea
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class App extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("mainView.fxml"));
            
            Parent root = loader.load();
            Scene scene = new Scene(root); // add root to the scene
            
            MainViewController controller = loader.getController();
            System.out.println(controller);
            
            // add css
            String css = this.getClass().getResource("styles.css").toExternalForm();
            scene.getStylesheets().add(css);
            
            // Stage basic config
            Image icon = new Image("gui/img/icon_cc.png");
            primaryStage.getIcons().add(icon); // set app icon
            primaryStage.setTitle("PS"); // set app title
            
            primaryStage.setMaximized(true);
            primaryStage.setScene(scene); // add scene to stage     
            primaryStage.show();
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void launchGUI(String[] args) {
        launch(args);
    }    
}

/**
 * Class with main method for starting application
 * 
 * @author Petar Deveric
 */
    
package start;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class StartApplication extends Application {
    
    //  Opens first ("Login") window
    @Override
    public void start(Stage stage) throws Exception {
        //  Get login screen
        Parent root = FXMLLoader.load(getClass().getResource("/welcome/view/LogIn.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/general/view/CSS.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Ponude");
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.show();
    }
    
    //  Starts the program
    public static void main(String[] args) {
        launch(args);
    }
    
}

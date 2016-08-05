/**
 * Welcome FXML Controller class. Contains methods for opening new offer, saved offers and settings screen.
 *
 * @author Petar Deveric
 */
package welcome;

import general.SetNewStage;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.Event;
import javafx.fxml.FXML;


public class WelcomeController extends SetNewStage {
    
    //  Opens "New Offer" window
    @FXML
    private void newOffer(Event event){
        setNewStage(event, "/new_offer/view/NewOffer.fxml");
    }
    
    //  Opens "Offer Database" window
    @FXML
    private void offerDatabase(Event event){
        setNewStage(event, "/databasse/view/DataBase.fxml");
    }
    
    //  Opens "Settins" window
    @FXML
    private void settings(Event event){
        setNewStage(event, "/settings/view/Settings.fxml");
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {} 
}

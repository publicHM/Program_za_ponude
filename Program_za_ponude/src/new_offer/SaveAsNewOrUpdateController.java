/**
 * SaveAsNewOrUpdate FXML Controller class. Provides methods for calling methods in parent (NewOfferController) to save
 * data as new offer or overwrite existing offer.
 * 
 * @author Petar Deveric
 */
package new_offer;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;
import new_offer.helper.SaveData;


public class SaveAsNewOrUpdateController {
    
    private final Event parentEvent;
    private final SaveData saveDataClass;
    private final int customerID;
    
    public SaveAsNewOrUpdateController(Event parentEvent, SaveData saveDataClass, int customerID){
        this.parentEvent = parentEvent;
        this.saveDataClass = saveDataClass;
        this.customerID = customerID;
    }

    //  Closes popup stage and calls method for saving offer as a new offer
    @FXML
    private void saveAsNew(ActionEvent event){
        ( (Stage) ( (Node) event.getSource() ).getScene().getWindow() ).close();
        saveDataClass.saveTablesAsNewOffer(parentEvent, customerID);
    }
    
    //  Closes popup stage and calls method for saving offer as an update
    @FXML
    private void updateCurrent(ActionEvent event){
        ( (Stage) ( (Node) event.getSource() ).getScene().getWindow() ).close();
        saveDataClass.saveDataAsUpdate(parentEvent);
    }
}

/**
 * SaveAsNewOrUpdate FXML Controller class. Provides methods for calling methods in parent (NewOfferController) to save
 * data as new offer or overwrite existing offer.
 * 
 * @author Petar Deveric
 */
package newoffer;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;


public class SaveAsNewOrUpdateController {
    
    private final Event parentEvent;
    private final NewOfferController parentController;
    private final int customerID;
    
    public SaveAsNewOrUpdateController(Event parentEvent, NewOfferController parentController, int customerID){
        this.parentEvent = parentEvent;
        this.parentController = parentController;
        this.customerID = customerID;
    }

    @FXML
    private void saveAsNew(ActionEvent event){
        ( (Stage) ( (Node) event.getSource() ).getScene().getWindow() ).close();
        parentController.saveTablesAsNewOffer(parentEvent, customerID);
    }
    
    @FXML
    private void updateCurrent(ActionEvent event){
        ( (Stage) ( (Node) event.getSource() ).getScene().getWindow() ).close();
        parentController.saveDataAsUpdate(parentEvent);
    }
}

/**
 * Helper class that adds passed treatment to corresponding table (upperJaw, lowerJaw or other table)
 * 
 * @author Petar
 */
package new_offer.helper;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import new_offer.InitializeTable;
import new_offer.NewOfferController;
import new_offer.Treatment;


public class SetTable extends NewOfferAbstract {
    
    private final NewOfferController controller;
    
    public SetTable(NewOfferController controller){
        this.controller = controller;
    }
    
    // Calss method to check if treatment is alreaddy in corresponding table. If it is, closes popup stage,
    // adds treatment to list, clears table and restores table with new values
    public void setTable(Stage popupStage, String selectedItem, TableView table, ObservableList<Treatment> list,
            Treatment treatment){
        if( selectedItem == null || !checkIftreatmentIsAlreadyInTable(selectedItem, list) ){
            closePopup(popupStage);

            list.add(treatment);
            list = setItemsNo(list);
            table.getColumns().clear();
            table.setItems(list);
            InitializeTable it = new InitializeTable(table, controller);
            it.initialize();
        }
    }
    
    // Checks if treatment is alreaddy in corresponding table. Returns true if it is, and false if it is not
    private boolean checkIftreatmentIsAlreadyInTable(String selectedItem, ObservableList<Treatment> observableList){
        boolean treatmentAlreadyInTable = false;
        for(Treatment t : observableList){
            String treatmentName = t.getTreatment();
            if(treatmentName.equals(selectedItem)){
                treatmentAlreadyInTable = true;
                break;
            }
        }
        return treatmentAlreadyInTable;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {}
    
}

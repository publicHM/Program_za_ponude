/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 * 
 * @author Petar
 */
package new_offer.helper;

import general.SetNewStage;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import new_offer.Treatment;


public abstract class NewOfferAbstract extends SetNewStage {
    
    public void closePopup(Stage popupStage){
        if(popupStage != null)
            popupStage.close();
    }
    
    public ObservableList<Treatment> setItemsNo(ObservableList<Treatment> ol){   
        int k = 1;
        for(Treatment treatment: ol){
            treatment.setNo(k);
            k++;
        }
        return ol;
    }
}

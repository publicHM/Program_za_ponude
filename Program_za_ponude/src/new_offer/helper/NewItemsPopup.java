/**
 * Helper class that opens new popup window with list of filtered treatments
 * 
 * @author Petar
 */
package new_offer.helper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import new_offer.MyPopupController;
import new_offer.NewOfferController;


public class NewItemsPopup {
    
    private final NewOfferController controller;
    private Stage popupStage; 
    private final Connection conn;
    private final PreparedStatement psShowItems;
    private final String languageNameInDB;
    private final TextField textField;
    private final Event event;
    private final String jaw;
    
    public NewItemsPopup(NewOfferController controller, Stage popupStage, Connection conn,
            PreparedStatement psShowItems, String languageNameInDB, TextField textField, Event event, String jaw){
        this.controller = controller;
        this.popupStage = popupStage;
        this.conn = conn;
        this.psShowItems = psShowItems;
        this.languageNameInDB = languageNameInDB;
        this.textField = textField;
        this.event = event;
        this.jaw = jaw;
    }
    
    //  Opens new popup window with list of filtered treatments
    public void newItemsPopup(){
        Bounds boundsInScene = textField.localToScreen(textField.getBoundsInLocal());
        int x = (int) boundsInScene.getMinX();
        int y = (int) boundsInScene.getMinY();
        double width = textField.getWidth();
        double height = textField.getHeight();
        
        try{Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/new_offer/view/MyPopup.fxml"));
            
            loader.setControllerFactory((Class<?> passedController) -> {
                    MyPopupController popupController = new MyPopupController(controller, conn, psShowItems,
                            languageNameInDB, textField, jaw);
                    return popupController ;
            });
            
            Scene scene = new Scene((Pane) loader.load());
            popupStage = new Stage();
            popupStage.setScene(scene);
            popupStage.setX(x+1);
            popupStage.setY(y + height);
            popupStage.setWidth(width);
            popupStage.initOwner(stage);
            popupStage.initStyle(StageStyle.UNDECORATED);
            popupStage.show();
            }
        catch(Exception e){
            System.err.println("Exception in class NewItemsPopup, method newItemsPopup()");
            e.printStackTrace(System.out);
        }
    }
}

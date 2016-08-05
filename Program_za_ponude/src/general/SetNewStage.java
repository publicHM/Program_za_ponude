/*
 * Abstract class with methods for setting new main stages. Also provides method for popup confirm dialog box
 *
 * @author Petar Deveric
 */
package general;


import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import new_offer.NewOfferController;
import new_offer.SaveAsNewOrUpdateController;
import new_offer.helper.SaveData;


public abstract class SetNewStage implements Initializable {
    
    //  Finds appropriate logic for passed fxml location, applyies it and sets new stages
    public void setNewStage(Event event, String location){
        try{
            Parent root = FXMLLoader.load(getClass().getResource(location));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            switch(location){
                case "/welcome/view/Welcome.fxml":
                    scene.getStylesheets().add(getClass().getResource("/welcome/view/WelcomeCSS.css").toExternalForm());
                    stage.centerOnScreen();
                    stage.setTitle("Dobrodošli");
                    break;
                case "/databasse/view/DataBase.fxml":
                    scene.getStylesheets().add(getClass().getResource("/general/view/CSS.css").toExternalForm());
                    stage.centerOnScreen();
                    stage.setTitle("Baza ponuda");
                    break;
                case "/settings/view/Settings.fxml":
                    scene.getStylesheets().add(getClass().getResource("/general/view/CSS.css").toExternalForm());
                    stage.centerOnScreen();
                    stage.setTitle("Postavke");
                    break;
                case "/new_offer/view/NewOffer.fxml":
                    scene.getStylesheets().add(getClass().getResource("/general/view/CSS.css").toExternalForm());
                    stage.setY(0);
                    stage.setX(0);
                    stage.setTitle("Nova ponuda");
                    break;
                case "/settings/view/ChangeData.fxml":
                case "/settings/view/ChangeCompanyInfo.fxml":
                case "/settings/view/AddUser.fxml":
                case "/settings/view/LogIn.fxml":
                    scene.getStylesheets().add(getClass().getResource("/general/view/CSS.css").toExternalForm());
                    stage.centerOnScreen();
                    stage.setTitle("Promjena podataka");
                    break;
            }
            stage.setResizable(true);
            stage.show();
        }
        catch(Exception e){
            e.printStackTrace(System.out);
        }
    }
    
    //  Passes values NewOfferController constructor and sets up new NewOffer.fxml stage
    public void setNewStageWithPassedValues(Event event, String location, Class<?> controllerClass, int customerID,
            int offerID, String date){
        try{            
            FXMLLoader loader = new FXMLLoader(getClass().getResource(location));
            
            loader.setControllerFactory((Class<?> passedController) -> {
                if (controllerClass == NewOfferController.class) {
                    NewOfferController controller = new NewOfferController(customerID, offerID, date);
                    return controller ;
                }
                else {
                    try {
                        return controllerClass.newInstance();
                    } catch (Exception e) {
                        System.err.println("Exception in class SetNewStage, method setNewStageWithPassedValue(), contoller");
                        throw new RuntimeException(e);
                    }
                }
            });
            
            Scene scene = new Scene((Parent) loader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            scene.getStylesheets().add(getClass().getResource("/general/view/CSS.css").toExternalForm());
            stage.setY(0);
            stage.setX(0);
            stage.show();
        }
        catch(Exception e){
            System.err.println("Exception in class SetNewStage, method setNewStageWithPassedValue()");
            e.printStackTrace(System.out);
        }
    }
    
    //  Passes values SaveAsNewOrUpdateController constructor and opens up new SaveAsNewOrUpdate.fxml stage
    //  This window is asks user if he wants to save offer as new or update existing one
    //  when user tries to save exisisting offer
    public void setUpdateOrNewOfferQueryStage(Event event, SaveData saveDataClass, int customerID){
        
        try{Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/new_offer/view/SaveAsNewOrUpdate.fxml"));
            
            loader.setControllerFactory((Class<?> passedController) -> {
                    SaveAsNewOrUpdateController controller = new SaveAsNewOrUpdateController(event, saveDataClass,
                            customerID);
                    return controller ;
            });
            
            Scene scene = new Scene((Pane) loader.load());
            scene.getStylesheets().add(getClass().getResource("/general/view/OKBox.css").toExternalForm());
            Stage uonStage = new Stage();
            uonStage.setScene(scene);
            uonStage.centerOnScreen();
            uonStage.initOwner(stage);
            uonStage.initModality(Modality.WINDOW_MODAL);
            uonStage.show();
        }
        catch(Exception e){
            System.err.println("Exception in class NewOffer, method newItemsPopup()");
            e.printStackTrace(System.out);
        }
    }

    //  Passes values OKDialogBoxController constructor and opens up new OKDialogBox.fxml with information
    //  message
    public void setOKBox(String message, Event event){
         try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/general/view/OKDialogBox.fxml"));
            
            loader.setControllerFactory((Class<?> passedController) -> {
                OKDialogBoxController controller = new OKDialogBoxController(message);
                return controller ;
            });
            
            Scene scene = new Scene((Pane) loader.load());
            scene.getStylesheets().add(getClass().getResource("/general/view/OKBox.css").toExternalForm());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner( ( (Node) event.getSource() ).getScene().getWindow() );
            stage.show();
        }
        catch(Exception e){
            System.err.println("Exception in class SetNewStage, method setOKBox()");
            e.printStackTrace(System.out);
        }
    }
}

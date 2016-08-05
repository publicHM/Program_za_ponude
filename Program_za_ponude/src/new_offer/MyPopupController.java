/**
 * MyPopup FXML controller class. Provides variables and methods for:
 * 1. getting list of treatments that contains passed filtering text
 * 2. Choosing treatment;
 * 3. Calling methods in NewOfferController to add treatment to corresponding table.
 *
 * @author Petar Deveric
 */
package new_offer;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;


public class MyPopupController implements Initializable {
    
    private ObservableList items;
    private String filteringString;
    private final NewOfferController parentController;
    private final Connection conn;
    private final PreparedStatement psShowItems;
    private final TextField textField;
    private final String jaw;
    private final String languageNameInDB;
    
    @FXML
    ListView list;
    
    public MyPopupController(NewOfferController parentController, Connection conn, PreparedStatement ps,
            String language, TextField textField, String jaw){
        this.parentController = parentController;
        this.conn = conn;
        this.psShowItems = ps;
        this.textField = textField;
        this.languageNameInDB = language;
        this.filteringString = textField.getText().toLowerCase();
        this.jaw = jaw;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        items = FXCollections.observableArrayList();
        getData();
    }
    
    //  Get data offers from database and add them to list
    private void getData(){
        // Get all items if there is no filtering text
        if(filteringString.isEmpty() || filteringString.equals("")){
            String showItems = "SELECT " + languageNameInDB + " FROM TreatmentList WHERE " + languageNameInDB +
                    " IS NOT NULL AND " + languageNameInDB + " !='' ORDER BY " + languageNameInDB + " ASC";
            try(PreparedStatement ps = conn.prepareStatement(showItems);){
                try(ResultSet rs = ps.executeQuery();){
                    while(rs.next()){
                        String item = rs.getString(1);
                        items.add(item);
                    }
                    list.setItems(items);
                }
            }
            catch(SQLException e){
                System.err.println("SQLException in class NewOfferController, method getData()");
                e.printStackTrace(System.out);
            }
            catch(Exception e){
                System.err.println("Exception in class NewOfferController, method getData()");
                e.printStackTrace(System.out);
            }
        // Get items that contain filtering text
        }else{
            try{
                psShowItems.setString(1, "%" + filteringString + "%");
                try(ResultSet rs = psShowItems.executeQuery();){
                    while(rs.next()){
                        String item = rs.getString(1);
                        items.add(item);
                    }
                    list.setItems(items);
                }
            }
            catch(SQLException e){
            System.err.println("SQLException in class NewOfferController, method getData()");
            e.printStackTrace(System.out);
            }
            catch(Exception e){
                System.err.println("Exception in class NewOfferController, method getData()");
                e.printStackTrace(System.out);
            }

        }
    }
    
    //  Calls method for setting new treatment to appropriate table when mouse is double clicked on some treatment
    @FXML
    private void mouseClicked(MouseEvent event){
        if(event.getClickCount() == 2){
            itemSelected(event);
        }
    }
    
    //  Method does appropriate activity for corresponding key pressed
    @FXML
    private void keyClicked(KeyEvent event){
        //  Calls method for setting new treatment to appropriate table when 'Enter' is pressed
        if(event.getCode() == KeyCode.ENTER){
            itemSelected(event);
        }
        //  Calls method for closing popup stage
        else if(event.getCode() == KeyCode.ESCAPE){
            close(event);
        }
        //  Calls method for closing popup stage, takes string without last character and calls method
        //  for new popup stage with list that contains new filtering string
        else if(event.getCode() == KeyCode.BACK_SPACE){
            close(event);
            int length = filteringString.length();
            filteringString = filteringString.substring(0,length-1);
            textField.setText(filteringString);
            refreshPopup(event);
        }
        //  If pressed key is letter: Calls method for closing popup stage, adds pressed key to string and calls method
        //  for new popup stage with list that contains new filtering string
        else if(event.getCode().isLetterKey() || event.getCode() == KeyCode.MINUS
                || event.getCode() == KeyCode.COMMA || event.getCode() == KeyCode.STOP){
            
            close(event);
            
            String newCharacter = event.getCode().getName().toLowerCase();
            if(event.getCode() == KeyCode.MINUS)    
                newCharacter = "-";
            if(event.getCode() == KeyCode.COMMA)    
                newCharacter = ",";
            if(event.getCode() == KeyCode.STOP)    
                newCharacter = ".";
            
            filteringString = filteringString + newCharacter;
            textField.setText(filteringString);
            refreshPopup(event);
        }
    }
    
    //  Removes all treatments form list and calls method for adding new treatments to list
    private void refreshPopup(KeyEvent event){
        items.removeAll();
        list.refresh();
        getData();
        itemSelected(event);
    }
    
    //  Sets new treatment to appropriate table and calls method for closing popup stage
    private void itemSelected(Event event){
        try{
            String selectedItem = list.getSelectionModel().getSelectedItem().toString();
                if(jaw.equals("upper"))
                    parentController.setUpperJawTable(selectedItem);
                if(jaw.equals("lower"))
                    parentController.setLowerJawTable(selectedItem);
                if(jaw.equals("other")) 
                    parentController.setOtherTable(selectedItem);
                parentController.setTotalFields();
                parentController.setDurationTable();
                close(event);
        }
        catch(NullPointerException e){
            System.err.println("String does not exist in database");
        }
    }
    
    //  Method for closing popup stage
    private void close(Event event){
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}

/**
 * DataBase FXML controller class. Provides variables and methods for:
 * 1. showing table with saved offers and customers;
 * 2. opening, saving as .xls file and/or deleting an offer;
 * 3. changing offer ID if it is not already in database.
 * 
 * @author Petar Deveric
 */
package databasse;

import general.DatabaseLocation;
import general.SetNewStage;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import new_offer.NewOfferController;
import save_as_xls.OfferToXLS;


public class DataBaseController extends SetNewStage{
    
    private Connection conn;
    private ObservableList<OfferBasicInfo> tableList;
    private String filteringString;
    
    @FXML
    private TableView table;
    
    //  Constructor sets begning filtering String to empty value and calls method for setting connection to database
    public DataBaseController(){
        filteringString = "";
        getConnection();
    }
    
    //  Sets connection to database
    private void getConnection(){
        //  Get database location
        String databaseLocation = new DatabaseLocation().getDatabaseLocation();
            
        try{conn = DriverManager.getConnection("jdbc:derby:" + databaseLocation);
        } catch (SQLException e){
            e.printStackTrace(System.out);
        }
    }
    
    //  Initializes list that will hold and show basic data about offers and customers and
    //  calls method to get data all offers and cusromers from database
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tableList = FXCollections.observableArrayList();
        getData();
    }    
    
    //  Get offers and cusomers from database based on filtering string or number
    @FXML
    private void getData(){
        tableList.clear();
        table.getColumns().clear();
        
        GetOffers offers = new GetOffers(conn, filteringString);
        
        //  Check if search field is empty and get all offers and customers from database if it is empty
        if ( filteringString.isEmpty() || filteringString.equals("") ){
            offers.getAllOffersAndCustomers();
            tableList = offers.getTableList();
        }
        //  Do nothing if filtering string is made of one or more zeros
        else if(filteringString.matches("0+")){}
        //  Check if tekst in search field is integer. If true, find and add all offers that contain that number
        else if ( filteringString.matches("\\d+") ){
            offers.getAllOffersForMatchingNumber();
            tableList = offers.getTableList();
        }
        //  Find and add all offers where customer name contain searched phrase
        else{
            offers.getAllOffersForMatchingCustomer();
            tableList = offers.getTableList();
        }
        
        table.setItems(tableList);
        InitializeTable it = new InitializeTable(table);
        table = it.getTable();
    }
    
    //  Checks which key is pressed on search field. If key is number or letter, adds symbol to
    //  filtering string and calls method getData() to get data for new filltering string
    @FXML
    private void keyPressesOnSearchField(KeyEvent event){

        if(event.getCode() == KeyCode.ENTER){
            getData();
        }
        else if(event.getCode() == KeyCode.BACK_SPACE){
            int length = filteringString.length();
            if(length<=1)
                filteringString = "";
            else
                filteringString = filteringString.substring(0,length-1);
            getData();
        }
        else if(event.getCode().isLetterKey() || event.getCode() == KeyCode.MINUS
                || event.getCode() == KeyCode.COMMA || event.getCode() == KeyCode.STOP){

            String newCharacter = event.getCode().getName().toLowerCase();
            if(event.getCode() == KeyCode.MINUS)    
                newCharacter = "-";
            if(event.getCode() == KeyCode.COMMA)    
                newCharacter = ",";
            if(event.getCode() == KeyCode.STOP)    
                newCharacter = ".";

            filteringString += newCharacter;
            getData();
        }
        else if( event.getCode().isDigitKey() ){
            String newCharacter = event.getCode().getName();
            //  Omit 'Numpad' if key code contains it so only digit could be taken
            if(newCharacter.contains("Numpad") )   filteringString += newCharacter.substring(7);
            else filteringString += newCharacter;
            getData();
        }
    }
    
    //  Open full data for selected row if doubleclicked
    @FXML
    private void tableRowDoubleClicked(MouseEvent event){
        if(event.getClickCount() == 2){
            openOffer(event);
        }
    }
    
    //  Remove row and record from database if key "Delete" is pressed while a row in the table is selected
    @FXML
    private void keyPressesOnTable(KeyEvent event){
        if(event.getCode() == KeyCode.DELETE){
            //  Remove row from table
            int selectedRow = table.getSelectionModel().getSelectedIndex();
            OfferBasicInfo info = tableList.get(selectedRow);
            int customerID = info.getCustomerID();
            int offerID = info.getOfferID();
            tableList.remove(selectedRow);
            
            //  Delete customer in database (from Customers table) if customer is not connected to another offer
            String numberOfCustomerOffers = "SELECT DISTINCT OfferID FROM Offers WHERE CustomerID = " + customerID;
            String deleteCustomerQuery = "DELETE FROM Customers WHERE CustomerID = ?";
            try(PreparedStatement psNumberOfCustomerOffers = conn.prepareStatement(numberOfCustomerOffers);
                PreparedStatement psDeleteCustomer = conn.prepareStatement(deleteCustomerQuery);
                ResultSet rsNumberOfCustomerOffers = psNumberOfCustomerOffers.executeQuery();){
                
                //  Check if there is more then one offer for a customer
                int no = 0;
                while( rsNumberOfCustomerOffers.next() ) {
                    no++;
                    if(no == 2) break;
                }
                
                // Delete customer if there is only one offer for a customer
                if(no < 2){
                    psDeleteCustomer.setInt(1, customerID);
                    psDeleteCustomer.executeUpdate();
                }
            } catch (SQLException e){
                e.printStackTrace(System.out);
            }
            
            //  Delete offer in database (from Offers table)
            String deleteOfferQuery = "DELETE FROM Offers WHERE OfferID = ?";
            try(PreparedStatement psDeleteOffer = conn.prepareStatement(deleteOfferQuery);){
                psDeleteOffer.setInt(1, offerID);
                psDeleteOffer.executeUpdate();

            } catch (SQLException e){
                e.printStackTrace(System.out);
            }
        }
        else if(event.getCode() == KeyCode.ENTER)
            openOffer(event);
    }
    
    //  Go back to "Welcome" screen
    @FXML
    private void goBack(Event event){
        setNewStage(event, "/welcome/view/Welcome.fxml");
        closeConnection();
    }
    
    //  Close conection
    private void closeConnection(){
        if (conn != null){ 
            try{conn.close();
            }catch(SQLException e){
                System.err.println("SQLException in class DataBaseController, method closeConnection()");
                e.printStackTrace(System.out);
            }
        }
    }
    
    //  Export (save) offer in excel dokument in .xls format
    @FXML
    private void saveAsXLS(ActionEvent event){
        try{
            int selectedRow = table.getSelectionModel().getSelectedIndex();
            if(selectedRow == -1){
                setOKBox("Odaberite ponudu u tabeli", event);
            } else {
                OfferBasicInfo info = tableList.get(selectedRow);
                OfferToXLS save = new OfferToXLS(conn, info.getCustomerID(), info.getOfferID(), info.getDate(), event);
                save.saveDataToXLS();
            }
        } catch (StringIndexOutOfBoundsException e){
            setOKBox("Ne postoji ispis za nepotpunu ponudu", event);
            e.printStackTrace(System.out);
        }
    }
    
    
    //  Open window with all data for selected row (offer) in the table
    @FXML
    private void openOffer(Event event){
        int selectedRow = table.getSelectionModel().getSelectedIndex();
        OfferBasicInfo info = tableList.get(selectedRow);
        int customerID = info.getCustomerID();
        int offerID = info.getOfferID();
        String date = info.getDate();
        setNewStageWithPassedValues(event, "/new_offer/view/NewOffer.fxml", NewOfferController.class, customerID,
                offerID, date);
    }
}
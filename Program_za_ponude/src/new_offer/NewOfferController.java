/*
 * NewOffer FXML Controller class. Provides variables and methods for customer data and tables for corresponding treatment
 * data, separated to different tables for each group (upper jaw, lower jaw, other and duration of treatments). Provades 
 * button to save it.
 * 
 * @author Petar Deveric
 */
package new_offer;

import general.DatabaseLocation;
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
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import new_offer.helper.ChangeCurrency;
import new_offer.helper.ChangeTableLanguageAndPrices;
import new_offer.helper.DurationTable;
import new_offer.helper.GetTreatment;
import new_offer.helper.NewItemsPopup;
import new_offer.helper.NewOfferAbstract;
import new_offer.helper.SaveData;
import new_offer.helper.SetCurrency;
import new_offer.helper.SetLanguage;
import new_offer.helper.SetTable;
import new_offer.helper.TotalFields;
import save_as_xls.OfferToXLS;


public class NewOfferController extends NewOfferAbstract {
    
    private int customerID;
    private int offerID;
    private String date;
    private Stage popupStage;
    private ObservableList<Treatment> upperJawList;
    private ObservableList<Treatment> lowerJawList;
    private ObservableList<Treatment> otherList;
    private ObservableList<Duration> durationList;
    private Connection conn;
    private PreparedStatement psShowItems;
    private PreparedStatement psGetTreatment;
    private String languageNameInDB;
    private String currencyNameInDB;
    private String discountNameInDB;
    
    @FXML
    private TextField nameField;
    
    @FXML
    private TextArea addressArea;
    
    @FXML
    private TextField telephoneField;
    
    @FXML
    private TextField emailField;
    
    @FXML
    private MenuButton languageButton;
    
    @FXML
    private MenuButton databaseCurrencyButton;
            
    @FXML
    private TextField exchangeRateField;
    
    @FXML
    private TextField currency1Field;
    
    @FXML
    private TextField currency2Field;
    
    @FXML
    private TextField cashDiscountPercentageField;
    
    @FXML
    private TextField totalBeforeDiscountField;
    
    @FXML
    private TextField totalDiscountField;
    
    @FXML
    private TextField totalAfterDiscountField;
    
    @FXML
    private TextField textFieldUpperJaw;
    
    @FXML
    private TextField textFieldLowerJaw;
    
    @FXML
    private TextField textFieldOther;
    
    @FXML
    private TableView tableUpperJaw;
    
    @FXML
    private TableView tableLowerJaw;
    
    @FXML
    private TableView tableOther;
    
    @FXML
    private TableView tableDuraton;

    public NewOfferController(){
        languageNameInDB = "Croatian";
        currencyNameInDB = "PriceCroHRK";
        discountNameInDB = "DiscountCroHRK";
        establishConnection();
    }
    
    public NewOfferController(int customerID, int offerID, String date){
        this.customerID = customerID;
        this.offerID = offerID;
        this.date = date;
        establishConnection();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        upperJawList = FXCollections.observableArrayList();
        lowerJawList = FXCollections.observableArrayList();
        otherList = FXCollections.observableArrayList();
        durationList = FXCollections.observableArrayList();
        if(customerID != 0) setExistingCustomerAndOffer();
        refreshPreparedStatementsForPopup();
    }
    
    private void establishConnection(){
        //  Get database location
            String databaseLocation = new DatabaseLocation().getDatabaseLocation();
        
        try{conn = DriverManager.getConnection("jdbc:derby:" + databaseLocation);
        } catch (SQLException e){
            System.err.println("SQLException in class NewOfferController, method establishConnection()");
            e.printStackTrace(System.out);
        }
    }
    
    //  Refresh prepared statements so popup window could get appropriate treatmant names from database
    private void refreshPreparedStatementsForPopup(){
        String showItems = "SELECT " + languageNameInDB + " FROM TreatmentList WHERE lower(" + languageNameInDB + ") LIKE ? "
                + "AND " + languageNameInDB + " IS NOT NULL ORDER BY " + languageNameInDB + " ASC";
        String treatmentQuery = "SELECT TreatmentID, " + languageNameInDB + ", " + currencyNameInDB  + ", "
                + discountNameInDB + " FROM TreatmentList WHERE " + languageNameInDB + " = ? ";
        
        try{psShowItems = conn.prepareStatement(showItems);
            psGetTreatment = conn.prepareStatement(treatmentQuery);
        } catch (SQLException e){
            System.err.println("SQLException in class NewOfferController, method refreshPreparedStatementsForPopup()");
            e.printStackTrace(System.out);
        }
    }
    
    //  Gets data from database and sets data at UI when exisiting offer is opened
    private void setExistingCustomerAndOffer(){
        String customerInfoQuery = "SELECT Name, Address, Telephone, Email, Language, StartCurrency, ExchangeRate, "
                + "FinalCurrency, CashDiscount FROM Customers WHERE CustomerID = ?";
        String offerInfoQuery = "SELECT TableName, TreatmentID, Treatment, Quantity, Price, Discount, Visit FROM Offers "
                + "WHERE OfferID = ?";
        try(PreparedStatement psCustomerInfo = conn.prepareStatement(customerInfoQuery);
            PreparedStatement psOfferInfo = conn.prepareStatement(offerInfoQuery);){
            
            //  Set customer information
            psCustomerInfo.setInt(1, customerID);
            try(ResultSet rsCustomerInfo = psCustomerInfo.executeQuery();){
                    rsCustomerInfo.next();
                    nameField.setText( rsCustomerInfo.getString(1));
                    addressArea.setText(rsCustomerInfo.getString(2) );
                    telephoneField.setText(rsCustomerInfo.getString(3) );
                    emailField.setText(rsCustomerInfo.getString(4) );
                    String clientLanguage = rsCustomerInfo.getString(5);
                    languageButton.setText(clientLanguage);
                    String clientCurrency = rsCustomerInfo.getString(6);
                    databaseCurrencyButton.setText(clientCurrency);
                    exchangeRateField.setText( String.valueOf(rsCustomerInfo.getFloat(7) ) );
                    currency1Field.setText(rsCustomerInfo.getString(8));
                    currency2Field.setText(rsCustomerInfo.getString(8));
                    cashDiscountPercentageField.setText( String.valueOf(rsCustomerInfo.getInt(9) ));
                    setLanguage(clientLanguage, false, false);
                    setCurrency(clientCurrency, false);

                    //  Refresh prepared statements for retrieving data from database
                    refreshPreparedStatementsForPopup();
            }
            
            //  Fill tables with information about treatments if htere is any
            if(offerID != 0){
                psOfferInfo.setInt(1, offerID);
                try(ResultSet rsOfferInfo = psOfferInfo.executeQuery();){
                    while( rsOfferInfo.next() ){
                        String tableName = rsOfferInfo.getString(1);
                        int treatmentID = rsOfferInfo.getInt(2);
                        String treatment = rsOfferInfo.getString(3);
                        int quantity = rsOfferInfo.getInt(4);
                        float price = rsOfferInfo.getFloat(5);
                        int discount = rsOfferInfo.getInt(6);
                        float total = quantity * price * (100 - discount)/100;
                        int visit = rsOfferInfo.getInt(7);
                        if(tableName.equals("UpperJaw") ){
                            setTable(tableUpperJaw, upperJawList, null,
                                    new Treatment(treatmentID, treatment, quantity, price, discount, total, visit) );
                        } else if(tableName.equals("LowerJaw") ){
                            setTable(tableLowerJaw, lowerJawList, null,
                                    new Treatment(treatmentID, treatment, quantity, price, discount, total, visit) );
                        } else if(tableName.equals("Other") ){
                            setTable(tableOther, otherList, null,
                                    new Treatment(treatmentID, treatment, quantity, price, discount, total, visit) );
                        }
                    }
                }
            }
        } catch (SQLException e){
            System.err.println("SQLException in class NewOfferController, method setExistingCustomerAndOffer()");
            e.printStackTrace(System.out);
        }
        
        //  Set rows numbers in the tables
        setItemsNo(upperJawList);
        setItemsNo(lowerJawList);
        setItemsNo(otherList);
            
        //  Sums all amounts and puts it in field for total amount
        setTotalFields();
        
        //  Sums amounts per visit and puts in in the duration table
        setDurationTable();
    }
    
    // The method is called when key is relesed while serach box (text field) above "Upper Jaw" table is focused
    @FXML
    private void textFieldUpperJawEvent(KeyEvent event){
        if(event.getCode() == KeyCode.ENTER)
            newItemsPopup(textFieldUpperJaw, event, "upper");
        else if(event.getCode().isLetterKey() || event.getCode() == KeyCode.MINUS )
            newItemsPopup(textFieldUpperJaw, event, "upper");
        else if(event.getCode() == KeyCode.ESCAPE)
            closePopup(popupStage);
    }
    
    // The method is called when button "Search" above "Upper Jaw" table is pressed
    @FXML
    private void searchUpperJaw(Event event){
        closePopup(popupStage);
        newItemsPopup(textFieldUpperJaw, event, "upper");
    }
    
    // The method is called when key is relesed while serach box (text field) above "Lower Jaw" table is focused
    @FXML
    private void textFieldLowerJawEvent(KeyEvent event){
        if(event.getCode() == KeyCode.ENTER)
            newItemsPopup(textFieldLowerJaw, event, "lower");
        else if(event.getCode().isLetterKey() || event.getCode() == KeyCode.MINUS )
            newItemsPopup(textFieldLowerJaw, event, "lower");
        else if(event.getCode() == KeyCode.ESCAPE)
            closePopup(popupStage);
    }
    
    // The method is called when button "Search" above "Lower Jaw" table is pressed
    @FXML
    private void searchLowerJaw(Event event){
        newItemsPopup(textFieldLowerJaw, event, "lower");
    }
    
    // The method is called when key is relesed while serach box (text field) above "Other" table is focused
    @FXML
    private void textFieldOtherEvent(KeyEvent event){
        if(event.getCode() == KeyCode.ENTER)
            newItemsPopup(textFieldOther, event, "other");
        else if(event.getCode().isLetterKey() || event.getCode() == KeyCode.MINUS 
                || event.getCode() == KeyCode.COMMA || event.getCode() == KeyCode.STOP)
            newItemsPopup(textFieldOther, event, "other");
        else if(event.getCode() == KeyCode.ESCAPE)
            closePopup(popupStage);
    }
    
    // The method is called when button "Search" above "Other" table is pressed
    @FXML
    private void searchOther(Event event){
        newItemsPopup(textFieldOther, event, "other");
    }
    
    //  Calls helper class that opens new popup window with list of filtered treatments
    private void newItemsPopup(TextField textField, Event event, String jaw){
        new NewItemsPopup(this, popupStage, conn, psShowItems, languageNameInDB, textField, event, jaw).newItemsPopup();
    }
    
    //  Calls helper class that adds passed treatment to upperJaw table
    public void setUpperJawTable(String selectedItem){
            setTable(tableUpperJaw, upperJawList, selectedItem, getTreatment(selectedItem) );
    }
    
    //  Calls helper class that adds passed treatment to lowerJaw table
    public void setLowerJawTable(String selectedItem){
            setTable(tableLowerJaw, lowerJawList, selectedItem, getTreatment(selectedItem) );
    }
    
    //  Calls helper class that adds passed treatment to other table
    public void setOtherTable(String selectedItem){
            setTable(tableOther, otherList, selectedItem, getTreatment(selectedItem) );
    }
    
        
    private void setTable(TableView table, ObservableList<Treatment> list, String selectedItem, Treatment treatment){
        new SetTable(this).setTable(popupStage, selectedItem, table, list, treatment);
    }
    
        
    //  Calls helper class to get information (price, discount etc.) from database for chosen treatment name
    //  and returns new treatment with all information. Refreshes duration table and total amount that should
    //  be payed
    private Treatment getTreatment(String selectedItem){
        Treatment treatment = GetTreatment.getTreatment(this, selectedItem, psGetTreatment,
            exchangeRateField.getText());
        return treatment;
    }
    
    //  Sums all amounts and puts it in field for total amount
    public void setTotalFields(){
        TotalFields.setTotalFields(upperJawList, lowerJawList, otherList, cashDiscountPercentageField,
                totalBeforeDiscountField, totalDiscountField, totalAfterDiscountField);
    }
    
    //  Resets toatal fields and duration table if 'Enter' key is pressed in discount text field for cash is changed.
    //  If key pressed is letter, comma or period, sets new warning with message that only 0-9 keys can be used
    @FXML
    private void discountChanged(KeyEvent event){
        try{if(event.getCode() == KeyCode.ENTER){
                setTotalFields();
                setDurationTable();
            }
            else if( event.getCode().isLetterKey() || event.getCode() == KeyCode.COMMA ||
                    event.getCode() == KeyCode.PERIOD){
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e){
            setOKBox("Please use 0 - 9 keys only", event);
        }
    }
    
    //  Calls helper class for setting duration table
    public void setDurationTable(){
        new DurationTable(durationList, upperJawList, lowerJawList, otherList, cashDiscountPercentageField.getText(),
                tableDuraton).setDurationTable();
    }
    
    //  Calls helper class for changing prices in tables to another currency if key 'Enter' is pressed
    @FXML
    private void exchangeRateFieldEnterPressed(KeyEvent event){
        if(event.getCode() == KeyCode.ENTER){
            new ChangeCurrency(upperJawList, lowerJawList, otherList, durationList, tableUpperJaw,
                tableLowerJaw, tableOther, tableDuraton).
                    exchangeRateFieldEnterPressed(event, exchangeRateField.getText() );
            setTotalFields();
        }
    }
    
    //  Calls helper class for changing prices in tables to another currency
    @FXML
    private void changeCurrency(MouseEvent event){
        new ChangeCurrency(upperJawList, lowerJawList, otherList, durationList, tableUpperJaw,
            tableLowerJaw, tableOther, tableDuraton).
                changeCurrencyButtonClicked(popupStage, event, exchangeRateField.getText() );
        setTotalFields();
    }
    
    //  Set client language
    @FXML
    private void setDataLanguage(ActionEvent event){
        MenuItem menuItem = (MenuItem) event.getSource();
        String clientLanguage = menuItem.getText();
        languageButton.setText(clientLanguage);
        setLanguage(clientLanguage, true, true);
        
        //  Refresh prepared statements for retrieving data from database
        refreshPreparedStatementsForPopup();
        
        //  Change language in tables if tables are already populated with data
        changeTablesLanguageAndCurrency();
    }
    
    //  Calls helper class for setting up language values that are then retrieved with getters and assigned
    //  to appropriate fields in this class
    private void setLanguage(String clientLanguage, boolean changeCurrency, boolean setCurrencyFields){
        SetLanguage sl = new SetLanguage();
        sl.setLanguage(clientLanguage);
        //  Call method for changing currency if changeCurrency is true
        if(changeCurrency)    setCurrency( sl.getClientCurrency(), setCurrencyFields);
        languageNameInDB = sl.getLanguageNameInDB();
    }
    
    //  Set client currency on GUI and call mathod for setting up currency values in program
    @FXML
    private void setDataCurrency(ActionEvent event){
        MenuItem menuItem = (MenuItem) event.getSource();
        //  Call method for setting up currency values in program
        String clientCurrency = menuItem.getText();
        databaseCurrencyButton.setText(clientCurrency);
        setCurrency(clientCurrency, true);
        
        //  Refresh prepared statements for retrieving data from database
        refreshPreparedStatementsForPopup();
        
        //  Change language in tables if tables are already populated with data
        changeTablesLanguageAndCurrency();
    }
    
    //  Calls helper class for setting up currency values that are then retrieved with getters and assigned
    //  to appropriate fields in this class
    private void setCurrency(String clientCurrency, boolean setCurrencyFields){
        SetCurrency sc = new SetCurrency();
        sc.setCurrency( clientCurrency);
        currencyNameInDB = sc.getCurrencyNameInDB();
        discountNameInDB = sc.getDiscountNameInDB();
        if(setCurrencyFields){
            currency1Field.setText( sc.getCurrency1Field() );
            currency2Field.setText( sc.getCurrency2Field() );
        }
        
        if(setCurrencyFields)   databaseCurrencyButton.setText(clientCurrency);
    }
    
    //  Method calls another methods to change language and prices in tables to new language and corresponding prices
    private void changeTablesLanguageAndCurrency(){
        exchangeRateField.setText("1.0");
        
        String changeCurrencyQuery = "SELECT " + languageNameInDB + ", "+ currencyNameInDB + " FROM TreatmentList"
                + " WHERE TreatmentID = ? ";
        try(PreparedStatement psChangeCurrency = conn.prepareStatement(changeCurrencyQuery) ){
            if ( !upperJawList.isEmpty() ){
                changeLanguageAndPricesForATable(upperJawList, psChangeCurrency);
                tableUpperJaw.refresh();
            }
            if ( !lowerJawList.isEmpty() ){
                changeLanguageAndPricesForATable(lowerJawList, psChangeCurrency);
                tableLowerJaw.refresh();
            }
            if ( !otherList.isEmpty() ){
                changeLanguageAndPricesForATable(otherList, psChangeCurrency);
                tableOther.refresh();
            }
            if ( !durationList.isEmpty() ){
                setDurationTable();
            }
        } catch (SQLException e){
            System.err.println("SQLException in class NewOfferController, method changeTablesLanguageAndCurrency()");
            e.printStackTrace(System.out);
        }
    }
    
    //  Calls static method in helper class to change language and prices in a table to new language and
    //  prices in another currency
    private void changeLanguageAndPricesForATable(ObservableList<Treatment> observableList, 
            PreparedStatement psChangeCurrency){
        ChangeTableLanguageAndPrices.changeLanguageAndPricesForATable(observableList, psChangeCurrency);
    }
    
    //  Closes popup stage
    @FXML
    private void closePopupOnMouseClickedOrScrolling(){
        closePopup(popupStage);
    }
    
    //  Calls method for deleting row when key 'Delete' is pressed while a table is selected
    @FXML
    private void deleteRow(KeyEvent event){
        if(event.getCode() == KeyCode.DELETE){
            TableView table = (TableView) event.getSource();
            deleteRow(table);
        }
    }
    
    //  Deletes selected row in passed table
    private void deleteRow(TableView table){
        closePopup(popupStage);
        int selectedRow = table.getSelectionModel().getSelectedIndex();
        ObservableList rows = table.getItems();
        rows.remove(selectedRow);
        setItemsNo(rows);
        table.refresh();
        setDurationTable();
        setTotalFields();
    }
    
    //  Opens 'Database' stage
    @FXML
    private void goBack(Event event){
        closePopup(popupStage);
        setNewStage(event, "/databasse/view/DataBase.fxml");
        closeConnection();
    }
    
    //  Closes connection
    private void closeConnection(){
        if (conn != null){ 
            try{conn.close();
            }catch(SQLException e){
                System.err.println("SQLException in class NewOfferController, method closeConnection()");
                e.printStackTrace(System.out);
            }
        }
    }
    
    //  Calls helper class that saves an offer as .xls file
    @FXML
    private void saveAsXLS(ActionEvent event){
        if( customerID>0 && offerID >0){
            OfferToXLS save = new OfferToXLS(conn, customerID, offerID, date, event);
            save.saveDataToXLS();
        }
    }
    
    //  Calls helper class to save offer to database
    @FXML
    private void saveData(ActionEvent event){
        SaveData sd = new SaveData(conn, offerID, customerID, upperJawList, lowerJawList, otherList,durationList,
                nameField.getText(), addressArea.getText(), telephoneField.getText(), emailField.getText(),
                languageButton.getText(), databaseCurrencyButton.getText(), exchangeRateField.getText(),
                cashDiscountPercentageField.getText(), currency2Field.getText());
        sd.saveData(event);
    }
}

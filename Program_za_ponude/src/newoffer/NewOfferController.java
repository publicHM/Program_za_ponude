/*
 * NewOffer FXML Controller class. Provides variables and methods for customer data and tables for corresponding treatment
 * data, separated to different tables for each group (upper jaw, lower jaw, other and duration of treatments). Provades 
 * button to save it.
 * 
 * @author Petar Deveric
 */
package newoffer;

import general.DatabaseLocation;
import general.SetNewStage;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import saveAsXLS.OfferToXLS;


public class NewOfferController extends SetNewStage {
    
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
                            setTable(tableUpperJaw, upperJawList,
                                    new Treatment(treatmentID, treatment, quantity, price, discount, total, visit) );
                        } else if(tableName.equals("LowerJaw") ){
                            setTable(tableLowerJaw, lowerJawList,
                                    new Treatment(treatmentID, treatment, quantity, price, discount, total, visit) );
                        } else if(tableName.equals("Other") ){
                            setTable(tableOther, otherList, 
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
            
        //  Sum all amounts and put it in field for total amount
        setTotalFields();
        
        //  Sum amounts per visit and put in in the duration table
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
            closePopupOnMouseClickedOrScrolling();
    }
    
    // The method is called when button "Search" above "Upper Jaw" table is pressed
    @FXML
    private void searchUpperJaw(Event event){
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
            closePopupOnMouseClickedOrScrolling();
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
            closePopupOnMouseClickedOrScrolling();
    }
    
    // The method is called when button "Search" above "Other" table is pressed
    @FXML
    private void searchOther(Event event){
        newItemsPopup(textFieldOther, event, "other");
    }
    
    private void newItemsPopup(TextField textField, Event event, String jaw){
        Bounds boundsInScene = textField.localToScreen(textField.getBoundsInLocal());
        int x = (int) boundsInScene.getMinX();
        int y = (int) boundsInScene.getMinY();
        double width = textField.getWidth();
        double height = textField.getHeight();
        
        try{
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/newoffer/MyPopup.fxml"));
            
                loader.setControllerFactory((Class<?> passedController) -> {
                        MyPopupController controller = new MyPopupController(stage, this, conn, psShowItems,
                                languageNameInDB, textField, jaw);
                        return controller ;

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
            System.err.println("Exception in class NewOffer, method newItemsPopup()");
            e.printStackTrace(System.out);
        }
    }
    
    public void setUpperJawTable(String selectedItem){
        if( ! checkIftreatmentIsAlreadyInTable(selectedItem, upperJawList) )
            setTable(tableUpperJaw, upperJawList, getTreatment(selectedItem) );
    }
    
    public void setLowerJawTable(String selectedItem){
        if( ! checkIftreatmentIsAlreadyInTable(selectedItem, lowerJawList) )
            setTable(tableLowerJaw, lowerJawList, getTreatment(selectedItem) );
    }
    
    public void setOtherTable(String selectedItem){
        if( ! checkIftreatmentIsAlreadyInTable(selectedItem, otherList) )
            setTable(tableOther, otherList, getTreatment(selectedItem) );
    }

    private Treatment getTreatment(String selectedItem){
        int treatmentID = 0;
        String treatmentName = null;
        Float treatmentPrice = 0.0f;
        int treatmentDiscount = 0;
        try{psGetTreatment.setString(1, selectedItem);
            try(ResultSet rsGetTreatment = psGetTreatment.executeQuery();){
                while( rsGetTreatment.next() ){
                    treatmentID = rsGetTreatment.getInt(1);
                    treatmentName = rsGetTreatment.getString(2);
                    treatmentPrice = rsGetTreatment.getFloat(3);
                    treatmentDiscount = rsGetTreatment.getInt(4);
                }
            }
        }
        catch(SQLException e){
            System.err.println("SQLException in class NewOfferController, method getTreatment()");
            e.printStackTrace(System.out);
        }
        String exchangeRate = exchangeRateField.getText();
        if ( !exchangeRate.equals("") || !exchangeRate.equals("1.0"))   treatmentPrice /= Float.parseFloat(exchangeRate);
        float total = treatmentPrice * (100 - treatmentDiscount) / 100;
        
        //  Sum all amounts and put it in field for total amount
        setTotalFields();
        
        //  Sum amounts per visit and put in in the duration table
        setDurationTable();
        
        return new Treatment(treatmentID, treatmentName, 1, treatmentPrice, treatmentDiscount, total, 1);
    }
    
    private void setTable(TableView table, ObservableList<Treatment> list, Treatment treatment){
        closePopupOnMouseClickedOrScrolling();
            
        list.add(treatment);
        list = setItemsNo(list);
        table.getColumns().clear();
        table.setItems(list);
        InitializeTable it = new InitializeTable(table, this);
        table = it.initialize();
    }
    
    private boolean checkIftreatmentIsAlreadyInTable(String selectedItem, ObservableList<Treatment> observableList){
        boolean treatmentAlreadyInTable = false;
        for(Treatment treatment : observableList){
            String treatmentName = treatment.getTreatment();
            if(treatmentName.equals(selectedItem)){
                treatmentAlreadyInTable = true;
                break;
            }
        }
        return treatmentAlreadyInTable;
    }
    
    private ObservableList<Treatment> setItemsNo(ObservableList<Treatment> ol){   
        int k = 1;
        for(Treatment treatment: ol){
            treatment.setNo(k);
            k++;
        }
        return ol;
    }
      
    public void setTotalFields(){
        
        float newBeforeDiscount = sumTotalInTable(upperJawList) + sumTotalInTable(lowerJawList) + sumTotalInTable(otherList);
        totalBeforeDiscountField.setText( String.valueOf(newBeforeDiscount) );
        
        float newDiscount;
        if( cashDiscountPercentageField.getText() == null || cashDiscountPercentageField.getText().equals("")
                || cashDiscountPercentageField.getText().equals("0"))
            newDiscount = 0;
        else{
            int discount = Integer.parseInt( cashDiscountPercentageField.getText() );
            newDiscount = newBeforeDiscount * discount /100;
        }
        totalDiscountField.setText( String.valueOf(newDiscount) );
        
        totalAfterDiscountField.setText( String.valueOf( newBeforeDiscount - newDiscount) );
    }
    
    private Float sumTotalInTable(ObservableList<Treatment> ol){
        float sumOfTotals = 0f;
        if(!ol.isEmpty()){
            for(Treatment treatment : ol){
                sumOfTotals += treatment.getTotal();
            }
        }
        return sumOfTotals;
    }
    
    @FXML
    private void discountChanged(KeyEvent event){
        try{if(event.getCode() == KeyCode.ENTER){
                setTotalFields();
                setDurationTable();
            }
            else if( event.getCode().isLetterKey() || event.getCode() == KeyCode.COMMA || event.getCode() == KeyCode.PERIOD){
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e){
            setOKBox("Please use 0 - 9 keys only", event);
        }

    }
    
    public void setDurationTable(){
        
        if( !durationList.isEmpty() )
            durationList.clear();
        
        Map<Integer, Float> amountPerVisit = new HashMap();
        // Get ammounts per visits for all tables
        amountPerVisit = populateHashMapForAmountsPerVisit(upperJawList, amountPerVisit);
        amountPerVisit = populateHashMapForAmountsPerVisit(lowerJawList, amountPerVisit);
        amountPerVisit = populateHashMapForAmountsPerVisit(otherList, amountPerVisit);
        
        for(Map.Entry<Integer, Float> entry : amountPerVisit.entrySet()){
            int visit = entry.getKey();
            float amountForCards = entry.getValue();
            int discount;
            if( cashDiscountPercentageField.getText() == null || cashDiscountPercentageField.getText().equals(""))
                discount = 0;
            else
                discount = Integer.parseInt( cashDiscountPercentageField.getText() );
            float amountForCash = amountForCards * (100 -  discount)/100;
            Duration duration = new Duration(visit, amountForCards, amountForCash);
            durationList.add(duration);
        }
        
        tableDuraton.getColumns().clear();
        tableDuraton.setItems(durationList);
        InitializeDurationTable idt = new InitializeDurationTable(tableDuraton);
        tableDuraton = idt.initialize();
    }
    
    private Map<Integer, Float> populateHashMapForAmountsPerVisit(ObservableList<Treatment> ol,
            Map<Integer, Float> amountPerVisit){
        for(Treatment treatment : ol){
            int visit = treatment.getVisit();
            float currentAmount = treatment.getTotal();
            float previousAmount;
            if(amountPerVisit.get(visit) == null)
                previousAmount = 0f;
            else
                previousAmount = amountPerVisit.get(visit);

            float totalAmountPerVisit = previousAmount + currentAmount;
            amountPerVisit.put(visit, totalAmountPerVisit);
        }
        return amountPerVisit;
    }
    
    @FXML
    private void exchangeRateFieldEnterPressed(KeyEvent event){
        if(event.getCode() == KeyCode.ENTER){
            try{float exchangeRate = Float.parseFloat( exchangeRateField.getText() );
                changeCurrencySecondStep(exchangeRate);
            } catch (NumberFormatException e){
                setOKBox("Korisite samo brojeve i točku (.) za decimalne vrijednosti", event);
            }
        }
    }
    
    @FXML
    private void changeCurrency(MouseEvent event){
        try{float exchangeRate = Float.parseFloat( exchangeRateField.getText() );
            if( exchangeRate !=  0f && exchangeRate != 1f){
                if(event.getClickCount() == 2 )
                    exchangeRate = 1 / (float) Math.pow(exchangeRate, 2);
                changeCurrencySecondStep(exchangeRate);
            }
        } catch (NumberFormatException e){
            setOKBox("Korisite samo brojeve i točku (.) za decimalne vrijednosti", event);
        }
    }
    
    private void changeCurrencySecondStep(float exchangeRate){
        if ( !upperJawList.isEmpty() ){
            changeJawTableCurrency(upperJawList, exchangeRate);
            tableUpperJaw.refresh();
        }
        if ( !lowerJawList.isEmpty() ){
            changeJawTableCurrency(lowerJawList, exchangeRate);
            tableLowerJaw.refresh();
        }
        if ( !otherList.isEmpty() ){
            changeJawTableCurrency(otherList, exchangeRate);
            tableOther.refresh();
        }
        if ( !durationList.isEmpty() ){
            changeDurationTableCurrency(durationList, exchangeRate);
            tableDuraton.refresh();
        }
    }
    
    private void changeJawTableCurrency(ObservableList<Treatment> observableList, Float exchangeRate){
        for(Treatment treatment : observableList){
            float newPrice = treatment.getPrice() / exchangeRate;
            float newTotal = treatment.getTotal() / exchangeRate;
            treatment.setPrice(newPrice);
            treatment.setTotal(newTotal);
        }
    }
    
    private void changeDurationTableCurrency(ObservableList<Duration> observableList, Float exchangeRate){
        for(Duration duration : observableList){
            float newPriceCards = duration.getPriceCards() / exchangeRate;
            float newPriceCash = duration.getPriceCash() / exchangeRate;
            duration.setPriceCards(newPriceCards);
            duration.setPriceCash(newPriceCash);
        }
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
    
    private void setLanguage(String clientLanguage, boolean changeCurrency, boolean setCurrencyFields){
        switch(clientLanguage){
            case "Engleski Općenito":
                if(changeCurrency)    setCurrency("EUR Općenito", setCurrencyFields);
                languageNameInDB = "EnglishEUR";
                break;
            case "Engleski UK":
                if(changeCurrency)    setCurrency("GBP Engleska", setCurrencyFields);
                languageNameInDB = "EnglishGBP";
                break;
            case "Hrvatski":
                if(changeCurrency)    setCurrency("HRK Hrvatska", setCurrencyFields);
                languageNameInDB = "Croatian";
                break;
            case "Njemački":
                if(changeCurrency)    setCurrency("EUR Njemačka", setCurrencyFields);
                languageNameInDB = "German";
                break;
            case "Talijanski":
                if(changeCurrency)    setCurrency("EUR Italija", setCurrencyFields);
                languageNameInDB = "Italian";
                break;
        }
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
    
    //  Setting up currency values in program
    private void setCurrency(String clientCurrency, boolean setCurrencyFields){

        switch(clientCurrency){
            case "EUR Općenito": 
                currencyNameInDB = "PriceEngEUR";
                discountNameInDB = "DiscountEngEUR";
                if(setCurrencyFields){
                    currency1Field.setText("EUR");
                    currency2Field.setText("EUR");
                }
                break;
            case "GBP Engleska": 
                currencyNameInDB = "PriceEngGBP";
                discountNameInDB = "DiscountEngGBP";
                if(setCurrencyFields){
                    currency1Field.setText("GBP");
                    currency2Field.setText("GBP");
                }
                break;
            case "HRK Hrvatska": 
                currencyNameInDB = "PriceCroHRK";
                discountNameInDB = "DiscountCroHRK";
                if(setCurrencyFields){
                    currency1Field.setText("Kn");
                    currency2Field.setText("Kn");
                }
                break;
            case "EUR Njemačka": 
                currencyNameInDB = "PriceGerEUR";
                discountNameInDB = "DiscountGerEUR";
                if(setCurrencyFields){
                    currency1Field.setText("EUR");
                    currency2Field.setText("EUR");
                }
                break;
            case "EUR Italija":
                currencyNameInDB = "PriceItaEUR";
                discountNameInDB = "DiscountItaEUR";
                if(setCurrencyFields){
                    currency1Field.setText("EUR");
                    currency2Field.setText("EUR");
                }
                break;
        }
        
        if(setCurrencyFields)   databaseCurrencyButton.setText(clientCurrency);
    }
    
    private void changeTablesLanguageAndCurrency(){
        
        exchangeRateField.setText("1.0");
        
        String changeCurrencyQuery = "SELECT " + languageNameInDB + ", "+ currencyNameInDB + " FROM TreatmentList"
                + " WHERE TreatmentID = ? ";
        try(PreparedStatement psChangeCurrency = conn.prepareStatement(changeCurrencyQuery) ){
            if ( !upperJawList.isEmpty() ){
                changeLanguageAndCurrencyForATable(upperJawList, psChangeCurrency);
                tableUpperJaw.refresh();
            }
            if ( !lowerJawList.isEmpty() ){
                changeLanguageAndCurrencyForATable(lowerJawList, psChangeCurrency);
                tableLowerJaw.refresh();
            }
            if ( !otherList.isEmpty() ){
                changeLanguageAndCurrencyForATable(otherList, psChangeCurrency);
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
    
    private void changeLanguageAndCurrencyForATable(ObservableList<Treatment> observableList, 
            PreparedStatement psChangeCurrency){
        
        try{
            for(Treatment treatment : observableList){
                
                int treatmentID = treatment.getTreatmentID();
                int quantity = treatment.getQuantity();
                int discount = treatment.getDiscount();
                
                psChangeCurrency.setInt(1, treatmentID);
                try(ResultSet rs = psChangeCurrency.executeQuery();){
                    
                    rs.next();
                    
                    String newTreatmentName = rs.getString(1);
                    float newPrice = rs.getFloat(2);
                    float newTotal = quantity * newPrice * (100 - discount) / 100;
                    treatment.setTreatment(newTreatmentName);
                    treatment.setPrice(newPrice);
                    treatment.setTotal(newTotal);
                }
            }
        } catch (SQLException e){
            System.err.println("SQLException in class NewOfferController, method changeLanguageAndCurrencyForATable()");
            e.printStackTrace(System.out);
        }
    }
    
    @FXML
    private void closePopupOnMouseClickedOrScrolling(){
        if(popupStage != null)
            popupStage.close();
    }
    
    //  For key "delete" pressed while a table is selected
    @FXML
    private void deleteRow(KeyEvent event){
        if(event.getCode() == KeyCode.DELETE){
            TableView table = (TableView) event.getSource();
            ObservableList list = table.getItems();
            deleteRow(table, list);
        }
    }
    
    private void deleteRow(TableView table, ObservableList<Treatment> rows){
        closePopupOnMouseClickedOrScrolling();
        int selectedRow = table.getSelectionModel().getSelectedIndex();
        rows.remove(selectedRow);
        rows = setItemsNo(rows);
        table.refresh();
        setDurationTable();
        setTotalFields();
    }
    
    @FXML
    private void goBack(Event event){
        closePopupOnMouseClickedOrScrolling();
        setNewStage(event, "/databasse/DataBase.fxml");
        closeConnection();
    }
    
    private void closeConnection(){
        if (conn != null){ 
            try{conn.close();
            }catch(SQLException e){
                System.err.println("SQLException in class NewOfferController, method closeConnection()");
                e.printStackTrace(System.out);
            }
        }
    }
    
    @FXML
    private void saveAsXLS(ActionEvent event){
        if( customerID>0 && offerID >0){
            OfferToXLS save = new OfferToXLS(conn, customerID, offerID, date, event);
            save.saveDataToXLS();
        }
    }
    
    @FXML
    private void saveData(ActionEvent event){
        
        if(customerID == 0){
            // Get highest CustomerID from table Customers in database or set it zero if there is none
            String getHighestCustomerID = "SELECT MAX(CustomerID) FROM Customers";
            
            try(PreparedStatement psMaxCustomerID = conn.prepareStatement(getHighestCustomerID);
                ResultSet rsMaxCustomerID = psMaxCustomerID.executeQuery();){

                //  Get highest customer ID from database. Set it to zero if 'Customers' table is empty
                int highestCustomerID = 0;
                if( rsMaxCustomerID.next() )    highestCustomerID = rsMaxCustomerID.getInt(1);

                // Save data of new customer in 'Cutomers' table in database and check for exception
                boolean exceptionFound = saveCustomerToDatabase(event, highestCustomerID + 1);
                // Save data of new offer in 'Offers' table in database if there was not excaption
                if(!exceptionFound)    saveTablesAsNewOffer(event, highestCustomerID + 1);
            } catch(SQLException e){
                setOKBox("Podaci nisu spremljeni. Provjerite podatke", event);
                System.err.println("SQLException in class NewOfferController, method saveData()");
                e.printStackTrace(System.out);
            }
        }
        else{
            setUpdateOrNewOfferQueryStage(event, this, customerID);
        }
    }
    
    private boolean saveCustomerToDatabase(Event event, int newCustomerID){
        boolean exceptionFound = false;
        String saveNewCustomer = "INSERT INTO Customers (CustomerID, Name, Address, Telephone, Email, Language,"
                    + "StartCurrency, ExchangeRate, FinalCurrency, CashDiscount) VALUES (?,?,?,?,?,?,?,?,?,?)";
        try(PreparedStatement psSaveCustomer = conn.prepareStatement(saveNewCustomer);){
            psSaveCustomer.setInt(1, newCustomerID);
            psSaveCustomer.setString(2, nameField.getText() );
            psSaveCustomer.setString(3, addressArea.getText() );
            psSaveCustomer.setString(4, telephoneField.getText() );
            psSaveCustomer.setString(5, emailField.getText() );
            psSaveCustomer.setString(6, languageButton.getText() );
            psSaveCustomer.setString(7, databaseCurrencyButton.getText() );
            float exchangeRate = 0;
            if( !exchangeRateField.getText().equals("")) 
                exchangeRate = Float.parseFloat(exchangeRateField.getText() );
            psSaveCustomer.setFloat( 8, exchangeRate );
            psSaveCustomer.setString(9, currency2Field.getText() );
            int cashDiscount = 0;
            if( !cashDiscountPercentageField.getText().equals("")) 
                cashDiscount = Integer.parseInt(cashDiscountPercentageField.getText() );
            psSaveCustomer.setInt(10,  cashDiscount );
            psSaveCustomer.executeUpdate();
        } catch(SQLException e){
            setOKBox("Podaci nisu spremljeni. Provjerite podatke", event);
            System.err.println("SQLException in class NewOfferController, method saveCustomerToDatabase()");
            e.printStackTrace(System.out);
            exceptionFound = false;
        }
        return exceptionFound;
    }
    
    public void saveTablesAsNewOffer(Event event, int newCustomerID){
        String getHighestOfferID = "SELECT MAX(OfferID) FROM Offers WHERE YEAR(Date) = YEAR(CURRENT_DATE)";
        try(PreparedStatement psMaxID = conn.prepareStatement(getHighestOfferID);
            ResultSet rsMaxID = psMaxID.executeQuery();){

                // Get the highest offer ID
                if( rsMaxID.next() )    offerID = rsMaxID.getInt(1);

                // Save treatments from offer
                String saveTreatemnts = "INSERT INTO Offers (OfferID, CustomerID, TableName, Treatment, Quantity, Price,"
                    + "Discount, Visit, Date) VALUES (?,?,?,?,?,?,?,?, CURRENT_DATE)";
                try(PreparedStatement psSaveTreatment = conn.prepareStatement(saveTreatemnts)){
                    saveTreatmentsToDatabase(psSaveTreatment, "UpperJaw", upperJawList, offerID + 1,
                            newCustomerID, null);
                    saveTreatmentsToDatabase(psSaveTreatment, "LowerJaw", lowerJawList, offerID + 1,
                            newCustomerID, null);
                    saveTreatmentsToDatabase(psSaveTreatment, "Other", otherList, offerID + 1, newCustomerID, null);
                }
                saveDurationTable(offerID + 1, null);
                setOKBox("Podaci su uspješno spremljeni", event);
                setNewStage(event, "/databasse/DataBase.fxml");
        } catch(SQLException e){
            setOKBox("Podaci nisu spremljeni. Provjerite podatke", event);
            System.err.println("SQLException in class NewOfferController, method saveTablesInDatabase()");
            e.printStackTrace(System.out);
        }
    }
    
    public void saveDataAsUpdate(Event event){
        
        //  Gate date from existing offer
        String getDate = "SELECT DISTINCT Date FROM Offers WHERE OfferID = " + offerID;
        //  Delete old records at corresponding customer ID
        String deleteCustomer = "DELETE FROM Customers WHERE CustomerID = " + customerID;
        //  Delete old records at corresponding offer ID
        String deleteTreatemnts = "DELETE FROM Offers WHERE OfferID = " + offerID;
        // Save treatments from offer
        String saveTreatemnts = "INSERT INTO Offers (OfferID, CustomerID, TableName, Treatment, Quantity, Price,"
            + "Discount, Visit, Date) VALUES (?,?,?,?,?,?,?,?,?)";
        try(PreparedStatement psGetDate = conn.prepareStatement(getDate);
            PreparedStatement psDeleteCustomer = conn.prepareStatement(deleteCustomer);
            PreparedStatement psDeleteTreatment = conn.prepareStatement(deleteTreatemnts);
            PreparedStatement psSaveTreatment = conn.prepareStatement(saveTreatemnts);
            ResultSet rsGetDate = psGetDate.executeQuery();){
            rsGetDate.next();
            Date date = rsGetDate.getDate(1);
            rsGetDate.close();
            
            psDeleteCustomer.executeUpdate();
            saveCustomerToDatabase(event, customerID);
            psDeleteTreatment.executeUpdate();
            saveTreatmentsToDatabase(psSaveTreatment, "UpperJaw", upperJawList, offerID, customerID, date);
            saveTreatmentsToDatabase(psSaveTreatment, "LowerJaw", lowerJawList, offerID, customerID, date);
            saveTreatmentsToDatabase(psSaveTreatment, "Other", otherList, offerID, customerID, date);
            
            saveDurationTable(offerID, date);
            
            setOKBox("Podaci su uspješno spremljeni", event);
            setNewStage(event, "/databasse/DataBase.fxml");
        } catch(SQLException e){
            setOKBox("Podaci nisu spremljeni. Provjerite podatke", event);
            System.err.println("SQLException in class NewOfferController, method saveDataAsUpdate()");
            e.printStackTrace(System.out);
        }
    }
    
    private void saveTreatmentsToDatabase(PreparedStatement psSaveTreatment, String tableName, ObservableList<Treatment> ol,
            int newOfferID, int newCustomerID, Date date) {
        try{
            for(Treatment treatment : ol){
                psSaveTreatment.setInt(1, newOfferID);
                psSaveTreatment.setInt(2, newCustomerID);
                psSaveTreatment.setString(3, tableName);
                psSaveTreatment.setString(4, treatment.getTreatment());
                psSaveTreatment.setInt(5, treatment.getQuantity() );
                psSaveTreatment.setFloat(6, treatment.getPrice() );
                psSaveTreatment.setInt(7, treatment.getDiscount() );
                psSaveTreatment.setInt(8, treatment.getVisit() );
                if(date != null)    psSaveTreatment.setDate(9, date);
                psSaveTreatment.executeUpdate();
            }
        } catch(SQLException e){
            System.err.println("SQLException in class NewOfferController, method saveTreatmentsToDatabase()");
            e.printStackTrace(System.out);
        }
    }
    
    private void saveDurationTable(int newOfferID, Date date){
        //  If exists (date is not null), delete previous records from database 
        if (date != null){
            String deletePrevious = "DELETE FROM Duration WHERE OfferID = ? AND Date = ?";
            try(PreparedStatement psDeletePrevious = conn.prepareStatement(deletePrevious);){
                psDeletePrevious.setInt(1, newOfferID);
                psDeletePrevious.setDate(2, date);
                psDeletePrevious.executeUpdate();
            } catch(SQLException e){
                System.err.println("SQLException in class NewOfferController, method saveDurationTable()");
                e.printStackTrace(System.out);
            }
        //  Set current date if offer is new
        } else {
            date = new Date( ( new java.util.Date() ).getTime() );
        }
            
        String saveDuartion = "INSERT INTO Duration (OfferID, Date, Visit, Duration, Cards, Cash) VALUES (?,?,?,?,?,?)";
        try(PreparedStatement psSaveDuration = conn.prepareStatement(saveDuartion);){
            for(Duration duration : durationList){
                psSaveDuration.setInt(1, newOfferID);
                psSaveDuration.setDate(2, date);
                psSaveDuration.setInt(3, duration.getVisit() );
                psSaveDuration.setString(4, duration.getDuration() );
                psSaveDuration.setFloat(5, duration.getPriceCards() );
                psSaveDuration.setFloat(6, duration.getPriceCash() );
                psSaveDuration.executeUpdate();
            }
        } catch(SQLException e){
            System.err.println("SQLException in class NewOfferController, method saveDurationTable()");
            e.printStackTrace(System.out);
        }
    }
}

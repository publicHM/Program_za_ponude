/**
 * Helper class that saves offer to database
 * 
 * @author Petar
 */
package new_offer.helper;

import general.SetNewStage;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import new_offer.Duration;
import new_offer.Treatment;


public class SaveData extends SetNewStage {
    
    private final Connection conn;
    private int offerID;
    private final int customerID;
    private final ObservableList<Treatment> upperJawList;
    private final ObservableList<Treatment> lowerJawList;
    private final ObservableList<Treatment> otherList;
    private final ObservableList<Duration> durationList;
    private final String name;
    private final String address;
    private final String telephone;
    private final String email;
    private final String language;
    private final String databaseCurrency;
    private final String exchangeRate;
    private final String cashDiscountPercentage;
    private final String currency;
    
    public SaveData(Connection conn, int offerID, int customerID, ObservableList<Treatment> upperJawList,
            ObservableList<Treatment> lowerJawList, ObservableList<Treatment> otherList,
            ObservableList<Duration> durationList, String name, String address, String telephone, String email,
            String language, String databaseCurrency, String exchangeRate, String cashDiscountPercentage,
            String currency){
        this.conn = conn;
        this.offerID = offerID;
        this.customerID = customerID;
        this.upperJawList = upperJawList;
        this.lowerJawList = lowerJawList;
        this.otherList = otherList;
        this.durationList = durationList;
        this.name = name;
        this.address = address;
        this.telephone = telephone;
        this.email = email;
        this.language = language;
        this.databaseCurrency = databaseCurrency;
        this.exchangeRate = exchangeRate;
        this.cashDiscountPercentage = cashDiscountPercentage;
        this.currency = currency;
    }
    
    //  Checks if customer is new (customerID is 0) or not. If customer is new, finds highest customer ID
    //  and adds one to it for curren customer's customer ID. Calls appropriate methods to save offer
    public void saveData(ActionEvent event){
        
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
                System.err.println("SQLException in class SaveData, method saveData()");
                e.printStackTrace(System.out);
            }
        }
        else{
            setUpdateOrNewOfferQueryStage(event, this, customerID);
        }
    }
    
    //  Saves data about customer to database
    private boolean saveCustomerToDatabase(Event event, int newCustomerID){
        boolean exceptionFound = false;
        String saveNewCustomer = "INSERT INTO Customers (CustomerID, Name, Address, Telephone, Email, Language,"
                    + "StartCurrency, ExchangeRate, FinalCurrency, CashDiscount) VALUES (?,?,?,?,?,?,?,?,?,?)";
        try(PreparedStatement psSaveCustomer = conn.prepareStatement(saveNewCustomer);){
            psSaveCustomer.setInt(1, newCustomerID);
            psSaveCustomer.setString(2, name );
            psSaveCustomer.setString(3, address );
            psSaveCustomer.setString(4, telephone );
            psSaveCustomer.setString(5, email );
            psSaveCustomer.setString(6, language );
            psSaveCustomer.setString(7, databaseCurrency );
            float exchRateFloat = 0;
            if( !exchangeRate.equals("") ) 
                exchRateFloat = Float.parseFloat(exchangeRate );
            psSaveCustomer.setFloat( 8, exchRateFloat );
            psSaveCustomer.setString(9, currency );
            int cashDiscount = 0;
            if( !cashDiscountPercentage.equals("")) 
                cashDiscount = Integer.parseInt(cashDiscountPercentage );
            psSaveCustomer.setInt(10,  cashDiscount );
            psSaveCustomer.executeUpdate();
        } catch(SQLException e){
            setOKBox("Podaci nisu spremljeni. Provjerite podatke", event);
            System.err.println("SQLException in class SaveData, method saveCustomerToDatabase()");
            e.printStackTrace(System.out);
            exceptionFound = false;
        }
        return exceptionFound;
    }
    
    //  Saves tabels (offer data) as a new offer
    public void saveTablesAsNewOffer(Event event, int newCustomerID){
        String getHighestOfferID = "SELECT MAX(OfferID) FROM Offers WHERE YEAR(Date) = YEAR(CURRENT_DATE)";
        try(PreparedStatement psMaxID = conn.prepareStatement(getHighestOfferID);
            ResultSet rsMaxID = psMaxID.executeQuery();){

                // Get the highest offer ID
                if( rsMaxID.next() )    offerID = rsMaxID.getInt(1);
                
                // Save treatments from offer
                String saveTreatemnts = "INSERT INTO Offers (OfferID, CustomerID, TableName, TreatmentID, Treatment,"
                        + "Quantity, Price, Discount, Visit, Date) VALUES (?,?,?,?,?,?,?,?,?, CURRENT_DATE)";
                try(PreparedStatement psSaveTreatment = conn.prepareStatement(saveTreatemnts)){
                    saveTreatmentsToDatabase(psSaveTreatment, "UpperJaw", upperJawList, offerID + 1,
                            newCustomerID, null);
                    saveTreatmentsToDatabase(psSaveTreatment, "LowerJaw", lowerJawList, offerID + 1,
                            newCustomerID, null);
                    saveTreatmentsToDatabase(psSaveTreatment, "Other", otherList, offerID + 1, newCustomerID, null);
                }
                saveDurationTable(offerID + 1, null);
                setOKBox("Podaci su uspješno spremljeni", event);
                setNewStage(event, "/databasse/view/DataBase.fxml");
        } catch(SQLException e){
            setOKBox("Podaci nisu spremljeni. Provjerite podatke", event);
            System.err.println("SQLException in class SaveData, method saveTablesAsNewOffer()");
            e.printStackTrace(System.out);
        }
    }
    
    //  Saves tabels (offer data) as an update
    public void saveDataAsUpdate(Event event){
        
        //  Gate date from existing offer
        String getDate = "SELECT DISTINCT Date FROM Offers WHERE OfferID = " + offerID;
        //  Delete old records at corresponding customer ID
        String deleteCustomer = "DELETE FROM Customers WHERE CustomerID = " + customerID;
        //  Delete old records at corresponding offer ID
        String deleteTreatemnts = "DELETE FROM Offers WHERE OfferID = " + offerID;
        // Save treatments from offer
        String saveTreatemnts = "INSERT INTO Offers (OfferID, CustomerID, TableName, TreatmentID, Treatment, Quantity,"
                + "Price, Discount, Visit, Date) VALUES (?,?,?,?,?,?,?,?,?,?)";
        try(PreparedStatement psGetDate = conn.prepareStatement(getDate);
            PreparedStatement psDeleteCustomer = conn.prepareStatement(deleteCustomer);
            PreparedStatement psDeleteTreatment = conn.prepareStatement(deleteTreatemnts);
            PreparedStatement psSaveTreatment = conn.prepareStatement(saveTreatemnts);
            ResultSet rsGetDate = psGetDate.executeQuery();){
            rsGetDate.next();
            Date tempDate = rsGetDate.getDate(1);
            rsGetDate.close();
            
            psDeleteCustomer.executeUpdate();
            saveCustomerToDatabase(event, customerID);
            psDeleteTreatment.executeUpdate();
            saveTreatmentsToDatabase(psSaveTreatment, "UpperJaw", upperJawList, offerID, customerID, tempDate);
            saveTreatmentsToDatabase(psSaveTreatment, "LowerJaw", lowerJawList, offerID, customerID, tempDate);
            saveTreatmentsToDatabase(psSaveTreatment, "Other", otherList, offerID, customerID, tempDate);
            
            saveDurationTable(offerID, tempDate);
            
            setOKBox("Podaci su uspješno spremljeni", event);
            setNewStage(event, "/databasse/view/DataBase.fxml");
        } catch(SQLException e){
            setOKBox("Podaci nisu spremljeni. Provjerite podatke", event);
            System.err.println("SQLException in class SaveData, method saveDataAsUpdate()");
            e.printStackTrace(System.out);
        }
    }
    
    //  Saves treatments from passed list to database
    private void saveTreatmentsToDatabase(PreparedStatement psSaveTreatment, String tableName,
            ObservableList<Treatment> ol, int newOfferID, int newCustomerID, Date date) {
        try{
            for(Treatment treatment : ol){
                psSaveTreatment.setInt(1, newOfferID);
                psSaveTreatment.setInt(2, newCustomerID);
                psSaveTreatment.setString(3, tableName);
                psSaveTreatment.setInt(4, treatment.getTreatmentID() );
                psSaveTreatment.setString(5, treatment.getTreatment() );
                psSaveTreatment.setInt(6, treatment.getQuantity() );
                psSaveTreatment.setFloat(7, treatment.getPrice() );
                psSaveTreatment.setInt(8, treatment.getDiscount() );
                psSaveTreatment.setInt(9, treatment.getVisit() );
                if(date != null)    psSaveTreatment.setDate(10, date);
                psSaveTreatment.executeUpdate();
            }
        } catch(SQLException e){
            System.err.println("SQLException in class SaveData, method saveTreatmentsToDatabase()");
            e.printStackTrace(System.out);
        }
    }
    
    //  saves duration table
    private void saveDurationTable(int newOfferID, Date date){
        //  If exists (date is not null), delete previous records from database 
        if (date != null){
            String deletePrevious = "DELETE FROM Duration WHERE OfferID = ? AND Date = ?";
            try(PreparedStatement psDeletePrevious = conn.prepareStatement(deletePrevious);){
                psDeletePrevious.setInt(1, newOfferID);
                psDeletePrevious.setDate(2, date);
                psDeletePrevious.executeUpdate();
            } catch(SQLException e){
                System.err.println("SQLException in class SaveData, method saveDurationTable()");
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
            System.err.println("SQLException in class SaveData, method saveDurationTable()");
            e.printStackTrace(System.out);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {}
}

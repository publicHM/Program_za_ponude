/*
 * ChangeData FXML controller class shows table with all treatmeants names, discounts and prices in two chosen languages.
 * It provides methods for changing that data and saving this changes.
 * 
 * @author Petar
 */
package settings;

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
import javafx.fxml.FXML;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;


public class ChangeDataController extends SetNewStage {
    
    private ObservableList<AddData> rows;
    private String languageOne;
    private String priceOne;
    private String discountOne;
    private String languageTwo;
    private String priceTwo;
    private String discountTwo;
    private String databaseLocation;
    
        
    @FXML
    private MenuButton language1;
    
    @FXML
    private MenuButton language2;
    
    @FXML
    private TableView table;
    
    public ChangeDataController(){
        languageOne = null;
        priceOne = null;
        discountOne = null;
        languageTwo = null;
        priceTwo = null;
        discountTwo = null;
        getLocation();
    }
    
    //  Retrieves main database location from local database
    private void getLocation(){
        databaseLocation = null;
        
        try(Connection locationConn = DriverManager.getConnection(
                "jdbc:derby:LocalDatabase; user=pdeveric; password=dyifwu3m;");
            PreparedStatement ps = locationConn.prepareStatement("SELECT Location FROM Location");
            ResultSet rs = ps.executeQuery();){
            if( rs.next() )
                databaseLocation =  rs.getString(1);
        }
        catch(SQLException e){
            e.printStackTrace(System.out);
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        refresh();
    }
    
    //  Calls method for retrieving data about treatments and class and methods for initializing table that shows
    //  that treatments data
    private void refresh(){
        rows = FXCollections.observableArrayList();
        getData();
        table.setItems(rows);
        InitializeTable it = new InitializeTable(table);
        table = it.initialize();
    }
    
    //  Retrieves all treatments with belonging prices and discounts for two choosen languages
    private void getData(){
        String langOne = language1.getText();
        String langTwo = language2.getText();
        
        //  Sets fields names for language one SQL query
        switch(langOne){
            case "Engleski Općenito": 
                languageOne = "EnglishEUR";
                priceOne = "PriceEngEUR";
                discountOne = "DiscountEngEUR";
                break;
            case "Engleski UK": 
                languageOne = "EnglishGBP";
                priceOne = "PriceEngGBP";
                discountOne = "DiscountEngGBP";
                break;
            case "Hrvatski": 
                languageOne = "Croatian";
                priceOne = "PriceCroHRK";
                discountOne = "DiscountCroHRK";
                break;
            case "Njemački": 
                languageOne = "German";
                priceOne = "PriceGerEUR";
                discountOne = "DiscountGerEUR";
                break;
            case "Talijanski": 
                languageOne = "Italian";
                priceOne = "PriceItaEUR";
                discountOne = "DiscountItaEUR";
                break;
        }
        
        //  Sets fields names for language two SQL query
        switch(langTwo){
            case "Engleski Općenito": 
                languageTwo = "EnglishEUR";
                priceTwo = "PriceEngEUR";
                discountTwo = "DiscountEngEUR";
                break;
            case "Engleski UK": 
                languageTwo = "EnglishGBP";
                priceTwo = "PriceEngGBP";
                discountTwo = "DiscountEngGBP";
                break;
            case "Hrvatski": 
                languageTwo = "Croatian";
                priceTwo = "PriceCroHRK";
                discountTwo = "DiscountCroHRK";
                break;
            case "Njemački": 
                languageTwo = "German";
                priceTwo = "PriceGerEUR";
                discountTwo = "DiscountGerEUR";
                break;
            case "Talijanski": 
                languageTwo = "Italian";
                priceTwo = "PriceItaEUR";
                discountTwo = "DiscountItaEUR";
                break;
        }
        
        String treatmentQuery = "SELECT TreatmentID, " + languageOne + ", " + priceOne + ", " + discountOne + ", "  + languageTwo
                + ", " + priceTwo + ", " + discountTwo + " FROM TreatmentList WHERE " + languageOne + " IS NOT NULL OR " 
                + languageTwo + " IS NOT NULL";
        
        //  Gets treatment inforamtion from database for choosen two languages and ads data to list
        try(Connection conn = DriverManager.getConnection("jdbc:derby:" + databaseLocation);
            PreparedStatement ps = conn.prepareStatement(treatmentQuery);
            ResultSet rs = ps.executeQuery();){
            while( rs.next() ){
                AddData ad = new AddData(rs.getInt(1), rs.getString(2), rs.getFloat(3), rs.getInt(4), rs.getString(5),
                        rs.getFloat(6), rs.getInt(7));
                rows.add(ad);
            }
        }
        catch(SQLException e){
            System.err.println("SQLException in class ChangeDataController, method getData()");
            e.printStackTrace(System.out);
        }
        catch(Exception e){
            System.err.println("Exception in class ChangeDataController, method getData()");
            e.printStackTrace(System.out);
        }
    }
    
    //  Saves treatment information from table to database
    @FXML
    private void saveData(ActionEvent event){
        String update = "UPDATE TreatmentList SET " + languageOne + " =?, " + priceOne + " =?, " + discountOne
                + " =?, " + languageTwo + " =?, " + priceTwo + " =?, " + discountTwo + " =? WHERE TreatmentID = ?";
        String saveNew = "INSERT INTO TreatmentList (TreatmentID, " + languageOne + ", " + priceOne + ", " + discountOne
                + ", " + languageTwo + ", " + priceTwo + ", " + discountTwo + ") VALUES (?,?,?,?,?,?,?)";
        String biggestID = "SELECT MAX(TreatmentID) FROM TreatmentList";
        
        try(Connection conn = DriverManager.getConnection("jdbc:derby:" + databaseLocation);
            //  Prepared statement for case when treatment is already in database and should be updated
            PreparedStatement psUpdate = conn.prepareStatement(update);
            //  Prepared statement for case when treatment is not already in database and should be saved as new
            PreparedStatement psSaveNew = conn.prepareStatement(saveNew);
            //  Prepared statement for cfinding biggest id (number)
            PreparedStatement psBiggestID = conn.prepareStatement(biggestID);){
            
            for (AddData ad : rows) {
                int id = ad.getID();
                
                // Check if enty is new and assign new ID if it is new. Save data.
                if(id == 0){
                    try(ResultSet rs = psBiggestID.executeQuery();){
                        rs.next();
                        id = rs.getInt(1);
                    }
                    psSaveNew.setInt(1, id);
                    psSaveNew.setString(2, ad.getLanguageOne());
                    psSaveNew.setFloat(3, ad.getPriceOne());
                    psSaveNew.setInt(4, ad.getDiscountOne());
                    psSaveNew.setString(5, ad.getLanguageTwo());
                    psSaveNew.setFloat(6, ad.getPriceTwo());
                    psSaveNew.setInt(7, ad.getDiscountTwo());
                    psSaveNew.executeUpdate();
                }
                else{
                    psUpdate.setString(1, ad.getLanguageOne() );
                    psUpdate.setFloat(2, ad.getPriceOne() );
                    psUpdate.setInt(3, ad.getDiscountOne() );
                    psUpdate.setString(4, ad.getLanguageTwo() );
                    psUpdate.setFloat(5, ad.getPriceTwo() );
                    psUpdate.setInt(6, ad.getDiscountTwo() );
                    psUpdate.setInt(7, id );
                }
            }
            //  Shows new stage with message that saving was successful
            setOKBox("Podaci su uspješno spremljeni", event);
        }
        //  Catches SQL exception and shows new stage with message that saving was not successful
        catch(SQLException e){
            setOKBox("Pogreška. Podaci nisu spremljeni.", event);
            System.err.println("SQLException in class ChangeDataController, method saveData()");
            e.printStackTrace(System.out);
        }
        catch(Exception e){
            System.err.println("Exception in class ChangeDataController, method saveData()");
            e.printStackTrace(System.out);
        }
    }
    
    //  Adds new row to table
    @FXML
    private void addNewRow(){
        rows.add(new AddData(0, null, 0, 0, null, 0, 0));
    }
    
    //  Deletes selected row from table
    @FXML
    private void deleteRow(){
        int selectedRow = table.getSelectionModel().getSelectedIndex();
        rows.remove(selectedRow);
    }
    
    //  Sets language one to belonging menu button and refreshes table
    @FXML
    private void setLanguage1(ActionEvent event){
        MenuItem menuItem = (MenuItem) event.getSource();
        language1.setText(menuItem.getText());
        table.getColumns().clear();
        refresh();
    }
    
    //  Sets language two to belonging menu button and refreshes table
    @FXML
    private void setLanguage2(ActionEvent event){
        MenuItem menuItem = (MenuItem) event.getSource();
        language2.setText(menuItem.getText());
        table.getColumns().clear();
        refresh();
    }
    
    //  Stage changes back to Settings screen
    @FXML
    private void goBack(ActionEvent event){
        setNewStage(event, "/settings/view/Settings.fxml");
    }
}

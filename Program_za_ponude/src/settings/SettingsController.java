/*
 * Settings FXML controller class has methods for opening screens to manipulate treatments, company and database
 * location data.
 *
 * @author Petar Deveric
 */
package settings;

import general.DatabaseLocation;
import general.SetNewStage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;


public class SettingsController extends SetNewStage {
    
    private String location;
    
    @FXML
    private TextField databaseLocation;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeFieldDatabaseLocation();
    }    
    
    //  Set database location path into databaseLocation text field
    private void initializeFieldDatabaseLocation(){
        //  Get database location
        location = new DatabaseLocation().getDatabaseLocation();
        databaseLocation.setText( location );
    }
    
    //  Save database location path from databaseLocation text field
    @FXML
    private void saveDatabaseLocation(ActionEvent event){
        //  Retrieves locaton of database that is written by user in text field
        location = databaseLocation.getText();
        //  Saves database locations to databaseLocation.ser file
        String path = System.getProperty("user.dir")  + "\\documents\\databaseLocation.ser";
        try(FileOutputStream fos = new FileOutputStream(path);
            ObjectOutputStream oos = new ObjectOutputStream(fos);){
            oos.writeObject(location);
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
        
        //  Tries to connect to database with new database path. If connection is established, shows new stage
        //  with message that says location is successfully saved. Otherwise, shows new stage with message
        //  that says location address is wrong
        try(Connection conn = DriverManager.getConnection("jdbc:derby:" + location);){
            setOKBox("Lokacija je uspješno spremljena", event);
        } catch(SQLException e){
            setOKBox("Adresa lokacije je pogrešna", event);
            e.printStackTrace(System.out);
        }
    }
    
    //  Open window for changing data about company
    @FXML
    private void changeCompanyInfo(ActionEvent event){
        setNewStage(event, "/settings/view/ChangeCompanyInfo.fxml");
    }
    
    //  Open window for adding new users
    @FXML
    private void addUser(ActionEvent event){
        setNewStage(event, "/settings/view/LogIn.fxml");
    }
    
    //  Make backup so data coud be retreived in case of exceptions while uploding new data
    @FXML
    private void databaseBackup(ActionEvent event){
        String delete = "DELETE FROM TreatmentListBackup";
        String insert = "INSERT INTO TreatmentListBackup SELECT * FROM TreatmentList";
        deleteAndInsert(delete, insert, "Backup uspješno obavljen", event);
    }
    
    //  Deletes data in TreatmentListBackup table and saves new data from TreatmentList table
    private void deleteAndInsert(String delete, String insert, String message, ActionEvent event){
        try(Connection conn = DriverManager.getConnection("jdbc:derby:" + location);
            PreparedStatement psDelete = conn.prepareStatement(delete);
            PreparedStatement psInsert = conn.prepareStatement(insert);){
            psDelete.executeUpdate();
            psInsert.executeUpdate();
            setOKBox(message, event);
        }
        catch(SQLException e){
            setOKBox("Restart/Backup nije uspio", event );
            e.printStackTrace(System.out);
        }
    }
    
    //  Opens window for changing treatment data in database (treatment name, price, discount)
    @FXML
    private void openChangeData(ActionEvent event){
        setNewStage(event, "/settings/view/ChangeData.fxml");
    }
    
    //  Calls helper class that imports treatments from *.csv file and saves it to treatment table in database
    @FXML
    private void uploadData(ActionEvent event){
        new ImportCSVAndSaveTreatments(location, event).uploadData();
    }
    
    
    //  Restart data in case of exceptions while uploding new data
    @FXML
    private void databaseRestart(ActionEvent event){
        String delete = "DELETE FROM TreatmentList";
        String insert = "INSERT INTO TreatmentList SELECT * FROM TreatmentListBackup";
        deleteAndInsert(delete, insert, "Restart uspješno obavljen", event);
    }
    
    //  Stage changes back to Settings screen
    @FXML
    private void goBack(ActionEvent event){
        setNewStage(event, "/welcome/view/Welcome.fxml");
    }
}

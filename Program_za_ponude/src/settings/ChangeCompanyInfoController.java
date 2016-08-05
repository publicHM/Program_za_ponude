/**
 * FXML ChangeCompanyInfo Controller class
 *
 * Controller does following tasks: 
 * 1. Retrieves and saves information about company into database
 * 2. Overrides images (logo and other image) that are going to be used on exported excel
 * 
 * Images are overridden in src folders as program was constantly making exception while trying to convert File to
 * Image, even although numerous ways were tried (getting paths dynamically, using streams etc.)
 * 
 * @author Petar
 */
package settings;

import general.DatabaseLocation;
import general.SetNewStage;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;


public class ChangeCompanyInfoController extends SetNewStage {
    
    private String databaseLocation;
    
    @FXML
    private TextField addressField;
    
    @FXML
    private TextField telephoneField;
    
    @FXML
    private TextField emailField;
    
    @FXML
    private TextField webField;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setTextFieldsAndImageAddress();
    }
    
    //  Retrieves company information from database and sets it to apropriate text fields
    private void setTextFieldsAndImageAddress(){
        
        //  Get database location
        databaseLocation = new DatabaseLocation().getDatabaseLocation();
        
        try(Connection conn = DriverManager.getConnection("jdbc:derby:" + databaseLocation);
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM CompanyInfo");
            ResultSet rs = ps.executeQuery();){
            rs.next();
                addressField.setText( rs.getString(1) );
                telephoneField.setText( rs.getString(2) );
                emailField.setText( rs.getString(3) );
                webField.setText( rs.getString(4) );
        }
        catch(SQLException e){
            System.err.println("SQLException in class ChangeDataController, method getData()");
            e.printStackTrace(System.out);
        }
    }
    
    //  Stage changes back to Settings screen
    @FXML
    private void goBack(ActionEvent event){
        setNewStage(event, "/settings/view/Settings.fxml");
    }
    
    //  Saves texs from text fields to database
    @FXML
    private void saveData(ActionEvent event){
         try(Connection conn = DriverManager.getConnection("jdbc:derby:" + databaseLocation);
            PreparedStatement ps = conn.prepareStatement("UPDATE CompanyInfo SET Address = ?, Telephone = ?,"
                    + " Email = ?, WebPage = ?");){
                ps.setString(1, addressField.getText() );
                ps.setString(2, telephoneField.getText() );
                ps.setString(3, emailField.getText() );
                ps.setString(4, webField.getText() );
                ps.executeUpdate();
                setOKBox("Poadaci su spremljeni", event);
                setNewStage(event, "/settings/view/Settings.fxml");
        }
        catch(SQLException e){
            setOKBox("Došlo je do pogreške. \nMolimo provjerite podatke", event);
            System.err.println("SQLException in class ChangeDataController, method setData()");
            e.printStackTrace(System.out);
        }
    }
}

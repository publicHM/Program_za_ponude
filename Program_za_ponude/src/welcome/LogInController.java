/**
 * Login FXML controller class. Takes username and password from text fields in FXML and checks if they are in database.
 * If they are in database opens new welcome screen. If not, shows appropriate warning message.
 * 
 * @author Petar Deveric
 */
package welcome;

import general.DatabaseLocation;
import general.SetNewStage;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;


public class LogInController extends SetNewStage {
    
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private ImageView logo;
    
    @FXML
    private ImageView otherImage;
    
    //  Call method for login if key "enter" is pressed while username or password field is selected
    @FXML
    private void logInOnEnterPressed(KeyEvent event){
        if(event.getCode() == KeyCode.ENTER){
            logIn(event);
        }
    }
    
    //  Method for login. Takes username and password, checks if they are in database and logs in if they are.
    //  If not, calls method for popup window that shows passed warning message.
    @FXML
    private void logIn(Event event){
        String username = usernameField.getText();
        String password = passwordField.getText();
        
        //  Check if username and corresponding password are in database. Proceed to new window if they are.
        
        //  First check if username is "admin" as its data is in different database then other username and passwords.
        //  If username is not admin, check another database
        if ( username.equals("admin") ){
            //  Username: admin ; Password: admin
            String logInQuery = "SELECT Username FROM Users WHERE Username = ? AND Password = ?";
            try(Connection conn = DriverManager.getConnection("jdbc:derby:LocalDatabase; user=pdeveric; password=dyifwu3m;");
                PreparedStatement psLogIn = conn.prepareStatement(logInQuery);){
                psLogIn.setString(1, username);
                psLogIn.setString(2, password);
                try(ResultSet rsLogIn = psLogIn.executeQuery(); ){
                    if( rsLogIn.next() )    setNewStage(event, "/welcome/view/Welcome.fxml" );
                    else    setOKBox("Neispravao korisničko ime i/ili lozinka", event);
                }
            } catch (SQLException e){
                System.err.println("SQLException in class LogInController, method logIn().");
                e.printStackTrace(System.out);
            }
        } else {
            String databaseLocation = new DatabaseLocation().getDatabaseLocation();
            
            try(Connection conn = DriverManager.getConnection("jdbc:derby:" + databaseLocation);
                PreparedStatement psLogIn = conn.prepareStatement("SELECT Username, Password FROM Users WHERE "
                        + "Username = ? AND Password = ?"); ){
                psLogIn.setString(1, username);
                psLogIn.setString(2, password);
                    try(ResultSet rsLogIn = psLogIn.executeQuery();){
                        if( rsLogIn.next() )    setNewStage(event, "/welcome/view/Welcome.fxml" );
                        else    setOKBox("Neispravao korisničko ime i/ili lozinka", event);
                    }
                }
                catch(SQLException e){
                    e.printStackTrace(System.out);
                }
        }
        
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {}
}

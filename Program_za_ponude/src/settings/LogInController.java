/**
 * LogIn FXML controller class. Takes username and password from text fields in FXML and checks if they are in database.
 * If they are in database opens screen for manipulating users login data. If not, shows appropriate warning message.
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
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;


public class LogInController extends SetNewStage {
    
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;

    @Override
    public void initialize(URL url, ResourceBundle rb) {}
    
    //  Calls logIn method if key "Enter" is pressed while username or password field is selected
    @FXML
    private void logInOnEnterPressed(KeyEvent event){
        if(event.getCode() == KeyCode.ENTER){
            logIn(event);
        }
    }
    
    //  Checks if username and password corresponds admin's username and password. If not, opens new stage with
    //  appropriate warning message. If it does, procedes to new window for managing users accounts
    @FXML
    private void logIn(Event event){
        String username = usernameField.getText();
        String password = passwordField.getText();
        
        if ( username.equals("admin") ){
            //  Username: admin ; Password: admin
            String logInQuery = "SELECT Username FROM Users WHERE Username = ? AND Password = ?";
            try(Connection conn = DriverManager.getConnection("jdbc:derby:LocalDatabase; user=pdeveric; password=dyifwu3m;");
                PreparedStatement psLogIn = conn.prepareStatement(logInQuery);){
                psLogIn.setString(1, username);
                psLogIn.setString(2, password);
                try(ResultSet rsLogIn = psLogIn.executeQuery(); ){
                    //  Procedes to new window for managing users accounts if username and password are good 
                    //  (result set has next)
                    if( rsLogIn.next() )    setNewStage(event, "/settings/view/AddUser.fxml" );
                    //  Opens new stage with warning message that says username or password is wrong
                    else    setOKBox("Neispravao korisničko ime i/ili lozinka", event);
                }
            } catch (SQLException e){
                System.err.println("SQLException in class LogInController, method logIn().");
                e.printStackTrace(System.out);
            }
        } else {
            //  Opens new stage with warning message that says username is wrong
            setOKBox("Neispravao korisničko ime", event);
        }
    }
    
    //  Stage changes back to Settings screen
    @FXML
    private void goBack(ActionEvent event){
        setNewStage(event, "/settings/view/Settings.fxml");
    }
    
}

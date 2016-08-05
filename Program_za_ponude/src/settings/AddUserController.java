/**
 * AddUser FXML Controller class saves new username and password. It takes username and password from text fields in FXML
 * and checks if they are in database. If they are in database shows appropriate warning message. If not, saves them to
 * database.
 * 
 * @author Petar Deveric
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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;


public class AddUserController extends SetNewStage {
    
    private ObservableList<String> list;
    private String databaseLocation;
    
    @FXML
    private TextField usernameField;
    
    @FXML
    private TextField passwordField;
    
    @FXML
    private ListView listView;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        list = FXCollections.observableArrayList();
        //  Get database location
        databaseLocation = new DatabaseLocation().getDatabaseLocation();
        restartListView();
    }
    
    //  Get all userenames from database and add them to list
    private void restartListView(){
            
        try(Connection conn = DriverManager.getConnection("jdbc:derby:" + databaseLocation);
            PreparedStatement psUsers = conn.prepareStatement("SELECT Username FROM Users");
            ResultSet rsUsers = psUsers.executeQuery();){

            while ( rsUsers. next() ){
                list.add( rsUsers.getString(1) );
            }
        }
        catch(SQLException e){
            e.printStackTrace(System.out);
        }
        
        listView.setItems(list);
    }
    
    //  Call method for adding user when key "Enter" is pressed while username or password field is selected
    @FXML
    private void addUserOnEnterPressed(KeyEvent event){
        if(event.getCode() == KeyCode.ENTER){
            addUser(event);
        }
    }
    
    @FXML
    private void addUser(Event event){
        String user = usernameField.getText();
        String password = passwordField.getText();
        
        //  Check if username is equal to 'admin'. If it is, don't save and set warning that username already exists
        if ( user.equals("admin") ){
            setOKBox("Korisničko ime već postoji", event);
        }
        //  Check if username has more than 25 charracters or less then 2 characters. If it has, don't save and set 
        //  warning that username must have between 2 and 25 characters
        else if( user.length() > 25 || user.length() < 2 ){
            setOKBox("Korisničko ime treba sadržavati od 2 do 25 znakova", event);
        }
        //  Check if password has more than 15 charracters or less then 4 characters. If it has, don't save and set 
        //  warning that username must have between 4 and 15 characters
        else if( password.length() > 15 || password.length() < 4){
            setOKBox("Lonzinka treba sadržavati od 4 do 15 znakova", event);
        }
        //  Check if username or password has empty spaces. If it has, don't save and set 
        //  warning that username and password can't contain empty spaces
        else if( user.contains(" ") || password.contains(" ") ){
            setOKBox("Korisničko ime i lonzinka ne smiju\nsadžavati prazan prostor (razmak)", event);
        }
        else {
            try(Connection conn = DriverManager.getConnection("jdbc:derby:" + databaseLocation);
            PreparedStatement psGetUsername = conn.prepareStatement("SELECT Username FROM Users WHERE Username = ?");
            PreparedStatement psSetUsername = conn.prepareStatement("INSERT INTO Users (Username, Password) "
                    + "VALUES (?,?)");){
            
            //  Check if username is already in database. If it is, don't save and set warning that username already exists.
            //  If it is not, save new user with password
            psGetUsername.setString(1, user);
            ResultSet rsGetUsername = psGetUsername.executeQuery();
            if( rsGetUsername.next() ){
                setOKBox("Korisničko ime već postoji", event);
                rsGetUsername.close();
            } else {
                rsGetUsername.close();
                psSetUsername.setString(1, user);
                psSetUsername.setString(2, password);
                psSetUsername.executeUpdate();
                setOKBox("Novi korisnik uspješno spremljen.", event);
                list.add(user);
                listView.refresh();
            }
            } catch(SQLException e){
                e.printStackTrace(System.out);
            }
        }
    }
    
    //  Calls methotd for deliting user if key "Delete" is pressed while listView table is selected
    @FXML
    private void deleteUserOnEnterPressed(KeyEvent event){
        if(event.getCode() == KeyCode.DELETE){
            deleteUser(event);
        }
    }
    
    //  Deletes user from database
    @FXML
    private void deleteUser(Event event){
        int index = listView.getSelectionModel().getSelectedIndex();
        
        //  Checks if username is selected. If it is, deletes selected user. If not, sets warning that user is not selected
        if(index == -1){
            setOKBox("Odaberite korisničko ime u tabeli", event);
        } else {
            String selctedUsername = list.get(index);

            try(Connection conn = DriverManager.getConnection("jdbc:derby:" + databaseLocation);
            PreparedStatement psDelete = conn.prepareStatement("DELETE FROM Users WHERE Username = ?");){
                psDelete.setString(1, selctedUsername);
                psDelete.executeUpdate();
            } catch(SQLException e){
                e.printStackTrace(System.out);
            }

            list.remove(index);
            listView.refresh();
        }
    }
    
    @FXML
    private void goBack(ActionEvent event){
        setNewStage(event, "/settings/view/Settings.fxml");
    }
}

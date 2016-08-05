/**
 *  Helper class that imports treatments from .CSV file and saves it to treatment table in database
 * 
 * @author Petar Deverić
 */
package settings;

import general.SetNewStage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.Event;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


public class ImportCSVAndSaveTreatments extends SetNewStage{
    
    private final String location;
    private final Event event;
    
    //  Constructors get information of database location and event that triggered call for this class
    public ImportCSVAndSaveTreatments(String location, Event event){
        this.location = location;
        this.event = event;
    }
    
    //  Opens new file chooser window for .csv files, reads file row by row, splits row in String array
    //  that holds separated information about treatment and passes it to (calls) method "update" to save treatment
    public void uploadData(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open CSV File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showOpenDialog(new Stage() );
        
        if (file != null) {
            try(FileInputStream fileStream = new FileInputStream(file);
                InputStreamReader inputStreamReader = new InputStreamReader(fileStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);){
                
                String deleteQuery = "DELETE FROM TreatmentList";
                String insertQuery = "INSERT INTO TreatmentList (TreatmentID, Croatian, EnglishEUR, EnglishGBP, German, "
                        + "Italian, PriceCroHRK, DiscountCroHRK, PriceEngEUR, DiscountEngEUR, PriceEngGBP, DiscountEngGBP, "
                        + "PriceGerEUR, DiscountGerEUR, PriceItaEUR, DiscountItaEUR) VALUES "
                        + "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

                try(Connection conn = DriverManager.getConnection("jdbc:derby:" + location);
                    PreparedStatement psDelete = conn.prepareStatement(deleteQuery);
                    PreparedStatement psInsert = conn.prepareStatement(insertQuery);){
                    psDelete.executeUpdate();
                    
                    String rowData;
                    while ( (rowData = reader.readLine()) != null ){
                        String[] dataArray = rowData.split(";", 16);
                        update(psInsert, dataArray);
                    }
                    setOKBox("Podaci su uspješno učitani", event );
                }
                catch(SQLException e){
                    setOKBox("Postoji problem. Podaci nisu učitani.", event );
                    System.err.println("SQLException in class uploadData, method main()");
                    e.printStackTrace(System.out);
                }
            } catch (IOException ioe) {
                System.out.println("IOException in class SettingsController, method uploadData()");
                ioe.printStackTrace(System.out);
            }
        }
    }
    
    //  Saves new treatment taking values from String array
    private void update(PreparedStatement psInsert, String[] dataArray){
        try{//  Check if some values are null or strings without characters and assign zero to them.
            //  This is necessary for strings that will be parsed to integer or float so exception 
            //  would not be throwen.
            String priceCroHRK = dataArray[6];
            if( priceCroHRK.equals("") )   priceCroHRK = "0.0";

            String discountCroHRK = dataArray[7];
            if( discountCroHRK.equals("") )   discountCroHRK = "0";

            String priceEngEUR = dataArray[8];
            if( priceEngEUR.equals("") )   priceEngEUR = "0.0";

            String discountEngEUR = dataArray[9];
            if( discountEngEUR.equals("") )   discountEngEUR = "0";

            String priceEngGBP = dataArray[10];
            if( priceEngGBP.equals("") )   priceEngGBP = "0.0";

            String discountEngGBP = dataArray[11];
            if( discountEngGBP.equals("") )   discountEngGBP = "0";

            String priceGerEUR = dataArray[12];
            if( priceGerEUR.equals("") )   priceGerEUR = "0.0";

            String discountGerEUR = dataArray[13];
            if( discountGerEUR.equals("") )   discountGerEUR = "0";

            String priceItaEUR = dataArray[14];
            if( priceItaEUR.equals("") )   priceItaEUR = "0.0";
            
            String discountItaEUR = dataArray[15];
            if( discountItaEUR.equals("") )   discountItaEUR = "0";
            
            //  Insert new values (treatments) into Treatments table
            psInsert.setInt(1, Integer.parseInt(dataArray[0] ) );
            psInsert.setString(2, dataArray[1] );
            psInsert.setString(3, dataArray[2] );
            psInsert.setString(4, dataArray[3] );
            psInsert.setString(5, dataArray[4] );
            psInsert.setString(6, dataArray[5] );
            psInsert.setFloat(7, Float.parseFloat( priceCroHRK ) );
            psInsert.setInt(8, Integer.parseInt( discountCroHRK ) );
            psInsert.setFloat(9, Float.parseFloat( priceEngEUR ) );
            psInsert.setInt(10, Integer.parseInt( discountEngEUR ) );
            psInsert.setFloat(11, Float.parseFloat( priceEngGBP ) );
            psInsert.setInt(12, Integer.parseInt( discountEngGBP ) );
            psInsert.setFloat(13, Float.parseFloat( priceGerEUR ) );
            psInsert.setInt(14, Integer.parseInt( discountGerEUR ) );
            psInsert.setFloat(15, Float.parseFloat( priceItaEUR ) );
            psInsert.setInt(16, Integer.parseInt( discountItaEUR ) );
            
            psInsert.executeUpdate();
        }
        catch(SQLException e){
            System.err.println("SQLException in class SettingsController, method update()");
            e.printStackTrace(System.out);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {}
}

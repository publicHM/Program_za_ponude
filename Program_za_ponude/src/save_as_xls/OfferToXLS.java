/**
 * Class writes passed information to new excel .xls workbook using Apache POI library
 * 
 * @author Petar
 */
package save_as_xls;

import general.SetNewStage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;
import javafx.event.Event;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.PrintSetup;
import save_as_xls.helper.CompanyInfo;
import save_as_xls.helper.CustomerInfo;
import save_as_xls.helper.Duration;
import save_as_xls.helper.Footer;
import save_as_xls.helper.ImagesToSheet;
import save_as_xls.helper.Treatments;


public class OfferToXLS extends SetNewStage{
    
    private final Connection conn;
    private final int customerID;
    private final int offerID;
    private final String date;
    private final Event event;
    private CustomerInfo customerInfo;
    private TranslateText translateText;
    private int rowCount;
    private final HSSFWorkbook workbook;
    private final HSSFSheet sheet;
    
    public OfferToXLS(Connection conn, int customerID, int offerID, String date, Event event){
        
        this.conn = conn;
        this.customerID = customerID;
        this.offerID = offerID;
        this.date = date;
        this.event = event;
        rowCount = 13;
        workbook = new HSSFWorkbook();
        sheet = workbook.createSheet("DentalCentar");
    }
    
    //  Calls all methods needed for retrieving data and saving it to *.xls file
    public void saveDataToXLS(){
        setCompanyInfo();
        setCustomerInfo();
        setTreatments();
        setDuration();
        autoSizeColumns();
        setFooter();
        imagesToSheet();
        setPageBreak();
        showSaveDialog();
    }
    
    //  Calls helper class to retrieve data about company from database and write it to sheet
    private void setCompanyInfo(){
        CompanyInfo wci = new CompanyInfo(conn, workbook, sheet);
        wci.setCompanyInfo();
    }
    
    //  Calls helper class to retrieve data about customer from database and write it to sheet
    private void setCustomerInfo(){
        customerInfo = new CustomerInfo(conn, workbook, sheet, date, customerID, offerID);
        customerInfo.setCustomerInfo();
        translateText = customerInfo.getTranslateText();
    }
    
    //  Calls helper class to retrieve data about treatment from database and write it to sheet
    private void setTreatments(){
        Treatments treatments = new Treatments(conn, workbook, sheet, offerID,
                date, rowCount, translateText, customerInfo);
        treatments.setTreatments();
        rowCount = treatments.getRowCount();
    }
    
    //  Calls helper class to retrieve data about treatment duration from database and write it to sheet
    private void setDuration(){
        Duration duration = new Duration(conn, workbook, sheet, rowCount, translateText, customerInfo, offerID, date);
        duration.setDuration();
        rowCount = duration.getRowCount();
    }
    
    //  Auto sizes sheet's columns
    private void autoSizeColumns(){
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);
        sheet.autoSizeColumn(5);
        sheet.autoSizeColumn(6);
    }
    
    //  Calls helper class to create footer rows and message
    private void setFooter(){
        Footer footer = new Footer(workbook, sheet, translateText, customerInfo.getLanguage(), rowCount);
        footer.setFooter();
        rowCount = footer.getRowCount();
    }
    
    //  Calls helper class to insert images to sheet
    private void imagesToSheet(){
        ImagesToSheet its = new ImagesToSheet(workbook, sheet, rowCount, translateText.getJawImagePath() );
        its.imagesToWorkbook();
        rowCount = its.getRowCount();
    }
    
    //  Sets page break to sheet
    private void setPageBreak(){
        sheet.setAutobreaks(true);
        
        PrintSetup ps = sheet.getPrintSetup();
        ps.setFitHeight( (short) 2 );
        ps.setFitWidth( (short) 1);
        sheet.setFitToPage(true);
    }
    
    //  Shows new stage for choosing directrory where file is going to be saved
    private void showSaveDialog(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Odaberite lokaciju za spremanje");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XLS", "*.xls"));
        fileChooser.setInitialFileName(date.substring(0,4) + "-" + offerID + " - " + customerInfo.getName() + " - "
                + translateText.getOfferTranslated() + " Dental Centar.xls");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("XLS", "*.xls") );
        File file = fileChooser.showSaveDialog(new Stage());
        
        if(file != null){
            try(FileOutputStream fileOut = new FileOutputStream(file);){
                workbook.write(fileOut);
            } catch(FileNotFoundException e){
                setOKBox("Ne može se spremiti kao dokument koji je trenutno u upotrebi."
                        + "\nZatvorite dokument i pokušajte ponovno.", event);
                e.printStackTrace(System.out);
            } catch(IOException e){
                e.printStackTrace(System.out);
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {}
    
}

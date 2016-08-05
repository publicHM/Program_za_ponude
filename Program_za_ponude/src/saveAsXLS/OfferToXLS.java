/**
 * Class writes passed information to new excel .xls workbook
 * 
 * @author Petar
 */
package saveAsXLS;

import general.SetNewStage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.Event;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import newoffer.Duration;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFPicture;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;


public class OfferToXLS extends SetNewStage{
    
    private final Connection conn;
    private final int customerID;
    private final int offerID;
    private final String date;
    private final Event event;
    private int rowCount;
    private String currency;
    private float totalAmount;
    private int cashDiscount;
    private String language;
    private TranslateText translateText;
    private String name;
    private final List<OutputTreatment> upperJawList;
    private final List<OutputTreatment> lowerJawList;
    private final List<OutputTreatment> otherList;
    private final List<Duration> durationList;
    private final HSSFWorkbook workbook;
    private final HSSFSheet sheet;
    
    public OfferToXLS(Connection conn, int customerID, int offerID, String date, Event event){
        
        this.conn = conn;
        this.customerID = customerID;
        this.offerID = offerID;
        this.date = date;
        this.event = event;
        
        rowCount = 13;
        totalAmount = 0f;
        cashDiscount = 0;
        upperJawList = new ArrayList();
        lowerJawList = new ArrayList();
        otherList = new ArrayList();
        durationList = new ArrayList();
        workbook = new HSSFWorkbook();
        sheet = workbook.createSheet("DentalCentar");
    }
    
    public void saveDataToXLS(){
        setCompanyInfo();
        setCustomerInfo();
        setTreatments();
        setDurationList();
        setFooterAndAutoSizeColumns();
        imagesToWorkbook();
        setPageBreak();
        showSaveDialog();
    }
        
    private void setCompanyInfo(){
        //  Get company information from database
        String address, telephone, email, webPage;
        try(Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT Address, Telephone, Email, WebPage FROM CompanyInfo");){
                if( rs.next() ){
                    address = rs.getString(1);
                    telephone = rs.getString(2);
                    email = rs.getString(3);
                    webPage = rs.getString(4);
                    companyInfoToWorkbook(address, telephone, email, webPage);
                }
        }
        catch ( SQLException e) {
            e.printStackTrace(System.out);
        }
    }
    
    private void setCustomerInfo(){
        //  Get cutomer information from database
        try(Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT Name, Language, FinalCurrency, CashDiscount FROM Customers "
                    + "WHERE CustomerID = " + customerID);){
                if( rs.next() ){
                    name = rs.getString(1);
                    language = rs.getString(2);
                    currency = " (" + rs.getString(3) + ")";
                    cashDiscount = rs.getInt(4);
                    translateText = new TranslateText(language);
                    customerInfoToWorkbook(language);
                }
        }
        catch ( SQLException e) {
            e.printStackTrace(System.out);
        }
    }
    
    private void setTreatments(){
        //  Get cutomer information from database
        String tableName, treatment;
        int quantity, discount, visit;
        float price, total;
        String getTreatmentsQuery = "SELECT TableName, Treatment, Quantity, Price, Discount, Visit "
                    + "FROM Offers WHERE OfferID = ? AND Date = ?";
        try(PreparedStatement ps = conn.prepareStatement(getTreatmentsQuery);){
            ps.setInt(1, offerID);
            ps.setString(2, date);
            try(ResultSet rs = ps.executeQuery();){
                int upperJawNo = 1;
                int lowerJawNo = 1;
                int otherNo = 1;
                while( rs.next() ){
                    tableName = rs.getString(1);
                    treatment = rs.getString(2);
                    quantity = rs.getInt(3);
                    price = rs.getFloat(4);
                    discount = rs.getInt(5);
                    total = quantity * price * (100 - discount) / 100;
                    visit = rs.getInt(6);
                    totalAmount += total;
                    if( tableName.equals("UpperJaw") ){
                        upperJawList.add( new OutputTreatment (upperJawNo, treatment, quantity, price, discount,
                                total, visit) );
                        upperJawNo++;
                    }else if(tableName.equals("LowerJaw") ){
                        lowerJawList.add( new OutputTreatment (lowerJawNo, treatment, quantity, price, discount,
                                total, visit) );
                        lowerJawNo++;
                    } else if(tableName.equals("Other") ){
                        otherList.add( new OutputTreatment (otherNo, treatment, quantity, price, discount,
                                total, visit) );
                        otherNo++;
                    }
                }
            }
        treatmentToWorkbook();
        } catch ( SQLException e) {
            e.printStackTrace(System.out);
        }
    }
    
    private void setDurationList(){
        String getDurationQuery = "SELECT Visit, Duration, Cards, Cash FROM Duration WHERE OfferID = ? AND "
                + "Date = ?";
        try(PreparedStatement psGetDuration = conn.prepareStatement(getDurationQuery);){
            psGetDuration.setInt(1, offerID);
             psGetDuration.setString(2, date);
            try(ResultSet rsGetDuration = psGetDuration.executeQuery();){
                while ( rsGetDuration.next() ){
                    Duration duration = new Duration (rsGetDuration.getInt(1), rsGetDuration.getFloat(3),
                            rsGetDuration.getFloat(4) );
                    duration.setDuration( rsGetDuration.getString(2) );
                    durationList.add( duration );
                }
            }
        if( !language.equals("Hrvatski") )    durationToWorkbook();
        } catch ( SQLException e) {
            e.printStackTrace(System.out);
        }
    }
    
    //  Set passed company information to sheet
    private void companyInfoToWorkbook(String address, String telephone, String email, String webPage){
        setCompanyInfoRows(2, address, 12);
        setCompanyInfoRows(3, telephone, 12);
        setCompanyInfoRows(4, email, 12);
        setCompanyInfoRows(5, webPage, 12);
        setCompanyInfoRows(6, null, 6);
    }
    
    //  Create a new row for company information, set text to second cell and set row's height
    private void setCompanyInfoRows(int rowNo, String value, int rowHeight){
        CellStyle style = getFontStyle(9, false, "", false);
        
        Row companyRow = sheet.createRow(rowNo);
        Cell cell = companyRow.createCell(1);
        cell.setCellValue(value);
        cell.setCellStyle(style);
        companyRow.setHeightInPoints(rowHeight);
    }
    
    //  Set customer information to sheet
    private void customerInfoToWorkbook(String language){
        setCustomerInfoRow(7, name);
        
        String offerText = translateText.getOfferTranslated() + " " + offerID + "-" + date.substring(2,4);
        setCustomerInfoRow(8, offerText);
        
        String city = "Zagreb";
        if( language.equals("Talijanski") ) city = "Zagabria";
        String dateText = city + ", " + date.substring(8) + "/" + date.substring(5,7) + "/" + date.substring(0,4);
        setCustomerInfoRow(9, dateText);
        
        setCustomerInfoRow(10, translateText.getFirstOptionTranslated());
        
        Row emptyRow = sheet.createRow(11);
        emptyRow.setHeightInPoints(6);
    }
    
    //  Create a new row for customer information, set text to fourth cell, set row's height and style
    private void setCustomerInfoRow(int rowNo, String value){
        CellStyle style = getFontStyle(14, true, "Blue", false);
        
        Row customerRow = sheet.createRow(rowNo);
        sheet.addMergedRegion( new CellRangeAddress(rowNo, rowNo, 3, 6) );
        Cell cell;
        if (rowNo == 10){
            cell = customerRow.createCell(1);
        }
        else{
            cell = customerRow.createCell(3);
        }
        cell.setCellValue(value);
        cell.setCellStyle(style);
        customerRow.setHeightInPoints(15);
    }
    
    //  Set treatments to sheet
    private void treatmentToWorkbook(){
        tablesHeaderRow();
        setTreatmentRows();
    }
    
    //  Create header row (with appanding cells) for tabels
    private void tablesHeaderRow(){
        CellStyle style = getFontStyle(11, true, "Blue", false);
        style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setBorderTop(CellStyle.BORDER_THIN);
        
        Row headerRow = sheet.createRow(12);
        headerRow.setHeightInPoints(15);
        
        createCell(headerRow, style, 0, null);
        createCell(headerRow, style, 1, translateText.getTretmentTranslated() );
        createCell(headerRow, style, 2, translateText.getQuantityTranslated() );
        createCell(headerRow, style, 3, translateText.getPriceTranslated() + currency);
        createCell(headerRow, style, 4, translateText.getDiscountTranslated() );
        createCell(headerRow, style, 5, translateText.getTotalTranslated() + currency);
        createCell(headerRow, style, 6, null);
    }
    
    //  Create tabels with treatments for each table
    private void setTreatmentRows(){
        if( !upperJawList.isEmpty() ){
            createEmptyRowAndRowWithTableName(translateText.getUpperJawTranslated() );
            Iterator<OutputTreatment> it = upperJawList.iterator();
            while ( it.hasNext() ) {
                OutputTreatment treatment = it.next();
                newTreatmentRowWithCells(treatment);
            }
        }
        if( !lowerJawList.isEmpty() ){
            createEmptyRowAndRowWithTableName(translateText.getLowerJawTranslated() );
            Iterator<OutputTreatment> it = lowerJawList.iterator();
            while( it.hasNext() ){
                OutputTreatment treatment = it.next();
                newTreatmentRowWithCells(treatment);
            }
        }
        if( !otherList.isEmpty() ){
            createEmptyRowAndRowWithTableName(translateText.getOtherTranslated() );
            Iterator<OutputTreatment> it = otherList.iterator();
            while( it.hasNext() ){
                OutputTreatment treatment = it.next();
                newTreatmentRowWithCells(treatment);
            }
        }
        
        setEmptySpacesAndTotalAfterTreatments();
    }
    
    //  Insert empty row for space and then new row with table name
    private void createEmptyRowAndRowWithTableName(String tableName){
        Row emptyRow = sheet.createRow(rowCount);
        emptyRow.setHeightInPoints(16);
        rowCount++;
        Row tableNameRow = sheet.createRow(rowCount);
        tableNameRow.setHeightInPoints(16);
        Cell cell = tableNameRow.createCell(1);
        cell.setCellValue(tableName);
        CellStyle style = getFontStyle(12, true, "", false);
        cell.setCellStyle(style);
        rowCount++;
    }
    
    //  Create individual row for treatment with data about it
    private void newTreatmentRowWithCells(OutputTreatment treatment){
        CellStyle styleGeneral = getFontStyle(11, false, "", true);
        CellStyle styleTreatment = getFontStyle(11, false, "", false);
        CellStyle styleDecimal = getFontStyle(11, false, "", true);
        styleDecimal.setDataFormat(workbook.createDataFormat().getFormat("0.00"));
        CellStyle styleDiscount = getFontStyle(11, false, "Red", true);
        Row treatmentRow = sheet.createRow(rowCount);
        //  Discount is zero, save empty value so zero wouldn't be showen on printed offer
        String discount = "";
        if( treatment.getDiscount() != 0 )    discount = String.valueOf( treatment.getDiscount() ) + "%";
        
        createCell(treatmentRow, styleGeneral, 0, treatment.getNo() );
        createCell(treatmentRow, styleTreatment, 1, treatment.getTreatment() );
        createCell(treatmentRow, styleGeneral, 2, treatment.getQuantity() );
        createCell(treatmentRow, styleDecimal, 3, treatment.getPrice() );
        createCell(treatmentRow, styleDiscount, 4, discount );
        createCell(treatmentRow, styleDecimal, 5, treatment.getTotal() );
        createCell(treatmentRow, styleGeneral, 6, treatment.getVisit() );
        rowCount++;
    }
    
    // Set empty row after treatments are writen in sheet and write in total amount before and after discount
    private void setEmptySpacesAndTotalAfterTreatments(){
        Row emptyRow = sheet.createRow(rowCount);
        rowCount++;
        emptyRow.setHeightInPoints(6);
        
        CellStyle styleText = getFontStyle(11, true, "", false);
        styleText.setBorderTop(CellStyle.BORDER_THIN);
        CellStyle styleAmount = getFontStyle(11, false, "", true);
        styleAmount.setBorderTop(CellStyle.BORDER_THIN);
        styleAmount.setBorderBottom(CellStyle.BORDER_THIN);
        styleAmount.setDataFormat(workbook.createDataFormat().getFormat("0.00"));
        Row totalRow = sheet.createRow(rowCount);
        sheet.addMergedRegion( new CellRangeAddress(rowCount, rowCount, 2, 3) );
        rowCount++;
        totalRow.setHeightInPoints(21);
        createCell(totalRow, styleText, 0, "");
        createCell(totalRow, styleText, 1, "");
        createCell(totalRow, styleText, 2, translateText.getTotalTranslated() + currency + ":" );
        createCell(totalRow, styleText, 3, "");
        createCell(totalRow, styleAmount, 4, "");
        createCell(totalRow, styleAmount, 5, totalAmount );
        createCell(totalRow, styleAmount, 6, "");
        
        if(cashDiscount != 0){
            CellStyle styleTextAfterDiscount = getFontStyle(11, true, "Red", false);
            CellStyle styleAmountAfterDiscount = getFontStyle(11, true, "Red", true);
            styleAmountAfterDiscount.setDataFormat(workbook.createDataFormat().getFormat("0.00"));
            Row totalRowAfterDiscount = sheet.createRow(rowCount);
            sheet.addMergedRegion( new CellRangeAddress(rowCount, rowCount, 2, 3) );
            rowCount++;
            totalRowAfterDiscount.setHeightInPoints(21);
            createCell(totalRowAfterDiscount, styleTextAfterDiscount, 0, "");
            createCell(totalRowAfterDiscount, styleTextAfterDiscount, 1, "");
            createCell(totalRowAfterDiscount, styleTextAfterDiscount, 2, 
                    translateText.getDiscountTranslated() + " " + cashDiscount + "% ("
                            + translateText.getCashTranslated().toLowerCase() + "):" );
            createCell(totalRowAfterDiscount, styleTextAfterDiscount, 3, "");
            createCell(totalRowAfterDiscount, styleTextAfterDiscount, 4, "");
            createCell(totalRowAfterDiscount, styleAmountAfterDiscount, 5, totalAmount * (100 - cashDiscount)/100 );
            createCell(totalRowAfterDiscount, styleTextAfterDiscount, 6, "");
        }
    }
    
    //  Create duration table on sheet
    private void durationToWorkbook(){
        //setTreatmentRows();
        durationHeader();
        setDurationRows();
    }
    
    //  Create duration table header
    private void durationHeader(){
        Row emptyRow1 = sheet.createRow(rowCount);
        emptyRow1.setHeightInPoints(25);
        rowCount++;
        
        CellStyle styleLeft = getFontStyle(11, true, "Blue", false);
        styleLeft.setBorderBottom(CellStyle.BORDER_THIN);
        styleLeft.setBorderTop(CellStyle.BORDER_THIN);
        CellStyle styleCentered = getFontStyle(11, true, "Blue", true);
        styleCentered.setBorderBottom(CellStyle.BORDER_THIN);
        styleCentered.setBorderTop(CellStyle.BORDER_THIN);
        
        Row headerRow = sheet.createRow(rowCount);
        sheet.addMergedRegion( new CellRangeAddress(rowCount, rowCount, 2, 4) );
        headerRow.setHeightInPoints(16);
        rowCount++;
        
        createCell(headerRow, styleCentered, 0, null);
        createCell(headerRow, styleLeft, 1, translateText.getDurationTranslated() );
        createCell(headerRow, styleCentered, 2, translateText.getCardsTranslated() + currency);
        createCell(headerRow, styleCentered, 3, null);
        createCell(headerRow, styleCentered, 4, null);
        createCell(headerRow, styleCentered, 5, translateText.getCashTranslated() + currency);
        createCell(headerRow, styleCentered, 6, null);
        
        Row emptyRow2 = sheet.createRow(rowCount);
        emptyRow2.setHeightInPoints(6);
        rowCount++;
    }
    
    //  Create rows in duration table with information about each visit
    private void setDurationRows(){
        Iterator<Duration> it = durationList.iterator();
        while( it.hasNext() ){
            Duration duration = it.next();
            Row row = sheet.createRow(rowCount);
            sheet.addMergedRegion( new CellRangeAddress(rowCount, rowCount, 2, 4) );
            row.setHeightInPoints(16);
            rowCount++;
            
            CellStyle syleLeft = getFontStyle(11, false, "", false);
            CellStyle syleCenter = getFontStyle(11, false, "", true);
            CellStyle syleDecimal = getFontStyle(11, false, "", true);
            syleDecimal.setDataFormat(workbook.createDataFormat().getFormat("0.00"));
            createCell(row, syleCenter, 0, duration.getVisit() );
            createCell(row, syleLeft, 1, duration.getDuration() );
            createCell(row, syleDecimal, 2, duration.getPriceCards() );
            createCell(row, syleDecimal, 5, duration.getPriceCash() );
        }
        
        CellStyle styleBottom = getFontStyle(4, false, "", false);
        styleBottom.setBorderBottom(CellStyle.BORDER_THIN);
        Row emptyRow2 = sheet.createRow(rowCount);
        emptyRow2.setHeightInPoints(6);
        rowCount++;
        for(int k=0; k<7; k++){
            createCell(emptyRow2, styleBottom, k, null);
        }
    }

    //  Set and return style
    private CellStyle getFontStyle(int fontSize, boolean bold, String color, boolean center ){
        CellStyle style = workbook.createCellStyle();
        HSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short)fontSize);
        font.setFontName("Calibri");
        if( color.equals("Blue") )    font.setColor( IndexedColors.BLUE.getIndex() );
        else if( color.equals("Red") )    font.setColor( IndexedColors.RED.getIndex() );
        font.setBold(bold);
        style.setFont(font);
        if(center)    style.setAlignment(CellStyle.ALIGN_CENTER);
        return style;
    }
        
    // Create a cell with style
    private void createCell(Row row, CellStyle style, int cellNo, Object value){
        Cell cell = row.createCell(cellNo);
        
        if(value instanceof String)     cell.setCellValue( (String) value);
        else if(value instanceof Integer)     cell.setCellValue( (int) value);
        else if(value instanceof Float)     cell.setCellValue( (float) value);
        
        cell.setCellStyle(style);
    }
    
    //  Auto size columns and create footer message
    private void setFooterAndAutoSizeColumns(){
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);
        sheet.autoSizeColumn(5);
        sheet.autoSizeColumn(6);
        
        Row emptyRow = sheet.createRow(rowCount);
        emptyRow.setHeightInPoints(34);
        rowCount++;
        
        CellStyle syle = getFontStyle(11, true, "", false);
        
        Row validRow = sheet.createRow(rowCount);
        emptyRow.setHeightInPoints(16);
        rowCount++;
        createCell(validRow, syle, 0, translateText.getValidTranslated() );
        
        Row mayChangeRow = sheet.createRow(rowCount);
        mayChangeRow.setHeightInPoints(16);
        String mayChangeText;
        if (language.equals("Hrvatski"))    mayChangeText = translateText.getMayChangeTranslated()
                + translateText.getImmidiatePaymentTranslated();
        else    mayChangeText = translateText.getMayChangeTranslated();
        if( mayChangeText.length() <= 80 )
            createCell(mayChangeRow, syle, 0, mayChangeText);
        else{
            int index = mayChangeText.indexOf(' ', 81);
            createCell( mayChangeRow, syle, 0, mayChangeText.substring(0, index) );
            
            rowCount++;
            Row mayChangeRow2 = sheet.createRow(rowCount);
            mayChangeRow2.setHeightInPoints(16);
            
            createCell( mayChangeRow2, syle, 0, mayChangeText.substring(index + 1) );
        }
    }
    
    //  Insert images to to workbook
    private void imagesToWorkbook(){
        //  Header images on page one
        Row imageRow1 = sheet.createRow(0); 
        imageRow1.setHeightInPoints(20);
        Row imageRow2 = sheet.createRow(1); 
        imageRow2.setHeightInPoints(89);
        rowCount++;
        
        //  Page two jaw image
        Row emptyRowPage2 = sheet.createRow(rowCount); 
        emptyRowPage2.setHeightInPoints(70);
        rowCount++;
        Row jawRowPage2 = sheet.createRow(rowCount);
        jawRowPage2.setHeightInPoints(400);
        sheet.addMergedRegion( new CellRangeAddress(rowCount, rowCount, 1, 5) );
        
        
        String path = System.getProperty("user.dir");
        
        try(InputStream isLogo = new FileInputStream(path + "\\src\\images\\logo.png");
            InputStream isOther = new FileInputStream(path + "\\src\\images\\tooth.jpeg");
            InputStream isJaw = new FileInputStream(path + translateText.getJawImagePath());){
            byte[] bytesLogo = IOUtils.toByteArray(isLogo);
            byte[] bytesOther = IOUtils.toByteArray(isOther);
            byte[] bytesJaw = IOUtils.toByteArray(isJaw);
            int pictureIdxLogo = workbook.addPicture(bytesLogo, Workbook.PICTURE_TYPE_PNG);
            int pictureIdxOther = workbook.addPicture(bytesOther, Workbook.PICTURE_TYPE_JPEG);
            int pictureIdxJaw = workbook.addPicture(bytesJaw, Workbook.PICTURE_TYPE_JPEG);
            
            HSSFPatriarch drawing = sheet.createDrawingPatriarch();
            
            ClientAnchor anchorLogo = new HSSFClientAnchor();
            anchorLogo.setAnchorType(ClientAnchor.AnchorType.MOVE_DONT_RESIZE);
            anchorLogo.setCol1(1);
            anchorLogo.setRow1(1);
            
            ClientAnchor anchorOther = new HSSFClientAnchor();
            anchorOther.setAnchorType(ClientAnchor.AnchorType.MOVE_DONT_RESIZE);
            anchorOther.setCol1(3);
            anchorOther.setRow1(0);
            
            ClientAnchor anchorJaw = new HSSFClientAnchor();
            anchorJaw.setAnchorType(ClientAnchor.AnchorType.MOVE_DONT_RESIZE);
            anchorJaw.setCol1(1);
            anchorJaw.setRow1(rowCount);
            
            HSSFPicture  pictLogo = drawing.createPicture(anchorLogo, pictureIdxLogo);
            HSSFPicture  pictOther = drawing.createPicture(anchorOther, pictureIdxOther);
            HSSFPicture  pictJaw = drawing.createPicture(anchorJaw, pictureIdxJaw);
            pictLogo.resize();
            pictOther.resize();
            pictJaw.resize();
            
        } catch(FileNotFoundException e){
            e.printStackTrace(System.out);
        } catch(IOException e){
            e.printStackTrace(System.out);
        }
    }
    
    private void setPageBreak(){
        sheet.setAutobreaks(true);
        
        PrintSetup ps = sheet.getPrintSetup();
        ps.setFitHeight( (short) 2 );
        ps.setFitWidth( (short) 1);
        sheet.setFitToPage(true);
    }
    
    //  Show new stage to choose directrory where file is going to be saved
    private void showSaveDialog(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Odaberite lokaciju za spremanje");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XLS", "*.xls"));
        fileChooser.setInitialFileName(date.substring(0,4) + "-" + offerID + " - " + name + " - "
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

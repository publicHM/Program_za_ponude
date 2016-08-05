/**
 * Helper class that retrieves data about company from database and write it to sheet
 * 
 * @author Petar Deveric
 */
package save_as_xls.helper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import save_as_xls.TranslateText;


public class CustomerInfo extends CreateCellAndFontStyle{
    
    private final Connection conn;
    private final HSSFSheet sheet;
    private final String date;
    private final int customerID;
    private final int offerID;
    private String name;
    private String currency;
    private int cashDiscount;
    private String language;
    private TranslateText translateText;
    
    public CustomerInfo(Connection conn, HSSFWorkbook workbook, HSSFSheet sheet, String date, int customerID,
            int offerID){
        super(workbook);
        this.conn = conn;
        this.sheet = sheet;
        this.date = date;
        this.customerID = customerID;
        this.offerID = offerID;
        this.cashDiscount = 0;
    }
    
    //  Get cutomer information from database
    public void setCustomerInfo(){
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
    
    public String getName(){
        return name;
    }
    
    public String getCurrency(){
        return currency;
    }
    
    public int getCashDiscount(){
        return cashDiscount;
    }
    
    public String getLanguage(){
        return language;
    }
    
    public TranslateText getTranslateText(){
        return translateText;
    }
}

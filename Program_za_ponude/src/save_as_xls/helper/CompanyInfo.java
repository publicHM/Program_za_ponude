/**
 * Helper class that retrieves data about company from database and writes it to sheet
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


public class CompanyInfo extends CreateCellAndFontStyle{
    
    private final Connection conn;
    private final HSSFSheet sheet;
    
    public CompanyInfo(Connection conn, HSSFWorkbook workbook, HSSFSheet sheet){
        super(workbook);
        this.conn = conn;
        this.sheet = sheet;
    }
    
     //  Retrieves data about company from database 
    public void setCompanyInfo(){
        //  Get company information from database
        String address, telephone, email, webPage;
        try(Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT Address, Telephone, Email, WebPage FROM CompanyInfo");){
                if( rs.next() ){
                    address = rs.getString(1);
                    telephone = rs.getString(2);
                    email = rs.getString(3);
                    webPage = rs.getString(4);
                    companyInfoToSheet(address, telephone, email, webPage);
                }
        }
        catch ( SQLException e) {
            e.printStackTrace(System.out);
        }
    }
    
    //  Sets passed company information to sheet
    private void companyInfoToSheet(String address, String telephone, String email, String webPage){
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
}

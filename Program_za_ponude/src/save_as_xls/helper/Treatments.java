/**
 * Helper class that retrieves data about treatments from database and writes it to sheet
 * 
 * @author Petar Deveric
 */
package save_as_xls.helper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import save_as_xls.OutputTreatment;
import save_as_xls.TranslateText;


public class Treatments extends CreateCellAndFontStyle{
    
    private final Connection conn;
    private final HSSFSheet sheet;
    private final int offerID;
    private final String date;
    private float totalAmount;
    private int rowCount;
    private final TranslateText translateText;
    private final CustomerInfo customerInfo;
    private final List<OutputTreatment> upperJawList;
    private final List<OutputTreatment> lowerJawList;
    private final List<OutputTreatment> otherList;
    
    public Treatments(Connection conn, HSSFWorkbook workbook, HSSFSheet sheet, int offerID, String date,
            int rowCount, TranslateText translateText, CustomerInfo customerInfo){
        super(workbook);
        this.conn = conn;
        this.sheet = sheet;
        this.offerID = offerID;
        this.date = date;
        this.totalAmount = 0f;
        this.translateText = translateText;
        this.customerInfo = customerInfo;
        this.rowCount = rowCount;
        upperJawList = new ArrayList();
        lowerJawList = new ArrayList();
        otherList = new ArrayList();
    }
    
    //  Set treatment information to sheet
    public void setTreatments(){
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
        createCell(headerRow, style, 3, translateText.getPriceTranslated() + customerInfo.getCurrency() );
        createCell(headerRow, style, 4, translateText.getDiscountTranslated() );
        createCell(headerRow, style, 5, translateText.getTotalTranslated() + customerInfo.getCurrency() );
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
        styleDecimal.setDataFormat(getWorkbook().createDataFormat().getFormat("0.00"));
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
        styleAmount.setDataFormat(getWorkbook().createDataFormat().getFormat("0.00"));
        Row totalRow = sheet.createRow(rowCount);
        sheet.addMergedRegion( new CellRangeAddress(rowCount, rowCount, 2, 3) );
        rowCount++;
        totalRow.setHeightInPoints(21);
        createCell(totalRow, styleText, 0, "");
        createCell(totalRow, styleText, 1, "");
        createCell(totalRow, styleText, 2, translateText.getTotalTranslated() + customerInfo.getCurrency() + ":" );
        createCell(totalRow, styleText, 3, "");
        createCell(totalRow, styleAmount, 4, "");
        createCell(totalRow, styleAmount, 5, totalAmount );
        createCell(totalRow, styleAmount, 6, "");
        
        if(customerInfo.getCashDiscount() != 0){
            CellStyle styleTextAfterDiscount = getFontStyle(11, true, "Red", false);
            CellStyle styleAmountAfterDiscount = getFontStyle(11, true, "Red", true);
            styleAmountAfterDiscount.setDataFormat(getWorkbook().createDataFormat().getFormat("0.00"));
            Row totalRowAfterDiscount = sheet.createRow(rowCount);
            sheet.addMergedRegion( new CellRangeAddress(rowCount, rowCount, 2, 3) );
            rowCount++;
            totalRowAfterDiscount.setHeightInPoints(21);
            createCell(totalRowAfterDiscount, styleTextAfterDiscount, 0, "");
            createCell(totalRowAfterDiscount, styleTextAfterDiscount, 1, "");
            createCell(totalRowAfterDiscount, styleTextAfterDiscount, 2, 
                    translateText.getDiscountTranslated() + " " + customerInfo.getCashDiscount() + "% ("
                            + translateText.getCashTranslated().toLowerCase() + "):" );
            createCell(totalRowAfterDiscount, styleTextAfterDiscount, 3, "");
            createCell(totalRowAfterDiscount, styleTextAfterDiscount, 4, "");
            createCell(totalRowAfterDiscount, styleAmountAfterDiscount, 5, totalAmount *
                    (100 - customerInfo.getCashDiscount())/100 );
            createCell(totalRowAfterDiscount, styleTextAfterDiscount, 6, "");
        }
    }
    
    public int getRowCount(){
        return rowCount;
    }
}

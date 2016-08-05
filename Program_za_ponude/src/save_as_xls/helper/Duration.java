/**
 * Helper class that retrieves data about treatment duration from database and writes it to sheet
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
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import save_as_xls.TranslateText;


public class Duration extends CreateCellAndFontStyle {
    
    private final Connection conn;
    private final HSSFSheet sheet;
    private int rowCount;
    private final TranslateText translateText;
    private final CustomerInfo customerInfo;
    private final int offerID;
    private final String date;
    private final List<new_offer.Duration> durationList;
    
    public Duration(Connection conn, HSSFWorkbook workbook, HSSFSheet sheet, int rowCount, TranslateText translateText,
            CustomerInfo customerInfo, int offerID, String date){
        super(workbook);
        this.conn = conn;
        this.sheet = sheet;
        this.translateText = translateText;
        this.customerInfo = customerInfo;
        this.rowCount = rowCount;
        this.offerID = offerID;
        this.date = date;
        durationList = new ArrayList();
    }
    
    //  Set treatment duration information to sheet
    public void setDuration(){
        String getDurationQuery = "SELECT Visit, Duration, Cards, Cash FROM Duration WHERE OfferID = ? AND "
                + "Date = ?";
        try(PreparedStatement psGetDuration = conn.prepareStatement(getDurationQuery);){
            psGetDuration.setInt(1, offerID);
             psGetDuration.setString(2, date);
            try(ResultSet rsGetDuration = psGetDuration.executeQuery();){
                while ( rsGetDuration.next() ){
                    new_offer.Duration duration = new new_offer.Duration (rsGetDuration.getInt(1), rsGetDuration.getFloat(3),
                            rsGetDuration.getFloat(4) );
                    duration.setDuration( rsGetDuration.getString(2) );
                    durationList.add( duration );
                }
            }
        if( !customerInfo.getLanguage().equals("Hrvatski") )    durationToWorkbook();
        } catch ( SQLException e) {
            e.printStackTrace(System.out);
        }
    }
    
    //  Create durationList table on a sheet
    private void durationToWorkbook(){
        durationHeader();
        setDurationRows();
    }
    
    //  Create duration's table header
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
        createCell(headerRow, styleCentered, 2, translateText.getCardsTranslated() + customerInfo.getCurrency() );
        createCell(headerRow, styleCentered, 3, null);
        createCell(headerRow, styleCentered, 4, null);
        createCell(headerRow, styleCentered, 5, translateText.getCashTranslated() + customerInfo.getCurrency() );
        createCell(headerRow, styleCentered, 6, null);
        
        Row emptyRow2 = sheet.createRow(rowCount);
        emptyRow2.setHeightInPoints(6);
        rowCount++;
    }
    
    //  Create rows in duration table with information about each visit
    private void setDurationRows(){
        Iterator<new_offer.Duration> it = durationList.iterator();
        while( it.hasNext() ){
            new_offer.Duration duration = it.next();
            Row row = sheet.createRow(rowCount);
            sheet.addMergedRegion( new CellRangeAddress(rowCount, rowCount, 2, 4) );
            row.setHeightInPoints(16);
            rowCount++;
            
            CellStyle syleLeft = getFontStyle(11, false, "", false);
            CellStyle syleCenter = getFontStyle(11, false, "", true);
            CellStyle syleDecimal = getFontStyle(11, false, "", true);
            syleDecimal.setDataFormat(getWorkbook().createDataFormat().getFormat("0.00"));
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
    
    public int getRowCount(){
        return rowCount;
    }
}

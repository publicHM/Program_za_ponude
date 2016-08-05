/**
 * Helper class that creates footer rows and message and writes it to sheet
 * 
 * @author Petar Deveric
 */
package save_as_xls.helper;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import save_as_xls.TranslateText;


public class Footer extends CreateCellAndFontStyle {
    
    private final HSSFSheet sheet;
    private final TranslateText translateText;
    private final String language;
    private int rowCount;
    
    public Footer(HSSFWorkbook workbook, HSSFSheet sheet, TranslateText translateText, 
            String language, int rowCount){
        super(workbook);
        this.translateText = translateText;
        this.sheet = sheet;
        this.language = language;
        this.rowCount = rowCount;
    }
    
    //  Create footer rows and message
    public void setFooter(){
        
        //  Set cell style
        CellStyle syle = getFontStyle(11, true, "", false);
        
        //  Create first footer row
        Row emptyRow = sheet.createRow(rowCount);
        emptyRow.setHeightInPoints(34);
        rowCount++;
        
        //  Set first footer row text in appropriate cell
        Row validRow = sheet.createRow(rowCount);
        emptyRow.setHeightInPoints(16);
        rowCount++;
        createCell(validRow, syle, 0, translateText.getValidTranslated() );
        
        //  Create second footer row
        Row mayChangeRow = sheet.createRow(rowCount);
        mayChangeRow.setHeightInPoints(16);
        
        //  Set second footer row text in appropriate cell
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
    
    public int getRowCount(){
        return rowCount;
    }
}

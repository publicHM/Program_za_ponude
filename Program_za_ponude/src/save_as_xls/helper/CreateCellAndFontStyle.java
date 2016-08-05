/**
 * Abstract class that provides methods for creating cells and cell (font) style
 * 
 * @author Petar Deveric
 */
package save_as_xls.helper;

import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;


public abstract class CreateCellAndFontStyle {
    
    private final HSSFWorkbook workbook;
    
    public CreateCellAndFontStyle(HSSFWorkbook workbook){
        this.workbook = workbook;
    }
    
    // Create a cell with style
    public void createCell(Row row, CellStyle style, int cellNo, Object value){
        Cell cell = row.createCell(cellNo);
        if(value instanceof String)     cell.setCellValue( (String)  value);
        else if(value instanceof Integer)     cell.setCellValue( (int) value);
        else if(value instanceof Float)     cell.setCellValue( (float) value);
        
        cell.setCellStyle(style);
    }
    
    //  Set and return cell(font) style
    public CellStyle getFontStyle(int fontSize, boolean bold, String color, boolean center ){
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
    
    public HSSFWorkbook getWorkbook(){
        return workbook;
    }
}

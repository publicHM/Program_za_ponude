/**
 * Helper class that insert images to sheet
 * 
 * @author Petar Deveric
 */
package save_as_xls.helper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFPicture;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;


public class ImagesToSheet {
    
    private final HSSFWorkbook workbook;
    private final HSSFSheet sheet;
    private int rowCount;
    private final String jawImagePath;
    
    public ImagesToSheet(HSSFWorkbook workbook, HSSFSheet sheet, int rowCount, String jawImagePath){
        this.workbook = workbook;
        this.sheet = sheet;
        this.rowCount = rowCount;
        this.jawImagePath = jawImagePath;
    }
    
    //  Insert images to to workbook
    public void imagesToWorkbook(){
        //  Rows for header images on page one
        Row imageRow1 = sheet.createRow(0); 
        imageRow1.setHeightInPoints(20);
        Row imageRow2 = sheet.createRow(1); 
        imageRow2.setHeightInPoints(89);
        rowCount++;
        
        //  Rows for page two jaw image
        Row emptyRowPage2 = sheet.createRow(rowCount); 
        emptyRowPage2.setHeightInPoints(70);
        rowCount++;
        Row jawRowPage2 = sheet.createRow(rowCount);
        jawRowPage2.setHeightInPoints(400);
        sheet.addMergedRegion( new CellRangeAddress(rowCount, rowCount, 1, 5) );
        
        
        String path = System.getProperty("user.dir");
        
        //  Streaming images from files and writing it to sheet
        try(InputStream isLogo = new FileInputStream(path + "\\src\\images\\logo.png");
            InputStream isOther = new FileInputStream(path + "\\documents\\tooth.jpeg");
            InputStream isJaw = new FileInputStream(path + jawImagePath);){
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
    
    public int getRowCount(){
        return rowCount;
    }
}

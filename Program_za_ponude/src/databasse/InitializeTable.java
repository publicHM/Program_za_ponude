/**
 * Initializes table that shows data from OfferBasicInfo object. It is called from DataBaseController and is shown
 * in database window.
 * 
 * @author Petar Deveric
 */
package databasse;

import general.DatabaseLocation;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;


public class InitializeTable {
    
    private final TableView table;
    
    //  Constructor sets table to equal passed table from caller class
    public InitializeTable(TableView table){
        this.table = table;
    }
    
    //  sets columns and appropriate behavior for columns
    public TableView getTable(){
        TableColumn<OfferBasicInfo, Integer> offerIDColumn = new TableColumn("Broj ponude");
        TableColumn<OfferBasicInfo, String> nameColumn = new TableColumn("Ime i prezime");
        TableColumn<OfferBasicInfo, String> dateColumn = new TableColumn("Datum");
        
        offerIDColumn.setStyle("-fx-alignment: CENTER;");
        nameColumn.setStyle("-fx-alignment: CENTER;");
        dateColumn.setStyle("-fx-alignment: CENTER;");
        
        offerIDColumn.setCellValueFactory( new PropertyValueFactory("offerID") );
        nameColumn.setCellValueFactory( new PropertyValueFactory("name") );
        dateColumn.setCellValueFactory( new PropertyValueFactory("date") );

        offerIDColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        dateColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        
        offerIDColumn.setEditable(true);
        nameColumn.setEditable(false);
        dateColumn.setEditable(false);
        
        offerIDColumn.setOnEditCommit((TableColumn.CellEditEvent<OfferBasicInfo, Integer> tc) -> {
                int oldValue = tc.getOldValue();
                int newValue = tc.getNewValue();
                
                String databaseLocation = new DatabaseLocation().getDatabaseLocation();
                
                try(Connection conn = DriverManager.getConnection("jdbc:derby:" + databaseLocation);
                    PreparedStatement psInsert = conn.prepareStatement("UPDATE Offers SET OfferID = ? WHERE OfferID = ? "
                            + "AND NOT EXISTS ( SELECT OfferID FROM Offers WHERE OfferID = ? AND "
                            + "YEAR(Date) = YEAR(CURRENT_DATE) )") ){
                            psInsert.setInt(1, newValue);
                            psInsert.setInt(2, oldValue);
                            psInsert.setInt(3, newValue);
                            psInsert.executeUpdate();
                            table.refresh();
                }
                catch(SQLException e){
                    e.printStackTrace(System.out);
                }
                
            });
        
        table.getColumns().addAll(offerIDColumn, nameColumn, dateColumn);
        
        return table;
    }
}

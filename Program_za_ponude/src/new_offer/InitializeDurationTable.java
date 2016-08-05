/*
 * Initializes table that shows data from Duration object. It is called from NewOfferController and is shown
 * in new offer window.
 * 
 * @author Petar Deveric
 */
package new_offer;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.FloatStringConverter;
import javafx.util.converter.IntegerStringConverter;


public class InitializeDurationTable {
    
    private final TableView table;
    
    public InitializeDurationTable(TableView table){
        this.table = table;
    }
    
    public TableView initialize(){
        TableColumn<Duration, Integer> visitColumn = new TableColumn("Posjet");
        TableColumn<Duration, String> durationColumn = new TableColumn("Trajanje");
        TableColumn<Duration, Float> priceCardColumn = new TableColumn("Cijena za kartice");
        TableColumn<Duration, Float> priceCashColumn = new TableColumn("Cijena za gotovinu");
        
        visitColumn.setMinWidth(60);
        visitColumn.setMaxWidth(60);
        
        visitColumn.setStyle("-fx-alignment: CENTER;");
        priceCardColumn.setStyle("-fx-alignment: CENTER;");
        priceCashColumn.setStyle("-fx-alignment: CENTER;");
        
        visitColumn.setCellValueFactory( new PropertyValueFactory("visit") );
        durationColumn.setCellValueFactory( new PropertyValueFactory("duration") );
        priceCardColumn.setCellValueFactory( new PropertyValueFactory("priceCards") );
        priceCashColumn.setCellValueFactory( new PropertyValueFactory("priceCash") );
        
        visitColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        durationColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        
        priceCardColumn.setCellFactory(TextFieldTableCell.<Duration, Float>forTableColumn(new FloatStringConverter() {
            private final NumberFormat nf = DecimalFormat.getNumberInstance();

            {nf.setMaximumFractionDigits(2);
             nf.setMinimumFractionDigits(2);}

            @Override
            public String toString(final Float value) {
                return nf.format(value);
            }
        }));
        
        priceCashColumn.setCellFactory(TextFieldTableCell.<Duration, Float>forTableColumn(new FloatStringConverter() {
            private final NumberFormat nf = DecimalFormat.getNumberInstance();

            {nf.setMaximumFractionDigits(2);
             nf.setMinimumFractionDigits(2);}

            @Override
            public String toString(final Float value) {
                return nf.format(value);
            }
        }));
        
        visitColumn.setEditable(false);
        
        durationColumn.setOnEditCommit((TableColumn.CellEditEvent<Duration, String> tc) -> {
                tc.getRowValue().setDuration( tc.getNewValue() );
            });
        
        priceCardColumn.setEditable(false);
        priceCashColumn.setEditable(false);
        
        table.getColumns().addAll(visitColumn, durationColumn, priceCardColumn, priceCashColumn);
        
        return table;
    }
}

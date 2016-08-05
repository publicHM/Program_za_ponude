/*
 * Initializes table that shows data from Treatment object. It is called from NewOfferController and is shown
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


public class InitializeTable {
    
    private final TableView table;
    private final NewOfferController controller;
    
    public InitializeTable(TableView table, NewOfferController controller){
        this.table = table;
        this.controller = controller;
    }
    
    public TableView initialize(){
        TableColumn<Treatment,Integer> noColumn = new TableColumn<>("#");
        TableColumn<Treatment,String> treatmentColumn = new TableColumn("Tretman");
        TableColumn<Treatment,Integer> quantityColumn = new TableColumn<>("Količina");
        TableColumn<Treatment,Float> priceColumn = new TableColumn<>("Cijena");
        TableColumn<Treatment,Integer> discountColumn = new TableColumn<>("Popust(%)");
        TableColumn<Treatment,Float> totalColumn = new TableColumn<>("Ukupno");
        TableColumn<Treatment,Integer> visitColumn = new TableColumn<>("Posjet");
        
        noColumn.setMinWidth(30);
        noColumn.setMaxWidth(30);
        treatmentColumn.setMinWidth(300);
        
        noColumn.setStyle("-fx-alignment: CENTER;");
        quantityColumn.setStyle("-fx-alignment: CENTER;");
        priceColumn.setStyle("-fx-alignment: CENTER;");
        discountColumn.setStyle("-fx-alignment: CENTER;");
        totalColumn.setStyle("-fx-alignment: CENTER;");
        visitColumn.setStyle("-fx-alignment: CENTER;");
        
        noColumn.setCellValueFactory(new PropertyValueFactory("no"));
        treatmentColumn.setCellValueFactory(new PropertyValueFactory("treatment"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory("quantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory("price"));
        discountColumn.setCellValueFactory(new PropertyValueFactory("discount"));
        totalColumn.setCellValueFactory(new PropertyValueFactory("total"));
        visitColumn.setCellValueFactory(new PropertyValueFactory("visit"));
        
        noColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        treatmentColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        quantityColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        
        priceColumn.setCellFactory(TextFieldTableCell.<Treatment, Float>forTableColumn(new FloatStringConverter() {
            private final NumberFormat nf = DecimalFormat.getNumberInstance();

            {nf.setMaximumFractionDigits(2);
             nf.setMinimumFractionDigits(2);}

            @Override
            public String toString(final Float value) {
                return nf.format(value);
            }
        }));
        
        discountColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        totalColumn.setCellFactory(TextFieldTableCell.<Treatment, Float>forTableColumn(new FloatStringConverter() {
            private final NumberFormat nf = DecimalFormat.getNumberInstance();

            {nf.setMaximumFractionDigits(2);
             nf.setMinimumFractionDigits(2);}

            @Override
            public String toString(final Float value) {
                return nf.format(value);
            }
        }));
        visitColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        
        noColumn.setEditable(false);
        
        treatmentColumn.setOnEditCommit((TableColumn.CellEditEvent<Treatment, String> tc) -> {
                tc.getRowValue().setTreatment(tc.getNewValue());
            });
            
        quantityColumn.setOnEditCommit((TableColumn.CellEditEvent<Treatment, Integer> tc) -> {
            Treatment treatment = tc.getRowValue();
            treatment.setQuantity(tc.getNewValue());
            treatment.setTotal( treatment.getQuantity() * treatment.getPrice() * ( 100 - treatment.getDiscount() )/100 );
            table.refresh();
            controller.setTotalFields();
            controller.setDurationTable();
        });
        
        priceColumn.setOnEditCommit((TableColumn.CellEditEvent<Treatment, Float> tc) -> {
            Treatment treatment = tc.getRowValue();
            treatment.setPrice( tc.getNewValue() );
            treatment.setTotal( treatment.getQuantity() * treatment.getPrice() * ( 100 - treatment.getDiscount() )/100 );
            table.refresh();
            controller.setTotalFields();
            controller.setDurationTable();
        });
        
        discountColumn.setOnEditCommit((TableColumn.CellEditEvent<Treatment, Integer> tc) -> {
            Treatment treatment = tc.getRowValue();
            treatment.setDiscount(tc.getNewValue());
            treatment.setTotal( treatment.getQuantity() * treatment.getPrice() * ( 100 - treatment.getDiscount() )/100 );
            table.refresh();
            controller.setTotalFields();
            controller.setDurationTable();
        });
        
        totalColumn.setEditable(false);
        
        visitColumn.setOnEditCommit((TableColumn.CellEditEvent<Treatment, Integer> tc) -> {
            tc.getRowValue().setVisit(tc.getNewValue());
            controller.setDurationTable();
        });
        
        table.getColumns().addAll(noColumn, treatmentColumn, quantityColumn, priceColumn, discountColumn, 
                totalColumn, visitColumn);
        
        return table;
    }
}

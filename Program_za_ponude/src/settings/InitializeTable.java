/*
 * Initializes table that shows data from AddData object. It is called from ChangeDataController and is shown
 * in change data window.
 * 
 * @author Petar
 */
package settings;

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
    
    public InitializeTable(TableView table){
        this.table = table;
    }
    
    public TableView initialize(){
        TableColumn<AddData,String> languageOneColumn = new TableColumn("Jezik 1");
        TableColumn<AddData,Float> priceOneColumn = new TableColumn<>("Cijena 1");
        TableColumn<AddData,Integer> discountOneColumn = new TableColumn<>("Popust 1");
        TableColumn<AddData,String> languageTwoColumn = new TableColumn("Jezik 2");
        TableColumn<AddData,Float> priceTwoColumn = new TableColumn<>("Cijena 2");
        TableColumn<AddData,Integer> discountTwoColumn = new TableColumn<>("Popust 2");
        
        priceOneColumn.setStyle("-fx-alignment: CENTER;");
        priceOneColumn.setMaxWidth(1200);
        discountOneColumn.setStyle("-fx-alignment: CENTER;");
        discountOneColumn.setMaxWidth(1200);
        priceTwoColumn.setStyle("-fx-alignment: CENTER;");
        priceTwoColumn.setMaxWidth(1200);
        discountTwoColumn.setStyle("-fx-alignment: CENTER;");
        discountTwoColumn.setMaxWidth(1200);
        
        languageOneColumn.setCellValueFactory(new PropertyValueFactory("languageOne"));
        priceOneColumn.setCellValueFactory(new PropertyValueFactory("priceOne"));
        discountOneColumn.setCellValueFactory(new PropertyValueFactory("discountOne"));
        languageTwoColumn.setCellValueFactory(new PropertyValueFactory("languageTwo"));
        priceTwoColumn.setCellValueFactory(new PropertyValueFactory("priceTwo"));
        discountTwoColumn.setCellValueFactory(new PropertyValueFactory("discountTwo"));
        
        languageOneColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        priceOneColumn.setCellFactory(TextFieldTableCell.<AddData, Float>forTableColumn(new FloatStringConverter() {
            private final NumberFormat nf = DecimalFormat.getNumberInstance();

            {nf.setMaximumFractionDigits(2);
             nf.setMinimumFractionDigits(2);}

            @Override
            public String toString(final Float value) {
                return nf.format(value);
            }
        }));
        discountOneColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        languageTwoColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        priceTwoColumn.setCellFactory(TextFieldTableCell.<AddData, Float>forTableColumn(new FloatStringConverter() {
            private final NumberFormat nf = DecimalFormat.getNumberInstance();

            {nf.setMaximumFractionDigits(2);
             nf.setMinimumFractionDigits(2);}

            @Override
            public String toString(final Float value) {
                return nf.format(value);
            }
        }));
        discountTwoColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        
        languageOneColumn.setOnEditCommit((TableColumn.CellEditEvent<AddData, String> tc) -> {
                tc.getRowValue().setLanguageOne(tc.getNewValue());
            });
            
        priceOneColumn.setOnEditCommit((TableColumn.CellEditEvent<AddData, Float> tc) -> {
            tc.getRowValue().setPriceOne(tc.getNewValue());
        });
        
        discountOneColumn.setOnEditCommit((TableColumn.CellEditEvent<AddData, Integer> tc) -> {
            tc.getRowValue().setDiscountOne(tc.getNewValue());
        });
        
        languageTwoColumn.setOnEditCommit((TableColumn.CellEditEvent<AddData, String> tc) -> {
                tc.getRowValue().setLanguageTwo(tc.getNewValue());
        });
        
        priceTwoColumn.setOnEditCommit((TableColumn.CellEditEvent<AddData, Float> tc) -> {
            tc.getRowValue().setPriceTwo(tc.getNewValue());
        });
        
        discountTwoColumn.setOnEditCommit((TableColumn.CellEditEvent<AddData, Integer> tc) -> {
            tc.getRowValue().setDiscountTwo(tc.getNewValue());
        });
        
        table.getColumns().addAll(languageOneColumn, priceOneColumn, discountOneColumn, languageTwoColumn,
                priceTwoColumn, discountTwoColumn);
        
        return table;
    }
}

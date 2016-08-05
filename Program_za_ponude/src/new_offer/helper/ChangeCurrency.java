/**
 * Helper class that changes prices in tables to prices in another currency
 * 
 * @author Petar
 */

package new_offer.helper;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import new_offer.Duration;
import new_offer.Treatment;


public class ChangeCurrency extends NewOfferAbstract {
    
    private final ObservableList<Treatment> upperJawList;
    private final ObservableList<Treatment> lowerJawList;
    private final ObservableList<Treatment> otherList;
    private final ObservableList<Duration> durationList;
    private final TableView tableUpperJaw;
    private final TableView tableLowerJaw;
    private final TableView tableOther;
    private final TableView tableDuraton;
    
    public ChangeCurrency(ObservableList<Treatment> upperJawList, ObservableList<Treatment> lowerJawList,
            ObservableList<Treatment> otherList, ObservableList<Duration> durationList, TableView tableUpperJaw,
            TableView tableLowerJaw, TableView tableOther, TableView tableDuraton){
        this.upperJawList = upperJawList;
        this.lowerJawList = lowerJawList;
        this.otherList = otherList;
        this.durationList = durationList;
        this.tableUpperJaw = tableUpperJaw;
        this.tableLowerJaw = tableLowerJaw;
        this.tableOther = tableOther;
        this.tableDuraton = tableDuraton;
    }
    
    //  Fore key pressed calls method for changing prices in tables to prices in another currency
    public void exchangeRateFieldEnterPressed(KeyEvent event, String exchangeRateString){
        try{float exchangeRate = Float.parseFloat( exchangeRateString );
            changeCurrencySecondStep(exchangeRate);
        } catch (NumberFormatException e){
            setOKBox("Korisite samo brojeve i točku (.) za decimalne vrijednosti", event);
        }
    }
    
    //  For mouse pressed calls method for changing prices in tables to prices in another currency
    public void changeCurrencyButtonClicked(Stage popupStage, MouseEvent event, String exchangeRateString){
        closePopup(popupStage);
        try{float exchangeRate = Float.parseFloat(exchangeRateString);
            if( exchangeRate !=  0f && exchangeRate != 1f){
                //  If mouse is double clicked, converts exchange rate so prices are returned back
                //  to original values (before currency swap)
                if(event.getClickCount() == 2 )
                    exchangeRate = 1 / (float) Math.pow(exchangeRate, 2);
                //  Calls method for changing price values in tables to prices in anoter currency
                changeCurrencySecondStep(exchangeRate);
            }
        } catch (NumberFormatException e){
            setOKBox("Korisite samo brojeve i točku (.) za decimalne vrijednosti", event);
        }
    }
    
    //  Calls method for changing price values in each table to prices in anoter currency
    private void changeCurrencySecondStep(float exchangeRate){
        if ( !upperJawList.isEmpty() ){
            changeJawTableCurrency(upperJawList, exchangeRate);
            tableUpperJaw.refresh();
        }
        if ( !lowerJawList.isEmpty() ){
            changeJawTableCurrency(lowerJawList, exchangeRate);
            tableLowerJaw.refresh();
        }
        if ( !otherList.isEmpty() ){
            changeJawTableCurrency(otherList, exchangeRate);
            tableOther.refresh();
        }
        if ( !durationList.isEmpty() ){
            changeDurationTableCurrency(durationList, exchangeRate);
            tableDuraton.refresh();
        }
    }
    
    //  Changes price values in passsed tableUpperJaw, tableLowerJaw or tableOther to prices in anoter currency
    private void changeJawTableCurrency(ObservableList<Treatment> observableList, Float exchangeRate){
        observableList.stream().forEach((treatment) -> {
            float newPrice = treatment.getPrice() / exchangeRate;
            float newTotal = treatment.getTotal() / exchangeRate;
            treatment.setPrice(newPrice);
            treatment.setTotal(newTotal);
        });
    }
    
    //  Changes price values in passsed tableDuraton to prices in anoter currency
    private void changeDurationTableCurrency(ObservableList<Duration> observableList, Float exchangeRate){
        observableList.stream().forEach((duration) -> {
            float newPriceCards = duration.getPriceCards() / exchangeRate;
            float newPriceCash = duration.getPriceCash() / exchangeRate;
            duration.setPriceCards(newPriceCards);
            duration.setPriceCash(newPriceCash);
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {}
}

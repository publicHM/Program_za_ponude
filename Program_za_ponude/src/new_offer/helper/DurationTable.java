/**
 * Helper class for setting duration table
 * 
 * @author Petar
 */
package new_offer.helper;

import java.util.HashMap;
import java.util.Map;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import new_offer.Duration;
import new_offer.InitializeDurationTable;
import new_offer.Treatment;


public class DurationTable {
    
    private final ObservableList<Duration> durationList;
    private final ObservableList<Treatment> upperJawList;
    private final ObservableList<Treatment> lowerJawList;
    private final ObservableList<Treatment> otherList;
    private final String cashDiscount;
    private TableView tableDuraton;
    
    public DurationTable(ObservableList<Duration> durationList, ObservableList<Treatment> upperJawList,
            ObservableList<Treatment> lowerJawList, ObservableList<Treatment> otherList, 
            String cashDiscount, TableView tableDuraton){
        this.durationList = durationList;
        this.upperJawList = upperJawList;
        this.lowerJawList = lowerJawList;
        this.otherList = otherList;
        this.cashDiscount = cashDiscount;
        this.tableDuraton = tableDuraton;
    }
    
    // Clears previous suration list, calls method that sums amounts that shoud be paid after each visit
    // and adds ammounts that shoul be paid per each visits to duration list
    public void setDurationTable(){
        // Clears previous suration list
        if( !durationList.isEmpty() )   durationList.clear();
        
        Map<Integer, Float> amountPerVisit = new HashMap();
        // Gets ammounts that shoul be paid per each visits for all lists (tables)
        amountPerVisit = populateHashMapForAmountsPerVisit(upperJawList, amountPerVisit);
        amountPerVisit = populateHashMapForAmountsPerVisit(lowerJawList, amountPerVisit);
        amountPerVisit = populateHashMapForAmountsPerVisit(otherList, amountPerVisit);
        
        //  Iterates over map and adds ammounts that shoul be paid per each visits to duration list
        for(Map.Entry<Integer, Float> entry : amountPerVisit.entrySet()){
            int visit = entry.getKey();
            float amountForCards = entry.getValue();
            int discount;
            if( cashDiscount == null || cashDiscount.equals(""))
                discount = 0;
            else
                discount = Integer.parseInt(cashDiscount);
            float amountForCash = amountForCards * (100 -  discount)/100;
            
            durationList.add( new Duration(visit, amountForCards, amountForCash) );
        }
        
        tableDuraton.getColumns().clear();
        tableDuraton.setItems(durationList);
        InitializeDurationTable idt = new InitializeDurationTable(tableDuraton);
        tableDuraton = idt.initialize();
    }
    
    //  Iterates over passed list, extract data about price and visit at which treatmant is going to be held 
    //  and adds amount that should be payed at this visit to total amount for corresponding visit
    private Map<Integer, Float> populateHashMapForAmountsPerVisit(ObservableList<Treatment> ol,
            Map<Integer, Float> amountPerVisit){
        ol.stream().forEach((treatment) -> {
            int visit = treatment.getVisit();
            float currentAmount = treatment.getTotal();
            //  Get previous total amount that should be paied at that visit
            float previousAmount;
            if(amountPerVisit.get(visit) == null)
                previousAmount = 0f;
            else
                previousAmount = amountPerVisit.get(visit);
            
            //  Add current amount to previous total amount that should be paid at this visit
            float totalAmountPerVisit = previousAmount + currentAmount;
            amountPerVisit.put(visit, totalAmountPerVisit);
        });
        return amountPerVisit;
    }
}

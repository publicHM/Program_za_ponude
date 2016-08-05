/*
 * Class-object for taking, holding and setting values that are showed in Duration table at 'NewOffer.fxml'.
 * Provides information about visit number, duration of visit, price for paying with cards and price for paying in cash.
 *
 * @author Petar Deveric
 */
package newoffer;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Duration {
    
    private final IntegerProperty visit;
    private final StringProperty duration;
    private final FloatProperty priceCards;
    private final FloatProperty priceCash;
    
    public Duration (int visit, float priceCards, float priceCash){
        this.visit = new SimpleIntegerProperty(visit);
        this.duration = new SimpleStringProperty();
        this.priceCards = new SimpleFloatProperty(priceCards);
        this.priceCash = new SimpleFloatProperty(priceCash);
    }
    
    public void setVisit(int visit){
        this.visit.set(visit);
    }
    
    public int getVisit(){
        return this.visit.get();
    }
    
    public void setDuration(String duration){
        this.duration.set(duration);
    }
    
    public String getDuration(){
        return this.duration.get();
    }
    
    
    public void setPriceCards(float priceCards){
        this.priceCards.set(priceCards);
    }
    
    public float getPriceCards(){
        return this.priceCards.get();
    }
    
    
    public void setPriceCash(float priceCash){
        this.priceCash.set(priceCash);
    }
    
    public float getPriceCash(){
        return this.priceCash.get();
    }
}

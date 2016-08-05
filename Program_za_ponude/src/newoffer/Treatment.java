/*
 * Class-object for taking, holding and setting values that are showed in tables (Upper Jaw, Lower Jaw and/or Other) at
 * 'NewOffer.fxml'. Provides information about ordinal number, treatment ID, treatment name, quantity, price per treatment, 
 * discount for treatment and visit at which treatment is going to get held.
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


public class Treatment {
    private final IntegerProperty no;
    private int treatmentID;
    private final StringProperty treatment;
    private final IntegerProperty quantity;
    private final FloatProperty price;
    private final IntegerProperty discount;
    private final FloatProperty total;
    private final IntegerProperty visit;
    
    public Treatment(int treatmentID, String treatment, int quantity, float price, int discount, float total,
            int visit){
        this.no = new SimpleIntegerProperty();
        this.treatmentID = treatmentID;
        this.treatment = new SimpleStringProperty(treatment);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.price = new SimpleFloatProperty(price);
        this.discount = new SimpleIntegerProperty(discount);
        this.total = new SimpleFloatProperty(total);
        this.visit = new SimpleIntegerProperty(visit);
    }
    
    public void setNo(int no){
        this.no.set(no);
    }
    public int getNo(){
        return this.no.get();
    }
    
    public void setTreatmentID(int treatmentID){
        this.treatmentID = treatmentID;
    }
    public int getTreatmentID(){
        return this.treatmentID;
    }
    
    public void setTreatment(String treatment){
        this.treatment.set(treatment);
    }
    public String getTreatment(){
        return this.treatment.get();
    }
    
    public void setQuantity(int quantity){
        this.quantity.set(quantity);
    }
    public int getQuantity(){
        return this.quantity.get();
    }
    
    public void setPrice(float price){
        this.price.set(price);
    }
    public float getPrice(){
        return this.price.get();
    }
    
    public void setDiscount(int discount){
        this.discount.set(discount);
    }
    public int getDiscount(){
        return this.discount.get();
    }
    
    public void setTotal(float total){
        this.total.set(total);
    }
    public float getTotal(){
        return this.total.get();
    }
    
    public void setVisit(int visit){
        this.visit.set(visit);
    }
    public int getVisit(){
        return this.visit.get();
    }
}

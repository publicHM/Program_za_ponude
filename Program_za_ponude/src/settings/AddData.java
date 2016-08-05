/*
 * Class-object for taking, holding and setting values that are showed in table at 'ChangeData.fxml'.
 * Provides information about treatment id, and in two languages information about treatment name, discount and price
 * for that language.
 *
 * @author Petar Deveric
 */
package settings;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class AddData {
    
    private final IntegerProperty id;
    private final StringProperty languageOne;
    private final FloatProperty priceOne;
    private final IntegerProperty discountOne;
    private final StringProperty languageTwo;
    private final FloatProperty priceTwo;
    private final IntegerProperty discountTwo;
    
    public AddData(int id, String languageOne, float priceOne, int discountOne, String languageTwo, float priceTwo,
            int discountTwo){
        this.id = new SimpleIntegerProperty(id);
        this.languageOne = new SimpleStringProperty(languageOne);
        this.priceOne = new SimpleFloatProperty(priceOne);
        this.discountOne = new SimpleIntegerProperty(discountOne);
        this.languageTwo = new SimpleStringProperty(languageTwo);
        this.priceTwo = new SimpleFloatProperty(priceTwo);
        this.discountTwo = new SimpleIntegerProperty(discountTwo);
    }
    
    public void setID(int id){
        this.id.set(id);
    }
    public int getID(){
        return this.id.get();
    }
    
    public void setLanguageOne(String languageOne){
        this.languageOne.set(languageOne);
    }
    public String getLanguageOne(){
        return this.languageOne.get();
    }
    
    public void setPriceOne(Float priceOne){
        this.priceOne.set(priceOne);
    }
    public Float getPriceOne(){
        return this.priceOne.get();
    }
    
    public void setDiscountOne(int discountOne){
        this.discountOne.set(discountOne);
    }
    public int getDiscountOne(){
        return this.discountOne.get();
    }
    
     public void setLanguageTwo(String languageTwo){
        this.languageTwo.set(languageTwo);
    }
    public String getLanguageTwo(){
        return this.languageTwo.get();
    }
    
    public void setPriceTwo(Float priceTwo){
        this.priceTwo.set(priceTwo);
    }
    public Float getPriceTwo(){
        return this.priceTwo.get();
    }
    
    public void setDiscountTwo(int discountTwo){
        this.discountTwo.set(discountTwo);
    }
    public int getDiscountTwo(){
        return this.discountTwo.get();
    }
}

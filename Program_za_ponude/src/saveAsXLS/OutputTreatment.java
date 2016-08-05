/*
 * Class-object for taking, holding and setting values that are passed to output excel file.
 * Provides information about ordinal number, treatment name, quantity, price per treatment, total price, discount for
 * treatment, total price and visit at which treatment is going to get held.
 * 
 * @author Petar Deveric
 */

package saveAsXLS;


public class OutputTreatment {
    private int no;
    private String treatment;
    private int quantity;
    private float price;
    private int discount;
    private float total;
    private int visit;
    
    public OutputTreatment(int no, String treatment, int quantity, float price, int discount, float total, int visit){
        this.no = no;
        this.treatment = treatment;
        this.quantity = quantity;
        this.price = price;
        this.discount = discount;
        this.total = total;
        this.visit = visit;
    }
    
    public void setNo(int no){
        this.no = no;
    }
    public int getNo(){
        return this.no;
    }

    public void setTreatment(String treatment){
        this.treatment = treatment;
    }
    public String getTreatment(){
        return this.treatment;
    }
    
    public void setQuantity(int quantity){
        this.quantity = quantity;
    }
    public int getQuantity(){
        return this.quantity;
    }
    
    public void setPrice(float price){
        this.price = price;
    }
    public float getPrice(){
        return this.price;
    }
    
    public void setDiscount(int discount){
        this.discount = discount;
    }
    public int getDiscount(){
        return this.discount;
    }
    
    public void setTotal(float total){
        this.total = total;
    }
    public Float getTotal(){
        return this.total;
    }
    
    public void setVisit(int visit){
        this.visit = visit;
    }
    public int getVisit(){
        return this.visit;
    }
}

/*
 * Class-object for taking, holding and setting values that are taken from database and showed in table at 'DataBase.fxml'.
 * Provides information about offer ID, customer ID, customer name and date of day on which offer was made
 *
 * @author Petar Deveric
 */
package databasse;


public class OfferBasicInfo {
    
    private int offerID;
    private int customerID;
    private String name;
    private String date;
    
    public OfferBasicInfo (int offerID, int customerID, String name, String date){
        this.offerID = offerID;
        this.customerID = customerID;
        this.name = name;
        this.date = date;
    }
        
    public void setOfferID(int offerID){
        this.offerID = offerID;
    }
    public int getOfferID(){
        return this.offerID;
    }
    
    public void setCustomerID(int customerID){
        this.customerID = customerID;
    }
    public int getCustomerID(){
        return this.customerID;
    }
    
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }

    public void setDate(String date){
        this.date = date;
    }
    public String getDate(){
        return this.date;
    }
}

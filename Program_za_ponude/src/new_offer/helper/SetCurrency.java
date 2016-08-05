/**
 * Helper class for setting up currency values that can be retrieved with getters
 * 
 * @author Petar
 */
package new_offer.helper;


public class SetCurrency {
    
    private String currencyNameInDB;
    private String discountNameInDB;
    private String currency1Field;
    private String currency2Field;
    
    //  Setting up currency values in program
    public void setCurrency(String clientCurrency){

        switch(clientCurrency){
            case "EUR Općenito": 
                currencyNameInDB = "PriceEngEUR";
                discountNameInDB = "DiscountEngEUR";
                currency1Field = "EUR";
                currency2Field = "EUR";
                break;
            case "GBP Engleska": 
                currencyNameInDB = "PriceEngGBP";
                discountNameInDB = "DiscountEngGBP";
                currency1Field = "GBP";
                currency2Field = "GBP";
                break;
            case "HRK Hrvatska": 
                currencyNameInDB = "PriceCroHRK";
                discountNameInDB = "DiscountCroHRK";
                currency1Field = "Kn";
                currency2Field = "Kn";
                break;
            case "EUR Njemačka": 
                currencyNameInDB = "PriceGerEUR";
                discountNameInDB = "DiscountGerEUR";
                currency1Field = "EUR";
                currency2Field = "EUR";
                break;
            case "EUR Italija":
                currencyNameInDB = "PriceItaEUR";
                discountNameInDB = "DiscountItaEUR";
                currency1Field = "EUR";
                currency2Field = "EUR";
                break;
        }
    }
    
    public String getCurrencyNameInDB(){
        return currencyNameInDB;
    }    
    
    public String getDiscountNameInDB(){
        return discountNameInDB;
    }
    
    public String getCurrency1Field(){
        return currency1Field;
    }
    
    public String getCurrency2Field(){
        return currency2Field;
    }
}

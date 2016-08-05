/**
 * Helper class that sets up client currency and language name in database. They can be retrieved back to
 * caller class with getters
 * 
 * @author Petar
 */
package new_offer.helper;


public class SetLanguage {
    
    private String clientCurrency;
    private String languageNameInDB;
    
    public void setLanguage(String clientLanguage){
        switch(clientLanguage){
            case "Engleski Općenito":
                clientCurrency = "EUR Općenito";
                languageNameInDB = "EnglishEUR";
                break;
            case "Engleski UK":
                clientCurrency = "GBP Engleska";
                languageNameInDB = "EnglishGBP";
                break;
            case "Hrvatski":
                clientCurrency = "HRK Hrvatska";
                languageNameInDB = "Croatian";
                break;
            case "Njemački":
                clientCurrency = "EUR Njemačka";
                languageNameInDB = "German";
                break;
            case "Talijanski":
                clientCurrency = "EUR Italija";
                languageNameInDB = "Italian";
                break;
        }
    }
    
    public String getClientCurrency(){
        return clientCurrency;
    }
    
    public String getLanguageNameInDB(){
        return languageNameInDB;
    }
}

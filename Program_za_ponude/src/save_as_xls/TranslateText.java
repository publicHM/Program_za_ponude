/**
 * Helper class with method that returns translated text based on passed customer language
 * 
 * @author Petar
 */
package save_as_xls;


public class TranslateText {
    
    private String offerTranslated;
    private String firstOptionTranslated;
    private String tretmentTranslated;
    private String quantityTranslated;
    private String priceTranslated;
    private String discountTranslated;
    private String totalTranslated;
    private String upperJawTranslated;
    private String lowerJawTranslated;
    private String otherTranslated;
    private String durationTranslated;
    private String cardsTranslated;
    private String cashTranslated;
    private String validTranslated;
    private String mayChangeTranslated;
    private String immidiatePaymentTranslated;
    private String jawImagePath;
    
    public TranslateText(String language){
        setText(language);
    }
    
    //  Sets variables to appropriate value for passed language
    private void setText(String language){
        switch(language){
            case "Engleski Općenito":
            case "Engleski UK":
                offerTranslated = "Estimate";
                firstOptionTranslated = "First option";
                tretmentTranslated = "Treatment";
                quantityTranslated = "Quantity";
                priceTranslated = "Price";
                discountTranslated = "Discount";
                totalTranslated = "Total";
                upperJawTranslated = "Upper Jaw";
                lowerJawTranslated = "Lower Jaw";
                otherTranslated = "Other";
                durationTranslated = "Duration of treatments";
                cardsTranslated = "Credit cards/Debit cards";
                cashTranslated = "Cash";
                validTranslated = "The estimate is valid for 6 months.";
                mayChangeTranslated = "The total amount of offer may change in case of complications or "
                        + "unforseen works during the procedure.";
                jawImagePath = "\\documents\\JawImageEnglish.jpeg";
                break;
            case "Hrvatski":
                offerTranslated = "Ponuda";
                firstOptionTranslated = "Prva opcija";
                tretmentTranslated = "Zahvat";
                quantityTranslated = "Količina";
                priceTranslated = "Cijena";
                discountTranslated = "Popust";
                totalTranslated = "Ukupno";
                upperJawTranslated = "Gornja čeljust";
                lowerJawTranslated = "Donja čeljust";
                otherTranslated = "Drugo";
                cashTranslated = "Gotovina";
                validTranslated = "Ponuda je važeća 3 mjeseca.";
                mayChangeTranslated = "Ukupan iznos ponude može se promijeniti u slučaju komplikacija ili "
                        + "nepredviđenih zahvata tijekom terapije. ";
                immidiatePaymentTranslated = "ZAHVATI SE PODMIRUJU ODMAH NAKON ŠTO SU ODRAĐENI.";
                jawImagePath = "\\documents\\JawImageCroatian.jpeg";
                break;
            case "Njemački":
                offerTranslated = "Angebot";
                firstOptionTranslated = "Erste Option";
                tretmentTranslated = "Eingriff";
                quantityTranslated = "Anzahl";
                priceTranslated = "Einzelpreis";
                discountTranslated = "Ermäβigung";
                totalTranslated = "Gesamtpreis";
                upperJawTranslated = "Oberkiefer";
                lowerJawTranslated = "Unterkiefer";
                otherTranslated = "Weiter";
                durationTranslated = "Die für die Engriffe geplannte Zeit";
                cardsTranslated = "Banküberweisung/Kreditkarte";
                cashTranslated = "Bargeld";
                validTranslated = "Das ist ein Rahmenangebot und ist 3 Monate gültig.";
                mayChangeTranslated = "Der im Angebot aufgeführte Gesamtbetrag kann infolge Komplikationen oder "
                        + "unvorhergesehener Zufälle bei Eingriffen geändert werden.";
                jawImagePath = "\\documents\\JawImageGerman.jpeg";
                break;
            case "Talijanski":
                offerTranslated = "Preventivo";
                firstOptionTranslated = "Prima soluzione";
                tretmentTranslated = "Intervento";
                quantityTranslated = "Quantita'";
                priceTranslated = "Prezzo";
                discountTranslated = "Sconto";
                totalTranslated = "Totale";
                upperJawTranslated = "Arcata superiore";
                lowerJawTranslated = "Arcata inferiore";
                otherTranslated = "Altro";
                durationTranslated = "Tempo indicativo previsto per gli interventi";
                cardsTranslated = "Carta di credito";
                cashTranslated = "Contanti";
                validTranslated = "Il preventivo vale 3 MESI.";
                mayChangeTranslated = "Il totale del preventivo puo' cambiare nel caso delle complicazioni o dei "
                        + "lavori imprevisti durante gli interventi.";
                jawImagePath = "\\documents\\JawImageItalian.jpeg";
                break;
        }
    }
    
    public String getOfferTranslated() {
        return this.offerTranslated;
    }
    
    public String getFirstOptionTranslated() {
        return this.firstOptionTranslated;
    }
    
    public String getTretmentTranslated() {
        return this.tretmentTranslated;
    }
    
    public String getQuantityTranslated() {
        return this.quantityTranslated;
    }
    
    public String getPriceTranslated() {
        return this.priceTranslated;
    }
    
    public String getDiscountTranslated() {
        return this.discountTranslated;
    }
    
    public String getTotalTranslated() {
        return this.totalTranslated;
    }
    
    public String getUpperJawTranslated() {
        return this.upperJawTranslated;
    }
    
    public String getLowerJawTranslated() {
        return this.lowerJawTranslated;
    }
    
    public String getOtherTranslated() {
        return this.otherTranslated;
    }
    
    public String getDurationTranslated() {
        return this.durationTranslated;
    }
    
    public String getCardsTranslated() {
        return this.cardsTranslated;
    }
    
    public String getCashTranslated() {
        return this.cashTranslated;
    }
    
    public String getValidTranslated() {
        return this.validTranslated;
    }
    
    public String getMayChangeTranslated() {
        return this.mayChangeTranslated;
    }
    
    public String getImmidiatePaymentTranslated() {
        return this.immidiatePaymentTranslated;
    }
    
    public String getJawImagePath() {
        return this.jawImagePath;
    }
    
}

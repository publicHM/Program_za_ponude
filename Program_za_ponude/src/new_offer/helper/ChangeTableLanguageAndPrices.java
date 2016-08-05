/**
 * Helper class for changing language and prices in a table to new language and prices in another currency
 * 
 * @author Petar
 */
package new_offer.helper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.ObservableList;
import new_offer.Treatment;


public class ChangeTableLanguageAndPrices {
    
    public static void changeLanguageAndPricesForATable(ObservableList<Treatment> observableList, 
            PreparedStatement psChangeCurrency){
        
        try{
            for(Treatment treatment : observableList){
                
                int treatmentID = treatment.getTreatmentID();
                int quantity = treatment.getQuantity();
                int discount = treatment.getDiscount();
                psChangeCurrency.setInt(1, treatmentID);
                try(ResultSet rs = psChangeCurrency.executeQuery();){
                    rs.next();

                    String newTreatmentName = rs.getString(1);
                    float newPrice = rs.getFloat(2);
                    float newTotal = quantity * newPrice * (100 - discount) / 100;
                    treatment.setTreatment(newTreatmentName);
                    treatment.setPrice(newPrice);
                    treatment.setTotal(newTotal);
                }
            }
        } catch (SQLException e){
            System.err.println("SQLException in class ChangeTableLanguageAndPrices, method "
                    + "changeLanguageAndCurrencyForATable()");
            e.printStackTrace(System.out);
        }
    }
}

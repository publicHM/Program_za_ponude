/**
 * Helper class that gets information (price, discount etc.) from database for chosen treatment name
 * and returns new treatment with all information. Refreshes duration table and total amount that should
 * be payed.
 * 
 * @author Petar
 */
package new_offer.helper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import new_offer.NewOfferController;
import new_offer.Treatment;


public class GetTreatment {
    
    public static Treatment getTreatment(NewOfferController controller, String selectedItem,
            PreparedStatement psGetTreatment, String exchangeRate){
        int treatmentID = 0;
        String treatmentName = null;
        Float treatmentPrice = 0.0f;
        int treatmentDiscount = 0;
        try{psGetTreatment.setString(1, selectedItem);
            try(ResultSet rsGetTreatment = psGetTreatment.executeQuery();){
                while( rsGetTreatment.next() ){
                    treatmentID = rsGetTreatment.getInt(1);
                    treatmentName = rsGetTreatment.getString(2);
                    treatmentPrice = rsGetTreatment.getFloat(3);
                    treatmentDiscount = rsGetTreatment.getInt(4);
                }
            }
        }
        catch(SQLException e){
            System.err.println("SQLException in class GetTreatment, method getTreatment()");
            e.printStackTrace(System.out);
        }
        
        if ( !exchangeRate.equals("") || !exchangeRate.equals("1.0"))   treatmentPrice /= Float.parseFloat(exchangeRate);
        //  Calculate total amount that should be paid for given treatment
        float total = treatmentPrice * (100 - treatmentDiscount) / 100;
        
        //  Sums all amounts and put it in field for total amount
        controller.setTotalFields();
        
        //  Sums amounts per visit and put in in the duration table
        controller.setDurationTable();
        
        return new Treatment(treatmentID, treatmentName, 1, treatmentPrice, treatmentDiscount, total, 1);
    }
}
